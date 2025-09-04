package com.yxrobot.service;

import com.yxrobot.dto.DeviceAlertDTO;
import com.yxrobot.entity.DeviceAlert;
import com.yxrobot.entity.AlertLevel;
import com.yxrobot.mapper.DeviceAlertMapper;
import com.yxrobot.service.DeviceMonitoringService.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 设备告警服务类
 * 处理设备告警业务逻辑，支持告警查询和管理功能
 * 
 * 主要功能：
 * - 告警数据查询和筛选
 * - 告警级别管理
 * - 告警解决和批量操作
 * - 告警统计和趋势分析
 * - 自动告警生成
 * 
 * @author YXRobot Development Team
 * @since 2024
 */
@Service
@Transactional(readOnly = true)
public class DeviceAlertService {
    
    private static final Logger logger = LoggerFactory.getLogger(DeviceAlertService.class);
    
    @Autowired
    private DeviceAlertMapper deviceAlertMapper;
    
    /**
     * 分页查询设备告警列表
     * 支持按告警级别、时间范围的筛选查询
     * 
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @param alertLevel 告警级别筛选
     * @param deviceId 设备ID筛选
     * @param isResolved 是否已解决筛选
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param keyword 关键词搜索
     * @return 分页结果
     */
    public PageResult<DeviceAlertDTO> getDeviceAlerts(
            Integer page, Integer size, String alertLevel, Long deviceId, 
            Boolean isResolved, LocalDateTime startTime, LocalDateTime endTime, String keyword) {
        
        logger.info("查询设备告警列表: page={}, size={}, level={}, deviceId={}, resolved={}", 
                   page, size, alertLevel, deviceId, isResolved);
        
        try {
            // 1. 构建查询参数
            Map<String, Object> params = buildAlertQueryParams(
                page, size, alertLevel, deviceId, isResolved, startTime, endTime, keyword);
            
            // 2. 查询数据列表
            List<DeviceAlert> alertList = deviceAlertMapper.selectWithPagination(params);
            
            // 3. 查询总数
            int total = deviceAlertMapper.countWithConditions(params);
            
            // 4. 转换为DTO
            List<DeviceAlertDTO> dtoList = alertList.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            // 5. 构建分页结果
            PageResult<DeviceAlertDTO> result = new PageResult<>();
            result.setData(dtoList);
            result.setTotal(total);
            result.setPage(page);
            result.setSize(size);
            result.setTotalPages((int) Math.ceil((double) total / size));
            
            logger.info("成功查询到{}条设备告警数据", dtoList.size());
            return result;
            
        } catch (Exception e) {
            logger.error("查询设备告警列表失败", e);
            return new PageResult<>();
        }
    }
    
    /**
     * 根据设备ID查询告警列表
     * 
     * @param deviceId 设备ID
     * @param limit 限制数量
     * @return 告警列表
     */
    public List<DeviceAlertDTO> getAlertsByDeviceId(Long deviceId, Integer limit) {
        logger.info("查询设备告警: deviceId={}, limit={}", deviceId, limit);
        
        try {
            List<DeviceAlert> alertList = deviceAlertMapper.selectByDeviceId(deviceId, limit);
            
            return alertList.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            logger.error("查询设备告警失败: deviceId={}", deviceId, e);
            return List.of();
        }
    }
    
    /**
     * 根据告警级别查询告警列表
     * 
     * @param alertLevel 告警级别
     * @param limit 限制数量
     * @return 告警列表
     */
    public List<DeviceAlertDTO> getAlertsByLevel(AlertLevel alertLevel, Integer limit) {
        logger.info("根据级别查询告警: level={}, limit={}", alertLevel, limit);
        
        try {
            List<DeviceAlert> alertList = deviceAlertMapper.selectByAlertLevel(alertLevel, limit);
            
            return alertList.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            logger.error("根据级别查询告警失败: level={}", alertLevel, e);
            return List.of();
        }
    }
    
    /**
     * 查询未解决的告警列表
     * 
     * @param limit 限制数量
     * @return 未解决告警列表
     */
    public List<DeviceAlertDTO> getUnresolvedAlerts(Integer limit) {
        logger.info("查询未解决告警: limit={}", limit);
        
        try {
            List<DeviceAlert> alertList = deviceAlertMapper.selectUnresolved(limit);
            
            return alertList.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            logger.error("查询未解决告警失败", e);
            return List.of();
        }
    }
    
    /**
     * 查询最近的告警列表
     * 
     * @param hours 最近几小时
     * @param limit 限制数量
     * @return 最近告警列表
     */
    public List<DeviceAlertDTO> getRecentAlerts(int hours, Integer limit) {
        logger.info("查询最近告警: hours={}, limit={}", hours, limit);
        
        try {
            List<DeviceAlert> alertList = deviceAlertMapper.selectRecent(hours, limit);
            
            return alertList.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            logger.error("查询最近告警失败: hours={}", hours, e);
            return List.of();
        }
    }
    
