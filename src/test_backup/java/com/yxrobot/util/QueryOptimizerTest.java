package com.yxrobot.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 查询优化器测试类
 * 
 * 测试查询优化器的各种优化功能
 * 
 * @author YXRobot
 * @since 1.0.0
 */
@SpringBootTest
@ActiveProfiles("test")
public class QueryOptimizerTest {

    @Autowired
    private QueryOptimizer queryOptimizer;

    @Test
    public void testOptimizePagination() {
        // 测试正常参数
        Map<String, Integer> result = queryOptimizer.optimizePagination(2, 10);
        assertEquals(2, result.get("page"));
        assertEquals(10, result.get("size"));
        assertEquals(10, result.get("offset"));

        // 测试null参数
        result = queryOptimizer.optimizePagination(null, null);
        assertEquals(1, result.get("page"));
        assertEquals(QueryOptimizer.DEFAULT_PAGE_SIZE, result.get("size"));
        assertEquals(0, result.get("offset"));

        // 测试无效参数
        result = queryOptimizer.optimizePagination(0, -1);
        assertEquals(1, result.get("page"));
        assertEquals(QueryOptimizer.DEFAULT_PAGE_SIZE, result.get("size"));
        assertEquals(0, result.get("offset"));

        // 测试超大页面大小
        result = queryOptimizer.optimizePagination(1, 200);
        assertEquals(1, result.get("page"));
        assertEquals(QueryOptimizer.MAX_PAGE_SIZE, result.get("size"));
        assertEquals(0, result.get("offset"));
    }

    @Test
    public void testOptimizeKeyword() {
        // 测试正常关键词
        assertEquals("test", queryOptimizer.optimizeKeyword("test"));
        assertEquals("test keyword", queryOptimizer.optimizeKeyword("  test keyword  "));

        // 测试null和空字符串
        assertNull(queryOptimizer.optimizeKeyword(null));
        assertNull(queryOptimizer.optimizeKeyword(""));
        assertNull(queryOptimizer.optimizeKeyword("   "));

        // 测试特殊字符转义
        assertEquals("test''s", queryOptimizer.optimizeKeyword("test's"));
        assertEquals("test\\\\", queryOptimizer.optimizeKeyword("test\\"));
        assertEquals("test\\%", queryOptimizer.optimizeKeyword("test%"));
        assertEquals("test\\_", queryOptimizer.optimizeKeyword("test_"));

        // 测试超长关键词
        String longKeyword = "a".repeat(150);
        String optimized = queryOptimizer.optimizeKeyword(longKeyword);
        assertEquals(100, optimized.length());
    }

    @Test
    public void testBuildLikePattern() {
        String keyword = "test";

        // 测试不同匹配类型
        assertEquals("test", queryOptimizer.buildLikePattern(keyword, "full"));
        assertEquals("test%", queryOptimizer.buildLikePattern(keyword, "start"));
        assertEquals("%test", queryOptimizer.buildLikePattern(keyword, "end"));
        assertEquals("%test%", queryOptimizer.buildLikePattern(keyword, "contain"));
        assertEquals("%test%", queryOptimizer.buildLikePattern(keyword, "unknown"));

        // 测试null和空关键词
        assertNull(queryOptimizer.buildLikePattern(null, "contain"));
        assertNull(queryOptimizer.buildLikePattern("", "contain"));
        assertNull(queryOptimizer.buildLikePattern("   ", "contain"));
    }

    @Test
    public void testValidateSortField() {
        String[] allowedFields = {"id", "name", "create_time", "update_time"};
        String defaultField = "create_time";

        // 测试有效字段
        assertEquals("id", queryOptimizer.validateSortField("id", allowedFields, defaultField));
        assertEquals("name", queryOptimizer.validateSortField("NAME", allowedFields, defaultField));

        // 测试无效字段
        assertEquals(defaultField, queryOptimizer.validateSortField("invalid_field", allowedFields, defaultField));
        assertEquals(defaultField, queryOptimizer.validateSortField(null, allowedFields, defaultField));
        assertEquals(defaultField, queryOptimizer.validateSortField("", allowedFields, defaultField));
    }

    @Test
    public void testValidateSortOrder() {
        // 测试有效排序方向
        assertEquals("ASC", queryOptimizer.validateSortOrder("asc"));
        assertEquals("DESC", queryOptimizer.validateSortOrder("desc"));
        assertEquals("ASC", queryOptimizer.validateSortOrder("ASC"));
        assertEquals("DESC", queryOptimizer.validateSortOrder("DESC"));

        // 测试无效排序方向
        assertEquals("DESC", queryOptimizer.validateSortOrder("invalid"));
        assertEquals("DESC", queryOptimizer.validateSortOrder(null));
        assertEquals("DESC", queryOptimizer.validateSortOrder(""));
    }

    @Test
    public void testBuildOrderByClause() {
        String[] allowedFields = {"id", "name", "create_time"};
        String defaultField = "create_time";

        // 测试正常情况
        assertEquals("id ASC", queryOptimizer.buildOrderByClause("id", "asc", allowedFields, defaultField));
        assertEquals("name DESC", queryOptimizer.buildOrderByClause("name", "desc", allowedFields, defaultField));

        // 测试无效参数
        assertEquals("create_time DESC", queryOptimizer.buildOrderByClause("invalid", "invalid", allowedFields, defaultField));
    }

    @Test
    public void testCalculateTotalPages() {
        // 测试正常情况
        assertEquals(5, queryOptimizer.calculateTotalPages(100, 20));
        assertEquals(6, queryOptimizer.calculateTotalPages(101, 20));
        assertEquals(1, queryOptimizer.calculateTotalPages(1, 20));

        // 测试边界情况
        assertEquals(0, queryOptimizer.calculateTotalPages(0, 20));
        assertEquals(0, queryOptimizer.calculateTotalPages(-1, 20));
        assertEquals(0, queryOptimizer.calculateTotalPages(100, 0));
        assertEquals(0, queryOptimizer.calculateTotalPages(100, -1));
    }

    @Test
    public void testIsValidPagination() {
        // 测试有效分页
        assertTrue(queryOptimizer.isValidPagination(1, 20, 100));
        assertTrue(queryOptimizer.isValidPagination(5, 20, 100));
        assertTrue(queryOptimizer.isValidPagination(1, 20, 0)); // 无数据时第一页有效

        // 测试无效分页
        assertFalse(queryOptimizer.isValidPagination(0, 20, 100));
        assertFalse(queryOptimizer.isValidPagination(1, 0, 100));
        assertFalse(queryOptimizer.isValidPagination(6, 20, 100)); // 超出总页数
        assertFalse(queryOptimizer.isValidPagination(2, 20, 0)); // 无数据时第二页无效
    }

    @Test
    public void testOptimizeQueryTimeout() {
        // 测试不同预期结果数量的超时时间
        assertEquals(5000, queryOptimizer.optimizeQueryTimeout(100));
        assertEquals(10000, queryOptimizer.optimizeQueryTimeout(5000));
        assertEquals(15000, queryOptimizer.optimizeQueryTimeout(50000));
    }

    @Test
    public void testLogSlowQuery() {
        // 这个方法主要是记录日志，我们测试它不会抛出异常
        assertDoesNotThrow(() -> {
            queryOptimizer.logSlowQuery("testQuery", 1500, 1000);
            queryOptimizer.logSlowQuery("fastQuery", 500, 1000);
        });
    }
}