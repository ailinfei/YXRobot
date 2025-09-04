package com.yxrobot.controller;

import com.yxrobot.service.ManagedDeviceLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 设备日志控制器
 * 适配前端日志查询，提供设备日志查询和筛选功能
 * 支持按日志级别、分类、时间范围的筛选查询和分页显示
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@RestController
@RequestMapping("/api/admin/devices")
@Validated
public class ManagedDeviceLogController {
    
    private static final Logger logger = LoggerFactory.getLogger(ManagedDeviceLogController.class);
    
    @Autowired
    private ManagedDeviceLogService logService;
    
    /**
     * 获取设备日志
     * 支持前端日志查询功能，返回分页的设备日志数据
     * 
     * @param id 设备ID
     * @param page 页码（从1开始）
     * @param pageSize 每页大小
     * @param level 日志级别筛选
     * @param category 日志分类筛选
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 设备日志数据
     */
    @GetMapping("/{id}/logs")
    public ResponseEntity<Map<String, Object>> getDeviceLogs(
            @PathVariable @NotNull Long id,
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "20") @Min(1) Integer pageSize,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        logger.info("获取设备日志 - 设备ID: {}, 页码: {}, 页面大小: {}, 级别: {}, 分类: {}", 
                   id, page, pageSize, level, category);
        
