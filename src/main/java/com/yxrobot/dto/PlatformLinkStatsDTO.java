package com.yxrobot.dto;

import java.util.List;

/**
 * 平台链接统计数据传输对象
 * 用于返回平台链接统计信息给前端
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-22
 */
public class PlatformLinkStatsDTO {
    
    /**
     * 总链接数
     */
    private Integer totalLinks;
    
    /**
     * 活跃链接数
     */
    private Integer activeLinks;
    
    /**
     * 非活跃链接数
     */
    private Integer inactiveLinks;
    
    /**
     * 总点击量
     */
    private Long totalClicks;
    
    /**
     * 总转化量
     */
    private Long totalConversions;
    
    /**
     * 转化率
     */
    private Double conversionRate;
    
    /**
     * 表现最佳的链接
     */
    private List<TopPerformingLinkDTO> topPerformingLinks;
    
    /**
     * 地区统计数据
     */
    private List<RegionStatsDTO> regionStats;
    
    /**
     * 语言统计数据
     */
    private List<LanguageStatsDTO> languageStats;
    
    // 默认构造函数
    public PlatformLinkStatsDTO() {
    }
    
    // Getters and Setters
    public Integer getTotalLinks() {
        return totalLinks;
    }
    
    public void setTotalLinks(Integer totalLinks) {
        this.totalLinks = totalLinks;
    }
    
    public Integer getActiveLinks() {
        return activeLinks;
    }
    
    public void setActiveLinks(Integer activeLinks) {
        this.activeLinks = activeLinks;
    }
    
    public Integer getInactiveLinks() {
        return inactiveLinks;
    }
    
    public void setInactiveLinks(Integer inactiveLinks) {
        this.inactiveLinks = inactiveLinks;
    }
    
    public Long getTotalClicks() {
        return totalClicks;
    }
    
    public void setTotalClicks(Long totalClicks) {
        this.totalClicks = totalClicks;
    }
    
    public Long getTotalConversions() {
        return totalConversions;
    }
    
    public void setTotalConversions(Long totalConversions) {
        this.totalConversions = totalConversions;
    }
    
    public Double getConversionRate() {
        return conversionRate;
    }
    
    public void setConversionRate(Double conversionRate) {
        this.conversionRate = conversionRate;
    }
    
    public List<TopPerformingLinkDTO> getTopPerformingLinks() {
        return topPerformingLinks;
    }
    
    public void setTopPerformingLinks(List<TopPerformingLinkDTO> topPerformingLinks) {
        this.topPerformingLinks = topPerformingLinks;
    }
    
    public List<RegionStatsDTO> getRegionStats() {
        return regionStats;
    }
    
    public void setRegionStats(List<RegionStatsDTO> regionStats) {
        this.regionStats = regionStats;
    }
    
    public List<LanguageStatsDTO> getLanguageStats() {
        return languageStats;
    }
    
    public void setLanguageStats(List<LanguageStatsDTO> languageStats) {
        this.languageStats = languageStats;
    }
    
    /**
     * 表现最佳链接DTO
     */
    public static class TopPerformingLinkDTO {
        private Long id;
        private String platformName;
        private String region;
        private Integer clickCount;
        private Integer conversionCount;
        private Double conversionRate;
        
        // 构造函数
        public TopPerformingLinkDTO() {
        }
        
        public TopPerformingLinkDTO(Long id, String platformName, String region, Integer clickCount, Integer conversionCount) {
            this.id = id;
            this.platformName = platformName;
            this.region = region;
            this.clickCount = clickCount;
            this.conversionCount = conversionCount;
            this.conversionRate = calculateConversionRate(clickCount, conversionCount);
        }
        
        // Getters and Setters
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public String getPlatformName() {
            return platformName;
        }
        
        public void setPlatformName(String platformName) {
            this.platformName = platformName;
        }
        
        public String getRegion() {
            return region;
        }
        
        public void setRegion(String region) {
            this.region = region;
        }
        
        public Integer getClickCount() {
            return clickCount;
        }
        
        public void setClickCount(Integer clickCount) {
            this.clickCount = clickCount;
        }
        
        public Integer getConversionCount() {
            return conversionCount;
        }
        
        public void setConversionCount(Integer conversionCount) {
            this.conversionCount = conversionCount;
        }
        
        public Double getConversionRate() {
            return conversionRate;
        }
        
        public void setConversionRate(Double conversionRate) {
            this.conversionRate = conversionRate;
        }
    }
    
    /**
     * 地区统计DTO
     */
    public static class RegionStatsDTO {
        private String region;
        private Integer linkCount;
        private Long clickCount;
        private Long conversionCount;
        
        // 构造函数
        public RegionStatsDTO() {
        }
        
        public RegionStatsDTO(String region, Integer linkCount, Long clickCount, Long conversionCount) {
            this.region = region;
            this.linkCount = linkCount;
            this.clickCount = clickCount;
            this.conversionCount = conversionCount;
        }
        
        // Getters and Setters
        public String getRegion() {
            return region;
        }
        
        public void setRegion(String region) {
            this.region = region;
        }
        
        public Integer getLinkCount() {
            return linkCount;
        }
        
        public void setLinkCount(Integer linkCount) {
            this.linkCount = linkCount;
        }
        
        public Long getClickCount() {
            return clickCount;
        }
        
        public void setClickCount(Long clickCount) {
            this.clickCount = clickCount;
        }
        
        public Long getConversionCount() {
            return conversionCount;
        }
        
        public void setConversionCount(Long conversionCount) {
            this.conversionCount = conversionCount;
        }
    }
    
    /**
     * 语言统计DTO
     */
    public static class LanguageStatsDTO {
        private String languageCode;
        private String languageName;
        private Integer linkCount;
        private Long clickCount;
        private Long conversionCount;
        
        // 构造函数
        public LanguageStatsDTO() {
        }
        
        public LanguageStatsDTO(String languageCode, String languageName, Integer linkCount, Long clickCount, Long conversionCount) {
            this.languageCode = languageCode;
            this.languageName = languageName;
            this.linkCount = linkCount;
            this.clickCount = clickCount;
            this.conversionCount = conversionCount;
        }
        
        // Getters and Setters
        public String getLanguageCode() {
            return languageCode;
        }
        
        public void setLanguageCode(String languageCode) {
            this.languageCode = languageCode;
        }
        
        public String getLanguageName() {
            return languageName;
        }
        
        public void setLanguageName(String languageName) {
            this.languageName = languageName;
        }
        
        public Integer getLinkCount() {
            return linkCount;
        }
        
        public void setLinkCount(Integer linkCount) {
            this.linkCount = linkCount;
        }
        
        public Long getClickCount() {
            return clickCount;
        }
        
        public void setClickCount(Long clickCount) {
            this.clickCount = clickCount;
        }
        
        public Long getConversionCount() {
            return conversionCount;
        }
        
        public void setConversionCount(Long conversionCount) {
            this.conversionCount = conversionCount;
        }
    }
    
    /**
     * 计算转化率
     * @param clickCount 点击量
     * @param conversionCount 转化量
     * @return 转化率（百分比）
     */
    private static Double calculateConversionRate(Integer clickCount, Integer conversionCount) {
        if (clickCount == null || clickCount == 0) {
            return 0.0;
        }
        if (conversionCount == null) {
            return 0.0;
        }
        return (conversionCount.doubleValue() / clickCount.doubleValue()) * 100;
    }
}