package com.yxrobot.service;

import com.yxrobot.dto.ManagedDeviceDTO;
import com.yxrobot.entity.DeviceModel;
import com.yxrobot.entity.DeviceStatus;
import com.yxrobot.entity.ManagedDevice;
import com.yxrobot.exception.ManagedDeviceException;
import com.yxrobot.mapper.ManagedDeviceMapper;
import com.yxrobot.util.DeviceValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 设备管理数据验证服务
 * 提供设备管理相关的业务验证功能
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@Service
public class ManagedDeviceValidationService {
    
    private static final Logger logger = LoggerFactory.getLogger(ManagedDeviceValidationService.class);
    private static final Logger securityLogger = LoggerFactory.getLogger("SECURITY");
    
    @Autowired
    private ManagedDeviceMapper managedDeviceMapper;
    
    /**
     * 验证设备创建数据
     * 
     * @param deviceDTO 设备数据
     * @throws ManagedDeviceException 验证失败时抛出异常
     */
    public void validateDeviceCreation(ManagedDeviceDTO deviceDTO) {
        logger.debug("开始验证设备创建数据: {}", deviceDTO.getSerialNumber());
        
        // 基础字段验证
        validateBasicDeviceData(deviceDTO);
        
        // 序列号唯一性验证
        validateSerialNumberUniqueness(deviceDTO.getSerialNumber(), null);
        
        // 客户信息验证
        validateCustomerData(deviceDTO);
        
        // 设备型号和状态验证
        validateDeviceModelAndStatus(deviceDTO);
        
        // 技术参数验证
        validateDeviceSpecifications(deviceDTO);
        
        // 配置信息验证
        validateDeviceConfiguration(deviceDTO);
        
        // 记录验证成功日志
        logValidationEvent("DEVICE_CREATION_VALIDATION", "SUCCESS", deviceDTO.getSerialNumber());
        
        logger.debug("设备创建数据验证通过: {}", deviceDTO.getSerialNumber());
    }
    
    /**
     * 验证设备更新数据
     * 
     * @param deviceId 设备ID
     * @param deviceDTO 设备数据
     * @throws ManagedDeviceException 验证失败时抛出异常
     */
    public void validateDeviceUpdate(Long deviceId, ManagedDeviceDTO deviceDTO) {
        logger.debug("开始验证设备更新数据: ID={}, SerialNumber={}", deviceId, deviceDTO.getSerialNumber());
        
        // 设备ID验证
        DeviceValidationUtils.validateId(deviceId, "deviceId");
        
        // 基础字段验证
        validateBasicDeviceData(deviceDTO);
        
        // 序列号唯一性验证（排除当前设备）
        validateSerialNumberUniqueness(deviceDTO.getSerialNumber(), deviceId);
        
        // 客户信息验证
        validateCustomerData(deviceDTO);
        
        // 设备型号和状态验证
        validateDeviceModelAndStatus(deviceDTO);
        
        // 状态转换验证
        validateStatusTransition(deviceId, deviceDTO.getStatus());
        
        // 技术参数验证
        validateDeviceSpecifications(deviceDTO);
        
        // 配置信息验证
        validateDeviceConfiguration(deviceDTO);
        
        // 记录验证成功日志
        logValidationEvent("DEVICE_UPDATE_VALIDATION", "SUCCESS", deviceDTO.getSerialNumber());
        
        logger.debug("设备更新数据验证通过: ID={}, SerialNumber={}", deviceId, deviceDTO.getSerialNumber());
    }
    
