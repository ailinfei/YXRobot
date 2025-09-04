package com.yxrobot.controller;

import com.yxrobot.dto.OrderStatsDTO;
import com.yxrobot.service.OrderStatsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * 订单统计控制器
 * 适配前端统计卡片显示需求
 * 提供订单统计数据API接口
 * 
 * 主要功能：
 * 1. 获取订单统计数据 - 支持前端统计卡片显示
 * 2. 按日期范围统计 - 支持前端时间筛选功能
 * 3. 订单状态分布统计 - 支持前端图表显示
 * 4. 订单类型分布统计 - 支持前端分类统计
 * 5. KPI指标计算 - 支持前端关键指标显示
 * 
 * API响应格式：
 * {
 *   "code": 200,
 *   "message": "操作成功",
 *   "data": {统计数据}
 * }
 * 
 * 字段映射规范：
 * - 数据库字段：snake_case（如：total_amount, order_status）
 * - Java控制器：camelCase（如：totalAmount, orderStatus）
 * - 前端接口：camelCase（如：totalAmount, orderStatus）
 */
@RestController
@RequestMapping("/api/admin/orders")
@CrossOrigin(origins = "*")
public class OrderStatsController {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderStatsController.class);
    
    @Autowired
    private OrderStatsService orderStatsService;
    
    /**
     * 获取订单统计数据
     * 支持前端统计卡片显示：订单总数、总收入、处理中订单、已完成订单
     * 
     * GET /api/admin/orders/stats
     * 
     * @param startDate 可选的开始日期，格式：yyyy-MM-dd
     * @param endDate 可选的结束日期，格式：yyyy-MM-dd
     * @return 订单统计数据
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getOrderStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        try {
            logger.info("获取订单统计数据请求，日期范围: {} - {}", startDate, endDate);
            
            OrderStatsDTO stats;
            
            // 根据是否有日期范围参数选择不同的查询方法
            if (startDate != null || endDate != null) {
                stats = orderStatsService.getOrderStatsByDateRange(startDate, endDate);
                logger.debug("按日期范围获取统计数据完成");
            } else {
                stats = orderStatsService.getOrderStats();
                logger.debug("获取全部统计数据完成");
            }
            
            // 构建响应数据
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", stats);
            
            logger.info("订单统计数据返回成功，总订单数: {}, 总收入: {}", 
                       stats.getTotal(), stats.getTotalRevenue());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取订单统计数据失败", e);
            
            // 发生异常时返回空统计数据，确保前端正常显示
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "获取统计数据失败: " + e.getMessage());
            response.put("data", new OrderStatsDTO()); // 返回空统计对象
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取订单状态分布统计
     * 支持前端状态分布图表显示
     * 
     * GET /api/admin/orders/stats/status-distribution
     * 
     * @return 订单状态分布数据
     */
    @GetMapping("/stats/status-distribution")
    public ResponseEntity<Map<String, Object>> getOrderStatusDistribution() {
        try {
            logger.info("获取订单状态分布统计请求");
            
            Map<String, Integer> distribution = orderStatsService.getOrderStatusDistribution();
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", distribution);
            
            logger.debug("订单状态分布统计返回成功");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取订单状态分布统计失败", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "获取状态分布统计失败: " + e.getMessage());
            response.put("data", new HashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取订单类型分布统计
     * 支持前端类型分布图表显示
     * 
     * GET /api/admin/orders/stats/type-distribution
     * 
     * @return 订单类型分布数据
     */
    @GetMapping("/stats/type-distribution")
    public ResponseEntity<Map<String, Object>> getOrderTypeDistribution() {
        try {
            logger.info("获取订单类型分布统计请求");
            
            Map<String, Integer> distribution = orderStatsService.getOrderTypeDistribution();
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", distribution);
            
            logger.debug("订单类型分布统计返回成功");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取订单类型分布统计失败", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "获取类型分布统计失败: " + e.getMessage());
            response.put("data", new HashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取订单KPI指标
     * 支持前端关键指标显示：完成率、取消率、处理中订单数
     * 
     * GET /api/admin/orders/stats/kpi
     * 
     * @return 订单KPI指标数据
     */
    @GetMapping("/stats/kpi")
    public ResponseEntity<Map<String, Object>> getOrderKPI() {
        try {
            logger.info("获取订单KPI指标请求");
            
            // 获取各项KPI指标
            BigDecimal completionRate = orderStatsService.calculateCompletionRate();
            BigDecimal cancellationRate = orderStatsService.calculateCancellationRate();
            Integer pendingOrdersCount = orderStatsService.getPendingOrdersCount();
            
            // 构建KPI数据
            Map<String, Object> kpiData = new HashMap<>();
            kpiData.put("completionRate", completionRate);      // 完成率
            kpiData.put("cancellationRate", cancellationRate);  // 取消率
            kpiData.put("pendingOrdersCount", pendingOrdersCount); // 待处理订单数
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", kpiData);
            
            logger.debug("订单KPI指标返回成功，完成率: {}%, 取消率: {}%, 待处理: {}", 
                        completionRate, cancellationRate, pendingOrdersCount);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取订单KPI指标失败", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "获取KPI指标失败: " + e.getMessage());
            response.put("data", new HashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取简化的统计数据
     * 专门为前端统计卡片优化的接口
     * 只返回前端统计卡片需要的核心数据
     * 
     * GET /api/admin/orders/stats/summary
     * 
     * @return 简化的统计数据
     */
    @GetMapping("/stats/summary")
    public ResponseEntity<Map<String, Object>> getOrderStatsSummary() {
        try {
            logger.info("获取简化订单统计数据请求");
            
            OrderStatsDTO stats = orderStatsService.getOrderStats();
            
            // 构建前端统计卡片需要的数据结构
            Map<String, Object> summaryData = new HashMap<>();
            summaryData.put("totalOrders", stats.getTotal());           // 订单总数
            summaryData.put("totalRevenue", stats.getTotalRevenue());   // 总收入
            summaryData.put("processingOrders", stats.getPending() + stats.getConfirmed() + stats.getProcessing()); // 处理中订单
            summaryData.put("completedOrders", stats.getCompleted());   // 已完成订单
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", summaryData);
            
            logger.debug("简化订单统计数据返回成功");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取简化订单统计数据失败", e);
            
            // 返回空数据确保前端正常显示
            Map<String, Object> summaryData = new HashMap<>();
            summaryData.put("totalOrders", 0);
            summaryData.put("totalRevenue", BigDecimal.ZERO);
            summaryData.put("processingOrders", 0);
            summaryData.put("completedOrders", 0);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "获取简化统计数据失败: " + e.getMessage());
            response.put("data", summaryData);
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 健康检查接口
     * 用于验证统计服务是否正常工作
     * 
     * GET /api/admin/orders/stats/health
     * 
     * @return 服务健康状态
     */
    @GetMapping("/stats/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        try {
            logger.debug("订单统计服务健康检查请求");
            
            // 尝试获取统计数据以验证服务是否正常
            OrderStatsDTO stats = orderStatsService.getOrderStats();
            
            Map<String, Object> healthData = new HashMap<>();
            healthData.put("status", "healthy");
            healthData.put("service", "OrderStatsService");
            healthData.put("timestamp", System.currentTimeMillis());
            healthData.put("dataAvailable", stats.getTotal() > 0);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "服务正常");
            response.put("data", healthData);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("订单统计服务健康检查失败", e);
            
            Map<String, Object> healthData = new HashMap<>();
            healthData.put("status", "unhealthy");
            healthData.put("service", "OrderStatsService");
            healthData.put("timestamp", System.currentTimeMillis());
            healthData.put("error", e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "服务异常");
            response.put("data", healthData);
            
            return ResponseEntity.status(500).body(response);
        }
    }
}