package com.yxrobot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 数据库连接测试
 * 
 * @author YXRobot开发团队s
 * @version 1.0
 * @since 2024-12-19
 */
public class DatabaseConnectionTest {
    
    private static final String DB_URL = "jdbc:mysql://yun.finiot.cn:3306/YXRobot?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=utf8";
    private static final String DB_USER = "YXRobot";
    private static final String DB_PASSWORD = "2200548qq";
    
    @Test
    public void testDatabaseConnection() {
        System.out.println("=====================================================");
        System.out.println("YXRobot 数据库连接测试");
        System.out.println("服务器: yun.finiot.cn:3306");
        System.out.println("数据库: YXRobot");
        System.out.println("用户名: YXRobot");
        System.out.println("=====================================================");
        
        try {
            // 加载MySQL驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ MySQL驱动加载成功");
            
            // 建立数据库连接
            System.out.println("正在连接数据库...");
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("✅ 数据库连接成功！");
            
            // 测试查询
            Statement statement = connection.createStatement();
            
            // 检查数据库版本
            ResultSet versionResult = statement.executeQuery("SELECT VERSION() as version");
            if (versionResult.next()) {
                System.out.println("数据库版本: " + versionResult.getString("version"));
            }
            
            // 检查当前时间
            ResultSet timeResult = statement.executeQuery("SELECT NOW() as server_time");
            if (timeResult.next()) {
                System.out.println("服务器时间: " + timeResult.getString("server_time"));
            }
            
            // 检查表是否存在
            ResultSet tablesResult = statement.executeQuery("SHOW TABLES");
            System.out.println("数据库中的表:");
            boolean hasProducts = false;
            while (tablesResult.next()) {
                String tableName = tablesResult.getString(1);
                System.out.println("  - " + tableName);
                if ("products".equals(tableName)) {
                    hasProducts = true;
                }
            }
            
            // 如果products表存在，检查数据
            if (hasProducts) {
                ResultSet countResult = statement.executeQuery("SELECT COUNT(*) as count FROM products");
                if (countResult.next()) {
                    System.out.println("产品表记录数: " + countResult.getInt("count"));
                }
            } else {
                System.out.println("⚠️  products表不存在，正在创建表...");
                
                // 创建products表
                String createProductsTable = "CREATE TABLE `products` (" +
                    "`id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '产品ID，主键'," +
                    "`name` VARCHAR(200) NOT NULL COMMENT '产品名称'," +
                    "`model` VARCHAR(100) NOT NULL COMMENT '产品型号'," +
                    "`description` TEXT COMMENT '产品描述'," +
                    "`price` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '销售价格，单位：元'," +
                    "`cover_image_url` VARCHAR(500) DEFAULT NULL COMMENT '封面图片URL'," +
                    "`status` VARCHAR(20) NOT NULL DEFAULT 'draft' COMMENT '产品状态：draft-草稿，published-已发布，archived-已归档'," +
                    "`created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'," +
                    "`updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'," +
                    "`is_deleted` TINYINT DEFAULT 0 COMMENT '是否已删除：0-否，1-是'," +
                    "PRIMARY KEY (`id`)," +
                    "UNIQUE KEY `uk_model` (`model`)," +
                    "KEY `idx_name` (`name`)," +
                    "KEY `idx_status` (`status`)," +
                    "KEY `idx_price` (`price`)," +
                    "KEY `idx_created_at` (`created_at`)," +
                    "KEY `idx_is_deleted` (`is_deleted`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产品表'";
                
                statement.execute(createProductsTable);
                System.out.println("✅ products表创建成功");
                
                // 插入测试数据
                String insertTestData = "INSERT INTO `products` (`id`, `name`, `model`, `description`, `price`, `status`, `cover_image_url`, `created_at`, `updated_at`) VALUES " +
                    "(1, '家用练字机器人', 'YX-HOME-001', '适合家庭使用的智能练字机器人，支持多种字体和练习模式', 2999.00, 'published', 'https://via.placeholder.com/150x150', '2024-01-15 10:30:00', '2024-01-20 14:20:00'), " +
                    "(2, '商用练字机器人', 'YX-BUSINESS-001', '适合商业场所使用的高性能练字机器人', 8999.00, 'published', 'https://via.placeholder.com/150x150', '2024-01-10 09:15:00', '2024-01-18 16:45:00'), " +
                    "(3, '教育版练字机器人', 'YX-EDU-001', '专为教育机构设计的练字机器人', 5999.00, 'draft', '', '2024-01-12 11:20:00', '2024-01-19 13:30:00')";
                
                statement.execute(insertTestData);
                System.out.println("✅ 测试数据插入成功");
                
                // 验证数据
                ResultSet countResult = statement.executeQuery("SELECT COUNT(*) as count FROM products");
                if (countResult.next()) {
                    System.out.println("产品表记录数: " + countResult.getInt("count"));
                }
            }
            
            // 关闭连接
            connection.close();
            System.out.println("✅ 数据库连接测试完成");
            
        } catch (Exception e) {
            System.err.println("❌ 数据库连接失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}