package com.yxrobot.config;

import com.yxrobot.interceptor.PerformanceMonitorInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 租赁模块性能监控配置
 * 配置性能监控拦截器和相关设置
 * 
 * @author Kiro
 * @date 2025-01-28
 */
@Configuration
public class RentalPerformanceConfig implements WebMvcConfigurer {
    
    @Autowired
    private PerformanceMonitorInterceptor performanceMonitorInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加性能监控拦截器，只监控租赁模块API
        registry.addInterceptor(performanceMonitorInterceptor)
                .addPathPatterns("/api/rental/**")
                .order(1); // 设置拦截器优先级
    }
}