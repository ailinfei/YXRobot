package com.yxrobot.service;

import com.yxrobot.dto.NewsDTO;
import com.yxrobot.dto.NewsFormDTO;
import com.yxrobot.dto.NewsTagDTO;
import com.yxrobot.exception.NewsNotFoundException;
import com.yxrobot.exception.NewsValidationException;
import com.yxrobot.exception.NewsOperationException;
import com.yxrobot.entity.News;
import com.yxrobot.entity.NewsCategory;
import com.yxrobot.entity.NewsStatus;
import com.yxrobot.entity.NewsTag;
import com.yxrobot.mapper.NewsMapper;
import com.yxrobot.mapper.NewsCategoryMapper;
import com.yxrobot.mapper.NewsTagMapper;
import com.yxrobot.mapper.NewsTagRelationMapper;
import com.yxrobot.validation.NewsValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 新闻管理服务类
 * 负责处理新闻相关的业务逻辑
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@Service
@Transactional
public class NewsService {
    
    private static final Logger logger = LoggerFactory.getLogger(NewsService.class);
    
    @Autowired
    private NewsMapper newsMapper;
    
    @Autowired
    private NewsCategoryMapper newsCategoryMapper;
    
    @Autowired
    private NewsValidator newsValidator;
    
    @Autowired
    private NewsTagMapper newsTagMapper;
    
    @Autowired
    private NewsTagRelationMapper newsTagRelationMapper;
    
    /**
     * 分页查询新闻列表
     * 支持按分类、状态、作者、关键词等条件筛选
     * 
     * @param page 页码（从1开始）
     * @param pageSize 每页大小
     * @param categoryId 分类ID（可选）
     * @param status 新闻状态（可选）
     * @param author 作者（可选）
     * @param keyword 搜索关键词（可选）
     * @param isFeatured 是否推荐（可选）
     * @return 分页结果
     */
    public Map<String, Object> getNewsList(int page, int pageSize, Long categoryId, 
                                          NewsStatus status, String author, String keyword, 
                                          Boolean isFeatured) {
        logger.info("查询新闻列表 - 页码: {}, 每页大小: {}, 分类ID: {}, 状态: {}, 作者: {}, 关键词: {}, 是否推荐: {}", 
                   page, pageSize, categoryId, status, author, keyword, isFeatured);
        
        // 参数验证
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 10;
        
        int offset = (page - 1) * pageSize;
        
        // 构建查询条件
        Map<String, Object> conditions = new HashMap<>();
        if (categoryId != null) {
            conditions.put("categoryId", categoryId);
        }
        if (status != null) {
            conditions.put("status", status);
        }
        if (StringUtils.hasText(author)) {
            conditions.put("author", author);
        }
        if (StringUtils.hasText(keyword)) {
            conditions.put("keyword", keyword);
        }
        if (isFeatured != null) {
            conditions.put("isFeatured", isFeatured);
        }
        
        // 查询数据
        List<News> newsList;
        int total;
        
        if (conditions.isEmpty()) {
            // 无条件查询
            newsList = newsMapper.selectByPage(offset, pageSize);
            total = newsMapper.countAll();
        } else {
            // 条件查询
            newsList = newsMapper.selectByConditions(conditions, offset, pageSize);
            total = newsMapper.countByConditions(conditions);
        }
        
        // 转换为DTO
        List<NewsDTO> newsDTOList = newsList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("list", newsDTOList);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        result.put("totalPages", (int) Math.ceil((double) total / pageSize));
        
        logger.info("查询新闻列表完成 - 总数: {}, 当前页数据量: {}", total, newsDTOList.size());
        return result;
    }
    
    /**
     * 根据ID获取新闻详情
     * 
     * @param id 新闻ID
     * @return 新闻详情
     */
    public NewsDTO getNewsById(Long id) {
        logger.info("查询新闻详情 - ID: {}", id);
        
        if (id == null) {
            throw new NewsValidationException("id", id, "新闻ID不能为空");
        }
        
        News news = newsMapper.selectByIdWithDetails(id);
        if (news == null) {
            throw new NewsNotFoundException(id);
        }
        
        NewsDTO newsDTO = convertToDTO(news);
        logger.info("查询新闻详情完成 - 标题: {}", news.getTitle());
        return newsDTO;
    }
    
