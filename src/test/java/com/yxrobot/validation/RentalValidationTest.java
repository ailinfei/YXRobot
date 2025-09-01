package com.yxrobot.validation;

import com.yxrobot.entity.RentalRecord;
import com.yxrobot.entity.RentalCustomer;
import com.yxrobot.entity.RentalDevice;
import com.yxrobot.exception.RentalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 租赁验证功能测试类
 * 测试数据验证功能的正确性和异常处理
 * 
 * @author Kiro
 * @date 2025-01-28
 */
@ExtendWith(MockitoExtension.class)
class RentalValidationTest {
    
    @Mock
    private RentalValidator rentalValidator;
    
    @InjectMocks
    private RentalFormValidator rentalFormValidator;
    
    private RentalValidator realValidator;
    private RentalRecord testRentalRecord;
    private RentalCustomer testCustomer;
    private RentalDevice testDevice;
    
    @BeforeEach
    void setUp() {
        // 创建真实的验证器实例用于测试
        realValidator = new RentalValidator();
        
        // 创建测试用的租赁记录
        testRentalRecord = new RentalRecord();
        testRentalRecord.setRentalOrderNumber("TEST20250128001");
        testRentalRecord.setDeviceId(1L);
        testRentalRecord.setCustomerId(1L);
        testRentalRecord.setRentalStartDate(LocalDate.of(2025, 1, 28));
        testRentalRecord.setRentalEndDate(LocalDate.of(2025, 2, 12));
        testRentalRecord.setRentalPeriod(15);
        testRentalRecord.setDailyRentalFee(new BigDecimal("100.00"));
        testRentalRecord.setTotalRentalFee(new BigDecimal("1500.00"));
        testRentalRecord.setDepositAmount(new BigDecimal("500.00"));
        testRentalRecord.setActualPayment(new BigDecimal("2000.00"));
        testRentalRecord.setCreatedAt(LocalDateTime.now());
        testRentalRecord.setUpdatedAt(LocalDateTime.now());
        
        // 创建测试用的客户
        testCustomer = new RentalCustomer();
        testCustomer.setCustomerName("测试客户");
        testCustomer.setPhone("13800138000");
        testCustomer.setEmail("test@example.com");
        testCustomer.setAddress("北京市朝阳区测试街道123号");
        testCustomer.setRegion("北京");
        testCustomer.setIndustry("科技");
        testCustomer.setCreditLevel("A");
        
        // 创建测试用的设备
        testDevice = new RentalDevice();
        testDevice.setDeviceId("YX-TEST-001");
        testDevice.setDeviceModel("YX-Robot-Pro");
        testDevice.setDeviceName("测试智能机器人");
        testDevice.setDailyRentalPrice(new BigDecimal("100.00"));
        testDevice.setPerformanceScore(85);
        testDevice.setSignalStrength(90);
    }
    
    // ========== RentalValidator 测试 ==========
    
    @Test
    void testValidatePaginationParams_WithValidParams_ShouldNotThrowException() {
        // Given
        Integer page = 1;
        Integer pageSize = 20;
        
        // When & Then
        assertDoesNotThrow(() -> realValidator.validatePaginationParams(page, pageSize));
    }
    
    @Test
    void testValidatePaginationParams_WithInvalidPage_ShouldThrowException() {
        // Given
        Integer page = 0;
        Integer pageSize = 20;
        
        // When & Then
        RentalException exception = assertThrows(RentalException.class, 
            () -> realValidator.validatePaginationParams(page, pageSize));
        
        assertTrue(exception.getMessage().contains("page"));
    }
    
    @Test
    void testValidatePaginationParams_WithInvalidPageSize_ShouldThrowException() {
        // Given
        Integer page = 1;
        Integer pageSize = 101; // 超过最大限制
        
        // When & Then
        RentalException exception = assertThrows(RentalException.class, 
            () -> realValidator.validatePaginationParams(page, pageSize));
        
        assertTrue(exception.getMessage().contains("pageSize"));
    }
    
    @Test
    void testValidateDateParam_WithValidDate_ShouldReturnLocalDate() {
        // Given
        String dateStr = "2025-01-28";
        
        // When
        LocalDate result = realValidator.validateDateParam(dateStr, "testDate");
        
        // Then
        assertNotNull(result);
        assertEquals(LocalDate.of(2025, 1, 28), result);
    }
    
    @Test
    void testValidateDateParam_WithInvalidDate_ShouldThrowException() {
        // Given
        String dateStr = "invalid-date";
        
        // When & Then
        RentalException exception = assertThrows(RentalException.class, 
            () -> realValidator.validateDateParam(dateStr, "testDate"));
        
        assertTrue(exception.getMessage().contains("testDate"));
    }
    
