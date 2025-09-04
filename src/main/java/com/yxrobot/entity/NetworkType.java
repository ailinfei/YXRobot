package com.yxrobot.entity;

/**
 * 网络类型枚举
 * 对应device_network_status表的network_type字段
 * 
 * @author YXRobot Development Team
 * @since 2024
 */
public enum NetworkType {
    WIFI("wifi", "WiFi"),
    ETHERNET("ethernet", "以太网"),
    CELLULAR_4G("4g", "4G移动网络"),
    CELLULAR_5G("5g", "5G移动网络"),
    BLUETOOTH("bluetooth", "蓝牙"),
    UNKNOWN("unknown", "未知");
    
    private final String code;
    private final String description;
    
    NetworkType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static NetworkType fromCode(String code) {
        for (NetworkType type : NetworkType.values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown network type code: " + code);
    }
}