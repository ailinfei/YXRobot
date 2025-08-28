package com.yxrobot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 链接验证日志实体类
 * 用于记录平台链接的验证结果和历史
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-22
 */
public class LinkValidationLog {
    
    /**
     * 日志ID，主键
     */
    private Long id;
    
    /**
     * 链接ID
     */
    @NotNull(message = "链接ID不能为空")
    private Long linkId;
    
    /**
     * 是否有效
     */
    @NotNull(message = "验证结果不能为空")
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
    public LinkValidationLog() {
        this.checkedAt = LocalDateTime.now();
    }
    
    // 带参构造函数
    public LinkValidationLog(Long linkId, Boolean isValid, Integer statusCode, Integer responseTime, String errorMessage) {
        this();
        this.linkId = linkId;
        this.isValid = isValid;
        this.statusCode = statusCode;
        this.responseTime = responseTime;
        this.errorMessage = errorMessage;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getLinkId() {
        return linkId;
    }
    
    public void setLinkId(Long linkId) {
        this.linkId = linkId;
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
     * 获取响应时间的可读格式
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
    
    @Override
    public String toString() {
        return "LinkValidationLog{" +
                "id=" + id +
                ", linkId=" + linkId +
                ", isValid=" + isValid +
                ", statusCode=" + statusCode +
                ", responseTime=" + responseTime +
                ", errorMessage='" + errorMessage + '\'' +
                ", checkedAt=" + checkedAt +
                '}';
    }
}