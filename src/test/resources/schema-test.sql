-- 测试环境数据库表结构
-- 用于单元测试的简化表结构

-- 客户表
CREATE TABLE IF NOT EXISTS customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(100) NOT NULL,
    customer_type VARCHAR(20) DEFAULT 'INDIVIDUAL',
    customer_level VARCHAR(20) DEFAULT 'REGULAR',
    customer_status VARCHAR(20) DEFAULT 'ACTIVE',
    contact_person VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    avatar_url VARCHAR(255),
    customer_tags JSON,
    notes TEXT,
    address VARCHAR(255),
    region VARCHAR(100),
    industry VARCHAR(50),
    credit_level VARCHAR(20) DEFAULT 'FAIR',
    total_spent DECIMAL(15,2) DEFAULT 0.00,
    customer_value DECIMAL(3,1) DEFAULT 0.0,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_active_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE
);

-- 客户地址表
CREATE TABLE IF NOT EXISTS customer_addresses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    address_type VARCHAR(20) DEFAULT 'HOME',
    province VARCHAR(50),
    city VARCHAR(50),
    district VARCHAR(50),
    street VARCHAR(255),
    postal_code VARCHAR(10),
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE
);

-- 设备表
CREATE TABLE IF NOT EXISTS devices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_name VARCHAR(100) NOT NULL,
    device_type VARCHAR(50) NOT NULL,
    device_model VARCHAR(100),
    device_status VARCHAR(20) DEFAULT 'ACTIVE',
    purchase_date DATE,
    warranty_period INT DEFAULT 12,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE
);

-- 客户设备关联表
CREATE TABLE IF NOT EXISTS customer_device_relation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    device_id BIGINT NOT NULL,
    relation_type VARCHAR(20) DEFAULT 'PURCHASED',
    start_date DATE,
    end_date DATE,
    status TINYINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(50) UNIQUE NOT NULL,
    order_status VARCHAR(20) DEFAULT 'PENDING',
    total_amount DECIMAL(15,2) DEFAULT 0.00,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE
);

-- 客户订单关联表
CREATE TABLE IF NOT EXISTS customer_order_relation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    order_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 服务记录表
CREATE TABLE IF NOT EXISTS service_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    service_type VARCHAR(50) NOT NULL,
    service_description TEXT,
    status VARCHAR(20) DEFAULT 'PENDING',
    service_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE
);

-- 客户服务关联表
CREATE TABLE IF NOT EXISTS customer_service_relation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    service_record_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_customers_name ON customers(customer_name);
CREATE INDEX IF NOT EXISTS idx_customers_phone ON customers(phone);
CREATE INDEX IF NOT EXISTS idx_customers_email ON customers(email);
CREATE INDEX IF NOT EXISTS idx_customers_level ON customers(customer_level);
CREATE INDEX IF NOT EXISTS idx_customers_status ON customers(customer_status);
CREATE INDEX IF NOT EXISTS idx_customers_region ON customers(region);
CREATE INDEX IF NOT EXISTS idx_customers_deleted ON customers(is_deleted);

CREATE INDEX IF NOT EXISTS idx_customer_device_customer ON customer_device_relation(customer_id);
CREATE INDEX IF NOT EXISTS idx_customer_device_device ON customer_device_relation(device_id);
CREATE INDEX IF NOT EXISTS idx_customer_order_customer ON customer_order_relation(customer_id);
CREATE INDEX IF NOT EXISTS idx_customer_service_customer ON customer_service_relation(customer_id);