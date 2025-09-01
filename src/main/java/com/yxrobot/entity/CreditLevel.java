package com.yxrobot.entity;

/**
 * 信用等级枚举
 * 定义客户的信用评级
 */
public enum CreditLevel {
    
    EXCELLENT("excellent", "优秀", "信用记录优秀，AAA级"),
    GOOD("good", "良好", "信用记录良好，AA级"),
    FAIR("fair", "一般", "信用记录一般，A级"),
    POOR("poor", "较差", "信用记录较差，B级"),
    BAD("bad", "很差", "信用记录很差，C级"),
    UNKNOWN("unknown", "未知", "信用记录未知");
    
    private final String code;
    private final String name;
    private final String description;
    
    CreditLevel(String code, String name, String description) {
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
     * 获取枚举值（用于类型处理器）
     */
    public String getValue() {
        return code;
    }
    
    /**
     * 根据值获取枚举（用于类型处理器）
     */
    public static CreditLevel fromValue(String value) {
        return fromCode(value);
    }
    
    /**
     * 根据代码获取枚举值
     */
    public static CreditLevel fromCode(String code) {
        if (code == null) {
            return null;
        }
        
        for (CreditLevel level : values()) {
            if (level.code.equalsIgnoreCase(code)) {
                return level;
            }
        }
        
        throw new IllegalArgumentException("未知的信用等级代码: " + code);
    }
    
    /**
     * 根据名称获取枚举值
     */
    public static CreditLevel fromName(String name) {
        if (name == null) {
            return null;
        }
        
        for (CreditLevel level : values()) {
            if (level.name.equals(name)) {
                return level;
            }
        }
        
        throw new IllegalArgumentException("未知的信用等级名称: " + name);
    }
    
    @Override
    public String toString() {
        return name;
    }
}