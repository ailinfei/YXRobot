package com.yxrobot.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * 数据库性能优化配置
 * 配置数据库连接池参数，提升数据库连接性能
 */
@Configuration
public class DatabasePerformanceConfig {

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    /**
     * 配置优化的数据库连接池
     * 
     * @return 优化配置的数据源
     */
    @Bean
    @Primary
    public DataSource optimizedDataSource() {
        HikariConfig config = new HikariConfig();
        
        // 基础连接配置
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driverClassName);
        
        // 连接池性能优化配置
        config.setMaximumPoolSize(20);              // 最大连接数
        config.setMinimumIdle(5);                   // 最小空闲连接数
        config.setConnectionTimeout(30000);         // 连接超时时间 30秒
        config.setIdleTimeout(600000);              // 空闲连接超时时间 10分钟
        config.setMaxLifetime(1800000);             // 连接最大生命周期 30分钟
        config.setLeakDetectionThreshold(60000);    // 连接泄漏检测阈值 1分钟
        
        // 性能优化配置
        config.setAutoCommit(true);                 // 自动提交
        config.setConnectionTestQuery("SELECT 1");  // 连接测试查询
        config.setValidationTimeout(5000);          // 验证超时时间 5秒
        
        // 连接池名称
        config.setPoolName("YXRobot-ManagedDevice-Pool");
        
        // 性能监控配置
        config.setRegisterMbeans(true);             // 启用JMX监控
        
        // 连接初始化SQL
        config.setConnectionInitSql("SET NAMES utf8mb4");
        
        return new HikariDataSource(config);
    }
}