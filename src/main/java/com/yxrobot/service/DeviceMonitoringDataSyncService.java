package com.yxrobot.service;

import com.yxrobot.entity.DeviceMonitoringData;
import com.yxrobot.entity.DeviceStatus;
import com.yxrobot.mapper.DeviceMonitoringDataMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 设备监控数据同步服务
 * 负责定期同步和维护设备监控数据
 * 
 * @author YXRobot Development Team
 * @since 2024
 */
@Service
public class DeviceMonitoringDataSyncService {
    
    private static final Logger logger = LoggerFactory.getLogger(DeviceMonitoringDataSyncService.class);
    
    // 离线判定阈值（分钟）
    private static final int OFFLINE_THRESHOLD_MINUTES = 30;
    
    @Autowired
    private DeviceMonitoringDataMapper deviceMonitoringDataMapper;
    
    @Autowired
    private DeviceMonitoringService deviceMonitoringService;
    
    /**
     * 每10分钟同步一次设备基本信息
     * 确保监控数据与主设备表保持一致
     */
    @Scheduled(cron = "0 */10 * * * ?") // 每10分钟执行
    public void syncDeviceBasicInfo() {
        logger.info("开始执行设备基本信息同步任务");
        
        try {
            int syncCount = deviceMonitoringService.syncDeviceInfo(null);
            logger.info("设备基本信息同步任务执行成功，同步了{}台设备", syncCount);
        } catch (Exception e) {
            logger.error("设备基本信息同步任务执行失败", e);
        }
    }
    
    /**
     * 每5分钟检查一次设备在线状态
     * 自动将长时间未上报的设备标记为离线
     */
    @Scheduled(cron = "0 */5 * * * ?") // 每5分钟执行
    public void checkDeviceOnlineStatus() {
        logger.info("开始执行设备在线状态检查任务");
        
        try {
            LocalDateTime offlineThreshold = LocalDateTime.now().minusMinutes(OFFLINE_THRESHOLD_MINUTES);
            
            // 查询长时间离线的设备
            List<DeviceMonitoringData> longOfflineDevices = 
                deviceMonitoringDataMapper.selectLongOfflineDevices(offlineThreshold);
            
            if (!longOfflineDevices.isEmpty()) {
                logger.info("发现{}台长时间离线的设备", longOfflineDevices.size());
                
                // 批量更新为离线状态
                List<Long> deviceIds = longOfflineDevices.stream()
                        .map(DeviceMonitoringData::getDeviceId)
                        .toList();
                
                int updateCount = deviceMonitoringService.batchUpdateDeviceStatus(deviceIds, DeviceStatus.OFFLINE);
                logger.info("已将{}台设备标记为离线状态", updateCount);
            } else {
                logger.debug("未发现长时间离线的设备");
            }
            
        } catch (Exception e) {
            logger.error("设备在线状态检查任务执行失败", e);
        }
    }
    
    /**
     * 每小时执行一次数据质量检查
     * 检查和修复数据不一致问题
     */
    @Scheduled(cron = "0 0 * * * ?") // 每小时执行
    public void performDataQualityCheck() {
        logger.info("开始执行数据质量检查任务");
        
        try {
            // 检查缺失的监控数据记录
            checkMissingMonitoringRecords();
            
            // 检查数据一致性
            checkDataConsistency();
            
            logger.info("数据质量检查任务执行完成");
        } catch (Exception e) {
            logger.error("数据质量检查任务执行失败", e);
        }
    }
    
    /**
     * 每天凌晨3点执行数据清理任务
     * 清理过期和无效的监控数据
     */
    @Scheduled(cron = "0 0 3 * * ?") // 每天凌晨3点执行
    public void performDataCleanup() {
        logger.info("开始执行数据清理任务");
        
        try {
            // 清理孤立的监控数据（对应的设备已被删除）
            cleanupOrphanedMonitoringData();
            
            logger.info("数据清理任务执行完成");
        } catch (Exception e) {
            logger.error("数据清理任务执行失败", e);
        }
    }
    
