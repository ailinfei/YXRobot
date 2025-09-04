-- 设备管理模块数据库表结构创建脚本
-- 基于现有前端页面DeviceManagement.vue的功能需求
-- 遵循项目数据库设计规范：禁止外键约束，使用关联表实现表间关系
-- 字段映射规范：数据库使用snake_case，Java实体类使用camelCase

USE YXRobot;

-- 安全检查：显示当前数据库状态
SELECT 'Creating device management tables...' as info;
SELECT 'Current database:' as info, DATABASE() as current_db;

-- 检查现有customers表结构（设备管理需要关联客户信息）
SELECT 'Verifying customers table structure:' as info;
DESCRIBE customers;

-- ========================================
-- 1. 设备管理主表 (managed_devices)
-- 专门用于设备管理模块，与现有devices表共存
-- ========================================
CREATE TABLE IF NOT EXISTS `managed_devices` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '设备ID，主键',
  `serial_number` VARCHAR(50) NOT NULL COMMENT '设备序列号',
  `model` ENUM('YX-EDU-2024', 'YX-HOME-2024', 'YX-PRO-2024') NOT NULL COMMENT '设备型号：教育版、家庭版、专业版',
  `status` ENUM('online', 'offline', 'error', 'maintenance') DEFAULT 'offline' COMMENT '设备状态：在线、离线、故障、维护中',
  `firmware_version` VARCHAR(20) NOT NULL COMMENT '固件版本',
  `customer_id` BIGINT NOT NULL COMMENT '客户ID（引用customers表）',
  `customer_name` VARCHAR(100) NOT NULL COMMENT '客户名称（冗余字段，避免关联查询）',
  `customer_phone` VARCHAR(20) NOT NULL COMMENT '客户电话（冗余字段，避免关联查询）',
  `last_online_at` DATETIME COMMENT '最后在线时间',
  `activated_at` DATETIME COMMENT '激活时间',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` VARCHAR(100) COMMENT '创建人',
  `notes` TEXT COMMENT '设备备注',
  `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除（软删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_serial_number` (`serial_number`),
  INDEX `idx_customer_id` (`customer_id`),
  INDEX `idx_model` (`model`),
  INDEX `idx_status` (`status`),
  INDEX `idx_created_at` (`created_at`),
  INDEX `idx_last_online_at` (`last_online_at`),
  INDEX `idx_activated_at` (`activated_at`),
  INDEX `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备管理主表';

-- ========================================
-- 2. 设备技术参数表 (managed_device_specifications)
-- ========================================
CREATE TABLE IF NOT EXISTS `managed_device_specifications` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '规格ID，主键',
  `device_id` BIGINT NOT NULL COMMENT '设备ID（引用managed_devices表）',
  `cpu` VARCHAR(100) COMMENT 'CPU规格',
  `memory` VARCHAR(50) COMMENT '内存规格',
  `storage` VARCHAR(50) COMMENT '存储规格',
  `display` VARCHAR(100) COMMENT '显示屏规格',
  `battery` VARCHAR(50) COMMENT '电池规格',
  `connectivity` JSON COMMENT '连接性规格（JSON数组）',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_device_id` (`device_id`),
  INDEX `idx_device_id` (`device_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备技术参数表（设备管理模块）';

-- ========================================
-- 3. 设备使用统计表 (managed_device_usage_stats)
-- ========================================
CREATE TABLE IF NOT EXISTS `managed_device_usage_stats` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '统计ID，主键',
  `device_id` BIGINT NOT NULL COMMENT '设备ID（引用managed_devices表）',
  `total_runtime` INT DEFAULT 0 COMMENT '总运行时间（分钟）',
  `usage_count` INT DEFAULT 0 COMMENT '使用次数',
  `last_used_at` DATETIME COMMENT '最后使用时间',
  `average_session_time` INT DEFAULT 0 COMMENT '平均使用时长（分钟）',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_device_id` (`device_id`),
  INDEX `idx_device_id` (`device_id`),
  INDEX `idx_last_used_at` (`last_used_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备使用统计表（设备管理模块）';

