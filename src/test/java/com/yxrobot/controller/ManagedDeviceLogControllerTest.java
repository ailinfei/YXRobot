package com.yxrobot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yxrobot.service.ManagedDeviceLogService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 设备日志控制器测试类
 */
@ExtendWith(MockitoExtension.class)
@WebMvcTest(ManagedDeviceLogController.class)
class ManagedDeviceLogControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private ManagedDeviceLogService logService;
    
    private Map<String, Object> testLogsResult;
    
    @BeforeEach
    void setUp() {
        testLogsResult = createTestLogsResult();
    }
    
    @Test
    void testGetDeviceLogs_Success() throws Exception {
        // 准备测试数据
        Long deviceId = 1L;
        when(logService.getManagedDeviceLogs(eq(deviceId), eq(1), eq(20), 
                isNull(), isNull(), isNull(), isNull()))
            .thenReturn(testLogsResult);
        
        // 执行测试
        mockMvc.perform(get("/api/admin/devices/{id}/logs", deviceId)
                .param("page", "1")
                .param("pageSize", "20")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.list").isArray())
                .andExpect(jsonPath("$.data.total").value(5))
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(20));
        
        // 验证服务调用
        verify(logService, times(1)).getManagedDeviceLogs(
                deviceId, 1, 20, null, null, null, null);
    }
    
    @Test
    void testGetDeviceLogs_WithFilters() throws Exception {
        // 准备测试数据
        Long deviceId = 1L;
        when(logService.getManagedDeviceLogs(eq(deviceId), eq(1), eq(20), 
                eq("error"), eq("system"), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(testLogsResult);
        
        // 执行测试
        mockMvc.perform(get("/api/admin/devices/{id}/logs", deviceId)
                .param("page", "1")
                .param("pageSize", "20")
                .param("level", "error")
                .param("category", "system")
                .param("startDate", "2025-01-01T00:00:00")
                .param("endDate", "2025-01-31T23:59:59")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"));
        
        // 验证服务调用
        verify(logService, times(1)).getManagedDeviceLogs(
                eq(deviceId), eq(1), eq(20), eq("error"), eq("system"), 
                any(LocalDateTime.class), any(LocalDateTime.class));
    }
    
    @Test
    void testGetDeviceLogs_InvalidDeviceId() throws Exception {
        // 准备测试数据
        Long deviceId = 999L;
        when(logService.getManagedDeviceLogs(eq(deviceId), anyInt(), anyInt(), 
                anyString(), anyString(), any(), any()))
            .thenThrow(new IllegalArgumentException("设备ID不能为空"));
        
        // 执行测试
        mockMvc.perform(get("/api/admin/devices/{id}/logs", deviceId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("设备ID不能为空"));
        
        // 验证服务调用
        verify(logService, times(1)).getManagedDeviceLogs(
                eq(deviceId), anyInt(), anyInt(), isNull(), isNull(), isNull(), isNull());
    }
    
    @Test
    void testGetLogLevelStats_Success() throws Exception {
        // 准备测试数据
        Long deviceId = 1L;
        
        // 执行测试
        mockMvc.perform(get("/api/admin/devices/{id}/logs/level-stats", deviceId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.info").value(150))
                .andExpect(jsonPath("$.data.warning").value(25))
                .andExpect(jsonPath("$.data.error").value(8))
                .andExpect(jsonPath("$.data.debug").value(45))
                .andExpect(jsonPath("$.data.total").value(228));
    }
    
    @Test
    void testGetLogCategoryStats_Success() throws Exception {
        // 准备测试数据
        Long deviceId = 1L;
        
        // 执行测试
        mockMvc.perform(get("/api/admin/devices/{id}/logs/category-stats", deviceId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.system").value(120))
                .andExpect(jsonPath("$.data.user").value(65))
                .andExpect(jsonPath("$.data.network").value(28))
                .andExpect(jsonPath("$.data.hardware").value(10))
                .andExpect(jsonPath("$.data.software").value(5))
                .andExpect(jsonPath("$.data.total").value(228));
    }
    
    @Test
    void testGetLatestLogs_Success() throws Exception {
        // 准备测试数据
        Long deviceId = 1L;
        when(logService.getManagedDeviceLogs(eq(deviceId), eq(1), eq(10), 
                isNull(), isNull(), isNull(), isNull()))
            .thenReturn(testLogsResult);
        
        // 执行测试
        mockMvc.perform(get("/api/admin/devices/{id}/logs/latest", deviceId)
                .param("limit", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.list").isArray());
        
        // 验证服务调用
        verify(logService, times(1)).getManagedDeviceLogs(
                deviceId, 1, 10, null, null, null, null);
    }
    
    @Test
    void testGetLatestLogs_DefaultLimit() throws Exception {
        // 准备测试数据
        Long deviceId = 1L;
        when(logService.getManagedDeviceLogs(eq(deviceId), eq(1), eq(10), 
                isNull(), isNull(), isNull(), isNull()))
            .thenReturn(testLogsResult);
        
        // 执行测试（不传limit参数，使用默认值10）
        mockMvc.perform(get("/api/admin/devices/{id}/logs/latest", deviceId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        
        // 验证服务调用（使用默认limit=10）
        verify(logService, times(1)).getManagedDeviceLogs(
                deviceId, 1, 10, null, null, null, null);
    }
    
    @Test
    void testSearchDeviceLogs_Success() throws Exception {
        // 准备测试数据
        Long deviceId = 1L;
        String keyword = "error";
        when(logService.getManagedDeviceLogs(eq(deviceId), eq(1), eq(20), 
                isNull(), isNull(), isNull(), isNull()))
            .thenReturn(testLogsResult);
        
        // 执行测试
        mockMvc.perform(get("/api/admin/devices/{id}/logs/search", deviceId)
                .param("keyword", keyword)
                .param("page", "1")
                .param("pageSize", "20")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("搜索成功"))
                .andExpect(jsonPath("$.data.keyword").value(keyword))
                .andExpect(jsonPath("$.data.results").exists());
        
        // 验证服务调用
        verify(logService, times(1)).getManagedDeviceLogs(
                deviceId, 1, 20, null, null, null, null);
    }
    
    @Test
    void testSearchDeviceLogs_WithFilters() throws Exception {
        // 准备测试数据
        Long deviceId = 1L;
        String keyword = "connection";
        when(logService.getManagedDeviceLogs(eq(deviceId), eq(1), eq(20), 
                eq("warning"), eq("network"), isNull(), isNull()))
            .thenReturn(testLogsResult);
        
        // 执行测试
        mockMvc.perform(get("/api/admin/devices/{id}/logs/search", deviceId)
                .param("keyword", keyword)
                .param("level", "warning")
                .param("category", "network")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("搜索成功"));
        
        // 验证服务调用
        verify(logService, times(1)).getManagedDeviceLogs(
                deviceId, 1, 20, "warning", "network", null, null);
    }
    
    @Test
    void testExportDeviceLogs_Success() throws Exception {
        // 准备测试数据
        Long deviceId = 1L;
        
        // 执行测试
        mockMvc.perform(get("/api/admin/devices/{id}/logs/export", deviceId)
                .param("format", "csv")
                .param("level", "error")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("导出成功"))
                .andExpect(jsonPath("$.data.filename").exists())
                .andExpect(jsonPath("$.data.downloadUrl").exists())
                .andExpect(jsonPath("$.data.format").value("csv"))
                .andExpect(jsonPath("$.data.exportTime").exists());
    }
    
    @Test
    void testExportDeviceLogs_DefaultFormat() throws Exception {
        // 准备测试数据
        Long deviceId = 1L;
        
        // 执行测试（不传format参数，使用默认值csv）
        mockMvc.perform(get("/api/admin/devices/{id}/logs/export", deviceId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.format").value("csv"));
    }
    
    @Test
    void testCleanupDeviceLogs_Success() throws Exception {
        // 准备测试数据
        Long deviceId = 1L;
        ManagedDeviceLogController.LogCleanupRequest request = new ManagedDeviceLogController.LogCleanupRequest();
        request.setStartDate(LocalDateTime.now().minusDays(30));
        request.setEndDate(LocalDateTime.now().minusDays(7));
        
        when(logService.deleteLogsByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(15);
        
        // 执行测试
        mockMvc.perform(delete("/api/admin/devices/{id}/logs/cleanup", deviceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("日志清理成功"))
                .andExpect(jsonPath("$.data.deletedCount").value(15))
                .andExpect(jsonPath("$.data.startDate").exists())
                .andExpect(jsonPath("$.data.endDate").exists())
                .andExpect(jsonPath("$.data.cleanupTime").exists());
        
        // 验证服务调用
        verify(logService, times(1)).deleteLogsByDateRange(
                any(LocalDateTime.class), any(LocalDateTime.class));
    }
    
    @Test
    void testCleanupDeviceLogs_InvalidDateRange() throws Exception {
        // 准备测试数据（开始时间晚于结束时间）
        Long deviceId = 1L;
        ManagedDeviceLogController.LogCleanupRequest request = new ManagedDeviceLogController.LogCleanupRequest();
        request.setStartDate(LocalDateTime.now());
        request.setEndDate(LocalDateTime.now().minusDays(7));
        
        // 执行测试
        mockMvc.perform(delete("/api/admin/devices/{id}/logs/cleanup", deviceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("开始时间不能晚于结束时间"));
        
        // 验证服务未被调用
        verify(logService, never()).deleteLogsByDateRange(any(), any());
    }
    
    @Test
    void testCleanupDeviceLogs_MissingDates() throws Exception {
        // 准备测试数据（缺少日期）
        Long deviceId = 1L;
        ManagedDeviceLogController.LogCleanupRequest request = new ManagedDeviceLogController.LogCleanupRequest();
        // 不设置日期
        
        // 执行测试
        mockMvc.perform(delete("/api/admin/devices/{id}/logs/cleanup", deviceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("开始时间和结束时间不能为空"));
        
        // 验证服务未被调用
        verify(logService, never()).deleteLogsByDateRange(any(), any());
    }
    
    @Test
    void testGetDeviceLogs_ServiceException() throws Exception {
        // 准备测试数据
        Long deviceId = 1L;
        when(logService.getManagedDeviceLogs(anyLong(), anyInt(), anyInt(), 
                anyString(), anyString(), any(), any()))
            .thenThrow(new RuntimeException("数据库连接失败"));
        
        // 执行测试
        mockMvc.perform(get("/api/admin/devices/{id}/logs", deviceId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("查询失败: 数据库连接失败"));
        
        // 验证服务调用
        verify(logService, times(1)).getManagedDeviceLogs(
                anyLong(), anyInt(), anyInt(), isNull(), isNull(), isNull(), isNull());
    }
    
    @Test
    void testCleanupDeviceLogs_ServiceException() throws Exception {
        // 准备测试数据
        Long deviceId = 1L;
        ManagedDeviceLogController.LogCleanupRequest request = new ManagedDeviceLogController.LogCleanupRequest();
        request.setStartDate(LocalDateTime.now().minusDays(30));
        request.setEndDate(LocalDateTime.now().minusDays(7));
        
        when(logService.deleteLogsByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenThrow(new RuntimeException("清理操作失败"));
        
        // 执行测试
        mockMvc.perform(delete("/api/admin/devices/{id}/logs/cleanup", deviceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("清理失败: 清理操作失败"));
        
        // 验证服务调用
        verify(logService, times(1)).deleteLogsByDateRange(
                any(LocalDateTime.class), any(LocalDateTime.class));
    }
    
    /**
     * 创建测试日志结果
     */
    private Map<String, Object> createTestLogsResult() {
        Map<String, Object> result = new HashMap<>();
        result.put("list", new ArrayList<>());
        result.put("total", 5);
        result.put("page", 1);
        result.put("pageSize", 20);
        result.put("totalPages", 1);
        return result;
    }
}