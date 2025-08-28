package com.yxrobot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yxrobot.entity.NewsStatus;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 新闻数据传输对象
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
public class NewsDTO {
    
    /**
     * 新闻ID
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
     * 分类名称
     */
    private String categoryName;
    
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
     * 状态描述
     */
    private String statusText;
    
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
     * 关联的标签列表
     */
    private List<NewsTagDTO> tags;
    
    /**
     * 标签ID列表（用于表单提交）
     */
    private List<Long> tagIds;
    
    // 构造函数
    public NewsDTO() {
    }
    
    public NewsDTO(Long id, String title, String author, NewsStatus status) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.status = status;
        this.statusText = status != null ? status.getDescription() : null;
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
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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
        this.statusText = status != null ? status.getDescription() : null;
    }
    
    public String getStatusText() {
        return statusText;
    }
    
    public void setStatusText(String statusText) {
        this.statusText = statusText;
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
    
    public List<NewsTagDTO> getTags() {
        return tags;
    }
    
    public void setTags(List<NewsTagDTO> tags) {
        this.tags = tags;
    }
    
    public List<Long> getTagIds() {
        return tagIds;
    }
    
    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }
}