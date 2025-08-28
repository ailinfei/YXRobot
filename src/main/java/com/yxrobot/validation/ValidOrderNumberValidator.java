package com.yxrobot.validation;

import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * 订单号格式验证器
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-27
 */
public class ValidOrderNumberValidator implements ConstraintValidator<ValidOrderNumber, String> {
    
    private static final Pattern ORDER_NUMBER_PATTERN = Pattern.compile("^[A-Z0-9]{6,20}$");
    
    @Override
    public void initialize(ValidOrderNumber constraintAnnotation) {
        // 初始化方法，可以在这里获取注解参数
    }
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 空值由@NotBlank等其他注解处理
        if (!StringUtils.hasText(value)) {
            return true;
        }
        
        return ORDER_NUMBER_PATTERN.matcher(value).matches();
    }
}