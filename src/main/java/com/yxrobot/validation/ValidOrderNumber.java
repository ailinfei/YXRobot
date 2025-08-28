package com.yxrobot.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 订单号格式验证注解
 * 验证订单号格式是否符合业务规范
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-27
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidOrderNumberValidator.class)
@Documented
public @interface ValidOrderNumber {
    
    String message() default "订单号格式不正确，应为6-20位字母数字组合";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}