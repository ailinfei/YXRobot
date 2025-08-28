package com.yxrobot.service;

import com.yxrobot.dto.PlatformLinkDTO;
import com.yxrobot.dto.PlatformLinkFormDTO;
import com.yxrobot.entity.PlatformLink;
import com.yxrobot.mapper.PlatformLinkMapper;
import com.yxrobot.mapper.RegionConfigMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 平台链接管理服务类
 * 负责处理平台链接相关的业务逻辑
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-22
 */
@Service
@Transactional
public class PlatformLinkService {
    
    private static final Logger logger = LoggerFactory.getLogger(PlatformLinkService.class);
    
    @Autowired
    private PlatformLinkMapper platformLinkMapper;
    
    @Autowired
    private RegionConfigMapper regionConfigMapper;
    
    /**
     * 分页查询平台链接列表
     * 支持按平台类型、地区、语言、状态等条件筛选
     * 
     * @param page 页码（从1开始）
     * @param pageSize 每页大小
     * @param platformType 平台类型（可选）
     * @param region 地区（可选）
     * @param languageCode 语言代码（可选）
     * @param linkStatus 链接状态（可选）
     * @param isEnabled 是否启用（可选）
     * @param keyword 关键词搜索（可选）
     * @return 分页结果
     */
    public Map<String, Object> getPlatformLinks(Integer page, Integer pageSize, 
                                              String platformType, String region, String languageCode,
                                              String linkStatus, Boolean isEnabled, String keyword) {
        logger.info("查询平台链接列表 - 页码: {}, 页大小: {}, 平台类型: {}, 地区: {}, 语言: {}, 状态: {}, 启用: {}, 关键词: {}", 
                   page, pageSize, platformType, region, languageCode, linkStatus, isEnabled, keyword);
        
        // 参数验证
        if (page == null || page < 1) {
            page = 1;
        }
        if (pageSize == null || pageSize < 1 || pageSize > 100) {
            pageSize = 20;
        }
        
        // 构建查询参数
        Map<String, Object> params = new HashMap<>();
        params.put("offset", (page - 1) * pageSize);
        params.put("limit", pageSize);
        
        if (platformType != null && !platformType.trim().isEmpty()) {
            params.put("platformType", platformType.trim());
        }
        if (region != null && !region.trim().isEmpty()) {
            params.put("region", region.trim());
        }
        if (languageCode != null && !languageCode.trim().isEmpty()) {
            params.put("languageCode", languageCode.trim());
        }
        if (linkStatus != null && !linkStatus.trim().isEmpty()) {
            params.put("linkStatus", linkStatus.trim());
        }
        if (isEnabled != null) {
            params.put("isEnabled", isEnabled);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            params.put("keyword", keyword.trim());
        }
        
        // 查询数据
        List<PlatformLink> links = platformLinkMapper.selectList(params);
        int total = platformLinkMapper.selectCount(params);
        
        // 转换为DTO
        List<PlatformLinkDTO> linkDTOs = links.stream()
                .map(PlatformLinkDTO::new)
                .collect(Collectors.toList());
        
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("list", linkDTOs);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        result.put("totalPages", (int) Math.ceil((double) total / pageSize));
        
        logger.info("查询平台链接列表完成 - 总数: {}, 当前页: {}/{}", total, page, result.get("totalPages"));
        return result;
    }
    
    /**
     * 根据ID获取平台链接详情
     * 
     * @param id 链接ID
     * @return 平台链接DTO
     * @throws IllegalArgumentException 如果链接不存在
     */
    public PlatformLinkDTO getPlatformLinkById(Long id) {
        logger.info("查询平台链接详情 - ID: {}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("链接ID不能为空");
        }
        
        PlatformLink link = platformLinkMapper.selectById(id);
        if (link == null) {
            throw new IllegalArgumentException("链接不存在，ID: " + id);
        }
        
        logger.info("查询平台链接详情完成 - ID: {}, 平台: {}", id, link.getPlatformName());
        return new PlatformLinkDTO(link);
    }
    
