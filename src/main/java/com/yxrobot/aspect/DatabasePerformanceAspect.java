package com.yxrobot.aspect;

import com.yxrobot.service.ManagedDevicePerformanceMonitorService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 数据库性能监控切面
 * 自动监控MyBatis Mapper方法的执行时间
 */
@Aspect
@Component
public class DatabasePerformanceAspect {

    private static final Logger logger = LoggerFactory.getLogger(DatabasePerformanceAspect.class);
    
    @Autowired
    private ManagedDevicePerformanceMonitorService performanceMonitorService;
    
    /**
     * 监控ManagedDevice相关的Mapper方法性能
     */
    @Around("execution(* com.yxrobot.mapper.ManagedDevice*.*(..))")
    public Object monitorManagedDeviceMapperPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        
        try {
            // 执行原方法
            Object result = joinPoint.proceed();
            
            // 计算执行时间
            long executionTime = System.currentTimeMillis() - startTime;
            
            // 记录数据库查询性能
            performanceMonitorService.recordDatabaseQueryPerformance(methodName, executionTime);
            
            logger.debug("数据库查询完成 - 方法: {}, 执行时间: {}ms", methodName, executionTime);
            
            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("数据库查询异常 - 方法: {}, 执行时间: {}ms, 异常: {}", 
                methodName, executionTime, e.getMessage());
            throw e;
        }
    }
    
    /**
     * 监控Service层方法性能
     */
    @Around("execution(* com.yxrobot.service.ManagedDevice*.*(..))")
    public Object monitorManagedDeviceServicePerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        
        try {
            // 执行原方法
            Object result = joinPoint.proceed();
            
            // 计算执行时间
            long executionTime = System.currentTimeMillis() - startTime;
            
            // 记录业务方法性能
            if (executionTime > 1000) { // 只记录超过1秒的业务方法
                performanceMonitorService.recordDatabaseQueryPerformance("service_" + methodName, executionTime);
            }
            
            logger.debug("业务方法完成 - 方法: {}, 执行时间: {}ms", methodName, executionTime);
            
            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("业务方法异常 - 方法: {}, 执行时间: {}ms, 异常: {}", 
                methodName, executionTime, e.getMessage());
            throw e;
        }
    }
}