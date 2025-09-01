-- 扩展现有customers表以支持客户管理模块
-- 基于现有表结构添加缺失字段，保持数据兼容性
-- ⚠️ 注意：customers表被sales_records表外键引用，只能添加字段，不能修改现有结构

USE YXRobot;

-- 安全检查：显示当前数据状态
SELECT 'Current customers table structure:' as info;
DESCRIBE customers;

SELECT 'Current data count:' as info;
SELECT 
  COUNT(*) as total_customers,
  COUNT(CASE WHEN is_deleted = 0 THEN 1 END) as active_customers
FROM customers;

-- 检查外键约束（重要：确认依赖关系）
SELECT 'Foreign key constraints referencing customers table:' as info;
SELECT TABLE_NAME, COLUMN_NAME, CONSTRAINT_NAME 
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE 
WHERE REFERENCED_TABLE_NAME = 'customers';

-- 检查sales_records依赖数据
SELECT 'Sales records depending on customers:' as info;
SELECT COUNT(*) as sales_records_count FROM sales_records WHERE is_deleted = 0;

-- 添加客户管理模块需要的字段
ALTER TABLE `customers` 
-- 客户等级字段
ADD COLUMN `customer_level` ENUM('regular', 'vip', 'premium') DEFAULT 'regular' COMMENT '客户等级：regular-普通客户，vip-VIP客户，premium-高级客户' AFTER `customer_type`,

-- 客户状态字段（区别于is_active）
ADD COLUMN `customer_status` ENUM('active', 'inactive', 'suspended') DEFAULT 'active' COMMENT '客户状态：active-活跃，inactive-不活跃，suspended-暂停' AFTER `customer_level`,

-- 头像URL字段
ADD COLUMN `avatar_url` VARCHAR(500) COMMENT '客户头像URL' AFTER `email`,

-- 客户标签字段（JSON格式）
ADD COLUMN `customer_tags` JSON COMMENT '客户标签，JSON数组格式' AFTER `avatar_url`,

-- 备注信息字段
ADD COLUMN `notes` TEXT COMMENT '客户备注信息' AFTER `customer_tags`,

-- 累计消费金额字段
ADD COLUMN `total_spent` DECIMAL(12,2) DEFAULT 0.00 COMMENT '累计消费金额' AFTER `notes`,

-- 客户价值评分字段
ADD COLUMN `customer_value` DECIMAL(3,1) DEFAULT 0.0 COMMENT '客户价值评分（0-10分）' AFTER `total_spent`,

