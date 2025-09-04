package com.yxrobot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 订单操作日志数据传输对象
 * 适配前端TypeScript接口，确保字段名称完全匹配
 * 
 * 字段映射规范：
 * - 数据库字段：snake_case（如：order_id, created_at）
 * - Java DTO：camelCase（如：orderId, createdAt）
 * - 前端接口：camelCase（如：orderId, createdAt）
 */
public class OrderLogDTO {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("orderId")
    @NotNull(message = "订单ID不能为空")
    private Long orderId;               // 订单ID
    
    @JsonProperty("action")
    @NotNull(message = "操作动作不能为空")
    @Size(max = 100, message = "操作动作不能超过100个字符")
    private String action;              // 操作动作
    
    @JsonProperty("operator")
    @NotNull(message = "操作人不能为空")
    @Size(max = 100, message = "操作人不能超过100个字符")
    private String operator;            // 操作人
    
    @JsonProperty("notes")
    private String notes;               // 操作备注
    
    // 系统字段
    @JsonProperty("createdAt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    // 构造函数
    public OrderLogDTO() {}
    
    public OrderLogDTO(Long orderId, String action, String operator, String notes) {
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
}