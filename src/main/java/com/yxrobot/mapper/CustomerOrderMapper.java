package com.yxrobot.mapper;

import com.yxrobot.dto.CustomerOrderDTO;
import com.yxrobot.entity.CustomerOrder;
import com.yxrobot.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 客户订单关联Mapper接口
 * 对应数据库表：customer_order_relation
 * 遵循项目关联表设计规范：不使用外键约束，通过关联表实现表间关系
 * 
 * 字段映射规范：
 * - 数据库字段使用snake_case命名（如：customer_role, relation_notes, created_time）
 * - Java属性使用camelCase命名（如：customerRole, relationNotes, createdTime）
 * - MyBatis映射文件中确保column和property正确对应
 */
@Mapper
public interface CustomerOrderMapper {
    
    // ==================== 基础CRUD操作 ====================
    
    /**
     * 根据ID查询客户订单关联
     */
    CustomerOrder selectById(@Param("id") Long id);
    
    /**
     * 根据ID查询客户订单关联DTO（包含订单详细信息）
     */
    CustomerOrderDTO selectDTOById(@Param("id") Long id);
    
    /**
     * 根据客户ID查询订单关联列表
     */
    List<CustomerOrder> selectByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 根据客户ID查询订单关联DTO列表（适配前端页面需求）
     */
    List<CustomerOrderDTO> selectDTOByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 根据客户ID查询客户订单列表（兼容方法）
     */
    List<CustomerOrderDTO> selectCustomerOrdersByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 根据订单ID查询客户关联列表
     */
    List<CustomerOrder> selectByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 插入客户订单关联
     */
    int insert(CustomerOrder customerOrder);
    
    /**
     * 更新客户订单关联
     */
    int updateById(CustomerOrder customerOrder);
    
    /**
     * 根据ID删除客户订单关联（设置status=0）
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据客户ID和订单ID删除关联
     */
    int deleteByCustomerAndOrder(@Param("customerId") Long customerId, @Param("orderId") Long orderId);
    
    // ==================== 关联查询功能 ====================
    
    /**
     * 查询客户的所有订单（通过关联表）
     */
    List<Order> selectOrdersByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 查询订单的所有客户（通过关联表）
     */
    List<com.yxrobot.entity.Customer> selectCustomersByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 查询客户的购买订单列表
     */
    List<CustomerOrderDTO> selectPurchaseOrdersByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 查询客户的租赁订单列表
     */
    List<CustomerOrderDTO> selectRentalOrdersByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 查询客户的已完成订单列表
     */
    List<CustomerOrderDTO> selectCompletedOrdersByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 查询客户的进行中订单列表
     */
    List<CustomerOrderDTO> selectPendingOrdersByCustomerId(@Param("customerId") Long customerId);
    
    // ==================== 统计查询功能 ====================
    
    /**
     * 查询客户订单统计信息
     */
    Map<String, Object> selectCustomerOrderStats(@Param("customerId") Long customerId);
    
    /**
     * 查询客户订单数量统计
     */
    Map<String, Object> selectCustomerOrderCount(@Param("customerId") Long customerId);
    
    /**
     * 查询客户订单金额统计
     */
    Map<String, Object> selectCustomerOrderAmountStats(@Param("customerId") Long customerId);
    
    /**
     * 查询订单类型分布统计
     */
    List<Map<String, Object>> selectOrderTypeDistribution(@Param("customerId") Long customerId);
    
    /**
     * 查询订单状态分布统计
     */
    List<Map<String, Object>> selectOrderStatusDistribution(@Param("customerId") Long customerId);
    
    /**
     * 查询客户月度订单统计
     */
    List<Map<String, Object>> selectMonthlyOrderStats(@Param("customerId") Long customerId, 
                                                      @Param("months") Integer months);
    
    // ==================== 订单管理功能 ====================
    
    /**
     * 查询客户最近的订单
     */
    List<CustomerOrderDTO> selectRecentOrdersByCustomerId(@Param("customerId") Long customerId, 
                                                          @Param("limit") Integer limit);
    
    /**
     * 查询客户的大额订单（金额超过指定值）
     */
    List<CustomerOrderDTO> selectHighValueOrders(@Param("customerId") Long customerId, 
                                                @Param("minAmount") BigDecimal minAmount);
    
    /**
     * 查询指定日期范围内的客户订单
     */
    List<CustomerOrderDTO> selectOrdersByDateRange(@Param("customerId") Long customerId,
                                                  @Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);
    
    /**
     * 查询需要跟进的订单（待处理或处理中状态）
     */
    List<CustomerOrderDTO> selectOrdersNeedingFollowUp(@Param("customerId") Long customerId);
    
    /**
     * 更新订单关联状态
     */
    int updateRelationStatus(@Param("id") Long id, @Param("status") Integer status);
    
    /**
     * 批量更新订单关联状态
     */
    int batchUpdateRelationStatus(@Param("ids") List<Long> ids, @Param("status") Integer status);
    
    // ==================== 数据验证功能 ====================
    
    /**
     * 检查客户订单关联是否存在
     */
    boolean existsByCustomerAndOrder(@Param("customerId") Long customerId, 
                                    @Param("orderId") Long orderId, 
                                    @Param("customerRole") String customerRole);
    
    /**
     * 检查订单是否已被其他客户关联
     */
    boolean isOrderOccupied(@Param("orderId") Long orderId, 
                           @Param("customerRole") String customerRole);
    
    /**
     * 查询客户订单关联数量
     */
    int countByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 查询订单关联数量
     */
    int countByOrderId(@Param("orderId") Long orderId);
    
    // ==================== 高级查询功能 ====================
    
    /**
     * 查询客户订单历史（包括已删除的关联）
     */
    List<CustomerOrderDTO> selectCustomerOrderHistory(@Param("customerId") Long customerId);
    
    /**
     * 查询订单流转历史
     */
    List<CustomerOrderDTO> selectOrderTransferHistory(@Param("orderId") Long orderId);
    
    /**
     * 查询客户的重复订单（相同产品的多次订单）
     */
    List<CustomerOrderDTO> selectRepeatOrders(@Param("customerId") Long customerId);
    
    /**
     * 查询客户的订单趋势数据
     */
    List<Map<String, Object>> selectCustomerOrderTrend(@Param("customerId") Long customerId, 
                                                       @Param("months") Integer months);
    
    /**
     * 查询客户的平均订单价值
     */
    BigDecimal selectAverageOrderValue(@Param("customerId") Long customerId);
    
    /**
     * 查询客户的订单频率（平均间隔天数）
     */
    Double selectOrderFrequency(@Param("customerId") Long customerId);
}