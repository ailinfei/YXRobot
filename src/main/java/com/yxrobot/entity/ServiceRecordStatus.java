package com.yxrobot.entity;

/**
 * 服务记录状态枚举
 * 对应数据库中的service_status字段
 */
public enum ServiceRecordStatus {
    IN_PROGRESS("in_progress", "进行中"),
    COMPLETED("completed", "已完成"),
    CANCELLED("cancelled", "已取消");
    
    private final String code;
    private final String description;
    
    ServiceRecordStatus(String code, String description) {
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
     * 根据代码获取枚举值
     */
    public static ServiceRecordStatus fromCode(String code) {
        for (ServiceRecordStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown service record status code: " + code);
    }
}