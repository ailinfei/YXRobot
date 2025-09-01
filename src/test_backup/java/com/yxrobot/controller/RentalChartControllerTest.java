package com.yxrobot.controller;

import com.yxrobot.service.RentalAnalysisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 租赁图表控制器测试类
 * 
 * @author Kiro
 * @date 2025-01-28
 */
@ExtendWith(MockitoExtension.class)
class RentalChartControllerTest {
    
    @Mock
    private RentalAnalysisService rentalAnalysisService;
    
    @InjectMocks
    private RentalController rentalController;
    
    private Map<String, Object> mockTrendChartData;
    private Map<String, Object> mockDistributionData;
    private Map<String, Object> mockUtilizationRankingData;
    private Map<String, Object> mockAllChartsData;
    
    @BeforeEach
    void setUp() {
        // 准备模拟趋势图表数据
        mockTrendChartData = new HashMap<>();
        mockTrendChartData.put("categories", List.of("2025-01-20", "2025-01-21", "2025-01-22"));
        mockTrendChartData.put("series", List.of(
            Map.of("name", "租赁收入", "data", List.of(35000, 42000, 38000)),
            Map.of("name", "订单数量", "data", List.of(45, 52, 48)),
            Map.of("name", "设备利用率", "data", List.of(78.5, 82.3, 80.1))
        ));
        
        // 准备模拟分布数据
        mockDistributionData = new HashMap<>();
        mockDistributionData.put("categories", List.of("华东", "华北", "华南"));
        mockDistributionData.put("series", List.of(
            Map.of("name", "租赁收入", "data", List.of(150000, 120000, 95000))
        ));
        
        // 准备模拟利用率排行数据
        mockUtilizationRankingData = new HashMap<>();
        mockUtilizationRankingData.put("categories", List.of("YX-0001", "YX-0002", "YX-0003"));
        mockUtilizationRankingData.put("series", List.of(
            Map.of("name", "利用率", "data", List.of(95.5, 89.2, 85.7))
        ));
        
        // 准备模拟所有图表数据
        mockAllChartsData = new HashMap<>();
        mockAllChartsData.put("trendChart", mockTrendChartData);
        mockAllChartsData.put("regionChart", mockDistributionData);
        mockAllChartsData.put("deviceModelChart", mockDistributionData);
        mockAllChartsData.put("utilizationRankingChart", mockUtilizationRankingData);
    }
    
    @Test
    void testGetTrendChartData() {
        // 准备测试数据
        when(rentalAnalysisService.getTrendChartData(eq("daily"), any(), any()))
            .thenReturn(mockTrendChartData);
        
        // 执行测试
        ResponseEntity<Map<String, Object>> response = rentalController.getTrendChartData(
            "daily", "2025-01-20", "2025-01-27");
        
        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.get("code"));
        assertEquals("查询成功", responseBody.get("message"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
        assertNotNull(data);
        assertTrue(data.containsKey("categories"));
        assertTrue(data.containsKey("series"));
        
        @SuppressWarnings("unchecked")
        List<String> categories = (List<String>) data.get("categories");
        assertEquals(3, categories.size());
        assertEquals("2025-01-20", categories.get(0));
        
        // 验证服务调用
        verify(rentalAnalysisService, times(1)).getTrendChartData(eq("daily"), any(), any());
    }
    
    @Test
    void testGetTrendChartDataWithDefaultParams() {
        // 准备测试数据
        when(rentalAnalysisService.getTrendChartData(eq("daily"), isNull(), isNull()))
            .thenReturn(mockTrendChartData);
        
        // 执行测试（使用默认参数）
        ResponseEntity<Map<String, Object>> response = rentalController.getTrendChartData(
            null, null, null);
        
        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.get("code"));
        
        // 验证服务调用（默认period为daily）
        verify(rentalAnalysisService, times(1)).getTrendChartData(eq("daily"), isNull(), isNull());
    }
    
    @Test
    void testGetDistributionData() {
        // 准备测试数据
        when(rentalAnalysisService.getDistributionData(eq("region"), any(), any()))
            .thenReturn(mockDistributionData);
        
        // 执行测试
        ResponseEntity<Map<String, Object>> response = rentalController.getDistributionData(
            "region", "2025-01-01", "2025-01-31");
        
        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.get("code"));
        assertEquals("查询成功", responseBody.get("message"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
        assertNotNull(data);
        assertTrue(data.containsKey("categories"));
        assertTrue(data.containsKey("series"));
        
        @SuppressWarnings("unchecked")
        List<String> categories = (List<String>) data.get("categories");
        assertEquals(3, categories.size());
        assertEquals("华东", categories.get(0));
        
        // 验证服务调用
        verify(rentalAnalysisService, times(1)).getDistributionData(eq("region"), any(), any());
    }
    
    @Test
    void testGetDistributionDataDeviceModel() {
        // 准备测试数据
        when(rentalAnalysisService.getDistributionData(eq("device-model"), isNull(), isNull()))
            .thenReturn(mockDistributionData);
        
        // 执行测试
        ResponseEntity<Map<String, Object>> response = rentalController.getDistributionData(
            "device-model", null, null);
        
        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.get("code"));
        
        // 验证服务调用
        verify(rentalAnalysisService, times(1)).getDistributionData(eq("device-model"), isNull(), isNull());
    }
    
