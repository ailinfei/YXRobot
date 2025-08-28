package com.yxrobot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 新闻实体类
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
public class News {
    
    /**
     * 新闻ID，主键
     */
    private Long id;
    
    /**
     * 新闻标题
     */
    @NotBlank(message = "新闻标题不能为空")
    @Size(min = 5, max = 200, message = "新闻标题长度应在5-200个字符之间")
    private String title;
    
    /**
     * 新闻摘要
     */
    @Size(min = 10, max = 500, message = "新闻摘要长度应在10-500个字符之间")
    private String excerpt;
    
    /**
     * 新闻内容（HTML格式）
     */
    @NotBlank(message = "新闻内容不能为空")
    @Size(min = 50, message = "新闻内容长度不能少于50个字符")
    private String content;
    
    /**
     * 分类ID
     */
    @NotNull(message = "新闻分类不能为空")
    private Long categoryId;
    
    /**
     * 作者
     */
    @NotBlank(message = "作者不能为空")
    @Size(max = 100, message = "作者姓名不能超过100个字符")
    private String author;
    
    /**
     * 状态
     */
    private NewsStatus status;
    
    /**
     * 封面图片URL
     */
    @Size(max = 500, message = "封面图片URL长度不能超过500个字符")
    private String coverImage;
    
    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;
    
    /**
     * 浏览量
     */
    private Integer views;
    
    /**
     * 评论数
     */
    private Integer comments;
    
    /**
     * 点赞数
     */
    private Integer likes;
    
    /**
     * 是否推荐
     */
    private Boolean isFeatured;
    
    /**
     * 排序权重
     */
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
    
    /**
     * 是否删除
     */
    private Boolean isDeleted;
    
    /**
     * 关联的分类信息（非数据库字段）
     */
    private NewsCategory category;
    
    /**
     * 关联的标签列表（非数据库字段）
     */
    private List<NewsTag> tags;
    
    // 构造函数
    public News() {
        this.status = NewsStatus.DRAFT;
        this.views = 0;
        this.comments = 0;
        this.likes = 0;
        this.isFeatured = false;
        this.sortOrder = 0;
        this.isDeleted = false;
    }
    
    public News(String title, String content, Long categoryId, String author) {
        this();
        this.title = title;
        this.content = content;
        this.categoryId = categoryId;
        this.author = author;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getExcerpt() {
        return excerpt;
    }
    
    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Long getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public NewsStatus getStatus() {
        return status;
    }
    
    public void setStatus(NewsStatus status) {
        this.status = status;
    }
    
    public String getCoverImage() {
        return coverImage;
    }
    
    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }
    
    public LocalDateTime getPublishTime() {
        return publishTime;
    }
    
    public void setPublishTime(LocalDateTime publishTime) {
        this.publishTime = publishTime;
    }
    
    public Integer getViews() {
        return views;
    }
    
    public void setViews(Integer views) {
        this.views = views;
    }
    
    public Integer getComments() {
        return comments;
    }
    
    public void setComments(Integer comments) {
        this.comments = comments;
    }
    
    public Integer getLikes() {
        return likes;
    }
    
    public void setLikes(Integer likes) {
        this.likes = likes;
    }
    
    public Boolean getIsFeatured() {
        return isFeatured;
    }
    
    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
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
    
    public Boolean getIsDeleted() {
        return isDeleted;
    }
    
    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    
    public NewsCategory getCategory() {
        return category;
    }
    
    public void setCategory(NewsCategory category) {
        this.category = category;
    }
    
    public List<NewsTag> getTags() {
        return tags;
    }
    
    public void setTags(List<NewsTag> tags) {
        this.tags = tags;
    }
    
    // 业务方法
    
    /**
     * 增加浏览量
     */
    public void incrementViews() {
        this.views = (this.views == null ? 0 : this.views) + 1;
    }
    
    /**
     * 增加评论数
     */
    public void incrementComments() {
        this.comments = (this.comments == null ? 0 : this.comments) + 1;
    }
    
    /**
     * 增加点赞数
     */
    public void incrementLikes() {
        this.likes = (this.likes == null ? 0 : this.likes) + 1;
    }
    
    /**
     * 减少点赞数
     */
    public void decrementLikes() {
        this.likes = Math.max(0, (this.likes == null ? 0 : this.likes) - 1);
    }
    
    /**
     * 发布新闻
     */
    public void publish() {
        if (this.status != null && this.status.canTransitionTo(NewsStatus.PUBLISHED)) {
            this.status = NewsStatus.PUBLISHED;
            if (this.publishTime == null) {
                this.publishTime = LocalDateTime.now();
            }
        } else {
            throw new IllegalStateException("当前状态不允许发布: " + this.status);
        }
    }
    
    /**
     * 下线新闻
     */
    public void offline() {
        if (this.status != null && this.status.canTransitionTo(NewsStatus.OFFLINE)) {
            this.status = NewsStatus.OFFLINE;
        } else {
            throw new IllegalStateException("当前状态不允许下线: " + this.status);
        }
    }
    
    /**
     * 检查是否已发布
     */
    public boolean isPublished() {
        return NewsStatus.PUBLISHED.equals(this.status);
    }
    
    /**
     * 检查是否为草稿
     */
    public boolean isDraft() {
        return NewsStatus.DRAFT.equals(this.status);
    }
    
    /**
     * 检查是否已下线
     */
    public boolean isOffline() {
        return NewsStatus.OFFLINE.equals(this.status);
    }
    
    @Override
    public String toString() {
        return "News{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", excerpt='" + excerpt + '\'' +
                ", categoryId=" + categoryId +
                ", author='" + author + '\'' +
                ", status=" + status +
                ", publishTime=" + publishTime +
                ", views=" + views +
                ", comments=" + comments +
                ", likes=" + likes +
                ", isFeatured=" + isFeatured +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", isDeleted=" + isDeleted +
                '}';
    }
}