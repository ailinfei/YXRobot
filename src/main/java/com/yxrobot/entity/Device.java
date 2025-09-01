package com.yxrobot.entity;

import java.time.LocalDateTime;

/**
 * 设备实体类
 * 对应devices表
 */
public class Device {
    
    private Long id;
    private String deviceId;            // 设备编号
    private String serialNumber;        // 设备序列号
    private String deviceModel;         // 设备型号
    private String deviceName;          // 设备名称
    private String deviceCategory;      // 设备类别
    private DeviceStatus deviceStatus;  // 设备状态
    private String firmwareVersion;     // 固件版本
    private Integer healthScore;        // 健康评分（0-100）
    private String region;              // 所在地区
    private Boolean isActive;           // 是否启用
    private LocalDateTime createdAt;    // 创建时间
    private LocalDateTime updatedAt;    // 更新时间
    private Boolean isDeleted;          // 是否删除
    
    // 构造函数
    public Device() {}
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    
    public String getSerialNumber() {
        return serialNumber;
    }
    
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public String getDeviceModel() {
        return deviceModel;
    }
    
    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }
    
    public String getDeviceName() {
        return deviceName;
    }
    
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
    
    public String getDeviceCategory() {
        return deviceCategory;
    }
    
    public void setDeviceCategory(String deviceCategory) {
        this.deviceCategory = deviceCategory;
    }
    
    public DeviceStatus getDeviceStatus() {
        return deviceStatus;
    }
    
    public void setDeviceStatus(DeviceStatus deviceStatus) {
        this.deviceStatus = deviceStatus;
    }
    
    public String getFirmwareVersion() {
        return firmwareVersion;
    }
    
    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }
    
    public Integer getHealthScore() {
        return healthScore;
    }
    
    public void setHealthScore(Integer healthScore) {
        this.healthScore = healthScore;
    }
    
    public String getRegion() {
        return region;
    }
    
    public void setRegion(String region) {
        this.region = region;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Boolean getIsDeleted() {
        return isDeleted;
    }
    
    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}