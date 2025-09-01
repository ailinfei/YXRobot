package com.yxrobot.service;

import com.yxrobot.entity.RentalCustomer;
import com.yxrobot.mapper.RentalCustomerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

/**
 * 租赁客户管理服务类
 * 处理客户相关业务逻辑，支持前端客户管理功能
 * 
 * @author Kiro
 * @date 2025-01-28
 */
@Service
public class RentalCustomerService {
    
    private static final Logger logger = LoggerFactory.getLogger(RentalCustomerService.class);
    
    @Autowired
    private RentalCustomerMapper rentalCustomerMapper;
    
    /**
     * 获取客户列表
     * 支持分页、搜索、筛选功能
     * 
     * @param params 查询参数Map
     * @return 客户列表
     */
    public List<RentalCustomer> getCustomerList(Map<String, Object> params) {
        logger.info("开始获取客户列表，参数：{}", params);
        
        try {
            List<RentalCustomer> customerList = rentalCustomerMapper.selectList(params);
            
            logger.info("客户列表获取成功，数量：{}", 
                       customerList != null ? customerList.size() : 0);
            
            return customerList != null ? customerList : List.of();
            
        } catch (Exception e) {
            logger.error("获取客户列表失败", e);
            return List.of();
        }
    }
    
    /**
     * 获取客户总数
     * 支持分页功能
     * 
     * @param params 查询参数Map
     * @return 客户总数
     */
    public Long getCustomerCount(Map<String, Object> params) {
        logger.info("开始获取客户总数，参数：{}", params);
        
        try {
            Long count = rentalCustomerMapper.selectCount(params);
            
            logger.info("客户总数获取成功：{}", count);
            
            return count != null ? count : 0L;
            
        } catch (Exception e) {
            logger.error("获取客户总数失败", e);
            return 0L;
        }
    }
    
    /**
     * 根据ID获取客户详情
     * 
     * @param customerId 客户ID
     * @return 客户详情
     */
    public RentalCustomer getCustomerById(Long customerId) {
        logger.info("开始获取客户详情，客户ID：{}", customerId);
        
        try {
            if (customerId == null) {
                logger.warn("客户ID为空，无法查询客户详情");
                return null;
            }
            
            RentalCustomer customer = rentalCustomerMapper.selectById(customerId);
            
            if (customer != null) {
                logger.info("客户详情获取成功：客户名称={}, 类型={}, 地区={}", 
                           customer.getCustomerName(), customer.getCustomerType(), customer.getRegion());
            } else {
                logger.warn("未找到客户详情，客户ID：{}", customerId);
            }
            
            return customer;
            
        } catch (Exception e) {
            logger.error("获取客户详情失败，客户ID：{}", customerId, e);
            return null;
        }
    }
    
    /**
     * 根据客户名称获取客户详情
     * 
     * @param customerName 客户名称
     * @return 客户详情
     */
    public RentalCustomer getCustomerByName(String customerName) {
        logger.info("开始获取客户详情，客户名称：{}", customerName);
        
        try {
            if (customerName == null || customerName.trim().isEmpty()) {
                logger.warn("客户名称为空，无法查询客户详情");
                return null;
            }
            
            RentalCustomer customer = rentalCustomerMapper.selectByName(customerName.trim());
            
            if (customer != null) {
                logger.info("客户详情获取成功：客户ID={}, 类型={}, 地区={}", 
                           customer.getId(), customer.getCustomerType(), customer.getRegion());
            } else {
                logger.warn("未找到客户详情，客户名称：{}", customerName);
            }
            
            return customer;
            
        } catch (Exception e) {
            logger.error("获取客户详情失败，客户名称：{}", customerName, e);
            return null;
        }
    }
    
