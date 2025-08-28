package com.yxrobot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yxrobot.entity.LinkValidationLog;

import java.time.LocalDateTime;

/**
 * 链接验证日志数据传输对象
 * 用于返回链接验证日志信息给前端
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-22
 */
public class LinkValidationLogDTO {
    
    /**
     * 日志ID
     */
    private Long id;
    
    /**
     * 链接ID
     */
    private Long linkId;
    
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
     * 格式化的响应时间
     */
    private String formattedResponseTime;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 检查时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkedAt;
    
    /**
     * 是否成功
     */
    private Boolean isSuccessful;
    
    // 默认构造函数
    public LinkValidationLogDTO() {
    }
    
    // 从实体类转换的构造函数
    public LinkValidationLogDTO(LinkValidationLog entity) {
        this.id = entity.getId();
        this.linkId = entity.getLinkId();
        this.isValid = entity.getIsValid();
        this.statusCode = entity.getStatusCode();
        this.responseTime = entity.getResponseTime();
        this.formattedResponseTime = entity.getFormattedResponseTime();
        this.errorMessage = entity.getErrorMessage();
        this.checkedAt = entity.getCheckedAt();
        this.isSuccessful = entity.isSuccessful();
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
    
    public String getFormattedResponseTime() {
        return formattedResponseTime;
    }
    
    public void setFormattedResponseTime(String formattedResponseTime) {
        this.formattedResponseTime = formattedResponseTime;
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
    
    public Boolean getIsSuccessful() {
        return isSuccessful;
    }
    
    public void setIsSuccessful(Boolean isSuccessful) {
        this.isSuccessful = isSuccessful;
    }
    
    /**
     * 获取状态描述
     * @return 状态描述字符串
     */
    public String getStatusDescription() {
        if (statusCode == null) {
            return "未知状态";
        }
        
        if (statusCode >= 200 && statusCode < 300) {
            return "成功";
        } else if (statusCode >= 300 && statusCode < 400) {
            return "重定向";
        } else if (statusCode >= 400 && statusCode < 500) {
            return "客户端错误";
        } else if (statusCode >= 500) {
            return "服务器错误";
        } else {
            return "未知状态";
        }
    }
    
    /**
     * 获取验证结果的颜色标识
     * @return 颜色标识字符串
     */
    public String getStatusColor() {
        if (isValid != null && isValid) {
            return "success";
        } else if (statusCode != null && statusCode >= 400) {
            return "error";
        } else {
            return "warning";
        }
    }
    
    @Override
    public String toString() {
        return "LinkValidationLogDTO{" +
                "id=" + id +
                ", linkId=" + linkId +
                ", isValid=" + isValid +
                ", statusCode=" + statusCode +
                ", responseTime=" + responseTime +
                ", checkedAt=" + checkedAt +
                '}';
    }
}