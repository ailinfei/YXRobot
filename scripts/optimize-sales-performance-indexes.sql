-- 销售数据管理模块性能优化索引脚本
-- 任务11：性能优化和错误处理 - 确保系统稳定性
-- 
-- 功能：
-- 1. 优化数据库查询性能，添加必要的索引
-- 2. 提高统计和图表数据查询性能
-- 3. 优化搜索和筛选功能的查询速度
-- 4. 支持大数据量的高效查询

-- ==================== 销售记录表索引优化 ====================

-- 1. 复合索引：日期范围查询优化（最常用的查询条件）
CREATE INDEX IF NOT EXISTS idx_sales_records_date_status 
ON sales_records (order_date DESC, status, is_deleted);

-- 2. 复合索引：客户相关查询优化
CREATE INDEX IF NOT EXISTS idx_sales_records_customer_date 
ON sales_records (customer_id, order_date DESC, is_deleted);

-- 3. 复合索引：产品相关查询优化
CREATE INDEX IF NOT EXISTS idx_sales_records_product_date 
ON sales_records (product_id, order_date DESC, is_deleted);

-- 4. 复合索引：销售人员相关查询优化
CREATE INDEX IF NOT EXISTS idx_sales_records_staff_date 
ON sales_records (sales_staff_id, order_date DESC, is_deleted);

-- 5. 复合索引：地区和渠道查询优化
CREATE INDEX IF NOT EXISTS idx_sales_records_region_channel 
ON sales_records (region, channel, order_date DESC, is_deleted);

-- 6. 复合索引：金额范围查询优化
CREATE INDEX IF NOT EXISTS idx_sales_records_amount_date 
ON sales_records (sales_amount DESC, order_date DESC, is_deleted);

-- 7. 复合索引：状态和付款状态查询优化
CREATE INDEX IF NOT EXISTS idx_sales_records_status_payment 
ON sales_records (status, payment_status, order_date DESC, is_deleted);

-- 8. 全文搜索索引：订单号搜索优化
CREATE INDEX IF NOT EXISTS idx_sales_records_order_number_search 
ON sales_records (order_number, is_deleted);

-- 9. 复合索引：创建时间和更新时间查询优化
CREATE INDEX IF NOT EXISTS idx_sales_records_timestamps 
ON sales_records (created_at DESC, updated_at DESC, is_deleted);

-- 10. 复合索引：分页查询优化（支持大数据量分页）
CREATE INDEX IF NOT EXISTS idx_sales_records_pagination 
ON sales_records (id DESC, order_date DESC, is_deleted);

-- ==================== 客户表索引优化 ====================

-- 1. 复合索引：客户搜索优化
CREATE INDEX IF NOT EXISTS idx_customers_search 
ON customers (customer_name, customer_type, is_active, is_deleted);

-- 2. 复合索引：地区和行业查询优化
CREATE INDEX IF NOT EXISTS idx_customers_region_industry 
ON customers (region, industry, is_active, is_deleted);

-- 3. 复合索引：信用等级查询优化
CREATE INDEX IF NOT EXISTS idx_customers_credit_active 
ON customers (credit_level, is_active, is_deleted);

-- 4. 电话号码搜索索引
CREATE INDEX IF NOT EXISTS idx_customers_phone 
ON customers (phone, is_deleted);

-- ==================== 产品表索引优化 ====================

-- 1. 复合索引：产品搜索优化
CREATE INDEX IF NOT EXISTS idx_products_search 
ON products (product_name, category, brand, is_active, is_deleted);

-- 2. 复合索引：产品分类查询优化
CREATE INDEX IF NOT EXISTS idx_products_category_brand 
ON products (category, brand, is_active, is_deleted);

-- 3. 复合索引：价格范围查询优化
CREATE INDEX IF NOT EXISTS idx_products_price_range 
ON products (unit_price DESC, cost_price DESC, is_active, is_deleted);

-- 4. 产品编码搜索索引
CREATE INDEX IF NOT EXISTS idx_products_code_search 
ON products (product_code, is_active, is_deleted);

-- ==================== 销售人员表索引优化 ====================

-- 1. 复合索引：销售人员搜索优化
CREATE INDEX IF NOT EXISTS idx_sales_staff_search 
ON sales_staff (staff_name, department, position, is_active, is_deleted);

-- 2. 复合索引：部门和职位查询优化
CREATE INDEX IF NOT EXISTS idx_sales_staff_dept_position 
ON sales_staff (department, position, is_active, is_deleted);

