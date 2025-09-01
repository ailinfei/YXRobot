package com.yxrobot.controller;

import com.yxrobot.dto.DeviceUtilizationDTO;
import com.yxrobot.dto.RentalStatsDTO;
import com.yxrobot.service.RentalStatsService;
import com.yxrobot.service.DeviceUtilizationService;
import com.yxrobot.service.RentalAnalysisService;
import com.yxrobot.service.RentalCustomerService;
import com.yxrobot.validation.RentalValidator;
import com.yxrobot.validation.RentalFormValidator;
import com.yxrobot.cache.RentalCacheService;
import com.yxrobot.exception.RentalException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 租赁控制器测试类
 * 测试租赁API接口的正确性和异常处理
 * 
 * @author Kiro
 * @date 2025-01-28
 */
@WebMvcTest(RentalController.class)
class RentalControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private RentalStatsService rentalStatsService;
    
    @MockBean
    private DeviceUtilizationService deviceUtilizationService;
    
    @MockBean
    private RentalAnalysisService rentalAnalysisService;
    
    @MockBean
    private RentalCustomerService rentalCustomerService;
    
    @MockBean
    private RentalValidator rentalValidator;
    
    @MockBean
    private RentalFormValidator rentalFormValidator;
    
    @MockBean
    private RentalCacheService cacheService;
    
    private RentalStatsDTO mockRentalStats;
    private List<DeviceUtilizationDTO> mockDeviceList;
    private Map<String, Object> mockTodayStats;
    
    @BeforeEach
    void setUp() {
        // 模拟租赁统计数据
        mockRentalStats = new RentalStatsDTO();
        mockRentalStats.setTotalRentalRevenue(new BigDecimal("125000.00"));
        mockRentalStats.setTotalRentalOrders(150);
        mockRentalStats.setTotalRentalDevices(80);
        mockRentalStats.setDeviceUtilizationRate(new BigDecimal("81.25"));
        
        // 模拟设备利用率数据
        mockDeviceList = new ArrayList<>();
        DeviceUtilizationDTO device1 = new DeviceUtilizationDTO();
        device1.setDeviceId("YX-001");
        device1.setDeviceModel("YX-Robot-Pro");
        device1.setUtilizationRate(new BigDecimal("85.5"));
        device1.setCurrentStatus("active");
        mockDeviceList.add(device1);
        
        // 模拟今日统计数据
        mockTodayStats = new HashMap<>();
        mockTodayStats.put("revenue", new BigDecimal("5000.00"));
        mockTodayStats.put("orders", 8);
        mockTodayStats.put("activeDevices", 12);
        mockTodayStats.put("avgUtilization", new BigDecimal("75.5"));
    }
    
    @Test
    void testGetRentalStats_WithValidParams_ShouldReturnSuccess() throws Exception {
        // Given
        String startDate = "2025-01-01";
        String endDate = "2025-01-31";
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 1, 31);
        
        when(rentalValidator.validateDateParam(startDate, "startDate")).thenReturn(start);
        when(rentalValidator.validateDateParam(endDate, "endDate")).thenReturn(end);
        doNothing().when(rentalValidator).validateDateRange(start, end);
        when(rentalStatsService.getRentalStats(start, end)).thenReturn(mockRentalStats);
        
        // When & Then
        mockMvc.perform(get("/api/rental/stats")
                .param("startDate", startDate)
                .param("endDate", endDate))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.totalRentalRevenue").value(125000.00))
                .andExpect(jsonPath("$.data.totalRentalOrders").value(150))
                .andExpect(jsonPath("$.data.totalRentalDevices").value(80))
                .andExpect(jsonPath("$.data.deviceUtilizationRate").value(81.25));
        
        verify(rentalValidator).validateDateParam(startDate, "startDate");
        verify(rentalValidator).validateDateParam(endDate, "endDate");
        verify(rentalValidator).validateDateRange(start, end);
        verify(rentalStatsService).getRentalStats(start, end);
    }
    
    @Test
    void testGetRentalStats_WithoutParams_ShouldReturnSuccess() throws Exception {
        // Given
        when(rentalValidator.validateDateParam(null, "startDate")).thenReturn(null);
        when(rentalValidator.validateDateParam(null, "endDate")).thenReturn(null);
        doNothing().when(rentalValidator).validateDateRange(null, null);
        when(rentalStatsService.getRentalStats(null, null)).thenReturn(mockRentalStats);
        
        // When & Then
        mockMvc.perform(get("/api/rental/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalRentalRevenue").value(125000.00));
        
        verify(rentalStatsService).getRentalStats(null, null);
    }
    
    @Test
    void testGetRentalStats_WithInvalidDate_ShouldReturnError() throws Exception {
        // Given
        String invalidDate = "invalid-date";
        when(rentalValidator.validateDateParam(invalidDate, "startDate"))
            .thenThrow(RentalException.dataValidationError("startDate", invalidDate));
        
        // When & Then
        mockMvc.perform(get("/api/rental/stats")
                .param("startDate", invalidDate))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").isEmpty());
    }
    
    @Test
    void testGetDeviceUtilizationData_WithValidParams_ShouldReturnSuccess() throws Exception {
        // Given
        Integer page = 1;
        Integer pageSize = 20;
        String keyword = "YX";
        String deviceModel = "YX-Robot-Pro";
        String currentStatus = "active";
        
        doNothing().when(rentalValidator).validatePaginationParams(page, pageSize);
        doNothing().when(rentalValidator).validateSearchKeyword(keyword);
        doNothing().when(rentalValidator).validateDeviceStatus(currentStatus);
        
        when(deviceUtilizationService.getDeviceUtilizationData(any(Map.class))).thenReturn(mockDeviceList);
        when(deviceUtilizationService.getDeviceUtilizationCount(any(Map.class))).thenReturn(25L);
        
        // When & Then
        mockMvc.perform(get("/api/rental/devices")
                .param("page", "1")
                .param("pageSize", "20")
                .param("keyword", keyword)
                .param("deviceModel", deviceModel)
                .param("currentStatus", currentStatus))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list").isArray())
                .andExpect(jsonPath("$.data.list[0].deviceId").value("YX-001"))
                .andExpect(jsonPath("$.data.total").value(25))
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(20))
                .andExpect(jsonPath("$.data.totalPages").value(2)); // (25+20-1)/20 = 2
        
        verify(rentalValidator).validatePaginationParams(page, pageSize);
        verify(rentalValidator).validateSearchKeyword(keyword);
        verify(rentalValidator).validateDeviceStatus(currentStatus);
    }
    
    @Test
    void testGetDeviceUtilizationData_WithDefaultParams_ShouldReturnSuccess() throws Exception {
        // Given
        doNothing().when(rentalValidator).validatePaginationParams(1, 20);
        doNothing().when(rentalValidator).validateSearchKeyword(null);
        doNothing().when(rentalValidator).validateDeviceStatus(null);
        
        when(deviceUtilizationService.getDeviceUtilizationData(any(Map.class))).thenReturn(mockDeviceList);
        when(deviceUtilizationService.getDeviceUtilizationCount(any(Map.class))).thenReturn(1L);
        
        // When & Then
        mockMvc.perform(get("/api/rental/devices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(20));
    }
    
    @Test
    void testGetDeviceUtilizationData_WithInvalidPagination_ShouldReturnError() throws Exception {
        // Given
        doThrow(RentalException.dataValidationError("page", "0"))
            .when(rentalValidator).validatePaginationParams(0, 20);
        
        // When & Then
        mockMvc.perform(get("/api/rental/devices")
                .param("page", "0")
                .param("pageSize", "20"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500));
    }
    
    @Test
    void testGetTodayStats_ShouldReturnSuccess() throws Exception {
        // Given
        when(rentalStatsService.getTodayStats()).thenReturn(mockTodayStats);
        
        // When & Then
        mockMvc.perform(get("/api/rental/today-stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.revenue").value(5000.00))
                .andExpect(jsonPath("$.data.orders").value(8))
                .andExpect(jsonPath("$.data.activeDevices").value(12))
                .andExpect(jsonPath("$.data.avgUtilization").value(75.5));
        
        verify(rentalStatsService).getTodayStats();
    }
    
    @Test
    void testGetDeviceStatusStats_ShouldReturnSuccess() throws Exception {
        // Given
        Map<String, Object> statusStats = new HashMap<>();
        statusStats.put("active", 65);
        statusStats.put("idle", 15);
        statusStats.put("maintenance", 8);
        
        when(deviceUtilizationService.getDeviceStatusStats()).thenReturn(statusStats);
        
        // When & Then
        mockMvc.perform(get("/api/rental/device-status-stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.active").value(65))
                .andExpect(jsonPath("$.data.idle").value(15))
                .andExpect(jsonPath("$.data.maintenance").value(8));
        
        verify(deviceUtilizationService).getDeviceStatusStats();
    }
    
    @Test
    void testGetTopDevices_WithValidLimit_ShouldReturnSuccess() throws Exception {
        // Given
        Integer limit = 5;
        when(deviceUtilizationService.getTopDevicesByUtilization(limit)).thenReturn(mockDeviceList);
        
        // When & Then
        mockMvc.perform(get("/api/rental/top-devices")
                .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].deviceId").value("YX-001"));
        
        verify(deviceUtilizationService).getTopDevicesByUtilization(5);
    }
    
    @Test
    void testGetTopDevices_WithDefaultLimit_ShouldReturnSuccess() throws Exception {
        // Given
        when(deviceUtilizationService.getTopDevicesByUtilization(5)).thenReturn(mockDeviceList);
        
        // When & Then
        mockMvc.perform(get("/api/rental/top-devices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        
        verify(deviceUtilizationService).getTopDevicesByUtilization(5);
    }
    
    @Test
    void testGetDeviceModels_ShouldReturnSuccess() throws Exception {
        // Given
        List<String> deviceModels = Arrays.asList("YX-Robot-Pro", "YX-Robot-Standard", "YX-Robot-Lite");
        when(deviceUtilizationService.getAllDeviceModels()).thenReturn(deviceModels);
        
        // When & Then
        mockMvc.perform(get("/api/rental/device-models"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(3))
                .andExpect(jsonPath("$.data[0]").value("YX-Robot-Pro"));
        
        verify(deviceUtilizationService).getAllDeviceModels();
    }
    
    @Test
    void testGetRegions_ShouldReturnSuccess() throws Exception {
        // Given
        List<String> regions = Arrays.asList("北京", "上海", "广州", "深圳");
        when(deviceUtilizationService.getAllRegions()).thenReturn(regions);
        
        // When & Then
        mockMvc.perform(get("/api/rental/regions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(4))
                .andExpect(jsonPath("$.data[0]").value("北京"));
        
        verify(deviceUtilizationService).getAllRegions();
    }
    
    @Test
    void testGetTrendChartData_WithValidParams_ShouldReturnSuccess() throws Exception {
        // Given
        String period = "daily";
        String startDate = "2025-01-01";
        String endDate = "2025-01-31";
        
        Map<String, Object> chartData = new HashMap<>();
        chartData.put("categories", Arrays.asList("2025-01-01", "2025-01-02"));
        chartData.put("series", Arrays.asList(
            Map.of("name", "收入", "data", Arrays.asList(1000, 1200)),
            Map.of("name", "订单数", "data", Arrays.asList(5, 6))
        ));
        
        when(rentalAnalysisService.getTrendChartData(eq(period), any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(chartData);
        
        // When & Then
        mockMvc.perform(get("/api/rental/charts/trends")
                .param("period", period)
                .param("startDate", startDate)
                .param("endDate", endDate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.categories").isArray())
                .andExpect(jsonPath("$.data.series").isArray());
        
        verify(rentalAnalysisService).getTrendChartData(eq(period), any(LocalDate.class), any(LocalDate.class));
    }
    
    @Test
    void testGetTrendChartData_WithDefaultPeriod_ShouldReturnSuccess() throws Exception {
        // Given
        Map<String, Object> chartData = new HashMap<>();
        chartData.put("categories", Arrays.asList("2025-01-01"));
        chartData.put("series", Arrays.asList());
        
        when(rentalAnalysisService.getTrendChartData(eq("daily"), isNull(), isNull()))
            .thenReturn(chartData);
        
        // When & Then
        mockMvc.perform(get("/api/rental/charts/trends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        
        verify(rentalAnalysisService).getTrendChartData(eq("daily"), isNull(), isNull());
    }
    
    @Test
    void testGetDistributionData_WithValidParams_ShouldReturnSuccess() throws Exception {
        // Given
        String type = "region";
        Map<String, Object> chartData = new HashMap<>();
        chartData.put("categories", Arrays.asList("北京", "上海"));
        chartData.put("data", Arrays.asList(100, 80));
        
        when(rentalAnalysisService.getDistributionData(eq(type), any(), any()))
            .thenReturn(chartData);
        
        // When & Then
        mockMvc.perform(get("/api/rental/charts/distribution")
                .param("type", type))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.categories").isArray());
        
        verify(rentalAnalysisService).getDistributionData(eq(type), any(), any());
    }
    
    @Test
    void testGetUtilizationRankingData_WithValidLimit_ShouldReturnSuccess() throws Exception {
        // Given
        Integer limit = 12;
        Map<String, Object> chartData = new HashMap<>();
        chartData.put("categories", Arrays.asList("YX-001", "YX-002"));
        chartData.put("data", Arrays.asList(85.5, 72.3));
        
        when(rentalAnalysisService.getUtilizationRankingData(limit)).thenReturn(chartData);
        
        // When & Then
        mockMvc.perform(get("/api/rental/charts/utilization-ranking")
                .param("limit", "12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.categories").isArray());
        
        verify(rentalAnalysisService).getUtilizationRankingData(12);
    }
    
    @Test
    void testGetAllChartsData_ShouldReturnSuccess() throws Exception {
        // Given
        Map<String, Object> allChartsData = new HashMap<>();
        allChartsData.put("trends", Map.of("categories", Arrays.asList("2025-01-01")));
        allChartsData.put("distribution", Map.of("categories", Arrays.asList("北京")));
        allChartsData.put("ranking", Map.of("categories", Arrays.asList("YX-001")));
        
        when(rentalAnalysisService.getAllChartsData(eq("daily"), any(), any()))
            .thenReturn(allChartsData);
        
        // When & Then
        mockMvc.perform(get("/api/rental/charts/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.trends").exists())
                .andExpect(jsonPath("$.data.distribution").exists())
                .andExpect(jsonPath("$.data.ranking").exists());
        
        verify(rentalAnalysisService).getAllChartsData(eq("daily"), any(), any());
    }
    
    @Test
    void testBatchOperation_WithValidRequest_ShouldReturnSuccess() throws Exception {
        // Given
        Map<String, Object> batchRequest = new HashMap<>();
        batchRequest.put("ids", Arrays.asList("YX-001", "YX-002"));
        batchRequest.put("operation", "updateStatus");
        batchRequest.put("params", Map.of("status", "maintenance"));
        
        when(rentalFormValidator.validateBatchOperationForm(batchRequest)).thenReturn(new ArrayList<>());
        when(deviceUtilizationService.updateDeviceStatus("YX-001", "maintenance")).thenReturn(true);
        when(deviceUtilizationService.updateDeviceStatus("YX-002", "maintenance")).thenReturn(true);
        
        // When & Then
        mockMvc.perform(post("/api/rental/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(batchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.successCount").value(2))
                .andExpect(jsonPath("$.data.failCount").value(0))
                .andExpect(jsonPath("$.data.totalCount").value(2));
        
        verify(rentalFormValidator).validateBatchOperationForm(batchRequest);
        verify(deviceUtilizationService, times(2)).updateDeviceStatus(anyString(), eq("maintenance"));
    }
    
    @Test
    void testBatchOperation_WithValidationErrors_ShouldReturnBadRequest() throws Exception {
        // Given
        Map<String, Object> batchRequest = new HashMap<>();
        batchRequest.put("ids", Arrays.asList());
        batchRequest.put("operation", "invalid");
        
        List<String> validationErrors = Arrays.asList("批量操作ID列表不能为空", "不支持的操作类型");
        when(rentalFormValidator.validateBatchOperationForm(batchRequest)).thenReturn(validationErrors);
        
        // When & Then
        mockMvc.perform(post("/api/rental/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(batchRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.validationErrors").isArray())
                .andExpect(jsonPath("$.validationErrors.length()").value(2));
    }
    
    @Test
    void testBatchOperation_WithUnsupportedOperation_ShouldReturnBadRequest() throws Exception {
        // Given
        Map<String, Object> batchRequest = new HashMap<>();
        batchRequest.put("ids", Arrays.asList("YX-001"));
        batchRequest.put("operation", "unsupported");
        
        when(rentalFormValidator.validateBatchOperationForm(batchRequest)).thenReturn(new ArrayList<>());
        
        // When & Then
        mockMvc.perform(post("/api/rental/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(batchRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("不支持的操作类型: unsupported"));
    }
    
    @Test
    void testServiceException_ShouldReturnInternalServerError() throws Exception {
        // Given
        when(rentalStatsService.getRentalStats(any(), any()))
            .thenThrow(new RuntimeException("Service error"));
        
        // When & Then
        mockMvc.perform(get("/api/rental/stats"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("查询失败: Service error"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}