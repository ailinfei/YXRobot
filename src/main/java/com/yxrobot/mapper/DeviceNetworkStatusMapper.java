package com.yxrobot.mapper;

import com.yxrobot.entity.DeviceNetworkStatus;
import com.yxrobot.entity.NetworkType;
import com.yxrobot.entity.ConnectionStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 设备网络状态Mapper接口
 * 对应device_network_status表的数据访问操作
 * 
 * 字段映射规范：
 * - 数据库字段使用snake_case命名（如：network_type, signal_strength）
 * - Java属性使用camelCase命名（如：networkType, signalStrength）
 * - XML映射文件中确保column和property正确对应
 * 
 * @author YXRobot Development Team
 * @since 2024
 */
@Mapper
public interface DeviceNetworkStatusMapper {
    
    /**
     * 插入设备网络状态
     * @param networkStatus 设备网络状态对象
     * @return 影响行数
     */
    int insert(DeviceNetworkStatus networkStatus);
    
    /**
     * 根据ID查询设备网络状态
     * @param id 网络状态ID
     * @return 设备网络状态对象
     */
    DeviceNetworkStatus selectById(@Param("id") Long id);
    
    /**
     * 根据设备ID查询最新网络状态
     * @param deviceId 设备ID
     * @return 设备网络状态对象
     */
    DeviceNetworkStatus selectLatestByDeviceId(@Param("deviceId") Long deviceId);
    
    /**
     * 根据设备ID查询网络状态历史记录
     * @param deviceId 设备ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制数量
     * @return 设备网络状态列表
     */
    List<DeviceNetworkStatus> selectHistoryByDeviceId(@Param("deviceId") Long deviceId,
                                                     @Param("startTime") LocalDateTime startTime,
                                                     @Param("endTime") LocalDateTime endTime,
                                                     @Param("limit") Integer limit);
    
    /**
     * 分页查询设备网络状态列表
     * @param params 查询参数
     * @return 设备网络状态列表
     */
    List<DeviceNetworkStatus> selectWithPagination(@Param("params") Map<String, Object> params);
    
    /**
     * 统计设备网络状态总数
     * @param params 查询参数
     * @return 总数
     */
    int countWithConditions(@Param("params") Map<String, Object> params);
    
    /**
     * 根据连接状态查询设备列表
     * @param connectionStatus 连接状态
     * @param limit 限制数量
     * @return 设备网络状态列表
     */
    List<DeviceNetworkStatus> selectByConnectionStatus(@Param("connectionStatus") ConnectionStatus connectionStatus,
                                                      @Param("limit") Integer limit);
    
    /**
     * 根据网络类型查询设备列表
     * @param networkType 网络类型
     * @param limit 限制数量
     * @return 设备网络状态列表
     */
    List<DeviceNetworkStatus> selectByNetworkType(@Param("networkType") NetworkType networkType,
                                                 @Param("limit") Integer limit);
    
    /**
     * 查询信号强度低的设备
     * @param signalThreshold 信号强度阈值
     * @param limit 限制数量
     * @return 设备网络状态列表
     */
    List<DeviceNetworkStatus> selectLowSignalDevices(@Param("signalThreshold") Integer signalThreshold,
                                                     @Param("limit") Integer limit);
    
    /**
     * 查询网络延迟高的设备
     * @param latencyThreshold 延迟阈值（毫秒）
     * @param limit 限制数量
     * @return 设备网络状态列表
     */
    List<DeviceNetworkStatus> selectHighLatencyDevices(@Param("latencyThreshold") Integer latencyThreshold,
                                                       @Param("limit") Integer limit);
    
    /**
     * 查询网速慢的设备
     * @param speedThreshold 网速阈值（Mbps）
     * @param limit 限制数量
     * @return 设备网络状态列表
     */
    List<DeviceNetworkStatus> selectSlowSpeedDevices(@Param("speedThreshold") BigDecimal speedThreshold,
                                                     @Param("limit") Integer limit);
    
    /**
     * 更新设备网络状态
     * @param networkStatus 设备网络状态对象
     * @return 影响行数
     */
    int updateById(DeviceNetworkStatus networkStatus);
    
