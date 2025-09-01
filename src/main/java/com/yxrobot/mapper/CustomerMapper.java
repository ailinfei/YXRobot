package com.yxrobot.mapper;

import com.yxrobot.dto.CustomerDTO;
import com.yxrobot.dto.CustomerQueryDTO;
import com.yxrobot.dto.CustomerStatsDTO;
import com.yxrobot.entity.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 客户Mapper接口
 * 对应数据库表：customers
 * 
 * 字段映射规范：
 * - 数据库字段使用snake_case命名（如：customer_name, customer_level, total_spent）
 * - Java属性使用camelCase命名（如：customerName, customerLevel, totalSpent）
 * - MyBatis映射文件中确保column和property正确对应
 * - 前端接口使用camelCase命名，通过@JsonProperty注解映射
 */
@Mapper
public interface CustomerMapper {
    
    // ==================== 基础CRUD操作 ====================
    
    /**
     * 根据ID查询客户
     */
    Customer selectById(@Param("id") Long id);
    
    /**
     * 根据ID查询客户DTO（包含关联统计信息）
     */
    CustomerDTO selectDTOById(@Param("id") Long id);
    
    /**
     * 分页查询客户列表
     */
    List<Customer> selectList(@Param("query") CustomerQueryDTO query);
    
    /**
     * 分页查询客户DTO列表（适配前端页面需求）
     */
    List<CustomerDTO> selectDTOList(@Param("query") CustomerQueryDTO query);
    
    /**
     * 查询客户总数
     */
    Long selectCount(@Param("query") CustomerQueryDTO query);
    
    /**
     * 查询所有客户总数（兼容方法）
     */
    Long countCustomers();
    
    /**
     * 查询所有客户列表（兼容方法）
     */
    List<Customer> selectAllCustomers();
    
    /**
     * 插入客户
     */
    int insert(Customer customer);
    
    /**
     * 更新客户信息
     */
    int updateById(Customer customer);
    
    /**
     * 根据ID软删除客户
     */
    int softDeleteById(@Param("id") Long id);
    
    /**
     * 批量软删除客户
     */
    int softDeleteByIds(@Param("ids") List<Long> ids);
    
    // ==================== 客户统计功能 ====================
    
    /**
     * 查询客户统计数据（支持前端统计卡片）
     */
    CustomerStatsDTO selectCustomerStats();
    
    /**
     * 查询客户等级分布统计
     */
    List<Map<String, Object>> selectCustomerLevelStats();
    
    /**
     * 查询客户状态分布统计
     */
    List<Map<String, Object>> selectCustomerStatusStats();
    
    /**
     * 查询地区客户分布统计
     */
    List<Map<String, Object>> selectRegionDistribution();
    
    /**
     * 查询本月新增客户数
     */
    Integer selectNewCustomersThisMonth();
    
    /**
     * 查询活跃设备总数
     */
    Integer selectActiveDeviceCount();
    
    /**
     * 查询客户总收入
     */
    BigDecimal selectTotalRevenue();
    
    // ==================== 搜索和筛选功能 ====================
    
    /**
     * 根据关键词搜索客户（姓名、电话、邮箱）
     */
    List<CustomerDTO> searchCustomers(@Param("keyword") String keyword, 
                                     @Param("limit") Integer limit);
    
    /**
     * 根据客户等级筛选客户
     */
    List<CustomerDTO> selectByCustomerLevel(@Param("level") String level, 
                                           @Param("query") CustomerQueryDTO query);
    
    /**
     * 根据地区筛选客户
     */
    List<CustomerDTO> selectByRegion(@Param("region") String region, 
                                    @Param("query") CustomerQueryDTO query);
    
    /**
     * 根据设备类型筛选客户
     */
    List<CustomerDTO> selectByDeviceType(@Param("deviceType") String deviceType, 
                                        @Param("query") CustomerQueryDTO query);
    
    /**
     * 根据注册时间范围筛选客户
     */
    List<CustomerDTO> selectByRegisteredDateRange(@Param("startDate") LocalDate startDate, 
                                                 @Param("endDate") LocalDate endDate, 
                                                 @Param("query") CustomerQueryDTO query);
    
    // ==================== 关联查询功能 ====================
    
    /**
     * 查询客户的设备统计信息
     */
    Map<String, Object> selectCustomerDeviceStats(@Param("customerId") Long customerId);
    
    /**
     * 查询客户的订单统计信息
     */
    Map<String, Object> selectCustomerOrderStats(@Param("customerId") Long customerId);
    
