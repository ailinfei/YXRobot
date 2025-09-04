package com.yxrobot.service;

import com.yxrobot.dto.ManagedDeviceLogDTO;
import com.yxrobot.dto.ManagedDeviceLogSearchCriteria;
import com.yxrobot.entity.ManagedDeviceLog;
import com.yxrobot.exception.ManagedDeviceException;
import com.yxrobot.mapper.ManagedDeviceLogMapper;
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
 * ManagedDeviceLogService 单元测试类
 * 
 * 测试覆盖范围：
 * - 设备日志查询功能
 * - 日志筛选和分页
 * - 日志创建功能
 * - 日志级别和分类管理
 * - 异常处理
 * 
 * 测试目标覆盖率：
 * - 行覆盖率 ≥ 80%
 * - 分支覆盖率 ≥ 75%
 * - 方法覆盖率 ≥ 85%
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("设备日志服务单元测试")
public class ManagedDeviceLogServiceTest {

    @Mock
    private ManagedDeviceLogMapper logMapper;
    
    @Mock
    private ManagedDeviceMapper deviceMapper;
    
    @InjectMocks
    private ManagedDeviceLogService logService;
    
    private ManagedDeviceLog testLog;
    private ManagedDeviceLogDTO testLogDTO;
    private ManagedDeviceLogSearchCriteria testCriteria;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // 初始化测试数据
        testLog = new ManagedDeviceLog();
        testLog.setId(1L);
        testLog.setDeviceId(1L);
        testLog.setTimestamp(LocalDateTime.now());
        testLog.setLevel("info");
        testLog.setCategory("system");
        testLog.setMessage("设备启动成功");
        testLog.setDetails("{\"cpu_usage\": \"15%\", \"memory_usage\": \"45%\"}");
        testLog.setCreatedAt(LocalDateTime.now());
        
        testLogDTO = new ManagedDeviceLogDTO();
        testLogDTO.setId("1");
        testLogDTO.setDeviceId("1");
        testLogDTO.setTimestamp(LocalDateTime.now().toString());
        testLogDTO.setLevel("info");
        testLogDTO.setCategory("system");
        testLogDTO.setMessage("设备启动成功");
        testLogDTO.setDetails("{\"cpu_usage\": \"15%\", \"memory_usage\": \"45%\"}");
        
