package com.yxrobot.service;

import com.yxrobot.dto.NewsDTO;
import com.yxrobot.entity.News;
import com.yxrobot.entity.NewsStatus;
import com.yxrobot.exception.NewsNotFoundException;
import com.yxrobot.exception.NewsStatusException;
import com.yxrobot.exception.NewsValidationException;
import com.yxrobot.exception.NewsOperationException;
import com.yxrobot.entity.NewsStatusLog;
import com.yxrobot.mapper.NewsMapper;
import com.yxrobot.mapper.NewsStatusLogMapper;
import com.yxrobot.validation.NewsValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 新闻状态管理服务类
 * 负责处理新闻状态转换相关的业务逻辑
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@Service
@Transactional
public class NewsStatusService {
    
    private static final Logger logger = LoggerFactory.getLogger(NewsStatusService.class);
    
    @Autowired
    private NewsMapper newsMapper;
    
    @Autowired
    private NewsService newsService;
    
    @Autowired
    private NewsStatusLogMapper newsStatusLogMapper;
    
    @Autowired
    private NewsValidator newsValidator;
    
    /**
     * 发布新闻
     * 将新闻状态从草稿或下线状态转换为已发布状态
     * 
     * @param newsId 新闻ID
     * @return 更新后的新闻信息
     */
    public NewsDTO publishNews(Long newsId) {
        logger.info("发布新闻 - ID: {}", newsId);
        
        if (newsId == null) {
            throw new NewsValidationException("newsId", newsId, "新闻ID不能为空");
        }
        
        // 获取新闻信息
        News news = newsMapper.selectById(newsId);
        if (news == null) {
            throw new NewsNotFoundException(newsId);
        }
        
        // 验证状态转换是否合法
        validateStatusTransition(news.getStatus(), NewsStatus.PUBLISHED, newsId);
        
        // 验证新闻内容是否完整
        validateNewsForPublish(news);
        
        // 更新状态
        LocalDateTime publishTime = LocalDateTime.now();
        int result = newsMapper.updateStatus(newsId, NewsStatus.PUBLISHED, publishTime);
        if (result <= 0) {
            throw new NewsOperationException("发布新闻", newsId, "数据库更新失败");
        }
        
        // 记录状态变更日志
        recordStatusChangeLog(newsId, news.getStatus(), NewsStatus.PUBLISHED, "发布新闻", "system");
        
        logger.info("发布新闻成功 - ID: {}, 标题: {}", newsId, news.getTitle());
        return newsService.getNewsById(newsId);
    }
    
    /**
     * 下线新闻
     * 将新闻状态从已发布状态转换为下线状态
     * 
     * @param newsId 新闻ID
     * @param reason 下线原因（可选）
     * @return 更新后的新闻信息
     */
    public NewsDTO offlineNews(Long newsId, String reason) {
        logger.info("下线新闻 - ID: {}, 原因: {}", newsId, reason);
        
        if (newsId == null) {
            throw new NewsValidationException("newsId", newsId, "新闻ID不能为空");
        }
        
        // 获取新闻信息
        News news = newsMapper.selectById(newsId);
        if (news == null) {
            throw new NewsNotFoundException(newsId);
        }
        
        // 验证状态转换是否合法
        validateStatusTransition(news.getStatus(), NewsStatus.OFFLINE, newsId);
        
        // 更新状态
        int result = newsMapper.updateStatus(newsId, NewsStatus.OFFLINE, null);
        if (result <= 0) {
            throw new NewsOperationException("下线新闻", newsId, "数据库更新失败");
        }
        
        // 记录状态变更日志
        String logReason = (reason != null && !reason.trim().isEmpty()) ? reason : "下线新闻";
        recordStatusChangeLog(newsId, news.getStatus(), NewsStatus.OFFLINE, logReason, "system");
        
        logger.info("下线新闻成功 - ID: {}, 标题: {}", newsId, news.getTitle());
        return newsService.getNewsById(newsId);
    }
    
