package com.yxrobot.util;

import com.yxrobot.exception.OrderException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 订单验证工具类测试
 * 测试各种数据验证方法
 */
class OrderValidationUtilTest {
    
    /**
     * 测试有效订单号验证
     */
    @Test
    void testIsValidOrderNumber_Valid() {
        assertTrue(OrderValidationUtil.isValidOrderNumber("ORD1234567890"));
        assertTrue(OrderValidationUtil.isValidOrderNumber("ABC0123456789"));
        assertTrue(OrderValidationUtil.isValidOrderNumber("XYZ9876543210"));
    }
    
    /**
     * 测试无效订单号验证
     */
    @Test
    void testIsValidOrderNumber_Invalid() {
        assertFalse(OrderValidationUtil.isValidOrderNumber(null));
        assertFalse(OrderValidationUtil.isValidOrderNumber(""));
        assertFalse(OrderValidationUtil.isValidOrderNumber("ORD123")); // 太短
        assertFalse(OrderValidationUtil.isValidOrderNumber("ord1234567890")); // 小写字母
        assertFalse(OrderValidationUtil.isValidOrderNumber("1234567890123")); // 没有字母
        assertFalse(OrderValidationUtil.isValidOrderNumber("ABCD1234567890")); // 4位字母
    }
    
    /**
     * 测试订单号验证异常抛出
     */
    @Test
    void testValidateOrderNumber_ThrowsException() {
        assertDoesNotThrow(() -> OrderValidationUtil.validateOrderNumber("ORD1234567890"));
        
        OrderException exception1 = assertThrows(OrderException.class, 
            () -> OrderValidationUtil.validateOrderNumber(null));
        assertEquals("VALIDATION_FAILED", exception1.getErrorCode());
        
        OrderException exception2 = assertThrows(OrderException.class, 
            () -> OrderValidationUtil.validateOrderNumber("INVALID"));
        assertEquals("VALIDATION_FAILED", exception2.getErrorCode());
    }
    
    /**
     * 测试有效电话号码验证
     */
    @Test
    void testIsValidPhone_Valid() {
        assertTrue(OrderValidationUtil.isValidPhone("13800138000"));
        assertTrue(OrderValidationUtil.isValidPhone("15912345678"));
        assertTrue(OrderValidationUtil.isValidPhone("18888888888"));
    }
    
    /**
     * 测试无效电话号码验证
     */
    @Test
    void testIsValidPhone_Invalid() {
        assertFalse(OrderValidationUtil.isValidPhone(null));
        assertFalse(OrderValidationUtil.isValidPhone(""));
        assertFalse(OrderValidationUtil.isValidPhone("12345678901")); // 不是1开头的手机号
        assertFalse(OrderValidationUtil.isValidPhone("1380013800")); // 位数不够
        assertFalse(OrderValidationUtil.isValidPhone("138001380000")); // 位数过多
    }
    
    /**
     * 测试电话号码验证异常抛出
     */
    @Test
    void testValidatePhone_ThrowsException() {
        assertDoesNotThrow(() -> OrderValidationUtil.validatePhone("13800138000"));
        
        OrderException exception = assertThrows(OrderException.class, 
            () -> OrderValidationUtil.validatePhone("invalid"));
        assertEquals("VALIDATION_FAILED", exception.getErrorCode());
    }
    
    /**
     * 测试有效邮箱验证
     */
    @Test
    void testIsValidEmail_Valid() {
        assertTrue(OrderValidationUtil.isValidEmail("test@example.com"));
        assertTrue(OrderValidationUtil.isValidEmail("user.name@domain.co.uk"));
        assertTrue(OrderValidationUtil.isValidEmail("test+tag@example.org"));
    }
    
