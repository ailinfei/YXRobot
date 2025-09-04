package com.yxrobot.service;

import com.yxrobot.common.Result;
import com.yxrobot.exception.ManagedDeviceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 设备管理错误处理服务
 * 提供统一的错误处理、数据验证和安全防护功能
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-28
 */
@Service
public class ManagedDeviceErrorHandlingService {
    
    private static final Logger logger = LoggerFactory.getLogger(ManagedDeviceErrorHandlingService.class);
    
    // 数据验证规则
    private static final Pattern SERIAL_NUMBER_PATTERN = Pattern.compile("^[A-Z0-9-]{6,20}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern FIRMWARE_VERSION_PATTERN = Pattern.compile("^\\d+\\.\\d+\\.\\d+$");
    
    // XSS防护模式
    private static final Pattern XSS_PATTERN = Pattern.compile(
        "(?i)<script[^>]*>.*?</script>|javascript:|on\\w+\\s*=|<iframe|<object|<embed", 
        Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );
    
    // SQL注入防护模式
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
        "(?i)(union|select|insert|update|delete|drop|create|alter|exec|execute)\\s+",
        Pattern.CASE_INSENSITIVE
    );
    
    // PII数据模式
    private static final Pattern PHONE_PII_PATTERN = Pattern.compile("1[3-9]\\d{9}");
    private static final Pattern EMAIL_PII_PATTERN = Pattern.compile("[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}");
    private static final Pattern ID_CARD_PATTERN = Pattern.compile("\\d{15}|\\d{18}");
    
    /**
     * 验证设备数据
     * 
     * @param deviceData 设备数据
     * @return 验证结果
     */
    public Map<String, Object> validateDeviceData(Map<String, Object> deviceData) {
        logger.debug("开始验证设备数据");
        
        Map<String, Object> validationResult = new HashMap<>();
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        try {
            // 1. 必填字段验证
            validateRequiredFields(deviceData, errors);
            
            // 2. 字段格式验证
            validateFieldFormats(deviceData, errors, warnings);
            
            // 3. 业务规则验证
            validateBusinessRules(deviceData, errors, warnings);
            
            // 4. 安全验证
            validateSecurity(deviceData, errors, warnings);
            
        } catch (Exception e) {
            logger.error("设备数据验证失败", e);
            errors.add("数据验证过程异常: " + e.getMessage());
        }
        
        validationResult.put("errors", errors);
        validationResult.put("warnings", warnings);
        validationResult.put("isValid", errors.isEmpty());
        validationResult.put("errorCount", errors.size());
        validationResult.put("warningCount", warnings.size());
        
        logger.debug("设备数据验证完成，错误: {}, 警告: {}", errors.size(), warnings.size());
        return validationResult;
    }
    
    /**
     * 处理业务异常
     * 
     * @param exception 异常对象
     * @return 错误响应
     */
    public Result<Map<String, Object>> handleBusinessException(Exception exception) {
        logger.error("处理业务异常", exception);
        
        Map<String, Object> errorData = new HashMap<>();
        
        if (exception instanceof ManagedDeviceException) {
            ManagedDeviceException deviceException = (ManagedDeviceException) exception;
            return Result.error(parseErrorCode(deviceException.getErrorCode()), deviceException.getMessage(), errorData);
        }
        
        // 根据异常类型返回不同的错误信息
        if (exception instanceof IllegalArgumentException) {
            return Result.<Map<String, Object>>error(400, "参数错误: " + sanitizeErrorMessage(exception.getMessage()));
        } else if (exception instanceof NullPointerException) {
            return Result.<Map<String, Object>>error(500, "系统内部错误，请稍后重试");
        } else if (exception instanceof RuntimeException) {
            return Result.<Map<String, Object>>error(500, "业务处理异常: " + sanitizeErrorMessage(exception.getMessage()));
        } else {
            return Result.<Map<String, Object>>error(500, "系统异常，请联系管理员");
        }
    }
    
    /**
     * XSS防护处理
     * 
     * @param input 输入字符串
     * @return 清理后的字符串
     */
    public String sanitizeXSS(String input) {
        if (!StringUtils.hasText(input)) {
            return input;
        }
        
        // 检测XSS攻击
        if (XSS_PATTERN.matcher(input).find()) {
            logger.warn("检测到XSS攻击尝试: {}", input.substring(0, Math.min(input.length(), 50)));
            throw new ManagedDeviceException("输入内容包含不安全字符");
        }
        
        // 基本HTML编码
        return input.replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#x27;")
                   .replace("/", "&#x2F;");
    }
    
