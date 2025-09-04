package com.yxrobot.service;

import com.yxrobot.dto.DeviceMonitoringDataDTO;
import com.yxrobot.dto.DeviceMapDataDTO;
import com.yxrobot.entity.DeviceMonitoringData;
import com.yxrobot.entity.DevicePerformanceMetrics;
import com.yxrobot.entity.DeviceNetworkStatus;
import com.yxrobot.entity.DeviceStatus;
import com.yxrobot.mapper.DeviceMonitoringDataMapper;
import com.yxrobot.mapper.DevicePerformanceMetricsMapper;
import com.yxrobot.mapper.DeviceNetworkStatusMapper;
import com.yxrobot.util.DeviceMonitoringStatsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 设备监控数据服务类
 * 处理设备监控数据业务逻辑，支持前端设备列表和操作功能
 * 
 * 主要功能：
 * - 分页查询设备监控数据
 * - 设备搜索和筛选
 * - 设备详情查询
 * - 设备状态更新
 * - 地图数据查询
 * 
 * @author YXRobot Development Team
 * @since 2024
 */
@Service
@Transactional(readOnly = true)
public class DeviceMonitoringService {
    
    private static final Logger logger = LoggerFactory.getLogger(DeviceMonitoringService.class);
    
    @Autowired
    private DeviceMonitoringDataMapper deviceMonitoringDataMapper;
    
    @Autowired
    private DevicePerformanceMetricsMapper devicePerformanceMetricsMapper;
    
    @Autowired
    private DeviceNetworkStatusMapper deviceNetworkStatusMapper;
    
    /**
     * 分页查询设备监控数据列表
     * 支持前端页面的分页、搜索、筛选需求
     * 
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @param keyword 搜索关键词
     * @param status 设备状态筛选
     * @param model 设备型号筛选
     * @param customerName 客户名称筛选
     * @return 分页结果
     */
    public PageResult<DeviceMonitoringDataDTO> getMonitoringDevices(
            Integer page, Integer size, String keyword, String status, 
            String model, String customerName) {
        
        logger.info("查询设备监控数据列表: page={}, size={}, keyword={}, status={}", 
                   page, size, keyword, status);
        
        try {
            // 1. 构建查询参数
            Map<String, Object> params = buildQueryParams(page, size, keyword, status, model, customerName);
            
            // 2. 查询数据列表
            List<DeviceMonitoringData> dataList = deviceMonitoringDataMapper.selectWithPagination(params);
            
            // 3. 查询总数
            int total = deviceMonitoringDataMapper.countWithConditions(params);
            
            // 4. 转换为DTO并填充关联数据
            List<DeviceMonitoringDataDTO> dtoList = dataList.stream()
                    .map(this::convertToDTO)
                    .peek(this::fillAssociatedData)
                    .collect(Collectors.toList());
            
            // 5. 构建分页结果
            PageResult<DeviceMonitoringDataDTO> result = new PageResult<>();
            result.setData(dtoList);
            result.setTotal(total);
            result.setPage(page);
            result.setSize(size);
            result.setTotalPages((int) Math.ceil((double) total / size));
            
            logger.info("成功查询到{}条设备监控数据", dtoList.size());
            return result;
            
        } catch (Exception e) {
            logger.error("查询设备监控数据列表失败", e);
            return new PageResult<>();
        }
    }
    
    /**
     * 根据设备ID获取完整的设备监控信息
     * 
     * @param deviceId 设备ID
     * @return 设备监控数据DTO
     */
    public DeviceMonitoringDataDTO getDeviceMonitoringById(Long deviceId) {
        logger.info("查询设备监控详情: deviceId={}", deviceId);
        
        try {
            // 1. 查询基本监控数据
            DeviceMonitoringData data = deviceMonitoringDataMapper.selectByDeviceId(deviceId);
            
            if (data == null) {
                logger.warn("未找到设备监控数据: deviceId={}", deviceId);
                return null;
            }
            
            // 2. 转换为DTO
            DeviceMonitoringDataDTO dto = convertToDTO(data);
            
            // 3. 填充关联数据
            fillAssociatedData(dto);
            
            logger.info("成功获取设备监控详情: deviceId={}", deviceId);
            return dto;
            
        } catch (Exception e) {
            logger.error("查询设备监控详情失败: deviceId={}", deviceId, e);
            return null;
        }
    }
    