    /**
     * 测试无效邮箱验证
     */
    @Test
    void testIsValidEmail_Invalid() {
        assertFalse(OrderValidationUtil.isValidEmail(null));
        assertFalse(OrderValidationUtil.isValidEmail(""));
        assertFalse(OrderValidationUtil.isValidEmail("invalid-email"));
        assertFalse(OrderValidationUtil.isValidEmail("@example.com"));
        assertFalse(OrderValidationUtil.isValidEmail("test@"));
    }
    
    /**
     * 测试邮箱验证异常抛出
     */
    @Test
    void testValidateEmail_ThrowsException() {
        assertDoesNotThrow(() -> OrderValidationUtil.validateEmail("test@example.com"));
        assertDoesNotThrow(() -> OrderValidationUtil.validateEmail(null)); // 邮箱可以为空
        assertDoesNotThrow(() -> OrderValidationUtil.validateEmail("")); // 邮箱可以为空
        
        OrderException exception = assertThrows(OrderException.class, 
            () -> OrderValidationUtil.validateEmail("invalid-email"));
        assertEquals("VALIDATION_FAILED", exception.getErrorCode());
    }
    
    /**
     * 测试有效金额验证
     */
    @Test
    void testIsValidAmount_Valid() {
        assertTrue(OrderValidationUtil.isValidAmount(new BigDecimal("100.00")));
        assertTrue(OrderValidationUtil.isValidAmount(new BigDecimal("0.01")));
        assertTrue(OrderValidationUtil.isValidAmount(new BigDecimal("999999.99")));
    }
    
    /**
     * 测试无效金额验证
     */
    @Test
    void testIsValidAmount_Invalid() {
        assertFalse(OrderValidationUtil.isValidAmount(null));
        assertFalse(OrderValidationUtil.isValidAmount(BigDecimal.ZERO));
        assertFalse(OrderValidationUtil.isValidAmount(new BigDecimal("-100.00")));
    }
    
    /**
     * 测试金额验证异常抛出
     */
    @Test
    void testValidateAmount_ThrowsException() {
        assertDoesNotThrow(() -> OrderValidationUtil.validateAmount(new BigDecimal("100.00"), "金额"));
        
        OrderException exception1 = assertThrows(OrderException.class, 
            () -> OrderValidationUtil.validateAmount(null, "金额"));
        assertEquals("VALIDATION_FAILED", exception1.getErrorCode());
        
        OrderException exception2 = assertThrows(OrderException.class, 
            () -> OrderValidationUtil.validateAmount(BigDecimal.ZERO, "金额"));
        assertEquals("VALIDATION_FAILED", exception2.getErrorCode());
    }
    
    /**
     * 测试有效数量验证
     */
    @Test
    void testIsValidQuantity_Valid() {
        assertTrue(OrderValidationUtil.isValidQuantity(1));
        assertTrue(OrderValidationUtil.isValidQuantity(100));
        assertTrue(OrderValidationUtil.isValidQuantity(Integer.MAX_VALUE));
    }
    
    /**
     * 测试无效数量验证
     */
    @Test
    void testIsValidQuantity_Invalid() {
        assertFalse(OrderValidationUtil.isValidQuantity(null));
        assertFalse(OrderValidationUtil.isValidQuantity(0));
        assertFalse(OrderValidationUtil.isValidQuantity(-1));
    }
    
    /**
     * 测试数量验证异常抛出
     */
    @Test
    void testValidateQuantity_ThrowsException() {
        assertDoesNotThrow(() -> OrderValidationUtil.validateQuantity(5, "数量"));
        
        OrderException exception = assertThrows(OrderException.class, 
            () -> OrderValidationUtil.validateQuantity(0, "数量"));
        assertEquals("VALIDATION_FAILED", exception.getErrorCode());
    }
    
    /**
     * 测试有效字符串长度验证
     */
    @Test
    void testIsValidLength_Valid() {
        assertTrue(OrderValidationUtil.isValidLength("test", 1, 10));
        assertTrue(OrderValidationUtil.isValidLength("hello world", 5, 20));
        assertTrue(OrderValidationUtil.isValidLength("a", 1, 1));
    }
    
