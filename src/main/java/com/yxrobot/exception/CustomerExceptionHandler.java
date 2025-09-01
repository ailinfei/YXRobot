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
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 客户管理模块全局异常处理器
 * 提供友好的错误信息和统一的错误响应格式
 */
@RestControllerAdvice(basePackages = "com.yxrobot.controller")
public class CustomerExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerExceptionHandler.class);
    
    /**
     * 处理客户业务异常
     */
    @ExceptionHandler(CustomerException.class)
    public ResponseEntity<Map<String, Object>> handleCustomerException(
            CustomerException ex, HttpServletRequest request) {
        
        logger.warn("Customer business exception: {} - {}", ex.getErrorCode(), ex.getMessage());
        
        Map<String, Object> response = createErrorResponse(
            getHttpStatusForCustomerException(ex),
            ex.getErrorCode(),
            ex.getMessage(),
            ex.getData(),
            request.getRequestURI()
        );
        
        return ResponseEntity.status(getHttpStatusForCustomerException(ex)).body(response);
    }
    
    /**
     * 处理数据验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        logger.warn("Validation exception: {}", ex.getMessage());
        
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });
        
        Map<String, Object> response = createErrorResponse(
            HttpStatus.BAD_REQUEST,
            "VALIDATION_ERROR",
            "数据验证失败",
            fieldErrors,
            request.getRequestURI()
        );
        
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String, Object>> handleBindException(
            BindException ex, HttpServletRequest request) {
        
        logger.warn("Bind exception: {}", ex.getMessage());
        
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });
        
        Map<String, Object> response = createErrorResponse(
            HttpStatus.BAD_REQUEST,
            "BIND_ERROR",
            "参数绑定失败",
            fieldErrors,
            request.getRequestURI()
        );
        
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * 处理约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {
        
        logger.warn("Constraint violation exception: {}", ex.getMessage());
        
        Map<String, String> violations = ex.getConstraintViolations()
            .stream()
            .collect(Collectors.toMap(
                violation -> violation.getPropertyPath().toString(),
                ConstraintViolation::getMessage
            ));
        
        Map<String, Object> response = createErrorResponse(
            HttpStatus.BAD_REQUEST,
            "CONSTRAINT_VIOLATION",
            "约束验证失败",
            violations,
            request.getRequestURI()
        );
        
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * 处理参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatchException(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        
        logger.warn("Type mismatch exception: parameter '{}' with value '{}' could not be converted to type '{}'", 
                   ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName());
        
        String message = String.format("参数类型错误：参数 '%s' 的值 '%s' 无法转换为 %s 类型", 
                                     ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName());
        
        Map<String, Object> response = createErrorResponse(
            HttpStatus.BAD_REQUEST,
            "TYPE_MISMATCH",
            message,
            Map.of("parameter", ex.getName(), "value", ex.getValue(), "expectedType", ex.getRequiredType().getSimpleName()),
            request.getRequestURI()
        );
        
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {
        
        logger.warn("Illegal argument exception: {}", ex.getMessage());
        
        Map<String, Object> response = createErrorResponse(
            HttpStatus.BAD_REQUEST,
            "ILLEGAL_ARGUMENT",
            "参数错误: " + ex.getMessage(),
            null,
            request.getRequestURI()
        );
        
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Map<String, Object>> handleNullPointerException(
            NullPointerException ex, HttpServletRequest request) {
        
        logger.error("Null pointer exception in customer API", ex);
        
        Map<String, Object> response = createErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "NULL_POINTER_ERROR",
            "系统内部错误，请联系管理员",
            null,
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    /**
     * 处理数据库异常
     */
    @ExceptionHandler(org.springframework.dao.DataAccessException.class)
    public ResponseEntity<Map<String, Object>> handleDataAccessException(
            org.springframework.dao.DataAccessException ex, HttpServletRequest request) {
        
        logger.error("Database access exception in customer API", ex);
        
        String message = "数据库操作失败";
        String errorCode = "DATABASE_ERROR";
        
        // 根据具体异常类型提供更友好的错误信息
        if (ex instanceof org.springframework.dao.DuplicateKeyException) {
            message = "数据已存在，请检查唯一性约束";
            errorCode = "DUPLICATE_KEY_ERROR";
        } else if (ex instanceof org.springframework.dao.DataIntegrityViolationException) {
            message = "数据完整性约束违反";
            errorCode = "DATA_INTEGRITY_ERROR";
        } else if (ex instanceof org.springframework.dao.EmptyResultDataAccessException) {
            message = "未找到相关数据";
            errorCode = "DATA_NOT_FOUND";
        }
        
        Map<String, Object> response = createErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            errorCode,
            message,
            null,
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    /**
     * 处理通用运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException ex, HttpServletRequest request) {
        
        logger.error("Runtime exception in customer API", ex);
        
        Map<String, Object> response = createErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "RUNTIME_ERROR",
            "系统运行时错误，请联系管理员",
            null,
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    /**
     * 处理通用异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex, HttpServletRequest request) {
        
        logger.error("Unexpected exception in customer API", ex);
        
        Map<String, Object> response = createErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "INTERNAL_ERROR",
            "系统内部错误，请联系管理员",
            null,
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    /**
     * 创建统一的错误响应格式
     */
    private Map<String, Object> createErrorResponse(HttpStatus status, String errorCode, 
                                                   String message, Object data, String path) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", status.value());
        response.put("errorCode", errorCode);
        response.put("message", message);
        response.put("data", data);
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("path", path);
        
        return response;
    }
    
    /**
     * 根据客户异常类型确定HTTP状态码
     */
    private HttpStatus getHttpStatusForCustomerException(CustomerException ex) {
        String errorCode = ex.getErrorCode();
        
        switch (errorCode) {
            case "CUSTOMER_NOT_FOUND":
                return HttpStatus.NOT_FOUND;
            case "CUSTOMER_ALREADY_EXISTS":
            case "INVALID_CUSTOMER_DATA":
            case "CUSTOMER_VALIDATION_ERROR":
                return HttpStatus.BAD_REQUEST;
            case "CUSTOMER_PERMISSION_DENIED":
                return HttpStatus.FORBIDDEN;
            case "CUSTOMER_OPERATION_NOT_ALLOWED":
                return HttpStatus.METHOD_NOT_ALLOWED;
            case "CUSTOMER_CONCURRENCY_ERROR":
                return HttpStatus.CONFLICT;
            case "CUSTOMER_DATA_INTEGRITY_ERROR":
            case "CUSTOMER_SERVICE_ERROR":
            case "CUSTOMER_EXTERNAL_SERVICE_ERROR":
                return HttpStatus.INTERNAL_SERVER_ERROR;
            default:
                return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}