    /**
     * 更新设备连接状态
     * @param deviceId 设备ID
     * @param connectionStatus 连接状态
     * @param lastConnectedAt 最后连接时间
     * @return 影响行数
     */
    int updateConnectionStatus(@Param("deviceId") Long deviceId,
                              @Param("connectionStatus") ConnectionStatus connectionStatus,
                              @Param("lastConnectedAt") LocalDateTime lastConnectedAt);
    
    /**
     * 批量更新设备连接状态
     * @param deviceIds 设备ID列表
     * @param connectionStatus 连接状态
     * @return 影响行数
     */
    int batchUpdateConnectionStatus(@Param("deviceIds") List<Long> deviceIds,
                                   @Param("connectionStatus") ConnectionStatus connectionStatus);
    
    /**
     * 删除设备网络状态
     * @param id 网络状态ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据设备ID删除所有网络状态
     * @param deviceId 设备ID
     * @return 影响行数
     */
    int deleteByDeviceId(@Param("deviceId") Long deviceId);
    
    /**
     * 批量插入设备网络状态
     * @param networkStatusList 设备网络状态列表
     * @return 影响行数
     */
    int batchInsert(@Param("list") List<DeviceNetworkStatus> networkStatusList);
    
    /**
     * 统计各连接状态设备数量
     * @return 连接状态统计Map
     */
    Map<String, Integer> countDevicesByConnectionStatus();
    
    /**
     * 统计各网络类型设备数量
     * @return 网络类型统计Map
     */
    Map<String, Integer> countDevicesByNetworkType();
    
    /**
     * 计算平均网络性能指标
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 平均网络性能Map
     */
    Map<String, BigDecimal> calculateAverageNetworkMetrics(@Param("startTime") LocalDateTime startTime,
                                                          @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查询网络图表数据（按时间间隔聚合）
     * @param deviceId 设备ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param intervalMinutes 时间间隔（分钟）
     * @return 图表数据列表
     */
    List<Map<String, Object>> selectNetworkChartData(@Param("deviceId") Long deviceId,
                                                    @Param("startTime") LocalDateTime startTime,
                                                    @Param("endTime") LocalDateTime endTime,
                                                    @Param("intervalMinutes") Integer intervalMinutes);
    
    /**
     * 查询网络质量分布统计
     * @return 网络质量分布Map
     */
    Map<String, Integer> selectNetworkQualityDistribution();
    
    /**
     * 查询长时间断开连接的设备
     * @param disconnectedThreshold 断开连接时间阈值
     * @return 设备网络状态列表
     */
    List<DeviceNetworkStatus> selectLongDisconnectedDevices(@Param("disconnectedThreshold") LocalDateTime disconnectedThreshold);
    
    /**
     * 清理过期的网络状态数据
     * @param beforeTime 清理此时间之前的数据
     * @return 影响行数
     */
    int deleteExpiredNetworkStatus(@Param("beforeTime") LocalDateTime beforeTime);
    
    /**
     * 查询网络异常的设备（信号差、延迟高、速度慢）
     * @param signalThreshold 信号强度阈值
     * @param latencyThreshold 延迟阈值
     * @param speedThreshold 速度阈值
     * @param recentMinutes 最近几分钟
     * @return 异常设备列表
     */
    List<DeviceNetworkStatus> selectAbnormalNetworkDevices(@Param("signalThreshold") Integer signalThreshold,
                                                          @Param("latencyThreshold") Integer latencyThreshold,
                                                          @Param("speedThreshold") BigDecimal speedThreshold,
                                                          @Param("recentMinutes") Integer recentMinutes);
    
    /**
     * 根据IP地址查询设备网络状态
     * @param ipAddress IP地址
     * @return 设备网络状态对象
     */
    DeviceNetworkStatus selectByIpAddress(@Param("ipAddress") String ipAddress);
    
    /**
     * 根据MAC地址查询设备网络状态
     * @param macAddress MAC地址
     * @return 设备网络状态对象
     */
    DeviceNetworkStatus selectByMacAddress(@Param("macAddress") String macAddress);
}