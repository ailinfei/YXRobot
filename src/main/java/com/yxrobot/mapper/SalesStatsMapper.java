package com.yxrobot.mapper;

import com.yxrobot.entity.SalesStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 销售统计Mapper接口
 * 对应数据库表：sales_stats
 * 
 * 字段映射规范：
 * - 数据库字段使用snake_case命名
 * - Java属性使用camelCase命名
 * - MyBatis映射文件中确保column和property正确对应
 */
@Mapper
public interface SalesStatsMapper {
    
    /**
     * 根据ID查询销售统计
     */
    SalesStats selectById(@Param("id") Long id);
    
    /**
     * 根据日期和类型查询销售统计
     */
    SalesStats selectByDateAndType(@Param("statDate") LocalDate statDate, @Param("statType") String statType);
    
    /**
     * 查询所有销售统计列表
     */
    List<SalesStats> selectAll();
    
    /**
     * 分页查询销售统计列表
     */
    List<SalesStats> selectList(@Param("page") Integer page, 
                               @Param("size") Integer size, 
                               @Param("statType") String statType,
                               @Param("startDate") LocalDate startDate,
                               @Param("endDate") LocalDate endDate);
    
    /**
     * 查询销售统计总数
     */
    Long selectCount(@Param("statType") String statType,
                    @Param("startDate") LocalDate startDate,
                    @Param("endDate") LocalDate endDate);
    
    /**
     * 查询销售统计列表（带关联信息）
     */
    List<Map<String, Object>> selectListWithDetails(@Param("page") Integer page, 
                                                    @Param("size") Integer size, 
                                                    @Param("statType") String statType,
                                                    @Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate);
    
    /**
     * 查询销售统计总数（带关联信息）
     */
    Long selectCountWithDetails(@Param("statType") String statType,
                               @Param("startDate") LocalDate startDate,
                               @Param("endDate") LocalDate endDate);
    
    /**
     * 插入销售统计
     */
    int insert(SalesStats salesStats);
    
    /**
     * 批量插入销售统计
     */
    int insertBatch(@Param("list") List<SalesStats> salesStatsList);
    
    /**
     * 更新销售统计
     */
    int updateById(SalesStats salesStats);
    
    /**
     * 根据ID删除销售统计
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据日期和类型删除销售统计
     */
    int deleteByDateAndType(@Param("statDate") LocalDate statDate, @Param("statType") String statType);
    
    /**
     * 批量删除销售统计
     */
    int deleteByIds(@Param("ids") List<Long> ids);
    
    /**
     * 根据统计类型查询销售统计
     */
    List<SalesStats> selectByStatType(@Param("statType") String statType);
    
    /**
     * 根据日期范围查询销售统计
     */
    List<SalesStats> selectByDateRange(@Param("startDate") LocalDate startDate, 
                                      @Param("endDate") LocalDate endDate);
    
    /**
     * 查询最新的销售统计
     */
    SalesStats selectLatestByType(@Param("statType") String statType);
    
    /**
     * 查询日统计数据
     */
    List<SalesStats> selectDailyStats(@Param("startDate") LocalDate startDate, 
                                     @Param("endDate") LocalDate endDate);
    
    /**
     * 查询周统计数据
     */
    List<SalesStats> selectWeeklyStats(@Param("startDate") LocalDate startDate, 
                                      @Param("endDate") LocalDate endDate);
    
    /**
     * 查询月统计数据
     */
    List<SalesStats> selectMonthlyStats(@Param("startDate") LocalDate startDate, 
                                       @Param("endDate") LocalDate endDate);
    
    /**
     * 查询年统计数据
     */
    List<SalesStats> selectYearlyStats(@Param("startDate") LocalDate startDate, 
                                      @Param("endDate") LocalDate endDate);
    
    /**
     * 查询销售趋势数据
     */
    List<Map<String, Object>> selectSalesTrends(@Param("statType") String statType,
                                               @Param("startDate") LocalDate startDate, 
                                               @Param("endDate") LocalDate endDate);
    
    /**
     * 查询同比增长数据
     */
    List<Map<String, Object>> selectYearOverYearGrowth(@Param("statType") String statType,
                                                      @Param("currentDate") LocalDate currentDate);
    
    /**
     * 查询环比增长数据
     */
    List<Map<String, Object>> selectMonthOverMonthGrowth(@Param("statType") String statType,
                                                        @Param("currentDate") LocalDate currentDate);
    
    /**
     * 查询关键指标汇总
     */
    Map<String, Object> selectKeyMetricsSummary(@Param("startDate") LocalDate startDate, 
                                               @Param("endDate") LocalDate endDate);
    
    /**
     * 查询实时统计数据
     */
    Map<String, Object> selectRealTimeStats();
    
    /**
     * 查询今日统计数据
     */
    Map<String, Object> selectTodayStats();
    
    /**
     * 查询本周统计数据
     */
    Map<String, Object> selectCurrentWeekStats();
    
    /**
     * 查询本月统计数据
     */
    Map<String, Object> selectCurrentMonthStats();
    
    /**
     * 查询本年统计数据
     */
    Map<String, Object> selectCurrentYearStats();
    
    /**
     * 查询统计数据对比
     */
    Map<String, Object> selectStatsComparison(@Param("currentStartDate") LocalDate currentStartDate,
                                             @Param("currentEndDate") LocalDate currentEndDate,
                                             @Param("previousStartDate") LocalDate previousStartDate,
                                             @Param("previousEndDate") LocalDate previousEndDate);
    
    /**
     * 查询预警数据
     */
    List<Map<String, Object>> selectAlertData(@Param("thresholds") Map<String, Object> thresholds);
    
    /**
     * 生成日统计数据
     */
    int generateDailyStats(@Param("statDate") LocalDate statDate);
    
    /**
     * 生成周统计数据
     */
    int generateWeeklyStats(@Param("statDate") LocalDate statDate);
    
    /**
     * 生成月统计数据
     */
    int generateMonthlyStats(@Param("statDate") LocalDate statDate);
    
    /**
     * 生成年统计数据
     */
    int generateYearlyStats(@Param("statDate") LocalDate statDate);
    
    /**
     * 更新统计数据
     */
    int updateStatsByDateAndType(@Param("statDate") LocalDate statDate, 
                                @Param("statType") String statType);
    
    /**
     * 检查统计数据是否存在
     */
    boolean existsByDateAndType(@Param("statDate") LocalDate statDate, @Param("statType") String statType);
    
    /**
     * 清理过期统计数据
     */
    int cleanupExpiredStats(@Param("beforeDate") LocalDate beforeDate);
    
    /**
     * 查询销售记录日期范围
     */
    Map<String, Object> selectSalesDateRange();
    
    /**
     * 查询销售记录总数
     */
    Long selectTotalRecordsCount();
    
    /**
     * 查询可用年份列表
     */
    List<Integer> selectAvailableYears();
    
    /**
     * 查询可用月份列表
     */
    List<String> selectAvailableMonths();
}