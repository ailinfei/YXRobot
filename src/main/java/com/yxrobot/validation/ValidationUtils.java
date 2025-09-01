package com.yxrobot.validation;

import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 验证工具类
 * 提供通用的验证方法
 */
public class ValidationUtils {
    
    // 常用正则表达式
    private static final Pattern CHINESE_PATTERN = Pattern.compile("[\\u4e00-\\u9fa5]");
    private static final Pattern ENGLISH_PATTERN = Pattern.compile("[a-zA-Z]");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]");
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s");
    
    // ID身份证正则表达式
    private static final Pattern ID_CARD_PATTERN = Pattern.compile("^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$");
    
    // 银行卡号正则表达式
    private static final Pattern BANK_CARD_PATTERN = Pattern.compile("^[1-9]\\d{12,19}$");
    
    // URL正则表达式
    private static final Pattern URL_PATTERN = Pattern.compile("^(https?|ftp)://[^\\s/$.?#].[^\\s]*$");
    
    /**
     * 检查字符串是否为空或仅包含空白字符
     */
    public static boolean isEmpty(String str) {
        return !StringUtils.hasText(str);
    }
    
    /**
     * 检查字符串长度是否在指定范围内
     */
    public static boolean isLengthInRange(String str, int minLength, int maxLength) {
        if (str == null) {
            return minLength == 0;
        }
        int length = str.length();
        return length >= minLength && length <= maxLength;
    }
    
    /**
     * 检查字符串是否只包含中文字符
     */
    public static boolean isChineseOnly(String str) {
        if (isEmpty(str)) {
            return false;
        }
        return str.chars().allMatch(c -> CHINESE_PATTERN.matcher(String.valueOf((char) c)).matches());
    }
    
    /**
     * 检查字符串是否只包含英文字符
     */
    public static boolean isEnglishOnly(String str) {
        if (isEmpty(str)) {
            return false;
        }
        return str.chars().allMatch(c -> ENGLISH_PATTERN.matcher(String.valueOf((char) c)).matches());
    }
    
    /**
     * 检查字符串是否只包含数字
     */
    public static boolean isNumericOnly(String str) {
        if (isEmpty(str)) {
            return false;
        }
        return str.chars().allMatch(c -> NUMBER_PATTERN.matcher(String.valueOf((char) c)).matches());
    }
    
    /**
     * 检查字符串是否包含中文字符
     */
    public static boolean containsChinese(String str) {
        if (isEmpty(str)) {
            return false;
        }
        return CHINESE_PATTERN.matcher(str).find();
    }
    
    /**
     * 检查字符串是否包含英文字符
     */
    public static boolean containsEnglish(String str) {
        if (isEmpty(str)) {
            return false;
        }
        return ENGLISH_PATTERN.matcher(str).find();
    }
    
    /**
     * 检查字符串是否包含数字
     */
    public static boolean containsNumber(String str) {
        if (isEmpty(str)) {
            return false;
        }
        return NUMBER_PATTERN.matcher(str).find();
    }
    
    /**
     * 检查字符串是否包含特殊字符
     */
    public static boolean containsSpecialChar(String str) {
        if (isEmpty(str)) {
            return false;
        }
        return SPECIAL_CHAR_PATTERN.matcher(str).find();
    }
    
    /**
     * 检查字符串是否包含空白字符
     */
    public static boolean containsWhitespace(String str) {
        if (isEmpty(str)) {
            return false;
        }
        return WHITESPACE_PATTERN.matcher(str).find();
    }
    
    /**
     * 检查字符串是否为有效的身份证号
     */
    public static boolean isValidIdCard(String idCard) {
        if (isEmpty(idCard)) {
            return false;
        }
        
        if (!ID_CARD_PATTERN.matcher(idCard).matches()) {
            return false;
        }
        
        // 验证校验码
        return validateIdCardChecksum(idCard);
    }
    
    /**
     * 验证身份证校验码
     */
    private static boolean validateIdCardChecksum(String idCard) {
        if (idCard.length() != 18) {
            return false;
        }
        
        int[] weights = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        char[] checksums = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
        
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += Character.getNumericValue(idCard.charAt(i)) * weights[i];
        }
        
        char expectedChecksum = checksums[sum % 11];
        char actualChecksum = Character.toUpperCase(idCard.charAt(17));
        
        return expectedChecksum == actualChecksum;
    }
    
    /**
     * 检查字符串是否为有效的银行卡号
     */
    public static boolean isValidBankCard(String bankCard) {
        if (isEmpty(bankCard)) {
            return false;
        }
        
        if (!BANK_CARD_PATTERN.matcher(bankCard).matches()) {
            return false;
        }
        
        // 使用Luhn算法验证
        return validateLuhnChecksum(bankCard);
    }
    
    /**
     * 使用Luhn算法验证银行卡号
     */
    private static boolean validateLuhnChecksum(String cardNumber) {
        int sum = 0;
        boolean alternate = false;
        
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));
            
            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit = (digit % 10) + 1;
                }
            }
            
            sum += digit;
            alternate = !alternate;
        }
        
        return sum % 10 == 0;
    }
    
    /**
     * 检查字符串是否为有效的URL
     */
    public static boolean isValidUrl(String url) {
        if (isEmpty(url)) {
            return false;
        }
        return URL_PATTERN.matcher(url).matches();
    }
    
    /**
     * 检查BigDecimal是否在指定范围内
     */
    public static boolean isInRange(BigDecimal value, BigDecimal min, BigDecimal max) {
        if (value == null) {
            return false;
        }
        return value.compareTo(min) >= 0 && value.compareTo(max) <= 0;
    }
    
    /**
     * 检查整数是否在指定范围内
     */
    public static boolean isInRange(Integer value, int min, int max) {
        if (value == null) {
            return false;
        }
        return value >= min && value <= max;
    }
    
    /**
     * 检查日期是否在指定范围内
     */
    public static boolean isDateInRange(LocalDateTime date, LocalDateTime start, LocalDateTime end) {
        if (date == null) {
            return false;
        }
        return (start == null || !date.isBefore(start)) && (end == null || !date.isAfter(end));
    }
    
    /**
     * 检查列表是否为空
     */
    public static boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }
    
    /**
     * 检查列表大小是否在指定范围内
     */
    public static boolean isSizeInRange(List<?> list, int minSize, int maxSize) {
        if (list == null) {
            return minSize == 0;
        }
        int size = list.size();
        return size >= minSize && size <= maxSize;
    }
    
    /**
     * 清理字符串（移除前后空白字符）
     */
    public static String cleanString(String str) {
        return str == null ? null : str.trim();
    }
    
    /**
     * 标准化电话号码（移除空格、横线等）
     */
    public static String normalizePhoneNumber(String phone) {
        if (isEmpty(phone)) {
            return phone;
        }
        return phone.replaceAll("[\\s-()（）]", "");
    }
    
    /**
     * 标准化邮箱地址（转换为小写）
     */
    public static String normalizeEmail(String email) {
        if (isEmpty(email)) {
            return email;
        }
        return email.trim().toLowerCase();
    }
    
    /**
     * 检查字符串是否包含敏感词
     */
    public static boolean containsSensitiveWords(String text, List<String> sensitiveWords, boolean caseSensitive) {
        if (isEmpty(text) || isEmpty(sensitiveWords)) {
            return false;
        }
        
        String checkText = caseSensitive ? text : text.toLowerCase();
        
        for (String word : sensitiveWords) {
            String checkWord = caseSensitive ? word : word.toLowerCase();
            if (checkText.contains(checkWord)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 检查字符串是否有连续重复字符
     */
    public static boolean hasConsecutiveRepeatingChars(String str, int count) {
        if (isEmpty(str) || count <= 1) {
            return false;
        }
        
        for (int i = 0; i <= str.length() - count; i++) {
            char firstChar = str.charAt(i);
            boolean hasRepeating = true;
            
            for (int j = 1; j < count; j++) {
                if (str.charAt(i + j) != firstChar) {
                    hasRepeating = false;
                    break;
                }
            }
            
            if (hasRepeating) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 检查字符串是否有连续递增字符
     */
    public static boolean hasConsecutiveIncreasingChars(String str, int count) {
        if (isEmpty(str) || count <= 1) {
            return false;
        }
        
        for (int i = 0; i <= str.length() - count; i++) {
            boolean hasIncreasing = true;
            
            for (int j = 1; j < count; j++) {
                if (str.charAt(i + j) != str.charAt(i + j - 1) + 1) {
                    hasIncreasing = false;
                    break;
                }
            }
            
            if (hasIncreasing) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 检查字符串是否有连续递减字符
     */
    public static boolean hasConsecutiveDecreasingChars(String str, int count) {
        if (isEmpty(str) || count <= 1) {
            return false;
        }
        
        for (int i = 0; i <= str.length() - count; i++) {
            boolean hasDecreasing = true;
            
            for (int j = 1; j < count; j++) {
                if (str.charAt(i + j) != str.charAt(i + j - 1) - 1) {
                    hasDecreasing = false;
                    break;
                }
            }
            
            if (hasDecreasing) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 计算字符串的复杂度评分
     */
    public static int calculateComplexityScore(String str) {
        if (isEmpty(str)) {
            return 0;
        }
        
        int score = 0;
        
        // 长度评分
        score += Math.min(str.length() * 2, 20);
        
        // 字符类型评分
        if (containsChinese(str)) score += 10;
        if (containsEnglish(str)) score += 10;
        if (containsNumber(str)) score += 10;
        if (containsSpecialChar(str)) score += 10;
        
        // 复杂度惩罚
        if (hasConsecutiveRepeatingChars(str, 3)) score -= 10;
        if (hasConsecutiveIncreasingChars(str, 3)) score -= 10;
        if (hasConsecutiveDecreasingChars(str, 3)) score -= 10;
        
        return Math.max(0, Math.min(100, score));
    }
    
    /**
     * 掩码敏感信息
     */
    public static String maskSensitiveInfo(String info, int visibleStart, int visibleEnd) {
        if (isEmpty(info)) {
            return info;
        }
        
        int length = info.length();
        if (length <= visibleStart + visibleEnd) {
            return info;
        }
        
        StringBuilder masked = new StringBuilder();
        
        // 显示开头部分
        masked.append(info, 0, visibleStart);
        
        // 添加掩码
        int maskLength = length - visibleStart - visibleEnd;
        for (int i = 0; i < maskLength; i++) {
            masked.append("*");
        }
        
        // 显示结尾部分
        masked.append(info.substring(length - visibleEnd));
        
        return masked.toString();
    }
    
    /**
     * 掩码电话号码
     */
    public static String maskPhoneNumber(String phone) {
        return maskSensitiveInfo(phone, 3, 4);
    }
    
    /**
     * 掩码邮箱地址
     */
    public static String maskEmail(String email) {
        if (isEmpty(email) || !email.contains("@")) {
            return email;
        }
        
        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];
        
        String maskedUsername = maskSensitiveInfo(username, 1, 1);
        return maskedUsername + "@" + domain;
    }
    
    /**
     * 掩码身份证号
     */
    public static String maskIdCard(String idCard) {
        return maskSensitiveInfo(idCard, 6, 4);
    }
}