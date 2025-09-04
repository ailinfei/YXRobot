package com.yxrobot.controller;

import com.yxrobot.dto.ManagedDeviceDTO;
import com.yxrobot.dto.ManagedDeviceSearchCriteria;
import com.yxrobot.dto.PageResult;
import com.yxrobot.common.Result;
import com.yxrobot.entity.DeviceModel;
import com.yxrobot.entity.DeviceStatus;
import com.yxrobot.service.ManagedDeviceSearchOptimizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 设备搜索控制器
 * 提供设备搜索和筛选的API接口
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-28
 */
@RestController
@RequestMapping("/api/admin/devices/search")
public class ManagedDeviceSearchController {
    
    private static final Logger logger = LoggerFactory.getLogger(ManagedDeviceSearchController.class);
    
    @Autowired
    private ManagedDeviceSearchOptimizationService searchService;
    
    /**
     * 高级搜索设备
     * 
     * @param criteria 搜索条件
     * @return 搜索结果
     */
    @PostMapping("/advanced")
    public Result<PageResult<ManagedDeviceDTO>> advancedSearch(@RequestBody ManagedDeviceSearchCriteria criteria) {
        logger.info("接收到设备高级搜索请求: {}", criteria);
        
        try {
            PageResult<ManagedDeviceDTO> result = searchService.advancedSearch(criteria);
            return Result.success(result);
            
        } catch (Exception e) {
            logger.error("设备高级搜索失败", e);
            return Result.error("搜索失败: " + e.getMessage());
        }
    }
    
    /**
     * 快速搜索设备
     * 
     * @param keyword 搜索关键词
     * @param page 页码
     * @param pageSize 页大小
     * @return 搜索结果
     */
    @GetMapping("/quick")
    public Result<PageResult<ManagedDeviceDTO>> quickSearch(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        logger.info("接收到设备快速搜索请求，关键词: {}, 页码: {}, 页大小: {}", keyword, page, pageSize);
        
        try {
            ManagedDeviceSearchCriteria criteria = new ManagedDeviceSearchCriteria();
            criteria.setKeyword(keyword);
            criteria.setPage(page);
            criteria.setPageSize(pageSize);
            
            PageResult<ManagedDeviceDTO> result = searchService.advancedSearch(criteria);
            return Result.success(result);
            
        } catch (Exception e) {
            logger.error("设备快速搜索失败", e);
            return Result.error("搜索失败: " + e.getMessage());
        }
    }
    
    /**
     * 多条件搜索设备
     * 
     * @param keyword 关键词
     * @param status 设备状态
     * @param model 设备型号
     * @param customerId 客户ID
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param page 页码
     * @param pageSize 页大小
     * @return 搜索结果
     */
    @GetMapping("/multi-condition")
    public Result<PageResult<ManagedDeviceDTO>> multiConditionSearch(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) DeviceStatus status,
            @RequestParam(required = false) DeviceModel model,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        logger.info("接收到设备多条件搜索请求，关键词: {}, 状态: {}, 型号: {}, 客户ID: {}", 
                keyword, status, model, customerId);
        