    /**
     * 测试无效字符串长度验证
     */
    @Test
    void testIsValidLength_Invalid() {
        assertFalse(OrderValidationUtil.isValidLength(null, 1, 10));
        assertFalse(OrderValidationUtil.isValidLength("", 1, 10));
        assertFalse(OrderValidationUtil.isValidLength("test", 5, 10)); // 太短
        assertFalse(OrderValidationUtil.isValidLength("this is a very long string", 1, 10)); // 太长
    }
    
    /**
     * 测试字符串长度验证异常抛出
     */
    @Test
    void testValidateLength_ThrowsException() {
        assertDoesNotThrow(() -> OrderValidationUtil.validateLength("test", "字段", 1, 10));
        
        OrderException exception1 = assertThrows(OrderException.class, 
            () -> OrderValidationUtil.validateLength(null, "字段", 1, 10));
        assertEquals("VALIDATION_FAILED", exception1.getErrorCode());
        
        OrderException exception2 = assertThrows(OrderException.class, 
            () -> OrderValidationUtil.validateLength("test", "字段", 5, 10));
        assertEquals("VALIDATION_FAILED", exception2.getErrorCode());
    }
    
    /**
     * 测试必填字段验证异常抛出
     */
    @Test
    void testValidateRequired_ThrowsException() {
        assertDoesNotThrow(() -> OrderValidationUtil.validateRequired("value", "字段"));
        assertDoesNotThrow(() -> OrderValidationUtil.validateRequired(123, "字段"));
        
        OrderException exception1 = assertThrows(OrderException.class, 
            () -> OrderValidationUtil.validateRequired(null, "字段"));
        assertEquals("VALIDATION_FAILED", exception1.getErrorCode());
        
        OrderException exception2 = assertThrows(OrderException.class, 
            () -> OrderValidationUtil.validateRequired("", "字段"));
        assertEquals("VALIDATION_FAILED", exception2.getErrorCode());
    }
    
    /**
     * 测试ID验证异常抛出
     */
    @Test
    void testValidateId_ThrowsException() {
        assertDoesNotThrow(() -> OrderValidationUtil.validateId(1L, "ID"));
        assertDoesNotThrow(() -> OrderValidationUtil.validateId(Long.MAX_VALUE, "ID"));
        
        OrderException exception1 = assertThrows(OrderException.class, 
            () -> OrderValidationUtil.validateId(null, "ID"));
        assertEquals("VALIDATION_FAILED", exception1.getErrorCode());
        
        OrderException exception2 = assertThrows(OrderException.class, 
            () -> OrderValidationUtil.validateId(0L, "ID"));
        assertEquals("VALIDATION_FAILED", exception2.getErrorCode());
    }
    
    /**
     * 测试日期范围验证异常抛出
     */
    @Test
    void testValidateDateRange_ThrowsException() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        
        assertDoesNotThrow(() -> OrderValidationUtil.validateDateRange(today, tomorrow));
        assertDoesNotThrow(() -> OrderValidationUtil.validateDateRange(today, today));
        
        OrderException exception1 = assertThrows(OrderException.class, 
            () -> OrderValidationUtil.validateDateRange(null, tomorrow));
        assertEquals("VALIDATION_FAILED", exception1.getErrorCode());
        
