package com.yxrobot.entity;

/**
 * 设备类型枚举
 * 定义设备的类型分类
 * 与前端CustomerDevice接口的type字段匹配
 */
public enum DeviceType {
    
    PURCHASED("purchased", "购买", "客户购买的设备"),
    RENTAL("rental", "租赁", "客户租赁的设备");
    
    private final String code;
    private final String name;
    private final String description;
    
    DeviceType(String code, String name, String description) {
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
    public static DeviceType fromCode(String code) {
        if (code == null) {
            return null;
        }
        
        for (DeviceType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        
        throw new IllegalArgumentException("未知的设备类型代码: " + code);
    }
    
    /**
     * 根据名称获取枚举值
     */
    public static DeviceType fromName(String name) {
        if (name == null) {
            return null;
        }
        
        for (DeviceType type : values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        
        throw new IllegalArgumentException("未知的设备类型名称: " + name);
    }
    
    @Override
    public String toString() {
        return name;
    }
}