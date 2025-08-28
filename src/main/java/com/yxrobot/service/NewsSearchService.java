package com.yxrobot.service;

import com.yxrobot.dto.NewsDTO;
import com.yxrobot.entity.News;
import com.yxrobot.entity.NewsStatus;
import com.yxrobot.exception.NewsValidationException;
import com.yxrobot.mapper.NewsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 新闻搜索服务类
 * 提供高级搜索和筛选功能
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@Service
public class NewsSearchService {
    
    private static final Logger logger = LoggerFactory.getLogger(NewsSearchService.class);
    
    @Autowired
    private NewsMapper newsMapper;
    
    @Autowired
    private NewsService newsService;
    
    /**
     * 高级搜索新闻
     * 
     * @param searchCriteria 搜索条件
     * @return 搜索结果
     */
    public Map<String, Object> advancedSearch(NewsSearchCriteria searchCriteria) {
        logger.info("高级搜索新闻 - 条件: {}", searchCriteria);
        
        validateSearchCriteria(searchCriteria);
        
        // 构建查询条件
        Map<String, Object> conditions = buildSearchConditions(searchCriteria);
        
        int offset = (searchCriteria.getPage() - 1) * searchCriteria.getPageSize();
        
        // 执行搜索
        List<News> newsList = newsMapper.selectByConditions(conditions, offset, searchCriteria.getPageSize());
        int total = newsMapper.countByConditions(conditions);
        
        // 转换为DTO
        List<NewsDTO> newsDTOList = newsList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("list", newsDTOList);
        result.put("total", total);
        result.put("page", searchCriteria.getPage());
        result.put("pageSize", searchCriteria.getPageSize());
        result.put("totalPages", (int) Math.ceil((double) total / searchCriteria.getPageSize()));
        result.put("searchCriteria", searchCriteria);
        
        logger.info("高级搜索新闻完成 - 总数: {}", total);
        return result;
    }
    
    /**
     * 全文搜索新闻
     * 
     * @param keyword 关键词
     * @param page 页码
     * @param pageSize 每页大小
     * @param filters 额外筛选条件
     * @return 搜索结果
     */
    public Map<String, Object> fullTextSearch(String keyword, int page, int pageSize, Map<String, Object> filters) {
        logger.info("全文搜索新闻 - 关键词: {}, 页码: {}, 每页大小: {}", keyword, page, pageSize);
        
        if (!StringUtils.hasText(keyword)) {
            throw new NewsValidationException("keyword", keyword, "搜索关键词不能为空");
        }
        
        // 参数验证
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 10;
        
        // 构建搜索条件
        NewsSearchCriteria criteria = new NewsSearchCriteria();
        criteria.setKeyword(keyword);
        criteria.setPage(page);
        criteria.setPageSize(pageSize);
        
        // 应用额外筛选条件
        if (filters != null) {
            applyFilters(criteria, filters);
        }
        
        return advancedSearch(criteria);
    }
    
    /**
     * 按分类筛选新闻
     * 
     * @param categoryId 分类ID
     * @param page 页码
     * @param pageSize 每页大小
     * @param additionalFilters 额外筛选条件
     * @return 筛选结果
     */
    public Map<String, Object> filterByCategory(Long categoryId, int page, int pageSize, Map<String, Object> additionalFilters) {
        logger.info("按分类筛选新闻 - 分类ID: {}, 页码: {}, 每页大小: {}", categoryId, page, pageSize);
        
        if (categoryId == null) {
            throw new NewsValidationException("categoryId", categoryId, "分类ID不能为空");
        }
        
        NewsSearchCriteria criteria = new NewsSearchCriteria();
        criteria.setCategoryId(categoryId);
        criteria.setPage(page);
        criteria.setPageSize(pageSize);
        
        // 应用额外筛选条件
        if (additionalFilters != null) {
            applyFilters(criteria, additionalFilters);
        }
        
        return advancedSearch(criteria);
    }
    
    /**
     * 按状态筛选新闻
     * 
     * @param status 新闻状态
     * @param page 页码
     * @param pageSize 每页大小
     * @param additionalFilters 额外筛选条件
     * @return 筛选结果
     */
    public Map<String, Object> filterByStatus(NewsStatus status, int page, int pageSize, Map<String, Object> additionalFilters) {
        logger.info("按状态筛选新闻 - 状态: {}, 页码: {}, 每页大小: {}", status, page, pageSize);
        
        if (status == null) {
            throw new NewsValidationException("status", status, "新闻状态不能为空");
        }
        
        NewsSearchCriteria criteria = new NewsSearchCriteria();
        criteria.setStatus(status);
        criteria.setPage(page);
        criteria.setPageSize(pageSize);
        
        // 应用额外筛选条件
        if (additionalFilters != null) {
            applyFilters(criteria, additionalFilters);
        }
        
        return advancedSearch(criteria);
    }
    
