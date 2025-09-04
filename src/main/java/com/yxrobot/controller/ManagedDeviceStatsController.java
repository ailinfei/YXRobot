package com.yxrobot.controller;

import com.yxrobot.dto.ManagedDeviceStatsDTO;
import com.yxrobot.service.ManagedDeviceStatsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 设备统计控制器
 * 适配前端统计卡片，提供设备统计数据API
 * 支持实时统计、按日期范围统计、状态分布统计等功能
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@RestController
@RequestMapping("/api/admin/devices")
@Validated
public class ManagedDeviceStatsController {
    
    private static final Logger logger = LoggerFactory.getLogger(ManagedDeviceStatsController.class);
    
    @Autowired
    private ManagedDeviceStatsService statsService;
    
    /**
     * 获取设备统计数据
     * 支持前端统计卡片显示，返回设备总数、在线设备、离线设备、故障设备等统计信息
     * 
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 设备统计数据
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDeviceStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        logger.info("获取设备统计数据 - 开始时间: {}, 结束时间: {}", startDate, endDate);
        
        try {
            ManagedDeviceStatsDTO stats = statsService.getManagedDeviceStats(startDate, endDate);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", stats);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取设备统计数据失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 获取实时设备统计数据
     * 用于前端页面的实时刷新，返回最新的设备统计信息
     * 
     * @return 实时统计数据
     */
    @GetMapping("/stats/realtime")
    public ResponseEntity<Map<String, Object>> getRealTimeStats() {
        logger.info("获取实时设备统计数据");
        
        try {
            ManagedDeviceStatsDTO stats = statsService.getRealTimeStats();
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", stats);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取实时设备统计数据失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 获取设备状态分布统计
     * 返回各种设备状态的数量分布
     * 
     * @return 状态分布统计
     */
    @GetMapping("/stats/status-distribution")
    public ResponseEntity<Map<String, Object>> getStatusDistribution() {
        logger.info("获取设备状态分布统计");
        
        try {
            Map<String, Integer> distribution = statsService.getStatusDistribution();
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", distribution);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取设备状态分布统计失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 获取设备型号分布统计
     * 返回各种设备型号的数量分布
     * 
     * @return 型号分布统计
     */
    @GetMapping("/stats/model-distribution")
    public ResponseEntity<Map<String, Object>> getModelDistribution() {
        logger.info("获取设备型号分布统计");
        
        try {
            Map<String, Integer> distribution = statsService.getModelDistribution();
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", distribution);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取设备型号分布统计失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 获取统计数据摘要
     * 返回统计数据的文本摘要，用于日志记录和监控
     * 
     * @return 统计摘要
     */
    @GetMapping("/stats/summary")
    public ResponseEntity<Map<String, Object>> getStatsSummary() {
        logger.info("获取统计数据摘要");
        
        try {
            String summary = statsService.getStatsSummary();
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            
            Map<String, Object> data = new HashMap<>();
            data.put("summary", summary);
            data.put("timestamp", LocalDateTime.now());
            
            response.put("data", data);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取统计数据摘要失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 检查统计数据是否为空
     * 用于前端判断是否显示空状态页面
     * 
     * @return 统计数据状态
     */
    @GetMapping("/stats/check")
    public ResponseEntity<Map<String, Object>> checkStatsEmpty() {
        logger.info("检查统计数据是否为空");
        
        try {
            ManagedDeviceStatsDTO stats = statsService.getManagedDeviceStats();
            boolean isEmpty = statsService.isStatsEmpty(stats);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            
            Map<String, Object> data = new HashMap<>();
            data.put("isEmpty", isEmpty);
            data.put("totalDevices", stats != null ? stats.getTotal() : 0);
            data.put("timestamp", LocalDateTime.now());
            
            response.put("data", data);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("检查统计数据状态失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 获取设备统计趋势数据
     * 返回按时间段的设备统计趋势（用于图表显示）
     * 
     * @param days 统计天数（默认7天）
     * @return 趋势统计数据
     */
    @GetMapping("/stats/trend")
    public ResponseEntity<Map<String, Object>> getStatsTrend(
            @RequestParam(defaultValue = "7") Integer days) {
        
        logger.info("获取设备统计趋势数据 - 统计天数: {}", days);
        
        try {
            // 计算时间范围
            LocalDateTime endDate = LocalDateTime.now();
            LocalDateTime startDate = endDate.minusDays(days);
            
            // 获取趋势数据（这里简化处理，实际项目中可能需要按天分组统计）
            ManagedDeviceStatsDTO currentStats = statsService.getManagedDeviceStats(startDate, endDate);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            
            Map<String, Object> data = new HashMap<>();
            data.put("period", days + "天");
            data.put("startDate", startDate);
            data.put("endDate", endDate);
            data.put("stats", currentStats);
            
            // 模拟趋势数据（实际项目中应该从数据库查询历史数据）
            Map<String, Object> trend = new HashMap<>();
            trend.put("totalTrend", "+5%");
            trend.put("onlineTrend", "+3%");
            trend.put("offlineTrend", "-2%");
            trend.put("errorTrend", "+1%");
            data.put("trend", trend);
            
            response.put("data", data);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取设备统计趋势数据失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 刷新统计数据缓存
     * 用于手动刷新统计数据（如果有缓存的话）
     * 
     * @return 刷新结果
     */
    @PostMapping("/stats/refresh")
    public ResponseEntity<Map<String, Object>> refreshStats() {
        logger.info("刷新设备统计数据缓存");
        
        try {
            // 重新计算统计数据
            ManagedDeviceStatsDTO stats = statsService.getRealTimeStats();
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "统计数据已刷新");
            
            Map<String, Object> data = new HashMap<>();
            data.put("refreshTime", LocalDateTime.now());
            data.put("stats", stats);
            
            response.put("data", data);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("刷新设备统计数据失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "刷新失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}