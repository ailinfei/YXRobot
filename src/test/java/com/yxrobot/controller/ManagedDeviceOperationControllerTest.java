package com.yxrobot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yxrobot.service.ManagedDeviceOperationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 设备操作控制器测试类
 */
@ExtendWith(MockitoExtension.class)
@WebMvcTest(ManagedDeviceOperationController.class)
class ManagedDeviceOperationControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private ManagedDeviceOperationService operationService;
    
    @BeforeEach
    void setUp() {
        // 设置通用的mock行为
    }
    
    @Test
    void testUpdateDeviceStatus_Success() throws Exception {
        // 准备测试数据
        Long deviceId = 1L;
        ManagedDeviceOperationController.StatusUpdateRequest request = new ManagedDeviceOperationController.StatusUpdateRequest();
        request.setStatus("online");
        request.setOperator("测试管理员");
        
        Map<String, Object> resultData = new HashMap<>();
        resultData.put("deviceId", deviceId);
        resultData.put("status", "online");
        resultData.put("updatedAt", LocalDateTime.now());
        
        ManagedDeviceOperationService.OperationResult operationResult = 
            ManagedDeviceOperationService.OperationResult.success("设备状态更新成功", resultData);
        
        when(operationService.updateManagedDeviceStatus(eq(deviceId), eq("online"), eq("测试管理员")))
            .thenReturn(operationResult);
        
        // 执行测试
        mockMvc.perform(patch("/api/admin/devices/{id}/status", deviceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("设备状态更新成功"))
                .andExpect(jsonPath("$.data.deviceId").value(deviceId))
                .andExpect(jsonPath("$.data.status").value("online"));
        
        // 验证服务调用
        verify(operationService, times(1)).updateManagedDeviceStatus(deviceId, "online", "测试管理员");
    }
    
    @Test
    void testUpdateDeviceStatus_Failure() throws Exception {
        // 准备测试数据
        Long deviceId = 1L;
        ManagedDeviceOperationController.StatusUpdateRequest request = new ManagedDeviceOperationController.StatusUpdateRequest();
        request.setStatus("invalid_status");
        request.setOperator("测试管理员");
        
        ManagedDeviceOperationService.OperationResult operationResult = 
            ManagedDeviceOperationService.OperationResult.failure("无效的设备状态: invalid_status");
        
        when(operationService.updateManagedDeviceStatus(eq(deviceId), eq("invalid_status"), eq("测试管理员")))
            .thenReturn(operationResult);
        
        // 执行测试
        mockMvc.perform(patch("/api/admin/devices/{id}/status", deviceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("无效的设备状态: invalid_status"));
        
        // 验证服务调用
        verify(operationService, times(1)).updateManagedDeviceStatus(deviceId, "invalid_status", "测试管理员");
    }
    
    @Test
    void testRebootDevice_Success() throws Exception {
        // 准备测试数据
        Long deviceId = 1L;
        ManagedDeviceOperationController.OperationRequest request = new ManagedDeviceOperationController.OperationRequest();
        request.setOperator("测试管理员");
        
        Map<String, Object> resultData = new HashMap<>();
        resultData.put("deviceId", deviceId);
        resultData.put("operation", "reboot");
        resultData.put("timestamp", LocalDateTime.now());
        
        ManagedDeviceOperationService.OperationResult operationResult = 
            ManagedDeviceOperationService.OperationResult.success("设备重启指令已发送", resultData);
        
        when(operationService.rebootManagedDevice(eq(deviceId), eq("测试管理员")))
            .thenReturn(operationResult);
        
        // 执行测试
        mockMvc.perform(post("/api/admin/devices/{id}/reboot", deviceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("设备重启指令已发送"))
                .andExpect(jsonPath("$.data.deviceId").value(deviceId))
                .andExpect(jsonPath("$.data.operation").value("reboot"));
        
        // 验证服务调用
        verify(operationService, times(1)).rebootManagedDevice(deviceId, "测试管理员");
    }
    
    @Test
    void testRebootDevice_WithoutRequestBody() throws Exception {
        // 准备测试数据
        Long deviceId = 1L;
        
        Map<String, Object> resultData = new HashMap<>();
        resultData.put("deviceId", deviceId);
        resultData.put("operation", "reboot");
        
        ManagedDeviceOperationService.OperationResult operationResult = 
            ManagedDeviceOperationService.OperationResult.success("设备重启指令已发送", resultData);
        
        when(operationService.rebootManagedDevice(eq(deviceId), eq("系统管理员")))
            .thenReturn(operationResult);
        
        // 执行测试（不传请求体）
        mockMvc.perform(post("/api/admin/devices/{id}/reboot", deviceId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("设备重启指令已发送"));
        
        // 验证服务调用（使用默认操作人）
        verify(operationService, times(1)).rebootManagedDevice(deviceId, "系统管理员");
    }
    
    @Test
    void testActivateDevice_Success() throws Exception {
        // 准备测试数据
        Long deviceId = 1L;
        ManagedDeviceOperationController.OperationRequest request = new ManagedDeviceOperationController.OperationRequest();
        request.setOperator("测试管理员");
        
        Map<String, Object> resultData = new HashMap<>();
        resultData.put("deviceId", deviceId);
        resultData.put("status", "online");
        resultData.put("activatedAt", LocalDateTime.now());
        
        ManagedDeviceOperationService.OperationResult operationResult = 
            ManagedDeviceOperationService.OperationResult.success("设备激活成功", resultData);
        
        when(operationService.activateManagedDevice(eq(deviceId), eq("测试管理员")))
            .thenReturn(operationResult);
        
        // 执行测试
        mockMvc.perform(post("/api/admin/devices/{id}/activate", deviceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("设备激活成功"))
                .andExpect(jsonPath("$.data.deviceId").value(deviceId))
                .andExpect(jsonPath("$.data.status").value("online"));
        
        // 验证服务调用
        verify(operationService, times(1)).activateManagedDevice(deviceId, "测试管理员");
    }
    
    @Test
    void testPushFirmware_Success() throws Exception {
        // 准备测试数据
        Long deviceId = 1L;
        ManagedDeviceOperationController.FirmwarePushRequest request = new ManagedDeviceOperationController.FirmwarePushRequest();
        request.setVersion("1.2.3");
        request.setOperator("测试管理员");
        
        Map<String, Object> resultData = new HashMap<>();
        resultData.put("deviceId", deviceId);
        resultData.put("firmwareVersion", "1.2.3");
        resultData.put("timestamp", LocalDateTime.now());
        
        ManagedDeviceOperationService.OperationResult operationResult = 
            ManagedDeviceOperationService.OperationResult.success("固件推送已启动", resultData);
        
        when(operationService.pushFirmware(eq(deviceId), eq("1.2.3"), eq("测试管理员")))
            .thenReturn(operationResult);
        
        // 执行测试
        mockMvc.perform(post("/api/admin/devices/{id}/firmware", deviceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("固件推送已启动"))
                .andExpect(jsonPath("$.data.deviceId").value(deviceId))
                .andExpect(jsonPath("$.data.firmwareVersion").value("1.2.3"));
        
        // 验证服务调用
        verify(operationService, times(1)).pushFirmware(deviceId, "1.2.3", "测试管理员");
    }
    
    @Test
    void testPushFirmware_WithoutVersion() throws Exception {
        // 准备测试数据
        Long deviceId = 1L;
        ManagedDeviceOperationController.FirmwarePushRequest request = new ManagedDeviceOperationController.FirmwarePushRequest();
        request.setOperator("测试管理员");
        
        Map<String, Object> resultData = new HashMap<>();
        resultData.put("deviceId", deviceId);
        resultData.put("firmwareVersion", "1.2.3");
        
        ManagedDeviceOperationService.OperationResult operationResult = 
            ManagedDeviceOperationService.OperationResult.success("固件推送已启动", resultData);
        
        when(operationService.pushFirmware(eq(deviceId), isNull(), eq("测试管理员")))
            .thenReturn(operationResult);
        
        // 执行测试
        mockMvc.perform(post("/api/admin/devices/{id}/firmware", deviceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("固件推送已启动"));
        
        // 验证服务调用（版本为null，使用最新版本）
        verify(operationService, times(1)).pushFirmware(deviceId, null, "测试管理员");
    }
    
    @Test
    void testBatchPushFirmware_Success() throws Exception {
        // 准备测试数据
        ManagedDeviceOperationController.BatchFirmwarePushRequest request = new ManagedDeviceOperationController.BatchFirmwarePushRequest();
        request.setDeviceIds(Arrays.asList(1L, 2L, 3L));
        request.setVersion("1.2.3");
        request.setOperator("测试管理员");
        
        ManagedDeviceOperationService.BatchOperationResult batchResult = new ManagedDeviceOperationService.BatchOperationResult();
        batchResult.setTotalCount(3);
        batchResult.addSuccess(1L, "固件推送成功");
        batchResult.addSuccess(2L, "固件推送成功");
        batchResult.addFailure(3L, "设备离线，无法推送固件");
        
        when(operationService.batchOperation(eq(Arrays.asList(1L, 2L, 3L)), eq("firmware"), any(Map.class), eq("测试管理员")))
            .thenReturn(batchResult);
        
        // 执行测试
        mockMvc.perform(post("/api/admin/devices/batch/firmware")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("批量固件推送已启动"))
                .andExpect(jsonPath("$.data.totalCount").value(3))
                .andExpect(jsonPath("$.data.successCount").value(2))
                .andExpect(jsonPath("$.data.failureCount").value(1));
        
        // 验证服务调用
        verify(operationService, times(1)).batchOperation(eq(Arrays.asList(1L, 2L, 3L)), eq("firmware"), any(Map.class), eq("测试管理员"));
    }
    
    @Test
    void testBatchRebootDevices_Success() throws Exception {
        // 准备测试数据
        ManagedDeviceOperationController.BatchOperationRequest request = new ManagedDeviceOperationController.BatchOperationRequest();
        request.setDeviceIds(Arrays.asList(1L, 2L));
        request.setOperator("测试管理员");
        
        ManagedDeviceOperationService.BatchOperationResult batchResult = new ManagedDeviceOperationService.BatchOperationResult();
        batchResult.setTotalCount(2);
        batchResult.addSuccess(1L, "设备重启成功");
        batchResult.addSuccess(2L, "设备重启成功");
        
        when(operationService.batchOperation(eq(Arrays.asList(1L, 2L)), eq("reboot"), isNull(), eq("测试管理员")))
            .thenReturn(batchResult);
        
        // 执行测试
        mockMvc.perform(post("/api/admin/devices/batch/reboot")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("批量重启指令已发送"))
                .andExpect(jsonPath("$.data.totalCount").value(2))
                .andExpect(jsonPath("$.data.successCount").value(2))
                .andExpect(jsonPath("$.data.failureCount").value(0));
        
        // 验证服务调用
        verify(operationService, times(1)).batchOperation(Arrays.asList(1L, 2L), "reboot", null, "测试管理员");
    }
    
    @Test
    void testBatchUpdateDeviceStatus_Success() throws Exception {
        // 准备测试数据
        ManagedDeviceOperationController.BatchStatusUpdateRequest request = new ManagedDeviceOperationController.BatchStatusUpdateRequest();
        request.setDeviceIds(Arrays.asList(1L, 2L));
        request.setStatus("maintenance");
        request.setOperator("测试管理员");
        
        ManagedDeviceOperationService.BatchOperationResult batchResult = new ManagedDeviceOperationService.BatchOperationResult();
        batchResult.setTotalCount(2);
        batchResult.addSuccess(1L, "状态更新成功");
        batchResult.addSuccess(2L, "状态更新成功");
        
        when(operationService.batchOperation(eq(Arrays.asList(1L, 2L)), eq("status"), any(Map.class), eq("测试管理员")))
            .thenReturn(batchResult);
        
        // 执行测试
        mockMvc.perform(post("/api/admin/devices/batch/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("批量状态更新完成"))
                .andExpect(jsonPath("$.data.totalCount").value(2))
                .andExpect(jsonPath("$.data.successCount").value(2));
        
        // 验证服务调用
        verify(operationService, times(1)).batchOperation(eq(Arrays.asList(1L, 2L)), eq("status"), any(Map.class), eq("测试管理员"));
    }
    
    @Test
    void testUpdateDeviceStatus_ValidationError() throws Exception {
        // 准备测试数据（缺少必填字段）
        ManagedDeviceOperationController.StatusUpdateRequest request = new ManagedDeviceOperationController.StatusUpdateRequest();
        // 不设置status字段
        
        // 执行测试
        mockMvc.perform(patch("/api/admin/devices/{id}/status", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        
        // 验证服务未被调用
        verify(operationService, never()).updateManagedDeviceStatus(anyLong(), anyString(), anyString());
    }
    
    @Test
    void testBatchPushFirmware_ValidationError() throws Exception {
        // 准备测试数据（缺少必填字段）
        ManagedDeviceOperationController.BatchFirmwarePushRequest request = new ManagedDeviceOperationController.BatchFirmwarePushRequest();
        // 不设置deviceIds字段
        request.setVersion("1.2.3");
        
        // 执行测试
        mockMvc.perform(post("/api/admin/devices/batch/firmware")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        
        // 验证服务未被调用
        verify(operationService, never()).batchOperation(anyList(), anyString(), any(), anyString());
    }
    
    @Test
    void testRebootDevice_ServiceException() throws Exception {
        // 准备测试数据
        Long deviceId = 1L;
        
        when(operationService.rebootManagedDevice(eq(deviceId), anyString()))
            .thenThrow(new RuntimeException("数据库连接失败"));
        
        // 执行测试
        mockMvc.perform(post("/api/admin/devices/{id}/reboot", deviceId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("设备重启失败: 数据库连接失败"));
        
        // 验证服务调用
        verify(operationService, times(1)).rebootManagedDevice(deviceId, "系统管理员");
    }
}