        testCriteria = new ManagedDeviceLogSearchCriteria();
        testCriteria.setDeviceId(1L);
        testCriteria.setPage(1);
        testCriteria.setPageSize(20);
    }

    @Test
    @DisplayName("获取设备日志 - 成功场景")
    void testGetManagedDeviceLogs_Success() {
        // Given
        List<ManagedDeviceLog> mockLogs = Arrays.asList(testLog);
        when(logMapper.selectBySearchCriteria(any(ManagedDeviceLogSearchCriteria.class)))
            .thenReturn(mockLogs);
        when(logMapper.countBySearchCriteria(any(ManagedDeviceLogSearchCriteria.class)))
            .thenReturn(1L);

        // When
        Map<String, Object> result = logService.getManagedDeviceLogs(testCriteria);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.get("total"));
        assertEquals(1, result.get("page"));
        assertEquals(20, result.get("pageSize"));
        
        @SuppressWarnings("unchecked")
        List<ManagedDeviceLogDTO> logs = (List<ManagedDeviceLogDTO>) result.get("list");
        assertNotNull(logs);
        assertEquals(1, logs.size());
        assertEquals("设备启动成功", logs.get(0).getMessage());
        assertEquals("info", logs.get(0).getLevel());
        assertEquals("system", logs.get(0).getCategory());
        
        verify(logMapper).selectBySearchCriteria(testCriteria);
        verify(logMapper).countBySearchCriteria(testCriteria);
    }

    @Test
    @DisplayName("获取设备日志 - 空结果")
    void testGetManagedDeviceLogs_EmptyResult() {
        // Given
        when(logMapper.selectBySearchCriteria(any(ManagedDeviceLogSearchCriteria.class)))
            .thenReturn(Arrays.asList());
        when(logMapper.countBySearchCriteria(any(ManagedDeviceLogSearchCriteria.class)))
            .thenReturn(0L);

        // When
        Map<String, Object> result = logService.getManagedDeviceLogs(testCriteria);

        // Then
        assertNotNull(result);
        assertEquals(0L, result.get("total"));
        
        @SuppressWarnings("unchecked")
        List<ManagedDeviceLogDTO> logs = (List<ManagedDeviceLogDTO>) result.get("list");
        assertNotNull(logs);
        assertTrue(logs.isEmpty());
    }

    @Test
    @DisplayName("日志级别筛选 - info级别")
    void testLogLevelFilter_InfoLevel() {
        // Given
        testCriteria.setLevel("info");
        List<ManagedDeviceLog> mockLogs = Arrays.asList(testLog);
        when(logMapper.selectBySearchCriteria(testCriteria)).thenReturn(mockLogs);
        when(logMapper.countBySearchCriteria(testCriteria)).thenReturn(1L);

        // When
        Map<String, Object> result = logService.getManagedDeviceLogs(testCriteria);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.get("total"));
        
        @SuppressWarnings("unchecked")
        List<ManagedDeviceLogDTO> logs = (List<ManagedDeviceLogDTO>) result.get("list");
        logs.forEach(log -> assertEquals("info", log.getLevel()));
        
        verify(logMapper).selectBySearchCriteria(testCriteria);
    }

    @Test
    @DisplayName("日志级别筛选 - warning级别")
    void testLogLevelFilter_WarningLevel() {
        // Given
        testLog.setLevel("warning");
        testLog.setMessage("设备温度过高");
        testCriteria.setLevel("warning");
        
        List<ManagedDeviceLog> mockLogs = Arrays.asList(testLog);
        when(logMapper.selectBySearchCriteria(testCriteria)).thenReturn(mockLogs);
        when(logMapper.countBySearchCriteria(testCriteria)).thenReturn(1L);

        // When
        Map<String, Object> result = logService.getManagedDeviceLogs(testCriteria);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.get("total"));
        
        @SuppressWarnings("unchecked")
        List<ManagedDeviceLogDTO> logs = (List<ManagedDeviceLogDTO>) result.get("list");
        logs.forEach(log -> {
            assertEquals("warning", log.getLevel());
            assertEquals("设备温度过高", log.getMessage());
        });
    }

    @Test
    @DisplayName("日志级别筛选 - error级别")
    void testLogLevelFilter_ErrorLevel() {
        // Given
        testLog.setLevel("error");
        testLog.setMessage("设备连接失败");
        testCriteria.setLevel("error");
        
        List<ManagedDeviceLog> mockLogs = Arrays.asList(testLog);
        when(logMapper.selectBySearchCriteria(testCriteria)).thenReturn(mockLogs);
        when(logMapper.countBySearchCriteria(testCriteria)).thenReturn(1L);

        // When
        Map<String, Object> result = logService.getManagedDeviceLogs(testCriteria);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.get("total"));
        
        @SuppressWarnings("unchecked")
        List<ManagedDeviceLogDTO> logs = (List<ManagedDeviceLogDTO>) result.get("list");
        logs.forEach(log -> {
            assertEquals("error", log.getLevel());
            assertEquals("设备连接失败", log.getMessage());
        });
    }

    @Test
    @DisplayName("日志分类筛选 - system分类")
    void testLogCategoryFilter_SystemCategory() {
        // Given
        testCriteria.setCategory("system");
        List<ManagedDeviceLog> mockLogs = Arrays.asList(testLog);
        when(logMapper.selectBySearchCriteria(testCriteria)).thenReturn(mockLogs);
        when(logMapper.countBySearchCriteria(testCriteria)).thenReturn(1L);

        // When
        Map<String, Object> result = logService.getManagedDeviceLogs(testCriteria);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.get("total"));
        
        @SuppressWarnings("unchecked")
        List<ManagedDeviceLogDTO> logs = (List<ManagedDeviceLogDTO>) result.get("list");
        logs.forEach(log -> assertEquals("system", log.getCategory()));
    }

    @Test
    @DisplayName("日志分类筛选 - user分类")
    void testLogCategoryFilter_UserCategory() {
        // Given
        testLog.setCategory("user");
        testLog.setMessage("用户登录");
        testCriteria.setCategory("user");
        
        List<ManagedDeviceLog> mockLogs = Arrays.asList(testLog);
        when(logMapper.selectBySearchCriteria(testCriteria)).thenReturn(mockLogs);
        when(logMapper.countBySearchCriteria(testCriteria)).thenReturn(1L);

        // When
        Map<String, Object> result = logService.getManagedDeviceLogs(testCriteria);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.get("total"));
        
        @SuppressWarnings("unchecked")
        List<ManagedDeviceLogDTO> logs = (List<ManagedDeviceLogDTO>) result.get("list");
        logs.forEach(log -> {
            assertEquals("user", log.getCategory());
            assertEquals("用户登录", log.getMessage());
        });
    }

    @Test
    @DisplayName("时间范围筛选 - 指定时间段")
    void testTimeRangeFilter_SpecificPeriod() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().minusHours(1);
        LocalDateTime endTime = LocalDateTime.now();
        testCriteria.setStartTime(startTime);
        testCriteria.setEndTime(endTime);
        
        List<ManagedDeviceLog> mockLogs = Arrays.asList(testLog);
        when(logMapper.selectBySearchCriteria(testCriteria)).thenReturn(mockLogs);
        when(logMapper.countBySearchCriteria(testCriteria)).thenReturn(1L);

        // When
        Map<String, Object> result = logService.getManagedDeviceLogs(testCriteria);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.get("total"));
        
        verify(logMapper).selectBySearchCriteria(testCriteria);
    }

    @Test
    @DisplayName("复合条件筛选 - 多个条件组合")
    void testMultipleConditionsFilter() {
        // Given
        testCriteria.setLevel("error");
        testCriteria.setCategory("hardware");
        testCriteria.setStartTime(LocalDateTime.now().minusHours(1));
        testCriteria.setEndTime(LocalDateTime.now());
        
        testLog.setLevel("error");
        testLog.setCategory("hardware");
        testLog.setMessage("硬件故障");
        
        List<ManagedDeviceLog> mockLogs = Arrays.asList(testLog);
        when(logMapper.selectBySearchCriteria(testCriteria)).thenReturn(mockLogs);
        when(logMapper.countBySearchCriteria(testCriteria)).thenReturn(1L);

        // When
        Map<String, Object> result = logService.getManagedDeviceLogs(testCriteria);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.get("total"));
        
        @SuppressWarnings("unchecked")
        List<ManagedDeviceLogDTO> logs = (List<ManagedDeviceLogDTO>) result.get("list");
        logs.forEach(log -> {
            assertEquals("error", log.getLevel());
            assertEquals("hardware", log.getCategory());
            assertEquals("硬件故障", log.getMessage());
        });
    }

    @Test
    @DisplayName("创建日志 - 成功场景")
    void testCreateLog_Success() {
        // Given
        when(logMapper.insert(any(ManagedDeviceLog.class))).thenReturn(1);

        // When
        ManagedDeviceLogDTO result = logService.createLog(testLogDTO);

        // Then
        assertNotNull(result);
        assertEquals("1", result.getDeviceId());
        assertEquals("info", result.getLevel());
        assertEquals("system", result.getCategory());
        assertEquals("设备启动成功", result.getMessage());
        
        verify(logMapper).insert(any(ManagedDeviceLog.class));
    }

    @Test
    @DisplayName("创建日志 - 设备不存在")
    void testCreateLog_DeviceNotFound() {
        // Given
        testLogDTO.setDeviceId("999");
        when(deviceMapper.selectById(999L)).thenReturn(null);

        // When & Then
        ManagedDeviceException exception = assertThrows(
            ManagedDeviceException.class,
            () -> logService.createLog(testLogDTO)
        );
        
        assertEquals("设备不存在", exception.getMessage());
        verify(deviceMapper).selectById(999L);
        verify(logMapper, never()).insert(any(ManagedDeviceLog.class));
    }

    @Test
    @DisplayName("分页查询 - 第一页")
    void testPagination_FirstPage() {
        // Given
        testCriteria.setPage(1);
        testCriteria.setPageSize(10);
        
        List<ManagedDeviceLog> mockLogs = Arrays.asList(testLog);
        when(logMapper.selectBySearchCriteria(testCriteria)).thenReturn(mockLogs);
        when(logMapper.countBySearchCriteria(testCriteria)).thenReturn(25L);

        // When
        Map<String, Object> result = logService.getManagedDeviceLogs(testCriteria);

        // Then
        assertNotNull(result);
        assertEquals(25L, result.get("total"));
        assertEquals(1, result.get("page"));
        assertEquals(10, result.get("pageSize"));
        assertEquals(3, result.get("totalPages")); // 25条记录，每页10条，共3页
    }

    @Test
    @DisplayName("分页查询 - 第二页")
    void testPagination_SecondPage() {
        // Given
        testCriteria.setPage(2);
        testCriteria.setPageSize(10);
        
        List<ManagedDeviceLog> mockLogs = Arrays.asList(testLog);
        when(logMapper.selectBySearchCriteria(testCriteria)).thenReturn(mockLogs);
        when(logMapper.countBySearchCriteria(testCriteria)).thenReturn(25L);

        // When
        Map<String, Object> result = logService.getManagedDeviceLogs(testCriteria);

        // Then
        assertNotNull(result);
        assertEquals(25L, result.get("total"));
        assertEquals(2, result.get("page"));
        assertEquals(10, result.get("pageSize"));
        assertEquals(3, result.get("totalPages"));
    }

    @Test
    @DisplayName("日志详情展开 - JSON格式详情")
    void testLogDetailsExpansion_JSONFormat() {
        // Given
        testLog.setDetails("{\"error_code\": \"E001\", \"stack_trace\": \"...\", \"timestamp\": \"2024-01-01T10:00:00\"}");
        List<ManagedDeviceLog> mockLogs = Arrays.asList(testLog);
        when(logMapper.selectBySearchCriteria(testCriteria)).thenReturn(mockLogs);
        when(logMapper.countBySearchCriteria(testCriteria)).thenReturn(1L);

        // When
        Map<String, Object> result = logService.getManagedDeviceLogs(testCriteria);

        // Then
        assertNotNull(result);
        
        @SuppressWarnings("unchecked")
        List<ManagedDeviceLogDTO> logs = (List<ManagedDeviceLogDTO>) result.get("list");
        assertEquals(1, logs.size());
        
        String details = logs.get(0).getDetails();
        assertNotNull(details);
        assertTrue(details.contains("error_code"));
        assertTrue(details.contains("E001"));
    }

    @Test
    @DisplayName("日志级别统计 - 各级别数量统计")
    void testLogLevelStatistics() {
        // Given
        when(logMapper.countByLevel(1L, "info")).thenReturn(10L);
        when(logMapper.countByLevel(1L, "warning")).thenReturn(5L);
        when(logMapper.countByLevel(1L, "error")).thenReturn(2L);
        when(logMapper.countByLevel(1L, "debug")).thenReturn(15L);

        // When
        Map<String, Long> result = logService.getLogLevelStatistics(1L);

        // Then
        assertNotNull(result);
        assertEquals(10L, result.get("info"));
        assertEquals(5L, result.get("warning"));
        assertEquals(2L, result.get("error"));
        assertEquals(15L, result.get("debug"));
        
        verify(logMapper).countByLevel(1L, "info");
        verify(logMapper).countByLevel(1L, "warning");
        verify(logMapper).countByLevel(1L, "error");
        verify(logMapper).countByLevel(1L, "debug");
    }

    @Test
    @DisplayName("日志分类统计 - 各分类数量统计")
    void testLogCategoryStatistics() {
        // Given
        when(logMapper.countByCategory(1L, "system")).thenReturn(8L);
        when(logMapper.countByCategory(1L, "user")).thenReturn(12L);
        when(logMapper.countByCategory(1L, "network")).thenReturn(3L);
        when(logMapper.countByCategory(1L, "hardware")).thenReturn(1L);
        when(logMapper.countByCategory(1L, "software")).thenReturn(6L);

        // When
        Map<String, Long> result = logService.getLogCategoryStatistics(1L);

        // Then
        assertNotNull(result);
        assertEquals(8L, result.get("system"));
        assertEquals(12L, result.get("user"));
        assertEquals(3L, result.get("network"));
        assertEquals(1L, result.get("hardware"));
        assertEquals(6L, result.get("software"));
    }

    @Test
    @DisplayName("数据转换测试 - Entity到DTO")
    void testEntityToDTO_Conversion() {
        // Given
        ManagedDeviceLog log = new ManagedDeviceLog();
        log.setId(1L);
        log.setDeviceId(1L);
        log.setTimestamp(LocalDateTime.of(2024, 1, 1, 10, 0, 0));
        log.setLevel("error");
        log.setCategory("hardware");
        log.setMessage("硬件故障");
        log.setDetails("{\"component\": \"CPU\"}");
        log.setCreatedAt(LocalDateTime.of(2024, 1, 1, 10, 0, 0));

        // When
        ManagedDeviceLogDTO dto = logService.convertToDTO(log);

        // Then
        assertNotNull(dto);
        assertEquals("1", dto.getId());
        assertEquals("1", dto.getDeviceId());
        assertEquals("error", dto.getLevel());
        assertEquals("hardware", dto.getCategory());
        assertEquals("硬件故障", dto.getMessage());
        assertEquals("{\"component\": \"CPU\"}", dto.getDetails());
        assertNotNull(dto.getTimestamp());
        assertNotNull(dto.getCreatedAt());
    }

    @Test
    @DisplayName("数据转换测试 - DTO到Entity")
    void testDTOToEntity_Conversion() {
        // Given
        ManagedDeviceLogDTO dto = new ManagedDeviceLogDTO();
        dto.setId("1");
        dto.setDeviceId("1");
        dto.setTimestamp("2024-01-01T10:00:00");
        dto.setLevel("warning");
        dto.setCategory("network");
        dto.setMessage("网络连接不稳定");
        dto.setDetails("{\"latency\": \"500ms\"}");

        // When
        ManagedDeviceLog entity = logService.convertToEntity(dto);

        // Then
        assertNotNull(entity);
        assertEquals(1L, entity.getId());
        assertEquals(1L, entity.getDeviceId());
        assertEquals("warning", entity.getLevel());
        assertEquals("network", entity.getCategory());
        assertEquals("网络连接不稳定", entity.getMessage());
        assertEquals("{\"latency\": \"500ms\"}", entity.getDetails());
        assertNotNull(entity.getTimestamp());
    }

    @Test
    @DisplayName("异常处理 - 空参数")
    void testExceptionHandling_NullParameters() {
        // When & Then
        assertThrows(IllegalArgumentException.class,
            () -> logService.getManagedDeviceLogs(null));
        
        assertThrows(IllegalArgumentException.class,
            () -> logService.createLog(null));
        
        assertThrows(IllegalArgumentException.class,
            () -> logService.getLogLevelStatistics(null));
        
        assertThrows(IllegalArgumentException.class,
            () -> logService.getLogCategoryStatistics(null));
    }

    @Test
    @DisplayName("性能测试 - 大数据量日志查询")
    void testPerformance_LargeLogQuery() {
        // Given
        List<ManagedDeviceLog> largeLogs = Arrays.asList(
            testLog, testLog, testLog, testLog, testLog,
            testLog, testLog, testLog, testLog, testLog
        );
        when(logMapper.selectBySearchCriteria(any(ManagedDeviceLogSearchCriteria.class)))
            .thenReturn(largeLogs);
        when(logMapper.countBySearchCriteria(any(ManagedDeviceLogSearchCriteria.class)))
            .thenReturn(1000L);

        // When
        long startTime = System.currentTimeMillis();
        Map<String, Object> result = logService.getManagedDeviceLogs(testCriteria);
        long endTime = System.currentTimeMillis();

        // Then
        assertNotNull(result);
        assertEquals(1000L, result.get("total"));
        
        // 性能断言：查询时间应该在合理范围内（小于1秒）
        long queryTime = endTime - startTime;
        assertTrue(queryTime < 1000, "日志查询时间应该小于1秒，实际：" + queryTime + "ms");
    }

    @Test
    @DisplayName("边界条件测试 - 极大页码")
    void testBoundaryConditions_LargePageNumber() {
        // Given
        testCriteria.setPage(999999);
        testCriteria.setPageSize(20);
        
        when(logMapper.selectBySearchCriteria(testCriteria)).thenReturn(Arrays.asList());
        when(logMapper.countBySearchCriteria(testCriteria)).thenReturn(100L);

        // When
        Map<String, Object> result = logService.getManagedDeviceLogs(testCriteria);

        // Then
        assertNotNull(result);
        assertEquals(100L, result.get("total"));
        assertEquals(999999, result.get("page"));
        
        @SuppressWarnings("unchecked")
        List<ManagedDeviceLogDTO> logs = (List<ManagedDeviceLogDTO>) result.get("list");
        assertTrue(logs.isEmpty()); // 超出范围的页码应该返回空结果
    }

    @Test
    @DisplayName("日志清理功能 - 按时间清理旧日志")
    void testLogCleanup_ByTime() {
        // Given
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(30);
        when(logMapper.deleteByTimeBefore(1L, cutoffTime)).thenReturn(50);

        // When
        int deletedCount = logService.cleanupOldLogs(1L, cutoffTime);

        // Then
        assertEquals(50, deletedCount);
        verify(logMapper).deleteByTimeBefore(1L, cutoffTime);
    }

    @Test
    @DisplayName("日志导出功能 - 导出指定条件的日志")
    void testLogExport_WithConditions() {
        // Given
        List<ManagedDeviceLog> exportLogs = Arrays.asList(testLog);
        when(logMapper.selectForExport(any(ManagedDeviceLogSearchCriteria.class)))
            .thenReturn(exportLogs);

        // When
        List<ManagedDeviceLogDTO> result = logService.exportLogs(testCriteria);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("设备启动成功", result.get(0).getMessage());
        
        verify(logMapper).selectForExport(testCriteria);
    }
}