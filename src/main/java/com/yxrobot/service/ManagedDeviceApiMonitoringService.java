package com.yxrobot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 设备管理API监控服务
 * 提供API响应时间监控、性能分析和告警功能
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-28
 */
@Service
public class ManagedDeviceApiMonitoringService {
    
    private static final Logger logger = LoggerFactory.getLogger(ManagedDeviceApiMonitoringService.class);
    
    @Autowired
    private ManagedDevicePerformanceOptimizationService performanceService;
    
    // 监控数据存储
    private final Map<String, List<ApiMetric>> apiMetrics = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> apiCallCounts = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> apiErrorCounts = new ConcurrentHashMap<>();
    
    // 性能阈值配置
    private static final long EXCELLENT_THRESHOLD = 200;  // 200ms
    private static final long GOOD_THRESHOLD = 500;       // 500ms
    private static final long ACCEPTABLE_THRESHOLD = 1000; // 1s
    private static final long POOR_THRESHOLD = 2000;      // 2s
    
    // 监控配置
    private static final int MAX_METRICS_PER_API = 1000;
    private static final int ALERT_THRESHOLD_COUNT = 5; // 连续5次超阈值触发告警
    
    /**
     * API指标数据类
     */
    public static class ApiMetric {
        private final String apiPath;
        private final String method;
        private final long responseTime;
        private final int statusCode;
        private final LocalDateTime timestamp;
        private final String clientIp;
        private final String userAgent;
        
        public ApiMetric(String apiPath, String method, long responseTime, int statusCode, 
                        String clientIp, String userAgent) {
            this.apiPath = apiPath;
            this.method = method;
            this.responseTime = responseTime;
            this.statusCode = statusCode;
            this.timestamp = LocalDateTime.now();
            this.clientIp = clientIp;
            this.userAgent = userAgent;
        }
        
        // Getters
        public String getApiPath() { return apiPath; }
        public String getMethod() { return method; }
        public long getResponseTime() { return responseTime; }
        public int getStatusCode() { return statusCode; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public String getClientIp() { return clientIp; }
        public String getUserAgent() { return userAgent; }
    }
    
    /**
     * 记录API调用指标
     * 
     * @param apiPath API路径
     * @param method HTTP方法
     * @param responseTime 响应时间（毫秒）
     * @param statusCode HTTP状态码
     * @param clientIp 客户端IP
     * @param userAgent 用户代理
     */
    public void recordApiMetric(String apiPath, String method, long responseTime, 
                               int statusCode, String clientIp, String userAgent) {
        
        String apiKey = method + " " + apiPath;
        
        // 创建指标记录
        ApiMetric metric = new ApiMetric(apiPath, method, responseTime, statusCode, clientIp, userAgent);
        
        // 存储指标
        apiMetrics.computeIfAbsent(apiKey, k -> Collections.synchronizedList(new ArrayList<>()))
                  .add(metric);
        
        // 限制存储数量
        List<ApiMetric> metrics = apiMetrics.get(apiKey);
        if (metrics.size() > MAX_METRICS_PER_API) {
            metrics.remove(0);
        }
        
        // 更新计数器
        apiCallCounts.computeIfAbsent(apiKey, k -> new AtomicLong(0)).incrementAndGet();
        
        if (statusCode >= 400) {
            apiErrorCounts.computeIfAbsent(apiKey, k -> new AtomicLong(0)).incrementAndGet();
        }
        
        // 记录到性能优化服务
        performanceService.recordQueryExecution(apiKey, responseTime);
        
        // 性能告警检查
        checkPerformanceAlert(apiKey, responseTime, statusCode);
        
        logger.debug("记录API指标 - {}: {}ms, 状态码: {}", apiKey, responseTime, statusCode);
    }
    
