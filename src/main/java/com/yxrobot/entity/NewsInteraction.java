package com.yxrobot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 新闻互动数据实体类
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
public class NewsInteraction {
    
    /**
     * 互动ID，主键
     */
    private Long id;
    
    /**
     * 新闻ID
     */
    @NotNull(message = "新闻ID不能为空")
    private Long newsId;
    
    /**
     * 互动类型
     */
    @NotNull(message = "互动类型不能为空")
    private InteractionType interactionType;
    
    /**
     * 用户ID（可为空）
     */
    private Long userId;
    
    /**
     * IP地址
     */
    @Size(max = 45, message = "IP地址长度不能超过45个字符")
    private String ipAddress;
    
    /**
     * 用户代理
     */
    private String userAgent;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    // 构造函数
    public NewsInteraction() {
    }
    
    public NewsInteraction(Long newsId, InteractionType interactionType) {
        this.newsId = newsId;
        this.interactionType = interactionType;
    }
    
    public NewsInteraction(Long newsId, InteractionType interactionType, String ipAddress) {
        this.newsId = newsId;
        this.interactionType = interactionType;
        this.ipAddress = ipAddress;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getNewsId() {
        return newsId;
    }
    
    public void setNewsId(Long newsId) {
        this.newsId = newsId;
    }
    
    public InteractionType getInteractionType() {
        return interactionType;
    }
    
    public void setInteractionType(InteractionType interactionType) {
        this.interactionType = interactionType;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "NewsInteraction{" +
                "id=" + id +
                ", newsId=" + newsId +
                ", interactionType=" + interactionType +
                ", userId=" + userId +
                ", ipAddress='" + ipAddress + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}