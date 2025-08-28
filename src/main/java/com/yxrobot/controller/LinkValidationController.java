package com.yxrobot.controller;

import com.yxrobot.dto.LinkValidationResultDTO;
import com.yxrobot.entity.LinkValidationLog;
import com.yxrobot.service.LinkValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 链接验证控制器
 * 处理链接验证相关的HTTP请求
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-22
 */
@RestController
@RequestMapping("/api/platform-links/validation")
@Validated
public class LinkValidationController {
    
    private static final Logger logger = LoggerFactory.getLogger(LinkValidationController.class);
    
    @Autowired
    private LinkValidationService linkValidationService;
    
    /**
     * 验证单个平台链接
     * 
     * @param id 链接ID
     * @return 验证结果
     */
    @PostMapping("/{id}/validate")
    public ResponseEntity<Map<String, Object>> validatePlatformLink(@PathVariable @NotNull Long id) {
        logger.info("验证平台链接 - ID: {}", id);
        
        try {
            LinkValidationResultDTO result = linkValidationService.validatePlatformLink(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "验证完成");
            response.put("data", result);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("验证平台链接失败 - ID: {}, 错误: {}", id, e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(400).body(response);
            
        } catch (Exception e) {
            logger.error("验证平台链接失败 - ID: {}", id, e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "验证失败: " + e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 异步验证单个平台链接
     * 
     * @param id 链接ID
     * @return 异步验证任务ID
     */
    @PostMapping("/{id}/validate-async")
    public ResponseEntity<Map<String, Object>> validatePlatformLinkAsync(@PathVariable @NotNull Long id) {
        logger.info("异步验证平台链接 - ID: {}", id);
        
        try {
            // 启动异步验证
            CompletableFuture<LinkValidationResultDTO> future = linkValidationService.validatePlatformLinkAsync(id);
            
            // 生成任务ID（实际项目中可以使用UUID或其他唯一标识）
            String taskId = "validation_" + id + "_" + System.currentTimeMillis();
            
            Map<String, Object> result = new HashMap<>();
            result.put("taskId", taskId);
            result.put("linkId", id);
            result.put("status", "processing");
            result.put("message", "验证任务已启动");
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "异步验证任务已启动");
            response.put("data", result);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("启动异步验证失败 - ID: {}", id, e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "启动异步验证失败: " + e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 批量验证平台链接
     * 
     * @param linkIds 链接ID列表
     * @return 批量验证结果
     */
    @PostMapping("/batch/validate")
    public ResponseEntity<Map<String, Object>> batchValidatePlatformLinks(@RequestBody List<Long> linkIds) {
        logger.info("批量验证平台链接 - 数量: {}", linkIds.size());
        
        try {
            List<LinkValidationResultDTO> results = linkValidationService.batchValidatePlatformLinks(linkIds);
            
            // 统计验证结果
            long successCount = results.stream().mapToLong(r -> r.getIsValid() ? 1 : 0).sum();
            long failedCount = results.size() - successCount;
            
            Map<String, Object> summary = new HashMap<>();
            summary.put("total", results.size());
            summary.put("success", successCount);
            summary.put("failed", failedCount);
            summary.put("results", results);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "批量验证完成");
            response.put("data", summary);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("批量验证平台链接失败", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "批量验证失败: " + e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取链接的验证历史
     * 
     * @param id 链接ID
     * @param limit 限制数量
     * @return 验证历史列表
     */
    @GetMapping("/{id}/validation-history")
    public ResponseEntity<Map<String, Object>> getValidationHistory(
            @PathVariable @NotNull Long id,
            @RequestParam(defaultValue = "20") Integer limit) {
        
        logger.info("获取链接验证历史 - ID: {}, 限制: {}", id, limit);
        
        try {
            List<LinkValidationLog> history = linkValidationService.getValidationHistory(id, limit);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", history);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取链接验证历史失败 - ID: {}", id, e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取链接的最新验证结果
     * 
     * @param id 链接ID
     * @return 最新验证结果
     */
    @GetMapping("/{id}/latest-validation")
    public ResponseEntity<Map<String, Object>> getLatestValidationResult(@PathVariable @NotNull Long id) {
        logger.info("获取链接最新验证结果 - ID: {}", id);
        
        try {
            LinkValidationLog latest = linkValidationService.getLatestValidationResult(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", latest);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取链接最新验证结果失败 - ID: {}", id, e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 验证需要检查的链接（定时任务接口）
     * 
     * @param limit 限制数量
     * @return 验证结果
     */
    @PostMapping("/validate-scheduled")
    public ResponseEntity<Map<String, Object>> validateLinksForScheduledTask(
            @RequestParam(defaultValue = "50") Integer limit) {
        
        logger.info("定时验证链接 - 限制数量: {}", limit);
        
        try {
            List<LinkValidationResultDTO> results = linkValidationService.validateLinksForScheduledTask(limit);
            
            // 统计验证结果
            long successCount = results.stream().mapToLong(r -> r.getIsValid() ? 1 : 0).sum();
            long failedCount = results.size() - successCount;
            
            Map<String, Object> summary = new HashMap<>();
            summary.put("total", results.size());
            summary.put("success", successCount);
            summary.put("failed", failedCount);
            summary.put("results", results);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "定时验证完成");
            response.put("data", summary);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("定时验证链接失败", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "定时验证失败: " + e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    

}