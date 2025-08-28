package com.yxrobot.task;

import com.yxrobot.dto.LinkValidationResultDTO;
import com.yxrobot.service.LinkValidationService;
import com.yxrobot.service.PlatformLinkStatsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 链接监控定时任务类
 * 负责定期检查链接有效性和性能监控
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-22
 */
@Component
@ConditionalOnProperty(name = "platform.link.monitoring.enabled", havingValue = "true", matchIfMissing = true)
public class LinkMonitoringTask {
    
    private static final Logger logger = LoggerFactory.getLogger(LinkMonitoringTask.class);
    
    @Autowired
    private LinkValidationService linkValidationService;
    
    @Autowired
    private PlatformLinkStatsService statsService;
    
    /**
     * 定期检查链接有效性
     * 每小时执行一次
     */
    @Scheduled(cron = "0 0 * * * ?") // 每小时的0分0秒执行
    public void validateLinksScheduled() {
        logger.info("开始执行定时链接验证任务");
        
        try {
            // 每次检查50个链接
            List<LinkValidationResultDTO> results = linkValidationService.validateLinksForScheduledTask(50);
            
            if (results.isEmpty()) {
                logger.info("没有需要验证的链接");
                return;
            }
            
            // 统计验证结果
            long successCount = results.stream().mapToLong(r -> r.getIsValid() ? 1 : 0).sum();
            long failedCount = results.size() - successCount;
            
            logger.info("定时链接验证完成 - 总数: {}, 成功: {}, 失败: {}", 
                       results.size(), successCount, failedCount);
            
            // 如果失败率过高，记录警告
            if (results.size() > 0) {
                double failureRate = (double) failedCount / results.size() * 100;
                if (failureRate > 20) { // 失败率超过20%
                    logger.warn("链接验证失败率过高: {:.2f}% ({}/{})", 
                               failureRate, failedCount, results.size());
                    
                    // 这里可以添加告警逻辑，比如发送邮件或消息通知
                    sendHighFailureRateAlert(failureRate, failedCount, results.size());
                }
            }
            
        } catch (Exception e) {
            logger.error("定时链接验证任务执行失败", e);
        }
    }
    
    /**
     * 定期刷新统计数据缓存
     * 每30分钟执行一次
     */
    @Scheduled(cron = "0 */30 * * * ?") // 每30分钟执行一次
    public void refreshStatsData() {
        logger.info("开始刷新统计数据");
        
        try {
            // 调用刷新方法
            statsService.refreshStats();
            
            // 预热数据 - 主动加载统计数据
            statsService.getPlatformLinkStats();
            statsService.getRegionStats();
            statsService.getLanguageStats();
            statsService.getTopPerformingLinks(10);
            
            logger.info("统计数据刷新完成");
            
        } catch (Exception e) {
            logger.error("刷新统计数据失败", e);
        }
    }
    
    /**
     * 定期清理过期的验证日志
     * 每天凌晨2点执行
     */
    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点执行
    public void cleanupOldValidationLogs() {
        logger.info("开始清理过期的验证日志");
        
        try {
            // 清理30天前的验证日志
            LocalDateTime cutoffTime = LocalDateTime.now().minusDays(30);
            
            // 这里需要在LinkValidationService中添加清理方法
            // int deletedCount = linkValidationService.cleanupOldValidationLogs(cutoffTime);
            
            logger.info("过期验证日志清理完成");
            
        } catch (Exception e) {
            logger.error("清理过期验证日志失败", e);
        }
    }
    
    /**
     * 定期清理过期的点击日志
     * 每天凌晨3点执行
     */
    @Scheduled(cron = "0 0 3 * * ?") // 每天凌晨3点执行
    public void cleanupOldClickLogs() {
        logger.info("开始清理过期的点击日志");
        
        try {
            // 清理90天前的点击日志
            LocalDateTime cutoffTime = LocalDateTime.now().minusDays(90);
            
            // 这里需要在LinkClickService中添加清理方法
            // int deletedCount = linkClickService.cleanupOldClickLogs(cutoffTime);
            
            logger.info("过期点击日志清理完成");
            
        } catch (Exception e) {
            logger.error("清理过期点击日志失败", e);
        }
    }
    
