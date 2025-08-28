package com.yxrobot.exception;

/**
 * 平台链接业务异常类
 * 用于处理平台链接相关的业务异常
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-22
 */
public class PlatformLinkException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 错误码
     */
    private String errorCode;
    
    /**
     * 错误详情
     */
    private Object errorDetails;
    
    /**
     * 默认构造函数
     */
    public PlatformLinkException() {
        super();
    }
    
    /**
     * 带消息的构造函数
     * 
     * @param message 错误消息
     */
    public PlatformLinkException(String message) {
        super(message);
    }
    
    /**
     * 带消息和原因的构造函数
     * 
     * @param message 错误消息
     * @param cause 原因
     */
    public PlatformLinkException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * 带错误码和消息的构造函数
     * 
     * @param errorCode 错误码
     * @param message 错误消息
     */
    public PlatformLinkException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    /**
     * 带错误码、消息和详情的构造函数
     * 
     * @param errorCode 错误码
     * @param message 错误消息
     * @param errorDetails 错误详情
     */
    public PlatformLinkException(String errorCode, String message, Object errorDetails) {
        super(message);
        this.errorCode = errorCode;
        this.errorDetails = errorDetails;
    }
    
    /**
     * 带错误码、消息、详情和原因的构造函数
     * 
     * @param errorCode 错误码
     * @param message 错误消息
     * @param errorDetails 错误详情
     * @param cause 原因
     */
    public PlatformLinkException(String errorCode, String message, Object errorDetails, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.errorDetails = errorDetails;
    }
    
    // Getters and Setters
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    public Object getErrorDetails() {
        return errorDetails;
    }
    
    public void setErrorDetails(Object errorDetails) {
        this.errorDetails = errorDetails;
    }
    
    /**
     * 常用错误码定义
     */
    public static class ErrorCodes {
        public static final String LINK_NOT_FOUND = "LINK_NOT_FOUND";
        public static final String LINK_ALREADY_EXISTS = "LINK_ALREADY_EXISTS";
        public static final String INVALID_URL_FORMAT = "INVALID_URL_FORMAT";
        public static final String INVALID_REGION_LANGUAGE = "INVALID_REGION_LANGUAGE";
        public static final String VALIDATION_FAILED = "VALIDATION_FAILED";
        public static final String REGION_CONFIG_NOT_FOUND = "REGION_CONFIG_NOT_FOUND";
        public static final String LINK_VALIDATION_ERROR = "LINK_VALIDATION_ERROR";
        public static final String CLICK_RECORD_ERROR = "CLICK_RECORD_ERROR";
        public static final String STATS_CALCULATION_ERROR = "STATS_CALCULATION_ERROR";
        public static final String CACHE_ERROR = "CACHE_ERROR";
        public static final String DATABASE_ERROR = "DATABASE_ERROR";
    }
    
    /**
     * 创建链接不存在异常
     * 
     * @param linkId 链接ID
     * @return 异常实例
     */
    public static PlatformLinkException linkNotFound(Long linkId) {
        return new PlatformLinkException(
            ErrorCodes.LINK_NOT_FOUND,
            "链接不存在，ID: " + linkId,
            linkId
        );
    }
    
    /**
     * 创建链接已存在异常
     * 
     * @param platformName 平台名称
     * @param region 地区
     * @param languageCode 语言代码
     * @return 异常实例
     */
    public static PlatformLinkException linkAlreadyExists(String platformName, String region, String languageCode) {
        return new PlatformLinkException(
            ErrorCodes.LINK_ALREADY_EXISTS,
            "该平台在指定地区和语言下的链接已存在: " + platformName + " - " + region + " - " + languageCode,
            new String[]{platformName, region, languageCode}
        );
    }
    
    /**
     * 创建无效URL格式异常
     * 
     * @param url URL地址
     * @return 异常实例
     */
    public static PlatformLinkException invalidUrlFormat(String url) {
        return new PlatformLinkException(
            ErrorCodes.INVALID_URL_FORMAT,
            "无效的URL格式: " + url,
            url
        );
    }
    
    /**
     * 创建无效地区语言组合异常
     * 
     * @param region 地区
     * @param languageCode 语言代码
     * @return 异常实例
     */
    public static PlatformLinkException invalidRegionLanguage(String region, String languageCode) {
        return new PlatformLinkException(
            ErrorCodes.INVALID_REGION_LANGUAGE,
            "无效的地区和语言组合: " + region + " - " + languageCode,
            new String[]{region, languageCode}
        );
    }
    
    /**
     * 创建数据验证失败异常
     * 
     * @param fieldName 字段名
     * @param fieldValue 字段值
     * @param validationMessage 验证消息
     * @return 异常实例
     */
    public static PlatformLinkException validationFailed(String fieldName, Object fieldValue, String validationMessage) {
        return new PlatformLinkException(
            ErrorCodes.VALIDATION_FAILED,
            "数据验证失败: " + fieldName + " = " + fieldValue + ", " + validationMessage,
            new Object[]{fieldName, fieldValue, validationMessage}
        );
    }
    
    /**
     * 创建链接验证错误异常
     * 
     * @param linkId 链接ID
     * @param errorMessage 错误消息
     * @return 异常实例
     */
    public static PlatformLinkException linkValidationError(Long linkId, String errorMessage) {
        return new PlatformLinkException(
            ErrorCodes.LINK_VALIDATION_ERROR,
            "链接验证失败，ID: " + linkId + ", 错误: " + errorMessage,
            new Object[]{linkId, errorMessage}
        );
    }
    
    /**
     * 创建统计计算错误异常
     * 
     * @param statsType 统计类型
     * @param errorMessage 错误消息
     * @return 异常实例
     */
    public static PlatformLinkException statsCalculationError(String statsType, String errorMessage) {
        return new PlatformLinkException(
            ErrorCodes.STATS_CALCULATION_ERROR,
            "统计数据计算失败: " + statsType + ", 错误: " + errorMessage,
            new Object[]{statsType, errorMessage}
        );
    }
}