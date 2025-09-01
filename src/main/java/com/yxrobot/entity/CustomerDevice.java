package com.yxrobot.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 客户设备关联实体类
 * 对应customer_device_relation表
 * 遵循项目关联表设计规范：不使用外键约束，通过关联表实现表间关系
 */
public class CustomerDevice {
    
    private Long id;
    private Long customerId;            // 客户ID
    private Long deviceId;              // 设备ID
    private RelationType relationType;  // 关联类型：purchased, rental
    private LocalDate startDate;        // 关联开始日期
    private LocalDate endDate;          // 关联结束日期（租赁设备）
    private LocalDate warrantyEndDate;  // 保修结束日期（购买设备）
    private BigDecimal dailyRentalFee;  // 日租金（租赁设备）
    private BigDecimal purchasePrice;   // 购买价格（购买设备）
    private String relationNotes;       // 关联备注
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private Integer status;             // 状态：1-有效，0-无效
    
    // 关联的设备信息（用于查询时填充）
    private Device device;
    
    // 构造函数
    public CustomerDevice() {}
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    
    public Long getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }
    
    public RelationType getRelationType() {
        return relationType;
    }
    
    public void setRelationType(RelationType relationType) {
        this.relationType = relationType;
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
    
    public LocalDate getWarrantyEndDate() {
        return warrantyEndDate;
    }
    
    public void setWarrantyEndDate(LocalDate warrantyEndDate) {
        this.warrantyEndDate = warrantyEndDate;
    }
    
    public BigDecimal getDailyRentalFee() {
        return dailyRentalFee;
    }
    
    public void setDailyRentalFee(BigDecimal dailyRentalFee) {
        this.dailyRentalFee = dailyRentalFee;
    }
    
    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }
    
    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }
    
    public String getRelationNotes() {
        return relationNotes;
    }
    
    public void setRelationNotes(String relationNotes) {
        this.relationNotes = relationNotes;
    }
    
    public LocalDateTime getCreatedTime() {
        return createdTime;
    }
    
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }
    
    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }
    
    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public Device getDevice() {
        return device;
    }
    
    public void setDevice(Device device) {
        this.device = device;
    }
}