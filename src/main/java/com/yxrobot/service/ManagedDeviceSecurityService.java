package com.yxrobot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 设备管理安全监控服务
 * 提供安全事件记录、监控和告警功能
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@Service
public class ManagedDeviceSecurityService {
    
    private static final Logger logger = LoggerFactory.getLogger(ManagedDeviceSecurityService.class);
    private static final Logger securityLogger = LoggerFactory.getLogger("SECURITY");
    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");
    
    // 安全事件计数器
    private final Map<String, AtomicInteger> securityEventCounters = new ConcurrentHashMap<>();
    
    // 最近安全事件记录（用于频率检测）
    private final Map<String, LocalDateTime> recentSecurityEvents = new ConcurrentHashMap<>();
    
    /**
     * 记录安全事件
     * 
     * @param eventType 事件类型
     * @param severity 严重程度（LOW, MEDIUM, HIGH, CRITICAL）
     * @param source 事件源
     * @param message 事件消息
     * @param details 事件详情
     */
    public void logSecurityEvent(String eventType, String severity, String source, String message, Object details) {
        try {
            // 创建安全事件记录
            Map<String, Object> securityEvent = new HashMap<>();
            securityEvent.put("eventType", eventType);
            securityEvent.put("severity", severity);
            securityEvent.put("source", source);
            securityEvent.put("message", sanitizeMessage(message));
            securityEvent.put("details", sanitizeDetails(details));
            securityEvent.put("timestamp", LocalDateTime.now());
            securityEvent.put("eventId", generateEventId());
            
            // 记录到安全日志
            securityLogger.warn("Security Event: {}", securityEvent);
            
            // 更新事件计数器
            updateEventCounter(eventType);
            
            // 检查是否需要告警
            checkSecurityAlert(eventType, severity);
            
            // 记录审计日志
            auditLogger.info("Security audit: eventType={}, severity={}, source={}", 
                eventType, severity, source);
            
        } catch (Exception e) {
            logger.error("记录安全事件失败", e);
        }
    }
    
    /**
     * 记录数据访问事件
     * 
     * @param operation 操作类型（CREATE, READ, UPDATE, DELETE）
     * @param resourceType 资源类型
     * @param resourceId 资源ID
     * @param userId 用户ID
     * @param success 是否成功
     */
    public void logDataAccessEvent(String operation, String resourceType, String resourceId, 
                                 String userId, boolean success) {
        try {
            Map<String, Object> accessEvent = new HashMap<>();
            accessEvent.put("operation", operation);
            accessEvent.put("resourceType", resourceType);
            accessEvent.put("resourceId", sanitizeResourceId(resourceId));
            accessEvent.put("userId", sanitizeUserId(userId));
            accessEvent.put("success", success);
            accessEvent.put("timestamp", LocalDateTime.now());
            accessEvent.put("eventId", generateEventId());
            
            // 记录到审计日志
            auditLogger.info("Data Access: {}", accessEvent);
            
            // 如果操作失败，记录为安全事件
            if (!success) {
                logSecurityEvent("DATA_ACCESS_FAILED", "MEDIUM", "DataAccess", 
                    "数据访问失败: " + operation + " " + resourceType, accessEvent);
            }
            
        } catch (Exception e) {
            logger.error("记录数据访问事件失败", e);
        }
    }
    
    /**
     * 记录认证事件
     * 
     * @param eventType 事件类型（LOGIN, LOGOUT, AUTH_FAILED）
     * @param userId 用户ID
     * @param ipAddress IP地址
     * @param userAgent 用户代理
     * @param success 是否成功
     */
    public void logAuthenticationEvent(String eventType, String userId, String ipAddress, 
                                     String userAgent, boolean success) {
        try {
            Map<String, Object> authEvent = new HashMap<>();
            authEvent.put("eventType", eventType);
            authEvent.put("userId", sanitizeUserId(userId));
            authEvent.put("ipAddress", sanitizeIpAddress(ipAddress));
            authEvent.put("userAgent", sanitizeUserAgent(userAgent));
            authEvent.put("success", success);
            authEvent.put("timestamp", LocalDateTime.now());
            authEvent.put("eventId", generateEventId());
            
            // 记录到审计日志
            auditLogger.info("Authentication: {}", authEvent);
            
            // 如果认证失败，记录为安全事件
            if (!success) {
                String severity = "AUTH_FAILED".equals(eventType) ? "HIGH" : "MEDIUM";
                logSecurityEvent(eventType, severity, "Authentication", 
                    "认证事件: " + eventType, authEvent);
            }
            
        } catch (Exception e) {
            logger.error("记录认证事件失败", e);
        }
    }
    
