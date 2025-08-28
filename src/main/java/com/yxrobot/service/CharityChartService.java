package com.yxrobot.service;

import com.yxrobot.dto.CharityChartDataDTO;
import com.yxrobot.mapper.CharityStatsMapper;
import com.yxrobot.mapper.CharityInstitutionMapper;
import com.yxrobot.mapper.CharityActivityMapper;
import com.yxrobot.mapper.CharityProjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 公益图表数据生成服务类
 * 负责生成各种图表数据，支持ECharts格式输出
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-18
 */
@Service
@Transactional(readOnly = true)
public class CharityChartService {
    
    private static final Logger logger = LoggerFactory.getLogger(CharityChartService.class);
    
    @Autowired
    private CharityStatsMapper charityStatsMapper;
    
    @Autowired
    private CharityInstitutionMapper charityInstitutionMapper;
    
    @Autowired
    private CharityActivityMapper charityActivityMapper;
    
    @Autowired
    private CharityProjectMapper charityProjectMapper;
    
    /**
     * 获取完整的图表数据集合
     * 包含所有类型的图表数据，用于仪表板页面一次性加载
     * 
     * @return 完整的图表数据DTO
     */
    public CharityChartDataDTO getCharityChartData() {
        logger.info("获取完整的公益图表数据集合");
        
        try {
            CharityChartDataDTO chartData = new CharityChartDataDTO();
            
            // 获取各种图表数据
            chartData.setProjectStatusData(getProjectStatusData());
            chartData.setFundingTrendData(getFundingTrendData(12)); // 默认12个月
            chartData.setRegionDistributionData(getRegionDistributionData());
            chartData.setVolunteerActivityData(getVolunteerActivityData(6)); // 默认6个月
            chartData.setInstitutionTypeData(getInstitutionTypeData());
            chartData.setActivityTypeData(getActivityTypeData());
            chartData.setProjectProgressData(getProjectProgressData());
            chartData.setBudgetExecutionData(getBudgetExecutionData());
            
            logger.info("成功获取完整的公益图表数据集合");
            return chartData;
            
        } catch (Exception e) {
            logger.error("获取完整的公益图表数据集合失败", e);
            throw new RuntimeException("获取图表数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取项目状态分布数据
     * 用于生成项目状态分布饼图
     * 
     * @return 项目状态分布数据，ECharts饼图格式
     */
    public Map<String, Object> getProjectStatusData() {
        logger.info("获取项目状态分布数据");
        
        try {
            List<Map<String, Object>> statusData = charityStatsMapper.selectProjectStatusDistribution();
            
            // 转换为ECharts饼图格式
            Map<String, Object> chartData = new HashMap<>();
            chartData.put("type", "pie");
            chartData.put("title", "项目状态分布");
            
            List<String> categories = new ArrayList<>();
            List<Map<String, Object>> series = new ArrayList<>();
            
            for (Map<String, Object> item : statusData) {
                String statusName = (String) item.get("statusName");
                Object count = item.get("count");
                
                categories.add(statusName);
                
                Map<String, Object> seriesItem = new HashMap<>();
                seriesItem.put("name", statusName);
                seriesItem.put("value", count);
                series.add(seriesItem);
            }
            
            chartData.put("categories", categories);
            chartData.put("series", series);
            
            logger.info("成功获取项目状态分布数据，包含 {} 种状态", statusData.size());
            return chartData;
            
        } catch (Exception e) {
            logger.error("获取项目状态分布数据失败", e);
            return createEmptyPieChartData("项目状态分布");
        }
    }
    
    /**
     * 获取资金筹集趋势数据
     * 用于生成资金筹集趋势折线图
     * 
     * @param months 月份数量，默认12个月
     * @return 资金筹集趋势数据，ECharts折线图格式
     */
    public Map<String, Object> getFundingTrendData(Integer months) {
        logger.info("获取资金筹集趋势数据，月份数量: {}", months);
        
        if (months == null || months <= 0) {
            months = 12; // 默认12个月
        }
        
        try {
            List<Map<String, Object>> trendData = charityStatsMapper.selectFundingTrend(months);
            
            // 转换为ECharts折线图格式
            Map<String, Object> chartData = new HashMap<>();
            chartData.put("type", "line");
            chartData.put("title", "资金筹集趋势");
            
            List<String> xAxisData = new ArrayList<>();
            List<Object> raisedSeries = new ArrayList<>();
            List<Object> donatedSeries = new ArrayList<>();
            
            for (Map<String, Object> item : trendData) {
                String month = (String) item.get("month");
                Object raisedAmount = item.get("raised_amount");
                Object donatedAmount = item.get("donated_amount");
                
                xAxisData.add(month);
                raisedSeries.add(raisedAmount != null ? raisedAmount : 0);
                donatedSeries.add(donatedAmount != null ? donatedAmount : 0);
            }
            
            chartData.put("xAxisData", xAxisData);
            
            List<Map<String, Object>> series = new ArrayList<>();
            
            Map<String, Object> raisedSeriesData = new HashMap<>();
            raisedSeriesData.put("name", "筹集金额");
            raisedSeriesData.put("type", "line");
            raisedSeriesData.put("data", raisedSeries);
            series.add(raisedSeriesData);
            
            Map<String, Object> donatedSeriesData = new HashMap<>();
            donatedSeriesData.put("name", "捐赠金额");
            donatedSeriesData.put("type", "line");
            donatedSeriesData.put("data", donatedSeries);
            series.add(donatedSeriesData);
            
            chartData.put("series", series);
            
            logger.info("成功获取资金筹集趋势数据，包含 {} 个月的数据", trendData.size());
            return chartData;
            
        } catch (Exception e) {
            logger.error("获取资金筹集趋势数据失败", e);
            return createEmptyLineChartData("资金筹集趋势");
        }
    }
    
    /**
     * 获取地区分布数据
     * 用于生成项目地区分布饼图
     * 
     * @return 地区分布数据，ECharts饼图格式
     */
    public Map<String, Object> getRegionDistributionData() {
        logger.info("获取地区分布数据");
        
        try {
            List<Map<String, Object>> regionData = charityStatsMapper.selectRegionDistribution();
            
            // 转换为ECharts饼图格式
            Map<String, Object> chartData = new HashMap<>();
            chartData.put("type", "pie");
            chartData.put("title", "项目地区分布");
            
            List<String> categories = new ArrayList<>();
            List<Map<String, Object>> series = new ArrayList<>();
            
            for (Map<String, Object> item : regionData) {
                String region = (String) item.get("region");
                Object count = item.get("count");
                
                categories.add(region);
                
                Map<String, Object> seriesItem = new HashMap<>();
                seriesItem.put("name", region);
                seriesItem.put("value", count);
                series.add(seriesItem);
            }
            
            chartData.put("categories", categories);
            chartData.put("series", series);
            
            logger.info("成功获取地区分布数据，包含 {} 个地区", regionData.size());
            return chartData;
            
        } catch (Exception e) {
            logger.error("获取地区分布数据失败", e);
            return createEmptyPieChartData("项目地区分布");
        }
    }
    
    /**
     * 获取志愿者活动统计数据
     * 用于生成志愿者活动统计柱状图
     * 
     * @param months 月份数量，默认6个月
     * @return 志愿者活动统计数据，ECharts柱状图格式
     */
    public Map<String, Object> getVolunteerActivityData(Integer months) {
        logger.info("获取志愿者活动统计数据，月份数量: {}", months);
        
        if (months == null || months <= 0) {
            months = 6; // 默认6个月
        }
        
        try {
            List<Map<String, Object>> activityData = charityStatsMapper.selectVolunteerActivityStats(months);
            
            // 转换为ECharts柱状图格式
            Map<String, Object> chartData = new HashMap<>();
            chartData.put("type", "bar");
            chartData.put("title", "志愿者活动统计");
            
            List<String> xAxisData = new ArrayList<>();
            List<Object> activityCountSeries = new ArrayList<>();
            List<Object> volunteerCountSeries = new ArrayList<>();
            List<Object> participantCountSeries = new ArrayList<>();
            
            for (Map<String, Object> item : activityData) {
                String month = (String) item.get("month");
                Object activityCount = item.get("activity_count");
                Object volunteerCount = item.get("volunteer_count");
                Object participantCount = item.get("participant_count");
                
                xAxisData.add(month);
                activityCountSeries.add(activityCount != null ? activityCount : 0);
                volunteerCountSeries.add(volunteerCount != null ? volunteerCount : 0);
                participantCountSeries.add(participantCount != null ? participantCount : 0);
            }
            
            chartData.put("xAxisData", xAxisData);
            
            List<Map<String, Object>> series = new ArrayList<>();
            
            Map<String, Object> activitySeriesData = new HashMap<>();
            activitySeriesData.put("name", "活动数量");
            activitySeriesData.put("type", "bar");
            activitySeriesData.put("data", activityCountSeries);
            series.add(activitySeriesData);
            
            Map<String, Object> volunteerSeriesData = new HashMap<>();
            volunteerSeriesData.put("name", "志愿者人数");
            volunteerSeriesData.put("type", "bar");
            volunteerSeriesData.put("data", volunteerCountSeries);
            series.add(volunteerSeriesData);
            
            Map<String, Object> participantSeriesData = new HashMap<>();
            participantSeriesData.put("name", "参与人数");
            participantSeriesData.put("type", "bar");
            participantSeriesData.put("data", participantCountSeries);
            series.add(participantSeriesData);
            
            chartData.put("series", series);
            
            logger.info("成功获取志愿者活动统计数据，包含 {} 个月的数据", activityData.size());
            return chartData;
            
        } catch (Exception e) {
            logger.error("获取志愿者活动统计数据失败", e);
            return createEmptyBarChartData("志愿者活动统计");
        }
    }
    
    /**
     * 获取机构类型分布数据
     * 用于生成机构类型分布饼图
     * 
     * @return 机构类型分布数据，ECharts饼图格式
     */
    public Map<String, Object> getInstitutionTypeData() {
        logger.info("获取机构类型分布数据");
        
        try {
            List<Map<String, Object>> typeData = charityInstitutionMapper.getTypeStatistics();
            
            // 转换为ECharts饼图格式
            Map<String, Object> chartData = new HashMap<>();
            chartData.put("type", "pie");
            chartData.put("title", "机构类型分布");
            
            List<String> categories = new ArrayList<>();
            List<Map<String, Object>> series = new ArrayList<>();
            
            for (Map<String, Object> item : typeData) {
                String typeName = (String) item.get("typeName");
                Object count = item.get("count");
                
                categories.add(typeName);
                
                Map<String, Object> seriesItem = new HashMap<>();
                seriesItem.put("name", typeName);
                seriesItem.put("value", count);
                series.add(seriesItem);
            }
            
            chartData.put("categories", categories);
            chartData.put("series", series);
            
            logger.info("成功获取机构类型分布数据，包含 {} 种类型", typeData.size());
            return chartData;
            
        } catch (Exception e) {
            logger.error("获取机构类型分布数据失败", e);
            return createEmptyPieChartData("机构类型分布");
        }
    }
    
    /**
     * 获取活动类型分布数据
     * 用于生成活动类型分布饼图
     * 
     * @return 活动类型分布数据，ECharts饼图格式
     */
    public Map<String, Object> getActivityTypeData() {
        logger.info("获取活动类型分布数据");
        
        try {
            List<Map<String, Object>> typeData = charityActivityMapper.getTypeStatistics();
            
            // 转换为ECharts饼图格式
            Map<String, Object> chartData = new HashMap<>();
            chartData.put("type", "pie");
            chartData.put("title", "活动类型分布");
            
            List<String> categories = new ArrayList<>();
            List<Map<String, Object>> series = new ArrayList<>();
            
            for (Map<String, Object> item : typeData) {
                String typeName = (String) item.get("typeName");
                Object count = item.get("count");
                
                categories.add(typeName);
                
                Map<String, Object> seriesItem = new HashMap<>();
                seriesItem.put("name", typeName);
                seriesItem.put("value", count);
                series.add(seriesItem);
            }
            
            chartData.put("categories", categories);
            chartData.put("series", series);
            
            logger.info("成功获取活动类型分布数据，包含 {} 种类型", typeData.size());
            return chartData;
            
        } catch (Exception e) {
            logger.error("获取活动类型分布数据失败", e);
            return createEmptyPieChartData("活动类型分布");
        }
    }
    
    /**
     * 获取项目进度分布数据
     * 用于生成项目进度分布柱状图
     * 
     * @return 项目进度分布数据，ECharts柱状图格式
     */
    public Map<String, Object> getProjectProgressData() {
        logger.info("获取项目进度分布数据");
        
        try {
            List<Map<String, Object>> progressData = charityProjectMapper.getProgressStatistics();
            
            // 转换为ECharts柱状图格式
            Map<String, Object> chartData = new HashMap<>();
            chartData.put("type", "bar");
            chartData.put("title", "项目进度分布");
            
            List<String> xAxisData = new ArrayList<>();
            List<Object> series = new ArrayList<>();
            
            for (Map<String, Object> item : progressData) {
                String progressRange = (String) item.get("progress_range");
                Object count = item.get("count");
                
                xAxisData.add(progressRange);
                series.add(count != null ? count : 0);
            }
            
            chartData.put("xAxisData", xAxisData);
            
            List<Map<String, Object>> seriesList = new ArrayList<>();
            Map<String, Object> seriesData = new HashMap<>();
            seriesData.put("name", "项目数量");
            seriesData.put("type", "bar");
            seriesData.put("data", series);
            seriesList.add(seriesData);
            
            chartData.put("series", seriesList);
            
            logger.info("成功获取项目进度分布数据，包含 {} 个进度区间", progressData.size());
            return chartData;
            
        } catch (Exception e) {
            logger.error("获取项目进度分布数据失败", e);
            return createEmptyBarChartData("项目进度分布");
        }
    }
    
    /**
     * 获取预算执行情况数据
     * 用于生成预算执行情况柱状图
     * 
     * @return 预算执行情况数据，ECharts柱状图格式
     */
    public Map<String, Object> getBudgetExecutionData() {
        logger.info("获取预算执行情况数据");
        
        try {
            // 获取项目预算执行数据
            List<Map<String, Object>> projectBudgetData = charityProjectMapper.getBudgetExecutionStats();
            
            // 获取活动预算执行数据
            List<Map<String, Object>> activityBudgetData = charityActivityMapper.getBudgetExecutionStats();
            
            // 转换为ECharts柱状图格式
            Map<String, Object> chartData = new HashMap<>();
            chartData.put("type", "bar");
            chartData.put("title", "预算执行情况");
            
            List<String> xAxisData = new ArrayList<>();
            List<Object> budgetSeries = new ArrayList<>();
            List<Object> costSeries = new ArrayList<>();
            List<Object> executionRateSeries = new ArrayList<>();
            
            // 处理项目预算数据
            for (Map<String, Object> item : projectBudgetData) {
                String type = "项目-" + (String) item.get("type");
                Object totalBudget = item.get("total_budget");
                Object totalCost = item.get("total_cost");
                Object executionRate = item.get("execution_rate");
                
                xAxisData.add(type);
                budgetSeries.add(totalBudget != null ? totalBudget : 0);
                costSeries.add(totalCost != null ? totalCost : 0);
                executionRateSeries.add(executionRate != null ? executionRate : 0);
            }
            
            // 处理活动预算数据
            for (Map<String, Object> item : activityBudgetData) {
                String type = "活动-" + (String) item.get("type");
                Object totalBudget = item.get("total_budget");
                Object totalCost = item.get("total_cost");
                Object executionRate = item.get("execution_rate");
                
                xAxisData.add(type);
                budgetSeries.add(totalBudget != null ? totalBudget : 0);
                costSeries.add(totalCost != null ? totalCost : 0);
                executionRateSeries.add(executionRate != null ? executionRate : 0);
            }
            
            chartData.put("xAxisData", xAxisData);
            
            List<Map<String, Object>> series = new ArrayList<>();
            
            Map<String, Object> budgetSeriesData = new HashMap<>();
            budgetSeriesData.put("name", "预算金额");
            budgetSeriesData.put("type", "bar");
            budgetSeriesData.put("data", budgetSeries);
            series.add(budgetSeriesData);
            
            Map<String, Object> costSeriesData = new HashMap<>();
            costSeriesData.put("name", "实际支出");
            costSeriesData.put("type", "bar");
            costSeriesData.put("data", costSeries);
            series.add(costSeriesData);
            
            Map<String, Object> rateSeriesData = new HashMap<>();
            rateSeriesData.put("name", "执行率(%)");
            rateSeriesData.put("type", "line");
            rateSeriesData.put("yAxisIndex", 1);
            rateSeriesData.put("data", executionRateSeries);
            series.add(rateSeriesData);
            
            chartData.put("series", series);
            
            logger.info("成功获取预算执行情况数据，包含 {} 个类别", xAxisData.size());
            return chartData;
            
        } catch (Exception e) {
            logger.error("获取预算执行情况数据失败", e);
            return createEmptyBarChartData("预算执行情况");
        }
    }
    
    /**
     * 获取月度活动统计数据
     * 用于生成月度活动统计折线图
     * 
     * @param months 月份数量，默认12个月
     * @return 月度活动统计数据，ECharts折线图格式
     */
    public Map<String, Object> getMonthlyActivityData(Integer months) {
        logger.info("获取月度活动统计数据，月份数量: {}", months);
        
        if (months == null || months <= 0) {
            months = 12; // 默认12个月
        }
        
        try {
            List<Map<String, Object>> monthlyData = charityActivityMapper.getMonthlyStatistics(months);
            
            // 转换为ECharts折线图格式
            Map<String, Object> chartData = new HashMap<>();
            chartData.put("type", "line");
            chartData.put("title", "月度活动统计");
            
            List<String> xAxisData = new ArrayList<>();
            List<Object> activityCountSeries = new ArrayList<>();
            List<Object> participantSeries = new ArrayList<>();
            List<Object> budgetSeries = new ArrayList<>();
            
            for (Map<String, Object> item : monthlyData) {
                String month = (String) item.get("month");
                Object activityCount = item.get("activity_count");
                Object totalParticipants = item.get("total_participants");
                Object totalBudget = item.get("total_budget");
                
                xAxisData.add(month);
                activityCountSeries.add(activityCount != null ? activityCount : 0);
                participantSeries.add(totalParticipants != null ? totalParticipants : 0);
                budgetSeries.add(totalBudget != null ? totalBudget : 0);
            }
            
            chartData.put("xAxisData", xAxisData);
            
            List<Map<String, Object>> series = new ArrayList<>();
            
            Map<String, Object> activitySeriesData = new HashMap<>();
            activitySeriesData.put("name", "活动数量");
            activitySeriesData.put("type", "line");
            activitySeriesData.put("data", activityCountSeries);
            series.add(activitySeriesData);
            
            Map<String, Object> participantSeriesData = new HashMap<>();
            participantSeriesData.put("name", "参与人数");
            participantSeriesData.put("type", "line");
            participantSeriesData.put("data", participantSeries);
            series.add(participantSeriesData);
            
            Map<String, Object> budgetSeriesData = new HashMap<>();
            budgetSeriesData.put("name", "预算金额");
            budgetSeriesData.put("type", "line");
            budgetSeriesData.put("yAxisIndex", 1);
            budgetSeriesData.put("data", budgetSeries);
            series.add(budgetSeriesData);
            
            chartData.put("series", series);
            
            logger.info("成功获取月度活动统计数据，包含 {} 个月的数据", monthlyData.size());
            return chartData;
            
        } catch (Exception e) {
            logger.error("获取月度活动统计数据失败", e);
            return createEmptyLineChartData("月度活动统计");
        }
    }
    
    // 辅助方法：创建空的图表数据
    
    /**
     * 创建空的饼图数据
     * 
     * @param title 图表标题
     * @return 空的饼图数据
     */
    private Map<String, Object> createEmptyPieChartData(String title) {
        Map<String, Object> chartData = new HashMap<>();
        chartData.put("type", "pie");
        chartData.put("title", title);
        chartData.put("categories", new ArrayList<>());
        chartData.put("series", new ArrayList<>());
        return chartData;
    }
    
    /**
     * 创建空的折线图数据
     * 
     * @param title 图表标题
     * @return 空的折线图数据
     */
    private Map<String, Object> createEmptyLineChartData(String title) {
        Map<String, Object> chartData = new HashMap<>();
        chartData.put("type", "line");
        chartData.put("title", title);
        chartData.put("xAxisData", new ArrayList<>());
        chartData.put("series", new ArrayList<>());
        return chartData;
    }
    
    /**
     * 创建空的柱状图数据
     * 
     * @param title 图表标题
     * @return 空的柱状图数据
     */
    private Map<String, Object> createEmptyBarChartData(String title) {
        Map<String, Object> chartData = new HashMap<>();
        chartData.put("type", "bar");
        chartData.put("title", title);
        chartData.put("xAxisData", new ArrayList<>());
        chartData.put("series", new ArrayList<>());
        return chartData;
    }
    
    /**
     * 创建模拟的项目状态数据
     */
    private List<Map<String, Object>> createMockProjectStatusData() {
        List<Map<String, Object>> mockData = new ArrayList<>();
        
        Map<String, Object> item1 = new HashMap<>();
        item1.put("statusName", "进行中");
        item1.put("count", 42);
        mockData.add(item1);
        
        Map<String, Object> item2 = new HashMap<>();
        item2.put("statusName", "已完成");
        item2.put("count", 89);
        mockData.add(item2);
        
        Map<String, Object> item3 = new HashMap<>();
        item3.put("statusName", "规划中");
        item3.put("count", 25);
        mockData.add(item3);
        
        return mockData;
    }
    
    /**
     * 创建模拟的项目状态图表数据
     */
    private Map<String, Object> createMockProjectStatusChartData() {
        Map<String, Object> chartData = new HashMap<>();
        chartData.put("type", "pie");
        chartData.put("title", "项目状态分布");
        
        List<String> categories = new ArrayList<>();
        categories.add("进行中");
        categories.add("已完成");
        categories.add("规划中");
        
        List<Map<String, Object>> series = new ArrayList<>();
        
        Map<String, Object> item1 = new HashMap<>();
        item1.put("name", "进行中");
        item1.put("value", 42);
        series.add(item1);
        
        Map<String, Object> item2 = new HashMap<>();
        item2.put("name", "已完成");
        item2.put("value", 89);
        series.add(item2);
        
        Map<String, Object> item3 = new HashMap<>();
        item3.put("name", "规划中");
        item3.put("value", 25);
        series.add(item3);
        
        chartData.put("categories", categories);
        chartData.put("series", series);
        
        return chartData;
    }

    /**
     * 清除图表数据缓存
     * 使用内存缓存，此方法现在为空实现
     * 
     * @param cacheType 缓存类型（已废弃，保留兼容性）
     */
    public void clearChartCache(String cacheType) {
        logger.info("清除图表数据缓存请求 - 缓存类型: {} (使用内存缓存，无需手动清除)", cacheType);
        // 使用内存缓存，缓存会自动管理
        // 保留此方法以维持API兼容性
    }
}