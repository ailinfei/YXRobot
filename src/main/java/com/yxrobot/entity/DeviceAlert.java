package com.yxrobot.entity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 设备告警实体类
 * 对应device_alerts表
 * 
 * 字段映射规范：
 * - 数据库字段使用snake_case命名（如：alert_level, alert_message）
 * - Java属性使用camelCase命名（如：alertLevel, alertMessage）
 * - MyBatis映射文件中确保column和property正确对应
 * 
 * @author YXRobot Development Team
 * @since 2024
 */
public class DeviceAlert {
    
    private Long id;
    
    @NotNull(message = "设备ID不能为空")
    private Long deviceId;              // 设备ID（引用managed_devices表）
    
    @NotNull(message = "告警级别不能为空")
    private AlertLevel alertLevel;      // 告警级别
    
    @NotBlank(message = "告警类型不能为空")
    @Size(max = 50, message = "告警类型长度不能超过50字符")
    private String alertType;           // 告警类型
    
    @NotBlank(message = "告警消息不能为空")
    private String alertMessage;        // 告警消息
    
    @NotNull(message = "告警时间不能为空")
    private LocalDateTime alertTimestamp; // 告警时间
    
    private Boolean isResolved;         // 是否已解决
    private LocalDateTime resolvedAt;   // 解决时间
    private String resolvedBy;          // 解决人
    
    private LocalDateTime createdAt;    // 创建时间
    private LocalDateTime updatedAt;    // 更新时间
    
    // 构造函数
    public DeviceAlert() {}
    
    public DeviceAlert(Long deviceId, AlertLevel alertLevel, String alertType, String alertMessage) {
        this.deviceId = deviceId;
        this.alertLevel = alertLevel;
        this.alertType = alertType;
        this.alertMessage = alertMessage;
        this.alertTimestamp = LocalDateTime.now();
        this.isResolved = false;
    }
    
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
    
    public AlertLevel getAlertLevel() {
        return alertLevel;
    }
    
    public void setAlertLevel(AlertLevel alertLevel) {
        this.alertLevel = alertLevel;
    }
    
    public String getAlertType() {
        return alertType;
    }
    
    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }
    
    public String getAlertMessage() {
        return alertMessage;
    }
    
    public void setAlertMessage(String alertMessage) {
        this.alertMessage = alertMessage;
    }
    
    public LocalDateTime getAlertTimestamp() {
        return alertTimestamp;
    }
    
    public void setAlertTimestamp(LocalDateTime alertTimestamp) {
        this.alertTimestamp = alertTimestamp;
    }
    
    public Boolean getIsResolved() {
        return isResolved;
    }
    
    public void setIsResolved(Boolean isResolved) {
        this.isResolved = isResolved;
    }
    
    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }
    
    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
    
    public String getResolvedBy() {
        return resolvedBy;
    }
    
    public void setResolvedBy(String resolvedBy) {
        this.resolvedBy = resolvedBy;
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
    
    // 业务方法
    public void resolve(String resolvedBy) {
        this.isResolved = true;
        this.resolvedAt = LocalDateTime.now();
        this.resolvedBy = resolvedBy;
    }
    
    @Override
    public String toString() {
        return "DeviceAlert{" +
                "id=" + id +
                ", deviceId=" + deviceId +
                ", alertLevel=" + alertLevel +
                ", alertType='" + alertType + '\'' +
                ", alertMessage='" + alertMessage + '\'' +
                ", alertTimestamp=" + alertTimestamp +
                ", isResolved=" + isResolved +
                '}';
    }
}