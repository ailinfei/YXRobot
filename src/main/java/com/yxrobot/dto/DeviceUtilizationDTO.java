package com.yxrobot.dto;

import java.math.BigDecimal;

/**
 * 设备利用率数据传输对象
 * 适配前端 DeviceUtilizationData TypeScript 接口
 * 
 * @author Kiro
 * @date 2025-01-28
 */
public class DeviceUtilizationDTO {
    
    /**
     * 设备ID
     * 对应前端字段: deviceId
     */
    private String deviceId;
    
    /**
     * 设备型号
     * 对应前端字段: deviceModel
     */
    private String deviceModel;
    
    /**
     * 利用率
     * 对应前端字段: utilizationRate
     */
    private BigDecimal utilizationRate;
    
    /**
     * 租赁天数
     * 对应前端字段: totalRentalDays
     */
    private Integer totalRentalDays;
    
    /**
     * 可用天数
     * 对应前端字段: totalAvailableDays
     */
    private Integer totalAvailableDays;
    
    /**
     * 当前状态
     * 对应前端字段: currentStatus
     */
    private String currentStatus;
    
    /**
     * 最后租赁日期
     * 对应前端字段: lastRentalDate
     */
    private String lastRentalDate;
    
    /**
     * 所在地区
     * 对应前端字段: region
     */
    private String region;
    
    /**
     * 性能评分
     * 对应前端字段: performanceScore
     */
    private Integer performanceScore;
    
    /**
     * 信号强度
     * 对应前端字段: signalStrength
     */
    private Integer signalStrength;
    
    /**
     * 维护状态
     * 对应前端字段: maintenanceStatus
     */
    private String maintenanceStatus;
    
    // 构造函数
    public DeviceUtilizationDTO() {
        this.utilizationRate = BigDecimal.ZERO;
        this.totalRentalDays = 0;
        this.totalAvailableDays = 0;
        this.performanceScore = 100;
        this.signalStrength = 100;
    }
    
    // Getter 和 Setter 方法
    public String getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    
    public String getDeviceModel() {
        return deviceModel;
    }
    
    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }
    
    public BigDecimal getUtilizationRate() {
        return utilizationRate;
    }
    
    public void setUtilizationRate(BigDecimal utilizationRate) {
        this.utilizationRate = utilizationRate;
    }
    
    public Integer getTotalRentalDays() {
        return totalRentalDays;
    }
    
    public void setTotalRentalDays(Integer totalRentalDays) {
        this.totalRentalDays = totalRentalDays;
    }
    
    public Integer getTotalAvailableDays() {
        return totalAvailableDays;
    }
    
    public void setTotalAvailableDays(Integer totalAvailableDays) {
        this.totalAvailableDays = totalAvailableDays;
    }
    
    public String getCurrentStatus() {
        return currentStatus;
    }
    
    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }
    
    public String getLastRentalDate() {
        return lastRentalDate;
    }
    
    public void setLastRentalDate(String lastRentalDate) {
        this.lastRentalDate = lastRentalDate;
    }
    
    public String getRegion() {
        return region;
    }
    
    public void setRegion(String region) {
        this.region = region;
    }
    
    public Integer getPerformanceScore() {
        return performanceScore;
    }
    
    public void setPerformanceScore(Integer performanceScore) {
        this.performanceScore = performanceScore;
    }
    
    public Integer getSignalStrength() {
        return signalStrength;
    }
    
    public void setSignalStrength(Integer signalStrength) {
        this.signalStrength = signalStrength;
    }
    
    public String getMaintenanceStatus() {
        return maintenanceStatus;
    }
    
    public void setMaintenanceStatus(String maintenanceStatus) {
        this.maintenanceStatus = maintenanceStatus;
    }
    
    @Override
    public String toString() {
        return "DeviceUtilizationDTO{" +
                "deviceId='" + deviceId + '\'' +
                ", deviceModel='" + deviceModel + '\'' +
                ", utilizationRate=" + utilizationRate +
                ", currentStatus='" + currentStatus + '\'' +
                ", region='" + region + '\'' +
                ", performanceScore=" + performanceScore +
                '}';
    }
}