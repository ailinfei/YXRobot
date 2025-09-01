-- 客户管理模块数据库索引优化脚本
-- 用于提高查询性能，确保API响应时间满足前端要求

-- ==================== 客户表索引优化 ====================

-- 客户姓名索引（支持模糊搜索）
CREATE INDEX IF NOT EXISTS idx_customers_name ON customers(customer_name);

-- 客户等级索引（支持筛选）
CREATE INDEX IF NOT EXISTS idx_customers_level ON customers(customer_level);

-- 客户状态索引（支持筛选）
CREATE INDEX IF NOT EXISTS idx_customers_status ON customers(customer_status);

-- 客户电话索引（支持搜索和唯一性检查）
CREATE INDEX IF NOT EXISTS idx_customers_phone ON customers(phone);

-- 客户邮箱索引（支持搜索和唯一性检查）
CREATE INDEX IF NOT EXISTS idx_customers_email ON customers(email);

-- 客户地区索引（支持地区筛选）
CREATE INDEX IF NOT EXISTS idx_customers_region ON customers(region);

-- 客户行业索引（支持行业筛选）
CREATE INDEX IF NOT EXISTS idx_customers_industry ON customers(industry);

-- 客户注册时间索引（支持时间范围查询）
CREATE INDEX IF NOT EXISTS idx_customers_registered_at ON customers(registered_at);

-- 客户最后活跃时间索引（支持活跃度查询）
CREATE INDEX IF NOT EXISTS idx_customers_last_active_at ON customers(last_active_at);

-- 客户删除状态索引（过滤已删除记录）
CREATE INDEX IF NOT EXISTS idx_customers_is_deleted ON customers(is_deleted);

-- 复合索引：状态+等级（支持常用组合查询）
CREATE INDEX IF NOT EXISTS idx_customers_status_level ON customers(customer_status, customer_level);

-- 复合索引：删除状态+创建时间（支持分页查询）
CREATE INDEX IF NOT EXISTS idx_customers_deleted_created ON customers(is_deleted, created_at);

-- ==================== 客户设备关联表索引优化 ====================

-- 客户ID索引（支持根据客户查询设备）
CREATE INDEX IF NOT EXISTS idx_customer_device_customer_id ON customer_device_relation(customer_id);

-- 设备ID索引（支持根据设备查询客户）
CREATE INDEX IF NOT EXISTS idx_customer_device_device_id ON customer_device_relation(device_id);

-- 关联类型索引（支持按设备类型筛选）
CREATE INDEX IF NOT EXISTS idx_customer_device_relation_type ON customer_device_relation(relation_type);

-- 复合索引：客户ID+关联类型（支持客户设备类型查询）
CREATE INDEX IF NOT EXISTS idx_customer_device_customer_type ON customer_device_relation(customer_id, relation_type);

-- ==================== 客户订单关联表索引优化 ====================

-- 客户ID索引（支持根据客户查询订单）
CREATE INDEX IF NOT EXISTS idx_customer_order_customer_id ON customer_order_relation(customer_id);

-- 订单ID索引（支持根据订单查询客户）
CREATE INDEX IF NOT EXISTS idx_customer_order_order_id ON customer_order_relation(order_id);

-- 客户角色索引（支持按角色筛选）
CREATE INDEX IF NOT EXISTS idx_customer_order_role ON customer_order_relation(customer_role);

-- 复合索引：客户ID+角色（支持客户订单角色查询）
CREATE INDEX IF NOT EXISTS idx_customer_order_customer_role ON customer_order_relation(customer_id, customer_role);

-- ==================== 客户服务记录关联表索引优化 ====================

-- 客户ID索引（支持根据客户查询服务记录）
CREATE INDEX IF NOT EXISTS idx_customer_service_customer_id ON customer_service_relation(customer_id);

-- 服务记录ID索引（支持根据服务记录查询客户）
CREATE INDEX IF NOT EXISTS idx_customer_service_service_id ON customer_service_relation(service_record_id);

-- 客户角色索引（支持按角色筛选）
CREATE INDEX IF NOT EXISTS idx_customer_service_role ON customer_service_relation(customer_role);

