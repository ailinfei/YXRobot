package com.yxrobot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * 数据初始化服务
 * 负责系统启动时的数据初始化和迁移
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-25
 */
@Service
@Order(1)
public class DataInitializationService implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializationService.class);
    
    @Autowired
    private DataSource dataSource;
    
    @Override
    public void run(String... args) throws Exception {
        logger.info("开始执行数据初始化...");
        
        try {
            // 1. 检查数据库连接
            checkDatabaseConnection();
            
            // 2. 初始化平台链接数据
            initializePlatformLinksData();
            
            // 3. 初始化区域配置数据
            initializeRegionConfigData();
            
            // 4. 初始化新闻管理数据
            initializeNewsData();
            
            // 5. 初始化租赁基础数据（不包含示例租赁记录）
            initializeRentalBaseData();
            
            // 6. 初始化客户管理基础数据（不包含示例客户数据）
            initializeCustomerBaseData();
            
            // 7. 验证数据完整性
            validateDataIntegrity();
            
            logger.info("数据初始化完成");
            
        } catch (Exception e) {
            logger.error("数据初始化失败", e);
            throw e;
        }
    }
    
    /**
     * 检查数据库连接
     */
    private void checkDatabaseConnection() throws SQLException {
        logger.info("检查数据库连接...");
        
        try (Connection connection = dataSource.getConnection()) {
            logger.info("数据库连接成功 - URL: {}", connection.getMetaData().getURL());
        }
    }
    
    /**
     * 初始化平台链接数据
     */
    private void initializePlatformLinksData() throws SQLException {
        logger.info("初始化平台链接数据...");
        
        try (Connection connection = dataSource.getConnection()) {
            // 检查是否已有数据
            if (hasExistingPlatformLinks(connection)) {
                logger.info("平台链接数据已存在，跳过初始化");
                return;
            }
            
            // 插入初始数据
            insertInitialPlatformLinks(connection);
            logger.info("平台链接初始数据插入完成");
        }
    }
    
    /**
     * 初始化区域配置数据
     */
    private void initializeRegionConfigData() throws SQLException {
        logger.info("初始化区域配置数据...");
        
        try (Connection connection = dataSource.getConnection()) {
            // 检查是否已有数据
            if (hasExistingRegionConfig(connection)) {
                logger.info("区域配置数据已存在，跳过初始化");
                return;
            }
            
            // 插入初始数据
            insertInitialRegionConfig(connection);
            logger.info("区域配置初始数据插入完成");
        }
    }
    
    /**
     * 初始化新闻管理数据
     */
    private void initializeNewsData() throws SQLException {
        logger.info("初始化新闻管理数据...");
        
        try (Connection connection = dataSource.getConnection()) {
            // 初始化新闻分类
            if (!hasExistingNewsCategories(connection)) {
                insertInitialNewsCategories(connection);
                logger.info("新闻分类初始数据插入完成");
            } else {
                logger.info("新闻分类数据已存在，跳过初始化");
            }
            
            // 初始化新闻标签
            if (!hasExistingNewsTags(connection)) {
                insertInitialNewsTags(connection);
                logger.info("新闻标签初始数据插入完成");
            } else {
                logger.info("新闻标签数据已存在，跳过初始化");
            }
            
            // 注意：根据项目需求，不再插入示例新闻数据
            // 新闻内容应该由管理员通过管理后台手动创建，确保使用真实数据而非模拟数据
            logger.info("新闻数据初始化完成 - 仅创建必要的分类和标签，不插入示例新闻");
        }
    }
    
    /**
     * 初始化租赁基础数据
     * 注意：只初始化必要的基础数据，不插入任何示例租赁记录
     */
    private void initializeRentalBaseData() throws SQLException {
        logger.info("初始化租赁基础数据...");
        
        try (Connection connection = dataSource.getConnection()) {
            // 初始化设备型号基础数据
            if (!hasExistingDeviceModels(connection)) {
                insertBasicDeviceModels(connection);
                logger.info("设备型号基础数据插入完成");
            } else {
                logger.info("设备型号数据已存在，跳过初始化");
            }
            
            // 初始化客户类型基础数据
            if (!hasExistingCustomerTypes(connection)) {
                insertBasicCustomerTypes(connection);
                logger.info("客户类型基础数据插入完成");
            } else {
                logger.info("客户类型数据已存在，跳过初始化");
            }
            
            // 初始化地区信息基础数据
            if (!hasExistingRentalRegions(connection)) {
                insertBasicRentalRegions(connection);
                logger.info("租赁地区基础数据插入完成");
            } else {
                logger.info("租赁地区数据已存在，跳过初始化");
            }
            
            // 严禁插入示例租赁记录
            // 确保rental_records表保持空状态，让用户手动录入真实数据
            logger.info("租赁基础数据初始化完成 - 未插入任何示例租赁记录，保持真实数据原则");
        }
    }
    
    /**
     * 检查是否已有设备型号数据
     */
    private boolean hasExistingDeviceModels(Connection connection) throws SQLException {
        String sql = "SELECT COUNT(*) FROM rental_devices WHERE is_deleted = 0";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() && rs.getInt(1) > 0;
        }
    }
    
    /**
     * 检查是否已有客户类型数据
     */
    private boolean hasExistingCustomerTypes(Connection connection) throws SQLException {
        String sql = "SELECT COUNT(*) FROM rental_customers WHERE is_deleted = 0";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() && rs.getInt(1) > 0;
        }
    }
    
    /**
     * 检查是否已有租赁地区数据
     */
    private boolean hasExistingRentalRegions(Connection connection) throws SQLException {
        // 使用现有的region_configs表
        String sql = "SELECT COUNT(*) FROM region_configs WHERE is_active = 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() && rs.getInt(1) > 0;
        }
    }
    
    /**
     * 插入基础设备型号数据
     */
    private void insertBasicDeviceModels(Connection connection) throws SQLException {
        String sql = "INSERT INTO rental_devices (" +
            "device_id, device_model, device_name, device_category, " +
            "daily_rental_price, current_status, region, " +
            "performance_score, signal_strength, maintenance_status, " +
            "total_rental_days, total_available_days, utilization_rate, " +
            "is_active, created_at, updated_at, is_deleted" +
            ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW(), 0)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // YX-Robot-Pro 系列
            addDeviceModel(stmt, "YX-PRO-001", "YX-Robot-Pro", "练字机器人Pro版", "智能练字设备", 
                          150.00, "idle", "中国大陆", 100, 100, "normal", 0, 0, 0.00, true);
            addDeviceModel(stmt, "YX-PRO-002", "YX-Robot-Pro", "练字机器人Pro版", "智能练字设备", 
                          150.00, "idle", "中国大陆", 100, 100, "normal", 0, 0, 0.00, true);
            addDeviceModel(stmt, "YX-PRO-003", "YX-Robot-Pro", "练字机器人Pro版", "智能练字设备", 
                          150.00, "idle", "美国", 100, 100, "normal", 0, 0, 0.00, true);
            
            // YX-Robot-Standard 系列
            addDeviceModel(stmt, "YX-STD-001", "YX-Robot-Standard", "练字机器人标准版", "智能练字设备", 
                          100.00, "idle", "中国大陆", 95, 95, "normal", 0, 0, 0.00, true);
            addDeviceModel(stmt, "YX-STD-002", "YX-Robot-Standard", "练字机器人标准版", "智能练字设备", 
                          100.00, "idle", "中国大陆", 95, 95, "normal", 0, 0, 0.00, true);
            addDeviceModel(stmt, "YX-STD-003", "YX-Robot-Standard", "练字机器人标准版", "智能练字设备", 
                          100.00, "idle", "日本", 95, 95, "normal", 0, 0, 0.00, true);
            
            // YX-Robot-Lite 系列
            addDeviceModel(stmt, "YX-LITE-001", "YX-Robot-Lite", "练字机器人轻量版", "智能练字设备", 
                          80.00, "idle", "中国大陆", 90, 90, "normal", 0, 0, 0.00, true);
            addDeviceModel(stmt, "YX-LITE-002", "YX-Robot-Lite", "练字机器人轻量版", "智能练字设备", 
                          80.00, "idle", "中国大陆", 90, 90, "normal", 0, 0, 0.00, true);
            
            // YX-Robot-Mini 系列
            addDeviceModel(stmt, "YX-MINI-001", "YX-Robot-Mini", "练字机器人迷你版", "智能练字设备", 
                          60.00, "idle", "中国大陆", 85, 85, "normal", 0, 0, 0.00, true);
            addDeviceModel(stmt, "YX-MINI-002", "YX-Robot-Mini", "练字机器人迷你版", "智能练字设备", 
                          60.00, "idle", "中国大陆", 85, 85, "normal", 0, 0, 0.00, true);
            
            stmt.executeBatch();
            logger.info("插入了 {} 条设备型号基础数据", 10);
        }
    }
    
    /**
     * 添加设备型号数据到批处理
     */
    private void addDeviceModel(PreparedStatement stmt, String deviceId, String deviceModel, 
                               String deviceName, String deviceCategory, double dailyRentalPrice, 
                               String currentStatus, String region, int performanceScore, 
                               int signalStrength, String maintenanceStatus, int totalRentalDays, 
                               int totalAvailableDays, double utilizationRate, boolean isActive) throws SQLException {
        stmt.setString(1, deviceId);
        stmt.setString(2, deviceModel);
        stmt.setString(3, deviceName);
        stmt.setString(4, deviceCategory);
        stmt.setDouble(5, dailyRentalPrice);
        stmt.setString(6, currentStatus);
        stmt.setString(7, region);
        stmt.setInt(8, performanceScore);
        stmt.setInt(9, signalStrength);
        stmt.setString(10, maintenanceStatus);
        stmt.setInt(11, totalRentalDays);
        stmt.setInt(12, totalAvailableDays);
        stmt.setDouble(13, utilizationRate);
        stmt.setBoolean(14, isActive);
        stmt.addBatch();
    }
    
    /**
     * 插入基础客户类型数据
     */
    private void insertBasicCustomerTypes(Connection connection) throws SQLException {
        String sql = "INSERT INTO rental_customers (" +
            "customer_name, customer_type, contact_person, phone, email, " +
            "address, region, industry, credit_level, " +
            "total_rental_amount, total_rental_days, is_active, " +
            "created_at, updated_at, is_deleted" +
            ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW(), 0)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // 个人客户示例类型（用于分类，不是真实客户）
            addCustomerType(stmt, "个人用户类型", "individual", "个人用户", "000-0000-0000", 
                           "individual@example.com", "个人地址", "中国大陆", "教育", "B", 
                           0.00, 0, true);
            
            // 企业客户示例类型
            addCustomerType(stmt, "企业用户类型", "enterprise", "企业联系人", "000-0000-0001", 
                           "enterprise@example.com", "企业地址", "中国大陆", "教育科技", "A", 
                           0.00, 0, true);
            
            // 机构客户示例类型
            addCustomerType(stmt, "机构用户类型", "institution", "机构联系人", "000-0000-0002", 
                           "institution@example.com", "机构地址", "中国大陆", "教育机构", "A", 
                           0.00, 0, true);
            
            stmt.executeBatch();
            logger.info("插入了 {} 条客户类型基础数据", 3);
        }
    }
    
    /**
     * 添加客户类型数据到批处理
     */
    private void addCustomerType(PreparedStatement stmt, String customerName, String customerType, 
                                String contactPerson, String phone, String email, String address, 
                                String region, String industry, String creditLevel, 
                                double totalRentalAmount, int totalRentalDays, boolean isActive) throws SQLException {
        stmt.setString(1, customerName);
        stmt.setString(2, customerType);
        stmt.setString(3, contactPerson);
        stmt.setString(4, phone);
        stmt.setString(5, email);
        stmt.setString(6, address);
        stmt.setString(7, region);
        stmt.setString(8, industry);
        stmt.setString(9, creditLevel);
        stmt.setDouble(10, totalRentalAmount);
        stmt.setInt(11, totalRentalDays);
        stmt.setBoolean(12, isActive);
        stmt.addBatch();
    }
    
    /**
     * 插入基础租赁地区数据
     */
    private void insertBasicRentalRegions(Connection connection) throws SQLException {
        // 租赁地区数据使用现有的region_configs表，这里只需要确保有基础数据
        logger.info("租赁地区数据使用现有的region_configs表");
    }
    
    /**
     * 插入示例租赁记录
     * 注意：此方法已被禁用 - 不插入任何示例租赁记录
     * 根据项目需求，严禁使用模拟数据，所有租赁数据必须通过管理后台手动录入
     */
    private void insertSampleRentalRecords(Connection connection) throws SQLException {
        // 此方法已被禁用 - 不插入任何示例租赁记录
        logger.warn("示例租赁记录插入已被禁用 - 遵循真实数据原则");
        return; // 直接返回，不执行任何插入操作
    }
    
    /**
     * 初始化客户管理基础数据
     * 注意：只初始化必要的基础数据，严禁插入任何示例客户数据
     */
    private void initializeCustomerBaseData() throws SQLException {
        logger.info("初始化客户管理基础数据...");
        
        try (Connection connection = dataSource.getConnection()) {
            // 初始化客户等级基础数据
            if (!hasExistingCustomerLevels(connection)) {
                insertBasicCustomerLevels(connection);
                logger.info("客户等级基础数据插入完成");
            } else {
                logger.info("客户等级数据已存在，跳过初始化");
            }
            
            // 初始化客户标签基础数据
            if (!hasExistingCustomerTags(connection)) {
                insertBasicCustomerTags(connection);
                logger.info("客户标签基础数据插入完成");
            } else {
                logger.info("客户标签数据已存在，跳过初始化");
            }
            
            // 初始化地区信息基础数据（复用现有的region_configs）
            logger.info("客户地区信息使用现有的region_configs表");
            
            // 严禁插入示例客户数据
            // 确保customers表保持空状态，让用户手动录入真实数据
            validateCustomersTableEmpty(connection);
            
            logger.info("客户管理基础数据初始化完成 - 未插入任何示例客户数据，保持真实数据原则");
        }
    }
    
    /**
     * 检查是否已有客户等级数据
     */
    private boolean hasExistingCustomerLevels(Connection connection) throws SQLException {
        // 检查是否有客户等级配置表，如果没有则创建
        String checkTableSql = "SHOW TABLES LIKE 'customer_level_configs'";
        try (PreparedStatement stmt = connection.prepareStatement(checkTableSql);
             ResultSet rs = stmt.executeQuery()) {
            if (!rs.next()) {
                // 表不存在，创建表
                createCustomerLevelConfigsTable(connection);
                return false;
            }
        }
        
        String sql = "SELECT COUNT(*) FROM customer_level_configs WHERE is_active = 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() && rs.getInt(1) > 0;
        }
    }
    
    /**
     * 检查是否已有客户标签数据
     */
    private boolean hasExistingCustomerTags(Connection connection) throws SQLException {
        // 检查是否有客户标签配置表，如果没有则创建
        String checkTableSql = "SHOW TABLES LIKE 'customer_tag_configs'";
        try (PreparedStatement stmt = connection.prepareStatement(checkTableSql);
             ResultSet rs = stmt.executeQuery()) {
            if (!rs.next()) {
                // 表不存在，创建表
                createCustomerTagConfigsTable(connection);
                return false;
            }
        }
        
        String sql = "SELECT COUNT(*) FROM customer_tag_configs WHERE is_active = 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() && rs.getInt(1) > 0;
        }
    }
    
    /**
     * 创建客户等级配置表
     */
    private void createCustomerLevelConfigsTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE customer_level_configs (" +
            "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
            "level_code VARCHAR(20) NOT NULL UNIQUE COMMENT '等级代码', " +
            "level_name VARCHAR(50) NOT NULL COMMENT '等级名称', " +
            "level_description TEXT COMMENT '等级描述', " +
            "min_spent DECIMAL(10,2) DEFAULT 0.00 COMMENT '最低消费金额', " +
            "max_spent DECIMAL(10,2) COMMENT '最高消费金额', " +
            "discount_rate DECIMAL(5,2) DEFAULT 0.00 COMMENT '折扣率', " +
            "sort_order INT DEFAULT 0 COMMENT '排序', " +
            "is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用', " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间', " +
            "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间', " +
            "INDEX idx_level_code (level_code), " +
            "INDEX idx_sort_order (sort_order)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户等级配置表'";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.executeUpdate();
            logger.info("客户等级配置表创建成功");
        }
    }
    
    /**
     * 创建客户标签配置表
     */
    private void createCustomerTagConfigsTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE customer_tag_configs (" +
            "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
            "tag_code VARCHAR(20) NOT NULL UNIQUE COMMENT '标签代码', " +
            "tag_name VARCHAR(50) NOT NULL COMMENT '标签名称', " +
            "tag_description TEXT COMMENT '标签描述', " +
            "tag_color VARCHAR(7) DEFAULT '#409EFF' COMMENT '标签颜色', " +
            "category VARCHAR(20) DEFAULT 'general' COMMENT '标签分类', " +
            "sort_order INT DEFAULT 0 COMMENT '排序', " +
            "is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用', " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间', " +
            "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间', " +
            "INDEX idx_tag_code (tag_code), " +
            "INDEX idx_category (category), " +
            "INDEX idx_sort_order (sort_order)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户标签配置表'";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.executeUpdate();
            logger.info("客户标签配置表创建成功");
        }
    }
    
    /**
     * 插入基础客户等级数据
     */
    private void insertBasicCustomerLevels(Connection connection) throws SQLException {
        String sql = "INSERT INTO customer_level_configs (" +
            "level_code, level_name, level_description, min_spent, max_spent, " +
            "discount_rate, sort_order, is_active, created_at, updated_at" +
            ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // 普通客户
            addCustomerLevel(stmt, "REGULAR", "普通客户", "新注册客户或消费金额较低的客户", 
                           0.00, 999.99, 0.00, 1, true);
            
            // VIP客户
            addCustomerLevel(stmt, "VIP", "VIP客户", "消费金额达到一定标准的优质客户", 
                           1000.00, 4999.99, 5.00, 2, true);
            
            // 高级客户
            addCustomerLevel(stmt, "PREMIUM", "高级客户", "消费金额很高的顶级客户", 
                           5000.00, null, 10.00, 3, true);
            
            stmt.executeBatch();
            logger.info("插入了 {} 条客户等级基础数据", 3);
        }
    }
    
    /**
     * 添加客户等级数据到批处理
     */
    private void addCustomerLevel(PreparedStatement stmt, String levelCode, String levelName, 
                                 String levelDescription, double minSpent, Double maxSpent, 
                                 double discountRate, int sortOrder, boolean isActive) throws SQLException {
        stmt.setString(1, levelCode);
        stmt.setString(2, levelName);
        stmt.setString(3, levelDescription);
        stmt.setDouble(4, minSpent);
        if (maxSpent != null) {
            stmt.setDouble(5, maxSpent);
        } else {
            stmt.setNull(5, java.sql.Types.DECIMAL);
        }
        stmt.setDouble(6, discountRate);
        stmt.setInt(7, sortOrder);
        stmt.setBoolean(8, isActive);
        stmt.addBatch();
    }
    
    /**
     * 插入基础客户标签数据
     */
    private void insertBasicCustomerTags(Connection connection) throws SQLException {
        String sql = "INSERT INTO customer_tag_configs (" +
            "tag_code, tag_name, tag_description, tag_color, category, " +
            "sort_order, is_active, created_at, updated_at" +
            ") VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // 客户类型标签
            addCustomerTag(stmt, "NEW_CUSTOMER", "新客户", "新注册的客户", "#67C23A", "type", 1, true);
            addCustomerTag(stmt, "LOYAL_CUSTOMER", "忠实客户", "长期合作的客户", "#409EFF", "type", 2, true);
            addCustomerTag(stmt, "HIGH_VALUE", "高价值客户", "消费金额较高的客户", "#E6A23C", "value", 3, true);
            
            // 行业标签
            addCustomerTag(stmt, "EDUCATION", "教育行业", "教育机构或学校客户", "#F56C6C", "industry", 4, true);
            addCustomerTag(stmt, "ENTERPRISE", "企业客户", "企业或公司客户", "#909399", "industry", 5, true);
            addCustomerTag(stmt, "INDIVIDUAL", "个人客户", "个人用户", "#C71585", "industry", 6, true);
            
            // 地区标签
            addCustomerTag(stmt, "DOMESTIC", "国内客户", "中国大陆地区客户", "#FF6347", "region", 7, true);
            addCustomerTag(stmt, "OVERSEAS", "海外客户", "海外地区客户", "#32CD32", "region", 8, true);
            
            // 服务标签
            addCustomerTag(stmt, "RENTAL_ONLY", "仅租赁", "只使用租赁服务的客户", "#1E90FF", "service", 9, true);
            addCustomerTag(stmt, "PURCHASE_ONLY", "仅购买", "只购买设备的客户", "#FF69B4", "service", 10, true);
            addCustomerTag(stmt, "MIXED_SERVICE", "混合服务", "同时使用租赁和购买服务的客户", "#8A2BE2", "service", 11, true);
            
            // 特殊标签
            addCustomerTag(stmt, "VIP_SERVICE", "VIP服务", "享受VIP服务的客户", "#00CED1", "special", 12, true);
            
            stmt.executeBatch();
            logger.info("插入了 {} 条客户标签基础数据", 12);
        }
    }
    
    /**
     * 添加客户标签数据到批处理
     */
    private void addCustomerTag(PreparedStatement stmt, String tagCode, String tagName, 
                               String tagDescription, String tagColor, String category, 
                               int sortOrder, boolean isActive) throws SQLException {
        stmt.setString(1, tagCode);
        stmt.setString(2, tagName);
        stmt.setString(3, tagDescription);
        stmt.setString(4, tagColor);
        stmt.setString(5, category);
        stmt.setInt(6, sortOrder);
        stmt.setBoolean(7, isActive);
        stmt.addBatch();
    }
    
    /**
     * 验证客户表为空状态
     * 确保没有示例客户数据
     */
    private void validateCustomersTableEmpty(Connection connection) throws SQLException {
        String sql = "SELECT COUNT(*) FROM customers WHERE is_deleted = 0 OR is_deleted IS NULL";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int count = rs.getInt(1);
                if (count == 0) {
                    logger.info("客户表验证通过 - 表为空，符合真实数据要求");
                } else {
                    logger.warn("检测到客户表中有 {} 条数据，请确认是否为真实数据而非示例数据", count);
                }
            }
        } catch (SQLException e) {
            // 如果表不存在，这是正常的
            logger.info("客户表不存在或查询失败，这是正常的初始状态");
        }
    }
    
    /**
     * 验证数据完整性
     */
    private void validateDataIntegrity() throws SQLException {
        logger.info("验证数据完整性...");
        
        try (Connection connection = dataSource.getConnection()) {
            // 验证平台链接数据
            validatePlatformLinksData(connection);
            
            // 验证区域配置数据
            validateRegionConfigData(connection);
            
            // 验证新闻管理数据
            validateNewsData(connection);
            
            // 验证租赁基础数据
            validateRentalBaseData(connection);
            
            // 验证客户管理基础数据
            validateCustomerBaseData(connection);
            
            logger.info("数据完整性验证通过");
        }
    }
    
    /**
     * 检查是否已有平台链接数据
     */
    private boolean hasExistingPlatformLinks(Connection connection) throws SQLException {
        String sql = "SELECT COUNT(*) FROM platform_links WHERE is_deleted = 0";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() && rs.getInt(1) > 0;
        }
    }
    
    /**
     * 检查是否已有区域配置数据
     */
    private boolean hasExistingRegionConfig(Connection connection) throws SQLException {
        String sql = "SELECT COUNT(*) FROM region_configs";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() && rs.getInt(1) > 0;
        }
    }
    
    /**
     * 插入初始平台链接数据
     */
    private void insertInitialPlatformLinks(Connection connection) throws SQLException {
        String sql = "INSERT INTO platform_links (" +
            "platform_name, platform_type, link_url, region, country, " +
            "language_code, language_name, is_enabled, link_status, " +
            "click_count, conversion_count, created_at, updated_at, is_deleted" +
            ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW(), 0)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // 中国大陆 - 电商平台
            addPlatformLink(stmt, "淘宝", "ecommerce", "https://www.taobao.com", "中国大陆", "中国", "zh-CN", "简体中文", true, "active", 1250, 89);
            addPlatformLink(stmt, "京东", "ecommerce", "https://www.jd.com", "中国大陆", "中国", "zh-CN", "简体中文", true, "active", 980, 67);
            addPlatformLink(stmt, "天猫", "ecommerce", "https://www.tmall.com", "中国大陆", "中国", "zh-CN", "简体中文", true, "active", 1100, 78);
            
            // 美国 - 电商平台
            addPlatformLink(stmt, "Amazon US", "ecommerce", "https://www.amazon.com", "美国", "美国", "en-US", "英语", true, "active", 2100, 145);
            addPlatformLink(stmt, "eBay", "ecommerce", "https://www.ebay.com", "美国", "美国", "en-US", "英语", true, "active", 890, 62);
            
            // 日本 - 电商平台
            addPlatformLink(stmt, "Amazon Japan", "ecommerce", "https://www.amazon.co.jp", "日本", "日本", "ja-JP", "日语", true, "active", 750, 52);
            addPlatformLink(stmt, "楽天市場", "ecommerce", "https://www.rakuten.co.jp", "日本", "日本", "ja-JP", "日语", true, "active", 650, 45);
            
            // 租赁平台
            addPlatformLink(stmt, "租赁宝", "rental", "https://rental.example.com", "中国大陆", "中国", "zh-CN", "简体中文", true, "active", 320, 28);
            addPlatformLink(stmt, "设备租赁网", "rental", "https://device-rental.example.com", "中国大陆", "中国", "zh-CN", "简体中文", true, "active", 280, 22);
            
            stmt.executeBatch();
            logger.info("插入了 {} 条平台链接初始数据", 9);
        }
    }
    
    /**
     * 添加平台链接数据到批处理
     */
    private void addPlatformLink(PreparedStatement stmt, String platformName, String platformType, 
                                String linkUrl, String region, String country, String languageCode, 
                                String languageName, boolean isEnabled, String linkStatus, 
                                int clickCount, int conversionCount) throws SQLException {
        stmt.setString(1, platformName);
        stmt.setString(2, platformType);
        stmt.setString(3, linkUrl);
        stmt.setString(4, region);
        stmt.setString(5, country);
        stmt.setString(6, languageCode);
        stmt.setString(7, languageName);
        stmt.setBoolean(8, isEnabled);
        stmt.setString(9, linkStatus);
        stmt.setInt(10, clickCount);
        stmt.setInt(11, conversionCount);
        stmt.addBatch();
    }
    
    /**
     * 插入初始区域配置数据
     */
    private void insertInitialRegionConfig(Connection connection) throws SQLException {
        String sql = "INSERT INTO region_configs (" +
            "region, country, language_code, language_name, " +
            "is_enabled, created_at, updated_at" +
            ") VALUES (?, ?, ?, ?, ?, NOW(), NOW())";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // 中国大陆
            addRegionConfig(stmt, "中国大陆", "中国", "zh-CN", "简体中文", true);
            
            // 香港
            addRegionConfig(stmt, "香港", "中国", "zh-HK", "繁体中文", true);
            
            // 台湾
            addRegionConfig(stmt, "台湾", "中国", "zh-TW", "繁体中文", true);
            
            // 美国
            addRegionConfig(stmt, "美国", "美国", "en-US", "英语", true);
            
            // 日本
            addRegionConfig(stmt, "日本", "日本", "ja-JP", "日语", true);
            
            // 韩国
            addRegionConfig(stmt, "韩国", "韩国", "ko-KR", "韩语", true);
            
            // 德国
            addRegionConfig(stmt, "德国", "德国", "de-DE", "德语", true);
            
            // 法国
            addRegionConfig(stmt, "法国", "法国", "fr-FR", "法语", true);
            
            stmt.executeBatch();
            logger.info("插入了 {} 条区域配置初始数据", 8);
        }
    }
    
    /**
     * 添加区域配置数据到批处理
     */
    private void addRegionConfig(PreparedStatement stmt, String region, String country, 
                                String languageCode, String languageName, boolean isEnabled) throws SQLException {
        stmt.setString(1, region);
        stmt.setString(2, country);
        stmt.setString(3, languageCode);
        stmt.setString(4, languageName);
        stmt.setBoolean(5, isEnabled);
        stmt.addBatch();
    }
    
    /**
     * 验证平台链接数据
     */
    private void validatePlatformLinksData(Connection connection) throws SQLException {
        String sql = "SELECT COUNT(*) FROM platform_links WHERE is_deleted = 0";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int count = rs.getInt(1);
                logger.info("平台链接数据验证通过，共 {} 条记录", count);
                if (count == 0) {
                    throw new SQLException("平台链接数据为空");
                }
            }
        }
    }
    
    /**
     * 验证区域配置数据
     */
    private void validateRegionConfigData(Connection connection) throws SQLException {
        String sql = "SELECT COUNT(*) FROM region_configs WHERE is_active = 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int count = rs.getInt(1);
                logger.info("区域配置数据验证通过，共 {} 条记录", count);
                if (count == 0) {
                    throw new SQLException("区域配置数据为空");
                }
            }
        }
    }
    
    /**
     * 检查是否已有新闻分类数据
     */
    private boolean hasExistingNewsCategories(Connection connection) throws SQLException {
        String sql = "SELECT COUNT(*) FROM news_categories";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() && rs.getInt(1) > 0;
        }
    }
    
    /**
     * 检查是否已有新闻标签数据
     */
    private boolean hasExistingNewsTags(Connection connection) throws SQLException {
        String sql = "SELECT COUNT(*) FROM news_tags";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() && rs.getInt(1) > 0;
        }
    }
    
    /**
     * 检查是否已有新闻数据
     */
    private boolean hasExistingNews(Connection connection) throws SQLException {
        String sql = "SELECT COUNT(*) FROM news";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() && rs.getInt(1) > 0;
        }
    }
    
    /**
     * 插入初始新闻分类数据
     */
    private void insertInitialNewsCategories(Connection connection) throws SQLException {
        String sql = "INSERT INTO news_categories (" +
            "name, description, sort_order, is_enabled, created_at, updated_at" +
            ") VALUES (?, ?, ?, ?, NOW(), NOW())";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            addNewsCategory(stmt, "产品动态", "练字机器人产品相关的最新动态和更新", 1, true);
            addNewsCategory(stmt, "公司新闻", "公司发展、合作、活动等相关新闻", 2, true);
            addNewsCategory(stmt, "行业资讯", "教育科技行业的最新资讯和趋势", 3, true);
            addNewsCategory(stmt, "技术分享", "技术开发、创新应用等技术相关内容", 4, true);
            addNewsCategory(stmt, "用户故事", "用户使用体验、成功案例等故事分享", 5, true);
            addNewsCategory(stmt, "公益活动", "公益项目、社会责任等相关活动", 6, true);
            
            stmt.executeBatch();
            logger.info("插入了 {} 条新闻分类初始数据", 6);
        }
    }
    
    /**
     * 添加新闻分类数据到批处理
     */
    private void addNewsCategory(PreparedStatement stmt, String name, String description, 
                                int sortOrder, boolean isEnabled) throws SQLException {
        stmt.setString(1, name);
        stmt.setString(2, description);
        stmt.setInt(3, sortOrder);
        stmt.setBoolean(4, isEnabled);
        stmt.addBatch();
    }
    
    /**
     * 插入初始新闻标签数据
     */
    private void insertInitialNewsTags(Connection connection) throws SQLException {
        String sql = "INSERT INTO news_tags (" +
            "name, color, created_at, updated_at" +
            ") VALUES (?, ?, NOW(), NOW())";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            addNewsTag(stmt, "产品发布", "#409EFF");
            addNewsTag(stmt, "功能更新", "#67C23A");
            addNewsTag(stmt, "合作伙伴", "#E6A23C");
            addNewsTag(stmt, "教育科技", "#F56C6C");
            addNewsTag(stmt, "AI技术", "#909399");
            addNewsTag(stmt, "用户体验", "#C71585");
            addNewsTag(stmt, "社会责任", "#FF6347");
            addNewsTag(stmt, "行业趋势", "#32CD32");
            addNewsTag(stmt, "创新应用", "#1E90FF");
            addNewsTag(stmt, "成功案例", "#FF69B4");
            addNewsTag(stmt, "技术突破", "#8A2BE2");
            addNewsTag(stmt, "市场拓展", "#00CED1");
            
            stmt.executeBatch();
            logger.info("插入了 {} 条新闻标签初始数据", 12);
        }
    }
    
    /**
     * 添加新闻标签数据到批处理
     */
    private void addNewsTag(PreparedStatement stmt, String name, String color) throws SQLException {
        stmt.setString(1, name);
        stmt.setString(2, color);
        stmt.addBatch();
    }
    
    /**
     * 插入示例新闻数据
     * 注意：根据项目需求，此方法已被禁用，不再插入任何示例新闻数据
     * 所有新闻内容应该通过管理后台手动创建，确保使用真实数据
     */
    private void insertSampleNews(Connection connection) throws SQLException {
        // 此方法已被禁用 - 不插入任何示例新闻数据
        // 根据项目需求文档，禁止使用模拟数据，所有新闻内容应该是真实的
        logger.info("跳过示例新闻数据插入 - 根据项目需求，不使用模拟数据");
    }
    
    /**
     * 验证新闻管理数据
     */
    private void validateNewsData(Connection connection) throws SQLException {
        // 验证新闻分类数据
        String categorySql = "SELECT COUNT(*) FROM news_categories WHERE is_enabled = 1";
        try (PreparedStatement stmt = connection.prepareStatement(categorySql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int count = rs.getInt(1);
                logger.info("新闻分类数据验证通过，共 {} 条记录", count);
            }
        }
        
        // 验证新闻标签数据
        String tagSql = "SELECT COUNT(*) FROM news_tags";
        try (PreparedStatement stmt = connection.prepareStatement(tagSql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int count = rs.getInt(1);
                logger.info("新闻标签数据验证通过，共 {} 条记录", count);
            }
        }
        
        // 验证新闻数据
        String newsSql = "SELECT COUNT(*) FROM news";
        try (PreparedStatement stmt = connection.prepareStatement(newsSql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int count = rs.getInt(1);
                logger.info("新闻数据验证通过，共 {} 条记录", count);
            }
        }
    }
    
    /**
     * 验证租赁基础数据
     */
    private void validateRentalBaseData(Connection connection) throws SQLException {
        // 验证设备型号数据
        String deviceSql = "SELECT COUNT(*) FROM rental_devices WHERE is_deleted = 0";
        try (PreparedStatement stmt = connection.prepareStatement(deviceSql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int count = rs.getInt(1);
                logger.info("租赁设备数据验证通过，共 {} 条记录", count);
            }
        }
        
        // 验证客户类型数据
        String customerSql = "SELECT COUNT(*) FROM rental_customers WHERE is_deleted = 0";
        try (PreparedStatement stmt = connection.prepareStatement(customerSql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int count = rs.getInt(1);
                logger.info("租赁客户数据验证通过，共 {} 条记录", count);
            }
        }
        
        // 验证租赁记录表为空（确保没有示例数据）
        String rentalRecordSql = "SELECT COUNT(*) FROM rental_records WHERE is_deleted = 0";
        try (PreparedStatement stmt = connection.prepareStatement(rentalRecordSql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int count = rs.getInt(1);
                logger.info("租赁记录表验证通过，共 {} 条记录（应为0，确保无示例数据）", count);
                if (count > 0) {
                    logger.warn("检测到租赁记录表中有数据，请确认是否为真实数据而非示例数据");
                }
            }
        }
    }
    
    /**
     * 验证客户管理基础数据
     */
    private void validateCustomerBaseData(Connection connection) throws SQLException {
        // 验证客户等级配置数据
        try {
            String levelSql = "SELECT COUNT(*) FROM customer_level_configs WHERE is_active = 1";
            try (PreparedStatement stmt = connection.prepareStatement(levelSql);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    logger.info("客户等级配置数据验证通过，共 {} 条记录", count);
                }
            }
        } catch (SQLException e) {
            logger.warn("客户等级配置表不存在或查询失败: {}", e.getMessage());
        }
        
        // 验证客户标签配置数据
        try {
            String tagSql = "SELECT COUNT(*) FROM customer_tag_configs WHERE is_active = 1";
            try (PreparedStatement stmt = connection.prepareStatement(tagSql);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    logger.info("客户标签配置数据验证通过，共 {} 条记录", count);
                }
            }
        } catch (SQLException e) {
            logger.warn("客户标签配置表不存在或查询失败: {}", e.getMessage());
        }
        
        // 验证客户表为空（确保没有示例数据）
        try {
            String customerSql = "SELECT COUNT(*) FROM customers WHERE is_deleted = 0 OR is_deleted IS NULL";
            try (PreparedStatement stmt = connection.prepareStatement(customerSql);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    if (count == 0) {
                        logger.info("客户表验证通过 - 表为空，符合真实数据要求");
                    } else {
                        logger.warn("检测到客户表中有 {} 条数据，请确认是否为真实数据而非示例数据", count);
                    }
                }
            }
        } catch (SQLException e) {
            logger.info("客户表不存在或查询失败，这是正常的初始状态: {}", e.getMessage());
        }
    }
}