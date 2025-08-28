package com.yxrobot.dto;

import com.yxrobot.entity.PaymentStatus;
import com.yxrobot.entity.SalesStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 销售记录查询条件DTO
 * 用于销售记录的搜索和筛选
 * 
 * 字段映射规范：
 * - 与前端查询表单保持一致的camelCase命名
 * - 用于API接口的查询参数传输
 */
public class SalesRecordQueryDTO {
    
    /**
     * 页码
     */
    private Integer page = 1;
    
    /**
     * 每页大小
     */
    private Integer size = 10;
    
    /**
     * 偏移量（用于分页查询）
     */
    private Integer offset = 0;
    
    /**
     * 排序字段
     */
    private String sortBy = "createdAt";
    
    /**
     * 排序方向
     */
    private String sortDir = "desc";
    
    /**
     * 订单号（模糊搜索）
     */
    private String orderNumber;
    
    /**
     * 客户ID
     */
    private Long customerId;
    
    /**
     * 客户名称（模糊搜索）
     */
    private String customerName;
    
    /**
     * 产品ID
     */
    private Long productId;
    
    /**
     * 产品名称（模糊搜索）
     */
    private String productName;
    
    /**
     * 销售人员ID
     */
    private Long salesStaffId;
    
    /**
     * 销售人员姓名（模糊搜索）
     */
    private String salesStaffName;
    
    /**
     * 最小销售金额
     */
    private BigDecimal minSalesAmount;
    
    /**
     * 最大销售金额
     */
    private BigDecimal maxSalesAmount;
    
    /**
     * 订单开始日期
     */
    private LocalDate startDate;
    
    /**
     * 订单结束日期
     */
    private LocalDate endDate;
    
    /**
     * 订单状态
     */
    private SalesStatus status;
    
    /**
     * 付款状态
     */
    private PaymentStatus paymentStatus;
    
    /**
     * 销售地区
     */
    private String region;
    
    /**
     * 销售渠道
     */
    private String channel;
    
    /**
     * 付款方式
     */
    private String paymentMethod;
    
    /**
     * 关键词搜索（搜索订单号、客户名称、产品名称、备注等）
     */
    private String keyword;
    
    // 默认构造函数
    public SalesRecordQueryDTO() {
    }
    
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
    
    public Integer getOffset() {
        return offset;
    }
    
    public void setOffset(Integer offset) {
        this.offset = offset;
    }
    
    public String getSortBy() {
        return sortBy;
    }
    
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
    
    public String getSortDir() {
        return sortDir;
    }
    
    public void setSortDir(String sortDir) {
        this.sortDir = sortDir;
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
    
    public String getSalesStaffName() {
        return salesStaffName;
    }
    
    public void setSalesStaffName(String salesStaffName) {
        this.salesStaffName = salesStaffName;
    }
    
    public BigDecimal getMinSalesAmount() {
        return minSalesAmount;
    }
    
    public void setMinSalesAmount(BigDecimal minSalesAmount) {
        this.minSalesAmount = minSalesAmount;
    }
    
    public BigDecimal getMaxSalesAmount() {
        return maxSalesAmount;
    }
    
    public void setMaxSalesAmount(BigDecimal maxSalesAmount) {
        this.maxSalesAmount = maxSalesAmount;
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
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getKeyword() {
        return keyword;
    }
    
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    
    @Override
    public String toString() {
        return "SalesRecordQueryDTO{" +
                "page=" + page +
                ", size=" + size +
                ", sortBy='" + sortBy + '\'' +
                ", sortDir='" + sortDir + '\'' +
                ", orderNumber='" + orderNumber + '\'' +
                ", customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", salesStaffId=" + salesStaffId +
                ", salesStaffName='" + salesStaffName + '\'' +
                ", minSalesAmount=" + minSalesAmount +
                ", maxSalesAmount=" + maxSalesAmount +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status=" + status +
                ", paymentStatus=" + paymentStatus +
                ", region='" + region + '\'' +
                ", channel='" + channel + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", keyword='" + keyword + '\'' +
                '}';
    }
}