    /**
     * 获取API监控统计
     * 
     * @return 监控统计数据
     */
    public Map<String, Object> getApiMonitoringStatistics() {
        logger.info("获取API监控统计数据");
        
        Map<String, Object> statistics = new HashMap<>();
        
        // 总体统计
        statistics.put("overallStats", getOverallStatistics());
        
        // 各API详细统计
        statistics.put("apiDetailStats", getApiDetailStatistics());
        
        // 性能分析
        statistics.put("performanceAnalysis", getPerformanceAnalysis());
        
        // 错误分析
        statistics.put("errorAnalysis", getErrorAnalysis());
        
        // 趋势分析
        statistics.put("trendAnalysis", getTrendAnalysis());
        
        return statistics;
    }
    
    /**
     * 获取实时监控数据
     * 
     * @return 实时监控数据
     */
    public Map<String, Object> getRealTimeMonitoringData() {
        Map<String, Object> realTimeData = new HashMap<>();
        
        // 当前时间
        realTimeData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        // 最近5分钟的统计
        Map<String, Object> recentStats = getRecentStatistics(5);
        realTimeData.put("recent5Minutes", recentStats);
        
        // 当前活跃API
        realTimeData.put("activeApis", getActiveApis());
        
        // 性能状态
        realTimeData.put("performanceStatus", getPerformanceStatus());
        
        return realTimeData;
    }
    
    /**
     * 获取API性能报告
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 性能报告
     */
    public Map<String, Object> getPerformanceReport(LocalDateTime startTime, LocalDateTime endTime) {
        logger.info("生成API性能报告，时间范围: {} - {}", startTime, endTime);
        
        Map<String, Object> report = new HashMap<>();
        
        // 报告基本信息
        report.put("reportPeriod", Map.of(
            "startTime", startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            "endTime", endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        ));
        
        // 筛选时间范围内的数据
        Map<String, List<ApiMetric>> filteredMetrics = filterMetricsByTimeRange(startTime, endTime);
        
        // 生成报告内容
        report.put("summary", generateReportSummary(filteredMetrics));
        report.put("apiPerformance", generateApiPerformanceReport(filteredMetrics));
        report.put("recommendations", generatePerformanceRecommendations(filteredMetrics));
        
        return report;
    }
    
    /**
     * 检查API健康状态
     * 
     * @return 健康状态检查结果
     */
    public Map<String, Object> checkApiHealth() {
        logger.info("检查API健康状态");
        
        Map<String, Object> healthCheck = new HashMap<>();
        List<Map<String, Object>> apiHealthList = new ArrayList<>();
        
        for (Map.Entry<String, List<ApiMetric>> entry : apiMetrics.entrySet()) {
            String apiKey = entry.getKey();
            List<ApiMetric> metrics = entry.getValue();
            
            if (metrics.isEmpty()) {
                continue;
            }
            
            Map<String, Object> apiHealth = new HashMap<>();
            apiHealth.put("api", apiKey);
            
            // 最近10次调用的平均响应时间
            List<ApiMetric> recentMetrics = metrics.subList(
                Math.max(0, metrics.size() - 10), metrics.size());
            
            double avgResponseTime = recentMetrics.stream()
                    .mapToLong(ApiMetric::getResponseTime)
                    .average()
                    .orElse(0);
            
            // 错误率
            long errorCount = recentMetrics.stream()
                    .mapToInt(ApiMetric::getStatusCode)
                    .filter(code -> code >= 400)
                    .count();
            
            double errorRate = (double) errorCount / recentMetrics.size() * 100;
            
            // 健康状态评估
            String healthStatus = evaluateHealthStatus(avgResponseTime, errorRate);
            
            apiHealth.put("averageResponseTime", Math.round(avgResponseTime));
            apiHealth.put("errorRate", Math.round(errorRate * 100) / 100.0);
            apiHealth.put("healthStatus", healthStatus);
            apiHealth.put("lastCallTime", recentMetrics.get(recentMetrics.size() - 1).getTimestamp());
            
            apiHealthList.add(apiHealth);
        }
        
        // 按健康状态排序
        apiHealthList.sort((a, b) -> {
            String statusA = (String) a.get("healthStatus");
            String statusB = (String) b.get("healthStatus");
            return getHealthStatusPriority(statusA) - getHealthStatusPriority(statusB);
        });
        
        healthCheck.put("apiHealthList", apiHealthList);
        healthCheck.put("overallHealth", calculateOverallHealth(apiHealthList));
        
        return healthCheck;
    }
    
