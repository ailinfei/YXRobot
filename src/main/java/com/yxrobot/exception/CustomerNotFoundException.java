package com.yxrobot.exception;

/**
 * 客户未找到异常
 * 当根据ID查找客户但客户不存在时抛出此异常
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-27
 */
public class CustomerNotFoundException extends RuntimeException {
    
    private final Long customerId;
    
    public CustomerNotFoundException(Long customerId) {
        super("客户不存在，ID: " + customerId);
        this.customerId = customerId;
    }
    
    public CustomerNotFoundException(Long customerId, String message) {
        super(message);
        this.customerId = customerId;
    }
    
    public CustomerNotFoundException(String message) {
        super(message);
        this.customerId = null;
    }
    
    public CustomerNotFoundException(Long customerId, String message, Throwable cause) {
        super(message, cause);
        this.customerId = customerId;
    }
    
    public Long getCustomerId() {
        return customerId;
    }
}