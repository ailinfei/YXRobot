package com.yxrobot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 性能配置类
 * 配置平台链接系统的性能参数
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-22
 */
@Configuration
@ConfigurationProperties(prefix = "platform.link.performance")
public class PerformanceConfig {
    
    /**
     * 缓存配置
     */
    private Cache cache = new Cache();
    
    /**
     * 验证配置
     */
    private Validation validation = new Validation();
    
    /**
     * 统计配置
     */
    private Stats stats = new Stats();
    
    /**
     * 监控配置
     */
    private Monitoring monitoring = new Monitoring();
    
    // Getters and Setters
    public Cache getCache() {
        return cache;
    }
    
    public void setCache(Cache cache) {
        this.cache = cache;
    }
    
    public Validation getValidation() {
        return validation;
    }
    
    public void setValidation(Validation validation) {
        this.validation = validation;
    }
    
    public Stats getStats() {
        return stats;
    }
    
    public void setStats(Stats stats) {
        this.stats = stats;
    }
    
    public Monitoring getMonitoring() {
        return monitoring;
    }
    
    public void setMonitoring(Monitoring monitoring) {
        this.monitoring = monitoring;
    }
    
    /**
     * 缓存配置
     */
    public static class Cache {
        /**
         * 统计数据缓存过期时间（分钟）
         */
        private int statsExpireMinutes = 30;
        
        /**
         * 区域配置缓存过期时间（小时）
         */
        private int regionConfigExpireHours = 24;
        
        /**
         * 验证结果缓存过期时间（分钟）
         */
        private int validationResultExpireMinutes = 60;
        
        /**
         * 是否启用缓存预热
         */
        private boolean enableWarmup = true;
        
        // Getters and Setters
        public int getStatsExpireMinutes() {
            return statsExpireMinutes;
        }
        
        public void setStatsExpireMinutes(int statsExpireMinutes) {
            this.statsExpireMinutes = statsExpireMinutes;
        }
        
        public int getRegionConfigExpireHours() {
            return regionConfigExpireHours;
        }
        
        public void setRegionConfigExpireHours(int regionConfigExpireHours) {
            this.regionConfigExpireHours = regionConfigExpireHours;
        }
        
        public int getValidationResultExpireMinutes() {
            return validationResultExpireMinutes;
        }
        
        public void setValidationResultExpireMinutes(int validationResultExpireMinutes) {
            this.validationResultExpireMinutes = validationResultExpireMinutes;
        }
        
        public boolean isEnableWarmup() {
            return enableWarmup;
        }
        
        public void setEnableWarmup(boolean enableWarmup) {
            this.enableWarmup = enableWarmup;
        }
    }
    
    /**
     * 验证配置
     */
    public static class Validation {
        /**
         * 连接超时时间（毫秒）
         */
        private int connectionTimeoutMs = 10000;
        
        /**
         * 读取超时时间（毫秒）
         */
        private int readTimeoutMs = 15000;
        
        /**
         * 最大并发验证数
         */
        private int maxConcurrentValidations = 20;
        
        /**
         * 批量验证最大数量
         */
        private int maxBatchSize = 100;
        
        /**
         * 重试次数
         */
        private int retryCount = 2;
        
        // Getters and Setters
        public int getConnectionTimeoutMs() {
            return connectionTimeoutMs;
        }
        
        public void setConnectionTimeoutMs(int connectionTimeoutMs) {
            this.connectionTimeoutMs = connectionTimeoutMs;
        }
        
        public int getReadTimeoutMs() {
            return readTimeoutMs;
        }
        
        public void setReadTimeoutMs(int readTimeoutMs) {
            this.readTimeoutMs = readTimeoutMs;
        }
        
        public int getMaxConcurrentValidations() {
            return maxConcurrentValidations;
        }
        
        public void setMaxConcurrentValidations(int maxConcurrentValidations) {
            this.maxConcurrentValidations = maxConcurrentValidations;
        }
        
        public int getMaxBatchSize() {
            return maxBatchSize;
        }
        
