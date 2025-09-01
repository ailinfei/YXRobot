package com.yxrobot.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 客户服务记录关联实体类
 * 对应customer_service_relation表
 * 遵循项目关联表设计规范：不使用外键约束，通过关联表实现表间关系
 */
public class CustomerServiceRecord {
    
    private Long id;
    private Long customerId;            // 客户ID
    private Long serviceRecordId;       // 服务记录ID
    private String relationNotes;       // 关联备注
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private Integer status;             // 状态：1-有效，0-无效
    
    // 关联的服务记录信息（用于查询时填充）
    private ServiceRecord serviceRecord;
    
    // 构造函数
    public CustomerServiceRecord() {}
    
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
    
    public Long getServiceRecordId() {
        return serviceRecordId;
    }
    
    public void setServiceRecordId(Long serviceRecordId) {
        this.serviceRecordId = serviceRecordId;
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
    
    public ServiceRecord getServiceRecord() {
        return serviceRecord;
    }
    
    public void setServiceRecord(ServiceRecord serviceRecord) {
        this.serviceRecord = serviceRecord;
    }
}