    /**
     * 验证设备状态变更
     * 
     * @param deviceId 设备ID
     * @param newStatus 新状态
     * @throws ManagedDeviceException 验证失败时抛出异常
     */
    public void validateStatusChange(Long deviceId, String newStatus) {
        logger.debug("开始验证设备状态变更: ID={}, NewStatus={}", deviceId, newStatus);
        
        // 设备ID验证
        DeviceValidationUtils.validateId(deviceId, "deviceId");
        
        // 新状态验证
        if (!StringUtils.hasText(newStatus)) {
            throw ManagedDeviceException.validationFailed("status", newStatus, "设备状态不能为空");
        }
        
        // 验证状态值有效性
        try {
            DeviceStatus.fromCode(newStatus);
        } catch (Exception e) {
            throw ManagedDeviceException.validationFailed("status", newStatus, "无效的设备状态值");
        }
        
        // 状态转换验证
        validateStatusTransition(deviceId, newStatus);
        
        // 记录验证成功日志
        logValidationEvent("STATUS_CHANGE_VALIDATION", "SUCCESS", "DeviceId=" + deviceId + ", NewStatus=" + newStatus);
        
        logger.debug("设备状态变更验证通过: ID={}, NewStatus={}", deviceId, newStatus);
    }
    
    /**
     * 验证批量操作数据
     * 
     * @param deviceIds 设备ID列表
     * @param operation 操作类型
     * @throws ManagedDeviceException 验证失败时抛出异常
     */
    public void validateBatchOperation(java.util.List<Long> deviceIds, String operation) {
        logger.debug("开始验证批量操作: Operation={}, DeviceCount={}", operation, deviceIds != null ? deviceIds.size() : 0);
        
        // 设备ID列表验证
        if (deviceIds == null || deviceIds.isEmpty()) {
            throw ManagedDeviceException.validationFailed("deviceIds", deviceIds, "设备ID列表不能为空");
        }
        
        if (deviceIds.size() > 100) {
            throw ManagedDeviceException.validationFailed("deviceIds", deviceIds.size(), "批量操作设备数量不能超过100个");
        }
        
        // 操作类型验证
        if (!StringUtils.hasText(operation)) {
            throw ManagedDeviceException.validationFailed("operation", operation, "操作类型不能为空");
        }
        
        // 验证每个设备ID
        for (Long deviceId : deviceIds) {
            DeviceValidationUtils.validateId(deviceId, "deviceId");
        }
        
        // 验证操作类型有效性
        validateOperationType(operation);
        
        // 记录验证成功日志
        logValidationEvent("BATCH_OPERATION_VALIDATION", "SUCCESS", "Operation=" + operation + ", DeviceCount=" + deviceIds.size());
        
        logger.debug("批量操作验证通过: Operation={}, DeviceCount={}", operation, deviceIds.size());
    }
    
    /**
     * 验证分页参数
     * 
     * @param page 页码
     * @param pageSize 页大小
     * @throws ManagedDeviceException 验证失败时抛出异常
     */
    public void validatePaginationParams(Integer page, Integer pageSize) {
        DeviceValidationUtils.validatePagination(page, pageSize);
    }
    
    /**
     * 验证搜索参数
     * 
     * @param keyword 搜索关键词
     * @param status 状态筛选
     * @param model 型号筛选
     * @throws ManagedDeviceException 验证失败时抛出异常
     */
    public void validateSearchParams(String keyword, String status, String model) {
        // 关键词验证
        if (StringUtils.hasText(keyword)) {
            DeviceValidationUtils.validateAgainstXSS(keyword, "搜索关键词");
            DeviceValidationUtils.validateAgainstSQLInjection(keyword, "搜索关键词");
            
            if (keyword.trim().length() > 100) {
                throw ManagedDeviceException.validationFailed("keyword", keyword, "搜索关键词长度不能超过100字符");
            }
        }
        
        // 状态筛选验证
        if (StringUtils.hasText(status)) {
            try {
                DeviceStatus.fromCode(status);
            } catch (Exception e) {
                throw ManagedDeviceException.validationFailed("status", status, "无效的设备状态筛选值");
            }
        }
        
        // 型号筛选验证
        if (StringUtils.hasText(model)) {
            try {
                DeviceModel.fromCode(model);
            } catch (Exception e) {
                throw ManagedDeviceException.validationFailed("model", model, "无效的设备型号筛选值");
            }
        }
    }
    
