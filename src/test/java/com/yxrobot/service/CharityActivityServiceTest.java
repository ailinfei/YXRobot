package com.yxrobot.service;

import com.yxrobot.dto.CharityActivityDTO;
import com.yxrobot.dto.PageResult;
import com.yxrobot.entity.CharityActivity;
import com.yxrobot.mapper.CharityActivityMapper;
import com.yxrobot.mapper.CharityProjectMapper;
import com.yxrobot.validation.CharityDataValidator;
import com.yxrobot.util.QueryOptimizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

/**
 * CharityActivityService单元测试类
 * 测试公益活动服务层的查询功能
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-18
 */
@ExtendWith(MockitoExtension.class)
class CharityActivityServiceTest {

    @Mock
    private CharityActivityMapper charityActivityMapper;

    @Mock
    private CharityDataValidator charityDataValidator;

    @Mock
    private CharityProjectMapper charityProjectMapper;

    @Mock
    private QueryOptimizer queryOptimizer;

    @InjectMocks
    private CharityActivityService charityActivityService;

    private CharityActivity testActivity;
    private List<CharityActivity> testActivities;

    @BeforeEach
    void setUp() {
        // 创建测试数据
        testActivity = new CharityActivity();
        testActivity.setId(1L);
        testActivity.setProjectId(1L);
        testActivity.setTitle("测试公益活动");
        testActivity.setDescription("这是一个测试公益活动的描述");
        testActivity.setType("donation");
        testActivity.setDate(LocalDate.now().plusDays(7));
        testActivity.setStartTime(LocalDateTime.now().plusDays(7).withHour(9).withMinute(0));
        testActivity.setEndTime(LocalDateTime.now().plusDays(7).withHour(17).withMinute(0));
        testActivity.setLocation("测试地点");
        testActivity.setParticipants(100);
        testActivity.setTargetParticipants(150);
        testActivity.setOrganizer("测试组织方");
        testActivity.setStatus("planned");
        testActivity.setBudget(new BigDecimal("10000.00"));
        testActivity.setActualCost(new BigDecimal("0.00"));
        testActivity.setManager("测试负责人");
        testActivity.setManagerContact("13800138000");
        testActivity.setVolunteerNeeded(20);
        testActivity.setActualVolunteers(0);
        testActivity.setAchievements("");
        testActivity.setPhotos(new String[]{});
        testActivity.setNotes("测试备注");
        testActivity.setCreateTime(LocalDateTime.now());
        testActivity.setUpdateTime(LocalDateTime.now());
        testActivity.setCreateBy(1L);
        testActivity.setUpdateBy(1L);
        testActivity.setDeleted(0);

        testActivities = Arrays.asList(testActivity);

        // 设置QueryOptimizer的默认行为
        Map<String, Integer> defaultPaginationParams = new HashMap<>();
        defaultPaginationParams.put("page", 1);
        defaultPaginationParams.put("size", 20);
        defaultPaginationParams.put("offset", 0);
        lenient().when(queryOptimizer.optimizePagination(anyInt(), anyInt())).thenReturn(defaultPaginationParams);
        lenient().when(queryOptimizer.optimizeKeyword(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void testGetCharityActivities_Success() {
        // 准备测试数据
        when(charityActivityMapper.selectByQuery(any(), any(), any(), any(), any(), any(), any()))
            .thenReturn(testActivities);
        when(charityActivityMapper.countByQuery(any(), any(), any(), any(), any()))
            .thenReturn(1L);

        // 执行测试
        PageResult<CharityActivityDTO> result = charityActivityService.getCharityActivities(
            null, null, null, null, null, 1, 20);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getTotal());
        assertEquals(1, result.getList().size());
        
        CharityActivityDTO dto = result.getList().get(0);
        assertEquals(testActivity.getId(), dto.getId());
        assertEquals(testActivity.getTitle(), dto.getTitle());
        assertEquals(testActivity.getType(), dto.getType());
        assertEquals(testActivity.getStatus(), dto.getStatus());
        
        // 验证时间戳字段正确转换
        assertNotNull(dto.getCreateTime(), "创建时间应该正确转换");
        assertNotNull(dto.getUpdateTime(), "更新时间应该正确转换");

        // 验证方法调用
        verify(charityActivityMapper, times(1)).selectByQuery(any(), any(), any(), any(), any(), any(), any());
        verify(charityActivityMapper, times(1)).countByQuery(any(), any(), any(), any(), any());
        verify(queryOptimizer, times(1)).optimizePagination(1, 20);
    }

    @Test
    void testGetCharityActivities_WithKeywordSearch() {
        // 准备测试数据
        when(charityActivityMapper.selectByQuery(eq("测试"), any(), any(), any(), any(), any(), any()))
            .thenReturn(testActivities);
        when(charityActivityMapper.countByQuery(eq("测试"), any(), any(), any(), any()))
            .thenReturn(1L);

        // 执行测试
        PageResult<CharityActivityDTO> result = charityActivityService.getCharityActivities(
            "测试", null, null, null, null, 1, 20);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getTotal());
        assertEquals(1, result.getList().size());

        // 验证方法调用时传递了正确的关键词
        verify(charityActivityMapper, times(1)).selectByQuery(eq("测试"), any(), any(), any(), any(), any(), any());
        verify(charityActivityMapper, times(1)).countByQuery(eq("测试"), any(), any(), any(), any());
    }

    @Test
    void testGetCharityActivities_WithTypeAndStatusFilters() {
        // 准备测试数据
        when(charityActivityMapper.selectByQuery(any(), eq("donation"), eq("planned"), any(), any(), any(), any()))
            .thenReturn(testActivities);
        when(charityActivityMapper.countByQuery(any(), eq("donation"), eq("planned"), any(), any()))
            .thenReturn(1L);

        // 执行测试
        PageResult<CharityActivityDTO> result = charityActivityService.getCharityActivities(
            null, "donation", "planned", null, null, 1, 20);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getTotal());

        // 验证方法调用时传递了正确的过滤条件
        verify(charityActivityMapper, times(1)).selectByQuery(any(), eq("donation"), eq("planned"), any(), any(), any(), any());
        verify(charityActivityMapper, times(1)).countByQuery(any(), eq("donation"), eq("planned"), any(), any());
    }

