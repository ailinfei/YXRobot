package com.yxrobot.entity;

import com.yxrobot.enums.PaymentStatus;
import com.yxrobot.enums.RentalStatus;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 租赁记录实体类
 * 对应数据库表 rental_records
 * 
 * @author Kiro
 * @date 2025-01-28
 */
public class RentalRecord {
    
    /**
     * 租赁记录ID，主键
     */
    private Long id;
    
    /**
     * 租赁订单号
     */
    @NotBlank(message = "租赁订单号不能为空")
    @Size(max = 50, message = "租赁订单号长度不能超过50字符")
    private String rentalOrderNumber;
    
    /**
     * 设备ID
     */
    @NotNull(message = "设备ID不能为空")
    private Long deviceId;
    
    /**
     * 客户ID
     */
    @NotNull(message = "客户ID不能为空")
    private Long customerId;
    
    /**
     * 租赁开始日期
     */
    @NotNull(message = "租赁开始日期不能为空")
    private LocalDate rentalStartDate;
    
    /**
     * 租赁结束日期
     */
    private LocalDate rentalEndDate;
    
    /**
     * 计划结束日期
     */
    @NotNull(message = "计划结束日期不能为空")
    private LocalDate plannedEndDate;
    
    /**
     * 租赁期间（天数）
     */
    @NotNull(message = "租赁期间不能为空")
    @Min(value = 1, message = "租赁期间至少为1天")
    @Max(value = 365, message = "租赁期间不能超过365天")
    private Integer rentalPeriod;
    
    /**
     * 日租金
     */
    @NotNull(message = "日租金不能为空")
    @DecimalMin(value = "0.01", message = "日租金必须大于0")
    @DecimalMax(value = "9999.99", message = "日租金不能超过9999.99")
    @Digits(integer = 8, fraction = 2, message = "日租金格式不正确")
    private BigDecimal dailyRentalFee;
    
    /**
     * 总租金
     */
    @NotNull(message = "总租金不能为空")
    @DecimalMin(value = "0.01", message = "总租金必须大于0")
    @DecimalMax(value = "999999.99", message = "总租金不能超过999999.99")
    @Digits(integer = 10, fraction = 2, message = "总租金格式不正确")
    private BigDecimal totalRentalFee;
    
    /**
     * 押金金额
     */
    @DecimalMin(value = "0", message = "押金金额不能为负数")
    @DecimalMax(value = "99999.99", message = "押金金额不能超过99999.99")
    @Digits(integer = 8, fraction = 2, message = "押金金额格式不正确")
    private BigDecimal depositAmount;
    
    /**
     * 实际支付金额
     */
    @NotNull(message = "实际支付金额不能为空")
    @DecimalMin(value = "0", message = "实际支付金额不能为负数")
    @DecimalMax(value = "999999.99", message = "实际支付金额不能超过999999.99")
    @Digits(integer = 10, fraction = 2, message = "实际支付金额格式不正确")
    private BigDecimal actualPayment;
    
    /**
     * 付款状态
     */
    private PaymentStatus paymentStatus;
    
    /**
     * 租赁状态
     */
    private RentalStatus rentalStatus;
    
    /**
     * 交付方式
     */
    @Size(max = 50, message = "交付方式长度不能超过50字符")
    private String deliveryMethod;
    
    /**
     * 交付地址
     */
    private String deliveryAddress;
    
    /**
     * 实际归还日期
     */
    private LocalDate returnDate;
    
    /**
     * 归还状态
     */
    @Size(max = 100, message = "归还状态长度不能超过100字符")
    private String returnCondition;
    
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
    
    /**
     * 是否删除
     */
    private Boolean isDeleted;
    
    // 构造函数
    public RentalRecord() {
        this.paymentStatus = PaymentStatus.UNPAID;
        this.rentalStatus = RentalStatus.PENDING;
        this.depositAmount = BigDecimal.ZERO;
        this.isDeleted = false;
    }
    
    // Getter 和 Setter 方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getRentalOrderNumber() {
        return rentalOrderNumber;
    }
    
    public void setRentalOrderNumber(String rentalOrderNumber) {
        this.rentalOrderNumber = rentalOrderNumber;
    }
    
    public Long getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }
    
    public Long getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
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
    
    public LocalDate getPlannedEndDate() {
        return plannedEndDate;
    }
    
    public void setPlannedEndDate(LocalDate plannedEndDate) {
        this.plannedEndDate = plannedEndDate;
    }
    
    public Integer getRentalPeriod() {
        return rentalPeriod;
    }
    
    public void setRentalPeriod(Integer rentalPeriod) {
        this.rentalPeriod = rentalPeriod;
    }
    
    public BigDecimal getDailyRentalFee() {
        return dailyRentalFee;
    }
    
    public void setDailyRentalFee(BigDecimal dailyRentalFee) {
        this.dailyRentalFee = dailyRentalFee;
    }
    
    public BigDecimal getTotalRentalFee() {
        return totalRentalFee;
    }
    
    public void setTotalRentalFee(BigDecimal totalRentalFee) {
        this.totalRentalFee = totalRentalFee;
    }
    
    public BigDecimal getDepositAmount() {
        return depositAmount;
    }
    
    public void setDepositAmount(BigDecimal depositAmount) {
        this.depositAmount = depositAmount;
    }
    
    public BigDecimal getActualPayment() {
        return actualPayment;
    }
    
    public void setActualPayment(BigDecimal actualPayment) {
        this.actualPayment = actualPayment;
    }
    
    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public RentalStatus getRentalStatus() {
        return rentalStatus;
    }
    
    public void setRentalStatus(RentalStatus rentalStatus) {
        this.rentalStatus = rentalStatus;
    }
    
    public String getDeliveryMethod() {
        return deliveryMethod;
    }
    
    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }
    
    public String getDeliveryAddress() {
        return deliveryAddress;
    }
    
    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
    
    public LocalDate getReturnDate() {
        return returnDate;
    }
    
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
    
    public String getReturnCondition() {
        return returnCondition;
    }
    
    public void setReturnCondition(String returnCondition) {
        this.returnCondition = returnCondition;
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
        return "RentalRecord{" +
                "id=" + id +
                ", rentalOrderNumber='" + rentalOrderNumber + '\'' +
                ", deviceId=" + deviceId +
                ", customerId=" + customerId +
                ", rentalStartDate=" + rentalStartDate +
                ", rentalEndDate=" + rentalEndDate +
                ", totalRentalFee=" + totalRentalFee +
                ", rentalStatus=" + rentalStatus +
                ", paymentStatus=" + paymentStatus +
                '}';
    }
}