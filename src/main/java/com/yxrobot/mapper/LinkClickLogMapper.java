package com.yxrobot.mapper;

import com.yxrobot.entity.LinkClickLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 链接点击日志数据访问层接口
 * 负责链接点击日志相关的数据库操作
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-22
 */
@Mapper
public interface LinkClickLogMapper {
    
    /**
     * 根据ID查询点击日志
     * @param id 日志ID
     * @return 点击日志实体
     */
    LinkClickLog selectById(@Param("id") Long id);
    
    /**
     * 根据链接ID查询点击日志列表
     * @param linkId 链接ID
     * @param limit 限制数量
     * @return 点击日志列表
     */
    List<LinkClickLog> selectByLinkId(@Param("linkId") Long linkId, @Param("limit") Integer limit);
    
    /**
     * 插入点击日志
     * @param log 点击日志实体
     * @return 影响行数
     */
    int insert(LinkClickLog log);
    
    /**
     * 批量插入点击日志
     * @param logs 点击日志列表
     * @return 影响行数
     */
    int batchInsert(@Param("logs") List<LinkClickLog> logs);
    
    /**
     * 更新转化信息
     * @param id 日志ID
     * @param conversionType 转化类型
     * @param conversionValue 转化价值
     * @return 影响行数
     */
    int updateConversion(@Param("id") Long id, 
                        @Param("conversionType") String conversionType, 
                        @Param("conversionValue") java.math.BigDecimal conversionValue);
    
    /**
     * 删除指定时间之前的日志
     * @param beforeTime 时间点
     * @return 影响行数
     */
    int deleteBeforeTime(@Param("beforeTime") LocalDateTime beforeTime);
    
    /**
     * 统计链接点击量
     * @param linkId 链接ID
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 点击量
     */
    Long selectClickCount(@Param("linkId") Long linkId, 
                         @Param("startTime") LocalDateTime startTime, 
                         @Param("endTime") LocalDateTime endTime);
    
    /**
     * 统计链接转化量
     * @param linkId 链接ID
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 转化量
     */
    Long selectConversionCount(@Param("linkId") Long linkId, 
                              @Param("startTime") LocalDateTime startTime, 
                              @Param("endTime") LocalDateTime endTime);
    
    /**
     * 获取点击趋势数据
     * @param linkId 链接ID（可选）
     * @param days 天数
     * @return 趋势数据
     */
    List<Map<String, Object>> selectClickTrends(@Param("linkId") Long linkId, @Param("days") Integer days);
    
    /**
     * 获取转化趋势数据
     * @param linkId 链接ID（可选）
     * @param days 天数
     * @return 趋势数据
     */
    List<Map<String, Object>> selectConversionTrends(@Param("linkId") Long linkId, @Param("days") Integer days);
    
    /**
     * 获取用户设备统计
     * @param linkId 链接ID（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 设备统计数据
     */
    List<Map<String, Object>> selectDeviceStats(@Param("linkId") Long linkId, 
                                               @Param("startTime") LocalDateTime startTime, 
                                               @Param("endTime") LocalDateTime endTime);
    
    /**
     * 获取来源统计
     * @param linkId 链接ID（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 来源统计数据
     */
    List<Map<String, Object>> selectRefererStats(@Param("linkId") Long linkId, 
                                                 @Param("startTime") LocalDateTime startTime, 
                                                 @Param("endTime") LocalDateTime endTime);
    
    /**
     * 更新链接的点击统计
     * @param linkId 链接ID
     * @return 影响行数
     */
    int updateLinkClickStats(@Param("linkId") Long linkId);
}