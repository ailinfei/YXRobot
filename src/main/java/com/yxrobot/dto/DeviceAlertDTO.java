package com.yxrobot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

/**
 * 设备告警DTO类
 * 适配前端DeviceMonitoring.vue页面的告警列表数据需求
 * 确保字段名称与前端TypeScript接口完全匹配（camelCase）
 * 
 * 对应前端接口：
 * interface DeviceAlert {
 *   id: string;
 *   level: 'error' | 'warning' | 'info';
 *   message: string;
 *   timestamp: string;
 *   deviceId: string;
 *   deviceSerialNumber: string;
 * }
 * 
 * @author YXRobot Development Team
 * @since 2024
 */
public class DeviceAlertDTO {
    
    private String id;                  // 告警ID（转换为字符串）
    private String level;               // 告警级别（前端字段名：level）
    private String message;             // 告警消息（前端字段名：message）
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;    // 告警时间（前端字段名：timestamp）
    
    private String deviceId;            // 设备ID（前端字段名：deviceId）
    private String deviceSerialNumber;  // 设备序列号（前端字段名：deviceSerialNumber）
    
    // 额外信息
    private String type;                // 告警类型
    private Boolean isResolved;         // 是否已解决
    private String resolvedBy;          // 解决人
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime resolvedAt;   // 解决时间
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;    // 创建时间
    
    // 格式化的时间字符串（用于前端显示）
    private String timestampFormatted;
    private String resolvedAtFormatted;
    private String createdAtFormatted;
    
    // 相对时间显示（如：刚刚、5分钟前、2小时前）
    private String relativeTime;
    
    // 级别描述和图标
    private String levelDescription;
    private String levelIcon;
    private String levelColor;
    
    // 构造函数
    public DeviceAlertDTO() {}
    
    public DeviceAlertDTO(String id, String level, String message, LocalDateTime timestamp, 
                         String deviceId, String deviceSerialNumber) {
        this.id = id;
        this.level = level;
        this.message = message;
        this.timestamp = timestamp;
        this.deviceId = deviceId;
        this.deviceSerialNumber = deviceSerialNumber;
    }
    
    // Getter和Setter方法
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getLevel() {
        return level;
    }
    
    public void setLevel(String level) {
        this.level = level;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    
    public String getDeviceSerialNumber() {
        return deviceSerialNumber;
    }
    
    public void setDeviceSerialNumber(String deviceSerialNumber) {
        this.deviceSerialNumber = deviceSerialNumber;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public Boolean getIsResolved() {
        return isResolved;
    }
    
    public void setIsResolved(Boolean isResolved) {
        this.isResolved = isResolved;
    }
    
    public String getResolvedBy() {
        return resolvedBy;
    }
    
    public void setResolvedBy(String resolvedBy) {
        this.resolvedBy = resolvedBy;
    }
    
    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }
    
    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getTimestampFormatted() {
        return timestampFormatted;
    }
    
    public void setTimestampFormatted(String timestampFormatted) {
        this.timestampFormatted = timestampFormatted;
    }
    
    public String getResolvedAtFormatted() {
        return resolvedAtFormatted;
    }
    
    public void setResolvedAtFormatted(String resolvedAtFormatted) {
        this.resolvedAtFormatted = resolvedAtFormatted;
    }
    
    public String getCreatedAtFormatted() {
        return createdAtFormatted;
    }
    
    public void setCreatedAtFormatted(String createdAtFormatted) {
        this.createdAtFormatted = createdAtFormatted;
    }
    
    public String getRelativeTime() {
        return relativeTime;
    }
    
    public void setRelativeTime(String relativeTime) {
        this.relativeTime = relativeTime;
    }
    
    public String getLevelDescription() {
        return levelDescription;
    }
    
    public void setLevelDescription(String levelDescription) {
        this.levelDescription = levelDescription;
    }
    
    public String getLevelIcon() {
        return levelIcon;
    }
    
    public void setLevelIcon(String levelIcon) {
        this.levelIcon = levelIcon;
    }
    
    public String getLevelColor() {
        return levelColor;
    }
    
    public void setLevelColor(String levelColor) {
        this.levelColor = levelColor;
    }
    
    @Override
    public String toString() {
        return "DeviceAlertDTO{" +
                "id='" + id + '\'' +
                ", level='" + level + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", deviceId='" + deviceId + '\'' +
                ", deviceSerialNumber='" + deviceSerialNumber + '\'' +
                ", isResolved=" + isResolved +
                '}';
    }
}