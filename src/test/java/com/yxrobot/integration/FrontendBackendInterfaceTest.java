package com.yxrobot.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
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
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 前后端接口匹配验证测试
 * 验证API响应格式与前端TypeScript接口的匹配性
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("前后端接口匹配验证测试")
class FrontendBackendInterfaceTest {
    
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private MockMvc mockMvc;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    
    @Test
    @DisplayName("验证ManagedDevice接口格式")
    void testManagedDeviceInterface() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/admin/devices")
                .param("page", "1")
                .param("pageSize", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        
        String responseContent = result.getResponse().getContentAsString();
        JsonNode response = objectMapper.readTree(responseContent);
        JsonNode data = response.get("data");
        
        if (data != null && data.has("list") && data.get("list").isArray() && data.get("list").size() > 0) {
            JsonNode device = data.get("list").get(0);
            
            // 验证ManagedDevice接口必需字段
            assertDeviceInterfaceFields(device);
        }
    }
    
    @Test
    @DisplayName("验证ManagedDeviceStats接口格式")
    void testManagedDeviceStatsInterface() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/admin/devices/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        
        String responseContent = result.getResponse().getContentAsString();
        JsonNode response = objectMapper.readTree(responseContent);
        JsonNode data = response.get("data");
        
        assertNotNull(data, "统计数据不能为空");
        
        // 验证ManagedDeviceStats接口字段
        List<String> requiredStatsFields = Arrays.asList(
            "totalDevices", "onlineDevices", "offlineDevices", "errorDevices",
            "educationDevices", "homeDevices", "professionalDevices"
        );
        