    /**
     * 记录权限检查事件
     * 
     * @param operation 操作
     * @param resource 资源
     * @param userId 用户ID
     * @param granted 是否授权
     */
    public void logPermissionEvent(String operation, String resource, String userId, boolean granted) {
        try {
            Map<String, Object> permissionEvent = new HashMap<>();
            permissionEvent.put("operation", operation);
            permissionEvent.put("resource", resource);
            permissionEvent.put("userId", sanitizeUserId(userId));
            permissionEvent.put("granted", granted);
            permissionEvent.put("timestamp", LocalDateTime.now());
            permissionEvent.put("eventId", generateEventId());
            
            // 记录到审计日志
            auditLogger.info("Permission Check: {}", permissionEvent);
            
            // 如果权限被拒绝，记录为安全事件
            if (!granted) {
                logSecurityEvent("PERMISSION_DENIED", "MEDIUM", "Authorization", 
                    "权限被拒绝: " + operation + " on " + resource, permissionEvent);
            }
            
        } catch (Exception e) {
            logger.error("记录权限事件失败", e);
        }
    }
    
    /**
     * 记录可疑活动
     * 
     * @param activityType 活动类型
     * @param description 描述
     * @param userId 用户ID
     * @param ipAddress IP地址
     * @param details 详情
     */
    public void logSuspiciousActivity(String activityType, String description, String userId, 
                                    String ipAddress, Object details) {
        try {
            Map<String, Object> suspiciousEvent = new HashMap<>();
            suspiciousEvent.put("activityType", activityType);
            suspiciousEvent.put("description", sanitizeMessage(description));
            suspiciousEvent.put("userId", sanitizeUserId(userId));
            suspiciousEvent.put("ipAddress", sanitizeIpAddress(ipAddress));
            suspiciousEvent.put("details", sanitizeDetails(details));
            suspiciousEvent.put("timestamp", LocalDateTime.now());
            suspiciousEvent.put("eventId", generateEventId());
            
            // 记录为高级别安全事件
            logSecurityEvent("SUSPICIOUS_ACTIVITY", "HIGH", "SecurityMonitor", 
                "可疑活动: " + activityType, suspiciousEvent);
            
        } catch (Exception e) {
            logger.error("记录可疑活动失败", e);
        }
    }
    
    /**
     * 获取安全事件统计
     * 
     * @return 安全事件统计信息
     */
    public Map<String, Object> getSecurityStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // 复制计数器数据（避免并发修改）
        Map<String, Integer> eventCounts = new HashMap<>();
        securityEventCounters.forEach((key, value) -> eventCounts.put(key, value.get()));
        
        stats.put("eventCounts", eventCounts);
        stats.put("totalEvents", eventCounts.values().stream().mapToInt(Integer::intValue).sum());
        stats.put("lastUpdated", LocalDateTime.now());
        
