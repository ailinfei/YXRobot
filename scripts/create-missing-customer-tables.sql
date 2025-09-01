-- 创建客户管理模块缺失的表
-- 补充extend-customers-table.sql中遗漏的表

USE YXRobot;

-- 5. 服务记录主表
CREATE TABLE IF NOT EXISTS `service_records` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '服务记录ID，主键',
  `service_number` VARCHAR(50) NOT NULL COMMENT '服务单号',
  `service_type` ENUM('maintenance', 'upgrade', 'consultation', 'complaint') NOT NULL COMMENT '服务类型',
  `service_title` VARCHAR(200) NOT NULL COMMENT '服务标题',
  `service_description` TEXT COMMENT '服务描述',
  `service_staff` VARCHAR(100) COMMENT '服务人员',
  `service_cost` DECIMAL(10,2) DEFAULT 0 COMMENT '服务费用',
  `service_status` ENUM('in_progress', 'completed', 'cancelled') DEFAULT 'in_progress' COMMENT '服务状态',
  `priority` ENUM('low', 'medium', 'high', 'urgent') DEFAULT 'medium' COMMENT '优先级',
  `assigned_to` VARCHAR(100) COMMENT '分配给',
  `resolved_at` DATETIME COMMENT '解决时间',
  `service_date` DATE NOT NULL COMMENT '服务日期',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_service_number` (`service_number`),
  INDEX `idx_service_type` (`service_type`),
  INDEX `idx_service_status` (`service_status`),
  INDEX `idx_priority` (`priority`),
  INDEX `idx_service_date` (`service_date`),
  INDEX `idx_created_at` (`created_at`),
  INDEX `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务记录表';

-- 6. 客户服务关联表
CREATE TABLE IF NOT EXISTS `customer_service_relation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID，主键',
  `customer_id` BIGINT NOT NULL COMMENT '客户ID',
  `service_id` BIGINT NOT NULL COMMENT '服务记录ID',
  `customer_role` ENUM('requester', 'contact', 'beneficiary') DEFAULT 'requester' COMMENT '客户角色',
  `relation_notes` TEXT COMMENT '关联备注',
  `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` TINYINT DEFAULT 1 COMMENT '状态：1-有效，0-无效',
  PRIMARY KEY (`id`),
  INDEX `idx_customer_id` (`customer_id`),
  INDEX `idx_service_id` (`service_id`),
  INDEX `idx_customer_role` (`customer_role`),
  INDEX `idx_status` (`status`),
  UNIQUE KEY `uk_customer_service_role` (`customer_id`, `service_id`, `customer_role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户服务关联表';

-- 7. 设备服务关联表
CREATE TABLE IF NOT EXISTS `device_service_relation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID，主键',
  `device_id` BIGINT NOT NULL COMMENT '设备ID',
  `service_id` BIGINT NOT NULL COMMENT '服务记录ID',
  `relation_type` ENUM('target', 'related', 'affected') DEFAULT 'target' COMMENT '关联类型',
  `relation_notes` TEXT COMMENT '关联备注',
  `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` TINYINT DEFAULT 1 COMMENT '状态：1-有效，0-无效',
  PRIMARY KEY (`id`),
  INDEX `idx_device_id` (`device_id`),
  INDEX `idx_service_id` (`service_id`),
  INDEX `idx_relation_type` (`relation_type`),
  INDEX `idx_status` (`status`),
  UNIQUE KEY `uk_device_service_type` (`device_id`, `service_id`, `relation_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备服务关联表';

-- 8. 订单商品明细表
CREATE TABLE IF NOT EXISTS `order_items` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '明细ID，主键',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `product_id` BIGINT NOT NULL COMMENT '产品ID',
  `product_name` VARCHAR(200) NOT NULL COMMENT '产品名称',
  `product_model` VARCHAR(100) NOT NULL COMMENT '产品型号',
  `quantity` INT NOT NULL COMMENT '数量',
  `unit_price` DECIMAL(10,2) NOT NULL COMMENT '单价',
  `total_price` DECIMAL(12,2) NOT NULL COMMENT '小计金额',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_order_id` (`order_id`),
  INDEX `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单商品明细表';

-- 9. 客户统计表
CREATE TABLE IF NOT EXISTS `customer_stats` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '统计ID，主键',
  `stat_date` DATE NOT NULL COMMENT '统计日期',
  `total_customers` INT DEFAULT 0 COMMENT '总客户数',
  `regular_customers` INT DEFAULT 0 COMMENT '普通客户数',
  `vip_customers` INT DEFAULT 0 COMMENT 'VIP客户数',
  `premium_customers` INT DEFAULT 0 COMMENT '高级客户数',
  `active_devices` INT DEFAULT 0 COMMENT '活跃设备数',
  `total_revenue` DECIMAL(15,2) DEFAULT 0 COMMENT '总收入',
  `new_customers_this_month` INT DEFAULT 0 COMMENT '本月新增客户数',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_stat_date` (`stat_date`),
  INDEX `idx_stat_date` (`stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户统计表';

-- 验证表创建结果
SELECT 'Missing customer tables created successfully!' as result;

-- 显示所有客户相关的表
SELECT 'Customer related tables:' as info;
SHOW TABLES LIKE '%customer%';
SHOW TABLES LIKE '%service%';
SHOW TABLES LIKE '%order%';
SHOW TABLES LIKE '%device%';