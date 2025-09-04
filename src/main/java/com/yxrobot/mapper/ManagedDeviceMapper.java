package com.yxrobot.mapper;

import com.yxrobot.dto.ManagedDeviceSearchCriteria;
import com.yxrobot.entity.ManagedDevice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 设备管理Mapper接口
 * 提供设备管理模块的数据访问功能
 * 支持复杂查询、分页、搜索和筛选
 */
@Mapper
public interface ManagedDeviceMapper {
    
    /**
     * 根据ID查询设备（包含关联信息）
     */
    ManagedDevice selectByIdWithAssociations(@Param("id") Long id);
    
    /**
     * 根据ID查询设备（基本信息）
     */
    ManagedDevice selectById(@Param("id") Long id);
    
    /**
     * 分页查询设备列表（支持搜索和筛选）
     */
    List<ManagedDevice> selectByPage(@Param("offset") Integer offset,
                                   @Param("limit") Integer limit,
                                   @Param("keyword") String keyword,
                                   @Param("status") String status,
                                   @Param("model") String model,
                                   @Param("customerId") Long customerId,
                                   @Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate,
                                   @Param("sortBy") String sortBy,
                                   @Param("sortOrder") String sortOrder);
    
    /**
     * 统计设备总数（支持搜索和筛选）
     */
    Integer countByConditions(@Param("keyword") String keyword,
                            @Param("status") String status,
                            @Param("model") String model,
                            @Param("customerId") Long customerId,
                            @Param("startDate") LocalDateTime startDate,
                            @Param("endDate") LocalDateTime endDate);
    
    /**
     * 统计设备状态分布
     */
    List<StatusCountDTO> countByStatus();
    
    /**
     * 统计设备型号分布
     */
    List<ModelCountDTO> countByModel();
    
    /**
     * 插入设备
     */
    int insert(ManagedDevice device);
    
    /**
     * 更新设备
     */
    int updateById(ManagedDevice device);
    
    /**
     * 软删除设备
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 批量软删除设备
     */
    int deleteBatchByIds(@Param("ids") List<Long> ids);
    
    /**
     * 更新设备状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    
    /**
     * 批量更新设备状态
     */
    int updateStatusBatch(@Param("ids") List<Long> ids, @Param("status") String status);
    
    /**
     * 根据序列号查询设备
     */
    ManagedDevice selectBySerialNumber(@Param("serialNumber") String serialNumber);
    
    /**
     * 根据客户ID查询设备列表
     */
    List<ManagedDevice> selectByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 检查序列号是否存在（排除指定ID）
     */
    boolean existsSerialNumber(@Param("serialNumber") String serialNumber, @Param("excludeId") Long excludeId);
    
    /**
     * 获取设备状态
     */
    String getDeviceStatus(@Param("id") Long id);
    
    /**
     * 高级搜索设备
     */
    List<ManagedDevice> advancedSearch(ManagedDeviceSearchCriteria criteria);
    
    /**
     * 统计高级搜索结果数量
     */
    Integer countAdvancedSearch(ManagedDeviceSearchCriteria criteria);
    
    /**
     * 获取搜索建议
     */
    List<String> getSearchSuggestions(@Param("field") String field, @Param("query") String query, @Param("limit") Integer limit);
    
    /**
     * 快速搜索设备
     */
    List<ManagedDevice> quickSearch(@Param("keyword") String keyword, 
                                  @Param("offset") Integer offset, 
                                  @Param("limit") Integer limit);
    
    /**
     * 快速搜索设备总数
     */
    Integer countQuickSearch(@Param("keyword") String keyword);
    
    /**
     * 按状态筛选设备
     */
    List<ManagedDevice> filterByStatus(@Param("statuses") List<String> statuses,
                                     @Param("offset") Integer offset,
                                     @Param("limit") Integer limit);
    
    /**
     * 按状态统计设备数量
     */
    Integer countByStatus(@Param("statuses") List<String> statuses);
    
    /**
     * 按型号筛选设备
     */
    List<ManagedDevice> filterByModel(@Param("models") List<String> models,
                                    @Param("offset") Integer offset,
                                    @Param("limit") Integer limit);
    
    /**
     * 按型号统计设备数量
     */
    Integer countByModel(@Param("models") List<String> models);
    
    /**
     * 按客户筛选设备
     */
    List<ManagedDevice> filterByCustomer(@Param("customerIds") List<Long> customerIds,
                                       @Param("offset") Integer offset,
                                       @Param("limit") Integer limit);
    
    /**
     * 按客户统计设备数量
     */
    Integer countByCustomer(@Param("customerIds") List<Long> customerIds);
    
