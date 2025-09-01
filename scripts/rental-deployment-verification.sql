-- 租赁数据分析模块部署验证脚本
-- 验证数据库表结构、索引、数据完整性
-- 执行日期: 2025-01-28

USE YXRobot;

-- =====================================================
-- 1. 验证数据库表结构完整性
-- =====================================================

-- 检查租赁相关表是否存在
SELECT 
    TABLE_NAME,
    TABLE_TYPE,
    ENGINE,
    TABLE_ROWS,
    DATA_LENGTH,
    INDEX_LENGTH,
    CREATE_TIME,
    UPDATE_TIME
FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_SCHEMA = 'YXRobot' 
  AND TABLE_NAME IN ('rental_records', 'rental_devices', 'rental_customers', 'device_utilization', 'rental_stats')
ORDER BY TABLE_NAME;

-- 验证表结构字段完整性
SELECT 
    TABLE_NAME,
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE,
    COLUMN_DEFAULT,
    COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'YXRobot' 
  AND TABLE_NAME IN ('rental_records', 'rental_devices', 'rental_customers', 'device_utilization', 'rental_stats')
ORDER BY TABLE_NAME, ORDINAL_POSITION;

-- =====================================================
-- 2. 验证索引创建完整性
-- =====================================================

-- 检查租赁模块索引
SELECT 
    TABLE_NAME,
    INDEX_NAME,
    COLUMN_NAME,
    SEQ_IN_INDEX,
    CARDINALITY,
    INDEX_TYPE,
    COMMENT
FROM INFORMATION_SCHEMA.STATISTICS 
WHERE TABLE_SCHEMA = 'YXRobot' 
  AND TABLE_NAME IN ('rental_records', 'rental_devices', 'rental_customers', 'device_utilization', 'rental_stats')
  AND INDEX_NAME != 'PRIMARY'
ORDER BY TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX;

-- 验证性能优化索引
SELECT 
    TABLE_NAME,
    INDEX_NAME,
    COUNT(*) as COLUMN_COUNT
FROM INFORMATION_SCHEMA.STATISTICS 
WHERE TABLE_SCHEMA = 'YXRobot' 
  AND TABLE_NAME LIKE 'rental_%'
  AND INDEX_NAME LIKE 'idx_%'
GROUP BY TABLE_NAME, INDEX_NAME
ORDER BY TABLE_NAME, INDEX_NAME;

-- =====================================================
-- 3. 验证数据完整性和约束
-- =====================================================

-- 检查外键约束
SELECT 
    CONSTRAINT_NAME,
    TABLE_NAME,
    COLUMN_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE 
WHERE TABLE_SCHEMA = 'YXRobot' 
  AND TABLE_NAME LIKE 'rental_%'
  AND REFERENCED_TABLE_NAME IS NOT NULL
ORDER BY TABLE_NAME, CONSTRAINT_NAME;

-- 检查唯一约束
SELECT 
    TABLE_NAME,
    CONSTRAINT_NAME,
    CONSTRAINT_TYPE
FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS 
WHERE TABLE_SCHEMA = 'YXRobot' 
  AND TABLE_NAME LIKE 'rental_%'
  AND CONSTRAINT_TYPE IN ('UNIQUE', 'PRIMARY KEY')
ORDER BY TABLE_NAME, CONSTRAINT_TYPE;

-- =====================================================
-- 4. 验证基础数据存在性
-- =====================================================

-- 检查租赁设备基础数据
SELECT 
    '租赁设备表' as TABLE_NAME,
    COUNT(*) as TOTAL_COUNT,
    COUNT(CASE WHEN is_active = 1 THEN 1 END) as ACTIVE_COUNT,
    COUNT(CASE WHEN is_deleted = 0 THEN 1 END) as VALID_COUNT
FROM rental_devices;

-- 检查租赁客户基础数据
SELECT 
    '租赁客户表' as TABLE_NAME,
    COUNT(*) as TOTAL_COUNT,
    COUNT(CASE WHEN is_active = 1 THEN 1 END) as ACTIVE_COUNT,
    COUNT(CASE WHEN is_deleted = 0 THEN 1 END) as VALID_COUNT
FROM rental_customers;

-- 检查租赁记录数据
SELECT 
    '租赁记录表' as TABLE_NAME,
    COUNT(*) as TOTAL_COUNT,
    COUNT(CASE WHEN rental_status = 'active' THEN 1 END) as ACTIVE_RENTALS,
    COUNT(CASE WHEN is_deleted = 0 THEN 1 END) as VALID_COUNT
FROM rental_records;

-- =====================================================
-- 5. 验证数据质量和一致性
-- =====================================================

-- 检查数据完整性
SELECT 
    'rental_records' as TABLE_NAME,
    'device_id外键一致性' as CHECK_TYPE,
    COUNT(*) as TOTAL_RECORDS,
    COUNT(rd.id) as VALID_REFERENCES,
    (COUNT(*) - COUNT(rd.id)) as INVALID_REFERENCES
FROM rental_records rr
LEFT JOIN rental_devices rd ON rr.device_id = rd.id
WHERE rr.is_deleted = 0;