    /**
     * 按作者筛选新闻
     * 
     * @param author 作者
     * @param page 页码
     * @param pageSize 每页大小
     * @param additionalFilters 额外筛选条件
     * @return 筛选结果
     */
    public Map<String, Object> filterByAuthor(String author, int page, int pageSize, Map<String, Object> additionalFilters) {
        logger.info("按作者筛选新闻 - 作者: {}, 页码: {}, 每页大小: {}", author, page, pageSize);
        
        if (!StringUtils.hasText(author)) {
            throw new NewsValidationException("author", author, "作者不能为空");
        }
        
        NewsSearchCriteria criteria = new NewsSearchCriteria();
        criteria.setAuthor(author);
        criteria.setPage(page);
        criteria.setPageSize(pageSize);
        
        // 应用额外筛选条件
        if (additionalFilters != null) {
            applyFilters(criteria, additionalFilters);
        }
        
        return advancedSearch(criteria);
    }
    
    /**
     * 按时间范围筛选新闻
     * 
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param page 页码
     * @param pageSize 每页大小
     * @param additionalFilters 额外筛选条件
     * @return 筛选结果
     */
    public Map<String, Object> filterByDateRange(LocalDateTime startDate, LocalDateTime endDate, 
                                                int page, int pageSize, Map<String, Object> additionalFilters) {
        logger.info("按时间范围筛选新闻 - 开始: {}, 结束: {}, 页码: {}, 每页大小: {}", startDate, endDate, page, pageSize);
        
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new NewsValidationException("dateRange", null, "开始时间不能晚于结束时间");
        }
        
        NewsSearchCriteria criteria = new NewsSearchCriteria();
        criteria.setStartDate(startDate);
        criteria.setEndDate(endDate);
        criteria.setPage(page);
        criteria.setPageSize(pageSize);
        
        // 应用额外筛选条件
        if (additionalFilters != null) {
            applyFilters(criteria, additionalFilters);
        }
        
