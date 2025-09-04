package com.yxrobot.service;

import com.yxrobot.mapper.ManagedDeviceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备搜索索引优化服务
 * 提供数据库索引优化和查询性能提升功能
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-28
 */
@Service
public class ManagedDeviceIndexOptimizationService {
    
    private static final Logger logger = LoggerFactory.getLogger(ManagedDeviceIndexOptimizationService.class);
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private ManagedDeviceMapper managedDeviceMapper;
    
    /**
     * 创建搜索优化索引
     * 为常用的搜索字段创建复合索引，提升查询性能
     */
    public void createSearchOptimizationIndexes() {
        logger.info("开始创建设备搜索优化索引");
        
        List<String> indexSqls = new ArrayList<>();
        
        // 1. 复合索引：状态 + 型号 + 创建时间
        indexSqls.add("CREATE INDEX IF NOT EXISTS idx_managed_devices_status_model_created " +
                     "ON managed_devices (status, model, created_at DESC)");
        
        // 2. 复合索引：客户ID + 状态 + 最后在线时间
        indexSqls.add("CREATE INDEX IF NOT EXISTS idx_managed_devices_customer_status_online " +
                     "ON managed_devices (customer_id, status, last_online_at DESC)");
        
        // 3. 复合索引：序列号 + 删除标记（用于序列号唯一性检查）
        indexSqls.add("CREATE INDEX IF NOT EXISTS idx_managed_devices_serial_deleted " +
                     "ON managed_devices (serial_number, is_deleted)");
        
        // 4. 复合索引：固件版本 + 状态 + 更新时间
        indexSqls.add("CREATE INDEX IF NOT EXISTS idx_managed_devices_firmware_status_updated " +
                     "ON managed_devices (firmware_version, status, updated_at DESC)");
        
        // 5. 复合索引：激活时间 + 状态（用于激活设备查询）
        indexSqls.add("CREATE INDEX IF NOT EXISTS idx_managed_devices_activated_status " +
                     "ON managed_devices (activated_at, status)");
        
        // 6. 全文索引：客户姓名 + 备注（用于文本搜索）
        indexSqls.add("CREATE FULLTEXT INDEX IF NOT EXISTS idx_managed_devices_fulltext " +
                     "ON managed_devices (customer_name, notes)");
        
        // 7. 复合索引：删除标记 + 状态 + 创建时间（用于列表查询）
        indexSqls.add("CREATE INDEX IF NOT EXISTS idx_managed_devices_deleted_status_created " +
                     "ON managed_devices (is_deleted, status, created_at DESC)");
        
        // 8. 复合索引：型号 + 固件版本 + 最后在线时间
        indexSqls.add("CREATE INDEX IF NOT EXISTS idx_managed_devices_model_firmware_online " +
                     "ON managed_devices (model, firmware_version, last_online_at DESC)");
        
        executeIndexSqls(indexSqls);
        
        logger.info("设备搜索优化索引创建完成");
    }
    
    /**
     * 分析查询性能
     * 分析常用查询的执行计划和性能指标
     * 
     * @return 性能分析结果
     */
    public Map<String, Object> analyzeQueryPerformance() {
        logger.info("开始分析设备查询性能");
        
        Map<String, Object> analysisResult = new HashMap<>();
        
        try (Connection conn = dataSource.getConnection()) {
            
            // 1. 分析基础查询性能
            analysisResult.put("basicQuery", analyzeBasicQueryPerformance(conn));
            
            // 2. 分析搜索查询性能
            analysisResult.put("searchQuery", analyzeSearchQueryPerformance(conn));
            
            // 3. 分析筛选查询性能
            analysisResult.put("filterQuery", analyzeFilterQueryPerformance(conn));
            
            // 4. 分析索引使用情况
            analysisResult.put("indexUsage", analyzeIndexUsage(conn));
            
            // 5. 分析表统计信息
            analysisResult.put("tableStats", analyzeTableStatistics(conn));
            
        } catch (SQLException e) {
            logger.error("分析查询性能失败", e);
            analysisResult.put("error", e.getMessage());
        }
        
        logger.info("设备查询性能分析完成");
        return analysisResult;
    }
    
    /**
     * 优化查询性能
     * 根据分析结果优化数据库配置和索引
     */
    public void optimizeQueryPerformance() {
        logger.info("开始优化设备查询性能");
        
        try (Connection conn = dataSource.getConnection()) {
            
            // 1. 更新表统计信息
            updateTableStatistics(conn);
            
            // 2. 优化索引
            optimizeIndexes(conn);
            
            // 3. 分析表碎片
            analyzeTableFragmentation(conn);
            
            logger.info("设备查询性能优化完成");
            
        } catch (SQLException e) {
            logger.error("优化查询性能失败", e);
        }
    }
    
