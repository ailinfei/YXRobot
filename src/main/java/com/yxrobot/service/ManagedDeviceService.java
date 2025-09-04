package com.yxrobot.service;

import com.yxrobot.dto.ManagedDeviceDTO;
import com.yxrobot.dto.ManagedDeviceMaintenanceRecordDTO;
import com.yxrobot.dto.ManagedDeviceSearchCriteria;
import com.yxrobot.entity.*;
import com.yxrobot.exception.ManagedDeviceException;
import com.yxrobot.mapper.*;
import com.yxrobot.util.DeviceValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 设备管理服务类
 * 处理设备管理业务逻辑，支持前端列表和操作功能
 * 提供设备的CRUD操作、分页查询、搜索筛选等功能
 */
@Service
public class ManagedDeviceService {
    
    private static final Logger logger = LoggerFactory.getLogger(ManagedDeviceService.class);
    
    @Autowired
    private ManagedDeviceMapper managedDeviceMapper;
    
    @Autowired
    private ManagedDeviceValidationService validationService;
    
    @Autowired
    private ManagedDeviceSecurityService securityService;
    
    @Autowired
    private ManagedDeviceSpecificationMapper specificationMapper;
    
    @Autowired
    private ManagedDeviceUsageStatsMapper usageStatsMapper;
    
    @Autowired
    private ManagedDeviceMaintenanceRecordMapper maintenanceRecordMapper;
    
    @Autowired
    private ManagedDeviceConfigurationMapper configurationMapper;
    
    @Autowired
    private ManagedDevicePerformanceMonitorService performanceMonitorService;
    
    @Autowired
    private ManagedDeviceLargeDataOptimizationService largeDataOptimizationService;
    
    @Autowired
    private ManagedDeviceLocationMapper locationMapper;
    
