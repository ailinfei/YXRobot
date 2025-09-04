package com.yxrobot.mapper;

import com.yxrobot.entity.OrderLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 订单操作日志Mapper接口
 * 对应数据库表：order_logs
 * 
 * 字段映射规范：
 * - 数据库字段：snake_case（如：order_id, created_at）
 * - Java实体类：camelCase（如：orderId, createdAt）
 * - MyBatis映射：确保column和property正确对应
 */
@Mapper
public interface OrderLogMapper {
    
    // ==================== 基础CRUD操作 ====================
    
    /**
     * 根据ID查询订单日志
     */
    OrderLog selectById(@Param("id") Long id);
    
    /**
     * 插入订单日志
     */
    int insert(OrderLog orderLog);
    
    /**
     * 批量插入订单日志
     */
    int batchInsert(@Param("orderLogs") List<OrderLog> orderLogs);
    
    /**
     * 根据ID删除订单日志
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据订单ID删除所有日志
     */
    int deleteByOrderId(@Param("orderId") Long orderId);
    
    // ==================== 查询功能 ====================
    
    /**
     * 根据订单ID查询日志列表
     */
    List<OrderLog> selectByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 根据订单ID查询日志列表（按时间倒序）
     */
    List<OrderLog> selectByOrderIdOrderByTimeDesc(@Param("orderId") Long orderId);
    
    /**
     * 根据操作人查询日志列表
     */
    List<OrderLog> selectByOperator(@Param("operator") String operator);
    
    /**
     * 根据操作动作查询日志列表
     */
    List<OrderLog> selectByAction(@Param("action") String action);
    
    /**
     * 根据订单ID列表查询日志
     */
    List<OrderLog> selectByOrderIds(@Param("orderIds") List<Long> orderIds);
    
    /**
     * 根据时间范围查询日志
     */
    List<OrderLog> selectByTimeRange(@Param("startTime") LocalDateTime startTime,
                                   @Param("endTime") LocalDateTime endTime);
    
    /**
     * 根据订单ID和时间范围查询日志
     */
    List<OrderLog> selectByOrderIdAndTimeRange(@Param("orderId") Long orderId,
                                             @Param("startTime") LocalDateTime startTime,
                                             @Param("endTime") LocalDateTime endTime);
    
    // ==================== 统计查询功能 ====================
    
    /**
     * 查询订单日志统计信息
     */
    Map<String, Object> selectLogStats();
    
    /**
     * 查询按操作动作分组的统计信息
     */
    List<Map<String, Object>> selectStatsByAction();
    
    /**
     * 查询按操作人分组的统计信息
     */
    List<Map<String, Object>> selectStatsByOperator();
    
    /**
     * 查询月度操作统计
     */
    List<Map<String, Object>> selectMonthlyOperationStats(@Param("months") Integer months);
    
    /**
     * 查询日度操作统计
     */
    List<Map<String, Object>> selectDailyOperationStats(@Param("days") Integer days);
    
    /**
     * 查询操作频率统计
     */
    List<Map<String, Object>> selectOperationFrequencyStats();
    
    // ==================== 业务查询功能 ====================
    
    /**
     * 查询订单的最新操作日志
     */
    OrderLog selectLatestByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 查询订单的第一条操作日志
     */
    OrderLog selectFirstByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 查询指定操作的日志
     */
    List<OrderLog> selectByOrderIdAndAction(@Param("orderId") Long orderId,
                                          @Param("action") String action);
    
    /**
     * 查询操作人的最近操作
     */
    List<OrderLog> selectRecentOperationsByOperator(@Param("operator") String operator,
                                                   @Param("limit") Integer limit);
    
    /**
     * 查询异常操作日志
     */
    List<OrderLog> selectAbnormalOperations(@Param("keywords") List<String> keywords);
    
    /**
     * 查询频繁操作的订单
     */
    List<Map<String, Object>> selectFrequentlyOperatedOrders(@Param("minCount") Integer minCount);
    
    // ==================== 审计查询功能 ====================
    
    /**
     * 查询订单状态变更历史
     */
    List<OrderLog> selectStatusChangeHistory(@Param("orderId") Long orderId);
    
    /**
     * 查询订单的完整操作轨迹
     */
    List<OrderLog> selectOrderOperationTrail(@Param("orderId") Long orderId);
    
    /**
     * 查询操作人的操作历史
     */
    List<OrderLog> selectOperatorHistory(@Param("operator") String operator,
                                       @Param("startTime") LocalDateTime startTime,
                                       @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查询可疑操作日志
     */
    List<OrderLog> selectSuspiciousOperations(@Param("timeWindow") Integer timeWindow);
    
    /**
     * 查询批量操作日志
     */
    List<OrderLog> selectBatchOperations(@Param("operator") String operator,
                                       @Param("action") String action,
                                       @Param("timeWindow") Integer timeWindow);
    
    // ==================== 数据验证功能 ====================
    
    /**
     * 查询订单的日志数量
     */
    int countByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 查询操作人的操作次数
     */
    int countByOperator(@Param("operator") String operator);
    
    /**
     * 查询指定动作的操作次数
     */
    int countByAction(@Param("action") String action);
    
    /**
     * 查询时间范围内的日志数量
     */
    int countByTimeRange(@Param("startTime") LocalDateTime startTime,
                        @Param("endTime") LocalDateTime endTime);
    
    // ==================== 高级查询功能 ====================
    
    /**
     * 搜索订单日志
     */
    List<OrderLog> searchOrderLogs(@Param("keyword") String keyword,
                                 @Param("limit") Integer limit);
    
    /**
     * 查询订单处理时长统计
     */
    Map<String, Object> selectOrderProcessingTimeStats(@Param("orderId") Long orderId);
    
    /**
     * 查询操作效率分析
     */
    List<Map<String, Object>> selectOperationEfficiencyAnalysis(@Param("operator") String operator);
    
    /**
     * 查询订单生命周期分析
     */
    Map<String, Object> selectOrderLifecycleAnalysis(@Param("orderId") Long orderId);
    
    /**
     * 查询系统操作负载分析
     */
    List<Map<String, Object>> selectSystemLoadAnalysis(@Param("hours") Integer hours);
}