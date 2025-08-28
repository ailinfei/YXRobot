package com.yxrobot.mapper;

import com.yxrobot.entity.NewsTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 新闻标签映射器接口
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@Mapper
public interface NewsTagMapper {
    
    /**
     * 插入新闻标签
     */
    int insert(NewsTag newsTag);
    
    /**
     * 根据ID删除新闻标签
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 更新新闻标签
     */
    int updateById(NewsTag newsTag);
    
    /**
     * 根据ID查询新闻标签
     */
    NewsTag selectById(@Param("id") Long id);
    
    /**
     * 根据名称查询新闻标签
     */
    NewsTag selectByName(@Param("name") String name);
    
    /**
     * 查询所有新闻标签
     */
    List<NewsTag> selectAll();
    
    /**
     * 分页查询新闻标签
     */
    List<NewsTag> selectByPage(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 根据使用次数排序查询标签
     */
    List<NewsTag> selectByUsageCount(@Param("limit") int limit);
    
    /**
     * 根据名称模糊查询标签
     */
    List<NewsTag> selectByNameLike(@Param("name") String name);
    
    /**
     * 根据ID列表查询标签
     */
    List<NewsTag> selectByIds(@Param("ids") List<Long> ids);
    
    /**
     * 统计标签总数
     */
    int countAll();
    
    /**
     * 检查标签名称是否存在
     */
    boolean existsByName(@Param("name") String name);
    
    /**
     * 检查标签名称是否存在（排除指定ID）
     */
    boolean existsByNameExcludeId(@Param("name") String name, @Param("id") Long id);
    
    /**
     * 增加标签使用次数
     */
    int incrementUsageCount(@Param("id") Long id);
    
    /**
     * 减少标签使用次数
     */
    int decrementUsageCount(@Param("id") Long id);
    
    /**
     * 批量增加标签使用次数
     */
    int batchIncrementUsageCount(@Param("ids") List<Long> ids);
    
    /**
     * 批量减少标签使用次数
     */
    int batchDecrementUsageCount(@Param("ids") List<Long> ids);
    
    /**
     * 重置标签使用次数
     */
    int resetUsageCount(@Param("id") Long id);
}