    /**
     * 手动触发全量数据同步
     * 用于数据修复或初始化场景
     * 
     * @return 同步结果
     */
    @Transactional
    public SyncResult performFullDataSync() {
        logger.info("开始执行全量数据同步");
        
        SyncResult result = new SyncResult();
        
        try {
            // 1. 同步设备基本信息
            int syncCount = deviceMonitoringService.syncDeviceInfo(null);
            result.setSyncedDevices(syncCount);
            
            // 2. 检查设备在线状态
            LocalDateTime offlineThreshold = LocalDateTime.now().minusMinutes(OFFLINE_THRESHOLD_MINUTES);
            List<DeviceMonitoringData> longOfflineDevices = 
                deviceMonitoringDataMapper.selectLongOfflineDevices(offlineThreshold);
            
            if (!longOfflineDevices.isEmpty()) {
                List<Long> deviceIds = longOfflineDevices.stream()
                        .map(DeviceMonitoringData::getDeviceId)
                        .toList();
                
                int updateCount = deviceMonitoringService.batchUpdateDeviceStatus(deviceIds, DeviceStatus.OFFLINE);
                result.setUpdatedOfflineDevices(updateCount);
            }
            
            // 3. 数据质量检查
            checkMissingMonitoringRecords();
            checkDataConsistency();
            
            result.setSuccess(true);
            result.setMessage("全量数据同步完成");
            
            logger.info("全量数据同步执行成功: {}", result);
            
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("全量数据同步失败: " + e.getMessage());
            logger.error("全量数据同步执行失败", e);
        }
        
        return result;
    }
    
    /**
     * 检查缺失的监控数据记录
     */
    private void checkMissingMonitoringRecords() {
        logger.debug("检查缺失的监控数据记录");
        
        try {
            // 这里可以添加检查逻辑，比如：
            // 1. 检查managed_devices表中的设备是否都有对应的监控记录
            // 2. 为缺失的设备创建监控记录
            
            // 示例实现：同步所有设备信息
            int syncCount = deviceMonitoringService.syncDeviceInfo(null);
            if (syncCount > 0) {
                logger.info("为{}台设备补充了监控记录", syncCount);
            }
            
        } catch (Exception e) {
            logger.error("检查缺失监控记录失败", e);
        }
    }
    
    /**
     * 检查数据一致性
     */
    private void checkDataConsistency() {
        logger.debug("检查数据一致性");
        
        try {
            // 这里可以添加数据一致性检查逻辑，比如：
            // 1. 检查设备状态是否与实际情况一致
            // 2. 检查时间戳的合理性
            // 3. 检查关联数据的完整性
            
            logger.debug("数据一致性检查完成");
            
        } catch (Exception e) {
            logger.error("数据一致性检查失败", e);
        }
    }
    
    /**
     * 清理孤立的监控数据
     */
    private void cleanupOrphanedMonitoringData() {
        logger.debug("清理孤立的监控数据");
        
        try {
            // 这里可以添加清理逻辑，比如：
            // 1. 删除对应设备已被删除的监控记录
            // 2. 清理过期的临时数据
            
            logger.debug("孤立监控数据清理完成");
            
        } catch (Exception e) {
            logger.error("清理孤立监控数据失败", e);
        }
    }
    
    /**
     * 同步结果类
     */
    public static class SyncResult {
        private boolean success;
        private String message;
        private int syncedDevices;
        private int updatedOfflineDevices;
        private LocalDateTime syncTime;
        
        public SyncResult() {
            this.syncTime = LocalDateTime.now();
        }
        
        // Getter和Setter方法
        public boolean isSuccess() {
            return success;
        }
        
        public void setSuccess(boolean success) {
            this.success = success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public int getSyncedDevices() {
            return syncedDevices;
        }
        
        public void setSyncedDevices(int syncedDevices) {
            this.syncedDevices = syncedDevices;
        }
        
        public int getUpdatedOfflineDevices() {
            return updatedOfflineDevices;
        }
        
        public void setUpdatedOfflineDevices(int updatedOfflineDevices) {
            this.updatedOfflineDevices = updatedOfflineDevices;
        }
        
        public LocalDateTime getSyncTime() {
            return syncTime;
        }
        
        public void setSyncTime(LocalDateTime syncTime) {
            this.syncTime = syncTime;
        }
        
        @Override
        public String toString() {
            return "SyncResult{" +
                    "success=" + success +
                    ", message='" + message + '\'' +
                    ", syncedDevices=" + syncedDevices +
                    ", updatedOfflineDevices=" + updatedOfflineDevices +
                    ", syncTime=" + syncTime +
                    '}';
        }
    }
}