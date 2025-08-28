-- 平台链接管理系统测试数据
-- 创建时间: 2024-12-22
-- 维护人员: YXRobot开发团队

USE YXRobot;

-- 插入测试平台链接数据
INSERT INTO platform_links (platform_name, platform_type, link_url, region, country, language_code, language_name, is_enabled, link_status, click_count, conversion_count) VALUES
-- 中国大陆电商平台
('淘宝', 'ecommerce', 'https://shop.taobao.com/yxrobot', '中国大陆', '中国', 'zh-CN', '简体中文', 1, 'active', 45680, 3245),
('京东', 'ecommerce', 'https://shop.jd.com/yxrobot', '中国大陆', '中国', 'zh-CN', '简体中文', 1, 'active', 32450, 2156),
('天猫', 'ecommerce', 'https://shop.tmall.com/yxrobot', '中国大陆', '中国', 'zh-CN', '简体中文', 1, 'active', 28900, 1890),
('拼多多', 'ecommerce', 'https://shop.pinduoduo.com/yxrobot', '中国大陆', '中国', 'zh-CN', '简体中文', 1, 'active', 18650, 1654),

-- 美国电商平台
('亚马逊美国', 'ecommerce', 'https://amazon.com/yxrobot', '美国', '美国', 'en-US', '英语', 1, 'active', 25420, 1876),
('eBay美国', 'ecommerce', 'https://ebay.com/yxrobot', '美国', '美国', 'en-US', '英语', 1, 'active', 15340, 987),
('沃尔玛', 'ecommerce', 'https://walmart.com/yxrobot', '美国', '美国', 'en-US', '英语', 1, 'active', 12890, 756),

-- 日本电商平台
('亚马逊日本', 'ecommerce', 'https://amazon.co.jp/yxrobot', '日本', '日本', 'ja-JP', '日语', 1, 'active', 18750, 1234),
('乐天', 'ecommerce', 'https://rakuten.co.jp/yxrobot', '日本', '日本', 'ja-JP', '日语', 1, 'active', 14560, 892),

-- 韩国电商平台
('Gmarket', 'ecommerce', 'https://gmarket.co.kr/yxrobot', '韩国', '韩国', 'ko-KR', '韩语', 1, 'active', 12340, 678),
('11번가', 'ecommerce', 'https://11st.co.kr/yxrobot', '韩国', '韩国', 'ko-KR', '韩语', 1, 'active', 9870, 543),

-- 德国电商平台
('亚马逊德国', 'ecommerce', 'https://amazon.de/yxrobot', '德国', '德国', 'de-DE', '德语', 1, 'active', 11250, 687),
('Otto', 'ecommerce', 'https://otto.de/yxrobot', '德国', '德国', 'de-DE', '德语', 1, 'active', 8760, 456),

-- 法国电商平台
('亚马逊法国', 'ecommerce', 'https://amazon.fr/yxrobot', '法国', '法国', 'fr-FR', '法语', 1, 'active', 9850, 534),
('Cdiscount', 'ecommerce', 'https://cdiscount.com/yxrobot', '法国', '法国', 'fr-FR', '法语', 1, 'active', 7650, 398),

-- 租赁平台
('租赁宝', 'rental', 'https://rental.taobao.com/yxrobot', '中国大陆', '中国', 'zh-CN', '简体中文', 1, 'active', 8950, 1245),
('京东租赁', 'rental', 'https://rental.jd.com/yxrobot', '中国大陆', '中国', 'zh-CN', '简体中文', 1, 'active', 6780, 892),
('美国租赁网', 'rental', 'https://rental.us/yxrobot', '美国', '美国', 'en-US', '英语', 1, 'active', 5640, 678),
('日本租赁', 'rental', 'https://rental.jp/yxrobot', '日本', '日本', 'ja-JP', '日语', 1, 'active', 4320, 456),

-- 一些状态异常的链接
('测试平台1', 'ecommerce', 'https://test1.example.com/yxrobot', '中国大陆', '中国', 'zh-CN', '简体中文', 0, 'inactive', 0, 0),
('测试平台2', 'ecommerce', 'https://test2.example.com/yxrobot', '美国', '美国', 'en-US', '英语', 1, 'error', 120, 5);

-- 插入一些验证日志数据
INSERT INTO link_validation_logs (link_id, is_valid, status_code, response_time, error_message) VALUES
(1, 1, 200, 1250, NULL),
(2, 1, 200, 980, NULL),
(3, 1, 200, 1100, NULL),
(4, 1, 200, 1350, NULL),
(5, 1, 200, 2100, NULL),
(19, 0, 404, 3000, '页面不存在'),
(20, 0, 500, 5000, '服务器内部错误');

-- 插入一些点击日志数据（模拟数据）
INSERT INTO link_click_logs (link_id, user_ip, clicked_at, is_conversion, conversion_type, conversion_value) VALUES
(1, '192.168.1.100', DATE_SUB(NOW(), INTERVAL 1 HOUR), 1, 'purchase', 299.00),
(1, '192.168.1.101', DATE_SUB(NOW(), INTERVAL 2 HOUR), 0, NULL, NULL),
(2, '192.168.1.102', DATE_SUB(NOW(), INTERVAL 3 HOUR), 1, 'purchase', 199.00),
(3, '192.168.1.103', DATE_SUB(NOW(), INTERVAL 4 HOUR), 1, 'purchase', 399.00),
(4, '192.168.1.104', DATE_SUB(NOW(), INTERVAL 5 HOUR), 0, NULL, NULL),
(5, '192.168.1.105', DATE_SUB(NOW(), INTERVAL 6 HOUR), 1, 'purchase', 299.00);

-- 显示插入结果统计
SELECT 
    '平台链接' as table_name,
    COUNT(*) as total_records,
    SUM(CASE WHEN platform_type = 'ecommerce' THEN 1 ELSE 0 END) as ecommerce_count,
    SUM(CASE WHEN platform_type = 'rental' THEN 1 ELSE 0 END) as rental_count,
    SUM(CASE WHEN is_enabled = 1 THEN 1 ELSE 0 END) as enabled_count,
    SUM(CASE WHEN link_status = 'active' THEN 1 ELSE 0 END) as active_count
FROM platform_links

UNION ALL

SELECT 
    '验证日志' as table_name,
    COUNT(*) as total_records,
    SUM(CASE WHEN is_valid = 1 THEN 1 ELSE 0 END) as valid_count,
    SUM(CASE WHEN is_valid = 0 THEN 1 ELSE 0 END) as invalid_count,
    NULL as enabled_count,
    NULL as active_count
FROM link_validation_logs

UNION ALL

SELECT 
    '点击日志' as table_name,
    COUNT(*) as total_records,
    SUM(CASE WHEN is_conversion = 1 THEN 1 ELSE 0 END) as conversion_count,
    SUM(CASE WHEN is_conversion = 0 THEN 1 ELSE 0 END) as no_conversion_count,
    NULL as enabled_count,
    NULL as active_count
FROM link_click_logs;