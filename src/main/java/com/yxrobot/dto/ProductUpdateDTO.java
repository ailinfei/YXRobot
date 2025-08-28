package com.yxrobot.dto;

import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 产品更新DTO
 * 用于产品信息更新操作
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-19
 */
public class ProductUpdateDTO {
    
    /**
     * 产品名称
     */
    @Size(max = 200, message = "产品名称长度不能超过200个字符")
    private String name;
    
    /**
     * 产品型号
     */
    @Size(max = 100, message = "产品型号长度不能超过100个字符")
    private String model;
    
    /**
     * 产品描述
     */
    @Size(max = 5000, message = "产品描述长度不能超过5000个字符")
    private String description;
    
    /**
     * 产品价格
     */
    @DecimalMin(value = "0.0", inclusive = true, message = "产品价格必须大于等于0")
    @Digits(integer = 10, fraction = 2, message = "价格格式不正确")
    private BigDecimal price;
    
    /**
     * 产品状态
     * 可选值: draft(草稿), published(已发布), archived(已归档)
     */
    @Pattern(regexp = "^(draft|published|archived)$", message = "产品状态只能是 draft、published 或 archived")
    private String status;
    
    // 默认构造函数
    public ProductUpdateDTO() {
    }
    
    // 带参数构造函数
    public ProductUpdateDTO(String name, String model, String description, BigDecimal price, String status) {
        this.name = name;
        this.model = model;
        this.description = description;
        this.price = price;
        this.status = status;
    }
    
    // Getter 和 Setter 方法
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
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "ProductUpdateDTO{" +
                "name='" + name + '\'' +
                ", model='" + model + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", status='" + status + '\'' +
                '}';
    }
}