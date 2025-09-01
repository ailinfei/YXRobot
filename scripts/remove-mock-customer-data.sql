-- 模拟客户数据清理脚本
-- 用于清理系统中的示例/模拟客户数据，确保使用真实数据
-- 
-- 执行前请备份数据库！
-- 
-- 作者: YXRobot开发团队
-- 版本: 1.0
-- 日期: 2025-01-25

-- 开始事务
START TRANSACTION;

-- 记录清理开始时间
SELECT 
    '开始清理模拟客户数据' as 操作,
    NOW() as 开始时间;

-- 1. 清理客户表中的示例数据
-- 删除明显的测试/示例客户数据
DELETE FROM customers 
WHERE 
    -- 删除包含测试关键词的客户
    (customer_name LIKE '%测试%' 
     OR customer_name LIKE '%示例%' 
     OR customer_name LIKE '%test%' 
     OR customer_name LIKE '%demo%'
     OR customer_name LIKE '%mock%'
     OR customer_name LIKE '%sample%'
     OR customer_name LIKE '%example%')
    -- 删除明显的假数据
    OR (phone LIKE '000-%' 
        OR phone LIKE '111-%' 
        OR phone LIKE '123-%'
        OR phone = '000-0000-0000'
        OR phone = '111-1111-1111'
        OR phone = '123-4567-8901')
    -- 删除示例邮箱
    OR (email LIKE '%@example.com' 
        OR email LIKE '%@test.com' 
        OR email LIKE '%@demo.com'
        OR email LIKE '%@mock.com'
        OR email LIKE '%@sample.com')
    -- 删除明显的假地址
    OR (address LIKE '%示例%' 
        OR address LIKE '%测试%' 
        OR address LIKE '%test%'
        OR address LIKE '%demo%'
        OR address LIKE '%example%');

-- 记录删除的客户数量
SELECT ROW_COUNT() as 已删除客户数量;

-- 2. 清理客户设备关联表中的相关数据
-- 删除已删除客户的设备关联
DELETE FROM customer_devices 
WHERE customer_id NOT IN (SELECT id FROM customers WHERE is_deleted = 0 OR is_deleted IS NULL);

SELECT ROW_COUNT() as 已删除客户设备关联数量;

-- 3. 清理客户订单关联表中的相关数据
-- 删除已删除客户的订单关联
DELETE FROM customer_orders 
WHERE customer_id NOT IN (SELECT id FROM customers WHERE is_deleted = 0 OR is_deleted IS NULL);

SELECT ROW_COUNT() as 已删除客户订单关联数量;

-- 4. 清理客户服务记录关联表中的相关数据
-- 删除已删除客户的服务记录关联
DELETE FROM customer_service_records 
WHERE customer_id NOT IN (SELECT id FROM customers WHERE is_deleted = 0 OR is_deleted IS NULL);

SELECT ROW_COUNT() as 已删除客户服务记录关联数量;

-- 5. 清理客户统计表中的相关数据
-- 删除已删除客户的统计数据
DELETE FROM customer_stats 
WHERE customer_id NOT IN (SELECT id FROM customers WHERE is_deleted = 0 OR is_deleted IS NULL);

SELECT ROW_COUNT() as 已删除客户统计数据数量;

-- 6. 清理租赁客户表中的示例数据
-- 删除明显的测试/示例租赁客户数据
DELETE FROM rental_customers 
WHERE 
    -- 删除包含测试关键词的客户
    (customer_name LIKE '%测试%' 
     OR customer_name LIKE '%示例%' 
     OR customer_name LIKE '%test%' 
     OR customer_name LIKE '%demo%'
     OR customer_name LIKE '%mock%'
     OR customer_name LIKE '%sample%'
     OR customer_name LIKE '%example%'
     OR customer_name LIKE '%类型%')  -- 删除"XX类型"这样的分类数据
    -- 删除明显的假数据
    OR (phone LIKE '000-%' 
        OR phone LIKE '111-%' 
        OR phone LIKE '123-%'
        OR phone = '000-0000-0000'
        OR phone = '000-0000-0001'
        OR phone = '000-0000-0002')
    -- 删除示例邮箱
    OR (email LIKE '%@example.com' 
        OR email LIKE '%@test.com' 
        OR email LIKE '%@demo.com'
        OR email LIKE '%@mock.com'
        OR email LIKE '%@sample.com')
    -- 删除明显的假地址
    OR (address LIKE '%示例%' 
        OR address LIKE '%测试%' 
        OR address LIKE '%test%'
        OR address LIKE '%demo%'
        OR address LIKE '%example%'
        OR address LIKE '%类型%'
        OR address = '个人地址'
        OR address = '企业地址'
        OR address = '机构地址');

