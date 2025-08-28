-- 修复所有公益相关表的字段名称不一致问题
-- 执行时间: 2025-08-20

USE YXRobot;

-- 1. 修复 charity_institutions 表
ALTER TABLE `charity_institutions` 
ADD COLUMN `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' AFTER `is_deleted`,
ADD COLUMN `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' AFTER `create_time`,
ADD COLUMN `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID' AFTER `update_time`,
ADD COLUMN `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID' AFTER `create_by`,
ADD COLUMN `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除：0-否，1-是' AFTER `update_by`;

-- 复制数据到新字段
UPDATE `charity_institutions` SET 
    `create_time` = `created_at`,
    `update_time` = `updated_at`,
    `create_by` = `created_by`,
    `update_by` = `updated_by`,
    `deleted` = `is_deleted`;

-- 2. 修复 charity_projects 表（如果存在字段不一致）
ALTER TABLE `charity_projects` 
ADD COLUMN `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' AFTER `is_deleted`,
ADD COLUMN `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' AFTER `create_time`,
ADD COLUMN `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID' AFTER `update_time`,
ADD COLUMN `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID' AFTER `create_by`,
ADD COLUMN `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除：0-否，1-是' AFTER `update_by`;

-- 复制数据到新字段
UPDATE `charity_projects` SET 
    `create_time` = `created_at`,
    `update_time` = `updated_at`,
    `create_by` = `created_by`,
    `update_by` = `updated_by`,
    `deleted` = `is_deleted`;

-- 3. 修复 charity_stats_logs 表
ALTER TABLE `charity_stats_logs` 
ADD COLUMN `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' AFTER `created_at`,
ADD COLUMN `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' AFTER `create_time`,
ADD COLUMN `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID' AFTER `update_time`,
ADD COLUMN `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID' AFTER `create_by`,
ADD COLUMN `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除：0-否，1-是' AFTER `update_by`;

-- 复制数据到新字段
UPDATE `charity_stats_logs` SET 
    `create_time` = `created_at`,
    `create_by` = `operator_id`;

-- 4. 修复 charity_stats 表
ALTER TABLE `charity_stats` 
ADD COLUMN `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' AFTER `updated_at`,
ADD COLUMN `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' AFTER `create_time`,
ADD COLUMN `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID' AFTER `update_time`,
ADD COLUMN `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID' AFTER `create_by`,
ADD COLUMN `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除：0-否，1-是' AFTER `update_by`;

-- 复制数据到新字段
UPDATE `charity_stats` SET 
    `create_time` = `created_at`,
    `update_time` = `updated_at`;

-- 5. 插入一些测试数据到 charity_institutions 表
INSERT INTO `charity_institutions` (
    `name`, `type`, `location`, `address`, `contact_person`, `contact_phone`, `email`,
    `student_count`, `cooperation_date`, `status`, `device_count`, `last_visit_date`, `notes`,
    `create_time`, `update_time`, `create_by`, `update_by`, `deleted`
) VALUES 
('四川省凉山州昭觉县第一小学', 'school', '四川省凉山州', '四川省凉山州昭觉县解放路123号', '张校长', '13800138001', 'zhaojue1@edu.cn',
 450, '2023-03-15', 'active', 25, '2024-12-15', '山区重点扶贫学校，教学设备较为落后', NOW(), NOW(), 1, 1, 0),

('贵州省毕节市威宁县教育局', 'government', '贵州省毕节市', '贵州省毕节市威宁县政府大楼3楼', '李局长', '13900139002', 'weining@edu.gov.cn',
 0, '2023-06-20', 'active', 0, '2024-12-20', '负责协调全县教育资源配置', NOW(), NOW(), 1, 1, 0),

('成都市武侯区养老院', 'elderly_care', '四川省成都市', '成都市武侯区科华北路88号', '王院长', '13700137003', 'wuhou@elderly.org',
 120, '2022-11-10', 'active', 8, '2024-11-30', '为孤寡老人提供养老服务', NOW(), NOW(), 1, 1, 0),

('云南省昆明市东川区希望小学', 'school', '云南省昆明市', '云南省昆明市东川区希望路66号', '刘校长', '13500135005', 'dongchuan@hope.edu.cn',
 280, '2023-09-01', 'active', 15, '2024-06-01', '留守儿童较多的山区学校', NOW(), NOW(), 1, 1, 0),

('成都市高新区天府软件园社区', 'community', '四川省成都市', '成都市高新区天府大道中段1366号', '陈主任', '13600136004', 'tianfu@community.org',
 5000, '2024-01-15', 'active', 3, '2024-10-15', '高新技术产业园区社区', NOW(), NOW(), 1, 1, 0);

-- 6. 插入一些测试数据到 charity_projects 表
INSERT INTO `charity_projects` (
    `name`, `description`, `type`, `status`, `start_date`, `end_date`, `target_amount`, `raised_amount`,
    `beneficiaries`, `location`, `organizer`, `contact_person`, `contact_phone`, `images`, `tags`,
    `create_time`, `update_time`, `create_by`, `update_by`, `deleted`
) VALUES 
('山区教育援助计划', '为偏远山区学校提供教学设备和师资培训', 'education', 'active', '2024-01-01', '2024-12-31', 500000.00, 320000.00,
 1200, '四川省凉山州', 'YXRobot公益基金会', '张明', '13800138001', '[]', '["教育", "扶贫", "山区"]',
 NOW(), NOW(), 1, 1, 0),

('科技助学项目', '为乡村教师提供信息技术培训和设备支持', 'training', 'active', '2024-03-01', '2024-11-30', 200000.00, 150000.00,
 500, '贵州省毕节市', 'YXRobot教育基金', '李华', '13900139002', '[]', '["科技", "培训", "乡村"]',
 NOW(), NOW(), 1, 1, 0),

('关爱老人温暖行动', '为孤寡老人提供生活用品和精神关怀', 'donation', 'completed', '2024-01-01', '2024-12-31', 100000.00, 95000.00,
 300, '成都市武侯区', 'YXRobot志愿者协会', '王芳', '13700137003', '[]', '["养老", "关爱", "志愿服务"]',
 NOW(), NOW(), 1, 1, 0);

-- 验证数据插入
SELECT COUNT(*) as total_institutions FROM charity_institutions WHERE deleted = 0;
SELECT COUNT(*) as total_projects FROM charity_projects WHERE deleted = 0;
SELECT * FROM charity_institutions WHERE deleted = 0 ORDER BY create_time DESC LIMIT 3;
SELECT * FROM charity_projects WHERE deleted = 0 ORDER BY create_time DESC LIMIT 3;