        public void setMaxBatchSize(int maxBatchSize) {
            this.maxBatchSize = maxBatchSize;
        }
        
        public int getRetryCount() {
            return retryCount;
        }
        
        public void setRetryCount(int retryCount) {
            this.retryCount = retryCount;
        }
    }
    
    /**
     * 统计配置
     */
    public static class Stats {
        /**
         * 统计数据计算批次大小
         */
        private int calculationBatchSize = 1000;
        
        /**
         * 是否启用实时统计
         */
        private boolean enableRealTimeStats = true;
        
        /**
         * 统计数据刷新间隔（分钟）
         */
        private int refreshIntervalMinutes = 30;
        
        /**
         * 历史数据保留天数
         */
        private int historyRetentionDays = 90;
        
        // Getters and Setters
        public int getCalculationBatchSize() {
            return calculationBatchSize;
        }
        
        public void setCalculationBatchSize(int calculationBatchSize) {
            this.calculationBatchSize = calculationBatchSize;
        }
        
        public boolean isEnableRealTimeStats() {
            return enableRealTimeStats;
        }
        
        public void setEnableRealTimeStats(boolean enableRealTimeStats) {
            this.enableRealTimeStats = enableRealTimeStats;
        }
        
        public int getRefreshIntervalMinutes() {
            return refreshIntervalMinutes;
        }
        
        public void setRefreshIntervalMinutes(int refreshIntervalMinutes) {
            this.refreshIntervalMinutes = refreshIntervalMinutes;
        }
        
        public int getHistoryRetentionDays() {
            return historyRetentionDays;
        }
        
        public void setHistoryRetentionDays(int historyRetentionDays) {
            this.historyRetentionDays = historyRetentionDays;
        }
    }
    
    /**
     * 监控配置
     */
    public static class Monitoring {
        /**
         * 是否启用监控
         */
        private boolean enabled = true;
        
        /**
         * 健康检查间隔（分钟）
         */
        private int healthCheckIntervalMinutes = 5;
        
        /**
         * 性能监控间隔（分钟）
         */
        private int performanceMonitorIntervalMinutes = 15;
        
        /**
         * 失败率告警阈值（百分比）
         */
        private double failureRateAlertThreshold = 20.0;
        
        /**
         * 非活跃率告警阈值（百分比）
         */
        private double inactiveRateAlertThreshold = 30.0;
        
        /**
         * 响应时间告警阈值（毫秒）
         */
        private int responseTimeAlertThresholdMs = 5000;
        
        // Getters and Setters
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        public int getHealthCheckIntervalMinutes() {
            return healthCheckIntervalMinutes;
        }
        
        public void setHealthCheckIntervalMinutes(int healthCheckIntervalMinutes) {
            this.healthCheckIntervalMinutes = healthCheckIntervalMinutes;
        }
        
        public int getPerformanceMonitorIntervalMinutes() {
            return performanceMonitorIntervalMinutes;
        }
        
        public void setPerformanceMonitorIntervalMinutes(int performanceMonitorIntervalMinutes) {
            this.performanceMonitorIntervalMinutes = performanceMonitorIntervalMinutes;
        }
        
        public double getFailureRateAlertThreshold() {
            return failureRateAlertThreshold;
        }
        
        public void setFailureRateAlertThreshold(double failureRateAlertThreshold) {
            this.failureRateAlertThreshold = failureRateAlertThreshold;
        }
        
        public double getInactiveRateAlertThreshold() {
            return inactiveRateAlertThreshold;
        }
        
        public void setInactiveRateAlertThreshold(double inactiveRateAlertThreshold) {
            this.inactiveRateAlertThreshold = inactiveRateAlertThreshold;
        }
        
        public int getResponseTimeAlertThresholdMs() {
            return responseTimeAlertThresholdMs;
        }
        
        public void setResponseTimeAlertThresholdMs(int responseTimeAlertThresholdMs) {
            this.responseTimeAlertThresholdMs = responseTimeAlertThresholdMs;
        }
    }
}