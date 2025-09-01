-- 租赁数据分析模块 - 数据库索引优化脚本
-- 用于提升查询性能，确保API响应时间满足前端要求
-- 执行日期: 2025-01-28

USE YXRobot;

-- =====================================================
-- 租赁记录表 (rental_records) 索引优化
-- =====================================================

-- 1. 复合索引：租赁状态 + 创建时间 (用于统计查询)
CREATE INDEX idx_rental_status_created 
ON rental_records (rental_status, created_at) 
WHERE is_deleted = 0;

-- 2. 复合索引：租赁开始日期 + 状态 (用于趋势分析)
CREATE INDEX idx_rental_start_status 
ON rental_records (rental_start_date, rental_status, is_deleted);

-- 3. 复合索引：设备ID + 租赁状态 (用于设备利用率计算)
CREATE INDEX idx_device_rental_status 
ON rental_records (device_id, rental_status, rental_start_date) 
WHERE is_deleted = 0;

-- 4. 复合索引：客户ID + 创建时间 (用于客户分析)
CREATE INDEX idx_customer_created 
ON rental_records (customer_id, created_at) 
WHERE is_deleted = 0;

-- 5. 覆盖索引：统计查询优化
CREATE INDEX idx_rental_stats_coverage 
ON rental_records (rental_start_date, rental_status, total_rental_fee, rental_period, device_id) 
WHERE is_deleted = 0;

-- =====================================================
-- 租赁设备表 (rental_devices) 索引优化
-- =====================================================

-- 1. 复合索引：设备状态 + 利用率 (用于状态统计和排行)
CREATE INDEX idx_device_status_utilization 
ON rental_devices (current_status, utilization_rate DESC, is_active) 
WHERE is_deleted = 0;

-- 2. 复合索引：设备型号 + 地区 (用于分布分析)
CREATE INDEX idx_device_model_region 
ON rental_devices (device_model, region, current_status) 
WHERE is_deleted = 0 AND is_active = 1;

-- 3. 复合索引：利用率排序 (用于TOP设备查询)
CREATE INDEX idx_utilization_ranking 
ON rental_devices (utilization_rate DESC, device_id, device_model) 
WHERE is_deleted = 0 AND is_active = 1;

-- 4. 复合索引：最后租赁日期 + 状态 (用于设备活跃度分析)
CREATE INDEX idx_last_rental_status 
ON rental_devices (last_rental_date, current_status, is_active) 
WHERE is_deleted = 0;

-- 5. 覆盖索引：设备利用率查询优化
CREATE INDEX idx_device_utilization_coverage 
ON rental_devices (device_id, device_model, utilization_rate, total_rental_days, 
                   total_available_days, current_status, last_rental_date, region,
                   performance_score, signal_strength, maintenance_status) 
WHERE is_deleted = 0 AND is_active = 1;

-- =====================================================
-- 租赁客户表 (rental_customers) 索引优化
-- =====================================================

-- 1. 复合索引：客户类型 + 地区 (用于客户分析)
CREATE INDEX idx_customer_type_region 
ON rental_customers (customer_type, region, is_active) 
WHERE is_deleted = 0;

-- 2. 复合索引：最后租赁日期 + 活跃状态 (用于客户活跃度分析)
CREATE INDEX idx_customer_last_rental 
ON rental_customers (last_rental_date, is_active, total_rental_amount) 
WHERE is_deleted = 0;

-- 3. 文本索引：客户名称搜索优化
CREATE INDEX idx_customer_name_search 
ON rental_customers (customer_name, customer_type) 
WHERE is_deleted = 0;

-- =====================================================
-- 设备利用率表 (device_utilization) 索引优化
-- =====================================================

-- 1. 复合索引：设备ID + 统计日期 (用于趋势查询)
CREATE INDEX idx_device_utilization_trend 
ON device_utilization (device_id, stat_date, stat_period);

-- 2. 复合索引：统计日期 + 周期 (用于聚合查询)
CREATE INDEX idx_utilization_date_period 
ON device_utilization (stat_date, stat_period, utilization_rate);

-- =====================================================
-- 租赁统计表 (rental_stats) 索引优化
-- =====================================================

-- 1. 复合索引：统计日期 + 类型 (用于统计查询)
CREATE INDEX idx_stats_date_type 
ON rental_stats (stat_date, stat_type);

-- 2. 复合索引：统计类型 + 日期范围 (用于趋势分析)
CREATE INDEX idx_stats_type_date_range 
ON rental_stats (stat_type, stat_date, total_rental_revenue);

-- =====================================================
-- 查询性能验证
-- =====================================================

-- 验证索引创建情况
SELECT 
    TABLE_NAME,
    INDEX_NAME,
    COLUMN_NAME,
    SEQ_IN_INDEX,
    INDEX_TYPE
FROM INFORMATION_SCHEMA.STATISTICS 
WHERE TABLE_SCHEMA = 'YXRobot' 
  AND TABLE_NAME IN ('rental_records', 'rental_devices', 'rental_customers', 'device_utilization', 'rental_stats')
  AND INDEX_NAME LIKE 'idx_%'
ORDER BY TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX;

-- 分析表统计信息
ANALYZE TABLE rental_records, rental_devices, rental_customers, device_utilization, rental_stats;

-- =====================================================
-- 性能监控查询示例
-- =====================================================

-- 1. 检查慢查询
SELECT 
    query_time,
    lock_time,
    rows_sent,
    rows_examined,
    sql_text
FROM mysql.slow_log 
WHERE start_time >= DATE_SUB(NOW(), INTERVAL 1 HOUR)
  AND sql_text LIKE '%rental_%'
ORDER BY query_time DESC
LIMIT 10;

-- 2. 检查索引使用情况
SELECT 
    TABLE_SCHEMA,
    TABLE_NAME,
    INDEX_NAME,
    CARDINALITY,
    SUB_PART,
    PACKED,
    NULLABLE,
    INDEX_TYPE
FROM INFORMATION_SCHEMA.STATISTICS 
WHERE TABLE_SCHEMA = 'YXRobot' 
  AND TABLE_NAME LIKE 'rental_%'
ORDER BY TABLE_NAME, CARDINALITY DESC;

-- =====================================================
-- 索引维护建议
-- =====================================================

/*
索引维护建议：

1. 定期监控索引使用情况：
   - 使用 SHOW INDEX FROM table_name 检查索引状态
   - 监控 INFORMATION_SCHEMA.INDEX_STATISTICS 表

2. 定期更新表统计信息：
   - 每周执行 ANALYZE TABLE 更新统计信息
   - 在大量数据变更后重新分析表

3. 监控查询性能：
   - 启用慢查询日志监控性能问题
   - 使用 EXPLAIN 分析查询执行计划

4. 索引优化策略：
   - 优先创建高选择性的索引
   - 避免创建过多的索引影响写入性能
   - 定期清理未使用的索引

5. 分区策略（可选）：
   - 对于大表可考虑按日期分区
   - 提升历史数据查询性能
*/

COMMIT;