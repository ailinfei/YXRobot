package com.yxrobot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 设备管理性能监控服务
 * 提供API响应时间监控、性能统计和优化建议
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@Service
public class ManagedDevicePerformanceService {
    
    private static final Logger logger = LoggerFactory.getLogger(ManagedDevicePerformanceService.class);
    private static final Logger performanceLogger = LoggerFactory.getLogger("PERFORMANCE");
    
    // API调用统计
    private final Map<String, AtomicInteger> apiCallCounts = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> apiResponseTimes = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> apiMaxResponseTimes = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> apiMinResponseTimes = new ConcurrentHashMap<>();
    
    // 数据库查询统计
    private final Map<String, AtomicInteger> dbQueryCounts = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> dbQueryTimes = new ConcurrentHashMap<>();
    
    // 错误统计
    private final Map<String, AtomicInteger> errorCounts = new ConcurrentHashMap<>();
    
    // 性能阈值配置
    private static final long API_SLOW_THRESHOLD = 1000; // 1秒
    private static final long DB_SLOW_THRESHOLD = 500;   // 500毫秒
    
    /**
     * 记录API调用性能
     * 
     * @param apiName API名称
     * @param responseTime 响应时间（毫秒）
     * @param success 是否成功
     */
    public void recordApiPerformance(String apiName, long responseTime, boolean success) {
        try {
            // 更新调用次数
            apiCallCounts.computeIfAbsent(apiName, k -> new AtomicInteger(0)).incrementAndGet();
            
            // 更新响应时间统计
            apiResponseTimes.computeIfAbsent(apiName, k -> new AtomicLong(0)).addAndGet(responseTime);
            
            // 更新最大响应时间
            apiMaxResponseTimes.computeIfAbsent(apiName, k -> new AtomicLong(0))
                .updateAndGet(current -> Math.max(current, responseTime));
            
            // 更新最小响应时间
            apiMinResponseTimes.computeIfAbsent(apiName, k -> new AtomicLong(Long.MAX_VALUE))
                .updateAndGet(current -> Math.min(current, responseTime));
            
            // 记录错误
            if (!success) {
                errorCounts.computeIfAbsent(apiName, k -> new AtomicInteger(0)).incrementAndGet();
            }
            
            // 检查是否为慢查询
            if (responseTime > API_SLOW_THRESHOLD) {
                logSlowApi(apiName, responseTime);
            }
            
            // 记录性能日志
            Map<String, Object> performanceLog = new HashMap<>();
            performanceLog.put("apiName", apiName);
            performanceLog.put("responseTime", responseTime);
            performanceLog.put("success", success);
            performanceLog.put("timestamp", LocalDateTime.now());
            
            performanceLogger.debug("API Performance: {}", performanceLog);
            
        } catch (Exception e) {
            logger.error("记录API性能失败", e);
        }
    }
    
    /**
     * 记录数据库查询性能
     * 
     * @param queryName 查询名称
     * @param queryTime 查询时间（毫秒）
     */
    public void recordDbQueryPerformance(String queryName, long queryTime) {
        try {
            // 更新查询次数
            dbQueryCounts.computeIfAbsent(queryName, k -> new AtomicInteger(0)).incrementAndGet();
            
            // 更新查询时间统计
            dbQueryTimes.computeIfAbsent(queryName, k -> new AtomicLong(0)).addAndGet(queryTime);
            
            // 检查是否为慢查询
            if (queryTime > DB_SLOW_THRESHOLD) {
                logSlowQuery(queryName, queryTime);
            }
            
            // 记录性能日志
            Map<String, Object> queryLog = new HashMap<>();
            queryLog.put("queryName", queryName);
            queryLog.put("queryTime", queryTime);
            queryLog.put("timestamp", LocalDateTime.now());
            
            performanceLogger.debug("DB Query Performance: {}", queryLog);
            
        } catch (Exception e) {
            logger.error("记录数据库查询性能失败", e);
        }
    }
    
