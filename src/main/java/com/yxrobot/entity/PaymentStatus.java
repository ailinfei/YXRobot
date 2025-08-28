package com.yxrobot.entity;

/**
 * 付款状态枚举
 * 对应数据库字段：payment_status
 * 
 * 字段映射规范：
 * - 数据库存储值使用小写英文
 * - Java枚举使用大写命名
 */
public enum PaymentStatus {
    /**
     * 未付款
     */
    UNPAID("unpaid", "未付款"),
    
    /**
     * 部分付款
     */
    PARTIAL("partial", "部分付款"),
    
    /**
     * 已付款
     */
    PAID("paid", "已付款"),
    
    /**
     * 已退款
     */
    REFUNDED("refunded", "已退款");
    
    private final String value;
    private final String description;
    
    PaymentStatus(String value, String description) {
        this.value = value;
        this.description = description;
    }
    
    public String getValue() {
        return value;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据数据库值获取枚举
     */
    public static PaymentStatus fromValue(String value) {
        for (PaymentStatus status : PaymentStatus.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的付款状态: " + value);
    }
    
    @Override
    public String toString() {
        return this.value;
    }
}