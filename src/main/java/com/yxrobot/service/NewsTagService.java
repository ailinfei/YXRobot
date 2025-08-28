package com.yxrobot.service;

import com.yxrobot.dto.NewsTagDTO;
import com.yxrobot.entity.NewsTag;
import com.yxrobot.exception.NewsValidationException;
import com.yxrobot.exception.NewsOperationException;
import com.yxrobot.exception.NewsNotFoundException;
import com.yxrobot.mapper.NewsTagMapper;
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
 * 新闻标签服务类
 * 负责处理新闻标签相关的业务逻辑
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@Service
@Transactional
public class NewsTagService {
    
    private static final Logger logger = LoggerFactory.getLogger(NewsTagService.class);
    
    @Autowired
    private NewsTagMapper newsTagMapper;
    
    /**
     * 获取所有新闻标签
     * 
     * @return 标签列表
     */
    public List<NewsTagDTO> getAllTags() {
        logger.info("获取所有新闻标签");
        
        List<NewsTag> tags = newsTagMapper.selectAll();
        List<NewsTagDTO> tagDTOs = tags.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        logger.info("获取所有新闻标签完成 - 数量: {}", tagDTOs.size());
        return tagDTOs;
    }
    
    /**
     * 分页获取新闻标签
     * 
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    public Map<String, Object> getTagsByPage(int page, int pageSize) {
        logger.info("分页获取新闻标签 - 页码: {}, 每页大小: {}", page, pageSize);
        
        // 参数验证
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 10;
        
        int offset = (page - 1) * pageSize;
        
        List<NewsTag> tags = newsTagMapper.selectByPage(offset, pageSize);
        int total = newsTagMapper.countAll();
        
        List<NewsTagDTO> tagDTOs = tags.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", tagDTOs);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        result.put("totalPages", (int) Math.ceil((double) total / pageSize));
        
        logger.info("分页获取新闻标签完成 - 总数: {}, 当前页数据量: {}", total, tagDTOs.size());
        return result;
    }
    
    /**
     * 根据使用次数获取热门标签
     * 
     * @param limit 数量限制
     * @return 热门标签列表
     */
    public List<NewsTagDTO> getPopularTags(int limit) {
        logger.info("获取热门标签 - 数量限制: {}", limit);
        
        if (limit < 1 || limit > 50) limit = 10;
        
        List<NewsTag> tags = newsTagMapper.selectByUsageCount(limit);
        List<NewsTagDTO> tagDTOs = tags.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        logger.info("获取热门标签完成 - 数量: {}", tagDTOs.size());
        return tagDTOs;
    }
    
    /**
     * 根据名称搜索标签
     * 
     * @param name 标签名称（支持模糊搜索）
     * @return 标签列表
     */
    public List<NewsTagDTO> searchTagsByName(String name) {
        logger.info("搜索标签 - 名称: {}", name);
        
        if (!StringUtils.hasText(name)) {
            throw new NewsValidationException("name", name, "搜索名称不能为空");
        }
        
        List<NewsTag> tags = newsTagMapper.selectByNameLike(name);
        List<NewsTagDTO> tagDTOs = tags.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        logger.info("搜索标签完成 - 名称: {}, 数量: {}", name, tagDTOs.size());
        return tagDTOs;
    }
    
    /**
     * 根据ID获取标签
     * 
     * @param id 标签ID
     * @return 标签信息
     */
    public NewsTagDTO getTagById(Long id) {
        logger.info("获取标签 - ID: {}", id);
        
        if (id == null) {
            throw new NewsValidationException("id", id, "标签ID不能为空");
        }
        
        NewsTag tag = newsTagMapper.selectById(id);
        if (tag == null) {
            throw new NewsNotFoundException(id);
        }
        
        NewsTagDTO tagDTO = convertToDTO(tag);
        logger.info("获取标签完成 - 名称: {}", tag.getName());
        return tagDTO;
    }
    
