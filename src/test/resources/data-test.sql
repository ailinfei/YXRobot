-- 测试环境初始数据
-- 用于单元测试的基础数据

-- 清理现有测试数据
DELETE FROM customer_service_relation WHERE 1=1;
DELETE FROM customer_order_relation WHERE 1=1;
DELETE FROM customer_device_relation WHERE 1=1;
DELETE FROM service_records WHERE 1=1;
DELETE FROM orders WHERE 1=1;
DELETE FROM devices WHERE 1=1;
DELETE FROM customer_addresses WHERE 1=1;
DELETE FROM customers WHERE 1=1;

-- 重置自增ID
ALTER TABLE customers ALTER COLUMN id RESTART WITH 1;
ALTER TABLE customer_addresses ALTER COLUMN id RESTART WITH 1;
ALTER TABLE devices ALTER COLUMN id RESTART WITH 1;
ALTER TABLE orders ALTER COLUMN id RESTART WITH 1;
ALTER TABLE service_records ALTER COLUMN id RESTART WITH 1;

-- 插入测试客户数据
INSERT INTO customers (
    customer_name, customer_type, customer_level, customer_status,
    contact_person, phone, email, avatar_url, customer_tags, notes,
    address, region, industry, credit_level, total_spent, customer_value,
    registered_at, last_active_at, is_active, is_deleted
) VALUES 
-- VIP客户
('测试VIP客户1', 'ENTERPRISE', 'VIP', 'ACTIVE', '张经理', '13800138001', 'vip1@test.com', '/avatar1.jpg',
 '["VIP", "重要客户"]', 'VIP重要客户', '朝阳区建国门外大街1号', '北京-朝阳区', '科技', 'EXCELLENT', 
 50000.00, 9.5, '2023-01-15 10:00:00', '2024-08-30 15:30:00', TRUE, FALSE),

('测试VIP客户2', 'INDIVIDUAL', 'VIP', 'ACTIVE', '李总', '13800138002', 'vip2@test.com', '/avatar2.jpg',
 '["VIP", "长期合作"]', 'VIP个人客户', '海淀区中关村大街2号', '北京-海淀区', '教育', 'GOOD',
 80000.00, 9.8, '2023-02-20 14:00:00', '2024-08-29 09:15:00', TRUE, FALSE),

-- 高级客户
('测试高级客户1', 'ENTERPRISE', 'PREMIUM', 'ACTIVE', '王总', '13800138003', 'premium1@test.com', '/avatar3.jpg',
 '["高级客户", "制造业"]', '制造业高级客户', '浦东新区陆家嘴5号', '上海-浦东新区', '制造', 'EXCELLENT',
 120000.00, 9.9, '2023-03-10 16:30:00', '2024-08-31 16:00:00', TRUE, FALSE),

-- 普通客户
('测试普通客户1', 'INDIVIDUAL', 'REGULAR', 'ACTIVE', '赵先生', '13800138004', 'regular1@test.com', '/avatar4.jpg',
 '["普通客户"]', '普通个人客户', '西城区西单大街3号', '北京-西城区', '零售', 'FAIR',
 15000.00, 7.2, '2023-04-05 08:20:00', '2024-08-28 11:45:00', TRUE, FALSE),

('测试普通客户2', 'INDIVIDUAL', 'REGULAR', 'INACTIVE', '钱女士', '13800138005', 'regular2@test.com', '/avatar5.jpg',
 '["普通客户", "待激活"]', '待激活客户', '天河区珠江新城7号', '广州-天河区', '服务', 'POOR',
 8000.00, 6.5, '2023-05-12 12:10:00', '2024-07-15 14:20:00', FALSE, FALSE),

-- 上海地区客户
('测试上海客户1', 'ENTERPRISE', 'VIP', 'ACTIVE', '孙总', '13800138006', 'shanghai1@test.com', '/avatar6.jpg',
 '["VIP", "上海客户"]', '上海VIP企业客户', '徐汇区淮海中路6号', '上海-徐汇区', '金融', 'EXCELLENT',
 95000.00, 8.8, '2023-06-18 09:45:00', '2024-08-30 13:25:00', TRUE, FALSE),

-- 广州地区客户
('测试广州客户1', 'INDIVIDUAL', 'REGULAR', 'ACTIVE', '周先生', '13800138007', 'guangzhou1@test.com', '/avatar7.jpg',
 '["普通客户", "广州"]', '广州普通客户', '越秀区中山五路8号', '广州-越秀区', '贸易', 'FAIR',
 25000.00, 7.8, '2023-07-22 15:15:00', '2024-08-27 10:30:00', TRUE, FALSE),

