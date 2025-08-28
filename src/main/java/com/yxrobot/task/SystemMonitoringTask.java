package com.yxrobot.task;

import com.yxrobot.service.PlatformLinkStatsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 系统监控定时任务类
 * 负责系统健康检查、性能监控和数据统计
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-25
 */
@Component
@ConditionalOnProperty(name = "system.monitoring.enabled", havingValue = "true", matchIfMissing = true)
public class SystemMonitoringTask {
    
    private static final Logger logger = LoggerFactory.getLogger(SystemMonitoringTask.class);
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private PlatformLinkStatsService statsService;
    
    /**
     * 系统健康检查
     * 每5分钟执行一次
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void systemHealthCheck() {
        logger.info("开始执行系统健康检查");
        
        try {
            StringBuilder healthReport = new StringBuilder();
            healthReport.append("系统健康检查报告 - ").append(LocalDateTime.now()).append("\n");
            
            // 1. 数据库连接检查
            boolean dbHealthy = checkDatabaseHealth();
            healthReport.append("数据库连接: ").append(dbHealthy ? "正常" : "异常").append("\n");
            
            // 2. 平台链接统计检查
            boolean statsHealthy = checkStatsServiceHealth();
            healthReport.append("统计服务: ").append(statsHealthy ? "正常" : "异常").append("\n");
            
            // 3. 内存使用情况
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            double memoryUsagePercent = (double) usedMemory / totalMemory * 100;
            
            healthReport.append(String.format("内存使用: %.2f%% (%d MB / %d MB)\n", 
                               memoryUsagePercent, usedMemory / 1024 / 1024, totalMemory / 1024 / 1024));
            
            // 4. 记录健康状态
            if (dbHealthy && statsHealthy && memoryUsagePercent < 90) {
                logger.info("系统健康检查通过:\n{}", healthReport.toString());
            } else {
                logger.warn("系统健康检查发现问题:\n{}", healthReport.toString());
            }
            
        } catch (Exception e) {
            logger.error("系统健康检查执行失败", e);
        }
    }
    
    /**
     * 数据统计更新任务
     * 每30分钟执行一次
     */
    @Scheduled(cron = "0 */30 * * * ?")
    public void updateStatistics() {
        logger.info("开始执行数据统计更新任务");
        
        try {
            // 更新平台链接统计
            Map<String, Object> stats = statsService.getBasicStats();
            logger.info("平台链接统计更新完成 - 总链接: {}, 活跃链接: {}", 
                       stats.get("totalLinks"), stats.get("activeLinks"));
            
            // 检查异常数据
            Integer totalLinks = convertToInteger(stats.get("totalLinks"));
            Integer activeLinks = convertToInteger(stats.get("activeLinks"));
            Integer inactiveLinks = convertToInteger(stats.get("inactiveLinks"));
            
            if (totalLinks != null && totalLinks > 0) {
                double inactiveRate = (double) (inactiveLinks != null ? inactiveLinks : 0) / totalLinks * 100;
                
                if (inactiveRate > 30) {
                    logger.warn("链接非活跃率过高: {:.2f}% ({}/{})", 
                               inactiveRate, inactiveLinks, totalLinks);
                }
            }
            
        } catch (Exception e) {
            logger.error("数据统计更新任务执行失败", e);
        }
    }
    
    /**
     * 数据库清理任务
     * 每天凌晨2点执行
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void databaseCleanup() {
        logger.info("开始执行数据库清理任务");
        
        try {
            // 清理30天前的验证日志
            cleanupOldValidationLogs();
            
            // 清理90天前的点击日志
            cleanupOldClickLogs();
            
            logger.info("数据库清理任务执行完成");
            
        } catch (Exception e) {
            logger.error("数据库清理任务执行失败", e);
        }
    }
    
    /**
     * 检查数据库健康状态
     */
    private boolean checkDatabaseHealth() {
        try (Connection connection = dataSource.getConnection()) {
            // 执行简单查询测试连接
            try (PreparedStatement stmt = connection.prepareStatement("SELECT 1")) {
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next() && rs.getInt(1) == 1;
                }
            }
        } catch (Exception e) {
            logger.error("数据库健康检查失败", e);
            return false;
        }
    }
    
    /**
     * 检查统计服务健康状态
     */
    private boolean checkStatsServiceHealth() {
        try {
            Map<String, Object> stats = statsService.getBasicStats();
            return stats != null && !stats.isEmpty();
        } catch (Exception e) {
            logger.error("统计服务健康检查失败", e);
            return false;
        }
    }
    
    /**
     * 清理旧的验证日志
     */
    private void cleanupOldValidationLogs() {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "DELETE FROM link_validation_logs WHERE created_at < DATE_SUB(NOW(), INTERVAL 30 DAY)";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                int deletedRows = stmt.executeUpdate();
                logger.info("清理了 {} 条30天前的验证日志", deletedRows);
            }
        } catch (Exception e) {
            logger.error("清理验证日志失败", e);
        }
    }
    
    /**
     * 清理旧的点击日志
     */
    private void cleanupOldClickLogs() {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "DELETE FROM link_click_logs WHERE created_at < DATE_SUB(NOW(), INTERVAL 90 DAY)";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                int deletedRows = stmt.executeUpdate();
                logger.info("清理了 {} 条90天前的点击日志", deletedRows);
            }
        } catch (Exception e) {
            logger.error("清理点击日志失败", e);
        }
    }
    
    /**
     * 安全地将Object转换为Integer
     */
    private Integer convertToInteger(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Long) {
            Long longValue = (Long) value;
            if (longValue > Integer.MAX_VALUE) {
                logger.warn("Long值 {} 超出Integer范围，使用Integer.MAX_VALUE", longValue);
                return Integer.MAX_VALUE;
            }
            return longValue.intValue();
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.valueOf(value.toString());
        } catch (NumberFormatException e) {
            logger.warn("无法将值 {} 转换为Integer，返回0", value);
            return 0;
        }
    }
}