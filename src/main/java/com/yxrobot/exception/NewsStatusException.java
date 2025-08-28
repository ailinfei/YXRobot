package com.yxrobot.exception;

import com.yxrobot.entity.NewsStatus;

/**
 * 新闻状态异常
 * 当新闻状态转换不合法时抛出此异常
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
public class NewsStatusException extends RuntimeException {
    
    private final Long newsId;
    private final NewsStatus currentStatus;
    private final NewsStatus targetStatus;
    
    public NewsStatusException(Long newsId, NewsStatus currentStatus, NewsStatus targetStatus) {
        super(String.format("新闻状态转换不合法，新闻ID: %d，当前状态: %s，目标状态: %s", 
                           newsId, currentStatus, targetStatus));
        this.newsId = newsId;
        this.currentStatus = currentStatus;
        this.targetStatus = targetStatus;
    }
    
    public NewsStatusException(Long newsId, NewsStatus currentStatus, NewsStatus targetStatus, String message) {
        super(message);
        this.newsId = newsId;
        this.currentStatus = currentStatus;
        this.targetStatus = targetStatus;
    }
    
    public NewsStatusException(String message) {
        super(message);
        this.newsId = null;
        this.currentStatus = null;
        this.targetStatus = null;
    }
    
    public NewsStatusException(String message, Throwable cause) {
        super(message, cause);
        this.newsId = null;
        this.currentStatus = null;
        this.targetStatus = null;
    }
    
    public Long getNewsId() {
        return newsId;
    }
    
    public NewsStatus getCurrentStatus() {
        return currentStatus;
    }
    
    public NewsStatus getTargetStatus() {
        return targetStatus;
    }
}