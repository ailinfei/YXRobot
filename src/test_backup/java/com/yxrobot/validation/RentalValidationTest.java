package com.yxrobot.validation;

import com.yxrobot.entity.RentalCustomer;
import com.yxrobot.entity.RentalDevice;
import com.yxrobot.entity.RentalRecord;
import com.yxrobot.enums.CustomerType;
import com.yxrobot.exception.RentalException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 租赁数据验证测试类
 * 测试数据验证和异常处理功能
 * 
 * @author Kiro
 * @date 2025-01-28
 */
public class RentalValidationTest {
    
    @InjectMocks
    private RentalValidator rentalValidator;
    
    @Mock
    private RentalFormValidator rentalFormValidator;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rentalValidator = new RentalValidator();
        rentalFormValidator = new RentalFormValidator();
        rentalFormValidator.rentalValidator = rentalValidator;
    }
    
    /**
     * 测试分页参数验证
     */
    @Test
    void testValidatePaginationParams() {
        // 正常情况
        assertDoesNotThrow(() -> rentalValidator.validatePaginationParams(1, 20));
        assertDoesNotThrow(() -> rentalValidator.validatePaginationParams(null, null));
        
        // 异常情况
        assertThrows(RentalException.class, () -> rentalValidator.validatePaginationParams(0, 20));
        assertThrows(RentalException.class, () -> rentalValidator.validatePaginationParams(-1, 20));
        assertThrows(RentalException.class, () -> rentalValidator.validatePaginationParams(1, 0));
        assertThrows(RentalException.class, () -> rentalValidator.validatePaginationParams(1, 101));
    }
    
    /**
     * 测试日期参数验证
     */
    @Test
    void testValidateDateParam() {
        // 正常情况
        LocalDate date = rentalValidator.validateDateParam("2025-01-28", "testDate");
        assertEquals(LocalDate.of(2025, 1, 28), date);
        
        assertNull(rentalValidator.validateDateParam("", "testDate"));
        assertNull(rentalValidator.validateDateParam(null, "testDate"));
        
        // 异常情况
        assertThrows(RentalException.class, () -> rentalValidator.validateDateParam("2025-13-01", "testDate"));
        assertThrows(RentalException.class, () -> rentalValidator.validateDateParam("invalid-date", "testDate"));
    }
    
    /**
     * 测试日期范围验证
     */
    @Test
    void testValidateDateRange() {
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 1, 31);
        
        // 正常情况
        assertDoesNotThrow(() -> rentalValidator.validateDateRange(start, end));
        assertDoesNotThrow(() -> rentalValidator.validateDateRange(null, null));
        assertDoesNotThrow(() -> rentalValidator.validateDateRange(start, null));
        
        // 异常情况 - 开始日期晚于结束日期
        assertThrows(RentalException.class, () -> rentalValidator.validateDateRange(end, start));
        
        // 异常情况 - 日期范围超过2年
        LocalDate tooLateEnd = start.plusYears(3);
        assertThrows(RentalException.class, () -> rentalValidator.validateDateRange(start, tooLateEnd));
    }
    
    /**
     * 测试设备状态验证
     */
    @Test
    void testValidateDeviceStatus() {
        // 正常情况
        assertDoesNotThrow(() -> rentalValidator.validateDeviceStatus("active"));
        assertDoesNotThrow(() -> rentalValidator.validateDeviceStatus("idle"));
        assertDoesNotThrow(() -> rentalValidator.validateDeviceStatus("maintenance"));
        assertDoesNotThrow(() -> rentalValidator.validateDeviceStatus("retired"));
        assertDoesNotThrow(() -> rentalValidator.validateDeviceStatus(""));
        assertDoesNotThrow(() -> rentalValidator.validateDeviceStatus(null));
        
        // 异常情况
        assertThrows(RentalException.class, () -> rentalValidator.validateDeviceStatus("invalid"));
        assertThrows(RentalException.class, () -> rentalValidator.validateDeviceStatus("unknown"));
    }
    
    /**
     * 测试搜索关键词验证
     */
    @Test
    void testValidateSearchKeyword() {
        // 正常情况
        assertDoesNotThrow(() -> rentalValidator.validateSearchKeyword("YX-0001"));
        assertDoesNotThrow(() -> rentalValidator.validateSearchKeyword("设备"));
        assertDoesNotThrow(() -> rentalValidator.validateSearchKeyword(""));
        assertDoesNotThrow(() -> rentalValidator.validateSearchKeyword(null));
        
        // 异常情况 - 长度超限
        String longKeyword = "a".repeat(101);
        assertThrows(RentalException.class, () -> rentalValidator.validateSearchKeyword(longKeyword));
        
        // 异常情况 - 包含特殊字符
        assertThrows(RentalException.class, () -> rentalValidator.validateSearchKeyword("test<script>"));
        assertThrows(RentalException.class, () -> rentalValidator.validateSearchKeyword("test'or'1=1"));
    }
    
    /**
     * 测试客户名称验证
     */
    @Test
    void testValidateCustomerName() {
        // 正常情况
        assertDoesNotThrow(() -> rentalValidator.validateCustomerName("测试客户"));
        assertDoesNotThrow(() -> rentalValidator.validateCustomerName("Test Customer"));
        
        // 异常情况 - 空值
        assertThrows(RentalException.class, () -> rentalValidator.validateCustomerName(""));
        assertThrows(RentalException.class, () -> rentalValidator.validateCustomerName(null));
        
        // 异常情况 - 长度超限
        String longName = "a".repeat(201);
        assertThrows(RentalException.class, () -> rentalValidator.validateCustomerName(longName));
        
        // 异常情况 - 包含特殊字符
        assertThrows(RentalException.class, () -> rentalValidator.validateCustomerName("客户<script>"));
    }
    
    /**
     * 测试手机号验证
     */
    @Test
    void testValidatePhone() {
        // 正常情况
        assertDoesNotThrow(() -> rentalValidator.validatePhone("13812345678"));
        assertDoesNotThrow(() -> rentalValidator.validatePhone("010-12345678"));
        assertDoesNotThrow(() -> rentalValidator.validatePhone(""));
        assertDoesNotThrow(() -> rentalValidator.validatePhone(null));
        
        // 异常情况
        assertThrows(RentalException.class, () -> rentalValidator.validatePhone("12345678901"));
        assertThrows(RentalException.class, () -> rentalValidator.validatePhone("1234567890"));
        assertThrows(RentalException.class, () -> rentalValidator.validatePhone("abc12345678"));
    }
    
    /**
     * 测试邮箱验证
     */
    @Test
    void testValidateEmail() {
        // 正常情况
        assertDoesNotThrow(() -> rentalValidator.validateEmail("test@example.com"));
        assertDoesNotThrow(() -> rentalValidator.validateEmail("user.name@domain.co.uk"));
        assertDoesNotThrow(() -> rentalValidator.validateEmail(""));
        assertDoesNotThrow(() -> rentalValidator.validateEmail(null));
        
        // 异常情况
        assertThrows(RentalException.class, () -> rentalValidator.validateEmail("invalid-email"));
        assertThrows(RentalException.class, () -> rentalValidator.validateEmail("test@"));
        assertThrows(RentalException.class, () -> rentalValidator.validateEmail("@example.com"));
        
        // 异常情况 - 长度超限
        String longEmail = "a".repeat(90) + "@example.com";
        assertThrows(RentalException.class, () -> rentalValidator.validateEmail(longEmail));
    }
    
    /**
     * 测试设备型号验证
     */
    @Test
    void testValidateDeviceModel() {
        // 正常情况
        assertDoesNotThrow(() -> rentalValidator.validateDeviceModel("YX-Robot-Pro"));
        assertDoesNotThrow(() -> rentalValidator.validateDeviceModel("YX-Robot-Standard"));
        assertDoesNotThrow(() -> rentalValidator.validateDeviceModel("YX-Robot-Lite"));
        assertDoesNotThrow(() -> rentalValidator.validateDeviceModel("YX-Robot-Mini"));
        
        // 异常情况
        assertThrows(RentalException.class, () -> rentalValidator.validateDeviceModel(""));
        assertThrows(RentalException.class, () -> rentalValidator.validateDeviceModel(null));
        assertThrows(RentalException.class, () -> rentalValidator.validateDeviceModel("Invalid-Model"));
    }
    
    /**
     * 测试批量操作参数验证
     */
    @Test
    void testValidateBatchOperationParams() {
        List<String> validIds = Arrays.asList("YX-0001", "YX-0002");
        
        // 正常情况
        assertDoesNotThrow(() -> rentalValidator.validateBatchOperationParams(validIds, "updateStatus"));
        assertDoesNotThrow(() -> rentalValidator.validateBatchOperationParams(validIds, "maintenance"));
        assertDoesNotThrow(() -> rentalValidator.validateBatchOperationParams(validIds, "delete"));
        
        // 异常情况 - 空ID列表
        assertThrows(RentalException.class, () -> rentalValidator.validateBatchOperationParams(null, "updateStatus"));
        assertThrows(RentalException.class, () -> rentalValidator.validateBatchOperationParams(Arrays.asList(), "updateStatus"));
        
        // 异常情况 - 空操作类型
        assertThrows(RentalException.class, () -> rentalValidator.validateBatchOperationParams(validIds, ""));
        assertThrows(RentalException.class, () -> rentalValidator.validateBatchOperationParams(validIds, null));
        
        // 异常情况 - 无效操作类型
        assertThrows(RentalException.class, () -> rentalValidator.validateBatchOperationParams(validIds, "invalidOperation"));
        
        // 异常情况 - ID数量超限
        List<String> tooManyIds = Arrays.asList(new String[101]);
        assertThrows(RentalException.class, () -> rentalValidator.validateBatchOperationParams(tooManyIds, "updateStatus"));
    }
    
    /**
     * 测试客户表单验证
     */
    @Test
    void testValidateCustomerForm() {
        // 创建有效的客户对象
        RentalCustomer validCustomer = new RentalCustomer();
        validCustomer.setCustomerName("测试客户");
        validCustomer.setCustomerType(CustomerType.ENTERPRISE);
        validCustomer.setPhone("13812345678");
        validCustomer.setEmail("test@example.com");
        validCustomer.setRegion("北京市");
        
        List<String> errors = rentalFormValidator.validateCustomerForm(validCustomer);
        assertTrue(errors.isEmpty(), "有效客户数据不应该有验证错误");
        
        // 测试无效客户对象
        RentalCustomer invalidCustomer = new RentalCustomer();
        // 缺少必填字段
        
        errors = rentalFormValidator.validateCustomerForm(invalidCustomer);
        assertFalse(errors.isEmpty(), "无效客户数据应该有验证错误");
        assertTrue(errors.stream().anyMatch(error -> error.contains("客户名称不能为空")));
        assertTrue(errors.stream().anyMatch(error -> error.contains("客户类型不能为空")));
    }
    
    /**
     * 测试设备表单验证
     */
    @Test
    void testValidateDeviceForm() {
        // 创建有效的设备对象
        RentalDevice validDevice = new RentalDevice();
        validDevice.setDeviceId("YX-0001");
        validDevice.setDeviceModel("YX-Robot-Pro");
        validDevice.setDeviceName("练字机器人Pro");
        validDevice.setDailyRentalPrice(new BigDecimal("100.00"));
        validDevice.setPerformanceScore(95);
        validDevice.setSignalStrength(85);
        
        List<String> errors = rentalFormValidator.validateDeviceForm(validDevice);
        assertTrue(errors.isEmpty(), "有效设备数据不应该有验证错误");
        
        // 测试无效设备对象
        RentalDevice invalidDevice = new RentalDevice();
        // 缺少必填字段
        
        errors = rentalFormValidator.validateDeviceForm(invalidDevice);
        assertFalse(errors.isEmpty(), "无效设备数据应该有验证错误");
        assertTrue(errors.stream().anyMatch(error -> error.contains("设备ID不能为空")));
        assertTrue(errors.stream().anyMatch(error -> error.contains("设备型号不能为空")));
        assertTrue(errors.stream().anyMatch(error -> error.contains("设备名称不能为空")));
        assertTrue(errors.stream().anyMatch(error -> error.contains("日租金不能为空")));
    }
    
    /**
     * 测试批量操作表单验证
     */
    @Test
    void testValidateBatchOperationForm() {
        // 创建有效的批量操作数据
        Map<String, Object> validBatchData = new HashMap<>();
        validBatchData.put("ids", Arrays.asList("YX-0001", "YX-0002"));
        validBatchData.put("operation", "updateStatus");
        
        Map<String, Object> params = new HashMap<>();
        params.put("status", "maintenance");
        validBatchData.put("params", params);
        
        List<String> errors = rentalFormValidator.validateBatchOperationForm(validBatchData);
        assertTrue(errors.isEmpty(), "有效批量操作数据不应该有验证错误");
        
        // 测试无效批量操作数据
        Map<String, Object> invalidBatchData = new HashMap<>();
        invalidBatchData.put("ids", Arrays.asList());
        invalidBatchData.put("operation", "");
        
        errors = rentalFormValidator.validateBatchOperationForm(invalidBatchData);
        assertFalse(errors.isEmpty(), "无效批量操作数据应该有验证错误");
    }
    
    /**
     * 测试查询参数表单验证
     */
    @Test
    void testValidateQueryParamsForm() {
        // 创建有效的查询参数
        Map<String, Object> validParams = new HashMap<>();
        validParams.put("page", 1);
        validParams.put("pageSize", 20);
        validParams.put("keyword", "YX-0001");
        validParams.put("currentStatus", "active");
        validParams.put("startDate", "2025-01-01");
        validParams.put("endDate", "2025-01-31");
        
        List<String> errors = rentalFormValidator.validateQueryParamsForm(validParams);
        assertTrue(errors.isEmpty(), "有效查询参数不应该有验证错误");
        
        // 测试无效查询参数
        Map<String, Object> invalidParams = new HashMap<>();
        invalidParams.put("page", 0);
        invalidParams.put("pageSize", 101);
        invalidParams.put("currentStatus", "invalid");
        invalidParams.put("startDate", "invalid-date");
        
        errors = rentalFormValidator.validateQueryParamsForm(invalidParams);
        assertFalse(errors.isEmpty(), "无效查询参数应该有验证错误");
    }
    
    /**
     * 测试异常抛出机制
     */
    @Test
    void testValidateAndThrow() {
        List<String> errors = Arrays.asList("错误1", "错误2");
        
        RentalException exception = assertThrows(RentalException.class, 
            () -> rentalFormValidator.validateAndThrow(errors, "测试"));
        
        assertEquals(RentalException.ErrorCodes.DATA_VALIDATION_ERROR, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("测试表单验证失败"));
        assertTrue(exception.getErrorDetail().contains("验证错误数量: 2"));
        
        // 测试无错误情况
        assertDoesNotThrow(() -> rentalFormValidator.validateAndThrow(Arrays.asList(), "测试"));
    }
}