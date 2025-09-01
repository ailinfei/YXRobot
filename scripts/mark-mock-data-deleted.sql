-- 安全的模拟数据清理脚本 - 使用软删除
-- 将示例数据标记为已删除，而不是物理删除

START TRANSACTION;

-- 标记重复客户为已删除（保留ID最小的）
UPDATE customers c1
INNER JOIN customers c2 
SET c1.is_deleted = 1, c1.updated_at = NOW()
WHERE c1.id > c2.id 
AND c1.customer_name = c2.customer_name 
AND c1.phone = c2.phone
AND (c1.is_deleted = 0 OR c1.is_deleted IS NULL);

-- 标记明显的测试数据为已删除
UPDATE customers 
SET is_deleted = 1, updated_at = NOW()
WHERE (phone LIKE '138-0000-%' 
   OR phone LIKE '139-0000-%'
   OR email LIKE '%@email.com'
   OR customer_name LIKE '%个人用户-%')
AND (is_deleted = 0 OR is_deleted IS NULL);

-- 检查剩余有效数据
SELECT COUNT(*) as remaining_active_customers FROM customers WHERE is_deleted = 0 OR is_deleted IS NULL;

-- 检查被标记删除的数据
SELECT COUNT(*) as marked_deleted_customers FROM customers WHERE is_deleted = 1;

COMMIT;