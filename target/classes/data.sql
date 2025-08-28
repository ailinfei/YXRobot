-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS YXRobot DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE YXRobot;

-- 创建region_configs表
CREATE TABLE IF NOT EXISTS region_configs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    region VARCHAR(100) NOT NULL COMMENT '地区名称',
    country VARCHAR(100) NOT NULL COMMENT '国家名称',
    language_code VARCHAR(20) NOT NULL COMMENT '语言代码',
    language_name VARCHAR(100) NOT NULL COMMENT '语言名称',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否激活',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_region_language (region, language_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='区域配置表';

-- 插入区域配置数据
INSERT IGNORE INTO region_configs (region, country, language_code, language_name, is_active, sort_order) VALUES
-- 中国地区
('中国大陆', '中国', 'zh-CN', '简体中文', 1, 1),
('香港', '中国', 'zh-HK', '繁体中文(香港)', 1, 2),
('澳门', '中国', 'zh-MO', '繁体中文(澳门)', 1, 3),
('台湾', '中国', 'zh-TW', '繁体中文(台湾)', 1, 4),

-- 亚洲其他国家
('日本', '日本', 'ja-JP', '日语', 1, 10),
('韩国', '韩国', 'ko-KR', '韩语', 1, 11),
('新加坡', '新加坡', 'en-SG', '英语', 1, 12),
('新加坡', '新加坡', 'zh-SG', '简体中文', 1, 13),
('马来西亚', '马来西亚', 'ms-MY', '马来语', 1, 14),
('马来西亚', '马来西亚', 'en-MY', '英语', 1, 15),
('泰国', '泰国', 'th-TH', '泰语', 1, 16),
('越南', '越南', 'vi-VN', '越南语', 1, 17),
('印度尼西亚', '印度尼西亚', 'id-ID', '印尼语', 1, 18),
('菲律宾', '菲律宾', 'en-PH', '英语', 1, 19),
('印度', '印度', 'en-IN', '英语', 1, 20),
('印度', '印度', 'hi-IN', '印地语', 1, 21),

-- 北美洲
('美国', '美国', 'en-US', '英语', 1, 30),
('美国', '美国', 'es-US', '西班牙语', 1, 31),
('加拿大', '加拿大', 'en-CA', '英语', 1, 32),
('加拿大', '加拿大', 'fr-CA', '法语', 1, 33),
('墨西哥', '墨西哥', 'es-MX', '西班牙语', 1, 34),

-- 欧洲
('英国', '英国', 'en-GB', '英语', 1, 40),
('德国', '德国', 'de-DE', '德语', 1, 41),
('法国', '法国', 'fr-FR', '法语', 1, 42),
('意大利', '意大利', 'it-IT', '意大利语', 1, 43),
('西班牙', '西班牙', 'es-ES', '西班牙语', 1, 44),
('荷兰', '荷兰', 'nl-NL', '荷兰语', 1, 45),
('比利时', '比利时', 'nl-BE', '荷兰语', 1, 46),
('比利时', '比利时', 'fr-BE', '法语', 1, 47),
('瑞士', '瑞士', 'de-CH', '德语', 1, 48),
('瑞士', '瑞士', 'fr-CH', '法语', 1, 49),
('瑞士', '瑞士', 'it-CH', '意大利语', 1, 50),
('奥地利', '奥地利', 'de-AT', '德语', 1, 51),
('瑞典', '瑞典', 'sv-SE', '瑞典语', 1, 52),
('挪威', '挪威', 'no-NO', '挪威语', 1, 53),
('丹麦', '丹麦', 'da-DK', '丹麦语', 1, 54),
('芬兰', '芬兰', 'fi-FI', '芬兰语', 1, 55),
('波兰', '波兰', 'pl-PL', '波兰语', 1, 56),
('俄罗斯', '俄罗斯', 'ru-RU', '俄语', 1, 57),

-- 南美洲
('巴西', '巴西', 'pt-BR', '葡萄牙语', 1, 60),
('阿根廷', '阿根廷', 'es-AR', '西班牙语', 1, 61),
('智利', '智利', 'es-CL', '西班牙语', 1, 62),
('哥伦比亚', '哥伦比亚', 'es-CO', '西班牙语', 1, 63),

-- 大洋洲
('澳大利亚', '澳大利亚', 'en-AU', '英语', 1, 70),
('新西兰', '新西兰', 'en-NZ', '英语', 1, 71),

-- 非洲
('南非', '南非', 'en-ZA', '英语', 1, 80),
('南非', '南非', 'af-ZA', '南非荷兰语', 1, 81),
('埃及', '埃及', 'ar-EG', '阿拉伯语', 1, 82),

