package com.yxrobot.service;

import com.yxrobot.dto.ManagedDeviceDTO;
import com.yxrobot.dto.ManagedDeviceSearchCriteria;
import com.yxrobot.dto.PageResult;
import com.yxrobot.entity.ManagedDevice;
import com.yxrobot.entity.DeviceStatus;
import com.yxrobot.entity.DeviceModel;
import com.yxrobot.exception.ManagedDeviceException;
import com.yxrobot.mapper.ManagedDeviceMapper;
import com.yxrobot.util.DeviceValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 设备管理搜索服务
 * 提供高级搜索和筛选功能，优化搜索性能
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@Service
public class ManagedDeviceSearchService {
    
    private static final Logger logger = LoggerFactory.getLogger(ManagedDeviceSearchService.class);
    
    @Autowired
    private ManagedDeviceMapper managedDeviceMapper;
    
    @Autowired
    private ManagedDeviceValidationService validationService;
    
    @Autowired
    private ManagedDeviceSecurityService securityService;
    
    /**
     * 高级搜索设备
     * 
     * @param criteria 搜索条件
     * @return 分页结果
     */
    public PageResult<ManagedDeviceDTO> advancedSearch(ManagedDeviceSearchCriteria criteria) {
        logger.debug("执行高级搜索: {}", criteria);
        
        try {
            // 验证搜索条件
            validateSearchCriteria(criteria);
            
            // 记录搜索事件
            securityService.logDataAccessEvent("SEARCH", "ManagedDevice", "ADVANCED_SEARCH", "SYSTEM", true);
            
            // 执行搜索查询
            List<ManagedDevice> devices = managedDeviceMapper.advancedSearch(criteria);
            
            // 统计总数
            Integer total = managedDeviceMapper.countAdvancedSearch(criteria);
            
            // 转换为DTO
            List<ManagedDeviceDTO> deviceDTOs = devices.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            // 计算总页数
            int totalPages = (int) Math.ceil((double) total / criteria.getPageSize());
            
            logger.debug("搜索完成: 找到{}条记录", total);
            
            // 明确指定PageResult的泛型类型
            PageResult<ManagedDeviceDTO> pageResult = new PageResult<>(deviceDTOs, total.longValue(), criteria.getPage(), criteria.getPageSize());
            return pageResult;
            
        } catch (Exception e) {
            logger.error("高级搜索失败: {}", criteria, e);
            securityService.logDataAccessEvent("SEARCH", "ManagedDevice", "ADVANCED_SEARCH", "SYSTEM", false);
            throw e;
        }
    }
    
    /**
     * 快速搜索设备（基于关键词）
     * 
     * @param keyword 搜索关键词
     * @param page 页码
     * @param pageSize 页大小
     * @return 分页结果
     */
    public PageResult<ManagedDeviceDTO> quickSearch(String keyword, Integer page, Integer pageSize) {
        logger.debug("执行快速搜索: keyword={}, page={}, pageSize={}", keyword, page, pageSize);
        
        try {
            // 创建搜索条件
            ManagedDeviceSearchCriteria criteria = new ManagedDeviceSearchCriteria();
            criteria.setKeyword(keyword);
            criteria.setPage(page);
            criteria.setPageSize(pageSize);
            
            return advancedSearch(criteria);
            
        } catch (Exception e) {
            logger.error("快速搜索失败: keyword={}", keyword, e);
            throw e;
        }
    }
    
    /**
     * 按状态搜索设备
     * 
     * @param statusList 状态列表
     * @param page 页码
     * @param pageSize 页大小
     * @return 分页结果
     */
    public PageResult<ManagedDeviceDTO> searchByStatus(List<String> statusList, Integer page, Integer pageSize) {
        logger.debug("按状态搜索: statusList={}, page={}, pageSize={}", statusList, page, pageSize);
        
        try {
            // 创建搜索条件
            ManagedDeviceSearchCriteria criteria = new ManagedDeviceSearchCriteria();
            // 将String列表转换为DeviceStatus列表
            if (statusList != null) {
                List<DeviceStatus> deviceStatusList = statusList.stream()
                    .map(DeviceStatus::fromCode)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
                criteria.setStatusList(deviceStatusList);
            }
            criteria.setPage(page);
            criteria.setPageSize(pageSize);
            
            return advancedSearch(criteria);
            
        } catch (Exception e) {
            logger.error("按状态搜索失败: statusList={}", statusList, e);
            throw e;
        }
    }
    
