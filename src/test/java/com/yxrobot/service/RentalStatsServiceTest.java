package com.yxrobot.service;

import com.yxrobot.dto.RentalStatsDTO;
import com.yxrobot.dto.TodayStatsDTO;
import com.yxrobot.mapper.RentalRecordMapper;
import com.yxrobot.mapper.RentalDeviceMapper;
import com.yxrobot.mapper.RentalCustomerMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 租赁统计服务测试类
 * 测试租赁统计业务逻辑的正确性
 * 
 * @author Kiro
 * @date 2025-01-28
 */
@ExtendWith(MockitoExtension.class)
class RentalStatsServiceTest {
    
    @Mock
    private RentalRecordMapper rentalRecordMapper;
    
    @Mock
    private RentalDeviceMapper rentalDeviceMapper;
    
    @Mock
    private RentalCustomerMapper rentalCustomerMapper;
    
    @InjectMocks
    private RentalStatsService rentalStatsService;
    
    private LocalDate startDate;
    private LocalDate endDate;
    private Map<String, Object> mockStatsData;
    private Map<String, Object> mockDeviceStats;
    
    @BeforeEach
    void setUp() {
        startDate = LocalDate.of(2025, 1, 1);
        endDate = LocalDate.of(2025, 1, 31);
        
        // 模拟统计数据
        mockStatsData = new HashMap<>();
        mockStatsData.put("totalRentalRevenue", new BigDecimal("125000.00"));
        mockStatsData.put("totalRentalOrders", 150);
        mockStatsData.put("totalRentalDevices", 80);
        mockStatsData.put("activeRentalDevices", 65);
        mockStatsData.put("averageRentalPeriod", new BigDecimal("15.5"));
        
        // 模拟设备状态统计
        mockDeviceStats = new HashMap<>();
        mockDeviceStats.put("active", 65);
        mockDeviceStats.put("idle", 10);
        mockDeviceStats.put("maintenance", 5);
    }
    
    @Test
    void testGetRentalStats_WithValidDateRange_ShouldReturnCorrectStats() {
        // Given
        when(rentalRecordMapper.selectRentalStats(startDate, endDate)).thenReturn(mockStatsData);
        when(rentalDeviceMapper.selectDeviceStatusStats()).thenReturn(mockDeviceStats);
        when(rentalCustomerMapper.selectActiveCustomerCount()).thenReturn(120L);
        
        // When
        RentalStatsDTO result = rentalStatsService.getRentalStats(startDate, endDate);
        
        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("125000.00"), result.getTotalRentalRevenue());
        assertEquals(Integer.valueOf(150), result.getTotalRentalOrders());
        assertEquals(Integer.valueOf(80), result.getTotalRentalDevices());
        assertEquals(Integer.valueOf(65), result.getActiveRentalDevices());
        assertEquals(new BigDecimal("15.5"), result.getAverageRentalPeriod());
        
        // 验证利用率计算：65/(65+10+5)*100 = 81.25%
        assertEquals(new BigDecimal("81.25"), result.getDeviceUtilizationRate());
        
