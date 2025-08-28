-- 创建产品表
-- 用于存储产品基本信息
-- 创建时间: 2024-12-19
-- 维护人员: 数据库管理员

-- 删除表（如果存在）
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
  KEY `idx_search` (`name`, `model`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品表';

-- 插入测试数据
INSERT INTO `products` (`name`, `model`, `description`, `price`, `cover_image_url`, `status`, `created_at`, `updated_at`, `is_deleted`) VALUES
('家用练字机器人', 'YX-HOME-001', '适合家庭使用的智能练字机器人，支持多种字体和练习模式，帮助孩子提升书写能力', 2999.00, 'https://via.placeholder.com/300x200/4CAF50/FFFFFF?text=家用版', 'published', '2024-01-15 10:30:00', '2024-01-20 14:20:00', 0),
('商用练字机器人', 'YX-BUSINESS-001', '适合商业场所使用的高性能练字机器人，支持多用户同时使用，配备专业管理系统', 8999.00, 'https://via.placeholder.com/300x200/2196F3/FFFFFF?text=商用版', 'published', '2024-01-10 09:15:00', '2024-01-18 16:45:00', 0),
('教育版练字机器人', 'YX-EDU-001', '专为教育机构设计的练字机器人，内置丰富的教学资源和课程管理功能', 5999.00, 'https://via.placeholder.com/300x200/FF9800/FFFFFF?text=教育版', 'published', '2024-01-12 11:20:00', '2024-01-19 13:30:00', 0),
('便携式练字机器人', 'YX-PORTABLE-001', '轻便小巧的便携式练字机器人，适合移动教学和个人练习使用', 1999.00, 'https://via.placeholder.com/300x200/9C27B0/FFFFFF?text=便携版', 'draft', '2024-01-20 15:45:00', '2024-01-20 15:45:00', 0),
('专业版练字机器人', 'YX-PRO-001', '面向专业书法培训机构的高端练字机器人，配备最先进的AI识别技术', 12999.00, 'https://via.placeholder.com/300x200/F44336/FFFFFF?text=专业版', 'archived', '2024-01-08 08:30:00', '2024-01-25 10:15:00', 0);

-- 查看插入的数据
SELECT * FROM `products` WHERE `is_deleted` = 0 ORDER BY `created_at` DESC;