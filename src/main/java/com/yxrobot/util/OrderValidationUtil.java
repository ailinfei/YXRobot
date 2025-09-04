package com.yxrobot.util;

import com.yxrobot.exception.OrderException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

/**
 * 订单数据验证工具类
 * 提供常用的数据验证方法
 */
public class OrderValidationUtil {
    
    // 订单号格式正则表达式：3位字母+10位数字
    private static final Pattern ORDER_NUMBER_PATTERN = Pattern.compile("^[A-Z]{3}\\d{10}$");
    
    // 电话号码格式正则表达式
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    
    // 邮箱格式正则表达式
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    // 身份证号码格式正则表达式
    private static final Pattern ID_CARD_PATTERN = Pattern.compile("^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$");
    
    /**
     * 验证订单号格式
     */
    public static boolean isValidOrderNumber(String orderNumber) {
        return orderNumber != null && ORDER_NUMBER_PATTERN.matcher(orderNumber).matches();
    }
    
    /**
     * 验证并抛出订单号格式异常
     */
    public static void validateOrderNumber(String orderNumber) {
        if (orderNumber == null || orderNumber.trim().isEmpty()) {
            throw OrderException.validationFailed("orderNumber", "订单号不能为空");
        }
        if (!isValidOrderNumber(orderNumber)) {
            throw OrderException.validationFailed("orderNumber", "订单号格式不正确，应为3位字母+10位数字");
        }
    }
    
