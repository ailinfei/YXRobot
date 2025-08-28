package com.yxrobot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 新闻标签关联实体类
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
public class NewsTagRelation {
    
    /**
     * 关联ID，主键
     */
    private Long id;
    
    /**
     * 新闻ID
     */
    @NotNull(message = "新闻ID不能为空")
    private Long newsId;
    
    /**
     * 标签ID
     */
    @NotNull(message = "标签ID不能为空")
    private Long tagId;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    // 构造函数
    public NewsTagRelation() {
    }
    
    public NewsTagRelation(Long newsId, Long tagId) {
        this.newsId = newsId;
        this.tagId = tagId;
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
    
    public Long getTagId() {
        return tagId;
    }
    
    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "NewsTagRelation{" +
                "id=" + id +
                ", newsId=" + newsId +
                ", tagId=" + tagId +
                ", createdAt=" + createdAt +
                '}';
    }
}