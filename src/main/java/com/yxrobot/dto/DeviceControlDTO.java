package com.yxrobot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 设备控制DTO类
 * 适配前端DeviceMonitoring.vue页面的设备控制功能需求
 * 确保字段名称与前端TypeScript接口完全匹配（camelCase）
 * 
 * @author YXRobot Development Team
 * @since 2024
 */
public class DeviceControlDTO {
    
    // 控制请求信息
    private String deviceId;            // 设备ID
    private String operation;           // 操作类型（restart、shutdown、service_restart、firmware_update等）
    private String operationDescription; // 操作描述
    
    // 控制参数（可选）
    private String parameters;          // 操作参数（JSON格式）
    private Boolean forceOperation;     // 是否强制执行
    private Integer timeoutSeconds;     // 超时时间（秒）
    
    // 控制结果信息
    private String controlId;           // 控制指令ID
    private String status;              // 执行状态（pending、executing、success、failed、timeout）
    private String result;              // 执行结果
    private String errorMessage;        // 错误信息
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime requestTime;  // 请求时间
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime responseTime; // 响应时间
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completedTime; // 完成时间
    
    // 格式化的时间字符串（用于前端显示）
    private String requestTimeFormatted;
    private String responseTimeFormatted;
    private String completedTimeFormatted;
    
    // 执行进度信息
    private Integer progress;           // 执行进度百分比
    private String progressDescription; // 进度描述
    
    // 操作权限验证
    private String operatorId;          // 操作员ID
    private String operatorName;        // 操作员姓名
    private Boolean hasPermission;      // 是否有权限
    
    // 设备信息（用于显示）
    private String deviceSerialNumber;  // 设备序列号
    private String deviceModel;         // 设备型号
    private String customerName;        // 客户名称
    
    // 构造函数
    public DeviceControlDTO() {}
    
    public DeviceControlDTO(String deviceId, String operation) {
        this.deviceId = deviceId;
        this.operation = operation;
        this.requestTime = LocalDateTime.now();
        this.status = "pending";
    }
    
    // Getter和Setter方法
    public String getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
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
    
    public String getParameters() {
        return parameters;
    }
    
    public void setParameters(String parameters) {
        this.parameters = parameters;
    }
    
    public Boolean getForceOperation() {
        return forceOperation;
    }
    
    public void setForceOperation(Boolean forceOperation) {
        this.forceOperation = forceOperation;
    }
    
    public Integer getTimeoutSeconds() {
        return timeoutSeconds;
    }
    
    public void setTimeoutSeconds(Integer timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }
    
    public String getControlId() {
        return controlId;
    }
    
