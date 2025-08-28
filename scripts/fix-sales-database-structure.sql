-- 销售数据管理模块数据库结构修复脚本
-- 修复前端页面需求与数据库设计不匹配的问题
-- 创建时间: 2025-01-27

USE YXRobot;

-- 1. 为sales_records表添加缺失的字段
ALTER TABLE sales_records 
ADD COLUMN shipping_address VARCHAR(500) COMMENT '收货地址' AFTER notes,
ADD COLUMN shipping_fee DECIMAL(10,2) DEFAULT 0.00 COMMENT '运费' AFTER shipping_address,
ADD COLUMN subtotal DECIMAL(10,2) COMMENT '商品小计金额' AFTER shipping_fee,
ADD COLUMN total_amount DECIMAL(10,2) COMMENT '订单总金额（含运费）' AFTER subtotal,
ADD COLUMN tracking_number VARCHAR(100) COMMENT '快递单号' AFTER total_amount,
ADD COLUMN shipping_company VARCHAR(100) COMMENT '快递公司' AFTER tracking_number;

-- 2. 为customers表添加缺失的字段（如果不存在）
-- 检查phone字段是否存在，如果不存在则添加
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'YXRobot' 
  AND TABLE_NAME = 'customers' 
  AND COLUMN_NAME = 'phone';

