package com.yxrobot.util;

import com.yxrobot.dto.OrderQueryDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 搜索优化工具类
 * 提供搜索性能优化和索引策略建议
 */
public class SearchOptimizationUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(SearchOptimizationUtil.class);
    
    // 常用的搜索模式
    private static final Pattern ORDER_NUMBER_PATTERN = Pattern.compile("^[A-Z]{3}\\d{10}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    /**
     * 优化搜索关键词
     * 清理和标准化搜索关键词，提高搜索准确性
     * 
     * @param keyword 原始关键词
     * @return 优化后的关键词
     */
    public static String optimizeKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }
        
        String optimized = keyword.trim();
        
        // 移除多余的空格
        optimized = optimized.replaceAll("\\s+", " ");
        
        // 移除特殊字符（保留中文、英文、数字、空格、连字符）
        optimized = optimized.replaceAll("[^\\u4e00-\\u9fa5a-zA-Z0-9\\s\\-_]", "");
        
        // 转换为小写（除了订单号等需要保持大小写的情况）
        if (!ORDER_NUMBER_PATTERN.matcher(optimized).matches()) {
            optimized = optimized.toLowerCase();
        }
        
        logger.debug("关键词优化: {} -> {}", keyword, optimized);
        return optimized.isEmpty() ? null : optimized;
    }
    
    /**
     * 检测搜索类型
     * 根据关键词模式自动检测搜索类型，优化搜索策略
     * 
     * @param keyword 搜索关键词
     * @return 搜索类型
     */
    public static SearchType detectSearchType(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return SearchType.GENERAL;
        }
        
        String trimmed = keyword.trim();
        
        // 订单号搜索
        if (ORDER_NUMBER_PATTERN.matcher(trimmed).matches()) {
            return SearchType.ORDER_NUMBER;
        }
        
        // 电话号码搜索
        if (PHONE_PATTERN.matcher(trimmed).matches()) {
            return SearchType.PHONE;
        }
        
        // 邮箱搜索
        if (EMAIL_PATTERN.matcher(trimmed).matches()) {
            return SearchType.EMAIL;
        }
        
        // 数字搜索（可能是ID或金额）
        if (trimmed.matches("^\\d+(\\.\\d+)?$")) {
            return SearchType.NUMERIC;
        }
        
        // 日期搜索
        if (trimmed.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            return SearchType.DATE;
        }
        
        // 中文姓名搜索
        if (trimmed.matches("^[\\u4e00-\\u9fa5]{2,10}$")) {
            return SearchType.CHINESE_NAME;
        }
        
        // 英文姓名搜索
        if (trimmed.matches("^[a-zA-Z\\s]{2,50}$")) {
            return SearchType.ENGLISH_NAME;
        }
        
        return SearchType.GENERAL;
    }
    
    /**
     * 生成搜索建议
     * 根据搜索类型和结果提供搜索建议
     * 
     * @param keyword 搜索关键词
     * @param searchType 搜索类型
     * @param resultCount 结果数量
     * @return 搜索建议列表
     */
    public static List<String> generateSearchSuggestions(String keyword, SearchType searchType, int resultCount) {
        List<String> suggestions = new ArrayList<>();
        
        if (resultCount == 0) {
            // 无结果时的建议
            switch (searchType) {
                case ORDER_NUMBER:
                    suggestions.add("请检查订单号是否正确");
                    suggestions.add("订单号格式：3位字母+10位数字");
                    break;
                case PHONE:
                    suggestions.add("请检查手机号是否正确");
                    suggestions.add("尝试搜索客户姓名");
                    break;
                case EMAIL:
                    suggestions.add("请检查邮箱地址是否正确");
                    suggestions.add("尝试搜索客户姓名");
                    break;
                case NUMERIC:
                    suggestions.add("尝试搜索订单金额范围");
                    suggestions.add("可能是客户ID，尝试搜索客户姓名");
                    break;
                case CHINESE_NAME:
                case ENGLISH_NAME:
                    suggestions.add("尝试输入姓名的一部分");
                    suggestions.add("检查姓名拼写是否正确");
                    break;
                default:
                    suggestions.add("尝试使用更具体的关键词");
                    suggestions.add("检查拼写是否正确");
                    suggestions.add("尝试使用订单号或客户姓名搜索");
            }
        } else if (resultCount > 100) {
            // 结果太多时的建议
            suggestions.add("结果较多，建议添加筛选条件");
            suggestions.add("尝试使用日期范围筛选");
            suggestions.add("按订单状态筛选");
        }
        
        return suggestions;
    }
    
    /**
     * 优化查询条件
     * 根据搜索模式优化查询条件，提高搜索效率
     * 
     * @param queryDTO 查询条件
     * @return 优化后的查询条件
     */
    public static OrderQueryDTO optimizeQueryConditions(OrderQueryDTO queryDTO) {
        if (queryDTO == null) {
            return new OrderQueryDTO();
        }
        
        // 优化关键词
        if (queryDTO.getKeyword() != null) {
            String optimizedKeyword = optimizeKeyword(queryDTO.getKeyword());
            queryDTO.setKeyword(optimizedKeyword);
            
            // 根据关键词类型自动设置特定字段
            if (optimizedKeyword != null) {
                SearchType searchType = detectSearchType(optimizedKeyword);
                autoSetSpecificFields(queryDTO, optimizedKeyword, searchType);
            }
        }
        
        // 优化分页参数
        if (queryDTO.getPage() == null || queryDTO.getPage() < 1) {
            queryDTO.setPage(1);
        }
        if (queryDTO.getSize() == null || queryDTO.getSize() < 1) {
            queryDTO.setSize(10);
        }
        if (queryDTO.getSize() > 100) {
            queryDTO.setSize(100); // 限制最大页面大小
        }
        
        // 优化排序参数
        if (queryDTO.getSortBy() == null || queryDTO.getSortBy().isEmpty()) {
            queryDTO.setSortBy("createdAt");
        }
        if (queryDTO.getSortOrder() == null || queryDTO.getSortOrder().isEmpty()) {
            queryDTO.setSortOrder("desc");
        }
        
        // 验证排序字段
        if (!isValidSortField(queryDTO.getSortBy())) {
            queryDTO.setSortBy("createdAt");
        }
        
        logger.debug("查询条件优化完成: {}", queryDTO);
        return queryDTO;
    }
    
    /**
     * 根据搜索类型自动设置特定字段
     */
    private static void autoSetSpecificFields(OrderQueryDTO queryDTO, String keyword, SearchType searchType) {
        switch (searchType) {
            case ORDER_NUMBER:
                queryDTO.setOrderNumber(keyword);
                queryDTO.setKeyword(null); // 清除通用关键词，使用精确匹配
                break;
            case PHONE:
                // 可以设置客户电话搜索（如果有这个字段）
                break;
            case EMAIL:
                // 可以设置客户邮箱搜索（如果有这个字段）
                break;
            case NUMERIC:
                // 如果是纯数字，可能是金额或ID
                try {
                    double amount = Double.parseDouble(keyword);
                    if (amount > 0 && amount < 1000000) {
                        // 可能是金额，设置金额范围搜索
                        queryDTO.setMinAmount(new java.math.BigDecimal(amount * 0.9));
                        queryDTO.setMaxAmount(new java.math.BigDecimal(amount * 1.1));
                    }
                } catch (NumberFormatException e) {
                    // 忽略转换错误
                }
                break;
            case CHINESE_NAME:
            case ENGLISH_NAME:
                queryDTO.setCustomerName(keyword);
                break;
        }
    }
    
    /**
     * 验证排序字段是否有效
     */
    private static boolean isValidSortField(String sortBy) {
        String[] validFields = {
            "id", "orderNumber", "type", "status", "customerId", "totalAmount",
            "paymentStatus", "createdAt", "updatedAt", "expectedDeliveryDate"
        };
        
        for (String field : validFields) {
            if (field.equals(sortBy)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 生成索引建议
     * 根据查询模式建议数据库索引策略
     * 
     * @param queryDTO 查询条件
     * @return 索引建议列表
     */
    public static List<IndexSuggestion> generateIndexSuggestions(OrderQueryDTO queryDTO) {
        List<IndexSuggestion> suggestions = new ArrayList<>();
        
        // 基础索引建议
        suggestions.add(new IndexSuggestion("orders", "idx_orders_created_at", 
            "CREATE INDEX idx_orders_created_at ON orders(created_at DESC)", 
            "优化按创建时间排序的查询"));
        
        suggestions.add(new IndexSuggestion("orders", "idx_orders_status", 
            "CREATE INDEX idx_orders_status ON orders(status)", 
            "优化按状态筛选的查询"));
        
        suggestions.add(new IndexSuggestion("orders", "idx_orders_customer_id", 
            "CREATE INDEX idx_orders_customer_id ON orders(customer_id)", 
            "优化按客户筛选的查询"));
        
        // 复合索引建议
        if (queryDTO.getType() != null && queryDTO.getStatus() != null) {
            suggestions.add(new IndexSuggestion("orders", "idx_orders_type_status", 
                "CREATE INDEX idx_orders_type_status ON orders(type, status)", 
                "优化按类型和状态组合筛选的查询"));
        }
        
        if (queryDTO.getStartDate() != null || queryDTO.getEndDate() != null) {
            suggestions.add(new IndexSuggestion("orders", "idx_orders_date_status", 
                "CREATE INDEX idx_orders_date_status ON orders(created_at, status)", 
                "优化按日期范围和状态筛选的查询"));
        }
        
        // 全文搜索索引建议
        if (queryDTO.getKeyword() != null) {
            suggestions.add(new IndexSuggestion("orders", "idx_orders_fulltext", 
                "CREATE FULLTEXT INDEX idx_orders_fulltext ON orders(order_number, notes)", 
                "优化关键词搜索性能"));
        }
        
        return suggestions;
    }
    
    /**
     * 计算查询复杂度
     * 评估查询的复杂度，用于性能优化
     * 
     * @param queryDTO 查询条件
     * @return 复杂度分数（1-10，10最复杂）
     */
    public static int calculateQueryComplexity(OrderQueryDTO queryDTO) {
        int complexity = 1;
        
        // 基础查询
        if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().isEmpty()) {
            complexity += 2; // 关键词搜索增加复杂度
        }
        
        // 筛选条件
        if (queryDTO.getType() != null) complexity += 1;
        if (queryDTO.getStatus() != null) complexity += 1;
        if (queryDTO.getPaymentStatus() != null) complexity += 1;
        if (queryDTO.getCustomerId() != null) complexity += 1;
        if (queryDTO.getCustomerName() != null) complexity += 1;
        if (queryDTO.getSalesPerson() != null) complexity += 1;
        
        // 范围查询
        if (queryDTO.getStartDate() != null || queryDTO.getEndDate() != null) {
            complexity += 2;
        }
        if (queryDTO.getMinAmount() != null || queryDTO.getMaxAmount() != null) {
            complexity += 2;
        }
        
        // 排序
        if (queryDTO.getSortBy() != null && !queryDTO.getSortBy().equals("createdAt")) {
            complexity += 1;
        }
        
        return Math.min(complexity, 10);
    }
    
    /**
     * 搜索类型枚举
     */
    public enum SearchType {
        GENERAL,        // 通用搜索
        ORDER_NUMBER,   // 订单号搜索
        PHONE,          // 电话号码搜索
        EMAIL,          // 邮箱搜索
        NUMERIC,        // 数字搜索
        DATE,           // 日期搜索
        CHINESE_NAME,   // 中文姓名搜索
        ENGLISH_NAME    // 英文姓名搜索
    }
    
    /**
     * 索引建议类
     */
    public static class IndexSuggestion {
        private String tableName;
        private String indexName;
        private String createSql;
        private String description;
        
        public IndexSuggestion(String tableName, String indexName, String createSql, String description) {
            this.tableName = tableName;
            this.indexName = indexName;
            this.createSql = createSql;
            this.description = description;
        }
        
        // Getter方法
        public String getTableName() { return tableName; }
        public String getIndexName() { return indexName; }
        public String getCreateSql() { return createSql; }
        public String getDescription() { return description; }
        
        @Override
        public String toString() {
            return String.format("IndexSuggestion{table='%s', index='%s', description='%s'}", 
                tableName, indexName, description);
        }
    }
}