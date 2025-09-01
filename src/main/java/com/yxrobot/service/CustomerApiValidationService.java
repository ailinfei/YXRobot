package com.yxrobot.service;

import com.yxrobot.dto.CustomerCreateDTO;
import com.yxrobot.dto.CustomerQueryDTO;
import com.yxrobot.dto.CustomerUpdateDTO;
import com.yxrobot.exception.CustomerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 客户API请求参数验证服务
 * 提供完善的API请求参数验证和数据格式验证
 */
@Service
public class CustomerApiValidationService {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerApiValidationService.class);
    
    // 验证规则配置
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\u4e00-\\u9fa5a-zA-Z0-9\\s·]{2,50}$");
    
    // 允许的枚举值
    private static final Set<String> VALID_CUSTOMER_LEVELS = Set.of("REGULAR", "VIP", "PREMIUM");
    private static final Set<String> VALID_CUSTOMER_STATUSES = Set.of("ACTIVE", "INACTIVE", "SUSPENDED", "DELETED");
    private static final Set<String> VALID_SORT_FIELDS = Set.of(
        "customer_name", "created_at", "last_active_at", "total_spent", "customer_value", "phone"
    );
    private static final Set<String> VALID_SORT_ORDERS = Set.of("ASC", "DESC");
    
    /**
     * 验证客户查询参数
     */
    public ValidationResult validateQueryParameters(CustomerQueryDTO queryDTO) {
        logger.debug("开始验证客户查询参数");
        
        ValidationResult result = new ValidationResult();
        
        if (queryDTO == null) {
            result.addError("query", "查询参数不能为空");
            return result;
        }
        
        // 验证分页参数
        validatePaginationParameters(queryDTO, result);
        
        // 验证搜索参数
        validateSearchParameters(queryDTO, result);
        
        // 验证筛选参数
        validateFilterParameters(queryDTO, result);
        
        // 验证排序参数
        validateSortParameters(queryDTO, result);
        
        // 验证时间范围参数
        validateDateRangeParameters(queryDTO, result);
        
        logger.debug("客户查询参数验证完成，错误数: {}, 警告数: {}", 
                    result.getErrors().size(), result.getWarnings().size());
        
        return result;
    }
    
    /**
     * 验证客户创建参数
     */
    public ValidationResult validateCreateParameters(CustomerCreateDTO createDTO) {
        logger.debug("开始验证客户创建参数");
        
        ValidationResult result = new ValidationResult();
        
        if (createDTO == null) {
            result.addError("customer", "客户数据不能为空");
            return result;
        }
        
        // 验证必填字段
        validateRequiredFields(createDTO, result);
        
        // 验证字段格式
        validateFieldFormats(createDTO, result);
        
        // 验证业务规则
        validateBusinessRules(createDTO, result);
        
        // 验证数据完整性
        validateDataIntegrity(createDTO, result);
        
        logger.debug("客户创建参数验证完成，错误数: {}, 警告数: {}", 
                    result.getErrors().size(), result.getWarnings().size());
        
        return result;
    }
    
    /**
     * 验证客户更新参数
     */
    public ValidationResult validateUpdateParameters(Long customerId, CustomerUpdateDTO updateDTO) {
        logger.debug("开始验证客户更新参数，客户ID: {}", customerId);
        
        ValidationResult result = new ValidationResult();
        
        // 验证客户ID
        if (customerId == null || customerId <= 0) {
            result.addError("customerId", "客户ID必须是正整数");
            return result;
        }
        
        if (updateDTO == null) {
            result.addError("customer", "更新数据不能为空");
            return result;
        }
        
        // 验证更新字段格式（只验证提供的字段）
        validateUpdateFieldFormats(updateDTO, result);
        
        // 验证更新业务规则
        validateUpdateBusinessRules(updateDTO, result);
        
        // 验证更新数据完整性
        validateUpdateDataIntegrity(updateDTO, result);
        
        logger.debug("客户更新参数验证完成，错误数: {}, 警告数: {}", 
                    result.getErrors().size(), result.getWarnings().size());
        
        return result;
    }
    
    /**
     * 验证分页参数
     */
    private void validatePaginationParameters(CustomerQueryDTO queryDTO, ValidationResult result) {
        Integer page = queryDTO.getPage();
        Integer pageSize = queryDTO.getPageSize();
        
        if (page != null && page < 1) {
            result.addError("page", "页码必须大于0");
        }
        
        if (page != null && page > 10000) {
            result.addWarning("page", "页码过大，可能影响查询性能");
        }
        
        if (pageSize != null && (pageSize < 1 || pageSize > 100)) {
            result.addError("pageSize", "每页大小必须在1-100之间");
        }
        
        if (pageSize != null && pageSize > 50) {
            result.addWarning("pageSize", "每页大小较大，可能影响查询性能");
        }
    }
    
    /**
     * 验证搜索参数
     */
    private void validateSearchParameters(CustomerQueryDTO queryDTO, ValidationResult result) {
        String keyword = queryDTO.getKeyword();
        
        if (StringUtils.hasText(keyword)) {
            // 检查关键词长度
            if (keyword.length() < 2) {
                result.addWarning("keyword", "搜索关键词过短，可能影响搜索效果");
            }
            
            if (keyword.length() > 100) {
                result.addError("keyword", "搜索关键词长度不能超过100个字符");
            }
            
            // 检查特殊字符
            if (keyword.contains("'") || keyword.contains("\"") || keyword.contains(";")) {
                result.addError("keyword", "搜索关键词包含非法字符");
            }
            
            // 检查SQL注入风险
            if (containsSqlInjectionRisk(keyword)) {
                result.addError("keyword", "搜索关键词存在安全风险");
            }
        }
    }
    
    /**
     * 验证筛选参数
     */
    private void validateFilterParameters(CustomerQueryDTO queryDTO, ValidationResult result) {
        // 验证客户等级
        if (StringUtils.hasText(queryDTO.getLevel())) {
            if (!VALID_CUSTOMER_LEVELS.contains(queryDTO.getLevel().toUpperCase())) {
                result.addError("level", "无效的客户等级: " + queryDTO.getLevel());
            }
        }
        
        // 验证客户状态
        if (StringUtils.hasText(queryDTO.getStatus())) {
            if (!VALID_CUSTOMER_STATUSES.contains(queryDTO.getStatus().toUpperCase())) {
                result.addError("status", "无效的客户状态: " + queryDTO.getStatus());
            }
        }
        
        // 验证地区
        if (StringUtils.hasText(queryDTO.getRegion())) {
            if (queryDTO.getRegion().length() > 50) {
                result.addError("region", "地区名称长度不能超过50个字符");
            }
        }
        
        // 验证行业
        if (StringUtils.hasText(queryDTO.getIndustry())) {
            if (queryDTO.getIndustry().length() > 50) {
                result.addError("industry", "行业名称长度不能超过50个字符");
            }
        }
    }
    
    /**
     * 验证排序参数
     */
    private void validateSortParameters(CustomerQueryDTO queryDTO, ValidationResult result) {
        String sortBy = queryDTO.getSortBy();
        String sortOrder = queryDTO.getSortOrder();
        
        if (StringUtils.hasText(sortBy)) {
            if (!VALID_SORT_FIELDS.contains(sortBy)) {
                result.addError("sortBy", "无效的排序字段: " + sortBy);
            }
        }
        
        if (StringUtils.hasText(sortOrder)) {
            if (!VALID_SORT_ORDERS.contains(sortOrder.toUpperCase())) {
                result.addError("sortOrder", "无效的排序方向: " + sortOrder);
            }
        }
        
        // 检查排序字段和方向的组合
        if (StringUtils.hasText(sortBy) && !StringUtils.hasText(sortOrder)) {
            result.addWarning("sortOrder", "指定了排序字段但未指定排序方向，将使用默认值DESC");
        }
    }
    
    /**
     * 验证时间范围参数
     */
    private void validateDateRangeParameters(CustomerQueryDTO queryDTO, ValidationResult result) {
        String startDateStr = queryDTO.getStartDate();
        String endDateStr = queryDTO.getEndDate();
        
        LocalDate startDate = null;
        LocalDate endDate = null;
        
        // 解析开始日期
        if (startDateStr != null) {
            try {
                startDate = LocalDate.parse(startDateStr);
            } catch (Exception e) {
                result.addError("startDateFormat", "开始日期格式不正确，请使用YYYY-MM-DD格式");
            }
        }
        
        // 解析结束日期
        if (endDateStr != null) {
            try {
                endDate = LocalDate.parse(endDateStr);
            } catch (Exception e) {
                result.addError("endDateFormat", "结束日期格式不正确，请使用YYYY-MM-DD格式");
            }
        }
        
        // 验证日期范围
        if (startDate != null && endDate != null) {
            if (startDate.isAfter(endDate)) {
                result.addError("dateRange", "开始日期不能晚于结束日期");
            }
            
            // 检查时间范围是否过大
            if (startDate.plusYears(5).isBefore(endDate)) {
                result.addWarning("dateRange", "时间范围过大，可能影响查询性能");
            }
        }
        
        if (startDate != null && startDate.isAfter(LocalDate.now())) {
            result.addWarning("startDate", "开始日期在未来，可能没有查询结果");
        }
        
        if (endDate != null && endDate.isAfter(LocalDate.now())) {
            result.addWarning("endDate", "结束日期在未来，将以当前日期为准");
        }
    }
    
    /**
     * 验证必填字段
     */
    private void validateRequiredFields(CustomerCreateDTO createDTO, ValidationResult result) {
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
            if (!NAME_PATTERN.matcher(createDTO.getName()).matches()) {
                result.addError("name", "客户姓名格式不正确，只能包含中文、英文、数字、空格和中点，长度2-50个字符");
            }
        }
        
        // 验证电话号码
        if (StringUtils.hasText(createDTO.getPhone())) {
            if (!PHONE_PATTERN.matcher(createDTO.getPhone()).matches()) {
                result.addError("phone", "电话号码格式不正确，请输入11位手机号码");
            }
        }
        
        // 验证邮箱地址
        if (StringUtils.hasText(createDTO.getEmail())) {
            if (!EMAIL_PATTERN.matcher(createDTO.getEmail()).matches()) {
                result.addError("email", "邮箱地址格式不正确");
            }
        }
        
        // 验证公司名称
        if (StringUtils.hasText(createDTO.getCompany())) {
            if (createDTO.getCompany().length() > 100) {
                result.addError("company", "公司名称长度不能超过100个字符");
            }
        }
        
        // 验证备注
        if (StringUtils.hasText(createDTO.getNotes())) {
            if (createDTO.getNotes().length() > 500) {
                result.addError("notes", "备注长度不能超过500个字符");
            }
        }
    }
    
    /**
     * 验证业务规则
     */
    private void validateBusinessRules(CustomerCreateDTO createDTO, ValidationResult result) {
        // 验证客户等级
        if (StringUtils.hasText(createDTO.getLevel())) {
            if (!VALID_CUSTOMER_LEVELS.contains(createDTO.getLevel().toUpperCase())) {
                result.addError("level", "无效的客户等级: " + createDTO.getLevel());
            }
        }
        
        // 验证客户状态
        if (StringUtils.hasText(createDTO.getStatus())) {
            if (!VALID_CUSTOMER_STATUSES.contains(createDTO.getStatus().toUpperCase())) {
                result.addError("status", "无效的客户状态: " + createDTO.getStatus());
            }
        }
        
        // 验证标签
        if (createDTO.getTags() != null) {
            if (createDTO.getTags().size() > 10) {
                result.addError("tags", "标签数量不能超过10个");
            }
            
            for (String tag : createDTO.getTags()) {
                if (!StringUtils.hasText(tag)) {
                    result.addError("tags", "标签内容不能为空");
                } else if (tag.length() > 20) {
                    result.addError("tags", "单个标签长度不能超过20个字符");
                }
            }
        }
    }
    
    /**
     * 验证数据完整性
     */
    private void validateDataIntegrity(CustomerCreateDTO createDTO, ValidationResult result) {
        // 检查邮箱和电话的一致性
        if (StringUtils.hasText(createDTO.getEmail()) && StringUtils.hasText(createDTO.getPhone())) {
            // 这里可以添加更复杂的一致性检查逻辑
        }
        
        // 检查客户等级和状态的合理性
        if ("PREMIUM".equals(createDTO.getLevel()) && "INACTIVE".equals(createDTO.getStatus())) {
            result.addWarning("levelStatus", "高级客户通常不应该是非活跃状态");
        }
        
        // 检查必要信息的完整性
        if (!StringUtils.hasText(createDTO.getEmail()) && !StringUtils.hasText(createDTO.getCompany())) {
            result.addWarning("contact", "建议至少提供邮箱或公司信息之一");
        }
    }
    
    /**
     * 验证更新字段格式
     */
    private void validateUpdateFieldFormats(CustomerUpdateDTO updateDTO, ValidationResult result) {
        // 只验证提供的字段
        if (StringUtils.hasText(updateDTO.getName())) {
            if (!NAME_PATTERN.matcher(updateDTO.getName()).matches()) {
                result.addError("name", "客户姓名格式不正确");
            }
        }
        
        if (StringUtils.hasText(updateDTO.getPhone())) {
            if (!PHONE_PATTERN.matcher(updateDTO.getPhone()).matches()) {
                result.addError("phone", "电话号码格式不正确");
            }
        }
        
        if (StringUtils.hasText(updateDTO.getEmail())) {
            if (!EMAIL_PATTERN.matcher(updateDTO.getEmail()).matches()) {
                result.addError("email", "邮箱地址格式不正确");
            }
        }
        
        if (StringUtils.hasText(updateDTO.getCompany())) {
            if (updateDTO.getCompany().length() > 100) {
                result.addError("company", "公司名称长度不能超过100个字符");
            }
        }
        
        if (StringUtils.hasText(updateDTO.getNotes())) {
            if (updateDTO.getNotes().length() > 500) {
                result.addError("notes", "备注长度不能超过500个字符");
            }
        }
    }
    
    /**
     * 验证更新业务规则
     */
    private void validateUpdateBusinessRules(CustomerUpdateDTO updateDTO, ValidationResult result) {
        if (StringUtils.hasText(updateDTO.getLevel())) {
            if (!VALID_CUSTOMER_LEVELS.contains(updateDTO.getLevel().toUpperCase())) {
                result.addError("level", "无效的客户等级: " + updateDTO.getLevel());
            }
        }
        
        if (StringUtils.hasText(updateDTO.getStatus())) {
            if (!VALID_CUSTOMER_STATUSES.contains(updateDTO.getStatus().toUpperCase())) {
                result.addError("status", "无效的客户状态: " + updateDTO.getStatus());
            }
        }
        
        if (updateDTO.getTags() != null) {
            if (updateDTO.getTags().size() > 10) {
                result.addError("tags", "标签数量不能超过10个");
            }
        }
    }
    
    /**
     * 验证更新数据完整性
     */
    private void validateUpdateDataIntegrity(CustomerUpdateDTO updateDTO, ValidationResult result) {
        // 检查更新的合理性
        if ("PREMIUM".equals(updateDTO.getLevel()) && "INACTIVE".equals(updateDTO.getStatus())) {
            result.addWarning("levelStatus", "高级客户通常不应该是非活跃状态");
        }
    }
    
    /**
     * 检查SQL注入风险
     */
    private boolean containsSqlInjectionRisk(String input) {
        String lowerInput = input.toLowerCase();
        String[] sqlKeywords = {
            "select", "insert", "update", "delete", "drop", "create", "alter", 
            "union", "or", "and", "--", "/*", "*/", "xp_", "sp_"
        };
        
        for (String keyword : sqlKeywords) {
            if (lowerInput.contains(keyword)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 验证结果类
     */
    public static class ValidationResult {
        private final List<ValidationIssue> errors = new ArrayList<>();
        private final List<ValidationIssue> warnings = new ArrayList<>();
        private final List<ValidationIssue> infos = new ArrayList<>();
        
        public void addError(String field, String message) {
            errors.add(new ValidationIssue(field, message, "ERROR"));
        }
        
        public void addWarning(String field, String message) {
            warnings.add(new ValidationIssue(field, message, "WARNING"));
        }
        
        public void addInfo(String field, String message) {
            infos.add(new ValidationIssue(field, message, "INFO"));
        }
        
        public boolean isValid() {
            return errors.isEmpty();
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
        
        public List<ValidationIssue> getInfos() {
            return infos;
        }
        
        public List<ValidationIssue> getAllIssues() {
            List<ValidationIssue> allIssues = new ArrayList<>();
            allIssues.addAll(errors);
            allIssues.addAll(warnings);
            allIssues.addAll(infos);
            return allIssues;
        }
        
        /**
         * 抛出验证异常（如果有错误）
         */
        public void throwIfHasErrors() {
            if (hasErrors()) {
                StringBuilder message = new StringBuilder("参数验证失败：");
                for (ValidationIssue error : errors) {
                    message.append(error.getField()).append(": ").append(error.getMessage()).append("; ");
                }
                throw new CustomerException.InvalidCustomerDataException("validation", message.toString());
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
        private final LocalDateTime timestamp;
        
        public ValidationIssue(String field, String message, String type) {
            this.field = field;
            this.message = message;
            this.type = type;
            this.timestamp = LocalDateTime.now();
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
        
        public LocalDateTime getTimestamp() {
            return timestamp;
        }
        
        @Override
        public String toString() {
            return String.format("%s[%s]: %s", type, field, message);
        }
    }
}