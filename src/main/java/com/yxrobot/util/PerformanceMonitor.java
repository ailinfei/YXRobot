package com.yxrobot.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 性能监控工具类
 * 用于监控API响应时间和系统性能指标
 */
@Component
public class PerformanceMonitor {
    
    private static final Logger logger = LoggerFactory.getLogger(PerformanceMonitor.class);
    
    // 存储各个API的性能统计数据
    private final Map<String, ApiPerformanceStats> apiStats = new ConcurrentHashMap<>();
    
    // 全局请求计数器
    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong totalErrors = new AtomicLong(0);
    
    /**
     * 记录API调用开始时间
     * 
     * @param apiPath API路径
     * @return 开始时间戳
     */
    public long startTiming(String apiPath) {
        totalRequests.incrementAndGet();
        return System.currentTimeMillis();
    }
    
    /**
     * 记录API调用结束时间并计算性能指标
     * 
     * @param apiPath API路径
     * @param startTime 开始时间戳
     * @param success 是否成功
     */
    public void endTiming(String apiPath, long startTime, boolean success) {
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // 获取或创建API统计数据
        ApiPerformanceStats stats = apiStats.computeIfAbsent(apiPath, k -> new ApiPerformanceStats());
        
        // 更新统计数据
        stats.addRequest(duration, success);
        
        if (!success) {
            totalErrors.incrementAndGet();
        }
        
        // 记录慢查询
        if (duration > getSlowQueryThreshold(apiPath)) {
            logger.warn("慢查询检测: API={}, 耗时={}ms, 成功={}", apiPath, duration, success);
        }
        
        // 记录详细性能日志
        if (logger.isDebugEnabled()) {
            logger.debug("API性能: path={}, duration={}ms, success={}, avgTime={}ms, totalCalls={}", 
                apiPath, duration, success, stats.getAverageResponseTime(), stats.getTotalCalls());
        }
    }
    
    /**
     * 获取API性能统计数据
     * 
     * @param apiPath API路径
     * @return 性能统计数据
     */
    public ApiPerformanceStats getApiStats(String apiPath) {
        return apiStats.get(apiPath);
    }
    
    /**
     * 获取所有API的性能统计数据
     * 
     * @return 所有API的性能统计数据
     */
    public Map<String, ApiPerformanceStats> getAllApiStats() {
        return new ConcurrentHashMap<>(apiStats);
    }
    
    /**
     * 获取系统整体性能指标
     * 
     * @return 系统性能指标
     */
    public SystemPerformanceMetrics getSystemMetrics() {
        SystemPerformanceMetrics metrics = new SystemPerformanceMetrics();
        metrics.setTotalRequests(totalRequests.get());
        metrics.setTotalErrors(totalErrors.get());
        metrics.setErrorRate(calculateErrorRate());
        metrics.setAverageResponseTime(calculateOverallAverageResponseTime());
        metrics.setActiveApis(apiStats.size());
        
        return metrics;
    }
    
    /**
     * 重置所有统计数据
     */
    public void reset() {
        apiStats.clear();
        totalRequests.set(0);
        totalErrors.set(0);
        logger.info("性能监控数据已重置");
    }
    
    /**
     * 获取慢查询阈值
     * 
     * @param apiPath API路径
     * @return 阈值（毫秒）
     */
    private long getSlowQueryThreshold(String apiPath) {
        // 根据不同的API类型设置不同的阈值
        if (apiPath.contains("/search") || apiPath.contains("/stats")) {
            return 1000; // 搜索和统计API：1秒
        } else if (apiPath.contains("/batch")) {
            return 3000; // 批量操作API：3秒
        } else {
            return 2000; // 其他API：2秒
        }
    }
    
    /**
     * 计算错误率
     * 
     * @return 错误率（百分比）
     */
    private double calculateErrorRate() {
        long total = totalRequests.get();
        if (total == 0) {
            return 0.0;
        }
        return (double) totalErrors.get() / total * 100;
    }
    
    /**
     * 计算整体平均响应时间
     * 
     * @return 平均响应时间（毫秒）
     */
    private double calculateOverallAverageResponseTime() {
        if (apiStats.isEmpty()) {
            return 0.0;
        }
        
        long totalTime = 0;
        long totalCalls = 0;
        
        for (ApiPerformanceStats stats : apiStats.values()) {
            totalTime += stats.getTotalResponseTime();
            totalCalls += stats.getTotalCalls();
        }
        
        return totalCalls > 0 ? (double) totalTime / totalCalls : 0.0;
    }
    
    /**
     * API性能统计数据类
     */
    public static class ApiPerformanceStats {
        private final AtomicLong totalCalls = new AtomicLong(0);
        private final AtomicLong totalResponseTime = new AtomicLong(0);
        private final AtomicLong successCalls = new AtomicLong(0);
        private final AtomicLong errorCalls = new AtomicLong(0);
        private volatile long minResponseTime = Long.MAX_VALUE;
        private volatile long maxResponseTime = 0;
        private volatile long lastCallTime = 0;
        
        public void addRequest(long responseTime, boolean success) {
            totalCalls.incrementAndGet();
            totalResponseTime.addAndGet(responseTime);
            lastCallTime = System.currentTimeMillis();
            
            if (success) {
                successCalls.incrementAndGet();
            } else {
                errorCalls.incrementAndGet();
            }
            
            // 更新最小和最大响应时间
            if (responseTime < minResponseTime) {
                minResponseTime = responseTime;
            }
            if (responseTime > maxResponseTime) {
                maxResponseTime = responseTime;
            }
        }
        
        public long getTotalCalls() {
            return totalCalls.get();
        }
        
        public long getTotalResponseTime() {
            return totalResponseTime.get();
        }
        
        public double getAverageResponseTime() {
            long calls = totalCalls.get();
            return calls > 0 ? (double) totalResponseTime.get() / calls : 0.0;
        }
        
        public long getSuccessCalls() {
            return successCalls.get();
        }
        
        public long getErrorCalls() {
            return errorCalls.get();
        }
        
        public double getSuccessRate() {
            long total = totalCalls.get();
            return total > 0 ? (double) successCalls.get() / total * 100 : 0.0;
        }
        
        public long getMinResponseTime() {
            return minResponseTime == Long.MAX_VALUE ? 0 : minResponseTime;
        }
        
        public long getMaxResponseTime() {
            return maxResponseTime;
        }
        
        public long getLastCallTime() {
            return lastCallTime;
        }
    }
    
    /**
     * 系统性能指标类
     */
    public static class SystemPerformanceMetrics {
        private long totalRequests;
        private long totalErrors;
        private double errorRate;
        private double averageResponseTime;
        private int activeApis;
        
        // Getter和Setter方法
        public long getTotalRequests() { return totalRequests; }
        public void setTotalRequests(long totalRequests) { this.totalRequests = totalRequests; }
        
        public long getTotalErrors() { return totalErrors; }
        public void setTotalErrors(long totalErrors) { this.totalErrors = totalErrors; }
        
        public double getErrorRate() { return errorRate; }
        public void setErrorRate(double errorRate) { this.errorRate = errorRate; }
        
        public double getAverageResponseTime() { return averageResponseTime; }
        public void setAverageResponseTime(double averageResponseTime) { this.averageResponseTime = averageResponseTime; }
        
        public int getActiveApis() { return activeApis; }
        public void setActiveApis(int activeApis) { this.activeApis = activeApis; }
    }
}