        for (String field : requiredStatsFields) {
            assertTrue(data.has(field), "统计数据应该包含字段: " + field);
            assertTrue(data.get(field).isNumber(), "统计字段应该是数字类型: " + field);
        }
    }
    
    @Test
    @DisplayName("验证ManagedDeviceLog接口格式")
    void testManagedDeviceLogInterface() throws Exception {
        // 假设存在设备ID为1的设备
        MvcResult result = mockMvc.perform(get("/api/admin/devices/1/logs")
                .param("page", "1")
                .param("pageSize", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        
        String responseContent = result.getResponse().getContentAsString();
        JsonNode response = objectMapper.readTree(responseContent);
        JsonNode data = response.get("data");
        
        if (data != null && data.has("list") && data.get("list").isArray() && data.get("list").size() > 0) {
            JsonNode log = data.get("list").get(0);
            
            // 验证ManagedDeviceLog接口字段
            List<String> requiredLogFields = Arrays.asList(
                "id", "deviceId", "level", "category", "message", "timestamp"
            );
            
            for (String field : requiredLogFields) {
                assertTrue(log.has(field), "日志数据应该包含字段: " + field);
            }
            
            // 验证字段类型
            if (log.has("id")) {
                assertTrue(log.get("id").isTextual() || log.get("id").isNumber(), "id应该是字符串或数字类型");
            }
            if (log.has("deviceId")) {
                assertTrue(log.get("deviceId").isTextual() || log.get("deviceId").isNumber(), "deviceId应该是字符串或数字类型");
            }
            if (log.has("level")) {
                assertTrue(log.get("level").isTextual(), "level应该是字符串类型");
            }
            if (log.has("category")) {
                assertTrue(log.get("category").isTextual(), "category应该是字符串类型");
            }
            if (log.has("message")) {
                assertTrue(log.get("message").isTextual(), "message应该是字符串类型");
            }
            if (log.has("timestamp")) {
                assertTrue(log.get("timestamp").isTextual(), "timestamp应该是字符串类型");
            }
        }
    }
    
    @Test
    @DisplayName("验证PageResult接口格式")
    void testPageResultInterface() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/admin/devices")
                .param("page", "1")
                .param("pageSize", "20")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        
        String responseContent = result.getResponse().getContentAsString();
        JsonNode response = objectMapper.readTree(responseContent);
        JsonNode data = response.get("data");
        
        assertNotNull(data, "分页数据不能为空");
        
        // 验证PageResult接口字段
        List<String> requiredPageFields = Arrays.asList(
            "list", "total", "page", "pageSize", "totalPages"
        );
        
        for (String field : requiredPageFields) {
            assertTrue(data.has(field), "分页数据应该包含字段: " + field);
        }
        
        // 验证字段类型
        assertTrue(data.get("list").isArray(), "list应该是数组类型");
        assertTrue(data.get("total").isNumber(), "total应该是数字类型");
        assertTrue(data.get("page").isNumber(), "page应该是数字类型");
        assertTrue(data.get("pageSize").isNumber(), "pageSize应该是数字类型");
        assertTrue(data.get("totalPages").isNumber(), "totalPages应该是数字类型");
    }
    
    @Test
    @DisplayName("验证API响应统一格式")
    void testUnifiedApiResponseFormat() throws Exception {
        // 测试成功响应格式
        MvcResult successResult = mockMvc.perform(get("/api/admin/devices/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        
        String successContent = successResult.getResponse().getContentAsString();
        JsonNode successResponse = objectMapper.readTree(successContent);
        
        // 验证成功响应格式
        assertTrue(successResponse.has("code"), "响应应该包含code字段");
        assertTrue(successResponse.has("message"), "响应应该包含message字段");
        assertTrue(successResponse.has("data"), "响应应该包含data字段");
        
        assertEquals(200, successResponse.get("code").asInt(), "成功响应code应该是200");
        assertTrue(successResponse.get("message").isTextual(), "message应该是字符串类型");
        
        // 测试错误响应格式
        MvcResult errorResult = mockMvc.perform(get("/api/admin/devices/999999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
        
        String errorContent = errorResult.getResponse().getContentAsString();
        JsonNode errorResponse = objectMapper.readTree(errorContent);
        
        // 验证错误响应格式
        assertTrue(errorResponse.has("code"), "错误响应应该包含code字段");
        assertTrue(errorResponse.has("message"), "错误响应应该包含message字段");
        
        assertEquals(404, errorResponse.get("code").asInt(), "错误响应code应该是404");
        assertTrue(errorResponse.get("message").isTextual(), "错误message应该是字符串类型");
    }
    
    @Test
    @DisplayName("验证搜索条件接口格式")
    void testSearchCriteriaInterface() throws Exception {
        Map<String, Object> searchCriteria = new HashMap<>();
        searchCriteria.put("page", 1);
        searchCriteria.put("pageSize", 20);
        searchCriteria.put("keyword", "YX2025");
        searchCriteria.put("statusList", Arrays.asList("online", "offline"));
        searchCriteria.put("modelList", Arrays.asList("EDUCATION", "HOME"));
        searchCriteria.put("sortBy", "createdAt");
        searchCriteria.put("sortOrder", "DESC");
        
        String criteriaJson = objectMapper.writeValueAsString(searchCriteria);
        
        MvcResult result = mockMvc.perform(post("/api/admin/devices/search/advanced")
                .contentType(MediaType.APPLICATION_JSON)
                .content(criteriaJson))
                .andExpect(status().isOk())
                .andReturn();
        
        String responseContent = result.getResponse().getContentAsString();
        JsonNode response = objectMapper.readTree(responseContent);
        
        // 验证搜索响应格式
        assertTrue(response.has("code"), "搜索响应应该包含code字段");
        assertTrue(response.has("message"), "搜索响应应该包含message字段");
        assertTrue(response.has("data"), "搜索响应应该包含data字段");
        
        JsonNode data = response.get("data");
        assertTrue(data.has("list"), "搜索结果应该包含list字段");
        assertTrue(data.has("total"), "搜索结果应该包含total字段");
    }
    
    @Test
    @DisplayName("验证批量操作响应格式")
    void testBatchOperationResponseFormat() throws Exception {
        Map<String, Object> batchRequest = new HashMap<>();
        batchRequest.put("deviceIds", Arrays.asList(1L, 2L, 3L));
        
        String requestJson = objectMapper.writeValueAsString(batchRequest);
        
        MvcResult result = mockMvc.perform(post("/api/admin/devices/batch/reboot")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andReturn();
        
        String responseContent = result.getResponse().getContentAsString();
        JsonNode response = objectMapper.readTree(responseContent);
        JsonNode data = response.get("data");
        
        // 验证批量操作响应格式
        assertTrue(data.has("successCount"), "批量操作响应应该包含successCount字段");
        assertTrue(data.has("failureCount"), "批量操作响应应该包含failureCount字段");
        assertTrue(data.get("successCount").isNumber(), "successCount应该是数字类型");
        assertTrue(data.get("failureCount").isNumber(), "failureCount应该是数字类型");
        
        if (data.has("failedItems")) {
            assertTrue(data.get("failedItems").isArray(), "failedItems应该是数组类型");
        }
    }
    
    @Test
    @DisplayName("验证时间格式一致性")
    void testDateTimeFormatConsistency() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/admin/devices")
                .param("page", "1")
                .param("pageSize", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        
        String responseContent = result.getResponse().getContentAsString();
        JsonNode response = objectMapper.readTree(responseContent);
        JsonNode data = response.get("data");
        
        if (data != null && data.has("list") && data.get("list").isArray() && data.get("list").size() > 0) {
            JsonNode device = data.get("list").get(0);
            
            // 验证时间字段格式
            List<String> dateFields = Arrays.asList("createdAt", "updatedAt", "lastOnlineAt", "activatedAt");
            
            for (String field : dateFields) {
                if (device.has(field) && !device.get(field).isNull()) {
                    String dateValue = device.get(field).asText();
                    // 验证时间格式（ISO 8601格式或自定义格式）
                    assertTrue(isValidDateTimeFormat(dateValue), 
                        "时间字段格式应该一致: " + field + " = " + dateValue);
                }
            }
        }
    }
    
    /**
     * 验证设备接口字段
     */
    private void assertDeviceInterfaceFields(JsonNode device) {
        // 验证必需字段
        List<String> requiredFields = Arrays.asList(
            "id", "serialNumber", "model", "status", "firmwareVersion",
            "customerName", "createdAt", "updatedAt"
        );
        
        for (String field : requiredFields) {
            assertTrue(device.has(field), "设备数据应该包含字段: " + field);
        }
        
        // 验证可选字段
        List<String> optionalFields = Arrays.asList(
            "customerId", "customerPhone", "lastOnlineAt", "activatedAt", 
            "createdBy", "notes", "specifications", "usageStats", 
            "configuration", "location", "maintenanceRecords"
        );
        
        // 可选字段如果存在，验证其类型
        for (String field : optionalFields) {
            if (device.has(field)) {
                assertNotNull(device.get(field), "可选字段如果存在不应该为null: " + field);
            }
        }
        
        // 验证字段类型
        if (device.has("id")) {
            assertTrue(device.get("id").isTextual() || device.get("id").isNumber(), 
                "id应该是字符串或数字类型");
        }
        if (device.has("serialNumber")) {
            assertTrue(device.get("serialNumber").isTextual(), "serialNumber应该是字符串类型");
        }
        if (device.has("model")) {
            assertTrue(device.get("model").isTextual(), "model应该是字符串类型");
        }
        if (device.has("status")) {
            assertTrue(device.get("status").isTextual(), "status应该是字符串类型");
        }
    }
    
    /**
     * 验证时间格式是否有效
     */
    private boolean isValidDateTimeFormat(String dateTime) {
        if (dateTime == null || dateTime.isEmpty()) {
            return false;
        }
        
        // 检查常见的时间格式
        return dateTime.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*") || // ISO 8601
               dateTime.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}") ||   // yyyy-MM-dd HH:mm:ss
               dateTime.matches("\\d{13}"); // 时间戳
    }
}