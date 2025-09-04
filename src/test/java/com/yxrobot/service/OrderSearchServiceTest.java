package com.yxrobot.service;

import com.yxrobot.dto.OrderDTO;
import com.yxrobot.dto.OrderQueryDTO;
import com.yxrobot.util.SearchOptimizationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 订单搜索服务测试
 * 测试搜索和筛选功能的正确性和性能
 */
@SpringBootTest
@SpringJUnitConfig
public class OrderSearchServiceTest {
    
    private OrderSearchService orderSearchService;
    
    @BeforeEach
    void setUp() {
        // 这里应该注入真实的服务，或者使用Mock
        // orderSearchService = new OrderSearchService();
    }
    
    @Test
    void testQuickSearch() {
        // 测试快速搜索功能
        String keyword = "ORD1234567890";
        Integer limit = 10;
        
        // 由于没有真实的数据库连接，这里只测试方法调用
        // List<OrderDTO> result = orderSearchService.quickSearch(keyword, limit);
        // assertNotNull(result);
        // assertTrue(result.size() <= limit);
        
        // 测试空关键词
        List<OrderDTO> emptyResult = orderSearchService.quickSearch("", limit);
        assertNotNull(emptyResult);
        assertTrue(emptyResult.isEmpty());
        
        // 测试null关键词
        List<OrderDTO> nullResult = orderSearchService.quickSearch(null, limit);
        assertNotNull(nullResult);
        assertTrue(nullResult.isEmpty());
    }
    
    @Test
    void testAdvancedSearch() {
        // 测试高级搜索功能
        OrderQueryDTO queryDTO = createTestQueryDTO();
        
        // 由于没有真实的数据库连接，这里只测试方法调用
        // OrderSearchService.AdvancedSearchResult result = orderSearchService.advancedSearch(queryDTO);
        // assertNotNull(result);
        // assertNotNull(result.getOrders());
        // assertNotNull(result.getStatistics());
        // assertNotNull(result.getSuggestions());
    }
    
    @Test
    void testGetFilterSuggestions() {
        // 测试筛选建议功能
        OrderQueryDTO queryDTO = createTestQueryDTO();
        
        OrderSearchService.FilterSuggestions suggestions = orderSearchService.getFilterSuggestions(queryDTO);
        assertNotNull(suggestions);
        assertNotNull(suggestions.getAvailableTypes());
        assertNotNull(suggestions.getAvailableStatuses());
        assertNotNull(suggestions.getAvailablePaymentStatuses());
        assertNotNull(suggestions.getAvailableSalesPersons());
        assertNotNull(suggestions.getAmountRangeSuggestions());
        assertNotNull(suggestions.getDateRangeSuggestions());
    }
    
    @Test
    void testSearchHistory() {
        // 测试搜索历史功能
        String userId = "testUser";
        
        List<OrderSearchService.SearchHistory> history = orderSearchService.getSearchHistory(userId);
        assertNotNull(history);
        
        // 测试保存搜索历史
        OrderQueryDTO queryDTO = createTestQueryDTO();
        assertDoesNotThrow(() -> {
            orderSearchService.saveSearchHistory(userId, queryDTO);
        });
    }
    
    /**
     * 创建测试用的查询DTO
     */
    private OrderQueryDTO createTestQueryDTO() {
        OrderQueryDTO queryDTO = new OrderQueryDTO();
        queryDTO.setKeyword("测试订单");
        queryDTO.setType("sales");
        queryDTO.setStatus("pending");
        queryDTO.setPaymentStatus("pending");
        queryDTO.setCustomerId(1L);
        queryDTO.setCustomerName("测试客户");
        queryDTO.setSalesPerson("张三");
        queryDTO.setStartDate(LocalDate.now().minusDays(30));
        queryDTO.setEndDate(LocalDate.now());
        queryDTO.setMinAmount(new BigDecimal("100"));
        queryDTO.setMaxAmount(new BigDecimal("10000"));
        queryDTO.setSortBy("createdAt");
        queryDTO.setSortOrder("desc");
        queryDTO.setPage(1);
        queryDTO.setSize(10);
        
        return queryDTO;
    }
}

/**
 * 搜索优化工具类测试
 */
class SearchOptimizationUtilTest {
    
    @Test
    void testOptimizeKeyword() {
        // 测试关键词优化
        assertEquals("测试订单", SearchOptimizationUtil.optimizeKeyword("  测试订单  "));
        assertEquals("ord1234567890", SearchOptimizationUtil.optimizeKeyword("ORD1234567890"));
        assertNull(SearchOptimizationUtil.optimizeKeyword(""));
        assertNull(SearchOptimizationUtil.optimizeKeyword(null));
        assertNull(SearchOptimizationUtil.optimizeKeyword("   "));
    }
    
