-- =====================================================
-- 订单管理模块 - 更新现有orders表结构
-- 创建时间: 2025-01-28
-- 说明: 为现有orders表添加订单管理所需的字段
-- =====================================================

-- 使用YXRobot数据库
USE YXRobot;

-- =====================================================
-- 更新orders表结构 - 添加缺失字段
-- =====================================================

-- 1. 更新订单类型枚举值（从purchase改为sales）
ALTER TABLE `orders` MODIFY COLUMN `order_type` ENUM('sales', 'rental') NOT NULL COMMENT '订单类型：销售、租赁';

-- 2. 更新订单状态枚举值
ALTER TABLE `orders` MODIFY COLUMN `order_status` ENUM('pending', 'confirmed', 'processing', 'shipped', 'delivered', 'completed', 'cancelled') DEFAULT 'pending' COMMENT '订单状态';

-- 3. 重命名字段以符合设计规范
ALTER TABLE `orders` CHANGE COLUMN `order_type` `type` ENUM('sales', 'rental') NOT NULL COMMENT '订单类型：销售、租赁';
ALTER TABLE `orders` CHANGE COLUMN `order_status` `status` ENUM('pending', 'confirmed', 'processing', 'shipped', 'delivered', 'completed', 'cancelled') DEFAULT 'pending' COMMENT '订单状态';
ALTER TABLE `orders` CHANGE COLUMN `order_date` `created_at_old` DATE NOT NULL COMMENT '原创建日期字段（临时）';

-- 4. 添加客户相关字段
ALTER TABLE `orders` ADD COLUMN `customer_id` BIGINT NOT NULL COMMENT '客户ID（引用customers表）' AFTER `status`;
ALTER TABLE `orders` ADD COLUMN `delivery_address` TEXT NOT NULL COMMENT '配送地址' AFTER `customer_id`;

-- 5. 添加金额相关字段
ALTER TABLE `orders` ADD COLUMN `subtotal` DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT '小计金额' AFTER `delivery_address`;
ALTER TABLE `orders` ADD COLUMN `shipping_fee` DECIMAL(10,2) DEFAULT 0 COMMENT '运费' AFTER `subtotal`;
ALTER TABLE `orders` ADD COLUMN `discount` DECIMAL(10,2) DEFAULT 0 COMMENT '折扣金额' AFTER `shipping_fee`;
ALTER TABLE `orders` ADD COLUMN `currency` VARCHAR(10) DEFAULT 'CNY' COMMENT '货币类型' AFTER `total_amount`;

-- 6. 添加支付相关字段
ALTER TABLE `orders` ADD COLUMN `payment_status` ENUM('pending', 'paid', 'failed', 'refunded') DEFAULT 'pending' COMMENT '支付状态' AFTER `currency`;
ALTER TABLE `orders` ADD COLUMN `payment_method` VARCHAR(50) COMMENT '支付方式' AFTER `payment_status`;
ALTER TABLE `orders` ADD COLUMN `payment_time` DATETIME COMMENT '支付时间' AFTER `payment_method`;

-- 7. 添加其他业务字段
ALTER TABLE `orders` ADD COLUMN `expected_delivery_date` DATE COMMENT '预期交付日期' AFTER `payment_time`;
ALTER TABLE `orders` ADD COLUMN `sales_person` VARCHAR(100) COMMENT '销售人员' AFTER `expected_delivery_date`;
ALTER TABLE `orders` ADD COLUMN `notes` TEXT COMMENT '订单备注' AFTER `sales_person`;

-- 8. 添加租赁相关字段
ALTER TABLE `orders` ADD COLUMN `rental_start_date` DATE COMMENT '租赁开始日期（仅租赁订单）' AFTER `notes`;
ALTER TABLE `orders` ADD COLUMN `rental_end_date` DATE COMMENT '租赁结束日期（仅租赁订单）' AFTER `rental_start_date`;
ALTER TABLE `orders` ADD COLUMN `rental_days` INT COMMENT '租赁天数（仅租赁订单）' AFTER `rental_end_date`;
ALTER TABLE `orders` ADD COLUMN `rental_notes` TEXT COMMENT '租赁备注（仅租赁订单）' AFTER `rental_days`;

-- 9. 添加系统字段
ALTER TABLE `orders` ADD COLUMN `created_by` VARCHAR(100) COMMENT '创建人' AFTER `updated_at`;

-- 10. 删除临时字段
ALTER TABLE `orders` DROP COLUMN `created_at_old`;

-- 11. 添加索引
ALTER TABLE `orders` ADD INDEX `idx_customer_id` (`customer_id`);
ALTER TABLE `orders` ADD INDEX `idx_type` (`type`);
ALTER TABLE `orders` ADD INDEX `idx_status` (`status`);
ALTER TABLE `orders` ADD INDEX `idx_payment_status` (`payment_status`);

-- =====================================================
-- 显示更新后的表结构
-- =====================================================
DESCRIBE orders;

-- =====================================================
-- 脚本执行完成
-- =====================================================
SELECT '订单表结构更新完成！' as result;