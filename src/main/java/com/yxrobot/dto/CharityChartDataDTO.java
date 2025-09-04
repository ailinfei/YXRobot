package com.yxrobot.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 公益项目图表数据传输对象
 * 用于前端ECharts图表数据的传输，采用Map格式以支持灵活的图表配置
 * 
 * @author YXRobot
 * @version 1.0
 * @since 2024-12-18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharityChartDataDTO {
    
    /**
     * 项目状态分布数据（饼图）
     */
    private Map<String, Object> projectStatusData;
    
    /**
     * 资金筹集趋势数据（折线图）
     */
    private Map<String, Object> fundingTrendData;
    
    /**
     * 项目地区分布数据（饼图）
     */
    private Map<String, Object> regionDistributionData;
    
    /**
     * 志愿者活动统计数据（柱状图）
     */
    private Map<String, Object> volunteerActivityData;
    
    /**
     * 机构类型分布数据（饼图）
     */
    private Map<String, Object> institutionTypeData;
    
    /**
     * 活动类型分布数据（饼图）
     */
    private Map<String, Object> activityTypeData;
    
    /**
     * 项目进度分布数据（柱状图）
     */
    private Map<String, Object> projectProgressData;
    
    /**
     * 预算执行情况数据（柱状图）
     */
    private Map<String, Object> budgetExecutionData;
    
    // 手动添加setter方法，确保Lombok正常工作
    public void setProjectStatusData(Map<String, Object> projectStatusData) {
        this.projectStatusData = projectStatusData;
    }
    
    public void setFundingTrendData(Map<String, Object> fundingTrendData) {
        this.fundingTrendData = fundingTrendData;
    }
    
    public void setRegionDistributionData(Map<String, Object> regionDistributionData) {
        this.regionDistributionData = regionDistributionData;
    }
    
    public void setVolunteerActivityData(Map<String, Object> volunteerActivityData) {
        this.volunteerActivityData = volunteerActivityData;
    }
    
    public void setInstitutionTypeData(Map<String, Object> institutionTypeData) {
        this.institutionTypeData = institutionTypeData;
    }
    
    public void setActivityTypeData(Map<String, Object> activityTypeData) {
        this.activityTypeData = activityTypeData;
    }
    
    public void setProjectProgressData(Map<String, Object> projectProgressData) {
        this.projectProgressData = projectProgressData;
    }
    
    public void setBudgetExecutionData(Map<String, Object> budgetExecutionData) {
        this.budgetExecutionData = budgetExecutionData;
    }
}

/**
 * 增强统计数据传输对象
 * 包含趋势分析和对比数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class CharityEnhancedStatsDTO {
    
    /**
     * 基础统计数据
     */
    private CharityStatsDTO basicStats;
    
    /**
     * 同比增长数据
     */
    private GrowthData yearOverYearGrowth;
    
    /**
     * 环比增长数据
     */
    private GrowthData monthOverMonthGrowth;
    
    /**
     * 趋势分析数据
     */
    private TrendAnalysisData trendAnalysis;
    
    /**
     * 增长数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GrowthData {
        /**
         * 受益人数增长率
         */
        private Double beneficiariesGrowthRate;
        
        /**
         * 机构数量增长率
         */
        private Double institutionsGrowthRate;
        
        /**
         * 志愿者数量增长率
         */
        private Double volunteersGrowthRate;
        
        /**
         * 筹集金额增长率
         */
        private Double raisedAmountGrowthRate;
        
        /**
         * 捐赠金额增长率
         */
        private Double donatedAmountGrowthRate;
        
        /**
         * 项目数量增长率
         */
        private Double projectsGrowthRate;
        
        /**
         * 活动数量增长率
         */
        private Double activitiesGrowthRate;
    }
    
    /**
     * 趋势分析数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendAnalysisData {
        /**
         * 整体趋势（上升、下降、平稳）
         */
        private String overallTrend;
        
        /**
         * 关键指标趋势
         */
        private List<KeyIndicatorTrend> keyIndicatorTrends;
        
        /**
         * 预测数据
         */
        private ForecastData forecast;
    }
    
    /**
     * 关键指标趋势
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KeyIndicatorTrend {
        /**
         * 指标名称
         */
        private String indicatorName;
        
        /**
         * 趋势方向（up、down、stable）
         */
        private String trendDirection;
        
        /**
         * 变化幅度
         */
        private Double changeRate;
        
        /**
         * 趋势描述
         */
        private String description;
    }
    
    /**
     * 预测数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ForecastData {
        /**
         * 下月预计受益人数
         */
        private Integer nextMonthBeneficiaries;
        
        /**
         * 下月预计筹集金额
         */
        private BigDecimal nextMonthRaisedAmount;
        
        /**
         * 下月预计活动数量
         */
        private Integer nextMonthActivities;
        
        /**
         * 预测准确度
         */
        private Double accuracy;
    }
}