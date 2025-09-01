package com.yxrobot.mapper;

import com.yxrobot.entity.RentalCustomer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * 租赁客户数据访问层接口
 * 
 * @author Kiro
 * @date 2025-01-28
 */
@Mapper
public interface RentalCustomerMapper {
    
    /**
     * 根据ID查询客户
     */
    RentalCustomer selectById(@Param("id") Long id);
    
    /**
     * 根据客户名称查询客户
     */
    RentalCustomer selectByName(@Param("customerName") String customerName);
    
    /**
     * 分页查询客户列表
     */
    List<RentalCustomer> selectList(@Param("params") Map<String, Object> params);
    
    /**
     * 查询客户总数
     */
    Long selectCount(@Param("params") Map<String, Object> params);
    
    /**
     * 插入客户
     */
    int insert(RentalCustomer rentalCustomer);
    
    /**
     * 更新客户
     */
    int updateById(RentalCustomer rentalCustomer);
    
    /**
     * 软删除客户
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 批量软删除客户
     */
    int deleteBatchByIds(@Param("ids") List<Long> ids);
    
    /**
     * 查询客户类型分布
     */
    List<Map<String, Object>> selectCustomerTypeDistribution();
    
    /**
     * 查询客户地区分布
     */
    List<Map<String, Object>> selectCustomerRegionDistribution();
    
    /**
     * 更新客户租赁统计
     */
    int updateRentalStats(@Param("customerId") Long customerId,
                         @Param("totalRentalAmount") Double totalRentalAmount,
                         @Param("totalRentalDays") Integer totalRentalDays);
    
    /**
     * 查询活跃客户数
     */
    Long selectActiveCustomerCount();
    
    /**
     * 查询新客户数（指定时间范围内）
     */
    Long selectNewCustomerCount(@Param("startDate") String startDate, 
                               @Param("endDate") String endDate);
}