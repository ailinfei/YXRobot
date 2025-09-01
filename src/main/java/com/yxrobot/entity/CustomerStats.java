package com.yxrobot.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 客户统计实体类
 * 用于存储客户相关的统计数据
 */
public class CustomerStats {
    
    private Long id;
    
    // 客户总数统计
    private Integer totalCustomers;           // 总客户数
    private Integer regularCustomers;         // 普通客户数
    private Integer vipCustomers;            // VIP客户数
    private Integer premiumCustomers;        // 高级客户数
    
    // 客户状态统计
    private Integer activeCustomers;         // 活跃客户数
    private Integer inactiveCustomers;       // 不活跃客户数
    private Integer suspendedCustomers;      // 暂停客户数
    
    // 设备统计
    private Integer totalDevices;            // 总设备数
    private Integer activeDevices;           // 活跃设备数
    private Integer purchasedDevices;        // 购买设备数
    private Integer rentalDevices;           // 租赁设备数
    
    // 财务统计
    private BigDecimal totalRevenue;         // 总收入
    private BigDecimal monthlyRevenue;       // 月收入
    private BigDecimal averageOrderValue;    // 平均订单价值
    private BigDecimal totalSpent;           // 客户总消费
    
    // 订单统计
    private Integer totalOrders;             // 总订单数
    private Integer monthlyOrders;           // 月订单数
    private Integer completedOrders;         // 已完成订单数
    private Integer pendingOrders;           // 待处理订单数
    
    // 时间统计
    private Integer newCustomersThisMonth;   // 本月新增客户
    private Integer newCustomersThisWeek;    // 本周新增客户
    private Integer newCustomersToday;       // 今日新增客户
    
    // 地区统计
    private String topRegion;                // 客户最多的地区
    private Integer topRegionCount;          // 最多地区的客户数
    
    // 行业统计
    private String topIndustry;              // 客户最多的行业
    private Integer topIndustryCount;        // 最多行业的客户数
    
    // 统计时间
    private LocalDateTime statisticsDate;    // 统计日期
    private LocalDateTime createdAt;         // 创建时间
    private LocalDateTime updatedAt;         // 更新时间
    
    // 构造函数
    public CustomerStats() {}
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Integer getTotalCustomers() {
        return totalCustomers;
    }
    
    public void setTotalCustomers(Integer totalCustomers) {
        this.totalCustomers = totalCustomers;
    }
    
    public Integer getRegularCustomers() {
        return regularCustomers;
    }
    
    public void setRegularCustomers(Integer regularCustomers) {
        this.regularCustomers = regularCustomers;
    }
    
    public Integer getVipCustomers() {
        return vipCustomers;
    }
    
    public void setVipCustomers(Integer vipCustomers) {
        this.vipCustomers = vipCustomers;
    }
    
    public Integer getPremiumCustomers() {
        return premiumCustomers;
    }
    
    public void setPremiumCustomers(Integer premiumCustomers) {
        this.premiumCustomers = premiumCustomers;
    }
    
    public Integer getActiveCustomers() {
        return activeCustomers;
    }
    
    public void setActiveCustomers(Integer activeCustomers) {
        this.activeCustomers = activeCustomers;
    }
    
    public Integer getInactiveCustomers() {
        return inactiveCustomers;
    }
    
    public void setInactiveCustomers(Integer inactiveCustomers) {
        this.inactiveCustomers = inactiveCustomers;
    }
    
    public Integer getSuspendedCustomers() {
        return suspendedCustomers;
    }
    
    public void setSuspendedCustomers(Integer suspendedCustomers) {
        this.suspendedCustomers = suspendedCustomers;
    }
    
    public Integer getTotalDevices() {
        return totalDevices;
    }
    
    public void setTotalDevices(Integer totalDevices) {
        this.totalDevices = totalDevices;
    }
    
    public Integer getActiveDevices() {
        return activeDevices;
    }
    
    public void setActiveDevices(Integer activeDevices) {
        this.activeDevices = activeDevices;
    }
    
    public Integer getPurchasedDevices() {
        return purchasedDevices;
    }
    
    public void setPurchasedDevices(Integer purchasedDevices) {
        this.purchasedDevices = purchasedDevices;
    }
    
    public Integer getRentalDevices() {
        return rentalDevices;
    }
    