    /**
     * 查询客户的服务记录统计信息
     */
    Map<String, Object> selectCustomerServiceStats(@Param("customerId") Long customerId);
    
    /**
     * 查询客户的完整统计信息（设备、订单、服务记录）
     */
    Map<String, Object> selectCustomerFullStats(@Param("customerId") Long customerId);
    
    // ==================== 客户价值和等级管理 ====================
    
    /**
     * 更新客户价值评分
     */
    int updateCustomerValue(@Param("customerId") Long customerId, 
                           @Param("customerValue") BigDecimal customerValue);
    
    /**
     * 更新客户等级
     */
    int updateCustomerLevel(@Param("customerId") Long customerId, 
                           @Param("customerLevel") String customerLevel);
    
    /**
     * 更新客户累计消费金额
     */
    int updateTotalSpent(@Param("customerId") Long customerId, 
                        @Param("totalSpent") BigDecimal totalSpent);
    
    /**
     * 更新客户最后活跃时间
     */
    int updateLastActiveAt(@Param("customerId") Long customerId, 
                          @Param("lastActiveAt") LocalDateTime lastActiveAt);
    
    /**
     * 批量更新客户等级
     */
    int batchUpdateCustomerLevel(@Param("customerIds") List<Long> customerIds, 
                                @Param("customerLevel") String customerLevel);
    
    // ==================== 数据验证功能 ====================
    
    /**
     * 检查客户姓名是否存在
     */
    boolean existsByCustomerName(@Param("customerName") String customerName);
    
    /**
     * 检查客户姓名是否存在（排除指定ID）
     */
    boolean existsByCustomerNameExcludeId(@Param("customerName") String customerName, 
                                         @Param("id") Long id);
    
    /**
     * 检查电话号码是否存在
     */
    boolean existsByPhone(@Param("phone") String phone);
    
    /**
     * 检查电话号码是否存在（排除指定ID）
     */
    boolean existsByPhoneExcludeId(@Param("phone") String phone, 
                                  @Param("id") Long id);
    
    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(@Param("email") String email);
    
    /**
     * 检查邮箱是否存在（排除指定ID）
     */
    boolean existsByEmailExcludeId(@Param("email") String email, 
                                  @Param("id") Long id);
    
    /**
     * 根据电话号码查询客户
     */
    Customer selectByPhone(@Param("phone") String phone);
    
    /**
     * 根据邮箱查询客户
     */
    Customer selectByEmail(@Param("email") String email);
    
    // ==================== 高级查询功能 ====================
    
    /**
     * 查询高价值客户（客户价值评分 >= 8分）
     */
    List<CustomerDTO> selectHighValueCustomers(@Param("minValue") BigDecimal minValue, 
                                              @Param("limit") Integer limit);
    
    /**
     * 查询活跃客户（最近30天有活动）
     */
    List<CustomerDTO> selectActiveCustomers(@Param("days") Integer days, 
                                           @Param("limit") Integer limit);
    
    /**
     * 查询沉睡客户（超过指定天数无活动）
     */
    List<CustomerDTO> selectInactiveCustomers(@Param("days") Integer days, 
                                             @Param("limit") Integer limit);
    
    /**
     * 查询VIP客户列表
     */
    List<CustomerDTO> selectVipCustomers(@Param("query") CustomerQueryDTO query);
    
    /**
     * 查询新注册客户（最近指定天数）
     */
    List<CustomerDTO> selectNewCustomers(@Param("days") Integer days, 
                                        @Param("limit") Integer limit);
    
    // ==================== 筛选选项数据 ====================
    
    /**
     * 获取客户等级筛选选项
     */
    List<Map<String, Object>> getCustomerLevelOptions();
    
    /**
     * 获取地区筛选选项
     */
    List<Map<String, Object>> getRegionOptions();
    
    /**
     * 获取行业筛选选项
     */
    List<Map<String, Object>> getIndustryOptions();
    
    /**
     * 获取客户标签筛选选项
     */
    List<Map<String, Object>> getCustomerTagOptions();
    
    // ==================== 性能优化查询 ====================
    
    /**
     * 优化的客户列表查询（带索引提示）
     */
    List<CustomerDTO> selectListOptimized(@Param("query") CustomerQueryDTO query);
    
    /**
     * 优化的客户统计查询
     */
    CustomerStatsDTO selectCustomerStatsOptimized();
    
    /**
     * 缓存友好的客户基本信息查询
     */
    List<Map<String, Object>> selectBasicInfoForCache();
}