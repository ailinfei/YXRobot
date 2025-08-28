-- 为customers表添加phone字段（如果不存在）
-- 这是前端Sales.vue页面需要的关键字段

USE YXRobot;

-- 检查并添加phone字段
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'YXRobot' 
  AND TABLE_NAME = 'customers' 
  AND COLUMN_NAME = 'phone';

-- 如果字段不存在则添加
SET @sql = IF(@col_exists = 0, 
  'ALTER TABLE customers ADD COLUMN phone VARCHAR(20) COMMENT ''联系电话'' AFTER contact_person', 
  'SELECT ''phone字段已存在'' as message');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 验证字段是否添加成功
SELECT 
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE,
    COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'YXRobot' 
  AND TABLE_NAME = 'customers'
  AND COLUMN_NAME = 'phone';

-- 插入一些测试数据（如果表为空）
INSERT IGNORE INTO customers (customer_name, customer_type, phone, email, address, region, industry, credit_level) VALUES
('测试客户A', 'individual', '13800138001', 'test1@example.com', '北京市朝阳区测试街道1号', '北京', '教育', 'A'),
('测试企业B', 'enterprise', '13800138002', 'test2@example.com', '上海市浦东新区测试路2号', '上海', '科技', 'B');

INSERT IGNORE INTO sales_products (product_name, product_code, category, brand, model, unit_price, cost_price, stock_quantity, unit, description) VALUES
('家用练字机器人', 'ROBOT001', '教育设备', '练字科技', '标准版', 2999.00, 2000.00, 100, '台', '智能练字辅导机器人'),
('商用练字机器人', 'ROBOT002', '教育设备', '练字科技', '专业版', 4999.00, 3500.00, 50, '台', '商用级练字辅导机器人');

INSERT IGNORE INTO sales_staff (staff_name, staff_code, department, position, phone, email, sales_target, commission_rate) VALUES
('张销售', 'SALES001', '销售部', '销售经理', '13900139001', 'zhang@example.com', 100000.00, 0.05),
('李销售', 'SALES002', '销售部', '销售专员', '13900139002', 'li@example.com', 80000.00, 0.04);

SELECT '✅ customers表phone字段添加完成，基础数据已插入' as result;