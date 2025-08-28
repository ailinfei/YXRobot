-- 修复公益活动表结构，添加缺失的字段
-- 执行时间: 2025-08-20

USE YXRobot;

-- 添加缺失的字段到 charity_activities 表
ALTER TABLE `charity_activities` 
ADD COLUMN `start_time` TIME DEFAULT NULL COMMENT '活动开始时间' AFTER `date`,
ADD COLUMN `end_time` TIME DEFAULT NULL COMMENT '活动结束时间' AFTER `start_time`,
ADD COLUMN `target_participants` INT DEFAULT 0 COMMENT '目标参与人数' AFTER `participants`,
ADD COLUMN `manager` VARCHAR(100) DEFAULT NULL COMMENT '活动负责人' AFTER `organizer`,
ADD COLUMN `manager_contact` VARCHAR(50) DEFAULT NULL COMMENT '负责人联系方式' AFTER `manager`,
ADD COLUMN `volunteer_needed` INT DEFAULT 0 COMMENT '需要志愿者数量' AFTER `manager_contact`,
ADD COLUMN `actual_volunteers` INT DEFAULT 0 COMMENT '实际志愿者数量' AFTER `volunteer_needed`,
ADD COLUMN `achievements` TEXT COMMENT '活动成果' AFTER `actual_volunteers`,
ADD COLUMN `notes` TEXT COMMENT '备注信息' AFTER `achievements`;

-- 重命名字段以保持一致性
ALTER TABLE `charity_activities` 
CHANGE COLUMN `feedback` `notes_old` TEXT COMMENT '旧的反馈字段（待删除）',
CHANGE COLUMN `created_by` `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
CHANGE COLUMN `updated_by` `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
CHANGE COLUMN `is_deleted` `deleted` TINYINT DEFAULT 0 COMMENT '是否已删除：0-否，1-是',
CHANGE COLUMN `created_at` `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
CHANGE COLUMN `updated_at` `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';

-- 更新索引
ALTER TABLE `charity_activities` DROP INDEX IF EXISTS `idx_is_deleted`;
ALTER TABLE `charity_activities` ADD INDEX `idx_deleted` (`deleted`);

-- 插入一些测试数据
INSERT INTO `charity_activities` (
  `project_id`, `title`, `description`, `type`, `date`, `start_time`, `end_time`,
  `location`, `participants`, `target_participants`, `organizer`, `manager`, `manager_contact`,
  `status`, `budget`, `actual_cost`, `volunteer_needed`, `actual_volunteers`,
  `achievements`, `notes`, `create_time`, `update_time`, `create_by`, `update_by`, `deleted`
) VALUES 
(1, '爱心图书捐赠活动', '为山区学校捐赠图书和学习用品', 'donation', '2024-12-15', '09:00:00', '17:00:00',
 '四川省凉山州昭觉县第一小学', 120, 100, 'YXRobot公益基金会', '张明', '13800138001',
 'completed', 15000.00, 14500.00, 10, 12,
 '成功捐赠图书2000册，学习用品500套，受到师生热烈欢迎', '活动圆满成功，媒体报道良好', NOW(), NOW(), 1, 1, 0),

(2, '科技助学培训', '为乡村教师提供信息技术培训', 'training', '2024-12-20', '14:00:00', '18:00:00',
 '贵州省毕节市威宁县教育局', 45, 50, 'YXRobot教育基金', '李华', '13900139002',
 'completed', 8000.00, 7800.00, 5, 6,
 '培训教师45名，提升了乡村教师的信息技术应用能力', '培训效果显著，教师反馈积极', NOW(), NOW(), 1, 1, 0),

(3, '新年关爱行动', '为孤寡老人送温暖活动', 'visit', '2025-01-15', '10:00:00', '16:00:00',
 '成都市武侯区养老院', 80, 100, 'YXRobot志愿者协会', '王芳', '13700137003',
 'planned', 5000.00, 0.00, 20, 0,
 '', '计划为老人们准备年货和文艺表演', NOW(), NOW(), 1, 1, 0),

(4, '环保宣传活动', '社区环保知识宣传和垃圾分类指导', 'event', '2025-02-01', '08:30:00', '12:00:00',
 '成都市高新区天府软件园', 200, 150, 'YXRobot环保志愿队', '陈强', '13600136004',
 'planned', 3000.00, 0.00, 15, 0,
 '', '提高社区居民环保意识，推广垃圾分类', NOW(), NOW(), 1, 1, 0),

(5, '儿童节关爱活动', '为留守儿童举办儿童节庆祝活动', 'event', '2024-06-01', '09:00:00', '15:00:00',
 '云南省昆明市东川区希望小学', 150, 120, 'YXRobot儿童关爱基金', '刘敏', '13500135005',
 'completed', 12000.00, 11800.00, 25, 28,
 '为150名留守儿童送去节日礼物，组织了丰富的文体活动', '孩子们度过了难忘的儿童节', NOW(), NOW(), 1, 1, 0);

-- 验证数据插入
SELECT COUNT(*) as total_activities FROM charity_activities WHERE deleted = 0;
SELECT * FROM charity_activities WHERE deleted = 0 ORDER BY create_time DESC LIMIT 3;