    /**
     * SQL注入防护
     * 
     * @param input 输入字符串
     * @return 验证结果
     */
    public boolean validateSQLInjection(String input) {
        if (!StringUtils.hasText(input)) {
            return true;
        }
        
        if (SQL_INJECTION_PATTERN.matcher(input).find()) {
            logger.warn("检测到SQL注入尝试: {}", input.substring(0, Math.min(input.length(), 50)));
            return false;
        }
        
        return true;
    }
    
    /**
     * PII数据保护
     * 
     * @param input 输入字符串
     * @return 脱敏后的字符串
     */
    public String protectPII(String input) {
        if (!StringUtils.hasText(input)) {
            return input;
        }
        
        String result = input;
        
        // 手机号脱敏
        result = PHONE_PII_PATTERN.matcher(result).replaceAll(match -> {
            String phone = match.group();
            return phone.substring(0, 3) + "****" + phone.substring(7);
        });
        
        // 邮箱脱敏
        result = EMAIL_PII_PATTERN.matcher(result).replaceAll(match -> {
            String email = match.group();
            int atIndex = email.indexOf('@');
            if (atIndex > 2) {
                return email.substring(0, 2) + "***" + email.substring(atIndex);
            }
            return "***" + email.substring(atIndex);
        });
        
        // 身份证脱敏
        result = ID_CARD_PATTERN.matcher(result).replaceAll(match -> {
            String idCard = match.group();
            return idCard.substring(0, 4) + "**********" + idCard.substring(idCard.length() - 4);
        });
        
        return result;
    }
    
    /**
     * 参数验证
     * 
     * @param parameters 参数映射
     * @return 验证结果
     */
    public Map<String, Object> validateParameters(Map<String, Object> parameters) {
        logger.debug("开始验证API参数");
        
        Map<String, Object> validationResult = new HashMap<>();
        List<String> errors = new ArrayList<>();
        
        if (parameters == null || parameters.isEmpty()) {
            validationResult.put("errors", errors);
            validationResult.put("isValid", true);
            return validationResult;
        }
        
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            
            // 验证参数名安全性
            if (!validateSQLInjection(key)) {
                errors.add("参数名包含不安全字符: " + key);
                continue;
            }
            
            // 验证参数值
            if (value instanceof String) {
                String stringValue = (String) value;
                
                // XSS防护
                if (XSS_PATTERN.matcher(stringValue).find()) {
                    errors.add("参数值包含不安全字符: " + key);
                }
                
                // SQL注入防护
                if (!validateSQLInjection(stringValue)) {
                    errors.add("参数值包含SQL注入风险: " + key);
                }
            }
        }
        
        validationResult.put("errors", errors);
        validationResult.put("isValid", errors.isEmpty());
        