    @Test
    void testGetCharityActivities_WithDateRange() {
        // 准备测试数据
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(30);
        
        when(charityActivityMapper.selectByQuery(any(), any(), any(), eq(startDate), eq(endDate), any(), any()))
            .thenReturn(testActivities);
        when(charityActivityMapper.countByQuery(any(), any(), any(), eq(startDate), eq(endDate)))
            .thenReturn(1L);

        // 执行测试
        PageResult<CharityActivityDTO> result = charityActivityService.getCharityActivities(
            null, null, null, startDate, endDate, 1, 20);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getTotal());

        // 验证方法调用时传递了正确的日期范围
        verify(charityActivityMapper, times(1)).selectByQuery(any(), any(), any(), eq(startDate), eq(endDate), any(), any());
        verify(charityActivityMapper, times(1)).countByQuery(any(), any(), any(), eq(startDate), eq(endDate));
    }

    @Test
    void testGetCharityActivities_WithPagination() {
        // 准备测试数据 - 模拟QueryOptimizer返回不同的分页参数
        Map<String, Integer> paginationParams = new HashMap<>();
        paginationParams.put("page", 2);
        paginationParams.put("size", 10);
        paginationParams.put("offset", 10);
        when(queryOptimizer.optimizePagination(2, 10)).thenReturn(paginationParams);
        
        when(charityActivityMapper.selectByQuery(any(), any(), any(), any(), any(), eq(10), eq(10)))
            .thenReturn(testActivities);
        when(charityActivityMapper.countByQuery(any(), any(), any(), any(), any()))
            .thenReturn(25L);

        // 执行测试
        PageResult<CharityActivityDTO> result = charityActivityService.getCharityActivities(
            null, null, null, null, null, 2, 10);

        // 验证结果
        assertNotNull(result);
        assertEquals(25L, result.getTotal());
        assertEquals(2, result.getPage());
        assertEquals(10, result.getSize());

        // 验证方法调用时传递了正确的分页参数
        verify(charityActivityMapper, times(1)).selectByQuery(any(), any(), any(), any(), any(), eq(10), eq(10));
        verify(queryOptimizer, times(1)).optimizePagination(2, 10);
    }

    @Test
    void testGetCharityActivities_EmptyResult() {
        // 准备测试数据 - 空结果
        when(charityActivityMapper.selectByQuery(any(), any(), any(), any(), any(), any(), any()))
            .thenReturn(Collections.emptyList());
        when(charityActivityMapper.countByQuery(any(), any(), any(), any(), any()))
            .thenReturn(0L);

        // 执行测试
        PageResult<CharityActivityDTO> result = charityActivityService.getCharityActivities(
            null, null, null, null, null, 1, 20);

        // 验证结果
        assertNotNull(result);
        assertEquals(0L, result.getTotal());
        assertEquals(0, result.getList().size());
    }

    @Test
    void testGetCharityActivities_DatabaseException() {
        // 准备测试数据 - 模拟数据库异常
        when(charityActivityMapper.selectByQuery(any(), any(), any(), any(), any(), any(), any()))
            .thenThrow(new RuntimeException("数据库连接失败"));

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            charityActivityService.getCharityActivities(null, null, null, null, null, 1, 20);
        });

        assertTrue(exception.getMessage().contains("获取公益活动列表失败") || 
                  exception.getMessage().contains("数据库连接失败"));
    }

    @Test
    void testGetCharityActivityById_Success() {
        // 准备测试数据
        when(charityActivityMapper.selectById(1L)).thenReturn(testActivity);

        // 执行测试
        CharityActivityDTO result = charityActivityService.getCharityActivityById(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(testActivity.getId(), result.getId());
        assertEquals(testActivity.getTitle(), result.getTitle());
        assertEquals(testActivity.getType(), result.getType());
        assertEquals(testActivity.getStatus(), result.getStatus());
        
        // 验证时间戳字段正确转换
        assertNotNull(result.getCreateTime(), "创建时间应该正确转换");
        assertNotNull(result.getUpdateTime(), "更新时间应该正确转换");

        // 验证方法调用
        verify(charityActivityMapper, times(1)).selectById(1L);
    }

    @Test
    void testGetCharityActivityById_NotFound() {
        // 准备测试数据
        when(charityActivityMapper.selectById(999L)).thenReturn(null);

        // 执行测试
        CharityActivityDTO result = charityActivityService.getCharityActivityById(999L);

        // 验证结果
        assertNull(result);

        // 验证方法调用
        verify(charityActivityMapper, times(1)).selectById(999L);
    }

    @Test
    void testGetCharityActivityById_NullId() {
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            charityActivityService.getCharityActivityById(null);
        });

        assertEquals("活动ID不能为空", exception.getMessage());
    }

    @Test
    void testGetActivitiesByProjectId_Success() {
        // 准备测试数据
        when(charityActivityMapper.selectByProjectId(1L)).thenReturn(testActivities);

        // 执行测试
        List<CharityActivityDTO> result = charityActivityService.getActivitiesByProjectId(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testActivity.getId(), result.get(0).getId());
        assertEquals(testActivity.getTitle(), result.get(0).getTitle());

        // 验证方法调用
        verify(charityActivityMapper, times(1)).selectByProjectId(1L);
    }

    @Test
    void testGetActivitiesByProjectId_NullProjectId() {
        // 执行测试并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            charityActivityService.getActivitiesByProjectId(null);
        });

        assertEquals("项目ID不能为空", exception.getMessage());
    }

    @Test
    void testTimestampFieldsInDTOConversion() {
        // 准备测试数据 - 确保时间戳字段有值
        testActivity.setCreateTime(LocalDateTime.of(2024, 12, 18, 10, 30, 0));
        testActivity.setUpdateTime(LocalDateTime.of(2024, 12, 18, 15, 45, 30));
        
        when(charityActivityMapper.selectById(1L)).thenReturn(testActivity);

        // 执行测试
        CharityActivityDTO result = charityActivityService.getCharityActivityById(1L);

        // 验证时间戳字段的转换
        assertNotNull(result);
        assertNotNull(result.getCreateTime(), "创建时间应该正确转换");
        assertNotNull(result.getUpdateTime(), "更新时间应该正确转换");
        
        assertEquals(LocalDateTime.of(2024, 12, 18, 10, 30, 0), result.getCreateTime());
        assertEquals(LocalDateTime.of(2024, 12, 18, 15, 45, 30), result.getUpdateTime());
    }

    @Test
    void testQueryWithAllParametersAndTimestampFields() {
        // 准备测试数据 - 包含完整的时间戳信息
        testActivity.setCreateTime(LocalDateTime.now().minusDays(1));
        testActivity.setUpdateTime(LocalDateTime.now().minusHours(2));
        testActivities = Arrays.asList(testActivity);
        
        when(charityActivityMapper.selectByQuery(eq("测试"), eq("donation"), eq("planned"), 
                                                any(LocalDate.class), any(LocalDate.class), eq(0), eq(20)))
            .thenReturn(testActivities);
        when(charityActivityMapper.countByQuery(eq("测试"), eq("donation"), eq("planned"), 
                                               any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(1L);

        // 执行测试
        PageResult<CharityActivityDTO> result = charityActivityService.getCharityActivities(
            "测试", "donation", "planned", LocalDate.now(), LocalDate.now().plusDays(30), 1, 20);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getTotal());
        assertEquals(1, result.getList().size());
        
        CharityActivityDTO dto = result.getList().get(0);
        assertNotNull(dto.getCreateTime(), "创建时间应该在API响应中正确返回");
        assertNotNull(dto.getUpdateTime(), "更新时间应该在API响应中正确返回");
        
        // 验证时间戳字段的值是合理的
        assertTrue(dto.getCreateTime().isBefore(LocalDateTime.now()), "创建时间应该在当前时间之前");
        assertTrue(dto.getUpdateTime().isBefore(LocalDateTime.now()), "更新时间应该在当前时间之前");
        assertTrue(dto.getCreateTime().isBefore(dto.getUpdateTime()) || 
                  dto.getCreateTime().equals(dto.getUpdateTime()), 
                  "创建时间应该早于或等于更新时间");
    }

    @Test
    void testQueryOptimizationIntegration() {
        // 测试查询优化器的集成
        Map<String, Integer> optimizedParams = new HashMap<>();
        optimizedParams.put("page", 1);
        optimizedParams.put("size", 50); // 优化后的大小
        optimizedParams.put("offset", 0);
        
        when(queryOptimizer.optimizePagination(1, 100)).thenReturn(optimizedParams);
        when(queryOptimizer.optimizeKeyword("  测试关键词  ")).thenReturn("测试关键词");
        
        when(charityActivityMapper.selectByQuery(eq("测试关键词"), any(), any(), any(), any(), eq(0), eq(50)))
            .thenReturn(testActivities);
        when(charityActivityMapper.countByQuery(eq("测试关键词"), any(), any(), any(), any()))
            .thenReturn(1L);

        // 执行测试
        PageResult<CharityActivityDTO> result = charityActivityService.getCharityActivities(
            "  测试关键词  ", null, null, null, null, 1, 100);

        // 验证结果
        assertNotNull(result);
        assertEquals(50, result.getSize()); // 应该使用优化后的大小

        // 验证优化器被正确调用
        verify(queryOptimizer, times(1)).optimizePagination(1, 100);
        verify(queryOptimizer, times(1)).optimizeKeyword("  测试关键词  ");
        verify(charityActivityMapper, times(1)).selectByQuery(eq("测试关键词"), any(), any(), any(), any(), eq(0), eq(50));
    }
}