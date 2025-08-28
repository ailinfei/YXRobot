package com.yxrobot.entity;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 产品实体类
 * 对应数据库表：products
 * 
 * 字段映射规范：
 * - 数据库字段使用snake_case命名
 * - Java属性使用camelCase命名
 * - 通过MyBatis映射进行转换
 */
public class Product {
    
    /**
     * 产品ID，主键
     * 数据库字段：id
     */
    private Long id;
    
    /**
     * 产品名称
     * 数据库字段：name
     */
    @NotBlank(message = "产品名称不能为空")
    @Size(max = 200, message = "产品名称长度不能超过200字符")
    private String name;
    
    /**
     * 产品型号
     * 数据库字段：model
     */
    @NotBlank(message = "产品型号不能为空")
    @Size(max = 100, message = "产品型号长度不能超过100字符")
    private String model;
    
    /**
     * 产品描述
     * 数据库字段：description
     */
    private String description;
    
    /**
     * 价格
     * 数据库字段：price
     */
    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.00", message = "价格不能为负数")
    private BigDecimal price;
    
    /**
     * 封面图片URL
     * 数据库字段：cover_image_url
     */
    @Size(max = 500, message = "封面图片URL长度不能超过500字符")
    private String coverImageUrl;
    
    /**
     * 状态
     * 数据库字段：status
     */
    @Size(max = 20, message = "状态长度不能超过20字符")
    private String status;
    
    /**
     * 创建时间
     * 数据库字段：created_at
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     * 数据库字段：updated_at
     */
    private LocalDateTime updatedAt;
    
    /**
     * 是否删除
     * 数据库字段：is_deleted
     */
    private Boolean isDeleted;
    
    // 默认构造函数
    public Product() {
        this.status = "draft";
        this.isDeleted = false;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public String getCoverImageUrl() {
        return coverImageUrl;
    }
    
    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
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
    
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", model='" + model + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", coverImageUrl='" + coverImageUrl + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", isDeleted=" + isDeleted +
                '}';
    }
}