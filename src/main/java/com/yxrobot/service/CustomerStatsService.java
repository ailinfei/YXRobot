package com.yxrobot.service;

import com.yxrobot.dto.CustomerStatsDTO;
import com.yxrobot.mapper.CustomerMapper;
import com.yxrobot.mapper.CustomerStatsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 客户统计服务类
 * 专门处理客户统计相关的业务逻辑
 * 支持前端统计卡片和图表显示
 * 
 * 主要功能：
 * 1. 实时统计计算 - 支持前端统计卡片数据需求
 * 2. 客户等级分布统计 - 普通、VIP、高级客户统计
 * 3. 活跃设备统计计算 - 设备使用情况统计
 * 4. 总收入统计和本月新增客户统计
 * 5. 统计数据缓存和性能优化
 */
@Service
@Transactional(readOnly = true)
public class CustomerStatsService {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerStatsService.class);
    
    @Autowired
    private CustomerMapper customerMapper;
    
    @Autowired
    private CustomerStatsMapper customerStatsMapper;
    
    /**
     * 获取客户统计数据（支持前端统计卡片）
     * 包含：总客户数、等级分布、活跃设备、总收入等
     * 使用缓存提高性能，缓存时间5分钟
     * @return 客户统计数据
     */
    @Cacheable(value = "customerStats", key = "'current'", unless = "#result == null")
    public CustomerStatsDTO getCustomerStats() {
        try {
            logger.debug("开始获取客户统计数据");
            
            // 优先使用实时计算的统计数据，确保数据准确性
            CustomerStatsDTO stats = customerStatsMapper.calculateRealTimeStats();
            
            // 如果实时计算失败，尝试从统计表获取最新数据
            if (stats == null) {
                logger.warn("实时统计计算失败，尝试从统计表获取数据");
                stats = customerStatsMapper.selectStatsDTO();
            }
            
            // 如果仍然没有数据，返回零值统计（空状态处理）
            if (stats == null) {
                logger.info("数据库中暂无客户数据，返回零值统计");
                stats = createEmptyStats();
            }
            
            // 验证和修正数据
            validateAndFixStats(stats);
            
            logger.debug("客户统计数据获取完成: 总数={}, VIP={}, 高级={}", 
                        stats.getTotal(), stats.getVip(), stats.getPremium());
            
            return stats;
            
        } catch (Exception e) {
            logger.error("获取客户统计数据失败", e);
            // 发生异常时返回空统计，确保前端页面正常显示
            return createEmptyStats();
        }
    }
    
    /**
     * 创建空的统计数据（用于空状态处理）
     * @return 零值统计数据
     */
    private CustomerStatsDTO createEmptyStats() {
        CustomerStatsDTO stats = new CustomerStatsDTO();
        stats.setTotal(0);
        stats.setRegular(0);
        stats.setVip(0);
        stats.setPremium(0);
        stats.setActiveDevices(0);
        stats.setTotalRevenue(BigDecimal.ZERO);
        stats.setNewThisMonth(0);
        return stats;
    }
    
    /**
     * 验证和修正统计数据
     * @param stats 统计数据
     */
    private void validateAndFixStats(CustomerStatsDTO stats) {
        if (stats == null) return;
        
        // 确保数值不为null
        if (stats.getTotal() == null) stats.setTotal(0);
        if (stats.getRegular() == null) stats.setRegular(0);
        if (stats.getVip() == null) stats.setVip(0);
        if (stats.getPremium() == null) stats.setPremium(0);
        if (stats.getActiveDevices() == null) stats.setActiveDevices(0);
        if (stats.getTotalRevenue() == null) stats.setTotalRevenue(BigDecimal.ZERO);
        if (stats.getNewThisMonth() == null) stats.setNewThisMonth(0);
        
        // 验证数据逻辑一致性
        int calculatedTotal = stats.getRegular() + stats.getVip() + stats.getPremium();
        if (calculatedTotal != stats.getTotal()) {
            logger.warn("客户等级统计数据不一致，总数={}, 计算总数={}", stats.getTotal(), calculatedTotal);
            // 以等级分布的总和为准
            stats.setTotal(calculatedTotal);
        }
        
        // 确保收入金额精度
        if (stats.getTotalRevenue() != null) {
            stats.setTotalRevenue(stats.getTotalRevenue().setScale(2, RoundingMode.HALF_UP));
        }
    }
    
    /**
     * 获取客户等级分布统计（支持前端图表显示）
     * @return 客户等级分布数据
     */
    @Cacheable(value = "customerLevelDistribution", key = "'current'")
    public List<Map<String, Object>> getCustomerLevelDistribution() {
        try {
            List<Map<String, Object>> distribution = customerMapper.selectCustomerLevelStats();
            
            // 如果没有数据，返回空分布
            if (distribution == null || distribution.isEmpty()) {
                logger.info("暂无客户等级分布数据");
                return createEmptyLevelDistribution();
            }
            
            return distribution;
            
        } catch (Exception e) {
            logger.error("获取客户等级分布失败", e);
            return createEmptyLevelDistribution();
        }
    }
    
    /**
     * 创建空的等级分布数据
     * @return 空等级分布
     */
    private List<Map<String, Object>> createEmptyLevelDistribution() {
        return List.of(
            Map.of("level", "regular", "count", 0, "percentage", 0.0),
            Map.of("level", "vip", "count", 0, "percentage", 0.0),
            Map.of("level", "premium", "count", 0, "percentage", 0.0)
        );
    }
    
    /**
     * 获取客户状态分布统计
     * @return 客户状态分布数据
     */
    @Cacheable(value = "customerStatusDistribution", key = "'current'")
    public List<Map<String, Object>> getCustomerStatusDistribution() {
        try {
            return customerMapper.selectCustomerStatusStats();
        } catch (Exception e) {
            logger.error("获取客户状态分布失败", e);
            return List.of();
        }
    }
    
    /**
     * 获取地区客户分布统计
     * @return 地区分布数据
     */
    @Cacheable(value = "customerRegionDistribution", key = "'current'")
    public List<Map<String, Object>> getRegionDistribution() {
        try {
            return customerMapper.selectRegionDistribution();
        } catch (Exception e) {
            logger.error("获取地区分布失败", e);
            return List.of();
        }
    }
    
    /**
     * 获取本月新增客户数（支持前端统计卡片）
     * @return 本月新增客户数
     */
    @Cacheable(value = "newCustomersThisMonth", key = "'current'")
    public Integer getNewCustomersThisMonth() {
        try {
            Integer count = customerStatsMapper.calculateNewCustomersThisMonth();
            return count != null ? count : 0;
        } catch (Exception e) {
            logger.error("获取本月新增客户数失败", e);
            return 0;
        }
    }
    
    /**
     * 获取活跃设备总数（支持前端统计卡片）
     * @return 活跃设备总数
     */
    @Cacheable(value = "activeDeviceCount", key = "'current'")
    public Integer getActiveDeviceCount() {
        try {
            Integer count = customerStatsMapper.calculateActiveDeviceCount();
            return count != null ? count : 0;
        } catch (Exception e) {
            logger.error("获取活跃设备总数失败", e);
            return 0;
        }
    }
    
    /**
     * 获取客户总收入（支持前端统计卡片）
     * 格式化为万元单位显示
     * @return 客户总收入
     */
    @Cacheable(value = "totalRevenue", key = "'current'")
    public BigDecimal getTotalRevenue() {
        try {
            BigDecimal revenue = customerStatsMapper.calculateTotalRevenue();
            return revenue != null ? revenue.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        } catch (Exception e) {
            logger.error("获取客户总收入失败", e);
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * 获取格式化的总收入（以万元为单位）
     * @return 格式化的收入字符串
     */
    public String getFormattedTotalRevenue() {
        BigDecimal revenue = getTotalRevenue();
        if (revenue.compareTo(new BigDecimal("10000")) >= 0) {
            BigDecimal revenueInWan = revenue.divide(new BigDecimal("10000"), 2, RoundingMode.HALF_UP);
            return revenueInWan + "万";
        }
        return revenue.toString();
    }
    
    /**
     * 计算客户价值评分
     * 基于消费金额、设备数量、活跃度等因素
     * @param customerId 客户ID
     * @return 客户价值评分（0-10分）
     */
    public BigDecimal calculateCustomerValue(Long customerId) {
        Map<String, Object> stats = customerMapper.selectCustomerFullStats(customerId);
        
        if (stats == null) {
            return BigDecimal.ZERO;
        }
        
        // 获取统计数据
        BigDecimal totalSpent = (BigDecimal) stats.getOrDefault("totalSpent", BigDecimal.ZERO);
        Integer deviceCount = (Integer) stats.getOrDefault("deviceCount", 0);
        Integer orderCount = (Integer) stats.getOrDefault("orderCount", 0);
        Integer serviceCount = (Integer) stats.getOrDefault("serviceCount", 0);
        
        // 计算价值评分（简化算法）
        BigDecimal score = BigDecimal.ZERO;
        
        // 消费金额权重（40%）
        if (totalSpent.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal spentScore = totalSpent.divide(new BigDecimal("10000"), 2, BigDecimal.ROUND_HALF_UP);
            if (spentScore.compareTo(new BigDecimal("4")) > 0) {
                spentScore = new BigDecimal("4");
            }
            score = score.add(spentScore);
        }
        
        // 设备数量权重（30%）
        if (deviceCount > 0) {
            BigDecimal deviceScore = new BigDecimal(Math.min(deviceCount * 0.5, 3.0));
            score = score.add(deviceScore);
        }
        
        // 订单数量权重（20%）
        if (orderCount > 0) {
            BigDecimal orderScore = new BigDecimal(Math.min(orderCount * 0.2, 2.0));
            score = score.add(orderScore);
        }
        
        // 服务记录权重（10%）
        if (serviceCount > 0) {
            BigDecimal serviceScore = new BigDecimal(Math.min(serviceCount * 0.1, 1.0));
            score = score.add(serviceScore);
        }
        
        // 确保评分在0-10范围内
        if (score.compareTo(new BigDecimal("10")) > 0) {
            score = new BigDecimal("10");
        }
        
        return score.setScale(1, BigDecimal.ROUND_HALF_UP);
    }
    
    /**
     * 自动升级客户等级
     * 基于消费金额和设备数量自动调整客户等级
     * @param customerId 客户ID
     */
    public void autoUpgradeCustomerLevel(Long customerId) {
        Map<String, Object> stats = customerMapper.selectCustomerFullStats(customerId);
        
        if (stats == null) {
            return;
        }
        
        BigDecimal totalSpent = (BigDecimal) stats.getOrDefault("totalSpent", BigDecimal.ZERO);
        Integer deviceCount = (Integer) stats.getOrDefault("deviceCount", 0);
        
        String newLevel = null;
        
        // 升级规则
        if (totalSpent.compareTo(new BigDecimal("50000")) >= 0 && deviceCount >= 5) {
            newLevel = "premium";
        } else if (totalSpent.compareTo(new BigDecimal("10000")) >= 0 && deviceCount >= 2) {
            newLevel = "vip";
        } else {
            newLevel = "regular";
        }
        
        // 更新客户等级
        customerMapper.updateCustomerLevel(customerId, newLevel);
    }
    
    /**
     * 获取客户统计概览（用于前端仪表板）
     * 包含完整的统计信息和趋势数据
     * @return 统计概览数据
     */
    @Cacheable(value = "customerOverview", key = "'dashboard'")
    public Map<String, Object> getCustomerOverview() {
        try {
            CustomerStatsDTO stats = getCustomerStats();
            
            Map<String, Object> overview = new HashMap<>();
            overview.put("totalCustomers", stats.getTotal());
            overview.put("levelDistribution", Map.of(
                "regular", stats.getRegular(),
                "vip", stats.getVip(),
                "premium", stats.getPremium()
            ));
            overview.put("activeDevices", stats.getActiveDevices());
            overview.put("totalRevenue", stats.getTotalRevenue());
            overview.put("formattedRevenue", getFormattedTotalRevenue());
            overview.put("newThisMonth", stats.getNewThisMonth());
            overview.put("growthRate", calculateGrowthRate());
            overview.put("lastUpdated", LocalDateTime.now());
            
            return overview;
            
        } catch (Exception e) {
            logger.error("获取客户统计概览失败", e);
            return createEmptyOverview();
        }
    }
    
    /**
     * 创建空的概览数据
     * @return 空概览数据
     */
    private Map<String, Object> createEmptyOverview() {
        Map<String, Object> overview = new HashMap<>();
        overview.put("totalCustomers", 0);
        overview.put("levelDistribution", Map.of("regular", 0, "vip", 0, "premium", 0));
        overview.put("activeDevices", 0);
        overview.put("totalRevenue", BigDecimal.ZERO);
        overview.put("formattedRevenue", "0");
        overview.put("newThisMonth", 0);
        overview.put("growthRate", BigDecimal.ZERO);
        overview.put("lastUpdated", LocalDateTime.now());
        return overview;
    }
    
    /**
     * 计算客户增长率（与上月比较）
     * @return 增长率百分比
     */
    private BigDecimal calculateGrowthRate() {
        try {
            // 获取本月和上月的新增客户数
            LocalDate now = LocalDate.now();
            Integer thisMonth = customerStatsMapper.calculateNewCustomersByMonth(now.getYear(), now.getMonthValue());
            Integer lastMonth = customerStatsMapper.calculateNewCustomersByMonth(
                now.minusMonths(1).getYear(), 
                now.minusMonths(1).getMonthValue()
            );
            
            if (thisMonth == null) thisMonth = 0;
            if (lastMonth == null) lastMonth = 0;
            
            if (lastMonth == 0) {
                return thisMonth > 0 ? new BigDecimal("100") : BigDecimal.ZERO;
            }
            
            // 计算增长率：(本月 - 上月) / 上月 * 100
            BigDecimal growth = new BigDecimal(thisMonth - lastMonth)
                .divide(new BigDecimal(lastMonth), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
            
            return growth.setScale(2, RoundingMode.HALF_UP);
            
        } catch (Exception e) {
            logger.error("计算客户增长率失败", e);
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * 刷新统计数据缓存
     * 用于数据更新后立即刷新统计
     */
    @Transactional
    public void refreshStatsCache() {
        try {
            logger.info("开始刷新客户统计数据缓存");
            
            // 生成今日统计数据
            customerStatsMapper.generateTodayStats();
            
            // 这里可以添加缓存清理逻辑
            // cacheManager.getCache("customerStats").clear();
            
            logger.info("客户统计数据缓存刷新完成");
            
        } catch (Exception e) {
            logger.error("刷新统计数据缓存失败", e);
        }
    }
    
    /**
     * 获取客户趋势数据（用于前端图表）
     * @param days 天数
     * @return 趋势数据
     */
    @Cacheable(value = "customerTrend", key = "#days")
    public List<Map<String, Object>> getCustomerTrend(Integer days) {
        try {
            if (days == null || days <= 0) {
                days = 30; // 默认30天
            }
            
            return customerStatsMapper.selectCustomerTrendByDay(days);
            
        } catch (Exception e) {
            logger.error("获取客户趋势数据失败", e);
            return List.of();
        }
    }
    
    /**
     * 获取实时统计数据（不使用缓存）
     * 用于需要最新数据的场景
     * @return 实时统计数据
     */
    public CustomerStatsDTO getRealTimeStats() {
        try {
            logger.debug("获取实时客户统计数据");
            CustomerStatsDTO stats = customerStatsMapper.calculateRealTimeStats();
            
            if (stats == null) {
                stats = createEmptyStats();
            } else {
                validateAndFixStats(stats);
            }
            
            return stats;
            
        } catch (Exception e) {
            logger.error("获取实时统计数据失败", e);
            return createEmptyStats();
        }
    }
}