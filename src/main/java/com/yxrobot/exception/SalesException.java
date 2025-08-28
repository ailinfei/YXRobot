package com.yxrobot.exception;

/**
 * 销售业务异常基类
 * 所有销售相关的业务异常都应该继承此类
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-27
 */
public class SalesException extends RuntimeException {
    
    private final String errorCode;
    private final Object errorDetails;
    
    public SalesException(String message) {
        super(message);
        this.errorCode = null;
        this.errorDetails = null;
    }
    
    public SalesException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.errorDetails = null;
    }
    
    public SalesException(String errorCode, String message, Object errorDetails) {
        super(message);
        this.errorCode = errorCode;
        this.errorDetails = errorDetails;
    }
    
    public SalesException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
        this.errorDetails = null;
    }
    
    public SalesException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.errorDetails = null;
    }
    
    public SalesException(String errorCode, String message, Object errorDetails, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.errorDetails = errorDetails;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public Object getErrorDetails() {
        return errorDetails;
    }
}