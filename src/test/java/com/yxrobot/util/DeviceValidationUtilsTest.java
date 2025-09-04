package com.yxrobot.util;

import com.yxrobot.exception.ManagedDeviceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 设备验证工具类测试
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@DisplayName("设备验证工具类测试")
class DeviceValidationUtilsTest {
    
    @Test
    @DisplayName("测试设备序列号验证 - 正常情况")
    void testValidateSerialNumber_Valid() {
        // 正常的序列号
        assertDoesNotThrow(() -> DeviceValidationUtils.validateSerialNumber("YX2025001234"));
        assertDoesNotThrow(() -> DeviceValidationUtils.validateSerialNumber("ABC-123-DEF456"));
        assertDoesNotThrow(() -> DeviceValidationUtils.validateSerialNumber("1234567890ABCD"));
    }
    
    @Test
    @DisplayName("测试设备序列号验证 - 异常情况")
    void testValidateSerialNumber_Invalid() {
        // 空值
        assertThrows(ManagedDeviceException.class, 
            () -> DeviceValidationUtils.validateSerialNumber(null));
        assertThrows(ManagedDeviceException.class, 
            () -> DeviceValidationUtils.validateSerialNumber(""));
        assertThrows(ManagedDeviceException.class, 
            () -> DeviceValidationUtils.validateSerialNumber("   "));
        
        // 长度不符
        assertThrows(ManagedDeviceException.class, 
            () -> DeviceValidationUtils.validateSerialNumber("ABC123")); // 太短
        assertThrows(ManagedDeviceException.class, 
            () -> DeviceValidationUtils.validateSerialNumber("ABCDEFGHIJKLMNOPQRSTUVWXYZ")); // 太长
        
        // 包含非法字符
        assertThrows(ManagedDeviceException.class, 
            () -> DeviceValidationUtils.validateSerialNumber("ABC123@#$"));
        assertThrows(ManagedDeviceException.class, 
            () -> DeviceValidationUtils.validateSerialNumber("abc123def")); // 小写字母
    }
    
    @Test
    @DisplayName("测试固件版本验证 - 正常情况")
    void testValidateFirmwareVersion_Valid() {
        assertDoesNotThrow(() -> DeviceValidationUtils.validateFirmwareVersion("1.0.0"));
        assertDoesNotThrow(() -> DeviceValidationUtils.validateFirmwareVersion("2.15.3"));
        assertDoesNotThrow(() -> DeviceValidationUtils.validateFirmwareVersion("10.99.999"));
    }
    
    @Test
    @DisplayName("测试固件版本验证 - 异常情况")
    void testValidateFirmwareVersion_Invalid() {
        // 空值
        assertThrows(ManagedDeviceException.class, 
            () -> DeviceValidationUtils.validateFirmwareVersion(null));
        assertThrows(ManagedDeviceException.class, 
            () -> DeviceValidationUtils.validateFirmwareVersion(""));
        
        // 格式不正确
        assertThrows(ManagedDeviceException.class, 
            () -> DeviceValidationUtils.validateFirmwareVersion("1.0"));
        assertThrows(ManagedDeviceException.class, 
            () -> DeviceValidationUtils.validateFirmwareVersion("1.0.0.1"));
        assertThrows(ManagedDeviceException.class, 
            () -> DeviceValidationUtils.validateFirmwareVersion("v1.0.0"));
        assertThrows(ManagedDeviceException.class, 
            () -> DeviceValidationUtils.validateFirmwareVersion("1.0.a"));
    }
    
    @Test
    @DisplayName("测试客户名称验证 - 正常情况")
    void testValidateCustomerName_Valid() {
        assertDoesNotThrow(() -> DeviceValidationUtils.validateCustomerName("张三"));
        assertDoesNotThrow(() -> DeviceValidationUtils.validateCustomerName("John Smith"));
        assertDoesNotThrow(() -> DeviceValidationUtils.validateCustomerName("李明 123"));
        assertDoesNotThrow(() -> DeviceValidationUtils.validateCustomerName("ABC公司"));
    }
    
    @Test
    @DisplayName("测试客户名称验证 - 异常情况")
    void testValidateCustomerName_Invalid() {
        // 空值
        assertThrows(ManagedDeviceException.class, 
            () -> DeviceValidationUtils.validateCustomerName(null));
        assertThrows(ManagedDeviceException.class, 
            () -> DeviceValidationUtils.validateCustomerName(""));
        
        // 长度不符
        assertThrows(ManagedDeviceException.class, 
            () -> DeviceValidationUtils.validateCustomerName("A")); // 太短
        
        // 包含特殊字符
        assertThrows(ManagedDeviceException.class, 
            () -> DeviceValidationUtils.validateCustomerName("张三@#$"));
    }
    
