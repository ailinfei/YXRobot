package com.yxrobot.util;

import com.yxrobot.config.ValidationConfig;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 验证工具类
 * 提供常用的数据验证方法
 */
@Component
public class ValidationUtils {
    
    private final ValidationConfig validationConfig;
    
    // 预编译的正则表达式模式
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern ORDER_NUMBER_PATTERN = Pattern.compile("^[A-Z]{3}\\d{10}$");
    
    public ValidationUtils(ValidationConfig validationConfig) {
        this.validationConfig = validationConfig;
    }
    
    /**
     * 检查字符串是否为空或null
     * 
     * @param value 要检查的字符串
     * @return true如果为空或null
     */
    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
    
    /**
     * 检查字符串是否不为空
     * 
     * @param value 要检查的字符串
     * @return true如果不为空
     */
    public static boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }
    
    /**
     * 检查集合是否为空或null
     * 
     * @param collection 要检查的集合
     * @return true如果为空或null
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
    
    /**
     * 检查集合是否不为空
     * 
     * @param collection 要检查的集合
     * @return true如果不为空
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }
    
    /**
     * 验证必填字段
     * 
     * @param value 字段值
     * @param fieldName 字段名
     * @return 验证结果消息，null表示验证通过
     */
    public String validateRequired(String value, String fieldName) {
        if (isEmpty(value)) {
            return String.format("字段 %s 是必填项", fieldName);
        }
        return null;
    }
    
    /**
     * 验证字符串长度
     * 
     * @param value 字符串值
     * @param fieldName 字段名
     * @param minLength 最小长度
     * @param maxLength 最大长度
     * @return 验证结果消息，null表示验证通过
     */
    public String validateLength(String value, String fieldName, int minLength, int maxLength) {
        if (value == null) {
            return null; // 长度验证不检查null，由必填验证处理
        }
        
        int length = value.length();
        if (length < minLength || length > maxLength) {
            return String.format("字段 %s 长度应在 %d 到 %d 之间，当前长度: %d", 
                fieldName, minLength, maxLength, length);
        }
        return null;
    }
    
    /**
     * 验证手机号格式
     * 
     * @param phone 手机号
     * @return 验证结果消息，null表示验证通过
     */
    public String validatePhone(String phone) {
        if (isEmpty(phone)) {
            return null; // 空值由必填验证处理
        }
        
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            return "手机号格式不正确，应为11位数字且以1开头";
        }
        return null;
    }
    
    /**
     * 验证邮箱格式
     * 
     * @param email 邮箱地址
     * @return 验证结果消息，null表示验证通过
     */
    public String validateEmail(String email) {
        if (isEmpty(email)) {
            return null; // 空值由必填验证处理
        }
        
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return "邮箱格式不正确";
        }
        return null;
    }
    
    /**
     * 验证订单号格式
     * 
     * @param orderNumber 订单号
     * @return 验证结果消息，null表示验证通过
     */
    public String validateOrderNumber(String orderNumber) {
        if (isEmpty(orderNumber)) {
            return null; // 空值由必填验证处理
        }
        
        if (!ORDER_NUMBER_PATTERN.matcher(orderNumber).matches()) {
            return "订单号格式不正确，应为3位大写字母+10位数字";
        }
        
        // 检查前缀是否允许
        String prefix = orderNumber.substring(0, 3);
        List<String> allowedPrefixes = validationConfig.getOrderNumber().getAllowedPrefixes();
        if (!allowedPrefixes.contains(prefix)) {
            return String.format("订单号前缀 %s 不被允许，允许的前缀: %s", prefix, allowedPrefixes);
        }
        
        return null;
    }
    
    /**
     * 验证金额
     * 
     * @param amount 金额
     * @param fieldName 字段名
     * @return 验证结果消息，null表示验证通过
     */
    public String validateAmount(BigDecimal amount, String fieldName) {
        if (amount == null) {
            return String.format("字段 %s 不能为空", fieldName);
        }
        
        ValidationConfig.AmountConfig config = validationConfig.getAmount();
        
        // 检查是否允许负数
        if (!config.isAllowNegative() && amount.compareTo(BigDecimal.ZERO) < 0) {
            return String.format("字段 %s 不能为负数", fieldName);
        }
        
        // 检查是否允许零
        if (!config.isAllowZero() && amount.compareTo(BigDecimal.ZERO) == 0) {
            return String.format("字段 %s 不能为零", fieldName);
        }
        
        // 检查金额范围
        if (amount.compareTo(config.getMinAmount()) < 0) {
            return String.format("字段 %s 不能小于 %s", fieldName, config.getMinAmount());
        }
        
        if (amount.compareTo(config.getMaxAmount()) > 0) {
            return String.format("字段 %s 不能大于 %s", fieldName, config.getMaxAmount());
        }
        
        // 检查小数位数
        int scale = amount.scale();
        if (scale > config.getDecimalPlaces()) {
            return String.format("字段 %s 小数位数不能超过 %d 位", fieldName, config.getDecimalPlaces());
        }
        
        return null;
    }
    
    /**
     * 验证数量
     * 
     * @param quantity 数量
     * @param fieldName 字段名
     * @return 验证结果消息，null表示验证通过
     */
    public String validateQuantity(Integer quantity, String fieldName) {
        if (quantity == null) {
            return String.format("字段 %s 不能为空", fieldName);
        }
        
        ValidationConfig.ProductConfig config = validationConfig.getProduct();
        
        if (quantity < config.getMinQuantity()) {
            return String.format("字段 %s 不能小于 %d", fieldName, config.getMinQuantity());
        }
        
        if (quantity > config.getMaxQuantity()) {
            return String.format("字段 %s 不能大于 %d", fieldName, config.getMaxQuantity());
        }
        
        return null;
    }
    
    /**
     * 验证日期范围
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param fieldName 字段名前缀
     * @return 验证结果消息，null表示验证通过
     */
    public String validateDateRange(LocalDate startDate, LocalDate endDate, String fieldName) {
        if (startDate == null || endDate == null) {
            return String.format("%s 的开始日期和结束日期都不能为空", fieldName);
        }
        
        if (startDate.isAfter(endDate)) {
            return String.format("%s 的开始日期不能晚于结束日期", fieldName);
        }
        
        return null;
    }
    
    /**
     * 验证租赁日期
     * 
     * @param startDate 租赁开始日期
     * @param endDate 租赁结束日期
     * @return 验证结果消息，null表示验证通过
     */
    public String validateRentalDates(LocalDate startDate, LocalDate endDate) {
        String dateRangeError = validateDateRange(startDate, endDate, "租赁期间");
        if (dateRangeError != null) {
            return dateRangeError;
        }
        
        ValidationConfig.RentalConfig config = validationConfig.getRental();
        
        // 检查租赁天数
        long rentalDays = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        if (rentalDays < config.getMinRentalDays()) {
            return String.format("租赁天数不能少于 %d 天", config.getMinRentalDays());
        }
        
        if (rentalDays > config.getMaxRentalDays()) {
            return String.format("租赁天数不能超过 %d 天", config.getMaxRentalDays());
        }
        
        // 检查提前预订天数
        LocalDate today = LocalDate.now();
        long advanceDays = java.time.temporal.ChronoUnit.DAYS.between(today, startDate);
        if (advanceDays > config.getAdvanceBookingDays()) {
            return String.format("租赁开始日期不能超过今天后 %d 天", config.getAdvanceBookingDays());
        }
        
        return null;
    }
    
    /**
     * 验证枚举值
     * 
     * @param value 值
     * @param allowedValues 允许的值列表
     * @param fieldName 字段名
     * @return 验证结果消息，null表示验证通过
     */
    public String validateEnum(String value, List<String> allowedValues, String fieldName) {
        if (isEmpty(value)) {
            return null; // 空值由必填验证处理
        }
        
        if (!allowedValues.contains(value)) {
            return String.format("字段 %s 值必须是以下之一: %s，当前值: %s", 
                fieldName, allowedValues, value);
        }
        
        return null;
    }
    
    /**
     * 验证订单类型
     * 
     * @param orderType 订单类型
     * @return 验证结果消息，null表示验证通过
     */
    public String validateOrderType(String orderType) {
        List<String> allowedTypes = validationConfig.getBusinessRule().getAllowedOrderTypes();
        return validateEnum(orderType, allowedTypes, "订单类型");
    }
    
    /**
     * 验证订单状态
     * 
     * @param orderStatus 订单状态
     * @return 验证结果消息，null表示验证通过
     */
    public String validateOrderStatus(String orderStatus) {
        List<String> allowedStatuses = validationConfig.getBusinessRule().getAllowedOrderStatuses();
        return validateEnum(orderStatus, allowedStatuses, "订单状态");
    }
    
    /**
     * 验证支付方式
     * 
     * @param paymentMethod 支付方式
     * @return 验证结果消息，null表示验证通过
     */
    public String validatePaymentMethod(String paymentMethod) {
        List<String> allowedMethods = validationConfig.getBusinessRule().getAllowedPaymentMethods();
        return validateEnum(paymentMethod, allowedMethods, "支付方式");
    }
    
    /**
     * 验证状态流转
     * 
     * @param currentStatus 当前状态
     * @param targetStatus 目标状态
     * @return 验证结果消息，null表示验证通过
     */
    public String validateStatusTransition(String currentStatus, String targetStatus) {
        if (isEmpty(currentStatus) || isEmpty(targetStatus)) {
            return "当前状态和目标状态都不能为空";
        }
        
        var transitions = validationConfig.getBusinessRule().getStatusTransitions();
        List<String> allowedTargets = transitions.get(currentStatus);
        
        if (allowedTargets == null) {
            return String.format("未知的当前状态: %s", currentStatus);
        }
        
        if (!allowedTargets.contains(targetStatus)) {
            return String.format("不允许从状态 %s 变更为 %s，允许的目标状态: %s", 
                currentStatus, targetStatus, allowedTargets);
        }
        
        return null;
    }
    
    /**
     * 验证ID是否有效
     * 
     * @param id ID值
     * @param fieldName 字段名
     * @return 验证结果消息，null表示验证通过
     */
    public String validateId(Long id, String fieldName) {
        if (id == null) {
            return String.format("字段 %s 不能为空", fieldName);
        }
        
        if (id <= 0) {
            return String.format("字段 %s 必须是正整数", fieldName);
        }
        
        return null;
    }
    
    /**
     * 验证时间戳是否合理
     * 
     * @param dateTime 时间戳
     * @param fieldName 字段名
     * @return 验证结果消息，null表示验证通过
     */
    public String validateDateTime(LocalDateTime dateTime, String fieldName) {
        if (dateTime == null) {
            return null; // 时间戳可以为空
        }
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneYearAgo = now.minusYears(1);
        LocalDateTime oneYearLater = now.plusYears(1);
        
        if (dateTime.isBefore(oneYearAgo)) {
            return String.format("字段 %s 时间不能早于一年前", fieldName);
        }
        
        if (dateTime.isAfter(oneYearLater)) {
            return String.format("字段 %s 时间不能晚于一年后", fieldName);
        }
        
        return null;
    }
}