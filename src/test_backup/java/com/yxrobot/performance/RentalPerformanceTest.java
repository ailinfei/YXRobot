package com.yxrobot.performance;

import com.yxrobot.cache.RentalCacheService;
import com.yxrobot.validation.RentalValidator;
import com.yxrobot.exception.RentalException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 租赁模块性能优化测试
 * 验证性能优化和错误处理功能
 * 
 * @author Kiro
 * @date 2025-01-28
 */
@ExtendWith(MockitoExtension.class)
class RentalPerformanceTest {
    
    @InjectMocks
    private RentalValidator rentalValidator;
    
    @InjectMocks
    private RentalCacheService cacheService;
    
    /**
     * 测试参数验证功能
     */
    @Test
    void testParameterValidation() {
        // 测试分页参数验证
        assertDoesNotThrow(() -> rentalValidator.validatePaginationParams(1, 20));
        
        // 测试无效分页参数
        assertThrows(RentalException.class, () -> rentalValidator.validatePaginationParams(0, 20));
        assertThrows(RentalException.class, () -> rentalValidator.validatePaginationParams(1, 0));
        assertThrows(RentalException.class, () -> rentalValidator.validatePaginationParams(1, 101));
        
        // 测试日期参数验证
        LocalDate validDate = rentalValidator.validateDateParam("2025-01-28", "testDate");
        assertEquals(LocalDate.of(2025, 1, 28), validDate);
        
        // 测试无效日期格式
        assertThrows(RentalException.class, () -> rentalValidator.validateDateParam("invalid-date", "testDate"));
        
        // 测试日期范围验证
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);
        assertDoesNotThrow(() -> rentalValidator.validateDateRange(startDate, endDate));
        
        // 测试无效日期范围
        assertThrows(RentalException.class, () -> rentalValidator.validateDateRange(endDate, startDate));
        
