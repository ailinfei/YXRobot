package com.yxrobot.entity;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 设备性能指标实体类
 * 对应device_performance_metrics表
 * 
 * 字段映射规范：
 * - 数据库字段使用snake_case命名（如：cpu_usage, memory_usage）
 * - Java属性使用camelCase命名（如：cpuUsage, memoryUsage）
 * - MyBatis映射文件中确保column和property正确对应
 * 
 * @author YXRobot Development Team
 * @since 2024
 */
public class DevicePerformanceMetrics {
    
    private Long id;
    
    @NotNull(message = "设备ID不能为空")
    private Long deviceId;              // 设备ID（引用managed_devices表）
    
    @DecimalMin(value = "0.00", message = "CPU使用率不能为负数")
    @DecimalMax(value = "100.00", message = "CPU使用率不能超过100%")
    private BigDecimal cpuUsage;        // CPU使用率百分比
    
    @DecimalMin(value = "0.00", message = "内存使用率不能为负数")
    @DecimalMax(value = "100.00", message = "内存使用率不能超过100%")
    private BigDecimal memoryUsage;     // 内存使用率百分比
    
    @DecimalMin(value = "0.00", message = "磁盘使用率不能为负数")
    @DecimalMax(value = "100.00", message = "磁盘使用率不能超过100%")
    private BigDecimal diskUsage;       // 磁盘使用率百分比
    
    private BigDecimal temperature;     // 设备温度
    
    @DecimalMin(value = "0.00", message = "电池电量不能为负数")
    @DecimalMax(value = "100.00", message = "电池电量不能超过100%")
    private BigDecimal batteryLevel;    // 电池电量百分比
    
    @Min(value = 0, message = "网络延迟不能为负数")
    private Integer networkLatency;     // 网络延迟（毫秒）
    
    @DecimalMin(value = "0.00", message = "网络带宽不能为负数")
    private BigDecimal networkBandwidth; // 网络带宽（Mbps）
    
    @NotNull(message = "指标采集时间不能为空")
    private LocalDateTime metricTimestamp; // 指标采集时间
    
    private LocalDateTime createdAt;    // 创建时间
    
    // 构造函数
    public DevicePerformanceMetrics() {}
    
    public DevicePerformanceMetrics(Long deviceId, BigDecimal cpuUsage, BigDecimal memoryUsage, 
                                  BigDecimal diskUsage, LocalDateTime metricTimestamp) {
        this.deviceId = deviceId;
        this.cpuUsage = cpuUsage;
        this.memoryUsage = memoryUsage;
        this.diskUsage = diskUsage;
        this.metricTimestamp = metricTimestamp;
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
    
    public BigDecimal getCpuUsage() {
        return cpuUsage;
    }
    
    public void setCpuUsage(BigDecimal cpuUsage) {
        this.cpuUsage = cpuUsage;
    }
    
    public BigDecimal getMemoryUsage() {
        return memoryUsage;
    }
    
    public void setMemoryUsage(BigDecimal memoryUsage) {
        this.memoryUsage = memoryUsage;
    }
    
    public BigDecimal getDiskUsage() {
        return diskUsage;
    }
    
    public void setDiskUsage(BigDecimal diskUsage) {
        this.diskUsage = diskUsage;
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
    
    public LocalDateTime getMetricTimestamp() {
        return metricTimestamp;
    }
    
    public void setMetricTimestamp(LocalDateTime metricTimestamp) {
        this.metricTimestamp = metricTimestamp;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // 业务方法
    public BigDecimal getOverallPerformance() {
        if (cpuUsage == null || memoryUsage == null) {
            return BigDecimal.ZERO;
        }
        // 计算综合性能分数（100 - 平均使用率）
        BigDecimal avgUsage = cpuUsage.add(memoryUsage).divide(BigDecimal.valueOf(2));
        return BigDecimal.valueOf(100).subtract(avgUsage);
    }
    
    public boolean isHighCpuUsage() {
        return cpuUsage != null && cpuUsage.compareTo(BigDecimal.valueOf(80)) > 0;
    }
    
    public boolean isHighMemoryUsage() {
        return memoryUsage != null && memoryUsage.compareTo(BigDecimal.valueOf(80)) > 0;
    }
    
    @Override
    public String toString() {
        return "DevicePerformanceMetrics{" +
                "id=" + id +
                ", deviceId=" + deviceId +
                ", cpuUsage=" + cpuUsage +
                ", memoryUsage=" + memoryUsage +
                ", diskUsage=" + diskUsage +
                ", temperature=" + temperature +
                ", metricTimestamp=" + metricTimestamp +
                '}';
    }
}