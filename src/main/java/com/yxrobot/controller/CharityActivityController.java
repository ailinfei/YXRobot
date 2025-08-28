package com.yxrobot.controller;

import com.yxrobot.common.Result;
import com.yxrobot.dto.CharityActivityDTO;
import com.yxrobot.dto.PageResult;
import com.yxrobot.service.CharityActivityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 公益活动管理控制器
 * 提供公益活动的CRUD操作和统计查询接口
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-18
 */
@RestController
@RequestMapping("/api/admin/charity/activities")
public class CharityActivityController {
    
    private static final Logger logger = LoggerFactory.getLogger(CharityActivityController.class);
    
    @Autowired
    private CharityActivityService charityActivityService;
    
    /**
     * 获取公益活动列表
     * 支持分页查询、搜索和多条件筛选
     * 
     * @param keyword 搜索关键词（活动标题、组织方、地点）
     * @param type 活动类型
     * @param status 活动状态
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param page 页码，从1开始，默认1
     * @param size 每页数量，默认20
     * @return 分页活动列表
     */
    @GetMapping
    public Result<PageResult<CharityActivityDTO>> getCharityActivities(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        
        logger.info("获取公益活动列表请求 - 关键词: {}, 类型: {}, 状态: {}, 开始日期: {}, 结束日期: {}, 页码: {}, 每页数量: {}", 
                   keyword, type, status, startDate, endDate, page, size);
        
        try {
            PageResult<CharityActivityDTO> result = charityActivityService.getCharityActivities(
                keyword, type, status, startDate, endDate, page, size);
            
            logger.info("成功获取公益活动列表，总数: {}, 当前页数据量: {}", result.getTotal(), result.getList().size());
            return Result.success(result);
            
        } catch (IllegalArgumentException e) {
            logger.warn("获取公益活动列表参数错误: {}", e.getMessage());
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            logger.error("获取公益活动列表失败", e);
            return Result.error(500, "获取公益活动列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取活动详情
     * 
     * @param id 活动ID
     * @return 活动详情
     */
    @GetMapping("/{id}")
    public Result<CharityActivityDTO> getCharityActivityById(@PathVariable Long id) {
        logger.info("获取公益活动详情请求 - ID: {}", id);
        
        try {
            CharityActivityDTO activity = charityActivityService.getCharityActivityById(id);
            
            if (activity == null) {
                logger.warn("公益活动不存在 - ID: {}", id);
                return Result.error(404, "公益活动不存在");
            }
            
            logger.info("成功获取公益活动详情 - 标题: {}", activity.getTitle());
            return Result.success(activity);
            
        } catch (IllegalArgumentException e) {
            logger.warn("获取公益活动详情参数错误: {}", e.getMessage());
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            logger.error("获取公益活动详情失败 - ID: {}", id, e);
            return Result.error(500, "获取公益活动详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建新的公益活动
     * 
     * @param activityDTO 活动信息
     * @param request HTTP请求对象
     * @return 创建的活动信息
     */
    @PostMapping
    public Result<CharityActivityDTO> createCharityActivity(
            @Valid @RequestBody CharityActivityDTO activityDTO,
            HttpServletRequest request) {
        
        logger.info("创建公益活动请求 - 标题: {}", activityDTO.getTitle());
        
        try {
            // 从请求中获取用户ID（实际项目中应该从JWT token或session中获取）
            Long createBy = getUserIdFromRequest(request);
            
            CharityActivityDTO createdActivity = charityActivityService.createCharityActivity(activityDTO, createBy);
            
            logger.info("成功创建公益活动 - ID: {}, 标题: {}", createdActivity.getId(), createdActivity.getTitle());
            return Result.success(createdActivity);
            
        } catch (IllegalArgumentException e) {
            logger.warn("创建公益活动参数错误: {}", e.getMessage());
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            logger.error("创建公益活动失败", e);
            return Result.error(500, "创建公益活动失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新活动信息
     * 
     * @param id 活动ID
     * @param activityDTO 活动信息
     * @param request HTTP请求对象
     * @return 更新后的活动信息
     */
    @PutMapping("/{id}")
    public Result<CharityActivityDTO> updateCharityActivity(
            @PathVariable Long id,
            @Valid @RequestBody CharityActivityDTO activityDTO,
            HttpServletRequest request) {
        
        logger.info("更新公益活动请求 - ID: {}, 标题: {}", id, activityDTO.getTitle());
        
        try {
            // 从请求中获取用户ID（实际项目中应该从JWT token或session中获取）
            Long updateBy = getUserIdFromRequest(request);
            
            CharityActivityDTO updatedActivity = charityActivityService.updateCharityActivity(id, activityDTO, updateBy);
            
            logger.info("成功更新公益活动 - ID: {}, 标题: {}", id, updatedActivity.getTitle());
            return Result.success(updatedActivity);
            
        } catch (IllegalArgumentException e) {
            logger.warn("更新公益活动参数错误: {}", e.getMessage());
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            logger.error("更新公益活动失败 - ID: {}", id, e);
            return Result.error(500, "更新公益活动失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除活动（软删除）
     * 
     * @param id 活动ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteCharityActivity(@PathVariable Long id) {
        logger.info("删除公益活动请求 - ID: {}", id);
        
        try {
            charityActivityService.deleteCharityActivity(id);
            
            logger.info("成功删除公益活动 - ID: {}", id);
            return Result.success();
            
        } catch (IllegalArgumentException e) {
            logger.warn("删除公益活动参数错误: {}", e.getMessage());
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            logger.error("删除公益活动失败 - ID: {}", id, e);
            return Result.error(500, "删除公益活动失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量删除活动（软删除）
     * 
     * @param ids 活动ID列表
     * @return 删除结果
     */
    @DeleteMapping("/batch")
    public Result<Integer> batchDeleteCharityActivities(@RequestBody List<Long> ids) {
        logger.info("批量删除公益活动请求 - 数量: {}", ids.size());
        
        try {
            int deletedCount = charityActivityService.batchDeleteCharityActivities(ids);
            
            logger.info("成功批量删除公益活动 - 删除数量: {}", deletedCount);
            return Result.success(deletedCount);
            
        } catch (IllegalArgumentException e) {
            logger.warn("批量删除公益活动参数错误: {}", e.getMessage());
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            logger.error("批量删除公益活动失败", e);
            return Result.error(500, "批量删除公益活动失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据项目ID获取活动列表
     * 
     * @param projectId 项目ID
     * @return 活动列表
     */
    @GetMapping("/project/{projectId}")
    public Result<List<CharityActivityDTO>> getActivitiesByProjectId(@PathVariable Long projectId) {
        logger.info("根据项目ID获取活动列表请求 - 项目ID: {}", projectId);
        
        try {
            List<CharityActivityDTO> activities = charityActivityService.getActivitiesByProjectId(projectId);
            
            logger.info("成功获取项目活动列表 - 项目ID: {}, 活动数量: {}", projectId, activities.size());
            return Result.success(activities);
            
        } catch (IllegalArgumentException e) {
            logger.warn("获取项目活动列表参数错误: {}", e.getMessage());
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            logger.error("获取项目活动列表失败 - 项目ID: {}", projectId, e);
            return Result.error(500, "获取项目活动列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新活动状态
     * 
     * @param id 活动ID
     * @param status 新状态
     * @return 更新结果
     */
    @PatchMapping("/{id}/status")
    public Result<Void> updateActivityStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        
        logger.info("更新活动状态请求 - ID: {}, 状态: {}", id, status);
        
        try {
            charityActivityService.updateActivityStatus(id, status);
            
            logger.info("成功更新活动状态 - ID: {}, 状态: {}", id, status);
            return Result.success();
            
        } catch (IllegalArgumentException e) {
            logger.warn("更新活动状态参数错误: {}", e.getMessage());
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            logger.error("更新活动状态失败 - ID: {}", id, e);
            return Result.error(500, "更新活动状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量更新活动状态
     * 
     * @param ids 活动ID列表
     * @param status 新状态
     * @return 更新结果
     */
    @PatchMapping("/batch/status")
    public Result<Integer> batchUpdateStatus(
            @RequestBody List<Long> ids,
            @RequestParam String status) {
        
        logger.info("批量更新活动状态请求 - 数量: {}, 状态: {}", ids.size(), status);
        
        try {
            int updatedCount = charityActivityService.batchUpdateStatus(ids, status);
            
            logger.info("成功批量更新活动状态 - 更新数量: {}", updatedCount);
            return Result.success(updatedCount);
            
        } catch (IllegalArgumentException e) {
            logger.warn("批量更新活动状态参数错误: {}", e.getMessage());
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            logger.error("批量更新活动状态失败", e);
            return Result.error(500, "批量更新活动状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取即将开始的活动
     * 
     * @param days 天数范围，默认7天
     * @return 即将开始的活动列表
     */
    @GetMapping("/upcoming")
    public Result<List<CharityActivityDTO>> getUpcomingActivities(
            @RequestParam(defaultValue = "7") Integer days) {
        
        logger.info("获取即将开始的活动请求 - 天数范围: {}", days);
        
        try {
            List<CharityActivityDTO> activities = charityActivityService.getUpcomingActivities(days);
            
            logger.info("成功获取即将开始的活动 - 数量: {}", activities.size());
            return Result.success(activities);
            
        } catch (Exception e) {
            logger.error("获取即将开始的活动失败", e);
            return Result.error(500, "获取即将开始的活动失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取正在进行的活动
     * 
     * @return 正在进行的活动列表
     */
    @GetMapping("/ongoing")
    public Result<List<CharityActivityDTO>> getOngoingActivities() {
        logger.info("获取正在进行的活动请求");
        
        try {
            List<CharityActivityDTO> activities = charityActivityService.getOngoingActivities();
            
            logger.info("成功获取正在进行的活动 - 数量: {}", activities.size());
            return Result.success(activities);
            
        } catch (Exception e) {
            logger.error("获取正在进行的活动失败", e);
            return Result.error(500, "获取正在进行的活动失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取最近完成的活动
     * 
     * @param limit 限制返回数量，默认10
     * @return 最近完成的活动列表
     */
    @GetMapping("/recent-completed")
    public Result<List<CharityActivityDTO>> getRecentCompletedActivities(
            @RequestParam(defaultValue = "10") Integer limit) {
        
        logger.info("获取最近完成的活动请求 - 限制数量: {}", limit);
        
        try {
            List<CharityActivityDTO> activities = charityActivityService.getRecentCompletedActivities(limit);
            
            logger.info("成功获取最近完成的活动 - 数量: {}", activities.size());
            return Result.success(activities);
            
        } catch (Exception e) {
            logger.error("获取最近完成的活动失败", e);
            return Result.error(500, "获取最近完成的活动失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取活动状态统计
     * 
     * @return 状态统计信息
     */
    @GetMapping("/statistics/status")
    public Result<List<Map<String, Object>>> getStatusStatistics() {
        logger.info("获取活动状态统计请求");
        
        try {
            List<Map<String, Object>> statistics = charityActivityService.getStatusStatistics();
            
            logger.info("成功获取活动状态统计 - 统计项数量: {}", statistics.size());
            return Result.success(statistics);
            
        } catch (Exception e) {
            logger.error("获取活动状态统计失败", e);
            return Result.error(500, "获取活动状态统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取活动类型统计
     * 
     * @return 类型统计信息
     */
    @GetMapping("/statistics/type")
    public Result<List<Map<String, Object>>> getTypeStatistics() {
        logger.info("获取活动类型统计请求");
        
        try {
            List<Map<String, Object>> statistics = charityActivityService.getTypeStatistics();
            
            logger.info("成功获取活动类型统计 - 统计项数量: {}", statistics.size());
            return Result.success(statistics);
            
        } catch (Exception e) {
            logger.error("获取活动类型统计失败", e);
            return Result.error(500, "获取活动类型统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取月度活动统计
     * 
     * @param months 月份数量，默认12个月
     * @return 月度活动统计信息
     */
    @GetMapping("/statistics/monthly")
    public Result<List<Map<String, Object>>> getMonthlyStatistics(
            @RequestParam(defaultValue = "12") Integer months) {
        
        logger.info("获取月度活动统计请求 - 月份数量: {}", months);
        
        try {
            List<Map<String, Object>> statistics = charityActivityService.getMonthlyStatistics(months);
            
            logger.info("成功获取月度活动统计 - 统计项数量: {}", statistics.size());
            return Result.success(statistics);
            
        } catch (Exception e) {
            logger.error("获取月度活动统计失败", e);
            return Result.error(500, "获取月度活动统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取志愿者参与统计
     * 
     * @param months 月份数量，默认6个月
     * @return 志愿者参与统计信息
     */
    @GetMapping("/statistics/volunteer")
    public Result<List<Map<String, Object>>> getVolunteerParticipationStats(
            @RequestParam(defaultValue = "6") Integer months) {
        
        logger.info("获取志愿者参与统计请求 - 月份数量: {}", months);
        
        try {
            List<Map<String, Object>> statistics = charityActivityService.getVolunteerParticipationStats(months);
            
            logger.info("成功获取志愿者参与统计 - 统计项数量: {}", statistics.size());
            return Result.success(statistics);
            
        } catch (Exception e) {
            logger.error("获取志愿者参与统计失败", e);
            return Result.error(500, "获取志愿者参与统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取活动预算执行统计
     * 
     * @return 预算执行统计信息
     */
    @GetMapping("/statistics/budget")
    public Result<List<Map<String, Object>>> getBudgetExecutionStats() {
        logger.info("获取活动预算执行统计请求");
        
        try {
            List<Map<String, Object>> statistics = charityActivityService.getBudgetExecutionStats();
            
            logger.info("成功获取活动预算执行统计 - 统计项数量: {}", statistics.size());
            return Result.success(statistics);
            
        } catch (Exception e) {
            logger.error("获取活动预算执行统计失败", e);
            return Result.error(500, "获取活动预算执行统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取活动地区分布统计
     * 
     * @return 地区分布统计信息
     */
    @GetMapping("/statistics/location")
    public Result<List<Map<String, Object>>> getLocationStatistics() {
        logger.info("获取活动地区分布统计请求");
        
        try {
            List<Map<String, Object>> statistics = charityActivityService.getLocationStatistics();
            
            logger.info("成功获取活动地区分布统计 - 统计项数量: {}", statistics.size());
            return Result.success(statistics);
            
        } catch (Exception e) {
            logger.error("获取活动地区分布统计失败", e);
            return Result.error(500, "获取活动地区分布统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取活动参与人数统计
     * 
     * @return 参与人数统计信息
     */
    @GetMapping("/statistics/participants")
    public Result<List<Map<String, Object>>> getParticipantStatistics() {
        logger.info("获取活动参与人数统计请求");
        
        try {
            List<Map<String, Object>> statistics = charityActivityService.getParticipantStatistics();
            
            logger.info("成功获取活动参与人数统计 - 统计项数量: {}", statistics.size());
            return Result.success(statistics);
            
        } catch (Exception e) {
            logger.error("获取活动参与人数统计失败", e);
            return Result.error(500, "获取活动参与人数统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 从HTTP请求中获取用户ID
     * 实际项目中应该从JWT token或session中获取
     * 
     * @param request HTTP请求对象
     * @return 用户ID
     */
    private Long getUserIdFromRequest(HttpServletRequest request) {
        // 临时实现：从请求头中获取用户ID
        String userIdHeader = request.getHeader("X-User-Id");
        if (userIdHeader != null && !userIdHeader.trim().isEmpty()) {
            try {
                return Long.parseLong(userIdHeader);
            } catch (NumberFormatException e) {
                logger.warn("无效的用户ID格式: {}", userIdHeader);
            }
        }
        
        // 默认返回管理员用户ID（实际项目中应该抛出异常或重定向到登录页面）
        return 1L;
    }
}