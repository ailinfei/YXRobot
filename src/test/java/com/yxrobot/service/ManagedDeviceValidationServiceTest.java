package com.yxrobot.service;

import com.yxrobot.dto.ManagedDeviceDTO;
import com.yxrobot.exception.ManagedDeviceException;
import com.yxrobot.mapper.ManagedDeviceMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 设备管理验证服务测试
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("设备管理验证服务测试")
class ManagedDeviceValidationServiceTest {
    
    @Mock
    private ManagedDeviceMapper managedDeviceMapper;
    
    @InjectMocks
    private ManagedDeviceValidationService validationService;
    
    private ManagedDeviceDTO validDeviceDTO;
    
    @BeforeEach
    void setUp() {
        validDeviceDTO = new ManagedDeviceDTO();
        validDeviceDTO.setSerialNumber("YX2025001234");
        validDeviceDTO.setModel("EDUCATION");
        validDeviceDTO.setStatus("offline");
        validDeviceDTO.setFirmwareVersion("1.0.0");
        validDeviceDTO.setCustomerId("1");
        validDeviceDTO.setCustomerName("张三");
        validDeviceDTO.setCustomerPhone("13812345678");
        validDeviceDTO.setNotes("测试设备");
    }
    
    @Test
    @DisplayName("测试设备创建验证 - 正常情况")
    void testValidateDeviceCreation_Valid() {
        // Mock 序列号不存在
        when(managedDeviceMapper.existsSerialNumber(anyString(), any())).thenReturn(false);
        
        // 应该不抛出异常
        assertDoesNotThrow(() -> validationService.validateDeviceCreation(validDeviceDTO));
        
        // 验证调用了序列号检查
        verify(managedDeviceMapper).existsSerialNumber(validDeviceDTO.getSerialNumber(), null);
    }
    
    @Test
    @DisplayName("测试设备创建验证 - 空数据")
    void testValidateDeviceCreation_NullData() {
        assertThrows(ManagedDeviceException.class, 
            () -> validationService.validateDeviceCreation(null));
    }
    
    @Test
    @DisplayName("测试设备创建验证 - 序列号已存在")
    void testValidateDeviceCreation_SerialNumberExists() {
        // Mock 序列号已存在
        when(managedDeviceMapper.existsSerialNumber(anyString(), any())).thenReturn(true);
        
        ManagedDeviceException exception = assertThrows(ManagedDeviceException.class, 
            () -> validationService.validateDeviceCreation(validDeviceDTO));
        
        assertEquals("SERIAL_NUMBER_EXISTS", exception.getErrorCode());
    }
    
    @Test
    @DisplayName("测试设备创建验证 - 无效序列号")
    void testValidateDeviceCreation_InvalidSerialNumber() {
        validDeviceDTO.setSerialNumber("invalid");
        
        assertThrows(ManagedDeviceException.class, 
            () -> validationService.validateDeviceCreation(validDeviceDTO));
    }
    
    @Test
    @DisplayName("测试设备创建验证 - 无效固件版本")
    void testValidateDeviceCreation_InvalidFirmwareVersion() {
        validDeviceDTO.setFirmwareVersion("1.0");
        
        assertThrows(ManagedDeviceException.class, 
            () -> validationService.validateDeviceCreation(validDeviceDTO));
    }
    
    @Test
    @DisplayName("测试设备创建验证 - 无效客户名称")
    void testValidateDeviceCreation_InvalidCustomerName() {
        validDeviceDTO.setCustomerName("");
        
        assertThrows(ManagedDeviceException.class, 
            () -> validationService.validateDeviceCreation(validDeviceDTO));
    }
    
    @Test
    @DisplayName("测试设备创建验证 - XSS攻击检测")
    void testValidateDeviceCreation_XSSAttack() {
        validDeviceDTO.setCustomerName("<script>alert('xss')</script>");
        
        ManagedDeviceException exception = assertThrows(ManagedDeviceException.class, 
            () -> validationService.validateDeviceCreation(validDeviceDTO));
        
        assertTrue(exception.getMessage().contains("XSS攻击风险"));
    }
    
