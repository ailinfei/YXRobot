-- 数据库表结构验证脚本
-- 用于检查charity相关表是否存在以及字段是否匹配实体类定义

-- 1. 检查所有charity相关表是否存在
SELECT 
    TABLE_NAME,
    TABLE_COMMENT,
    ENGINE,
    TABLE_COLLATION
FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME LIKE 'charity_%'
ORDER BY TABLE_NAME;

-- 2. 检查charity_stats表结构
SELECT 
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE,
    COLUMN_DEFAULT,
    COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 'charity_stats'
ORDER BY ORDINAL_POSITION;

-- 3. 检查charity_institutions表结构
SELECT 
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE,
    COLUMN_DEFAULT,
    COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 'charity_institutions'
ORDER BY ORDINAL_POSITION;

-- 4. 检查charity_activities表结构
SELECT 
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE,
    COLUMN_DEFAULT,
    COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 'charity_activities'
ORDER BY ORDINAL_POSITION;

-- 5. 检查charity_projects表结构
SELECT 
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE,
    COLUMN_DEFAULT,
    COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 'charity_projects'
ORDER BY ORDINAL_POSITION;

-- 6. 检查charity_stats_logs表结构
SELECT 
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE,
    COLUMN_DEFAULT,
    COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 'charity_stats_logs'
ORDER BY ORDINAL_POSITION;

-- 7. 检查所有表的索引
SELECT 
    TABLE_NAME,
    INDEX_NAME,
    COLUMN_NAME,
    NON_UNIQUE,
    INDEX_TYPE
FROM INFORMATION_SCHEMA.STATISTICS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME LIKE 'charity_%'
ORDER BY TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX;

-- 8. 检查外键约束
SELECT 
    CONSTRAINT_NAME,
    TABLE_NAME,
    COLUMN_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME LIKE 'charity_%'
AND REFERENCED_TABLE_NAME IS NOT NULL;

-- 9. 验证数据完整性
SELECT 'charity_stats' as table_name, COUNT(*) as record_count FROM charity_stats WHERE is_deleted = 0 OR is_deleted IS NULL
UNION ALL
SELECT 'charity_institutions' as table_name, COUNT(*) as record_count FROM charity_institutions WHERE is_deleted = 0 OR is_deleted IS NULL
UNION ALL
SELECT 'charity_activities' as table_name, COUNT(*) as record_count FROM charity_activities WHERE is_deleted = 0 OR is_deleted IS NULL
UNION ALL
SELECT 'charity_projects' as table_name, COUNT(*) as record_count FROM charity_projects WHERE is_deleted = 0 OR is_deleted IS NULL
UNION ALL
SELECT 'charity_stats_logs' as table_name, COUNT(*) as record_count FROM charity_stats_logs;