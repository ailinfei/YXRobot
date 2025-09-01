package com.yxrobot.entity;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单商品明细实体类
 * 对应order_items表
 * 记录订单中的具体商品信息
 */
public class OrderItem {
    
    private Long id;
    
    @NotNull(message = "订单ID不能为空")
    private Long orderId;               // 订单ID
    
    @NotNull(message = "商品名称不能为空")
    @Size(max = 100, message = "商品名称不能超过100个字符")
    private String productName;         // 商品名称
    
    @Size(max = 50, message = "商品型号不能超过50个字符")
    private String productModel;        // 商品型号
    
    @Size(max = 50, message = "商品SKU不能超过50个字符")
    private String productSku;          // 商品SKU
    
    @NotNull(message = "商品数量不能为空")
    @Positive(message = "商品数量必须大于0")
    private Integer quantity;           // 商品数量
    
    @NotNull(message = "商品单价不能为空")
    @Positive(message = "商品单价必须大于0")
    private BigDecimal unitPrice;       // 商品单价
    
    @NotNull(message = "商品总价不能为空")
    private BigDecimal totalPrice;      // 商品总价
    
    private BigDecimal discountAmount;  // 折扣金额
    private BigDecimal finalPrice;      // 最终价格
    
    @Size(max = 500, message = "商品描述不能超过500个字符")
    private String description;         // 商品描述
    
    @Size(max = 200, message = "备注不能超过200个字符")
    private String notes;               // 备注
    
    // 租赁相关字段（仅租赁订单使用）
    private Integer rentalDays;         // 租赁天数
    private BigDecimal dailyRentalFee;  // 日租金
    
    // 系统字段
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;
    
    // 构造函数
    public OrderItem() {}
    
    public OrderItem(Long orderId, String productName, Integer quantity, BigDecimal unitPrice) {
        this.orderId = orderId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        this.finalPrice = this.totalPrice;
        this.isDeleted = false;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public String getProductModel() {
        return productModel;
    }
    
    public void setProductModel(String productModel) {
        this.productModel = productModel;
    }
    
    public String getProductSku() {
        return productSku;
    }
    
    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        // 自动计算总价
        if (this.unitPrice != null) {
            this.totalPrice = this.unitPrice.multiply(BigDecimal.valueOf(quantity));
            if (this.discountAmount == null) {
                this.finalPrice = this.totalPrice;
            } else {
                this.finalPrice = this.totalPrice.subtract(this.discountAmount);
            }
        }
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        // 自动计算总价
        if (this.quantity != null) {
            this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(this.quantity));
            if (this.discountAmount == null) {
                this.finalPrice = this.totalPrice;
            } else {
                this.finalPrice = this.totalPrice.subtract(this.discountAmount);
            }
        }
    }
    
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }
    
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
        // 自动计算最终价格
        if (this.totalPrice != null) {
            this.finalPrice = this.totalPrice.subtract(discountAmount != null ? discountAmount : BigDecimal.ZERO);
        }
    }
    
    public BigDecimal getFinalPrice() {
        return finalPrice;
    }
    
    public void setFinalPrice(BigDecimal finalPrice) {
        this.finalPrice = finalPrice;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public Integer getRentalDays() {
        return rentalDays;
    }
    
    public void setRentalDays(Integer rentalDays) {
        this.rentalDays = rentalDays;
    }
    
    public BigDecimal getDailyRentalFee() {
        return dailyRentalFee;
    }
    
    public void setDailyRentalFee(BigDecimal dailyRentalFee) {
        this.dailyRentalFee = dailyRentalFee;
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
        return "OrderItem{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", productName='" + productName + '\'' +
                ", productModel='" + productModel + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", totalPrice=" + totalPrice +
                ", finalPrice=" + finalPrice +
                '}';
    }
}