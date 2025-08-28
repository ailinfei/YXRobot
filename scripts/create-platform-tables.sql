-- 平台链接管理系统数据库表创建脚本
-- 创建时间: 2024-12-22
-- 维护人员: YXRobot开发团队

USE YXRobot;

-- 1. 创建平台链接表
CREATE TABLE IF NOT EXISTS platform_links (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '链接ID，主键',
    platform_name VARCHAR(100) NOT NULL COMMENT '平台名称',
    platform_type ENUM('ecommerce', 'rental') NOT NULL COMMENT '平台类型：电商平台、租赁平台',
    link_url VARCHAR(500) NOT NULL COMMENT '链接地址',
    region VARCHAR(50) NOT NULL COMMENT '地区',
    country VARCHAR(50) NOT NULL COMMENT '国家',
    language_code VARCHAR(10) NOT NULL COMMENT '语言代码',
    language_name VARCHAR(50) NOT NULL COMMENT '语言名称',
    is_enabled TINYINT(1) DEFAULT 1 COMMENT '是否启用：1-启用，0-禁用',
    link_status ENUM('active', 'inactive', 'checking', 'error') DEFAULT 'active' COMMENT '链接状态',
    last_checked_at DATETIME COMMENT '最后检查时间',
    click_count INT DEFAULT 0 COMMENT '点击量',
    conversion_count INT DEFAULT 0 COMMENT '转化量',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '是否删除：1-已删除，0-未删除',
    
    PRIMARY KEY (id),
    INDEX idx_platform_type (platform_type),
    INDEX idx_region (region),
    INDEX idx_language_code (language_code),
    INDEX idx_link_status (link_status),
    INDEX idx_is_enabled (is_enabled),
    INDEX idx_created_at (created_at),
    INDEX idx_is_deleted (is_deleted),
    UNIQUE KEY uk_platform_region_lang (platform_name, region, language_code, is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='平台链接表';

-- 2. 创建链接验证日志表
CREATE TABLE IF NOT EXISTS link_validation_logs (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID，主键',
    link_id BIGINT NOT NULL COMMENT '链接ID',
    is_valid TINYINT(1) NOT NULL COMMENT '是否有效：1-有效，0-无效',
    status_code INT COMMENT 'HTTP状态码',
    response_time INT COMMENT '响应时间（毫秒）',
    error_message TEXT COMMENT '错误信息',
    checked_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '检查时间',
    
    PRIMARY KEY (id),
    INDEX idx_link_id (link_id),
    INDEX idx_checked_at (checked_at),
    INDEX idx_is_valid (is_valid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='链接验证日志表';

-- 3. 创建链接点击日志表
CREATE TABLE IF NOT EXISTS link_click_logs (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID，主键',
    link_id BIGINT NOT NULL COMMENT '链接ID',
    user_ip VARCHAR(45) COMMENT '用户IP地址',
    user_agent TEXT COMMENT '用户代理',
    referer VARCHAR(500) COMMENT '来源页面',
    clicked_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '点击时间',
    is_conversion TINYINT(1) DEFAULT 0 COMMENT '是否转化：1-转化，0-未转化',
    conversion_type VARCHAR(50) COMMENT '转化类型',
    conversion_value DECIMAL(10,2) COMMENT '转化价值',
    
    PRIMARY KEY (id),
    INDEX idx_link_id (link_id),
    INDEX idx_clicked_at (clicked_at),
    INDEX idx_is_conversion (is_conversion),
    INDEX idx_user_ip (user_ip)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='链接点击日志表';

-- 4. 创建区域配置表
CREATE TABLE IF NOT EXISTS region_configs (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '配置ID，主键',
    region VARCHAR(50) NOT NULL COMMENT '地区名称',
    country VARCHAR(50) NOT NULL COMMENT '国家名称',
    language_code VARCHAR(10) NOT NULL COMMENT '语言代码',
    language_name VARCHAR(50) NOT NULL COMMENT '语言名称',
    is_active TINYINT(1) DEFAULT 1 COMMENT '是否激活：1-激活，0-未激活',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    PRIMARY KEY (id),
    INDEX idx_region (region),
    INDEX idx_country (country),
    INDEX idx_language_code (language_code),
    INDEX idx_is_active (is_active),
    INDEX idx_sort_order (sort_order),
    UNIQUE KEY uk_region_language (region, language_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='区域配置表';

-- 显示创建结果
SELECT 'Platform links tables created successfully!' as result;