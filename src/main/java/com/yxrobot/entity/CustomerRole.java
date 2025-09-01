package com.yxrobot.entity;

/**
 * 客户角色枚举
 * 定义客户在订单中的角色
 * 用于customer_order_relation表
 */
public enum CustomerRole {
    
    BUYER("buyer", "购买者", "订单的购买者"),
    PAYER("payer", "付款者", "订单的付款者"),
    RECEIVER("receiver", "收货者", "订单的收货者");
    
    private final String code;
    private final String name;
    private final String description;
    
    CustomerRole(String code, String name, String description) {
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
    public static CustomerRole fromCode(String code) {
        if (code == null) {
            return null;
        }
        
        for (CustomerRole role : values()) {
            if (role.code.equalsIgnoreCase(code)) {
                return role;
            }
        }
        
        throw new IllegalArgumentException("未知的客户角色代码: " + code);
    }
    
    /**
     * 根据名称获取枚举值
     */
    public static CustomerRole fromName(String name) {
        if (name == null) {
            return null;
        }
        
        for (CustomerRole role : values()) {
            if (role.name.equals(name)) {
                return role;
            }
        }
        
        throw new IllegalArgumentException("未知的客户角色名称: " + name);
    }
    
    @Override
    public String toString() {
        return name;
    }
}