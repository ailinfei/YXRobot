package com.yxrobot.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 数据保护工具类测试
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@DisplayName("数据保护工具类测试")
class DataProtectionUtilsTest {
    
    @Test
    @DisplayName("测试手机号脱敏")
    void testMaskPhone() {
        // 正常手机号
        assertEquals("138****5678", DataProtectionUtils.maskPhone("13812345678"));
        assertEquals("159****4321", DataProtectionUtils.maskPhone("15987654321"));
        
        // 非手机号格式
        assertEquals("12345678901", DataProtectionUtils.maskPhone("12345678901"));
        assertEquals("021-12345678", DataProtectionUtils.maskPhone("021-12345678"));
        
        // 空值处理
        assertNull(DataProtectionUtils.maskPhone(null));
        assertEquals("", DataProtectionUtils.maskPhone(""));
    }
    
    @Test
    @DisplayName("测试邮箱脱敏")
    void testMaskEmail() {
        // 正常邮箱
        assertEquals("te***@example.com", DataProtectionUtils.maskEmail("test@example.com"));
        assertEquals("us***@domain.co.uk", DataProtectionUtils.maskEmail("user.name@domain.co.uk"));
        
        // 短用户名
        assertEquals("*b@test.com", DataProtectionUtils.maskEmail("ab@test.com"));
        assertEquals("*c@test.com", DataProtectionUtils.maskEmail("abc@test.com"));
        
        // 非邮箱格式
        assertEquals("invalid-email", DataProtectionUtils.maskEmail("invalid-email"));
        
        // 空值处理
        assertNull(DataProtectionUtils.maskEmail(null));
        assertEquals("", DataProtectionUtils.maskEmail(""));
    }
    
    @Test
    @DisplayName("测试身份证号脱敏")
    void testMaskIdCard() {
        // 18位身份证
        assertEquals("110101********1234", DataProtectionUtils.maskIdCard("110101199001011234"));
        
        // 15位身份证
        assertEquals("110101*****1234", DataProtectionUtils.maskIdCard("110101900101234"));
        
        // 非身份证格式
        assertEquals("12345", DataProtectionUtils.maskIdCard("12345"));
        
        // 空值处理
        assertNull(DataProtectionUtils.maskIdCard(null));
        assertEquals("", DataProtectionUtils.maskIdCard(""));
    }
    
    @Test
    @DisplayName("测试银行卡号脱敏")
    void testMaskBankCard() {
        // 16位银行卡
        assertEquals("6222 **** **** 1234", DataProtectionUtils.maskBankCard("6222021234561234"));
        
        // 19位银行卡
        assertEquals("6222 **** **** 7890", DataProtectionUtils.maskBankCard("6222021234567890123"));
        
        // 非银行卡格式
        assertEquals("12345", DataProtectionUtils.maskBankCard("12345"));
        
        // 空值处理
        assertNull(DataProtectionUtils.maskBankCard(null));
        assertEquals("", DataProtectionUtils.maskBankCard(""));
    }
    
    @Test
    @DisplayName("测试IP地址脱敏")
    void testMaskIpAddress() {
        // 正常IP地址
        assertEquals("192.168.***.1", DataProtectionUtils.maskIpAddress("192.168.1.1"));
        assertEquals("10.0.***.100", DataProtectionUtils.maskIpAddress("10.0.0.100"));
        
        // 非IP格式
        assertEquals("invalid-ip", DataProtectionUtils.maskIpAddress("invalid-ip"));
        
        // 空值处理
        assertNull(DataProtectionUtils.maskIpAddress(null));
        assertEquals("", DataProtectionUtils.maskIpAddress(""));
    }
    
    @Test
    @DisplayName("测试姓名脱敏")
    void testMaskName() {
        // 不同长度姓名
        assertEquals("*", DataProtectionUtils.maskName("张"));
        assertEquals("李*", DataProtectionUtils.maskName("李四"));
        assertEquals("王*明", DataProtectionUtils.maskName("王小明"));
        assertEquals("欧***龙", DataProtectionUtils.maskName("欧阳成龙"));
        
        // 英文姓名
        assertEquals("J***n", DataProtectionUtils.maskName("John"));
        assertEquals("J*** S****h", DataProtectionUtils.maskName("John Smith"));
        
        // 空值处理
        assertNull(DataProtectionUtils.maskName(null));
        assertEquals("", DataProtectionUtils.maskName(""));
    }
    
