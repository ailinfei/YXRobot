package com.yxrobot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 设备管理性能监控服务
 * 监控API响应时间、数据库查询性能、业务指标等
 */
@Service
public class ManagedDevicePerformanceMonitorService {

    private static final Logger logger = LoggerFactory.getLogger(ManagedDevicePerformanceMonitorService.class);
    private static final Logger performanceLogger = LoggerFactory.getLogger("PERFORMANCE");
    
    // 性能指标存储
    private final ConcurrentHashMap<String, AtomicLong> apiCallCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> apiResponseTimes = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> databaseQueryTimes = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> slowQueryCounts = new ConcurrentHashMap<>();
    
    // 性能阈值配置
    private static final long API_SLOW_THRESHOLD = 2000; // API慢响应阈值 2秒
    private static final long DB_SLOW_THRESHOLD = 1000;  // 数据库慢查询阈值 1秒
    
    /**
     * 记录API调用性能
     * 
     * @param apiName API名称
     * @param responseTime 响应时间（毫秒）
     */
    public void recordApiPerformance(String apiName, long responseTime) {
        // 更新调用次数
        apiCallCounts.computeIfAbsent(apiName, k -> new AtomicLong(0)).incrementAndGet();
        
        // 更新响应时间
        apiResponseTimes.computeIfAbsent(apiName, k -> new AtomicLong(0)).addAndGet(responseTime);
        
        // 记录慢响应
        if (responseTime > API_SLOW_THRESHOLD) {
            performanceLogger.warn("慢API响应 - API: {}, 响应时间: {}ms, 时间: {}", 
                apiName, responseTime, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
        
        // 记录性能日志
        performanceLogger.info("API性能 - API: {}, 响应时间: {}ms", apiName, responseTime);
    }
    
    /**
     * 记录数据库查询性能
     * 
     * @param queryName 查询名称
     * @param queryTime 查询时间（毫秒）
     */
    public void recordDatabaseQueryPerformance(String queryName, long queryTime) {
        // 更新查询时间
        databaseQueryTimes.computeIfAbsent(queryName, k -> new AtomicLong(0)).addAndGet(queryTime);
        
        // 记录慢查询
        if (queryTime > DB_SLOW_THRESHOLD) {
            slowQueryCounts.computeIfAbsent(queryName, k -> new AtomicLong(0)).incrementAndGet();
            performanceLogger.warn("慢查询检测 - 查询: {}, 执行时间: {}ms, 时间: {}", 
                queryName, queryTime, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
        
        // 记录查询性能日志
        performanceLogger.debug("数据库查询性能 - 查询: {}, 执行时间: {}ms", queryName, queryTime);
    }
    
    /**
     * 记录业务监控指标
     * 
     * @param metricName 指标名称
     * @param value 指标值
     */
    public void recordBusinessMetric(String metricName, long value) {
        performanceLogger.info("业务指标 - 指标: {}, 值: {}, 时间: {}", 
            metricName, value, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
    
    /**
     * 获取API平均响应时间
     * 
     * @param apiName API名称
     * @return 平均响应时间（毫秒）
     */
    public double getAverageApiResponseTime(String apiName) {
        AtomicLong totalTime = apiResponseTimes.get(apiName);
        AtomicLong callCount = apiCallCounts.get(apiName);
        
        if (totalTime == null || callCount == null || callCount.get() == 0) {
            return 0.0;
        }
        
        return (double) totalTime.get() / callCount.get();
    }
    
    /**
     * 获取API调用次数
     * 
     * @param apiName API名称
     * @return 调用次数
     */
    public long getApiCallCount(String apiName) {
        AtomicLong count = apiCallCounts.get(apiName);
        return count != null ? count.get() : 0;
    }
    
    /**
     * 获取慢查询次数
     * 
     * @param queryName 查询名称
     * @return 慢查询次数
     */
    public long getSlowQueryCount(String queryName) {
        AtomicLong count = slowQueryCounts.get(queryName);
        return count != null ? count.get() : 0;
    }
    
    /**
     * 记录设备在线率指标
     * 
     * @param totalDevices 设备总数
     * @param onlineDevices 在线设备数
     */
    public void recordDeviceOnlineRate(long totalDevices, long onlineDevices) {
        if (totalDevices > 0) {
            double onlineRate = (double) onlineDevices / totalDevices * 100;
            recordBusinessMetric("device_online_rate", Math.round(onlineRate));
            recordBusinessMetric("total_devices", totalDevices);
            recordBusinessMetric("online_devices", onlineDevices);
        }
    }
    
    /**
     * 记录功能使用统计
     * 
     * @param functionName 功能名称
     */
    public void recordFunctionUsage(String functionName) {
        String metricName = "function_usage_" + functionName;
        AtomicLong count = apiCallCounts.computeIfAbsent(metricName, k -> new AtomicLong(0));
        count.incrementAndGet();
        
        performanceLogger.info("功能使用统计 - 功能: {}, 使用次数: {}", functionName, count.get());
    }
    
    /**
     * 记录并发访问性能
     * 
     * @param concurrentUsers 并发用户数
     * @param responseTime 响应时间
     */
    public void recordConcurrentAccessPerformance(int concurrentUsers, long responseTime) {
        performanceLogger.info("并发访问性能 - 并发用户数: {}, 响应时间: {}ms, 时间: {}", 
            concurrentUsers, responseTime, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
    
    /**
     * 生成性能报告
     * 
     * @return 性能报告字符串
     */
    public String generatePerformanceReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== 设备管理模块性能报告 ===\n");
        report.append("生成时间: ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\n\n");
        
        // API性能统计
        report.append("API性能统计:\n");
        apiCallCounts.forEach((apiName, count) -> {
            double avgTime = getAverageApiResponseTime(apiName);
            report.append(String.format("  %s: 调用次数=%d, 平均响应时间=%.2fms\n", 
                apiName, count.get(), avgTime));
        });
        
        // 慢查询统计
        report.append("\n慢查询统计:\n");
        slowQueryCounts.forEach((queryName, count) -> {
            report.append(String.format("  %s: 慢查询次数=%d\n", queryName, count.get()));
        });
        
        return report.toString();
    }
}