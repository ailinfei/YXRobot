package com.yxrobot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

/**
 * 设备管理API性能测试服务
 * 测试API接口的性能指标和响应时间
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-28
 */
@Service
public class ManagedDeviceApiPerformanceTestService {
    
    private static final Logger logger = LoggerFactory.getLogger(ManagedDeviceApiPerformanceTestService.class);
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    private final ExecutorService executorService = Executors.newFixedThreadPool(20);
    
    // 性能测试配置
    private static final int DEFAULT_CONCURRENT_USERS = 10;
    private static final int DEFAULT_TEST_DURATION_SECONDS = 30;
    private static final int DEFAULT_REQUESTS_PER_USER = 100;
    
    // 性能阈值
    private static final long ACCEPTABLE_RESPONSE_TIME = 2000; // 2秒
    private static final long GOOD_RESPONSE_TIME = 1000; // 1秒
    private static final long EXCELLENT_RESPONSE_TIME = 500; // 0.5秒
    
    /**
     * 执行完整的API性能测试
     * 
     * @return 性能测试结果
     */
    public Map<String, Object> executeFullPerformanceTest() {
        logger.info("开始执行设备管理API性能测试");
        
        Map<String, Object> testResults = new HashMap<>();
        
        try {
            // 1. 单接口响应时间测试
            testResults.put("singleApiResponseTime", testSingleApiResponseTime());
            
            // 2. 并发性能测试
            testResults.put("concurrencyTest", testConcurrencyPerformance());
            
            // 3. 负载测试
            testResults.put("loadTest", testLoadPerformance());
            
            // 4. 压力测试
            testResults.put("stressTest", testStressPerformance());
            
            // 5. 大数据量测试
            testResults.put("largeDataTest", testLargeDataPerformance());
            
            // 6. 搜索性能测试
            testResults.put("searchPerformanceTest", testSearchPerformance());
            
            // 生成性能报告
            testResults.put("performanceReport", generatePerformanceReport(testResults));
            
        } catch (Exception e) {
            logger.error("API性能测试执行失败", e);
            testResults.put("error", e.getMessage());
        }
        
        logger.info("设备管理API性能测试完成");
        return testResults;
    }
    
    /**
     * 测试单接口响应时间
     */
    private Map<String, Object> testSingleApiResponseTime() {
        logger.info("开始测试单接口响应时间");
        
        Map<String, Object> results = new HashMap<>();
        
        // 测试各个API接口
        results.put("deviceListApi", measureApiResponseTime("/api/admin/devices?page=1&pageSize=10", 10));
        results.put("deviceStatsApi", measureApiResponseTime("/api/admin/devices/stats", 10));
        results.put("searchApi", measureApiResponseTime("/api/admin/devices/search/quick?keyword=test", 10));
        results.put("customerOptionsApi", measureApiResponseTime("/api/admin/customers/options", 10));
        
        return results;
    }
    
    /**
     * 测试并发性能
     */
    private Map<String, Object> testConcurrencyPerformance() {
        logger.info("开始测试并发性能");
        
        Map<String, Object> results = new HashMap<>();
        
        // 测试不同并发用户数
        results.put("concurrency5", testConcurrentRequests("/api/admin/devices?page=1&pageSize=10", 5, 20));
        results.put("concurrency10", testConcurrentRequests("/api/admin/devices?page=1&pageSize=10", 10, 20));
        results.put("concurrency20", testConcurrentRequests("/api/admin/devices?page=1&pageSize=10", 20, 20));
        results.put("concurrency50", testConcurrentRequests("/api/admin/devices?page=1&pageSize=10", 50, 20));
        
        return results;
    }
    
