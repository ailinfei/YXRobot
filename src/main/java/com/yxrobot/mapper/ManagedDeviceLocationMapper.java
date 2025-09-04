package com.yxrobot.mapper;

import com.yxrobot.entity.ManagedDeviceLocation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 设备位置信息Mapper接口
 */
@Mapper
public interface ManagedDeviceLocationMapper {
    
    /**
     * 根据设备ID查询位置信息
     */
    ManagedDeviceLocation selectByDeviceId(@Param("deviceId") Long deviceId);
    
    /**
     * 插入位置信息
     */
    int insert(ManagedDeviceLocation location);
    
    /**
     * 更新位置信息
     */
    int updateByDeviceId(ManagedDeviceLocation location);
    
    /**
     * 删除位置信息
     */
    int deleteByDeviceId(@Param("deviceId") Long deviceId);
}