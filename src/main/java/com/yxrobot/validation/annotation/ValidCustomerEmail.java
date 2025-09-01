package com.yxrobot.validation.annotation;

import com.yxrobot.validation.validator.CustomerEmailValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 客户邮箱地址验证注解
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CustomerEmailValidator.class)
@Documented
public @interface ValidCustomerEmail {
    
    String message() default "邮箱地址格式不正确";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    /**
     * 是否允许临时邮箱
     */
    boolean allowTemporary() default false;
    
    /**
     * 是否要求可信域名
     */
    boolean requireTrusted() default false;
    
    /**
     * 是否必填
     */
    boolean required() default false;
}