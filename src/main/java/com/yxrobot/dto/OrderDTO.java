package com.yxrobot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单数据传输对象
 * 适配前端TypeScript接口，确保字段名称完全匹配
 * 
 * 字段映射规范：
 * - 数据库字段：snake_case（如：order_number, total_amount）
 * - Java DTO：camelCase（如：orderNumber, totalAmount）
 * - 前端接口：camelCase（如：orderNumber, totalAmount）
 */
public class OrderDTO {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("orderNumber")
    @NotNull(message = "订单号不能为空")
    @Size(max = 50, message = "订单号不能超过50个字符")
    private String orderNumber;         // 订单号
    
    @JsonProperty("type")
    @NotNull(message = "订单类型不能为空")
    private String type;                // 订单类型：sales, rental
    
    @JsonProperty("status")
    private String status;              // 订单状态：pending, confirmed, processing, shipped, delivered, completed, cancelled
    
    @JsonProperty("customerId")
    @NotNull(message = "客户ID不能为空")
    private Long customerId;            // 客户ID
    
    @JsonProperty("customerName")
    private String customerName;        // 客户名称（关联查询填充）
    
    @JsonProperty("customerPhone")
    private String customerPhone;       // 客户电话（关联查询填充）
    
    @JsonProperty("customerEmail")
    private String customerEmail;       // 客户邮箱（关联查询填充）
    
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
    private String currency;            // 货币类型
    
    @JsonProperty("paymentStatus")
    private String paymentStatus;       // 支付状态：pending, paid, failed, refunded
    
    @JsonProperty("paymentMethod")
    private String paymentMethod;       // 支付方式
    
    @JsonProperty("paymentTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentTime;  // 支付时间
    
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
    
    // 系统字段
    @JsonProperty("createdAt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonProperty("updatedAt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    @JsonProperty("createdBy")
    private String createdBy;           // 创建人
    
    // 关联数据
    @JsonProperty("orderItems")
    private List<OrderItemDTO> orderItems; // 订单商品明细
    
    @JsonProperty("shippingInfo")
    private ShippingInfoDTO shippingInfo;  // 物流信息
    
    @JsonProperty("orderLogs")
    private List<OrderLogDTO> orderLogs;   // 操作日志
    
    // 构造函数
    public OrderDTO() {}
    
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
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
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
    
    public String getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(String paymentStatus) {
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
    
    public List<OrderItemDTO> getOrderItems() {
        return orderItems;
    }
    
    public void setOrderItems(List<OrderItemDTO> orderItems) {
        this.orderItems = orderItems;
    }
    
    public ShippingInfoDTO getShippingInfo() {
        return shippingInfo;
    }
    
    public void setShippingInfo(ShippingInfoDTO shippingInfo) {
        this.shippingInfo = shippingInfo;
    }
    
    public List<OrderLogDTO> getOrderLogs() {
        return orderLogs;
    }
    
    public void setOrderLogs(List<OrderLogDTO> orderLogs) {
        this.orderLogs = orderLogs;
    }
}