package com.yxrobot.mapper;

import com.yxrobot.dto.ServiceRecordDTO;
import com.yxrobot.entity.CustomerServiceRecord;
import com.yxrobot.entity.ServiceRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 客户服务记录关联Mapper接口
 * 对应数据库表：customer_service_relation
 * 遵循项目关联表设计规范：不使用外键约束，通过关联表实现表间关系
 * 
 * 字段映射规范：
 * - 数据库字段使用snake_case命名（如：customer_role, relation_notes, created_time）
 * - Java属性使用camelCase命名（如：customerRole, relationNotes, createdTime）
 * - MyBatis映射文件中确保column和property正确对应
 */
@Mapper
public interface CustomerServiceRecordMapper {
    
    // ==================== 基础CRUD操作 ====================
    
    /**
     * 根据ID查询客户服务记录关联
     */
    CustomerServiceRecord selectById(@Param("id") Long id);
    
    /**
     * 根据ID查询客户服务记录关联DTO（包含服务记录详细信息）
     */
    ServiceRecordDTO selectDTOById(@Param("id") Long id);
    
    /**
     * 根据客户ID查询服务记录关联列表
     */
    List<CustomerServiceRecord> selectByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 根据客户ID查询服务记录关联DTO列表（适配前端页面需求）
     */
    List<ServiceRecordDTO> selectDTOByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 根据客户ID查询客户服务记录列表（兼容方法）
     */
    List<ServiceRecordDTO> selectCustomerServiceRecordsByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 根据服务记录ID查询客户关联列表
     */
    List<CustomerServiceRecord> selectByServiceId(@Param("serviceId") Long serviceId);
    
    /**
     * 插入客户服务记录关联
     */
    int insert(CustomerServiceRecord customerServiceRecord);
    
    /**
     * 更新客户服务记录关联
     */
    int updateById(CustomerServiceRecord customerServiceRecord);
    
    /**
     * 根据ID删除客户服务记录关联（设置status=0）
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据客户ID和服务记录ID删除关联
     */
    int deleteByCustomerAndService(@Param("customerId") Long customerId, @Param("serviceId") Long serviceId);
    
    // ==================== 关联查询功能 ====================
    
    /**
     * 查询客户的所有服务记录（通过关联表）
     */
    List<ServiceRecord> selectServiceRecordsByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 查询服务记录的所有客户（通过关联表）
     */
    List<com.yxrobot.entity.Customer> selectCustomersByServiceId(@Param("serviceId") Long serviceId);
    
    /**
     * 查询客户的维护服务记录列表
     */
    List<ServiceRecordDTO> selectMaintenanceServicesByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 查询客户的升级服务记录列表
     */
    List<ServiceRecordDTO> selectUpgradeServicesByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 查询客户的咨询服务记录列表
     */
    List<ServiceRecordDTO> selectConsultationServicesByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 查询客户的投诉处理记录列表
     */
    List<ServiceRecordDTO> selectComplaintServicesByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 查询客户的进行中服务记录列表
     */
    List<ServiceRecordDTO> selectInProgressServicesByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 查询客户的已完成服务记录列表
     */
    List<ServiceRecordDTO> selectCompletedServicesByCustomerId(@Param("customerId") Long customerId);
    
    // ==================== 统计查询功能 ====================
    
    /**
     * 查询客户服务记录统计信息
     */
    Map<String, Object> selectCustomerServiceStats(@Param("customerId") Long customerId);
    
    /**
     * 查询客户服务记录数量统计
     */
    Map<String, Object> selectCustomerServiceCount(@Param("customerId") Long customerId);
    
    /**
     * 查询客户服务费用统计
     */
    Map<String, Object> selectCustomerServiceCostStats(@Param("customerId") Long customerId);
    
    /**
     * 查询服务类型分布统计
     */
    List<Map<String, Object>> selectServiceTypeDistribution(@Param("customerId") Long customerId);
    
    /**
     * 查询服务状态分布统计
     */
    List<Map<String, Object>> selectServiceStatusDistribution(@Param("customerId") Long customerId);
    