    /**
     * 创建新的平台链接
     * 
     * @param formDTO 表单数据
     * @return 创建的平台链接DTO
     * @throws IllegalArgumentException 如果数据验证失败
     */
    public PlatformLinkDTO createPlatformLink(@Valid PlatformLinkFormDTO formDTO) {
        logger.info("创建平台链接 - 平台: {}, 地区: {}, 语言: {}", 
                   formDTO.getPlatformName(), formDTO.getRegion(), formDTO.getLanguageCode());
        
        // 验证地区和语言配置是否有效
        if (!regionConfigMapper.validateRegionLanguage(formDTO.getRegion(), formDTO.getLanguageCode())) {
            throw new IllegalArgumentException("无效的地区和语言组合: " + formDTO.getRegion() + " - " + formDTO.getLanguageCode());
        }
        
        // 检查是否已存在相同的平台链接
        int existCount = platformLinkMapper.checkExists(
            formDTO.getPlatformName(), 
            formDTO.getRegion(), 
            formDTO.getLanguageCode(), 
            null
        );
        if (existCount > 0) {
            throw new IllegalArgumentException("该平台在指定地区和语言下的链接已存在");
        }
        
        // 创建实体对象
        PlatformLink link = new PlatformLink();
        link.setPlatformName(formDTO.getPlatformName());
        link.setPlatformType(PlatformLink.PlatformType.fromCode(formDTO.getPlatformType()));
        link.setLinkUrl(formDTO.getLinkUrl());
        link.setRegion(formDTO.getRegion());
        link.setCountry(formDTO.getCountry());
        link.setLanguageCode(formDTO.getLanguageCode());
        link.setIsEnabled(formDTO.getIsEnabled());
        
        // 从区域配置中获取语言名称
        List<com.yxrobot.entity.RegionConfig> configs = regionConfigMapper.selectByRegion(formDTO.getRegion());
        String languageName = configs.stream()
                .filter(config -> config.getLanguageCode().equals(formDTO.getLanguageCode()))
                .map(com.yxrobot.entity.RegionConfig::getLanguageName)
                .findFirst()
                .orElse(formDTO.getLanguageCode());
        link.setLanguageName(languageName);
        
        // 设置默认值
        link.setLinkStatus(PlatformLink.LinkStatus.ACTIVE);
        link.setClickCount(0);
        link.setConversionCount(0);
        link.setIsDeleted(false);
        link.setCreatedAt(LocalDateTime.now());
        link.setUpdatedAt(LocalDateTime.now());
        
        // 保存到数据库
        int result = platformLinkMapper.insert(link);
        if (result <= 0) {
            throw new RuntimeException("创建平台链接失败");
        }
        
        logger.info("创建平台链接成功 - ID: {}, 平台: {}", link.getId(), link.getPlatformName());
        return new PlatformLinkDTO(link);
    }
    
    /**
     * 更新平台链接信息
     * 
     * @param id 链接ID
     * @param formDTO 表单数据
     * @return 更新后的平台链接DTO
     * @throws IllegalArgumentException 如果链接不存在或数据验证失败
     */
    public PlatformLinkDTO updatePlatformLink(Long id, @Valid PlatformLinkFormDTO formDTO) {
        logger.info("更新平台链接 - ID: {}, 平台: {}", id, formDTO.getPlatformName());
        
        if (id == null) {
            throw new IllegalArgumentException("链接ID不能为空");
        }
        
        // 检查链接是否存在
        PlatformLink existingLink = platformLinkMapper.selectById(id);
        if (existingLink == null) {
            throw new IllegalArgumentException("链接不存在，ID: " + id);
        }
        
        // 验证地区和语言配置是否有效
        if (!regionConfigMapper.validateRegionLanguage(formDTO.getRegion(), formDTO.getLanguageCode())) {
            throw new IllegalArgumentException("无效的地区和语言组合: " + formDTO.getRegion() + " - " + formDTO.getLanguageCode());
        }
        
        // 检查是否与其他链接冲突
        int existCount = platformLinkMapper.checkExists(
            formDTO.getPlatformName(), 
            formDTO.getRegion(), 
            formDTO.getLanguageCode(), 
            id
        );
        if (existCount > 0) {
            throw new IllegalArgumentException("该平台在指定地区和语言下的链接已存在");
        }
        
        // 更新实体对象
        existingLink.setPlatformName(formDTO.getPlatformName());
        existingLink.setPlatformType(PlatformLink.PlatformType.fromCode(formDTO.getPlatformType()));
        existingLink.setLinkUrl(formDTO.getLinkUrl());
        existingLink.setRegion(formDTO.getRegion());
        existingLink.setCountry(formDTO.getCountry());
        existingLink.setLanguageCode(formDTO.getLanguageCode());
        existingLink.setIsEnabled(formDTO.getIsEnabled());
        
        // 从区域配置中获取语言名称
        List<com.yxrobot.entity.RegionConfig> configs = regionConfigMapper.selectByRegion(formDTO.getRegion());
        String languageName = configs.stream()
                .filter(config -> config.getLanguageCode().equals(formDTO.getLanguageCode()))
                .map(com.yxrobot.entity.RegionConfig::getLanguageName)
                .findFirst()
                .orElse(formDTO.getLanguageCode());
        existingLink.setLanguageName(languageName);
        
        existingLink.setUpdatedAt(LocalDateTime.now());
        
        // 保存到数据库
        int result = platformLinkMapper.update(existingLink);
        if (result <= 0) {
            throw new RuntimeException("更新平台链接失败");
        }
        
        logger.info("更新平台链接成功 - ID: {}, 平台: {}", id, existingLink.getPlatformName());
        return new PlatformLinkDTO(existingLink);
    }
    
