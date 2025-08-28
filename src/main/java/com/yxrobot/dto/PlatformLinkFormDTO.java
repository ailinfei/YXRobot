package com.yxrobot.dto;

import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 平台链接表单数据传输对象
 * 用于创建和更新平台链接的表单数据传输
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-22
 */
public class PlatformLinkFormDTO {
    
    /**
     * 平台名称
     */
    @NotBlank(message = "平台名称不能为空")
    @Size(max = 100, message = "平台名称长度不能超过100字符")
    private String platformName;
    
    /**
     * 平台类型
     */
    @NotBlank(message = "平台类型不能为空")
    private String platformType;
    
    /**
     * 链接地址
     */
    @NotBlank(message = "链接地址不能为空")
    @URL(message = "链接地址格式不正确")
    @Size(max = 500, message = "链接地址长度不能超过500字符")
    private String linkUrl;
    
    /**
     * 地区
     */
    @NotBlank(message = "地区不能为空")
    @Size(max = 50, message = "地区长度不能超过50字符")
    private String region;
    
    /**
     * 国家
     */
    @NotBlank(message = "国家不能为空")
    @Size(max = 50, message = "国家长度不能超过50字符")
    private String country;
    
    /**
     * 语言代码
     */
    @NotBlank(message = "语言代码不能为空")
    @Size(max = 10, message = "语言代码长度不能超过10字符")
    private String languageCode;
    
    /**
     * 是否启用
     */
    @NotNull(message = "启用状态不能为空")
    private Boolean isEnabled;
    
    // 默认构造函数
    public PlatformLinkFormDTO() {
        this.isEnabled = true;
    }
    
    // Getters and Setters
    public String getPlatformName() {
        return platformName;
    }
    
    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }
    
    public String getPlatformType() {
        return platformType;
    }
    
    public void setPlatformType(String platformType) {
        this.platformType = platformType;
    }
    
    public String getLinkUrl() {
        return linkUrl;
    }
    
    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }
    
    public String getRegion() {
        return region;
    }
    
    public void setRegion(String region) {
        this.region = region;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getLanguageCode() {
        return languageCode;
    }
    
    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }
    
    public Boolean getIsEnabled() {
        return isEnabled;
    }
    
    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
    
    @Override
    public String toString() {
        return "PlatformLinkFormDTO{" +
                "platformName='" + platformName + '\'' +
                ", platformType='" + platformType + '\'' +
                ", linkUrl='" + linkUrl + '\'' +
                ", region='" + region + '\'' +
                ", country='" + country + '\'' +
                ", languageCode='" + languageCode + '\'' +
                ", isEnabled=" + isEnabled +
                '}';
    }
}