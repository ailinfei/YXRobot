package com.yxrobot.controller;

import com.yxrobot.service.ManagedDeviceOperationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备操作控制器
 * 适配前端设备操作，提供设备状态变更、重启、激活、固件推送等功能
 * 支持单个设备操作和批量操作
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@RestController
@RequestMapping("/api/admin/devices")
@Validated
public class ManagedDeviceOperationController {
    
    private static final Logger logger = LoggerFactory.getLogger(ManagedDeviceOperationController.class);
    
    @Autowired
    private ManagedDeviceOperationService operationService;
    
    /**
     * 更新设备状态
     * 支持设备状态变更功能
     * 
     * @param id 设备ID
     * @param request 状态更新请求
     * @return 操作结果
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateDeviceStatus(
            @PathVariable @NotNull Long id,
            @Valid @RequestBody StatusUpdateRequest request) {
        
        logger.info("更新设备状态 - 设备ID: {}, 新状态: {}", id, request.getStatus());
        
        try {
            ManagedDeviceOperationService.OperationResult result = operationService.updateManagedDeviceStatus(
                id, request.getStatus(), request.getOperator()
            );
            
            Map<String, Object> response = new HashMap<>();
            
            if (result.isSuccess()) {
                response.put("code", 200);
                response.put("message", result.getMessage());
                response.put("data", result.getData());
                return ResponseEntity.ok(response);
            } else {
                response.put("code", 400);
                response.put("message", result.getMessage());
                return ResponseEntity.status(400).body(response);
            }
            
        } catch (Exception e) {
            logger.error("更新设备状态失败 - 设备ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "状态更新失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 重启设备
     * 支持设备重启功能
     * 
     * @param id 设备ID
     * @param request 重启请求
     * @return 操作结果
     */
    @PostMapping("/{id}/reboot")
    public ResponseEntity<Map<String, Object>> rebootDevice(
            @PathVariable @NotNull Long id,
            @RequestBody(required = false) OperationRequest request) {
        
        logger.info("重启设备 - 设备ID: {}", id);
        
        try {
            String operator = request != null ? request.getOperator() : "系统管理员";
            
            ManagedDeviceOperationService.OperationResult result = operationService.rebootManagedDevice(id, operator);
            
            Map<String, Object> response = new HashMap<>();
            
            if (result.isSuccess()) {
                response.put("code", 200);
                response.put("message", result.getMessage());
                response.put("data", result.getData());
                return ResponseEntity.ok(response);
            } else {
                response.put("code", 400);
                response.put("message", result.getMessage());
                return ResponseEntity.status(400).body(response);
            }
            
        } catch (Exception e) {
            logger.error("重启设备失败 - 设备ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "设备重启失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 激活设备
     * 支持设备激活功能
     * 
     * @param id 设备ID
     * @param request 激活请求
     * @return 操作结果
     */
    @PostMapping("/{id}/activate")
    public ResponseEntity<Map<String, Object>> activateDevice(
            @PathVariable @NotNull Long id,
            @RequestBody(required = false) OperationRequest request) {
        
        logger.info("激活设备 - 设备ID: {}", id);
        
        try {
            String operator = request != null ? request.getOperator() : "系统管理员";
            
            ManagedDeviceOperationService.OperationResult result = operationService.activateManagedDevice(id, operator);
            
            Map<String, Object> response = new HashMap<>();
            
            if (result.isSuccess()) {
                response.put("code", 200);
                response.put("message", result.getMessage());
                response.put("data", result.getData());
                return ResponseEntity.ok(response);
            } else {
                response.put("code", 400);
                response.put("message", result.getMessage());
                return ResponseEntity.status(400).body(response);
            }
            
        } catch (Exception e) {
            logger.error("激活设备失败 - 设备ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "设备激活失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 推送固件
     * 支持固件推送功能
     * 
     * @param id 设备ID
     * @param request 固件推送请求
     * @return 操作结果
     */
    @PostMapping("/{id}/firmware")
    public ResponseEntity<Map<String, Object>> pushFirmware(
            @PathVariable @NotNull Long id,
            @RequestBody(required = false) FirmwarePushRequest request) {
        
        logger.info("推送固件 - 设备ID: {}, 固件版本: {}", id, 
                   request != null ? request.getVersion() : "最新版本");
        
        try {
            String version = request != null ? request.getVersion() : null;
            String operator = request != null ? request.getOperator() : "系统管理员";
            
            ManagedDeviceOperationService.OperationResult result = operationService.pushFirmware(id, version, operator);
            
            Map<String, Object> response = new HashMap<>();
            
            if (result.isSuccess()) {
                response.put("code", 200);
                response.put("message", result.getMessage());
                response.put("data", result.getData());
                return ResponseEntity.ok(response);
            } else {
                response.put("code", 400);
                response.put("message", result.getMessage());
                return ResponseEntity.status(400).body(response);
            }
            
        } catch (Exception e) {
            logger.error("推送固件失败 - 设备ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "固件推送失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 批量推送固件
     * 支持批量固件推送功能
     * 
     * @param request 批量固件推送请求
     * @return 批量操作结果
     */
    @PostMapping("/batch/firmware")
    public ResponseEntity<Map<String, Object>> batchPushFirmware(@Valid @RequestBody BatchFirmwarePushRequest request) {
        logger.info("批量推送固件 - 设备数量: {}, 固件版本: {}", 
                   request.getDeviceIds().size(), request.getVersion());
        
        try {
            Map<String, Object> params = new HashMap<>();
            if (request.getVersion() != null) {
                params.put("version", request.getVersion());
            }
            
            ManagedDeviceOperationService.BatchOperationResult result = operationService.batchOperation(
                request.getDeviceIds(), "firmware", params, request.getOperator()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "批量固件推送已启动");
            
            Map<String, Object> data = new HashMap<>();
            data.put("totalCount", result.getTotalCount());
            data.put("successCount", result.getSuccessCount());
            data.put("failureCount", result.getFailureCount());
            data.put("successResults", result.getSuccessResults());
            data.put("failureResults", result.getFailureResults());
            
            response.put("data", data);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("批量推送固件失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "批量固件推送失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 批量重启设备
     * 支持批量重启功能
     * 
     * @param request 批量重启请求
     * @return 批量操作结果
     */
    @PostMapping("/batch/reboot")
    public ResponseEntity<Map<String, Object>> batchRebootDevices(@Valid @RequestBody BatchOperationRequest request) {
        logger.info("批量重启设备 - 设备数量: {}", request.getDeviceIds().size());
        
        try {
            ManagedDeviceOperationService.BatchOperationResult result = operationService.batchOperation(
                request.getDeviceIds(), "reboot", null, request.getOperator()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "批量重启指令已发送");
            
            Map<String, Object> data = new HashMap<>();
            data.put("totalCount", result.getTotalCount());
            data.put("successCount", result.getSuccessCount());
            data.put("failureCount", result.getFailureCount());
            data.put("successResults", result.getSuccessResults());
            data.put("failureResults", result.getFailureResults());
            
            response.put("data", data);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("批量重启设备失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "批量重启失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 批量更新设备状态
     * 支持批量状态更新功能
     * 
     * @param request 批量状态更新请求
     * @return 批量操作结果
     */
    @PostMapping("/batch/status")
    public ResponseEntity<Map<String, Object>> batchUpdateDeviceStatus(@Valid @RequestBody BatchStatusUpdateRequest request) {
        logger.info("批量更新设备状态 - 设备数量: {}, 新状态: {}", 
                   request.getDeviceIds().size(), request.getStatus());
        
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("status", request.getStatus());
            
            ManagedDeviceOperationService.BatchOperationResult result = operationService.batchOperation(
                request.getDeviceIds(), "status", params, request.getOperator()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "批量状态更新完成");
            
            Map<String, Object> data = new HashMap<>();
            data.put("totalCount", result.getTotalCount());
            data.put("successCount", result.getSuccessCount());
            data.put("failureCount", result.getFailureCount());
            data.put("successResults", result.getSuccessResults());
            data.put("failureResults", result.getFailureResults());
            
            response.put("data", data);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("批量更新设备状态失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "批量状态更新失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 状态更新请求类
     */
    public static class StatusUpdateRequest {
        @NotNull(message = "设备状态不能为空")
        private String status;
        
        private String operator = "系统管理员";
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getOperator() { return operator; }
        public void setOperator(String operator) { this.operator = operator; }
    }
    
    /**
     * 操作请求类
     */
    public static class OperationRequest {
        private String operator = "系统管理员";
        
        public String getOperator() { return operator; }
        public void setOperator(String operator) { this.operator = operator; }
    }
    
    /**
     * 固件推送请求类
     */
    public static class FirmwarePushRequest {
        private String version;
        private String operator = "系统管理员";
        
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        
        public String getOperator() { return operator; }
        public void setOperator(String operator) { this.operator = operator; }
    }
    
    /**
     * 批量操作请求类
     */
    public static class BatchOperationRequest {
        @NotNull(message = "设备ID列表不能为空")
        private List<Long> deviceIds;
        
        private String operator = "系统管理员";
        
        public List<Long> getDeviceIds() { return deviceIds; }
        public void setDeviceIds(List<Long> deviceIds) { this.deviceIds = deviceIds; }
        
        public String getOperator() { return operator; }
        public void setOperator(String operator) { this.operator = operator; }
    }
    
    /**
     * 批量固件推送请求类
     */
    public static class BatchFirmwarePushRequest {
        @NotNull(message = "设备ID列表不能为空")
        private List<Long> deviceIds;
        
        private String version;
        private String operator = "系统管理员";
        
        public List<Long> getDeviceIds() { return deviceIds; }
        public void setDeviceIds(List<Long> deviceIds) { this.deviceIds = deviceIds; }
        
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        
        public String getOperator() { return operator; }
        public void setOperator(String operator) { this.operator = operator; }
    }
    
    /**
     * 批量状态更新请求类
     */
    public static class BatchStatusUpdateRequest {
        @NotNull(message = "设备ID列表不能为空")
        private List<Long> deviceIds;
        
        @NotNull(message = "设备状态不能为空")
        private String status;
        
        private String operator = "系统管理员";
        
        public List<Long> getDeviceIds() { return deviceIds; }
        public void setDeviceIds(List<Long> deviceIds) { this.deviceIds = deviceIds; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getOperator() { return operator; }
        public void setOperator(String operator) { this.operator = operator; }
    }
}