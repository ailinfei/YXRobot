package com.yxrobot.service;

import com.yxrobot.dto.ManagedDeviceLogDTO;
import com.yxrobot.entity.LogCategory;
import com.yxrobot.entity.LogLevel;
import com.yxrobot.entity.ManagedDeviceLog;
import com.yxrobot.mapper.ManagedDeviceLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 设备日志服务类
 * 处理设备日志业务逻辑，支持日志查询和筛选功能
 */
@Service
public class ManagedDeviceLogService {
    
    @Autowired
    private ManagedDeviceLogMapper managedDeviceLogMapper;
    
    /**
     * 分页查询设备日志（支持筛选）
     * 
     * @param deviceId 设备ID
     * @param page 页码（从1开始）
     * @param pageSize 每页大小
     * @param level 日志级别筛选
     * @param category 日志分类筛选
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 分页日志数据
     */
    public Map<String, Object> getManagedDeviceLogs(Long deviceId, Integer page, Integer pageSize, 
                                                   String level, String category, 
                                                   LocalDateTime startDate, LocalDateTime endDate) {
        // 参数验证
        if (deviceId == null) {
            throw new IllegalArgumentException("设备ID不能为空");
        }
        
        // 设置默认分页参数
        if (page == null || page < 1) {
            page = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 20;
        }
        
        // 计算偏移量
        int offset = (page - 1) * pageSize;
        
        // 查询日志列表
        List<ManagedDeviceLog> logs = managedDeviceLogMapper.selectByPage(
            deviceId, offset, pageSize, level, category, startDate, endDate
        );
        
        // 查询总数
        Integer total = managedDeviceLogMapper.countByConditions(
            deviceId, level, category, startDate, endDate
        );
        
        // 转换为DTO
        List<ManagedDeviceLogDTO> logDTOs = logs.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("list", logDTOs);
        result.put("total", total != null ? total : 0);
        result.put("page", page);
        result.put("pageSize", pageSize);
        result.put("totalPages", (int) Math.ceil((double) (total != null ? total : 0) / pageSize));
        
        return result;
    }
    
    /**
     * 创建设备日志
     * 
     * @param deviceId 设备ID
     * @param level 日志级别
     * @param category 日志分类
     * @param message 日志消息
     * @param details 详细信息
     * @return 创建的日志ID
     */
    public Long createLog(Long deviceId, LogLevel level, LogCategory category, 
                         String message, Map<String, Object> details) {
        // 参数验证
        if (deviceId == null) {
            throw new IllegalArgumentException("设备ID不能为空");
        }
        if (level == null) {
            throw new IllegalArgumentException("日志级别不能为空");
        }
        if (category == null) {
            throw new IllegalArgumentException("日志分类不能为空");
        }
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("日志消息不能为空");
        }
        
        // 创建日志对象
        ManagedDeviceLog log = new ManagedDeviceLog();
        log.setDeviceId(deviceId);
        log.setTimestamp(LocalDateTime.now());
        log.setLevel(level);
        log.setCategory(category);
        log.setMessage(message.trim());
        log.setDetails(details);
        
        // 插入数据库
        int result = managedDeviceLogMapper.insert(log);
        if (result > 0) {
            return log.getId();
        } else {
            throw new RuntimeException("创建设备日志失败");
        }
    }
    
    /**
     * 根据设备ID删除所有日志
     * 
     * @param deviceId 设备ID
     * @return 删除的日志数量
     */
    public int deleteLogsByDeviceId(Long deviceId) {
        if (deviceId == null) {
            throw new IllegalArgumentException("设备ID不能为空");
        }
        
        return managedDeviceLogMapper.deleteByDeviceId(deviceId);
    }
    
    /**
     * 根据时间范围删除日志
     * 
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 删除的日志数量
     */
    public int deleteLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("开始时间和结束时间不能为空");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("开始时间不能晚于结束时间");
        }
        
        return managedDeviceLogMapper.deleteByDateRange(startDate, endDate);
    }
    
    /**
     * 将实体对象转换为DTO对象
     * 
     * @param log 日志实体
     * @return 日志DTO
     */
    private ManagedDeviceLogDTO convertToDTO(ManagedDeviceLog log) {
        if (log == null) {
            return null;
        }
        
        ManagedDeviceLogDTO dto = new ManagedDeviceLogDTO();
        dto.setId(log.getId() != null ? log.getId().toString() : null);
        dto.setDeviceId(log.getDeviceId() != null ? log.getDeviceId().toString() : null);
        dto.setTimestamp(log.getTimestamp());
        dto.setLevel(log.getLevel() != null ? log.getLevel().name().toLowerCase() : null);
        dto.setCategory(log.getCategory() != null ? log.getCategory().name().toLowerCase() : null);
        dto.setMessage(log.getMessage());
        dto.setDetails(log.getDetails());
        dto.setCreatedAt(log.getCreatedAt());
        
        return dto;
    }
    
    /**
     * 批量创建设备日志
     * 
     * @param logs 日志列表
     * @return 创建成功的日志数量
     */
    public int createLogs(List<ManagedDeviceLog> logs) {
        if (logs == null || logs.isEmpty()) {
            return 0;
        }
        
        int successCount = 0;
        for (ManagedDeviceLog log : logs) {
            try {
                // 设置时间戳
                if (log.getTimestamp() == null) {
                    log.setTimestamp(LocalDateTime.now());
                }
                
                int result = managedDeviceLogMapper.insert(log);
                if (result > 0) {
                    successCount++;
                }
            } catch (Exception e) {
                // 记录错误但继续处理其他日志
                System.err.println("创建设备日志失败: " + e.getMessage());
            }
        }
        
        return successCount;
    }
    
    /**
     * 记录设备操作日志
     * 
     * @param deviceId 设备ID
     * @param operation 操作类型
     * @param result 操作结果
     * @param operator 操作人
     */
    public void logDeviceOperation(Long deviceId, String operation, String result, String operator) {
        Map<String, Object> details = new HashMap<>();
        details.put("operation", operation);
        details.put("result", result);
        details.put("operator", operator);
        details.put("operationTime", LocalDateTime.now().toString());
        
        createLog(deviceId, LogLevel.INFO, LogCategory.USER, 
                 "设备操作: " + operation + " - " + result, details);
    }
    
    /**
     * 记录设备状态变更日志
     * 
     * @param deviceId 设备ID
     * @param oldStatus 原状态
     * @param newStatus 新状态
     * @param reason 变更原因
     */
    public void logStatusChange(Long deviceId, String oldStatus, String newStatus, String reason) {
        Map<String, Object> details = new HashMap<>();
        details.put("oldStatus", oldStatus);
        details.put("newStatus", newStatus);
        details.put("reason", reason);
        details.put("changeTime", LocalDateTime.now().toString());
        
        createLog(deviceId, LogLevel.INFO, LogCategory.SYSTEM, 
                 "设备状态变更: " + oldStatus + " -> " + newStatus, details);
    }
    
    /**
     * 记录设备错误日志
     * 
     * @param deviceId 设备ID
     * @param errorCode 错误代码
     * @param errorMessage 错误消息
     * @param stackTrace 堆栈跟踪
     */
    public void logDeviceError(Long deviceId, String errorCode, String errorMessage, String stackTrace) {
        Map<String, Object> details = new HashMap<>();
        details.put("errorCode", errorCode);
        details.put("stackTrace", stackTrace);
        details.put("errorTime", LocalDateTime.now().toString());
        
        createLog(deviceId, LogLevel.ERROR, LogCategory.SYSTEM, 
                 "设备错误: " + errorMessage, details);
    }
}