-- ========================================
-- 4. 设备维护记录表 (managed_device_maintenance_records)
-- ========================================
CREATE TABLE IF NOT EXISTS `managed_device_maintenance_records` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '维护记录ID，主键',
  `device_id` BIGINT NOT NULL COMMENT '设备ID（引用managed_devices表）',
  `type` ENUM('repair', 'upgrade', 'inspection', 'replacement') NOT NULL COMMENT '维护类型：维修、升级、检查、更换',
  `description` TEXT NOT NULL COMMENT '维护描述',
  `technician` VARCHAR(100) NOT NULL COMMENT '技术员',
  `start_time` DATETIME NOT NULL COMMENT '开始时间',
  `end_time` DATETIME COMMENT '结束时间',
  `status` ENUM('pending', 'in_progress', 'completed', 'cancelled') DEFAULT 'pending' COMMENT '维护状态',
  `cost` DECIMAL(10,2) COMMENT '维护费用',
  `parts` JSON COMMENT '更换部件（JSON数组）',
  `notes` TEXT COMMENT '维护备注',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_device_id` (`device_id`),
  INDEX `idx_type` (`type`),
  INDEX `idx_status` (`status`),
  INDEX `idx_start_time` (`start_time`),
  INDEX `idx_technician` (`technician`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备维护记录表（设备管理模块）';

-- ========================================
-- 5. 设备配置表 (managed_device_configurations)
-- ========================================
CREATE TABLE IF NOT EXISTS `managed_device_configurations` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '配置ID，主键',
  `device_id` BIGINT NOT NULL COMMENT '设备ID（引用managed_devices表）',
  `language` VARCHAR(10) DEFAULT 'zh-CN' COMMENT '语言设置',
  `timezone` VARCHAR(50) DEFAULT 'Asia/Shanghai' COMMENT '时区设置',
  `auto_update` TINYINT(1) DEFAULT 1 COMMENT '自动更新',
  `debug_mode` TINYINT(1) DEFAULT 0 COMMENT '调试模式',
  `custom_settings` JSON COMMENT '自定义设置（JSON对象）',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_device_id` (`device_id`),
  INDEX `idx_device_id` (`device_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备配置表（设备管理模块）';

-- ========================================
-- 6. 设备位置信息表 (managed_device_locations)
-- ========================================
CREATE TABLE IF NOT EXISTS `managed_device_locations` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '位置ID，主键',
  `device_id` BIGINT NOT NULL COMMENT '设备ID（引用managed_devices表）',
  `latitude` DECIMAL(10,8) COMMENT '纬度',
  `longitude` DECIMAL(11,8) COMMENT '经度',
  `address` VARCHAR(500) COMMENT '地址',
  `last_updated` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_device_id` (`device_id`),
  INDEX `idx_device_id` (`device_id`),
  INDEX `idx_latitude_longitude` (`latitude`, `longitude`),
  INDEX `idx_last_updated` (`last_updated`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备位置信息表（设备管理模块）';

-- ========================================
-- 7. 设备日志表 (managed_device_logs)
-- ========================================
CREATE TABLE IF NOT EXISTS `managed_device_logs` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID，主键',
  `device_id` BIGINT NOT NULL COMMENT '设备ID（引用managed_devices表）',
  `timestamp` DATETIME NOT NULL COMMENT '时间戳',
  `level` ENUM('info', 'warning', 'error', 'debug') NOT NULL COMMENT '日志级别',
  `category` ENUM('system', 'user', 'network', 'hardware', 'software') NOT NULL COMMENT '日志分类',
  `message` TEXT NOT NULL COMMENT '日志消息',
  `details` JSON COMMENT '详细信息（JSON对象）',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_device_id` (`device_id`),
  INDEX `idx_timestamp` (`timestamp`),
  INDEX `idx_level` (`level`),
  INDEX `idx_category` (`category`),
  INDEX `idx_device_timestamp` (`device_id`, `timestamp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备日志表（设备管理模块）';

-- ========================================
-- 8. 设备客户关联表 (managed_device_customer_relation)
-- 遵循项目规范：禁止外键约束，使用关联表实现表间关系
-- ========================================
CREATE TABLE IF NOT EXISTS `managed_device_customer_relation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID，主键',
  `device_id` BIGINT NOT NULL COMMENT '设备ID（引用managed_devices表）',
  `customer_id` BIGINT NOT NULL COMMENT '客户ID',
  `relation_type` ENUM('owner', 'user', 'maintainer') DEFAULT 'owner' COMMENT '关联类型：拥有者、使用者、维护者',
  `start_date` DATE NOT NULL COMMENT '关联开始日期',
  `end_date` DATE COMMENT '关联结束日期',
  `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` TINYINT DEFAULT 1 COMMENT '状态：1-有效，0-无效',
  PRIMARY KEY (`id`),
  INDEX `idx_device_id` (`device_id`),
  INDEX `idx_customer_id` (`customer_id`),
  INDEX `idx_relation_type` (`relation_type`),
  INDEX `idx_status` (`status`),
  UNIQUE KEY `uk_device_customer_type` (`device_id`, `customer_id`, `relation_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备客户关联表（设备管理模块）';

-- ========================================
-- 9. 设备维护关联表 (managed_device_maintenance_relation)
-- ========================================
CREATE TABLE IF NOT EXISTS `managed_device_maintenance_relation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID，主键',
  `device_id` BIGINT NOT NULL COMMENT '设备ID（引用managed_devices表）',
  `maintenance_id` BIGINT NOT NULL COMMENT '维护记录ID',
  `relation_type` ENUM('primary', 'secondary', 'affected') DEFAULT 'primary' COMMENT '关联类型：主要、次要、受影响',
  `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` TINYINT DEFAULT 1 COMMENT '状态：1-有效，0-无效',
  PRIMARY KEY (`id`),
  INDEX `idx_device_id` (`device_id`),
  INDEX `idx_maintenance_id` (`maintenance_id`),
  INDEX `idx_relation_type` (`relation_type`),
  INDEX `idx_status` (`status`),
  UNIQUE KEY `uk_device_maintenance_type` (`device_id`, `maintenance_id`, `relation_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备维护关联表（设备管理模块）';

-- ========================================
-- 数据库连接池优化配置验证
-- ========================================
SELECT 'Verifying database connection pool configuration:' as info;
SHOW VARIABLES LIKE 'max_connections';
SHOW VARIABLES LIKE 'wait_timeout';
SHOW VARIABLES LIKE 'interactive_timeout';

-- ========================================
-- 插入基础测试数据
-- ========================================
SELECT 'Inserting basic test data...' as info;

-- 插入测试设备数据（基于现有customers表数据）
INSERT INTO `managed_devices` (
  `serial_number`, `model`, `status`, `firmware_version`, 
  `customer_id`, `customer_name`, `customer_phone`,
  `last_online_at`, `activated_at`, `created_by`, `notes`
) VALUES 
-- 教育版设备
('YX-EDU-001', 'YX-EDU-2024', 'online', '1.2.3', 1, '[客户姓名]', '[客户电话]', NOW(), DATE_SUB(NOW(), INTERVAL 30 DAY), 'system', '教育版练字机器人，适用于学校教学'),
('YX-EDU-002', 'YX-EDU-2024', 'offline', '1.2.2', 2, '[客户姓名]', '[客户电话]', DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_SUB(NOW(), INTERVAL 25 DAY), 'system', '教育版练字机器人，学生专用'),
('YX-EDU-003', 'YX-EDU-2024', 'maintenance', '1.2.3', 3, '[客户姓名]', '[客户电话]', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY), 'system', '教育版练字机器人，维护中'),

-- 家庭版设备
('YX-HOME-001', 'YX-HOME-2024', 'online', '2.1.5', 4, '[客户姓名]', '[客户电话]', NOW(), DATE_SUB(NOW(), INTERVAL 15 DAY), 'system', '家庭版练字机器人，家庭使用'),
('YX-HOME-002', 'YX-HOME-2024', 'error', '2.1.4', 5, '[客户姓名]', '[客户电话]', DATE_SUB(NOW(), INTERVAL 3 HOUR), DATE_SUB(NOW(), INTERVAL 10 DAY), 'system', '家庭版练字机器人，故障待修'),
('YX-HOME-003', 'YX-HOME-2024', 'online', '2.1.5', 6, '[客户姓名]', '[客户电话]', DATE_SUB(NOW(), INTERVAL 30 MINUTE), DATE_SUB(NOW(), INTERVAL 5 DAY), 'system', '家庭版练字机器人，正常使用'),

-- 专业版设备
('YX-PRO-001', 'YX-PRO-2024', 'online', '3.0.1', 7, '[客户姓名]', '[客户电话]', NOW(), DATE_SUB(NOW(), INTERVAL 7 DAY), 'system', '专业版练字机器人，商业用途'),
('YX-PRO-002', 'YX-PRO-2024', 'offline', '3.0.0', 8, '[客户姓名]', '[客户电话]', DATE_SUB(NOW(), INTERVAL 5 HOUR), DATE_SUB(NOW(), INTERVAL 3 DAY), 'system', '专业版练字机器人，培训机构使用'),
('YX-PRO-003', 'YX-PRO-2024', 'maintenance', '3.0.1', 9, '[客户姓名]', '[客户电话]', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), 'system', '专业版练字机器人，定期维护');