-- 暂停服务客户
('测试暂停客户1', 'ENTERPRISE', 'VIP', 'SUSPENDED', '吴总', '13800138008', 'suspended1@test.com', '/avatar8.jpg',
 '["VIP", "暂停服务"]', 'VIP客户暂停服务', '南山区科技园9号', '深圳-南山区', '科技', 'POOR',
 60000.00, 8.5, '2023-08-30 11:00:00', '2024-06-10 08:45:00', FALSE, FALSE);

-- 插入客户地址数据
INSERT INTO customer_addresses (
    customer_id, address_type, province, city, district, street, postal_code, is_default, is_deleted
) VALUES 
(1, 'OFFICE', '北京市', '朝阳区', '建国门外', '建国门外大街1号', '100020', TRUE, FALSE),
(2, 'HOME', '北京市', '海淀区', '中关村', '中关村大街2号', '100080', TRUE, FALSE),
(3, 'OFFICE', '上海市', '浦东新区', '陆家嘴', '陆家嘴金融区5号', '200120', TRUE, FALSE),
(4, 'HOME', '北京市', '西城区', '西单', '西单大街3号', '100032', TRUE, FALSE),
(5, 'HOME', '广东省', '广州市', '天河区', '珠江新城7号', '510623', TRUE, FALSE);

-- 插入设备数据
INSERT INTO devices (
    device_name, device_type, device_model, device_status, purchase_date, warranty_period, is_deleted
) VALUES 
('清洁机器人A1', 'CLEANING_ROBOT', 'Model-A1', 'ACTIVE', '2023-01-20', 24, FALSE),
('服务机器人B1', 'SERVICE_ROBOT', 'Model-B1', 'ACTIVE', '2023-02-25', 36, FALSE),
('清洁机器人C1', 'CLEANING_ROBOT', 'Model-C1', 'MAINTENANCE', '2023-03-15', 24, FALSE),
('安防机器人D1', 'SECURITY_ROBOT', 'Model-D1', 'ACTIVE', '2023-04-10', 48, FALSE),
('配送机器人E1', 'DELIVERY_ROBOT', 'Model-E1', 'ACTIVE', '2023-05-20', 24, FALSE);

-- 插入客户设备关联数据
INSERT INTO customer_device_relation (
    customer_id, device_id, relation_type, start_date, status
) VALUES 
(1, 1, 'PURCHASED', '2023-01-20', 1),
(2, 2, 'RENTAL', '2023-02-25', 1),
(3, 3, 'PURCHASED', '2023-03-15', 1),
(3, 4, 'PURCHASED', '2023-04-10', 1),
(6, 5, 'RENTAL', '2023-05-20', 1);

-- 插入订单数据
INSERT INTO orders (
    order_number, order_status, total_amount, order_date, is_deleted
) VALUES 
('ORD-2023-001', 'COMPLETED', 50000.00, '2023-01-20 10:00:00', FALSE),
('ORD-2023-002', 'COMPLETED', 80000.00, '2023-02-25 14:00:00', FALSE),
('ORD-2023-003', 'COMPLETED', 120000.00, '2023-03-15 16:00:00', FALSE),
('ORD-2023-004', 'PENDING', 15000.00, '2023-04-05 08:00:00', FALSE),
('ORD-2023-005', 'CANCELLED', 8000.00, '2023-05-12 12:00:00', FALSE);

-- 插入客户订单关联数据
INSERT INTO customer_order_relation (customer_id, order_id) VALUES 
(1, 1),
(2, 2),
(3, 3),
(4, 4),
(5, 5);

-- 插入服务记录数据
INSERT INTO service_records (
    service_type, service_description, status, service_date, is_deleted
) VALUES 
('MAINTENANCE', '设备定期维护', 'COMPLETED', '2023-06-01 09:00:00', FALSE),
('REPAIR', '设备故障维修', 'COMPLETED', '2023-06-15 14:00:00', FALSE),
('UPGRADE', '设备软件升级', 'IN_PROGRESS', '2023-07-01 10:00:00', FALSE),
('TRAINING', '客户培训服务', 'COMPLETED', '2023-07-15 15:00:00', FALSE),
('CONSULTATION', '技术咨询服务', 'PENDING', '2023-08-01 11:00:00', FALSE);

-- 插入客户服务关联数据
INSERT INTO customer_service_relation (customer_id, service_record_id) VALUES 
(1, 1),
(2, 2),
(3, 3),
(4, 4),
(5, 5);

-- 验证数据插入
SELECT 'Test data initialization completed' as status;
SELECT COUNT(*) as customer_count FROM customers WHERE is_deleted = FALSE;
SELECT COUNT(*) as device_count FROM devices WHERE is_deleted = FALSE;
SELECT COUNT(*) as order_count FROM orders WHERE is_deleted = FALSE;