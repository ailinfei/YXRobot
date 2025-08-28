package com.yxrobot.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CharityException 单元测试
 * 测试公益业务异常类的创建和属性设置
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-19
 */
class CharityExceptionTest {

    @Test
    void testCharityException_WithMessage() {
        // 创建异常
        String message = "测试异常消息";
        CharityException exception = new CharityException(message);

        // 验证异常属性
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
        assertNull(exception.getErrorCode());
    }

    @Test
    void testCharityException_WithMessageAndCause() {
        // 创建异常
        String message = "测试异常消息";
        Throwable cause = new RuntimeException("原因异常");
        CharityException exception = new CharityException(message, cause);

        // 验证异常属性
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertNull(exception.getErrorCode());
    }

    @Test
    void testCharityException_WithMessageAndErrorCode() {
        // 创建异常
        String message = "测试异常消息";
        String errorCode = "CHARITY_001";
        CharityException exception = new CharityException(message, errorCode);

        // 验证异常属性
        assertEquals(message, exception.getMessage());
        assertEquals(errorCode, exception.getErrorCode());
        assertNull(exception.getCause());
    }

    @Test
    void testCharityException_WithAllParameters() {
        // 创建异常
        String message = "测试异常消息";
        String errorCode = "CHARITY_001";
        Throwable cause = new RuntimeException("原因异常");
        CharityException exception = new CharityException(message, errorCode, cause);

        // 验证异常属性
        assertEquals(message, exception.getMessage());
        assertEquals(errorCode, exception.getErrorCode());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testValidationError_StaticMethod() {
        // 使用静态方法创建验证错误异常
        String message = "数据验证失败";
        CharityException exception = CharityException.validationError(message);

        // 验证异常属性
        assertEquals(message, exception.getMessage());
        assertEquals("VALIDATION_ERROR", exception.getErrorCode());
        assertNull(exception.getCause());
    }

    @Test
    void testValidationError_WithDetails() {
        // 使用静态方法创建验证错误异常（带详细信息）
        String message = "数据验证失败";
        String details = "字段不能为空";
        CharityException exception = CharityException.validationError(message, details);

        // 验证异常属性
        assertEquals(message + ": " + details, exception.getMessage());
        assertEquals("VALIDATION_ERROR", exception.getErrorCode());
        assertNull(exception.getCause());
    }

    @Test
    void testDataNotFound_StaticMethod() {
        // 使用静态方法创建数据未找到异常
        String resource = "统计数据";
        CharityException exception = CharityException.dataNotFound(resource);

        // 验证异常属性
        assertEquals("未找到指定的" + resource, exception.getMessage());
        assertEquals("DATA_NOT_FOUND", exception.getErrorCode());
        assertNull(exception.getCause());
    }

    @Test
    void testDataNotFound_WithId() {
        // 使用静态方法创建数据未找到异常（带ID）
        String resource = "统计数据";
        Long id = 123L;
        String message = "未找到指定的" + resource + "，ID: " + id;
        CharityException exception = CharityException.dataNotFound(message);

        // 验证异常属性
        assertEquals(message, exception.getMessage());
        assertEquals("CHARITY_DATA_NOT_FOUND", exception.getErrorCode());
        assertNull(exception.getCause());
    }

    @Test
    void testDataConflict_StaticMethod() {
        // 使用静态方法创建数据冲突异常
        String message = "数据版本冲突";
        CharityException exception = CharityException.dataConflict(message);

        // 验证异常属性
        assertEquals(message, exception.getMessage());
        assertEquals("DATA_CONFLICT", exception.getErrorCode());
        assertNull(exception.getCause());
    }

    @Test
    void testOperationFailed_StaticMethod() {
        // 使用静态方法创建操作失败异常
        String operation = "更新统计数据";
        CharityException exception = CharityException.operationFailed(operation);

        // 验证异常属性
        assertEquals(operation + "操作失败", exception.getMessage());
        assertEquals("OPERATION_FAILED", exception.getErrorCode());
        assertNull(exception.getCause());
    }

    @Test
    void testOperationFailed_WithCause() {
        // 使用静态方法创建操作失败异常（带原因）
        String operation = "更新统计数据";
        Throwable cause = new RuntimeException("数据库连接失败");
        CharityException exception = CharityException.operationFailed(operation, cause);

        // 验证异常属性
        assertEquals(operation + "操作失败", exception.getMessage());
        assertEquals("OPERATION_FAILED", exception.getErrorCode());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testOperationFailed_WithDetails() {
        // 使用静态方法创建操作失败异常（带详细信息）
        String operation = "更新统计数据";
        String details = "版本号不匹配";
        CharityException exception = CharityException.operationFailed(operation, details);

        // 验证异常属性
        assertEquals(operation + "操作失败: " + details, exception.getMessage());
        assertEquals("OPERATION_FAILED", exception.getErrorCode());
        assertNull(exception.getCause());
    }

    @Test
    void testStatsError_StaticMethod() {
        // 使用静态方法创建统计数据错误异常
        String message = "统计数据计算错误";
        CharityException exception = CharityException.statsError(message);

        // 验证异常属性
        assertEquals(message, exception.getMessage());
        assertEquals("STATS_ERROR", exception.getErrorCode());
        assertNull(exception.getCause());
    }

    @Test
    void testStatsError_WithDetails() {
        // 使用静态方法创建统计数据错误异常（带详细信息）
        String message = "统计数据计算错误";
        String details = "数据源不一致";
        CharityException exception = CharityException.statsError(message, details);

        // 验证异常属性
        assertEquals(message + ": " + details, exception.getMessage());
        assertEquals("STATS_ERROR", exception.getErrorCode());
        assertNull(exception.getCause());
    }

    @Test
    void testInstitutionError_StaticMethod() {
        // 使用静态方法创建机构错误异常
        String message = "机构信息错误";
        CharityException exception = CharityException.institutionError(message);

        // 验证异常属性
        assertEquals(message, exception.getMessage());
        assertEquals("INSTITUTION_ERROR", exception.getErrorCode());
        assertNull(exception.getCause());
    }

    @Test
    void testActivityError_StaticMethod() {
        // 使用静态方法创建活动错误异常
        String message = "活动信息错误";
        CharityException exception = CharityException.activityError(message);

        // 验证异常属性
        assertEquals(message, exception.getMessage());
        assertEquals("ACTIVITY_ERROR", exception.getErrorCode());
        assertNull(exception.getCause());
    }

    @Test
    void testCacheError_StaticMethod() {
        // 使用静态方法创建缓存错误异常
        String message = "缓存操作失败";
        CharityException exception = CharityException.cacheError(message);

        // 验证异常属性
        assertEquals(message, exception.getMessage());
        assertEquals("CACHE_ERROR", exception.getErrorCode());
        assertNull(exception.getCause());
    }

    @Test
    void testCacheError_WithCause() {
        // 使用静态方法创建缓存错误异常（带原因）
        String message = "缓存操作失败";
        Throwable cause = new RuntimeException("缓存连接失败");
        CharityException exception = CharityException.cacheError(message, cause);

        // 验证异常属性
        assertEquals(message, exception.getMessage());
        assertEquals("CACHE_ERROR", exception.getErrorCode());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testChartError_StaticMethod() {
        // 使用静态方法创建图表错误异常
        String message = "图表数据生成失败";
        CharityException exception = CharityException.chartError(message);

        // 验证异常属性
        assertEquals(message, exception.getMessage());
        assertEquals("CHART_ERROR", exception.getErrorCode());
        assertNull(exception.getCause());
    }

    @Test
    void testExceptionInheritance() {
        // 验证异常继承关系
        CharityException exception = new CharityException("测试");
        
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    @Test
    void testExceptionSerialization() {
        // 测试异常的序列化属性
        CharityException exception = new CharityException("测试消息", "TEST_CODE");
        
        // 验证异常可以被序列化（通过toString方法）
        String exceptionString = exception.toString();
        assertNotNull(exceptionString);
        assertTrue(exceptionString.contains("CharityException"));
        assertTrue(exceptionString.contains("测试消息"));
    }

    @Test
    void testNullMessageHandling() {
        // 测试空消息处理
        CharityException exception = new CharityException(null);
        
        // 验证异常可以正常创建
        assertNull(exception.getMessage());
        assertNull(exception.getErrorCode());
    }

    @Test
    void testNullErrorCodeHandling() {
        // 测试空错误码处理
        CharityException exception = new CharityException("测试消息", (String) null);
        
        // 验证异常可以正常创建
        assertEquals("测试消息", exception.getMessage());
        assertNull(exception.getErrorCode());
    }

    @Test
    void testEmptyMessageHandling() {
        // 测试空字符串消息处理
        CharityException exception = new CharityException("");
        
        // 验证异常可以正常创建
        assertEquals("", exception.getMessage());
        assertNull(exception.getErrorCode());
    }

    @Test
    void testEmptyErrorCodeHandling() {
        // 测试空字符串错误码处理
        CharityException exception = new CharityException("测试消息", "");
        
        // 验证异常可以正常创建
        assertEquals("测试消息", exception.getMessage());
        assertEquals("", exception.getErrorCode());
    }

    @Test
    void testExceptionChaining() {
        // 测试异常链
        RuntimeException rootCause = new RuntimeException("根本原因");
        IllegalArgumentException intermediateCause = new IllegalArgumentException("中间原因", rootCause);
        CharityException exception = new CharityException("最终异常", intermediateCause);
        
        // 验证异常链
        assertEquals("最终异常", exception.getMessage());
        assertEquals(intermediateCause, exception.getCause());
        assertEquals(rootCause, exception.getCause().getCause());
    }

    @Test
    void testStackTracePreservation() {
        // 测试堆栈跟踪保留
        try {
            throw new RuntimeException("原始异常");
        } catch (RuntimeException e) {
            CharityException exception = new CharityException("包装异常", e);
            
            // 验证堆栈跟踪被保留
            assertNotNull(exception.getStackTrace());
            assertTrue(exception.getStackTrace().length > 0);
            assertNotNull(exception.getCause().getStackTrace());
            assertTrue(exception.getCause().getStackTrace().length > 0);
        }
    }
}