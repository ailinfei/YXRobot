package com.yxrobot.service;

import com.yxrobot.dto.PageResult;
import com.yxrobot.entity.CharityStatsLog;
import com.yxrobot.mapper.CharityStatsLogMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 公益统计数据更新日志服务类
 * 负责处理统计数据更新日志的记录、查询和管理功能
 * 支持审计和问题排查功能
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-18
 */
@Service
@Transactional
public class CharityStatsLogService {
    
    private static final Logger logger = LoggerFactory.getLogger(CharityStatsLogService.class);
    
    @Autowired
    private CharityStatsLogMapper charityStatsLogMapper;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 记录统计数据更新历史
     * 用于记录所有统计数据的变更操作，支持审计和问题排查
     * 
     * @param statsId 统计数据ID
     * @param operationType 操作类型（create、update、delete）
     * @param updatedFields 更新的字段名称，多个字段用逗号分隔
     * @param beforeData 更新前的数据对象
     * @param afterData 更新后的数据对象
     * @param updateReason 更新原因
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     * @param operatorIp 操作人IP地址
     * @param dataVersion 数据版本号
     * @param operationResult 操作结果（success、failed）
     * @param errorMessage 错误信息（操作失败时）
     * @param notes 备注信息
     * @return 创建的日志记录ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long logStatsUpdate(Long statsId, String operationType, String updatedFields,
                              Object beforeData, Object afterData, String updateReason,
                              Long operatorId, String operatorName, String operatorIp,
                              Integer dataVersion, String operationResult, String errorMessage,
                              String notes) {
        logger.info("记录统计数据更新日志，操作类型: {}, 操作人: {}, 操作结果: {}", 
                   operationType, operatorName, operationResult);
        
        try {
            // 参数验证
            validateLogParameters(operationType, updateReason, operatorId, operatorName, operationResult);
            
            // 数据完整性检查
            validateDataIntegrity(statsId, operationType, beforeData, afterData);
            
            // 创建日志对象
            CharityStatsLog log = new CharityStatsLog();
            log.setStatsId(statsId);
            log.setOperationType(operationType != null ? operationType.trim() : null);
            log.setUpdatedFields(updatedFields != null ? updatedFields.trim() : null);
            log.setUpdateReason(updateReason != null ? updateReason.trim() : null);
            log.setOperatorId(operatorId);
            log.setOperatorName(operatorName != null ? operatorName.trim() : null);
            log.setOperatorIp(operatorIp != null ? operatorIp.trim() : "unknown");
            log.setOperationTime(LocalDateTime.now());
            log.setDataVersion(dataVersion);
            log.setOperationResult(operationResult != null ? operationResult.trim() : null);
            log.setErrorMessage(errorMessage != null ? errorMessage.trim() : null);
            log.setNotes(notes != null ? notes.trim() : null);
            log.setCreateTime(LocalDateTime.now());
            
            // 序列化数据对象为JSON字符串
            serializeDataObjects(log, beforeData, afterData);
            
            // 插入日志记录
            int result = charityStatsLogMapper.insert(log);
            if (result > 0) {
                logger.info("成功记录统计数据更新日志，日志ID: {}", log.getId());
                return log.getId();
            } else {
                logger.error("插入统计数据更新日志失败，数据库操作返回结果为0");
                throw new RuntimeException("记录统计数据更新日志失败：数据库操作失败");
            }
            
        } catch (IllegalArgumentException e) {
            logger.error("记录统计数据更新日志参数验证失败: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("记录统计数据更新日志时发生异常", e);
            // 日志记录失败不应该影响主业务流程，但需要记录错误
            throw new RuntimeException("记录统计数据更新日志失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 记录统计数据更新历史（简化版本）
     * 提供更简单的接口用于常见的更新日志记录场景
     * 
     * @param statsId 统计数据ID
     * @param operationType 操作类型
     * @param beforeData 更新前数据
     * @param afterData 更新后数据
     * @param updateReason 更新原因
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     * @param request HTTP请求对象（用于获取IP地址）
     * @return 创建的日志记录ID
     */
    public Long logStatsUpdate(Long statsId, String operationType, Object beforeData, Object afterData,
                              String updateReason, Long operatorId, String operatorName, 
                              HttpServletRequest request) {
        String operatorIp = getClientIpAddress(request);
        return logStatsUpdate(statsId, operationType, null, beforeData, afterData, updateReason,
                            operatorId, operatorName, operatorIp, null, "success", null, null);
    }
    
    /**
     * 记录操作失败的日志
     * 专门用于记录操作失败的情况
     * 
     * @param statsId 统计数据ID
     * @param operationType 操作类型
     * @param updateReason 更新原因
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     * @param operatorIp 操作人IP地址
     * @param errorMessage 错误信息
     * @return 创建的日志记录ID
     */
    public Long logFailedOperation(Long statsId, String operationType, String updateReason,
                                  Long operatorId, String operatorName, String operatorIp,
                                  String errorMessage) {
        return logStatsUpdate(statsId, operationType, null, null, null, updateReason,
                            operatorId, operatorName, operatorIp, null, "failed", errorMessage, null);
    }
    
    /**
     * 查询统计数据更新历史记录
     * 支持多种查询条件和分页功能
     * 
     * @param statsId 统计数据ID（可选）
     * @param operatorId 操作人ID（可选）
     * @param operationType 操作类型（可选）
     * @param operationResult 操作结果（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param page 页码（从1开始）
     * @param pageSize 每页数量
     * @return 分页的更新历史记录
     */
    @Transactional(readOnly = true)
    public PageResult<CharityStatsLog> getStatsUpdateHistory(Long statsId, Long operatorId, 
                                                           String operationType, String operationResult,
                                                           LocalDateTime startTime, LocalDateTime endTime,
                                                           Integer page, Integer pageSize) {
        logger.info("查询统计数据更新历史，statsId: {}, operatorId: {}, operationType: {}, page: {}, pageSize: {}", 
                   statsId, operatorId, operationType, page, pageSize);
        
        // 参数验证和默认值设置
        if (page == null || page < 1) {
            page = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 20;
        }
        if (pageSize > 100) {
            pageSize = 100; // 限制最大页面大小
        }
        
        // 验证时间范围的合理性
        if (startTime != null && endTime != null && startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("开始时间不能晚于结束时间");
        }
        
        int offset = (page - 1) * pageSize;
        
        try {
            List<CharityStatsLog> logs = null;
            Long total = 0L;
            
            // 根据不同的查询条件选择相应的查询方法
            if (statsId != null) {
                logs = charityStatsLogMapper.selectByStatsId(statsId, offset, pageSize);
                total = charityStatsLogMapper.countByStatsId(statsId);
            } else if (operatorId != null) {
                logs = charityStatsLogMapper.selectByOperator(operatorId, offset, pageSize);
                total = charityStatsLogMapper.countByOperator(operatorId);
            } else if (operationType != null && !operationType.trim().isEmpty()) {
                logs = charityStatsLogMapper.selectByOperationType(operationType.trim(), offset, pageSize);
                total = charityStatsLogMapper.countByOperationType(operationType.trim());
            } else if (operationResult != null && !operationResult.trim().isEmpty()) {
                logs = charityStatsLogMapper.selectByOperationResult(operationResult.trim(), offset, pageSize);
                total = charityStatsLogMapper.countByOperationResult(operationResult.trim());
            } else if (startTime != null && endTime != null) {
                logs = charityStatsLogMapper.selectByTimeRange(startTime, endTime, offset, pageSize);
                total = charityStatsLogMapper.countByTimeRange(startTime, endTime);
            } else {
                // 获取最新的日志记录
                logs = charityStatsLogMapper.selectLatestLogs(pageSize);
                total = (long) (logs != null ? logs.size() : 0);
            }
            
            // 空值检查
            if (logs == null) {
                logger.warn("查询更新历史记录返回null，返回空结果");
                logs = new ArrayList<>();
                total = 0L;
            }
            
            logger.info("查询到 {} 条更新历史记录，总数: {}", logs.size(), total);
            
            return new PageResult<>(logs, total, page, pageSize);
            
        } catch (IllegalArgumentException e) {
            logger.error("查询统计数据更新历史参数错误: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("查询统计数据更新历史失败", e);
            throw new RuntimeException("查询统计数据更新历史失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 根据统计数据ID查询更新历史
     * 
     * @param statsId 统计数据ID
     * @param page 页码
     * @param pageSize 每页数量
     * @return 分页的更新历史记录
     */
    @Transactional(readOnly = true)
    public PageResult<CharityStatsLog> getStatsUpdateHistoryByStatsId(Long statsId, Integer page, Integer pageSize) {
        return getStatsUpdateHistory(statsId, null, null, null, null, null, page, pageSize);
    }
    
    /**
     * 根据操作人查询更新历史
     * 
     * @param operatorId 操作人ID
     * @param page 页码
     * @param pageSize 每页数量
     * @return 分页的更新历史记录
     */
    @Transactional(readOnly = true)
    public PageResult<CharityStatsLog> getStatsUpdateHistoryByOperator(Long operatorId, Integer page, Integer pageSize) {
        return getStatsUpdateHistory(null, operatorId, null, null, null, null, page, pageSize);
    }
    
    /**
     * 获取最新的更新历史记录
     * 
     * @param limit 限制返回数量
     * @return 最新的更新历史记录列表
     */
    @Transactional(readOnly = true)
    public List<CharityStatsLog> getLatestStatsUpdateHistory(Integer limit) {
        logger.info("获取最新的统计数据更新历史，限制数量: {}", limit);
        
        if (limit == null || limit < 1) {
            limit = 10;
        }
        if (limit > 50) {
            limit = 50; // 限制最大数量
        }
        
        try {
            List<CharityStatsLog> logs = charityStatsLogMapper.selectLatestLogs(limit);
            
            // 空值检查
            if (logs == null) {
                logger.warn("查询最新更新历史记录返回null，返回空列表");
                return new ArrayList<>();
            }
            
            // 过滤null元素
            logs = logs.stream()
                    .filter(log -> log != null)
                    .collect(java.util.stream.Collectors.toList());
            
            logger.info("获取到 {} 条最新更新历史记录", logs.size());
            return logs;
            
        } catch (Exception e) {
            logger.error("获取最新统计数据更新历史失败", e);
            throw new RuntimeException("获取最新统计数据更新历史失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取失败的操作记录
     * 用于问题排查和错误分析
     * 
     * @param page 页码
     * @param pageSize 每页数量
     * @return 分页的失败操作记录
     */
    @Transactional(readOnly = true)
    public PageResult<CharityStatsLog> getFailedOperations(Integer page, Integer pageSize) {
        logger.info("获取失败的操作记录，page: {}, pageSize: {}", page, pageSize);
        
        // 参数验证和默认值设置
        if (page == null || page < 1) {
            page = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 20;
        }
        if (pageSize > 100) {
            pageSize = 100;
        }
        
        int offset = (page - 1) * pageSize;
        
        try {
            List<CharityStatsLog> logs = charityStatsLogMapper.selectFailedOperations(offset, pageSize);
            Long total = charityStatsLogMapper.countFailedOperations();
            
            logger.info("获取到 {} 条失败操作记录，总数: {}", logs.size(), total);
            
            return new PageResult<>(logs, total, page, pageSize);
            
        } catch (Exception e) {
            logger.error("获取失败操作记录失败", e);
            throw new RuntimeException("获取失败操作记录失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取操作统计信息
     * 用于审计和分析操作模式
     * 
     * @return 包含各种统计信息的Map
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getOperationStatistics() {
        logger.info("获取操作统计信息");
        
        try {
            Map<String, Object> statistics = new java.util.HashMap<>();
            
            // 获取操作类型统计
            List<Map<String, Object>> operationTypeStats = charityStatsLogMapper.getOperationTypeStatistics();
            statistics.put("operationTypeStatistics", operationTypeStats);
            
            // 获取操作结果统计
            List<Map<String, Object>> operationResultStats = charityStatsLogMapper.getOperationResultStatistics();
            statistics.put("operationResultStatistics", operationResultStats);
            
            // 获取操作人统计（前10名）
            List<Map<String, Object>> operatorStats = charityStatsLogMapper.getOperatorStatistics(10);
            statistics.put("operatorStatistics", operatorStats);
            
            // 获取每日操作统计（最近30天）
            List<Map<String, Object>> dailyStats = charityStatsLogMapper.getDailyOperationStatistics(30);
            statistics.put("dailyOperationStatistics", dailyStats);
            
            logger.info("获取操作统计信息成功");
            return statistics;
            
        } catch (Exception e) {
            logger.error("获取操作统计信息失败", e);
            throw new RuntimeException("获取操作统计信息失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取数据版本变更历史
     * 用于跟踪数据的版本演进
     * 
     * @param statsId 统计数据ID
     * @return 版本变更历史列表
     */
    @Transactional(readOnly = true)
    public List<CharityStatsLog> getVersionHistory(Long statsId) {
        logger.info("获取数据版本变更历史，statsId: {}", statsId);
        
        if (statsId == null) {
            throw new IllegalArgumentException("统计数据ID不能为空");
        }
        
        try {
            List<CharityStatsLog> versionHistory = charityStatsLogMapper.selectVersionHistory(statsId);
            logger.info("获取到 {} 条版本变更历史记录", versionHistory.size());
            return versionHistory;
            
        } catch (Exception e) {
            logger.error("获取数据版本变更历史失败", e);
            throw new RuntimeException("获取数据版本变更历史失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 清理过期的日志记录
     * 用于定期清理历史日志，避免数据库存储过多历史数据
     * 
     * @param retentionDays 保留天数
     * @return 清理的记录数
     */
    public int cleanExpiredLogs(Integer retentionDays) {
        logger.info("清理过期的日志记录，保留天数: {}", retentionDays);
        
        if (retentionDays == null || retentionDays < 1) {
            retentionDays = 90; // 默认保留90天
        }
        
        try {
            int deletedCount = charityStatsLogMapper.cleanExpiredLogs(retentionDays);
            logger.info("成功清理 {} 条过期日志记录", deletedCount);
            return deletedCount;
            
        } catch (Exception e) {
            logger.error("清理过期日志记录失败", e);
            throw new RuntimeException("清理过期日志记录失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 根据日志ID获取详细信息
     * 
     * @param logId 日志ID
     * @return 日志详细信息
     */
    @Transactional(readOnly = true)
    public CharityStatsLog getLogById(Long logId) {
        logger.info("根据ID获取日志详细信息，logId: {}", logId);
        
        if (logId == null) {
            throw new IllegalArgumentException("日志ID不能为空");
        }
        
        if (logId <= 0) {
            throw new IllegalArgumentException("日志ID必须为正数");
        }
        
        try {
            CharityStatsLog log = charityStatsLogMapper.selectById(logId);
            if (log == null) {
                logger.warn("未找到指定ID的日志记录，logId: {}", logId);
                throw new RuntimeException("未找到指定ID的日志记录: " + logId);
            }
            
            logger.debug("成功获取日志详细信息，logId: {}, 操作类型: {}", logId, log.getOperationType());
            return log;
            
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            logger.error("根据ID获取日志详细信息失败", e);
            throw new RuntimeException("获取日志详细信息失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 验证日志记录参数
     * 
     * @param operationType 操作类型
     * @param updateReason 更新原因
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     * @param operationResult 操作结果
     */
    private void validateLogParameters(String operationType, String updateReason, Long operatorId, 
                                     String operatorName, String operationResult) {
        if (operationType == null || operationType.trim().isEmpty()) {
            throw new IllegalArgumentException("操作类型不能为空");
        }
        
        if (!operationType.matches("^(create|update|delete|recalculate)$")) {
            throw new IllegalArgumentException("操作类型必须是：create、update、delete、recalculate 中的一种");
        }
        
        if (updateReason == null || updateReason.trim().isEmpty()) {
            throw new IllegalArgumentException("更新原因不能为空");
        }
        
        if (updateReason.length() > 200) {
            throw new IllegalArgumentException("更新原因长度不能超过200个字符");
        }
        
        if (operatorId == null) {
            throw new IllegalArgumentException("操作人ID不能为空");
        }
        
        if (operatorName == null || operatorName.trim().isEmpty()) {
            throw new IllegalArgumentException("操作人姓名不能为空");
        }
        
        if (operatorName.length() > 50) {
            throw new IllegalArgumentException("操作人姓名长度不能超过50个字符");
        }
        
        if (operationResult == null || operationResult.trim().isEmpty()) {
            throw new IllegalArgumentException("操作结果不能为空");
        }
        
        if (!operationResult.matches("^(success|failed)$")) {
            throw new IllegalArgumentException("操作结果必须是：success、failed 中的一种");
        }
    }
    
    /**
     * 记录统计数据更新（简化版本）
     * 用于CharityService调用的简化接口
     * 
     * @param charityStats 统计数据实体
     * @param updateReason 更新原因
     * @return 创建的日志记录ID
     */
    public Long logStatsUpdate(Object charityStats, String updateReason) {
        logger.info("记录统计数据更新日志（简化版本），更新原因: {}", updateReason);
        
        try {
            // 参数空值检查
            if (charityStats == null) {
                logger.warn("统计数据对象为空，跳过日志记录");
                return null;
            }
            
            if (updateReason == null || updateReason.trim().isEmpty()) {
                updateReason = "系统更新";
            }
            
            // 使用默认的操作人信息（实际项目中应该从认证系统获取）
            Long operatorId = 1L; // TODO: 从认证系统获取当前用户ID
            String operatorName = "系统"; // TODO: 从认证系统获取当前用户名
            String operatorIp = "127.0.0.1"; // TODO: 从请求中获取真实IP
            
            // 尝试从统计数据对象中获取ID
            Long statsId = null;
            try {
                if (charityStats instanceof com.yxrobot.entity.CharityStats) {
                    statsId = ((com.yxrobot.entity.CharityStats) charityStats).getId();
                }
            } catch (Exception e) {
                logger.debug("无法从统计数据对象中获取ID", e);
            }
            
            return logStatsUpdate(statsId, "update", null, null, charityStats, updateReason.trim(),
                                operatorId, operatorName, operatorIp, null, "success", null, null);
            
        } catch (Exception e) {
            logger.error("记录统计数据更新日志失败", e);
            // 日志记录失败不应该影响主业务流程，但要记录错误
            try {
                // 尝试记录失败日志
                logFailedOperation(null, "update", updateReason, 1L, "系统", "127.0.0.1", 
                                 "日志记录失败: " + e.getMessage());
            } catch (Exception logFailedException) {
                logger.error("记录失败日志也失败了", logFailedException);
            }
            return null;
        }
    }
    
    /**
     * 验证数据完整性
     * 
     * @param statsId 统计数据ID
     * @param operationType 操作类型
     * @param beforeData 更新前数据
     * @param afterData 更新后数据
     */
    private void validateDataIntegrity(Long statsId, String operationType, Object beforeData, Object afterData) {
        // 对于创建操作，不应该有beforeData
        if ("create".equals(operationType) && beforeData != null) {
            logger.warn("创建操作不应该有更新前数据，操作类型: {}", operationType);
        }
        
        // 对于删除操作，不应该有afterData
        if ("delete".equals(operationType) && afterData != null) {
            logger.warn("删除操作不应该有更新后数据，操作类型: {}", operationType);
        }
        
        // 对于更新操作，应该有beforeData和afterData
        if ("update".equals(operationType)) {
            if (beforeData == null) {
                logger.warn("更新操作缺少更新前数据，statsId: {}", statsId);
            }
            if (afterData == null) {
                logger.warn("更新操作缺少更新后数据，statsId: {}", statsId);
            }
        }
        
        // 验证statsId的合理性
        if (statsId != null && statsId <= 0) {
            throw new IllegalArgumentException("统计数据ID必须为正数");
        }
    }
    
    /**
     * 序列化数据对象
     * 
     * @param log 日志对象
     * @param beforeData 更新前数据
     * @param afterData 更新后数据
     */
    private void serializeDataObjects(CharityStatsLog log, Object beforeData, Object afterData) {
        // 序列化更新前数据
        if (beforeData != null) {
            try {
                String beforeJson = objectMapper.writeValueAsString(beforeData);
                // 限制JSON字符串长度，防止数据库字段溢出
                if (beforeJson.length() > 4000) {
                    beforeJson = beforeJson.substring(0, 4000) + "...[truncated]";
                }
                log.setBeforeData(beforeJson);
            } catch (JsonProcessingException e) {
                logger.warn("序列化更新前数据失败", e);
                log.setBeforeData("序列化失败: " + e.getMessage());
            } catch (Exception e) {
                logger.warn("序列化更新前数据时发生未知异常", e);
                log.setBeforeData("序列化异常: " + e.getClass().getSimpleName());
            }
        }
        
        // 序列化更新后数据
        if (afterData != null) {
            try {
                String afterJson = objectMapper.writeValueAsString(afterData);
                // 限制JSON字符串长度，防止数据库字段溢出
                if (afterJson.length() > 4000) {
                    afterJson = afterJson.substring(0, 4000) + "...[truncated]";
                }
                log.setAfterData(afterJson);
            } catch (JsonProcessingException e) {
                logger.warn("序列化更新后数据失败", e);
                log.setAfterData("序列化失败: " + e.getMessage());
            } catch (Exception e) {
                logger.warn("序列化更新后数据时发生未知异常", e);
                log.setAfterData("序列化异常: " + e.getClass().getSimpleName());
            }
        }
    }
    
    /**
     * 从HTTP请求中获取客户端IP地址
     * 考虑代理服务器的情况
     * 
     * @param request HTTP请求对象
     * @return 客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // 如果通过多级代理，第一个IP为客户端真实IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip != null ? ip : "unknown";
    }
}