    /**
     * 分页查询设备列表
     * 支持前端页面的分页、搜索、筛选需求
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
    public com.yxrobot.dto.PageResult<ManagedDeviceDTO> getManagedDevices(Integer page, Integer pageSize, 
                                                        String keyword, String status, String model, 
                                                        Long customerId, LocalDateTime startDate, 
                                                        LocalDateTime endDate, String sortBy, String sortOrder) {
        
        logger.debug("查询设备列表: page={}, pageSize={}, keyword={}", page, pageSize, keyword);
        
        long startTime = System.currentTimeMillis();
        
        try {
            // 参数验证
            validationService.validatePaginationParams(page, pageSize);
            validationService.validateSearchParams(keyword, status, model);
            
            // 参数验证和默认值设置
            page = page != null && page > 0 ? page : 1;
            pageSize = pageSize != null && pageSize > 0 ? pageSize : 20;
            
            // 创建搜索条件对象
            ManagedDeviceSearchCriteria criteria = new ManagedDeviceSearchCriteria();
            criteria.setPage(page);
            criteria.setPageSize(pageSize);
            criteria.setKeyword(keyword);
            // 修复类型转换问题
            criteria.setStatus(status != null ? DeviceStatus.fromCode(status) : null);
            criteria.setModel(model != null ? DeviceModel.fromCode(model) : null);
            criteria.setCustomerId(customerId);
            criteria.setSortBy(sortBy);
            criteria.setSortOrder(sortOrder);
            
            // 应用大数据量优化
            criteria = largeDataOptimizationService.optimizePaginationCriteria(criteria);
            
            // 检查是否为大数据量查询并应用优化
            if (largeDataOptimizationService.isLargeDataQuery(criteria)) {
                logger.info("检测到大数据量查询，应用性能优化");
                Map<String, Object> optimization = largeDataOptimizationService.optimizeLargeDataQuery(criteria);
                if (optimization.containsKey("optimizedCriteria")) {
                    criteria = (ManagedDeviceSearchCriteria) optimization.get("optimizedCriteria");
                }
            }
        
            // 计算偏移量
            int offset = (criteria.getPage() - 1) * criteria.getPageSize();
            
            // 记录查询开始时间
            long queryStartTime = System.currentTimeMillis();
            
            // 查询设备列表
            List<ManagedDevice> devices = managedDeviceMapper.selectByPage(
                offset, criteria.getPageSize(), criteria.getKeyword(), 
                criteria.getStatus() != null ? criteria.getStatus().getCode() : null, 
                criteria.getModel() != null ? criteria.getModel().getCode() : null, 
                criteria.getCustomerId(), startDate, endDate, 
                criteria.getSortBy(), criteria.getSortOrder()
            );
            
            // 查询总数
            Integer total = managedDeviceMapper.countByConditions(
                criteria.getKeyword(), 
                criteria.getStatus() != null ? criteria.getStatus().getCode() : null, 
                criteria.getModel() != null ? criteria.getModel().getCode() : null, 
                criteria.getCustomerId(), startDate, endDate
            );
            
            // 记录数据库查询性能
            long queryTime = System.currentTimeMillis() - queryStartTime;
            performanceMonitorService.recordDatabaseQueryPerformance("selectDevicesByPage", queryTime);
            
            // 监控分页查询性能
            largeDataOptimizationService.monitorPaginationPerformance(criteria, queryTime, devices.size());
            
            // 转换为DTO
            List<ManagedDeviceDTO> deviceDTOs = devices.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
            // 计算总页数
            int totalPages = (int) Math.ceil((double) total / criteria.getPageSize());
            
            // 记录业务指标
            performanceMonitorService.recordBusinessMetric("device_list_query_count", deviceDTOs.size());
            performanceMonitorService.recordBusinessMetric("device_list_total_count", total);
            
            // 记录数据访问事件
            securityService.logDataAccessEvent("READ", "ManagedDevice", "LIST", "SYSTEM", true);
            
            // 记录整体性能
            long totalTime = System.currentTimeMillis() - startTime;
            performanceMonitorService.recordApiPerformance("getManagedDevices", totalTime);
            
            // 使用DTO包中的PageResult类
            com.yxrobot.dto.PageResult<ManagedDeviceDTO> pageResult = new com.yxrobot.dto.PageResult<>(deviceDTOs, total.longValue(), criteria.getPage(), criteria.getPageSize());
            return pageResult;
            
        } catch (Exception e) {
            logger.error("查询设备列表失败", e);
            securityService.logDataAccessEvent("READ", "ManagedDevice", "LIST", "SYSTEM", false);
            
            // 记录失败的性能指标
            long totalTime = System.currentTimeMillis() - startTime;
            performanceMonitorService.recordApiPerformance("getManagedDevices_failed", totalTime);
            
            throw e;
        }
    }
    
    /**
     * 根据ID查询设备详细信息
     * 返回完整的设备详细信息，包含关联数据
     * 
     * @param id 设备ID
     * @return 设备详细信息
     */
    public ManagedDeviceDTO getManagedDeviceById(Long id) {
        logger.debug("查询设备详情: id={}", id);
        
        try {
            // 参数验证
            DeviceValidationUtils.validateId(id, "deviceId");
            
            // 查询设备基本信息
            ManagedDevice device = managedDeviceMapper.selectByIdWithAssociations(id);
            
            if (device == null) {
                securityService.logDataAccessEvent("READ", "ManagedDevice", id.toString(), "SYSTEM", false);
                throw ManagedDeviceException.deviceNotFound(id);
            }
            
            // 记录数据访问事件
            securityService.logDataAccessEvent("READ", "ManagedDevice", id.toString(), "SYSTEM", true);
            
            // 转换为DTO（包含完整关联信息）
            return convertToDTOWithAssociations(device);
            
        } catch (Exception e) {
            logger.error("查询设备详情失败: id={}", id, e);
            if (!(e instanceof ManagedDeviceException)) {
                securityService.logDataAccessEvent("READ", "ManagedDevice", id.toString(), "SYSTEM", false);
            }
            throw e;
        }
    }
    
