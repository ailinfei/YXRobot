package com.yxrobot.entity;

/**
 * 客户类型枚举
 * 定义客户的类型分类
 * 用于兼容现有系统的客户类型字段
 */
public enum CustomerType {
    
    INDIVIDUAL("individual", "个人客户", "个人用户"),
    ENTERPRISE("enterprise", "企业客户", "企业用户"),
    EDUCATION("education", "教育客户", "学校、培训机构等教育用户"),
    GOVERNMENT("government", "政府客户", "政府机构用户"),
    PARTNER("partner", "合作伙伴", "业务合作伙伴"),
    DISTRIBUTOR("distributor", "经销商", "产品经销商"),
    OTHER("other", "其他", "其他类型客户");
    
    private final String code;
    private final String name;
    private final String description;
    
    CustomerType(String code, String name, String description) {
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
     * 获取枚举值（用于类型处理器）
     */
    public String getValue() {
        return code;
    }
    
    /**
     * 根据值获取枚举（用于类型处理器）
     */
    public static CustomerType fromValue(String value) {
        return fromCode(value);
    }
    
    /**
     * 根据代码获取枚举值
     */
    public static CustomerType fromCode(String code) {
        if (code == null) {
            return null;
        }
        
        for (CustomerType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        
        throw new IllegalArgumentException("未知的客户类型代码: " + code);
    }
    
    /**
     * 根据名称获取枚举值
     */
    public static CustomerType fromName(String name) {
        if (name == null) {
            return null;
        }
        
        for (CustomerType type : values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        
        throw new IllegalArgumentException("未知的客户类型名称: " + name);
    }
    
    @Override
    public String toString() {
        return name;
    }
}