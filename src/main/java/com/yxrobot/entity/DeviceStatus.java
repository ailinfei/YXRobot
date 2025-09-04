package com.yxrobot.entity;

/**
 * 设备状态枚举
 * 对应device_monitoring_data表的status字段
 * 
 * @author YXRobot Development Team
 * @since 2024
 */
public enum DeviceStatus {
    ONLINE("online", "在线"),
    OFFLINE("offline", "离线"),
    ERROR("error", "故障"),
    MAINTENANCE("maintenance", "维护中"),
    PENDING("pending", "待激活"); // 添加PENDING状态
    
    private final String code;
    private final String description;
    
    DeviceStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    // 添加getName()方法，返回description
    public String getName() {
        return description;
    }
    
    public static DeviceStatus fromCode(String code) {
        for (DeviceStatus status : DeviceStatus.values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown device status code: " + code);
    }
}