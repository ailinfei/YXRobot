package com.yxrobot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yxrobot.dto.OrderStatsDTO;
import com.yxrobot.service.OrderStatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 订单统计控制器测试类
 * 测试订单统计API接口的功能正确性
 * 
 * 测试覆盖：
 * 1. 获取订单统计数据接口测试
 * 2. 按日期范围统计接口测试
 * 3. 状态分布统计接口测试
 * 4. 类型分布统计接口测试
 * 5. KPI指标接口测试
 * 6. 简化统计数据接口测试
 * 7. 健康检查接口测试
 * 8. 异常处理测试
 * 9. 空数据处理测试
 */
@ExtendWith(MockitoExtension.class)
class OrderStatsControllerTest {
    
    @Mock
    private OrderStatsService orderStatsService;
    
    @InjectMocks
    private OrderStatsController orderStatsController;
    
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private OrderStatsDTO mockStats;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderStatsController).build();
        objectMapper = new ObjectMapper();
        
        // 创建模拟统计数据
        mockStats = new OrderStatsDTO();
        mockStats.setTotal(100);
        mockStats.setPending(10);
        mockStats.setConfirmed(15);
        mockStats.setProcessing(20);
        mockStats.setShipped(15);
        mockStats.setDelivered(10);
        mockStats.setCompleted(25);
        mockStats.setCancelled(5);
        mockStats.setTotalRevenue(new BigDecimal("250000.00"));
        mockStats.setAverageOrderValue(new BigDecimal("2500.00"));
        mockStats.setSalesOrders(70);
        mockStats.setRentalOrders(30);
    }
    
    /**
     * 测试获取订单统计数据接口 - 成功情况
     */
    @Test
    void testGetOrderStats_Success() throws Exception {
        // 准备测试数据
        when(orderStatsService.getOrderStats()).thenReturn(mockStats);
        
        // 执行请求并验证结果
        mockMvc.perform(get("/api/admin/orders/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.total").value(100))
                .andExpect(jsonPath("$.data.pending").value(10))
                .andExpect(jsonPath("$.data.completed").value(25))
                .andExpect(jsonPath("$.data.totalRevenue").value(250000.00))
                .andExpect(jsonPath("$.data.salesOrders").value(70))
                .andExpect(jsonPath("$.data.rentalOrders").value(30));
        
        // 验证服务方法被调用
        verify(orderStatsService, times(1)).getOrderStats();
    }
    
    /**
     * 测试按日期范围获取订单统计数据接口 - 成功情况
     */
    @Test
    void testGetOrderStatsByDateRange_Success() throws Exception {
        // 准备测试数据
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        when(orderStatsService.getOrderStatsByDateRange(startDate, endDate)).thenReturn(mockStats);
        
        // 执行请求并验证结果
        mockMvc.perform(get("/api/admin/orders/stats")
                .param("startDate", "2024-01-01")
                .param("endDate", "2024-12-31")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.total").value(100));
        
        // 验证服务方法被调用
        verify(orderStatsService, times(1)).getOrderStatsByDateRange(startDate, endDate);
    }
    
    /**
     * 测试获取订单状态分布统计接口 - 成功情况
     */
    @Test
    void testGetOrderStatusDistribution_Success() throws Exception {
        // 准备测试数据
        Map<String, Integer> distribution = new HashMap<>();
        distribution.put("pending", 10);
        distribution.put("confirmed", 15);
        distribution.put("processing", 20);
        distribution.put("completed", 25);
        distribution.put("cancelled", 5);
        
        when(orderStatsService.getOrderStatusDistribution()).thenReturn(distribution);
        
        // 执行请求并验证结果
        mockMvc.perform(get("/api/admin/orders/stats/status-distribution")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.pending").value(10))
                .andExpect(jsonPath("$.data.completed").value(25));
        
        // 验证服务方法被调用
        verify(orderStatsService, times(1)).getOrderStatusDistribution();
    }
    
    /**
     * 测试获取订单类型分布统计接口 - 成功情况
     */
    @Test
    void testGetOrderTypeDistribution_Success() throws Exception {
        // 准备测试数据
        Map<String, Integer> distribution = new HashMap<>();
        distribution.put("sales", 70);
        distribution.put("rental", 30);
        
        when(orderStatsService.getOrderTypeDistribution()).thenReturn(distribution);
        
        // 执行请求并验证结果
        mockMvc.perform(get("/api/admin/orders/stats/type-distribution")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.sales").value(70))
                .andExpect(jsonPath("$.data.rental").value(30));
        
        // 验证服务方法被调用
        verify(orderStatsService, times(1)).getOrderTypeDistribution();
    }
    
    /**
     * 测试获取订单KPI指标接口 - 成功情况
     */
    @Test
    void testGetOrderKPI_Success() throws Exception {
        // 准备测试数据
        BigDecimal completionRate = new BigDecimal("75.50");
        BigDecimal cancellationRate = new BigDecimal("5.00");
        Integer pendingOrdersCount = 45;
        
        when(orderStatsService.calculateCompletionRate()).thenReturn(completionRate);
        when(orderStatsService.calculateCancellationRate()).thenReturn(cancellationRate);
        when(orderStatsService.getPendingOrdersCount()).thenReturn(pendingOrdersCount);
        
        // 执行请求并验证结果
        mockMvc.perform(get("/api/admin/orders/stats/kpi")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.completionRate").value(75.50))
                .andExpect(jsonPath("$.data.cancellationRate").value(5.00))
                .andExpect(jsonPath("$.data.pendingOrdersCount").value(45));
        
        // 验证服务方法被调用
        verify(orderStatsService, times(1)).calculateCompletionRate();
        verify(orderStatsService, times(1)).calculateCancellationRate();
        verify(orderStatsService, times(1)).getPendingOrdersCount();
    }
    
    /**
     * 测试获取简化统计数据接口 - 成功情况
     */
    @Test
    void testGetOrderStatsSummary_Success() throws Exception {
        // 准备测试数据
        when(orderStatsService.getOrderStats()).thenReturn(mockStats);
        
        // 执行请求并验证结果
        mockMvc.perform(get("/api/admin/orders/stats/summary")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.totalOrders").value(100))
                .andExpect(jsonPath("$.data.totalRevenue").value(250000.00))
                .andExpect(jsonPath("$.data.processingOrders").value(45)) // 10+15+20
                .andExpect(jsonPath("$.data.completedOrders").value(25));
        
        // 验证服务方法被调用
        verify(orderStatsService, times(1)).getOrderStats();
    }
    
    /**
     * 测试健康检查接口 - 成功情况
     */
    @Test
    void testHealthCheck_Success() throws Exception {
        // 准备测试数据
        when(orderStatsService.getOrderStats()).thenReturn(mockStats);
        
        // 执行请求并验证结果
        mockMvc.perform(get("/api/admin/orders/stats/health")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("服务正常"))
                .andExpect(jsonPath("$.data.status").value("healthy"))
                .andExpect(jsonPath("$.data.service").value("OrderStatsService"))
                .andExpect(jsonPath("$.data.dataAvailable").value(true));
        
        // 验证服务方法被调用
        verify(orderStatsService, times(1)).getOrderStats();
    }
    
    /**
     * 测试获取订单统计数据接口 - 异常情况
     */
    @Test
    void testGetOrderStats_Exception() throws Exception {
        // 准备测试数据 - 模拟服务异常
        when(orderStatsService.getOrderStats()).thenThrow(new RuntimeException("数据库连接失败"));
        
        // 执行请求并验证结果
        mockMvc.perform(get("/api/admin/orders/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("获取统计数据失败: 数据库连接失败"))
                .andExpect(jsonPath("$.data.total").value(0));
        
        // 验证服务方法被调用
        verify(orderStatsService, times(1)).getOrderStats();
    }
    
    /**
     * 测试获取订单状态分布统计接口 - 异常情况
     */
    @Test
    void testGetOrderStatusDistribution_Exception() throws Exception {
        // 准备测试数据 - 模拟服务异常
        when(orderStatsService.getOrderStatusDistribution()).thenThrow(new RuntimeException("查询失败"));
        
        // 执行请求并验证结果
        mockMvc.perform(get("/api/admin/orders/stats/status-distribution")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("获取状态分布统计失败: 查询失败"));
        
        // 验证服务方法被调用
        verify(orderStatsService, times(1)).getOrderStatusDistribution();
    }
    
    /**
     * 测试获取订单类型分布统计接口 - 异常情况
     */
    @Test
    void testGetOrderTypeDistribution_Exception() throws Exception {
        // 准备测试数据 - 模拟服务异常
        when(orderStatsService.getOrderTypeDistribution()).thenThrow(new RuntimeException("查询失败"));
        
        // 执行请求并验证结果
        mockMvc.perform(get("/api/admin/orders/stats/type-distribution")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("获取类型分布统计失败: 查询失败"));
        
        // 验证服务方法被调用
        verify(orderStatsService, times(1)).getOrderTypeDistribution();
    }
    
    /**
     * 测试获取KPI指标接口 - 异常情况
     */
    @Test
    void testGetOrderKPI_Exception() throws Exception {
        // 准备测试数据 - 模拟服务异常
        when(orderStatsService.calculateCompletionRate()).thenThrow(new RuntimeException("计算失败"));
        
        // 执行请求并验证结果
        mockMvc.perform(get("/api/admin/orders/stats/kpi")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("获取KPI指标失败: 计算失败"));
        
        // 验证服务方法被调用
        verify(orderStatsService, times(1)).calculateCompletionRate();
    }
    
    /**
     * 测试获取简化统计数据接口 - 异常情况
     */
    @Test
    void testGetOrderStatsSummary_Exception() throws Exception {
        // 准备测试数据 - 模拟服务异常
        when(orderStatsService.getOrderStats()).thenThrow(new RuntimeException("服务不可用"));
        
        // 执行请求并验证结果
        mockMvc.perform(get("/api/admin/orders/stats/summary")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("获取简化统计数据失败: 服务不可用"))
                .andExpect(jsonPath("$.data.totalOrders").value(0))
                .andExpect(jsonPath("$.data.totalRevenue").value(0))
                .andExpect(jsonPath("$.data.processingOrders").value(0))
                .andExpect(jsonPath("$.data.completedOrders").value(0));
        
        // 验证服务方法被调用
        verify(orderStatsService, times(1)).getOrderStats();
    }
    
    /**
     * 测试健康检查接口 - 异常情况
     */
    @Test
    void testHealthCheck_Exception() throws Exception {
        // 准备测试数据 - 模拟服务异常
        when(orderStatsService.getOrderStats()).thenThrow(new RuntimeException("服务异常"));
        
        // 执行请求并验证结果
        mockMvc.perform(get("/api/admin/orders/stats/health")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("服务异常"))
                .andExpect(jsonPath("$.data.status").value("unhealthy"))
                .andExpect(jsonPath("$.data.service").value("OrderStatsService"))
                .andExpect(jsonPath("$.data.error").value("服务异常"));
        
        // 验证服务方法被调用
        verify(orderStatsService, times(1)).getOrderStats();
    }
    
    /**
     * 测试空数据情况处理
     */
    @Test
    void testGetOrderStats_EmptyData() throws Exception {
        // 准备测试数据 - 空统计数据
        OrderStatsDTO emptyStats = new OrderStatsDTO();
        when(orderStatsService.getOrderStats()).thenReturn(emptyStats);
        
        // 执行请求并验证结果
        mockMvc.perform(get("/api/admin/orders/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.total").value(0))
                .andExpect(jsonPath("$.data.totalRevenue").value(0))
                .andExpect(jsonPath("$.data.completed").value(0));
        
        // 验证服务方法被调用
        verify(orderStatsService, times(1)).getOrderStats();
    }
    
    /**
     * 测试日期参数格式验证
     */
    @Test
    void testGetOrderStats_InvalidDateFormat() throws Exception {
        // 执行请求并验证结果 - 无效日期格式
        mockMvc.perform(get("/api/admin/orders/stats")
                .param("startDate", "invalid-date")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        
        // 验证服务方法未被调用
        verify(orderStatsService, never()).getOrderStats();
        verify(orderStatsService, never()).getOrderStatsByDateRange(any(), any());
    }
}