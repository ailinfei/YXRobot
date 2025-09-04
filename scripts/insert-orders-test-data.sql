-- =====================================================
-- 订单管理模块 - 插入基础测试数据
-- 创建时间: 2025-01-28
-- 说明: 为订单管理模块插入基础测试数据
-- =====================================================

-- 使用YXRobot数据库
USE YXRobot;

-- =====================================================
-- 1. 插入测试订单数据
-- =====================================================

-- 插入销售订单测试数据
INSERT INTO `orders` (
    `order_number`, `type`, `status`, `customer_id`, `delivery_address`, 
    `subtotal`, `shipping_fee`, `discount`, `total_amount`, `currency`,
    `payment_status`, `payment_method`, `expected_delivery_date`, 
    `sales_person`, `notes`, `created_by`
) VALUES 
(
    'ORD202501280001', 'sales', 'pending', 1, '北京市朝阳区建国路88号SOHO现代城',
    9800.00, 200.00, 0.00, 10000.00, 'CNY',
    'pending', '支付宝', '2025-02-05', '张三', '客户要求加急处理', 'admin'
),
(
    'ORD202501280002', 'sales', 'confirmed', 2, '上海市浦东新区陆家嘴金融中心',
    15600.00, 300.00, 500.00, 15400.00, 'CNY',
    'paid', '微信支付', '2025-02-10', '李四', '大客户订单，优先处理', 'admin'
),
(
    'ORD202501280003', 'rental', 'processing', 3, '广州市天河区珠江新城CBD',
    8000.00, 0.00, 200.00, 7800.00, 'CNY',
    'paid', '银行转账', '2025-02-01', '王五', '租赁期3个月', 'admin'
);

-- =====================================================
-- 2. 插入订单商品明细数据
-- =====================================================

-- 为第一个订单插入商品明细
INSERT INTO `order_items` (
    `order_id`, `product_id`, `product_name`, `product_model`, `quantity`, `unit_price`, `total_price`
) VALUES 
(1, 1, 'YX-Robot-Pro智能机器人', 'YX-PRO-2024', 2, 4900.00, 9800.00),
(2, 2, 'YX-Robot-Enterprise企业版机器人', 'YX-ENT-2024', 1, 15600.00, 15600.00),
(3, 3, 'YX-Robot-Rental租赁版机器人', 'YX-RENTAL-2024', 1, 8000.00, 8000.00);

-- =====================================================
-- 3. 插入物流信息数据
-- =====================================================

-- 为已确认的订单插入物流信息
INSERT INTO `shipping_info` (
    `order_id`, `company`, `tracking_number`, `shipped_at`, `notes`
) VALUES 
(2, '顺丰速运', 'SF1234567890', '2025-01-28 10:30:00', '已发货，预计2天内到达'),
(3, '德邦物流', 'DB0987654321', '2025-01-28 14:20:00', '大件物流，需要预约送货');

-- =====================================================
-- 4. 插入订单操作日志数据
-- =====================================================

-- 插入订单操作日志
INSERT INTO `order_logs` (
    `order_id`, `action`, `operator`, `notes`
) VALUES 
(1, '创建订单', 'admin', '客户通过在线系统下单'),
(2, '创建订单', 'admin', '销售人员代客户下单'),
(2, '确认订单', '李四', '客户确认订单信息无误'),
(2, '支付完成', 'system', '客户通过微信支付完成付款'),
(2, '安排发货', '李四', '订单已安排发货'),
(3, '创建订单', 'admin', '租赁订单创建'),
(3, '支付完成', 'system', '客户通过银行转账完成付款'),
(3, '开始处理', '王五', '租赁订单开始处理');

-- =====================================================
-- 5. 验证插入的数据
-- =====================================================

-- 查看订单数据
SELECT '=== 订单数据 ===' as info;
SELECT id, order_number, type, status, customer_id, total_amount, payment_status, created_at 
FROM orders 
ORDER BY created_at DESC;

-- 查看订单商品数据
SELECT '=== 订单商品数据 ===' as info;
SELECT oi.id, oi.order_id, o.order_number, oi.product_name, oi.quantity, oi.unit_price, oi.total_price
FROM order_items oi
JOIN orders o ON oi.order_id = o.id
ORDER BY oi.order_id;

-- 查看物流信息数据
SELECT '=== 物流信息数据 ===' as info;
SELECT si.id, si.order_id, o.order_number, si.company, si.tracking_number, si.shipped_at
FROM shipping_info si
JOIN orders o ON si.order_id = o.id
ORDER BY si.order_id;

-- 查看操作日志数据
SELECT '=== 操作日志数据 ===' as info;
SELECT ol.id, ol.order_id, o.order_number, ol.action, ol.operator, ol.created_at
FROM order_logs ol
JOIN orders o ON ol.order_id = o.id
ORDER BY ol.order_id, ol.created_at;

-- =====================================================
-- 脚本执行完成
-- =====================================================
SELECT '订单管理模块测试数据插入完成！' as result;