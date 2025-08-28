package com.yxrobot.aspect;

import com.yxrobot.util.QueryOptimizer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 性能监控切面
 * 
 * 监控公益项目管理相关服务的性能，记录慢查询和性能指标
 * 
 * @author YXRobot
 * @since 1.0.0
 */
@Aspect
@Component
public class PerformanceMonitoringAspect {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceMonitoringAspect.class);

    /**
     * 慢查询阈值（毫秒）
     */
    private static final long SLOW_QUERY_THRESHOLD = 1000;

    @Autowired
    private QueryOptimizer queryOptimizer;

    /**
     * 监控公益服务方法的执行性能
     */
    @Around("execution(* com.yxrobot.service.Charity*Service.*(..))")
    public Object monitorCharityServicePerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getDeclaringType().getSimpleName() + "." + joinPoint.getSignature().getName();
        long startTime = System.currentTimeMillis();
        
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            
            // 记录慢查询
            queryOptimizer.logSlowQuery(methodName, executionTime, SLOW_QUERY_THRESHOLD);
            
            // 记录正常执行日志
            if (executionTime > 500) { // 超过500ms记录警告
                logger.warn("方法执行较慢 - {}: {}ms", methodName, executionTime);
            } else if (logger.isDebugEnabled()) {
                logger.debug("方法执行完成 - {}: {}ms", methodName, executionTime);
            }
            
            return result;
            
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("方法执行异常 - {}: {}ms, 异常: {}", methodName, executionTime, e.getMessage());
            throw e;
        }
    }

    // CacheService监控已移除，因为CacheService已被移除

    /**
     * 监控Mapper方法的执行性能
     */
    @Around("execution(* com.yxrobot.mapper.Charity*Mapper.*(..))")
    public Object monitorMapperPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getDeclaringType().getSimpleName() + "." + joinPoint.getSignature().getName();
        long startTime = System.currentTimeMillis();
        
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            
            // 记录慢查询
            queryOptimizer.logSlowQuery(methodName, executionTime, SLOW_QUERY_THRESHOLD);
            
            // 数据库查询超过200ms记录警告
            if (executionTime > 200) {
                logger.warn("数据库查询较慢 - {}: {}ms", methodName, executionTime);
            } else if (logger.isDebugEnabled()) {
                logger.debug("数据库查询完成 - {}: {}ms", methodName, executionTime);
            }
            
            return result;
            
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("数据库查询异常 - {}: {}ms, 异常: {}", methodName, executionTime, e.getMessage());
            throw e;
        }
    }
}