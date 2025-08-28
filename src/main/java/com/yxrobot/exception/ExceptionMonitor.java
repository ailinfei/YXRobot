package com.yxrobot.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 异常监控器
 * 用于监控和统计系统异常情况
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@Component
public class ExceptionMonitor {
    
    private static final Logger logger = LoggerFactory.getLogger(ExceptionMonitor.class);
    
    // 异常统计
    private final Map<String, AtomicLong> exceptionCounts = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> lastOccurrenceTime = new ConcurrentHashMap<>();
    
    // 新闻相关异常统计
    private final AtomicLong newsNotFoundCount = new AtomicLong(0);
    private final AtomicLong newsValidationErrorCount = new AtomicLong(0);
    private final AtomicLong newsStatusErrorCount = new AtomicLong(0);
    private final AtomicLong newsOperationErrorCount = new AtomicLong(0);
    
    // 销售相关异常统计
    private final AtomicLong salesRecordNotFoundCount = new AtomicLong(0);
    private final AtomicLong salesValidationErrorCount = new AtomicLong(0);
    private final AtomicLong salesOperationErrorCount = new AtomicLong(0);
    private final AtomicLong customerNotFoundCount = new AtomicLong(0);
    private final AtomicLong productNotFoundCount = new AtomicLong(0);
    private final AtomicLong salesStaffNotFoundCount = new AtomicLong(0);
    
    /**
     * 记录异常
     * 
     * @param exception 异常对象
     * @param context 异常上下文信息
     */
    public void recordException(Exception exception, Map<String, Object> context) {
        String exceptionType = exception.getClass().getSimpleName();
        
        // 更新统计信息
        exceptionCounts.computeIfAbsent(exceptionType, k -> new AtomicLong(0)).incrementAndGet();
        lastOccurrenceTime.put(exceptionType, LocalDateTime.now());
        
        // 特殊处理新闻相关异常
        if (exception instanceof NewsNotFoundException) {
            newsNotFoundCount.incrementAndGet();
            recordNewsException("NEWS_NOT_FOUND", (NewsNotFoundException) exception, context);
        } else if (exception instanceof NewsValidationException) {
            newsValidationErrorCount.incrementAndGet();
            recordNewsException("NEWS_VALIDATION_ERROR", (NewsValidationException) exception, context);
        } else if (exception instanceof NewsStatusException) {
            newsStatusErrorCount.incrementAndGet();
            recordNewsException("NEWS_STATUS_ERROR", (NewsStatusException) exception, context);
        } else if (exception instanceof NewsOperationException) {
            newsOperationErrorCount.incrementAndGet();
            recordNewsException("NEWS_OPERATION_ERROR", (NewsOperationException) exception, context);
        }
        
        // 特殊处理销售相关异常
        if (exception instanceof SalesRecordNotFoundException) {
            salesRecordNotFoundCount.incrementAndGet();
            recordSalesException("SALES_RECORD_NOT_FOUND", (SalesRecordNotFoundException) exception, context);
        } else if (exception instanceof SalesValidationException) {
            salesValidationErrorCount.incrementAndGet();
            recordSalesException("SALES_VALIDATION_ERROR", (SalesValidationException) exception, context);
        } else if (exception instanceof SalesOperationException) {
            salesOperationErrorCount.incrementAndGet();
            recordSalesException("SALES_OPERATION_ERROR", (SalesOperationException) exception, context);
        } else if (exception instanceof CustomerNotFoundException) {
            customerNotFoundCount.incrementAndGet();
            recordSalesException("CUSTOMER_NOT_FOUND", (CustomerNotFoundException) exception, context);
        } else if (exception instanceof ProductNotFoundException) {
            productNotFoundCount.incrementAndGet();
            recordSalesException("PRODUCT_NOT_FOUND", (ProductNotFoundException) exception, context);
        } else if (exception instanceof SalesStaffNotFoundException) {
            salesStaffNotFoundCount.incrementAndGet();
            recordSalesException("SALES_STAFF_NOT_FOUND", (SalesStaffNotFoundException) exception, context);
        }
        
        // 记录异常日志
        logException(exception, context);
        
        // 检查是否需要告警
        checkAlertThreshold(exceptionType);
    }
    
