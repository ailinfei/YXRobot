package com.yxrobot.exception;

/**
 * 客户管理模块业务异常类
 * 用于处理客户相关的业务异常情况
 */
public class CustomerException extends RuntimeException {
    
    private final String errorCode;
    private final Object data;
    
    public CustomerException(String message) {
        super(message);
        this.errorCode = "CUSTOMER_ERROR";
        this.data = null;
    }
    
    public CustomerException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.data = null;
    }
    
    public CustomerException(String errorCode, String message, Object data) {
        super(message);
        this.errorCode = errorCode;
        this.data = data;
    }
    
    public CustomerException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "CUSTOMER_ERROR";
        this.data = null;
    }
    
    public CustomerException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.data = null;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public Object getData() {
        return data;
    }
    
    // 常用的客户异常类型
    public static class CustomerNotFoundException extends CustomerException {
        public CustomerNotFoundException(Long customerId) {
            super("CUSTOMER_NOT_FOUND", "客户不存在，ID: " + customerId, customerId);
        }
        
        public CustomerNotFoundException(String identifier) {
            super("CUSTOMER_NOT_FOUND", "客户不存在，标识: " + identifier, identifier);
        }
    }
    
    public static class CustomerAlreadyExistsException extends CustomerException {
        public CustomerAlreadyExistsException(String field, String value) {
            super("CUSTOMER_ALREADY_EXISTS", 
                  String.format("客户已存在，%s: %s", field, value), 
                  new Object[]{field, value});
        }
    }
    
    public static class InvalidCustomerDataException extends CustomerException {
        public InvalidCustomerDataException(String field, String message) {
            super("INVALID_CUSTOMER_DATA", 
                  String.format("客户数据无效，%s: %s", field, message), 
                  field);
        }
    }
    
    public static class CustomerOperationNotAllowedException extends CustomerException {
        public CustomerOperationNotAllowedException(String operation, String reason) {
            super("CUSTOMER_OPERATION_NOT_ALLOWED", 
                  String.format("客户操作不被允许，操作: %s，原因: %s", operation, reason), 
                  operation);
        }
    }
    
    public static class CustomerDataIntegrityException extends CustomerException {
        public CustomerDataIntegrityException(String message) {
            super("CUSTOMER_DATA_INTEGRITY_ERROR", "客户数据完整性错误: " + message);
        }
        
        public CustomerDataIntegrityException(String message, Throwable cause) {
            super("CUSTOMER_DATA_INTEGRITY_ERROR", "客户数据完整性错误: " + message, cause);
        }
    }
    
    public static class CustomerServiceException extends CustomerException {
        public CustomerServiceException(String service, String message) {
            super("CUSTOMER_SERVICE_ERROR", 
                  String.format("客户服务错误，服务: %s，消息: %s", service, message), 
                  service);
        }
        
        public CustomerServiceException(String service, String message, Throwable cause) {
            super("CUSTOMER_SERVICE_ERROR", 
                  String.format("客户服务错误，服务: %s，消息: %s", service, message), 
                  cause);
        }
    }
    
    public static class CustomerValidationException extends CustomerException {
        public CustomerValidationException(String field, String value, String rule) {
            super("CUSTOMER_VALIDATION_ERROR", 
                  String.format("客户数据验证失败，字段: %s，值: %s，规则: %s", field, value, rule), 
                  new Object[]{field, value, rule});
        }
    }
    
    public static class CustomerPermissionException extends CustomerException {
        public CustomerPermissionException(String operation) {
            super("CUSTOMER_PERMISSION_DENIED", 
                  "客户操作权限不足，操作: " + operation, 
                  operation);
        }
    }
    
    public static class CustomerConcurrencyException extends CustomerException {
        public CustomerConcurrencyException(Long customerId) {
            super("CUSTOMER_CONCURRENCY_ERROR", 
                  "客户数据并发冲突，ID: " + customerId, 
                  customerId);
        }
    }
    
    public static class CustomerExternalServiceException extends CustomerException {
        public CustomerExternalServiceException(String service, String message) {
            super("CUSTOMER_EXTERNAL_SERVICE_ERROR", 
                  String.format("外部服务调用失败，服务: %s，消息: %s", service, message), 
                  service);
        }
        
        public CustomerExternalServiceException(String service, String message, Throwable cause) {
            super("CUSTOMER_EXTERNAL_SERVICE_ERROR", 
                  String.format("外部服务调用失败，服务: %s，消息: %s", service, message), 
                  cause);
        }
    }
}