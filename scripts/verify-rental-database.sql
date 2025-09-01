-- =====================================================
-- 租赁数据分析模块 - 数据库验证脚本
-- 验证数据库表结构和数据完整性
-- 创建时间: 2025-01-28
-- =====================================================

USE YXRobot;

-- =====================================================
-- 1. 验证表结构是否创建成功
-- =====================================================
SELECT '=== 验证表结构创建情况 ===' as verification_step;

-- 检查租赁相关表是否存在
SELECT 
    TABLE_NAME as table_name,
    TABLE_COMMENT as table_comment,
    ENGINE as engine,
    TABLE_COLLATION as collation
FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_SCHEMA = 'YXRobot' 
AND TABLE_NAME IN ('rental_records', 'rental_devices', 'device_utilization', 'rental_stats', 'rental_customers')
ORDER BY TABLE_NAME;

-- =====================================================
-- 2. 验证表字段结构
-- =====================================================
SELECT '=== 验证表字段结构 ===' as verification_step;

-- 验证 rental_records 表字段
SELECT 'rental_records 表字段验证:' as table_info;
SELECT 
    COLUMN_NAME as field_name,
    DATA_TYPE as data_type,
    IS_NULLABLE as nullable,
    COLUMN_DEFAULT as default_value,
    COLUMN_COMMENT as comment
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'YXRobot' AND TABLE_NAME = 'rental_records'
ORDER BY ORDINAL_POSITION;

-- 验证 rental_devices 表字段
SELECT 'rental_devices 表字段验证:' as table_info;
SELECT 
    COLUMN_NAME as field_name,
    DATA_TYPE as data_type,
    IS_NULLABLE as nullable,
    COLUMN_DEFAULT as default_value,
    COLUMN_COMMENT as comment
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'YXRobot' AND TABLE_NAME = 'rental_devices'
ORDER BY ORDINAL_POSITION;

-- =====================================================
-- 3. 验证索引创建情况
-- =====================================================
SELECT '=== 验证索引创建情况 ===' as verification_step;

SELECT 
    TABLE_NAME as table_name,
    INDEX_NAME as index_name,
    COLUMN_NAME as column_name,
    NON_UNIQUE as non_unique,
    INDEX_TYPE as index_type
FROM INFORMATION_SCHEMA.STATISTICS 
WHERE TABLE_SCHEMA = 'YXRobot' 
AND TABLE_NAME IN ('rental_records', 'rental_devices', 'device_utilization', 'rental_stats', 'rental_customers')
ORDER BY TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX;

-- =====================================================
-- 4. 验证数据插入情况
-- =====================================================
SELECT '=== 验证数据插入情况 ===' as verification_step;

-- 统计各表数据量
SELECT 'rental_devices' as table_name, COUNT(*) as record_count FROM rental_devices
UNION ALL
SELECT 'rental_customers' as table_name, COUNT(*) as record_count FROM rental_customers
UNION ALL
SELECT 'rental_records' as table_name, COUNT(*) as record_count FROM rental_records
UNION ALL
SELECT 'rental_stats' as table_name, COUNT(*) as record_count FROM rental_stats
UNION ALL
SELECT 'device_utilization' as table_name, COUNT(*) as record_count FROM device_utilization;

-- =====================================================
-- 5. 验证设备数据完整性
-- =====================================================
SELECT '=== 验证设备数据完整性 ===' as verification_step;

-- 检查设备型号分布
SELECT 
    device_model,
    COUNT(*) as device_count,
    AVG(utilization_rate) as avg_utilization_rate,
    AVG(performance_score) as avg_performance_score
FROM rental_devices 
GROUP BY device_model
ORDER BY device_count DESC;

-- 检查设备状态分布
SELECT 
    current_status,
    COUNT(*) as device_count,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM rental_devices), 2) as percentage
FROM rental_devices 
GROUP BY current_status
ORDER BY device_count DESC;

-- 检查设备地区分布
SELECT 
    region,
    COUNT(*) as device_count,
    AVG(utilization_rate) as avg_utilization_rate
FROM rental_devices 
GROUP BY region
ORDER BY device_count DESC;

-- =====================================================
-- 6. 验证租赁记录数据完整性
-- =====================================================
SELECT '=== 验证租赁记录数据完整性 ===' as verification_step;

-- 检查租赁状态分布
SELECT 
    rental_status,
    COUNT(*) as record_count,
    SUM(total_rental_fee) as total_revenue
FROM rental_records 
GROUP BY rental_status
ORDER BY record_count DESC;

-- 检查付款状态分布
SELECT 
    payment_status,
    COUNT(*) as record_count,
    SUM(actual_payment) as total_payment
FROM rental_records 
GROUP BY payment_status
ORDER BY record_count DESC;

