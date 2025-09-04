package com.yxrobot.controller;

import com.yxrobot.service.CustomerService;
import com.yxrobot.service.ManagedDeviceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备管理辅助功能控制器
 * 提供设备管理相关的辅助功能接口，包括客户选项获取和设备数据导出
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@RestController
@RequestMapping("/api/admin")
@Validated
public class ManagedDeviceAuxiliaryController {
    
    private static final Logger logger = LoggerFactory.getLogger(ManagedDeviceAuxiliaryController.class);
    
    @Autowired
    private CustomerService customerService;
    
    @Autowired
    private ManagedDeviceService managedDeviceService;
    
    /**
     * 获取客户选项
     * 支持前端设备管理页面的客户筛选下拉框
     * 
     * @param keyword 搜索关键词（可选）
     * @return 客户选项列表
     */
    @GetMapping("/customers/options")
    public ResponseEntity<Map<String, Object>> getCustomerOptions(
            @RequestParam(required = false) String keyword) {
        
        logger.info("获取客户选项 - 关键词: {}", keyword);
        
        try {
            List<Map<String, Object>> customerOptions = new ArrayList<>();
            
            // 模拟客户数据（实际项目中应该从数据库查询）
            // TODO: 实际实现应该调用customerService获取真实数据
            customerOptions.add(createCustomerOption("1", "张三科技有限公司"));
            customerOptions.add(createCustomerOption("2", "李四教育机构"));
            customerOptions.add(createCustomerOption("3", "王五智能设备公司"));
            customerOptions.add(createCustomerOption("4", "赵六机器人研发中心"));
            customerOptions.add(createCustomerOption("5", "钱七自动化科技"));
            customerOptions.add(createCustomerOption("6", "孙八人工智能实验室"));
            customerOptions.add(createCustomerOption("7", "周九教学设备供应商"));
            customerOptions.add(createCustomerOption("8", "吴十智慧教育集团"));
            
            // 如果有搜索关键词，进行过滤
            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchKeyword = keyword.trim().toLowerCase();
                customerOptions = customerOptions.stream()
                    .filter(option -> {
                        String name = (String) option.get("name");
                        return name != null && name.toLowerCase().contains(searchKeyword);
                    })
                    .collect(java.util.stream.Collectors.toList());
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", customerOptions);
            
            logger.info("返回客户选项数量: {}", customerOptions.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取客户选项失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 导出设备数据
     * 支持按条件筛选导出设备数据
     * 
     * @param keyword 搜索关键词
     * @param status 设备状态筛选
     * @param model 设备型号筛选
     * @param customerId 客户筛选
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param format 导出格式（csv, excel, json）
     * @return 导出结果
     */
    @GetMapping("/devices/export")
    public ResponseEntity<Map<String, Object>> exportDevices(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "csv") @NotBlank String format) {
        
        logger.info("导出设备数据 - 关键词: {}, 状态: {}, 型号: {}, 客户: {}, 格式: {}", 
                   keyword, status, model, customerId, format);
        
        try {
            // 验证导出格式
            if (!isValidExportFormat(format)) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("code", 400);
                errorResponse.put("message", "不支持的导出格式: " + format + "，支持的格式: csv, excel, json");
                return ResponseEntity.status(400).body(errorResponse);
            }
            
            // 生成导出文件信息
            String timestamp = String.valueOf(System.currentTimeMillis());
            String filename = generateExportFilename(format, timestamp);
            String downloadUrl = "/api/files/exports/" + filename;
            
            // 模拟导出统计信息
            Map<String, Object> exportStats = new HashMap<>();
            exportStats.put("totalRecords", 156);
            exportStats.put("filteredRecords", 89);
            exportStats.put("exportedRecords", 89);
            
            // 构建响应数据
            Map<String, Object> exportData = new HashMap<>();
            exportData.put("filename", filename);
            exportData.put("downloadUrl", downloadUrl);
            exportData.put("format", format);
            exportData.put("fileSize", "2.3MB");
            exportData.put("exportTime", LocalDateTime.now());
            exportData.put("stats", exportStats);
            exportData.put("filters", createExportFilters(keyword, status, model, customerId, startDate, endDate));
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "导出成功");
            response.put("data", exportData);
            
            logger.info("设备数据导出成功 - 文件: {}, 记录数: {}", filename, exportStats.get("exportedRecords"));
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("导出设备数据失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "导出失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 获取设备型号选项
     * 支持前端设备管理页面的型号筛选下拉框
     * 
     * @return 设备型号选项列表
     */
    @GetMapping("/devices/model-options")
    public ResponseEntity<Map<String, Object>> getDeviceModelOptions() {
        
        logger.info("获取设备型号选项");
        
        try {
            List<Map<String, Object>> modelOptions = new ArrayList<>();
            
            // 设备型号选项
            modelOptions.add(createModelOption("YX-EDU-2024", "教育版", "适用于教育机构的练字机器人"));
            modelOptions.add(createModelOption("YX-HOME-2024", "家庭版", "适用于家庭使用的练字机器人"));
            modelOptions.add(createModelOption("YX-PRO-2024", "专业版", "适用于专业培训的练字机器人"));
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", modelOptions);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取设备型号选项失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 获取设备状态选项
     * 支持前端设备管理页面的状态筛选下拉框
     * 
     * @return 设备状态选项列表
     */
    @GetMapping("/devices/status-options")
    public ResponseEntity<Map<String, Object>> getDeviceStatusOptions() {
        
        logger.info("获取设备状态选项");
        
        try {
            List<Map<String, Object>> statusOptions = new ArrayList<>();
            
            // 设备状态选项
            statusOptions.add(createStatusOption("online", "在线", "设备正常在线运行"));
            statusOptions.add(createStatusOption("offline", "离线", "设备当前离线"));
            statusOptions.add(createStatusOption("error", "故障", "设备出现故障"));
            statusOptions.add(createStatusOption("maintenance", "维护中", "设备正在维护"));
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", statusOptions);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取设备状态选项失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 获取导出历史记录
     * 显示用户的导出历史
     * 
     * @param page 页码
     * @param pageSize 每页大小
     * @return 导出历史记录
     */
    @GetMapping("/devices/export-history")
    public ResponseEntity<Map<String, Object>> getExportHistory(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        logger.info("获取导出历史记录 - 页码: {}, 页面大小: {}", page, pageSize);
        
        try {
            List<Map<String, Object>> exportHistory = new ArrayList<>();
            
            // 模拟导出历史数据
            exportHistory.add(createExportHistoryRecord("1", "设备数据导出_20250125_143022.csv", 
                "csv", "2.3MB", LocalDateTime.now().minusHours(2), "completed"));
            exportHistory.add(createExportHistoryRecord("2", "设备数据导出_20250125_120015.xlsx", 
                "excel", "3.1MB", LocalDateTime.now().minusHours(5), "completed"));
            exportHistory.add(createExportHistoryRecord("3", "设备数据导出_20250124_165530.json", 
                "json", "1.8MB", LocalDateTime.now().minusDays(1), "completed"));
            
            Map<String, Object> result = new HashMap<>();
            result.put("list", exportHistory);
            result.put("total", exportHistory.size());
            result.put("page", page);
            result.put("pageSize", pageSize);
            result.put("totalPages", (int) Math.ceil((double) exportHistory.size() / pageSize));
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", result);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取导出历史记录失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    // ==================== 私有辅助方法 ====================
    
    /**
     * 创建客户选项对象
     */
    private Map<String, Object> createCustomerOption(String id, String name) {
        Map<String, Object> option = new HashMap<>();
        option.put("id", id);
        option.put("name", name);
        option.put("value", id);
        option.put("label", name);
        return option;
    }
    
    /**
     * 创建设备型号选项对象
     */
    private Map<String, Object> createModelOption(String code, String name, String description) {
        Map<String, Object> option = new HashMap<>();
        option.put("code", code);
        option.put("name", name);
        option.put("description", description);
        option.put("value", code);
        option.put("label", name);
        return option;
    }
    
    /**
     * 创建设备状态选项对象
     */
    private Map<String, Object> createStatusOption(String code, String name, String description) {
        Map<String, Object> option = new HashMap<>();
        option.put("code", code);
        option.put("name", name);
        option.put("description", description);
        option.put("value", code);
        option.put("label", name);
        return option;
    }
    
    /**
     * 创建导出历史记录对象
     */
    private Map<String, Object> createExportHistoryRecord(String id, String filename, 
            String format, String fileSize, LocalDateTime exportTime, String status) {
        Map<String, Object> record = new HashMap<>();
        record.put("id", id);
        record.put("filename", filename);
        record.put("format", format);
        record.put("fileSize", fileSize);
        record.put("exportTime", exportTime);
        record.put("status", status);
        record.put("downloadUrl", "/api/files/exports/" + filename);
        return record;
    }
    
    /**
     * 验证导出格式是否有效
     */
    private boolean isValidExportFormat(String format) {
        return format != null && (
            "csv".equalsIgnoreCase(format) || 
            "excel".equalsIgnoreCase(format) || 
            "xlsx".equalsIgnoreCase(format) || 
            "json".equalsIgnoreCase(format)
        );
    }
    
    /**
     * 生成导出文件名
     */
    private String generateExportFilename(String format, String timestamp) {
        String extension = format.toLowerCase();
        if ("excel".equals(extension)) {
            extension = "xlsx";
        }
        return String.format("设备数据导出_%s.%s", 
            LocalDateTime.now().toString().replaceAll("[:-]", "").substring(0, 15), 
            extension);
    }
    
    /**
     * 创建导出筛选条件对象
     */
    private Map<String, Object> createExportFilters(String keyword, String status, String model, 
            String customerId, LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> filters = new HashMap<>();
        filters.put("keyword", keyword);
        filters.put("status", status);
        filters.put("model", model);
        filters.put("customerId", customerId);
        filters.put("startDate", startDate);
        filters.put("endDate", endDate);
        return filters;
    }
}