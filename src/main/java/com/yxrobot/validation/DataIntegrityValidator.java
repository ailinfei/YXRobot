package com.yxrobot.validation;

import com.yxrobot.entity.Customer;
import com.yxrobot.exception.CustomerException;
import com.yxrobot.mapper.CustomerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据完整性验证器
 * 确保客户数据的完整性和一致性
 */
@Component
public class DataIntegrityValidator {
    
    @Autowired
    private CustomerMapper customerMapper;
    
    /**
     * 验证客户数据完整性
     */
    public IntegrityValidationResult validateCustomerIntegrity(Customer customer) {
        IntegrityValidationResult result = new IntegrityValidationResult();
        
        if (customer == null) {
            result.addError("customer", "客户对象不能为空");
            return result;
        }
        
        // 验证必填字段完整性
        validateRequiredFields(customer, result);
        
        // 验证数据一致性
        validateDataConsistency(customer, result);
        
        // 验证业务规则完整性
        validateBusinessIntegrity(customer, result);
        
        // 验证关联数据完整性
        validateRelationalIntegrity(customer, result);
        
        return result;
    }
    
    /**
     * 验证必填字段完整性
     */
    private void validateRequiredFields(Customer customer, IntegrityValidationResult result) {
        // 验证基本信息
        if (!StringUtils.hasText(customer.getCustomerName())) {
            result.addError("customerName", "客户姓名是必填字段");
        }
        
        if (!StringUtils.hasText(customer.getPhone())) {
            result.addError("phone", "联系电话是必填字段");
        }
        
        if (customer.getCustomerLevel() == null) {
            result.addError("customerLevel", "客户等级是必填字段");
        }
        
        if (customer.getCustomerStatus() == null) {
            result.addError("customerStatus", "客户状态是必填字段");
        }
        
        // 验证时间字段
        if (customer.getCreatedAt() == null) {
            result.addError("createdAt", "创建时间不能为空");
        }
        
        if (customer.getUpdatedAt() == null) {
            result.addError("updatedAt", "更新时间不能为空");
        }
    }
    
    /**
     * 验证数据一致性
     */
    private void validateDataConsistency(Customer customer, IntegrityValidationResult result) {
        // 验证时间一致性
        if (customer.getCreatedAt() != null && customer.getUpdatedAt() != null) {
            if (customer.getUpdatedAt().isBefore(customer.getCreatedAt())) {
                result.addError("updatedAt", "更新时间不能早于创建时间");
            }
        }
        
        if (customer.getRegisteredAt() != null && customer.getCreatedAt() != null) {
            if (customer.getRegisteredAt().isAfter(customer.getCreatedAt())) {
                result.addWarning("registeredAt", "注册时间晚于创建时间，请确认数据正确性");
            }
        }
        
        if (customer.getLastActiveAt() != null && customer.getCreatedAt() != null) {
            if (customer.getLastActiveAt().isBefore(customer.getCreatedAt())) {
                result.addWarning("lastActiveAt", "最后活跃时间早于创建时间，请确认数据正确性");
            }
        }
        
        // 验证数值一致性
        if (customer.getTotalSpent() != null && customer.getTotalSpent().compareTo(BigDecimal.ZERO) < 0) {
            result.addError("totalSpent", "累计消费金额不能为负数");
        }
        
        if (customer.getCustomerValue() != null) {
            if (customer.getCustomerValue().compareTo(BigDecimal.ZERO) < 0 || 
                customer.getCustomerValue().compareTo(new BigDecimal("10")) > 0) {
                result.addError("customerValue", "客户价值评分必须在0-10之间");
            }
        }
        
        // 验证统计数据一致性
        validateStatisticsConsistency(customer, result);
    }
    
    /**
     * 验证统计数据一致性
     */
    private void validateStatisticsConsistency(Customer customer, IntegrityValidationResult result) {
        // 验证设备数量一致性
        if (customer.getDeviceCount() != null && customer.getPurchasedDeviceCount() != null && 
            customer.getRentalDeviceCount() != null) {
            
            int totalCalculated = customer.getPurchasedDeviceCount() + customer.getRentalDeviceCount();
            if (!customer.getDeviceCount().equals(totalCalculated)) {
                result.addError("deviceCount", 
                    String.format("设备总数(%d)与购买设备数(%d)和租赁设备数(%d)之和不匹配", 
                                customer.getDeviceCount(), customer.getPurchasedDeviceCount(), 
                                customer.getRentalDeviceCount()));
            }
        }
        
        // 验证订单统计一致性
        if (customer.getTotalOrders() != null && customer.getTotalOrders() < 0) {
            result.addError("totalOrders", "订单总数不能为负数");
        }
        
        if (customer.getTotalSalesAmount() != null && customer.getTotalSalesAmount().compareTo(BigDecimal.ZERO) < 0) {
            result.addError("totalSalesAmount", "销售总额不能为负数");
        }
        
        // 验证消费金额与销售金额的关系
        if (customer.getTotalSpent() != null && customer.getTotalSalesAmount() != null) {
            if (customer.getTotalSpent().compareTo(customer.getTotalSalesAmount()) > 0) {
                result.addWarning("totalSpent", "累计消费金额大于销售总额，请确认数据正确性");
            }
        }
    }
    
