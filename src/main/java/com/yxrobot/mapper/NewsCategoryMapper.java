package com.yxrobot.mapper;

import com.yxrobot.entity.NewsCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 新闻分类映射器接口
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@Mapper
public interface NewsCategoryMapper {
    
    /**
     * 插入新闻分类
     */
    int insert(NewsCategory newsCategory);
    
    /**
     * 根据ID删除新闻分类
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 更新新闻分类
     */
    int updateById(NewsCategory newsCategory);
    
    /**
     * 根据ID查询新闻分类
     */
    NewsCategory selectById(@Param("id") Long id);
    
    /**
     * 根据名称查询新闻分类
     */
    NewsCategory selectByName(@Param("name") String name);
    
    /**
     * 查询所有启用的新闻分类
     */
    List<NewsCategory> selectAllEnabled();
    
    /**
     * 查询所有新闻分类
     */
    List<NewsCategory> selectAll();
    
    /**
     * 分页查询新闻分类
     */
    List<NewsCategory> selectByPage(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 统计新闻分类总数
     */
    int countAll();
    
    /**
     * 统计启用的新闻分类数量
     */
    int countEnabled();
    
    /**
     * 检查分类名称是否存在
     */
    boolean existsByName(@Param("name") String name);
    
    /**
     * 检查分类名称是否存在（排除指定ID）
     */
    boolean existsByNameExcludeId(@Param("name") String name, @Param("id") Long id);
    
    /**
     * 批量更新排序权重
     */
    int batchUpdateSortOrder(@Param("categories") List<NewsCategory> categories);
    
    /**
     * 启用/禁用分类
     */
    int updateEnabledStatus(@Param("id") Long id, @Param("isEnabled") Boolean isEnabled);
}