package com.yxrobot.mapper;

import com.yxrobot.dto.CustomerStatsDTO;
import com.yxrobot.entity.CustomerStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 客户统计Mapper接口
 * 对应数据库表：customer_stats
 * 
 * 字段映射规范：
 * - 数据库字段使用snake_case命名（如：stat_date, total_customers, new_customers_this_month）
 * - Java属性使用camelCase命名（如：statDate, totalCustomers, newCustomersThisMonth）
 * - MyBatis映射文件中确保column和property正确对应
 */
@Mapper
public interface CustomerStatsMapper {
    
    // ==================== 基础CRUD操作 ====================
    
    /**
     * 根据ID查询客户统计
     */
    CustomerStats selectById(@Param("id") Long id);
    
    /**
     * 根据统计日期查询客户统计
     */
    CustomerStats selectByStatDate(@Param("statDate") LocalDate statDate);
    
    /**
     * 查询最新的客户统计数据
     */
    CustomerStats selectLatest();
    
    /**
     * 查询客户统计DTO（适配前端统计卡片）
     */
    CustomerStatsDTO selectStatsDTO();
    
    /**
     * 查询指定日期的客户统计DTO
     */
    CustomerStatsDTO selectStatsDTOByDate(@Param("statDate") LocalDate statDate);
    
    /**
     * 查询客户统计列表
     */
    List<CustomerStats> selectList(@Param("startDate") LocalDate startDate, 
                                  @Param("endDate") LocalDate endDate);
    
    /**
     * 插入客户统计
     */
    int insert(CustomerStats customerStats);
    
    /**
     * 更新客户统计
     */
    int updateById(CustomerStats customerStats);
    
    /**
     * 根据统计日期更新客户统计
     */
    int updateByStatDate(CustomerStats customerStats);
    
    /**
     * 删除客户统计
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据统计日期删除客户统计
     */
    int deleteByStatDate(@Param("statDate") LocalDate statDate);
    
    // ==================== 实时统计计算 ====================
    
    /**
     * 计算并返回实时客户统计数据（不依赖统计表）
     */
    CustomerStatsDTO calculateRealTimeStats();
    
    /**
     * 计算总客户数
     */
    Integer calculateTotalCustomers();
    
    /**
     * 计算各等级客户数量
     */
    Map<String, Integer> calculateCustomerLevelCounts();
    
    /**
     * 计算活跃设备数
     */
    Integer calculateActiveDeviceCount();
    
    /**
     * 计算总收入
     */
    BigDecimal calculateTotalRevenue();
    
    /**
     * 计算本月新增客户数
     */
    Integer calculateNewCustomersThisMonth();
    
    /**
     * 计算指定月份新增客户数
     */
    Integer calculateNewCustomersByMonth(@Param("year") Integer year, @Param("month") Integer month);
    
    // ==================== 统计数据生成 ====================
    
    /**
     * 生成指定日期的客户统计数据
     */
    int generateStatsForDate(@Param("statDate") LocalDate statDate);
    
    /**
     * 生成今日客户统计数据
     */
    int generateTodayStats();
    
    /**
     * 批量生成指定日期范围的客户统计数据
     */
    int batchGenerateStats(@Param("startDate") LocalDate startDate, 
                          @Param("endDate") LocalDate endDate);
    
    /**
     * 更新或插入客户统计数据（UPSERT操作）
     */
    int upsertStats(CustomerStats customerStats);
    
    // ==================== 趋势分析查询 ====================
    
    /**
     * 查询客户数量趋势（按日）
     */
    List<Map<String, Object>> selectCustomerTrendByDay(@Param("days") Integer days);
    
    /**
     * 查询客户数量趋势（按月）
     */
    List<Map<String, Object>> selectCustomerTrendByMonth(@Param("months") Integer months);
    
    /**
     * 查询客户等级分布趋势
     */
    List<Map<String, Object>> selectCustomerLevelTrend(@Param("months") Integer months);
    
    /**
     * 查询收入趋势
     */
    List<Map<String, Object>> selectRevenueTrend(@Param("months") Integer months);
    
    /**
     * 查询新增客户趋势
     */
    List<Map<String, Object>> selectNewCustomerTrend(@Param("months") Integer months);
    
    /**
     * 查询活跃设备趋势
     */
    List<Map<String, Object>> selectActiveDeviceTrend(@Param("months") Integer months);
    
    // ==================== 比较分析查询 ====================
    
    /**
     * 比较两个日期的客户统计数据
     */
    Map<String, Object> compareStatsBetweenDates(@Param("date1") LocalDate date1, 
                                                @Param("date2") LocalDate date2);
    
    /**
     * 查询同比增长数据（与去年同期比较）
     */
    Map<String, Object> selectYearOverYearGrowth(@Param("statDate") LocalDate statDate);
    
    /**
     * 查询环比增长数据（与上月比较）
     */
    Map<String, Object> selectMonthOverMonthGrowth(@Param("statDate") LocalDate statDate);
    
    /**
     * 查询周同比增长数据（与上周比较）
     */
    Map<String, Object> selectWeekOverWeekGrowth(@Param("statDate") LocalDate statDate);
    
    // ==================== 高级统计查询 ====================
    
    /**
     * 查询客户统计汇总数据
     */
    Map<String, Object> selectStatsSummary(@Param("startDate") LocalDate startDate, 
                                          @Param("endDate") LocalDate endDate);
    
    /**
     * 查询客户统计平均值
     */
    Map<String, Object> selectStatsAverage(@Param("startDate") LocalDate startDate, 
                                          @Param("endDate") LocalDate endDate);
    
    /**
     * 查询客户统计最大值
     */
    Map<String, Object> selectStatsMaximum(@Param("startDate") LocalDate startDate, 
                                          @Param("endDate") LocalDate endDate);
    
    /**
     * 查询客户统计最小值
     */
    Map<String, Object> selectStatsMinimum(@Param("startDate") LocalDate startDate, 
                                          @Param("endDate") LocalDate endDate);
    
    // ==================== 数据验证和维护 ====================
    
    /**
     * 检查指定日期的统计数据是否存在
     */
    boolean existsByStatDate(@Param("statDate") LocalDate statDate);
    
    /**
     * 查询统计数据的日期范围
     */
    Map<String, Object> selectDateRange();
    
    /**
     * 查询缺失统计数据的日期列表
     */
    List<LocalDate> selectMissingStatsDates(@Param("startDate") LocalDate startDate, 
                                           @Param("endDate") LocalDate endDate);
    
    /**
     * 清理过期的统计数据（保留指定天数）
     */
    int cleanupOldStats(@Param("keepDays") Integer keepDays);
    
    /**
     * 重新计算并更新所有统计数据
     */
    int recalculateAllStats();
    
    /**
     * 验证统计数据的准确性
     */
    List<Map<String, Object>> validateStatsAccuracy(@Param("statDate") LocalDate statDate);
    
    // ==================== 缓存支持查询 ====================
    
    /**
     * 查询用于缓存的基础统计数据
     */
    Map<String, Object> selectBasicStatsForCache();
    
    /**
     * 查询用于缓存的趋势数据
     */
    List<Map<String, Object>> selectTrendDataForCache(@Param("days") Integer days);
    
    /**
     * 查询用于缓存的分布数据
     */
    Map<String, Object> selectDistributionDataForCache();
}