package com.yxrobot.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 查询优化工具类
 * 
 * 提供数据库查询优化相关的工具方法，包括分页参数优化、查询条件优化等
 * 
 * @author YXRobot
 * @since 1.0.0
 */
@Component
public class QueryOptimizer {

    private static final Logger logger = LoggerFactory.getLogger(QueryOptimizer.class);

    /**
     * 默认页面大小
     */
    public static final int DEFAULT_PAGE_SIZE = 20;

    /**
     * 最大页面大小
     */
    public static final int MAX_PAGE_SIZE = 100;

    /**
     * 最小页面大小
     */
    public static final int MIN_PAGE_SIZE = 1;

    /**
     * 优化分页参数
     * 
     * @param page 页码
     * @param size 每页数量
     * @return 优化后的分页参数Map，包含page、size、offset
     */
    public Map<String, Integer> optimizePagination(Integer page, Integer size) {
        Map<String, Integer> result = new HashMap<>();
        
        // 优化页码
        if (page == null || page < 1) {
            page = 1;
        }
        
        // 优化页面大小
        if (size == null || size < MIN_PAGE_SIZE) {
            size = DEFAULT_PAGE_SIZE;
        } else if (size > MAX_PAGE_SIZE) {
            size = MAX_PAGE_SIZE;
            logger.warn("页面大小超过最大限制，已调整为: {}", MAX_PAGE_SIZE);
        }
        
        // 计算偏移量
        Integer offset = (page - 1) * size;
        
        result.put("page", page);
        result.put("size", size);
        result.put("offset", offset);
        
        return result;
    }

    /**
     * 优化搜索关键词
     * 
     * @param keyword 原始关键词
     * @return 优化后的关键词
     */
    public String optimizeKeyword(String keyword) {
        if (keyword == null) {
            return null;
        }
        
        // 去除首尾空格
        keyword = keyword.trim();
        
        // 如果为空字符串，返回null
        if (keyword.isEmpty()) {
            return null;
        }
        
        // 限制关键词长度，防止过长的查询影响性能
        if (keyword.length() > 100) {
            keyword = keyword.substring(0, 100);
            logger.warn("搜索关键词过长，已截取前100个字符");
        }
        
        // 转义特殊字符，防止SQL注入
        keyword = keyword.replace("'", "''")
                        .replace("\\", "\\\\")
                        .replace("%", "\\%")
                        .replace("_", "\\_");
        
        return keyword;
    }

    /**
     * 构建LIKE查询模式
     * 
     * @param keyword 关键词
     * @param matchType 匹配类型：full(全匹配)、start(前缀匹配)、end(后缀匹配)、contain(包含匹配)
     * @return LIKE查询模式
     */
    public String buildLikePattern(String keyword, String matchType) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }
        
        keyword = optimizeKeyword(keyword);
        if (keyword == null) {
            return null;
        }
        
        switch (matchType.toLowerCase()) {
            case "full":
                return keyword;
            case "start":
                return keyword + "%";
            case "end":
                return "%" + keyword;
            case "contain":
            default:
                return "%" + keyword + "%";
        }
    }

    /**
     * 验证排序字段
     * 
     * @param sortField 排序字段
     * @param allowedFields 允许的排序字段数组
     * @return 验证通过的排序字段，如果验证失败返回默认字段
     */
    public String validateSortField(String sortField, String[] allowedFields, String defaultField) {
        if (sortField == null || sortField.trim().isEmpty()) {
            return defaultField;
        }
        
        sortField = sortField.trim();
        
        // 检查是否在允许的字段列表中
        for (String allowedField : allowedFields) {
            if (allowedField.equalsIgnoreCase(sortField)) {
                return allowedField;
            }
        }
        
        logger.warn("无效的排序字段: {}，使用默认字段: {}", sortField, defaultField);
        return defaultField;
    }

    /**
     * 验证排序方向
     * 
     * @param sortOrder 排序方向
     * @return 验证通过的排序方向（ASC或DESC）
     */
    public String validateSortOrder(String sortOrder) {
        if (sortOrder == null || sortOrder.trim().isEmpty()) {
            return "DESC"; // 默认降序
        }
        
        sortOrder = sortOrder.trim().toUpperCase();
        
        if ("ASC".equals(sortOrder) || "DESC".equals(sortOrder)) {
            return sortOrder;
        }
        
        logger.warn("无效的排序方向: {}，使用默认方向: DESC", sortOrder);
        return "DESC";
    }

    /**
     * 构建排序子句
     * 
     * @param sortField 排序字段
     * @param sortOrder 排序方向
     * @param allowedFields 允许的排序字段数组
     * @param defaultField 默认排序字段
     * @return 排序子句
     */
    public String buildOrderByClause(String sortField, String sortOrder, String[] allowedFields, String defaultField) {
        String validatedField = validateSortField(sortField, allowedFields, defaultField);
        String validatedOrder = validateSortOrder(sortOrder);
        
        return validatedField + " " + validatedOrder;
    }

    /**
     * 计算总页数
     * 
     * @param totalCount 总记录数
     * @param pageSize 每页数量
     * @return 总页数
     */
    public int calculateTotalPages(long totalCount, int pageSize) {
        if (totalCount <= 0 || pageSize <= 0) {
            return 0;
        }
        
        return (int) Math.ceil((double) totalCount / pageSize);
    }

    /**
     * 验证分页参数的合理性
     * 
     * @param page 页码
     * @param size 每页数量
     * @param totalCount 总记录数
     * @return 是否合理
     */
    public boolean isValidPagination(int page, int size, long totalCount) {
        if (page < 1 || size < 1) {
            return false;
        }
        
        if (totalCount <= 0) {
            return page == 1; // 没有数据时只有第一页是合理的
        }
        
        int totalPages = calculateTotalPages(totalCount, size);
        return page <= totalPages;
    }

    /**
     * 优化查询超时时间
     * 
     * @param expectedResultCount 预期结果数量
     * @return 建议的查询超时时间（毫秒）
     */
    public int optimizeQueryTimeout(int expectedResultCount) {
        // 基础超时时间：5秒
        int baseTimeout = 5000;
        
        // 根据预期结果数量调整超时时间
        if (expectedResultCount > 10000) {
            return baseTimeout * 3; // 15秒
        } else if (expectedResultCount > 1000) {
            return baseTimeout * 2; // 10秒
        } else {
            return baseTimeout; // 5秒
        }
    }

    /**
     * 记录慢查询
     * 
     * @param queryName 查询名称
     * @param executionTime 执行时间（毫秒）
     * @param threshold 慢查询阈值（毫秒）
     */
    public void logSlowQuery(String queryName, long executionTime, long threshold) {
        if (executionTime > threshold) {
            logger.warn("慢查询检测 - 查询名称: {}, 执行时间: {}ms, 阈值: {}ms", 
                       queryName, executionTime, threshold);
        }
    }
}