-- 3. 复合索引：销售目标查询优化
CREATE INDEX IF NOT EXISTS idx_sales_staff_target 
ON sales_staff (sales_target DESC, commission_rate DESC, is_active, is_deleted);

-- 4. 员工编号搜索索引
CREATE INDEX IF NOT EXISTS idx_sales_staff_code 
ON sales_staff (staff_code, is_active, is_deleted);

-- ==================== 销售统计表索引优化 ====================

-- 1. 复合索引：统计数据查询优化
CREATE INDEX IF NOT EXISTS idx_sales_stats_date_type 
ON sales_stats (stat_date DESC, stat_type);

-- 2. 复合索引：统计类型和日期范围查询
CREATE INDEX IF NOT EXISTS idx_sales_stats_type_date_range 
ON sales_stats (stat_type, stat_date DESC);

-- 3. 复合索引：销售金额统计查询优化
CREATE INDEX IF NOT EXISTS idx_sales_stats_amount 
ON sales_stats (total_sales_amount DESC, stat_date DESC, stat_type);

-- ==================== 性能监控和分析 ====================

-- 查看索引使用情况的查询语句
-- 注意：这些查询用于性能监控，不会自动执行

-- 1. 查看表的索引信息
-- SHOW INDEX FROM sales_records;
-- SHOW INDEX FROM customers;
-- SHOW INDEX FROM products;
-- SHOW INDEX FROM sales_staff;
-- SHOW INDEX FROM sales_stats;

-- 2. 分析查询执行计划（示例）
-- EXPLAIN SELECT * FROM sales_records 
-- WHERE order_date BETWEEN '2024-01-01' AND '2024-12-31' 
-- AND status = 'confirmed' 
-- AND is_deleted = 0 
-- ORDER BY order_date DESC 
-- LIMIT 20;

-- 3. 查看索引大小和使用统计
-- SELECT 
--     TABLE_NAME,
--     INDEX_NAME,
--     CARDINALITY,
--     INDEX_LENGTH
-- FROM information_schema.STATISTICS 
-- WHERE TABLE_SCHEMA = DATABASE() 
-- AND TABLE_NAME IN ('sales_records', 'customers', 'products', 'sales_staff', 'sales_stats')
-- ORDER BY TABLE_NAME, INDEX_NAME;

-- ==================== 索引维护建议 ====================

-- 1. 定期分析表统计信息
-- ANALYZE TABLE sales_records, customers, products, sales_staff, sales_stats;

-- 2. 定期优化表结构
-- OPTIMIZE TABLE sales_records, customers, products, sales_staff, sales_stats;

-- 3. 监控慢查询日志
-- 建议在MySQL配置中启用慢查询日志：
-- slow_query_log = 1
-- slow_query_log_file = /var/log/mysql/slow.log
-- long_query_time = 2

-- ==================== 查询优化建议 ====================

-- 1. 使用覆盖索引减少回表查询
-- 示例：查询销售记录列表时，尽量只查询需要的字段

-- 2. 避免在WHERE子句中使用函数
-- 错误示例：WHERE DATE(order_date) = '2024-01-01'
-- 正确示例：WHERE order_date >= '2024-01-01' AND order_date < '2024-01-02'

-- 3. 合理使用LIMIT进行分页
-- 对于大偏移量的分页查询，考虑使用游标分页：
-- WHERE id > last_id ORDER BY id LIMIT 20

-- 4. 统计查询优化
-- 对于复杂的统计查询，考虑使用预计算的统计表

-- ==================== 缓存策略建议 ====================

-- 1. 应用层缓存
-- - 统计数据缓存（5-15分钟）
-- - 图表数据缓存（10-30分钟）
-- - 下拉选项数据缓存（1-6小时）

-- 2. 数据库查询缓存
-- - 启用MySQL查询缓存
-- - 合理设置缓存大小和过期时间

-- 3. Redis缓存
-- - 热点数据缓存
-- - 会话数据缓存
-- - 计算结果缓存

-- ==================== 执行完成标记 ====================

-- 记录索引优化执行时间
INSERT INTO system_logs (log_type, log_message, created_at) 
VALUES ('PERFORMANCE_OPTIMIZATION', 'Sales module database indexes optimized', NOW())
ON DUPLICATE KEY UPDATE 
log_message = 'Sales module database indexes re-optimized', 
updated_at = NOW();

-- 输出优化完成信息
SELECT 
    'Sales Performance Optimization Completed' as status,
    NOW() as completion_time,
    'Database indexes have been optimized for better query performance' as message;