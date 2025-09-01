package com.yxrobot.enums;

/**
 * 客户类型枚举
 * 对应数据库 rental_customers.customer_type 字段
 * 
 * @author Kiro
 * @date 2025-01-28
 */
public enum CustomerType {
    
    /**
     * 个人
     */
    INDIVIDUAL("individual", "个人"),
    
    /**
     * 企业
     */
    ENTERPRISE("enterprise", "企业"),
    
    /**
     * 机构
     */
    INSTITUTION("institution", "机构");
    
    private final String code;
    private final String description;
    
    CustomerType(String code, String description) {
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
    public static CustomerType fromCode(String code) {
        for (CustomerType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown customer type code: " + code);
    }
}