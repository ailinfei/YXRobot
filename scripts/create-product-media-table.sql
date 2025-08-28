-- 创建产品媒体表
-- 用于存储产品的图片和视频等媒体文件信息

CREATE TABLE IF NOT EXISTS `product_media` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '媒体ID，主键',
  `product_id` bigint(20) NOT NULL COMMENT '产品ID，外键关联products表',
  `media_type` varchar(20) NOT NULL COMMENT '媒体类型：image-图片，video-视频',
  `media_url` varchar(500) NOT NULL COMMENT '媒体文件访问URL',
  `sort_order` int(11) NOT NULL DEFAULT '1' COMMENT '排序值，用于控制媒体显示顺序',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_media_type` (`media_type`),
  KEY `idx_sort_order` (`sort_order`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_is_deleted` (`is_deleted`),
  CONSTRAINT `fk_product_media_product_id` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产品媒体表';

-- 插入测试数据
INSERT INTO `product_media` (`product_id`, `media_type`, `media_url`, `sort_order`, `created_at`, `updated_at`) VALUES
(1, 'image', 'https://via.placeholder.com/800x600/4CAF50/FFFFFF?text=YX-HOME-001-Image1', 1, NOW(), NOW()),
(1, 'image', 'https://via.placeholder.com/800x600/4CAF50/FFFFFF?text=YX-HOME-001-Image2', 2, NOW(), NOW()),
(1, 'image', 'https://via.placeholder.com/800x600/4CAF50/FFFFFF?text=YX-HOME-001-Image3', 3, NOW(), NOW()),
(1, 'video', 'https://www.w3schools.com/html/mov_bbb.mp4', 1, NOW(), NOW()),

(2, 'image', 'https://via.placeholder.com/800x600/2196F3/FFFFFF?text=YX-BUSINESS-001-Image1', 1, NOW(), NOW()),
(2, 'image', 'https://via.placeholder.com/800x600/2196F3/FFFFFF?text=YX-BUSINESS-001-Image2', 2, NOW(), NOW()),
(2, 'video', 'https://www.w3schools.com/html/mov_bbb.mp4', 1, NOW(), NOW()),

(3, 'image', 'https://via.placeholder.com/800x600/FF9800/FFFFFF?text=YX-EDU-001-Image1', 1, NOW(), NOW()),
(3, 'image', 'https://via.placeholder.com/800x600/FF9800/FFFFFF?text=YX-EDU-001-Image2', 2, NOW(), NOW()),
(3, 'image', 'https://via.placeholder.com/800x600/FF9800/FFFFFF?text=YX-EDU-001-Image3', 3, NOW(), NOW()),
(3, 'image', 'https://via.placeholder.com/800x600/FF9800/FFFFFF?text=YX-EDU-001-Image4', 4, NOW(), NOW()),

(4, 'image', 'https://via.placeholder.com/800x600/9C27B0/FFFFFF?text=YX-PORTABLE-001-Image1', 1, NOW(), NOW()),
(4, 'video', 'https://www.w3schools.com/html/mov_bbb.mp4', 1, NOW(), NOW()),

(5, 'image', 'https://via.placeholder.com/800x600/F44336/FFFFFF?text=YX-PRO-001-Image1', 1, NOW(), NOW()),
(5, 'image', 'https://via.placeholder.com/800x600/F44336/FFFFFF?text=YX-PRO-001-Image2', 2, NOW(), NOW()),
(5, 'video', 'https://www.w3schools.com/html/mov_bbb.mp4', 1, NOW(), NOW()),
(5, 'video', 'https://www.w3schools.com/html/mov_bbb.mp4', 2, NOW(), NOW());

-- 创建索引以优化查询性能
CREATE INDEX `idx_product_media_composite` ON `product_media` (`product_id`, `media_type`, `sort_order`, `is_deleted`);

-- 添加注释
ALTER TABLE `product_media` COMMENT = '产品媒体表，存储产品的图片和视频等媒体文件信息';