package com.yxrobot.entity;

/**
 * 设备状态枚举
 * 定义设备的状态分类
 * 与前端CustomerDevice接口的status字段匹配
 */
public enum DeviceStatus {
    
    PENDING("pending", "待激活", "设备已分配但尚未激活"),
    ACTIVE("active", "活跃", "设备正常运行中"),
    OFFLINE("offline", "离线", "设备离线或无法连接"),
    MAINTENANCE("maintenance", "维护中", "设备正在维护或维修"),
    RETIRED("retired", "已退役", "设备已停用或回收");
    
    private final String code;
    private final String name;
    private final String description;
    
    DeviceStatus(String code, String name, String description) {
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
    public static DeviceStatus fromCode(String code) {
        if (code == null) {
            return null;
        }
        
        for (DeviceStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        
        throw new IllegalArgumentException("未知的设备状态代码: " + code);
    }
    
    /**
     * 根据名称获取枚举值
     */
    public static DeviceStatus fromName(String name) {
        if (name == null) {
            return null;
        }
        
        for (DeviceStatus status : values()) {
            if (status.name.equals(name)) {
                return status;
            }
        }
        
        throw new IllegalArgumentException("未知的设备状态名称: " + name);
    }
    
    @Override
    public String toString() {
        return name;
    }
}