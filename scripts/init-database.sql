-- =====================================================
-- YXRobot 数据库初始化脚本
-- 数据库: YXRobot
-- 服务器: yun.finiot.cn:3306
-- 版本: MySQL 8.4.5
-- 创建时间: 2024-12-19
-- =====================================================

-- 使用YXRobot数据库
USE YXRobot;

-- 删除已存在的表（如果存在）
DROP TABLE IF EXISTS `product_media`;
DROP TABLE IF EXISTS `products`;

-- =====================================================
-- 产品表
-- =====================================================
CREATE TABLE `products` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '产品ID，主键',
  `name` VARCHAR(200) NOT NULL COMMENT '产品名称',
  `model` VARCHAR(100) NOT NULL COMMENT '产品型号',
  `description` TEXT COMMENT '产品描述',
  `price` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '销售价格，单位：元',
  `cover_image_url` VARCHAR(500) DEFAULT NULL COMMENT '封面图片URL',
  `status` VARCHAR(20) NOT NULL DEFAULT 'draft' COMMENT '产品状态：draft-草稿，published-已发布，archived-已归档',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '是否已删除：0-否，1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_model` (`model`),
  KEY `idx_name` (`name`),
  KEY `idx_status` (`status`),
  KEY `idx_price` (`price`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产品表';

-- =====================================================
-- 产品媒体表
-- =====================================================
CREATE TABLE `product_media` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '媒体ID，主键',
  `product_id` BIGINT NOT NULL COMMENT '产品ID，外键关联products表',
  `media_type` VARCHAR(20) NOT NULL COMMENT '媒体类型：image-图片，video-视频',
  `media_url` VARCHAR(500) NOT NULL COMMENT '媒体文件URL',
  `sort_order` INT DEFAULT 0 COMMENT '排序权重',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_media_type` (`media_type`),
  KEY `idx_sort_order` (`sort_order`),
  CONSTRAINT `fk_product_media_product_id` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产品媒体表';

-- =====================================================
-- 插入测试数据（基于前端页面的模拟数据）
-- =====================================================

-- 插入测试产品数据（与前端Products.vue中的数据保持一致）
INSERT INTO `products` (`id`, `name`, `model`, `description`, `price`, `status`, `cover_image_url`, `created_at`, `updated_at`) VALUES
(1, '家用练字机器人', 'YX-HOME-001', '适合家庭使用的智能练字机器人，支持多种字体和练习模式', 2999.00, 'published', 'https://via.placeholder.com/150x150', '2024-01-15 10:30:00', '2024-01-20 14:20:00'),
(2, '商用练字机器人', 'YX-BUSINESS-001', '适合商业场所使用的高性能练字机器人', 8999.00, 'published', 'https://via.placeholder.com/150x150', '2024-01-10 09:15:00', '2024-01-18 16:45:00'),
(3, '教育版练字机器人', 'YX-EDU-001', '专为教育机构设计的练字机器人', 5999.00, 'draft', '', '2024-01-12 11:20:00', '2024-01-19 13:30:00'),
(4, '便携式练字机器人', 'YX-MINI-001', '小巧便携的练字机器人，适合随身携带和旅行使用', 1999.99, 'draft', 'https://via.placeholder.com/150x150', '2024-01-08 14:25:00', '2024-01-22 10:15:00'),
(5, '专业版练字机器人', 'YX-PRO-001', '专业级练字机器人，具有最先进的技术和最完善的功能', 12999.99, 'archived', 'https://via.placeholder.com/150x150', '2024-01-05 16:40:00', '2024-01-25 11:30:00'),
(6, '智能练字机器人', 'YX-SMART-001', '集成AI技术的智能练字机器人，能够自动识别和纠正书写错误', 6999.00, 'published', 'https://via.placeholder.com/150x150', '2024-01-20 09:00:00', '2024-01-28 15:20:00'),
(7, '儿童练字机器人', 'YX-KIDS-001', '专为儿童设计的练字机器人，界面友好，操作简单', 3999.00, 'published', 'https://via.placeholder.com/150x150', '2024-01-18 11:45:00', '2024-01-26 14:10:00'),
(8, '企业版练字机器人', 'YX-ENTERPRISE-001', '面向企业培训的练字机器人，支持批量管理和数据分析', 15999.00, 'draft', 'https://via.placeholder.com/150x150', '2024-01-22 13:20:00', '2024-01-30 16:50:00');

-- 插入产品媒体数据（基于前端页面的媒体管理数据）
INSERT INTO `product_media` (`product_id`, `media_type`, `media_url`, `sort_order`) VALUES
-- 家用练字机器人媒体
(1, 'image', 'https://via.placeholder.com/300x200/4CAF50/FFFFFF?text=YX-HOME-001-Image1', 1),
(1, 'image', 'https://via.placeholder.com/300x200/4CAF50/FFFFFF?text=YX-HOME-001-Image2', 2),
(1, 'video', 'https://www.w3schools.com/html/mov_bbb.mp4', 1),

-- 商用练字机器人媒体
(2, 'image', 'https://via.placeholder.com/300x200/2196F3/FFFFFF?text=YX-BUSINESS-001-Image1', 1),
(2, 'image', 'https://via.placeholder.com/300x200/2196F3/FFFFFF?text=YX-BUSINESS-001-Image2', 2),
(2, 'image', 'https://via.placeholder.com/300x200/2196F3/FFFFFF?text=YX-BUSINESS-001-Image3', 3),
(2, 'video', 'https://www.w3schools.com/html/mov_bbb.mp4', 1),

-- 教育版练字机器人媒体
(3, 'image', 'https://via.placeholder.com/300x200/FF9800/FFFFFF?text=YX-EDU-001-Image1', 1),
(3, 'image', 'https://via.placeholder.com/300x200/FF9800/FFFFFF?text=YX-EDU-001-Image2', 2),

-- 便携式练字机器人媒体
(4, 'image', 'https://via.placeholder.com/300x200/9C27B0/FFFFFF?text=YX-MINI-001-Image1', 1),
(4, 'image', 'https://via.placeholder.com/300x200/9C27B0/FFFFFF?text=YX-MINI-001-Image2', 2),

-- 专业版练字机器人媒体
(5, 'image', 'https://via.placeholder.com/300x200/F44336/FFFFFF?text=YX-PRO-001-Image1', 1),
(5, 'image', 'https://via.placeholder.com/300x200/F44336/FFFFFF?text=YX-PRO-001-Image2', 2),
(5, 'video', 'https://www.w3schools.com/html/mov_bbb.mp4', 1),

-- 智能练字机器人媒体
(6, 'image', 'https://via.placeholder.com/300x200/00BCD4/FFFFFF?text=YX-SMART-001-Image1', 1),
(6, 'image', 'https://via.placeholder.com/300x200/00BCD4/FFFFFF?text=YX-SMART-001-Image2', 2),

-- 儿童练字机器人媒体
(7, 'image', 'https://via.placeholder.com/300x200/FFEB3B/000000?text=YX-KIDS-001-Image1', 1),
(7, 'image', 'https://via.placeholder.com/300x200/FFEB3B/000000?text=YX-KIDS-001-Image2', 2),
(7, 'video', 'https://www.w3schools.com/html/mov_bbb.mp4', 1),

-- 企业版练字机器人媒体
(8, 'image', 'https://via.placeholder.com/300x200/795548/FFFFFF?text=YX-ENTERPRISE-001-Image1', 1),
(8, 'image', 'https://via.placeholder.com/300x200/795548/FFFFFF?text=YX-ENTERPRISE-001-Image2', 2);

-- =====================================================
-- 验证数据插入
-- =====================================================
SELECT '产品数据插入完成' as message;
SELECT COUNT(*) as product_count FROM products;
SELECT COUNT(*) as media_count FROM product_media;

-- 显示插入的产品数据
SELECT id, name, model, price, status, created_at FROM products ORDER BY id;

COMMIT;