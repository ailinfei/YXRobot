package com.yxrobot.entity;

/**
 * 关联类型枚举
 * 定义客户与设备之间的关联类型
 * 用于customer_device_relation表
 */
public enum RelationType {
    
    PURCHASED("purchased", "购买", "客户购买了该设备"),
    RENTAL("rental", "租赁", "客户租赁了该设备");
    
    private final String code;
    private final String name;
    private final String description;
    
    RelationType(String code, String name, String description) {
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
    public static RelationType fromCode(String code) {
        if (code == null) {
            return null;
        }
        
        for (RelationType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        
        throw new IllegalArgumentException("未知的关联类型代码: " + code);
    }
    
    /**
     * 根据名称获取枚举值
     */
    public static RelationType fromName(String name) {
        if (name == null) {
            return null;
        }
        
        for (RelationType type : values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        
        throw new IllegalArgumentException("未知的关联类型名称: " + name);
    }
    
    @Override
    public String toString() {
        return name;
    }
}