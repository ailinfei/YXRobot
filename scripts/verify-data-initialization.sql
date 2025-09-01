-- 数据初始化验证脚本
-- 任务19：数据初始化和真实数据验证
-- 用于验证系统启动后的数据初始化状态

-- 设置查询结果显示格式
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

-- 显示验证开始信息
SELECT 
    '数据初始化验证开始' as 验证状态,
    NOW() as 验证时间,
    '检查系统数据初始化是否符合真实数据要求' as 验证目的;

-- 1. 验证客户表状态
SELECT '=== 客户表验证 ===' as 验证项目;

-- 检查客户表是否存在
SELECT 
    CASE 
        WHEN COUNT(*) > 0 THEN '客户表存在'
        ELSE '客户表不存在'
    END as 表状态
FROM information_schema.tables 
WHERE table_schema = DATABASE() AND table_name = 'customers';

-- 检查客户表记录数
SELECT 
    COALESCE(COUNT(*), 0) as 客户记录总数,
    COALESCE(COUNT(CASE WHEN is_deleted = 0 OR is_deleted IS NULL THEN 1 END), 0) as 有效客户数,
    CASE 
        WHEN COALESCE(COUNT(CASE WHEN is_deleted = 0 OR is_deleted IS NULL THEN 1 END), 0) = 0 
        THEN '✓ 符合要求 - 无示例数据'
        ELSE '⚠ 需要检查 - 存在客户数据'
    END as 验证结果
FROM customers
WHERE EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'customers');

-- 检查可疑的模拟客户数据
SELECT 
    '检查疑似模拟客户数据' as 检查项目,
    COALESCE(COUNT(*), 0) as 疑似模拟数据数量
FROM customers 
WHERE 
    EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'customers')
    AND (is_deleted = 0 OR is_deleted IS NULL)
    AND (
        name LIKE '%测试%' OR name LIKE '%test%' OR name LIKE '%demo%' OR name LIKE '%示例%'
        OR email LIKE '%test%' OR email LIKE '%example%' OR email LIKE '%demo%'
        OR phone LIKE '000-%' OR phone LIKE '111-%' OR phone = '0000000000'
        OR address LIKE '%测试%' OR address LIKE '%虚拟%'
    );

-- 2. 验证客户等级配置表
SELECT '=== 客户等级配置验证 ===' as 验证项目;

SELECT 
    CASE 
        WHEN COUNT(*) > 0 THEN '客户等级配置表存在'
        ELSE '客户等级配置表不存在'
    END as 表状态
FROM information_schema.tables 
WHERE table_schema = DATABASE() AND table_name = 'customer_level_configs';

SELECT 
    COALESCE(COUNT(*), 0) as 等级配置数量,
    COALESCE(COUNT(CASE WHEN is_active = 1 THEN 1 END), 0) as 启用配置数量,
    CASE 
        WHEN COALESCE(COUNT(CASE WHEN is_active = 1 THEN 1 END), 0) >= 3 
        THEN '✓ 符合要求 - 基础等级配置完整'
        ELSE '⚠ 需要检查 - 等级配置不足'
    END as 验证结果
FROM customer_level_configs
WHERE EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'customer_level_configs');

-- 3. 验证客户标签配置表
SELECT '=== 客户标签配置验证 ===' as 验证项目;

SELECT 
    CASE 
        WHEN COUNT(*) > 0 THEN '客户标签配置表存在'
        ELSE '客户标签配置表不存在'
    END as 表状态
FROM information_schema.tables 
WHERE table_schema = DATABASE() AND table_name = 'customer_tag_configs';

SELECT 
    COALESCE(COUNT(*), 0) as 标签配置数量,
    COALESCE(COUNT(CASE WHEN is_active = 1 THEN 1 END), 0) as 启用配置数量,
    CASE 
        WHEN COALESCE(COUNT(CASE WHEN is_active = 1 THEN 1 END), 0) >= 10 
        THEN '✓ 符合要求 - 基础标签配置完整'
        ELSE '⚠ 需要检查 - 标签配置不足'
    END as 验证结果
FROM customer_tag_configs
WHERE EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'customer_tag_configs');

-- 4. 验证销售记录表状态
SELECT '=== 销售记录表验证 ===' as 验证项目;

SELECT 
    CASE 
        WHEN COUNT(*) > 0 THEN '销售记录表存在'
        ELSE '销售记录表不存在'
    END as 表状态
FROM information_schema.tables 
WHERE table_schema = DATABASE() AND table_name = 'sales_records';

