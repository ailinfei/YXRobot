package com.yxrobot.entity;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 销售记录实体类
 * 对应数据库表：sales_records
 * 
 * 字段映射规范：
 * - 数据库字段使用snake_case命名
 * - Java属性使用camelCase命名
 * - 通过MyBatis映射进行转换
 */
public class SalesRecord {
    
    /**
     * 销售记录ID，主键
     * 数据库字段：id
     */
    private Long id;
    
    /**
     * 订单号
     * 数据库字段：order_number
     */
    @NotBlank(message = "订单号不能为空")
    @Size(max = 50, message = "订单号长度不能超过50字符")
    private String orderNumber;
    
    /**
     * 客户ID
     * 数据库字段：customer_id
     */
    @NotNull(message = "客户ID不能为空")
    private Long customerId;
    
    /**
     * 产品ID
     * 数据库字段：product_id
     */
    @NotNull(message = "产品ID不能为空")
    private Long productId;
    
    /**
     * 销售人员ID
     * 数据库字段：sales_staff_id
     */
    @NotNull(message = "销售人员ID不能为空")
    private Long salesStaffId;
    
    /**
     * 销售金额
     * 数据库字段：sales_amount
     */
    @NotNull(message = "销售金额不能为空")
    @DecimalMin(value = "0.01", message = "销售金额必须大于0")
    @DecimalMax(value = "999999.99", message = "销售金额不能超过999999.99")
    private BigDecimal salesAmount;
    
    /**
     * 销售数量
     * 数据库字段：quantity
     */
    @NotNull(message = "销售数量不能为空")
    @Min(value = 1, message = "销售数量必须大于0")
    @Max(value = 9999, message = "销售数量不能超过9999")
    private Integer quantity;
    
    /**
     * 单价
     * 数据库字段：unit_price
     */
    @NotNull(message = "单价不能为空")
    @DecimalMin(value = "0.01", message = "单价必须大于0")
    private BigDecimal unitPrice;
    
    /**
     * 折扣金额
     * 数据库字段：discount_amount
     */
    @DecimalMin(value = "0", message = "折扣金额不能为负数")
    private BigDecimal discountAmount;
    
    /**
     * 订单日期
     * 数据库字段：order_date
     */
    @NotNull(message = "订单日期不能为空")
    private LocalDate orderDate;
    
    /**
     * 交付日期
     * 数据库字段：delivery_date
     */
    private LocalDate deliveryDate;
    
    /**
     * 订单状态
     * 数据库字段：status
     */
    @NotNull(message = "订单状态不能为空")
    private SalesStatus status;
    
    /**
     * 付款状态
     * 数据库字段：payment_status
     */
    @NotNull(message = "付款状态不能为空")
    private PaymentStatus paymentStatus;
    
    /**
     * 付款方式
     * 数据库字段：payment_method
     */
    @Size(max = 50, message = "付款方式长度不能超过50字符")
    private String paymentMethod;
    
    /**
     * 销售地区
     * 数据库字段：region
     */
    @Size(max = 100, message = "销售地区长度不能超过100字符")
    private String region;
    
    /**
     * 销售渠道
     * 数据库字段：channel
     */
    @Size(max = 50, message = "销售渠道长度不能超过50字符")
    private String channel;
    
    /**
     * 备注信息
     * 数据库字段：notes
     */
    private String notes;
    
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
    public SalesRecord() {
        this.discountAmount = BigDecimal.ZERO;
        this.status = SalesStatus.PENDING;
        this.paymentStatus = PaymentStatus.PENDING;
        this.isDeleted = false;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getOrderNumber() {
        return orderNumber;
    }
    
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }
    
    public Long getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public Long getSalesStaffId() {
        return salesStaffId;
    }
    
    public void setSalesStaffId(Long salesStaffId) {
        this.salesStaffId = salesStaffId;
    }
    
    public BigDecimal getSalesAmount() {
        return salesAmount;
    }
    
    public void setSalesAmount(BigDecimal salesAmount) {
        this.salesAmount = salesAmount;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }
    
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
    
    public LocalDate getOrderDate() {
        return orderDate;
    }
    
    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }
    
    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }
    
    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
    
    public SalesStatus getStatus() {
        return status;
    }
    
    public void setStatus(SalesStatus status) {
        this.status = status;
    }
    
    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getRegion() {
        return region;
    }
    
    public void setRegion(String region) {
        this.region = region;
    }
    
    public String getChannel() {
        return channel;
    }
    
    public void setChannel(String channel) {
        this.channel = channel;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
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
        return "SalesRecord{" +
                "id=" + id +
                ", orderNumber='" + orderNumber + '\'' +
                ", customerId=" + customerId +
                ", productId=" + productId +
                ", salesStaffId=" + salesStaffId +
                ", salesAmount=" + salesAmount +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", discountAmount=" + discountAmount +
                ", orderDate=" + orderDate +
                ", deliveryDate=" + deliveryDate +
                ", status=" + status +
                ", paymentStatus=" + paymentStatus +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", region='" + region + '\'' +
                ", channel='" + channel + '\'' +
                ", notes='" + notes + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", isDeleted=" + isDeleted +
                '}';
    }
}