    /**
     * 创建新闻
     * 
     * @param newsFormDTO 新闻表单数据
     * @return 创建的新闻信息
     */
    public NewsDTO createNews(@Valid NewsFormDTO newsFormDTO) {
        logger.info("创建新闻 - 标题: {}", newsFormDTO.getTitle());
        
        // 数据验证
        validateNewsForm(newsFormDTO);
        
        // 转换为实体
        News news = convertFromFormDTO(newsFormDTO);
        news.setCreatedAt(LocalDateTime.now());
        news.setUpdatedAt(LocalDateTime.now());
        
        // 保存新闻
        int result = newsMapper.insert(news);
        if (result <= 0) {
            throw new NewsOperationException("创建新闻", "数据库插入失败");
        }
        
        // 处理标签关联
        if (newsFormDTO.getTagIds() != null && !newsFormDTO.getTagIds().isEmpty()) {
            newsTagRelationMapper.updateNewsTagRelations(news.getId(), newsFormDTO.getTagIds());
            // 更新标签使用次数
            newsTagMapper.batchIncrementUsageCount(newsFormDTO.getTagIds());
        }
        
        logger.info("创建新闻成功 - ID: {}, 标题: {}", news.getId(), news.getTitle());
        return getNewsById(news.getId());
    }
    
    /**
     * 更新新闻
     * 
     * @param id 新闻ID
     * @param newsFormDTO 新闻表单数据
     * @return 更新后的新闻信息
     */
    public NewsDTO updateNews(Long id, @Valid NewsFormDTO newsFormDTO) {
        logger.info("更新新闻 - ID: {}, 标题: {}", id, newsFormDTO.getTitle());
        
        if (id == null) {
            throw new NewsValidationException("id", id, "新闻ID不能为空");
        }
        
        // 检查新闻是否存在
        News existingNews = newsMapper.selectById(id);
        if (existingNews == null) {
            throw new NewsNotFoundException(id);
        }
        
        // 数据验证
        validateNewsForm(newsFormDTO);
        
        // 转换为实体并设置ID
        News news = convertFromFormDTO(newsFormDTO);
        news.setId(id);
        news.setUpdatedAt(LocalDateTime.now());
        
        // 更新新闻
        int result = newsMapper.updateById(news);
        if (result <= 0) {
            throw new NewsOperationException("更新新闻", id, "数据库更新失败");
        }
        
        // 处理标签关联
        List<Long> oldTagIds = newsTagRelationMapper.selectTagIdsByNewsId(id);
        List<Long> newTagIds = newsFormDTO.getTagIds();
        
        // 更新标签关联
        newsTagRelationMapper.updateNewsTagRelations(id, newTagIds);
        
        // 更新标签使用次数
        if (oldTagIds != null && !oldTagIds.isEmpty()) {
            newsTagMapper.batchDecrementUsageCount(oldTagIds);
        }
        if (newTagIds != null && !newTagIds.isEmpty()) {
            newsTagMapper.batchIncrementUsageCount(newTagIds);
        }
        
        logger.info("更新新闻成功 - ID: {}", id);
        return getNewsById(id);
    }
    
    /**
     * 删除新闻（软删除）
     * 
     * @param id 新闻ID
     */
    public void deleteNews(Long id) {
        logger.info("删除新闻 - ID: {}", id);
        
        if (id == null) {
            throw new NewsValidationException("id", id, "新闻ID不能为空");
        }
        
        // 检查新闻是否存在
        News existingNews = newsMapper.selectById(id);
        if (existingNews == null) {
            throw new NewsNotFoundException(id);
        }
        
        // 软删除新闻
        int result = newsMapper.softDeleteById(id);
        if (result <= 0) {
            throw new NewsOperationException("删除新闻", id, "数据库删除失败");
        }
        
        // 减少标签使用次数
        List<Long> tagIds = newsTagRelationMapper.selectTagIdsByNewsId(id);
        if (tagIds != null && !tagIds.isEmpty()) {
            newsTagMapper.batchDecrementUsageCount(tagIds);
        }
        
        logger.info("删除新闻成功 - ID: {}", id);
    }
    