    public void setControlId(String controlId) {
        this.controlId = controlId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getResult() {
        return result;
    }
    
    public void setResult(String result) {
        this.result = result;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public LocalDateTime getRequestTime() {
        return requestTime;
    }
    
    public void setRequestTime(LocalDateTime requestTime) {
        this.requestTime = requestTime;
    }
    
    public LocalDateTime getResponseTime() {
        return responseTime;
    }
    
    public void setResponseTime(LocalDateTime responseTime) {
        this.responseTime = responseTime;
    }
    
    public LocalDateTime getCompletedTime() {
        return completedTime;
    }
    
    public void setCompletedTime(LocalDateTime completedTime) {
        this.completedTime = completedTime;
    }
    
    public String getRequestTimeFormatted() {
        return requestTimeFormatted;
    }
    
    public void setRequestTimeFormatted(String requestTimeFormatted) {
        this.requestTimeFormatted = requestTimeFormatted;
    }
    
    public String getResponseTimeFormatted() {
        return responseTimeFormatted;
    }
    
    public void setResponseTimeFormatted(String responseTimeFormatted) {
        this.responseTimeFormatted = responseTimeFormatted;
    }
    
    public String getCompletedTimeFormatted() {
        return completedTimeFormatted;
    }
    
    public void setCompletedTimeFormatted(String completedTimeFormatted) {
        this.completedTimeFormatted = completedTimeFormatted;
    }
    
    public Integer getProgress() {
        return progress;
    }
    
    public void setProgress(Integer progress) {
        this.progress = progress;
    }
    
    public String getProgressDescription() {
        return progressDescription;
    }
    
    public void setProgressDescription(String progressDescription) {
        this.progressDescription = progressDescription;
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
    
    public Boolean getHasPermission() {
        return hasPermission;
    }
    
    public void setHasPermission(Boolean hasPermission) {
        this.hasPermission = hasPermission;
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
    
    @Override
    public String toString() {
        return "DeviceControlDTO{" +
                "deviceId='" + deviceId + '\'' +
                ", operation='" + operation + '\'' +
                ", status='" + status + '\'' +
                ", controlId='" + controlId + '\'' +
                ", requestTime=" + requestTime +
                ", progress=" + progress +
                '}';
    }
    
    /**
     * 控制操作结果类
     */
    public static class ControlResult {
        private boolean success;
        private String message;
        private String operationId;
        private LocalDateTime timestamp;
        private Map<String, Object> data;
        
        public ControlResult() {
            this.timestamp = LocalDateTime.now();
            this.data = new HashMap<>();
        }
        
        public ControlResult(boolean success, String message, String operationId, LocalDateTime timestamp) {
            this.success = success;
            this.message = message;
            this.operationId = operationId;
            this.timestamp = timestamp;
            this.data = new HashMap<>();
        }
        
        public static ControlResult success(String message) {
            return new ControlResult(true, message, generateOperationId(), LocalDateTime.now());
        }
        
        public static ControlResult failure(String message) {
            return new ControlResult(false, message, generateOperationId(), LocalDateTime.now());
        }
        
        private static String generateOperationId() {
            return "OP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        
        // Getter和Setter方法
        public boolean isSuccess() {
            return success;
        }
        
        public void setSuccess(boolean success) {
            this.success = success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public String getOperationId() {
            return operationId;
        }
        
        public void setOperationId(String operationId) {
            this.operationId = operationId;
        }
        
        public LocalDateTime getTimestamp() {
            return timestamp;
        }
        
        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }
        
        public Map<String, Object> getData() {
            return data;
        }
        
        public void setData(Map<String, Object> data) {
            this.data = data;
        }
        
        public void addData(String key, Object value) {
            this.data.put(key, value);
        }
    }
    
    /**
     * 批量控制结果类
     */
    public static class BatchControlResult {
        private int totalCount;
        private int successCount;
        private int failureCount;
        private Map<Long, String> failureDetails;
        private LocalDateTime timestamp;
        private String batchId;
        
        public BatchControlResult() {
            this.failureDetails = new HashMap<>();
            this.timestamp = LocalDateTime.now();
            this.batchId = "BATCH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        
        public void incrementSuccessCount() {
            this.successCount++;
        }
        
        public void incrementFailureCount() {
            this.failureCount++;
        }
        
        public void addFailureDetail(Long deviceId, String reason) {
            this.failureDetails.put(deviceId, reason);
        }
        
        public double getSuccessRate() {
            if (totalCount == 0) return 0.0;
            return (double) successCount / totalCount * 100;
        }
        
        // Getter和Setter方法
        public int getTotalCount() {
            return totalCount;
        }
        
        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }
        
        public int getSuccessCount() {
            return successCount;
        }
        
        public void setSuccessCount(int successCount) {
            this.successCount = successCount;
        }
        
        public int getFailureCount() {
            return failureCount;
        }
        
        public void setFailureCount(int failureCount) {
            this.failureCount = failureCount;
        }
        
        public Map<Long, String> getFailureDetails() {
            return failureDetails;
        }
        
        public void setFailureDetails(Map<Long, String> failureDetails) {
            this.failureDetails = failureDetails;
        }
        
        public LocalDateTime getTimestamp() {
            return timestamp;
        }
        
        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }
        
        public String getBatchId() {
            return batchId;
        }
        
        public void setBatchId(String batchId) {
            this.batchId = batchId;
        }
    }
    
    /**
     * 配置更新请求类
     */
    public static class ConfigUpdateRequest {
        private Map<String, Object> configData;
        private boolean requireRestart;
        private String updateReason;
        private String operatorId;
        private LocalDateTime requestTime;
        
        public ConfigUpdateRequest() {
            this.configData = new HashMap<>();
            this.requestTime = LocalDateTime.now();
        }
        
        public ConfigUpdateRequest(Map<String, Object> configData, boolean requireRestart, String updateReason) {
            this.configData = configData != null ? configData : new HashMap<>();
            this.requireRestart = requireRestart;
            this.updateReason = updateReason;
            this.requestTime = LocalDateTime.now();
        }
        
        // Getter和Setter方法
        public Map<String, Object> getConfigData() {
            return configData;
        }
        
        public void setConfigData(Map<String, Object> configData) {
            this.configData = configData;
        }
        
        public boolean isRequireRestart() {
            return requireRestart;
        }
        
        public void setRequireRestart(boolean requireRestart) {
            this.requireRestart = requireRestart;
        }
        
        public String getUpdateReason() {
            return updateReason;
        }
        
        public void setUpdateReason(String updateReason) {
            this.updateReason = updateReason;
        }
        
        public String getOperatorId() {
            return operatorId;
        }
        
        public void setOperatorId(String operatorId) {
            this.operatorId = operatorId;
        }
        
        public LocalDateTime getRequestTime() {
            return requestTime;
        }
        
        public void setRequestTime(LocalDateTime requestTime) {
            this.requestTime = requestTime;
        }
        
        public void addConfigItem(String key, Object value) {
            this.configData.put(key, value);
        }
    }
}