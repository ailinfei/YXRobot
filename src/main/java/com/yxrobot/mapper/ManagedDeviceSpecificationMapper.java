package com.yxrobot.mapper;

import com.yxrobot.entity.ManagedDeviceSpecification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 设备技术参数Mapper接口
 */
@Mapper
public interface ManagedDeviceSpecificationMapper {
    
    /**
     * 根据设备ID查询技术参数
     */
    ManagedDeviceSpecification selectByDeviceId(@Param("deviceId") Long deviceId);
    
    /**
     * 插入技术参数
     */
    int insert(ManagedDeviceSpecification specification);
    
    /**
     * 更新技术参数
     */
    int updateByDeviceId(ManagedDeviceSpecification specification);
    
    /**
     * 删除技术参数
     */
    int deleteByDeviceId(@Param("deviceId") Long deviceId);
}