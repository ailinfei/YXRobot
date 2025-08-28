-- 新闻管理系统数据清理和归档脚本
-- 
-- @author YXRobot开发团队
-- @version 1.0.0
-- @since 2025-01-25

-- 设置安全模式（防止误删除）
SET SQL_SAFE_UPDATES = 1;

-- 创建数据归档表（如果不存在）
CREATE TABLE IF NOT EXISTS news_archive (
    id BIGINT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    excerpt TEXT,
    content LONGTEXT NOT NULL,
    category_id BIGINT,
    category_name VARCHAR(100),
    author VARCHAR(100) NOT NULL,
    status ENUM('DRAFT', 'PUBLISHED', 'OFFLINE') NOT NULL,
    views INT DEFAULT 0,
    comments INT DEFAULT 0,
    likes INT DEFAULT 0,
    is_featured BOOLEAN DEFAULT FALSE,
    cover_image VARCHAR(500),
    publish_time DATETIME,
    sort_order INT DEFAULT 0,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    archived_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    archive_reason VARCHAR(200)
);

CREATE TABLE IF NOT EXISTS news_interactions_archive (
    id BIGINT PRIMARY KEY,
    news_id BIGINT,
    interaction_type ENUM('VIEW', 'LIKE', 'COMMENT', 'SHARE') NOT NULL,
    user_ip VARCHAR(45),
    user_agent TEXT,
    created_at DATETIME NOT NULL,
    archived_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 数据清理配置
SET @cleanup_days = 365; -- 保留365天内的数据
SET @archive_days = 180;  -- 归档180天前的已下线新闻
SET @interaction_cleanup_days = 90; -- 清理90天前的互动记录

-- 1. 归档长期下线的新闻
SELECT '=== 开始归档长期下线的新闻 ===' as cleanup_step;

INSERT INTO news_archive (
    id, title, excerpt, content, category_id, category_name, author, 
    status, views, comments, likes, is_featured, cover_image, 
    publish_time, sort_order, created_at, updated_at, archive_reason
)
SELECT 
    n.id, n.title, n.excerpt, n.content, n.category_id, nc.name, n.author,
    n.status, n.views, n.comments, n.likes, n.is_featured, n.cover_image,
    n.publish_time, n.sort_order, n.created_at, n.updated_at,
    CONCAT('长期下线归档 - 下线时间超过', @archive_days, '天')
FROM news n
LEFT JOIN news_categories nc ON n.category_id = nc.id
WHERE n.status = 'OFFLINE' 
  AND n.updated_at < DATE_SUB(NOW(), INTERVAL @archive_days DAY)
  AND n.id NOT IN (SELECT id FROM news_archive);

SELECT ROW_COUNT() as archived_offline_news_count;

-- 2. 归档相关的标签关联
INSERT INTO news_tag_relations_archive (news_id, tag_id, archived_at)
SELECT ntr.news_id, ntr.tag_id, NOW()
FROM news_tag_relations ntr
WHERE ntr.news_id IN (
    SELECT id FROM news_archive 
    WHERE archived_at >= DATE_SUB(NOW(), INTERVAL 1 HOUR)
);

-- 3. 归档相关的互动记录
INSERT INTO news_interactions_archive (
    id, news_id, interaction_type, user_ip, user_agent, created_at
)
SELECT 
    ni.id, ni.news_id, ni.interaction_type, ni.user_ip, ni.user_agent, ni.created_at
FROM news_interactions ni
WHERE ni.news_id IN (
    SELECT id FROM news_archive 
    WHERE archived_at >= DATE_SUB(NOW(), INTERVAL 1 HOUR)
);

-- 4. 删除已归档的新闻数据
DELETE FROM news_tag_relations 
WHERE news_id IN (
    SELECT id FROM news_archive 
    WHERE archived_at >= DATE_SUB(NOW(), INTERVAL 1 HOUR)
);

DELETE FROM news_interactions 
WHERE news_id IN (
    SELECT id FROM news_archive 
    WHERE archived_at >= DATE_SUB(NOW(), INTERVAL 1 HOUR)
);

DELETE FROM news_status_logs 
WHERE news_id IN (
    SELECT id FROM news_archive 
    WHERE archived_at >= DATE_SUB(NOW(), INTERVAL 1 HOUR)
);

DELETE FROM news 
WHERE id IN (
    SELECT id FROM news_archive 
    WHERE archived_at >= DATE_SUB(NOW(), INTERVAL 1 HOUR)
);

-- 5. 清理过期的互动记录（保留重要统计数据）
SELECT '=== 开始清理过期互动记录 ===' as cleanup_step;

-- 先备份要删除的互动统计数据
CREATE TEMPORARY TABLE temp_interaction_stats AS
SELECT 
    news_id,
    interaction_type,
    COUNT(*) as interaction_count,
    MIN(created_at) as first_interaction,
    MAX(created_at) as last_interaction
FROM news_interactions 
WHERE created_at < DATE_SUB(NOW(), INTERVAL @interaction_cleanup_days DAY)
GROUP BY news_id, interaction_type;

-- 删除过期的详细互动记录
DELETE FROM news_interactions 
WHERE created_at < DATE_SUB(NOW(), INTERVAL @interaction_cleanup_days DAY);

SELECT ROW_COUNT() as deleted_interactions_count;

-- 6. 清理过期的状态变更日志
SELECT '=== 开始清理过期状态日志 ===' as cleanup_step;

DELETE FROM news_status_logs 
WHERE created_at < DATE_SUB(NOW(), INTERVAL @cleanup_days DAY);

SELECT ROW_COUNT() as deleted_status_logs_count;

-- 7. 清理未使用的标签
SELECT '=== 开始清理未使用的标签 ===' as cleanup_step;

-- 标记未使用的标签（可选择是否删除）
SELECT 
    nt.id,
    nt.name,
    nt.created_at,
    '未使用标签' as cleanup_reason
FROM news_tags nt
LEFT JOIN news_tag_relations ntr ON nt.id = ntr.tag_id
WHERE ntr.tag_id IS NULL
  AND nt.created_at < DATE_SUB(NOW(), INTERVAL 30 DAY);

-- 如果确认要删除未使用的标签，取消下面的注释
-- DELETE FROM news_tags 
-- WHERE id IN (
--     SELECT nt.id
--     FROM news_tags nt
--     LEFT JOIN news_tag_relations ntr ON nt.id = ntr.tag_id
--     WHERE ntr.tag_id IS NULL
--       AND nt.created_at < DATE_SUB(NOW(), INTERVAL 30 DAY)
-- );

-- 8. 清理重复的互动记录
SELECT '=== 开始清理重复互动记录 ===' as cleanup_step;

-- 查找重复的互动记录
CREATE TEMPORARY TABLE temp_duplicate_interactions AS
SELECT 
    news_id,
    interaction_type,
    user_ip,
    DATE(created_at) as interaction_date,
    COUNT(*) as duplicate_count,
    MIN(id) as keep_id
FROM news_interactions
GROUP BY news_id, interaction_type, user_ip, DATE(created_at)
HAVING COUNT(*) > 1;

-- 删除重复记录（保留最早的一条）
DELETE ni FROM news_interactions ni
INNER JOIN temp_duplicate_interactions tdi 
    ON ni.news_id = tdi.news_id 
    AND ni.interaction_type = tdi.interaction_type
    AND ni.user_ip = tdi.user_ip
    AND DATE(ni.created_at) = tdi.interaction_date
WHERE ni.id != tdi.keep_id;

SELECT ROW_COUNT() as deleted_duplicate_interactions;

-- 9. 更新新闻统计数据
SELECT '=== 开始更新新闻统计数据 ===' as cleanup_step;

-- 重新计算浏览量
UPDATE news n
SET views = (
    SELECT COUNT(*)
    FROM news_interactions ni
    WHERE ni.news_id = n.id AND ni.interaction_type = 'VIEW'
);

-- 重新计算点赞数
UPDATE news n
SET likes = (
    SELECT COUNT(*)
    FROM news_interactions ni
    WHERE ni.news_id = n.id AND ni.interaction_type = 'LIKE'
);

-- 重新计算评论数（如果有评论表的话）
-- UPDATE news n
-- SET comments = (
--     SELECT COUNT(*)
--     FROM news_comments nc
--     WHERE nc.news_id = n.id AND nc.is_deleted = FALSE
-- );

-- 10. 优化表结构
SELECT '=== 开始优化表结构 ===' as cleanup_step;

-- 分析表以更新统计信息
ANALYZE TABLE news;
ANALYZE TABLE news_categories;
ANALYZE TABLE news_tags;
ANALYZE TABLE news_tag_relations;
ANALYZE TABLE news_interactions;
ANALYZE TABLE news_status_logs;

-- 优化表（重建索引）
OPTIMIZE TABLE news;
OPTIMIZE TABLE news_interactions;
OPTIMIZE TABLE news_tag_relations;

-- 11. 生成清理报告
SELECT '=== 数据清理报告 ===' as cleanup_report;

SELECT 
    '清理完成统计' as report_item,
    (SELECT COUNT(*) FROM news) as current_news_count,
    (SELECT COUNT(*) FROM news_archive) as archived_news_count,
    (SELECT COUNT(*) FROM news_interactions) as current_interactions_count,
    (SELECT COUNT(*) FROM news_interactions_archive) as archived_interactions_count,
    (SELECT COUNT(*) FROM news_tags) as current_tags_count,
    (SELECT COUNT(*) FROM news_status_logs) as current_status_logs_count;

-- 检查数据完整性
SELECT 
    '数据完整性检查' as report_item,
    (SELECT COUNT(*) FROM news WHERE category_id NOT IN (SELECT id FROM news_categories)) as orphaned_news,
    (SELECT COUNT(*) FROM news_tag_relations WHERE news_id NOT IN (SELECT id FROM news)) as orphaned_relations,
    (SELECT COUNT(*) FROM news_interactions WHERE news_id NOT IN (SELECT id FROM news)) as orphaned_interactions;

-- 存储空间统计
SELECT 
    table_name,
    ROUND(((data_length + index_length) / 1024 / 1024), 2) as size_mb,
    table_rows
FROM information_schema.tables 
WHERE table_schema = DATABASE() 
  AND table_name LIKE 'news%'
ORDER BY (data_length + index_length) DESC;

-- 清理完成
SELECT 
    'NEWS_DATA_CLEANUP_COMPLETED' as cleanup_status,
    NOW() as completion_time;

-- 重置安全模式
SET SQL_SAFE_UPDATES = 0;