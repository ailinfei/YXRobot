-- =====================================================
-- 订单管理模块 - 数据库索引优化
-- 创建时间: 2025-01-28
-- 说明: 为订单管理模块添加性能优化索引
-- =====================================================

-- 使用YXRobot数据库
USE YXRobot;

-- =====================================================
-- 1. 订单表(orders)索引优化
-- =====================================================

-- 复合索引：支持按状态和创建时间查询（最常用的查询场景）
CREATE INDEX idx_orders_status_created_at ON orders (status, created_at DESC, is_deleted);

-- 复合索引：支持按类型和状态查询
CREATE INDEX idx_orders_type_status ON orders (type, status, is_deleted);

-- 复合索引：支持按客户查询订单
CREATE INDEX idx_orders_customer_created_at ON orders (customer_id, created_at DESC, is_deleted);

-- 复合索引：支持按支付状态查询
CREATE INDEX idx_orders_payment_status_created_at ON orders (payment_status, created_at DESC, is_deleted);

-- 复合索引：支持分页查询优化
CREATE INDEX idx_orders_pagination ON orders (id DESC, created_at DESC, is_deleted);

-- 复合索引：支持按销售人员查询
CREATE INDEX idx_orders_sales_person ON orders (sales_person, created_at DESC, is_deleted);

-- =====================================================
-- 2. 订单商品表(order_items)索引优化
-- =====================================================

-- 复合索引：支持按订单查询商品明细
CREATE INDEX idx_order_items_order_product ON order_items (order_id, product_id);

-- 单独索引：支持按产品查询相关订单
CREATE INDEX idx_order_items_product_id ON order_items (product_id);

-- =====================================================
-- 3. 物流信息表(shipping_info)索引优化
-- =====================================================

-- 单独索引：支持按运单号查询
CREATE INDEX idx_shipping_info_tracking ON shipping_info (tracking_number);

-- 复合索引：支持按物流公司和发货时间查询
CREATE INDEX idx_shipping_info_company_shipped ON shipping_info (company, shipped_at);

-- =====================================================
-- 4. 订单日志表(order_logs)索引优化
-- =====================================================

-- 复合索引：支持按订单查询操作日志
CREATE INDEX idx_order_logs_order_created ON order_logs (order_id, created_at DESC);

-- 复合索引：支持按操作人查询日志
CREATE INDEX idx_order_logs_operator_created ON order_logs (operator, created_at DESC);

-- 复合索引：支持按操作类型查询日志
CREATE INDEX idx_order_logs_action_created ON order_logs (action, created_at DESC);

-- =====================================================
-- 5. 显示所有索引信息
-- =====================================================

-- 显示orders表的索引
SELECT '=== orders表索引 ===' as info;
SHOW INDEX FROM orders;

-- 显示order_items表的索引
SELECT '=== order_items表索引 ===' as info;
SHOW INDEX FROM order_items;

-- 显示shipping_info表的索引
SELECT '=== shipping_info表索引 ===' as info;
SHOW INDEX FROM shipping_info;

-- 显示order_logs表的索引
SELECT '=== order_logs表索引 ===' as info;
SHOW INDEX FROM order_logs;

-- =====================================================
-- 脚本执行完成
-- =====================================================
SELECT '订单管理模块索引优化完成！' as result;