package com.yxrobot.entity;

/**
 * 服务角色枚举（客户在服务中的角色）
 * 对应数据库中的customer_role字段（在服务关联表中）
 */
public enum ServiceRole {
    REQUESTER("requester", "服务请求者"),
    CONTACT("contact", "联系人"),
    BENEFICIARY("beneficiary", "受益人");
    
    private final String code;
    private final String description;
    
    ServiceRole(String code, String description) {
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
    public static ServiceRole fromCode(String code) {
        for (ServiceRole role : values()) {
            if (role.code.equals(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown service role code: " + code);
    }
}