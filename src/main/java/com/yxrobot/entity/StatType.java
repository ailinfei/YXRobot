package com.yxrobot.entity;

/**
 * 统计类型枚举
 * 对应数据库字段：stat_type
 * 
 * 字段映射规范：
 * - 数据库存储值使用小写英文
 * - Java枚举使用大写命名
 */
public enum StatType {
    /**
     * 日统计
     */
    DAILY("daily", "日统计"),
    
    /**
     * 周统计
     */
    WEEKLY("weekly", "周统计"),
    
    /**
     * 月统计
     */
    MONTHLY("monthly", "月统计"),
    
    /**
     * 年统计
     */
    YEARLY("yearly", "年统计");
    
    private final String value;
    private final String description;
    
    StatType(String value, String description) {
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
    public static StatType fromValue(String value) {
        for (StatType type : StatType.values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的统计类型: " + value);
    }
    
    @Override
    public String toString() {
        return this.value;
    }
}