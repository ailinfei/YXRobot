package com.yxrobot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.*;

/**
 * 路由检查控制器
 * 用于诊断和查看所有可用的API路由
 */
@RestController
@RequestMapping("/api/route-check")

public class RouteCheckController {
    
    private static final Logger logger = LoggerFactory.getLogger(RouteCheckController.class);
    
    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;
    
    /**
     * 获取所有可用的路由映射
     */
    @GetMapping("/all-routes")
    public ResponseEntity<Map<String, Object>> getAllRoutes() {
        logger.info("获取所有路由映射");
        
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> routes = new ArrayList<>();
        
        try {
            Map<RequestMappingInfo, HandlerMethod> handlerMethods = 
                requestMappingHandlerMapping.getHandlerMethods();
            
            for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
                RequestMappingInfo mappingInfo = entry.getKey();
                HandlerMethod handlerMethod = entry.getValue();
                
                Map<String, Object> routeInfo = new HashMap<>();
                routeInfo.put("patterns", mappingInfo.getPatternsCondition().getPatterns());
                routeInfo.put("methods", mappingInfo.getMethodsCondition().getMethods());
                routeInfo.put("controller", handlerMethod.getBeanType().getSimpleName());
                routeInfo.put("method", handlerMethod.getMethod().getName());
                
                routes.add(routeInfo);
            }
            
            // 按路径排序
            routes.sort((a, b) -> {
                Set<String> patternsA = (Set<String>) a.get("patterns");
                Set<String> patternsB = (Set<String>) b.get("patterns");
                String pathA = patternsA.isEmpty() ? "" : patternsA.iterator().next();
                String pathB = patternsB.isEmpty() ? "" : patternsB.iterator().next();
                return pathA.compareTo(pathB);
            });
            
            result.put("status", "success");
            result.put("message", "获取路由成功");
            result.put("totalRoutes", routes.size());
            result.put("routes", routes);
            
        } catch (Exception e) {
            logger.error("获取路由失败", e);
            result.put("status", "error");
            result.put("message", "获取路由失败: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取新闻相关的路由
     */
    @GetMapping("/news-routes")
    public ResponseEntity<Map<String, Object>> getNewsRoutes() {
        logger.info("获取新闻相关路由");
        
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> newsRoutes = new ArrayList<>();
        
        try {
            Map<RequestMappingInfo, HandlerMethod> handlerMethods = 
                requestMappingHandlerMapping.getHandlerMethods();
            
            for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
                RequestMappingInfo mappingInfo = entry.getKey();
                HandlerMethod handlerMethod = entry.getValue();
                
                // 过滤新闻相关的路由
                Set<String> patterns = mappingInfo.getPatternsCondition().getPatterns();
                boolean isNewsRoute = patterns.stream().anyMatch(pattern -> 
                    pattern.contains("/api/news"));
                
                if (isNewsRoute) {
                    Map<String, Object> routeInfo = new HashMap<>();
                    routeInfo.put("patterns", patterns);
                    routeInfo.put("methods", mappingInfo.getMethodsCondition().getMethods());
                    routeInfo.put("controller", handlerMethod.getBeanType().getSimpleName());
                    routeInfo.put("method", handlerMethod.getMethod().getName());
                    
                    newsRoutes.add(routeInfo);
                }
            }
            
            result.put("status", "success");
            result.put("message", "获取新闻路由成功");
            result.put("totalNewsRoutes", newsRoutes.size());
            result.put("routes", newsRoutes);
            
        } catch (Exception e) {
            logger.error("获取新闻路由失败", e);
            result.put("status", "error");
            result.put("message", "获取新闻路由失败: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 测试特定路径是否存在
     */
    @GetMapping("/test-path")
    public ResponseEntity<Map<String, Object>> testPath(@RequestParam String path) {
        logger.info("测试路径: {}", path);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<RequestMappingInfo, HandlerMethod> handlerMethods = 
                requestMappingHandlerMapping.getHandlerMethods();
            
            boolean pathExists = false;
            List<Map<String, Object>> matchingRoutes = new ArrayList<>();
            
            for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
                RequestMappingInfo mappingInfo = entry.getKey();
                HandlerMethod handlerMethod = entry.getValue();
                
                Set<String> patterns = mappingInfo.getPatternsCondition().getPatterns();
                for (String pattern : patterns) {
                    if (pattern.equals(path) || pattern.matches(path.replace("*", ".*"))) {
                        pathExists = true;
                        
                        Map<String, Object> routeInfo = new HashMap<>();
                        routeInfo.put("pattern", pattern);
                        routeInfo.put("methods", mappingInfo.getMethodsCondition().getMethods());
                        routeInfo.put("controller", handlerMethod.getBeanType().getSimpleName());
                        routeInfo.put("method", handlerMethod.getMethod().getName());
                        
                        matchingRoutes.add(routeInfo);
                    }
                }
            }
            
            result.put("status", "success");
            result.put("path", path);
            result.put("exists", pathExists);
            result.put("matchingRoutes", matchingRoutes);
            
            if (!pathExists) {
                result.put("message", "路径不存在");
            } else {
                result.put("message", "路径存在，找到 " + matchingRoutes.size() + " 个匹配的路由");
            }
            
        } catch (Exception e) {
            logger.error("测试路径失败", e);
            result.put("status", "error");
            result.put("message", "测试路径失败: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 应用健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("message", "应用运行正常");
        result.put("timestamp", System.currentTimeMillis());
        result.put("port", "8081");
        
        return ResponseEntity.ok(result);
    }
}