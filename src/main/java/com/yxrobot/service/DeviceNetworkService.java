package com.yxrobot.service;

import com.yxrobot.dto.DeviceNetworkStatusDTO;
import com.yxrobot.dto.DeviceChartDataDTO;
import com.yxrobot.entity.DeviceNetworkStatus;
import com.yxrobot.entity.NetworkType;
import com.yxrobot.entity.ConnectionStatus;
import com.yxrobot.mapper.DeviceNetworkStatusMapper;
import com.yxrobot.service.DeviceMonitoringService.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 设备网络服务类
 * 处理设备网络状态监控业务逻辑，支持网络状态查询和图表生成
 * 
 * 主要功能：
 * - 网络状态查询和分析
 * - 网络图表数据生成
 * - 网络质量评估
 * - 异常网络检测
 * - 网络统计和对比
 * 
 * @author YXRobot Development Team
 * @since 2024
 */
@Service
@Transactional(readOnly = true)
public class DeviceNetworkService {
    
    private static final Logger logger = LoggerFactory.getLogger(DeviceNetworkService.class);
    
    @Autowired
    private DeviceNetworkStatusMapper deviceNetworkStatusMapper; 
   
    /**
     * 根据设备ID获取最新网络状态
     * 
     * @param deviceId 设备ID
     * @return 网络状态DTO
     */
    public DeviceNetworkStatusDTO getLatestNetworkStatus(Long deviceId) {
        logger.info("获取设备最新网络状态: deviceId={}", deviceId);
        
        try {
            DeviceNetworkStatus networkStatus = deviceNetworkStatusMapper.selectLatestByDeviceId(deviceId);
            
            if (networkStatus == null) {
                logger.warn("未找到设备网络状态: deviceId={}", deviceId);
                return null;
            }
            
            return convertToDTO(networkStatus);
            
        } catch (Exception e) {
            logger.error("获取设备最新网络状态失败: deviceId={}", deviceId, e);
            return null;
        }
    }
    
    /**
     * 获取设备网络状态历史记录
     * 
     * @param deviceId 设备ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制数量
     * @return 网络状态列表
     */
    public List<DeviceNetworkStatusDTO> getNetworkStatusHistory(
            Long deviceId, LocalDateTime startTime, LocalDateTime endTime, Integer limit) {
        
        logger.info("获取设备网络状态历史记录: deviceId={}, startTime={}, endTime={}", 
                   deviceId, startTime, endTime);
        
        try {
            List<DeviceNetworkStatus> statusList = deviceNetworkStatusMapper
                    .selectHistoryByDeviceId(deviceId, startTime, endTime, limit);
            
            return statusList.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            logger.error("获取设备网络状态历史记录失败: deviceId={}", deviceId, e);
            return List.of();
        }
    }    