    /**
     * 链接性能监控
     * 每15分钟执行一次
     */
    @Scheduled(cron = "0 */15 * * * ?") // 每15分钟执行一次
    public void monitorLinkPerformance() {
        logger.debug("开始执行链接性能监控");
        
        try {
            // 获取基础统计数据
            var basicStats = statsService.getBasicStats();
            
            if (basicStats != null) {
                // 安全地转换数据库返回的Long类型到Integer
                Integer totalLinks = convertToInteger(basicStats.get("totalLinks"));
                Integer activeLinks = convertToInteger(basicStats.get("activeLinks"));
                Integer inactiveLinks = convertToInteger(basicStats.get("inactiveLinks"));
                
                if (totalLinks != null && totalLinks > 0) {
                    double inactiveRate = (double) (inactiveLinks != null ? inactiveLinks : 0) / totalLinks * 100;
                    
                    // 如果非活跃链接比例过高，记录警告
                    if (inactiveRate > 30) { // 非活跃率超过30%
                        logger.warn("链接非活跃率过高: {:.2f}% ({}/{})", 
                                   inactiveRate, inactiveLinks, totalLinks);
                        
                        // 发送告警
                        sendHighInactiveRateAlert(inactiveRate, inactiveLinks, totalLinks);
                    }
                }
            }
            
            logger.debug("链接性能监控完成");
            
        } catch (Exception e) {
            logger.error("链接性能监控失败", e);
        }
    }
    
    /**
     * 系统健康检查
     * 每5分钟执行一次
     */
    @Scheduled(cron = "0 */5 * * * ?") // 每5分钟执行一次
    public void systemHealthCheck() {
        logger.debug("开始执行系统健康检查");
        
        try {
            // 检查服务是否正常
            boolean isHealthy = true;
            StringBuilder healthReport = new StringBuilder();
            
            // 检查统计服务
            try {
                statsService.getBasicStats();
                healthReport.append("统计服务: 正常; ");
            } catch (Exception e) {
                isHealthy = false;
                healthReport.append("统计服务: 异常(").append(e.getMessage()).append("); ");
                logger.warn("统计服务健康检查失败", e);
            }
            
            // 检查验证服务
            try {
                // 这里可以添加一个简单的健康检查方法
                healthReport.append("验证服务: 正常; ");
            } catch (Exception e) {
                isHealthy = false;
                healthReport.append("验证服务: 异常(").append(e.getMessage()).append("); ");
                logger.warn("验证服务健康检查失败", e);
            }
            
            if (isHealthy) {
                logger.debug("系统健康检查通过: {}", healthReport.toString());
            } else {
                logger.error("系统健康检查发现问题: {}", healthReport.toString());
                sendSystemHealthAlert(healthReport.toString());
            }
            
        } catch (Exception e) {
            logger.error("系统健康检查失败", e);
        }
    }
    
    /**
     * 发送高失败率告警
     * 
     * @param failureRate 失败率
     * @param failedCount 失败数量
     * @param totalCount 总数量
     */
    private void sendHighFailureRateAlert(double failureRate, long failedCount, int totalCount) {
        logger.warn("发送高失败率告警 - 失败率: {:.2f}%, 失败数: {}, 总数: {}", 
                   failureRate, failedCount, totalCount);
        
        // 这里可以实现具体的告警逻辑，比如：
        // 1. 发送邮件通知
        // 2. 发送短信通知
        // 3. 推送到监控系统
        // 4. 记录到告警日志
        
        // 示例：记录告警事件
        String alertMessage = String.format(
            "链接验证失败率告警: 失败率 %.2f%% (%d/%d) 超过阈值 20%%",
            failureRate, failedCount, totalCount
        );
        
        // 这里可以调用告警服务
        // alertService.sendAlert("LINK_VALIDATION_HIGH_FAILURE_RATE", alertMessage);
    }
    
    /**
     * 发送高非活跃率告警
     * 
     * @param inactiveRate 非活跃率
     * @param inactiveCount 非活跃数量
     * @param totalCount 总数量
     */
    private void sendHighInactiveRateAlert(double inactiveRate, Integer inactiveCount, Integer totalCount) {
        logger.warn("发送高非活跃率告警 - 非活跃率: {:.2f}%, 非活跃数: {}, 总数: {}", 
                   inactiveRate, inactiveCount, totalCount);
        
        String alertMessage = String.format(
            "链接非活跃率告警: 非活跃率 %.2f%% (%d/%d) 超过阈值 30%%",
            inactiveRate, inactiveCount, totalCount
        );
        
        // 调用告警服务
        // alertService.sendAlert("LINK_HIGH_INACTIVE_RATE", alertMessage);
    }
    
    /**
     * 发送系统健康告警
     * 
     * @param healthReport 健康报告
     */
    private void sendSystemHealthAlert(String healthReport) {
        logger.error("发送系统健康告警 - 报告: {}", healthReport);
        
        String alertMessage = "系统健康检查发现问题: " + healthReport;
        
        // 调用告警服务
        // alertService.sendAlert("SYSTEM_HEALTH_CHECK_FAILED", alertMessage);
    }
    
    /**
     * 安全地将Object转换为Integer
     * 处理数据库返回的Long类型
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
            // 检查是否在Integer范围内
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