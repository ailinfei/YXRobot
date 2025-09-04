-- 订单搜索性能优化索引脚本
-- 用于提高订单搜索和筛选功能的性能

-- 使用数据库
USE YXRobot;

-- ==================== 基础索引 ====================

-- 1. 订单表基础索引
-- 订单号索引（唯一索引，用于精确搜索）
CREATE UNIQUE INDEX idx_orders_order_number ON orders(order_number);

-- 创建时间索引（用于排序和日期筛选）
CREATE INDEX idx_orders_created_at ON orders(created_at DESC);

-- 更新时间索引
CREATE INDEX idx_orders_updated_at ON orders(updated_at DESC);

-- 订单状态索引（用于状态筛选）
CREATE INDEX idx_orders_status ON orders(status);

-- 订单类型索引（用于类型筛选）
CREATE INDEX idx_orders_type ON orders(type);

-- 支付状态索引
CREATE INDEX idx_orders_payment_status ON orders(payment_status);

-- 客户ID索引（用于客户筛选）
CREATE INDEX idx_orders_customer_id ON orders(customer_id);

-- 销售人员索引
CREATE INDEX idx_orders_sales_person ON orders(sales_person);

-- 软删除标记索引
CREATE INDEX idx_orders_is_deleted ON orders(is_deleted);

-- 订单金额索引（用于金额范围筛选）
CREATE INDEX idx_orders_total_amount ON orders(total_amount);

-- 预期交付日期索引
CREATE INDEX idx_orders_expected_delivery_date ON orders(expected_delivery_date);

-- ==================== 复合索引 ====================

-- 2. 常用组合查询索引
-- 状态 + 创建时间（最常用的组合）
CREATE INDEX idx_orders_status_created_at ON orders(status, created_at DESC);

-- 类型 + 状态（用于按类型和状态筛选）
CREATE INDEX idx_orders_type_status ON orders(type, status);

-- 客户 + 状态（用于查看客户的订单状态）
CREATE INDEX idx_orders_customer_status ON orders(customer_id, status);

-- 软删除 + 状态 + 创建时间（用于列表查询）
CREATE INDEX idx_orders_deleted_status_created ON orders(is_deleted, status, created_at DESC);

-- 软删除 + 类型 + 状态（用于分类筛选）
CREATE INDEX idx_orders_deleted_type_status ON orders(is_deleted, type, status);

-- 日期范围 + 状态（用于日期筛选）
CREATE INDEX idx_orders_created_status ON orders(DATE(created_at), status);

-- 销售人员 + 状态 + 创建时间（用于销售人员查看订单）
CREATE INDEX idx_orders_sales_status_created ON orders(sales_person, status, created_at DESC);

-- 支付状态 + 订单状态（用于财务查询）
CREATE INDEX idx_orders_payment_order_status ON orders(payment_status, status);

-- ==================== 全文搜索索引 ====================

-- 3. 全文搜索索引（用于关键词搜索）
-- 订单号和备注的全文搜索
CREATE FULLTEXT INDEX idx_orders_fulltext_search ON orders(order_number, notes);

-- 如果MySQL版本支持，可以添加更多字段的全文搜索
-- CREATE FULLTEXT INDEX idx_orders_fulltext_extended ON orders(order_number, notes, delivery_address);

-- ==================== 关联表索引 ====================

-- 4. 订单商品表索引
-- 订单ID索引（用于查询订单的商品明细）
CREATE INDEX idx_order_items_order_id ON order_items(order_id);

-- 产品ID索引（用于查询产品的订单记录）
CREATE INDEX idx_order_items_product_id ON order_items(product_id);

-- 复合索引：订单ID + 产品ID
CREATE INDEX idx_order_items_order_product ON order_items(order_id, product_id);

-- 5. 物流信息表索引
-- 订单ID索引
CREATE INDEX idx_shipping_info_order_id ON shipping_info(order_id);

-- 物流公司索引
CREATE INDEX idx_shipping_info_company ON shipping_info(company);

-- 快递单号索引
CREATE INDEX idx_shipping_info_tracking_number ON shipping_info(tracking_number);

