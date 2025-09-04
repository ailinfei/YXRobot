package com.yxrobot.entity;

/**
 * 付款状态枚举
 * 对应数据库字段：payment_status
 * 
 * 字段映射规范：
 * - 数据库存储值：小写英文（pending, paid, failed, refunded）
 * - Java枚举：大写命名（PENDING, PAID, FAILED, REFUNDED）
 * - 前端显示：中文描述（待付款, 已付款, 付款失败, 已退款）
 */
public enum PaymentStatus {
    /**
     * 待付款
     */
    PENDING("pending", "待付款"),
    
    /**
     * 已付款
     */
    PAID("paid", "已付款"),
    
    /**
     * 付款失败
     */
    FAILED("failed", "付款失败"),
    
    /**
     * 已退款
     */
    REFUNDED("refunded", "已退款");
    
    private final String code;
    private final String description;
    
    PaymentStatus(String code, String description) {
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
     * 根据数据库值获取枚举
     */
    public static PaymentStatus fromCode(String code) {
        if (code == null) {
            return null;
        }
        
        for (PaymentStatus status : PaymentStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的付款状态: " + code);
    }
    
    @Override
    public String toString() {
        return description;
    }
}