    /**
     * 按型号搜索设备
     * 
     * @param modelList 型号列表
     * @param page 页码
     * @param pageSize 页大小
     * @return 分页结果
     */
    public PageResult<ManagedDeviceDTO> searchByModel(List<String> modelList, Integer page, Integer pageSize) {
        logger.debug("按型号搜索: modelList={}, page={}, pageSize={}", modelList, page, pageSize);
        
        try {
            // 创建搜索条件
            ManagedDeviceSearchCriteria criteria = new ManagedDeviceSearchCriteria();
            // 将String列表转换为DeviceModel列表
            if (modelList != null) {
                List<DeviceModel> deviceModelList = modelList.stream()
                    .map(DeviceModel::fromCode)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
                criteria.setModelList(deviceModelList);
            }
            criteria.setPage(page);
            criteria.setPageSize(pageSize);
            
            return advancedSearch(criteria);
            
        } catch (Exception e) {
            logger.error("按型号搜索失败: modelList={}", modelList, e);
            throw e;
        }
    }
    
    /**
     * 按客户搜索设备
     * 
     * @param customerId 客户ID
     * @param customerName 客户名称（模糊搜索）
     * @param page 页码
     * @param pageSize 页大小
     * @return 分页结果
     */
    public PageResult<ManagedDeviceDTO> searchByCustomer(Long customerId, String customerName, 
                                                        Integer page, Integer pageSize) {
        logger.debug("按客户搜索: customerId={}, customerName={}, page={}, pageSize={}", 
                    customerId, customerName, page, pageSize);
        
        try {
            // 创建搜索条件
            ManagedDeviceSearchCriteria criteria = new ManagedDeviceSearchCriteria();
            criteria.setCustomerId(customerId);
            criteria.setCustomerName(customerName);
            criteria.setPage(page);
            criteria.setPageSize(pageSize);
            
            return advancedSearch(criteria);
            
        } catch (Exception e) {
            logger.error("按客户搜索失败: customerId={}, customerName={}", customerId, customerName, e);
            throw e;
        }
    }
    
    /**
     * 按时间范围搜索设备
     * 
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param dateType 时间类型（created, lastOnline, activated）
     * @param page 页码
     * @param pageSize 页大小
     * @return 分页结果
     */
    public PageResult<ManagedDeviceDTO> searchByDateRange(LocalDateTime startDate, LocalDateTime endDate, 
                                                         String dateType, Integer page, Integer pageSize) {
        logger.debug("按时间范围搜索: startDate={}, endDate={}, dateType={}, page={}, pageSize={}", 
                    startDate, endDate, dateType, page, pageSize);
        
        try {
            // 创建搜索条件
            ManagedDeviceSearchCriteria criteria = new ManagedDeviceSearchCriteria();
            criteria.setPage(page);
            criteria.setPageSize(pageSize);
            
            // 根据时间类型设置相应的时间范围
            switch (dateType.toLowerCase()) {
                case "created":
                    criteria.setCreatedStartDate(startDate);
                    criteria.setCreatedEndDate(endDate);
                    break;
                case "lastonline":
                    criteria.setLastOnlineStartDate(startDate);
                    criteria.setLastOnlineEndDate(endDate);
                    break;
                case "activated":
                    criteria.setActivatedStartDate(startDate);
                    criteria.setActivatedEndDate(endDate);
                    break;
                default:
                    throw ManagedDeviceException.validationFailed("dateType", dateType, "无效的时间类型");
            }
            
            return advancedSearch(criteria);
            
        } catch (Exception e) {
            logger.error("按时间范围搜索失败: startDate={}, endDate={}, dateType={}", 
                        startDate, endDate, dateType, e);
            throw e;
        }
    }
    
