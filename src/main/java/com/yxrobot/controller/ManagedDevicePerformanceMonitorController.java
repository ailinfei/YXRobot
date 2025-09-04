package com.yxrobot.controller;

import com.yxrobot.service.ManagedDevicePerformanceMonitorService;
import com.yxrobot.service.ManagedDeviceStatsService;
import com.yxrobot.service.ManagedDevicePerformanceTestService;
import com.yxrobot.service.ManagedDeviceLargeDataOptimizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 设备管理性能监控控制器
 * 提供性能监控数据查询和管理接口
 */
@RestController
@RequestMapping("/api/admin/devices/performance")
public class ManagedDevicePerformanceMonitorController {

    private static final Logger logger = LoggerFactory.getLogger(ManagedDevicePerformanceMonitorController.class);
    
    @Autowired
    private ManagedDevicePerformanceMonitorService performanceMonitorService;
    
    @Autowired
    private ManagedDeviceStatsService statsService;
    
    @Autowired
    private ManagedDevicePerformanceTestService performanceTestService;
    
    @Autowired
    private ManagedDeviceLargeDataOptimizationService largeDataOptimizationService;
    
    /**
     * 获取性能监控报告
     * 
     * @return 性能监控报告
     */
    @GetMapping("/report")
    public ResponseEntity<Map<String, Object>> getPerformanceReport() {
        try {
            Map<String, Object> response = new HashMap<>();
            
            // 生成性能报告
            String report = performanceMonitorService.generatePerformanceReport();
            
            // 获取关键性能指标
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("deviceListApiAvgTime", performanceMonitorService.getAverageApiResponseTime("GET /api/admin/devices"));
            metrics.put("deviceDetailApiAvgTime", performanceMonitorService.getAverageApiResponseTime("GET /api/admin/devices/{id}"));
            metrics.put("deviceCreateApiAvgTime", performanceMonitorService.getAverageApiResponseTime("POST /api/admin/devices"));
            metrics.put("deviceUpdateApiAvgTime", performanceMonitorService.getAverageApiResponseTime("PUT /api/admin/devices/{id}"));
            
            // 获取功能使用统计
            Map<String, Object> functionUsage = new HashMap<>();
            functionUsage.put("deviceListQuery", performanceMonitorService.getApiCallCount("function_usage_device_list_query"));
            functionUsage.put("deviceDetailQuery", performanceMonitorService.getApiCallCount("function_usage_device_detail_query"));
            functionUsage.put("deviceCreate", performanceMonitorService.getApiCallCount("function_usage_device_create"));
            functionUsage.put("deviceUpdate", performanceMonitorService.getApiCallCount("function_usage_device_update"));
            functionUsage.put("deviceDelete", performanceMonitorService.getApiCallCount("function_usage_device_delete"));
            functionUsage.put("deviceReboot", performanceMonitorService.getApiCallCount("function_usage_device_reboot"));
            functionUsage.put("firmwarePush", performanceMonitorService.getApiCallCount("function_usage_firmware_push"));
            
            response.put("report", report);
            response.put("metrics", metrics);
            response.put("functionUsage", functionUsage);
            
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "性能报告获取成功",
                "data", response
            ));
        } catch (Exception e) {
            logger.error("获取性能报告失败", e);
            return ResponseEntity.ok(Map.of(
                "code", 500,
                "message", "获取性能报告失败: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 获取API性能指标
     * 
     * @param apiName API名称
     * @return API性能指标
     */
    @GetMapping("/api/{apiName}/metrics")
    public ResponseEntity<Map<String, Object>> getApiMetrics(@PathVariable String apiName) {
        try {
            Map<String, Object> metrics = new HashMap<>();
            
            String fullApiName = apiName.replace("_", " ").replace("-", "/");
            
            metrics.put("averageResponseTime", performanceMonitorService.getAverageApiResponseTime(fullApiName));
            metrics.put("callCount", performanceMonitorService.getApiCallCount(fullApiName));
            
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "API性能指标获取成功",
                "data", metrics
            ));
        } catch (Exception e) {
            logger.error("获取API性能指标失败", e);
            return ResponseEntity.ok(Map.of(
                "code", 500,
                "message", "获取API性能指标失败: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 获取业务监控指标
     * 
     * @return 业务监控指标
     */
    @GetMapping("/business-metrics")
    public ResponseEntity<Map<String, Object>> getBusinessMetrics() {
        try {
            Map<String, Object> metrics = new HashMap<>();
            
            // 获取设备统计数据并记录业务指标
            try {
                var stats = statsService.getManagedDeviceStats();
                if (stats != null) {
                    performanceMonitorService.recordDeviceOnlineRate(stats.getTotal(), stats.getOnline());
                    
                    metrics.put("totalDevices", stats.getTotal());
                    metrics.put("onlineDevices", stats.getOnline());
                    metrics.put("offlineDevices", stats.getOffline());
                    metrics.put("errorDevices", stats.getError());
                    metrics.put("maintenanceDevices", stats.getMaintenance());
                    
                    // 计算在线率
                    double onlineRate = stats.getTotal() > 0 ? (double) stats.getOnline() / stats.getTotal() * 100 : 0;
                    metrics.put("onlineRate", Math.round(onlineRate * 100.0) / 100.0);
                }
            } catch (Exception e) {
                logger.warn("获取设备统计数据失败: {}", e.getMessage());
            }
            
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "业务监控指标获取成功",
                "data", metrics
            ));
        } catch (Exception e) {
            logger.error("获取业务监控指标失败", e);
            return ResponseEntity.ok(Map.of(
                "code", 500,
                "message", "获取业务监控指标失败: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 测试并发访问性能
     * 
     * @param concurrentUsers 并发用户数
     * @return 并发测试结果
     */
    @PostMapping("/concurrent-test")
    public ResponseEntity<Map<String, Object>> testConcurrentAccess(@RequestParam(defaultValue = "10") int concurrentUsers) {
        try {
            long startTime = System.currentTimeMillis();
            
            // 模拟并发访问测试
            // 这里可以实现实际的并发测试逻辑
            Thread.sleep(100); // 模拟处理时间
            
            long responseTime = System.currentTimeMillis() - startTime;
            
            // 记录并发访问性能
            performanceMonitorService.recordConcurrentAccessPerformance(concurrentUsers, responseTime);
            
            Map<String, Object> result = new HashMap<>();
            result.put("concurrentUsers", concurrentUsers);
            result.put("responseTime", responseTime);
            result.put("status", "success");
            
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "并发访问测试完成",
                "data", result
            ));
        } catch (Exception e) {
            logger.error("并发访问测试失败", e);
            return ResponseEntity.ok(Map.of(
                "code", 500,
                "message", "并发访问测试失败: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 执行并发访问性能测试
     * 
     * @param concurrentUsers 并发用户数
     * @param testDurationSeconds 测试持续时间
     * @return 并发测试结果
     */
    @PostMapping("/concurrent-load-test")
    public ResponseEntity<Map<String, Object>> executeConcurrentLoadTest(
            @RequestParam(defaultValue = "10") int concurrentUsers,
            @RequestParam(defaultValue = "30") int testDurationSeconds) {
        try {
            Map<String, Object> testResult = performanceTestService.executeConcurrentAccessTest(
                concurrentUsers, testDurationSeconds);
            
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "并发负载测试完成",
                "data", testResult
            ));
        } catch (Exception e) {
            logger.error("并发负载测试失败", e);
            return ResponseEntity.ok(Map.of(
                "code", 500,
                "message", "并发负载测试失败: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 执行分页性能基准测试
     * 
     * @return 基准测试结果
     */
    @PostMapping("/pagination-benchmark")
    public ResponseEntity<Map<String, Object>> executePaginationBenchmark() {
        try {
            Map<String, Object> benchmarkResult = performanceTestService.executePaginationBenchmarkTest();
            
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "分页性能基准测试完成",
                "data", benchmarkResult
            ));
        } catch (Exception e) {
            logger.error("分页性能基准测试失败", e);
            return ResponseEntity.ok(Map.of(
                "code", 500,
                "message", "分页性能基准测试失败: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 执行大数据量查询性能测试
     * 
     * @return 大数据量测试结果
     */
    @PostMapping("/large-data-test")
    public ResponseEntity<Map<String, Object>> executeLargeDataTest() {
        try {
            Map<String, Object> testResult = performanceTestService.executeLargeDataQueryTest();
            
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "大数据量查询性能测试完成",
                "data", testResult
            ));
        } catch (Exception e) {
            logger.error("大数据量查询性能测试失败", e);
            return ResponseEntity.ok(Map.of(
                "code", 500,
                "message", "大数据量查询性能测试失败: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 获取分页性能统计
     * 
     * @return 分页性能统计
     */
    @GetMapping("/pagination-stats")
    public ResponseEntity<Map<String, Object>> getPaginationStats() {
        try {
            Map<String, Object> stats = largeDataOptimizationService.getPaginationPerformanceStats();
            
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "分页性能统计获取成功",
                "data", stats
            ));
        } catch (Exception e) {
            logger.error("获取分页性能统计失败", e);
            return ResponseEntity.ok(Map.of(
                "code", 500,
                "message", "获取分页性能统计失败: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 获取完整性能测试报告
     * 
     * @return 完整性能测试报告
     */
    @GetMapping("/full-report")
    public ResponseEntity<Map<String, Object>> getFullPerformanceReport() {
        try {
            Map<String, Object> fullReport = performanceTestService.getPerformanceTestReport();
            
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "完整性能报告获取成功",
                "data", fullReport
            ));
        } catch (Exception e) {
            logger.error("获取完整性能报告失败", e);
            return ResponseEntity.ok(Map.of(
                "code", 500,
                "message", "获取完整性能报告失败: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 健康检查接口
     * 
     * @return 健康状态
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(Map.of(
            "code", 200,
            "message", "系统健康",
            "data", health
        ));
    }
}