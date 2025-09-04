package com.yxrobot.entity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

/**
 * 设备管理实体类
 * 对应managed_devices表
 * 专门用于设备管理模块，与现有devices表共存
 * 
 * 字段映射规范：
 * - 数据库字段使用snake_case命名（如：serial_number, customer_id）
 * - Java属性使用camelCase命名（如：serialNumber, customerId）
 * - MyBatis映射文件中确保column和property正确对应
 */
public class ManagedDevice {
    
    private Long id;
    
    @NotBlank(message = "设备序列号不能为空")
    @Pattern(regexp = "^[A-Z0-9-]{10,20}$", message = "设备序列号格式不正确")
    private String serialNumber;        // 设备序列号
    
    @NotNull(message = "设备型号不能为空")
    private DeviceModel model;          // 设备型号（枚举）
    
    @NotNull(message = "设备状态不能为空")
    private DeviceStatus status;        // 设备状态（枚举）
    
    @NotBlank(message = "固件版本不能为空")
    @Pattern(regexp = "^\\d+\\.\\d+\\.\\d+$", message = "固件版本格式不正确")
    private String firmwareVersion;     // 固件版本
    
    @NotNull(message = "客户ID不能为空")
    private Long customerId;            // 客户ID（引用customers表）
    
    @NotBlank(message = "客户名称不能为空")
    private String customerName;        // 客户名称（冗余字段，避免关联查询）
    
    @NotBlank(message = "客户电话不能为空")
    private String customerPhone;       // 客户电话（冗余字段，避免关联查询）
    private LocalDateTime lastOnlineAt; // 最后在线时间
    private LocalDateTime activatedAt;  // 激活时间
    private LocalDateTime createdAt;    // 创建时间
    private LocalDateTime updatedAt;    // 更新时间
    private String createdBy;           // 创建人
    private String notes;               // 设备备注
    private Boolean isDeleted;          // 是否删除（软删除）
    
    // 关联对象（用于查询时填充）
    private ManagedDeviceSpecification specifications;  // 技术参数
    private ManagedDeviceUsageStats usageStats;        // 使用统计
    private ManagedDeviceConfiguration configuration;   // 设备配置
    private ManagedDeviceLocation location;            // 位置信息
    private java.util.List<ManagedDeviceMaintenanceRecord> maintenanceRecords; // 维护记录列表
    
    // 构造函数
    public ManagedDevice() {}
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getSerialNumber() {
        return serialNumber;
    }
    
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public DeviceModel getModel() {
        return model;
    }
    
    public void setModel(DeviceModel model) {
        this.model = model;
    }
    
    public DeviceStatus getStatus() {
        return status;
    }
    
    public void setStatus(DeviceStatus status) {
        this.status = status;
    }
    
    public String getFirmwareVersion() {
        return firmwareVersion;
    }
    
    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
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
    
    public String getCustomerPhone() {
        return customerPhone;
    }
    
    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }
    
    public LocalDateTime getLastOnlineAt() {
        return lastOnlineAt;
    }
    
    public void setLastOnlineAt(LocalDateTime lastOnlineAt) {
        this.lastOnlineAt = lastOnlineAt;
    }
    
    public LocalDateTime getActivatedAt() {
        return activatedAt;
    }
    
    public void setActivatedAt(LocalDateTime activatedAt) {
        this.activatedAt = activatedAt;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public Boolean getIsDeleted() {
        return isDeleted;
    }
    
    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    
    public ManagedDeviceSpecification getSpecifications() {
        return specifications;
    }
    
    public void setSpecifications(ManagedDeviceSpecification specifications) {
        this.specifications = specifications;
    }
    
    public ManagedDeviceUsageStats getUsageStats() {
        return usageStats;
    }
    
    public void setUsageStats(ManagedDeviceUsageStats usageStats) {
        this.usageStats = usageStats;
    }
    
    public ManagedDeviceConfiguration getConfiguration() {
        return configuration;
    }
    
    public void setConfiguration(ManagedDeviceConfiguration configuration) {
        this.configuration = configuration;
    }
    
    public ManagedDeviceLocation getLocation() {
        return location;
    }
    
    public void setLocation(ManagedDeviceLocation location) {
        this.location = location;
    }
    
    public java.util.List<ManagedDeviceMaintenanceRecord> getMaintenanceRecords() {
        return maintenanceRecords;
    }
    
    public void setMaintenanceRecords(java.util.List<ManagedDeviceMaintenanceRecord> maintenanceRecords) {
        this.maintenanceRecords = maintenanceRecords;
    }
}