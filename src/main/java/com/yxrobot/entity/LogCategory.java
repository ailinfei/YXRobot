package com.yxrobot.entity;

/**
 * 日志分类枚举
 * 定义设备日志的分类
 */
public enum LogCategory {
    
    SYSTEM("system", "系统", "系统相关日志"),
    USER("user", "用户", "用户操作日志"),
    NETWORK("network", "网络", "网络连接日志"),
    HARDWARE("hardware", "硬件", "硬件状态日志"),
    SOFTWARE("software", "软件", "软件运行日志");
    
    private final String code;
    private final String name;
    private final String description;
    
    LogCategory(String code, String name, String description) {
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
    public static LogCategory fromCode(String code) {
        if (code == null) {
            return null;
        }
        
        for (LogCategory category : values()) {
            if (category.code.equalsIgnoreCase(code)) {
                return category;
            }
        }
        
        throw new IllegalArgumentException("未知的日志分类代码: " + code);
    }
    
    /**
     * 根据名称获取枚举值
     */
    public static LogCategory fromName(String name) {
        if (name == null) {
            return null;
        }
        
        for (LogCategory category : values()) {
            if (category.name.equals(name)) {
                return category;
            }
        }
        
        throw new IllegalArgumentException("未知的日志分类名称: " + name);
    }
    
    @Override
    public String toString() {
        return name;
    }
}