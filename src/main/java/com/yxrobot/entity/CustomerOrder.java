package com.yxrobot.entity;

import java.time.LocalDateTime;

/**
 * 客户订单关联实体类
 * 对应customer_order_relation表
 * 遵循项目关联表设计规范：不使用外键约束，通过关联表实现表间关系
 */
public class CustomerOrder {
    
    private Long id;
    private Long customerId;            // 客户ID
    private Long orderId;               // 订单ID
    private CustomerRole customerRole;  // 客户角色：buyer, payer, receiver
    private String relationNotes;       // 关联备注
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private Integer status;             // 状态：1-有效，0-无效
    
    // 关联的订单信息（用于查询时填充）
    private Order order;
    
    // 构造函数
    public CustomerOrder() {}
    
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
    
    public Long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    
    public CustomerRole getCustomerRole() {
        return customerRole;
    }
    
    public void setCustomerRole(CustomerRole customerRole) {
        this.customerRole = customerRole;
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
    
    public Order getOrder() {
        return order;
    }
    
    public void setOrder(Order order) {
        this.order = order;
    }
}