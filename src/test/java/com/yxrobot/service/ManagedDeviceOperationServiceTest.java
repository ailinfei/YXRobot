package com.yxrobot.service;

import com.yxrobot.dto.ManagedDeviceDTO;
import com.yxrobot.entity.ManagedDevice;
import com.yxrobot.exception.ManagedDeviceException;
import com.yxrobot.mapper.ManagedDeviceMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ManagedDeviceOperationService 单元测试类
 * 
 * 测试覆盖范围：
 * - 设备操作业务逻辑
 * - 设备状态管理
 * - 批量操作功能
 * - 设备控制操作
 * - 异常处理
 * 
 * 测试目标覆盖率：
 * - 行覆盖率 ≥ 80%
 * - 分支覆盖率 ≥ 75%
 * - 方法覆盖率 ≥ 85%
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("设备操作服务单元测试")
public class ManagedDeviceOperationServiceTest {

    @Mock
    private ManagedDeviceMapper managedDeviceMapper;
    
    @InjectMocks
    private ManagedDeviceOperationService operationService;
    
    private ManagedDevice testDevice;
    private ManagedDeviceDTO testDeviceDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // 初始化测试数据
        testDevice = new ManagedDevice();
        testDevice.setId(1L);
        testDevice.setSerialNumber("YX-TEST-001");
        testDevice.setModel("YX-EDU-2024");
        testDevice.setStatus("online");
        testDevice.setFirmwareVersion("1.0.0");
        testDevice.setCustomerId(1L);
        testDevice.setCustomerName("[测试客户]");
        testDevice.setCustomerPhone("[测试电话]");
        testDevice.setCreatedAt(LocalDateTime.now());
        testDevice.setUpdatedAt(LocalDateTime.now());
        
