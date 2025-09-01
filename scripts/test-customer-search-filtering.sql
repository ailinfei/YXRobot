-- 客户搜索和筛选功能测试脚本
-- 用于验证搜索和筛选功能的正确性和性能

-- ==================== 测试数据准备 ====================

-- 清理测试数据（如果存在）
DELETE FROM customers WHERE customer_name LIKE 'TEST_%';

-- 插入测试客户数据
INSERT INTO customers (
    customer_name, customer_type, customer_level, customer_status,
    contact_person, phone, email, avatar_url, customer_tags, notes,
    address, region, industry, credit_level, total_spent, customer_value,
    registered_at, last_active_at, is_active, created_at, updated_at, is_deleted
) VALUES 
-- VIP客户测试数据
('TEST_张三', 'ENTERPRISE', 'vip', 'active', '张三', '13800138001', 'zhangsan@test.com', '/avatar1.jpg', 
 '["VIP", "重要客户"]', '重要VIP客户', '朝阳区建国门外大街1号', '北京-朝阳区', '科技', 'excellent', 
 50000.00, 9.5, '2023-01-15 10:00:00', '2024-08-30 15:30:00', 1, NOW(), NOW(), 0),

('TEST_李四', 'INDIVIDUAL', 'premium', 'active', '李四', '13800138002', 'lisi@test.com', '/avatar2.jpg',
 '["高端客户", "长期合作"]', '高端个人客户', '海淀区中关村大街2号', '北京-海淀区', '教育', 'good',
 80000.00, 9.8, '2023-02-20 14:00:00', '2024-08-29 09:15:00', 1, NOW(), NOW(), 0),

-- 普通客户测试数据
('TEST_王五', 'INDIVIDUAL', 'regular', 'active', '王五', '13800138003', 'wangwu@test.com', '/avatar3.jpg',
 '["普通客户"]', '普通个人客户', '西城区西单大街3号', '北京-西城区', '零售', 'fair',
 15000.00, 7.2, '2023-03-10 16:30:00', '2024-08-28 11:45:00', 1, NOW(), NOW(), 0),

('TEST_赵六', 'ENTERPRISE', 'regular', 'inactive', '赵六', '13800138004', 'zhaoliu@test.com', '/avatar4.jpg',
 '["企业客户", "待激活"]', '企业客户待激活', '东城区王府井大街4号', '北京-东城区', '商贸', 'poor',
 8000.00, 6.5, '2023-04-05 08:20:00', '2024-07-15 14:20:00', 0, NOW(), NOW(), 0),

-- 上海地区客户
('TEST_孙七', 'INDIVIDUAL', 'vip', 'active', '孙七', '13800138005', 'sunqi@test.com', '/avatar5.jpg',
 '["VIP", "上海客户"]', '上海VIP客户', '浦东新区陆家嘴金融区5号', '上海-浦东新区', '金融', 'excellent',
 120000.00, 9.9, '2023-05-12 12:10:00', '2024-08-31 16:00:00', 1, NOW(), NOW(), 0),

('TEST_周八', 'ENTERPRISE', 'premium', 'active', '周八', '13800138006', 'zhouba@test.com', '/avatar6.jpg',
 '["高端客户", "制造业"]', '制造业高端客户', '徐汇区淮海中路6号', '上海-徐汇区', '制造', 'good',
 95000.00, 8.8, '2023-06-18 09:45:00', '2024-08-30 13:25:00', 1, NOW(), NOW(), 0),

-- 广州地区客户
('TEST_吴九', 'INDIVIDUAL', 'regular', 'active', '吴九', '13800138007', 'wujiu@test.com', '/avatar7.jpg',
 '["普通客户", "广州"]', '广州普通客户', '天河区珠江新城7号', '广州-天河区', '服务', 'fair',
 25000.00, 7.8, '2023-07-22 15:15:00', '2024-08-27 10:30:00', 1, NOW(), NOW(), 0),

