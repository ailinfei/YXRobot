package com.yxrobot.service;

import com.yxrobot.dto.ServiceRecordDTO;
import com.yxrobot.entity.CustomerServiceRecord;
import com.yxrobot.mapper.CustomerMapper;
import com.yxrobot.mapper.CustomerServiceRecordMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 客户服务记录服务类
 * 处理客户服务记录相关的业务逻辑，支持前端详情功能
 * 
 * 主要功能：
 * 1. 获取客户服务记录列表 - 支持前端详情页显示
 * 2. 服务记录统计计算 - 服务次数、费用统计等
 * 3. 服务记录分类管理 - 维护、升级、咨询、投诉服务管理
 * 4. 服务记录状态统计 - 进行中、已完成等状态统计
 * 5. 关联数据格式适配前端详情页面显示
 */
@Service
@Transactional(readOnly = true)
public class CustomerServiceRecordService {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceRecordService.class);
    
    @Autowired
    private CustomerMapper customerMapper;
    
    @Autowired
    private CustomerServiceRecordMapper customerServiceRecordMapper;
    
    /**
     * 服务记录信息内部类 - 用于返回给前端
     */
    public static class ServiceRecordInfo {
        private Long serviceId;
        private String serviceNumber;
        private String serviceType;
        private String serviceStatus;
        private String priority;
        private String title;
        private String description;
        private BigDecimal serviceCost;
        private String serviceDate;
        private String completedDate;
        private String assignedTechnician;
        private String customerFeedback;
        private Integer satisfactionRating;
        private String deviceInfo;
        private String location;
        private Integer estimatedHours;
        private Integer actualHours;
        private String notes;
        
        // 构造函数
        public ServiceRecordInfo() {}
        
        // Getter和Setter方法
        public Long getServiceId() { return serviceId; }
        public void setServiceId(Long serviceId) { this.serviceId = serviceId; }
        public String getServiceNumber() { return serviceNumber; }
        public void setServiceNumber(String serviceNumber) { this.serviceNumber = serviceNumber; }
        public String getServiceType() { return serviceType; }
        public void setServiceType(String serviceType) { this.serviceType = serviceType; }
        public String getServiceStatus() { return serviceStatus; }
        public void setServiceStatus(String serviceStatus) { this.serviceStatus = serviceStatus; }
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public BigDecimal getServiceCost() { return serviceCost; }
        public void setServiceCost(BigDecimal serviceCost) { this.serviceCost = serviceCost; }
        public String getServiceDate() { return serviceDate; }
        public void setServiceDate(String serviceDate) { this.serviceDate = serviceDate; }
        public String getCompletedDate() { return completedDate; }
        public void setCompletedDate(String completedDate) { this.completedDate = completedDate; }
        public String getAssignedTechnician() { return assignedTechnician; }
        public void setAssignedTechnician(String assignedTechnician) { this.assignedTechnician = assignedTechnician; }
        public String getCustomerFeedback() { return customerFeedback; }
        public void setCustomerFeedback(String customerFeedback) { this.customerFeedback = customerFeedback; }
        public Integer getSatisfactionRating() { return satisfactionRating; }
        public void setSatisfactionRating(Integer satisfactionRating) { this.satisfactionRating = satisfactionRating; }
        public String getDeviceInfo() { return deviceInfo; }
        public void setDeviceInfo(String deviceInfo) { this.deviceInfo = deviceInfo; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public Integer getEstimatedHours() { return estimatedHours; }
        public void setEstimatedHours(Integer estimatedHours) { this.estimatedHours = estimatedHours; }
        public Integer getActualHours() { return actualHours; }
        public void setActualHours(Integer actualHours) { this.actualHours = actualHours; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }
    
    /**
     * 获取客户的服务记录列表（支持前端详情页显示）
     * 返回格式与前端CustomerServiceRecord接口完全匹配
     * @param customerId 客户ID
     * @return 客户服务记录列表
     */
    @Cacheable(value = "customerServiceRecords", key = "#customerId", unless = "#result.isEmpty()")
    public List<ServiceRecordDTO> getCustomerServiceRecords(Long customerId) {
        try {
            if (customerId == null || customerId <= 0) {
                logger.warn("客户ID无效: {}", customerId);
                return List.of();
            }
            
            logger.debug("获取客户服务记录列表: customerId={}", customerId);
            
            List<ServiceRecordDTO> serviceRecords = customerServiceRecordMapper.selectCustomerServiceRecordsByCustomerId(customerId);
            
            if (serviceRecords == null) {
                serviceRecords = List.of();
            }
            
            logger.debug("客户服务记录列表获取完成: customerId={}, 记录数量={}", customerId, serviceRecords.size());
            
            return serviceRecords;
            
        } catch (Exception e) {
            logger.error("获取客户服务记录列表失败: customerId={}", customerId, e);
            return List.of();
        }
    }
    
    /**
     * 获取客户的服务记录统计信息（支持前端统计显示）
     * @param customerId 客户ID
     * @return 服务记录统计信息
     */
    @Cacheable(value = "customerServiceStats", key = "#customerId")
    public Map<String, Object> getCustomerServiceStats(Long customerId) {
        try {
            if (customerId == null || customerId <= 0) {
                logger.warn("客户ID无效: {}", customerId);
                return createEmptyServiceStats();
            }
            
            Map<String, Object> stats = customerServiceRecordMapper.selectCustomerServiceStats(customerId);
            
            if (stats == null || stats.isEmpty()) {
                stats = createEmptyServiceStats();
            }
            
            return stats;
            
        } catch (Exception e) {
            logger.error("获取客户服务记录统计失败: customerId={}", customerId, e);
            return createEmptyServiceStats();
        }
    }
    
    /**
     * 创建空的服务记录统计数据
     * @return 空统计数据
     */
    private Map<String, Object> createEmptyServiceStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalServices", 0);
        stats.put("maintenanceServices", 0);
        stats.put("upgradeServices", 0);
        stats.put("consultationServices", 0);
        stats.put("complaintServices", 0);
        stats.put("completedServices", 0);
        stats.put("inProgressServices", 0);
        stats.put("cancelledServices", 0);
        stats.put("urgentServices", 0);
        stats.put("highPriorityServices", 0);
        stats.put("totalServiceCost", BigDecimal.ZERO);
        stats.put("avgServiceCost", BigDecimal.ZERO);
        return stats;
    }
    
    /**
     * 获取客户的维护服务记录（支持前端分类显示）
     * @param customerId 客户ID
     * @return 维护服务记录列表
     */
    @Cacheable(value = "customerMaintenanceRecords", key = "#customerId")
    public List<ServiceRecordDTO> getCustomerMaintenanceServices(Long customerId) {
        try {
            if (customerId == null || customerId <= 0) {
                return List.of();
            }
            
            logger.debug("获取客户维护服务记录: customerId={}", customerId);
            
            List<ServiceRecordDTO> records = customerServiceRecordMapper.selectMaintenanceServicesByCustomerId(customerId);
            
            return records != null ? records : List.of();
            
        } catch (Exception e) {
            logger.error("获取客户维护服务记录失败: customerId={}", customerId, e);
            return List.of();
        }
    }
    
    /**
     * 获取客户的升级服务记录（支持前端分类显示）
     * @param customerId 客户ID
     * @return 升级服务记录列表
     */
    @Cacheable(value = "customerUpgradeRecords", key = "#customerId")
    public List<ServiceRecordDTO> getCustomerUpgradeServices(Long customerId) {
        try {
            if (customerId == null || customerId <= 0) {
                return List.of();
            }
            
            logger.debug("获取客户升级服务记录: customerId={}", customerId);
            
            List<ServiceRecordDTO> records = customerServiceRecordMapper.selectUpgradeServicesByCustomerId(customerId);
            
            return records != null ? records : List.of();
            
        } catch (Exception e) {
            logger.error("获取客户升级服务记录失败: customerId={}", customerId, e);
            return List.of();
        }
    }
    
    /**
     * 获取客户的咨询服务记录（支持前端分类显示）
     * @param customerId 客户ID
     * @return 咨询服务记录列表
     */
    @Cacheable(value = "customerConsultationRecords", key = "#customerId")
    public List<ServiceRecordDTO> getCustomerConsultationServices(Long customerId) {
        try {
            if (customerId == null || customerId <= 0) {
                return List.of();
            }
            
            logger.debug("获取客户咨询服务记录: customerId={}", customerId);
            
            List<ServiceRecordDTO> records = customerServiceRecordMapper.selectConsultationServicesByCustomerId(customerId);
            
            return records != null ? records : List.of();
            
        } catch (Exception e) {
            logger.error("获取客户咨询服务记录失败: customerId={}", customerId, e);
            return List.of();
        }
    }
    
    /**
     * 获取客户的投诉服务记录（支持前端分类显示）
     * @param customerId 客户ID
     * @return 投诉服务记录列表
     */
    @Cacheable(value = "customerComplaintRecords", key = "#customerId")
    public List<ServiceRecordDTO> getCustomerComplaintServices(Long customerId) {
        try {
            if (customerId == null || customerId <= 0) {
                return List.of();
            }
            
            logger.debug("获取客户投诉服务记录: customerId={}", customerId);
            
            List<ServiceRecordDTO> records = customerServiceRecordMapper.selectComplaintServicesByCustomerId(customerId);
            
            return records != null ? records : List.of();
            
        } catch (Exception e) {
            logger.error("获取客户投诉服务记录失败: customerId={}", customerId, e);
            return List.of();
        }
    }
    
    /**
     * 获取客户的已完成服务记录（支持前端状态筛选）
     * @param customerId 客户ID
     * @return 已完成服务记录列表
     */
    @Cacheable(value = "customerCompletedServices", key = "#customerId")
    public List<ServiceRecordDTO> getCustomerCompletedServices(Long customerId) {
        try {
            if (customerId == null || customerId <= 0) {
                return List.of();
            }
            
            logger.debug("获取客户已完成服务记录: customerId={}", customerId);
            
            List<ServiceRecordDTO> records = customerServiceRecordMapper.selectCompletedServicesByCustomerId(customerId);
            
            return records != null ? records : List.of();
            
        } catch (Exception e) {
            logger.error("获取客户已完成服务记录失败: customerId={}", customerId, e);
            return List.of();
        }
    }
    
    /**
     * 获取客户的进行中服务记录（支持前端状态筛选）
     * @param customerId 客户ID
     * @return 进行中服务记录列表
     */
    @Cacheable(value = "customerInProgressServices", key = "#customerId")
    public List<ServiceRecordDTO> getCustomerInProgressServices(Long customerId) {
        try {
            if (customerId == null || customerId <= 0) {
                return List.of();
            }
            
            logger.debug("获取客户进行中服务记录: customerId={}", customerId);
            
            List<ServiceRecordDTO> records = customerServiceRecordMapper.selectInProgressServicesByCustomerId(customerId);
            
            return records != null ? records : List.of();
            
        } catch (Exception e) {
            logger.error("获取客户进行中服务记录失败: customerId={}", customerId, e);
            return List.of();
        }
    }
    
    /**
     * 获取客户的紧急服务记录（支持前端紧急情况显示）
     * @param customerId 客户ID
     * @return 紧急服务记录列表
     */
    @Cacheable(value = "customerUrgentServices", key = "#customerId")
    public List<ServiceRecordDTO> getCustomerUrgentServices(Long customerId) {
        try {
            if (customerId == null || customerId <= 0) {
                return List.of();
            }
            
            logger.debug("获取客户紧急服务记录: customerId={}", customerId);
            
            List<ServiceRecordDTO> records = customerServiceRecordMapper.selectUrgentServices(customerId);
            
            return records != null ? records : List.of();
            
        } catch (Exception e) {
            logger.error("获取客户紧急服务记录失败: customerId={}", customerId, e);
            return List.of();
        }
    }
    
    /**
     * 获取客户的高优先级服务记录（支持前端优先级筛选）
     * @param customerId 客户ID
     * @return 高优先级服务记录列表
     */
    @Cacheable(value = "customerHighPriorityServices", key = "#customerId")
    public List<ServiceRecordDTO> getCustomerHighPriorityServices(Long customerId) {
        try {
            if (customerId == null || customerId <= 0) {
                return List.of();
            }
            
            logger.debug("获取客户高优先级服务记录: customerId={}", customerId);
            
            List<ServiceRecordDTO> records = customerServiceRecordMapper.selectHighPriorityServices(customerId);
            
            return records != null ? records : List.of();
            
        } catch (Exception e) {
            logger.error("获取客户高优先级服务记录失败: customerId={}", customerId, e);
            return List.of();
        }
    }
    
    /**
     * 获取客户服务记录状态分布（支持前端图表显示）
     * @param customerId 客户ID
     * @return 服务记录状态分布
     */
    @Cacheable(value = "customerServiceStatusDistribution", key = "#customerId")
    public List<Map<String, Object>> getCustomerServiceStatusDistribution(Long customerId) {
        try {
            if (customerId == null || customerId <= 0) {
                return List.of();
            }
            
            return customerServiceRecordMapper.selectServiceStatusDistribution(customerId);
            
        } catch (Exception e) {
            logger.error("获取服务记录状态分布失败: customerId={}", customerId, e);
            return List.of();
        }
    }
    
    /**
     * 获取客户服务记录类型分布（支持前端图表显示）
     * @param customerId 客户ID
     * @return 服务记录类型分布
     */
    @Cacheable(value = "customerServiceTypeDistribution", key = "#customerId")
    public List<Map<String, Object>> getCustomerServiceTypeDistribution(Long customerId) {
        try {
            if (customerId == null || customerId <= 0) {
                return List.of();
            }
            
            return customerServiceRecordMapper.selectServiceTypeDistribution(customerId);
            
        } catch (Exception e) {
            logger.error("获取服务记录类型分布失败: customerId={}", customerId, e);
            return List.of();
        }
    }
    
    /**
     * 计算客户服务费用总计（支持前端统计显示）
     * @param customerId 客户ID
     * @return 服务费用总计
     */
    public BigDecimal calculateCustomerServiceCost(Long customerId) {
        try {
            Map<String, Object> stats = getCustomerServiceStats(customerId);
            Object totalCost = stats.get("totalServiceCost");
            
            if (totalCost instanceof BigDecimal) {
                return (BigDecimal) totalCost;
            }
            
            return BigDecimal.ZERO;
            
        } catch (Exception e) {
            logger.error("计算客户服务费用总计失败: customerId={}", customerId, e);
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * 获取客户的最近服务记录（支持前端最近记录显示）
     * @param customerId 客户ID
     * @param limit 限制数量
     * @return 最近服务记录列表
     */
    @Cacheable(value = "customerRecentServices", key = "#customerId + '_' + #limit")
    public List<ServiceRecordDTO> getCustomerRecentServices(Long customerId, Integer limit) {
        try {
            if (customerId == null || customerId <= 0) {
                return List.of();
            }
            
            if (limit == null || limit <= 0) {
                limit = 10; // 默认10条
            }
            
            logger.debug("获取客户最近服务记录: customerId={}, limit={}", customerId, limit);
            
            List<ServiceRecordDTO> records = customerServiceRecordMapper.selectRecentServicesByCustomerId(customerId, limit);
            
            return records != null ? records : List.of();
            
        } catch (Exception e) {
            logger.error("获取客户最近服务记录失败: customerId={}, limit={}", customerId, limit, e);
            return List.of();
        }
    }
    
    /**
     * 获取客户的平均服务费用（支持前端统计显示）
     * @param customerId 客户ID
     * @return 平均服务费用
     */
    public BigDecimal getCustomerAverageServiceCost(Long customerId) {
        try {
            if (customerId == null || customerId <= 0) {
                return BigDecimal.ZERO;
            }
            
            BigDecimal avgCost = customerServiceRecordMapper.selectAverageServiceCost(customerId);
            
            return avgCost != null ? avgCost : BigDecimal.ZERO;
            
        } catch (Exception e) {
            logger.error("获取客户平均服务费用失败: customerId={}", customerId, e);
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * 获取需要跟进的服务记录（支持前端待办事项显示）
     * @param customerId 客户ID
     * @return 需要跟进的服务记录列表
     */
    public List<ServiceRecordDTO> getServicesNeedingFollowUp(Long customerId) {
        try {
            if (customerId == null || customerId <= 0) {
                return List.of();
            }
            
            logger.debug("获取需要跟进的服务记录: customerId={}", customerId);
            
            List<ServiceRecordDTO> records = customerServiceRecordMapper.selectServicesNeedingFollowUp(customerId);
            
            return records != null ? records : List.of();
            
        } catch (Exception e) {
            logger.error("获取需要跟进的服务记录失败: customerId={}", customerId, e);
            return List.of();
        }
    }
    
    /**
     * 获取指定日期范围内的客户服务记录（支持前端日期筛选）
     * @param customerId 客户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 服务记录列表
     */
    public List<ServiceRecordDTO> getCustomerServicesByDateRange(Long customerId, LocalDate startDate, LocalDate endDate) {
        try {
            if (customerId == null || customerId <= 0) {
                return List.of();
            }
            
            if (startDate == null || endDate == null) {
                logger.warn("日期范围无效: startDate={}, endDate={}", startDate, endDate);
                return List.of();
            }
            
            logger.debug("获取客户日期范围服务记录: customerId={}, startDate={}, endDate={}", customerId, startDate, endDate);
            
            List<ServiceRecordDTO> records = customerServiceRecordMapper.selectServicesByDateRange(customerId, startDate, endDate);
            
            return records != null ? records : List.of();
            
        } catch (Exception e) {
            logger.error("获取客户日期范围服务记录失败: customerId={}, startDate={}, endDate={}", customerId, startDate, endDate, e);
            return List.of();
        }
    }
    
    /**
     * 获取客户月度服务统计（支持前端趋势图表）
     * @param customerId 客户ID
     * @param months 月数
     * @return 月度服务统计
     */
    @Cacheable(value = "customerMonthlyServiceStats", key = "#customerId + '_' + #months")
    public List<Map<String, Object>> getCustomerMonthlyServiceStats(Long customerId, Integer months) {
        try {
            if (customerId == null || customerId <= 0) {
                return List.of();
            }
            
            if (months == null || months <= 0) {
                months = 12; // 默认12个月
            }
            
            return customerServiceRecordMapper.selectMonthlyServiceStats(customerId, months);
            
        } catch (Exception e) {
            logger.error("获取客户月度服务统计失败: customerId={}, months={}", customerId, months, e);
            return List.of();
        }
    }
}