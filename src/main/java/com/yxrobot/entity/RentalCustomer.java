package com.yxrobot.entity;

import com.yxrobot.enums.CustomerType;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 租赁客户实体类
 * 对应数据库表 rental_customers
 * 
 * @author Kiro
 * @date 2025-01-28
 */
public class RentalCustomer {
    
    /**
     * 客户ID，主键
     */
    private Long id;
    
    /**
     * 客户名称
     */
    @NotBlank(message = "客户名称不能为空")
    @Size(max = 200, message = "客户名称长度不能超过200字符")
    private String customerName;
    
    /**
     * 客户类型
     */
    @NotNull(message = "客户类型不能为空")
    private CustomerType customerType;
    
    /**
     * 联系人
     */
    @Size(max = 100, message = "联系人长度不能超过100字符")
    private String contactPerson;
    
    /**
     * 联系电话
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$|^0\\d{2,3}-?\\d{7,8}$", message = "联系电话格式不正确")
    @Size(max = 20, message = "联系电话长度不能超过20字符")
    private String phone;
    
    /**
     * 邮箱地址
     */
    @Email(message = "邮箱地址格式不正确")
    @Size(max = 100, message = "邮箱地址长度不能超过100字符")
    private String email;
    
    /**
     * 地址
     */
    private String address;
    
    /**
     * 所在地区
     */
    @Size(max = 100, message = "所在地区长度不能超过100字符")
    private String region;
    
    /**
     * 所属行业
     */
    @Size(max = 100, message = "所属行业长度不能超过100字符")
    private String industry;
    
    /**
     * 信用等级
     */
    private String creditLevel;
    
    /**
     * 累计租赁金额
     */
    @DecimalMin(value = "0", message = "累计租赁金额不能为负数")
    @Digits(integer = 10, fraction = 2, message = "累计租赁金额格式不正确")
    private BigDecimal totalRentalAmount;
    
    /**
     * 累计租赁天数
     */
    @Min(value = 0, message = "累计租赁天数不能为负数")
    private Integer totalRentalDays;
    
    /**
     * 最后租赁日期
     */
    private LocalDate lastRentalDate;
    
    /**
     * 是否活跃
     */
    private Boolean isActive;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 是否删除
     */
    private Boolean isDeleted;
    
    // 构造函数
    public RentalCustomer() {
        this.creditLevel = "B";
        this.totalRentalAmount = BigDecimal.ZERO;
        this.totalRentalDays = 0;
        this.isActive = true;
        this.isDeleted = false;
    }
    
    // Getter 和 Setter 方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public CustomerType getCustomerType() {
        return customerType;
    }
    
    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }
    
    public String getContactPerson() {
        return contactPerson;
    }
    
    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
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
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getRegion() {
        return region;
    }
    
    public void setRegion(String region) {
        this.region = region;
    }
    
    public String getIndustry() {
        return industry;
    }
    
    public void setIndustry(String industry) {
        this.industry = industry;
    }
    
    public String getCreditLevel() {
        return creditLevel;
    }
    
    public void setCreditLevel(String creditLevel) {
        this.creditLevel = creditLevel;
    }
    
    public BigDecimal getTotalRentalAmount() {
        return totalRentalAmount;
    }
    
    public void setTotalRentalAmount(BigDecimal totalRentalAmount) {
        this.totalRentalAmount = totalRentalAmount;
    }
    
    public Integer getTotalRentalDays() {
        return totalRentalDays;
    }
    
    public void setTotalRentalDays(Integer totalRentalDays) {
        this.totalRentalDays = totalRentalDays;
    }
    
    public LocalDate getLastRentalDate() {
        return lastRentalDate;
    }
    
    public void setLastRentalDate(LocalDate lastRentalDate) {
        this.lastRentalDate = lastRentalDate;
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
    
    public Boolean getIsDeleted() {
        return isDeleted;
    }
    
    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    
    @Override
    public String toString() {
        return "RentalCustomer{" +
                "id=" + id +
                ", customerName='" + customerName + '\'' +
                ", customerType=" + customerType +
                ", phone='" + phone + '\'' +
                ", region='" + region + '\'' +
                ", totalRentalAmount=" + totalRentalAmount +
                '}';
    }
}