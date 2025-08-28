package com.yxrobot.entity;

import java.time.LocalDateTime;

/**
 * 产品媒体实体类
 * 对应数据库表 product_media
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-19
 */
public class ProductMedia {
    
    /**
     * 媒体ID，主键
     */
    private Long id;
    
    /**
     * 产品ID，外键关联products表
     */
    private Long productId;
    
    /**
     * 媒体类型：image-图片，video-视频
     */
    private String mediaType;
    
    /**
     * 媒体文件访问URL
     */
    private String mediaUrl;
    
    /**
     * 排序值，用于控制媒体显示顺序
     */
    private Integer sortOrder;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 是否已删除：0-未删除，1-已删除
     */
    private Integer isDeleted;
    
    // 构造方法
    public ProductMedia() {}
    
    public ProductMedia(Long productId, String mediaType, String mediaUrl, Integer sortOrder) {
        this.productId = productId;
        this.mediaType = mediaType;
        this.mediaUrl = mediaUrl;
        this.sortOrder = sortOrder;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isDeleted = 0;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public String getMediaType() {
        return mediaType;
    }
    
    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }
    
    public String getMediaUrl() {
        return mediaUrl;
    }
    
    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
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
    
    public Integer getIsDeleted() {
        return isDeleted;
    }
    
    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }
    
    @Override
    public String toString() {
        return "ProductMedia{" +
                "id=" + id +
                ", productId=" + productId +
                ", mediaType='" + mediaType + '\'' +
                ", mediaUrl='" + mediaUrl + '\'' +
                ", sortOrder=" + sortOrder +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", isDeleted=" + isDeleted +
                '}';
    }
}