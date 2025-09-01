package com.yxrobot.entity;

/**
 * 客户状态枚举
 * 定义客户的状态分类
 * 与前端Customer接口的status字段匹配
 */
public enum CustomerStatus {
    
    ACTIVE("active", "活跃", "客户状态正常，可以正常使用服务"),
    INACTIVE("inactive", "非活跃", "客户暂时不活跃，但账户正常"),
    SUSPENDED("suspended", "暂停", "客户账户被暂停，无法使用服务");
    
    private final String code;
    private final String name;
    private final String description;
    
    CustomerStatus(String code, String name, String description) {
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
    public static CustomerStatus fromCode(String code) {
        if (code == null) {
            return null;
        }
        
        for (CustomerStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        
        throw new IllegalArgumentException("未知的客户状态代码: " + code);
    }
    
    /**
     * 根据名称获取枚举值
     */
    public static CustomerStatus fromName(String name) {
        if (name == null) {
            return null;
        }
        
        for (CustomerStatus status : values()) {
            if (status.name.equals(name)) {
                return status;
            }
        }
        
        throw new IllegalArgumentException("未知的客户状态名称: " + name);
    }
    
    @Override
    public String toString() {
        return name;
    }
}