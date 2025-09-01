package com.yxrobot.entity;

/**
 * 服务类型枚举
 * 定义服务记录的类型分类
 * 与前端CustomerServiceRecord接口的type字段匹配
 */
public enum ServiceType {
    
    MAINTENANCE("maintenance", "维护", "设备维护服务"),
    UPGRADE("upgrade", "升级", "设备升级服务"),
    CONSULTATION("consultation", "咨询", "技术咨询服务"),
    COMPLAINT("complaint", "投诉", "客户投诉处理");
    
    private final String code;
    private final String name;
    private final String description;
    
    ServiceType(String code, String name, String description) {
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
    public static ServiceType fromCode(String code) {
        if (code == null) {
            return null;
        }
        
        for (ServiceType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        
        throw new IllegalArgumentException("未知的服务类型代码: " + code);
    }
    
    /**
     * 根据名称获取枚举值
     */
    public static ServiceType fromName(String name) {
        if (name == null) {
            return null;
        }
        
        for (ServiceType type : values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        
        throw new IllegalArgumentException("未知的服务类型名称: " + name);
    }
    
    @Override
    public String toString() {
        return name;
    }
}