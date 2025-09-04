package com.yxrobot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

/**
 * 订单查询数据传输对象
 * 用于前端搜索和筛选功能
 * 
 * 字段映射规范：
 * - Java DTO：camelCase（如：orderNumber, customerName）
 * - 前端接口：camelCase（如：orderNumber, customerName）
 */
public class OrderQueryDTO {
    
    @JsonProperty("page")
    private Integer page = 1;           // 页码，默认第1页
    
    @JsonProperty("size")
    private Integer size = 10;          // 每页大小，默认10条
    
    @JsonProperty("keyword")
    private String keyword;             // 关键词搜索（订单号、客户名称）
    
    @JsonProperty("orderNumber")
    private String orderNumber;         // 订单号
    
    @JsonProperty("type")
    private String type;                // 订单类型：sales, rental
    
    @JsonProperty("status")
    private String status;              // 订单状态
    
    @JsonProperty("paymentStatus")
    private String paymentStatus;       // 支付状态
    
    @JsonProperty("customerId")
    private Long customerId;            // 客户ID
    
    @JsonProperty("customerName")
    private String customerName;        // 客户名称
    
    @JsonProperty("salesPerson")
    private String salesPerson;         // 销售人员
    
    @JsonProperty("startDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;        // 开始日期
    
    @JsonProperty("endDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;          // 结束日期
    
    @JsonProperty("minAmount")
    private java.math.BigDecimal minAmount; // 最小金额
    
    @JsonProperty("maxAmount")
    private java.math.BigDecimal maxAmount; // 最大金额
    
    @JsonProperty("sortBy")
    private String sortBy = "createdAt"; // 排序字段，默认按创建时间
    
    @JsonProperty("sortOrder")
    private String sortOrder = "desc";   // 排序方向：asc, desc，默认降序
    
    // 构造函数
    public OrderQueryDTO() {}
    
    // Getter和Setter方法
    public Integer getPage() {
        return page;
    }
    
    public void setPage(Integer page) {
        this.page = page;
    }
    
    public Integer getSize() {
        return size;
    }
    
    public void setSize(Integer size) {
        this.size = size;
    }
    
    public String getKeyword() {
        return keyword;
    }
    
    public void setKeyword(String keyword) {
        this.keyword = keyword;
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
    
    public String getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
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
    
    public String getSalesPerson() {
        return salesPerson;
    }
    
    public void setSalesPerson(String salesPerson) {
        this.salesPerson = salesPerson;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public java.math.BigDecimal getMinAmount() {
        return minAmount;
    }
    
    public void setMinAmount(java.math.BigDecimal minAmount) {
        this.minAmount = minAmount;
    }
    
    public java.math.BigDecimal getMaxAmount() {
        return maxAmount;
    }
    
    public void setMaxAmount(java.math.BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }
    
    public String getSortBy() {
        return sortBy;
    }
    
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
    
    public String getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
}