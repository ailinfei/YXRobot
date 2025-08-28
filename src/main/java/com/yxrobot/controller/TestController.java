package com.yxrobot.controller;

import com.yxrobot.dto.RegionConfigDTO;
import com.yxrobot.entity.RegionConfig;
import com.yxrobot.mapper.RegionConfigMapper;
import com.yxrobot.service.RegionConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试控制器
 * 用于测试数据库连接和数据
 */
@RestController
@RequestMapping("/api/test")

public class TestController {
    
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private RegionConfigMapper regionConfigMapper;
    
    @Autowired
    private RegionConfigService regionConfigService;
    
    /**
     * 测试数据库连接
     */
    @GetMapping("/db-connection")
    public ResponseEntity<Map<String, Object>> testDatabaseConnection() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 测试数据库连接
            Connection connection = dataSource.getConnection();
            String url = connection.getMetaData().getURL();
            String username = connection.getMetaData().getUserName();
            
            result.put("status", "success");
            result.put("message", "数据库连接成功");
            result.put("url", url);
            result.put("username", username);
            
            connection.close();
            
        } catch (Exception e) {
            logger.error("数据库连接测试失败", e);
            result.put("status", "error");
            result.put("message", "数据库连接失败: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 测试region_configs表数据
     */
    @GetMapping("/region-data")
    public ResponseEntity<Map<String, Object>> testRegionData() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 直接查询数据库
            List<RegionConfig> configs = regionConfigMapper.selectAllActive();
            
            result.put("status", "success");
            result.put("message", "查询成功");
            result.put("count", configs.size());
            result.put("data", configs);
            
            if (configs.isEmpty()) {
                result.put("warning", "region_configs表中没有数据，需要执行insert-region-config-data.sql脚本");
            }
            
        } catch (Exception e) {
            logger.error("查询region_configs表失败", e);
            result.put("status", "error");
            result.put("message", "查询失败: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 测试RegionConfigService
     */
    @GetMapping("/region-service")
    public ResponseEntity<Map<String, Object>> testRegionService() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 测试服务层
            List<RegionConfigDTO> configs = regionConfigService.getRegionConfigs();
            
            result.put("status", "success");
            result.put("message", "服务调用成功");
            result.put("count", configs.size());
            result.put("data", configs);
            
        } catch (Exception e) {
            logger.error("RegionConfigService测试失败", e);
            result.put("status", "error");
            result.put("message", "服务调用失败: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 测试原始API接口
     */
    @GetMapping("/regions-api")
    public ResponseEntity<Map<String, Object>> testRegionsApi() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 模拟原始API调用
            List<RegionConfigDTO> regions = regionConfigService.getRegionConfigs();
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", regions);
            response.put("timestamp", System.currentTimeMillis());
            
            result.put("status", "success");
            result.put("message", "API测试成功");
            result.put("apiResponse", response);
            
        } catch (Exception e) {
            logger.error("API测试失败", e);
            result.put("status", "error");
            result.put("message", "API测试失败: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }
}