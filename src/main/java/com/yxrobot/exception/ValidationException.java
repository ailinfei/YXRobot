package com.yxrobot.exception;

/**
 * 数据验证异常类
 * 用于处理数据验证失败的情况
 * 
 * @author Kiro
 * @date 2025-01-28
 */
public class ValidationException extends RuntimeException {
    
    /**
     * 验证失败的字段名
     */
    private String field;
    
    /**
     * 验证失败的字段值
     */
    private Object value;
    
    /**
     * 默认构造方法
     */
    public ValidationException() {
        super();
    }
    
    /**
     * 带消息的构造方法
     * 
     * @param message 错误消息
     */
    public ValidationException(String message) {
        super(message);
    }
    
    /**
     * 带字段信息的构造方法
     * 
     * @param field 验证失败的字段名
     * @param value 验证失败的字段值
     * @param message 错误消息
     */
    public ValidationException(String field, Object value, String message) {
        super(message);
        this.field = field;
        this.value = value;
    }
    
    /**
     * 带原因的构造方法
     * 
     * @param message 错误消息
     * @param cause 原始异常
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * 完整构造方法
     * 
     * @param field 验证失败的字段名
     * @param value 验证失败的字段值
     * @param message 错误消息
     * @param cause 原始异常
     */
    public ValidationException(String field, Object value, String message, Throwable cause) {
        super(message, cause);
        this.field = field;
        this.value = value;
    }
    
    /**
     * 获取验证失败的字段名
     * 
     * @return 字段名
     */
    public String getField() {
        return field;
    }
    
    /**
     * 设置验证失败的字段名
     * 
     * @param field 字段名
     */
    public void setField(String field) {
        this.field = field;
    }
    
    /**
     * 获取验证失败的字段值
     * 
     * @return 字段值
     */
    public Object getValue() {
        return value;
    }
    
    /**
     * 设置验证失败的字段值
     * 
     * @param value 字段值
     */
    public void setValue(Object value) {
        this.value = value;
    }
    
    /**
     * 创建数据验证错误异常
     * 
     * @param field 字段名
     * @param value 字段值
     * @return ValidationException实例
     */
    public static ValidationException dataValidationError(String field, Object value) {
        return new ValidationException(field, value, 
            String.format("数据验证失败: 字段 '%s' 的值 '%s' 不符合要求", field, value));
    }
    
    /**
     * 创建必填字段验证错误异常
     * 
     * @param field 字段名
     * @return ValidationException实例
     */
    public static ValidationException requiredFieldError(String field) {
        return new ValidationException(field, null, 
            String.format("必填字段验证失败: 字段 '%s' 不能为空", field));
    }
    
    /**
     * 创建格式验证错误异常
     * 
     * @param field 字段名
     * @param value 字段值
     * @param expectedFormat 期望格式
     * @return ValidationException实例
     */
    public static ValidationException formatError(String field, Object value, String expectedFormat) {
        return new ValidationException(field, value, 
            String.format("格式验证失败: 字段 '%s' 的值 '%s' 不符合期望格式 '%s'", field, value, expectedFormat));
    }
    
    /**
     * 创建范围验证错误异常
     * 
     * @param field 字段名
     * @param value 字段值
     * @param min 最小值
     * @param max 最大值
     * @return ValidationException实例
     */
    public static ValidationException rangeError(String field, Object value, Object min, Object max) {
        return new ValidationException(field, value, 
            String.format("范围验证失败: 字段 '%s' 的值 '%s' 不在范围 [%s, %s] 内", field, value, min, max));
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ValidationException{");
        if (field != null) {
            sb.append("field='").append(field).append("'");
        }
        if (value != null) {
            if (field != null) sb.append(", ");
            sb.append("value=").append(value);
        }
        if (getMessage() != null) {
            if (field != null || value != null) sb.append(", ");
            sb.append("message='").append(getMessage()).append("'");
        }
        sb.append("}");
        return sb.toString();
    }
}