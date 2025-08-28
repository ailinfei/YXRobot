package com.yxrobot.controller;

import com.yxrobot.dto.NewsDTO;
import com.yxrobot.entity.NewsStatus;
import com.yxrobot.service.NewsStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 新闻状态管理控制器
 * 处理新闻状态转换相关的HTTP请求
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@RestController
@RequestMapping("/api/admin/news/status")

public class NewsStatusController {
    
    private static final Logger logger = LoggerFactory.getLogger(NewsStatusController.class);
    
    @Autowired
    private NewsStatusService newsStatusService;
    
    /**
     * 发布新闻
     * 
     * @param id 新闻ID
     * @return 更新后的新闻信息
     */
    @PostMapping("/{id}/publish")
    public ResponseEntity<Map<String, Object>> publishNews(@PathVariable Long id) {
        logger.info("发布新闻请求 - ID: {}", id);
        
        NewsDTO newsDTO = newsStatusService.publishNews(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "新闻发布成功");
        response.put("data", newsDTO);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 下线新闻
     * 
     * @param id 新闻ID
     * @param request 请求体，包含下线原因
     * @return 更新后的新闻信息
     */
    @PostMapping("/{id}/offline")
    public ResponseEntity<Map<String, Object>> offlineNews(@PathVariable Long id, 
                                                          @RequestBody(required = false) Map<String, String> request) {
        logger.info("下线新闻请求 - ID: {}", id);
        
        String reason = null;
        if (request != null) {
            reason = request.get("reason");
        }
        
        NewsDTO newsDTO = newsStatusService.offlineNews(id, reason);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "新闻下线成功");
        response.put("data", newsDTO);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 转为草稿
     * 
     * @param id 新闻ID
     * @return 更新后的新闻信息
     */
    @PostMapping("/{id}/draft")
    public ResponseEntity<Map<String, Object>> draftNews(@PathVariable Long id) {
        logger.info("转为草稿请求 - ID: {}", id);
        
        NewsDTO newsDTO = newsStatusService.draftNews(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "新闻转为草稿成功");
        response.put("data", newsDTO);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 批量更新新闻状态
     * 
     * @param request 批量操作请求
     * @return 操作结果
     */
    @PostMapping("/batch-status")
    public ResponseEntity<Map<String, Object>> batchUpdateStatus(@RequestBody BatchStatusRequest request) {
        logger.info("批量更新状态请求 - 数量: {}, 目标状态: {}", 
                   request.getNewsIds().size(), request.getTargetStatus());
        
        int successCount = newsStatusService.batchUpdateStatus(request.getNewsIds(), request.getTargetStatus());
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "批量操作完成");
        response.put("data", Map.of(
            "successCount", successCount,
            "totalCount", request.getNewsIds().size(),
            "targetStatus", request.getTargetStatus()
        ));
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取可用的状态转换
     * 
     * @param id 新闻ID
     * @return 可转换的状态列表
     */
    @GetMapping("/{id}/available-transitions")
    public ResponseEntity<Map<String, Object>> getAvailableTransitions(@PathVariable Long id) {
        logger.info("获取可用状态转换请求 - ID: {}", id);
        
        List<NewsStatus> transitions = newsStatusService.getAvailableStatusTransitions(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取成功");
        response.put("data", transitions);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 检查新闻是否可以删除
     * 
     * @param id 新闻ID
     * @return 是否可以删除
     */
    @GetMapping("/{id}/can-delete")
    public ResponseEntity<Map<String, Object>> canDelete(@PathVariable Long id) {
        logger.info("检查删除权限请求 - ID: {}", id);
        
        boolean canDelete = newsStatusService.canDelete(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "检查完成");
        response.put("data", Map.of("canDelete", canDelete));
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取新闻状态统计
     * 
     * @return 状态统计信息
     */
    @GetMapping("/status-statistics")
    public ResponseEntity<Map<String, Object>> getStatusStatistics() {
        logger.info("获取状态统计请求");
        
        Map<NewsStatus, Integer> statistics = newsStatusService.getStatusStatistics();
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取成功");
        response.put("data", statistics);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 批量状态更新请求类
     */
    public static class BatchStatusRequest {
        private List<Long> newsIds;
        private NewsStatus targetStatus;
        
        public List<Long> getNewsIds() {
            return newsIds;
        }
        
        public void setNewsIds(List<Long> newsIds) {
            this.newsIds = newsIds;
        }
        
        public NewsStatus getTargetStatus() {
            return targetStatus;
        }
        
        public void setTargetStatus(NewsStatus targetStatus) {
            this.targetStatus = targetStatus;
        }
    }
}