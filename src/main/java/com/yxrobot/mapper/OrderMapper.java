package com.yxrobot.mapper;

import com.yxrobot.dto.OrderQueryDTO;
import com.yxrobot.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 订单Mapper接口
 * 对应数据库表：orders
 * 
 * 字段映射规范：
 * - 数据库字段：snake_case（如：order_number, total_amount, customer_id）
 * - Java实体类：camelCase（如：orderNumber, totalAmount, customerId）
 * - MyBatis映射：确保column和property正确对应
 */
@Mapper
public interface OrderMapper {
    
    // ==================== 基础CRUD操作 ====================
    
    /**
     * 根据ID查询订单
     */
    Order selectById(@Param("id") Long id);
    
    /**
     * 根据ID查询订单详情（包含关联数据）
     */
    Order selectByIdWithDetails(@Param("id") Long id);
    
    /**
     * 根据订单号查询订单
     */
    Order selectByOrderNumber(@Param("orderNumber") String orderNumber);
    
    /**
     * 插入订单
     */
    int insert(Order order);
    
    /**
     * 更新订单
     */
    int updateById(Order order);
    
    /**
     * 根据ID删除订单（软删除）
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 批量删除订单（软删除）
     */
    int batchDelete(@Param("ids") List<Long> ids);
    
    // ==================== 查询功能 ====================
    
    /**
     * 分页查询订单列表
     */
    List<Order> selectList(@Param("query") OrderQueryDTO query);
    
    /**
     * 查询订单总数
     */
    Long selectCount(@Param("query") OrderQueryDTO query);
    
    /**
     * 分页查询订单列表（包含关联数据）
     */
    List<Order> selectListWithDetails(@Param("query") OrderQueryDTO query);
    
    /**
     * 查询订单总数（包含关联数据）
     */
    Long selectCountWithDetails(@Param("query") OrderQueryDTO query);
    
    /**
     * 根据客户ID查询订单列表
     */
    List<Order> selectByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 根据状态查询订单列表
     */
    List<Order> selectByStatus(@Param("status") String status);
    
    /**
     * 根据类型查询订单列表
     */
    List<Order> selectByType(@Param("type") String type);
    
    /**
     * 根据支付状态查询订单列表
     */
    List<Order> selectByPaymentStatus(@Param("paymentStatus") String paymentStatus);
    
    /**
     * 根据销售人员查询订单列表
     */
    List<Order> selectBySalesPerson(@Param("salesPerson") String salesPerson);
    
    /**
     * 根据日期范围查询订单列表
     */
    List<Order> selectByDateRange(@Param("startDate") LocalDate startDate, 
                                 @Param("endDate") LocalDate endDate);
    
    // ==================== 状态管理功能 ====================
    
    /**
     * 更新订单状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    
    /**
     * 批量更新订单状态
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") String status);
    
    /**
     * 更新支付状态
     */
    int updatePaymentStatus(@Param("id") Long id, @Param("paymentStatus") String paymentStatus);
    
    /**
     * 更新支付信息
     */
    int updatePaymentInfo(@Param("id") Long id, 
                         @Param("paymentStatus") String paymentStatus,
                         @Param("paymentMethod") String paymentMethod,
                         @Param("paymentTime") java.time.LocalDateTime paymentTime);
    
    // ==================== 统计查询功能 ====================
    
    /**
     * 查询订单统计数据
     */
    Map<String, Object> selectOrderStats();
    
    /**
     * 根据日期范围查询订单统计数据
     */
    Map<String, Object> selectOrderStatsByDateRange(@Param("startDate") LocalDate startDate, 
                                                   @Param("endDate") LocalDate endDate);
    
    /**
     * 查询按状态分组的订单统计
     */
    List<Map<String, Object>> selectOrderStatsByStatus();
    
    /**
     * 查询按类型分组的订单统计
     */
    List<Map<String, Object>> selectOrderStatsByType();
    
