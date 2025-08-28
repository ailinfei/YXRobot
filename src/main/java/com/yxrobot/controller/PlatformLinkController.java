package com.yxrobot.controller;

import com.yxrobot.dto.PlatformLinkDTO;
import com.yxrobot.dto.PlatformLinkFormDTO;
import com.yxrobot.dto.RegionConfigDTO;
import com.yxrobot.service.PlatformLinkService;
import com.yxrobot.service.RegionConfigService;
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
 * 平台链接管理控制器
 * 处理平台链接相关的HTTP请求
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-22
 */
@RestController
@RequestMapping("/api/platform-links")
@Validated

public class PlatformLinkController {
    
    private static final Logger logger = LoggerFactory.getLogger(PlatformLinkController.class);
    
    @Autowired
    private PlatformLinkService platformLinkService;
    
    @Autowired
    private RegionConfigService regionConfigService;
    
    /**
     * 分页查询平台链接列表
     * 
     * @param page 页码（从1开始）
     * @param pageSize 每页大小
     * @param platformType 平台类型（可选）
     * @param region 地区（可选）
     * @param languageCode 语言代码（可选）
     * @param linkStatus 链接状态（可选）
     * @param isEnabled 是否启用（可选）
     * @param keyword 关键词搜索（可选）
     * @return 分页结果
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getPlatformLinks(
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "20") @Min(1) Integer pageSize,
            @RequestParam(required = false) String platformType,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String languageCode,
            @RequestParam(required = false) String linkStatus,
            @RequestParam(required = false) Boolean isEnabled,
            @RequestParam(required = false) String keyword) {
        
        logger.info("查询平台链接列表 - 页码: {}, 页大小: {}, 平台类型: {}, 地区: {}, 语言: {}, 状态: {}, 启用: {}, 关键词: {}", 
                   page, pageSize, platformType, region, languageCode, linkStatus, isEnabled, keyword);
        
        try {
            Map<String, Object> result = platformLinkService.getPlatformLinks(
                page, pageSize, platformType, region, languageCode, linkStatus, isEnabled, keyword);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", result);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("查询平台链接列表失败", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 根据ID获取平台链接详情
     * 
     * @param id 链接ID
     * @return 平台链接详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPlatformLinkById(@PathVariable @NotNull Long id) {
        logger.info("查询平台链接详情 - ID: {}", id);
        
        try {
            PlatformLinkDTO platformLink = platformLinkService.getPlatformLinkById(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", platformLink);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("查询平台链接详情失败 - ID: {}, 错误: {}", id, e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 404);
            response.put("message", e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(404).body(response);
            
        } catch (Exception e) {
            logger.error("查询平台链接详情失败 - ID: {}", id, e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 创建新的平台链接
     * 
     * @param formDTO 表单数据
     * @return 创建的平台链接
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createPlatformLink(@Valid @RequestBody PlatformLinkFormDTO formDTO) {
        logger.info("创建平台链接 - 平台: {}, 地区: {}, 语言: {}", 
                   formDTO.getPlatformName(), formDTO.getRegion(), formDTO.getLanguageCode());
        
        try {
            PlatformLinkDTO platformLink = platformLinkService.createPlatformLink(formDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "创建成功");
            response.put("data", platformLink);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("创建平台链接失败 - 错误: {}", e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(400).body(response);
            
        } catch (Exception e) {
            logger.error("创建平台链接失败", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "创建失败: " + e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 更新平台链接信息
     * 
     * @param id 链接ID
     * @param formDTO 表单数据
     * @return 更新后的平台链接
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updatePlatformLink(
            @PathVariable @NotNull Long id, 
            @Valid @RequestBody PlatformLinkFormDTO formDTO) {
        
        logger.info("更新平台链接 - ID: {}, 平台: {}", id, formDTO.getPlatformName());
        
        try {
            PlatformLinkDTO platformLink = platformLinkService.updatePlatformLink(id, formDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "更新成功");
            response.put("data", platformLink);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("更新平台链接失败 - ID: {}, 错误: {}", id, e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(400).body(response);
            
        } catch (Exception e) {
            logger.error("更新平台链接失败 - ID: {}", id, e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "更新失败: " + e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 删除平台链接（软删除）
     * 
     * @param id 链接ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletePlatformLink(@PathVariable @NotNull Long id) {
        logger.info("删除平台链接 - ID: {}", id);
        
        try {
            platformLinkService.deletePlatformLink(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "删除成功");
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("删除平台链接失败 - ID: {}, 错误: {}", id, e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 404);
            response.put("message", e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(404).body(response);
            
        } catch (Exception e) {
            logger.error("删除平台链接失败 - ID: {}", id, e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "删除失败: " + e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 批量删除平台链接
     * 
     * @param ids 链接ID列表
     * @return 删除结果
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Map<String, Object>> batchDeletePlatformLinks(@RequestBody List<Long> ids) {
        logger.info("批量删除平台链接 - 数量: {}", ids.size());
        
        try {
            int successCount = platformLinkService.batchDeletePlatformLinks(ids);
            
            Map<String, Object> result = new HashMap<>();
            result.put("total", ids.size());
            result.put("success", successCount);
            result.put("failed", ids.size() - successCount);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "批量删除完成");
            response.put("data", result);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("批量删除平台链接失败", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "批量删除失败: " + e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 根据地区和语言查询平台链接
     * 
     * @param region 地区
     * @param languageCode 语言代码
     * @return 平台链接列表
     */
    @GetMapping("/by-region-language")
    public ResponseEntity<Map<String, Object>> getPlatformLinksByRegionAndLanguage(
            @RequestParam String region,
            @RequestParam String languageCode) {
        
        logger.info("根据地区和语言查询平台链接 - 地区: {}, 语言: {}", region, languageCode);
        
        try {
            List<PlatformLinkDTO> links = platformLinkService.getPlatformLinksByRegionAndLanguage(region, languageCode);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", links);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("根据地区和语言查询平台链接失败", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 根据平台类型查询平台链接
     * 
     * @param platformType 平台类型
     * @return 平台链接列表
     */
    @GetMapping("/by-type")
    public ResponseEntity<Map<String, Object>> getPlatformLinksByType(@RequestParam String platformType) {
        logger.info("根据平台类型查询平台链接 - 类型: {}", platformType);
        
        try {
            List<PlatformLinkDTO> links = platformLinkService.getPlatformLinksByType(platformType);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", links);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("根据平台类型查询平台链接失败", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 切换平台链接的启用状态
     * 
     * @param id 链接ID
     * @param requestBody 请求体，包含isEnabled字段
     * @return 更新后的平台链接
     */
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Map<String, Object>> togglePlatformLinkStatus(
            @PathVariable @NotNull Long id,
            @RequestBody Map<String, Object> requestBody) {
        
        logger.info("切换平台链接状态 - ID: {}", id);
        
        try {
            // 从请求体中获取isEnabled参数
            Boolean isEnabled = null;
            if (requestBody.containsKey("isEnabled")) {
                Object enabledValue = requestBody.get("isEnabled");
                if (enabledValue instanceof Boolean) {
                    isEnabled = (Boolean) enabledValue;
                } else if (enabledValue instanceof String) {
                    isEnabled = Boolean.parseBoolean((String) enabledValue);
                }
            }
            
            if (isEnabled == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 400);
                response.put("message", "缺少必需的参数：isEnabled");
                response.put("data", null);
                response.put("timestamp", System.currentTimeMillis());
                
                return ResponseEntity.status(400).body(response);
            }
            
            PlatformLinkDTO platformLink = platformLinkService.togglePlatformLinkStatus(id, isEnabled);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "状态切换成功");
            response.put("data", platformLink);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("切换平台链接状态失败 - ID: {}, 错误: {}", id, e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(400).body(response);
            
        } catch (Exception e) {
            logger.error("切换平台链接状态失败 - ID: {}", id, e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "状态切换失败: " + e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 批量切换平台链接的启用状态
     * 
     * @param requestBody 请求体，包含linkIds和isEnabled字段
     * @return 批量操作结果
     */
    @PatchMapping("/batch/toggle")
    public ResponseEntity<Map<String, Object>> batchTogglePlatformLinkStatus(@RequestBody Map<String, Object> requestBody) {
        logger.info("批量切换平台链接状态");
        
        try {
            // 从请求体中获取参数
            @SuppressWarnings("unchecked")
            List<Long> linkIds = (List<Long>) requestBody.get("linkIds");
            Boolean isEnabled = null;
            
            if (requestBody.containsKey("isEnabled")) {
                Object enabledValue = requestBody.get("isEnabled");
                if (enabledValue instanceof Boolean) {
                    isEnabled = (Boolean) enabledValue;
                } else if (enabledValue instanceof String) {
                    isEnabled = Boolean.parseBoolean((String) enabledValue);
                }
            }
            
            if (linkIds == null || linkIds.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 400);
                response.put("message", "缺少必需的参数：linkIds");
                response.put("data", null);
                response.put("timestamp", System.currentTimeMillis());
                
                return ResponseEntity.status(400).body(response);
            }
            
            if (isEnabled == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 400);
                response.put("message", "缺少必需的参数：isEnabled");
                response.put("data", null);
                response.put("timestamp", System.currentTimeMillis());
                
                return ResponseEntity.status(400).body(response);
            }
            
            // 批量切换状态
            int successCount = 0;
            for (Long linkId : linkIds) {
                try {
                    platformLinkService.togglePlatformLinkStatus(linkId, isEnabled);
                    successCount++;
                } catch (Exception e) {
                    logger.warn("切换链接状态失败 - ID: {}, 错误: {}", linkId, e.getMessage());
                }
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("total", linkIds.size());
            result.put("success", successCount);
            result.put("failed", linkIds.size() - successCount);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "批量状态切换完成");
            response.put("data", result);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("批量切换平台链接状态失败", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "批量状态切换失败: " + e.getMessage());
            response.put("data", null);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    

    

}