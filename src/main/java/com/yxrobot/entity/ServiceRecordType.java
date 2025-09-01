package com.yxrobot.entity;

/**
 * 服务记录类型枚举
 * 对应数据库中的service_type字段
 */
public enum ServiceRecordType {
    MAINTENANCE("maintenance", "维护"),
    UPGRADE("upgrade", "升级"),
    CONSULTATION("consultation", "咨询"),
    COMPLAINT("complaint", "投诉");
    
    private final String code;
    private final String description;
    
    ServiceRecordType(String code, String description) {
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
    public static ServiceRecordType fromCode(String code) {
        for (ServiceRecordType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown service record type code: " + code);
    }
}