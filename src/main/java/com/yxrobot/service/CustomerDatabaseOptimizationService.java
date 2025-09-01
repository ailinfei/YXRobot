package com.yxrobot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 客户管理数据库优化服务
 * 提供数据库索引监控和优化建议
 */
@Service
public class CustomerDatabaseOptimizationService {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerDatabaseOptimizationService.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * 检查数据库索引状态
     */
    public DatabaseIndexReport checkDatabaseIndexes() {
        logger.info("开始检查客户管理相关表的索引状态");
        
        DatabaseIndexReport report = new DatabaseIndexReport();
        
        try {
            // 检查主要表的索引
            List<IndexInfo> customerIndexes = checkTableIndexes("customers");
            List<IndexInfo> deviceIndexes = checkTableIndexes("customer_device_relation");
            List<IndexInfo> orderIndexes = checkTableIndexes("customer_order_relation");
            List<IndexInfo> serviceIndexes = checkTableIndexes("customer_service_relation");
            
            report.setCustomerIndexes(customerIndexes);
            report.setDeviceRelationIndexes(deviceIndexes);
            report.setOrderRelationIndexes(orderIndexes);
            report.setServiceRelationIndexes(serviceIndexes);
            
            // 生成优化建议
            List<OptimizationSuggestion> suggestions = generateOptimizationSuggestions(report);
            report.setOptimizationSuggestions(suggestions);
            
            // 计算索引健康度
            double healthScore = calculateIndexHealthScore(report);
            report.setHealthScore(healthScore);
            
            logger.info("索引检查完成，健康度: {:.2f}%", healthScore);
            
        } catch (Exception e) {
            logger.error("检查数据库索引失败", e);
            report.setError("检查索引失败: " + e.getMessage());
        }
        
        return report;
    }
    
    /**
     * 检查指定表的索引
     */
    private List<IndexInfo> checkTableIndexes(String tableName) {
        List<IndexInfo> indexes = new ArrayList<>();
        
        try {
            String sql = """
                SELECT 
                    INDEX_NAME,
                    COLUMN_NAME,
                    CARDINALITY,
                    SUB_PART,
                    NULLABLE,
                    INDEX_TYPE,
                    NON_UNIQUE
                FROM information_schema.STATISTICS 
                WHERE TABLE_SCHEMA = DATABASE() 
                    AND TABLE_NAME = ?
                ORDER BY INDEX_NAME, SEQ_IN_INDEX
                """;
            
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, tableName);
            
            Map<String, IndexInfo> indexMap = new HashMap<>();
            
            for (Map<String, Object> row : results) {
                String indexName = (String) row.get("INDEX_NAME");
                String columnName = (String) row.get("COLUMN_NAME");
                Long cardinality = (Long) row.get("CARDINALITY");
                String indexType = (String) row.get("INDEX_TYPE");
                Integer nonUnique = (Integer) row.get("NON_UNIQUE");
                
                IndexInfo indexInfo = indexMap.computeIfAbsent(indexName, k -> {
                    IndexInfo info = new IndexInfo();
                    info.setIndexName(k);
                    info.setTableName(tableName);
                    info.setIndexType(indexType);
                    info.setUnique(nonUnique == 0);
                    info.setColumns(new ArrayList<>());
                    return info;
                });
                
                indexInfo.getColumns().add(columnName);
                if (cardinality != null && cardinality > indexInfo.getCardinality()) {
                    indexInfo.setCardinality(cardinality);
                }
            }
            
            indexes.addAll(indexMap.values());
            
        } catch (Exception e) {
            logger.warn("检查表 {} 的索引失败: {}", tableName, e.getMessage());
        }
        