    /**
     * 清理过期的监控数据
     */
    public void cleanupExpiredMetrics() {
        logger.info("开始清理过期的监控数据");
        
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24); // 保留24小时数据
        int cleanedCount = 0;
        
        for (List<ApiMetric> metrics : apiMetrics.values()) {
            Iterator<ApiMetric> iterator = metrics.iterator();
            while (iterator.hasNext()) {
                ApiMetric metric = iterator.next();
                if (metric.getTimestamp().isBefore(cutoffTime)) {
                    iterator.remove();
                    cleanedCount++;
                }
            }
        }
        
        logger.info("清理过期监控数据完成，清理了{}条记录", cleanedCount);
    }
    
    // 私有方法
    
    private void checkPerformanceAlert(String apiKey, long responseTime, int statusCode) {
        // 检查响应时间告警
        if (responseTime > POOR_THRESHOLD) {
            logger.warn("API性能告警 - {}: 响应时间{}ms超过阈值{}ms", apiKey, responseTime, POOR_THRESHOLD);
        } else if (responseTime > ACCEPTABLE_THRESHOLD) {
            logger.info("API性能警告 - {}: 响应时间{}ms", apiKey, responseTime);
        }
        
        // 检查错误状态码告警
        if (statusCode >= 500) {
            logger.error("API错误告警 - {}: 服务器错误，状态码{}", apiKey, statusCode);
        } else if (statusCode >= 400) {
            logger.warn("API客户端错误 - {}: 状态码{}", apiKey, statusCode);
        }
    }
    
    private Map<String, Object> getOverallStatistics() {
        Map<String, Object> overallStats = new HashMap<>();
        
        long totalCalls = apiCallCounts.values().stream().mapToLong(AtomicLong::get).sum();
        long totalErrors = apiErrorCounts.values().stream().mapToLong(AtomicLong::get).sum();
        
        overallStats.put("totalApiCalls", totalCalls);
        overallStats.put("totalErrors", totalErrors);
        overallStats.put("errorRate", totalCalls > 0 ? (double) totalErrors / totalCalls * 100 : 0);
        overallStats.put("successRate", totalCalls > 0 ? (double) (totalCalls - totalErrors) / totalCalls * 100 : 100);
        
        // 计算总体平均响应时间
        double overallAvgResponseTime = apiMetrics.values().stream()
                .flatMap(List::stream)
                .mapToLong(ApiMetric::getResponseTime)
                .average()
                .orElse(0);
        
        overallStats.put("averageResponseTime", Math.round(overallAvgResponseTime));
        
        return overallStats;
    }
    
    private Map<String, Object> getApiDetailStatistics() {
        Map<String, Object> apiDetailStats = new HashMap<>();
        
        for (Map.Entry<String, List<ApiMetric>> entry : apiMetrics.entrySet()) {
            String apiKey = entry.getKey();
            List<ApiMetric> metrics = entry.getValue();
            
            if (metrics.isEmpty()) {
                continue;
            }
            
            Map<String, Object> apiStats = new HashMap<>();
            
            // 基本统计
            apiStats.put("callCount", apiCallCounts.getOrDefault(apiKey, new AtomicLong(0)).get());
            apiStats.put("errorCount", apiErrorCounts.getOrDefault(apiKey, new AtomicLong(0)).get());
            
            // 响应时间统计
            List<Long> responseTimes = metrics.stream()
                    .map(ApiMetric::getResponseTime)
                    .sorted()
                    .toList();
            
            if (!responseTimes.isEmpty()) {
                apiStats.put("averageResponseTime", responseTimes.stream().mapToLong(Long::longValue).average().orElse(0));
                apiStats.put("minResponseTime", responseTimes.get(0));
                apiStats.put("maxResponseTime", responseTimes.get(responseTimes.size() - 1));
                apiStats.put("p50ResponseTime", getPercentile(responseTimes, 50));
                apiStats.put("p95ResponseTime", getPercentile(responseTimes, 95));
                apiStats.put("p99ResponseTime", getPercentile(responseTimes, 99));
            }
            
            apiDetailStats.put(apiKey, apiStats);
        }
        
        return apiDetailStats;
    }
    
