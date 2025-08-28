-- =====================================================
-- YXRobot 产品管理系统测试数据初始化脚本
-- 版本: V002
-- 创建时间: 2024-12-19
-- 描述: 插入产品管理测试数据
-- =====================================================

-- 插入测试产品数据
INSERT INTO `products` (`id`, `name`, `model`, `description`, `price`, `cover_image_url`, `status`, `created_at`, `updated_at`) VALUES
(1, '家用练字机器人', 'YX-HOME-001', '适合家庭使用的智能练字机器人，支持多种字体和练习模式', 2999.00, 'https://via.placeholder.com/150x150', 'published', '2024-01-15 10:30:00', '2024-01-20 14:20:00'),
(2, '商用练字机器人', 'YX-BUSINESS-001', '适合商业场所使用的高性能练字机器人', 8999.00, 'https://via.placeholder.com/150x150', 'published', '2024-01-10 09:15:00', '2024-01-18 16:45:00'),
(3, '教育版练字机器人', 'YX-EDU-001', '专为教育机构设计的练字机器人', 5999.00, '', 'draft', '2024-01-12 11:20:00', '2024-01-19 13:30:00'),
(4, '便携式练字机器人', 'YX-PORTABLE-001', '轻便易携带的练字机器人，适合移动使用', 1999.00, 'https://via.placeholder.com/150x150', 'published', '2024-01-08 14:45:00', '2024-01-22 10:15:00'),
(5, '专业版练字机器人', 'YX-PRO-001', '专业级练字机器人，具备高精度书写能力', 12999.00, 'https://via.placeholder.com/150x150', 'archived', '2024-01-05 16:20:00', '2024-01-25 09:30:00');

-- 插入产品媒体测试数据
INSERT INTO `product_media` (`id`, `product_id`, `media_type`, `media_url`, `sort_order`) VALUES
-- 家用练字机器人的媒体
(1, 1, 'image', 'https://via.placeholder.com/300x200', 1),
(2, 1, 'image', 'https://via.placeholder.com/300x200', 2),
(3, 1, 'video', 'https://www.w3schools.com/html/mov_bbb.mp4', 1),

-- 商用练字机器人的媒体
(4, 2, 'image', 'https://via.placeholder.com/300x200', 1),
(5, 2, 'image', 'https://via.placeholder.com/300x200', 2),
(6, 2, 'image', 'https://via.placeholder.com/300x200', 3),
(7, 2, 'video', 'https://www.w3schools.com/html/mov_bbb.mp4', 1),

-- 便携式练字机器人的媒体
(8, 4, 'image', 'https://via.placeholder.com/300x200', 1),
(9, 4, 'video', 'https://www.w3schools.com/html/mov_bbb.mp4', 1),

-- 专业版练字机器人的媒体
(10, 5, 'image', 'https://via.placeholder.com/300x200', 1),
(11, 5, 'image', 'https://via.placeholder.com/300x200', 2),
(12, 5, 'image', 'https://via.placeholder.com/300x200', 3),
(13, 5, 'image', 'https://via.placeholder.com/300x200', 4),
(14, 5, 'video', 'https://www.w3schools.com/html/mov_bbb.mp4', 1),
(15, 5, 'video', 'https://www.w3schools.com/html/mov_bbb.mp4', 2);