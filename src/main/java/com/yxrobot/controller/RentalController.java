package com.yxrobot.controller;

import com.yxrobot.dto.DeviceUtilizationDTO;
import com.yxrobot.dto.RentalStatsDTO;
import com.yxrobot.entity.RentalCustomer;
import com.yxrobot.service.RentalStatsService;
import com.yxrobot.service.DeviceUtilizationService;
import com.yxrobot.service.RentalAnalysisService;
import com.yxrobot.service.RentalCustomerService;
import com.yxrobot.validation.RentalValidator;
import com.yxrobot.validation.RentalFormValidator;
import com.yxrobot.cache.RentalCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 租赁管理控制器
 * 完全基于前端RentalAnalytics.vue页面需求开发
 * 适配前端API调用，提供租赁数据分析功能
 * 
 * @author Kiro
 * @date 2025-01-28
 */
@RestController
@RequestMapping("/api/rental")
@CrossOrigin(origins = "*")
public class RentalController {
    
    private static final Logger logger = LoggerFactory.getLogger(RentalController.class);
    
    @Autowired
    private RentalStatsService rentalStatsService;
    
    @Autowired
    private DeviceUtilizationService deviceUtilizationService;
    
    @Autowired
    private RentalAnalysisService rentalAnalysisService;
    
    @Autowired
    private RentalCustomerService rentalCustomerService;
    
    @Autowired
    private RentalValidator rentalValidator;
    
    @Autowired
    private RentalFormValidator rentalFormValidator;
    
    @Autowired
    private RentalCacheService cacheService;
    
    /**
     * 获取租赁统计数据 - 适配前端核心指标卡片
     * 对应前端API: mockRentalAPI.getRentalStats()
     * 
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 租赁统计数据
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getRentalStats(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        logger.info("获取租赁统计数据，时间范围：{} 到 {}", startDate, endDate);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 验证和解析日期参数
            LocalDate start = rentalValidator.validateDateParam(startDate, "startDate");
            LocalDate end = rentalValidator.validateDateParam(endDate, "endDate");
            
            // 验证日期范围
            rentalValidator.validateDateRange(start, end);
            
            // 获取统计数据
            RentalStatsDTO stats = rentalStatsService.getRentalStats(start, end);
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", stats);
            
            logger.info("租赁统计数据获取成功");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取租赁统计数据失败", e);
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取设备利用率数据 - 适配前端设备利用率表格
     * 对应前端API: mockRentalAPI.getDeviceUtilizationData()
     * 
     * @param page 页码
     * @param pageSize 每页大小
     * @param keyword 搜索关键词
     * @param deviceModel 设备型号筛选
     * @param currentStatus 设备状态筛选
     * @param region 地区筛选
     * @return 设备利用率数据列表
     */
    @GetMapping("/devices")
    public ResponseEntity<Map<String, Object>> getDeviceUtilizationData(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String deviceModel,
            @RequestParam(required = false) String currentStatus,
            @RequestParam(required = false) String region) {
        
        logger.info("获取设备利用率数据，页码：{}，每页：{}，关键词：{}，型号：{}，状态：{}，地区：{}", 
                   page, pageSize, keyword, deviceModel, currentStatus, region);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 验证分页参数
            rentalValidator.validatePaginationParams(page, pageSize);
            
            // 验证搜索关键词
            rentalValidator.validateSearchKeyword(keyword);
            
            // 验证设备状态
            rentalValidator.validateDeviceStatus(currentStatus);
            
            // 设置默认分页参数
            if (page == null || page <= 0) {
                page = 1;
            }
            if (pageSize == null || pageSize <= 0) {
                pageSize = 20;
            }
            
            // 构建查询参数
            Map<String, Object> params = new HashMap<>();
            params.put("page", page);
            params.put("pageSize", pageSize);
            params.put("offset", (page - 1) * pageSize);
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                params.put("keyword", keyword.trim());
            }
            
            if (deviceModel != null && !deviceModel.trim().isEmpty()) {
                params.put("deviceModel", deviceModel.trim());
            }
            
            if (currentStatus != null && !currentStatus.trim().isEmpty()) {
                params.put("currentStatus", currentStatus.trim());
            }
            
            if (region != null && !region.trim().isEmpty()) {
                params.put("region", region.trim());
            }
            
            // 获取设备利用率数据
            List<DeviceUtilizationDTO> deviceList = deviceUtilizationService.getDeviceUtilizationData(params);
            Long total = deviceUtilizationService.getDeviceUtilizationCount(params);
            
