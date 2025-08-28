package com.yxrobot.exception;

import java.util.Map;

/**
 * 新闻数据验证异常
 * 当新闻数据验证失败时抛出此异常
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
public class NewsValidationException extends RuntimeException {
    
    private final String field;
    private final Object value;
    private final Map<String, String> validationErrors;
    
    public NewsValidationException(String message) {
        super(message);
        this.field = null;
        this.value = null;
        this.validationErrors = null;
    }
    
    public NewsValidationException(String field, Object value, String message) {
        super(message);
        this.field = field;
        this.value = value;
        this.validationErrors = null;
    }
    
    public NewsValidationException(Map<String, String> validationErrors) {
        super("新闻数据验证失败");
        this.field = null;
        this.value = null;
        this.validationErrors = validationErrors;
    }
    
    public NewsValidationException(String message, Map<String, String> validationErrors) {
        super(message);
        this.field = null;
        this.value = null;
        this.validationErrors = validationErrors;
    }
    
    public NewsValidationException(String message, Throwable cause) {
        super(message, cause);
        this.field = null;
        this.value = null;
        this.validationErrors = null;
    }
    
    public String getField() {
        return field;
    }
    
    public Object getValue() {
        return value;
    }
    
    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }
}