package com.yxrobot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 设备控制历史记录DTO类
 * 用于设备控制操作历史的数据传输
 * 
 * @author YXRobot Development Team
 * @since 2024
 */
public class DeviceControlHistoryDTO {
    
    private Long id;
    private Long deviceId;
    private String deviceSerialNumber;
    private String deviceModel;
    private String customerName;
    
    // 操作信息
    private String operation;
    private String operationDescription;
    private String result;
    private String message;
    private String errorMessage;
    
    // 操作人员信息
    private String operator;
    private String operatorId;
    private String operatorName;
    
    // 时间信息
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operationTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    
    // 操作标识
    private String operationId;
    private String batchId;
    
    // 操作详情
    private Map<String, Object> operationDetails;
    private String parameters;
    
    // 执行信息
    private Integer duration; // 执行时长（秒）
    private String status;    // 执行状态
    private Integer progress; // 执行进度
    
    // 格式化的时间字符串（用于前端显示）
    private String operationTimeFormatted;
    private String startTimeFormatted;
    private String endTimeFormatted;
    private String durationFormatted;
    
    // 构造函数
    public DeviceControlHistoryDTO() {}
    
    public DeviceControlHistoryDTO(Long deviceId, String operation, String result, String operator) {
        this.deviceId = deviceId;
        this.operation = operation;
        this.result = result;
        this.operator = operator;
        this.operationTime = LocalDateTime.now();
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }
    
    public String getDeviceSerialNumber() {
        return deviceSerialNumber;
    }
    
    public void setDeviceSerialNumber(String deviceSerialNumber) {
        this.deviceSerialNumber = deviceSerialNumber;
    }
    
    public String getDeviceModel() {
        return deviceModel;
    }
    
    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public void setOperation(String operation) {
        this.operation = operation;
    }
    
    public String getOperationDescription() {
        return operationDescription;
    }
    
    public void setOperationDescription(String operationDescription) {
        this.operationDescription = operationDescription;
    }
    
    public String getResult() {
        return result;
    }
    
    public void setResult(String result) {
        this.result = result;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public String getOperator() {
        return operator;
    }
    
    public void setOperator(String operator) {
        this.operator = operator;
    }
    
    public String getOperatorId() {
        return operatorId;
    }
    
    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }
    
    public String getOperatorName() {
        return operatorName;
    }
    
    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }
    
    public LocalDateTime getOperationTime() {
        return operationTime;
    }
    
    public void setOperationTime(LocalDateTime operationTime) {
        this.operationTime = operationTime;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    
    public String getOperationId() {
        return operationId;
    }
    
    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }
    
    public String getBatchId() {
        return batchId;
    }
    
    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }
    
    public Map<String, Object> getOperationDetails() {
        return operationDetails;
    }
    
    public void setOperationDetails(Map<String, Object> operationDetails) {
        this.operationDetails = operationDetails;
    }
    
    public String getParameters() {
        return parameters;
    }
    
    public void setParameters(String parameters) {
        this.parameters = parameters;
    }
    
    public Integer getDuration() {
        return duration;
    }
    
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Integer getProgress() {
        return progress;
    }
    
    public void setProgress(Integer progress) {
        this.progress = progress;
    }
    
    public String getOperationTimeFormatted() {
        return operationTimeFormatted;
    }
    
    public void setOperationTimeFormatted(String operationTimeFormatted) {
        this.operationTimeFormatted = operationTimeFormatted;
    }
    
    public String getStartTimeFormatted() {
        return startTimeFormatted;
    }
    
    public void setStartTimeFormatted(String startTimeFormatted) {
        this.startTimeFormatted = startTimeFormatted;
    }
    
    public String getEndTimeFormatted() {
        return endTimeFormatted;
    }
    
    public void setEndTimeFormatted(String endTimeFormatted) {
        this.endTimeFormatted = endTimeFormatted;
    }
    
    public String getDurationFormatted() {
        return durationFormatted;
    }
    
    public void setDurationFormatted(String durationFormatted) {
        this.durationFormatted = durationFormatted;
    }
    
    @Override
    public String toString() {
        return "DeviceControlHistoryDTO{" +
                "id=" + id +
                ", deviceId=" + deviceId +
                ", operation='" + operation + '\'' +
                ", result='" + result + '\'' +
                ", operator='" + operator + '\'' +
                ", operationTime=" + operationTime +
                ", operationId='" + operationId + '\'' +
                '}';
    }
}