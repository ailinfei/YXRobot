package com.yxrobot.validation;

import com.yxrobot.entity.Customer;
import com.yxrobot.entity.CustomerLevel;
import com.yxrobot.entity.CustomerStatus;
import com.yxrobot.exception.CustomerException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * 客户数据验证器
 * 提供客户相关数据的验证逻辑
 */
@Component
public class CustomerValidator {
    
    // 正则表达式模式
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\u4e00-\\u9fa5a-zA-Z0-9\\s]{1,50}$");
    
    // 验证常量
    private static final int MAX_NAME_LENGTH = 50;
    private static final int MAX_COMPANY_LENGTH = 100;
    private static final int MAX_NOTES_LENGTH = 500;
    private static final int MAX_TAGS_COUNT = 10;
    private static final int MAX_TAG_LENGTH = 20;
    
    /**
     * 验证客户创建数据
     */
    public void validateCustomerForCreate(Customer customer) {
        if (customer == null) {
            throw new CustomerException.InvalidCustomerDataException("customer", "客户对象不能为空");
        }
        
        // 验证必需字段
        validateRequiredFields(customer);
        
        // 验证字段格式
        validateFieldFormats(customer);
        
        // 验证业务规则
        validateBusinessRules(customer);
    }
    
    /**
     * 验证客户更新数据
     */
    public void validateCustomerForUpdate(Customer customer) {
        if (customer == null) {
            throw new CustomerException.InvalidCustomerDataException("customer", "客户对象不能为空");
        }
        
        if (customer.getId() == null) {
            throw new CustomerException.InvalidCustomerDataException("id", "客户ID不能为空");
        }
        
        // 验证字段格式（更新时某些字段可能为空）
        validateFieldFormatsForUpdate(customer);
        
        // 验证业务规则
        validateBusinessRules(customer);
    }
    
    /**
     * 验证必需字段
     */
    private void validateRequiredFields(Customer customer) {
        if (!StringUtils.hasText(customer.getCustomerName())) {
            throw new CustomerException.InvalidCustomerDataException("name", "客户姓名不能为空");
        }
        
        if (!StringUtils.hasText(customer.getPhone())) {
            throw new CustomerException.InvalidCustomerDataException("phone", "联系电话不能为空");
        }
        
        if (customer.getCustomerLevel() == null) {
            throw new CustomerException.InvalidCustomerDataException("level", "客户等级不能为空");
        }
        
        if (customer.getCustomerStatus() == null) {
            throw new CustomerException.InvalidCustomerDataException("status", "客户状态不能为空");
        }
    }
    
    /**
     * 验证字段格式
     */
    private void validateFieldFormats(Customer customer) {
        // 验证客户姓名
        validateCustomerName(customer.getCustomerName());
        
        // 验证电话号码
        validatePhoneNumber(customer.getPhone());
        
        // 验证邮箱地址
        if (StringUtils.hasText(customer.getEmail())) {
            validateEmailAddress(customer.getEmail());
        }
        
        // 验证公司名称
        if (StringUtils.hasText(customer.getContactPerson())) {
            validateCompanyName(customer.getContactPerson());
        }
        
        // 验证备注
        if (StringUtils.hasText(customer.getNotes())) {
            validateNotes(customer.getNotes());
        }
        
        // 验证标签
        if (customer.getCustomerTags() != null) {
            validateTags(customer.getCustomerTags());
        }
    }
    
    /**
     * 验证字段格式（更新时）
     */
    private void validateFieldFormatsForUpdate(Customer customer) {
        // 验证客户姓名（如果提供）
        if (StringUtils.hasText(customer.getCustomerName())) {
            validateCustomerName(customer.getCustomerName());
        }
        
        // 验证电话号码（如果提供）
        if (StringUtils.hasText(customer.getPhone())) {
            validatePhoneNumber(customer.getPhone());
        }
        
        // 验证邮箱地址（如果提供）
        if (StringUtils.hasText(customer.getEmail())) {
            validateEmailAddress(customer.getEmail());
        }
        
        // 验证公司名称（如果提供）
        if (StringUtils.hasText(customer.getContactPerson())) {
            validateCompanyName(customer.getContactPerson());
        }
        
        // 验证备注（如果提供）
        if (StringUtils.hasText(customer.getNotes())) {
            validateNotes(customer.getNotes());
        }
        
        // 验证标签（如果提供）
        if (customer.getCustomerTags() != null) {
            validateTags(customer.getCustomerTags());
        }
    }
    
    /**
     * 验证业务规则
     */
    private void validateBusinessRules(Customer customer) {
        // 验证客户等级
        if (customer.getCustomerLevel() != null) {
            validateCustomerLevel(customer.getCustomerLevel());
        }
        
        // 验证客户状态
        if (customer.getCustomerStatus() != null) {
            validateCustomerStatus(customer.getCustomerStatus());
        }
        
        // 验证客户价值评分
        if (customer.getCustomerValue() != null) {
            validateCustomerValue(customer.getCustomerValue());
        }
        
        // 验证累计消费金额
        if (customer.getTotalSpent() != null) {
            validateTotalSpent(customer.getTotalSpent());
        }
    }
    
    /**
     * 验证客户姓名
     */
    private void validateCustomerName(String name) {
        if (name.length() > MAX_NAME_LENGTH) {
            throw new com.yxrobot.exception.ValidationException("name", name, 
                "客户姓名长度不能超过" + MAX_NAME_LENGTH + "个字符");
        }
        
        if (!NAME_PATTERN.matcher(name).matches()) {
            throw new com.yxrobot.exception.ValidationException("name", name, 
                "客户姓名只能包含中文、英文、数字和空格");
        }
    }
    
    /**
     * 验证电话号码
     */
    private void validatePhoneNumber(String phone) {
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new com.yxrobot.exception.ValidationException("phone", phone, 
                "电话号码格式不正确，请输入11位手机号码");
        }
    }
    
    /**
     * 验证邮箱地址
     */
    private void validateEmailAddress(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new com.yxrobot.exception.ValidationException("email", email, 
                "邮箱地址格式不正确");
        }
    }
    
    /**
     * 验证公司名称
     */
    private void validateCompanyName(String company) {
        if (company.length() > MAX_COMPANY_LENGTH) {
            throw new com.yxrobot.exception.ValidationException("company", company, 
                "公司名称长度不能超过" + MAX_COMPANY_LENGTH + "个字符");
        }
    }
    
    /**
     * 验证备注
     */
    private void validateNotes(String notes) {
        if (notes.length() > MAX_NOTES_LENGTH) {
            throw new com.yxrobot.exception.ValidationException("notes", notes, 
                "备注长度不能超过" + MAX_NOTES_LENGTH + "个字符");
        }
    }
    
    /**
     * 验证标签
     */
    private void validateTags(java.util.List<String> tags) {
        if (tags.size() > MAX_TAGS_COUNT) {
            throw new com.yxrobot.exception.ValidationException("tags", tags.toString(), 
                "标签数量不能超过" + MAX_TAGS_COUNT + "个");
        }
        
        for (String tag : tags) {
            if (!StringUtils.hasText(tag)) {
                throw new com.yxrobot.exception.ValidationException("tags", tag, 
                    "标签内容不能为空");
            }
            
            if (tag.length() > MAX_TAG_LENGTH) {
                throw new com.yxrobot.exception.ValidationException("tags", tag, 
                    "单个标签长度不能超过" + MAX_TAG_LENGTH + "个字符");
            }
        }
    }
    
    /**
     * 验证客户等级
     */
    private void validateCustomerLevel(CustomerLevel level) {
        // CustomerLevel是枚举，Spring会自动验证
        // 这里可以添加额外的业务规则验证
    }
    
    /**
     * 验证客户状态
     */
    private void validateCustomerStatus(CustomerStatus status) {
        // CustomerStatus是枚举，Spring会自动验证
        // 这里可以添加额外的业务规则验证
    }
    
    /**
     * 验证客户价值评分
     */
    private void validateCustomerValue(java.math.BigDecimal customerValue) {
        if (customerValue.compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new com.yxrobot.exception.ValidationException("customerValue", customerValue.toString(), 
                "客户价值评分不能为负数");
        }
        
        if (customerValue.compareTo(new java.math.BigDecimal("10")) > 0) {
            throw new com.yxrobot.exception.ValidationException("customerValue", customerValue.toString(), 
                "客户价值评分不能超过10");
        }
    }
    
    /**
     * 验证累计消费金额
     */
    private void validateTotalSpent(java.math.BigDecimal totalSpent) {
        if (totalSpent.compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new com.yxrobot.exception.ValidationException("totalSpent", totalSpent.toString(), 
                "累计消费金额不能为负数");
        }
    }
    
    /**
     * 验证查询参数
     */
    public void validateQueryParams(String keyword, String level, String status, 
                                   Integer page, Integer pageSize) {
        // 验证分页参数
        if (page != null && page < 1) {
            throw new CustomerException.InvalidCustomerDataException("page", "页码必须大于0");
        }
        
        if (pageSize != null && (pageSize < 1 || pageSize > 100)) {
            throw new CustomerException.InvalidCustomerDataException("pageSize", 
                "每页大小必须在1-100之间");
        }
        
        // 验证等级参数
        if (StringUtils.hasText(level)) {
            try {
                CustomerLevel.valueOf(level.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new CustomerException.InvalidCustomerDataException("level", 
                    "无效的客户等级: " + level);
            }
        }
        
        // 验证状态参数
        if (StringUtils.hasText(status)) {
            try {
                CustomerStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new CustomerException.InvalidCustomerDataException("status", 
                    "无效的客户状态: " + status);
            }
        }
        
        // 验证关键词长度
        if (StringUtils.hasText(keyword) && keyword.length() > 100) {
            throw new CustomerException.InvalidCustomerDataException("keyword", 
                "搜索关键词长度不能超过100个字符");
        }
    }
    
    /**
     * 验证客户ID
     */
    public void validateCustomerId(Long customerId) {
        if (customerId == null || customerId <= 0) {
            throw new CustomerException.InvalidCustomerDataException("customerId", 
                "客户ID必须是正整数");
        }
    }
}