    /**
     * 按时间范围筛选设备
     */
    List<ManagedDevice> filterByDateRange(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate,
                                        @Param("dateField") String dateField,
                                        @Param("offset") Integer offset,
                                        @Param("limit") Integer limit);
    
    /**
     * 按时间范围统计设备数量
     */
    Integer countByDateRange(@Param("startDate") LocalDateTime startDate,
                           @Param("endDate") LocalDateTime endDate,
                           @Param("dateField") String dateField);
    

    
    /**
     * 获取客户选项
     */
    List<Map<String, Object>> getCustomerOptions();
    
    /**
     * 优化的高级搜索设备
     */
    List<ManagedDevice> advancedSearchOptimized(ManagedDeviceSearchCriteria criteria);
    
    /**
     * 优化的搜索结果统计
     */
    Integer countAdvancedSearchOptimized(ManagedDeviceSearchCriteria criteria);
    
    /**
     * 多条件组合搜索
     */
    List<ManagedDevice> multiConditionSearch(@Param("criteria") ManagedDeviceSearchCriteria criteria);
    
    /**
     * 多条件组合搜索统计
     */
    Integer countMultiConditionSearch(@Param("criteria") ManagedDeviceSearchCriteria criteria);
    
    /**
     * 智能搜索（支持模糊匹配和权重排序）
     */
    List<ManagedDevice> intelligentSearch(@Param("keyword") String keyword,
                                        @Param("offset") Integer offset,
                                        @Param("limit") Integer limit);
    
    /**
     * 智能搜索统计
     */
    Integer countIntelligentSearch(@Param("keyword") String keyword);
    
    /**
     * 获取搜索历史统计
     */
    List<Map<String, Object>> getSearchHistoryStats(@Param("limit") Integer limit);
    
    /**
     * 获取热门搜索关键词
     */
    List<String> getPopularSearchKeywords(@Param("limit") Integer limit);
    
    /**
     * 获取搜索过滤器选项
     */
    Map<String, List<String>> getSearchFilterOptions();
    
    /**
     * 按固件版本范围筛选设备
     */
    List<ManagedDevice> filterByFirmwareVersionRange(@Param("minVersion") String minVersion,
                                                   @Param("maxVersion") String maxVersion,
                                                   @Param("offset") Integer offset,
                                                   @Param("limit") Integer limit);
    
    /**
     * 按固件版本范围统计设备数量
     */
    Integer countByFirmwareVersionRange(@Param("minVersion") String minVersion,
                                      @Param("maxVersion") String maxVersion);
    
    /**
     * 获取在线设备列表
     */
    List<ManagedDevice> getOnlineDevices(@Param("offset") Integer offset,
                                       @Param("limit") Integer limit);
    
    /**
     * 统计在线设备数量
     */
    Integer countOnlineDevices();
    
    /**
     * 获取离线设备列表
     */
    List<ManagedDevice> getOfflineDevices(@Param("offset") Integer offset,
                                        @Param("limit") Integer limit);
    
    /**
     * 统计离线设备数量
     */
    Integer countOfflineDevices();
    
    /**
     * 获取故障设备列表
     */
    List<ManagedDevice> getFaultDevices(@Param("offset") Integer offset,
                                      @Param("limit") Integer limit);
    
    /**
     * 统计故障设备数量
     */
    Integer countFaultDevices();
    
    /**
     * 获取维护中设备列表
     */
    List<ManagedDevice> getMaintenanceDevices(@Param("offset") Integer offset,
                                            @Param("limit") Integer limit);
    
    /**
     * 统计维护中设备数量
     */
    Integer countMaintenanceDevices();
    
    /**
     * 获取未激活设备列表
     */
    List<ManagedDevice> getUnactivatedDevices(@Param("offset") Integer offset,
                                            @Param("limit") Integer limit);
    
    /**
     * 统计未激活设备数量
     */
    Integer countUnactivatedDevices();
    
    /**
     * 获取最近在线设备列表
     */
    List<ManagedDevice> getRecentlyOnlineDevices(@Param("hours") Integer hours,
                                               @Param("offset") Integer offset,
                                               @Param("limit") Integer limit);
    
    /**
     * 统计最近在线设备数量
     */
    Integer countRecentlyOnlineDevices(@Param("hours") Integer hours);
    
    /**
     * 状态统计DTO
     */
    class StatusCountDTO {
        private String status;
        private Integer count;
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Integer getCount() { return count; }
        public void setCount(Integer count) { this.count = count; }
    }
    
    /**
     * 型号统计DTO
     */
    class ModelCountDTO {
        private String model;
        private Integer count;
        
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public Integer getCount() { return count; }
        public void setCount(Integer count) { this.count = count; }
    }
}