    /**
     * 创建新客户
     * 
     * @param customer 客户信息
     * @return 创建结果
     */
    public boolean createCustomer(RentalCustomer customer) {
        logger.info("开始创建新客户：{}", customer.getCustomerName());
        
        try {
            if (customer == null) {
                logger.warn("客户信息为空，无法创建");
                return false;
            }
            
            // 检查客户名称是否已存在
            RentalCustomer existingCustomer = rentalCustomerMapper.selectByName(customer.getCustomerName());
            if (existingCustomer != null) {
                logger.warn("客户名称已存在：{}", customer.getCustomerName());
                return false;
            }
            
            int result = rentalCustomerMapper.insert(customer);
            
            if (result > 0) {
                logger.info("客户创建成功：{}", customer.getCustomerName());
                return true;
            } else {
                logger.warn("客户创建失败：{}", customer.getCustomerName());
                return false;
            }
            
        } catch (Exception e) {
            logger.error("创建客户失败：{}", customer != null ? customer.getCustomerName() : "null", e);
            return false;
        }
    }
    
    /**
     * 更新客户信息
     * 
     * @param customer 客户信息
     * @return 更新结果
     */
    public boolean updateCustomer(RentalCustomer customer) {
        logger.info("开始更新客户信息，客户ID：{}", customer.getId());
        
        try {
            if (customer == null || customer.getId() == null) {
                logger.warn("客户信息或ID为空，无法更新");
                return false;
            }
            
            int result = rentalCustomerMapper.updateById(customer);
            
            if (result > 0) {
                logger.info("客户信息更新成功，客户ID：{}", customer.getId());
                return true;
            } else {
                logger.warn("客户信息更新失败，客户ID：{}", customer.getId());
                return false;
            }
            
        } catch (Exception e) {
            logger.error("更新客户信息失败，客户ID：{}", customer != null ? customer.getId() : "null", e);
            return false;
        }
    }
    
    /**
     * 软删除客户
     * 
     * @param customerId 客户ID
     * @return 删除结果
     */
    public boolean deleteCustomer(Long customerId) {
        logger.info("开始软删除客户，客户ID：{}", customerId);
        
        try {
            if (customerId == null) {
                logger.warn("客户ID为空，无法删除");
                return false;
            }
            
            int result = rentalCustomerMapper.deleteById(customerId);
            
            if (result > 0) {
                logger.info("客户软删除成功，客户ID：{}", customerId);
                return true;
            } else {
                logger.warn("客户软删除失败，客户ID：{}", customerId);
                return false;
            }
            
        } catch (Exception e) {
            logger.error("软删除客户失败，客户ID：{}", customerId, e);
            return false;
        }
    }
    
    /**
     * 获取所有客户类型列表
     * 支持前端筛选功能
     * 
     * @return 客户类型列表
     */
    public List<String> getAllCustomerTypes() {
        logger.info("开始获取所有客户类型列表");
        
        try {
            // 返回预定义的客户类型列表
            List<String> customerTypes = Arrays.asList(
                "individual",    // 个人
                "enterprise",    // 企业
                "institution"    // 机构
            );
            
            logger.info("客户类型列表获取成功，数量：{}", customerTypes.size());
            
            return customerTypes;
            
        } catch (Exception e) {
            logger.error("获取客户类型列表失败", e);
            return List.of();
        }
    }
    
    /**
     * 获取所有客户地区列表
     * 支持前端筛选功能
     * 
     * @return 客户地区列表
     */
    public List<String> getAllCustomerRegions() {
        logger.info("开始获取所有客户地区列表");
        
        try {
            // 从数据库查询客户地区分布
            List<Map<String, Object>> regionDistribution = rentalCustomerMapper.selectCustomerRegionDistribution();
            
            List<String> regions = regionDistribution.stream()
                .map(item -> (String) item.get("region"))
                .filter(region -> region != null && !region.trim().isEmpty())
                .distinct()
                .toList();
            
            logger.info("客户地区列表获取成功，数量：{}", regions.size());
            
            return regions;
            
        } catch (Exception e) {
            logger.error("获取客户地区列表失败", e);
            // 返回默认地区列表
            return Arrays.asList(
                "北京市", "上海市", "广州市", "深圳市", "杭州市", 
                "南京市", "武汉市", "成都市", "西安市", "重庆市"
            );
        }
    }
    
