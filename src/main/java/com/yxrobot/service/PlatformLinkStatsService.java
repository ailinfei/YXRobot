package com.yxrobot.service;

import com.yxrobot.dto.PlatformLinkStatsDTO;
import com.yxrobot.mapper.PlatformLinkMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 平台链接统计数据服务类
 * 负责处理平台链接统计相关的业务逻辑
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-22
 */
@Service
public class PlatformLinkStatsService {
    
    private static final Logger logger = LoggerFactory.getLogger(PlatformLinkStatsService.class);
    
    @Autowired
    private PlatformLinkMapper platformLinkMapper;
    
    /**
     * 获取平台链接统计数据
     * 包含基础统计、表现最佳链接、地区统计、语言统计等
     * 
     * @return 统计数据DTO
     */
    public PlatformLinkStatsDTO getPlatformLinkStats() {
        logger.info("开始获取平台链接统计数据");
        
        PlatformLinkStatsDTO statsDTO = new PlatformLinkStatsDTO();
        
        try {
            // 获取基础统计数据
            Map<String, Object> basicStats = platformLinkMapper.selectStats();
            if (basicStats != null) {
                statsDTO.setTotalLinks(getIntValue(basicStats, "totalLinks"));
                statsDTO.setActiveLinks(getIntValue(basicStats, "activeLinks"));
                statsDTO.setInactiveLinks(getIntValue(basicStats, "inactiveLinks"));
                statsDTO.setTotalClicks(getLongValue(basicStats, "totalClicks"));
                statsDTO.setTotalConversions(getLongValue(basicStats, "totalConversions"));
                statsDTO.setConversionRate(getDoubleValue(basicStats, "conversionRate"));
            }
            
            // 获取表现最佳的链接
            List<Map<String, Object>> topLinks = platformLinkMapper.selectTopPerformingLinks(10);
            List<PlatformLinkStatsDTO.TopPerformingLinkDTO> topPerformingLinks = topLinks.stream()
                    .map(this::mapToTopPerformingLink)
                    .collect(Collectors.toList());
            statsDTO.setTopPerformingLinks(topPerformingLinks);
            
            // 获取地区统计数据
            List<Map<String, Object>> regionData = platformLinkMapper.selectRegionStats();
            List<PlatformLinkStatsDTO.RegionStatsDTO> regionStats = regionData.stream()
                    .map(this::mapToRegionStats)
                    .collect(Collectors.toList());
            statsDTO.setRegionStats(regionStats);
            
            // 获取语言统计数据
            List<Map<String, Object>> languageData = platformLinkMapper.selectLanguageStats();
            List<PlatformLinkStatsDTO.LanguageStatsDTO> languageStats = languageData.stream()
                    .map(this::mapToLanguageStats)
                    .collect(Collectors.toList());
            statsDTO.setLanguageStats(languageStats);
            
            logger.info("获取平台链接统计数据完成 - 总链接: {}, 活跃: {}, 总点击: {}", 
                       statsDTO.getTotalLinks(), statsDTO.getActiveLinks(), statsDTO.getTotalClicks());
            
        } catch (Exception e) {
            logger.error("获取平台链接统计数据失败", e);
            throw new RuntimeException("获取统计数据失败: " + e.getMessage());
        }
        
        return statsDTO;
    }
    
