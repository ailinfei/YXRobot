package com.yxrobot.entity;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 设备网络状态实体类
 * 对应device_network_status表
 * 
 * 字段映射规范：
 * - 数据库字段使用snake_case命名（如：network_type, signal_strength）
 * - Java属性使用camelCase命名（如：networkType, signalStrength）
 * - MyBatis映射文件中确保column和property正确对应
 * 
 * @author YXRobot Development Team
 * @since 2024
 */
public class DeviceNetworkStatus {
    
    private Long id;
    
    @NotNull(message = "设备ID不能为空")
    private Long deviceId;              // 设备ID（引用managed_devices表）
    
    private NetworkType networkType;    // 网络类型
    
    @Min(value = 0, message = "信号强度不能为负数")
    @Max(value = 100, message = "信号强度不能超过100")
    private Integer signalStrength;     // 信号强度百分比
    
    @NotNull(message = "连接状态不能为空")
    private ConnectionStatus connectionStatus; // 连接状态
    
    @Pattern(regexp = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$|^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$", 
             message = "IP地址格式不正确")
    private String ipAddress;           // IP地址
    
    @Pattern(regexp = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$", message = "MAC地址格式不正确")
    private String macAddress;          // MAC地址
    
    @DecimalMin(value = "0.00", message = "下载速度不能为负数")
    private BigDecimal downloadSpeed;   // 下载速度（Mbps）
    
    @DecimalMin(value = "0.00", message = "上传速度不能为负数")
    private BigDecimal uploadSpeed;     // 上传速度（Mbps）
    
    @Min(value = 0, message = "Ping延迟不能为负数")
    private Integer pingLatency;        // Ping延迟（毫秒）
    
    private LocalDateTime lastConnectedAt; // 最后连接时间
    
    private LocalDateTime createdAt;    // 创建时间
    private LocalDateTime updatedAt;    // 更新时间
    
    // 构造函数
    public DeviceNetworkStatus() {}
    
    public DeviceNetworkStatus(Long deviceId, NetworkType networkType, ConnectionStatus connectionStatus) {
        this.deviceId = deviceId;
        this.networkType = networkType;
        this.connectionStatus = connectionStatus;
        this.signalStrength = 0;
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
    
    public NetworkType getNetworkType() {
        return networkType;
    }
    
    public void setNetworkType(NetworkType networkType) {
        this.networkType = networkType;
    }
    
    public Integer getSignalStrength() {
        return signalStrength;
    }
    
    public void setSignalStrength(Integer signalStrength) {
        this.signalStrength = signalStrength;
    }
    
    public ConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }
    
    public void setConnectionStatus(ConnectionStatus connectionStatus) {
        this.connectionStatus = connectionStatus;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getMacAddress() {
        return macAddress;
    }
    
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
    
    public BigDecimal getDownloadSpeed() {
        return downloadSpeed;
    }
    
    public void setDownloadSpeed(BigDecimal downloadSpeed) {
        this.downloadSpeed = downloadSpeed;
    }
    
    public BigDecimal getUploadSpeed() {
        return uploadSpeed;
    }
    
    public void setUploadSpeed(BigDecimal uploadSpeed) {
        this.uploadSpeed = uploadSpeed;
    }
    
    public Integer getPingLatency() {
        return pingLatency;
    }
    
    public void setPingLatency(Integer pingLatency) {
        this.pingLatency = pingLatency;
    }
    
    public LocalDateTime getLastConnectedAt() {
        return lastConnectedAt;
    }
    
    public void setLastConnectedAt(LocalDateTime lastConnectedAt) {
        this.lastConnectedAt = lastConnectedAt;
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
    
    // 业务方法
    public boolean isConnected() {
        return ConnectionStatus.CONNECTED.equals(connectionStatus);
    }
    
    public boolean hasGoodSignal() {
        return signalStrength != null && signalStrength >= 70;
    }
    
    public boolean isHighLatency() {
        return pingLatency != null && pingLatency > 100;
    }
    
    @Override
    public String toString() {
        return "DeviceNetworkStatus{" +
                "id=" + id +
                ", deviceId=" + deviceId +
                ", networkType=" + networkType +
                ", signalStrength=" + signalStrength +
                ", connectionStatus=" + connectionStatus +
                ", ipAddress='" + ipAddress + '\'' +
                ", downloadSpeed=" + downloadSpeed +
                ", uploadSpeed=" + uploadSpeed +
                '}';
    }
}