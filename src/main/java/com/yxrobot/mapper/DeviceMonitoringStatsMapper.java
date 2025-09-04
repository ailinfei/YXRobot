package com.yxrobot.mapper;

import com.yxrobot.entity.DeviceMonitoringStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 设备监控统计Mapper接口
 * 对应device_monitoring_stats表的数据访问操作
 * 
 * 字段映射规范：
 * - 数据库字段使用snake_case命名（如：online_count, avg_performance）
 * - Java属性使用camelCase命名（如：onlineCount, avgPerformance）
 * - XML映射文件中确保column和property正确对应
 * 
 * @author YXRobot Development Team
 * @since 2024
 */
@Mapper
public interface DeviceMonitoringStatsMapper {
    
    /**
     * 插入监控统计记录
     * @param stats 监控统计对象
     * @return 影响行数
     */
    int insert(DeviceMonitoringStats stats);
    
    /**
     * 根据ID查询监控统计
     * @param id 统计ID
     * @return 监控统计对象
     */
    DeviceMonitoringStats selectById(@Param("id") Long id);
    
    /**
     * 根据统计日期查询监控统计
     * @param statsDate 统计日期
     * @return 监控统计对象
     */
    DeviceMonitoringStats selectByStatsDate(@Param("statsDate") LocalDate statsDate);
    
    /**
     * 查询最新的监控统计
     * @return 最新的监控统计对象
     */
    DeviceMonitoringStats selectLatest();
    
    /**
     * 查询指定日期范围内的监控统计列表
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 监控统计列表
     */
    List<DeviceMonitoringStats> selectByDateRange(@Param("startDate") LocalDate startDate, 
                                                 @Param("endDate") LocalDate endDate);
    
    /**
     * 更新监控统计
     * @param stats 监控统计对象
     * @return 影响行数
     */
    int updateById(DeviceMonitoringStats stats);
    
    /**
     * 删除监控统计
     * @param id 统计ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 计算实时监控统计数据
     * 基于managed_devices表的当前状态计算统计
     * @return 实时监控统计对象
     */
    DeviceMonitoringStats calculateRealTimeStats();
    
    /**
     * 批量插入监控统计记录
     * @param statsList 监控统计列表
     * @return 影响行数
     */
    int batchInsert(@Param("list") List<DeviceMonitoringStats> statsList);
    
    /**
     * 查询历史趋势数据（用于计算趋势变化）
     * @param currentDate 当前日期
     * @param daysBefore 之前的天数
     * @return 历史统计对象
     */
    DeviceMonitoringStats selectHistoryForTrend(@Param("currentDate") LocalDate currentDate, 
                                               @Param("daysBefore") int daysBefore);
    
    /**
     * 清理过期的统计数据
     * @param beforeDate 清理此日期之前的数据
     * @return 影响行数
     */
    int deleteExpiredStats(@Param("beforeDate") LocalDate beforeDate);
}