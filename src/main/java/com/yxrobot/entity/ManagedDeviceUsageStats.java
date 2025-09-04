package com.yxrobot.entity;

import java.time.LocalDateTime;

/**
 * 设备使用统计实体类
 * 对应managed_device_usage_stats表
 */
public class ManagedDeviceUsageStats {
    
    private Long id;
    private Long deviceId;              // 设备ID（引用managed_devices表）
    private Integer totalRuntime;       // 总运行时间（分钟）
    private Integer usageCount;         // 使用次数
    private LocalDateTime lastUsedAt;   // 最后使用时间
    private Integer averageSessionTime; // 平均使用时长（分钟）
    private LocalDateTime createdAt;    // 创建时间
    private LocalDateTime updatedAt;    // 更新时间
    
    // 构造函数
    public ManagedDeviceUsageStats() {}
    
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
    
    public Integer getTotalRuntime() {
        return totalRuntime;
    }
    
    public void setTotalRuntime(Integer totalRuntime) {
        this.totalRuntime = totalRuntime;
    }
    
    public Integer getUsageCount() {
        return usageCount;
    }
    
    public void setUsageCount(Integer usageCount) {
        this.usageCount = usageCount;
    }
    
    public LocalDateTime getLastUsedAt() {
        return lastUsedAt;
    }
    
    public void setLastUsedAt(LocalDateTime lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }
    
    public Integer getAverageSessionTime() {
        return averageSessionTime;
    }
    
    public void setAverageSessionTime(Integer averageSessionTime) {
        this.averageSessionTime = averageSessionTime;
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