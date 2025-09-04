package com.yxrobot.dto;

import com.yxrobot.entity.PaymentStatus;
import com.yxrobot.entity.SalesStatus;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 销售记录数据传输对象
 * 用于前后端数据传输
 * 
 * 字段映射规范：
 * - 与SalesRecord实体类保持一致的camelCase命名
 * - 用于API接口的请求和响应数据传输
 */
public class SalesRecordDTO {
    
    /**
     * 销售记录ID
     */
    private Long id;
    
    /**
     * 订单号
     */
    @NotBlank(message = "订单号不能为空")
    @Size(max = 50, message = "订单号长度不能超过50字符")
    private String orderNumber;
    
    /**
     * 客户ID
     */
    @NotNull(message = "客户ID不能为空")
    private Long customerId;
    
    /**
     * 客户名称（关联查询字段）
     */
    private String customerName;
    
    /**
     * 产品ID
     */
    @NotNull(message = "产品ID不能为空")
    private Long productId;
    
    /**
     * 产品名称（关联查询字段）
     */
    private String productName;
    
    /**
     * 销售人员ID
     */
    @NotNull(message = "销售人员ID不能为空")
    private Long salesStaffId;
    
    /**
     * 客户电话（关联查询字段）
     */
    private String customerPhone;
    
    /**
     * 销售人员姓名（关联查询字段）
     * 注意：前端使用 staffName，不是 salesStaffName
     */
    private String staffName;
    
    /**
     * 销售金额
     */
    @NotNull(message = "销售金额不能为空")
    @DecimalMin(value = "0.01", message = "销售金额必须大于0")
    @DecimalMax(value = "999999.99", message = "销售金额不能超过999999.99")
    private BigDecimal salesAmount;
    
    /**
     * 销售数量
     */
    @NotNull(message = "销售数量不能为空")
    @Min(value = 1, message = "销售数量必须大于0")
    @Max(value = 9999, message = "销售数量不能超过9999")
    private Integer quantity;
    
    /**
     * 单价
     */
    @NotNull(message = "单价不能为空")
    @DecimalMin(value = "0.01", message = "单价必须大于0")
    private BigDecimal unitPrice;
    
    /**
     * 折扣金额
     */
    @DecimalMin(value = "0", message = "折扣金额不能为负数")
    private BigDecimal discountAmount;
    
    /**
     * 订单日期
     */
    @NotNull(message = "订单日期不能为空")
    private LocalDate orderDate;
    
    /**
     * 交付日期
     */
    private LocalDate deliveryDate;
    
    /**
     * 订单状态
     */
    @NotNull(message = "订单状态不能为空")
    private SalesStatus status;
    
    /**
     * 付款状态
     */
    @NotNull(message = "付款状态不能为空")
    private PaymentStatus paymentStatus;
    
    /**
     * 付款方式
     */
    @Size(max = 50, message = "付款方式长度不能超过50字符")
    private String paymentMethod;
    
    /**
     * 销售地区
     */
    @Size(max = 100, message = "销售地区长度不能超过100字符")
    private String region;
    
    /**
     * 销售渠道
     */
    @Size(max = 50, message = "销售渠道长度不能超过50字符")
    private String channel;
    
    /**
     * 备注信息
     */
    private String notes;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    // 默认构造函数
    public SalesRecordDTO() {
        this.discountAmount = BigDecimal.ZERO;
        this.status = SalesStatus.PENDING;
        this.paymentStatus = PaymentStatus.PENDING;
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
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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
    
    public Long getSalesStaffId() {
        return salesStaffId;
    }
    
    public void setSalesStaffId(Long salesStaffId) {
        this.salesStaffId = salesStaffId;
    }
    
    public String getCustomerPhone() {
        return customerPhone;
    }
    
    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }
    
    public String getStaffName() {
        return staffName;
    }
    
    public void setStaffName(String staffName) {
        this.staffName = staffName;
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
    
    @Override
    public String toString() {
        return "SalesRecordDTO{" +
                "id=" + id +
                ", orderNumber='" + orderNumber + '\'' +
                ", customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", salesStaffId=" + salesStaffId +
                ", customerPhone='" + customerPhone + '\'' +
                ", staffName='" + staffName + '\'' +
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
                '}';
    }
}