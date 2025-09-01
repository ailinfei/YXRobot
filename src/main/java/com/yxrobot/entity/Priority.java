package com.yxrobot.entity;

/**
 * 优先级枚举
 * 对应数据库中的priority字段
 */
public enum Priority {
    LOW("low", "低优先级"),
    MEDIUM("medium", "中优先级"),
    HIGH("high", "高优先级"),
    URGENT("urgent", "紧急");
    
    private final String code;
    private final String description;
    
    Priority(String code, String description) {
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
     * 根据代码获取枚举值
     */
    public static Priority fromCode(String code) {
        for (Priority priority : values()) {
            if (priority.code.equals(code)) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Unknown priority code: " + code);
    }
}