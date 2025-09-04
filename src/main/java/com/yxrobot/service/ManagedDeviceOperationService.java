package com.yxrobot.service;

import com.yxrobot.entity.DeviceStatus;
import com.yxrobot.entity.LogCategory;
import com.yxrobot.entity.LogLevel;
import com.yxrobot.entity.ManagedDevice;
import com.yxrobot.entity.ManagedDeviceLog;
import com.yxrobot.mapper.ManagedDeviceLogMapper;
import com.yxrobot.mapper.ManagedDeviceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备操作服务类
 * 处理设备操作业务逻辑，支持设备控制功能
 * 提供设备状态变更、重启、激活、固件推送等操作
 */
@Service
public class ManagedDeviceOperationService {
    
    @Autowired
    private ManagedDeviceMapper managedDeviceMapper;
    
    @Autowired
    private ManagedDeviceLogMapper deviceLogMapper;
    
    /**
     * 更新设备状态
     * 支持设备状态变更，包含状态流转验证
     * 
     * @param id 设备ID
     * @param newStatus 新状态
     * @param operator 操作人
     * @return 操作结果
     */
    @Transactional
    public OperationResult updateManagedDeviceStatus(Long id, String newStatus, String operator) {
        if (id == null) {
            return OperationResult.failure("设备ID不能为空");
        }
        
        if (newStatus == null || newStatus.trim().isEmpty()) {
            return OperationResult.failure("设备状态不能为空");
        }
        
        // 查询设备
        ManagedDevice device = managedDeviceMapper.selectById(id);
        if (device == null) {
            return OperationResult.failure("设备不存在，ID: " + id);
        }
        
        // 验证状态转换
        DeviceStatus currentStatus = device.getStatus();
        DeviceStatus targetStatus;
        
        try {
            targetStatus = DeviceStatus.fromCode(newStatus);
        } catch (IllegalArgumentException e) {
            return OperationResult.failure("无效的设备状态: " + newStatus);
        }
        
        if (!isValidStatusTransition(currentStatus, targetStatus)) {
            return OperationResult.failure(
                String.format("不允许从状态 %s 转换到 %s", 
                    currentStatus.getName(), targetStatus.getName())
            );
        }
        
        // 更新设备状态
        int result = managedDeviceMapper.updateStatus(id, newStatus);
        if (result <= 0) {
            return OperationResult.failure("更新设备状态失败");
        }
        
        // 记录操作日志
        recordOperationLog(id, "状态变更", 
            String.format("设备状态从 %s 变更为 %s", currentStatus.getName(), targetStatus.getName()),
            operator, LogLevel.INFO);
        
        return OperationResult.success("设备状态更新成功", 
            Map.of("deviceId", id, "status", newStatus, "updatedAt", LocalDateTime.now()));
    }
    
    /**
     * 重启设备
     * 支持设备重启操作
     * 
     * @param id 设备ID
     * @param operator 操作人
     * @return 操作结果
     */
    @Transactional
    public OperationResult rebootManagedDevice(Long id, String operator) {
        if (id == null) {
            return OperationResult.failure("设备ID不能为空");
        }
        
        // 查询设备
        ManagedDevice device = managedDeviceMapper.selectById(id);
        if (device == null) {
            return OperationResult.failure("设备不存在，ID: " + id);
        }
        
        // 检查设备状态
        if (device.getStatus() != DeviceStatus.ONLINE) {
            return OperationResult.failure("只有在线设备才能执行重启操作");
        }
        
        // 模拟重启操作（实际项目中这里会调用设备API）
        boolean rebootSuccess = simulateDeviceReboot(id);
        
        if (!rebootSuccess) {
            recordOperationLog(id, "设备重启", "设备重启失败", operator, LogLevel.ERROR);
            return OperationResult.failure("设备重启失败");
        }
        
        // 记录操作日志
        recordOperationLog(id, "设备重启", "设备重启成功", operator, LogLevel.INFO);
        
        return OperationResult.success("设备重启指令已发送", 
            Map.of("deviceId", id, "operation", "reboot", "timestamp", LocalDateTime.now()));
    }
    