-- 发货时间索引
CREATE INDEX idx_shipping_info_shipped_at ON shipping_info(shipped_at);

-- 6. 订单日志表索引
-- 订单ID索引
CREATE INDEX idx_order_logs_order_id ON order_logs(order_id);

-- 操作时间索引
CREATE INDEX idx_order_logs_created_at ON order_logs(created_at DESC);

-- 操作人索引
CREATE INDEX idx_order_logs_operator ON order_logs(operator);

-- 操作类型索引
CREATE INDEX idx_order_logs_action ON order_logs(action);

-- 复合索引：订单ID + 操作时间
CREATE INDEX idx_order_logs_order_created ON order_logs(order_id, created_at DESC);

-- ==================== 客户表相关索引 ====================

-- 7. 客户表索引（用于订单搜索中的客户信息关联）
-- 客户姓名索引（用于按客户姓名搜索订单）
CREATE INDEX idx_customers_name ON customers(name);

-- 客户电话索引
CREATE INDEX idx_customers_phone ON customers(phone);

-- 客户邮箱索引
CREATE INDEX idx_customers_email ON customers(email);

-- 客户公司索引
CREATE INDEX idx_customers_company ON customers(company);

-- ==================== 产品表相关索引 ====================

-- 8. 产品表索引（用于订单商品信息关联）
-- 产品名称索引
CREATE INDEX idx_products_name ON products(name);

-- 产品型号索引
CREATE INDEX idx_products_model ON products(model);

-- 产品状态索引
CREATE INDEX idx_products_status ON products(status);

-- ==================== 统计查询优化索引 ====================

-- 9. 统计查询专用索引
-- 按日期统计的索引
CREATE INDEX idx_orders_date_type_status ON orders(DATE(created_at), type, status);

-- 按月份统计的索引
CREATE INDEX idx_orders_month_type_status ON orders(YEAR(created_at), MONTH(created_at), type, status);

-- 收入统计索引（已完成订单的金额）
CREATE INDEX idx_orders_completed_amount ON orders(status, total_amount) WHERE status = 'completed';

-- ==================== 性能监控 ====================

-- 10. 查看索引使用情况的查询
-- 可以用来监控索引的使用效果

-- 查看表的索引信息
-- SHOW INDEX FROM orders;

-- 查看索引使用统计
-- SELECT * FROM information_schema.statistics WHERE table_schema = 'YXRobot' AND table_name = 'orders';

-- ==================== 索引维护建议 ====================

-- 定期维护索引的建议：
-- 1. 定期分析表和索引的使用情况
-- ANALYZE TABLE orders;

-- 2. 定期优化表
-- OPTIMIZE TABLE orders;

-- 3. 监控慢查询日志，识别需要优化的查询

-- 4. 根据实际查询模式调整索引策略

-- ==================== 注意事项 ====================

-- 索引创建注意事项：
-- 1. 索引会占用额外的存储空间
-- 2. 索引会影响INSERT、UPDATE、DELETE的性能
-- 3. 应该根据实际的查询模式创建索引
-- 4. 定期监控和维护索引的使用效果
-- 5. 避免创建过多的重复或无用索引

-- 查询优化建议：
-- 1. 在WHERE子句中使用索引字段
-- 2. 避免在索引字段上使用函数
-- 3. 使用LIMIT限制返回结果数量
-- 4. 合理使用ORDER BY和GROUP BY
-- 5. 考虑使用分区表处理大数据量

-- ==================== 验证索引效果 ====================

-- 可以使用EXPLAIN分析查询计划，验证索引是否被正确使用
-- 示例：
-- EXPLAIN SELECT * FROM orders WHERE status = 'pending' ORDER BY created_at DESC LIMIT 10;
-- EXPLAIN SELECT * FROM orders WHERE order_number = 'ORD1234567890';
-- EXPLAIN SELECT * FROM orders WHERE customer_id = 1 AND status = 'completed';

-- 完成索引创建
SELECT 'Search optimization indexes created successfully!' as message;