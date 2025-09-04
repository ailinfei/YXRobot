package com.yxrobot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yxrobot.service.CustomerService;
import com.yxrobot.service.ManagedDeviceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 设备管理辅助功能控制器测试类
 */
@ExtendWith(MockitoExtension.class)
@WebMvcTest(ManagedDeviceAuxiliaryController.class)
class ManagedDeviceAuxiliaryControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private CustomerService customerService;
    
    @MockBean
    private ManagedDeviceService managedDeviceService;
    
    @BeforeEach
    void setUp() {
        // 测试前的准备工作
    }
    
    @Test
    void testGetCustomerOptions_Success() throws Exception {
        // 执行测试
        mockMvc.perform(get("/api/admin/customers/options")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").exists())
                .andExpect(jsonPath("$.data[0].name").exists())
                .andExpect(jsonPath("$.data[0].value").exists())
                .andExpect(jsonPath("$.data[0].label").exists());
    }
    
    @Test
    void testGetCustomerOptions_WithKeyword() throws Exception {
        // 执行测试
        mockMvc.perform(get("/api/admin/customers/options")
                .param("keyword", "张三")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data").isArray());
    }
    
    @Test
    void testExportDevices_Success() throws Exception {
        // 执行测试
        mockMvc.perform(get("/api/admin/devices/export")
                .param("format", "csv")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("导出成功"))
                .andExpect(jsonPath("$.data.filename").exists())
                .andExpect(jsonPath("$.data.downloadUrl").exists())
                .andExpect(jsonPath("$.data.format").value("csv"))
                .andExpect(jsonPath("$.data.fileSize").exists())
                .andExpect(jsonPath("$.data.exportTime").exists())
                .andExpect(jsonPath("$.data.stats").exists())
                .andExpect(jsonPath("$.data.filters").exists());
    }
    
    @Test
    void testExportDevices_WithFilters() throws Exception {
        // 执行测试
        mockMvc.perform(get("/api/admin/devices/export")
                .param("keyword", "测试设备")
                .param("status", "online")
                .param("model", "YX-EDU-2024")
                .param("customerId", "1")
                .param("format", "excel")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("导出成功"))
                .andExpect(jsonPath("$.data.format").value("excel"));
    }
    
    @Test
    void testExportDevices_InvalidFormat() throws Exception {
        // 执行测试
        mockMvc.perform(get("/api/admin/devices/export")
                .param("format", "pdf")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("不支持的导出格式: pdf，支持的格式: csv, excel, json"));
    }
    
    @Test
    void testExportDevices_DefaultFormat() throws Exception {
        // 执行测试（不传format参数，使用默认值csv）
        mockMvc.perform(get("/api/admin/devices/export")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.format").value("csv"));
    }
    
    @Test
    void testGetDeviceModelOptions_Success() throws Exception {
        // 执行测试
        mockMvc.perform(get("/api/admin/devices/model-options")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].code").exists())
                .andExpect(jsonPath("$.data[0].name").exists())
                .andExpect(jsonPath("$.data[0].description").exists())
                .andExpect(jsonPath("$.data[0].value").exists())
                .andExpect(jsonPath("$.data[0].label").exists());
    }
    
    @Test
    void testGetDeviceStatusOptions_Success() throws Exception {
        // 执行测试
        mockMvc.perform(get("/api/admin/devices/status-options")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].code").exists())
                .andExpect(jsonPath("$.data[0].name").exists())
                .andExpect(jsonPath("$.data[0].description").exists())
                .andExpect(jsonPath("$.data[0].value").exists())
                .andExpect(jsonPath("$.data[0].label").exists());
    }
    
    @Test
    void testGetExportHistory_Success() throws Exception {
        // 执行测试
        mockMvc.perform(get("/api/admin/devices/export-history")
                .param("page", "1")
                .param("pageSize", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.list").isArray())
                .andExpect(jsonPath("$.data.total").exists())
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(10))
                .andExpect(jsonPath("$.data.totalPages").exists());
    }
    
    @Test
    void testGetExportHistory_DefaultPagination() throws Exception {
        // 执行测试（不传分页参数，使用默认值）
        mockMvc.perform(get("/api/admin/devices/export-history")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(10));
    }
    
    @Test
    void testExportDevices_JsonFormat() throws Exception {
        // 执行测试
        mockMvc.perform(get("/api/admin/devices/export")
                .param("format", "json")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.format").value("json"));
    }
    
    @Test
    void testExportDevices_ExcelFormat() throws Exception {
        // 执行测试
        mockMvc.perform(get("/api/admin/devices/export")
                .param("format", "xlsx")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.format").value("xlsx"));
    }
    
    @Test
    void testExportDevices_WithDateRange() throws Exception {
        // 执行测试
        mockMvc.perform(get("/api/admin/devices/export")
                .param("startDate", "2025-01-01T00:00:00")
                .param("endDate", "2025-01-31T23:59:59")
                .param("format", "csv")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("导出成功"));
    }
    
    @Test
    void testGetCustomerOptions_EmptyKeyword() throws Exception {
        // 执行测试
        mockMvc.perform(get("/api/admin/customers/options")
                .param("keyword", "")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }
    
    @Test
    void testGetCustomerOptions_NoMatchKeyword() throws Exception {
        // 执行测试
        mockMvc.perform(get("/api/admin/customers/options")
                .param("keyword", "不存在的客户")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }
    
    @Test
    void testExportDevices_AllFormats() throws Exception {
        // 测试所有支持的格式
        String[] formats = {"csv", "excel", "xlsx", "json"};
        
        for (String format : formats) {
            mockMvc.perform(get("/api/admin/devices/export")
                    .param("format", format)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("导出成功"));
        }
    }
    
    @Test
    void testGetDeviceModelOptions_CheckAllModels() throws Exception {
        // 执行测试并验证所有型号都存在
        mockMvc.perform(get("/api/admin/devices/model-options")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(3)); // 应该有3个型号
    }
    
    @Test
    void testGetDeviceStatusOptions_CheckAllStatuses() throws Exception {
        // 执行测试并验证所有状态都存在
        mockMvc.perform(get("/api/admin/devices/status-options")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(4)); // 应该有4个状态
    }
}