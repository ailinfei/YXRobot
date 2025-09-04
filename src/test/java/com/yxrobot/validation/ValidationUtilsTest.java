package com.yxrobot.validation;

import com.yxrobot.config.ValidationConfig;
import com.yxrobot.util.ValidationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 验证工具类测试
 * 测试所有验证方法的正确性
 */
@DisplayName("验证工具类测试")
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class ValidationUtilsTest {
    
    private ValidationUtils validationUtils;
    private ValidationConfig validationConfig;
    
    @BeforeEach
    void setUp() {
        validationConfig = new ValidationConfig();
        validationUtils = new ValidationUtils(validationConfig);
    }
    
    @Test
    @DisplayName("01. 空值检查测试")
    void testEmptyChecks() {
        System.out.println("🔍 开始空值检查测试...");
        
        // 测试字符串空值检查
        assertTrue(ValidationUtils.isEmpty(null));
        assertTrue(ValidationUtils.isEmpty(""));
        assertTrue(ValidationUtils.isEmpty("   "));
        assertFalse(ValidationUtils.isEmpty("test"));
        
        // 测试非空检查
        assertFalse(ValidationUtils.isNotEmpty(null));
        assertFalse(ValidationUtils.isNotEmpty(""));
        assertFalse(ValidationUtils.isNotEmpty("   "));
        assertTrue(ValidationUtils.isNotEmpty("test"));
        
        System.out.println("✅ 空值检查测试通过");
    }
    
    @Test
    @DisplayName("02. 必填字段验证测试")
    void testRequiredFieldValidation() {
        System.out.println("🔍 开始必填字段验证测试...");
        
        // 测试空值
        String result = validationUtils.validateRequired(null, "testField");
        assertNotNull(result);
        assertTrue(result.contains("testField"));
        assertTrue(result.contains("必填"));
        
        // 测试空字符串
        result = validationUtils.validateRequired("", "testField");
        assertNotNull(result);
        
        // 测试空白字符串
        result = validationUtils.validateRequired("   ", "testField");
        assertNotNull(result);
        
        // 测试有效值
        result = validationUtils.validateRequired("valid", "testField");
        assertNull(result);
        
        System.out.println("✅ 必填字段验证测试通过");
    }
    
    @Test
    @DisplayName("03. 字符串长度验证测试")
    void testLengthValidation() {
        System.out.println("🔍 开始字符串长度验证测试...");
        
        // 测试长度过短
        String result = validationUtils.validateLength("ab", "testField", 3, 10);
        assertNotNull(result);
        assertTrue(result.contains("长度"));
        
        // 测试长度过长
        result = validationUtils.validateLength("abcdefghijk", "testField", 3, 10);
        assertNotNull(result);
        assertTrue(result.contains("长度"));
        
        // 测试有效长度
        result = validationUtils.validateLength("abcde", "testField", 3, 10);
        assertNull(result);
        
        // 测试null值（应该通过，由必填验证处理）
        result = validationUtils.validateLength(null, "testField", 3, 10);
        assertNull(result);
        
        System.out.println("✅ 字符串长度验证测试通过");
    }
    
    @Test
    @DisplayName("04. 手机号验证测试")
    void testPhoneValidation() {
        System.out.println("🔍 开始手机号验证测试...");
        
        // 测试有效手机号
        String result = validationUtils.validatePhone("13800138000");
        assertNull(result);
        
        result = validationUtils.validatePhone("15912345678");
        assertNull(result);
        
        // 测试无效手机号
        result = validationUtils.validatePhone("12800138000"); // 以12开头
        assertNotNull(result);
        
        result = validationUtils.validatePhone("1380013800"); // 10位数字
        assertNotNull(result);
        
        result = validationUtils.validatePhone("138001380000"); // 12位数字
        assertNotNull(result);
        
        result = validationUtils.validatePhone("abcdefghijk"); // 非数字
        assertNotNull(result);
        
        // 测试空值（应该通过，由必填验证处理）
        result = validationUtils.validatePhone(null);
        assertNull(result);
        
        result = validationUtils.validatePhone("");
        assertNull(result);
        
        System.out.println("✅ 手机号验证测试通过");
    }
    
    @Test
    @DisplayName("05. 邮箱验证测试")
    void testEmailValidation() {
        System.out.println("🔍 开始邮箱验证测试...");
        
        // 测试有效邮箱
        String result = validationUtils.validateEmail("test@example.com");
        assertNull(result);
        
        result = validationUtils.validateEmail("user.name+tag@domain.co.uk");
        assertNull(result);
        
        // 测试无效邮箱
        result = validationUtils.validateEmail("invalid-email");
        assertNotNull(result);
        
        result = validationUtils.validateEmail("@example.com");
        assertNotNull(result);
        
        result = validationUtils.validateEmail("test@");
        assertNotNull(result);
        
        result = validationUtils.validateEmail("test@.com");
        assertNotNull(result);
        
        // 测试空值（应该通过，由必填验证处理）
        result = validationUtils.validateEmail(null);
        assertNull(result);
        
        result = validationUtils.validateEmail("");
        assertNull(result);
        
        System.out.println("✅ 邮箱验证测试通过");
    }
    
    @Test
    @DisplayName("06. 订单号验证测试")
    void testOrderNumberValidation() {
        System.out.println("🔍 开始订单号验证测试...");
        
        // 测试有效订单号
        String result = validationUtils.validateOrderNumber("ORD1234567890");
        assertNull(result);
        
        result = validationUtils.validateOrderNumber("SAL1234567890");
        assertNull(result);
        
        result = validationUtils.validateOrderNumber("REN1234567890");
        assertNull(result);
        
        // 测试无效格式
        result = validationUtils.validateOrderNumber("ord1234567890"); // 小写
        assertNotNull(result);
        
        result = validationUtils.validateOrderNumber("ORD123456789"); // 9位数字
        assertNotNull(result);
        
        result = validationUtils.validateOrderNumber("ORD12345678901"); // 11位数字
        assertNotNull(result);
        
        result = validationUtils.validateOrderNumber("ABCD1234567890"); // 4位字母
        assertNotNull(result);
        
        // 测试不允许的前缀
        result = validationUtils.validateOrderNumber("ABC1234567890");
        assertNotNull(result);
        assertTrue(result.contains("前缀"));
        
        System.out.println("✅ 订单号验证测试通过");
    }
    
    @Test
    @DisplayName("07. 金额验证测试")
    void testAmountValidation() {
        System.out.println("🔍 开始金额验证测试...");
        
        // 测试有效金额
        String result = validationUtils.validateAmount(new BigDecimal("100.50"), "金额");
        assertNull(result);
        
        // 测试null值
        result = validationUtils.validateAmount(null, "金额");
        assertNotNull(result);
        assertTrue(result.contains("不能为空"));
        
        // 测试负数（默认不允许）
        result = validationUtils.validateAmount(new BigDecimal("-100"), "金额");
        assertNotNull(result);
        assertTrue(result.contains("负数"));
        
        // 测试零值（默认不允许）
        result = validationUtils.validateAmount(BigDecimal.ZERO, "金额");
        assertNotNull(result);
        assertTrue(result.contains("零"));
        
        // 测试超出范围
        result = validationUtils.validateAmount(new BigDecimal("0.001"), "金额");
        assertNotNull(result);
        assertTrue(result.contains("不能小于"));
        
        result = validationUtils.validateAmount(new BigDecimal("9999999"), "金额");
        assertNotNull(result);
        assertTrue(result.contains("不能大于"));
        
        // 测试小数位数过多
        result = validationUtils.validateAmount(new BigDecimal("100.123"), "金额");
        assertNotNull(result);
        assertTrue(result.contains("小数位数"));
        
        System.out.println("✅ 金额验证测试通过");
    }
    
    @Test
    @DisplayName("08. 数量验证测试")
    void testQuantityValidation() {
        System.out.println("🔍 开始数量验证测试...");
        
        // 测试有效数量
        String result = validationUtils.validateQuantity(5, "数量");
        assertNull(result);
        
        // 测试null值
        result = validationUtils.validateQuantity(null, "数量");
        assertNotNull(result);
        assertTrue(result.contains("不能为空"));
        
        // 测试小于最小值
        result = validationUtils.validateQuantity(0, "数量");
        assertNotNull(result);
        assertTrue(result.contains("不能小于"));
        
        // 测试大于最大值
        result = validationUtils.validateQuantity(10000, "数量");
        assertNotNull(result);
        assertTrue(result.contains("不能大于"));
        
        System.out.println("✅ 数量验证测试通过");
    }
    
    @Test
    @DisplayName("09. 日期范围验证测试")
    void testDateRangeValidation() {
        System.out.println("🔍 开始日期范围验证测试...");
        
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate yesterday = today.minusDays(1);
        
        // 测试有效日期范围
        String result = validationUtils.validateDateRange(today, tomorrow, "测试期间");
        assertNull(result);
        
        // 测试开始日期晚于结束日期
        result = validationUtils.validateDateRange(tomorrow, today, "测试期间");
        assertNotNull(result);
        assertTrue(result.contains("开始日期不能晚于结束日期"));
        
        // 测试null值
        result = validationUtils.validateDateRange(null, tomorrow, "测试期间");
        assertNotNull(result);
        assertTrue(result.contains("不能为空"));
        
        result = validationUtils.validateDateRange(today, null, "测试期间");
        assertNotNull(result);
        assertTrue(result.contains("不能为空"));
        
        System.out.println("✅ 日期范围验证测试通过");
    }
    
    @Test
    @DisplayName("10. 枚举值验证测试")
    void testEnumValidation() {
        System.out.println("🔍 开始枚举值验证测试...");
        
        java.util.List<String> allowedValues = java.util.List.of("value1", "value2", "value3");
        
        // 测试有效值
        String result = validationUtils.validateEnum("value1", allowedValues, "测试字段");
        assertNull(result);
        
        // 测试无效值
        result = validationUtils.validateEnum("invalid", allowedValues, "测试字段");
        assertNotNull(result);
        assertTrue(result.contains("必须是以下之一"));
        assertTrue(result.contains("value1"));
        
        // 测试空值（应该通过，由必填验证处理）
        result = validationUtils.validateEnum(null, allowedValues, "测试字段");
        assertNull(result);
        
        result = validationUtils.validateEnum("", allowedValues, "测试字段");
        assertNull(result);
        
        System.out.println("✅ 枚举值验证测试通过");
    }
    
    @Test
    @DisplayName("11. 状态流转验证测试")
    void testStatusTransitionValidation() {
        System.out.println("🔍 开始状态流转验证测试...");
        
        // 测试有效状态流转
        String result = validationUtils.validateStatusTransition("pending", "confirmed");
        assertNull(result);
        
        result = validationUtils.validateStatusTransition("confirmed", "processing");
        assertNull(result);
        
        // 测试无效状态流转
        result = validationUtils.validateStatusTransition("completed", "pending");
        assertNotNull(result);
        assertTrue(result.contains("不允许"));
        
        result = validationUtils.validateStatusTransition("pending", "delivered");
        assertNotNull(result);
        assertTrue(result.contains("不允许"));
        
        // 测试未知状态
        result = validationUtils.validateStatusTransition("unknown", "pending");
        assertNotNull(result);
        assertTrue(result.contains("未知"));
        
        // 测试空值
        result = validationUtils.validateStatusTransition(null, "confirmed");
        assertNotNull(result);
        assertTrue(result.contains("不能为空"));
        
        result = validationUtils.validateStatusTransition("pending", null);
        assertNotNull(result);
        assertTrue(result.contains("不能为空"));
        
        System.out.println("✅ 状态流转验证测试通过");
    }
    
    @Test
    @DisplayName("12. ID验证测试")
    void testIdValidation() {
        System.out.println("🔍 开始ID验证测试...");
        
        // 测试有效ID
        String result = validationUtils.validateId(1L, "ID");
        assertNull(result);
        
        result = validationUtils.validateId(999L, "ID");
        assertNull(result);
        
        // 测试null值
        result = validationUtils.validateId(null, "ID");
        assertNotNull(result);
        assertTrue(result.contains("不能为空"));
        
        // 测试零值
        result = validationUtils.validateId(0L, "ID");
        assertNotNull(result);
        assertTrue(result.contains("正整数"));
        
        // 测试负值
        result = validationUtils.validateId(-1L, "ID");
        assertNotNull(result);
        assertTrue(result.contains("正整数"));
        
        System.out.println("✅ ID验证测试通过");
    }
    
    @Test
    @DisplayName("13. 时间戳验证测试")
    void testDateTimeValidation() {
        System.out.println("🔍 开始时间戳验证测试...");
        
        LocalDateTime now = LocalDateTime.now();
        
        // 测试有效时间戳
        String result = validationUtils.validateDateTime(now, "时间");
        assertNull(result);
        
        result = validationUtils.validateDateTime(now.minusMonths(6), "时间");
        assertNull(result);
        
        result = validationUtils.validateDateTime(now.plusMonths(6), "时间");
        assertNull(result);
        
        // 测试null值（应该通过）
        result = validationUtils.validateDateTime(null, "时间");
        assertNull(result);
        
        // 测试过早的时间
        result = validationUtils.validateDateTime(now.minusYears(2), "时间");
        assertNotNull(result);
        assertTrue(result.contains("不能早于"));
        
        // 测试过晚的时间
        result = validationUtils.validateDateTime(now.plusYears(2), "时间");
        assertNotNull(result);
        assertTrue(result.contains("不能晚于"));
        
        System.out.println("✅ 时间戳验证测试通过");
    }
}