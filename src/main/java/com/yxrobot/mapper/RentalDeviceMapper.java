package com.yxrobot.mapper;

import com.yxrobot.entity.RentalDevice;
import com.yxrobot.dto.DeviceUtilizationDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * 租赁设备数据访问层接口
 * 
 * @author Kiro
 * @date 2025-01-28
 */
@Mapper
public interface RentalDeviceMapper {
    
    /**
     * 根据ID查询设备
     */
    RentalDevice selectById(@Param("id") Long id);
    
    /**
     * 根据设备编号查询设备
     */
    RentalDevice selectByDeviceId(@Param("deviceId") String deviceId);
    
    /**
     * 分页查询设备列表
     */
    List<RentalDevice> selectList(@Param("params") Map<String, Object> params);
    
    /**
     * 查询设备总数
     */
    Long selectCount(@Param("params") Map<String, Object> params);
    
    /**
     * 查询设备利用率数据（适配前端表格）
     */
    List<DeviceUtilizationDTO> selectDeviceUtilizationList(@Param("params") Map<String, Object> params);
    
    /**
     * 查询设备利用率数据总数
     */
    Long selectDeviceUtilizationCount(@Param("params") Map<String, Object> params);
    
    /**
     * 插入设备
     */
    int insert(RentalDevice rentalDevice);
    
    /**
     * 更新设备
     */
    int updateById(RentalDevice rentalDevice);
    
    /**
     * 软删除设备
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 批量软删除设备
     */
    int deleteBatchByIds(@Param("ids") List<Long> ids);
    
    /**
     * 查询设备状态统计
     */
    Map<String, Object> selectDeviceStatusStats();
    
    /**
     * 查询利用率TOP设备
     */
    List<DeviceUtilizationDTO> selectTopDevicesByUtilization(@Param("limit") Integer limit);
    
    /**
     * 查询设备型号分布
     */
    List<Map<String, Object>> selectDeviceModelDistribution();
    
    /**
     * 查询设备利用率排行数据
     */
    List<Map<String, Object>> selectUtilizationRanking(@Param("limit") Integer limit);
    
    /**
     * 更新设备利用率
     */
    int updateUtilizationRate(@Param("deviceId") Long deviceId, 
                             @Param("utilizationRate") Double utilizationRate,
                             @Param("totalRentalDays") Integer totalRentalDays,
                             @Param("totalAvailableDays") Integer totalAvailableDays);
    
    /**
     * 查询所有设备型号
     */
    List<String> selectAllDeviceModels();
    
    /**
     * 查询所有地区
     */
    List<String> selectAllRegions();
    
    /**
     * 更新设备状态（根据设备编号）
     */
    int updateDeviceStatus(@Param("deviceId") String deviceId, @Param("currentStatus") String currentStatus);
    
    /**
     * 更新设备维护状态（根据设备编号）
     */
    int updateMaintenanceStatus(@Param("deviceId") String deviceId, @Param("maintenanceStatus") String maintenanceStatus);
    
    /**
     * 软删除设备（根据设备编号）
     */
    int softDeleteByDeviceId(@Param("deviceId") String deviceId);
    
    /**
     * 批量更新设备状态
     */
    int batchUpdateDeviceStatus(@Param("deviceIds") List<String> deviceIds, @Param("currentStatus") String currentStatus);
    
    /**
     * 批量更新设备维护状态
     */
    int batchUpdateMaintenanceStatus(@Param("deviceIds") List<String> deviceIds, @Param("maintenanceStatus") String maintenanceStatus);
    
    /**
     * 批量软删除设备（根据设备编号）
     */
    int batchSoftDeleteByDeviceIds(@Param("deviceIds") List<String> deviceIds);
    
    /**
     * 高级搜索设备利用率数据
     */
    List<DeviceUtilizationDTO> selectDeviceUtilizationWithAdvancedSearch(@Param("params") Map<String, Object> params);
    
    /**
     * 高级搜索设备利用率数据总数
     */
    Long selectDeviceUtilizationCountWithAdvancedSearch(@Param("params") Map<String, Object> params);
    
    /**
     * 按时间范围查询设备利用率数据
     */
    List<DeviceUtilizationDTO> selectDeviceUtilizationByDateRange(@Param("params") Map<String, Object> params);
    
    /**
     * 按多个设备型号查询
     */
    List<DeviceUtilizationDTO> selectDeviceUtilizationByModels(@Param("deviceModels") List<String> deviceModels, 
                                                               @Param("params") Map<String, Object> params);
    
    /**
     * 按多个状态查询
     */
    List<DeviceUtilizationDTO> selectDeviceUtilizationByStatuses(@Param("statuses") List<String> statuses, 
                                                                 @Param("params") Map<String, Object> params);
    
    /**
     * 按利用率范围查询
     */
    List<DeviceUtilizationDTO> selectDeviceUtilizationByUtilizationRange(@Param("minUtilization") Double minUtilization,
                                                                         @Param("maxUtilization") Double maxUtilization,
                                                                         @Param("params") Map<String, Object> params);
    
    /**
     * 获取搜索建议（自动完成）
     */
    List<String> selectSearchSuggestions(@Param("keyword") String keyword, @Param("type") String type);
    
    /**
     * 获取筛选选项统计
     */
    Map<String, Object> selectFilterOptionsStats();
}