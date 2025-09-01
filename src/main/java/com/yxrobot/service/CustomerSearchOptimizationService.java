package com.yxrobot.service;

import com.yxrobot.dto.CustomerQueryDTO;
import com.yxrobot.dto.CustomerDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 客户搜索优化服务
 * 提供高性能的搜索和筛选功能优化
 * 
 * 主要功能：
 * 1. 搜索查询优化和索引建议
 * 2. 搜索结果缓存和预加载
 * 3. 搜索性能监控和分析
 * 4. 智能搜索建议和自动完成
 * 5. 搜索热词统计和推荐
 */
@Service
public class CustomerSearchOptimizationService {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerSearchOptimizationService.class);
    
    @Autowired
    private CustomerService customerService;
    
    // 异步执行器
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    
    // 搜索性能统计
    private final Map<String, SearchPerformanceStats> performanceStats = new HashMap<>();
    
    // 热门搜索关键词
    private final Map<String, Integer> popularKeywords = new HashMap<>();
    
    // 搜索建议缓存
    private final Map<String, List<String>> suggestionCache = new HashMap<>();
    
    /**
     * 搜索性能统计类
     */
    public static class SearchPerformanceStats {
        private long totalSearches = 0;
        private long totalExecutionTime = 0;
        private long averageExecutionTime = 0;
        private int resultCount = 0;
        private Date lastSearchTime = new Date();
        
        // Getters and setters
        public long getTotalSearches() { return totalSearches; }
        public void setTotalSearches(long totalSearches) { this.totalSearches = totalSearches; }
        
        public long getTotalExecutionTime() { return totalExecutionTime; }
        public void setTotalExecutionTime(long totalExecutionTime) { this.totalExecutionTime = totalExecutionTime; }
        
        public long getAverageExecutionTime() { return averageExecutionTime; }
        public void setAverageExecutionTime(long averageExecutionTime) { this.averageExecutionTime = averageExecutionTime; }
        
        public int getResultCount() { return resultCount; }
        public void setResultCount(int resultCount) { this.resultCount = resultCount; }
        
        public Date getLastSearchTime() { return lastSearchTime; }
        public void setLastSearchTime(Date lastSearchTime) { this.lastSearchTime = lastSearchTime; }
    }
    
    /**
     * 搜索查询优化结果
     */
    public static class SearchOptimizationResult {
        private CustomerQueryDTO optimizedQuery;
        private List<String> optimizationTips;
        private List<String> indexSuggestions;
        private boolean useCache;
        private String cacheKey;
        
        public SearchOptimizationResult(CustomerQueryDTO optimizedQuery) {
            this.optimizedQuery = optimizedQuery;
            this.optimizationTips = new ArrayList<>();
            this.indexSuggestions = new ArrayList<>();
            this.useCache = false;
        }
        
        // Getters and setters
        public CustomerQueryDTO getOptimizedQuery() { return optimizedQuery; }
        public void setOptimizedQuery(CustomerQueryDTO optimizedQuery) { this.optimizedQuery = optimizedQuery; }
        
        public List<String> getOptimizationTips() { return optimizationTips; }
        public void setOptimizationTips(List<String> optimizationTips) { this.optimizationTips = optimizationTips; }
        
        public List<String> getIndexSuggestions() { return indexSuggestions; }
        public void setIndexSuggestions(List<String> indexSuggestions) { this.indexSuggestions = indexSuggestions; }
        
        public boolean isUseCache() { return useCache; }
        public void setUseCache(boolean useCache) { this.useCache = useCache; }
        
        public String getCacheKey() { return cacheKey; }
        public void setCacheKey(String cacheKey) { this.cacheKey = cacheKey; }
    }
    
    // ==================== 搜索查询优化 ====================
    
    /**
     * 优化搜索查询
     * @param queryDTO 原始查询条件
     * @return 优化结果
     */
    public SearchOptimizationResult optimizeSearchQuery(CustomerQueryDTO queryDTO) {
        try {
            logger.debug("开始优化搜索查询: {}", queryDTO);
            
            SearchOptimizationResult result = new SearchOptimizationResult(queryDTO);
            
            // 1. 查询条件优化
            optimizeQueryConditions(result);
            
            // 2. 排序优化
            optimizeSorting(result);
            
            // 3. 分页优化
            optimizePagination(result);
            
            // 4. 缓存策略
            determineCacheStrategy(result);
            
            // 5. 索引建议
            generateIndexSuggestions(result);
            
            logger.debug("搜索查询优化完成: {}", result.getOptimizationTips());
            
            return result;
            
        } catch (Exception e) {
            logger.error("搜索查询优化失败", e);
            return new SearchOptimizationResult(queryDTO);
        }
    }
    
    /**
     * 优化查询条件
     */
    private void optimizeQueryConditions(SearchOptimizationResult result) {
        CustomerQueryDTO query = result.getOptimizedQuery();
        
        // 关键词搜索优化
        if (StringUtils.hasText(query.getKeyword())) {
            String keyword = query.getKeyword().trim();
            
            // 去除多余空格
            if (!keyword.equals(query.getKeyword())) {
                query.setKeyword(keyword);
                result.getOptimizationTips().add("已优化搜索关键词格式");
            }
            
            // 短关键词建议
            if (keyword.length() < 2) {
                result.getOptimizationTips().add("建议使用2个字符以上的关键词以获得更好的搜索效果");
            }
            
            // 记录热门关键词
            recordPopularKeyword(keyword);
        }
        
        // 筛选条件优化
        if (query.hasAdvancedFilters()) {
            result.getOptimizationTips().add("检测到高级筛选条件，建议使用索引优化查询性能");
        }
        
        // 时间范围优化
        if (query.hasDateRangeFilters()) {
            result.getOptimizationTips().add("时间范围查询已启用，建议在时间字段上创建索引");
        }
    }
    
    /**
     * 优化排序
     */
    private void optimizeSorting(SearchOptimizationResult result) {
        CustomerQueryDTO query = result.getOptimizedQuery();
        
        // 默认排序优化
        if (!StringUtils.hasText(query.getSortBy())) {
            query.setSortBy("lastActiveAt");
            query.setSortOrder("DESC");
            result.getOptimizationTips().add("已设置默认排序为最后活跃时间降序");
        }
        
        // 多字段排序建议
        if (StringUtils.hasText(query.getSortBy()) && !StringUtils.hasText(query.getSecondarySortBy())) {
            // 根据主排序字段建议次要排序字段
            switch (query.getSortBy()) {
                case "customerLevel":
                    query.setSecondarySortBy("totalSpent");
                    query.setSecondarySortOrder("DESC");
                    result.getOptimizationTips().add("已添加消费金额作为次要排序条件");
                    break;
                case "totalSpent":
                    query.setSecondarySortBy("customerValue");
                    query.setSecondarySortOrder("DESC");
                    result.getOptimizationTips().add("已添加客户价值作为次要排序条件");
                    break;
            }
        }
    }
    
    /**
     * 优化分页
     */
    private void optimizePagination(SearchOptimizationResult result) {
        CustomerQueryDTO query = result.getOptimizedQuery();
        
        // 分页大小优化
        if (query.getPageSize() != null && query.getPageSize() > 100) {
            query.setPageSize(100);
            result.getOptimizationTips().add("已将分页大小限制为100以提高性能");
        }
        
        // 深度分页优化建议
        if (query.getPage() != null && query.getPage() > 100) {
            result.getOptimizationTips().add("深度分页可能影响性能，建议使用筛选条件缩小结果范围");
        }
    }
    
    /**
     * 确定缓存策略
     */
    private void determineCacheStrategy(SearchOptimizationResult result) {
        CustomerQueryDTO query = result.getOptimizedQuery();
        
        // 简单查询使用缓存
        if (!query.hasAdvancedFilters() && !query.hasDateRangeFilters()) {
            result.setUseCache(true);
            result.setCacheKey(generateCacheKey(query));
            result.getOptimizationTips().add("已启用查询结果缓存");
        }
        
        // 热门搜索使用缓存
        if (StringUtils.hasText(query.getKeyword()) && isPopularKeyword(query.getKeyword())) {
            result.setUseCache(true);
            result.setCacheKey(generateCacheKey(query));
            result.getOptimizationTips().add("热门搜索关键词，已启用缓存");
        }
    }
    
    /**
     * 生成索引建议
     */
    private void generateIndexSuggestions(SearchOptimizationResult result) {
        CustomerQueryDTO query = result.getOptimizedQuery();
        
        // 基于查询条件生成索引建议
        if (StringUtils.hasText(query.getCustomerLevel())) {
            result.getIndexSuggestions().add("建议在customer_level字段上创建索引");
        }
        
        if (StringUtils.hasText(query.getRegion())) {
            result.getIndexSuggestions().add("建议在region字段上创建索引");
        }
        
        if (query.hasDateRangeFilters()) {
            result.getIndexSuggestions().add("建议在时间字段(registered_at, last_active_at)上创建索引");
        }
        
        if (query.hasAmountRangeFilters()) {
            result.getIndexSuggestions().add("建议在金额字段(total_spent, customer_value)上创建索引");
        }
        
        // 复合索引建议
        if (StringUtils.hasText(query.getCustomerLevel()) && StringUtils.hasText(query.getRegion())) {
            result.getIndexSuggestions().add("建议创建(customer_level, region)复合索引");
        }
    }
    
    // ==================== 智能搜索建议 ====================
    
    /**
     * 获取智能搜索建议
     * @param keyword 搜索关键词
     * @param limit 限制数量
     * @return 搜索建议列表
     */
    @Cacheable(value = "searchSuggestions", key = "#keyword + '_' + #limit")
    public List<String> getIntelligentSuggestions(String keyword, Integer limit) {
        try {
            if (!StringUtils.hasText(keyword) || keyword.length() < 2) {
                return getPopularKeywords(limit);
            }
            
            logger.debug("获取智能搜索建议: 关键词={}", keyword);
            
            List<String> suggestions = new ArrayList<>();
            
            // 1. 从缓存获取建议
            List<String> cachedSuggestions = suggestionCache.get(keyword.toLowerCase());
            if (cachedSuggestions != null) {
                suggestions.addAll(cachedSuggestions);
            }
            
            // 2. 基于历史搜索生成建议
            suggestions.addAll(generateHistoryBasedSuggestions(keyword));
            
            // 3. 基于客户数据生成建议
            CompletableFuture<List<String>> customerSuggestions = 
                CompletableFuture.supplyAsync(() -> generateCustomerBasedSuggestions(keyword), executorService);
            
            // 4. 合并和排序建议
            try {
                suggestions.addAll(customerSuggestions.get());
            } catch (Exception e) {
                logger.warn("获取客户数据建议失败", e);
            }
            
            // 5. 去重和限制数量
            suggestions = suggestions.stream()
                    .distinct()
                    .limit(limit != null ? limit : 10)
                    .collect(java.util.stream.Collectors.toList());
            
            // 6. 缓存建议结果
            suggestionCache.put(keyword.toLowerCase(), suggestions);
            
            return suggestions;
            
        } catch (Exception e) {
            logger.error("获取智能搜索建议失败: 关键词={}", keyword, e);
            return getPopularKeywords(limit);
        }
    }
    
    /**
     * 基于历史搜索生成建议
     */
    private List<String> generateHistoryBasedSuggestions(String keyword) {
        return popularKeywords.keySet().stream()
                .filter(k -> k.toLowerCase().contains(keyword.toLowerCase()))
                .sorted((k1, k2) -> popularKeywords.get(k2).compareTo(popularKeywords.get(k1)))
                .limit(5)
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 基于客户数据生成建议
     */
    private List<String> generateCustomerBasedSuggestions(String keyword) {
        try {
            // 搜索匹配的客户
            List<Map<String, Object>> customers = customerService.getSearchSuggestions(keyword, 5);
            
            return customers.stream()
                    .map(customer -> (String) customer.get("name"))
                    .filter(Objects::nonNull)
                    .collect(java.util.stream.Collectors.toList());
                    
        } catch (Exception e) {
            logger.warn("基于客户数据生成建议失败", e);
            return List.of();
        }
    }
    
    // ==================== 搜索性能监控 ====================
    
    /**
     * 记录搜索性能
     * @param searchType 搜索类型
     * @param executionTime 执行时间
     * @param resultCount 结果数量
     */
    public void recordSearchPerformance(String searchType, long executionTime, int resultCount) {
        try {
            SearchPerformanceStats stats = performanceStats.computeIfAbsent(searchType, 
                k -> new SearchPerformanceStats());
            
            synchronized (stats) {
                stats.setTotalSearches(stats.getTotalSearches() + 1);
                stats.setTotalExecutionTime(stats.getTotalExecutionTime() + executionTime);
                stats.setAverageExecutionTime(stats.getTotalExecutionTime() / stats.getTotalSearches());
                stats.setResultCount(resultCount);
                stats.setLastSearchTime(new Date());
            }
            
            // 性能警告
            if (executionTime > 5000) { // 超过5秒
                logger.warn("搜索性能警告: 类型={}, 执行时间={}ms, 结果数量={}", 
                    searchType, executionTime, resultCount);
            }
            
        } catch (Exception e) {
            logger.error("记录搜索性能失败", e);
        }
    }
    
    /**
     * 获取搜索性能统计
     * @return 性能统计信息
     */
    public Map<String, SearchPerformanceStats> getSearchPerformanceStats() {
        return new HashMap<>(performanceStats);
    }
    
    /**
     * 获取搜索性能报告
     * @return 性能报告
     */
    public Map<String, Object> getSearchPerformanceReport() {
        Map<String, Object> report = new HashMap<>();
        
        // 总体统计
        long totalSearches = performanceStats.values().stream()
                .mapToLong(SearchPerformanceStats::getTotalSearches)
                .sum();
        
        long totalExecutionTime = performanceStats.values().stream()
                .mapToLong(SearchPerformanceStats::getTotalExecutionTime)
                .sum();
        
        long averageExecutionTime = totalSearches > 0 ? totalExecutionTime / totalSearches : 0;
        
        report.put("totalSearches", totalSearches);
        report.put("averageExecutionTime", averageExecutionTime);
        report.put("searchTypes", performanceStats.size());
        
        // 详细统计
        report.put("detailStats", performanceStats);
        
        // 热门关键词
        report.put("popularKeywords", getTopPopularKeywords(10));
        
        // 性能建议
        List<String> suggestions = new ArrayList<>();
        if (averageExecutionTime > 2000) {
            suggestions.add("平均搜索时间较长，建议优化数据库索引");
        }
        if (totalSearches > 1000) {
            suggestions.add("搜索量较大，建议启用搜索结果缓存");
        }
        report.put("performanceSuggestions", suggestions);
        
        return report;
    }
    
    // ==================== 热门搜索管理 ====================
    
    /**
     * 记录热门关键词
     */
    private void recordPopularKeyword(String keyword) {
        if (StringUtils.hasText(keyword) && keyword.length() >= 2) {
            popularKeywords.merge(keyword.toLowerCase(), 1, Integer::sum);
        }
    }
    
    /**
     * 检查是否为热门关键词
     */
    private boolean isPopularKeyword(String keyword) {
        return popularKeywords.getOrDefault(keyword.toLowerCase(), 0) >= 10;
    }
    
    /**
     * 获取热门关键词列表
     */
    public List<String> getPopularKeywords(Integer limit) {
        return popularKeywords.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limit != null ? limit : 10)
                .map(Map.Entry::getKey)
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 获取TOP热门关键词
     */
    private Map<String, Integer> getTopPopularKeywords(int limit) {
        return popularKeywords.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limit)
                .collect(java.util.stream.Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (e1, e2) -> e1,
                    LinkedHashMap::new
                ));
    }
    
    // ==================== 工具方法 ====================
    
    /**
     * 生成缓存键
     */
    private String generateCacheKey(CustomerQueryDTO query) {
        StringBuilder keyBuilder = new StringBuilder("customer_search:");
        
        if (StringUtils.hasText(query.getKeyword())) {
            keyBuilder.append("kw:").append(query.getKeyword()).append(":");
        }
        if (StringUtils.hasText(query.getCustomerLevel())) {
            keyBuilder.append("lv:").append(query.getCustomerLevel()).append(":");
        }
        if (StringUtils.hasText(query.getRegion())) {
            keyBuilder.append("rg:").append(query.getRegion()).append(":");
        }
        
        keyBuilder.append("pg:").append(query.getPage())
                  .append(":sz:").append(query.getPageSize());
        
        return keyBuilder.toString();
    }
    
    /**
     * 清理缓存
     */
    public void clearCache() {
        suggestionCache.clear();
        logger.info("搜索建议缓存已清理");
    }
    
    /**
     * 清理性能统计
     */
    public void clearPerformanceStats() {
        performanceStats.clear();
        popularKeywords.clear();
        logger.info("搜索性能统计已清理");
    }
}