    /**
     * 将新闻转为草稿状态
     * 
     * @param newsId 新闻ID
     * @return 更新后的新闻信息
     */
    public NewsDTO draftNews(Long newsId) {
        logger.info("转为草稿 - ID: {}", newsId);
        
        if (newsId == null) {
            throw new NewsValidationException("newsId", newsId, "新闻ID不能为空");
        }
        
        // 获取新闻信息
        News news = newsMapper.selectById(newsId);
        if (news == null) {
            throw new NewsNotFoundException(newsId);
        }
        
        // 验证状态转换是否合法
        validateStatusTransition(news.getStatus(), NewsStatus.DRAFT, newsId);
        
        // 更新状态
        int result = newsMapper.updateStatus(newsId, NewsStatus.DRAFT, LocalDateTime.now());
        if (result <= 0) {
            throw new NewsOperationException("转为草稿", newsId, "数据库更新失败");
        }
        
        // 记录状态变更日志
        recordStatusChangeLog(newsId, news.getStatus(), NewsStatus.DRAFT, "转为草稿", "system");
        
        logger.info("转为草稿成功 - ID: {}, 标题: {}", newsId, news.getTitle());
        return newsService.getNewsById(newsId);
    }
    
    /**
     * 批量更新新闻状态
     * 
     * @param newsIds 新闻ID列表
     * @param targetStatus 目标状态
     * @return 更新成功的数量
     */
    public int batchUpdateStatus(List<Long> newsIds, NewsStatus targetStatus) {
        logger.info("批量更新新闻状态 - 数量: {}, 目标状态: {}", newsIds.size(), targetStatus);
        
        if (newsIds == null || newsIds.isEmpty()) {
            throw new NewsValidationException("newsIds", newsIds, "新闻ID列表不能为空");
        }
        
        if (targetStatus == null) {
            throw new NewsValidationException("targetStatus", targetStatus, "目标状态不能为空");
        }
        
        int successCount = 0;
        int failCount = 0;
        
        for (Long newsId : newsIds) {
            try {
                switch (targetStatus) {
                    case PUBLISHED:
                        publishNews(newsId);
                        break;
                    case OFFLINE:
                        offlineNews(newsId, "批量下线操作");
                        break;
                    case DRAFT:
                        draftNews(newsId);
                        break;
                    default:
                        throw new NewsValidationException("targetStatus", targetStatus, "不支持的目标状态: " + targetStatus);
                }
                successCount++;
            } catch (Exception e) {
                logger.warn("批量更新新闻状态失败 - ID: {}, 错误: {}", newsId, e.getMessage());
                failCount++;
            }
        }
        
        logger.info("批量更新新闻状态完成 - 成功: {}, 失败: {}", successCount, failCount);
        return successCount;
    }
    
    /**
     * 获取可进行的状态转换列表
     * 
     * @param newsId 新闻ID
     * @return 可转换的状态列表
     */
    public List<NewsStatus> getAvailableStatusTransitions(Long newsId) {
        logger.info("获取可用状态转换 - ID: {}", newsId);
        
        if (newsId == null) {
            throw new NewsValidationException("newsId", newsId, "新闻ID不能为空");
        }
        
        // 获取新闻信息
        News news = newsMapper.selectById(newsId);
        if (news == null) {
            throw new NewsNotFoundException(newsId);
        }
        
        return getAvailableTransitions(news.getStatus());
    }
    
    /**
     * 验证状态转换是否合法
     * 
     * @param currentStatus 当前状态
     * @param targetStatus 目标状态
     * @param newsId 新闻ID
     */
    private void validateStatusTransition(NewsStatus currentStatus, NewsStatus targetStatus, Long newsId) {
        if (currentStatus == targetStatus) {
            throw new NewsStatusException(newsId, currentStatus, targetStatus, 
                    "新闻已经是目标状态: " + targetStatus);
        }
        
        // 使用NewsValidator进行状态转换验证
        if (!newsValidator.isValidStatusTransition(currentStatus, targetStatus)) {
            throw new NewsStatusException(newsId, currentStatus, targetStatus, 
                    String.format("不允许从状态 %s 转换到状态 %s", currentStatus, targetStatus));
        }
    }
    
