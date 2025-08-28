-- 修复products表中is_deleted字段为NULL的记录
-- 将所有is_deleted为NULL的记录设置为0（未删除状态）

UPDATE products 
SET is_deleted = 0 
WHERE is_deleted IS NULL;

-- 验证修复结果
SELECT 
    COUNT(*) as total_products,
    SUM(CASE WHEN is_deleted = 0 THEN 1 ELSE 0 END) as active_products,
    SUM(CASE WHEN is_deleted = 1 THEN 1 ELSE 0 END) as deleted_products,
    SUM(CASE WHEN is_deleted IS NULL THEN 1 ELSE 0 END) as null_is_deleted
FROM products;