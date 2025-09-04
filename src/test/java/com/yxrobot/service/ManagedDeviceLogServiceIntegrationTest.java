package com.yxrobot.service;

import com.yxrobot.entity.LogCategory;
import com.yxrobot.entity.LogLevel;
import com.yxrobot.entity.ManagedDeviceLog;
import com.yxrobot.mapper.ManagedDeviceLogMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 设备日志服务集成测试类
 * 测试ManagedDeviceLogService的核心功能
 */
@SpringBootTest
@ActiveProfiles("test")
class ManagedDeviceLogServiceIntegrationTest {
    
    @Autowired
    private ManagedDeviceLogService managedDeviceLogService;
    
    @MockBean
    private ManagedDeviceLogMapper managedDeviceLogMapper;
    
    @Test
    void testGetManagedDeviceLogs_Integration() {
        // 准备测试数据
        Long deviceId = 1L;
        ManagedDeviceLog testLog = createTestLog(deviceId);
        List<ManagedDeviceLog> logs = Arrays.asList(testLog);
        
        // 模拟Mapper行为
        when(managedDeviceLogMapper.selectByPage(eq(deviceId), eq(0), eq(20), 
                isNull(), isNull(), isNull(), isNull()))
            .thenReturn(logs);
        when(managedDeviceLogMapper.countByConditions(eq(deviceId), 
                isNull(), isNull(), isNull(), isNull()))
            .thenReturn(1);
        
        // 执行测试
        Map<String, Object> result = managedDeviceLogService.getManagedDeviceLogs(
            deviceId, 1, 20, null, null, null, null
        );
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.get("total"));
        assertEquals(1, result.get("page"));
        assertEquals(20, result.get("pageSize"));
        assertEquals(1, result.get("totalPages"));
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> logList = (List<Map<String, Object>>) result.get("list");
        assertEquals(1, logList.size());
        
