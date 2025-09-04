package com.yxrobot.service;

import com.yxrobot.dto.DevicePerformanceMetricsDTO;
import com.yxrobot.dto.DeviceChartDataDTO;
import com.yxrobot.entity.DevicePerformanceMetrics;
import com.yxrobot.mapper.DevicePerformanceMetricsMapper;
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
 * 设备性能服务类
 * 处理设备性能监控业务逻辑，支持性能数据查询和图表生成
 * 
 * 主要功能：
 * - 性能指标查询和分析
 * - 性能图表数据生成
 * - 性能趋势分析
 * - 异常性能检测
 * - 性能统计和对比
 * 
 * @author YXRobot Development Team
 * @since 2024
 */
@Service
@Transactional(readOnly = true)
public class DevicePerformanceService {
    
    private static final Logger logger = LoggerFactory.getLogger(DevicePerformanceService.class);
    
    @Autowired
    private DevicePerformanceMetricsMapper devicePerformanceMetricsMapper;
    
    /**
     * 根据设备ID获取最新性能指标
     * 
     * @param deviceId 设备ID
     * @return 性能指标DTO
     */
    public DevicePerformanceMetricsDTO getLatestPerformanceMetrics(Long deviceId) {
        logger.info("获取设备最新性能指标: deviceId={}", deviceId);
        
        try {
            DevicePerformanceMetrics metrics = devicePerformanceMetricsMapper.selectLatestByDeviceId(deviceId);
            
            if (metrics == null) {
                logger.warn("未找到设备性能指标: deviceId={}", deviceId);
                return null;
            }
            
            return convertToDTO(metrics);
            
        } catch (Exception e) {
            logger.error("获取设备最新性能指标失败: deviceId={}", deviceId, e);
            return null;
        }
    }
    
    /**
     * 获取设备性能历史记录
     * 
     * @param deviceId 设备ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制数量
     * @return 性能指标列表
     */
    public List<DevicePerformanceMetricsDTO> getPerformanceHistory(
            Long deviceId, LocalDateTime startTime, LocalDateTime endTime, Integer limit) {
        
        logger.info("获取设备性能历史记录: deviceId={}, startTime={}, endTime={}", 
                   deviceId, startTime, endTime);
        
        try {
            List<DevicePerformanceMetrics> metricsList = devicePerformanceMetricsMapper
                    .selectHistoryByDeviceId(deviceId, startTime, endTime, limit);
            
            return metricsList.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            logger.error("获取设备性能历史记录失败: deviceId={}", deviceId, e);
            return List.of();
        }
    }
    