        testDeviceDTO = new ManagedDeviceDTO();
        testDeviceDTO.setId("1");
        testDeviceDTO.setSerialNumber("YX-TEST-001");
        testDeviceDTO.setModel("YX-EDU-2024");
        testDeviceDTO.setStatus("online");
        testDeviceDTO.setFirmwareVersion("1.0.0");
        testDeviceDTO.setCustomerId("1");
        testDeviceDTO.setCustomerName("[测试客户]");
        testDeviceDTO.setCustomerPhone("[测试电话]");
    }

    @Test
    @DisplayName("更新设备状态 - 成功场景")
    void testUpdateManagedDeviceStatus_Success() {
        // Given
        when(managedDeviceMapper.selectById(1L)).thenReturn(testDevice);
        when(managedDeviceMapper.updateById(any(ManagedDevice.class))).thenReturn(1);

        // When
        Map<String, Object> result = operationService.updateManagedDeviceStatus(1L, "offline");

        // Then
        assertNotNull(result);
        assertEquals("1", result.get("id"));
        assertEquals("offline", result.get("status"));
        assertNotNull(result.get("updatedAt"));
        
        verify(managedDeviceMapper).selectById(1L);
        verify(managedDeviceMapper).updateById(any(ManagedDevice.class));
    }

    @Test
    @DisplayName("更新设备状态 - 设备不存在")
    void testUpdateManagedDeviceStatus_DeviceNotFound() {
        // Given
        when(managedDeviceMapper.selectById(999L)).thenReturn(null);

        // When & Then
        ManagedDeviceException exception = assertThrows(
            ManagedDeviceException.class,
            () -> operationService.updateManagedDeviceStatus(999L, "offline")
        );
        
        assertEquals("设备不存在", exception.getMessage());
        verify(managedDeviceMapper).selectById(999L);
        verify(managedDeviceMapper, never()).updateById(any(ManagedDevice.class));
    }

    @Test
    @DisplayName("更新设备状态 - 无效状态转换")
    void testUpdateManagedDeviceStatus_InvalidTransition() {
        // Given
        testDevice.setStatus("offline");
        when(managedDeviceMapper.selectById(1L)).thenReturn(testDevice);

        // When & Then
        ManagedDeviceException exception = assertThrows(
            ManagedDeviceException.class,
            () -> operationService.updateManagedDeviceStatus(1L, "error")
        );
        
        assertEquals("无效的状态转换", exception.getMessage());
        verify(managedDeviceMapper).selectById(1L);
        verify(managedDeviceMapper, never()).updateById(any(ManagedDevice.class));
    }

    @Test
    @DisplayName("重启设备 - 成功场景")
    void testRebootManagedDevice_Success() {
        // Given
        when(managedDeviceMapper.selectById(1L)).thenReturn(testDevice);
        when(managedDeviceMapper.updateById(any(ManagedDevice.class))).thenReturn(1);

        // When
        Map<String, Object> result = operationService.rebootManagedDevice(1L);

        // Then
        assertNotNull(result);
        assertEquals("重启指令已发送", result.get("message"));
        assertEquals("1", result.get("deviceId"));
        assertNotNull(result.get("timestamp"));
        
        verify(managedDeviceMapper).selectById(1L);
        verify(managedDeviceMapper).updateById(any(ManagedDevice.class));
    }

    @Test
    @DisplayName("重启设备 - 设备离线")
    void testRebootManagedDevice_DeviceOffline() {
        // Given
        testDevice.setStatus("offline");
        when(managedDeviceMapper.selectById(1L)).thenReturn(testDevice);

        // When & Then
        ManagedDeviceException exception = assertThrows(
            ManagedDeviceException.class,
            () -> operationService.rebootManagedDevice(1L)
        );
        
        assertEquals("设备离线，无法重启", exception.getMessage());
        verify(managedDeviceMapper).selectById(1L);
        verify(managedDeviceMapper, never()).updateById(any(ManagedDevice.class));
    }

    @Test
    @DisplayName("激活设备 - 成功场景")
    void testActivateManagedDevice_Success() {
        // Given
        testDevice.setStatus("offline");
        when(managedDeviceMapper.selectById(1L)).thenReturn(testDevice);
        when(managedDeviceMapper.updateById(any(ManagedDevice.class))).thenReturn(1);

        // When
        Map<String, Object> result = operationService.activateManagedDevice(1L);

        // Then
        assertNotNull(result);
        assertEquals("激活指令已发送", result.get("message"));
        assertEquals("1", result.get("deviceId"));
        assertNotNull(result.get("timestamp"));
        
        verify(managedDeviceMapper).selectById(1L);
        verify(managedDeviceMapper).updateById(any(ManagedDevice.class));
    }

    @Test
    @DisplayName("激活设备 - 设备已在线")
    void testActivateManagedDevice_AlreadyOnline() {
        // Given
        when(managedDeviceMapper.selectById(1L)).thenReturn(testDevice);

        // When & Then
        ManagedDeviceException exception = assertThrows(
            ManagedDeviceException.class,
            () -> operationService.activateManagedDevice(1L)
        );
        
        assertEquals("设备已在线", exception.getMessage());
        verify(managedDeviceMapper).selectById(1L);
        verify(managedDeviceMapper, never()).updateById(any(ManagedDevice.class));
    }

    @Test
    @DisplayName("推送固件 - 成功场景")
    void testPushFirmware_Success() {
        // Given
        when(managedDeviceMapper.selectById(1L)).thenReturn(testDevice);
        when(managedDeviceMapper.updateById(any(ManagedDevice.class))).thenReturn(1);

        // When
        Map<String, Object> result = operationService.pushFirmware(1L, "1.1.0");

        // Then
        assertNotNull(result);
        assertEquals("固件推送已启动", result.get("message"));
        assertEquals("1", result.get("deviceId"));
        assertEquals("1.1.0", result.get("firmwareVersion"));
        assertNotNull(result.get("timestamp"));
        
        verify(managedDeviceMapper).selectById(1L);
        verify(managedDeviceMapper).updateById(any(ManagedDevice.class));
    }

    @Test
    @DisplayName("推送固件 - 使用默认版本")
    void testPushFirmware_DefaultVersion() {
        // Given
        when(managedDeviceMapper.selectById(1L)).thenReturn(testDevice);
        when(managedDeviceMapper.updateById(any(ManagedDevice.class))).thenReturn(1);

        // When
        Map<String, Object> result = operationService.pushFirmware(1L, null);

        // Then
        assertNotNull(result);
        assertEquals("固件推送已启动", result.get("message"));
        assertEquals("1", result.get("deviceId"));
        assertNotNull(result.get("firmwareVersion")); // 应该使用默认版本
        
        verify(managedDeviceMapper).selectById(1L);
        verify(managedDeviceMapper).updateById(any(ManagedDevice.class));
    }

    @Test
    @DisplayName("推送固件 - 设备离线")
    void testPushFirmware_DeviceOffline() {
        // Given
        testDevice.setStatus("offline");
        when(managedDeviceMapper.selectById(1L)).thenReturn(testDevice);

        // When & Then
        ManagedDeviceException exception = assertThrows(
            ManagedDeviceException.class,
            () -> operationService.pushFirmware(1L, "1.1.0")
        );
        
        assertEquals("设备离线，无法推送固件", exception.getMessage());
        verify(managedDeviceMapper).selectById(1L);
        verify(managedDeviceMapper, never()).updateById(any(ManagedDevice.class));
    }

    @Test
    @DisplayName("批量固件推送 - 成功场景")
    void testBatchPushFirmware_Success() {
        // Given
        List<String> deviceIds = Arrays.asList("1", "2", "3");
        
        ManagedDevice device1 = createTestDevice(1L, "online");
        ManagedDevice device2 = createTestDevice(2L, "online");
        ManagedDevice device3 = createTestDevice(3L, "online");
        
        when(managedDeviceMapper.selectById(1L)).thenReturn(device1);
        when(managedDeviceMapper.selectById(2L)).thenReturn(device2);
        when(managedDeviceMapper.selectById(3L)).thenReturn(device3);
        when(managedDeviceMapper.updateById(any(ManagedDevice.class))).thenReturn(1);

        // When
        Map<String, Object> result = operationService.batchPushFirmware(deviceIds, "1.1.0");

        // Then
        assertNotNull(result);
        assertEquals("批量固件推送已启动", result.get("message"));
        assertEquals(3, result.get("updatedCount"));
        assertEquals(0, result.get("failedCount"));
        
        verify(managedDeviceMapper, times(3)).selectById(anyLong());
        verify(managedDeviceMapper, times(3)).updateById(any(ManagedDevice.class));
    }

    @Test
    @DisplayName("批量固件推送 - 部分失败")
    void testBatchPushFirmware_PartialFailure() {
        // Given
        List<String> deviceIds = Arrays.asList("1", "2", "999");
        
        ManagedDevice device1 = createTestDevice(1L, "online");
        ManagedDevice device2 = createTestDevice(2L, "offline"); // 离线设备
        
        when(managedDeviceMapper.selectById(1L)).thenReturn(device1);
        when(managedDeviceMapper.selectById(2L)).thenReturn(device2);
        when(managedDeviceMapper.selectById(999L)).thenReturn(null); // 不存在的设备
        when(managedDeviceMapper.updateById(any(ManagedDevice.class))).thenReturn(1);

        // When
        Map<String, Object> result = operationService.batchPushFirmware(deviceIds, "1.1.0");

        // Then
        assertNotNull(result);
        assertEquals("批量固件推送已启动", result.get("message"));
        assertEquals(1, result.get("updatedCount")); // 只有1个成功
        assertEquals(2, result.get("failedCount"));   // 2个失败
        
        @SuppressWarnings("unchecked")
        List<String> failedDevices = (List<String>) result.get("failedDevices");
        assertEquals(2, failedDevices.size());
        assertTrue(failedDevices.contains("2"));
        assertTrue(failedDevices.contains("999"));
    }

    @Test
    @DisplayName("批量重启设备 - 成功场景")
    void testBatchRebootDevices_Success() {
        // Given
        List<String> deviceIds = Arrays.asList("1", "2");
        
        ManagedDevice device1 = createTestDevice(1L, "online");
        ManagedDevice device2 = createTestDevice(2L, "online");
        
        when(managedDeviceMapper.selectById(1L)).thenReturn(device1);
        when(managedDeviceMapper.selectById(2L)).thenReturn(device2);
        when(managedDeviceMapper.updateById(any(ManagedDevice.class))).thenReturn(1);

        // When
        Map<String, Object> result = operationService.batchRebootDevices(deviceIds);

        // Then
        assertNotNull(result);
        assertEquals("批量重启指令已发送", result.get("message"));
        assertEquals(2, result.get("updatedCount"));
        assertEquals(0, result.get("failedCount"));
        
        verify(managedDeviceMapper, times(2)).selectById(anyLong());
        verify(managedDeviceMapper, times(2)).updateById(any(ManagedDevice.class));
    }

    @Test
    @DisplayName("批量删除设备 - 成功场景")
    void testBatchDeleteDevices_Success() {
        // Given
        List<String> deviceIds = Arrays.asList("1", "2");
        
        ManagedDevice device1 = createTestDevice(1L, "offline");
        ManagedDevice device2 = createTestDevice(2L, "offline");
        
        when(managedDeviceMapper.selectById(1L)).thenReturn(device1);
        when(managedDeviceMapper.selectById(2L)).thenReturn(device2);
        when(managedDeviceMapper.deleteById(anyLong())).thenReturn(1);

        // When
        Map<String, Object> result = operationService.batchDeleteDevices(deviceIds);

        // Then
        assertNotNull(result);
        assertEquals("批量删除成功", result.get("message"));
        assertEquals(2, result.get("deletedCount"));
        assertEquals(0, result.get("failedCount"));
        
        verify(managedDeviceMapper, times(2)).selectById(anyLong());
        verify(managedDeviceMapper, times(2)).deleteById(anyLong());
    }

    @Test
    @DisplayName("批量删除设备 - 在线设备不能删除")
    void testBatchDeleteDevices_OnlineDeviceCannotDelete() {
        // Given
        List<String> deviceIds = Arrays.asList("1");
        
        ManagedDevice device1 = createTestDevice(1L, "online"); // 在线设备
        
        when(managedDeviceMapper.selectById(1L)).thenReturn(device1);

        // When
        Map<String, Object> result = operationService.batchDeleteDevices(deviceIds);

        // Then
        assertNotNull(result);
        assertEquals("批量删除成功", result.get("message"));
        assertEquals(0, result.get("deletedCount"));
        assertEquals(1, result.get("failedCount"));
        
        @SuppressWarnings("unchecked")
        List<String> failedDevices = (List<String>) result.get("failedDevices");
        assertEquals(1, failedDevices.size());
        assertTrue(failedDevices.contains("1"));
        
        verify(managedDeviceMapper).selectById(1L);
        verify(managedDeviceMapper, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("设备状态转换验证 - 有效转换")
    void testStatusTransitionValidation_ValidTransitions() {
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

        // When & Then
        for (String[] transition : validTransitions) {
            String fromStatus = transition[0];
            String toStatus = transition[1];
            
            boolean isValid = operationService.isValidStatusTransition(fromStatus, toStatus);
            assertTrue(isValid, "状态转换应该有效: " + fromStatus + " -> " + toStatus);
        }
    }

    @Test
    @DisplayName("设备状态转换验证 - 无效转换")
    void testStatusTransitionValidation_InvalidTransitions() {
        // Given
        String[][] invalidTransitions = {
            {"offline", "error"},      // 离线不能直接变故障
            {"error", "online"},       // 故障不能直接变在线
            {"maintenance", "error"}   // 维护中不能直接变故障
        };

        // When & Then
        for (String[] transition : invalidTransitions) {
            String fromStatus = transition[0];
            String toStatus = transition[1];
            
            boolean isValid = operationService.isValidStatusTransition(fromStatus, toStatus);
            assertFalse(isValid, "状态转换应该无效: " + fromStatus + " -> " + toStatus);
        }
    }

    @Test
    @DisplayName("操作权限验证 - 设备操作权限")
    void testOperationPermission_DeviceOperations() {
        // Given
        when(managedDeviceMapper.selectById(1L)).thenReturn(testDevice);

        // When & Then
        assertTrue(operationService.canReboot(1L), "在线设备应该可以重启");
        assertFalse(operationService.canActivate(1L), "在线设备不需要激活");
        assertTrue(operationService.canPushFirmware(1L), "在线设备应该可以推送固件");
        assertFalse(operationService.canDelete(1L), "在线设备不能删除");
    }

    @Test
    @DisplayName("异常处理 - 空参数")
    void testExceptionHandling_NullParameters() {
        // When & Then
        assertThrows(IllegalArgumentException.class,
            () -> operationService.updateManagedDeviceStatus(null, "online"));
        
        assertThrows(IllegalArgumentException.class,
            () -> operationService.updateManagedDeviceStatus(1L, null));
        
        assertThrows(IllegalArgumentException.class,
            () -> operationService.rebootManagedDevice(null));
        
        assertThrows(IllegalArgumentException.class,
            () -> operationService.activateManagedDevice(null));
        
        assertThrows(IllegalArgumentException.class,
            () -> operationService.pushFirmware(null, "1.1.0"));
        
        assertThrows(IllegalArgumentException.class,
            () -> operationService.batchPushFirmware(null, "1.1.0"));
        
        assertThrows(IllegalArgumentException.class,
            () -> operationService.batchRebootDevices(null));
        
        assertThrows(IllegalArgumentException.class,
            () -> operationService.batchDeleteDevices(null));
    }

    @Test
    @DisplayName("性能测试 - 批量操作性能")
    void testPerformance_BatchOperations() {
        // Given
        List<String> deviceIds = Arrays.asList("1", "2", "3", "4", "5");
        
        for (int i = 1; i <= 5; i++) {
            ManagedDevice device = createTestDevice((long) i, "online");
            when(managedDeviceMapper.selectById((long) i)).thenReturn(device);
        }
        when(managedDeviceMapper.updateById(any(ManagedDevice.class))).thenReturn(1);

        // When
        long startTime = System.currentTimeMillis();
        Map<String, Object> result = operationService.batchPushFirmware(deviceIds, "1.1.0");
        long endTime = System.currentTimeMillis();

        // Then
        assertNotNull(result);
        assertEquals(5, result.get("updatedCount"));
        
        // 性能断言：批量操作应该在合理时间内完成（小于1秒）
        long operationTime = endTime - startTime;
        assertTrue(operationTime < 1000, "批量操作时间应该小于1秒，实际：" + operationTime + "ms");
    }

    // 辅助方法
    private ManagedDevice createTestDevice(Long id, String status) {
        ManagedDevice device = new ManagedDevice();
        device.setId(id);
        device.setSerialNumber("YX-TEST-" + String.format("%03d", id));
        device.setModel("YX-EDU-2024");
        device.setStatus(status);
        device.setFirmwareVersion("1.0.0");
        device.setCustomerId(1L);
        device.setCustomerName("[测试客户]");
        device.setCustomerPhone("[测试电话]");
        device.setCreatedAt(LocalDateTime.now());
        device.setUpdatedAt(LocalDateTime.now());
        return device;
    }
}