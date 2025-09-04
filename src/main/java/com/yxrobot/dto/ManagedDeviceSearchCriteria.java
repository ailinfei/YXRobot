package com.yxrobot.dto;

import com.yxrobot.entity.DeviceModel;
import com.yxrobot.entity.DeviceStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 设备搜索条件DTO
 * 用于设备管理的高级搜索和筛选功能
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-28
 */
public class ManagedDeviceSearchCriteria {
    
    /**
     * 关键词搜索（支持设备序列号、客户姓名、设备型号、备注）
     */
    private String keyword;
    
    /**
     * 搜索范围限定（serialNumber、customerName、model、notes、all）
     */
    private String searchScope;
    
    /**
     * 设备状态筛选
     */
    private DeviceStatus status;
    
    /**
     * 设备状态列表筛选（支持多选）
     */
    private List<DeviceStatus> statusList;
    
    /**
     * 设备型号筛选
     */
    private DeviceModel model;
    
    /**
     * 设备型号列表筛选（支持多选）
     */
    private List<DeviceModel> modelList;
    
    /**
     * 客户ID筛选
     */
    private Long customerId;
    
    /**
     * 客户姓名筛选
     */
    private String customerName;
    
    /**
     * 客户ID列表筛选（支持多选）
     */
    private List<Long> customerIds;
    
    /**
     * 固件版本筛选
     */
    private String firmwareVersion;
    
    /**
     * 固件版本列表筛选（支持多选）
     */
    private List<String> firmwareVersionList;
    
    /**
     * 最小固件版本
     */
    private String minFirmwareVersion;
    
    /**
     * 最大固件版本
     */
    private String maxFirmwareVersion;
    
    /**
     * 创建时间开始日期
     */
    private LocalDateTime createdStartDate;
    
    /**
     * 创建时间结束日期
     */
    private LocalDateTime createdEndDate;
    
    /**
     * 最后在线时间开始日期
     */
    private LocalDateTime lastOnlineStartDate;
    
    /**
     * 最后在线时间结束日期
     */
    private LocalDateTime lastOnlineEndDate;
    
    /**
     * 激活时间开始日期
     */
    private LocalDateTime activatedStartDate;
    
    /**
     * 激活时间结束日期
     */
    private LocalDateTime activatedEndDate;
    
    /**
     * 更新时间开始日期
     */
    private LocalDateTime updatedStartDate;
    
    /**
     * 更新时间结束日期
     */
    private LocalDateTime updatedEndDate;
    
    /**
     * 日期字段类型（created、updated、activated、lastOnline）
     */
    private String dateField;
    
    /**
     * 是否在线
     */
    private Boolean isOnline;
    
    /**
     * 是否已激活
     */
    private Boolean isActivated;
    
    /**
     * 最近在线小时数（查询最近N小时内在线的设备）
     */
    private Integer lastOnlineHours;
    
    /**
     * 备注内容筛选
     */
    private String notes;
    
    /**
     * 创建者筛选
     */
    private String createdBy;
    
    /**
     * 是否包含已删除的设备
     */
    private Boolean includeDeleted;
    
    /**
     * 排序字段
     */
    private String sortBy;
    
    /**
     * 排序方向（ASC、DESC）
     */
    private String sortOrder;
    
    /**
     * 分页偏移量
     */
    private Integer offset;
    
    /**
     * 分页大小
     */
    private Integer limit;
    
    /**
     * 页码（从1开始）
     */
    private Integer page;
    
    /**
     * 每页大小
     */
    private Integer pageSize;
    
    // 构造函数
    public ManagedDeviceSearchCriteria() {
        this.includeDeleted = false;
        this.sortBy = "createdAt";
        this.sortOrder = "DESC";
        this.page = 1;
        this.pageSize = 10;
    }
    
    // Getter和Setter方法
    public String getKeyword() {
        return keyword;
    }
    
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    
    public String getSearchScope() {
        return searchScope;
    }
    
    public void setSearchScope(String searchScope) {
        this.searchScope = searchScope;
    }
    