    /**
     * 创建设备
     * 支持设备创建功能
     * 
     * @param deviceDTO 设备数据
     * @return 创建的设备信息
     */
    @Transactional
    public ManagedDeviceDTO createManagedDevice(ManagedDeviceDTO deviceDTO) {
        logger.debug("创建设备: serialNumber={}", deviceDTO != null ? deviceDTO.getSerialNumber() : null);
        
        try {
            // 数据验证
            validationService.validateDeviceCreation(deviceDTO);
        
        // 转换DTO为实体
        ManagedDevice device = convertToEntity(deviceDTO);
        device.setCreatedAt(LocalDateTime.now());
        device.setUpdatedAt(LocalDateTime.now());
        device.setIsDeleted(false);
        
            // 插入设备
            int result = managedDeviceMapper.insert(device);
            if (result <= 0) {
                securityService.logDataAccessEvent("CREATE", "ManagedDevice", deviceDTO.getSerialNumber(), "SYSTEM", false);
                throw ManagedDeviceException.operationFailed("创建设备", null, "数据库插入失败");
            }
            
            // 创建关联数据
            createAssociatedData(device, deviceDTO);
            
            // 记录数据访问事件
            securityService.logDataAccessEvent("CREATE", "ManagedDevice", device.getId().toString(), "SYSTEM", true);
            
            logger.info("设备创建成功: id={}, serialNumber={}", device.getId(), device.getSerialNumber());
            
            // 返回创建的设备信息
            return getManagedDeviceById(device.getId());
            
        } catch (Exception e) {
            logger.error("创建设备失败: serialNumber={}", deviceDTO != null ? deviceDTO.getSerialNumber() : null, e);
            if (!(e instanceof ManagedDeviceException)) {
                securityService.logDataAccessEvent("CREATE", "ManagedDevice", 
                    deviceDTO != null ? deviceDTO.getSerialNumber() : "UNKNOWN", "SYSTEM", false);
            }
            throw e;
        }
    }
    
    /**
     * 更新设备
     * 支持设备编辑功能
     * 
     * @param id 设备ID
     * @param deviceDTO 设备数据
     * @return 更新后的设备信息
     */
    @Transactional
    public ManagedDeviceDTO updateManagedDevice(Long id, ManagedDeviceDTO deviceDTO) {
        logger.debug("更新设备: id={}, serialNumber={}", id, deviceDTO != null ? deviceDTO.getSerialNumber() : null);
        
        try {
            // 数据验证
            validationService.validateDeviceUpdate(id, deviceDTO);
            
            // 检查设备是否存在
            ManagedDevice existingDevice = managedDeviceMapper.selectById(id);
            if (existingDevice == null) {
                throw ManagedDeviceException.deviceNotFound(id);
            }
        
        // 转换DTO为实体
        ManagedDevice device = convertToEntity(deviceDTO);
        device.setId(id);
        device.setUpdatedAt(LocalDateTime.now());
        
            // 更新设备
            int result = managedDeviceMapper.updateById(device);
            if (result <= 0) {
                securityService.logDataAccessEvent("UPDATE", "ManagedDevice", id.toString(), "SYSTEM", false);
                throw ManagedDeviceException.operationFailed("更新设备", id, "数据库更新失败");
            }
            
            // 更新关联数据
            updateAssociatedData(device, deviceDTO);
            
            // 记录数据访问事件
            securityService.logDataAccessEvent("UPDATE", "ManagedDevice", id.toString(), "SYSTEM", true);
            
            logger.info("设备更新成功: id={}, serialNumber={}", id, device.getSerialNumber());
            
            // 返回更新后的设备信息
            return getManagedDeviceById(id);
            
        } catch (Exception e) {
            logger.error("更新设备失败: id={}", id, e);
            if (!(e instanceof ManagedDeviceException)) {
                securityService.logDataAccessEvent("UPDATE", "ManagedDevice", id.toString(), "SYSTEM", false);
            }
            throw e;
        }
    }
    
    /**
     * 删除设备（软删除）
     * 支持设备删除功能
     * 
     * @param id 设备ID
     */
    @Transactional
    public void deleteManagedDevice(Long id) {
        logger.debug("删除设备: id={}", id);
        
        try {
            // 参数验证
            DeviceValidationUtils.validateId(id, "deviceId");
            
            // 检查设备是否存在
            ManagedDevice device = managedDeviceMapper.selectById(id);
            if (device == null) {
                throw ManagedDeviceException.deviceNotFound(id);
            }
            
            // 软删除设备
            int result = managedDeviceMapper.deleteById(id);
            if (result <= 0) {
                securityService.logDataAccessEvent("DELETE", "ManagedDevice", id.toString(), "SYSTEM", false);
                throw ManagedDeviceException.operationFailed("删除设备", id, "数据库删除失败");
            }
            
            // 删除关联数据
            deleteAssociatedData(id);
            
            // 记录数据访问事件
            securityService.logDataAccessEvent("DELETE", "ManagedDevice", id.toString(), "SYSTEM", true);
            
            logger.info("设备删除成功: id={}, serialNumber={}", id, device.getSerialNumber());
            
        } catch (Exception e) {
            logger.error("删除设备失败: id={}", id, e);
            if (!(e instanceof ManagedDeviceException)) {
                securityService.logDataAccessEvent("DELETE", "ManagedDevice", id.toString(), "SYSTEM", false);
            }
            throw e;
        }
    }
    
