package com.yxrobot.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 租赁模块全局异常处理器
 * 提供友好的错误信息返回给前端
 * 
 * @author Kiro
 * @date 2025-01-28
 */
@RestControllerAdvice(basePackages = "com.yxrobot.controller")
public class RentalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(RentalExceptionHandler.class);
    
    /**
     * 处理租赁业务异常
     */
    @ExceptionHandler(RentalException.class)
    public ResponseEntity<Map<String, Object>> handleRentalException(RentalException ex, 
                                                                    HttpServletRequest request) {
        
        logger.warn("租赁业务异常: {} - {}", ex.getErrorCode(), ex.getMessage(), ex);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", ex.getMessage());
        response.put("data", null);
        response.put("errorCode", ex.getErrorCode());
        response.put("errorDetail", ex.getErrorDetail());
        response.put("timestamp", LocalDateTime.now());
        response.put("path", request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * 处理参数验证异常
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<Map<String, Object>> handleValidationException(Exception ex, 
                                                                         HttpServletRequest request) {
        
        logger.warn("参数验证异常: {}", ex.getMessage());
        
        String errorMessage = "参数验证失败";
        Map<String, String> fieldErrors = new HashMap<>();
        
        if (ex instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException validEx = (MethodArgumentNotValidException) ex;
            fieldErrors = validEx.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                    FieldError::getField,
                    FieldError::getDefaultMessage,
                    (existing, replacement) -> existing
                ));
        } else if (ex instanceof BindException) {
            BindException bindEx = (BindException) ex;
            fieldErrors = bindEx.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                    FieldError::getField,
                    FieldError::getDefaultMessage,
                    (existing, replacement) -> existing
                ));
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", errorMessage);
        response.put("data", null);
        response.put("errorCode", RentalException.ErrorCodes.DATA_VALIDATION_ERROR);
        response.put("fieldErrors", fieldErrors);
        response.put("timestamp", LocalDateTime.now());
        response.put("path", request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * 处理日期解析异常
     */
    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<Map<String, Object>> handleDateTimeParseException(DateTimeParseException ex, 
                                                                           HttpServletRequest request) {
        
        logger.warn("日期格式解析异常: {}", ex.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", "日期格式不正确，请使用YYYY-MM-DD格式");
        response.put("data", null);
        response.put("errorCode", RentalException.ErrorCodes.DATA_VALIDATION_ERROR);
        response.put("errorDetail", "无效的日期格式: " + ex.getParsedString());
        response.put("timestamp", LocalDateTime.now());
        response.put("path", request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * 处理数字格式异常
     */
    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<Map<String, Object>> handleNumberFormatException(NumberFormatException ex, 
                                                                          HttpServletRequest request) {
        
        logger.warn("数字格式异常: {}", ex.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", "数字格式不正确");
        response.put("data", null);
        response.put("errorCode", RentalException.ErrorCodes.DATA_VALIDATION_ERROR);
        response.put("errorDetail", ex.getMessage());
        response.put("timestamp", LocalDateTime.now());
        response.put("path", request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex, 
                                                                             HttpServletRequest request) {
        
        logger.warn("非法参数异常: {}", ex.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", "参数不合法: " + ex.getMessage());
        response.put("data", null);
        response.put("errorCode", RentalException.ErrorCodes.DATA_VALIDATION_ERROR);
        response.put("timestamp", LocalDateTime.now());
        response.put("path", request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Map<String, Object>> handleNullPointerException(NullPointerException ex, 
                                                                         HttpServletRequest request) {
        
        logger.error("空指针异常: {}", ex.getMessage(), ex);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 500);
        response.put("message", "系统内部错误，请稍后重试");
        response.put("data", null);
        response.put("errorCode", RentalException.ErrorCodes.SYSTEM_ERROR);
        response.put("timestamp", LocalDateTime.now());
        response.put("path", request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    /**
     * 处理数据库异常
     */
    @ExceptionHandler({org.springframework.dao.DataAccessException.class})
    public ResponseEntity<Map<String, Object>> handleDataAccessException(Exception ex, 
                                                                        HttpServletRequest request) {
        
        logger.error("数据库访问异常: {}", ex.getMessage(), ex);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 500);
        response.put("message", "数据库访问错误，请稍后重试");
        response.put("data", null);
        response.put("errorCode", RentalException.ErrorCodes.SYSTEM_ERROR);
        response.put("timestamp", LocalDateTime.now());
        response.put("path", request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    /**
     * 处理通用异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex, 
                                                                     HttpServletRequest request) {
        
        logger.error("未处理的异常: {}", ex.getMessage(), ex);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 500);
        response.put("message", "系统内部错误，请联系管理员");
        response.put("data", null);
        response.put("errorCode", RentalException.ErrorCodes.SYSTEM_ERROR);
        response.put("timestamp", LocalDateTime.now());
        response.put("path", request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    /**
     * 处理SQL异常
     */
    @ExceptionHandler({java.sql.SQLException.class, org.springframework.dao.DataIntegrityViolationException.class})
    public ResponseEntity<Map<String, Object>> handleSQLException(Exception ex, 
                                                                 HttpServletRequest request) {
        
        logger.error("SQL异常: {}", ex.getMessage(), ex);
        
        String userMessage = "数据操作失败";
        String errorCode = RentalException.ErrorCodes.SYSTEM_ERROR;
        
        // 根据具体的SQL异常类型提供更友好的错误信息
        if (ex.getMessage().contains("Duplicate entry")) {
            userMessage = "数据已存在，请检查唯一性约束";
            errorCode = RentalException.ErrorCodes.DUPLICATE_RENTAL_ORDER;
        } else if (ex.getMessage().contains("foreign key constraint")) {
            userMessage = "数据关联约束错误，请检查相关数据";
            errorCode = RentalException.ErrorCodes.DATA_VALIDATION_ERROR;
        } else if (ex.getMessage().contains("cannot be null")) {
            userMessage = "必填字段不能为空";
            errorCode = RentalException.ErrorCodes.REQUIRED_FIELD_MISSING;
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 500);
        response.put("message", userMessage);
        response.put("data", null);
        response.put("errorCode", errorCode);
        response.put("timestamp", LocalDateTime.now());
        response.put("path", request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    /**
     * 处理JSON解析异常
     */
    @ExceptionHandler({com.fasterxml.jackson.core.JsonProcessingException.class, 
                      org.springframework.http.converter.HttpMessageNotReadableException.class})
    public ResponseEntity<Map<String, Object>> handleJsonException(Exception ex, 
                                                                  HttpServletRequest request) {
        
        logger.warn("JSON解析异常: {}", ex.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", "请求数据格式不正确，请检查JSON格式");
        response.put("data", null);
        response.put("errorCode", RentalException.ErrorCodes.DATA_VALIDATION_ERROR);
        response.put("timestamp", LocalDateTime.now());
        response.put("path", request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * 处理HTTP方法不支持异常
     */
    @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMethodNotSupportedException(
            org.springframework.web.HttpRequestMethodNotSupportedException ex, 
            HttpServletRequest request) {
        
        logger.warn("HTTP方法不支持异常: {}", ex.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 405);
        response.put("message", "不支持的HTTP方法: " + ex.getMethod());
        response.put("data", null);
        response.put("errorCode", RentalException.ErrorCodes.DATA_VALIDATION_ERROR);
        response.put("supportedMethods", ex.getSupportedMethods());
        response.put("timestamp", LocalDateTime.now());
        response.put("path", request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }
    
    /**
     * 处理请求参数缺失异常
     */
    @ExceptionHandler(org.springframework.web.bind.MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParameterException(
            org.springframework.web.bind.MissingServletRequestParameterException ex, 
            HttpServletRequest request) {
        
        logger.warn("请求参数缺失异常: {}", ex.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", "缺少必需的请求参数: " + ex.getParameterName());
        response.put("data", null);
        response.put("errorCode", RentalException.ErrorCodes.REQUIRED_FIELD_MISSING);
        response.put("missingParameter", ex.getParameterName());
        response.put("parameterType", ex.getParameterType());
        response.put("timestamp", LocalDateTime.now());
        response.put("path", request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * 处理类型转换异常
     */
    @ExceptionHandler(org.springframework.beans.TypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatchException(
            org.springframework.beans.TypeMismatchException ex, 
            HttpServletRequest request) {
        
        logger.warn("类型转换异常: {}", ex.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", "参数类型不匹配: " + ex.getPropertyName());
        response.put("data", null);
        response.put("errorCode", RentalException.ErrorCodes.DATA_VALIDATION_ERROR);
        response.put("propertyName", ex.getPropertyName());
        response.put("requiredType", ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");
        response.put("timestamp", LocalDateTime.now());
        response.put("path", request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * 处理文件上传异常
     */
    @ExceptionHandler(org.springframework.web.multipart.MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleMaxUploadSizeException(
            org.springframework.web.multipart.MaxUploadSizeExceededException ex, 
            HttpServletRequest request) {
        
        logger.warn("文件上传大小超限异常: {}", ex.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 413);
        response.put("message", "上传文件大小超出限制");
        response.put("data", null);
        response.put("errorCode", RentalException.ErrorCodes.DATA_VALIDATION_ERROR);
        response.put("maxUploadSize", ex.getMaxUploadSize());
        response.put("timestamp", LocalDateTime.now());
        response.put("path", request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(response);
    }
    

    
    /**
     * 处理租赁API特定的异常
     * 仅对租赁模块的API请求生效
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRentalRuntimeException(RuntimeException ex, 
                                                                           HttpServletRequest request) {
        
        // 只处理租赁模块的API请求
        if (!request.getRequestURI().startsWith("/api/rental/")) {
            throw ex; // 重新抛出，让其他处理器处理
        }
        
        logger.error("租赁模块运行时异常: {}", ex.getMessage(), ex);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 500);
        response.put("message", "查询失败: " + ex.getMessage());
        response.put("data", null);
        response.put("errorCode", RentalException.ErrorCodes.SYSTEM_ERROR);
        response.put("timestamp", LocalDateTime.now());
        response.put("path", request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    /**
     * 处理业务逻辑异常的辅助方法
     * 根据异常类型返回不同的HTTP状态码
     */
    private HttpStatus getHttpStatusForRentalException(RentalException ex) {
        switch (ex.getErrorCode()) {
            case RentalException.ErrorCodes.RENTAL_RECORD_NOT_FOUND:
            case RentalException.ErrorCodes.DEVICE_NOT_FOUND:
            case RentalException.ErrorCodes.CUSTOMER_NOT_FOUND:
                return HttpStatus.NOT_FOUND;
                
            case RentalException.ErrorCodes.PERMISSION_DENIED:
                return HttpStatus.FORBIDDEN;
                
            case RentalException.ErrorCodes.RENTAL_CONFLICT:
            case RentalException.ErrorCodes.DUPLICATE_RENTAL_ORDER:
                return HttpStatus.CONFLICT;
                
            case RentalException.ErrorCodes.DATA_VALIDATION_ERROR:
            case RentalException.ErrorCodes.INVALID_RENTAL_PERIOD:
            case RentalException.ErrorCodes.INVALID_RENTAL_STATUS:
            case RentalException.ErrorCodes.INVALID_PAYMENT_STATUS:
                return HttpStatus.BAD_REQUEST;
                
            case RentalException.ErrorCodes.SYSTEM_ERROR:
                return HttpStatus.INTERNAL_SERVER_ERROR;
                
            default:
                return HttpStatus.BAD_REQUEST;
        }
    }
}