package com.yxrobot.controller;

import com.yxrobot.dto.ManagedDeviceDTO;
import com.yxrobot.dto.ManagedDeviceStatsDTO;
import com.yxrobot.service.ManagedDeviceService;
import com.yxrobot.service.ManagedDeviceStatsService;
import com.yxrobot.service.ManagedDeviceLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备管理控制器
 * 提供RESTful API接口，适配前端API调用
 * 支持设备的CRUD操作、分页查询、搜索筛选等功能
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@RestController
@RequestMapping("/api/admin/devices")
@Validated
public class ManagedDeviceController {
    
    private static final Logger logger = LoggerFactory.getLogger(ManagedDeviceController.class);
    
    @Autowired
    private ManagedDeviceService managedDeviceService;
    
    @Autowired
    private ManagedDeviceStatsService managedDeviceStatsService;
    
    @Autowired
    private ManagedDeviceLogService managedDeviceLogService;
    
    /**
     * 获取设备列表
     * 支持前端列表查询需求，包括分页、搜索、筛选功能
     * 
     * @param page 页码（从1开始）
     * @param pageSize 每页大小
     * @param keyword 搜索关键词
     * @param status 设备状态筛选
     * @param model 设备型号筛选
     * @param customerId 客户筛选
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param sortBy 排序字段
     * @param sortOrder 排序方向
     * @return 设备列表和分页信息
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDevices(
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "20") @Min(1) Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        
        logger.info("获取设备列表 - 页码: {}, 页面大小: {}, 关键词: {}, 状态: {}, 型号: {}", 
                   page, pageSize, keyword, status, model);
        
        try {
            com.yxrobot.dto.PageResult<ManagedDeviceDTO> result = managedDeviceService.getManagedDevices(
                page, pageSize, keyword, status, model, customerId, 
                startDate, endDate, sortBy, sortOrder
            );
            
            // 获取统计数据
            ManagedDeviceStatsDTO stats = managedDeviceStatsService.getManagedDeviceStats();
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            
            Map<String, Object> data = new HashMap<>();
            data.put("list", result.getList());
            data.put("total", result.getTotal());
            data.put("page", result.getPage());
            data.put("pageSize", result.getSize());
            data.put("totalPages", result.getTotalPages());
            data.put("stats", stats);
            
            response.put("data", data);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取设备列表失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 根据ID获取设备详情
     * 支持前端详情查询，返回完整的设备详细信息
     * 
     * @param id 设备ID
     * @return 设备详细信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getDeviceById(@PathVariable @NotNull Long id) {
        logger.info("获取设备详情 - ID: {}", id);
        
        try {
            ManagedDeviceDTO device = managedDeviceService.getManagedDeviceById(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", device);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("获取设备详情失败 - 参数错误: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 400);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        } catch (RuntimeException e) {
            logger.error("获取设备详情失败 - ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 404);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(404).body(errorResponse);
        } catch (Exception e) {
            logger.error("获取设备详情失败 - ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 创建设备
     * 支持前端设备创建功能
     * 
     * @param request 设备创建请求
     * @return 创建的设备信息
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createDevice(@Valid @RequestBody DeviceCreateRequest request) {
        logger.info("创建设备 - 序列号: {}, 型号: {}", request.getSerialNumber(), request.getModel());
        
        try {
            // 转换请求为DTO
            ManagedDeviceDTO deviceDTO = convertCreateRequestToDTO(request);
            
            // 创建设备
            ManagedDeviceDTO createdDevice = managedDeviceService.createManagedDevice(deviceDTO);
            
            // 记录操作日志
            managedDeviceLogService.logDeviceOperation(
                Long.valueOf(createdDevice.getId()), 
                "创建设备", 
                "成功", 
                "系统管理员"
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "创建成功");
            response.put("data", createdDevice);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("创建设备失败 - 参数错误: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 400);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        } catch (RuntimeException e) {
            logger.error("创建设备失败: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 409);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(409).body(errorResponse);
        } catch (Exception e) {
            logger.error("创建设备失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "创建失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 更新设备
     * 支持前端设备编辑功能
     * 
     * @param id 设备ID
     * @param request 设备更新请求
     * @return 更新后的设备信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateDevice(
            @PathVariable @NotNull Long id, 
            @Valid @RequestBody DeviceUpdateRequest request) {
        
        logger.info("更新设备 - ID: {}, 序列号: {}", id, request.getSerialNumber());
        
        try {
            // 转换请求为DTO
            ManagedDeviceDTO deviceDTO = convertUpdateRequestToDTO(request);
            
            // 更新设备
            ManagedDeviceDTO updatedDevice = managedDeviceService.updateManagedDevice(id, deviceDTO);
            
            // 记录操作日志
            managedDeviceLogService.logDeviceOperation(
                id, 
                "更新设备", 
                "成功", 
                "系统管理员"
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "更新成功");
            response.put("data", updatedDevice);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("更新设备失败 - 参数错误: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 400);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        } catch (RuntimeException e) {
            logger.error("更新设备失败 - ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 404);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(404).body(errorResponse);
        } catch (Exception e) {
            logger.error("更新设备失败 - ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "更新失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 删除设备
     * 支持前端设备删除功能（软删除）
     * 
     * @param id 设备ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteDevice(@PathVariable @NotNull Long id) {
        logger.info("删除设备 - ID: {}", id);
        
        try {
            // 删除设备
            managedDeviceService.deleteManagedDevice(id);
            
            // 记录操作日志
            managedDeviceLogService.logDeviceOperation(
                id, 
                "删除设备", 
                "成功", 
                "系统管理员"
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "删除成功");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("删除设备失败 - 参数错误: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 400);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        } catch (RuntimeException e) {
            logger.error("删除设备失败 - ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 404);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(404).body(errorResponse);
        } catch (Exception e) {
            logger.error("删除设备失败 - ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "删除失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 获取设备统计数据
     * 支持前端统计卡片显示
     * 
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 设备统计数据
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDeviceStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        logger.info("获取设备统计数据");
        
        try {
            ManagedDeviceStatsDTO stats = managedDeviceStatsService.getManagedDeviceStats(startDate, endDate);
            
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
     * 获取设备日志
     * 支持前端日志查询功能
     * 
     * @param id 设备ID
     * @param page 页码
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
        
        logger.info("获取设备日志 - 设备ID: {}, 页码: {}, 页面大小: {}", id, page, pageSize);
        
        try {
            Map<String, Object> logs = managedDeviceLogService.getManagedDeviceLogs(
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
     * 批量删除设备
     * 支持前端批量删除功能
     * 
     * @param request 批量删除请求
     * @return 删除结果
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Map<String, Object>> batchDeleteDevices(@RequestBody BatchDeleteRequest request) {
        logger.info("批量删除设备 - 数量: {}", request.getDeviceIds().size());
        
        try {
            // 批量删除设备
            managedDeviceService.deleteManagedDevicesBatch(request.getDeviceIds());
            
            // 记录操作日志
            for (Long deviceId : request.getDeviceIds()) {
                managedDeviceLogService.logDeviceOperation(
                    deviceId, 
                    "批量删除设备", 
                    "成功", 
                    "系统管理员"
                );
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "批量删除成功");
            
            Map<String, Object> data = new HashMap<>();
            data.put("deletedCount", request.getDeviceIds().size());
            response.put("data", data);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("批量删除设备失败 - 参数错误: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 400);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        } catch (Exception e) {
            logger.error("批量删除设备失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "批量删除失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 根据序列号查询设备
     * 
     * @param serialNumber 设备序列号
     * @return 设备信息
     */
    @GetMapping("/serial/{serialNumber}")
    public ResponseEntity<Map<String, Object>> getDeviceBySerialNumber(@PathVariable String serialNumber) {
        logger.info("根据序列号查询设备 - 序列号: {}", serialNumber);
        
        try {
            ManagedDeviceDTO device = managedDeviceService.getManagedDeviceBySerialNumber(serialNumber);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", device);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("根据序列号查询设备失败 - 参数错误: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 400);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        } catch (Exception e) {
            logger.error("根据序列号查询设备失败 - 序列号: {}", serialNumber, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 转换创建请求为DTO
     */
    private ManagedDeviceDTO convertCreateRequestToDTO(DeviceCreateRequest request) {
        ManagedDeviceDTO dto = new ManagedDeviceDTO();
        dto.setSerialNumber(request.getSerialNumber());
        dto.setModel(request.getModel());
        dto.setStatus("offline"); // 新设备默认离线状态
        dto.setFirmwareVersion(request.getFirmwareVersion());
        dto.setCustomerId(request.getCustomerId());
        dto.setCustomerName(request.getCustomerName());
        dto.setCustomerPhone(request.getCustomerPhone());
        dto.setNotes(request.getNotes());
        dto.setCreatedBy("系统管理员");
        
        // 设置技术参数
        if (request.getSpecifications() != null) {
            dto.setSpecifications(request.getSpecifications());
        }
        
        // 设置配置信息
        if (request.getConfiguration() != null) {
            dto.setConfiguration(request.getConfiguration());
        }
        
        return dto;
    }
    
    /**
     * 转换更新请求为DTO
     */
    private ManagedDeviceDTO convertUpdateRequestToDTO(DeviceUpdateRequest request) {
        ManagedDeviceDTO dto = new ManagedDeviceDTO();
        dto.setSerialNumber(request.getSerialNumber());
        dto.setModel(request.getModel());
        dto.setStatus(request.getStatus());
        dto.setFirmwareVersion(request.getFirmwareVersion());
        dto.setCustomerId(request.getCustomerId());
        dto.setCustomerName(request.getCustomerName());
        dto.setCustomerPhone(request.getCustomerPhone());
        dto.setNotes(request.getNotes());
        
        // 设置技术参数
        if (request.getSpecifications() != null) {
            dto.setSpecifications(request.getSpecifications());
        }
        
        // 设置配置信息
        if (request.getConfiguration() != null) {
            dto.setConfiguration(request.getConfiguration());
        }
        
        return dto;
    }
    
    /**
     * 设备创建请求类
     */
    public static class DeviceCreateRequest {
        @NotNull(message = "设备序列号不能为空")
        private String serialNumber;
        
        @NotNull(message = "设备型号不能为空")
        private String model;
        
        @NotNull(message = "固件版本不能为空")
        private String firmwareVersion;
        
        @NotNull(message = "客户ID不能为空")
        private String customerId;
        
        @NotNull(message = "客户名称不能为空")
        private String customerName;
        
        private String customerPhone;
        private String notes;
        private ManagedDeviceDTO.DeviceSpecificationDTO specifications;
        private ManagedDeviceDTO.DeviceConfigurationDTO configuration;
        
        // Getter和Setter方法
        public String getSerialNumber() { return serialNumber; }
        public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
        
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        
        public String getFirmwareVersion() { return firmwareVersion; }
        public void setFirmwareVersion(String firmwareVersion) { this.firmwareVersion = firmwareVersion; }
        
        public String getCustomerId() { return customerId; }
        public void setCustomerId(String customerId) { this.customerId = customerId; }
        
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        
        public String getCustomerPhone() { return customerPhone; }
        public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
        
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
        
        public ManagedDeviceDTO.DeviceSpecificationDTO getSpecifications() { return specifications; }
        public void setSpecifications(ManagedDeviceDTO.DeviceSpecificationDTO specifications) { this.specifications = specifications; }
        
        public ManagedDeviceDTO.DeviceConfigurationDTO getConfiguration() { return configuration; }
        public void setConfiguration(ManagedDeviceDTO.DeviceConfigurationDTO configuration) { this.configuration = configuration; }
    }
    
    /**
     * 设备更新请求类
     */
    public static class DeviceUpdateRequest {
        @NotNull(message = "设备序列号不能为空")
        private String serialNumber;
        
        @NotNull(message = "设备型号不能为空")
        private String model;
        
        private String status;
        
        @NotNull(message = "固件版本不能为空")
        private String firmwareVersion;
        
        @NotNull(message = "客户ID不能为空")
        private String customerId;
        
        @NotNull(message = "客户名称不能为空")
        private String customerName;
        
        private String customerPhone;
        private String notes;
        private ManagedDeviceDTO.DeviceSpecificationDTO specifications;
        private ManagedDeviceDTO.DeviceConfigurationDTO configuration;
        
        // Getter和Setter方法
        public String getSerialNumber() { return serialNumber; }
        public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
        
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getFirmwareVersion() { return firmwareVersion; }
        public void setFirmwareVersion(String firmwareVersion) { this.firmwareVersion = firmwareVersion; }
        
        public String getCustomerId() { return customerId; }
        public void setCustomerId(String customerId) { this.customerId = customerId; }
        
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        
        public String getCustomerPhone() { return customerPhone; }
        public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
        
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
        
        public ManagedDeviceDTO.DeviceSpecificationDTO getSpecifications() { return specifications; }
        public void setSpecifications(ManagedDeviceDTO.DeviceSpecificationDTO specifications) { this.specifications = specifications; }
        
        public ManagedDeviceDTO.DeviceConfigurationDTO getConfiguration() { return configuration; }
        public void setConfiguration(ManagedDeviceDTO.DeviceConfigurationDTO configuration) { this.configuration = configuration; }
    }
    
    /**
     * 批量删除请求类
     */
    public static class BatchDeleteRequest {
        @NotNull(message = "设备ID列表不能为空")
        private List<Long> deviceIds;
        
        public List<Long> getDeviceIds() { return deviceIds; }
        public void setDeviceIds(List<Long> deviceIds) { this.deviceIds = deviceIds; }
    }
}