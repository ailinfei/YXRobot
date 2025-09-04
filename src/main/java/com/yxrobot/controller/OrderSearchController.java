package com.yxrobot.controller;

import com.yxrobot.dto.OrderQueryDTO;
import com.yxrobot.service.OrderSearchService;
import com.yxrobot.util.SearchOptimizationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单搜索控制器
 * 提供高级搜索和筛选功能的API接口
 */
@RestController
@RequestMapping("/api/admin/orders/search")
@Validated
public class OrderSearchController {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderSearchController.class);
    
    @Autowired
    private OrderSearchService orderSearchService;
    
    /**
     * 高级搜索订单
     * POST /api/admin/orders/search/advanced
     * 支持复杂的多条件搜索和筛选
     */
    @PostMapping("/advanced")
    public ResponseEntity<Map<String, Object>> advancedSearch(@Valid @RequestBody OrderQueryDTO queryDTO) {
        try {
            logger.info("开始高级搜索订单，查询条件: {}", queryDTO);
            
            // 优化查询条件
            OrderQueryDTO optimizedQuery = SearchOptimizationUtil.optimizeQueryConditions(queryDTO);
            
            // 执行高级搜索
            OrderSearchService.AdvancedSearchResult result = orderSearchService.advancedSearch(optimizedQuery);
            
            // 构建响应数据
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("orders", result.getOrders());
            responseData.put("total", result.getTotal());
            responseData.put("statistics", result.getStatistics());
            responseData.put("suggestions", result.getSuggestions());
            responseData.put("queryTime", result.getQueryTime());
            responseData.put("queryComplexity", SearchOptimizationUtil.calculateQueryComplexity(optimizedQuery));
            
            return ResponseEntity.ok(createSuccessResponse("搜索成功", responseData));
            
        } catch (Exception e) {
            logger.error("高级搜索订单失败", e);
            return ResponseEntity.badRequest().body(createErrorResponse("搜索失败：" + e.getMessage()));
        }
    }
    
    /**
     * 快速搜索订单
     * GET /api/admin/orders/search/quick
     * 用于前端搜索框的实时搜索
     */
    @GetMapping("/quick")
    public ResponseEntity<Map<String, Object>> quickSearch(
            @RequestParam @NotBlank String keyword,
            @RequestParam(defaultValue = "10") @Positive Integer limit) {
        try {
            logger.debug("开始快速搜索订单，关键词: {}, 限制: {}", keyword, limit);
            
            // 优化关键词
            String optimizedKeyword = SearchOptimizationUtil.optimizeKeyword(keyword);
            if (optimizedKeyword == null) {
                return ResponseEntity.ok(createSuccessResponse("搜索成功", Map.of("orders", List.of(), "total", 0)));
            }
            
            // 检测搜索类型
            SearchOptimizationUtil.SearchType searchType = SearchOptimizationUtil.detectSearchType(optimizedKeyword);
            
            // 执行快速搜索
            List<com.yxrobot.dto.OrderDTO> orders = orderSearchService.quickSearch(optimizedKeyword, limit);
            
            // 生成搜索建议
            List<String> suggestions = SearchOptimizationUtil.generateSearchSuggestions(
                optimizedKeyword, searchType, orders.size());
            
            // 构建响应数据
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("orders", orders);
            responseData.put("total", orders.size());
            responseData.put("searchType", searchType.name());
            responseData.put("suggestions", suggestions);
            responseData.put("optimizedKeyword", optimizedKeyword);
            
            return ResponseEntity.ok(createSuccessResponse("搜索成功", responseData));
            
        } catch (Exception e) {
            logger.error("快速搜索订单失败", e);
            return ResponseEntity.badRequest().body(createErrorResponse("搜索失败：" + e.getMessage()));
        }
    }
    
    /**
     * 获取筛选建议
     * GET /api/admin/orders/search/filter-suggestions
     * 根据当前搜索条件提供筛选建议
     */
    @GetMapping("/filter-suggestions")
    public ResponseEntity<Map<String, Object>> getFilterSuggestions(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String paymentStatus) {
        try {
            // 构建查询条件
            OrderQueryDTO queryDTO = new OrderQueryDTO();
            queryDTO.setKeyword(keyword);
            queryDTO.setType(type);
            queryDTO.setStatus(status);
            queryDTO.setPaymentStatus(paymentStatus);
            
            // 获取筛选建议
            OrderSearchService.FilterSuggestions suggestions = orderSearchService.getFilterSuggestions(queryDTO);
            
            return ResponseEntity.ok(createSuccessResponse("获取筛选建议成功", suggestions));
            
        } catch (Exception e) {
            logger.error("获取筛选建议失败", e);
            return ResponseEntity.badRequest().body(createErrorResponse("获取筛选建议失败：" + e.getMessage()));
        }
    }
    
    /**
     * 搜索历史记录
     * GET /api/admin/orders/search/history
     * 获取用户的搜索历史
     */
    @GetMapping("/history")
    public ResponseEntity<Map<String, Object>> getSearchHistory(
            @RequestParam(defaultValue = "admin") String userId,
            @RequestParam(defaultValue = "10") @Positive Integer limit) {
        try {
            List<OrderSearchService.SearchHistory> history = orderSearchService.getSearchHistory(userId);
            
            // 限制返回数量
            if (history.size() > limit) {
                history = history.subList(0, limit);
            }
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("history", history);
            responseData.put("total", history.size());
            
            return ResponseEntity.ok(createSuccessResponse("获取搜索历史成功", responseData));
            
        } catch (Exception e) {
            logger.error("获取搜索历史失败", e);
            return ResponseEntity.badRequest().body(createErrorResponse("获取搜索历史失败：" + e.getMessage()));
        }
    }
    
    /**
     * 保存搜索历史
     * POST /api/admin/orders/search/history
     * 保存用户的搜索记录
     */
    @PostMapping("/history")
    public ResponseEntity<Map<String, Object>> saveSearchHistory(
            @RequestParam(defaultValue = "admin") String userId,
            @Valid @RequestBody OrderQueryDTO queryDTO) {
        try {
            orderSearchService.saveSearchHistory(userId, queryDTO);
            return ResponseEntity.ok(createSuccessResponse("保存搜索历史成功", null));
            
        } catch (Exception e) {
            logger.error("保存搜索历史失败", e);
            return ResponseEntity.badRequest().body(createErrorResponse("保存搜索历史失败：" + e.getMessage()));
        }
    }
    
    /**
     * 搜索性能分析
     * POST /api/admin/orders/search/analyze
     * 分析搜索查询的性能和复杂度
     */
    @PostMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyzeSearchPerformance(@Valid @RequestBody OrderQueryDTO queryDTO) {
        try {
            // 优化查询条件
            OrderQueryDTO optimizedQuery = SearchOptimizationUtil.optimizeQueryConditions(queryDTO);
            
            // 计算查询复杂度
            int complexity = SearchOptimizationUtil.calculateQueryComplexity(optimizedQuery);
            
            // 生成索引建议
            List<SearchOptimizationUtil.IndexSuggestion> indexSuggestions = 
                SearchOptimizationUtil.generateIndexSuggestions(optimizedQuery);
            
            // 检测搜索类型
            SearchOptimizationUtil.SearchType searchType = null;
            if (optimizedQuery.getKeyword() != null) {
                searchType = SearchOptimizationUtil.detectSearchType(optimizedQuery.getKeyword());
            }
            
            // 构建分析结果
            Map<String, Object> analysisResult = new HashMap<>();
            analysisResult.put("originalQuery", queryDTO);
            analysisResult.put("optimizedQuery", optimizedQuery);
            analysisResult.put("complexity", complexity);
            analysisResult.put("complexityLevel", getComplexityLevel(complexity));
            analysisResult.put("searchType", searchType != null ? searchType.name() : "UNKNOWN");
            analysisResult.put("indexSuggestions", indexSuggestions);
            analysisResult.put("performanceTips", generatePerformanceTips(complexity, searchType));
            
            return ResponseEntity.ok(createSuccessResponse("搜索性能分析完成", analysisResult));
            
        } catch (Exception e) {
            logger.error("搜索性能分析失败", e);
            return ResponseEntity.badRequest().body(createErrorResponse("搜索性能分析失败：" + e.getMessage()));
        }
    }
    
    /**
     * 搜索统计信息
     * GET /api/admin/orders/search/statistics
     * 获取搜索相关的统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getSearchStatistics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            // TODO: 实现搜索统计功能
            // 这里可以统计搜索频率、热门关键词、搜索成功率等
            
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalSearches", 0);
            statistics.put("successfulSearches", 0);
            statistics.put("averageResultCount", 0);
            statistics.put("popularKeywords", List.of());
            statistics.put("searchTypeDistribution", Map.of());
            
            return ResponseEntity.ok(createSuccessResponse("获取搜索统计成功", statistics));
            
        } catch (Exception e) {
            logger.error("获取搜索统计失败", e);
            return ResponseEntity.badRequest().body(createErrorResponse("获取搜索统计失败：" + e.getMessage()));
        }
    }
    
    /**
     * 搜索关键词建议
     * GET /api/admin/orders/search/keyword-suggestions
     * 根据输入提供关键词建议
     */
    @GetMapping("/keyword-suggestions")
    public ResponseEntity<Map<String, Object>> getKeywordSuggestions(
            @RequestParam String input,
            @RequestParam(defaultValue = "5") @Positive Integer limit) {
        try {
            // TODO: 实现关键词建议功能
            // 这里可以基于历史搜索、订单数据等提供智能建议
            
            List<String> suggestions = List.of();
            
            // 简单的建议逻辑
            if (input != null && !input.trim().isEmpty()) {
                String trimmed = input.trim().toLowerCase();
                suggestions = List.of(
                    trimmed + " 销售订单",
                    trimmed + " 租赁订单",
                    trimmed + " 已完成",
                    trimmed + " 处理中"
                );
            }
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("input", input);
            responseData.put("suggestions", suggestions.subList(0, Math.min(suggestions.size(), limit)));
            
            return ResponseEntity.ok(createSuccessResponse("获取关键词建议成功", responseData));
            
        } catch (Exception e) {
            logger.error("获取关键词建议失败", e);
            return ResponseEntity.badRequest().body(createErrorResponse("获取关键词建议失败：" + e.getMessage()));
        }
    }
    
    /**
     * 获取复杂度级别描述
     */
    private String getComplexityLevel(int complexity) {
        if (complexity <= 3) return "简单";
        if (complexity <= 6) return "中等";
        if (complexity <= 8) return "复杂";
        return "非常复杂";
    }
    
    /**
     * 生成性能优化建议
     */
    private List<String> generatePerformanceTips(int complexity, SearchOptimizationUtil.SearchType searchType) {
        List<String> tips = new ArrayList<>();
        
        if (complexity > 6) {
            tips.add("查询较为复杂，建议减少筛选条件");
            tips.add("考虑使用分页查询，避免一次性加载大量数据");
        }
        
        if (searchType == SearchOptimizationUtil.SearchType.GENERAL) {
            tips.add("通用搜索性能较低，建议使用更具体的搜索条件");
        }
        
        if (searchType == SearchOptimizationUtil.SearchType.ORDER_NUMBER) {
            tips.add("订单号搜索性能最佳，建议优先使用");
        }
        
        tips.add("建议为常用查询字段添加数据库索引");
        tips.add("使用缓存可以提高重复查询的性能");
        
        return tips;
    }
    
    /**
     * 创建成功响应
     */
    private Map<String, Object> createSuccessResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", message);
        response.put("data", data);
        response.put("success", true);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
    
    /**
     * 创建错误响应
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", message);
        response.put("data", null);
        response.put("success", false);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}