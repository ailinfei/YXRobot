-- 新闻管理系统数据验证脚本
-- 
-- @author YXRobot开发团队
-- @version 1.0.0
-- @since 2025-01-25

-- 验证新闻分类数据
SELECT '=== 新闻分类数据验证 ===' as validation_section;

SELECT 
    '分类数据统计' as check_item,
    COUNT(*) as total_count,
    COUNT(CASE WHEN is_enabled = true THEN 1 END) as enabled_count,
    COUNT(CASE WHEN is_enabled = false THEN 1 END) as disabled_count
FROM news_categories;

-- 检查分类名称是否有重复
SELECT 
    '分类名称重复检查' as check_item,
    name,
    COUNT(*) as duplicate_count
FROM news_categories 
GROUP BY name 
HAVING COUNT(*) > 1;

-- 验证新闻标签数据
SELECT '=== 新闻标签数据验证 ===' as validation_section;

SELECT 
    '标签数据统计' as check_item,
    COUNT(*) as total_count,
    COUNT(DISTINCT color) as unique_colors
FROM news_tags;

-- 检查标签名称是否有重复
SELECT 
    '标签名称重复检查' as check_item,
    name,
    COUNT(*) as duplicate_count
FROM news_tags 
GROUP BY name 
HAVING COUNT(*) > 1;

-- 验证新闻数据
SELECT '=== 新闻数据验证 ===' as validation_section;

SELECT 
    '新闻数据统计' as check_item,
    COUNT(*) as total_count,
    COUNT(CASE WHEN status = 'PUBLISHED' THEN 1 END) as published_count,
    COUNT(CASE WHEN status = 'DRAFT' THEN 1 END) as draft_count,
    COUNT(CASE WHEN status = 'OFFLINE' THEN 1 END) as offline_count,
    COUNT(CASE WHEN is_featured = true THEN 1 END) as featured_count
FROM news;

-- 检查新闻标题是否有重复
SELECT 
    '新闻标题重复检查' as check_item,
    title,
    COUNT(*) as duplicate_count
FROM news 
GROUP BY title 
HAVING COUNT(*) > 1;

-- 检查新闻分类关联是否正确
SELECT 
    '新闻分类关联检查' as check_item,
    COUNT(*) as news_with_invalid_category
FROM news n
LEFT JOIN news_categories nc ON n.category_id = nc.id
WHERE nc.id IS NULL;

-- 验证新闻标签关联数据
SELECT '=== 新闻标签关联验证 ===' as validation_section;

SELECT 
    '标签关联统计' as check_item,
    COUNT(*) as total_relations,
    COUNT(DISTINCT news_id) as news_with_tags,
    COUNT(DISTINCT tag_id) as used_tags
FROM news_tag_relations;

-- 检查标签关联的数据完整性
SELECT 
    '无效新闻ID关联' as check_item,
    COUNT(*) as invalid_news_relations
FROM news_tag_relations ntr
LEFT JOIN news n ON ntr.news_id = n.id
WHERE n.id IS NULL;

SELECT 
    '无效标签ID关联' as check_item,
    COUNT(*) as invalid_tag_relations
FROM news_tag_relations ntr
LEFT JOIN news_tags nt ON ntr.tag_id = nt.id
WHERE nt.id IS NULL;

-- 验证新闻互动数据
SELECT '=== 新闻互动数据验证 ===' as validation_section;

SELECT 
    '互动数据统计' as check_item,
    COUNT(*) as total_interactions,
    COUNT(CASE WHEN interaction_type = 'VIEW' THEN 1 END) as view_count,
    COUNT(CASE WHEN interaction_type = 'LIKE' THEN 1 END) as like_count,
    COUNT(CASE WHEN interaction_type = 'COMMENT' THEN 1 END) as comment_count,
    COUNT(CASE WHEN interaction_type = 'SHARE' THEN 1 END) as share_count
FROM news_interactions;

-- 检查互动数据的新闻ID是否有效
SELECT 
    '无效新闻ID互动' as check_item,
    COUNT(*) as invalid_news_interactions
FROM news_interactions ni
LEFT JOIN news n ON ni.news_id = n.id
WHERE n.id IS NULL;

-- 验证新闻状态日志
SELECT '=== 新闻状态日志验证 ===' as validation_section;

SELECT 
    '状态日志统计' as check_item,
    COUNT(*) as total_logs,
    COUNT(DISTINCT news_id) as news_with_logs,
    COUNT(DISTINCT operator) as operators_count
FROM news_status_logs;

-- 检查状态日志的新闻ID是否有效
SELECT 
    '无效新闻ID日志' as check_item,
    COUNT(*) as invalid_news_logs
FROM news_status_logs nsl
LEFT JOIN news n ON nsl.news_id = n.id
WHERE n.id IS NULL;

-- 数据一致性检查
SELECT '=== 数据一致性检查 ===' as validation_section;