    /**
     * 验证基础设备数据
     */
    private void validateBasicDeviceData(ManagedDeviceDTO deviceDTO) {
        if (deviceDTO == null) {
            throw ManagedDeviceException.validationFailed("deviceData", null, "设备数据不能为空");
        }
        
        // 使用工具类进行基础验证
        DeviceValidationUtils.validateSerialNumber(deviceDTO.getSerialNumber());
        DeviceValidationUtils.validateFirmwareVersion(deviceDTO.getFirmwareVersion());
        DeviceValidationUtils.validateCustomerName(deviceDTO.getCustomerName());
        DeviceValidationUtils.validatePhone(deviceDTO.getCustomerPhone());
        DeviceValidationUtils.validateNotes(deviceDTO.getNotes());
    }
    
    /**
     * 验证序列号唯一性
     */
    private void validateSerialNumberUniqueness(String serialNumber, Long excludeDeviceId) {
        if (managedDeviceMapper.existsSerialNumber(serialNumber, excludeDeviceId)) {
            // 记录安全事件
            logValidationEvent("SERIAL_NUMBER_DUPLICATE", "FAILED", serialNumber);
            throw ManagedDeviceException.serialNumberExists(serialNumber);
        }
    }
    
    /**
     * 验证客户数据
     */
    private void validateCustomerData(ManagedDeviceDTO deviceDTO) {
        // 客户ID验证
        if (StringUtils.hasText(deviceDTO.getCustomerId())) {
            try {
                Long customerId = Long.valueOf(deviceDTO.getCustomerId());
                DeviceValidationUtils.validateId(customerId, "customerId");
            } catch (NumberFormatException e) {
                throw ManagedDeviceException.validationFailed("customerId", deviceDTO.getCustomerId(), "客户ID格式不正确");
            }
        }
        
        // 客户名称验证
        DeviceValidationUtils.validateCustomerName(deviceDTO.getCustomerName());
        
        // 客户电话验证
        DeviceValidationUtils.validatePhone(deviceDTO.getCustomerPhone());
    }
    
    /**
     * 验证设备型号和状态
     */
    private void validateDeviceModelAndStatus(ManagedDeviceDTO deviceDTO) {
        // 设备型号验证
        if (StringUtils.hasText(deviceDTO.getModel())) {
            try {
                DeviceModel.fromCode(deviceDTO.getModel());
            } catch (Exception e) {
                throw ManagedDeviceException.validationFailed("model", deviceDTO.getModel(), "无效的设备型号");
            }
        }
        
        // 设备状态验证
        if (StringUtils.hasText(deviceDTO.getStatus())) {
            try {
                DeviceStatus.fromCode(deviceDTO.getStatus());
            } catch (Exception e) {
                throw ManagedDeviceException.validationFailed("status", deviceDTO.getStatus(), "无效的设备状态");
            }
        }
    }
    
    /**
     * 验证状态转换
     */
    private void validateStatusTransition(Long deviceId, String newStatus) {
        // 获取当前设备状态
        ManagedDevice device = managedDeviceMapper.selectById(deviceId);
        if (device == null) {
            throw ManagedDeviceException.deviceNotFound(deviceId);
        }
        // 修复：正确获取设备状态的字符串代码
        String currentStatus = device.getStatus().getCode();
        
        // 验证状态转换的合理性
        if (!isValidStatusTransition(currentStatus, newStatus)) {
            // 记录安全事件
            logValidationEvent("INVALID_STATUS_TRANSITION", "FAILED", 
                "DeviceId=" + deviceId + ", From=" + currentStatus + ", To=" + newStatus);
            throw ManagedDeviceException.invalidStatusTransition(currentStatus, newStatus);
        }
    }
    
