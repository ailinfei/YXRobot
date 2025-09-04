package com.yxrobot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 设备搜索性能监控服务
 * 监控搜索性能指标，提供性能分析和优化建议
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-28
 */
@Service
public class ManagedDeviceSearchPerformanceService {
    
    private static final Logger logger = LoggerFactory.getLogger(ManagedDeviceSearchPerformanceService.class);
    
    // 搜索性能统计
    private final Map<String, AtomicLong> searchCounters = new ConcurrentHashMap<>();
    private final Map<String, List<Long>> searchExecutionTimes = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> searchErrors = new ConcurrentHashMap<>();
    
    // 搜索关键词统计
    private final Map<String, AtomicLong> keywordFrequency = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> lastSearchTime = new ConcurrentHashMap<>();
    
    // 性能阈值配置
    private static final long SLOW_QUERY_THRESHOLD = 1000; // 1秒
    private static final long WARNING_QUERY_THRESHOLD = 500; // 0.5秒
    private static final int MAX_EXECUTION_TIME_RECORDS = 1000; // 最大记录数
    
    /**
     * 记录搜索执行
     * 
     * @param searchType 搜索类型
     * @param keyword 搜索关键词
     * @param executionTime 执行时间（毫秒）
     * @param resultCount 结果数量
     * @param success 是否成功
     */
    public void recordSearchExecution(String searchType, String keyword, long executionTime, 
                                    int resultCount, boolean success) {
        
        // 记录搜索次数
        searchCounters.computeIfAbsent(searchType, k -> new AtomicLong(0)).incrementAndGet();
        
        // 记录执行时间
        searchExecutionTimes.computeIfAbsent(searchType, k -> Collections.synchronizedList(new ArrayList<>()))
                .add(executionTime);
        
        // 限制记录数量，避免内存溢出
        List<Long> times = searchExecutionTimes.get(searchType);
        if (times.size() > MAX_EXECUTION_TIME_RECORDS) {
            times.remove(0);
        }
        
        // 记录关键词频率
        if (keyword != null && !keyword.trim().isEmpty()) {
            keywordFrequency.computeIfAbsent(keyword.toLowerCase(), k -> new AtomicLong(0)).incrementAndGet();
            lastSearchTime.put(keyword.toLowerCase(), LocalDateTime.now());
        }
        
        // 记录错误
        if (!success) {
            searchErrors.computeIfAbsent(searchType, k -> new AtomicLong(0)).incrementAndGet();
        }
        
        // 性能告警
        if (executionTime > SLOW_QUERY_THRESHOLD) {
            logger.warn("慢查询告警 - 搜索类型: {}, 关键词: {}, 执行时间: {}ms, 结果数量: {}", 
                       searchType, keyword, executionTime, resultCount);
        } else if (executionTime > WARNING_QUERY_THRESHOLD) {
            logger.info("查询性能警告 - 搜索类型: {}, 关键词: {}, 执行时间: {}ms, 结果数量: {}", 
                       searchType, keyword, executionTime, resultCount);
        }
        
        logger.debug("搜索执行记录 - 类型: {}, 关键词: {}, 时间: {}ms, 结果: {}, 成功: {}", 
                    searchType, keyword, executionTime, resultCount, success);
    }
    
    /**
     * 获取搜索性能统计
     * 
     * @return 性能统计信息
     */
    public Map<String, Object> getSearchPerformanceStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 总体统计
        Map<String, Object> overallStats = new HashMap<>();
        long totalSearches = searchCounters.values().stream().mapToLong(AtomicLong::get).sum();
        long totalErrors = searchErrors.values().stream().mapToLong(AtomicLong::get).sum();
        
        overallStats.put("totalSearches", totalSearches);
        overallStats.put("totalErrors", totalErrors);
        overallStats.put("errorRate", totalSearches > 0 ? (double) totalErrors / totalSearches * 100 : 0);
        overallStats.put("successRate", totalSearches > 0 ? (double) (totalSearches - totalErrors) / totalSearches * 100 : 100);
        
        stats.put("overall", overallStats);
        