    /**
     * 验证业务规则完整性
     */
    private void validateBusinessIntegrity(Customer customer, IntegrityValidationResult result) {
        // 验证客户等级与消费金额的匹配性
        if (customer.getCustomerLevel() != null && customer.getTotalSpent() != null) {
            validateLevelSpentConsistency(customer, result);
        }
        
        // 验证客户状态的合理性
        if (customer.getCustomerStatus() != null && customer.getLastActiveAt() != null) {
            validateStatusActivityConsistency(customer, result);
        }
        
        // 验证客户价值评分的合理性
        if (customer.getCustomerValue() != null && customer.getTotalSpent() != null) {
            validateValueSpentConsistency(customer, result);
        }
    }
    
    /**
     * 验证客户等级与消费金额的一致性
     */
    private void validateLevelSpentConsistency(Customer customer, IntegrityValidationResult result) {
        BigDecimal totalSpent = customer.getTotalSpent();
        String level = customer.getCustomerLevel().getCode();
        
        // 定义等级阈值（这些值应该从配置中获取）
        BigDecimal vipThreshold = new BigDecimal("10000");
        BigDecimal premiumThreshold = new BigDecimal("50000");
        
        switch (level.toLowerCase()) {
            case "regular":
                if (totalSpent.compareTo(vipThreshold) >= 0) {
                    result.addWarning("customerLevel", 
                        "客户消费金额已达到VIP标准，建议升级客户等级");
                }
                break;
            case "vip":
                if (totalSpent.compareTo(vipThreshold) < 0) {
                    result.addWarning("customerLevel", 
                        "客户消费金额未达到VIP标准，请确认等级设置");
                } else if (totalSpent.compareTo(premiumThreshold) >= 0) {
                    result.addWarning("customerLevel", 
                        "客户消费金额已达到高级客户标准，建议升级客户等级");
                }
                break;
            case "premium":
                if (totalSpent.compareTo(premiumThreshold) < 0) {
                    result.addWarning("customerLevel", 
                        "客户消费金额未达到高级客户标准，请确认等级设置");
                }
                break;
        }
    }
    
    /**
     * 验证客户状态与活跃度的一致性
     */
    private void validateStatusActivityConsistency(Customer customer, IntegrityValidationResult result) {
        String status = customer.getCustomerStatus().getCode();
        LocalDateTime lastActive = customer.getLastActiveAt();
        LocalDateTime now = LocalDateTime.now();
        
        long daysSinceLastActive = java.time.Duration.between(lastActive, now).toDays();
        
        if ("active".equals(status.toLowerCase()) && daysSinceLastActive > 90) {
            result.addWarning("customerStatus", 
                "客户状态为活跃，但已超过90天未活跃，建议更新状态");
        } else if ("inactive".equals(status.toLowerCase()) && daysSinceLastActive < 30) {
            result.addWarning("customerStatus", 
                "客户状态为不活跃，但最近30天内有活跃记录，请确认状态设置");
        }
    }
    
    /**
     * 验证客户价值与消费金额的一致性
     */
    private void validateValueSpentConsistency(Customer customer, IntegrityValidationResult result) {
        BigDecimal customerValue = customer.getCustomerValue();
        BigDecimal totalSpent = customer.getTotalSpent();
        
        // 简单的价值评分验证逻辑
        if (totalSpent.compareTo(new BigDecimal("100000")) >= 0 && customerValue.compareTo(new BigDecimal("8")) < 0) {
            result.addWarning("customerValue", 
                "客户消费金额较高但价值评分较低，请确认评分准确性");
        } else if (totalSpent.compareTo(new BigDecimal("1000")) < 0 && customerValue.compareTo(new BigDecimal("7")) > 0) {
            result.addWarning("customerValue", 
                "客户消费金额较低但价值评分较高，请确认评分准确性");
        }
    }
    
    /**
     * 验证关联数据完整性
     */
    private void validateRelationalIntegrity(Customer customer, IntegrityValidationResult result) {
        if (customer.getId() == null) {
            // 新客户，跳过关联数据验证
            return;
        }
        
        try {
            // 验证客户是否存在
            Customer existingCustomer = customerMapper.selectById(customer.getId());
            if (existingCustomer == null) {
                result.addError("id", "客户ID不存在");
                return;
            }
            
            // 验证唯一性约束
            validateUniquenessConstraints(customer, result);
            
        } catch (Exception e) {
            result.addError("database", "数据库验证失败: " + e.getMessage());
        }
    }
    
