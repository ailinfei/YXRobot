package com.yxrobot.validation;

import com.yxrobot.dto.ManagedDeviceDTO;
import com.yxrobot.entity.ManagedDevice;
import com.yxrobot.exception.ManagedDeviceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 设备管理模块数据验证单元测试类
 * 
 * 测试覆盖范围：
 * - 设备数据格式验证
 * - 设备业务规则验证
 * - 设备边界值验证
 * - XSS防护验证
 * - SQL注入防护验证
 * - PII数据保护验证
 * 
 * 测试目标覆盖率：
 * - 行覆盖率 ≥ 80%
 * - 分支覆盖率 ≥ 75%
 * - 方法覆盖率 ≥ 85%
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("设备管理模块数据验证单元测试")
public class DeviceValidationTest {

    private Validator validator;
    private ManagedDeviceFrontendValidationService validationService;
    private ManagedDeviceDTO testDeviceDTO;
    private ManagedDevice testDevice;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        validationService = new ManagedDeviceFrontendValidationService();

        // 初始化有效的测试数据
        testDeviceDTO = new ManagedDeviceDTO();
        testDeviceDTO.setSerialNumber("YX-TEST-001");
        testDeviceDTO.setModel("YX-EDU-2024");
        testDeviceDTO.setStatus("online");
        testDeviceDTO.setFirmwareVersion("1.0.0");
        testDeviceDTO.setCustomerId("1");
        testDeviceDTO.setCustomerName("[测试客户]");
        testDeviceDTO.setCustomerPhone("[测试电话]");

