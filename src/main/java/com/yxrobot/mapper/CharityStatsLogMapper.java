package com.yxrobot.mapper;

import com.yxrobot.entity.CharityStatsLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 公益统计数据更新日志访问接口
 * 使用MyBatis进行数据库操作，支持日志记录的CRUD操作和查询
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-18
 */
@Mapper
public interface CharityStatsLogMapper {
    
    /**
     * 插入新的日志记录
     * 
     * @param log 日志对象
     * @return 影响的行数
     */
    int insert(CharityStatsLog log);
    
    /**
     * 根据ID查询日志详情
     * 
     * @param id 日志ID
     * @return 日志详情，如果不存在返回null
     */
    CharityStatsLog selectById(@Param("id") Long id);
    
    /**
     * 根据统计数据ID查询日志列表
     * 按操作时间倒序排列
     * 
     * @param statsId 统计数据ID
     * @param offset 分页偏移量
     * @param limit 每页数量
     * @return 日志列表
     */
    List<CharityStatsLog> selectByStatsId(@Param("statsId") Long statsId,
                                        @Param("offset") Integer offset,
                                        @Param("limit") Integer limit);
    
    /**
     * 根据统计数据ID统计日志总数
     * 用于分页计算
     * 
     * @param statsId 统计数据ID
     * @return 日志总数
     */
    Long countByStatsId(@Param("statsId") Long statsId);
    
    /**
     * 根据操作人查询日志列表
     * 按操作时间倒序排列
     * 
     * @param operatorId 操作人ID
     * @param offset 分页偏移量
     * @param limit 每页数量
     * @return 日志列表
     */
    List<CharityStatsLog> selectByOperator(@Param("operatorId") Long operatorId,
                                         @Param("offset") Integer offset,
                                         @Param("limit") Integer limit);
    
    /**
     * 根据操作人统计日志总数
     * 用于分页计算
     * 
     * @param operatorId 操作人ID
     * @return 日志总数
     */
    Long countByOperator(@Param("operatorId") Long operatorId);
    
    /**
     * 根据时间范围查询日志列表
     * 按操作时间倒序排列
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param offset 分页偏移量
     * @param limit 每页数量
     * @return 日志列表
     */
    List<CharityStatsLog> selectByTimeRange(@Param("startTime") LocalDateTime startTime,
                                          @Param("endTime") LocalDateTime endTime,
                                          @Param("offset") Integer offset,
                                          @Param("limit") Integer limit);
    
    /**
     * 根据时间范围统计日志总数
     * 用于分页计算
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日志总数
     */
    Long countByTimeRange(@Param("startTime") LocalDateTime startTime,
                         @Param("endTime") LocalDateTime endTime);
    
    /**
     * 根据操作类型查询日志列表
     * 按操作时间倒序排列
     * 
     * @param operationType 操作类型
     * @param offset 分页偏移量
     * @param limit 每页数量
     * @return 日志列表
     */
    List<CharityStatsLog> selectByOperationType(@Param("operationType") String operationType,
                                              @Param("offset") Integer offset,
                                              @Param("limit") Integer limit);
    
    /**
     * 根据操作类型统计日志总数
     * 用于分页计算
     * 
     * @param operationType 操作类型
     * @return 日志总数
     */
    Long countByOperationType(@Param("operationType") String operationType);
    
    /**
     * 根据操作结果查询日志列表
     * 按操作时间倒序排列
     * 
     * @param operationResult 操作结果
     * @param offset 分页偏移量
     * @param limit 每页数量
     * @return 日志列表
     */
    List<CharityStatsLog> selectByOperationResult(@Param("operationResult") String operationResult,
                                                @Param("offset") Integer offset,
                                                @Param("limit") Integer limit);
    
    /**
     * 根据操作结果统计日志总数
     * 用于分页计算
     * 
     * @param operationResult 操作结果
     * @return 日志总数
     */
    Long countByOperationResult(@Param("operationResult") String operationResult);
    
    /**
     * 获取最新的日志记录
     * 按操作时间倒序排列
     * 
     * @param limit 限制返回数量
     * @return 最新日志列表
     */
    List<CharityStatsLog> selectLatestLogs(@Param("limit") Integer limit);
    
    /**
     * 获取操作类型统计
     * 返回各操作类型的日志数量
     * 
     * @return 操作类型统计列表
     */
    List<Map<String, Object>> getOperationTypeStatistics();
    
    /**
     * 获取操作结果统计
     * 返回各操作结果的日志数量
     * 
     * @return 操作结果统计列表
     */
    List<Map<String, Object>> getOperationResultStatistics();
    
    /**
     * 获取操作人统计
     * 返回各操作人的日志数量
     * 
     * @param limit 限制返回数量
     * @return 操作人统计列表
     */
    List<Map<String, Object>> getOperatorStatistics(@Param("limit") Integer limit);
    
    /**
     * 获取每日操作统计
     * 返回指定天数内每日的操作数量
     * 
     * @param days 天数
     * @return 每日操作统计列表
     */
    List<Map<String, Object>> getDailyOperationStatistics(@Param("days") Integer days);
    
    /**
     * 获取失败操作日志
     * 返回操作结果为failed的日志
     * 
     * @param offset 分页偏移量
     * @param limit 每页数量
     * @return 失败操作日志列表
     */
    List<CharityStatsLog> selectFailedOperations(@Param("offset") Integer offset,
                                               @Param("limit") Integer limit);
    
    /**
     * 统计失败操作总数
     * 
     * @return 失败操作总数
     */
    Long countFailedOperations();
    
    /**
     * 根据IP地址查询日志列表
     * 按操作时间倒序排列
     * 
     * @param operatorIp 操作IP地址
     * @param offset 分页偏移量
     * @param limit 每页数量
     * @return 日志列表
     */
    List<CharityStatsLog> selectByOperatorIp(@Param("operatorIp") String operatorIp,
                                           @Param("offset") Integer offset,
                                           @Param("limit") Integer limit);
    
    /**
     * 根据IP地址统计日志总数
     * 用于分页计算
     * 
     * @param operatorIp 操作IP地址
     * @return 日志总数
     */
    Long countByOperatorIp(@Param("operatorIp") String operatorIp);
    
    /**
     * 清理过期日志
     * 删除指定天数之前的日志记录
     * 
     * @param days 保留天数
     * @return 删除的记录数
     */
    int cleanExpiredLogs(@Param("days") Integer days);
    
    /**
     * 获取数据版本变更历史
     * 返回指定统计数据的版本变更记录
     * 
     * @param statsId 统计数据ID
     * @return 版本变更历史列表
     */
    List<CharityStatsLog> selectVersionHistory(@Param("statsId") Long statsId);
}