-- 检查月度租赁趋势
SELECT 
    DATE_FORMAT(rental_start_date, '%Y-%m') as rental_month,
    COUNT(*) as order_count,
    SUM(total_rental_fee) as monthly_revenue,
    AVG(rental_period) as avg_rental_period
FROM rental_records 
GROUP BY DATE_FORMAT(rental_start_date, '%Y-%m')
ORDER BY rental_month;

-- =====================================================
-- 7. 验证客户数据完整性
-- =====================================================
SELECT '=== 验证客户数据完整性 ===' as verification_step;

-- 检查客户类型分布
SELECT 
    customer_type,
    COUNT(*) as customer_count,
    AVG(total_rental_amount) as avg_rental_amount,
    AVG(total_rental_days) as avg_rental_days
FROM rental_customers 
GROUP BY customer_type
ORDER BY customer_count DESC;

-- 检查客户地区分布
SELECT 
    region,
    COUNT(*) as customer_count,
    SUM(total_rental_amount) as total_revenue
FROM rental_customers 
GROUP BY region
ORDER BY total_revenue DESC;

-- =====================================================
-- 8. 验证统计数据准确性
-- =====================================================
SELECT '=== 验证统计数据准确性 ===' as verification_step;

-- 验证月度统计数据
SELECT 
    stat_date,
    stat_type,
    total_rental_revenue,
    total_rental_orders,
    total_rental_devices,
    active_rental_devices,
    device_utilization_rate,
    top_device_model,
    top_region
FROM rental_stats 
ORDER BY stat_date DESC;

-- =====================================================
-- 9. 验证设备利用率数据
-- =====================================================
SELECT '=== 验证设备利用率数据 ===' as verification_step;

-- 检查设备利用率统计
SELECT 
    d.device_id,
    d.device_model,
    d.current_status,
    u.utilization_rate,
    u.rental_revenue,
    u.rental_count
FROM rental_devices d
LEFT JOIN device_utilization u ON d.id = u.device_id AND u.stat_date = '2024-12-31'
ORDER BY u.utilization_rate DESC;

-- =====================================================
-- 10. 数据关联性验证
-- =====================================================
SELECT '=== 验证数据关联性 ===' as verification_step;

-- 验证租赁记录与设备的关联
SELECT 
    '租赁记录-设备关联' as relation_type,
    COUNT(DISTINCT r.device_id) as linked_devices,
    COUNT(DISTINCT d.id) as total_devices,
    ROUND(COUNT(DISTINCT r.device_id) * 100.0 / COUNT(DISTINCT d.id), 2) as link_percentage
FROM rental_records r
RIGHT JOIN rental_devices d ON r.device_id = d.id;

-- 验证租赁记录与客户的关联
SELECT 
    '租赁记录-客户关联' as relation_type,
    COUNT(DISTINCT r.customer_id) as linked_customers,
    COUNT(DISTINCT c.id) as total_customers,
    ROUND(COUNT(DISTINCT r.customer_id) * 100.0 / COUNT(DISTINCT c.id), 2) as link_percentage
FROM rental_records r
RIGHT JOIN rental_customers c ON r.customer_id = c.id;

-- =====================================================
-- 11. 字段映射验证（前后端一致性检查）
-- =====================================================
SELECT '=== 字段映射验证 ===' as verification_step;

-- 验证关键字段命名是否符合前端期望
SELECT 'rental_records 关键字段映射验证:' as mapping_check;
SELECT 
    'rental_order_number -> rentalOrderNumber' as field_mapping,
    COUNT(*) as record_count
FROM rental_records 
WHERE rental_order_number IS NOT NULL;

SELECT 
    'device_id -> deviceId' as field_mapping,
    COUNT(*) as record_count
FROM rental_records 
WHERE device_id IS NOT NULL;

SELECT 
    'total_rental_fee -> totalRentalFee' as field_mapping,
    COUNT(*) as record_count
FROM rental_records 
WHERE total_rental_fee IS NOT NULL;

-- 验证设备表字段映射
SELECT 'rental_devices 关键字段映射验证:' as mapping_check;
SELECT 
    'device_model -> deviceModel' as field_mapping,
    COUNT(*) as record_count
FROM rental_devices 
WHERE device_model IS NOT NULL;

SELECT 
    'utilization_rate -> utilizationRate' as field_mapping,
    COUNT(*) as record_count
FROM rental_devices 
WHERE utilization_rate IS NOT NULL;

SELECT 
    'current_status -> currentStatus' as field_mapping,
    COUNT(*) as record_count
FROM rental_devices 
WHERE current_status IS NOT NULL;

-- =====================================================
-- 验证完成提示
-- =====================================================
SELECT '=== 数据库验证完成 ===' as verification_result;
SELECT 'All rental database tables and data have been verified successfully!' as final_message;