        return indexes;
    }
    
    /**
     * 生成优化建议
     */
    private List<OptimizationSuggestion> generateOptimizationSuggestions(DatabaseIndexReport report) {
        List<OptimizationSuggestion> suggestions = new ArrayList<>();
        
        // 检查customers表的索引
        checkCustomerTableIndexes(report.getCustomerIndexes(), suggestions);
        
        // 检查关联表的索引
        checkRelationTableIndexes(report.getDeviceRelationIndexes(), "customer_device_relation", suggestions);
        checkRelationTableIndexes(report.getOrderRelationIndexes(), "customer_order_relation", suggestions);
        checkRelationTableIndexes(report.getServiceRelationIndexes(), "customer_service_relation", suggestions);
        
        // 检查复合索引建议
        checkCompositeIndexSuggestions(suggestions);
        
        return suggestions;
    }
    
    /**
     * 检查customers表索引
     */
    private void checkCustomerTableIndexes(List<IndexInfo> indexes, List<OptimizationSuggestion> suggestions) {
        Set<String> existingIndexColumns = new HashSet<>();
        
        for (IndexInfo index : indexes) {
            existingIndexColumns.addAll(index.getColumns());
        }
        
        // 必需的单列索引
        Map<String, String> requiredIndexes = Map.of(
            "customer_name", "支持客户姓名搜索",
            "phone", "支持电话号码搜索和唯一性检查",
            "email", "支持邮箱搜索和唯一性检查",
            "customer_level", "支持客户等级筛选",
            "customer_status", "支持客户状态筛选",
            "region", "支持地区筛选",
            "created_at", "支持创建时间排序",
            "last_active_at", "支持最后活跃时间排序"
        );
        
        for (Map.Entry<String, String> entry : requiredIndexes.entrySet()) {
            String column = entry.getKey();
            String purpose = entry.getValue();
            
            if (!existingIndexColumns.contains(column)) {
                suggestions.add(new OptimizationSuggestion(
                    "MISSING_INDEX",
                    "HIGH",
                    "customers表缺少" + column + "字段的索引",
                    "CREATE INDEX idx_customers_" + column + " ON customers(" + column + ");",
                    purpose
                ));
            }
        }
        
        // 检查复合索引
        boolean hasStatusLevelIndex = indexes.stream()
            .anyMatch(idx -> idx.getColumns().contains("customer_status") && 
                           idx.getColumns().contains("customer_level"));
        
        if (!hasStatusLevelIndex) {
            suggestions.add(new OptimizationSuggestion(
                "MISSING_COMPOSITE_INDEX",
                "MEDIUM",
                "建议创建(customer_status, customer_level)复合索引",
                "CREATE INDEX idx_customers_status_level ON customers(customer_status, customer_level);",
                "优化状态和等级组合筛选查询"
            ));
        }
    }
    
    /**
     * 检查关联表索引
     */
    private void checkRelationTableIndexes(List<IndexInfo> indexes, String tableName, 
                                         List<OptimizationSuggestion> suggestions) {
        Set<String> existingIndexColumns = new HashSet<>();
        
        for (IndexInfo index : indexes) {
            existingIndexColumns.addAll(index.getColumns());
        }
        
        // 关联表必需的索引
        if (!existingIndexColumns.contains("customer_id")) {
            suggestions.add(new OptimizationSuggestion(
                "MISSING_INDEX",
                "HIGH",
                tableName + "表缺少customer_id字段的索引",
                "CREATE INDEX idx_" + tableName + "_customer_id ON " + tableName + "(customer_id);",
                "优化根据客户ID查询关联数据的性能"
            ));
        }
        
        // 检查其他关键字段
        String[] keyFields = getKeyFieldsForTable(tableName);
        for (String field : keyFields) {
            if (!existingIndexColumns.contains(field)) {
                suggestions.add(new OptimizationSuggestion(
                    "MISSING_INDEX",
                    "MEDIUM",
                    tableName + "表缺少" + field + "字段的索引",
                    "CREATE INDEX idx_" + tableName + "_" + field + " ON " + tableName + "(" + field + ");",
                    "优化" + field + "字段的查询性能"
                ));
            }
        }
    }
    
    /**
     * 获取表的关键字段
     */
    private String[] getKeyFieldsForTable(String tableName) {
        return switch (tableName) {
            case "customer_device_relation" -> new String[]{"device_id", "relation_type"};
            case "customer_order_relation" -> new String[]{"order_id", "customer_role"};
            case "customer_service_relation" -> new String[]{"service_record_id", "customer_role"};
            default -> new String[]{};
        };
    }
    
    /**
     * 检查复合索引建议
     */
    private void checkCompositeIndexSuggestions(List<OptimizationSuggestion> suggestions) {
        // 基于常见查询模式的复合索引建议
        suggestions.add(new OptimizationSuggestion(
            "PERFORMANCE_OPTIMIZATION",
            "LOW",
            "建议创建分页查询优化索引",
            "CREATE INDEX idx_customers_deleted_created ON customers(is_deleted, created_at DESC);",
            "优化分页查询性能，支持按创建时间降序排列"
        ));
        
        suggestions.add(new OptimizationSuggestion(
            "PERFORMANCE_OPTIMIZATION",
            "LOW",
            "建议创建搜索优化索引",
            "CREATE INDEX idx_customers_name_phone ON customers(customer_name, phone);",
            "优化姓名和电话的组合搜索"
        ));
    }
    
    /**
     * 计算索引健康度
     */
    private double calculateIndexHealthScore(DatabaseIndexReport report) {
        int totalRequiredIndexes = 20; // 预期的索引总数
        int existingIndexes = 0;
        
        existingIndexes += report.getCustomerIndexes().size();
        existingIndexes += report.getDeviceRelationIndexes().size();
        existingIndexes += report.getOrderRelationIndexes().size();
        existingIndexes += report.getServiceRelationIndexes().size();
        
        // 基础分数
        double baseScore = Math.min(100.0, (double) existingIndexes / totalRequiredIndexes * 100);
        
        // 根据优化建议调整分数
        int highPrioritySuggestions = (int) report.getOptimizationSuggestions().stream()
            .filter(s -> "HIGH".equals(s.getPriority()))
            .count();
        
        int mediumPrioritySuggestions = (int) report.getOptimizationSuggestions().stream()
            .filter(s -> "MEDIUM".equals(s.getPriority()))
            .count();
        
        // 高优先级建议每个扣10分，中优先级建议每个扣5分
        double penalty = highPrioritySuggestions * 10 + mediumPrioritySuggestions * 5;
        
        return Math.max(0, baseScore - penalty);
    }
    
    /**
     * 获取表大小统计
     */
    public Map<String, TableStats> getTableStats() {
        Map<String, TableStats> stats = new HashMap<>();
        
        String[] tables = {"customers", "customer_device_relation", "customer_order_relation", "customer_service_relation"};
        
        for (String table : tables) {
            try {
                TableStats tableStats = getTableStatsForTable(table);
                stats.put(table, tableStats);
            } catch (Exception e) {
                logger.warn("获取表 {} 统计信息失败: {}", table, e.getMessage());
            }
        }
        
        return stats;
    }
    
    /**
     * 获取单个表的统计信息
     */
    private TableStats getTableStatsForTable(String tableName) {
        TableStats stats = new TableStats();
        stats.setTableName(tableName);
        
        try {
            // 获取行数
            String countSql = "SELECT COUNT(*) FROM " + tableName;
            Long rowCount = jdbcTemplate.queryForObject(countSql, Long.class);
            stats.setRowCount(rowCount != null ? rowCount : 0);
            
            // 获取表大小信息
            String sizeSql = """
                SELECT 
                    ROUND(((data_length + index_length) / 1024 / 1024), 2) AS size_mb,
                    ROUND((data_length / 1024 / 1024), 2) AS data_size_mb,
                    ROUND((index_length / 1024 / 1024), 2) AS index_size_mb
                FROM information_schema.TABLES 
                WHERE table_schema = DATABASE() 
                    AND table_name = ?
                """;
            
            Map<String, Object> sizeInfo = jdbcTemplate.queryForMap(sizeSql, tableName);
            stats.setSizeMB((Double) sizeInfo.get("size_mb"));
            stats.setDataSizeMB((Double) sizeInfo.get("data_size_mb"));
            stats.setIndexSizeMB((Double) sizeInfo.get("index_size_mb"));
            
        } catch (Exception e) {
            logger.warn("获取表 {} 统计信息失败: {}", tableName, e.getMessage());
        }
        
        return stats;
    }
    
    /**
     * 数据库索引报告
     */
    public static class DatabaseIndexReport {
        private List<IndexInfo> customerIndexes;
        private List<IndexInfo> deviceRelationIndexes;
        private List<IndexInfo> orderRelationIndexes;
        private List<IndexInfo> serviceRelationIndexes;
        private List<OptimizationSuggestion> optimizationSuggestions;
        private double healthScore;
        private String error;
        
        // Getters and Setters
        public List<IndexInfo> getCustomerIndexes() { return customerIndexes; }
        public void setCustomerIndexes(List<IndexInfo> customerIndexes) { this.customerIndexes = customerIndexes; }
        
        public List<IndexInfo> getDeviceRelationIndexes() { return deviceRelationIndexes; }
        public void setDeviceRelationIndexes(List<IndexInfo> deviceRelationIndexes) { this.deviceRelationIndexes = deviceRelationIndexes; }
        
        public List<IndexInfo> getOrderRelationIndexes() { return orderRelationIndexes; }
        public void setOrderRelationIndexes(List<IndexInfo> orderRelationIndexes) { this.orderRelationIndexes = orderRelationIndexes; }
        
        public List<IndexInfo> getServiceRelationIndexes() { return serviceRelationIndexes; }
        public void setServiceRelationIndexes(List<IndexInfo> serviceRelationIndexes) { this.serviceRelationIndexes = serviceRelationIndexes; }
        
        public List<OptimizationSuggestion> getOptimizationSuggestions() { return optimizationSuggestions; }
        public void setOptimizationSuggestions(List<OptimizationSuggestion> optimizationSuggestions) { this.optimizationSuggestions = optimizationSuggestions; }
        
        public double getHealthScore() { return healthScore; }
        public void setHealthScore(double healthScore) { this.healthScore = healthScore; }
        
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }
    
    /**
     * 索引信息
     */
    public static class IndexInfo {
        private String indexName;
        private String tableName;
        private List<String> columns;
        private String indexType;
        private boolean unique;
        private long cardinality;
        
        // Getters and Setters
        public String getIndexName() { return indexName; }
        public void setIndexName(String indexName) { this.indexName = indexName; }
        
        public String getTableName() { return tableName; }
        public void setTableName(String tableName) { this.tableName = tableName; }
        
        public List<String> getColumns() { return columns; }
        public void setColumns(List<String> columns) { this.columns = columns; }
        
        public String getIndexType() { return indexType; }
        public void setIndexType(String indexType) { this.indexType = indexType; }
        
        public boolean isUnique() { return unique; }
        public void setUnique(boolean unique) { this.unique = unique; }
        
        public long getCardinality() { return cardinality; }
        public void setCardinality(long cardinality) { this.cardinality = cardinality; }
    }
    
    /**
     * 优化建议
     */
    public static class OptimizationSuggestion {
        private String type;
        private String priority;
        private String description;
        private String sqlCommand;
        private String purpose;
        
        public OptimizationSuggestion(String type, String priority, String description, 
                                    String sqlCommand, String purpose) {
            this.type = type;
            this.priority = priority;
            this.description = description;
            this.sqlCommand = sqlCommand;
            this.purpose = purpose;
        }
        
        // Getters and Setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getSqlCommand() { return sqlCommand; }
        public void setSqlCommand(String sqlCommand) { this.sqlCommand = sqlCommand; }
        
        public String getPurpose() { return purpose; }
        public void setPurpose(String purpose) { this.purpose = purpose; }
    }
    
    /**
     * 表统计信息
     */
    public static class TableStats {
        private String tableName;
        private long rowCount;
        private double sizeMB;
        private double dataSizeMB;
        private double indexSizeMB;
        
        // Getters and Setters
        public String getTableName() { return tableName; }
        public void setTableName(String tableName) { this.tableName = tableName; }
        
        public long getRowCount() { return rowCount; }
        public void setRowCount(long rowCount) { this.rowCount = rowCount; }
        
        public double getSizeMB() { return sizeMB; }
        public void setSizeMB(double sizeMB) { this.sizeMB = sizeMB; }
        
        public double getDataSizeMB() { return dataSizeMB; }
        public void setDataSizeMB(double dataSizeMB) { this.dataSizeMB = dataSizeMB; }
        
        public double getIndexSizeMB() { return indexSizeMB; }
        public void setIndexSizeMB(double indexSizeMB) { this.indexSizeMB = indexSizeMB; }
    }
}