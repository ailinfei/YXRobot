package com.yxrobot.service;

import com.yxrobot.dto.RentalStatsDTO;
import com.yxrobot.dto.DeviceUtilizationDTO;
import com.yxrobot.entity.RentalCustomer;
import com.yxrobot.entity.RentalDevice;
import com.yxrobot.mapper.RentalRecordMapper;
import com.yxrobot.mapper.RentalDeviceMapper;
import com.yxrobot.mapper.RentalCustomerMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 租赁服务业务逻辑测试类
 * 测试任务16 - 业务逻辑功能
 * 
 * @author Kiro
 * @date 2025-01-28
 */
public class RentalServiceTest {
    
    @Mock
    private RentalRecordMapper rentalRecordMapper;
    
    @Mock
    private RentalDeviceMapper rentalDeviceMapper;
    
    @Mock
    private RentalCustomerMapper rentalCustomerMapper;
    
    @InjectMocks
    private RentalStatsService rentalStatsService;
    
    @InjectMocks
    private DeviceUtilizationService deviceUtilizationService;
    
    @InjectMocks
    private RentalCustomerService rentalCustomerService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    /**
     * 测试租赁统计服务 - 正常情况
     */
    @Test
    void testGetRentalStats_Success() {
        // 准备测试数据
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);
        
        Map<String, Object> mockStatsData = new HashMap<>();
        mockStatsData.put("totalRentalRevenue", new BigDecimal("125000.00"));
        mockStatsData.put("totalRentalOrders", 150);
        mockStatsData.put("totalRentalDevices", 50);
        mockStatsData.put("activeRentalDevices", 35);
        mockStatsData.put("averageRentalPeriod", new BigDecimal("15.5"));
        
        Map<String, Object> mockDeviceStats = new HashMap<>();
        mockDeviceStats.put("active", 35);
        mockDeviceStats.put("idle", 10);
        mockDeviceStats.put("maintenance", 5);
        
        // 模拟Mapper方法
        when(rentalRecordMapper.selectRentalStats(startDate, endDate))
            .thenReturn(mockStatsData);
        when(rentalDeviceMapper.selectDeviceStatusStats())
            .thenReturn(mockDeviceStats);
        when(rentalCustomerMapper.selectActiveCustomerCount())
            .thenReturn(25L);
        
        // 执行测试
        RentalStatsDTO result = rentalStatsService.getRentalStats(startDate, endDate);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(new BigDecimal("125000.00"), result.getTotalRentalRevenue());
        assertEquals(150, result.getTotalRentalOrders());
        assertEquals(50, result.getTotalRentalDevices());
        assertEquals(35, result.getActiveRentalDevices());
        assertEquals(new BigDecimal("15.5"), result.getAverageRentalPeriod());
        
        // 验证利用率计算
        assertEquals(new BigDecimal("70.00"), result.getDeviceUtilizationRate());
        
