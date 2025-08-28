package com.yxrobot.exception;

/**
 * 新闻未找到异常
 * 当根据ID查找新闻但新闻不存在时抛出此异常
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
public class NewsNotFoundException extends RuntimeException {
    
    private final Long newsId;
    
    public NewsNotFoundException(Long newsId) {
        super("新闻不存在，ID: " + newsId);
        this.newsId = newsId;
    }
    
    public NewsNotFoundException(Long newsId, String message) {
        super(message);
        this.newsId = newsId;
    }
    
    public NewsNotFoundException(Long newsId, String message, Throwable cause) {
        super(message, cause);
        this.newsId = newsId;
    }
    
    public Long getNewsId() {
        return newsId;
    }
}