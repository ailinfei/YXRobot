package com.yxrobot.validator;

import com.yxrobot.exception.OrderException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 验证结果收集器
 * 用于收集和汇总多个验证器的验证结果
 * 提供统一的验证结果处理机制
 */
@Component
public class ValidationResultCollector {
    
    /**
     * 验证错误列表
     */
    private List<ValidationError> errors = new ArrayList<>();
    
    /**
     * 验证警告列表
     */
    private List<ValidationWarning> warnings = new ArrayList<>();
    
    /**
     * 添加验证错误
     * 
     * @param field 字段名
     * @param message 错误消息
     * @param errorCode 错误码
     */
    public void addError(String field, String message, String errorCode) {
        errors.add(new ValidationError(field, message, errorCode));
    }
    
    /**
     * 添加验证错误（简化版本）
     * 
     * @param field 字段名
     * @param message 错误消息
     */
    public void addError(String field, String message) {
        addError(field, message, "VALIDATION_ERROR");
    }
    
    /**
     * 添加验证警告
     * 
     * @param field 字段名
     * @param message 警告消息
     */
    public void addWarning(String field, String message) {
        warnings.add(new ValidationWarning(field, message));
    }
    
    /**
     * 检查是否有验证错误
     * 
     * @return true如果有错误，false如果没有错误
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    /**
     * 检查是否有验证警告
     * 
     * @return true如果有警告，false如果没有警告
     */
    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }
    
    /**
     * 获取错误数量
     * 
     * @return 错误数量
     */
    public int getErrorCount() {
        return errors.size();
    }
    
    /**
     * 获取警告数量
     * 
     * @return 警告数量
     */
    public int getWarningCount() {
        return warnings.size();
    }
    
    /**
     * 获取所有错误
     * 
     * @return 错误列表
     */
    public List<ValidationError> getErrors() {
        return new ArrayList<>(errors);
    }
    
    /**
     * 获取所有警告
     * 
     * @return 警告列表
     */
    public List<ValidationWarning> getWarnings() {
        return new ArrayList<>(warnings);
    }
    
    /**
     * 获取错误消息列表
     * 
     * @return 错误消息列表
     */
    public List<String> getErrorMessages() {
        List<String> messages = new ArrayList<>();
        for (ValidationError error : errors) {
            messages.add(error.getMessage());
        }
        return messages;
    }
    
    /**
     * 获取警告消息列表
     * 
     * @return 警告消息列表
     */
    public List<String> getWarningMessages() {
        List<String> messages = new ArrayList<>();
        for (ValidationWarning warning : warnings) {
            messages.add(warning.getMessage());
        }
        return messages;
    }
    
    /**
     * 获取按字段分组的错误
     * 
     * @return 按字段分组的错误Map
     */
    public Map<String, List<String>> getErrorsByField() {
        Map<String, List<String>> errorsByField = new HashMap<>();
        for (ValidationError error : errors) {
            errorsByField.computeIfAbsent(error.getField(), k -> new ArrayList<>())
                        .add(error.getMessage());
        }
        return errorsByField;
    }
    
    /**
     * 获取验证结果摘要
     * 
     * @return 验证结果摘要
     */
    public ValidationResultSummary getSummary() {
        ValidationResultSummary summary = new ValidationResultSummary();
        summary.setErrorCount(getErrorCount());
        summary.setWarningCount(getWarningCount());
        summary.setErrors(getErrors());
        summary.setWarnings(getWarnings());
        summary.setValid(!hasErrors());
        return summary;
    }
    
    /**
     * 如果有错误则抛出异常
     * 
     * @throws OrderException 如果有验证错误
     */
    public void throwIfHasErrors() throws OrderException {
        if (hasErrors()) {
            String errorMessage = String.join("; ", getErrorMessages());
            throw OrderException.validationFailed("multiple_fields", errorMessage);
        }
    }
    
    /**
     * 清空所有验证结果
     */
    public void clear() {
        errors.clear();
        warnings.clear();
    }
    
    /**
     * 重置收集器状态
     */
    public void reset() {
        clear();
    }
    
    /**
     * 合并其他收集器的结果
     * 
     * @param other 其他验证结果收集器
     */
    public void merge(ValidationResultCollector other) {
        this.errors.addAll(other.getErrors());
        this.warnings.addAll(other.getWarnings());
    }
    
    /**
     * 验证错误内部类
     */
    public static class ValidationError {
        private String field;
        private String message;
        private String errorCode;
        
        public ValidationError(String field, String message, String errorCode) {
            this.field = field;
            this.message = message;
            this.errorCode = errorCode;
        }
        
        public String getField() {
            return field;
        }
        
        public String getMessage() {
            return message;
        }
        
        public String getErrorCode() {
            return errorCode;
        }
        
        @Override
        public String toString() {
            return String.format("[%s] %s: %s", errorCode, field, message);
        }
    }
    
    /**
     * 验证警告内部类
     */
    public static class ValidationWarning {
        private String field;
        private String message;
        
        public ValidationWarning(String field, String message) {
            this.field = field;
            this.message = message;
        }
        
        public String getField() {
            return field;
        }
        
        public String getMessage() {
            return message;
        }
        
        @Override
        public String toString() {
            return String.format("[WARNING] %s: %s", field, message);
        }
    }
}