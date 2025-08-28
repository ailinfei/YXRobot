-- 新闻管理模块数据库表结构
-- 创建时间: 2025-01-25

-- 1. 新闻分类表
CREATE TABLE IF NOT EXISTS `news_categories` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID，主键',
  `name` VARCHAR(100) NOT NULL COMMENT '分类名称',
  `description` TEXT COMMENT '分类描述',
  `sort_order` INT DEFAULT 0 COMMENT '排序权重',
  `is_enabled` TINYINT(1) DEFAULT 1 COMMENT '是否启用',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`),
  INDEX `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='新闻分类表';

-- 2. 新闻标签表
CREATE TABLE IF NOT EXISTS `news_tags` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '标签ID，主键',
  `name` VARCHAR(50) NOT NULL COMMENT '标签名称',
  `color` VARCHAR(20) DEFAULT '#409EFF' COMMENT '标签颜色',
  `usage_count` INT DEFAULT 0 COMMENT '使用次数',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`),
  INDEX `idx_usage_count` (`usage_count`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='新闻标签表';

-- 3. 新闻主表
CREATE TABLE IF NOT EXISTS `news` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '新闻ID，主键',
  `title` VARCHAR(200) NOT NULL COMMENT '新闻标题',
  `excerpt` TEXT COMMENT '新闻摘要',
  `content` LONGTEXT NOT NULL COMMENT '新闻内容（HTML格式）',
  `category_id` BIGINT NOT NULL COMMENT '分类ID',
  `author` VARCHAR(100) NOT NULL COMMENT '作者',
  `status` ENUM('draft', 'published', 'offline') DEFAULT 'draft' COMMENT '状态：草稿、已发布、已下线',
  `cover_image` VARCHAR(500) COMMENT '封面图片URL',
  `publish_time` DATETIME COMMENT '发布时间',
  `views` INT DEFAULT 0 COMMENT '浏览量',
  `comments` INT DEFAULT 0 COMMENT '评论数',
  `likes` INT DEFAULT 0 COMMENT '点赞数',
  `is_featured` TINYINT(1) DEFAULT 0 COMMENT '是否推荐',
  `sort_order` INT DEFAULT 0 COMMENT '排序权重',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`),
  INDEX `idx_category_id` (`category_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_publish_time` (`publish_time`),
  INDEX `idx_created_at` (`created_at`),
  INDEX `idx_is_deleted` (`is_deleted`),
  INDEX `idx_status_publish_deleted` (`status`, `publish_time`, `is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='新闻表';

-- 4. 新闻标签关联表
CREATE TABLE IF NOT EXISTS `news_tag_relations` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID，主键',
  `news_id` BIGINT NOT NULL COMMENT '新闻ID',
  `tag_id` BIGINT NOT NULL COMMENT '标签ID',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_news_tag` (`news_id`, `tag_id`),
  INDEX `idx_news_id` (`news_id`),
  INDEX `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='新闻标签关联表';

-- 5. 新闻互动数据表
CREATE TABLE IF NOT EXISTS `news_interactions` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '互动ID，主键',
  `news_id` BIGINT NOT NULL COMMENT '新闻ID',
  `interaction_type` ENUM('view', 'like', 'comment', 'share') NOT NULL COMMENT '互动类型',
  `user_id` BIGINT COMMENT '用户ID（可为空）',
  `ip_address` VARCHAR(45) COMMENT 'IP地址',
  `user_agent` TEXT COMMENT '用户代理',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_news_id` (`news_id`),
  INDEX `idx_interaction_type` (`interaction_type`),
  INDEX `idx_created_at` (`created_at`),
  INDEX `idx_news_type_time` (`news_id`, `interaction_type`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='新闻互动数据表';

-- 6. 新闻状态变更日志表
CREATE TABLE IF NOT EXISTS `news_status_logs` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID，主键',
  `news_id` BIGINT NOT NULL COMMENT '新闻ID',
  `from_status` ENUM('draft', 'published', 'offline') NOT NULL COMMENT '原状态',
  `to_status` ENUM('draft', 'published', 'offline') NOT NULL COMMENT '新状态',
  `reason` VARCHAR(500) COMMENT '变更原因',
  `operator` VARCHAR(100) COMMENT '操作人',
  `change_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '变更时间',
  `remark` TEXT COMMENT '备注信息',
  PRIMARY KEY (`id`),
  INDEX `idx_news_id` (`news_id`),
  INDEX `idx_change_time` (`change_time`),
  INDEX `idx_operator` (`operator`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='新闻状态变更日志表';

-- 插入一些初始分类数据
INSERT IGNORE INTO `news_categories` (`name`, `description`, `sort_order`, `is_enabled`) VALUES
('公司新闻', 'YXRobot公司相关新闻', 1, 1),
('产品动态', '产品更新和发布信息', 2, 1),
('行业资讯', '机器人行业相关资讯', 3, 1),
('技术分享', '技术文章和教程', 4, 1);