    /**
     * 查询服务优先级分布统计
     */
    List<Map<String, Object>> selectServicePriorityDistribution(@Param("customerId") Long customerId);
    
    /**
     * 查询客户月度服务统计
     */
    List<Map<String, Object>> selectMonthlyServiceStats(@Param("customerId") Long customerId, 
                                                        @Param("months") Integer months);
    
    // ==================== 服务管理功能 ====================
    
    /**
     * 查询客户最近的服务记录
     */
    List<ServiceRecordDTO> selectRecentServicesByCustomerId(@Param("customerId") Long customerId, 
                                                           @Param("limit") Integer limit);
    
    /**
     * 查询客户的高优先级服务记录
     */
    List<ServiceRecordDTO> selectHighPriorityServices(@Param("customerId") Long customerId);
    
    /**
     * 查询客户的紧急服务记录
     */
    List<ServiceRecordDTO> selectUrgentServices(@Param("customerId") Long customerId);
    
    /**
     * 查询指定日期范围内的客户服务记录
     */
    List<ServiceRecordDTO> selectServicesByDateRange(@Param("customerId") Long customerId,
                                                    @Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate);
    
    /**
     * 查询需要跟进的服务记录（进行中状态）
     */
    List<ServiceRecordDTO> selectServicesNeedingFollowUp(@Param("customerId") Long customerId);
    
    /**
     * 查询超时未解决的服务记录
     */
    List<ServiceRecordDTO> selectOverdueServices(@Param("customerId") Long customerId, 
                                                @Param("days") Integer days);
    
    /**
     * 更新服务记录关联状态
     */
    int updateRelationStatus(@Param("id") Long id, @Param("status") Integer status);
    
    /**
     * 批量更新服务记录关联状态
     */
    int batchUpdateRelationStatus(@Param("ids") List<Long> ids, @Param("status") Integer status);
    
    // ==================== 数据验证功能 ====================
    
    /**
     * 检查客户服务记录关联是否存在
     */
    boolean existsByCustomerAndService(@Param("customerId") Long customerId, 
                                      @Param("serviceId") Long serviceId, 
                                      @Param("customerRole") String customerRole);
    
    /**
     * 检查服务记录是否已被其他客户关联
     */
    boolean isServiceOccupied(@Param("serviceId") Long serviceId, 
                             @Param("customerRole") String customerRole);
    
    /**
     * 查询客户服务记录关联数量
     */
    int countByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 查询服务记录关联数量
     */
    int countByServiceId(@Param("serviceId") Long serviceId);
    
    // ==================== 高级查询功能 ====================
    
    /**
     * 查询客户服务记录历史（包括已删除的关联）
     */
    List<ServiceRecordDTO> selectCustomerServiceHistory(@Param("customerId") Long customerId);
    
    /**
     * 查询服务记录流转历史
     */
    List<ServiceRecordDTO> selectServiceTransferHistory(@Param("serviceId") Long serviceId);
    
    /**
     * 查询客户的重复服务记录（相同类型的多次服务）
     */
    List<ServiceRecordDTO> selectRepeatServices(@Param("customerId") Long customerId);
    
    /**
     * 查询客户的服务趋势数据
     */
    List<Map<String, Object>> selectCustomerServiceTrend(@Param("customerId") Long customerId, 
                                                         @Param("months") Integer months);
    
    /**
     * 查询客户的平均服务费用
     */
    BigDecimal selectAverageServiceCost(@Param("customerId") Long customerId);
    
    /**
     * 查询客户的服务频率（平均间隔天数）
     */
    Double selectServiceFrequency(@Param("customerId") Long customerId);
    
    /**
     * 查询客户的服务满意度统计
     */
    Map<String, Object> selectServiceSatisfactionStats(@Param("customerId") Long customerId);
    
    /**
     * 查询客户的服务响应时间统计
     */
    Map<String, Object> selectServiceResponseTimeStats(@Param("customerId") Long customerId);
}