    /**
     * 批量删除新闻
     * 
     * @param ids 新闻ID列表
     */
    public void batchDeleteNews(List<Long> ids) {
        logger.info("批量删除新闻 - 数量: {}", ids.size());
        
        if (ids == null || ids.isEmpty()) {
            throw new NewsValidationException("ids", ids, "新闻ID列表不能为空");
        }
        
        for (Long id : ids) {
            deleteNews(id);
        }
        
        logger.info("批量删除新闻完成 - 数量: {}", ids.size());
    }
    
    /**
     * 搜索新闻
     * 
     * @param keyword 搜索关键词
     * @param page 页码
     * @param pageSize 每页大小
     * @return 搜索结果
     */
    public Map<String, Object> searchNews(String keyword, int page, int pageSize) {
        logger.info("搜索新闻 - 关键词: {}, 页码: {}, 每页大小: {}", keyword, page, pageSize);
        
        if (!StringUtils.hasText(keyword)) {
            throw new NewsValidationException("keyword", keyword, "搜索关键词不能为空");
        }
        
        // 参数验证
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 10;
        
        int offset = (page - 1) * pageSize;
        
        // 搜索新闻
        List<News> newsList = newsMapper.searchByKeyword(keyword, offset, pageSize);
        int total = newsMapper.countByKeyword(keyword);
        
        // 转换为DTO
        List<NewsDTO> newsDTOList = newsList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("list", newsDTOList);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        result.put("totalPages", (int) Math.ceil((double) total / pageSize));
        
        logger.info("搜索新闻完成 - 关键词: {}, 总数: {}", keyword, total);
        return result;
    }
    
    /**
     * 获取推荐新闻
     * 
     * @param limit 数量限制
     * @return 推荐新闻列表
     */
    public List<NewsDTO> getFeaturedNews(int limit) {
        logger.info("获取推荐新闻 - 数量限制: {}", limit);
        
        if (limit < 1 || limit > 50) limit = 10;
        
        List<News> newsList = newsMapper.selectFeatured(limit);
        List<NewsDTO> newsDTOList = newsList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        logger.info("获取推荐新闻完成 - 数量: {}", newsDTOList.size());
        return newsDTOList;
    }
    
    /**
     * 获取热门新闻
     * 
     * @param limit 数量限制
     * @return 热门新闻列表
     */
    public List<NewsDTO> getHotNews(int limit) {
        logger.info("获取热门新闻 - 数量限制: {}", limit);
        
        if (limit < 1 || limit > 50) limit = 10;
        
        List<News> newsList = newsMapper.selectHotNews(limit);
        List<NewsDTO> newsDTOList = newsList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        logger.info("获取热门新闻完成 - 数量: {}", newsDTOList.size());
        return newsDTOList;
    }
    
    /**
     * 获取最新发布的新闻
     * 
     * @param limit 数量限制
     * @return 最新新闻列表
     */
    public List<NewsDTO> getLatestNews(int limit) {
        logger.info("获取最新新闻 - 数量限制: {}", limit);
        
        if (limit < 1 || limit > 50) limit = 10;
        
        List<News> newsList = newsMapper.selectLatestPublished(limit);
        List<NewsDTO> newsDTOList = newsList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        logger.info("获取最新新闻完成 - 数量: {}", newsDTOList.size());
        return newsDTOList;
    }
    
    /**
     * 获取相关新闻
     * 
     * @param newsId 新闻ID
     * @param limit 数量限制
     * @return 相关新闻列表
     */
    public List<NewsDTO> getRelatedNews(Long newsId, int limit) {
        logger.info("获取相关新闻 - 新闻ID: {}, 数量限制: {}", newsId, limit);
        
        if (newsId == null) {
            throw new NewsValidationException("newsId", newsId, "新闻ID不能为空");
        }
        
        if (limit < 1 || limit > 20) limit = 5;
        
        // 获取新闻信息
        News news = newsMapper.selectById(newsId);
        if (news == null) {
            throw new NewsNotFoundException(newsId);
        }
        
        // 获取相关新闻（同分类）
        List<News> newsList = newsMapper.selectRelatedNews(newsId, news.getCategoryId(), limit);
        List<NewsDTO> newsDTOList = newsList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        logger.info("获取相关新闻完成 - 数量: {}", newsDTOList.size());
        return newsDTOList;
    }
    
