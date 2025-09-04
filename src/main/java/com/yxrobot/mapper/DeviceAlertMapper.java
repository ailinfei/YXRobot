package com.yxrobot.mapper;

import com.yxrobot.entity.DeviceAlert;
import com.yxrobot.entity.AlertLevel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 设备告警Mapper接口
 * 对应device_alerts表的数据访问操作
 * 
 * 字段映射规范：
 * - 数据库字段使用snake_case命名（如：alert_level, alert_message）
 * - Java属性使用camelCase命名（如：alertLevel, alertMessage）
 * - XML映射文件中确保column和property正确对应
 * 
 * @author YXRobot Development Team
 * @since 2024
 */
@Mapper
public interface DeviceAlertMapper {
    
    /**
     * 插入设备告警
     * @param alert 设备告警对象
     * @return 影响行数
     */
    int insert(DeviceAlert alert);
    
    /**
     * 根据ID查询设备告警
     * @param id 告警ID
     * @return 设备告警对象
     */
    DeviceAlert selectById(@Param("id") Long id);
    
    /**
     * 分页查询设备告警列表（支持筛选）
     * @param params 查询参数
     * @return 设备告警列表
     */
    List<DeviceAlert> selectWithPagination(@Param("params") Map<String, Object> params);
    
    /**
     * 统计设备告警总数（支持筛选）
     * @param params 查询参数
     * @return 总数
     */
    int countWithConditions(@Param("params") Map<String, Object> params);
    
    /**
     * 根据设备ID查询告警列表
     * @param deviceId 设备ID
     * @param limit 限制数量
     * @return 设备告警列表
     */
    List<DeviceAlert> selectByDeviceId(@Param("deviceId") Long deviceId, 
                                      @Param("limit") Integer limit);
    
    /**
     * 根据告警级别查询告警列表
     * @param alertLevel 告警级别
     * @param limit 限制数量
     * @return 设备告警列表
     */
    List<DeviceAlert> selectByAlertLevel(@Param("alertLevel") AlertLevel alertLevel, 
                                        @Param("limit") Integer limit);
    
    /**
     * 查询未解决的告警列表
     * @param limit 限制数量
     * @return 设备告警列表
     */
    List<DeviceAlert> selectUnresolved(@Param("limit") Integer limit);
    
    /**
     * 查询最近的告警列表
     * @param hours 最近几小时
     * @param limit 限制数量
     * @return 设备告警列表
     */
    List<DeviceAlert> selectRecent(@Param("hours") int hours, 
                                  @Param("limit") Integer limit);
    
    /**
     * 根据时间范围查询告警列表
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 设备告警列表
     */
    List<DeviceAlert> selectByTimeRange(@Param("startTime") LocalDateTime startTime, 
                                       @Param("endTime") LocalDateTime endTime);
    
    /**
     * 更新设备告警
     * @param alert 设备告警对象
     * @return 影响行数
     */
    int updateById(DeviceAlert alert);
    
    /**
     * 解决告警
     * @param id 告警ID
     * @param resolvedBy 解决人
     * @param resolvedAt 解决时间
     * @return 影响行数
     */
    int resolveAlert(@Param("id") Long id, 
                    @Param("resolvedBy") String resolvedBy, 
                    @Param("resolvedAt") LocalDateTime resolvedAt);
    
    /**
     * 批量解决告警
     * @param ids 告警ID列表
     * @param resolvedBy 解决人
     * @param resolvedAt 解决时间
     * @return 影响行数
     */
    int batchResolveAlerts(@Param("ids") List<Long> ids, 
                          @Param("resolvedBy") String resolvedBy, 
                          @Param("resolvedAt") LocalDateTime resolvedAt);
    
    /**
     * 删除设备告警
     * @param id 告警ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据设备ID删除所有告警
     * @param deviceId 设备ID
     * @return 影响行数
     */
    int deleteByDeviceId(@Param("deviceId") Long deviceId);
    
    /**
     * 统计各级别告警数量
     * @return 级别统计Map
     */
    Map<String, Integer> countAlertsByLevel();
    
    /**
     * 统计设备告警数量
     * @param deviceId 设备ID
     * @return 告警数量统计Map
     */
    Map<String, Integer> countAlertsByDevice(@Param("deviceId") Long deviceId);
    
    /**
     * 查询告警趋势数据（按日期分组）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 趋势数据列表
     */
    List<Map<String, Object>> selectAlertTrend(@Param("startDate") LocalDateTime startDate, 
                                              @Param("endDate") LocalDateTime endDate);
    
    /**
     * 清理过期的已解决告警
     * @param beforeDate 清理此日期之前的数据
     * @return 影响行数
     */
    int deleteResolvedAlertsBefore(@Param("beforeDate") LocalDateTime beforeDate);
    
    /**
     * 查询重复告警（相同设备、相同类型、未解决）
     * @param deviceId 设备ID
     * @param alertType 告警类型
     * @return 设备告警列表
     */
    List<DeviceAlert> selectDuplicateAlerts(@Param("deviceId") Long deviceId, 
                                           @Param("alertType") String alertType);
    
    /**
     * 自动解决过期告警（超过指定时间未处理的低级别告警）
     * @param beforeTime 过期时间阈值
     * @param alertLevel 告警级别
     * @param autoResolvedBy 自动解决标识
     * @return 影响行数
     */
    int autoResolveExpiredAlerts(@Param("beforeTime") LocalDateTime beforeTime, 
                                @Param("alertLevel") AlertLevel alertLevel,
                                @Param("autoResolvedBy") String autoResolvedBy);
}