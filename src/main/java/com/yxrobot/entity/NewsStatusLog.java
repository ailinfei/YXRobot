package com.yxrobot.entity;

import java.time.LocalDateTime;

/**
 * 新闻状态变更日志实体类
 * 记录新闻状态变更的历史信息
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
public class NewsStatusLog {
    
    /**
     * 日志ID
     */
    private Long id;
    
    /**
     * 新闻ID
     */
    private Long newsId;
    
    /**
     * 原状态
     */
    private NewsStatus fromStatus;
    
    /**
     * 新状态
     */
    private NewsStatus toStatus;
    
    /**
     * 变更原因
     */
    private String reason;
    
    /**
     * 操作人
     */
    private String operator;
    
    /**
     * 变更时间
     */
    private LocalDateTime changeTime;
    
    /**
     * 备注信息
     */
    private String remark;
    
    // 构造函数
    public NewsStatusLog() {}
    
    public NewsStatusLog(Long newsId, NewsStatus fromStatus, NewsStatus toStatus, String reason, String operator) {
        this.newsId = newsId;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.reason = reason;
        this.operator = operator;
        this.changeTime = LocalDateTime.now();
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getNewsId() {
        return newsId;
    }
    
    public void setNewsId(Long newsId) {
        this.newsId = newsId;
    }
    
    public NewsStatus getFromStatus() {
        return fromStatus;
    }
    
    public void setFromStatus(NewsStatus fromStatus) {
        this.fromStatus = fromStatus;
    }
    
    public NewsStatus getToStatus() {
        return toStatus;
    }
    
    public void setToStatus(NewsStatus toStatus) {
        this.toStatus = toStatus;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public String getOperator() {
        return operator;
    }
    
    public void setOperator(String operator) {
        this.operator = operator;
    }
    
    public LocalDateTime getChangeTime() {
        return changeTime;
    }
    
    public void setChangeTime(LocalDateTime changeTime) {
        this.changeTime = changeTime;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    @Override
    public String toString() {
        return "NewsStatusLog{" +
                "id=" + id +
                ", newsId=" + newsId +
                ", fromStatus=" + fromStatus +
                ", toStatus=" + toStatus +
                ", reason='" + reason + '\'' +
                ", operator='" + operator + '\'' +
                ", changeTime=" + changeTime +
                ", remark='" + remark + '\'' +
                '}';
    }
}