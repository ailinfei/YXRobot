package com.yxrobot.service;

import com.yxrobot.dto.DeviceMonitoringStatsDTO;
import com.yxrobot.entity.DeviceMonitoringStats;
import com.yxrobot.mapper.DeviceMonitoringStatsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 设备监控统计服务类
 * 处理监控统计业务逻辑，支持前端统计卡片数据需求
 * 
 * 主要功能：
 * - 实时统计计算（在线、离线、故障设备数量）
 * - 趋势数据计算（与历史数据对比）
 * - 平均性能计算
 * - 统计数据持久化和查询
 * 
 * @author YXRobot Development Team
 * @since 2024
 */
@Service
@Transactional(readOnly = true)
public class DeviceMonitoringStatsService {
    
    private static final Logger logger = LoggerFactory.getLogger(DeviceMonitoringStatsService.class);
    
    @Autowired
    private DeviceMonitoringStatsMapper deviceMonitoringStatsMapper;
    
    /**
     * 获取实时监控统计数据
     * 支持前端统计卡片显示需求
     * 
     * @return 监控统计DTO对象
     */
    public DeviceMonitoringStatsDTO getMonitoringStats() {
        logger.info("开始获取实时监控统计数据");
        
        try {
            // 1. 计算实时统计数据
            DeviceMonitoringStats realTimeStats = deviceMonitoringStatsMapper.calculateRealTimeStats();
            
            if (realTimeStats == null) {
                logger.warn("实时统计数据为空，返回默认值");
                return createDefaultStats();
            }
            
            // 2. 计算趋势数据（与昨天对比）
            DeviceMonitoringStats yesterdayStats = deviceMonitoringStatsMapper.selectHistoryForTrend(
                LocalDate.now(), 1);
            
            // 3. 转换为DTO对象
            DeviceMonitoringStatsDTO statsDTO = convertToDTO(realTimeStats);
            
            // 4. 计算趋势变化
            calculateTrends(statsDTO, realTimeStats, yesterdayStats);
            
            // 5. 设置格式化时间
            setFormattedTimes(statsDTO);
            
            logger.info("成功获取监控统计数据: 在线={}, 离线={}, 故障={}, 平均性能={}", 
                       statsDTO.getOnline(), statsDTO.getOffline(), 
                       statsDTO.getError(), statsDTO.getAvgPerformance());
            
            return statsDTO;
            
        } catch (Exception e) {
            logger.error("获取监控统计数据失败", e);
            return createDefaultStats();
        }
    }
    
    /**
     * 根据日期范围获取历史统计数据
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 历史统计数据列表
     */
    public List<DeviceMonitoringStatsDTO> getHistoryStats(LocalDate startDate, LocalDate endDate) {
        logger.info("获取历史统计数据: {} 到 {}", startDate, endDate);
        
        try {
            List<DeviceMonitoringStats> historyStats = deviceMonitoringStatsMapper.selectByDateRange(startDate, endDate);
            
            return historyStats.stream()
                    .map(this::convertToDTO)
                    .peek(this::setFormattedTimes)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            logger.error("获取历史统计数据失败", e);
            return List.of();
        }
    }
    
    /**
     * 获取最新的统计数据
     * 
     * @return 最新统计DTO对象
     */
    public DeviceMonitoringStatsDTO getLatestStats() {
        logger.info("获取最新统计数据");
        
        try {
            DeviceMonitoringStats latestStats = deviceMonitoringStatsMapper.selectLatest();
            
            if (latestStats == null) {
                logger.warn("没有找到最新统计数据，返回实时统计");
                return getMonitoringStats();
            }
            
            DeviceMonitoringStatsDTO statsDTO = convertToDTO(latestStats);
            setFormattedTimes(statsDTO);
            
            return statsDTO;
            
        } catch (Exception e) {
            logger.error("获取最新统计数据失败", e);
            return createDefaultStats();
        }
    }
    
