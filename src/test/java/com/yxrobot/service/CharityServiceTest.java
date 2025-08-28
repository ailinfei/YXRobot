package com.yxrobot.service;

import com.yxrobot.dto.CharityStatsDTO;
import com.yxrobot.entity.CharityStats;
import com.yxrobot.mapper.CharityStatsMapper;
import com.yxrobot.validation.CharityDataValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * CharityService单元测试类
 * 测试公益统计数据服务的各项功能
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-18
 */
@ExtendWith(MockitoExtension.class)
class CharityServiceTest {

    @Mock
    private CharityStatsMapper charityStatsMapper;

    @Mock
    private CharityStatsLogService charityStatsLogService;

    @Mock
    private CharityDataValidator charityDataValidator;

    // CacheService已移除，不再需要Mock

    @InjectMocks
    private CharityService charityService;

    private CharityStats testCharityStats;
    private CharityStatsDTO testCharityStatsDTO;
    private Map<String, Object> testEnhancedData;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        testCharityStats = new CharityStats();
        testCharityStats.setId(1L);
        testCharityStats.setTotalBeneficiaries(1000);
        testCharityStats.setTotalInstitutions(50);
        testCharityStats.setTotalActivities(200);
        testCharityStats.setTotalProjects(100);
        testCharityStats.setTotalRaised(new BigDecimal("500000.00"));
        testCharityStats.setTotalDonated(new BigDecimal("450000.00"));
        testCharityStats.setActiveProjects(30);
        testCharityStats.setCompletedProjects(70);
        testCharityStats.setCooperatingInstitutions(45);
        testCharityStats.setTotalVolunteers(300);
        testCharityStats.setThisMonthActivities(15);
        testCharityStats.setVersion(1);
        testCharityStats.setUpdateTime(LocalDateTime.now());
        testCharityStats.setCreateTime(LocalDateTime.now());

        testCharityStatsDTO = new CharityStatsDTO();
        testCharityStatsDTO.setId(1L);
        testCharityStatsDTO.setTotalBeneficiaries(1000);
        testCharityStatsDTO.setTotalInstitutions(50);
        testCharityStatsDTO.setTotalActivities(200);
        testCharityStatsDTO.setTotalProjects(100);
        testCharityStatsDTO.setTotalRaised(new BigDecimal("500000.00"));
        testCharityStatsDTO.setTotalDonated(new BigDecimal("450000.00"));
        testCharityStatsDTO.setActiveProjects(30);
        testCharityStatsDTO.setCompletedProjects(70);
        testCharityStatsDTO.setCooperatingInstitutions(45);
        testCharityStatsDTO.setTotalVolunteers(300);
        testCharityStatsDTO.setThisMonthActivities(15);
        testCharityStatsDTO.setVersion(1);
        testCharityStatsDTO.setUpdateTime(LocalDateTime.now());
        testCharityStatsDTO.setCreateTime(LocalDateTime.now());