        return advancedSearch(criteria);
    }
    
    /**
     * 获取搜索建议
     * 
     * @param keyword 关键词
     * @param limit 建议数量限制
     * @return 搜索建议列表
     */
    public List<String> getSearchSuggestions(String keyword, int limit) {
        logger.info("获取搜索建议 - 关键词: {}, 限制: {}", keyword, limit);
        
        if (!StringUtils.hasText(keyword)) {
            return List.of();
        }
        
        if (limit < 1 || limit > 20) limit = 10;
        
        // 这里可以实现基于历史搜索、热门关键词等的建议逻辑
        // 目前返回基于标题的简单建议
        List<News> newsList = newsMapper.searchByKeyword(keyword, 0, limit);
        
        return newsList.stream()
                .map(News::getTitle)
                .distinct()
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    /**
     * 验证搜索条件
     * 
     * @param criteria 搜索条件
     */
    private void validateSearchCriteria(NewsSearchCriteria criteria) {
        if (criteria == null) {
            throw new NewsValidationException("搜索条件不能为空");
        }
        
        if (criteria.getPage() < 1) {
            criteria.setPage(1);
        }
        
        if (criteria.getPageSize() < 1 || criteria.getPageSize() > 100) {
            criteria.setPageSize(10);
        }
        
        if (criteria.getStartDate() != null && criteria.getEndDate() != null) {
            if (criteria.getStartDate().isAfter(criteria.getEndDate())) {
                throw new NewsValidationException("dateRange", null, "开始时间不能晚于结束时间");
            }
        }
    }
    
    /**
     * 构建搜索条件
     * 
     * @param criteria 搜索条件
     * @return 查询条件Map
     */
    private Map<String, Object> buildSearchConditions(NewsSearchCriteria criteria) {
        Map<String, Object> conditions = new HashMap<>();
        
        if (StringUtils.hasText(criteria.getKeyword())) {
            conditions.put("keyword", criteria.getKeyword());
        }
        
        if (StringUtils.hasText(criteria.getTitle())) {
            conditions.put("title", criteria.getTitle());
        }
        
        if (StringUtils.hasText(criteria.getAuthor())) {
            conditions.put("author", criteria.getAuthor());
        }
        
        if (criteria.getStatus() != null) {
            conditions.put("status", criteria.getStatus());
        }
        
        if (criteria.getCategoryId() != null) {
            conditions.put("categoryId", criteria.getCategoryId());
        }
        
        if (criteria.getIsFeatured() != null) {
            conditions.put("isFeatured", criteria.getIsFeatured());
        }
        
        if (criteria.getStartDate() != null) {
            conditions.put("startDate", criteria.getStartDate());
        }
        
        if (criteria.getEndDate() != null) {
            conditions.put("endDate", criteria.getEndDate());
        }
        
        return conditions;
    }
    
    /**
     * 应用额外筛选条件
     * 
     * @param criteria 搜索条件
     * @param filters 筛选条件
     */
    private void applyFilters(NewsSearchCriteria criteria, Map<String, Object> filters) {
        if (filters.containsKey("status") && filters.get("status") instanceof NewsStatus) {
            criteria.setStatus((NewsStatus) filters.get("status"));
        }
        
        if (filters.containsKey("categoryId") && filters.get("categoryId") instanceof Long) {
            criteria.setCategoryId((Long) filters.get("categoryId"));
        }
        
        if (filters.containsKey("author") && filters.get("author") instanceof String) {
            criteria.setAuthor((String) filters.get("author"));
        }
        
        if (filters.containsKey("isFeatured") && filters.get("isFeatured") instanceof Boolean) {
            criteria.setIsFeatured((Boolean) filters.get("isFeatured"));
        }
        
        if (filters.containsKey("startDate") && filters.get("startDate") instanceof LocalDateTime) {
            criteria.setStartDate((LocalDateTime) filters.get("startDate"));
        }
        
        if (filters.containsKey("endDate") && filters.get("endDate") instanceof LocalDateTime) {
            criteria.setEndDate((LocalDateTime) filters.get("endDate"));
        }
    }
    
    /**
     * 新闻搜索条件类
     */
    public static class NewsSearchCriteria {
        private String keyword;
        private String title;
        private String author;
        private NewsStatus status;
        private Long categoryId;
        private Boolean isFeatured;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private int page = 1;
        private int pageSize = 10;
        
        // Getter和Setter方法
        public String getKeyword() {
            return keyword;
        }
        
        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }
        
        public String getTitle() {
            return title;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }
        
        public String getAuthor() {
            return author;
        }
        
        public void setAuthor(String author) {
            this.author = author;
        }
        
        public NewsStatus getStatus() {
            return status;
        }
        
        public void setStatus(NewsStatus status) {
            this.status = status;
        }
        
        public Long getCategoryId() {
            return categoryId;
        }
        
        public void setCategoryId(Long categoryId) {
            this.categoryId = categoryId;
        }
        
        public Boolean getIsFeatured() {
            return isFeatured;
        }
        
        public void setIsFeatured(Boolean isFeatured) {
            this.isFeatured = isFeatured;
        }
        
        public LocalDateTime getStartDate() {
            return startDate;
        }
        
        public void setStartDate(LocalDateTime startDate) {
            this.startDate = startDate;
        }
        
        public LocalDateTime getEndDate() {
            return endDate;
        }
        
        public void setEndDate(LocalDateTime endDate) {
            this.endDate = endDate;
        }
        
        public int getPage() {
            return page;
        }
        
        public void setPage(int page) {
            this.page = page;
        }
        
        public int getPageSize() {
            return pageSize;
        }
        
        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }
        
        @Override
        public String toString() {
            return "NewsSearchCriteria{" +
                    "keyword='" + keyword + '\'' +
                    ", title='" + title + '\'' +
                    ", author='" + author + '\'' +
                    ", status=" + status +
                    ", categoryId=" + categoryId +
                    ", isFeatured=" + isFeatured +
                    ", startDate=" + startDate +
                    ", endDate=" + endDate +
                    ", page=" + page +
                    ", pageSize=" + pageSize +
                    '}';
        }
    }
    
    /**
     * 将News实体转换为NewsDTO
     * 
     * @param news 新闻实体
     * @return 新闻DTO
     */
    private NewsDTO convertToDTO(News news) {
        if (news == null) {
            return null;
        }
        
        NewsDTO dto = new NewsDTO();
        dto.setId(news.getId());
        dto.setTitle(news.getTitle());
        dto.setExcerpt(news.getExcerpt());
        dto.setContent(news.getContent());
        dto.setCategoryId(news.getCategoryId());
        dto.setAuthor(news.getAuthor());
        dto.setStatus(news.getStatus());
        dto.setCoverImage(news.getCoverImage());
        dto.setPublishTime(news.getPublishTime());
        dto.setIsFeatured(news.getIsFeatured());
        dto.setSortOrder(news.getSortOrder());
        dto.setViews(news.getViews());
        dto.setLikes(news.getLikes());
        dto.setComments(news.getComments());
        dto.setCreatedAt(news.getCreatedAt());
        dto.setUpdatedAt(news.getUpdatedAt());
        
        return dto;
    }
}