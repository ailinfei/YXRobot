-- 设备管理搜索性能优化索引脚本
-- 用于提升设备搜索和筛选功能的查询性能
-- 
-- 执行方式：
-- mysql -h yun.finiot.cn -P 3306 -u YXRobot -p2200548qq YXRobot < optimize-device-search-indexes.sql

USE YXRobot;

-- ==================== 设备管理表索引优化 ====================

-- 1. 基础查询索引
-- 序列号索引（唯一性约束和快速查找）
CREATE UNIQUE INDEX idx_managed_devices_serial_number ON managed_devices(serial_number) 
COMMENT '设备序列号唯一索引，用于快速查找和唯一性约束';

-- 软删除标记索引
CREATE INDEX idx_managed_devices_is_deleted ON managed_devices(is_deleted) 
COMMENT '软删除标记索引，用于过滤已删除记录';

-- 2. 状态和型号筛选索引
-- 设备状态索引
CREATE INDEX idx_managed_devices_status ON managed_devices(status) 
COMMENT '设备状态索引，用于状态筛选查询';

-- 设备型号索引
CREATE INDEX idx_managed_devices_model ON managed_devices(model) 
COMMENT '设备型号索引，用于型号筛选查询';

-- 状态和型号组合索引
CREATE INDEX idx_managed_devices_status_model ON managed_devices(status, model, is_deleted) 
COMMENT '状态和型号组合索引，用于多条件筛选';

-- 3. 客户相关索引
-- 客户ID索引
CREATE INDEX idx_managed_devices_customer_id ON managed_devices(customer_id) 
COMMENT '客户ID索引，用于按客户筛选设备';

-- 客户名称索引（支持模糊搜索）
CREATE INDEX idx_managed_devices_customer_name ON managed_devices(customer_name) 
COMMENT '客户名称索引，用于客户名称搜索';

-- 客户ID和状态组合索引
CREATE INDEX idx_managed_devices_customer_status ON managed_devices(customer_id, status, is_deleted) 
COMMENT '客户和状态组合索引，用于客户设备状态查询';

-- 4. 时间范围查询索引
-- 创建时间索引
CREATE INDEX idx_managed_devices_created_at ON managed_devices(created_at) 
COMMENT '创建时间索引，用于时间范围查询和排序';

-- 更新时间索引
CREATE INDEX idx_managed_devices_updated_at ON managed_devices(updated_at) 
COMMENT '更新时间索引，用于时间范围查询';

-- 最后在线时间索引
CREATE INDEX idx_managed_devices_last_online_at ON managed_devices(last_online_at) 
COMMENT '最后在线时间索引，用于在线状态判断';

-- 激活时间索引
CREATE INDEX idx_managed_devices_activated_at ON managed_devices(activated_at) 
COMMENT '激活时间索引，用于激活状态查询';

-- 5. 固件版本索引
-- 固件版本索引
CREATE INDEX idx_managed_devices_firmware_version ON managed_devices(firmware_version) 
COMMENT '固件版本索引，用于固件版本筛选';

-- 6. 复合搜索索引
-- 全文搜索复合索引（序列号、客户名称、备注）
CREATE INDEX idx_managed_devices_search_text ON managed_devices(serial_number, customer_name, notes) 
COMMENT '文本搜索复合索引，用于关键词搜索';

-- 状态、型号、时间复合索引
CREATE INDEX idx_managed_devices_status_model_time ON managed_devices(status, model, created_at, is_deleted) 
COMMENT '状态、型号、时间复合索引，用于多维度筛选';

-- 7. 创建人和备注索引
-- 创建人索引
CREATE INDEX idx_managed_devices_created_by ON managed_devices(created_by) 
COMMENT '创建人索引，用于按创建人筛选';

-- 8. 在线状态判断优化索引
-- 在线状态复合索引（基于最后在线时间和状态）
CREATE INDEX idx_managed_devices_online_status ON managed_devices(last_online_at, status, is_deleted) 
COMMENT '在线状态复合索引，用于在线/离线设备查询';

-- 9. 激活状态优化索引
-- 激活状态复合索引
CREATE INDEX idx_managed_devices_activation_status ON managed_devices(activated_at, status, is_deleted) 
COMMENT '激活状态复合索引，用于激活/未激活设备查询';

-- ==================== 关联表索引优化 ====================

