package com.yxrobot.service;

import com.yxrobot.dto.ManagedDeviceSearchCriteria;
import com.yxrobot.entity.DeviceStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 设备管理性能测试服务
 * 提供并发访问测试、性能基准测试等功能
 */
@Service
public class ManagedDevicePerformanceTestService {

    private static final Logger logger = LoggerFactory.getLogger(ManagedDevicePerformanceTestService.class);
    
    @Autowired
    private ManagedDeviceService managedDeviceService;
    
    @Autowired
    private ManagedDevicePerformanceMonitorService performanceMonitorService;
    
    @Autowired
    private ManagedDeviceLargeDataOptimizationService largeDataOptimizationService;
    
    private final ExecutorService executorService = Executors.newFixedThreadPool(50);
    
    /**
     * 执行并发访问性能测试
     * 
     * @param concurrentUsers 并发用户数
     * @param testDurationSeconds 测试持续时间（秒）
     * @return 测试结果
     */
    public Map<String, Object> executeConcurrentAccessTest(int concurrentUsers, int testDurationSeconds) {
        logger.info("开始并发访问性能测试 - 并发用户数: {}, 测试时长: {}秒", concurrentUsers, testDurationSeconds);
        
        Map<String, Object> testResult = new HashMap<>();
        List<Future<Map<String, Object>>> futures = new ArrayList<>();
        
        long testStartTime = System.currentTimeMillis();
        long testEndTime = testStartTime + (testDurationSeconds * 1000L);
        
        // 启动并发测试任务
        for (int i = 0; i < concurrentUsers; i++) {
            Future<Map<String, Object>> future = executorService.submit(() -> 
                executeSingleUserTest(testEndTime));
            futures.add(future);
        }
        
        // 收集测试结果
        List<Map<String, Object>> userResults = new ArrayList<>();
        int successfulUsers = 0;
        int failedUsers = 0;
        long totalResponseTime = 0;
        int totalRequests = 0;
        
        for (Future<Map<String, Object>> future : futures) {
            try {
                Map<String, Object> userResult = future.get(testDurationSeconds + 10, TimeUnit.SECONDS);
                userResults.add(userResult);
                
                boolean success = (Boolean) userResult.get("success");
                if (success) {
                    successfulUsers++;
                    totalResponseTime += (Long) userResult.get("totalResponseTime");
                    totalRequests += (Integer) userResult.get("requestCount");
                } else {
                    failedUsers++;
                }
            } catch (Exception e) {
                logger.error("获取用户测试结果失败", e);
                failedUsers++;
            }
        }
        
        // 计算测试指标
        long actualTestDuration = System.currentTimeMillis() - testStartTime;
        double averageResponseTime = totalRequests > 0 ? (double) totalResponseTime / totalRequests : 0;
        double throughput = totalRequests > 0 ? (double) totalRequests / (actualTestDuration / 1000.0) : 0;
        double successRate = concurrentUsers > 0 ? (double) successfulUsers / concurrentUsers * 100 : 0;
        
        // 记录并发访问性能
        performanceMonitorService.recordConcurrentAccessPerformance(concurrentUsers, (long) averageResponseTime);
        
        // 组装测试结果
        testResult.put("concurrentUsers", concurrentUsers);
        testResult.put("testDurationSeconds", testDurationSeconds);
        testResult.put("actualTestDurationMs", actualTestDuration);
        testResult.put("successfulUsers", successfulUsers);
        testResult.put("failedUsers", failedUsers);
        testResult.put("totalRequests", totalRequests);
        testResult.put("averageResponseTimeMs", Math.round(averageResponseTime));
        testResult.put("throughputRequestsPerSecond", Math.round(throughput * 100.0) / 100.0);
        testResult.put("successRatePercent", Math.round(successRate * 100.0) / 100.0);
        testResult.put("userResults", userResults);
        
        logger.info("并发访问性能测试完成 - 成功用户: {}, 失败用户: {}, 平均响应时间: {}ms, 吞吐量: {} req/s", 
            successfulUsers, failedUsers, Math.round(averageResponseTime), Math.round(throughput * 100.0) / 100.0);
        
        return testResult;
    }
    
