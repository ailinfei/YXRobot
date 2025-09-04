package com.yxrobot.entity;

/**
 * 连接状态枚举
 * 对应device_network_status表的connection_status字段
 * 
 * @author YXRobot Development Team
 * @since 2024
 */
public enum ConnectionStatus {
    CONNECTED("connected", "已连接"),
    DISCONNECTED("disconnected", "已断开"),
    CONNECTING("connecting", "连接中");
    
    private final String code;
    private final String description;
    
    ConnectionStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static ConnectionStatus fromCode(String code) {
        for (ConnectionStatus status : ConnectionStatus.values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown connection status code: " + code);
    }
}