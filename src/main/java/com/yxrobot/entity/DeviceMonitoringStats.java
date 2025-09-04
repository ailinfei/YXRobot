package com.yxrobot.entity;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 设备监控统计实体类
 * 对应device_monitoring_stats表
 * 
 * 字段映射规范：
 * - 数据库字段使用snake_case命名（如：online_count, avg_performance）
 * - Java属性使用camelCase命名（如：onlineCount, avgPerformance）
 * - MyBatis映射文件中确保column和property正确对应
 * 
 * @author YXRobot Development Team
 * @since 2024
 */
public class DeviceMonitoringStats {
    
    private Long id;
    
    @Min(value = 0, message = "在线设备数量不能为负数")
    private Integer onlineCount;        // 在线设备数量
    
    @Min(value = 0, message = "离线设备数量不能为负数")
    private Integer offlineCount;       // 离线设备数量
    
    @Min(value = 0, message = "故障设备数量不能为负数")
    private Integer errorCount;         // 故障设备数量
    
    @Min(value = 0, message = "维护中设备数量不能为负数")
    private Integer maintenanceCount;   // 维护中设备数量
    
    @Min(value = 0, message = "设备总数不能为负数")
    private Integer totalCount;         // 设备总数
    
    @Min(value = 0, message = "平均性能不能为负数")
    private BigDecimal avgPerformance;  // 平均性能百分比
    
    private Integer onlineTrend;        // 在线设备趋势变化
    private Integer offlineTrend;       // 离线设备趋势变化
    private Integer errorTrend;         // 故障设备趋势变化
    private Integer performanceTrend;   // 性能趋势变化
    
    @NotNull(message = "统计日期不能为空")
    private LocalDate statsDate;       // 统计日期
    
    private LocalDateTime createdAt;    // 创建时间
    private LocalDateTime updatedAt;    // 更新时间
    
    // 构造函数
    public DeviceMonitoringStats() {}
    
    public DeviceMonitoringStats(Integer onlineCount, Integer offlineCount, Integer errorCount, 
                               Integer maintenanceCount, BigDecimal avgPerformance, LocalDate statsDate) {
        this.onlineCount = onlineCount;
        this.offlineCount = offlineCount;
        this.errorCount = errorCount;
        this.maintenanceCount = maintenanceCount;
        this.totalCount = onlineCount + offlineCount + errorCount + maintenanceCount;
        this.avgPerformance = avgPerformance;
        this.statsDate = statsDate;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Integer getOnlineCount() {
        return onlineCount;
    }
    
    public void setOnlineCount(Integer onlineCount) {
        this.onlineCount = onlineCount;
    }
    
    public Integer getOfflineCount() {
        return offlineCount;
    }
    
    public void setOfflineCount(Integer offlineCount) {
        this.offlineCount = offlineCount;
    }
    
    public Integer getErrorCount() {
        return errorCount;
    }
    
    public void setErrorCount(Integer errorCount) {
        this.errorCount = errorCount;
    }
    
    public Integer getMaintenanceCount() {
        return maintenanceCount;
    }
    
    public void setMaintenanceCount(Integer maintenanceCount) {
        this.maintenanceCount = maintenanceCount;
    }
    
    public Integer getTotalCount() {
        return totalCount;
    }
    
    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
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
    
    @Override
    public String toString() {
        return "DeviceMonitoringStats{" +
                "id=" + id +
                ", onlineCount=" + onlineCount +
                ", offlineCount=" + offlineCount +
                ", errorCount=" + errorCount +
                ", maintenanceCount=" + maintenanceCount +
                ", totalCount=" + totalCount +
                ", avgPerformance=" + avgPerformance +
                ", statsDate=" + statsDate +
                '}';
    }
}