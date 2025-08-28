package com.yxrobot.exception;

/**
 * 产品未找到异常
 * 当根据ID查找产品但产品不存在时抛出此异常
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-27
 */
public class ProductNotFoundException extends RuntimeException {
    
    private final Long productId;
    
    public ProductNotFoundException(Long productId) {
        super("产品不存在，ID: " + productId);
        this.productId = productId;
    }
    
    public ProductNotFoundException(Long productId, String message) {
        super(message);
        this.productId = productId;
    }
    
    public ProductNotFoundException(String message) {
        super(message);
        this.productId = null;
    }
    
    public ProductNotFoundException(Long productId, String message, Throwable cause) {
        super(message, cause);
        this.productId = productId;
    }
    
    public Long getProductId() {
        return productId;
    }
}