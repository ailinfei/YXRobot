package com.yxrobot.controller;

import com.yxrobot.dto.DeviceMonitoringStatsDTO;
import com.yxrobot.dto.DeviceMonitoringDataDTO;
import com.yxrobot.dto.DeviceMapDataDTO;
import com.yxrobot.service.DeviceMonitoringStatsService;
import com.yxrobot.service.DeviceMonitoringService;
import com.yxrobot.service.DeviceMonitoringService.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备监控控制器
 * 提供RESTful API接口，适配前端DeviceMonitoring.vue页面调用
 * 
 * 主要接口：
 * - GET /api/admin/device/monitoring/stats - 获取监控统计数据
 * - GET /api/admin/device/monitoring/devices - 获取设备监控列表
 * - GET /api/admin/device/monitoring/device/{id} - 获取设备监控详情
 * - GET /api/admin/device/monitoring/map - 获取设备分布地图数据
 * 
 * @author YXRobot Development Team
 * @since 2024
 */
@RestController
@RequestMapping("/api/admin/device/monitoring")
@CrossOrigin(origins = "*")
public class DeviceMonitoringController {
    
    private static final Logger logger = LoggerFactory.getLogger(DeviceMonitoringController.class);
    
    @Autowired
    private DeviceMonitoringStatsService deviceMonitoringStatsService;
    
    @Autowired
    private DeviceMonitoringService deviceMonitoringService;   
 
