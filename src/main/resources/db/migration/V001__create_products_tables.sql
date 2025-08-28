-- =====================================================
-- YXRobot 产品管理系统数据库初始化脚本
-- 版本: V001
-- 创建时间: 2024-12-19
-- 描述: 创建产品管理相关的数据表
-- =====================================================

-- 产品表
-- 用于存储产品基本信息
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
  KEY `idx_name` (`name`),
  KEY `idx_model` (`model`),
  KEY `idx_status` (`status`),
  KEY `idx_price` (`price`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品表';

-- 产品媒体表
-- 用于存储产品相关的图片和视频
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品媒体表';

-- 创建索引以优化查询性能
CREATE INDEX `idx_products_search` ON `products` (`name`, `model`, `status`);
CREATE INDEX `idx_product_media_product_type` ON `product_media` (`product_id`, `media_type`);