    /**
     * 创建新告警
     * 
     * @param deviceId 设备ID
     * @param alertLevel 告警级别
     * @param alertType 告警类型
     * @param alertMessage 告警消息
     * @return 创建的告警DTO
     */
    @Transactional
    public DeviceAlertDTO createAlert(Long deviceId, AlertLevel alertLevel, String alertType, String alertMessage) {
        logger.info("创建设备告警: deviceId={}, level={}, type={}", deviceId, alertLevel, alertType);
        
        try {
            // 1. 检查是否存在重复告警
            List<DeviceAlert> duplicateAlerts = deviceAlertMapper.selectDuplicateAlerts(deviceId, alertType);
            
            if (!duplicateAlerts.isEmpty()) {
                logger.info("发现重复告警，跳过创建: deviceId={}, type={}", deviceId, alertType);
                return convertToDTO(duplicateAlerts.get(0));
            }
            
            // 2. 创建新告警
            DeviceAlert alert = new DeviceAlert(deviceId, alertLevel, alertType, alertMessage);
            
            int result = deviceAlertMapper.insert(alert);
            
            if (result > 0) {
                logger.info("设备告警创建成功: id={}, deviceId={}", alert.getId(), deviceId);
                return convertToDTO(alert);
            } else {
                logger.error("设备告警创建失败: deviceId={}", deviceId);
                return null;
            }
            
        } catch (Exception e) {
            logger.error("创建设备告警失败: deviceId={}, level={}", deviceId, alertLevel, e);
            return null;
        }
    }
    
    /**
     * 解决告警
     * 
     * @param alertId 告警ID
     * @param resolvedBy 解决人
     * @return 是否成功
     */
    @Transactional
    public boolean resolveAlert(Long alertId, String resolvedBy) {
        logger.info("解决告警: alertId={}, resolvedBy={}", alertId, resolvedBy);
        
        try {
            int result = deviceAlertMapper.resolveAlert(alertId, resolvedBy, LocalDateTime.now());
            
            if (result > 0) {
                logger.info("告警解决成功: alertId={}", alertId);
                return true;
            } else {
                logger.warn("告警解决失败，未找到告警: alertId={}", alertId);
                return false;
            }
            
        } catch (Exception e) {
            logger.error("解决告警失败: alertId={}", alertId, e);
            return false;
        }
    }
    
    /**
     * 批量解决告警
     * 
     * @param alertIds 告警ID列表
     * @param resolvedBy 解决人
     * @return 解决的告警数量
     */
    @Transactional
    public int batchResolveAlerts(List<Long> alertIds, String resolvedBy) {
        logger.info("批量解决告警: count={}, resolvedBy={}", alertIds.size(), resolvedBy);
        
        try {
            int result = deviceAlertMapper.batchResolveAlerts(alertIds, resolvedBy, LocalDateTime.now());
            logger.info("批量解决告警成功: 解决了{}条告警", result);
            return result;
            
        } catch (Exception e) {
            logger.error("批量解决告警失败: resolvedBy={}", resolvedBy, e);
            return 0;
        }
    }
    
    /**
     * 自动解决过期告警
     * 超过指定时间未处理的低级别告警自动解决
     * 
     * @param expireHours 过期小时数
     * @param alertLevel 告警级别
     * @return 自动解决的告警数量
     */
    @Transactional
    public int autoResolveExpiredAlerts(int expireHours, AlertLevel alertLevel) {
        logger.info("自动解决过期告警: expireHours={}, level={}", expireHours, alertLevel);
        
        try {
            LocalDateTime beforeTime = LocalDateTime.now().minusHours(expireHours);
            String autoResolvedBy = "SYSTEM_AUTO_RESOLVE";
            
            int result = deviceAlertMapper.autoResolveExpiredAlerts(beforeTime, alertLevel, autoResolvedBy);
            
            if (result > 0) {
                logger.info("自动解决过期告警成功: 解决了{}条告警", result);
            }
            
            return result;
            
        } catch (Exception e) {
            logger.error("自动解决过期告警失败: expireHours={}, level={}", expireHours, alertLevel, e);
            return 0;
        }
    }
    
    /**
     * 获取告警统计信息
     * 
     * @return 告警统计Map
     */
    public Map<String, Integer> getAlertStatistics() {
        logger.info("获取告警统计信息");
        
        try {
            return deviceAlertMapper.countAlertsByLevel();
        } catch (Exception e) {
            logger.error("获取告警统计信息失败", e);
            return new HashMap<>();
        }
    }
    
    /**
     * 获取设备告警统计
     * 
     * @param deviceId 设备ID
     * @return 设备告警统计Map
     */
    public Map<String, Integer> getDeviceAlertStatistics(Long deviceId) {
        logger.info("获取设备告警统计: deviceId={}", deviceId);
        
        try {
            return deviceAlertMapper.countAlertsByDevice(deviceId);
        } catch (Exception e) {
            logger.error("获取设备告警统计失败: deviceId={}", deviceId, e);
            return new HashMap<>();
        }
    }
    
