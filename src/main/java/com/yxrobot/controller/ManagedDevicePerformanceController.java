package com.yxrobot.controller;

import com.yxrobot.common.Result;
import com.yxrobot.service.ManagedDeviceApiMonitoringService;
import com.yxrobot.service.ManagedDeviceErrorHandlingService;
import com.yxrobot.service.ManagedDevicePerformanceOptimizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 设备管理性能优化控制器
 * 提供性能优化、监控和错误处理的API接口
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-28
 */
@RestController
@RequestMapping("/api/admin/devices/performance")
public class ManagedDevicePerformanceController {
    
    private static final Logger logger = LoggerFactory.getLogger(ManagedDevicePerformanceController.class);
    
    @Autowired
    private ManagedDevicePerformanceOptimizationService performanceOptimizationService;
    
    @Autowired
    private ManagedDeviceApiMonitoringService apiMonitoringService;
    
    @Autowired
    private ManagedDeviceErrorHandlingService errorHandlingService;
    
    /**
     * 执行完整的性能优化
     * 
     * @return 优化结果
     */
    @PostMapping("/optimize")
    public Result<Map<String, Object>> executePerformanceOptimization() {
        logger.info("接收到性能优化请求");
        
        try {
            Map<String, Object> optimizationResults = performanceOptimizationService.executeFullPerformanceOptimization();
            return Result.success(optimizationResults);
            
        } catch (Exception e) {
            logger.error("性能优化执行失败", e);
            return errorHandlingService.handleBusinessException(e);
        }
    }
    
    /**
     * 获取性能统计数据
     * 
     * @return 性能统计
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getPerformanceStatistics() {
        logger.info("获取性能统计数据");
        
        try {
            Map<String, Object> statistics = performanceOptimizationService.getPerformanceStatistics();
            return Result.success(statistics);
            
        } catch (Exception e) {
            logger.error("获取性能统计失败", e);
            return errorHandlingService.handleBusinessException(e);
        }
    }
    
    /**
     * 获取API监控统计
     * 
     * @return API监控统计
     */
    @GetMapping("/api-monitoring")
    public Result<Map<String, Object>> getApiMonitoringStatistics() {
        logger.info("获取API监控统计数据");
        
        try {
            Map<String, Object> monitoringStats = apiMonitoringService.getApiMonitoringStatistics();
            return Result.success(monitoringStats);
            
        } catch (Exception e) {
            logger.error("获取API监控统计失败", e);
            return errorHandlingService.handleBusinessException(e);
        }
    }
    
    /**
     * 获取实时监控数据
     * 
     * @return 实时监控数据
     */
    @GetMapping("/real-time")
    public Result<Map<String, Object>> getRealTimeMonitoringData() {
        logger.info("获取实时监控数据");
        
        try {
            Map<String, Object> realTimeData = apiMonitoringService.getRealTimeMonitoringData();
            return Result.success(realTimeData);
            
        } catch (Exception e) {
            logger.error("获取实时监控数据失败", e);
            return errorHandlingService.handleBusinessException(e);
        }
    }
    
    /**
     * 生成性能报告
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 性能报告
     */
    @GetMapping("/report")
    public Result<Map<String, Object>> generatePerformanceReport(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        
        logger.info("生成性能报告，时间范围: {} - {}", startTime, endTime);
        
        try {
            Map<String, Object> report = apiMonitoringService.getPerformanceReport(startTime, endTime);
            return Result.success(report);
            
        } catch (Exception e) {
            logger.error("生成性能报告失败", e);
            return errorHandlingService.handleBusinessException(e);
        }
    }
    
    /**
     * 检查API健康状态
     * 
     * @return API健康状态
     */
    @GetMapping("/health")
    public Result<Map<String, Object>> checkApiHealth() {
        logger.info("检查API健康状态");
        
        try {
            Map<String, Object> healthCheck = apiMonitoringService.checkApiHealth();
            return Result.success(healthCheck);
            
        } catch (Exception e) {
            logger.error("API健康检查失败", e);
            return errorHandlingService.handleBusinessException(e);
        }
    }
    
    /**
     * 验证数据格式
     * 
     * @param data 待验证的数据
     * @return 验证结果
     */
    @PostMapping("/validate")
    public Result<Map<String, Object>> validateData(@RequestBody Map<String, Object> data) {
        logger.info("验证数据格式");
        
        try {
            Map<String, Object> validationResult = errorHandlingService.validateDeviceData(data);
            return Result.success(validationResult);
            
        } catch (Exception e) {
            logger.error("数据验证失败", e);
            return errorHandlingService.handleBusinessException(e);
        }
    }
    
    /**
     * 验证API参数
     * 
     * @param parameters 参数映射
     * @return 验证结果
     */
    @PostMapping("/validate-parameters")
    public Result<Map<String, Object>> validateParameters(@RequestBody Map<String, Object> parameters) {
        logger.info("验证API参数");
        
        try {
            Map<String, Object> validationResult = errorHandlingService.validateParameters(parameters);
            return Result.success(validationResult);
            
        } catch (Exception e) {
            logger.error("参数验证失败", e);
            return errorHandlingService.handleBusinessException(e);
        }
    }
    
    /**
     * XSS防护测试
     * 
     * @param input 输入字符串
     * @return 清理后的字符串
     */
    @PostMapping("/xss-sanitize")
    public Result<Map<String, Object>> sanitizeXSS(@RequestBody Map<String, String> input) {
        logger.info("执行XSS防护处理");
        
        try {
            Map<String, Object> result = new HashMap<>();
            
            for (Map.Entry<String, String> entry : input.entrySet()) {
                String sanitized = errorHandlingService.sanitizeXSS(entry.getValue());
                result.put(entry.getKey(), sanitized);
            }
            
            return Result.success(result);
            
        } catch (Exception e) {
            logger.error("XSS防护处理失败", e);
            return errorHandlingService.handleBusinessException(e);
        }
    }
    
