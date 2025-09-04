package com.yxrobot.util;

import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 数据保护工具类
 * 提供PII（个人身份信息）数据保护功能
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
public class DataProtectionUtils {
    
    // PII数据模式
    private static final Pattern PHONE_PATTERN = Pattern.compile("\\b1[3-9]\\d{9}\\b");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b");
    private static final Pattern ID_CARD_PATTERN = Pattern.compile("\\b\\d{15}|\\d{18}\\b");
    private static final Pattern BANK_CARD_PATTERN = Pattern.compile("\\b\\d{16,19}\\b");
    private static final Pattern IP_ADDRESS_PATTERN = Pattern.compile("\\b(?:[0-9]{1,3}\\.){3}[0-9]{1,3}\\b");
    
    // 敏感关键词模式
    private static final Pattern[] SENSITIVE_PATTERNS = {
        Pattern.compile("(?i)\\b(password|pwd|secret|token|key)\\b"),
        Pattern.compile("(?i)\\b(身份证|护照|驾驶证)\\b"),
        Pattern.compile("(?i)\\b(银行卡|信用卡|储蓄卡)\\b"),
        Pattern.compile("(?i)\\b(社保|医保|公积金)\\b")
    };
    
    /**
     * 脱敏手机号码
     * 
     * @param phone 原始手机号
     * @return 脱敏后的手机号
     */
    public static String maskPhone(String phone) {
        if (!StringUtils.hasText(phone)) {
            return phone;
        }
        
        if (PHONE_PATTERN.matcher(phone).matches()) {
            return phone.substring(0, 3) + "****" + phone.substring(7);
        }
        
        return phone;
    }
    