    /**
     * 根据ID列表获取标签
     * 
     * @param ids 标签ID列表
     * @return 标签列表
     */
    public List<NewsTagDTO> getTagsByIds(List<Long> ids) {
        logger.info("批量获取标签 - 数量: {}", ids.size());
        
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        
        List<NewsTag> tags = newsTagMapper.selectByIds(ids);
        List<NewsTagDTO> tagDTOs = tags.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        logger.info("批量获取标签完成 - 数量: {}", tagDTOs.size());
        return tagDTOs;
    }
    
    /**
     * 创建标签
     * 
     * @param name 标签名称
     * @param color 标签颜色
     * @return 创建的标签信息
     */
    public NewsTagDTO createTag(String name, String color) {
        logger.info("创建标签 - 名称: {}, 颜色: {}", name, color);
        
        // 数据验证
        validateTagData(name, color);
        
        // 检查名称是否已存在
        if (newsTagMapper.existsByName(name)) {
            throw new NewsValidationException("name", name, "标签名称已存在: " + name);
        }
        
        // 创建标签
        NewsTag tag = new NewsTag();
        tag.setName(name);
        tag.setColor(StringUtils.hasText(color) ? color : "#409EFF");
        tag.setUsageCount(0);
        tag.setCreatedAt(LocalDateTime.now());
        tag.setUpdatedAt(LocalDateTime.now());
        
        int result = newsTagMapper.insert(tag);
        if (result <= 0) {
            throw new NewsOperationException("创建标签", "数据库插入失败");
        }
        
        logger.info("创建标签成功 - ID: {}, 名称: {}", tag.getId(), tag.getName());
        return convertToDTO(tag);
    }
    
    /**
     * 更新标签
     * 
     * @param id 标签ID
     * @param name 标签名称
     * @param color 标签颜色
     * @return 更新后的标签信息
     */
    public NewsTagDTO updateTag(Long id, String name, String color) {
        logger.info("更新标签 - ID: {}, 名称: {}, 颜色: {}", id, name, color);
        
        if (id == null) {
            throw new NewsValidationException("id", id, "标签ID不能为空");
        }
        
        // 检查标签是否存在
        NewsTag existingTag = newsTagMapper.selectById(id);
        if (existingTag == null) {
            throw new NewsNotFoundException(id);
        }
        
        // 数据验证
        validateTagData(name, color);
        
        // 检查名称是否已被其他标签使用
        if (newsTagMapper.existsByNameExcludeId(name, id)) {
            throw new NewsValidationException("name", name, "标签名称已存在: " + name);
        }
        
        // 更新标签
        NewsTag tag = new NewsTag();
        tag.setId(id);
        tag.setName(name);
        tag.setColor(color);
        tag.setUpdatedAt(LocalDateTime.now());
        
        int result = newsTagMapper.updateById(tag);
        if (result <= 0) {
            throw new NewsOperationException("更新标签", id, "数据库更新失败");
        }
        
        logger.info("更新标签成功 - ID: {}", id);
        return getTagById(id);
    }
    
    /**
     * 删除标签
     * 
     * @param id 标签ID
     */
    public void deleteTag(Long id) {
        logger.info("删除标签 - ID: {}", id);
        
        if (id == null) {
            throw new NewsValidationException("id", id, "标签ID不能为空");
        }
        
        // 检查标签是否存在
        NewsTag existingTag = newsTagMapper.selectById(id);
        if (existingTag == null) {
            throw new NewsNotFoundException(id);
        }
        
        // TODO: 检查是否有新闻使用该标签，如果有则需要先解除关联
        
        int result = newsTagMapper.deleteById(id);
        if (result <= 0) {
            throw new NewsOperationException("删除标签", id, "数据库删除失败");
        }
        
        logger.info("删除标签成功 - ID: {}", id);
    }
    
    /**
     * 批量删除标签
     * 
     * @param ids 标签ID列表
     */
    public void batchDeleteTags(List<Long> ids) {
        logger.info("批量删除标签 - 数量: {}", ids.size());
        
        if (ids == null || ids.isEmpty()) {
            throw new NewsValidationException("ids", ids, "标签ID列表不能为空");
        }
        
        for (Long id : ids) {
            deleteTag(id);
        }
        
        logger.info("批量删除标签完成 - 数量: {}", ids.size());
    }
    