    /**
     * 搜索在线设备
     * 
     * @param page 页码
     * @param pageSize 页大小
     * @return 分页结果
     */
    public PageResult<ManagedDeviceDTO> searchOnlineDevices(Integer page, Integer pageSize) {
        logger.debug("搜索在线设备: page={}, pageSize={}", page, pageSize);
        
        try {
            // 创建搜索条件
            ManagedDeviceSearchCriteria criteria = new ManagedDeviceSearchCriteria();
            criteria.setIsOnline(true);
            criteria.setPage(page);
            criteria.setPageSize(pageSize);
            
            return advancedSearch(criteria);
            
        } catch (Exception e) {
            logger.error("搜索在线设备失败", e);
            throw e;
        }
    }
    
    /**
     * 搜索离线设备
     * 
     * @param page 页码
     * @param pageSize 页大小
     * @return 分页结果
     */
    public PageResult<ManagedDeviceDTO> searchOfflineDevices(Integer page, Integer pageSize) {
        logger.debug("搜索离线设备: page={}, pageSize={}", page, pageSize);
        
        try {
            // 创建搜索条件
            ManagedDeviceSearchCriteria criteria = new ManagedDeviceSearchCriteria();
            criteria.setIsOnline(false);
            criteria.setPage(page);
            criteria.setPageSize(pageSize);
            
            return advancedSearch(criteria);
            
        } catch (Exception e) {
            logger.error("搜索离线设备失败", e);
            throw e;
        }
    }
    
    /**
     * 搜索未激活设备
     * 
     * @param page 页码
     * @param pageSize 页大小
     * @return 分页结果
     */
    public PageResult<ManagedDeviceDTO> searchUnactivatedDevices(Integer page, Integer pageSize) {
        logger.debug("搜索未激活设备: page={}, pageSize={}", page, pageSize);
        
        try {
            // 创建搜索条件
            ManagedDeviceSearchCriteria criteria = new ManagedDeviceSearchCriteria();
            criteria.setIsActivated(false);
            criteria.setPage(page);
            criteria.setPageSize(pageSize);
            
            return advancedSearch(criteria);
            
        } catch (Exception e) {
            logger.error("搜索未激活设备失败", e);
            throw e;
        }
    }
    
    /**
     * 获取搜索建议（自动完成）
     * 
     * @param field 字段名（serialNumber, customerName, model等）
     * @param query 查询字符串
     * @param limit 返回数量限制
     * @return 建议列表
     */
    public List<String> getSearchSuggestions(String field, String query, Integer limit) {
        logger.debug("获取搜索建议: field={}, query={}, limit={}", field, query, limit);
        
        try {
            // 验证输入
            DeviceValidationUtils.validateAgainstXSS(query, "搜索查询");
            DeviceValidationUtils.validateAgainstSQLInjection(query, "搜索查询");
            
            if (limit == null || limit <= 0) {
                limit = 10;
            }
            if (limit > 50) {
                limit = 50; // 限制最大返回数量
            }
            
            // 根据字段类型获取建议
            List<String> suggestions = managedDeviceMapper.getSearchSuggestions(field, query, limit);
            
            logger.debug("获取到{}条搜索建议", suggestions.size());
            
            return suggestions;
            
        } catch (Exception e) {
            logger.error("获取搜索建议失败: field={}, query={}", field, query, e);
            throw e;
        }
    }
    
