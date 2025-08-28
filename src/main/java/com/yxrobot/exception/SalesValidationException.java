package com.yxrobot.exception;

/**
 * 销售数据验证异常
 * 当销售数据验证失败时抛出此异常
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-26
 */
public class SalesValidationException extends RuntimeException {
    
    private final String field;
    private final Object value;
    
    public SalesValidationException(String message) {
        super(message);
        this.field = null;
        this.value = null;
    }
    
    public SalesValidationException(String field, Object value, String message) {
        super(message);
        this.field = field;
        this.value = value;
    }
    
    public SalesValidationException(String message, Throwable cause) {
        super(message, cause);
        this.field = null;
        this.value = null;
    }
    
    public SalesValidationException(String field, Object value, String message, Throwable cause) {
        super(message, cause);
        this.field = field;
        this.value = value;
    }
    
    public String getField() {
        return field;
    }
    
    public Object getValue() {
        return value;
    }
}