    /**
     * 获取指定状态可以转换到的状态列表
     * 
     * @param currentStatus 当前状态
     * @return 可转换的状态列表
     */
    private List<NewsStatus> getAvailableTransitions(NewsStatus currentStatus) {
        switch (currentStatus) {
            case DRAFT:
                // 草稿可以发布或删除
                return List.of(NewsStatus.PUBLISHED);
            case PUBLISHED:
                // 已发布可以下线或转为草稿
                return List.of(NewsStatus.OFFLINE, NewsStatus.DRAFT);
            case OFFLINE:
                // 已下线可以重新发布或转为草稿
                return List.of(NewsStatus.PUBLISHED, NewsStatus.DRAFT);
            default:
                return List.of();
        }
    }
    
    /**
     * 验证新闻是否可以发布
     * 
     * @param news 新闻实体
     */
    private void validateNewsForPublish(News news) {
        if (news.getTitle() == null || news.getTitle().trim().isEmpty()) {
            throw new NewsValidationException("title", news.getTitle(), "新闻标题不能为空");
        }
        
        if (news.getContent() == null || news.getContent().trim().isEmpty()) {
            throw new NewsValidationException("content", news.getContent(), "新闻内容不能为空");
        }
        
        if (news.getCategoryId() == null) {
            throw new NewsValidationException("categoryId", news.getCategoryId(), "新闻分类不能为空");
        }
        
        if (news.getAuthor() == null || news.getAuthor().trim().isEmpty()) {
            throw new NewsValidationException("author", news.getAuthor(), "新闻作者不能为空");
        }
        
        // 验证内容长度
        if (news.getContent().length() < 50) {
            throw new NewsValidationException("content", news.getContent(), "新闻内容长度不能少于50个字符");
        }
        
        // 验证标题长度
        if (news.getTitle().length() < 5 || news.getTitle().length() > 200) {
            throw new NewsValidationException("title", news.getTitle(), "新闻标题长度应在5-200个字符之间");
        }
    }
    
    /**
     * 记录状态变更日志
     * 
     * @param newsId 新闻ID
     * @param fromStatus 原状态
     * @param toStatus 新状态
     * @param reason 变更原因
     * @param operator 操作人
     */
    private void recordStatusChangeLog(Long newsId, NewsStatus fromStatus, NewsStatus toStatus, String reason, String operator) {
        logger.info("记录状态变更日志 - 新闻ID: {}, 从 {} 到 {}, 原因: {}, 操作人: {}", 
                   newsId, fromStatus, toStatus, reason, operator);
        
        try {
            NewsStatusLog log = new NewsStatusLog(newsId, fromStatus, toStatus, reason, operator);
            int result = newsStatusLogMapper.insert(log);
            if (result > 0) {
                logger.debug("状态变更日志记录成功 - 日志ID: {}", log.getId());
            } else {
                logger.warn("状态变更日志记录失败 - 新闻ID: {}", newsId);
            }
        } catch (Exception e) {
            logger.error("记录状态变更日志异常 - 新闻ID: {}, 错误: {}", newsId, e.getMessage(), e);
            // 日志记录失败不应该影响主业务流程，所以这里只记录错误日志
        }
    }
    
    /**
     * 检查新闻是否可以删除
     * 
     * @param newsId 新闻ID
     * @return 是否可以删除
     */
    public boolean canDelete(Long newsId) {
        if (newsId == null) {
            return false;
        }
        
        News news = newsMapper.selectById(newsId);
        if (news == null) {
            return false;
        }
        
        // 只有草稿状态的新闻可以删除
        return news.getStatus() == NewsStatus.DRAFT;
    }
    
