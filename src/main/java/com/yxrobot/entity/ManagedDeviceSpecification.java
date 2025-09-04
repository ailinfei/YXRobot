package com.yxrobot.entity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 设备技术参数实体类
 * 对应managed_device_specifications表
 */
public class ManagedDeviceSpecification {
    
    private Long id;
    private Long deviceId;              // 设备ID（引用managed_devices表）
    private String cpu;                 // CPU规格
    private String memory;              // 内存规格
    private String storage;             // 存储规格
    private String display;             // 显示屏规格
    private String battery;             // 电池规格
    private List<String> connectivity;  // 连接性规格（JSON数组）
    private LocalDateTime createdAt;    // 创建时间
    private LocalDateTime updatedAt;    // 更新时间
    
    // 构造函数
    public ManagedDeviceSpecification() {}
    
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
    
    public String getCpu() {
        return cpu;
    }
    
    public void setCpu(String cpu) {
        this.cpu = cpu;
    }
    
    public String getMemory() {
        return memory;
    }
    
    public void setMemory(String memory) {
        this.memory = memory;
    }
    
    public String getStorage() {
        return storage;
    }
    
    public void setStorage(String storage) {
        this.storage = storage;
    }
    
    public String getDisplay() {
        return display;
    }
    
    public void setDisplay(String display) {
        this.display = display;
    }
    
    public String getBattery() {
        return battery;
    }
    
    public void setBattery(String battery) {
        this.battery = battery;
    }
    
    public List<String> getConnectivity() {
        return connectivity;
    }
    
    public void setConnectivity(List<String> connectivity) {
        this.connectivity = connectivity;
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