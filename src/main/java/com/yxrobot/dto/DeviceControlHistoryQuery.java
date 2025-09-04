package com.yxrobot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

/**
 * 设备控制历史查询条件DTO类
 * 用于设备控制历史记录的查询筛选
 * 
 * @author YXRobot Development Team
 * @since 2024
 */
public class DeviceControlHistoryQuery {
    
    // 分页参数
    private Integer page = 1;
    private Integer pageSize = 20;
    
    // 设备筛选
    private Long deviceId;
    private String deviceSerialNumber;
    private String deviceModel;
    private Long customerId;
    private String customerName;
    
    // 操作筛选
    private String operation;
    private String result;
    private String status;
    private String operatorId;
    private String operator;
    
    // 时间范围筛选
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    
    // 操作标识筛选
    private String operationId;
    private String batchId;
    
    // 关键词搜索
    private String keyword;
    
    // 排序参数
    private String sortBy = "operationTime";
    private String sortOrder = "DESC";
    
    // 导出参数
    private String exportFormat; // excel, csv
    private Boolean includeDetails = false;
    
    // 构造函数
    public DeviceControlHistoryQuery() {}
    
    // Getter和Setter方法
    public Integer getPage() {
        return page;
    }
    
    public void setPage(Integer page) {
        this.page = page != null && page > 0 ? page : 1;
    }
    
    public Integer getPageSize() {
        return pageSize;
    }
    
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize != null && pageSize > 0 ? pageSize : 20;
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
    
    public Long getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
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
    
    public String getResult() {
        return result;
    }
    
    public void setResult(String result) {
        this.result = result;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getOperatorId() {
        return operatorId;
    }
    
    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }
    
    public String getOperator() {
        return operator;
    }
    
    public void setOperator(String operator) {
        this.operator = operator;
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
    
    public String getKeyword() {
        return keyword;
    }
    
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    
    public String getSortBy() {
        return sortBy;
    }
    
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy != null ? sortBy : "operationTime";
    }
    
    public String getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder != null ? sortOrder : "DESC";
    }
    
    public String getExportFormat() {
        return exportFormat;
    }
    
    public void setExportFormat(String exportFormat) {
        this.exportFormat = exportFormat;
    }
    
    public Boolean getIncludeDetails() {
        return includeDetails;
    }
    
    public void setIncludeDetails(Boolean includeDetails) {
        this.includeDetails = includeDetails != null ? includeDetails : false;
    }
    
    /**
     * 计算分页偏移量
     */
    public int getOffset() {
        return (page - 1) * pageSize;
    }
    
    /**
     * 验证查询参数
     */
    public boolean isValid() {
        return page > 0 && pageSize > 0 && pageSize <= 1000;
    }
    
    @Override
    public String toString() {
        return "DeviceControlHistoryQuery{" +
                "page=" + page +
                ", pageSize=" + pageSize +
                ", deviceId=" + deviceId +
                ", operation='" + operation + '\'' +
                ", result='" + result + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", keyword='" + keyword + '\'' +
                ", sortBy='" + sortBy + '\'' +
                ", sortOrder='" + sortOrder + '\'' +
                '}';
    }
}