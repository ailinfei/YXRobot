package com.yxrobot.controller;

import com.yxrobot.dto.LinkClickLogDTO;
import com.yxrobot.service.LinkClickService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 链接点击统计控制器
 * 处理链接点击和转化跟踪相关的HTTP请求
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-22
 */
@RestController
@RequestMapping("/api/platform-links/clicks")
@Validated
public class LinkClickController {
    
    private static final Logger logger = LoggerFactory.getLogger(LinkClickController.class);
    
    @Autowired
    private LinkClickService linkClickService;
    
    /**
     * 记录链接点击事件
     * 
     * @param id 链接ID
     * @param request HTTP请求对象
     * @return 点击记录结果
     */
    @PostMapping("/{id}/click")
    public ResponseEntity<Map<String, Object>> recordClick(
            @PathVariable @NotNull Long id,
            HttpServletRequest request) {
        
        logger.info("记录链接点击事件 - 链接ID: {}", id);
        
        try {
            LinkClickLogDTO clickLog = linkClickService.recordClick(id, request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "点击记录成功");
            response.put("data", clickLog);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("记录链接点击事件失败 - 链接ID: {}, 错误: {}", id, e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(400).body(response);
            
        } catch (Exception e) {
            logger.error("记录链接点击事件失败 - 链接ID: {}", id, e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "点击记录失败: " + e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 记录转化事件
     * 
     * @param id 链接ID
     * @param requestBody 请求体，包含转化信息
     * @param request HTTP请求对象
     * @return 转化记录结果
     */
    @PostMapping("/{id}/conversion")
    public ResponseEntity<Map<String, Object>> recordConversion(
            @PathVariable @NotNull Long id,
            @RequestBody Map<String, Object> requestBody,
            HttpServletRequest request) {
        
        logger.info("记录转化事件 - 链接ID: {}", id);
        
        try {
            String conversionType = (String) requestBody.get("conversionType");
            BigDecimal conversionValue = null;
            
            if (requestBody.containsKey("conversionValue")) {
                Object value = requestBody.get("conversionValue");
                if (value instanceof Number) {
                    conversionValue = new BigDecimal(value.toString());
                } else if (value instanceof String) {
                    conversionValue = new BigDecimal((String) value);
                }
            }
            
            LinkClickLogDTO clickLog = linkClickService.recordConversion(id, conversionType, conversionValue, request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "转化记录成功");
            response.put("data", clickLog);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("记录转化事件失败 - 链接ID: {}", id, e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "转化记录失败: " + e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取点击趋势数据
     * 
     * @param id 链接ID（可选）
     * @param days 天数
     * @return 趋势数据
     */
    @GetMapping("/{id}/click-trends")
    public ResponseEntity<Map<String, Object>> getClickTrends(
            @PathVariable(required = false) Long id,
            @RequestParam(defaultValue = "30") Integer days) {
        
        logger.info("获取点击趋势数据 - 链接ID: {}, 天数: {}", id, days);
        
        try {
            List<Map<String, Object>> trends = linkClickService.getClickTrends(id, days);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", trends);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取点击趋势数据失败", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取转化趋势数据
     * 
     * @param id 链接ID（可选）
     * @param days 天数
     * @return 趋势数据
     */
    @GetMapping("/{id}/conversion-trends")
    public ResponseEntity<Map<String, Object>> getConversionTrends(
            @PathVariable(required = false) Long id,
            @RequestParam(defaultValue = "30") Integer days) {
        
        logger.info("获取转化趋势数据 - 链接ID: {}, 天数: {}", id, days);
        
        try {
            List<Map<String, Object>> trends = linkClickService.getConversionTrends(id, days);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", trends);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取转化趋势数据失败", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取转化率统计
     * 
     * @param id 链接ID（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 转化率统计
     */
    @GetMapping("/{id}/conversion-rates")
    public ResponseEntity<Map<String, Object>> getConversionRates(
            @PathVariable(required = false) Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        logger.info("获取转化率统计 - 链接ID: {}, 开始时间: {}, 结束时间: {}", id, startTime, endTime);
        
        try {
            Map<String, Object> conversionRates = linkClickService.getConversionRates(id, startTime, endTime);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", conversionRates);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取转化率统计失败", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取用户设备统计
     * 
     * @param id 链接ID（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 设备统计数据
     */
    @GetMapping("/{id}/device-stats")
    public ResponseEntity<Map<String, Object>> getDeviceStats(
            @PathVariable(required = false) Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        logger.info("获取用户设备统计 - 链接ID: {}, 开始时间: {}, 结束时间: {}", id, startTime, endTime);
        
        try {
            List<Map<String, Object>> deviceStats = linkClickService.getDeviceStats(id, startTime, endTime);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", deviceStats);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取用户设备统计失败", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取来源统计
     * 
     * @param id 链接ID（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 来源统计数据
     */
    @GetMapping("/{id}/referer-stats")
    public ResponseEntity<Map<String, Object>> getRefererStats(
            @PathVariable(required = false) Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        logger.info("获取来源统计 - 链接ID: {}, 开始时间: {}, 结束时间: {}", id, startTime, endTime);
        
        try {
            List<Map<String, Object>> refererStats = linkClickService.getRefererStats(id, startTime, endTime);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", refererStats);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取来源统计失败", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取链接的点击历史
     * 
     * @param id 链接ID
     * @param limit 限制数量
     * @return 点击历史列表
     */
    @GetMapping("/{id}/click-history")
    public ResponseEntity<Map<String, Object>> getClickHistory(
            @PathVariable @NotNull Long id,
            @RequestParam(defaultValue = "50") Integer limit) {
        
        logger.info("获取链接点击历史 - 链接ID: {}, 限制: {}", id, limit);
        
        try {
            List<LinkClickLogDTO> history = linkClickService.getClickHistory(id, limit);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", history);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取链接点击历史失败", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    

}