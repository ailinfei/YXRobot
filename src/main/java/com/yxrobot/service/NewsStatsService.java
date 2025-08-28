package com.yxrobot.service;

import com.yxrobot.dto.NewsStatsDTO;
import com.yxrobot.exception.NewsValidationException;
import com.yxrobot.mapper.NewsMapper;
import com.yxrobot.mapper.NewsInteractionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 新闻统计服务类
 * 负责处理新闻统计相关的业务逻辑
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@Service
@Transactional(readOnly = true)
public class NewsStatsService {
    
    private static final Logger logger = LoggerFactory.getLogger(NewsStatsService.class);
    
    @Autowired
    private NewsMapper newsMapper;
    
    @Autowired
    private NewsInteractionMapper newsInteractionMapper;
    
    /**
     * 获取新闻基础统计数据
     * 
     * @return 统计数据
     */
    public NewsStatsDTO getNewsStats() {
        logger.info("获取新闻基础统计数据");
        
        Map<String, Object> statsMap = newsMapper.getNewsStats();
        
        NewsStatsDTO stats = new NewsStatsDTO();
        stats.setTotalNews(getIntValue(statsMap, "total"));
        stats.setPublishedNews(getIntValue(statsMap, "published"));
        stats.setDraftNews(getIntValue(statsMap, "draft"));
        stats.setOfflineNews(getIntValue(statsMap, "offline"));
        stats.setTotalViews(getLongValue(statsMap, "totalViews"));
        stats.setTotalComments(getLongValue(statsMap, "totalComments"));
        stats.setTotalLikes(getLongValue(statsMap, "totalLikes"));
        
        logger.info("获取新闻基础统计数据完成 - 总数: {}, 已发布: {}, 草稿: {}, 已下线: {}", 
                   stats.getTotalNews(), stats.getPublishedNews(), stats.getDraftNews(), stats.getOfflineNews());
        return stats;
    }
    
    /**
     * 获取按日期统计的新闻数据
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 按日期统计的数据
     */
    public List<Map<String, Object>> getNewsStatsByDate(LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("获取按日期统计的新闻数据 - 开始日期: {}, 结束日期: {}", startDate, endDate);
        
        if (startDate == null || endDate == null) {
            throw new NewsValidationException("date", null, "开始日期和结束日期不能为空");
        }
        
        if (startDate.isAfter(endDate)) {
            throw new NewsValidationException("date", startDate + " - " + endDate, "开始日期不能晚于结束日期");
        }
        
        List<Map<String, Object>> stats = newsMapper.getNewsStatsByDate(startDate, endDate);
        
        // 格式化日期字段
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (Map<String, Object> stat : stats) {
            Object dateObj = stat.get("date");
            if (dateObj instanceof LocalDateTime) {
                stat.put("date", ((LocalDateTime) dateObj).format(formatter));
            }
        }
        
        logger.info("获取按日期统计的新闻数据完成 - 数据条数: {}", stats.size());
        return stats;
    }
    
    /**
     * 获取按分类统计的新闻数据
     * 
     * @return 按分类统计的数据
     */
    public List<Map<String, Object>> getNewsStatsByCategory() {
        logger.info("获取按分类统计的新闻数据");
        
        List<Map<String, Object>> stats = newsMapper.getNewsStatsByCategory();
        
        logger.info("获取按分类统计的新闻数据完成 - 分类数: {}", stats.size());
        return stats;
    }
    
    /**
     * 获取按作者统计的新闻数据
     * 
     * @return 按作者统计的数据
     */
    public List<Map<String, Object>> getNewsStatsByAuthor() {
        logger.info("获取按作者统计的新闻数据");
        
        List<Map<String, Object>> stats = newsMapper.getNewsStatsByAuthor();
        
        logger.info("获取按作者统计的新闻数据完成 - 作者数: {}", stats.size());
        return stats;
    }
    
    /**
     * 获取新闻浏览量统计
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 浏览量统计数据
     */
    public List<Map<String, Object>> getNewsViewStats(LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("获取新闻浏览量统计 - 开始日期: {}, 结束日期: {}", startDate, endDate);
        
        if (startDate == null || endDate == null) {
            throw new NewsValidationException("date", null, "开始日期和结束日期不能为空");
        }
        
        if (startDate.isAfter(endDate)) {
            throw new NewsValidationException("date", startDate + " - " + endDate, "开始日期不能晚于结束日期");
        }
        
        List<Map<String, Object>> stats = newsInteractionMapper.getInteractionStatsByDate(startDate, endDate);
        
        // 格式化日期字段
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (Map<String, Object> stat : stats) {
            Object dateObj = stat.get("date");
            if (dateObj instanceof LocalDateTime) {
                stat.put("date", ((LocalDateTime) dateObj).format(formatter));
            }
        }
        
        logger.info("获取新闻浏览量统计完成 - 数据条数: {}", stats.size());
        return stats;
    }
    
    /**
     * 获取新闻互动统计
     * 
     * @param limit 数量限制
     * @return 互动统计数据
     */
    public List<Map<String, Object>> getNewsInteractionStats(int limit) {
        logger.info("获取新闻互动统计 - 数量限制: {}", limit);
        
        if (limit < 1 || limit > 100) limit = 10;
        
        List<Map<String, Object>> stats = newsInteractionMapper.getInteractionStatsByNews(limit);
        
        logger.info("获取新闻互动统计完成 - 数据条数: {}", stats.size());
        return stats;
    }
    
