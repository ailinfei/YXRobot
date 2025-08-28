package com.yxrobot.entity;

/**
 * 互动类型枚举
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
public enum InteractionType {
    
    /**
     * 浏览
     */
    VIEW("view", "浏览"),
    
    /**
     * 点赞
     */
    LIKE("like", "点赞"),
    
    /**
     * 评论
     */
    COMMENT("comment", "评论"),
    
    /**
     * 分享
     */
    SHARE("share", "分享");
    
    private final String code;
    private final String description;
    
    InteractionType(String code, String description) {
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
     * 根据代码获取互动类型
     */
    public static InteractionType fromCode(String code) {
        for (InteractionType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的互动类型代码: " + code);
    }
}