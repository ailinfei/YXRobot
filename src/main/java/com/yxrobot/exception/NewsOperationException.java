package com.yxrobot.exception;

/**
 * 新闻操作异常
 * 当新闻操作失败时抛出此异常
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
public class NewsOperationException extends RuntimeException {
    
    private final String operation;
    private final Long newsId;
    private final String errorCode;
    
    public NewsOperationException(String operation, String message) {
        super(message);
        this.operation = operation;
        this.newsId = null;
        this.errorCode = null;
    }
    
    public NewsOperationException(String operation, Long newsId, String message) {
        super(message);
        this.operation = operation;
        this.newsId = newsId;
        this.errorCode = null;
    }
    
    public NewsOperationException(String operation, Long newsId, String message, String errorCode) {
        super(message);
        this.operation = operation;
        this.newsId = newsId;
        this.errorCode = errorCode;
    }
    
    public NewsOperationException(String operation, String message, Throwable cause) {
        super(message, cause);
        this.operation = operation;
        this.newsId = null;
        this.errorCode = null;
    }
    
    public NewsOperationException(String operation, Long newsId, String message, Throwable cause) {
        super(message, cause);
        this.operation = operation;
        this.newsId = newsId;
        this.errorCode = null;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public Long getNewsId() {
        return newsId;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}