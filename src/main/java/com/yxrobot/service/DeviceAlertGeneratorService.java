package com.yxrobot.service;

import com.yxrobot.dto.DeviceAlertDTO;
import com.yxrobot.entity.AlertLevel;
import com.yxrobot.entity.DevicePerformanceMetrics;
import com.yxrobot.entity.DeviceNetworkStatus;
import com.yxrobot.entity.DeviceMonitoringData;
import com.yxrobot.entity.DeviceStatus;
import com.yxrobot.mapper.DevicePerformanceMetricsMapper;
import com.yxrobot.mapper.DeviceNetworkStatusMapper;
import com.yxrobot.mapper.DeviceMonitoringDataMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 设备告警自动生成服务
 * 根据设备状态和性能指标自动生成告警
 * 
 * @author YXRobot Development Team
 * @since 2024
 */
@Service
public class DeviceAlertGeneratorService {
    
    private static final Logger logger = LoggerFactory.getLogger(DeviceAlertGeneratorService.class);
    
    // 性能阈值配置
    private static final BigDecimal HIGH_CPU_THRESHOLD = BigDecimal.valueOf(85);
    private static final BigDecimal HIGH_MEMORY_THRESHOLD = BigDecimal.valueOf(90);
    private static final BigDecimal HIGH_DISK_THRESHOLD = BigDecimal.valueOf(85);
    private static final BigDecimal HIGH_TEMPERATURE_THRESHOLD = BigDecimal.valueOf(75);
    private static final BigDecimal LOW_BATTERY_THRESHOLD = BigDecimal.valueOf(20);
    
    // 网络阈值配置
    private static final int LOW_SIGNAL_THRESHOLD = 30;
    private static final int HIGH_LATENCY_THRESHOLD = 200;
    private static final BigDecimal LOW_SPEED_THRESHOLD = BigDecimal.valueOf(1.0);
    
    @Autowired
    private DeviceAlertService deviceAlertService;
    
    @Autowired
    private DevicePerformanceMetricsMapper devicePerformanceMetricsMapper;
    
    @Autowired
    private DeviceNetworkStatusMapper deviceNetworkStatusMapper;
    
    @Autowired
    private DeviceMonitoringDataMapper deviceMonitoringDataMapper;
    
    /**
     * 每5分钟检查一次性能告警
     */
    @Scheduled(cron = "0 */5 * * * ?") // 每5分钟执行
    public void checkPerformanceAlerts() {
        logger.info("开始执行性能告警检查任务");
        
        try {
            int alertCount = 0;
            
            // 检查高CPU使用率
            alertCount += checkHighCpuUsage();
            
            // 检查高内存使用率
            alertCount += checkHighMemoryUsage();
            
            // 检查高磁盘使用率
            alertCount += checkHighDiskUsage();
            
            // 检查高温告警
            alertCount += checkHighTemperature();
            
            // 检查低电池告警
            alertCount += checkLowBattery();
            
            logger.info("性能告警检查任务完成，生成了{}条告警", alertCount);
            
        } catch (Exception e) {
            logger.error("性能告警检查任务执行失败", e);
        }
    }
    
    /**
     * 每10分钟检查一次网络告警
     */
    @Scheduled(cron = "0 */10 * * * ?") // 每10分钟执行
    public void checkNetworkAlerts() {
        logger.info("开始执行网络告警检查任务");
        
        try {
            int alertCount = 0;
            
            // 检查低信号强度
            alertCount += checkLowSignalStrength();
            
            // 检查高网络延迟
            alertCount += checkHighNetworkLatency();
            
            // 检查低网络速度
            alertCount += checkLowNetworkSpeed();
            
            logger.info("网络告警检查任务完成，生成了{}条告警", alertCount);
            
        } catch (Exception e) {
            logger.error("网络告警检查任务执行失败", e);
        }
    }
    
    /**
     * 每30分钟检查一次设备状态告警
     */
    @Scheduled(cron = "0 */30 * * * ?") // 每30分钟执行
    public void checkDeviceStatusAlerts() {
        logger.info("开始执行设备状态告警检查任务");
        
        try {
            int alertCount = 0;
            
            // 检查离线设备
            alertCount += checkOfflineDevices();
            
            // 检查故障设备
            alertCount += checkErrorDevices();
            
            logger.info("设备状态告警检查任务完成，生成了{}条告警", alertCount);
            
        } catch (Exception e) {
            logger.error("设备状态告警检查任务执行失败", e);
        }
    }
    
    /**
     * 每天凌晨2点自动解决过期的INFO级别告警
     */
    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点执行
    public void autoResolveExpiredInfoAlerts() {
        logger.info("开始执行过期INFO告警自动解决任务");
        
        try {
            // 自动解决24小时前的INFO级别告警
            int resolvedCount = deviceAlertService.autoResolveExpiredAlerts(24, AlertLevel.INFO);
            logger.info("过期INFO告警自动解决任务完成，解决了{}条告警", resolvedCount);
            
        } catch (Exception e) {
            logger.error("过期INFO告警自动解决任务执行失败", e);
        }
    }
    