-- 插入设备技术参数数据
INSERT INTO `managed_device_specifications` (
  `device_id`, `cpu`, `memory`, `storage`, `display`, `battery`, `connectivity`
) VALUES 
(1, 'ARM Cortex-A72 1.5GHz', '4GB LPDDR4', '32GB eMMC', '10.1寸 1920x1200 IPS', '5000mAh锂电池', '["WiFi 802.11ac", "蓝牙5.0", "4G LTE"]'),
(2, 'ARM Cortex-A72 1.5GHz', '4GB LPDDR4', '32GB eMMC', '10.1寸 1920x1200 IPS', '5000mAh锂电池', '["WiFi 802.11ac", "蓝牙5.0"]'),
(3, 'ARM Cortex-A72 1.5GHz', '4GB LPDDR4', '32GB eMMC', '10.1寸 1920x1200 IPS', '5000mAh锂电池', '["WiFi 802.11ac", "蓝牙5.0", "4G LTE"]'),
(4, 'ARM Cortex-A53 1.2GHz', '2GB LPDDR3', '16GB eMMC', '8寸 1280x800 IPS', '3000mAh锂电池', '["WiFi 802.11n", "蓝牙4.2"]'),
(5, 'ARM Cortex-A53 1.2GHz', '2GB LPDDR3', '16GB eMMC', '8寸 1280x800 IPS', '3000mAh锂电池', '["WiFi 802.11n", "蓝牙4.2"]'),
(6, 'ARM Cortex-A53 1.2GHz', '2GB LPDDR3', '16GB eMMC', '8寸 1280x800 IPS', '3000mAh锂电池', '["WiFi 802.11n", "蓝牙4.2"]'),
(7, 'ARM Cortex-A76 2.0GHz', '8GB LPDDR5', '128GB UFS', '12.9寸 2732x2048 OLED', '8000mAh锂电池', '["WiFi 6", "蓝牙5.2", "5G", "以太网"]'),
(8, 'ARM Cortex-A76 2.0GHz', '8GB LPDDR5', '128GB UFS', '12.9寸 2732x2048 OLED', '8000mAh锂电池', '["WiFi 6", "蓝牙5.2", "5G"]'),
(9, 'ARM Cortex-A76 2.0GHz', '8GB LPDDR5', '128GB UFS', '12.9寸 2732x2048 OLED', '8000mAh锂电池', '["WiFi 6", "蓝牙5.2", "5G", "以太网"]');