    @Test
    @DisplayName("测试电话号码验证 - 正常情况")
    void testValidatePhone_Valid() {
        // 手机号
        assertDoesNotThrow(() -> DeviceValidationUtils.validatePhone("13812345678"));
        assertDoesNotThrow(() -> DeviceValidationUtils.validatePhone("15987654321"));
        
        // 固定电话
        assertDoesNotThrow(() -> DeviceValidationUtils.validatePhone("021-12345678"));
        assertDoesNotThrow(() -> DeviceValidationUtils.validatePhone("02112345678"));
        
        // 空值（允许）
        assertDoesNotThrow(() -> DeviceValidationUtils.validatePhone(null));
        assertDoesNotThrow(() -> DeviceValidationUtils.validatePhone(""));
    }
    
    @Test
    @DisplayName("测试电话号码验证 - 异常情况")
    void testValidatePhone_Invalid() {
        // 格式不正确的手机号
        assertThrows(ManagedDeviceException.class, 
            () -> DeviceValidationUtils.validatePhone("12812345678")); // 不是1开头
        assertThrows(ManagedDeviceException.class, 
            () -> DeviceValidationUtils.validatePhone("1381234567")); // 位数不够
        
        // 格式不正确的固定电话
        assertThrows(ManagedDeviceException.class, 
            () -> DeviceValidationUtils.validatePhone("021-123456")); // 位数不够
        
        // 包含非法字符
        assertThrows(ManagedDeviceException.class, 
            () -> DeviceValidationUtils.validatePhone("138abc45678"));
    }
    
    @Test
    @DisplayName("测试邮箱验证 - 正常情况")
    void testValidateEmail_Valid() {
        assertDoesNotThrow(() -> DeviceValidationUtils.validateEmail("test@example.com"));
        assertDoesNotThrow(() -> DeviceValidationUtils.validateEmail("user.name@domain.co.uk"));
        assertDoesNotThrow(() -> DeviceValidationUtils.validateEmail("test123@test-domain.org"));
        
        // 空值（允许）
        assertDoesNotThrow(() -> DeviceValidationUtils.validateEmail(null));
        assertDoesNotThrow(() -> DeviceValidationUtils.validateEmail(""));
    }
    
    @Test
    @DisplayName("测试邮箱验证 - 异常情况")
    void testValidateEmail_Invalid() {
        assertThrows(ManagedDeviceException.class, 
            () -> DeviceValidationUtils.validateEmail("invalid-email"));
        assertThrows(ManagedDeviceException.class, 
            () -> DeviceValidationUtils.validateEmail("test@"));
        assertThrows(ManagedDeviceException.class, 
            () -> DeviceValidationUtils.validateEmail("@example.com"));
        assertThrows(ManagedDeviceException.class, 
            () -> DeviceValidationUtils.validateEmail("test@.com"));
    }
    
    @Test
    @DisplayName("测试XSS攻击检测")
    void testValidateAgainstXSS() {
        // 正常内容
        assertDoesNotThrow(() -> DeviceValidationUtils.validateAgainstXSS("正常内容", "测试字段"));
        
        // XSS攻击内容
        assertThrows(ManagedDeviceException.class, 
            () -> DeviceValidationUtils.validateAgainstXSS("<script>alert('xss')</script>", "测试字段"));
        assertThrows(ManagedDeviceException.class, 
            () -> DeviceValidationUtils.validateAgainstXSS("javascript:alert('xss')", "测试字段"));
        assertThrows(ManagedDeviceException.class, 
            () -> DeviceValidationUtils.validateAgainstXSS("<img onload='alert(1)'>", "测试字段"));
        assertThrows(ManagedDeviceException.class, 
            () -> DeviceValidationUtils.validateAgainstXSS("<iframe src='evil.com'></iframe>", "测试字段"));
    }
    
    @Test
    @DisplayName("测试SQL注入检测")
    void testValidateAgainstSQLInjection() {
        // 正常内容
        assertDoesNotThrow(() -> DeviceValidationUtils.validateAgainstSQLInjection("正常内容", "测试字段"));
        
        // SQL注入内容
        assertThrows(ManagedDeviceException.class, 
            () -> DeviceValidationUtils.validateAgainstSQLInjection("'; DROP TABLE users; --", "测试字段"));
        assertThrows(ManagedDeviceException.class, 
            () -> DeviceValidationUtils.validateAgainstSQLInjection("1' OR '1'='1", "测试字段"));
        assertThrows(ManagedDeviceException.class, 
            () -> DeviceValidationUtils.validateAgainstSQLInjection("UNION SELECT * FROM users", "测试字段"));
    }
    