    /**
     * 分页查询网络状态数据
     * 
     * @param page 页码
     * @param size 每页大小
     * @param deviceId 设备ID筛选
     * @param networkType 网络类型筛选
     * @param connectionStatus 连接状态筛选
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    public PageResult<DeviceNetworkStatusDTO> getNetworkStatus(
            Integer page, Integer size, Long deviceId, String networkType, 
            String connectionStatus, LocalDateTime startTime, LocalDateTime endTime) {
        
        logger.info("分页查询网络状态: page={}, size={}, deviceId={}", page, size, deviceId);
        
        try {
            // 构建查询参数
            Map<String, Object> params = buildNetworkQueryParams(
                page, size, deviceId, networkType, connectionStatus, startTime, endTime);
            
            // 查询数据列表
            List<DeviceNetworkStatus> statusList = 
                deviceNetworkStatusMapper.selectWithPagination(params);
            
            // 查询总数
            int total = deviceNetworkStatusMapper.countWithConditions(params);
            
            // 转换为DTO
            List<DeviceNetworkStatusDTO> dtoList = statusList.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            // 构建分页结果
            PageResult<DeviceNetworkStatusDTO> result = new PageResult<>();
            result.setData(dtoList);
            result.setTotal(total);
            result.setPage(page);
            result.setSize(size);
            result.setTotalPages((int) Math.ceil((double) total / size));
            
            return result;
            
        } catch (Exception e) {
            logger.error("分页查询网络状态失败", e);
            return new PageResult<>();
        }
    } 
   
    /**
     * 生成网络图表数据
     * 支持前端网络状态图表显示
     * 
     * @param deviceId 设备ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param intervalMinutes 时间间隔（分钟）
     * @return 图表数据DTO
     */
    public DeviceChartDataDTO getNetworkChartData(
            Long deviceId, LocalDateTime startTime, LocalDateTime endTime, Integer intervalMinutes) {
        
        logger.info("生成网络图表数据: deviceId={}, interval={}分钟", deviceId, intervalMinutes);
        
        try {
            // 查询图表数据
            List<Map<String, Object>> chartData = deviceNetworkStatusMapper
                    .selectNetworkChartData(deviceId, startTime, endTime, intervalMinutes);
            
            if (chartData.isEmpty()) {
                logger.warn("未找到网络图表数据: deviceId={}", deviceId);
                return createEmptyNetworkChartData();
            }
            
            // 构建图表DTO
            DeviceChartDataDTO chartDTO = new DeviceChartDataDTO();
            chartDTO.setChartType("network");
            chartDTO.setTitle("设备网络监控");
            chartDTO.setTimeRange(formatTimeRange(startTime, endTime));
            
            // 提取时间标签
            List<String> labels = chartData.stream()
                    .map(data -> (String) data.get("timePoint"))
                    .collect(Collectors.toList());
            chartDTO.setLabels(labels);
            
            // 构建数据系列
            List<DeviceChartDataDTO.ChartSeriesDTO> seriesList = new ArrayList<>();
            
            // 信号强度系列
            seriesList.add(createNetworkChartSeries("信号强度", "line", "#52c41a", 
                chartData, "avgSignalStrength", "%"));
            
            // 下载速度系列
            seriesList.add(createNetworkChartSeries("下载速度", "line", "#1890ff", 
                chartData, "avgDownloadSpeed", "Mbps"));
            
            // 上传速度系列
            seriesList.add(createNetworkChartSeries("上传速度", "line", "#722ed1", 
                chartData, "avgUploadSpeed", "Mbps"));
            
            // 网络延迟系列
            seriesList.add(createNetworkChartSeries("网络延迟", "line", "#fa541c", 
                chartData, "avgPingLatency", "ms"));
            
            chartDTO.setSeries(seriesList);
            
            // 设置图表配置
            chartDTO.setConfig(createNetworkChartConfig());
            
            // 计算统计信息
            chartDTO.setStatistics(calculateNetworkChartStatistics(chartData));
            
            return chartDTO;
            
        } catch (Exception e) {
            logger.error("生成网络图表数据失败: deviceId={}", deviceId, e);
            return createEmptyNetworkChartData();
        }
    } 
   
    /**
     * 根据连接状态查询设备列表
     * 
     * @param connectionStatus 连接状态
     * @param limit 限制数量
     * @return 设备网络状态列表
     */
    public List<DeviceNetworkStatusDTO> getDevicesByConnectionStatus(
            ConnectionStatus connectionStatus, Integer limit) {
        
        logger.info("根据连接状态查询设备: status={}, limit={}", connectionStatus, limit);
        
        try {
            List<DeviceNetworkStatus> statusList = deviceNetworkStatusMapper
                    .selectByConnectionStatus(connectionStatus, limit);
            
            return statusList.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            logger.error("根据连接状态查询设备失败: status={}", connectionStatus, e);
            return List.of();
        }
    }
    
    /**
     * 根据网络类型查询设备列表
     * 
     * @param networkType 网络类型
     * @param limit 限制数量
     * @return 设备网络状态列表
     */
    public List<DeviceNetworkStatusDTO> getDevicesByNetworkType(
            NetworkType networkType, Integer limit) {
        
        logger.info("根据网络类型查询设备: type={}, limit={}", networkType, limit);
        
        try {
            List<DeviceNetworkStatus> statusList = deviceNetworkStatusMapper
                    .selectByNetworkType(networkType, limit);
            
            return statusList.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            logger.error("根据网络类型查询设备失败: type={}", networkType, e);
            return List.of();
        }
    }    

