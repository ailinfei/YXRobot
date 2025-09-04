package com.yxrobot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * 订单统计数据传输对象
 * 适配前端TypeScript接口，确保字段名称完全匹配
 * 用于前端统计卡片显示
 * 
 * 字段映射规范：
 * - Java DTO：camelCase（如：totalOrders, totalRevenue）
 * - 前端接口：camelCase（如：totalOrders, totalRevenue）
 */
public class OrderStatsDTO {
    
    @JsonProperty("total")
    private Integer total;              // 订单总数
    
    @JsonProperty("pending")
    private Integer pending;            // 待处理订单数
    
    @JsonProperty("confirmed")
    private Integer confirmed;          // 已确认订单数
    
    @JsonProperty("processing")
    private Integer processing;         // 处理中订单数
    
    @JsonProperty("shipped")
    private Integer shipped;            // 已发货订单数
    
    @JsonProperty("delivered")
    private Integer delivered;          // 已送达订单数
    
    @JsonProperty("completed")
    private Integer completed;          // 已完成订单数
    
    @JsonProperty("cancelled")
    private Integer cancelled;          // 已取消订单数
    
    @JsonProperty("totalRevenue")
    private BigDecimal totalRevenue;    // 总收入（已完成订单）
    
    @JsonProperty("averageOrderValue")
    private BigDecimal averageOrderValue; // 平均订单价值
    
    @JsonProperty("salesOrders")
    private Integer salesOrders;        // 销售订单数
    
    @JsonProperty("rentalOrders")
    private Integer rentalOrders;       // 租赁订单数
    
    // 构造函数
    public OrderStatsDTO() {
        this.total = 0;
        this.pending = 0;
        this.confirmed = 0;
        this.processing = 0;
        this.shipped = 0;
        this.delivered = 0;
        this.completed = 0;
        this.cancelled = 0;
        this.totalRevenue = BigDecimal.ZERO;
        this.averageOrderValue = BigDecimal.ZERO;
        this.salesOrders = 0;
        this.rentalOrders = 0;
    }
    
    // Getter和Setter方法
    public Integer getTotal() {
        return total;
    }
    
    public void setTotal(Integer total) {
        this.total = total;
    }
    
    public Integer getPending() {
        return pending;
    }
    
    public void setPending(Integer pending) {
        this.pending = pending;
    }
    
    public Integer getConfirmed() {
        return confirmed;
    }
    
    public void setConfirmed(Integer confirmed) {
        this.confirmed = confirmed;
    }
    
    public Integer getProcessing() {
        return processing;
    }
    
    public void setProcessing(Integer processing) {
        this.processing = processing;
    }
    
    public Integer getShipped() {
        return shipped;
    }
    
    public void setShipped(Integer shipped) {
        this.shipped = shipped;
    }
    
    public Integer getDelivered() {
        return delivered;
    }
    
    public void setDelivered(Integer delivered) {
        this.delivered = delivered;
    }
    
    public Integer getCompleted() {
        return completed;
    }
    
    public void setCompleted(Integer completed) {
        this.completed = completed;
    }
    
    public Integer getCancelled() {
        return cancelled;
    }
    
    public void setCancelled(Integer cancelled) {
        this.cancelled = cancelled;
    }
    
    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }
    
    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
    
    public BigDecimal getAverageOrderValue() {
        return averageOrderValue;
    }
    
    public void setAverageOrderValue(BigDecimal averageOrderValue) {
        this.averageOrderValue = averageOrderValue;
    }
    
    public Integer getSalesOrders() {
        return salesOrders;
    }
    
    public void setSalesOrders(Integer salesOrders) {
        this.salesOrders = salesOrders;
    }
    
    public Integer getRentalOrders() {
        return rentalOrders;
    }
    
    public void setRentalOrders(Integer rentalOrders) {
        this.rentalOrders = rentalOrders;
    }
}