package com.yxrobot.search;

import com.yxrobot.controller.RentalController;
import com.yxrobot.service.DeviceUtilizationService;
import com.yxrobot.service.RentalStatsService;
import com.yxrobot.service.RentalAnalysisService;
import com.yxrobot.service.RentalCustomerService;
import com.yxrobot.validation.RentalValidator;
import com.yxrobot.validation.RentalFormValidator;
import com.yxrobot.cache.RentalCacheService;
import com.yxrobot.dto.DeviceUtilizationDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 租赁搜索和筛选功能测试类
 * 测试任务15实现的搜索和筛选功能
 * 
 * @author Kiro
 * @date 2025-01-28
 */
public class RentalSearchAndFilterTest {
    
    @Mock
    private DeviceUtilizationService deviceUtilizationService;
    
    @Mock
    private RentalStatsService rentalStatsService;
    
    @Mock
    private RentalAnalysisService rentalAnalysisService;
    
    @Mock
    private RentalCustomerService rentalCustomerService;
    
    @Mock
    private RentalValidator rentalValidator;
    
    @Mock
    private RentalFormValidator rentalFormValidator;
    
    @Mock
    private RentalCacheService cacheService;
    
    @InjectMocks
    private RentalController rentalController;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    /**
     * 测试高级搜索功能
     */
    @Test
    void testAdvancedSearchDevices() {
        // 准备测试数据
        Map<String, Object> searchRequest = new HashMap<>();
        searchRequest.put("keyword", "YX-0001");
        searchRequest.put("deviceModels", Arrays.asList("YX-Robot-Pro", "YX-Robot-Standard"));
        searchRequest.put("statuses", Arrays.asList("active", "idle"));
        searchRequest.put("minUtilization", 50.0);
        searchRequest.put("maxUtilization", 90.0);
        searchRequest.put("page", 1);
        searchRequest.put("pageSize", 20);
        
        List<DeviceUtilizationDTO> mockDeviceList = Arrays.asList(
            createMockDeviceUtilization("YX-0001", "YX-Robot-Pro", 85.5),
            createMockDeviceUtilization("YX-0002", "YX-Robot-Standard", 72.3)
        );
        
        // 模拟服务方法
        when(rentalFormValidator.validateQueryParamsForm(any(Map.class)))
            .thenReturn(new ArrayList<>());
        when(deviceUtilizationService.buildAdvancedSearchParams(any(Map.class)))
            .thenReturn(searchRequest);
        when(deviceUtilizationService.getDeviceUtilizationWithAdvancedSearch(any(Map.class)))
            .thenReturn(mockDeviceList);
        when(deviceUtilizationService.getDeviceUtilizationCountWithAdvancedSearch(any(Map.class)))
            .thenReturn(2L);
        
        // 执行测试
        ResponseEntity<Map<String, Object>> response = rentalController.advancedSearchDevices(searchRequest);
        
        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.get("code"));
        assertEquals("搜索成功", responseBody.get("message"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
        assertNotNull(data);
        assertEquals(2L, data.get("total"));
        assertEquals(1, data.get("page"));
        assertEquals(20, data.get("pageSize"));
        
        @SuppressWarnings("unchecked")
        List<DeviceUtilizationDTO> resultList = (List<DeviceUtilizationDTO>) data.get("list");
        assertEquals(2, resultList.size());
        
        // 验证服务方法被调用
        verify(rentalFormValidator, times(1)).validateQueryParamsForm(any(Map.class));
        verify(deviceUtilizationService, times(1)).buildAdvancedSearchParams(any(Map.class));
        verify(deviceUtilizationService, times(1)).getDeviceUtilizationWithAdvancedSearch(any(Map.class));
        verify(deviceUtilizationService, times(1)).getDeviceUtilizationCountWithAdvancedSearch(any(Map.class));
    }
    
    /**
     * 测试按时间范围搜索功能
     */
    @Test
    void testGetDevicesByDateRange() {
        // 准备测试数据
        String startDate = "2025-01-01";
        String endDate = "2025-01-31";
        String keyword = "YX-0001";
        
        List<DeviceUtilizationDTO> mockDeviceList = Arrays.asList(
            createMockDeviceUtilization("YX-0001", "YX-Robot-Pro", 85.5),
            createMockDeviceUtilization("YX-0003", "YX-Robot-Lite", 68.2)
        );
        
        // 模拟验证器方法
        doNothing().when(rentalValidator).validateDateRange(any(), any());
        doNothing().when(rentalValidator).validatePaginationParams(anyInt(), anyInt());
        doNothing().when(rentalValidator).validateSearchKeyword(anyString());
        
        // 模拟服务方法
        when(deviceUtilizationService.getDeviceUtilizationByDateRange(any(Map.class)))
            .thenReturn(mockDeviceList);
        
        // 执行测试
        ResponseEntity<Map<String, Object>> response = rentalController.getDevicesByDateRange(
            startDate, endDate, 1, 20, keyword);
        
        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.get("code"));
        assertEquals("搜索成功", responseBody.get("message"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
        assertNotNull(data);
        assertEquals(2, data.get("total"));
        
        // 验证服务方法被调用
        verify(rentalValidator, times(1)).validateDateRange(any(), any());
        verify(rentalValidator, times(1)).validatePaginationParams(eq(1), eq(20));
        verify(rentalValidator, times(1)).validateSearchKeyword(eq(keyword));
        verify(deviceUtilizationService, times(1)).getDeviceUtilizationByDateRange(any(Map.class));
    }
    
    /**
     * 测试按设备型号筛选功能
     */
    @Test
    void testGetDevicesByModels() {
        // 准备测试数据
        String deviceModels = "YX-Robot-Pro,YX-Robot-Standard";
        String keyword = "YX";
        String region = "北京市";
        
        List<DeviceUtilizationDTO> mockDeviceList = Arrays.asList(
            createMockDeviceUtilization("YX-0001", "YX-Robot-Pro", 85.5),
            createMockDeviceUtilization("YX-0002", "YX-Robot-Standard", 72.3)
        );
        
        // 模拟验证器方法
        doNothing().when(rentalValidator).validateDeviceModel(anyString());
        doNothing().when(rentalValidator).validatePaginationParams(anyInt(), anyInt());
        doNothing().when(rentalValidator).validateSearchKeyword(anyString());
        
        // 模拟服务方法
        when(deviceUtilizationService.getDeviceUtilizationByModels(any(List.class), any(Map.class)))
            .thenReturn(mockDeviceList);
        
        // 执行测试
        ResponseEntity<Map<String, Object>> response = rentalController.getDevicesByModels(
            deviceModels, 1, 20, keyword, region);
        
        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.get("code"));
        assertEquals("筛选成功", responseBody.get("message"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
        assertNotNull(data);
        assertEquals(2, data.get("total"));
        
        @SuppressWarnings("unchecked")
        List<String> selectedModels = (List<String>) data.get("selectedModels");
        assertEquals(2, selectedModels.size());
        assertTrue(selectedModels.contains("YX-Robot-Pro"));
        assertTrue(selectedModels.contains("YX-Robot-Standard"));
        
        // 验证服务方法被调用
        verify(rentalValidator, times(2)).validateDeviceModel(anyString());
        verify(deviceUtilizationService, times(1)).getDeviceUtilizationByModels(any(List.class), any(Map.class));
    }
    
    /**
     * 测试按设备状态筛选功能
     */
    @Test
    void testGetDevicesByStatuses() {
        // 准备测试数据
        String statuses = "active,idle";
        String keyword = "YX";
        
        List<DeviceUtilizationDTO> mockDeviceList = Arrays.asList(
            createMockDeviceUtilization("YX-0001", "YX-Robot-Pro", 85.5),
            createMockDeviceUtilization("YX-0002", "YX-Robot-Standard", 72.3)
        );
        
        // 模拟验证器方法
        doNothing().when(rentalValidator).validateDeviceStatus(anyString());
        doNothing().when(rentalValidator).validatePaginationParams(anyInt(), anyInt());
        doNothing().when(rentalValidator).validateSearchKeyword(anyString());
        
        // 模拟服务方法
        when(deviceUtilizationService.getDeviceUtilizationByStatuses(any(List.class), any(Map.class)))
            .thenReturn(mockDeviceList);
        
        // 执行测试
        ResponseEntity<Map<String, Object>> response = rentalController.getDevicesByStatuses(
            statuses, 1, 20, keyword);
        
        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.get("code"));
        assertEquals("筛选成功", responseBody.get("message"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
        assertNotNull(data);
        assertEquals(2, data.get("total"));
        
        @SuppressWarnings("unchecked")
        List<String> selectedStatuses = (List<String>) data.get("selectedStatuses");
        assertEquals(2, selectedStatuses.size());
        assertTrue(selectedStatuses.contains("active"));
        assertTrue(selectedStatuses.contains("idle"));
        
        // 验证服务方法被调用
        verify(rentalValidator, times(2)).validateDeviceStatus(anyString());
        verify(deviceUtilizationService, times(1)).getDeviceUtilizationByStatuses(any(List.class), any(Map.class));
    }
    
    /**
     * 测试按利用率范围筛选功能
     */
    @Test
    void testGetDevicesByUtilizationRange() {
        // 准备测试数据
        Double minUtilization = 50.0;
        Double maxUtilization = 90.0;
        String keyword = "YX";
        
        List<DeviceUtilizationDTO> mockDeviceList = Arrays.asList(
            createMockDeviceUtilization("YX-0001", "YX-Robot-Pro", 85.5),
            createMockDeviceUtilization("YX-0002", "YX-Robot-Standard", 72.3)
        );
        
        // 模拟验证器方法
        doNothing().when(rentalValidator).validatePaginationParams(anyInt(), anyInt());
        doNothing().when(rentalValidator).validateSearchKeyword(anyString());
        
        // 模拟服务方法
        when(deviceUtilizationService.getDeviceUtilizationByUtilizationRange(
            eq(minUtilization), eq(maxUtilization), any(Map.class)))
            .thenReturn(mockDeviceList);
        
        // 执行测试
        ResponseEntity<Map<String, Object>> response = rentalController.getDevicesByUtilizationRange(
            minUtilization, maxUtilization, 1, 20, keyword);
        
        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.get("code"));
        assertEquals("筛选成功", responseBody.get("message"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
        assertNotNull(data);
        assertEquals(2, data.get("total"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> utilizationRange = (Map<String, Object>) data.get("utilizationRange");
        assertNotNull(utilizationRange);
        assertEquals(50.0, utilizationRange.get("min"));
        assertEquals(90.0, utilizationRange.get("max"));
        
        // 验证服务方法被调用
        verify(deviceUtilizationService, times(1)).getDeviceUtilizationByUtilizationRange(
            eq(minUtilization), eq(maxUtilization), any(Map.class));
    }
    
    /**
     * 测试利用率范围参数验证
     */
    @Test
    void testUtilizationRangeValidation() {
        // 测试无效的最小利用率
        ResponseEntity<Map<String, Object>> response = rentalController.getDevicesByUtilizationRange(
            -10.0, 90.0, 1, 20, null);
        
        assertEquals(500, response.getStatusCodeValue());
        
        // 测试无效的最大利用率
        response = rentalController.getDevicesByUtilizationRange(
            50.0, 150.0, 1, 20, null);
        
        assertEquals(500, response.getStatusCodeValue());
        
        // 测试最小值大于最大值
        response = rentalController.getDevicesByUtilizationRange(
            90.0, 50.0, 1, 20, null);
        
        assertEquals(500, response.getStatusCodeValue());
    }
    
    /**
     * 测试搜索建议功能
     */
    @Test
    void testGetSearchSuggestions() {
        // 准备测试数据
        String keyword = "YX";
        String type = "deviceId";
        
        List<String> mockSuggestions = Arrays.asList(
            "YX-0001", "YX-0002", "YX-0003", "YX-0004", "YX-0005"
        );
        
        // 模拟验证器方法
        doNothing().when(rentalValidator).validateSearchKeyword(anyString());
        
        // 模拟服务方法
        when(deviceUtilizationService.getSearchSuggestions(eq(keyword), eq(type)))
            .thenReturn(mockSuggestions);
        
        // 执行测试
        ResponseEntity<Map<String, Object>> response = rentalController.getSearchSuggestions(keyword, type);
        
        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.get("code"));
        assertEquals("查询成功", responseBody.get("message"));
        
        @SuppressWarnings("unchecked")
        List<String> suggestions = (List<String>) responseBody.get("data");
        assertNotNull(suggestions);
        assertEquals(5, suggestions.size());
        assertTrue(suggestions.contains("YX-0001"));
        
        // 验证服务方法被调用
        verify(rentalValidator, times(1)).validateSearchKeyword(eq(keyword));
        verify(deviceUtilizationService, times(1)).getSearchSuggestions(eq(keyword), eq(type));
    }
    
    /**
     * 测试筛选选项统计功能
     */
    @Test
    void testGetFilterOptionsStats() {
        // 准备测试数据
        Map<String, Object> mockStats = new HashMap<>();
        mockStats.put("deviceModels", Arrays.asList(
            Map.of("value", "YX-Robot-Pro", "label", "YX-Robot-Pro", "count", 10),
            Map.of("value", "YX-Robot-Standard", "label", "YX-Robot-Standard", "count", 8)
        ));
        mockStats.put("statuses", Arrays.asList(
            Map.of("value", "active", "label", "运行中", "count", 12),
            Map.of("value", "idle", "label", "空闲", "count", 6)
        ));
        
        // 模拟服务方法
        when(deviceUtilizationService.getFilterOptionsStats())
            .thenReturn(mockStats);
        
        // 执行测试
        ResponseEntity<Map<String, Object>> response = rentalController.getFilterOptionsStats();
        
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
        assertTrue(data.containsKey("deviceModels"));
        assertTrue(data.containsKey("statuses"));
        
        // 验证服务方法被调用
        verify(deviceUtilizationService, times(1)).getFilterOptionsStats();
    }
    
    /**
     * 测试高级搜索参数验证失败
     */
    @Test
    void testAdvancedSearchValidationFailure() {
        // 准备测试数据
        Map<String, Object> searchRequest = new HashMap<>();
        searchRequest.put("keyword", "invalid<script>");
        
        List<String> validationErrors = Arrays.asList("搜索关键词包含非法字符");
        
        // 模拟验证失败
        when(rentalFormValidator.validateQueryParamsForm(any(Map.class)))
            .thenReturn(validationErrors);
        
        // 执行测试
        ResponseEntity<Map<String, Object>> response = rentalController.advancedSearchDevices(searchRequest);
        
        // 验证结果
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(400, responseBody.get("code"));
        assertEquals("搜索参数验证失败", responseBody.get("message"));
        assertTrue(responseBody.containsKey("validationErrors"));
        
        // 验证服务方法被调用
        verify(rentalFormValidator, times(1)).validateQueryParamsForm(any(Map.class));
        verify(deviceUtilizationService, never()).getDeviceUtilizationWithAdvancedSearch(any(Map.class));
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