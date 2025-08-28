package com.yxrobot.entity;

/**
 * 新闻状态枚举
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
public enum NewsStatus {
    
    /**
     * 草稿
     */
    DRAFT("draft", "草稿"),
    
    /**
     * 已发布
     */
    PUBLISHED("published", "已发布"),
    
    /**
     * 已下线
     */
    OFFLINE("offline", "已下线");
    
    private final String code;
    private final String description;
    
    NewsStatus(String code, String description) {
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
     * 根据代码获取状态
     */
    public static NewsStatus fromCode(String code) {
        for (NewsStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的新闻状态代码: " + code);
    }
    
    /**
     * 检查是否可以转换到目标状态
     */
    public boolean canTransitionTo(NewsStatus targetStatus) {
        switch (this) {
            case DRAFT:
                return targetStatus == PUBLISHED;
            case PUBLISHED:
                return targetStatus == OFFLINE;
            case OFFLINE:
                return targetStatus == PUBLISHED || targetStatus == DRAFT;
            default:
                return false;
        }
    }
}