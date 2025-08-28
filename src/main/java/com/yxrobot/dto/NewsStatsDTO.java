package com.yxrobot.dto;

/**
 * 新闻统计数据传输对象
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
public class NewsStatsDTO {
    
    /**
     * 总新闻数
     */
    private Integer totalNews;
    
    /**
     * 已发布新闻数
     */
    private Integer publishedNews;
    
    /**
     * 草稿新闻数
     */
    private Integer draftNews;
    
    /**
     * 已下线新闻数
     */
    private Integer offlineNews;
    
    /**
     * 总浏览量
     */
    private Long totalViews;
    
    /**
     * 总评论数
     */
    private Long totalComments;
    
    /**
     * 总点赞数
     */
    private Long totalLikes;
    
    /**
     * 今日新增新闻数
     */
    private Integer todayNews;
    
    /**
     * 今日浏览量
     */
    private Long todayViews;
    
    /**
     * 本周新增新闻数
     */
    private Integer weekNews;
    
    /**
     * 本月新增新闻数
     */
    private Integer monthNews;
    
    /**
     * 推荐新闻数
     */
    private Integer featuredNews;
    
    // 构造函数
    public NewsStatsDTO() {
    }
    
    public NewsStatsDTO(Integer totalNews, Integer publishedNews, Integer draftNews, Integer offlineNews) {
        this.totalNews = totalNews;
        this.publishedNews = publishedNews;
        this.draftNews = draftNews;
        this.offlineNews = offlineNews;
    }
    
    // Getter和Setter方法
    public Integer getTotalNews() {
        return totalNews;
    }
    
    public void setTotalNews(Integer totalNews) {
        this.totalNews = totalNews;
    }
    
    public Integer getPublishedNews() {
        return publishedNews;
    }
    
    public void setPublishedNews(Integer publishedNews) {
        this.publishedNews = publishedNews;
    }
    
    public Integer getDraftNews() {
        return draftNews;
    }
    
    public void setDraftNews(Integer draftNews) {
        this.draftNews = draftNews;
    }
    
    public Integer getOfflineNews() {
        return offlineNews;
    }
    
    public void setOfflineNews(Integer offlineNews) {
        this.offlineNews = offlineNews;
    }
    
    public Long getTotalViews() {
        return totalViews;
    }
    
    public void setTotalViews(Long totalViews) {
        this.totalViews = totalViews;
    }
    
    public Long getTotalComments() {
        return totalComments;
    }
    
    public void setTotalComments(Long totalComments) {
        this.totalComments = totalComments;
    }
    
    public Long getTotalLikes() {
        return totalLikes;
    }
    
    public void setTotalLikes(Long totalLikes) {
        this.totalLikes = totalLikes;
    }
    
    public Integer getTodayNews() {
        return todayNews;
    }
    
    public void setTodayNews(Integer todayNews) {
        this.todayNews = todayNews;
    }
    
    public Long getTodayViews() {
        return todayViews;
    }
    
    public void setTodayViews(Long todayViews) {
        this.todayViews = todayViews;
    }
    
    public Integer getWeekNews() {
        return weekNews;
    }
    
    public void setWeekNews(Integer weekNews) {
        this.weekNews = weekNews;
    }
    
    public Integer getMonthNews() {
        return monthNews;
    }
    
    public void setMonthNews(Integer monthNews) {
        this.monthNews = monthNews;
    }
    
    public Integer getFeaturedNews() {
        return featuredNews;
    }
    
    public void setFeaturedNews(Integer featuredNews) {
        this.featuredNews = featuredNews;
    }
    
    /**
     * 计算发布率
     */
    public Double getPublishRate() {
        if (totalNews == null || totalNews == 0) {
            return 0.0;
        }
        return (publishedNews != null ? publishedNews : 0) * 100.0 / totalNews;
    }
    
    /**
     * 计算平均浏览量
     */
    public Double getAverageViews() {
        if (publishedNews == null || publishedNews == 0) {
            return 0.0;
        }
        return (totalViews != null ? totalViews : 0) * 1.0 / publishedNews;
    }
}