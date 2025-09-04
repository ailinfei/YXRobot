package com.yxrobot.controller;

import com.yxrobot.util.DatabaseOptimizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库性能监控控制器
 * 提供数据库性能分析和优化接口
 */
@RestController
@RequestMapping("/api/database")
public class DatabasePerformanceController {
    
    @Autowired
    private DatabaseOptimizer databaseOptimizer;
    
    /**
     * 分析数据库性能
     * GET /api/database/analyze
     */
    @GetMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyzeDatabasePerformance() {
        try {
            Map<String, Object> analysis = databaseOptimizer.analyzeOrderTablePerformance();
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "数据库性能分析完成");
            response.put("data", analysis);
            response.put("success", true);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse("数据库性能分析失败：" + e.getMessage()));
        }
    }
    
    /**
     * 优化数据库
     * POST /api/database/optimize
     */
    @PostMapping("/optimize")
    public ResponseEntity<Map<String, Object>> optimizeDatabase() {
        try {
            Map<String, Object> result = databaseOptimizer.optimizeDatabase();
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "数据库优化完成");
            response.put("data", result);
            response.put("success", (Boolean) result.get("success"));
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse("数据库优化失败：" + e.getMessage()));
        }
    }
    
    /**
     * 检查必要的索引
     * GET /api/database/indexes/check
     */
    @GetMapping("/indexes/check")
    public ResponseEntity<Map<String, Object>> checkRequiredIndexes() {
        try {
            Map<String, Object> result = databaseOptimizer.checkRequiredIndexes();
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "索引检查完成");
            response.put("data", result);
            response.put("success", true);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse("索引检查失败：" + e.getMessage()));
        }
    }
    
    /**
     * 生成创建索引的SQL语句
     * POST /api/database/indexes/generate-sql
     */
    @PostMapping("/indexes/generate-sql")
    public ResponseEntity<Map<String, Object>> generateCreateIndexSql(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<String> missingIndexes = (List<String>) request.get("missingIndexes");
            
            if (missingIndexes == null || missingIndexes.isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("缺失索引列表不能为空"));
            }
            
            List<String> sqlStatements = databaseOptimizer.generateCreateIndexSql(missingIndexes);
            
            Map<String, Object> result = new HashMap<>();
            result.put("sqlStatements", sqlStatements);
            result.put("count", sqlStatements.size());
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "SQL语句生成完成");
            response.put("data", result);
            response.put("success", true);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse("SQL语句生成失败：" + e.getMessage()));
        }
    }
    
    /**
     * 获取数据库健康状态
     * GET /api/database/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getDatabaseHealth() {
        try {
            Map<String, Object> health = new HashMap<>();
            
            // 检查索引状态
            Map<String, Object> indexCheck = databaseOptimizer.checkRequiredIndexes();
            boolean allIndexesExist = (Boolean) indexCheck.getOrDefault("allIndexesExist", false);
            health.put("indexesHealthy", allIndexesExist);
            
            // 分析表大小
            Map<String, Object> analysis = databaseOptimizer.analyzeOrderTablePerformance();
            @SuppressWarnings("unchecked")
            Map<String, Object> tableSize = (Map<String, Object>) analysis.get("tableSize");
            Long orderCount = (Long) tableSize.get("orderCount");
            
            // 评估健康状态
            String healthStatus;
            if (allIndexesExist && orderCount != null && orderCount < 1000000) {
                healthStatus = "healthy";
            } else if (allIndexesExist || (orderCount != null && orderCount < 5000000)) {
                healthStatus = "warning";
            } else {
                healthStatus = "critical";
            }
            
            health.put("status", healthStatus);
            health.put("orderCount", orderCount);
            health.put("missingIndexes", indexCheck.get("missingIndexes"));
            health.put("recommendations", analysis.get("recommendations"));
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "数据库健康检查完成");
            response.put("data", health);
            response.put("success", true);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse("数据库健康检查失败：" + e.getMessage()));
        }
    }
    
    /**
     * 获取数据库统计信息
     * GET /api/database/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDatabaseStats() {
        try {
            Map<String, Object> analysis = databaseOptimizer.analyzeOrderTablePerformance();
            
            // 提取统计信息
            @SuppressWarnings("unchecked")
            Map<String, Object> tableSize = (Map<String, Object>) analysis.get("tableSize");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> indexUsage = (List<Map<String, Object>>) analysis.get("indexUsage");
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("tableSize", tableSize);
            stats.put("indexCount", indexUsage.size());
            stats.put("lastAnalyzed", System.currentTimeMillis());
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "数据库统计信息获取成功");
            response.put("data", stats);
            response.put("success", true);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse("获取数据库统计信息失败：" + e.getMessage()));
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