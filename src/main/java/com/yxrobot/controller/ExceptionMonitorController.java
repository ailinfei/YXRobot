package com.yxrobot.controller;

import com.yxrobot.exception.ExceptionMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 异常监控控制器
 * 提供异常统计和监控数据的API接口
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@RestController
@RequestMapping("/api/admin/exception-monitor")
public class ExceptionMonitorController {
    
    @Autowired
    private ExceptionMonitor exceptionMonitor;
    
    /**
     * 获取异常统计信息
     * 
     * @return 异常统计数据
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getExceptionStatistics() {
        try {
            Map<String, Object> statistics = exceptionMonitor.getExceptionStatistics();
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取异常统计成功");
            response.put("data", statistics);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "获取异常统计失败: " + e.getMessage());
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取新闻相关异常统计
     * 
     * @return 新闻异常统计数据
     */
    @GetMapping("/news-statistics")
    public ResponseEntity<Map<String, Object>> getNewsExceptionStatistics() {
        try {
            Map<String, Long> newsStats = exceptionMonitor.getNewsExceptionStatistics();
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取新闻异常统计成功");
            response.put("data", newsStats);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "获取新闻异常统计失败: " + e.getMessage());
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 检查系统健康状态
     * 
     * @return 系统健康状态
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> checkSystemHealth() {
        try {
            boolean isHealthy = exceptionMonitor.isSystemHealthy();
            
            Map<String, Object> healthData = new HashMap<>();
            healthData.put("healthy", isHealthy);
            healthData.put("status", isHealthy ? "正常" : "异常");
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "系统健康检查完成");
            response.put("data", healthData);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "系统健康检查失败: " + e.getMessage());
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 重置异常统计
     * 
     * @return 操作结果
     */
    @PostMapping("/reset")
    public ResponseEntity<Map<String, Object>> resetStatistics() {
        try {
            exceptionMonitor.resetStatistics();
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "异常统计已重置");
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "重置异常统计失败: " + e.getMessage());
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取异常监控仪表板数据
     * 
     * @return 仪表板数据
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        try {
            Map<String, Object> statistics = exceptionMonitor.getExceptionStatistics();
            Map<String, Long> newsStats = exceptionMonitor.getNewsExceptionStatistics();
            boolean isHealthy = exceptionMonitor.isSystemHealthy();
            
            // 构建仪表板数据
            Map<String, Object> dashboardData = new HashMap<>();
            dashboardData.put("systemHealth", isHealthy);
            dashboardData.put("overallStatistics", statistics);
            dashboardData.put("newsExceptionStats", newsStats);
            
            // 计算总异常数
            @SuppressWarnings("unchecked")
            Map<String, Long> exceptionCounts = (Map<String, Long>) statistics.get("exceptionCounts");
            long totalExceptions = exceptionCounts != null ? 
                exceptionCounts.values().stream().mapToLong(Long::longValue).sum() : 0;
            dashboardData.put("totalExceptions", totalExceptions);
            
            // 计算新闻异常总数
            long totalNewsExceptions = newsStats.values().stream().mapToLong(Long::longValue).sum();
            dashboardData.put("totalNewsExceptions", totalNewsExceptions);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取仪表板数据成功");
            response.put("data", dashboardData);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "获取仪表板数据失败: " + e.getMessage());
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
}