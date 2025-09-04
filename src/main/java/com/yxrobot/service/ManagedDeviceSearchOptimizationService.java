package com.yxrobot.service;

import com.yxrobot.dto.ManagedDeviceDTO;
import com.yxrobot.dto.ManagedDeviceSearchCriteria;
import com.yxrobot.dto.PageResult;
import com.yxrobot.entity.DeviceModel;
import com.yxrobot.entity.DeviceStatus;
import com.yxrobot.entity.ManagedDevice;
import com.yxrobot.exception.ManagedDeviceException;
import com.yxrobot.mapper.ManagedDeviceMapper;
import com.yxrobot.util.DeviceValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 设备搜索优化服务
 * 提供高级搜索、筛选、排序和性能优化功能
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-28
 */
@Service
public class ManagedDeviceSearchOptimizationService {
    
    private static final Logger logger = LoggerFactory.getLogger(ManagedDeviceSearchOptimizationService.class);
    
    @Autowired
    private ManagedDeviceMapper managedDeviceMapper;
    
    @Autowired
    private ManagedDeviceSearchPerformanceService performanceService;
    
    /**
     * 高级搜索设备
     * 支持多条件组合搜索、分页、排序
     * 
     * @param criteria 搜索条件
     * @return 分页结果
     */
    public PageResult<ManagedDeviceDTO> advancedSearch(ManagedDeviceSearchCriteria criteria) {
        logger.info("执行设备高级搜索，条件: {}", criteria);
        
        long startTime = System.currentTimeMillis();
        boolean success = false;
        int resultCount = 0;
        
        try {
            // 验证搜索条件
            validateSearchCriteria(criteria);
            
            // 优化搜索条件
            optimizeSearchCriteria(criteria);
            
            // 执行搜索
            List<ManagedDevice> devices = managedDeviceMapper.advancedSearchOptimized(criteria);
            int totalCount = managedDeviceMapper.countAdvancedSearchOptimized(criteria);
            
            // 转换为DTO
            List<ManagedDeviceDTO> deviceDTOs = devices.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            // 构建分页结果
            PageResult<ManagedDeviceDTO> result = new PageResult<>();
            result.setList(deviceDTOs);
            result.setTotal((long) totalCount);
            result.setPage(criteria.getPage());
            result.setSize(criteria.getPageSize());
            result.setTotalPages((int) Math.ceil((double) totalCount / criteria.getPageSize()));
            
            resultCount = deviceDTOs.size();
            success = true;
            
            logger.info("设备高级搜索完成，返回{}条记录，总计{}条", deviceDTOs.size(), totalCount);
            return result;
            
        } catch (Exception e) {
            logger.error("设备高级搜索失败", e);
            throw new ManagedDeviceException("设备搜索失败: " + e.getMessage());
        } finally {
            // 记录性能指标
            long executionTime = System.currentTimeMillis() - startTime;
            performanceService.recordSearchExecution("advancedSearch", criteria.getKeyword(), 
                                                    executionTime, resultCount, success);
        }
    }
    
    /**
     * 多条件搜索设备
     * 支持关键词、状态、型号、客户、时间范围等多种条件
     * 
     * @param keyword 关键词
     * @param status 设备状态
     * @param model 设备型号
     * @param customerId 客户ID
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param page 页码
     * @param pageSize 页大小
     * @return 分页结果
     */
    public PageResult<ManagedDeviceDTO> searchDevices(String keyword, DeviceStatus status, 
            DeviceModel model, Long customerId, LocalDateTime startDate, LocalDateTime endDate,
            Integer page, Integer pageSize) {
        
        logger.info("执行设备多条件搜索，关键词: {}, 状态: {}, 型号: {}, 客户ID: {}", 
                keyword, status, model, customerId);
        
        // 构建搜索条件
        ManagedDeviceSearchCriteria criteria = new ManagedDeviceSearchCriteria();
        criteria.setKeyword(keyword);
        criteria.setStatus(status);
        criteria.setModel(model);
        criteria.setCustomerId(customerId);
        criteria.setCreatedStartDate(startDate);
        criteria.setCreatedEndDate(endDate);
        criteria.setPage(page != null ? page : 1);
        criteria.setPageSize(pageSize != null ? pageSize : 10);
        
        return advancedSearch(criteria);
    }
    
