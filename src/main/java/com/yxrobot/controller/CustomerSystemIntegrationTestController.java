package com.yxrobot.controller;

import com.yxrobot.service.CustomerSystemIntegrationTestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 客户系统集成测试控制器
 * 提供系统集成测试接口，验证前后端功能完整性
 */
@RestController
@RequestMapping("/api/admin/system/integration-test")
@CrossOrigin(origins = "*")
public class CustomerSystemIntegrationTestController {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerSystemIntegrationTestController.class);
    
    @Autowired
    private CustomerSystemIntegrationTestService integrationTestService;
    
    /**
     * 执行完整的系统集成测试
     * 验证客户管理模块的所有功能
     */
    @PostMapping("/execute")
    public ResponseEntity<Map<String, Object>> executeIntegrationTest() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("开始执行客户管理系统集成测试");
            
            CustomerSystemIntegrationTestService.IntegrationTestResult result = 
                integrationTestService.executeFullIntegrationTest();
            
            response.put("code", 200);
            response.put("message", "集成测试执行完成");
            response.put("data", Map.of(
                "summary", result.getSummary(),
                "totalTests", result.getTotalTests(),
                "passedTests", result.getPassedTests(),
                "failedTests", result.getFailedTests(),
                "passRate", result.getPassRate(),
                "allPassed", result.isAllPassed(),
                "executionTime", result.getTotalExecutionTime(),
                "testCases", result.getTestCases().stream()
                    .map(tc -> Map.of(
                        "name", tc.getName(),
                        "passed", tc.isPassed(),
                        "message", tc.getMessage(),
                        "status", tc.isPassed() ? "PASS" : "FAIL"
                    ))
                    .toList()
            ));
            
            // 如果有测试失败，记录警告
            if (!result.isAllPassed()) {
                logger.warn("集成测试存在失败项，通过率: {}%", result.getPassRate());
                response.put("warning", "部分测试未通过，请检查失败项");
            } else {
                logger.info("集成测试全部通过，系统功能正常");
            }
            
        } catch (Exception e) {
            logger.error("集成测试执行异常", e);
            response.put("code", 500);
            response.put("message", "集成测试执行失败: " + e.getMessage());
            response.put("data", null);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取集成测试状态
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getTestStatus() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            response.put("code", 200);
            response.put("message", "测试状态查询成功");
            response.put("data", Map.of(
                "available", true,
                "description", "客户管理系统集成测试",
                "testCategories", java.util.List.of(
                    "数据库连接测试",
                    "客户统计API测试",
                    "客户列表API测试",
                    "客户CRUD操作测试",
                    "数据格式匹配测试",
                    "字段映射测试",
                    "API性能测试",
                    "错误处理测试",
                    "空数据状态测试",
                    "前端功能支持测试"
                )
            ));
            
        } catch (Exception e) {
            logger.error("获取测试状态失败", e);
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 执行快速健康检查
     */
    @GetMapping("/health-check")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 执行基础健康检查
            long startTime = System.currentTimeMillis();
            
            // 检查数据库连接
            boolean dbConnected = true;
            try {
                integrationTestService.executeFullIntegrationTest();
            } catch (Exception e) {
                dbConnected = false;
            }
            
            long responseTime = System.currentTimeMillis() - startTime;
            
            response.put("code", 200);
            response.put("message", "健康检查完成");
            response.put("data", Map.of(
                "status", dbConnected ? "healthy" : "unhealthy",
                "database", dbConnected ? "connected" : "disconnected",
                "responseTime", responseTime + "ms",
                "timestamp", java.time.LocalDateTime.now().toString()
            ));
            
        } catch (Exception e) {
            logger.error("健康检查失败", e);
            response.put("code", 500);
            response.put("message", "健康检查失败: " + e.getMessage());
            response.put("data", Map.of(
                "status", "error",
                "error", e.getMessage()
            ));
        }
        
        return ResponseEntity.ok(response);
    }
}