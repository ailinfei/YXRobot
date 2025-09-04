package com.yxrobot.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库查询优化工具类
 * 提供数据库性能分析和优化建议
 */
@Component
public class DatabaseOptimizer {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseOptimizer.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * 分析订单表的查询性能
     * 
     * @return 性能分析结果
     */
    public Map<String, Object> analyzeOrderTablePerformance() {
        Map<String, Object> analysis = new HashMap<>();
        
        try {
            // 分析表大小
            Map<String, Object> tableSize = analyzeTableSize();
            analysis.put("tableSize", tableSize);
            
            // 分析索引使用情况
            List<Map<String, Object>> indexUsage = analyzeIndexUsage();
            analysis.put("indexUsage", indexUsage);
            
            // 分析慢查询
            List<String> slowQueries = identifySlowQueries();
            analysis.put("slowQueries", slowQueries);
            
            // 生成优化建议
            List<String> recommendations = generateOptimizationRecommendations(tableSize, indexUsage);
            analysis.put("recommendations", recommendations);
            
            logger.info("数据库性能分析完成，发现 {} 个优化建议", recommendations.size());
            
        } catch (Exception e) {
            logger.error("数据库性能分析失败", e);
            analysis.put("error", "分析失败：" + e.getMessage());
        }
        
        return analysis;
    }
    
    /**
     * 分析表大小
     */
    private Map<String, Object> analyzeTableSize() {
        Map<String, Object> tableSize = new HashMap<>();
        
        try {
            // 查询订单表记录数
            String countSql = "SELECT COUNT(*) FROM orders WHERE is_deleted = 0";
            Long orderCount = jdbcTemplate.queryForObject(countSql, Long.class);
            tableSize.put("orderCount", orderCount);
            
            // 查询订单商品表记录数
            String itemCountSql = "SELECT COUNT(*) FROM order_items";
            Long itemCount = jdbcTemplate.queryForObject(itemCountSql, Long.class);
            tableSize.put("orderItemCount", itemCount);
            
            // 查询表大小信息（MySQL特定）
            String tableSizeSql = """
                SELECT 
                    table_name,
                    ROUND(((data_length + index_length) / 1024 / 1024), 2) AS size_mb,
                    table_rows
                FROM information_schema.tables 
                WHERE table_schema = DATABASE() 
                AND table_name IN ('orders', 'order_items', 'shipping_info', 'order_logs')
                """;
            
            List<Map<String, Object>> tableSizes = jdbcTemplate.queryForList(tableSizeSql);
            tableSize.put("tableSizes", tableSizes);
            
        } catch (Exception e) {
            logger.warn("获取表大小信息失败", e);
            tableSize.put("error", "无法获取表大小信息");
        }
        
        return tableSize;
    }
    
    /**
     * 分析索引使用情况
     */
    private List<Map<String, Object>> analyzeIndexUsage() {
        List<Map<String, Object>> indexUsage = new ArrayList<>();
        
        try {
            // 查询索引信息（MySQL特定）
            String indexSql = """
                SELECT 
                    table_name,
                    index_name,
                    column_name,
                    cardinality,
                    index_type
                FROM information_schema.statistics 
                WHERE table_schema = DATABASE() 
                AND table_name IN ('orders', 'order_items', 'shipping_info', 'order_logs')
                ORDER BY table_name, index_name, seq_in_index
                """;
            
            indexUsage = jdbcTemplate.queryForList(indexSql);
            
        } catch (Exception e) {
            logger.warn("获取索引信息失败", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "无法获取索引信息");
            indexUsage.add(error);
        }
        
        return indexUsage;
    }
    
    /**
     * 识别慢查询
     */
    private List<String> identifySlowQueries() {
        List<String> slowQueries = new ArrayList<>();
        
        // 这里列出一些可能的慢查询场景
        slowQueries.add("大量数据的全表扫描查询");
        slowQueries.add("没有使用索引的复杂JOIN查询");
        slowQueries.add("没有LIMIT的大结果集查询");
        slowQueries.add("复杂的统计查询没有适当的索引");
        
        return slowQueries;
    }
    
