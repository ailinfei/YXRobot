-- 简化的公益统计数据初始化脚本
-- 直接在MySQL命令行中执行

USE yxrobot;

-- 创建或修复charity_stats表
DROP TABLE IF EXISTS charity_stats;
CREATE TABLE charity_stats (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '统计ID，主键',
  total_beneficiaries INT NOT NULL DEFAULT 0 COMMENT '累计受益人数',
  total_institutions INT NOT NULL DEFAULT 0 COMMENT '合作机构总数',
  cooperating_institutions INT NOT NULL DEFAULT 0 COMMENT '活跃合作机构数',
  total_volunteers INT NOT NULL DEFAULT 0 COMMENT '志愿者总数',
  total_raised DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '累计筹集金额，单位：元',
  total_donated DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '累计捐赠金额，单位：元',
  total_projects INT NOT NULL DEFAULT 0 COMMENT '项目总数',
  active_projects INT NOT NULL DEFAULT 0 COMMENT '进行中项目数',
  completed_projects INT NOT NULL DEFAULT 0 COMMENT '已完成项目数',
  total_activities INT NOT NULL DEFAULT 0 COMMENT '活动总数',
  this_month_activities INT NOT NULL DEFAULT 0 COMMENT '本月活动数',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  version INT DEFAULT 1 COMMENT '版本号，用于乐观锁',
  deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (id),
  KEY idx_deleted (deleted),
  KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公益统计数据表';

-- 插入测试数据
INSERT INTO charity_stats (
  total_beneficiaries, total_institutions, cooperating_institutions, 
  total_volunteers, total_raised, total_donated,
  total_projects, active_projects, completed_projects,
  total_activities, this_month_activities,
  version, deleted
) VALUES (
  28650, 342, 198, 285, 18500000.00, 15200000.00, 
  156, 42, 89, 456, 28, 1, 0
);

-- 创建其他必要的表（如果不存在）
CREATE TABLE IF NOT EXISTS charity_projects (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '项目ID，主键',
  name VARCHAR(200) NOT NULL COMMENT '项目名称',
  description TEXT COMMENT '项目描述',
  type VARCHAR(50) NOT NULL COMMENT '项目类型',
  status VARCHAR(20) NOT NULL DEFAULT 'planning' COMMENT '项目状态',
  start_date DATE NOT NULL COMMENT '开始日期',
  end_date DATE DEFAULT NULL COMMENT '结束日期',
  budget DECIMAL(12,2) DEFAULT 0.00 COMMENT '预算金额',
  actual_cost DECIMAL(12,2) DEFAULT 0.00 COMMENT '实际费用',
  actual_beneficiaries INT DEFAULT 0 COMMENT '实际受益人数',
  region VARCHAR(100) DEFAULT NULL COMMENT '地区',
  deleted TINYINT DEFAULT 0 COMMENT '是否删除',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_status (status),
  KEY idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公益项目表';

CREATE TABLE IF NOT EXISTS charity_activities (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '活动ID，主键',
  project_id BIGINT DEFAULT NULL COMMENT '关联项目ID',
  title VARCHAR(200) NOT NULL COMMENT '活动标题',
  type VARCHAR(50) NOT NULL COMMENT '活动类型',
  date DATE NOT NULL COMMENT '活动日期',
  participants INT DEFAULT 0 COMMENT '参与人数',
  actual_volunteers INT DEFAULT 0 COMMENT '实际志愿者数',
  status VARCHAR(20) NOT NULL DEFAULT 'planned' COMMENT '活动状态',
  deleted TINYINT DEFAULT 0 COMMENT '是否删除',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_project_id (project_id),
  KEY idx_type (type),
  KEY idx_date (date),
  KEY idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公益活动表';

CREATE TABLE IF NOT EXISTS charity_institutions (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '机构ID，主键',
  name VARCHAR(200) NOT NULL COMMENT '机构名称',
  type VARCHAR(50) NOT NULL COMMENT '机构类型',
  status VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '机构状态',
  deleted TINYINT DEFAULT 0 COMMENT '是否删除',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_status (status),
  KEY idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合作机构表';

-- 插入一些测试数据
INSERT IGNORE INTO charity_institutions (name, type, status, deleted) VALUES 
('阳光小学', 'school', 'active', 0),
('希望孤儿院', 'orphanage', 'active', 0),
('社区服务中心', 'community', 'active', 0);

INSERT IGNORE INTO charity_projects (name, type, status, budget, actual_cost, actual_beneficiaries, region, deleted) VALUES 
('教育援助项目', 'education', 'active', 100000.00, 80000.00, 500, '北京', 0),
('爱心捐赠活动', 'donation', 'completed', 50000.00, 45000.00, 200, '上海', 0),
('志愿服务项目', 'volunteer', 'active', 30000.00, 25000.00, 150, '广州', 0);

INSERT IGNORE INTO charity_activities (title, type, date, participants, actual_volunteers, status, deleted) VALUES 
('春季助学活动', 'education', CURDATE(), 100, 20, 'completed', 0),
('爱心物资捐赠', 'donation', CURDATE(), 50, 15, 'completed', 0),
('社区清洁志愿服务', 'volunteer', CURDATE(), 80, 25, 'completed', 0);

-- 验证数据
SELECT '公益统计数据:' as info;
SELECT * FROM charity_stats WHERE deleted = 0 ORDER BY id DESC LIMIT 1;

SELECT '机构数据:' as info;
SELECT COUNT(*) as total_institutions FROM charity_institutions WHERE deleted = 0;

SELECT '项目数据:' as info;
SELECT COUNT(*) as total_projects FROM charity_projects WHERE deleted = 0;

SELECT '活动数据:' as info;
SELECT COUNT(*) as total_activities FROM charity_activities WHERE deleted = 0;