    /**
     * 保存统计数据到数据库
     * 用于定时任务持久化统计数据
     * 
     * @return 保存的统计DTO对象
     */
    @Transactional
    public DeviceMonitoringStatsDTO saveCurrentStats() {
        logger.info("开始保存当前统计数据");
        
        try {
            // 1. 计算当前实时统计
            DeviceMonitoringStats currentStats = deviceMonitoringStatsMapper.calculateRealTimeStats();
            
            if (currentStats == null) {
                logger.warn("当前统计数据为空，无法保存");
                return createDefaultStats();
            }
            
            // 2. 设置统计日期为今天
            currentStats.setStatsDate(LocalDate.now());
            
            // 3. 计算趋势数据
            DeviceMonitoringStats yesterdayStats = deviceMonitoringStatsMapper.selectHistoryForTrend(
                LocalDate.now(), 1);
            
            if (yesterdayStats != null) {
                currentStats.setOnlineTrend(currentStats.getOnlineCount() - yesterdayStats.getOnlineCount());
                currentStats.setOfflineTrend(currentStats.getOfflineCount() - yesterdayStats.getOfflineCount());
                currentStats.setErrorTrend(currentStats.getErrorCount() - yesterdayStats.getErrorCount());
                
                // 计算性能趋势
                if (currentStats.getAvgPerformance() != null && yesterdayStats.getAvgPerformance() != null) {
                    BigDecimal performanceDiff = currentStats.getAvgPerformance().subtract(yesterdayStats.getAvgPerformance());
                    currentStats.setPerformanceTrend(performanceDiff.intValue());
                }
            }
            
            // 4. 检查今天是否已有统计记录
            DeviceMonitoringStats existingStats = deviceMonitoringStatsMapper.selectByStatsDate(LocalDate.now());
            
            if (existingStats != null) {
                // 更新现有记录
                currentStats.setId(existingStats.getId());
                deviceMonitoringStatsMapper.updateById(currentStats);
                logger.info("更新今日统计数据成功");
            } else {
                // 插入新记录
                deviceMonitoringStatsMapper.insert(currentStats);
                logger.info("插入新统计数据成功");
            }
            
            // 5. 转换为DTO返回
            DeviceMonitoringStatsDTO statsDTO = convertToDTO(currentStats);
            setFormattedTimes(statsDTO);
            
            return statsDTO;
            
        } catch (Exception e) {
            logger.error("保存统计数据失败", e);
            throw new RuntimeException("保存统计数据失败", e);
        }
    }
    
    /**
     * 清理过期的统计数据
     * 
     * @param retentionDays 保留天数
     * @return 清理的记录数
     */
    @Transactional
    public int cleanupExpiredStats(int retentionDays) {
        logger.info("开始清理{}天前的统计数据", retentionDays);
        
        try {
            LocalDate cutoffDate = LocalDate.now().minusDays(retentionDays);
            int deletedCount = deviceMonitoringStatsMapper.deleteExpiredStats(cutoffDate);
            
            logger.info("成功清理{}条过期统计数据", deletedCount);
            return deletedCount;
            
        } catch (Exception e) {
            logger.error("清理过期统计数据失败", e);
            throw new RuntimeException("清理过期统计数据失败", e);
        }
    }
    
