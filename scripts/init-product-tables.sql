-- ========================================
-- YXRobot 产品管理模块数据库初始化脚本
-- 创建时间: 2024-12-19
-- 说明: 创建产品管理相关的数据库表和初始数据
-- ========================================

-- 使用YXRobot数据库
USE `YXRobot`;

-- ========================================
-- 1. 创建产品表 (products)
-- ========================================

-- 删除表（如果存在）
DROP TABLE IF EXISTS `product_media`;
DROP TABLE IF EXISTS `products`;

-- 创建产品表
CREATE TABLE `products` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '产品ID，主键',
  `name` VARCHAR(200) NOT NULL COMMENT '产品名称',
  `model` VARCHAR(100) NOT NULL COMMENT '产品型号，唯一标识',
  `description` LONGTEXT COMMENT '产品详细描述',
  `price` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '销售价格，单位：元',
  `cover_image_url` VARCHAR(500) DEFAULT NULL COMMENT '封面图片URL',
  `status` VARCHAR(20) NOT NULL DEFAULT 'draft' COMMENT '产品状态：draft-草稿，published-已发布，archived-已下架',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '是否已删除：0-否，1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_model` (`model`),
  KEY `idx_status` (`status`),
  KEY `idx_price` (`price`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_is_deleted` (`is_deleted`),
  KEY `idx_search` (`name`, `model`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产品表';

-- ========================================
-- 2. 创建产品媒体表 (product_media)
-- ========================================

CREATE TABLE `product_media` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '媒体ID，主键',
  `product_id` BIGINT NOT NULL COMMENT '产品ID，外键关联products表',
  `media_type` VARCHAR(20) NOT NULL COMMENT '媒体类型：image-图片，video-视频',
  `media_url` VARCHAR(500) NOT NULL COMMENT '媒体文件访问URL',
  `sort_order` INT NOT NULL DEFAULT 1 COMMENT '排序值，用于控制媒体显示顺序',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_media_type` (`media_type`),
  KEY `idx_sort_order` (`sort_order`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_is_deleted` (`is_deleted`),
  KEY `idx_product_media_composite` (`product_id`, `media_type`, `sort_order`, `is_deleted`),
  CONSTRAINT `fk_product_media_product_id` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产品媒体表，存储产品的图片和视频等媒体文件信息';

-- ========================================
-- 3. 插入产品测试数据
-- ========================================

INSERT INTO `products` (`name`, `model`, `description`, `price`, `cover_image_url`, `status`, `created_at`, `updated_at`, `is_deleted`) VALUES
('家用练字机器人', 'YX-HOME-001', '适合家庭使用的智能练字机器人，支持多种字体和练习模式，帮助孩子提升书写能力。配备高精度书写系统，可以模拟真实的书写体验。', 2999.00, 'https://via.placeholder.com/300x200/4CAF50/FFFFFF?text=家用版', 'published', '2024-01-15 10:30:00', '2024-01-20 14:20:00', 0),

('商用练字机器人', 'YX-BUSINESS-001', '适合商业场所使用的高性能练字机器人，支持多用户同时使用，配备专业管理系统。具有强大的数据分析功能，可以跟踪学习进度。', 8999.00, 'https://via.placeholder.com/300x200/2196F3/FFFFFF?text=商用版', 'published', '2024-01-10 09:15:00', '2024-01-18 16:45:00', 0),

('教育版练字机器人', 'YX-EDU-001', '专为教育机构设计的练字机器人，内置丰富的教学资源和课程管理功能。支持班级管理，教师可以实时监控学生的练习情况。', 5999.00, 'https://via.placeholder.com/300x200/FF9800/FFFFFF?text=教育版', 'published', '2024-01-12 11:20:00', '2024-01-19 13:30:00', 0),

('便携式练字机器人', 'YX-PORTABLE-001', '轻便小巧的便携式练字机器人，适合移动教学和个人练习使用。采用无线设计，电池续航可达8小时。', 1999.00, 'https://via.placeholder.com/300x200/9C27B0/FFFFFF?text=便携版', 'draft', '2024-01-20 15:45:00', '2024-01-20 15:45:00', 0),

('专业版练字机器人', 'YX-PRO-001', '面向专业书法培训机构的高端练字机器人，配备最先进的AI识别技术。支持多种书法字体，包括楷书、行书、草书等。', 12999.00, 'https://via.placeholder.com/300x200/F44336/FFFFFF?text=专业版', 'archived', '2024-01-08 08:30:00', '2024-01-25 10:15:00', 0);

-- ========================================
-- 4. 插入产品媒体测试数据
-- ========================================

INSERT INTO `product_media` (`product_id`, `media_type`, `media_url`, `sort_order`, `created_at`, `updated_at`) VALUES
-- 家用练字机器人媒体
(1, 'image', 'https://via.placeholder.com/800x600/4CAF50/FFFFFF?text=YX-HOME-001-主图', 1, NOW(), NOW()),
(1, 'image', 'https://via.placeholder.com/800x600/4CAF50/FFFFFF?text=YX-HOME-001-侧面图', 2, NOW(), NOW()),
(1, 'image', 'https://via.placeholder.com/800x600/4CAF50/FFFFFF?text=YX-HOME-001-细节图', 3, NOW(), NOW()),
(1, 'video', 'https://www.w3schools.com/html/mov_bbb.mp4', 1, NOW(), NOW()),

-- 商用练字机器人媒体
(2, 'image', 'https://via.placeholder.com/800x600/2196F3/FFFFFF?text=YX-BUSINESS-001-主图', 1, NOW(), NOW()),
(2, 'image', 'https://via.placeholder.com/800x600/2196F3/FFFFFF?text=YX-BUSINESS-001-功能图', 2, NOW(), NOW()),
(2, 'video', 'https://www.w3schools.com/html/mov_bbb.mp4', 1, NOW(), NOW()),

-- 教育版练字机器人媒体
(3, 'image', 'https://via.placeholder.com/800x600/FF9800/FFFFFF?text=YX-EDU-001-主图', 1, NOW(), NOW()),
(3, 'image', 'https://via.placeholder.com/800x600/FF9800/FFFFFF?text=YX-EDU-001-教学场景', 2, NOW(), NOW()),
(3, 'image', 'https://via.placeholder.com/800x600/FF9800/FFFFFF?text=YX-EDU-001-界面展示', 3, NOW(), NOW()),
(3, 'image', 'https://via.placeholder.com/800x600/FF9800/FFFFFF?text=YX-EDU-001-配件图', 4, NOW(), NOW()),

-- 便携式练字机器人媒体
(4, 'image', 'https://via.placeholder.com/800x600/9C27B0/FFFFFF?text=YX-PORTABLE-001-主图', 1, NOW(), NOW()),
(4, 'video', 'https://www.w3schools.com/html/mov_bbb.mp4', 1, NOW(), NOW()),

-- 专业版练字机器人媒体
(5, 'image', 'https://via.placeholder.com/800x600/F44336/FFFFFF?text=YX-PRO-001-主图', 1, NOW(), NOW()),
(5, 'image', 'https://via.placeholder.com/800x600/F44336/FFFFFF?text=YX-PRO-001-专业功能', 2, NOW(), NOW()),
(5, 'video', 'https://www.w3schools.com/html/mov_bbb.mp4', 1, NOW(), NOW()),
(5, 'video', 'https://www.w3schools.com/html/mov_bbb.mp4', 2, NOW(), NOW());

-- ========================================
-- 5. 验证数据插入
-- ========================================

-- 查看产品数据
SELECT 
    id, name, model, price, status, created_at 
FROM `products` 
WHERE `is_deleted` = 0 
ORDER BY `created_at` DESC;

-- 查看产品媒体数据统计
SELECT 
    p.name AS product_name,
    pm.media_type,
    COUNT(*) AS media_count
FROM `products` p
LEFT JOIN `product_media` pm ON p.id = pm.product_id AND pm.is_deleted = 0
WHERE p.is_deleted = 0
GROUP BY p.id, p.name, pm.media_type
ORDER BY p.id, pm.media_type;

-- 显示完成信息
SELECT '产品管理模块数据库初始化完成！' AS message;
SELECT CONCAT('共创建 ', COUNT(*), ' 个产品') AS product_count FROM `products` WHERE `is_deleted` = 0;
SELECT CONCAT('共创建 ', COUNT(*), ' 个媒体文件') AS media_count FROM `product_media` WHERE `is_deleted` = 0;