    /**
     * 获取新闻状态统计
     * 
     * @return 状态统计信息
     */
    public java.util.Map<NewsStatus, Integer> getStatusStatistics() {
        logger.info("获取新闻状态统计");
        
        java.util.Map<NewsStatus, Integer> stats = new java.util.HashMap<>();
        
        // 统计各状态的新闻数量
        stats.put(NewsStatus.DRAFT, newsMapper.countByStatus(NewsStatus.DRAFT));
        stats.put(NewsStatus.PUBLISHED, newsMapper.countByStatus(NewsStatus.PUBLISHED));
        stats.put(NewsStatus.OFFLINE, newsMapper.countByStatus(NewsStatus.OFFLINE));
        
        logger.info("新闻状态统计完成 - 草稿: {}, 已发布: {}, 已下线: {}", 
                   stats.get(NewsStatus.DRAFT), stats.get(NewsStatus.PUBLISHED), stats.get(NewsStatus.OFFLINE));
        
        return stats;
    }
    
    /**
     * 获取新闻状态变更历史
     * 
     * @param newsId 新闻ID
     * @return 状态变更历史列表
     */
    public List<NewsStatusLog> getStatusChangeHistory(Long newsId) {
        logger.info("获取新闻状态变更历史 - ID: {}", newsId);
        
        if (newsId == null) {
            throw new NewsValidationException("newsId", newsId, "新闻ID不能为空");
        }
        
        // 验证新闻是否存在
        News news = newsMapper.selectById(newsId);
        if (news == null) {
            throw new NewsNotFoundException(newsId);
        }
        
        List<NewsStatusLog> history = newsStatusLogMapper.selectByNewsId(newsId);
        logger.info("获取状态变更历史完成 - 新闻ID: {}, 记录数: {}", newsId, history.size());
        
        return history;
    }
    
    /**
     * 获取新闻最新状态变更记录
     * 
     * @param newsId 新闻ID
     * @return 最新状态变更记录，如果没有记录则返回null
     */
    public NewsStatusLog getLatestStatusChange(Long newsId) {
        logger.info("获取新闻最新状态变更记录 - ID: {}", newsId);
        
        if (newsId == null) {
            throw new NewsValidationException("newsId", newsId, "新闻ID不能为空");
        }
        
        NewsStatusLog latestLog = newsStatusLogMapper.selectLatestByNewsId(newsId);
        if (latestLog != null) {
            logger.info("获取最新状态变更记录成功 - 新闻ID: {}, 变更时间: {}", 
                       newsId, latestLog.getChangeTime());
        } else {
            logger.info("未找到状态变更记录 - 新闻ID: {}", newsId);
        }
        
        return latestLog;
    }
    
    /**
     * 获取状态变更统计信息
     * 
     * @return 状态变更统计信息
     */
    public java.util.Map<String, Object> getStatusChangeStatistics() {
        logger.info("获取状态变更统计信息");
        
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        
        // 总变更次数
        int totalChanges = newsStatusLogMapper.countAll();
        stats.put("totalChanges", totalChanges);
        
        // 今日变更次数（这里简化处理，实际可能需要更复杂的查询）
        stats.put("todayChanges", 0); // TODO: 实现今日变更统计
        
        // 最近活跃的新闻（有状态变更的新闻数量）
        // TODO: 可以添加更多统计维度
        
        logger.info("状态变更统计完成 - 总变更次数: {}", totalChanges);
        return stats;
    }
    
    /**
     * 清理指定新闻的状态变更日志
     * 当新闻被删除时调用此方法清理相关日志
     * 
     * @param newsId 新闻ID
     * @return 清理的日志数量
     */
    public int cleanupStatusLogs(Long newsId) {
        logger.info("清理新闻状态日志 - ID: {}", newsId);
        
        if (newsId == null) {
            throw new NewsValidationException("newsId", newsId, "新闻ID不能为空");
        }
        
        try {
            int deletedCount = newsStatusLogMapper.deleteByNewsId(newsId);
            logger.info("清理状态日志完成 - 新闻ID: {}, 清理数量: {}", newsId, deletedCount);
            return deletedCount;
        } catch (Exception e) {
            logger.error("清理状态日志失败 - 新闻ID: {}, 错误: {}", newsId, e.getMessage(), e);
            throw new NewsOperationException("清理状态日志", newsId, "清理操作失败: " + e.getMessage());
        }
    }
}