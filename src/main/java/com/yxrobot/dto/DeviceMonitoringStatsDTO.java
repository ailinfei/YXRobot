package com.yxrobot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 设备监控统计DTO类
 * 适配前端DeviceMonitoring.vue页面的统计卡片数据需求
 * 确保字段名称与前端TypeScript接口完全匹配（camelCase）
 * 
 * 对应前端接口：
 * interface MonitoringStats {
 *   online: number;
 *   offline: number;
 *   error: number;
 *   avgPerformance: number;
 *   onlineTrend: number;
 *   offlineTrend: number;
 *   errorTrend: number;
 *   performanceTrend: number;
 * }
 * 
 * @author YXRobot Development Team
 * @since 2024
 */
public class DeviceMonitoringStatsDTO {
    
    private String id;                  // 统计ID（转换为字符串）
    
    // 前端统计卡片需要的核心字段
    private Integer online;             // 在线设备数量（前端字段名：online）
    private Integer offline;            // 离线设备数量（前端字段名：offline）
    private Integer error;              // 故障设备数量（前端字段名：error）
    private BigDecimal avgPerformance;  // 平均性能百分比（前端字段名：avgPerformance）
    
    // 趋势数据（前端用于显示变化趋势）
    private Integer onlineTrend;        // 在线设备趋势变化（前端字段名：onlineTrend）
    private Integer offlineTrend;       // 离线设备趋势变化（前端字段名：offlineTrend）
    private Integer errorTrend;         // 故障设备趋势变化（前端字段名：errorTrend）
    private Integer performanceTrend;   // 性能趋势变化（前端字段名：performanceTrend）
    
    // 额外统计信息
    private Integer maintenance;        // 维护中设备数量
    private Integer total;              // 设备总数
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate statsDate;       // 统计日期
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;    // 创建时间
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;    // 更新时间
    
    // 格式化的时间字符串（用于前端显示）
    private String statsDateFormatted;
    private String createdAtFormatted;
    private String updatedAtFormatted;
    
    // 构造函数
    public DeviceMonitoringStatsDTO() {}
    
    public DeviceMonitoringStatsDTO(Integer online, Integer offline, Integer error, 
                                  BigDecimal avgPerformance, Integer onlineTrend, 
                                  Integer offlineTrend, Integer errorTrend, Integer performanceTrend) {
        this.online = online;
        this.offline = offline;
        this.error = error;
        this.avgPerformance = avgPerformance;
        this.onlineTrend = onlineTrend;
        this.offlineTrend = offlineTrend;
        this.errorTrend = errorTrend;
        this.performanceTrend = performanceTrend;
        this.total = online + offline + error;
    }
    
    // Getter和Setter方法
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public Integer getOnline() {
        return online;
    }
    
    public void setOnline(Integer online) {
        this.online = online;
    }
    
    public Integer getOffline() {
        return offline;
    }
    
    public void setOffline(Integer offline) {
        this.offline = offline;
    }
    
    public Integer getError() {
        return error;
    }
    
    public void setError(Integer error) {
        this.error = error;
    }
    
    public BigDecimal getAvgPerformance() {
        return avgPerformance;
    }
    
    public void setAvgPerformance(BigDecimal avgPerformance) {
        this.avgPerformance = avgPerformance;
    }
    
    public Integer getOnlineTrend() {
        return onlineTrend;
    }
    
    public void setOnlineTrend(Integer onlineTrend) {
        this.onlineTrend = onlineTrend;
    }
    
    public Integer getOfflineTrend() {
        return offlineTrend;
    }
    
    public void setOfflineTrend(Integer offlineTrend) {
        this.offlineTrend = offlineTrend;
    }
    
    public Integer getErrorTrend() {
        return errorTrend;
    }
    
    public void setErrorTrend(Integer errorTrend) {
        this.errorTrend = errorTrend;
    }
    
    public Integer getPerformanceTrend() {
        return performanceTrend;
    }
    
    public void setPerformanceTrend(Integer performanceTrend) {
        this.performanceTrend = performanceTrend;
    }
    
    public Integer getMaintenance() {
        return maintenance;
    }
    
    public void setMaintenance(Integer maintenance) {
        this.maintenance = maintenance;
    }
    
    public Integer getTotal() {
        return total;
    }
    
    public void setTotal(Integer total) {
        this.total = total;
    }
    
    public LocalDate getStatsDate() {
        return statsDate;
    }
    
    public void setStatsDate(LocalDate statsDate) {
        this.statsDate = statsDate;
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
    
    public String getStatsDateFormatted() {
        return statsDateFormatted;
    }
    
    public void setStatsDateFormatted(String statsDateFormatted) {
        this.statsDateFormatted = statsDateFormatted;
    }
    
    public String getCreatedAtFormatted() {
        return createdAtFormatted;
    }
    
    public void setCreatedAtFormatted(String createdAtFormatted) {
        this.createdAtFormatted = createdAtFormatted;
    }
    
    public String getUpdatedAtFormatted() {
        return updatedAtFormatted;
    }
    
    public void setUpdatedAtFormatted(String updatedAtFormatted) {
        this.updatedAtFormatted = updatedAtFormatted;
    }
    
    @Override
    public String toString() {
        return "DeviceMonitoringStatsDTO{" +
                "id='" + id + '\'' +
                ", online=" + online +
                ", offline=" + offline +
                ", error=" + error +
                ", avgPerformance=" + avgPerformance +
                ", onlineTrend=" + onlineTrend +
                ", offlineTrend=" + offlineTrend +
                ", errorTrend=" + errorTrend +
                ", performanceTrend=" + performanceTrend +
                ", total=" + total +
                '}';
    }
}