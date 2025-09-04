package com.yxrobot.util;

import com.yxrobot.dto.OrderStatsDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 订单统计工具类
 * 提供订单统计相关的通用方法和计算功能
 * 
 * 主要功能：
 * 1. 统计数据格式化和转换
 * 2. 百分比计算和格式化
 * 3. 统计数据验证和修正
 * 4. 前端展示数据格式化
 */
public class OrderStatsUtil {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final int PERCENTAGE_SCALE = 2; // 百分比保留2位小数
    private static final int AMOUNT_SCALE = 2; // 金额保留2位小数
    
    /**
     * 计算百分比
     * 
     * @param numerator 分子
     * @param denominator 分母
     * @return 百分比值（0-100）
     */
    public static BigDecimal calculatePercentage(int numerator, int denominator) {
        if (denominator == 0) {
            return BigDecimal.ZERO;
        }
        
        return BigDecimal.valueOf(numerator)
                .divide(BigDecimal.valueOf(denominator), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(PERCENTAGE_SCALE, RoundingMode.HALF_UP);
    }
    
    /**
     * 计算百分比
     * 
     * @param numerator 分子
     * @param denominator 分母
     * @return 百分比值（0-100）
     */
    public static BigDecimal calculatePercentage(BigDecimal numerator, BigDecimal denominator) {
        if (denominator.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        return numerator.divide(denominator, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(PERCENTAGE_SCALE, RoundingMode.HALF_UP);
    }
    
    /**
     * 格式化金额
     * 
     * @param amount 金额
     * @return 格式化后的金额
     */
    public static BigDecimal formatAmount(BigDecimal amount) {
        if (amount == null) {
            return BigDecimal.ZERO;
        }
        return amount.setScale(AMOUNT_SCALE, RoundingMode.HALF_UP);
    }
    
    /**
     * 验证和修正统计数据
     * 确保数据的合理性和一致性
     * 
     * @param stats 统计数据对象
     * @return 修正后的统计数据对象
     */
    public static OrderStatsDTO validateAndFixStats(OrderStatsDTO stats) {
        if (stats == null) {
            return new OrderStatsDTO();
        }
        
        // 确保所有数值不为负数
        stats.setTotal(Math.max(0, stats.getTotal()));
        stats.setPending(Math.max(0, stats.getPending()));
        stats.setConfirmed(Math.max(0, stats.getConfirmed()));
        stats.setProcessing(Math.max(0, stats.getProcessing()));
        stats.setShipped(Math.max(0, stats.getShipped()));
        stats.setDelivered(Math.max(0, stats.getDelivered()));
        stats.setCompleted(Math.max(0, stats.getCompleted()));
        stats.setCancelled(Math.max(0, stats.getCancelled()));
        stats.setSalesOrders(Math.max(0, stats.getSalesOrders()));
        stats.setRentalOrders(Math.max(0, stats.getRentalOrders()));
        
        // 确保金额不为负数
        if (stats.getTotalRevenue().compareTo(BigDecimal.ZERO) < 0) {
            stats.setTotalRevenue(BigDecimal.ZERO);
        }
        if (stats.getAverageOrderValue().compareTo(BigDecimal.ZERO) < 0) {
            stats.setAverageOrderValue(BigDecimal.ZERO);
        }
        
        // 格式化金额
        stats.setTotalRevenue(formatAmount(stats.getTotalRevenue()));
        stats.setAverageOrderValue(formatAmount(stats.getAverageOrderValue()));
        
        return stats;
    }
    
    /**
     * 创建前端展示用的统计数据Map
     * 包含格式化的数据和计算的百分比
     * 
     * @param stats 统计数据对象
     * @return 前端展示用的数据Map
     */
    public static Map<String, Object> createDisplayStatsMap(OrderStatsDTO stats) {
        Map<String, Object> displayMap = new HashMap<>();
        
        if (stats == null) {
            return displayMap;
        }
        
        // 基础统计数据
        displayMap.put("total", stats.getTotal());
        displayMap.put("pending", stats.getPending());
        displayMap.put("confirmed", stats.getConfirmed());
        displayMap.put("processing", stats.getProcessing());
        displayMap.put("shipped", stats.getShipped());
        displayMap.put("delivered", stats.getDelivered());
        displayMap.put("completed", stats.getCompleted());
        displayMap.put("cancelled", stats.getCancelled());
        
        // 金额数据
        displayMap.put("totalRevenue", stats.getTotalRevenue());
        displayMap.put("averageOrderValue", stats.getAverageOrderValue());
        
        // 类型统计
        displayMap.put("salesOrders", stats.getSalesOrders());
        displayMap.put("rentalOrders", stats.getRentalOrders());
        
        // 计算百分比
        displayMap.put("completionRate", calculatePercentage(stats.getCompleted(), stats.getTotal()));
        displayMap.put("cancellationRate", calculatePercentage(stats.getCancelled(), stats.getTotal()));
        displayMap.put("salesOrderRate", calculatePercentage(stats.getSalesOrders(), stats.getTotal()));
        displayMap.put("rentalOrderRate", calculatePercentage(stats.getRentalOrders(), stats.getTotal()));
        
        // 处理中订单数量（需要关注的订单）
        int pendingCount = stats.getPending() + stats.getConfirmed() + stats.getProcessing();
        displayMap.put("pendingOrdersCount", pendingCount);
        displayMap.put("pendingOrdersRate", calculatePercentage(pendingCount, stats.getTotal()));
        
        return displayMap;
    }
    
    /**
     * 创建状态分布数据
     * 用于前端图表展示
     * 
     * @param stats 统计数据对象
     * @return 状态分布数据
     */
    public static Map<String, Object> createStatusDistribution(OrderStatsDTO stats) {
        Map<String, Object> distribution = new HashMap<>();
        
        if (stats == null) {
            return distribution;
        }
        
        distribution.put("pending", createStatusItem("待处理", stats.getPending(), stats.getTotal()));
        distribution.put("confirmed", createStatusItem("已确认", stats.getConfirmed(), stats.getTotal()));
        distribution.put("processing", createStatusItem("处理中", stats.getProcessing(), stats.getTotal()));
        distribution.put("shipped", createStatusItem("已发货", stats.getShipped(), stats.getTotal()));
        distribution.put("delivered", createStatusItem("已送达", stats.getDelivered(), stats.getTotal()));
        distribution.put("completed", createStatusItem("已完成", stats.getCompleted(), stats.getTotal()));
        distribution.put("cancelled", createStatusItem("已取消", stats.getCancelled(), stats.getTotal()));
        
        return distribution;
    }
    
    /**
     * 创建类型分布数据
     * 用于前端图表展示
     * 
     * @param stats 统计数据对象
     * @return 类型分布数据
     */
    public static Map<String, Object> createTypeDistribution(OrderStatsDTO stats) {
        Map<String, Object> distribution = new HashMap<>();
        
        if (stats == null) {
            return distribution;
        }
        
        distribution.put("sales", createTypeItem("销售订单", stats.getSalesOrders(), stats.getTotal()));
        distribution.put("rental", createTypeItem("租赁订单", stats.getRentalOrders(), stats.getTotal()));
        
        return distribution;
    }
    
    /**
     * 创建状态项数据
     * 
     * @param name 状态名称
     * @param count 数量
     * @param total 总数
     * @return 状态项数据
     */
    private static Map<String, Object> createStatusItem(String name, int count, int total) {
        Map<String, Object> item = new HashMap<>();
        item.put("name", name);
        item.put("count", count);
        item.put("percentage", calculatePercentage(count, total));
        return item;
    }
    
    /**
     * 创建类型项数据
     * 
     * @param name 类型名称
     * @param count 数量
     * @param total 总数
     * @return 类型项数据
     */
    private static Map<String, Object> createTypeItem(String name, int count, int total) {
        Map<String, Object> item = new HashMap<>();
        item.put("name", name);
        item.put("count", count);
        item.put("percentage", calculatePercentage(count, total));
        return item;
    }
    
    /**
     * 格式化日期为字符串
     * 
     * @param date 日期
     * @return 格式化后的日期字符串
     */
    public static String formatDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DATE_FORMATTER);
    }
    
    /**
     * 解析日期字符串
     * 
     * @param dateString 日期字符串
     * @return 解析后的日期
     */
    public static LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateString, DATE_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 检查统计数据是否为空
     * 
     * @param stats 统计数据对象
     * @return 是否为空
     */
    public static boolean isEmpty(OrderStatsDTO stats) {
        return stats == null || stats.getTotal() == 0;
    }
    
    /**
     * 创建空的统计数据对象
     * 
     * @return 空的统计数据对象
     */
    public static OrderStatsDTO createEmptyStats() {
        return new OrderStatsDTO();
    }
}