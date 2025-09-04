package com.yxrobot.service;

import com.yxrobot.dto.DeviceMonitoringDataDTO;
import com.yxrobot.entity.DeviceStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 设备监控数据验证服务
 * 确保设备监控数据的准确性和完整性
 * 
 * @author YXRobot Development Team
 * @since 2024
 */
@Service
public class DeviceMonitoringDataValidationService {
    
    private static final Logger logger = LoggerFactory.getLogger(DeviceMonitoringDataValidationService.class);
    
    // 序列号格式正则表达式
    private static final Pattern SERIAL_NUMBER_PATTERN = Pattern.compile("^[A-Z0-9-]{10,20}$");
    
    // 固件版本格式正则表达式
    private static final Pattern FIRMWARE_VERSION_PATTERN = Pattern.compile("^v?\\d+\\.\\d+\\.\\d+$");
    
    // IP地址格式正则表达式
    private static final Pattern IP_ADDRESS_PATTERN = Pattern.compile(
        "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"
    );
    
    /**
     * 验证设备监控数据的完整性和准确性
     * 
     * @param data 设备监控数据
     * @return 验证结果
     */
    public ValidationResult validateDeviceData(DeviceMonitoringDataDTO data) {
        logger.debug("开始验证设备监控数据: deviceId={}", data != null ? data.getId() : null);
        
        ValidationResult result = new ValidationResult();
        
        if (data == null) {
            result.addError("设备监控数据不能为空");
            return result;
        }
        
        // 1. 验证基本字段
        validateBasicFields(data, result);
        
        // 2. 验证格式规范
        validateFieldFormats(data, result);
        
        // 3. 验证业务逻辑
        validateBusinessLogic(data, result);
        
        // 4. 验证关联数据
        validateAssociatedData(data, result);
        
        logger.debug("设备监控数据验证完成: deviceId={}, 错误数={}, 警告数={}", 
                    data.getId(), result.getErrors().size(), result.getWarnings().size());
        
        return result;
    }
    
    /**
     * 验证基本字段
     */
    private void validateBasicFields(DeviceMonitoringDataDTO data, ValidationResult result) {
        // 验证必填字段
        if (data.getId() == null || data.getId().trim().isEmpty()) {
            result.addError("设备ID不能为空");
        }
        
        if (data.getSerialNumber() == null || data.getSerialNumber().trim().isEmpty()) {
            result.addError("设备序列号不能为空");
        }
        
        if (data.getStatus() == null || data.getStatus().trim().isEmpty()) {
            result.addError("设备状态不能为空");
        }
        
        if (data.getCustomerName() == null || data.getCustomerName().trim().isEmpty()) {
            result.addWarning("客户名称为空");
        }
        
        if (data.getModel() == null || data.getModel().trim().isEmpty()) {
            result.addWarning("设备型号为空");
        }
    }
    
