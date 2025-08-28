package com.yxrobot.service;

import com.yxrobot.dto.RegionConfigDTO;
import com.yxrobot.entity.RegionConfig;
import com.yxrobot.mapper.RegionConfigMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 区域配置管理服务类
 * 负责处理区域配置相关的业务逻辑
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-22
 */
@Service
@Transactional
public class RegionConfigService {
    
    private static final Logger logger = LoggerFactory.getLogger(RegionConfigService.class);
    
    @Autowired
    private RegionConfigMapper regionConfigMapper;
    
    /**
     * 获取所有激活的区域配置
     * 按地区分组，每个地区包含支持的语言列表
     * 
     * @return 区域配置DTO列表
     */
    public List<RegionConfigDTO> getRegionConfigs() {
        logger.info("获取所有区域配置");
        
        try {
            List<RegionConfig> configs = regionConfigMapper.selectAllActive();
            
            // 按地区分组
            Map<String, List<RegionConfig>> groupedConfigs = configs.stream()
                    .collect(Collectors.groupingBy(RegionConfig::getRegion));
            
            // 转换为DTO
            List<RegionConfigDTO> result = groupedConfigs.entrySet().stream()
                    .map(entry -> {
                        String region = entry.getKey();
                        List<RegionConfig> regionConfigs = entry.getValue();
                        
                        // 获取国家（同一地区的国家应该相同）
                        String country = regionConfigs.get(0).getCountry();
                        
                        // 转换语言列表
                        List<RegionConfigDTO.LanguageDTO> languages = regionConfigs.stream()
                                .map(RegionConfigDTO.LanguageDTO::new)
                                .collect(Collectors.toList());
                        
                        return new RegionConfigDTO(region, country, languages);
                    })
                    .collect(Collectors.toList());
            
            logger.info("获取所有区域配置完成 - 地区数量: {}, 总配置数: {}", result.size(), configs.size());
            return result;
            
        } catch (Exception e) {
            logger.error("获取区域配置失败", e);
            throw new RuntimeException("获取区域配置失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据地区获取支持的语言
     * 
     * @param region 地区名称
     * @return 语言DTO列表
     */
    public List<RegionConfigDTO.LanguageDTO> getLanguagesByRegion(String region) {
        logger.info("根据地区获取支持的语言 - 地区: {}", region);
        
        if (region == null || region.trim().isEmpty()) {
            throw new IllegalArgumentException("地区名称不能为空");
        }
        
        try {
            List<RegionConfig> configs = regionConfigMapper.selectByRegion(region);
            List<RegionConfigDTO.LanguageDTO> languages = configs.stream()
                    .map(RegionConfigDTO.LanguageDTO::new)
                    .collect(Collectors.toList());
            
            logger.info("根据地区获取支持的语言完成 - 地区: {}, 语言数量: {}", region, languages.size());
            return languages;
            
        } catch (Exception e) {
            logger.error("根据地区获取支持的语言失败 - 地区: {}", region, e);
            throw new RuntimeException("获取地区语言失败: " + e.getMessage());
        }
    }
    
    /**
     * 验证地区和语言的匹配关系
     * 
     * @param region 地区名称
     * @param languageCode 语言代码
     * @return 是否匹配
     */
    public boolean validateRegionLanguage(String region, String languageCode) {
        logger.info("验证地区和语言匹配关系 - 地区: {}, 语言: {}", region, languageCode);
        
        if (region == null || region.trim().isEmpty()) {
            throw new IllegalArgumentException("地区名称不能为空");
        }
        if (languageCode == null || languageCode.trim().isEmpty()) {
            throw new IllegalArgumentException("语言代码不能为空");
        }
        
        try {
            boolean isValid = regionConfigMapper.validateRegionLanguage(region, languageCode);
            
            logger.info("验证地区和语言匹配关系完成 - 地区: {}, 语言: {}, 有效: {}", 
                       region, languageCode, isValid);
            return isValid;
            
        } catch (Exception e) {
            logger.error("验证地区和语言匹配关系失败 - 地区: {}, 语言: {}", region, languageCode, e);
            return false;
        }
    }
    
    /**
     * 获取所有不重复的地区列表
     * 
     * @return 地区列表
     */
    public List<String> getDistinctRegions() {
        logger.info("获取所有不重复的地区列表");
        
        try {
            List<String> regions = regionConfigMapper.selectDistinctRegions();
            
            logger.info("获取所有不重复的地区列表完成 - 数量: {}", regions.size());
            return regions;
            
        } catch (Exception e) {
            logger.error("获取地区列表失败", e);
            throw new RuntimeException("获取地区列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有不重复的国家列表
     * 
     * @return 国家列表
     */
    public List<String> getDistinctCountries() {
        logger.info("获取所有不重复的国家列表");
        
        try {
            List<String> countries = regionConfigMapper.selectDistinctCountries();
            
            logger.info("获取所有不重复的国家列表完成 - 数量: {}", countries.size());
            return countries;
            
        } catch (Exception e) {
            logger.error("获取国家列表失败", e);
            throw new RuntimeException("获取国家列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有不重复的语言列表
     * 
     * @return 语言列表
     */
    public List<Map<String, String>> getDistinctLanguages() {
        logger.info("获取所有不重复的语言列表");
        
        try {
            List<Map<String, String>> languages = regionConfigMapper.selectDistinctLanguages();
            
            logger.info("获取所有不重复的语言列表完成 - 数量: {}", languages.size());
            return languages;
            
        } catch (Exception e) {
            logger.error("获取语言列表失败", e);
            throw new RuntimeException("获取语言列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取区域配置
     * 
     * @param id 配置ID
     * @return 区域配置实体
     */
    public RegionConfig getRegionConfigById(Long id) {
        logger.info("根据ID获取区域配置 - ID: {}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("配置ID不能为空");
        }
        
        RegionConfig config = regionConfigMapper.selectById(id);
        if (config == null) {
            throw new IllegalArgumentException("区域配置不存在，ID: " + id);
        }
        
        logger.info("根据ID获取区域配置完成 - ID: {}, 地区: {}, 语言: {}", 
                   id, config.getRegion(), config.getLanguageName());
        return config;
    }
    
    /**
     * 创建区域配置
     * 
     * @param config 区域配置实体
     * @return 创建的区域配置
     */
    public RegionConfig createRegionConfig(RegionConfig config) {
        logger.info("创建区域配置 - 地区: {}, 语言: {}", config.getRegion(), config.getLanguageName());
        
        // 验证必填字段
        validateRegionConfig(config);
        
        // 检查是否已存在
        int existCount = regionConfigMapper.checkExists(
            config.getRegion(), 
            config.getLanguageCode(), 
            null
        );
        if (existCount > 0) {
            throw new IllegalArgumentException("该地区和语言的配置已存在");
        }
        
        // 设置默认值
        if (config.getIsActive() == null) {
            config.setIsActive(true);
        }
        if (config.getSortOrder() == null) {
            config.setSortOrder(0);
        }
        config.setCreatedAt(LocalDateTime.now());
        config.setUpdatedAt(LocalDateTime.now());
        
        // 保存到数据库
        int result = regionConfigMapper.insert(config);
        if (result <= 0) {
            throw new RuntimeException("创建区域配置失败");
        }
        
        logger.info("创建区域配置成功 - ID: {}, 地区: {}, 语言: {}", 
                   config.getId(), config.getRegion(), config.getLanguageName());
        return config;
    }
    
    /**
     * 更新区域配置
     * 
     * @param id 配置ID
     * @param config 区域配置实体
     * @return 更新后的区域配置
     */
    public RegionConfig updateRegionConfig(Long id, RegionConfig config) {
        logger.info("更新区域配置 - ID: {}, 地区: {}, 语言: {}", 
                   id, config.getRegion(), config.getLanguageName());
        
        if (id == null) {
            throw new IllegalArgumentException("配置ID不能为空");
        }
        
        // 检查配置是否存在
        RegionConfig existingConfig = regionConfigMapper.selectById(id);
        if (existingConfig == null) {
            throw new IllegalArgumentException("区域配置不存在，ID: " + id);
        }
        
        // 验证必填字段
        validateRegionConfig(config);
        
        // 检查是否与其他配置冲突
        int existCount = regionConfigMapper.checkExists(
            config.getRegion(), 
            config.getLanguageCode(), 
            id
        );
        if (existCount > 0) {
            throw new IllegalArgumentException("该地区和语言的配置已存在");
        }
        
        // 更新字段
        config.setId(id);
        config.setUpdatedAt(LocalDateTime.now());
        
        // 保存到数据库
        int result = regionConfigMapper.update(config);
        if (result <= 0) {
            throw new RuntimeException("更新区域配置失败");
        }
        
        logger.info("更新区域配置成功 - ID: {}, 地区: {}, 语言: {}", 
                   id, config.getRegion(), config.getLanguageName());
        return config;
    }
    
    /**
     * 删除区域配置
     * 
     * @param id 配置ID
     */
    public void deleteRegionConfig(Long id) {
        logger.info("删除区域配置 - ID: {}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("配置ID不能为空");
        }
        
        // 检查配置是否存在
        RegionConfig existingConfig = regionConfigMapper.selectById(id);
        if (existingConfig == null) {
            throw new IllegalArgumentException("区域配置不存在，ID: " + id);
        }
        
        // 删除配置
        int result = regionConfigMapper.deleteById(id);
        if (result <= 0) {
            throw new RuntimeException("删除区域配置失败");
        }
        
        logger.info("删除区域配置成功 - ID: {}, 地区: {}, 语言: {}", 
                   id, existingConfig.getRegion(), existingConfig.getLanguageName());
    }
    
    /**
     * 更新区域配置的激活状态
     * 
     * @param id 配置ID
     * @param isActive 是否激活
     * @return 更新后的区域配置
     */
    public RegionConfig updateActiveStatus(Long id, Boolean isActive) {
        logger.info("更新区域配置激活状态 - ID: {}, 激活: {}", id, isActive);
        
        if (id == null) {
            throw new IllegalArgumentException("配置ID不能为空");
        }
        if (isActive == null) {
            throw new IllegalArgumentException("激活状态不能为空");
        }
        
        // 检查配置是否存在
        RegionConfig existingConfig = regionConfigMapper.selectById(id);
        if (existingConfig == null) {
            throw new IllegalArgumentException("区域配置不存在，ID: " + id);
        }
        
        // 更新激活状态
        int result = regionConfigMapper.updateActiveStatus(id, isActive);
        if (result <= 0) {
            throw new RuntimeException("更新区域配置激活状态失败");
        }
        
        // 重新查询更新后的数据
        existingConfig = regionConfigMapper.selectById(id);
        
        logger.info("更新区域配置激活状态成功 - ID: {}, 地区: {}, 激活: {}", 
                   id, existingConfig.getRegion(), isActive);
        return existingConfig;
    }
    
    /**
     * 批量创建区域配置
     * 
     * @param configs 区域配置列表
     * @return 成功创建的数量
     */
    public int batchCreateRegionConfigs(List<RegionConfig> configs) {
        logger.info("批量创建区域配置 - 数量: {}", configs.size());
        
        if (configs == null || configs.isEmpty()) {
            throw new IllegalArgumentException("区域配置列表不能为空");
        }
        
        // 验证所有配置
        for (RegionConfig config : configs) {
            validateRegionConfig(config);
            
            // 设置默认值
            if (config.getIsActive() == null) {
                config.setIsActive(true);
            }
            if (config.getSortOrder() == null) {
                config.setSortOrder(0);
            }
            config.setCreatedAt(LocalDateTime.now());
            config.setUpdatedAt(LocalDateTime.now());
        }
        
        // 批量插入
        int result = regionConfigMapper.batchInsert(configs);
        
        logger.info("批量创建区域配置完成 - 成功: {}/{}", result, configs.size());
        return result;
    }
    
    /**
     * 刷新区域配置数据
     * 重新加载区域配置信息
     */
    public void refreshData() {
        logger.info("刷新区域配置数据");
        // 数据刷新逻辑可以在这里实现，如重新验证数据库连接等
    }
    
    /**
     * 验证区域配置数据
     * 
     * @param config 区域配置实体
     */
    private void validateRegionConfig(RegionConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("区域配置不能为空");
        }
        if (config.getRegion() == null || config.getRegion().trim().isEmpty()) {
            throw new IllegalArgumentException("地区名称不能为空");
        }
        if (config.getCountry() == null || config.getCountry().trim().isEmpty()) {
            throw new IllegalArgumentException("国家名称不能为空");
        }
        if (config.getLanguageCode() == null || config.getLanguageCode().trim().isEmpty()) {
            throw new IllegalArgumentException("语言代码不能为空");
        }
        if (config.getLanguageName() == null || config.getLanguageName().trim().isEmpty()) {
            throw new IllegalArgumentException("语言名称不能为空");
        }
    }
}