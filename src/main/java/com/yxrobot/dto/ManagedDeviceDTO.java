package com.yxrobot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 设备管理DTO类
 * 适配前端DeviceManagement.vue页面的数据需求
 * 确保字段名称与前端TypeScript接口完全匹配（camelCase）
 */
public class ManagedDeviceDTO {
    
    private String id;                  // 设备ID（转换为字符串）
    private String serialNumber;        // 设备序列号
    private String model;               // 设备型号
    private String status;              // 设备状态
    private String firmwareVersion;     // 固件版本
    private String customerId;          // 客户ID（转换为字符串）
    private String customerName;        // 客户名称
    private String customerPhone;       // 客户电话
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastOnlineAt; // 最后在线时间
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime activatedAt;  // 激活时间
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;    // 创建时间
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;    // 更新时间
    
    // 格式化的时间字符串（用于前端显示）
    private String lastOnlineAtFormatted;
    private String activatedAtFormatted;
    private String createdAtFormatted;
    private String updatedAtFormatted;
    
    // 状态和型号描述
    private String statusDescription;
    private String modelDescription;
    
    private String createdBy;           // 创建人
    private String notes;               // 设备备注
    
    // 关联信息
    private DeviceSpecificationDTO specifications;  // 技术参数
    private DeviceUsageStatsDTO usageStats;        // 使用统计
    private DeviceConfigurationDTO configuration;   // 设备配置
    private DeviceLocationDTO location;            // 位置信息
    private List<ManagedDeviceMaintenanceRecordDTO> maintenanceRecords; // 维护记录列表
    
    // 构造函数
    public ManagedDeviceDTO() {}
    
    // 内部类：设备技术参数DTO
    public static class DeviceSpecificationDTO {
        private String cpu;
        private String memory;
        private String storage;
        private String display;
        private String battery;
        private List<String> connectivity;
        
        // Getter和Setter方法
        public String getCpu() { return cpu; }
        public void setCpu(String cpu) { this.cpu = cpu; }
        
        public String getMemory() { return memory; }
        public void setMemory(String memory) { this.memory = memory; }
        
        public String getStorage() { return storage; }
        public void setStorage(String storage) { this.storage = storage; }
        
        public String getDisplay() { return display; }
        public void setDisplay(String display) { this.display = display; }
        
        public String getBattery() { return battery; }
        public void setBattery(String battery) { this.battery = battery; }
        
        public List<String> getConnectivity() { return connectivity; }
        public void setConnectivity(List<String> connectivity) { this.connectivity = connectivity; }
    }
    
    // 内部类：设备使用统计DTO
    public static class DeviceUsageStatsDTO {
        private Integer totalRuntime;       // 总运行时间（分钟）
        private Integer usageCount;         // 使用次数
        private Integer averageSessionTime; // 平均使用时长（分钟）
        
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime lastUsedAt;   // 最后使用时间
        
        // Getter和Setter方法
        public Integer getTotalRuntime() { return totalRuntime; }
        public void setTotalRuntime(Integer totalRuntime) { this.totalRuntime = totalRuntime; }
        
        public Integer getUsageCount() { return usageCount; }
        public void setUsageCount(Integer usageCount) { this.usageCount = usageCount; }
        
        public Integer getAverageSessionTime() { return averageSessionTime; }
        public void setAverageSessionTime(Integer averageSessionTime) { this.averageSessionTime = averageSessionTime; }
        
        public LocalDateTime getLastUsedAt() { return lastUsedAt; }
        public void setLastUsedAt(LocalDateTime lastUsedAt) { this.lastUsedAt = lastUsedAt; }
    }
    
    // 内部类：设备配置DTO
    public static class DeviceConfigurationDTO {
        private String language;
        private String timezone;
        private Boolean autoUpdate;
        private Boolean debugMode;
        private Map<String, Object> customSettings;
        
        // Getter和Setter方法
        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }
        
