package com.yxrobot.controller;

import com.yxrobot.dto.DeviceControlDTO;
import com.yxrobot.dto.PageResult;
import com.yxrobot.service.DeviceControlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 设备控制控制器
 * 提供设备远程控制的REST API接口
 * 
 * @author YXRobot Development Team
 * @since 2024
 */
@RestController
@RequestMapping("/api/device-control")
@CrossOrigin(origins = "*")
public class DeviceControlController {
    
    private static final Logger logger = LoggerFactory.getLogger(DeviceControlController.class);
    
    @Autowired
    private DeviceControlService deviceControlService;
    
    /**
     * 启动设备
     * 
     * @param deviceId 设备ID
     * @return 控制结果
     */
    @PostMapping("/{deviceId}/start")
    public ResponseEntity<DeviceControlDTO.ControlResult> startDevice(@PathVariable Long deviceId) {
        logger.info("接收到启动设备请求: deviceId={}", deviceId);
        
        try {
            DeviceControlDTO.ControlResult result = deviceControlService.startDevice(deviceId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("启动设备失败: deviceId={}", deviceId, e);
            return ResponseEntity.ok(DeviceControlDTO.ControlResult.failure("启动设备失败: " + e.getMessage()));
        }
    }
    
    /**
     * 停止设备
     * 
     * @param deviceId 设备ID
     * @return 控制结果
     */
    @PostMapping("/{deviceId}/stop")
    public ResponseEntity<DeviceControlDTO.ControlResult> stopDevice(@PathVariable Long deviceId) {
        logger.info("接收到停止设备请求: deviceId={}", deviceId);
        
        try {
            DeviceControlDTO.ControlResult result = deviceControlService.stopDevice(deviceId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("停止设备失败: deviceId={}", deviceId, e);
            return ResponseEntity.ok(DeviceControlDTO.ControlResult.failure("停止设备失败: " + e.getMessage()));
        }
    }
    
    /**
     * 重启设备
     * 
     * @param deviceId 设备ID
     * @return 控制结果
     */
    @PostMapping("/{deviceId}/restart")
    public ResponseEntity<DeviceControlDTO.ControlResult> restartDevice(@PathVariable Long deviceId) {
        logger.info("接收到重启设备请求: deviceId={}", deviceId);
        
        try {
            DeviceControlDTO.ControlResult result = deviceControlService.restartDevice(deviceId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("重启设备失败: deviceId={}", deviceId, e);
            return ResponseEntity.ok(DeviceControlDTO.ControlResult.failure("重启设备失败: " + e.getMessage()));
        }
    }
    
    /**
     * 更新设备配置
     * 
     * @param deviceId 设备ID
     * @param configRequest 配置更新请求
     * @return 控制结果
     */
    @PostMapping("/{deviceId}/config")
    public ResponseEntity<DeviceControlDTO.ControlResult> updateConfig(
            @PathVariable Long deviceId,
            @Valid @RequestBody DeviceControlDTO.ConfigUpdateRequest configRequest) {
        logger.info("接收到更新设备配置请求: deviceId={}, configData={}", deviceId, configRequest.getConfigData());
        
        try {
            DeviceControlDTO.ControlResult result = deviceControlService.updateDeviceConfig(deviceId, configRequest.getConfigData());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("更新设备配置失败: deviceId={}", deviceId, e);
            return ResponseEntity.ok(DeviceControlDTO.ControlResult.failure("更新设备配置失败: " + e.getMessage()));
        }
    }
    
    /**
     * 批量控制设备
     * 
     * @param operation 操作类型
     * @param deviceIds 设备ID列表
     * @return 批量控制结果
     */
    @PostMapping("/batch/{operation}")
    public ResponseEntity<DeviceControlDTO.BatchControlResult> batchControl(
            @PathVariable String operation,
            @Valid @RequestBody List<Long> deviceIds) {
        logger.info("接收到批量控制设备请求: operation={}, deviceCount={}", operation, deviceIds.size());
        
        try {
            DeviceControlDTO.BatchControlResult result = deviceControlService.batchControlDevices(deviceIds, operation);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("批量控制设备失败: operation={}, deviceCount={}", operation, deviceIds.size(), e);
            DeviceControlDTO.BatchControlResult errorResult = new DeviceControlDTO.BatchControlResult();
            errorResult.setTotalCount(deviceIds.size());
            errorResult.setFailureCount(deviceIds.size());
            for (Long deviceId : deviceIds) {
                errorResult.addFailureDetail(deviceId, "批量操作失败: " + e.getMessage());
            }
            return ResponseEntity.ok(errorResult);
        }
    }
    
    /**
     * 获取设备控制状态
     * 
     * @param deviceId 设备ID
     * @return 设备控制状态
     */
    @GetMapping("/{deviceId}/status")
    public ResponseEntity<Map<String, Object>> getControlStatus(@PathVariable Long deviceId) {
        logger.debug("获取设备控制状态: deviceId={}", deviceId);
        
        try {
            // TODO: 实现获取设备控制状态的逻辑
            Map<String, Object> status = Map.of(
                "deviceId", deviceId,
                "controllable", true,
                "currentOperation", "none",
                "lastControlTime", null
            );
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            logger.error("获取设备控制状态失败: deviceId={}", deviceId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 取消设备控制操作
     * 
     * @param deviceId 设备ID
     * @param operationId 操作ID
     * @return 取消结果
     */
    @PostMapping("/{deviceId}/cancel/{operationId}")
    public ResponseEntity<DeviceControlDTO.ControlResult> cancelOperation(
            @PathVariable Long deviceId,
            @PathVariable String operationId) {
        logger.info("接收到取消设备控制操作请求: deviceId={}, operationId={}", deviceId, operationId);
        
        try {
            // TODO: 实现取消控制操作的逻辑
            DeviceControlDTO.ControlResult result = DeviceControlDTO.ControlResult.success("操作已取消");
            result.addData("deviceId", deviceId);
            result.addData("operationId", operationId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("取消设备控制操作失败: deviceId={}, operationId={}", deviceId, operationId, e);
            return ResponseEntity.ok(DeviceControlDTO.ControlResult.failure("取消操作失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取支持的控制操作列表
     * 
     * @return 支持的操作列表
     */
    @GetMapping("/operations")
    public ResponseEntity<List<Map<String, Object>>> getSupportedOperations() {
        logger.debug("获取支持的控制操作列表");
        
        try {
            List<Map<String, Object>> operations = List.of(
                Map.of("code", "start", "name", "启动设备", "description", "启动设备运行", "riskLevel", "low"),
                Map.of("code", "stop", "name", "停止设备", "description", "停止设备运行", "riskLevel", "medium"),
                Map.of("code", "restart", "name", "重启设备", "description", "重启设备系统", "riskLevel", "high"),
                Map.of("code", "config_update", "name", "更新配置", "description", "更新设备配置参数", "riskLevel", "medium")
            );
            return ResponseEntity.ok(operations);
        } catch (Exception e) {
            logger.error("获取支持的控制操作列表失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}