        // 验证Mapper调用
        verify(managedDeviceLogMapper, times(1)).selectByPage(
            eq(deviceId), eq(0), eq(20), isNull(), isNull(), isNull(), isNull()
        );
        verify(managedDeviceLogMapper, times(1)).countByConditions(
            eq(deviceId), isNull(), isNull(), isNull(), isNull()
        );
    }
    
    @Test
    void testCreateLog_Integration() {
        // 准备测试数据
        Long deviceId = 1L;
        Map<String, Object> details = new HashMap<>();
        details.put("operation", "test");
        
        // 模拟Mapper行为
        when(managedDeviceLogMapper.insert(any(ManagedDeviceLog.class))).thenAnswer(invocation -> {
            ManagedDeviceLog log = invocation.getArgument(0);
            log.setId(1L);
            return 1;
        });
        
        // 执行测试
        Long logId = managedDeviceLogService.createLog(
            deviceId, LogLevel.INFO, LogCategory.SYSTEM, "测试日志", details
        );
        
        // 验证结果
        assertEquals(1L, logId);
        
        // 验证Mapper调用
        verify(managedDeviceLogMapper, times(1)).insert(any(ManagedDeviceLog.class));
    }
    
    @Test
    void testLogDeviceOperation_Integration() {
        // 模拟Mapper行为
        when(managedDeviceLogMapper.insert(any(ManagedDeviceLog.class))).thenAnswer(invocation -> {
            ManagedDeviceLog log = invocation.getArgument(0);
            log.setId(1L);
            return 1;
        });
        
        // 执行测试
        managedDeviceLogService.logDeviceOperation(1L, "重启", "成功", "管理员");
        
        // 验证Mapper调用
        verify(managedDeviceLogMapper, times(1)).insert(any(ManagedDeviceLog.class));
    }
    
    @Test
    void testLogStatusChange_Integration() {
        // 模拟Mapper行为
        when(managedDeviceLogMapper.insert(any(ManagedDeviceLog.class))).thenAnswer(invocation -> {
            ManagedDeviceLog log = invocation.getArgument(0);
            log.setId(1L);
            return 1;
        });
        
        // 执行测试
        managedDeviceLogService.logStatusChange(1L, "offline", "online", "设备激活");
        
        // 验证Mapper调用
        verify(managedDeviceLogMapper, times(1)).insert(any(ManagedDeviceLog.class));
    }
    
    @Test
    void testLogDeviceError_Integration() {
        // 模拟Mapper行为
        when(managedDeviceLogMapper.insert(any(ManagedDeviceLog.class))).thenAnswer(invocation -> {
            ManagedDeviceLog log = invocation.getArgument(0);
            log.setId(1L);
            return 1;
        });
        
        // 执行测试
        managedDeviceLogService.logDeviceError(1L, "E001", "连接超时", "堆栈跟踪信息");
        
        // 验证Mapper调用
        verify(managedDeviceLogMapper, times(1)).insert(any(ManagedDeviceLog.class));
    }
    
    @Test
    void testDeleteLogsByDeviceId_Integration() {
        // 模拟Mapper行为
        when(managedDeviceLogMapper.deleteByDeviceId(1L)).thenReturn(5);
        
        // 执行测试
        int deletedCount = managedDeviceLogService.deleteLogsByDeviceId(1L);
        
        // 验证结果
        assertEquals(5, deletedCount);
        
        // 验证Mapper调用
        verify(managedDeviceLogMapper, times(1)).deleteByDeviceId(1L);
    }
    
    @Test
    void testCreateLogs_Integration() {
        // 准备测试数据
        List<ManagedDeviceLog> logs = Arrays.asList(
            createTestLog(1L),
            createTestLog(1L)
        );
        
        // 模拟Mapper行为
        when(managedDeviceLogMapper.insert(any(ManagedDeviceLog.class))).thenReturn(1);
        
        // 执行测试
        int successCount = managedDeviceLogService.createLogs(logs);
        
        // 验证结果
        assertEquals(2, successCount);
        
        // 验证Mapper调用
        verify(managedDeviceLogMapper, times(2)).insert(any(ManagedDeviceLog.class));
    }
    
    @Test
    void testGetManagedDeviceLogs_WithFilters_Integration() {
        // 准备测试数据
        Long deviceId = 1L;
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        ManagedDeviceLog testLog = createTestLog(deviceId);
        List<ManagedDeviceLog> logs = Arrays.asList(testLog);
        
        // 模拟Mapper行为
        when(managedDeviceLogMapper.selectByPage(eq(deviceId), eq(0), eq(20), 
                eq("info"), eq("system"), eq(startDate), eq(endDate)))
            .thenReturn(logs);
        when(managedDeviceLogMapper.countByConditions(eq(deviceId), 
                eq("info"), eq("system"), eq(startDate), eq(endDate)))
            .thenReturn(1);
        
        // 执行测试
        Map<String, Object> result = managedDeviceLogService.getManagedDeviceLogs(
            deviceId, 1, 20, "info", "system", startDate, endDate
        );
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.get("total"));
        
        // 验证Mapper调用
        verify(managedDeviceLogMapper, times(1)).selectByPage(
            eq(deviceId), eq(0), eq(20), eq("info"), eq("system"), eq(startDate), eq(endDate)
        );
        verify(managedDeviceLogMapper, times(1)).countByConditions(
            eq(deviceId), eq("info"), eq("system"), eq(startDate), eq(endDate)
        );
    }
    
    @Test
    void testDeleteLogsByDateRange_Integration() {
        // 准备测试数据
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now().minusDays(7);
        
        // 模拟Mapper行为
        when(managedDeviceLogMapper.deleteByDateRange(startDate, endDate)).thenReturn(10);
        
        // 执行测试
        int deletedCount = managedDeviceLogService.deleteLogsByDateRange(startDate, endDate);
        
        // 验证结果
        assertEquals(10, deletedCount);
        
        // 验证Mapper调用
        verify(managedDeviceLogMapper, times(1)).deleteByDateRange(startDate, endDate);
    }
    
    /**
     * 创建测试日志对象
     */
    private ManagedDeviceLog createTestLog(Long deviceId) {
        ManagedDeviceLog log = new ManagedDeviceLog();
        log.setId(1L);
        log.setDeviceId(deviceId);
        log.setTimestamp(LocalDateTime.now());
        log.setLevel(LogLevel.INFO);
        log.setCategory(LogCategory.SYSTEM);
        log.setMessage("测试日志消息");
        log.setDetails(new HashMap<>());
        log.setCreatedAt(LocalDateTime.now());
        return log;
    }
}