        // 按搜索类型统计
        Map<String, Object> typeStats = new HashMap<>();
        for (Map.Entry<String, AtomicLong> entry : searchCounters.entrySet()) {
            String searchType = entry.getKey();
            long count = entry.getValue().get();
            long errors = searchErrors.getOrDefault(searchType, new AtomicLong(0)).get();
            
            Map<String, Object> typeStat = new HashMap<>();
            typeStat.put("count", count);
            typeStat.put("errors", errors);
            typeStat.put("errorRate", count > 0 ? (double) errors / count * 100 : 0);
            
            // 执行时间统计
            List<Long> times = searchExecutionTimes.get(searchType);
            if (times != null && !times.isEmpty()) {
                typeStat.put("averageTime", times.stream().mapToLong(Long::longValue).average().orElse(0));
                typeStat.put("maxTime", times.stream().mapToLong(Long::longValue).max().orElse(0));
                typeStat.put("minTime", times.stream().mapToLong(Long::longValue).min().orElse(0));
                
                long slowQueries = times.stream().mapToLong(Long::longValue).filter(t -> t > SLOW_QUERY_THRESHOLD).count();
                typeStat.put("slowQueries", slowQueries);
                typeStat.put("slowQueryRate", (double) slowQueries / times.size() * 100);
            }
            
            typeStats.put(searchType, typeStat);
        }
        
        stats.put("byType", typeStats);
        
        // 热门搜索关键词
        List<Map<String, Object>> popularKeywords = getPopularKeywords(10);
        stats.put("popularKeywords", popularKeywords);
        
        // 性能趋势
        stats.put("performanceTrend", getPerformanceTrend());
        
