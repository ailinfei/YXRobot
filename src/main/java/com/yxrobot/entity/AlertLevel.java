package com.yxrobot.entity;

/**
 * 告警级别枚举
 * 对应device_alerts表的alert_level字段
 * 
 * @author YXRobot Development Team
 * @since 2024
 */
public enum AlertLevel {
    ERROR("error", "错误", "#f56565", "error-circle"),
    WARNING("warning", "警告", "#ed8936", "warning"),
    INFO("info", "信息", "#4299e1", "info-circle");
    
    private final String code;
    private final String description;
    private final String color;
    private final String icon;
    
    AlertLevel(String code, String description, String color, String icon) {
        this.code = code;
        this.description = description;
        this.color = color;
        this.icon = icon;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getColor() {
        return color;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public static AlertLevel fromCode(String code) {
        for (AlertLevel level : AlertLevel.values()) {
            if (level.code.equals(code)) {
                return level;
            }
        }
        throw new IllegalArgumentException("Unknown alert level code: " + code);
    }
}