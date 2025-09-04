package com.yxrobot.entity;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 设备日志实体类
 * 对应managed_device_logs表
 */
public class ManagedDeviceLog {
    
    private Long id;
    private Long deviceId;              // 设备ID（引用managed_devices表）
    private LocalDateTime timestamp;    // 时间戳
    private LogLevel level;             // 日志级别（枚举）
    private LogCategory category;       // 日志分类（枚举）
    private String message;             // 日志消息
    private Map<String, Object> details; // 详细信息（JSON对象）
    private LocalDateTime createdAt;    // 创建时间
    
    // 构造函数
    public ManagedDeviceLog() {}
    
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
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public LogLevel getLevel() {
        return level;
    }
    
    public void setLevel(LogLevel level) {
        this.level = level;
    }
    
    public LogCategory getCategory() {
        return category;
    }
    
    public void setCategory(LogCategory category) {
        this.category = category;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Map<String, Object> getDetails() {
        return details;
    }
    
    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}