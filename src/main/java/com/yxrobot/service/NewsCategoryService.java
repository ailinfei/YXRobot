package com.yxrobot.service;

import com.yxrobot.dto.NewsCategoryDTO;
import com.yxrobot.entity.NewsCategory;
import com.yxrobot.exception.NewsValidationException;
import com.yxrobot.exception.NewsOperationException;
import com.yxrobot.exception.NewsNotFoundException;
import com.yxrobot.mapper.NewsCategoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 新闻分类服务类
 * 负责处理新闻分类相关的业务逻辑
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@Service
@Transactional
public class NewsCategoryService {
    
    private static final Logger logger = LoggerFactory.getLogger(NewsCategoryService.class);
    
    @Autowired
    private NewsCategoryMapper newsCategoryMapper;
    
    /**
     * 获取所有启用的新闻分类
     * 
     * @return 分类列表
     */
    public List<NewsCategoryDTO> getAllEnabledCategories() {
        logger.info("获取所有启用的新闻分类");
        
        List<NewsCategory> categories = newsCategoryMapper.selectAllEnabled();
        List<NewsCategoryDTO> categoryDTOs = categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        logger.info("获取启用的新闻分类完成 - 数量: {}", categoryDTOs.size());
        return categoryDTOs;
    }
    
    /**
     * 获取所有新闻分类
     * 
     * @return 分类列表
     */
    public List<NewsCategoryDTO> getAllCategories() {
        logger.info("获取所有新闻分类");
        
        List<NewsCategory> categories = newsCategoryMapper.selectAll();
        List<NewsCategoryDTO> categoryDTOs = categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        logger.info("获取所有新闻分类完成 - 数量: {}", categoryDTOs.size());
        return categoryDTOs;
    }
    
    /**
     * 根据ID获取新闻分类
     * 
     * @param id 分类ID
     * @return 分类信息
     */
    public NewsCategoryDTO getCategoryById(Long id) {
        logger.info("获取新闻分类 - ID: {}", id);
        
        if (id == null) {
            throw new NewsValidationException("id", id, "分类ID不能为空");
        }
        
        NewsCategory category = newsCategoryMapper.selectById(id);
        if (category == null) {
            throw new NewsNotFoundException(id);
        }
        
        NewsCategoryDTO categoryDTO = convertToDTO(category);
        logger.info("获取新闻分类完成 - 名称: {}", category.getName());
        return categoryDTO;
    }
    
    /**
     * 创建新闻分类
     * 
     * @param name 分类名称
     * @param description 分类描述
     * @param sortOrder 排序权重
     * @return 创建的分类信息
     */
    public NewsCategoryDTO createCategory(String name, String description, Integer sortOrder) {
        logger.info("创建新闻分类 - 名称: {}", name);
        
        // 数据验证
        validateCategoryData(name, description);
        
        // 检查名称是否已存在
        if (newsCategoryMapper.existsByName(name)) {
            throw new NewsValidationException("name", name, "分类名称已存在: " + name);
        }
        
        // 创建分类
        NewsCategory category = new NewsCategory();
        category.setName(name);
        category.setDescription(description);
        category.setSortOrder(sortOrder != null ? sortOrder : 0);
        category.setIsEnabled(true);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        
        int result = newsCategoryMapper.insert(category);
        if (result <= 0) {
            throw new NewsOperationException("创建新闻分类", "数据库插入失败");
        }
        
        logger.info("创建新闻分类成功 - ID: {}, 名称: {}", category.getId(), category.getName());
        return convertToDTO(category);
    }
    
    /**
     * 更新新闻分类
     * 
     * @param id 分类ID
     * @param name 分类名称
     * @param description 分类描述
     * @param sortOrder 排序权重
     * @param isEnabled 是否启用
     * @return 更新后的分类信息
     */
    public NewsCategoryDTO updateCategory(Long id, String name, String description, 
                                         Integer sortOrder, Boolean isEnabled) {
        logger.info("更新新闻分类 - ID: {}, 名称: {}", id, name);
        
        if (id == null) {
            throw new NewsValidationException("id", id, "分类ID不能为空");
        }
        
        // 检查分类是否存在
        NewsCategory existingCategory = newsCategoryMapper.selectById(id);
        if (existingCategory == null) {
            throw new NewsNotFoundException(id);
        }
        
        // 数据验证
        validateCategoryData(name, description);
        
        // 检查名称是否已被其他分类使用
        if (newsCategoryMapper.existsByNameExcludeId(name, id)) {
            throw new NewsValidationException("name", name, "分类名称已存在: " + name);
        }
        
        // 更新分类
        NewsCategory category = new NewsCategory();
        category.setId(id);
        category.setName(name);
        category.setDescription(description);
        category.setSortOrder(sortOrder);
        category.setIsEnabled(isEnabled);
        category.setUpdatedAt(LocalDateTime.now());
        
        int result = newsCategoryMapper.updateById(category);
        if (result <= 0) {
            throw new NewsOperationException("更新新闻分类", id, "数据库更新失败");
        }
        
        logger.info("更新新闻分类成功 - ID: {}", id);
        return getCategoryById(id);
    }
    