    /**
     * 记录新闻相关异常的详细信息
     */
    private void recordNewsException(String errorType, Exception exception, Map<String, Object> context) {
        Map<String, Object> newsContext = new HashMap<>(context);
        
        if (exception instanceof NewsNotFoundException) {
            NewsNotFoundException nfe = (NewsNotFoundException) exception;
            newsContext.put("newsId", nfe.getNewsId());
        } else if (exception instanceof NewsValidationException) {
            NewsValidationException nve = (NewsValidationException) exception;
            newsContext.put("field", nve.getField());
            newsContext.put("value", nve.getValue());
            newsContext.put("validationErrors", nve.getValidationErrors());
        } else if (exception instanceof NewsStatusException) {
            NewsStatusException nse = (NewsStatusException) exception;
            newsContext.put("newsId", nse.getNewsId());
            newsContext.put("currentStatus", nse.getCurrentStatus());
            newsContext.put("targetStatus", nse.getTargetStatus());
        } else if (exception instanceof NewsOperationException) {
            NewsOperationException noe = (NewsOperationException) exception;
            newsContext.put("operation", noe.getOperation());
            newsContext.put("newsId", noe.getNewsId());
            newsContext.put("errorCode", noe.getErrorCode());
        }
        
        logger.warn("新闻异常记录 - 类型: {}, 详情: {}", errorType, newsContext);
    }
    
    /**
     * 记录销售相关异常的详细信息
     */
    private void recordSalesException(String errorType, Exception exception, Map<String, Object> context) {
        Map<String, Object> salesContext = new HashMap<>(context);
        
        if (exception instanceof SalesRecordNotFoundException) {
            SalesRecordNotFoundException srfe = (SalesRecordNotFoundException) exception;
            salesContext.put("salesRecordId", srfe.getSalesRecordId());
        } else if (exception instanceof SalesValidationException) {
            SalesValidationException sve = (SalesValidationException) exception;
            salesContext.put("field", sve.getField());
            salesContext.put("value", sve.getValue());
        } else if (exception instanceof SalesOperationException) {
            SalesOperationException soe = (SalesOperationException) exception;
            salesContext.put("operation", soe.getOperation());
            salesContext.put("target", soe.getTarget());
        } else if (exception instanceof CustomerNotFoundException) {
            CustomerNotFoundException cnfe = (CustomerNotFoundException) exception;
            salesContext.put("customerId", cnfe.getCustomerId());
        } else if (exception instanceof ProductNotFoundException) {
            ProductNotFoundException pnfe = (ProductNotFoundException) exception;
            salesContext.put("productId", pnfe.getProductId());
        } else if (exception instanceof SalesStaffNotFoundException) {
            SalesStaffNotFoundException ssnfe = (SalesStaffNotFoundException) exception;
            salesContext.put("salesStaffId", ssnfe.getSalesStaffId());
        }
        
        logger.warn("销售异常记录 - 类型: {}, 详情: {}", errorType, salesContext);
    }
    
    /**
     * 记录异常日志
     */
    private void logException(Exception exception, Map<String, Object> context) {
        String exceptionType = exception.getClass().getSimpleName();
        String message = exception.getMessage();
        
        // 构建日志信息
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("异常监控 - ");
        logBuilder.append("类型: ").append(exceptionType);
        logBuilder.append(", 消息: ").append(message);
        
        if (context != null && !context.isEmpty()) {
            logBuilder.append(", 上下文: ").append(context);
        }
        
        // 根据异常类型选择日志级别
        if (isBusinessException(exception)) {
            logger.warn(logBuilder.toString());
        } else {
            logger.error(logBuilder.toString(), exception);
        }
    }
    
    /**
     * 判断是否为业务异常
     */
    private boolean isBusinessException(Exception exception) {
        return exception instanceof NewsNotFoundException ||
               exception instanceof NewsValidationException ||
               exception instanceof NewsStatusException ||
               exception instanceof PlatformLinkException ||
               exception instanceof SalesRecordNotFoundException ||
               exception instanceof SalesValidationException ||
               exception instanceof CustomerNotFoundException ||
               exception instanceof ProductNotFoundException ||
               exception instanceof SalesStaffNotFoundException;
    }
    
    /**
     * 检查告警阈值
     */
    private void checkAlertThreshold(String exceptionType) {
        AtomicLong count = exceptionCounts.get(exceptionType);
        if (count != null) {
            long currentCount = count.get();
            
            // 设置不同异常类型的告警阈值
            int threshold = getAlertThreshold(exceptionType);
            
            if (currentCount > 0 && currentCount % threshold == 0) {
                logger.error("异常告警 - 异常类型: {} 在短时间内发生了 {} 次", exceptionType, currentCount);
                // 这里可以集成告警系统，如发送邮件、短信等
            }
        }
    }
    
