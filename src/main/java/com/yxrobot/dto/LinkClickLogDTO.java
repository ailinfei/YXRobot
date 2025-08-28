package com.yxrobot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yxrobot.entity.LinkClickLog;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 链接点击日志数据传输对象
 * 用于返回链接点击日志信息给前端
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-22
 */
public class LinkClickLogDTO {
    
    /**
     * 日志ID
     */
    private Long id;
    
    /**
     * 链接ID
     */
    private Long linkId;
    
    /**
     * 用户IP地址
     */
    private String userIp;
    
    /**
     * 用户代理
     */
    private String userAgent;
    
    /**
     * 来源页面
     */
    private String referer;
    
    /**
     * 点击时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime clickedAt;
    
    /**
     * 是否转化
     */
    private Boolean isConversion;
    
    /**
     * 转化类型
     */
    private String conversionType;
    
    /**
     * 转化价值
     */
    private BigDecimal conversionValue;
    
    /**
     * 设备类型
     */
    private String deviceType;
    
    /**
     * 浏览器类型
     */
    private String browserType;
    
    // 默认构造函数
    public LinkClickLogDTO() {
    }
    
    // 从实体类转换的构造函数
    public LinkClickLogDTO(LinkClickLog entity) {
        this.id = entity.getId();
        this.linkId = entity.getLinkId();
        this.userIp = entity.getUserIp();
        this.userAgent = entity.getUserAgent();
        this.referer = entity.getReferer();
        this.clickedAt = entity.getClickedAt();
        this.isConversion = entity.getIsConversion();
        this.conversionType = entity.getConversionType();
        this.conversionValue = entity.getConversionValue();
        this.deviceType = entity.getDeviceType();
        this.browserType = entity.getBrowserType();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getLinkId() {
        return linkId;
    }
    
    public void setLinkId(Long linkId) {
        this.linkId = linkId;
    }
    
    public String getUserIp() {
        return userIp;
    }
    
    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    public String getReferer() {
        return referer;
    }
    
    public void setReferer(String referer) {
        this.referer = referer;
    }
    
    public LocalDateTime getClickedAt() {
        return clickedAt;
    }
    
    public void setClickedAt(LocalDateTime clickedAt) {
        this.clickedAt = clickedAt;
    }
    
    public Boolean getIsConversion() {
        return isConversion;
    }
    
    public void setIsConversion(Boolean isConversion) {
        this.isConversion = isConversion;
    }
    
    public String getConversionType() {
        return conversionType;
    }
    
    public void setConversionType(String conversionType) {
        this.conversionType = conversionType;
    }
    
    public BigDecimal getConversionValue() {
        return conversionValue;
    }
    
    public void setConversionValue(BigDecimal conversionValue) {
        this.conversionValue = conversionValue;
    }
    
    public String getDeviceType() {
        return deviceType;
    }
    
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
    
    public String getBrowserType() {
        return browserType;
    }
    
    public void setBrowserType(String browserType) {
        this.browserType = browserType;
    }
    
    /**
     * 获取脱敏的IP地址
     * @return 脱敏后的IP地址
     */
    public String getMaskedIp() {
        if (userIp == null || userIp.isEmpty()) {
            return "未知";
        }
        
        // IPv4地址脱敏
        if (userIp.contains(".")) {
            String[] parts = userIp.split("\\.");
            if (parts.length == 4) {
                return parts[0] + "." + parts[1] + ".***." + parts[3];
            }
        }
        
        // IPv6地址脱敏
        if (userIp.contains(":")) {
            String[] parts = userIp.split(":");
            if (parts.length >= 4) {
                return parts[0] + ":" + parts[1] + ":***:" + parts[parts.length - 1];
            }
        }
        
        return "***";
    }
    
    @Override
    public String toString() {
        return "LinkClickLogDTO{" +
                "id=" + id +
                ", linkId=" + linkId +
                ", userIp='" + getMaskedIp() + '\'' +
                ", clickedAt=" + clickedAt +
                ", isConversion=" + isConversion +
                ", deviceType='" + deviceType + '\'' +
                ", browserType='" + browserType + '\'' +
                '}';
    }
}