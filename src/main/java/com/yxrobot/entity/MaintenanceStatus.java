package com.yxrobot.entity;

/**
 * 维护状态枚举
 * 定义设备维护的状态分类
 */
public enum MaintenanceStatus {
    
    PENDING("pending", "待处理", "维护任务已创建，等待开始"),
    IN_PROGRESS("in_progress", "进行中", "维护任务正在进行"),
    COMPLETED("completed", "已完成", "维护任务已完成"),
    CANCELLED("cancelled", "已取消", "维护任务已取消");
    
    private final String code;
    private final String name;
    private final String description;
    
    MaintenanceStatus(String code, String name, String description) {
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
    public static MaintenanceStatus fromCode(String code) {
        if (code == null) {
            return null;
        }
        
        for (MaintenanceStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        
        throw new IllegalArgumentException("未知的维护状态代码: " + code);
    }
    
    /**
     * 根据名称获取枚举值
     */
    public static MaintenanceStatus fromName(String name) {
        if (name == null) {
            return null;
        }
        
        for (MaintenanceStatus status : values()) {
            if (status.name.equals(name)) {
                return status;
            }
        }
        
        throw new IllegalArgumentException("未知的维护状态名称: " + name);
    }
    
    @Override
    public String toString() {
        return name;
    }
}