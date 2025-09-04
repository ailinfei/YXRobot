package com.yxrobot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 设备监控数据DTO类
 * 适配前端DeviceMonitoring.vue页面的设备列表数据需求
 * 确保字段名称与前端TypeScript接口完全匹配（camelCase）
 * 
 * 对应前端接口：
 * interface Device {
 *   id: string;
 *   serialNumber: string;
 *   customerName: string;
 *   status: 'online' | 'offline' | 'error' | 'maintenance';
 *   performance: { cpu: number; memory: number; };
 *   lastOnlineAt: string;
 *   model: string;
 *   firmwareVersion: string;
 *   location: { latitude: number; longitude: number; address: string; };
 * }
 * 
 * @author YXRobot Development Team
 * @since 2024
 */
public class DeviceMonitoringDataDTO {
    
    private String id;                  // 设备ID（转换为字符串）
    private String serialNumber;        // 设备序列号（前端字段名：serialNumber）
    private String customerName;        // 客户名称（前端字段名：customerName）
    private String status;              // 设备状态（前端字段名：status）
    private String model;               // 设备型号（前端字段名：model）
    private String firmwareVersion;     // 固件版本（前端字段名：firmwareVersion）
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastOnlineAt; // 最后在线时间（前端字段名：lastOnlineAt）
    
    // 性能指标对象（前端字段名：performance）
    private PerformanceDTO performance;
    
    // 位置信息对象（前端字段名：location）
    private LocationDTO location;
    
    // 网络状态信息
    private NetworkStatusDTO network;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;    // 创建时间
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;    // 更新时间
    
    // 格式化的时间字符串（用于前端显示）
    private String lastOnlineAtFormatted;
    private String createdAtFormatted;
    private String updatedAtFormatted;
    
    // 状态描述
    private String statusDescription;
    
    // 内嵌类：性能指标DTO
    public static class PerformanceDTO {
        private BigDecimal cpu;         // CPU使用率（前端字段名：cpu）
        private BigDecimal memory;      // 内存使用率（前端字段名：memory）
        private BigDecimal disk;        // 磁盘使用率
        private BigDecimal temperature; // 设备温度
        private BigDecimal batteryLevel; // 电池电量
        
        public PerformanceDTO() {}
        
        public PerformanceDTO(BigDecimal cpu, BigDecimal memory) {
            this.cpu = cpu;
            this.memory = memory;
        }
        
        // Getter和Setter方法
        public BigDecimal getCpu() {
            return cpu;
        }
        
        public void setCpu(BigDecimal cpu) {
            this.cpu = cpu;
        }
        
        public BigDecimal getMemory() {
            return memory;
        }
        
        public void setMemory(BigDecimal memory) {
            this.memory = memory;
        }
        
        public BigDecimal getDisk() {
            return disk;
        }
        
        public void setDisk(BigDecimal disk) {
            this.disk = disk;
        }
        
        public BigDecimal getTemperature() {
            return temperature;
        }
        
        public void setTemperature(BigDecimal temperature) {
            this.temperature = temperature;
        }
        
        public BigDecimal getBatteryLevel() {
            return batteryLevel;
        }
        
        public void setBatteryLevel(BigDecimal batteryLevel) {
            this.batteryLevel = batteryLevel;
        }
    }
    
    // 内嵌类：位置信息DTO
    public static class LocationDTO {
        private BigDecimal latitude;    // 纬度（前端字段名：latitude）
        private BigDecimal longitude;   // 经度（前端字段名：longitude）
        private String address;         // 地址（前端字段名：address）
        
        public LocationDTO() {}
        
        public LocationDTO(BigDecimal latitude, BigDecimal longitude, String address) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.address = address;
        }
        
        // Getter和Setter方法
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
    }
    
    // 内嵌类：网络状态DTO
    public static class NetworkStatusDTO {
        private String type;            // 网络类型
        private Integer signalStrength; // 信号强度
        private String connectionStatus; // 连接状态
        private String ipAddress;       // IP地址
        private BigDecimal downloadSpeed; // 下载速度
        private BigDecimal uploadSpeed;   // 上传速度
        
        public NetworkStatusDTO() {}
        
        // Getter和Setter方法
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
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
    }
    
    // 构造函数
    public DeviceMonitoringDataDTO() {}
    
    public DeviceMonitoringDataDTO(String id, String serialNumber, String customerName, String status) {
        this.id = id;
        this.serialNumber = serialNumber;
        this.customerName = customerName;
        this.status = status;
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
    
    public String getFirmwareVersion() {
        return firmwareVersion;
    }
    
    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }
    
    public LocalDateTime getLastOnlineAt() {
        return lastOnlineAt;
    }
    
    public void setLastOnlineAt(LocalDateTime lastOnlineAt) {
        this.lastOnlineAt = lastOnlineAt;
    }
    
    public PerformanceDTO getPerformance() {
        return performance;
    }
    
    public void setPerformance(PerformanceDTO performance) {
        this.performance = performance;
    }
    
    public LocationDTO getLocation() {
        return location;
    }
    
    public void setLocation(LocationDTO location) {
        this.location = location;
    }
    
    public NetworkStatusDTO getNetwork() {
        return network;
    }
    
    public void setNetwork(NetworkStatusDTO network) {
        this.network = network;
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
    
    public String getLastOnlineAtFormatted() {
        return lastOnlineAtFormatted;
    }
    
    public void setLastOnlineAtFormatted(String lastOnlineAtFormatted) {
        this.lastOnlineAtFormatted = lastOnlineAtFormatted;
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
    
    public String getStatusDescription() {
        return statusDescription;
    }
    
    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }
    
    @Override
    public String toString() {
        return "DeviceMonitoringDataDTO{" +
                "id='" + id + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", customerName='" + customerName + '\'' +
                ", status='" + status + '\'' +
                ", model='" + model + '\'' +
                ", lastOnlineAt=" + lastOnlineAt +
                '}';
    }
}