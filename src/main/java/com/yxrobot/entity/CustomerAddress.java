package com.yxrobot.entity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 客户地址实体类
 * 支持客户多地址管理
 */
public class CustomerAddress {
    
    private Long id;
    
    // 关联客户ID
    private Long customerId;
    
    // 地址类型
    private AddressType addressType;
    
    // 地址信息
    @NotBlank(message = "省份不能为空")
    @Size(max = 50, message = "省份名称不能超过50个字符")
    private String province;
    
    @NotBlank(message = "城市不能为空")
    @Size(max = 50, message = "城市名称不能超过50个字符")
    private String city;
    
    @Size(max = 50, message = "区县名称不能超过50个字符")
    private String district;
    
    @Size(max = 200, message = "详细地址不能超过200个字符")
    private String detail;
    
    @Size(max = 10, message = "邮政编码不能超过10个字符")
    private String postalCode;
    
    // 联系信息
    @Size(max = 50, message = "联系人姓名不能超过50个字符")
    private String contactName;
    
    @Size(max = 20, message = "联系电话不能超过20个字符")
    private String contactPhone;
    
    // 是否为默认地址
    private Boolean isDefault;
    
    // 是否启用
    private Boolean isActive;
    
    // 系统字段
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;
    
    // 地址类型枚举
    public enum AddressType {
        HOME("home", "家庭地址"),
        OFFICE("office", "办公地址"),
        DELIVERY("delivery", "收货地址"),
        BILLING("billing", "账单地址"),
        OTHER("other", "其他地址");
        
        private final String code;
        private final String name;
        
        AddressType(String code, String name) {
            this.code = code;
            this.name = name;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getName() {
            return name;
        }
        
        public static AddressType fromCode(String code) {
            if (code == null) {
                return null;
            }
            
            for (AddressType type : values()) {
                if (type.code.equalsIgnoreCase(code)) {
                    return type;
                }
            }
            
            throw new IllegalArgumentException("未知的地址类型代码: " + code);
        }
    }
    
    // 构造函数
    public CustomerAddress() {}
    
    public CustomerAddress(Long customerId, String province, String city, String detail) {
        this.customerId = customerId;
        this.province = province;
        this.city = city;
        this.detail = detail;
        this.addressType = AddressType.HOME;
        this.isDefault = false;
        this.isActive = true;
        this.isDeleted = false;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    
    public AddressType getAddressType() {
        return addressType;
    }
    
    public void setAddressType(AddressType addressType) {
        this.addressType = addressType;
    }
    
    public String getProvince() {
        return province;
    }
    
    public void setProvince(String province) {
        this.province = province;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getDistrict() {
        return district;
    }
    
    public void setDistrict(String district) {
        this.district = district;
    }
    
    public String getDetail() {
        return detail;
    }
    
    public void setDetail(String detail) {
        this.detail = detail;
    }
    
    public String getPostalCode() {
        return postalCode;
    }
    
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    
    public String getContactName() {
        return contactName;
    }
    
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
    
    public String getContactPhone() {
        return contactPhone;
    }
    
    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }
    
    public Boolean getIsDefault() {
        return isDefault;
    }
    
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
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
    
    /**
     * 获取完整地址字符串
     */
    public String getFullAddress() {
        StringBuilder fullAddress = new StringBuilder();
        
        if (province != null) {
            fullAddress.append(province);
        }
        if (city != null) {
            fullAddress.append(city);
        }
        if (district != null) {
            fullAddress.append(district);
        }
        if (detail != null) {
            fullAddress.append(detail);
        }
        
        return fullAddress.toString();
    }
    
    @Override
    public String toString() {
        return "CustomerAddress{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", addressType=" + addressType +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", detail='" + detail + '\'' +
                ", isDefault=" + isDefault +
                '}';
    }
}