    /**
     * 获取监控统计数据
     * 支持前端统计卡片显示
     * 
     * @return 统一响应格式的监控统计数据
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getMonitoringStats() {
        logger.info("获取监控统计数据");
        
        try {
            DeviceMonitoringStatsDTO stats = deviceMonitoringStatsService.getMonitoringStats();
            
            return ResponseEntity.ok(createSuccessResponse(stats, "获取监控统计数据成功"));
            
        } catch (Exception e) {
            logger.error("获取监控统计数据失败", e);
            return ResponseEntity.ok(createErrorResponse("获取监控统计数据失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取设备监控列表
     * 支持前端设备列表显示，包含分页、搜索、筛选功能
     * 
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @param keyword 搜索关键词
     * @param status 设备状态筛选
     * @param model 设备型号筛选
     * @param customerName 客户名称筛选
     * @return 统一响应格式的设备监控列表
     */
    @GetMapping("/devices")
    public ResponseEntity<Map<String, Object>> getMonitoringDevices(
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String customerName) {
        
        logger.info("获取设备监控列表: page={}, size={}, keyword={}, status={}", 
                   page, size, keyword, status);
        
        try {
            PageResult<DeviceMonitoringDataDTO> result = deviceMonitoringService
                    .getMonitoringDevices(page, size, keyword, status, model, customerName);
            
            // 构建分页响应数据
            Map<String, Object> pageData = new HashMap<>();
            pageData.put("list", result.getData());
            pageData.put("total", result.getTotal());
            pageData.put("page", result.getPage());
            pageData.put("size", result.getSize());
            pageData.put("totalPages", result.getTotalPages());
            
            return ResponseEntity.ok(createSuccessResponse(pageData, "获取设备监控列表成功"));
            
        } catch (Exception e) {
            logger.error("获取设备监控列表失败", e);
            return ResponseEntity.ok(createErrorResponse("获取设备监控列表失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取设备监控详情
     * 支持前端设备详情查看
     * 
     * @param id 设备ID
     * @return 统一响应格式的设备监控详情
     */
    @GetMapping("/device/{id}")
    public ResponseEntity<Map<String, Object>> getDeviceMonitoringById(@PathVariable Long id) {
        logger.info("获取设备监控详情: deviceId={}", id);
        
        try {
            DeviceMonitoringDataDTO device = deviceMonitoringService.getDeviceMonitoringById(id);
            
            if (device == null) {
                return ResponseEntity.ok(createErrorResponse("设备不存在或无监控数据"));
            }
            
            return ResponseEntity.ok(createSuccessResponse(device, "获取设备监控详情成功"));
            
        } catch (Exception e) {
            logger.error("获取设备监控详情失败: deviceId={}", id, e);
            return ResponseEntity.ok(createErrorResponse("获取设备监控详情失败: " + e.getMessage()));
        }
    }  
  
    /**
     * 获取设备分布地图数据
     * 支持前端地图组件显示
     * 
     * @return 统一响应格式的地图数据
     */
    @GetMapping("/map")
    public ResponseEntity<Map<String, Object>> getDeviceMapData() {
        logger.info("获取设备分布地图数据");
        
        try {
            List<DeviceMapDataDTO> mapData = deviceMonitoringService.getDeviceMapData();
            
            // 构建地图响应数据
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("devices", mapData);
            responseData.put("total", mapData.size());
            responseData.put("lastUpdateTime", java.time.LocalDateTime.now().toString());
            
            return ResponseEntity.ok(createSuccessResponse(responseData, "获取设备分布地图数据成功"));
            
        } catch (Exception e) {
            logger.error("获取设备分布地图数据失败", e);
            return ResponseEntity.ok(createErrorResponse("获取设备分布地图数据失败: " + e.getMessage()));
        }
    }
    
    /**
     * 搜索设备
     * 支持前端设备搜索功能
     * 
     * @param keyword 搜索关键词
     * @param limit 限制数量
     * @return 统一响应格式的搜索结果
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchDevices(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) Integer limit) {
        
        logger.info("搜索设备: keyword={}, limit={}", keyword, limit);
        
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return ResponseEntity.ok(createErrorResponse("搜索关键词不能为空"));
            }
            
            List<DeviceMonitoringDataDTO> devices = deviceMonitoringService
                    .searchDevices(keyword.trim(), limit);
            
            return ResponseEntity.ok(createSuccessResponse(devices, "设备搜索成功"));
            
        } catch (Exception e) {
            logger.error("搜索设备失败: keyword={}", keyword, e);
            return ResponseEntity.ok(createErrorResponse("设备搜索失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取历史统计数据
     * 支持前端历史趋势查看
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统一响应格式的历史统计数据
     */
    @GetMapping("/stats/history")
    public ResponseEntity<Map<String, Object>> getHistoryStats(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        
        logger.info("获取历史统计数据: startDate={}, endDate={}", startDate, endDate);
        
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            
            // 验证日期范围
            if (start.isAfter(end)) {
                return ResponseEntity.ok(createErrorResponse("开始日期不能晚于结束日期"));
            }
            
            if (start.isBefore(LocalDate.now().minusDays(90))) {
                return ResponseEntity.ok(createErrorResponse("查询范围不能超过90天"));
            }
            
            List<DeviceMonitoringStatsDTO> historyStats = deviceMonitoringStatsService
                    .getHistoryStats(start, end);
            
            return ResponseEntity.ok(createSuccessResponse(historyStats, "获取历史统计数据成功"));
            
        } catch (Exception e) {
            logger.error("获取历史统计数据失败: startDate={}, endDate={}", startDate, endDate, e);
            return ResponseEntity.ok(createErrorResponse("获取历史统计数据失败: " + e.getMessage()));
        }
    }    

    /**
     * 获取设备状态统计
     * 支持前端状态分布显示
     * 
     * @return 统一响应格式的状态统计数据
     */
    @GetMapping("/stats/status")
    public ResponseEntity<Map<String, Object>> getDeviceStatusStats() {
        logger.info("获取设备状态统计");
        
        try {
            Map<String, Integer> statusStats = deviceMonitoringService.getDeviceStatusStats();
            
            return ResponseEntity.ok(createSuccessResponse(statusStats, "获取设备状态统计成功"));
            
        } catch (Exception e) {
            logger.error("获取设备状态统计失败", e);
            return ResponseEntity.ok(createErrorResponse("获取设备状态统计失败: " + e.getMessage()));
        }
    }
    
    /**
     * 刷新监控数据
     * 支持前端手动刷新功能
     * 
     * @return 统一响应格式的刷新结果
     */
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshMonitoringData() {
        logger.info("刷新监控数据");
        
        try {
            // 保存当前统计数据
            DeviceMonitoringStatsDTO stats = deviceMonitoringStatsService.saveCurrentStats();
            
            // 同步设备信息
            int syncCount = deviceMonitoringService.syncDeviceInfo(null);
            
            Map<String, Object> result = new HashMap<>();
            result.put("stats", stats);
            result.put("syncCount", syncCount);
            result.put("refreshTime", java.time.LocalDateTime.now().toString());
            
            return ResponseEntity.ok(createSuccessResponse(result, "监控数据刷新成功"));
            
        } catch (Exception e) {
            logger.error("刷新监控数据失败", e);
            return ResponseEntity.ok(createErrorResponse("刷新监控数据失败: " + e.getMessage()));
        }
    }
    
    /**
     * 创建成功响应
     * 
     * @param data 响应数据
     * @param message 响应消息
     * @return 统一格式的成功响应
     */
    private Map<String, Object> createSuccessResponse(Object data, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", message);
        response.put("data", data);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
    
    /**
     * 创建错误响应
     * 
     * @param message 错误消息
     * @return 统一格式的错误响应
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 500);
        response.put("message", message);
        response.put("data", null);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
    
    /**
     * 创建参数错误响应
     * 
     * @param message 错误消息
     * @return 统一格式的参数错误响应
     */
    private Map<String, Object> createBadRequestResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", message);
        response.put("data", null);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}