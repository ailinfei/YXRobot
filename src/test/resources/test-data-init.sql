-- 租赁模块测试数据初始化脚本
-- 用于单元测试和集成测试

-- 清理现有测试数据
DELETE FROM rental_records WHERE rental_order_number LIKE 'TEST%';
DELETE FROM rental_devices WHERE device_id LIKE 'TEST%';
DELETE FROM rental_customers WHERE customer_name LIKE '测试%';

-- 插入测试客户数据
INSERT INTO rental_customers (
    customer_name, customer_type, phone, email, address, region, industry, credit_level,
    created_at, updated_at, is_deleted
) VALUES 
('测试客户001', 'enterprise', '13800138001', 'test001@example.com', '北京市朝阳区测试街道001号', '北京', '科技', 'A', NOW(), NOW(), 0),
('测试客户002', 'individual', '13800138002', 'test002@example.com', '上海市浦东新区测试街道002号', '上海', '制造业', 'B', NOW(), NOW(), 0),
('测试客户003', 'institution', '13800138003', 'test003@example.com', '广州市天河区测试街道003号', '广州', '教育', 'A', NOW(), NOW(), 0),
('测试客户004', 'enterprise', '13800138004', 'test004@example.com', '深圳市南山区测试街道004号', '深圳', '金融', 'C', NOW(), NOW(), 0),
('测试客户005', 'individual', '13800138005', 'test005@example.com', '杭州市西湖区测试街道005号', '杭州', '电商', 'B', NOW(), NOW(), 0);

-- 插入测试设备数据
INSERT INTO rental_devices (
    device_id, device_model, device_name, daily_rental_price, current_status, 
    utilization_rate, total_rental_days, last_rental_date, region, maintenance_status,
    performance_score, signal_strength, created_at, updated_at, is_deleted
) VALUES 
('TEST-YX-001', 'YX-Robot-Pro', '测试智能机器人Pro-001', 120.00, 'active', 85.50, 150, '2025-01-28', '北京', 'normal', 90, 95, NOW(), NOW(), 0),
('TEST-YX-002', 'YX-Robot-Standard', '测试智能机器人Standard-002', 100.00, 'idle', 72.30, 120, '2025-01-25', '上海', 'normal', 85, 88, NOW(), NOW(), 0),
('TEST-YX-003', 'YX-Robot-Lite', '测试智能机器人Lite-003', 80.00, 'maintenance', 65.80, 95, '2025-01-20', '广州', 'warning', 80, 82, NOW(), NOW(), 0),
('TEST-YX-004', 'YX-Robot-Pro', '测试智能机器人Pro-004', 120.00, 'active', 88.20, 180, '2025-01-27', '深圳', 'normal', 92, 96, NOW(), NOW(), 0),
('TEST-YX-005', 'YX-Robot-Standard', '测试智能机器人Standard-005', 100.00, 'idle', 70.50, 110, '2025-01-22', '杭州', 'normal', 83, 85, NOW(), NOW(), 0),
('TEST-YX-006', 'YX-Robot-Mini', '测试智能机器人Mini-006', 60.00, 'active', 75.60, 80, '2025-01-26', '北京', 'normal', 78, 80, NOW(), NOW(), 0),
('TEST-YX-007', 'YX-Robot-Pro', '测试智能机器人Pro-007', 120.00, 'retired', 45.30, 200, '2025-01-15', '上海', 'urgent', 65, 70, NOW(), NOW(), 0),
('TEST-YX-008', 'YX-Robot-Standard', '测试智能机器人Standard-008', 100.00, 'active', 82.40, 140, '2025-01-28', '广州', 'normal', 87, 90, NOW(), NOW(), 0);

-- 插入测试租赁记录数据
INSERT INTO rental_records (
    rental_order_number, device_id, customer_id, rental_start_date, rental_end_date, 
    rental_period, daily_rental_fee, total_rental_fee, deposit_amount, actual_payment,
    rental_status, payment_status, created_at, updated_at, is_deleted
) VALUES 
('TEST20250128001', 1, 1, '2025-01-28', '2025-02-12', 15, 120.00, 1800.00, 600.00, 2400.00, 'active', 'paid', NOW(), NOW(), 0),
('TEST20250127001', 2, 2, '2025-01-27', '2025-02-06', 10, 100.00, 1000.00, 500.00, 1500.00, 'active', 'paid', NOW(), NOW(), 0),
('TEST20250126001', 3, 3, '2025-01-26', '2025-02-10', 15, 80.00, 1200.00, 400.00, 1600.00, 'completed', 'paid', NOW(), NOW(), 0),
('TEST20250125001', 4, 4, '2025-01-25', '2025-02-09', 15, 120.00, 1800.00, 600.00, 2400.00, 'active', 'paid', NOW(), NOW(), 0),
('TEST20250124001', 5, 5, '2025-01-24', '2025-02-03', 10, 100.00, 1000.00, 500.00, 1500.00, 'completed', 'paid', NOW(), NOW(), 0),
('TEST20250123001', 6, 1, '2025-01-23', '2025-02-07', 15, 60.00, 900.00, 300.00, 1200.00, 'active', 'partial', NOW(), NOW(), 0),
('TEST20250122001', 7, 2, '2025-01-22', '2025-01-27', 5, 120.00, 600.00, 600.00, 1200.00, 'completed', 'paid', NOW(), NOW(), 0),
('TEST20250121001', 8, 3, '2025-01-21', '2025-02-05', 15, 100.00, 1500.00, 500.00, 2000.00, 'active', 'paid', NOW(), NOW(), 0);

-- 插入测试统计数据（用于验证统计功能）
-- 这些数据会在测试运行时动态计算，这里只是为了确保表结构正确

-- 提交事务
COMMIT;

-- 验证测试数据
SELECT '测试客户数据' as data_type, COUNT(*) as count FROM rental_customers WHERE customer_name LIKE '测试%'
UNION ALL
SELECT '测试设备数据' as data_type, COUNT(*) as count FROM rental_devices WHERE device_id LIKE 'TEST%'
UNION ALL
SELECT '测试租赁记录' as data_type, COUNT(*) as count FROM rental_records WHERE rental_order_number LIKE 'TEST%';

-- 显示测试数据概览
SELECT 
    '数据概览' as info,
    (SELECT COUNT(*) FROM rental_customers WHERE customer_name LIKE '测试%') as test_customers,
    (SELECT COUNT(*) FROM rental_devices WHERE device_id LIKE 'TEST%') as test_devices,
    (SELECT COUNT(*) FROM rental_records WHERE rental_order_number LIKE 'TEST%') as test_records,
    (SELECT SUM(total_rental_fee) FROM rental_records WHERE rental_order_number LIKE 'TEST%') as total_test_revenue;