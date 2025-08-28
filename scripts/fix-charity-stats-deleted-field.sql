-- 修复charity_stats表结构 - 添加deleted字段
-- 创建时间: 2024-12-20
-- 目的: 解决"Unknown column 'deleted' in 'field list'"错误

-- 检查并添加deleted字段到charity_stats表
ALTER TABLE `charity_stats` 
ADD COLUMN `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除：0-否，1-是'
AFTER `version`;

-- 为deleted字段添加索引以提高查询性能
ALTER TABLE `charity_stats` 
ADD INDEX `idx_deleted` (`deleted`);

-- 验证表结构修改
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_DEFAULT, COLUMN_COMMENT 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'charity_stats' 
  AND COLUMN_NAME = 'deleted';

-- 显示修复后的表结构
DESCRIBE `charity_stats`;