    /**
     * 删除新闻分类
     * 
     * @param id 分类ID
     */
    public void deleteCategory(Long id) {
        logger.info("删除新闻分类 - ID: {}", id);
        
        if (id == null) {
            throw new NewsValidationException("id", id, "分类ID不能为空");
        }
        
        // 检查分类是否存在
        NewsCategory existingCategory = newsCategoryMapper.selectById(id);
        if (existingCategory == null) {
            throw new NewsNotFoundException(id);
        }
        
        // TODO: 检查是否有新闻使用该分类，如果有则不允许删除
        
        int result = newsCategoryMapper.deleteById(id);
        if (result <= 0) {
            throw new NewsOperationException("删除新闻分类", id, "数据库删除失败");
        }
        
        logger.info("删除新闻分类成功 - ID: {}", id);
    }
    
    /**
     * 启用/禁用分类
     * 
     * @param id 分类ID
     * @param isEnabled 是否启用
     */
    public void updateCategoryStatus(Long id, Boolean isEnabled) {
        logger.info("更新新闻分类状态 - ID: {}, 启用: {}", id, isEnabled);
        
        if (id == null) {
            throw new NewsValidationException("id", id, "分类ID不能为空");
        }
        
        if (isEnabled == null) {
            throw new NewsValidationException("isEnabled", isEnabled, "启用状态不能为空");
        }
        
        // 检查分类是否存在
        NewsCategory existingCategory = newsCategoryMapper.selectById(id);
        if (existingCategory == null) {
            throw new NewsNotFoundException(id);
        }
        
        int result = newsCategoryMapper.updateEnabledStatus(id, isEnabled);
        if (result <= 0) {
            throw new NewsOperationException("更新新闻分类状态", id, "数据库更新失败");
        }
        
        logger.info("更新新闻分类状态成功 - ID: {}", id);
    }
    
    /**
     * 批量更新分类排序
     * 
     * @param categories 分类列表（包含ID和排序权重）
     */
    public void batchUpdateSortOrder(List<NewsCategory> categories) {
        logger.info("批量更新分类排序 - 数量: {}", categories.size());
        
        if (categories == null || categories.isEmpty()) {
            throw new NewsValidationException("categories", categories, "分类列表不能为空");
        }
        
        // 验证数据
        for (NewsCategory category : categories) {
            if (category.getId() == null) {
                throw new NewsValidationException("id", category.getId(), "分类ID不能为空");
            }
            if (category.getSortOrder() == null) {
                throw new NewsValidationException("sortOrder", category.getSortOrder(), "排序权重不能为空");
            }
        }
        
        int result = newsCategoryMapper.batchUpdateSortOrder(categories);
        if (result <= 0) {
            throw new NewsOperationException("批量更新分类排序", "数据库更新失败");
        }
        
        logger.info("批量更新分类排序成功 - 数量: {}", categories.size());
    }
    
    /**
     * 获取分类统计信息
     * 
     * @return 统计信息
     */
    public Map<String, Object> getCategoryStats() {
        logger.info("获取分类统计信息");
        
        int totalCount = newsCategoryMapper.countAll();
        int enabledCount = newsCategoryMapper.countEnabled();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", totalCount);
        stats.put("enabled", enabledCount);
        stats.put("disabled", totalCount - enabledCount);
        
        logger.info("获取分类统计信息完成 - 总数: {}, 启用: {}", totalCount, enabledCount);
        return stats;
    }
    
    /**
     * 验证分类数据
     * 
     * @param name 分类名称
     * @param description 分类描述
     */
    private void validateCategoryData(String name, String description) {
        if (!StringUtils.hasText(name)) {
            throw new NewsValidationException("name", name, "分类名称不能为空");
        }
        
        if (name.length() > 100) {
            throw new NewsValidationException("name", name, "分类名称长度不能超过100个字符");
        }
        
        if (StringUtils.hasText(description) && description.length() > 500) {
            throw new NewsValidationException("description", description, "分类描述长度不能超过500个字符");
        }
    }
    
    /**
     * 将NewsCategory实体转换为NewsCategoryDTO
     * 
     * @param category NewsCategory实体
     * @return NewsCategoryDTO
     */
    private NewsCategoryDTO convertToDTO(NewsCategory category) {
        if (category == null) {
            return null;
        }
        
        NewsCategoryDTO dto = new NewsCategoryDTO();
        BeanUtils.copyProperties(category, dto);
        return dto;
    }
}