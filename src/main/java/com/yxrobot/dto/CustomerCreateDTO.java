package com.yxrobot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yxrobot.validation.annotation.ValidCustomerPhone;
import com.yxrobot.validation.annotation.ValidCustomerEmail;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 客户创建数据传输对象
 * 用于前端创建客户时的请求数据
 * 字段映射：前端camelCase -> 后端camelCase -> 数据库snake_case
 */
public class CustomerCreateDTO {
    
    @NotBlank(message = "客户姓名不能为空")
    @Size(min = 2, max = 50, message = "客户姓名长度应在2-50字符之间")
    private String name;                    // 对应前端name字段
    
    @NotBlank(message = "客户等级不能为空")
    @Pattern(regexp = "^(REGULAR|VIP|PREMIUM)$", message = "客户等级必须为REGULAR、VIP或PREMIUM")
    private String level;                   // 对应前端level字段
    
    @ValidCustomerPhone(message = "请输入正确的手机号码")
    private String phone;                   // 对应前端phone字段
    
    @ValidCustomerEmail(message = "请输入正确的邮箱地址")
    private String email;                   // 对应前端email字段
    
    private String company;                 // 对应前端company字段
    
    @Pattern(regexp = "^(ACTIVE|INACTIVE|SUSPENDED)$", message = "客户状态必须为ACTIVE、INACTIVE或SUSPENDED")
    private String status;                  // 对应前端status字段
    
    // 地址信息 - 对应前端address对象
    private AddressCreateInfo address;
    
    // 标签和备注 - 对应前端tags和notes字段
    private List<String> tags;
    
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String notes;
    
    // 头像 - 对应前端avatar字段
    private String avatar;
    
    // 内部类 - 地址创建信息
    public static class AddressCreateInfo {
        private String province;
        private String city;
        private String district;
        private String detail;
        private String postalCode;
        
        public AddressCreateInfo() {}
        
        // Getter和Setter
        public String getProvince() { return province; }
        public void setProvince(String province) { this.province = province; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getDistrict() { return district; }
        public void setDistrict(String district) { this.district = district; }
        public String getDetail() { return detail; }
        public void setDetail(String detail) { this.detail = detail; }
        public String getPostalCode() { return postalCode; }
        public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    }
    
    // 构造函数
    public CustomerCreateDTO() {}
    
    // Getter和Setter方法
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getLevel() {
        return level;
    }
    
    public void setLevel(String level) {
        this.level = level;
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
    
    public String getCompany() {
        return company;
    }
    
    public void setCompany(String company) {
        this.company = company;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public AddressCreateInfo getAddress() {
        return address;
    }
    
    public void setAddress(AddressCreateInfo address) {
        this.address = address;
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
    
    public String getAvatar() {
        return avatar;
    }
    
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    
    // 兼容方法 - 为了兼容现有代码中使用的方法名
    public String getCustomerName() {
        return name;
    }
    
    public void setCustomerName(String customerName) {
        this.name = customerName;
    }
    
    public String getContactPerson() {
        return name; // 对于个人客户，联系人就是客户姓名
    }
    
    public void setContactPerson(String contactPerson) {
        this.name = contactPerson;
    }
    
    public String getAvatarUrl() {
        return avatar;
    }
    
    public void setAvatarUrl(String avatarUrl) {
        this.avatar = avatarUrl;
    }
    
    public String getCustomerLevel() {
        return level;
    }
    
    public void setCustomerLevel(String customerLevel) {
        this.level = customerLevel;
    }
    
    public String getCustomerStatus() {
        return status;
    }
    
    public void setCustomerStatus(String customerStatus) {
        this.status = customerStatus;
    }
}