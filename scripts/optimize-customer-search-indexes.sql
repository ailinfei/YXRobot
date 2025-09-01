-- 客户搜索和筛选功能索引优化脚本
-- 用于提升客户管理模块的搜索和筛选性能

-- ==================== 基础索引优化 ====================

-- 1. 客户基本信息搜索索引
-- 支持姓名、电话、邮箱的快速搜索
CREATE INDEX IF NOT EXISTS idx_customer_search_basic 
ON customers (customer_name, phone, email, is_deleted);

-- 2. 客户等级和状态组合索引
-- 支持按等级和状态筛选
CREATE INDEX IF NOT EXISTS idx_customer_level_status 
ON customers (customer_level, customer_status, is_deleted);

-- 3. 地区筛选索引
-- 支持按地区快速筛选
CREATE INDEX IF NOT EXISTS idx_customer_region 
ON customers (region, is_deleted);

-- 4. 行业筛选索引
-- 支持按行业快速筛选
CREATE INDEX IF NOT EXISTS idx_customer_industry 
ON customers (industry, is_deleted);

-- 5. 信用等级索引
-- 支持按信用等级筛选
CREATE INDEX IF NOT EXISTS idx_customer_credit_level 
ON customers (credit_level, is_deleted);

-- ==================== 时间范围查询索引 ====================

-- 6. 注册时间索引
-- 支持按注册时间范围筛选
CREATE INDEX IF NOT EXISTS idx_customer_registered_at 
ON customers (registered_at, is_deleted);

-- 7. 最后活跃时间索引
-- 支持按最后活跃时间筛选
CREATE INDEX IF NOT EXISTS idx_customer_last_active 
ON customers (last_active_at, is_deleted);

-- 8. 创建时间索引
-- 支持按创建时间排序和筛选
CREATE INDEX IF NOT EXISTS idx_customer_created_at 
ON customers (created_at, is_deleted);

-- 9. 更新时间索引
-- 支持按更新时间排序
CREATE INDEX IF NOT EXISTS idx_customer_updated_at 
ON customers (updated_at, is_deleted);

-- ==================== 金额范围查询索引 ====================

-- 10. 消费金额索引
-- 支持按消费金额范围筛选和排序
CREATE INDEX IF NOT EXISTS idx_customer_total_spent 
ON customers (total_spent, is_deleted);

-- 11. 客户价值索引
-- 支持按客户价值范围筛选和排序
CREATE INDEX IF NOT EXISTS idx_customer_value 
ON customers (customer_value, is_deleted);

-- ==================== 复合索引优化 ====================

-- 12. 等级+消费金额复合索引
-- 支持VIP客户按消费金额排序
CREATE INDEX IF NOT EXISTS idx_customer_level_spent 
ON customers (customer_level, total_spent DESC, is_deleted);

-- 13. 地区+等级复合索引
-- 支持地区内按等级筛选
CREATE INDEX IF NOT EXISTS idx_customer_region_level 
ON customers (region, customer_level, is_deleted);

-- 14. 状态+活跃时间复合索引
-- 支持活跃客户查询
CREATE INDEX IF NOT EXISTS idx_customer_status_active 
ON customers (customer_status, last_active_at DESC, is_deleted);

-- 15. 等级+价值+消费复合索引
-- 支持高价值客户查询
CREATE INDEX IF NOT EXISTS idx_customer_level_value_spent 
ON customers (customer_level, customer_value DESC, total_spent DESC, is_deleted);

-- ==================== 全文搜索索引 ====================

-- 16. 客户姓名全文搜索索引
-- 支持模糊搜索客户姓名
CREATE FULLTEXT INDEX IF NOT EXISTS ft_idx_customer_name 
ON customers (customer_name);

-- 17. 联系人全文搜索索引
-- 支持模糊搜索联系人
CREATE FULLTEXT INDEX IF NOT EXISTS ft_idx_contact_person 
ON customers (contact_person);

-- 18. 备注全文搜索索引
-- 支持搜索客户备注信息
CREATE FULLTEXT INDEX IF NOT EXISTS ft_idx_customer_notes 
ON customers (notes);

-- ==================== 关联表索引优化 ====================

-- 19. 客户设备关联表索引
-- 支持按设备类型筛选客户
CREATE INDEX IF NOT EXISTS idx_customer_device_relation_customer 
ON customer_device_relation (customer_id, status);

CREATE INDEX IF NOT EXISTS idx_customer_device_relation_device 
ON customer_device_relation (device_id, status);

CREATE INDEX IF NOT EXISTS idx_customer_device_relation_type 
ON customer_device_relation (customer_id, relation_type, status);

-- 20. 设备表索引优化
-- 支持设备类型和状态查询
CREATE INDEX IF NOT EXISTS idx_device_type_status 
ON devices (device_type, device_status, is_deleted);

-- 21. 客户订单关联表索引
-- 支持按订单筛选客户
CREATE INDEX IF NOT EXISTS idx_customer_order_relation_customer 
ON customer_order_relation (customer_id);

CREATE INDEX IF NOT EXISTS idx_customer_order_relation_order 
ON customer_order_relation (order_id);

-- 22. 订单表索引优化
-- 支持订单状态和金额查询
CREATE INDEX IF NOT EXISTS idx_order_status_amount 
ON orders (order_status, total_amount, is_deleted);

CREATE INDEX IF NOT EXISTS idx_order_created_at 
ON orders (created_at, is_deleted);

-- 23. 客户服务关联表索引
-- 支持按服务记录筛选客户
CREATE INDEX IF NOT EXISTS idx_customer_service_relation_customer 
ON customer_service_relation (customer_id);

CREATE INDEX IF NOT EXISTS idx_customer_service_relation_service 
ON customer_service_relation (service_record_id);