    public DeviceStatus getStatus() {
        return status;
    }
    
    public void setStatus(DeviceStatus status) {
        this.status = status;
    }
    
    public List<DeviceStatus> getStatusList() {
        return statusList;
    }
    
    public void setStatusList(List<DeviceStatus> statusList) {
        this.statusList = statusList;
    }
    
    public DeviceModel getModel() {
        return model;
    }
    
    public void setModel(DeviceModel model) {
        this.model = model;
    }
    
    public List<DeviceModel> getModelList() {
        return modelList;
    }
    
    public void setModelList(List<DeviceModel> modelList) {
        this.modelList = modelList;
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
    
    public List<Long> getCustomerIds() {
        return customerIds;
    }
    
    public void setCustomerIds(List<Long> customerIds) {
        this.customerIds = customerIds;
    }
    
    public String getFirmwareVersion() {
        return firmwareVersion;
    }
    
    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }
    
    public List<String> getFirmwareVersionList() {
        return firmwareVersionList;
    }
    
    public void setFirmwareVersionList(List<String> firmwareVersionList) {
        this.firmwareVersionList = firmwareVersionList;
    }
    
    public String getMinFirmwareVersion() {
        return minFirmwareVersion;
    }
    
    public void setMinFirmwareVersion(String minFirmwareVersion) {
        this.minFirmwareVersion = minFirmwareVersion;
    }
    
    public String getMaxFirmwareVersion() {
        return maxFirmwareVersion;
    }
    
    public void setMaxFirmwareVersion(String maxFirmwareVersion) {
        this.maxFirmwareVersion = maxFirmwareVersion;
    }
    
    public LocalDateTime getCreatedStartDate() {
        return createdStartDate;
    }
    
    public void setCreatedStartDate(LocalDateTime createdStartDate) {
        this.createdStartDate = createdStartDate;
    }
    
    public LocalDateTime getCreatedEndDate() {
        return createdEndDate;
    }
    
    public void setCreatedEndDate(LocalDateTime createdEndDate) {
        this.createdEndDate = createdEndDate;
    }
    
    public LocalDateTime getLastOnlineStartDate() {
        return lastOnlineStartDate;
    }
    
    public void setLastOnlineStartDate(LocalDateTime lastOnlineStartDate) {
        this.lastOnlineStartDate = lastOnlineStartDate;
    }
    
    public LocalDateTime getLastOnlineEndDate() {
        return lastOnlineEndDate;
    }
    
    public void setLastOnlineEndDate(LocalDateTime lastOnlineEndDate) {
        this.lastOnlineEndDate = lastOnlineEndDate;
    }
    
    public LocalDateTime getActivatedStartDate() {
        return activatedStartDate;
    }
    
    public void setActivatedStartDate(LocalDateTime activatedStartDate) {
        this.activatedStartDate = activatedStartDate;
    }
    
    public LocalDateTime getActivatedEndDate() {
        return activatedEndDate;
    }
    
    public void setActivatedEndDate(LocalDateTime activatedEndDate) {
        this.activatedEndDate = activatedEndDate;
    }
    
    public LocalDateTime getUpdatedStartDate() {
        return updatedStartDate;
    }
    
    public void setUpdatedStartDate(LocalDateTime updatedStartDate) {
        this.updatedStartDate = updatedStartDate;
    }
    
    public LocalDateTime getUpdatedEndDate() {
        return updatedEndDate;
    }
    
    public void setUpdatedEndDate(LocalDateTime updatedEndDate) {
        this.updatedEndDate = updatedEndDate;
    }
    
    public String getDateField() {
        return dateField;
    }
    
    public void setDateField(String dateField) {
        this.dateField = dateField;
    }
    
    public Boolean getIsOnline() {
        return isOnline;
    }
    
    public void setIsOnline(Boolean isOnline) {
        this.isOnline = isOnline;
    }
    
    public Boolean getIsActivated() {
        return isActivated;
    }
    
    public void setIsActivated(Boolean isActivated) {
        this.isActivated = isActivated;
    }
    
