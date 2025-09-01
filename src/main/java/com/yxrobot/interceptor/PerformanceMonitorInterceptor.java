package com.yxrobot.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * API性能监控拦截器
 * 监控租赁数据分析模块API的响应时间，确保满足前端性能要求
 * 
 * @author Kiro
 * @date 2025-01-28
 */
@Component
public class PerformanceMonitorInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(PerformanceMonitorInterceptor.class);
    private static final Logger performanceLogger = LoggerFactory.getLogger("PERFORMANCE");
    
    // 性能要求阈值（毫秒）
    private static final long STATS_API_THRESHOLD = 2000;      // 统计API: 2秒
    private static final long DEVICE_API_THRESHOLD = 2000;     // 设备API: 2秒
    private static final long CHART_API_THRESHOLD = 3000;      // 图表API: 3秒
    private static final long TODAY_STATS_THRESHOLD = 2000;    // 今日统计API: 2秒
    private static final long DEFAULT_THRESHOLD = 5000;        // 默认阈值: 5秒
    
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
        
        logger.debug("API请求开始: {} {} {}", method, requestURI, 
                    queryString != null ? "?" + queryString : "");
        
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
        long responseTime = endTime - startTime;
        
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        int statusCode = response.getStatus();
        
        // 判断是否为租赁模块API
        if (!isRentalAPI(requestURI)) {
            return;
        }
        
        // 获取API类型对应的性能阈值
        long threshold = getPerformanceThreshold(requestURI);
        
        // 记录性能日志
        logPerformance(method, requestURI, responseTime, statusCode, threshold);
        
        // 性能告警
        if (responseTime > threshold) {
            performanceLogger.warn("API性能告警: {} {} 响应时间 {}ms 超过阈值 {}ms, 状态码: {}", 
                                 method, requestURI, responseTime, threshold, statusCode);
        }
        
        // 记录异常情况
        if (ex != null) {
            logger.error("API执行异常: {} {}, 响应时间: {}ms", method, requestURI, responseTime, ex);
        }
    }
    
    /**
     * 判断是否为租赁模块API
     */
    private boolean isRentalAPI(String requestURI) {
        return requestURI != null && requestURI.startsWith("/api/rental/");
    }
    
    /**
     * 获取API对应的性能阈值
     */
    private long getPerformanceThreshold(String requestURI) {
        if (requestURI.contains("/api/rental/stats")) {
            return STATS_API_THRESHOLD;
        } else if (requestURI.contains("/api/rental/devices")) {
            return DEVICE_API_THRESHOLD;
        } else if (requestURI.contains("/api/rental/charts/")) {
            return CHART_API_THRESHOLD;
        } else if (requestURI.contains("/api/rental/today-stats") || 
                   requestURI.contains("/api/rental/device-status-stats") ||
                   requestURI.contains("/api/rental/top-devices")) {
            return TODAY_STATS_THRESHOLD;
        } else {
            return DEFAULT_THRESHOLD;
        }
    }
    
    /**
     * 记录性能日志
     */
    private void logPerformance(String method, String requestURI, long responseTime, 
                               int statusCode, long threshold) {
        
        String performanceLevel = getPerformanceLevel(responseTime, threshold);
        
        performanceLogger.info("API性能监控: {} {} | 响应时间: {}ms | 状态码: {} | 性能等级: {} | 阈值: {}ms", 
                              method, requestURI, responseTime, statusCode, performanceLevel, threshold);
        
        // 详细性能分析
        if (responseTime > threshold * 0.8) { // 超过阈值80%时记录详细信息
            performanceLogger.warn("API性能接近阈值: {} {} | 响应时间: {}ms | 阈值: {}ms | 使用率: {:.1f}%", 
                                  method, requestURI, responseTime, threshold, 
                                  (double) responseTime / threshold * 100);
        }
    }
    
    /**
     * 获取性能等级
     */
    private String getPerformanceLevel(long responseTime, long threshold) {
        double ratio = (double) responseTime / threshold;
        
        if (ratio <= 0.3) {
            return "优秀";
        } else if (ratio <= 0.6) {
            return "良好";
        } else if (ratio <= 0.8) {
            return "一般";
        } else if (ratio <= 1.0) {
            return "较慢";
        } else {
            return "超时";
        }
    }
}