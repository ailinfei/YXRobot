-- 清理重复的设备相关表
-- 删除空的device_开头的表，保留managed_device_开头的表
-- 这些表都是空的，可以安全删除

USE YXRobot;

-- 安全检查：显示当前设备相关表的数据量
SELECT 'Checking data in device tables before cleanup:' as info;

SELECT 
  'device_specifications' as table_name, 
  COUNT(*) as record_count 
FROM device_specifications
UNION ALL
SELECT 'device_usage_stats', COUNT(*) FROM device_usage_stats
UNION ALL
SELECT 'device_maintenance_records', COUNT(*) FROM device_maintenance_records
UNION ALL
SELECT 'device_configurations', COUNT(*) FROM device_configurations
UNION ALL
SELECT 'device_locations', COUNT(*) FROM device_locations
UNION ALL
SELECT 'device_logs', COUNT(*) FROM device_logs
UNION ALL
SELECT 'device_customer_relation', COUNT(*) FROM device_customer_relation
UNION ALL
SELECT 'device_maintenance_relation', COUNT(*) FROM device_maintenance_relation;

-- 只有在表为空的情况下才删除
-- 删除空的device_开头的表（保留devices主表，因为它被客户管理模块使用）

-- 1. 删除device_specifications表（如果为空）
SET @count = (SELECT COUNT(*) FROM device_specifications);
SET @sql = IF(@count = 0, 'DROP TABLE IF EXISTS device_specifications', 'SELECT "device_specifications table has data, skipping deletion" as warning');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 2. 删除device_usage_stats表（如果为空）
SET @count = (SELECT COUNT(*) FROM device_usage_stats);
SET @sql = IF(@count = 0, 'DROP TABLE IF EXISTS device_usage_stats', 'SELECT "device_usage_stats table has data, skipping deletion" as warning');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 3. 删除device_maintenance_records表（如果为空）
SET @count = (SELECT COUNT(*) FROM device_maintenance_records);
SET @sql = IF(@count = 0, 'DROP TABLE IF EXISTS device_maintenance_records', 'SELECT "device_maintenance_records table has data, skipping deletion" as warning');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 4. 删除device_configurations表（如果为空）
SET @count = (SELECT COUNT(*) FROM device_configurations);
SET @sql = IF(@count = 0, 'DROP TABLE IF EXISTS device_configurations', 'SELECT "device_configurations table has data, skipping deletion" as warning');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 5. 删除device_locations表（如果为空）
SET @count = (SELECT COUNT(*) FROM device_locations);
SET @sql = IF(@count = 0, 'DROP TABLE IF EXISTS device_locations', 'SELECT "device_locations table has data, skipping deletion" as warning');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 6. 删除device_logs表（如果为空）
SET @count = (SELECT COUNT(*) FROM device_logs);
SET @sql = IF(@count = 0, 'DROP TABLE IF EXISTS device_logs', 'SELECT "device_logs table has data, skipping deletion" as warning');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 7. 删除device_customer_relation表（如果为空）
SET @count = (SELECT COUNT(*) FROM device_customer_relation);
SET @sql = IF(@count = 0, 'DROP TABLE IF EXISTS device_customer_relation', 'SELECT "device_customer_relation table has data, skipping deletion" as warning');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 8. 删除device_maintenance_relation表（如果为空）
SET @count = (SELECT COUNT(*) FROM device_maintenance_relation);
SET @sql = IF(@count = 0, 'DROP TABLE IF EXISTS device_maintenance_relation', 'SELECT "device_maintenance_relation table has data, skipping deletion" as warning');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 注意：保留以下表，因为它们被其他模块使用：
-- - devices（被客户管理模块使用）
-- - customer_device_relation（被客户管理模块使用）
-- - device_service_relation（被服务管理模块使用）
-- - rental_devices（被租赁管理模块使用）
-- - device_utilization（用途不明，保留）

-- 验证清理结果
SELECT 'Cleanup completed! Remaining device-related tables:' as info;
SHOW TABLES LIKE '%device%';

SELECT 'Managed device tables (our new tables):' as info;
SHOW TABLES LIKE 'managed_device%';

SELECT 'Cleanup summary completed successfully!' as result;