    public Integer getLastOnlineHours() {
        return lastOnlineHours;
    }
    
    public void setLastOnlineHours(Integer lastOnlineHours) {
        this.lastOnlineHours = lastOnlineHours;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public Boolean getIncludeDeleted() {
        return includeDeleted;
    }
    
    public void setIncludeDeleted(Boolean includeDeleted) {
        this.includeDeleted = includeDeleted;
    }
    
    public String getSortBy() {
        return sortBy;
    }
    
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
    
    public String getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public Integer getOffset() {
        return offset;
    }
    
    public void setOffset(Integer offset) {
        this.offset = offset;
    }
    
    public Integer getLimit() {
        return limit;
    }
    
    public void setLimit(Integer limit) {
        this.limit = limit;
    }
    
    public Integer getPage() {
        return page;
    }
    
    public void setPage(Integer page) {
        this.page = page;
        // 自动计算offset
        if (page != null && pageSize != null) {
            this.offset = (page - 1) * pageSize;
            this.limit = pageSize;
        }
    }
    
    public Integer getPageSize() {
        return pageSize;
    }
    
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        // 自动计算offset
        if (page != null && pageSize != null) {
            this.offset = (page - 1) * pageSize;
            this.limit = pageSize;
        }
    }
    
    /**
     * 验证搜索条件的有效性
     * 
     * @return 是否有效
     */
    public boolean isValid() {
        // 检查分页参数
        if (page != null && page < 1) {
            return false;
        }
        if (pageSize != null && (pageSize < 1 || pageSize > 1000)) {
            return false;
        }
        
        // 检查日期范围
        if (createdStartDate != null && createdEndDate != null && 
            createdStartDate.isAfter(createdEndDate)) {
            return false;
        }
        if (lastOnlineStartDate != null && lastOnlineEndDate != null && 
            lastOnlineStartDate.isAfter(lastOnlineEndDate)) {
            return false;
        }
        if (activatedStartDate != null && activatedEndDate != null && 
            activatedStartDate.isAfter(activatedEndDate)) {
            return false;
        }
        if (updatedStartDate != null && updatedEndDate != null && 
            updatedStartDate.isAfter(updatedEndDate)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 检查是否有搜索条件
     * 
     * @return 是否有搜索条件
     */
    public boolean hasSearchConditions() {
        return (keyword != null && !keyword.trim().isEmpty()) ||
               status != null ||
               (statusList != null && !statusList.isEmpty()) ||
               model != null ||
               (modelList != null && !modelList.isEmpty()) ||
               customerId != null ||
               (customerName != null && !customerName.trim().isEmpty()) ||
               (customerIds != null && !customerIds.isEmpty()) ||
               (firmwareVersion != null && !firmwareVersion.trim().isEmpty()) ||
               (firmwareVersionList != null && !firmwareVersionList.isEmpty()) ||
               createdStartDate != null ||
               createdEndDate != null ||
               lastOnlineStartDate != null ||
               lastOnlineEndDate != null ||
               activatedStartDate != null ||
               activatedEndDate != null ||
               updatedStartDate != null ||
               updatedEndDate != null ||
               isOnline != null ||
               isActivated != null ||
               lastOnlineHours != null ||
               (notes != null && !notes.trim().isEmpty()) ||
               (createdBy != null && !createdBy.trim().isEmpty());
    }
    
    @Override
    public String toString() {
        return "ManagedDeviceSearchCriteria{" +
                "keyword='" + keyword + '\'' +
                ", searchScope='" + searchScope + '\'' +
                ", status=" + status +
                ", model=" + model +
                ", customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                ", firmwareVersion='" + firmwareVersion + '\'' +
                ", createdStartDate=" + createdStartDate +
                ", createdEndDate=" + createdEndDate +
                ", isOnline=" + isOnline +
                ", isActivated=" + isActivated +
                ", sortBy='" + sortBy + '\'' +
                ", sortOrder='" + sortOrder + '\'' +
                ", page=" + page +
                ", pageSize=" + pageSize +
                '}';
    }
}