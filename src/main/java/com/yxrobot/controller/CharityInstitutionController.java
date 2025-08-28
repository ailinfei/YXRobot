package com.yxrobot.controller;

import com.yxrobot.common.Result;
import com.yxrobot.dto.CharityInstitutionDTO;
import com.yxrobot.dto.PageResult;
import com.yxrobot.service.CharityInstitutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 合作机构管理控制器
 * 负责处理合作机构相关的HTTP请求，包括机构的CRUD操作、查询和统计分析
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-18
 */
@RestController
@RequestMapping("/api/admin/charity/institutions")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CharityInstitutionController {
    
    private static final Logger logger = LoggerFactory.getLogger(CharityInstitutionController.class);
    
    @Autowired
    private CharityInstitutionService charityInstitutionService;
    
    /**
     * 处理CORS预检请求
     * 
     * @return 成功响应
     */
    @RequestMapping(method = RequestMethod.OPTIONS)
    public Result<Void> handleOptions() {
        return Result.success();
    }
    
    /**
     * 获取合作机构列表
     * 支持分页查询、搜索和多条件筛选
     * 
     * GET /api/admin/charity/institutions
     * 
     * @param keyword 搜索关键词（机构名称、联系人、地区）
     * @param type 机构类型（school、hospital、community、ngo、other）
     * @param status 机构状态（active、inactive、suspended）
     * @param page 页码，从1开始，默认1
     * @param size 每页数量，默认20
     * @return Result<PageResult<CharityInstitutionDTO>> 分页机构列表响应
     */
    @GetMapping
    public Result<PageResult<CharityInstitutionDTO>> getCharityInstitutions(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        
        logger.info("获取合作机构列表请求 - 关键词: {}, 类型: {}, 状态: {}, 页码: {}, 每页数量: {}", 
                   keyword, type, status, page, size);
        
        try {
            // 参数验证
            if (page < 1) {
                return Result.badRequest("页码必须大于0");
            }
            if (size < 1 || size > 100) {
                return Result.badRequest("每页数量必须在1-100之间");
            }
            
            // 调用服务层获取数据
            PageResult<CharityInstitutionDTO> pageResult = charityInstitutionService.getCharityInstitutions(
                keyword, type, status, page, size);
            
            logger.info("成功获取合作机构列表 - 总数: {}, 当前页数据量: {}", 
                       pageResult.getTotal(), pageResult.getList().size());
            
            return Result.success(pageResult);
            
        } catch (IllegalArgumentException e) {
            logger.warn("获取合作机构列表参数错误: {}", e.getMessage());
            return Result.badRequest(e.getMessage());
        } catch (Exception e) {
            logger.error("获取合作机构列表失败", e);
            return Result.error("获取合作机构列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取机构详情
     * 
     * GET /api/admin/charity/institutions/{id}
     * 
     * @param id 机构ID
     * @return Result<CharityInstitutionDTO> 机构详情响应
     */
    @GetMapping("/{id}")
    public Result<CharityInstitutionDTO> getCharityInstitutionById(@PathVariable Long id) {
        logger.info("获取合作机构详情请求 - ID: {}", id);
        
        try {
            // 参数验证
            if (id == null || id <= 0) {
                return Result.badRequest("机构ID必须是正整数");
            }
            
            // 调用服务层获取数据
            CharityInstitutionDTO institution = charityInstitutionService.getCharityInstitutionById(id);
            
            if (institution == null) {
                logger.warn("合作机构不存在 - ID: {}", id);
                return Result.notFound("合作机构不存在");
            }
            
            logger.info("成功获取合作机构详情 - ID: {}, 名称: {}", id, institution.getName());
            return Result.success(institution);
            
        } catch (IllegalArgumentException e) {
            logger.warn("获取合作机构详情参数错误: {}", e.getMessage());
            return Result.badRequest(e.getMessage());
        } catch (Exception e) {
            logger.error("获取合作机构详情失败 - ID: {}", id, e);
            return Result.error("获取合作机构详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建新的合作机构
     * 
     * POST /api/admin/charity/institutions
     * Content-Type: application/json
     * 
     * @param institutionDTO 机构信息DTO
     * @param request HTTP请求对象，用于获取操作人信息
     * @return Result<CharityInstitutionDTO> 创建的机构信息响应
     */
    @PostMapping
    public Result<CharityInstitutionDTO> createCharityInstitution(
            @Valid @RequestBody CharityInstitutionDTO institutionDTO,
            HttpServletRequest request) {
        
        logger.info("创建合作机构请求 - 名称: {}, 类型: {}, 地区: {}", 
                   institutionDTO.getName(), institutionDTO.getType(), institutionDTO.getLocation());
        
        try {
            // 参数验证
            if (institutionDTO == null) {
                return Result.badRequest("机构信息不能为空");
            }
            
            // 获取操作人信息（实际项目中应该从JWT token或session中获取）
            // 这里使用模拟数据，实际应该集成认证系统
            Long createBy = 1L; // TODO: 从认证系统获取当前用户ID
            
            // 调用服务层创建机构
            CharityInstitutionDTO createdInstitution = charityInstitutionService.createCharityInstitution(
                institutionDTO, createBy);
            
            logger.info("成功创建合作机构 - ID: {}, 名称: {}", 
                       createdInstitution.getId(), createdInstitution.getName());
            
            return Result.success("合作机构创建成功", createdInstitution);
            
        } catch (IllegalArgumentException e) {
            logger.warn("创建合作机构参数错误: {}", e.getMessage());
            return Result.badRequest(e.getMessage());
        } catch (RuntimeException e) {
            logger.error("创建合作机构业务异常: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            logger.error("创建合作机构失败", e);
            return Result.error("创建合作机构失败，请稍后重试");
        }
    }
    
    /**
     * 更新机构信息
     * 
     * PUT /api/admin/charity/institutions/{id}
     * Content-Type: application/json
     * 
     * @param id 机构ID
     * @param institutionDTO 机构信息DTO
     * @param request HTTP请求对象，用于获取操作人信息
     * @return Result<CharityInstitutionDTO> 更新后的机构信息响应
     */
    @PutMapping("/{id}")
    public Result<CharityInstitutionDTO> updateCharityInstitution(
            @PathVariable Long id,
            @Valid @RequestBody CharityInstitutionDTO institutionDTO,
            HttpServletRequest request) {
        
        logger.info("更新合作机构请求 - ID: {}, 名称: {}, 类型: {}", 
                   id, institutionDTO.getName(), institutionDTO.getType());
        
        try {
            // 参数验证
            if (id == null || id <= 0) {
                return Result.badRequest("机构ID必须是正整数");
            }
            if (institutionDTO == null) {
                return Result.badRequest("机构信息不能为空");
            }
            
            // 获取操作人信息（实际项目中应该从JWT token或session中获取）
            // 这里使用模拟数据，实际应该集成认证系统
            Long updateBy = 1L; // TODO: 从认证系统获取当前用户ID
            
            // 调用服务层更新机构
            CharityInstitutionDTO updatedInstitution = charityInstitutionService.updateCharityInstitution(
                id, institutionDTO, updateBy);
            
            logger.info("成功更新合作机构 - ID: {}, 名称: {}", 
                       updatedInstitution.getId(), updatedInstitution.getName());
            
            return Result.success("合作机构更新成功", updatedInstitution);
            
        } catch (IllegalArgumentException e) {
            logger.warn("更新合作机构参数错误: {}", e.getMessage());
            return Result.badRequest(e.getMessage());
        } catch (RuntimeException e) {
            logger.error("更新合作机构业务异常: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            logger.error("更新合作机构失败 - ID: {}", id, e);
            return Result.error("更新合作机构失败，请稍后重试");
        }
    }
    
    /**
     * 删除机构（软删除）
     * 
     * DELETE /api/admin/charity/institutions/{id}
     * 
     * @param id 机构ID
     * @return Result<Void> 删除结果响应
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteCharityInstitution(@PathVariable Long id) {
        logger.info("删除合作机构请求 - ID: {}", id);
        
        try {
            // 参数验证
            if (id == null || id <= 0) {
                return Result.badRequest("机构ID必须是正整数");
            }
            
            // 调用服务层删除机构
            charityInstitutionService.deleteCharityInstitution(id);
            
            logger.info("成功删除合作机构 - ID: {}", id);
            return Result.success("合作机构删除成功", null);
            
        } catch (IllegalArgumentException e) {
            logger.warn("删除合作机构参数错误: {}", e.getMessage());
            return Result.badRequest(e.getMessage());
        } catch (RuntimeException e) {
            logger.error("删除合作机构业务异常: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            logger.error("删除合作机构失败 - ID: {}", id, e);
            return Result.error("删除合作机构失败，请稍后重试");
        }
    }
    
    /**
     * 批量删除机构（软删除）
     * 
     * DELETE /api/admin/charity/institutions/batch
     * Content-Type: application/json
     * 
     * @param ids 机构ID列表
     * @return Result<Map<String, Object>> 批量删除结果响应
     */
    @DeleteMapping("/batch")
    public Result<Map<String, Object>> batchDeleteCharityInstitutions(
            @RequestBody List<Long> ids) {
        
        logger.info("批量删除合作机构请求 - 数量: {}", ids != null ? ids.size() : 0);
        
        try {
            // 参数验证
            if (ids == null || ids.isEmpty()) {
                return Result.badRequest("机构ID列表不能为空");
            }
            if (ids.size() > 100) {
                return Result.badRequest("批量删除数量不能超过100个");
            }
            
            // 验证ID有效性
            for (Long id : ids) {
                if (id == null || id <= 0) {
                    return Result.badRequest("机构ID必须是正整数");
                }
            }
            
            // 调用服务层批量删除机构
            int deletedCount = charityInstitutionService.batchDeleteCharityInstitutions(ids);
            
            logger.info("成功批量删除合作机构 - 删除数量: {}", deletedCount);
            
            Map<String, Object> result = Map.of(
                "deletedCount", deletedCount,
                "totalRequested", ids.size()
            );
            
            return Result.success("批量删除合作机构成功", result);
            
        } catch (IllegalArgumentException e) {
            logger.warn("批量删除合作机构参数错误: {}", e.getMessage());
            return Result.badRequest(e.getMessage());
        } catch (RuntimeException e) {
            logger.error("批量删除合作机构业务异常: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            logger.error("批量删除合作机构失败", e);
            return Result.error("批量删除合作机构失败，请稍后重试");
        }
    }
    
    /**
     * 批量更新机构状态
     * 
     * PUT /api/admin/charity/institutions/batch/status
     * Content-Type: application/json
     * 
     * @param requestBody 包含机构ID列表和新状态的请求体
     * @return Result<Map<String, Object>> 批量更新结果响应
     */
    @PutMapping("/batch/status")
    public Result<Map<String, Object>> batchUpdateStatus(
            @RequestBody Map<String, Object> requestBody) {
        
        logger.info("批量更新机构状态请求");
        
        try {
            // 参数验证和提取
            @SuppressWarnings("unchecked")
            List<Long> ids = (List<Long>) requestBody.get("ids");
            String status = (String) requestBody.get("status");
            
            if (ids == null || ids.isEmpty()) {
                return Result.badRequest("机构ID列表不能为空");
            }
            if (status == null || status.trim().isEmpty()) {
                return Result.badRequest("机构状态不能为空");
            }
            if (ids.size() > 100) {
                return Result.badRequest("批量更新数量不能超过100个");
            }
            
            // 验证ID有效性
            for (Long id : ids) {
                if (id == null || id <= 0) {
                    return Result.badRequest("机构ID必须是正整数");
                }
            }
            
            // 调用服务层批量更新状态
            int updatedCount = charityInstitutionService.batchUpdateStatus(ids, status);
            
            logger.info("成功批量更新机构状态 - 更新数量: {}, 新状态: {}", updatedCount, status);
            
            Map<String, Object> result = Map.of(
                "updatedCount", updatedCount,
                "totalRequested", ids.size(),
                "newStatus", status
            );
            
            return Result.success("批量更新机构状态成功", result);
            
        } catch (IllegalArgumentException e) {
            logger.warn("批量更新机构状态参数错误: {}", e.getMessage());
            return Result.badRequest(e.getMessage());
        } catch (RuntimeException e) {
            logger.error("批量更新机构状态业务异常: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            logger.error("批量更新机构状态失败", e);
            return Result.error("批量更新机构状态失败，请稍后重试");
        }
    }
    
    /**
     * 获取活跃机构列表
     * 
     * GET /api/admin/charity/institutions/active
     * 
     * @return Result<List<CharityInstitutionDTO>> 活跃机构列表响应
     */
    @GetMapping("/active")
    public Result<List<CharityInstitutionDTO>> getActiveInstitutions() {
        logger.info("获取活跃机构列表请求");
        
        try {
            List<CharityInstitutionDTO> activeInstitutions = charityInstitutionService.getActiveInstitutions();
            
            logger.info("成功获取活跃机构列表 - 数量: {}", activeInstitutions.size());
            return Result.success(activeInstitutions);
            
        } catch (Exception e) {
            logger.error("获取活跃机构列表失败", e);
            return Result.error("获取活跃机构列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取最近合作的机构
     * 
     * GET /api/admin/charity/institutions/recent
     * 
     * @param limit 限制返回数量，默认10个
     * @return Result<List<CharityInstitutionDTO>> 最近合作机构列表响应
     */
    @GetMapping("/recent")
    public Result<List<CharityInstitutionDTO>> getRecentCooperations(
            @RequestParam(defaultValue = "10") Integer limit) {
        
        logger.info("获取最近合作机构请求 - 限制数量: {}", limit);
        
        try {
            // 参数验证
            if (limit <= 0 || limit > 50) {
                return Result.badRequest("限制数量必须在1-50之间");
            }
            
            List<CharityInstitutionDTO> recentInstitutions = charityInstitutionService.getRecentCooperations(limit);
            
            logger.info("成功获取最近合作机构 - 数量: {}", recentInstitutions.size());
            return Result.success(recentInstitutions);
            
        } catch (IllegalArgumentException e) {
            logger.warn("获取最近合作机构参数错误: {}", e.getMessage());
            return Result.badRequest(e.getMessage());
        } catch (Exception e) {
            logger.error("获取最近合作机构失败", e);
            return Result.error("获取最近合作机构失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取机构统计信息
     * 
     * GET /api/admin/charity/institutions/statistics
     * 
     * @param type 统计类型（status、type、location、duration）
     * @return Result<List<Map<String, Object>>> 统计信息响应
     */
    @GetMapping("/statistics")
    public Result<List<Map<String, Object>>> getInstitutionStatistics(
            @RequestParam(defaultValue = "status") String type) {
        
        logger.info("获取机构统计信息请求 - 统计类型: {}", type);
        
        try {
            List<Map<String, Object>> statistics;
            
            switch (type.toLowerCase()) {
                case "status":
                    statistics = charityInstitutionService.getStatusStatistics();
                    break;
                case "type":
                    statistics = charityInstitutionService.getTypeStatistics();
                    break;
                case "location":
                    statistics = charityInstitutionService.getLocationStatistics();
                    break;
                case "duration":
                    statistics = charityInstitutionService.getCooperationDurationStats();
                    break;
                default:
                    return Result.badRequest("不支持的统计类型: " + type);
            }
            
            logger.info("成功获取机构统计信息 - 类型: {}, 数量: {}", type, statistics.size());
            return Result.success(statistics);
            
        } catch (IllegalArgumentException e) {
            logger.warn("获取机构统计信息参数错误: {}", e.getMessage());
            return Result.badRequest(e.getMessage());
        } catch (Exception e) {
            logger.error("获取机构统计信息失败", e);
            return Result.error("获取机构统计信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新机构最后访问日期
     * 
     * PUT /api/admin/charity/institutions/{id}/last-visit
     * Content-Type: application/json
     * 
     * @param id 机构ID
     * @param requestBody 包含最后访问日期的请求体
     * @return Result<Void> 更新结果响应
     */
    @PutMapping("/{id}/last-visit")
    public Result<Void> updateLastVisitDate(
            @PathVariable Long id,
            @RequestBody Map<String, String> requestBody) {
        
        logger.info("更新机构最后访问日期请求 - ID: {}", id);
        
        try {
            // 参数验证
            if (id == null || id <= 0) {
                return Result.badRequest("机构ID必须是正整数");
            }
            
            String lastVisitDateStr = requestBody.get("lastVisitDate");
            if (lastVisitDateStr == null || lastVisitDateStr.trim().isEmpty()) {
                return Result.badRequest("最后访问日期不能为空");
            }
            
            // 解析日期
            java.time.LocalDate lastVisitDate;
            try {
                lastVisitDate = java.time.LocalDate.parse(lastVisitDateStr);
            } catch (Exception e) {
                return Result.badRequest("最后访问日期格式不正确，请使用yyyy-MM-dd格式");
            }
            
            // 调用服务层更新最后访问日期
            charityInstitutionService.updateLastVisitDate(id, lastVisitDate);
            
            logger.info("成功更新机构最后访问日期 - ID: {}, 日期: {}", id, lastVisitDate);
            return Result.success("最后访问日期更新成功", null);
            
        } catch (IllegalArgumentException e) {
            logger.warn("更新机构最后访问日期参数错误: {}", e.getMessage());
            return Result.badRequest(e.getMessage());
        } catch (RuntimeException e) {
            logger.error("更新机构最后访问日期业务异常: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            logger.error("更新机构最后访问日期失败 - ID: {}", id, e);
            return Result.error("更新最后访问日期失败，请稍后重试");
        }
    }
    
    /**
     * 获取客户端IP地址
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
}