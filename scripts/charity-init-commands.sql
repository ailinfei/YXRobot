-- 公益统计数据初始化命令
-- 可以直接在MySQL命令行中逐行执行

-- 1. 选择数据库
USE yxrobot;

-- 2. 删除并重新创建charity_stats表
DROP TABLE IF EXISTS charity_stats;

-- 3. 创建charity_stats表
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

-- 4. 插入测试数据
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

-- 5. 验证数据
SELECT * FROM charity_stats WHERE deleted = 0;

-- 6. 创建其他必要的表
CREATE TABLE IF NOT EXISTS charity_projects (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(200) NOT NULL,
  type VARCHAR(50) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'planning',
  budget DECIMAL(12,2) DEFAULT 0.00,
  actual_cost DECIMAL(12,2) DEFAULT 0.00,
  actual_beneficiaries INT DEFAULT 0,
  region VARCHAR(100) DEFAULT NULL,
  deleted TINYINT DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS charity_activities (
  id BIGINT NOT NULL AUTO_INCREMENT,
  title VARCHAR(200) NOT NULL,
  type VARCHAR(50) NOT NULL,
  date DATE NOT NULL,
  actual_volunteers INT DEFAULT 0,
  deleted TINYINT DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS charity_institutions (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(200) NOT NULL,
  type VARCHAR(50) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'active',
  deleted TINYINT DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. 插入测试数据
INSERT IGNORE INTO charity_institutions (name, type, status, deleted) VALUES 
('阳光小学', 'school', 'active', 0),
('希望孤儿院', 'orphanage', 'active', 0),
('社区服务中心', 'community', 'active', 0);

INSERT IGNORE INTO charity_projects (name, type, status, budget, actual_cost, actual_beneficiaries, region, deleted) VALUES 
('教育援助项目', 'education', 'active', 100000.00, 80000.00, 500, '北京', 0),
('爱心捐赠活动', 'donation', 'completed', 50000.00, 45000.00, 200, '上海', 0);

INSERT IGNORE INTO charity_activities (title, type, date, actual_volunteers, deleted) VALUES 
('春季助学活动', 'education', CURDATE(), 20, 0),
('爱心物资捐赠', 'donation', CURDATE(), 15, 0);

-- 8. 最终验证
SELECT 'charity_stats表数据:' as info;
SELECT * FROM charity_stats;

SELECT 'charity_institutions表数据:' as info;
SELECT COUNT(*) as count FROM charity_institutions WHERE deleted = 0;

SELECT 'charity_projects表数据:' as info;
SELECT COUNT(*) as count FROM charity_projects WHERE deleted = 0;

SELECT 'charity_activities表数据:' as info;
SELECT COUNT(*) as count FROM charity_activities WHERE deleted = 0;