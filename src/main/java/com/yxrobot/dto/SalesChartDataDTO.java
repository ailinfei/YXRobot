package com.yxrobot.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 销售图表数据传输对象
 * 用于前端图表展示的数据传输
 * 
 * 字段映射规范：
 * - 与前端图表组件保持一致的camelCase命名
 * - 用于API接口的图表数据传输
 */
public class SalesChartDataDTO {
    
    /**
     * 图表类型
     */
    private String chartType;
    
    /**
     * 图表标题
     */
    private String title;
    
    /**
     * 数据更新时间
     */
    private LocalDate updateDate;
    
    /**
     * 趋势图数据
     */
    private TrendData trendData;
    
    /**
     * 分布图数据
     */
    private DistributionData distributionData;
    
    /**
     * 柱状图数据
     */
    private BarData barData;
    
    /**
     * 漏斗图数据
     */
    private FunnelData funnelData;
    
    /**
     * 趋势图数据结构
     */
    public static class TrendData {
        private List<String> dates;
        private List<BigDecimal> salesAmounts;
        private List<Integer> orderCounts;
        private List<BigDecimal> avgOrderAmounts;
        
        // Getter和Setter方法
        public List<String> getDates() {
            return dates;
        }
        
        public void setDates(List<String> dates) {
            this.dates = dates;
        }
        
        public List<BigDecimal> getSalesAmounts() {
            return salesAmounts;
        }
        
        public void setSalesAmounts(List<BigDecimal> salesAmounts) {
            this.salesAmounts = salesAmounts;
        }
        
        public List<Integer> getOrderCounts() {
            return orderCounts;
        }
        
        public void setOrderCounts(List<Integer> orderCounts) {
            this.orderCounts = orderCounts;
        }
        
        public List<BigDecimal> getAvgOrderAmounts() {
            return avgOrderAmounts;
        }
        
        public void setAvgOrderAmounts(List<BigDecimal> avgOrderAmounts) {
            this.avgOrderAmounts = avgOrderAmounts;
        }
    }
    
    /**
     * 分布图数据结构
     */
    public static class DistributionData {
        private List<PieItem> regionDistribution;
        private List<PieItem> channelDistribution;
        private List<PieItem> productDistribution;
        private List<PieItem> staffDistribution;
        
        public static class PieItem {
            private String name;
            private BigDecimal value;
            private String percentage;
            
            public PieItem() {}
            
            public PieItem(String name, BigDecimal value, String percentage) {
                this.name = name;
                this.value = value;
                this.percentage = percentage;
            }
            
            // Getter和Setter方法
            public String getName() {
                return name;
            }
            
            public void setName(String name) {
                this.name = name;
            }
            
            public BigDecimal getValue() {
                return value;
            }
            
            public void setValue(BigDecimal value) {
                this.value = value;
            }
            
            public String getPercentage() {
                return percentage;
            }
            
            public void setPercentage(String percentage) {
                this.percentage = percentage;
            }
        }
        
        // Getter和Setter方法
        public List<PieItem> getRegionDistribution() {
            return regionDistribution;
        }
        
        public void setRegionDistribution(List<PieItem> regionDistribution) {
            this.regionDistribution = regionDistribution;
        }
        
        public List<PieItem> getChannelDistribution() {
            return channelDistribution;
        }
        
        public void setChannelDistribution(List<PieItem> channelDistribution) {
            this.channelDistribution = channelDistribution;
        }
        
        public List<PieItem> getProductDistribution() {
            return productDistribution;
        }
        
        public void setProductDistribution(List<PieItem> productDistribution) {
            this.productDistribution = productDistribution;
        }
        
        public List<PieItem> getStaffDistribution() {
            return staffDistribution;
        }
        
        public void setStaffDistribution(List<PieItem> staffDistribution) {
            this.staffDistribution = staffDistribution;
        }
    }
    
    /**
     * 柱状图数据结构
     */
    public static class BarData {
        private List<String> categories;
        private List<BarSeries> series;
        
        public static class BarSeries {
            private String name;
            private List<BigDecimal> data;
            
            public BarSeries() {}
            
            public BarSeries(String name, List<BigDecimal> data) {
                this.name = name;
                this.data = data;
            }
            
            // Getter和Setter方法
            public String getName() {
                return name;
            }
            
            public void setName(String name) {
                this.name = name;
            }
            
            public List<BigDecimal> getData() {
                return data;
            }
            
            public void setData(List<BigDecimal> data) {
                this.data = data;
            }
        }
        
        // Getter和Setter方法
        public List<String> getCategories() {
            return categories;
        }
        
        public void setCategories(List<String> categories) {
            this.categories = categories;
        }
        
        public List<BarSeries> getSeries() {
            return series;
        }
        
        public void setSeries(List<BarSeries> series) {
            this.series = series;
        }
    }
    
    /**
     * 漏斗图数据结构
     */
    public static class FunnelData {
        private List<FunnelItem> items;
        
        public static class FunnelItem {
            private String name;
            private BigDecimal value;
            private String conversionRate;
            
            public FunnelItem() {}
            
            public FunnelItem(String name, BigDecimal value, String conversionRate) {
                this.name = name;
                this.value = value;
                this.conversionRate = conversionRate;
            }
            
            // Getter和Setter方法
            public String getName() {
                return name;
            }
            
            public void setName(String name) {
                this.name = name;
            }
            
            public BigDecimal getValue() {
                return value;
            }
            
            public void setValue(BigDecimal value) {
                this.value = value;
            }
            
            public String getConversionRate() {
                return conversionRate;
            }
            
            public void setConversionRate(String conversionRate) {
                this.conversionRate = conversionRate;
            }
        }
        
        // Getter和Setter方法
        public List<FunnelItem> getItems() {
            return items;
        }
        
        public void setItems(List<FunnelItem> items) {
            this.items = items;
        }
    }
    
    // 默认构造函数
    public SalesChartDataDTO() {
        this.updateDate = LocalDate.now();
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
    
    public LocalDate getUpdateDate() {
        return updateDate;
    }
    
    public void setUpdateDate(LocalDate updateDate) {
        this.updateDate = updateDate;
    }
    
    public TrendData getTrendData() {
        return trendData;
    }
    
    public void setTrendData(TrendData trendData) {
        this.trendData = trendData;
    }
    
    public DistributionData getDistributionData() {
        return distributionData;
    }
    
    public void setDistributionData(DistributionData distributionData) {
        this.distributionData = distributionData;
    }
    
    public BarData getBarData() {
        return barData;
    }
    
    public void setBarData(BarData barData) {
        this.barData = barData;
    }
    
    public FunnelData getFunnelData() {
        return funnelData;
    }
    
    public void setFunnelData(FunnelData funnelData) {
        this.funnelData = funnelData;
    }
    
    @Override
    public String toString() {
        return "SalesChartDataDTO{" +
                "chartType='" + chartType + '\'' +
                ", title='" + title + '\'' +
                ", updateDate=" + updateDate +
                ", trendData=" + trendData +
                ", distributionData=" + distributionData +
                ", barData=" + barData +
                ", funnelData=" + funnelData +
                '}';
    }
}