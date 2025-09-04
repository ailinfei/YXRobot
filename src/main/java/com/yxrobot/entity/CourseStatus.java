package com.yxrobot.entity;

/**
 * 课程状态枚举
 * 定义课程的发布状态和生命周期
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-28
 */
public enum CourseStatus {
    
    /**
     * 草稿状态 - 课程正在编辑中，未发布
     */
    DRAFT("草稿", "课程正在编辑中，未对外发布"),
    
    /**
     * 已发布 - 课程已发布，可供机器人下载使用
     */
    PUBLISHED("已发布", "课程已发布，可供机器人下载使用"),
    
    /**
     * 已归档 - 课程已停用，不再提供下载
     */
    ARCHIVED("已归档", "课程已停用，不再提供下载"),
    
    /**
     * 维护中 - 课程暂时下线维护
     */
    MAINTENANCE("维护中", "课程暂时下线进行内容维护");
    
    private final String displayName;
    private final String description;
    
    CourseStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据显示名称获取课程状态
     * 
     * @param displayName 显示名称
     * @return 课程状态，如果未找到返回null
     */
    public static CourseStatus fromDisplayName(String displayName) {
        for (CourseStatus status : CourseStatus.values()) {
            if (status.displayName.equals(displayName)) {
                return status;
            }
        }
        return null;
    }
    
    /**
     * 检查课程是否可用（已发布状态）
     * 
     * @return 是否可用
     */
    public boolean isAvailable() {
        return this == PUBLISHED;
    }
    
    /**
     * 检查课程是否可编辑（草稿或维护状态）
     * 
     * @return 是否可编辑
     */
    public boolean isEditable() {
        return this == DRAFT || this == MAINTENANCE;
    }
    
    /**
     * 检查课程是否已停用（归档状态）
     * 
     * @return 是否已停用
     */
    public boolean isDisabled() {
        return this == ARCHIVED;
    }
}