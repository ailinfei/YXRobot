package com.yxrobot.entity;

/**
 * 客户类型枚举
 * 对应数据库字段：customer_type
 * 
 * 字段映射规范：
 * - 数据库存储值使用小写英文
 * - Java枚举使用大写命名
 */
public enum CustomerType {
    /**
     * 个人客户
     */
    INDIVIDUAL("individual", "个人"),
    
    /**
     * 企业客户
     */
    ENTERPRISE("enterprise", "企业");
    
    private final String value;
    private final String description;
    
    CustomerType(String value, String description) {
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
    public static CustomerType fromValue(String value) {
        for (CustomerType type : CustomerType.values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的客户类型: " + value);
    }
    
    @Override
    public String toString() {
        return this.value;
    }
}