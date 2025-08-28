package com.yxrobot.controller;

import com.yxrobot.dto.NewsDTO;
import com.yxrobot.dto.NewsFormDTO;
import com.yxrobot.dto.NewsCategoryDTO;
import com.yxrobot.dto.NewsTagDTO;
import com.yxrobot.dto.NewsStatsDTO;
import com.yxrobot.entity.NewsStatus;
import com.yxrobot.service.NewsService;
import com.yxrobot.service.NewsCategoryService;
import com.yxrobot.service.NewsTagService;
import com.yxrobot.service.NewsStatsService;
import com.yxrobot.service.NewsFileUploadService;
import com.yxrobot.service.NewsInteractionService;
import com.yxrobot.service.NewsSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 新闻管理控制器
 * 处理新闻相关的HTTP请求
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@RestController
@RequestMapping("/api/admin/news")
@Validated
public class NewsController {
    
    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);
    
    @Autowired
    private NewsService newsService;
    
    @Autowired
    private NewsCategoryService newsCategoryService;
    
    @Autowired
    private NewsTagService newsTagService;
    
    @Autowired
    private NewsStatsService newsStatsService;
    
    @Autowired
    private NewsFileUploadService newsFileUploadService;
    
    @Autowired
    private NewsInteractionService newsInteractionService;
    
    @Autowired
    private NewsSearchService newsSearchService;
    
    /**
     * 分页查询新闻列表
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
    @GetMapping
    public ResponseEntity<Map<String, Object>> getNewsList(
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "10") @Min(1) int pageSize,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) NewsStatus status,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isFeatured) {
        
        logger.info("查询新闻列表 - 页码: {}, 每页大小: {}, 分类ID: {}, 状态: {}, 作者: {}, 关键词: {}, 是否推荐: {}", 
                   page, pageSize, categoryId, status, author, keyword, isFeatured);
        
        try {
            Map<String, Object> result = newsService.getNewsList(page, pageSize, categoryId, status, author, keyword, isFeatured);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", result);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("查询新闻列表失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 根据ID获取新闻详情
     * 
     * @param id 新闻ID
     * @return 新闻详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getNewsById(@PathVariable @NotNull Long id) {
        logger.info("查询新闻详情 - ID: {}", id);
        
        try {
            NewsDTO news = newsService.getNewsById(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", news);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("查询新闻详情失败 - ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 404);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(404).body(errorResponse);
        } catch (Exception e) {
            logger.error("查询新闻详情失败 - ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 创建新闻
     * 
     * @param newsFormDTO 新闻表单数据
     * @return 创建的新闻信息
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createNews(@Valid @RequestBody NewsFormDTO newsFormDTO) {
        logger.info("创建新闻 - 标题: {}", newsFormDTO.getTitle());
        
        try {
            NewsDTO news = newsService.createNews(newsFormDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "创建成功");
            response.put("data", news);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("创建新闻失败 - 参数错误: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 400);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        } catch (Exception e) {
            logger.error("创建新闻失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "创建失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 更新新闻
     * 
     * @param id 新闻ID
     * @param newsFormDTO 新闻表单数据
     * @return 更新后的新闻信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateNews(
            @PathVariable @NotNull Long id, 
            @Valid @RequestBody NewsFormDTO newsFormDTO) {
        
        logger.info("更新新闻 - ID: {}, 标题: {}", id, newsFormDTO.getTitle());
        
        try {
            NewsDTO news = newsService.updateNews(id, newsFormDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "更新成功");
            response.put("data", news);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("更新新闻失败 - 参数错误: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 400);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        } catch (RuntimeException e) {
            logger.error("更新新闻失败 - ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 404);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(404).body(errorResponse);
        } catch (Exception e) {
            logger.error("更新新闻失败 - ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "更新失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 删除新闻
     * 
     * @param id 新闻ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteNews(@PathVariable @NotNull Long id) {
        logger.info("删除新闻 - ID: {}", id);
        
        try {
            newsService.deleteNews(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "删除成功");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("删除新闻失败 - 参数错误: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 400);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        } catch (RuntimeException e) {
            logger.error("删除新闻失败 - ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 404);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(404).body(errorResponse);
        } catch (Exception e) {
            logger.error("删除新闻失败 - ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "删除失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 批量删除新闻
     * 
     * @param ids 新闻ID列表
     * @return 删除结果
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Map<String, Object>> batchDeleteNews(@RequestBody List<Long> ids) {
        logger.info("批量删除新闻 - 数量: {}", ids.size());
        
        try {
            newsService.batchDeleteNews(ids);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "批量删除成功");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("批量删除新闻失败 - 参数错误: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 400);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        } catch (Exception e) {
            logger.error("批量删除新闻失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "批量删除失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 搜索新闻
     * 
     * @param keyword 搜索关键词
     * @param page 页码
     * @param pageSize 每页大小
     * @return 搜索结果
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchNews(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "10") @Min(1) int pageSize) {
        
        logger.info("搜索新闻 - 关键词: {}, 页码: {}, 每页大小: {}", keyword, page, pageSize);
        
        try {
            Map<String, Object> result = newsService.searchNews(keyword, page, pageSize);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "搜索成功");
            response.put("data", result);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("搜索新闻失败 - 参数错误: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 400);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        } catch (Exception e) {
            logger.error("搜索新闻失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "搜索失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 获取推荐新闻
     * 
     * @param limit 数量限制
     * @return 推荐新闻列表
     */
    @GetMapping("/featured")
    public ResponseEntity<Map<String, Object>> getFeaturedNews(
            @RequestParam(defaultValue = "10") @Min(1) int limit) {
        
        logger.info("获取推荐新闻 - 数量限制: {}", limit);
        
        try {
            List<NewsDTO> newsList = newsService.getFeaturedNews(limit);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", newsList);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取推荐新闻失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 获取热门新闻
     * 
     * @param limit 数量限制
     * @return 热门新闻列表
     */
    @GetMapping("/hot")
    public ResponseEntity<Map<String, Object>> getHotNews(
            @RequestParam(defaultValue = "10") @Min(1) int limit) {
        
        logger.info("获取热门新闻 - 数量限制: {}", limit);
        
        try {
            List<NewsDTO> newsList = newsService.getHotNews(limit);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", newsList);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取热门新闻失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 获取最新新闻
     * 
     * @param limit 数量限制
     * @return 最新新闻列表
     */
    @GetMapping("/latest")
    public ResponseEntity<Map<String, Object>> getLatestNews(
            @RequestParam(defaultValue = "10") @Min(1) int limit) {
        
        logger.info("获取最新新闻 - 数量限制: {}", limit);
        
        try {
            List<NewsDTO> newsList = newsService.getLatestNews(limit);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", newsList);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取最新新闻失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 获取相关新闻
     * 
     * @param id 新闻ID
     * @param limit 数量限制
     * @return 相关新闻列表
     */
    @GetMapping("/{id}/related")
    public ResponseEntity<Map<String, Object>> getRelatedNews(
            @PathVariable @NotNull Long id,
            @RequestParam(defaultValue = "5") @Min(1) int limit) {
        
        logger.info("获取相关新闻 - 新闻ID: {}, 数量限制: {}", id, limit);
        
        try {
            List<NewsDTO> newsList = newsService.getRelatedNews(id, limit);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", newsList);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("获取相关新闻失败 - 参数错误: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 400);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        } catch (RuntimeException e) {
            logger.error("获取相关新闻失败 - 新闻ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 404);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(404).body(errorResponse);
        } catch (Exception e) {
            logger.error("获取相关新闻失败 - 新闻ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 记录新闻浏览
     * 
     * @param id 新闻ID
     * @param request HTTP请求对象
     * @return 操作结果
     */
    @PostMapping("/{id}/view")
    public ResponseEntity<Map<String, Object>> recordView(@PathVariable @NotNull Long id, HttpServletRequest request) {
        logger.debug("记录新闻浏览 - ID: {}", id);
        
        try {
            String ipAddress = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            
            newsInteractionService.recordView(id, null, ipAddress, userAgent);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "记录成功");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("记录新闻浏览失败 - 参数错误: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 400);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        } catch (Exception e) {
            logger.error("记录新闻浏览失败 - ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "记录失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 记录新闻点赞
     * 
     * @param id 新闻ID
     * @param request HTTP请求对象
     * @return 操作结果
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> recordLike(@PathVariable @NotNull Long id, HttpServletRequest request) {
        logger.info("记录新闻点赞 - ID: {}", id);
        
        try {
            String ipAddress = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            
            newsInteractionService.recordLike(id, null, ipAddress, userAgent);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "点赞成功");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("记录新闻点赞失败 - 参数错误: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 400);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        } catch (Exception e) {
            logger.error("记录新闻点赞失败 - ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "点赞失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 记录新闻收藏
     * 
     * @param id 新闻ID
     * @param request HTTP请求对象
     * @return 操作结果
     */
    @PostMapping("/{id}/collect")
    public ResponseEntity<Map<String, Object>> recordCollect(@PathVariable @NotNull Long id, HttpServletRequest request) {
        logger.info("记录新闻收藏 - ID: {}", id);
        
        try {
            String ipAddress = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            
            // 使用分享类型来记录收藏（可以根据需要调整）
            newsInteractionService.recordShare(id, null, ipAddress, userAgent);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "收藏成功");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("记录新闻收藏失败 - 参数错误: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 400);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        } catch (Exception e) {
            logger.error("记录新闻收藏失败 - ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "收藏失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 记录新闻分享
     * 
     * @param id 新闻ID
     * @param request HTTP请求对象
     * @return 操作结果
     */
    @PostMapping("/{id}/share")
    public ResponseEntity<Map<String, Object>> recordShare(@PathVariable @NotNull Long id, HttpServletRequest request) {
        logger.info("记录新闻分享 - ID: {}", id);
        
        try {
            String ipAddress = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            
            newsInteractionService.recordShare(id, null, ipAddress, userAgent);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "分享记录成功");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("记录新闻分享失败 - 参数错误: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 400);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        } catch (Exception e) {
            logger.error("记录新闻分享失败 - ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "分享记录失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 获取新闻互动统计
     * 
     * @param id 新闻ID
     * @return 互动统计数据
     */
    @GetMapping("/{id}/interaction-stats")
    public ResponseEntity<Map<String, Object>> getInteractionStats(@PathVariable @NotNull Long id) {
        logger.info("获取新闻互动统计 - ID: {}", id);
        
        try {
            Map<String, Object> stats = newsInteractionService.getInteractionStats(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", stats);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("获取新闻互动统计失败 - 参数错误: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 400);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        } catch (Exception e) {
            logger.error("获取新闻互动统计失败 - ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 获取新闻分类列表
     * 
     * @return 分类列表
     */
    @GetMapping("/category-list")
    public ResponseEntity<Map<String, Object>> getNewsCategories() {
        logger.info("获取新闻分类列表");
        
        try {
            List<NewsCategoryDTO> categories = newsCategoryService.getAllEnabledCategories();
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", categories);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取新闻分类列表失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 获取新闻标签列表
     * 
     * @return 标签列表
     */
    @GetMapping("/tag-list")
    public ResponseEntity<Map<String, Object>> getNewsTags() {
        logger.info("获取新闻标签列表");
        
        try {
            List<NewsTagDTO> tags = newsTagService.getAllTags();
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", tags);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取新闻标签列表失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 获取新闻统计数据
     * 
     * @return 统计数据
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getNewsStats() {
        logger.info("获取新闻统计数据");
        
        try {
            NewsStatsDTO stats = newsStatsService.getNewsStats();
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", stats);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取新闻统计数据失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 上传新闻图片
     * 
     * @param file 上传的图片文件
     * @return 上传结果
     */
    @PostMapping("/upload-image")
    public ResponseEntity<Map<String, Object>> uploadNewsImage(@RequestParam("file") MultipartFile file) {
        logger.info("上传新闻图片 - 文件名: {}, 大小: {} bytes", file.getOriginalFilename(), file.getSize());
        
        try {
            Map<String, Object> uploadResult = newsFileUploadService.uploadNewsImage(file);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "上传成功");
            response.put("data", uploadResult);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("上传新闻图片失败 - 参数错误: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 400);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        } catch (Exception e) {
            logger.error("上传新闻图片失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "上传失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 批量上传新闻图片
     * 
     * @param files 上传的图片文件列表
     * @return 上传结果
     */
    @PostMapping("/upload-images")
    public ResponseEntity<Map<String, Object>> batchUploadNewsImages(@RequestParam("files") List<MultipartFile> files) {
        logger.info("批量上传新闻图片 - 数量: {}", files.size());
        
        try {
            List<Map<String, Object>> uploadResults = newsFileUploadService.batchUploadNewsImages(files);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "批量上传成功");
            response.put("data", uploadResults);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("批量上传新闻图片失败 - 参数错误: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 400);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        } catch (Exception e) {
            logger.error("批量上传新闻图片失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "批量上传失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 获取客户端真实IP地址
     * 
     * @param request HTTP请求对象
     * @return 客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    /**
     * 高级搜索新闻
     * 
     * @param keyword 关键词
     * @param title 标题
     * @param author 作者
     * @param status 状态
     * @param categoryId 分类ID
     * @param isFeatured 是否推荐
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param page 页码
     * @param pageSize 每页大小
     * @return 搜索结果
     */
    @GetMapping("/advanced-search")
    public ResponseEntity<Map<String, Object>> advancedSearch(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) NewsStatus status,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean isFeatured,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "10") @Min(1) int pageSize) {
        
        logger.info("高级搜索新闻 - 关键词: {}, 标题: {}, 作者: {}, 状态: {}, 分类ID: {}, 是否推荐: {}", 
                   keyword, title, author, status, categoryId, isFeatured);
        
        try {
            NewsSearchService.NewsSearchCriteria criteria = new NewsSearchService.NewsSearchCriteria();
            criteria.setKeyword(keyword);
            criteria.setTitle(title);
            criteria.setAuthor(author);
            criteria.setStatus(status);
            criteria.setCategoryId(categoryId);
            criteria.setIsFeatured(isFeatured);
            criteria.setStartDate(startDate);
            criteria.setEndDate(endDate);
            criteria.setPage(page);
            criteria.setPageSize(pageSize);
            
            Map<String, Object> result = newsSearchService.advancedSearch(criteria);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "搜索成功");
            response.put("data", result);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("高级搜索新闻失败 - 参数错误: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 400);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        } catch (Exception e) {
            logger.error("高级搜索新闻失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "搜索失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 按分类筛选新闻
     * 
     * @param categoryId 分类ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 筛选结果
     */
    @GetMapping("/filter/category/{categoryId}")
    public ResponseEntity<Map<String, Object>> filterByCategory(
            @PathVariable @NotNull Long categoryId,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "10") @Min(1) int pageSize) {
        
        logger.info("按分类筛选新闻 - 分类ID: {}, 页码: {}, 每页大小: {}", categoryId, page, pageSize);
        
        try {
            Map<String, Object> result = newsSearchService.filterByCategory(categoryId, page, pageSize, null);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "筛选成功");
            response.put("data", result);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("按分类筛选新闻失败 - 参数错误: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 400);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        } catch (Exception e) {
            logger.error("按分类筛选新闻失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "筛选失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 按状态筛选新闻
     * 
     * @param status 新闻状态
     * @param page 页码
     * @param pageSize 每页大小
     * @return 筛选结果
     */
    @GetMapping("/filter/status/{status}")
    public ResponseEntity<Map<String, Object>> filterByStatus(
            @PathVariable @NotNull NewsStatus status,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "10") @Min(1) int pageSize) {
        
        logger.info("按状态筛选新闻 - 状态: {}, 页码: {}, 每页大小: {}", status, page, pageSize);
        
        try {
            Map<String, Object> result = newsSearchService.filterByStatus(status, page, pageSize, null);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "筛选成功");
            response.put("data", result);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("按状态筛选新闻失败 - 参数错误: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 400);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        } catch (Exception e) {
            logger.error("按状态筛选新闻失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "筛选失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 按作者筛选新闻
     * 
     * @param author 作者
     * @param page 页码
     * @param pageSize 每页大小
     * @return 筛选结果
     */
    @GetMapping("/filter/author/{author}")
    public ResponseEntity<Map<String, Object>> filterByAuthor(
            @PathVariable @NotNull String author,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "10") @Min(1) int pageSize) {
        
        logger.info("按作者筛选新闻 - 作者: {}, 页码: {}, 每页大小: {}", author, page, pageSize);
        
        try {
            Map<String, Object> result = newsSearchService.filterByAuthor(author, page, pageSize, null);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "筛选成功");
            response.put("data", result);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("按作者筛选新闻失败 - 参数错误: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 400);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        } catch (Exception e) {
            logger.error("按作者筛选新闻失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "筛选失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 获取搜索建议
     * 
     * @param keyword 关键词
     * @param limit 建议数量限制
     * @return 搜索建议列表
     */
    @GetMapping("/search-suggestions")
    public ResponseEntity<Map<String, Object>> getSearchSuggestions(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "10") @Min(1) int limit) {
        
        logger.info("获取搜索建议 - 关键词: {}, 限制: {}", keyword, limit);
        
        try {
            List<String> suggestions = newsSearchService.getSearchSuggestions(keyword, limit);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取成功");
            response.put("data", suggestions);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取搜索建议失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "获取失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}