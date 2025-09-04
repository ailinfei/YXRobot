package com.yxrobot.entity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 设备监控数据实体类
 * 对应device_monitoring_data表
 * 
 * 字段映射规范：
 * - 数据库字段使用snake_case命名（如：device_id, serial_number）
 * - Java属性使用camelCase命名（如：deviceId, serialNumber）
 * - MyBatis映射文件中确保column和property正确对应
 * 
 * @author YXRobot Development Team
 * @since 2024
 */
public class DeviceMonitoringData {
    
    private Long id;
    
    @NotNull(message = "设备ID不能为空")
    private Long deviceId;              // 设备ID（引用managed_devices表）
    
    @NotBlank(message = "设备序列号不能为空")
    @Pattern(regexp = "^[A-Z0-9-]{10,20}$", message = "设备序列号格式不正确")
    private String serialNumber;        // 设备序列号（冗余字段）
    
    private String customerName;        // 客户名称（冗余字段）
    
    @NotNull(message = "设备状态不能为空")
    private DeviceStatus status;        // 设备状态
    
    private LocalDateTime lastOnlineAt; // 最后在线时间
    
    private String model;               // 设备型号
    
    @Pattern(regexp = "^v\\d+\\.\\d+\\.\\d+$", message = "固件版本格式不正确")
    private String firmwareVersion;     // 固件版本
    
    private BigDecimal locationLatitude;  // 位置纬度
    private BigDecimal locationLongitude; // 位置经度
    private String locationAddress;       // 位置地址
    
    private LocalDateTime createdAt;    // 创建时间
    private LocalDateTime updatedAt;    // 更新时间
    
    // 构造函数
    public DeviceMonitoringData() {}
    
    public DeviceMonitoringData(Long deviceId, String serialNumber, String customerName, DeviceStatus status) {
        this.deviceId = deviceId;
        this.serialNumber = serialNumber;
        this.customerName = customerName;
        this.status = status;
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
    
    public String getSerialNumber() {
        return serialNumber;
    }
    
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public DeviceStatus getStatus() {
        return status;
    }
    
    public void setStatus(DeviceStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getLastOnlineAt() {
        return lastOnlineAt;
    }
    
    public void setLastOnlineAt(LocalDateTime lastOnlineAt) {
        this.lastOnlineAt = lastOnlineAt;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public String getFirmwareVersion() {
        return firmwareVersion;
    }
    
    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }
    
    public BigDecimal getLocationLatitude() {
        return locationLatitude;
    }
    
    public void setLocationLatitude(BigDecimal locationLatitude) {
        this.locationLatitude = locationLatitude;
    }
    
    public BigDecimal getLocationLongitude() {
        return locationLongitude;
    }
    
    public void setLocationLongitude(BigDecimal locationLongitude) {
        this.locationLongitude = locationLongitude;
    }
    
    public String getLocationAddress() {
        return locationAddress;
    }
    
    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
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
    
    @Override
    public String toString() {
        return "DeviceMonitoringData{" +
                "id=" + id +
                ", deviceId=" + deviceId +
                ", serialNumber='" + serialNumber + '\'' +
                ", customerName='" + customerName + '\'' +
                ", status=" + status +
                ", lastOnlineAt=" + lastOnlineAt +
                ", model='" + model + '\'' +
                ", firmwareVersion='" + firmwareVersion + '\'' +
                '}';
    }
}