    /**
     * 查询按支付状态分组的订单统计
     */
    List<Map<String, Object>> selectOrderStatsByPaymentStatus();
    
    /**
     * 查询按销售人员分组的订单统计
     */
    List<Map<String, Object>> selectOrderStatsBySalesPerson();
    
    /**
     * 查询月度订单统计
     */
    List<Map<String, Object>> selectMonthlyOrderStats(@Param("months") Integer months);
    
    /**
     * 查询日度订单统计
     */
    List<Map<String, Object>> selectDailyOrderStats(@Param("days") Integer days);
    
    // ==================== 业务查询功能 ====================
    
    /**
     * 查询待处理订单
     */
    List<Order> selectPendingOrders();
    
    /**
     * 查询超期未处理订单
     */
    List<Order> selectOverdueOrders(@Param("days") Integer days);
    
    /**
     * 查询高价值订单
     */
    List<Order> selectHighValueOrders(@Param("minAmount") BigDecimal minAmount);
    
    /**
     * 查询需要跟进的订单
     */
    List<Order> selectOrdersNeedingFollowUp();
    
    /**
     * 查询即将到期的租赁订单
     */
    List<Order> selectExpiringRentalOrders(@Param("days") Integer days);
    
    /**
     * 查询客户的重复订单
     */
    List<Order> selectRepeatOrdersByCustomer(@Param("customerId") Long customerId);
    
    // ==================== 数据验证功能 ====================
    
    /**
     * 检查订单号是否存在
     */
    boolean existsByOrderNumber(@Param("orderNumber") String orderNumber);
    
    /**
     * 检查订单是否属于指定客户
     */
    boolean existsByIdAndCustomerId(@Param("id") Long id, @Param("customerId") Long customerId);
    
    /**
     * 查询客户的订单数量
     */
    int countByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 查询指定状态的订单数量
     */
    int countByStatus(@Param("status") String status);
    
    /**
     * 查询指定类型的订单数量
     */
    int countByType(@Param("type") String type);
    
    // ==================== 高级查询功能 ====================
    
    /**
     * 搜索订单（关键词搜索）
     */
    List<Order> searchOrders(@Param("keyword") String keyword, 
                           @Param("limit") Integer limit);
    
    /**
     * 查询相似订单
     */
    List<Order> selectSimilarOrders(@Param("customerId") Long customerId, 
                                  @Param("productIds") List<Long> productIds,
                                  @Param("limit") Integer limit);
    
    /**
     * 查询订单趋势数据
     */
    List<Map<String, Object>> selectOrderTrend(@Param("months") Integer months);
    
    /**
     * 查询客户订单价值分析
     */
    Map<String, Object> selectCustomerOrderValueAnalysis(@Param("customerId") Long customerId);
    
    /**
     * 查询产品销售排行
     */
    List<Map<String, Object>> selectProductSalesRanking(@Param("limit") Integer limit);
    
    /**
     * 查询销售人员业绩排行
     */
    List<Map<String, Object>> selectSalesPersonPerformance(@Param("limit") Integer limit);
    
    // ==================== OrderService专用查询方法 ====================
    
    /**
     * 分页查询订单列表，支持搜索和筛选
     */
    List<Order> selectOrdersWithPagination(@Param("keyword") String keyword,
                                         @Param("type") String type,
                                         @Param("status") String status,
                                         @Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate,
                                         @Param("offset") int offset,
                                         @Param("pageSize") int pageSize);
    
    /**
     * 查询订单总数，支持搜索和筛选
     */
    int countOrders(@Param("keyword") String keyword,
                   @Param("type") String type,
                   @Param("status") String status,
                   @Param("startDate") LocalDate startDate,
                   @Param("endDate") LocalDate endDate);
    
    /**
     * 根据查询条件查询订单列表
     */
    List<Order> selectByQuery(@Param("query") OrderQueryDTO query);
    
    /**
     * 根据查询条件统计订单数量
     */
    Long countByQuery(@Param("query") OrderQueryDTO query);
}