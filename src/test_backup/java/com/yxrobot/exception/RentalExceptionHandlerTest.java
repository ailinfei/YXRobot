package com.yxrobot.exception;

import com.yxrobot.controller.RentalController;
import com.yxrobot.service.RentalStatsService;
import com.yxrobot.service.DeviceUtilizationService;
import com.yxrobot.service.RentalAnalysisService;
import com.yxrobot.service.RentalCustomerService;
import com.yxrobot.validation.RentalValidator;
import com.yxrobot.validation.RentalFormValidator;
import com.yxrobot.cache.RentalCacheService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.format.DateTimeParseException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 租赁异常处理器测试类
 * 测试全局异常处理功能
 * 
 * @author Kiro
 * @date 2025-01-28
 */
public class RentalExceptionHandlerTest {
    
    @InjectMocks
    private RentalExceptionHandler exceptionHandler;
    
    @Mock
    private RentalController rentalController;
    
    private MockHttpServletRequest request;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        exceptionHandler = new RentalExceptionHandler();
        request = new MockHttpServletRequest();
        request.setRequestURI("/api/rental/test");
    }
    
    /**
     * 测试租赁业务异常处理
     */
    @Test
    void testHandleRentalException() {
        RentalException exception = RentalException.deviceNotFound("YX-0001");
        
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleRentalException(exception, request);
        
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.get("code"));
        assertEquals("设备未找到", body.get("message"));
        assertEquals(RentalException.ErrorCodes.DEVICE_NOT_FOUND, body.get("errorCode"));
        assertEquals("设备ID: YX-0001", body.get("errorDetail"));
        assertEquals("/api/rental/test", body.get("path"));
        assertNotNull(body.get("timestamp"));
    }
    
    /**
     * 测试参数验证异常处理
     */
    @Test
    void testHandleValidationException() {
        // 创建模拟的BindException
        BindException bindException = new BindException(new Object(), "testObject");
        bindException.addError(new FieldError("testObject", "customerName", "客户名称不能为空"));
        bindException.addError(new FieldError("testObject", "phone", "手机号格式不正确"));
        
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleValidationException(bindException, request);
        
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.get("code"));
        assertEquals("参数验证失败", body.get("message"));
        assertEquals(RentalException.ErrorCodes.DATA_VALIDATION_ERROR, body.get("errorCode"));
        
        @SuppressWarnings("unchecked")
        Map<String, String> fieldErrors = (Map<String, String>) body.get("fieldErrors");
        assertNotNull(fieldErrors);
        assertEquals("客户名称不能为空", fieldErrors.get("customerName"));
        assertEquals("手机号格式不正确", fieldErrors.get("phone"));
    }
    
    /**
     * 测试日期解析异常处理
     */
    @Test
    void testHandleDateTimeParseException() {
        DateTimeParseException exception = new DateTimeParseException("无效日期格式", "2025-13-01", 0);
        
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleDateTimeParseException(exception, request);
        
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.get("code"));
        assertEquals("日期格式不正确，请使用YYYY-MM-DD格式", body.get("message"));
        assertEquals(RentalException.ErrorCodes.DATA_VALIDATION_ERROR, body.get("errorCode"));
        assertTrue(body.get("errorDetail").toString().contains("2025-13-01"));
    }
    
    /**
     * 测试数字格式异常处理
     */
    @Test
    void testHandleNumberFormatException() {
        NumberFormatException exception = new NumberFormatException("For input string: \"abc\"");
        
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleNumberFormatException(exception, request);
        
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.get("code"));
        assertEquals("数字格式不正确", body.get("message"));
        assertEquals(RentalException.ErrorCodes.DATA_VALIDATION_ERROR, body.get("errorCode"));
    }
    
    /**
     * 测试非法参数异常处理
     */
    @Test
    void testHandleIllegalArgumentException() {
        IllegalArgumentException exception = new IllegalArgumentException("参数值不合法");
        
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleIllegalArgumentException(exception, request);
        
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.get("code"));
        assertEquals("参数不合法: 参数值不合法", body.get("message"));
        assertEquals(RentalException.ErrorCodes.DATA_VALIDATION_ERROR, body.get("errorCode"));
    }
    
    /**
     * 测试空指针异常处理
     */
    @Test
    void testHandleNullPointerException() {
        NullPointerException exception = new NullPointerException("对象为空");
        
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleNullPointerException(exception, request);
        
        assertNotNull(response);
        assertEquals(500, response.getStatusCodeValue());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(500, body.get("code"));
        assertEquals("系统内部错误，请稍后重试", body.get("message"));
        assertEquals(RentalException.ErrorCodes.SYSTEM_ERROR, body.get("errorCode"));
    }
    
    /**
     * 测试数据库异常处理
     */
    @Test
    void testHandleDataAccessException() {
        org.springframework.dao.DataAccessException exception = 
            new org.springframework.dao.DataAccessResourceFailureException("数据库连接失败");
        
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleDataAccessException(exception, request);
        
        assertNotNull(response);
        assertEquals(500, response.getStatusCodeValue());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(500, body.get("code"));
        assertEquals("数据库访问错误，请稍后重试", body.get("message"));
        assertEquals(RentalException.ErrorCodes.SYSTEM_ERROR, body.get("errorCode"));
    }
    
    /**
     * 测试通用异常处理
     */
    @Test
    void testHandleGenericException() {
        Exception exception = new Exception("未知错误");
        
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleGenericException(exception, request);
        
        assertNotNull(response);
        assertEquals(500, response.getStatusCodeValue());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(500, body.get("code"));
        assertEquals("系统内部错误，请联系管理员", body.get("message"));
        assertEquals(RentalException.ErrorCodes.SYSTEM_ERROR, body.get("errorCode"));
    }
    
    /**
     * 测试租赁API特定的运行时异常处理
     */
    @Test
    void testHandleRentalRuntimeException() {
        RuntimeException exception = new RuntimeException("租赁模块运行时错误");
        
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleRentalRuntimeException(exception, request);
        
        assertNotNull(response);
        assertEquals(500, response.getStatusCodeValue());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(500, body.get("code"));
        assertTrue(body.get("message").toString().contains("查询失败"));
        assertEquals(RentalException.ErrorCodes.SYSTEM_ERROR, body.get("errorCode"));
    }
    
    /**
     * 测试非租赁API的运行时异常处理
     */
    @Test
    void testHandleNonRentalRuntimeException() {
        RuntimeException exception = new RuntimeException("非租赁模块错误");
        request.setRequestURI("/api/other/test");
        
        // 应该重新抛出异常，不被处理
        assertThrows(RuntimeException.class, () -> {
            exceptionHandler.handleRentalRuntimeException(exception, request);
        });
    }
    
    /**
     * 测试不同错误代码对应的HTTP状态码
     */
    @Test
    void testDifferentErrorCodesHttpStatus() {
        // 测试404错误
        RentalException notFoundException = RentalException.rentalRecordNotFound("123");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleRentalException(notFoundException, request);
        assertEquals(400, response.getStatusCodeValue()); // 当前实现返回400，可以根据需要调整
        
        // 测试数据验证错误
        RentalException validationException = RentalException.dataValidationError("field", "value");
        response = exceptionHandler.handleRentalException(validationException, request);
        assertEquals(400, response.getStatusCodeValue());
        
        // 测试系统错误
        RentalException systemException = RentalException.systemError("系统错误", new Exception());
        response = exceptionHandler.handleRentalException(systemException, request);
        assertEquals(400, response.getStatusCodeValue()); // 当前实现返回400，可以根据需要调整
    }
    
    /**
     * 测试异常信息的完整性
     */
    @Test
    void testExceptionResponseCompleteness() {
        RentalException exception = new RentalException(
            RentalException.ErrorCodes.DATA_VALIDATION_ERROR,
            "测试异常消息",
            "详细错误信息"
        );
        
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleRentalException(exception, request);
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        
        // 验证所有必需字段都存在
        assertTrue(body.containsKey("code"));
        assertTrue(body.containsKey("message"));
        assertTrue(body.containsKey("data"));
        assertTrue(body.containsKey("errorCode"));
        assertTrue(body.containsKey("errorDetail"));
        assertTrue(body.containsKey("timestamp"));
        assertTrue(body.containsKey("path"));
        
        // 验证字段值
        assertEquals(400, body.get("code"));
        assertEquals("测试异常消息", body.get("message"));
        assertNull(body.get("data"));
        assertEquals(RentalException.ErrorCodes.DATA_VALIDATION_ERROR, body.get("errorCode"));
        assertEquals("详细错误信息", body.get("errorDetail"));
        assertEquals("/api/rental/test", body.get("path"));
    }
}