        return stats;
    }
    
    /**
     * 获取热门搜索关键词
     * 
     * @param limit 数量限制
     * @return 热门关键词列表
     */
    public List<Map<String, Object>> getPopularKeywords(int limit) {
        return keywordFrequency.entrySet().stream()
                .sorted(Map.Entry.<String, AtomicLong>comparingByValue((a, b) -> Long.compare(b.get(), a.get())))
                .limit(limit)
                .map(entry -> {
                    Map<String, Object> keyword = new HashMap<>();
                    keyword.put("keyword", entry.getKey());
                    keyword.put("count", entry.getValue().get());
                    keyword.put("lastSearchTime", lastSearchTime.get(entry.getKey()));
                    return keyword;
                })
                .collect(ArrayList::new, (list, item) -> list.add(item), ArrayList::addAll);
    }
    
    /**
     * 获取性能趋势
     * 
     * @return 性能趋势数据
     */
    public Map<String, Object> getPerformanceTrend() {
        Map<String, Object> trend = new HashMap<>();
        
        // 计算最近的平均响应时间
        Map<String, Double> recentAverageTime = new HashMap<>();
        for (Map.Entry<String, List<Long>> entry : searchExecutionTimes.entrySet()) {
            List<Long> times = entry.getValue();
            if (!times.isEmpty()) {
                // 取最近100次查询的平均时间
                int recentCount = Math.min(100, times.size());
                double recentAverage = times.subList(times.size() - recentCount, times.size())
                        .stream().mapToLong(Long::longValue).average().orElse(0);
                recentAverageTime.put(entry.getKey(), recentAverage);
            }
        }
        
        trend.put("recentAverageTime", recentAverageTime);
        
        // 性能等级评估
        Map<String, String> performanceGrade = new HashMap<>();
        for (Map.Entry<String, Double> entry : recentAverageTime.entrySet()) {
            double avgTime = entry.getValue();
            String grade;
            if (avgTime < 100) {
                grade = "优秀";
            } else if (avgTime < 300) {
                grade = "良好";
            } else if (avgTime < 500) {
                grade = "一般";
            } else if (avgTime < 1000) {
                grade = "较差";
            } else {
                grade = "很差";
            }
            performanceGrade.put(entry.getKey(), grade);
        }
        
        trend.put("performanceGrade", performanceGrade);
        
        return trend;
    }
    
    /**
     * 获取性能优化建议
     * 
     * @return 优化建议列表
     */
    public List<String> getPerformanceOptimizationSuggestions() {
        List<String> suggestions = new ArrayList<>();
        
        // 分析慢查询
        for (Map.Entry<String, List<Long>> entry : searchExecutionTimes.entrySet()) {
            String searchType = entry.getKey();
            List<Long> times = entry.getValue();
            
            if (!times.isEmpty()) {
                double averageTime = times.stream().mapToLong(Long::longValue).average().orElse(0);
                long slowQueries = times.stream().mapToLong(Long::longValue).filter(t -> t > SLOW_QUERY_THRESHOLD).count();
                double slowQueryRate = (double) slowQueries / times.size() * 100;
                
                if (averageTime > SLOW_QUERY_THRESHOLD) {
                    suggestions.add(String.format("搜索类型 '%s' 平均响应时间过长(%.0fms)，建议优化索引或查询逻辑", 
                                                 searchType, averageTime));
                }
                
                if (slowQueryRate > 10) {
                    suggestions.add(String.format("搜索类型 '%s' 慢查询比例过高(%.1f%%)，建议检查数据库性能", 
                                                 searchType, slowQueryRate));
                }
            }
        }
        
        // 分析错误率
        for (Map.Entry<String, AtomicLong> entry : searchErrors.entrySet()) {
            String searchType = entry.getKey();
            long errors = entry.getValue().get();
            long total = searchCounters.getOrDefault(searchType, new AtomicLong(0)).get();
            
            if (total > 0) {
                double errorRate = (double) errors / total * 100;
                if (errorRate > 5) {
                    suggestions.add(String.format("搜索类型 '%s' 错误率过高(%.1f%%)，建议检查错误处理逻辑", 
                                                 searchType, errorRate));
                }
            }
        }
        
        // 通用优化建议
        if (suggestions.isEmpty()) {
            suggestions.add("当前搜索性能良好，建议继续监控并定期优化索引");
        }
        
        suggestions.add("建议定期清理无效数据，保持数据库性能");
        suggestions.add("建议根据搜索热点优化索引策略");
        
        return suggestions;
    }
    
    /**
     * 重置性能统计
     */
    public void resetPerformanceStats() {
        logger.info("重置搜索性能统计");
        
        searchCounters.clear();
        searchExecutionTimes.clear();
        searchErrors.clear();
        keywordFrequency.clear();
        lastSearchTime.clear();
        
        logger.info("搜索性能统计已重置");
    }
    
    /**
     * 获取实时性能指标
     * 
     * @return 实时性能指标
     */
    public Map<String, Object> getRealTimePerformanceMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // 当前时间
        metrics.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        // 最近1分钟的搜索次数
        long recentSearches = searchCounters.values().stream().mapToLong(AtomicLong::get).sum();
        metrics.put("recentSearches", recentSearches);
        
        // 平均响应时间
        double overallAverageTime = searchExecutionTimes.values().stream()
                .flatMap(List::stream)
                .mapToLong(Long::longValue)
                .average()
                .orElse(0);
        metrics.put("averageResponseTime", Math.round(overallAverageTime));
        
        // 当前错误率
        long totalErrors = searchErrors.values().stream().mapToLong(AtomicLong::get).sum();
        long totalSearches = searchCounters.values().stream().mapToLong(AtomicLong::get).sum();
        double currentErrorRate = totalSearches > 0 ? (double) totalErrors / totalSearches * 100 : 0;
        metrics.put("currentErrorRate", Math.round(currentErrorRate * 100) / 100.0);
        
        // 性能状态
        String performanceStatus;
        if (overallAverageTime < WARNING_QUERY_THRESHOLD && currentErrorRate < 1) {
            performanceStatus = "优秀";
        } else if (overallAverageTime < SLOW_QUERY_THRESHOLD && currentErrorRate < 5) {
            performanceStatus = "良好";
        } else {
            performanceStatus = "需要优化";
        }
        metrics.put("performanceStatus", performanceStatus);
        
        return metrics;
    }
    
    /**
     * 导出性能报告
     * 
     * @return 性能报告
     */
    public Map<String, Object> exportPerformanceReport() {
        Map<String, Object> report = new HashMap<>();
        
        // 报告基本信息
        report.put("reportTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        report.put("reportType", "设备搜索性能报告");
        
        // 性能统计
        report.put("performanceStats", getSearchPerformanceStats());
        
        // 优化建议
        report.put("optimizationSuggestions", getPerformanceOptimizationSuggestions());
        
        // 实时指标
        report.put("realTimeMetrics", getRealTimePerformanceMetrics());
        
        logger.info("性能报告导出完成");
        return report;
    }
}