    private Map<String, Object> getPerformanceAnalysis() {
        Map<String, Object> performanceAnalysis = new HashMap<>();
        
        // 性能等级分布
        Map<String, Integer> performanceGrades = new HashMap<>();
        performanceGrades.put("excellent", 0);
        performanceGrades.put("good", 0);
        performanceGrades.put("acceptable", 0);
        performanceGrades.put("poor", 0);
        
        for (List<ApiMetric> metrics : apiMetrics.values()) {
            for (ApiMetric metric : metrics) {
                long responseTime = metric.getResponseTime();
                if (responseTime <= EXCELLENT_THRESHOLD) {
                    performanceGrades.put("excellent", performanceGrades.get("excellent") + 1);
                } else if (responseTime <= GOOD_THRESHOLD) {
                    performanceGrades.put("good", performanceGrades.get("good") + 1);
                } else if (responseTime <= ACCEPTABLE_THRESHOLD) {
                    performanceGrades.put("acceptable", performanceGrades.get("acceptable") + 1);
                } else {
                    performanceGrades.put("poor", performanceGrades.get("poor") + 1);
                }
            }
        }
        
        performanceAnalysis.put("performanceGrades", performanceGrades);
        
        return performanceAnalysis;
    }
    
    private Map<String, Object> getErrorAnalysis() {
        Map<String, Object> errorAnalysis = new HashMap<>();
        
        // 错误状态码分布
        Map<String, Integer> statusCodeDistribution = new HashMap<>();
        
        for (List<ApiMetric> metrics : apiMetrics.values()) {
            for (ApiMetric metric : metrics) {
                String statusCodeRange = getStatusCodeRange(metric.getStatusCode());
                statusCodeDistribution.put(statusCodeRange, 
                    statusCodeDistribution.getOrDefault(statusCodeRange, 0) + 1);
            }
        }
        
        errorAnalysis.put("statusCodeDistribution", statusCodeDistribution);
        
        return errorAnalysis;
    }
    
    private Map<String, Object> getTrendAnalysis() {
        Map<String, Object> trendAnalysis = new HashMap<>();
        
        // 最近24小时的趋势（按小时分组）
        Map<String, List<Long>> hourlyResponseTimes = new HashMap<>();
        
        LocalDateTime now = LocalDateTime.now();
        for (int i = 23; i >= 0; i--) {
            LocalDateTime hour = now.minusHours(i);
            String hourKey = hour.format(DateTimeFormatter.ofPattern("HH:00"));
            hourlyResponseTimes.put(hourKey, new ArrayList<>());
        }
        
        // 收集数据
        for (List<ApiMetric> metrics : apiMetrics.values()) {
            for (ApiMetric metric : metrics) {
                if (metric.getTimestamp().isAfter(now.minusHours(24))) {
                    String hourKey = metric.getTimestamp().format(DateTimeFormatter.ofPattern("HH:00"));
                    List<Long> times = hourlyResponseTimes.get(hourKey);
                    if (times != null) {
                        times.add(metric.getResponseTime());
                    }
                }
            }
        }
        
        // 计算每小时平均响应时间
        Map<String, Double> hourlyAverages = new HashMap<>();
        for (Map.Entry<String, List<Long>> entry : hourlyResponseTimes.entrySet()) {
            List<Long> times = entry.getValue();
            double average = times.isEmpty() ? 0 : times.stream().mapToLong(Long::longValue).average().orElse(0);
            hourlyAverages.put(entry.getKey(), average);
        }
        
        trendAnalysis.put("hourlyAverageResponseTime", hourlyAverages);
        
        return trendAnalysis;
    }
    
    private Map<String, Object> getRecentStatistics(int minutes) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(minutes);
        