    /**
     * 激活设备
     * 支持设备激活操作
     * 
     * @param id 设备ID
     * @param operator 操作人
     * @return 操作结果
     */
    @Transactional
    public OperationResult activateManagedDevice(Long id, String operator) {
        if (id == null) {
            return OperationResult.failure("设备ID不能为空");
        }
        
        // 查询设备
        ManagedDevice device = managedDeviceMapper.selectById(id);
        if (device == null) {
            return OperationResult.failure("设备不存在，ID: " + id);
        }
        
        // 检查设备状态
        if (device.getStatus() == DeviceStatus.ONLINE) {
            return OperationResult.failure("设备已经在线，无需激活");
        }
        
        // 模拟激活操作
        boolean activateSuccess = simulateDeviceActivation(id);
        
        if (!activateSuccess) {
            recordOperationLog(id, "设备激活", "设备激活失败", operator, LogLevel.ERROR);
            return OperationResult.failure("设备激活失败");
        }
        
        // 更新设备状态为在线
        managedDeviceMapper.updateStatus(id, DeviceStatus.ONLINE.getCode());
        
        // 更新激活时间
        ManagedDevice updateDevice = new ManagedDevice();
        updateDevice.setId(id);
        updateDevice.setActivatedAt(LocalDateTime.now());
        managedDeviceMapper.updateById(updateDevice);
        
        // 记录操作日志
        recordOperationLog(id, "设备激活", "设备激活成功", operator, LogLevel.INFO);
        
        return OperationResult.success("设备激活成功", 
            Map.of("deviceId", id, "status", DeviceStatus.ONLINE.getCode(), 
                   "activatedAt", LocalDateTime.now()));
    }
    
    /**
     * 推送固件
     * 支持固件推送操作
     * 
     * @param id 设备ID
     * @param firmwareVersion 固件版本（可选）
     * @param operator 操作人
     * @return 操作结果
     */
    @Transactional
    public OperationResult pushFirmware(Long id, String firmwareVersion, String operator) {
        if (id == null) {
            return OperationResult.failure("设备ID不能为空");
        }
        
        // 查询设备
        ManagedDevice device = managedDeviceMapper.selectById(id);
        if (device == null) {
            return OperationResult.failure("设备不存在，ID: " + id);
        }
        
        // 检查设备状态
        if (device.getStatus() != DeviceStatus.ONLINE) {
            return OperationResult.failure("只有在线设备才能推送固件");
        }
        
        // 使用默认固件版本
        if (firmwareVersion == null || firmwareVersion.trim().isEmpty()) {
            firmwareVersion = getLatestFirmwareVersion(device.getModel().getCode());
        }
        
        // 检查固件版本
        if (firmwareVersion.equals(device.getFirmwareVersion())) {
            return OperationResult.failure("设备已是最新固件版本: " + firmwareVersion);
        }
        
        // 模拟固件推送
        boolean pushSuccess = simulateFirmwarePush(id, firmwareVersion);
        
        if (!pushSuccess) {
            recordOperationLog(id, "固件推送", 
                String.format("固件推送失败，版本: %s", firmwareVersion), operator, LogLevel.ERROR);
            return OperationResult.failure("固件推送失败");
        }
        
        // 更新设备固件版本
        ManagedDevice updateDevice = new ManagedDevice();
        updateDevice.setId(id);
        updateDevice.setFirmwareVersion(firmwareVersion);
        managedDeviceMapper.updateById(updateDevice);
        
        // 记录操作日志
        recordOperationLog(id, "固件推送", 
            String.format("固件推送成功，版本: %s", firmwareVersion), operator, LogLevel.INFO);
        
        return OperationResult.success("固件推送已启动", 
            Map.of("deviceId", id, "firmwareVersion", firmwareVersion, 
                   "timestamp", LocalDateTime.now()));
    }
    
    /**
     * 批量操作
     * 支持批量操作（重启、固件推送、删除）
     * 
     * @param deviceIds 设备ID列表
     * @param operation 操作类型
     * @param params 操作参数
     * @param operator 操作人
     * @return 批量操作结果
     */
    @Transactional
    public BatchOperationResult batchOperation(List<Long> deviceIds, String operation, 
                                             Map<String, Object> params, String operator) {
        if (deviceIds == null || deviceIds.isEmpty()) {
            return BatchOperationResult.failure("设备ID列表不能为空");
        }
        
        BatchOperationResult result = new BatchOperationResult();
        result.setTotalCount(deviceIds.size());
        
        for (Long deviceId : deviceIds) {
            try {
                OperationResult operationResult = null;
                
                switch (operation.toLowerCase()) {
                    case "reboot":
                        operationResult = rebootManagedDevice(deviceId, operator);
                        break;
                    case "firmware":
                        String version = params != null ? (String) params.get("version") : null;
                        operationResult = pushFirmware(deviceId, version, operator);
                        break;
                    case "status":
                        String status = params != null ? (String) params.get("status") : null;
                        operationResult = updateManagedDeviceStatus(deviceId, status, operator);
                        break;
                    default:
                        operationResult = OperationResult.failure("不支持的操作类型: " + operation);
                }
                
                if (operationResult.isSuccess()) {
                    result.addSuccess(deviceId, operationResult.getMessage());
                } else {
                    result.addFailure(deviceId, operationResult.getMessage());
                }
                
            } catch (Exception e) {
                result.addFailure(deviceId, "操作异常: " + e.getMessage());
            }
        }
        
        return result;
    }
    