    /**
     * 查询信号强度低的设备
     * 
     * @param signalThreshold 信号强度阈值
     * @param limit 限制数量
     * @return 低信号设备列表
     */
    public List<DeviceNetworkStatusDTO> getLowSignalDevices(Integer signalThreshold, Integer limit) {
        logger.info("查询低信号设备: threshold={}, limit={}", signalThreshold, limit);
        
        try {
            List<DeviceNetworkStatus> statusList = deviceNetworkStatusMapper
                    .selectLowSignalDevices(signalThreshold, limit);
            
            return statusList.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            logger.error("查询低信号设备失败: threshold={}", signalThreshold, e);
            return List.of();
        }
    }
    
    /**
     * 查询网络延迟高的设备
     * 
     * @param latencyThreshold 延迟阈值（毫秒）
     * @param limit 限制数量
     * @return 高延迟设备列表
     */
    public List<DeviceNetworkStatusDTO> getHighLatencyDevices(Integer latencyThreshold, Integer limit) {
        logger.info("查询高延迟设备: threshold={}ms, limit={}", latencyThreshold, limit);
        
        try {
            List<DeviceNetworkStatus> statusList = deviceNetworkStatusMapper
                    .selectHighLatencyDevices(latencyThreshold, limit);
            
            return statusList.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            logger.error("查询高延迟设备失败: threshold={}", latencyThreshold, e);
            return List.of();
        }
    }
    
    /**
     * 查询网速慢的设备
     * 
     * @param speedThreshold 网速阈值（Mbps）
     * @param limit 限制数量
     * @return 慢速设备列表
     */
    public List<DeviceNetworkStatusDTO> getSlowSpeedDevices(BigDecimal speedThreshold, Integer limit) {
        logger.info("查询慢速设备: threshold={}Mbps, limit={}", speedThreshold, limit);
        
        try {
            List<DeviceNetworkStatus> statusList = deviceNetworkStatusMapper
                    .selectSlowSpeedDevices(speedThreshold, limit);
            
            return statusList.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            logger.error("查询慢速设备失败: threshold={}", speedThreshold, e);
            return List.of();
        }
    }    
  
  /**
     * 更新设备连接状态
     * 
     * @param deviceId 设备ID
     * @param connectionStatus 连接状态
     * @param lastConnectedAt 最后连接时间
     * @return 是否成功
     */
    @Transactional
    public boolean updateConnectionStatus(Long deviceId, ConnectionStatus connectionStatus, 
                                        LocalDateTime lastConnectedAt) {
        logger.info("更新设备连接状态: deviceId={}, status={}", deviceId, connectionStatus);
        
        try {
            int result = deviceNetworkStatusMapper.updateConnectionStatus(
                deviceId, connectionStatus, lastConnectedAt);
            
            if (result > 0) {
                logger.info("设备连接状态更新成功: deviceId={}", deviceId);
                return true;
            } else {
                logger.warn("设备连接状态更新失败，未找到设备: deviceId={}", deviceId);
                return false;
            }
            
        } catch (Exception e) {
            logger.error("更新设备连接状态失败: deviceId={}", deviceId, e);
            return false;
        }
    }
    
