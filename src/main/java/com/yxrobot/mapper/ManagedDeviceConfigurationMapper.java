package com.yxrobot.mapper;

import com.yxrobot.entity.ManagedDeviceConfiguration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 设备配置Mapper接口
 */
@Mapper
public interface ManagedDeviceConfigurationMapper {
    
    /**
     * 根据设备ID查询配置
     */
    ManagedDeviceConfiguration selectByDeviceId(@Param("deviceId") Long deviceId);
    
    /**
     * 插入配置
     */
    int insert(ManagedDeviceConfiguration configuration);
    
    /**
     * 更新配置
     */
    int updateByDeviceId(ManagedDeviceConfiguration configuration);
    
    /**
     * 删除配置
     */
    int deleteByDeviceId(@Param("deviceId") Long deviceId);
}