-- 插入设备使用统计数据
INSERT INTO `managed_device_usage_stats` (
  `device_id`, `total_runtime`, `usage_count`, `last_used_at`, `average_session_time`
) VALUES 
(1, 1800, 120, NOW(), 15),
(2, 960, 64, DATE_SUB(NOW(), INTERVAL 2 HOUR), 15),
(3, 720, 48, DATE_SUB(NOW(), INTERVAL 1 DAY), 15),
(4, 450, 30, DATE_SUB(NOW(), INTERVAL 30 MINUTE), 15),
(5, 300, 20, DATE_SUB(NOW(), INTERVAL 3 HOUR), 15),
(6, 600, 40, DATE_SUB(NOW(), INTERVAL 1 HOUR), 15),
(7, 2400, 80, NOW(), 30),
(8, 1200, 40, DATE_SUB(NOW(), INTERVAL 5 HOUR), 30),
(9, 900, 30, DATE_SUB(NOW(), INTERVAL 2 DAY), 30);

-- 插入设备维护记录数据
INSERT INTO `managed_device_maintenance_records` (
  `device_id`, `type`, `description`, `technician`, `start_time`, `end_time`, `status`, `cost`, `notes`
) VALUES 
(3, 'repair', '屏幕触摸不灵敏，需要更换触摸屏模块', '[技术员姓名]', DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, 'in_progress', 350.00, '客户反馈触摸响应延迟'),
(5, 'inspection', '设备定期检查，发现电池老化问题', '[技术员姓名]', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), 'completed', 0.00, '建议更换电池'),
(9, 'upgrade', '固件升级到最新版本3.0.1', '[技术员姓名]', DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, 'in_progress', 0.00, '升级新功能模块');

