package com.yxrobot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * 客户订单数据传输对象
 * 与前端CustomerOrder接口保持一致
 * 用于客户订单列表和详情显示
 */
public class CustomerOrderDTO {
    
    private String id;                  // 订单ID
    
    @JsonProperty("orderNumber")
    private String orderNumber;         // 订单号
    
    @JsonProperty("type")
    private String type;                // 订单类型：sales, rental
    
    @JsonProperty("productName")
    private String productName;         // 产品名称
    
    @JsonProperty("productModel")
    private String productModel;        // 产品型号
    
    @JsonProperty("quantity")
    private Integer quantity;           // 数量
    
    @JsonProperty("amount")
    private BigDecimal amount;          // 订单金额
    
    @JsonProperty("status")
    private String status;              // 订单状态：pending, processing, completed, cancelled
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("createdAt")
    private String createdAt;           // 创建时间
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("updatedAt")
    private String updatedAt;           // 更新时间
    
    // 租赁订单特有字段
    @JsonProperty("rentalDays")
    private Integer rentalDays;         // 租赁天数
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("rentalStartDate")
    private String rentalStartDate;     // 租赁开始日期
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("rentalEndDate")
    private String rentalEndDate;       // 租赁结束日期
    
    @JsonProperty("notes")
    private String notes;               // 备注
    
    // 订单商品明细列表
    @JsonProperty("items")
    private java.util.List<OrderItemInfo> items; // 订单商品明细
    
    // 订单详细信息
    @JsonProperty("orderDetails")
    private OrderDetailsDTO orderDetails; // 订单详情
    
    // 内部类 - 订单商品明细
    public static class OrderItemInfo {
        @JsonProperty("id")
        private String id;                  // 商品明细ID
        
        @JsonProperty("productId")
        private String productId;           // 产品ID
        
        @JsonProperty("productName")
        private String productName;         // 产品名称
        
        @JsonProperty("productModel")
        private String productModel;        // 产品型号
        
        @JsonProperty("quantity")
        private Integer quantity;           // 数量
        
        @JsonProperty("unitPrice")
        private BigDecimal unitPrice;       // 单价
        
        @JsonProperty("totalPrice")
        private BigDecimal totalPrice;      // 总价
        
        // Getter和Setter
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public String getProductModel() { return productModel; }
        public void setProductModel(String productModel) { this.productModel = productModel; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
        public BigDecimal getTotalPrice() { return totalPrice; }
        public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    }
    
    // 内部类 - 订单详情
    public static class OrderDetailsDTO {
        @JsonProperty("totalAmount")
        private BigDecimal totalAmount;     // 订单总金额
        
        @JsonProperty("discountAmount")
        private BigDecimal discountAmount;  // 折扣金额
        
        @JsonProperty("finalAmount")
        private BigDecimal finalAmount;     // 最终金额
        
        @JsonProperty("paymentMethod")
        private String paymentMethod;       // 支付方式
        
        @JsonProperty("paymentStatus")
        private String paymentStatus;       // 支付状态
        
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonProperty("paymentTime")
        private String paymentTime;         // 支付时间
        
        @JsonProperty("deliveryAddress")
        private String deliveryAddress;     // 收货地址
        
        @JsonProperty("deliveryStatus")
        private String deliveryStatus;      // 配送状态
        
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonProperty("deliveryTime")
        private String deliveryTime;        // 配送时间
        
        // Getter和Setter
        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
        public BigDecimal getDiscountAmount() { return discountAmount; }
        public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
        public BigDecimal getFinalAmount() { return finalAmount; }
        public void setFinalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; }
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
        public String getPaymentStatus() { return paymentStatus; }
        public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
        public String getPaymentTime() { return paymentTime; }
        public void setPaymentTime(String paymentTime) { this.paymentTime = paymentTime; }
        public String getDeliveryAddress() { return deliveryAddress; }
        public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
        public String getDeliveryStatus() { return deliveryStatus; }
        public void setDeliveryStatus(String deliveryStatus) { this.deliveryStatus = deliveryStatus; }
        public String getDeliveryTime() { return deliveryTime; }
        public void setDeliveryTime(String deliveryTime) { this.deliveryTime = deliveryTime; }
    }
    
    // 构造函数
    public CustomerOrderDTO() {}
    
    // Getter和Setter方法
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getOrderNumber() {
        return orderNumber;
    }
    
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
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
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Integer getRentalDays() {
        return rentalDays;
    }
    
    public void setRentalDays(Integer rentalDays) {
        this.rentalDays = rentalDays;
    }
    
    public String getRentalStartDate() {
        return rentalStartDate;
    }
    
    public void setRentalStartDate(String rentalStartDate) {
        this.rentalStartDate = rentalStartDate;
    }
    
    public String getRentalEndDate() {
        return rentalEndDate;
    }
    
    public void setRentalEndDate(String rentalEndDate) {
        this.rentalEndDate = rentalEndDate;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public java.util.List<OrderItemInfo> getItems() {
        return items;
    }
    
    public void setItems(java.util.List<OrderItemInfo> items) {
        this.items = items;
    }
    
    public OrderDetailsDTO getOrderDetails() {
        return orderDetails;
    }
    
    public void setOrderDetails(OrderDetailsDTO orderDetails) {
        this.orderDetails = orderDetails;
    }
}