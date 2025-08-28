package com.yxrobot.entity;

/**
 * 信用等级枚举
 * 对应数据库字段：credit_level
 * 
 * 字段映射规范：
 * - 数据库存储值使用大写字母
 * - Java枚举使用大写命名
 */
public enum CreditLevel {
    /**
     * A级信用（最高）
     */
    A("A", "A级信用"),
    
    /**
     * B级信用（良好）
     */
    B("B", "B级信用"),
    
    /**
     * C级信用（一般）
     */
    C("C", "C级信用"),
    
    /**
     * D级信用（较差）
     */
    D("D", "D级信用");
    
    private final String value;
    private final String description;
    
    CreditLevel(String value, String description) {
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
    public static CreditLevel fromValue(String value) {
        for (CreditLevel level : CreditLevel.values()) {
            if (level.getValue().equals(value)) {
                return level;
            }
        }
        throw new IllegalArgumentException("未知的信用等级: " + value);
    }
    
    @Override
    public String toString() {
        return this.value;
    }
}