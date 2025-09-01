package com.yxrobot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yxrobot.dto.*;
import com.yxrobot.entity.Customer;
import com.yxrobot.service.*;
import com.yxrobot.exception.CustomerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * CustomerController单元测试类
 * 测试客户管理API接口
 */
@WebMvcTest(CustomerController.class)
@ActiveProfiles("test")
public class CustomerControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private CustomerService customerService;
    
    @MockBean
    private CustomerStatsService customerStatsService;
    
    @MockBean
    private CustomerDeviceService customerDeviceService;
    
    @MockBean
    private CustomerOrderService customerOrderService;
    
    @MockBean
    private CustomerServiceRecordService customerServiceRecordService;
    
    @MockBean
    private CustomerValidationService customerValidationService;
    
    @MockBean
    private CustomerApiValidationService apiValidationService;
    
    @MockBean
    private CustomerQueryOptimizationService queryOptimizationService;
    
    @MockBean
    private CustomerSearchOptimizationService searchOptimizationService;
    
    private CustomerDTO testCustomerDTO;
    private CustomerStatsDTO testStatsDTO;
    private CustomerCreateDTO testCreateDTO;
    private CustomerUpdateDTO testUpdateDTO;
    
    @BeforeEach
    void setUp() {
        setupTestData();
        setupMockBehaviors();
    }  
  private void setupTestData() {
        // 设置测试客户DTO
        testCustomerDTO = new CustomerDTO();
        testCustomerDTO.setId(1L);
        testCustomerDTO.setCustomerName("测试客户");
        testCustomerDTO.setPhone("13800138001");
        testCustomerDTO.setEmail("test@example.com");
        testCustomerDTO.setCustomerLevel("vip");
        testCustomerDTO.setTotalSpent(new BigDecimal("50000.00"));
        testCustomerDTO.setCustomerValue(new BigDecimal("8.5"));
        
        // 设置测试统计DTO
        testStatsDTO = new CustomerStatsDTO();
        testStatsDTO.setTotal(100);
        testStatsDTO.setRegular(60);
        testStatsDTO.setVip(30);
        testStatsDTO.setPremium(10);
        testStatsDTO.setActiveDevices(150);
        testStatsDTO.setTotalRevenue(new BigDecimal("1000000.00"));
        testStatsDTO.setNewThisMonth(15);
        
        // 设置测试创建DTO
        testCreateDTO = new CustomerCreateDTO();
        testCreateDTO.setCustomerName("新客户");
        testCreateDTO.setPhone("13900139001");
        testCreateDTO.setEmail("newcustomer@example.com");
        
        // 设置测试更新DTO
        testUpdateDTO = new CustomerUpdateDTO();
        testUpdateDTO.setCustomerName("更新客户");
        testUpdateDTO.setPhone("13900139002");
    }
    
    private void setupMockBehaviors() {
        // 设置默认的Mock行为
        CustomerApiValidationService.ValidationResult validationResult = 
            new CustomerApiValidationService.ValidationResult();
        when(apiValidationService.validateQueryParameters(any())).thenReturn(validationResult);
        when(apiValidationService.validateCreateParameters(any())).thenReturn(validationResult);
        
        CustomerSearchOptimizationService.SearchOptimizationResult searchResult = 
            new CustomerSearchOptimizationService.SearchOptimizationResult(new CustomerQueryDTO());
        when(searchOptimizationService.optimizeSearchQuery(any())).thenReturn(searchResult);
        
        CustomerQueryOptimizationService.OptimizedQuery optimizedQuery = 
            new CustomerQueryOptimizationService.OptimizedQuery(new CustomerQueryDTO());
        when(queryOptimizationService.optimizeCustomerQuery(any())).thenReturn(optimizedQuery);
        
        CustomerValidationService.ValidationSummary validationSummary = 
            new CustomerValidationService.ValidationSummary();
        validationSummary.setValid(true);
        when(customerValidationService.validateCustomerCreate(any())).thenReturn(validationSummary);
        when(customerValidationService.validateCustomerUpdate(any(), any())).thenReturn(validationSummary);
    }
    
    // ==================== 客户列表查询接口测试 ====================
    
    @Test
    void testGetCustomers_Success() throws Exception {
        // Given
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("list", Arrays.asList(testCustomerDTO));
        mockResult.put("total", 1L);
        mockResult.put("page", 1);
        mockResult.put("pageSize", 20);
        mockResult.put("isEmpty", false);
        
        when(customerService.getCustomers(any(CustomerQueryDTO.class))).thenReturn(mockResult);
        
        // When & Then
        mockMvc.perform(get("/api/admin/customers")
                .param("page", "1")
                .param("pageSize", "20")
                .param("keyword", "测试")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list").isArray())
                .andExpect(jsonPath("$.data.list[0].customerName").value("测试客户"));
        
        verify(customerService).getCustomers(any(CustomerQueryDTO.class));
    }
    
    @Test
    void testGetCustomers_EmptyResult() throws Exception {
        // Given
        Map<String, Object> emptyResult = new HashMap<>();
        emptyResult.put("list", Collections.emptyList());
        emptyResult.put("total", 0L);
        emptyResult.put("page", 1);
        emptyResult.put("pageSize", 20);
        emptyResult.put("isEmpty", true);
        
        when(customerService.getCustomers(any(CustomerQueryDTO.class))).thenReturn(emptyResult);
        
        // When & Then
        mockMvc.perform(get("/api/admin/customers")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(0))
                .andExpect(jsonPath("$.data.isEmpty").value(true));
    }
    
    @Test
    void testGetCustomers_ValidationError() throws Exception {
        // Given
        CustomerApiValidationService.ValidationResult errorResult = 
            new CustomerApiValidationService.ValidationResult();
        errorResult.addError("页码必须大于0");
        when(apiValidationService.validateQueryParameters(any())).thenReturn(errorResult);
        
        // When & Then
        mockMvc.perform(get("/api/admin/customers")
                .param("page", "0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("参数验证失败"));
    }
    
    @Test
    void testGetCustomer_Success() throws Exception {
        // Given
        Long customerId = 1L;
        when(customerService.getCustomerById(customerId)).thenReturn(testCustomerDTO);
        
        // When & Then
        mockMvc.perform(get("/api/admin/customers/{id}", customerId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.customerName").value("测试客户"))
                .andExpect(jsonPath("$.data.phone").value("13800138001"));
        
        verify(customerService).getCustomerById(customerId);
    }
    
    @Test
    void testGetCustomer_NotFound() throws Exception {
        // Given
        Long customerId = 999L;
        when(customerService.getCustomerById(customerId)).thenReturn(null);
        
        // When & Then
        mockMvc.perform(get("/api/admin/customers/{id}", customerId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("客户不存在"));
    }    
// ==================== 客户统计接口测试 ====================
    
    @Test
    void testGetCustomerStats_Success() throws Exception {
        // Given
        when(customerStatsService.getCustomerStats()).thenReturn(testStatsDTO);
        
        // When & Then
        mockMvc.perform(get("/api/admin/customers/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(100))
                .andExpect(jsonPath("$.data.vip").value(30))
                .andExpect(jsonPath("$.data.activeDevices").value(150));
        
        verify(customerStatsService).getCustomerStats();
    }
    
    @Test
    void testGetCustomerStats_ServiceException() throws Exception {
        // Given
        when(customerStatsService.getCustomerStats())
            .thenThrow(new RuntimeException("统计服务异常"));
        
        // When & Then
        mockMvc.perform(get("/api/admin/customers/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").exists());
    }
    
    // ==================== 客户CRUD接口测试 ====================
    
    @Test
    void testCreateCustomer_Success() throws Exception {
        // Given
        Customer mockCustomer = new Customer();
        mockCustomer.setId(1L);
        when(customerService.createCustomerFromDTO(any())).thenReturn(mockCustomer);
        when(customerService.getCustomerById(1L)).thenReturn(testCustomerDTO);
        
        // When & Then
        mockMvc.perform(post("/api/admin/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCreateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("客户创建成功"))
                .andExpect(jsonPath("$.data.customerName").value("测试客户"));
        
        verify(customerService).createCustomerFromDTO(any());
    }
    
    @Test
    void testCreateCustomer_ValidationError() throws Exception {
        // Given
        CustomerValidationService.ValidationSummary errorSummary = 
            new CustomerValidationService.ValidationSummary();
        errorSummary.setValid(false);
        errorSummary.addError("客户姓名不能为空");
        when(customerValidationService.validateCustomerCreate(any())).thenReturn(errorSummary);
        
        // When & Then
        mockMvc.perform(post("/api/admin/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCreateDTO)))
                .andExpected(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("数据验证失败"));
    }
    
    @Test
    void testCreateCustomer_BusinessException() throws Exception {
        // Given
        when(customerService.createCustomerFromDTO(any()))
            .thenThrow(new CustomerException("客户姓名已存在", "DUPLICATE_NAME"));
        
        // When & Then
        mockMvc.perform(post("/api/admin/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCreateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.errorCode").value("DUPLICATE_NAME"));
    }
    
    @Test
    void testUpdateCustomer_Success() throws Exception {
        // Given
        Long customerId = 1L;
        Customer mockCustomer = new Customer();
        mockCustomer.setId(customerId);
        when(customerService.updateCustomerFromDTO(eq(customerId), any())).thenReturn(mockCustomer);
        when(customerService.getCustomerById(customerId)).thenReturn(testCustomerDTO);
        
        // When & Then
        mockMvc.perform(put("/api/admin/customers/{id}", customerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("客户更新成功"));
        
        verify(customerService).updateCustomerFromDTO(eq(customerId), any());
    }
    
    @Test
    void testDeleteCustomer_Success() throws Exception {
        // Given
        Long customerId = 1L;
        doNothing().when(customerService).deleteCustomer(customerId);
        
        // When & Then
        mockMvc.perform(delete("/api/admin/customers/{id}", customerId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("客户删除成功"));
        
        verify(customerService).deleteCustomer(customerId);
    }
    
    @Test
    void testDeleteCustomer_ServiceException() throws Exception {
        // Given
        Long customerId = 1L;
        doThrow(new RuntimeException("删除失败")).when(customerService).deleteCustomer(customerId);
        
        // When & Then
        mockMvc.perform(delete("/api/admin/customers/{id}", customerId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").exists());
    }    // 
==================== 搜索和筛选接口测试 ====================
    
    @Test
    void testSearchCustomers_Success() throws Exception {
        // Given
        String keyword = "测试";
        List<CustomerDTO> mockResults = Arrays.asList(testCustomerDTO);
        when(customerService.searchCustomers(keyword, 10)).thenReturn(mockResults);
        
        // When & Then
        mockMvc.perform(get("/api/admin/customers/search")
                .param("keyword", keyword)
                .param("limit", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].customerName").value("测试客户"));
        
        verify(customerService).searchCustomers(keyword, 10);
    }
    
    @Test
    void testGetFilterOptions_Success() throws Exception {
        // Given
        List<Map<String, Object>> levelOptions = Arrays.asList(
            Map.of("value", "vip", "label", "VIP客户", "count", 30)
        );
        when(customerService.getCustomerLevelOptions()).thenReturn(levelOptions);
        when(customerService.getRegionOptions()).thenReturn(Collections.emptyList());
        when(customerService.getIndustryOptions()).thenReturn(Collections.emptyList());
        
        // When & Then
        mockMvc.perform(get("/api/admin/customers/filter-options")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.levels").isArray())
                .andExpect(jsonPath("$.data.regions").isArray())
                .andExpect(jsonPath("$.data.industries").isArray());
    }
    
    @Test
    void testAdvancedSearch_Success() throws Exception {
        // Given
        CustomerQueryDTO queryDTO = new CustomerQueryDTO();
        queryDTO.setKeyword("测试");
        queryDTO.setCustomerLevel("vip");
        
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("list", Arrays.asList(testCustomerDTO));
        mockResult.put("total", 1L);
        
        when(customerService.advancedSearch(any(CustomerQueryDTO.class))).thenReturn(mockResult);
        
        // When & Then
        mockMvc.perform(post("/api/admin/customers/advanced-search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(queryDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1));
        
        verify(customerService).advancedSearch(any(CustomerQueryDTO.class));
    }
    
    @Test
    void testGetSearchSuggestions_Success() throws Exception {
        // Given
        String keyword = "测试";
        List<Map<String, Object>> suggestions = Arrays.asList(
            Map.of("id", 1L, "name", "测试客户", "phone", "13800138001")
        );
        when(customerService.getSearchSuggestions(keyword, 10)).thenReturn(suggestions);
        
        // When & Then
        mockMvc.perform(get("/api/admin/customers/search-suggestions")
                .param("keyword", keyword)
                .param("limit", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("测试客户"));
    }
    
    @Test
    void testGetIntelligentSuggestions_Success() throws Exception {
        // Given
        String keyword = "测试";
        List<String> suggestions = Arrays.asList("测试客户1", "测试客户2");
        when(searchOptimizationService.getIntelligentSuggestions(keyword, 10))
            .thenReturn(suggestions);
        
        // When & Then
        mockMvc.perform(get("/api/admin/customers/intelligent-suggestions")
                .param("keyword", keyword)
                .param("limit", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0]").value("测试客户1"));
    }
    
    @Test
    void testGetPopularKeywords_Success() throws Exception {
        // Given
        List<String> keywords = Arrays.asList("VIP客户", "北京", "科技公司");
        when(searchOptimizationService.getPopularKeywords(10)).thenReturn(keywords);
        
        // When & Then
        mockMvc.perform(get("/api/admin/customers/popular-keywords")
                .param("limit", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0]").value("VIP客户"));
    }
    
    // ==================== 关联数据接口测试 ====================
    
    @Test
    void testGetCustomerDevices_Success() throws Exception {
        // Given
        Long customerId = 1L;
        List<CustomerDeviceService.DeviceInfo> devices = Arrays.asList(
            new CustomerDeviceService.DeviceInfo()
        );
        when(customerDeviceService.getCustomerDevices(customerId)).thenReturn(devices);
        
        // When & Then
        mockMvc.perform(get("/api/admin/customers/{id}/devices", customerId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
        
        verify(customerDeviceService).getCustomerDevices(customerId);
    }
    
    @Test
    void testGetCustomerOrders_Success() throws Exception {
        // Given
        Long customerId = 1L;
        List<CustomerOrderService.OrderInfo> orders = Arrays.asList(
            new CustomerOrderService.OrderInfo()
        );
        when(customerOrderService.getCustomerOrders(customerId)).thenReturn(orders);
        
        // When & Then
        mockMvc.perform(get("/api/admin/customers/{id}/orders", customerId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
        
        verify(customerOrderService).getCustomerOrders(customerId);
    }
    
    @Test
    void testGetCustomerServiceRecords_Success() throws Exception {
        // Given
        Long customerId = 1L;
        List<CustomerServiceRecordService.ServiceRecordInfo> records = Arrays.asList(
            new CustomerServiceRecordService.ServiceRecordInfo()
        );
        when(customerServiceRecordService.getCustomerServiceRecords(customerId))
            .thenReturn(records);
        
        // When & Then
        mockMvc.perform(get("/api/admin/customers/{id}/service-records", customerId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
        
        verify(customerServiceRecordService).getCustomerServiceRecords(customerId);
    }
}    // =
=================== 性能监控接口测试 ====================
    
    @Test
    void testGetSearchPerformanceReport_Success() throws Exception {
        // Given
        Map<String, Object> report = Map.of(
            "totalSearches", 1000L,
            "averageExecutionTime", 150L,
            "searchTypes", 5
        );
        when(searchOptimizationService.getSearchPerformanceReport()).thenReturn(report);
        
        // When & Then
        mockMvc.perform(get("/api/admin/customers/performance-report")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalSearches").value(1000))
                .andExpect(jsonPath("$.data.averageExecutionTime").value(150));
    }
    
    @Test
    void testClearSearchCache_Success() throws Exception {
        // Given
        doNothing().when(searchOptimizationService).clearCache();
        
        // When & Then
        mockMvc.perform(post("/api/admin/customers/clear-search-cache")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("搜索缓存清理成功"));
        
        verify(searchOptimizationService).clearCache();
    }
    
    @Test
    void testGetSearchOptimizationAdvice_Success() throws Exception {
        // Given
        CustomerSearchOptimizationService.SearchOptimizationResult optimization = 
            new CustomerSearchOptimizationService.SearchOptimizationResult(new CustomerQueryDTO());
        optimization.getOptimizationTips().add("建议使用索引");
        optimization.getIndexSuggestions().add("创建复合索引");
        
        when(searchOptimizationService.optimizeSearchQuery(any())).thenReturn(optimization);
        when(searchOptimizationService.getSearchPerformanceReport()).thenReturn(Map.of());
        
        // When & Then
        mockMvc.perform(get("/api/admin/customers/optimization-advice")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.optimizationTips").isArray())
                .andExpect(jsonPath("$.data.indexSuggestions").isArray());
    }
    
    // ==================== 错误处理测试 ====================
    
    @Test
    void testGetCustomers_InternalServerError() throws Exception {
        // Given
        when(customerService.getCustomers(any(CustomerQueryDTO.class)))
            .thenThrow(new RuntimeException("Internal server error"));
        
        // When & Then
        mockMvc.perform(get("/api/admin/customers")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").exists());
    }
    
    @Test
    void testCreateCustomer_InvalidJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/admin/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid json"))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testGetCustomer_InvalidId() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/admin/customers/invalid")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    
    // ==================== 边界条件测试 ====================
    
    @Test
    void testGetCustomers_MaxPageSize() throws Exception {
        // Given
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("list", Collections.emptyList());
        mockResult.put("total", 0L);
        mockResult.put("page", 1);
        mockResult.put("pageSize", 100);
        mockResult.put("isEmpty", true);
        
        when(customerService.getCustomers(any(CustomerQueryDTO.class))).thenReturn(mockResult);
        
        // When & Then
        mockMvc.perform(get("/api/admin/customers")
                .param("pageSize", "100")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.pageSize").value(100));
    }
    
    @Test
    void testSearchCustomers_EmptyKeyword() throws Exception {
        // Given
        when(customerService.searchCustomers("", 10)).thenReturn(Collections.emptyList());
        
        // When & Then
        mockMvc.perform(get("/api/admin/customers/search")
                .param("keyword", "")
                .param("limit", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }
    
    @Test
    void testGetCustomersByTags_Success() throws Exception {
        // Given
        String tags = "VIP,重要客户";
        List<CustomerDTO> mockResults = Arrays.asList(testCustomerDTO);
        when(customerService.getCustomersByTags(eq(tags), any(CustomerQueryDTO.class)))
            .thenReturn(mockResults);
        
        // When & Then
        mockMvc.perform(get("/api/admin/customers/by-tags")
                .param("tags", tags)
                .param("page", "1")
                .param("pageSize", "20")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list").isArray())
                .andExpect(jsonPath("$.data.total").value(1));
    }
    
    @Test
    void testGetCustomersByDateRange_Success() throws Exception {
        // Given
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("list", Arrays.asList(testCustomerDTO));
        mockResult.put("total", 1L);
        
        when(customerService.getCustomers(any(CustomerQueryDTO.class))).thenReturn(mockResult);
        
        // When & Then
        mockMvc.perform(get("/api/admin/customers/by-date-range")
                .param("startDate", "2023-01-01")
                .param("endDate", "2023-12-31")
                .param("dateType", "registered")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1));
    }
}