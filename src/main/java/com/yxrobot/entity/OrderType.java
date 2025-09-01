package com.yxrobot.entity;

/**
 * 订单类型枚举
 * 定义订单的类型分类
 * 与前端CustomerOrder接口的type字段匹配
 */
public enum OrderType {
    
    SALES("sales", "销售", "设备销售订单"),
    RENTAL("rental", "租赁", "设备租赁订单");
    
    private final String code;
    private final String name;
    private final String description;
    
    OrderType(String code, String name, String description) {
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
    public static OrderType fromCode(String code) {
        if (code == null) {
            return null;
        }
        
        for (OrderType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        
        throw new IllegalArgumentException("未知的订单类型代码: " + code);
    }
    
    /**
     * 根据名称获取枚举值
     */
    public static OrderType fromName(String name) {
        if (name == null) {
            return null;
        }
        
        for (OrderType type : values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        
        throw new IllegalArgumentException("未知的订单类型名称: " + name);
    }
    
    @Override
    public String toString() {
        return name;
    }
}