        testEnhancedData = new HashMap<>();
        testEnhancedData.put("totalBeneficiaries", 1000);
        testEnhancedData.put("totalInstitutions", 50);
        testEnhancedData.put("beneficiariesGrowthRate", new BigDecimal("5.2"));
        testEnhancedData.put("institutionsGrowthRate", new BigDecimal("2.1"));
    }

    @Test
    void testGetCharityStats_Success() {
        // 准备测试数据
        when(charityStatsMapper.selectLatest()).thenReturn(testCharityStats);

        // 执行测试
        CharityStatsDTO result = charityService.getCharityStats();

        // 验证结果
        assertNotNull(result);
        assertEquals(testCharityStats.getId(), result.getId());
        assertEquals(testCharityStats.getTotalBeneficiaries(), result.getTotalBeneficiaries());
        assertEquals(testCharityStats.getTotalInstitutions(), result.getTotalInstitutions());
        assertEquals(testCharityStats.getTotalActivities(), result.getTotalActivities());
        assertEquals(testCharityStats.getTotalProjects(), result.getTotalProjects());

        // 验证方法调用
        verify(charityStatsMapper, times(1)).selectLatest();
    }

    @Test
    void testGetCharityStats_NoDataFound() {
        // 准备测试数据
        when(charityStatsMapper.selectLatest()).thenReturn(null);

        // 执行测试
        CharityStatsDTO result = charityService.getCharityStats();

        // 验证结果
        assertNotNull(result);
        assertEquals(0, result.getTotalBeneficiaries());
        assertEquals(0, result.getTotalInstitutions());
        assertEquals(0, result.getTotalActivities());
        assertEquals(0, result.getTotalProjects());
        assertEquals(BigDecimal.ZERO, result.getTotalDonated());

        // 验证方法调用
        verify(charityStatsMapper, times(1)).selectLatest();
    }

    @Test
    void testGetEnhancedCharityStats_Success() {
        // 准备测试数据
        List<CharityStats> historyList = new ArrayList<>();
        historyList.add(testCharityStats);
        
        CharityStats previousStats = new CharityStats();
        previousStats.setTotalBeneficiaries(950);
        previousStats.setTotalInstitutions(48);
        previousStats.setTotalRaised(new BigDecimal("480000.00"));
        historyList.add(previousStats);
        
        when(charityStatsMapper.selectLatest()).thenReturn(testCharityStats);
        when(charityStatsMapper.selectHistory(2)).thenReturn(historyList);

        // 执行测试
        CharityStatsDTO result = charityService.getEnhancedCharityStats();

        // 验证结果
        assertNotNull(result);
        assertEquals(testCharityStats.getId(), result.getId());
        assertEquals(testCharityStats.getTotalBeneficiaries(), result.getTotalBeneficiaries());
        
        // 验证增长率计算
        assertNotNull(result.getBeneficiariesGrowthRate());
        assertNotNull(result.getRaisedGrowthRate());
        assertNotNull(result.getProjectsGrowthRate());

        // 验证方法调用
        verify(charityStatsMapper, times(1)).selectLatest();
        verify(charityStatsMapper, times(1)).selectHistory(2);
    }

    @Test
    void testUpdateCharityStats_Success() {
        // 准备测试数据
        when(charityStatsMapper.selectLatest()).thenReturn(testCharityStats);
        when(charityStatsMapper.updateById(any(CharityStats.class))).thenReturn(1);
        when(charityStatsLogService.logStatsUpdate(any(), anyString())).thenReturn(1L);

        // 执行测试
        CharityStatsDTO result = charityService.updateCharityStats(testCharityStatsDTO, 1L, "测试用户", "127.0.0.1", "测试更新");

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.getVersion()); // 版本应该递增

        // 验证方法调用
        verify(charityStatsMapper, times(1)).selectLatest();
        verify(charityStatsMapper, times(1)).updateById(any(CharityStats.class));
        verify(charityStatsLogService, times(1)).logStatsUpdate(any(), anyString());
    }

    @Test
    void testRecalculateCharityStats_Success() {
        // 准备测试数据
        when(charityStatsMapper.calculateStatsFromSource()).thenReturn(testCharityStats);
        when(charityStatsMapper.selectLatest()).thenReturn(testCharityStats);
        when(charityStatsMapper.updateById(any(CharityStats.class))).thenReturn(1);
        when(charityStatsLogService.logStatsUpdate(any(), anyString())).thenReturn(1L);

        // 执行测试
        CharityStatsDTO result = charityService.recalculateCharityStats(1L, "测试用户", "127.0.0.1");

        // 验证结果
        assertNotNull(result);
        assertEquals(testCharityStats.getTotalBeneficiaries(), result.getTotalBeneficiaries());

        // 验证方法调用
        verify(charityStatsMapper, times(1)).calculateStatsFromSource();
        verify(charityStatsMapper, times(1)).selectLatest();
        verify(charityStatsMapper, times(1)).updateById(any(CharityStats.class));
        verify(charityStatsLogService, times(1)).logStatsUpdate(any(), anyString());
    }

    @Test
    void testGetStatsHistory_Success() {
        // 准备测试数据
        List<CharityStats> historyList = new ArrayList<>();
        historyList.add(testCharityStats);
        when(charityStatsMapper.selectHistory(10)).thenReturn(historyList);

        // 执行测试
        List<CharityStatsDTO> result = charityService.getStatsHistory(10);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCharityStats.getId(), result.get(0).getId());

        // 验证方法调用
        verify(charityStatsMapper, times(1)).selectHistory(10);
    }

    @Test
    void testGetStatsTrend_Success() {
        // 准备测试数据
        List<Map<String, Object>> trendDataList = new ArrayList<>();
        Map<String, Object> trendItem = new HashMap<>();
        trendItem.put("date", "2024-12-01");
        trendItem.put("beneficiaries", 1000);
        trendDataList.add(trendItem);
        
        when(charityStatsMapper.selectStatsTrend(30)).thenReturn(trendDataList);

        // 执行测试
        Map<String, Object> result = charityService.getStatsTrend(30);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.containsKey("trendData"));
        assertTrue(result.containsKey("days"));
        assertEquals(30, result.get("days"));

        // 验证方法调用
        verify(charityStatsMapper, times(1)).selectStatsTrend(30);
    }

    @Test
    void testValidateStatsConsistency_Success() {
        // 执行测试
        Map<String, Object> result = charityService.validateStatsConsistency(testCharityStatsDTO);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.containsKey("is_valid"));
        assertTrue(result.containsKey("errors"));
        assertTrue(result.containsKey("warnings"));
        assertEquals("true", result.get("is_valid"));
    }

    @Test
    void testValidateStatsConsistency_WithErrors() {
        // 准备无效数据
        CharityStatsDTO invalidDTO = new CharityStatsDTO();
        invalidDTO.setTotalBeneficiaries(-1); // 负数应该产生错误
        invalidDTO.setTotalDonated(new BigDecimal("-100")); // 负数应该产生错误

        // 执行测试
        Map<String, Object> result = charityService.validateStatsConsistency(invalidDTO);

        // 验证结果
        assertNotNull(result);
        assertEquals("false", result.get("is_valid"));
        
        @SuppressWarnings("unchecked")
        List<String> errors = (List<String>) result.get("errors");
        assertFalse(errors.isEmpty());
    }
}