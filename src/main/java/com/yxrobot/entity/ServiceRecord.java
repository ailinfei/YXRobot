package com.yxrobot.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 服务记录实体类
 * 对应service_records表 - 独立服务记录主表
 */
public class ServiceRecord {
    
    private Long id;
    private String serviceNumber;       // 服务单号
    private ServiceType serviceType;    // 服务类型：maintenance, upgrade, consultation, complaint
    private String serviceTitle;        // 服务标题
    private String serviceDescription;  // 服务描述
    private String serviceStaff;        // 服务人员
    private BigDecimal serviceCost;     // 服务费用
    private ServiceStatus serviceStatus; // 服务状态：in_progress, completed, cancelled
    private Priority priority;          // 优先级：low, medium, high, urgent
    private String assignedTo;          // 分配给
    private LocalDateTime resolvedAt;   // 解决时间
    private LocalDate serviceDate;      // 服务日期
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;
    
    // 构造函数
    public ServiceRecord() {}
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getServiceNumber() {
        return serviceNumber;
    }
    
    public void setServiceNumber(String serviceNumber) {
        this.serviceNumber = serviceNumber;
    }
    
    public ServiceType getServiceType() {
        return serviceType;
    }
    
    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }
    
    public String getServiceTitle() {
        return serviceTitle;
    }
    
    public void setServiceTitle(String serviceTitle) {
        this.serviceTitle = serviceTitle;
    }
    
    public String getServiceDescription() {
        return serviceDescription;
    }
    
    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }
    
    public String getServiceStaff() {
        return serviceStaff;
    }
    
    public void setServiceStaff(String serviceStaff) {
        this.serviceStaff = serviceStaff;
    }
    
    public BigDecimal getServiceCost() {
        return serviceCost;
    }
    
    public void setServiceCost(BigDecimal serviceCost) {
        this.serviceCost = serviceCost;
    }
    
    public ServiceStatus getServiceStatus() {
        return serviceStatus;
    }
    
    public void setServiceStatus(ServiceStatus serviceStatus) {
        this.serviceStatus = serviceStatus;
    }
    
    public Priority getPriority() {
        return priority;
    }
    
    public void setPriority(Priority priority) {
        this.priority = priority;
    }
    
    public String getAssignedTo() {
        return assignedTo;
    }
    
    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }
    
    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }
    
    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
    
    public LocalDate getServiceDate() {
        return serviceDate;
    }
    
    public void setServiceDate(LocalDate serviceDate) {
        this.serviceDate = serviceDate;
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
}