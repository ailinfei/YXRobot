package com.yxrobot.exception;

import com.yxrobot.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 公益项目管理全局异常处理器
 * 专门处理公益项目相关的异常，提供友好的错误信息返回给前端
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-19
 */
@RestControllerAdvice(basePackages = {"com.yxrobot.controller"})
public class CharityExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(CharityExceptionHandler.class);
    
    /**
     * 处理公益项目业务异常
     * 
     * @param e CharityException异常
     * @return 错误响应
     */
    @ExceptionHandler(CharityException.class)
    public ResponseEntity<Result<Map<String, Object>>> handleCharityException(CharityException e) {
        logger.warn("公益项目业务异常: {}", e.getMessage(), e);
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("errorCode", e.getErrorCode());
        errorDetails.put("message", e.getMessage());
        errorDetails.put("details", e.getDetails());
        errorDetails.put("timestamp", System.currentTimeMillis());
        
        // 根据错误码确定HTTP状态码
        HttpStatus httpStatus = getHttpStatusByErrorCode(e.getErrorCode());
        
        Result<Map<String, Object>> result = Result.error(httpStatus.value(), e.getMessage());
        result.setData(errorDetails);
        
        return ResponseEntity.status(httpStatus).body(result);
    }
    
    /**
     * 处理参数验证异常（@Valid注解触发）
     * 
     * @param e MethodArgumentNotValidException异常
     * @return 错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Map<String, Object>>> handleValidationException(MethodArgumentNotValidException e) {
        logger.warn("参数验证异常: {}", e.getMessage());
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("errorCode", "VALIDATION_ERROR");
        errorDetails.put("timestamp", System.currentTimeMillis());
        
        // 收集所有字段验证错误
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        Map<String, String> fieldErrorMap = fieldErrors.stream()
                .collect(Collectors.toMap(
                    FieldError::getField,
                    FieldError::getDefaultMessage,
                    (existing, replacement) -> existing + "; " + replacement
                ));
        
        errorDetails.put("fieldErrors", fieldErrorMap);
        
        String errorMessage = "数据验证失败：" + fieldErrors.stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        errorDetails.put("message", errorMessage);
        
        Result<Map<String, Object>> result = Result.badRequest(errorMessage);
        result.setData(errorDetails);
        
        return ResponseEntity.badRequest().body(result);
    }
    
    /**
     * 处理绑定异常（表单数据绑定失败）
     * 
     * @param e BindException异常
     * @return 错误响应
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Result<Map<String, Object>>> handleBindException(BindException e) {
        logger.warn("数据绑定异常: {}", e.getMessage());
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("errorCode", "BIND_ERROR");
        errorDetails.put("timestamp", System.currentTimeMillis());
        
        // 收集所有字段绑定错误
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        Map<String, String> fieldErrorMap = fieldErrors.stream()
                .collect(Collectors.toMap(
                    FieldError::getField,
                    FieldError::getDefaultMessage,
                    (existing, replacement) -> existing + "; " + replacement
                ));
        
        errorDetails.put("fieldErrors", fieldErrorMap);
        
        String errorMessage = "数据绑定失败：" + fieldErrors.stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        errorDetails.put("message", errorMessage);
        
        Result<Map<String, Object>> result = Result.badRequest(errorMessage);
        result.setData(errorDetails);
        
        return ResponseEntity.badRequest().body(result);
    }
    
    /**
     * 处理约束违反异常（Bean Validation）
     * 
     * @param e ConstraintViolationException异常
     * @return 错误响应
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Result<Map<String, Object>>> handleConstraintViolationException(ConstraintViolationException e) {
        logger.warn("约束违反异常: {}", e.getMessage());
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("errorCode", "CONSTRAINT_VIOLATION");
        errorDetails.put("timestamp", System.currentTimeMillis());
        
        // 收集所有约束违反错误
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        Map<String, String> violationMap = violations.stream()
                .collect(Collectors.toMap(
                    violation -> violation.getPropertyPath().toString(),
                    ConstraintViolation::getMessage,
                    (existing, replacement) -> existing + "; " + replacement
                ));
        
        errorDetails.put("violations", violationMap);
        
        String errorMessage = "数据约束违反：" + violations.stream()
                .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
                .collect(Collectors.joining(", "));
        
        errorDetails.put("message", errorMessage);
        
        Result<Map<String, Object>> result = Result.badRequest(errorMessage);
        result.setData(errorDetails);
        
        return ResponseEntity.badRequest().body(result);
    }
    
    /**
     * 处理非法参数异常
     * 
     * @param e IllegalArgumentException异常
     * @return 错误响应
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<Map<String, Object>>> handleIllegalArgumentException(IllegalArgumentException e) {
        logger.warn("非法参数异常: {}", e.getMessage());
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("errorCode", "ILLEGAL_ARGUMENT");
        errorDetails.put("message", e.getMessage());
        errorDetails.put("timestamp", System.currentTimeMillis());
        
        Result<Map<String, Object>> result = Result.badRequest(e.getMessage());
        result.setData(errorDetails);
        
        return ResponseEntity.badRequest().body(result);
    }
    
    /**
     * 处理空指针异常
     * 
     * @param e NullPointerException异常
     * @return 错误响应
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Result<Map<String, Object>>> handleNullPointerException(NullPointerException e) {
        logger.error("空指针异常: {}", e.getMessage(), e);
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("errorCode", "NULL_POINTER");
        errorDetails.put("message", "系统内部错误，请稍后重试");
        errorDetails.put("timestamp", System.currentTimeMillis());
        
        Result<Map<String, Object>> result = Result.error("系统内部错误，请稍后重试");
        result.setData(errorDetails);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }
    
    /**
     * 处理运行时异常
     * 
     * @param e RuntimeException异常
     * @return 错误响应
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Result<Map<String, Object>>> handleRuntimeException(RuntimeException e) {
        logger.error("运行时异常: {}", e.getMessage(), e);
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("errorCode", "RUNTIME_ERROR");
        errorDetails.put("message", e.getMessage());
        errorDetails.put("timestamp", System.currentTimeMillis());
        
        Result<Map<String, Object>> result = Result.error(e.getMessage());
        result.setData(errorDetails);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }
    
    /**
     * 处理通用异常
     * 
     * @param e Exception异常
     * @return 错误响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Map<String, Object>>> handleGenericException(Exception e) {
        logger.error("未处理的异常: {}", e.getMessage(), e);
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("errorCode", "INTERNAL_ERROR");
        errorDetails.put("message", "系统内部错误，请联系管理员");
        errorDetails.put("timestamp", System.currentTimeMillis());
        
        Result<Map<String, Object>> result = Result.error("系统内部错误，请联系管理员");
        result.setData(errorDetails);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }
    
    /**
     * 根据错误码获取对应的HTTP状态码
     * 
     * @param errorCode 错误码
     * @return HTTP状态码
     */
    private HttpStatus getHttpStatusByErrorCode(String errorCode) {
        if (errorCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        
        switch (errorCode) {
            case "CHARITY_VALIDATION_ERROR":
            case "CHARITY_BUSINESS_RULE_VIOLATION":
                return HttpStatus.BAD_REQUEST;
                
            case "CHARITY_DATA_NOT_FOUND":
                return HttpStatus.NOT_FOUND;
                
            case "CHARITY_DATA_CONFLICT":
                return HttpStatus.CONFLICT;
                
            case "CHARITY_OPERATION_FAILED":
            case "CHARITY_STATS_ERROR":
            case "CHARITY_INSTITUTION_ERROR":
            case "CHARITY_ACTIVITY_ERROR":
            case "CHARITY_CHART_ERROR":
                return HttpStatus.INTERNAL_SERVER_ERROR;
                
            default:
                return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
    
    /**
     * 创建友好的错误信息
     * 
     * @param originalMessage 原始错误信息
     * @param errorCode 错误码
     * @return 友好的错误信息
     */
    private String createFriendlyMessage(String originalMessage, String errorCode) {
        if (originalMessage == null || originalMessage.trim().isEmpty()) {
            return "操作失败，请稍后重试";
        }
        
        // 根据错误码提供更友好的提示
        switch (errorCode) {
            case "CHARITY_VALIDATION_ERROR":
                return "数据验证失败：" + originalMessage;
                
            case "CHARITY_DATA_NOT_FOUND":
                return "未找到相关数据：" + originalMessage;
                
            case "CHARITY_DATA_CONFLICT":
                return "数据冲突：" + originalMessage;
                
            case "CHARITY_BUSINESS_RULE_VIOLATION":
                return "业务规则违反：" + originalMessage;
                
            case "CHARITY_OPERATION_FAILED":
                return "操作失败：" + originalMessage;
                
            case "CHARITY_STATS_ERROR":
                return "统计数据错误：" + originalMessage;
                
            case "CHARITY_INSTITUTION_ERROR":
                return "机构管理错误：" + originalMessage;
                
            case "CHARITY_ACTIVITY_ERROR":
                return "活动管理错误：" + originalMessage;
                
            case "CHARITY_CHART_ERROR":
                return "图表数据错误：" + originalMessage;
                
            default:
                return originalMessage;
        }
    }
    
    /**
     * 记录异常详细信息（用于调试和监控）
     * 
     * @param exception 异常对象
     * @param additionalInfo 附加信息
     */
    private void logExceptionDetails(Exception exception, String additionalInfo) {
        logger.error("异常详细信息 - 类型: {}, 消息: {}, 附加信息: {}", 
                    exception.getClass().getSimpleName(), 
                    exception.getMessage(), 
                    additionalInfo, 
                    exception);
    }
}