    /**
     * 按时间范围筛选设备
     * 支持按创建时间、更新时间、激活时间、最后在线时间筛选
     * 
     * @param dateField 时间字段类型
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param page 页码
     * @param pageSize 页大小
     * @return 分页结果
     */
    public PageResult<ManagedDeviceDTO> searchByDateRange(String dateField, 
            LocalDateTime startDate, LocalDateTime endDate, Integer page, Integer pageSize) {
        
        logger.info("按时间范围筛选设备，字段: {}, 开始: {}, 结束: {}", dateField, startDate, endDate);
        
        ManagedDeviceSearchCriteria criteria = new ManagedDeviceSearchCriteria();
        criteria.setDateField(dateField);
        
        // 根据字段类型设置对应的时间范围
        switch (dateField) {
            case "created":
                criteria.setCreatedStartDate(startDate);
                criteria.setCreatedEndDate(endDate);
                break;
            case "updated":
                criteria.setUpdatedStartDate(startDate);
                criteria.setUpdatedEndDate(endDate);
                break;
            case "activated":
                criteria.setActivatedStartDate(startDate);
                criteria.setActivatedEndDate(endDate);
                break;
            case "lastOnline":
                criteria.setLastOnlineStartDate(startDate);
                criteria.setLastOnlineEndDate(endDate);
                break;
            default:
                criteria.setCreatedStartDate(startDate);
                criteria.setCreatedEndDate(endDate);
        }
        
        criteria.setPage(page != null ? page : 1);
        criteria.setPageSize(pageSize != null ? pageSize : 10);
        
        return advancedSearch(criteria);
    }
    
    /**
     * 按客户筛选设备
     * 支持单个客户或多个客户筛选
     * 
     * @param customerIds 客户ID列表
     * @param customerName 客户姓名（模糊匹配）
     * @param page 页码
     * @param pageSize 页大小
     * @return 分页结果
     */
    public PageResult<ManagedDeviceDTO> searchByCustomer(List<Long> customerIds, 
            String customerName, Integer page, Integer pageSize) {
        
        logger.info("按客户筛选设备，客户IDs: {}, 客户姓名: {}", customerIds, customerName);
        
        ManagedDeviceSearchCriteria criteria = new ManagedDeviceSearchCriteria();
        criteria.setCustomerIds(customerIds);
        criteria.setCustomerName(customerName);
        criteria.setPage(page != null ? page : 1);
        criteria.setPageSize(pageSize != null ? pageSize : 10);
        
        return advancedSearch(criteria);
    }
    
    /**
     * 按设备型号筛选设备
     * 支持单个型号或多个型号筛选
     * 
     * @param models 设备型号列表
     * @param page 页码
     * @param pageSize 页大小
     * @return 分页结果
     */
    public PageResult<ManagedDeviceDTO> searchByModel(List<DeviceModel> models, 
            Integer page, Integer pageSize) {
        
        logger.info("按设备型号筛选设备，型号: {}", models);
        
        ManagedDeviceSearchCriteria criteria = new ManagedDeviceSearchCriteria();
        criteria.setModelList(models);
        criteria.setPage(page != null ? page : 1);
        criteria.setPageSize(pageSize != null ? pageSize : 10);
        
        return advancedSearch(criteria);
    }
    
    /**
     * 按设备状态筛选设备
     * 支持单个状态或多个状态筛选
     * 
     * @param statuses 设备状态列表
     * @param page 页码
     * @param pageSize 页大小
     * @return 分页结果
     */
    public PageResult<ManagedDeviceDTO> searchByStatus(List<DeviceStatus> statuses, 
            Integer page, Integer pageSize) {
        
        logger.info("按设备状态筛选设备，状态: {}", statuses);
        
        ManagedDeviceSearchCriteria criteria = new ManagedDeviceSearchCriteria();
        criteria.setStatusList(statuses);
        criteria.setPage(page != null ? page : 1);
        criteria.setPageSize(pageSize != null ? pageSize : 10);
        
        return advancedSearch(criteria);
    }
    