    /**
     * 搜索设备
     * 支持序列号、客户名称、型号的模糊搜索
     * 
     * @param keyword 搜索关键词
     * @param limit 限制数量
     * @return 设备列表
     */
    public List<DeviceMonitoringDataDTO> searchDevices(String keyword, Integer limit) {
        logger.info("搜索设备: keyword={}, limit={}", keyword, limit);
        
        try {
            List<DeviceMonitoringData> dataList = deviceMonitoringDataMapper.searchDevices(keyword, limit);
            
            return dataList.stream()
                    .map(this::convertToDTO)
                    .peek(this::fillAssociatedData)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            logger.error("搜索设备失败: keyword={}", keyword, e);
            return List.of();
        }
    }
    
    /**
     * 根据状态查询设备列表
     * 
     * @param status 设备状态
     * @param limit 限制数量
     * @return 设备列表
     */
    public List<DeviceMonitoringDataDTO> getDevicesByStatus(DeviceStatus status, Integer limit) {
        logger.info("根据状态查询设备: status={}, limit={}", status, limit);
        
        try {
            List<DeviceMonitoringData> dataList = deviceMonitoringDataMapper.selectByStatus(status);
            
            return dataList.stream()
                    .limit(limit != null ? limit : Long.MAX_VALUE)
                    .map(this::convertToDTO)
                    .peek(this::fillAssociatedData)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            logger.error("根据状态查询设备失败: status={}", status, e);
            return List.of();
        }
    }
    
    /**
     * 获取设备分布地图数据
     * 
     * @return 地图数据列表
     */
    public List<DeviceMapDataDTO> getDeviceMapData() {
        logger.info("获取设备分布地图数据");
        
        try {
            List<DeviceMonitoringData> dataList = deviceMonitoringDataMapper.selectDevicesWithLocation();
            
            return dataList.stream()
                    .map(this::convertToMapDTO)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            logger.error("获取设备分布地图数据失败", e);
            return List.of();
        }
    }
    
    /**
     * 更新设备状态
     * 
     * @param deviceId 设备ID
     * @param status 新状态
     * @param lastOnlineAt 最后在线时间
     * @return 是否成功
     */
    @Transactional
    public boolean updateDeviceStatus(Long deviceId, DeviceStatus status, LocalDateTime lastOnlineAt) {
        logger.info("更新设备状态: deviceId={}, status={}", deviceId, status);
        
        try {
            int result = deviceMonitoringDataMapper.updateDeviceStatus(deviceId, status, lastOnlineAt);
            
            if (result > 0) {
                logger.info("设备状态更新成功: deviceId={}, status={}", deviceId, status);
                return true;
            } else {
                logger.warn("设备状态更新失败，未找到设备: deviceId={}", deviceId);
                return false;
            }
            
        } catch (Exception e) {
            logger.error("更新设备状态失败: deviceId={}, status={}", deviceId, status, e);
            return false;
        }
    }
    
    /**
     * 批量更新设备状态
     * 
     * @param deviceIds 设备ID列表
     * @param status 新状态
     * @return 更新的设备数量
     */
    @Transactional
    public int batchUpdateDeviceStatus(List<Long> deviceIds, DeviceStatus status) {
        logger.info("批量更新设备状态: deviceIds={}, status={}", deviceIds.size(), status);
        
        try {
            int result = deviceMonitoringDataMapper.batchUpdateStatus(deviceIds, status);
            logger.info("批量更新设备状态成功: 更新了{}台设备", result);
            return result;
            
        } catch (Exception e) {
            logger.error("批量更新设备状态失败: status={}", status, e);
            return 0;
        }
    }
    
    /**
     * 同步设备基本信息
     * 从managed_devices表同步数据到device_monitoring_data表
     * 
     * @param deviceId 设备ID，为null时同步所有设备
     * @return 同步的设备数量
     */
    @Transactional
    public int syncDeviceInfo(Long deviceId) {
        logger.info("同步设备基本信息: deviceId={}", deviceId);
        
        try {
            int result;
            if (deviceId != null) {
                result = deviceMonitoringDataMapper.syncDeviceInfo(deviceId);
            } else {
                result = deviceMonitoringDataMapper.batchSyncAllDeviceInfo();
            }
            
            logger.info("设备信息同步成功: 同步了{}台设备", result);
            return result;
            
        } catch (Exception e) {
            logger.error("同步设备基本信息失败: deviceId={}", deviceId, e);
            return 0;
        }
    }
    