        public String getTimezone() { return timezone; }
        public void setTimezone(String timezone) { this.timezone = timezone; }
        
        public Boolean getAutoUpdate() { return autoUpdate; }
        public void setAutoUpdate(Boolean autoUpdate) { this.autoUpdate = autoUpdate; }
        
        public Boolean getDebugMode() { return debugMode; }
        public void setDebugMode(Boolean debugMode) { this.debugMode = debugMode; }
        
        public Map<String, Object> getCustomSettings() { return customSettings; }
        public void setCustomSettings(Map<String, Object> customSettings) { this.customSettings = customSettings; }
    }
    
    // 内部类：设备位置DTO
    public static class DeviceLocationDTO {
        private Double latitude;
        private Double longitude;
        private String address;
        
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime lastUpdated;
        
        // Getter和Setter方法
        public Double getLatitude() { return latitude; }
        public void setLatitude(Double latitude) { this.latitude = latitude; }
        
        public Double getLongitude() { return longitude; }
        public void setLongitude(Double longitude) { this.longitude = longitude; }
        
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        
        public LocalDateTime getLastUpdated() { return lastUpdated; }
        public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
    }
    
    // 主类的Getter和Setter方法
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getFirmwareVersion() { return firmwareVersion; }
    public void setFirmwareVersion(String firmwareVersion) { this.firmwareVersion = firmwareVersion; }
    
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    
    public LocalDateTime getLastOnlineAt() { return lastOnlineAt; }
    public void setLastOnlineAt(LocalDateTime lastOnlineAt) { this.lastOnlineAt = lastOnlineAt; }
    
    public LocalDateTime getActivatedAt() { return activatedAt; }
    public void setActivatedAt(LocalDateTime activatedAt) { this.activatedAt = activatedAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // 格式化时间的Getter和Setter方法
    public String getLastOnlineAtFormatted() { return lastOnlineAtFormatted; }
    public void setLastOnlineAtFormatted(String lastOnlineAtFormatted) { this.lastOnlineAtFormatted = lastOnlineAtFormatted; }
    
    public String getActivatedAtFormatted() { return activatedAtFormatted; }
    public void setActivatedAtFormatted(String activatedAtFormatted) { this.activatedAtFormatted = activatedAtFormatted; }
    
    public String getCreatedAtFormatted() { return createdAtFormatted; }
    public void setCreatedAtFormatted(String createdAtFormatted) { this.createdAtFormatted = createdAtFormatted; }
    
    public String getUpdatedAtFormatted() { return updatedAtFormatted; }
    public void setUpdatedAtFormatted(String updatedAtFormatted) { this.updatedAtFormatted = updatedAtFormatted; }
    
    // 状态和型号描述的Getter和Setter方法
    public String getStatusDescription() { return statusDescription; }
    public void setStatusDescription(String statusDescription) { this.statusDescription = statusDescription; }
    
    public String getModelDescription() { return modelDescription; }
    public void setModelDescription(String modelDescription) { this.modelDescription = modelDescription; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public DeviceSpecificationDTO getSpecifications() { return specifications; }
    public void setSpecifications(DeviceSpecificationDTO specifications) { this.specifications = specifications; }
    
    public DeviceUsageStatsDTO getUsageStats() { return usageStats; }
    public void setUsageStats(DeviceUsageStatsDTO usageStats) { this.usageStats = usageStats; }
    
    public DeviceConfigurationDTO getConfiguration() { return configuration; }
    public void setConfiguration(DeviceConfigurationDTO configuration) { this.configuration = configuration; }
    
    public DeviceLocationDTO getLocation() { return location; }
    public void setLocation(DeviceLocationDTO location) { this.location = location; }
    
    public List<ManagedDeviceMaintenanceRecordDTO> getMaintenanceRecords() { return maintenanceRecords; }
    public void setMaintenanceRecords(List<ManagedDeviceMaintenanceRecordDTO> maintenanceRecords) { this.maintenanceRecords = maintenanceRecords; }
}