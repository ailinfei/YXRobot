package com.yxrobot.mapper;

import com.yxrobot.dto.CustomerDeviceDTO;
import com.yxrobot.entity.CustomerDevice;
import com.yxrobot.entity.Device;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 客户设备关联Mapper接口
 * 对应数据库表：customer_device_relation
 * 遵循项目关联表设计规范：不使用外键约束，通过关联表实现表间关系
 * 
 * 字段映射规范：
 * - 数据库字段使用snake_case命名（如：relation_type, start_date, daily_rental_fee）
 * - Java属性使用camelCase命名（如：relationType, startDate, dailyRentalFee）
 * - MyBatis映射文件中确保column和property正确对应
 */
@Mapper
public interface CustomerDeviceMapper {
    
    // ==================== 基础CRUD操作 ====================
    
    /**
     * 根据ID查询客户设备关联
     */
    CustomerDevice selectById(@Param("id") Long id);
    
    /**
     * 根据ID查询客户设备关联DTO（包含设备详细信息）
     */
    CustomerDeviceDTO selectDTOById(@Param("id") Long id);
    
    /**
     * 根据客户ID查询设备关联列表
     */
    List<CustomerDevice> selectByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 根据客户ID查询设备关联DTO列表（适配前端页面需求）
     */
    List<CustomerDeviceDTO> selectDTOByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 根据客户ID查询客户设备列表（兼容方法）
     */
    List<CustomerDeviceDTO> selectCustomerDevicesByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 根据客户ID查询购买设备信息（兼容方法）
     */
    List<CustomerDeviceDTO> selectPurchasedDeviceInfoByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 根据设备ID查询客户关联列表
     */
    List<CustomerDevice> selectByDeviceId(@Param("deviceId") Long deviceId);
    
    /**
     * 插入客户设备关联
     */
    int insert(CustomerDevice customerDevice);
    
    /**
     * 更新客户设备关联
     */
    int updateById(CustomerDevice customerDevice);
    
    /**
     * 根据ID删除客户设备关联（设置status=0）
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据客户ID和设备ID删除关联
     */
    int deleteByCustomerAndDevice(@Param("customerId") Long customerId, @Param("deviceId") Long deviceId);
    
    // ==================== 关联查询功能 ====================
    
    /**
     * 查询客户的所有设备（通过关联表）
     */
    List<Device> selectDevicesByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 查询设备的所有客户（通过关联表）
     */
    List<com.yxrobot.entity.Customer> selectCustomersByDeviceId(@Param("deviceId") Long deviceId);
    
    /**
     * 查询客户的购买设备列表
     */
    List<CustomerDeviceDTO> selectPurchasedDevicesByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 查询客户的租赁设备列表
     */
    List<CustomerDeviceDTO> selectRentalDevicesByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 查询客户的活跃设备列表
     */
    List<CustomerDeviceDTO> selectActiveDevicesByCustomerId(@Param("customerId") Long customerId);
    
    // ==================== 统计查询功能 ====================
    
    /**
     * 查询客户设备统计信息
     */
    Map<String, Object> selectCustomerDeviceStats(@Param("customerId") Long customerId);
    
    /**
     * 查询客户设备数量统计
     */
    Map<String, Object> selectCustomerDeviceCount(@Param("customerId") Long customerId);
    
    /**
     * 查询设备类型分布统计
     */
    List<Map<String, Object>> selectDeviceTypeDistribution(@Param("customerId") Long customerId);
    
    /**
     * 查询设备状态分布统计
     */
    List<Map<String, Object>> selectDeviceStatusDistribution(@Param("customerId") Long customerId);
    
    /**
     * 查询租赁设备收入统计
     */
    Map<String, Object> selectRentalRevenueStats(@Param("customerId") Long customerId);
    
    // ==================== 设备管理功能 ====================
    
    /**
     * 查询即将到期的租赁设备
     */
    List<CustomerDeviceDTO> selectExpiringRentalDevices(@Param("days") Integer days);
    
    /**
     * 查询保修即将到期的购买设备
     */
    List<CustomerDeviceDTO> selectExpiringWarrantyDevices(@Param("days") Integer days);
    
    /**
     * 查询需要维护的设备
     */
    List<CustomerDeviceDTO> selectMaintenanceRequiredDevices();
    
    /**
     * 更新设备关联状态
     */
    int updateRelationStatus(@Param("id") Long id, @Param("status") Integer status);
    
    /**
     * 批量更新设备关联状态
     */
    int batchUpdateRelationStatus(@Param("ids") List<Long> ids, @Param("status") Integer status);
    
    // ==================== 数据验证功能 ====================
    
    /**
     * 检查客户设备关联是否存在
     */
    boolean existsByCustomerAndDevice(@Param("customerId") Long customerId, 
                                     @Param("deviceId") Long deviceId, 
                                     @Param("relationType") String relationType);
    
    /**
     * 检查设备是否已被其他客户关联
     */
    boolean isDeviceOccupied(@Param("deviceId") Long deviceId, 
                            @Param("relationType") String relationType);
    
    /**
     * 查询客户设备关联数量
     */
    int countByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 查询设备关联数量
     */
    int countByDeviceId(@Param("deviceId") Long deviceId);
    
    // ==================== 高级查询功能 ====================
    
    /**
     * 查询高价值设备关联（购买价格或租金较高）
     */
    List<CustomerDeviceDTO> selectHighValueDeviceRelations(@Param("minValue") java.math.BigDecimal minValue);
    
    /**
     * 查询长期租赁设备（租赁时间超过指定月数）
     */
    List<CustomerDeviceDTO> selectLongTermRentalDevices(@Param("months") Integer months);
    
    /**
     * 查询客户设备使用历史
     */
    List<CustomerDeviceDTO> selectCustomerDeviceHistory(@Param("customerId") Long customerId);
    
    /**
     * 查询设备流转历史
     */
    List<CustomerDeviceDTO> selectDeviceTransferHistory(@Param("deviceId") Long deviceId);
}