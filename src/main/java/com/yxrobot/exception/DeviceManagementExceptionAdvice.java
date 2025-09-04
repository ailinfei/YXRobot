package com.yxrobot.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 设备管理专用异常处理器
 * 专门处理设备管理模块的ManagedDeviceException异常
 * 使用@Order(1)确保优先级高于全局异常处理器
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@RestControllerAdvice(basePackages = {"com.yxrobot.controller"})
@Order(1) // 确保优先级高于全局异常处理器
public class DeviceManagementExceptionAdvice {
    
    private static final Logger logger = LoggerFactory.getLogger(DeviceManagementExceptionAdvice.class);
    private static final Logger securityLogger = LoggerFactory.getLogger("SECURITY");
    
    /**
     * 处理设备管理业务异常
     * 
     * @param e ManagedDeviceException
     * @return 统一错误响应
     */
    @ExceptionHandler(ManagedDeviceException.class)
    public ResponseEntity<Map<String, Object>> handleManagedDeviceException(ManagedDeviceException e) {
        logger.error("设备管理业务异常: {}", e.getMessage(), e);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", getHttpStatusByErrorCode(e.getErrorCode()).value());
        response.put("message", sanitizeErrorMessage(e.getMessage()));
        
        // 添加详细错误信息（如果有）
        if (e.getDetails() != null) {
            response.put("details", sanitizeErrorDetails(e.getDetails()));
        }
        
        // 添加错误代码
        if (e.getErrorCode() != null) {
            response.put("errorCode", e.getErrorCode());
        }
        
        // 记录安全日志
        logSecurityEvent("DEVICE_BUSINESS_EXCEPTION", e.getErrorCode(), e.getMessage());
        
        return ResponseEntity.status(getHttpStatusByErrorCode(e.getErrorCode())).body(response);
    }
    
    /**
     * 根据错误代码获取HTTP状态码
     * 
     * @param errorCode 错误代码
     * @return HTTP状态码
     */
    private HttpStatus getHttpStatusByErrorCode(String errorCode) {
        if (errorCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        
        switch (errorCode) {
            case "MANAGED_DEVICE_NOT_FOUND":
            case "CUSTOMER_NOT_FOUND":
                return HttpStatus.NOT_FOUND;
                
            case "SERIAL_NUMBER_EXISTS":
            case "INVALID_STATUS_TRANSITION":
            case "BUSINESS_RULE_VIOLATION":
                return HttpStatus.CONFLICT;
                
            case "VALIDATION_FAILED":
            case "INVALID_FIRMWARE_VERSION":
            case "UNSUPPORTED_DEVICE_MODEL":
                return HttpStatus.BAD_REQUEST;
                
            case "PERMISSION_DENIED":
                return HttpStatus.FORBIDDEN;
                
            case "DEVICE_OPERATION_FAILED":
            case "BATCH_OPERATION_PARTIAL_FAILURE":
                return HttpStatus.UNPROCESSABLE_ENTITY;
                
            case "SYSTEM_ERROR":
            default:
                return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
    
    /**
     * 清理错误消息，移除敏感信息
     * 
     * @param message 原始错误消息
     * @return 清理后的错误消息
     */
    private String sanitizeErrorMessage(String message) {
        if (message == null) {
            return "未知错误";
        }
        
        // 移除可能的敏感信息
        String sanitized = message;
        
        // 替换可能的PII信息
        sanitized = sanitized.replaceAll("\\b\\d{11}\\b", "[手机号]"); // 手机号
        sanitized = sanitized.replaceAll("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b", "[邮箱]"); // 邮箱
        sanitized = sanitized.replaceAll("\\b\\d{15,20}\\b", "[身份证号]"); // 身份证号
        
        // 移除SQL相关信息
        sanitized = sanitized.replaceAll("(?i)\\b(select|insert|update|delete|drop|create|alter)\\b", "[SQL操作]");
        
        // 移除文件路径信息
        sanitized = sanitized.replaceAll("(?i)[a-z]:\\\\[^\\s]+", "[文件路径]");
        sanitized = sanitized.replaceAll("(?i)/[^\\s]+", "[文件路径]");
        
        return sanitized;
    }
    
    /**
     * 清理错误详情，移除敏感信息
     * 
     * @param details 原始错误详情
     * @return 清理后的错误详情
     */
    private Object sanitizeErrorDetails(Object details) {
        if (details == null) {
            return null;
        }
        
        // 如果是字符串，直接清理
        if (details instanceof String) {
            return sanitizeErrorMessage((String) details);
        }
        
        // 如果是数组，清理每个元素
        if (details instanceof Object[]) {
            Object[] array = (Object[]) details;
            Object[] sanitized = new Object[array.length];
            for (int i = 0; i < array.length; i++) {
                sanitized[i] = sanitizeErrorDetails(array[i]);
            }
            return sanitized;
        }
        
        // 其他类型直接返回（数字、布尔值等）
        return details;
    }
    
    /**
     * 记录安全事件日志
     * 
     * @param eventType 事件类型
     * @param errorCode 错误代码
     * @param message 消息
     */
    private void logSecurityEvent(String eventType, String errorCode, String message) {
        // 创建安全日志记录
        Map<String, Object> securityLog = new HashMap<>();
        securityLog.put("eventType", eventType);
        securityLog.put("errorCode", errorCode);
        securityLog.put("message", sanitizeErrorMessage(message));
        securityLog.put("timestamp", System.currentTimeMillis());
        securityLog.put("source", "DeviceManagementExceptionAdvice");
        
        // 记录到安全日志（使用专门的安全日志记录器）
        securityLogger.info("Device Management Security Event: {}", securityLog);
    }
}