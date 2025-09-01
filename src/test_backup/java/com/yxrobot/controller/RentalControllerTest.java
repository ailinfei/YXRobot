package com.yxrobot.controller;

import com.yxrobot.dto.DeviceUtilizationDTO;
import com.yxrobot.dto.RentalStatsDTO;
import com.yxrobot.service.DeviceUtilizationService;
import com.yxrobot.service.RentalStatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 租赁控制器测试类
 * 
 * @author Kiro
 * @date 2025-01-28
 */
@ExtendWith(MockitoExtension.class)
class RentalControllerTest {
    
    @Mock
    private RentalStatsService rentalStatsService;
    
    @Mock
    private DeviceUtilizationService deviceUtilizationService;
    
    @InjectMocks
    private RentalController rentalController;
    
    private RentalStatsDTO mockRentalStats;
    private List<DeviceUtilizationDTO> mockDeviceList;
    private Map<String, Object> mockTodayStats;
    private Map<String, Object> mockDeviceStatusStats;
    
    @BeforeEach
    void setUp() {
        // 准备模拟租赁统计数据
        mockRentalStats = new RentalStatsDTO();
        mockRentalStats.setTotalRentalRevenue(new BigDecimal("1285420.00"));
        mockRentalStats.setTotalRentalDevices(285);
        mockRentalStats.setActiveRentalDevices(198);
        mockRentalStats.setDeviceUtilizationRate(new BigDecimal("78.5"));
        mockRentalStats.setAverageRentalPeriod(new BigDecimal("32"));
        mockRentalStats.setTotalRentalOrders(1456);
        mockRentalStats.setRevenueGrowthRate(new BigDecimal("18.6"));
        mockRentalStats.setDeviceGrowthRate(new BigDecimal("12.3"));
        
        // 准备模拟设备利用率数据
        mockDeviceList = new ArrayList<>();
        DeviceUtilizationDTO device1 = new DeviceUtilizationDTO();
        device1.setDeviceId("YX-0001");
        device1.setDeviceModel("YX-Robot-Pro");
        device1.setUtilizationRate(new BigDecimal("95.5"));
        device1.setTotalRentalDays(348);
        device1.setTotalAvailableDays(365);
        device1.setCurrentStatus("active");
        device1.setRegion("华东");
        mockDeviceList.add(device1);
        
        DeviceUtilizationDTO device2 = new DeviceUtilizationDTO();
        device2.setDeviceId("YX-0002");
        device2.setDeviceModel("YX-Robot-Standard");
        device2.setUtilizationRate(new BigDecimal("89.2"));
        device2.setTotalRentalDays(325);
        device2.setTotalAvailableDays(365);
        device2.setCurrentStatus("active");
        device2.setRegion("华北");
        mockDeviceList.add(device2);
        
        // 准备模拟今日统计数据
        mockTodayStats = new HashMap<>();
        mockTodayStats.put("revenue", new BigDecimal("45680.00"));
        mockTodayStats.put("orders", 23);
        mockTodayStats.put("activeDevices", 198);
        mockTodayStats.put("avgUtilization", new BigDecimal("78.5"));
        
        // 准备模拟设备状态统计数据
        mockDeviceStatusStats = new HashMap<>();
        mockDeviceStatusStats.put("active", 198);
        mockDeviceStatusStats.put("idle", 67);
        mockDeviceStatusStats.put("maintenance", 20);
    }
    
    @Test
    void testGetRentalStats() {
        // 准备测试数据
        when(rentalStatsService.getRentalStats(any(), any())).thenReturn(mockRentalStats);
        
        // 执行测试
        ResponseEntity<Map<String, Object>> response = rentalController.getRentalStats("2025-01-01", "2025-01-31");
        
        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.get("code"));
        assertEquals("查询成功", responseBody.get("message"));
        
        RentalStatsDTO data = (RentalStatsDTO) responseBody.get("data");
        assertNotNull(data);
        assertEquals(new BigDecimal("1285420.00"), data.getTotalRentalRevenue());
        assertEquals(285, data.getTotalRentalDevices());
        assertEquals(new BigDecimal("78.5"), data.getDeviceUtilizationRate());
        
