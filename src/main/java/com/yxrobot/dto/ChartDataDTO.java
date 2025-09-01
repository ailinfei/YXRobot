package com.yxrobot.dto;

import java.util.List;
import java.util.Map;

/**
 * 图表数据传输对象
 * 用于封装ECharts格式的图表数据
 * 
 * @author Kiro
 * @date 2025-01-28
 */
public class ChartDataDTO {
    
    /**
     * X轴数据（类目轴数据）
     */
    private List<String> categories;
    
    /**
     * 系列数据
     */
    private List<SeriesData> series;
    
    /**
     * 图表标题
     */
    private String title;
    
    /**
     * 图表副标题
     */
    private String subtitle;
    
    /**
     * 扩展数据
     */
    private Map<String, Object> extra;
    
    public ChartDataDTO() {}
    
    public ChartDataDTO(List<String> categories, List<SeriesData> series) {
        this.categories = categories;
        this.series = series;
    }
    
    public ChartDataDTO(List<String> categories, List<SeriesData> series, String title) {
        this.categories = categories;
        this.series = series;
        this.title = title;
    }
    
    // Getters and Setters
    public List<String> getCategories() {
        return categories;
    }
    
    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
    
    public List<SeriesData> getSeries() {
        return series;
    }
    
    public void setSeries(List<SeriesData> series) {
        this.series = series;
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
    
    public Map<String, Object> getExtra() {
        return extra;
    }
    
    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }
    
    /**
     * 系列数据内部类
     */
    public static class SeriesData {
        /**
         * 系列名称
         */
        private String name;
        
        /**
         * 系列数据
         */
        private List<Object> data;
        
        /**
         * 系列类型（line、bar、pie等）
         */
        private String type;
        
        /**
         * Y轴索引（用于双Y轴图表）
         */
        private Integer yAxisIndex;
        
        /**
         * 系列颜色
         */
        private String color;
        
        public SeriesData() {}
        
        public SeriesData(String name, List<Object> data) {
            this.name = name;
            this.data = data;
        }
        
        public SeriesData(String name, List<Object> data, String type) {
            this.name = name;
            this.data = data;
            this.type = type;
        }
        
        // Getters and Setters
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public List<Object> getData() {
            return data;
        }
        
        public void setData(List<Object> data) {
            this.data = data;
        }
        
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public Integer getYAxisIndex() {
            return yAxisIndex;
        }
        
        public void setYAxisIndex(Integer yAxisIndex) {
            this.yAxisIndex = yAxisIndex;
        }
        
        public String getColor() {
            return color;
        }
        
        public void setColor(String color) {
            this.color = color;
        }
    }
}