-- 复合索引：客户ID+角色（支持客户服务角色查询）
CREATE INDEX IF NOT EXISTS idx_customer_service_customer_role ON customer_service_relation(customer_id, customer_role);

-- ==================== 设备表索引优化 ====================

-- 设备序列号索引（支持设备查找）
CREATE INDEX IF NOT EXISTS idx_devices_serial_number ON devices(serial_number);

-- 设备型号索引（支持型号筛选）
CREATE INDEX IF NOT EXISTS idx_devices_model ON devices(device_model);

-- 设备状态索引（支持状态筛选）
CREATE INDEX IF NOT EXISTS idx_devices_status ON devices(device_status);

-- 设备类型索引（支持类型筛选）
CREATE INDEX IF NOT EXISTS idx_devices_type ON devices(device_type);

-- 复合索引：状态+类型（支持常用组合查询）
CREATE INDEX IF NOT EXISTS idx_devices_status_type ON devices(device_status, device_type);

-- ==================== 订单表索引优化 ====================

-- 订单号索引（支持订单查找）
CREATE INDEX IF NOT EXISTS idx_orders_order_number ON orders(order_number);

-- 订单类型索引（支持类型筛选）
CREATE INDEX IF NOT EXISTS idx_orders_type ON orders(order_type);

-- 订单状态索引（支持状态筛选）
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(order_status);

-- 订单日期索引（支持时间范围查询）
CREATE INDEX IF NOT EXISTS idx_orders_order_date ON orders(order_date);

-- 复合索引：状态+类型（支持常用组合查询）
CREATE INDEX IF NOT EXISTS idx_orders_status_type ON orders(order_status, order_type);

-- 复合索引：订单日期+状态（支持时间状态查询）
CREATE INDEX IF NOT EXISTS idx_orders_date_status ON orders(order_date, order_status);

-- ==================== 服务记录表索引优化 ====================

-- 服务编号索引（支持服务记录查找）
CREATE INDEX IF NOT EXISTS idx_service_records_number ON service_records(service_number);

-- 服务类型索引（支持类型筛选）
CREATE INDEX IF NOT EXISTS idx_service_records_type ON service_records(service_type);

-- 服务状态索引（支持状态筛选）
CREATE INDEX IF NOT EXISTS idx_service_records_status ON service_records(service_status);

-- 服务日期索引（支持时间范围查询）
CREATE INDEX IF NOT EXISTS idx_service_records_date ON service_records(service_date);

-- 服务人员索引（支持按人员筛选）
CREATE INDEX IF NOT EXISTS idx_service_records_staff ON service_records(service_staff);

-- 复合索引：状态+类型（支持常用组合查询）
CREATE INDEX IF NOT EXISTS idx_service_records_status_type ON service_records(service_status, service_type);

-- ==================== 客户统计表索引优化 ====================

-- 统计日期索引（支持按日期查询统计）
CREATE INDEX IF NOT EXISTS idx_customer_stats_date ON customer_stats(stat_date);

-- 复合索引：统计日期（降序，获取最新统计）
CREATE INDEX IF NOT EXISTS idx_customer_stats_date_desc ON customer_stats(stat_date DESC);

-- ==================== 性能优化建议 ====================

-- 1. 定期更新表统计信息
-- ANALYZE TABLE customers;
-- ANALYZE TABLE customer_device_relation;
-- ANALYZE TABLE customer_order_relation;
-- ANALYZE TABLE customer_service_relation;

-- 2. 定期清理碎片
-- OPTIMIZE TABLE customers;
-- OPTIMIZE TABLE customer_device_relation;
-- OPTIMIZE TABLE customer_order_relation;
-- OPTIMIZE TABLE customer_service_relation;

-- 3. 监控慢查询
-- SET GLOBAL slow_query_log = 'ON';
-- SET GLOBAL long_query_time = 2;

-- ==================== 索引使用情况监控 ====================

