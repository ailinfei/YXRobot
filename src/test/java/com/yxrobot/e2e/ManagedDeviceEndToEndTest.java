package com.yxrobot.e2e;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yxrobot.dto.ManagedDeviceDTO;
import com.yxrobot.entity.DeviceModel;
import com.yxrobot.entity.DeviceStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 设备管理端到端测试
 * 模拟完整的用户操作流程，验证整个系统的功能完整性
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
@DisplayName("设备管理端到端测试")
class ManagedDeviceEndToEndTest {
    
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private MockMvc mockMvc;
    
    private String testDeviceId;
    private ManagedDeviceDTO testDevice;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // 创建测试设备数据
        testDevice = new ManagedDeviceDTO();
        testDevice.setSerialNumber("YX2025E2E001");
        testDevice.setModel(DeviceModel.EDUCATION.getCode());
        testDevice.setStatus(DeviceStatus.OFFLINE.getCode());
        testDevice.setFirmwareVersion("1.0.0");
        testDevice.setCustomerId("1");
        testDevice.setCustomerName("端到端测试客户");
        testDevice.setCustomerPhone("13812345678");
        testDevice.setNotes("端到端测试设备");
    }
    
    @Test
    @Order(1)
    @DisplayName("E2E测试 - 完整的设备管理流程")
    void testCompleteDeviceManagementFlow() throws Exception {
        // 1. 查看初始统计数据
        MvcResult statsResult = mockMvc.perform(get("/api/admin/devices/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpected(jsonPath("$.code").value(200))
                .andReturn();
        
        String statsContent = statsResult.getResponse().getContentAsString();
        JsonNode initialStats = objectMapper.readTree(statsContent).get("data");
        int initialTotalDevices = initialStats.get("totalDevices").asInt();
        
        // 2. 创建新设备
        String deviceJson = objectMapper.writeValueAsString(testDevice);
        
        MvcResult createResult = mockMvc.perform(post("/api/admin/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(deviceJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.serialNumber").value("YX2025E2E001"))
                .andReturn();
        
        String createContent = createResult.getResponse().getContentAsString();
        JsonNode createData = objectMapper.readTree(createContent).get("data");
        testDeviceId = createData.get("id").asText();
        
        // 3. 验证统计数据更新
        mockMvc.perform(get("/api/admin/devices/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalDevices").value(initialTotalDevices + 1));
        
        // 4. 查询设备详情
        mockMvc.perform(get("/api/admin/devices/{id}", testDeviceId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.serialNumber").value("YX2025E2E001"))
                .andExpect(jsonPath("$.data.status").value("offline"));
        
        // 5. 更新设备信息
        testDevice.setId(testDeviceId);
        testDevice.setCustomerName("更新后的客户名称");
        testDevice.setNotes("更新后的备注");
        
        String updateJson = objectMapper.writeValueAsString(testDevice);
        
        mockMvc.perform(put("/api/admin/devices/{id}", testDeviceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.customerName").value("更新后的客户名称"));
        
        // 6. 激活设备
        mockMvc.perform(post("/api/admin/devices/{id}/activate", testDeviceId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("激活成功"));
        
        // 7. 更新设备状态为在线
        Map<String, String> statusUpdate = new HashMap<>();
        statusUpdate.put("status", "online");
        String statusJson = objectMapper.writeValueAsString(statusUpdate);
        
        mockMvc.perform(patch("/api/admin/devices/{id}/status", testDeviceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(statusJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("状态更新成功"));
        
        // 8. 验证设备状态更新
        mockMvc.perform(get("/api/admin/devices/{id}", testDeviceId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("online"));
        
        // 9. 推送固件更新
        Map<String, String> firmwareUpdate = new HashMap<>();
        firmwareUpdate.put("firmwareVersion", "1.1.0");
        firmwareUpdate.put("firmwareUrl", "http://example.com/firmware/1.1.0.bin");
        String firmwareJson = objectMapper.writeValueAsString(firmwareUpdate);
        
        mockMvc.perform(post("/api/admin/devices/{id}/firmware", testDeviceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(firmwareJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("固件推送成功"));
        
        // 10. 重启设备
        mockMvc.perform(post("/api/admin/devices/{id}/reboot", testDeviceId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("重启命令发送成功"));
        
        // 11. 查询设备日志
        mockMvc.perform(get("/api/admin/devices/{id}/logs", testDeviceId)
                .param("page", "1")
                .param("pageSize", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list").isArray());
        
        // 12. 搜索设备
        mockMvc.perform(get("/api/admin/devices")
                .param("keyword", "YX2025E2E001")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list[0].serialNumber").value("YX2025E2E001"));
        
        // 13. 删除设备
        mockMvc.perform(delete("/api/admin/devices/{id}", testDeviceId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("删除成功"));
        
        // 14. 验证设备已删除
        mockMvc.perform(get("/api/admin/devices/{id}", testDeviceId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        
        // 15. 验证统计数据恢复
        mockMvc.perform(get("/api/admin/devices/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalDevices").value(initialTotalDevices));
    }
    
    @Test
    @Order(2)
    @DisplayName("E2E测试 - 批量操作流程")
    void testBatchOperationFlow() throws Exception {
        // 1. 创建多个测试设备
        String[] deviceIds = new String[3];
        
        for (int i = 0; i < 3; i++) {
            ManagedDeviceDTO batchDevice = new ManagedDeviceDTO();
            batchDevice.setSerialNumber("YX2025BATCH00" + (i + 1));
            batchDevice.setModel(DeviceModel.EDUCATION.getCode());
            batchDevice.setStatus(DeviceStatus.OFFLINE.getCode());
            batchDevice.setFirmwareVersion("1.0.0");
            batchDevice.setCustomerId("1");
            batchDevice.setCustomerName("批量测试客户" + (i + 1));
            batchDevice.setCustomerPhone("1381234567" + i);
            
            String deviceJson = objectMapper.writeValueAsString(batchDevice);
            
            MvcResult result = mockMvc.perform(post("/api/admin/devices")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(deviceJson))
                    .andExpect(status().isOk())
                    .andReturn();
            
            String content = result.getResponse().getContentAsString();
            JsonNode data = objectMapper.readTree(content).get("data");
            deviceIds[i] = data.get("id").asText();
        }
        
        // 2. 批量重启设备
        Map<String, Object> batchRebootRequest = new HashMap<>();
        batchRebootRequest.put("deviceIds", Arrays.asList(
            Long.parseLong(deviceIds[0]), 
            Long.parseLong(deviceIds[1]), 
            Long.parseLong(deviceIds[2])
        ));
        
        String rebootJson = objectMapper.writeValueAsString(batchRebootRequest);
        
        mockMvc.perform(post("/api/admin/devices/batch/reboot")
                .contentType(MediaType.APPLICATION_JSON)
                .content(rebootJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.successCount").isNumber())
                .andExpect(jsonPath("$.data.failureCount").isNumber());
        
        // 3. 批量固件推送
        Map<String, Object> batchFirmwareRequest = new HashMap<>();
        batchFirmwareRequest.put("deviceIds", Arrays.asList(
            Long.parseLong(deviceIds[0]), 
            Long.parseLong(deviceIds[1])
        ));
        batchFirmwareRequest.put("firmwareVersion", "1.1.0");
        batchFirmwareRequest.put("firmwareUrl", "http://example.com/firmware/1.1.0.bin");
        
        String firmwareJson = objectMapper.writeValueAsString(batchFirmwareRequest);
        
        mockMvc.perform(post("/api/admin/devices/batch/firmware")
                .contentType(MediaType.APPLICATION_JSON)
                .content(firmwareJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.successCount").isNumber());
        
        // 4. 批量删除设备
        Map<String, Object> batchDeleteRequest = new HashMap<>();
        batchDeleteRequest.put("deviceIds", Arrays.asList(
            Long.parseLong(deviceIds[0]), 
            Long.parseLong(deviceIds[1]), 
            Long.parseLong(deviceIds[2])
        ));
        
        String deleteJson = objectMapper.writeValueAsString(batchDeleteRequest);
        
        mockMvc.perform(delete("/api/admin/devices/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(deleteJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.successCount").value(3))
                .andExpect(jsonPath("$.data.failureCount").value(0));
        
        // 5. 验证设备已删除
        for (String deviceId : deviceIds) {
            mockMvc.perform(get("/api/admin/devices/{id}", deviceId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }
    
    @Test
    @Order(3)
    @DisplayName("E2E测试 - 搜索和筛选流程")
    void testSearchAndFilterFlow() throws Exception {
        // 1. 创建不同类型的测试设备
        createTestDeviceForSearch("YX2025SEARCH001", "EDUCATION", "online", "测试客户A");
        createTestDeviceForSearch("YX2025SEARCH002", "HOME", "offline", "测试客户B");
        createTestDeviceForSearch("YX2025SEARCH003", "PROFESSIONAL", "error", "测试客户C");
        
        // 2. 测试关键词搜索
        mockMvc.perform(get("/api/admin/devices")
                .param("keyword", "SEARCH")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list").isArray())
                .andExpect(jsonPath("$.data.total").value(greaterThanOrEqualTo(3)));
        
        // 3. 测试状态筛选
        mockMvc.perform(get("/api/admin/devices")
                .param("status", "online")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list").isArray());
        
        // 4. 测试型号筛选
        mockMvc.perform(get("/api/admin/devices")
                .param("model", "EDUCATION")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list").isArray());
        
        // 5. 测试组合筛选
        mockMvc.perform(get("/api/admin/devices")
                .param("keyword", "SEARCH")
                .param("status", "online")
                .param("model", "EDUCATION")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list").isArray());
        
        // 6. 测试快速搜索
        mockMvc.perform(get("/api/admin/devices/search/quick")
                .param("keyword", "YX2025SEARCH")
                .param("page", "1")
                .param("pageSize", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list").isArray());
        
        // 7. 测试搜索建议
        mockMvc.perform(get("/api/admin/devices/search/suggestions")
                .param("field", "serialNumber")
                .param("query", "YX2025")
                .param("limit", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
        
        // 8. 测试按状态搜索
        mockMvc.perform(get("/api/admin/devices/search/by-status")
                .param("statusList", "online,offline")
                .param("page", "1")
                .param("pageSize", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list").isArray());
    }
    
    @Test
    @Order(4)
    @DisplayName("E2E测试 - 错误处理流程")
    void testErrorHandlingFlow() throws Exception {
        // 1. 测试创建重复序列号设备
        String deviceJson = objectMapper.writeValueAsString(testDevice);
        
        // 先创建一个设备
        mockMvc.perform(post("/api/admin/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(deviceJson))
                .andExpect(status().isOk());
        
        // 再次创建相同序列号的设备，应该失败
        mockMvc.perform(post("/api/admin/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(deviceJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(409));
        
        // 2. 测试查询不存在的设备
        mockMvc.perform(get("/api/admin/devices/999999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
        
        // 3. 测试无效的设备ID格式
        mockMvc.perform(get("/api/admin/devices/invalid-id")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
        
        // 4. 测试无效的数据验证
        ManagedDeviceDTO invalidDevice = new ManagedDeviceDTO();
        invalidDevice.setSerialNumber(""); // 空序列号
        invalidDevice.setModel("INVALID"); // 无效型号
        
        String invalidJson = objectMapper.writeValueAsString(invalidDevice);
        
        mockMvc.perform(post("/api/admin/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
        
        // 5. 测试无效的状态转换
        Map<String, String> invalidStatus = new HashMap<>();
        invalidStatus.put("status", "invalid_status");
        String statusJson = objectMapper.writeValueAsString(invalidStatus);
        
        mockMvc.perform(patch("/api/admin/devices/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(statusJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }
    
    /**
     * 创建用于搜索测试的设备
     */
    private void createTestDeviceForSearch(String serialNumber, String model, String status, String customerName) throws Exception {
        ManagedDeviceDTO device = new ManagedDeviceDTO();
        device.setSerialNumber(serialNumber);
        device.setModel(model);
        device.setStatus(status);
        device.setFirmwareVersion("1.0.0");
        device.setCustomerId("1");
        device.setCustomerName(customerName);
        device.setCustomerPhone("13812345678");
        
        String deviceJson = objectMapper.writeValueAsString(device);
        
        mockMvc.perform(post("/api/admin/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(deviceJson))
                .andExpect(status().isOk());
    }
}