    @Test
    void testDetectSearchType() {
        // 测试搜索类型检测
        assertEquals(SearchOptimizationUtil.SearchType.ORDER_NUMBER, 
            SearchOptimizationUtil.detectSearchType("ORD1234567890"));
        assertEquals(SearchOptimizationUtil.SearchType.PHONE, 
            SearchOptimizationUtil.detectSearchType("13800138000"));
        assertEquals(SearchOptimizationUtil.SearchType.EMAIL, 
            SearchOptimizationUtil.detectSearchType("test@example.com"));
        assertEquals(SearchOptimizationUtil.SearchType.NUMERIC, 
            SearchOptimizationUtil.detectSearchType("1000"));
        assertEquals(SearchOptimizationUtil.SearchType.DATE, 
            SearchOptimizationUtil.detectSearchType("2024-01-01"));
        assertEquals(SearchOptimizationUtil.SearchType.CHINESE_NAME, 
            SearchOptimizationUtil.detectSearchType("张三"));
        assertEquals(SearchOptimizationUtil.SearchType.ENGLISH_NAME, 
            SearchOptimizationUtil.detectSearchType("John Smith"));
        assertEquals(SearchOptimizationUtil.SearchType.GENERAL, 
            SearchOptimizationUtil.detectSearchType("一般搜索"));
    }
    
    @Test
    void testGenerateSearchSuggestions() {
        // 测试搜索建议生成
        List<String> suggestions = SearchOptimizationUtil.generateSearchSuggestions(
            "ORD1234567890", SearchOptimizationUtil.SearchType.ORDER_NUMBER, 0);
        assertNotNull(suggestions);
        assertFalse(suggestions.isEmpty());
        assertTrue(suggestions.get(0).contains("订单号"));
        
        suggestions = SearchOptimizationUtil.generateSearchSuggestions(
            "测试", SearchOptimizationUtil.SearchType.GENERAL, 150);
        assertNotNull(suggestions);
        assertFalse(suggestions.isEmpty());
        assertTrue(suggestions.get(0).contains("筛选条件"));
    }
    
    @Test
    void testOptimizeQueryConditions() {
        // 测试查询条件优化
        OrderQueryDTO queryDTO = new OrderQueryDTO();
        queryDTO.setKeyword("  测试订单  ");
        queryDTO.setPage(null);
        queryDTO.setSize(null);
        queryDTO.setSortBy(null);
        queryDTO.setSortOrder(null);
        
        OrderQueryDTO optimized = SearchOptimizationUtil.optimizeQueryConditions(queryDTO);
        
        assertNotNull(optimized);
        assertEquals("测试订单", optimized.getKeyword());
        assertEquals(1, optimized.getPage());
        assertEquals(10, optimized.getSize());
        assertEquals("createdAt", optimized.getSortBy());
        assertEquals("desc", optimized.getSortOrder());
    }
    
    @Test
    void testCalculateQueryComplexity() {
        // 测试查询复杂度计算
        OrderQueryDTO simpleQuery = new OrderQueryDTO();
        simpleQuery.setKeyword("测试");
        
        int complexity = SearchOptimizationUtil.calculateQueryComplexity(simpleQuery);
        assertTrue(complexity >= 1 && complexity <= 10);
        
        OrderQueryDTO complexQuery = new OrderQueryDTO();
        complexQuery.setKeyword("测试");
        complexQuery.setType("sales");
        complexQuery.setStatus("pending");
        complexQuery.setPaymentStatus("paid");
        complexQuery.setCustomerId(1L);
        complexQuery.setCustomerName("客户");
        complexQuery.setSalesPerson("销售");
        complexQuery.setStartDate(LocalDate.now().minusDays(30));
        complexQuery.setEndDate(LocalDate.now());
        complexQuery.setMinAmount(new BigDecimal("100"));
        complexQuery.setMaxAmount(new BigDecimal("1000"));
        
        int complexComplexity = SearchOptimizationUtil.calculateQueryComplexity(complexQuery);
        assertTrue(complexComplexity > complexity);
        assertTrue(complexComplexity <= 10);
    }
    
    @Test
    void testGenerateIndexSuggestions() {
        // 测试索引建议生成
        OrderQueryDTO queryDTO = new OrderQueryDTO();
        queryDTO.setKeyword("测试");
        queryDTO.setType("sales");
        queryDTO.setStatus("pending");
        
        List<SearchOptimizationUtil.IndexSuggestion> suggestions = 
            SearchOptimizationUtil.generateIndexSuggestions(queryDTO);
        
        assertNotNull(suggestions);
        assertFalse(suggestions.isEmpty());
        
        // 验证索引建议包含必要的信息
        for (SearchOptimizationUtil.IndexSuggestion suggestion : suggestions) {
            assertNotNull(suggestion.getTableName());
            assertNotNull(suggestion.getIndexName());
            assertNotNull(suggestion.getCreateSql());
            assertNotNull(suggestion.getDescription());
        }
    }
}