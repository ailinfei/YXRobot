package com.yxrobot.entity;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 客户实体类
 * 支持客户管理模块的完整功能需求
 * 字段映射规范：数据库snake_case -> Java camelCase -> 前端camelCase
 */
public class Customer {
    
    private Long id;
    
    // 基本信息字段 - 与前端Customer接口匹配
    @NotBlank(message = "客户姓名不能为空")
    private String name;                        // 对应前端name字段，数据库customer_name
    
    @NotNull(message = "客户等级不能为空")
    private CustomerLevel level;                // 对应前端level字段，数据库customer_level
    
    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;                       // 对应前端phone字段，数据库phone
    
    @Email(message = "邮箱格式不正确")
    private String email;                       // 对应前端email字段，数据库email
    
    private String company;                     // 对应前端company字段，数据库company
    
    @NotNull(message = "客户状态不能为空")
    private CustomerStatus status;              // 对应前端status字段，数据库customer_status
    
    private String avatar;                      // 对应前端avatar字段，数据库avatar_url
    private List<String> tags;                  // 对应前端tags字段，数据库customer_tags
    private String notes;                       // 对应前端notes字段，数据库notes
    
    // 统计字段 - 与前端Customer接口匹配
    private BigDecimal totalSpent;              // 对应前端totalSpent字段，数据库total_spent
    private BigDecimal customerValue;           // 对应前端customerValue字段，数据库customer_value
    
    // 时间字段 - 与前端Customer接口匹配
    private LocalDateTime registeredAt;         // 对应前端registeredAt字段，数据库registered_at
    private LocalDateTime lastActiveAt;         // 对应前端lastActiveAt字段，数据库last_active_at
    
    // 系统字段
    private LocalDateTime createdAt;            // 数据库created_at
    private LocalDateTime updatedAt;            // 数据库updated_at
    private Boolean isDeleted;                  // 数据库is_deleted
    
    // 关联统计字段 - 支持前端页面显示
    private Integer deviceCount;                // 设备总数
    private Integer purchasedDeviceCount;       // 购买设备数
    private Integer rentalDeviceCount;          // 租赁设备数
    
    // 保留原有字段以兼容现有代码
    private CustomerType customerType;
    private String contactPerson;
    private String address;
    private String region;
    private String industry;
    private CreditLevel creditLevel;
    private Boolean isActive;
    private Integer totalOrders;
    private BigDecimal totalSalesAmount;
    private LocalDateTime lastOrderDate;
    
    // 构造函数
    public Customer() {}
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public CustomerLevel getLevel() {
        return level;
    }
    
    public void setLevel(CustomerLevel level) {
        this.level = level;
    }
    
    public CustomerStatus getStatus() {
        return status;
    }
    
    public void setStatus(CustomerStatus status) {
        this.status = status;
    }
    
    public String getCompany() {
        return company;
    }
    
    public void setCompany(String company) {
        this.company = company;
    }
    
    public String getAvatar() {
        return avatar;
    }
    
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    
    public List<String> getTags() {
        return tags;
    }
    
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    
    // 保留原有字段的getter/setter以兼容现有代码
    public String getCustomerName() {
        return name; // 映射到新的name字段
    }
    
    public void setCustomerName(String customerName) {
        this.name = customerName; // 映射到新的name字段
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
    
    public CreditLevel getCreditLevel() {
        return creditLevel;
    }
    
    public void setCreditLevel(CreditLevel creditLevel) {
        this.creditLevel = creditLevel;
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
    
    public Integer getTotalOrders() {
        return totalOrders;
    }
    
    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }
    
    public BigDecimal getTotalSalesAmount() {
        return totalSalesAmount;
    }
    
    public void setTotalSalesAmount(BigDecimal totalSalesAmount) {
        this.totalSalesAmount = totalSalesAmount;
    }
    
    public LocalDateTime getLastOrderDate() {
        return lastOrderDate;
    }
    
    public void setLastOrderDate(LocalDateTime lastOrderDate) {
        this.lastOrderDate = lastOrderDate;
    }
    
    // 兼容性方法 - 映射到新字段
    public CustomerLevel getCustomerLevel() {
        return level; // 映射到新的level字段
    }
    
    public void setCustomerLevel(CustomerLevel customerLevel) {
        this.level = customerLevel; // 映射到新的level字段
    }
    
    public CustomerStatus getCustomerStatus() {
        return status; // 映射到新的status字段
    }
    
    public void setCustomerStatus(CustomerStatus customerStatus) {
        this.status = customerStatus; // 映射到新的status字段
    }
    
    public String getAvatarUrl() {
        return avatar; // 映射到新的avatar字段
    }
    
    public void setAvatarUrl(String avatarUrl) {
        this.avatar = avatarUrl; // 映射到新的avatar字段
    }
    
    public List<String> getCustomerTags() {
        return tags; // 映射到新的tags字段
    }
    
    public void setCustomerTags(List<String> customerTags) {
        this.tags = customerTags; // 映射到新的tags字段
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public BigDecimal getTotalSpent() {
        return totalSpent;
    }
    
    public void setTotalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }
    
    public BigDecimal getCustomerValue() {
        return customerValue;
    }
    
    public void setCustomerValue(BigDecimal customerValue) {
        this.customerValue = customerValue;
    }
    
    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }
    
    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }
    
    public LocalDateTime getLastActiveAt() {
        return lastActiveAt;
    }
    
    public void setLastActiveAt(LocalDateTime lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
    }
    
    public Boolean getIsDeleted() {
        return isDeleted;
    }
    
    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    
    public Integer getDeviceCount() {
        return deviceCount;
    }
    
    public void setDeviceCount(Integer deviceCount) {
        this.deviceCount = deviceCount;
    }
    
    public Integer getPurchasedDeviceCount() {
        return purchasedDeviceCount;
    }
    
    public void setPurchasedDeviceCount(Integer purchasedDeviceCount) {
        this.purchasedDeviceCount = purchasedDeviceCount;
    }
    
    public Integer getRentalDeviceCount() {
        return rentalDeviceCount;
    }
    
    public void setRentalDeviceCount(Integer rentalDeviceCount) {
        this.rentalDeviceCount = rentalDeviceCount;
    }
}