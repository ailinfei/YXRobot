package com.yxrobot.cache;

import com.yxrobot.dto.DeviceUtilizationDTO;
import com.yxrobot.dto.RentalStatsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 租赁数据缓存服务
 * 提高统计和图表数据查询性能
 * 
 * @author Kiro
 * @date 2025-01-28
 */
@Service
public class RentalCacheService {
    
    private static final Logger logger = LoggerFactory.getLogger(RentalCacheService.class);
    
    // 缓存过期时间（分钟）
    private static final int STATS_CACHE_EXPIRE_MINUTES = 5;      // 统计数据缓存5分钟
    private static final int CHART_CACHE_EXPIRE_MINUTES = 10;     // 图表数据缓存10分钟
    private static final int DEVICE_CACHE_EXPIRE_MINUTES = 3;     // 设备数据缓存3分钟
    private static final int TODAY_CACHE_EXPIRE_MINUTES = 1;      // 今日数据缓存1分钟
    
    // 缓存存储
    private final Map<String, CacheEntry<RentalStatsDTO>> statsCache = new ConcurrentHashMap<>();
    private final Map<String, CacheEntry<Map<String, Object>>> chartCache = new ConcurrentHashMap<>();
    private final Map<String, CacheEntry<List<DeviceUtilizationDTO>>> deviceCache = new ConcurrentHashMap<>();
    private final Map<String, CacheEntry<Map<String, Object>>> todayStatsCache = new ConcurrentHashMap<>();
    
    /**
     * 缓存条目
     */
    private static class CacheEntry<T> {
        private final T data;
        private final LocalDateTime expireTime;
        
        public CacheEntry(T data, int expireMinutes) {
            this.data = data;
            this.expireTime = LocalDateTime.now().plusMinutes(expireMinutes);
        }
        