-- 查看索引使用情况的SQL语句
/*
-- 检查索引使用情况
SELECT 
    TABLE_NAME,
    INDEX_NAME,
    CARDINALITY,
    SUB_PART,
    PACKED,
    NULLABLE,
    INDEX_TYPE
FROM information_schema.STATISTICS 
WHERE TABLE_SCHEMA = 'YXRobot' 
    AND TABLE_NAME IN ('customers', 'customer_device_relation', 'customer_order_relation', 'customer_service_relation')
ORDER BY TABLE_NAME, INDEX_NAME;

-- 检查未使用的索引
SELECT 
    s.TABLE_SCHEMA,
    s.TABLE_NAME,
    s.INDEX_NAME,
    s.CARDINALITY
FROM information_schema.STATISTICS s
LEFT JOIN performance_schema.table_io_waits_summary_by_index_usage i
    ON s.TABLE_SCHEMA = i.OBJECT_SCHEMA
    AND s.TABLE_NAME = i.OBJECT_NAME
    AND s.INDEX_NAME = i.INDEX_NAME
WHERE s.TABLE_SCHEMA = 'YXRobot'
    AND i.INDEX_NAME IS NULL
    AND s.INDEX_NAME != 'PRIMARY'
ORDER BY s.TABLE_NAME, s.INDEX_NAME;
*/

-- ==================== 性能监控索引 ====================

-- 创建性能监控相关的索引
CREATE INDEX IF NOT EXISTS idx_customers_performance_monitor ON customers(customer_status, customer_level, created_at DESC);

-- 创建查询优化索引
CREATE INDEX IF NOT EXISTS idx_customers_search_optimization ON customers(customer_name, phone, email);

-- 创建统计查询优化索引
CREATE INDEX IF NOT EXISTS idx_customers_stats_optimization ON customers(customer_level, customer_status, is_deleted, created_at);

-- ==================== 索引使用情况分析 ====================

-- 创建索引使用情况分析视图
CREATE OR REPLACE VIEW v_customer_index_usage AS
SELECT 
    s.TABLE_NAME,
    s.INDEX_NAME,
    s.COLUMN_NAME,
    s.CARDINALITY,
    s.SUB_PART,
    s.PACKED,
    s.NULLABLE,
    s.INDEX_TYPE,
    CASE 
        WHEN s.NON_UNIQUE = 0 THEN 'UNIQUE'
        ELSE 'NON_UNIQUE'
    END as INDEX_UNIQUENESS
FROM information_schema.STATISTICS s
WHERE s.TABLE_SCHEMA = DATABASE() 
    AND s.TABLE_NAME IN ('customers', 'customer_device_relation', 'customer_order_relation', 'customer_service_relation')
ORDER BY s.TABLE_NAME, s.INDEX_NAME, s.SEQ_IN_INDEX;

-- 创建表大小统计视图
CREATE OR REPLACE VIEW v_customer_table_stats AS
SELECT 
    TABLE_NAME,
    TABLE_ROWS,
    ROUND(((DATA_LENGTH + INDEX_LENGTH) / 1024 / 1024), 2) AS total_size_mb,
    ROUND((DATA_LENGTH / 1024 / 1024), 2) AS data_size_mb,
    ROUND((INDEX_LENGTH / 1024 / 1024), 2) AS index_size_mb,
    ROUND((INDEX_LENGTH / DATA_LENGTH * 100), 2) AS index_ratio_percent,
    ENGINE,
    TABLE_COLLATION,
    CREATE_TIME,
    UPDATE_TIME
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME IN ('customers', 'customer_device_relation', 'customer_order_relation', 'customer_service_relation')
ORDER BY total_size_mb DESC;

-- ==================== 索引健康检查 ====================

-- 检查重复索引
SELECT 
    'DUPLICATE_INDEX_CHECK' as check_type,
    GROUP_CONCAT(INDEX_NAME) as duplicate_indexes,
    TABLE_NAME,
    GROUP_CONCAT(COLUMN_NAME ORDER BY SEQ_IN_INDEX) as columns
FROM information_schema.STATISTICS 
WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME IN ('customers', 'customer_device_relation', 'customer_order_relation', 'customer_service_relation')
GROUP BY TABLE_NAME, GROUP_CONCAT(COLUMN_NAME ORDER BY SEQ_IN_INDEX)
HAVING COUNT(*) > 1;