            // 构建分页响应数据
            Map<String, Object> data = new HashMap<>();
            data.put("list", deviceList);
            data.put("total", total);
            data.put("page", page);
            data.put("pageSize", pageSize);
            data.put("totalPages", (total + pageSize - 1) / pageSize);
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", data);
            
            logger.info("设备利用率数据获取成功，总数：{}，当前页数据：{}", total, deviceList.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取设备利用率数据失败", e);
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取今日统计数据 - 适配前端右侧面板今日概览
     * 对应前端API: mockRentalAPI.getTodayStats()
     * 
     * @return 今日统计数据
     */
    @GetMapping("/today-stats")
    public ResponseEntity<Map<String, Object>> getTodayStats() {
        
        logger.info("获取今日租赁统计数据");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 获取今日统计数据
            Map<String, Object> todayStats = rentalStatsService.getTodayStats();
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", todayStats);
            
            logger.info("今日租赁统计数据获取成功");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取今日租赁统计数据失败", e);
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取设备状态统计 - 适配前端右侧面板设备状态
     * 对应前端API: mockRentalAPI.getDeviceStatusStats()
     * 
     * @return 设备状态统计数据
     */
    @GetMapping("/device-status-stats")
    public ResponseEntity<Map<String, Object>> getDeviceStatusStats() {
        
        logger.info("获取设备状态统计数据");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 获取设备状态统计数据
            Map<String, Object> statusStats = deviceUtilizationService.getDeviceStatusStats();
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", statusStats);
            
            logger.info("设备状态统计数据获取成功");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取设备状态统计数据失败", e);
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取利用率TOP设备排行 - 适配前端右侧面板TOP设备
     * 对应前端API: mockRentalAPI.getTopDevices()
     * 
     * @param limit 返回数量限制，默认5
     * @return TOP设备排行数据
     */
    @GetMapping("/top-devices")
    public ResponseEntity<Map<String, Object>> getTopDevices(
            @RequestParam(defaultValue = "5") Integer limit) {
        
        logger.info("获取利用率TOP设备排行，限制数量：{}", limit);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 获取TOP设备数据
            List<DeviceUtilizationDTO> topDevices = deviceUtilizationService.getTopDevicesByUtilization(limit);
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", topDevices);
            
            logger.info("利用率TOP设备排行获取成功，数量：{}", topDevices.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取利用率TOP设备排行失败", e);
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取设备型号列表 - 适配前端筛选下拉框
     * 
     * @return 设备型号列表
     */
    @GetMapping("/device-models")
    public ResponseEntity<Map<String, Object>> getDeviceModels() {
        
        logger.info("获取设备型号列表");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 获取设备型号列表
            List<String> deviceModels = deviceUtilizationService.getAllDeviceModels();
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", deviceModels);
            
            logger.info("设备型号列表获取成功，数量：{}", deviceModels.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取设备型号列表失败", e);
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取地区列表 - 适配前端筛选下拉框
     * 
     * @return 地区列表
     */
    @GetMapping("/regions")
    public ResponseEntity<Map<String, Object>> getRegions() {
        
        logger.info("获取地区列表");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 获取地区列表
            List<String> regions = deviceUtilizationService.getAllRegions();
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", regions);
            
            logger.info("地区列表获取成功，数量：{}", regions.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取地区列表失败", e);
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取租赁趋势图表数据 - 适配前端趋势图表
     * 对应前端API: mockRentalAPI.getTrendChartData()
     * 
     * @param period 时间周期（daily、weekly、monthly、quarterly）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 租赁趋势图表数据
     */
    @GetMapping("/charts/trends")
    public ResponseEntity<Map<String, Object>> getTrendChartData(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        logger.info("获取租赁趋势图表数据，周期：{}，时间范围：{} 到 {}", period, startDate, endDate);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 设置默认period
            if (period == null || period.trim().isEmpty()) {
                period = "daily";
            }
            
            // 解析日期参数
            LocalDate start = null;
            LocalDate end = null;
            
            if (startDate != null && !startDate.trim().isEmpty()) {
                start = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE);
            }
            
            if (endDate != null && !endDate.trim().isEmpty()) {
                end = LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE);
            }
            
            // 获取趋势图表数据
            Map<String, Object> chartData = rentalAnalysisService.getTrendChartData(period, start, end);
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", chartData);
            
            logger.info("租赁趋势图表数据获取成功");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取租赁趋势图表数据失败", e);
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取分布图表数据 - 适配前端分布图表
     * 对应前端API: mockRentalAPI.getDistributionData()
     * 
     * @param type 分布类型（region、device-model、utilization-ranking）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 分布图表数据
     */
    @GetMapping("/charts/distribution")
    public ResponseEntity<Map<String, Object>> getDistributionData(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        logger.info("获取分布图表数据，类型：{}，时间范围：{} 到 {}", type, startDate, endDate);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 设置默认type
            if (type == null || type.trim().isEmpty()) {
                type = "region";
            }
            
            // 解析日期参数
            LocalDate start = null;
            LocalDate end = null;
            
            if (startDate != null && !startDate.trim().isEmpty()) {
                start = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE);
            }
            
            if (endDate != null && !endDate.trim().isEmpty()) {
                end = LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE);
            }
            
            // 获取分布图表数据
            Map<String, Object> chartData = rentalAnalysisService.getDistributionData(type, start, end);
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", chartData);
            
            logger.info("分布图表数据获取成功，类型：{}", type);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取分布图表数据失败，类型：{}", type, e);
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取设备利用率排行图表数据 - 适配前端排行图表
     * 对应前端API: mockRentalAPI.getUtilizationRankingData()
     * 
     * @param limit 返回数量限制，默认12
     * @return 设备利用率排行图表数据
     */
    @GetMapping("/charts/utilization-ranking")
    public ResponseEntity<Map<String, Object>> getUtilizationRankingData(
            @RequestParam(required = false) Integer limit) {
        
        logger.info("获取设备利用率排行图表数据，限制数量：{}", limit);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 设置默认limit
            if (limit == null || limit <= 0) {
                limit = 12;
            }
            
            // 获取利用率排行图表数据
            Map<String, Object> chartData = rentalAnalysisService.getUtilizationRankingData(limit);
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", chartData);
            
            logger.info("设备利用率排行图表数据获取成功");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取设备利用率排行图表数据失败", e);
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取所有图表数据 - 适配前端一次性加载所有图表
     * 对应前端API: mockRentalAPI.getAllChartsData()
     * 
     * @param period 时间周期（daily、weekly、monthly、quarterly）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 所有图表数据
     */
    @GetMapping("/charts/all")
    public ResponseEntity<Map<String, Object>> getAllChartsData(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        logger.info("获取所有图表数据，周期：{}，时间范围：{} 到 {}", period, startDate, endDate);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 设置默认period
            if (period == null || period.trim().isEmpty()) {
                period = "daily";
            }
            
            // 解析日期参数
            LocalDate start = null;
            LocalDate end = null;
            
            if (startDate != null && !startDate.trim().isEmpty()) {
                start = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE);
            }
            
            if (endDate != null && !endDate.trim().isEmpty()) {
                end = LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE);
            }
            
            // 获取所有图表数据
            Map<String, Object> allChartsData = rentalAnalysisService.getAllChartsData(period, start, end);
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", allChartsData);
            
            logger.info("所有图表数据获取成功");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取所有图表数据失败", e);
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取客户列表 - 任务13辅助功能接口
     * 支持搜索和筛选功能
     * 
     * @param page 页码
     * @param pageSize 每页大小
     * @param keyword 搜索关键词
     * @param customerType 客户类型筛选
     * @param region 地区筛选
     * @return 客户列表数据
     */
    @GetMapping("/customers")
    public ResponseEntity<Map<String, Object>> getCustomers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String customerType,
            @RequestParam(required = false) String region) {
        
        logger.info("获取客户列表，页码：{}，每页：{}，关键词：{}，类型：{}，地区：{}", 
                   page, pageSize, keyword, customerType, region);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 设置默认分页参数
            if (page == null || page <= 0) {
                page = 1;
            }
            if (pageSize == null || pageSize <= 0) {
                pageSize = 20;
            }
            
            // 构建查询参数
            Map<String, Object> params = new HashMap<>();
            params.put("page", page);
            params.put("pageSize", pageSize);
            params.put("offset", (page - 1) * pageSize);
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                params.put("keyword", keyword.trim());
            }
            
            if (customerType != null && !customerType.trim().isEmpty()) {
                params.put("customerType", customerType.trim());
            }
            
            if (region != null && !region.trim().isEmpty()) {
                params.put("region", region.trim());
            }
            
            // 获取客户列表数据
            List<RentalCustomer> customerList = rentalCustomerService.getCustomerList(params);
            Long total = rentalCustomerService.getCustomerCount(params);
            
            // 构建分页响应数据
            Map<String, Object> data = new HashMap<>();
            data.put("list", customerList);
            data.put("total", total);
            data.put("page", page);
            data.put("pageSize", pageSize);
            data.put("totalPages", (total + pageSize - 1) / pageSize);
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", data);
            
            logger.info("客户列表数据获取成功，总数：{}，当前页数据：{}", total, customerList.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取客户列表数据失败", e);
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 批量操作接口 - 任务13辅助功能接口
     * 支持批量更新状态、批量维护等操作
     * 
     * @param batchRequest 批量操作请求
     * @return 操作结果
     */
    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> batchOperation(@RequestBody Map<String, Object> batchRequest) {
        
        logger.info("执行批量操作，请求参数：{}", batchRequest);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 验证批量操作表单数据
            List<String> validationErrors = rentalFormValidator.validateBatchOperationForm(batchRequest);
            if (!validationErrors.isEmpty()) {
                response.put("code", 400);
                response.put("message", "批量操作参数验证失败");
                response.put("data", null);
                response.put("validationErrors", validationErrors);
                return ResponseEntity.badRequest().body(response);
            }
            
            // 获取操作参数
            @SuppressWarnings("unchecked")
            List<String> ids = (List<String>) batchRequest.get("ids");
            String operation = (String) batchRequest.get("operation");
            @SuppressWarnings("unchecked")
            Map<String, Object> params = (Map<String, Object>) batchRequest.get("params");
            
            int successCount = 0;
            int failCount = 0;
            
            // 根据操作类型执行批量操作
            switch (operation.toLowerCase()) {
                case "updatestatus":
                    // 批量更新设备状态
                    String newStatus = params != null ? (String) params.get("status") : null;
                    if (newStatus == null || newStatus.trim().isEmpty()) {
                        response.put("code", 400);
                        response.put("message", "新状态不能为空");
                        response.put("data", null);
                        return ResponseEntity.badRequest().body(response);
                    }
                    
                    for (String deviceId : ids) {
                        try {
                            boolean success = deviceUtilizationService.updateDeviceStatus(deviceId, newStatus);
                            if (success) {
                                successCount++;
                            } else {
                                failCount++;
                            }
                        } catch (Exception e) {
                            logger.warn("更新设备状态失败，设备ID：{}", deviceId, e);
                            failCount++;
                        }
                    }
                    break;
                    
                case "maintenance":
                    // 批量设置维护状态
                    String maintenanceStatus = params != null ? (String) params.get("maintenanceStatus") : "maintenance";
                    
                    for (String deviceId : ids) {
                        try {
                            boolean success = deviceUtilizationService.updateMaintenanceStatus(deviceId, maintenanceStatus);
                            if (success) {
                                successCount++;
                            } else {
                                failCount++;
                            }
                        } catch (Exception e) {
                            logger.warn("更新维护状态失败，设备ID：{}", deviceId, e);
                            failCount++;
                        }
                    }
                    break;
                    
                case "delete":
                    // 批量软删除
                    for (String deviceId : ids) {
                        try {
                            boolean success = deviceUtilizationService.softDeleteDevice(deviceId);
                            if (success) {
                                successCount++;
                            } else {
                                failCount++;
                            }
                        } catch (Exception e) {
                            logger.warn("软删除设备失败，设备ID：{}", deviceId, e);
                            failCount++;
                        }
                    }
                    break;
                    
                default:
                    response.put("code", 400);
                    response.put("message", "不支持的操作类型: " + operation);
                    response.put("data", null);
                    return ResponseEntity.badRequest().body(response);
            }
            
            // 构建响应结果
            Map<String, Object> result = new HashMap<>();
            result.put("successCount", successCount);
            result.put("failCount", failCount);
            result.put("totalCount", ids.size());
            
            response.put("code", 200);
            response.put("message", String.format("批量操作完成，成功：%d，失败：%d", successCount, failCount));
            response.put("data", result);
            
            logger.info("批量操作完成，操作类型：{}，成功：{}，失败：{}", operation, successCount, failCount);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("批量操作失败", e);
            response.put("code", 500);
            response.put("message", "批量操作失败: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取客户类型列表 - 辅助功能接口
     * 支持前端筛选下拉框
     * 
     * @return 客户类型列表
     */
    @GetMapping("/customer-types")
    public ResponseEntity<Map<String, Object>> getCustomerTypes() {
        
        logger.info("获取客户类型列表");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 获取客户类型列表
            List<String> customerTypes = rentalCustomerService.getAllCustomerTypes();
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", customerTypes);
            
            logger.info("客户类型列表获取成功，数量：{}", customerTypes.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取客户类型列表失败", e);
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取客户地区列表 - 辅助功能接口
     * 支持前端筛选下拉框
     * 
     * @return 客户地区列表
     */
    @GetMapping("/customer-regions")
    public ResponseEntity<Map<String, Object>> getCustomerRegions() {
        
        logger.info("获取客户地区列表");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 获取客户地区列表
            List<String> regions = rentalCustomerService.getAllCustomerRegions();
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", regions);
            
            logger.info("客户地区列表获取成功，数量：{}", regions.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取客户地区列表失败", e);
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 高级搜索设备利用率数据 - 任务15搜索和筛选功能
     * 支持多条件搜索和复杂筛选
     * 
     * @param searchRequest 搜索请求参数
     * @return 搜索结果
     */
    @PostMapping("/devices/advanced-search")
    public ResponseEntity<Map<String, Object>> advancedSearchDevices(@RequestBody Map<String, Object> searchRequest) {
        
        logger.info("执行高级搜索设备利用率数据，搜索参数：{}", searchRequest);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 验证搜索参数
            List<String> validationErrors = rentalFormValidator.validateQueryParamsForm(searchRequest);
            if (!validationErrors.isEmpty()) {
                response.put("code", 400);
                response.put("message", "搜索参数验证失败");
                response.put("validationErrors", validationErrors);
                return ResponseEntity.badRequest().body(response);
            }
            
            // 构建搜索参数
            Map<String, Object> params = deviceUtilizationService.buildAdvancedSearchParams(searchRequest);
            
            // 执行高级搜索
            List<DeviceUtilizationDTO> deviceList = deviceUtilizationService.getDeviceUtilizationWithAdvancedSearch(params);
            Long total = deviceUtilizationService.getDeviceUtilizationCountWithAdvancedSearch(params);
            
            // 构建响应数据
            Map<String, Object> data = new HashMap<>();
            data.put("list", deviceList);
            data.put("total", total);
            data.put("page", params.getOrDefault("page", 1));
            data.put("pageSize", params.getOrDefault("pageSize", 20));
            data.put("totalPages", (total + (Integer) params.getOrDefault("pageSize", 20) - 1) / (Integer) params.getOrDefault("pageSize", 20));
            
            response.put("code", 200);
            response.put("message", "搜索成功");
            response.put("data", data);
            
            logger.info("高级搜索设备利用率数据成功，总数：{}，当前页数据：{}", total, deviceList.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("高级搜索设备利用率数据失败", e);
            response.put("code", 500);
            response.put("message", "搜索失败: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 按时间范围搜索设备利用率数据 - 任务15时间范围筛选功能
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param page 页码
     * @param pageSize 每页大小
     * @param keyword 搜索关键词
     * @return 搜索结果
     */
    @GetMapping("/devices/by-date-range")
    public ResponseEntity<Map<String, Object>> getDevicesByDateRange(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        
        logger.info("按时间范围搜索设备利用率数据，时间范围：{} 到 {}，页码：{}，关键词：{}", 
                   startDate, endDate, page, keyword);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 验证日期参数
            LocalDate start = rentalValidator.validateDateParam(startDate, "startDate");
            LocalDate end = rentalValidator.validateDateParam(endDate, "endDate");
            rentalValidator.validateDateRange(start, end);
            
            // 验证分页参数
            rentalValidator.validatePaginationParams(page, pageSize);
            
            // 验证搜索关键词
            rentalValidator.validateSearchKeyword(keyword);
            
            // 构建搜索参数
            Map<String, Object> params = new HashMap<>();
            params.put("startDate", startDate);
            params.put("endDate", endDate);
            params.put("page", page);
            params.put("pageSize", pageSize);
            params.put("offset", (page - 1) * pageSize);
            if (keyword != null && !keyword.trim().isEmpty()) {
                params.put("keyword", keyword.trim());
            }
            
            // 执行搜索
            List<DeviceUtilizationDTO> deviceList = deviceUtilizationService.getDeviceUtilizationByDateRange(params);
            
            // 构建响应数据
            Map<String, Object> data = new HashMap<>();
            data.put("list", deviceList);
            data.put("total", deviceList.size()); // 简化实现，实际应该查询总数
            data.put("page", page);
            data.put("pageSize", pageSize);
            
            response.put("code", 200);
            response.put("message", "搜索成功");
            response.put("data", data);
            
            logger.info("按时间范围搜索设备利用率数据成功，数据量：{}", deviceList.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("按时间范围搜索设备利用率数据失败", e);
            response.put("code", 500);
            response.put("message", "搜索失败: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 按设备型号筛选设备利用率数据 - 任务15设备型号筛选功能
     * 
     * @param deviceModels 设备型号列表（逗号分隔）
     * @param page 页码
     * @param pageSize 每页大小
     * @param keyword 搜索关键词
     * @param region 地区筛选
     * @return 筛选结果
     */
    @GetMapping("/devices/by-models")
    public ResponseEntity<Map<String, Object>> getDevicesByModels(
            @RequestParam String deviceModels,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String region) {
        
        logger.info("按设备型号筛选设备利用率数据，型号：{}，页码：{}，关键词：{}，地区：{}", 
                   deviceModels, page, keyword, region);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 解析设备型号列表
            List<String> modelList = Arrays.asList(deviceModels.split(","));
            
            // 验证设备型号
            for (String model : modelList) {
                rentalValidator.validateDeviceModel(model.trim());
            }
            
            // 验证分页参数
            rentalValidator.validatePaginationParams(page, pageSize);
            
            // 验证搜索关键词
            rentalValidator.validateSearchKeyword(keyword);
            
            // 构建搜索参数
            Map<String, Object> params = new HashMap<>();
            params.put("page", page);
            params.put("pageSize", pageSize);
            params.put("offset", (page - 1) * pageSize);
            if (keyword != null && !keyword.trim().isEmpty()) {
                params.put("keyword", keyword.trim());
            }
            if (region != null && !region.trim().isEmpty()) {
                params.put("region", region.trim());
            }
            
            // 执行筛选
            List<DeviceUtilizationDTO> deviceList = deviceUtilizationService.getDeviceUtilizationByModels(modelList, params);
            
            // 构建响应数据
            Map<String, Object> data = new HashMap<>();
            data.put("list", deviceList);
            data.put("total", deviceList.size());
            data.put("page", page);
            data.put("pageSize", pageSize);
            data.put("selectedModels", modelList);
            
            response.put("code", 200);
            response.put("message", "筛选成功");
            response.put("data", data);
            
            logger.info("按设备型号筛选设备利用率数据成功，数据量：{}", deviceList.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("按设备型号筛选设备利用率数据失败", e);
            response.put("code", 500);
            response.put("message", "筛选失败: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 按设备状态筛选设备利用率数据 - 任务15设备状态筛选功能
     * 
     * @param statuses 设备状态列表（逗号分隔）
     * @param page 页码
     * @param pageSize 每页大小
     * @param keyword 搜索关键词
     * @return 筛选结果
     */
    @GetMapping("/devices/by-statuses")
    public ResponseEntity<Map<String, Object>> getDevicesByStatuses(
            @RequestParam String statuses,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        
        logger.info("按设备状态筛选设备利用率数据，状态：{}，页码：{}，关键词：{}", 
                   statuses, page, keyword);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 解析状态列表
            List<String> statusList = Arrays.asList(statuses.split(","));
            
            // 验证设备状态
            for (String status : statusList) {
                rentalValidator.validateDeviceStatus(status.trim());
            }
            
            // 验证分页参数
            rentalValidator.validatePaginationParams(page, pageSize);
            
            // 验证搜索关键词
            rentalValidator.validateSearchKeyword(keyword);
            
            // 构建搜索参数
            Map<String, Object> params = new HashMap<>();
            params.put("page", page);
            params.put("pageSize", pageSize);
            params.put("offset", (page - 1) * pageSize);
            if (keyword != null && !keyword.trim().isEmpty()) {
                params.put("keyword", keyword.trim());
            }
            
            // 执行筛选
            List<DeviceUtilizationDTO> deviceList = deviceUtilizationService.getDeviceUtilizationByStatuses(statusList, params);
            
            // 构建响应数据
            Map<String, Object> data = new HashMap<>();
            data.put("list", deviceList);
            data.put("total", deviceList.size());
            data.put("page", page);
            data.put("pageSize", pageSize);
            data.put("selectedStatuses", statusList);
            
            response.put("code", 200);
            response.put("message", "筛选成功");
            response.put("data", data);
            
            logger.info("按设备状态筛选设备利用率数据成功，数据量：{}", deviceList.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("按设备状态筛选设备利用率数据失败", e);
            response.put("code", 500);
            response.put("message", "筛选失败: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 按利用率范围筛选设备利用率数据 - 任务15利用率范围筛选功能
     * 
     * @param minUtilization 最小利用率
     * @param maxUtilization 最大利用率
     * @param page 页码
     * @param pageSize 每页大小
     * @param keyword 搜索关键词
     * @return 筛选结果
     */
    @GetMapping("/devices/by-utilization-range")
    public ResponseEntity<Map<String, Object>> getDevicesByUtilizationRange(
            @RequestParam(required = false) Double minUtilization,
            @RequestParam(required = false) Double maxUtilization,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        
        logger.info("按利用率范围筛选设备利用率数据，范围：{}-{}，页码：{}，关键词：{}", 
                   minUtilization, maxUtilization, page, keyword);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 验证利用率范围
            if (minUtilization != null && (minUtilization < 0 || minUtilization > 100)) {
                throw new IllegalArgumentException("最小利用率必须在0-100之间");
            }
            if (maxUtilization != null && (maxUtilization < 0 || maxUtilization > 100)) {
                throw new IllegalArgumentException("最大利用率必须在0-100之间");
            }
            if (minUtilization != null && maxUtilization != null && minUtilization > maxUtilization) {
                throw new IllegalArgumentException("最小利用率不能大于最大利用率");
            }
            
            // 验证分页参数
            rentalValidator.validatePaginationParams(page, pageSize);
            
            // 验证搜索关键词
            rentalValidator.validateSearchKeyword(keyword);
            
            // 构建搜索参数
            Map<String, Object> params = new HashMap<>();
            params.put("page", page);
            params.put("pageSize", pageSize);
            params.put("offset", (page - 1) * pageSize);
            if (keyword != null && !keyword.trim().isEmpty()) {
                params.put("keyword", keyword.trim());
            }
            
            // 执行筛选
            List<DeviceUtilizationDTO> deviceList = deviceUtilizationService.getDeviceUtilizationByUtilizationRange(minUtilization, maxUtilization, params);
            
            // 构建响应数据
            Map<String, Object> data = new HashMap<>();
            data.put("list", deviceList);
            data.put("total", deviceList.size());
            data.put("page", page);
            data.put("pageSize", pageSize);
            data.put("utilizationRange", Map.of(
                "min", minUtilization != null ? minUtilization : 0,
                "max", maxUtilization != null ? maxUtilization : 100
            ));
            
            response.put("code", 200);
            response.put("message", "筛选成功");
            response.put("data", data);
            
            logger.info("按利用率范围筛选设备利用率数据成功，数据量：{}", deviceList.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("按利用率范围筛选设备利用率数据失败", e);
            response.put("code", 500);
            response.put("message", "筛选失败: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取搜索建议 - 任务15搜索自动完成功能
     * 
     * @param keyword 搜索关键词
     * @param type 搜索类型
     * @return 搜索建议列表
     */
    @GetMapping("/search-suggestions")
    public ResponseEntity<Map<String, Object>> getSearchSuggestions(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "all") String type) {
        
        logger.info("获取搜索建议，关键词：{}，类型：{}", keyword, type);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 验证搜索关键词
            rentalValidator.validateSearchKeyword(keyword);
            
            // 获取搜索建议
            List<String> suggestions = deviceUtilizationService.getSearchSuggestions(keyword, type);
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", suggestions);
            
            logger.info("搜索建议获取成功，数量：{}", suggestions.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取搜索建议失败", e);
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取筛选选项统计 - 任务15筛选选项统计功能
     * 
     * @return 筛选选项统计数据
     */
    @GetMapping("/filter-options-stats")
    public ResponseEntity<Map<String, Object>> getFilterOptionsStats() {
        
        logger.info("获取筛选选项统计");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 获取筛选选项统计
            Map<String, Object> stats = deviceUtilizationService.getFilterOptionsStats();
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", stats);
            
            logger.info("筛选选项统计获取成功");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取筛选选项统计失败", e);
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }
}