    /**
     * 测试负载性能
     */
    private Map<String, Object> testLoadPerformance() {
        logger.info("开始测试负载性能");
        
        Map<String, Object> results = new HashMap<>();
        
        // 持续负载测试
        results.put("sustainedLoad", testSustainedLoad("/api/admin/devices?page=1&pageSize=10", 
                                                      DEFAULT_CONCURRENT_USERS, 
                                                      DEFAULT_TEST_DURATION_SECONDS));
        
        // 峰值负载测试
        results.put("peakLoad", testPeakLoad("/api/admin/devices?page=1&pageSize=10", 
                                           DEFAULT_CONCURRENT_USERS * 2, 
                                           10));
        
        return results;
    }
    
    /**
     * 测试压力性能
     */
    private Map<String, Object> testStressPerformance() {
        logger.info("开始测试压力性能");
        
        Map<String, Object> results = new HashMap<>();
        
        // 逐步增加负载
        results.put("gradualStress", testGradualStress("/api/admin/devices?page=1&pageSize=10"));
        
        // 突发压力测试
        results.put("burstStress", testBurstStress("/api/admin/devices?page=1&pageSize=10", 100, 5));
        
        return results;
    }
    
    /**
     * 测试大数据量性能
     */
    private Map<String, Object> testLargeDataPerformance() {
        logger.info("开始测试大数据量性能");
        
        Map<String, Object> results = new HashMap<>();
        
        // 测试不同页大小的性能
        results.put("pageSize10", measureApiResponseTime("/api/admin/devices?page=1&pageSize=10", 5));
        results.put("pageSize50", measureApiResponseTime("/api/admin/devices?page=1&pageSize=50", 5));
        results.put("pageSize100", measureApiResponseTime("/api/admin/devices?page=1&pageSize=100", 5));
        results.put("pageSize500", measureApiResponseTime("/api/admin/devices?page=1&pageSize=500", 5));
        results.put("pageSize1000", measureApiResponseTime("/api/admin/devices?page=1&pageSize=1000", 5));
        
        return results;
    }
    
    /**
     * 测试搜索性能
     */
    private Map<String, Object> testSearchPerformance() {
        logger.info("开始测试搜索性能");
        
        Map<String, Object> results = new HashMap<>();
        
        // 测试不同搜索场景
        results.put("simpleSearch", measureApiResponseTime("/api/admin/devices/search/quick?keyword=test", 10));
        results.put("complexSearch", measureApiResponseTime("/api/admin/devices?keyword=test&status=online&model=education", 10));
        results.put("emptySearch", measureApiResponseTime("/api/admin/devices/search/quick?keyword=nonexistent", 10));
        results.put("wildcardSearch", measureApiResponseTime("/api/admin/devices/search/quick?keyword=*", 10));
        
        return results;
    }
    
    /**
     * 测量API响应时间
     */
    private Map<String, Object> measureApiResponseTime(String url, int iterations) {
        List<Long> responseTimes = new ArrayList<>();
        int successCount = 0;
        int errorCount = 0;
        
        for (int i = 0; i < iterations; i++) {
            try {
                long startTime = System.currentTimeMillis();
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                long endTime = System.currentTimeMillis();
                
                long responseTime = endTime - startTime;
                responseTimes.add(responseTime);
                
                if (response.getStatusCode().is2xxSuccessful()) {
                    successCount++;
                } else {
                    errorCount++;
                }
                
            } catch (Exception e) {
                errorCount++;
                logger.warn("API请求失败: {}", e.getMessage());
            }
        }
        
        return calculateResponseTimeStats(responseTimes, successCount, errorCount);
    }
    