('TEST_郑十', 'ENTERPRISE', 'vip', 'suspended', '郑十', '13800138008', 'zhengshi@test.com', '/avatar8.jpg',
 '["VIP", "暂停服务"]', 'VIP客户暂停服务', '越秀区中山五路8号', '广州-越秀区', '贸易', 'poor',
 60000.00, 8.5, '2023-08-30 11:00:00', '2024-06-10 08:45:00', 0, NOW(), NOW(), 0);

-- 插入设备关联数据（用于设备类型筛选测试）
INSERT INTO devices (device_name, device_type, device_model, device_status, purchase_date, warranty_period, is_deleted, created_at, updated_at)
VALUES 
('TEST_机器人A1', 'cleaning_robot', 'Model-A1', 'active', '2023-01-20', 24, 0, NOW(), NOW()),
('TEST_机器人B1', 'service_robot', 'Model-B1', 'active', '2023-02-25', 36, 0, NOW(), NOW()),
('TEST_机器人C1', 'cleaning_robot', 'Model-C1', 'maintenance', '2023-03-15', 24, 0, NOW(), NOW()),
('TEST_机器人D1', 'security_robot', 'Model-D1', 'active', '2023-04-10', 48, 0, NOW(), NOW());

-- 插入客户设备关联数据
INSERT INTO customer_device_relation (customer_id, device_id, relation_type, start_date, status, created_at, updated_at)
SELECT 
    c.id, 
    d.id, 
    'purchased',
    '2023-01-20',
    1,
    NOW(),
    NOW()
FROM customers c, devices d 
WHERE c.customer_name = 'TEST_张三' AND d.device_name = 'TEST_机器人A1';

INSERT INTO customer_device_relation (customer_id, device_id, relation_type, start_date, status, created_at, updated_at)
SELECT 
    c.id, 
    d.id, 
    'rental',
    '2023-02-25',
    1,
    NOW(),
    NOW()
FROM customers c, devices d 
WHERE c.customer_name = 'TEST_李四' AND d.device_name = 'TEST_机器人B1';

INSERT INTO customer_device_relation (customer_id, device_id, relation_type, start_date, status, created_at, updated_at)
SELECT 
    c.id, 
    d.id, 
    'purchased',
    '2023-03-15',
    1,
    NOW(),
    NOW()
FROM customers c, devices d 
WHERE c.customer_name = 'TEST_孙七' AND d.device_name = 'TEST_机器人C1';

-- ==================== 基础搜索功能测试 ====================

-- 测试1: 按客户姓名搜索
SELECT '=== 测试1: 按客户姓名搜索 ===' as test_name;
SELECT customer_name, phone, email, customer_level 
FROM customers 
WHERE customer_name LIKE '%TEST_张%' AND is_deleted = 0;

-- 测试2: 按电话号码搜索
SELECT '=== 测试2: 按电话号码搜索 ===' as test_name;
SELECT customer_name, phone, email, customer_level 
FROM customers 
WHERE phone LIKE '%138001%' AND is_deleted = 0;

-- 测试3: 按邮箱搜索
SELECT '=== 测试3: 按邮箱搜索 ===' as test_name;
SELECT customer_name, phone, email, customer_level 
FROM customers 
WHERE email LIKE '%lisi@%' AND is_deleted = 0;

-- 测试4: 关键词组合搜索
SELECT '=== 测试4: 关键词组合搜索 ===' as test_name;
SELECT customer_name, phone, email, customer_level 
FROM customers 
WHERE (customer_name LIKE '%TEST_%' OR phone LIKE '%1380013800%' OR email LIKE '%@test.com%') 
AND is_deleted = 0
LIMIT 5;

-- ==================== 筛选功能测试 ====================

-- 测试5: 按客户等级筛选
SELECT '=== 测试5: 按客户等级筛选 ===' as test_name;
SELECT customer_level, COUNT(*) as count
FROM customers 
WHERE customer_name LIKE 'TEST_%' AND is_deleted = 0
GROUP BY customer_level
ORDER BY customer_level;