    public void setRentalDevices(Integer rentalDevices) {
        this.rentalDevices = rentalDevices;
    }
    
    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }
    
    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
    
    public BigDecimal getMonthlyRevenue() {
        return monthlyRevenue;
    }
    
    public void setMonthlyRevenue(BigDecimal monthlyRevenue) {
        this.monthlyRevenue = monthlyRevenue;
    }
    
    public BigDecimal getAverageOrderValue() {
        return averageOrderValue;
    }
    
    public void setAverageOrderValue(BigDecimal averageOrderValue) {
        this.averageOrderValue = averageOrderValue;
    }
    
    public BigDecimal getTotalSpent() {
        return totalSpent;
    }
    
    public void setTotalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }
    
    public Integer getTotalOrders() {
        return totalOrders;
    }
    
    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }
    
    public Integer getMonthlyOrders() {
        return monthlyOrders;
    }
    
    public void setMonthlyOrders(Integer monthlyOrders) {
        this.monthlyOrders = monthlyOrders;
    }
    
    public Integer getCompletedOrders() {
        return completedOrders;
    }
    
    public void setCompletedOrders(Integer completedOrders) {
        this.completedOrders = completedOrders;
    }
    
    public Integer getPendingOrders() {
        return pendingOrders;
    }
    
    public void setPendingOrders(Integer pendingOrders) {
        this.pendingOrders = pendingOrders;
    }
    
    public Integer getNewCustomersThisMonth() {
        return newCustomersThisMonth;
    }
    
    public void setNewCustomersThisMonth(Integer newCustomersThisMonth) {
        this.newCustomersThisMonth = newCustomersThisMonth;
    }
    
    public Integer getNewCustomersThisWeek() {
        return newCustomersThisWeek;
    }
    
    public void setNewCustomersThisWeek(Integer newCustomersThisWeek) {
        this.newCustomersThisWeek = newCustomersThisWeek;
    }
    
    public Integer getNewCustomersToday() {
        return newCustomersToday;
    }
    
    public void setNewCustomersToday(Integer newCustomersToday) {
        this.newCustomersToday = newCustomersToday;
    }
    
    public String getTopRegion() {
        return topRegion;
    }
    
    public void setTopRegion(String topRegion) {
        this.topRegion = topRegion;
    }
    
    public Integer getTopRegionCount() {
        return topRegionCount;
    }
    
    public void setTopRegionCount(Integer topRegionCount) {
        this.topRegionCount = topRegionCount;
    }
    
    public String getTopIndustry() {
        return topIndustry;
    }
    
    public void setTopIndustry(String topIndustry) {
        this.topIndustry = topIndustry;
    }
    
    public Integer getTopIndustryCount() {
        return topIndustryCount;
    }
    
    public void setTopIndustryCount(Integer topIndustryCount) {
        this.topIndustryCount = topIndustryCount;
    }
    
    public LocalDateTime getStatisticsDate() {
        return statisticsDate;
    }
    
    public void setStatisticsDate(LocalDateTime statisticsDate) {
        this.statisticsDate = statisticsDate;
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
    
    /**
     * 计算客户等级分布百分比
     */
    public CustomerLevelDistribution getCustomerLevelDistribution() {
        if (totalCustomers == null || totalCustomers == 0) {
            return new CustomerLevelDistribution(0.0, 0.0, 0.0);
        }
        
        double regularPercentage = (regularCustomers != null ? regularCustomers : 0) * 100.0 / totalCustomers;
        double vipPercentage = (vipCustomers != null ? vipCustomers : 0) * 100.0 / totalCustomers;
        double premiumPercentage = (premiumCustomers != null ? premiumCustomers : 0) * 100.0 / totalCustomers;
        
        return new CustomerLevelDistribution(regularPercentage, vipPercentage, premiumPercentage);
    }
    
    /**
     * 客户等级分布内部类
     */
    public static class CustomerLevelDistribution {
        private final double regularPercentage;
        private final double vipPercentage;
        private final double premiumPercentage;
        
        public CustomerLevelDistribution(double regularPercentage, double vipPercentage, double premiumPercentage) {
            this.regularPercentage = regularPercentage;
            this.vipPercentage = vipPercentage;
            this.premiumPercentage = premiumPercentage;
        }
        
        public double getRegularPercentage() {
            return regularPercentage;
        }
        
        public double getVipPercentage() {
            return vipPercentage;
        }
        
        public double getPremiumPercentage() {
            return premiumPercentage;
        }
    }
    
    @Override
    public String toString() {
        return "CustomerStats{" +
                "id=" + id +
                ", totalCustomers=" + totalCustomers +
                ", regularCustomers=" + regularCustomers +
                ", vipCustomers=" + vipCustomers +
                ", premiumCustomers=" + premiumCustomers +
                ", activeCustomers=" + activeCustomers +
                ", totalDevices=" + totalDevices +
                ", totalRevenue=" + totalRevenue +
                ", statisticsDate=" + statisticsDate +
                '}';
    }
}