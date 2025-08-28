package com.yxrobot.entity;

/**
 * 销售状态枚举
 * 对应数据库字段：status
 * 
 * 字段映射规范：
 * - 数据库存储值使用小写英文
 * - Java枚举使用大写命名
 */
public enum SalesStatus {
    /**
     * 待确认
     */
    PENDING("pending", "待确认"),
    
    /**
     * 已确认
     */
    CONFIRMED("confirmed", "已确认"),
    
    /**
     * 已交付
     */
    DELIVERED("delivered", "已交付"),
    
    /**
     * 已完成
     */
    COMPLETED("completed", "已完成"),
    
    /**
     * 已取消
     */
    CANCELLED("cancelled", "已取消");
    
    private final String value;
    private final String description;
    
    SalesStatus(String value, String description) {
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
    public static SalesStatus fromValue(String value) {
        for (SalesStatus status : SalesStatus.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的销售状态: " + value);
    }
    
    @Override
    public String toString() {
        return this.value;
    }
}