    /**
     * 获取按互动类型统计的数据
     * 
     * @return 按互动类型统计的数据
     */
    public List<Map<String, Object>> getInteractionStatsByType() {
        logger.info("获取按互动类型统计的数据");
        
        List<Map<String, Object>> stats = newsInteractionMapper.getInteractionStatsByType();
        
        logger.info("获取按互动类型统计的数据完成 - 类型数: {}", stats.size());
        return stats;
    }
    
    /**
     * 获取热门新闻排行榜
     * 
     * @param limit 数量限制
     * @return 热门新闻列表
     */
    public Map<String, Object> getHotNewsRanking(int limit) {
        logger.info("获取热门新闻排行榜 - 数量限制: {}", limit);
        
        if (limit < 1 || limit > 50) limit = 10;
        
        Map<String, Object> result = new HashMap<>();
        
        // 按浏览量排行
        List<Map<String, Object>> viewRanking = newsInteractionMapper.getHotNewsByInteractions(
                com.yxrobot.entity.InteractionType.VIEW, limit);
        result.put("viewRanking", viewRanking);
        
        // 按点赞数排行
        List<Map<String, Object>> likeRanking = newsInteractionMapper.getHotNewsByInteractions(
                com.yxrobot.entity.InteractionType.LIKE, limit);
        result.put("likeRanking", likeRanking);
        
        // 按评论数排行
        List<Map<String, Object>> commentRanking = newsInteractionMapper.getHotNewsByInteractions(
                com.yxrobot.entity.InteractionType.COMMENT, limit);
        result.put("commentRanking", commentRanking);
        
        logger.info("获取热门新闻排行榜完成");
        return result;
    }
    
    /**
     * 获取指定新闻的详细统计信息
     * 
     * @param newsId 新闻ID
     * @return 详细统计信息
     */
    public Map<String, Object> getNewsDetailStats(Long newsId) {
        logger.info("获取新闻详细统计信息 - 新闻ID: {}", newsId);
        
        if (newsId == null) {
            throw new NewsValidationException("newsId", newsId, "新闻ID不能为空");
        }
        
        Map<String, Object> stats = newsInteractionMapper.getInteractionStatsByNewsId(newsId);
        
        logger.info("获取新闻详细统计信息完成 - 新闻ID: {}", newsId);
        return stats;
    }
    
    /**
     * 获取综合统计仪表板数据
     * 
     * @return 仪表板数据
     */
    public Map<String, Object> getDashboardStats() {
        logger.info("获取综合统计仪表板数据");
        
        Map<String, Object> dashboard = new HashMap<>();
        
        // 基础统计
        NewsStatsDTO basicStats = getNewsStats();
        dashboard.put("basicStats", basicStats);
        
        // 分类统计
        List<Map<String, Object>> categoryStats = getNewsStatsByCategory();
        dashboard.put("categoryStats", categoryStats);
        
        // 互动类型统计
        List<Map<String, Object>> interactionTypeStats = getInteractionStatsByType();
        dashboard.put("interactionTypeStats", interactionTypeStats);
        
        // 热门新闻排行（前5名）
        Map<String, Object> hotNewsRanking = getHotNewsRanking(5);
        dashboard.put("hotNewsRanking", hotNewsRanking);
        
        // 最近7天的新闻发布趋势
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(7);
        List<Map<String, Object>> recentTrend = getNewsStatsByDate(startDate, endDate);
        dashboard.put("recentTrend", recentTrend);
        
        logger.info("获取综合统计仪表板数据完成");
        return dashboard;
    }
    
    /**
     * 获取实时统计数据
     * 
     * @return 实时统计数据
     */
    public Map<String, Object> getRealTimeStats() {
        logger.info("获取实时统计数据");
        
        Map<String, Object> realTimeStats = new HashMap<>();
        
        // 今日新增新闻数
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime todayEnd = todayStart.plusDays(1);
        
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("startDate", todayStart);
        conditions.put("endDate", todayEnd);
        
        int todayNewsCount = newsMapper.countByConditions(conditions);
        realTimeStats.put("todayNewsCount", todayNewsCount);
        
        // 今日互动总数
        int todayInteractionCount = newsInteractionMapper.countByDateRange(todayStart, todayEnd);
        realTimeStats.put("todayInteractionCount", todayInteractionCount);
        
        // 在线新闻总数
        int onlineNewsCount = newsMapper.countByStatus(com.yxrobot.entity.NewsStatus.PUBLISHED);
        realTimeStats.put("onlineNewsCount", onlineNewsCount);
        
        logger.info("获取实时统计数据完成 - 今日新闻: {}, 今日互动: {}, 在线新闻: {}", 
                   todayNewsCount, todayInteractionCount, onlineNewsCount);
        return realTimeStats;
    }
    
    /**
     * 从Map中安全获取Integer值
     * 
     * @param map Map对象
     * @param key 键名
     * @return Integer值，如果不存在或为null则返回0
     */
    private Integer getIntValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return 0;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return 0;
    }
    
    /**
     * 从Map中安全获取Long值
     * 
     * @param map Map对象
     * @param key 键名
     * @return Long值，如果不存在或为null则返回0L
     */
    private Long getLongValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return 0L;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return 0L;
    }
}