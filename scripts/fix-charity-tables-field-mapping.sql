-- 修复公益相关表的字段映射问题
-- 创建时间: 2024-12-20
-- 目的: 解决实体类与数据库表字段不匹配的问题

-- 1. 修复charity_activities表
-- 添加缺失的字段并修正字段名
ALTER TABLE `charity_activities` 
ADD COLUMN `feedback` TEXT COMMENT '活动反馈' AFTER `photos`;

-- 修正字段名映射问题
ALTER TABLE `charity_activities` 
CHANGE COLUMN `create_by` `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
CHANGE COLUMN `update_by` `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
CHANGE COLUMN `deleted` `is_deleted` TINYINT DEFAULT 0 COMMENT '是否已删除：0-否，1-是',
CHANGE COLUMN `create_time` `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
CHANGE COLUMN `update_time` `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';

-- 2. 修复charity_projects表
-- 添加缺失的字段
ALTER TABLE `charity_projects` 
ADD COLUMN `target_beneficiaries` INT DEFAULT 0 COMMENT '目标受益人数' AFTER `end_date`,
ADD COLUMN `actual_beneficiaries` INT DEFAULT 0 COMMENT '实际受益人数' AFTER `target_beneficiaries`,
ADD COLUMN `budget` DECIMAL(12,2) DEFAULT 0.00 COMMENT '项目预算' AFTER `actual_beneficiaries`,
ADD COLUMN `actual_cost` DECIMAL(12,2) DEFAULT 0.00 COMMENT '实际支出' AFTER `budget`,
ADD COLUMN `manager` VARCHAR(100) COMMENT '项目负责人' AFTER `actual_cost`,
ADD COLUMN `manager_contact` VARCHAR(50) COMMENT '负责人联系方式' AFTER `manager`,
ADD COLUMN `institution_id` BIGINT COMMENT '合作机构ID' AFTER `manager_contact`,
ADD COLUMN `region` VARCHAR(100) COMMENT '项目地区' AFTER `institution_id`,
ADD COLUMN `priority` INT DEFAULT 2 COMMENT '项目优先级：1-高，2-中，3-低' AFTER `region`,
ADD COLUMN `progress` INT DEFAULT 0 COMMENT '项目进度百分比' AFTER `priority`,
ADD COLUMN `notes` TEXT COMMENT '备注信息' AFTER `tags`;

-- 修正现有字段映射
UPDATE `charity_projects` SET 
    `target_beneficiaries` = `beneficiaries`,
    `budget` = `target_amount`,
    `actual_cost` = `raised_amount`,
    `manager` = `contact_person`,
    `manager_contact` = `contact_phone`,
    `region` = `location`;

-- 3. 验证修复结果
-- 检查charity_stats表的deleted字段
SELECT 'charity_stats deleted field' as table_field, 
       COUNT(*) as field_exists 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'charity_stats' 
  AND COLUMN_NAME = 'deleted';

-- 检查charity_activities表的字段
SELECT 'charity_activities fields' as table_info, 
       COUNT(*) as total_fields 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'charity_activities';

-- 检查charity_projects表的字段
SELECT 'charity_projects fields' as table_info, 
       COUNT(*) as total_fields 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'charity_projects';

-- 显示修复后的表结构
DESCRIBE `charity_activities`;
DESCRIBE `charity_projects`;
DESCRIBE `charity_institutions`;