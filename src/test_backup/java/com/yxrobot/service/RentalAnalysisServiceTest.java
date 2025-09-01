package com.yxrobot.service;

import com.yxrobot.dto.ChartDataDTO;
import com.yxrobot.dto.RentalTrendDTO;
import com.yxrobot.mapper.RentalRecordMapper;
import com.yxrobot.mapper.RentalDeviceMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 租赁分析服务测试类
 * 
 * @author Kiro
 * @date 2025-01-28
 */
@ExtendWith(MockitoExtension.class)
class RentalAnalysisServiceTest {
    
    @Mock
    private RentalRecordMapper rentalRecordMapper;
    
    @Mock
    private RentalDeviceMapper rentalDeviceMapper;
    
    @InjectMocks
    private RentalAnalysisService rentalAnalysisService;
    
    private List<RentalTrendDTO> mockTrendData;
    private List<Map<String, Object>> mockRegionData;
    private List<Map<String, Object>> mockDeviceModelData;
    private List<Map<String, Object>> mockUtilizationRankingData;
    
    @BeforeEach
    void setUp() {
        // 准备模拟趋势数据
        mockTrendData = new ArrayList<>();
        RentalTrendDTO trend1 = new RentalTrendDTO();
        trend1.setDate("2025-01-20");
        trend1.setRevenue(new BigDecimal("35000"));
        trend1.setOrderCount(45);
        trend1.setDeviceCount(150);
        trend1.setUtilizationRate(new BigDecimal("78.5"));
        mockTrendData.add(trend1);
        
        RentalTrendDTO trend2 = new RentalTrendDTO();
        trend2.setDate("2025-01-21");
        trend2.setRevenue(new BigDecimal("42000"));
        trend2.setOrderCount(52);
        trend2.setDeviceCount(155);
        trend2.setUtilizationRate(new BigDecimal("82.3"));
        mockTrendData.add(trend2);
        
        // 准备模拟地区数据
        mockRegionData = new ArrayList<>();
        Map<String, Object> region1 = new HashMap<>();
        region1.put("region", "华东");
        region1.put("revenue", new BigDecimal("150000"));
        region1.put("orderCount", 120);
        mockRegionData.add(region1);
        
        Map<String, Object> region2 = new HashMap<>();
        region2.put("region", "华北");
        region2.put("revenue", new BigDecimal("120000"));
        region2.put("orderCount", 95);
        mockRegionData.add(region2);
        
        // 准备模拟设备型号数据
        mockDeviceModelData = new ArrayList<>();
        Map<String, Object> model1 = new HashMap<>();
        model1.put("name", "YX-Robot-Pro");
        model1.put("value", 85);
        model1.put("avgUtilization", new BigDecimal("88.5"));
        mockDeviceModelData.add(model1);
        
        Map<String, Object> model2 = new HashMap<>();
        model2.put("name", "YX-Robot-Standard");
        model2.put("value", 120);
        model2.put("avgUtilization", new BigDecimal("75.2"));
        mockDeviceModelData.add(model2);
        
        // 准备模拟利用率排行数据
        mockUtilizationRankingData = new ArrayList<>();
        Map<String, Object> ranking1 = new HashMap<>();
        ranking1.put("name", "YX-0001 (YX-Robot-Pro)");
        ranking1.put("value", new BigDecimal("95.5"));
        mockUtilizationRankingData.add(ranking1);
        
        Map<String, Object> ranking2 = new HashMap<>();
        ranking2.put("name", "YX-0002 (YX-Robot-Standard)");
        ranking2.put("value", new BigDecimal("89.2"));
        mockUtilizationRankingData.add(ranking2);
    }
    
    @Test
    void testGetTrendChartData() {
        // 准备测试数据
        LocalDate startDate = LocalDate.of(2025, 1, 20);
        LocalDate endDate = LocalDate.of(2025, 1, 27);
        String period = "daily";
        
        // 模拟mapper调用
        when(rentalRecordMapper.selectRentalTrends(startDate, endDate, period))
            .thenReturn(mockTrendData);
        
        // 执行测试
        Map<String, Object> result = rentalAnalysisService.getTrendChartData(period, startDate, endDate);
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.containsKey("categories"));
        assertTrue(result.containsKey("series"));
        
        @SuppressWarnings("unchecked")
        List<String> categories = (List<String>) result.get("categories");
        assertEquals(2, categories.size());
        assertEquals("2025-01-20", categories.get(0));
        assertEquals("2025-01-21", categories.get(1));
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> series = (List<Map<String, Object>>) result.get("series");
        assertEquals(3, series.size());
        assertEquals("租赁收入", series.get(0).get("name"));
        assertEquals("订单数量", series.get(1).get("name"));
        assertEquals("设备利用率", series.get(2).get("name"));
        