-- 检查未使用的索引（需要开启performance_schema）
SELECT 
    'UNUSED_INDEX_CHECK' as check_type,
    s.TABLE_NAME,
    s.INDEX_NAME,
    'Potentially unused - check performance_schema for actual usage' as status
FROM information_schema.STATISTICS s
WHERE s.TABLE_SCHEMA = DATABASE()
    AND s.TABLE_NAME IN ('customers', 'customer_device_relation', 'customer_order_relation', 'customer_service_relation')
    AND s.INDEX_NAME != 'PRIMARY'
GROUP BY s.TABLE_NAME, s.INDEX_NAME;

-- 检查低基数索引
SELECT 
    'LOW_CARDINALITY_CHECK' as check_type,
    TABLE_NAME,
    INDEX_NAME,
    COLUMN_NAME,
    CARDINALITY,
    CASE 
        WHEN CARDINALITY < 10 THEN 'Very Low'
        WHEN CARDINALITY < 100 THEN 'Low'
        ELSE 'Acceptable'
    END as cardinality_status
FROM information_schema.STATISTICS 
WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME IN ('customers', 'customer_device_relation', 'customer_order_relation', 'customer_service_relation')
    AND CARDINALITY IS NOT NULL
    AND CARDINALITY < 100
ORDER BY CARDINALITY ASC;

-- ==================== 性能优化建议 ====================

-- 生成索引优化建议
SELECT 
    'INDEX_OPTIMIZATION_SUGGESTIONS' as suggestion_type,
    CASE 
        WHEN TABLE_NAME = 'customers' AND 
             (SELECT COUNT(*) FROM information_schema.STATISTICS 
              WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'customers' 
              AND COLUMN_NAME = 'customer_name') = 0
        THEN 'CREATE INDEX idx_customers_name ON customers(customer_name);'
        
        WHEN TABLE_NAME = 'customers' AND 
             (SELECT COUNT(*) FROM information_schema.STATISTICS 
              WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'customers' 
              AND INDEX_NAME LIKE '%status_level%') = 0
        THEN 'CREATE INDEX idx_customers_status_level ON customers(customer_status, customer_level);'
        
        WHEN TABLE_NAME = 'customer_device_relation' AND 
             (SELECT COUNT(*) FROM information_schema.STATISTICS 
              WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'customer_device_relation' 
              AND COLUMN_NAME = 'customer_id') = 0
        THEN 'CREATE INDEX idx_customer_device_customer_id ON customer_device_relation(customer_id);'
        
        ELSE 'No immediate suggestions'
    END as suggestion
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME IN ('customers', 'customer_device_relation', 'customer_order_relation', 'customer_service_relation')
HAVING suggestion != 'No immediate suggestions';

-- ==================== 维护建议 ====================

-- 生成表维护建议
SELECT 
    'MAINTENANCE_SUGGESTIONS' as suggestion_type,
    TABLE_NAME,
    CASE 
        WHEN (DATA_LENGTH + INDEX_LENGTH) > 100 * 1024 * 1024 THEN 
            CONCAT('OPTIMIZE TABLE ', TABLE_NAME, '; -- Table size: ', 
                   ROUND((DATA_LENGTH + INDEX_LENGTH) / 1024 / 1024, 2), 'MB')
        WHEN TABLE_ROWS > 100000 THEN 
            CONCAT('ANALYZE TABLE ', TABLE_NAME, '; -- Large table with ', TABLE_ROWS, ' rows')
        ELSE 'No immediate maintenance needed'
    END as maintenance_suggestion
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME IN ('customers', 'customer_device_relation', 'customer_order_relation', 'customer_service_relation')
HAVING maintenance_suggestion != 'No immediate maintenance needed';

-- ==================== 执行完成提示 ====================

SELECT 
    'OPTIMIZATION_COMPLETE' as status,
    'Customer database indexes optimization completed!' as message,
    NOW() as completion_time,
    (SELECT COUNT(*) FROM information_schema.STATISTICS 
     WHERE TABLE_SCHEMA = DATABASE() 
     AND TABLE_NAME IN ('customers', 'customer_device_relation', 'customer_order_relation', 'customer_service_relation')) as total_indexes_created;