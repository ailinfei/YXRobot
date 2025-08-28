package com.yxrobot.mapper;

import com.yxrobot.dto.SalesRecordQueryDTO;
import com.yxrobot.entity.SalesRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 销售记录Mapper接口
 * 对应数据库表：sales_records
 * 
 * 字段映射规范：
 * - 数据库字段使用snake_case命名
 * - Java属性使用camelCase命名
 * - MyBatis映射文件中确保column和property正确对应
 */
@Mapper
public interface SalesRecordMapper {
    
    /**
     * 根据ID查询销售记录
     */
    SalesRecord selectById(@Param("id") Long id);
    
    /**
     * 根据订单号查询销售记录
     */
    SalesRecord selectByOrderNumber(@Param("orderNumber") String orderNumber);
    
    /**
     * 分页查询销售记录列表
     */
    List<SalesRecord> selectList(@Param("query") SalesRecordQueryDTO query);
    
    /**
     * 查询销售记录总数
     */
    Long selectCount(@Param("query") SalesRecordQueryDTO query);
    
    /**
     * 查询销售记录列表（带关联信息）
     */
    List<Map<String, Object>> selectListWithDetails(@Param("query") SalesRecordQueryDTO query);
    
    /**
     * 查询销售记录DTO列表（包含关联数据，匹配前端需求）
     */
    List<com.yxrobot.dto.SalesRecordDTO> selectDTOList(@Param("query") SalesRecordQueryDTO query);
    
    /**
     * 查询销售记录总数（带关联信息）
     */
    Long selectCountWithDetails(@Param("query") SalesRecordQueryDTO query);
    
    /**
     * 插入销售记录
     */
    int insert(SalesRecord salesRecord);
    
    /**
     * 批量插入销售记录
     */
    int insertBatch(@Param("list") List<SalesRecord> salesRecords);
    
    /**
     * 更新销售记录
     */
    int updateById(SalesRecord salesRecord);
    
    /**
     * 根据ID删除销售记录（物理删除）
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据ID软删除销售记录
     */
    int softDeleteById(@Param("id") Long id);
    
    /**
     * 批量软删除销售记录
     */
    int softDeleteByIds(@Param("ids") List<Long> ids);
    
    /**
     * 根据客户ID查询销售记录
     */
    List<SalesRecord> selectByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 根据产品ID查询销售记录
     */
    List<SalesRecord> selectByProductId(@Param("productId") Long productId);
    
    /**
     * 根据销售人员ID查询销售记录
     */
    List<SalesRecord> selectBySalesStaffId(@Param("salesStaffId") Long salesStaffId);
    
    /**
     * 根据日期范围查询销售记录
     */
    List<SalesRecord> selectByDateRange(@Param("startDate") LocalDate startDate, 
                                       @Param("endDate") LocalDate endDate);
    
    /**
     * 查询指定时间段的销售统计
     */
    Map<String, Object> selectSalesStatsByDateRange(@Param("startDate") LocalDate startDate, 
                                                   @Param("endDate") LocalDate endDate);
    
    /**
     * 查询销售趋势数据
     */
    List<Map<String, Object>> selectSalesTrends(@Param("startDate") LocalDate startDate, 
                                               @Param("endDate") LocalDate endDate, 
                                               @Param("groupBy") String groupBy);
    
    /**
     * 查询产品销售排行
     */
    List<Map<String, Object>> selectProductRanking(@Param("startDate") LocalDate startDate, 
                                                  @Param("endDate") LocalDate endDate, 
                                                  @Param("limit") Integer limit);
    
    /**
     * 查询销售人员业绩排行
     */
    List<Map<String, Object>> selectStaffPerformance(@Param("startDate") LocalDate startDate, 
                                                    @Param("endDate") LocalDate endDate, 
                                                    @Param("limit") Integer limit);
    
    /**
     * 查询地区销售分布
     */
    List<Map<String, Object>> selectRegionDistribution(@Param("startDate") LocalDate startDate, 
                                                      @Param("endDate") LocalDate endDate);
    