    /**
     * 批量删除设备
     * 
     * @param ids 设备ID列表
     */
    @Transactional
    public void deleteManagedDevicesBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("设备ID列表不能为空");
        }
        
        // 批量软删除设备
        int result = managedDeviceMapper.deleteBatchByIds(ids);
        if (result <= 0) {
            throw new RuntimeException("批量删除设备失败");
        }
        
        // 删除关联数据
        for (Long id : ids) {
            deleteAssociatedData(id);
        }
    }
    
    /**
     * 根据序列号查询设备
     * 
     * @param serialNumber 设备序列号
     * @return 设备信息
     */
    public ManagedDeviceDTO getManagedDeviceBySerialNumber(String serialNumber) {
        if (serialNumber == null || serialNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("设备序列号不能为空");
        }
        
        ManagedDevice device = managedDeviceMapper.selectBySerialNumber(serialNumber);
        
        if (device == null) {
            return null;
        }
        
        return convertToDTO(device);
    }
    
    /**
     * 根据客户ID查询设备列表
     * 
     * @param customerId 客户ID
     * @return 设备列表
     */
    public List<ManagedDeviceDTO> getManagedDevicesByCustomerId(Long customerId) {
        if (customerId == null) {
            throw new IllegalArgumentException("客户ID不能为空");
        }
        
        List<ManagedDevice> devices = managedDeviceMapper.selectByCustomerId(customerId);
        
        return devices.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * 转换实体为DTO（基本信息）
     */
    private ManagedDeviceDTO convertToDTO(ManagedDevice device) {
        if (device == null) {
            return null;
        }
        
        ManagedDeviceDTO dto = new ManagedDeviceDTO();
        dto.setId(device.getId() != null ? device.getId().toString() : null);
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
    
    /**
     * 转换实体为DTO（包含关联信息）
     */
    private ManagedDeviceDTO convertToDTOWithAssociations(ManagedDevice device) {
        ManagedDeviceDTO dto = convertToDTO(device);
        
        if (dto == null) {
            return null;
        }
        
        // 转换技术参数
        if (device.getSpecifications() != null) {
            ManagedDeviceDTO.DeviceSpecificationDTO specDTO = new ManagedDeviceDTO.DeviceSpecificationDTO();
            specDTO.setCpu(device.getSpecifications().getCpu());
            specDTO.setMemory(device.getSpecifications().getMemory());
            specDTO.setStorage(device.getSpecifications().getStorage());
            specDTO.setDisplay(device.getSpecifications().getDisplay());
            specDTO.setBattery(device.getSpecifications().getBattery());
            specDTO.setConnectivity(device.getSpecifications().getConnectivity());
            dto.setSpecifications(specDTO);
        }
        
        // 转换使用统计
        if (device.getUsageStats() != null) {
            ManagedDeviceDTO.DeviceUsageStatsDTO statsDTO = new ManagedDeviceDTO.DeviceUsageStatsDTO();
            statsDTO.setTotalRuntime(device.getUsageStats().getTotalRuntime());
            statsDTO.setUsageCount(device.getUsageStats().getUsageCount());
            statsDTO.setAverageSessionTime(device.getUsageStats().getAverageSessionTime());
            statsDTO.setLastUsedAt(device.getUsageStats().getLastUsedAt());
            dto.setUsageStats(statsDTO);
        }
        
        // 转换配置信息
        if (device.getConfiguration() != null) {
            ManagedDeviceDTO.DeviceConfigurationDTO configDTO = new ManagedDeviceDTO.DeviceConfigurationDTO();
            configDTO.setLanguage(device.getConfiguration().getLanguage());
            configDTO.setTimezone(device.getConfiguration().getTimezone());
            configDTO.setAutoUpdate(device.getConfiguration().getAutoUpdate());
            configDTO.setDebugMode(device.getConfiguration().getDebugMode());
            configDTO.setCustomSettings(device.getConfiguration().getCustomSettings());
            dto.setConfiguration(configDTO);
        }
        
        // 转换位置信息
        if (device.getLocation() != null) {
            ManagedDeviceDTO.DeviceLocationDTO locationDTO = new ManagedDeviceDTO.DeviceLocationDTO();
            locationDTO.setLatitude(device.getLocation().getLatitude() != null ? 
                device.getLocation().getLatitude().doubleValue() : null);
            locationDTO.setLongitude(device.getLocation().getLongitude() != null ? 
                device.getLocation().getLongitude().doubleValue() : null);
            locationDTO.setAddress(device.getLocation().getAddress());
            locationDTO.setLastUpdated(device.getLocation().getLastUpdated());
            dto.setLocation(locationDTO);
        }
        
        // 转换维护记录
        if (device.getMaintenanceRecords() != null && !device.getMaintenanceRecords().isEmpty()) {
            List<ManagedDeviceMaintenanceRecordDTO> recordDTOs = device.getMaintenanceRecords().stream()
                .map(this::convertMaintenanceRecordToDTO)
                .collect(Collectors.toList());
            dto.setMaintenanceRecords(recordDTOs);
        }
        
        return dto;
    }
    
    /**
     * 转换维护记录为DTO
     */
    private ManagedDeviceMaintenanceRecordDTO convertMaintenanceRecordToDTO(ManagedDeviceMaintenanceRecord record) {
        if (record == null) {
            return null;
        }
        
        ManagedDeviceMaintenanceRecordDTO dto = new ManagedDeviceMaintenanceRecordDTO();
        dto.setId(record.getId() != null ? record.getId().toString() : null);
        dto.setDeviceId(record.getDeviceId() != null ? record.getDeviceId().toString() : null);
        dto.setType(record.getType() != null ? record.getType().getCode() : null);
        dto.setDescription(record.getDescription());
        dto.setTechnician(record.getTechnician());
        dto.setStartTime(record.getStartTime());
        dto.setEndTime(record.getEndTime());
        dto.setStatus(record.getStatus() != null ? record.getStatus().getCode() : null);
        dto.setCost(record.getCost());
        dto.setParts(record.getParts());
        dto.setNotes(record.getNotes());
        dto.setCreatedAt(record.getCreatedAt());
        dto.setUpdatedAt(record.getUpdatedAt());
        
        return dto;
    }
    
    /**
     * 转换DTO为实体
     */
    private ManagedDevice convertToEntity(ManagedDeviceDTO dto) {
        if (dto == null) {
            return null;
        }
        
        ManagedDevice device = new ManagedDevice();
        device.setSerialNumber(dto.getSerialNumber());
        device.setModel(dto.getModel() != null ? DeviceModel.fromCode(dto.getModel()) : null);
        device.setStatus(dto.getStatus() != null ? DeviceStatus.fromCode(dto.getStatus()) : DeviceStatus.OFFLINE);
        device.setFirmwareVersion(dto.getFirmwareVersion());
        device.setCustomerId(dto.getCustomerId() != null ? Long.valueOf(dto.getCustomerId()) : null);
        device.setCustomerName(dto.getCustomerName());
        device.setCustomerPhone(dto.getCustomerPhone());
        device.setLastOnlineAt(dto.getLastOnlineAt());
        device.setActivatedAt(dto.getActivatedAt());
        device.setCreatedBy(dto.getCreatedBy());
        device.setNotes(dto.getNotes());
        
        return device;
    }
    
    /**
     * 创建关联数据
     */
    private void createAssociatedData(ManagedDevice device, ManagedDeviceDTO dto) {
        // 创建技术参数
        if (dto.getSpecifications() != null) {
            ManagedDeviceSpecification spec = new ManagedDeviceSpecification();
            spec.setDeviceId(device.getId());
            spec.setCpu(dto.getSpecifications().getCpu());
            spec.setMemory(dto.getSpecifications().getMemory());
            spec.setStorage(dto.getSpecifications().getStorage());
            spec.setDisplay(dto.getSpecifications().getDisplay());
            spec.setBattery(dto.getSpecifications().getBattery());
            spec.setConnectivity(dto.getSpecifications().getConnectivity());
            specificationMapper.insert(spec);
        }
        
        // 创建使用统计
        if (dto.getUsageStats() != null) {
            ManagedDeviceUsageStats stats = new ManagedDeviceUsageStats();
            stats.setDeviceId(device.getId());
            stats.setTotalRuntime(dto.getUsageStats().getTotalRuntime() != null ? 
                dto.getUsageStats().getTotalRuntime() : 0);
            stats.setUsageCount(dto.getUsageStats().getUsageCount() != null ? 
                dto.getUsageStats().getUsageCount() : 0);
            stats.setAverageSessionTime(dto.getUsageStats().getAverageSessionTime() != null ? 
                dto.getUsageStats().getAverageSessionTime() : 0);
            stats.setLastUsedAt(dto.getUsageStats().getLastUsedAt());
            usageStatsMapper.insert(stats);
        }
        
        // 创建默认配置
        ManagedDeviceConfiguration config = new ManagedDeviceConfiguration();
        config.setDeviceId(device.getId());
        config.setLanguage("zh-CN");
        config.setTimezone("Asia/Shanghai");
        config.setAutoUpdate(true);
        config.setDebugMode(false);
        configurationMapper.insert(config);
    }
    
    /**
     * 更新关联数据
     */
    private void updateAssociatedData(ManagedDevice device, ManagedDeviceDTO dto) {
        // 更新技术参数
        if (dto.getSpecifications() != null) {
            ManagedDeviceSpecification spec = specificationMapper.selectByDeviceId(device.getId());
            if (spec != null) {
                spec.setCpu(dto.getSpecifications().getCpu());
                spec.setMemory(dto.getSpecifications().getMemory());
                spec.setStorage(dto.getSpecifications().getStorage());
                spec.setDisplay(dto.getSpecifications().getDisplay());
                spec.setBattery(dto.getSpecifications().getBattery());
                spec.setConnectivity(dto.getSpecifications().getConnectivity());
                specificationMapper.updateByDeviceId(spec);
            }
        }
        
        // 更新使用统计
        if (dto.getUsageStats() != null) {
            ManagedDeviceUsageStats stats = usageStatsMapper.selectByDeviceId(device.getId());
            if (stats != null) {
                stats.setTotalRuntime(dto.getUsageStats().getTotalRuntime());
                stats.setUsageCount(dto.getUsageStats().getUsageCount());
                stats.setAverageSessionTime(dto.getUsageStats().getAverageSessionTime());
                stats.setLastUsedAt(dto.getUsageStats().getLastUsedAt());
                usageStatsMapper.updateByDeviceId(stats);
            }
        }
    }
    
    /**
     * 删除关联数据
     */
    private void deleteAssociatedData(Long deviceId) {
        // 删除技术参数
        specificationMapper.deleteByDeviceId(deviceId);
        
        // 删除使用统计
        usageStatsMapper.deleteByDeviceId(deviceId);
        
        // 删除维护记录
        maintenanceRecordMapper.deleteByDeviceId(deviceId);
        
        // 删除配置
        configurationMapper.deleteByDeviceId(deviceId);
        
        // 删除位置信息
        locationMapper.deleteByDeviceId(deviceId);
    }
    
    /**
     * 分页结果类
     */
    public static class PageResult<T> {
        private List<T> list;
        private Integer total;
        private Integer page;
        private Integer pageSize;
        private Integer totalPages;
        
        public PageResult(List<T> list, Integer total, Integer page, Integer pageSize, Integer totalPages) {
            this.list = list != null ? list : new ArrayList<>();
            this.total = total != null ? total : 0;
            this.page = page != null ? page : 1;
            this.pageSize = pageSize != null ? pageSize : 20;
            this.totalPages = totalPages != null ? totalPages : 0;
        }
        
        // Getter和Setter方法
        public List<T> getList() { return list; }
        public void setList(List<T> list) { this.list = list; }
        
        public Integer getTotal() { return total; }
        public void setTotal(Integer total) { this.total = total; }
        
        public Integer getPage() { return page; }
        public void setPage(Integer page) { this.page = page; }
        
        public Integer getPageSize() { return pageSize; }
        public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
        
        public Integer getTotalPages() { return totalPages; }
        public void setTotalPages(Integer totalPages) { this.totalPages = totalPages; }
    }
}