        testDevice = new ManagedDevice();
        testDevice.setSerialNumber("YX-TEST-001");
        testDevice.setModel("YX-EDU-2024");
        testDevice.setStatus("online");
        testDevice.setFirmwareVersion("1.0.0");
        testDevice.setCustomerId(1L);
        testDevice.setCustomerName("[测试客户]");
        testDevice.setCustomerPhone("[测试电话]");
    }

    @Test
    @DisplayName("序列号验证 - 有效格式")
    void testSerialNumberValidation_ValidFormat() {
        // Given
        String[] validSerialNumbers = {
            "YX-EDU-001",
            "YX-HOME-2024-001",
            "YX-PRO-ABC123",
            "YX-TEST-999999"
        };

        // When & Then
        for (String serialNumber : validSerialNumbers) {
            testDeviceDTO.setSerialNumber(serialNumber);
            Set<ConstraintViolation<ManagedDeviceDTO>> violations = validator.validate(testDeviceDTO);
            
            // 过滤出序列号相关的违规
            long serialNumberViolations = violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("serialNumber"))
                .count();
            
            assertEquals(0, serialNumberViolations, 
                "序列号 " + serialNumber + " 应该是有效的");
        }
    }

    @Test
    @DisplayName("序列号验证 - 无效格式")
    void testSerialNumberValidation_InvalidFormat() {
        // Given
        String[] invalidSerialNumbers = {
            "",                    // 空字符串
            "   ",                 // 空白字符
            "YX",                  // 太短
            "INVALID-FORMAT",      // 不符合规范
            "YX-EDU-001-EXTRA-LONG-SERIAL-NUMBER", // 太长
            "YX@EDU#001",         // 包含特殊字符
            "yX-edu-001",         // 大小写不正确
            "123-456-789"         // 纯数字
        };

        // When & Then
        for (String serialNumber : invalidSerialNumbers) {
            testDeviceDTO.setSerialNumber(serialNumber);
            
            assertThrows(ManagedDeviceException.class, 
                () -> validationService.validateSerialNumber(serialNumber),
                "序列号 " + serialNumber + " 应该被识别为无效");
        }
    }

    @Test
    @DisplayName("设备型号验证 - 有效型号")
    void testModelValidation_ValidModels() {
        // Given
        String[] validModels = {
            "YX-EDU-2024",
            "YX-HOME-2024",
            "YX-PRO-2024"
        };

        // When & Then
        for (String model : validModels) {
            testDeviceDTO.setModel(model);
            Set<ConstraintViolation<ManagedDeviceDTO>> violations = validator.validate(testDeviceDTO);
            
            long modelViolations = violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("model"))
                .count();
            
            assertEquals(0, modelViolations, 
                "设备型号 " + model + " 应该是有效的");
        }
    }

    @Test
    @DisplayName("设备型号验证 - 无效型号")
    void testModelValidation_InvalidModels() {
        // Given
        String[] invalidModels = {
            "",                    // 空字符串
            "INVALID-MODEL",       // 不在枚举范围内
            "YX-UNKNOWN-2024",     // 未知型号
            "YX-EDU-2023",        // 旧版本
            "YX-EDU-2025"         // 未来版本
        };

        // When & Then
        for (String model : invalidModels) {
            testDeviceDTO.setModel(model);
            
            assertThrows(ManagedDeviceException.class, 
                () -> validationService.validateModel(model),
                "设备型号 " + model + " 应该被识别为无效");
        }
    }

    @Test
    @DisplayName("设备状态验证 - 有效状态")
    void testStatusValidation_ValidStatuses() {
        // Given
        String[] validStatuses = {
            "online",
            "offline",
            "error",
            "maintenance"
        };

        // When & Then
        for (String status : validStatuses) {
            testDeviceDTO.setStatus(status);
            Set<ConstraintViolation<ManagedDeviceDTO>> violations = validator.validate(testDeviceDTO);
            
            long statusViolations = violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("status"))
                .count();
            
            assertEquals(0, statusViolations, 
                "设备状态 " + status + " 应该是有效的");
        }
    }

    @Test
    @DisplayName("设备状态验证 - 无效状态")
    void testStatusValidation_InvalidStatuses() {
        // Given
        String[] invalidStatuses = {
            "",                    // 空字符串
            "unknown",             // 未知状态
            "ONLINE",              // 大写
            "off-line",           // 错误格式
            "active",             // 不在枚举范围内
            "inactive"            // 不在枚举范围内
        };

        // When & Then
        for (String status : invalidStatuses) {
            testDeviceDTO.setStatus(status);
            
            assertThrows(ManagedDeviceException.class, 
                () -> validationService.validateStatus(status),
                "设备状态 " + status + " 应该被识别为无效");
        }
    }

    @Test
    @DisplayName("固件版本验证 - 有效版本")
    void testFirmwareVersionValidation_ValidVersions() {
        // Given
        String[] validVersions = {
            "1.0.0",
            "2.1.3",
            "10.20.30",
            "0.0.1",
            "99.99.99"
        };

        // When & Then
        for (String version : validVersions) {
            testDeviceDTO.setFirmwareVersion(version);
            Set<ConstraintViolation<ManagedDeviceDTO>> violations = validator.validate(testDeviceDTO);
            
            long versionViolations = violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("firmwareVersion"))
                .count();
            
            assertEquals(0, versionViolations, 
                "固件版本 " + version + " 应该是有效的");
        }
    }

    @Test
    @DisplayName("固件版本验证 - 无效版本")
    void testFirmwareVersionValidation_InvalidVersions() {
        // Given
        String[] invalidVersions = {
            "",                    // 空字符串
            "1.0",                // 缺少补丁版本
            "1.0.0.0",           // 版本号过长
            "v1.0.0",            // 包含前缀
            "1.0.0-beta",        // 包含后缀
            "a.b.c",             // 非数字
            "1.0.0 ",            // 包含空格
            "1..0"               // 格式错误
        };

        // When & Then
        for (String version : invalidVersions) {
            testDeviceDTO.setFirmwareVersion(version);
            
            assertThrows(ManagedDeviceException.class, 
                () -> validationService.validateFirmwareVersion(version),
                "固件版本 " + version + " 应该被识别为无效");
        }
    }

    @Test
    @DisplayName("客户ID验证 - 有效ID")
    void testCustomerIdValidation_ValidIds() {
        // Given
        String[] validIds = {
            "1",
            "123",
            "999999"
        };

        // When & Then
        for (String customerId : validIds) {
            testDeviceDTO.setCustomerId(customerId);
            Set<ConstraintViolation<ManagedDeviceDTO>> violations = validator.validate(testDeviceDTO);
            
            long customerIdViolations = violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("customerId"))
                .count();
            
            assertEquals(0, customerIdViolations, 
                "客户ID " + customerId + " 应该是有效的");
        }
    }

    @Test
    @DisplayName("客户ID验证 - 无效ID")
    void testCustomerIdValidation_InvalidIds() {
        // Given
        String[] invalidIds = {
            "",                    // 空字符串
            "0",                   // 零值
            "-1",                  // 负数
            "abc",                 // 非数字
            "1.5",                 // 小数
            "1 2",                 // 包含空格
            "999999999999999999"   // 超出范围
        };

        // When & Then
        for (String customerId : invalidIds) {
            testDeviceDTO.setCustomerId(customerId);
            
            assertThrows(ManagedDeviceException.class, 
                () -> validationService.validateCustomerId(customerId),
                "客户ID " + customerId + " 应该被识别为无效");
        }
    }

    @Test
    @DisplayName("XSS防护验证 - 恶意脚本检测")
    void testXSSProtection_MaliciousScripts() {
        // Given
        String[] xssPayloads = {
            "<script>alert('xss')</script>",
            "<img src=x onerror=alert('xss')>",
            "javascript:alert('xss')",
            "<svg onload=alert('xss')>",
            "';alert('xss');//",
            "<iframe src='javascript:alert(\"xss\")'></iframe>",
            "<body onload=alert('xss')>",
            "<div onclick='alert(\"xss\")'>click me</div>"
        };

        // When & Then
        for (String payload : xssPayloads) {
            testDeviceDTO.setNotes(payload);
            
            assertThrows(ManagedDeviceException.class, 
                () -> validationService.validateXSS(payload),
                "XSS载荷应该被检测并阻止: " + payload);
        }
    }

    @Test
    @DisplayName("XSS防护验证 - 安全内容通过")
    void testXSSProtection_SafeContent() {
        // Given
        String[] safeContents = {
            "这是一个正常的设备备注",
            "[测试客户]的设备",
            "设备序列号: YX-TEST-001",
            "固件版本 1.0.0 已安装",
            "设备状态正常，运行良好"
        };

        // When & Then
        for (String content : safeContents) {
            testDeviceDTO.setNotes(content);
            
            assertDoesNotThrow(
                () -> validationService.validateXSS(content),
                "安全内容应该通过验证: " + content);
        }
    }

    @Test
    @DisplayName("SQL注入防护验证 - 恶意SQL检测")
    void testSQLInjectionProtection_MaliciousSQL() {
        // Given
        String[] sqlPayloads = {
            "'; DROP TABLE devices; --",
            "1' OR '1'='1",
            "admin'--",
            "1'; DELETE FROM devices WHERE 1=1; --",
            "' UNION SELECT * FROM users --",
            "1' AND (SELECT COUNT(*) FROM devices) > 0 --",
            "'; INSERT INTO devices VALUES ('hack'); --",
            "1' OR 1=1 LIMIT 1 --"
        };

        // When & Then
        for (String payload : sqlPayloads) {
            testDeviceDTO.setSerialNumber(payload);
            
            assertThrows(ManagedDeviceException.class, 
                () -> validationService.validateSQLInjection(payload),
                "SQL注入载荷应该被检测并阻止: " + payload);
        }
    }

    @Test
    @DisplayName("SQL注入防护验证 - 安全内容通过")
    void testSQLInjectionProtection_SafeContent() {
        // Given
        String[] safeContents = {
            "YX-TEST-001",
            "YX-EDU-2024",
            "[测试客户]",
            "1.0.0",
            "正常的设备备注内容"
        };

        // When & Then
        for (String content : safeContents) {
            assertDoesNotThrow(
                () -> validationService.validateSQLInjection(content),
                "安全内容应该通过验证: " + content);
        }
    }

    @Test
    @DisplayName("PII数据保护验证 - 敏感信息检测")
    void testPIIProtection_SensitiveDataDetection() {
        // Given
        String[] piiData = {
            "张三",                    // 真实姓名
            "13812345678",            // 手机号
            "zhangsan@email.com",     // 邮箱
            "北京市朝阳区某某街道123号", // 详细地址
            "110101199001011234",     // 身份证号
            "6222021234567890123"     // 银行卡号
        };

        // When & Then
        for (String pii : piiData) {
            testDeviceDTO.setCustomerName(pii);
            
            // PII数据应该被替换为占位符
            String sanitized = validationService.sanitizePII(pii);
            assertNotEquals(pii, sanitized, 
                "PII数据应该被替换: " + pii);
            assertTrue(sanitized.startsWith("[") && sanitized.endsWith("]"),
                "PII数据应该被替换为占位符格式: " + sanitized);
        }
    }

    @Test
    @DisplayName("PII数据保护验证 - 非敏感信息通过")
    void testPIIProtection_NonSensitiveDataPass() {
        // Given
        String[] nonPiiData = {
            "[测试客户]",              // 已经是占位符
            "[客户姓名]",              // 通用占位符
            "YX-TEST-001",           // 设备序列号
            "测试设备",               // 通用描述
            "1.0.0"                  // 版本号
        };

        // When & Then
        for (String data : nonPiiData) {
            String sanitized = validationService.sanitizePII(data);
            // 非PII数据应该保持不变或已经是占位符格式
            assertTrue(data.equals(sanitized) || 
                      (sanitized.startsWith("[") && sanitized.endsWith("]")),
                "非PII数据处理结果: " + data + " -> " + sanitized);
        }
    }

    @Test
    @DisplayName("业务规则验证 - 设备状态转换")
    void testBusinessRuleValidation_StatusTransition() {
        // Given
        String[][] validTransitions = {
            {"offline", "online"},
            {"online", "offline"},
            {"online", "error"},
            {"online", "maintenance"},
            {"error", "maintenance"},
            {"error", "offline"},
            {"maintenance", "online"},
            {"maintenance", "offline"}
        };

        String[][] invalidTransitions = {
            {"offline", "error"},      // 离线不能直接变故障
            {"error", "online"},       // 故障不能直接变在线
            {"maintenance", "error"}   // 维护中不能直接变故障
        };

        // When & Then - 有效转换
        for (String[] transition : validTransitions) {
            String fromStatus = transition[0];
            String toStatus = transition[1];
            
            assertDoesNotThrow(
                () -> validationService.validateStatusTransition(fromStatus, toStatus),
                "状态转换应该有效: " + fromStatus + " -> " + toStatus);
        }

        // When & Then - 无效转换
        for (String[] transition : invalidTransitions) {
            String fromStatus = transition[0];
            String toStatus = transition[1];
            
            assertThrows(ManagedDeviceException.class,
                () -> validationService.validateStatusTransition(fromStatus, toStatus),
                "状态转换应该无效: " + fromStatus + " -> " + toStatus);
        }
    }

    @Test
    @DisplayName("边界值验证 - 字符串长度限制")
    void testBoundaryValidation_StringLengthLimits() {
        // Given
        String shortString = "A";
        String normalString = "YX-TEST-001";
        String longString = "A".repeat(1000);

        // When & Then - 序列号长度
        testDeviceDTO.setSerialNumber(shortString);
        assertThrows(ManagedDeviceException.class,
            () -> validationService.validateSerialNumber(shortString),
            "序列号太短应该被拒绝");

        testDeviceDTO.setSerialNumber(normalString);
        assertDoesNotThrow(
            () -> validationService.validateSerialNumber(normalString),
            "正常长度序列号应该通过");

        testDeviceDTO.setSerialNumber(longString);
        assertThrows(ManagedDeviceException.class,
            () -> validationService.validateSerialNumber(longString),
            "序列号太长应该被拒绝");
    }

    @Test
    @DisplayName("数据完整性验证 - 必填字段检查")
    void testDataIntegrityValidation_RequiredFields() {
        // Given
        ManagedDeviceDTO incompleteDTO = new ManagedDeviceDTO();

        // When & Then - 缺少序列号
        Set<ConstraintViolation<ManagedDeviceDTO>> violations = validator.validate(incompleteDTO);
        assertTrue(violations.size() > 0, "缺少必填字段应该产生验证错误");

        // 检查具体的必填字段错误
        boolean hasSerialNumberError = violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("serialNumber"));
        assertTrue(hasSerialNumberError, "应该检测到序列号缺失");

        boolean hasModelError = violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("model"));
        assertTrue(hasModelError, "应该检测到型号缺失");
    }

    @Test
    @DisplayName("性能验证 - 大量数据验证")
    void testPerformanceValidation_LargeDataSet() {
        // Given
        int testCount = 1000;
        long startTime = System.currentTimeMillis();

        // When
        for (int i = 0; i < testCount; i++) {
            ManagedDeviceDTO dto = new ManagedDeviceDTO();
            dto.setSerialNumber("YX-PERF-" + String.format("%06d", i));
            dto.setModel("YX-EDU-2024");
            dto.setStatus("online");
            dto.setFirmwareVersion("1.0.0");
            dto.setCustomerId(String.valueOf(i % 100 + 1));
            dto.setCustomerName("[测试客户" + (i % 100 + 1) + "]");
            dto.setCustomerPhone("[测试电话" + (i % 100 + 1) + "]");

            Set<ConstraintViolation<ManagedDeviceDTO>> violations = validator.validate(dto);
            assertEquals(0, violations.size(), "第" + i + "个对象验证应该通过");
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        // Then
        // 验证性能：1000个对象的验证应该在合理时间内完成（小于5秒）
        assertTrue(totalTime < 5000, 
            "验证" + testCount + "个对象应该在5秒内完成，实际用时：" + totalTime + "ms");
    }

    @Test
    @DisplayName("异常处理验证 - 空值处理")
    void testExceptionHandling_NullValues() {
        // When & Then
        assertThrows(IllegalArgumentException.class,
            () -> validationService.validateSerialNumber(null),
            "空序列号应该抛出IllegalArgumentException");

        assertThrows(IllegalArgumentException.class,
            () -> validationService.validateModel(null),
            "空型号应该抛出IllegalArgumentException");

        assertThrows(IllegalArgumentException.class,
            () -> validationService.validateStatus(null),
            "空状态应该抛出IllegalArgumentException");

        assertThrows(IllegalArgumentException.class,
            () -> validationService.validateFirmwareVersion(null),
            "空固件版本应该抛出IllegalArgumentException");
    }
}