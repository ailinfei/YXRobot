package com.yxrobot.dto;

import java.math.BigDecimal;

/**
 * 租赁趋势数据传输对象
 * 适配前端 RentalTrendData TypeScript 接口
 * 
 * @author Kiro
 * @date 2025-01-28
 */
public class RentalTrendDTO {
    
    /**
     * 日期
     * 对应前端字段: date
     */
    private String date;
    
    /**
     * 收入
     * 对应前端字段: revenue
     */
    private BigDecimal revenue;
    
    /**
     * 订单数量
     * 对应前端字段: orderCount
     */
    private Integer orderCount;
    
    /**
     * 设备数量
     * 对应前端字段: deviceCount
     */
    private Integer deviceCount;
    
    /**
     * 利用率
     * 对应前端字段: utilizationRate
     */
    private BigDecimal utilizationRate;
    
    // 构造函数
    public RentalTrendDTO() {
        this.revenue = BigDecimal.ZERO;
        this.orderCount = 0;
        this.deviceCount = 0;
        this.utilizationRate = BigDecimal.ZERO;
    }
    
    public RentalTrendDTO(String date, BigDecimal revenue, Integer orderCount, Integer deviceCount, BigDecimal utilizationRate) {
        this.date = date;
        this.revenue = revenue;
        this.orderCount = orderCount;
        this.deviceCount = deviceCount;
        this.utilizationRate = utilizationRate;
    }
    
    // Getter 和 Setter 方法
    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
    
    public BigDecimal getRevenue() {
        return revenue;
    }
    
    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }
    
    public Integer getOrderCount() {
        return orderCount;
    }
    
    public void setOrderCount(Integer orderCount) {
        this.orderCount = orderCount;
    }
    
    public Integer getDeviceCount() {
        return deviceCount;
    }
    
    public void setDeviceCount(Integer deviceCount) {
        this.deviceCount = deviceCount;
    }
    
    public BigDecimal getUtilizationRate() {
        return utilizationRate;
    }
    
    public void setUtilizationRate(BigDecimal utilizationRate) {
        this.utilizationRate = utilizationRate;
    }
    
    @Override
    public String toString() {
        return "RentalTrendDTO{" +
                "date='" + date + '\'' +
                ", revenue=" + revenue +
                ", orderCount=" + orderCount +
                ", deviceCount=" + deviceCount +
                ", utilizationRate=" + utilizationRate +
                '}';
    }
}