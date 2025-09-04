package com.yxrobot.mapper;

import com.yxrobot.entity.DeviceMonitoringData;
import com.yxrobot.entity.DeviceStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 设备监控数据Mapper接口
 * 对应device_monitoring_data表的数据访问操作
 * 
 * 字段映射规范：
 * - 数据库字段使用snake_case命名（如：device_id, serial_number）
 * - Java属性使用camelCase命名（如：deviceId, serialNumber）
 * - XML映射文件中确保column和property正确对应
 * 
 * @author YXRobot Development Team
 * @since 2024
 */
@Mapper
public interface DeviceMonitoringDataMapper {
    
    /**
     * 插入设备监控数据
     * @param data 设备监控数据对象
     * @return 影响行数
     */
    int insert(DeviceMonitoringData data);
    
    /**
     * 根据ID查询设备监控数据
     * @param id 监控数据ID
     * @return 设备监控数据对象
     */
    DeviceMonitoringData selectById(@Param("id") Long id);
    
    /**
     * 根据设备ID查询监控数据
     * @param deviceId 设备ID
     * @return 设备监控数据对象
     */
    DeviceMonitoringData selectByDeviceId(@Param("deviceId") Long deviceId);
    
    /**
     * 根据设备序列号查询监控数据
     * @param serialNumber 设备序列号
     * @return 设备监控数据对象
     */
    DeviceMonitoringData selectBySerialNumber(@Param("serialNumber") String serialNumber);
    
    /**
     * 分页查询设备监控数据列表（支持搜索和筛选）
     * @param params 查询参数
     * @return 设备监控数据列表
     */
    List<DeviceMonitoringData> selectWithPagination(@Param("params") Map<String, Object> params);
    
    /**
     * 统计设备监控数据总数（支持搜索和筛选）
     * @param params 查询参数
     * @return 总数
     */
    int countWithConditions(@Param("params") Map<String, Object> params);
    
    /**
     * 根据状态查询设备列表
     * @param status 设备状态
     * @return 设备监控数据列表
     */
    List<DeviceMonitoringData> selectByStatus(@Param("status") DeviceStatus status);
    
    /**
     * 查询有位置信息的设备列表（用于地图显示）
     * @return 设备监控数据列表
     */
    List<DeviceMonitoringData> selectDevicesWithLocation();
    
    /**
     * 更新设备监控数据
     * @param data 设备监控数据对象
     * @return 影响行数
     */
    int updateById(DeviceMonitoringData data);
    
    /**
     * 更新设备状态
     * @param deviceId 设备ID
     * @param status 新状态
     * @param lastOnlineAt 最后在线时间
     * @return 影响行数
     */
    int updateDeviceStatus(@Param("deviceId") Long deviceId, 
                          @Param("status") DeviceStatus status,
                          @Param("lastOnlineAt") LocalDateTime lastOnlineAt);
    
    /**
     * 批量更新设备状态
     * @param deviceIds 设备ID列表
     * @param status 新状态
     * @return 影响行数
     */
    int batchUpdateStatus(@Param("deviceIds") List<Long> deviceIds, 
                         @Param("status") DeviceStatus status);
    
    /**
     * 删除设备监控数据
     * @param id 监控数据ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据设备ID删除监控数据
     * @param deviceId 设备ID
     * @return 影响行数
     */
    int deleteByDeviceId(@Param("deviceId") Long deviceId);
    
    /**
     * 搜索设备（支持序列号、客户名称、型号模糊搜索）
     * @param keyword 搜索关键词
     * @param limit 限制数量
     * @return 设备监控数据列表
     */
    List<DeviceMonitoringData> searchDevices(@Param("keyword") String keyword, 
                                           @Param("limit") Integer limit);
    
    /**
     * 查询长时间离线的设备
     * @param offlineThreshold 离线时间阈值
     * @return 设备监控数据列表
     */
    List<DeviceMonitoringData> selectLongOfflineDevices(@Param("offlineThreshold") LocalDateTime offlineThreshold);
    
    /**
     * 统计各状态设备数量
     * @return 状态统计Map
     */
    Map<String, Integer> countDevicesByStatus();
    
    /**
     * 同步设备基本信息（从managed_devices表同步）
     * @param deviceId 设备ID
     * @return 影响行数
     */
    int syncDeviceInfo(@Param("deviceId") Long deviceId);
    
    /**
     * 批量同步所有设备信息
     * @return 影响行数
     */
    int batchSyncAllDeviceInfo();
}