    @Test
    @DisplayName("测试设备更新验证 - 正常情况")
    void testValidateDeviceUpdate_Valid() {
        Long deviceId = 1L;
        
        // Mock 序列号不存在冲突
        when(managedDeviceMapper.existsSerialNumber(anyString(), eq(deviceId))).thenReturn(false);
        when(managedDeviceMapper.getDeviceStatus(deviceId)).thenReturn("offline");
        
        assertDoesNotThrow(() -> validationService.validateDeviceUpdate(deviceId, validDeviceDTO));
        
        verify(managedDeviceMapper).existsSerialNumber(validDeviceDTO.getSerialNumber(), deviceId);
    }
    
    @Test
    @DisplayName("测试设备更新验证 - 无效ID")
    void testValidateDeviceUpdate_InvalidId() {
        assertThrows(ManagedDeviceException.class, 
            () -> validationService.validateDeviceUpdate(null, validDeviceDTO));
        
        assertThrows(ManagedDeviceException.class, 
            () -> validationService.validateDeviceUpdate(0L, validDeviceDTO));
    }
    
    @Test
    @DisplayName("测试状态变更验证 - 正常情况")
    void testValidateStatusChange_Valid() {
        Long deviceId = 1L;
        String newStatus = "online";
        
        // Mock 当前状态为 offline
        when(managedDeviceMapper.getDeviceStatus(deviceId)).thenReturn("offline");
        
        assertDoesNotThrow(() -> validationService.validateStatusChange(deviceId, newStatus));
        
        verify(managedDeviceMapper).getDeviceStatus(deviceId);
    }
    
    @Test
    @DisplayName("测试状态变更验证 - 设备不存在")
    void testValidateStatusChange_DeviceNotFound() {
        Long deviceId = 1L;
        String newStatus = "online";
        
        // Mock 设备不存在
        when(managedDeviceMapper.getDeviceStatus(deviceId)).thenReturn(null);
        
        ManagedDeviceException exception = assertThrows(ManagedDeviceException.class, 
            () -> validationService.validateStatusChange(deviceId, newStatus));
        
        assertEquals("MANAGED_DEVICE_NOT_FOUND", exception.getErrorCode());
    }
    
    @Test
    @DisplayName("测试状态变更验证 - 无效状态转换")
    void testValidateStatusChange_InvalidTransition() {
        Long deviceId = 1L;
        String newStatus = "online";
        
        // Mock 当前状态为 error，不能直接转换为 online
        when(managedDeviceMapper.getDeviceStatus(deviceId)).thenReturn("error");
        
        ManagedDeviceException exception = assertThrows(ManagedDeviceException.class, 
            () -> validationService.validateStatusChange(deviceId, newStatus));
        
        assertEquals("INVALID_STATUS_TRANSITION", exception.getErrorCode());
    }
    
    @Test
    @DisplayName("测试批量操作验证 - 正常情况")
    void testValidateBatchOperation_Valid() {
        List<Long> deviceIds = Arrays.asList(1L, 2L, 3L);
        String operation = "reboot";
        
        assertDoesNotThrow(() -> validationService.validateBatchOperation(deviceIds, operation));
    }
    
    @Test
    @DisplayName("测试批量操作验证 - 空ID列表")
    void testValidateBatchOperation_EmptyIds() {
        List<Long> deviceIds = Arrays.asList();
        String operation = "reboot";
        
        assertThrows(ManagedDeviceException.class, 
            () -> validationService.validateBatchOperation(deviceIds, operation));
    }
    
    @Test
    @DisplayName("测试批量操作验证 - ID数量超限")
    void testValidateBatchOperation_TooManyIds() {
        // 创建超过100个ID的列表
        List<Long> deviceIds = Arrays.asList(new Long[101]);
        for (int i = 0; i < 101; i++) {
            deviceIds.set(i, (long) (i + 1));
        }
        String operation = "reboot";
        
        ManagedDeviceException exception = assertThrows(ManagedDeviceException.class, 
            () -> validationService.validateBatchOperation(deviceIds, operation));
        
        assertTrue(exception.getMessage().contains("不能超过100个"));
    }
    
