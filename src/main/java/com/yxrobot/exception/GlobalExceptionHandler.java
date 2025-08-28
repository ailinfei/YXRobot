package com.yxrobot.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 处理系统中所有异常，包括平台链接、新闻管理等模块的异常
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2024-12-22
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @Autowired
    private ExceptionMonitor exceptionMonitor;
    
    /**
     * 处理平台链接业务异常
     * 
     * @param e 平台链接异常
     * @return 错误响应
     */
    @ExceptionHandler(PlatformLinkException.class)
    public ResponseEntity<Map<String, Object>> handlePlatformLinkException(PlatformLinkException e) {
        logger.warn("平台链接业务异常: {}", e.getMessage(), e);
        
        // 记录异常到监控系统
        Map<String, Object> context = buildExceptionContext();
        context.put("errorCode", e.getErrorCode());
        context.put("errorDetails", e.getErrorDetails());
        exceptionMonitor.recordException(e, context);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", e.getMessage());
        response.put("errorCode", e.getErrorCode());
        response.put("errorDetails", e.getErrorDetails());
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * 处理新闻未找到异常
     * 
     * @param e 新闻未找到异常
     * @return 错误响应
     */
    @ExceptionHandler(NewsNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNewsNotFoundException(NewsNotFoundException e) {
        logger.warn("新闻未找到异常: {}", e.getMessage());
        
        // 记录异常到监控系统
        Map<String, Object> context = buildExceptionContext();
        context.put("newsId", e.getNewsId());
        exceptionMonitor.recordException(e, context);
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("newsId", e.getNewsId());
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 404);
        response.put("message", e.getMessage());
        response.put("errorCode", "NEWS_NOT_FOUND");
        response.put("errorDetails", errorDetails);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    
    /**
     * 处理新闻数据验证异常
     * 
     * @param e 新闻数据验证异常
     * @return 错误响应
     */
    @ExceptionHandler(NewsValidationException.class)
    public ResponseEntity<Map<String, Object>> handleNewsValidationException(NewsValidationException e) {
        logger.warn("新闻数据验证异常: {}", e.getMessage());
        
        // 记录异常到监控系统
        Map<String, Object> context = buildExceptionContext();
        context.put("field", e.getField());
        context.put("value", e.getValue());
        context.put("validationErrors", e.getValidationErrors());
        exceptionMonitor.recordException(e, context);
        
        Map<String, Object> errorDetails = new HashMap<>();
        if (e.getField() != null) {
            errorDetails.put("field", e.getField());
            errorDetails.put("value", e.getValue());
        }
        if (e.getValidationErrors() != null) {
            errorDetails.put("validationErrors", e.getValidationErrors());
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", e.getMessage());
        response.put("errorCode", "NEWS_VALIDATION_ERROR");
        response.put("errorDetails", errorDetails);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * 处理新闻状态异常
     * 
     * @param e 新闻状态异常
     * @return 错误响应
     */
    @ExceptionHandler(NewsStatusException.class)
    public ResponseEntity<Map<String, Object>> handleNewsStatusException(NewsStatusException e) {
        logger.warn("新闻状态异常: {}", e.getMessage());
        
        // 记录异常到监控系统
        Map<String, Object> context = buildExceptionContext();
        context.put("newsId", e.getNewsId());
        context.put("currentStatus", e.getCurrentStatus());
        context.put("targetStatus", e.getTargetStatus());
        exceptionMonitor.recordException(e, context);
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("newsId", e.getNewsId());
        errorDetails.put("currentStatus", e.getCurrentStatus());
        errorDetails.put("targetStatus", e.getTargetStatus());
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", e.getMessage());
        response.put("errorCode", "NEWS_STATUS_ERROR");
        response.put("errorDetails", errorDetails);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * 处理新闻操作异常
     * 
     * @param e 新闻操作异常
     * @return 错误响应
     */
    @ExceptionHandler(NewsOperationException.class)
    public ResponseEntity<Map<String, Object>> handleNewsOperationException(NewsOperationException e) {
        logger.error("新闻操作异常: {}", e.getMessage(), e);
        
        // 记录异常到监控系统
        Map<String, Object> context = buildExceptionContext();
        context.put("operation", e.getOperation());
        context.put("newsId", e.getNewsId());
        context.put("errorCode", e.getErrorCode());
        exceptionMonitor.recordException(e, context);
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("operation", e.getOperation());
        errorDetails.put("newsId", e.getNewsId());
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 500);
        response.put("message", e.getMessage());
        response.put("errorCode", e.getErrorCode() != null ? e.getErrorCode() : "NEWS_OPERATION_ERROR");
        response.put("errorDetails", errorDetails);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    /**
     * 处理销售记录未找到异常
     * 
     * @param e 销售记录未找到异常
     * @return 错误响应
     */
    @ExceptionHandler(SalesRecordNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleSalesRecordNotFoundException(SalesRecordNotFoundException e) {
        logger.warn("销售记录未找到异常: {}", e.getMessage());
        
        // 记录异常到监控系统
        Map<String, Object> context = buildExceptionContext();
        context.put("salesRecordId", e.getSalesRecordId());
        exceptionMonitor.recordException(e, context);
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("salesRecordId", e.getSalesRecordId());
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 404);
        response.put("message", e.getMessage());
        response.put("errorCode", "SALES_RECORD_NOT_FOUND");
        response.put("errorDetails", errorDetails);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    
    /**
     * 处理销售数据验证异常
     * 
     * @param e 销售数据验证异常
     * @return 错误响应
     */
    @ExceptionHandler(SalesValidationException.class)
    public ResponseEntity<Map<String, Object>> handleSalesValidationException(SalesValidationException e) {
        logger.warn("销售数据验证异常: {}", e.getMessage());
        
        // 记录异常到监控系统
        Map<String, Object> context = buildExceptionContext();
        context.put("field", e.getField());
        context.put("value", e.getValue());
        exceptionMonitor.recordException(e, context);
        
        Map<String, Object> errorDetails = new HashMap<>();
        if (e.getField() != null) {
            errorDetails.put("field", e.getField());
            errorDetails.put("value", e.getValue());
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", e.getMessage());
        response.put("errorCode", "SALES_VALIDATION_ERROR");
        response.put("errorDetails", errorDetails);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * 处理销售操作异常
     * 
     * @param e 销售操作异常
     * @return 错误响应
     */
    @ExceptionHandler(SalesOperationException.class)
    public ResponseEntity<Map<String, Object>> handleSalesOperationException(SalesOperationException e) {
        logger.error("销售操作异常: {}", e.getMessage(), e);
        
        // 记录异常到监控系统
        Map<String, Object> context = buildExceptionContext();
        context.put("operation", e.getOperation());
        context.put("target", e.getTarget());
        exceptionMonitor.recordException(e, context);
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("operation", e.getOperation());
        errorDetails.put("target", e.getTarget());
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 500);
        response.put("message", e.getMessage());
        response.put("errorCode", "SALES_OPERATION_ERROR");
        response.put("errorDetails", errorDetails);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    /**
     * 处理销售业务异常基类
     * 
     * @param e 销售业务异常
     * @return 错误响应
     */
    @ExceptionHandler(SalesException.class)
    public ResponseEntity<Map<String, Object>> handleSalesException(SalesException e) {
        logger.warn("销售业务异常: {}", e.getMessage());
        
        // 记录异常到监控系统
        Map<String, Object> context = buildExceptionContext();
        context.put("errorCode", e.getErrorCode());
        context.put("errorDetails", e.getErrorDetails());
        exceptionMonitor.recordException(e, context);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", e.getMessage());
        response.put("errorCode", e.getErrorCode() != null ? e.getErrorCode() : "SALES_BUSINESS_ERROR");
        response.put("errorDetails", e.getErrorDetails());
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * 处理客户未找到异常
     * 
     * @param e 客户未找到异常
     * @return 错误响应
     */
    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCustomerNotFoundException(CustomerNotFoundException e) {
        logger.warn("客户未找到异常: {}", e.getMessage());
        
        // 记录异常到监控系统
        Map<String, Object> context = buildExceptionContext();
        context.put("customerId", e.getCustomerId());
        exceptionMonitor.recordException(e, context);
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("customerId", e.getCustomerId());
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 404);
        response.put("message", e.getMessage());
        response.put("errorCode", "CUSTOMER_NOT_FOUND");
        response.put("errorDetails", errorDetails);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    
    /**
     * 处理产品未找到异常
     * 
     * @param e 产品未找到异常
     * @return 错误响应
     */
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleProductNotFoundException(ProductNotFoundException e) {
        logger.warn("产品未找到异常: {}", e.getMessage());
        
        // 记录异常到监控系统
        Map<String, Object> context = buildExceptionContext();
        context.put("productId", e.getProductId());
        exceptionMonitor.recordException(e, context);
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("productId", e.getProductId());
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 404);
        response.put("message", e.getMessage());
        response.put("errorCode", "PRODUCT_NOT_FOUND");
        response.put("errorDetails", errorDetails);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    
    /**
     * 处理销售人员未找到异常
     * 
     * @param e 销售人员未找到异常
     * @return 错误响应
     */
    @ExceptionHandler(SalesStaffNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleSalesStaffNotFoundException(SalesStaffNotFoundException e) {
        logger.warn("销售人员未找到异常: {}", e.getMessage());
        
        // 记录异常到监控系统
        Map<String, Object> context = buildExceptionContext();
        context.put("salesStaffId", e.getSalesStaffId());
        exceptionMonitor.recordException(e, context);
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("salesStaffId", e.getSalesStaffId());
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 404);
        response.put("message", e.getMessage());
        response.put("errorCode", "SALES_STAFF_NOT_FOUND");
        response.put("errorDetails", errorDetails);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    
    /**
     * 处理参数验证异常（@Valid注解）
     * 
     * @param e 方法参数验证异常
     * @return 错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        logger.warn("参数验证失败: {}", e.getMessage());
        
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", "参数验证失败");
        response.put("errorCode", "VALIDATION_ERROR");
        response.put("errorDetails", fieldErrors);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * 处理绑定异常
     * 
     * @param e 绑定异常
     * @return 错误响应
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String, Object>> handleBindException(BindException e) {
        logger.warn("数据绑定失败: {}", e.getMessage());
        
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", "数据绑定失败");
        response.put("errorCode", "BIND_ERROR");
        response.put("errorDetails", fieldErrors);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * 处理约束违反异常（@Validated注解）
     * 
     * @param e 约束违反异常
     * @return 错误响应
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolationException(ConstraintViolationException e) {
        logger.warn("约束验证失败: {}", e.getMessage());
        
        Map<String, String> violations = e.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                    violation -> violation.getPropertyPath().toString(),
                    ConstraintViolation::getMessage
                ));
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", "约束验证失败");
        response.put("errorCode", "CONSTRAINT_VIOLATION");
        response.put("errorDetails", violations);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * 处理方法参数类型不匹配异常
     * 
     * @param e 方法参数类型不匹配异常
     * @return 错误响应
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        logger.warn("参数类型不匹配: {}", e.getMessage());
        
        String parameterName = e.getName();
        String requiredType = e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "未知";
        String providedValue = e.getValue() != null ? e.getValue().toString() : "null";
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("parameterName", parameterName);
        errorDetails.put("requiredType", requiredType);
        errorDetails.put("providedValue", providedValue);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", "参数类型不匹配: " + parameterName + " 需要 " + requiredType + " 类型，但提供了 " + providedValue);
        response.put("errorCode", "TYPE_MISMATCH");
        response.put("errorDetails", errorDetails);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * 处理非法参数异常
     * 
     * @param e 非法参数异常
     * @return 错误响应
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException e) {
        logger.warn("非法参数: {}", e.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", e.getMessage());
        response.put("errorCode", "ILLEGAL_ARGUMENT");
        response.put("errorDetails", null);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * 处理空指针异常
     * 
     * @param e 空指针异常
     * @return 错误响应
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Map<String, Object>> handleNullPointerException(NullPointerException e) {
        logger.error("空指针异常", e);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 500);
        response.put("message", "系统内部错误：空指针异常");
        response.put("errorCode", "NULL_POINTER");
        response.put("errorDetails", null);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    /**
     * 处理运行时异常
     * 
     * @param e 运行时异常
     * @return 错误响应
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException e) {
        logger.error("运行时异常: {}", e.getMessage(), e);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 500);
        response.put("message", "系统内部错误: " + e.getMessage());
        response.put("errorCode", "RUNTIME_ERROR");
        response.put("errorDetails", null);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    /**
     * 处理所有其他异常
     * 
     * @param e 异常
     * @return 错误响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        logger.error("未处理的异常: {}", e.getMessage(), e);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 500);
        response.put("message", "系统内部错误，请联系管理员");
        response.put("errorCode", "INTERNAL_ERROR");
        response.put("errorDetails", null);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    /**
     * 构建异常上下文信息
     * 
     * @return 异常上下文
     */
    private Map<String, Object> buildExceptionContext() {
        Map<String, Object> context = new HashMap<>();
        
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                context.put("requestUri", request.getRequestURI());
                context.put("requestMethod", request.getMethod());
                context.put("remoteAddr", request.getRemoteAddr());
                context.put("userAgent", request.getHeader("User-Agent"));
                
                // 添加请求参数（排除敏感信息）
                Map<String, String[]> parameterMap = request.getParameterMap();
                Map<String, Object> safeParameters = new HashMap<>();
                parameterMap.forEach((key, values) -> {
                    if (!isSensitiveParameter(key)) {
                        safeParameters.put(key, values.length == 1 ? values[0] : values);
                    }
                });
                context.put("requestParameters", safeParameters);
            }
        } catch (Exception e) {
            logger.debug("构建异常上下文时发生错误", e);
        }
        
        context.put("timestamp", System.currentTimeMillis());
        context.put("threadName", Thread.currentThread().getName());
        
        return context;
    }
    
    /**
     * 检查是否为敏感参数
     * 
     * @param parameterName 参数名
     * @return 是否为敏感参数
     */
    private boolean isSensitiveParameter(String parameterName) {
        String lowerName = parameterName.toLowerCase();
        return lowerName.contains("password") || 
               lowerName.contains("token") || 
               lowerName.contains("secret") ||
               lowerName.contains("key");
    }
}