-- 测试6: 按客户状态筛选
SELECT '=== 测试6: 按客户状态筛选 ===' as test_name;
SELECT customer_status, COUNT(*) as count
FROM customers 
WHERE customer_name LIKE 'TEST_%' AND is_deleted = 0
GROUP BY customer_status;

-- 测试7: 按地区筛选
SELECT '=== 测试7: 按地区筛选 ===' as test_name;
SELECT region, COUNT(*) as count
FROM customers 
WHERE customer_name LIKE 'TEST_%' AND is_deleted = 0
GROUP BY region
ORDER BY count DESC;

-- 测试8: 按行业筛选
SELECT '=== 测试8: 按行业筛选 ===' as test_name;
SELECT industry, COUNT(*) as count
FROM customers 
WHERE customer_name LIKE 'TEST_%' AND is_deleted = 0
GROUP BY industry
ORDER BY count DESC;

-- ==================== 复合筛选测试 ====================

-- 测试9: 等级+地区组合筛选
SELECT '=== 测试9: 等级+地区组合筛选 ===' as test_name;
SELECT customer_name, customer_level, region, total_spent
FROM customers 
WHERE customer_level = 'vip' 
AND region LIKE '%北京%' 
AND customer_name LIKE 'TEST_%'
AND is_deleted = 0;

-- 测试10: 消费金额范围筛选
SELECT '=== 测试10: 消费金额范围筛选 ===' as test_name;
SELECT customer_name, total_spent, customer_level
FROM customers 
WHERE total_spent BETWEEN 20000 AND 100000
AND customer_name LIKE 'TEST_%'
AND is_deleted = 0
ORDER BY total_spent DESC;

-- 测试11: 客户价值范围筛选
SELECT '=== 测试11: 客户价值范围筛选 ===' as test_name;
SELECT customer_name, customer_value, customer_level, total_spent
FROM customers 
WHERE customer_value >= 8.0
AND customer_name LIKE 'TEST_%'
AND is_deleted = 0
ORDER BY customer_value DESC;

-- 测试12: 注册时间范围筛选
SELECT '=== 测试12: 注册时间范围筛选 ===' as test_name;
SELECT customer_name, registered_at, customer_level
FROM customers 
WHERE registered_at BETWEEN '2023-01-01' AND '2023-06-30'
AND customer_name LIKE 'TEST_%'
AND is_deleted = 0
ORDER BY registered_at;

-- ==================== 设备类型筛选测试 ====================

-- 测试13: 按设备类型筛选客户
SELECT '=== 测试13: 按设备类型筛选客户 ===' as test_name;
SELECT DISTINCT c.customer_name, c.customer_level, d.device_type, d.device_name
FROM customers c
INNER JOIN customer_device_relation cdr ON c.id = cdr.customer_id
INNER JOIN devices d ON cdr.device_id = d.id
WHERE d.device_type = 'cleaning_robot'
AND c.customer_name LIKE 'TEST_%'
AND c.is_deleted = 0 
AND d.is_deleted = 0 
AND cdr.status = 1;

-- 测试14: 有设备的客户筛选
SELECT '=== 测试14: 有设备的客户筛选 ===' as test_name;
SELECT c.customer_name, c.customer_level, COUNT(cdr.device_id) as device_count
FROM customers c
INNER JOIN customer_device_relation cdr ON c.id = cdr.customer_id
INNER JOIN devices d ON cdr.device_id = d.id
WHERE c.customer_name LIKE 'TEST_%'
AND c.is_deleted = 0 
AND d.is_deleted = 0 
AND cdr.status = 1
GROUP BY c.id, c.customer_name, c.customer_level
ORDER BY device_count DESC;

-- 测试15: 无设备的客户筛选
SELECT '=== 测试15: 无设备的客户筛选 ===' as test_name;
SELECT c.customer_name, c.customer_level, c.total_spent
FROM customers c
WHERE c.customer_name LIKE 'TEST_%'
AND c.is_deleted = 0
AND NOT EXISTS (
    SELECT 1 FROM customer_device_relation cdr
    INNER JOIN devices d ON cdr.device_id = d.id
    WHERE cdr.customer_id = c.id 
    AND d.is_deleted = 0 
    AND cdr.status = 1
);