        try {
            PageResult<ManagedDeviceDTO> result = searchService.searchDevices(
                keyword, status, model, customerId, startDate, endDate, page, pageSize);
            return Result.success(result);
            
        } catch (Exception e) {
            logger.error("设备多条件搜索失败", e);
            return Result.error("搜索失败: " + e.getMessage());
        }
    }
    
    /**
     * 按时间范围筛选设备
     * 
     * @param dateField 时间字段类型
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param page 页码
     * @param pageSize 页大小
     * @return 筛选结果
     */
    @GetMapping("/by-date-range")
    public Result<PageResult<ManagedDeviceDTO>> searchByDateRange(
            @RequestParam(defaultValue = "created") String dateField,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        logger.info("接收到按时间范围筛选设备请求，字段: {}, 开始: {}, 结束: {}", dateField, startDate, endDate);
        
        try {
            PageResult<ManagedDeviceDTO> result = searchService.searchByDateRange(
                dateField, startDate, endDate, page, pageSize);
            return Result.success(result);
            
        } catch (Exception e) {
            logger.error("按时间范围筛选设备失败", e);
            return Result.error("筛选失败: " + e.getMessage());
        }
    }
    
    /**
     * 按客户筛选设备
     * 
     * @param customerIds 客户ID列表
     * @param customerName 客户姓名
     * @param page 页码
     * @param pageSize 页大小
     * @return 筛选结果
     */
    @GetMapping("/by-customer")
    public Result<PageResult<ManagedDeviceDTO>> searchByCustomer(
            @RequestParam(required = false) List<Long> customerIds,
            @RequestParam(required = false) String customerName,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        logger.info("接收到按客户筛选设备请求，客户IDs: {}, 客户姓名: {}", customerIds, customerName);
        
        try {
            PageResult<ManagedDeviceDTO> result = searchService.searchByCustomer(
                customerIds, customerName, page, pageSize);
            return Result.success(result);
            
        } catch (Exception e) {
            logger.error("按客户筛选设备失败", e);
            return Result.error("筛选失败: " + e.getMessage());
        }
    }
    
    /**
     * 按设备型号筛选设备
     * 
     * @param models 设备型号列表
     * @param page 页码
     * @param pageSize 页大小
     * @return 筛选结果
     */
    @GetMapping("/by-model")
    public Result<PageResult<ManagedDeviceDTO>> searchByModel(
            @RequestParam List<DeviceModel> models,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        logger.info("接收到按设备型号筛选设备请求，型号: {}", models);
        
        try {
            PageResult<ManagedDeviceDTO> result = searchService.searchByModel(models, page, pageSize);
            return Result.success(result);
            
        } catch (Exception e) {
            logger.error("按设备型号筛选设备失败", e);
            return Result.error("筛选失败: " + e.getMessage());
        }
    }
    
    /**
     * 按设备状态筛选设备
     * 
     * @param statuses 设备状态列表
     * @param page 页码
     * @param pageSize 页大小
     * @return 筛选结果
     */
    @GetMapping("/by-status")
    public Result<PageResult<ManagedDeviceDTO>> searchByStatus(
            @RequestParam List<DeviceStatus> statuses,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        logger.info("接收到按设备状态筛选设备请求，状态: {}", statuses);
        
        try {
            PageResult<ManagedDeviceDTO> result = searchService.searchByStatus(statuses, page, pageSize);
            return Result.success(result);
            
        } catch (Exception e) {
            logger.error("按设备状态筛选设备失败", e);
            return Result.error("筛选失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取搜索建议
     * 
     * @param field 搜索字段
     * @param query 查询关键词
     * @param limit 建议数量限制
     * @return 搜索建议列表
     */
    @GetMapping("/suggestions")
    public Result<List<String>> getSearchSuggestions(
            @RequestParam String field,
            @RequestParam String query,
            @RequestParam(defaultValue = "10") Integer limit) {
        
        logger.info("接收到获取搜索建议请求，字段: {}, 查询: {}, 限制: {}", field, query, limit);
        
        try {
            List<String> suggestions = searchService.getSearchSuggestions(field, query, limit);
            return Result.success(suggestions);
            
        } catch (Exception e) {
            logger.error("获取搜索建议失败", e);
            return Result.error("获取建议失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取热门搜索关键词
     * 
     * @param limit 数量限制
     * @return 热门关键词列表
     */
    @GetMapping("/popular-keywords")
    public Result<List<String>> getPopularSearchKeywords(
            @RequestParam(defaultValue = "10") Integer limit) {
        
        logger.info("接收到获取热门搜索关键词请求，限制: {}", limit);
        
        try {
            List<String> keywords = searchService.getPopularSearchKeywords(limit);
            return Result.success(keywords);
            
        } catch (Exception e) {
            logger.error("获取热门搜索关键词失败", e);
            return Result.error("获取热门关键词失败: " + e.getMessage());
        }
    }
    
    /**
     * 搜索条件验证
     * 
     * @param criteria 搜索条件
     * @return 验证结果
     */
    @PostMapping("/validate")
    public Result<Boolean> validateSearchCriteria(@RequestBody ManagedDeviceSearchCriteria criteria) {
        logger.info("接收到搜索条件验证请求: {}", criteria);
        
        try {
            boolean isValid = criteria != null && criteria.isValid();
            return Result.success(isValid);
            
        } catch (Exception e) {
            logger.error("搜索条件验证失败", e);
            return Result.error("验证失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查是否有搜索条件
     * 
     * @param criteria 搜索条件
     * @return 检查结果
     */
    @PostMapping("/has-conditions")
    public Result<Boolean> hasSearchConditions(@RequestBody ManagedDeviceSearchCriteria criteria) {
        logger.info("接收到检查搜索条件请求: {}", criteria);
        
        try {
            boolean hasConditions = criteria != null && criteria.hasSearchConditions();
            return Result.success(hasConditions);
            
        } catch (Exception e) {
            logger.error("检查搜索条件失败", e);
            return Result.error("检查失败: " + e.getMessage());
        }
    }
}