    @Test
    @DisplayName("测试ID验证")
    void testValidateId() {
        // 正常ID
        assertDoesNotThrow(() -> DeviceValidationUtils.validateId(1L, "testId"));
        assertDoesNotThrow(() -> DeviceValidationUtils.validateId(999999L, "testId"));
        
        // 异常ID
        assertThrows(ManagedDeviceException.class, 
            () -> DeviceValidationUtils.validateId(null, "testId"));
        assertThrows(ManagedDeviceException.class, 
            () -> DeviceValidationUtils.validateId(0L, "testId"));
        assertThrows(ManagedDeviceException.class, 
            () -> DeviceValidationUtils.validateId(-1L, "testId"));
    }
    
    @Test
    @DisplayName("测试分页参数验证")
    void testValidatePagination() {
        // 正常参数
        assertDoesNotThrow(() -> DeviceValidationUtils.validatePagination(1, 20));
        assertDoesNotThrow(() -> DeviceValidationUtils.validatePagination(null, null));
        assertDoesNotThrow(() -> DeviceValidationUtils.validatePagination(0, 10));
        
        // 异常参数
        assertThrows(ManagedDeviceException.class, 
            () -> DeviceValidationUtils.validatePagination(-1, 20));
        assertThrows(ManagedDeviceException.class, 
            () -> DeviceValidationUtils.validatePagination(1, 0));
        assertThrows(ManagedDeviceException.class, 
            () -> DeviceValidationUtils.validatePagination(1, 1001));
    }
    
    @Test
    @DisplayName("测试批量验证设备数据")
    void testBatchValidateDeviceData() {
        // 正常数据
        Map<String, Object> validData = new HashMap<>();
        validData.put("serialNumber", "YX2025001234");
        validData.put("firmwareVersion", "1.0.0");
        validData.put("customerName", "张三");
        validData.put("phone", "13812345678");
        validData.put("email", "test@example.com");
        
        Map<String, String> errors = DeviceValidationUtils.batchValidateDeviceData(validData);
        assertTrue(errors.isEmpty());
        
        // 包含错误的数据
        Map<String, Object> invalidData = new HashMap<>();
        invalidData.put("serialNumber", "invalid"); // 无效序列号
        invalidData.put("firmwareVersion", "1.0"); // 无效版本
        invalidData.put("customerName", ""); // 空名称
        
        errors = DeviceValidationUtils.batchValidateDeviceData(invalidData);
        assertFalse(errors.isEmpty());
        assertTrue(errors.containsKey("serialNumber"));
        assertTrue(errors.containsKey("firmwareVersion"));
        assertTrue(errors.containsKey("customerName"));
    }
    
    @Test
    @DisplayName("测试输入清理")
    void testSanitizeInput() {
        // 正常输入
        assertEquals("正常内容", DeviceValidationUtils.sanitizeInput("正常内容"));
        
        // 包含HTML标签
        assertEquals("测试内容", DeviceValidationUtils.sanitizeInput("测试<script>alert(1)</script>内容"));
        
        // 包含JavaScript
        assertEquals("测试内容", DeviceValidationUtils.sanitizeInput("测试javascript:alert(1)内容"));
        
        // 包含事件处理器
        assertEquals("测试内容", DeviceValidationUtils.sanitizeInput("测试onload=alert(1)内容"));
        
        // 空值处理
        assertNull(DeviceValidationUtils.sanitizeInput(null));
        assertEquals("", DeviceValidationUtils.sanitizeInput(""));
    }
    
    @Test
    @DisplayName("测试JSON格式验证")
    void testValidateJsonFormat() {
        // 正常JSON
        assertDoesNotThrow(() -> DeviceValidationUtils.validateJsonFormat("{\"key\":\"value\"}", "测试字段"));
        assertDoesNotThrow(() -> DeviceValidationUtils.validateJsonFormat("[1,2,3]", "测试字段"));
        
        // 空值（允许）
        assertDoesNotThrow(() -> DeviceValidationUtils.validateJsonFormat(null, "测试字段"));
        assertDoesNotThrow(() -> DeviceValidationUtils.validateJsonFormat("", "测试字段"));
        
        // 无效JSON
        assertThrows(ManagedDeviceException.class, 
            () -> DeviceValidationUtils.validateJsonFormat("invalid json", "测试字段"));
        assertThrows(ManagedDeviceException.class, 
            () -> DeviceValidationUtils.validateJsonFormat("{key:value}", "测试字段"));
    }
}