package com.yxrobot.validation;

import com.yxrobot.exception.CharityException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 表单验证工具类
 * 提供通用的表单字段验证方法，确保必填字段完整性
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-19
 */
@Component
public class FormValidationUtils {
    
    // 常用正则表达式
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^1[3-9]\\d{9}$|^0\\d{2,3}-?\\d{7,8}$|^400-?\\d{3}-?\\d{4}$"
    );
    
    private static final Pattern CHINESE_NAME_PATTERN = Pattern.compile(
        "^[\\u4e00-\\u9fa5]{2,10}$"
    );
    
    private static final Pattern ORGANIZATION_NAME_PATTERN = Pattern.compile(
        "^[\\u4e00-\\u9fa5a-zA-Z0-9\\s\\-_()（）]{2,50}$"
    );
    
    /**
     * 验证必填字符串字段
     * 
     * @param value 字段值
     * @param fieldName 字段名称
     * @param errors 错误列表
     */
    public void validateRequiredString(String value, String fieldName, List<String> errors) {
        if (isNullOrEmpty(value)) {
            errors.add(fieldName + "不能为空");
        }
    }
    
    /**
     * 验证必填字符串字段（带长度限制）
     * 
     * @param value 字段值
     * @param fieldName 字段名称
     * @param maxLength 最大长度
     * @param errors 错误列表
     */
    public void validateRequiredString(String value, String fieldName, int maxLength, List<String> errors) {
        if (isNullOrEmpty(value)) {
            errors.add(fieldName + "不能为空");
        } else if (value.trim().length() > maxLength) {
            errors.add(fieldName + "长度不能超过" + maxLength + "个字符");
        }
    }
    
    /**
     * 验证可选字符串字段（带长度限制）
     * 
     * @param value 字段值
     * @param fieldName 字段名称
     * @param maxLength 最大长度
     * @param errors 错误列表
     */
    public void validateOptionalString(String value, String fieldName, int maxLength, List<String> errors) {
        if (value != null && !value.trim().isEmpty() && value.trim().length() > maxLength) {
            errors.add(fieldName + "长度不能超过" + maxLength + "个字符");
        }
    }
    
    /**
     * 验证必填整数字段
     * 
     * @param value 字段值
     * @param fieldName 字段名称
     * @param errors 错误列表
     */
    public void validateRequiredInteger(Integer value, String fieldName, List<String> errors) {
        if (value == null) {
            errors.add(fieldName + "不能为空");
        }
    }
    
    /**
     * 验证必填整数字段（带范围限制）
     * 
     * @param value 字段值
     * @param fieldName 字段名称
     * @param min 最小值
     * @param max 最大值
     * @param errors 错误列表
     */
    public void validateRequiredInteger(Integer value, String fieldName, int min, int max, List<String> errors) {
        if (value == null) {
            errors.add(fieldName + "不能为空");
        } else if (value < min || value > max) {
            errors.add(fieldName + "必须在" + min + "到" + max + "之间");
        }
    }
    
    /**
     * 验证可选整数字段（带范围限制）
     * 
     * @param value 字段值
     * @param fieldName 字段名称
     * @param min 最小值
     * @param max 最大值
     * @param errors 错误列表
     */
    public void validateOptionalInteger(Integer value, String fieldName, int min, int max, List<String> errors) {
        if (value != null && (value < min || value > max)) {
            errors.add(fieldName + "必须在" + min + "到" + max + "之间");
        }
    }
    
    /**
     * 验证必填BigDecimal字段
     * 
     * @param value 字段值
     * @param fieldName 字段名称
     * @param errors 错误列表
     */
    public void validateRequiredBigDecimal(BigDecimal value, String fieldName, List<String> errors) {
        if (value == null) {
            errors.add(fieldName + "不能为空");
        }
    }
    
    /**
     * 验证必填BigDecimal字段（带范围限制）
     * 
     * @param value 字段值
     * @param fieldName 字段名称
     * @param min 最小值
     * @param max 最大值
     * @param errors 错误列表
     */
    public void validateRequiredBigDecimal(BigDecimal value, String fieldName, BigDecimal min, BigDecimal max, List<String> errors) {
        if (value == null) {
            errors.add(fieldName + "不能为空");
        } else if (value.compareTo(min) < 0 || value.compareTo(max) > 0) {
            errors.add(fieldName + "必须在" + min + "到" + max + "之间");
        }
    }
    
    /**
     * 验证可选BigDecimal字段（带范围限制）
     * 
     * @param value 字段值
     * @param fieldName 字段名称
     * @param min 最小值
     * @param max 最大值
     * @param errors 错误列表
     */
    public void validateOptionalBigDecimal(BigDecimal value, String fieldName, BigDecimal min, BigDecimal max, List<String> errors) {
        if (value != null && (value.compareTo(min) < 0 || value.compareTo(max) > 0)) {
            errors.add(fieldName + "必须在" + min + "到" + max + "之间");
        }
    }
    
    /**
     * 验证必填日期字段
     * 
     * @param value 字段值
     * @param fieldName 字段名称
     * @param errors 错误列表
     */
    public void validateRequiredDate(LocalDate value, String fieldName, List<String> errors) {
        if (value == null) {
            errors.add(fieldName + "不能为空");
        }
    }
    
    /**
     * 验证必填日期字段（带范围限制）
     * 
     * @param value 字段值
     * @param fieldName 字段名称
     * @param minDate 最早日期
     * @param maxDate 最晚日期
     * @param errors 错误列表
     */
    public void validateRequiredDate(LocalDate value, String fieldName, LocalDate minDate, LocalDate maxDate, List<String> errors) {
        if (value == null) {
            errors.add(fieldName + "不能为空");
        } else if (value.isBefore(minDate) || value.isAfter(maxDate)) {
            errors.add(fieldName + "必须在" + minDate + "到" + maxDate + "之间");
        }
    }
    
    /**
     * 验证可选日期字段（带范围限制）
     * 
     * @param value 字段值
     * @param fieldName 字段名称
     * @param minDate 最早日期
     * @param maxDate 最晚日期
     * @param errors 错误列表
     */
    public void validateOptionalDate(LocalDate value, String fieldName, LocalDate minDate, LocalDate maxDate, List<String> errors) {
        if (value != null && (value.isBefore(minDate) || value.isAfter(maxDate))) {
            errors.add(fieldName + "必须在" + minDate + "到" + maxDate + "之间");
        }
    }
    
    /**
     * 验证必填日期时间字段
     * 
     * @param value 字段值
     * @param fieldName 字段名称
     * @param errors 错误列表
     */
    public void validateRequiredDateTime(LocalDateTime value, String fieldName, List<String> errors) {
        if (value == null) {
            errors.add(fieldName + "不能为空");
        }
    }
    
    /**
     * 验证邮箱格式
     * 
     * @param email 邮箱地址
     * @param fieldName 字段名称
     * @param required 是否必填
     * @param errors 错误列表
     */
    public void validateEmail(String email, String fieldName, boolean required, List<String> errors) {
        if (required && isNullOrEmpty(email)) {
            errors.add(fieldName + "不能为空");
        } else if (!isNullOrEmpty(email) && !EMAIL_PATTERN.matcher(email.trim()).matches()) {
            errors.add(fieldName + "格式不正确");
        }
    }
    
    /**
     * 验证手机号格式
     * 
     * @param phone 手机号
     * @param fieldName 字段名称
     * @param required 是否必填
     * @param errors 错误列表
     */
    public void validatePhone(String phone, String fieldName, boolean required, List<String> errors) {
        if (required && isNullOrEmpty(phone)) {
            errors.add(fieldName + "不能为空");
        } else if (!isNullOrEmpty(phone) && !PHONE_PATTERN.matcher(phone.trim()).matches()) {
            errors.add(fieldName + "格式不正确，请输入有效的手机号码或固定电话");
        }
    }
    
    /**
     * 验证中文姓名格式
     * 
     * @param name 姓名
     * @param fieldName 字段名称
     * @param required 是否必填
     * @param errors 错误列表
     */
    public void validateChineseName(String name, String fieldName, boolean required, List<String> errors) {
        if (required && isNullOrEmpty(name)) {
            errors.add(fieldName + "不能为空");
        } else if (!isNullOrEmpty(name) && !CHINESE_NAME_PATTERN.matcher(name.trim()).matches()) {
            errors.add(fieldName + "格式不正确，请输入2-10个中文字符");
        }
    }
    
    /**
     * 验证机构名称格式
     * 
     * @param name 机构名称
     * @param fieldName 字段名称
     * @param required 是否必填
     * @param errors 错误列表
     */
    public void validateOrganizationName(String name, String fieldName, boolean required, List<String> errors) {
        if (required && isNullOrEmpty(name)) {
            errors.add(fieldName + "不能为空");
        } else if (!isNullOrEmpty(name) && !ORGANIZATION_NAME_PATTERN.matcher(name.trim()).matches()) {
            errors.add(fieldName + "格式不正确，只能包含中文、英文、数字、空格和常用符号，长度2-50个字符");
        }
    }
    
    /**
     * 验证枚举值
     * 
     * @param value 字段值
     * @param fieldName 字段名称
     * @param validValues 有效值数组
     * @param required 是否必填
     * @param errors 错误列表
     */
    public void validateEnum(String value, String fieldName, String[] validValues, boolean required, List<String> errors) {
        if (required && isNullOrEmpty(value)) {
            errors.add(fieldName + "不能为空");
        } else if (!isNullOrEmpty(value)) {
            boolean isValid = false;
            for (String validValue : validValues) {
                if (validValue.equals(value.trim())) {
                    isValid = true;
                    break;
                }
            }
            if (!isValid) {
                errors.add(fieldName + "不正确，必须是：" + String.join("、", validValues) + " 中的一种");
            }
        }
    }
    
    /**
     * 验证两个数值的逻辑关系
     * 
     * @param value1 数值1
     * @param value2 数值2
     * @param field1Name 字段1名称
     * @param field2Name 字段2名称
     * @param relationship 关系类型（"<=", ">=", "<", ">", "=="）
     * @param errors 错误列表
     */
    public void validateNumberRelationship(Integer value1, Integer value2, String field1Name, String field2Name, 
                                         String relationship, List<String> errors) {
        if (value1 != null && value2 != null) {
            boolean isValid = false;
            String errorMessage = "";
            
            switch (relationship) {
                case "<=":
                    isValid = value1 <= value2;
                    errorMessage = field1Name + "不能超过" + field2Name;
                    break;
                case ">=":
                    isValid = value1 >= value2;
                    errorMessage = field1Name + "不能小于" + field2Name;
                    break;
                case "<":
                    isValid = value1 < value2;
                    errorMessage = field1Name + "必须小于" + field2Name;
                    break;
                case ">":
                    isValid = value1 > value2;
                    errorMessage = field1Name + "必须大于" + field2Name;
                    break;
                case "==":
                    isValid = value1.equals(value2);
                    errorMessage = field1Name + "必须等于" + field2Name;
                    break;
            }
            
            if (!isValid) {
                errors.add(errorMessage);
            }
        }
    }
    
    /**
     * 验证两个BigDecimal数值的逻辑关系
     * 
     * @param value1 数值1
     * @param value2 数值2
     * @param field1Name 字段1名称
     * @param field2Name 字段2名称
     * @param relationship 关系类型（"<=", ">=", "<", ">", "=="）
     * @param errors 错误列表
     */
    public void validateBigDecimalRelationship(BigDecimal value1, BigDecimal value2, String field1Name, String field2Name, 
                                             String relationship, List<String> errors) {
        if (value1 != null && value2 != null) {
            boolean isValid = false;
            String errorMessage = "";
            
            switch (relationship) {
                case "<=":
                    isValid = value1.compareTo(value2) <= 0;
                    errorMessage = field1Name + "不能超过" + field2Name;
                    break;
                case ">=":
                    isValid = value1.compareTo(value2) >= 0;
                    errorMessage = field1Name + "不能小于" + field2Name;
                    break;
                case "<":
                    isValid = value1.compareTo(value2) < 0;
                    errorMessage = field1Name + "必须小于" + field2Name;
                    break;
                case ">":
                    isValid = value1.compareTo(value2) > 0;
                    errorMessage = field1Name + "必须大于" + field2Name;
                    break;
                case "==":
                    isValid = value1.compareTo(value2) == 0;
                    errorMessage = field1Name + "必须等于" + field2Name;
                    break;
            }
            
            if (!isValid) {
                errors.add(errorMessage);
            }
        }
    }
    
    /**
     * 验证日期的逻辑关系
     * 
     * @param date1 日期1
     * @param date2 日期2
     * @param field1Name 字段1名称
     * @param field2Name 字段2名称
     * @param relationship 关系类型（"<=", ">=", "<", ">", "=="）
     * @param errors 错误列表
     */
    public void validateDateRelationship(LocalDate date1, LocalDate date2, String field1Name, String field2Name, 
                                       String relationship, List<String> errors) {
        if (date1 != null && date2 != null) {
            boolean isValid = false;
            String errorMessage = "";
            
            switch (relationship) {
                case "<=":
                    isValid = !date1.isAfter(date2);
                    errorMessage = field1Name + "不能晚于" + field2Name;
                    break;
                case ">=":
                    isValid = !date1.isBefore(date2);
                    errorMessage = field1Name + "不能早于" + field2Name;
                    break;
                case "<":
                    isValid = date1.isBefore(date2);
                    errorMessage = field1Name + "必须早于" + field2Name;
                    break;
                case ">":
                    isValid = date1.isAfter(date2);
                    errorMessage = field1Name + "必须晚于" + field2Name;
                    break;
                case "==":
                    isValid = date1.equals(date2);
                    errorMessage = field1Name + "必须等于" + field2Name;
                    break;
            }
            
            if (!isValid) {
                errors.add(errorMessage);
            }
        }
    }
    
    /**
     * 抛出验证异常（如果有错误）
     * 
     * @param errors 错误列表
     * @param operationType 操作类型（用于错误消息）
     */
    public void throwIfHasErrors(List<String> errors, String operationType) {
        if (!errors.isEmpty()) {
            String errorMessage = operationType + "验证失败：" + String.join("; ", errors);
            throw CharityException.validationError(errorMessage, String.join("\n", errors));
        }
    }
    
    /**
     * 创建错误列表
     * 
     * @return 新的错误列表
     */
    public List<String> createErrorList() {
        return new ArrayList<>();
    }
    
    /**
     * 检查字符串是否为空或null
     * 
     * @param str 待检查字符串
     * @return true-为空，false-不为空
     */
    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}