    /**
     * 生成优化建议
     */
    private List<String> generateOptimizationRecommendations(Map<String, Object> tableSize, List<Map<String, Object>> indexUsage) {
        List<String> recommendations = new ArrayList<>();
        
        // 基于表大小的建议
        Long orderCount = (Long) tableSize.get("orderCount");
        if (orderCount != null && orderCount > 100000) {
            recommendations.add("订单表记录数较多(" + orderCount + ")，建议考虑分表或归档历史数据");
        }
        
        // 基于索引的建议
        boolean hasCreatedAtIndex = false;
        boolean hasStatusIndex = false;
        boolean hasCustomerIdIndex = false;
        
        for (Map<String, Object> index : indexUsage) {
            String columnName = (String) index.get("column_name");
            if ("created_at".equals(columnName)) {
                hasCreatedAtIndex = true;
            } else if ("status".equals(columnName)) {
                hasStatusIndex = true;
            } else if ("customer_id".equals(columnName)) {
                hasCustomerIdIndex = true;
            }
        }
        
        if (!hasCreatedAtIndex) {
            recommendations.add("建议为orders表的created_at字段添加索引，优化按时间查询的性能");
        }
        if (!hasStatusIndex) {
            recommendations.add("建议为orders表的status字段添加索引，优化按状态筛选的性能");
        }
        if (!hasCustomerIdIndex) {
            recommendations.add("建议为orders表的customer_id字段添加索引，优化按客户查询的性能");
        }
        
        // 通用优化建议
        recommendations.add("定期执行ANALYZE TABLE命令更新表统计信息");
        recommendations.add("考虑使用复合索引优化多条件查询");
        recommendations.add("定期清理软删除的数据或移至历史表");
        recommendations.add("监控慢查询日志，识别需要优化的SQL语句");
        
        return recommendations;
    }
    
    /**
     * 执行数据库优化操作
     * 
     * @return 优化结果
     */
    public Map<String, Object> optimizeDatabase() {
        Map<String, Object> result = new HashMap<>();
        List<String> operations = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        
        try {
            // 更新表统计信息
            try {
                jdbcTemplate.execute("ANALYZE TABLE orders");
                operations.add("已更新orders表统计信息");
            } catch (Exception e) {
                errors.add("更新orders表统计信息失败：" + e.getMessage());
            }
            
            try {
                jdbcTemplate.execute("ANALYZE TABLE order_items");
                operations.add("已更新order_items表统计信息");
            } catch (Exception e) {
                errors.add("更新order_items表统计信息失败：" + e.getMessage());
            }
            
            // 优化表
            try {
                jdbcTemplate.execute("OPTIMIZE TABLE orders");
                operations.add("已优化orders表");
            } catch (Exception e) {
                errors.add("优化orders表失败：" + e.getMessage());
            }
            
            result.put("operations", operations);
            result.put("errors", errors);
            result.put("success", errors.isEmpty());
            
            logger.info("数据库优化完成，执行了 {} 个操作，发生了 {} 个错误", operations.size(), errors.size());
            
        } catch (Exception e) {
            logger.error("数据库优化过程中发生异常", e);
            result.put("error", "优化过程异常：" + e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }
    
    /**
     * 检查必要的索引是否存在
     * 
     * @return 索引检查结果
     */
    public Map<String, Object> checkRequiredIndexes() {
        Map<String, Object> result = new HashMap<>();
        List<String> missingIndexes = new ArrayList<>();
        List<String> existingIndexes = new ArrayList<>();
        
        // 定义必要的索引
        String[] requiredIndexes = {
            "idx_orders_created_at",
            "idx_orders_status", 
            "idx_orders_customer_id",
            "idx_orders_order_number",
            "idx_order_items_order_id",
            "idx_order_items_product_id"
        };
        
        try {
            // 查询现有索引
            String sql = """
                SELECT DISTINCT index_name 
                FROM information_schema.statistics 
                WHERE table_schema = DATABASE() 
                AND table_name IN ('orders', 'order_items')
                AND index_name != 'PRIMARY'
                """;
            
            List<String> currentIndexes = jdbcTemplate.queryForList(sql, String.class);
            
            // 检查每个必要的索引
            for (String requiredIndex : requiredIndexes) {
                if (currentIndexes.contains(requiredIndex)) {
                    existingIndexes.add(requiredIndex);
                } else {
                    missingIndexes.add(requiredIndex);
                }
            }
            
            result.put("existingIndexes", existingIndexes);
            result.put("missingIndexes", missingIndexes);
            result.put("allIndexesExist", missingIndexes.isEmpty());
            
        } catch (Exception e) {
            logger.error("检查索引时发生异常", e);
            result.put("error", "检查索引失败：" + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 生成创建缺失索引的SQL语句
     * 
     * @param missingIndexes 缺失的索引列表
     * @return SQL语句列表
     */
    public List<String> generateCreateIndexSql(List<String> missingIndexes) {
        List<String> sqlStatements = new ArrayList<>();
        
        for (String indexName : missingIndexes) {
            String sql = switch (indexName) {
                case "idx_orders_created_at" -> "CREATE INDEX idx_orders_created_at ON orders(created_at DESC)";
                case "idx_orders_status" -> "CREATE INDEX idx_orders_status ON orders(status)";
                case "idx_orders_customer_id" -> "CREATE INDEX idx_orders_customer_id ON orders(customer_id)";
                case "idx_orders_order_number" -> "CREATE UNIQUE INDEX idx_orders_order_number ON orders(order_number)";
                case "idx_order_items_order_id" -> "CREATE INDEX idx_order_items_order_id ON order_items(order_id)";
                case "idx_order_items_product_id" -> "CREATE INDEX idx_order_items_product_id ON order_items(product_id)";
                default -> null;
            };
            
            if (sql != null) {
                sqlStatements.add(sql);
            }
        }
        
        return sqlStatements;
    }
}