        OrderException exception2 = assertThrows(OrderException.class, 
            () -> OrderValidationUtil.validateDateRange(tomorrow, today));
        assertEquals("VALIDATION_FAILED", exception2.getErrorCode());
    }
    
    /**
     * 测试订单类型验证
     */
    @Test
    void testIsValidOrderType() {
        assertTrue(OrderValidationUtil.isValidOrderType("sales"));
        assertTrue(OrderValidationUtil.isValidOrderType("rental"));
        assertFalse(OrderValidationUtil.isValidOrderType("invalid"));
        assertFalse(OrderValidationUtil.isValidOrderType(null));
    }
    
    /**
     * 测试订单状态验证
     */
    @Test
    void testIsValidOrderStatus() {
        assertTrue(OrderValidationUtil.isValidOrderStatus("pending"));
        assertTrue(OrderValidationUtil.isValidOrderStatus("confirmed"));
        assertTrue(OrderValidationUtil.isValidOrderStatus("processing"));
        assertTrue(OrderValidationUtil.isValidOrderStatus("shipped"));
        assertTrue(OrderValidationUtil.isValidOrderStatus("delivered"));
        assertTrue(OrderValidationUtil.isValidOrderStatus("completed"));
        assertTrue(OrderValidationUtil.isValidOrderStatus("cancelled"));
        assertFalse(OrderValidationUtil.isValidOrderStatus("invalid"));
        assertFalse(OrderValidationUtil.isValidOrderStatus(null));
    }
    
    /**
     * 测试支付状态验证
     */
    @Test
    void testIsValidPaymentStatus() {
        assertTrue(OrderValidationUtil.isValidPaymentStatus("pending"));
        assertTrue(OrderValidationUtil.isValidPaymentStatus("paid"));
        assertTrue(OrderValidationUtil.isValidPaymentStatus("failed"));
        assertTrue(OrderValidationUtil.isValidPaymentStatus("refunded"));
        assertFalse(OrderValidationUtil.isValidPaymentStatus("invalid"));
        assertFalse(OrderValidationUtil.isValidPaymentStatus(null));
    }
    
    /**
     * 测试货币类型验证
     */
    @Test
    void testIsValidCurrency() {
        assertTrue(OrderValidationUtil.isValidCurrency("CNY"));
        assertTrue(OrderValidationUtil.isValidCurrency("USD"));
        assertTrue(OrderValidationUtil.isValidCurrency("EUR"));
        assertFalse(OrderValidationUtil.isValidCurrency("invalid"));
        assertFalse(OrderValidationUtil.isValidCurrency(null));
    }
    
    /**
     * 测试金额计算验证异常抛出
     */
    @Test
    void testValidateAmountCalculation_ThrowsException() {
        BigDecimal subtotal = new BigDecimal("1000.00");
        BigDecimal shippingFee = new BigDecimal("50.00");
        BigDecimal discount = new BigDecimal("0.00");
        BigDecimal correctTotal = new BigDecimal("1050.00");
        BigDecimal incorrectTotal = new BigDecimal("2000.00");
        
        assertDoesNotThrow(() -> OrderValidationUtil.validateAmountCalculation(
            subtotal, shippingFee, discount, correctTotal));
        
        OrderException exception1 = assertThrows(OrderException.class, 
            () -> OrderValidationUtil.validateAmountCalculation(
                subtotal, shippingFee, discount, null));
        assertEquals("VALIDATION_FAILED", exception1.getErrorCode());
        
        OrderException exception2 = assertThrows(OrderException.class, 
            () -> OrderValidationUtil.validateAmountCalculation(
                subtotal, shippingFee, discount, incorrectTotal));
        assertEquals("AMOUNT_CALCULATION_ERROR", exception2.getErrorCode());
    }
    
    /**
     * 测试状态流转验证异常抛出
     */
    @Test
    void testValidateStatusTransition_ThrowsException() {
        assertDoesNotThrow(() -> OrderValidationUtil.validateStatusTransition("pending", "confirmed"));
        assertDoesNotThrow(() -> OrderValidationUtil.validateStatusTransition("confirmed", "processing"));
        
        OrderException exception1 = assertThrows(OrderException.class, 
            () -> OrderValidationUtil.validateStatusTransition(null, "confirmed"));
        assertEquals("VALIDATION_FAILED", exception1.getErrorCode());
        
        OrderException exception2 = assertThrows(OrderException.class, 
            () -> OrderValidationUtil.validateStatusTransition("completed", "pending"));
        assertEquals("INVALID_ORDER_STATUS", exception2.getErrorCode());
    }
}