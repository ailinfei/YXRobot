package com.yxrobot.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

/**
 * 统一响应结果封装类
 * 用于封装所有API接口的响应数据
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-19
 * @param <T> 数据类型
 */
public class Result<T> {
    
    /**
     * 响应状态码
     */
    private Integer code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 响应时间戳
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime timestamp;
    
    // 私有构造函数
    private Result() {
        this.timestamp = LocalDateTime.now();
    }
    
    // 私有构造函数
    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * 成功响应（无数据）
     * @param <T> 数据类型
     * @return 成功响应对象
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "success", null);
    }
    
    /**
     * 成功响应（带数据）
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 成功响应对象
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }
    
    /**
     * 成功响应（自定义消息）
     * @param message 响应消息
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 成功响应对象
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }
    
    /**
     * 失败响应
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 失败响应对象
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }
    
    /**
     * 失败响应（自定义状态码）
     * @param code 状态码
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 失败响应对象
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }
    
    /**
     * 失败响应（自定义状态码和数据）
     * @param code 状态码
     * @param message 错误消息
     * @param data 错误数据
     * @param <T> 数据类型
     * @return 失败响应对象
     */
    public static <T> Result<T> error(Integer code, String message, T data) {
        return new Result<>(code, message, data);
    }
    
    /**
     * 参数错误响应
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 参数错误响应对象
     */
    public static <T> Result<T> badRequest(String message) {
        return new Result<>(400, message, null);
    }
    
    /**
     * 未授权响应
     * @param <T> 数据类型
     * @return 未授权响应对象
     */
    public static <T> Result<T> unauthorized() {
        return new Result<>(401, "未授权访问", null);
    }
    
    /**
     * 禁止访问响应
     * @param <T> 数据类型
     * @return 禁止访问响应对象
     */
    public static <T> Result<T> forbidden() {
        return new Result<>(403, "禁止访问", null);
    }
    
    /**
     * 资源不存在响应
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 资源不存在响应对象
     */
    public static <T> Result<T> notFound(String message) {
        return new Result<>(404, message, null);
    }
    
    /**
     * 判断是否成功
     * @return true-成功，false-失败
     */
    public boolean isSuccess() {
        return code == 200;
    }
    
    /**
     * 判断是否失败
     * @return true-失败，false-成功
     */
    public boolean isError() {
        return code != 200;
    }
    
    // Getter 和 Setter 方法
    public Integer getCode() {
        return code;
    }
    
    public void setCode(Integer code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", timestamp=" + timestamp +
                '}';
    }
}