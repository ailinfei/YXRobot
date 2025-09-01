package com.yxrobot.validation;

import com.yxrobot.dto.CustomerCreateDTO;
import com.yxrobot.dto.CustomerUpdateDTO;
import com.yxrobot.entity.CustomerLevel;
import com.yxrobot.entity.CustomerStatus;
import com.yxrobot.exception.CustomerException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 客户表单数据验证器
 * 专门用于验证前端表单提交的客户数据
 */
@Component
public class CustomerFormValidator {
    
    // 正则表达式模式
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\u4e00-\\u9fa5a-zA-Z0-9\\s·]{1,50}$");
    private static final Pattern COMPANY_PATTERN = Pattern.compile("^[\\u4e00-\\u9fa5a-zA-Z0-9\\s()（）&＆-]{1,100}$");
    
    // 验证常量
    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 50;
    private static final int MAX_COMPANY_LENGTH = 100;
    private static final int MAX_NOTES_LENGTH = 500;
    private static final int MAX_TAGS_COUNT = 10;
    private static final int MAX_TAG_LENGTH = 20;
    
    /**
     * 验证客户创建表单数据
     */
    public ValidationResult validateCreateForm(CustomerCreateDTO createDTO) {
        ValidationResult result = new ValidationResult();
        
        if (createDTO == null) {
            result.addError("form", "表单数据不能为空");
            return result;
        }
        
        // 验证必填字段
        validateRequiredFieldsForCreate(createDTO, result);
        
        // 验证字段格式
        validateFieldFormats(createDTO, result);
        
        // 验证业务规则
        validateBusinessRules(createDTO, result);
        
        return result;
    }
    
    /**
     * 验证客户更新表单数据
     */
    public ValidationResult validateUpdateForm(CustomerUpdateDTO updateDTO) {
        ValidationResult result = new ValidationResult();
        
        if (updateDTO == null) {
            result.addError("form", "表单数据不能为空");
            return result;
        }
        
        // 验证字段格式（更新时某些字段可能为空）
        validateFieldFormatsForUpdate(updateDTO, result);
        
        // 验证业务规则
        validateBusinessRulesForUpdate(updateDTO, result);
        
        return result;
    }
    
    /**
     * 验证创建表单的必填字段
     */
    private void validateRequiredFieldsForCreate(CustomerCreateDTO createDTO, ValidationResult result) {
        if (!StringUtils.hasText(createDTO.getName())) {
            result.addError("name", "客户姓名不能为空");
        }
        
        if (!StringUtils.hasText(createDTO.getPhone())) {
            result.addError("phone", "联系电话不能为空");
        }
        
        if (!StringUtils.hasText(createDTO.getLevel())) {
            result.addError("level", "客户等级不能为空");
        }
        
        if (!StringUtils.hasText(createDTO.getStatus())) {
            result.addError("status", "客户状态不能为空");
        }
    }
    
    /**
     * 验证字段格式
     */
    private void validateFieldFormats(CustomerCreateDTO createDTO, ValidationResult result) {
        // 验证客户姓名
        if (StringUtils.hasText(createDTO.getName())) {
            validateCustomerName(createDTO.getName(), result);
        }
        
        // 验证电话号码
        if (StringUtils.hasText(createDTO.getPhone())) {
            validatePhoneNumber(createDTO.getPhone(), result);
        }
        
        // 验证邮箱地址
        if (StringUtils.hasText(createDTO.getEmail())) {
            validateEmailAddress(createDTO.getEmail(), result);
        }
        
        // 验证公司名称
        if (StringUtils.hasText(createDTO.getCompany())) {
            validateCompanyName(createDTO.getCompany(), result);
        }
        
        // 验证备注
        if (StringUtils.hasText(createDTO.getNotes())) {
            validateNotes(createDTO.getNotes(), result);
        }
        
        // 验证标签
        if (createDTO.getTags() != null && !createDTO.getTags().isEmpty()) {
            validateTags(createDTO.getTags(), result);
        }
        
        // 验证地址
        if (createDTO.getAddress() != null) {
            validateAddress(createDTO.getAddress(), result);
        }
    }
    
    /**
     * 验证更新表单的字段格式
     */
    private void validateFieldFormatsForUpdate(CustomerUpdateDTO updateDTO, ValidationResult result) {
        // 验证客户姓名（如果提供）
        if (StringUtils.hasText(updateDTO.getName())) {
            validateCustomerName(updateDTO.getName(), result);
        }
        
        // 验证电话号码（如果提供）
        if (StringUtils.hasText(updateDTO.getPhone())) {
            validatePhoneNumber(updateDTO.getPhone(), result);
        }
        
        // 验证邮箱地址（如果提供）
        if (StringUtils.hasText(updateDTO.getEmail())) {
            validateEmailAddress(updateDTO.getEmail(), result);
        }
        
        // 验证公司名称（如果提供）
        if (StringUtils.hasText(updateDTO.getCompany())) {
            validateCompanyName(updateDTO.getCompany(), result);
        }
        
        // 验证备注（如果提供）
        if (StringUtils.hasText(updateDTO.getNotes())) {
            validateNotes(updateDTO.getNotes(), result);
        }
        
        // 验证标签（如果提供）
        if (updateDTO.getTags() != null) {
            validateTags(updateDTO.getTags(), result);
        }
        
        // 验证地址（如果提供）
        if (updateDTO.getAddress() != null) {
            validateAddress(updateDTO.getAddress(), result);
        }
    }
    
    /**
     * 验证业务规则
     */
    private void validateBusinessRules(CustomerCreateDTO createDTO, ValidationResult result) {
        // 验证客户等级
        if (StringUtils.hasText(createDTO.getLevel())) {
            validateCustomerLevel(createDTO.getLevel(), result);
        }
        
        // 验证客户状态
        if (StringUtils.hasText(createDTO.getStatus())) {
            validateCustomerStatus(createDTO.getStatus(), result);
        }
    }
    
    /**
     * 验证更新表单的业务规则
     */
    private void validateBusinessRulesForUpdate(CustomerUpdateDTO updateDTO, ValidationResult result) {
        // 验证客户等级（如果提供）
        if (StringUtils.hasText(updateDTO.getLevel())) {
            validateCustomerLevel(updateDTO.getLevel(), result);
        }
        
        // 验证客户状态（如果提供）
        if (StringUtils.hasText(updateDTO.getStatus())) {
            validateCustomerStatus(updateDTO.getStatus(), result);
        }
    }
    
    /**
     * 验证客户姓名
     */
    private void validateCustomerName(String name, ValidationResult result) {
        if (name.length() < MIN_NAME_LENGTH) {
            result.addError("name", "客户姓名至少需要" + MIN_NAME_LENGTH + "个字符");
            return;
        }
        
        if (name.length() > MAX_NAME_LENGTH) {
            result.addError("name", "客户姓名不能超过" + MAX_NAME_LENGTH + "个字符");
            return;
        }
        
        if (!NAME_PATTERN.matcher(name).matches()) {
            result.addError("name", "客户姓名只能包含中文、英文、数字、空格和中点");
        }
        
        // 检查是否包含敏感词
        if (containsSensitiveWords(name)) {
            result.addError("name", "客户姓名包含不当内容");
        }
    }
    
    /**
     * 验证电话号码
     */
    private void validatePhoneNumber(String phone, ValidationResult result) {
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            result.addError("phone", "请输入正确的11位手机号码");
            return;
        }
        
        // 检查是否为虚拟号码段
        String prefix = phone.substring(0, 3);
        if (isVirtualPhonePrefix(prefix)) {
            result.addWarning("phone", "检测到虚拟号码段，请确认号码正确性");
        }
    }
    
    /**
     * 验证邮箱地址
     */
    private void validateEmailAddress(String email, ValidationResult result) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            result.addError("email", "请输入正确的邮箱地址格式");
            return;
        }
        
        // 检查邮箱长度
        if (email.length() > 100) {
            result.addError("email", "邮箱地址长度不能超过100个字符");
            return;
        }
        
        // 检查是否为临时邮箱
        if (isTemporaryEmail(email)) {
            result.addWarning("email", "检测到临时邮箱，建议使用常用邮箱");
        }
    }
    
    /**
     * 验证公司名称
     */
    private void validateCompanyName(String company, ValidationResult result) {
        if (company.length() > MAX_COMPANY_LENGTH) {
            result.addError("company", "公司名称不能超过" + MAX_COMPANY_LENGTH + "个字符");
            return;
        }
        
        if (!COMPANY_PATTERN.matcher(company).matches()) {
            result.addError("company", "公司名称包含不支持的字符");
        }
    }
    
    /**
     * 验证备注
     */
    private void validateNotes(String notes, ValidationResult result) {
        if (notes.length() > MAX_NOTES_LENGTH) {
            result.addError("notes", "备注内容不能超过" + MAX_NOTES_LENGTH + "个字符");
        }
        
        // 检查是否包含敏感词
        if (containsSensitiveWords(notes)) {
            result.addError("notes", "备注内容包含不当内容");
        }
    }
    
    /**
     * 验证标签
     */
    private void validateTags(List<String> tags, ValidationResult result) {
        if (tags.size() > MAX_TAGS_COUNT) {
            result.addError("tags", "标签数量不能超过" + MAX_TAGS_COUNT + "个");
            return;
        }
        
        for (int i = 0; i < tags.size(); i++) {
            String tag = tags.get(i);
            
            if (!StringUtils.hasText(tag)) {
                result.addError("tags[" + i + "]", "标签内容不能为空");
                continue;
            }
            
            if (tag.length() > MAX_TAG_LENGTH) {
                result.addError("tags[" + i + "]", "标签长度不能超过" + MAX_TAG_LENGTH + "个字符");
                continue;
            }
            
            // 检查重复标签
            for (int j = i + 1; j < tags.size(); j++) {
                if (tag.equals(tags.get(j))) {
                    result.addError("tags[" + j + "]", "标签不能重复");
                }
            }
        }
    }
    
    /**
     * 验证地址信息
     */
    private void validateAddress(Object address, ValidationResult result) {
        // 这里可以根据具体的地址DTO结构进行验证
        // 暂时简单验证
        if (address == null) {
            result.addError("address", "地址信息不能为空");
        }
    }
    
    /**
     * 验证客户等级
     */
    private void validateCustomerLevel(String level, ValidationResult result) {
        try {
            CustomerLevel.valueOf(level.toUpperCase());
        } catch (IllegalArgumentException e) {
            result.addError("level", "无效的客户等级：" + level);
        }
    }
    
    /**
     * 验证客户状态
     */
    private void validateCustomerStatus(String status, ValidationResult result) {
        try {
            CustomerStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            result.addError("status", "无效的客户状态：" + status);
        }
    }
    
    /**
     * 检查是否包含敏感词
     */
    private boolean containsSensitiveWords(String text) {
        // 简单的敏感词检查，实际项目中应该使用专门的敏感词过滤库
        String[] sensitiveWords = {"测试", "admin", "test", "fuck", "shit"};
        String lowerText = text.toLowerCase();
        
        for (String word : sensitiveWords) {
            if (lowerText.contains(word.toLowerCase())) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 检查是否为虚拟号码段
     */
    private boolean isVirtualPhonePrefix(String prefix) {
        // 虚拟号码段列表（示例）
        String[] virtualPrefixes = {"170", "171", "162", "165", "167"};
        
        for (String virtualPrefix : virtualPrefixes) {
            if (prefix.equals(virtualPrefix)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 检查是否为临时邮箱
     */
    private boolean isTemporaryEmail(String email) {
        // 临时邮箱域名列表（示例）
        String[] tempDomains = {"10minutemail.com", "guerrillamail.com", "tempmail.org"};
        String domain = email.substring(email.indexOf("@") + 1).toLowerCase();
        
        for (String tempDomain : tempDomains) {
            if (domain.equals(tempDomain)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 验证结果类
     */
    public static class ValidationResult {
        private final List<ValidationError> errors = new ArrayList<>();
        private final List<ValidationError> warnings = new ArrayList<>();
        
        public void addError(String field, String message) {
            errors.add(new ValidationError(field, message, "ERROR"));
        }
        
        public void addWarning(String field, String message) {
            warnings.add(new ValidationError(field, message, "WARNING"));
        }
        
        public boolean hasErrors() {
            return !errors.isEmpty();
        }
        
        public boolean hasWarnings() {
            return !warnings.isEmpty();
        }
        
        public List<ValidationError> getErrors() {
            return errors;
        }
        
        public List<ValidationError> getWarnings() {
            return warnings;
        }
        
        public List<ValidationError> getAllIssues() {
            List<ValidationError> allIssues = new ArrayList<>(errors);
            allIssues.addAll(warnings);
            return allIssues;
        }
        
        /**
         * 抛出验证异常（如果有错误）
         */
        public void throwIfHasErrors() {
            if (hasErrors()) {
                StringBuilder message = new StringBuilder("表单验证失败：");
                for (ValidationError error : errors) {
                    message.append(error.getField()).append(": ").append(error.getMessage()).append("; ");
                }
                throw new CustomerException.InvalidCustomerDataException("form", message.toString());
            }
        }
    }
    
    /**
     * 验证错误类
     */
    public static class ValidationError {
        private final String field;
        private final String message;
        private final String type;
        
        public ValidationError(String field, String message, String type) {
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