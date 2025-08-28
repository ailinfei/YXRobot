package com.yxrobot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 链接点击日志实体类
 * 用于记录平台链接的点击和转化数据
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-22
 */
public class LinkClickLog {
    
    /**
     * 日志ID，主键
     */
    private Long id;
    
    /**
     * 链接ID
     */
    @NotNull(message = "链接ID不能为空")
    private Long linkId;
    
    /**
     * 用户IP地址
     */
    @Size(max = 45, message = "IP地址长度不能超过45字符")
    private String userIp;
    
    /**
     * 用户代理
     */
    private String userAgent;
    
    /**
     * 来源页面
     */
    @Size(max = 500, message = "来源页面长度不能超过500字符")
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
    @Size(max = 50, message = "转化类型长度不能超过50字符")
    private String conversionType;
    
    /**
     * 转化价值
     */
    @DecimalMin(value = "0.0", message = "转化价值不能为负数")
    private BigDecimal conversionValue;
    
    // 默认构造函数
    public LinkClickLog() {
        this.clickedAt = LocalDateTime.now();
        this.isConversion = false;
    }
    
    // 带参构造函数
    public LinkClickLog(Long linkId, String userIp, String userAgent, String referer) {
        this();
        this.linkId = linkId;
        this.userIp = userIp;
        this.userAgent = userAgent;
        this.referer = referer;
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
    
    /**
     * 设置转化信息
     * @param conversionType 转化类型
     * @param conversionValue 转化价值
     */
    public void setConversion(String conversionType, BigDecimal conversionValue) {
        this.isConversion = true;
        this.conversionType = conversionType;
        this.conversionValue = conversionValue;
    }
    
    /**
     * 获取用户设备类型（从User-Agent中解析）
     * @return 设备类型
     */
    public String getDeviceType() {
        if (userAgent == null) {
            return "未知";
        }
        
        String ua = userAgent.toLowerCase();
        if (ua.contains("mobile") || ua.contains("android") || ua.contains("iphone")) {
            return "移动设备";
        } else if (ua.contains("tablet") || ua.contains("ipad")) {
            return "平板设备";
        } else {
            return "桌面设备";
        }
    }
    
    /**
     * 获取浏览器类型（从User-Agent中解析）
     * @return 浏览器类型
     */
    public String getBrowserType() {
        if (userAgent == null) {
            return "未知";
        }
        
        String ua = userAgent.toLowerCase();
        if (ua.contains("chrome")) {
            return "Chrome";
        } else if (ua.contains("firefox")) {
            return "Firefox";
        } else if (ua.contains("safari")) {
            return "Safari";
        } else if (ua.contains("edge")) {
            return "Edge";
        } else {
            return "其他";
        }
    }
    
    @Override
    public String toString() {
        return "LinkClickLog{" +
                "id=" + id +
                ", linkId=" + linkId +
                ", userIp='" + userIp + '\'' +
                ", clickedAt=" + clickedAt +
                ", isConversion=" + isConversion +
                ", conversionType='" + conversionType + '\'' +
                ", conversionValue=" + conversionValue +
                '}';
    }
}