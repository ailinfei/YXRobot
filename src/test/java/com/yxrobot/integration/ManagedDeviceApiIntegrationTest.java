package com.yxrobot.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yxrobot.dto.ManagedDeviceDTO;
import com.yxrobot.dto.ManagedDeviceSearchCriteria;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 设备管理API集成测试
 * 验证前后端API接口的完整性和正确性
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
@DisplayName("设备管理API集成测试")
class ManagedDeviceApiIntegrationTest {
    
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
        testDevice.setSerialNumber("YX2025TEST001");
        testDevice.setModel(DeviceModel.EDUCATION.getCode());
        testDevice.setStatus(DeviceStatus.OFFLINE.getCode());
        testDevice.setFirmwareVersion("1.0.0");
        testDevice.setCustomerId("1");
        testDevice.setCustomerName("测试客户");
        testDevice.setCustomerPhone("13812345678");
        testDevice.setNotes("API集成测试设备");
    }
    
    @Test
    @Order(1)
    @DisplayName("测试设备统计API - 获取统计数据")
    void testGetDeviceStats() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/admin/devices/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("获取统计数据成功"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.totalDevices").isNumber())
                .andExpect(jsonPath("$.data.onlineDevices").isNumber())
                .andExpect(jsonPath("$.data.offlineDevices").isNumber())
                .andExpect(jsonPath("$.data.errorDevices").isNumber())
                .andExpect(jsonPath("$.data.educationDevices").isNumber())
                .andExpect(jsonPath("$.data.homeDevices").isNumber())
                .andExpect(jsonPath("$.data.professionalDevices").isNumber())
                .andReturn();
        
        // 验证响应数据格式
        String responseContent = result.getResponse().getContentAsString();
        Map<String, Object> response = objectMapper.readValue(responseContent, Map.class);
        Map<String, Object> data = (Map<String, Object>) response.get("data");
        
        assertNotNull(data);
        assertTrue(data.containsKey("totalDevices"));
        assertTrue(data.containsKey("onlineDevices"));
        assertTrue(data.containsKey("offlineDevices"));
        assertTrue(data.containsKey("errorDevices"));
    }
    
    @Test
    @Order(2)
    @DisplayName("测试设备列表API - 分页查询")
    void testGetDeviceList() throws Exception {
        mockMvc.perform(get("/api/admin/devices")
                .param("page", "1")
                .param("pageSize", "20")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.list").isArray())
                .andExpect(jsonPath("$.data.total").isNumber())
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(20))
                .andExpect(jsonPath("$.data.totalPages").isNumber());
    }
    
    @Test
    @Order(3)
    @DisplayName("测试设备列表API - 关键词搜索")
    void testSearchDevicesByKeyword() throws Exception {
        mockMvc.perform(get("/api/admin/devices")
                .param("keyword", "YX2025")
                .param("page", "1")
                .param("pageSize", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list").isArray());
    }
    
    @Test
    @Order(4)
    @DisplayName("测试设备列表API - 状态筛选")
    void testFilterDevicesByStatus() throws Exception {
        mockMvc.perform(get("/api/admin/devices")
                .param("status", "online")
                .param("page", "1")
                .param("pageSize", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpected(jsonPath("$.data.list").isArray());
    }
    
    @Test
    @Order(5)
    @DisplayName("测试设备列表API - 型号筛选")
    void testFilterDevicesByModel() throws Exception {
        mockMvc.perform(get("/api/admin/devices")
                .param("model", "EDUCATION")
                .param("page", "1")
                .param("pageSize", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list").isArray());
    }
    
    @Test
    @Order(6)
    @DisplayName("测试设备创建API")
    void testCreateDevice() throws Exception {
        String deviceJson = objectMapper.writeValueAsString(testDevice);
        
        MvcResult result = mockMvc.perform(post("/api/admin/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(deviceJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("创建成功"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.serialNumber").value("YX2025TEST001"))
                .andExpect(jsonPath("$.data.model").value("EDUCATION"))
                .andExpect(jsonPath("$.data.status").value("offline"))
                .andExpect(jsonPath("$.data.firmwareVersion").value("1.0.0"))
                .andExpect(jsonPath("$.data.customerName").value("测试客户"))
                .andReturn();
        
        // 保存创建的设备ID用于后续测试
        String responseContent = result.getResponse().getContentAsString();
        Map<String, Object> response = objectMapper.readValue(responseContent, Map.class);
        Map<String, Object> data = (Map<String, Object>) response.get("data");
        testDeviceId = data.get("id").toString();
        
        assertNotNull(testDeviceId);
    }
    
    @Test
    @Order(7)
    @DisplayName("测试设备详情API")
    void testGetDeviceById() throws Exception {
        // 先创建一个设备用于测试
        if (testDeviceId == null) {
            testCreateDevice();
        }
        
        mockMvc.perform(get("/api/admin/devices/{id}", testDeviceId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.id").value(testDeviceId))
                .andExpect(jsonPath("$.data.serialNumber").exists())
                .andExpect(jsonPath("$.data.model").exists())
                .andExpect(jsonPath("$.data.status").exists())
                .andExpect(jsonPath("$.data.firmwareVersion").exists())
                .andExpect(jsonPath("$.data.customerName").exists())
                .andExpect(jsonPath("$.data.createdAt").exists())
                .andExpect(jsonPath("$.data.updatedAt").exists());
    }
    
    @Test
    @Order(8)
    @DisplayName("测试设备更新API")
    void testUpdateDevice() throws Exception {
        // 先创建一个设备用于测试
        if (testDeviceId == null) {
            testCreateDevice();
        }
        
        // 更新设备信息
        testDevice.setId(testDeviceId);
        testDevice.setCustomerName("更新后的客户名称");
        testDevice.setNotes("更新后的备注信息");
        
        String deviceJson = objectMapper.writeValueAsString(testDevice);
        
        mockMvc.perform(put("/api/admin/devices/{id}", testDeviceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(deviceJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("更新成功"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.id").value(testDeviceId))
                .andExpect(jsonPath("$.data.customerName").value("更新后的客户名称"))
                .andExpect(jsonPath("$.data.notes").value("更新后的备注信息"));
    }
    
    @Test
    @Order(9)
    @DisplayName("测试设备状态变更API")
    void testUpdateDeviceStatus() throws Exception {
        // 先创建一个设备用于测试
        if (testDeviceId == null) {
            testCreateDevice();
        }
        
        Map<String, String> statusUpdate = new HashMap<>();
        statusUpdate.put("status", "online");
        
        String statusJson = objectMapper.writeValueAsString(statusUpdate);
        
        mockMvc.perform(patch("/api/admin/devices/{id}/status", testDeviceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(statusJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("状态更新成功"));
    }
    
    @Test
    @Order(10)
    @DisplayName("测试设备重启API")
    void testRebootDevice() throws Exception {
        // 先创建一个设备用于测试
        if (testDeviceId == null) {
            testCreateDevice();
        }
        
        mockMvc.perform(post("/api/admin/devices/{id}/reboot", testDeviceId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("重启命令发送成功"));
    }
    
    @Test
    @Order(11)
    @DisplayName("测试设备激活API")
    void testActivateDevice() throws Exception {
        // 先创建一个设备用于测试
        if (testDeviceId == null) {
            testCreateDevice();
        }
        
        mockMvc.perform(post("/api/admin/devices/{id}/activate", testDeviceId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("激活成功"));
    }
    
    @Test
    @Order(12)
    @DisplayName("测试固件推送API")
    void testPushFirmware() throws Exception {
        // 先创建一个设备用于测试
        if (testDeviceId == null) {
            testCreateDevice();
        }
        
        Map<String, String> firmwareUpdate = new HashMap<>();
        firmwareUpdate.put("firmwareVersion", "1.1.0");
        firmwareUpdate.put("firmwareUrl", "http://example.com/firmware/1.1.0.bin");
        
        String firmwareJson = objectMapper.writeValueAsString(firmwareUpdate);
        
        mockMvc.perform(post("/api/admin/devices/{id}/firmware", testDeviceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(firmwareJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("固件推送成功"));
    }
    
    @Test
    @Order(13)
    @DisplayName("测试批量重启API")
    void testBatchRebootDevices() throws Exception {
        Map<String, Object> batchRequest = new HashMap<>();
        batchRequest.put("deviceIds", Arrays.asList(1L, 2L, 3L));
        
        String requestJson = objectMapper.writeValueAsString(batchRequest);
        
        mockMvc.perform(post("/api/admin/devices/batch/reboot")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.successCount").isNumber())
                .andExpect(jsonPath("$.data.failureCount").isNumber());
    }
    
    @Test
    @Order(14)
    @DisplayName("测试批量固件推送API")
    void testBatchPushFirmware() throws Exception {
        Map<String, Object> batchRequest = new HashMap<>();
        batchRequest.put("deviceIds", Arrays.asList(1L, 2L, 3L));
        batchRequest.put("firmwareVersion", "1.1.0");
        batchRequest.put("firmwareUrl", "http://example.com/firmware/1.1.0.bin");
        
        String requestJson = objectMapper.writeValueAsString(batchRequest);
        
        mockMvc.perform(post("/api/admin/devices/batch/firmware")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.successCount").isNumber())
                .andExpect(jsonPath("$.data.failureCount").isNumber());
    }
    
    @Test
    @Order(15)
    @DisplayName("测试设备日志API")
    void testGetDeviceLogs() throws Exception {
        // 先创建一个设备用于测试
        if (testDeviceId == null) {
            testCreateDevice();
        }
        
        mockMvc.perform(get("/api/admin/devices/{id}/logs", testDeviceId)
                .param("page", "1")
                .param("pageSize", "20")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.list").isArray())
                .andExpect(jsonPath("$.data.total").isNumber())
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(20));
    }
    
    @Test
    @Order(16)
    @DisplayName("测试设备日志筛选API")
    void testFilterDeviceLogs() throws Exception {
        // 先创建一个设备用于测试
        if (testDeviceId == null) {
            testCreateDevice();
        }
        
        mockMvc.perform(get("/api/admin/devices/{id}/logs", testDeviceId)
                .param("level", "error")
                .param("category", "system")
                .param("page", "1")
                .param("pageSize", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list").isArray());
    }
    
    @Test
    @Order(17)
    @DisplayName("测试高级搜索API")
    void testAdvancedSearch() throws Exception {
        ManagedDeviceSearchCriteria criteria = new ManagedDeviceSearchCriteria();
        criteria.setKeyword("YX2025");
        criteria.setPage(1);
        criteria.setPageSize(20);
        criteria.setSortBy("createdAt");
        criteria.setSortOrder("DESC");
        
        String criteriaJson = objectMapper.writeValueAsString(criteria);
        
        mockMvc.perform(post("/api/admin/devices/search/advanced")
                .contentType(MediaType.APPLICATION_JSON)
                .content(criteriaJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("搜索成功"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.list").isArray())
                .andExpect(jsonPath("$.data.total").isNumber());
    }
    
    @Test
    @Order(18)
    @DisplayName("测试快速搜索API")
    void testQuickSearch() throws Exception {
        mockMvc.perform(get("/api/admin/devices/search/quick")
                .param("keyword", "YX2025")
                .param("page", "1")
                .param("pageSize", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("搜索成功"))
                .andExpect(jsonPath("$.data.list").isArray());
    }
    
    @Test
    @Order(19)
    @DisplayName("测试搜索建议API")
    void testSearchSuggestions() throws Exception {
        mockMvc.perform(get("/api/admin/devices/search/suggestions")
                .param("field", "serialNumber")
                .param("query", "YX")
                .param("limit", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("获取建议成功"))
                .andExpect(jsonPath("$.data").isArray());
    }
    
    @Test
    @Order(20)
    @DisplayName("测试客户选项API")
    void testGetCustomerOptions() throws Exception {
        mockMvc.perform(get("/api/admin/customers/options")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data").isArray());
    }
    
    @Test
    @Order(21)
    @DisplayName("测试设备删除API")
    void testDeleteDevice() throws Exception {
        // 先创建一个设备用于测试
        if (testDeviceId == null) {
            testCreateDevice();
        }
        
        mockMvc.perform(delete("/api/admin/devices/{id}", testDeviceId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("删除成功"));
        
        // 验证设备已被删除（软删除）
        mockMvc.perform(get("/api/admin/devices/{id}", testDeviceId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }
    
    @Test
    @Order(22)
    @DisplayName("测试批量删除API")
    void testBatchDeleteDevices() throws Exception {
        Map<String, Object> batchRequest = new HashMap<>();
        batchRequest.put("deviceIds", Arrays.asList(1L, 2L, 3L));
        
        String requestJson = objectMapper.writeValueAsString(batchRequest);
        
        mockMvc.perform(delete("/api/admin/devices/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.successCount").isNumber())
                .andExpect(jsonPath("$.data.failureCount").isNumber());
    }
    
    @Test
    @Order(23)
    @DisplayName("测试API错误处理 - 无效设备ID")
    void testApiErrorHandling_InvalidDeviceId() throws Exception {
        mockMvc.perform(get("/api/admin/devices/{id}", "invalid-id")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").exists());
    }
    
    @Test
    @Order(24)
    @DisplayName("测试API错误处理 - 设备不存在")
    void testApiErrorHandling_DeviceNotFound() throws Exception {
        mockMvc.perform(get("/api/admin/devices/{id}", "999999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").exists());
    }
    
    @Test
    @Order(25)
    @DisplayName("测试API错误处理 - 数据验证失败")
    void testApiErrorHandling_ValidationFailure() throws Exception {
        ManagedDeviceDTO invalidDevice = new ManagedDeviceDTO();
        invalidDevice.setSerialNumber(""); // 空序列号
        invalidDevice.setModel("INVALID_MODEL"); // 无效型号
        
        String deviceJson = objectMapper.writeValueAsString(invalidDevice);
        
        mockMvc.perform(post("/api/admin/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(deviceJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").exists());
    }
    
    @Test
    @Order(26)
    @DisplayName("测试API性能 - 响应时间")
    void testApiPerformance_ResponseTime() throws Exception {
        long startTime = System.currentTimeMillis();
        
        mockMvc.perform(get("/api/admin/devices")
                .param("page", "1")
                .param("pageSize", "20")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;
        
        // 验证响应时间小于2秒
        assertTrue(responseTime < 2000, "API响应时间应该小于2秒，实际: " + responseTime + "ms");
    }
    
    @Test
    @Order(27)
    @DisplayName("测试API数据格式 - 字段映射一致性")
    void testApiDataFormat_FieldMapping() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/admin/devices")
                .param("page", "1")
                .param("pageSize", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        
        String responseContent = result.getResponse().getContentAsString();
        Map<String, Object> response = objectMapper.readValue(responseContent, Map.class);
        Map<String, Object> data = (Map<String, Object>) response.get("data");
        
        if (data != null && data.get("list") != null) {
            java.util.List<Map<String, Object>> devices = (java.util.List<Map<String, Object>>) data.get("list");
            if (!devices.isEmpty()) {
                Map<String, Object> device = devices.get(0);
                
                // 验证字段名使用camelCase格式
                assertTrue(device.containsKey("serialNumber"), "应该包含serialNumber字段");
                assertTrue(device.containsKey("firmwareVersion"), "应该包含firmwareVersion字段");
                assertTrue(device.containsKey("customerName"), "应该包含customerName字段");
                assertTrue(device.containsKey("customerPhone"), "应该包含customerPhone字段");
                assertTrue(device.containsKey("lastOnlineAt"), "应该包含lastOnlineAt字段");
                assertTrue(device.containsKey("activatedAt"), "应该包含activatedAt字段");
                assertTrue(device.containsKey("createdAt"), "应该包含createdAt字段");
                assertTrue(device.containsKey("updatedAt"), "应该包含updatedAt字段");
                assertTrue(device.containsKey("createdBy"), "应该包含createdBy字段");
                
                // 验证不应该包含snake_case字段
                assertFalse(device.containsKey("serial_number"), "不应该包含serial_number字段");
                assertFalse(device.containsKey("firmware_version"), "不应该包含firmware_version字段");
                assertFalse(device.containsKey("customer_name"), "不应该包含customer_name字段");
                assertFalse(device.containsKey("last_online_at"), "不应该包含last_online_at字段");
            }
        }
    }
}