package com.yxrobot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * 销售人员数据传输对象
 * 与前端SalesStaff接口保持一致
 */
public class SalesStaffDTO {
    
    private Long id;
    
    @JsonProperty("staffName")
    private String staffName;
    
    @JsonProperty("staffCode")
    private String staffCode;
    
    private String department;
    private String position;
    private String phone;
    private String email;
    
    @JsonProperty("hireDate")
    private String hireDate;
    
    @JsonProperty("salesTarget")
    private BigDecimal salesTarget;
    
    @JsonProperty("commissionRate")
    private BigDecimal commissionRate;
    
    @JsonProperty("isActive")
    private Boolean isActive;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("createdAt")
    private String createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("updatedAt")
    private String updatedAt;
    
    // 业绩统计（可选）
    @JsonProperty("currentMonthSales")
    private BigDecimal currentMonthSales;
    
    @JsonProperty("currentMonthOrders")
    private Integer currentMonthOrders;
    
    @JsonProperty("totalSalesAmount")
    private BigDecimal totalSalesAmount;
    
    @JsonProperty("totalOrders")
    private Integer totalOrders;
    
    @JsonProperty("customerCount")
    private Integer customerCount;
    
    @JsonProperty("targetCompletionRate")
    private BigDecimal targetCompletionRate;
    
    // 构造函数
    public SalesStaffDTO() {}
    
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
    
    public String getHireDate() {
        return hireDate;
    }
    
    public void setHireDate(String hireDate) {
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
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(String updatedAt) {
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