package com.yxrobot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 设备搜索配置管理服务
 * 管理搜索相关的配置参数和策略
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-28
 */
@Service
public class ManagedDeviceSearchConfigService {
    
    private static final Logger logger = LoggerFactory.getLogger(ManagedDeviceSearchConfigService.class);
    
    // 搜索配置参数
    private final Map<String, Object> searchConfig = new HashMap<>();
    
    // 默认配置
    private static final Map<String, Object> DEFAULT_CONFIG = new HashMap<>();
    
    static {
        // 分页配置
        DEFAULT_CONFIG.put("defaultPageSize", 10);
        DEFAULT_CONFIG.put("maxPageSize", 1000);
        DEFAULT_CONFIG.put("minPageSize", 1);
        
        // 搜索配置
        DEFAULT_CONFIG.put("maxKeywordLength", 100);
        DEFAULT_CONFIG.put("minKeywordLength", 1);
        DEFAULT_CONFIG.put("enableFuzzySearch", true);
        DEFAULT_CONFIG.put("enableFullTextSearch", true);
        DEFAULT_CONFIG.put("searchTimeout", 30000); // 30秒
        
        // 性能配置
        DEFAULT_CONFIG.put("slowQueryThreshold", 1000); // 1秒
        DEFAULT_CONFIG.put("warningQueryThreshold", 500); // 0.5秒
        DEFAULT_CONFIG.put("maxConcurrentSearches", 100);
        
        // 缓存配置（虽然项目不使用缓存，但保留配置项）
        DEFAULT_CONFIG.put("enableSearchCache", false);
        DEFAULT_CONFIG.put("searchCacheTtl", 300); // 5分钟
        DEFAULT_CONFIG.put("maxCacheSize", 1000);
        
        // 索引配置
        DEFAULT_CONFIG.put("enableAutoIndexOptimization", true);
        DEFAULT_CONFIG.put("indexOptimizationInterval", 3600000); // 1小时
        
        // 搜索建议配置
        DEFAULT_CONFIG.put("maxSuggestions", 20);
        DEFAULT_CONFIG.put("minSuggestionLength", 2);
        DEFAULT_CONFIG.put("enableSearchSuggestions", true);
        
        // 热门关键词配置
        DEFAULT_CONFIG.put("maxPopularKeywords", 50);
        DEFAULT_CONFIG.put("popularKeywordThreshold", 5);
        DEFAULT_CONFIG.put("keywordStatsPeriod", 86400000); // 24小时
    }
    
    /**
     * 初始化搜索配置
     */
    public void initializeSearchConfig() {
        logger.info("初始化设备搜索配置");
        
        // 加载默认配置
        searchConfig.putAll(DEFAULT_CONFIG);
        
        // 这里可以从数据库或配置文件加载自定义配置
        loadCustomConfig();
        
        logger.info("设备搜索配置初始化完成，配置项数量: {}", searchConfig.size());
    }
    
    /**
     * 获取搜索配置
     * 
     * @param key 配置键
     * @return 配置值
     */
    public Object getSearchConfig(String key) {
        return searchConfig.getOrDefault(key, DEFAULT_CONFIG.get(key));
    }
    
    /**
     * 获取搜索配置（指定类型）
     * 
     * @param key 配置键
     * @param type 值类型
     * @param <T> 泛型类型
     * @return 配置值
     */
    @SuppressWarnings("unchecked")
    public <T> T getSearchConfig(String key, Class<T> type) {
        Object value = getSearchConfig(key);
        if (value != null && type.isAssignableFrom(value.getClass())) {
            return (T) value;
        }
        return null;
    }
    
    /**
     * 设置搜索配置
     * 
     * @param key 配置键
     * @param value 配置值
     */
    public void setSearchConfig(String key, Object value) {
        logger.info("更新搜索配置: {} = {}", key, value);
        searchConfig.put(key, value);
    }
    
    /**
     * 批量设置搜索配置
     * 
     * @param config 配置映射
     */
    public void setSearchConfig(Map<String, Object> config) {
        logger.info("批量更新搜索配置，配置项数量: {}", config.size());
        searchConfig.putAll(config);
    }
    
