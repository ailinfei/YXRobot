package com.yxrobot.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 客户设备关联实体类
 * 对应customer_device_relation表
 */
public class CustomerDeviceRelation {
    
    private Long id;
    private Long customerId;            // 客户ID
    private Long deviceId;              // 设备ID
    private RelationType relationType;  // 关联类型：购买、租赁
    private LocalDate startDate;        // 关联开始日期
    private LocalDate endDate;          // 关联结束日期（租赁设备）
    private LocalDateTime createdTime;  // 创建时间
    private LocalDateTime updatedTime;  // 更新时间
    private Integer status;             // 状态：1-有效，0-无效
    
    // 构造函数
    public CustomerDeviceRelation() {}
    
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
}