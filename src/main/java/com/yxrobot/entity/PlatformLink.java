package com.yxrobot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * 平台链接实体类
 * 用于存储电商平台和租赁平台的链接信息
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-22
 */
public class PlatformLink {
    
    /**
     * 链接ID，主键
     */
    private Long id;
    
    /**
     * 平台名称
     */
    @NotBlank(message = "平台名称不能为空")
    @Size(max = 100, message = "平台名称长度不能超过100字符")
    private String platformName;
    
    /**
     * 平台类型
     */
    @NotNull(message = "平台类型不能为空")
    private PlatformType platformType;
    
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
     * 语言名称
     */
    @NotBlank(message = "语言名称不能为空")
    @Size(max = 50, message = "语言名称长度不能超过50字符")
    private String languageName;
    
    /**
     * 是否启用
     */
    @NotNull(message = "启用状态不能为空")
    private Boolean isEnabled;
    
    /**
     * 链接状态
     */
    private LinkStatus linkStatus;
    
    /**
     * 最后检查时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastCheckedAt;
    
    /**
     * 点击量
     */
    @Min(value = 0, message = "点击量不能为负数")
    private Integer clickCount;
    
    /**
     * 转化量
     */
    @Min(value = 0, message = "转化量不能为负数")
    private Integer conversionCount;
    
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
    
    /**
     * 是否删除
     */
    private Boolean isDeleted;
    
    /**
     * 平台类型枚举
     */
    public enum PlatformType {
        ECOMMERCE("ecommerce", "电商平台"),
        RENTAL("rental", "租赁平台");
        
        private final String code;
        private final String description;
        
        PlatformType(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
        
        /**
         * 根据代码获取枚举值
         */
        public static PlatformType fromCode(String code) {
            for (PlatformType type : values()) {
                if (type.code.equals(code)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("未知的平台类型代码: " + code);
        }
    }
    
    /**
     * 链接状态枚举
     */
    public enum LinkStatus {
        ACTIVE("active", "正常"),
        INACTIVE("inactive", "异常"),
        CHECKING("checking", "检查中"),
        ERROR("error", "错误");
        
        private final String code;
        private final String description;
        
        LinkStatus(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
        
        /**
         * 根据代码获取枚举值
         */
        public static LinkStatus fromCode(String code) {
            for (LinkStatus status : values()) {
                if (status.code.equals(code)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("未知的链接状态代码: " + code);
        }
    }
    
    // 默认构造函数
    public PlatformLink() {
        this.isEnabled = true;
        this.linkStatus = LinkStatus.ACTIVE;
        this.clickCount = 0;
        this.conversionCount = 0;
        this.isDeleted = false;
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
    
    public PlatformType getPlatformType() {
        return platformType;
    }
    
    public void setPlatformType(PlatformType platformType) {
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
    
    public LinkStatus getLinkStatus() {
        return linkStatus;
    }
    
    public void setLinkStatus(LinkStatus linkStatus) {
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
     * 计算转化率
     * @return 转化率（百分比）
     */
    public Double getConversionRate() {
        if (clickCount == null || clickCount == 0) {
            return 0.0;
        }
        if (conversionCount == null) {
            return 0.0;
        }
        return (conversionCount.doubleValue() / clickCount.doubleValue()) * 100;
    }
    
    @Override
    public String toString() {
        return "PlatformLink{" +
                "id=" + id +
                ", platformName='" + platformName + '\'' +
                ", platformType=" + platformType +
                ", linkUrl='" + linkUrl + '\'' +
                ", region='" + region + '\'' +
                ", country='" + country + '\'' +
                ", languageCode='" + languageCode + '\'' +
                ", languageName='" + languageName + '\'' +
                ", isEnabled=" + isEnabled +
                ", linkStatus=" + linkStatus +
                ", clickCount=" + clickCount +
                ", conversionCount=" + conversionCount +
                '}';
    }
}