-- 中东
('阿联酋', '阿联酋', 'ar-AE', '阿拉伯语', 1, 90),
('沙特阿拉伯', '沙特阿拉伯', 'ar-SA', '阿拉伯语', 1, 91),
('以色列', '以色列', 'he-IL', '希伯来语', 1, 92),
('以色列', '以色列', 'en-IL', '英语', 1, 93),
('土耳其', '土耳其', 'tr-TR', '土耳其语', 1, 94);

-- 创建platform_links表
CREATE TABLE IF NOT EXISTS platform_links (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    platform_name VARCHAR(200) NOT NULL COMMENT '平台名称',
    platform_type ENUM('ecommerce', 'rental') NOT NULL COMMENT '平台类型',
    link_url TEXT NOT NULL COMMENT '链接地址',
    region VARCHAR(100) NOT NULL COMMENT '地区',
    country VARCHAR(100) NOT NULL COMMENT '国家',
    language_code VARCHAR(20) NOT NULL COMMENT '语言代码',
    language_name VARCHAR(100) NOT NULL COMMENT '语言名称',
    is_enabled BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    link_status ENUM('active', 'inactive', 'checking', 'error') DEFAULT 'active' COMMENT '链接状态',
    click_count INT DEFAULT 0 COMMENT '点击次数',
    conversion_count INT DEFAULT 0 COMMENT '转化次数',
    last_checked_at TIMESTAMP NULL COMMENT '最后检查时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at TIMESTAMP NULL COMMENT '删除时间',
    INDEX idx_platform_type (platform_type),
    INDEX idx_region (region),
    INDEX idx_country (country),
    INDEX idx_language_code (language_code),
    INDEX idx_is_enabled (is_enabled),
    INDEX idx_link_status (link_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='平台链接表';

-- ========================================
-- 新闻管理模块数据库表结构
-- ========================================

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

-- ========================================
-- 新闻管理模块初始数据
-- ========================================

-- 插入新闻分类数据
INSERT IGNORE INTO `news_categories` (`name`, `description`, `sort_order`, `is_enabled`) VALUES
('公司新闻', 'YXRobot公司相关新闻动态', 1, 1),
('产品发布', '新产品发布和产品更新相关新闻', 2, 1),
('技术创新', '技术研发和创新成果相关新闻', 3, 1),
('教育科技', '教育科技领域相关新闻', 4, 1),
('书法教育', '书法教育和传统文化相关新闻', 5, 1),
('行业动态', '行业发展趋势和市场动态', 6, 1),
('合作伙伴', '合作伙伴和商务合作相关新闻', 7, 1),
('媒体报道', '媒体对公司的报道和采访', 8, 1);

-- 插入新闻标签数据
INSERT IGNORE INTO `news_tags` (`name`, `color`, `usage_count`) VALUES
('人工智能', '#409EFF', 0),
('教育科技', '#67C23A', 0),
('书法教育', '#E6A23C', 0),
('技术创新', '#F56C6C', 0),
('产品发布', '#909399', 0),
('机器人', '#409EFF', 0),
('智能硬件', '#67C23A', 0),
('在线教育', '#E6A23C', 0),
('传统文化', '#F56C6C', 0),
('数字化转型', '#909399', 0),
('STEAM教育', '#409EFF', 0),
('创客教育', '#67C23A', 0),
('智慧校园', '#E6A23C', 0),
('教学工具', '#F56C6C', 0),
('行业合作', '#909399', 0);

-- 插入测试新闻数据
INSERT IGNORE INTO `news` (`title`, `excerpt`, `content`, `category_id`, `author`, `status`, `cover_image`, `publish_time`, `views`, `comments`, `likes`, `is_featured`, `sort_order`) VALUES
('YXRobot发布全新AI书法教学机器人', 'YXRobot公司今日正式发布了全新一代AI书法教学机器人，该产品融合了人工智能技术与传统书法教育，为学生提供个性化的书法学习体验。', 
'<h2>产品亮点</h2><p>全新AI书法教学机器人具备以下特色功能：</p><ul><li>智能笔迹识别与纠正</li><li>个性化学习路径规划</li><li>实时书法指导反馈</li><li>传统文化知识普及</li></ul><h2>技术创新</h2><p>该产品采用了最新的计算机视觉和机器学习技术，能够精准识别学生的书写动作，并提供实时的指导建议。</p>', 
2, 'YXRobot产品团队', 'published', '/uploads/news/robot-calligraphy.jpg', '2025-01-20 10:00:00', 1250, 15, 89, 1, 100),

('公司荣获"2024年度教育科技创新奖"', 'YXRobot凭借在教育科技领域的突出贡献，荣获由中国教育技术协会颁发的"2024年度教育科技创新奖"。', 
'<h2>获奖背景</h2><p>此次获奖是对YXRobot在教育科技领域持续创新的认可。公司自成立以来，始终致力于将先进的人工智能技术应用于教育场景。</p><h2>未来展望</h2><p>YXRobot将继续深耕教育科技领域，为更多学校和学生提供优质的智能教育解决方案。</p>', 
1, 'YXRobot市场部', 'published', '/uploads/news/award-2024.jpg', '2025-01-18 14:30:00', 890, 8, 56, 1, 90),

('智慧校园解决方案助力教育数字化转型', '本文介绍YXRobot智慧校园解决方案如何帮助学校实现教育数字化转型，提升教学质量和管理效率。', 
'<h2>解决方案概述</h2><p>YXRobot智慧校园解决方案包含以下核心模块：</p><ul><li>智能教学系统</li><li>校园管理平台</li><li>学生学习分析</li><li>家校互动系统</li></ul><p>通过这些模块的有机结合，为学校提供全方位的数字化教育服务。</p>', 
4, 'YXRobot技术团队', 'published', '/uploads/news/smart-campus.jpg', '2025-01-15 09:15:00', 650, 12, 34, 0, 80),

('新品预告：下一代STEAM教育机器人即将发布', 'YXRobot即将发布全新的STEAM教育机器人，该产品将为学生提供更加丰富的科学、技术、工程、艺术和数学学习体验。', 
'<h2>产品特色</h2><p>新一代STEAM教育机器人具备以下创新功能：</p><ul><li>模块化设计，支持自由组装</li><li>图形化编程界面</li><li>多学科融合教学</li><li>云端学习资源库</li></ul><h2>发布时间</h2><p>产品预计将在2025年3月正式发布，敬请期待！</p>', 
2, 'YXRobot产品团队', 'draft', '/uploads/news/steam-robot-preview.jpg', NULL, 0, 0, 0, 0, 70);
-- 插入新闻
标签关联数据（使用子查询获取ID）
INSERT IGNORE INTO `news_tag_relations` (`news_id`, `tag_id`) 
SELECT n.id, t.id FROM `news` n, `news_tags` t 
WHERE n.title = 'YXRobot发布全新AI书法教学机器人' AND t.name IN ('人工智能', '书法教育', '技术创新', '传统文化');

INSERT IGNORE INTO `news_tag_relations` (`news_id`, `tag_id`) 
SELECT n.id, t.id FROM `news` n, `news_tags` t 
WHERE n.title = '公司荣获"2024年度教育科技创新奖"' AND t.name IN ('教育科技', '技术创新', '行业合作');

INSERT IGNORE INTO `news_tag_relations` (`news_id`, `tag_id`) 
SELECT n.id, t.id FROM `news` n, `news_tags` t 
WHERE n.title = '智慧校园解决方案助力教育数字化转型' AND t.name IN ('教育科技', '数字化转型', '智慧校园');

INSERT IGNORE INTO `news_tag_relations` (`news_id`, `tag_id`) 
SELECT n.id, t.id FROM `news` n, `news_tags` t 
WHERE n.title = '新品预告：下一代STEAM教育机器人即将发布' AND t.name IN ('教育科技', '机器人', 'STEAM教育', '创客教育');

-- 插入一些互动数据示例（使用子查询获取新闻ID）
INSERT IGNORE INTO `news_interactions` (`news_id`, `interaction_type`, `user_id`, `ip_address`) 
SELECT n.id, 'view', NULL, '192.168.1.100' FROM `news` n WHERE n.title = 'YXRobot发布全新AI书法教学机器人';

INSERT IGNORE INTO `news_interactions` (`news_id`, `interaction_type`, `user_id`, `ip_address`) 
SELECT n.id, 'like', NULL, '192.168.1.100' FROM `news` n WHERE n.title = 'YXRobot发布全新AI书法教学机器人';

INSERT IGNORE INTO `news_interactions` (`news_id`, `interaction_type`, `user_id`, `ip_address`) 
SELECT n.id, 'view', NULL, '192.168.1.102' FROM `news` n WHERE n.title = '公司荣获"2024年度教育科技创新奖"';

INSERT IGNORE INTO `news_interactions` (`news_id`, `interaction_type`, `user_id`, `ip_address`) 
SELECT n.id, 'like', NULL, '192.168.1.102' FROM `news` n WHERE n.title = '公司荣获"2024年度教育科技创新奖"';