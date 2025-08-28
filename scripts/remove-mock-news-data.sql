-- 清理模拟新闻数据脚本
-- 
-- @author YXRobot开发团队
-- @version 1.0.0
-- @since 2025-01-25
-- 
-- 此脚本用于清理系统中的模拟新闻数据，确保只保留真实的新闻内容
-- 根据项目需求，禁止使用模拟数据，所有新闻内容应该是真实的

-- 设置安全模式
SET SQL_SAFE_UPDATES = 0;

-- 显示清理前的数据统计
SELECT '=== 清理前数据统计 ===' as step;
SELECT 
    (SELECT COUNT(*) FROM news) as news_count,
    (SELECT COUNT(*) FROM news_interactions) as interactions_count,
    (SELECT COUNT(*) FROM news_tag_relations) as tag_relations_count,
    (SELECT COUNT(*) FROM news_status_logs) as status_logs_count;

-- 1. 删除所有新闻互动记录
DELETE FROM news_interactions;
SELECT '删除新闻互动记录完成' as step, ROW_COUNT() as deleted_count;

-- 2. 删除所有新闻标签关联
DELETE FROM news_tag_relations;
SELECT '删除新闻标签关联完成' as step, ROW_COUNT() as deleted_count;

-- 3. 删除所有新闻状态日志
DELETE FROM news_status_logs;
SELECT '删除新闻状态日志完成' as step, ROW_COUNT() as deleted_count;

-- 4. 删除所有新闻记录
DELETE FROM news;
SELECT '删除新闻记录完成' as step, ROW_COUNT() as deleted_count;

-- 5. 重置自增ID（可选）
ALTER TABLE news AUTO_INCREMENT = 1;
ALTER TABLE news_interactions AUTO_INCREMENT = 1;
ALTER TABLE news_status_logs AUTO_INCREMENT = 1;

-- 显示清理后的数据统计
SELECT '=== 清理后数据统计 ===' as step;
SELECT 
    (SELECT COUNT(*) FROM news) as news_count,
    (SELECT COUNT(*) FROM news_interactions) as interactions_count,
    (SELECT COUNT(*) FROM news_tag_relations) as tag_relations_count,
    (SELECT COUNT(*) FROM news_status_logs) as status_logs_count;

-- 验证分类和标签数据仍然存在（这些是必要的基础数据）
SELECT '=== 验证基础数据 ===' as step;
SELECT 
    (SELECT COUNT(*) FROM news_categories) as categories_count,
    (SELECT COUNT(*) FROM news_tags) as tags_count;

SELECT '模拟新闻数据清理完成！' as result;
SELECT '现在可以通过管理后台创建真实的新闻内容。' as instruction;

-- 恢复安全模式
SET SQL_SAFE_UPDATES = 1;