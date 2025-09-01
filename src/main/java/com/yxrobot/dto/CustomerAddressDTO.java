package com.yxrobot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 客户地址数据传输对象
 * 与前端Address接口保持一致
 * 用于客户地址信息的传输
 */
public class CustomerAddressDTO {
    
    private Long id;                    // 地址ID
    
    @JsonProperty("province")
    private String province;            // 省份
    
    @JsonProperty("city")
    private String city;                // 城市
    
    @JsonProperty("district")
    private String district;            // 区县
    
    @JsonProperty("detail")
    private String detail;              // 详细地址
    
    @JsonProperty("postalCode")
    private String postalCode;          // 邮政编码
    
    @JsonProperty("contactName")
    private String contactName;         // 联系人姓名
    
    @JsonProperty("contactPhone")
    private String contactPhone;        // 联系电话
    
    @JsonProperty("addressType")
    private String addressType;         // 地址类型：home, office, delivery, billing, other
    
    @JsonProperty("isDefault")
    private Boolean isDefault;          // 是否为默认地址
    
    @JsonProperty("isActive")
    private Boolean isActive;           // 是否启用
    
    // 构造函数
    public CustomerAddressDTO() {}
    
    public CustomerAddressDTO(String province, String city, String detail) {
        this.province = province;
        this.city = city;
        this.detail = detail;
        this.isDefault = false;
        this.isActive = true;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public String getAddressType() {
        return addressType;
    }
    
    public void setAddressType(String addressType) {
        this.addressType = addressType;
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
        return "CustomerAddressDTO{" +
                "id=" + id +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", detail='" + detail + '\'' +
                ", isDefault=" + isDefault +
                '}';
    }
}