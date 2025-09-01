package com.yxrobot.config;

import com.yxrobot.interceptor.CustomerPerformanceMonitorInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;

/**
 * 客户管理模块性能优化配置
 * 配置性能监控、缓存管理等功能
 */
@Configuration
@EnableScheduling
public class CustomerPerformanceConfig implements WebMvcConfigurer {
    
    @Autowired
    private CustomerPerformanceMonitorInterceptor performanceMonitorInterceptor;
    
    @Autowired
    private com.yxrobot.cache.CustomerCacheService cacheService;
    
    /**
     * 注册性能监控拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(performanceMonitorInterceptor)
                .addPathPatterns("/api/admin/customers/**")
                .order(1); // 设置拦截器执行顺序
    }
    
    /**
     * 系统启动后的初始化操作
     */
    @PostConstruct
    public void init() {
        // 预热缓存
        cacheService.warmUpCache();
        
        // 记录系统启动信息
        org.slf4j.LoggerFactory.getLogger(CustomerPerformanceConfig.class)
            .info("Customer performance optimization configuration initialized");
    }
    
    /**
     * 定期清理过期缓存
     * 每5分钟执行一次
     */
    @Scheduled(fixedRate = 300000) // 5分钟 = 300000毫秒
    public void clearExpiredCache() {
        cacheService.clearExpiredCache();
    }
    
    /**
     * 定期输出缓存统计信息
     * 每30分钟执行一次
     */
    @Scheduled(fixedRate = 1800000) // 30分钟 = 1800000毫秒
    public void logCacheStats() {
        var stats = cacheService.getCacheStats();
        org.slf4j.LoggerFactory.getLogger(CustomerPerformanceConfig.class)
            .info("Customer cache statistics: {}", stats);
    }
    
    /**
     * 每天凌晨2点清理所有缓存
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void dailyCacheClear() {
        cacheService.clearAllCache();
        org.slf4j.LoggerFactory.getLogger(CustomerPerformanceConfig.class)
            .info("Daily cache clear completed");
    }
    
    /**
     * 每天凌晨3点预热缓存
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void dailyCacheWarmUp() {
        cacheService.warmUpCache();
        org.slf4j.LoggerFactory.getLogger(CustomerPerformanceConfig.class)
            .info("Daily cache warm-up completed");
    }
    
    /**
     * 性能监控配置Bean
     */
    @Bean
    public CustomerPerformanceProperties customerPerformanceProperties() {
        return new CustomerPerformanceProperties();
    }
    
    /**
     * 性能监控配置属性类
     */
    public static class CustomerPerformanceProperties {
        private boolean enablePerformanceMonitoring = true;
        private boolean enableCaching = true;
        private long customerListThreshold = 2000;
        private long customerStatsThreshold = 1000;
        private long customerDetailThreshold = 1500;
        private long defaultThreshold = 3000;
        
        // Getter和Setter方法
        public boolean isEnablePerformanceMonitoring() {
            return enablePerformanceMonitoring;
        }
        
        public void setEnablePerformanceMonitoring(boolean enablePerformanceMonitoring) {
            this.enablePerformanceMonitoring = enablePerformanceMonitoring;
        }
        
        public boolean isEnableCaching() {
            return enableCaching;
        }
        
        public void setEnableCaching(boolean enableCaching) {
            this.enableCaching = enableCaching;
        }
        
        public long getCustomerListThreshold() {
            return customerListThreshold;
        }
        
        public void setCustomerListThreshold(long customerListThreshold) {
            this.customerListThreshold = customerListThreshold;
        }
        
        public long getCustomerStatsThreshold() {
            return customerStatsThreshold;
        }
        
        public void setCustomerStatsThreshold(long customerStatsThreshold) {
            this.customerStatsThreshold = customerStatsThreshold;
        }
        
        public long getCustomerDetailThreshold() {
            return customerDetailThreshold;
        }
        
        public void setCustomerDetailThreshold(long customerDetailThreshold) {
            this.customerDetailThreshold = customerDetailThreshold;
        }
        
        public long getDefaultThreshold() {
            return defaultThreshold;
        }
        
        public void setDefaultThreshold(long defaultThreshold) {
            this.defaultThreshold = defaultThreshold;
        }
    }
}