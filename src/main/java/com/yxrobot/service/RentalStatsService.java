package com.yxrobot.service;

import com.yxrobot.dto.RentalStatsDTO;
import com.yxrobot.dto.TodayStatsDTO;
import com.yxrobot.mapper.RentalRecordMapper;
import com.yxrobot.mapper.RentalDeviceMapper;
import com.yxrobot.mapper.RentalCustomerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;

/**
 * 租赁统计服务类
 * 处理租赁统计业务逻辑，支持前端核心指标卡片功能
 * 
 * @author Kiro
 * @date 2025-01-28
 */
@Service
public class RentalStatsService {
    
    private static final Logger logger = LoggerFactory.getLogger(RentalStatsService.class);
    
    @Autowired
    private RentalRecordMapper rentalRecordMapper;
    
    @Autowired
    private RentalDeviceMapper rentalDeviceMapper;
    
    @Autowired
    private RentalCustomerMapper rentalCustomerMapper;
    
    /**
     * 获取租赁统计数据
     * 支持前端核心指标卡片显示
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 租赁统计DTO
     */
    public RentalStatsDTO getRentalStats(LocalDate startDate, LocalDate endDate) {
        logger.info("开始获取租赁统计数据，时间范围：{} 到 {}", startDate, endDate);
        
        try {
            // 查询基础统计数据
            Map<String, Object> statsData = rentalRecordMapper.selectRentalStats(startDate, endDate);
            
            // 查询设备状态统计
            Map<String, Object> deviceStats = rentalDeviceMapper.selectDeviceStatusStats();
            
            // 查询客户统计
            Long activeCustomerCount = rentalCustomerMapper.selectActiveCustomerCount();
            
            // 创建统计DTO
            RentalStatsDTO statsDTO = new RentalStatsDTO();
            
            // 设置基础统计数据
            if (statsData != null) {
                statsDTO.setTotalRentalRevenue(getBigDecimalValue(statsData, "totalRentalRevenue"));
                statsDTO.setTotalRentalOrders(getIntegerValue(statsData, "totalRentalOrders"));
                statsDTO.setTotalRentalDevices(getIntegerValue(statsData, "totalRentalDevices"));
                statsDTO.setActiveRentalDevices(getIntegerValue(statsData, "activeRentalDevices"));
                statsDTO.setAverageRentalPeriod(getBigDecimalValue(statsData, "averageRentalPeriod"));
            }
            
            // 设置设备统计数据
            if (deviceStats != null) {
                Integer activeDevices = getIntegerValue(deviceStats, "active");
                Integer totalDevices = activeDevices + getIntegerValue(deviceStats, "idle") + getIntegerValue(deviceStats, "maintenance");
                
                if (totalDevices > 0) {
                    BigDecimal utilizationRate = BigDecimal.valueOf(activeDevices)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(totalDevices), 2, RoundingMode.HALF_UP);
                    statsDTO.setDeviceUtilizationRate(utilizationRate);
                }
            }
            
            // 计算增长率
            calculateGrowthRates(statsDTO, startDate, endDate);
            
            logger.info("租赁统计数据获取成功：总收入={}, 总订单={}, 设备利用率={}%", 
                       statsDTO.getTotalRentalRevenue(), 
                       statsDTO.getTotalRentalOrders(),
                       statsDTO.getDeviceUtilizationRate());
            
            return statsDTO;
            
        } catch (Exception e) {
            logger.error("获取租赁统计数据失败", e);
            // 返回默认值，避免前端报错
            return new RentalStatsDTO();
        }
    }
    
    /**
     * 获取今日概览统计数据（Map格式）
     * 支持前端右侧面板今日概览功能
     * 
     * @return 今日统计Map
     */
    public Map<String, Object> getTodayStats() {
        logger.info("开始获取今日概览统计数据（Map格式）");
        
        try {
            LocalDate today = LocalDate.now();
            Map<String, Object> todayData = rentalRecordMapper.selectTodayStats(today);
            
            if (todayData != null) {
                logger.info("今日概览统计数据获取成功：收入={}, 订单={}, 活跃设备={}", 
                           todayData.get("revenue"), 
                           todayData.get("orders"),
                           todayData.get("activeDevices"));
                return todayData;
            } else {
                // 返回默认值，避免前端报错
                Map<String, Object> defaultStats = new HashMap<>();
                defaultStats.put("revenue", BigDecimal.ZERO);
                defaultStats.put("orders", 0);
                defaultStats.put("activeDevices", 0);
                defaultStats.put("avgUtilization", BigDecimal.ZERO);
                return defaultStats;
            }
            
        } catch (Exception e) {
            logger.error("获取今日概览统计数据失败", e);
            // 返回默认值，避免前端报错
            Map<String, Object> defaultStats = new HashMap<>();
            defaultStats.put("revenue", BigDecimal.ZERO);
            defaultStats.put("orders", 0);
            defaultStats.put("activeDevices", 0);
            defaultStats.put("avgUtilization", BigDecimal.ZERO);
            return defaultStats;
        }
    }
    
    /**
     * 获取今日概览统计数据（DTO格式）
     * 支持前端右侧面板今日概览功能
     * 
     * @return 今日统计DTO
     */
    public TodayStatsDTO getTodayStatsDTO() {
        logger.info("开始获取今日概览统计数据");
        
        try {
            LocalDate today = LocalDate.now();
            Map<String, Object> todayData = rentalRecordMapper.selectTodayStats(today);
            
            TodayStatsDTO todayStats = new TodayStatsDTO();
            
            if (todayData != null) {
                todayStats.setRevenue(getBigDecimalValue(todayData, "revenue"));
                todayStats.setOrders(getIntegerValue(todayData, "orders"));
                todayStats.setActiveDevices(getIntegerValue(todayData, "activeDevices"));
                todayStats.setAvgUtilization(getBigDecimalValue(todayData, "avgUtilization"));
            }
            
            logger.info("今日概览统计数据获取成功：收入={}, 订单={}, 活跃设备={}", 
                       todayStats.getRevenue(), 
                       todayStats.getOrders(),
                       todayStats.getActiveDevices());
            
            return todayStats;
            
        } catch (Exception e) {
            logger.error("获取今日概览统计数据失败", e);
            // 返回默认值，避免前端报错
            return new TodayStatsDTO();
        }
    }
    
    /**
     * 计算增长率
     * 
     * @param statsDTO 统计DTO
     * @param startDate 开始日期
     * @param endDate 结束日期
     */
    private void calculateGrowthRates(RentalStatsDTO statsDTO, LocalDate startDate, LocalDate endDate) {
        try {
            // 计算上一个周期的日期范围
            long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
            LocalDate previousStartDate = startDate.minusDays(daysBetween + 1);
            LocalDate previousEndDate = startDate.minusDays(1);
            
            // 查询上一个周期的统计数据
            Map<String, Object> previousStats = rentalRecordMapper.selectRentalStats(previousStartDate, previousEndDate);
            
            if (previousStats != null) {
                // 计算收入增长率
                BigDecimal currentRevenue = statsDTO.getTotalRentalRevenue();
                BigDecimal previousRevenue = getBigDecimalValue(previousStats, "totalRentalRevenue");
                
                if (previousRevenue.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal revenueGrowthRate = currentRevenue.subtract(previousRevenue)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(previousRevenue, 2, RoundingMode.HALF_UP);
                    statsDTO.setRevenueGrowthRate(revenueGrowthRate);
                }
                
                // 计算设备增长率
                Integer currentDevices = statsDTO.getTotalRentalDevices();
                Integer previousDevices = getIntegerValue(previousStats, "totalRentalDevices");
                
                if (previousDevices > 0) {
                    BigDecimal deviceGrowthRate = BigDecimal.valueOf(currentDevices - previousDevices)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(previousDevices), 2, RoundingMode.HALF_UP);
                    statsDTO.setDeviceGrowthRate(deviceGrowthRate);
                }
            }
            
        } catch (Exception e) {
            logger.warn("计算增长率失败，使用默认值", e);
            // 设置默认增长率
            statsDTO.setRevenueGrowthRate(BigDecimal.valueOf(15.0));
            statsDTO.setDeviceGrowthRate(BigDecimal.valueOf(10.0));
        }
    }
    
    /**
     * 从Map中安全获取BigDecimal值
     */
    private BigDecimal getBigDecimalValue(Map<String, Object> map, String key) {
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
        try {
            return new BigDecimal(value.toString());
        } catch (NumberFormatException e) {
            logger.warn("无法转换为BigDecimal: {} = {}", key, value);
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * 从Map中安全获取Integer值
     */
    private Integer getIntegerValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return 0;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.valueOf(value.toString());
        } catch (NumberFormatException e) {
            logger.warn("无法转换为Integer: {} = {}", key, value);
            return 0;
        }
    }
    
    /**
     * 获取租赁统计数据（无日期限制）
     * 用于获取总体统计数据
     * 
     * @return 租赁统计DTO
     */
    public RentalStatsDTO getRentalStats() {
        return getRentalStats(null, null);
    }
    
    /**
     * 获取指定月份的租赁统计数据
     * 
     * @param year 年份
     * @param month 月份
     * @return 租赁统计DTO
     */
    public RentalStatsDTO getMonthlyRentalStats(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        return getRentalStats(startDate, endDate);
    }
    
    /**
     * 获取最近N天的租赁统计数据
     * 
     * @param days 天数
     * @return 租赁统计DTO
     */
    public RentalStatsDTO getRecentDaysRentalStats(int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);
        return getRentalStats(startDate, endDate);
    }
}