    /**
     * 增加标签使用次数
     * 
     * @param id 标签ID
     */
    public void incrementUsageCount(Long id) {
        logger.debug("增加标签使用次数 - ID: {}", id);
        
        if (id == null) {
            throw new NewsValidationException("id", id, "标签ID不能为空");
        }
        
        int result = newsTagMapper.incrementUsageCount(id);
        if (result <= 0) {
            logger.warn("增加标签使用次数失败 - ID: {}", id);
        }
    }
    
    /**
     * 减少标签使用次数
     * 
     * @param id 标签ID
     */
    public void decrementUsageCount(Long id) {
        logger.debug("减少标签使用次数 - ID: {}", id);
        
        if (id == null) {
            throw new NewsValidationException("id", id, "标签ID不能为空");
        }
        
        int result = newsTagMapper.decrementUsageCount(id);
        if (result <= 0) {
            logger.warn("减少标签使用次数失败 - ID: {}", id);
        }
    }
    
    /**
     * 批量增加标签使用次数
     * 
     * @param ids 标签ID列表
     */
    public void batchIncrementUsageCount(List<Long> ids) {
        logger.debug("批量增加标签使用次数 - 数量: {}", ids.size());
        
        if (ids == null || ids.isEmpty()) {
            return;
        }
        
        int result = newsTagMapper.batchIncrementUsageCount(ids);
        if (result <= 0) {
            logger.warn("批量增加标签使用次数失败");
        }
    }
    
    /**
     * 批量减少标签使用次数
     * 
     * @param ids 标签ID列表
     */
    public void batchDecrementUsageCount(List<Long> ids) {
        logger.debug("批量减少标签使用次数 - 数量: {}", ids.size());
        
        if (ids == null || ids.isEmpty()) {
            return;
        }
        
        int result = newsTagMapper.batchDecrementUsageCount(ids);
        if (result <= 0) {
            logger.warn("批量减少标签使用次数失败");
        }
    }
    
    /**
     * 重置标签使用次数
     * 
     * @param id 标签ID
     */
    public void resetUsageCount(Long id) {
        logger.info("重置标签使用次数 - ID: {}", id);
        
        if (id == null) {
            throw new NewsValidationException("id", id, "标签ID不能为空");
        }
        
        int result = newsTagMapper.resetUsageCount(id);
        if (result <= 0) {
            throw new NewsOperationException("重置标签使用次数", id, "数据库更新失败");
        }
        
        logger.info("重置标签使用次数成功 - ID: {}", id);
    }
    
    /**
     * 获取标签统计信息
     * 
     * @return 统计信息
     */
    public Map<String, Object> getTagStats() {
        logger.info("获取标签统计信息");
        
        int totalCount = newsTagMapper.countAll();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", totalCount);
        
        logger.info("获取标签统计信息完成 - 总数: {}", totalCount);
        return stats;
    }
    
    /**
     * 验证标签数据
     * 
     * @param name 标签名称
     * @param color 标签颜色
     */
    private void validateTagData(String name, String color) {
        if (!StringUtils.hasText(name)) {
            throw new NewsValidationException("name", name, "标签名称不能为空");
        }
        
        if (name.length() > 50) {
            throw new NewsValidationException("name", name, "标签名称长度不能超过50个字符");
        }
        
        if (StringUtils.hasText(color) && color.length() > 20) {
            throw new NewsValidationException("color", color, "标签颜色长度不能超过20个字符");
        }
        
        // 验证颜色格式（简单验证）
        if (StringUtils.hasText(color) && !color.matches("^#[0-9A-Fa-f]{6}$")) {
            throw new NewsValidationException("color", color, "标签颜色格式不正确，应为十六进制颜色值（如：#409EFF）");
        }
    }
    
    /**
     * 将NewsTag实体转换为NewsTagDTO
     * 
     * @param tag NewsTag实体
     * @return NewsTagDTO
     */
    private NewsTagDTO convertToDTO(NewsTag tag) {
        if (tag == null) {
            return null;
        }
        
        NewsTagDTO dto = new NewsTagDTO();
        BeanUtils.copyProperties(tag, dto);
        return dto;
    }
}