    /**
     * 脱敏邮箱地址
     * 
     * @param email 原始邮箱
     * @return 脱敏后的邮箱
     */
    public static String maskEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return email;
        }
        
        if (EMAIL_PATTERN.matcher(email).matches()) {
            int atIndex = email.indexOf('@');
            if (atIndex > 0) {
                String username = email.substring(0, atIndex);
                String domain = email.substring(atIndex);
                
                if (username.length() <= 2) {
                    return "*" + username.substring(1) + domain;
                } else {
                    return username.substring(0, 2) + "***" + domain;
                }
            }
        }
        
        return email;
    }
    
    /**
     * 脱敏身份证号
     * 
     * @param idCard 原始身份证号
     * @return 脱敏后的身份证号
     */
    public static String maskIdCard(String idCard) {
        if (!StringUtils.hasText(idCard)) {
            return idCard;
        }
        
        if (ID_CARD_PATTERN.matcher(idCard).matches()) {
            if (idCard.length() == 15) {
                return idCard.substring(0, 6) + "*****" + idCard.substring(11);
            } else if (idCard.length() == 18) {
                return idCard.substring(0, 6) + "********" + idCard.substring(14);
            }
        }
        
        return idCard;
    }
    
    /**
     * 脱敏银行卡号
     * 
     * @param bankCard 原始银行卡号
     * @return 脱敏后的银行卡号
     */
    public static String maskBankCard(String bankCard) {
        if (!StringUtils.hasText(bankCard)) {
            return bankCard;
        }
        
        if (BANK_CARD_PATTERN.matcher(bankCard).matches()) {
            return bankCard.substring(0, 4) + " **** **** " + bankCard.substring(bankCard.length() - 4);
        }
        
        return bankCard;
    }
    
    /**
     * 脱敏IP地址
     * 
     * @param ipAddress 原始IP地址
     * @return 脱敏后的IP地址
     */
    public static String maskIpAddress(String ipAddress) {
        if (!StringUtils.hasText(ipAddress)) {
            return ipAddress;
        }
        
        if (IP_ADDRESS_PATTERN.matcher(ipAddress).matches()) {
            String[] parts = ipAddress.split("\\.");
            if (parts.length == 4) {
                return parts[0] + "." + parts[1] + ".***." + parts[3];
            }
        }
        
        return ipAddress;
    }
    
    /**
     * 脱敏姓名
     * 
     * @param name 原始姓名
     * @return 脱敏后的姓名
     */
    public static String maskName(String name) {
        if (!StringUtils.hasText(name)) {
            return name;
        }
        
        String trimmed = name.trim();
        if (trimmed.length() == 1) {
            return "*";
        } else if (trimmed.length() == 2) {
            return trimmed.substring(0, 1) + "*";
        } else {
            return trimmed.substring(0, 1) + "*".repeat(trimmed.length() - 2) + trimmed.substring(trimmed.length() - 1);
        }
    }
    
    /**
     * 脱敏地址信息
     * 
     * @param address 原始地址
     * @return 脱敏后的地址
     */
    public static String maskAddress(String address) {
        if (!StringUtils.hasText(address)) {
            return address;
        }
        
        // 保留省市信息，脱敏详细地址
        String trimmed = address.trim();
        if (trimmed.length() <= 10) {
            return trimmed.substring(0, Math.min(4, trimmed.length())) + "***";
        } else {
            return trimmed.substring(0, 6) + "***" + trimmed.substring(trimmed.length() - 2);
        }
    }
    
    /**
     * 自动脱敏文本中的PII信息
     * 
     * @param text 原始文本
     * @return 脱敏后的文本
     */
    public static String autoMaskPII(String text) {
        if (!StringUtils.hasText(text)) {
            return text;
        }
        
        String masked = text;
        
        // 脱敏手机号
        masked = PHONE_PATTERN.matcher(masked).replaceAll(matchResult -> {
            String phone = matchResult.group();
            return maskPhone(phone);
        });
        
        // 脱敏邮箱
        masked = EMAIL_PATTERN.matcher(masked).replaceAll(matchResult -> {
            String email = matchResult.group();
            return maskEmail(email);
        });
        
        // 脱敏身份证号
        masked = ID_CARD_PATTERN.matcher(masked).replaceAll(matchResult -> {
            String idCard = matchResult.group();
            return maskIdCard(idCard);
        });
        
        // 脱敏银行卡号
        masked = BANK_CARD_PATTERN.matcher(masked).replaceAll(matchResult -> {
            String bankCard = matchResult.group();
            return maskBankCard(bankCard);
        });
        
        // 脱敏IP地址
        masked = IP_ADDRESS_PATTERN.matcher(masked).replaceAll(matchResult -> {
            String ip = matchResult.group();
            return maskIpAddress(ip);
        });
        
        return masked;
    }
    
    /**
     * 检查文本是否包含敏感信息
     * 
     * @param text 待检查的文本
     * @return 是否包含敏感信息
     */
    public static boolean containsSensitiveInfo(String text) {
        if (!StringUtils.hasText(text)) {
            return false;
        }
        
        // 检查PII模式
        if (PHONE_PATTERN.matcher(text).find() ||
            EMAIL_PATTERN.matcher(text).find() ||
            ID_CARD_PATTERN.matcher(text).find() ||
            BANK_CARD_PATTERN.matcher(text).find()) {
            return true;
        }
        
        // 检查敏感关键词
        for (Pattern pattern : SENSITIVE_PATTERNS) {
            if (pattern.matcher(text).find()) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 批量脱敏Map中的数据
     * 
     * @param data 原始数据Map
     * @return 脱敏后的数据Map
     */
    public static Map<String, Object> maskMapData(Map<String, Object> data) {
        if (data == null || data.isEmpty()) {
            return data;
        }
        
        Map<String, Object> maskedData = new HashMap<>();
        
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            
            if (value instanceof String) {
                String stringValue = (String) value;
                
                // 根据字段名判断脱敏方式
                if (isPhoneField(key)) {
                    maskedData.put(key, maskPhone(stringValue));
                } else if (isEmailField(key)) {
                    maskedData.put(key, maskEmail(stringValue));
                } else if (isNameField(key)) {
                    maskedData.put(key, maskName(stringValue));
                } else if (isAddressField(key)) {
                    maskedData.put(key, maskAddress(stringValue));
                } else if (isIdCardField(key)) {
                    maskedData.put(key, maskIdCard(stringValue));
                } else {
                    // 自动检测并脱敏
                    maskedData.put(key, autoMaskPII(stringValue));
                }
            } else {
                maskedData.put(key, value);
            }
        }
        
        return maskedData;
    }
    
    /**
     * 判断是否为手机号字段
     */
    private static boolean isPhoneField(String fieldName) {
        return fieldName != null && (
            fieldName.toLowerCase().contains("phone") ||
            fieldName.toLowerCase().contains("mobile") ||
            fieldName.toLowerCase().contains("tel") ||
            fieldName.contains("电话") ||
            fieldName.contains("手机")
        );
    }
    
    /**
     * 判断是否为邮箱字段
     */
    private static boolean isEmailField(String fieldName) {
        return fieldName != null && (
            fieldName.toLowerCase().contains("email") ||
            fieldName.toLowerCase().contains("mail") ||
            fieldName.contains("邮箱") ||
            fieldName.contains("邮件")
        );
    }
    
    /**
     * 判断是否为姓名字段
     */
    private static boolean isNameField(String fieldName) {
        return fieldName != null && (
            fieldName.toLowerCase().contains("name") ||
            fieldName.contains("姓名") ||
            fieldName.contains("客户名") ||
            fieldName.contains("用户名")
        );
    }
    
    /**
     * 判断是否为地址字段
     */
    private static boolean isAddressField(String fieldName) {
        return fieldName != null && (
            fieldName.toLowerCase().contains("address") ||
            fieldName.contains("地址") ||
            fieldName.contains("住址")
        );
    }
    
    /**
     * 判断是否为身份证字段
     */
    private static boolean isIdCardField(String fieldName) {
        return fieldName != null && (
            fieldName.toLowerCase().contains("idcard") ||
            fieldName.toLowerCase().contains("id_card") ||
            fieldName.contains("身份证") ||
            fieldName.contains("证件号")
        );
    }
    
    /**
     * 生成通用占位符
     * 
     * @param dataType 数据类型
     * @return 占位符
     */
    public static String generatePlaceholder(String dataType) {
        switch (dataType.toLowerCase()) {
            case "phone":
            case "mobile":
                return "[手机号]";
            case "email":
                return "[邮箱地址]";
            case "name":
                return "[姓名]";
            case "address":
                return "[地址信息]";
            case "idcard":
                return "[身份证号]";
            case "bankcard":
                return "[银行卡号]";
            case "ip":
                return "[IP地址]";
            default:
                return "[敏感信息]";
        }
    }
}