    /**
     * 获取搜索性能统计
     * 
     * @return 性能统计信息
     */
    public Map<String, Object> getSearchPerformanceStats() {
        logger.info("获取设备搜索性能统计");
        
        Map<String, Object> stats = new HashMap<>();
        
        try (Connection conn = dataSource.getConnection()) {
            
            // 1. 查询执行时间统计
            stats.put("queryExecutionTime", getQueryExecutionTimeStats(conn));
            
            // 2. 索引命中率统计
            stats.put("indexHitRate", getIndexHitRateStats(conn));
            
            // 3. 慢查询统计
            stats.put("slowQueries", getSlowQueryStats(conn));
            
            // 4. 表扫描统计
            stats.put("tableScans", getTableScanStats(conn));
            
        } catch (SQLException e) {
            logger.error("获取搜索性能统计失败", e);
            stats.put("error", e.getMessage());
        }
        
        return stats;
    }
    
    /**
     * 执行索引创建SQL
     * 
     * @param indexSqls 索引SQL列表
     */
    private void executeIndexSqls(List<String> indexSqls) {
        try (Connection conn = dataSource.getConnection()) {
            
            for (String sql : indexSqls) {
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.execute();
                    logger.info("索引创建成功: {}", sql);
                } catch (SQLException e) {
                    logger.warn("索引创建失败: {}, 错误: {}", sql, e.getMessage());
                }
            }
            
        } catch (SQLException e) {
            logger.error("执行索引创建SQL失败", e);
        }
    }
    
    /**
     * 分析基础查询性能
     */
    private Map<String, Object> analyzeBasicQueryPerformance(Connection conn) throws SQLException {
        Map<String, Object> result = new HashMap<>();
        
        // 分析基础查询的执行计划
        String sql = "EXPLAIN SELECT * FROM managed_devices WHERE is_deleted = 0 ORDER BY created_at DESC LIMIT 10";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            List<Map<String, Object>> explainResult = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("id", rs.getInt("id"));
                row.put("select_type", rs.getString("select_type"));
                row.put("table", rs.getString("table"));
                row.put("type", rs.getString("type"));
                row.put("possible_keys", rs.getString("possible_keys"));
                row.put("key", rs.getString("key"));
                row.put("key_len", rs.getString("key_len"));
                row.put("ref", rs.getString("ref"));
                row.put("rows", rs.getLong("rows"));
                row.put("Extra", rs.getString("Extra"));
                explainResult.add(row);
            }
            
            result.put("explainResult", explainResult);
        }
        
        return result;
    }
    
    /**
     * 分析搜索查询性能
     */
    private Map<String, Object> analyzeSearchQueryPerformance(Connection conn) throws SQLException {
        Map<String, Object> result = new HashMap<>();
        
        // 分析搜索查询的执行计划
        String sql = "EXPLAIN SELECT * FROM managed_devices WHERE is_deleted = 0 " +
                    "AND (serial_number LIKE '%test%' OR customer_name LIKE '%test%') " +
                    "ORDER BY created_at DESC LIMIT 10";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            List<Map<String, Object>> explainResult = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("type", rs.getString("type"));
                row.put("key", rs.getString("key"));
                row.put("rows", rs.getLong("rows"));
                row.put("Extra", rs.getString("Extra"));
                explainResult.add(row);
            }
            
            result.put("explainResult", explainResult);
        }
        
        return result;
    }
    
    /**
     * 分析筛选查询性能
     */
    private Map<String, Object> analyzeFilterQueryPerformance(Connection conn) throws SQLException {
        Map<String, Object> result = new HashMap<>();
        
        // 分析筛选查询的执行计划
        String sql = "EXPLAIN SELECT * FROM managed_devices WHERE is_deleted = 0 " +
                    "AND status = 'online' AND model = 'education' " +
                    "ORDER BY created_at DESC LIMIT 10";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            List<Map<String, Object>> explainResult = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("type", rs.getString("type"));
                row.put("key", rs.getString("key"));
                row.put("rows", rs.getLong("rows"));
                explainResult.add(row);
            }
            
            result.put("explainResult", explainResult);
        }
        
        return result;
    }
    
    /**
     * 分析索引使用情况
     */
    private Map<String, Object> analyzeIndexUsage(Connection conn) throws SQLException {
        Map<String, Object> result = new HashMap<>();
        
        String sql = "SHOW INDEX FROM managed_devices";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            List<Map<String, Object>> indexes = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> index = new HashMap<>();
                index.put("keyName", rs.getString("Key_name"));
                index.put("columnName", rs.getString("Column_name"));
                index.put("cardinality", rs.getLong("Cardinality"));
                index.put("indexType", rs.getString("Index_type"));
                indexes.add(index);
            }
            
            result.put("indexes", indexes);
        }
        
        return result;
    }
    
    /**
     * 分析表统计信息
     */
    private Map<String, Object> analyzeTableStatistics(Connection conn) throws SQLException {
        Map<String, Object> result = new HashMap<>();
        
        String sql = "SELECT COUNT(*) as total_rows, " +
                    "COUNT(CASE WHEN is_deleted = 0 THEN 1 END) as active_rows, " +
                    "COUNT(CASE WHEN is_deleted = 1 THEN 1 END) as deleted_rows, " +
                    "COUNT(DISTINCT status) as status_count, " +
                    "COUNT(DISTINCT model) as model_count, " +
                    "COUNT(DISTINCT customer_id) as customer_count " +
                    "FROM managed_devices";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                result.put("totalRows", rs.getLong("total_rows"));
                result.put("activeRows", rs.getLong("active_rows"));
                result.put("deletedRows", rs.getLong("deleted_rows"));
                result.put("statusCount", rs.getInt("status_count"));
                result.put("modelCount", rs.getInt("model_count"));
                result.put("customerCount", rs.getInt("customer_count"));
            }
        }
        
        return result;
    }
    
    /**
     * 更新表统计信息
     */
    private void updateTableStatistics(Connection conn) throws SQLException {
        String sql = "ANALYZE TABLE managed_devices";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
            logger.info("表统计信息更新完成");
        }
    }
    
    /**
     * 优化索引
     */
    private void optimizeIndexes(Connection conn) throws SQLException {
        String sql = "OPTIMIZE TABLE managed_devices";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
            logger.info("表索引优化完成");
        }
    }
    
    /**
     * 分析表碎片
     */
    private void analyzeTableFragmentation(Connection conn) throws SQLException {
        String sql = "SELECT table_name, data_length, index_length, data_free " +
                    "FROM information_schema.tables " +
                    "WHERE table_schema = DATABASE() AND table_name = 'managed_devices'";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                long dataLength = rs.getLong("data_length");
                long indexLength = rs.getLong("index_length");
                long dataFree = rs.getLong("data_free");
                
                logger.info("表碎片分析 - 数据大小: {} bytes, 索引大小: {} bytes, 碎片大小: {} bytes", 
                           dataLength, indexLength, dataFree);
                
                // 如果碎片超过10%，建议重建表
                if (dataFree > (dataLength + indexLength) * 0.1) {
                    logger.warn("表碎片较多，建议执行 ALTER TABLE managed_devices ENGINE=InnoDB 重建表");
                }
            }
        }
    }
    
    /**
     * 获取查询执行时间统计
     */
    private Map<String, Object> getQueryExecutionTimeStats(Connection conn) throws SQLException {
        Map<String, Object> stats = new HashMap<>();
        
        // 这里可以根据实际需求实现查询执行时间统计
        // 暂时返回模拟数据
        stats.put("averageQueryTime", "50ms");
        stats.put("maxQueryTime", "200ms");
        stats.put("minQueryTime", "10ms");
        
        return stats;
    }
    
    /**
     * 获取索引命中率统计
     */
    private Map<String, Object> getIndexHitRateStats(Connection conn) throws SQLException {
        Map<String, Object> stats = new HashMap<>();
        
        // 这里可以根据实际需求实现索引命中率统计
        // 暂时返回模拟数据
        stats.put("indexHitRate", "95%");
        stats.put("tableScans", 5);
        stats.put("indexScans", 95);
        
        return stats;
    }
    
    /**
     * 获取慢查询统计
     */
    private Map<String, Object> getSlowQueryStats(Connection conn) throws SQLException {
        Map<String, Object> stats = new HashMap<>();
        
        // 这里可以根据实际需求实现慢查询统计
        // 暂时返回模拟数据
        stats.put("slowQueryCount", 2);
        stats.put("slowQueryThreshold", "1s");
        
        return stats;
    }
    
    /**
     * 获取表扫描统计
     */
    private Map<String, Object> getTableScanStats(Connection conn) throws SQLException {
        Map<String, Object> stats = new HashMap<>();
        
        // 这里可以根据实际需求实现表扫描统计
        // 暂时返回模拟数据
        stats.put("fullTableScans", 3);
        stats.put("indexScans", 97);
        stats.put("scanEfficiency", "97%");
        
        return stats;
    }
}