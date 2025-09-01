package com.yxrobot.service;

import com.yxrobot.dto.CustomerQueryDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 客户查询优化服务
 * 优化数据库查询性能，添加必要的索引建议
 */
@Service
public class CustomerQueryOptimizationService {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerQueryOptimizationService.class);
    
    // 查询性能统计
    private final Map<String, QueryPerformanceStats> queryStats = new HashMap<>();
    
    /**
     * 优化客户查询参数
     */
    public OptimizedQuery optimizeCustomerQuery(CustomerQueryDTO queryDTO) {
        logger.debug("开始优化客户查询参数");
        
        OptimizedQuery optimizedQuery = new OptimizedQuery();
        List<String> optimizationTips = new ArrayList<>();
        List<String> indexSuggestions = new ArrayList<>();
        
        // 1. 优化分页参数
        optimizePagination(queryDTO, optimizedQuery, optimizationTips);
        
        // 2. 优化搜索条件
        optimizeSearchConditions(queryDTO, optimizedQuery, optimizationTips, indexSuggestions);
        
        // 3. 优化排序条件
        optimizeSortConditions(queryDTO, optimizedQuery, optimizationTips, indexSuggestions);
        
        // 4. 优化筛选条件
        optimizeFilterConditions(queryDTO, optimizedQuery, optimizationTips, indexSuggestions);
        
        // 5. 生成查询计划
        generateQueryPlan(optimizedQuery, optimizationTips);
        
        optimizedQuery.setOptimizationTips(optimizationTips);
        optimizedQuery.setIndexSuggestions(indexSuggestions);
        
        logger.debug("查询优化完成，优化建议数: {}, 索引建议数: {}", 
                    optimizationTips.size(), indexSuggestions.size());
        
        return optimizedQuery;
    }
    
    /**
     * 优化分页参数
     */
    private void optimizePagination(CustomerQueryDTO queryDTO, OptimizedQuery optimizedQuery, 
                                   List<String> optimizationTips) {
        Integer page = queryDTO.getPage();
        Integer pageSize = queryDTO.getPageSize();
        
        // 设置默认值
        if (page == null || page < 1) {
            page = 1;
            optimizationTips.add("页码已设置为默认值1");
        }
        
        if (pageSize == null || pageSize < 1) {
            pageSize = 20;
            optimizationTips.add("每页大小已设置为默认值20");
        }
        
        // 限制最大页面大小
        if (pageSize > 100) {
            pageSize = 100;
            optimizationTips.add("每页大小已限制为最大值100，避免查询过多数据");
        }
        
        // 检查深分页问题
        int offset = (page - 1) * pageSize;
        if (offset > 10000) {
            optimizationTips.add("检测到深分页查询（偏移量: " + offset + "），建议使用游标分页或限制查询范围");
        }
        
        optimizedQuery.setPage(page);
        optimizedQuery.setPageSize(pageSize);
        optimizedQuery.setOffset(offset);
    }
    
    /**
     * 优化搜索条件
     */
    private void optimizeSearchConditions(CustomerQueryDTO queryDTO, OptimizedQuery optimizedQuery,
                                        List<String> optimizationTips, List<String> indexSuggestions) {
        String keyword = queryDTO.getKeyword();
        
        if (StringUtils.hasText(keyword)) {
            // 清理和标准化关键词
            String cleanKeyword = keyword.trim();
            
            // 检查关键词长度
            if (cleanKeyword.length() < 2) {
                optimizationTips.add("搜索关键词过短，可能影响搜索效果");
            } else if (cleanKeyword.length() > 50) {
                cleanKeyword = cleanKeyword.substring(0, 50);
                optimizationTips.add("搜索关键词已截断为50个字符");
            }
            
            // 检查是否为纯数字（可能是电话或ID搜索）
            if (cleanKeyword.matches("\\d+")) {
                optimizedQuery.setSearchType("NUMERIC");
                if (cleanKeyword.length() == 11) {
                    optimizedQuery.setSearchType("PHONE");
                    indexSuggestions.add("建议在phone字段上创建索引以优化电话号码搜索");
                } else if (cleanKeyword.length() <= 10) {
                    optimizedQuery.setSearchType("ID");
                    optimizationTips.add("检测到ID搜索，将使用精确匹配");
                }
            } else {
                optimizedQuery.setSearchType("TEXT");
                indexSuggestions.add("建议在customer_name字段上创建全文索引以优化文本搜索");
            }
            
            optimizedQuery.setKeyword(cleanKeyword);
        }
    }
    
    /**
     * 优化排序条件
     */
    private void optimizeSortConditions(CustomerQueryDTO queryDTO, OptimizedQuery optimizedQuery,
                                      List<String> optimizationTips, List<String> indexSuggestions) {
        String sortBy = queryDTO.getSortBy();
        String sortOrder = queryDTO.getSortOrder();
        
        // 设置默认排序
        if (!StringUtils.hasText(sortBy)) {
            sortBy = "created_at";
            sortOrder = "DESC";
            optimizationTips.add("已设置默认排序：按创建时间降序");
        }
        
        // 验证排序字段
        Set<String> allowedSortFields = Set.of(
            "customer_name", "created_at", "last_active_at", 
            "total_spent", "customer_value", "phone"
        );
        
        if (!allowedSortFields.contains(sortBy)) {
            sortBy = "created_at";
            optimizationTips.add("排序字段不支持，已改为按创建时间排序");
        }
        
        // 验证排序方向
        if (!"ASC".equalsIgnoreCase(sortOrder) && !"DESC".equalsIgnoreCase(sortOrder)) {
            sortOrder = "DESC";
            optimizationTips.add("排序方向无效，已设置为降序");
        }
        
        // 索引建议
        if ("total_spent".equals(sortBy) || "customer_value".equals(sortBy)) {
            indexSuggestions.add("建议在" + sortBy + "字段上创建索引以优化排序性能");
        }
        
        optimizedQuery.setSortBy(sortBy);
        optimizedQuery.setSortOrder(sortOrder.toUpperCase());
    }
    
    /**
     * 优化筛选条件
     */
    private void optimizeFilterConditions(CustomerQueryDTO queryDTO, OptimizedQuery optimizedQuery,
                                        List<String> optimizationTips, List<String> indexSuggestions) {
        Map<String, Object> filters = new HashMap<>();
        
        // 客户等级筛选
        if (StringUtils.hasText(queryDTO.getLevel())) {
            filters.put("customer_level", queryDTO.getLevel().toUpperCase());
            indexSuggestions.add("建议在customer_level字段上创建索引以优化等级筛选");
        }
        
        // 客户状态筛选
        if (StringUtils.hasText(queryDTO.getStatus())) {
            filters.put("customer_status", queryDTO.getStatus().toUpperCase());
            indexSuggestions.add("建议在customer_status字段上创建索引以优化状态筛选");
        }
        
        // 地区筛选
        if (StringUtils.hasText(queryDTO.getRegion())) {
            filters.put("region", queryDTO.getRegion());
            indexSuggestions.add("建议在region字段上创建索引以优化地区筛选");
        }
        
        // 行业筛选
        if (StringUtils.hasText(queryDTO.getIndustry())) {
            filters.put("industry", queryDTO.getIndustry());
            indexSuggestions.add("建议在industry字段上创建索引以优化行业筛选");
        }
        
        // 时间范围筛选
        if (queryDTO.getStartDate() != null || queryDTO.getEndDate() != null) {
            Map<String, Object> dateRange = new HashMap<>();
            if (queryDTO.getStartDate() != null) {
                dateRange.put("start", queryDTO.getStartDate());
            }
            if (queryDTO.getEndDate() != null) {
                dateRange.put("end", queryDTO.getEndDate());
            }
            filters.put("date_range", dateRange);
            indexSuggestions.add("建议在created_at字段上创建索引以优化时间范围查询");
        }
        
        // 组合筛选优化
        if (filters.size() > 1) {
            List<String> filterFields = new ArrayList<>(filters.keySet());
            if (filterFields.contains("customer_level") && filterFields.contains("customer_status")) {
                indexSuggestions.add("建议创建(customer_level, customer_status)复合索引以优化组合筛选");
            }
        }
        
        optimizedQuery.setFilters(filters);
        
        if (!filters.isEmpty()) {
            optimizationTips.add("已应用" + filters.size() + "个筛选条件");
        }
    }
    
    /**
     * 生成查询计划
     */
    private void generateQueryPlan(OptimizedQuery optimizedQuery, List<String> optimizationTips) {
        QueryPlan queryPlan = new QueryPlan();
        
        // 估算查询复杂度
        int complexity = 0;
        
        // 基础查询复杂度
        complexity += 1;
        
        // 搜索条件复杂度
        if (StringUtils.hasText(optimizedQuery.getKeyword())) {
            if ("TEXT".equals(optimizedQuery.getSearchType())) {
                complexity += 3; // 文本搜索复杂度较高
            } else {
                complexity += 1; // 精确搜索复杂度较低
            }
        }
        
        // 筛选条件复杂度
        complexity += optimizedQuery.getFilters().size();
        
        // 排序复杂度
        if (StringUtils.hasText(optimizedQuery.getSortBy())) {
            complexity += 2;
        }
        
        // 分页复杂度
        if (optimizedQuery.getOffset() > 1000) {
            complexity += 2; // 深分页增加复杂度
        }
        
        queryPlan.setComplexity(complexity);
        
        // 设置查询策略
        if (complexity <= 3) {
            queryPlan.setStrategy("SIMPLE");
            optimizationTips.add("查询复杂度较低，使用简单查询策略");
        } else if (complexity <= 6) {
            queryPlan.setStrategy("MODERATE");
            optimizationTips.add("查询复杂度中等，建议使用索引优化");
        } else {
            queryPlan.setStrategy("COMPLEX");
            optimizationTips.add("查询复杂度较高，建议分步查询或使用缓存");
        }
        
        // 估算执行时间
        long estimatedTime = complexity * 100; // 基础估算：每个复杂度单位100ms
        queryPlan.setEstimatedExecutionTime(estimatedTime);
        
        if (estimatedTime > 2000) {
            optimizationTips.add("预计查询时间较长(" + estimatedTime + "ms)，建议优化查询条件");
        }
        
        optimizedQuery.setQueryPlan(queryPlan);
    }
    
    /**
     * 记录查询性能
     */
    public void recordQueryPerformance(String queryType, long executionTime, int resultCount) {
        QueryPerformanceStats stats = queryStats.computeIfAbsent(queryType, 
            k -> new QueryPerformanceStats(k));
        
        stats.recordExecution(executionTime, resultCount);
        
        logger.debug("记录查询性能 - 类型: {}, 执行时间: {}ms, 结果数: {}", 
                    queryType, executionTime, resultCount);
    }
    
    /**
     * 获取查询性能统计
     */
    public Map<String, QueryPerformanceStats> getQueryPerformanceStats() {
        return new HashMap<>(queryStats);
    }
    
    /**
     * 优化后的查询对象
     */
    public static class OptimizedQuery {
        private Integer page;
        private Integer pageSize;
        private Integer offset;
        private String keyword;
        private String searchType;
        private String sortBy;
        private String sortOrder;
        private Map<String, Object> filters;
        private QueryPlan queryPlan;
        private List<String> optimizationTips;
        private List<String> indexSuggestions;
        
        // Getters and Setters
        public Integer getPage() { return page; }
        public void setPage(Integer page) { this.page = page; }
        
        public Integer getPageSize() { return pageSize; }
        public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
        
        public Integer getOffset() { return offset; }
        public void setOffset(Integer offset) { this.offset = offset; }
        
        public String getKeyword() { return keyword; }
        public void setKeyword(String keyword) { this.keyword = keyword; }
        
        public String getSearchType() { return searchType; }
        public void setSearchType(String searchType) { this.searchType = searchType; }
        
        public String getSortBy() { return sortBy; }
        public void setSortBy(String sortBy) { this.sortBy = sortBy; }
        
        public String getSortOrder() { return sortOrder; }
        public void setSortOrder(String sortOrder) { this.sortOrder = sortOrder; }
        
        public Map<String, Object> getFilters() { return filters; }
        public void setFilters(Map<String, Object> filters) { this.filters = filters; }
        
        public QueryPlan getQueryPlan() { return queryPlan; }
        public void setQueryPlan(QueryPlan queryPlan) { this.queryPlan = queryPlan; }
        
        public List<String> getOptimizationTips() { return optimizationTips; }
        public void setOptimizationTips(List<String> optimizationTips) { this.optimizationTips = optimizationTips; }
        
        public List<String> getIndexSuggestions() { return indexSuggestions; }
        public void setIndexSuggestions(List<String> indexSuggestions) { this.indexSuggestions = indexSuggestions; }
    }
    
    /**
     * 查询计划
     */
    public static class QueryPlan {
        private int complexity;
        private String strategy;
        private long estimatedExecutionTime;
        
        public int getComplexity() { return complexity; }
        public void setComplexity(int complexity) { this.complexity = complexity; }
        
        public String getStrategy() { return strategy; }
        public void setStrategy(String strategy) { this.strategy = strategy; }
        
        public long getEstimatedExecutionTime() { return estimatedExecutionTime; }
        public void setEstimatedExecutionTime(long estimatedExecutionTime) { this.estimatedExecutionTime = estimatedExecutionTime; }
    }
    
    /**
     * 查询性能统计
     */
    public static class QueryPerformanceStats {
        private final String queryType;
        private long totalExecutions = 0;
        private long totalExecutionTime = 0;
        private long minExecutionTime = Long.MAX_VALUE;
        private long maxExecutionTime = 0;
        private long totalResults = 0;
        
        public QueryPerformanceStats(String queryType) {
            this.queryType = queryType;
        }
        
        public void recordExecution(long executionTime, int resultCount) {
            totalExecutions++;
            totalExecutionTime += executionTime;
            totalResults += resultCount;
            
            if (executionTime < minExecutionTime) {
                minExecutionTime = executionTime;
            }
            if (executionTime > maxExecutionTime) {
                maxExecutionTime = executionTime;
            }
        }
        
        public String getQueryType() { return queryType; }
        public long getTotalExecutions() { return totalExecutions; }
        public double getAverageExecutionTime() { 
            return totalExecutions > 0 ? (double) totalExecutionTime / totalExecutions : 0; 
        }
        public long getMinExecutionTime() { 
            return minExecutionTime == Long.MAX_VALUE ? 0 : minExecutionTime; 
        }
        public long getMaxExecutionTime() { return maxExecutionTime; }
        public double getAverageResultCount() { 
            return totalExecutions > 0 ? (double) totalResults / totalExecutions : 0; 
        }
    }
}