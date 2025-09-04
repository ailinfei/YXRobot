package com.yxrobot.exception;

/**
 * 设备控制异常基类
 * 处理设备控制操作中的各种异常情况
 * 
 * @author YXRobot Development Team
 * @since 2024
 */
public class DeviceControlException extends RuntimeException {
    
    private String errorCode;
    private Object[] args;
    
    public DeviceControlException(String message) {
        super(message);
    }
    
    public DeviceControlException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public DeviceControlException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public DeviceControlException(String errorCode, String message, Object... args) {
        super(message);
        this.errorCode = errorCode;
        this.args = args;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public Object[] getArgs() {
        return args;
    }
    
    /**
     * 设备不可控制异常
     */
    public static class DeviceNotControllableException extends DeviceControlException {
        public DeviceNotControllableException(Long deviceId, String reason) {
            super("DEVICE_NOT_CONTROLLABLE", 
                  "设备不可控制: deviceId=" + deviceId + ", reason=" + reason, 
                  deviceId, reason);
        }
        
        public DeviceNotControllableException(String deviceSerialNumber, String reason) {
            super("DEVICE_NOT_CONTROLLABLE", 
                  "设备不可控制: serialNumber=" + deviceSerialNumber + ", reason=" + reason, 
                  deviceSerialNumber, reason);
        }
    }
    
    /**
     * 控制命令执行失败异常
     */
    public static class ControlCommandFailedException extends DeviceControlException {
        public ControlCommandFailedException(String operation, String reason) {
            super("CONTROL_COMMAND_FAILED", 
                  "控制命令执行失败: operation=" + operation + ", reason=" + reason, 
                  operation, reason);
        }
        
        public ControlCommandFailedException(String operation, String reason, Throwable cause) {
            super("CONTROL_COMMAND_FAILED", 
                  "控制命令执行失败: operation=" + operation + ", reason=" + reason, 
                  cause);
        }
    }
    
    /**
     * 权限不足异常
     */
    public static class InsufficientPermissionException extends DeviceControlException {
        public InsufficientPermissionException(String operation) {
            super("INSUFFICIENT_PERMISSION", 
                  "权限不足，无法执行操作: " + operation, 
                  operation);
        }
        
        public InsufficientPermissionException(String userId, String operation) {
            super("INSUFFICIENT_PERMISSION", 
                  "用户权限不足: userId=" + userId + ", operation=" + operation, 
                  userId, operation);
        }
    }
    
    /**
     * 设备控制超时异常
     */
    public static class ControlTimeoutException extends DeviceControlException {
        public ControlTimeoutException(String operation, int timeoutSeconds) {
            super("CONTROL_TIMEOUT", 
                  "控制操作超时: operation=" + operation + ", timeout=" + timeoutSeconds + "s", 
                  operation, timeoutSeconds);
        }
    }
    
    /**
     * 设备状态不匹配异常
     */
    public static class DeviceStateException extends DeviceControlException {
        public DeviceStateException(String currentState, String requiredState, String operation) {
            super("DEVICE_STATE_MISMATCH", 
                  "设备状态不匹配: current=" + currentState + ", required=" + requiredState + ", operation=" + operation, 
                  currentState, requiredState, operation);
        }
    }
    
    /**
     * 配置验证失败异常
     */
    public static class ConfigValidationException extends DeviceControlException {
        public ConfigValidationException(String configKey, String reason) {
            super("CONFIG_VALIDATION_FAILED", 
                  "配置验证失败: key=" + configKey + ", reason=" + reason, 
                  configKey, reason);
        }
    }
    
    /**
     * 并发控制异常
     */
    public static class ConcurrentControlException extends DeviceControlException {
        public ConcurrentControlException(Long deviceId, String currentOperator) {
            super("CONCURRENT_CONTROL_CONFLICT", 
                  "设备正在被其他用户控制: deviceId=" + deviceId + ", operator=" + currentOperator, 
                  deviceId, currentOperator);
        }
    }
    
    /**
     * 网络通信异常
     */
    public static class NetworkCommunicationException extends DeviceControlException {
        public NetworkCommunicationException(String operation, String reason) {
            super("NETWORK_COMMUNICATION_ERROR", 
                  "网络通信失败: operation=" + operation + ", reason=" + reason, 
                  operation, reason);
        }
        
        public NetworkCommunicationException(String operation, String reason, Throwable cause) {
            super("NETWORK_COMMUNICATION_ERROR", 
                  "网络通信失败: operation=" + operation + ", reason=" + reason, 
                  cause);
        }
    }
}