    /**
     * 分页查询性能指标数据
     * 
     * @param page 页码
     * @param size 每页大小
     * @param deviceId 设备ID筛选
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    public PageResult<DevicePerformanceMetricsDTO> getPerformanceMetrics(
            Integer page, Integer size, Long deviceId, LocalDateTime startTime, LocalDateTime endTime) {
        
        logger.info("分页查询性能指标: page={}, size={}, deviceId={}", page, size, deviceId);
        
        try {
            // 构建查询参数
            Map<String, Object> params = buildPerformanceQueryParams(
                page, size, deviceId, startTime, endTime);
            
            // 查询数据列表
            List<DevicePerformanceMetrics> metricsList = 
                devicePerformanceMetricsMapper.selectWithPagination(params);
            
            // 查询总数
            int total = devicePerformanceMetricsMapper.countWithConditions(params);
            
            // 转换为DTO
            List<DevicePerformanceMetricsDTO> dtoList = metricsList.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            // 构建分页结果
            PageResult<DevicePerformanceMetricsDTO> result = new PageResult<>();
            result.setData(dtoList);
            result.setTotal(total);
            result.setPage(page);
            result.setSize(size);
            result.setTotalPages((int) Math.ceil((double) total / size));
            
            return result;
            
        } catch (Exception e) {
            logger.error("分页查询性能指标失败", e);
            return new PageResult<>();
        }
    }
    
    /**
     * 生成性能图表数据
     * 支持前端性能图表显示
     * 
     * @param deviceId 设备ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param intervalMinutes 时间间隔（分钟）
     * @return 图表数据DTO
     */
    public DeviceChartDataDTO getPerformanceChartData(
            Long deviceId, LocalDateTime startTime, LocalDateTime endTime, Integer intervalMinutes) {
        
        logger.info("生成性能图表数据: deviceId={}, interval={}分钟", deviceId, intervalMinutes);
        
        try {
            // 查询图表数据
            List<Map<String, Object>> chartData = devicePerformanceMetricsMapper
                    .selectChartData(deviceId, startTime, endTime, intervalMinutes);
            
            if (chartData.isEmpty()) {
                logger.warn("未找到图表数据: deviceId={}", deviceId);
                return createEmptyChartData();
            }
            
            // 构建图表DTO
            DeviceChartDataDTO chartDTO = new DeviceChartDataDTO();
            chartDTO.setChartType("performance");
            chartDTO.setTitle("设备性能监控");
            chartDTO.setTimeRange(formatTimeRange(startTime, endTime));
            
            // 提取时间标签
            List<String> labels = chartData.stream()
                    .map(data -> (String) data.get("timePoint"))
                    .collect(Collectors.toList());
            chartDTO.setLabels(labels);
            
            // 构建数据系列
            List<DeviceChartDataDTO.ChartSeriesDTO> seriesList = new ArrayList<>();
            
            // CPU使用率系列
            seriesList.add(createChartSeries("CPU使用率", "line", "#ff6b6b", 
                chartData, "avgCpuUsage", "%"));
            
            // 内存使用率系列
            seriesList.add(createChartSeries("内存使用率", "line", "#4ecdc4", 
                chartData, "avgMemoryUsage", "%"));
            
            // 磁盘使用率系列
            seriesList.add(createChartSeries("磁盘使用率", "line", "#45b7d1", 
                chartData, "avgDiskUsage", "%"));
            
            // 温度系列
            seriesList.add(createChartSeries("设备温度", "line", "#f9ca24", 
                chartData, "avgTemperature", "°C"));
            
            chartDTO.setSeries(seriesList);
            
            // 设置图表配置
            chartDTO.setConfig(createChartConfig());
            
            // 计算统计信息
            chartDTO.setStatistics(calculateChartStatistics(chartData));
            
            return chartDTO;
            
        } catch (Exception e) {
            logger.error("生成性能图表数据失败: deviceId={}", deviceId, e);
            return createEmptyChartData();
        }
    }
    
    /**
     * 获取多设备性能对比数据
     * 
     * @param deviceIds 设备ID列表
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 对比数据列表
     */
    public List<Map<String, Object>> getPerformanceComparison(
            List<Long> deviceIds, LocalDateTime startTime, LocalDateTime endTime) {
        
        logger.info("获取多设备性能对比数据: deviceCount={}", deviceIds.size());
        
        try {
            return devicePerformanceMetricsMapper.selectComparisonData(deviceIds, startTime, endTime);
        } catch (Exception e) {
            logger.error("获取多设备性能对比数据失败", e);
            return List.of();
        }
    }
    
    /**
     * 计算设备平均性能指标
     * 
     * @param deviceId 设备ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 平均性能指标Map
     */
    public Map<String, BigDecimal> calculateAverageMetrics(
            Long deviceId, LocalDateTime startTime, LocalDateTime endTime) {
        
        logger.info("计算设备平均性能指标: deviceId={}", deviceId);
        
        try {
            return devicePerformanceMetricsMapper.calculateAverageMetrics(deviceId, startTime, endTime);
        } catch (Exception e) {
            logger.error("计算设备平均性能指标失败: deviceId={}", deviceId, e);
            return new HashMap<>();
        }
    }
    
