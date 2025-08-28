package com.yxrobot.dto;

import com.yxrobot.entity.StatType;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 销售统计数据传输对象
 * 用于前后端数据传输
 * 
 * 字段映射规范：
 * - 与SalesStats实体类保持一致的camelCase命名
 * - 用于API接口的请求和响应数据传输
 */
public class SalesStatsDTO {
    
    /**
     * 统计ID
     */
    private Long id;
    
    /**
     * 统计日期
     */
    @NotNull(message = "统计日期不能为空")
    private LocalDate statDate;
    
    /**
     * 统计类型
     */
    @NotNull(message = "统计类型不能为空")
    private StatType statType;
    
    /**
     * 总销售金额
     */
    @DecimalMin(value = "0", message = "总销售金额不能为负数")
    private BigDecimal totalSalesAmount;
    
    /**
     * 总订单数
     */
    @Min(value = 0, message = "总订单数不能为负数")
    private Integer totalOrders;
    
    /**
     * 总销售数量
     */
    @Min(value = 0, message = "总销售数量不能为负数")
    private Integer totalQuantity;
    
    /**
     * 平均订单金额
     */
    @DecimalMin(value = "0", message = "平均订单金额不能为负数")
    private BigDecimal avgOrderAmount;
    
    /**
     * 新客户数
     */
    @Min(value = 0, message = "新客户数不能为负数")
    private Integer newCustomers;
    
    /**
     * 活跃客户数
     */
    @Min(value = 0, message = "活跃客户数不能为负数")
    private Integer activeCustomers;
    
    /**
     * 销量最高产品ID
     */
    private Long topProductId;
    
    /**
     * 销量最高产品名称（关联查询字段）
     */
    private String topProductName;
    
    /**
     * 业绩最高销售人员ID
     */
    private Long topStaffId;
    
    /**
     * 业绩最高销售人员姓名（关联查询字段）
     */
    private String topStaffName;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    // 扩展统计字段（用于仪表板展示）
    /**
     * 同比增长率
     */
    private BigDecimal yearOverYearGrowth;
    
    /**
     * 环比增长率
     */
    private BigDecimal monthOverMonthGrowth;
    
    /**
     * 目标完成率
     */
    private BigDecimal targetCompletionRate;
    
    /**
     * 客户转化率
     */
    private BigDecimal customerConversionRate;
    
    /**
     * 复购率
     */
    private BigDecimal repeatPurchaseRate;
    
    // 默认构造函数
    public SalesStatsDTO() {
        this.totalSalesAmount = BigDecimal.ZERO;
        this.totalOrders = 0;
        this.totalQuantity = 0;
        this.avgOrderAmount = BigDecimal.ZERO;
        this.newCustomers = 0;
        this.activeCustomers = 0;
        this.yearOverYearGrowth = BigDecimal.ZERO;
        this.monthOverMonthGrowth = BigDecimal.ZERO;
        this.targetCompletionRate = BigDecimal.ZERO;
        this.customerConversionRate = BigDecimal.ZERO;
        this.repeatPurchaseRate = BigDecimal.ZERO;
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
    
    public String getTopProductName() {
        return topProductName;
    }
    
    public void setTopProductName(String topProductName) {
        this.topProductName = topProductName;
    }
    
    public Long getTopStaffId() {
        return topStaffId;
    }
    
    public void setTopStaffId(Long topStaffId) {
        this.topStaffId = topStaffId;
    }
    
    public String getTopStaffName() {
        return topStaffName;
    }
    
    public void setTopStaffName(String topStaffName) {
        this.topStaffName = topStaffName;
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
    
    public BigDecimal getYearOverYearGrowth() {
        return yearOverYearGrowth;
    }
    
    public void setYearOverYearGrowth(BigDecimal yearOverYearGrowth) {
        this.yearOverYearGrowth = yearOverYearGrowth;
    }
    
    public BigDecimal getMonthOverMonthGrowth() {
        return monthOverMonthGrowth;
    }
    
    public void setMonthOverMonthGrowth(BigDecimal monthOverMonthGrowth) {
        this.monthOverMonthGrowth = monthOverMonthGrowth;
    }
    
    public BigDecimal getTargetCompletionRate() {
        return targetCompletionRate;
    }
    
    public void setTargetCompletionRate(BigDecimal targetCompletionRate) {
        this.targetCompletionRate = targetCompletionRate;
    }
    
    public BigDecimal getCustomerConversionRate() {
        return customerConversionRate;
    }
    
    public void setCustomerConversionRate(BigDecimal customerConversionRate) {
        this.customerConversionRate = customerConversionRate;
    }
    
    public BigDecimal getRepeatPurchaseRate() {
        return repeatPurchaseRate;
    }
    
    public void setRepeatPurchaseRate(BigDecimal repeatPurchaseRate) {
        this.repeatPurchaseRate = repeatPurchaseRate;
    }
    
    @Override
    public String toString() {
        return "SalesStatsDTO{" +
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
                ", topProductName='" + topProductName + '\'' +
                ", topStaffId=" + topStaffId +
                ", topStaffName='" + topStaffName + '\'' +
                ", yearOverYearGrowth=" + yearOverYearGrowth +
                ", monthOverMonthGrowth=" + monthOverMonthGrowth +
                ", targetCompletionRate=" + targetCompletionRate +
                ", customerConversionRate=" + customerConversionRate +
                ", repeatPurchaseRate=" + repeatPurchaseRate +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}