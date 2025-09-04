package com.yxrobot.service;

import com.yxrobot.dto.OrderStatsDTO;
import com.yxrobot.mapper.OrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Map;

/**
 * 订单统计服务类
 * 专门处理订单统计相关的业务逻辑
 * 支持前端统计卡片显示
 * 
 * 主要功能：
 * 1. 实时统计计算 - 支持前端统计卡片数据需求
 * 2. 订单状态分布统计 - 各状态订单数量统计
 * 3. 订单类型统计 - 销售订单和租赁订单统计
 * 4. 收入统计和平均订单价值计算
 * 5. 按日期范围的动态统计计算
 * 6. 统计数据性能优化
 * 
 * 字段映射规范：
 * - 数据库字段：snake_case（如：total_amount, order_status）
 * - Java服务：camelCase（如：totalAmount, orderStatus）
 * - 前端接口：camelCase（如：totalAmount, orderStatus）
 */
@Service
@Transactional(readOnly = true)
public class OrderStatsService {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderStatsService.class);
    
    @Autowired
    private OrderMapper orderMapper;
    
    /**
     * 获取订单统计数据（支持前端统计卡片）
     * 包含：订单总数、各状态分布、收入统计、订单类型分布等
     * 
     * @return 订单统计数据
     */
    public OrderStatsDTO getOrderStats() {
        try {
            logger.debug("开始获取订单统计数据");
            
            // 获取实时统计数据
            Map<String, Object> statsMap = orderMapper.selectOrderStats();
            
            // 如果没有数据，返回零值统计
            if (statsMap == null || statsMap.isEmpty()) {
                logger.info("数据库中暂无订单数据，返回零值统计");
                return createEmptyStats();
            }
            
            // 转换为DTO对象
            OrderStatsDTO stats = convertMapToDTO(statsMap);
            
            // 验证和修正数据
            validateAndFixStats(stats);
            
            logger.debug("订单统计数据获取完成: 总数={}, 已完成={}, 总收入={}", 
                        stats.getTotal(), stats.getCompleted(), stats.getTotalRevenue());
            
            return stats;
            
        } catch (Exception e) {
            logger.error("获取订单统计数据失败", e);
            // 发生异常时返回零值统计，确保前端正常显示
            return createEmptyStats();
        }
    }
    
    /**
     * 根据日期范围获取订单统计数据
     * 支持前端按时间筛选的统计需求
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 订单统计数据
     */
    public OrderStatsDTO getOrderStatsByDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            logger.debug("开始获取日期范围订单统计数据: {} - {}", startDate, endDate);
            
            // 参数验证
            if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
                logger.warn("开始日期不能晚于结束日期，交换日期参数");
                LocalDate temp = startDate;
                startDate = endDate;
                endDate = temp;
            }
            
            // 获取指定日期范围的统计数据
            Map<String, Object> statsMap = orderMapper.selectOrderStatsByDateRange(startDate, endDate);
            
            // 如果没有数据，返回零值统计
            if (statsMap == null || statsMap.isEmpty()) {
                logger.info("指定日期范围内暂无订单数据，返回零值统计");
                return createEmptyStats();
            }
            
            // 转换为DTO对象
            OrderStatsDTO stats = convertMapToDTO(statsMap);
            
            // 验证和修正数据
            validateAndFixStats(stats);
            
            logger.debug("日期范围订单统计数据获取完成: 总数={}, 已完成={}, 总收入={}", 
                        stats.getTotal(), stats.getCompleted(), stats.getTotalRevenue());
            
            return stats;
            
        } catch (Exception e) {
            logger.error("获取日期范围订单统计数据失败", e);
            return createEmptyStats();
        }
    }
    
    /**
     * 获取订单状态分布统计
     * 支持前端状态分布图表显示
     * 
     * @return 订单状态分布数据
     */
    public Map<String, Integer> getOrderStatusDistribution() {
        try {
            logger.debug("开始获取订单状态分布统计");
            
            OrderStatsDTO stats = getOrderStats();
            
            Map<String, Integer> distribution = new java.util.HashMap<>();
            distribution.put("pending", stats.getPending());
            distribution.put("confirmed", stats.getConfirmed());
            distribution.put("processing", stats.getProcessing());
            distribution.put("shipped", stats.getShipped());
            distribution.put("delivered", stats.getDelivered());
            distribution.put("completed", stats.getCompleted());
            distribution.put("cancelled", stats.getCancelled());
            
            logger.debug("订单状态分布统计获取完成");
            return distribution;
            
        } catch (Exception e) {
            logger.error("获取订单状态分布统计失败", e);
            return new java.util.HashMap<>();
        }
    }
    
    /**
     * 获取订单类型分布统计
     * 支持前端类型分布图表显示
     * 
     * @return 订单类型分布数据
     */
    public Map<String, Integer> getOrderTypeDistribution() {
        try {
            logger.debug("开始获取订单类型分布统计");
            
            OrderStatsDTO stats = getOrderStats();
            
            Map<String, Integer> distribution = new java.util.HashMap<>();
            distribution.put("sales", stats.getSalesOrders());
            distribution.put("rental", stats.getRentalOrders());
            
            logger.debug("订单类型分布统计获取完成");
            return distribution;
            
        } catch (Exception e) {
            logger.error("获取订单类型分布统计失败", e);
            return new java.util.HashMap<>();
        }
    }
    
    /**
     * 计算订单完成率
     * 支持前端KPI指标显示
     * 
     * @return 订单完成率（百分比）
     */
    public BigDecimal calculateCompletionRate() {
        try {
            OrderStatsDTO stats = getOrderStats();
            
            if (stats.getTotal() == 0) {
                return BigDecimal.ZERO;
            }
            
            BigDecimal completionRate = BigDecimal.valueOf(stats.getCompleted())
                    .divide(BigDecimal.valueOf(stats.getTotal()), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            
            logger.debug("订单完成率计算完成: {}%", completionRate);
            return completionRate;
            
        } catch (Exception e) {
            logger.error("计算订单完成率失败", e);
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * 计算订单取消率
     * 支持前端风险指标显示
     * 
     * @return 订单取消率（百分比）
     */
    public BigDecimal calculateCancellationRate() {
        try {
            OrderStatsDTO stats = getOrderStats();
            
            if (stats.getTotal() == 0) {
                return BigDecimal.ZERO;
            }
            
            BigDecimal cancellationRate = BigDecimal.valueOf(stats.getCancelled())
                    .divide(BigDecimal.valueOf(stats.getTotal()), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            
            logger.debug("订单取消率计算完成: {}%", cancellationRate);
            return cancellationRate;
            
        } catch (Exception e) {
            logger.error("计算订单取消率失败", e);
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * 获取处理中订单数量（需要关注的订单）
     * 支持前端待办事项提醒
     * 
     * @return 处理中订单数量
     */
    public Integer getPendingOrdersCount() {
        try {
            OrderStatsDTO stats = getOrderStats();
            // 待处理 + 已确认 + 处理中 = 需要关注的订单
            return stats.getPending() + stats.getConfirmed() + stats.getProcessing();
        } catch (Exception e) {
            logger.error("获取处理中订单数量失败", e);
            return 0;
        }
    }
    
    /**
     * 将Map数据转换为OrderStatsDTO对象
     * 确保字段映射正确性
     * 
     * @param statsMap 统计数据Map
     * @return OrderStatsDTO对象
     */
    private OrderStatsDTO convertMapToDTO(Map<String, Object> statsMap) {
        OrderStatsDTO stats = new OrderStatsDTO();
        
        // 基础统计数据
        stats.setTotal(getIntValue(statsMap, "total"));
        stats.setPending(getIntValue(statsMap, "pending"));
        stats.setConfirmed(getIntValue(statsMap, "confirmed"));
        stats.setProcessing(getIntValue(statsMap, "processing"));
        stats.setShipped(getIntValue(statsMap, "shipped"));
        stats.setDelivered(getIntValue(statsMap, "delivered"));
        stats.setCompleted(getIntValue(statsMap, "completed"));
        stats.setCancelled(getIntValue(statsMap, "cancelled"));
        
        // 收入统计数据
        stats.setTotalRevenue(getBigDecimalValue(statsMap, "totalRevenue"));
        stats.setAverageOrderValue(getBigDecimalValue(statsMap, "averageOrderValue"));
        
        // 类型统计数据
        stats.setSalesOrders(getIntValue(statsMap, "salesOrders"));
        stats.setRentalOrders(getIntValue(statsMap, "rentalOrders"));
        
        return stats;
    }
    
    /**
     * 创建空的统计数据对象
     * 用于无数据情况的处理
     * 
     * @return 空的OrderStatsDTO对象
     */
    private OrderStatsDTO createEmptyStats() {
        OrderStatsDTO stats = new OrderStatsDTO();
        // 构造函数已经初始化为0值，无需额外设置
        return stats;
    }
    
    /**
     * 验证和修正统计数据
     * 确保数据的合理性和一致性
     * 
     * @param stats 统计数据对象
     */
    private void validateAndFixStats(OrderStatsDTO stats) {
        if (stats == null) {
            return;
        }
        
        // 确保所有数值不为负数
        if (stats.getTotal() < 0) stats.setTotal(0);
        if (stats.getPending() < 0) stats.setPending(0);
        if (stats.getConfirmed() < 0) stats.setConfirmed(0);
        if (stats.getProcessing() < 0) stats.setProcessing(0);
        if (stats.getShipped() < 0) stats.setShipped(0);
        if (stats.getDelivered() < 0) stats.setDelivered(0);
        if (stats.getCompleted() < 0) stats.setCompleted(0);
        if (stats.getCancelled() < 0) stats.setCancelled(0);
        if (stats.getSalesOrders() < 0) stats.setSalesOrders(0);
        if (stats.getRentalOrders() < 0) stats.setRentalOrders(0);
        
        // 确保金额不为负数
        if (stats.getTotalRevenue().compareTo(BigDecimal.ZERO) < 0) {
            stats.setTotalRevenue(BigDecimal.ZERO);
        }
        if (stats.getAverageOrderValue().compareTo(BigDecimal.ZERO) < 0) {
            stats.setAverageOrderValue(BigDecimal.ZERO);
        }
        
        // 验证总数的一致性
        int calculatedTotal = stats.getPending() + stats.getConfirmed() + stats.getProcessing() + 
                             stats.getShipped() + stats.getDelivered() + stats.getCompleted() + stats.getCancelled();
        
        if (calculatedTotal != stats.getTotal() && calculatedTotal > 0) {
            logger.warn("订单状态统计总数不一致，修正总数: {} -> {}", stats.getTotal(), calculatedTotal);
            stats.setTotal(calculatedTotal);
        }
        
        // 验证类型统计的一致性
        int calculatedTypeTotal = stats.getSalesOrders() + stats.getRentalOrders();
        if (calculatedTypeTotal != stats.getTotal() && calculatedTypeTotal > 0) {
            logger.warn("订单类型统计总数不一致，修正总数: {} -> {}", stats.getTotal(), calculatedTypeTotal);
            stats.setTotal(calculatedTypeTotal);
        }
    }
    
    /**
     * 从Map中安全获取Integer值
     * 
     * @param map 数据Map
     * @param key 键名
     * @return Integer值，如果不存在或转换失败则返回0
     */
    private Integer getIntValue(Map<String, Object> map, String key) {
        try {
            Object value = map.get(key);
            if (value == null) {
                return 0;
            }
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
            return Integer.parseInt(value.toString());
        } catch (Exception e) {
            logger.warn("获取Integer值失败，key: {}, 返回默认值0", key);
            return 0;
        }
    }
    
    /**
     * 从Map中安全获取BigDecimal值
     * 
     * @param map 数据Map
     * @param key 键名
     * @return BigDecimal值，如果不存在或转换失败则返回0
     */
    private BigDecimal getBigDecimalValue(Map<String, Object> map, String key) {
        try {
            Object value = map.get(key);
            if (value == null) {
                return BigDecimal.ZERO;
            }
            if (value instanceof BigDecimal) {
                return (BigDecimal) value;
            }
            if (value instanceof Number) {
                return BigDecimal.valueOf(((Number) value).doubleValue());
            }
            return new BigDecimal(value.toString());
        } catch (Exception e) {
            logger.warn("获取BigDecimal值失败，key: {}, 返回默认值0", key);
            return BigDecimal.ZERO;
        }
    }
}