        // 验证服务调用
        verify(rentalStatsService, times(1)).getRentalStats(any(), any());
    }
    
    @Test
    void testGetDeviceUtilizationData() {
        // 准备测试数据
        when(deviceUtilizationService.getDeviceUtilizationData(any(Map.class))).thenReturn(mockDeviceList);
        when(deviceUtilizationService.getDeviceUtilizationCount(any(Map.class))).thenReturn(2L);
        
        // 执行测试
        ResponseEntity<Map<String, Object>> response = rentalController.getDeviceUtilizationData(
            1, 20, "YX", "YX-Robot-Pro", "active", "华东");
        
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
        assertEquals(2L, data.get("total"));
        assertEquals(1, data.get("page"));
        assertEquals(20, data.get("pageSize"));
        
        @SuppressWarnings("unchecked")
        List<DeviceUtilizationDTO> list = (List<DeviceUtilizationDTO>) data.get("list");
        assertNotNull(list);
        assertEquals(2, list.size());
        assertEquals("YX-0001", list.get(0).getDeviceId());
        
        // 验证服务调用
        verify(deviceUtilizationService, times(1)).getDeviceUtilizationData(any(Map.class));
        verify(deviceUtilizationService, times(1)).getDeviceUtilizationCount(any(Map.class));
    }
    
    @Test
    void testGetTodayStats() {
        // 准备测试数据
        when(rentalStatsService.getTodayStats()).thenReturn(mockTodayStats);
        
        // 执行测试
        ResponseEntity<Map<String, Object>> response = rentalController.getTodayStats();
        
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
        assertEquals(new BigDecimal("45680.00"), data.get("revenue"));
        assertEquals(23, data.get("orders"));
        assertEquals(198, data.get("activeDevices"));
        
        // 验证服务调用
        verify(rentalStatsService, times(1)).getTodayStats();
    }
    
    @Test
    void testGetDeviceStatusStats() {
        // 准备测试数据
        when(deviceUtilizationService.getDeviceStatusStats()).thenReturn(mockDeviceStatusStats);
        
        // 执行测试
        ResponseEntity<Map<String, Object>> response = rentalController.getDeviceStatusStats();
        
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
        assertEquals(198, data.get("active"));
        assertEquals(67, data.get("idle"));
        assertEquals(20, data.get("maintenance"));
        
        // 验证服务调用
        verify(deviceUtilizationService, times(1)).getDeviceStatusStats();
    }
    
    @Test
    void testGetTopDevices() {
        // 准备测试数据
        when(deviceUtilizationService.getTopDevicesByUtilization(5)).thenReturn(mockDeviceList);
        
        // 执行测试
        ResponseEntity<Map<String, Object>> response = rentalController.getTopDevices(5);
        
        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.get("code"));
        assertEquals("查询成功", responseBody.get("message"));
        
        @SuppressWarnings("unchecked")
        List<DeviceUtilizationDTO> data = (List<DeviceUtilizationDTO>) responseBody.get("data");
        assertNotNull(data);
        assertEquals(2, data.size());
        assertEquals("YX-0001", data.get(0).getDeviceId());
        assertEquals(new BigDecimal("95.5"), data.get(0).getUtilizationRate());
        
        // 验证服务调用
        verify(deviceUtilizationService, times(1)).getTopDevicesByUtilization(5);
    }
    
    @Test
    void testGetDeviceModels() {
        // 准备测试数据
        List<String> mockModels = List.of("YX-Robot-Pro", "YX-Robot-Standard", "YX-Robot-Lite");
        when(deviceUtilizationService.getAllDeviceModels()).thenReturn(mockModels);
        
        // 执行测试
        ResponseEntity<Map<String, Object>> response = rentalController.getDeviceModels();
        
        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.get("code"));
        assertEquals("查询成功", responseBody.get("message"));
        
        @SuppressWarnings("unchecked")
        List<String> data = (List<String>) responseBody.get("data");
        assertNotNull(data);
        assertEquals(3, data.size());
        assertTrue(data.contains("YX-Robot-Pro"));
        
        // 验证服务调用
        verify(deviceUtilizationService, times(1)).getAllDeviceModels();
    }
    
    @Test
    void testGetRegions() {
        // 准备测试数据
        List<String> mockRegions = List.of("华东", "华北", "华南", "华中");
        when(deviceUtilizationService.getAllRegions()).thenReturn(mockRegions);
        
        // 执行测试
        ResponseEntity<Map<String, Object>> response = rentalController.getRegions();
        
        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.get("code"));
        assertEquals("查询成功", responseBody.get("message"));
        
        @SuppressWarnings("unchecked")
        List<String> data = (List<String>) responseBody.get("data");
        assertNotNull(data);
        assertEquals(4, data.size());
        assertTrue(data.contains("华东"));
        
        // 验证服务调用
        verify(deviceUtilizationService, times(1)).getAllRegions();
    }
    
    @Test
    void testGetRentalStatsWithException() {
        // 模拟异常
        when(rentalStatsService.getRentalStats(any(), any())).thenThrow(new RuntimeException("数据库连接失败"));
        
        // 执行测试
        ResponseEntity<Map<String, Object>> response = rentalController.getRentalStats("2025-01-01", "2025-01-31");
        
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
    void testGetDeviceUtilizationDataWithDefaultParams() {
        // 准备测试数据
        when(deviceUtilizationService.getDeviceUtilizationData(any(Map.class))).thenReturn(mockDeviceList);
        when(deviceUtilizationService.getDeviceUtilizationCount(any(Map.class))).thenReturn(2L);
        
        // 执行测试（使用默认参数）
        ResponseEntity<Map<String, Object>> response = rentalController.getDeviceUtilizationData(
            null, null, null, null, null, null);
        
        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.get("code"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
        assertNotNull(data);
        assertEquals(1, data.get("page")); // 默认页码
        assertEquals(20, data.get("pageSize")); // 默认每页大小
        
        // 验证服务调用
        verify(deviceUtilizationService, times(1)).getDeviceUtilizationData(any(Map.class));
        verify(deviceUtilizationService, times(1)).getDeviceUtilizationCount(any(Map.class));
    }
}