    /**
     * 转换实体对象为DTO对象
     * 
     * @param stats 实体对象
     * @return DTO对象
     */
    private DeviceMonitoringStatsDTO convertToDTO(DeviceMonitoringStats stats) {
        if (stats == null) {
            return createDefaultStats();
        }
        
        DeviceMonitoringStatsDTO dto = new DeviceMonitoringStatsDTO();
        
        // 设置ID（转换为字符串）
        dto.setId(stats.getId() != null ? stats.getId().toString() : null);
        
        // 设置核心统计数据（确保字段名与前端匹配）
        dto.setOnline(stats.getOnlineCount() != null ? stats.getOnlineCount() : 0);
        dto.setOffline(stats.getOfflineCount() != null ? stats.getOfflineCount() : 0);
        dto.setError(stats.getErrorCount() != null ? stats.getErrorCount() : 0);
        dto.setMaintenance(stats.getMaintenanceCount() != null ? stats.getMaintenanceCount() : 0);
        dto.setTotal(stats.getTotalCount() != null ? stats.getTotalCount() : 0);
        dto.setAvgPerformance(stats.getAvgPerformance() != null ? stats.getAvgPerformance() : BigDecimal.ZERO);
        
        // 设置趋势数据
        dto.setOnlineTrend(stats.getOnlineTrend() != null ? stats.getOnlineTrend() : 0);
        dto.setOfflineTrend(stats.getOfflineTrend() != null ? stats.getOfflineTrend() : 0);
        dto.setErrorTrend(stats.getErrorTrend() != null ? stats.getErrorTrend() : 0);
        dto.setPerformanceTrend(stats.getPerformanceTrend() != null ? stats.getPerformanceTrend() : 0);
        
        // 设置时间信息
        dto.setStatsDate(stats.getStatsDate());
        dto.setCreatedAt(stats.getCreatedAt());
        dto.setUpdatedAt(stats.getUpdatedAt());
        
        return dto;
    }
    
    /**
     * 计算趋势变化
     * 
     * @param dto 目标DTO对象
     * @param current 当前统计
     * @param previous 历史统计
     */
    private void calculateTrends(DeviceMonitoringStatsDTO dto, DeviceMonitoringStats current, DeviceMonitoringStats previous) {
        if (previous == null) {
            // 没有历史数据，趋势为0
            dto.setOnlineTrend(0);
            dto.setOfflineTrend(0);
            dto.setErrorTrend(0);
            dto.setPerformanceTrend(0);
            return;
        }
        
        // 计算数量变化趋势
        dto.setOnlineTrend(current.getOnlineCount() - previous.getOnlineCount());
        dto.setOfflineTrend(current.getOfflineCount() - previous.getOfflineCount());
        dto.setErrorTrend(current.getErrorCount() - previous.getErrorCount());
        
        // 计算性能变化趋势
        if (current.getAvgPerformance() != null && previous.getAvgPerformance() != null) {
            BigDecimal performanceDiff = current.getAvgPerformance().subtract(previous.getAvgPerformance());
            dto.setPerformanceTrend(performanceDiff.intValue());
        } else {
            dto.setPerformanceTrend(0);
        }
    }
    
    /**
     * 设置格式化时间字符串
     * 
     * @param dto DTO对象
     */
    private void setFormattedTimes(DeviceMonitoringStatsDTO dto) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        if (dto.getStatsDate() != null) {
            dto.setStatsDateFormatted(dto.getStatsDate().format(dateFormatter));
        }
        
        if (dto.getCreatedAt() != null) {
            dto.setCreatedAtFormatted(dto.getCreatedAt().format(dateTimeFormatter));
        }
        
        if (dto.getUpdatedAt() != null) {
            dto.setUpdatedAtFormatted(dto.getUpdatedAt().format(dateTimeFormatter));
        }
    }
    
    /**
     * 创建默认统计数据
     * 
     * @return 默认统计DTO对象
     */
    private DeviceMonitoringStatsDTO createDefaultStats() {
        DeviceMonitoringStatsDTO dto = new DeviceMonitoringStatsDTO();
        dto.setOnline(0);
        dto.setOffline(0);
        dto.setError(0);
        dto.setMaintenance(0);
        dto.setTotal(0);
        dto.setAvgPerformance(BigDecimal.ZERO);
        dto.setOnlineTrend(0);
        dto.setOfflineTrend(0);
        dto.setErrorTrend(0);
        dto.setPerformanceTrend(0);
        dto.setStatsDate(LocalDate.now());
        
        setFormattedTimes(dto);
        
        return dto;
    }
}