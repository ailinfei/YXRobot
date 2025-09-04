package com.yxrobot.entity;

/**
 * 维护类型枚举
 * 定义设备维护的类型分类
 */
public enum MaintenanceType {
    
    REPAIR("repair", "维修", "设备故障维修"),
    UPGRADE("upgrade", "升级", "设备升级改造"),
    INSPECTION("inspection", "检查", "定期检查维护"),
    REPLACEMENT("replacement", "更换", "部件更换");
    
    private final String code;
    private final String name;
    private final String description;
    
    MaintenanceType(String code, String name, String description) {
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
    public static MaintenanceType fromCode(String code) {
        if (code == null) {
            return null;
        }
        
        for (MaintenanceType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        
        throw new IllegalArgumentException("未知的维护类型代码: " + code);
    }
    
    /**
     * 根据名称获取枚举值
     */
    public static MaintenanceType fromName(String name) {
        if (name == null) {
            return null;
        }
        
        for (MaintenanceType type : values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        
        throw new IllegalArgumentException("未知的维护类型名称: " + name);
    }
    
    @Override
    public String toString() {
        return name;
    }
}