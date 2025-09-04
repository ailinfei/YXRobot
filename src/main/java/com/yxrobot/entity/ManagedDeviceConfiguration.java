package com.yxrobot.entity;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 设备配置实体类
 * 对应managed_device_configurations表
 */
public class ManagedDeviceConfiguration {
    
    private Long id;
    private Long deviceId;              // 设备ID（引用managed_devices表）
    private String language;            // 语言设置
    private String timezone;            // 时区设置
    private Boolean autoUpdate;         // 自动更新
    private Boolean debugMode;          // 调试模式
    private Map<String, Object> customSettings; // 自定义设置（JSON对象）
    private LocalDateTime createdAt;    // 创建时间
    private LocalDateTime updatedAt;    // 更新时间
    
    // 构造函数
    public ManagedDeviceConfiguration() {}
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }
    
    public String getLanguage() {
        return language;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }
    
    public String getTimezone() {
        return timezone;
    }
    
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
    
    public Boolean getAutoUpdate() {
        return autoUpdate;
    }
    
    public void setAutoUpdate(Boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }
    
    public Boolean getDebugMode() {
        return debugMode;
    }
    
    public void setDebugMode(Boolean debugMode) {
        this.debugMode = debugMode;
    }
    
    public Map<String, Object> getCustomSettings() {
        return customSettings;
    }
    
    public void setCustomSettings(Map<String, Object> customSettings) {
        this.customSettings = customSettings;
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
}