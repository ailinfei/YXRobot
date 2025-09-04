package com.yxrobot.mapper;

import com.yxrobot.entity.ShippingInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 物流信息Mapper接口
 * 对应数据库表：shipping_info
 * 
 * 字段映射规范：
 * - 数据库字段：snake_case（如：order_id, tracking_number, shipped_at）
 * - Java实体类：camelCase（如：orderId, trackingNumber, shippedAt）
 * - MyBatis映射：确保column和property正确对应
 */
@Mapper
public interface ShippingInfoMapper {
    
    // ==================== 基础CRUD操作 ====================
    
    /**
     * 根据ID查询物流信息
     */
    ShippingInfo selectById(@Param("id") Long id);
    
    /**
     * 根据订单ID查询物流信息
     */
    ShippingInfo selectByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 根据运单号查询物流信息
     */
    ShippingInfo selectByTrackingNumber(@Param("trackingNumber") String trackingNumber);
    
    /**
     * 插入物流信息
     */
    int insert(ShippingInfo shippingInfo);
    
    /**
     * 更新物流信息
     */
    int updateById(ShippingInfo shippingInfo);
    
    /**
     * 根据ID删除物流信息
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据订单ID删除物流信息
     */
    int deleteByOrderId(@Param("orderId") Long orderId);
    
    // ==================== 查询功能 ====================
    
    /**
     * 根据物流公司查询物流信息列表
     */
    List<ShippingInfo> selectByCompany(@Param("company") String company);
    
    /**
     * 根据订单ID列表查询物流信息
     */
    List<ShippingInfo> selectByOrderIds(@Param("orderIds") List<Long> orderIds);
    
    /**
     * 查询已发货但未送达的物流信息
     */
    List<ShippingInfo> selectShippedButNotDelivered();
    
    /**
     * 查询指定日期范围内发货的物流信息
     */
    List<ShippingInfo> selectByShippedDateRange(@Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);
    
    /**
     * 查询指定日期范围内送达的物流信息
     */
    List<ShippingInfo> selectByDeliveredDateRange(@Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate);
    
    // ==================== 状态更新功能 ====================
    
    /**
     * 更新发货信息
     */
    int updateShippingInfo(@Param("orderId") Long orderId,
                          @Param("company") String company,
                          @Param("trackingNumber") String trackingNumber,
                          @Param("shippedAt") LocalDateTime shippedAt);
    
    /**
     * 更新送达信息
     */
    int updateDeliveryInfo(@Param("orderId") Long orderId,
                          @Param("deliveredAt") LocalDateTime deliveredAt);
    
    /**
     * 批量更新发货状态
     */
    int batchUpdateShippingStatus(@Param("orderIds") List<Long> orderIds,
                                 @Param("company") String company,
                                 @Param("shippedAt") LocalDateTime shippedAt);
    
    // ==================== 统计查询功能 ====================
    
    /**
     * 查询物流统计信息
     */
    Map<String, Object> selectShippingStats();
    
    /**
     * 查询按物流公司分组的统计信息
     */
    List<Map<String, Object>> selectStatsByCompany();
    
    /**
     * 查询物流时效统计
     */
    Map<String, Object> selectDeliveryTimeStats();
    
    /**
     * 查询月度物流统计
     */
    List<Map<String, Object>> selectMonthlyShippingStats(@Param("months") Integer months);
    
    /**
     * 查询物流公司性能对比
     */
    List<Map<String, Object>> selectCompanyPerformanceComparison();
    
    // ==================== 业务查询功能 ====================
    
    /**
     * 查询超期未送达的物流信息
     */
    List<ShippingInfo> selectOverdueDeliveries(@Param("days") Integer days);
    
    /**
     * 查询需要跟进的物流信息
     */
    List<ShippingInfo> selectShippingNeedingFollowUp();
    
    /**
     * 查询物流异常信息
     */
    List<ShippingInfo> selectAbnormalShipping(@Param("days") Integer days);
    
    /**
     * 查询快速配送的物流信息
     */
    List<ShippingInfo> selectFastDeliveries(@Param("hours") Integer hours);
    
    /**
     * 查询慢速配送的物流信息
     */
    List<ShippingInfo> selectSlowDeliveries(@Param("days") Integer days);
    
    // ==================== 数据验证功能 ====================
    
    /**
     * 检查运单号是否存在
     */
    boolean existsByTrackingNumber(@Param("trackingNumber") String trackingNumber);
    
    /**
     * 检查订单是否已有物流信息
     */
    boolean existsByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 查询物流公司的订单数量
     */
    int countByCompany(@Param("company") String company);
    
    /**
     * 查询已发货订单数量
     */
    int countShippedOrders();
    
    /**
     * 查询已送达订单数量
     */
    int countDeliveredOrders();
    
    // ==================== 高级查询功能 ====================
    
    /**
     * 搜索物流信息
     */
    List<ShippingInfo> searchShippingInfo(@Param("keyword") String keyword);
    
    /**
     * 查询物流轨迹（如果有扩展表）
     */
    List<Map<String, Object>> selectShippingTrack(@Param("trackingNumber") String trackingNumber);
    
    /**
     * 查询配送区域统计
     */
    List<Map<String, Object>> selectDeliveryAreaStats();
    
    /**
     * 查询物流成本分析
     */
    Map<String, Object> selectShippingCostAnalysis(@Param("company") String company);
    
    /**
     * 查询客户配送偏好
     */
    List<Map<String, Object>> selectCustomerDeliveryPreference(@Param("customerId") Long customerId);
}