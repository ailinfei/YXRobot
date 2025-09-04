package com.yxrobot.mapper;

import com.yxrobot.entity.ManagedDeviceLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 设备日志Mapper接口
 */
@Mapper
public interface ManagedDeviceLogMapper {
    
    /**
     * 分页查询设备日志（支持筛选）
     */
    List<ManagedDeviceLog> selectByPage(@Param("deviceId") Long deviceId,
                                      @Param("offset") Integer offset,
                                      @Param("limit") Integer limit,
                                      @Param("level") String level,
                                      @Param("category") String category,
                                      @Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);
    
    /**
     * 统计设备日志总数（支持筛选）
     */
    Integer countByConditions(@Param("deviceId") Long deviceId,
                            @Param("level") String level,
                            @Param("category") String category,
                            @Param("startDate") LocalDateTime startDate,
                            @Param("endDate") LocalDateTime endDate);
    
    /**
     * 插入日志
     */
    int insert(ManagedDeviceLog log);
    
    /**
     * 根据设备ID删除日志
     */
    int deleteByDeviceId(@Param("deviceId") Long deviceId);
    
    /**
     * 根据时间范围删除日志
     */
    int deleteByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}