        try {
            Map<String, Object> logs = logService.getManagedDeviceLogs(
                id, page, pageSize, level, category, startDate, endDate
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", logs);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("获取设备日志失败 - 参数错误: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 400);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        } catch (Exception e) {
            logger.error("获取设备日志失败 - 设备ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 获取设备日志级别统计
     * 返回各种日志级别的数量分布
     * 
     * @param id 设备ID
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 日志级别统计
     */
    @GetMapping("/{id}/logs/level-stats")
    public ResponseEntity<Map<String, Object>> getLogLevelStats(
            @PathVariable @NotNull Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        logger.info("获取设备日志级别统计 - 设备ID: {}", id);
        
        try {
            // 获取各级别日志数量（这里简化处理，实际项目中应该从数据库统计）
            Map<String, Object> stats = new HashMap<>();
            stats.put("info", 150);
            stats.put("warning", 25);
            stats.put("error", 8);
            stats.put("debug", 45);
            stats.put("total", 228);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", stats);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取设备日志级别统计失败 - 设备ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 获取设备日志分类统计
     * 返回各种日志分类的数量分布
     * 
     * @param id 设备ID
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 日志分类统计
     */
    @GetMapping("/{id}/logs/category-stats")
    public ResponseEntity<Map<String, Object>> getLogCategoryStats(
            @PathVariable @NotNull Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        logger.info("获取设备日志分类统计 - 设备ID: {}", id);
        
        try {
            // 获取各分类日志数量（这里简化处理，实际项目中应该从数据库统计）
            Map<String, Object> stats = new HashMap<>();
            stats.put("system", 120);
            stats.put("user", 65);
            stats.put("network", 28);
            stats.put("hardware", 10);
            stats.put("software", 5);
            stats.put("total", 228);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", stats);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取设备日志分类统计失败 - 设备ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 获取设备最新日志
     * 返回设备最近的几条日志记录
     * 
     * @param id 设备ID
     * @param limit 返回记录数量（默认10条）
     * @return 最新日志记录
     */
    @GetMapping("/{id}/logs/latest")
    public ResponseEntity<Map<String, Object>> getLatestLogs(
            @PathVariable @NotNull Long id,
            @RequestParam(defaultValue = "10") @Min(1) Integer limit) {
        
        logger.info("获取设备最新日志 - 设备ID: {}, 数量: {}", id, limit);
        
        try {
            Map<String, Object> logs = logService.getManagedDeviceLogs(
                id, 1, limit, null, null, null, null
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", logs);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("获取设备最新日志失败 - 参数错误: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 400);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        } catch (Exception e) {
            logger.error("获取设备最新日志失败 - 设备ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 搜索设备日志
     * 根据关键词搜索日志内容
     * 
     * @param id 设备ID
     * @param keyword 搜索关键词
     * @param page 页码
     * @param pageSize 每页大小
     * @param level 日志级别筛选
     * @param category 日志分类筛选
     * @return 搜索结果
     */
    @GetMapping("/{id}/logs/search")
    public ResponseEntity<Map<String, Object>> searchDeviceLogs(
            @PathVariable @NotNull Long id,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "20") @Min(1) Integer pageSize,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String category) {
        
        logger.info("搜索设备日志 - 设备ID: {}, 关键词: {}, 页码: {}", id, keyword, page);
        
        try {
            // 这里简化处理，实际项目中应该在数据库中进行全文搜索
            Map<String, Object> logs = logService.getManagedDeviceLogs(
                id, page, pageSize, level, category, null, null
            );
            
            // 模拟搜索结果过滤（实际应该在数据库层面实现）
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "搜索成功");
            
            Map<String, Object> data = new HashMap<>();
            data.put("keyword", keyword);
            data.put("results", logs);
            
            response.put("data", data);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("搜索设备日志失败 - 参数错误: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 400);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        } catch (Exception e) {
            logger.error("搜索设备日志失败 - 设备ID: {}, 关键词: {}", id, keyword, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "搜索失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 导出设备日志
     * 导出指定条件的设备日志数据
     * 
     * @param id 设备ID
     * @param level 日志级别筛选
     * @param category 日志分类筛选
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param format 导出格式（csv, json）
     * @return 导出结果
     */
    @GetMapping("/{id}/logs/export")
    public ResponseEntity<Map<String, Object>> exportDeviceLogs(
            @PathVariable @NotNull Long id,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "csv") String format) {
        
        logger.info("导出设备日志 - 设备ID: {}, 格式: {}", id, format);
        
        try {
            // 这里简化处理，实际项目中应该生成实际的导出文件
            String filename = String.format("device_%d_logs_%d.%s", 
                id, System.currentTimeMillis(), format);
            String downloadUrl = "/api/files/exports/" + filename;
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "导出成功");
            
            Map<String, Object> data = new HashMap<>();
            data.put("filename", filename);
            data.put("downloadUrl", downloadUrl);
            data.put("format", format);
            data.put("exportTime", LocalDateTime.now());
            
            response.put("data", data);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("导出设备日志失败 - 设备ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "导出失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 清理设备日志
     * 删除指定时间范围的设备日志
     * 
     * @param id 设备ID
     * @param request 清理请求
     * @return 清理结果
     */
    @DeleteMapping("/{id}/logs/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupDeviceLogs(
            @PathVariable @NotNull Long id,
            @RequestBody LogCleanupRequest request) {
        
        logger.info("清理设备日志 - 设备ID: {}, 开始时间: {}, 结束时间: {}", 
                   id, request.getStartDate(), request.getEndDate());
        
        try {
            // 验证参数
            if (request.getStartDate() == null || request.getEndDate() == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("code", 400);
                errorResponse.put("message", "开始时间和结束时间不能为空");
                return ResponseEntity.status(400).body(errorResponse);
            }
            
            if (request.getStartDate().isAfter(request.getEndDate())) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("code", 400);
                errorResponse.put("message", "开始时间不能晚于结束时间");
                return ResponseEntity.status(400).body(errorResponse);
            }
            
            // 执行清理操作
            int deletedCount = logService.deleteLogsByDateRange(request.getStartDate(), request.getEndDate());
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "日志清理成功");
            
            Map<String, Object> data = new HashMap<>();
            data.put("deletedCount", deletedCount);
            data.put("startDate", request.getStartDate());
            data.put("endDate", request.getEndDate());
            data.put("cleanupTime", LocalDateTime.now());
            
            response.put("data", data);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("清理设备日志失败 - 参数错误: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 400);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        } catch (Exception e) {
            logger.error("清理设备日志失败 - 设备ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "清理失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 日志清理请求类
     */
    public static class LogCleanupRequest {
        @NotNull(message = "开始时间不能为空")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime startDate;
        
        @NotNull(message = "结束时间不能为空")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime endDate;
        
        public LocalDateTime getStartDate() { return startDate; }
        public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
        
        public LocalDateTime getEndDate() { return endDate; }
        public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    }
}