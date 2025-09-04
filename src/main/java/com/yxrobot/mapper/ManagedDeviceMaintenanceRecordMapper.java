package com.yxrobot.mapper;

import com.yxrobot.entity.ManagedDeviceMaintenanceRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 设备维护记录Mapper接口
 */
@Mapper
public interface ManagedDeviceMaintenanceRecordMapper {
    
    /**
     * 根据设备ID查询维护记录列表
     */
    List<ManagedDeviceMaintenanceRecord> selectByDeviceId(@Param("deviceId") Long deviceId);
    
    /**
     * 根据ID查询维护记录
     */
    ManagedDeviceMaintenanceRecord selectById(@Param("id") Long id);
    
    /**
     * 插入维护记录
     */
    int insert(ManagedDeviceMaintenanceRecord record);
    
    /**
     * 更新维护记录
     */
    int updateById(ManagedDeviceMaintenanceRecord record);
    
    /**
     * 删除维护记录
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据设备ID删除所有维护记录
     */
    int deleteByDeviceId(@Param("deviceId") Long deviceId);
}