    /**
     * 增加新闻浏览量
     * 
     * @param id 新闻ID
     */
    public void incrementViews(Long id) {
        logger.debug("增加新闻浏览量 - ID: {}", id);
        
        if (id == null) {
            throw new NewsValidationException("id", id, "新闻ID不能为空");
        }
        
        int result = newsMapper.incrementViews(id);
        if (result <= 0) {
            logger.warn("增加新闻浏览量失败 - ID: {}", id);
        }
    }
    
    /**
     * 验证新闻表单数据
     * 
     * @param newsFormDTO 新闻表单数据
     */
    private void validateNewsForm(NewsFormDTO newsFormDTO) {
        if (newsFormDTO == null) {
            throw new NewsValidationException("新闻数据不能为空");
        }
        
        // 使用专门的验证器进行详细验证
        newsValidator.validateNewsForm(newsFormDTO);
        
        // 验证分类是否存在
        if (newsFormDTO.getCategoryId() != null) {
            NewsCategory category = newsCategoryMapper.selectById(newsFormDTO.getCategoryId());
            if (category == null) {
                throw new NewsValidationException("categoryId", newsFormDTO.getCategoryId(), "新闻分类不存在");
            }
        }
        
        // 验证标签是否存在
        if (newsFormDTO.getTagIds() != null && !newsFormDTO.getTagIds().isEmpty()) {
            for (Long tagId : newsFormDTO.getTagIds()) {
                NewsTag tag = newsTagMapper.selectById(tagId);
                if (tag == null) {
                    throw new NewsValidationException("tagIds", tagId, "标签不存在，ID: " + tagId);
                }
            }
        }
        
        // 验证封面图片URL长度
        if (StringUtils.hasText(newsFormDTO.getCoverImage())) {
            if (newsFormDTO.getCoverImage().length() > 500) {
                throw new NewsValidationException("coverImage", newsFormDTO.getCoverImage(), "封面图片URL长度不能超过500个字符");
            }
        }
    }
    
    /**
     * 将News实体转换为NewsDTO
     * 
     * @param news News实体
     * @return NewsDTO
     */
    private NewsDTO convertToDTO(News news) {
        if (news == null) {
            return null;
        }
        
        NewsDTO dto = new NewsDTO();
        BeanUtils.copyProperties(news, dto);
        
        // 设置分类信息
        if (news.getCategory() != null) {
            dto.setCategoryName(news.getCategory().getName());
        }
        
        // 设置标签信息
        if (news.getTags() != null) {
            List<NewsTagDTO> tagDTOs = news.getTags().stream()
                    .map(tag -> {
                        NewsTagDTO tagDTO = new NewsTagDTO();
                        BeanUtils.copyProperties(tag, tagDTO);
                        return tagDTO;
                    })
                    .collect(Collectors.toList());
            dto.setTags(tagDTOs);
        }
        
        return dto;
    }
    
    /**
     * 将NewsFormDTO转换为News实体
     * 
     * @param newsFormDTO NewsFormDTO
     * @return News实体
     */
    private News convertFromFormDTO(NewsFormDTO newsFormDTO) {
        if (newsFormDTO == null) {
            return null;
        }
        
        News news = new News();
        BeanUtils.copyProperties(newsFormDTO, news);
        
        // 设置默认值
        if (news.getStatus() == null) {
            news.setStatus(NewsStatus.DRAFT);
        }
        if (news.getViews() == null) {
            news.setViews(0);
        }
        if (news.getComments() == null) {
            news.setComments(0);
        }
        if (news.getLikes() == null) {
            news.setLikes(0);
        }
        if (news.getIsFeatured() == null) {
            news.setIsFeatured(false);
        }
        if (news.getSortOrder() == null) {
            news.setSortOrder(0);
        }
        if (news.getIsDeleted() == null) {
            news.setIsDeleted(false);
        }
        
        return news;
    }
}