    /**
     * 获取API性能统计
     * 
     * @return 性能统计信息
     */
    public Map<String, Object> getApiPerformanceStats() {
        Map<String, Object> stats = new HashMap<>();
        Map<String, Map<String, Object>> apiStats = new HashMap<>();
        
        for (String apiName : apiCallCounts.keySet()) {
            Map<String, Object> apiStat = new HashMap<>();
            
            int callCount = apiCallCounts.get(apiName).get();
            long totalTime = apiResponseTimes.get(apiName).get();
            long maxTime = apiMaxResponseTimes.get(apiName).get();
            long minTime = apiMinResponseTimes.get(apiName).get();
            int errorCount = errorCounts.getOrDefault(apiName, new AtomicInteger(0)).get();
            
            apiStat.put("callCount", callCount);
            apiStat.put("totalTime", totalTime);
            apiStat.put("averageTime", callCount > 0 ? totalTime / callCount : 0);
            apiStat.put("maxTime", maxTime);
            apiStat.put("minTime", minTime == Long.MAX_VALUE ? 0 : minTime);
            apiStat.put("errorCount", errorCount);
            apiStat.put("successRate", callCount > 0 ? (double)(callCount - errorCount) / callCount * 100 : 0);
            
            apiStats.put(apiName, apiStat);
        }
        
        stats.put("apiStats", apiStats);
        stats.put("totalApiCalls", apiCallCounts.values().stream().mapToInt(AtomicInteger::get).sum());
        stats.put("totalErrors", errorCounts.values().stream().mapToInt(AtomicInteger::get).sum());
        stats.put("lastUpdated", LocalDateTime.now());
        
        return stats;
    }
    
    /**
     * 获取数据库查询性能统计
     * 
     * @return 查询性能统计信息
     */
    public Map<String, Object> getDbQueryPerformanceStats() {
        Map<String, Object> stats = new HashMap<>();
        Map<String, Map<String, Object>> queryStats = new HashMap<>();
        
        for (String queryName : dbQueryCounts.keySet()) {
            Map<String, Object> queryStat = new HashMap<>();
            
            int queryCount = dbQueryCounts.get(queryName).get();
            long totalTime = dbQueryTimes.get(queryName).get();
            
            queryStat.put("queryCount", queryCount);
            queryStat.put("totalTime", totalTime);
            queryStat.put("averageTime", queryCount > 0 ? totalTime / queryCount : 0);
            
            queryStats.put(queryName, queryStat);
        }
        
        stats.put("queryStats", queryStats);
        stats.put("totalQueries", dbQueryCounts.values().stream().mapToInt(AtomicInteger::get).sum());
        stats.put("lastUpdated", LocalDateTime.now());
        
        return stats;
    }
    
    /**
     * 获取性能报告
     * 
     * @return 完整的性能报告
     */
    public Map<String, Object> getPerformanceReport() {
        Map<String, Object> report = new HashMap<>();
        
        // API性能统计
        report.put("apiPerformance", getApiPerformanceStats());
        
        // 数据库性能统计
        report.put("dbPerformance", getDbQueryPerformanceStats());
        
        // 系统资源使用情况
        Runtime runtime = Runtime.getRuntime();
        Map<String, Object> systemStats = new HashMap<>();
        systemStats.put("totalMemory", runtime.totalMemory());
        systemStats.put("freeMemory", runtime.freeMemory());
        systemStats.put("usedMemory", runtime.totalMemory() - runtime.freeMemory());
        systemStats.put("maxMemory", runtime.maxMemory());
        systemStats.put("availableProcessors", runtime.availableProcessors());
        
        report.put("systemStats", systemStats);
        
        // 性能建议
        report.put("recommendations", generatePerformanceRecommendations());
        
        report.put("generatedAt", LocalDateTime.now());
        
        return report;
    }
    
