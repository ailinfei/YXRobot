package com.yxrobot.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * 数据库连接池优化配置
 * 
 * 针对公益项目管理页面的查询特点，优化数据库连接池配置
 * 
 * @author YXRobot
 * @since 1.0.0
 */
@Configuration
public class DatabaseConfig {

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    /**
     * 配置优化的数据源
     * 
     * @return 优化后的数据源
     */
    @Bean
    @Primary
    public DataSource optimizedDataSource() {
        HikariConfig config = new HikariConfig();
        
        // 基本连接配置
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driverClassName);
        
        // 连接池大小配置 - 针对公益项目管理的并发特点优化
        config.setMinimumIdle(5);           // 最小空闲连接数
        config.setMaximumPoolSize(20);      // 最大连接池大小
        config.setConnectionTimeout(30000); // 连接超时时间 30秒
        config.setIdleTimeout(600000);      // 空闲连接超时时间 10分钟
        config.setMaxLifetime(1800000);     // 连接最大生命周期 30分钟
        config.setLeakDetectionThreshold(60000); // 连接泄漏检测阈值 1分钟
        
        // 性能优化配置
        config.setConnectionTestQuery("SELECT 1");
        config.setValidationTimeout(5000);  // 连接验证超时时间 5秒
        config.setInitializationFailTimeout(1); // 初始化失败超时时间
        
        // 连接池名称
        config.setPoolName("YXRobot-Charity-HikariCP");
        
        // 缓存配置 - 优化PreparedStatement缓存
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");
        
        // 字符集和时区配置
        config.addDataSourceProperty("useUnicode", "true");
        config.addDataSourceProperty("characterEncoding", "utf8");
        config.addDataSourceProperty("serverTimezone", "Asia/Shanghai");
        
        // SSL和安全配置
        config.addDataSourceProperty("useSSL", "false");
        config.addDataSourceProperty("allowPublicKeyRetrieval", "true");
        
        // 针对公益项目管理的查询优化
        config.addDataSourceProperty("defaultFetchSize", "100"); // 默认获取大小
        config.addDataSourceProperty("useCursorFetch", "true");  // 使用游标获取
        
        return new HikariDataSource(config);
    }
}