-- 注册时间字段（区别于created_at）
ADD COLUMN `registered_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '客户注册时间' AFTER `customer_value`,

-- 最后活跃时间字段
ADD COLUMN `last_active_at` DATETIME COMMENT '最后活跃时间' AFTER `registered_at`;

-- 添加新字段的索引以优化查询性能
ALTER TABLE `customers`
ADD INDEX `idx_customer_level` (`customer_level`),
ADD INDEX `idx_customer_status` (`customer_status`),
ADD INDEX `idx_total_spent` (`total_spent`),
ADD INDEX `idx_customer_value` (`customer_value`),
ADD INDEX `idx_registered_at` (`registered_at`),
ADD INDEX `idx_last_active_at` (`last_active_at`);

-- 更新现有数据的默认值（保持兼容性）
UPDATE `customers` SET 
  `customer_level` = 'regular',
  `customer_status` = CASE 
    WHEN `is_active` = 1 THEN 'active' 
    ELSE 'inactive' 
  END,
  `total_spent` = 0.00,
  `customer_value` = 0.0,
  `registered_at` = `created_at`,
  `last_active_at` = `updated_at`
WHERE `customer_level` IS NULL;

-- 验证表结构扩展结果
SELECT 'Extended customers table structure:' as info;
DESCRIBE customers;

-- 验证数据完整性（重要：确保外键关系未破坏）
SELECT 'Data integrity verification:' as info;
SELECT 
  COUNT(*) as total_customers,
  COUNT(CASE WHEN customer_level = 'regular' THEN 1 END) as regular_customers,
  COUNT(CASE WHEN customer_level = 'vip' THEN 1 END) as vip_customers,
  COUNT(CASE WHEN customer_level = 'premium' THEN 1 END) as premium_customers,
  COUNT(CASE WHEN customer_status = 'active' THEN 1 END) as active_customers,
  COUNT(CASE WHEN is_deleted = 0 THEN 1 END) as non_deleted_customers
FROM customers;

-- 验证外键关系完整性
SELECT 'Foreign key relationship verification:' as info;
SELECT 
  c.customer_name,
  COUNT(s.id) as sales_count
FROM customers c
LEFT JOIN sales_records s ON c.id = s.customer_id AND s.is_deleted = 0
WHERE c.is_deleted = 0
GROUP BY c.id, c.customer_name
HAVING sales_count > 0
ORDER BY sales_count DESC
LIMIT 5;

-- 创建关联表（遵循项目数据库设计规范）
-- 规范：禁止使用外键约束，通过关联表实现表间关系

-- 1. 设备信息主表（如果不存在）
CREATE TABLE IF NOT EXISTS `devices` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '设备ID，主键',
  `device_id` VARCHAR(50) NOT NULL COMMENT '设备编号',
  `serial_number` VARCHAR(100) NOT NULL COMMENT '设备序列号',
  `device_model` VARCHAR(100) NOT NULL COMMENT '设备型号',
  `device_name` VARCHAR(200) NOT NULL COMMENT '设备名称',
  `device_category` VARCHAR(100) NOT NULL COMMENT '设备类别',
  `device_status` ENUM('pending', 'active', 'offline', 'maintenance', 'retired') DEFAULT 'pending' COMMENT '设备状态',
  `firmware_version` VARCHAR(50) COMMENT '固件版本',
  `health_score` INT DEFAULT 100 COMMENT '健康评分（0-100）',
  `region` VARCHAR(100) COMMENT '所在地区',
  `is_active` TINYINT(1) DEFAULT 1 COMMENT '是否启用',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_device_id` (`device_id`),
  INDEX `idx_device_model` (`device_model`),
  INDEX `idx_device_status` (`device_status`),
  INDEX `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备信息表';

-- 2. 客户设备关联表
CREATE TABLE IF NOT EXISTS `customer_device_relation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID，主键',
  `customer_id` BIGINT NOT NULL COMMENT '客户ID',
  `device_id` BIGINT NOT NULL COMMENT '设备ID',
  `relation_type` ENUM('purchased', 'rental') NOT NULL COMMENT '关联类型：购买、租赁',
  `start_date` DATE NOT NULL COMMENT '关联开始日期',
  `end_date` DATE COMMENT '关联结束日期（租赁设备）',
  `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` TINYINT DEFAULT 1 COMMENT '状态：1-有效，0-无效',
  PRIMARY KEY (`id`),
  INDEX `idx_customer_id` (`customer_id`),
  INDEX `idx_device_id` (`device_id`),
  INDEX `idx_relation_type` (`relation_type`),
  INDEX `idx_status` (`status`),
  UNIQUE KEY `uk_customer_device` (`customer_id`, `device_id`, `relation_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户设备关联表';

-- 3. 订单信息主表（如果不存在）
CREATE TABLE IF NOT EXISTS `orders` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID，主键',
  `order_number` VARCHAR(50) NOT NULL COMMENT '订单号',
  `order_type` ENUM('purchase', 'rental') NOT NULL COMMENT '订单类型',
  `order_status` ENUM('pending', 'processing', 'completed', 'cancelled') DEFAULT 'pending' COMMENT '订单状态',
  `total_amount` DECIMAL(12,2) NOT NULL COMMENT '订单总金额',
  `order_date` DATE NOT NULL COMMENT '订单日期',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_number` (`order_number`),
  INDEX `idx_order_type` (`order_type`),
  INDEX `idx_order_status` (`order_status`),
  INDEX `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单信息表';

-- 4. 客户订单关联表
CREATE TABLE IF NOT EXISTS `customer_order_relation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID，主键',
  `customer_id` BIGINT NOT NULL COMMENT '客户ID',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `customer_role` ENUM('buyer', 'payer', 'receiver') DEFAULT 'buyer' COMMENT '客户角色',
  `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` TINYINT DEFAULT 1 COMMENT '状态：1-有效，0-无效',
  PRIMARY KEY (`id`),
  INDEX `idx_customer_id` (`customer_id`),
  INDEX `idx_order_id` (`order_id`),
  INDEX `idx_status` (`status`),
  UNIQUE KEY `uk_customer_order_role` (`customer_id`, `order_id`, `customer_role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户订单关联表';

SELECT 'Customers table extension completed successfully!' as result;
SELECT 'All relation tables created following project database design standards!' as confirmation;
SELECT 'No foreign key constraints used - all relationships managed through relation tables!' as design_compliance;