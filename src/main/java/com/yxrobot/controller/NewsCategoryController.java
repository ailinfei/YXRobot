package com.yxrobot.controller;

import com.yxrobot.dto.NewsCategoryDTO;
import com.yxrobot.service.NewsCategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 新闻分类管理控制器
 * 处理新闻分类相关的HTTP请求
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@RestController
@RequestMapping("/api/admin/news/categories")
@Validated
public class NewsCategoryController {
    
    private static final Logger logger = LoggerFactory.getLogger(NewsCategoryController.class);
    
    @Autowired
    private NewsCategoryService newsCategoryService;
    
    /**
     * 获取所有启用的新闻分类
     * 
     * @return 分类列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllEnabledCategories() {
        logger.info("获取所有启用的新闻分类");
        
        try {
            List<NewsCategoryDTO> categories = newsCategoryService.getAllEnabledCategories();
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", categories);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取新闻分类失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 获取所有新闻分类（包括禁用的）
     * 
     * @return 分类列表
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllCategories() {
        logger.info("获取所有新闻分类");
        
        try {
            List<NewsCategoryDTO> categories = newsCategoryService.getAllCategories();
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", categories);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取所有新闻分类失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 根据ID获取新闻分类
     * 
     * @param id 分类ID
     * @return 分类信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCategoryById(@PathVariable @NotNull Long id) {
        logger.info("获取新闻分类 - ID: {}", id);
        
        try {
            NewsCategoryDTO category = newsCategoryService.getCategoryById(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", category);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("获取新闻分类失败 - ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 404);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(404).body(errorResponse);
        } catch (Exception e) {
            logger.error("获取新闻分类失败 - ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 创建新闻分类
     * 
     * @param request 分类创建请求
     * @return 创建的分类信息
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createCategory(@Valid @RequestBody CategoryRequest request) {
        logger.info("创建新闻分类 - 名称: {}", request.getName());
        
        try {
            NewsCategoryDTO category = newsCategoryService.createCategory(
                request.getName(), 
                request.getDescription(), 
                request.getSortOrder()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "创建成功");
            response.put("data", category);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("创建新闻分类失败 - 参数错误: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 400);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        } catch (Exception e) {
            logger.error("创建新闻分类失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "创建失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 更新新闻分类
     * 
     * @param id 分类ID
     * @param request 分类更新请求
     * @return 更新后的分类信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateCategory(
            @PathVariable @NotNull Long id, 
            @Valid @RequestBody CategoryRequest request) {
        
        logger.info("更新新闻分类 - ID: {}, 名称: {}", id, request.getName());
        
        try {
            NewsCategoryDTO category = newsCategoryService.updateCategory(
                id,
                request.getName(), 
                request.getDescription(), 
                request.getSortOrder(),
                request.getIsEnabled()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "更新成功");
            response.put("data", category);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("更新新闻分类失败 - 参数错误: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 400);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        } catch (RuntimeException e) {
            logger.error("更新新闻分类失败 - ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 404);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(404).body(errorResponse);
        } catch (Exception e) {
            logger.error("更新新闻分类失败 - ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "更新失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 删除新闻分类
     * 
     * @param id 分类ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCategory(@PathVariable @NotNull Long id) {
        logger.info("删除新闻分类 - ID: {}", id);
        
        try {
            newsCategoryService.deleteCategory(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "删除成功");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("删除新闻分类失败 - 参数错误: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 400);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        } catch (RuntimeException e) {
            logger.error("删除新闻分类失败 - ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 404);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(404).body(errorResponse);
        } catch (Exception e) {
            logger.error("删除新闻分类失败 - ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "删除失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 启用/禁用分类
     * 
     * @param id 分类ID
     * @param request 状态更新请求
     * @return 操作结果
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateCategoryStatus(
            @PathVariable @NotNull Long id, 
            @RequestBody StatusRequest request) {
        
        logger.info("更新新闻分类状态 - ID: {}, 启用: {}", id, request.getIsEnabled());
        
        try {
            newsCategoryService.updateCategoryStatus(id, request.getIsEnabled());
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "状态更新成功");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("更新新闻分类状态失败 - 参数错误: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 400);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        } catch (RuntimeException e) {
            logger.error("更新新闻分类状态失败 - ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 404);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(404).body(errorResponse);
        } catch (Exception e) {
            logger.error("更新新闻分类状态失败 - ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "状态更新失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 获取分类统计信息
     * 
     * @return 统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getCategoryStats() {
        logger.info("获取分类统计信息");
        
        try {
            Map<String, Object> stats = newsCategoryService.getCategoryStats();
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", stats);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取分类统计信息失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 分类请求类
     */
    public static class CategoryRequest {
        @NotNull(message = "分类名称不能为空")
        private String name;
        private String description;
        private Integer sortOrder;
        private Boolean isEnabled;
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public Integer getSortOrder() {
            return sortOrder;
        }
        
        public void setSortOrder(Integer sortOrder) {
            this.sortOrder = sortOrder;
        }
        
        public Boolean getIsEnabled() {
            return isEnabled;
        }
        
        public void setIsEnabled(Boolean isEnabled) {
            this.isEnabled = isEnabled;
        }
    }
    
    /**
     * 状态请求类
     */
    public static class StatusRequest {
        @NotNull(message = "启用状态不能为空")
        private Boolean isEnabled;
        
        public Boolean getIsEnabled() {
            return isEnabled;
        }
        
        public void setIsEnabled(Boolean isEnabled) {
            this.isEnabled = isEnabled;
        }
    }
}