    /**
     * 验证状态转换是否合法
     */
    private boolean isValidStatusTransition(DeviceStatus currentStatus, DeviceStatus targetStatus) {
        if (currentStatus == null || targetStatus == null) {
            return false;
        }
        
        // 定义状态转换规则
        Map<DeviceStatus, List<DeviceStatus>> transitions = Map.of(
            DeviceStatus.OFFLINE, List.of(DeviceStatus.ONLINE, DeviceStatus.MAINTENANCE),
            DeviceStatus.ONLINE, List.of(DeviceStatus.OFFLINE, DeviceStatus.ERROR, DeviceStatus.MAINTENANCE),
            DeviceStatus.ERROR, List.of(DeviceStatus.OFFLINE, DeviceStatus.MAINTENANCE),
            DeviceStatus.MAINTENANCE, List.of(DeviceStatus.ONLINE, DeviceStatus.OFFLINE)
        );
        
        List<DeviceStatus> allowedTransitions = transitions.get(currentStatus);
        return allowedTransitions != null && allowedTransitions.contains(targetStatus);
    }
    
    /**
     * 记录操作日志
     */
    private void recordOperationLog(Long deviceId, String operation, String message, 
                                  String operator, LogLevel level) {
        try {
            ManagedDeviceLog log = new ManagedDeviceLog();
            log.setDeviceId(deviceId);
            log.setTimestamp(LocalDateTime.now());
            log.setLevel(level);
            log.setCategory(LogCategory.SYSTEM);
            log.setMessage(String.format("[%s] %s - %s", operation, message, 
                operator != null ? "操作人: " + operator : "系统操作"));
            
            Map<String, Object> details = new HashMap<>();
            details.put("operation", operation);
            details.put("operator", operator);
            details.put("timestamp", LocalDateTime.now().toString());
            log.setDetails(details);
            
            deviceLogMapper.insert(log);
        } catch (Exception e) {
            // 日志记录失败不应影响主要操作
            System.err.println("记录操作日志失败: " + e.getMessage());
        }
    }
    
    /**
     * 模拟设备重启（实际项目中会调用设备API）
     */
    private boolean simulateDeviceReboot(Long deviceId) {
        // 模拟网络调用延迟
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 模拟90%成功率
        return Math.random() > 0.1;
    }
    
    /**
     * 模拟设备激活
     */
    private boolean simulateDeviceActivation(Long deviceId) {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return Math.random() > 0.05;
    }
    
    /**
     * 模拟固件推送
     */
    private boolean simulateFirmwarePush(Long deviceId, String version) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return Math.random() > 0.15;
    }
    
    /**
     * 获取最新固件版本
     */
    private String getLatestFirmwareVersion(String model) {
        // 根据设备型号返回最新固件版本
        switch (model) {
            case "YX-EDU-2024":
                return "1.2.3";
            case "YX-HOME-2024":
                return "2.1.5";
            case "YX-PRO-2024":
                return "3.0.1";
            default:
                return "1.0.0";
        }
    }
    
    /**
     * 操作结果类
     */
    public static class OperationResult {
        private boolean success;
        private String message;
        private Map<String, Object> data;
        
        private OperationResult(boolean success, String message, Map<String, Object> data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }
        
        public static OperationResult success(String message) {
            return new OperationResult(true, message, null);
        }
        
        public static OperationResult success(String message, Map<String, Object> data) {
            return new OperationResult(true, message, data);
        }
        
        public static OperationResult failure(String message) {
            return new OperationResult(false, message, null);
        }
        
        // Getter方法
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public Map<String, Object> getData() { return data; }
    }
    
    /**
     * 批量操作结果类
     */
    public static class BatchOperationResult {
        private int totalCount;
        private int successCount;
        private int failureCount;
        private Map<Long, String> successResults = new HashMap<>();
        private Map<Long, String> failureResults = new HashMap<>();
        
        public static BatchOperationResult failure(String message) {
            BatchOperationResult result = new BatchOperationResult();
            result.failureResults.put(0L, message);
            return result;
        }
        
        public void addSuccess(Long deviceId, String message) {
            successResults.put(deviceId, message);
            successCount++;
        }
        
        public void addFailure(Long deviceId, String message) {
            failureResults.put(deviceId, message);
            failureCount++;
        }
        
        public boolean isAllSuccess() {
            return failureCount == 0 && successCount > 0;
        }
        
        public boolean hasFailures() {
            return failureCount > 0;
        }
        
        // Getter和Setter方法
        public int getTotalCount() { return totalCount; }
        public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
        
        public int getSuccessCount() { return successCount; }
        public int getFailureCount() { return failureCount; }
        
        public Map<Long, String> getSuccessResults() { return successResults; }
        public Map<Long, String> getFailureResults() { return failureResults; }
    }
}