    /**
     * 清理过期的性能数据
     */
    public void cleanupPerformanceData() {
        try {
            // 这里可以实现数据清理逻辑
            // 例如：清理超过24小时的性能数据
            logger.debug("清理过期性能数据完成");
            
        } catch (Exception e) {
            logger.error("清理性能数据失败", e);
        }
    }
    
    /**
     * 检查API性能是否正常
     * 
     * @param apiName API名称
     * @return 是否正常
     */
    public boolean isApiPerformanceNormal(String apiName) {
        AtomicInteger callCount = apiCallCounts.get(apiName);
        AtomicLong totalTime = apiResponseTimes.get(apiName);
        
        if (callCount == null || totalTime == null || callCount.get() == 0) {
            return true; // 没有调用记录，认为正常
        }
        
        long averageTime = totalTime.get() / callCount.get();
        return averageTime <= API_SLOW_THRESHOLD;
    }
    
    /**
     * 获取慢API列表
     * 
     * @return 慢API列表
     */
    public Map<String, Long> getSlowApis() {
        Map<String, Long> slowApis = new HashMap<>();
        
        for (String apiName : apiCallCounts.keySet()) {
            AtomicInteger callCount = apiCallCounts.get(apiName);
            AtomicLong totalTime = apiResponseTimes.get(apiName);
            
            if (callCount.get() > 0) {
                long averageTime = totalTime.get() / callCount.get();
                if (averageTime > API_SLOW_THRESHOLD) {
                    slowApis.put(apiName, averageTime);
                }
            }
        }
        
        return slowApis;
    }
    
    /**
     * 记录慢API
     */
    private void logSlowApi(String apiName, long responseTime) {
        Map<String, Object> slowApiLog = new HashMap<>();
        slowApiLog.put("apiName", apiName);
        slowApiLog.put("responseTime", responseTime);
        slowApiLog.put("threshold", API_SLOW_THRESHOLD);
        slowApiLog.put("timestamp", LocalDateTime.now());
        
        performanceLogger.warn("Slow API detected: {}", slowApiLog);
    }
    
    /**
     * 记录慢查询
     */
    private void logSlowQuery(String queryName, long queryTime) {
        Map<String, Object> slowQueryLog = new HashMap<>();
        slowQueryLog.put("queryName", queryName);
        slowQueryLog.put("queryTime", queryTime);
        slowQueryLog.put("threshold", DB_SLOW_THRESHOLD);
        slowQueryLog.put("timestamp", LocalDateTime.now());
        
        performanceLogger.warn("Slow Query detected: {}", slowQueryLog);
    }
    
    /**
     * 生成性能优化建议
     */
    private Map<String, Object> generatePerformanceRecommendations() {
        Map<String, Object> recommendations = new HashMap<>();
        
        // 检查慢API
        Map<String, Long> slowApis = getSlowApis();
        if (!slowApis.isEmpty()) {
            recommendations.put("slowApis", slowApis);
            recommendations.put("slowApiAdvice", "建议优化响应时间超过" + API_SLOW_THRESHOLD + "ms的API");
        }
        
        // 检查错误率
        int totalCalls = apiCallCounts.values().stream().mapToInt(AtomicInteger::get).sum();
        int totalErrors = errorCounts.values().stream().mapToInt(AtomicInteger::get).sum();
        
        if (totalCalls > 0) {
            double errorRate = (double) totalErrors / totalCalls * 100;
            if (errorRate > 5.0) { // 错误率超过5%
                recommendations.put("highErrorRate", errorRate);
                recommendations.put("errorRateAdvice", "API错误率较高，建议检查错误处理逻辑");
            }
        }
        
        // 检查内存使用
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long maxMemory = runtime.maxMemory();
        double memoryUsageRate = (double) usedMemory / maxMemory * 100;
        
        if (memoryUsageRate > 80.0) {
            recommendations.put("highMemoryUsage", memoryUsageRate);
            recommendations.put("memoryAdvice", "内存使用率较高，建议优化内存使用");
        }
        
        return recommendations;
    }
}