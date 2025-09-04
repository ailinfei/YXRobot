package com.yxrobot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 设备网络状态DTO类
 * 适配前端DeviceMonitoring.vue页面的网络状态图表数据需求
 * 确保字段名称与前端TypeScript接口完全匹配（camelCase）
 * 
 * @author YXRobot Development Team
 * @since 2024
 */
public class DeviceNetworkStatusDTO {
    
    private String id;                  // 网络状态ID（转换为字符串）
    private String deviceId;            // 设备ID（转换为字符串）
    
    // 网络基本信息
    private String networkType;         // 网络类型（wifi、ethernet、4g、5g等）
    private Integer signalStrength;     // 信号强度百分比
    private String connectionStatus;    // 连接状态（connected、disconnected、connecting）
    
    // 网络地址信息
    private String ipAddress;           // IP地址
    private String macAddress;          // MAC地址
    
    // 网络性能指标
    private BigDecimal downloadSpeed;   // 下载速度（Mbps）
    private BigDecimal uploadSpeed;     // 上传速度（Mbps）
    private Integer pingLatency;        // Ping延迟（毫秒）
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastConnectedAt; // 最后连接时间
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;    // 创建时间
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;    // 更新时间
    
    // 格式化的时间字符串（用于前端显示）
    private String lastConnectedAtFormatted;
    private String createdAtFormatted;
    private String updatedAtFormatted;
    
    // 网络状态描述
    private String networkTypeDescription;    // 网络类型描述
    private String connectionStatusDescription; // 连接状态描述
    private String signalStrengthDescription; // 信号强度描述
    private String speedDescription;          // 网速描述
    private String latencyDescription;        // 延迟描述
    
    // 网络质量评级
    private String networkQuality;      // 网络质量（优秀、良好、一般、差）
    private String qualityColor;        // 质量颜色标识
    
    // 构造函数
    public DeviceNetworkStatusDTO() {}
    
    public DeviceNetworkStatusDTO(String deviceId, String networkType, String connectionStatus) {
        this.deviceId = deviceId;
        this.networkType = networkType;
        this.connectionStatus = connectionStatus;
    }
    
    // Getter和Setter方法
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    
    public String getNetworkType() {
        return networkType;
    }
    
    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }
    
    public Integer getSignalStrength() {
        return signalStrength;
    }
    
    public void setSignalStrength(Integer signalStrength) {
        this.signalStrength = signalStrength;
    }
    
    public String getConnectionStatus() {
        return connectionStatus;
    }
    
    public void setConnectionStatus(String connectionStatus) {
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
    
    public String getLastConnectedAtFormatted() {
        return lastConnectedAtFormatted;
    }
    
    public void setLastConnectedAtFormatted(String lastConnectedAtFormatted) {
        this.lastConnectedAtFormatted = lastConnectedAtFormatted;
    }
    
    public String getCreatedAtFormatted() {
        return createdAtFormatted;
    }
    
    public void setCreatedAtFormatted(String createdAtFormatted) {
        this.createdAtFormatted = createdAtFormatted;
    }
    
    public String getUpdatedAtFormatted() {
        return updatedAtFormatted;
    }
    
    public void setUpdatedAtFormatted(String updatedAtFormatted) {
        this.updatedAtFormatted = updatedAtFormatted;
    }
    
    public String getNetworkTypeDescription() {
        return networkTypeDescription;
    }
    
    public void setNetworkTypeDescription(String networkTypeDescription) {
        this.networkTypeDescription = networkTypeDescription;
    }
    
    public String getConnectionStatusDescription() {
        return connectionStatusDescription;
    }
    
    public void setConnectionStatusDescription(String connectionStatusDescription) {
        this.connectionStatusDescription = connectionStatusDescription;
    }
    
    public String getSignalStrengthDescription() {
        return signalStrengthDescription;
    }
    
    public void setSignalStrengthDescription(String signalStrengthDescription) {
        this.signalStrengthDescription = signalStrengthDescription;
    }
    
    public String getSpeedDescription() {
        return speedDescription;
    }
    
    public void setSpeedDescription(String speedDescription) {
        this.speedDescription = speedDescription;
    }
    
    public String getLatencyDescription() {
        return latencyDescription;
    }
    
    public void setLatencyDescription(String latencyDescription) {
        this.latencyDescription = latencyDescription;
    }
    
    public String getNetworkQuality() {
        return networkQuality;
    }
    
    public void setNetworkQuality(String networkQuality) {
        this.networkQuality = networkQuality;
    }
    
    public String getQualityColor() {
        return qualityColor;
    }
    
    public void setQualityColor(String qualityColor) {
        this.qualityColor = qualityColor;
    }
    
    @Override
    public String toString() {
        return "DeviceNetworkStatusDTO{" +
                "id='" + id + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", networkType='" + networkType + '\'' +
                ", signalStrength=" + signalStrength +
                ", connectionStatus='" + connectionStatus + '\'' +
                ", downloadSpeed=" + downloadSpeed +
                ", uploadSpeed=" + uploadSpeed +
                ", pingLatency=" + pingLatency +
                '}';
    }
}