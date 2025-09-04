package com.yxrobot.entity;

/**
 * 订单状态枚举
 * 对应数据库中的status字段
 * 
 * 字段映射规范：
 * - 数据库存储值：小写英文（pending, confirmed, processing, shipped, delivered, completed, cancelled）
 * - Java枚举：大写命名（PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, COMPLETED, CANCELLED）
 * - 前端显示：中文描述（待处理, 已确认, 处理中, 已发货, 已送达, 已完成, 已取消）
 */
public enum OrderStatus {
    PENDING("pending", "待处理"),
    CONFIRMED("confirmed", "已确认"),
    PROCESSING("processing", "处理中"),
    SHIPPED("shipped", "已发货"),
    DELIVERED("delivered", "已送达"),
    COMPLETED("completed", "已完成"),
    CANCELLED("cancelled", "已取消");
    
    private final String code;
    private final String description;
    
    OrderStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据代码获取枚举值
     */
    public static OrderStatus fromCode(String code) {
        if (code == null) {
            return null;
        }
        
        for (OrderStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的订单状态代码: " + code);
    }
    
    /**
     * 检查状态流转是否合法
     */
    public boolean canTransitionTo(OrderStatus newStatus) {
        switch (this) {
            case PENDING:
                return newStatus == CONFIRMED || newStatus == CANCELLED;
            case CONFIRMED:
                return newStatus == PROCESSING || newStatus == CANCELLED;
            case PROCESSING:
                return newStatus == SHIPPED || newStatus == CANCELLED;
            case SHIPPED:
                return newStatus == DELIVERED || newStatus == CANCELLED;
            case DELIVERED:
                return newStatus == COMPLETED;
            case COMPLETED:
            case CANCELLED:
                return false; // 终态，不能再转换
            default:
                return false;
        }
    }
    
    @Override
    public String toString() {
        return description;
    }
}