-- 24. 服务记录表索引优化
-- 支持服务状态查询
CREATE INDEX IF NOT EXISTS idx_service_record_status 
ON service_records (status, is_deleted);

CREATE INDEX IF NOT EXISTS idx_service_record_created_at 
ON service_records (created_at, is_deleted);

-- ==================== 统计查询索引优化 ====================

-- 25. 客户统计专用索引
-- 支持快速统计客户数量和分布
CREATE INDEX IF NOT EXISTS idx_customer_stats_level 
ON customers (customer_level, is_deleted, registered_at);

CREATE INDEX IF NOT EXISTS idx_customer_stats_status 
ON customers (customer_status, is_active, is_deleted);

CREATE INDEX IF NOT EXISTS idx_customer_stats_region 
ON customers (region, is_deleted, registered_at);

-- ==================== 性能监控和分析 ====================

-- 查看索引使用情况
SELECT 
    TABLE_NAME,
    INDEX_NAME,
    COLUMN_NAME,
    SEQ_IN_INDEX,
    CARDINALITY,
    INDEX_TYPE
FROM information_schema.STATISTICS 
WHERE TABLE_SCHEMA = 'YXRobot' 
AND TABLE_NAME = 'customers'
ORDER BY TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX;

-- 查看表大小和索引大小
SELECT 
    TABLE_NAME,
    ROUND(((DATA_LENGTH + INDEX_LENGTH) / 1024 / 1024), 2) AS 'Total Size (MB)',
    ROUND((DATA_LENGTH / 1024 / 1024), 2) AS 'Data Size (MB)',
    ROUND((INDEX_LENGTH / 1024 / 1024), 2) AS 'Index Size (MB)',
    TABLE_ROWS
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = 'YXRobot' 
AND TABLE_NAME IN ('customers', 'customer_device_relation', 'customer_order_relation', 'customer_service_relation')
ORDER BY (DATA_LENGTH + INDEX_LENGTH) DESC;

-- ==================== 索引维护建议 ====================

-- 定期分析表统计信息
ANALYZE TABLE customers;
ANALYZE TABLE customer_device_relation;
ANALYZE TABLE customer_order_relation;
ANALYZE TABLE customer_service_relation;
ANALYZE TABLE devices;
ANALYZE TABLE orders;
ANALYZE TABLE service_records;

-- 优化表结构
OPTIMIZE TABLE customers;

-- ==================== 查询性能测试 ====================

-- 测试基本搜索性能
EXPLAIN SELECT * FROM customers 
WHERE customer_name LIKE '%张%' 
AND is_deleted = 0 
ORDER BY created_at DESC 
LIMIT 20;

-- 测试复合条件查询性能
EXPLAIN SELECT * FROM customers 
WHERE customer_level = 'vip' 
AND region LIKE '%北京%' 
AND total_spent >= 10000 
AND is_deleted = 0 
ORDER BY customer_value DESC 
LIMIT 20;

-- 测试关联查询性能
EXPLAIN SELECT c.*, COUNT(cdr.device_id) as device_count
FROM customers c
LEFT JOIN customer_device_relation cdr ON c.id = cdr.customer_id AND cdr.status = 1
LEFT JOIN devices d ON cdr.device_id = d.id AND d.is_deleted = 0
WHERE c.is_deleted = 0
GROUP BY c.id
ORDER BY c.customer_value DESC
LIMIT 20;

-- 测试统计查询性能
EXPLAIN SELECT 
    customer_level,
    COUNT(*) as count,
    AVG(total_spent) as avg_spent,
    SUM(customer_value) as total_value
FROM customers 
WHERE is_deleted = 0 
GROUP BY customer_level;

-- ==================== 索引监控查询 ====================

-- 监控慢查询
SELECT 
    query_time,
    lock_time,
    rows_sent,
    rows_examined,
    sql_text
FROM mysql.slow_log 
WHERE sql_text LIKE '%customers%'
ORDER BY query_time DESC
LIMIT 10;

-- 监控索引使用统计
SELECT 
    OBJECT_SCHEMA,
    OBJECT_NAME,
    INDEX_NAME,
    COUNT_FETCH,
    COUNT_INSERT,
    COUNT_UPDATE,
    COUNT_DELETE
FROM performance_schema.table_io_waits_summary_by_index_usage
WHERE OBJECT_SCHEMA = 'YXRobot'
AND OBJECT_NAME = 'customers'
ORDER BY COUNT_FETCH DESC;

-- ==================== 清理无用索引 ====================

-- 查找未使用的索引
SELECT 
    OBJECT_SCHEMA,
    OBJECT_NAME,
    INDEX_NAME
FROM performance_schema.table_io_waits_summary_by_index_usage
WHERE OBJECT_SCHEMA = 'YXRobot'
AND OBJECT_NAME = 'customers'
AND INDEX_NAME IS NOT NULL
AND INDEX_NAME != 'PRIMARY'
AND COUNT_FETCH = 0
AND COUNT_INSERT = 0
AND COUNT_UPDATE = 0
AND COUNT_DELETE = 0;

-- ==================== 索引优化完成提示 ====================

SELECT 'Customer search and filtering indexes optimization completed!' as message;
SELECT CONCAT('Total indexes created: ', COUNT(*)) as index_count
FROM information_schema.STATISTICS 
WHERE TABLE_SCHEMA = 'YXRobot' 
AND TABLE_NAME = 'customers'
AND INDEX_NAME != 'PRIMARY';

-- 性能优化建议
SELECT '
索引优化完成！建议：
1. 定期监控索引使用情况
2. 根据实际查询模式调整索引
3. 定期执行ANALYZE TABLE更新统计信息
4. 监控慢查询日志
5. 考虑使用查询缓存提升性能
' as optimization_tips;