    /**
     * 执行单个用户的测试
     * 
     * @param testEndTime 测试结束时间
     * @return 用户测试结果
     */
    private Map<String, Object> executeSingleUserTest(long testEndTime) {
        Map<String, Object> userResult = new HashMap<>();
        int requestCount = 0;
        long totalResponseTime = 0;
        boolean success = true;
        List<String> errors = new ArrayList<>();
        
        try {
            while (System.currentTimeMillis() < testEndTime) {
                long requestStartTime = System.currentTimeMillis();
                
                try {
                    // 执行设备列表查询
                    managedDeviceService.getManagedDevices(1, 20, null, null, null, null, null, null, null, null);
                    
                    long responseTime = System.currentTimeMillis() - requestStartTime;
                    totalResponseTime += responseTime;
                    requestCount++;
                    
                    // 短暂休息避免过度压力
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        success = false;
                        errors.add("测试被中断: " + ie.getMessage());
                        break;
                    }
                    
                } catch (Exception e) {
                    errors.add(e.getMessage());
                    if (errors.size() > 10) { // 错误过多则停止测试
                        success = false;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            success = false;
            errors.add("测试执行失败: " + e.getMessage());
        }
        
        userResult.put("success", success);
        userResult.put("requestCount", requestCount);
        userResult.put("totalResponseTime", totalResponseTime);
        userResult.put("averageResponseTime", requestCount > 0 ? totalResponseTime / requestCount : 0);
        userResult.put("errorCount", errors.size());
        userResult.put("errors", errors);
        
        return userResult;
    }
    
    /**
     * 执行分页性能基准测试
     * 
     * @return 基准测试结果
     */
    public Map<String, Object> executePaginationBenchmarkTest() {
        logger.info("开始分页性能基准测试");
        
        Map<String, Object> benchmarkResult = new HashMap<>();
        
        // 测试不同页面大小的性能
        int[] pageSizes = {10, 20, 50, 100};
        Map<String, Object> pageSizeResults = new HashMap<>();
        
        for (int pageSize : pageSizes) {
            Map<String, Object> pageSizeResult = testPaginationPerformance(pageSize);
            pageSizeResults.put("pageSize_" + pageSize, pageSizeResult);
        }
        
        // 测试不同页码的性能
        int[] pageNumbers = {1, 5, 10, 20, 50};
        Map<String, Object> pageNumberResults = new HashMap<>();
        
        for (int pageNumber : pageNumbers) {
            Map<String, Object> pageNumberResult = testPaginationPerformance(20, pageNumber);
            pageNumberResults.put("page_" + pageNumber, pageNumberResult);
        }
        
        benchmarkResult.put("pageSizeTests", pageSizeResults);
        benchmarkResult.put("pageNumberTests", pageNumberResults);
        benchmarkResult.put("testTime", LocalDateTime.now());
        
        logger.info("分页性能基准测试完成");
        
        return benchmarkResult;
    }
    
    /**
     * 测试特定页面大小的分页性能
     * 
     * @param pageSize 页面大小
     * @return 测试结果
     */
    private Map<String, Object> testPaginationPerformance(int pageSize) {
        return testPaginationPerformance(pageSize, 1);
    }
    
    /**
     * 测试特定页面大小和页码的分页性能
     * 
     * @param pageSize 页面大小
     * @param pageNumber 页码
     * @return 测试结果
     */
    private Map<String, Object> testPaginationPerformance(int pageSize, int pageNumber) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            long startTime = System.currentTimeMillis();
            
            var pageResult = managedDeviceService.getManagedDevices(
                pageNumber, pageSize, null, null, null, null, null, null, null, null);
            
            long responseTime = System.currentTimeMillis() - startTime;
            
            result.put("pageSize", pageSize);
            result.put("pageNumber", pageNumber);
            result.put("responseTimeMs", responseTime);
            result.put("resultCount", pageResult.getList().size());
            result.put("totalCount", pageResult.getTotal());
            result.put("success", true);
            
            // 记录性能指标
            performanceMonitorService.recordDatabaseQueryPerformance(
                "pagination_benchmark_" + pageSize + "_" + pageNumber, responseTime);
            
        } catch (Exception e) {
            logger.error("分页性能测试失败: pageSize={}, pageNumber={}", pageSize, pageNumber, e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 执行大数据量查询性能测试
     * 
     * @return 测试结果
     */
    public Map<String, Object> executeLargeDataQueryTest() {
        logger.info("开始大数据量查询性能测试");
        
        Map<String, Object> testResult = new HashMap<>();
        
        try {
            // 创建大数据量查询条件
            ManagedDeviceSearchCriteria criteria = new ManagedDeviceSearchCriteria();
            criteria.setPage(1);
            criteria.setPageSize(100);
            
            // 测试优化前的性能
            long beforeOptimizationTime = testQueryPerformance(criteria, "before_optimization");
            
            // 应用大数据量优化
            Map<String, Object> optimization = largeDataOptimizationService.optimizeLargeDataQuery(criteria);
            
            // 测试优化后的性能
            ManagedDeviceSearchCriteria optimizedCriteria = 
                (ManagedDeviceSearchCriteria) optimization.get("optimizedCriteria");
            long afterOptimizationTime = testQueryPerformance(optimizedCriteria, "after_optimization");
            
            // 计算性能提升
            double performanceImprovement = beforeOptimizationTime > 0 ? 
                ((double) (beforeOptimizationTime - afterOptimizationTime) / beforeOptimizationTime) * 100 : 0;
            
            testResult.put("beforeOptimizationTimeMs", beforeOptimizationTime);
            testResult.put("afterOptimizationTimeMs", afterOptimizationTime);
            testResult.put("performanceImprovementPercent", Math.round(performanceImprovement * 100.0) / 100.0);
            testResult.put("optimization", optimization);
            testResult.put("testTime", LocalDateTime.now());
            
        } catch (Exception e) {
            logger.error("大数据量查询性能测试失败", e);
            testResult.put("error", e.getMessage());
        }
        
        logger.info("大数据量查询性能测试完成");
        
        return testResult;
    }
    
    /**
     * 测试查询性能
     * 
     * @param criteria 查询条件
     * @param testType 测试类型
     * @return 响应时间
     */
    private long testQueryPerformance(ManagedDeviceSearchCriteria criteria, String testType) {
        try {
            long startTime = System.currentTimeMillis();
            
            managedDeviceService.getManagedDevices(
                criteria.getPage(), criteria.getPageSize(), criteria.getKeyword(),
                criteria.getStatus() != null ? criteria.getStatus().getCode() : null, 
                criteria.getModel() != null ? criteria.getModel().getCode() : null, 
                criteria.getCustomerId(),
                null, null, criteria.getSortBy(), criteria.getSortOrder());
            
            long responseTime = System.currentTimeMillis() - startTime;
            
            // 记录测试性能
            performanceMonitorService.recordDatabaseQueryPerformance(
                "large_data_query_" + testType, responseTime);
            
            return responseTime;
            
        } catch (Exception e) {
            logger.error("查询性能测试失败: testType={}", testType, e);
            return -1;
        }
    }
    
    /**
     * 获取性能测试报告
     * 
     * @return 性能测试报告
     */
    public Map<String, Object> getPerformanceTestReport() {
        Map<String, Object> report = new HashMap<>();
        
        try {
            // 获取分页性能统计
            Map<String, Object> paginationStats = largeDataOptimizationService.getPaginationPerformanceStats();
            
            // 获取性能监控报告
            String performanceReport = performanceMonitorService.generatePerformanceReport();
            
            report.put("paginationStats", paginationStats);
            report.put("performanceReport", performanceReport);
            report.put("reportTime", LocalDateTime.now());
            
        } catch (Exception e) {
            logger.error("生成性能测试报告失败", e);
            report.put("error", e.getMessage());
        }
        
        return report;
    }
    
    /**
     * 关闭执行器服务
     */
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}