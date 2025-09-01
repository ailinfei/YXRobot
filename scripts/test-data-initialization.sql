-- 测试数据初始化功能
-- 验证DataInitializationService的功能是否正常工作
-- 
-- @author YXRobot开发团队
-- @version 1.0.0
-- @since 2025-01-25

-- 连接到数据库
USE YXRobot;

-- 1. 测试前清理（可选）
-- DELETE FROM rental_records WHERE rental_order_number LIKE 'TEST%';
-- DELETE FROM rental_devices WHERE device_id LIKE 'TEST%';
-- DELETE FROM rental_customers WHERE customer_name LIKE 'TEST%';

-- 2. 验证基础数据是否存在
SELECT '=== 基础数据验证 ===' as test_section;

-- 验证设备基础数据
SELECT 
    '设备基础数据' as data_type,
    COUNT(*) as count,
    GROUP_CONCAT(DISTINCT device_model) as models
FROM rental_devices 
WHERE is_deleted = 0;

-- 验证客户类型基础数据
SELECT 
    '客户类型基础数据' as data_type,
    COUNT(*) as count,
    GROUP_CONCAT(DISTINCT customer_type) as types
FROM rental_customers 
WHERE is_deleted = 0;

-- 验证地区配置数据
SELECT 
    '地区配置数据' as data_type,
    COUNT(*) as count,
    GROUP_CONCAT(DISTINCT region) as regions
FROM region_configs 
WHERE is_active = 1;

-- 3. 验证租赁记录表为空
SELECT '=== 租赁记录验证 ===' as test_section;

SELECT 
    '租赁记录表状态' as check_type,
    COUNT(*) as record_count,
    CASE 
        WHEN COUNT(*) = 0 THEN 'PASS - 表为空，符合真实数据原则'
        ELSE 'FAIL - 表中有数据，需要检查'
    END as status
FROM rental_records 
WHERE is_deleted = 0;

-- 4. 检查模拟数据特征
SELECT '=== 模拟数据检查 ===' as test_section;

-- 检查租赁记录中的模拟数据
SELECT 
    '租赁记录模拟数据检查' as check_type,
    COUNT(*) as suspicious_count,
    CASE 
        WHEN COUNT(*) = 0 THEN 'PASS - 未发现模拟数据'
        ELSE 'FAIL - 发现模拟数据特征'
    END as status
FROM rental_records 
WHERE is_deleted = 0 
AND (
    rental_order_number LIKE '%TEST%' OR
    rental_order_number LIKE '%DEMO%' OR
    rental_order_number LIKE '%SAMPLE%' OR
    rental_order_number LIKE '%示例%' OR
    rental_order_number LIKE '%测试%' OR
    notes LIKE '%系统生成%'
);

-- 检查客户信息中的模拟数据（排除基础类型数据）
SELECT 
    '客户信息模拟数据检查' as check_type,
    COUNT(*) as suspicious_count,
    CASE 
        WHEN COUNT(*) = 0 THEN 'PASS - 未发现可疑客户数据'
        ELSE 'WARNING - 发现可疑客户数据'
    END as status
FROM rental_customers 
WHERE is_deleted = 0 
AND customer_name NOT LIKE '%类型%'  -- 排除基础类型数据
AND (
    customer_name LIKE '%测试%' OR
    customer_name LIKE '%示例%' OR
    customer_name LIKE '%DEMO%' OR
    email LIKE '%example.com%' OR
    phone LIKE '000-0000-%'
);

-- 5. 验证设备利用率初始状态
SELECT '=== 设备利用率验证 ===' as test_section;

SELECT 
    '设备利用率初始状态' as check_type,
    COUNT(*) as total_devices,
    SUM(CASE WHEN utilization_rate = 0 THEN 1 ELSE 0 END) as zero_utilization_count,
    CASE 
        WHEN SUM(CASE WHEN utilization_rate = 0 THEN 1 ELSE 0 END) = COUNT(*) 
        THEN 'PASS - 所有设备利用率为0'
        ELSE 'FAIL - 部分设备利用率不为0'
    END as status
FROM rental_devices 
WHERE is_deleted = 0;

-- 6. 验证统计表为空
SELECT '=== 统计表验证 ===' as test_section;

SELECT 
    '设备利用率统计表' as table_name,
    COUNT(*) as record_count,
    CASE 
        WHEN COUNT(*) = 0 THEN 'PASS - 统计表为空'
        ELSE 'WARNING - 统计表有数据'
    END as status
FROM device_utilization

UNION ALL

SELECT 
    '租赁统计表' as table_name,
    COUNT(*) as record_count,
    CASE 
        WHEN COUNT(*) = 0 THEN 'PASS - 统计表为空'
        ELSE 'WARNING - 统计表有数据'
    END as status
FROM rental_stats;

-- 7. 生成测试报告
SELECT '=== 测试报告 ===' as test_section;

SELECT 
    '数据初始化测试结果' as report_type,
    CASE 
        WHEN (SELECT COUNT(*) FROM rental_records WHERE is_deleted = 0) = 0
        AND (SELECT COUNT(*) FROM rental_devices WHERE is_deleted = 0) > 0
        AND (SELECT COUNT(*) FROM rental_customers WHERE is_deleted = 0) > 0
        THEN 'PASS - 数据初始化符合要求'
        ELSE 'FAIL - 数据初始化存在问题'
    END as overall_status,
    NOW() as test_time;

-- 8. 详细数据统计
SELECT 
    '详细统计' as section,
    'rental_devices' as table_name,
    COUNT(*) as total_count,
    COUNT(DISTINCT device_model) as unique_models
FROM rental_devices 
WHERE is_deleted = 0

UNION ALL

SELECT 
    '详细统计' as section,
    'rental_customers' as table_name,
    COUNT(*) as total_count,
    COUNT(DISTINCT customer_type) as unique_types
FROM rental_customers 
WHERE is_deleted = 0

UNION ALL

SELECT 
    '详细统计' as section,
    'rental_records' as table_name,
    COUNT(*) as total_count,
    0 as unique_count
FROM rental_records 
WHERE is_deleted = 0;

-- 9. 如果发现问题，显示详细信息
SELECT '=== 问题详情 ===' as section;

-- 显示可疑的租赁记录
SELECT 
    '可疑租赁记录' as issue_type,
    rental_order_number,
    notes,
    created_at
FROM rental_records 
WHERE is_deleted = 0 
AND (
    rental_order_number LIKE '%TEST%' OR
    rental_order_number LIKE '%DEMO%' OR
    notes LIKE '%示例%'
)
LIMIT 5;

-- 显示可疑的客户信息
SELECT 
    '可疑客户信息' as issue_type,
    customer_name,
    email,
    phone
FROM rental_customers 
WHERE is_deleted = 0 
AND customer_name NOT LIKE '%类型%'
AND (
    customer_name LIKE '%测试%' OR
    email LIKE '%example.com%'
)
LIMIT 5;