-- ==================== 排序功能测试 ====================

-- 测试16: 按客户姓名排序
SELECT '=== 测试16: 按客户姓名排序 ===' as test_name;
SELECT customer_name, customer_level, total_spent
FROM customers 
WHERE customer_name LIKE 'TEST_%' AND is_deleted = 0
ORDER BY customer_name ASC;

-- 测试17: 按消费金额排序
SELECT '=== 测试17: 按消费金额排序 ===' as test_name;
SELECT customer_name, total_spent, customer_level
FROM customers 
WHERE customer_name LIKE 'TEST_%' AND is_deleted = 0
ORDER BY total_spent DESC;

-- 测试18: 按客户价值排序
SELECT '=== 测试18: 按客户价值排序 ===' as test_name;
SELECT customer_name, customer_value, customer_level, total_spent
FROM customers 
WHERE customer_name LIKE 'TEST_%' AND is_deleted = 0
ORDER BY customer_value DESC;

-- 测试19: 按最后活跃时间排序
SELECT '=== 测试19: 按最后活跃时间排序 ===' as test_name;
SELECT customer_name, last_active_at, customer_status
FROM customers 
WHERE customer_name LIKE 'TEST_%' AND is_deleted = 0
ORDER BY last_active_at DESC;

-- 测试20: 多字段排序
SELECT '=== 测试20: 多字段排序 ===' as test_name;
SELECT customer_name, customer_level, total_spent, customer_value
FROM customers 
WHERE customer_name LIKE 'TEST_%' AND is_deleted = 0
ORDER BY customer_level DESC, total_spent DESC;

-- ==================== 分页功能测试 ====================

-- 测试21: 分页查询第一页
SELECT '=== 测试21: 分页查询第一页 ===' as test_name;
SELECT customer_name, customer_level, total_spent
FROM customers 
WHERE customer_name LIKE 'TEST_%' AND is_deleted = 0
ORDER BY created_at DESC
LIMIT 3 OFFSET 0;

-- 测试22: 分页查询第二页
SELECT '=== 测试22: 分页查询第二页 ===' as test_name;
SELECT customer_name, customer_level, total_spent
FROM customers 
WHERE customer_name LIKE 'TEST_%' AND is_deleted = 0
ORDER BY created_at DESC
LIMIT 3 OFFSET 3;

-- 测试23: 分页总数统计
SELECT '=== 测试23: 分页总数统计 ===' as test_name;
SELECT COUNT(*) as total_count
FROM customers 
WHERE customer_name LIKE 'TEST_%' AND is_deleted = 0;

-- ==================== 统计功能测试 ====================

-- 测试24: 客户等级分布统计
SELECT '=== 测试24: 客户等级分布统计 ===' as test_name;
SELECT 
    customer_level,
    COUNT(*) as count,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM customers WHERE customer_name LIKE 'TEST_%' AND is_deleted = 0), 2) as percentage
FROM customers 
WHERE customer_name LIKE 'TEST_%' AND is_deleted = 0
GROUP BY customer_level
ORDER BY count DESC;

-- 测试25: 地区分布统计
SELECT '=== 测试25: 地区分布统计 ===' as test_name;
SELECT 
    region,
    COUNT(*) as count,
    AVG(total_spent) as avg_spent,
    SUM(total_spent) as total_spent
FROM customers 
WHERE customer_name LIKE 'TEST_%' AND is_deleted = 0
GROUP BY region
ORDER BY count DESC;

-- 测试26: 行业分布统计
SELECT '=== 测试26: 行业分布统计 ===' as test_name;
SELECT 
    industry,
    COUNT(*) as count,
    AVG(customer_value) as avg_value,
    MAX(total_spent) as max_spent
FROM customers 
WHERE customer_name LIKE 'TEST_%' AND is_deleted = 0
GROUP BY industry
ORDER BY count DESC;

