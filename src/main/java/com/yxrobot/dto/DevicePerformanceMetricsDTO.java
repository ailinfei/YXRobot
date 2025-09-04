package com.yxrobot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 设备性能指标DTO类
 * 适配前端DeviceMonitoring.vue页面的性能图表数据需求
 * 确保字段名称与前端TypeScript接口完全匹配（camelCase）
 * 
 * @author YXRobot Development Team
 * @since 2024
 */
public class DevicePerformanceMetricsDTO {
    
    private String id;                  // 指标ID（转换为字符串）
    private String deviceId;            // 设备ID（转换为字符串）
    
    // 性能指标（前端图表需要的核心数据）
    private BigDecimal cpu;             // CPU使用率百分比
    private BigDecimal memory;          // 内存使用率百分比
    private BigDecimal disk;            // 磁盘使用率百分比
    private BigDecimal temperature;     // 设备温度
    private BigDecimal batteryLevel;    // 电池电量百分比
    
    // 网络性能指标
    private Integer networkLatency;     // 网络延迟（毫秒）
    private BigDecimal networkBandwidth; // 网络带宽（Mbps）
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;    // 指标采集时间
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;    // 创建时间
    
    // 格式化的时间字符串（用于前端显示）
    private String timestampFormatted;
    private String createdAtFormatted;
    
    // 性能状态描述
    private String cpuStatus;           // CPU状态描述
    private String memoryStatus;        // 内存状态描述
    private String diskStatus;          // 磁盘状态描述
    private String temperatureStatus;   // 温度状态描述
    private String batteryStatus;       // 电池状态描述
    
    // 综合性能分数
    private BigDecimal overallPerformance; // 综合性能分数
    private String performanceLevel;       // 性能等级（优秀、良好、一般、差）
    
    // 构造函数
    public DevicePerformanceMetricsDTO() {}
    
    public DevicePerformanceMetricsDTO(String deviceId, BigDecimal cpu, BigDecimal memory, 
                                     BigDecimal disk, LocalDateTime timestamp) {
        this.deviceId = deviceId;
        this.cpu = cpu;
        this.memory = memory;
        this.disk = disk;
        this.timestamp = timestamp;
    }
    
    // Getter和Setter方法
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    
    public BigDecimal getCpu() {
        return cpu;
    }
    
    public void setCpu(BigDecimal cpu) {
        this.cpu = cpu;
    }
    
    public BigDecimal getMemory() {
        return memory;
    }
    
    public void setMemory(BigDecimal memory) {
        this.memory = memory;
    }
    
    public BigDecimal getDisk() {
        return disk;
    }
    
    public void setDisk(BigDecimal disk) {
        this.disk = disk;
    }
    
    public BigDecimal getTemperature() {
        return temperature;
    }
    
    public void setTemperature(BigDecimal temperature) {
        this.temperature = temperature;
    }
    
    public BigDecimal getBatteryLevel() {
        return batteryLevel;
    }
    
    public void setBatteryLevel(BigDecimal batteryLevel) {
        this.batteryLevel = batteryLevel;
    }
    
    public Integer getNetworkLatency() {
        return networkLatency;
    }
    
    public void setNetworkLatency(Integer networkLatency) {
        this.networkLatency = networkLatency;
    }
    
    public BigDecimal getNetworkBandwidth() {
        return networkBandwidth;
    }
    
    public void setNetworkBandwidth(BigDecimal networkBandwidth) {
        this.networkBandwidth = networkBandwidth;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
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
    
    public String getCreatedAtFormatted() {
        return createdAtFormatted;
    }
    
    public void setCreatedAtFormatted(String createdAtFormatted) {
        this.createdAtFormatted = createdAtFormatted;
    }
    
    public String getCpuStatus() {
        return cpuStatus;
    }
    
    public void setCpuStatus(String cpuStatus) {
        this.cpuStatus = cpuStatus;
    }
    
    public String getMemoryStatus() {
        return memoryStatus;
    }
    
    public void setMemoryStatus(String memoryStatus) {
        this.memoryStatus = memoryStatus;
    }
    
    public String getDiskStatus() {
        return diskStatus;
    }
    
    public void setDiskStatus(String diskStatus) {
        this.diskStatus = diskStatus;
    }
    
    public String getTemperatureStatus() {
        return temperatureStatus;
    }
    
    public void setTemperatureStatus(String temperatureStatus) {
        this.temperatureStatus = temperatureStatus;
    }
    
    public String getBatteryStatus() {
        return batteryStatus;
    }
    
    public void setBatteryStatus(String batteryStatus) {
        this.batteryStatus = batteryStatus;
    }
    
    public BigDecimal getOverallPerformance() {
        return overallPerformance;
    }
    
    public void setOverallPerformance(BigDecimal overallPerformance) {
        this.overallPerformance = overallPerformance;
    }
    
    public String getPerformanceLevel() {
        return performanceLevel;
    }
    
    public void setPerformanceLevel(String performanceLevel) {
        this.performanceLevel = performanceLevel;
    }
    
    @Override
    public String toString() {
        return "DevicePerformanceMetricsDTO{" +
                "id='" + id + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", cpu=" + cpu +
                ", memory=" + memory +
                ", disk=" + disk +
                ", temperature=" + temperature +
                ", timestamp=" + timestamp +
                '}';
    }
}