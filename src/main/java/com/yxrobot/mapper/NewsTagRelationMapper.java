package com.yxrobot.mapper;

import com.yxrobot.entity.NewsTag;
import com.yxrobot.entity.NewsTagRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 新闻标签关联映射器接口
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@Mapper
public interface NewsTagRelationMapper {
    
    /**
     * 插入新闻标签关联
     */
    int insert(NewsTagRelation relation);
    
    /**
     * 批量插入新闻标签关联
     */
    int batchInsert(@Param("relations") List<NewsTagRelation> relations);
    
    /**
     * 根据ID删除关联
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据新闻ID删除所有关联
     */
    int deleteByNewsId(@Param("newsId") Long newsId);
    
    /**
     * 根据标签ID删除所有关联
     */
    int deleteByTagId(@Param("tagId") Long tagId);
    
    /**
     * 删除指定新闻和标签的关联
     */
    int deleteByNewsIdAndTagId(@Param("newsId") Long newsId, @Param("tagId") Long tagId);
    
    /**
     * 批量删除新闻的标签关联
     */
    int batchDeleteByNewsIdAndTagIds(@Param("newsId") Long newsId, @Param("tagIds") List<Long> tagIds);
    
    /**
     * 根据新闻ID查询关联的标签
     */
    List<NewsTag> selectTagsByNewsId(@Param("newsId") Long newsId);
    
    /**
     * 根据标签ID查询关联的新闻ID列表
     */
    List<Long> selectNewsIdsByTagId(@Param("tagId") Long tagId);
    
    /**
     * 根据新闻ID查询标签ID列表
     */
    List<Long> selectTagIdsByNewsId(@Param("newsId") Long newsId);
    
    /**
     * 检查新闻和标签的关联是否存在
     */
    boolean existsByNewsIdAndTagId(@Param("newsId") Long newsId, @Param("tagId") Long tagId);
    
    /**
     * 统计新闻的标签数量
     */
    int countTagsByNewsId(@Param("newsId") Long newsId);
    
    /**
     * 统计标签关联的新闻数量
     */
    int countNewsByTagId(@Param("tagId") Long tagId);
    
    /**
     * 批量查询新闻的标签
     */
    List<NewsTag> selectTagsByNewsIds(@Param("newsIds") List<Long> newsIds);
    
    /**
     * 更新新闻的标签关联（先删除后插入）
     */
    int updateNewsTagRelations(@Param("newsId") Long newsId, @Param("tagIds") List<Long> tagIds);
}