    /**
     * 验证搜索条件
     * 
     * @param criteria 搜索条件
     */
    private void validateSearchCriteria(ManagedDeviceSearchCriteria criteria) {
        if (criteria == null) {
            throw ManagedDeviceException.validationFailed("searchCriteria", null, "搜索条件不能为空");
        }
        
        // 验证分页参数
        validationService.validatePaginationParams(criteria.getPage(), criteria.getPageSize());
        
        // 验证时间范围
        if (!criteria.isValid()) {
            throw ManagedDeviceException.validationFailed("dateRange", null, "时间范围无效，开始时间不能晚于结束时间");
        }
        
        // 验证关键词
        if (criteria.getKeyword() != null) {
            DeviceValidationUtils.validateAgainstXSS(criteria.getKeyword(), "搜索关键词");
            DeviceValidationUtils.validateAgainstSQLInjection(criteria.getKeyword(), "搜索关键词");
            
            if (criteria.getKeyword().trim().length() > 100) {
                throw ManagedDeviceException.validationFailed("keyword", criteria.getKeyword(), 
                    "搜索关键词长度不能超过100字符");
            }
        }
        
        // 验证客户名称
        if (criteria.getCustomerName() != null) {
            DeviceValidationUtils.validateAgainstXSS(criteria.getCustomerName(), "客户名称");
            DeviceValidationUtils.validateAgainstSQLInjection(criteria.getCustomerName(), "客户名称");
        }
        
        // 验证备注
        if (criteria.getNotes() != null) {
            DeviceValidationUtils.validateAgainstXSS(criteria.getNotes(), "备注");
            DeviceValidationUtils.validateAgainstSQLInjection(criteria.getNotes(), "备注");
        }
        
        // 验证创建人
        if (criteria.getCreatedBy() != null) {
            DeviceValidationUtils.validateAgainstXSS(criteria.getCreatedBy(), "创建人");
            DeviceValidationUtils.validateAgainstSQLInjection(criteria.getCreatedBy(), "创建人");
        }
        
        // 验证排序字段
        if (criteria.getSortBy() != null) {
            String[] allowedSortFields = {
                "id", "serialNumber", "model", "status", "firmwareVersion", 
                "customerName", "lastOnlineAt", "activatedAt", "createdAt", "updatedAt"
            };
            
            boolean isValidSortField = false;
            for (String field : allowedSortFields) {
                if (field.equals(criteria.getSortBy())) {
                    isValidSortField = true;
                    break;
                }
            }
            
            if (!isValidSortField) {
                throw ManagedDeviceException.validationFailed("sortBy", criteria.getSortBy(), "无效的排序字段");
            }
        }
        
        // 验证排序方向
        if (criteria.getSortOrder() != null) {
            if (!"ASC".equalsIgnoreCase(criteria.getSortOrder()) && 
                !"DESC".equalsIgnoreCase(criteria.getSortOrder())) {
                throw ManagedDeviceException.validationFailed("sortOrder", criteria.getSortOrder(), 
                    "排序方向只能是ASC或DESC");
            }
        }
    }
    
    /**
     * 转换实体为DTO
     * 
     * @param device 设备实体
     * @return 设备DTO
     */
    private ManagedDeviceDTO convertToDTO(ManagedDevice device) {
        // 这里应该调用现有的转换方法
        // 为了简化，这里只做基本转换
        ManagedDeviceDTO dto = new ManagedDeviceDTO();
        dto.setId(device.getId().toString());
        dto.setSerialNumber(device.getSerialNumber());
        dto.setModel(device.getModel() != null ? device.getModel().getCode() : null);
        dto.setStatus(device.getStatus() != null ? device.getStatus().getCode() : null);
        dto.setFirmwareVersion(device.getFirmwareVersion());
        dto.setCustomerId(device.getCustomerId() != null ? device.getCustomerId().toString() : null);
        dto.setCustomerName(device.getCustomerName());
        dto.setCustomerPhone(device.getCustomerPhone());
        dto.setLastOnlineAt(device.getLastOnlineAt());
        dto.setActivatedAt(device.getActivatedAt());
        dto.setCreatedAt(device.getCreatedAt());
        dto.setUpdatedAt(device.getUpdatedAt());
        dto.setCreatedBy(device.getCreatedBy());
        dto.setNotes(device.getNotes());
        
        return dto;
    }
}