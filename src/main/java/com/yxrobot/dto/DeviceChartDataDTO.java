package com.yxrobot.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * 设备图表数据DTO类
 * 适配前端DeviceMonitoring.vue页面的性能图表和网络图表数据需求
 * 确保字段名称与前端TypeScript接口完全匹配（camelCase）
 * 
 * @author YXRobot Development Team
 * @since 2024
 */
public class DeviceChartDataDTO {
    
    // 图表基本信息
    private String chartType;           // 图表类型（performance、network、alert_trend等）
    private String title;               // 图表标题
    private String subtitle;            // 图表副标题
    private String timeRange;           // 时间范围
    
    // 图表数据
    private List<String> labels;        // X轴标签（时间点）
    private List<ChartSeriesDTO> series; // 数据系列
    
    // 图表配置
    private ChartConfigDTO config;      // 图表配置
    
    // 统计信息
    private ChartStatisticsDTO statistics; // 图表统计信息
    
    /**
     * 图表数据系列DTO
     */
    public static class ChartSeriesDTO {
        private String name;            // 系列名称（如：CPU使用率、内存使用率）
        private String type;            // 系列类型（line、bar、area等）
        private String color;           // 系列颜色
        private List<BigDecimal> data;  // 数据点
        private String unit;            // 数据单位（%、MB、ms等）
        
        // 构造函数
        public ChartSeriesDTO() {}
        
        public ChartSeriesDTO(String name, String type, String color, List<BigDecimal> data, String unit) {
            this.name = name;
            this.type = type;
            this.color = color;
            this.data = data;
            this.unit = unit;
        }
        
        // Getter和Setter方法
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public String getColor() {
            return color;
        }
        
        public void setColor(String color) {
            this.color = color;
        }
        
        public List<BigDecimal> getData() {
            return data;
        }
        
        public void setData(List<BigDecimal> data) {
            this.data = data;
        }
        
        public String getUnit() {
            return unit;
        }
        
        public void setUnit(String unit) {
            this.unit = unit;
        }
    }
    
    /**
     * 图表配置DTO
     */
    public static class ChartConfigDTO {
        private String xAxisTitle;      // X轴标题
        private String yAxisTitle;      // Y轴标题
        private Boolean showLegend;     // 是否显示图例
        private Boolean showGrid;       // 是否显示网格
        private Boolean enableZoom;     // 是否启用缩放
        private Integer height;         // 图表高度
        private String theme;           // 图表主题
        
        // 构造函数
        public ChartConfigDTO() {}
        
        // Getter和Setter方法
        public String getxAxisTitle() {
            return xAxisTitle;
        }
        
        public void setxAxisTitle(String xAxisTitle) {
            this.xAxisTitle = xAxisTitle;
        }
        
        public String getyAxisTitle() {
            return yAxisTitle;
        }
        
        public void setyAxisTitle(String yAxisTitle) {
            this.yAxisTitle = yAxisTitle;
        }
        
        public Boolean getShowLegend() {
            return showLegend;
        }
        
        public void setShowLegend(Boolean showLegend) {
            this.showLegend = showLegend;
        }
        
        public Boolean getShowGrid() {
            return showGrid;
        }
        
        public void setShowGrid(Boolean showGrid) {
            this.showGrid = showGrid;
        }
        
        public Boolean getEnableZoom() {
            return enableZoom;
        }
        
        public void setEnableZoom(Boolean enableZoom) {
            this.enableZoom = enableZoom;
        }
        
        public Integer getHeight() {
            return height;
        }
        
        public void setHeight(Integer height) {
            this.height = height;
        }
        
        public String getTheme() {
            return theme;
        }
        
        public void setTheme(String theme) {
            this.theme = theme;
        }
    }
    
    /**
     * 图表统计信息DTO
     */
    public static class ChartStatisticsDTO {
        private BigDecimal maxValue;    // 最大值
        private BigDecimal minValue;    // 最小值
        private BigDecimal avgValue;    // 平均值
        private BigDecimal currentValue; // 当前值
        private String trend;           // 趋势（up、down、stable）
        private String trendDescription; // 趋势描述
        
        // 构造函数
        public ChartStatisticsDTO() {}
        
        // Getter和Setter方法
        public BigDecimal getMaxValue() {
            return maxValue;
        }
        
        public void setMaxValue(BigDecimal maxValue) {
            this.maxValue = maxValue;
        }
        
        public BigDecimal getMinValue() {
            return minValue;
        }
        
        public void setMinValue(BigDecimal minValue) {
            this.minValue = minValue;
        }
        
        public BigDecimal getAvgValue() {
            return avgValue;
        }
        
        public void setAvgValue(BigDecimal avgValue) {
            this.avgValue = avgValue;
        }
        
        public BigDecimal getCurrentValue() {
            return currentValue;
        }
        
        public void setCurrentValue(BigDecimal currentValue) {
            this.currentValue = currentValue;
        }
        
        public String getTrend() {
            return trend;
        }
        
        public void setTrend(String trend) {
            this.trend = trend;
        }
        
        public String getTrendDescription() {
            return trendDescription;
        }
        
        public void setTrendDescription(String trendDescription) {
            this.trendDescription = trendDescription;
        }
    }
    
    // 构造函数
    public DeviceChartDataDTO() {}
    
    public DeviceChartDataDTO(String chartType, String title, List<String> labels, List<ChartSeriesDTO> series) {
        this.chartType = chartType;
        this.title = title;
        this.labels = labels;
        this.series = series;
    }
    
    // Getter和Setter方法
    public String getChartType() {
        return chartType;
    }
    
    public void setChartType(String chartType) {
        this.chartType = chartType;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getSubtitle() {
        return subtitle;
    }
    
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }
    
    public String getTimeRange() {
        return timeRange;
    }
    
    public void setTimeRange(String timeRange) {
        this.timeRange = timeRange;
    }
    
    public List<String> getLabels() {
        return labels;
    }
    
    public void setLabels(List<String> labels) {
        this.labels = labels;
    }
    
    public List<ChartSeriesDTO> getSeries() {
        return series;
    }
    
    public void setSeries(List<ChartSeriesDTO> series) {
        this.series = series;
    }
    
    public ChartConfigDTO getConfig() {
        return config;
    }
    
    public void setConfig(ChartConfigDTO config) {
        this.config = config;
    }
    
    public ChartStatisticsDTO getStatistics() {
        return statistics;
    }
    
    public void setStatistics(ChartStatisticsDTO statistics) {
        this.statistics = statistics;
    }
    
    @Override
    public String toString() {
        return "DeviceChartDataDTO{" +
                "chartType='" + chartType + '\'' +
                ", title='" + title + '\'' +
                ", timeRange='" + timeRange + '\'' +
                ", seriesCount=" + (series != null ? series.size() : 0) +
                ", labelsCount=" + (labels != null ? labels.size() : 0) +
                '}';
    }
}