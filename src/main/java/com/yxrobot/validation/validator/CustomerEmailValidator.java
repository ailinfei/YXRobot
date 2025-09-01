package com.yxrobot.validation.validator;

import com.yxrobot.validation.EmailValidator;
import com.yxrobot.validation.annotation.ValidCustomerEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 客户邮箱地址验证器实现
 */
public class CustomerEmailValidator implements ConstraintValidator<ValidCustomerEmail, String> {
    
    @Autowired
    private EmailValidator emailValidator;
    
    private boolean allowTemporary;
    private boolean requireTrusted;
    private boolean required;
    
    // 可信邮箱域名
    private static final Set<String> TRUSTED_DOMAINS = new HashSet<>(Arrays.asList(
        "gmail.com", "yahoo.com", "hotmail.com", "outlook.com", "live.com",
        "qq.com", "163.com", "126.com", "sina.com", "sohu.com",
        "foxmail.com", "aliyun.com", "yeah.net"
    ));
    
    @Override
    public void initialize(ValidCustomerEmail constraintAnnotation) {
        this.allowTemporary = constraintAnnotation.allowTemporary();
        this.requireTrusted = constraintAnnotation.requireTrusted();
        this.required = constraintAnnotation.required();
    }
    
    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        // 如果不是必填且为空，则通过验证
        if (!required && !StringUtils.hasText(email)) {
            return true;
        }
        
        // 如果是必填但为空，则验证失败
        if (required && !StringUtils.hasText(email)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("邮箱地址不能为空")
                   .addConstraintViolation();
            return false;
        }
        
        // 使用邮箱验证器进行详细验证
        EmailValidator.EmailValidationResult result = 
            emailValidator.validateEmail(email);
        
        if (!result.isValid()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(result.getErrorMessage())
                   .addConstraintViolation();
            return false;
        }
        
        // 检查临时邮箱限制
        if (!allowTemporary && result.isTemporary()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("不允许使用临时邮箱地址")
                   .addConstraintViolation();
            return false;
        }
        
        // 检查可信域名要求
        if (requireTrusted && !isTrustedDomain(result.getDomain())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("请使用可信的邮箱服务商")
                   .addConstraintViolation();
            return false;
        }
        
        return true;
    }
    
    /**
     * 检查是否为可信域名
     */
    private boolean isTrustedDomain(String domain) {
        return TRUSTED_DOMAINS.contains(domain.toLowerCase());
    }
}