package com.yxrobot.service;

import com.yxrobot.dto.OrderDTO;
import com.yxrobot.dto.OrderQueryDTO;
import com.yxrobot.entity.Order;
import com.yxrobot.mapper.OrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单搜索服务
 * 提供高级搜索和筛选功能
 * 优化搜索性能和用户体验
 */
@Service
public class OrderSearchService {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderSearchService.class);
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 高级搜索订单
     * 支持多条件组合搜索和智能筛选
     * 
     * @param queryDTO 查询条件
     * @return 搜索结果
     */
    public AdvancedSearchResult advancedSearch(OrderQueryDTO queryDTO) {
        logger.debug("开始高级搜索订单，查询条件: {}", queryDTO);
        
        try {
            // 预处理查询条件
            preprocessQueryConditions(queryDTO);
            
            // 执行搜索
            List<Order> orders = executeAdvancedSearch(queryDTO);
            
            // 查询总数
            int total = countAdvancedSearch(queryDTO);
            
            // 转换为DTO
            List<OrderDTO> orderDTOs = orders.stream()
                .map(this::convertToOrderDTO)
                .collect(Collectors.toList());
            
            // 生成搜索统计信息
            SearchStatistics statistics = generateSearchStatistics(orders, queryDTO);
            
            // 生成搜索建议
            List<SearchSuggestion> suggestions = generateSearchSuggestions(queryDTO, total);
            
            AdvancedSearchResult result = new AdvancedSearchResult();
            result.setOrders(orderDTOs);
            result.setTotal(total);
            result.setStatistics(statistics);
            result.setSuggestions(suggestions);
            result.setQueryTime(System.currentTimeMillis());
            
            logger.debug("高级搜索完成，找到 {} 条记录", total);
            return result;
            
        } catch (Exception e) {
            logger.error("高级搜索订单时发生异常", e);
            throw new RuntimeException("搜索失败：" + e.getMessage(), e);
        }
    }
    
    /**
     * 快速搜索订单
     * 用于前端搜索框的实时搜索
     * 
     * @param keyword 关键词
     * @param limit 限制数量
     * @return 搜索结果
     */
    public List<OrderDTO> quickSearch(String keyword, Integer limit) {
        logger.debug("开始快速搜索订单，关键词: {}, 限制: {}", keyword, limit);
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        try {
            OrderQueryDTO queryDTO = new OrderQueryDTO();
            queryDTO.setKeyword(keyword.trim());
            queryDTO.setPage(1);
            queryDTO.setSize(limit != null ? limit : 10);
            queryDTO.setSortBy("createdAt");
            queryDTO.setSortOrder("desc");
            
            List<Order> orders = executeAdvancedSearch(queryDTO);
            
            List<OrderDTO> result = orders.stream()
                .map(this::convertToOrderDTO)
                .collect(Collectors.toList());
            
            logger.debug("快速搜索完成，找到 {} 条记录", result.size());
            return result;
            
        } catch (Exception e) {
            logger.error("快速搜索订单时发生异常", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 智能筛选建议
     * 根据当前搜索条件提供筛选建议
     * 
     * @param queryDTO 查询条件
     * @return 筛选建议
     */
    public FilterSuggestions getFilterSuggestions(OrderQueryDTO queryDTO) {
        logger.debug("获取筛选建议，查询条件: {}", queryDTO);
        
        try {
            FilterSuggestions suggestions = new FilterSuggestions();
            
            // 获取可用的订单类型
            suggestions.setAvailableTypes(getAvailableOrderTypes(queryDTO));
            
            // 获取可用的订单状态
            suggestions.setAvailableStatuses(getAvailableOrderStatuses(queryDTO));
            
            // 获取可用的支付状态
            suggestions.setAvailablePaymentStatuses(getAvailablePaymentStatuses(queryDTO));
            
            // 获取可用的销售人员
            suggestions.setAvailableSalesPersons(getAvailableSalesPersons(queryDTO));
            
            // 获取金额范围建议
            suggestions.setAmountRangeSuggestions(getAmountRangeSuggestions(queryDTO));
            
            // 获取日期范围建议
            suggestions.setDateRangeSuggestions(getDateRangeSuggestions(queryDTO));
            
            return suggestions;
            
        } catch (Exception e) {
            logger.error("获取筛选建议时发生异常", e);
            return new FilterSuggestions();
        }
    }
    
    /**
     * 搜索历史记录
     * 保存和获取用户的搜索历史
     * 
     * @param userId 用户ID
     * @return 搜索历史
     */
    public List<SearchHistory> getSearchHistory(String userId) {
        // TODO: 实现搜索历史功能
        // 这里可以使用Redis或数据库存储用户的搜索历史
        return new ArrayList<>();
    }
    
    /**
     * 保存搜索历史
     * 
     * @param userId 用户ID
     * @param queryDTO 查询条件
     */
    public void saveSearchHistory(String userId, OrderQueryDTO queryDTO) {
        // TODO: 实现保存搜索历史功能
        logger.debug("保存搜索历史，用户: {}, 查询条件: {}", userId, queryDTO);
    }
    
    /**
     * 预处理查询条件
     */
    private void preprocessQueryConditions(OrderQueryDTO queryDTO) {
        // 处理关键词
        if (queryDTO.getKeyword() != null) {
            queryDTO.setKeyword(queryDTO.getKeyword().trim());
        }
        
        // 处理客户名称
        if (queryDTO.getCustomerName() != null) {
            queryDTO.setCustomerName(queryDTO.getCustomerName().trim());
        }
        
        // 处理销售人员
        if (queryDTO.getSalesPerson() != null) {
            queryDTO.setSalesPerson(queryDTO.getSalesPerson().trim());
        }
        
        // 验证日期范围
        if (queryDTO.getStartDate() != null && queryDTO.getEndDate() != null) {
            if (queryDTO.getEndDate().isBefore(queryDTO.getStartDate())) {
                // 交换开始和结束日期
                LocalDate temp = queryDTO.getStartDate();
                queryDTO.setStartDate(queryDTO.getEndDate());
                queryDTO.setEndDate(temp);
            }
        }
        
        // 验证金额范围
        if (queryDTO.getMinAmount() != null && queryDTO.getMaxAmount() != null) {
            if (queryDTO.getMaxAmount().compareTo(queryDTO.getMinAmount()) < 0) {
                // 交换最小和最大金额
                BigDecimal temp = queryDTO.getMinAmount();
                queryDTO.setMinAmount(queryDTO.getMaxAmount());
                queryDTO.setMaxAmount(temp);
            }
        }
        
        // 设置默认排序
        if (queryDTO.getSortBy() == null || queryDTO.getSortBy().isEmpty()) {
            queryDTO.setSortBy("createdAt");
        }
        if (queryDTO.getSortOrder() == null || queryDTO.getSortOrder().isEmpty()) {
            queryDTO.setSortOrder("desc");
        }
    }
    
    /**
     * 执行高级搜索
     */
    private List<Order> executeAdvancedSearch(OrderQueryDTO queryDTO) {
        // 计算分页参数
        int offset = (queryDTO.getPage() - 1) * queryDTO.getSize();
        
        // 执行查询
        return orderMapper.selectOrdersWithPagination(
            queryDTO.getKeyword(),
            queryDTO.getType(),
            queryDTO.getStatus(),
            queryDTO.getStartDate(),
            queryDTO.getEndDate(),
            offset,
            queryDTO.getSize()
        );
    }
    
    /**
     * 统计高级搜索结果数量
     */
    private int countAdvancedSearch(OrderQueryDTO queryDTO) {
        return orderMapper.countOrders(
            queryDTO.getKeyword(),
            queryDTO.getType(),
            queryDTO.getStatus(),
            queryDTO.getStartDate(),
            queryDTO.getEndDate()
        );
    }
    
    /**
     * 生成搜索统计信息
     */
    private SearchStatistics generateSearchStatistics(List<Order> orders, OrderQueryDTO queryDTO) {
        SearchStatistics statistics = new SearchStatistics();
        
        if (orders.isEmpty()) {
            return statistics;
        }
        
        // 按状态统计
        Map<String, Integer> statusCount = new HashMap<>();
        Map<String, Integer> typeCount = new HashMap<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        for (Order order : orders) {
            // 状态统计
            String status = order.getStatus() != null ? order.getStatus().getCode() : "unknown";
            statusCount.put(status, statusCount.getOrDefault(status, 0) + 1);
            
            // 类型统计
            String type = order.getType() != null ? order.getType().getCode() : "unknown";
            typeCount.put(type, typeCount.getOrDefault(type, 0) + 1);
            
            // 金额统计
            if (order.getTotalAmount() != null) {
                totalAmount = totalAmount.add(order.getTotalAmount());
            }
        }
        
        statistics.setStatusCount(statusCount);
        statistics.setTypeCount(typeCount);
        statistics.setTotalAmount(totalAmount);
        statistics.setAverageAmount(orders.size() > 0 ? 
            totalAmount.divide(new BigDecimal(orders.size()), 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);
        
        return statistics;
    }
    
    /**
     * 生成搜索建议
     */
    private List<SearchSuggestion> generateSearchSuggestions(OrderQueryDTO queryDTO, int total) {
        List<SearchSuggestion> suggestions = new ArrayList<>();
        
        if (total == 0) {
            // 无结果时的建议
            suggestions.add(new SearchSuggestion("扩大搜索范围", "尝试移除一些筛选条件"));
            suggestions.add(new SearchSuggestion("检查拼写", "确认关键词拼写是否正确"));
            suggestions.add(new SearchSuggestion("使用通配符", "尝试使用部分关键词搜索"));
        } else if (total > 1000) {
            // 结果太多时的建议
            suggestions.add(new SearchSuggestion("缩小搜索范围", "添加更多筛选条件"));
            suggestions.add(new SearchSuggestion("使用日期筛选", "按时间范围筛选订单"));
            suggestions.add(new SearchSuggestion("按状态筛选", "选择特定的订单状态"));
        }
        
        return suggestions;
    }
    
    /**
     * 获取可用的订单类型
     */
    private List<String> getAvailableOrderTypes(OrderQueryDTO queryDTO) {
        // TODO: 从数据库查询实际可用的订单类型
        List<String> types = new ArrayList<>();
        types.add("sales");
        types.add("rental");
        return types;
    }
    
    /**
     * 获取可用的订单状态
     */
    private List<String> getAvailableOrderStatuses(OrderQueryDTO queryDTO) {
        // TODO: 从数据库查询实际可用的订单状态
        List<String> statuses = new ArrayList<>();
        statuses.add("pending");
        statuses.add("confirmed");
        statuses.add("processing");
        statuses.add("shipped");
        statuses.add("delivered");
        statuses.add("completed");
        statuses.add("cancelled");
        return statuses;
    }
    
    /**
     * 获取可用的支付状态
     */
    private List<String> getAvailablePaymentStatuses(OrderQueryDTO queryDTO) {
        List<String> statuses = new ArrayList<>();
        statuses.add("pending");
        statuses.add("paid");
        statuses.add("failed");
        statuses.add("refunded");
        return statuses;
    }
    
    /**
     * 获取可用的销售人员
     */
    private List<String> getAvailableSalesPersons(OrderQueryDTO queryDTO) {
        // TODO: 从数据库查询实际的销售人员列表
        List<String> salesPersons = new ArrayList<>();
        salesPersons.add("张三");
        salesPersons.add("李四");
        salesPersons.add("王五");
        return salesPersons;
    }
    
    /**
     * 获取金额范围建议
     */
    private List<AmountRange> getAmountRangeSuggestions(OrderQueryDTO queryDTO) {
        List<AmountRange> ranges = new ArrayList<>();
        ranges.add(new AmountRange("0-1000", new BigDecimal("0"), new BigDecimal("1000")));
        ranges.add(new AmountRange("1000-5000", new BigDecimal("1000"), new BigDecimal("5000")));
        ranges.add(new AmountRange("5000-10000", new BigDecimal("5000"), new BigDecimal("10000")));
        ranges.add(new AmountRange("10000+", new BigDecimal("10000"), null));
        return ranges;
    }
    
    /**
     * 获取日期范围建议
     */
    private List<DateRange> getDateRangeSuggestions(OrderQueryDTO queryDTO) {
        List<DateRange> ranges = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        ranges.add(new DateRange("今天", today, today));
        ranges.add(new DateRange("最近7天", today.minusDays(7), today));
        ranges.add(new DateRange("最近30天", today.minusDays(30), today));
        ranges.add(new DateRange("最近90天", today.minusDays(90), today));
        ranges.add(new DateRange("本月", today.withDayOfMonth(1), today));
        ranges.add(new DateRange("上月", today.minusMonths(1).withDayOfMonth(1), 
            today.withDayOfMonth(1).minusDays(1)));
        
        return ranges;
    }
    
    /**
     * 转换Order实体为OrderDTO
     */
    private OrderDTO convertToOrderDTO(Order order) {
        // 使用OrderService的转换方法
        return orderService.getOrderById(order.getId());
    }
    
    // 内部类定义
    
    /**
     * 高级搜索结果
     */
    public static class AdvancedSearchResult {
        private List<OrderDTO> orders;
        private int total;
        private SearchStatistics statistics;
        private List<SearchSuggestion> suggestions;
        private long queryTime;
        
        // Getter和Setter方法
        public List<OrderDTO> getOrders() { return orders; }
        public void setOrders(List<OrderDTO> orders) { this.orders = orders; }
        
        public int getTotal() { return total; }
        public void setTotal(int total) { this.total = total; }
        
        public SearchStatistics getStatistics() { return statistics; }
        public void setStatistics(SearchStatistics statistics) { this.statistics = statistics; }
        
        public List<SearchSuggestion> getSuggestions() { return suggestions; }
        public void setSuggestions(List<SearchSuggestion> suggestions) { this.suggestions = suggestions; }
        
        public long getQueryTime() { return queryTime; }
        public void setQueryTime(long queryTime) { this.queryTime = queryTime; }
    }
    
    /**
     * 搜索统计信息
     */
    public static class SearchStatistics {
        private Map<String, Integer> statusCount = new HashMap<>();
        private Map<String, Integer> typeCount = new HashMap<>();
        private BigDecimal totalAmount = BigDecimal.ZERO;
        private BigDecimal averageAmount = BigDecimal.ZERO;
        
        // Getter和Setter方法
        public Map<String, Integer> getStatusCount() { return statusCount; }
        public void setStatusCount(Map<String, Integer> statusCount) { this.statusCount = statusCount; }
        
        public Map<String, Integer> getTypeCount() { return typeCount; }
        public void setTypeCount(Map<String, Integer> typeCount) { this.typeCount = typeCount; }
        
        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
        
        public BigDecimal getAverageAmount() { return averageAmount; }
        public void setAverageAmount(BigDecimal averageAmount) { this.averageAmount = averageAmount; }
    }
    
    /**
     * 搜索建议
     */
    public static class SearchSuggestion {
        private String title;
        private String description;
        
        public SearchSuggestion(String title, String description) {
            this.title = title;
            this.description = description;
        }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    /**
     * 筛选建议
     */
    public static class FilterSuggestions {
        private List<String> availableTypes = new ArrayList<>();
        private List<String> availableStatuses = new ArrayList<>();
        private List<String> availablePaymentStatuses = new ArrayList<>();
        private List<String> availableSalesPersons = new ArrayList<>();
        private List<AmountRange> amountRangeSuggestions = new ArrayList<>();
        private List<DateRange> dateRangeSuggestions = new ArrayList<>();
        
        // Getter和Setter方法
        public List<String> getAvailableTypes() { return availableTypes; }
        public void setAvailableTypes(List<String> availableTypes) { this.availableTypes = availableTypes; }
        
        public List<String> getAvailableStatuses() { return availableStatuses; }
        public void setAvailableStatuses(List<String> availableStatuses) { this.availableStatuses = availableStatuses; }
        
        public List<String> getAvailablePaymentStatuses() { return availablePaymentStatuses; }
        public void setAvailablePaymentStatuses(List<String> availablePaymentStatuses) { this.availablePaymentStatuses = availablePaymentStatuses; }
        
        public List<String> getAvailableSalesPersons() { return availableSalesPersons; }
        public void setAvailableSalesPersons(List<String> availableSalesPersons) { this.availableSalesPersons = availableSalesPersons; }
        
        public List<AmountRange> getAmountRangeSuggestions() { return amountRangeSuggestions; }
        public void setAmountRangeSuggestions(List<AmountRange> amountRangeSuggestions) { this.amountRangeSuggestions = amountRangeSuggestions; }
        
        public List<DateRange> getDateRangeSuggestions() { return dateRangeSuggestions; }
        public void setDateRangeSuggestions(List<DateRange> dateRangeSuggestions) { this.dateRangeSuggestions = dateRangeSuggestions; }
    }
    
    /**
     * 金额范围
     */
    public static class AmountRange {
        private String label;
        private BigDecimal min;
        private BigDecimal max;
        
        public AmountRange(String label, BigDecimal min, BigDecimal max) {
            this.label = label;
            this.min = min;
            this.max = max;
        }
        
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        
        public BigDecimal getMin() { return min; }
        public void setMin(BigDecimal min) { this.min = min; }
        
        public BigDecimal getMax() { return max; }
        public void setMax(BigDecimal max) { this.max = max; }
    }
    
    /**
     * 日期范围
     */
    public static class DateRange {
        private String label;
        private LocalDate startDate;
        private LocalDate endDate;
        
        public DateRange(String label, LocalDate startDate, LocalDate endDate) {
            this.label = label;
            this.startDate = startDate;
            this.endDate = endDate;
        }
        
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        
        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
        
        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    }
    
    /**
     * 搜索历史
     */
    public static class SearchHistory {
        private String query;
        private LocalDate searchDate;
        private int resultCount;
        
        public String getQuery() { return query; }
        public void setQuery(String query) { this.query = query; }
        
        public LocalDate getSearchDate() { return searchDate; }
        public void setSearchDate(LocalDate searchDate) { this.searchDate = searchDate; }
        
        public int getResultCount() { return resultCount; }
        public void setResultCount(int resultCount) { this.resultCount = resultCount; }
    }
}