SET @sql = IF(@col_exists = 0, 
  'ALTER TABLE customers ADD COLUMN phone VARCHAR(20) COMMENT ''联系电话'' AFTER contact_person', 
  'SELECT ''phone column already exists'' as message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 3. 更新现有数据，计算total_amount和subtotal
UPDATE sales_records 
SET 
  subtotal = sales_amount - COALESCE(discount_amount, 0),
  total_amount = sales_amount + COALESCE(shipping_fee, 0)
WHERE subtotal IS NULL OR total_amount IS NULL;

-- 4. 创建销售记录扩展视图，包含关联表的字段
CREATE OR REPLACE VIEW sales_records_extended AS
SELECT 
    sr.id,
    sr.order_number,
    sr.customer_id,
    sr.product_id,
    sr.sales_staff_id,
    sr.sales_amount,
    sr.quantity,
    sr.unit_price,
    sr.discount_amount,
    sr.subtotal,
    sr.shipping_fee,
    sr.total_amount,
    sr.order_date,
    sr.delivery_date,
    sr.status,
    sr.payment_status,
    sr.payment_method,
    sr.region,
    sr.channel,
    sr.notes,
    sr.shipping_address,
    sr.tracking_number,
    sr.shipping_company,
    sr.created_at,
    sr.updated_at,
    sr.is_deleted,
    -- 客户信息
    c.customer_name,
    c.phone as customer_phone,
    c.email as customer_email,
    c.customer_type,
    c.credit_level,
    -- 产品信息
    p.product_name,
    p.product_code,
    p.category as product_category,
    p.brand as product_brand,
    p.model as product_model,
    p.specifications as product_specifications,
    -- 销售人员信息
    ss.staff_name,
    ss.staff_code,
    ss.department,
    ss.position
FROM sales_records sr
LEFT JOIN customers c ON sr.customer_id = c.id
LEFT JOIN sales_products p ON sr.product_id = p.id  
LEFT JOIN sales_staff ss ON sr.sales_staff_id = ss.id
WHERE sr.is_deleted = 0;

-- 5. 创建订单商品明细表（支持一个订单多个产品）
CREATE TABLE IF NOT EXISTS sales_order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL COMMENT '订单ID（关联sales_records.id）',
    product_id BIGINT NOT NULL COMMENT '产品ID',
    product_name VARCHAR(200) NOT NULL COMMENT '产品名称（冗余存储）',
    product_specification VARCHAR(200) COMMENT '产品规格',
    product_image VARCHAR(500) COMMENT '产品图片URL',
    quantity INT NOT NULL COMMENT '数量',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单价',
    total_price DECIMAL(10,2) NOT NULL COMMENT '小计金额',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_order_id (order_id),
    INDEX idx_product_id (product_id),
    FOREIGN KEY (order_id) REFERENCES sales_records(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES sales_products(id) ON DELETE RESTRICT
) COMMENT='销售订单商品明细表';

-- 6. 为sales_products表添加产品图片字段
ALTER TABLE sales_products 
ADD COLUMN product_image VARCHAR(500) COMMENT '产品图片URL' AFTER description;

-- 7. 创建数据迁移：将现有sales_records数据迁移到order_items表
INSERT INTO sales_order_items (
    order_id, product_id, product_name, quantity, unit_price, total_price
)
SELECT 
    sr.id,
    sr.product_id,
    COALESCE(p.product_name, '未知产品'),
    sr.quantity,
    sr.unit_price,
    sr.sales_amount
FROM sales_records sr
LEFT JOIN sales_products p ON sr.product_id = p.id
WHERE sr.is_deleted = 0
  AND NOT EXISTS (
    SELECT 1 FROM sales_order_items soi WHERE soi.order_id = sr.id
  );

-- 8. 更新索引以支持新字段查询
ALTER TABLE sales_records ADD INDEX idx_shipping_address (shipping_address(100));
ALTER TABLE sales_records ADD INDEX idx_tracking_number (tracking_number);
ALTER TABLE sales_records ADD INDEX idx_shipping_company (shipping_company);
ALTER TABLE sales_records ADD INDEX idx_total_amount (total_amount);

-- 9. 创建完整的销售订单视图（包含商品明细）
CREATE OR REPLACE VIEW sales_orders_complete AS
SELECT 
    sr.id,
    sr.order_number,
    sr.customer_id,
    sr.sales_staff_id,
    sr.sales_amount,
    sr.discount_amount,
    sr.subtotal,
    sr.shipping_fee,
    sr.total_amount,
    sr.order_date,
    sr.delivery_date,
    sr.status,
    sr.payment_status,
    sr.payment_method,
    sr.region,
    sr.channel,
    sr.notes,
    sr.shipping_address,
    sr.tracking_number,
    sr.shipping_company,
    sr.created_at,
    sr.updated_at,
    -- 客户信息
    c.customer_name,
    c.phone as customer_phone,
    c.email as customer_email,
    c.address as customer_address,
    c.customer_type,
    c.credit_level,
    -- 销售人员信息
    ss.staff_name,
    ss.staff_code,
    ss.department,
    ss.position,
    -- 商品信息（JSON格式，包含所有商品）
    JSON_ARRAYAGG(
        JSON_OBJECT(
            'id', soi.product_id,
            'name', soi.product_name,
            'specification', soi.product_specification,
            'image', soi.product_image,
            'quantity', soi.quantity,
            'unitPrice', soi.unit_price,
            'totalPrice', soi.total_price
        )
    ) as products
FROM sales_records sr
LEFT JOIN customers c ON sr.customer_id = c.id
LEFT JOIN sales_staff ss ON sr.sales_staff_id = ss.id
LEFT JOIN sales_order_items soi ON sr.id = soi.order_id
WHERE sr.is_deleted = 0
GROUP BY sr.id, sr.order_number, sr.customer_id, sr.sales_staff_id,
         sr.sales_amount, sr.discount_amount, sr.subtotal, sr.shipping_fee,
         sr.total_amount, sr.order_date, sr.delivery_date, sr.status,
         sr.payment_status, sr.payment_method, sr.region, sr.channel,
         sr.notes, sr.shipping_address, sr.tracking_number, sr.shipping_company,
         sr.created_at, sr.updated_at, c.customer_name, c.phone, c.email,
         c.address, c.customer_type, c.credit_level, ss.staff_name,
         ss.staff_code, ss.department, ss.position;

-- 10. 更新触发器以维护新字段的一致性
DELIMITER $$

-- 更新销售记录时自动计算total_amount
CREATE TRIGGER tr_sales_record_calculate_total
BEFORE UPDATE ON sales_records
FOR EACH ROW
BEGIN
    -- 自动计算subtotal和total_amount
    IF NEW.sales_amount IS NOT NULL THEN
        SET NEW.subtotal = NEW.sales_amount - COALESCE(NEW.discount_amount, 0);
        SET NEW.total_amount = NEW.subtotal + COALESCE(NEW.shipping_fee, 0);
    END IF;
END$$

-- 插入销售记录时自动计算total_amount
CREATE TRIGGER tr_sales_record_calculate_total_insert
BEFORE INSERT ON sales_records
FOR EACH ROW
BEGIN
    -- 自动计算subtotal和total_amount
    IF NEW.sales_amount IS NOT NULL THEN
        SET NEW.subtotal = NEW.sales_amount - COALESCE(NEW.discount_amount, 0);
        SET NEW.total_amount = NEW.subtotal + COALESCE(NEW.shipping_fee, 0);
    END IF;
END$$

DELIMITER ;

-- 11. 插入一些示例数据以验证结构
-- 注意：这里只插入基础数据，不插入示例销售记录
INSERT IGNORE INTO customers (customer_name, customer_type, phone, email, address, region, industry, credit_level) VALUES
('测试客户A', 'individual', '13800138001', 'test1@example.com', '北京市朝阳区测试街道1号', '北京', '教育', 'A'),
('测试企业B', 'enterprise', '13800138002', 'test2@example.com', '上海市浦东新区测试路2号', '上海', '科技', 'B');

INSERT IGNORE INTO sales_products (product_name, product_code, category, brand, model, unit_price, cost_price, stock_quantity, unit, description) VALUES
('家用练字机器人', 'ROBOT001', '教育设备', '练字科技', '标准版', 2999.00, 2000.00, 100, '台', '智能练字辅导机器人'),
('商用练字机器人', 'ROBOT002', '教育设备', '练字科技', '专业版', 4999.00, 3500.00, 50, '台', '商用级练字辅导机器人');

INSERT IGNORE INTO sales_staff (staff_name, staff_code, department, position, phone, email, sales_target, commission_rate) VALUES
('张销售', 'SALES001', '销售部', '销售经理', '13900139001', 'zhang@example.com', 100000.00, 0.05),
('李销售', 'SALES002', '销售部', '销售专员', '13900139002', 'li@example.com', 80000.00, 0.04);

-- 完成说明
SELECT 
    '销售数据库结构修复完成！' as message,
    '已添加前端页面需要的所有字段' as status,
    '请更新后端Entity和DTO类以匹配新的数据库结构' as next_step;

-- 验证修复结果
SELECT 
    TABLE_NAME,
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE,
    COLUMN_DEFAULT,
    COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'YXRobot' 
  AND TABLE_NAME = 'sales_records'
  AND COLUMN_NAME IN ('shipping_address', 'shipping_fee', 'subtotal', 'total_amount', 'tracking_number', 'shipping_company')
ORDER BY ORDINAL_POSITION;