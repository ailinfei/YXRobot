package com.yxrobot.entity;

/**
 * 日志级别枚举
 * 定义设备日志的级别分类
 */
public enum LogLevel {
    
    INFO("info", "信息", "一般信息日志"),
    WARNING("warning", "警告", "警告级别日志"),
    ERROR("error", "错误", "错误级别日志"),
    DEBUG("debug", "调试", "调试级别日志");
    
    private final String code;
    private final String name;
    private final String description;
    
    LogLevel(String code, String name, String description) {
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
    public static LogLevel fromCode(String code) {
        if (code == null) {
            return null;
        }
        
        for (LogLevel level : values()) {
            if (level.code.equalsIgnoreCase(code)) {
                return level;
            }
        }
        
        throw new IllegalArgumentException("未知的日志级别代码: " + code);
    }
    
    /**
     * 根据名称获取枚举值
     */
    public static LogLevel fromName(String name) {
        if (name == null) {
            return null;
        }
        
        for (LogLevel level : values()) {
            if (level.name.equals(name)) {
                return level;
            }
        }
        
        throw new IllegalArgumentException("未知的日志级别名称: " + name);
    }
    
    @Override
    public String toString() {
        return name;
    }
}