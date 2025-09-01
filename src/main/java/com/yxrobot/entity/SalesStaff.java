package com.yxrobot.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 销售人员实体类
 */
public class SalesStaff {
    
    private Long id;
    private String staffName;
    private String staffCode;
    private String department;
    private String position;
    private String phone;
    private String email;
    private LocalDate hireDate;
    private BigDecimal salesTarget;
    private BigDecimal commissionRate;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 业绩统计字段
    private BigDecimal currentMonthSales;
    private Integer currentMonthOrders;
    private BigDecimal totalSalesAmount;
    private Integer totalOrders;
    private Integer customerCount;
    private BigDecimal targetCompletionRate;
    
    // 构造函数
    public SalesStaff() {}
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getStaffName() {
        return staffName;
    }
    
    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }
    
    public String getStaffCode() {
        return staffCode;
    }
    
    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public String getPosition() {
        return position;
    }
    
    public void setPosition(String position) {
        this.position = position;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public LocalDate getHireDate() {
        return hireDate;
    }
    
    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }
    
    public BigDecimal getSalesTarget() {
        return salesTarget;
    }
    
    public void setSalesTarget(BigDecimal salesTarget) {
        this.salesTarget = salesTarget;
    }
    
    public BigDecimal getCommissionRate() {
        return commissionRate;
    }
    
    public void setCommissionRate(BigDecimal commissionRate) {
        this.commissionRate = commissionRate;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
    
    public BigDecimal getCurrentMonthSales() {
        return currentMonthSales;
    }
    
    public void setCurrentMonthSales(BigDecimal currentMonthSales) {
        this.currentMonthSales = currentMonthSales;
    }
    
    public Integer getCurrentMonthOrders() {
        return currentMonthOrders;
    }
    
    public void setCurrentMonthOrders(Integer currentMonthOrders) {
        this.currentMonthOrders = currentMonthOrders;
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
    
    public Integer getCustomerCount() {
        return customerCount;
    }
    
    public void setCustomerCount(Integer customerCount) {
        this.customerCount = customerCount;
    }
    
    public BigDecimal getTargetCompletionRate() {
        return targetCompletionRate;
    }
    
    public void setTargetCompletionRate(BigDecimal targetCompletionRate) {
        this.targetCompletionRate = targetCompletionRate;
    }
}