    /**
     * 批量更新设备连接状态
     * 
     * @param deviceIds 设备ID列表
     * @param connectionStatus 连接状态
     * @return 更新的设备数量
     */
    @Transactional
    public int batchUpdateConnectionStatus(List<Long> deviceIds, ConnectionStatus connectionStatus) {
        logger.info("批量更新设备连接状态: deviceCount={}, status={}", deviceIds.size(), connectionStatus);
        
        try {
            int result = deviceNetworkStatusMapper.batchUpdateConnectionStatus(deviceIds, connectionStatus);
            logger.info("批量更新设备连接状态成功: 更新了{}台设备", result);
            return result;
            
        } catch (Exception e) {
            logger.error("批量更新设备连接状态失败: status={}", connectionStatus, e);
            return 0;
        }
    }    
    
/**
     * 获取网络统计信息
     * 
     * @return 网络统计Map
     */
    public Map<String, Object> getNetworkStatistics() {
        logger.info("获取网络统计信息");
        
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 连接状态统计
            Map<String, Integer> connectionStats = deviceNetworkStatusMapper.countDevicesByConnectionStatus();
            statistics.put("connectionStatus", connectionStats);
            
            // 网络类型统计
            Map<String, Integer> networkTypeStats = deviceNetworkStatusMapper.countDevicesByNetworkType();
            statistics.put("networkType", networkTypeStats);
            
            // 网络质量分布
            Map<String, Integer> qualityDistribution = deviceNetworkStatusMapper.selectNetworkQualityDistribution();
            statistics.put("qualityDistribution", qualityDistribution);
            
            return statistics;
            
        } catch (Exception e) {
            logger.error("获取网络统计信息失败", e);
            return new HashMap<>();
        }
    }
    
    /**
     * 计算平均网络性能指标
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 平均网络性能Map
     */
    public Map<String, BigDecimal> calculateAverageNetworkMetrics(
            LocalDateTime startTime, LocalDateTime endTime) {
        
        logger.info("计算平均网络性能指标");
        
        try {
            return deviceNetworkStatusMapper.calculateAverageNetworkMetrics(startTime, endTime);
        } catch (Exception e) {
            logger.error("计算平均网络性能指标失败", e);
            return new HashMap<>();
        }
    }
    
    /**
     * 查询网络异常的设备
     * 
     * @param signalThreshold 信号强度阈值
     * @param latencyThreshold 延迟阈值
     * @param speedThreshold 速度阈值
     * @param recentMinutes 最近几分钟
     * @return 异常设备列表
     */
    public List<DeviceNetworkStatusDTO> getAbnormalNetworkDevices(
            Integer signalThreshold, Integer latencyThreshold, 
            BigDecimal speedThreshold, Integer recentMinutes) {
        
        logger.info("查询网络异常设备: signal={}, latency={}, speed={}", 
                   signalThreshold, latencyThreshold, speedThreshold);
        
        try {
            List<DeviceNetworkStatus> abnormalDevices = deviceNetworkStatusMapper
                    .selectAbnormalNetworkDevices(signalThreshold, latencyThreshold, 
                                                speedThreshold, recentMinutes);
            
            return abnormalDevices.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            logger.error("查询网络异常设备失败", e);
            return List.of();
        }
    }
    
    /**
     * 批量插入网络状态数据
     * 
     * @param statusList 网络状态列表
     * @return 插入的记录数
     */
    @Transactional
    public int batchInsertNetworkStatus(List<DeviceNetworkStatus> statusList) {
        logger.info("批量插入网络状态数据: count={}", statusList.size());
        
        try {
            int result = deviceNetworkStatusMapper.batchInsert(statusList);
            logger.info("批量插入网络状态数据成功: 插入了{}条记录", result);
            return result;
        } catch (Exception e) {
            logger.error("批量插入网络状态数据失败", e);
            return 0;
        }
    }
    
    /**
     * 清理过期的网络状态数据
     * 
     * @param retentionDays 保留天数
     * @return 清理的记录数
     */
    @Transactional
    public int cleanupExpiredNetworkStatus(int retentionDays) {
        logger.info("清理过期网络状态数据: retentionDays={}", retentionDays);
        
        try {
            LocalDateTime beforeTime = LocalDateTime.now().minusDays(retentionDays);
            int result = deviceNetworkStatusMapper.deleteExpiredNetworkStatus(beforeTime);
            
            logger.info("清理过期网络状态数据成功: 清理了{}条记录", result);
            return result;
        } catch (Exception e) {
            logger.error("清理过期网络状态数据失败: retentionDays={}", retentionDays, e);
            return 0;
        }
    }
    
    /**
     * 根据IP地址查询设备网络状态
     * 
     * @param ipAddress IP地址
     * @return 网络状态DTO
     */
    public DeviceNetworkStatusDTO getNetworkStatusByIpAddress(String ipAddress) {
        logger.info("根据IP地址查询网络状态: ip={}", ipAddress);
        
        try {
            DeviceNetworkStatus networkStatus = deviceNetworkStatusMapper.selectByIpAddress(ipAddress);
            return networkStatus != null ? convertToDTO(networkStatus) : null;
        } catch (Exception e) {
            logger.error("根据IP地址查询网络状态失败: ip={}", ipAddress, e);
            return null;
        }
    }
    
    /**
     * 根据MAC地址查询设备网络状态
     * 
     * @param macAddress MAC地址
     * @return 网络状态DTO
     */
    public DeviceNetworkStatusDTO getNetworkStatusByMacAddress(String macAddress) {
        logger.info("根据MAC地址查询网络状态: mac={}", macAddress);
        
        try {
            DeviceNetworkStatus networkStatus = deviceNetworkStatusMapper.selectByMacAddress(macAddress);
            return networkStatus != null ? convertToDTO(networkStatus) : null;
        } catch (Exception e) {
            logger.error("根据MAC地址查询网络状态失败: mac={}", macAddress, e);
            return null;
        }
    }
    
    /**
     * 构建网络查询参数
     */
    private Map<String, Object> buildNetworkQueryParams(
            Integer page, Integer size, Long deviceId, String networkType, 
            String connectionStatus, LocalDateTime startTime, LocalDateTime endTime) {
        
        Map<String, Object> params = new HashMap<>();
        
        // 分页参数
        if (page != null && size != null) {
            params.put("offset", (page - 1) * size);
            params.put("limit", size);
        }
        
        // 筛选条件
        if (deviceId != null) {
            params.put("deviceId", deviceId);
        }
        
        if (networkType != null && !networkType.trim().isEmpty()) {
            params.put("networkType", networkType.trim().toUpperCase());
        }
        
        if (connectionStatus != null && !connectionStatus.trim().isEmpty()) {
            params.put("connectionStatus", connectionStatus.trim().toUpperCase());
        }
        
        if (startTime != null) {
            params.put("startTime", startTime);
        }
        
        if (endTime != null) {
            params.put("endTime", endTime);
        }
        
        // 默认排序
        params.put("orderBy", "created_at DESC");
        
        return params;
    }
    
    /**
     * 转换实体对象为DTO对象
     */
    private DeviceNetworkStatusDTO convertToDTO(DeviceNetworkStatus networkStatus) {
        if (networkStatus == null) {
            return null;
        }
        
        DeviceNetworkStatusDTO dto = new DeviceNetworkStatusDTO();
        
        // 基本信息
        dto.setId(networkStatus.getId() != null ? networkStatus.getId().toString() : null);
        dto.setDeviceId(networkStatus.getDeviceId() != null ? networkStatus.getDeviceId().toString() : null);
        
        // 网络信息
        dto.setNetworkType(networkStatus.getNetworkType() != null ? 
                          networkStatus.getNetworkType().getCode() : null);
        dto.setSignalStrength(networkStatus.getSignalStrength());
        dto.setConnectionStatus(networkStatus.getConnectionStatus() != null ? 
                               networkStatus.getConnectionStatus().getCode() : null);
        dto.setIpAddress(networkStatus.getIpAddress());
        dto.setMacAddress(networkStatus.getMacAddress());
        dto.setDownloadSpeed(networkStatus.getDownloadSpeed());
        dto.setUploadSpeed(networkStatus.getUploadSpeed());
        dto.setPingLatency(networkStatus.getPingLatency());
        
        // 时间信息
        dto.setLastConnectedAt(networkStatus.getLastConnectedAt());
        dto.setCreatedAt(networkStatus.getCreatedAt());
        dto.setUpdatedAt(networkStatus.getUpdatedAt());
        
        // 设置格式化时间
        setFormattedTimes(dto);
        
        // 设置状态描述
        setStatusDescriptions(dto, networkStatus);
        
        // 计算网络质量
        dto.setNetworkQuality(calculateNetworkQuality(networkStatus));
        dto.setQualityColor(getQualityColor(dto.getNetworkQuality()));
        
        return dto;
    }    

    /**
     * 设置格式化时间字符串
     */
    private void setFormattedTimes(DeviceNetworkStatusDTO dto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        if (dto.getLastConnectedAt() != null) {
            dto.setLastConnectedAtFormatted(dto.getLastConnectedAt().format(formatter));
        }
        
        if (dto.getCreatedAt() != null) {
            dto.setCreatedAtFormatted(dto.getCreatedAt().format(formatter));
        }
        
        if (dto.getUpdatedAt() != null) {
            dto.setUpdatedAtFormatted(dto.getUpdatedAt().format(formatter));
        }
    }
    
    /**
     * 设置状态描述
     */
    private void setStatusDescriptions(DeviceNetworkStatusDTO dto, DeviceNetworkStatus networkStatus) {
        // 网络类型描述
        if (networkStatus.getNetworkType() != null) {
            dto.setNetworkTypeDescription(networkStatus.getNetworkType().getDescription());
        }
        
        // 连接状态描述
        if (networkStatus.getConnectionStatus() != null) {
            dto.setConnectionStatusDescription(networkStatus.getConnectionStatus().getDescription());
        }
        
        // 信号强度描述
        if (dto.getSignalStrength() != null) {
            dto.setSignalStrengthDescription(getSignalStrengthDescription(dto.getSignalStrength()));
        }
        
        // 网速描述
        if (dto.getDownloadSpeed() != null) {
            dto.setSpeedDescription(getSpeedDescription(dto.getDownloadSpeed()));
        }
        
        // 延迟描述
        if (dto.getPingLatency() != null) {
            dto.setLatencyDescription(getLatencyDescription(dto.getPingLatency()));
        }
    }
    
    /**
     * 获取信号强度描述
     */
    private String getSignalStrengthDescription(Integer signalStrength) {
        if (signalStrength >= 80) {
            return "信号强度优秀";
        } else if (signalStrength >= 60) {
            return "信号强度良好";
        } else if (signalStrength >= 40) {
            return "信号强度一般";
        } else if (signalStrength >= 20) {
            return "信号强度较弱";
        } else {
            return "信号强度很弱";
        }
    }
    
    /**
     * 获取网速描述
     */
    private String getSpeedDescription(BigDecimal speed) {
        if (speed.compareTo(BigDecimal.valueOf(100)) >= 0) {
            return "网速极快";
        } else if (speed.compareTo(BigDecimal.valueOf(50)) >= 0) {
            return "网速很快";
        } else if (speed.compareTo(BigDecimal.valueOf(20)) >= 0) {
            return "网速较快";
        } else if (speed.compareTo(BigDecimal.valueOf(5)) >= 0) {
            return "网速一般";
        } else {
            return "网速较慢";
        }
    }
    
    /**
     * 获取延迟描述
     */
    private String getLatencyDescription(Integer latency) {
        if (latency <= 20) {
            return "延迟极低";
        } else if (latency <= 50) {
            return "延迟较低";
        } else if (latency <= 100) {
            return "延迟正常";
        } else if (latency <= 200) {
            return "延迟较高";
        } else {
            return "延迟很高";
        }
    }    
  
  /**
     * 计算网络质量
     */
    private String calculateNetworkQuality(DeviceNetworkStatus networkStatus) {
        int score = 0;
        int factors = 0;
        
        // 信号强度评分
        if (networkStatus.getSignalStrength() != null) {
            if (networkStatus.getSignalStrength() >= 80) score += 4;
            else if (networkStatus.getSignalStrength() >= 60) score += 3;
            else if (networkStatus.getSignalStrength() >= 40) score += 2;
            else if (networkStatus.getSignalStrength() >= 20) score += 1;
            factors++;
        }
        
        // 延迟评分
        if (networkStatus.getPingLatency() != null) {
            if (networkStatus.getPingLatency() <= 20) score += 4;
            else if (networkStatus.getPingLatency() <= 50) score += 3;
            else if (networkStatus.getPingLatency() <= 100) score += 2;
            else if (networkStatus.getPingLatency() <= 200) score += 1;
            factors++;
        }
        
        // 下载速度评分
        if (networkStatus.getDownloadSpeed() != null) {
            if (networkStatus.getDownloadSpeed().compareTo(BigDecimal.valueOf(50)) >= 0) score += 4;
            else if (networkStatus.getDownloadSpeed().compareTo(BigDecimal.valueOf(20)) >= 0) score += 3;
            else if (networkStatus.getDownloadSpeed().compareTo(BigDecimal.valueOf(5)) >= 0) score += 2;
            else if (networkStatus.getDownloadSpeed().compareTo(BigDecimal.valueOf(1)) >= 0) score += 1;
            factors++;
        }
        
        if (factors == 0) return "未知";
        
        double avgScore = (double) score / factors;
        
        if (avgScore >= 3.5) return "优秀";
        else if (avgScore >= 2.5) return "良好";
        else if (avgScore >= 1.5) return "一般";
        else return "较差";
    }
    
    /**
     * 获取质量颜色
     */
    private String getQualityColor(String quality) {
        switch (quality) {
            case "优秀": return "#52c41a";
            case "良好": return "#1890ff";
            case "一般": return "#faad14";
            case "较差": return "#f5222d";
            default: return "#d9d9d9";
        }
    }
    
    /**
     * 创建网络图表数据系列
     */
    private DeviceChartDataDTO.ChartSeriesDTO createNetworkChartSeries(
            String name, String type, String color, List<Map<String, Object>> chartData, 
            String dataKey, String unit) {
        
        List<BigDecimal> data = chartData.stream()
                .map(item -> {
                    Object value = item.get(dataKey);
                    return value != null ? (BigDecimal) value : BigDecimal.ZERO;
                })
                .collect(Collectors.toList());
        
        return new DeviceChartDataDTO.ChartSeriesDTO(name, type, color, data, unit);
    }
    
    /**
     * 创建网络图表配置
     */
    private DeviceChartDataDTO.ChartConfigDTO createNetworkChartConfig() {
        DeviceChartDataDTO.ChartConfigDTO config = new DeviceChartDataDTO.ChartConfigDTO();
        config.setxAxisTitle("时间");
        config.setyAxisTitle("网络指标");
        config.setShowLegend(true);
        config.setShowGrid(true);
        config.setEnableZoom(true);
        config.setHeight(400);
        config.setTheme("light");
        return config;
    }    
    
