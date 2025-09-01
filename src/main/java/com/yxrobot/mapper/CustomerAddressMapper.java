package com.yxrobot.mapper;

import com.yxrobot.dto.CustomerAddressDTO;
import com.yxrobot.entity.CustomerAddress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 客户地址Mapper接口
 * 对应数据库表：customer_addresses
 * 
 * 字段映射规范：
 * - 数据库字段使用snake_case命名（如：detail_address, zip_code, is_default）
 * - Java属性使用camelCase命名（如：detailAddress, zipCode, isDefault）
 * - MyBatis映射文件中确保column和property正确对应
 */
@Mapper
public interface CustomerAddressMapper {
    
    // ==================== 基础CRUD操作 ====================
    
    /**
     * 根据ID查询客户地址
     */
    CustomerAddress selectById(@Param("id") Long id);
    
    /**
     * 根据ID查询客户地址DTO
     */
    CustomerAddressDTO selectDTOById(@Param("id") Long id);
    
    /**
     * 根据客户ID查询地址列表
     */
    List<CustomerAddress> selectByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 根据客户ID查询地址DTO列表
     */
    List<CustomerAddressDTO> selectDTOByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 查询客户的默认地址
     */
    CustomerAddress selectDefaultByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 查询客户的默认地址DTO
     */
    CustomerAddressDTO selectDefaultDTOByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 插入客户地址
     */
    int insert(CustomerAddress customerAddress);
    
    /**
     * 更新客户地址
     */
    int updateById(CustomerAddress customerAddress);
    
    /**
     * 根据ID软删除客户地址
     */
    int softDeleteById(@Param("id") Long id);
    
    /**
     * 根据客户ID软删除所有地址
     */
    int softDeleteByCustomerId(@Param("customerId") Long customerId);
    
    // ==================== 地址管理功能 ====================
    
    /**
     * 设置默认地址（先清除其他默认地址，再设置新的默认地址）
     */
    int setDefaultAddress(@Param("customerId") Long customerId, @Param("addressId") Long addressId);
    
    /**
     * 清除客户的所有默认地址标记
     */
    int clearDefaultAddress(@Param("customerId") Long customerId);
    
    /**
     * 查询客户地址数量
     */
    int countByCustomerId(@Param("customerId") Long customerId);
    
    // ==================== 地区统计功能 ====================
    
    /**
     * 查询省份分布统计
     */
    List<java.util.Map<String, Object>> selectProvinceDistribution();
    
    /**
     * 查询城市分布统计
     */
    List<java.util.Map<String, Object>> selectCityDistribution(@Param("province") String province);
    
    /**
     * 查询地区客户数量统计
     */
    List<java.util.Map<String, Object>> selectRegionCustomerCount();
    
    // ==================== 数据验证功能 ====================
    
    /**
     * 检查客户是否已有默认地址
     */
    boolean hasDefaultAddress(@Param("customerId") Long customerId);
    
    /**
     * 检查地址是否属于指定客户
     */
    boolean belongsToCustomer(@Param("addressId") Long addressId, @Param("customerId") Long customerId);
}