        // 验证mapper调用
        verify(rentalRecordMapper, times(1)).selectRentalTrends(startDate, endDate, period);
    }
    
    @Test
    void testGetDistributionDataRegion() {
        // 准备测试数据
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);
        String type = "region";
        
        // 模拟mapper调用
        when(rentalRecordMapper.selectRegionDistribution(startDate, endDate))
            .thenReturn(mockRegionData);
        
        // 执行测试
        Map<String, Object> result = rentalAnalysisService.getDistributionData(type, startDate, endDate);
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.containsKey("categories"));
        assertTrue(result.containsKey("series"));
        
        @SuppressWarnings("unchecked")
        List<String> categories = (List<String>) result.get("categories");
        assertEquals(2, categories.size());
        assertEquals("华东", categories.get(0));
        assertEquals("华北", categories.get(1));
        
        // 验证mapper调用
        verify(rentalRecordMapper, times(1)).selectRegionDistribution(startDate, endDate);
    }
    
    @Test
    void testGetDistributionDataDeviceModel() {
        // 准备测试数据
        String type = "device-model";
        
        // 模拟mapper调用
        when(rentalDeviceMapper.selectDeviceModelDistribution())
            .thenReturn(mockDeviceModelData);
        
        // 执行测试
        Map<String, Object> result = rentalAnalysisService.getDistributionData(type, null, null);
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.containsKey("categories"));
        assertTrue(result.containsKey("series"));
        
        @SuppressWarnings("unchecked")
        List<String> categories = (List<String>) result.get("categories");
        assertEquals(2, categories.size());
        assertEquals("YX-Robot-Pro", categories.get(0));
        assertEquals("YX-Robot-Standard", categories.get(1));
        
        // 验证mapper调用
        verify(rentalDeviceMapper, times(1)).selectDeviceModelDistribution();
    }
    
    @Test
    void testGetUtilizationRankingData() {
        // 准备测试数据
        Integer limit = 10;
        
        // 模拟mapper调用
        when(rentalDeviceMapper.selectUtilizationRanking(limit))
            .thenReturn(mockUtilizationRankingData);
        
        // 执行测试
        Map<String, Object> result = rentalAnalysisService.getUtilizationRankingData(limit);
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.containsKey("categories"));
        assertTrue(result.containsKey("series"));
        
        @SuppressWarnings("unchecked")
        List<String> categories = (List<String>) result.get("categories");
        assertEquals(2, categories.size());
        assertEquals("YX-0001 (YX-Robot-Pro)", categories.get(0));
        assertEquals("YX-0002 (YX-Robot-Standard)", categories.get(1));
        
        // 验证mapper调用
        verify(rentalDeviceMapper, times(1)).selectUtilizationRanking(limit);
    }
    
    @Test
    void testGetTrendChartDataDTO() {
        // 准备测试数据
        LocalDate startDate = LocalDate.of(2025, 1, 20);
        LocalDate endDate = LocalDate.of(2025, 1, 27);
        String period = "daily";
        
        // 模拟mapper调用
        when(rentalRecordMapper.selectRentalTrends(startDate, endDate, period))
            .thenReturn(mockTrendData);
        
        // 执行测试
        ChartDataDTO result = rentalAnalysisService.getTrendChartDataDTO(period, startDate, endDate);
        
        // 验证结果
        assertNotNull(result);
        assertEquals("租赁趋势分析", result.getTitle());
        assertEquals("租赁收入和订单数量趋势", result.getSubtitle());
        
        assertNotNull(result.getCategories());
        assertEquals(2, result.getCategories().size());
        assertEquals("2025-01-20", result.getCategories().get(0));
        
        assertNotNull(result.getSeries());
        assertEquals(3, result.getSeries().size());
        assertEquals("租赁收入", result.getSeries().get(0).getName());
        assertEquals("line", result.getSeries().get(0).getType());
        assertEquals(0, result.getSeries().get(0).getYAxisIndex());
        
        // 验证mapper调用
        verify(rentalRecordMapper, times(1)).selectRentalTrends(startDate, endDate, period);
    }
    
    @Test
    void testGetAllChartsData() {
        // 准备测试数据
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);
        String period = "daily";
        
        // 模拟所有mapper调用
        when(rentalRecordMapper.selectRentalTrends(any(), any(), any()))
            .thenReturn(mockTrendData);
        when(rentalRecordMapper.selectRegionDistribution(any(), any()))
            .thenReturn(mockRegionData);
        when(rentalDeviceMapper.selectDeviceModelDistribution())
            .thenReturn(mockDeviceModelData);
        when(rentalDeviceMapper.selectUtilizationRanking(any()))
            .thenReturn(mockUtilizationRankingData);
        
        // 执行测试
        Map<String, Object> result = rentalAnalysisService.getAllChartsData(period, startDate, endDate);
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.containsKey("trendChart"));
        assertTrue(result.containsKey("regionChart"));
        assertTrue(result.containsKey("deviceModelChart"));
        assertTrue(result.containsKey("utilizationRankingChart"));
        
        // 验证所有mapper调用
        verify(rentalRecordMapper, times(1)).selectRentalTrends(any(), any(), any());
        verify(rentalRecordMapper, times(1)).selectRegionDistribution(any(), any());
        verify(rentalDeviceMapper, times(1)).selectDeviceModelDistribution();
        verify(rentalDeviceMapper, times(1)).selectUtilizationRanking(any());
    }
    
    @Test
    void testGetTrendChartDataWithException() {
        // 模拟异常
        when(rentalRecordMapper.selectRentalTrends(any(), any(), any()))
            .thenThrow(new RuntimeException("数据库连接失败"));
        
        // 执行测试
        Map<String, Object> result = rentalAnalysisService.getTrendChartData("daily", null, null);
        
        // 验证结果（应该返回空数据而不是抛出异常）
        assertNotNull(result);
        assertTrue(result.containsKey("categories"));
        assertTrue(result.containsKey("series"));
        
        @SuppressWarnings("unchecked")
        List<String> categories = (List<String>) result.get("categories");
        assertTrue(categories.isEmpty());
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> series = (List<Map<String, Object>>) result.get("series");
        assertTrue(series.isEmpty());
    }
}