/**
     * 计算网络图表统计信息
     */
    private DeviceChartDataDTO.ChartStatisticsDTO calculateNetworkChartStatistics(
            List<Map<String, Object>> chartData) {
        
        DeviceChartDataDTO.ChartStatisticsDTO stats = new DeviceChartDataDTO.ChartStatisticsDTO();
        
        if (chartData.isEmpty()) {
            return stats;
        }
        
        // 计算信号强度统计
        List<BigDecimal> signalValues = chartData.stream()
                .map(data -> (BigDecimal) data.get("avgSignalStrength"))
                .filter(value -> value != null)
                .collect(Collectors.toList());
        
        if (!signalValues.isEmpty()) {
            stats.setMaxValue(signalValues.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO));
            stats.setMinValue(signalValues.stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO));
            
            BigDecimal sum = signalValues.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            stats.setAvgValue(sum.divide(BigDecimal.valueOf(signalValues.size()), 2, RoundingMode.HALF_UP));
            
            stats.setCurrentValue(signalValues.get(signalValues.size() - 1));
            
            // 计算趋势
            if (signalValues.size() > 1) {
                BigDecimal first = signalValues.get(0);
                BigDecimal last = signalValues.get(signalValues.size() - 1);
                
                if (last.compareTo(first) > 0) {
                    stats.setTrend("up");
                    stats.setTrendDescription("网络质量提升");
                } else if (last.compareTo(first) < 0) {
                    stats.setTrend("down");
                    stats.setTrendDescription("网络质量下降");
                } else {
                    stats.setTrend("stable");
                    stats.setTrendDescription("网络质量稳定");
                }
            }
        }
        
        return stats;
    }
    
    /**
     * 创建空的网络图表数据
     */
    private DeviceChartDataDTO createEmptyNetworkChartData() {
        DeviceChartDataDTO chartDTO = new DeviceChartDataDTO();
        chartDTO.setChartType("network");
        chartDTO.setTitle("设备网络监控");
        chartDTO.setLabels(List.of());
        chartDTO.setSeries(List.of());
        chartDTO.setConfig(createNetworkChartConfig());
        chartDTO.setStatistics(new DeviceChartDataDTO.ChartStatisticsDTO());
        return chartDTO;
    }
    
    /**
     * 格式化时间范围
     */
    private String formatTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm");
        return startTime.format(formatter) + " ~ " + endTime.format(formatter);
    }
}