    @Test
    @DisplayName("测试批量操作验证 - 无效操作类型")
    void testValidateBatchOperation_InvalidOperation() {
        List<Long> deviceIds = Arrays.asList(1L, 2L, 3L);
        String operation = "invalid_operation";
        
        assertThrows(ManagedDeviceException.class, 
            () -> validationService.validateBatchOperation(deviceIds, operation));
    }
    
    @Test
    @DisplayName("测试分页参数验证")
    void testValidatePaginationParams() {
        // 正常参数
        assertDoesNotThrow(() -> validationService.validatePaginationParams(1, 20));
        
        // 异常参数
        assertThrows(ManagedDeviceException.class, 
            () -> validationService.validatePaginationParams(-1, 20));
        assertThrows(ManagedDeviceException.class, 
            () -> validationService.validatePaginationParams(1, 0));
        assertThrows(ManagedDeviceException.class, 
            () -> validationService.validatePaginationParams(1, 1001));
    }
    
    @Test
    @DisplayName("测试搜索参数验证")
    void testValidateSearchParams() {
        // 正常参数
        assertDoesNotThrow(() -> validationService.validateSearchParams("test", "online", "EDUCATION"));
        
        // XSS攻击检测
        assertThrows(ManagedDeviceException.class, 
            () -> validationService.validateSearchParams("<script>alert(1)</script>", null, null));
        
        // 无效状态
        assertThrows(ManagedDeviceException.class, 
            () -> validationService.validateSearchParams(null, "invalid_status", null));
        
        // 无效型号
        assertThrows(ManagedDeviceException.class, 
            () -> validationService.validateSearchParams(null, null, "invalid_model"));
    }
    
    @Test
    @DisplayName("测试技术参数验证")
    void testValidateDeviceSpecifications() {
        ManagedDeviceDTO.DeviceSpecificationDTO spec = new ManagedDeviceDTO.DeviceSpecificationDTO();
        spec.setCpu("Intel i5");
        spec.setMemory("8GB");
        spec.setStorage("256GB SSD");
        spec.setDisplay("10.1寸触摸屏");
        spec.setBattery("5000mAh");
        spec.setConnectivity("WiFi, Bluetooth");
        
        validDeviceDTO.setSpecifications(spec);
        
        // Mock 序列号不存在
        when(managedDeviceMapper.existsSerialNumber(anyString(), any())).thenReturn(false);
        
        assertDoesNotThrow(() -> validationService.validateDeviceCreation(validDeviceDTO));
    }
    
    @Test
    @DisplayName("测试技术参数验证 - 字段过长")
    void testValidateDeviceSpecifications_TooLong() {
        ManagedDeviceDTO.DeviceSpecificationDTO spec = new ManagedDeviceDTO.DeviceSpecificationDTO();
        spec.setCpu("A".repeat(101)); // 超过100字符
        
        validDeviceDTO.setSpecifications(spec);
        
        assertThrows(ManagedDeviceException.class, 
            () -> validationService.validateDeviceCreation(validDeviceDTO));
    }
    
    @Test
    @DisplayName("测试设备配置验证")
    void testValidateDeviceConfiguration() {
        ManagedDeviceDTO.DeviceConfigurationDTO config = new ManagedDeviceDTO.DeviceConfigurationDTO();
        config.setLanguage("zh-CN");
        config.setTimezone("Asia/Shanghai");
        config.setAutoUpdate(true);
        config.setDebugMode(false);
        config.setCustomSettings("{\"theme\":\"dark\"}");
        
        validDeviceDTO.setConfiguration(config);
        
        // Mock 序列号不存在
        when(managedDeviceMapper.existsSerialNumber(anyString(), any())).thenReturn(false);
        
        assertDoesNotThrow(() -> validationService.validateDeviceCreation(validDeviceDTO));
    }
    
    @Test
    @DisplayName("测试设备配置验证 - 无效JSON")
    void testValidateDeviceConfiguration_InvalidJson() {
        ManagedDeviceDTO.DeviceConfigurationDTO config = new ManagedDeviceDTO.DeviceConfigurationDTO();
        config.setCustomSettings("invalid json");
        
        validDeviceDTO.setConfiguration(config);
        
        assertThrows(ManagedDeviceException.class, 
            () -> validationService.validateDeviceCreation(validDeviceDTO));
    }
}