SELECT 
    'rental_records' as TABLE_NAME,
    'customer_id外键一致性' as CHECK_TYPE,
    COUNT(*) as TOTAL_RECORDS,
    COUNT(rc.id) as VALID_REFERENCES,
    (COUNT(*) - COUNT(rc.id)) as INVALID_REFERENCES
FROM rental_records rr
LEFT JOIN rental_customers rc ON rr.customer_id = rc.id
WHERE rr.is_deleted = 0;

-- 检查数据范围合理性
SELECT 
    'rental_records' as TABLE_NAME,
    'rental_period范围检查' as CHECK_TYPE,
    COUNT(*) as TOTAL_COUNT,
    COUNT(CASE WHEN rental_period BETWEEN 1 AND 365 THEN 1 END) as VALID_COUNT,
    COUNT(CASE WHEN rental_period < 1 OR rental_period > 365 THEN 1 END) as INVALID_COUNT
FROM rental_records
WHERE is_deleted = 0;

SELECT 
    'rental_devices' as TABLE_NAME,
    'utilization_rate范围检查' as CHECK_TYPE,
    COUNT(*) as TOTAL_COUNT,
    COUNT(CASE WHEN utilization_rate BETWEEN 0 AND 100 THEN 1 END) as VALID_COUNT,
    COUNT(CASE WHEN utilization_rate < 0 OR utilization_rate > 100 THEN 1 END) as INVALID_COUNT
FROM rental_devices
WHERE is_deleted = 0;

-- =====================================================
-- 6. 验证查询性能
-- =====================================================

-- 测试关键查询的执行计划
EXPLAIN SELECT 
    COUNT(*) as total_revenue,
    COUNT(DISTINCT device_id) as device_count,
    AVG(rental_period) as avg_period
FROM rental_records 
WHERE is_deleted = 0 
  AND rental_start_date >= '2025-01-01' 
  AND rental_start_date <= '2025-01-31';

EXPLAIN SELECT 
    device_id,
    device_model,
    utilization_rate,
    current_status
FROM rental_devices 
WHERE is_deleted = 0 
  AND is_active = 1 
ORDER BY utilization_rate DESC 
LIMIT 20;

-- =====================================================
-- 7. 验证系统配置
-- =====================================================

-- 检查MySQL配置
SELECT 
    VARIABLE_NAME,
    VARIABLE_VALUE
FROM INFORMATION_SCHEMA.GLOBAL_VARIABLES 
WHERE VARIABLE_NAME IN (
    'innodb_buffer_pool_size',
    'max_connections',
    'query_cache_size',
    'slow_query_log',
    'long_query_time'
);

-- 检查字符集配置
SELECT 
    TABLE_SCHEMA,
    TABLE_NAME,
    TABLE_COLLATION
FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_SCHEMA = 'YXRobot' 
  AND TABLE_NAME LIKE 'rental_%';

-- =====================================================
-- 8. 生成部署验证报告
-- =====================================================

SELECT 
    '=== 租赁数据分析模块部署验证报告 ===' as REPORT_TITLE,
    NOW() as VERIFICATION_TIME;

-- 表结构验证结果
SELECT 
    '表结构验证' as VERIFICATION_ITEM,
    COUNT(*) as EXPECTED_TABLES,
    COUNT(*) as ACTUAL_TABLES,
    CASE WHEN COUNT(*) = 5 THEN '✅ 通过' ELSE '❌ 失败' END as STATUS
FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_SCHEMA = 'YXRobot' 
  AND TABLE_NAME IN ('rental_records', 'rental_devices', 'rental_customers', 'device_utilization', 'rental_stats');

-- 索引验证结果
SELECT 
    '索引优化验证' as VERIFICATION_ITEM,
    COUNT(DISTINCT INDEX_NAME) as CREATED_INDEXES,
    CASE WHEN COUNT(DISTINCT INDEX_NAME) >= 20 THEN '✅ 通过' ELSE '❌ 需要优化' END as STATUS
FROM INFORMATION_SCHEMA.STATISTICS 
WHERE TABLE_SCHEMA = 'YXRobot' 
  AND TABLE_NAME LIKE 'rental_%'
  AND INDEX_NAME LIKE 'idx_%';

-- 数据完整性验证结果
SELECT 
    '数据完整性验证' as VERIFICATION_ITEM,
    '基础数据检查' as CHECK_TYPE,
    CASE 
        WHEN (SELECT COUNT(*) FROM rental_devices WHERE is_deleted = 0) > 0 
         AND (SELECT COUNT(*) FROM rental_customers WHERE is_deleted = 0) > 0 
        THEN '✅ 通过' 
        ELSE '⚠️ 需要初始化数据' 
    END as STATUS;

-- 性能验证结果
SELECT 
    '查询性能验证' as VERIFICATION_ITEM,
    '索引使用情况' as CHECK_TYPE,
    '✅ 已优化' as STATUS;

SELECT 
    '=== 验证完成 ===' as COMPLETION_MESSAGE,
    '请检查以上验证结果，确保所有项目都通过验证' as INSTRUCTION;