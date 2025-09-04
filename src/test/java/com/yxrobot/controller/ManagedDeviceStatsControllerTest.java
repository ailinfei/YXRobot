package com.yxrobot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yxrobot.dto.ManagedDeviceStatsDTO;
import com.yxrobot.service.ManagedDeviceStatsService;
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
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 设备统计控制器测试类
 */
@ExtendWith(MockitoExtension.class)
@WebMvcTest(ManagedDeviceStatsController.class)
class ManagedDeviceStatsControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private ManagedDeviceStatsService statsService;
    
    private ManagedDeviceStatsDTO testStats;
    
    @BeforeEach
    void setUp() {
        testStats = createTestStats();
    }
    
    @Test
    void testGetDeviceStats_Success() throws Exception {
        // 准备测试数据
        when(statsService.getManagedDeviceStats(any(), any())).thenReturn(testStats);
        
        // 执行测试
        mockMvc.perform(get("/api/admin/devices/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.total").value(100))
                .andExpect(jsonPath("$.data.online").value(80))
                .andExpect(jsonPath("$.data.offline").value(15))
                .andExpect(jsonPath("$.data.error").value(3))
                .andExpect(jsonPath("$.data.maintenance").value(2));
        
        // 验证服务调用
        verify(statsService, times(1)).getManagedDeviceStats(isNull(), isNull());
    }
    
    @Test
    void testGetDeviceStats_WithDateRange() throws Exception {
        // 准备测试数据
        when(statsService.getManagedDeviceStats(any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(testStats);
        
        // 执行测试
        mockMvc.perform(get("/api/admin/devices/stats")
                .param("startDate", "2025-01-01T00:00:00")
                .param("endDate", "2025-01-31T23:59:59")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.total").value(100));
        
        // 验证服务调用
        verify(statsService, times(1)).getManagedDeviceStats(any(LocalDateTime.class), any(LocalDateTime.class));
    }
    
    @Test
    void testGetRealTimeStats_Success() throws Exception {
        // 准备测试数据
        when(statsService.getRealTimeStats()).thenReturn(testStats);
        
        // 执行测试
        mockMvc.perform(get("/api/admin/devices/stats/realtime")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.total").value(100))
                .andExpect(jsonPath("$.data.online").value(80));
        
        // 验证服务调用
        verify(statsService, times(1)).getRealTimeStats();
    }
    
    @Test
    void testGetStatusDistribution_Success() throws Exception {
        // 准备测试数据
        Map<String, Integer> distribution = new HashMap<>();
        distribution.put("online", 80);
        distribution.put("offline", 15);
        distribution.put("error", 3);
        distribution.put("maintenance", 2);
        
        when(statsService.getStatusDistribution()).thenReturn(distribution);
        
        // 执行测试
        mockMvc.perform(get("/api/admin/devices/stats/status-distribution")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.online").value(80))
                .andExpect(jsonPath("$.data.offline").value(15))
                .andExpect(jsonPath("$.data.error").value(3))
                .andExpect(jsonPath("$.data.maintenance").value(2));
        
        // 验证服务调用
        verify(statsService, times(1)).getStatusDistribution();
    }
    
    @Test
    void testGetModelDistribution_Success() throws Exception {
        // 准备测试数据
        Map<String, Integer> distribution = new HashMap<>();
        distribution.put("YX-EDU-2024", 40);
        distribution.put("YX-HOME-2024", 35);
        distribution.put("YX-PRO-2024", 25);
        
        when(statsService.getModelDistribution()).thenReturn(distribution);
        
        // 执行测试
        mockMvc.perform(get("/api/admin/devices/stats/model-distribution")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data['YX-EDU-2024']").value(40))
                .andExpect(jsonPath("$.data['YX-HOME-2024']").value(35))
                .andExpect(jsonPath("$.data['YX-PRO-2024']").value(25));
        
        // 验证服务调用
        verify(statsService, times(1)).getModelDistribution();
    }
    
    @Test
    void testGetStatsSummary_Success() throws Exception {
        // 准备测试数据
        String summary = "设备统计: 总数=100, 在线=80, 离线=15, 故障=3, 维护=2";
        when(statsService.getStatsSummary()).thenReturn(summary);
        
        // 执行测试
        mockMvc.perform(get("/api/admin/devices/stats/summary")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.summary").value(summary))
                .andExpect(jsonPath("$.data.timestamp").exists());
        
        // 验证服务调用
        verify(statsService, times(1)).getStatsSummary();
    }
    
    @Test
    void testCheckStatsEmpty_NotEmpty() throws Exception {
        // 准备测试数据
        when(statsService.getManagedDeviceStats()).thenReturn(testStats);
        when(statsService.isStatsEmpty(testStats)).thenReturn(false);
        
        // 执行测试
        mockMvc.perform(get("/api/admin/devices/stats/check")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.isEmpty").value(false))
                .andExpect(jsonPath("$.data.totalDevices").value(100))
                .andExpect(jsonPath("$.data.timestamp").exists());
        
        // 验证服务调用
        verify(statsService, times(1)).getManagedDeviceStats();
        verify(statsService, times(1)).isStatsEmpty(testStats);
    }
    
    @Test
    void testCheckStatsEmpty_Empty() throws Exception {
        // 准备测试数据
        ManagedDeviceStatsDTO emptyStats = new ManagedDeviceStatsDTO(0, 0, 0, 0, 0);
        when(statsService.getManagedDeviceStats()).thenReturn(emptyStats);
        when(statsService.isStatsEmpty(emptyStats)).thenReturn(true);
        
        // 执行测试
        mockMvc.perform(get("/api/admin/devices/stats/check")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.isEmpty").value(true))
                .andExpect(jsonPath("$.data.totalDevices").value(0));
        
        // 验证服务调用
        verify(statsService, times(1)).getManagedDeviceStats();
        verify(statsService, times(1)).isStatsEmpty(emptyStats);
    }
    
    @Test
    void testGetStatsTrend_Success() throws Exception {
        // 准备测试数据
        when(statsService.getManagedDeviceStats(any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(testStats);
        
        // 执行测试
        mockMvc.perform(get("/api/admin/devices/stats/trend")
                .param("days", "7")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.period").value("7天"))
                .andExpect(jsonPath("$.data.startDate").exists())
                .andExpect(jsonPath("$.data.endDate").exists())
                .andExpect(jsonPath("$.data.stats").exists())
                .andExpect(jsonPath("$.data.trend").exists())
                .andExpect(jsonPath("$.data.trend.totalTrend").value("+5%"));
        
        // 验证服务调用
        verify(statsService, times(1)).getManagedDeviceStats(any(LocalDateTime.class), any(LocalDateTime.class));
    }
    
    @Test
    void testGetStatsTrend_DefaultDays() throws Exception {
        // 准备测试数据
        when(statsService.getManagedDeviceStats(any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(testStats);
        
        // 执行测试（不传days参数，使用默认值7）
        mockMvc.perform(get("/api/admin/devices/stats/trend")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.period").value("7天"));
        
        // 验证服务调用
        verify(statsService, times(1)).getManagedDeviceStats(any(LocalDateTime.class), any(LocalDateTime.class));
    }
    
    @Test
    void testRefreshStats_Success() throws Exception {
        // 准备测试数据
        when(statsService.getRealTimeStats()).thenReturn(testStats);
        
        // 执行测试
        mockMvc.perform(post("/api/admin/devices/stats/refresh")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("统计数据已刷新"))
                .andExpect(jsonPath("$.data.refreshTime").exists())
                .andExpect(jsonPath("$.data.stats").exists())
                .andExpect(jsonPath("$.data.stats.total").value(100));
        
        // 验证服务调用
        verify(statsService, times(1)).getRealTimeStats();
    }
    
    @Test
    void testGetDeviceStats_ServiceException() throws Exception {
        // 准备测试数据
        when(statsService.getManagedDeviceStats(any(), any()))
            .thenThrow(new RuntimeException("数据库连接失败"));
        
        // 执行测试
        mockMvc.perform(get("/api/admin/devices/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("查询失败: 数据库连接失败"));
        
        // 验证服务调用
        verify(statsService, times(1)).getManagedDeviceStats(any(), any());
    }
    
    @Test
    void testGetRealTimeStats_ServiceException() throws Exception {
        // 准备测试数据
        when(statsService.getRealTimeStats())
            .thenThrow(new RuntimeException("服务不可用"));
        
        // 执行测试
        mockMvc.perform(get("/api/admin/devices/stats/realtime")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("查询失败: 服务不可用"));
        
        // 验证服务调用
        verify(statsService, times(1)).getRealTimeStats();
    }
    
    @Test
    void testGetStatusDistribution_ServiceException() throws Exception {
        // 准备测试数据
        when(statsService.getStatusDistribution())
            .thenThrow(new RuntimeException("查询超时"));
        
        // 执行测试
        mockMvc.perform(get("/api/admin/devices/stats/status-distribution")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("查询失败: 查询超时"));
        
        // 验证服务调用
        verify(statsService, times(1)).getStatusDistribution();
    }
    
    @Test
    void testRefreshStats_ServiceException() throws Exception {
        // 准备测试数据
        when(statsService.getRealTimeStats())
            .thenThrow(new RuntimeException("刷新失败"));
        
        // 执行测试
        mockMvc.perform(post("/api/admin/devices/stats/refresh")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("刷新失败: 刷新失败"));
        
        // 验证服务调用
        verify(statsService, times(1)).getRealTimeStats();
    }
    
    /**
     * 创建测试统计数据
     */
    private ManagedDeviceStatsDTO createTestStats() {
        return new ManagedDeviceStatsDTO(100, 80, 15, 3, 2);
    }
}