-- 检查新闻浏览量与互动记录是否一致
SELECT 
    '浏览量一致性检查' as check_item,
    n.id as news_id,
    n.title,
    n.views as recorded_views,
    COALESCE(ni.actual_views, 0) as actual_views,
    (n.views - COALESCE(ni.actual_views, 0)) as difference
FROM news n
LEFT JOIN (
    SELECT 
        news_id, 
        COUNT(*) as actual_views
    FROM news_interactions 
    WHERE interaction_type = 'VIEW'
    GROUP BY news_id
) ni ON n.id = ni.news_id
WHERE n.views != COALESCE(ni.actual_views, 0);

-- 检查新闻点赞数与互动记录是否一致
SELECT 
    '点赞数一致性检查' as check_item,
    n.id as news_id,
    n.title,
    n.likes as recorded_likes,
    COALESCE(ni.actual_likes, 0) as actual_likes,
    (n.likes - COALESCE(ni.actual_likes, 0)) as difference
FROM news n
LEFT JOIN (
    SELECT 
        news_id, 
        COUNT(*) as actual_likes
    FROM news_interactions 
    WHERE interaction_type = 'LIKE'
    GROUP BY news_id
) ni ON n.id = ni.news_id
WHERE n.likes != COALESCE(ni.actual_likes, 0);

-- 数据质量检查
SELECT '=== 数据质量检查 ===' as validation_section;

-- 检查新闻内容长度
SELECT 
    '新闻内容长度检查' as check_item,
    COUNT(*) as news_count,
    AVG(LENGTH(content)) as avg_content_length,
    MIN(LENGTH(content)) as min_content_length,
    MAX(LENGTH(content)) as max_content_length
FROM news;

-- 检查新闻摘要长度
SELECT 
    '新闻摘要长度检查' as check_item,
    COUNT(*) as news_with_excerpt,
    AVG(LENGTH(excerpt)) as avg_excerpt_length,
    MIN(LENGTH(excerpt)) as min_excerpt_length,
    MAX(LENGTH(excerpt)) as max_excerpt_length
FROM news 
WHERE excerpt IS NOT NULL AND excerpt != '';

-- 检查新闻标题长度
SELECT 
    '新闻标题长度检查' as check_item,
    COUNT(*) as news_count,
    AVG(LENGTH(title)) as avg_title_length,
    MIN(LENGTH(title)) as min_title_length,
    MAX(LENGTH(title)) as max_title_length
FROM news;

-- 检查发布时间的合理性
SELECT 
    '发布时间合理性检查' as check_item,
    COUNT(*) as total_published,
    COUNT(CASE WHEN publish_time > created_at THEN 1 END) as future_publish,
    COUNT(CASE WHEN publish_time < created_at THEN 1 END) as past_publish
FROM news 
WHERE status = 'PUBLISHED' AND publish_time IS NOT NULL;

-- 性能相关检查
SELECT '=== 性能相关检查 ===' as validation_section;

-- 检查需要添加索引的字段
SELECT 
    '分类使用频率' as check_item,
    nc.name as category_name,
    COUNT(n.id) as news_count
FROM news_categories nc
LEFT JOIN news n ON nc.id = n.category_id
GROUP BY nc.id, nc.name
ORDER BY news_count DESC;

-- 检查标签使用频率
SELECT 
    '标签使用频率' as check_item,
    nt.name as tag_name,
    COUNT(ntr.news_id) as usage_count
FROM news_tags nt
LEFT JOIN news_tag_relations ntr ON nt.id = ntr.tag_id
GROUP BY nt.id, nt.name
ORDER BY usage_count DESC;

-- 最终验证总结
SELECT '=== 验证总结 ===' as validation_section;

SELECT 
    '数据表记录统计' as summary_item,
    (SELECT COUNT(*) FROM news_categories) as categories,
    (SELECT COUNT(*) FROM news_tags) as tags,
    (SELECT COUNT(*) FROM news) as news,
    (SELECT COUNT(*) FROM news_tag_relations) as tag_relations,
    (SELECT COUNT(*) FROM news_interactions) as interactions,
    (SELECT COUNT(*) FROM news_status_logs) as status_logs;

-- 检查是否有孤立数据
SELECT 
    '孤立数据检查' as summary_item,
    (SELECT COUNT(*) FROM news WHERE category_id NOT IN (SELECT id FROM news_categories)) as orphaned_news,
    (SELECT COUNT(*) FROM news_tag_relations WHERE news_id NOT IN (SELECT id FROM news)) as orphaned_tag_relations,
    (SELECT COUNT(*) FROM news_interactions WHERE news_id NOT IN (SELECT id FROM news)) as orphaned_interactions,
    (SELECT COUNT(*) FROM news_status_logs WHERE news_id NOT IN (SELECT id FROM news)) as orphaned_status_logs;

SELECT 'NEWS_DATA_VALIDATION_COMPLETED' as validation_status;