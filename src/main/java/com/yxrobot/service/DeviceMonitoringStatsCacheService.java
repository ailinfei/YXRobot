package com.yxrobot.service;

import com.yxrobot.dto.DeviceMonitoringStatsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 设备监控统计缓存服务
 * 提供统计数据的内存缓存功能，提高查询性能
 * 
 * 注意：根据项目要求，本项目不使用Redis等外部缓存，
 * 这里使用简单的内存缓存实现
 * 
 * @author YXRobot Development Team
 * @since 2024
 */
@Service
public class DeviceMonitoringStatsCacheService {
    
    private static final Logger logger = LoggerFactory.getLogger(DeviceMonitoringStatsCacheService.class);
    
    // 缓存过期时间（分钟）
    private static final int CACHE_EXPIRE_MINUTES = 5;
    
    // 缓存数据
    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();
    
    // 读写锁
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    
    @Autowired
    private DeviceMonitoringStatsService deviceMonitoringStatsService;
    
    /**
     * 获取实时统计数据（带缓存）
     * 
     * @return 统计数据
     */
    public DeviceMonitoringStatsDTO getCachedMonitoringStats() {
        String cacheKey = "monitoring_stats";
        
        lock.readLock().lock();
        try {
            CacheEntry entry = cache.get(cacheKey);
            
            // 检查缓存是否有效
            if (entry != null && !entry.isExpired()) {
                logger.debug("从缓存获取监控统计数据");
                return entry.getData();
            }
        } finally {
            lock.readLock().unlock();
        }
        
        // 缓存无效，重新获取数据
        lock.writeLock().lock();
        try {
            // 双重检查，避免重复查询
            CacheEntry entry = cache.get(cacheKey);
            if (entry != null && !entry.isExpired()) {
                return entry.getData();
            }
            
            logger.debug("缓存过期，重新获取监控统计数据");
            DeviceMonitoringStatsDTO stats = deviceMonitoringStatsService.getMonitoringStats();
            
            // 更新缓存
            cache.put(cacheKey, new CacheEntry(stats, LocalDateTime.now().plusMinutes(CACHE_EXPIRE_MINUTES)));
            
            return stats;
            
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * 清除缓存
     */
    public void clearCache() {
        lock.writeLock().lock();
        try {
            cache.clear();
            logger.info("监控统计缓存已清除");
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * 清除过期缓存
     */
    public void clearExpiredCache() {
        lock.writeLock().lock();
        try {
            cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
            logger.debug("过期缓存已清除");
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * 获取缓存大小
     * 
     * @return 缓存条目数量
     */
    public int getCacheSize() {
        lock.readLock().lock();
        try {
            return cache.size();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * 预热缓存
     * 在应用启动时调用，提前加载常用数据
     */
    public void warmUpCache() {
        logger.info("开始预热监控统计缓存");
        
        try {
            getCachedMonitoringStats();
            logger.info("监控统计缓存预热完成");
        } catch (Exception e) {
            logger.error("监控统计缓存预热失败", e);
        }
    }
    
    /**
     * 缓存条目内部类
     */
    private static class CacheEntry {
        private final DeviceMonitoringStatsDTO data;
        private final LocalDateTime expireTime;
        
        public CacheEntry(DeviceMonitoringStatsDTO data, LocalDateTime expireTime) {
            this.data = data;
            this.expireTime = expireTime;
        }
        
        public DeviceMonitoringStatsDTO getData() {
            return data;
        }
        
        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expireTime);
        }
    }
}