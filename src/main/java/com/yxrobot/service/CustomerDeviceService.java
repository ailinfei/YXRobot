package com.yxrobot.service;

import com.yxrobot.dto.CustomerDeviceDTO;
import com.yxrobot.entity.CustomerDevice;
import com.yxrobot.mapper.CustomerMapper;
import com.yxrobot.mapper.CustomerDeviceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 客户设备关联服务类
 * 处理客户设备关联的业务逻辑，支持前端详情功能
 * 
 * 主要功能：
 * 1. 获取客户设备列表 - 支持前端详情页显示
 * 2. 设备统计计算 - 设备数量、类型分布等
 * 3. 设备关联管理 - 购买设备、租赁设备管理
 * 4. 设备状态统计 - 活跃、离线、维护状态统计
 * 5. 关联数据格式适配前端详情页面显示
 */
@Service
@Transactional(readOnly = true)
public class CustomerDeviceService {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerDeviceService.class);
    
    @Autowired
    private CustomerMapper customerMapper;
    
    @Autowired
    private CustomerDeviceMapper customerDeviceMapper;
    
    /**
     * 设备信息内部类 - 用于返回给前端
     */
    public static class DeviceInfo {
        private Long deviceId;
        private String deviceName;
        private String deviceType;
        private String deviceModel;
        private String serialNumber;
        private String status;
        private String relationType;
        private BigDecimal purchasePrice;
        private BigDecimal dailyRentalFee;
        private String startDate;
        private String endDate;
        private String warrantyEndDate;
        private Integer healthScore;
        private String lastMaintenanceDate;
        private String location;
        
        // 构造函数
        public DeviceInfo() {}
        
        // Getter和Setter方法
        public Long getDeviceId() { return deviceId; }
        public void setDeviceId(Long deviceId) { this.deviceId = deviceId; }
        public String getDeviceName() { return deviceName; }
        public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
        public String getDeviceType() { return deviceType; }
        public void setDeviceType(String deviceType) { this.deviceType = deviceType; }
        public String getDeviceModel() { return deviceModel; }
        public void setDeviceModel(String deviceModel) { this.deviceModel = deviceModel; }
        public String getSerialNumber() { return serialNumber; }
        public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getRelationType() { return relationType; }
        public void setRelationType(String relationType) { this.relationType = relationType; }
        public BigDecimal getPurchasePrice() { return purchasePrice; }
        public void setPurchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; }
        public BigDecimal getDailyRentalFee() { return dailyRentalFee; }
        public void setDailyRentalFee(BigDecimal dailyRentalFee) { this.dailyRentalFee = dailyRentalFee; }
        public String getStartDate() { return startDate; }
        public void setStartDate(String startDate) { this.startDate = startDate; }
        public String getEndDate() { return endDate; }
        public void setEndDate(String endDate) { this.endDate = endDate; }
        public String getWarrantyEndDate() { return warrantyEndDate; }
        public void setWarrantyEndDate(String warrantyEndDate) { this.warrantyEndDate = warrantyEndDate; }
        public Integer getHealthScore() { return healthScore; }
        public void setHealthScore(Integer healthScore) { this.healthScore = healthScore; }
        public String getLastMaintenanceDate() { return lastMaintenanceDate; }
        public void setLastMaintenanceDate(String lastMaintenanceDate) { this.lastMaintenanceDate = lastMaintenanceDate; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
    }
    
    /**
     * 获取客户的设备列表（支持前端详情页显示）
     * 返回格式与前端CustomerDevice接口完全匹配
     * @param customerId 客户ID
     * @return 客户设备列表
     */
    @Cacheable(value = "customerDevices", key = "#customerId", unless = "#result.isEmpty()")
    public List<CustomerDeviceDTO> getCustomerDevices(Long customerId) {
        try {
            if (customerId == null || customerId <= 0) {
                logger.warn("客户ID无效: {}", customerId);
                return List.of();
            }
            
            logger.debug("获取客户设备列表: customerId={}", customerId);
            
            List<CustomerDeviceDTO> devices = customerDeviceMapper.selectCustomerDevicesByCustomerId(customerId);
            
            if (devices == null) {
                devices = List.of();
            }
            
            logger.debug("客户设备列表获取完成: customerId={}, 设备数量={}", customerId, devices.size());
            
            return devices;
            
        } catch (Exception e) {
            logger.error("获取客户设备列表失败: customerId={}", customerId, e);
            return List.of();
        }
    }
    
    /**
     * 获取客户的设备统计信息（支持前端统计显示）
     * @param customerId 客户ID
     * @return 设备统计信息
     */
    @Cacheable(value = "customerDeviceStats", key = "#customerId")
    public Map<String, Object> getCustomerDeviceStats(Long customerId) {
        try {
            if (customerId == null || customerId <= 0) {
                logger.warn("客户ID无效: {}", customerId);
                return createEmptyDeviceStats();
            }
            
            Map<String, Object> stats = customerDeviceMapper.selectCustomerDeviceStats(customerId);
            
            if (stats == null || stats.isEmpty()) {
                stats = createEmptyDeviceStats();
            }
            
            return stats;
            
        } catch (Exception e) {
            logger.error("获取客户设备统计失败: customerId={}", customerId, e);
            return createEmptyDeviceStats();
        }
    }
    
    /**
     * 创建空的设备统计数据
     * @return 空统计数据
     */
    private Map<String, Object> createEmptyDeviceStats() {
        return Map.of(
            "totalDevices", 0,
            "purchasedDevices", 0,
            "rentalDevices", 0,
            "activeDevices", 0,
            "offlineDevices", 0,
            "maintenanceDevices", 0,
            "totalPurchaseValue", BigDecimal.ZERO,
            "totalDailyRentalFee", BigDecimal.ZERO,
            "avgHealthScore", 0.0
        );
    }
    
    /**
     * 获取客户的购买设备列表（支持前端分类显示）
     * @param customerId 客户ID
     * @return 购买设备列表
     */
    @Cacheable(value = "customerPurchasedDevices", key = "#customerId")
    public List<CustomerDeviceDTO> getCustomerPurchasedDevices(Long customerId) {
        try {
            if (customerId == null || customerId <= 0) {
                return List.of();
            }
            
            logger.debug("获取客户购买设备列表: customerId={}", customerId);
            
            List<CustomerDeviceDTO> devices = customerDeviceMapper.selectPurchasedDeviceInfoByCustomerId(customerId);
            
            return devices != null ? devices : List.of();
            
        } catch (Exception e) {
            logger.error("获取客户购买设备列表失败: customerId={}", customerId, e);
            return List.of();
        }
    }
    
    /**
     * 获取客户的租赁设备列表（支持前端分类显示）
     * @param customerId 客户ID
     * @return 租赁设备列表
     */
    @Cacheable(value = "customerRentalDevices", key = "#customerId")
    public List<CustomerDeviceDTO> getCustomerRentalDevices(Long customerId) {
        try {
            if (customerId == null || customerId <= 0) {
                return List.of();
            }
            
            logger.debug("获取客户租赁设备列表: customerId={}", customerId);
            
            List<CustomerDeviceDTO> devices = customerDeviceMapper.selectRentalDevicesByCustomerId(customerId);
            
            return devices != null ? devices : List.of();
            
        } catch (Exception e) {
            logger.error("获取客户租赁设备列表失败: customerId={}", customerId, e);
            return List.of();
        }
    }
    
    /**
     * 获取客户的活跃设备列表（支持前端状态筛选）
     * @param customerId 客户ID
     * @return 活跃设备列表
     */
    @Cacheable(value = "customerActiveDevices", key = "#customerId")
    public List<CustomerDeviceDTO> getCustomerActiveDevices(Long customerId) {
        try {
            if (customerId == null || customerId <= 0) {
                return List.of();
            }
            
            logger.debug("获取客户活跃设备列表: customerId={}", customerId);
            
            List<CustomerDeviceDTO> devices = customerDeviceMapper.selectActiveDevicesByCustomerId(customerId);
            
            return devices != null ? devices : List.of();
            
        } catch (Exception e) {
            logger.error("获取客户活跃设备列表失败: customerId={}", customerId, e);
            return List.of();
        }
    }
    
    /**
     * 添加客户设备关联（支持设备绑定操作）
     * @param customerId 客户ID
     * @param deviceId 设备ID
     * @param relationType 关联类型（purchased, rental）
     * @param purchasePrice 购买价格（购买设备时）
     * @param dailyRentalFee 日租金（租赁设备时）
     */
    @Transactional
    public void addCustomerDeviceRelation(Long customerId, Long deviceId, String relationType, 
                                         BigDecimal purchasePrice, BigDecimal dailyRentalFee) {
        try {
            logger.info("添加客户设备关联: customerId={}, deviceId={}, type={}", customerId, deviceId, relationType);
            
            // 检查关联是否已存在
            boolean exists = customerDeviceMapper.existsByCustomerAndDevice(customerId, deviceId, relationType);
            if (exists) {
                throw new RuntimeException("客户设备关联已存在");
            }
            
            // 创建客户设备关联记录
            CustomerDevice relation = new CustomerDevice();
            relation.setCustomerId(customerId);
            relation.setDeviceId(deviceId);
            relation.setRelationType(com.yxrobot.entity.RelationType.fromCode(relationType));
            relation.setStartDate(LocalDate.now());
            relation.setStatus(1);
            
            if ("purchased".equals(relationType) && purchasePrice != null) {
                relation.setPurchasePrice(purchasePrice);
                // 设置保修期（默认1年）
                relation.setWarrantyEndDate(LocalDate.now().plusYears(1));
            } else if ("rental".equals(relationType) && dailyRentalFee != null) {
                relation.setDailyRentalFee(dailyRentalFee);
                // 设置租赁结束日期（默认1个月）
                relation.setEndDate(LocalDate.now().plusMonths(1));
            }
            
            customerDeviceMapper.insert(relation);
            
            logger.info("客户设备关联添加成功: customerId={}, deviceId={}", customerId, deviceId);
            
        } catch (Exception e) {
            logger.error("添加客户设备关联失败: customerId={}, deviceId={}", customerId, deviceId, e);
            throw new RuntimeException("添加客户设备关联失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 移除客户设备关联（支持设备解绑操作）
     * @param customerId 客户ID
     * @param deviceId 设备ID
     */
    @Transactional
    public void removeCustomerDeviceRelation(Long customerId, Long deviceId) {
        try {
            logger.info("移除客户设备关联: customerId={}, deviceId={}", customerId, deviceId);
            
            int deletedCount = customerDeviceMapper.deleteByCustomerAndDevice(customerId, deviceId);
            
            if (deletedCount == 0) {
                throw new RuntimeException("客户设备关联不存在或已删除");
            }
            
            logger.info("客户设备关联移除成功: customerId={}, deviceId={}", customerId, deviceId);
            
        } catch (Exception e) {
            logger.error("移除客户设备关联失败: customerId={}, deviceId={}", customerId, deviceId, e);
            throw new RuntimeException("移除客户设备关联失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 更新设备关联状态
     * @param relationId 关联ID
     * @param status 状态
     */
    @Transactional
    public void updateDeviceRelationStatus(Long relationId, Integer status) {
        try {
            logger.info("更新设备关联状态: relationId={}, status={}", relationId, status);
            
            int updatedCount = customerDeviceMapper.updateRelationStatus(relationId, status);
            
            if (updatedCount == 0) {
                throw new RuntimeException("设备关联不存在或更新失败");
            }
            
            logger.info("设备关联状态更新成功: relationId={}, status={}", relationId, status);
            
        } catch (Exception e) {
            logger.error("更新设备关联状态失败: relationId={}, status={}", relationId, status, e);
            throw new RuntimeException("更新设备关联状态失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取设备类型分布统计（支持前端图表显示）
     * @param customerId 客户ID
     * @return 设备类型分布
     */
    @Cacheable(value = "customerDeviceTypeDistribution", key = "#customerId")
    public List<Map<String, Object>> getDeviceTypeDistribution(Long customerId) {
        try {
            if (customerId == null || customerId <= 0) {
                return List.of();
            }
            
            return customerDeviceMapper.selectDeviceTypeDistribution(customerId);
            
        } catch (Exception e) {
            logger.error("获取设备类型分布失败: customerId={}", customerId, e);
            return List.of();
        }
    }
    
    /**
     * 获取设备状态分布统计（支持前端图表显示）
     * @param customerId 客户ID
     * @return 设备状态分布
     */
    @Cacheable(value = "customerDeviceStatusDistribution", key = "#customerId")
    public List<Map<String, Object>> getDeviceStatusDistribution(Long customerId) {
        try {
            if (customerId == null || customerId <= 0) {
                return List.of();
            }
            
            return customerDeviceMapper.selectDeviceStatusDistribution(customerId);
            
        } catch (Exception e) {
            logger.error("获取设备状态分布失败: customerId={}", customerId, e);
            return List.of();
        }
    }
    
    /**
     * 获取即将到期的租赁设备（支持前端提醒功能）
     * @param days 提前天数
     * @return 即将到期的租赁设备列表
     */
    public List<CustomerDeviceDTO> getExpiringRentalDevices(Integer days) {
        try {
            if (days == null || days <= 0) {
                days = 7; // 默认7天
            }
            
            return customerDeviceMapper.selectExpiringRentalDevices(days);
            
        } catch (Exception e) {
            logger.error("获取即将到期的租赁设备失败: days={}", days, e);
            return List.of();
        }
    }
    
    /**
     * 获取保修即将到期的购买设备（支持前端提醒功能）
     * @param days 提前天数
     * @return 保修即将到期的设备列表
     */
    public List<CustomerDeviceDTO> getExpiringWarrantyDevices(Integer days) {
        try {
            if (days == null || days <= 0) {
                days = 30; // 默认30天
            }
            
            return customerDeviceMapper.selectExpiringWarrantyDevices(days);
            
        } catch (Exception e) {
            logger.error("获取保修即将到期的设备失败: days={}", days, e);
            return List.of();
        }
    }
}