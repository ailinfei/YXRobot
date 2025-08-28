package com.yxrobot.exception;

/**
 * 销售操作异常
 * 当销售相关操作失败时抛出此异常
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-26
 */
public class SalesOperationException extends RuntimeException {
    
    private final String operation;
    private final Object target;
    
    public SalesOperationException(String message) {
        super(message);
        this.operation = null;
        this.target = null;
    }
    
    public SalesOperationException(String operation, Object target, String message) {
        super(message);
        this.operation = operation;
        this.target = target;
    }
    
    public SalesOperationException(String message, Throwable cause) {
        super(message, cause);
        this.operation = null;
        this.target = null;
    }
    
    public SalesOperationException(String operation, Object target, String message, Throwable cause) {
        super(message, cause);
        this.operation = operation;
        this.target = target;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public Object getTarget() {
        return target;
    }
}