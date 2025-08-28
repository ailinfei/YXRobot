-- 修复公益统计数据表结构
-- 确保表结构与实体类和Mapper一致

-- 检查并修复charity_stats表结构
ALTER TABLE `charity_stats` 
MODIFY COLUMN `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
MODIFY COLUMN `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
ADD COLUMN IF NOT EXISTS `version` INT DEFAULT 1 COMMENT '版本号，用于乐观锁',
ADD COLUMN IF NOT EXISTS `deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是';

-- 如果表不存在，创建标准的charity_stats表
CREATE TABLE IF NOT EXISTS `charity_stats` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '统计ID，主键',
  `total_beneficiaries` INT NOT NULL DEFAULT 0 COMMENT '累计受益人数',
  `total_institutions` INT NOT NULL DEFAULT 0 COMMENT '合作机构总数',
  `cooperating_institutions` INT NOT NULL DEFAULT 0 COMMENT '活跃合作机构数',
  `total_volunteers` INT NOT NULL DEFAULT 0 COMMENT '志愿者总数',
  `total_raised` DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '累计筹集金额，单位：元',
  `total_donated` DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '累计捐赠金额，单位：元',
  `total_projects` INT NOT NULL DEFAULT 0 COMMENT '项目总数',
  `active_projects` INT NOT NULL DEFAULT 0 COMMENT '进行中项目数',
  `completed_projects` INT NOT NULL DEFAULT 0 COMMENT '已完成项目数',
  `total_activities` INT NOT NULL DEFAULT 0 COMMENT '活动总数',
  `this_month_activities` INT NOT NULL DEFAULT 0 COMMENT '本月活动数',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` INT DEFAULT 1 COMMENT '版本号，用于乐观锁',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  KEY `idx_deleted` (`deleted`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公益统计数据表';

-- 插入测试数据（如果表为空）
INSERT INTO `charity_stats` (
  `total_beneficiaries`, `total_institutions`, `cooperating_institutions`, 
  `total_volunteers`, `total_raised`, `total_donated`,
  `total_projects`, `active_projects`, `completed_projects`,
  `total_activities`, `this_month_activities`,
  `version`, `deleted`
) 
SELECT 28650, 342, 198, 285, 18500000.00, 15200000.00, 156, 42, 89, 456, 28, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `charity_stats` WHERE `deleted` = 0);

-- 确保其他相关表也存在
CREATE TABLE IF NOT EXISTS `charity_projects` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '项目ID，主键',
  `name` VARCHAR(200) NOT NULL COMMENT '项目名称',
  `description` TEXT COMMENT '项目描述',
  `type` VARCHAR(50) NOT NULL COMMENT '项目类型',
  `status` VARCHAR(20) NOT NULL DEFAULT 'planning' COMMENT '项目状态',
  `start_date` DATE NOT NULL COMMENT '开始日期',
  `end_date` DATE DEFAULT NULL COMMENT '结束日期',
  `budget` DECIMAL(12,2) DEFAULT 0.00 COMMENT '预算金额',
  `actual_cost` DECIMAL(12,2) DEFAULT 0.00 COMMENT '实际费用',
  `actual_beneficiaries` INT DEFAULT 0 COMMENT '实际受益人数',
  `region` VARCHAR(100) DEFAULT NULL COMMENT '地区',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公益项目表';

CREATE TABLE IF NOT EXISTS `charity_activities` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '活动ID，主键',
  `project_id` BIGINT DEFAULT NULL COMMENT '关联项目ID',
  `title` VARCHAR(200) NOT NULL COMMENT '活动标题',
  `type` VARCHAR(50) NOT NULL COMMENT '活动类型',
  `date` DATE NOT NULL COMMENT '活动日期',
  `participants` INT DEFAULT 0 COMMENT '参与人数',
  `actual_volunteers` INT DEFAULT 0 COMMENT '实际志愿者数',
  `status` VARCHAR(20) NOT NULL DEFAULT 'planned' COMMENT '活动状态',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_project_id` (`project_id`),
  KEY `idx_type` (`type`),
  KEY `idx_date` (`date`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公益活动表';

CREATE TABLE IF NOT EXISTS `charity_institutions` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '机构ID，主键',
  `name` VARCHAR(200) NOT NULL COMMENT '机构名称',
  `type` VARCHAR(50) NOT NULL COMMENT '机构类型',
  `status` VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '机构状态',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合作机构表';