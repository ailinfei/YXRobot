package com.yxrobot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 区域配置实体类
 * 用于存储不同地区和语言的配置信息
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-22
 */
public class RegionConfig {
    
    /**
     * 配置ID，主键
     */
    private Long id;
    
    /**
     * 地区名称
     */
    @NotBlank(message = "地区名称不能为空")
    @Size(max = 50, message = "地区名称长度不能超过50字符")
    private String region;
    
    /**
     * 国家名称
     */
    @NotBlank(message = "国家名称不能为空")
    @Size(max = 50, message = "国家名称长度不能超过50字符")
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
     * 是否激活
     */
    @NotNull(message = "激活状态不能为空")
    private Boolean isActive;
    
    /**
     * 排序顺序
     */
    @Min(value = 0, message = "排序顺序不能为负数")
    private Integer sortOrder;
    
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
    public RegionConfig() {
        this.isActive = true;
        this.sortOrder = 0;
    }
    
    // 带参构造函数
    public RegionConfig(String region, String country, String languageCode, String languageName) {
        this();
        this.region = region;
        this.country = country;
        this.languageCode = languageCode;
        this.languageName = languageName;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Integer getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
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
    
    /**
     * 获取完整的语言显示名称
     * @return 格式为"语言名称 (语言代码)"的字符串
     */
    public String getFullLanguageName() {
        return languageName + " (" + languageCode + ")";
    }
    
    /**
     * 获取完整的地区显示名称
     * @return 格式为"地区名称 - 国家名称"的字符串
     */
    public String getFullRegionName() {
        if (region.equals(country)) {
            return region;
        }
        return region + " - " + country;
    }
    
    @Override
    public String toString() {
        return "RegionConfig{" +
                "id=" + id +
                ", region='" + region + '\'' +
                ", country='" + country + '\'' +
                ", languageCode='" + languageCode + '\'' +
                ", languageName='" + languageName + '\'' +
                ", isActive=" + isActive +
                ", sortOrder=" + sortOrder +
                '}';
    }
}