    /**
     * 获取所有搜索配置
     * 
     * @return 配置映射
     */
    public Map<String, Object> getAllSearchConfig() {
        return new HashMap<>(searchConfig);
    }
    
    /**
     * 重置搜索配置为默认值
     */
    public void resetSearchConfig() {
        logger.info("重置搜索配置为默认值");
        searchConfig.clear();
        searchConfig.putAll(DEFAULT_CONFIG);
    }
    
    /**
     * 验证搜索配置
     * 
     * @return 验证结果
     */
    public Map<String, Object> validateSearchConfig() {
        Map<String, Object> validationResult = new HashMap<>();
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        // 验证分页配置
        Integer defaultPageSize = getSearchConfig("defaultPageSize", Integer.class);
        Integer maxPageSize = getSearchConfig("maxPageSize", Integer.class);
        Integer minPageSize = getSearchConfig("minPageSize", Integer.class);
        
        if (defaultPageSize == null || defaultPageSize < 1) {
            errors.add("默认页大小必须大于0");
        }
        if (maxPageSize == null || maxPageSize < 1) {
            errors.add("最大页大小必须大于0");
        }
        if (minPageSize == null || minPageSize < 1) {
            errors.add("最小页大小必须大于0");
        }
        if (defaultPageSize != null && maxPageSize != null && defaultPageSize > maxPageSize) {
            errors.add("默认页大小不能大于最大页大小");
        }
        if (defaultPageSize != null && minPageSize != null && defaultPageSize < minPageSize) {
            errors.add("默认页大小不能小于最小页大小");
        }
        
        // 验证搜索配置
        Integer maxKeywordLength = getSearchConfig("maxKeywordLength", Integer.class);
        Integer minKeywordLength = getSearchConfig("minKeywordLength", Integer.class);
        
        if (maxKeywordLength == null || maxKeywordLength < 1) {
            errors.add("最大关键词长度必须大于0");
        }
        if (minKeywordLength == null || minKeywordLength < 1) {
            errors.add("最小关键词长度必须大于0");
        }
        if (maxKeywordLength != null && minKeywordLength != null && maxKeywordLength < minKeywordLength) {
            errors.add("最大关键词长度不能小于最小关键词长度");
        }
        
        // 验证性能配置
        Integer slowQueryThreshold = getSearchConfig("slowQueryThreshold", Integer.class);
        Integer warningQueryThreshold = getSearchConfig("warningQueryThreshold", Integer.class);
        
        if (slowQueryThreshold == null || slowQueryThreshold < 100) {
            warnings.add("慢查询阈值建议不小于100ms");
        }
        if (warningQueryThreshold == null || warningQueryThreshold < 50) {
            warnings.add("警告查询阈值建议不小于50ms");
        }
        if (slowQueryThreshold != null && warningQueryThreshold != null && 
            slowQueryThreshold <= warningQueryThreshold) {
            warnings.add("慢查询阈值应该大于警告查询阈值");
        }
        
        validationResult.put("errors", errors);
        validationResult.put("warnings", warnings);
        validationResult.put("isValid", errors.isEmpty());
        
        return validationResult;
    }
    
    /**
     * 获取搜索策略配置
     * 
     * @return 搜索策略配置
     */
    public Map<String, Object> getSearchStrategyConfig() {
        Map<String, Object> strategyConfig = new HashMap<>();
        
        // 搜索优先级策略
        List<String> searchPriority = Arrays.asList(
            "exactMatch",      // 精确匹配
            "prefixMatch",     // 前缀匹配
            "fuzzyMatch",      // 模糊匹配
            "fullTextSearch"   // 全文搜索
        );
        strategyConfig.put("searchPriority", searchPriority);
        
        // 字段权重配置
        Map<String, Double> fieldWeights = new HashMap<>();
        fieldWeights.put("serialNumber", 1.0);    // 序列号权重最高
        fieldWeights.put("customerName", 0.8);    // 客户姓名权重较高
        fieldWeights.put("model", 0.6);           // 型号权重中等
        fieldWeights.put("notes", 0.4);           // 备注权重较低
        strategyConfig.put("fieldWeights", fieldWeights);
        
        // 排序策略
        Map<String, String> sortStrategies = new HashMap<>();
        sortStrategies.put("relevance", "按相关性排序");
        sortStrategies.put("createdAt", "按创建时间排序");
        sortStrategies.put("updatedAt", "按更新时间排序");
        sortStrategies.put("lastOnlineAt", "按最后在线时间排序");
        sortStrategies.put("serialNumber", "按序列号排序");
        sortStrategies.put("customerName", "按客户姓名排序");
        strategyConfig.put("sortStrategies", sortStrategies);
        
        return strategyConfig;
    }
    
