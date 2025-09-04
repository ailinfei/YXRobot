package com.yxrobot.entity;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 订单操作日志实体类
 * 对应order_logs表
 * 记录订单的所有操作历史
 * 
 * 字段映射规范：
 * - 数据库字段：snake_case（如：order_id, created_at）
 * - Java实体类：camelCase（如：orderId, createdAt）
 * - 前端接口：camelCase（如：orderId, createdAt）
 */
public class OrderLog {
    
    private Long id;
    
    @NotNull(message = "订单ID不能为空")
    private Long orderId;               // 订单ID
    
    @NotNull(message = "操作动作不能为空")
    @Size(max = 100, message = "操作动作不能超过100个字符")
    private String action;              // 操作动作
    
    @NotNull(message = "操作人不能为空")
    @Size(max = 100, message = "操作人不能超过100个字符")
    private String operator;            // 操作人
    
    private String notes;               // 操作备注
    
    // 系统字段
    private LocalDateTime createdAt;
    
    // 构造函数
    public OrderLog() {}
    
    public OrderLog(Long orderId, String action, String operator, String notes) {
        this.orderId = orderId;
        this.action = action;
        this.operator = operator;
        this.notes = notes;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public String getOperator() {
        return operator;
    }
    
    public void setOperator(String operator) {
        this.operator = operator;
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
    
    @Override
    public String toString() {
        return "OrderLog{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", action='" + action + '\'' +
                ", operator='" + operator + '\'' +
                ", notes='" + notes + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}