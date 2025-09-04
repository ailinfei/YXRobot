package com.yxrobot.service;

import com.yxrobot.dto.CustomerCreateDTO;
import com.yxrobot.dto.CustomerUpdateDTO;
import com.yxrobot.entity.Customer;
import com.yxrobot.exception.CustomerException;
import com.yxrobot.validation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 客户验证服务
 * 协调所有验证器，提供统一的验证接口
 */
@Service
public class CustomerValidationService {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerValidationService.class);
    
    @Autowired
    private CustomerValidator customerValidator;
    
    @Autowired
    private CustomerFormValidator customerFormValidator;
    
    @Autowired
    private PhoneNumberValidator phoneNumberValidator;
    
    @Autowired
    private EmailValidator emailValidator;
    
    @Autowired
    private DataIntegrityValidator dataIntegrityValidator;
    
    /**
     * 验证客户创建表单
     */
    public ValidationSummary validateCustomerCreate(CustomerCreateDTO createDTO) {
        logger.debug("开始验证客户创建表单数据");
        
        ValidationSummary summary = new ValidationSummary();
        
        try {
            // 1. 表单基础验证
            CustomerFormValidator.ValidationResult formResult = 
                customerFormValidator.validateCreateForm(createDTO);
            summary.addFormValidation(formResult);
            
            // 2. 电话号码详细验证
            if (createDTO.getPhone() != null) {
                PhoneNumberValidator.PhoneValidationResult phoneResult = 
                    phoneNumberValidator.validatePhone(createDTO.getPhone());
                summary.addPhoneValidation(phoneResult);
            }
            
            // 3. 邮箱地址详细验证
            if (createDTO.getEmail() != null) {
                EmailValidator.EmailValidationResult emailResult = 
                    emailValidator.validateEmail(createDTO.getEmail());
                summary.addEmailValidation(emailResult);
            }
            
            // 4. 如果基础验证通过，进行业务验证
            if (!summary.hasErrors()) {
                Customer customer = convertToCustomer(createDTO);
                customerValidator.validateCustomerForCreate(customer);
                summary.setBusinessValidationPassed(true);
            }
            
        } catch (CustomerException ex) {
            summary.addBusinessError(ex.getErrorCode(), ex.getMessage());
            logger.warn("客户创建验证失败: {}", ex.getMessage());
        } catch (Exception ex) {
            summary.addSystemError("系统验证错误: " + ex.getMessage());
            logger.error("客户创建验证系统错误", ex);
        }
        
        logger.debug("客户创建表单验证完成，结果: {}", summary.isValid() ? "通过" : "失败");
        return summary;
    }
    
    /**
     * 验证客户更新表单
     */
    public ValidationSummary validateCustomerUpdate(Long customerId, CustomerUpdateDTO updateDTO) {
        logger.debug("开始验证客户更新表单数据，客户ID: {}", customerId);
        
        ValidationSummary summary = new ValidationSummary();
        
        try {
            // 1. 验证客户ID
            customerValidator.validateCustomerId(customerId);
            
            // 2. 表单基础验证
            CustomerFormValidator.ValidationResult formResult = 
                customerFormValidator.validateUpdateForm(updateDTO);
            summary.addFormValidation(formResult);
            
            // 3. 电话号码详细验证（如果提供）
            if (updateDTO.getPhone() != null) {
                PhoneNumberValidator.PhoneValidationResult phoneResult = 
                    phoneNumberValidator.validatePhone(updateDTO.getPhone());
                summary.addPhoneValidation(phoneResult);
            }
            
            // 4. 邮箱地址详细验证（如果提供）
            if (updateDTO.getEmail() != null) {
                EmailValidator.EmailValidationResult emailResult = 
                    emailValidator.validateEmail(updateDTO.getEmail());
                summary.addEmailValidation(emailResult);
            }
            
            // 5. 如果基础验证通过，进行业务验证
            if (!summary.hasErrors()) {
                Customer customer = convertToCustomer(updateDTO);
                customer.setId(customerId);
                customerValidator.validateCustomerForUpdate(customer);
                summary.setBusinessValidationPassed(true);
            }
            
        } catch (CustomerException ex) {
            summary.addBusinessError(ex.getErrorCode(), ex.getMessage());
            logger.warn("客户更新验证失败: {}", ex.getMessage());
        } catch (Exception ex) {
            summary.addSystemError("系统验证错误: " + ex.getMessage());
            logger.error("客户更新验证系统错误", ex);
        }
        
        logger.debug("客户更新表单验证完成，结果: {}", summary.isValid() ? "通过" : "失败");
        return summary;
    }
    
    /**
     * 验证客户数据完整性
     */
    public ValidationSummary validateCustomerIntegrity(Customer customer) {
        logger.debug("开始验证客户数据完整性，客户ID: {}", customer.getId());
        
        ValidationSummary summary = new ValidationSummary();
        
        try {
            DataIntegrityValidator.IntegrityValidationResult integrityResult = 
                dataIntegrityValidator.validateCustomerIntegrity(customer);
            summary.addIntegrityValidation(integrityResult);
            
        } catch (Exception ex) {
            summary.addSystemError("完整性验证错误: " + ex.getMessage());
            logger.error("客户数据完整性验证系统错误", ex);
        }
        
        logger.debug("客户数据完整性验证完成，结果: {}", summary.isValid() ? "通过" : "失败");
        return summary;
    }
    
    /**
     * 验证查询参数
     */
    public void validateQueryParameters(String keyword, String level, String status, 
                                       Integer page, Integer pageSize) {
        logger.debug("验证查询参数");
        
        try {
            customerValidator.validateQueryParams(keyword, level, status, page, pageSize);
        } catch (CustomerException ex) {
            logger.warn("查询参数验证失败: {}", ex.getMessage());
            throw ex;
        }
    }
    
    /**
     * 批量验证客户数据完整性
     */
    public List<ValidationSummary> validateCustomersIntegrity(List<Customer> customers) {
        logger.debug("开始批量验证客户数据完整性，数量: {}", customers.size());
        
        List<ValidationSummary> summaries = new ArrayList<>();
        
        try {
            List<DataIntegrityValidator.IntegrityValidationResult> results = 
                dataIntegrityValidator.validateCustomersIntegrity(customers);
            
            for (DataIntegrityValidator.IntegrityValidationResult result : results) {
                ValidationSummary summary = new ValidationSummary();
                summary.addIntegrityValidation(result);
                summaries.add(summary);
            }
            
        } catch (Exception ex) {
            logger.error("批量完整性验证系统错误", ex);
            // 为每个客户创建错误摘要
            for (Customer customer : customers) {
                ValidationSummary summary = new ValidationSummary();
                summary.addSystemError("批量验证错误: " + ex.getMessage());
                summaries.add(summary);
            }
        }
        
        logger.debug("批量客户数据完整性验证完成");
        return summaries;
    }
    

    
    /**
     * 转换CreateDTO为Customer实体
     */
    private Customer convertToCustomer(CustomerCreateDTO createDTO) {
        Customer customer = new Customer();
        customer.setCustomerName(createDTO.getName());
        customer.setPhone(createDTO.getPhone());
        customer.setEmail(createDTO.getEmail());
        customer.setContactPerson(createDTO.getCompany());
        customer.setNotes(createDTO.getNotes());
        
        // 转换枚举类型
        if (createDTO.getLevel() != null) {
            customer.setCustomerLevel(
                com.yxrobot.entity.CustomerLevel.valueOf(createDTO.getLevel().toUpperCase()));
        }
        
        if (createDTO.getStatus() != null) {
            customer.setCustomerStatus(
                com.yxrobot.entity.CustomerStatus.valueOf(createDTO.getStatus().toUpperCase()));
        }
        
        return customer;
    }
    
    /**
     * 转换UpdateDTO为Customer实体
     */
    private Customer convertToCustomer(CustomerUpdateDTO updateDTO) {
        Customer customer = new Customer();
        customer.setCustomerName(updateDTO.getName());
        customer.setPhone(updateDTO.getPhone());
        customer.setEmail(updateDTO.getEmail());
        customer.setContactPerson(updateDTO.getCompany());
        customer.setNotes(updateDTO.getNotes());
        
        // 转换枚举类型
        if (updateDTO.getLevel() != null) {
            customer.setCustomerLevel(
                com.yxrobot.entity.CustomerLevel.valueOf(updateDTO.getLevel().toUpperCase()));
        }
        
        if (updateDTO.getStatus() != null) {
            customer.setCustomerStatus(
                com.yxrobot.entity.CustomerStatus.valueOf(updateDTO.getStatus().toUpperCase()));
        }
        
        return customer;
    }
    
    /**
     * 验证摘要类
     */
    public static class ValidationSummary {
        private boolean valid = true;
        private boolean businessValidationPassed = false;
        private List<ValidationMessage> errors = new ArrayList<>();
        private List<ValidationMessage> warnings = new ArrayList<>();
        private List<ValidationMessage> infos = new ArrayList<>();
        
        public void addFormValidation(CustomerFormValidator.ValidationResult formResult) {
            if (formResult.hasErrors()) {
                valid = false;
                for (CustomerFormValidator.ValidationError error : formResult.getErrors()) {
                    errors.add(new ValidationMessage("FORM", error.getField(), error.getMessage()));
                }
            }
            
            if (formResult.hasWarnings()) {
                for (CustomerFormValidator.ValidationError warning : formResult.getWarnings()) {
                    warnings.add(new ValidationMessage("FORM", warning.getField(), warning.getMessage()));
                }
            }
        }
        
        public void addPhoneValidation(PhoneNumberValidator.PhoneValidationResult phoneResult) {
            if (!phoneResult.isValid()) {
                valid = false;
                errors.add(new ValidationMessage("PHONE", "phone", phoneResult.getErrorMessage()));
            } else {
                // 添加电话信息
                if (phoneResult.getCarrier() != null) {
                    infos.add(new ValidationMessage("PHONE", "carrier", 
                        "运营商: " + phoneResult.getCarrier()));
                }
                
                if (phoneResult.hasWarnings()) {
                    for (String warning : phoneResult.getWarnings()) {
                        warnings.add(new ValidationMessage("PHONE", "phone", warning));
                    }
                }
            }
        }
        
        public void addEmailValidation(EmailValidator.EmailValidationResult emailResult) {
            if (!emailResult.isValid()) {
                valid = false;
                errors.add(new ValidationMessage("EMAIL", "email", emailResult.getErrorMessage()));
            } else {
                // 添加邮箱信息
                if (emailResult.getEmailType() != null) {
                    infos.add(new ValidationMessage("EMAIL", "type", 
                        "邮箱类型: " + emailResult.getEmailType()));
                }
                
                if (emailResult.hasWarnings()) {
                    for (String warning : emailResult.getWarnings()) {
                        warnings.add(new ValidationMessage("EMAIL", "email", warning));
                    }
                }
            }
        }
        
        public void addIntegrityValidation(DataIntegrityValidator.IntegrityValidationResult integrityResult) {
            if (!integrityResult.isValid()) {
                valid = false;
                for (DataIntegrityValidator.ValidationIssue error : integrityResult.getErrors()) {
                    errors.add(new ValidationMessage("INTEGRITY", error.getField(), error.getMessage()));
                }
            }
            
            if (integrityResult.hasWarnings()) {
                for (DataIntegrityValidator.ValidationIssue warning : integrityResult.getWarnings()) {
                    warnings.add(new ValidationMessage("INTEGRITY", warning.getField(), warning.getMessage()));
                }
            }
        }
        
        public void addBusinessError(String errorCode, String message) {
            valid = false;
            errors.add(new ValidationMessage("BUSINESS", errorCode, message));
        }
        
        public void addSystemError(String message) {
            valid = false;
            errors.add(new ValidationMessage("SYSTEM", "system", message));
        }
        
        public void addError(String field, String message) {
            valid = false;
            errors.add(new ValidationMessage("GENERAL", field, message));
        }
        
        public void addWarning(String field, String message) {
            warnings.add(new ValidationMessage("GENERAL", field, message));
        }
        
        public void addInfo(String field, String message) {
            infos.add(new ValidationMessage("GENERAL", field, message));
        }
        
        public void addErrors(List<String> errorList) {
            valid = false;
            for (String error : errorList) {
                errors.add(new ValidationMessage("GENERAL", "field", error));
            }
        }
        
        public void addWarnings(List<String> warningList) {
            for (String warning : warningList) {
                warnings.add(new ValidationMessage("GENERAL", "field", warning));
            }
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public void setValid(boolean valid) {
            this.valid = valid;
        }
        
        public boolean hasErrors() {
            return !errors.isEmpty();
        }
        
        public boolean hasWarnings() {
            return !warnings.isEmpty();
        }
        
        public boolean hasInfos() {
            return !infos.isEmpty();
        }
        
        public List<ValidationMessage> getErrors() {
            return errors;
        }
        
        public List<ValidationMessage> getWarnings() {
            return warnings;
        }
        
        public List<ValidationMessage> getInfos() {
            return infos;
        }
        
        public boolean isBusinessValidationPassed() {
            return businessValidationPassed;
        }
        
        public void setBusinessValidationPassed(boolean businessValidationPassed) {
            this.businessValidationPassed = businessValidationPassed;
        }
        
        /**
         * 抛出验证异常（如果有错误）
         */
        public void throwIfHasErrors() {
            if (hasErrors()) {
                StringBuilder message = new StringBuilder("数据验证失败：");
                for (ValidationMessage error : errors) {
                    message.append(error.getField()).append(": ").append(error.getMessage()).append("; ");
                }
                throw new CustomerException.InvalidCustomerDataException("validation", message.toString());
            }
        }
        
        public String getSummary() {
            return String.format("验证结果 - 错误: %d, 警告: %d, 信息: %d", 
                               errors.size(), warnings.size(), infos.size());
        }
    }
    
    /**
     * 验证消息类
     */
    public static class ValidationMessage {
        private final String category;
        private final String field;
        private final String message;
        
        public ValidationMessage(String category, String field, String message) {
            this.category = category;
            this.field = field;
            this.message = message;
        }
        
        public String getCategory() {
            return category;
        }
        
        public String getField() {
            return field;
        }
        
        public String getMessage() {
            return message;
        }
        
        @Override
        public String toString() {
            return String.format("[%s] %s: %s", category, field, message);
        }
    }
}