    /**
     * PII数据保护
     * 
     * @param input 输入数据
     * @return 脱敏后的数据
     */
    @PostMapping("/pii-protect")
    public Result<Map<String, Object>> protectPII(@RequestBody Map<String, String> input) {
        logger.info("执行PII数据保护");
        
        try {
            Map<String, Object> result = new HashMap<>();
            
            for (Map.Entry<String, String> entry : input.entrySet()) {
                String protected_data = errorHandlingService.protectPII(entry.getValue());
                result.put(entry.getKey(), protected_data);
            }
            
            return Result.success(result);
            
        } catch (Exception e) {
            logger.error("PII数据保护失败", e);
            return errorHandlingService.handleBusinessException(e);
        }
    }
    
    /**
     * 清理过期监控数据
     * 
     * @return 清理结果
     */
    @PostMapping("/cleanup")
    public Result<Map<String, Object>> cleanupExpiredData() {
        logger.info("清理过期监控数据");
        
        try {
            apiMonitoringService.cleanupExpiredMetrics();
            
            Map<String, Object> result = new HashMap<>();
            result.put("message", "过期数据清理完成");
            result.put("timestamp", LocalDateTime.now());
            
            return Result.success(result);
            
        } catch (Exception e) {
            logger.error("清理过期数据失败", e);
            return errorHandlingService.handleBusinessException(e);
        }
    }
    
    /**
     * 获取系统资源使用情况
     * 
     * @return 系统资源信息
     */
    @GetMapping("/system-resources")
    public Result<Map<String, Object>> getSystemResources() {
        logger.info("获取系统资源使用情况");
        
        try {
            Map<String, Object> systemResources = new HashMap<>();
            
            // 内存使用情况
            Runtime runtime = Runtime.getRuntime();
            Map<String, Object> memoryInfo = new HashMap<>();
            memoryInfo.put("totalMemory", runtime.totalMemory());
            memoryInfo.put("freeMemory", runtime.freeMemory());
            memoryInfo.put("usedMemory", runtime.totalMemory() - runtime.freeMemory());
            memoryInfo.put("maxMemory", runtime.maxMemory());
            
            systemResources.put("memory", memoryInfo);
            
            // 处理器信息
            Map<String, Object> processorInfo = new HashMap<>();
            processorInfo.put("availableProcessors", runtime.availableProcessors());
            
            systemResources.put("processor", processorInfo);
            
            // 系统时间
            systemResources.put("systemTime", LocalDateTime.now());
            
            return Result.success(systemResources);
            
        } catch (Exception e) {
            logger.error("获取系统资源信息失败", e);
            return errorHandlingService.handleBusinessException(e);
        }
    }
    
    /**
     * 性能优化建议
     * 
     * @return 优化建议
     */
    @GetMapping("/recommendations")
    public Result<Map<String, Object>> getPerformanceRecommendations() {
        logger.info("获取性能优化建议");
        
        try {
            Map<String, Object> recommendations = new HashMap<>();
            
            // 获取当前性能统计
            Map<String, Object> performanceStats = performanceOptimizationService.getPerformanceStatistics();
            Map<String, Object> apiStats = apiMonitoringService.getApiMonitoringStatistics();
            
            // 生成建议
            recommendations.put("databaseOptimization", generateDatabaseRecommendations(performanceStats));
            recommendations.put("apiOptimization", generateApiRecommendations(apiStats));
            recommendations.put("systemOptimization", generateSystemRecommendations());
            
            return Result.success(recommendations);
            
        } catch (Exception e) {
            logger.error("获取性能优化建议失败", e);
            return errorHandlingService.handleBusinessException(e);
        }
    }
    
    // 辅助方法
    
    private Map<String, Object> generateDatabaseRecommendations(Map<String, Object> performanceStats) {
        Map<String, Object> dbRecommendations = new HashMap<>();
        
        // 基于性能统计生成数据库优化建议
        dbRecommendations.put("indexOptimization", "定期检查和优化数据库索引");
        dbRecommendations.put("queryOptimization", "优化慢查询，使用EXPLAIN分析查询计划");
        dbRecommendations.put("statisticsUpdate", "定期更新表统计信息");
        dbRecommendations.put("connectionPool", "优化数据库连接池配置");
        
        return dbRecommendations;
    }
    
    private Map<String, Object> generateApiRecommendations(Map<String, Object> apiStats) {
        Map<String, Object> apiRecommendations = new HashMap<>();
        
        // 基于API统计生成优化建议
        apiRecommendations.put("responseTime", "监控API响应时间，优化慢接口");
        apiRecommendations.put("errorHandling", "完善错误处理机制，提供友好错误信息");
        apiRecommendations.put("caching", "对频繁访问的数据考虑使用缓存");
        apiRecommendations.put("rateLimit", "实施API限流，防止过度使用");
        
        return apiRecommendations;
    }
    
    private Map<String, Object> generateSystemRecommendations() {
        Map<String, Object> systemRecommendations = new HashMap<>();
        
        // 系统级优化建议
        systemRecommendations.put("memoryManagement", "监控内存使用，及时清理无用对象");
        systemRecommendations.put("logging", "合理配置日志级别，避免过度日志记录");
        systemRecommendations.put("monitoring", "建立完善的监控和告警机制");
        systemRecommendations.put("security", "定期进行安全检查和漏洞扫描");
        
        return systemRecommendations;
    }
}