    /**
     * 获取搜索建议
     * 根据用户输入提供搜索建议
     * 
     * @param field 搜索字段
     * @param query 查询关键词
     * @param limit 建议数量限制
     * @return 搜索建议列表
     */
    public List<String> getSearchSuggestions(String field, String query, Integer limit) {
        logger.info("获取搜索建议，字段: {}, 查询: {}, 限制: {}", field, query, limit);
        
        try {
            if (!StringUtils.hasText(query)) {
                return new ArrayList<>();
            }
            
            int suggestionLimit = limit != null ? Math.min(limit, 20) : 10;
            return managedDeviceMapper.getSearchSuggestions(field, query.trim(), suggestionLimit);
            
        } catch (Exception e) {
            logger.error("获取搜索建议失败", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 获取热门搜索关键词
     * 
     * @param limit 数量限制
     * @return 热门关键词列表
     */
    public List<String> getPopularSearchKeywords(Integer limit) {
        logger.info("获取热门搜索关键词，限制: {}", limit);
        
        // 这里可以根据实际需求实现热门搜索统计
        // 暂时返回一些常用的搜索关键词
        List<String> popularKeywords = Arrays.asList(
            "在线", "离线", "故障", "维护中", "教育版", "家庭版", "专业版",
            "已激活", "未激活", "最新版本", "需要更新"
        );
        
        int keywordLimit = limit != null ? Math.min(limit, popularKeywords.size()) : 10;
        return popularKeywords.subList(0, keywordLimit);
    }
    
    /**
     * 验证搜索条件
     * 
     * @param criteria 搜索条件
     */
    private void validateSearchCriteria(ManagedDeviceSearchCriteria criteria) {
        if (criteria == null) {
            throw new ManagedDeviceException("搜索条件不能为空");
        }
        
        if (!criteria.isValid()) {
            throw new ManagedDeviceException("搜索条件无效");
        }
        
        // 验证关键词长度
        if (StringUtils.hasText(criteria.getKeyword()) && criteria.getKeyword().length() > 100) {
            throw new ManagedDeviceException("搜索关键词长度不能超过100个字符");
        }
        
        // 验证分页参数
        if (criteria.getPageSize() != null && criteria.getPageSize() > 1000) {
            throw new ManagedDeviceException("每页大小不能超过1000");
        }
    }
    
    /**
     * 优化搜索条件
     * 
     * @param criteria 搜索条件
     */
    private void optimizeSearchCriteria(ManagedDeviceSearchCriteria criteria) {
        // 清理关键词
        if (StringUtils.hasText(criteria.getKeyword())) {
            criteria.setKeyword(criteria.getKeyword().trim());
        }
        
        // 设置默认排序
        if (!StringUtils.hasText(criteria.getSortBy())) {
            criteria.setSortBy("createdAt");
            criteria.setSortOrder("DESC");
        }
        
        // 设置默认分页
        if (criteria.getPage() == null || criteria.getPage() < 1) {
            criteria.setPage(1);
        }
        if (criteria.getPageSize() == null || criteria.getPageSize() < 1) {
            criteria.setPageSize(10);
        }
        
        // 计算offset和limit
        criteria.setOffset((criteria.getPage() - 1) * criteria.getPageSize());
        criteria.setLimit(criteria.getPageSize());
        
        // 优化搜索范围
        if (!StringUtils.hasText(criteria.getSearchScope())) {
            criteria.setSearchScope("all");
        }
    }
    
    /**
     * 转换实体为DTO
     * 
     * @param device 设备实体
     * @return 设备DTO
     */
    private ManagedDeviceDTO convertToDTO(ManagedDevice device) {
        if (device == null) {
            return null;
        }
        
        ManagedDeviceDTO dto = new ManagedDeviceDTO();
        BeanUtils.copyProperties(device, dto);
        
        // 设置格式化的时间字符串
        if (device.getCreatedAt() != null) {
            dto.setCreatedAtFormatted(device.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if (device.getUpdatedAt() != null) {
            dto.setUpdatedAtFormatted(device.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if (device.getLastOnlineAt() != null) {
            dto.setLastOnlineAtFormatted(device.getLastOnlineAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if (device.getActivatedAt() != null) {
            dto.setActivatedAtFormatted(device.getActivatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        
        // 设置状态描述
        if (device.getStatus() != null) {
            dto.setStatusDescription(getStatusDescription(device.getStatus()));
        }
        
        // 设置型号描述
        if (device.getModel() != null) {
            dto.setModelDescription(getModelDescription(device.getModel()));
        }
        
        return dto;
    }
    
    /**
     * 获取状态描述
     * 
     * @param status 设备状态
     * @return 状态描述
     */
    private String getStatusDescription(DeviceStatus status) {
        switch (status) {
            case ONLINE:
                return "在线";
            case OFFLINE:
                return "离线";
            case ERROR:
                return "故障";
            case MAINTENANCE:
                return "维护中";
            case PENDING:
                return "待激活";
            default:
                return status.name();
        }
    }
    
    /**
     * 获取型号描述
     * 
     * @param model 设备型号
     * @return 型号描述
     */
    private String getModelDescription(DeviceModel model) {
        switch (model) {
            case YX_EDU_2024:
                return "教育版";
            case YX_HOME_2024:
                return "家庭版";
            case YX_PRO_2024:
                return "专业版";
            default:
                return model.name();
        }
    }
}