        System.out.println("✅ 参数验证功能测试通过");
    }
    
    /**
     * 测试时间周期验证
     */
    @Test
    void testPeriodValidation() {
        // 测试有效的时间周期
        assertDoesNotThrow(() -> rentalValidator.validatePeriodParam("daily"));
        assertDoesNotThrow(() -> rentalValidator.validatePeriodParam("weekly"));
        assertDoesNotThrow(() -> rentalValidator.validatePeriodParam("monthly"));
        assertDoesNotThrow(() -> rentalValidator.validatePeriodParam("quarterly"));
        
        // 测试无效的时间周期
        assertThrows(RentalException.class, () -> rentalValidator.validatePeriodParam("invalid"));
        
        System.out.println("✅ 时间周期验证功能测试通过");
    }
    
    /**
     * 测试设备状态验证
     */
    @Test
    void testDeviceStatusValidation() {
        // 测试有效的设备状态
        assertDoesNotThrow(() -> rentalValidator.validateDeviceStatus("active"));
        assertDoesNotThrow(() -> rentalValidator.validateDeviceStatus("idle"));
        assertDoesNotThrow(() -> rentalValidator.validateDeviceStatus("maintenance"));
        assertDoesNotThrow(() -> rentalValidator.validateDeviceStatus("retired"));
        
        // 测试无效的设备状态
        assertThrows(RentalException.class, () -> rentalValidator.validateDeviceStatus("invalid"));
        
        System.out.println("✅ 设备状态验证功能测试通过");
    }
    
    /**
     * 测试搜索关键词验证
     */
    @Test
    void testSearchKeywordValidation() {
        // 测试有效的搜索关键词
        assertDoesNotThrow(() -> rentalValidator.validateSearchKeyword("YX-0001"));
        assertDoesNotThrow(() -> rentalValidator.validateSearchKeyword("设备"));
        
        // 测试过长的关键词
        String longKeyword = "a".repeat(101);
        assertThrows(RentalException.class, () -> rentalValidator.validateSearchKeyword(longKeyword));
        
        // 测试包含特殊字符的关键词
        assertThrows(RentalException.class, () -> rentalValidator.validateSearchKeyword("test<script>"));
        assertThrows(RentalException.class, () -> rentalValidator.validateSearchKeyword("test'or'1=1"));
        
        System.out.println("✅ 搜索关键词验证功能测试通过");
    }
    
    /**
     * 测试缓存功能
     */
    @Test
    void testCacheService() {
        // 测试缓存键生成
        String statsKey = cacheService.generateStatsKey("2025-01-01", "2025-01-31");
        assertEquals("stats:2025-01-01:2025-01-31", statsKey);
        
        String chartKey = cacheService.generateChartKey("trends", "daily", "2025-01-01", "2025-01-31", null);
        assertEquals("chart:trends:daily:2025-01-01:2025-01-31:null", chartKey);
        
        String deviceKey = cacheService.generateDeviceKey(1, 20, "YX", "YX-Robot-Pro", "active", "华东");
        assertEquals("device:1:20:YX:YX-Robot-Pro:active:华东", deviceKey);
        
        // 测试缓存统计
        Map<String, Object> cacheStats = cacheService.getCacheStats();
        assertNotNull(cacheStats);
        assertTrue(cacheStats.containsKey("statsCache"));
        assertTrue(cacheStats.containsKey("chartCache"));
        assertTrue(cacheStats.containsKey("deviceCache"));
        assertTrue(cacheStats.containsKey("todayStatsCache"));
        
        System.out.println("✅ 缓存服务功能测试通过");
    }
    
    /**
     * 测试异常处理
     */
    @Test
    void testExceptionHandling() {
        // 测试租赁异常创建
        RentalException deviceNotFound = RentalException.deviceNotFound("YX-0001");
        assertEquals(RentalException.ErrorCodes.DEVICE_NOT_FOUND, deviceNotFound.getErrorCode());
        assertEquals("设备未找到", deviceNotFound.getMessage());
        assertEquals("设备ID: YX-0001", deviceNotFound.getErrorDetail());
        
        RentalException customerNotFound = RentalException.customerNotFound("123");
        assertEquals(RentalException.ErrorCodes.CUSTOMER_NOT_FOUND, customerNotFound.getErrorCode());
        
        RentalException invalidPeriod = RentalException.invalidRentalPeriod(0);
        assertEquals(RentalException.ErrorCodes.INVALID_RENTAL_PERIOD, invalidPeriod.getErrorCode());
        
        RentalException dataValidation = RentalException.dataValidationError("field", "value");
        assertEquals(RentalException.ErrorCodes.DATA_VALIDATION_ERROR, dataValidation.getErrorCode());
        
        System.out.println("✅ 异常处理功能测试通过");
    }
    
    /**
     * 测试性能要求
     */
    @Test
    void testPerformanceRequirements() {
        // 模拟性能测试
        long startTime = System.currentTimeMillis();
        
        // 模拟参数验证操作
        for (int i = 0; i < 1000; i++) {
            rentalValidator.validatePaginationParams(1, 20);
            rentalValidator.validatePeriodParam("daily");
            rentalValidator.validateDeviceStatus("active");
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // 验证参数验证性能（应该很快）
        assertTrue(duration < 1000, "参数验证性能应该小于1秒，实际：" + duration + "ms");
        
        // 测试缓存键生成性能
        startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 10000; i++) {
            cacheService.generateStatsKey("2025-01-01", "2025-01-31");
            cacheService.generateChartKey("trends", "daily", "2025-01-01", "2025-01-31", null);
            cacheService.generateDeviceKey(1, 20, "YX", "YX-Robot-Pro", "active", "华东");
        }
        
        endTime = System.currentTimeMillis();
        duration = endTime - startTime;
        
        // 验证缓存键生成性能
        assertTrue(duration < 1000, "缓存键生成性能应该小于1秒，实际：" + duration + "ms");
        
        System.out.println("✅ 性能要求测试通过");
        System.out.println("   - 参数验证性能: 优秀");
        System.out.println("   - 缓存键生成性能: 优秀");
    }
    
    /**
     * 测试错误代码完整性
     */
    @Test
    void testErrorCodeCompleteness() {
        // 验证所有错误代码都已定义
        String[] errorCodes = {
            RentalException.ErrorCodes.RENTAL_RECORD_NOT_FOUND,
            RentalException.ErrorCodes.DEVICE_NOT_FOUND,
            RentalException.ErrorCodes.CUSTOMER_NOT_FOUND,
            RentalException.ErrorCodes.INVALID_RENTAL_PERIOD,
            RentalException.ErrorCodes.INVALID_RENTAL_STATUS,
            RentalException.ErrorCodes.DEVICE_NOT_AVAILABLE,
            RentalException.ErrorCodes.INVALID_PAYMENT_STATUS,
            RentalException.ErrorCodes.RENTAL_CONFLICT,
            RentalException.ErrorCodes.DATA_VALIDATION_ERROR,
            RentalException.ErrorCodes.PERMISSION_DENIED,
            RentalException.ErrorCodes.SYSTEM_ERROR
        };
        
        assertEquals(11, errorCodes.length, "应该定义11个错误代码");
        
        // 验证错误代码格式
        for (String errorCode : errorCodes) {
            assertNotNull(errorCode, "错误代码不能为空");
            assertTrue(errorCode.startsWith("RENTAL_"), "错误代码应以RENTAL_开头");
            assertTrue(errorCode.matches("RENTAL_\\d{3}"), "错误代码格式应为RENTAL_XXX");
        }
        
        System.out.println("✅ 错误代码完整性测试通过");
    }
}