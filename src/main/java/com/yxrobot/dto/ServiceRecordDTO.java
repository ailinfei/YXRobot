package com.yxrobot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

/**
 * 服务记录数据传输对象
 * 与前端CustomerServiceRecord接口保持一致
 * 用于客户服务记录列表和详情显示
 */
public class ServiceRecordDTO {
    
    private String id;                  // 服务记录ID
    
    @JsonProperty("type")
    private String type;                // 服务类型：maintenance, upgrade, consultation, complaint
    
    @JsonProperty("subject")
    private String subject;             // 服务主题
    
    @JsonProperty("description")
    private String description;         // 服务描述
    
    @JsonProperty("deviceId")
    private String deviceId;            // 关联设备ID
    
    @JsonProperty("serviceStaff")
    private String serviceStaff;        // 服务人员
    
    @JsonProperty("cost")
    private BigDecimal cost;            // 服务费用
    
    @JsonProperty("status")
    private String status;              // 服务状态：in_progress, completed, cancelled
    
    @JsonProperty("attachments")
    private List<ServiceAttachmentDTO> attachments; // 服务附件
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("createdAt")
    private String createdAt;           // 创建时间
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("updatedAt")
    private String updatedAt;           // 更新时间
    
    // 服务详细信息
    @JsonProperty("serviceDetails")
    private ServiceDetailsDTO serviceDetails; // 服务详情
    
    // 内部类 - 服务附件
    public static class ServiceAttachmentDTO {
        private String id;
        
        @JsonProperty("name")
        private String name;            // 附件名称
        
        @JsonProperty("url")
        private String url;             // 附件URL
        
        @JsonProperty("size")
        private Long size;              // 附件大小
        
        @JsonProperty("type")
        private String type;            // 附件类型
        
        // Getter和Setter
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public Long getSize() { return size; }
        public void setSize(Long size) { this.size = size; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }
    
    // 内部类 - 服务详情
    public static class ServiceDetailsDTO {
        @JsonProperty("priority")
        private String priority;        // 优先级
        
        @JsonProperty("urgency")
        private String urgency;         // 紧急程度
        
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonProperty("scheduledTime")
        private String scheduledTime;   // 预定时间
        
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonProperty("startTime")
        private String startTime;       // 开始时间
        
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonProperty("endTime")
        private String endTime;         // 结束时间
        
        @JsonProperty("duration")
        private Integer duration;       // 服务时长（分钟）
        
        @JsonProperty("resolution")
        private String resolution;      // 解决方案
        
        @JsonProperty("customerFeedback")
        private String customerFeedback; // 客户反馈
        
        @JsonProperty("rating")
        private Integer rating;         // 服务评分
        
        // Getter和Setter
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
        public String getUrgency() { return urgency; }
        public void setUrgency(String urgency) { this.urgency = urgency; }
        public String getScheduledTime() { return scheduledTime; }
        public void setScheduledTime(String scheduledTime) { this.scheduledTime = scheduledTime; }
        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }
        public String getEndTime() { return endTime; }
        public void setEndTime(String endTime) { this.endTime = endTime; }
        public Integer getDuration() { return duration; }
        public void setDuration(Integer duration) { this.duration = duration; }
        public String getResolution() { return resolution; }
        public void setResolution(String resolution) { this.resolution = resolution; }
        public String getCustomerFeedback() { return customerFeedback; }
        public void setCustomerFeedback(String customerFeedback) { this.customerFeedback = customerFeedback; }
        public Integer getRating() { return rating; }
        public void setRating(Integer rating) { this.rating = rating; }
    }
    
    // 构造函数
    public ServiceRecordDTO() {}
    
    // Getter和Setter方法
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    
    public String getServiceStaff() {
        return serviceStaff;
    }
    
    public void setServiceStaff(String serviceStaff) {
        this.serviceStaff = serviceStaff;
    }
    
    public BigDecimal getCost() {
        return cost;
    }
    
    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public List<ServiceAttachmentDTO> getAttachments() {
        return attachments;
    }
    
    public void setAttachments(List<ServiceAttachmentDTO> attachments) {
        this.attachments = attachments;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public ServiceDetailsDTO getServiceDetails() {
        return serviceDetails;
    }
    
    public void setServiceDetails(ServiceDetailsDTO serviceDetails) {
        this.serviceDetails = serviceDetails;
    }
}