SELECT ROW_COUNT() as 已删除租赁客户数量;

-- 7. 清理租赁记录表中的示例数据
-- 删除与已删除客户相关的租赁记录
DELETE FROM rental_records 
WHERE customer_id NOT IN (
    SELECT customer_id FROM rental_customers 
    WHERE is_deleted = 0 OR is_deleted IS NULL
);

SELECT ROW_COUNT() as 已删除租赁记录数量;

-- 8. 验证清理结果
SELECT 
    '数据清理验证' as 验证项目,
    '客户表' as 表名,
    COUNT(*) as 剩余记录数
FROM customers 
WHERE is_deleted = 0 OR is_deleted IS NULL

UNION ALL

SELECT 
    '数据清理验证' as 验证项目,
    '租赁客户表' as 表名,
    COUNT(*) as 剩余记录数
FROM rental_customers 
WHERE is_deleted = 0 OR is_deleted IS NULL

UNION ALL

SELECT 
    '数据清理验证' as 验证项目,
    '租赁记录表' as 表名,
    COUNT(*) as 剩余记录数
FROM rental_records 
WHERE is_deleted = 0 OR is_deleted IS NULL;

-- 9. 检查是否还有可疑的测试数据
SELECT 
    '可疑数据检查' as 检查项目,
    customer_name,
    phone,
    email,
    address
FROM customers 
WHERE 
    (customer_name REGEXP '(测试|示例|test|demo|mock|sample|example|张三|李四|王五|赵六)'
     OR phone REGEXP '^(000|111|123|999)'
     OR email REGEXP '@(example|test|demo|mock|sample)\\.'
     OR address REGEXP '(测试|示例|test|demo|example)')
    AND (is_deleted = 0 OR is_deleted IS NULL)
LIMIT 10;

-- 10. 重置自增ID（可选）
-- 如果需要重置客户表的自增ID，取消下面的注释
-- ALTER TABLE customers AUTO_INCREMENT = 1;
-- ALTER TABLE rental_customers AUTO_INCREMENT = 1;
-- ALTER TABLE rental_records AUTO_INCREMENT = 1;

-- 记录清理完成时间
SELECT 
    '模拟客户数据清理完成' as 操作,
    NOW() as 完成时间;

-- 提交事务
COMMIT;

-- 清理完成后的建议
SELECT 
    '清理完成后的建议' as 类型,
    '1. 验证剩余数据是否为真实客户数据' as 建议1,
    '2. 如有需要，手动删除其他可疑数据' as 建议2,
    '3. 通过管理后台录入真实客户数据' as 建议3,
    '4. 定期运行 verify-data-initialization.sql 验证数据质量' as 建议4;

-- 使用说明
/*
使用方法：
1. 备份数据库：mysqldump -u username -p database_name > backup.sql
2. 执行清理脚本：mysql -u username -p database_name < remove-mock-customer-data.sql
3. 验证清理结果：mysql -u username -p database_name < verify-data-initialization.sql
4. 通过管理后台录入真实客户数据

注意事项：
- 此脚本会永久删除匹配条件的数据，执行前请务必备份
- 清理后的数据无法恢复，请谨慎操作
- 建议在测试环境先验证脚本效果
- 如有特殊数据需要保留，请修改删除条件

脚本特点：
- 使用事务确保数据一致性
- 提供详细的清理日志
- 包含数据验证和检查
- 支持可疑数据识别
- 清理关联表数据
*/