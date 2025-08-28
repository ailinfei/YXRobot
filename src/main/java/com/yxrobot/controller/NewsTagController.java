package com.yxrobot.controller;

import com.yxrobot.dto.NewsTagDTO;
import com.yxrobot.service.NewsTagService;
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

/**
 * 新闻标签管理控制器
 * 处理新闻标签相关的HTTP请求
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@RestController
@RequestMapping("/api/admin/news/tags")
@Validated
public class NewsTagController {
    
    private static final Logger logger = LoggerFactory.getLogger(NewsTagController.class);
    
    @Autowired
    private NewsTagService newsTagService;
    
    /**
     * 获取所有新闻标签
     * 
     * @return 标签列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTags() {
        logger.info("获取所有新闻标签");
        
        try {
            List<NewsTagDTO> tags = newsTagService.getAllTags();
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", tags);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取新闻标签失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 分页获取新闻标签
     * 
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> getTagsByPage(
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "10") @Min(1) int pageSize) {
        
        logger.info("分页获取新闻标签 - 页码: {}, 每页大小: {}", page, pageSize);
        
        try {
            Map<String, Object> result = newsTagService.getTagsByPage(page, pageSize);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", result);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("分页获取新闻标签失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 获取热门标签
     * 
     * @param limit 数量限制
     * @return 热门标签列表
     */
    @GetMapping("/popular")
    public ResponseEntity<Map<String, Object>> getPopularTags(
            @RequestParam(defaultValue = "10") @Min(1) int limit) {
        
        logger.info("获取热门标签 - 数量限制: {}", limit);
        
        try {
            List<NewsTagDTO> tags = newsTagService.getPopularTags(limit);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", tags);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取热门标签失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 搜索标签
     * 
     * @param name 标签名称（支持模糊搜索）
     * @return 标签列表
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchTags(@RequestParam String name) {
        logger.info("搜索标签 - 名称: {}", name);
        
        try {
            List<NewsTagDTO> tags = newsTagService.searchTagsByName(name);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "搜索成功");
            response.put("data", tags);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("搜索标签失败 - 参数错误: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 400);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        } catch (Exception e) {
            logger.error("搜索标签失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "搜索失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 根据ID获取标签
     * 
     * @param id 标签ID
     * @return 标签信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getTagById(@PathVariable @NotNull Long id) {
        logger.info("获取标签 - ID: {}", id);
        
        try {
            NewsTagDTO tag = newsTagService.getTagById(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", tag);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("获取标签失败 - ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 404);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(404).body(errorResponse);
        } catch (Exception e) {
            logger.error("获取标签失败 - ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 创建标签
     * 
     * @param request 标签创建请求
     * @return 创建的标签信息
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createTag(@Valid @RequestBody TagRequest request) {
        logger.info("创建标签 - 名称: {}, 颜色: {}", request.getName(), request.getColor());
        
        try {
            NewsTagDTO tag = newsTagService.createTag(request.getName(), request.getColor());
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "创建成功");
            response.put("data", tag);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("创建标签失败 - 参数错误: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 400);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        } catch (Exception e) {
            logger.error("创建标签失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "创建失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 更新标签
     * 
     * @param id 标签ID
     * @param request 标签更新请求
     * @return 更新后的标签信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateTag(
            @PathVariable @NotNull Long id, 
            @Valid @RequestBody TagRequest request) {
        
        logger.info("更新标签 - ID: {}, 名称: {}, 颜色: {}", id, request.getName(), request.getColor());
        
        try {
            NewsTagDTO tag = newsTagService.updateTag(id, request.getName(), request.getColor());
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "更新成功");
            response.put("data", tag);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("更新标签失败 - 参数错误: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 400);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        } catch (RuntimeException e) {
            logger.error("更新标签失败 - ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 404);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(404).body(errorResponse);
        } catch (Exception e) {
            logger.error("更新标签失败 - ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "更新失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 删除标签
     * 
     * @param id 标签ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteTag(@PathVariable @NotNull Long id) {
        logger.info("删除标签 - ID: {}", id);
        
        try {
            newsTagService.deleteTag(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "删除成功");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("删除标签失败 - 参数错误: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 400);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        } catch (RuntimeException e) {
            logger.error("删除标签失败 - ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 404);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(404).body(errorResponse);
        } catch (Exception e) {
            logger.error("删除标签失败 - ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "删除失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 批量删除标签
     * 
     * @param ids 标签ID列表
     * @return 删除结果
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Map<String, Object>> batchDeleteTags(@RequestBody List<Long> ids) {
        logger.info("批量删除标签 - 数量: {}", ids.size());
        
        try {
            newsTagService.batchDeleteTags(ids);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "批量删除成功");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("批量删除标签失败 - 参数错误: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 400);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        } catch (Exception e) {
            logger.error("批量删除标签失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "批量删除失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 获取标签统计信息
     * 
     * @return 统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getTagStats() {
        logger.info("获取标签统计信息");
        
        try {
            Map<String, Object> stats = newsTagService.getTagStats();
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", stats);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取标签统计信息失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 标签请求类
     */
    public static class TagRequest {
        @NotNull(message = "标签名称不能为空")
        private String name;
        private String color;
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getColor() {
            return color;
        }
        
        public void setColor(String color) {
            this.color = color;
        }
    }
}