    /**
     * 获取客户类型分布数据
     * 支持前端图表分析功能
     * 
     * @return 客户类型分布数据
     */
    public List<Map<String, Object>> getCustomerTypeDistribution() {
        logger.info("开始获取客户类型分布数据");
        
        try {
            List<Map<String, Object>> distribution = rentalCustomerMapper.selectCustomerTypeDistribution();
            
            logger.info("客户类型分布数据获取成功，类型数量：{}", 
                       distribution != null ? distribution.size() : 0);
            
            return distribution != null ? distribution : List.of();
            
        } catch (Exception e) {
            logger.error("获取客户类型分布数据失败", e);
            return List.of();
        }
    }
    
    /**
     * 获取客户地区分布数据
     * 支持前端图表分析功能
     * 
     * @return 客户地区分布数据
     */
    public List<Map<String, Object>> getCustomerRegionDistribution() {
        logger.info("开始获取客户地区分布数据");
        
        try {
            List<Map<String, Object>> distribution = rentalCustomerMapper.selectCustomerRegionDistribution();
            
            logger.info("客户地区分布数据获取成功，地区数量：{}", 
                       distribution != null ? distribution.size() : 0);
            
            return distribution != null ? distribution : List.of();
            
        } catch (Exception e) {
            logger.error("获取客户地区分布数据失败", e);
            return List.of();
        }
    }
    
    /**
     * 更新客户租赁统计信息
     * 
     * @param customerId 客户ID
     * @param totalRentalAmount 累计租赁金额
     * @param totalRentalDays 累计租赁天数
     * @return 更新结果
     */
    public boolean updateCustomerRentalStats(Long customerId, Double totalRentalAmount, Integer totalRentalDays) {
        logger.info("开始更新客户租赁统计，客户ID：{}，金额：{}，天数：{}", 
                   customerId, totalRentalAmount, totalRentalDays);
        
        try {
            if (customerId == null) {
                logger.warn("客户ID为空，无法更新租赁统计");
                return false;
            }
            
            int result = rentalCustomerMapper.updateRentalStats(customerId, totalRentalAmount, totalRentalDays);
            
            if (result > 0) {
                logger.info("客户租赁统计更新成功，客户ID：{}", customerId);
                return true;
            } else {
                logger.warn("客户租赁统计更新失败，客户ID：{}", customerId);
                return false;
            }
            
        } catch (Exception e) {
            logger.error("更新客户租赁统计失败，客户ID：{}", customerId, e);
            return false;
        }
    }
    
    /**
     * 获取活跃客户数量
     * 
     * @return 活跃客户数量
     */
    public Long getActiveCustomerCount() {
        logger.info("开始获取活跃客户数量");
        
        try {
            Long count = rentalCustomerMapper.selectActiveCustomerCount();
            
            logger.info("活跃客户数量获取成功：{}", count);
            
            return count != null ? count : 0L;
            
        } catch (Exception e) {
            logger.error("获取活跃客户数量失败", e);
            return 0L;
        }
    }
    
    /**
     * 获取新客户数量（指定时间范围内）
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 新客户数量
     */
    public Long getNewCustomerCount(String startDate, String endDate) {
        logger.info("开始获取新客户数量，时间范围：{} 到 {}", startDate, endDate);
        
        try {
            Long count = rentalCustomerMapper.selectNewCustomerCount(startDate, endDate);
            
            logger.info("新客户数量获取成功：{}", count);
            
            return count != null ? count : 0L;
            
        } catch (Exception e) {
            logger.error("获取新客户数量失败", e);
            return 0L;
        }
    }
}