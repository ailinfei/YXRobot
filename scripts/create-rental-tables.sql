-- =====================================================
-- 租赁数据分析模块 - 数据库表结构创建脚本
-- 基于 RentalAnalytics.vue 前端页面需求设计
-- 创建时间: 2025-01-28
-- =====================================================

USE YXRobot;

-- =====================================================
-- 1. 租赁记录主表 (rental_records)
-- =====================================================
CREATE TABLE IF NOT EXISTS `rental_records` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '租赁记录ID，主键',
  `rental_order_number` VARCHAR(50) NOT NULL COMMENT '租赁订单号',
  `device_id` BIGINT NOT NULL COMMENT '设备ID',
  `customer_id` BIGINT NOT NULL COMMENT '客户ID',
  `rental_start_date` DATE NOT NULL COMMENT '租赁开始日期',
  `rental_end_date` DATE COMMENT '租赁结束日期',
  `planned_end_date` DATE NOT NULL COMMENT '计划结束日期',
  `rental_period` INT NOT NULL COMMENT '租赁期间（天数）',
  `daily_rental_fee` DECIMAL(10,2) NOT NULL COMMENT '日租金',
  `total_rental_fee` DECIMAL(12,2) NOT NULL COMMENT '总租金',
  `deposit_amount` DECIMAL(10,2) DEFAULT 0 COMMENT '押金金额',
  `actual_payment` DECIMAL(12,2) NOT NULL COMMENT '实际支付金额',
  `payment_status` ENUM('unpaid', 'partial', 'paid', 'refunded') DEFAULT 'unpaid' COMMENT '付款状态',
  `rental_status` ENUM('pending', 'active', 'completed', 'cancelled', 'overdue') DEFAULT 'pending' COMMENT '租赁状态',
  `delivery_method` VARCHAR(50) COMMENT '交付方式',
  `delivery_address` TEXT COMMENT '交付地址',
  `return_date` DATE COMMENT '实际归还日期',
  `return_condition` VARCHAR(100) COMMENT '归还状态',
  `notes` TEXT COMMENT '备注信息',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_rental_order_number` (`rental_order_number`),
  INDEX `idx_device_id` (`device_id`),
  INDEX `idx_customer_id` (`customer_id`),
  INDEX `idx_rental_start_date` (`rental_start_date`),
  INDEX `idx_rental_status` (`rental_status`),
  INDEX `idx_payment_status` (`payment_status`),
  INDEX `idx_created_at` (`created_at`),
  INDEX `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租赁记录表';

-- =====================================================
-- 2. 租赁设备表 (rental_devices)
-- =====================================================
CREATE TABLE IF NOT EXISTS `rental_devices` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '设备ID，主键',
  `device_id` VARCHAR(50) NOT NULL COMMENT '设备编号',
  `device_model` VARCHAR(100) NOT NULL COMMENT '设备型号',
  `device_name` VARCHAR(200) NOT NULL COMMENT '设备名称',
  `device_category` VARCHAR(100) NOT NULL COMMENT '设备类别',
  `serial_number` VARCHAR(100) COMMENT '序列号',
  `purchase_date` DATE COMMENT '采购日期',
  `purchase_price` DECIMAL(10,2) COMMENT '采购价格',
  `daily_rental_price` DECIMAL(8,2) NOT NULL COMMENT '日租金价格',
  `current_status` ENUM('active', 'idle', 'maintenance', 'retired') DEFAULT 'idle' COMMENT '当前状态',
  `location` VARCHAR(200) COMMENT '设备位置',
  `region` VARCHAR(100) COMMENT '所在地区',
  `performance_score` INT DEFAULT 100 COMMENT '性能评分（0-100）',
  `signal_strength` INT DEFAULT 100 COMMENT '信号强度（0-100）',
  `maintenance_status` ENUM('normal', 'warning', 'urgent') DEFAULT 'normal' COMMENT '维护状态',
  `last_maintenance_date` DATE COMMENT '最后维护日期',
  `next_maintenance_date` DATE COMMENT '下次维护日期',
  `total_rental_days` INT DEFAULT 0 COMMENT '累计租赁天数',
  `total_available_days` INT DEFAULT 0 COMMENT '累计可用天数',
  `utilization_rate` DECIMAL(5,2) DEFAULT 0 COMMENT '利用率',
  `last_rental_date` DATE COMMENT '最后租赁日期',
  `is_active` TINYINT(1) DEFAULT 1 COMMENT '是否启用',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_device_id` (`device_id`),
  INDEX `idx_device_model` (`device_model`),
  INDEX `idx_current_status` (`current_status`),
  INDEX `idx_region` (`region`),
  INDEX `idx_utilization_rate` (`utilization_rate`),
  INDEX `idx_is_active` (`is_active`),
  INDEX `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租赁设备表';

