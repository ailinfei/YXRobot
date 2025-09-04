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
 * 
 * 字段映射规范：
 * - 数据库字段：snake_case（如：product_name, unit_price）
 * - Java实体类：camelCase（如：productName, unitPrice）
 * - 前端接口：camelCase（如：productName, unitPrice）
 */
public class OrderItem {
    
    private Long id;
    
    @NotNull(message = "订单ID不能为空")
    private Long orderId;               // 订单ID
    
    @NotNull(message = "产品ID不能为空")
    private Long productId;             // 产品ID（引用products表）
    
    @NotNull(message = "产品名称不能为空")
    @Size(max = 200, message = "产品名称不能超过200个字符")
    private String productName;         // 产品名称（冗余字段，避免关联查询）
    
    @Size(max = 100, message = "产品型号不能超过100个字符")
    private String productModel;        // 产品型号
    
    @NotNull(message = "商品数量不能为空")
    @Positive(message = "商品数量必须大于0")
    private Integer quantity;           // 数量
    
    @NotNull(message = "商品单价不能为空")
    @Positive(message = "商品单价必须大于0")
    private BigDecimal unitPrice;       // 单价
    
    @NotNull(message = "商品总价不能为空")
    private BigDecimal totalPrice;      // 小计金额
    
    // 系统字段
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 关联数据（用于查询时填充）
    private Product product;            // 产品信息
    
    // 构造函数
    public OrderItem() {}
    
    public OrderItem(Long orderId, Long productId, String productName, Integer quantity, BigDecimal unitPrice) {
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
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
    
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
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
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        // 自动计算总价
        if (this.unitPrice != null) {
            this.totalPrice = this.unitPrice.multiply(BigDecimal.valueOf(quantity));
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
        }
    }
    
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
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
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
    
    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", totalPrice=" + totalPrice +
                '}';
    }
}