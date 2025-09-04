package com.yxrobot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Positive;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 订单创建数据传输对象
 * 用于前端创建订单请求
 * 
 * 字段映射规范：
 * - Java DTO：camelCase（如：orderNumber, totalAmount）
 * - 前端接口：camelCase（如：orderNumber, totalAmount）
 */
public class OrderCreateDTO {
    
    @JsonProperty("orderNumber")
    @NotNull(message = "订单号不能为空")
    @Size(max = 50, message = "订单号不能超过50个字符")
    private String orderNumber;         // 订单号
    
    @JsonProperty("type")
    @NotNull(message = "订单类型不能为空")
    private String type;                // 订单类型：sales, rental
    
    @JsonProperty("customerId")
    @NotNull(message = "客户ID不能为空")
    private Long customerId;            // 客户ID
    
    @JsonProperty("customerPhone")
    private String customerPhone;       // 客户电话
    
    @JsonProperty("customerEmail")
    private String customerEmail;       // 客户邮箱
    
    @JsonProperty("deliveryAddress")
    @NotNull(message = "配送地址不能为空")
    private String deliveryAddress;     // 配送地址
    
    @JsonProperty("subtotal")
    @NotNull(message = "小计金额不能为空")
    @Positive(message = "小计金额必须大于0")
    private BigDecimal subtotal;        // 小计金额
    
    @JsonProperty("shippingFee")
    private BigDecimal shippingFee;     // 运费
    
    @JsonProperty("discount")
    private BigDecimal discount;        // 折扣金额
    
    @JsonProperty("totalAmount")
    @NotNull(message = "订单总金额不能为空")
    @Positive(message = "订单总金额必须大于0")
    private BigDecimal totalAmount;     // 订单总金额
    
    @JsonProperty("currency")
    private String currency = "CNY";    // 货币类型，默认人民币
    
    @JsonProperty("paymentMethod")
    private String paymentMethod;       // 支付方式
    
    @JsonProperty("expectedDeliveryDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expectedDeliveryDate; // 预期交付日期
    
    @JsonProperty("salesPerson")
    private String salesPerson;         // 销售人员
    
    @JsonProperty("notes")
    private String notes;               // 订单备注
    
    // 租赁相关字段（仅租赁订单使用）
    @JsonProperty("rentalStartDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate rentalStartDate;  // 租赁开始日期
    
    @JsonProperty("rentalEndDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate rentalEndDate;    // 租赁结束日期
    
    @JsonProperty("rentalDays")
    private Integer rentalDays;         // 租赁天数
    
    @JsonProperty("rentalNotes")
    private String rentalNotes;         // 租赁备注
    
    @JsonProperty("createdBy")
    private String createdBy;           // 创建人
    
    // 订单商品明细
    @JsonProperty("orderItems")
    @NotNull(message = "订单商品明细不能为空")
    @Valid
    private List<OrderItemCreateDTO> orderItems; // 订单商品明细
    
    // 内部类 - 订单商品明细创建DTO
    public static class OrderItemCreateDTO {
        @JsonProperty("productId")
        @NotNull(message = "产品ID不能为空")
        private Long productId;             // 产品ID
        
        @JsonProperty("productName")
        @NotNull(message = "产品名称不能为空")
        @Size(max = 200, message = "产品名称不能超过200个字符")
        private String productName;         // 产品名称
        
        @JsonProperty("productModel")
        @Size(max = 100, message = "产品型号不能超过100个字符")
        private String productModel;        // 产品型号
        
        @JsonProperty("quantity")
        @NotNull(message = "商品数量不能为空")
        @Positive(message = "商品数量必须大于0")
        private Integer quantity;           // 数量
        
        @JsonProperty("unitPrice")
        @NotNull(message = "商品单价不能为空")
        @Positive(message = "商品单价必须大于0")
        private BigDecimal unitPrice;       // 单价
        
        @JsonProperty("totalPrice")
        @NotNull(message = "商品总价不能为空")
        private BigDecimal totalPrice;      // 小计金额
        
        // Getter和Setter方法
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
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
    
    // 构造函数
    public OrderCreateDTO() {}
    
    // Getter和Setter方法
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
    
    public Long getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    
    public String getCustomerPhone() {
        return customerPhone;
    }
    
    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }
    
    public String getCustomerEmail() {
        return customerEmail;
    }
    
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
    
    public String getDeliveryAddress() {
        return deliveryAddress;
    }
    
    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
    public BigDecimal getShippingFee() {
        return shippingFee;
    }
    
    public void setShippingFee(BigDecimal shippingFee) {
        this.shippingFee = shippingFee;
    }
    
    public BigDecimal getDiscount() {
        return discount;
    }
    
    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public LocalDate getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }
    
    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }
    
    public String getSalesPerson() {
        return salesPerson;
    }
    
    public void setSalesPerson(String salesPerson) {
        this.salesPerson = salesPerson;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public LocalDate getRentalStartDate() {
        return rentalStartDate;
    }
    
    public void setRentalStartDate(LocalDate rentalStartDate) {
        this.rentalStartDate = rentalStartDate;
    }
    
    public LocalDate getRentalEndDate() {
        return rentalEndDate;
    }
    
    public void setRentalEndDate(LocalDate rentalEndDate) {
        this.rentalEndDate = rentalEndDate;
    }
    
    public Integer getRentalDays() {
        return rentalDays;
    }
    
    public void setRentalDays(Integer rentalDays) {
        this.rentalDays = rentalDays;
    }
    
    public String getRentalNotes() {
        return rentalNotes;
    }
    
    public void setRentalNotes(String rentalNotes) {
        this.rentalNotes = rentalNotes;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public List<OrderItemCreateDTO> getOrderItems() {
        return orderItems;
    }
    
    public void setOrderItems(List<OrderItemCreateDTO> orderItems) {
        this.orderItems = orderItems;
    }
}