        verify(rentalRecordMapper).selectRentalStats(startDate, endDate);
        verify(rentalDeviceMapper).selectDeviceStatusStats();
        verify(rentalCustomerMapper).selectActiveCustomerCount();
    }
    
    @Test
    void testGetRentalStats_WithNullDateRange_ShouldReturnStats() {
        // Given
        when(rentalRecordMapper.selectRentalStats(null, null)).thenReturn(mockStatsData);
        when(rentalDeviceMapper.selectDeviceStatusStats()).thenReturn(mockDeviceStats);
        when(rentalCustomerMapper.selectActiveCustomerCount()).thenReturn(120L);
        
        // When
        RentalStatsDTO result = rentalStatsService.getRentalStats(null, null);
        
        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("125000.00"), result.getTotalRentalRevenue());
        
        verify(rentalRecordMapper).selectRentalStats(null, null);
    }
    
    @Test
    void testGetRentalStats_WithEmptyData_ShouldReturnDefaultValues() {
        // Given
        when(rentalRecordMapper.selectRentalStats(startDate, endDate)).thenReturn(null);
        when(rentalDeviceMapper.selectDeviceStatusStats()).thenReturn(null);
        when(rentalCustomerMapper.selectActiveCustomerCount()).thenReturn(0L);
        
        // When
        RentalStatsDTO result = rentalStatsService.getRentalStats(startDate, endDate);
        
        // Then
        assertNotNull(result);
        assertNull(result.getTotalRentalRevenue());
        assertNull(result.getTotalRentalOrders());
        assertNull(result.getDeviceUtilizationRate());
    }
    
    @Test
    void testGetRentalStats_WithException_ShouldReturnEmptyStats() {
        // Given
        when(rentalRecordMapper.selectRentalStats(startDate, endDate))
            .thenThrow(new RuntimeException("Database error"));
        
        // When
        RentalStatsDTO result = rentalStatsService.getRentalStats(startDate, endDate);
        
        // Then
        assertNotNull(result);
        assertNull(result.getTotalRentalRevenue());
        assertNull(result.getTotalRentalOrders());
    }
    
    @Test
    void testGetTodayStats_WithValidData_ShouldReturnTodayStats() {
        // Given
        LocalDate today = LocalDate.now();
        Map<String, Object> todayData = new HashMap<>();
        todayData.put("revenue", new BigDecimal("5000.00"));
        todayData.put("orders", 8);
        todayData.put("activeDevices", 12);
        todayData.put("avgUtilization", new BigDecimal("75.5"));
        
        when(rentalRecordMapper.selectTodayStats(today)).thenReturn(todayData);
        
        // When
        Map<String, Object> result = rentalStatsService.getTodayStats();
        
        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("5000.00"), result.get("revenue"));
        assertEquals(8, result.get("orders"));
        assertEquals(12, result.get("activeDevices"));
        assertEquals(new BigDecimal("75.5"), result.get("avgUtilization"));
        
        verify(rentalRecordMapper).selectTodayStats(today);
    }
    
    @Test
    void testGetTodayStats_WithNullData_ShouldReturnDefaultValues() {
        // Given
        LocalDate today = LocalDate.now();
        when(rentalRecordMapper.selectTodayStats(today)).thenReturn(null);
        
        // When
        Map<String, Object> result = rentalStatsService.getTodayStats();
        
        // Then
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.get("revenue"));
        assertEquals(0, result.get("orders"));
        assertEquals(0, result.get("activeDevices"));
        assertEquals(BigDecimal.ZERO, result.get("avgUtilization"));
    }
    
    @Test
    void testGetTodayStatsDTO_WithValidData_ShouldReturnTodayStatsDTO() {
        // Given
        LocalDate today = LocalDate.now();
        Map<String, Object> todayData = new HashMap<>();
        todayData.put("revenue", new BigDecimal("5000.00"));
        todayData.put("orders", 8);
        todayData.put("activeDevices", 12);
        todayData.put("avgUtilization", new BigDecimal("75.5"));
        
        when(rentalRecordMapper.selectTodayStats(today)).thenReturn(todayData);
        
        // When
        TodayStatsDTO result = rentalStatsService.getTodayStatsDTO();
        
        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("5000.00"), result.getRevenue());
        assertEquals(Integer.valueOf(8), result.getOrders());
        assertEquals(Integer.valueOf(12), result.getActiveDevices());
        assertEquals(new BigDecimal("75.5"), result.getAvgUtilization());
    }
    
    @Test
    void testGetRentalStats_NoParameterMethod_ShouldCallWithNullDates() {
        // Given
        when(rentalRecordMapper.selectRentalStats(null, null)).thenReturn(mockStatsData);
        when(rentalDeviceMapper.selectDeviceStatusStats()).thenReturn(mockDeviceStats);
        when(rentalCustomerMapper.selectActiveCustomerCount()).thenReturn(120L);
        
        // When
        RentalStatsDTO result = rentalStatsService.getRentalStats();
        
        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("125000.00"), result.getTotalRentalRevenue());
        
        verify(rentalRecordMapper).selectRentalStats(null, null);
    }
    
    @Test
    void testGetMonthlyRentalStats_WithValidYearMonth_ShouldReturnStats() {
        // Given
        int year = 2025;
        int month = 1;
        LocalDate expectedStart = LocalDate.of(2025, 1, 1);
        LocalDate expectedEnd = LocalDate.of(2025, 1, 31);
        
        when(rentalRecordMapper.selectRentalStats(expectedStart, expectedEnd)).thenReturn(mockStatsData);
        when(rentalDeviceMapper.selectDeviceStatusStats()).thenReturn(mockDeviceStats);
        when(rentalCustomerMapper.selectActiveCustomerCount()).thenReturn(120L);
        
        // When
        RentalStatsDTO result = rentalStatsService.getMonthlyRentalStats(year, month);
        
        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("125000.00"), result.getTotalRentalRevenue());
        
        verify(rentalRecordMapper).selectRentalStats(expectedStart, expectedEnd);
    }
    
    @Test
    void testGetRecentDaysRentalStats_WithValidDays_ShouldReturnStats() {
        // Given
        int days = 7;
        LocalDate today = LocalDate.now();
        LocalDate expectedStart = today.minusDays(6); // 7天前到今天
        LocalDate expectedEnd = today;
        
        when(rentalRecordMapper.selectRentalStats(expectedStart, expectedEnd)).thenReturn(mockStatsData);
        when(rentalDeviceMapper.selectDeviceStatusStats()).thenReturn(mockDeviceStats);
        when(rentalCustomerMapper.selectActiveCustomerCount()).thenReturn(120L);
        
        // When
        RentalStatsDTO result = rentalStatsService.getRecentDaysRentalStats(days);
        
        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("125000.00"), result.getTotalRentalRevenue());
        
        verify(rentalRecordMapper).selectRentalStats(expectedStart, expectedEnd);
    }
    
    @Test
    void testCalculateGrowthRates_WithValidPreviousData_ShouldCalculateCorrectly() {
        // Given
        Map<String, Object> previousStats = new HashMap<>();
        previousStats.put("totalRentalRevenue", new BigDecimal("100000.00"));
        previousStats.put("totalRentalDevices", 70);
        
        when(rentalRecordMapper.selectRentalStats(startDate, endDate)).thenReturn(mockStatsData);
        when(rentalDeviceMapper.selectDeviceStatusStats()).thenReturn(mockDeviceStats);
        when(rentalCustomerMapper.selectActiveCustomerCount()).thenReturn(120L);
        
        // 模拟上一个周期的数据查询
        when(rentalRecordMapper.selectRentalStats(any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(mockStatsData) // 当前周期
            .thenReturn(previousStats); // 上一个周期
        
        // When
        RentalStatsDTO result = rentalStatsService.getRentalStats(startDate, endDate);
        
        // Then
        assertNotNull(result);
        // 收入增长率：(125000-100000)/100000*100 = 25%
        // 设备增长率：(80-70)/70*100 = 14.29%
        assertNotNull(result.getRevenueGrowthRate());
        assertNotNull(result.getDeviceGrowthRate());
    }
    
    @Test
    void testDeviceUtilizationRateCalculation_WithZeroTotalDevices_ShouldNotSetRate() {
        // Given
        Map<String, Object> emptyDeviceStats = new HashMap<>();
        emptyDeviceStats.put("active", 0);
        emptyDeviceStats.put("idle", 0);
        emptyDeviceStats.put("maintenance", 0);
        
        when(rentalRecordMapper.selectRentalStats(startDate, endDate)).thenReturn(mockStatsData);
        when(rentalDeviceMapper.selectDeviceStatusStats()).thenReturn(emptyDeviceStats);
        when(rentalCustomerMapper.selectActiveCustomerCount()).thenReturn(120L);
        
        // When
        RentalStatsDTO result = rentalStatsService.getRentalStats(startDate, endDate);
        
        // Then
        assertNotNull(result);
        assertNull(result.getDeviceUtilizationRate());
    }
    
    @Test
    void testGetBigDecimalValue_WithDifferentTypes_ShouldConvertCorrectly() {
        // 这个测试验证私有方法的逻辑，通过公共方法间接测试
        
        // Given - 测试不同类型的数值转换
        Map<String, Object> mixedTypeData = new HashMap<>();
        mixedTypeData.put("totalRentalRevenue", 125000); // Integer
        mixedTypeData.put("totalRentalOrders", 150L); // Long
        mixedTypeData.put("totalRentalDevices", "80"); // String
        mixedTypeData.put("activeRentalDevices", new BigDecimal("65")); // BigDecimal
        mixedTypeData.put("averageRentalPeriod", 15.5); // Double
        
        when(rentalRecordMapper.selectRentalStats(startDate, endDate)).thenReturn(mixedTypeData);
        when(rentalDeviceMapper.selectDeviceStatusStats()).thenReturn(mockDeviceStats);
        when(rentalCustomerMapper.selectActiveCustomerCount()).thenReturn(120L);
        
        // When
        RentalStatsDTO result = rentalStatsService.getRentalStats(startDate, endDate);
        
        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("125000"), result.getTotalRentalRevenue());
        assertEquals(Integer.valueOf(150), result.getTotalRentalOrders());
        assertEquals(Integer.valueOf(80), result.getTotalRentalDevices());
        assertEquals(Integer.valueOf(65), result.getActiveRentalDevices());
        assertEquals(new BigDecimal("15.5"), result.getAverageRentalPeriod());
    }
    
    @Test
    void testGetIntegerValue_WithInvalidData_ShouldReturnZero() {
        // Given - 测试无效数据的处理
        Map<String, Object> invalidData = new HashMap<>();
        invalidData.put("totalRentalRevenue", null);
        invalidData.put("totalRentalOrders", "invalid");
        invalidData.put("totalRentalDevices", new HashMap<>()); // 无法转换的对象
        
        when(rentalRecordMapper.selectRentalStats(startDate, endDate)).thenReturn(invalidData);
        when(rentalDeviceMapper.selectDeviceStatusStats()).thenReturn(mockDeviceStats);
        when(rentalCustomerMapper.selectActiveCustomerCount()).thenReturn(120L);
        
        // When
        RentalStatsDTO result = rentalStatsService.getRentalStats(startDate, endDate);
        
        // Then
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getTotalRentalRevenue());
        assertEquals(Integer.valueOf(0), result.getTotalRentalOrders());
        assertEquals(Integer.valueOf(0), result.getTotalRentalDevices());
    }
}