    /**
     * 测试并发请求
     */
    private Map<String, Object> testConcurrentRequests(String url, int concurrentUsers, int requestsPerUser) {
        logger.info("测试并发请求: {} 用户, 每用户 {} 请求", concurrentUsers, requestsPerUser);
        
        CountDownLatch latch = new CountDownLatch(concurrentUsers);
        List<Future<List<Long>>> futures = new ArrayList<>();
        
        long testStartTime = System.currentTimeMillis();
        
        // 启动并发用户
        for (int i = 0; i < concurrentUsers; i++) {
            Future<List<Long>> future = executorService.submit(() -> {
                List<Long> userResponseTimes = new ArrayList<>();
                
                for (int j = 0; j < requestsPerUser; j++) {
                    try {
                        long startTime = System.currentTimeMillis();
                        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                        long endTime = System.currentTimeMillis();
                        
                        userResponseTimes.add(endTime - startTime);
                        
                    } catch (Exception e) {
                        // 记录错误但继续测试
                        userResponseTimes.add(-1L); // 用-1表示错误
                    }
                }
                
                latch.countDown();
                return userResponseTimes;
            });
            
            futures.add(future);
        }
        
        // 等待所有用户完成
        try {
            latch.await(60, TimeUnit.SECONDS); // 最多等待60秒
        } catch (InterruptedException e) {
            logger.error("并发测试被中断", e);
        }
        
        long testEndTime = System.currentTimeMillis();
        
        // 收集结果
        List<Long> allResponseTimes = new ArrayList<>();
        int totalRequests = 0;
        int successfulRequests = 0;
        int errorRequests = 0;
        
        for (Future<List<Long>> future : futures) {
            try {
                List<Long> userTimes = future.get(5, TimeUnit.SECONDS);
                for (Long time : userTimes) {
                    totalRequests++;
                    if (time > 0) {
                        allResponseTimes.add(time);
                        successfulRequests++;
                    } else {
                        errorRequests++;
                    }
                }
            } catch (Exception e) {
                logger.error("获取并发测试结果失败", e);
            }
        }
        
        Map<String, Object> result = calculateResponseTimeStats(allResponseTimes, successfulRequests, errorRequests);
        result.put("concurrentUsers", concurrentUsers);
        result.put("requestsPerUser", requestsPerUser);
        result.put("totalTestTime", testEndTime - testStartTime);
        result.put("throughput", calculateThroughput(totalRequests, testEndTime - testStartTime));
        
        return result;
    }
    
    /**
     * 测试持续负载
     */
    private Map<String, Object> testSustainedLoad(String url, int concurrentUsers, int durationSeconds) {
        logger.info("测试持续负载: {} 用户, 持续 {} 秒", concurrentUsers, durationSeconds);
        
        List<Long> responseTimes = Collections.synchronizedList(new ArrayList<>());
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        AtomicBoolean testRunning = new AtomicBoolean(true);
        
        long testStartTime = System.currentTimeMillis();
        
        // 启动并发用户
        List<Future<?>> futures = new ArrayList<>();
        for (int i = 0; i < concurrentUsers; i++) {
            Future<?> future = executorService.submit(() -> {
                while (testRunning.get()) {
                    try {
                        long startTime = System.currentTimeMillis();
                        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                        long endTime = System.currentTimeMillis();
                        
                        responseTimes.add(endTime - startTime);
                        
                        if (response.getStatusCode().is2xxSuccessful()) {
                            successCount.incrementAndGet();
                        } else {
                            errorCount.incrementAndGet();
                        }
                        
                        // 短暂休息避免过度压力
                        Thread.sleep(100);
                        
                    } catch (Exception e) {
                        errorCount.incrementAndGet();
                    }
                }
            });
            futures.add(future);
        }
        
        // 运行指定时间
        try {
            Thread.sleep(durationSeconds * 1000L);
        } catch (InterruptedException e) {
            logger.error("持续负载测试被中断", e);
        }
        
        // 停止测试
        testRunning.set(false);
        
        // 等待所有线程结束
        for (Future<?> future : futures) {
            try {
                future.get(5, TimeUnit.SECONDS);
            } catch (Exception e) {
                logger.error("等待持续负载测试线程结束失败", e);
            }
        }
        
        long testEndTime = System.currentTimeMillis();
        
        Map<String, Object> result = calculateResponseTimeStats(responseTimes, successCount.get(), errorCount.get());
        result.put("testDuration", testEndTime - testStartTime);
        result.put("throughput", calculateThroughput(successCount.get() + errorCount.get(), testEndTime - testStartTime));
        
        return result;
    }
    