    /**
     * 验证唯一性约束
     */
    private void validateUniquenessConstraints(Customer customer, IntegrityValidationResult result) {
        // 验证电话号码唯一性
        if (StringUtils.hasText(customer.getPhone())) {
            Customer existingByPhone = customerMapper.selectByPhone(customer.getPhone());
            if (existingByPhone != null && !existingByPhone.getId().equals(customer.getId())) {
                result.addError("phone", "电话号码已被其他客户使用");
            }
        }
        
        // 验证邮箱唯一性
        if (StringUtils.hasText(customer.getEmail())) {
            Customer existingByEmail = customerMapper.selectByEmail(customer.getEmail());
            if (existingByEmail != null && !existingByEmail.getId().equals(customer.getId())) {
                result.addError("email", "邮箱地址已被其他客户使用");
            }
        }
    }
    
    /**
     * 验证客户数据的完整性（批量）
     */
    public List<IntegrityValidationResult> validateCustomersIntegrity(List<Customer> customers) {
        List<IntegrityValidationResult> results = new ArrayList<>();
        
        for (Customer customer : customers) {
            IntegrityValidationResult result = validateCustomerIntegrity(customer);
            result.setCustomerId(customer.getId());
            results.add(result);
        }
        
        // 批量验证唯一性
        validateBatchUniqueness(customers, results);
        
        return results;
    }
    
    /**
     * 批量验证唯一性
     */
    private void validateBatchUniqueness(List<Customer> customers, List<IntegrityValidationResult> results) {
        // 验证批量数据中的重复项
        for (int i = 0; i < customers.size(); i++) {
            Customer customer1 = customers.get(i);
            IntegrityValidationResult result1 = results.get(i);
            
            for (int j = i + 1; j < customers.size(); j++) {
                Customer customer2 = customers.get(j);
                IntegrityValidationResult result2 = results.get(j);
                
                // 检查电话号码重复
                if (StringUtils.hasText(customer1.getPhone()) && 
                    customer1.getPhone().equals(customer2.getPhone())) {
                    result1.addError("phone", "批量数据中电话号码重复");
                    result2.addError("phone", "批量数据中电话号码重复");
                }
                
                // 检查邮箱重复
                if (StringUtils.hasText(customer1.getEmail()) && 
                    customer1.getEmail().equals(customer2.getEmail())) {
                    result1.addError("email", "批量数据中邮箱地址重复");
                    result2.addError("email", "批量数据中邮箱地址重复");
                }
            }
        }
    }
    
    /**
     * 完整性验证结果类
     */
    public static class IntegrityValidationResult {
        private Long customerId;
        private boolean valid = true;
        private List<ValidationIssue> errors = new ArrayList<>();
        private List<ValidationIssue> warnings = new ArrayList<>();
        
        public void addError(String field, String message) {
            errors.add(new ValidationIssue(field, message, "ERROR"));
            valid = false;
        }
        
        public void addWarning(String field, String message) {
            warnings.add(new ValidationIssue(field, message, "WARNING"));
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public boolean hasErrors() {
            return !errors.isEmpty();
        }
        
        public boolean hasWarnings() {
            return !warnings.isEmpty();
        }
        
        public List<ValidationIssue> getErrors() {
            return errors;
        }
        
        public List<ValidationIssue> getWarnings() {
            return warnings;
        }
        
        public List<ValidationIssue> getAllIssues() {
            List<ValidationIssue> allIssues = new ArrayList<>(errors);
            allIssues.addAll(warnings);
            return allIssues;
        }
        
        public Long getCustomerId() {
            return customerId;
        }
        
        public void setCustomerId(Long customerId) {
            this.customerId = customerId;
        }
        
        /**
         * 抛出完整性异常（如果有错误）
         */
        public void throwIfHasErrors() {
            if (hasErrors()) {
                StringBuilder message = new StringBuilder("数据完整性验证失败：");
                for (ValidationIssue error : errors) {
                    message.append(error.getField()).append(": ").append(error.getMessage()).append("; ");
                }
                throw new CustomerException.CustomerDataIntegrityException(message.toString());
            }
        }
    }
    
    /**
     * 验证问题类
     */
    public static class ValidationIssue {
        private final String field;
        private final String message;
        private final String type;
        
        public ValidationIssue(String field, String message, String type) {
            this.field = field;
            this.message = message;
            this.type = type;
        }
        
        public String getField() {
            return field;
        }
        
        public String getMessage() {
            return message;
        }
        
        public String getType() {
            return type;
        }
        
        @Override
        public String toString() {
            return String.format("%s[%s]: %s", type, field, message);
        }
    }
}