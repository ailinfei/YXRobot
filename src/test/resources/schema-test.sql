-- 测试数据库表结构
-- 设备管理模块测试表

-- 客户表（简化版，用于测试）
CREATE TABLE IF NOT EXISTS customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 设备管理主表
CREATE TABLE IF NOT EXISTS managed_devices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    serial_number VARCHAR(50) NOT NULL UNIQUE,
    model VARCHAR(50) NOT NULL,
    status VARCHAR(20) DEFAULT 'offline',
    firmware_version VARCHAR(20) NOT NULL,
    customer_id BIGINT NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    customer_phone VARCHAR(20) NOT NULL,
    last_online_at TIMESTAMP NULL,
    activated_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    notes TEXT,
    is_deleted TINYINT DEFAULT 0,
    INDEX idx_customer_id (customer_id),
    INDEX idx_model (model),
    INDEX idx_status (status),
    INDEX idx_serial_number (serial_number),
    INDEX idx_is_deleted (is_deleted)
);

-- 设备技术参数表
CREATE TABLE IF NOT EXISTS managed_device_specifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id BIGINT NOT NULL,
    cpu VARCHAR(100),
    memory VARCHAR(50),
    storage VARCHAR(50),
    display VARCHAR(100),
    battery VARCHAR(50),
    connectivity JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_device_id (device_id)
);

-- 设备使用统计表
CREATE TABLE IF NOT EXISTS managed_device_usage_stats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id BIGINT NOT NULL,
    total_runtime INT DEFAULT 0,
    usage_count INT DEFAULT 0,
    last_used_at TIMESTAMP NULL,
    average_session_time INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_device_id (device_id)
);

-- 设备维护记录表
CREATE TABLE IF NOT EXISTS managed_device_maintenance_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL,
    description TEXT NOT NULL,
    technician VARCHAR(100) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NULL,
    status VARCHAR(20) DEFAULT 'pending',
    cost DECIMAL(10,2),
    parts JSON,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_device_id (device_id),
    INDEX idx_type (type),
    INDEX idx_status (status)
);

-- 设备配置表
CREATE TABLE IF NOT EXISTS managed_device_configurations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id BIGINT NOT NULL,
    language VARCHAR(10) DEFAULT 'zh-CN',
    timezone VARCHAR(50) DEFAULT 'Asia/Shanghai',
    auto_update TINYINT DEFAULT 1,
    debug_mode TINYINT DEFAULT 0,
    custom_settings JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_device_id (device_id)
);

-- 设备位置信息表
CREATE TABLE IF NOT EXISTS managed_device_locations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id BIGINT NOT NULL,
    latitude DECIMAL(10,8),
    longitude DECIMAL(11,8),
    address VARCHAR(500),
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_device_id (device_id)
);

-- 设备日志表
CREATE TABLE IF NOT EXISTS managed_device_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id BIGINT NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    level VARCHAR(10) NOT NULL,
    category VARCHAR(20) NOT NULL,
    message TEXT NOT NULL,
    details JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_device_id (device_id),
    INDEX idx_timestamp (timestamp),
    INDEX idx_level (level),
    INDEX idx_category (category)
);