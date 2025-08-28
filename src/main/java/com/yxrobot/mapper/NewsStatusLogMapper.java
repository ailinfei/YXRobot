package com.yxrobot.mapper;

import com.yxrobot.entity.NewsStatusLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 新闻状态变更日志映射器接口
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@Mapper
public interface NewsStatusLogMapper {
    
    /**
     * 插入状态变更日志
     */
    int insert(NewsStatusLog log);
    
    /**
     * 根据新闻ID查询状态变更日志
     */
    List<NewsStatusLog> selectByNewsId(@Param("newsId") Long newsId);
    
    /**
     * 根据新闻ID查询最新的状态变更日志
     */
    NewsStatusLog selectLatestByNewsId(@Param("newsId") Long newsId);
    
    /**
     * 分页查询状态变更日志
     */
    List<NewsStatusLog> selectByPage(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 统计日志总数
     */
    int countAll();
    
    /**
     * 根据新闻ID统计日志数量
     */
    int countByNewsId(@Param("newsId") Long newsId);
    
    /**
     * 根据操作人查询日志
     */
    List<NewsStatusLog> selectByOperator(@Param("operator") String operator,
                                        @Param("offset") int offset, 
                                        @Param("limit") int limit);
    
    /**
     * 删除指定新闻的所有状态日志
     */
    int deleteByNewsId(@Param("newsId") Long newsId);
}