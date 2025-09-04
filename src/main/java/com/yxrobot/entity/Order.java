package com.yxrobot.entity;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单信息实体类
 * 对应orders表 - 订单管理主表
 * 
 * 字段映射规范：
 * - 数据库字段：snake_case（如：order_number, total_amount）
 * - Java实体类：camelCase（如：orderNumber, totalAmount）
 * - 前端接口：camelCase（如：orderNumber, totalAmount）
 */
public class Order {
    
    private Long id;
    
    @NotNull(message = "订单号不能为空")
    @Size(max = 50, message = "订单号不能超过50个字符")
    private String orderNumber;         // 订单号
    
    @NotNull(message = "订单类型不能为空")
    private OrderType type;             // 订单类型：sales, rental (对应数据库type字段)
    
    private OrderStatus status;         // 订单状态：pending, confirmed, processing, shipped, delivered, completed, cancelled
    
    @NotNull(message = "客户ID不能为空")
    private Long customerId;            // 客户ID（引用customers表）
    
    private String customerName;        // 客户名称（冗余字段，用于查询显示）
    
    @NotNull(message = "配送地址不能为空")
    private String deliveryAddress;     // 配送地址
    
    @NotNull(message = "小计金额不能为空")
    @Positive(message = "小计金额必须大于0")
    private BigDecimal subtotal;        // 小计金额
    
    private BigDecimal shippingFee;     // 运费
    private BigDecimal discount;        // 折扣金额
    
    @NotNull(message = "订单总金额不能为空")
    @Positive(message = "订单总金额必须大于0")
    private BigDecimal totalAmount;     // 订单总金额
    
    private String currency;            // 货币类型
    private PaymentStatus paymentStatus; // 支付状态：pending, paid, failed, refunded
    private String paymentMethod;       // 支付方式
    private LocalDateTime paymentTime;  // 支付时间
    private LocalDate expectedDeliveryDate; // 预期交付日期
    private String salesPerson;         // 销售人员
    private String notes;               // 订单备注
    
    // 租赁相关字段（仅租赁订单使用）
    private LocalDate rentalStartDate;  // 租赁开始日期
    private LocalDate rentalEndDate;    // 租赁结束日期
    private Integer rentalDays;         // 租赁天数
    private String rentalNotes;         // 租赁备注
    
    // 系统字段
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;           // 创建人
    private Boolean isDeleted;
    
    // 关联数据（用于查询时填充）
    private List<OrderItem> orderItems; // 订单商品明细
    private Customer customer;          // 客户信息
    private ShippingInfo shippingInfo;  // 物流信息
    private List<OrderLog> orderLogs;   // 操作日志
    
    // 构造函数
    public Order() {}
    
    public Order(String orderNumber, OrderType type, Long customerId, String deliveryAddress, BigDecimal totalAmount) {
        this.orderNumber = orderNumber;
        this.type = type;
        this.customerId = customerId;
        this.deliveryAddress = deliveryAddress;
        this.totalAmount = totalAmount;
        this.status = OrderStatus.PENDING;
        this.paymentStatus = PaymentStatus.PENDING;
        this.currency = "CNY";
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
    
    public OrderType getType() {
        return type;
    }
    
    public void setType(OrderType type) {
        this.type = type;
    }
    
    public OrderStatus getStatus() {
        return status;
    }
    
    public void setStatus(OrderStatus status) {
        this.status = status;
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
    
    public LocalDateTime getPaymentTime() {
        return paymentTime;
    }
    
    public void setPaymentTime(LocalDateTime paymentTime) {
        this.paymentTime = paymentTime;
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
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public Boolean getIsDeleted() {
        return isDeleted;
    }
    
    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    
    public List<OrderItem> getOrderItems() {
        return orderItems;
    }
    
    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
    
    public Customer getCustomer() {
        return customer;
    }
    
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
    public ShippingInfo getShippingInfo() {
        return shippingInfo;
    }
    
    public void setShippingInfo(ShippingInfo shippingInfo) {
        this.shippingInfo = shippingInfo;
    }
    
    public List<OrderLog> getOrderLogs() {
        return orderLogs;
    }
    
    public void setOrderLogs(List<OrderLog> orderLogs) {
        this.orderLogs = orderLogs;
    }
    
    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderNumber='" + orderNumber + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", customerId=" + customerId +
                ", totalAmount=" + totalAmount +
                ", paymentStatus='" + paymentStatus + '\'' +
                '}';
    }
}