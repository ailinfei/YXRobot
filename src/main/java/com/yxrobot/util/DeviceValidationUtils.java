package com.yxrobot.util;

import com.yxrobot.exception.ManagedDeviceException;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 设备数据验证工具类
 * 提供设备管理相关的数据验证功能，包括XSS防护和SQL注入防护
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
public class DeviceValidationUtils {
    
    // 设备序列号格式：字母数字和连字符，长度10-20
    private static final Pattern SERIAL_NUMBER_PATTERN = Pattern.compile("^[A-Z0-9-]{10,20}$");
    
    // 固件版本格式：x.y.z
    private static final Pattern FIRMWARE_VERSION_PATTERN = Pattern.compile("^\\d+\\.\\d+\\.\\d+$");
    
    // 客户名称格式：中文、英文、数字、空格，长度2-100
    private static final Pattern CUSTOMER_NAME_PATTERN = Pattern.compile("^[\\u4e00-\\u9fa5a-zA-Z0-9\\s]{2,100}$");
    
    // 电话号码格式：11位数字或带区号的固定电话
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(1[3-9]\\d{9}|0\\d{2,3}-?\\d{7,8})$");
    
    // 邮箱格式
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    
    // XSS攻击模式
    private static final Pattern[] XSS_PATTERNS = {
        Pattern.compile("<script[^>]*>.*?</script>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
        Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
        Pattern.compile("onload\\s*=", Pattern.CASE_INSENSITIVE),
        Pattern.compile("onerror\\s*=", Pattern.CASE_INSENSITIVE),
        Pattern.compile("onclick\\s*=", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<iframe[^>]*>.*?</iframe>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<object[^>]*>.*?</object>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<embed[^>]*>.*?</embed>", Pattern.CASE_INSENSITIVE)
    };
    
    // SQL注入攻击模式
    private static final Pattern[] SQL_INJECTION_PATTERNS = {
        Pattern.compile("('|(\\-\\-)|(;)|(\\|)|(\\*)|(%))", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(union|select|insert|delete|update|drop|create|alter|exec|execute)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(script|javascript|vbscript|onload|onerror|onclick)", Pattern.CASE_INSENSITIVE)
    };
    
    /**
     * 验证设备序列号
     * 
     * @param serialNumber 设备序列号
     * @throws ManagedDeviceException 验证失败时抛出异常
     */
    public static void validateSerialNumber(String serialNumber) {
        if (!StringUtils.hasText(serialNumber)) {
            throw ManagedDeviceException.validationFailed("serialNumber", serialNumber, "设备序列号不能为空");
        }
        
        // 防XSS和SQL注入检查
        validateAgainstXSS(serialNumber, "设备序列号");
        validateAgainstSQLInjection(serialNumber, "设备序列号");
        
        if (!SERIAL_NUMBER_PATTERN.matcher(serialNumber.trim().toUpperCase()).matches()) {
            throw ManagedDeviceException.validationFailed("serialNumber", serialNumber, 
                "设备序列号格式不正确，应为10-20位字母数字和连字符组合");
        }
    }
    
    /**
     * 验证固件版本
     * 
     * @param firmwareVersion 固件版本
     * @throws ManagedDeviceException 验证失败时抛出异常
     */
    public static void validateFirmwareVersion(String firmwareVersion) {
        if (!StringUtils.hasText(firmwareVersion)) {
            throw ManagedDeviceException.validationFailed("firmwareVersion", firmwareVersion, "固件版本不能为空");
        }
        
        // 防XSS和SQL注入检查
        validateAgainstXSS(firmwareVersion, "固件版本");
        validateAgainstSQLInjection(firmwareVersion, "固件版本");
        
        if (!FIRMWARE_VERSION_PATTERN.matcher(firmwareVersion.trim()).matches()) {
            throw ManagedDeviceException.invalidFirmwareVersion(firmwareVersion);
        }
    }
    
    /**
     * 验证客户名称
     * 
     * @param customerName 客户名称
     * @throws ManagedDeviceException 验证失败时抛出异常
     */
    public static void validateCustomerName(String customerName) {
        if (!StringUtils.hasText(customerName)) {
            throw ManagedDeviceException.validationFailed("customerName", customerName, "客户名称不能为空");
        }
        
        // 防XSS和SQL注入检查
        validateAgainstXSS(customerName, "客户名称");
        validateAgainstSQLInjection(customerName, "客户名称");
        
        if (!CUSTOMER_NAME_PATTERN.matcher(customerName.trim()).matches()) {
            throw ManagedDeviceException.validationFailed("customerName", customerName, 
                "客户名称格式不正确，应为2-100位中文、英文、数字或空格");
        }
    }
    
    /**
     * 验证电话号码
     * 
     * @param phone 电话号码
     * @throws ManagedDeviceException 验证失败时抛出异常
     */
    public static void validatePhone(String phone) {
        if (!StringUtils.hasText(phone)) {
            return; // 电话号码可以为空
        }
        
        // 防XSS和SQL注入检查
        validateAgainstXSS(phone, "电话号码");
        validateAgainstSQLInjection(phone, "电话号码");
        
        if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
            throw ManagedDeviceException.validationFailed("phone", phone, 
                "电话号码格式不正确，应为11位手机号或带区号的固定电话");
        }
    }
    
    /**
     * 验证邮箱地址
     * 
     * @param email 邮箱地址
     * @throws ManagedDeviceException 验证失败时抛出异常
     */
    public static void validateEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return; // 邮箱可以为空
        }
        
        // 防XSS和SQL注入检查
        validateAgainstXSS(email, "邮箱地址");
        validateAgainstSQLInjection(email, "邮箱地址");
        
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw ManagedDeviceException.validationFailed("email", email, "邮箱地址格式不正确");
        }
    }
    
    /**
     * 验证设备型号
     * 
     * @param model 设备型号
     * @throws ManagedDeviceException 验证失败时抛出异常
     */
    public static void validateDeviceModel(String model) {
        if (!StringUtils.hasText(model)) {
            throw ManagedDeviceException.validationFailed("model", model, "设备型号不能为空");
        }
        
        // 防XSS和SQL注入检查
        validateAgainstXSS(model, "设备型号");
        validateAgainstSQLInjection(model, "设备型号");
        
        if (model.trim().length() < 2 || model.trim().length() > 50) {
            throw ManagedDeviceException.validationFailed("model", model, "设备型号长度应在2-50字符之间");
        }
    }
    
    /**
     * 验证设备名称
     * 
     * @param deviceName 设备名称
     * @throws ManagedDeviceException 验证失败时抛出异常
     */
    public static void validateDeviceName(String deviceName) {
        if (!StringUtils.hasText(deviceName)) {
            throw ManagedDeviceException.validationFailed("deviceName", deviceName, "设备名称不能为空");
        }
        
        // 防XSS和SQL注入检查
        validateAgainstXSS(deviceName, "设备名称");
        validateAgainstSQLInjection(deviceName, "设备名称");
        
        if (deviceName.trim().length() < 2 || deviceName.trim().length() > 100) {
            throw ManagedDeviceException.validationFailed("deviceName", deviceName, "设备名称长度应在2-100字符之间");
        }
    }
    
    /**
     * 验证地址信息
     * 
     * @param address 地址信息
     * @throws ManagedDeviceException 验证失败时抛出异常
     */
    public static void validateAddress(String address) {
        if (!StringUtils.hasText(address)) {
            return; // 地址可以为空
        }
        
        // 防XSS和SQL注入检查
        validateAgainstXSS(address, "地址信息");
        validateAgainstSQLInjection(address, "地址信息");
        
        if (address.trim().length() > 200) {
            throw ManagedDeviceException.validationFailed("address", address, "地址信息长度不能超过200字符");
        }
    }
    
    /**
     * 验证备注信息
     * 
     * @param notes 备注信息
     * @throws ManagedDeviceException 验证失败时抛出异常
     */
    public static void validateNotes(String notes) {
        if (!StringUtils.hasText(notes)) {
            return; // 备注可以为空
        }
        
        // 防XSS和SQL注入检查
        validateAgainstXSS(notes, "备注信息");
        validateAgainstSQLInjection(notes, "备注信息");
        
        if (notes.trim().length() > 500) {
            throw ManagedDeviceException.validationFailed("notes", notes, "备注信息长度不能超过500字符");
        }
    }
    
    /**
     * 验证ID值
     * 
     * @param id ID值
     * @param fieldName 字段名称
     * @throws ManagedDeviceException 验证失败时抛出异常
     */
    public static void validateId(Long id, String fieldName) {
        if (id == null || id <= 0) {
            throw ManagedDeviceException.validationFailed(fieldName, id, fieldName + "必须是正整数");
        }
    }
    
    /**
     * 验证分页参数
     * 
     * @param page 页码
     * @param size 页大小
     * @throws ManagedDeviceException 验证失败时抛出异常
     */
    public static void validatePagination(Integer page, Integer size) {
        if (page != null && page < 0) {
            throw ManagedDeviceException.validationFailed("page", page, "页码不能为负数");
        }
        
        if (size != null && (size <= 0 || size > 1000)) {
            throw ManagedDeviceException.validationFailed("size", size, "页大小必须在1-1000之间");
        }
    }
    
    /**
     * 验证字符串是否包含XSS攻击代码
     * 
     * @param input 输入字符串
     * @param fieldName 字段名称
     * @throws ManagedDeviceException 包含XSS攻击代码时抛出异常
     */
    public static void validateAgainstXSS(String input, String fieldName) {
        if (!StringUtils.hasText(input)) {
            return;
        }
        
        for (Pattern pattern : XSS_PATTERNS) {
            if (pattern.matcher(input).find()) {
                throw ManagedDeviceException.validationFailed(fieldName, input, 
                    fieldName + "包含不安全的内容，可能存在XSS攻击风险");
            }
        }
    }
    
    /**
     * 验证字符串是否包含SQL注入攻击代码
     * 
     * @param input 输入字符串
     * @param fieldName 字段名称
     * @throws ManagedDeviceException 包含SQL注入攻击代码时抛出异常
     */
    public static void validateAgainstSQLInjection(String input, String fieldName) {
        if (!StringUtils.hasText(input)) {
            return;
        }
        
        for (Pattern pattern : SQL_INJECTION_PATTERNS) {
            if (pattern.matcher(input).find()) {
                throw ManagedDeviceException.validationFailed(fieldName, input, 
                    fieldName + "包含不安全的内容，可能存在SQL注入攻击风险");
            }
        }
    }
    
    /**
     * 批量验证设备数据
     * 
     * @param deviceData 设备数据Map
     * @return 验证结果Map，key为字段名，value为验证错误信息
     */
    public static Map<String, String> batchValidateDeviceData(Map<String, Object> deviceData) {
        Map<String, String> errors = new HashMap<>();
        
        try {
            if (deviceData.containsKey("serialNumber")) {
                validateSerialNumber((String) deviceData.get("serialNumber"));
            }
        } catch (ManagedDeviceException e) {
            errors.put("serialNumber", e.getMessage());
        }
        
        try {
            if (deviceData.containsKey("firmwareVersion")) {
                validateFirmwareVersion((String) deviceData.get("firmwareVersion"));
            }
        } catch (ManagedDeviceException e) {
            errors.put("firmwareVersion", e.getMessage());
        }
        
        try {
            if (deviceData.containsKey("customerName")) {
                validateCustomerName((String) deviceData.get("customerName"));
            }
        } catch (ManagedDeviceException e) {
            errors.put("customerName", e.getMessage());
        }
        
        try {
            if (deviceData.containsKey("phone")) {
                validatePhone((String) deviceData.get("phone"));
            }
        } catch (ManagedDeviceException e) {
            errors.put("phone", e.getMessage());
        }
        
        try {
            if (deviceData.containsKey("email")) {
                validateEmail((String) deviceData.get("email"));
            }
        } catch (ManagedDeviceException e) {
            errors.put("email", e.getMessage());
        }
        
        try {
            if (deviceData.containsKey("model")) {
                validateDeviceModel((String) deviceData.get("model"));
            }
        } catch (ManagedDeviceException e) {
            errors.put("model", e.getMessage());
        }
        
        try {
            if (deviceData.containsKey("deviceName")) {
                validateDeviceName((String) deviceData.get("deviceName"));
            }
        } catch (ManagedDeviceException e) {
            errors.put("deviceName", e.getMessage());
        }
        
        try {
            if (deviceData.containsKey("address")) {
                validateAddress((String) deviceData.get("address"));
            }
        } catch (ManagedDeviceException e) {
            errors.put("address", e.getMessage());
        }
        
        try {
            if (deviceData.containsKey("notes")) {
                validateNotes((String) deviceData.get("notes"));
            }
        } catch (ManagedDeviceException e) {
            errors.put("notes", e.getMessage());
        }
        
        return errors;
    }
    
    /**
     * 清理输入字符串，移除潜在的危险字符
     * 
     * @param input 输入字符串
     * @return 清理后的字符串
     */
    public static String sanitizeInput(String input) {
        if (!StringUtils.hasText(input)) {
            return input;
        }
        
        String sanitized = input.trim();
        
        // 移除HTML标签
        sanitized = sanitized.replaceAll("<[^>]+>", "");
        
        // 移除JavaScript相关内容
        sanitized = sanitized.replaceAll("(?i)javascript:", "");
        sanitized = sanitized.replaceAll("(?i)vbscript:", "");
        
        // 移除事件处理器
        sanitized = sanitized.replaceAll("(?i)on\\w+\\s*=", "");
        
        // 移除SQL关键字（在非SQL上下文中）
        sanitized = sanitized.replaceAll("(?i)\\b(union|select|insert|delete|update|drop|create|alter|exec|execute)\\b", "");
        
        return sanitized;
    }
    
    /**
     * 验证JSON字符串格式
     * 
     * @param jsonString JSON字符串
     * @param fieldName 字段名称
     * @throws ManagedDeviceException JSON格式不正确时抛出异常
     */
    public static void validateJsonFormat(String jsonString, String fieldName) {
        if (!StringUtils.hasText(jsonString)) {
            return;
        }
        
        // 防XSS和SQL注入检查
        validateAgainstXSS(jsonString, fieldName);
        validateAgainstSQLInjection(jsonString, fieldName);
        
        try {
            // 简单的JSON格式验证
            jsonString = jsonString.trim();
            if (!(jsonString.startsWith("{") && jsonString.endsWith("}")) && 
                !(jsonString.startsWith("[") && jsonString.endsWith("]"))) {
                throw new IllegalArgumentException("Invalid JSON format");
            }
        } catch (Exception e) {
            throw ManagedDeviceException.validationFailed(fieldName, jsonString, 
                fieldName + "的JSON格式不正确: " + e.getMessage());
        }
    }
}