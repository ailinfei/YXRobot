package com.yxrobot.config;

import com.yxrobot.interceptor.PerformanceInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * 配置拦截器、CORS等Web相关设置
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Autowired
    private PerformanceInterceptor performanceInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册性能监控拦截器
        registry.addInterceptor(performanceInterceptor)
                .addPathPatterns("/api/**") // 只拦截API请求
                .excludePathPatterns(
                    "/api/health",           // 排除健康检查
                    "/api/metrics",          // 排除指标接口
                    "/api/performance"       // 排除性能监控接口本身
                );
    }
}