    @Test
    void testGetDistributionDataWithDefaultType() {
        // 准备测试数据
        when(rentalAnalysisService.getDistributionData(eq("region"), isNull(), isNull()))
            .thenReturn(mockDistributionData);
        
        // 执行测试（使用默认type）
        ResponseEntity<Map<String, Object>> response = rentalController.getDistributionData(
            null, null, null);
        
        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        // 验证服务调用（默认type为region）
        verify(rentalAnalysisService, times(1)).getDistributionData(eq("region"), isNull(), isNull());
    }
    
    @Test
    void testGetUtilizationRankingData() {
        // 准备测试数据
        when(rentalAnalysisService.getUtilizationRankingData(12))
            .thenReturn(mockUtilizationRankingData);
        
        // 执行测试
        ResponseEntity<Map<String, Object>> response = rentalController.getUtilizationRankingData(12);
        
        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.get("code"));
        assertEquals("查询成功", responseBody.get("message"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
        assertNotNull(data);
        assertTrue(data.containsKey("categories"));
        assertTrue(data.containsKey("series"));
        
        @SuppressWarnings("unchecked")
        List<String> categories = (List<String>) data.get("categories");
        assertEquals(3, categories.size());
        assertEquals("YX-0001", categories.get(0));
        
        // 验证服务调用
        verify(rentalAnalysisService, times(1)).getUtilizationRankingData(12);
    }
    
    @Test
    void testGetUtilizationRankingDataWithDefaultLimit() {
        // 准备测试数据
        when(rentalAnalysisService.getUtilizationRankingData(12))
            .thenReturn(mockUtilizationRankingData);
        
        // 执行测试（使用默认limit）
        ResponseEntity<Map<String, Object>> response = rentalController.getUtilizationRankingData(null);
        
        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        // 验证服务调用（默认limit为12）
        verify(rentalAnalysisService, times(1)).getUtilizationRankingData(12);
    }
    
    @Test
    void testGetAllChartsData() {
        // 准备测试数据
        when(rentalAnalysisService.getAllChartsData(eq("daily"), any(), any()))
            .thenReturn(mockAllChartsData);
        
        // 执行测试
        ResponseEntity<Map<String, Object>> response = rentalController.getAllChartsData(
            "daily", "2025-01-01", "2025-01-31");
        
        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.get("code"));
        assertEquals("查询成功", responseBody.get("message"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
        assertNotNull(data);
        assertTrue(data.containsKey("trendChart"));
        assertTrue(data.containsKey("regionChart"));
        assertTrue(data.containsKey("deviceModelChart"));
        assertTrue(data.containsKey("utilizationRankingChart"));
        
        // 验证服务调用
        verify(rentalAnalysisService, times(1)).getAllChartsData(eq("daily"), any(), any());
    }
    
    @Test
    void testGetTrendChartDataWithException() {
        // 模拟异常
        when(rentalAnalysisService.getTrendChartData(any(), any(), any()))
            .thenThrow(new RuntimeException("数据库连接失败"));
        
        // 执行测试
        ResponseEntity<Map<String, Object>> response = rentalController.getTrendChartData(
            "daily", "2025-01-01", "2025-01-31");
        
        // 验证结果（应该返回错误响应而不是抛出异常）
        assertNotNull(response);
        assertEquals(500, response.getStatusCodeValue());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(500, responseBody.get("code"));
        assertTrue(responseBody.get("message").toString().contains("查询失败"));
        assertNull(responseBody.get("data"));
    }
    
    @Test
    void testGetDistributionDataWithException() {
        // 模拟异常
        when(rentalAnalysisService.getDistributionData(any(), any(), any()))
            .thenThrow(new RuntimeException("服务不可用"));
        
        // 执行测试
        ResponseEntity<Map<String, Object>> response = rentalController.getDistributionData(
            "region", null, null);
        
        // 验证结果（应该返回错误响应而不是抛出异常）
        assertNotNull(response);
        assertEquals(500, response.getStatusCodeValue());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(500, responseBody.get("code"));
        assertTrue(responseBody.get("message").toString().contains("查询失败"));
        assertNull(responseBody.get("data"));
    }
    
    @Test
    void testGetAllChartsDataWithDefaultParams() {
        // 准备测试数据
        when(rentalAnalysisService.getAllChartsData(eq("daily"), isNull(), isNull()))
            .thenReturn(mockAllChartsData);
        
        // 执行测试（使用默认参数）
        ResponseEntity<Map<String, Object>> response = rentalController.getAllChartsData(
            null, null, null);
        
        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.get("code"));
        
        // 验证服务调用（默认period为daily）
        verify(rentalAnalysisService, times(1)).getAllChartsData(eq("daily"), isNull(), isNull());
    }
}