    /**
     * 测试峰值负载
     */
    private Map<String, Object> testPeakLoad(String url, int peakUsers, int durationSeconds) {
        logger.info("测试峰值负载: {} 用户, 持续 {} 秒", peakUsers, durationSeconds);
        
        return testSustainedLoad(url, peakUsers, durationSeconds);
    }
    
    /**
     * 测试逐步增加压力
     */
    private Map<String, Object> testGradualStress(String url) {
        logger.info("测试逐步增加压力");
        
        Map<String, Object> results = new HashMap<>();
        
        // 逐步增加并发用户数
        int[] userCounts = {1, 5, 10, 20, 50, 100};
        
        for (int users : userCounts) {
            Map<String, Object> result = testConcurrentRequests(url, users, 10);
            results.put("users" + users, result);
            
            // 检查是否达到性能瓶颈
            if (result.containsKey("averageResponseTime")) {
                Long avgTime = (Long) result.get("averageResponseTime");
                if (avgTime > ACCEPTABLE_RESPONSE_TIME) {
                    logger.warn("在 {} 并发用户时达到性能瓶颈，平均响应时间: {}ms", users, avgTime);
                    results.put("performanceBottleneck", users);
                    break;
                }
            }
        }
        
        return results;
    }
    
    /**
     * 测试突发压力
     */
    private Map<String, Object> testBurstStress(String url, int burstUsers, int burstDurationSeconds) {
        logger.info("测试突发压力: {} 用户, 持续 {} 秒", burstUsers, burstDurationSeconds);
        
        return testSustainedLoad(url, burstUsers, burstDurationSeconds);
    }
    
    /**
     * 计算响应时间统计
     */
    private Map<String, Object> calculateResponseTimeStats(List<Long> responseTimes, int successCount, int errorCount) {
        Map<String, Object> stats = new HashMap<>();
        
        if (responseTimes.isEmpty()) {
            stats.put("averageResponseTime", 0L);
            stats.put("minResponseTime", 0L);
            stats.put("maxResponseTime", 0L);
            stats.put("medianResponseTime", 0L);
            stats.put("p95ResponseTime", 0L);
            stats.put("p99ResponseTime", 0L);
        } else {
            Collections.sort(responseTimes);
            
            long sum = responseTimes.stream().mapToLong(Long::longValue).sum();
            stats.put("averageResponseTime", sum / responseTimes.size());
            stats.put("minResponseTime", responseTimes.get(0));
            stats.put("maxResponseTime", responseTimes.get(responseTimes.size() - 1));
            stats.put("medianResponseTime", getPercentile(responseTimes, 50));
            stats.put("p95ResponseTime", getPercentile(responseTimes, 95));
            stats.put("p99ResponseTime", getPercentile(responseTimes, 99));
        }
        
        stats.put("totalRequests", successCount + errorCount);
        stats.put("successfulRequests", successCount);
        stats.put("errorRequests", errorCount);
        stats.put("successRate", calculateSuccessRate(successCount, errorCount));
        stats.put("performanceGrade", calculatePerformanceGrade(responseTimes));
        
        return stats;
    }
    
    /**
     * 获取百分位数
     */
    private long getPercentile(List<Long> sortedList, int percentile) {
        if (sortedList.isEmpty()) return 0;
        
        int index = (int) Math.ceil(sortedList.size() * percentile / 100.0) - 1;
        index = Math.max(0, Math.min(index, sortedList.size() - 1));
        
        return sortedList.get(index);
    }
    
    /**
     * 计算成功率
     */
    private double calculateSuccessRate(int successCount, int errorCount) {
        int total = successCount + errorCount;
        if (total == 0) return 0.0;
        return (double) successCount / total * 100;
    }
    
    /**
     * 计算吞吐量（请求/秒）
     */
    private double calculateThroughput(int totalRequests, long durationMs) {
        if (durationMs == 0) return 0.0;
        return (double) totalRequests / (durationMs / 1000.0);
    }
    
