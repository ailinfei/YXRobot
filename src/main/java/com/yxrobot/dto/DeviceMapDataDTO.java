package com.yxrobot.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * 设备地图数据DTO类
 * 适配前端DeviceMonitoring.vue页面的设备分布地图数据需求
 * 确保字段名称与前端TypeScript接口完全匹配（camelCase）
 * 
 * @author YXRobot Development Team
 * @since 2024
 */
public class DeviceMapDataDTO {
    
    private String id;                  // 设备ID（转换为字符串）
    private String serialNumber;        // 设备序列号
    private String customerName;        // 客户名称
    private String status;              // 设备状态
    private String model;               // 设备型号
    
    // 位置信息（地图核心数据）
    private BigDecimal latitude;        // 纬度
    private BigDecimal longitude;       // 经度
    private String address;             // 地址
    
    // 地图显示相关
    private String markerColor;         // 标记颜色（根据状态）
    private String markerIcon;          // 标记图标
    private String statusDescription;   // 状态描述
    
    // 设备基本信息（用于地图弹窗显示）
    private String firmwareVersion;     // 固件版本
    private String lastOnlineAtFormatted; // 最后在线时间（格式化）
    
    // 性能简要信息
    private BigDecimal cpuUsage;        // CPU使用率
    private BigDecimal memoryUsage;     // 内存使用率
    private String performanceLevel;    // 性能等级
    
    // 网络信息
    private String networkType;         // 网络类型
    private Integer signalStrength;     // 信号强度
    private String connectionStatus;    // 连接状态
    
    // 构造函数
    public DeviceMapDataDTO() {}
    
    public DeviceMapDataDTO(String id, String serialNumber, String customerName, String status,
                           BigDecimal latitude, BigDecimal longitude, String address) {
        this.id = id;
        this.serialNumber = serialNumber;
        this.customerName = customerName;
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }
    
    // Getter和Setter方法
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
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
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public BigDecimal getLatitude() {
        return latitude;
    }
    
    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }
    
    public BigDecimal getLongitude() {
        return longitude;
    }
    
    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getMarkerColor() {
        return markerColor;
    }
    
    public void setMarkerColor(String markerColor) {
        this.markerColor = markerColor;
    }
    
    public String getMarkerIcon() {
        return markerIcon;
    }
    
    public void setMarkerIcon(String markerIcon) {
        this.markerIcon = markerIcon;
    }
    
    public String getStatusDescription() {
        return statusDescription;
    }
    
    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }
    
    public String getFirmwareVersion() {
        return firmwareVersion;
    }
    
    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }
    
    public String getLastOnlineAtFormatted() {
        return lastOnlineAtFormatted;
    }
    
    public void setLastOnlineAtFormatted(String lastOnlineAtFormatted) {
        this.lastOnlineAtFormatted = lastOnlineAtFormatted;
    }
    
    public BigDecimal getCpuUsage() {
        return cpuUsage;
    }
    
    public void setCpuUsage(BigDecimal cpuUsage) {
        this.cpuUsage = cpuUsage;
    }
    
    public BigDecimal getMemoryUsage() {
        return memoryUsage;
    }
    
    public void setMemoryUsage(BigDecimal memoryUsage) {
        this.memoryUsage = memoryUsage;
    }
    
    public String getPerformanceLevel() {
        return performanceLevel;
    }
    
    public void setPerformanceLevel(String performanceLevel) {
        this.performanceLevel = performanceLevel;
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
    
    @Override
    public String toString() {
        return "DeviceMapDataDTO{" +
                "id='" + id + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", customerName='" + customerName + '\'' +
                ", status='" + status + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", address='" + address + '\'' +
                '}';
    }
}

/**
 * 设备地图响应DTO类
 * 包含设备列表和统计信息
 */
class DeviceMapResponseDTO {
    
    private List<DeviceMapDataDTO> devices;     // 设备列表
    private MapStatisticsDTO statistics;        // 地图统计信息
    
    // 内嵌类：地图统计信息
    public static class MapStatisticsDTO {
        private Integer totalDevices;           // 设备总数
        private Integer onlineDevices;          // 在线设备数
        private Integer offlineDevices;         // 离线设备数
        private Integer errorDevices;           // 故障设备数
        private String lastUpdateTime;          // 最后更新时间
        
        // 构造函数
        public MapStatisticsDTO() {}
        
        // Getter和Setter方法
        public Integer getTotalDevices() {
            return totalDevices;
        }
        
        public void setTotalDevices(Integer totalDevices) {
            this.totalDevices = totalDevices;
        }
        
        public Integer getOnlineDevices() {
            return onlineDevices;
        }
        
        public void setOnlineDevices(Integer onlineDevices) {
            this.onlineDevices = onlineDevices;
        }
        
        public Integer getOfflineDevices() {
            return offlineDevices;
        }
        
        public void setOfflineDevices(Integer offlineDevices) {
            this.offlineDevices = offlineDevices;
        }
        
        public Integer getErrorDevices() {
            return errorDevices;
        }
        
        public void setErrorDevices(Integer errorDevices) {
            this.errorDevices = errorDevices;
        }
        
        public String getLastUpdateTime() {
            return lastUpdateTime;
        }
        
        public void setLastUpdateTime(String lastUpdateTime) {
            this.lastUpdateTime = lastUpdateTime;
        }
    }
    
    // 构造函数
    public DeviceMapResponseDTO() {}
    
    public DeviceMapResponseDTO(List<DeviceMapDataDTO> devices, MapStatisticsDTO statistics) {
        this.devices = devices;
        this.statistics = statistics;
    }
    
    // Getter和Setter方法
    public List<DeviceMapDataDTO> getDevices() {
        return devices;
    }
    
    public void setDevices(List<DeviceMapDataDTO> devices) {
        this.devices = devices;
    }
    
    public MapStatisticsDTO getStatistics() {
        return statistics;
    }
    
    public void setStatistics(MapStatisticsDTO statistics) {
        this.statistics = statistics;
    }
}