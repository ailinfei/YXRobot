package com.yxrobot.mapper;

import com.yxrobot.entity.RentalRecord;
import com.yxrobot.dto.RentalTrendDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 租赁记录数据访问层接口
 * 
 * @author Kiro
 * @date 2025-01-28
 */
@Mapper
public interface RentalRecordMapper {
    
    /**
     * 根据ID查询租赁记录
     */
    RentalRecord selectById(@Param("id") Long id);
    
    /**
     * 根据订单号查询租赁记录
     */
    RentalRecord selectByOrderNumber(@Param("orderNumber") String orderNumber);
    
    /**
     * 分页查询租赁记录列表
     */
    List<RentalRecord> selectList(@Param("params") Map<String, Object> params);
    
    /**
     * 查询租赁记录总数
     */
    Long selectCount(@Param("params") Map<String, Object> params);
    
    /**
     * 插入租赁记录
     */
    int insert(RentalRecord rentalRecord);
    
    /**
     * 更新租赁记录
     */
    int updateById(RentalRecord rentalRecord);
    
    /**
     * 软删除租赁记录
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 批量软删除租赁记录
     */
    int deleteBatchByIds(@Param("ids") List<Long> ids);
    
    /**
     * 查询租赁统计数据
     */
    Map<String, Object> selectRentalStats(@Param("startDate") LocalDate startDate, 
                                         @Param("endDate") LocalDate endDate);
    
    /**
     * 查询租赁趋势数据
     */
    List<RentalTrendDTO> selectRentalTrends(@Param("startDate") LocalDate startDate, 
                                           @Param("endDate") LocalDate endDate,
                                           @Param("period") String period);
    
    /**
     * 查询今日租赁统计
     */
    Map<String, Object> selectTodayStats(@Param("today") LocalDate today);
    
    /**
     * 查询地区分布数据
     */
    List<Map<String, Object>> selectRegionDistribution(@Param("startDate") LocalDate startDate, 
                                                       @Param("endDate") LocalDate endDate);
    
    /**
     * 查询渠道分析数据
     */
    List<Map<String, Object>> selectChannelAnalysis(@Param("startDate") LocalDate startDate, 
                                                    @Param("endDate") LocalDate endDate);
    
    /**
     * 查询月度收入增长率
     */
    Map<String, Object> selectRevenueGrowthRate(@Param("currentMonth") LocalDate currentMonth,
                                               @Param("previousMonth") LocalDate previousMonth);
}