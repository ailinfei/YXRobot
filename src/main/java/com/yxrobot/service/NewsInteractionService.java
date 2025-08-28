package com.yxrobot.service;

import com.yxrobot.entity.InteractionType;
import com.yxrobot.entity.NewsInteraction;
import com.yxrobot.exception.NewsValidationException;
import com.yxrobot.exception.NewsOperationException;
import com.yxrobot.exception.NewsNotFoundException;
import com.yxrobot.mapper.NewsInteractionMapper;
import com.yxrobot.mapper.NewsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 新闻互动数据跟踪服务类
 * 负责处理新闻互动数据的记录和统计
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@Service
@Transactional
public class NewsInteractionService {
    
    private static final Logger logger = LoggerFactory.getLogger(NewsInteractionService.class);
    
    @Autowired
    private NewsInteractionMapper newsInteractionMapper;
    
    @Autowired
    private NewsMapper newsMapper;
    
    /**
     * 记录新闻浏览事件
     * 
     * @param newsId 新闻ID
     * @param userId 用户ID（可为空）
     * @param ipAddress IP地址
     * @param userAgent 用户代理
     */
    public void recordView(Long newsId, Long userId, String ipAddress, String userAgent) {
        logger.debug("记录新闻浏览 - 新闻ID: {}, 用户ID: {}, IP: {}", newsId, userId, ipAddress);
        
        recordInteraction(newsId, InteractionType.VIEW, userId, ipAddress, userAgent);
        
        // 更新新闻浏览量
        updateNewsViewCount(newsId);
    }
    
    /**
     * 记录新闻点赞事件
     * 
     * @param newsId 新闻ID
     * @param userId 用户ID（可为空）
     * @param ipAddress IP地址
     * @param userAgent 用户代理
     */
    public void recordLike(Long newsId, Long userId, String ipAddress, String userAgent) {
        logger.info("记录新闻点赞 - 新闻ID: {}, 用户ID: {}", newsId, userId);
        
        // 检查是否已经点赞过（防止重复点赞）
        if (hasUserInteracted(newsId, InteractionType.LIKE, userId, ipAddress)) {
            throw new NewsValidationException("interaction", "duplicate", "用户已经点赞过该新闻");
        }
        
        recordInteraction(newsId, InteractionType.LIKE, userId, ipAddress, userAgent);
        
        // 更新新闻点赞数
        updateNewsLikeCount(newsId);
    }
    
    /**
     * 记录新闻评论事件
     * 
     * @param newsId 新闻ID
     * @param userId 用户ID（可为空）
     * @param ipAddress IP地址
     * @param userAgent 用户代理
     */
    public void recordComment(Long newsId, Long userId, String ipAddress, String userAgent) {
        logger.info("记录新闻评论 - 新闻ID: {}, 用户ID: {}", newsId, userId);
        
        recordInteraction(newsId, InteractionType.COMMENT, userId, ipAddress, userAgent);
        
        // 更新新闻评论数
        updateNewsCommentCount(newsId);
    }
    
    /**
     * 记录新闻分享事件
     * 
     * @param newsId 新闻ID
     * @param userId 用户ID（可为空）
     * @param ipAddress IP地址
     * @param userAgent 用户代理
     */
    public void recordShare(Long newsId, Long userId, String ipAddress, String userAgent) {
        logger.info("记录新闻分享 - 新闻ID: {}, 用户ID: {}", newsId, userId);
        
        recordInteraction(newsId, InteractionType.SHARE, userId, ipAddress, userAgent);
    }
    
    /**
     * 获取新闻互动统计
     * 
     * @param newsId 新闻ID
     * @return 互动统计信息
     */
    public Map<String, Object> getInteractionStats(Long newsId) {
        logger.info("获取新闻互动统计 - 新闻ID: {}", newsId);
        
        if (newsId == null) {
            throw new NewsValidationException("newsId", newsId, "新闻ID不能为空");
        }
        
        // 验证新闻是否存在
        if (newsMapper.selectById(newsId) == null) {
            throw new NewsNotFoundException(newsId);
        }
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("views", newsInteractionMapper.countByNewsIdAndType(newsId, InteractionType.VIEW));
        stats.put("likes", newsInteractionMapper.countByNewsIdAndType(newsId, InteractionType.LIKE));
        stats.put("comments", newsInteractionMapper.countByNewsIdAndType(newsId, InteractionType.COMMENT));
        stats.put("shares", newsInteractionMapper.countByNewsIdAndType(newsId, InteractionType.SHARE));
        
        logger.info("获取新闻互动统计完成 - 新闻ID: {}", newsId);
        return stats;
    }
    