        return validationResult;
    }
    
    /**
     * 生成友好的错误信息
     * 
     * @param errorCode 错误代码
     * @param originalMessage 原始错误信息
     * @return 友好的错误信息
     */
    public String generateFriendlyErrorMessage(int errorCode, String originalMessage) {
        switch (errorCode) {
            case 400:
                return "请求参数有误，请检查输入内容";
            case 401:
                return "身份验证失败，请重新登录";
            case 403:
                return "权限不足，无法执行此操作";
            case 404:
                return "请求的资源不存在";
            case 409:
                return "数据冲突，请刷新后重试";
            case 422:
                return "数据验证失败，请检查输入格式";
            case 429:
                return "请求过于频繁，请稍后重试";
            case 500:
                return "系统内部错误，请稍后重试或联系管理员";
            case 502:
                return "服务暂时不可用，请稍后重试";
            case 503:
                return "服务维护中，请稍后重试";
            default:
                return sanitizeErrorMessage(originalMessage);
        }
    }
    
    /**
     * 记录安全事件
     * 
     * @param eventType 事件类型
     * @param description 事件描述
     * @param clientInfo 客户端信息
     */
    public void logSecurityEvent(String eventType, String description, Map<String, Object> clientInfo) {
        Map<String, Object> securityEvent = new HashMap<>();
        securityEvent.put("eventType", eventType);
        securityEvent.put("description", description);
        securityEvent.put("timestamp", new Date());
        securityEvent.put("clientInfo", clientInfo);
        
        // 记录安全日志
        logger.warn("安全事件 - 类型: {}, 描述: {}, 客户端: {}", eventType, description, clientInfo);
        
        // 这里可以集成安全监控系统
        // 例如发送告警、记录到安全日志系统等
    }
    
    // 私有方法
    
    /**
     * 验证必填字段
     */
    private void validateRequiredFields(Map<String, Object> deviceData, List<String> errors) {
        String[] requiredFields = {"serialNumber", "model", "status"};
        
        for (String field : requiredFields) {
            Object value = deviceData.get(field);
            if (value == null || (value instanceof String && !StringUtils.hasText((String) value))) {
                errors.add("必填字段不能为空: " + field);
            }
        }
    }
    
    /**
     * 验证字段格式
     */
    private void validateFieldFormats(Map<String, Object> deviceData, List<String> errors, List<String> warnings) {
        // 验证序列号格式
        Object serialNumber = deviceData.get("serialNumber");
        if (serialNumber instanceof String && StringUtils.hasText((String) serialNumber)) {
            if (!SERIAL_NUMBER_PATTERN.matcher((String) serialNumber).matches()) {
                errors.add("设备序列号格式不正确，应为6-20位字母数字和连字符组合");
            }
        }
        
        // 验证手机号格式
        Object customerPhone = deviceData.get("customerPhone");
        if (customerPhone instanceof String && StringUtils.hasText((String) customerPhone)) {
            if (!PHONE_PATTERN.matcher((String) customerPhone).matches()) {
                errors.add("客户手机号格式不正确");
            }
        }
        
        // 验证固件版本格式
        Object firmwareVersion = deviceData.get("firmwareVersion");
        if (firmwareVersion instanceof String && StringUtils.hasText((String) firmwareVersion)) {
            if (!FIRMWARE_VERSION_PATTERN.matcher((String) firmwareVersion).matches()) {
                warnings.add("固件版本格式建议使用x.y.z格式");
            }
        }
        
        // 验证客户姓名长度
        Object customerName = deviceData.get("customerName");
        if (customerName instanceof String) {
            String name = (String) customerName;
            if (name.length() > 50) {
                errors.add("客户姓名长度不能超过50个字符");
            }
        }
        
        // 验证备注长度
        Object notes = deviceData.get("notes");
        if (notes instanceof String) {
            String notesStr = (String) notes;
            if (notesStr.length() > 500) {
                errors.add("备注长度不能超过500个字符");
            }
        }
    }
    
    /**
     * 验证业务规则
     */
    private void validateBusinessRules(Map<String, Object> deviceData, List<String> errors, List<String> warnings) {
        // 验证设备型号和状态的组合
        Object model = deviceData.get("model");
        Object status = deviceData.get("status");
        
        if ("PENDING".equals(status) && deviceData.containsKey("lastOnlineAt")) {
            warnings.add("待激活设备不应有最后在线时间");
        }
        
        // 验证客户ID和客户姓名的一致性
        Object customerId = deviceData.get("customerId");
        Object customerName = deviceData.get("customerName");
        
        if (customerId != null && !StringUtils.hasText((String) customerName)) {
            errors.add("指定客户ID时必须提供客户姓名");
        }
    }
    
    /**
     * 验证安全性
     */
    private void validateSecurity(Map<String, Object> deviceData, List<String> errors, List<String> warnings) {
        for (Map.Entry<String, Object> entry : deviceData.entrySet()) {
            if (entry.getValue() instanceof String) {
                String value = (String) entry.getValue();
                
                // XSS检查
                if (XSS_PATTERN.matcher(value).find()) {
                    errors.add("字段包含不安全字符: " + entry.getKey());
                }
                
                // SQL注入检查
                if (!validateSQLInjection(value)) {
                    errors.add("字段包含SQL注入风险: " + entry.getKey());
                }
            }
        }
    }
    
    /**
     * 解析错误代码
     * 
     * @param errorCode 字符串错误代码
     * @return 整数错误代码
     */
    private Integer parseErrorCode(String errorCode) {
        if (errorCode == null) {
            return 500;
        }
        
        switch (errorCode) {
            case "MANAGED_DEVICE_NOT_FOUND":
                return 404;
            case "SERIAL_NUMBER_EXISTS":
                return 409;
            case "INVALID_STATUS_TRANSITION":
                return 400;
            case "CUSTOMER_NOT_FOUND":
                return 404;
            case "DEVICE_OPERATION_FAILED":
                return 500;
            case "VALIDATION_FAILED":
                return 400;
            case "PERMISSION_DENIED":
                return 403;
            case "BUSINESS_RULE_VIOLATION":
                return 400;
            case "SYSTEM_ERROR":
                return 500;
            case "INVALID_FIRMWARE_VERSION":
                return 400;
            case "UNSUPPORTED_DEVICE_MODEL":
                return 400;
            case "BATCH_OPERATION_PARTIAL_FAILURE":
                return 400;
            default:
                return 500;
        }
    }
    
    /**
     * 清理错误信息
     */
    private String sanitizeErrorMessage(String message) {
        if (!StringUtils.hasText(message)) {
            return "未知错误";
        }
        
        // 移除敏感信息
        String sanitized = message.replaceAll("password|pwd|token|secret|key", "***");
        
        // 限制长度
        if (sanitized.length() > 200) {
            sanitized = sanitized.substring(0, 200) + "...";
        }
        
        return sanitized;
    }
}