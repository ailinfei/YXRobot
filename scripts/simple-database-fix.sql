-- 简化的数据库字段修复脚本
-- 只添加必要的字段，避免复杂的存储过程和触发器

USE YXRobot;

-- 1. 检查并添加customers表的phone字段
ALTER TABLE customers 
ADD COLUMN IF NOT EXISTS phone VARCHAR(20) COMMENT '联系电话' AFTER contact_person;

-- 2. 为sales_records表添加必要的字段
ALTER TABLE sales_records 
ADD COLUMN IF NOT EXISTS shipping_address VARCHAR(500) COMMENT '收货地址' AFTER notes,
ADD COLUMN IF NOT EXISTS shipping_fee DECIMAL(10,2) DEFAULT 0.00 COMMENT '运费' AFTER shipping_address,
ADD COLUMN IF NOT EXISTS subtotal DECIMAL(10,2) COMMENT '商品小计金额' AFTER shipping_fee,
ADD COLUMN IF NOT EXISTS total_amount DECIMAL(10,2) COMMENT '订单总金额（含运费）' AFTER subtotal,
ADD COLUMN IF NOT EXISTS tracking_number VARCHAR(100) COMMENT '快递单号' AFTER total_amount,
ADD COLUMN IF NOT EXISTS shipping_company VARCHAR(100) COMMENT '快递公司' AFTER tracking_number;

-- 3. 为sales_products表添加产品图片字段
ALTER TABLE sales_products 
ADD COLUMN IF NOT EXISTS product_image VARCHAR(500) COMMENT '产品图片URL' AFTER description;

-- 4. 更新现有数据，计算total_amount和subtotal
UPDATE sales_records 
SET 
  subtotal = COALESCE(sales_amount - COALESCE(discount_amount, 0), sales_amount),
  total_amount = COALESCE(sales_amount + COALESCE(shipping_fee, 0), sales_amount)
WHERE subtotal IS NULL OR total_amount IS NULL;

-- 5. 添加索引以提高查询性能
CREATE INDEX IF NOT EXISTS idx_sales_records_shipping_address ON sales_records (shipping_address(100));
CREATE INDEX IF NOT EXISTS idx_sales_records_tracking_number ON sales_records (tracking_number);
CREATE INDEX IF NOT EXISTS idx_sales_records_total_amount ON sales_records (total_amount);

-- 6. 插入基础测试数据（如果表为空）
INSERT IGNORE INTO customers (customer_name, customer_type, phone, email, address, region, industry, credit_level) VALUES
('测试客户A', 'individual', '13800138001', 'test1@example.com', '北京市朝阳区测试街道1号', '北京', '教育', 'A'),
('测试企业B', 'enterprise', '13800138002', 'test2@example.com', '上海市浦东新区测试路2号', '上海', '科技', 'B'),
('练字教育机构', 'enterprise', '13800138003', 'edu@example.com', '广州市天河区教育路3号', '广州', '教育', 'A');

INSERT IGNORE INTO sales_products (product_name, product_code, category, brand, model, unit_price, cost_price, stock_quantity, unit, description) VALUES
('家用练字机器人', 'ROBOT001', '教育设备', '练字科技', '标准版', 2999.00, 2000.00, 100, '台', '智能练字辅导机器人'),
('商用练字机器人', 'ROBOT002', '教育设备', '练字科技', '专业版', 4999.00, 3500.00, 50, '台', '商用级练字辅导机器人'),
('练字笔芯套装', 'PEN001', '配件', '练字科技', '通用型', 99.00, 50.00, 500, '套', '专用练字笔芯，10支装');

INSERT IGNORE INTO sales_staff (staff_name, staff_code, department, position, phone, email, sales_target, commission_rate) VALUES
('张销售', 'SALES001', '销售部', '销售经理', '13900139001', 'zhang@example.com', 100000.00, 0.05),
('李销售', 'SALES002', '销售部', '销售专员', '13900139002', 'li@example.com', 80000.00, 0.04),
('王销售', 'SALES003', '销售部', '高级销售', '13900139003', 'wang@example.com', 120000.00, 0.06);

-- 验证修复结果
SELECT 
    '✅ 数据库字段修复完成' as status,
    '已添加必要的字段和基础数据' as message;

-- 显示新增字段
SELECT 
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE,
    COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'YXRobot' 
  AND TABLE_NAME = 'sales_records'
  AND COLUMN_NAME IN ('shipping_address', 'shipping_fee', 'subtotal', 'total_amount', 'tracking_number', 'shipping_company')
ORDER BY ORDINAL_POSITION;