    /**
     * 获取用户互动历史
     * 
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 互动历史
     */
    public Map<String, Object> getUserInteractionHistory(Long userId, int page, int pageSize) {
        logger.info("获取用户互动历史 - 用户ID: {}, 页码: {}, 每页大小: {}", userId, page, pageSize);
        
        if (userId == null) {
            throw new NewsValidationException("userId", userId, "用户ID不能为空");
        }
        
        // 参数验证
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 10;
        
        int offset = (page - 1) * pageSize;
        
        List<NewsInteraction> interactions = newsInteractionMapper.selectByUserId(userId, offset, pageSize);
        int total = newsInteractionMapper.countByUserId(userId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", interactions);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        result.put("totalPages", (int) Math.ceil((double) total / pageSize));
        
        logger.info("获取用户互动历史完成 - 用户ID: {}, 总数: {}", userId, total);
        return result;
    }
    
    /**
     * 获取时间段内的互动统计
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 时间段统计
     */
    public Map<String, Object> getInteractionStatsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        logger.info("获取时间段互动统计 - 开始: {}, 结束: {}", startTime, endTime);
        
        if (startTime == null || endTime == null) {
            throw new NewsValidationException("timeRange", null, "开始时间和结束时间不能为空");
        }
        
        if (startTime.isAfter(endTime)) {
            throw new NewsValidationException("timeRange", null, "开始时间不能晚于结束时间");
        }
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("views", newsInteractionMapper.countByTypeAndTimeRange(InteractionType.VIEW, startTime, endTime));
        stats.put("likes", newsInteractionMapper.countByTypeAndTimeRange(InteractionType.LIKE, startTime, endTime));
        stats.put("comments", newsInteractionMapper.countByTypeAndTimeRange(InteractionType.COMMENT, startTime, endTime));
        stats.put("shares", newsInteractionMapper.countByTypeAndTimeRange(InteractionType.SHARE, startTime, endTime));
        
        logger.info("获取时间段互动统计完成");
        return stats;
    }
    
    /**
     * 获取热门新闻（基于互动数据）
     * 
     * @param interactionType 互动类型
     * @param limit 数量限制
     * @param days 天数范围
     * @return 热门新闻ID列表
     */
    public List<Long> getPopularNewsByInteraction(InteractionType interactionType, int limit, int days) {
        logger.info("获取热门新闻 - 互动类型: {}, 数量: {}, 天数: {}", interactionType, limit, days);
        
        if (interactionType == null) {
            throw new NewsValidationException("interactionType", interactionType, "互动类型不能为空");
        }
        
        if (limit < 1 || limit > 100) limit = 10;
        if (days < 1 || days > 365) days = 7;
        
        LocalDateTime startTime = LocalDateTime.now().minusDays(days);
        
        List<Map<String, Object>> popularNews = newsInteractionMapper.selectPopularNewsByType(interactionType, startTime, limit);
        List<Long> newsIds = popularNews.stream()
                .map(map -> (Long) map.get("newsId"))
                .collect(Collectors.toList());
        
        logger.info("获取热门新闻完成 - 数量: {}", newsIds.size());
        return newsIds;
    }
    
    /**
     * 获取总体互动统计
     * 
     * @return 总体统计信息
     */
    public Map<String, Object> getTotalInteractionStats() {
        logger.info("获取总体互动统计");
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalViews", newsInteractionMapper.countByType(InteractionType.VIEW));
        stats.put("totalLikes", newsInteractionMapper.countByType(InteractionType.LIKE));
        stats.put("totalComments", newsInteractionMapper.countByType(InteractionType.COMMENT));
        stats.put("totalShares", newsInteractionMapper.countByType(InteractionType.SHARE));
        stats.put("totalInteractions", newsInteractionMapper.countAll());
        
        // 今日统计
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime todayEnd = todayStart.plusDays(1);
        
        stats.put("todayViews", newsInteractionMapper.countByTypeAndTimeRange(InteractionType.VIEW, todayStart, todayEnd));
        stats.put("todayLikes", newsInteractionMapper.countByTypeAndTimeRange(InteractionType.LIKE, todayStart, todayEnd));
        stats.put("todayComments", newsInteractionMapper.countByTypeAndTimeRange(InteractionType.COMMENT, todayStart, todayEnd));
        stats.put("todayShares", newsInteractionMapper.countByTypeAndTimeRange(InteractionType.SHARE, todayStart, todayEnd));
        
        logger.info("获取总体互动统计完成");
        return stats;
    }
    
