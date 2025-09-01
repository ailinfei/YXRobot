package com.yxrobot.service;

import com.yxrobot.entity.Customer;
import com.yxrobot.dto.CustomerDTO;
import com.yxrobot.dto.CustomerQueryDTO;
import com.yxrobot.dto.CustomerCreateDTO;
import com.yxrobot.dto.CustomerUpdateDTO;
import com.yxrobot.dto.CustomerStatsDTO;
import com.yxrobot.mapper.CustomerMapper;
import com.yxrobot.mapper.CustomerAddressMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 客户信息管理服务类
 * 处理客户相关的业务逻辑，支持前端表格功能
 * 
 * 主要功能：
 * 1. 客户信息的CRUD操作（创建、更新、删除）
 * 2. 多条件搜索功能（姓名、电话、邮箱）
 * 3. 多条件筛选功能（等级、设备类型、地区）
 * 4. 排序功能（姓名、消费金额、客户价值、最后活跃时间）
 * 5. 分页查询优化，支持大数据量处理
 * 6. 数据格式与前端Customer接口完全匹配
 */
@Service
@Transactional
public class CustomerService {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);
    
    @Autowired
    private CustomerMapper customerMapper;
    
    @Autowired
    private CustomerAddressMapper customerAddressMapper;
    
    // ==================== 客户列表查询功能（支持前端表格） ====================
    
    /**
     * 获取客户列表（支持前端分页、搜索、筛选需求）
     * 确保返回数据包含完整的客户信息（头像、姓名、等级、电话等）
     * @param queryDTO 查询条件
     * @return 客户列表数据
     */
    public Map<String, Object> getCustomers(CustomerQueryDTO queryDTO) {
        try {
            logger.debug("开始查询客户列表，查询条件: {}", queryDTO);
            
            // 设置默认分页参数
            if (queryDTO == null) {
                queryDTO = new CustomerQueryDTO();
            }
            if (queryDTO.getPage() == null || queryDTO.getPage() < 1) {
                queryDTO.setPage(1);
            }
            if (queryDTO.getPageSize() == null || queryDTO.getPageSize() < 1) {
                queryDTO.setPageSize(20); // 默认每页20条
            }
            
            // 计算偏移量
            queryDTO.setOffset((queryDTO.getPage() - 1) * queryDTO.getPageSize());
            
            // 从数据库查询真实数据
            List<CustomerDTO> customers = customerMapper.selectDTOList(queryDTO);
            Long total = customerMapper.selectCount(queryDTO);
            
            // 处理客户数据，确保字段完整性
            customers = processCustomerList(customers);
            
            Map<String, Object> result = new HashMap<>();
            result.put("list", customers);
            result.put("total", total != null ? total : 0);
            result.put("page", queryDTO.getPage());
            result.put("pageSize", queryDTO.getPageSize());
            result.put("isEmpty", customers.isEmpty());
            
            logger.debug("客户列表查询完成，返回{}条记录，总数{}", customers.size(), total);
            
            return result;
            
        } catch (Exception e) {
            logger.error("查询客户列表失败", e);
            // 返回空结果，确保前端正常显示
            return createEmptyCustomerList(queryDTO);
        }
    }
    
    /**
     * 处理客户列表数据，确保字段完整性
     * @param customers 客户列表
     * @return 处理后的客户列表
     */
    private List<CustomerDTO> processCustomerList(List<CustomerDTO> customers) {
        if (customers == null || customers.isEmpty()) {
            return customers;
        }
        
        for (CustomerDTO customer : customers) {
            // 确保必要字段不为null
            if (customer.getCustomerName() == null) {
                customer.setCustomerName("");
            }
            if (customer.getPhone() == null) {
                customer.setPhone("");
            }
            if (customer.getEmail() == null) {
                customer.setEmail("");
            }
            if (customer.getTotalSpent() == null) {
                customer.setTotalSpent(BigDecimal.ZERO);
            }
            if (customer.getCustomerValue() == null) {
                customer.setCustomerValue(BigDecimal.ZERO);
            }
            
            // 设置默认头像
            if (!StringUtils.hasText(customer.getAvatarUrl())) {
                customer.setAvatarUrl("/default-avatar.png");
            }
            
            // 确保设备数量信息完整
            if (customer.getDeviceCount() == null) {
                CustomerDTO.DeviceCountInfo deviceCount = new CustomerDTO.DeviceCountInfo(0, 0, 0);
                customer.setDeviceCount(deviceCount);
            }
            
            // 确保地址信息完整
            if (customer.getAddressInfo() == null && StringUtils.hasText(customer.getRegion())) {
                String[] regionParts = customer.getRegion().split("-");
                if (regionParts.length >= 2) {
                    CustomerDTO.AddressInfo address = new CustomerDTO.AddressInfo(
                        regionParts[0], regionParts[1], customer.getAddressString()
                    );
                    customer.setAddressInfo(address);
                }
            }
        }
        
        return customers;
    }
    
    /**
     * 创建空的客户列表结果
     * @param queryDTO 查询条件
     * @return 空结果
     */
    private Map<String, Object> createEmptyCustomerList(CustomerQueryDTO queryDTO) {
        Map<String, Object> result = new HashMap<>();
        result.put("list", List.of());
        result.put("total", 0);
        result.put("page", queryDTO != null && queryDTO.getPage() != null ? queryDTO.getPage() : 1);
        result.put("pageSize", queryDTO != null && queryDTO.getPageSize() != null ? queryDTO.getPageSize() : 20);
        result.put("isEmpty", true);
        return result;
    }
    
    /**
     * 获取活跃客户列表
     * @return 活跃客户列表
     */
    public List<CustomerDTO> getActiveCustomers() {
        CustomerQueryDTO queryDTO = new CustomerQueryDTO();
        queryDTO.setIsActive(true);
        queryDTO.setPageSize(100); // 限制返回数量
        return customerMapper.selectDTOList(queryDTO);
    }
    
    /**
     * 根据ID获取客户详细信息（支持前端详情查看）
     * @param id 客户ID
     * @return 客户详细信息
     */
    @Cacheable(value = "customerDetail", key = "#id", unless = "#result == null")
    public CustomerDTO getCustomerById(Long id) {
        try {
            if (id == null || id <= 0) {
                logger.warn("客户ID无效: {}", id);
                return null;
            }
            
            CustomerDTO customer = customerMapper.selectDTOById(id);
            
            if (customer != null) {
                // 处理单个客户数据
                processCustomerList(List.of(customer));
                logger.debug("获取客户详情成功: ID={}, 姓名={}", id, customer.getCustomerName());
            } else {
                logger.warn("客户不存在: ID={}", id);
            }
            
            return customer;
            
        } catch (Exception e) {
            logger.error("获取客户详情失败: ID={}", id, e);
            return null;
        }
    }
    
    // ==================== 新增客户统计功能 ====================
    
    /**
     * 获取客户统计数据（支持前端统计卡片）
     * @return 客户统计数据
     */
    public CustomerStatsDTO getCustomerStats() {
        return customerMapper.selectCustomerStats();
    }
    
    /**
     * 获取客户等级分布统计
     * @return 客户等级分布数据
     */
    public List<Map<String, Object>> getCustomerLevelStats() {
        return customerMapper.selectCustomerLevelStats();
    }
    
    /**
     * 获取客户状态分布统计
     * @return 客户状态分布数据
     */
    public List<Map<String, Object>> getCustomerStatusStats() {
        return customerMapper.selectCustomerStatusStats();
    }
    
    /**
     * 获取地区客户分布统计
     * @return 地区分布数据
     */
    public List<Map<String, Object>> getRegionDistribution() {
        return customerMapper.selectRegionDistribution();
    }
    
    // ==================== 客户CRUD操作（支持前端创建、编辑、删除） ====================
    
    /**
     * 创建客户（支持前端客户创建需求）
     * @param createDTO 客户创建数据
     * @return 创建的客户DTO
     */
    @CacheEvict(value = {"customerStats", "customerLevelDistribution"}, allEntries = true)
    @Transactional
    public CustomerDTO createCustomer(CustomerCreateDTO createDTO) {
        try {
            logger.info("开始创建客户: {}", createDTO.getCustomerName());
            
            // 数据验证
            validateCustomerData(createDTO.getCustomerName(), createDTO.getPhone(), createDTO.getEmail(), null);
            
            // 转换为实体对象
            Customer customer = convertCreateDTOToEntity(createDTO);
            
            // 设置默认值
            setDefaultValues(customer);
            
            // 插入数据库
            customerMapper.insert(customer);
            
            logger.info("客户创建成功: ID={}, 姓名={}", customer.getId(), customer.getCustomerName());
            
            // 返回创建的客户DTO
            return customerMapper.selectDTOById(customer.getId());
            
        } catch (Exception e) {
            logger.error("创建客户失败: {}", createDTO.getCustomerName(), e);
            throw new RuntimeException("创建客户失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 更新客户信息（支持前端客户编辑需求）
     * @param id 客户ID
     * @param updateDTO 客户更新数据
     * @return 更新的客户DTO
     */
    @CacheEvict(value = {"customerDetail", "customerStats"}, key = "#id")
    @Transactional
    public CustomerDTO updateCustomer(Long id, CustomerUpdateDTO updateDTO) {
        try {
            logger.info("开始更新客户: ID={}", id);
            
            // 检查客户是否存在
            Customer existingCustomer = customerMapper.selectById(id);
            if (existingCustomer == null) {
                throw new RuntimeException("客户不存在: ID=" + id);
            }
            
            // 数据验证（排除当前客户）
            if (StringUtils.hasText(updateDTO.getCustomerName())) {
                validateCustomerData(updateDTO.getCustomerName(), updateDTO.getPhone(), updateDTO.getEmail(), id);
            }
            
            // 转换为实体对象
            Customer customer = convertUpdateDTOToEntity(updateDTO, existingCustomer);
            customer.setId(id);
            
            // 更新数据库
            customerMapper.updateById(customer);
            
            logger.info("客户更新成功: ID={}, 姓名={}", id, customer.getCustomerName());
            
            // 返回更新的客户DTO
            return customerMapper.selectDTOById(id);
            
        } catch (Exception e) {
            logger.error("更新客户失败: ID={}", id, e);
            throw new RuntimeException("更新客户失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 删除客户（软删除，支持前端删除需求）
     * @param id 客户ID
     */
    @CacheEvict(value = {"customerDetail", "customerStats", "customerLevelDistribution"}, allEntries = true)
    @Transactional
    public void deleteCustomer(Long id) {
        try {
            logger.info("开始删除客户: ID={}", id);
            
            Customer existingCustomer = customerMapper.selectById(id);
            if (existingCustomer == null) {
                throw new RuntimeException("客户不存在: ID=" + id);
            }
            
            // 软删除客户
            customerMapper.softDeleteById(id);
            
            // 同时软删除相关地址信息
            customerAddressMapper.softDeleteByCustomerId(id);
            
            logger.info("客户删除成功: ID={}, 姓名={}", id, existingCustomer.getCustomerName());
            
        } catch (Exception e) {
            logger.error("删除客户失败: ID={}", id, e);
            throw new RuntimeException("删除客户失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 批量删除客户（软删除）
     * @param ids 客户ID列表
     */
    @CacheEvict(value = {"customerStats", "customerLevelDistribution"}, allEntries = true)
    @Transactional
    public void deleteCustomers(List<Long> ids) {
        try {
            if (ids == null || ids.isEmpty()) {
                logger.warn("批量删除客户：ID列表为空");
                return;
            }
            
            logger.info("开始批量删除客户: 数量={}", ids.size());
            
            // 批量软删除客户
            int deletedCount = customerMapper.softDeleteByIds(ids);
            
            // 批量软删除相关地址信息
            for (Long id : ids) {
                customerAddressMapper.softDeleteByCustomerId(id);
            }
            
            logger.info("批量删除客户成功: 删除数量={}", deletedCount);
            
        } catch (Exception e) {
            logger.error("批量删除客户失败", e);
            throw new RuntimeException("批量删除客户失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 设置客户默认值
     * @param customer 客户实体
     */
    private void setDefaultValues(Customer customer) {
        LocalDateTime now = LocalDateTime.now();
        
        if (customer.getRegisteredAt() == null) {
            customer.setRegisteredAt(now);
        }
        if (customer.getCustomerLevel() == null) {
            customer.setCustomerLevel(com.yxrobot.entity.CustomerLevel.REGULAR);
        }
        if (customer.getCustomerStatus() == null) {
            customer.setCustomerStatus(com.yxrobot.entity.CustomerStatus.ACTIVE);
        }
        if (customer.getTotalSpent() == null) {
            customer.setTotalSpent(BigDecimal.ZERO);
        }
        if (customer.getCustomerValue() == null) {
            customer.setCustomerValue(BigDecimal.ZERO);
        }
        if (customer.getIsActive() == null) {
            customer.setIsActive(true);
        }
        if (customer.getLastActiveAt() == null) {
            customer.setLastActiveAt(now);
        }
    }
    
    /**
     * 转换创建DTO为实体对象
     * @param createDTO 创建DTO
     * @return 客户实体
     */
    private Customer convertCreateDTOToEntity(CustomerCreateDTO createDTO) {
        Customer customer = new Customer();
        customer.setCustomerName(createDTO.getCustomerName());
        customer.setPhone(createDTO.getPhone());
        customer.setEmail(createDTO.getEmail());
        customer.setContactPerson(createDTO.getContactPerson());
        customer.setAvatarUrl(createDTO.getAvatarUrl());
        customer.setCustomerTags(createDTO.getTags());
        customer.setNotes(createDTO.getNotes());
        
        // 设置等级和状态
        if (StringUtils.hasText(createDTO.getCustomerLevel())) {
            customer.setCustomerLevel(com.yxrobot.entity.CustomerLevel.fromCode(createDTO.getCustomerLevel()));
        }
        if (StringUtils.hasText(createDTO.getCustomerStatus())) {
            customer.setCustomerStatus(com.yxrobot.entity.CustomerStatus.fromCode(createDTO.getCustomerStatus()));
        }
        
        // 处理地址信息
        if (createDTO.getAddress() != null) {
            customer.setRegion(createDTO.getAddress().getProvince() + "-" + createDTO.getAddress().getCity());
            customer.setAddress(createDTO.getAddress().getDetail());
        }
        
        return customer;
    }
    
    /**
     * 转换更新DTO为实体对象
     * @param updateDTO 更新DTO
     * @param existingCustomer 现有客户
     * @return 客户实体
     */
    private Customer convertUpdateDTOToEntity(CustomerUpdateDTO updateDTO, Customer existingCustomer) {
        Customer customer = new Customer();
        
        // 只更新非空字段
        customer.setCustomerName(StringUtils.hasText(updateDTO.getCustomerName()) ? 
            updateDTO.getCustomerName() : existingCustomer.getCustomerName());
        customer.setPhone(StringUtils.hasText(updateDTO.getPhone()) ? 
            updateDTO.getPhone() : existingCustomer.getPhone());
        customer.setEmail(StringUtils.hasText(updateDTO.getEmail()) ? 
            updateDTO.getEmail() : existingCustomer.getEmail());
        customer.setContactPerson(updateDTO.getContactPerson() != null ? 
            updateDTO.getContactPerson() : existingCustomer.getContactPerson());
        customer.setAvatarUrl(updateDTO.getAvatarUrl() != null ? 
            updateDTO.getAvatarUrl() : existingCustomer.getAvatarUrl());
        customer.setCustomerTags(updateDTO.getTags() != null ? 
            updateDTO.getTags() : existingCustomer.getCustomerTags());
        customer.setNotes(updateDTO.getNotes() != null ? 
            updateDTO.getNotes() : existingCustomer.getNotes());
        
        // 更新等级和状态
        if (StringUtils.hasText(updateDTO.getCustomerLevel())) {
            customer.setCustomerLevel(com.yxrobot.entity.CustomerLevel.fromCode(updateDTO.getCustomerLevel()));
        } else {
            customer.setCustomerLevel(existingCustomer.getCustomerLevel());
        }
        
        if (StringUtils.hasText(updateDTO.getCustomerStatus())) {
            customer.setCustomerStatus(com.yxrobot.entity.CustomerStatus.fromCode(updateDTO.getCustomerStatus()));
        } else {
            customer.setCustomerStatus(existingCustomer.getCustomerStatus());
        }
        
        // 处理地址信息
        if (updateDTO.getAddress() != null) {
            customer.setRegion(updateDTO.getAddress().getProvince() + "-" + updateDTO.getAddress().getCity());
            customer.setAddress(updateDTO.getAddress().getDetail());
        } else {
            customer.setRegion(existingCustomer.getRegion());
            customer.setAddress(existingCustomer.getAddress());
        }
        
        // 保持其他字段不变
        customer.setTotalSpent(existingCustomer.getTotalSpent());
        customer.setCustomerValue(existingCustomer.getCustomerValue());
        customer.setRegisteredAt(existingCustomer.getRegisteredAt());
        customer.setLastActiveAt(existingCustomer.getLastActiveAt());
        customer.setIsActive(existingCustomer.getIsActive());
        
        return customer;
    }
    
    // ==================== 搜索和筛选功能（支持前端多条件查询） ====================
    
    /**
     * 搜索客户（支持前端实时搜索）
     * 搜索客户姓名、电话、邮箱等字段并实时更新列表
     * @param keyword 搜索关键词
     * @param limit 限制数量
     * @return 客户列表
     */
    @Cacheable(value = "customerSearch", key = "#keyword + '_' + #limit", unless = "#result.isEmpty()")
    public List<CustomerDTO> searchCustomers(String keyword, Integer limit) {
        try {
            if (!StringUtils.hasText(keyword)) {
                return List.of();
            }
            
            if (limit == null || limit <= 0) {
                limit = 50; // 默认限制50条
            }
            
            logger.debug("搜索客户: 关键词={}, 限制={}", keyword, limit);
            
            List<CustomerDTO> customers = customerMapper.searchCustomers(keyword.trim(), limit);
            customers = processCustomerList(customers);
            
            logger.debug("搜索客户完成: 关键词={}, 结果数量={}", keyword, customers.size());
            
            return customers;
            
        } catch (Exception e) {
            logger.error("搜索客户失败: 关键词={}", keyword, e);
            return List.of();
        }
    }
    
    /**
     * 根据客户等级筛选客户（支持前端等级筛选）
     * @param level 客户等级
     * @param queryDTO 查询条件
     * @return 客户列表
     */
    public List<CustomerDTO> getCustomersByLevel(String level, CustomerQueryDTO queryDTO) {
        try {
            if (!StringUtils.hasText(level)) {
                return List.of();
            }
            
            logger.debug("按等级筛选客户: 等级={}", level);
            
            List<CustomerDTO> customers = customerMapper.selectByCustomerLevel(level, queryDTO);
            customers = processCustomerList(customers);
            
            return customers;
            
        } catch (Exception e) {
            logger.error("按等级筛选客户失败: 等级={}", level, e);
            return List.of();
        }
    }
    
    /**
     * 根据地区筛选客户（支持前端地区筛选）
     * @param region 地区
     * @param queryDTO 查询条件
     * @return 客户列表
     */
    public List<CustomerDTO> getCustomersByRegion(String region, CustomerQueryDTO queryDTO) {
        try {
            if (!StringUtils.hasText(region)) {
                return List.of();
            }
            
            logger.debug("按地区筛选客户: 地区={}", region);
            
            List<CustomerDTO> customers = customerMapper.selectByRegion(region, queryDTO);
            customers = processCustomerList(customers);
            
            return customers;
            
        } catch (Exception e) {
            logger.error("按地区筛选客户失败: 地区={}", region, e);
            return List.of();
        }
    }
    
    /**
     * 根据设备类型筛选客户（支持前端设备类型筛选）
     * @param deviceType 设备类型
     * @param queryDTO 查询条件
     * @return 客户列表
     */
    public List<CustomerDTO> getCustomersByDeviceType(String deviceType, CustomerQueryDTO queryDTO) {
        try {
            if (!StringUtils.hasText(deviceType)) {
                return List.of();
            }
            
            logger.debug("按设备类型筛选客户: 设备类型={}", deviceType);
            
            List<CustomerDTO> customers = customerMapper.selectByDeviceType(deviceType, queryDTO);
            customers = processCustomerList(customers);
            
            return customers;
            
        } catch (Exception e) {
            logger.error("按设备类型筛选客户失败: 设备类型={}", deviceType, e);
            return List.of();
        }
    }
    
    /**
     * 多条件组合搜索（支持前端复杂查询）
     * @param queryDTO 查询条件
     * @return 搜索结果
     */
    public Map<String, Object> searchCustomersWithConditions(CustomerQueryDTO queryDTO) {
        try {
            logger.debug("多条件搜索客户: {}", queryDTO);
            
            // 应用多个筛选条件
            return getCustomers(queryDTO);
            
        } catch (Exception e) {
            logger.error("多条件搜索客户失败", e);
            return createEmptyCustomerList(queryDTO);
        }
    }
    
    /**
     * 高级搜索功能（支持复杂筛选条件）
     * @param queryDTO 查询条件
     * @return 搜索结果
     */
    public Map<String, Object> advancedSearch(CustomerQueryDTO queryDTO) {
        try {
            logger.debug("高级搜索客户: {}", queryDTO);
            
            // 设置默认分页参数
            if (queryDTO == null) {
                queryDTO = new CustomerQueryDTO();
            }
            if (queryDTO.getPage() == null || queryDTO.getPage() < 1) {
                queryDTO.setPage(1);
            }
            if (queryDTO.getPageSize() == null || queryDTO.getPageSize() < 1) {
                queryDTO.setPageSize(20);
            }
            
            // 计算偏移量
            queryDTO.setOffset((queryDTO.getPage() - 1) * queryDTO.getPageSize());
            
            // 执行高级搜索
            List<CustomerDTO> customers = customerMapper.selectDTOList(queryDTO);
            Long total = customerMapper.selectCount(queryDTO);
            
            // 处理客户数据
            customers = processCustomerList(customers);
            
            Map<String, Object> result = new HashMap<>();
            result.put("list", customers);
            result.put("total", total != null ? total : 0);
            result.put("page", queryDTO.getPage());
            result.put("pageSize", queryDTO.getPageSize());
            result.put("isEmpty", customers.isEmpty());
            
            // 添加搜索统计信息
            if (queryDTO.hasAdvancedFilters()) {
                result.put("hasAdvancedFilters", true);
                result.put("filterSummary", buildFilterSummary(queryDTO));
            }
            
            logger.debug("高级搜索完成，返回{}条记录，总数{}", customers.size(), total);
            
            return result;
            
        } catch (Exception e) {
            logger.error("高级搜索客户失败", e);
            return createEmptyCustomerList(queryDTO);
        }
    }
    
    /**
     * 构建筛选条件摘要
     * @param queryDTO 查询条件
     * @return 筛选摘要
     */
    private Map<String, Object> buildFilterSummary(CustomerQueryDTO queryDTO) {
        Map<String, Object> summary = new HashMap<>();
        
        if (StringUtils.hasText(queryDTO.getKeyword())) {
            summary.put("keyword", queryDTO.getKeyword());
        }
        if (StringUtils.hasText(queryDTO.getCustomerLevel())) {
            summary.put("level", queryDTO.getCustomerLevel());
        }
        if (StringUtils.hasText(queryDTO.getRegion())) {
            summary.put("region", queryDTO.getRegion());
        }
        if (StringUtils.hasText(queryDTO.getDeviceType())) {
            summary.put("deviceType", queryDTO.getDeviceType());
        }
        if (queryDTO.hasDateRangeFilters()) {
            summary.put("hasDateRange", true);
        }
        if (queryDTO.hasAmountRangeFilters()) {
            summary.put("hasAmountRange", true);
        }
        
        return summary;
    }
    
    /**
     * 按标签筛选客户
     * @param tags 标签列表（逗号分隔）
     * @param queryDTO 查询条件
     * @return 客户列表
     */
    public List<CustomerDTO> getCustomersByTags(String tags, CustomerQueryDTO queryDTO) {
        try {
            if (!StringUtils.hasText(tags)) {
                return List.of();
            }
            
            logger.debug("按标签筛选客户: 标签={}", tags);
            
            if (queryDTO == null) {
                queryDTO = new CustomerQueryDTO();
            }
            queryDTO.setTags(tags);
            
            List<CustomerDTO> customers = customerMapper.selectDTOList(queryDTO);
            customers = processCustomerList(customers);
            
            return customers;
            
        } catch (Exception e) {
            logger.error("按标签筛选客户失败: 标签={}", tags, e);
            return List.of();
        }
    }
    
    /**
     * 按注册时间范围筛选客户
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param queryDTO 查询条件
     * @return 客户列表
     */
    public List<CustomerDTO> getCustomersByRegisteredDateRange(String startDate, String endDate, CustomerQueryDTO queryDTO) {
        try {
            logger.debug("按注册时间范围筛选客户: {}到{}", startDate, endDate);
            
            if (queryDTO == null) {
                queryDTO = new CustomerQueryDTO();
            }
            queryDTO.setRegisteredStartDate(startDate);
            queryDTO.setRegisteredEndDate(endDate);
            
            List<CustomerDTO> customers = customerMapper.selectDTOList(queryDTO);
            customers = processCustomerList(customers);
            
            return customers;
            
        } catch (Exception e) {
            logger.error("按注册时间范围筛选客户失败: {}到{}", startDate, endDate, e);
            return List.of();
        }
    }
    
    /**
     * 按消费金额范围筛选客户
     * @param minSpent 最小消费金额
     * @param maxSpent 最大消费金额
     * @param queryDTO 查询条件
     * @return 客户列表
     */
    public List<CustomerDTO> getCustomersBySpentRange(BigDecimal minSpent, BigDecimal maxSpent, CustomerQueryDTO queryDTO) {
        try {
            logger.debug("按消费金额范围筛选客户: {}到{}", minSpent, maxSpent);
            
            if (queryDTO == null) {
                queryDTO = new CustomerQueryDTO();
            }
            if (minSpent != null) {
                queryDTO.setMinSpent(minSpent.toString());
            }
            if (maxSpent != null) {
                queryDTO.setMaxSpent(maxSpent.toString());
            }
            
            List<CustomerDTO> customers = customerMapper.selectDTOList(queryDTO);
            customers = processCustomerList(customers);
            
            return customers;
            
        } catch (Exception e) {
            logger.error("按消费金额范围筛选客户失败: {}到{}", minSpent, maxSpent, e);
            return List.of();
        }
    }
    
    /**
     * 按客户价值范围筛选客户
     * @param minValue 最小客户价值
     * @param maxValue 最大客户价值
     * @param queryDTO 查询条件
     * @return 客户列表
     */
    public List<CustomerDTO> getCustomersByValueRange(BigDecimal minValue, BigDecimal maxValue, CustomerQueryDTO queryDTO) {
        try {
            logger.debug("按客户价值范围筛选客户: {}到{}", minValue, maxValue);
            
            if (queryDTO == null) {
                queryDTO = new CustomerQueryDTO();
            }
            if (minValue != null) {
                queryDTO.setMinCustomerValue(minValue.toString());
            }
            if (maxValue != null) {
                queryDTO.setMaxCustomerValue(maxValue.toString());
            }
            
            List<CustomerDTO> customers = customerMapper.selectDTOList(queryDTO);
            customers = processCustomerList(customers);
            
            return customers;
            
        } catch (Exception e) {
            logger.error("按客户价值范围筛选客户失败: {}到{}", minValue, maxValue, e);
            return List.of();
        }
    }
    
    /**
     * 智能搜索建议（支持前端搜索提示）
     * @param keyword 搜索关键词
     * @param limit 限制数量
     * @return 搜索建议列表
     */
    public List<Map<String, Object>> getSearchSuggestions(String keyword, Integer limit) {
        try {
            if (!StringUtils.hasText(keyword) || keyword.length() < 2) {
                return List.of();
            }
            
            if (limit == null || limit <= 0) {
                limit = 10;
            }
            
            logger.debug("获取搜索建议: 关键词={}, 限制={}", keyword, limit);
            
            List<CustomerDTO> customers = customerMapper.searchCustomers(keyword.trim(), limit);
            
            return customers.stream()
                    .map(customer -> {
                        Map<String, Object> suggestion = new HashMap<>();
                        suggestion.put("id", customer.getId());
                        suggestion.put("name", customer.getCustomerName());
                        suggestion.put("phone", customer.getPhone());
                        suggestion.put("email", customer.getEmail());
                        suggestion.put("level", customer.getCustomerLevel());
                        suggestion.put("avatar", customer.getAvatarUrl());
                        return suggestion;
                    })
                    .collect(java.util.stream.Collectors.toList());
            
        } catch (Exception e) {
            logger.error("获取搜索建议失败: 关键词={}", keyword, e);
            return List.of();
        }
    }
    
    /**
     * 获取筛选统计信息（支持前端筛选面板）
     * @return 筛选统计信息
     */
    public Map<String, Object> getFilterStatistics() {
        try {
            logger.debug("获取筛选统计信息");
            
            Map<String, Object> statistics = new HashMap<>();
            
            // 客户等级分布
            statistics.put("levelDistribution", customerMapper.selectCustomerLevelStats());
            
            // 地区分布
            statistics.put("regionDistribution", customerMapper.selectRegionDistribution());
            
            // 客户状态分布
            statistics.put("statusDistribution", customerMapper.selectCustomerStatusStats());
            
            // 筛选选项
            statistics.put("levelOptions", customerMapper.getCustomerLevelOptions());
            statistics.put("regionOptions", customerMapper.getRegionOptions());
            statistics.put("industryOptions", customerMapper.getIndustryOptions());
            
            return statistics;
            
        } catch (Exception e) {
            logger.error("获取筛选统计信息失败", e);
            return new HashMap<>();
        }
    }
    
    // ==================== 客户价值和等级管理 ====================
    
    /**
     * 更新客户价值评分
     * @param customerId 客户ID
     * @param customerValue 客户价值评分
     */
    public void updateCustomerValue(Long customerId, BigDecimal customerValue) {
        customerMapper.updateCustomerValue(customerId, customerValue);
    }
    
    /**
     * 更新客户等级
     * @param customerId 客户ID
     * @param customerLevel 客户等级
     */
    public void updateCustomerLevel(Long customerId, String customerLevel) {
        customerMapper.updateCustomerLevel(customerId, customerLevel);
    }
    
    /**
     * 更新客户累计消费金额
     * @param customerId 客户ID
     * @param totalSpent 累计消费金额
     */
    public void updateTotalSpent(Long customerId, BigDecimal totalSpent) {
        customerMapper.updateTotalSpent(customerId, totalSpent);
    }
    
    /**
     * 更新客户最后活跃时间
     * @param customerId 客户ID
     */
    public void updateLastActiveAt(Long customerId) {
        customerMapper.updateLastActiveAt(customerId, LocalDateTime.now());
    }
    
    // ==================== 数据验证功能（支持前端表单验证） ====================
    
    /**
     * 验证客户数据（支持前端表单验证）
     * @param customerName 客户姓名
     * @param phone 电话号码
     * @param email 邮箱
     * @param excludeId 排除的客户ID（更新时使用）
     */
    private void validateCustomerData(String customerName, String phone, String email, Long excludeId) {
        // 验证客户姓名
        if (StringUtils.hasText(customerName)) {
            boolean nameExists = (excludeId != null) ? 
                customerMapper.existsByCustomerNameExcludeId(customerName, excludeId) :
                customerMapper.existsByCustomerName(customerName);
            
            if (nameExists) {
                throw new RuntimeException("客户姓名已存在: " + customerName);
            }
        }
        
        // 验证电话号码
        if (StringUtils.hasText(phone)) {
            // 电话号码格式验证
            if (!phone.matches("^1[3-9]\\d{9}$")) {
                throw new RuntimeException("电话号码格式不正确: " + phone);
            }
            
            boolean phoneExists = (excludeId != null) ? 
                customerMapper.existsByPhoneExcludeId(phone, excludeId) :
                customerMapper.existsByPhone(phone);
            
            if (phoneExists) {
                throw new RuntimeException("电话号码已存在: " + phone);
            }
        }
        
        // 验证邮箱
        if (StringUtils.hasText(email)) {
            // 邮箱格式验证
            if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                throw new RuntimeException("邮箱格式不正确: " + email);
            }
            
            boolean emailExists = (excludeId != null) ? 
                customerMapper.existsByEmailExcludeId(email, excludeId) :
                customerMapper.existsByEmail(email);
            
            if (emailExists) {
                throw new RuntimeException("邮箱已存在: " + email);
            }
        }
    }
    
    /**
     * 检查客户姓名是否存在
     * @param customerName 客户姓名
     * @return 是否存在
     */
    public boolean existsByCustomerName(String customerName) {
        try {
            if (!StringUtils.hasText(customerName)) {
                return false;
            }
            return customerMapper.existsByCustomerName(customerName.trim());
        } catch (Exception e) {
            logger.error("检查客户姓名失败: {}", customerName, e);
            return false;
        }
    }
    
    /**
     * 检查客户姓名是否存在（排除指定ID）
     * @param customerName 客户姓名
     * @param id 排除的客户ID
     * @return 是否存在
     */
    public boolean existsByCustomerNameExcludeId(String customerName, Long id) {
        try {
            if (!StringUtils.hasText(customerName) || id == null) {
                return false;
            }
            return customerMapper.existsByCustomerNameExcludeId(customerName.trim(), id);
        } catch (Exception e) {
            logger.error("检查客户姓名失败: {}, ID={}", customerName, id, e);
            return false;
        }
    }
    
    /**
     * 检查电话号码是否存在
     * @param phone 电话号码
     * @return 是否存在
     */
    public boolean existsByPhone(String phone) {
        try {
            if (!StringUtils.hasText(phone)) {
                return false;
            }
            return customerMapper.existsByPhone(phone.trim());
        } catch (Exception e) {
            logger.error("检查电话号码失败: {}", phone, e);
            return false;
        }
    }
    
    /**
     * 检查邮箱是否存在
     * @param email 邮箱
     * @return 是否存在
     */
    public boolean existsByEmail(String email) {
        try {
            if (!StringUtils.hasText(email)) {
                return false;
            }
            return customerMapper.existsByEmail(email.trim().toLowerCase());
        } catch (Exception e) {
            logger.error("检查邮箱失败: {}", email, e);
            return false;
        }
    }
    
    // ==================== 高级查询功能 ====================
    
    /**
     * 获取高价值客户
     * @param minValue 最小价值评分
     * @param limit 限制数量
     * @return 高价值客户列表
     */
    public List<CustomerDTO> getHighValueCustomers(BigDecimal minValue, Integer limit) {
        return customerMapper.selectHighValueCustomers(minValue, limit);
    }
    
    /**
     * 获取VIP客户列表
     * @param queryDTO 查询条件
     * @return VIP客户列表
     */
    public List<CustomerDTO> getVipCustomers(CustomerQueryDTO queryDTO) {
        return customerMapper.selectVipCustomers(queryDTO);
    }
    
    /**
     * 获取新注册客户
     * @param days 最近天数
     * @param limit 限制数量
     * @return 新注册客户列表
     */
    public List<CustomerDTO> getNewCustomers(Integer days, Integer limit) {
        return customerMapper.selectNewCustomers(days, limit);
    }
    
    // ==================== 筛选选项数据 ====================
    
    /**
     * 获取客户等级筛选选项
     * @return 等级选项列表
     */
    public List<Map<String, Object>> getCustomerLevelOptions() {
        return customerMapper.getCustomerLevelOptions();
    }
    
    /**
     * 获取地区筛选选项
     * @return 地区选项列表
     */
    public List<Map<String, Object>> getRegionOptions() {
        return customerMapper.getRegionOptions();
    }
    
    /**
     * 获取行业筛选选项
     * @return 行业选项列表
     */
    public List<Map<String, Object>> getIndustryOptions() {
        return customerMapper.getIndustryOptions();
    }
    
    /**
     * 从DTO创建客户（兼容方法）
     * @param createDTO 创建客户DTO
     * @return 创建的客户实体
     */
    @CacheEvict(value = {"customerStats", "customerLevelDistribution"}, allEntries = true)
    @Transactional
    public Customer createCustomerFromDTO(CustomerCreateDTO createDTO) {
        try {
            logger.info("开始创建客户: {}", createDTO.getCustomerName());
            
            // 数据验证
            validateCustomerData(createDTO.getCustomerName(), createDTO.getPhone(), createDTO.getEmail(), null);
            
            // 转换为实体对象
            Customer customer = convertCreateDTOToEntity(createDTO);
            
            // 设置默认值
            setDefaultValues(customer);
            
            // 插入数据库
            customerMapper.insert(customer);
            
            logger.info("客户创建成功: ID={}, 姓名={}", customer.getId(), customer.getCustomerName());
            
            // 返回创建的客户实体
            return customer;
            
        } catch (Exception e) {
            logger.error("创建客户失败: {}", createDTO.getCustomerName(), e);
            throw new RuntimeException("创建客户失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 从DTO更新客户（兼容方法）
     * @param id 客户ID
     * @param updateDTO 更新客户DTO
     * @return 更新后的客户实体
     */
    @CacheEvict(value = {"customerDetail", "customerStats"}, key = "#id")
    @Transactional
    public Customer updateCustomerFromDTO(Long id, CustomerUpdateDTO updateDTO) {
        try {
            logger.info("开始更新客户: ID={}", id);
            
            // 检查客户是否存在
            Customer existingCustomer = customerMapper.selectById(id);
            if (existingCustomer == null) {
                throw new RuntimeException("客户不存在: ID=" + id);
            }
            
            // 数据验证（排除当前客户）
            if (StringUtils.hasText(updateDTO.getCustomerName())) {
                validateCustomerData(updateDTO.getCustomerName(), updateDTO.getPhone(), updateDTO.getEmail(), id);
            }
            
            // 转换为实体对象
            Customer customer = convertUpdateDTOToEntity(updateDTO, existingCustomer);
            customer.setId(id);
            
            // 更新数据库
            customerMapper.updateById(customer);
            
            logger.info("客户更新成功: ID={}, 姓名={}", id, customer.getCustomerName());
            
            // 返回更新后的客户实体
            return customer;
            
        } catch (Exception e) {
            logger.error("更新客户失败: ID={}", id, e);
            throw new RuntimeException("更新客户失败: " + e.getMessage(), e);
        }
    }
}