    /**
     * 查询渠道销售分布
     */
    List<Map<String, Object>> selectChannelDistribution(@Param("startDate") LocalDate startDate, 
                                                       @Param("endDate") LocalDate endDate);
    
    /**
     * 查询订单状态统计
     */
    List<Map<String, Object>> selectOrderStatusCounts(@Param("startDate") LocalDate startDate, 
                                                     @Param("endDate") LocalDate endDate);
    
    /**
     * 查询月度销售数据
     */
    List<Map<String, Object>> selectMonthlySalesData(@Param("year") Integer year);
    
    /**
     * 查询销售漏斗数据
     */
    List<Map<String, Object>> selectSalesFunnelData(@Param("startDate") LocalDate startDate, 
                                                   @Param("endDate") LocalDate endDate);
    
    /**
     * 查询客户销售汇总
     */
    List<Map<String, Object>> selectCustomerSalesSummary(@Param("startDate") LocalDate startDate, 
                                                        @Param("endDate") LocalDate endDate);
    
    /**
     * 查询销售记录数量（按状态分组）
     */
    List<Map<String, Object>> selectCountByStatus();
    
    /**
     * 查询销售记录数量（按付款状态分组）
     */
    List<Map<String, Object>> selectCountByPaymentStatus();
    
    /**
     * 查询今日销售数据
     */
    Map<String, Object> selectTodaySalesData();
    
    /**
     * 查询本月销售数据
     */
    Map<String, Object> selectCurrentMonthSalesData();
    
    /**
     * 查询本年销售数据
     */
    Map<String, Object> selectCurrentYearSalesData();
    
    /**
     * 更新销售记录状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    
    /**
     * 更新付款状态
     */
    int updatePaymentStatus(@Param("id") Long id, @Param("paymentStatus") String paymentStatus);
    
    /**
     * 批量更新销售记录状态
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") String status);
    
    /**
     * 检查订单号是否存在
     */
    boolean existsByOrderNumber(@Param("orderNumber") String orderNumber);
    
    /**
     * 检查订单号是否存在（排除指定ID）
     */
    boolean existsByOrderNumberExcludeId(@Param("orderNumber") String orderNumber, @Param("id") Long id);
    
    // ==================== 高级搜索和筛选功能 ====================
    
    /**
     * 获取搜索建议
     */
    List<Map<String, Object>> getSearchSuggestions(@Param("keyword") String keyword);
    
    /**
     * 获取地区筛选选项
     */
    List<Map<String, Object>> getRegionOptions();
    
    /**
     * 获取渠道筛选选项
     */
    List<Map<String, Object>> getChannelOptions();
    
    /**
     * 获取付款方式筛选选项
     */
    List<Map<String, Object>> getPaymentMethodOptions();
    
    /**
     * 获取销售金额范围统计
     */
    List<Map<String, Object>> getSalesAmountRangeStats();
    
    /**
     * 快速筛选 - 今日订单
     */
    List<SalesRecord> selectTodayOrders();
    
    /**
     * 快速筛选 - 本周订单
     */
    List<SalesRecord> selectThisWeekOrders();
    
    /**
     * 快速筛选 - 本月订单
     */
    List<SalesRecord> selectThisMonthOrders();
    
    /**
     * 快速筛选 - 待处理订单
     */
    List<SalesRecord> selectPendingOrders();
    
    /**
     * 快速筛选 - 高价值订单
     */
    List<SalesRecord> selectHighValueOrders(@Param("minAmount") BigDecimal minAmount);
    
    /**
     * 优化的列表查询（带关联信息）
     */
    List<Map<String, Object>> selectListWithDetailsOptimized(@Param("query") SalesRecordQueryDTO query);
    
    /**
     * 优化的计数查询
     */
    Long selectCountOptimized(@Param("query") SalesRecordQueryDTO query);
}