-- ==================== 性能测试 ====================

-- 测试27: 复杂查询性能测试
SELECT '=== 测试27: 复杂查询性能测试 ===' as test_name;
EXPLAIN SELECT 
    c.customer_name,
    c.customer_level,
    c.total_spent,
    c.customer_value,
    COUNT(cdr.device_id) as device_count,
    GROUP_CONCAT(d.device_type) as device_types
FROM customers c
LEFT JOIN customer_device_relation cdr ON c.id = cdr.customer_id AND cdr.status = 1
LEFT JOIN devices d ON cdr.device_id = d.id AND d.is_deleted = 0
WHERE c.customer_name LIKE 'TEST_%'
AND c.is_deleted = 0
AND (c.customer_level IN ('vip', 'premium') OR c.total_spent >= 50000)
GROUP BY c.id, c.customer_name, c.customer_level, c.total_spent, c.customer_value
ORDER BY c.customer_value DESC, c.total_spent DESC
LIMIT 10;

-- 测试28: 索引使用情况检查
SELECT '=== 测试28: 索引使用情况检查 ===' as test_name;
SHOW INDEX FROM customers WHERE Key_name != 'PRIMARY';

-- ==================== 数据完整性测试 ====================

-- 测试29: 数据完整性验证
SELECT '=== 测试29: 数据完整性验证 ===' as test_name;
SELECT 
    'customers' as table_name,
    COUNT(*) as total_records,
    COUNT(CASE WHEN customer_name IS NULL OR customer_name = '' THEN 1 END) as null_names,
    COUNT(CASE WHEN phone IS NULL OR phone = '' THEN 1 END) as null_phones,
    COUNT(CASE WHEN email IS NULL OR email = '' THEN 1 END) as null_emails,
    COUNT(CASE WHEN is_deleted = 1 THEN 1 END) as deleted_records
FROM customers 
WHERE customer_name LIKE 'TEST_%';

-- 测试30: 关联数据完整性验证
SELECT '=== 测试30: 关联数据完整性验证 ===' as test_name;
SELECT 
    'customer_device_relation' as table_name,
    COUNT(*) as total_relations,
    COUNT(CASE WHEN customer_id IS NULL THEN 1 END) as null_customer_ids,
    COUNT(CASE WHEN device_id IS NULL THEN 1 END) as null_device_ids,
    COUNT(CASE WHEN status = 0 THEN 1 END) as inactive_relations
FROM customer_device_relation cdr
INNER JOIN customers c ON cdr.customer_id = c.id
WHERE c.customer_name LIKE 'TEST_%';

-- ==================== 测试结果汇总 ====================

SELECT '=== 测试结果汇总 ===' as summary;
SELECT 
    '搜索和筛选功能测试' as test_category,
    '30个测试用例' as test_count,
    '基础搜索、筛选、排序、分页、统计、性能' as test_scope,
    CASE 
        WHEN (SELECT COUNT(*) FROM customers WHERE customer_name LIKE 'TEST_%' AND is_deleted = 0) >= 8 
        THEN '测试数据准备完成' 
        ELSE '测试数据不足' 
    END as data_status,
    NOW() as test_time;

-- 性能建议
SELECT 
    '性能优化建议' as category,
    '1. 确保已创建必要的索引' as suggestion_1,
    '2. 定期执行ANALYZE TABLE更新统计信息' as suggestion_2,
    '3. 监控慢查询日志' as suggestion_3,
    '4. 考虑使用查询缓存' as suggestion_4;

-- ==================== 清理测试数据 ====================

-- 注意：实际测试完成后可以取消注释以下语句清理测试数据
-- DELETE FROM customer_device_relation WHERE customer_id IN (SELECT id FROM customers WHERE customer_name LIKE 'TEST_%');
-- DELETE FROM devices WHERE device_name LIKE 'TEST_%';
-- DELETE FROM customers WHERE customer_name LIKE 'TEST_%';

SELECT '客户搜索和筛选功能测试完成！' as test_completed;