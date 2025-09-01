package com.yxrobot.dto;

import java.math.BigDecimal;

/**
 * 租赁统计数据传输对象
 * 适配前端 RentalStats TypeScript 接口
 * 
 * @author Kiro
 * @date 2025-01-28
 */
public class RentalStatsDTO {
    
    /**
     * 租赁总收入
     * 对应前端字段: totalRentalRevenue
     */
    private BigDecimal totalRentalRevenue;
    
    /**
     * 租赁设备总数
     * 对应前端字段: totalRentalDevices
     */
    private Integer totalRentalDevices;
    
    /**
     * 活跃租赁设备数
     * 对应前端字段: activeRentalDevices
     */
    private Integer activeRentalDevices;
    
    /**
     * 设备利用率
     * 对应前端字段: deviceUtilizationRate
     */
    private BigDecimal deviceUtilizationRate;
    
    /**
     * 平均租期
     * 对应前端字段: averageRentalPeriod
     */
    private BigDecimal averageRentalPeriod;
    
    /**
     * 租赁订单总数
     * 对应前端字段: totalRentalOrders
     */
    private Integer totalRentalOrders;
    
    /**
     * 收入增长率
     * 对应前端字段: revenueGrowthRate
     */
    private BigDecimal revenueGrowthRate;
    
    /**
     * 设备增长率
     * 对应前端字段: deviceGrowthRate
     */
    private BigDecimal deviceGrowthRate;
    
    // 构造函数
    public RentalStatsDTO() {
        this.totalRentalRevenue = BigDecimal.ZERO;
        this.totalRentalDevices = 0;
        this.activeRentalDevices = 0;
        this.deviceUtilizationRate = BigDecimal.ZERO;
        this.averageRentalPeriod = BigDecimal.ZERO;
        this.totalRentalOrders = 0;
        this.revenueGrowthRate = BigDecimal.ZERO;
        this.deviceGrowthRate = BigDecimal.ZERO;
    }
    
    // Getter 和 Setter 方法
    public BigDecimal getTotalRentalRevenue() {
        return totalRentalRevenue;
    }
    
    public void setTotalRentalRevenue(BigDecimal totalRentalRevenue) {
        this.totalRentalRevenue = totalRentalRevenue;
    }
    
    public Integer getTotalRentalDevices() {
        return totalRentalDevices;
    }
    
    public void setTotalRentalDevices(Integer totalRentalDevices) {
        this.totalRentalDevices = totalRentalDevices;
    }
    
    public Integer getActiveRentalDevices() {
        return activeRentalDevices;
    }
    
    public void setActiveRentalDevices(Integer activeRentalDevices) {
        this.activeRentalDevices = activeRentalDevices;
    }
    
    public BigDecimal getDeviceUtilizationRate() {
        return deviceUtilizationRate;
    }
    
    public void setDeviceUtilizationRate(BigDecimal deviceUtilizationRate) {
        this.deviceUtilizationRate = deviceUtilizationRate;
    }
    
    public BigDecimal getAverageRentalPeriod() {
        return averageRentalPeriod;
    }
    
    public void setAverageRentalPeriod(BigDecimal averageRentalPeriod) {
        this.averageRentalPeriod = averageRentalPeriod;
    }
    
    public Integer getTotalRentalOrders() {
        return totalRentalOrders;
    }
    
    public void setTotalRentalOrders(Integer totalRentalOrders) {
        this.totalRentalOrders = totalRentalOrders;
    }
    
    public BigDecimal getRevenueGrowthRate() {
        return revenueGrowthRate;
    }
    
    public void setRevenueGrowthRate(BigDecimal revenueGrowthRate) {
        this.revenueGrowthRate = revenueGrowthRate;
    }
    
    public BigDecimal getDeviceGrowthRate() {
        return deviceGrowthRate;
    }
    
    public void setDeviceGrowthRate(BigDecimal deviceGrowthRate) {
        this.deviceGrowthRate = deviceGrowthRate;
    }
    
    @Override
    public String toString() {
        return "RentalStatsDTO{" +
                "totalRentalRevenue=" + totalRentalRevenue +
                ", totalRentalDevices=" + totalRentalDevices +
                ", activeRentalDevices=" + activeRentalDevices +
                ", deviceUtilizationRate=" + deviceUtilizationRate +
                ", averageRentalPeriod=" + averageRentalPeriod +
                ", totalRentalOrders=" + totalRentalOrders +
                ", revenueGrowthRate=" + revenueGrowthRate +
                ", deviceGrowthRate=" + deviceGrowthRate +
                '}';
    }
}