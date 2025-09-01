package com.yxrobot.service;

import com.yxrobot.dto.CustomerQueryDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * CustomerSearchOptimizationService单元测试类
 * 测试搜索优化服务的功能
 */
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class CustomerSearchOptimizationServiceTest {
    
    @Mock
    private CustomerService customerService;
    
    @InjectMocks
    private CustomerSearchOptimizationService searchOptimizationService;
    
    private CustomerQueryDTO testQueryDTO;
    
    @BeforeEach
    void setUp() {
        testQueryDTO = new CustomerQueryDTO();
        testQueryDTO.setKeyword("测试");
        testQueryDTO.setCustomerLevel("vip");
        testQueryDTO.setRegion("北京");
        testQueryDTO.setPage(1);
        testQueryDTO.setPageSize(20);
    }
    
    // ==================== 搜索查询优化测试 ====================
    
    @Test
    void testOptimizeSearchQuery_Success() {
        // When
        CustomerSearchOptimizationService.SearchOptimizationResult result = 
            searchOptimizationService.optimizeSearchQuery(testQueryDTO);
        
        // Then
        assertNotNull(result);
        assertNotNull(result.getOptimizedQuery());
        assertNotNull(result.getOptimizationTips());
        assertNotNull(result.getIndexSuggestions());
        
        // 验证优化建议
        assertFalse(result.getOptimizationTips().isEmpty());
        assertFalse(result.getIndexSuggestions().isEmpty());
    }
    
    @Test
    void testOptimizeSearchQuery_WithKeywordOptimization() {
        // Given
        testQueryDTO.setKeyword("  测试客户  "); // 包含多余空格
        
        // When
        CustomerSearchOptimizationService.SearchOptimizationResult result = 
            searchOptimizationService.optimizeSearchQuery(testQueryDTO);
        
        // Then
        assertEquals("测试客户", result.getOptimizedQuery().getKeyword());
        assertTrue(result.getOptimizationTips().stream()
            .anyMatch(tip -> tip.contains("已优化搜索关键词格式")));
    }
    
    @Test
    void testOptimizeSearchQuery_WithShortKeyword() {
        // Given
        testQueryDTO.setKeyword("a"); // 短关键词
        
        // When
        CustomerSearchOptimizationService.SearchOptimizationResult result = 
            searchOptimizationService.optimizeSearchQuery(testQueryDTO);
        
        // Then
        assertTrue(result.getOptimizationTips().stream()
            .anyMatch(tip -> tip.contains("建议使用2个字符以上的关键词")));
    }
    
    @Test
    void testOptimizeSearchQuery_WithAdvancedFilters() {
        // Given
        testQueryDTO.setMinCustomerValue("8.0");
        testQueryDTO.setTags("VIP,重要客户");
        
        // When
        CustomerSearchOptimizationService.SearchOptimizationResult result = 
            searchOptimizationService.optimizeSearchQuery(testQueryDTO);
        
        // Then
        assertTrue(result.getOptimizationTips().stream()
            .anyMatch(tip -> tip.contains("检测到高级筛选条件")));
    }
    
    @Test
    void testOptimizeSearchQuery_WithDefaultSorting() {
        // Given
        testQueryDTO.setSortBy(null); // 无排序字段
        
        // When
        CustomerSearchOptimizationService.SearchOptimizationResult result = 
            searchOptimizationService.optimizeSearchQuery(testQueryDTO);
        
        // Then
        assertEquals("lastActiveAt", result.getOptimizedQuery().getSortBy());
        assertEquals("DESC", result.getOptimizedQuery().getSortOrder());
        assertTrue(result.getOptimizationTips().stream()
            .anyMatch(tip -> tip.contains("已设置默认排序")));
    }
    
    @Test
    void testOptimizeSearchQuery_WithSecondarySorting() {
        // Given
        testQueryDTO.setSortBy("customerLevel");
        
        // When
        CustomerSearchOptimizationService.SearchOptimizationResult result = 
            searchOptimizationService.optimizeSearchQuery(testQueryDTO);
        
        // Then
        assertEquals("totalSpent", result.getOptimizedQuery().getSecondarySortBy());
        assertEquals("DESC", result.getOptimizedQuery().getSecondarySortOrder());
        assertTrue(result.getOptimizationTips().stream()
            .anyMatch(tip -> tip.contains("已添加消费金额作为次要排序条件")));
    }
    
    @Test
    void testOptimizeSearchQuery_WithLargePageSize() {
        // Given
        testQueryDTO.setPageSize(200); // 超大分页
        
        // When
        CustomerSearchOptimizationService.SearchOptimizationResult result = 
            searchOptimizationService.optimizeSearchQuery(testQueryDTO);
        
        // Then
        assertEquals(100, result.getOptimizedQuery().getPageSize());
        assertTrue(result.getOptimizationTips().stream()
            .anyMatch(tip -> tip.contains("已将分页大小限制为100")));
    }
    
    @Test
    void testOptimizeSearchQuery_WithDeepPagination() {
        // Given
        testQueryDTO.setPage(150); // 深度分页
        
        // When
        CustomerSearchOptimizationService.SearchOptimizationResult result = 
            searchOptimizationService.optimizeSearchQuery(testQueryDTO);
        
        // Then
        assertTrue(result.getOptimizationTips().stream()
            .anyMatch(tip -> tip.contains("深度分页可能影响性能")));
    }
    
    // ==================== 缓存策略测试 ====================
    
    @Test
    void testOptimizeSearchQuery_SimpleCacheStrategy() {
        // Given - 简单查询
        CustomerQueryDTO simpleQuery = new CustomerQueryDTO();
        simpleQuery.setKeyword("测试");
        
        // When
        CustomerSearchOptimizationService.SearchOptimizationResult result = 
            searchOptimizationService.optimizeSearchQuery(simpleQuery);
        
        // Then
        assertTrue(result.isUseCache());
        assertNotNull(result.getCacheKey());
        assertTrue(result.getOptimizationTips().stream()
            .anyMatch(tip -> tip.contains("已启用查询结果缓存")));
    }
    
    @Test
    void testOptimizeSearchQuery_NoCache() {
        // Given - 复杂查询
        testQueryDTO.setMinCustomerValue("8.0");
        testQueryDTO.setRegisteredStartDate("2023-01-01");
        
        // When
        CustomerSearchOptimizationService.SearchOptimizationResult result = 
            searchOptimizationService.optimizeSearchQuery(testQueryDTO);
        
        // Then
        assertFalse(result.isUseCache());
    }
    
    // ==================== 索引建议测试 ====================
    
    @Test
    void testOptimizeSearchQuery_IndexSuggestions() {
        // Given
        testQueryDTO.setCustomerLevel("vip");
        testQueryDTO.setRegion("北京");
        testQueryDTO.setRegisteredStartDate("2023-01-01");
        testQueryDTO.setMinSpent("10000");
        
        // When
        CustomerSearchOptimizationService.SearchOptimizationResult result = 
            searchOptimizationService.optimizeSearchQuery(testQueryDTO);
        
        // Then
        List<String> suggestions = result.getIndexSuggestions();
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("customer_level")));
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("region")));
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("时间字段")));
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("金额字段")));
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("复合索引")));
    }
    
    // ==================== 智能搜索建议测试 ====================
    
    @Test
    void testGetIntelligentSuggestions_Success() {
        // Given
        String keyword = "测试";
        Integer limit = 10;
        
        List<Map<String, Object>> mockCustomerSuggestions = Arrays.asList(
            Map.of("name", "测试客户1"),
            Map.of("name", "测试客户2")
        );
        when(customerService.getSearchSuggestions(keyword, 5)).thenReturn(mockCustomerSuggestions);
        
        // When
        List<String> result = searchOptimizationService.getIntelligentSuggestions(keyword, limit);
        
        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(customerService).getSearchSuggestions(keyword, 5);
    }
    
    @Test
    void testGetIntelligentSuggestions_ShortKeyword() {
        // Given
        String keyword = "a"; // 短关键词
        Integer limit = 10;
        
        // When
        List<String> result = searchOptimizationService.getIntelligentSuggestions(keyword, limit);
        
        // Then
        assertNotNull(result);
        // 短关键词应该返回热门关键词
        verify(customerService, never()).getSearchSuggestions(anyString(), anyInt());
    }
    
    @Test
    void testGetIntelligentSuggestions_EmptyKeyword() {
        // Given
        String keyword = "";
        Integer limit = 10;
        
        // When
        List<String> result = searchOptimizationService.getIntelligentSuggestions(keyword, limit);
        
        // Then
        assertNotNull(result);
        // 空关键词应该返回热门关键词
        verify(customerService, never()).getSearchSuggestions(anyString(), anyInt());
    }
    
    // ==================== 搜索性能监控测试 ====================
    
    @Test
    void testRecordSearchPerformance_Success() {
        // Given
        String searchType = "customer_list";
        long executionTime = 150L;
        int resultCount = 25;
        
        // When
        searchOptimizationService.recordSearchPerformance(searchType, executionTime, resultCount);
        
        // Then
        Map<String, CustomerSearchOptimizationService.SearchPerformanceStats> stats = 
            searchOptimizationService.getSearchPerformanceStats();
        
        assertNotNull(stats);
        assertTrue(stats.containsKey(searchType));
        
        CustomerSearchOptimizationService.SearchPerformanceStats stat = stats.get(searchType);
        assertEquals(1L, stat.getTotalSearches());
        assertEquals(executionTime, stat.getTotalExecutionTime());
        assertEquals(executionTime, stat.getAverageExecutionTime());
        assertEquals(resultCount, stat.getResultCount());
    }
    
    @Test
    void testRecordSearchPerformance_SlowQuery() {
        // Given
        String searchType = "slow_search";
        long executionTime = 6000L; // 超过5秒的慢查询
        int resultCount = 100;
        
        // When & Then - 应该记录警告日志
        assertDoesNotThrow(() -> {
            searchOptimizationService.recordSearchPerformance(searchType, executionTime, resultCount);
        });
    }
    
    @Test
    void testGetSearchPerformanceReport_Success() {
        // Given - 先记录一些性能数据
        searchOptimizationService.recordSearchPerformance("search1", 100L, 10);
        searchOptimizationService.recordSearchPerformance("search2", 200L, 20);
        searchOptimizationService.recordSearchPerformance("search1", 150L, 15);
        
        // When
        Map<String, Object> report = searchOptimizationService.getSearchPerformanceReport();
        
        // Then
        assertNotNull(report);
        assertTrue(report.containsKey("totalSearches"));
        assertTrue(report.containsKey("averageExecutionTime"));
        assertTrue(report.containsKey("searchTypes"));
        assertTrue(report.containsKey("detailStats"));
        assertTrue(report.containsKey("performanceSuggestions"));
        
        assertEquals(3L, report.get("totalSearches"));
        assertEquals(2, report.get("searchTypes"));
    }
    
    // ==================== 热门关键词管理测试 ====================
    
    @Test
    void testGetPopularKeywords_Success() {
        // Given - 先记录一些搜索
        CustomerQueryDTO query1 = new CustomerQueryDTO();
        query1.setKeyword("VIP客户");
        CustomerQueryDTO query2 = new CustomerQueryDTO();
        query2.setKeyword("北京");
        CustomerQueryDTO query3 = new CustomerQueryDTO();
        query3.setKeyword("VIP客户"); // 重复搜索
        
        searchOptimizationService.optimizeSearchQuery(query1);
        searchOptimizationService.optimizeSearchQuery(query2);
        searchOptimizationService.optimizeSearchQuery(query3);
        
        // When
        List<String> keywords = searchOptimizationService.getPopularKeywords(10);
        
        // Then
        assertNotNull(keywords);
        assertFalse(keywords.isEmpty());
        // VIP客户应该排在前面（搜索次数更多）
        assertEquals("vip客户", keywords.get(0));
    }
    
    @Test
    void testGetPopularKeywords_WithLimit() {
        // Given
        Integer limit = 5;
        
        // When
        List<String> keywords = searchOptimizationService.getPopularKeywords(limit);
        
        // Then
        assertNotNull(keywords);
        assertTrue(keywords.size() <= limit);
    }
    
    // ==================== 缓存管理测试 ====================
    
    @Test
    void testClearCache_Success() {
        // Given - 先生成一些缓存
        searchOptimizationService.getIntelligentSuggestions("测试", 10);
        
        // When
        assertDoesNotThrow(() -> {
            searchOptimizationService.clearCache();
        });
        
        // Then - 缓存应该被清理
        // 这里主要测试方法不抛异常
    }
    
    @Test
    void testClearPerformanceStats_Success() {
        // Given - 先记录一些性能数据
        searchOptimizationService.recordSearchPerformance("test", 100L, 10);
        
        // When
        searchOptimizationService.clearPerformanceStats();
        
        // Then
        Map<String, CustomerSearchOptimizationService.SearchPerformanceStats> stats = 
            searchOptimizationService.getSearchPerformanceStats();
        assertTrue(stats.isEmpty());
        
        List<String> keywords = searchOptimizationService.getPopularKeywords(10);
        assertTrue(keywords.isEmpty());
    }
    
    // ==================== 异常处理测试 ====================
    
    @Test
    void testOptimizeSearchQuery_NullQuery() {
        // When & Then
        assertDoesNotThrow(() -> {
            CustomerSearchOptimizationService.SearchOptimizationResult result = 
                searchOptimizationService.optimizeSearchQuery(null);
            assertNotNull(result);
        });
    }
    
    @Test
    void testGetIntelligentSuggestions_ServiceException() {
        // Given
        when(customerService.getSearchSuggestions(anyString(), anyInt()))
            .thenThrow(new RuntimeException("Service error"));
        
        // When
        List<String> result = searchOptimizationService.getIntelligentSuggestions("测试", 10);
        
        // Then
        assertNotNull(result);
        // 应该返回热门关键词作为备选
    }
    
    @Test
    void testRecordSearchPerformance_Exception() {
        // When & Then - 异常情况下不应该抛出异常
        assertDoesNotThrow(() -> {
            searchOptimizationService.recordSearchPerformance(null, 100L, 10);
        });
    }
}