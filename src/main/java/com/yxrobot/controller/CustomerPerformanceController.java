package com.yxrobot.controller;

import com.yxrobot.service.CustomerPerformanceMonitorService;
import com.yxrobot.service.CustomerQueryOptimizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 客户管理性能监控控制器
 * 提供性能监控和优化建议的API接口
 */
@RestController
@RequestMapping("/api/admin/customers/performance")
public class CustomerPerformanceController {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerPerformanceController.class);
    
    @Autowired
    private CustomerPerformanceMonitorService performanceMonitorService;
    
    @Autowired
    private CustomerQueryOptimizationService queryOptimizationService;
    
    @Autowired
    private com.yxrobot.service.CustomerDatabaseOptimizationService databaseOptimizationService;
    
    /**
     * 获取性能监控报告
     */
    @GetMapping("/report")
    public ResponseEntity<Map<String, Object>> getPerformanceReport() {
        logger.info("获取客户管理性能监控报告");
        
        try {
            CustomerPerformanceMonitorService.PerformanceReport report = 
                performanceMonitorService.getPerformanceReport();
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取性能报告成功");
            response.put("data", report);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取性能报告失败", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "获取性能报告失败: " + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取查询性能统计
     */
    @GetMapping("/query-stats")
    public ResponseEntity<Map<String, Object>> getQueryPerformanceStats() {
        logger.info("获取查询性能统计");
        
        try {
            Map<String, CustomerQueryOptimizationService.QueryPerformanceStats> stats = 
                queryOptimizationService.getQueryPerformanceStats();
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取查询性能统计成功");
            response.put("data", stats);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取查询性能统计失败", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "获取查询性能统计失败: " + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 检查API健康状态
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> checkApiHealth() {
        logger.info("检查客户管理API健康状态");
        
        try {
            Map<String, Object> healthStatus = new HashMap<>();
            
            // 检查主要API的健康状态
            boolean customerListHealthy = performanceMonitorService.isApiHealthy("GET", "/api/admin/customers");
            boolean customerStatsHealthy = performanceMonitorService.isApiHealthy("GET", "/api/admin/customers/stats");
            boolean customerDetailHealthy = performanceMonitorService.isApiHealthy("GET", "/api/admin/customers/{id}");
            
            healthStatus.put("customerList", Map.of(
                "healthy", customerListHealthy,
                "status", customerListHealthy ? "OK" : "DEGRADED"
            ));
            
            healthStatus.put("customerStats", Map.of(
                "healthy", customerStatsHealthy,
                "status", customerStatsHealthy ? "OK" : "DEGRADED"
            ));
            
            healthStatus.put("customerDetail", Map.of(
                "healthy", customerDetailHealthy,
                "status", customerDetailHealthy ? "OK" : "DEGRADED"
            ));
            
            // 整体健康状态
            boolean overallHealthy = customerListHealthy && customerStatsHealthy && customerDetailHealthy;
            healthStatus.put("overall", Map.of(
                "healthy", overallHealthy,
                "status", overallHealthy ? "HEALTHY" : "DEGRADED"
            ));
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "API健康检查完成");
            response.put("data", healthStatus);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("API健康检查失败", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "API健康检查失败: " + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 重置性能统计
     */
    @PostMapping("/reset-stats")
    public ResponseEntity<Map<String, Object>> resetPerformanceStats() {
        logger.info("重置性能统计数据");
        
        try {
            performanceMonitorService.resetPerformanceStats();
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "性能统计数据已重置");
            response.put("data", null);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("重置性能统计失败", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "重置性能统计失败: " + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取性能阈值配置
     */
    @GetMapping("/thresholds")
    public ResponseEntity<Map<String, Object>> getPerformanceThresholds() {
        logger.info("获取性能阈值配置");
        
        try {
            Map<String, Long> thresholds = Map.of(
                "customerStats", 1000L,
                "customerList", 2000L,
                "customerDetail", 1500L,
                "customerCreate", 3000L,
                "customerUpdate", 2000L,
                "customerDelete", 1000L,
                "customerDevices", 2000L,
                "customerOrders", 2000L,
                "customerServiceRecords", 2000L
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取性能阈值配置成功");
            response.put("data", thresholds);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取性能阈值配置失败", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "获取性能阈值配置失败: " + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取性能优化建议
     */
    @GetMapping("/optimization-suggestions")
    public ResponseEntity<Map<String, Object>> getOptimizationSuggestions() {
        logger.info("获取性能优化建议");
        
        try {
            // 获取性能报告
            CustomerPerformanceMonitorService.PerformanceReport report = 
                performanceMonitorService.getPerformanceReport();
            
            // 生成优化建议
            Map<String, Object> suggestions = generateOptimizationSuggestions(report);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取性能优化建议成功");
            response.put("data", suggestions);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取性能优化建议失败", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "获取性能优化建议失败: " + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 生成性能优化建议
     */
    private Map<String, Object> generateOptimizationSuggestions(
            CustomerPerformanceMonitorService.PerformanceReport report) {
        
        Map<String, Object> suggestions = new HashMap<>();
        
        // 基于慢请求率生成建议
        if (report.getSlowRequestRate() > 10) {
            suggestions.put("slowRequestRate", Map.of(
                "issue", "慢请求率过高: " + String.format("%.2f%%", report.getSlowRequestRate()),
                "suggestions", java.util.List.of(
                    "检查数据库索引是否完整",
                    "优化查询条件，减少全表扫描",
                    "考虑增加缓存机制",
                    "检查网络连接和数据库连接池配置"
                )
            ));
        }
        
        // 基于API性能生成建议
        if (report.getApiPerformanceInfos() != null) {
            for (CustomerPerformanceMonitorService.ApiPerformanceInfo apiInfo : report.getApiPerformanceInfos()) {
                if (apiInfo.getThreshold() != null && 
                    apiInfo.getAverageResponseTime() > apiInfo.getThreshold()) {
                    
                    suggestions.put(apiInfo.getApiKey(), Map.of(
                        "issue", "API响应时间超过阈值: " + 
                                String.format("%.2fms > %dms", apiInfo.getAverageResponseTime(), apiInfo.getThreshold()),
                        "suggestions", generateApiSpecificSuggestions(apiInfo.getApiKey())
                    ));
                }
            }
        }
        
        // 基于成功率生成建议
        if (report.getApiPerformanceInfos() != null) {
            for (CustomerPerformanceMonitorService.ApiPerformanceInfo apiInfo : report.getApiPerformanceInfos()) {
                if (apiInfo.getSuccessRate() < 95.0) {
                    suggestions.put(apiInfo.getApiKey() + "_success_rate", Map.of(
                        "issue", "API成功率过低: " + String.format("%.2f%%", apiInfo.getSuccessRate()),
                        "suggestions", java.util.List.of(
                            "检查API错误日志，分析失败原因",
                            "完善错误处理和重试机制",
                            "检查数据库连接稳定性",
                            "验证输入参数的有效性"
                        )
                    ));
                }
            }
        }
        
        return suggestions;
    }
    
    /**
     * 生成API特定的优化建议
     */
    private java.util.List<String> generateApiSpecificSuggestions(String apiKey) {
        if (apiKey.contains("/stats")) {
            return java.util.List.of(
                "考虑缓存统计数据，减少实时计算",
                "优化统计查询SQL，使用聚合索引",
                "定期预计算统计数据"
            );
        } else if (apiKey.contains("/customers") && !apiKey.contains("/{id}")) {
            return java.util.List.of(
                "优化分页查询，使用覆盖索引",
                "限制查询结果集大小",
                "优化搜索条件，使用前缀索引",
                "考虑使用读写分离"
            );
        } else if (apiKey.contains("/{id}")) {
            return java.util.List.of(
                "确保主键索引有效",
                "优化关联查询，使用适当的JOIN策略",
                "考虑缓存热点数据"
            );
        } else {
            return java.util.List.of(
                "检查数据库索引配置",
                "优化查询逻辑",
                "考虑增加缓存"
            );
        }
    }
    
    /**
     * 获取数据库索引报告
     */
    @GetMapping("/database/indexes")
    public ResponseEntity<Map<String, Object>> getDatabaseIndexReport() {
        logger.info("获取数据库索引报告");
        
        try {
            com.yxrobot.service.CustomerDatabaseOptimizationService.DatabaseIndexReport report = 
                databaseOptimizationService.checkDatabaseIndexes();
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取数据库索引报告成功");
            response.put("data", report);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取数据库索引报告失败", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "获取数据库索引报告失败: " + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取表统计信息
     */
    @GetMapping("/database/table-stats")
    public ResponseEntity<Map<String, Object>> getTableStats() {
        logger.info("获取表统计信息");
        
        try {
            Map<String, com.yxrobot.service.CustomerDatabaseOptimizationService.TableStats> stats = 
                databaseOptimizationService.getTableStats();
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取表统计信息成功");
            response.put("data", stats);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取表统计信息失败", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "获取表统计信息失败: " + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.status(500).body(response);
        }
    }
}