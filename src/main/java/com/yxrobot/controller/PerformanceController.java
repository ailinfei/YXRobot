package com.yxrobot.controller;

import com.yxrobot.util.PerformanceMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 性能监控控制器
 * 提供系统性能数据查询接口
 */
@RestController
@RequestMapping("/api/performance")
public class PerformanceController {
    
    @Autowired
    private PerformanceMonitor performanceMonitor;
    
    /**
     * 获取系统整体性能指标
     * GET /api/performance/system
     */
    @GetMapping("/system")
    public ResponseEntity<Map<String, Object>> getSystemMetrics() {
        try {
            PerformanceMonitor.SystemPerformanceMetrics metrics = performanceMonitor.getSystemMetrics();
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取系统性能指标成功");
            response.put("data", metrics);
            response.put("success", true);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse("获取系统性能指标失败：" + e.getMessage()));
        }
    }
    
    /**
     * 获取所有API的性能统计
     * GET /api/performance/apis
     */
    @GetMapping("/apis")
    public ResponseEntity<Map<String, Object>> getAllApiStats() {
        try {
            Map<String, PerformanceMonitor.ApiPerformanceStats> apiStats = performanceMonitor.getAllApiStats();
            
            // 转换为前端友好的格式
            Map<String, Object> formattedStats = new HashMap<>();
            for (Map.Entry<String, PerformanceMonitor.ApiPerformanceStats> entry : apiStats.entrySet()) {
                PerformanceMonitor.ApiPerformanceStats stats = entry.getValue();
                Map<String, Object> statData = new HashMap<>();
                statData.put("totalCalls", stats.getTotalCalls());
                statData.put("averageResponseTime", Math.round(stats.getAverageResponseTime()));
                statData.put("minResponseTime", stats.getMinResponseTime());
                statData.put("maxResponseTime", stats.getMaxResponseTime());
                statData.put("successRate", Math.round(stats.getSuccessRate() * 100) / 100.0);
                statData.put("errorCalls", stats.getErrorCalls());
                statData.put("lastCallTime", stats.getLastCallTime());
                
                formattedStats.put(entry.getKey(), statData);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取API性能统计成功");
            response.put("data", formattedStats);
            response.put("success", true);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse("获取API性能统计失败：" + e.getMessage()));
        }
    }
    
    /**
     * 获取特定API的性能统计
     * GET /api/performance/apis/{apiPath}
     */
    @GetMapping("/apis/**")
    public ResponseEntity<Map<String, Object>> getApiStats(HttpServletRequest request) {
        try {
            String apiPath = request.getRequestURI().substring("/api/performance/apis".length());
            PerformanceMonitor.ApiPerformanceStats stats = performanceMonitor.getApiStats(apiPath);
            
            if (stats == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("未找到API性能数据：" + apiPath));
            }
            
            Map<String, Object> statData = new HashMap<>();
            statData.put("apiPath", apiPath);
            statData.put("totalCalls", stats.getTotalCalls());
            statData.put("averageResponseTime", Math.round(stats.getAverageResponseTime()));
            statData.put("minResponseTime", stats.getMinResponseTime());
            statData.put("maxResponseTime", stats.getMaxResponseTime());
            statData.put("successRate", Math.round(stats.getSuccessRate() * 100) / 100.0);
            statData.put("successCalls", stats.getSuccessCalls());
            statData.put("errorCalls", stats.getErrorCalls());
            statData.put("lastCallTime", stats.getLastCallTime());
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取API性能统计成功");
            response.put("data", statData);
            response.put("success", true);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse("获取API性能统计失败：" + e.getMessage()));
        }
    }
    
    /**
     * 获取性能报告
     * GET /api/performance/report
     */
    @GetMapping("/report")
    public ResponseEntity<Map<String, Object>> getPerformanceReport() {
        try {
            PerformanceMonitor.SystemPerformanceMetrics systemMetrics = performanceMonitor.getSystemMetrics();
            Map<String, PerformanceMonitor.ApiPerformanceStats> apiStats = performanceMonitor.getAllApiStats();
            
            // 生成性能报告
            Map<String, Object> report = new HashMap<>();
            
            // 系统概览
            Map<String, Object> overview = new HashMap<>();
            overview.put("totalRequests", systemMetrics.getTotalRequests());
            overview.put("totalErrors", systemMetrics.getTotalErrors());
            overview.put("errorRate", Math.round(systemMetrics.getErrorRate() * 100) / 100.0);
            overview.put("averageResponseTime", Math.round(systemMetrics.getAverageResponseTime()));
            overview.put("activeApis", systemMetrics.getActiveApis());
            report.put("overview", overview);
            
            // 性能分析
            Map<String, Object> analysis = new HashMap<>();
            
            // 找出最慢的API
            String slowestApi = null;
            double slowestTime = 0;
            for (Map.Entry<String, PerformanceMonitor.ApiPerformanceStats> entry : apiStats.entrySet()) {
                double avgTime = entry.getValue().getAverageResponseTime();
                if (avgTime > slowestTime) {
                    slowestTime = avgTime;
                    slowestApi = entry.getKey();
                }
            }
            analysis.put("slowestApi", slowestApi);
            analysis.put("slowestApiTime", Math.round(slowestTime));
            
            // 找出错误率最高的API
            String errorProneApi = null;
            double highestErrorRate = 0;
            for (Map.Entry<String, PerformanceMonitor.ApiPerformanceStats> entry : apiStats.entrySet()) {
                double errorRate = 100.0 - entry.getValue().getSuccessRate();
                if (errorRate > highestErrorRate) {
                    highestErrorRate = errorRate;
                    errorProneApi = entry.getKey();
                }
            }
            analysis.put("errorProneApi", errorProneApi);
            analysis.put("highestErrorRate", Math.round(highestErrorRate * 100) / 100.0);
            
            // 找出最热门的API
            String popularApi = null;
            long mostCalls = 0;
            for (Map.Entry<String, PerformanceMonitor.ApiPerformanceStats> entry : apiStats.entrySet()) {
                long calls = entry.getValue().getTotalCalls();
                if (calls > mostCalls) {
                    mostCalls = calls;
                    popularApi = entry.getKey();
                }
            }
            analysis.put("popularApi", popularApi);
            analysis.put("popularApiCalls", mostCalls);
            
            report.put("analysis", analysis);
            
            // 性能建议
            List<String> recommendations = new ArrayList<>();
            if (systemMetrics.getErrorRate() > 5) {
                recommendations.add("系统错误率较高(" + Math.round(systemMetrics.getErrorRate() * 100) / 100.0 + "%)，建议检查错误日志");
            }
            if (systemMetrics.getAverageResponseTime() > 2000) {
                recommendations.add("系统平均响应时间较长(" + Math.round(systemMetrics.getAverageResponseTime()) + "ms)，建议优化性能");
            }
            if (slowestTime > 3000) {
                recommendations.add("API " + slowestApi + " 响应时间过长，建议优化");
            }
            if (recommendations.isEmpty()) {
                recommendations.add("系统性能良好，继续保持");
            }
            report.put("recommendations", recommendations);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取性能报告成功");
            response.put("data", report);
            response.put("success", true);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse("获取性能报告失败：" + e.getMessage()));
        }
    }
    
    /**
     * 重置性能统计数据
     * POST /api/performance/reset
     */
    @PostMapping("/reset")
    public ResponseEntity<Map<String, Object>> resetPerformanceStats() {
        try {
            performanceMonitor.reset();
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "性能统计数据已重置");
            response.put("data", null);
            response.put("success", true);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse("重置性能统计数据失败：" + e.getMessage()));
        }
    }
    
    /**
     * 创建错误响应
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 500);
        response.put("message", message);
        response.put("data", null);
        response.put("success", false);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}