    /**
     * 验证技术参数
     */
    private void validateDeviceSpecifications(ManagedDeviceDTO deviceDTO) {
        if (deviceDTO.getSpecifications() != null) {
            ManagedDeviceDTO.DeviceSpecificationDTO spec = deviceDTO.getSpecifications();
            
            // CPU验证
            if (spec.getCpu() != null && spec.getCpu().toString().length() > 100) {
                throw ManagedDeviceException.validationFailed("cpu", spec.getCpu().toString(), "CPU信息长度不能超过100字符");
            }
            
            // 内存验证
            if (spec.getMemory() != null && spec.getMemory().toString().length() > 50) {
                throw ManagedDeviceException.validationFailed("memory", spec.getMemory().toString(), "内存信息长度不能超过50字符");
            }
            
            // 存储验证
            if (spec.getStorage() != null && spec.getStorage().toString().length() > 50) {
                throw ManagedDeviceException.validationFailed("storage", spec.getStorage().toString(), "存储信息长度不能超过50字符");
            }
            
            // 显示屏验证
            if (spec.getDisplay() != null && spec.getDisplay().toString().length() > 100) {
                throw ManagedDeviceException.validationFailed("display", spec.getDisplay().toString(), "显示屏信息长度不能超过100字符");
            }
            
            // 电池验证
            if (spec.getBattery() != null && spec.getBattery().toString().length() > 50) {
                throw ManagedDeviceException.validationFailed("battery", spec.getBattery().toString(), "电池信息长度不能超过50字符");
            }
            
            // 连接性验证
            if (spec.getConnectivity() != null && spec.getConnectivity().toString().length() > 200) {
                throw ManagedDeviceException.validationFailed("connectivity", spec.getConnectivity().toString(), "连接性信息长度不能超过200字符");
            }
        }
    }
    
    /**
     * 验证设备配置
     */
    private void validateDeviceConfiguration(ManagedDeviceDTO deviceDTO) {
        if (deviceDTO.getConfiguration() != null) {
            ManagedDeviceDTO.DeviceConfigurationDTO config = deviceDTO.getConfiguration();
            
            // 语言验证
            if (config.getLanguage() != null && config.getLanguage().toString().length() > 10) {
                throw ManagedDeviceException.validationFailed("language", config.getLanguage().toString(), "语言设置长度不能超过10字符");
            }
            
            // 时区验证
            if (config.getTimezone() != null && config.getTimezone().toString().length() > 50) {
                throw ManagedDeviceException.validationFailed("timezone", config.getTimezone().toString(), "时区设置长度不能超过50字符");
            }
            
            // 自定义设置验证
            if (config.getCustomSettings() != null) {
                DeviceValidationUtils.validateJsonFormat(config.getCustomSettings().toString(), "customSettings");
            }
        }
    }
    
    /**
     * 验证操作类型
     */
    private void validateOperationType(String operation) {
        String[] validOperations = {"reboot", "firmware_push", "delete", "activate", "maintenance"};
        boolean isValid = false;
        for (String validOp : validOperations) {
            if (validOp.equals(operation)) {
                isValid = true;
                break;
            }
        }
        
        if (!isValid) {
            throw ManagedDeviceException.validationFailed("operation", operation, "无效的操作类型");
        }
    }
    
    /**
     * 验证状态转换是否合理
     */
    private boolean isValidStatusTransition(String currentStatus, String newStatus) {
        if (currentStatus.equals(newStatus)) {
            return true; // 相同状态允许
        }
        
        // 修复：正确处理DeviceStatus枚举类型
        DeviceStatus current = DeviceStatus.fromCode(currentStatus);
        DeviceStatus target = DeviceStatus.fromCode(newStatus);
        
        // 定义状态转换规则
        switch (current) {
            case OFFLINE:
                return target == DeviceStatus.ONLINE || target == DeviceStatus.MAINTENANCE;
                
            case ONLINE:
                return target == DeviceStatus.OFFLINE || target == DeviceStatus.ERROR || target == DeviceStatus.MAINTENANCE;
                
            case ERROR:
                return target == DeviceStatus.OFFLINE || target == DeviceStatus.MAINTENANCE;
                
            case MAINTENANCE:
                return target == DeviceStatus.OFFLINE || target == DeviceStatus.ONLINE;
                
            default:
                return false;
        }
    }
    
    /**
     * 记录验证事件日志
     */
    private void logValidationEvent(String eventType, String result, String details) {
        Map<String, Object> validationLog = new HashMap<>();
        validationLog.put("eventType", eventType);
        validationLog.put("result", result);
        validationLog.put("details", DeviceValidationUtils.sanitizeInput(details));
        validationLog.put("timestamp", LocalDateTime.now());
        validationLog.put("source", "ManagedDeviceValidationService");
        
        securityLogger.info("Validation Event: {}", validationLog);
    }
}