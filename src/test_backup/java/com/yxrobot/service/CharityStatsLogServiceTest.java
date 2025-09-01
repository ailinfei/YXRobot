package com.yxrobot.service;

import com.yxrobot.dto.PageResult;
import com.yxrobot.entity.CharityStatsLog;
import com.yxrobot.mapper.CharityStatsLogMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 公益统计数据更新日志服务测试类
 * 测试CharityStatsLogService的核心功能
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-18
 */
@ExtendWith(MockitoExtension.class)
class CharityStatsLogServiceTest {
    
    @Mock
    private CharityStatsLogMapper charityStatsLogMapper;
    
    @InjectMocks
    private CharityStatsLogService charityStatsLogService;
    
    private CharityStatsLog testLog;
    
    @BeforeEach
    void setUp() {
        testLog = new CharityStatsLog();
        testLog.setId(1L);
        testLog.setStatsId(100L);
        testLog.setOperationType("update");
        testLog.setUpdateReason("测试更新");
        testLog.setOperatorId(1L);
        testLog.setOperatorName("测试用户");
        testLog.setOperatorIp("127.0.0.1");
        testLog.setOperationTime(LocalDateTime.now());
        testLog.setDataVersion(1);
        testLog.setOperationResult("success");
        testLog.setCreateTime(LocalDateTime.now());
    }
    
    @Test
    void testLogStatsUpdate_Success() {
        // 准备测试数据
        when(charityStatsLogMapper.insert(any(CharityStatsLog.class))).thenReturn(1);
        
        // 执行测试
        Long logId = charityStatsLogService.logStatsUpdate(
            100L, "update", "totalBeneficiaries", 
            "beforeData", "afterData", "测试更新",
            1L, "测试用户", "127.0.0.1",
            1, "success", null, "测试备注"
        );
        
        // 验证结果
        assertNotNull(logId);
        verify(charityStatsLogMapper, times(1)).insert(any(CharityStatsLog.class));
    }
    
    @Test
    void testLogStatsUpdate_InvalidParameters() {
        // 测试无效的操作类型
        assertThrows(IllegalArgumentException.class, () -> {
            charityStatsLogService.logStatsUpdate(
                100L, "invalid", null, null, null, "测试更新",
                1L, "测试用户", "127.0.0.1",
                1, "success", null, null
            );
        });
        
        // 测试空的更新原因
        assertThrows(IllegalArgumentException.class, () -> {
            charityStatsLogService.logStatsUpdate(
                100L, "update", null, null, null, "",
                1L, "测试用户", "127.0.0.1",
                1, "success", null, null
            );
        });
        
        // 测试空的操作人ID
        assertThrows(IllegalArgumentException.class, () -> {
            charityStatsLogService.logStatsUpdate(
                100L, "update", null, null, null, "测试更新",
                null, "测试用户", "127.0.0.1",
                1, "success", null, null
            );
        });
    }
    
    @Test
    void testGetStatsUpdateHistory_Success() {
        // 准备测试数据
        List<CharityStatsLog> mockLogs = Arrays.asList(testLog);
        when(charityStatsLogMapper.selectByStatsId(eq(100L), eq(0), eq(20))).thenReturn(mockLogs);
        when(charityStatsLogMapper.countByStatsId(eq(100L))).thenReturn(1L);
        
        // 执行测试
        PageResult<CharityStatsLog> result = charityStatsLogService.getStatsUpdateHistory(
            100L, null, null, null, null, null, 1, 20
        );
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getList().size());
        assertEquals(1L, result.getTotal());
        assertEquals(1, result.getPage());
        assertEquals(20, result.getSize());
        