    /**
     * 计算性能等级
     */
    private String calculatePerformanceGrade(List<Long> responseTimes) {
        if (responseTimes.isEmpty()) return "N/A";
        
        long avgTime = responseTimes.stream().mapToLong(Long::longValue).sum() / responseTimes.size();
        
        if (avgTime <= EXCELLENT_RESPONSE_TIME) {
            return "优秀";
        } else if (avgTime <= GOOD_RESPONSE_TIME) {
            return "良好";
        } else if (avgTime <= ACCEPTABLE_RESPONSE_TIME) {
            return "可接受";
        } else {
            return "需要优化";
        }
    }
    
    /**
     * 生成性能报告
     */
    private Map<String, Object> generatePerformanceReport(Map<String, Object> testResults) {
        Map<String, Object> report = new HashMap<>();
        
        // 性能总结
        Map<String, Object> summary = new HashMap<>();
        summary.put("testDate", new Date());
        summary.put("testDuration", "完整性能测试");
        
        // 分析各项测试结果
        List<String> recommendations = new ArrayList<>();
        
        // 分析单接口性能
        if (testResults.containsKey("singleApiResponseTime")) {
            analyzeSingleApiPerformance((Map<String, Object>) testResults.get("singleApiResponseTime"), recommendations);
        }
        
        // 分析并发性能
        if (testResults.containsKey("concurrencyTest")) {
            analyzeConcurrencyPerformance((Map<String, Object>) testResults.get("concurrencyTest"), recommendations);
        }
        
        // 分析负载性能
        if (testResults.containsKey("loadTest")) {
            analyzeLoadPerformance((Map<String, Object>) testResults.get("loadTest"), recommendations);
        }
        
        summary.put("recommendations", recommendations);
        report.put("summary", summary);
        
        return report;
    }
    
    /**
     * 分析单接口性能
     */
    private void analyzeSingleApiPerformance(Map<String, Object> singleApiResults, List<String> recommendations) {
        for (Map.Entry<String, Object> entry : singleApiResults.entrySet()) {
            if (entry.getValue() instanceof Map) {
                Map<String, Object> apiResult = (Map<String, Object>) entry.getValue();
                if (apiResult.containsKey("averageResponseTime")) {
                    Long avgTime = (Long) apiResult.get("averageResponseTime");
                    if (avgTime > ACCEPTABLE_RESPONSE_TIME) {
                        recommendations.add(entry.getKey() + " 平均响应时间过长(" + avgTime + "ms)，建议优化");
                    }
                }
            }
        }
    }
    
    /**
     * 分析并发性能
     */
    private void analyzeConcurrencyPerformance(Map<String, Object> concurrencyResults, List<String> recommendations) {
        // 分析并发性能趋势
        for (Map.Entry<String, Object> entry : concurrencyResults.entrySet()) {
            if (entry.getValue() instanceof Map) {
                Map<String, Object> concurrencyResult = (Map<String, Object>) entry.getValue();
                if (concurrencyResult.containsKey("successRate")) {
                    Double successRate = (Double) concurrencyResult.get("successRate");
                    if (successRate < 95.0) {
                        recommendations.add(entry.getKey() + " 成功率偏低(" + successRate + "%)，建议检查系统稳定性");
                    }
                }
            }
        }
    }
    
    /**
     * 分析负载性能
     */
    private void analyzeLoadPerformance(Map<String, Object> loadResults, List<String> recommendations) {
        // 分析负载测试结果
        if (loadResults.containsKey("sustainedLoad")) {
            Map<String, Object> sustainedResult = (Map<String, Object>) loadResults.get("sustainedLoad");
            if (sustainedResult.containsKey("throughput")) {
                Double throughput = (Double) sustainedResult.get("throughput");
                if (throughput < 10.0) { // 假设期望吞吐量为10 req/s
                    recommendations.add("系统吞吐量偏低(" + throughput + " req/s)，建议优化性能");
                }
            }
        }
    }
}