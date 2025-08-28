-- 公益项目管理数据库表创建脚本
-- 创建时间: 2024-12-19
-- 维护人员: YXRobot开发团队

-- 1. 公益统计数据表
CREATE TABLE IF NOT EXISTS `charity_stats` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '统计ID，主键',
  `total_projects` INT NOT NULL DEFAULT 0 COMMENT '项目总数',
  `active_projects` INT NOT NULL DEFAULT 0 COMMENT '进行中项目数',
  `completed_projects` INT NOT NULL DEFAULT 0 COMMENT '已完成项目数',
  `total_beneficiaries` INT NOT NULL DEFAULT 0 COMMENT '累计受益人数',
  `total_raised` DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '累计筹集金额，单位：元',
  `total_donated` DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '累计捐赠金额，单位：元',
  `total_volunteers` INT NOT NULL DEFAULT 0 COMMENT '志愿者总数',
  `active_volunteers` INT NOT NULL DEFAULT 0 COMMENT '活跃志愿者数',
  `total_institutions` INT NOT NULL DEFAULT 0 COMMENT '合作机构总数',
  `cooperating_institutions` INT NOT NULL DEFAULT 0 COMMENT '活跃合作机构数',
  `total_activities` INT NOT NULL DEFAULT 0 COMMENT '活动总数',
  `this_month_activities` INT NOT NULL DEFAULT 0 COMMENT '本月活动数',
  `update_reason` VARCHAR(500) DEFAULT NULL COMMENT '更新原因',
  `updated_by` VARCHAR(100) DEFAULT NULL COMMENT '更新人',
  `is_current` TINYINT DEFAULT 1 COMMENT '是否为当前数据：0-否，1-是',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_is_current` (`is_current`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公益统计数据表';

-- 2. 合作机构表
CREATE TABLE IF NOT EXISTS `charity_institutions` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '机构ID，主键',
  `name` VARCHAR(200) NOT NULL COMMENT '机构名称',
  `type` VARCHAR(50) NOT NULL COMMENT '机构类型：school-学校，orphanage-孤儿院，community-社区，hospital-医院，library-图书馆',
  `location` VARCHAR(100) NOT NULL COMMENT '所在地区',
  `address` VARCHAR(500) NOT NULL COMMENT '详细地址',
  `contact_person` VARCHAR(100) NOT NULL COMMENT '联系人姓名',
  `contact_phone` VARCHAR(20) NOT NULL COMMENT '联系电话',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱地址',
  `student_count` INT DEFAULT 0 COMMENT '学生/受益人数量',
  `cooperation_date` DATE NOT NULL COMMENT '合作开始日期',
  `status` VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '机构状态：active-活跃，inactive-不活跃，pending-待确认',
  `device_count` INT DEFAULT 0 COMMENT '设备数量',
  `last_visit_date` DATE DEFAULT NULL COMMENT '最后访问日期',
  `notes` TEXT COMMENT '备注信息',
  `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '是否已删除：0-否，1-是',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`),
  KEY `idx_location` (`location`),
  KEY `idx_cooperation_date` (`cooperation_date`),
  KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合作机构表';

-- 3. 公益活动表
CREATE TABLE IF NOT EXISTS `charity_activities` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '活动ID，主键',
  `project_id` BIGINT DEFAULT NULL COMMENT '关联项目ID',
  `title` VARCHAR(200) NOT NULL COMMENT '活动标题',
  `description` TEXT COMMENT '活动描述',
  `type` VARCHAR(50) NOT NULL COMMENT '活动类型：visit-探访，training-培训，donation-捐赠，event-活动',
  `date` DATE NOT NULL COMMENT '活动日期',
  `location` VARCHAR(200) NOT NULL COMMENT '活动地点',
  `participants` INT DEFAULT 0 COMMENT '参与人数',
  `organizer` VARCHAR(200) NOT NULL COMMENT '组织方',
  `status` VARCHAR(20) NOT NULL DEFAULT 'planned' COMMENT '活动状态：planned-计划中，ongoing-进行中，completed-已完成，cancelled-已取消',
  `budget` DECIMAL(10,2) DEFAULT 0.00 COMMENT '预算金额，单位：元',
  `actual_cost` DECIMAL(10,2) DEFAULT 0.00 COMMENT '实际费用，单位：元',
  `photos` JSON COMMENT '活动照片URL列表',
  `feedback` TEXT COMMENT '活动反馈',
  `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '是否已删除：0-否，1-是',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_project_id` (`project_id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`),
  KEY `idx_date` (`date`),
  KEY `idx_location` (`location`),
  KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公益活动表';

-- 4. 公益项目表
CREATE TABLE IF NOT EXISTS `charity_projects` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '项目ID，主键',
  `name` VARCHAR(200) NOT NULL COMMENT '项目名称',
  `description` TEXT COMMENT '项目描述',
  `type` VARCHAR(50) NOT NULL COMMENT '项目类型：education-教育，donation-捐赠，volunteer-志愿服务，training-培训',
  `status` VARCHAR(20) NOT NULL DEFAULT 'planning' COMMENT '项目状态：planning-规划中，active-进行中，completed-已完成，suspended-暂停',
  `start_date` DATE NOT NULL COMMENT '开始日期',
  `end_date` DATE DEFAULT NULL COMMENT '结束日期',
  `target_amount` DECIMAL(12,2) DEFAULT 0.00 COMMENT '目标金额，单位：元',
  `raised_amount` DECIMAL(12,2) DEFAULT 0.00 COMMENT '已筹集金额，单位：元',
  `beneficiaries` INT DEFAULT 0 COMMENT '受益人数',
  `location` VARCHAR(200) NOT NULL COMMENT '项目地点',
  `organizer` VARCHAR(200) NOT NULL COMMENT '组织方',
  `contact_person` VARCHAR(100) NOT NULL COMMENT '联系人',
  `contact_phone` VARCHAR(20) NOT NULL COMMENT '联系电话',
  `images` JSON COMMENT '项目图片URL列表',
  `tags` JSON COMMENT '项目标签列表',
  `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '是否已删除：0-否，1-是',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`),
  KEY `idx_start_date` (`start_date`),
  KEY `idx_location` (`location`),
  KEY `idx_organizer` (`organizer`),
  KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公益项目表';

-- 5. 统计数据更新日志表
CREATE TABLE IF NOT EXISTS `charity_stats_logs` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID，主键',
  `stats_id` BIGINT NOT NULL COMMENT '统计数据ID，外键关联charity_stats表',
  `operation_type` VARCHAR(20) NOT NULL COMMENT '操作类型：CREATE-创建，UPDATE-更新，DELETE-删除',
  `field_changes` JSON COMMENT '字段变更记录，JSON格式存储',
  `update_reason` VARCHAR(500) DEFAULT NULL COMMENT '更新原因',
  `operator_id` BIGINT DEFAULT NULL COMMENT '操作人ID',
  `operator_name` VARCHAR(100) DEFAULT NULL COMMENT '操作人姓名',
  `ip_address` VARCHAR(50) DEFAULT NULL COMMENT '操作IP地址',
  `user_agent` VARCHAR(500) DEFAULT NULL COMMENT '用户代理信息',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_stats_id` (`stats_id`),
  KEY `idx_operation_type` (`operation_type`),
  KEY `idx_operator_id` (`operator_id`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='统计数据更新日志表';

-- 插入初始统计数据
INSERT INTO `charity_stats` (
  `total_projects`, `active_projects`, `completed_projects`, 
  `total_beneficiaries`, `total_raised`, `total_donated`,
  `total_volunteers`, `active_volunteers`, `total_institutions`, 
  `cooperating_institutions`, `total_activities`, `this_month_activities`,
  `update_reason`, `updated_by`, `is_current`
) VALUES (
  156, 42, 89, 
  28650, 18500000.00, 15200000.00,
  285, 168, 342, 
  198, 456, 28,
  '系统初始化数据', '系统管理员', 1
) ON DUPLICATE KEY UPDATE 
  `updated_at` = CURRENT_TIMESTAMP;