    /**
     * 获取告警趋势数据
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 趋势数据列表
     */
    public List<Map<String, Object>> getAlertTrend(LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("获取告警趋势数据: {} 到 {}", startDate, endDate);
        
        try {
            return deviceAlertMapper.selectAlertTrend(startDate, endDate);
        } catch (Exception e) {
            logger.error("获取告警趋势数据失败", e);
            return List.of();
        }
    }
    
    /**
     * 清理已解决的过期告警
     * 
     * @param retentionDays 保留天数
     * @return 清理的告警数量
     */
    @Transactional
    public int cleanupResolvedAlerts(int retentionDays) {
        logger.info("清理已解决的过期告警: retentionDays={}", retentionDays);
        
        try {
            LocalDateTime beforeDate = LocalDateTime.now().minusDays(retentionDays);
            int result = deviceAlertMapper.deleteResolvedAlertsBefore(beforeDate);
            
            logger.info("清理过期告警成功: 清理了{}条告警", result);
            return result;
            
        } catch (Exception e) {
            logger.error("清理过期告警失败: retentionDays={}", retentionDays, e);
            return 0;
        }
    }
    
    /**
     * 构建告警查询参数
     */
    private Map<String, Object> buildAlertQueryParams(
            Integer page, Integer size, String alertLevel, Long deviceId, 
            Boolean isResolved, LocalDateTime startTime, LocalDateTime endTime, String keyword) {
        
        Map<String, Object> params = new HashMap<>();
        
        // 分页参数
        if (page != null && size != null) {
            params.put("offset", (page - 1) * size);
            params.put("limit", size);
        }
        
        // 筛选条件
        if (alertLevel != null && !alertLevel.trim().isEmpty()) {
            params.put("alertLevel", alertLevel.trim().toUpperCase());
        }
        
        if (deviceId != null) {
            params.put("deviceId", deviceId);
        }
        
        if (isResolved != null) {
            params.put("isResolved", isResolved);
        }
        
        if (startTime != null) {
            params.put("startTime", startTime);
        }
        
        if (endTime != null) {
            params.put("endTime", endTime);
        }
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            params.put("keyword", keyword.trim());
        }
        
        // 默认排序
        params.put("orderBy", "alert_timestamp DESC");
        
        return params;
    }
    
    /**
     * 转换实体对象为DTO对象
     */
    private DeviceAlertDTO convertToDTO(DeviceAlert alert) {
        if (alert == null) {
            return null;
        }
        
        DeviceAlertDTO dto = new DeviceAlertDTO();
        
        // 基本信息
        dto.setId(alert.getId() != null ? alert.getId().toString() : null);
        dto.setDeviceId(alert.getDeviceId() != null ? alert.getDeviceId().toString() : null);
        dto.setLevel(alert.getAlertLevel() != null ? alert.getAlertLevel().getCode() : null);
        dto.setType(alert.getAlertType());
        dto.setMessage(alert.getAlertMessage());
        dto.setTimestamp(alert.getAlertTimestamp());
        dto.setIsResolved(alert.getIsResolved());
        dto.setResolvedBy(alert.getResolvedBy());
        dto.setResolvedAt(alert.getResolvedAt());
        dto.setCreatedAt(alert.getCreatedAt());
        
        // 设置格式化时间
        setFormattedTimes(dto);
        
        // 设置相对时间
        setRelativeTime(dto);
        
        // 设置级别相关信息
        setLevelInfo(dto, alert.getAlertLevel());
        
        return dto;
    }
    
    /**
     * 设置格式化时间字符串
     */
    private void setFormattedTimes(DeviceAlertDTO dto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        if (dto.getTimestamp() != null) {
            dto.setTimestampFormatted(dto.getTimestamp().format(formatter));
        }
        
        if (dto.getResolvedAt() != null) {
            dto.setResolvedAtFormatted(dto.getResolvedAt().format(formatter));
        }
        
        if (dto.getCreatedAt() != null) {
            dto.setCreatedAtFormatted(dto.getCreatedAt().format(formatter));
        }
    }
    
    /**
     * 设置相对时间显示
     */
    private void setRelativeTime(DeviceAlertDTO dto) {
        if (dto.getTimestamp() == null) {
            return;
        }
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime alertTime = dto.getTimestamp();
        
        long minutes = ChronoUnit.MINUTES.between(alertTime, now);
        long hours = ChronoUnit.HOURS.between(alertTime, now);
        long days = ChronoUnit.DAYS.between(alertTime, now);
        
        if (minutes < 1) {
            dto.setRelativeTime("刚刚");
        } else if (minutes < 60) {
            dto.setRelativeTime(minutes + "分钟前");
        } else if (hours < 24) {
            dto.setRelativeTime(hours + "小时前");
        } else if (days < 30) {
            dto.setRelativeTime(days + "天前");
        } else {
            dto.setRelativeTime("很久以前");
        }
    }
    
    /**
     * 设置级别相关信息
     */
    private void setLevelInfo(DeviceAlertDTO dto, AlertLevel alertLevel) {
        if (alertLevel == null) {
            return;
        }
        
        dto.setLevelDescription(alertLevel.getDescription());
        dto.setLevelIcon(alertLevel.getIcon());
        dto.setLevelColor(alertLevel.getColor());
    }
}