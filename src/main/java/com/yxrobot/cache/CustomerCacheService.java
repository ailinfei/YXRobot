package com.yxrobot.cache;

import com.yxrobot.dto.CustomerStatsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 客户管理模块缓存服务
 * 提供数据缓存机制，提高统计和查询数据性能
 */
@Service
public class CustomerCacheService {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerCacheService.class);
    
    // 缓存配置
    private static final long STATS_CACHE_DURATION_MINUTES = 5;  // 统计数据缓存5分钟
    private static final long FILTER_OPTIONS_CACHE_DURATION_MINUTES = 30;  // 筛选选项缓存30分钟
    private static final long CUSTOMER_DETAIL_CACHE_DURATION_MINUTES = 10;  // 客户详情缓存10分钟
    
    // 内存缓存存储
    private final Map<String, CacheItem<CustomerStatsDTO>> statsCache = new ConcurrentHashMap<>();
    private final Map<String, CacheItem<Map<String, Object>>> filterOptionsCache = new ConcurrentHashMap<>();
    private final Map<String, CacheItem<Object>> customerDetailCache = new ConcurrentHashMap<>();
    
    /**
     * 缓存项包装类
     */
    private static class CacheItem<T> {
        private final T data;
        private final LocalDateTime timestamp;
        private final long durationMinutes;
        
        public CacheItem(T data, long durationMinutes) {
            this.data = data;
            this.timestamp = LocalDateTime.now();
            this.durationMinutes = durationMinutes;
        }
        
        public T getData() {
            return data;
        }
        
        public boolean isExpired() {
            return ChronoUnit.MINUTES.between(timestamp, LocalDateTime.now()) > durationMinutes;
        }
        
        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }
    
    // ==================== 客户统计数据缓存 ====================
    
    /**
     * 获取缓存的客户统计数据
     */
    public CustomerStatsDTO getCachedCustomerStats() {
        String key = "customer_stats";
        CacheItem<CustomerStatsDTO> cacheItem = statsCache.get(key);
        
        if (cacheItem != null && !cacheItem.isExpired()) {
            logger.debug("Cache hit for customer stats");
            return cacheItem.getData();
        }
        
        logger.debug("Cache miss for customer stats");
        return null;
    }
    
    /**
     * 缓存客户统计数据
     */
    public void cacheCustomerStats(CustomerStatsDTO stats) {
        String key = "customer_stats";
        statsCache.put(key, new CacheItem<>(stats, STATS_CACHE_DURATION_MINUTES));
        logger.debug("Cached customer stats for {} minutes", STATS_CACHE_DURATION_MINUTES);
    }
    
    /**
     * 清除客户统计数据缓存
     */
    public void clearCustomerStatsCache() {
        statsCache.clear();
        logger.debug("Cleared customer stats cache");
    }
    
    // ==================== 筛选选项缓存 ====================
    
    /**
     * 获取缓存的筛选选项
     */
    public Map<String, Object> getCachedFilterOptions() {
        String key = "filter_options";
        CacheItem<Map<String, Object>> cacheItem = filterOptionsCache.get(key);
        
        if (cacheItem != null && !cacheItem.isExpired()) {
            logger.debug("Cache hit for filter options");
            return cacheItem.getData();
        }
        
        logger.debug("Cache miss for filter options");
        return null;
    }
    
    /**
     * 缓存筛选选项
     */
    public void cacheFilterOptions(Map<String, Object> options) {
        String key = "filter_options";
        filterOptionsCache.put(key, new CacheItem<>(options, FILTER_OPTIONS_CACHE_DURATION_MINUTES));
        logger.debug("Cached filter options for {} minutes", FILTER_OPTIONS_CACHE_DURATION_MINUTES);
    }
    
    /**
     * 清除筛选选项缓存
     */
    public void clearFilterOptionsCache() {
        filterOptionsCache.clear();
        logger.debug("Cleared filter options cache");
    }
    
    // ==================== 客户详情缓存 ====================
    
    /**
     * 获取缓存的客户详情
     */
    public Object getCachedCustomerDetail(Long customerId) {
        String key = "customer_detail_" + customerId;
        CacheItem<Object> cacheItem = customerDetailCache.get(key);
        
        if (cacheItem != null && !cacheItem.isExpired()) {
            logger.debug("Cache hit for customer detail: {}", customerId);
            return cacheItem.getData();
        }
        
        logger.debug("Cache miss for customer detail: {}", customerId);
        return null;
    }
    
    /**
     * 缓存客户详情
     */
    public void cacheCustomerDetail(Long customerId, Object customerDetail) {
        String key = "customer_detail_" + customerId;
        customerDetailCache.put(key, new CacheItem<>(customerDetail, CUSTOMER_DETAIL_CACHE_DURATION_MINUTES));
        logger.debug("Cached customer detail for customer {} for {} minutes", 
                    customerId, CUSTOMER_DETAIL_CACHE_DURATION_MINUTES);
    }
    
    /**
     * 清除特定客户的详情缓存
     */
    public void clearCustomerDetailCache(Long customerId) {
        String key = "customer_detail_" + customerId;
        customerDetailCache.remove(key);
        logger.debug("Cleared customer detail cache for customer: {}", customerId);
    }
    
    /**
     * 清除所有客户详情缓存
     */
    public void clearAllCustomerDetailCache() {
        customerDetailCache.clear();
        logger.debug("Cleared all customer detail cache");
    }
    
    // ==================== 客户关联数据缓存 ====================
    
    /**
     * 获取缓存的客户设备列表
     */
    public List<Object> getCachedCustomerDevices(Long customerId) {
        String key = "customer_devices_" + customerId;
        CacheItem<Object> cacheItem = customerDetailCache.get(key);
        
        if (cacheItem != null && !cacheItem.isExpired()) {
            logger.debug("Cache hit for customer devices: {}", customerId);
            return (List<Object>) cacheItem.getData();
        }
        
        logger.debug("Cache miss for customer devices: {}", customerId);
        return null;
    }
    
    /**
     * 缓存客户设备列表
     */
    public void cacheCustomerDevices(Long customerId, List<Object> devices) {
        String key = "customer_devices_" + customerId;
        customerDetailCache.put(key, new CacheItem<>(devices, CUSTOMER_DETAIL_CACHE_DURATION_MINUTES));
        logger.debug("Cached customer devices for customer {} for {} minutes", 
                    customerId, CUSTOMER_DETAIL_CACHE_DURATION_MINUTES);
    }
    
    /**
     * 获取缓存的客户订单列表
     */
    public List<Object> getCachedCustomerOrders(Long customerId) {
        String key = "customer_orders_" + customerId;
        CacheItem<Object> cacheItem = customerDetailCache.get(key);
        
        if (cacheItem != null && !cacheItem.isExpired()) {
            logger.debug("Cache hit for customer orders: {}", customerId);
            return (List<Object>) cacheItem.getData();
        }
        
        logger.debug("Cache miss for customer orders: {}", customerId);
        return null;
    }
    
    /**
     * 缓存客户订单列表
     */
    public void cacheCustomerOrders(Long customerId, List<Object> orders) {
        String key = "customer_orders_" + customerId;
        customerDetailCache.put(key, new CacheItem<>(orders, CUSTOMER_DETAIL_CACHE_DURATION_MINUTES));
        logger.debug("Cached customer orders for customer {} for {} minutes", 
                    customerId, CUSTOMER_DETAIL_CACHE_DURATION_MINUTES);
    }
    
    /**
     * 获取缓存的客户服务记录列表
     */
    public List<Object> getCachedCustomerServiceRecords(Long customerId) {
        String key = "customer_service_records_" + customerId;
        CacheItem<Object> cacheItem = customerDetailCache.get(key);
        
        if (cacheItem != null && !cacheItem.isExpired()) {
            logger.debug("Cache hit for customer service records: {}", customerId);
            return (List<Object>) cacheItem.getData();
        }
        
        logger.debug("Cache miss for customer service records: {}", customerId);
        return null;
    }
    
    /**
     * 缓存客户服务记录列表
     */
    public void cacheCustomerServiceRecords(Long customerId, List<Object> serviceRecords) {
        String key = "customer_service_records_" + customerId;
        customerDetailCache.put(key, new CacheItem<>(serviceRecords, CUSTOMER_DETAIL_CACHE_DURATION_MINUTES));
        logger.debug("Cached customer service records for customer {} for {} minutes", 
                    customerId, CUSTOMER_DETAIL_CACHE_DURATION_MINUTES);
    }
    
    // ==================== 缓存管理 ====================
    
    /**
     * 清除所有缓存
     */
    public void clearAllCache() {
        statsCache.clear();
        filterOptionsCache.clear();
        customerDetailCache.clear();
        logger.info("Cleared all customer cache");
    }
    
    /**
     * 清除过期缓存
     */
    public void clearExpiredCache() {
        // 清除过期的统计数据缓存
        statsCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
        
        // 清除过期的筛选选项缓存
        filterOptionsCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
        
        // 清除过期的客户详情缓存
        customerDetailCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
        
        logger.debug("Cleared expired cache items");
    }
    
    /**
     * 获取缓存统计信息
     */
    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new ConcurrentHashMap<>();
        
        // 统计数据缓存
        long validStatsCache = statsCache.values().stream()
            .mapToLong(item -> item.isExpired() ? 0 : 1)
            .sum();
        stats.put("statsCache", Map.of(
            "total", statsCache.size(),
            "valid", validStatsCache,
            "expired", statsCache.size() - validStatsCache
        ));
        
        // 筛选选项缓存
        long validFilterCache = filterOptionsCache.values().stream()
            .mapToLong(item -> item.isExpired() ? 0 : 1)
            .sum();
        stats.put("filterOptionsCache", Map.of(
            "total", filterOptionsCache.size(),
            "valid", validFilterCache,
            "expired", filterOptionsCache.size() - validFilterCache
        ));
        
        // 客户详情缓存
        long validDetailCache = customerDetailCache.values().stream()
            .mapToLong(item -> item.isExpired() ? 0 : 1)
            .sum();
        stats.put("customerDetailCache", Map.of(
            "total", customerDetailCache.size(),
            "valid", validDetailCache,
            "expired", customerDetailCache.size() - validDetailCache
        ));
        
        return stats;
    }
    
    /**
     * 预热缓存
     * 在系统启动时或定期执行，预先加载常用数据到缓存
     */
    public void warmUpCache() {
        logger.info("Starting cache warm-up for customer module");
        
        // 这里可以预加载一些常用数据
        // 例如：客户统计数据、筛选选项等
        
        logger.info("Cache warm-up completed for customer module");
    }
    
    /**
     * 当客户数据发生变更时，清除相关缓存
     */
    public void invalidateCustomerCache(Long customerId) {
        // 清除客户详情缓存
        clearCustomerDetailCache(customerId);
        
        // 清除客户关联数据缓存
        String devicesKey = "customer_devices_" + customerId;
        String ordersKey = "customer_orders_" + customerId;
        String serviceRecordsKey = "customer_service_records_" + customerId;
        
        customerDetailCache.remove(devicesKey);
        customerDetailCache.remove(ordersKey);
        customerDetailCache.remove(serviceRecordsKey);
        
        // 清除统计数据缓存（因为客户数据变更可能影响统计）
        clearCustomerStatsCache();
        
        logger.debug("Invalidated cache for customer: {}", customerId);
    }
}