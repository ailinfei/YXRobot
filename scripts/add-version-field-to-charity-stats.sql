-- 为charity_stats表添加version字段用于乐观锁控制
-- 执行时间: 2025-08-20

-- 检查version字段是否已存在
SELECT COUNT(*) as field_exists 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 'charity_stats' 
AND COLUMN_NAME = 'version';

-- 如果字段不存在，则添加version字段
ALTER TABLE charity_stats 
ADD COLUMN IF NOT EXISTS version INT DEFAULT 1 COMMENT '版本号，用于乐观锁控制' 
AFTER is_current;

-- 为现有记录设置默认版本号
UPDATE charity_stats 
SET version = 1 
WHERE version IS NULL;

-- 验证字段添加成功
SELECT 
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE,
    COLUMN_DEFAULT,
    COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 'charity_stats' 
AND COLUMN_NAME = 'version';

-- 显示更新后的表结构
DESCRIBE charity_stats;