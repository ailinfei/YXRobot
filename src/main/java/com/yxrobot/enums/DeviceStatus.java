package com.yxrobot.enums;

/**
 * 设备状态枚举
 * 对应数据库 rental_devices.current_status 字段
 * 
 * @author Kiro
 * @date 2025-01-28
 */
public enum DeviceStatus {
    
    /**
     * 运行中
     */
    ACTIVE("active", "运行中"),
    
    /**
     * 空闲
     */
    IDLE("idle", "空闲"),
    
    /**
     * 维护中
     */
    MAINTENANCE("maintenance", "维护中"),
    
    /**
     * 已退役
     */
    RETIRED("retired", "已退役");
    
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
    
    /**
     * 根据代码获取枚举
     */
    public static DeviceStatus fromCode(String code) {
        for (DeviceStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown device status code: " + code);
    }
}