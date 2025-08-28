package com.yxrobot.mapper;

import com.yxrobot.entity.News;
import com.yxrobot.entity.NewsStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 新闻映射器接口
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@Mapper
public interface NewsMapper {
    
    /**
     * 插入新闻
     */
    int insert(News news);
    
    /**
     * 根据ID删除新闻（物理删除）
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据ID软删除新闻
     */
    int softDeleteById(@Param("id") Long id);
    
    /**
     * 批量软删除新闻
     */
    int batchSoftDelete(@Param("ids") List<Long> ids);
    
    /**
     * 更新新闻
     */
    int updateById(News news);
    
    /**
     * 根据ID查询新闻
     */
    News selectById(@Param("id") Long id);
    
    /**
     * 根据ID查询新闻（包含分类和标签信息）
     */
    News selectByIdWithDetails(@Param("id") Long id);
    
    /**
     * 分页查询新闻列表
     */
    List<News> selectByPage(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 条件查询新闻列表
     */
    List<News> selectByConditions(@Param("conditions") Map<String, Object> conditions,
                                  @Param("offset") int offset, 
                                  @Param("limit") int limit);
    
    /**
     * 根据状态查询新闻列表
     */
    List<News> selectByStatus(@Param("status") NewsStatus status,
                              @Param("offset") int offset, 
                              @Param("limit") int limit);
    
    /**
     * 根据分类ID查询新闻列表
     */
    List<News> selectByCategoryId(@Param("categoryId") Long categoryId,
                                  @Param("offset") int offset, 
                                  @Param("limit") int limit);
    
    /**
     * 根据作者查询新闻列表
     */
    List<News> selectByAuthor(@Param("author") String author,
                              @Param("offset") int offset, 
                              @Param("limit") int limit);
    
    /**
     * 搜索新闻（标题和内容）
     */
    List<News> searchByKeyword(@Param("keyword") String keyword,
                               @Param("offset") int offset, 
                               @Param("limit") int limit);
    
    /**
     * 查询推荐新闻
     */
    List<News> selectFeatured(@Param("limit") int limit);
    
    /**
     * 查询热门新闻（按浏览量排序）
     */
    List<News> selectHotNews(@Param("limit") int limit);
    
    /**
     * 查询最新发布的新闻
     */
    List<News> selectLatestPublished(@Param("limit") int limit);
    
    /**
     * 查询相关新闻（同分类）
     */
    List<News> selectRelatedNews(@Param("newsId") Long newsId, 
                                 @Param("categoryId") Long categoryId, 
                                 @Param("limit") int limit);
    
    /**
     * 统计新闻总数
     */
    int countAll();
    
    /**
     * 根据条件统计新闻数量
     */
    int countByConditions(@Param("conditions") Map<String, Object> conditions);
    
    /**
     * 根据状态统计新闻数量
     */
    int countByStatus(@Param("status") NewsStatus status);
    
    /**
     * 根据分类统计新闻数量
     */
    int countByCategoryId(@Param("categoryId") Long categoryId);
    
    /**
     * 根据作者统计新闻数量
     */
    int countByAuthor(@Param("author") String author);
    
    /**
     * 搜索新闻数量统计
     */
    int countByKeyword(@Param("keyword") String keyword);
    
    /**
     * 更新新闻状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") NewsStatus status, @Param("publishTime") LocalDateTime publishTime);
    
    /**
     * 批量更新新闻状态
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") NewsStatus status);
    
    /**
     * 更新发布时间
     */
    int updatePublishTime(@Param("id") Long id, @Param("publishTime") LocalDateTime publishTime);
    
    /**
     * 增加浏览量
     */
    int incrementViews(@Param("id") Long id);
    
    /**
     * 增加评论数
     */
    int incrementComments(@Param("id") Long id);
    
    /**
     * 增加点赞数
     */
    int incrementLikes(@Param("id") Long id);
    
    /**
     * 减少点赞数
     */
    int decrementLikes(@Param("id") Long id);
    
    /**
     * 更新推荐状态
     */
    int updateFeaturedStatus(@Param("id") Long id, @Param("isFeatured") Boolean isFeatured);
    
    /**
     * 批量更新推荐状态
     */
    int batchUpdateFeaturedStatus(@Param("ids") List<Long> ids, @Param("isFeatured") Boolean isFeatured);
    
    /**
     * 获取新闻统计数据
     */
    Map<String, Object> getNewsStats();
    
    /**
     * 获取按日期统计的新闻数据
     */
    List<Map<String, Object>> getNewsStatsByDate(@Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate);
    
    /**
     * 获取按分类统计的新闻数据
     */
    List<Map<String, Object>> getNewsStatsByCategory();
    
    /**
     * 获取按作者统计的新闻数据
     */
    List<Map<String, Object>> getNewsStatsByAuthor();
}