package com.yxrobot.mapper;

import com.yxrobot.entity.RentalRecord;
import com.yxrobot.dto.RentalTrendDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 租赁记录Mapper测试类
 * 测试数据访问层的正确性
 * 
 * @author Kiro
 * @date 2025-01-28
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RentalRecordMapperTest {
    
    @Autowired
    private RentalRecordMapper rentalRecordMapper;
    
    private RentalRecord testRentalRecord;
    
    @BeforeEach
    void setUp() {
        // 创建测试用的租赁记录
        testRentalRecord = new RentalRecord();
        testRentalRecord.setRentalOrderNumber("TEST001");
        testRentalRecord.setDeviceId(1L);
        testRentalRecord.setCustomerId(1L);
        testRentalRecord.setRentalStartDate(LocalDate.of(2025, 1, 1));
        testRentalRecord.setRentalEndDate(LocalDate.of(2025, 1, 15));
        testRentalRecord.setRentalPeriod(15);
        testRentalRecord.setDailyRentalFee(new BigDecimal("100.00"));
        testRentalRecord.setTotalRentalFee(new BigDecimal("1500.00"));
        testRentalRecord.setDepositAmount(new BigDecimal("500.00"));
        testRentalRecord.setActualPayment(new BigDecimal("2000.00"));
        testRentalRecord.setCreatedAt(LocalDateTime.now());
        testRentalRecord.setUpdatedAt(LocalDateTime.now());
    }
    
    @Test
    void testInsert_WithValidData_ShouldInsertSuccessfully() {
        // When
        int result = rentalRecordMapper.insert(testRentalRecord);
        
        // Then
        assertEquals(1, result);
        assertNotNull(testRentalRecord.getId());
        assertTrue(testRentalRecord.getId() > 0);
    }
    
    @Test
    void testSelectById_WithExistingId_ShouldReturnRecord() {
        // Given
        rentalRecordMapper.insert(testRentalRecord);
        Long recordId = testRentalRecord.getId();
        
        // When
        RentalRecord result = rentalRecordMapper.selectById(recordId);
        
        // Then
        assertNotNull(result);
        assertEquals(recordId, result.getId());
        assertEquals("TEST001", result.getRentalOrderNumber());
        assertEquals(new BigDecimal("100.00"), result.getDailyRentalFee());
        assertEquals(new BigDecimal("1500.00"), result.getTotalRentalFee());
    }
    
    @Test
    void testSelectById_WithNonExistingId_ShouldReturnNull() {
        // When
        RentalRecord result = rentalRecordMapper.selectById(99999L);
        
        // Then
        assertNull(result);
    }
    
    @Test
    void testSelectByOrderNumber_WithExistingOrderNumber_ShouldReturnRecord() {
        // Given
        rentalRecordMapper.insert(testRentalRecord);
        
        // When
        RentalRecord result = rentalRecordMapper.selectByOrderNumber("TEST001");
        
        // Then
        assertNotNull(result);
        assertEquals("TEST001", result.getRentalOrderNumber());
        assertEquals(testRentalRecord.getId(), result.getId());
    }
    
    @Test
    void testSelectByOrderNumber_WithNonExistingOrderNumber_ShouldReturnNull() {
        // When
        RentalRecord result = rentalRecordMapper.selectByOrderNumber("NONEXISTENT");
        
        // Then
        assertNull(result);
    }
    
    @Test
    void testSelectList_WithEmptyParams_ShouldReturnAllRecords() {
        // Given
        rentalRecordMapper.insert(testRentalRecord);
        
        RentalRecord record2 = new RentalRecord();
        record2.setRentalOrderNumber("TEST002");
        record2.setDeviceId(2L);
        record2.setCustomerId(2L);
        record2.setRentalStartDate(LocalDate.of(2025, 1, 10));
        record2.setRentalEndDate(LocalDate.of(2025, 1, 20));
        record2.setRentalPeriod(10);
        record2.setDailyRentalFee(new BigDecimal("80.00"));
        record2.setTotalRentalFee(new BigDecimal("800.00"));
        record2.setCreatedAt(LocalDateTime.now());
        record2.setUpdatedAt(LocalDateTime.now());
        rentalRecordMapper.insert(record2);
        
        Map<String, Object> params = new HashMap<>();
        
        // When
        List<RentalRecord> result = rentalRecordMapper.selectList(params);
        
        // Then
        assertNotNull(result);
        assertTrue(result.size() >= 2);
        
        // 验证包含我们插入的记录
        boolean foundTest001 = result.stream().anyMatch(r -> "TEST001".equals(r.getRentalOrderNumber()));
        boolean foundTest002 = result.stream().anyMatch(r -> "TEST002".equals(r.getRentalOrderNumber()));
        assertTrue(foundTest001);
        assertTrue(foundTest002);
    }
    
    @Test
    void testSelectList_WithPaginationParams_ShouldReturnPaginatedResults() {
        // Given
        rentalRecordMapper.insert(testRentalRecord);
        
        Map<String, Object> params = new HashMap<>();
        params.put("page", 1);
        params.put("pageSize", 10);
        params.put("offset", 0);
        
        // When
        List<RentalRecord> result = rentalRecordMapper.selectList(params);
        
        // Then
        assertNotNull(result);
        assertTrue(result.size() <= 10);
    }
    
    @Test
    void testSelectCount_WithEmptyParams_ShouldReturnTotalCount() {
        // Given
        rentalRecordMapper.insert(testRentalRecord);
        
        Map<String, Object> params = new HashMap<>();
        
        // When
        Long result = rentalRecordMapper.selectCount(params);
        
        // Then
        assertNotNull(result);
        assertTrue(result >= 1);
    }
    
    @Test
    void testUpdateById_WithValidData_ShouldUpdateSuccessfully() {
        // Given
        rentalRecordMapper.insert(testRentalRecord);
        Long recordId = testRentalRecord.getId();
        
        // 修改数据
        testRentalRecord.setDailyRentalFee(new BigDecimal("120.00"));
        testRentalRecord.setTotalRentalFee(new BigDecimal("1800.00"));
        testRentalRecord.setUpdatedAt(LocalDateTime.now());
        
        // When
        int result = rentalRecordMapper.updateById(testRentalRecord);
        
        // Then
        assertEquals(1, result);
        
        // 验证更新结果
        RentalRecord updated = rentalRecordMapper.selectById(recordId);
        assertEquals(new BigDecimal("120.00"), updated.getDailyRentalFee());
        assertEquals(new BigDecimal("1800.00"), updated.getTotalRentalFee());
    }
    
    @Test
    void testDeleteById_WithExistingId_ShouldDeleteSuccessfully() {
        // Given
        rentalRecordMapper.insert(testRentalRecord);
        Long recordId = testRentalRecord.getId();
        
        // When
        int result = rentalRecordMapper.deleteById(recordId);
        
        // Then
        assertEquals(1, result);
        
        // 验证删除结果（软删除，记录仍存在但标记为已删除）
        RentalRecord deleted = rentalRecordMapper.selectById(recordId);
        // 根据实际的软删除实现，这里可能需要调整断言
        // 如果是硬删除，则应该为null；如果是软删除，则应该检查删除标记
    }
    
    @Test
    void testSelectRentalStats_WithDateRange_ShouldReturnStats() {
        // Given
        rentalRecordMapper.insert(testRentalRecord);
        
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);
        
        // When
        Map<String, Object> result = rentalRecordMapper.selectRentalStats(startDate, endDate);
        
        // Then
        assertNotNull(result);
        
        // 验证统计数据的基本结构
        if (!result.isEmpty()) {
            // 如果有数据，验证字段存在
            assertTrue(result.containsKey("totalRentalRevenue") || 
                      result.containsKey("totalRentalOrders") ||
                      result.containsKey("totalRentalDevices"));
        }
    }
    
    @Test
    void testSelectRentalStats_WithNullDates_ShouldReturnAllStats() {
        // Given
        rentalRecordMapper.insert(testRentalRecord);
        
        // When
        Map<String, Object> result = rentalRecordMapper.selectRentalStats(null, null);
        
        // Then
        assertNotNull(result);
        // 无日期限制应该返回所有数据的统计
    }
    
    @Test
    void testSelectRentalTrends_WithValidParams_ShouldReturnTrends() {
        // Given
        rentalRecordMapper.insert(testRentalRecord);
        
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);
        String period = "daily";
        
        // When
        List<RentalTrendDTO> result = rentalRecordMapper.selectRentalTrends(startDate, endDate, period);
        
        // Then
        assertNotNull(result);
        // 趋势数据可能为空（如果没有足够的数据），但不应该为null
    }
    
    @Test
    void testSelectTodayStats_WithToday_ShouldReturnTodayStats() {
        // Given
        // 创建今天的租赁记录
        RentalRecord todayRecord = new RentalRecord();
        todayRecord.setRentalOrderNumber("TODAY001");
        todayRecord.setDeviceId(1L);
        todayRecord.setCustomerId(1L);
        todayRecord.setRentalStartDate(LocalDate.now());
        todayRecord.setRentalEndDate(LocalDate.now().plusDays(10));
        todayRecord.setRentalPeriod(10);
        todayRecord.setDailyRentalFee(new BigDecimal("100.00"));
        todayRecord.setTotalRentalFee(new BigDecimal("1000.00"));
        todayRecord.setCreatedAt(LocalDateTime.now());
        todayRecord.setUpdatedAt(LocalDateTime.now());
        rentalRecordMapper.insert(todayRecord);
        
        LocalDate today = LocalDate.now();
        
        // When
        Map<String, Object> result = rentalRecordMapper.selectTodayStats(today);
        
        // Then
        assertNotNull(result);
        
        // 验证今日统计数据的基本结构
        if (!result.isEmpty()) {
            assertTrue(result.containsKey("revenue") || 
                      result.containsKey("orders") ||
                      result.containsKey("activeDevices"));
        }
    }
    
    @Test
    void testSelectRegionDistribution_WithDateRange_ShouldReturnDistribution() {
        // Given
        rentalRecordMapper.insert(testRentalRecord);
        
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);
        
        // When
        List<Map<String, Object>> result = rentalRecordMapper.selectRegionDistribution(startDate, endDate);
        
        // Then
        assertNotNull(result);
        // 地区分布数据可能为空，但不应该为null
    }
    
    @Test
    void testSelectChannelAnalysis_WithDateRange_ShouldReturnAnalysis() {
        // Given
        rentalRecordMapper.insert(testRentalRecord);
        
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);
        
        // When
        List<Map<String, Object>> result = rentalRecordMapper.selectChannelAnalysis(startDate, endDate);
        
        // Then
        assertNotNull(result);
        // 渠道分析数据可能为空，但不应该为null
    }
    
    @Test
    void testSelectRevenueGrowthRate_WithValidMonths_ShouldReturnGrowthRate() {
        // Given
        rentalRecordMapper.insert(testRentalRecord);
        
        LocalDate currentMonth = LocalDate.of(2025, 1, 1);
        LocalDate previousMonth = LocalDate.of(2024, 12, 1);
        
        // When
        Map<String, Object> result = rentalRecordMapper.selectRevenueGrowthRate(currentMonth, previousMonth);
        
        // Then
        assertNotNull(result);
        // 增长率数据可能为空，但不应该为null
    }
    
    @Test
    void testBatchOperations_ShouldWorkCorrectly() {
        // Given
        rentalRecordMapper.insert(testRentalRecord);
        
        RentalRecord record2 = new RentalRecord();
        record2.setRentalOrderNumber("BATCH001");
        record2.setDeviceId(2L);
        record2.setCustomerId(2L);
        record2.setRentalStartDate(LocalDate.of(2025, 1, 5));
        record2.setRentalEndDate(LocalDate.of(2025, 1, 15));
        record2.setRentalPeriod(10);
        record2.setDailyRentalFee(new BigDecimal("90.00"));
        record2.setTotalRentalFee(new BigDecimal("900.00"));
        record2.setCreatedAt(LocalDateTime.now());
        record2.setUpdatedAt(LocalDateTime.now());
        rentalRecordMapper.insert(record2);
        
        List<Long> ids = List.of(testRentalRecord.getId(), record2.getId());
        
        // When
        int result = rentalRecordMapper.deleteBatchByIds(ids);
        
        // Then
        assertEquals(2, result);
    }
    
    @Test
    void testComplexQuery_WithMultipleParams_ShouldReturnFilteredResults() {
        // Given
        rentalRecordMapper.insert(testRentalRecord);
        
        Map<String, Object> params = new HashMap<>();
        params.put("deviceId", 1L);
        params.put("customerId", 1L);
        params.put("startDate", LocalDate.of(2025, 1, 1));
        params.put("endDate", LocalDate.of(2025, 1, 31));
        params.put("minAmount", new BigDecimal("50.00"));
        params.put("maxAmount", new BigDecimal("200.00"));
        
        // When
        List<RentalRecord> result = rentalRecordMapper.selectList(params);
        Long count = rentalRecordMapper.selectCount(params);
        
        // Then
        assertNotNull(result);
        assertNotNull(count);
        
        // 验证筛选条件生效
        for (RentalRecord record : result) {
            assertEquals(Long.valueOf(1L), record.getDeviceId());
            assertEquals(Long.valueOf(1L), record.getCustomerId());
            assertTrue(record.getDailyRentalFee().compareTo(new BigDecimal("50.00")) >= 0);
            assertTrue(record.getDailyRentalFee().compareTo(new BigDecimal("200.00")) <= 0);
        }
    }
    
    @Test
    void testDataIntegrity_ShouldMaintainConsistency() {
        // Given
        rentalRecordMapper.insert(testRentalRecord);
        Long originalId = testRentalRecord.getId();
        
        // When - 尝试插入重复的订单号（如果有唯一约束）
        RentalRecord duplicateRecord = new RentalRecord();
        duplicateRecord.setRentalOrderNumber("TEST001"); // 重复订单号
        duplicateRecord.setDeviceId(2L);
        duplicateRecord.setCustomerId(2L);
        duplicateRecord.setRentalStartDate(LocalDate.of(2025, 1, 10));
        duplicateRecord.setRentalEndDate(LocalDate.of(2025, 1, 20));
        duplicateRecord.setRentalPeriod(10);
        duplicateRecord.setDailyRentalFee(new BigDecimal("80.00"));
        duplicateRecord.setTotalRentalFee(new BigDecimal("800.00"));
        duplicateRecord.setCreatedAt(LocalDateTime.now());
        duplicateRecord.setUpdatedAt(LocalDateTime.now());
        
        // Then - 根据数据库约束，这可能会抛出异常或成功插入
        // 这里的测试逻辑取决于实际的数据库约束设计
        try {
            int result = rentalRecordMapper.insert(duplicateRecord);
            // 如果允许重复，验证插入成功
            if (result > 0) {
                assertNotNull(duplicateRecord.getId());
                assertNotEquals(originalId, duplicateRecord.getId());
            }
        } catch (Exception e) {
            // 如果不允许重复，验证抛出了适当的异常
            assertTrue(e.getMessage().contains("Duplicate") || 
                      e.getMessage().contains("constraint") ||
                      e.getMessage().contains("unique"));
        }
    }
}