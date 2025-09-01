package com.yxrobot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * 客户统计数据传输对象
 * 与前端CustomerStats接口保持一致
 */
public class CustomerStatsDTO {
    
    // 客户总数统计 - 对应前端统计卡片
    @JsonProperty("total")
    private Integer totalCustomers;
    
    @JsonProperty("regular")
    private Integer regularCustomers;
    
    @JsonProperty("vip")
    private Integer vipCustomers;
    
    @JsonProperty("premium")
    private Integer premiumCustomers;
    
    // 客户状态统计
    @JsonProperty("active")
    private Integer activeCustomers;
    
    @JsonProperty("inactive")
    private Integer inactiveCustomers;
    
    @JsonProperty("suspended")
    private Integer suspendedCustomers;
    
    // 设备统计 - 对应前端设备统计卡片
    @JsonProperty("totalDevices")
    private Integer totalDevices;
    
    @JsonProperty("activeDevices")
    private Integer activeDevices;
    
    @JsonProperty("purchasedDevices")
    private Integer purchasedDevices;
    
    @JsonProperty("rentalDevices")
    private Integer rentalDevices;
    
    // 财务统计 - 对应前端收入统计卡片
    @JsonProperty("totalRevenue")
    private BigDecimal totalRevenue;
    
    @JsonProperty("monthlyRevenue")
    private BigDecimal monthlyRevenue;
    
    @JsonProperty("averageOrderValue")
    private BigDecimal averageOrderValue;
    
    @JsonProperty("totalSpent")
    private BigDecimal totalSpent;
    
    // 订单统计
    @JsonProperty("totalOrders")
    private Integer totalOrders;
    
    @JsonProperty("monthlyOrders")
    private Integer monthlyOrders;
    
    @JsonProperty("completedOrders")
    private Integer completedOrders;
    
    @JsonProperty("pendingOrders")
    private Integer pendingOrders;
    
    // 时间统计 - 对应前端新增客户统计
    @JsonProperty("newThisMonth")
    private Integer newThisMonth;           // 对应前端newThisMonth字段
    
    @JsonProperty("newCustomersThisWeek")
    private Integer newCustomersThisWeek;
    
    @JsonProperty("newCustomersToday")
    private Integer newCustomersToday;
    
    // 地区和行业统计
    @JsonProperty("topRegion")
    private String topRegion;
    
    @JsonProperty("topRegionCount")
    private Integer topRegionCount;
    
    @JsonProperty("topIndustry")
    private String topIndustry;
    
    @JsonProperty("topIndustryCount")
    private Integer topIndustryCount;
    
    // 客户等级分布 - 对应前端图表数据
    @JsonProperty("levelDistribution")
    private LevelDistributionDTO levelDistribution;
    
    // 统计时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("statisticsDate")
    private String statisticsDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("updatedAt")
    private String updatedAt;
    
    // 客户等级分布DTO
    public static class LevelDistributionDTO {
        @JsonProperty("regularPercentage")
        private Double regularPercentage;
        
        @JsonProperty("vipPercentage")
        private Double vipPercentage;
        
        @JsonProperty("premiumPercentage")
        private Double premiumPercentage;
        
        public LevelDistributionDTO() {}
        
        public LevelDistributionDTO(Double regularPercentage, Double vipPercentage, Double premiumPercentage) {
            this.regularPercentage = regularPercentage;
            this.vipPercentage = vipPercentage;
            this.premiumPercentage = premiumPercentage;
        }
        
        // Getter和Setter
        public Double getRegularPercentage() {
            return regularPercentage;
        }
        
        public void setRegularPercentage(Double regularPercentage) {
            this.regularPercentage = regularPercentage;
        }
        
        public Double getVipPercentage() {
            return vipPercentage;
        }
        
        public void setVipPercentage(Double vipPercentage) {
            this.vipPercentage = vipPercentage;
        }
        
        public Double getPremiumPercentage() {
            return premiumPercentage;
        }
        
        public void setPremiumPercentage(Double premiumPercentage) {
            this.premiumPercentage = premiumPercentage;
        }
    }
    
    // 构造函数
    public CustomerStatsDTO() {}
    
    // Getter和Setter方法
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
    
    public Integer getNewThisMonth() {
        return newThisMonth;
    }
    
    public void setNewThisMonth(Integer newThisMonth) {
        this.newThisMonth = newThisMonth;
    }
    
    public Integer getNewCustomersThisMonth() {
        return newThisMonth; // 兼容性方法
    }
    
    public void setNewCustomersThisMonth(Integer newCustomersThisMonth) {
        this.newThisMonth = newCustomersThisMonth; // 兼容性方法
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
    
    public LevelDistributionDTO getLevelDistribution() {
        return levelDistribution;
    }
    
    public void setLevelDistribution(LevelDistributionDTO levelDistribution) {
        this.levelDistribution = levelDistribution;
    }
    
    public String getStatisticsDate() {
        return statisticsDate;
    }
    
    public void setStatisticsDate(String statisticsDate) {
        this.statisticsDate = statisticsDate;
    }
    
    public String getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // 兼容方法 - 为了兼容现有代码中使用的方法名
    public Integer getTotal() {
        return totalCustomers;
    }
    
    public void setTotal(int total) {
        this.totalCustomers = total;
    }
    
    public Integer getRegular() {
        return regularCustomers;
    }
    
    public void setRegular(int regular) {
        this.regularCustomers = regular;
    }
    
    public Integer getVip() {
        return vipCustomers;
    }
    
    public void setVip(int vip) {
        this.vipCustomers = vip;
    }
    
    public Integer getPremium() {
        return premiumCustomers;
    }
    
    public void setPremium(int premium) {
        this.premiumCustomers = premium;
    }
}