-- 插入设备配置数据
INSERT INTO `managed_device_configurations` (
  `device_id`, `language`, `timezone`, `auto_update`, `debug_mode`, `custom_settings`
) VALUES 
(1, 'zh-CN', 'Asia/Shanghai', 1, 0, '{"theme": "light", "font_size": "medium", "sound_enabled": true}'),
(2, 'zh-CN', 'Asia/Shanghai', 1, 0, '{"theme": "light", "font_size": "large", "sound_enabled": false}'),
(3, 'zh-CN', 'Asia/Shanghai', 1, 1, '{"theme": "dark", "font_size": "medium", "sound_enabled": true}'),
(4, 'zh-CN', 'Asia/Shanghai', 1, 0, '{"theme": "light", "font_size": "small", "sound_enabled": true}'),
(5, 'zh-CN', 'Asia/Shanghai', 0, 0, '{"theme": "light", "font_size": "medium", "sound_enabled": true}'),
(6, 'zh-CN', 'Asia/Shanghai', 1, 0, '{"theme": "light", "font_size": "medium", "sound_enabled": false}'),
(7, 'zh-CN', 'Asia/Shanghai', 1, 0, '{"theme": "professional", "font_size": "large", "sound_enabled": true, "multi_user": true}'),
(8, 'zh-CN', 'Asia/Shanghai', 1, 0, '{"theme": "professional", "font_size": "medium", "sound_enabled": true, "multi_user": true}'),
(9, 'zh-CN', 'Asia/Shanghai', 1, 1, '{"theme": "professional", "font_size": "large", "sound_enabled": false, "multi_user": true}');

