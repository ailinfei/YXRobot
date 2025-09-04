package com.yxrobot.mapper;

import com.yxrobot.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 订单商品明细Mapper接口
 * 对应数据库表：order_items
 * 
 * 字段映射规范：
 * - 数据库字段：snake_case（如：order_id, product_id, unit_price）
 * - Java实体类：camelCase（如：orderId, productId, unitPrice）
 * - MyBatis映射：确保column和property正确对应
 */
@Mapper
public interface OrderItemMapper {
    
    // ==================== 基础CRUD操作 ====================
    
    /**
     * 根据ID查询订单商品明细
     */
    OrderItem selectById(@Param("id") Long id);
    
    /**
     * 根据ID查询订单商品明细（包含产品信息）
     */
    OrderItem selectByIdWithProduct(@Param("id") Long id);
    
    /**
     * 插入订单商品明细
     */
    int insert(OrderItem orderItem);
    
    /**
     * 批量插入订单商品明细
     */
    int batchInsert(@Param("orderItems") List<OrderItem> orderItems);
    
    /**
     * 更新订单商品明细
     */
    int updateById(OrderItem orderItem);
    
    /**
     * 根据ID删除订单商品明细
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据订单ID删除所有商品明细
     */
    int deleteByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 批量删除订单商品明细
     */
    int batchDelete(@Param("ids") List<Long> ids);
    
    // ==================== 查询功能 ====================
    
    /**
     * 根据订单ID查询商品明细列表
     */
    List<OrderItem> selectByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 根据订单ID查询商品明细列表（包含产品信息）
     */
    List<OrderItem> selectByOrderIdWithProduct(@Param("orderId") Long orderId);
    
    /**
     * 根据产品ID查询相关订单商品明细
     */
    List<OrderItem> selectByProductId(@Param("productId") Long productId);
    
    /**
     * 根据订单ID列表查询商品明细
     */
    List<OrderItem> selectByOrderIds(@Param("orderIds") List<Long> orderIds);
    
    /**
     * 根据产品ID列表查询商品明细
     */
    List<OrderItem> selectByProductIds(@Param("productIds") List<Long> productIds);
    
    // ==================== 统计查询功能 ====================
    
    /**
     * 查询订单的商品总数量
     */
    Integer selectTotalQuantityByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 查询订单的商品总金额
     */
    BigDecimal selectTotalAmountByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 查询产品的销售统计
     */
    Map<String, Object> selectProductSalesStats(@Param("productId") Long productId);
    
    /**
     * 查询产品销售排行
     */
    List<Map<String, Object>> selectProductSalesRanking(@Param("limit") Integer limit);
    
    /**
     * 查询产品销售趋势
     */
    List<Map<String, Object>> selectProductSalesTrend(@Param("productId") Long productId, 
                                                     @Param("months") Integer months);
    
    /**
     * 查询热销产品
     */
    List<Map<String, Object>> selectHotProducts(@Param("limit") Integer limit);
    
    /**
     * 查询滞销产品
     */
    List<Map<String, Object>> selectSlowMovingProducts(@Param("days") Integer days, 
                                                      @Param("limit") Integer limit);
    
    // ==================== 业务查询功能 ====================
    
    /**
     * 查询客户购买的产品列表
     */
    List<OrderItem> selectProductsByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 查询客户重复购买的产品
     */
    List<Map<String, Object>> selectRepeatPurchasesByCustomer(@Param("customerId") Long customerId);
    
    /**
     * 查询产品的关联销售（经常一起购买的产品）
     */
    List<Map<String, Object>> selectRelatedProducts(@Param("productId") Long productId, 
                                                   @Param("limit") Integer limit);
    
    /**
     * 查询客户的购买偏好分析
     */
    List<Map<String, Object>> selectCustomerPurchasePreference(@Param("customerId") Long customerId);
    
    /**
     * 查询产品的平均销售价格
     */
    BigDecimal selectAverageSellingPrice(@Param("productId") Long productId);
    
    /**
     * 查询产品的价格变化趋势
     */
    List<Map<String, Object>> selectPriceTrend(@Param("productId") Long productId, 
                                              @Param("months") Integer months);
    
    // ==================== 数据验证功能 ====================
    
    /**
     * 检查订单是否包含指定产品
     */
    boolean existsByOrderIdAndProductId(@Param("orderId") Long orderId, 
                                       @Param("productId") Long productId);
    
    /**
     * 查询订单的商品种类数量
     */
    int countProductTypesByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 查询产品的订单数量
     */
    int countOrdersByProductId(@Param("productId") Long productId);
    
    /**
     * 查询订单商品明细总数
     */
    int countByOrderId(@Param("orderId") Long orderId);
    
    // ==================== 高级查询功能 ====================
    
    /**
     * 查询订单商品明细（分页）
     */
    List<OrderItem> selectListWithPagination(@Param("orderId") Long orderId,
                                            @Param("offset") Integer offset,
                                            @Param("limit") Integer limit);
    
    /**
     * 搜索订单商品明细
     */
    List<OrderItem> searchOrderItems(@Param("keyword") String keyword,
                                   @Param("orderId") Long orderId);
    
    /**
     * 查询大额商品明细
     */
    List<OrderItem> selectHighValueItems(@Param("minAmount") BigDecimal minAmount);
    
    /**
     * 查询批量购买的商品明细
     */
    List<OrderItem> selectBulkPurchaseItems(@Param("minQuantity") Integer minQuantity);
    
    /**
     * 查询商品明细汇总信息
     */
    Map<String, Object> selectOrderItemsSummary(@Param("orderId") Long orderId);
    
    /**
     * 查询产品在不同订单中的价格对比
     */
    List<Map<String, Object>> selectProductPriceComparison(@Param("productId") Long productId,
                                                          @Param("limit") Integer limit);
}