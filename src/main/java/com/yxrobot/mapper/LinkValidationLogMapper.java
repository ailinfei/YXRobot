package com.yxrobot.mapper;

import com.yxrobot.entity.LinkValidationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 链接验证日志数据访问层接口
 * 负责链接验证日志相关的数据库操作
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-22
 */
@Mapper
public interface LinkValidationLogMapper {
    
    /**
     * 根据ID查询验证日志
     * @param id 日志ID
     * @return 验证日志实体
     */
    LinkValidationLog selectById(@Param("id") Long id);
    
    /**
     * 根据链接ID查询验证日志列表
     * @param linkId 链接ID
     * @param limit 限制数量
     * @return 验证日志列表
     */
    List<LinkValidationLog> selectByLinkId(@Param("linkId") Long linkId, @Param("limit") Integer limit);
    
    /**
     * 查询最新的验证日志
     * @param linkId 链接ID
     * @return 最新的验证日志
     */
    LinkValidationLog selectLatestByLinkId(@Param("linkId") Long linkId);
    
    /**
     * 插入验证日志
     * @param log 验证日志实体
     * @return 影响行数
     */
    int insert(LinkValidationLog log);
    
    /**
     * 批量插入验证日志
     * @param logs 验证日志列表
     * @return 影响行数
     */
    int batchInsert(@Param("logs") List<LinkValidationLog> logs);
    
    /**
     * 删除指定时间之前的日志
     * @param beforeTime 时间点
     * @return 影响行数
     */
    int deleteBeforeTime(@Param("beforeTime") LocalDateTime beforeTime);
    
    /**
     * 统计验证成功率
     * @param linkId 链接ID（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 成功率统计
     */
    Double selectSuccessRate(@Param("linkId") Long linkId, 
                           @Param("startTime") LocalDateTime startTime, 
                           @Param("endTime") LocalDateTime endTime);
    
    /**
     * 统计平均响应时间
     * @param linkId 链接ID（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 平均响应时间
     */
    Double selectAverageResponseTime(@Param("linkId") Long linkId, 
                                   @Param("startTime") LocalDateTime startTime, 
                                   @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查询验证历史统计
     * @param linkId 链接ID
     * @param days 天数
     * @return 历史统计数据
     */
    List<java.util.Map<String, Object>> selectValidationHistory(@Param("linkId") Long linkId, @Param("days") Integer days);
}