        verify(charityStatsLogMapper, times(1)).selectByStatsId(eq(100L), eq(0), eq(20));
        verify(charityStatsLogMapper, times(1)).countByStatsId(eq(100L));
    }
    
    @Test
    void testGetLatestStatsUpdateHistory_Success() {
        // 准备测试数据
        List<CharityStatsLog> mockLogs = Arrays.asList(testLog);
        when(charityStatsLogMapper.selectLatestLogs(eq(10))).thenReturn(mockLogs);
        
        // 执行测试
        List<CharityStatsLog> result = charityStatsLogService.getLatestStatsUpdateHistory(10);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testLog.getId(), result.get(0).getId());
        
        verify(charityStatsLogMapper, times(1)).selectLatestLogs(eq(10));
    }
    
    @Test
    void testGetFailedOperations_Success() {
        // 准备测试数据
        CharityStatsLog failedLog = new CharityStatsLog();
        failedLog.setId(2L);
        failedLog.setOperationResult("failed");
        failedLog.setErrorMessage("测试错误");
        
        List<CharityStatsLog> mockLogs = Arrays.asList(failedLog);
        when(charityStatsLogMapper.selectFailedOperations(eq(0), eq(20))).thenReturn(mockLogs);
        when(charityStatsLogMapper.countFailedOperations()).thenReturn(1L);
        
        // 执行测试
        PageResult<CharityStatsLog> result = charityStatsLogService.getFailedOperations(1, 20);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getList().size());
        assertEquals("failed", result.getList().get(0).getOperationResult());
        assertEquals(1L, result.getTotal());
        
        verify(charityStatsLogMapper, times(1)).selectFailedOperations(eq(0), eq(20));
        verify(charityStatsLogMapper, times(1)).countFailedOperations();
    }
    
    @Test
    void testGetOperationStatistics_Success() {
        // 准备测试数据
        List<Map<String, Object>> operationTypeStats = Arrays.asList(
            Map.of("operation_type", "update", "count", 10)
        );
        List<Map<String, Object>> operationResultStats = Arrays.asList(
            Map.of("operation_result", "success", "count", 8),
            Map.of("operation_result", "failed", "count", 2)
        );
        List<Map<String, Object>> operatorStats = Arrays.asList(
            Map.of("operator_id", 1L, "operator_name", "测试用户", "operation_count", 5)
        );
        List<Map<String, Object>> dailyStats = Arrays.asList(
            Map.of("operation_date", "2024-12-18", "total_operations", 3)
        );
        
        when(charityStatsLogMapper.getOperationTypeStatistics()).thenReturn(operationTypeStats);
        when(charityStatsLogMapper.getOperationResultStatistics()).thenReturn(operationResultStats);
        when(charityStatsLogMapper.getOperatorStatistics(eq(10))).thenReturn(operatorStats);
        when(charityStatsLogMapper.getDailyOperationStatistics(eq(30))).thenReturn(dailyStats);
        
        // 执行测试
        Map<String, Object> result = charityStatsLogService.getOperationStatistics();
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.containsKey("operationTypeStatistics"));
        assertTrue(result.containsKey("operationResultStatistics"));
        assertTrue(result.containsKey("operatorStatistics"));
        assertTrue(result.containsKey("dailyOperationStatistics"));
        
        verify(charityStatsLogMapper, times(1)).getOperationTypeStatistics();
        verify(charityStatsLogMapper, times(1)).getOperationResultStatistics();
        verify(charityStatsLogMapper, times(1)).getOperatorStatistics(eq(10));
        verify(charityStatsLogMapper, times(1)).getDailyOperationStatistics(eq(30));
    }
    
    @Test
    void testGetVersionHistory_Success() {
        // 准备测试数据
        List<CharityStatsLog> mockLogs = Arrays.asList(testLog);
        when(charityStatsLogMapper.selectVersionHistory(eq(100L))).thenReturn(mockLogs);
        
        // 执行测试
        List<CharityStatsLog> result = charityStatsLogService.getVersionHistory(100L);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testLog.getId(), result.get(0).getId());
        
        verify(charityStatsLogMapper, times(1)).selectVersionHistory(eq(100L));
    }
    
    @Test
    void testGetVersionHistory_InvalidStatsId() {
        // 测试空的统计数据ID
        assertThrows(IllegalArgumentException.class, () -> {
            charityStatsLogService.getVersionHistory(null);
        });
    }
    
    @Test
    void testCleanExpiredLogs_Success() {
        // 准备测试数据
        when(charityStatsLogMapper.cleanExpiredLogs(eq(90))).thenReturn(5);
        
        // 执行测试
        int deletedCount = charityStatsLogService.cleanExpiredLogs(90);
        
        // 验证结果
        assertEquals(5, deletedCount);
        verify(charityStatsLogMapper, times(1)).cleanExpiredLogs(eq(90));
    }
    
    @Test
    void testCleanExpiredLogs_DefaultRetentionDays() {
        // 准备测试数据
        when(charityStatsLogMapper.cleanExpiredLogs(eq(90))).thenReturn(3);
        
        // 执行测试（传入null，应该使用默认值90天）
        int deletedCount = charityStatsLogService.cleanExpiredLogs(null);
        
        // 验证结果
        assertEquals(3, deletedCount);
        verify(charityStatsLogMapper, times(1)).cleanExpiredLogs(eq(90));
    }
    
    @Test
    void testGetLogById_Success() {
        // 准备测试数据
        when(charityStatsLogMapper.selectById(eq(1L))).thenReturn(testLog);
        
        // 执行测试
        CharityStatsLog result = charityStatsLogService.getLogById(1L);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(testLog.getId(), result.getId());
        assertEquals(testLog.getOperationType(), result.getOperationType());
        
        verify(charityStatsLogMapper, times(1)).selectById(eq(1L));
    }
    
    @Test
    void testGetLogById_NotFound() {
        // 准备测试数据
        when(charityStatsLogMapper.selectById(eq(999L))).thenReturn(null);
        
        // 执行测试
        CharityStatsLog result = charityStatsLogService.getLogById(999L);
        
        // 验证结果
        assertNull(result);
        verify(charityStatsLogMapper, times(1)).selectById(eq(999L));
    }
    
    @Test
    void testGetLogById_InvalidId() {
        // 测试空的日志ID
        assertThrows(IllegalArgumentException.class, () -> {
            charityStatsLogService.getLogById(null);
        });
    }
    
    @Test
    void testLogFailedOperation_Success() {
        // 准备测试数据
        when(charityStatsLogMapper.insert(any(CharityStatsLog.class))).thenReturn(1);
        
        // 执行测试
        Long logId = charityStatsLogService.logFailedOperation(
            100L, "update", "测试更新", 1L, "测试用户", "127.0.0.1", "测试错误信息"
        );
        
        // 验证结果
        assertNotNull(logId);
        verify(charityStatsLogMapper, times(1)).insert(any(CharityStatsLog.class));
    }
}