    /**
     * 清理过期的互动数据
     * 
     * @param days 保留天数
     * @return 清理的记录数
     */
    public int cleanupExpiredInteractions(int days) {
        logger.info("清理过期互动数据 - 保留天数: {}", days);
        
        if (days < 1) {
            throw new NewsValidationException("days", days, "保留天数必须大于0");
        }
        
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(days);
        int deletedCount = newsInteractionMapper.deleteByCreatedAtBefore(cutoffTime);
        
        logger.info("清理过期互动数据完成 - 清理数量: {}", deletedCount);
        return deletedCount;
    }
    
    /**
     * 记录互动事件的通用方法
     * 
     * @param newsId 新闻ID
     * @param interactionType 互动类型
     * @param userId 用户ID
     * @param ipAddress IP地址
     * @param userAgent 用户代理
     */
    private void recordInteraction(Long newsId, InteractionType interactionType, 
                                 Long userId, String ipAddress, String userAgent) {
        if (newsId == null) {
            throw new NewsValidationException("newsId", newsId, "新闻ID不能为空");
        }
        
        if (interactionType == null) {
            throw new NewsValidationException("interactionType", interactionType, "互动类型不能为空");
        }
        
        // 验证新闻是否存在
        if (newsMapper.selectById(newsId) == null) {
            throw new NewsNotFoundException(newsId);
        }
        
        // 创建互动记录
        NewsInteraction interaction = new NewsInteraction();
        interaction.setNewsId(newsId);
        interaction.setInteractionType(interactionType);
        interaction.setUserId(userId);
        interaction.setIpAddress(ipAddress);
        interaction.setUserAgent(userAgent);
        interaction.setCreatedAt(LocalDateTime.now());
        
        int result = newsInteractionMapper.insert(interaction);
        if (result <= 0) {
            throw new NewsOperationException("记录互动事件", newsId, "数据库插入失败");
        }
        
        logger.debug("记录互动事件成功 - 新闻ID: {}, 类型: {}", newsId, interactionType);
    }
    
    /**
     * 检查用户是否已经进行过某种互动
     * 
     * @param newsId 新闻ID
     * @param interactionType 互动类型
     * @param userId 用户ID
     * @param ipAddress IP地址
     * @return 是否已互动
     */
    private boolean hasUserInteracted(Long newsId, InteractionType interactionType, Long userId, String ipAddress) {
        if (userId != null) {
            // 如果有用户ID，按用户ID检查
            return newsInteractionMapper.existsByNewsIdAndTypeAndUserId(newsId, interactionType, userId);
        } else if (StringUtils.hasText(ipAddress)) {
            // 如果没有用户ID但有IP地址，按IP地址检查
            return newsInteractionMapper.existsByNewsIdAndTypeAndIp(newsId, interactionType, ipAddress);
        }
        
        return false;
    }
    
    /**
     * 更新新闻浏览量
     * 
     * @param newsId 新闻ID
     */
    private void updateNewsViewCount(Long newsId) {
        try {
            newsMapper.incrementViews(newsId);
        } catch (Exception e) {
            logger.warn("更新新闻浏览量失败 - 新闻ID: {}, 错误: {}", newsId, e.getMessage());
        }
    }
    
    /**
     * 更新新闻点赞数
     * 
     * @param newsId 新闻ID
     */
    private void updateNewsLikeCount(Long newsId) {
        try {
            newsMapper.incrementLikes(newsId);
        } catch (Exception e) {
            logger.warn("更新新闻点赞数失败 - 新闻ID: {}, 错误: {}", newsId, e.getMessage());
        }
    }
    
    /**
     * 更新新闻评论数
     * 
     * @param newsId 新闻ID
     */
    private void updateNewsCommentCount(Long newsId) {
        try {
            newsMapper.incrementComments(newsId);
        } catch (Exception e) {
            logger.warn("更新新闻评论数失败 - 新闻ID: {}, 错误: {}", newsId, e.getMessage());
        }
    }
}