        // 验证Mapper方法被调用
        verify(rentalRecordMapper, times(1)).selectRentalStats(startDate, endDate);
        verify(rentalDeviceMapper, times(1)).selectDeviceStatusStats();
        verify(rentalCustomerMapper, times(1)).selectActiveCustomerCount();
    }
    
    /**
     * 测试租赁统计服务 - 空数据情况
     */
    @Test
    void testGetRentalStats_EmptyData() {
        // 模拟空数据
        when(rentalRecordMapper.selectRentalStats(any(), any()))
            .thenReturn(null);
        when(rentalDeviceMapper.selectDeviceStatusStats())
            .thenReturn(null);
        when(rentalCustomerMapper.selectActiveCustomerCount())
            .thenReturn(0L);
        
        // 执行测试
        RentalStatsDTO result = rentalStatsService.getRentalStats(null, null);
        
        // 验证结果 - 应该返回默认值而不是null
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getTotalRentalRevenue());
        assertEquals(0, result.getTotalRentalOrders());
        assertEquals(0, result.getTotalRentalDevices());
        assertEquals(0, result.getActiveRentalDevices());
        assertEquals(BigDecimal.ZERO, result.getAverageRentalPeriod());
    }
    
    /**
     * 测试设备利用率服务 - 正常情况
     */
    @Test
    void testGetDeviceUtilizationData_Success() {
        // 准备测试数据
        Map<String, Object> params = new HashMap<>();
        params.put("page", 1);
        params.put("pageSize", 20);
        params.put("keyword", "YX-0001");
        
        List<DeviceUtilizationDTO> mockDeviceList = Arrays.asList(
            createMockDeviceUtilization("YX-0001", "YX-Robot-Pro", 85.5),
            createMockDeviceUtilization("YX-0002", "YX-Robot-Standard", 72.3)
        );
        
        // 模拟Mapper方法
        when(rentalDeviceMapper.selectDeviceUtilizationList(params))
            .thenReturn(mockDeviceList);
        when(rentalDeviceMapper.selectDeviceUtilizationCount(params))
            .thenReturn(2L);
        
        // 执行测试
        List<DeviceUtilizationDTO> result = deviceUtilizationService.getDeviceUtilizationData(params);
        Long count = deviceUtilizationService.getDeviceUtilizationCount(params);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("YX-0001", result.get(0).getDeviceId());
        assertEquals("YX-Robot-Pro", result.get(0).getDeviceModel());
        assertEquals(new BigDecimal("85.5"), result.get(0).getUtilizationRate());
        assertEquals(2L, count);
        
        // 验证Mapper方法被调用
        verify(rentalDeviceMapper, times(1)).selectDeviceUtilizationList(params);
        verify(rentalDeviceMapper, times(1)).selectDeviceUtilizationCount(params);
    }
    
    /**
     * 测试设备利用率服务 - 异常情况
     */
    @Test
    void testGetDeviceUtilizationData_Exception() {
        // 模拟异常
        Map<String, Object> params = new HashMap<>();
        when(rentalDeviceMapper.selectDeviceUtilizationList(params))
            .thenThrow(new RuntimeException("数据库连接失败"));
        
        // 执行测试
        List<DeviceUtilizationDTO> result = deviceUtilizationService.getDeviceUtilizationData(params);
        
        // 验证结果 - 应该返回空列表而不是抛出异常
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        // 验证Mapper方法被调用
        verify(rentalDeviceMapper, times(1)).selectDeviceUtilizationList(params);
    }
    
    /**
     * 测试客户服务 - 创建客户成功
     */
    @Test
    void testCreateCustomer_Success() {
        // 准备测试数据
        RentalCustomer customer = new RentalCustomer();
        customer.setCustomerName("测试客户");
        customer.setPhone("13812345678");
        customer.setEmail("test@example.com");
        
        // 模拟Mapper方法
        when(rentalCustomerMapper.selectByName("测试客户"))
            .thenReturn(null); // 客户不存在
        when(rentalCustomerMapper.insert(customer))
            .thenReturn(1); // 插入成功
        
        // 执行测试
        boolean result = rentalCustomerService.createCustomer(customer);
        
        // 验证结果
        assertTrue(result);
        
        // 验证Mapper方法被调用
        verify(rentalCustomerMapper, times(1)).selectByName("测试客户");
        verify(rentalCustomerMapper, times(1)).insert(customer);
    }
    
    /**
     * 测试客户服务 - 创建客户失败（客户已存在）
     */
    @Test
    void testCreateCustomer_AlreadyExists() {
        // 准备测试数据
        RentalCustomer customer = new RentalCustomer();
        customer.setCustomerName("已存在客户");
        
        RentalCustomer existingCustomer = new RentalCustomer();
        existingCustomer.setId(1L);
        existingCustomer.setCustomerName("已存在客户");
        
        // 模拟Mapper方法
        when(rentalCustomerMapper.selectByName("已存在客户"))
            .thenReturn(existingCustomer); // 客户已存在
        
        // 执行测试
        boolean result = rentalCustomerService.createCustomer(customer);
        
        // 验证结果
        assertFalse(result);
        
        // 验证Mapper方法被调用
        verify(rentalCustomerMapper, times(1)).selectByName("已存在客户");
        verify(rentalCustomerMapper, never()).insert(any()); // 不应该调用插入
    }
    
    /**
     * 测试设备状态更新功能
     */
    @Test
    void testUpdateDeviceStatus_Success() {
        // 准备测试数据
        String deviceId = "YX-0001";
        String newStatus = "maintenance";
        
        // 模拟Mapper方法
        when(rentalDeviceMapper.updateDeviceStatus(deviceId, newStatus))
            .thenReturn(1); // 更新成功
        
        // 执行测试
        boolean result = deviceUtilizationService.updateDeviceStatus(deviceId, newStatus);
        
        // 验证结果
        assertTrue(result);
        
        // 验证Mapper方法被调用
        verify(rentalDeviceMapper, times(1)).updateDeviceStatus(deviceId, newStatus);
    }
    
    /**
     * 测试设备状态更新功能 - 设备不存在
     */
    @Test
    void testUpdateDeviceStatus_DeviceNotFound() {
        // 准备测试数据
        String deviceId = "YX-9999";
        String newStatus = "maintenance";
        
        // 模拟Mapper方法
        when(rentalDeviceMapper.updateDeviceStatus(deviceId, newStatus))
            .thenReturn(0); // 更新失败，设备不存在
        
        // 执行测试
        boolean result = deviceUtilizationService.updateDeviceStatus(deviceId, newStatus);
        
        // 验证结果
        assertFalse(result);
        
        // 验证Mapper方法被调用
        verify(rentalDeviceMapper, times(1)).updateDeviceStatus(deviceId, newStatus);
    }
    
    /**
     * 测试批量设备状态更新功能
     */
    @Test
    void testBatchUpdateDeviceStatus() {
        // 准备测试数据
        List<String> deviceIds = Arrays.asList("YX-0001", "YX-0002", "YX-0003");
        String newStatus = "maintenance";
        
        // 模拟Mapper方法 - 前两个成功，第三个失败
        when(rentalDeviceMapper.updateDeviceStatus("YX-0001", newStatus))
            .thenReturn(1);
        when(rentalDeviceMapper.updateDeviceStatus("YX-0002", newStatus))
            .thenReturn(1);
        when(rentalDeviceMapper.updateDeviceStatus("YX-0003", newStatus))
            .thenReturn(0);
        
        // 执行测试
        int successCount = deviceUtilizationService.batchUpdateDeviceStatus(deviceIds, newStatus);
        
        // 验证结果
        assertEquals(2, successCount);
        
        // 验证Mapper方法被调用
        verify(rentalDeviceMapper, times(3)).updateDeviceStatus(anyString(), eq(newStatus));
    }
    
    /**
     * 测试今日统计数据获取
     */
    @Test
    void testGetTodayStats() {
        // 准备测试数据
        LocalDate today = LocalDate.now();
        Map<String, Object> mockTodayData = new HashMap<>();
        mockTodayData.put("revenue", new BigDecimal("5000.00"));
        mockTodayData.put("orders", 10);
        mockTodayData.put("activeDevices", 8);
        mockTodayData.put("avgUtilization", new BigDecimal("75.5"));
        
        // 模拟Mapper方法
        when(rentalRecordMapper.selectTodayStats(today))
            .thenReturn(mockTodayData);
        
        // 执行测试
        Map<String, Object> result = rentalStatsService.getTodayStats();
        
        // 验证结果
        assertNotNull(result);
        assertEquals(new BigDecimal("5000.00"), result.get("revenue"));
        assertEquals(10, result.get("orders"));
        assertEquals(8, result.get("activeDevices"));
        assertEquals(new BigDecimal("75.5"), result.get("avgUtilization"));
        
        // 验证Mapper方法被调用
        verify(rentalRecordMapper, times(1)).selectTodayStats(today);
    }
    
    /**
     * 测试设备型号列表获取
     */
    @Test
    void testGetAllDeviceModels() {
        // 准备测试数据
        List<String> mockModels = Arrays.asList(
            "YX-Robot-Pro", "YX-Robot-Standard", "YX-Robot-Lite", "YX-Robot-Mini"
        );
        
        // 模拟Mapper方法
        when(rentalDeviceMapper.selectAllDeviceModels())
            .thenReturn(mockModels);
        
        // 执行测试
        List<String> result = deviceUtilizationService.getAllDeviceModels();
        
        // 验证结果
        assertNotNull(result);
        assertEquals(4, result.size());
        assertTrue(result.contains("YX-Robot-Pro"));
        assertTrue(result.contains("YX-Robot-Standard"));
        
        // 验证Mapper方法被调用
        verify(rentalDeviceMapper, times(1)).selectAllDeviceModels();
    }
    
    /**
     * 测试高级搜索参数构建功能
     */
    @Test
    void testBuildAdvancedSearchParams() {
        // 准备测试数据
        Map<String, Object> searchRequest = new HashMap<>();
        searchRequest.put("page", 1);
        searchRequest.put("pageSize", 20);
        searchRequest.put("keyword", "YX-0001");
        searchRequest.put("deviceModels", Arrays.asList("YX-Robot-Pro", "YX-Robot-Standard"));
        searchRequest.put("statuses", Arrays.asList("active", "idle"));
        searchRequest.put("minUtilization", 50.0);
        searchRequest.put("maxUtilization", 90.0);
        searchRequest.put("startDate", "2025-01-01");
        searchRequest.put("endDate", "2025-01-31");
        
        // 执行测试
        Map<String, Object> result = deviceUtilizationService.buildAdvancedSearchParams(searchRequest);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.get("page"));
        assertEquals(20, result.get("pageSize"));
        assertEquals(0, result.get("offset"));
        assertEquals("YX-0001", result.get("keyword"));
        assertEquals(Arrays.asList("YX-Robot-Pro", "YX-Robot-Standard"), result.get("deviceModels"));
        assertEquals(Arrays.asList("active", "idle"), result.get("statuses"));
        assertEquals(50.0, result.get("minUtilization"));
        assertEquals(90.0, result.get("maxUtilization"));
        assertEquals("2025-01-01", result.get("startDate"));
        assertEquals("2025-01-31", result.get("endDate"));
    }
    
    /**
     * 创建模拟的设备利用率数据
     */
    private DeviceUtilizationDTO createMockDeviceUtilization(String deviceId, String deviceModel, Double utilizationRate) {
        DeviceUtilizationDTO dto = new DeviceUtilizationDTO();
        dto.setDeviceId(deviceId);
        dto.setDeviceModel(deviceModel);
        dto.setUtilizationRate(BigDecimal.valueOf(utilizationRate));
        dto.setTotalRentalDays(100);
        dto.setTotalAvailableDays(365);
        dto.setCurrentStatus("active");
        dto.setRegion("北京市");
        dto.setPerformanceScore(95);
        dto.setSignalStrength(85);
        dto.setMaintenanceStatus("normal");
        return dto;
    }
}