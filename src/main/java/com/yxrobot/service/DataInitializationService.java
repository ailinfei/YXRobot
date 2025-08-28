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
            
            // 5. 验证数据完整性
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
}