-- 简化版模拟数据清理脚本
-- 清理customers表中的重复和示例数据

START TRANSACTION;

-- 删除重复的客户记录（保留ID最小的）
DELETE c1 FROM customers c1
INNER JOIN customers c2 
WHERE c1.id > c2.id 
AND c1.customer_name = c2.customer_name 
AND c1.phone = c2.phone;

-- 删除明显的测试数据
DELETE FROM customers 
WHERE phone LIKE '138-0000-%' 
   OR phone LIKE '139-0000-%'
   OR email LIKE '%@email.com'
   OR customer_name LIKE '%个人用户-%';

-- 检查剩余数据
SELECT COUNT(*) as remaining_customers FROM customers WHERE is_deleted = 0 OR is_deleted IS NULL;

COMMIT;