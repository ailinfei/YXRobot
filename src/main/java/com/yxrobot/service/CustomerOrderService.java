package com.yxrobot.service;

import com.yxrobot.dto.CustomerOrderDTO;
import com.yxrobot.entity.CustomerOrder;
import com.yxrobot.mapper.CustomerMapper;
import com.yxrobot.mapper.CustomerOrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 客户订单服务类
 * 处理客户订单相关的业务逻辑，支持前端详情功能
 * 
 * 主要功能：
 * 1. 获取客户订单列表 - 支持前端详情页显示
 * 2. 订单统计计算 - 订单数量、金额统计等
 * 3. 订单分类管理 - 购买订单、租赁订单管理
 * 4. 订单状态统计 - 待处理、已完成等状态统计
 * 5. 关联数据格式适配前端详情页面显示
 */
@Service
@Transactional(readOnly = true)
public class CustomerOrderService {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerOrderService.class);
    
    @Autowired
    private CustomerMapper customerMapper;
    
    @Autowired
    private CustomerOrderMapper customerOrderMapper;
    
    /**
     * 订单信息内部类 - 用于返回给前端
     */
    public static class OrderInfo {
        private Long orderId;
        private String orderNumber;
        private String orderType;
        private String orderStatus;
        private BigDecimal totalAmount;
        private BigDecimal paidAmount;
        private BigDecimal remainingAmount;
        private String orderDate;
        private String deliveryDate;
        private String completedDate;
        private String paymentMethod;
        private String paymentStatus;
        private String deliveryAddress;
        private String notes;
        private Integer itemCount;
        private String customerName;
        private String contactPhone;
        
        // 构造函数
        public OrderInfo() {}
        
        // Getter和Setter方法
        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }
        public String getOrderNumber() { return orderNumber; }
        public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
        public String getOrderType() { return orderType; }
        public void setOrderType(String orderType) { this.orderType = orderType; }
        public String getOrderStatus() { return orderStatus; }
        public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
        public BigDecimal getPaidAmount() { return paidAmount; }
        public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }
        public BigDecimal getRemainingAmount() { return remainingAmount; }
        public void setRemainingAmount(BigDecimal remainingAmount) { this.remainingAmount = remainingAmount; }
        public String getOrderDate() { return orderDate; }
        public void setOrderDate(String orderDate) { this.orderDate = orderDate; }
        public String getDeliveryDate() { return deliveryDate; }
        public void setDeliveryDate(String deliveryDate) { this.deliveryDate = deliveryDate; }
        public String getCompletedDate() { return completedDate; }
        public void setCompletedDate(String completedDate) { this.completedDate = completedDate; }
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
        public String getPaymentStatus() { return paymentStatus; }
        public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
        public String getDeliveryAddress() { return deliveryAddress; }
        public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
        public Integer getItemCount() { return itemCount; }
        public void setItemCount(Integer itemCount) { this.itemCount = itemCount; }
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        public String getContactPhone() { return contactPhone; }
        public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    }
    
    /**
     * 获取客户的订单列表（支持前端详情页显示）
     * 返回格式与前端CustomerOrder接口完全匹配
     * @param customerId 客户ID
     * @return 客户订单列表
     */
    @Cacheable(value = "customerOrders", key = "#customerId", unless = "#result.isEmpty()")
    public List<CustomerOrderDTO> getCustomerOrders(Long customerId) {
        try {
            if (customerId == null || customerId <= 0) {
                logger.warn("客户ID无效: {}", customerId);
                return List.of();
            }
            
            logger.debug("获取客户订单列表: customerId={}", customerId);
            
            List<CustomerOrderDTO> orders = customerOrderMapper.selectCustomerOrdersByCustomerId(customerId);
            
            if (orders == null) {
                orders = List.of();
            }
            
            logger.debug("客户订单列表获取完成: customerId={}, 订单数量={}", customerId, orders.size());
            
            return orders;
            
        } catch (Exception e) {
            logger.error("获取客户订单列表失败: customerId={}", customerId, e);
            return List.of();
        }
    }
    
    /**
     * 获取客户的订单统计信息（支持前端统计显示）
     * @param customerId 客户ID
     * @return 订单统计信息
     */
    @Cacheable(value = "customerOrderStats", key = "#customerId")
    public Map<String, Object> getCustomerOrderStats(Long customerId) {
        try {
            if (customerId == null || customerId <= 0) {
                logger.warn("客户ID无效: {}", customerId);
                return createEmptyOrderStats();
            }
            
            Map<String, Object> stats = customerOrderMapper.selectCustomerOrderStats(customerId);
            
            if (stats == null || stats.isEmpty()) {
                stats = createEmptyOrderStats();
            }
            
            return stats;
            
        } catch (Exception e) {
            logger.error("获取客户订单统计失败: customerId={}", customerId, e);
            return createEmptyOrderStats();
        }
    }
    
    /**
     * 创建空的订单统计数据
     * @return 空统计数据
     */
    private Map<String, Object> createEmptyOrderStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOrders", 0);
        stats.put("purchaseOrders", 0);
        stats.put("rentalOrders", 0);
        stats.put("completedOrders", 0);
        stats.put("pendingOrders", 0);
        stats.put("processingOrders", 0);
        stats.put("cancelledOrders", 0);
        stats.put("totalAmount", BigDecimal.ZERO);
        stats.put("avgOrderValue", BigDecimal.ZERO);
        stats.put("maxOrderValue", BigDecimal.ZERO);
        stats.put("minOrderValue", BigDecimal.ZERO);
        return stats;
    }
    
    /**
     * 获取客户的购买订单列表（支持前端分类显示）
     * @param customerId 客户ID
     * @return 购买订单列表
     */
    @Cacheable(value = "customerPurchaseOrders", key = "#customerId")
    public List<CustomerOrderDTO> getCustomerPurchaseOrders(Long customerId) {
        try {
            if (customerId == null || customerId <= 0) {
                return List.of();
            }
            
            logger.debug("获取客户购买订单列表: customerId={}", customerId);
            
            List<CustomerOrderDTO> orders = customerOrderMapper.selectPurchaseOrdersByCustomerId(customerId);
            
            return orders != null ? orders : List.of();
            
        } catch (Exception e) {
            logger.error("获取客户购买订单列表失败: customerId={}", customerId, e);
            return List.of();
        }
    }
    
    /**
     * 获取客户的租赁订单列表（支持前端分类显示）
     * @param customerId 客户ID
     * @return 租赁订单列表
     */
    @Cacheable(value = "customerRentalOrders", key = "#customerId")
    public List<CustomerOrderDTO> getCustomerRentalOrders(Long customerId) {
        try {
            if (customerId == null || customerId <= 0) {
                return List.of();
            }
            
            logger.debug("获取客户租赁订单列表: customerId={}", customerId);
            
            List<CustomerOrderDTO> orders = customerOrderMapper.selectRentalOrdersByCustomerId(customerId);
            
            return orders != null ? orders : List.of();
            
        } catch (Exception e) {
            logger.error("获取客户租赁订单列表失败: customerId={}", customerId, e);
            return List.of();
        }
    }
    
    /**
     * 获取客户的已完成订单列表（支持前端状态筛选）
     * @param customerId 客户ID
     * @return 已完成订单列表
     */
    @Cacheable(value = "customerCompletedOrders", key = "#customerId")
    public List<CustomerOrderDTO> getCustomerCompletedOrders(Long customerId) {
        try {
            if (customerId == null || customerId <= 0) {
                return List.of();
            }
            
            logger.debug("获取客户已完成订单列表: customerId={}", customerId);
            
            List<CustomerOrderDTO> orders = customerOrderMapper.selectCompletedOrdersByCustomerId(customerId);
            
            return orders != null ? orders : List.of();
            
        } catch (Exception e) {
            logger.error("获取客户已完成订单列表失败: customerId={}", customerId, e);
            return List.of();
        }
    }
    
    /**
     * 获取客户的进行中订单列表（支持前端状态筛选）
     * @param customerId 客户ID
     * @return 进行中订单列表
     */
    @Cacheable(value = "customerPendingOrders", key = "#customerId")
    public List<CustomerOrderDTO> getCustomerPendingOrders(Long customerId) {
        try {
            if (customerId == null || customerId <= 0) {
                return List.of();
            }
            
            logger.debug("获取客户进行中订单列表: customerId={}", customerId);
            
            List<CustomerOrderDTO> orders = customerOrderMapper.selectPendingOrdersByCustomerId(customerId);
            
            return orders != null ? orders : List.of();
            
        } catch (Exception e) {
            logger.error("获取客户进行中订单列表失败: customerId={}", customerId, e);
            return List.of();
        }
    }
    
    /**
     * 获取客户的最近订单（支持前端最近记录显示）
     * @param customerId 客户ID
     * @param limit 限制数量
     * @return 最近订单列表
     */
    @Cacheable(value = "customerRecentOrders", key = "#customerId + '_' + #limit")
    public List<CustomerOrderDTO> getCustomerRecentOrders(Long customerId, Integer limit) {
        try {
            if (customerId == null || customerId <= 0) {
                return List.of();
            }
            
            if (limit == null || limit <= 0) {
                limit = 10; // 默认10条
            }
            
            logger.debug("获取客户最近订单: customerId={}, limit={}", customerId, limit);
            
            List<CustomerOrderDTO> orders = customerOrderMapper.selectRecentOrdersByCustomerId(customerId, limit);
            
            return orders != null ? orders : List.of();
            
        } catch (Exception e) {
            logger.error("获取客户最近订单失败: customerId={}, limit={}", customerId, limit, e);
            return List.of();
        }
    }
    
    /**
     * 计算客户订单总金额（支持前端统计显示）
     * @param customerId 客户ID
     * @return 订单总金额
     */
    public BigDecimal calculateCustomerOrderTotal(Long customerId) {
        try {
            Map<String, Object> stats = getCustomerOrderStats(customerId);
            Object totalAmount = stats.get("totalAmount");
            
            if (totalAmount instanceof BigDecimal) {
                return (BigDecimal) totalAmount;
            }
            
            return BigDecimal.ZERO;
            
        } catch (Exception e) {
            logger.error("计算客户订单总金额失败: customerId={}", customerId, e);
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * 获取客户订单状态分布（支持前端图表显示）
     * @param customerId 客户ID
     * @return 订单状态分布
     */
    @Cacheable(value = "customerOrderStatusDistribution", key = "#customerId")
    public List<Map<String, Object>> getCustomerOrderStatusDistribution(Long customerId) {
        try {
            if (customerId == null || customerId <= 0) {
                return List.of();
            }
            
            return customerOrderMapper.selectOrderStatusDistribution(customerId);
            
        } catch (Exception e) {
            logger.error("获取订单状态分布失败: customerId={}", customerId, e);
            return List.of();
        }
    }
    
    /**
     * 获取客户订单类型分布（支持前端图表显示）
     * @param customerId 客户ID
     * @return 订单类型分布
     */
    @Cacheable(value = "customerOrderTypeDistribution", key = "#customerId")
    public List<Map<String, Object>> getCustomerOrderTypeDistribution(Long customerId) {
        try {
            if (customerId == null || customerId <= 0) {
                return List.of();
            }
            
            return customerOrderMapper.selectOrderTypeDistribution(customerId);
            
        } catch (Exception e) {
            logger.error("获取订单类型分布失败: customerId={}", customerId, e);
            return List.of();
        }
    }
    
    /**
     * 获取客户的大额订单（支持前端高价值订单显示）
     * @param customerId 客户ID
     * @param minAmount 最小金额
     * @return 大额订单列表
     */
    public List<CustomerOrderDTO> getCustomerHighValueOrders(Long customerId, BigDecimal minAmount) {
        try {
            if (customerId == null || customerId <= 0) {
                return List.of();
            }
            
            if (minAmount == null || minAmount.compareTo(BigDecimal.ZERO) <= 0) {
                minAmount = new BigDecimal("10000"); // 默认1万元以上
            }
            
            logger.debug("获取客户大额订单: customerId={}, minAmount={}", customerId, minAmount);
            
            List<CustomerOrderDTO> orders = customerOrderMapper.selectHighValueOrders(customerId, minAmount);
            
            return orders != null ? orders : List.of();
            
        } catch (Exception e) {
            logger.error("获取客户大额订单失败: customerId={}, minAmount={}", customerId, minAmount, e);
            return List.of();
        }
    }
    
    /**
     * 获取指定日期范围内的客户订单（支持前端日期筛选）
     * @param customerId 客户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 订单列表
     */
    public List<CustomerOrderDTO> getCustomerOrdersByDateRange(Long customerId, LocalDate startDate, LocalDate endDate) {
        try {
            if (customerId == null || customerId <= 0) {
                return List.of();
            }
            
            if (startDate == null || endDate == null) {
                logger.warn("日期范围无效: startDate={}, endDate={}", startDate, endDate);
                return List.of();
            }
            
            logger.debug("获取客户日期范围订单: customerId={}, startDate={}, endDate={}", customerId, startDate, endDate);
            
            List<CustomerOrderDTO> orders = customerOrderMapper.selectOrdersByDateRange(customerId, startDate, endDate);
            
            return orders != null ? orders : List.of();
            
        } catch (Exception e) {
            logger.error("获取客户日期范围订单失败: customerId={}, startDate={}, endDate={}", customerId, startDate, endDate, e);
            return List.of();
        }
    }
    
    /**
     * 获取客户的平均订单价值（支持前端统计显示）
     * @param customerId 客户ID
     * @return 平均订单价值
     */
    public BigDecimal getCustomerAverageOrderValue(Long customerId) {
        try {
            if (customerId == null || customerId <= 0) {
                return BigDecimal.ZERO;
            }
            
            BigDecimal avgValue = customerOrderMapper.selectAverageOrderValue(customerId);
            
            return avgValue != null ? avgValue : BigDecimal.ZERO;
            
        } catch (Exception e) {
            logger.error("获取客户平均订单价值失败: customerId={}", customerId, e);
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * 获取客户月度订单统计（支持前端趋势图表）
     * @param customerId 客户ID
     * @param months 月数
     * @return 月度订单统计
     */
    @Cacheable(value = "customerMonthlyOrderStats", key = "#customerId + '_' + #months")
    public List<Map<String, Object>> getCustomerMonthlyOrderStats(Long customerId, Integer months) {
        try {
            if (customerId == null || customerId <= 0) {
                return List.of();
            }
            
            if (months == null || months <= 0) {
                months = 12; // 默认12个月
            }
            
            return customerOrderMapper.selectMonthlyOrderStats(customerId, months);
            
        } catch (Exception e) {
            logger.error("获取客户月度订单统计失败: customerId={}, months={}", customerId, months, e);
            return List.of();
        }
    }
}