-- =====================================================
-- 3. 设备利用率表 (device_utilization)
-- =====================================================
CREATE TABLE IF NOT EXISTS `device_utilization` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '利用率记录ID，主键',
  `device_id` BIGINT NOT NULL COMMENT '设备ID',
  `stat_date` DATE NOT NULL COMMENT '统计日期',
  `stat_period` ENUM('daily', 'weekly', 'monthly', 'yearly') NOT NULL COMMENT '统计周期',
  `rental_hours` DECIMAL(8,2) DEFAULT 0 COMMENT '租赁小时数',
  `available_hours` DECIMAL(8,2) DEFAULT 0 COMMENT '可用小时数',
  `utilization_rate` DECIMAL(5,2) DEFAULT 0 COMMENT '利用率',
  `rental_revenue` DECIMAL(10,2) DEFAULT 0 COMMENT '租赁收入',
  `rental_count` INT DEFAULT 0 COMMENT '租赁次数',
  `average_rental_duration` DECIMAL(8,2) DEFAULT 0 COMMENT '平均租赁时长',
  `downtime_hours` DECIMAL(8,2) DEFAULT 0 COMMENT '停机时间',
  `maintenance_hours` DECIMAL(8,2) DEFAULT 0 COMMENT '维护时间',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_device_stat_date_period` (`device_id`, `stat_date`, `stat_period`),
  INDEX `idx_device_id` (`device_id`),
  INDEX `idx_stat_date` (`stat_date`),
  INDEX `idx_stat_period` (`stat_period`),
  INDEX `idx_utilization_rate` (`utilization_rate`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备利用率表';

-- =====================================================
-- 4. 租赁统计表 (rental_stats)
-- =====================================================
CREATE TABLE IF NOT EXISTS `rental_stats` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '统计ID，主键',
  `stat_date` DATE NOT NULL COMMENT '统计日期',
  `stat_type` ENUM('daily', 'weekly', 'monthly', 'yearly') NOT NULL COMMENT '统计类型',
  `total_rental_revenue` DECIMAL(15,2) DEFAULT 0 COMMENT '总租赁收入',
  `total_rental_orders` INT DEFAULT 0 COMMENT '总租赁订单数',
  `total_rental_devices` INT DEFAULT 0 COMMENT '总租赁设备数',
  `active_rental_devices` INT DEFAULT 0 COMMENT '活跃租赁设备数',
  `average_rental_period` DECIMAL(8,2) DEFAULT 0 COMMENT '平均租赁期间',
  `average_daily_revenue` DECIMAL(10,2) DEFAULT 0 COMMENT '平均日收入',
  `device_utilization_rate` DECIMAL(5,2) DEFAULT 0 COMMENT '设备利用率',
  `new_customers` INT DEFAULT 0 COMMENT '新客户数',
  `returning_customers` INT DEFAULT 0 COMMENT '回头客户数',
  `top_device_model` VARCHAR(100) COMMENT '最受欢迎设备型号',
  `top_region` VARCHAR(100) COMMENT '最活跃地区',
  `revenue_growth_rate` DECIMAL(8,4) DEFAULT 0 COMMENT '收入增长率',
  `device_growth_rate` DECIMAL(8,4) DEFAULT 0 COMMENT '设备增长率',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_stat_date_type` (`stat_date`, `stat_type`),
  INDEX `idx_stat_date` (`stat_date`),
  INDEX `idx_stat_type` (`stat_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租赁统计表';

-- =====================================================
-- 5. 租赁客户表 (rental_customers)
-- =====================================================
CREATE TABLE IF NOT EXISTS `rental_customers` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '客户ID，主键',
  `customer_name` VARCHAR(200) NOT NULL COMMENT '客户名称',
  `customer_type` ENUM('individual', 'enterprise', 'institution') NOT NULL COMMENT '客户类型',
  `contact_person` VARCHAR(100) COMMENT '联系人',
  `phone` VARCHAR(20) COMMENT '联系电话',
  `email` VARCHAR(100) COMMENT '邮箱地址',
  `address` TEXT COMMENT '地址',
  `region` VARCHAR(100) COMMENT '所在地区',
  `industry` VARCHAR(100) COMMENT '所属行业',
  `credit_level` ENUM('A', 'B', 'C', 'D') DEFAULT 'B' COMMENT '信用等级',
  `total_rental_amount` DECIMAL(12,2) DEFAULT 0 COMMENT '累计租赁金额',
  `total_rental_days` INT DEFAULT 0 COMMENT '累计租赁天数',
  `last_rental_date` DATE COMMENT '最后租赁日期',
  `is_active` TINYINT(1) DEFAULT 1 COMMENT '是否活跃',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`),
  INDEX `idx_customer_name` (`customer_name`),
  INDEX `idx_customer_type` (`customer_type`),
  INDEX `idx_region` (`region`),
  INDEX `idx_is_active` (`is_active`),
  INDEX `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租赁客户表';

-- =====================================================
-- 创建表完成提示
-- =====================================================
SELECT 'Rental tables created successfully!' as message;

-- 验证表是否创建成功
SHOW TABLES LIKE '%rental%';
SHOW TABLES LIKE '%device%';

-- 显示表结构
DESCRIBE rental_records;
DESCRIBE rental_devices;
DESCRIBE device_utilization;
DESCRIBE rental_stats;
DESCRIBE rental_customers;