    /**
     * 检查高CPU使用率
     */
    private int checkHighCpuUsage() {
        try {
            List<DevicePerformanceMetrics> highCpuDevices = 
                devicePerformanceMetricsMapper.selectHighCpuUsage(HIGH_CPU_THRESHOLD, 50);
            
            int alertCount = 0;
            for (DevicePerformanceMetrics metrics : highCpuDevices) {
                String message = String.format("设备CPU使用率过高: %.1f%%", metrics.getCpuUsage());
                
                DeviceAlertDTO alert = deviceAlertService.createAlert(
                    metrics.getDeviceId(), 
                    AlertLevel.WARNING, 
                    "HIGH_CPU_USAGE", 
                    message
                );
                
                if (alert != null) {
                    alertCount++;
                }
            }
            
            return alertCount;
            
        } catch (Exception e) {
            logger.error("检查高CPU使用率失败", e);
            return 0;
        }
    }
    
    /**
     * 检查高内存使用率
     */
    private int checkHighMemoryUsage() {
        try {
            List<DevicePerformanceMetrics> highMemoryDevices = 
                devicePerformanceMetricsMapper.selectHighMemoryUsage(HIGH_MEMORY_THRESHOLD, 50);
            
            int alertCount = 0;
            for (DevicePerformanceMetrics metrics : highMemoryDevices) {
                String message = String.format("设备内存使用率过高: %.1f%%", metrics.getMemoryUsage());
                
                DeviceAlertDTO alert = deviceAlertService.createAlert(
                    metrics.getDeviceId(), 
                    AlertLevel.WARNING, 
                    "HIGH_MEMORY_USAGE", 
                    message
                );
                
                if (alert != null) {
                    alertCount++;
                }
            }
            
            return alertCount;
            
        } catch (Exception e) {
            logger.error("检查高内存使用率失败", e);
            return 0;
        }
    }
    
    /**
     * 检查高磁盘使用率
     */
    private int checkHighDiskUsage() {
        try {
            // 这里需要添加查询高磁盘使用率的方法
            // 暂时返回0，实际实现时需要添加相应的Mapper方法
            return 0;
            
        } catch (Exception e) {
            logger.error("检查高磁盘使用率失败", e);
            return 0;
        }
    }
    
    /**
     * 检查高温告警
     */
    private int checkHighTemperature() {
        try {
            List<DevicePerformanceMetrics> highTempDevices = 
                devicePerformanceMetricsMapper.selectHighTemperature(HIGH_TEMPERATURE_THRESHOLD, 50);
            
            int alertCount = 0;
            for (DevicePerformanceMetrics metrics : highTempDevices) {
                String message = String.format("设备温度过高: %.1f°C", metrics.getTemperature());
                
                AlertLevel level = metrics.getTemperature().compareTo(BigDecimal.valueOf(85)) > 0 ? 
                                  AlertLevel.ERROR : AlertLevel.WARNING;
                
                DeviceAlertDTO alert = deviceAlertService.createAlert(
                    metrics.getDeviceId(), 
                    level, 
                    "HIGH_TEMPERATURE", 
                    message
                );
                
                if (alert != null) {
                    alertCount++;
                }
            }
            
            return alertCount;
            
        } catch (Exception e) {
            logger.error("检查高温告警失败", e);
            return 0;
        }
    }
    
    /**
     * 检查低电池告警
     */
    private int checkLowBattery() {
        try {
            List<DevicePerformanceMetrics> lowBatteryDevices = 
                devicePerformanceMetricsMapper.selectLowBattery(LOW_BATTERY_THRESHOLD, 50);
            
            int alertCount = 0;
            for (DevicePerformanceMetrics metrics : lowBatteryDevices) {
                String message = String.format("设备电池电量过低: %.1f%%", metrics.getBatteryLevel());
                
                AlertLevel level = metrics.getBatteryLevel().compareTo(BigDecimal.valueOf(10)) < 0 ? 
                                  AlertLevel.ERROR : AlertLevel.WARNING;
                
                DeviceAlertDTO alert = deviceAlertService.createAlert(
                    metrics.getDeviceId(), 
                    level, 
                    "LOW_BATTERY", 
                    message
                );
                
                if (alert != null) {
                    alertCount++;
                }
            }
            
            return alertCount;
            
        } catch (Exception e) {
            logger.error("检查低电池告警失败", e);
            return 0;
        }
    }
    
