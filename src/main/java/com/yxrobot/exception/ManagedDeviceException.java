package com.yxrobot.exception;

/**
 * 设备管理业务异常类
 * 专门用于处理设备管理模块（ManagedDevice）的各种业务异常情况
 * 与现有的DeviceException区分，避免命名冲突
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
public class ManagedDeviceException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 异常错误代码
     */
    private String errorCode;
    
    /**
     * 异常详细信息
     */
    private Object details;
    
    /**
     * 构造函数
     * 
     * @param message 异常消息
     */
    public ManagedDeviceException(String message) {
        super(message);
    }
    
    /**
     * 构造函数
     * 
     * @param message 异常消息
     * @param cause 原因异常
     */
    public ManagedDeviceException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * 构造函数
     * 
     * @param errorCode 错误代码
     * @param message 异常消息
     */
    public ManagedDeviceException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    /**
     * 构造函数
     * 
     * @param errorCode 错误代码
     * @param message 异常消息
     * @param cause 原因异常
     */
    public ManagedDeviceException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    /**
     * 构造函数
     * 
     * @param errorCode 错误代码
     * @param message 异常消息
     * @param details 异常详细信息
     */
    public ManagedDeviceException(String errorCode, String message, Object details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details;
    }
    
    /**
     * 获取错误代码
     * 
     * @return 错误代码
     */
    public String getErrorCode() {
        return errorCode;
    }
    
    /**
     * 设置错误代码
     * 
     * @param errorCode 错误代码
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    /**
     * 获取异常详细信息
     * 
     * @return 异常详细信息
     */
    public Object getDetails() {
        return details;
    }
    
    /**
     * 设置异常详细信息
     * 
     * @param details 异常详细信息
     */
    public void setDetails(Object details) {
        this.details = details;
    }
    
    // ==================== 常用异常工厂方法 ====================
    
    /**
     * 设备不存在异常
     * 
     * @param deviceId 设备ID
     * @return ManagedDeviceException
     */
    public static ManagedDeviceException deviceNotFound(Long deviceId) {
        return new ManagedDeviceException("MANAGED_DEVICE_NOT_FOUND", 
            "设备不存在，设备ID: " + deviceId, deviceId);
    }
    
    /**
     * 设备序列号已存在异常
     * 
     * @param serialNumber 设备序列号
     * @return ManagedDeviceException
     */
    public static ManagedDeviceException serialNumberExists(String serialNumber) {
        return new ManagedDeviceException("SERIAL_NUMBER_EXISTS", 
            "设备序列号已存在: " + serialNumber, serialNumber);
    }
    
    /**
     * 设备状态无效异常
     * 
     * @param currentStatus 当前状态
     * @param targetStatus 目标状态
     * @return ManagedDeviceException
     */
    public static ManagedDeviceException invalidStatusTransition(String currentStatus, String targetStatus) {
        return new ManagedDeviceException("INVALID_STATUS_TRANSITION", 
            String.format("无效的状态转换: %s -> %s", currentStatus, targetStatus),
            new String[]{currentStatus, targetStatus});
    }
    
    /**
     * 客户不存在异常
     * 
     * @param customerId 客户ID
     * @return ManagedDeviceException
     */
    public static ManagedDeviceException customerNotFound(Long customerId) {
        return new ManagedDeviceException("CUSTOMER_NOT_FOUND", 
            "客户不存在，客户ID: " + customerId, customerId);
    }
    
    /**
     * 设备操作失败异常
     * 
     * @param operation 操作类型
     * @param deviceId 设备ID
     * @param reason 失败原因
     * @return ManagedDeviceException
     */
    public static ManagedDeviceException operationFailed(String operation, Long deviceId, String reason) {
        return new ManagedDeviceException("DEVICE_OPERATION_FAILED", 
            String.format("设备操作失败: %s，设备ID: %d，原因: %s", operation, deviceId, reason),
            new Object[]{operation, deviceId, reason});
    }
    
    /**
     * 数据验证失败异常
     * 
     * @param field 字段名
     * @param value 字段值
     * @param message 验证消息
     * @return ManagedDeviceException
     */
    public static ManagedDeviceException validationFailed(String field, Object value, String message) {
        return new ManagedDeviceException("VALIDATION_FAILED", 
            String.format("数据验证失败: %s，值: %s，原因: %s", field, value, message),
            new Object[]{field, value, message});
    }
    
    /**
     * 权限不足异常
     * 
     * @param operation 操作类型
     * @param userId 用户ID
     * @return ManagedDeviceException
     */
    public static ManagedDeviceException permissionDenied(String operation, String userId) {
        return new ManagedDeviceException("PERMISSION_DENIED", 
            String.format("权限不足: 用户 %s 无权执行操作 %s", userId, operation),
            new Object[]{operation, userId});
    }
    
    /**
     * 业务规则违反异常
     * 
     * @param rule 业务规则
     * @param message 详细消息
     * @return ManagedDeviceException
     */
    public static ManagedDeviceException businessRuleViolation(String rule, String message) {
        return new ManagedDeviceException("BUSINESS_RULE_VIOLATION", 
            String.format("业务规则违反: %s，详情: %s", rule, message),
            new Object[]{rule, message});
    }
    
    /**
     * 系统错误异常
     * 
     * @param message 错误消息
     * @param cause 原因异常
     * @return ManagedDeviceException
     */
    public static ManagedDeviceException systemError(String message, Throwable cause) {
        return new ManagedDeviceException("SYSTEM_ERROR", 
            "系统错误: " + message, cause);
    }
    
    /**
     * 固件版本无效异常
     * 
     * @param version 固件版本
     * @return ManagedDeviceException
     */
    public static ManagedDeviceException invalidFirmwareVersion(String version) {
        return new ManagedDeviceException("INVALID_FIRMWARE_VERSION", 
            "无效的固件版本: " + version, version);
    }
    
    /**
     * 设备型号不支持异常
     * 
     * @param model 设备型号
     * @return ManagedDeviceException
     */
    public static ManagedDeviceException unsupportedDeviceModel(String model) {
        return new ManagedDeviceException("UNSUPPORTED_DEVICE_MODEL", 
            "不支持的设备型号: " + model, model);
    }
    
    /**
     * 批量操作部分失败异常
     * 
     * @param operation 操作类型
     * @param totalCount 总数量
     * @param successCount 成功数量
     * @param failedIds 失败的ID列表
     * @return ManagedDeviceException
     */
    public static ManagedDeviceException batchOperationPartialFailure(String operation, 
            int totalCount, int successCount, Object failedIds) {
        return new ManagedDeviceException("BATCH_OPERATION_PARTIAL_FAILURE", 
            String.format("批量操作部分失败: %s，总数: %d，成功: %d，失败: %d", 
                operation, totalCount, successCount, totalCount - successCount),
            new Object[]{operation, totalCount, successCount, failedIds});
    }
}