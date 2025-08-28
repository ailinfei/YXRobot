package com.yxrobot.entity;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 销售统计实体类
 * 对应数据库表：sales_stats
 * 
 * 字段映射规范：
 * - 数据库字段使用snake_case命名
 * - Java属性使用camelCase命名
 * - 通过MyBatis映射进行转换
 */
public class SalesStats {
    
    /**
     * 统计ID，主键
     * 数据库字段：id
     */
    private Long id;
    
    /**
     * 统计日期
     * 数据库字段：stat_date
     */
    @NotNull(message = "统计日期不能为空")
    private LocalDate statDate;
    
    /**
     * 统计类型
     * 数据库字段：stat_type
     */
    @NotNull(message = "统计类型不能为空")
    private StatType statType;
    
    /**
     * 总销售金额
     * 数据库字段：total_sales_amount
     */
    @DecimalMin(value = "0", message = "总销售金额不能为负数")
    private BigDecimal totalSalesAmount;
    
    /**
     * 总订单数
     * 数据库字段：total_orders
     */
    @Min(value = 0, message = "总订单数不能为负数")
    private Integer totalOrders;
    
    /**
     * 总销售数量
     * 数据库字段：total_quantity
     */
    @Min(value = 0, message = "总销售数量不能为负数")
    private Integer totalQuantity;
    
    /**
     * 平均订单金额
     * 数据库字段：avg_order_amount
     */
    @DecimalMin(value = "0", message = "平均订单金额不能为负数")
    private BigDecimal avgOrderAmount;
    
    /**
     * 新客户数
     * 数据库字段：new_customers
     */
    @Min(value = 0, message = "新客户数不能为负数")
    private Integer newCustomers;
    
    /**
     * 活跃客户数
     * 数据库字段：active_customers
     */
    @Min(value = 0, message = "活跃客户数不能为负数")
    private Integer activeCustomers;
    
    /**
     * 销量最高产品ID
     * 数据库字段：top_product_id
     */
    private Long topProductId;
    
    /**
     * 业绩最高销售人员ID
     * 数据库字段：top_staff_id
     */
    private Long topStaffId;
    
    /**
     * 创建时间
     * 数据库字段：created_at
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     * 数据库字段：updated_at
     */
    private LocalDateTime updatedAt;
    
    // 默认构造函数
    public SalesStats() {
        this.totalSalesAmount = BigDecimal.ZERO;
        this.totalOrders = 0;
        this.totalQuantity = 0;
        this.avgOrderAmount = BigDecimal.ZERO;
        this.newCustomers = 0;
        this.activeCustomers = 0;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalDate getStatDate() {
        return statDate;
    }
    
    public void setStatDate(LocalDate statDate) {
        this.statDate = statDate;
    }
    
    public StatType getStatType() {
        return statType;
    }
    
    public void setStatType(StatType statType) {
        this.statType = statType;
    }
    
    public BigDecimal getTotalSalesAmount() {
        return totalSalesAmount;
    }
    
    public void setTotalSalesAmount(BigDecimal totalSalesAmount) {
        this.totalSalesAmount = totalSalesAmount;
    }
    
    public Integer getTotalOrders() {
        return totalOrders;
    }
    
    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }
    
    public Integer getTotalQuantity() {
        return totalQuantity;
    }
    
    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }
    
    public BigDecimal getAvgOrderAmount() {
        return avgOrderAmount;
    }
    
    public void setAvgOrderAmount(BigDecimal avgOrderAmount) {
        this.avgOrderAmount = avgOrderAmount;
    }
    
    public Integer getNewCustomers() {
        return newCustomers;
    }
    
    public void setNewCustomers(Integer newCustomers) {
        this.newCustomers = newCustomers;
    }
    
    public Integer getActiveCustomers() {
        return activeCustomers;
    }
    
    public void setActiveCustomers(Integer activeCustomers) {
        this.activeCustomers = activeCustomers;
    }
    
    public Long getTopProductId() {
        return topProductId;
    }
    
    public void setTopProductId(Long topProductId) {
        this.topProductId = topProductId;
    }
    
    public Long getTopStaffId() {
        return topStaffId;
    }
    
    public void setTopStaffId(Long topStaffId) {
        this.topStaffId = topStaffId;
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
        return "SalesStats{" +
                "id=" + id +
                ", statDate=" + statDate +
                ", statType=" + statType +
                ", totalSalesAmount=" + totalSalesAmount +
                ", totalOrders=" + totalOrders +
                ", totalQuantity=" + totalQuantity +
                ", avgOrderAmount=" + avgOrderAmount +
                ", newCustomers=" + newCustomers +
                ", activeCustomers=" + activeCustomers +
                ", topProductId=" + topProductId +
                ", topStaffId=" + topStaffId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}