    /**
     * 获取设备状态统计
     * 
     * @return 状态统计Map
     */
    public Map<String, Integer> getDeviceStatusStats() {
        logger.info("获取设备状态统计");
        
        try {
            return deviceMonitoringDataMapper.countDevicesByStatus();
        } catch (Exception e) {
            logger.error("获取设备状态统计失败", e);
            return new HashMap<>();
        }
    }
    
    /**
     * 构建查询参数
     */
    private Map<String, Object> buildQueryParams(Integer page, Integer size, String keyword, 
                                                String status, String model, String customerName) {
        Map<String, Object> params = new HashMap<>();
        
        // 分页参数
        if (page != null && size != null) {
            params.put("offset", (page - 1) * size);
            params.put("limit", size);
        }
        
        // 搜索条件
        if (keyword != null && !keyword.trim().isEmpty()) {
            params.put("keyword", keyword.trim());
        }
        
        if (status != null && !status.trim().isEmpty()) {
            params.put("status", status.trim());
        }
        
        if (model != null && !model.trim().isEmpty()) {
            params.put("model", model.trim());
        }
        
        if (customerName != null && !customerName.trim().isEmpty()) {
            params.put("customerName", customerName.trim());
        }
        
        // 默认排序
        params.put("orderBy", "updated_at DESC");
        
        return params;
    }
    
    /**
     * 转换实体对象为DTO对象
     */
    private DeviceMonitoringDataDTO convertToDTO(DeviceMonitoringData data) {
        if (data == null) {
            return null;
        }
        
        DeviceMonitoringDataDTO dto = new DeviceMonitoringDataDTO();
        
        // 基本信息
        dto.setId(data.getId() != null ? data.getId().toString() : null);
        dto.setSerialNumber(data.getSerialNumber());
        dto.setCustomerName(data.getCustomerName());
        dto.setStatus(data.getStatus() != null ? data.getStatus().getCode() : null);
        dto.setModel(data.getModel());
        dto.setFirmwareVersion(data.getFirmwareVersion());
        dto.setLastOnlineAt(data.getLastOnlineAt());
        dto.setCreatedAt(data.getCreatedAt());
        dto.setUpdatedAt(data.getUpdatedAt());
        
        // 位置信息
        if (data.getLocationLatitude() != null && data.getLocationLongitude() != null) {
            DeviceMonitoringDataDTO.LocationDTO location = new DeviceMonitoringDataDTO.LocationDTO();
            location.setLatitude(data.getLocationLatitude());
            location.setLongitude(data.getLocationLongitude());
            location.setAddress(data.getLocationAddress());
            dto.setLocation(location);
        }
        
        // 设置格式化时间
        setFormattedTimes(dto);
        
        // 设置状态描述
        if (data.getStatus() != null) {
            dto.setStatusDescription(DeviceMonitoringStatsUtil.getStatusText(data.getStatus()));
        }
        
        return dto;
    }
    
    /**
     * 转换为地图数据DTO
     */
    private DeviceMapDataDTO convertToMapDTO(DeviceMonitoringData data) {
        if (data == null) {
            return null;
        }
        
        DeviceMapDataDTO dto = new DeviceMapDataDTO();
        
        // 基本信息
        dto.setId(data.getId() != null ? data.getId().toString() : null);
        dto.setSerialNumber(data.getSerialNumber());
        dto.setCustomerName(data.getCustomerName());
        dto.setStatus(data.getStatus() != null ? data.getStatus().getCode() : null);
        dto.setModel(data.getModel());
        dto.setFirmwareVersion(data.getFirmwareVersion());
        
        // 位置信息
        dto.setLatitude(data.getLocationLatitude());
        dto.setLongitude(data.getLocationLongitude());
        dto.setAddress(data.getLocationAddress());
        
        // 地图显示相关
        if (data.getStatus() != null) {
            dto.setMarkerColor(DeviceMonitoringStatsUtil.getStatusColor(data.getStatus()));
            dto.setStatusDescription(DeviceMonitoringStatsUtil.getStatusText(data.getStatus()));
        }
        
        // 格式化最后在线时间
        if (data.getLastOnlineAt() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            dto.setLastOnlineAtFormatted(data.getLastOnlineAt().format(formatter));
        }
        
        return dto;
    }
    
