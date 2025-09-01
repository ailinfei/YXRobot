package com.yxrobot.enums;

/**
 * 维护状态枚举
 * 对应数据库 rental_devices.maintenance_status 字段
 * 
 * @author Kiro
 * @date 2025-01-28
 */
public enum MaintenanceStatus {
    
    /**
     * 正常
     */
    NORMAL("normal", "正常"),
    
    /**
     * 警告
     */
    WARNING("warning", "警告"),
    
    /**
     * 紧急
     */
    URGENT("urgent", "紧急");
    
    private final String code;
    private final String description;
    
    MaintenanceStatus(String code, String description) {
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
    public static MaintenanceStatus fromCode(String code) {
        for (MaintenanceStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown maintenance status code: " + code);
    }
}