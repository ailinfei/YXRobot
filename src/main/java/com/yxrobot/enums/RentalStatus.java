package com.yxrobot.enums;

/**
 * 租赁状态枚举
 * 对应数据库 rental_records.rental_status 字段
 * 
 * @author Kiro
 * @date 2025-01-28
 */
public enum RentalStatus {
    
    /**
     * 待确认
     */
    PENDING("pending", "待确认"),
    
    /**
     * 租赁中
     */
    ACTIVE("active", "租赁中"),
    
    /**
     * 已完成
     */
    COMPLETED("completed", "已完成"),
    
    /**
     * 已取消
     */
    CANCELLED("cancelled", "已取消"),
    
    /**
     * 逾期
     */
    OVERDUE("overdue", "逾期");
    
    private final String code;
    private final String description;
    
    RentalStatus(String code, String description) {
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
    public static RentalStatus fromCode(String code) {
        for (RentalStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown rental status code: " + code);
    }
}