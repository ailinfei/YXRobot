package com.yxrobot.validation.validator;

import com.yxrobot.validation.PhoneNumberValidator;
import com.yxrobot.validation.annotation.ValidCustomerPhone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 客户电话号码验证器实现
 */
public class CustomerPhoneValidator implements ConstraintValidator<ValidCustomerPhone, String> {
    
    @Autowired
    private PhoneNumberValidator phoneNumberValidator;
    
    private boolean allowVirtual;
    private boolean allowTest;
    private boolean required;
    
    @Override
    public void initialize(ValidCustomerPhone constraintAnnotation) {
        this.allowVirtual = constraintAnnotation.allowVirtual();
        this.allowTest = constraintAnnotation.allowTest();
        this.required = constraintAnnotation.required();
    }
    
    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        // 如果不是必填且为空，则通过验证
        if (!required && !StringUtils.hasText(phone)) {
            return true;
        }
        
        // 如果是必填但为空，则验证失败
        if (required && !StringUtils.hasText(phone)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("电话号码不能为空")
                   .addConstraintViolation();
            return false;
        }
        
        // 使用电话号码验证器进行详细验证
        PhoneNumberValidator.PhoneValidationResult result = 
            phoneNumberValidator.validatePhone(phone);
        
        if (!result.isValid()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(result.getErrorMessage())
                   .addConstraintViolation();
            return false;
        }
        
        // 检查虚拟号码限制
        if (!allowVirtual && "虚拟运营商".equals(result.getCarrier())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("不允许使用虚拟运营商号码")
                   .addConstraintViolation();
            return false;
        }
        
        // 检查测试号码限制
        if (!allowTest && isTestNumber(phone)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("不允许使用测试号码")
                   .addConstraintViolation();
            return false;
        }
        
        return true;
    }
    
    /**
     * 检查是否为测试号码
     */
    private boolean isTestNumber(String phone) {
        String[] testNumbers = {
            "13800138000", "13800138001", "13800138002",
            "18888888888", "16666666666", "19999999999"
        };
        
        for (String testNumber : testNumbers) {
            if (phone.equals(testNumber)) {
                return true;
            }
        }
        
        return false;
    }
}