    /**
     * 获取异常类型的告警阈值
     */
    private int getAlertThreshold(String exceptionType) {
        switch (exceptionType) {
            case "NewsNotFoundException":
                return 10; // 新闻未找到异常阈值
            case "NewsValidationException":
                return 20; // 验证异常阈值
            case "NewsStatusException":
                return 5;  // 状态异常阈值
            case "NewsOperationException":
                return 5;  // 操作异常阈值
            case "SalesRecordNotFoundException":
                return 15; // 销售记录未找到异常阈值
            case "SalesValidationException":
                return 25; // 销售验证异常阈值
            case "SalesOperationException":
                return 8;  // 销售操作异常阈值
            case "CustomerNotFoundException":
                return 12; // 客户未找到异常阈值
            case "ProductNotFoundException":
                return 12; // 产品未找到异常阈值
            case "SalesStaffNotFoundException":
                return 10; // 销售人员未找到异常阈值
            case "NullPointerException":
                return 1;  // 空指针异常阈值（严重）
            case "RuntimeException":
                return 3;  // 运行时异常阈值
            default:
                return 50; // 默认阈值
        }
    }
    
    /**
     * 获取异常统计信息
     */
    public Map<String, Object> getExceptionStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 总体统计
        Map<String, Long> exceptionCountMap = new HashMap<>();
        exceptionCounts.forEach((type, count) -> exceptionCountMap.put(type, count.get()));
        statistics.put("exceptionCounts", exceptionCountMap);
        statistics.put("lastOccurrenceTime", new HashMap<>(lastOccurrenceTime));
        
        // 新闻相关异常统计
        Map<String, Long> newsExceptionStats = new HashMap<>();
        newsExceptionStats.put("newsNotFound", newsNotFoundCount.get());
        newsExceptionStats.put("newsValidationError", newsValidationErrorCount.get());
        newsExceptionStats.put("newsStatusError", newsStatusErrorCount.get());
        newsExceptionStats.put("newsOperationError", newsOperationErrorCount.get());
        statistics.put("newsExceptionStats", newsExceptionStats);
        
        // 销售相关异常统计
        Map<String, Long> salesExceptionStats = new HashMap<>();
        salesExceptionStats.put("salesRecordNotFound", salesRecordNotFoundCount.get());
        salesExceptionStats.put("salesValidationError", salesValidationErrorCount.get());
        salesExceptionStats.put("salesOperationError", salesOperationErrorCount.get());
        salesExceptionStats.put("customerNotFound", customerNotFoundCount.get());
        salesExceptionStats.put("productNotFound", productNotFoundCount.get());
        salesExceptionStats.put("salesStaffNotFound", salesStaffNotFoundCount.get());
        statistics.put("salesExceptionStats", salesExceptionStats);
        
        return statistics;
    }
    
    /**
     * 重置统计信息
     */
    public void resetStatistics() {
        exceptionCounts.clear();
        lastOccurrenceTime.clear();
        newsNotFoundCount.set(0);
        newsValidationErrorCount.set(0);
        newsStatusErrorCount.set(0);
        newsOperationErrorCount.set(0);
        
        // 重置销售相关异常统计
        salesRecordNotFoundCount.set(0);
        salesValidationErrorCount.set(0);
        salesOperationErrorCount.set(0);
        customerNotFoundCount.set(0);
        productNotFoundCount.set(0);
        salesStaffNotFoundCount.set(0);
        
        logger.info("异常统计信息已重置");
    }
    
    /**
     * 获取新闻异常统计
     */
    public Map<String, Long> getNewsExceptionStatistics() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("notFound", newsNotFoundCount.get());
        stats.put("validation", newsValidationErrorCount.get());
        stats.put("status", newsStatusErrorCount.get());
        stats.put("operation", newsOperationErrorCount.get());
        return stats;
    }
    
    /**
     * 获取销售异常统计
     */
    public Map<String, Long> getSalesExceptionStatistics() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("salesRecordNotFound", salesRecordNotFoundCount.get());
        stats.put("salesValidation", salesValidationErrorCount.get());
        stats.put("salesOperation", salesOperationErrorCount.get());
        stats.put("customerNotFound", customerNotFoundCount.get());
        stats.put("productNotFound", productNotFoundCount.get());
        stats.put("salesStaffNotFound", salesStaffNotFoundCount.get());
        return stats;
    }
    
    /**
     * 检查系统健康状态
     */
    public boolean isSystemHealthy() {
        // 检查是否有严重异常
        AtomicLong npeCount = exceptionCounts.get("NullPointerException");
        if (npeCount != null && npeCount.get() > 0) {
            return false;
        }
        
        // 检查运行时异常数量
        AtomicLong runtimeCount = exceptionCounts.get("RuntimeException");
        if (runtimeCount != null && runtimeCount.get() > 10) {
            return false;
        }
        
        return true;
    }
}