        List<ApiMetric> recentMetrics = apiMetrics.values().stream()
                .flatMap(List::stream)
                .filter(metric -> metric.getTimestamp().isAfter(cutoffTime))
                .toList();
        
        Map<String, Object> recentStats = new HashMap<>();
        recentStats.put("totalCalls", recentMetrics.size());
        
        if (!recentMetrics.isEmpty()) {
            double avgResponseTime = recentMetrics.stream()
                    .mapToLong(ApiMetric::getResponseTime)
                    .average()
                    .orElse(0);
            
            long errorCount = recentMetrics.stream()
                    .mapToInt(ApiMetric::getStatusCode)
                    .filter(code -> code >= 400)
                    .count();
            
            recentStats.put("averageResponseTime", Math.round(avgResponseTime));
            recentStats.put("errorCount", errorCount);
            recentStats.put("errorRate", (double) errorCount / recentMetrics.size() * 100);
        }
        
        return recentStats;
    }
    
    private List<String> getActiveApis() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(5);
        
        return apiMetrics.entrySet().stream()
                .filter(entry -> entry.getValue().stream()
                        .anyMatch(metric -> metric.getTimestamp().isAfter(cutoffTime)))
                .map(Map.Entry::getKey)
                .sorted()
                .toList();
    }
    
    private String getPerformanceStatus() {
        Map<String, Object> recentStats = getRecentStatistics(5);
        
        if (recentStats.containsKey("averageResponseTime")) {
            long avgTime = Math.round((Double) recentStats.get("averageResponseTime"));
            double errorRate = (Double) recentStats.getOrDefault("errorRate", 0.0);
            
            if (avgTime <= EXCELLENT_THRESHOLD && errorRate < 1.0) {
                return "优秀";
            } else if (avgTime <= GOOD_THRESHOLD && errorRate < 5.0) {
                return "良好";
            } else if (avgTime <= ACCEPTABLE_THRESHOLD && errorRate < 10.0) {
                return "可接受";
            } else {
                return "需要关注";
            }
        }
        
        return "无数据";
    }
    
    private long getPercentile(List<Long> sortedList, int percentile) {
        if (sortedList.isEmpty()) return 0;
        
        int index = (int) Math.ceil(sortedList.size() * percentile / 100.0) - 1;
        index = Math.max(0, Math.min(index, sortedList.size() - 1));
        
        return sortedList.get(index);
    }
    
    private String getStatusCodeRange(int statusCode) {
        if (statusCode < 300) return "2xx";
        else if (statusCode < 400) return "3xx";
        else if (statusCode < 500) return "4xx";
        else return "5xx";
    }
    
    private Map<String, List<ApiMetric>> filterMetricsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        Map<String, List<ApiMetric>> filteredMetrics = new HashMap<>();
        
        for (Map.Entry<String, List<ApiMetric>> entry : apiMetrics.entrySet()) {
            List<ApiMetric> filtered = entry.getValue().stream()
                    .filter(metric -> !metric.getTimestamp().isBefore(startTime) && 
                                     !metric.getTimestamp().isAfter(endTime))
                    .toList();
            
            if (!filtered.isEmpty()) {
                filteredMetrics.put(entry.getKey(), filtered);
            }
        }
        
        return filteredMetrics;
    }
    
    private Map<String, Object> generateReportSummary(Map<String, List<ApiMetric>> filteredMetrics) {
        Map<String, Object> summary = new HashMap<>();
        
        int totalCalls = filteredMetrics.values().stream().mapToInt(List::size).sum();
        summary.put("totalCalls", totalCalls);
        
        if (totalCalls > 0) {
            double avgResponseTime = filteredMetrics.values().stream()
                    .flatMap(List::stream)
                    .mapToLong(ApiMetric::getResponseTime)
                    .average()
                    .orElse(0);
            
            long errorCount = filteredMetrics.values().stream()
                    .flatMap(List::stream)
                    .mapToInt(ApiMetric::getStatusCode)
                    .filter(code -> code >= 400)
                    .count();
            
            summary.put("averageResponseTime", Math.round(avgResponseTime));
            summary.put("errorCount", errorCount);
            summary.put("errorRate", (double) errorCount / totalCalls * 100);
        }
        
        return summary;
    }
    
    private Map<String, Object> generateApiPerformanceReport(Map<String, List<ApiMetric>> filteredMetrics) {
        Map<String, Object> apiPerformance = new HashMap<>();
        
        for (Map.Entry<String, List<ApiMetric>> entry : filteredMetrics.entrySet()) {
            String apiKey = entry.getKey();
            List<ApiMetric> metrics = entry.getValue();
            
            Map<String, Object> apiReport = new HashMap<>();
            
            List<Long> responseTimes = metrics.stream()
                    .map(ApiMetric::getResponseTime)
                    .sorted()
                    .toList();
            
            apiReport.put("callCount", metrics.size());
            apiReport.put("averageResponseTime", responseTimes.stream().mapToLong(Long::longValue).average().orElse(0));
            apiReport.put("p95ResponseTime", getPercentile(responseTimes, 95));
            
            long errorCount = metrics.stream()
                    .mapToInt(ApiMetric::getStatusCode)
                    .filter(code -> code >= 400)
                    .count();
            
            apiReport.put("errorCount", errorCount);
            apiReport.put("errorRate", (double) errorCount / metrics.size() * 100);
            
            apiPerformance.put(apiKey, apiReport);
        }
        
        return apiPerformance;
    }
    
    private List<String> generatePerformanceRecommendations(Map<String, List<ApiMetric>> filteredMetrics) {
        List<String> recommendations = new ArrayList<>();
        
        // 分析性能问题并生成建议
        for (Map.Entry<String, List<ApiMetric>> entry : filteredMetrics.entrySet()) {
            String apiKey = entry.getKey();
            List<ApiMetric> metrics = entry.getValue();
            
            double avgResponseTime = metrics.stream()
                    .mapToLong(ApiMetric::getResponseTime)
                    .average()
                    .orElse(0);
            
            if (avgResponseTime > ACCEPTABLE_THRESHOLD) {
                recommendations.add(apiKey + " 平均响应时间过长，建议优化查询或添加索引");
            }
            
            long errorCount = metrics.stream()
                    .mapToInt(ApiMetric::getStatusCode)
                    .filter(code -> code >= 400)
                    .count();
            
            double errorRate = (double) errorCount / metrics.size() * 100;
            if (errorRate > 5.0) {
                recommendations.add(apiKey + " 错误率偏高，建议检查业务逻辑和错误处理");
            }
        }
        
        if (recommendations.isEmpty()) {
            recommendations.add("API性能表现良好，建议继续监控");
        }
        
        return recommendations;
    }
    
    private String evaluateHealthStatus(double avgResponseTime, double errorRate) {
        if (avgResponseTime <= EXCELLENT_THRESHOLD && errorRate < 1.0) {
            return "健康";
        } else if (avgResponseTime <= GOOD_THRESHOLD && errorRate < 5.0) {
            return "良好";
        } else if (avgResponseTime <= ACCEPTABLE_THRESHOLD && errorRate < 10.0) {
            return "警告";
        } else {
            return "异常";
        }
    }
    
    private int getHealthStatusPriority(String status) {
        switch (status) {
            case "异常": return 1;
            case "警告": return 2;
            case "良好": return 3;
            case "健康": return 4;
            default: return 5;
        }
    }
    
    private String calculateOverallHealth(List<Map<String, Object>> apiHealthList) {
        if (apiHealthList.isEmpty()) {
            return "无数据";
        }
        
        long abnormalCount = apiHealthList.stream()
                .mapToInt(api -> "异常".equals(api.get("healthStatus")) ? 1 : 0)
                .sum();
        
        long warningCount = apiHealthList.stream()
                .mapToInt(api -> "警告".equals(api.get("healthStatus")) ? 1 : 0)
                .sum();
        
        if (abnormalCount > 0) {
            return "异常";
        } else if (warningCount > apiHealthList.size() / 2) {
            return "警告";
        } else if (warningCount > 0) {
            return "良好";
        } else {
            return "健康";
        }
    }
}