    /**
     * 验证字段格式
     */
    private void validateFieldFormats(DeviceMonitoringDataDTO data, ValidationResult result) {
        // 验证序列号格式
        if (data.getSerialNumber() != null && 
            !SERIAL_NUMBER_PATTERN.matcher(data.getSerialNumber()).matches()) {
            result.addError("设备序列号格式不正确，应为10-20位大写字母、数字或连字符");
        }
        
        // 验证设备状态
        if (data.getStatus() != null) {
            try {
                DeviceStatus.valueOf(data.getStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                result.addError("设备状态值无效: " + data.getStatus());
            }
        }
        
        // 验证固件版本格式
        if (data.getFirmwareVersion() != null && 
            !FIRMWARE_VERSION_PATTERN.matcher(data.getFirmwareVersion()).matches()) {
            result.addWarning("固件版本格式不规范，建议使用 v1.0.0 格式");
        }
        
        // 验证网络状态中的IP地址格式
        if (data.getNetwork() != null && data.getNetwork().getIpAddress() != null) {
            if (!IP_ADDRESS_PATTERN.matcher(data.getNetwork().getIpAddress()).matches()) {
                result.addError("IP地址格式不正确: " + data.getNetwork().getIpAddress());
            }
        }
    }
    
    /**
     * 验证业务逻辑
     */
    private void validateBusinessLogic(DeviceMonitoringDataDTO data, ValidationResult result) {
        // 验证时间逻辑
        if (data.getLastOnlineAt() != null && data.getCreatedAt() != null) {
            if (data.getLastOnlineAt().isBefore(data.getCreatedAt())) {
                result.addWarning("最后在线时间早于创建时间");
            }
        }
        
        // 验证未来时间
        LocalDateTime now = LocalDateTime.now();
        if (data.getLastOnlineAt() != null && data.getLastOnlineAt().isAfter(now.plusMinutes(5))) {
            result.addError("最后在线时间不能是未来时间");
        }
        
        if (data.getCreatedAt() != null && data.getCreatedAt().isAfter(now.plusMinutes(5))) {
            result.addError("创建时间不能是未来时间");
        }
        
        // 验证设备状态与最后在线时间的一致性
        if ("ONLINE".equals(data.getStatus()) && data.getLastOnlineAt() != null) {
            LocalDateTime threshold = now.minusMinutes(30);
            if (data.getLastOnlineAt().isBefore(threshold)) {
                result.addWarning("设备状态为在线，但最后在线时间超过30分钟前");
            }
        }
        
        if ("OFFLINE".equals(data.getStatus()) && data.getLastOnlineAt() != null) {
            LocalDateTime threshold = now.minusMinutes(5);
            if (data.getLastOnlineAt().isAfter(threshold)) {
                result.addWarning("设备状态为离线，但最后在线时间在5分钟内");
            }
        }
    }
    
    /**
     * 验证关联数据
     */
    private void validateAssociatedData(DeviceMonitoringDataDTO data, ValidationResult result) {
        // 验证性能数据
        if (data.getPerformance() != null) {
            validatePerformanceData(data.getPerformance(), result);
        }
        
        // 验证位置数据
        if (data.getLocation() != null) {
            validateLocationData(data.getLocation(), result);
        }
        
        // 验证网络状态数据
        if (data.getNetwork() != null) {
            validateNetworkData(data.getNetwork(), result);
        }
    }
    
    /**
     * 验证性能数据
     */
    private void validatePerformanceData(DeviceMonitoringDataDTO.PerformanceDTO performance, ValidationResult result) {
        // 验证CPU使用率
        if (performance.getCpu() != null) {
            if (performance.getCpu().compareTo(java.math.BigDecimal.ZERO) < 0 || 
                performance.getCpu().compareTo(java.math.BigDecimal.valueOf(100)) > 0) {
                result.addError("CPU使用率必须在0-100之间");
            }
            
            if (performance.getCpu().compareTo(java.math.BigDecimal.valueOf(95)) > 0) {
                result.addWarning("CPU使用率过高: " + performance.getCpu() + "%");
            }
        }
        
        // 验证内存使用率
        if (performance.getMemory() != null) {
            if (performance.getMemory().compareTo(java.math.BigDecimal.ZERO) < 0 || 
                performance.getMemory().compareTo(java.math.BigDecimal.valueOf(100)) > 0) {
                result.addError("内存使用率必须在0-100之间");
            }
            
            if (performance.getMemory().compareTo(java.math.BigDecimal.valueOf(90)) > 0) {
                result.addWarning("内存使用率过高: " + performance.getMemory() + "%");
            }
        }
        
        // 验证磁盘使用率
        if (performance.getDisk() != null) {
            if (performance.getDisk().compareTo(java.math.BigDecimal.ZERO) < 0 || 
                performance.getDisk().compareTo(java.math.BigDecimal.valueOf(100)) > 0) {
                result.addError("磁盘使用率必须在0-100之间");
            }
            
            if (performance.getDisk().compareTo(java.math.BigDecimal.valueOf(85)) > 0) {
                result.addWarning("磁盘使用率过高: " + performance.getDisk() + "%");
            }
        }
        
        // 验证温度
        if (performance.getTemperature() != null) {
            if (performance.getTemperature().compareTo(java.math.BigDecimal.valueOf(-40)) < 0 || 
                performance.getTemperature().compareTo(java.math.BigDecimal.valueOf(100)) > 0) {
                result.addError("设备温度超出合理范围(-40°C到100°C)");
            }
            
            if (performance.getTemperature().compareTo(java.math.BigDecimal.valueOf(80)) > 0) {
                result.addWarning("设备温度过高: " + performance.getTemperature() + "°C");
            }
        }
        
        // 验证电池电量
        if (performance.getBatteryLevel() != null) {
            if (performance.getBatteryLevel().compareTo(java.math.BigDecimal.ZERO) < 0 || 
                performance.getBatteryLevel().compareTo(java.math.BigDecimal.valueOf(100)) > 0) {
                result.addError("电池电量必须在0-100之间");
            }
            
            if (performance.getBatteryLevel().compareTo(java.math.BigDecimal.valueOf(20)) < 0) {
                result.addWarning("电池电量过低: " + performance.getBatteryLevel() + "%");
            }
        }
    }
    
    /**
     * 验证位置数据
     */
    private void validateLocationData(DeviceMonitoringDataDTO.LocationDTO location, ValidationResult result) {
        // 验证纬度
        if (location.getLatitude() != null) {
            if (location.getLatitude().compareTo(java.math.BigDecimal.valueOf(-90)) < 0 || 
                location.getLatitude().compareTo(java.math.BigDecimal.valueOf(90)) > 0) {
                result.addError("纬度必须在-90到90之间");
            }
        }
        
        // 验证经度
        if (location.getLongitude() != null) {
            if (location.getLongitude().compareTo(java.math.BigDecimal.valueOf(-180)) < 0 || 
                location.getLongitude().compareTo(java.math.BigDecimal.valueOf(180)) > 0) {
                result.addError("经度必须在-180到180之间");
            }
        }
        
        // 验证位置完整性
        if ((location.getLatitude() != null && location.getLongitude() == null) ||
            (location.getLatitude() == null && location.getLongitude() != null)) {
            result.addWarning("经纬度信息不完整");
        }
        
        // 验证地址长度
        if (location.getAddress() != null && location.getAddress().length() > 200) {
            result.addWarning("地址信息过长，建议控制在200字符以内");
        }
    }
    
    /**
     * 验证网络状态数据
     */
    private void validateNetworkData(DeviceMonitoringDataDTO.NetworkStatusDTO network, ValidationResult result) {
        // 验证信号强度
        if (network.getSignalStrength() != null) {
            if (network.getSignalStrength() < 0 || network.getSignalStrength() > 100) {
                result.addError("信号强度必须在0-100之间");
            }
            
            if (network.getSignalStrength() < 30) {
                result.addWarning("信号强度较弱: " + network.getSignalStrength() + "%");
            }
        }
        
        // 验证网络速度
        if (network.getDownloadSpeed() != null && 
            network.getDownloadSpeed().compareTo(java.math.BigDecimal.ZERO) < 0) {
            result.addError("下载速度不能为负数");
        }
        
        if (network.getUploadSpeed() != null && 
            network.getUploadSpeed().compareTo(java.math.BigDecimal.ZERO) < 0) {
            result.addError("上传速度不能为负数");
        }
        
        // 验证网络类型和连接状态的一致性
        if ("wifi".equals(network.getType()) && "disconnected".equals(network.getConnectionStatus())) {
            if (network.getSignalStrength() != null && network.getSignalStrength() > 0) {
                result.addWarning("WiFi已断开但仍有信号强度");
            }
        }
    }
    
    /**
     * 修复常见的数据问题
     * 
     * @param data 设备监控数据
     * @return 修复后的数据
     */
    public DeviceMonitoringDataDTO fixCommonIssues(DeviceMonitoringDataDTO data) {
        if (data == null) {
            return null;
        }
        
        logger.debug("开始修复设备监控数据问题: deviceId={}", data.getId());
        
        // 修复序列号格式
        if (data.getSerialNumber() != null) {
            data.setSerialNumber(data.getSerialNumber().toUpperCase().trim());
        }
        
        // 修复设备状态格式
        if (data.getStatus() != null) {
            data.setStatus(data.getStatus().toUpperCase().trim());
        }
        
        // 修复客户名称
        if (data.getCustomerName() != null) {
            data.setCustomerName(data.getCustomerName().trim());
        }
        
        // 修复性能数据范围
        if (data.getPerformance() != null) {
            fixPerformanceData(data.getPerformance());
        }
        
        // 修复位置数据
        if (data.getLocation() != null) {
            fixLocationData(data.getLocation());
        }
        
        logger.debug("设备监控数据问题修复完成: deviceId={}", data.getId());
        return data;
    }
    
    /**
     * 修复性能数据
     */
    private void fixPerformanceData(DeviceMonitoringDataDTO.PerformanceDTO performance) {
        // 修复CPU使用率范围
        if (performance.getCpu() != null) {
            if (performance.getCpu().compareTo(java.math.BigDecimal.ZERO) < 0) {
                performance.setCpu(java.math.BigDecimal.ZERO);
            } else if (performance.getCpu().compareTo(java.math.BigDecimal.valueOf(100)) > 0) {
                performance.setCpu(java.math.BigDecimal.valueOf(100));
            }
        }
        
        // 修复内存使用率范围
        if (performance.getMemory() != null) {
            if (performance.getMemory().compareTo(java.math.BigDecimal.ZERO) < 0) {
                performance.setMemory(java.math.BigDecimal.ZERO);
            } else if (performance.getMemory().compareTo(java.math.BigDecimal.valueOf(100)) > 0) {
                performance.setMemory(java.math.BigDecimal.valueOf(100));
            }
        }
        
        // 修复电池电量范围
        if (performance.getBatteryLevel() != null) {
            if (performance.getBatteryLevel().compareTo(java.math.BigDecimal.ZERO) < 0) {
                performance.setBatteryLevel(java.math.BigDecimal.ZERO);
            } else if (performance.getBatteryLevel().compareTo(java.math.BigDecimal.valueOf(100)) > 0) {
                performance.setBatteryLevel(java.math.BigDecimal.valueOf(100));
            }
        }
    }
    
    /**
     * 修复位置数据
     */
    private void fixLocationData(DeviceMonitoringDataDTO.LocationDTO location) {
        // 修复纬度范围
        if (location.getLatitude() != null) {
            if (location.getLatitude().compareTo(java.math.BigDecimal.valueOf(-90)) < 0) {
                location.setLatitude(java.math.BigDecimal.valueOf(-90));
            } else if (location.getLatitude().compareTo(java.math.BigDecimal.valueOf(90)) > 0) {
                location.setLatitude(java.math.BigDecimal.valueOf(90));
            }
        }
        
        // 修复经度范围
        if (location.getLongitude() != null) {
            if (location.getLongitude().compareTo(java.math.BigDecimal.valueOf(-180)) < 0) {
                location.setLongitude(java.math.BigDecimal.valueOf(-180));
            } else if (location.getLongitude().compareTo(java.math.BigDecimal.valueOf(180)) > 0) {
                location.setLongitude(java.math.BigDecimal.valueOf(180));
            }
        }
        
        // 修复地址长度
        if (location.getAddress() != null && location.getAddress().length() > 200) {
            location.setAddress(location.getAddress().substring(0, 200));
        }
    }
    
    /**
     * 验证结果类
     */
    public static class ValidationResult {
        private final List<String> errors = new ArrayList<>();
        private final List<String> warnings = new ArrayList<>();
        
        public void addError(String error) {
            errors.add(error);
        }
        
        public void addWarning(String warning) {
            warnings.add(warning);
        }
        
        public List<String> getErrors() {
            return errors;
        }
        
        public List<String> getWarnings() {
            return warnings;
        }
        
        public boolean hasErrors() {
            return !errors.isEmpty();
        }
        
        public boolean hasWarnings() {
            return !warnings.isEmpty();
        }
        
        public boolean isValid() {
            return !hasErrors();
        }
        
        @Override
        public String toString() {
            return "ValidationResult{" +
                    "errors=" + errors.size() +
                    ", warnings=" + warnings.size() +
                    "}";
        }
    }
}