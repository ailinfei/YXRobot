package com.yxrobot.entity;

/**
 * 客户等级枚举
 * 定义客户的等级分类
 */
public enum CustomerLevel {
    
    REGULAR("regular", "普通客户", "消费金额低于10000元的客户"),
    VIP("vip", "VIP客户", "消费金额10000-50000元的客户"),
    PREMIUM("premium", "高级客户", "消费金额超过50000元的客户");
    
    private final String code;
    private final String name;
    private final String description;
    
    CustomerLevel(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据代码获取枚举值
     */
    public static CustomerLevel fromCode(String code) {
        if (code == null) {
            return null;
        }
        
        for (CustomerLevel level : values()) {
            if (level.code.equalsIgnoreCase(code)) {
                return level;
            }
        }
        
        throw new IllegalArgumentException("未知的客户等级代码: " + code);
    }
    
    /**
     * 根据名称获取枚举值
     */
    public static CustomerLevel fromName(String name) {
        if (name == null) {
            return null;
        }
        
        for (CustomerLevel level : values()) {
            if (level.name.equals(name)) {
                return level;
            }
        }
        
        throw new IllegalArgumentException("未知的客户等级名称: " + name);
    }
    
    @Override
    public String toString() {
        return name;
    }
}