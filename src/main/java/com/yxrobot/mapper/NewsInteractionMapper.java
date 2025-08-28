package com.yxrobot.mapper;

import com.yxrobot.entity.InteractionType;
import com.yxrobot.entity.NewsInteraction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 新闻互动映射器接口
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@Mapper
public interface NewsInteractionMapper {
    
    /**
     * 插入新闻互动记录
     */
    int insert(NewsInteraction interaction);
    
    /**
     * 批量插入新闻互动记录
     */
    int batchInsert(@Param("interactions") List<NewsInteraction> interactions);
    
    /**
     * 根据ID删除互动记录
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据新闻ID删除所有互动记录
     */
    int deleteByNewsId(@Param("newsId") Long newsId);
    
    /**
     * 根据ID查询互动记录
     */
    NewsInteraction selectById(@Param("id") Long id);
    
    /**
     * 根据新闻ID查询互动记录
     */
    List<NewsInteraction> selectByNewsId(@Param("newsId") Long newsId,
                                         @Param("offset") int offset, 
                                         @Param("limit") int limit);
    
    /**
     * 根据新闻ID和互动类型查询记录
     */
    List<NewsInteraction> selectByNewsIdAndType(@Param("newsId") Long newsId,
                                                @Param("interactionType") InteractionType interactionType,
                                                @Param("offset") int offset, 
                                                @Param("limit") int limit);
    
    /**
     * 根据用户ID查询互动记录
     */
    List<NewsInteraction> selectByUserId(@Param("userId") Long userId,
                                         @Param("offset") int offset, 
                                         @Param("limit") int limit);
    
    /**
     * 根据IP地址查询互动记录
     */
    List<NewsInteraction> selectByIpAddress(@Param("ipAddress") String ipAddress,
                                            @Param("offset") int offset, 
                                            @Param("limit") int limit);
    
    /**
     * 根据时间范围查询互动记录
     */
    List<NewsInteraction> selectByDateRange(@Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate,
                                            @Param("offset") int offset, 
                                            @Param("limit") int limit);
    
    /**
     * 统计新闻的互动总数
     */
    int countByNewsId(@Param("newsId") Long newsId);
    
    /**
     * 统计新闻指定类型的互动数量
     */
    int countByNewsIdAndType(@Param("newsId") Long newsId, @Param("interactionType") InteractionType interactionType);
    
    /**
     * 统计用户的互动总数
     */
    int countByUserId(@Param("userId") Long userId);
    
    /**
     * 统计IP地址的互动总数
     */
    int countByIpAddress(@Param("ipAddress") String ipAddress);
    
    /**
     * 统计时间范围内的互动总数
     */
    int countByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    /**
     * 检查用户是否对新闻进行过指定类型的互动
     */
    boolean existsByUserIdAndNewsIdAndType(@Param("userId") Long userId,
                                           @Param("newsId") Long newsId,
                                           @Param("interactionType") InteractionType interactionType);
    
    /**
     * 检查IP是否对新闻进行过指定类型的互动
     */
    boolean existsByIpAndNewsIdAndType(@Param("ipAddress") String ipAddress,
                                       @Param("newsId") Long newsId,
                                       @Param("interactionType") InteractionType interactionType);
    
    /**
     * 获取新闻的互动统计
     */
    Map<String, Object> getInteractionStatsByNewsId(@Param("newsId") Long newsId);
    
    /**
     * 获取按日期统计的互动数据
     */
    List<Map<String, Object>> getInteractionStatsByDate(@Param("startDate") LocalDateTime startDate,
                                                         @Param("endDate") LocalDateTime endDate);
    
    /**
     * 获取按新闻统计的互动数据
     */
    List<Map<String, Object>> getInteractionStatsByNews(@Param("limit") int limit);
    
    /**
     * 获取按互动类型统计的数据
     */
    List<Map<String, Object>> getInteractionStatsByType();
    
    /**
     * 获取热门新闻（按互动数排序）
     */
    List<Map<String, Object>> getHotNewsByInteractions(@Param("interactionType") InteractionType interactionType,
                                                        @Param("limit") int limit);
    
    /**
     * 删除过期的互动记录
     */
    int deleteExpiredRecords(@Param("expireDate") LocalDateTime expireDate);
    
    /**
     * 根据类型和时间范围统计互动数量
     */
    int countByTypeAndTimeRange(@Param("interactionType") InteractionType interactionType,
                                @Param("startDate") LocalDateTime startDate,
                                @Param("endDate") LocalDateTime endDate);
    
    /**
     * 根据类型统计互动数量
     */
    int countByType(@Param("interactionType") InteractionType interactionType);
    
    /**
     * 统计所有互动数量
     */
    int countAll();
    
    /**
     * 根据类型获取热门新闻
     */
    List<Map<String, Object>> selectPopularNewsByType(@Param("interactionType") InteractionType interactionType,
                                                       @Param("since") LocalDateTime since,
                                                       @Param("limit") int limit);
    
    /**
     * 根据创建时间删除记录
     */
    int deleteByCreatedAtBefore(@Param("createdAt") LocalDateTime createdAt);
    
    /**
     * 检查用户是否对新闻进行过指定类型的互动
     */
    boolean existsByNewsIdAndTypeAndUserId(@Param("newsId") Long newsId,
                                           @Param("interactionType") InteractionType interactionType,
                                           @Param("userId") Long userId);
    
    /**
     * 检查IP是否对新闻进行过指定类型的互动
     */
    boolean existsByNewsIdAndTypeAndIp(@Param("newsId") Long newsId,
                                       @Param("interactionType") InteractionType interactionType,
                                       @Param("ipAddress") String ipAddress);
}