    /**
     * 删除平台链接（软删除）
     * 
     * @param id 链接ID
     * @throws IllegalArgumentException 如果链接不存在
     */
    public void deletePlatformLink(Long id) {
        logger.info("删除平台链接 - ID: {}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("链接ID不能为空");
        }
        
        // 检查链接是否存在
        PlatformLink existingLink = platformLinkMapper.selectById(id);
        if (existingLink == null) {
            throw new IllegalArgumentException("链接不存在，ID: " + id);
        }
        
        // 执行软删除
        int result = platformLinkMapper.deleteById(id);
        if (result <= 0) {
            throw new RuntimeException("删除平台链接失败");
        }
        
        logger.info("删除平台链接成功 - ID: {}, 平台: {}", id, existingLink.getPlatformName());
    }
    
    /**
     * 切换平台链接的启用状态
     * 
     * @param id 链接ID
     * @param isEnabled 是否启用
     * @return 更新后的平台链接DTO
     * @throws IllegalArgumentException 如果链接不存在
     */
    public PlatformLinkDTO togglePlatformLinkStatus(Long id, Boolean isEnabled) {
        logger.info("切换平台链接状态 - ID: {}, 启用: {}", id, isEnabled);
        
        if (id == null) {
            throw new IllegalArgumentException("链接ID不能为空");
        }
        if (isEnabled == null) {
            throw new IllegalArgumentException("启用状态不能为空");
        }
        
        // 检查链接是否存在
        PlatformLink existingLink = platformLinkMapper.selectById(id);
        if (existingLink == null) {
            throw new IllegalArgumentException("链接不存在，ID: " + id);
        }
        
        // 更新启用状态
        int result = platformLinkMapper.updateEnabledStatus(id, isEnabled);
        if (result <= 0) {
            throw new RuntimeException("更新平台链接状态失败");
        }
        
        // 重新查询更新后的数据
        existingLink = platformLinkMapper.selectById(id);
        
        logger.info("切换平台链接状态成功 - ID: {}, 平台: {}, 启用: {}", 
                   id, existingLink.getPlatformName(), isEnabled);
        return new PlatformLinkDTO(existingLink);
    }
    
    /**
     * 批量删除平台链接
     * 
     * @param ids 链接ID列表
     * @return 删除成功的数量
     */
    public int batchDeletePlatformLinks(List<Long> ids) {
        logger.info("批量删除平台链接 - 数量: {}", ids.size());
        
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("链接ID列表不能为空");
        }
        
        int successCount = 0;
        for (Long id : ids) {
            try {
                deletePlatformLink(id);
                successCount++;
            } catch (Exception e) {
                logger.warn("删除平台链接失败 - ID: {}, 错误: {}", id, e.getMessage());
            }
        }
        
        logger.info("批量删除平台链接完成 - 成功: {}/{}", successCount, ids.size());
        return successCount;
    }
    
    /**
     * 根据地区和语言查询平台链接
     * 
     * @param region 地区
     * @param languageCode 语言代码
     * @return 平台链接列表
     */
    public List<PlatformLinkDTO> getPlatformLinksByRegionAndLanguage(String region, String languageCode) {
        logger.info("根据地区和语言查询平台链接 - 地区: {}, 语言: {}", region, languageCode);
        
        if (region == null || region.trim().isEmpty()) {
            throw new IllegalArgumentException("地区不能为空");
        }
        if (languageCode == null || languageCode.trim().isEmpty()) {
            throw new IllegalArgumentException("语言代码不能为空");
        }
        
        List<PlatformLink> links = platformLinkMapper.selectByRegionAndLanguage(region, languageCode);
        List<PlatformLinkDTO> result = links.stream()
                .map(PlatformLinkDTO::new)
                .collect(Collectors.toList());
        
        logger.info("根据地区和语言查询平台链接完成 - 地区: {}, 语言: {}, 数量: {}", 
                   region, languageCode, result.size());
        return result;
    }
    
    /**
     * 根据平台类型查询平台链接
     * 
     * @param platformType 平台类型
     * @return 平台链接列表
     */
    public List<PlatformLinkDTO> getPlatformLinksByType(String platformType) {
        logger.info("根据平台类型查询平台链接 - 类型: {}", platformType);
        
        if (platformType == null || platformType.trim().isEmpty()) {
            throw new IllegalArgumentException("平台类型不能为空");
        }
        
        List<PlatformLink> links = platformLinkMapper.selectByPlatformType(platformType);
        List<PlatformLinkDTO> result = links.stream()
                .map(PlatformLinkDTO::new)
                .collect(Collectors.toList());
        
        logger.info("根据平台类型查询平台链接完成 - 类型: {}, 数量: {}", platformType, result.size());
        return result;
    }
}