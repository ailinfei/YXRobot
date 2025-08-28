package com.yxrobot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yxrobot.entity.PlatformLink;

import java.time.LocalDateTime;

/**
 * 平台链接数据传输对象
 * 用于前后端数据传输
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-22
 */
public class PlatformLinkDTO {
    
    /**
     * 链接ID
     */
    private Long id;
    
    /**
     * 平台名称
     */
    private String platformName;
    
    /**
     * 平台类型
     */
    private String platformType;
    
    /**
     * 链接地址
     */
    private String linkUrl;
    
    /**
     * 地区
     */
    private String region;
    
    /**
     * 国家
     */
    private String country;
    
    /**
     * 语言代码
     */
    private String languageCode;
    
    /**
     * 语言名称
     */
    private String languageName;
    
    /**
     * 是否启用
     */
    private Boolean isEnabled;
    
    /**
     * 链接状态
     */
    private String linkStatus;
    
    /**
     * 最后检查时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastCheckedAt;
    
    /**
     * 点击量
     */
    private Integer clickCount;
    
    /**
     * 转化量
     */
    private Integer conversionCount;
    
    /**
     * 转化率
     */
    private Double conversionRate;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // 默认构造函数
    public PlatformLinkDTO() {
    }
    
    // 从实体类转换的构造函数
    public PlatformLinkDTO(PlatformLink entity) {
        this.id = entity.getId();
        this.platformName = entity.getPlatformName();
        this.platformType = entity.getPlatformType() != null ? entity.getPlatformType().getCode() : null;
        this.linkUrl = entity.getLinkUrl();
        this.region = entity.getRegion();
        this.country = entity.getCountry();
        this.languageCode = entity.getLanguageCode();
        this.languageName = entity.getLanguageName();
        this.isEnabled = entity.getIsEnabled();
        this.linkStatus = entity.getLinkStatus() != null ? entity.getLinkStatus().getCode() : null;
        this.lastCheckedAt = entity.getLastCheckedAt();
        this.clickCount = entity.getClickCount();
        this.conversionCount = entity.getConversionCount();
        this.conversionRate = entity.getConversionRate();
        this.createdAt = entity.getCreatedAt();
        this.updatedAt = entity.getUpdatedAt();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public String getLanguageName() {
        return languageName;
    }
    
    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }
    
    public Boolean getIsEnabled() {
        return isEnabled;
    }
    
    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
    
    public String getLinkStatus() {
        return linkStatus;
    }
    
    public void setLinkStatus(String linkStatus) {
        this.linkStatus = linkStatus;
    }
    
    public LocalDateTime getLastCheckedAt() {
        return lastCheckedAt;
    }
    
    public void setLastCheckedAt(LocalDateTime lastCheckedAt) {
        this.lastCheckedAt = lastCheckedAt;
    }
    
    public Integer getClickCount() {
        return clickCount;
    }
    
    public void setClickCount(Integer clickCount) {
        this.clickCount = clickCount;
    }
    
    public Integer getConversionCount() {
        return conversionCount;
    }
    
    public void setConversionCount(Integer conversionCount) {
        this.conversionCount = conversionCount;
    }
    
    public Double getConversionRate() {
        return conversionRate;
    }
    
    public void setConversionRate(Double conversionRate) {
        this.conversionRate = conversionRate;
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
}