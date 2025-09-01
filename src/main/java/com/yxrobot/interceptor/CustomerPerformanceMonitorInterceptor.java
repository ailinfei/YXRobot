package com.yxrobot.interceptor;

import com.yxrobot.service.CustomerPerformanceMonitorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 客户管理模块性能监控拦截器
 * 监控API响应时间，确保满足前端性能要求
 */
@Component
public class CustomerPerformanceMonitorInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerPerformanceMonitorInterceptor.class);
    
    @Autowired
    private CustomerPerformanceMonitorService performanceMonitorService;
    
    // 性能阈值配置（毫秒）
    private static final long CUSTOMER_LIST_THRESHOLD = 2000;    // 客户列表API: 2秒
    private static final long CUSTOMER_STATS_THRESHOLD = 1000;   // 客户统计API: 1秒
    private static final long CUSTOMER_DETAIL_THRESHOLD = 1500;  // 客户详情API: 1.5秒
    private static final long DEFAULT_THRESHOLD = 3000;         // 默认阈值: 3秒
    
    private static final String START_TIME_ATTRIBUTE = "startTime";
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 记录请求开始时间
        long startTime = System.currentTimeMillis();
        request.setAttribute(START_TIME_ATTRIBUTE, startTime);
        
        // 记录请求信息
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        String queryString = request.getQueryString();
        String clientIP = getClientIP(request);
        
        logger.info("API Request Started - Method: {}, URI: {}, Query: {}, Client IP: {}", 
                   method, requestURI, queryString, clientIP);
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                               Object handler, Exception ex) {
        
        Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        if (startTime == null) {
            return;
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        int statusCode = response.getStatus();
        
        // 确定性能阈值
        long threshold = getPerformanceThreshold(requestURI);
        
        // 记录性能日志
        if (duration > threshold) {
            logger.warn("SLOW API Response - Method: {}, URI: {}, Duration: {}ms, Threshold: {}ms, Status: {}", 
                       method, requestURI, duration, threshold, statusCode);
            
            // 记录慢查询详细信息
            logSlowRequestDetails(request, duration, threshold);
        } else {
            logger.info("API Response Completed - Method: {}, URI: {}, Duration: {}ms, Status: {}", 
                       method, requestURI, duration, statusCode);
        }
        
        // 记录异常信息
        if (ex != null) {
            logger.error("API Request Exception - Method: {}, URI: {}, Duration: {}ms, Exception: {}", 
                        method, requestURI, duration, ex.getMessage(), ex);
        }
        
        // 添加性能头信息到响应
        response.setHeader("X-Response-Time", String.valueOf(duration));
        response.setHeader("X-Performance-Threshold", String.valueOf(threshold));
        
        // 记录性能指标到监控系统
        performanceMonitorService.recordApiPerformance(method, requestURI, duration, statusCode);
        recordPerformanceMetrics(requestURI, method, duration, statusCode);
    }
    
    /**
     * 根据API路径确定性能阈值
     */
    private long getPerformanceThreshold(String requestURI) {
        if (requestURI.contains("/api/admin/customers/stats")) {
            return CUSTOMER_STATS_THRESHOLD;
        } else if (requestURI.matches(".*/api/admin/customers/\\d+$")) {
            return CUSTOMER_DETAIL_THRESHOLD;
        } else if (requestURI.contains("/api/admin/customers") && !requestURI.contains("/")) {
            return CUSTOMER_LIST_THRESHOLD;
        } else {
            return DEFAULT_THRESHOLD;
        }
    }
    
    /**
     * 记录慢请求的详细信息
     */
    private void logSlowRequestDetails(HttpServletRequest request, long duration, long threshold) {
        StringBuilder details = new StringBuilder();
        details.append("Slow Request Details:\n");
        details.append("  URI: ").append(request.getRequestURI()).append("\n");
        details.append("  Method: ").append(request.getMethod()).append("\n");
        details.append("  Duration: ").append(duration).append("ms\n");
        details.append("  Threshold: ").append(threshold).append("ms\n");
        details.append("  Query String: ").append(request.getQueryString()).append("\n");
        details.append("  User Agent: ").append(request.getHeader("User-Agent")).append("\n");
        details.append("  Client IP: ").append(getClientIP(request)).append("\n");
        
        // 记录请求参数
        details.append("  Parameters:\n");
        request.getParameterMap().forEach((key, values) -> {
            details.append("    ").append(key).append(": ").append(String.join(", ", values)).append("\n");
        });
        
        logger.warn(details.toString());
    }
    
    /**
     * 获取客户端真实IP地址
     */
    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty() && !"unknown".equalsIgnoreCase(xRealIP)) {
            return xRealIP;
        }
        
        return request.getRemoteAddr();
    }
    
    /**
     * 记录性能指标到监控系统
     * 可以集成Prometheus、Micrometer等监控工具
     */
    private void recordPerformanceMetrics(String uri, String method, long duration, int statusCode) {
        // 这里可以集成具体的监控系统
        // 例如：Micrometer Timer
        /*
        Timer.Sample sample = Timer.start(meterRegistry);
        sample.stop(Timer.builder("customer.api.duration")
                .tag("uri", uri)
                .tag("method", method)
                .tag("status", String.valueOf(statusCode))
                .register(meterRegistry));
        */
        
        // 或者发送到自定义监控系统
        // metricsService.recordApiDuration(uri, method, duration, statusCode);
        
        // 临时实现：记录到日志
        logger.debug("Performance Metric - URI: {}, Method: {}, Duration: {}ms, Status: {}", 
                    uri, method, duration, statusCode);
    }
    
    /**
     * 检查是否为客户管理相关的API
     */
    private boolean isCustomerAPI(String requestURI) {
        return requestURI.startsWith("/api/admin/customers");
    }
    
    /**
     * 获取API类型描述
     */
    private String getAPIType(String requestURI) {
        if (requestURI.contains("/stats")) {
            return "Customer Stats";
        } else if (requestURI.matches(".*/api/admin/customers/\\d+$")) {
            return "Customer Detail";
        } else if (requestURI.contains("/devices")) {
            return "Customer Devices";
        } else if (requestURI.contains("/orders")) {
            return "Customer Orders";
        } else if (requestURI.contains("/service-records")) {
            return "Customer Service Records";
        } else if (requestURI.equals("/api/admin/customers")) {
            return "Customer List";
        } else {
            return "Customer API";
        }
    }
}