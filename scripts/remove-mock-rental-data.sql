-- 清理模拟租赁数据脚本
-- 用于清理系统中可能存在的模拟/示例租赁数据，确保数据库只包含真实数据
-- 
-- @author YXRobot开发团队
-- @version 1.0.0
-- @since 2025-01-25

-- 设置安全模式，防止误删
SET SQL_SAFE_UPDATES = 0;

-- 开始事务
START TRANSACTION;

-- 记录清理前的数据统计
SELECT 
    '清理前数据统计' as operation,
    (SELECT COUNT(*) FROM rental_records WHERE is_deleted = 0) as rental_records_count,
    (SELECT COUNT(*) FROM rental_devices WHERE is_deleted = 0) as rental_devices_count,
    (SELECT COUNT(*) FROM rental_customers WHERE is_deleted = 0) as rental_customers_count,
    (SELECT COUNT(*) FROM device_utilization) as device_utilization_count,
    (SELECT COUNT(*) FROM rental_stats) as rental_stats_count;

-- 1. 清理包含模拟数据特征的租赁记录
-- 删除订单号包含测试、示例、demo等关键词的记录
UPDATE rental_records 
SET is_deleted = 1, updated_at = NOW()
WHERE is_deleted = 0 
AND (
    rental_order_number LIKE '%TEST%' OR
    rental_order_number LIKE '%DEMO%' OR
    rental_order_number LIKE '%SAMPLE%' OR
    rental_order_number LIKE '%MOCK%' OR
    rental_order_number LIKE '%FAKE%' OR
    rental_order_number LIKE '%示例%' OR
    rental_order_number LIKE '%测试%' OR
    rental_order_number LIKE '%演示%' OR
    notes LIKE '%系统生成%' OR
    notes LIKE '%示例数据%' OR
    notes LIKE '%测试数据%'
);

-- 2. 清理包含模拟数据特征的客户记录
-- 删除客户名称包含测试、示例等关键词的记录
UPDATE rental_customers 
SET is_deleted = 1, updated_at = NOW()
WHERE is_deleted = 0 
AND (
    customer_name LIKE '%测试%' OR
    customer_name LIKE '%示例%' OR
    customer_name LIKE '%DEMO%' OR
    customer_name LIKE '%TEST%' OR
    customer_name LIKE '%SAMPLE%' OR
    customer_name LIKE '%MOCK%' OR
    customer_name LIKE '%类型%' OR  -- 清理客户类型示例数据
    email LIKE '%example.com%' OR
    phone LIKE '000-0000-%'
);

-- 3. 清理设备利用率统计数据（重新计算）
-- 删除所有统计数据，让系统重新计算真实数据
DELETE FROM device_utilization WHERE 1=1;

-- 4. 清理租赁统计数据（重新计算）
-- 删除所有统计数据，让系统重新计算真实数据
DELETE FROM rental_stats WHERE 1=1;

-- 5. 重置设备利用率数据
-- 将所有设备的利用率相关字段重置为0
UPDATE rental_devices 
SET 
    total_rental_days = 0,
    total_available_days = 0,
    utilization_rate = 0.00,
    last_rental_date = NULL,
    updated_at = NOW()
WHERE is_deleted = 0;

-- 6. 清理可能的系统自动生成数据
-- 删除创建时间在系统初始化时间段内的可疑数据
UPDATE rental_records 
SET is_deleted = 1, updated_at = NOW()
WHERE is_deleted = 0 
AND created_at BETWEEN '2024-01-01 00:00:00' AND '2024-12-31 23:59:59'
AND (
    rental_order_number REGEXP '^[A-Z]{2,4}[0-9]{6,10}$' OR  -- 匹配系统生成的订单号格式
    notes IS NULL OR
    notes = '' OR
    delivery_address LIKE '%示例%' OR
    delivery_address LIKE '%测试%'
);

-- 记录清理后的数据统计
SELECT 
    '清理后数据统计' as operation,
    (SELECT COUNT(*) FROM rental_records WHERE is_deleted = 0) as rental_records_count,
    (SELECT COUNT(*) FROM rental_devices WHERE is_deleted = 0) as rental_devices_count,
    (SELECT COUNT(*) FROM rental_customers WHERE is_deleted = 0) as rental_customers_count,
    (SELECT COUNT(*) FROM device_utilization) as device_utilization_count,
    (SELECT COUNT(*) FROM rental_stats) as rental_stats_count;

-- 显示被清理的数据统计
SELECT 
    '被清理的数据统计' as operation,
    (SELECT COUNT(*) FROM rental_records WHERE is_deleted = 1) as deleted_rental_records,
    (SELECT COUNT(*) FROM rental_customers WHERE is_deleted = 1) as deleted_customers;

-- 验证清理结果
-- 检查是否还有可疑的模拟数据
SELECT 
    '可疑数据检查' as check_type,
    rental_order_number,
    notes,
    created_at
FROM rental_records 
WHERE is_deleted = 0 
AND (
    rental_order_number LIKE '%TEST%' OR
    rental_order_number LIKE '%DEMO%' OR
    notes LIKE '%示例%' OR
    notes LIKE '%测试%'
)
LIMIT 10;

-- 提交事务
COMMIT;

-- 恢复安全模式
SET SQL_SAFE_UPDATES = 1;

-- 输出清理完成信息
SELECT 
    '清理完成' as status,
    '所有模拟/示例租赁数据已被清理' as message,
    '系统现在只包含真实数据或基础配置数据' as note,
    NOW() as completed_at;