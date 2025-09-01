package com.yxrobot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 客户数据传输对象
 * 与前端Customer接口保持一致，支持客户管理模块完整功能
 */
public class CustomerDTO {
    
    private Long id;
    
    // 基本信息字段 - 映射到前端
    @JsonProperty("name")  // 前端期望的字段名
    private String customerName;
    
    @JsonProperty("level")  // 前端期望的字段名
    private String customerLevel;
    
    @JsonProperty("status")  // 前端期望的字段名
    private String customerStatus;
    
    @JsonProperty("company")  // 前端期望的字段名
    private String contactPerson;
    
    private String phone;
    private String email;
    
    @JsonProperty("avatar")  // 前端期望的字段名
    private String avatarUrl;
    
    private List<String> tags;  // 客户标签
    private String notes;       // 备注信息
    
    // 地址信息 - 支持前端地址对象结构
    private AddressInfo address;
    
    // 统计信息字段 - 映射到前端
    @JsonProperty("totalSpent")
    private BigDecimal totalSpent;
    
    @JsonProperty("customerValue")
    private BigDecimal customerValue;
    
    // 设备统计信息 - 支持前端设备数量显示
    private DeviceCountInfo deviceCount;
    
    // 时间字段
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("registeredAt")
    private String registeredAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("lastActiveAt")
    private String lastActiveAt;
    
    // 保持现有字段兼容性
    @JsonProperty("customerType")
    private String customerType;
    
    private String region;
    private String industry;
    
    @JsonProperty("creditLevel")
    private String creditLevel;
    
    @JsonProperty("isActive")
    private Boolean isActive;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("createdAt")
    private String createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("updatedAt")
    private String updatedAt;
    
    // 统计信息（保持现有字段）
    @JsonProperty("totalOrders")
    private Integer totalOrders;
    
    @JsonProperty("totalSalesAmount")
    private BigDecimal totalSalesAmount;
    
    @JsonProperty("lastOrderDate")
    private String lastOrderDate;
    
    // 内部类 - 地址信息
    public static class AddressInfo {
        private String province;
        private String city;
        private String detail;
        
        public AddressInfo() {}
        
        public AddressInfo(String province, String city, String detail) {
            this.province = province;
            this.city = city;
            this.detail = detail;
        }
        
        // Getter和Setter
        public String getProvince() { return province; }
        public void setProvince(String province) { this.province = province; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getDetail() { return detail; }
        public void setDetail(String detail) { this.detail = detail; }
    }
    
    // 内部类 - 设备数量信息
    public static class DeviceCountInfo {
        private Integer total;
        private Integer purchased;
        private Integer rental;
        
        public DeviceCountInfo() {}
        
        public DeviceCountInfo(Integer total, Integer purchased, Integer rental) {
            this.total = total;
            this.purchased = purchased;
            this.rental = rental;
        }
        
        // Getter和Setter
        public Integer getTotal() { return total; }
        public void setTotal(Integer total) { this.total = total; }
        public Integer getPurchased() { return purchased; }
        public void setPurchased(Integer purchased) { this.purchased = purchased; }
        public Integer getRental() { return rental; }
        public void setRental(Integer rental) { this.rental = rental; }
    }
    
    // 构造函数
    public CustomerDTO() {}
    
    // Getter和Setter方法
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
    
    public String getCustomerType() {
        return customerType;
    }
    
    public void setCustomerType(String customerType) {
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
    
    public AddressInfo getAddress() {
        return address;
    }
    
    public void setAddress(AddressInfo address) {
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
    
    public String getLastOrderDate() {
        return lastOrderDate;
    }
    
    public void setLastOrderDate(String lastOrderDate) {
        this.lastOrderDate = lastOrderDate;
    }
    
    // 新增字段的Getter和Setter方法
    public String getCustomerLevel() {
        return customerLevel;
    }
    
    public void setCustomerLevel(String customerLevel) {
        this.customerLevel = customerLevel;
    }
    
    public String getCustomerStatus() {
        return customerStatus;
    }
    
    public void setCustomerStatus(String customerStatus) {
        this.customerStatus = customerStatus;
    }
    
    public String getAvatarUrl() {
        return avatarUrl;
    }
    
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
    
    public List<String> getTags() {
        return tags;
    }
    
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public AddressInfo getAddressInfo() {
        return address;
    }
    
    public void setAddressInfo(AddressInfo address) {
        this.address = address;
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
    
    public DeviceCountInfo getDeviceCount() {
        return deviceCount;
    }
    
    public void setDeviceCount(DeviceCountInfo deviceCount) {
        this.deviceCount = deviceCount;
    }
    
    public String getRegisteredAt() {
        return registeredAt;
    }
    
    public void setRegisteredAt(String registeredAt) {
        this.registeredAt = registeredAt;
    }
    
    public String getLastActiveAt() {
        return lastActiveAt;
    }
    
    public void setLastActiveAt(String lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
    }
    
    /**
     * 获取客户名称（兼容方法）
     * 映射到customerName字段
     */
    public String getName() {
        return customerName;
    }
    
    /**
     * 设置客户名称（兼容方法）
     */
    public void setName(String name) {
        this.customerName = name;
    }
    
    /**
     * 获取客户等级（兼容方法）
     * 映射到customerLevel字段
     */
    public String getLevel() {
        return customerLevel;
    }
    
    /**
     * 设置客户等级（兼容方法）
     */
    public void setLevel(String level) {
        this.customerLevel = level;
    }
    
    /**
     * 获取地址字符串表示
     * 用于解决AddressInfo类型转换问题
     */
    public String getAddressString() {
        if (address == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        if (address.getProvince() != null) {
            sb.append(address.getProvince());
        }
        if (address.getCity() != null) {
            if (sb.length() > 0) sb.append("-");
            sb.append(address.getCity());
        }
        if (address.getDetail() != null) {
            if (sb.length() > 0) sb.append("-");
            sb.append(address.getDetail());
        }
        return sb.toString();
    }
    
    /**
     * 从地址字符串设置地址信息
     */
    public void setAddressFromString(String addressStr) {
        if (addressStr == null || addressStr.trim().isEmpty()) {
            this.address = null;
            return;
        }
        
        String[] parts = addressStr.split("-");
        AddressInfo addr = new AddressInfo();
        
        if (parts.length >= 1) {
            addr.setProvince(parts[0].trim());
        }
        if (parts.length >= 2) {
            addr.setCity(parts[1].trim());
        }
        if (parts.length >= 3) {
            addr.setDetail(parts[2].trim());
        }
        
        this.address = addr;
    }
}