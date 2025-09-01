package com.yxrobot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 客户管理模块API响应时间监控服务
 * 实现API响应时间监控，确保满足前端性能要求
 */
@Service
public class CustomerPerformanceMonitorService {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerPerformanceMonitorService.class);
    
    // 性能阈值配置（毫秒）
    private static final Map<String, Long> PERFORMANCE_THRESHOLDS = Map.of(
        "GET:/api/admin/customers/stats", 1000L,           // 客户统计API: 1秒
        "GET:/api/admin/customers", 2000L,                 // 客户列表API: 2秒
        "GET:/api/admin/customers/{id}", 1500L,            // 客户详情API: 1.5秒
        "POST:/api/admin/customers", 3000L,                // 客户创建API: 3秒
        "PUT:/api/admin/customers/{id}", 2000L,            // 客户更新API: 2秒
        "DELETE:/api/admin/customers/{id}", 1000L,         // 客户删除API: 1秒
        "GET:/api/admin/customers/{id}/devices", 2000L,    // 客户设备API: 2秒
        "GET:/api/admin/customers/{id}/orders", 2000L,     // 客户订单API: 2秒
        "GET:/api/admin/customers/{id}/service-records", 2000L // 客户服务记录API: 2秒
    );
    
    // 性能统计数据
    private final Map<String, PerformanceStats> performanceStats = new ConcurrentHashMap<>();
    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong slowRequests = new AtomicLong(0);
    
    /**
     * 记录API性能数据
     */
    public void recordApiPerformance(String method, String uri, long duration, int statusCode) {
        String apiKey = method + ":" + normalizeUri(uri);
        
        // 更新总请求计数
        totalRequests.incrementAndGet();
        
        // 获取或创建性能统计
        PerformanceStats stats = performanceStats.computeIfAbsent(apiKey, k -> new PerformanceStats(k));
        
        // 更新统计数据
        stats.recordRequest(duration, statusCode);
        
        // 检查是否超过性能阈值
        Long threshold = getPerformanceThreshold(apiKey);
        if (threshold != null && duration > threshold) {
            slowRequests.incrementAndGet();
            
            logger.warn("API性能告警 - API: {}, 响应时间: {}ms, 阈值: {}ms, 状态码: {}", 
                       apiKey, duration, threshold, statusCode);
            
            // 记录慢请求详情
            recordSlowRequest(apiKey, duration, threshold, statusCode);
        }
        
        // 定期输出性能统计
        if (totalRequests.get() % 100 == 0) {
            logPerformanceSummary();
        }
    }
    
    /**
     * 获取API性能阈值
     */
    private Long getPerformanceThreshold(String apiKey) {
        // 精确匹配
        Long threshold = PERFORMANCE_THRESHOLDS.get(apiKey);
        if (threshold != null) {
            return threshold;
        }
        
        // 模式匹配
        for (Map.Entry<String, Long> entry : PERFORMANCE_THRESHOLDS.entrySet()) {
            if (matchesPattern(apiKey, entry.getKey())) {
                return entry.getValue();
            }
        }
        
        // 默认阈值
        return 3000L;
    }
    
    /**
     * 模式匹配（支持路径参数）
     */
    private boolean matchesPattern(String apiKey, String pattern) {
        if (pattern.contains("{id}")) {
            String regex = pattern.replace("{id}", "\\d+");
            return apiKey.matches(regex);
        }
        return false;
    }
    
    /**
     * 标准化URI（将路径参数替换为占位符）
     */
    private String normalizeUri(String uri) {
        // 将数字ID替换为{id}占位符
        return uri.replaceAll("/\\d+", "/{id}")
                 .replaceAll("/\\d+/", "/{id}/");
    }
    
    /**
     * 记录慢请求详情
     */
    private void recordSlowRequest(String apiKey, long duration, long threshold, int statusCode) {
        SlowRequestRecord record = new SlowRequestRecord(
            apiKey, duration, threshold, statusCode, LocalDateTime.now()
        );
        
        // 这里可以将慢请求记录发送到监控系统
        // 例如：发送到Prometheus、InfluxDB等
        logger.info("慢请求记录: {}", record);
    }
    
    /**
     * 输出性能统计摘要
     */
    private void logPerformanceSummary() {
        long total = totalRequests.get();
        long slow = slowRequests.get();
        double slowRate = total > 0 ? (double) slow / total * 100 : 0;
        
        logger.info("API性能统计 - 总请求数: {}, 慢请求数: {}, 慢请求率: {:.2f}%", 
                   total, slow, slowRate);
        
        // 输出Top 5慢API
        List<PerformanceStats> topSlowApis = performanceStats.values().stream()
            .sorted((a, b) -> Double.compare(b.getAverageResponseTime(), a.getAverageResponseTime()))
            .limit(5)
            .toList();
        
        if (!topSlowApis.isEmpty()) {
            logger.info("Top 5 慢API:");
            for (int i = 0; i < topSlowApis.size(); i++) {
                PerformanceStats stats = topSlowApis.get(i);
                logger.info("  {}. {} - 平均响应时间: {:.2f}ms, 请求数: {}", 
                           i + 1, stats.getApiKey(), stats.getAverageResponseTime(), stats.getTotalRequests());
            }
        }
    }
    
    /**
     * 获取性能统计报告
     */
    public PerformanceReport getPerformanceReport() {
        PerformanceReport report = new PerformanceReport();
        
        // 基础统计
        report.setTotalRequests(totalRequests.get());
        report.setSlowRequests(slowRequests.get());
        report.setSlowRequestRate(totalRequests.get() > 0 ? 
            (double) slowRequests.get() / totalRequests.get() * 100 : 0);
        
        // API统计
        List<ApiPerformanceInfo> apiInfos = new ArrayList<>();
        for (PerformanceStats stats : performanceStats.values()) {
            ApiPerformanceInfo info = new ApiPerformanceInfo();
            info.setApiKey(stats.getApiKey());
            info.setTotalRequests(stats.getTotalRequests());
            info.setAverageResponseTime(stats.getAverageResponseTime());
            info.setMinResponseTime(stats.getMinResponseTime());
            info.setMaxResponseTime(stats.getMaxResponseTime());
            info.setSuccessRate(stats.getSuccessRate());
            info.setThreshold(getPerformanceThreshold(stats.getApiKey()));
            
            apiInfos.add(info);
        }
        
        // 按平均响应时间排序
        apiInfos.sort((a, b) -> Double.compare(b.getAverageResponseTime(), a.getAverageResponseTime()));
        report.setApiPerformanceInfos(apiInfos);
        
        return report;
    }
    
    /**
     * 重置性能统计
     */
    public void resetPerformanceStats() {
        performanceStats.clear();
        totalRequests.set(0);
        slowRequests.set(0);
        logger.info("性能统计数据已重置");
    }
    
    /**
     * 检查API是否健康
     */
    public boolean isApiHealthy(String method, String uri) {
        String apiKey = method + ":" + normalizeUri(uri);
        PerformanceStats stats = performanceStats.get(apiKey);
        
        if (stats == null) {
            return true; // 没有统计数据，认为是健康的
        }
        
        // 检查成功率
        if (stats.getSuccessRate() < 95.0) {
            return false;
        }
        
        // 检查平均响应时间
        Long threshold = getPerformanceThreshold(apiKey);
        if (threshold != null && stats.getAverageResponseTime() > threshold * 1.5) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 性能统计类
     */
    private static class PerformanceStats {
        private final String apiKey;
        private final AtomicLong totalRequests = new AtomicLong(0);
        private final AtomicLong successRequests = new AtomicLong(0);
        private final AtomicLong totalResponseTime = new AtomicLong(0);
        private volatile long minResponseTime = Long.MAX_VALUE;
        private volatile long maxResponseTime = 0;
        
        public PerformanceStats(String apiKey) {
            this.apiKey = apiKey;
        }
        
        public void recordRequest(long duration, int statusCode) {
            totalRequests.incrementAndGet();
            totalResponseTime.addAndGet(duration);
            
            if (statusCode >= 200 && statusCode < 400) {
                successRequests.incrementAndGet();
            }
            
            // 更新最小和最大响应时间
            if (duration < minResponseTime) {
                minResponseTime = duration;
            }
            if (duration > maxResponseTime) {
                maxResponseTime = duration;
            }
        }
        
        public String getApiKey() {
            return apiKey;
        }
        
        public long getTotalRequests() {
            return totalRequests.get();
        }
        
        public double getAverageResponseTime() {
            long total = totalRequests.get();
            return total > 0 ? (double) totalResponseTime.get() / total : 0;
        }
        
        public long getMinResponseTime() {
            return minResponseTime == Long.MAX_VALUE ? 0 : minResponseTime;
        }
        
        public long getMaxResponseTime() {
            return maxResponseTime;
        }
        
        public double getSuccessRate() {
            long total = totalRequests.get();
            return total > 0 ? (double) successRequests.get() / total * 100 : 0;
        }
    }
    
    /**
     * 慢请求记录类
     */
    private static class SlowRequestRecord {
        private final String apiKey;
        private final long duration;
        private final long threshold;
        private final int statusCode;
        private final LocalDateTime timestamp;
        
        public SlowRequestRecord(String apiKey, long duration, long threshold, int statusCode, LocalDateTime timestamp) {
            this.apiKey = apiKey;
            this.duration = duration;
            this.threshold = threshold;
            this.statusCode = statusCode;
            this.timestamp = timestamp;
        }
        
        @Override
        public String toString() {
            return String.format("SlowRequest{api='%s', duration=%dms, threshold=%dms, status=%d, time=%s}",
                apiKey, duration, threshold, statusCode, timestamp);
        }
    }
    
    /**
     * 性能报告类
     */
    public static class PerformanceReport {
        private long totalRequests;
        private long slowRequests;
        private double slowRequestRate;
        private List<ApiPerformanceInfo> apiPerformanceInfos;
        
        // Getters and Setters
        public long getTotalRequests() { return totalRequests; }
        public void setTotalRequests(long totalRequests) { this.totalRequests = totalRequests; }
        
        public long getSlowRequests() { return slowRequests; }
        public void setSlowRequests(long slowRequests) { this.slowRequests = slowRequests; }
        
        public double getSlowRequestRate() { return slowRequestRate; }
        public void setSlowRequestRate(double slowRequestRate) { this.slowRequestRate = slowRequestRate; }
        
        public List<ApiPerformanceInfo> getApiPerformanceInfos() { return apiPerformanceInfos; }
        public void setApiPerformanceInfos(List<ApiPerformanceInfo> apiPerformanceInfos) { this.apiPerformanceInfos = apiPerformanceInfos; }
    }
    
    /**
     * API性能信息类
     */
    public static class ApiPerformanceInfo {
        private String apiKey;
        private long totalRequests;
        private double averageResponseTime;
        private long minResponseTime;
        private long maxResponseTime;
        private double successRate;
        private Long threshold;
        
        // Getters and Setters
        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
        
        public long getTotalRequests() { return totalRequests; }
        public void setTotalRequests(long totalRequests) { this.totalRequests = totalRequests; }
        
        public double getAverageResponseTime() { return averageResponseTime; }
        public void setAverageResponseTime(double averageResponseTime) { this.averageResponseTime = averageResponseTime; }
        
        public long getMinResponseTime() { return minResponseTime; }
        public void setMinResponseTime(long minResponseTime) { this.minResponseTime = minResponseTime; }
        
        public long getMaxResponseTime() { return maxResponseTime; }
        public void setMaxResponseTime(long maxResponseTime) { this.maxResponseTime = maxResponseTime; }
        
        public double getSuccessRate() { return successRate; }
        public void setSuccessRate(double successRate) { this.successRate = successRate; }
        
        public Long getThreshold() { return threshold; }
        public void setThreshold(Long threshold) { this.threshold = threshold; }
    }
}