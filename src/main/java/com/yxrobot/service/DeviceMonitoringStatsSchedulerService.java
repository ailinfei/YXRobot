package com.yxrobot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * 设备监控统计定时任务服务
 * 负责定期更新和维护监控统计数据
 * 
 * @author YXRobot Development Team
 * @since 2024
 */
@Service
public class DeviceMonitoringStatsSchedulerService {
    
    private static final Logger logger = LoggerFactory.getLogger(DeviceMonitoringStatsSchedulerService.class);
    
    @Autowired
    private DeviceMonitoringStatsService deviceMonitoringStatsService;
    
    /**
     * 每小时更新一次统计数据
     * 确保统计数据的实时性
     */
    @Scheduled(cron = "0 0 * * * ?") // 每小时的0分0秒执行
    public void updateHourlyStats() {
        logger.info("开始执行每小时统计数据更新任务");
        
        try {
            deviceMonitoringStatsService.saveCurrentStats();
            logger.info("每小时统计数据更新任务执行成功");
        } catch (Exception e) {
            logger.error("每小时统计数据更新任务执行失败", e);
        }
    }
    
    /**
     * 每天凌晨2点清理过期数据
     * 保留最近30天的统计数据
     */
    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点执行
    public void cleanupExpiredStats() {
        logger.info("开始执行过期统计数据清理任务");
        
        try {
            int deletedCount = deviceMonitoringStatsService.cleanupExpiredStats(30);
            logger.info("过期统计数据清理任务执行成功，清理了{}条记录", deletedCount);
        } catch (Exception e) {
            logger.error("过期统计数据清理任务执行失败", e);
        }
    }
    
    /**
     * 每天午夜生成当日统计快照
     * 确保每日统计数据的完整性
     */
    @Scheduled(cron = "0 0 0 * * ?") // 每天午夜执行
    public void generateDailySnapshot() {
        logger.info("开始执行每日统计快照生成任务");
        
        try {
            deviceMonitoringStatsService.saveCurrentStats();
            logger.info("每日统计快照生成任务执行成功");
        } catch (Exception e) {
            logger.error("每日统计快照生成任务执行失败", e);
        }
    }
}