package com.yxrobot.exception;

/**
 * 订单业务异常类
 * 用于处理订单管理相关的业务异常
 * 
 * 异常分类：
 * - 数据验证异常
 * - 业务规则异常
 * - 状态流转异常
 * - 权限验证异常
 */
public class OrderException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 异常错误码
     */
    private String errorCode;
    
    /**
     * 异常详细信息
     */
    private Object details;
    
    /**
     * 构造函数
     */
    public OrderException(String message) {
        super(message);
    }
    
    /**
     * 构造函数（带错误码）
     */
    public OrderException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    /**
     * 构造函数（带错误码和详细信息）
     */
    public OrderException(String errorCode, String message, Object details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details;
    }
    
    /**
     * 构造函数（带原因）
     */
    public OrderException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * 构造函数（带错误码和原因）
     */
    public OrderException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    /**
     * 构造函数（完整参数）
     */
    public OrderException(String errorCode, String message, Object details, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.details = details;
    }
    
    // Getter方法
    public String getErrorCode() {
        return errorCode;
    }
    
    public Object getDetails() {
        return details;
    }
    
    // 常用的静态工厂方法
    
    /**
     * 订单不存在异常
     */
    public static OrderException orderNotFound(String orderNumber) {
        return new OrderException("ORDER_NOT_FOUND", 
            "订单不存在：" + orderNumber, orderNumber);
    }
    
    /**
     * 订单状态无效异常
     */
    public static OrderException invalidOrderStatus(String currentStatus, String targetStatus) {
        return new OrderException("INVALID_ORDER_STATUS", 
            String.format("订单状态无法从 %s 变更为 %s", currentStatus, targetStatus),
            new String[]{currentStatus, targetStatus});
    }
    
    /**
     * 订单数据验证失败异常
     */
    public static OrderException validationFailed(String field, String message) {
        return new OrderException("VALIDATION_FAILED", 
            String.format("字段 %s 验证失败：%s", field, message),
            new String[]{field, message});
    }
    
    /**
     * 客户信息无效异常
     */
    public static OrderException invalidCustomer(Long customerId) {
        return new OrderException("INVALID_CUSTOMER", 
            "客户信息无效或不存在：" + customerId, customerId);
    }
    
    /**
     * 产品信息无效异常
     */
    public static OrderException invalidProduct(Long productId) {
        return new OrderException("INVALID_PRODUCT", 
            "产品信息无效或不存在：" + productId, productId);
    }
    
    /**
     * 订单金额计算错误异常
     */
    public static OrderException amountCalculationError(String message) {
        return new OrderException("AMOUNT_CALCULATION_ERROR", 
            "订单金额计算错误：" + message, message);
    }
    
    /**
     * 库存不足异常
     */
    public static OrderException insufficientStock(Long productId, Integer requested, Integer available) {
        return new OrderException("INSUFFICIENT_STOCK", 
            String.format("产品 %d 库存不足，需要 %d，可用 %d", productId, requested, available),
            new Object[]{productId, requested, available});
    }
    
    /**
     * 订单已被锁定异常
     */
    public static OrderException orderLocked(String orderNumber) {
        return new OrderException("ORDER_LOCKED", 
            "订单已被锁定，无法修改：" + orderNumber, orderNumber);
    }
    
    /**
     * 权限不足异常
     */
    public static OrderException permissionDenied(String operation) {
        return new OrderException("PERMISSION_DENIED", 
            "权限不足，无法执行操作：" + operation, operation);
    }
    
    /**
     * 业务规则违反异常
     */
    public static OrderException businessRuleViolation(String rule, String message) {
        return new OrderException("BUSINESS_RULE_VIOLATION", 
            String.format("违反业务规则 %s：%s", rule, message),
            new String[]{rule, message});
    }
    
    /**
     * 并发操作冲突异常
     */
    public static OrderException concurrencyConflict(String orderNumber) {
        return new OrderException("CONCURRENCY_CONFLICT", 
            "订单正在被其他用户操作，请稍后重试：" + orderNumber, orderNumber);
    }
    
    /**
     * 数据完整性异常
     */
    public static OrderException dataIntegrityViolation(String message) {
        return new OrderException("DATA_INTEGRITY_VIOLATION", 
            "数据完整性违反：" + message, message);
    }
    
    /**
     * 外部服务调用失败异常
     */
    public static OrderException externalServiceError(String service, String message) {
        return new OrderException("EXTERNAL_SERVICE_ERROR", 
            String.format("外部服务 %s 调用失败：%s", service, message),
            new String[]{service, message});
    }
}