    /**
     * 填充关联数据（性能指标、网络状态）
     */
    private void fillAssociatedData(DeviceMonitoringDataDTO dto) {
        if (dto == null || dto.getId() == null) {
            return;
        }
        
        try {
            Long deviceId = Long.parseLong(dto.getId());
            
            // 填充性能数据
            fillPerformanceData(dto, deviceId);
            
            // 填充网络状态数据
            fillNetworkData(dto, deviceId);
            
        } catch (Exception e) {
            logger.warn("填充关联数据失败: deviceId={}", dto.getId(), e);
        }
    }
    
    /**
     * 填充性能数据
     */
    private void fillPerformanceData(DeviceMonitoringDataDTO dto, Long deviceId) {
        try {
            DevicePerformanceMetrics metrics = devicePerformanceMetricsMapper.selectLatestByDeviceId(deviceId);
            
            if (metrics != null) {
                DeviceMonitoringDataDTO.PerformanceDTO performance = new DeviceMonitoringDataDTO.PerformanceDTO();
                performance.setCpu(metrics.getCpuUsage());
                performance.setMemory(metrics.getMemoryUsage());
                performance.setDisk(metrics.getDiskUsage());
                performance.setTemperature(metrics.getTemperature());
                performance.setBatteryLevel(metrics.getBatteryLevel());
                
                dto.setPerformance(performance);
            }
        } catch (Exception e) {
            logger.debug("填充性能数据失败: deviceId={}", deviceId, e);
        }
    }
    
    /**
     * 填充网络状态数据
     */
    private void fillNetworkData(DeviceMonitoringDataDTO dto, Long deviceId) {
        try {
            DeviceNetworkStatus networkStatus = deviceNetworkStatusMapper.selectLatestByDeviceId(deviceId);
            
            if (networkStatus != null) {
                DeviceMonitoringDataDTO.NetworkStatusDTO network = new DeviceMonitoringDataDTO.NetworkStatusDTO();
                network.setType(networkStatus.getNetworkType() != null ? networkStatus.getNetworkType().getCode() : null);
                network.setSignalStrength(networkStatus.getSignalStrength());
                network.setConnectionStatus(networkStatus.getConnectionStatus() != null ? 
                                          networkStatus.getConnectionStatus().getCode() : null);
                network.setIpAddress(networkStatus.getIpAddress());
                network.setDownloadSpeed(networkStatus.getDownloadSpeed());
                network.setUploadSpeed(networkStatus.getUploadSpeed());
                
                dto.setNetwork(network);
            }
        } catch (Exception e) {
            logger.debug("填充网络状态数据失败: deviceId={}", deviceId, e);
        }
    }
    
    /**
     * 设置格式化时间字符串
     */
    private void setFormattedTimes(DeviceMonitoringDataDTO dto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        if (dto.getLastOnlineAt() != null) {
            dto.setLastOnlineAtFormatted(dto.getLastOnlineAt().format(formatter));
        }
        
        if (dto.getCreatedAt() != null) {
            dto.setCreatedAtFormatted(dto.getCreatedAt().format(formatter));
        }
        
        if (dto.getUpdatedAt() != null) {
            dto.setUpdatedAtFormatted(dto.getUpdatedAt().format(formatter));
        }
    }
    
    /**
     * 分页结果类
     */
    public static class PageResult<T> {
        private List<T> data;
        private int total;
        private int page;
        private int size;
        private int totalPages;
        
        public PageResult() {
            this.data = List.of();
            this.total = 0;
            this.page = 1;
            this.size = 10;
            this.totalPages = 0;
        }
        
        // Getter和Setter方法
        public List<T> getData() {
            return data;
        }
        
        public void setData(List<T> data) {
            this.data = data;
        }
        
        public int getTotal() {
            return total;
        }
        
        public void setTotal(int total) {
            this.total = total;
        }
        
        public int getPage() {
            return page;
        }
        
        public void setPage(int page) {
            this.page = page;
        }
        
        public int getSize() {
            return size;
        }
        
        public void setSize(int size) {
            this.size = size;
        }
        
        public int getTotalPages() {
            return totalPages;
        }
        
        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }
    }
}