        return stats;
    }
    
    /**
     * 清理过期的安全事件记录
     */
    public void cleanupExpiredEvents() {
        try {
            LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);
            
            // 清理过期的最近事件记录
            recentSecurityEvents.entrySet().removeIf(entry -> entry.getValue().isBefore(cutoffTime));
            
            logger.debug("清理过期安全事件记录完成");
            
        } catch (Exception e) {
            logger.error("清理过期安全事件记录失败", e);
        }
    }
    
    /**
     * 更新事件计数器
     */
    private void updateEventCounter(String eventType) {
        securityEventCounters.computeIfAbsent(eventType, k -> new AtomicInteger(0)).incrementAndGet();
        recentSecurityEvents.put(eventType, LocalDateTime.now());
    }
    
    /**
     * 检查是否需要安全告警
     */
    private void checkSecurityAlert(String eventType, String severity) {
        try {
            // 检查事件频率
            AtomicInteger counter = securityEventCounters.get(eventType);
            if (counter != null && counter.get() > getAlertThreshold(eventType)) {
                // 触发告警
                triggerSecurityAlert(eventType, severity, counter.get());
            }
            
            // 检查严重程度
            if ("CRITICAL".equals(severity) || "HIGH".equals(severity)) {
                triggerSecurityAlert(eventType, severity, 1);
            }
            
        } catch (Exception e) {
            logger.error("检查安全告警失败", e);
        }
    }
    
    /**
     * 触发安全告警
     */
    private void triggerSecurityAlert(String eventType, String severity, int count) {
        Map<String, Object> alert = new HashMap<>();
        alert.put("alertType", "SECURITY_ALERT");
        alert.put("eventType", eventType);
        alert.put("severity", severity);
        alert.put("count", count);
        alert.put("timestamp", LocalDateTime.now());
        
        // 记录告警日志
        securityLogger.error("SECURITY ALERT: {}", alert);
        
        // 这里可以集成告警系统（邮件、短信、钉钉等）
        // alertService.sendAlert(alert);
    }
    
    /**
     * 获取告警阈值
     */
    private int getAlertThreshold(String eventType) {
        switch (eventType) {
            case "XSS_ATTACK":
            case "SQL_INJECTION":
                return 5; // 5次攻击尝试触发告警
            case "AUTH_FAILED":
                return 10; // 10次认证失败触发告警
            case "PERMISSION_DENIED":
                return 20; // 20次权限拒绝触发告警
            default:
                return 50; // 默认阈值
        }
    }
    
    /**
     * 生成事件ID
     */
    private String generateEventId() {
        return "SEC_" + System.currentTimeMillis() + "_" + Thread.currentThread().getId();
    }
    
    /**
     * 清理消息中的敏感信息
     */
    private String sanitizeMessage(String message) {
        if (message == null) {
            return null;
        }
        
        String sanitized = message;
        
        // 替换手机号
        sanitized = sanitized.replaceAll("\\b1[3-9]\\d{9}\\b", "[手机号]");
        
        // 替换邮箱
        sanitized = sanitized.replaceAll("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b", "[邮箱]");
        
        // 替换身份证号
        sanitized = sanitized.replaceAll("\\b\\d{15}|\\d{18}\\b", "[身份证号]");
        
        // 替换银行卡号
        sanitized = sanitized.replaceAll("\\b\\d{16,19}\\b", "[银行卡号]");
        
        return sanitized;
    }
    
    /**
     * 清理详情中的敏感信息
     */
    private Object sanitizeDetails(Object details) {
        if (details == null) {
            return null;
        }
        
        if (details instanceof String) {
            return sanitizeMessage((String) details);
        }
        
        // 对于复杂对象，这里可以进一步处理
        return details;
    }
    
    /**
     * 清理资源ID
     */
    private String sanitizeResourceId(String resourceId) {
        // 资源ID通常不包含敏感信息，但为了安全起见进行基本清理
        return resourceId != null ? resourceId.replaceAll("[^a-zA-Z0-9_-]", "") : null;
    }
    
    /**
     * 清理用户ID
     */
    private String sanitizeUserId(String userId) {
        // 用户ID清理，移除特殊字符
        return userId != null ? userId.replaceAll("[^a-zA-Z0-9_-]", "") : null;
    }
    
    /**
     * 清理IP地址
     */
    private String sanitizeIpAddress(String ipAddress) {
        if (ipAddress == null) {
            return null;
        }
        
        // 验证IP地址格式
        if (ipAddress.matches("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$") || 
            ipAddress.matches("^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$")) {
            return ipAddress;
        }
        
        return "[无效IP]";
    }
    
    /**
     * 清理用户代理
     */
    private String sanitizeUserAgent(String userAgent) {
        if (userAgent == null) {
            return null;
        }
        
        // 限制用户代理长度，移除潜在的恶意内容
        String sanitized = userAgent.replaceAll("[<>\"']", "");
        return sanitized.length() > 200 ? sanitized.substring(0, 200) + "..." : sanitized;
    }
}