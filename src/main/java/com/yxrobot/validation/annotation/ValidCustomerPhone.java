package com.yxrobot.validation.annotation;

import com.yxrobot.validation.validator.CustomerPhoneValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 客户电话号码验证注解
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CustomerPhoneValidator.class)
@Documented
public @interface ValidCustomerPhone {
    
    String message() default "电话号码格式不正确";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    /**
     * 是否允许虚拟运营商号码
     */
    boolean allowVirtual() default true;
    
    /**
     * 是否允许测试号码
     */
    boolean allowTest() default false;
    
    /**
     * 是否必填
     */
    boolean required() default false;
}