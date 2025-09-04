package com.yxrobot.mapper;

import com.yxrobot.entity.ManagedDeviceUsageStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 设备使用统计Mapper接口
 */
@Mapper
public interface ManagedDeviceUsageStatsMapper {
    
    /**
     * 根据设备ID查询使用统计
     */
    ManagedDeviceUsageStats selectByDeviceId(@Param("deviceId") Long deviceId);
    
    /**
     * 插入使用统计
     */
    int insert(ManagedDeviceUsageStats usageStats);
    
    /**
     * 更新使用统计
     */
    int updateByDeviceId(ManagedDeviceUsageStats usageStats);
    
    /**
     * 删除使用统计
     */
    int deleteByDeviceId(@Param("deviceId") Long deviceId);
}