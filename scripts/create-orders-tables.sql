-- =====================================================
-- 订单管理模块数据库表结构创建脚本
-- 创建时间: 2025-01-28
-- 说明: 创建订单管理相关的数据表结构
-- =====================================================

-- 使用YXRobot数据库
USE YXRobot;

-- =====================================================
-- 1. 创建订单主表 (orders)
-- =====================================================
CREATE TABLE IF NOT EXISTS `orders` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID，主键',
  `order_number` VARCHAR(50) NOT NULL COMMENT '订单号',
  `type` ENUM('sales', 'rental') NOT NULL COMMENT '订单类型：销售、租赁',
  `status` ENUM('pending', 'confirmed', 'processing', 'shipped', 'delivered', 'completed', 'cancelled') DEFAULT 'pending' COMMENT '订单状态',
  `customer_id` BIGINT NOT NULL COMMENT '客户ID（引用customers表）',
  `delivery_address` TEXT NOT NULL COMMENT '配送地址',
  `subtotal` DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT '小计金额',
  `shipping_fee` DECIMAL(10,2) DEFAULT 0 COMMENT '运费',
  `discount` DECIMAL(10,2) DEFAULT 0 COMMENT '折扣金额',
  `total_amount` DECIMAL(12,2) NOT NULL COMMENT '订单总金额',
  `currency` VARCHAR(10) DEFAULT 'CNY' COMMENT '货币类型',
  `payment_status` ENUM('pending', 'paid', 'failed', 'refunded') DEFAULT 'pending' COMMENT '支付状态',
  `payment_method` VARCHAR(50) COMMENT '支付方式',
  `payment_time` DATETIME COMMENT '支付时间',
  `expected_delivery_date` DATE COMMENT '预期交付日期',
  `sales_person` VARCHAR(100) COMMENT '销售人员',
  `notes` TEXT COMMENT '订单备注',
  `rental_start_date` DATE COMMENT '租赁开始日期（仅租赁订单）',
  `rental_end_date` DATE COMMENT '租赁结束日期（仅租赁订单）',
  `rental_days` INT COMMENT '租赁天数（仅租赁订单）',
  `rental_notes` TEXT COMMENT '租赁备注（仅租赁订单）',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` VARCHAR(100) COMMENT '创建人',
  `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_number` (`order_number`),
  INDEX `idx_customer_id` (`customer_id`),
  INDEX `idx_type` (`type`),
  INDEX `idx_status` (`status`),
  INDEX `idx_payment_status` (`payment_status`),
  INDEX `idx_created_at` (`created_at`),
  INDEX `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单主表';

-- =====================================================
-- 2. 创建订单商品表 (order_items)
-- =====================================================
CREATE TABLE IF NOT EXISTS `order_items` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单商品ID，主键',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `product_id` BIGINT NOT NULL COMMENT '产品ID（引用products表）',
  `product_name` VARCHAR(200) NOT NULL COMMENT '产品名称（冗余字段，避免关联查询）',
  `quantity` INT NOT NULL DEFAULT 1 COMMENT '数量',
  `unit_price` DECIMAL(10,2) NOT NULL COMMENT '单价',
  `total_price` DECIMAL(12,2) NOT NULL COMMENT '小计金额',
  `notes` TEXT COMMENT '商品备注',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_order_id` (`order_id`),
  INDEX `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单商品表';

-- =====================================================
-- 3. 创建物流信息表 (shipping_info)
-- =====================================================
CREATE TABLE IF NOT EXISTS `shipping_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '物流信息ID，主键',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `company` VARCHAR(100) COMMENT '物流公司',
  `tracking_number` VARCHAR(100) COMMENT '运单号',
  `shipped_at` DATETIME COMMENT '发货时间',
  `delivered_at` DATETIME COMMENT '送达时间',
  `notes` TEXT COMMENT '物流备注',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_id` (`order_id`),
  INDEX `idx_tracking_number` (`tracking_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流信息表';

-- =====================================================
-- 4. 创建订单操作日志表 (order_logs)
-- =====================================================
CREATE TABLE IF NOT EXISTS `order_logs` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID，主键',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `action` VARCHAR(100) NOT NULL COMMENT '操作动作',
  `operator` VARCHAR(100) NOT NULL COMMENT '操作人',
  `notes` TEXT COMMENT '操作备注',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_order_id` (`order_id`),
  INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单操作日志表';

-- =====================================================
-- 5. 验证现有表结构
-- =====================================================
-- 验证customers表是否存在
SELECT 'customers表验证' as table_check, COUNT(*) as table_exists 
FROM information_schema.tables 
WHERE table_schema = 'YXRobot' AND table_name = 'customers';

-- 验证products表是否存在
SELECT 'products表验证' as table_check, COUNT(*) as table_exists 
FROM information_schema.tables 
WHERE table_schema = 'YXRobot' AND table_name = 'products';

-- =====================================================
-- 6. 显示创建结果
-- =====================================================
SHOW TABLES LIKE 'orders';
SHOW TABLES LIKE 'order_items';
SHOW TABLES LIKE 'shipping_info';
SHOW TABLES LIKE 'order_logs';

-- 显示表结构
DESCRIBE orders;
DESCRIBE order_items;
DESCRIBE shipping_info;
DESCRIBE order_logs;

-- =====================================================
-- 脚本执行完成
-- =====================================================
SELECT '订单管理模块数据表创建完成！' as result;