    @Test
    void testValidateDateParam_WithNullDate_ShouldReturnNull() {
        // When
        LocalDate result = realValidator.validateDateParam(null, "testDate");
        
        // Then
        assertNull(result);
    }
    
    @Test
    void testValidateDateRange_WithValidRange_ShouldNotThrowException() {
        // Given
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);
        
        // When & Then
        assertDoesNotThrow(() -> realValidator.validateDateRange(startDate, endDate));
    }
    
    @Test
    void testValidateDateRange_WithInvalidRange_ShouldThrowException() {
        // Given
        LocalDate startDate = LocalDate.of(2025, 1, 31);
        LocalDate endDate = LocalDate.of(2025, 1, 1); // 结束日期早于开始日期
        
        // When & Then
        RentalException exception = assertThrows(RentalException.class, 
            () -> realValidator.validateDateRange(startDate, endDate));
        
        assertTrue(exception.getMessage().contains("开始日期不能晚于结束日期"));
    }
    
    @Test
    void testValidateDateRange_WithTooLargeRange_ShouldThrowException() {
        // Given
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 12, 31); // 超过2年
        
        // When & Then
        RentalException exception = assertThrows(RentalException.class, 
            () -> realValidator.validateDateRange(startDate, endDate));
        
        assertTrue(exception.getMessage().contains("日期范围不能超过2年"));
    }
    
    @Test
    void testValidatePeriodParam_WithValidPeriod_ShouldNotThrowException() {
        // Given
        String period = "daily";
        
        // When & Then
        assertDoesNotThrow(() -> realValidator.validatePeriodParam(period));
    }
    
    @Test
    void testValidatePeriodParam_WithInvalidPeriod_ShouldThrowException() {
        // Given
        String period = "invalid";
        
        // When & Then
        RentalException exception = assertThrows(RentalException.class, 
            () -> realValidator.validatePeriodParam(period));
        
        assertTrue(exception.getMessage().contains("period"));
    }
    
    @Test
    void testValidateDeviceStatus_WithValidStatus_ShouldNotThrowException() {
        // Given
        String status = "active";
        
        // When & Then
        assertDoesNotThrow(() -> realValidator.validateDeviceStatus(status));
    }
    
    @Test
    void testValidateDeviceStatus_WithInvalidStatus_ShouldThrowException() {
        // Given
        String status = "invalid";
        
        // When & Then
        RentalException exception = assertThrows(RentalException.class, 
            () -> realValidator.validateDeviceStatus(status));
        
        assertTrue(exception.getMessage().contains("currentStatus"));
    }
    
    @Test
    void testValidateDeviceId_WithValidId_ShouldNotThrowException() {
        // Given
        String deviceId = "YX-001";
        
        // When & Then
        assertDoesNotThrow(() -> realValidator.validateDeviceId(deviceId));
    }
    
    @Test
    void testValidateDeviceId_WithEmptyId_ShouldThrowException() {
        // Given
        String deviceId = "";
        
        // When & Then
        RentalException exception = assertThrows(RentalException.class, 
            () -> realValidator.validateDeviceId(deviceId));
        
        assertTrue(exception.getMessage().contains("设备ID不能为空"));
    }
    
    @Test
    void testValidateDeviceId_WithTooLongId_ShouldThrowException() {
        // Given
        String deviceId = "A".repeat(51); // 超过50个字符
        
        // When & Then
        RentalException exception = assertThrows(RentalException.class, 
            () -> realValidator.validateDeviceId(deviceId));
        
        assertTrue(exception.getMessage().contains("设备ID长度不能超过50个字符"));
    }
    
    @Test
    void testValidateCustomerId_WithValidId_ShouldNotThrowException() {
        // Given
        Long customerId = 1L;
        
        // When & Then
        assertDoesNotThrow(() -> realValidator.validateCustomerId(customerId));
    }
    
    @Test
    void testValidateCustomerId_WithInvalidId_ShouldThrowException() {
        // Given
        Long customerId = 0L;
        
        // When & Then
        RentalException exception = assertThrows(RentalException.class, 
            () -> realValidator.validateCustomerId(customerId));
        
        assertTrue(exception.getMessage().contains("customerId"));
    }
    
    @Test
    void testValidateRentalPeriod_WithValidPeriod_ShouldNotThrowException() {
        // Given
        Integer period = 15;
        
        // When & Then
        assertDoesNotThrow(() -> realValidator.validateRentalPeriod(period));
    }
    
    @Test
    void testValidateRentalPeriod_WithInvalidPeriod_ShouldThrowException() {
        // Given
        Integer period = 0;
        
        // When & Then
        RentalException exception = assertThrows(RentalException.class, 
            () -> realValidator.validateRentalPeriod(period));
        
        assertTrue(exception.getMessage().contains("租赁期间"));
    }
    
    @Test
    void testValidateRentalFee_WithValidFee_ShouldNotThrowException() {
        // Given
        BigDecimal fee = new BigDecimal("100.00");
        
        // When & Then
        assertDoesNotThrow(() -> realValidator.validateRentalFee(fee));
    }
    
    @Test
    void testValidateRentalFee_WithInvalidFee_ShouldThrowException() {
        // Given
        BigDecimal fee = BigDecimal.ZERO;
        
        // When & Then
        RentalException exception = assertThrows(RentalException.class, 
            () -> realValidator.validateRentalFee(fee));
        
        assertTrue(exception.getMessage().contains("rentalFee"));
    }
    
    @Test
    void testValidateSearchKeyword_WithValidKeyword_ShouldNotThrowException() {
        // Given
        String keyword = "YX-Robot";
        
        // When & Then
        assertDoesNotThrow(() -> realValidator.validateSearchKeyword(keyword));
    }
    
    @Test
    void testValidateSearchKeyword_WithTooLongKeyword_ShouldThrowException() {
        // Given
        String keyword = "A".repeat(101); // 超过100个字符
        
        // When & Then
        RentalException exception = assertThrows(RentalException.class, 
            () -> realValidator.validateSearchKeyword(keyword));
        
        assertTrue(exception.getMessage().contains("搜索关键词长度不能超过100个字符"));
    }
    
    @Test
    void testValidateSearchKeyword_WithSpecialCharacters_ShouldThrowException() {
        // Given
        String keyword = "test<script>";
        
        // When & Then
        RentalException exception = assertThrows(RentalException.class, 
            () -> realValidator.validateSearchKeyword(keyword));
        
        assertTrue(exception.getMessage().contains("搜索关键词包含非法字符"));
    }
    
    @Test
    void testValidatePhone_WithValidPhone_ShouldNotThrowException() {
        // Given
        String phone = "13800138000";
        
        // When & Then
        assertDoesNotThrow(() -> realValidator.validatePhone(phone));
    }
    
    @Test
    void testValidatePhone_WithInvalidPhone_ShouldThrowException() {
        // Given
        String phone = "12345";
        
        // When & Then
        RentalException exception = assertThrows(RentalException.class, 
            () -> realValidator.validatePhone(phone));
        
        assertTrue(exception.getMessage().contains("手机号"));
    }
    
    @Test
    void testValidateEmail_WithValidEmail_ShouldNotThrowException() {
        // Given
        String email = "test@example.com";
        
        // When & Then
        assertDoesNotThrow(() -> realValidator.validateEmail(email));
    }
    
    @Test
    void testValidateEmail_WithInvalidEmail_ShouldThrowException() {
        // Given
        String email = "invalid-email";
        
        // When & Then
        RentalException exception = assertThrows(RentalException.class, 
            () -> realValidator.validateEmail(email));
        
        assertTrue(exception.getMessage().contains("邮箱"));
    }
    
    @Test
    void testValidateDeviceModel_WithValidModel_ShouldNotThrowException() {
        // Given
        String deviceModel = "YX-Robot-Pro";
        
        // When & Then
        assertDoesNotThrow(() -> realValidator.validateDeviceModel(deviceModel));
    }
    
    @Test
    void testValidateDeviceModel_WithInvalidModel_ShouldThrowException() {
        // Given
        String deviceModel = "Invalid-Model";
        
        // When & Then
        RentalException exception = assertThrows(RentalException.class, 
            () -> realValidator.validateDeviceModel(deviceModel));
        
        assertTrue(exception.getMessage().contains("设备型号"));
    }
    
    @Test
    void testValidateCompleteRentalRecordData_WithValidData_ShouldNotThrowException() {
        // Given
        String orderNumber = "TEST20250128001";
        String deviceId = "YX-001";
        Long customerId = 1L;
        LocalDate startDate = LocalDate.of(2025, 1, 28);
        LocalDate endDate = LocalDate.of(2025, 2, 12);
        Integer period = 15;
        BigDecimal dailyFee = new BigDecimal("100.00");
        BigDecimal totalFee = new BigDecimal("1500.00");
        
        // When & Then
        assertDoesNotThrow(() -> realValidator.validateRentalRecordData(
            orderNumber, deviceId, customerId, startDate, endDate, period, dailyFee, totalFee));
    }
    
    @Test
    void testValidateCompleteRentalRecordData_WithIncorrectTotalFee_ShouldThrowException() {
        // Given
        String orderNumber = "TEST20250128001";
        String deviceId = "YX-001";
        Long customerId = 1L;
        LocalDate startDate = LocalDate.of(2025, 1, 28);
        LocalDate endDate = LocalDate.of(2025, 2, 12);
        Integer period = 15;
        BigDecimal dailyFee = new BigDecimal("100.00");
        BigDecimal totalFee = new BigDecimal("1000.00"); // 错误的总费用
        
        // When & Then
        RentalException exception = assertThrows(RentalException.class, 
            () -> realValidator.validateRentalRecordData(
                orderNumber, deviceId, customerId, startDate, endDate, period, dailyFee, totalFee));
        
        assertTrue(exception.getMessage().contains("总费用计算不正确"));
    }
    
    // ========== RentalFormValidator 测试 ==========
    
    @Test
    void testValidateRentalRecordForm_WithValidRecord_ShouldReturnNoErrors() {
        // Given
        // testRentalRecord 已在setUp中设置为有效数据
        
        // When
        List<String> errors = rentalFormValidator.validateRentalRecordForm(testRentalRecord);
        
        // Then
        assertTrue(errors.isEmpty());
    }
    
    @Test
    void testValidateRentalRecordForm_WithNullRecord_ShouldReturnError() {
        // When
        List<String> errors = rentalFormValidator.validateRentalRecordForm(null);
        
        // Then
        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).contains("租赁记录数据不能为空"));
    }
    
    @Test
    void testValidateRentalRecordForm_WithMissingRequiredFields_ShouldReturnErrors() {
        // Given
        RentalRecord invalidRecord = new RentalRecord();
        // 不设置必填字段
        
        // When
        List<String> errors = rentalFormValidator.validateRentalRecordForm(invalidRecord);
        
        // Then
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(error -> error.contains("租赁订单号不能为空")));
        assertTrue(errors.stream().anyMatch(error -> error.contains("设备ID不能为空")));
        assertTrue(errors.stream().anyMatch(error -> error.contains("客户ID不能为空")));
    }
    
    @Test
    void testValidateCustomerForm_WithValidCustomer_ShouldReturnNoErrors() {
        // When
        List<String> errors = rentalFormValidator.validateCustomerForm(testCustomer);
        
        // Then
        assertTrue(errors.isEmpty());
    }
    
    @Test
    void testValidateCustomerForm_WithNullCustomer_ShouldReturnError() {
        // When
        List<String> errors = rentalFormValidator.validateCustomerForm(null);
        
        // Then
        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).contains("客户数据不能为空"));
    }
    
    @Test
    void testValidateDeviceForm_WithValidDevice_ShouldReturnNoErrors() {
        // When
        List<String> errors = rentalFormValidator.validateDeviceForm(testDevice);
        
        // Then
        assertTrue(errors.isEmpty());
    }
    
    @Test
    void testValidateDeviceForm_WithNullDevice_ShouldReturnError() {
        // When
        List<String> errors = rentalFormValidator.validateDeviceForm(null);
        
        // Then
        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).contains("设备数据不能为空"));
    }
    
    @Test
    void testValidateBatchOperationForm_WithValidData_ShouldReturnNoErrors() {
        // Given
        Map<String, Object> batchData = new HashMap<>();
        batchData.put("ids", Arrays.asList("YX-001", "YX-002"));
        batchData.put("operation", "updateStatus");
        batchData.put("params", Map.of("status", "maintenance"));
        
        // When
        List<String> errors = rentalFormValidator.validateBatchOperationForm(batchData);
        
        // Then
        assertTrue(errors.isEmpty());
    }
    
    @Test
    void testValidateBatchOperationForm_WithNullData_ShouldReturnError() {
        // When
        List<String> errors = rentalFormValidator.validateBatchOperationForm(null);
        
        // Then
        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).contains("批量操作数据不能为空"));
    }
    
    @Test
    void testValidateBatchOperationForm_WithMissingStatus_ShouldReturnError() {
        // Given
        Map<String, Object> batchData = new HashMap<>();
        batchData.put("ids", Arrays.asList("YX-001", "YX-002"));
        batchData.put("operation", "updateStatus");
        batchData.put("params", new HashMap<>()); // 缺少status参数
        
        // When
        List<String> errors = rentalFormValidator.validateBatchOperationForm(batchData);
        
        // Then
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(error -> error.contains("更新状态操作需要提供新状态参数")));
    }
    
    @Test
    void testValidateQueryParamsForm_WithValidParams_ShouldReturnNoErrors() {
        // Given
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("page", 1);
        queryParams.put("pageSize", 20);
        queryParams.put("keyword", "YX");
        queryParams.put("currentStatus", "active");
        queryParams.put("startDate", "2025-01-01");
        queryParams.put("endDate", "2025-01-31");
        
        // When
        List<String> errors = rentalFormValidator.validateQueryParamsForm(queryParams);
        
        // Then
        assertTrue(errors.isEmpty());
    }
    
    @Test
    void testValidateAndThrow_WithErrors_ShouldThrowException() {
        // Given
        List<String> errors = Arrays.asList("错误1", "错误2");
        String formType = "测试表单";
        
        // When & Then
        RentalException exception = assertThrows(RentalException.class, 
            () -> rentalFormValidator.validateAndThrow(errors, formType));
        
        assertTrue(exception.getMessage().contains("测试表单表单验证失败"));
        assertTrue(exception.getMessage().contains("错误1"));
        assertTrue(exception.getMessage().contains("错误2"));
    }
    
    @Test
    void testValidateAndThrow_WithNoErrors_ShouldNotThrowException() {
        // Given
        List<String> errors = Arrays.asList();
        String formType = "测试表单";
        
        // When & Then
        assertDoesNotThrow(() -> rentalFormValidator.validateAndThrow(errors, formType));
    }
    
    @Test
    void testBusinessLogicValidation_WithIncorrectFeeCalculation_ShouldReturnError() {
        // Given
        testRentalRecord.setDailyRentalFee(new BigDecimal("100.00"));
        testRentalRecord.setRentalPeriod(15);
        testRentalRecord.setTotalRentalFee(new BigDecimal("1000.00")); // 错误的总费用
        
        // When
        List<String> errors = rentalFormValidator.validateRentalRecordForm(testRentalRecord);
        
        // Then
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(error -> error.contains("总租金计算不正确")));
    }
    
    @Test
    void testBusinessLogicValidation_WithInvalidDateRange_ShouldReturnError() {
        // Given
        testRentalRecord.setRentalStartDate(LocalDate.of(2025, 1, 31));
        testRentalRecord.setRentalEndDate(LocalDate.of(2025, 1, 1)); // 结束日期早于开始日期
        
        // When
        List<String> errors = rentalFormValidator.validateRentalRecordForm(testRentalRecord);
        
        // Then
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(error -> error.contains("租赁开始日期不能晚于结束日期")));
    }
    
    @Test
    void testBusinessLogicValidation_WithNegativeDeposit_ShouldReturnError() {
        // Given
        testRentalRecord.setDepositAmount(new BigDecimal("-100.00")); // 负数押金
        
        // When
        List<String> errors = rentalFormValidator.validateRentalRecordForm(testRentalRecord);
        
        // Then
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(error -> error.contains("押金金额不能为负数")));
    }
    
    @Test
    void testEdgeCaseValidation_WithBoundaryValues_ShouldHandleCorrectly() {
        // Given - 测试边界值
        
        // 测试最小有效租赁期间
        assertDoesNotThrow(() -> realValidator.validateRentalPeriod(1));
        
        // 测试最大有效租赁期间
        assertDoesNotThrow(() -> realValidator.validateRentalPeriod(365));
        
        // 测试超出边界的值
        assertThrows(RentalException.class, () -> realValidator.validateRentalPeriod(366));
        
        // 测试最小有效租金
        assertDoesNotThrow(() -> realValidator.validateRentalFee(new BigDecimal("0.01")));
        
        // 测试最大有效租金
        assertDoesNotThrow(() -> realValidator.validateRentalFee(new BigDecimal("999999.99")));
        
        // 测试超出边界的租金
        assertThrows(RentalException.class, 
            () -> realValidator.validateRentalFee(new BigDecimal("1000000.00")));
    }
    
    @Test
    void testConcurrentValidation_ShouldBeThreadSafe() throws InterruptedException {
        // Given
        int threadCount = 10;
        Thread[] threads = new Thread[threadCount];
        boolean[] results = new boolean[threadCount];
        
        // When - 并发执行验证
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                try {
                    realValidator.validateDeviceId("YX-" + String.format("%03d", index));
                    realValidator.validateRentalPeriod(15);
                    realValidator.validateRentalFee(new BigDecimal("100.00"));
                    results[index] = true;
                } catch (Exception e) {
                    results[index] = false;
                }
            });
            threads[i].start();
        }
        
        // 等待所有线程完成
        for (Thread thread : threads) {
            thread.join();
        }
        
        // Then - 验证所有线程都成功执行
        for (boolean result : results) {
            assertTrue(result);
        }
    }
}