package com.yxrobot.controller;

import com.yxrobot.dto.PlatformLinkStatsDTO;
import com.yxrobot.dto.RegionConfigDTO;
import com.yxrobot.service.PlatformLinkStatsService;
import com.yxrobot.service.RegionConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 平台链接统计数据控制器
 * 处理统计数据和区域配置相关的HTTP请求
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-22
 */
@RestController
@RequestMapping("/api/platform-stats")
@Validated

public class PlatformLinkStatsController {
    
    private static final Logger logger = LoggerFactory.getLogger(PlatformLinkStatsController.class);
    
    @Autowired
    private PlatformLinkStatsService statsService;
    
    @Autowired
    private RegionConfigService regionConfigService;
    
    /**
     * 获取平台链接统计数据
     * 包含基础统计、表现最佳链接、地区统计、语言统计等
     * 
     * @return 统计数据
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getPlatformLinkStats() {
        logger.info("获取平台链接统计数据");
        
        try {
            PlatformLinkStatsDTO stats = statsService.getPlatformLinkStats();
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", stats);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取平台链接统计数据失败", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取基础统计数据
     * 
     * @return 基础统计数据
     */
    @GetMapping("/stats/basic")
    public ResponseEntity<Map<String, Object>> getBasicStats() {
        logger.info("获取基础统计数据");
        
        try {
            Map<String, Object> basicStats = statsService.getBasicStats();
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", basicStats);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取基础统计数据失败", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取地区统计数据
     * 
     * @return 地区统计数据
     */
    @GetMapping("/stats/regions")
    public ResponseEntity<Map<String, Object>> getRegionStats() {
        logger.info("获取地区统计数据");
        
        try {
            List<PlatformLinkStatsDTO.RegionStatsDTO> regionStats = statsService.getRegionStats();
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", regionStats);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取地区统计数据失败", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取语言统计数据
     * 
     * @return 语言统计数据
     */
    @GetMapping("/stats/languages")
    public ResponseEntity<Map<String, Object>> getLanguageStats() {
        logger.info("获取语言统计数据");
        
        try {
            List<PlatformLinkStatsDTO.LanguageStatsDTO> languageStats = statsService.getLanguageStats();
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", languageStats);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取语言统计数据失败", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取表现最佳的链接
     * 
     * @param limit 限制数量
     * @return 表现最佳的链接列表
     */
    @GetMapping("/stats/top-performing")
    public ResponseEntity<Map<String, Object>> getTopPerformingLinks(
            @RequestParam(defaultValue = "10") Integer limit) {
        
        logger.info("获取表现最佳的链接 - 限制: {}", limit);
        
        try {
            List<PlatformLinkStatsDTO.TopPerformingLinkDTO> topLinks = statsService.getTopPerformingLinks(limit);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", topLinks);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取表现最佳的链接失败", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取图表数据
     * 用于前端图表展示
     * 
     * @return 图表数据
     */
    @GetMapping("/stats/charts")
    public ResponseEntity<Map<String, Object>> getChartData() {
        logger.info("获取图表数据");
        
        try {
            Map<String, Object> chartData = statsService.getChartData();
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", chartData);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取图表数据失败", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取区域配置列表
     * 
     * @return 区域配置列表
     */
    @GetMapping("/regions")
    public ResponseEntity<Map<String, Object>> getRegionConfigs() {
        logger.info("获取区域配置列表");
        
        try {
            List<RegionConfigDTO> regionConfigs = regionConfigService.getRegionConfigs();
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", regionConfigs);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取区域配置列表失败", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 根据地区获取支持的语言
     * 
     * @param region 地区名称
     * @return 语言列表
     */
    @GetMapping("/regions/{region}/languages")
    public ResponseEntity<Map<String, Object>> getLanguagesByRegion(@PathVariable String region) {
        logger.info("根据地区获取支持的语言 - 地区: {}", region);
        
        try {
            List<RegionConfigDTO.LanguageDTO> languages = regionConfigService.getLanguagesByRegion(region);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", languages);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("根据地区获取支持的语言失败 - 地区: {}", region, e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取所有不重复的地区列表
     * 
     * @return 地区列表
     */
    @GetMapping("/regions/distinct")
    public ResponseEntity<Map<String, Object>> getDistinctRegions() {
        logger.info("获取所有不重复的地区列表");
        
        try {
            List<String> regions = regionConfigService.getDistinctRegions();
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", regions);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取地区列表失败", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取所有不重复的国家列表
     * 
     * @return 国家列表
     */
    @GetMapping("/countries/distinct")
    public ResponseEntity<Map<String, Object>> getDistinctCountries() {
        logger.info("获取所有不重复的国家列表");
        
        try {
            List<String> countries = regionConfigService.getDistinctCountries();
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", countries);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取国家列表失败", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取所有不重复的语言列表
     * 
     * @return 语言列表
     */
    @GetMapping("/languages/distinct")
    public ResponseEntity<Map<String, Object>> getDistinctLanguages() {
        logger.info("获取所有不重复的语言列表");
        
        try {
            List<Map<String, String>> languages = regionConfigService.getDistinctLanguages();
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", languages);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取语言列表失败", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 验证地区和语言的匹配关系
     * 
     * @param region 地区名称
     * @param languageCode 语言代码
     * @return 验证结果
     */
    @GetMapping("/validate-region-language")
    public ResponseEntity<Map<String, Object>> validateRegionLanguage(
            @RequestParam String region,
            @RequestParam String languageCode) {
        
        logger.info("验证地区和语言匹配关系 - 地区: {}, 语言: {}", region, languageCode);
        
        try {
            boolean isValid = regionConfigService.validateRegionLanguage(region, languageCode);
            
            Map<String, Object> result = new HashMap<>();
            result.put("region", region);
            result.put("languageCode", languageCode);
            result.put("isValid", isValid);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "验证完成");
            response.put("data", result);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("验证地区和语言匹配关系失败", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "验证失败: " + e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 刷新统计数据
     * 重新计算和验证统计数据
     * 
     * @return 刷新结果
     */
    @PostMapping("/stats/refresh")
    public ResponseEntity<Map<String, Object>> refreshStats() {
        logger.info("刷新统计数据");
        
        try {
            // 调用服务层的刷新方法
            statsService.refreshStats();
            regionConfigService.refreshData();
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "统计数据刷新成功");
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("刷新统计数据失败", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "统计数据刷新失败: " + e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    

}