SELECT 
    COALESCE(COUNT(*), 0) as 销售记录总数,
    COALESCE(COUNT(CASE WHEN is_deleted = 0 OR is_deleted IS NULL THEN 1 END), 0) as 有效销售记录数,
    CASE 
        WHEN COALESCE(COUNT(CASE WHEN is_deleted = 0 OR is_deleted IS NULL THEN 1 END), 0) = 0 
        THEN '✓ 符合要求 - 无示例数据'
        ELSE '⚠ 需要检查 - 存在销售数据'
    END as 验证结果
FROM sales_records
WHERE EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'sales_records');

-- 5. 验证租赁记录表状态
SELECT '=== 租赁记录表验证 ===' as 验证项目;

SELECT 
    CASE 
        WHEN COUNT(*) > 0 THEN '租赁记录表存在'
        ELSE '租赁记录表不存在'
    END as 表状态
FROM information_schema.tables 
WHERE table_schema = DATABASE() AND table_name = 'rental_records';

SELECT 
    COALESCE(COUNT(*), 0) as 租赁记录总数,
    COALESCE(COUNT(CASE WHEN is_deleted = 0 OR is_deleted IS NULL THEN 1 END), 0) as 有效租赁记录数,
    CASE 
        WHEN COALESCE(COUNT(CASE WHEN is_deleted = 0 OR is_deleted IS NULL THEN 1 END), 0) = 0 
        THEN '✓ 符合要求 - 无示例数据'
        ELSE '⚠ 需要检查 - 存在租赁数据'
    END as 验证结果
FROM rental_records
WHERE EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'rental_records');

-- 6. 验证基础配置数据
SELECT '=== 基础配置数据验证 ===' as 验证项目;

-- 验证区域配置
SELECT 
    '区域配置' as 配置类型,
    COALESCE(COUNT(*), 0) as 配置数量,
    CASE 
        WHEN COALESCE(COUNT(*), 0) >= 5 
        THEN '✓ 符合要求'
        ELSE '⚠ 配置不足'
    END as 验证结果
FROM region_configs
WHERE EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'region_configs');

-- 验证新闻分类配置
SELECT 
    '新闻分类配置' as 配置类型,
    COALESCE(COUNT(*), 0) as 配置数量,
    CASE 
        WHEN COALESCE(COUNT(*), 0) >= 5 
        THEN '✓ 符合要求'
        ELSE '⚠ 配置不足'
    END as 验证结果
FROM news_categories
WHERE EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'news_categories');

-- 验证新闻标签配置
SELECT 
    '新闻标签配置' as 配置类型,
    COALESCE(COUNT(*), 0) as 配置数量,
    CASE 
        WHEN COALESCE(COUNT(*), 0) >= 10 
        THEN '✓ 符合要求'
        ELSE '⚠ 配置不足'
    END as 验证结果
FROM news_tags
WHERE EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'news_tags');

-- 7. 生成验证总结
SELECT '=== 验证总结 ===' as 验证项目;

SELECT 
    '数据初始化验证完成' as 验证状态,
    NOW() as 完成时间,
    CASE 
        WHEN (
            -- 检查客户表为空
            COALESCE((SELECT COUNT(*) FROM customers WHERE (is_deleted = 0 OR is_deleted IS NULL) AND EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'customers')), 0) = 0
            AND
            -- 检查销售记录表为空
            COALESCE((SELECT COUNT(*) FROM sales_records WHERE (is_deleted = 0 OR is_deleted IS NULL) AND EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'sales_records')), 0) = 0
            AND
            -- 检查租赁记录表为空
            COALESCE((SELECT COUNT(*) FROM rental_records WHERE (is_deleted = 0 OR is_deleted IS NULL) AND EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'rental_records')), 0) = 0
        ) THEN '✓ 验证通过 - 系统符合真实数据要求'
        ELSE '⚠ 需要注意 - 系统中存在业务数据，请确认是否为真实数据'
    END as 总体结论;

-- 8. 提供建议和下一步操作
SELECT 
    '建议和下一步操作' as 类型,
    '1. 如果发现示例数据，请运行 remove-mock-customer-data.sql 清理脚本' as 建议1,
    '2. 确保所有业务数据都通过管理后台手动录入' as 建议2,
    '3. 定期运行此验证脚本检查数据质量' as 建议3,
    '4. 在生产环境中启用严格的数据验证模式' as 建议4;

-- 恢复SQL模式
SET SQL_MODE=@OLD_SQL_MODE;

-- 验证完成提示
SELECT 
    '数据初始化验证脚本执行完成' as 状态,
    '请查看上述验证结果，确保系统符合真实数据要求' as 提醒;