-- 设备技术参数表索引
CREATE INDEX idx_managed_device_specifications_device_id ON managed_device_specifications(device_id) 
COMMENT '设备技术参数表设备ID索引';

-- 设备使用统计表索引
CREATE INDEX idx_managed_device_usage_stats_device_id ON managed_device_usage_stats(device_id) 
COMMENT '设备使用统计表设备ID索引';

CREATE INDEX idx_managed_device_usage_stats_last_used ON managed_device_usage_stats(last_used_at) 
COMMENT '设备使用统计表最后使用时间索引';

-- 设备维护记录表索引
CREATE INDEX idx_managed_device_maintenance_device_id ON managed_device_maintenance_records(device_id) 
COMMENT '设备维护记录表设备ID索引';

CREATE INDEX idx_managed_device_maintenance_date ON managed_device_maintenance_records(maintenance_date) 
COMMENT '设备维护记录表维护时间索引';

CREATE INDEX idx_managed_device_maintenance_type ON managed_device_maintenance_records(maintenance_type) 
COMMENT '设备维护记录表维护类型索引';

-- 设备配置表索引
CREATE INDEX idx_managed_device_configurations_device_id ON managed_device_configurations(device_id) 
COMMENT '设备配置表设备ID索引';

-- 设备位置信息表索引
CREATE INDEX idx_managed_device_locations_device_id ON managed_device_locations(device_id) 
COMMENT '设备位置信息表设备ID索引';

-- 设备日志表索引
CREATE INDEX idx_managed_device_logs_device_id ON managed_device_logs(device_id) 
COMMENT '设备日志表设备ID索引';

CREATE INDEX idx_managed_device_logs_timestamp ON managed_device_logs(timestamp) 
COMMENT '设备日志表时间戳索引';

CREATE INDEX idx_managed_device_logs_level ON managed_device_logs(level) 
COMMENT '设备日志表日志级别索引';

CREATE INDEX idx_managed_device_logs_category ON managed_device_logs(category) 
COMMENT '设备日志表日志分类索引';

-- 设备日志复合索引（设备ID + 时间戳）
CREATE INDEX idx_managed_device_logs_device_time ON managed_device_logs(device_id, timestamp) 
COMMENT '设备日志复合索引，用于按设备查询日志';

-- ==================== 性能优化建议 ====================

-- 1. 查询优化提示
-- 以下查询模式将获得最佳性能：

-- 快速查找设备（使用序列号）
-- SELECT * FROM managed_devices WHERE serial_number = 'YX2025001234' AND is_deleted = 0;

-- 按状态筛选设备
-- SELECT * FROM managed_devices WHERE status = 'online' AND is_deleted = 0 ORDER BY created_at DESC;

-- 按客户查询设备
-- SELECT * FROM managed_devices WHERE customer_id = 123 AND is_deleted = 0 ORDER BY created_at DESC;

-- 时间范围查询
-- SELECT * FROM managed_devices WHERE created_at >= '2025-01-01' AND created_at <= '2025-01-31' AND is_deleted = 0;

-- 在线设备查询
-- SELECT * FROM managed_devices WHERE last_online_at >= DATE_SUB(NOW(), INTERVAL 5 MINUTE) AND is_deleted = 0;

-- 2. 索引使用监控
-- 可以使用以下查询监控索引使用情况：
-- SHOW INDEX FROM managed_devices;
-- EXPLAIN SELECT * FROM managed_devices WHERE status = 'online' AND is_deleted = 0;

-- 3. 定期维护建议
-- 定期执行以下命令优化表性能：
-- ANALYZE TABLE managed_devices;
-- OPTIMIZE TABLE managed_devices;

-- ==================== 索引创建完成提示 ====================

SELECT 'Device Management Search Indexes Created Successfully!' as Status,
       COUNT(*) as Total_Indexes
FROM information_schema.statistics 
WHERE table_schema = 'YXRobot' 
  AND table_name LIKE 'managed_device%'
  AND index_name != 'PRIMARY';

-- 显示所有创建的索引
SELECT 
    table_name as 'Table Name',
    index_name as 'Index Name',
    column_name as 'Column Name',
    index_comment as 'Comment'
FROM information_schema.statistics 
WHERE table_schema = 'YXRobot' 
  AND table_name LIKE 'managed_device%'
  AND index_name != 'PRIMARY'
ORDER BY table_name, index_name, seq_in_index;