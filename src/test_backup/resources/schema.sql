-- 测试环境数据库表结构
-- 用于H2内存数据库的表创建

-- 创建产品表
CREATE TABLE IF NOT EXISTS products (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(200) NOT NULL,
  model VARCHAR(100) NOT NULL,
  description CLOB,
  price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  cover_image_url VARCHAR(500) DEFAULT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'draft',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  is_deleted TINYINT DEFAULT 0,
  PRIMARY KEY (id)
);

-- 创建产品媒体表
CREATE TABLE IF NOT EXISTS product_media (
  id BIGINT NOT NULL AUTO_INCREMENT,
  product_id BIGINT NOT NULL,
  media_type VARCHAR(20) NOT NULL,
  media_url VARCHAR(500) NOT NULL,
  sort_order INT NOT NULL DEFAULT 1,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  is_deleted TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
);

-- 创建公益活动表
CREATE TABLE IF NOT EXISTS charity_activities (
  id BIGINT NOT NULL AUTO_INCREMENT,
  project_id BIGINT,
  title VARCHAR(100) NOT NULL,
  description CLOB,
  type VARCHAR(20) NOT NULL,
  date DATE NOT NULL,
  start_time TIMESTAMP,
  end_time TIMESTAMP,
  location VARCHAR(200) NOT NULL,
  participants INT NOT NULL DEFAULT 0,
  target_participants INT DEFAULT 0,
  organizer VARCHAR(100) NOT NULL,
  status VARCHAR(20) NOT NULL,
  budget DECIMAL(15,2) NOT NULL DEFAULT 0.00,
  actual_cost DECIMAL(15,2) DEFAULT 0.00,
  manager VARCHAR(50) NOT NULL,
  manager_contact VARCHAR(20),
  volunteer_needed INT DEFAULT 0,
  actual_volunteers INT DEFAULT 0,
  achievements VARCHAR(500),
  photos VARCHAR(1000),
  notes VARCHAR(500),
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  create_by BIGINT,
  update_by BIGINT,
  deleted TINYINT DEFAULT 0,
  PRIMARY KEY (id)
);

-- 创建公益统计表
CREATE TABLE IF NOT EXISTS charity_stats (
  id BIGINT NOT NULL AUTO_INCREMENT,
  total_beneficiaries INT NOT NULL DEFAULT 0,
  total_institutions INT NOT NULL DEFAULT 0,
  total_activities INT NOT NULL DEFAULT 0,
  total_projects INT NOT NULL DEFAULT 0,
  total_raised DECIMAL(15,2) NOT NULL DEFAULT 0.00,
  total_donated DECIMAL(15,2) NOT NULL DEFAULT 0.00,
  active_projects INT NOT NULL DEFAULT 0,
  completed_projects INT NOT NULL DEFAULT 0,
  cooperating_institutions INT NOT NULL DEFAULT 0,
  total_volunteers INT NOT NULL DEFAULT 0,
  this_month_activities INT NOT NULL DEFAULT 0,
  version INT NOT NULL DEFAULT 1,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  PRIMARY KEY (id)
);

-- 创建公益项目表
CREATE TABLE IF NOT EXISTS charity_projects (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  description CLOB,
  status VARCHAR(20) NOT NULL DEFAULT 'active',
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  PRIMARY KEY (id)
);