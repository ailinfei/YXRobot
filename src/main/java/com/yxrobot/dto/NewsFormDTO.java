package com.yxrobot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yxrobot.entity.NewsStatus;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 新闻表单数据传输对象
 * 用于新闻创建和编辑表单
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
public class NewsFormDTO {
    
    /**
     * 新闻ID（编辑时使用）
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
     * 是否推荐
     */
    private Boolean isFeatured;
    
    /**
     * 排序权重
     */
    private Integer sortOrder;
    
    /**
     * 标签ID列表
     */
    private List<Long> tagIds;
    
    // 构造函数
    public NewsFormDTO() {
        this.status = NewsStatus.DRAFT;
        this.isFeatured = false;
        this.sortOrder = 0;
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
    
    public List<Long> getTagIds() {
        return tagIds;
    }
    
    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }
}