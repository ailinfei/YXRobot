package com.yxrobot.config;

import com.yxrobot.interceptor.PerformanceInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web性能监控配置
 * 配置性能监控拦截器
 */
@Configuration
public class WebPerformanceConfig implements WebMvcConfigurer {

    @Autowired
    private PerformanceInterceptor performanceInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加性能监控拦截器
        registry.addInterceptor(performanceInterceptor)
                .addPathPatterns("/api/admin/devices/**")  // 只监控设备管理API
                .excludePathPatterns("/api/admin/devices/health"); // 排除健康检查接口
    }
}