    /**
     * 检查低信号强度
     */
    private int checkLowSignalStrength() {
        try {
            List<DeviceNetworkStatus> lowSignalDevices = 
                deviceNetworkStatusMapper.selectLowSignalDevices(LOW_SIGNAL_THRESHOLD, 50);
            
            int alertCount = 0;
            for (DeviceNetworkStatus networkStatus : lowSignalDevices) {
                String message = String.format("设备信号强度过低: %d%%", networkStatus.getSignalStrength());
                
                DeviceAlertDTO alert = deviceAlertService.createAlert(
                    networkStatus.getDeviceId(), 
                    AlertLevel.WARNING, 
                    "LOW_SIGNAL_STRENGTH", 
                    message
                );
                
                if (alert != null) {
                    alertCount++;
                }
            }
            
            return alertCount;
            
        } catch (Exception e) {
            logger.error("检查低信号强度失败", e);
            return 0;
        }
    }
    
    /**
     * 检查高网络延迟
     */
    private int checkHighNetworkLatency() {
        try {
            List<DeviceNetworkStatus> highLatencyDevices = 
                deviceNetworkStatusMapper.selectHighLatencyDevices(HIGH_LATENCY_THRESHOLD, 50);
            
            int alertCount = 0;
            for (DeviceNetworkStatus networkStatus : highLatencyDevices) {
                String message = String.format("设备网络延迟过高: %dms", networkStatus.getPingLatency());
                
                DeviceAlertDTO alert = deviceAlertService.createAlert(
                    networkStatus.getDeviceId(), 
                    AlertLevel.WARNING, 
                    "HIGH_NETWORK_LATENCY", 
                    message
                );
                
                if (alert != null) {
                    alertCount++;
                }
            }
            
            return alertCount;
            
        } catch (Exception e) {
            logger.error("检查高网络延迟失败", e);
            return 0;
        }
    }
    
    /**
     * 检查低网络速度
     */
    private int checkLowNetworkSpeed() {
        try {
            List<DeviceNetworkStatus> slowSpeedDevices = 
                deviceNetworkStatusMapper.selectSlowSpeedDevices(LOW_SPEED_THRESHOLD, 50);
            
            int alertCount = 0;
            for (DeviceNetworkStatus networkStatus : slowSpeedDevices) {
                String message = String.format("设备网络速度过慢: %.1fMbps", networkStatus.getDownloadSpeed());
                
                DeviceAlertDTO alert = deviceAlertService.createAlert(
                    networkStatus.getDeviceId(), 
                    AlertLevel.INFO, 
                    "LOW_NETWORK_SPEED", 
                    message
                );
                
                if (alert != null) {
                    alertCount++;
                }
            }
            
            return alertCount;
            
        } catch (Exception e) {
            logger.error("检查低网络速度失败", e);
            return 0;
        }
    }
    
    /**
     * 检查离线设备
     */
    private int checkOfflineDevices() {
        try {
            List<DeviceMonitoringData> offlineDevices = 
                deviceMonitoringDataMapper.selectByStatus(DeviceStatus.OFFLINE);
            
            int alertCount = 0;
            for (DeviceMonitoringData device : offlineDevices) {
                // 只对长时间离线的设备生成告警
                if (device.getLastOnlineAt() != null) {
                    LocalDateTime threshold = LocalDateTime.now().minusHours(2);
                    if (device.getLastOnlineAt().isBefore(threshold)) {
                        String message = String.format("设备长时间离线: %s", device.getSerialNumber());
                        
                        DeviceAlertDTO alert = deviceAlertService.createAlert(
                            device.getDeviceId(), 
                            AlertLevel.WARNING, 
                            "DEVICE_OFFLINE", 
                            message
                        );
                        
                        if (alert != null) {
                            alertCount++;
                        }
                    }
                }
            }
            
            return alertCount;
            
        } catch (Exception e) {
            logger.error("检查离线设备失败", e);
            return 0;
        }
    }
    
    /**
     * 检查故障设备
     */
    private int checkErrorDevices() {
        try {
            List<DeviceMonitoringData> errorDevices = 
                deviceMonitoringDataMapper.selectByStatus(DeviceStatus.ERROR);
            
            int alertCount = 0;
            for (DeviceMonitoringData device : errorDevices) {
                String message = String.format("设备出现故障: %s", device.getSerialNumber());
                
                DeviceAlertDTO alert = deviceAlertService.createAlert(
                    device.getDeviceId(), 
                    AlertLevel.ERROR, 
                    "DEVICE_ERROR", 
                    message
                );
                
                if (alert != null) {
                    alertCount++;
                }
            }
            
            return alertCount;
            
        } catch (Exception e) {
            logger.error("检查故障设备失败", e);
            return 0;
        }
    }
    
    /**
     * 手动触发告警检查
     * 用于测试或紧急情况
     * 
     * @return 生成的告警数量
     */
    public int triggerManualAlertCheck() {
        logger.info("手动触发告警检查");
        
        int totalAlerts = 0;
        
        try {
            // 执行所有检查
            checkPerformanceAlerts();
            checkNetworkAlerts();
            checkDeviceStatusAlerts();
            
            logger.info("手动告警检查完成，共生成{}条告警", totalAlerts);
            
        } catch (Exception e) {
            logger.error("手动告警检查失败", e);
        }
        
        return totalAlerts;
    }
}