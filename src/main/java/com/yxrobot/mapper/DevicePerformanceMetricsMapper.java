package com.yxrobot.mapper;

import com.yxrobot.entity.DevicePerformanceMetrics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 设备性能指标Mapper接口
 * 对应device_performance_metrics表的数据访问操作
 * 
 * 字段映射规范：
 * - 数据库字段使用snake_case命名（如：cpu_usage, memory_usage）
 * - Java属性使用camelCase命名（如：cpuUsage, memoryUsage）
 * - XML映射文件中确保column和property正确对应
 * 
 * @author YXRobot Development Team
 * @since 2024
 */
@Mapper
public interface DevicePerformanceMetricsMapper {
    
    /**
     * 插入设备性能指标
     * @param metrics 设备性能指标对象
     * @return 影响行数
     */
    int insert(DevicePerformanceMetrics metrics);
    
    /**
     * 根据ID查询设备性能指标
     * @param id 指标ID
     * @return 设备性能指标对象
     */
    DevicePerformanceMetrics selectById(@Param("id") Long id);
    
    /**
     * 根据设备ID查询最新性能指标
     * @param deviceId 设备ID
     * @return 设备性能指标对象
     */
    DevicePerformanceMetrics selectLatestByDeviceId(@Param("deviceId") Long deviceId);
    
    /**
     * 根据设备ID查询性能指标历史记录
     * @param deviceId 设备ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制数量
     * @return 设备性能指标列表
     */
    List<DevicePerformanceMetrics> selectHistoryByDeviceId(@Param("deviceId") Long deviceId,
                                                          @Param("startTime") LocalDateTime startTime,
                                                          @Param("endTime") LocalDateTime endTime,
                                                          @Param("limit") Integer limit);
    
    /**
     * 分页查询设备性能指标列表
     * @param params 查询参数
     * @return 设备性能指标列表
     */
    List<DevicePerformanceMetrics> selectWithPagination(@Param("params") Map<String, Object> params);
    
    /**
     * 统计设备性能指标总数
     * @param params 查询参数
     * @return 总数
     */
    int countWithConditions(@Param("params") Map<String, Object> params);
    
    /**
     * 查询高CPU使用率的设备
     * @param cpuThreshold CPU使用率阈值
     * @param limit 限制数量
     * @return 设备性能指标列表
     */
    List<DevicePerformanceMetrics> selectHighCpuUsage(@Param("cpuThreshold") BigDecimal cpuThreshold,
                                                      @Param("limit") Integer limit);
    
    /**
     * 查询高内存使用率的设备
     * @param memoryThreshold 内存使用率阈值
     * @param limit 限制数量
     * @return 设备性能指标列表
     */
    List<DevicePerformanceMetrics> selectHighMemoryUsage(@Param("memoryThreshold") BigDecimal memoryThreshold,
                                                         @Param("limit") Integer limit);
    
    /**
     * 查询低电池电量的设备
     * @param batteryThreshold 电池电量阈值
     * @param limit 限制数量
     * @return 设备性能指标列表
     */
    List<DevicePerformanceMetrics> selectLowBattery(@Param("batteryThreshold") BigDecimal batteryThreshold,
                                                    @Param("limit") Integer limit);
    
    /**
     * 查询高温设备
     * @param temperatureThreshold 温度阈值
     * @param limit 限制数量
     * @return 设备性能指标列表
     */
    List<DevicePerformanceMetrics> selectHighTemperature(@Param("temperatureThreshold") BigDecimal temperatureThreshold,
                                                         @Param("limit") Integer limit);
    
    /**
     * 更新设备性能指标
     * @param metrics 设备性能指标对象
     * @return 影响行数
     */
    int updateById(DevicePerformanceMetrics metrics);
    
    /**
     * 删除设备性能指标
     * @param id 指标ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据设备ID删除所有性能指标
     * @param deviceId 设备ID
     * @return 影响行数
     */
    int deleteByDeviceId(@Param("deviceId") Long deviceId);
    
    /**
     * 批量插入设备性能指标
     * @param metricsList 设备性能指标列表
     * @return 影响行数
     */
    int batchInsert(@Param("list") List<DevicePerformanceMetrics> metricsList);
    
    /**
     * 计算设备平均性能指标
     * @param deviceId 设备ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 平均性能指标Map
     */
    Map<String, BigDecimal> calculateAverageMetrics(@Param("deviceId") Long deviceId,
                                                   @Param("startTime") LocalDateTime startTime,
                                                   @Param("endTime") LocalDateTime endTime);
    
    /**
     * 计算所有设备的平均性能
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 平均性能值
     */
    BigDecimal calculateOverallAveragePerformance(@Param("startTime") LocalDateTime startTime,
                                                 @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查询性能图表数据（按时间间隔聚合）
     * @param deviceId 设备ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param intervalMinutes 时间间隔（分钟）
     * @return 图表数据列表
     */
    List<Map<String, Object>> selectChartData(@Param("deviceId") Long deviceId,
                                             @Param("startTime") LocalDateTime startTime,
                                             @Param("endTime") LocalDateTime endTime,
                                             @Param("intervalMinutes") Integer intervalMinutes);
    
    /**
     * 查询多设备性能对比数据
     * @param deviceIds 设备ID列表
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 对比数据列表
     */
    List<Map<String, Object>> selectComparisonData(@Param("deviceIds") List<Long> deviceIds,
                                                  @Param("startTime") LocalDateTime startTime,
                                                  @Param("endTime") LocalDateTime endTime);
    
    /**
     * 清理过期的性能指标数据
     * @param beforeTime 清理此时间之前的数据
     * @return 影响行数
     */
    int deleteExpiredMetrics(@Param("beforeTime") LocalDateTime beforeTime);
    
    /**
     * 查询性能异常的设备（CPU或内存使用率异常高）
     * @param cpuThreshold CPU阈值
     * @param memoryThreshold 内存阈值
     * @param recentMinutes 最近几分钟
     * @return 异常设备列表
     */
    List<DevicePerformanceMetrics> selectAbnormalPerformance(@Param("cpuThreshold") BigDecimal cpuThreshold,
                                                            @Param("memoryThreshold") BigDecimal memoryThreshold,
                                                            @Param("recentMinutes") Integer recentMinutes);
    
    /**
     * 统计性能指标分布
     * @param metricType 指标类型（cpu、memory、disk等）
     * @return 分布统计Map
     */
    Map<String, Integer> countMetricsDistribution(@Param("metricType") String metricType);
}