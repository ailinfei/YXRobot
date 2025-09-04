package com.yxrobot.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 设备维护记录实体类
 * 对应managed_device_maintenance_records表
 */
public class ManagedDeviceMaintenanceRecord {
    
    private Long id;
    private Long deviceId;              // 设备ID（引用managed_devices表）
    private MaintenanceType type;       // 维护类型（枚举）
    private String description;         // 维护描述
    private String technician;          // 技术员
    private LocalDateTime startTime;    // 开始时间
    private LocalDateTime endTime;      // 结束时间
    private MaintenanceStatus status;   // 维护状态（枚举）
    private BigDecimal cost;            // 维护费用
    private List<String> parts;         // 更换部件（JSON数组）
    private String notes;               // 维护备注
    private LocalDateTime createdAt;    // 创建时间
    private LocalDateTime updatedAt;    // 更新时间
    
    // 构造函数
    public ManagedDeviceMaintenanceRecord() {}
    
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
    
    public MaintenanceType getType() {
        return type;
    }
    
    public void setType(MaintenanceType type) {
        this.type = type;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getTechnician() {
        return technician;
    }
    
    public void setTechnician(String technician) {
        this.technician = technician;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    
    public MaintenanceStatus getStatus() {
        return status;
    }
    
    public void setStatus(MaintenanceStatus status) {
        this.status = status;
    }
    
    public BigDecimal getCost() {
        return cost;
    }
    
    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }
    
    public List<String> getParts() {
        return parts;
    }
    
    public void setParts(List<String> parts) {
        this.parts = parts;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
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