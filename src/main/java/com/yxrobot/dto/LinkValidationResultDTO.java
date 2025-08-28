package com.yxrobot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yxrobot.entity.LinkValidationLog;

import java.time.LocalDateTime;

/**
 * 链接验证结果数据传输对象
 * 用于返回链接验证结果给前端
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-22
 */
public class LinkValidationResultDTO {
    
    /**
     * 链接ID
     */
    private Long id;
    
    /**
     * 是否有效
     */
    private Boolean isValid;
    
    /**
     * HTTP状态码
     */
    private Integer statusCode;
    
    /**
     * 响应时间（毫秒）
     */
    private Integer responseTime;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 检查时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkedAt;
    
    // 默认构造函数
    public LinkValidationResultDTO() {
    }
    
    // 从验证日志实体转换的构造函数
    public LinkValidationResultDTO(LinkValidationLog log) {
        this.id = log.getLinkId();
        this.isValid = log.getIsValid();
        this.statusCode = log.getStatusCode();
        this.responseTime = log.getResponseTime();
        this.errorMessage = log.getErrorMessage();
        this.checkedAt = log.getCheckedAt();
    }
    
    // 带参构造函数
    public LinkValidationResultDTO(Long id, Boolean isValid, Integer statusCode, Integer responseTime, String errorMessage) {
        this.id = id;
        this.isValid = isValid;
        this.statusCode = statusCode;
        this.responseTime = responseTime;
        this.errorMessage = errorMessage;
        this.checkedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Boolean getIsValid() {
        return isValid;
    }
    
    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }
    
    public Integer getStatusCode() {
        return statusCode;
    }
    
    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }
    
    public Integer getResponseTime() {
        return responseTime;
    }
    
    public void setResponseTime(Integer responseTime) {
        this.responseTime = responseTime;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public LocalDateTime getCheckedAt() {
        return checkedAt;
    }
    
    public void setCheckedAt(LocalDateTime checkedAt) {
        this.checkedAt = checkedAt;
    }
    
    /**
     * 判断是否为成功的验证
     * @return true如果状态码在200-299范围内
     */
    public boolean isSuccessful() {
        return statusCode != null && statusCode >= 200 && statusCode < 300;
    }
    
    /**
     * 获取格式化的响应时间
     * @return 格式化的响应时间字符串
     */
    public String getFormattedResponseTime() {
        if (responseTime == null) {
            return "未知";
        }
        if (responseTime < 1000) {
            return responseTime + "ms";
        } else {
            return String.format("%.2fs", responseTime / 1000.0);
        }
    }
}