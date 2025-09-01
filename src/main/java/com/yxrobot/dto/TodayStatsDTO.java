package com.yxrobot.dto;

import java.math.BigDecimal;

/**
 * 今日概览数据传输对象
 * 适配前端右侧面板的今日概览功能
 * 
 * @author Kiro
 * @date 2025-01-28
 */
public class TodayStatsDTO {
    
    /**
     * 今日收入
     * 对应前端字段: revenue
     */
    private BigDecimal revenue;
    
    /**
     * 新增订单
     * 对应前端字段: orders
     */
    private Integer orders;
    
    /**
     * 活跃设备
     * 对应前端字段: activeDevices
     */
    private Integer activeDevices;
    
    /**
     * 平均利用率
     * 对应前端字段: avgUtilization
     */
    private BigDecimal avgUtilization;
    
    // 构造函数
    public TodayStatsDTO() {
        this.revenue = BigDecimal.ZERO;
        this.orders = 0;
        this.activeDevices = 0;
        this.avgUtilization = BigDecimal.ZERO;
    }
    
    public TodayStatsDTO(BigDecimal revenue, Integer orders, Integer activeDevices, BigDecimal avgUtilization) {
        this.revenue = revenue;
        this.orders = orders;
        this.activeDevices = activeDevices;
        this.avgUtilization = avgUtilization;
    }
    
    // Getter 和 Setter 方法
    public BigDecimal getRevenue() {
        return revenue;
    }
    
    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }
    
    public Integer getOrders() {
        return orders;
    }
    
    public void setOrders(Integer orders) {
        this.orders = orders;
    }
    
    public Integer getActiveDevices() {
        return activeDevices;
    }
    
    public void setActiveDevices(Integer activeDevices) {
        this.activeDevices = activeDevices;
    }
    
    public BigDecimal getAvgUtilization() {
        return avgUtilization;
    }
    
    public void setAvgUtilization(BigDecimal avgUtilization) {
        this.avgUtilization = avgUtilization;
    }
    
    @Override
    public String toString() {
        return "TodayStatsDTO{" +
                "revenue=" + revenue +
                ", orders=" + orders +
                ", activeDevices=" + activeDevices +
                ", avgUtilization=" + avgUtilization +
                '}';
    }
}