-- 插入设备位置信息数据
INSERT INTO `managed_device_locations` (
  `device_id`, `latitude`, `longitude`, `address`, `last_updated`
) VALUES 
(1, 39.9042, 116.4074, '[地址信息]', NOW()),
(2, 31.2304, 121.4737, '[地址信息]', DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(3, 22.3193, 114.1694, '[地址信息]', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(4, 30.5728, 104.0668, '[地址信息]', DATE_SUB(NOW(), INTERVAL 30 MINUTE)),
(5, 36.0611, 120.3785, '[地址信息]', DATE_SUB(NOW(), INTERVAL 3 HOUR)),
(6, 29.5647, 106.5507, '[地址信息]', DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(7, 32.0603, 118.7969, '[地址信息]', NOW()),
(8, 26.0745, 119.2965, '[地址信息]', DATE_SUB(NOW(), INTERVAL 5 HOUR)),
(9, 38.0428, 114.5149, '[地址信息]', DATE_SUB(NOW(), INTERVAL 2 DAY));

-- 插入设备日志数据
INSERT INTO `managed_device_logs` (
  `device_id`, `timestamp`, `level`, `category`, `message`, `details`
) VALUES 
-- 设备1的日志
(1, NOW(), 'info', 'system', '设备启动成功', '{"boot_time": "2.3s", "system_version": "1.2.3"}'),
(1, DATE_SUB(NOW(), INTERVAL 1 HOUR), 'info', 'user', '用户开始练字会话', '{"user_id": "user001", "session_id": "sess001"}'),
(1, DATE_SUB(NOW(), INTERVAL 2 HOUR), 'warning', 'hardware', '电池电量低于20%', '{"battery_level": 18, "charging_status": false}'),

-- 设备2的日志
(2, DATE_SUB(NOW(), INTERVAL 2 HOUR), 'error', 'network', '网络连接失败', '{"error_code": "NET_001", "retry_count": 3}'),
(2, DATE_SUB(NOW(), INTERVAL 3 HOUR), 'info', 'system', '固件更新检查', '{"current_version": "1.2.2", "latest_version": "1.2.3"}'),

-- 设备5的日志
(5, DATE_SUB(NOW(), INTERVAL 3 HOUR), 'error', 'hardware', '触摸屏响应异常', '{"error_code": "HW_005", "affected_area": "bottom_right"}'),
(5, DATE_SUB(NOW(), INTERVAL 4 HOUR), 'warning', 'system', '存储空间不足', '{"available_space": "2.1GB", "total_space": "16GB"}');

-- 插入设备客户关联数据
INSERT INTO `managed_device_customer_relation` (
  `device_id`, `customer_id`, `relation_type`, `start_date`
) VALUES 
(1, 1, 'owner', DATE_SUB(NOW(), INTERVAL 30 DAY)),
(2, 2, 'owner', DATE_SUB(NOW(), INTERVAL 25 DAY)),
(3, 3, 'owner', DATE_SUB(NOW(), INTERVAL 20 DAY)),
(4, 4, 'owner', DATE_SUB(NOW(), INTERVAL 15 DAY)),
(5, 5, 'owner', DATE_SUB(NOW(), INTERVAL 10 DAY)),
(6, 6, 'owner', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(7, 7, 'owner', DATE_SUB(NOW(), INTERVAL 7 DAY)),
(8, 8, 'owner', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(9, 9, 'owner', DATE_SUB(NOW(), INTERVAL 1 DAY));

-- 插入设备维护关联数据
INSERT INTO `managed_device_maintenance_relation` (
  `device_id`, `maintenance_id`, `relation_type`
) VALUES 
(3, 1, 'primary'),
(5, 2, 'primary'),
(9, 3, 'primary');

-- ========================================
-- 验证表创建和数据插入结果
-- ========================================
SELECT 'Device management tables created successfully!' as result;

-- 显示所有设备管理相关的表
SELECT 'Device management tables:' as info;
SHOW TABLES LIKE 'managed_device%';

-- 验证数据插入结果
SELECT 'Data verification:' as info;
SELECT 
  'managed_devices' as table_name,
  COUNT(*) as total_records,
  COUNT(CASE WHEN status = 'online' THEN 1 END) as online_devices,
  COUNT(CASE WHEN status = 'offline' THEN 1 END) as offline_devices,
  COUNT(CASE WHEN status = 'error' THEN 1 END) as error_devices,
  COUNT(CASE WHEN status = 'maintenance' THEN 1 END) as maintenance_devices
FROM managed_devices
UNION ALL
SELECT 'managed_device_specifications', COUNT(*), 0, 0, 0, 0 FROM managed_device_specifications
UNION ALL
SELECT 'managed_device_usage_stats', COUNT(*), 0, 0, 0, 0 FROM managed_device_usage_stats
UNION ALL
SELECT 'managed_device_maintenance_records', COUNT(*), 0, 0, 0, 0 FROM managed_device_maintenance_records
UNION ALL
SELECT 'managed_device_configurations', COUNT(*), 0, 0, 0, 0 FROM managed_device_configurations
UNION ALL
SELECT 'managed_device_locations', COUNT(*), 0, 0, 0, 0 FROM managed_device_locations
UNION ALL
SELECT 'managed_device_logs', COUNT(*), 0, 0, 0, 0 FROM managed_device_logs
UNION ALL
SELECT 'managed_device_customer_relation', COUNT(*), 0, 0, 0, 0 FROM managed_device_customer_relation
UNION ALL
SELECT 'managed_device_maintenance_relation', COUNT(*), 0, 0, 0, 0 FROM managed_device_maintenance_relation;

-- 验证设备型号分布
SELECT 'Device model distribution:' as info;
SELECT 
  model,
  COUNT(*) as device_count,
  COUNT(CASE WHEN status = 'online' THEN 1 END) as online_count,
  COUNT(CASE WHEN status = 'offline' THEN 1 END) as offline_count,
  COUNT(CASE WHEN status = 'error' THEN 1 END) as error_count,
  COUNT(CASE WHEN status = 'maintenance' THEN 1 END) as maintenance_count
FROM managed_devices 
WHERE is_deleted = 0
GROUP BY model;

-- 验证索引创建
SELECT 'Index verification:' as info;
SELECT 
  TABLE_NAME,
  INDEX_NAME,
  COLUMN_NAME,
  NON_UNIQUE
FROM INFORMATION_SCHEMA.STATISTICS 
WHERE TABLE_SCHEMA = 'YXRobot' 
  AND TABLE_NAME LIKE 'managed_device%'
ORDER BY TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX;

-- 验证数据库连接池配置
SELECT 'Database connection pool verification:' as info;
SHOW STATUS LIKE 'Threads_connected';
SHOW STATUS LIKE 'Threads_running';
SHOW STATUS LIKE 'Max_used_connections';

SELECT 'Device management database setup completed successfully!' as final_result;
SELECT 'All tables created with proper indexes and test data inserted!' as confirmation;
SELECT 'Database follows project standards: no foreign key constraints, relation tables used!' as compliance;