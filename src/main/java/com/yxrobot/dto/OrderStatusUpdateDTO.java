package com.yxrobot.dto;

/**
 * 订单状态更新数据传输对象
 * 用于订单状态变更操作的参数传递
 */
public class OrderStatusUpdateDTO {
    
    /**
     * 新状态
     */
    private String status;
    
    /**
     * 操作人
     */
    private String operator;
    
    /**
     * 操作备注
     */
    private String notes;
    
    // 构造函数
    public OrderStatusUpdateDTO() {}
    
    public OrderStatusUpdateDTO(String status, String operator, String notes) {
        this.status = status;
        this.operator = operator;
        this.notes = notes;
    }
    
    // Getter和Setter方法
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
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
}