    /**
     * 计算所有设备的平均性能
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 平均性能值
     */
    public BigDecimal calculateOverallAveragePerformance(LocalDateTime startTime, LocalDateTime endTime) {
        logger.info("计算所有设备平均性能");
        
        try {
            BigDecimal avgPerformance = devicePerformanceMetricsMapper
                    .calculateOverallAveragePerformance(startTime, endTime);
            return avgPerformance != null ? avgPerformance : BigDecimal.ZERO;
        } catch (Exception e) {
            logger.error("计算所有设备平均性能失败", e);
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * 查询性能异常的设备
     * 
     * @param cpuThreshold CPU阈值
     * @param memoryThreshold 内存阈值
     * @param recentMinutes 最近几分钟
     * @return 异常设备列表
     */
    public List<DevicePerformanceMetricsDTO> getAbnormalPerformanceDevices(
            BigDecimal cpuThreshold, BigDecimal memoryThreshold, Integer recentMinutes) {
        
        logger.info("查询性能异常设备: cpuThreshold={}, memoryThreshold={}", cpuThreshold, memoryThreshold);
        
        try {
            List<DevicePerformanceMetrics> abnormalDevices = devicePerformanceMetricsMapper
                    .selectAbnormalPerformance(cpuThreshold, memoryThreshold, recentMinutes);
            
            return abnormalDevices.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            logger.error("查询性能异常设备失败", e);
            return List.of();
        }
    }
    
    /**
     * 获取性能指标分布统计
     * 
     * @param metricType 指标类型（cpu、memory、disk等）
     * @return 分布统计Map
     */
    public Map<String, Integer> getMetricsDistribution(String metricType) {
        logger.info("获取性能指标分布统计: metricType={}", metricType);
        
        try {
            return devicePerformanceMetricsMapper.countMetricsDistribution(metricType);
        } catch (Exception e) {
            logger.error("获取性能指标分布统计失败: metricType={}", metricType, e);
            return new HashMap<>();
        }
    }
    
    /**
     * 批量插入性能指标数据
     * 
     * @param metricsList 性能指标列表
     * @return 插入的记录数
     */
    @Transactional
    public int batchInsertMetrics(List<DevicePerformanceMetrics> metricsList) {
        logger.info("批量插入性能指标数据: count={}", metricsList.size());
        
        try {
            int result = devicePerformanceMetricsMapper.batchInsert(metricsList);
            logger.info("批量插入性能指标数据成功: 插入了{}条记录", result);
            return result;
        } catch (Exception e) {
            logger.error("批量插入性能指标数据失败", e);
            return 0;
        }
    }
    
    /**
     * 清理过期的性能指标数据
     * 
     * @param retentionDays 保留天数
     * @return 清理的记录数
     */
    @Transactional
    public int cleanupExpiredMetrics(int retentionDays) {
        logger.info("清理过期性能指标数据: retentionDays={}", retentionDays);
        
        try {
            LocalDateTime beforeTime = LocalDateTime.now().minusDays(retentionDays);
            int result = devicePerformanceMetricsMapper.deleteExpiredMetrics(beforeTime);
            
            logger.info("清理过期性能指标数据成功: 清理了{}条记录", result);
            return result;
        } catch (Exception e) {
            logger.error("清理过期性能指标数据失败: retentionDays={}", retentionDays, e);
            return 0;
        }
    }
    
    /**
     * 构建性能查询参数
     */
    private Map<String, Object> buildPerformanceQueryParams(
            Integer page, Integer size, Long deviceId, LocalDateTime startTime, LocalDateTime endTime) {
        
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
        
        if (startTime != null) {
            params.put("startTime", startTime);
        }
        
        if (endTime != null) {
            params.put("endTime", endTime);
        }
        
        // 默认排序
        params.put("orderBy", "metric_timestamp DESC");
        
        return params;
    }
    
    /**
     * 转换实体对象为DTO对象
     */
    private DevicePerformanceMetricsDTO convertToDTO(DevicePerformanceMetrics metrics) {
        if (metrics == null) {
            return null;
        }
        
        DevicePerformanceMetricsDTO dto = new DevicePerformanceMetricsDTO();
        
        // 基本信息
        dto.setId(metrics.getId() != null ? metrics.getId().toString() : null);
        dto.setDeviceId(metrics.getDeviceId() != null ? metrics.getDeviceId().toString() : null);
        
        // 性能指标
        dto.setCpu(metrics.getCpuUsage());
        dto.setMemory(metrics.getMemoryUsage());
        dto.setDisk(metrics.getDiskUsage());
        dto.setTemperature(metrics.getTemperature());
        dto.setBatteryLevel(metrics.getBatteryLevel());
        dto.setNetworkLatency(metrics.getNetworkLatency());
        dto.setNetworkBandwidth(metrics.getNetworkBandwidth());
        
        // 时间信息
        dto.setTimestamp(metrics.getMetricTimestamp());
        dto.setCreatedAt(metrics.getCreatedAt());
        
        // 设置格式化时间
        setFormattedTimes(dto);
        
        // 设置状态描述
        setStatusDescriptions(dto);
        
        // 计算综合性能分数
        dto.setOverallPerformance(calculateOverallPerformance(metrics));
        dto.setPerformanceLevel(getPerformanceLevel(dto.getOverallPerformance()));
        
        return dto;
    }
    
    /**
     * 设置格式化时间字符串
     */
    private void setFormattedTimes(DevicePerformanceMetricsDTO dto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        if (dto.getTimestamp() != null) {
            dto.setTimestampFormatted(dto.getTimestamp().format(formatter));
        }
        
        if (dto.getCreatedAt() != null) {
            dto.setCreatedAtFormatted(dto.getCreatedAt().format(formatter));
        }
    }
    
    /**
     * 设置状态描述
     */
    private void setStatusDescriptions(DevicePerformanceMetricsDTO dto) {
        // CPU状态描述
        if (dto.getCpu() != null) {
            dto.setCpuStatus(getUsageStatus(dto.getCpu(), "CPU"));
        }
        
        // 内存状态描述
        if (dto.getMemory() != null) {
            dto.setMemoryStatus(getUsageStatus(dto.getMemory(), "内存"));
        }
        
        // 磁盘状态描述
        if (dto.getDisk() != null) {
            dto.setDiskStatus(getUsageStatus(dto.getDisk(), "磁盘"));
        }
        
        // 温度状态描述
        if (dto.getTemperature() != null) {
            dto.setTemperatureStatus(getTemperatureStatus(dto.getTemperature()));
        }
        
        // 电池状态描述
        if (dto.getBatteryLevel() != null) {
            dto.setBatteryStatus(getBatteryStatus(dto.getBatteryLevel()));
        }
    }
    
    /**
     * 获取使用率状态描述
     */
    private String getUsageStatus(BigDecimal usage, String type) {
        if (usage.compareTo(BigDecimal.valueOf(80)) > 0) {
            return type + "使用率过高";
        } else if (usage.compareTo(BigDecimal.valueOf(60)) > 0) {
            return type + "使用率较高";
        } else if (usage.compareTo(BigDecimal.valueOf(30)) > 0) {
            return type + "使用率正常";
        } else {
            return type + "使用率较低";
        }
    }
    
    /**
     * 获取温度状态描述
     */
    private String getTemperatureStatus(BigDecimal temperature) {
        if (temperature.compareTo(BigDecimal.valueOf(80)) > 0) {
            return "温度过高";
        } else if (temperature.compareTo(BigDecimal.valueOf(60)) > 0) {
            return "温度较高";
        } else if (temperature.compareTo(BigDecimal.valueOf(40)) > 0) {
            return "温度正常";
        } else {
            return "温度较低";
        }
    }
    
    /**
     * 获取电池状态描述
     */
    private String getBatteryStatus(BigDecimal batteryLevel) {
        if (batteryLevel.compareTo(BigDecimal.valueOf(80)) > 0) {
            return "电量充足";
        } else if (batteryLevel.compareTo(BigDecimal.valueOf(50)) > 0) {
            return "电量正常";
        } else if (batteryLevel.compareTo(BigDecimal.valueOf(20)) > 0) {
            return "电量较低";
        } else {
            return "电量不足";
        }
    }
    
    /**
     * 计算综合性能分数
     */
    private BigDecimal calculateOverallPerformance(DevicePerformanceMetrics metrics) {
        if (metrics.getCpuUsage() == null || metrics.getMemoryUsage() == null) {
            return BigDecimal.ZERO;
        }
        
        // 计算综合性能分数（100 - 平均使用率）
        BigDecimal avgUsage = metrics.getCpuUsage().add(metrics.getMemoryUsage())
                .divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
        
        return BigDecimal.valueOf(100).subtract(avgUsage)
                .max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * 获取性能等级
     */
    private String getPerformanceLevel(BigDecimal performance) {
        if (performance == null) {
            return "未知";
        }
        
        if (performance.compareTo(BigDecimal.valueOf(80)) >= 0) {
            return "优秀";
        } else if (performance.compareTo(BigDecimal.valueOf(60)) >= 0) {
            return "良好";
        } else if (performance.compareTo(BigDecimal.valueOf(40)) >= 0) {
            return "一般";
        } else {
            return "较差";
        }
    }
    
    /**
     * 创建图表数据系列
     */
    private DeviceChartDataDTO.ChartSeriesDTO createChartSeries(
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
     * 创建图表配置
     */
    private DeviceChartDataDTO.ChartConfigDTO createChartConfig() {
        DeviceChartDataDTO.ChartConfigDTO config = new DeviceChartDataDTO.ChartConfigDTO();
        config.setxAxisTitle("时间");
        config.setyAxisTitle("使用率(%)");
        config.setShowLegend(true);
        config.setShowGrid(true);
        config.setEnableZoom(true);
        config.setHeight(400);
        config.setTheme("light");
        return config;
    }
    
    /**
     * 计算图表统计信息
     */
    private DeviceChartDataDTO.ChartStatisticsDTO calculateChartStatistics(List<Map<String, Object>> chartData) {
        DeviceChartDataDTO.ChartStatisticsDTO stats = new DeviceChartDataDTO.ChartStatisticsDTO();
        
        if (chartData.isEmpty()) {
            return stats;
        }
        
        // 计算CPU使用率统计
        List<BigDecimal> cpuValues = chartData.stream()
                .map(data -> (BigDecimal) data.get("avgCpuUsage"))
                .filter(value -> value != null)
                .collect(Collectors.toList());
        
        if (!cpuValues.isEmpty()) {
            stats.setMaxValue(cpuValues.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO));
            stats.setMinValue(cpuValues.stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO));
            
            BigDecimal sum = cpuValues.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            stats.setAvgValue(sum.divide(BigDecimal.valueOf(cpuValues.size()), 2, RoundingMode.HALF_UP));
            
            stats.setCurrentValue(cpuValues.get(cpuValues.size() - 1));
            
            // 计算趋势
            if (cpuValues.size() > 1) {
                BigDecimal first = cpuValues.get(0);
                BigDecimal last = cpuValues.get(cpuValues.size() - 1);
                
                if (last.compareTo(first) > 0) {
                    stats.setTrend("up");
                    stats.setTrendDescription("性能使用率上升");
                } else if (last.compareTo(first) < 0) {
                    stats.setTrend("down");
                    stats.setTrendDescription("性能使用率下降");
                } else {
                    stats.setTrend("stable");
                    stats.setTrendDescription("性能使用率稳定");
                }
            }
        }
        
        return stats;
    }
    
    /**
     * 创建空的图表数据
     */
    private DeviceChartDataDTO createEmptyChartData() {
        DeviceChartDataDTO chartDTO = new DeviceChartDataDTO();
        chartDTO.setChartType("performance");
        chartDTO.setTitle("设备性能监控");
        chartDTO.setLabels(List.of());
        chartDTO.setSeries(List.of());
        chartDTO.setConfig(createChartConfig());
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