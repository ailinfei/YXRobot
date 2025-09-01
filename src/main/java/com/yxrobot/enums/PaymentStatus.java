package com.yxrobot.enums;

/**
 * 付款状态枚举
 * 对应数据库 rental_records.payment_status 字段
 * 
 * @author Kiro
 * @date 2025-01-28
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
     * 根据代码获取枚举
     */
    public static PaymentStatus fromCode(String code) {
        for (PaymentStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown payment status code: " + code);
    }
}