        public T getData() {
            return data;
        }
        
        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expireTime);
        }
        
        public LocalDateTime getExpireTime() {
            return expireTime;
        }
    }
    
    /**
     * 获取租赁统计数据缓存
     */
    public RentalStatsDTO getStatsCache(String key) {
        CacheEntry<RentalStatsDTO> entry = statsCache.get(key);
        if (entry != null && !entry.isExpired()) {
            logger.debug("命中租赁统计缓存: {}", key);
            return entry.getData();
        }
        
        if (entry != null && entry.isExpired()) {
            statsCache.remove(key);
            logger.debug("租赁统计缓存已过期，已清除: {}", key);
        }
        
        return null;
    }
    
    /**
     * 设置租赁统计数据缓存
     */
    public void putStatsCache(String key, RentalStatsDTO data) {
        if (data != null) {
            statsCache.put(key, new CacheEntry<>(data, STATS_CACHE_EXPIRE_MINUTES));
            logger.debug("设置租赁统计缓存: {}", key);
        }
    }
    
    /**
     * 获取图表数据缓存
     */
    public Map<String, Object> getChartCache(String key) {
        CacheEntry<Map<String, Object>> entry = chartCache.get(key);
        if (entry != null && !entry.isExpired()) {
            logger.debug("命中图表数据缓存: {}", key);
            return entry.getData();
        }
        
        if (entry != null && entry.isExpired()) {
            chartCache.remove(key);
            logger.debug("图表数据缓存已过期，已清除: {}", key);
        }
        
        return null;
    }
    
    /**
     * 设置图表数据缓存
     */
    public void putChartCache(String key, Map<String, Object> data) {
        if (data != null) {
            chartCache.put(key, new CacheEntry<>(data, CHART_CACHE_EXPIRE_MINUTES));
            logger.debug("设置图表数据缓存: {}", key);
        }
    }
    
    /**
     * 获取设备数据缓存
     */
    public List<DeviceUtilizationDTO> getDeviceCache(String key) {
        CacheEntry<List<DeviceUtilizationDTO>> entry = deviceCache.get(key);
        if (entry != null && !entry.isExpired()) {
            logger.debug("命中设备数据缓存: {}", key);
            return entry.getData();
        }
        
        if (entry != null && entry.isExpired()) {
            deviceCache.remove(key);
            logger.debug("设备数据缓存已过期，已清除: {}", key);
        }
        
        return null;
    }
    
    /**
     * 设置设备数据缓存
     */
    public void putDeviceCache(String key, List<DeviceUtilizationDTO> data) {
        if (data != null) {
            deviceCache.put(key, new CacheEntry<>(data, DEVICE_CACHE_EXPIRE_MINUTES));
            logger.debug("设置设备数据缓存: {}", key);
        }
    }
    
    /**
     * 获取今日统计数据缓存
     */
    public Map<String, Object> getTodayStatsCache(String key) {
        CacheEntry<Map<String, Object>> entry = todayStatsCache.get(key);
        if (entry != null && !entry.isExpired()) {
            logger.debug("命中今日统计缓存: {}", key);
            return entry.getData();
        }
        
        if (entry != null && entry.isExpired()) {
            todayStatsCache.remove(key);
            logger.debug("今日统计缓存已过期，已清除: {}", key);
        }
        
        return null;
    }
    
    /**
     * 设置今日统计数据缓存
     */
    public void putTodayStatsCache(String key, Map<String, Object> data) {
        if (data != null) {
            todayStatsCache.put(key, new CacheEntry<>(data, TODAY_CACHE_EXPIRE_MINUTES));
            logger.debug("设置今日统计缓存: {}", key);
        }
    }
    
    /**
     * 生成缓存键
     */
    public String generateStatsKey(String startDate, String endDate) {
        return String.format("stats:%s:%s", 
                           startDate != null ? startDate : "null", 
                           endDate != null ? endDate : "null");
    }
    
    /**
     * 生成图表缓存键
     */
    public String generateChartKey(String chartType, String period, String startDate, String endDate, String type) {
        return String.format("chart:%s:%s:%s:%s:%s", 
                           chartType, 
                           period != null ? period : "null",
                           startDate != null ? startDate : "null", 
                           endDate != null ? endDate : "null",
                           type != null ? type : "null");
    }
    
    /**
     * 生成设备缓存键
     */
    public String generateDeviceKey(Integer page, Integer pageSize, String keyword, 
                                   String deviceModel, String currentStatus, String region) {
        return String.format("device:%d:%d:%s:%s:%s:%s", 
                           page != null ? page : 1,
                           pageSize != null ? pageSize : 20,
                           keyword != null ? keyword : "null",
                           deviceModel != null ? deviceModel : "null",
                           currentStatus != null ? currentStatus : "null",
                           region != null ? region : "null");
    }
    
    /**
     * 生成今日统计缓存键
     */
    public String generateTodayStatsKey() {
        return "today_stats:" + LocalDateTime.now().toLocalDate().toString();
    }
    
    /**
     * 清除所有缓存
     */
    public void clearAllCache() {
        statsCache.clear();
        chartCache.clear();
        deviceCache.clear();
        todayStatsCache.clear();
        logger.info("已清除所有租赁数据缓存");
    }
    
    /**
     * 清除过期缓存
     */
    public void clearExpiredCache() {
        int clearedCount = 0;
        
        // 清除过期的统计缓存
        clearedCount += clearExpiredEntries(statsCache);
        
        // 清除过期的图表缓存
        clearedCount += clearExpiredEntries(chartCache);
        
        // 清除过期的设备缓存
        clearedCount += clearExpiredEntries(deviceCache);
        
        // 清除过期的今日统计缓存
        clearedCount += clearExpiredEntries(todayStatsCache);
        
        if (clearedCount > 0) {
            logger.info("已清除 {} 个过期缓存条目", clearedCount);
        }
    }
    
    /**
     * 清除指定缓存Map中的过期条目
     */
    private <T> int clearExpiredEntries(Map<String, CacheEntry<T>> cache) {
        int clearedCount = 0;
        var iterator = cache.entrySet().iterator();
        
        while (iterator.hasNext()) {
            var entry = iterator.next();
            if (entry.getValue().isExpired()) {
                iterator.remove();
                clearedCount++;
            }
        }
        
        return clearedCount;
    }
    
    /**
     * 获取缓存统计信息
     */
    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("statsCache", Map.of(
            "size", statsCache.size(),
            "expired", countExpiredEntries(statsCache)
        ));
        
        stats.put("chartCache", Map.of(
            "size", chartCache.size(),
            "expired", countExpiredEntries(chartCache)
        ));
        
        stats.put("deviceCache", Map.of(
            "size", deviceCache.size(),
            "expired", countExpiredEntries(deviceCache)
        ));
        
        stats.put("todayStatsCache", Map.of(
            "size", todayStatsCache.size(),
            "expired", countExpiredEntries(todayStatsCache)
        ));
        
        return stats;
    }
    
    /**
     * 统计过期条目数量
     */
    private <T> long countExpiredEntries(Map<String, CacheEntry<T>> cache) {
        return cache.values().stream()
                   .mapToLong(entry -> entry.isExpired() ? 1 : 0)
                   .sum();
    }
}