    @Test
    @DisplayName("测试地址脱敏")
    void testMaskAddress() {
        // 短地址
        assertEquals("北京市***", DataProtectionUtils.maskAddress("北京市朝阳区"));
        
        // 长地址
        assertEquals("北京市朝阳***号楼", DataProtectionUtils.maskAddress("北京市朝阳区某某街道123号楼"));
        
        // 空值处理
        assertNull(DataProtectionUtils.maskAddress(null));
        assertEquals("", DataProtectionUtils.maskAddress(""));
    }
    
    @Test
    @DisplayName("测试自动PII脱敏")
    void testAutoMaskPII() {
        String text = "用户张三，手机号13812345678，邮箱test@example.com，身份证110101199001011234";
        String masked = DataProtectionUtils.autoMaskPII(text);
        
        assertTrue(masked.contains("138****5678"));
        assertTrue(masked.contains("te***@example.com"));
        assertTrue(masked.contains("110101********1234"));
        
        // 空值处理
        assertNull(DataProtectionUtils.autoMaskPII(null));
        assertEquals("", DataProtectionUtils.autoMaskPII(""));
    }
    
    @Test
    @DisplayName("测试敏感信息检测")
    void testContainsSensitiveInfo() {
        // 包含敏感信息
        assertTrue(DataProtectionUtils.containsSensitiveInfo("手机号：13812345678"));
        assertTrue(DataProtectionUtils.containsSensitiveInfo("邮箱：test@example.com"));
        assertTrue(DataProtectionUtils.containsSensitiveInfo("身份证：110101199001011234"));
        assertTrue(DataProtectionUtils.containsSensitiveInfo("密码：password123"));
        
        // 不包含敏感信息
        assertFalse(DataProtectionUtils.containsSensitiveInfo("这是普通文本"));
        assertFalse(DataProtectionUtils.containsSensitiveInfo("设备序列号：YX2025001234"));
        
        // 空值处理
        assertFalse(DataProtectionUtils.containsSensitiveInfo(null));
        assertFalse(DataProtectionUtils.containsSensitiveInfo(""));
    }
    
    @Test
    @DisplayName("测试Map数据批量脱敏")
    void testMaskMapData() {
        Map<String, Object> data = new HashMap<>();
        data.put("customerName", "张三");
        data.put("phone", "13812345678");
        data.put("email", "test@example.com");
        data.put("address", "北京市朝阳区某某街道123号");
        data.put("serialNumber", "YX2025001234"); // 非敏感信息
        data.put("age", 30); // 非字符串类型
        
        Map<String, Object> masked = DataProtectionUtils.maskMapData(data);
        
        assertEquals("张*", masked.get("customerName"));
        assertEquals("138****5678", masked.get("phone"));
        assertEquals("te***@example.com", masked.get("email"));
        assertTrue(((String) masked.get("address")).contains("***"));
        assertEquals("YX2025001234", masked.get("serialNumber")); // 应该保持不变
        assertEquals(30, masked.get("age")); // 应该保持不变
        
        // 空值处理
        assertNull(DataProtectionUtils.maskMapData(null));
        assertTrue(DataProtectionUtils.maskMapData(new HashMap<>()).isEmpty());
    }
    
    @Test
    @DisplayName("测试占位符生成")
    void testGeneratePlaceholder() {
        assertEquals("[手机号]", DataProtectionUtils.generatePlaceholder("phone"));
        assertEquals("[邮箱地址]", DataProtectionUtils.generatePlaceholder("email"));
        assertEquals("[姓名]", DataProtectionUtils.generatePlaceholder("name"));
        assertEquals("[地址信息]", DataProtectionUtils.generatePlaceholder("address"));
        assertEquals("[身份证号]", DataProtectionUtils.generatePlaceholder("idcard"));
        assertEquals("[银行卡号]", DataProtectionUtils.generatePlaceholder("bankcard"));
        assertEquals("[IP地址]", DataProtectionUtils.generatePlaceholder("ip"));
        assertEquals("[敏感信息]", DataProtectionUtils.generatePlaceholder("unknown"));
    }
}