    /**
     * 获取地区统计数据
     * 
     * @return 地区统计数据列表
     */
    public List<PlatformLinkStatsDTO.RegionStatsDTO> getRegionStats() {
        logger.info("开始获取地区统计数据");
        
        try {
            List<Map<String, Object>> regionData = platformLinkMapper.selectRegionStats();
            List<PlatformLinkStatsDTO.RegionStatsDTO> regionStats = regionData.stream()
                    .map(this::mapToRegionStats)
                    .collect(Collectors.toList());
            
            logger.info("获取地区统计数据完成 - 地区数量: {}", regionStats.size());
            return regionStats;
            
        } catch (Exception e) {
            logger.error("获取地区统计数据失败", e);
            throw new RuntimeException("获取地区统计数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取语言统计数据
     * 
     * @return 语言统计数据列表
     */
    public List<PlatformLinkStatsDTO.LanguageStatsDTO> getLanguageStats() {
        logger.info("开始获取语言统计数据");
        
        try {
            List<Map<String, Object>> languageData = platformLinkMapper.selectLanguageStats();
            List<PlatformLinkStatsDTO.LanguageStatsDTO> languageStats = languageData.stream()
                    .map(this::mapToLanguageStats)
                    .collect(Collectors.toList());
            
            logger.info("获取语言统计数据完成 - 语言数量: {}", languageStats.size());
            return languageStats;
            
        } catch (Exception e) {
            logger.error("获取语言统计数据失败", e);
            throw new RuntimeException("获取语言统计数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取表现最佳的链接
     * 
     * @param limit 限制数量
     * @return 表现最佳的链接列表
     */
    public List<PlatformLinkStatsDTO.TopPerformingLinkDTO> getTopPerformingLinks(Integer limit) {
        logger.info("开始获取表现最佳的链接 - 限制: {}", limit);
        
        if (limit == null || limit <= 0) {
            limit = 10; // 默认10个
        }
        
        try {
            List<Map<String, Object>> topLinks = platformLinkMapper.selectTopPerformingLinks(limit);
            List<PlatformLinkStatsDTO.TopPerformingLinkDTO> result = topLinks.stream()
                    .map(this::mapToTopPerformingLink)
                    .collect(Collectors.toList());
            
            logger.info("获取表现最佳的链接完成 - 数量: {}", result.size());
            return result;
            
        } catch (Exception e) {
            logger.error("获取表现最佳的链接失败", e);
            throw new RuntimeException("获取表现最佳的链接失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取基础统计数据
     * 
     * @return 基础统计数据Map
     */
    public Map<String, Object> getBasicStats() {
        logger.info("开始获取基础统计数据");
        
        try {
            Map<String, Object> basicStats = platformLinkMapper.selectStats();
            
            logger.info("获取基础统计数据完成 - 总链接: {}, 活跃: {}", 
                       basicStats.get("totalLinks"), basicStats.get("activeLinks"));
            return basicStats;
            
        } catch (Exception e) {
            logger.error("获取基础统计数据失败", e);
            throw new RuntimeException("获取基础统计数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取图表数据
     * 用于前端图表展示
     * 
     * @return 图表数据Map
     */
    public Map<String, Object> getChartData() {
        logger.info("开始获取图表数据");
        
        try {
            Map<String, Object> chartData = new java.util.HashMap<>();
            
            // 地区分布饼图数据
            List<Map<String, Object>> regionData = platformLinkMapper.selectRegionStats();
            List<Map<String, Object>> regionChartData = regionData.stream()
                    .map(data -> {
                        Map<String, Object> item = new java.util.HashMap<>();
                        item.put("name", data.get("region"));
                        item.put("value", data.get("linkCount"));
                        return item;
                    })
                    .collect(Collectors.toList());
            chartData.put("regionDistribution", regionChartData);
            
            // 语言分布饼图数据
            List<Map<String, Object>> languageData = platformLinkMapper.selectLanguageStats();
            List<Map<String, Object>> languageChartData = languageData.stream()
                    .map(data -> {
                        Map<String, Object> item = new java.util.HashMap<>();
                        item.put("name", data.get("languageName"));
                        item.put("value", data.get("linkCount"));
                        return item;
                    })
                    .collect(Collectors.toList());
            chartData.put("languageDistribution", languageChartData);
            
            // 点击量排行榜数据
            List<Map<String, Object>> topLinks = platformLinkMapper.selectTopPerformingLinks(5);
            List<Map<String, Object>> clickRankingData = topLinks.stream()
                    .map(data -> {
                        Map<String, Object> item = new java.util.HashMap<>();
                        item.put("name", data.get("platformName"));
                        item.put("value", data.get("clickCount"));
                        return item;
                    })
                    .collect(Collectors.toList());
            chartData.put("clickRanking", clickRankingData);
            
            // 转化率排行榜数据
            List<Map<String, Object>> conversionRankingData = topLinks.stream()
                    .map(data -> {
                        Map<String, Object> item = new java.util.HashMap<>();
                        item.put("name", data.get("platformName"));
                        item.put("value", data.get("conversionRate"));
                        return item;
                    })
                    .collect(Collectors.toList());
            chartData.put("conversionRanking", conversionRankingData);
            
            logger.info("获取图表数据完成 - 地区: {}, 语言: {}, 排行: {}", 
                       regionChartData.size(), languageChartData.size(), clickRankingData.size());
            
            return chartData;
            
        } catch (Exception e) {
            logger.error("获取图表数据失败", e);
            throw new RuntimeException("获取图表数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 刷新统计数据
     * 重新计算和加载统计数据
     */
    public void refreshStats() {
        logger.info("刷新平台链接统计数据");
        
        // 可以在这里实现数据刷新逻辑，如重新计算统计指标
        
        logger.info("平台链接统计数据已刷新");
    }
    
    /**
     * 将Map数据转换为TopPerformingLinkDTO
     */
    private PlatformLinkStatsDTO.TopPerformingLinkDTO mapToTopPerformingLink(Map<String, Object> data) {
        PlatformLinkStatsDTO.TopPerformingLinkDTO dto = new PlatformLinkStatsDTO.TopPerformingLinkDTO();
        dto.setId(getLongValue(data, "id"));
        dto.setPlatformName(getStringValue(data, "platformName"));
        dto.setRegion(getStringValue(data, "region"));
        dto.setClickCount(getIntValue(data, "clickCount"));
        dto.setConversionCount(getIntValue(data, "conversionCount"));
        dto.setConversionRate(getDoubleValue(data, "conversionRate"));
        return dto;
    }
    
    /**
     * 将Map数据转换为RegionStatsDTO
     */
    private PlatformLinkStatsDTO.RegionStatsDTO mapToRegionStats(Map<String, Object> data) {
        PlatformLinkStatsDTO.RegionStatsDTO dto = new PlatformLinkStatsDTO.RegionStatsDTO();
        dto.setRegion(getStringValue(data, "region"));
        dto.setLinkCount(getIntValue(data, "linkCount"));
        dto.setClickCount(getLongValue(data, "clickCount"));
        dto.setConversionCount(getLongValue(data, "conversionCount"));
        return dto;
    }
    
    /**
     * 将Map数据转换为LanguageStatsDTO
     */
    private PlatformLinkStatsDTO.LanguageStatsDTO mapToLanguageStats(Map<String, Object> data) {
        PlatformLinkStatsDTO.LanguageStatsDTO dto = new PlatformLinkStatsDTO.LanguageStatsDTO();
        dto.setLanguageCode(getStringValue(data, "languageCode"));
        dto.setLanguageName(getStringValue(data, "languageName"));
        dto.setLinkCount(getIntValue(data, "linkCount"));
        dto.setClickCount(getLongValue(data, "clickCount"));
        dto.setConversionCount(getLongValue(data, "conversionCount"));
        return dto;
    }
    
    /**
     * 安全获取String值
     */
    private String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : "";
    }
    
    /**
     * 安全获取Integer值
     */
    private Integer getIntValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return 0;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    /**
     * 安全获取Long值
     */
    private Long getLongValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return 0L;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
    
    /**
     * 安全获取Double值
     */
    private Double getDoubleValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return 0.0;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}