    /**
     * 获取搜索过滤器配置
     * 
     * @return 过滤器配置
     */
    public Map<String, Object> getSearchFilterConfig() {
        Map<String, Object> filterConfig = new HashMap<>();
        
        // 可用的过滤器
        List<Map<String, Object>> availableFilters = new ArrayList<>();
        
        // 状态过滤器
        Map<String, Object> statusFilter = new HashMap<>();
        statusFilter.put("key", "status");
        statusFilter.put("name", "设备状态");
        statusFilter.put("type", "multiSelect");
        statusFilter.put("options", Arrays.asList("online", "offline", "fault", "maintenance", "pending"));
        availableFilters.add(statusFilter);
        
        // 型号过滤器
        Map<String, Object> modelFilter = new HashMap<>();
        modelFilter.put("key", "model");
        modelFilter.put("name", "设备型号");
        modelFilter.put("type", "multiSelect");
        modelFilter.put("options", Arrays.asList("education", "home", "professional"));
        availableFilters.add(modelFilter);
        
        // 时间范围过滤器
        Map<String, Object> dateFilter = new HashMap<>();
        dateFilter.put("key", "dateRange");
        dateFilter.put("name", "时间范围");
        dateFilter.put("type", "dateRange");
        dateFilter.put("fields", Arrays.asList("created", "updated", "activated", "lastOnline"));
        availableFilters.add(dateFilter);
        
        // 客户过滤器
        Map<String, Object> customerFilter = new HashMap<>();
        customerFilter.put("key", "customer");
        customerFilter.put("name", "客户");
        customerFilter.put("type", "select");
        customerFilter.put("searchable", true);
        availableFilters.add(customerFilter);
        
        filterConfig.put("availableFilters", availableFilters);
        
        // 默认启用的过滤器
        filterConfig.put("defaultEnabledFilters", Arrays.asList("status", "model", "dateRange"));
        
        return filterConfig;
    }
    
    /**
     * 加载自定义配置
     */
    private void loadCustomConfig() {
        // 这里可以从数据库或配置文件加载自定义配置
        // 暂时使用硬编码的配置
        
        // 根据项目需求调整默认配置
        searchConfig.put("defaultPageSize", 20); // 调整默认页大小
        searchConfig.put("maxSuggestions", 15);  // 调整建议数量
        
        logger.debug("自定义搜索配置加载完成");
    }
    
    /**
     * 导出搜索配置
     * 
     * @return 配置导出结果
     */
    public Map<String, Object> exportSearchConfig() {
        Map<String, Object> exportData = new HashMap<>();
        
        exportData.put("timestamp", new Date());
        exportData.put("version", "1.0.0");
        exportData.put("config", getAllSearchConfig());
        exportData.put("strategyConfig", getSearchStrategyConfig());
        exportData.put("filterConfig", getSearchFilterConfig());
        
        logger.info("搜索配置导出完成");
        return exportData;
    }
    
    /**
     * 导入搜索配置
     * 
     * @param configData 配置数据
     * @return 导入结果
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> importSearchConfig(Map<String, Object> configData) {
        Map<String, Object> importResult = new HashMap<>();
        
        try {
            if (configData.containsKey("config")) {
                Map<String, Object> config = (Map<String, Object>) configData.get("config");
                setSearchConfig(config);
                
                importResult.put("success", true);
                importResult.put("importedCount", config.size());
                importResult.put("message", "搜索配置导入成功");
                
                logger.info("搜索配置导入成功，配置项数量: {}", config.size());
            } else {
                importResult.put("success", false);
                importResult.put("message", "配置数据格式错误");
            }
            
        } catch (Exception e) {
            logger.error("搜索配置导入失败", e);
            importResult.put("success", false);
            importResult.put("message", "导入失败: " + e.getMessage());
        }
        
        return importResult;
    }
}