    /**
     * 验证电话号码格式
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }
    
    /**
     * 验证并抛出电话号码格式异常
     */
    public static void validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw OrderException.validationFailed("phone", "电话号码不能为空");
        }
        if (!isValidPhone(phone)) {
            throw OrderException.validationFailed("phone", "电话号码格式不正确");
        }
    }
    
    /**
     * 验证邮箱格式
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * 验证并抛出邮箱格式异常
     */
    public static void validateEmail(String email) {
        if (email != null && !email.trim().isEmpty() && !isValidEmail(email)) {
            throw OrderException.validationFailed("email", "邮箱格式不正确");
        }
    }
    
    /**
     * 验证身份证号码格式
     */
    public static boolean isValidIdCard(String idCard) {
        return idCard != null && ID_CARD_PATTERN.matcher(idCard).matches();
    }
    
    /**
     * 验证金额
     */
    public static boolean isValidAmount(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * 验证并抛出金额异常
     */
    public static void validateAmount(BigDecimal amount, String fieldName) {
        if (amount == null) {
            throw OrderException.validationFailed(fieldName, fieldName + "不能为空");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw OrderException.validationFailed(fieldName, fieldName + "必须大于0");
        }
    }
    
    /**
     * 验证数量
     */
    public static boolean isValidQuantity(Integer quantity) {
        return quantity != null && quantity > 0;
    }
    
    /**
     * 验证并抛出数量异常
     */
    public static void validateQuantity(Integer quantity, String fieldName) {
        if (quantity == null) {
            throw OrderException.validationFailed(fieldName, fieldName + "不能为空");
        }
        if (quantity <= 0) {
            throw OrderException.validationFailed(fieldName, fieldName + "必须大于0");
        }
    }
    
    /**
     * 验证字符串长度
     */
    public static boolean isValidLength(String str, int minLength, int maxLength) {
        if (str == null) return false;
        int length = str.trim().length();
        return length >= minLength && length <= maxLength;
    }
    
    /**
     * 验证并抛出字符串长度异常
     */
    public static void validateLength(String str, String fieldName, int minLength, int maxLength) {
        if (str == null || str.trim().isEmpty()) {
            throw OrderException.validationFailed(fieldName, fieldName + "不能为空");
        }
        int length = str.trim().length();
        if (length < minLength) {
            throw OrderException.validationFailed(fieldName, 
                fieldName + "长度不能少于" + minLength + "个字符");
        }
        if (length > maxLength) {
            throw OrderException.validationFailed(fieldName, 
                fieldName + "长度不能超过" + maxLength + "个字符");
        }
    }
    
    /**
     * 验证必填字段
     */
    public static void validateRequired(Object value, String fieldName) {
        if (value == null) {
            throw OrderException.validationFailed(fieldName, fieldName + "不能为空");
        }
        if (value instanceof String && ((String) value).trim().isEmpty()) {
            throw OrderException.validationFailed(fieldName, fieldName + "不能为空");
        }
    }
    
    /**
     * 验证ID
     */
    public static void validateId(Long id, String fieldName) {
        if (id == null || id <= 0) {
            throw OrderException.validationFailed(fieldName, fieldName + "无效");
        }
    }
    
    /**
     * 验证日期范围
     */
    public static void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            throw OrderException.validationFailed("startDate", "开始日期不能为空");
        }
        if (endDate == null) {
            throw OrderException.validationFailed("endDate", "结束日期不能为空");
        }
        if (endDate.isBefore(startDate)) {
            throw OrderException.validationFailed("dateRange", "结束日期不能早于开始日期");
        }
    }
    
    /**
     * 验证订单类型
     */
    public static boolean isValidOrderType(String type) {
        return "sales".equals(type) || "rental".equals(type);
    }
    
    /**
     * 验证并抛出订单类型异常
     */
    public static void validateOrderType(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw OrderException.validationFailed("type", "订单类型不能为空");
        }
        if (!isValidOrderType(type)) {
            throw OrderException.validationFailed("type", "订单类型无效，只支持 sales 或 rental");
        }
    }
    
    /**
     * 验证订单状态
     */
    public static boolean isValidOrderStatus(String status) {
        return "pending".equals(status) || "confirmed".equals(status) || 
               "processing".equals(status) || "shipped".equals(status) ||
               "delivered".equals(status) || "completed".equals(status) || 
               "cancelled".equals(status);
    }
    
    /**
     * 验证并抛出订单状态异常
     */
    public static void validateOrderStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw OrderException.validationFailed("status", "订单状态不能为空");
        }
        if (!isValidOrderStatus(status)) {
            throw OrderException.validationFailed("status", "订单状态无效");
        }
    }
    
    /**
     * 验证支付状态
     */
    public static boolean isValidPaymentStatus(String paymentStatus) {
        return "pending".equals(paymentStatus) || "paid".equals(paymentStatus) || 
               "failed".equals(paymentStatus) || "refunded".equals(paymentStatus);
    }
    
    /**
     * 验证并抛出支付状态异常
     */
    public static void validatePaymentStatus(String paymentStatus) {
        if (paymentStatus != null && !paymentStatus.trim().isEmpty() && 
            !isValidPaymentStatus(paymentStatus)) {
            throw OrderException.validationFailed("paymentStatus", "支付状态无效");
        }
    }
    
    /**
     * 验证货币类型
     */
    public static boolean isValidCurrency(String currency) {
        return "CNY".equals(currency) || "USD".equals(currency) || "EUR".equals(currency);
    }
    
    /**
     * 验证并抛出货币类型异常
     */
    public static void validateCurrency(String currency) {
        if (currency != null && !currency.trim().isEmpty() && !isValidCurrency(currency)) {
            throw OrderException.validationFailed("currency", "货币类型无效");
        }
    }
    
    /**
     * 验证金额计算
     */
    public static void validateAmountCalculation(BigDecimal subtotal, BigDecimal shippingFee, 
                                                BigDecimal discount, BigDecimal totalAmount) {
        if (totalAmount == null) {
            throw OrderException.validationFailed("totalAmount", "订单总金额不能为空");
        }
        
        BigDecimal expectedTotal = BigDecimal.ZERO;
        
        if (subtotal != null) {
            expectedTotal = expectedTotal.add(subtotal);
        }
        
        if (shippingFee != null) {
            expectedTotal = expectedTotal.add(shippingFee);
        }
        
        if (discount != null) {
            expectedTotal = expectedTotal.subtract(discount);
        }
        
        if (totalAmount.compareTo(expectedTotal) != 0) {
            throw OrderException.amountCalculationError(
                String.format("订单总金额计算错误，应为：%s，实际：%s", expectedTotal, totalAmount));
        }
    }
    
    /**
     * 验证状态流转
     */
    public static void validateStatusTransition(String currentStatus, String targetStatus) {
        if (currentStatus == null || currentStatus.trim().isEmpty()) {
            throw OrderException.validationFailed("currentStatus", "当前状态不能为空");
        }
        
        if (targetStatus == null || targetStatus.trim().isEmpty()) {
            throw OrderException.validationFailed("targetStatus", "目标状态不能为空");
        }
        
        if (!isValidStatusTransition(currentStatus, targetStatus)) {
            throw OrderException.invalidOrderStatus(currentStatus, targetStatus);
        }
    }
    
    /**
     * 检查状态流转是否合法
     */
    private static boolean isValidStatusTransition(String currentStatus, String targetStatus) {
        switch (currentStatus) {
            case "pending":
                return "confirmed".equals(targetStatus) || "cancelled".equals(targetStatus);
            case "confirmed":
                return "processing".equals(targetStatus) || "cancelled".equals(targetStatus);
            case "processing":
                return "shipped".equals(targetStatus);
            case "shipped":
                return "delivered".equals(targetStatus);
            case "delivered":
                return "completed".equals(targetStatus);
            case "completed":
            case "cancelled":
                return false; // 终态，不允许再变更
            default:
                return false;
        }
    }
}