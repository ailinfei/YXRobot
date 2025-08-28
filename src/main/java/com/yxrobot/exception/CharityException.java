package com.yxrobot.exception;

/**
 * 公益项目管理业务异常类
 * 用于处理公益项目管理相关的业务异常，提供友好的错误信息
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-19
 */
public class CharityException extends RuntimeException {
    
    /**
     * 异常错误码
     */
    private String errorCode;
    
    /**
     * 异常详细信息
     */
    private String details;
    
    /**
     * 构造函数
     * 
     * @param message 异常消息
     */
    public CharityException(String message) {
        super(message);
    }
    
    /**
     * 构造函数
     * 
     * @param message 异常消息
     * @param cause 原因异常
     */
    public CharityException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * 构造函数
     * 
     * @param errorCode 错误码
     * @param message 异常消息
     */
    public CharityException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    /**
     * 构造函数
     * 
     * @param errorCode 错误码
     * @param message 异常消息
     * @param details 详细信息
     */
    public CharityException(String errorCode, String message, String details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details;
    }
    
    /**
     * 构造函数
     * 
     * @param errorCode 错误码
     * @param message 异常消息
     * @param cause 原因异常
     */
    public CharityException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    /**
     * 构造函数
     * 
     * @param errorCode 错误码
     * @param message 异常消息
     * @param details 详细信息
     * @param cause 原因异常
     */
    public CharityException(String errorCode, String message, String details, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.details = details;
    }
    
    /**
     * 获取错误码
     * 
     * @return 错误码
     */
    public String getErrorCode() {
        return errorCode;
    }
    
    /**
     * 设置错误码
     * 
     * @param errorCode 错误码
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    /**
     * 获取详细信息
     * 
     * @return 详细信息
     */
    public String getDetails() {
        return details;
    }
    
    /**
     * 设置详细信息
     * 
     * @param details 详细信息
     */
    public void setDetails(String details) {
        this.details = details;
    }
    
    // 常用的静态工厂方法
    
    /**
     * 创建数据验证异常
     * 
     * @param message 异常消息
     * @return CharityException实例
     */
    public static CharityException validationError(String message) {
        return new CharityException("CHARITY_VALIDATION_ERROR", message);
    }
    
    /**
     * 创建数据验证异常
     * 
     * @param message 异常消息
     * @param details 详细信息
     * @return CharityException实例
     */
    public static CharityException validationError(String message, String details) {
        return new CharityException("CHARITY_VALIDATION_ERROR", message, details);
    }
    
    /**
     * 创建数据不存在异常
     * 
     * @param message 异常消息
     * @return CharityException实例
     */
    public static CharityException dataNotFound(String message) {
        return new CharityException("CHARITY_DATA_NOT_FOUND", message);
    }
    
    /**
     * 创建数据冲突异常
     * 
     * @param message 异常消息
     * @return CharityException实例
     */
    public static CharityException dataConflict(String message) {
        return new CharityException("CHARITY_DATA_CONFLICT", message);
    }
    
    /**
     * 创建业务规则异常
     * 
     * @param message 异常消息
     * @return CharityException实例
     */
    public static CharityException businessRuleViolation(String message) {
        return new CharityException("CHARITY_BUSINESS_RULE_VIOLATION", message);
    }
    
    /**
     * 创建业务规则异常
     * 
     * @param message 异常消息
     * @param details 详细信息
     * @return CharityException实例
     */
    public static CharityException businessRuleViolation(String message, String details) {
        return new CharityException("CHARITY_BUSINESS_RULE_VIOLATION", message, details);
    }
    
    /**
     * 创建操作失败异常
     * 
     * @param message 异常消息
     * @return CharityException实例
     */
    public static CharityException operationFailed(String message) {
        return new CharityException("CHARITY_OPERATION_FAILED", message);
    }
    
    /**
     * 创建操作失败异常
     * 
     * @param message 异常消息
     * @param cause 原因异常
     * @return CharityException实例
     */
    public static CharityException operationFailed(String message, Throwable cause) {
        return new CharityException("CHARITY_OPERATION_FAILED", message, cause);
    }
    
    /**
     * 创建统计数据异常
     * 
     * @param message 异常消息
     * @return CharityException实例
     */
    public static CharityException statsError(String message) {
        return new CharityException("CHARITY_STATS_ERROR", message);
    }
    
    /**
     * 创建统计数据异常
     * 
     * @param message 异常消息
     * @param details 详细信息
     * @return CharityException实例
     */
    public static CharityException statsError(String message, String details) {
        return new CharityException("CHARITY_STATS_ERROR", message, details);
    }
    
    /**
     * 创建机构管理异常
     * 
     * @param message 异常消息
     * @return CharityException实例
     */
    public static CharityException institutionError(String message) {
        return new CharityException("CHARITY_INSTITUTION_ERROR", message);
    }
    
    /**
     * 创建活动管理异常
     * 
     * @param message 异常消息
     * @return CharityException实例
     */
    public static CharityException activityError(String message) {
        return new CharityException("CHARITY_ACTIVITY_ERROR", message);
    }
    
    /**
     * 创建图表数据异常
     * 
     * @param message 异常消息
     * @return CharityException实例
     */
    public static CharityException chartError(String message) {
        return new CharityException("CHARITY_CHART_ERROR", message);
    }
    
    /**
     * 创建缓存错误异常
     * 
     * @param message 异常消息
     * @return CharityException实例
     */
    public static CharityException cacheError(String message) {
        return new CharityException("CACHE_ERROR", message);
    }
    
    /**
     * 创建缓存错误异常
     * 
     * @param message 异常消息
     * @param cause 原因异常
     * @return CharityException实例
     */
    public static CharityException cacheError(String message, Throwable cause) {
        return new CharityException("CACHE_ERROR", message, cause);
    }
    
    /**
     * 创建操作失败异常（带详细信息）
     * 
     * @param operation 操作名称
     * @param details 详细信息
     * @return CharityException实例
     */
    public static CharityException operationFailed(String operation, String details) {
        return new CharityException("OPERATION_FAILED", operation + "操作失败: " + details);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CharityException{");
        sb.append("errorCode='").append(errorCode).append('\'');
        sb.append(", message='").append(getMessage()).append('\'');
        if (details != null) {
            sb.append(", details='").append(details).append('\'');
        }
        sb.append('}');
        return sb.toString();
    }
}