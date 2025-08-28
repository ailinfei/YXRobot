package com.yxrobot.service;

import com.yxrobot.dto.CharityActivityDTO;
import com.yxrobot.dto.PageResult;
import com.yxrobot.entity.CharityActivity;
import com.yxrobot.exception.CharityException;
import com.yxrobot.mapper.CharityActivityMapper;
import com.yxrobot.mapper.CharityProjectMapper;
import com.yxrobot.validation.CharityDataValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 公益活动管理业务服务类
 * 负责处理公益活动相关的业务逻辑，包括CRUD操作、查询和统计分析
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-18
 */
@Service
@Transactional
public class CharityActivityService {
    
    private static final Logger logger = LoggerFactory.getLogger(CharityActivityService.class);
    
    @Autowired
    private CharityActivityMapper charityActivityMapper;
    
    @Autowired
    private CharityDataValidator charityDataValidator;
    
    @Autowired
    private CharityProjectMapper charityProjectMapper;
    

    
    @Autowired
    private com.yxrobot.util.QueryOptimizer queryOptimizer;
    
    /**
     * 获取公益活动列表
     * 支持分页查询、搜索和多条件筛选
     * 
     * @param keyword 搜索关键词（活动标题、组织方、地点）
     * @param type 活动类型
     * @param status 活动状态
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param page 页码，从1开始
     * @param size 每页数量
     * @return 分页活动列表
     */
    @Transactional(readOnly = true)
    public PageResult<CharityActivityDTO> getCharityActivities(String keyword, String type, String status,
                                                              LocalDate startDate, LocalDate endDate,
                                                              Integer page, Integer size) {
        logger.info("获取公益活动列表，关键词: {}, 类型: {}, 状态: {}, 开始日期: {}, 结束日期: {}, 页码: {}, 每页数量: {}", 
                   keyword, type, status, startDate, endDate, page, size);
        
        // 使用查询优化器优化分页参数
        Map<String, Integer> paginationParams = queryOptimizer.optimizePagination(page, size);
        page = paginationParams.get("page");
        size = paginationParams.get("size");
        Integer offset = paginationParams.get("offset");
        
        // 优化搜索关键词
        keyword = queryOptimizer.optimizeKeyword(keyword);
        
        try {
            // 查询活动列表
            List<CharityActivity> activities = charityActivityMapper.selectByQuery(
                keyword, type, status, startDate, endDate, offset, size);
            
            // 查询总数
            Long total = charityActivityMapper.countByQuery(keyword, type, status, startDate, endDate);
            
            logger.info("查询到 {} 条活动记录，总数: {}", activities.size(), total);
            
            // 转换为DTO
            List<CharityActivityDTO> activityDTOs = activities.stream()
                    .map(this::convertToCharityActivityDTO)
                    .collect(Collectors.toList());
            
            return PageResult.of(activityDTOs, total, page, size);
            
        } catch (Exception e) {
            logger.error("获取公益活动列表失败", e);
            throw new RuntimeException("获取公益活动列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取活动详情
     * 
     * @param id 活动ID
     * @return 活动详情，如果不存在返回null
     */
    @Transactional(readOnly = true)
    public CharityActivityDTO getCharityActivityById(Long id) {
        logger.info("获取公益活动详情，ID: {}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("活动ID不能为空");
        }
        
        try {

            
            CharityActivity activity = charityActivityMapper.selectById(id);
            if (activity == null) {
                logger.warn("公益活动不存在，ID: {}", id);
                return null;
            }
            
            CharityActivityDTO activityDTO = convertToCharityActivityDTO(activity);
            
            logger.info("成功获取公益活动详情: {}", activity.getTitle());
            
            return activityDTO;
            
        } catch (Exception e) {
            logger.error("获取公益活动详情失败，ID: {}", id, e);
            throw new RuntimeException("获取公益活动详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据项目ID获取活动列表
     * 
     * @param projectId 项目ID
     * @return 活动列表
     */
    @Transactional(readOnly = true)
    public List<CharityActivityDTO> getActivitiesByProjectId(Long projectId) {
        logger.info("根据项目ID获取活动列表，项目ID: {}", projectId);
        
        if (projectId == null) {
            throw new IllegalArgumentException("项目ID不能为空");
        }
        
        try {
            List<CharityActivity> activities = charityActivityMapper.selectByProjectId(projectId);
            
            List<CharityActivityDTO> activityDTOs = activities.stream()
                    .map(this::convertToCharityActivityDTO)
                    .collect(Collectors.toList());
            
            logger.info("获取到 {} 个项目相关活动", activityDTOs.size());
            return activityDTOs;
            
        } catch (Exception e) {
            logger.error("根据项目ID获取活动列表失败，项目ID: {}", projectId, e);
            throw new RuntimeException("获取项目活动列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建新的公益活动
     * 
     * @param activityDTO 活动信息DTO
     * @param createBy 创建人ID
     * @return 创建的活动信息DTO
     */
    public CharityActivityDTO createCharityActivity(CharityActivityDTO activityDTO, Long createBy) {
        logger.info("创建新的公益活动: {}", activityDTO.getTitle());
        
        // 参数验证
        validateCharityActivityDTO(activityDTO);
        validateCreateBy(createBy);
        
        // 验证关联的项目是否存在
        if (activityDTO.getProjectId() != null) {
            if (charityProjectMapper.selectById(activityDTO.getProjectId()) == null) {
                throw new IllegalArgumentException("关联的项目不存在，项目ID: " + activityDTO.getProjectId());
            }
        }
        
        try {
            // 转换为实体对象
            CharityActivity activity = convertToCharityActivity(activityDTO);
            activity.setCreateTime(LocalDateTime.now());
            activity.setUpdateTime(LocalDateTime.now());
            activity.setCreateBy(createBy);
            activity.setUpdateBy(createBy);
            activity.setDeleted(0);
            
            // 插入数据库
            int result = charityActivityMapper.insert(activity);
            if (result <= 0) {
                throw new RuntimeException("创建公益活动失败");
            }
            
            logger.info("成功创建公益活动，ID: {}, 标题: {}", activity.getId(), activity.getTitle());
            return convertToCharityActivityDTO(activity);
            
        } catch (Exception e) {
            logger.error("创建公益活动失败", e);
            throw new RuntimeException("创建公益活动失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新活动信息
     * 
     * @param id 活动ID
     * @param activityDTO 活动信息DTO
     * @param updateBy 更新人ID
     * @return 更新后的活动信息DTO
     */
    public CharityActivityDTO updateCharityActivity(Long id, CharityActivityDTO activityDTO, Long updateBy) {
        logger.info("更新公益活动信息，ID: {}, 标题: {}", id, activityDTO.getTitle());
        
        if (id == null) {
            throw new IllegalArgumentException("活动ID不能为空");
        }
        
        // 参数验证
        validateCharityActivityDTO(activityDTO);
        validateUpdateBy(updateBy);
        
        // 检查活动是否存在
        CharityActivity existingActivity = charityActivityMapper.selectById(id);
        if (existingActivity == null) {
            throw new IllegalArgumentException("公益活动不存在，ID: " + id);
        }
        
        // 验证关联的项目是否存在
        if (activityDTO.getProjectId() != null) {
            if (charityProjectMapper.selectById(activityDTO.getProjectId()) == null) {
                throw new IllegalArgumentException("关联的项目不存在，项目ID: " + activityDTO.getProjectId());
            }
        }
        
        try {
            // 转换为实体对象
            CharityActivity activity = convertToCharityActivity(activityDTO);
            activity.setId(id);
            activity.setUpdateTime(LocalDateTime.now());
            activity.setUpdateBy(updateBy);
            // 保留创建信息
            activity.setCreateTime(existingActivity.getCreateTime());
            activity.setCreateBy(existingActivity.getCreateBy());
            activity.setDeleted(existingActivity.getDeleted());
            
            // 更新数据库
            int result = charityActivityMapper.updateById(activity);
            if (result <= 0) {
                throw new RuntimeException("更新公益活动失败");
            }
            
            logger.info("成功更新公益活动，ID: {}, 标题: {}", id, activity.getTitle());
            return convertToCharityActivityDTO(activity);
            
        } catch (Exception e) {
            logger.error("更新公益活动失败，ID: {}", id, e);
            throw new RuntimeException("更新公益活动失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除活动（软删除）
     * 
     * @param id 活动ID
     */
    public void deleteCharityActivity(Long id) {
        logger.info("删除公益活动，ID: {}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("活动ID不能为空");
        }
        
        // 检查活动是否存在
        CharityActivity activity = charityActivityMapper.selectById(id);
        if (activity == null) {
            throw new IllegalArgumentException("公益活动不存在，ID: " + id);
        }
        
        try {
            // 执行软删除
            int result = charityActivityMapper.deleteById(id);
            if (result <= 0) {
                throw new RuntimeException("删除公益活动失败");
            }
            
            logger.info("成功删除公益活动，ID: {}, 标题: {}", id, activity.getTitle());
            
        } catch (Exception e) {
            logger.error("删除公益活动失败，ID: {}", id, e);
            throw new RuntimeException("删除公益活动失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量删除活动（软删除）
     * 
     * @param ids 活动ID列表
     * @return 删除的活动数量
     */
    public int batchDeleteCharityActivities(List<Long> ids) {
        logger.info("批量删除公益活动，数量: {}", ids.size());
        
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("活动ID列表不能为空");
        }
        
        try {
            // 执行批量软删除
            int result = charityActivityMapper.batchDeleteByIds(ids);
            
            logger.info("成功批量删除公益活动，删除数量: {}", result);
            return result;
            
        } catch (Exception e) {
            logger.error("批量删除公益活动失败", e);
            throw new RuntimeException("批量删除公益活动失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新活动状态
     * 
     * @param id 活动ID
     * @param status 新状态
     */
    public void updateActivityStatus(Long id, String status) {
        logger.info("更新活动状态，ID: {}, 状态: {}", id, status);
        
        if (id == null) {
            throw new IllegalArgumentException("活动ID不能为空");
        }
        
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("活动状态不能为空");
        }
        
        // 验证状态值
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("无效的活动状态: " + status);
        }
        
        try {
            int result = charityActivityMapper.updateStatus(id, status);
            if (result <= 0) {
                throw new RuntimeException("更新活动状态失败");
            }
            
            logger.info("成功更新活动状态，ID: {}", id);
            
        } catch (Exception e) {
            logger.error("更新活动状态失败，ID: {}", id, e);
            throw new RuntimeException("更新活动状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量更新活动状态
     * 
     * @param ids 活动ID列表
     * @param status 新状态
     * @return 更新的活动数量
     */
    public int batchUpdateStatus(List<Long> ids, String status) {
        logger.info("批量更新活动状态，数量: {}, 状态: {}", ids.size(), status);
        
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("活动ID列表不能为空");
        }
        
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("活动状态不能为空");
        }
        
        // 验证状态值
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("无效的活动状态: " + status);
        }
        
        try {
            int result = charityActivityMapper.batchUpdateStatus(ids, status);
            
            logger.info("成功批量更新活动状态，更新数量: {}", result);
            return result;
            
        } catch (Exception e) {
            logger.error("批量更新活动状态失败", e);
            throw new RuntimeException("批量更新活动状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取即将开始的活动
     * 
     * @param days 天数范围
     * @return 即将开始的活动列表
     */
    @Transactional(readOnly = true)
    public List<CharityActivityDTO> getUpcomingActivities(Integer days) {
        logger.info("获取即将开始的活动，天数范围: {}", days);
        
        if (days == null || days <= 0) {
            days = 7; // 默认7天
        }
        
        try {
            List<CharityActivity> activities = charityActivityMapper.selectUpcomingActivities(days);
            
            List<CharityActivityDTO> activityDTOs = activities.stream()
                    .map(this::convertToCharityActivityDTO)
                    .collect(Collectors.toList());
            
            logger.info("获取到 {} 个即将开始的活动", activityDTOs.size());
            return activityDTOs;
            
        } catch (Exception e) {
            logger.error("获取即将开始的活动失败", e);
            throw new RuntimeException("获取即将开始的活动失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取正在进行的活动
     * 
     * @return 正在进行的活动列表
     */
    @Transactional(readOnly = true)
    public List<CharityActivityDTO> getOngoingActivities() {
        logger.info("获取正在进行的活动");
        
        try {
            List<CharityActivity> activities = charityActivityMapper.selectOngoingActivities();
            
            List<CharityActivityDTO> activityDTOs = activities.stream()
                    .map(this::convertToCharityActivityDTO)
                    .collect(Collectors.toList());
            
            logger.info("获取到 {} 个正在进行的活动", activityDTOs.size());
            return activityDTOs;
            
        } catch (Exception e) {
            logger.error("获取正在进行的活动失败", e);
            throw new RuntimeException("获取正在进行的活动失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取最近完成的活动
     * 
     * @param limit 限制返回数量
     * @return 最近完成的活动列表
     */
    @Transactional(readOnly = true)
    public List<CharityActivityDTO> getRecentCompletedActivities(Integer limit) {
        logger.info("获取最近完成的活动，限制数量: {}", limit);
        
        if (limit == null || limit <= 0) {
            limit = 10; // 默认10个
        }
        
        try {
            List<CharityActivity> activities = charityActivityMapper.selectRecentCompletedActivities(limit);
            
            List<CharityActivityDTO> activityDTOs = activities.stream()
                    .map(this::convertToCharityActivityDTO)
                    .collect(Collectors.toList());
            
            logger.info("获取到 {} 个最近完成的活动", activityDTOs.size());
            return activityDTOs;
            
        } catch (Exception e) {
            logger.error("获取最近完成的活动失败", e);
            throw new RuntimeException("获取最近完成的活动失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取活动状态统计
     * 
     * @return 状态统计信息
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getStatusStatistics() {
        logger.info("获取活动状态统计");
        
        try {
            List<Map<String, Object>> statistics = charityActivityMapper.getStatusStatistics();
            logger.info("获取到 {} 种状态的统计信息", statistics.size());
            return statistics;
            
        } catch (Exception e) {
            logger.error("获取活动状态统计失败", e);
            throw new RuntimeException("获取活动状态统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取活动类型统计
     * 
     * @return 类型统计信息
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTypeStatistics() {
        logger.info("获取活动类型统计");
        
        try {
            List<Map<String, Object>> statistics = charityActivityMapper.getTypeStatistics();
            logger.info("获取到 {} 种类型的统计信息", statistics.size());
            return statistics;
            
        } catch (Exception e) {
            logger.error("获取活动类型统计失败", e);
            throw new RuntimeException("获取活动类型统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取月度活动统计
     * 
     * @param months 月份数量
     * @return 月度活动统计信息
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMonthlyStatistics(Integer months) {
        logger.info("获取月度活动统计，月份数量: {}", months);
        
        if (months == null || months <= 0) {
            months = 12; // 默认12个月
        }
        
        try {
            List<Map<String, Object>> statistics = charityActivityMapper.getMonthlyStatistics(months);
            logger.info("获取到 {} 个月的活动统计信息", statistics.size());
            return statistics;
            
        } catch (Exception e) {
            logger.error("获取月度活动统计失败", e);
            throw new RuntimeException("获取月度活动统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取志愿者参与统计
     * 
     * @param months 月份数量
     * @return 志愿者参与统计信息
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getVolunteerParticipationStats(Integer months) {
        logger.info("获取志愿者参与统计，月份数量: {}", months);
        
        if (months == null || months <= 0) {
            months = 6; // 默认6个月
        }
        
        try {
            List<Map<String, Object>> statistics = charityActivityMapper.getVolunteerParticipationStats(months);
            logger.info("获取到 {} 个月的志愿者参与统计", statistics.size());
            return statistics;
            
        } catch (Exception e) {
            logger.error("获取志愿者参与统计失败", e);
            throw new RuntimeException("获取志愿者参与统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取活动预算执行统计
     * 
     * @return 预算执行统计信息
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getBudgetExecutionStats() {
        logger.info("获取活动预算执行统计");
        
        try {
            List<Map<String, Object>> statistics = charityActivityMapper.getBudgetExecutionStats();
            logger.info("获取到 {} 种类型的预算执行统计", statistics.size());
            return statistics;
            
        } catch (Exception e) {
            logger.error("获取活动预算执行统计失败", e);
            throw new RuntimeException("获取活动预算执行统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取活动地区分布统计
     * 
     * @return 地区分布统计信息
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getLocationStatistics() {
        logger.info("获取活动地区分布统计");
        
        try {
            List<Map<String, Object>> statistics = charityActivityMapper.getLocationStatistics();
            logger.info("获取到 {} 个地区的统计信息", statistics.size());
            return statistics;
            
        } catch (Exception e) {
            logger.error("获取活动地区分布统计失败", e);
            throw new RuntimeException("获取活动地区分布统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取活动参与人数统计
     * 
     * @return 参与人数统计信息
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getParticipantStatistics() {
        logger.info("获取活动参与人数统计");
        
        try {
            List<Map<String, Object>> statistics = charityActivityMapper.getParticipantStatistics();
            logger.info("获取到 {} 个参与人数区间的统计信息", statistics.size());
            return statistics;
            
        } catch (Exception e) {
            logger.error("获取活动参与人数统计失败", e);
            throw new RuntimeException("获取活动参与人数统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 验证活动DTO参数
     * 
     * @param activityDTO 活动DTO
     */
    private void validateCharityActivityDTO(CharityActivityDTO activityDTO) {
        if (activityDTO == null) {
            throw new IllegalArgumentException("活动信息不能为空");
        }
        
        if (activityDTO.getTitle() == null || activityDTO.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("活动标题不能为空");
        }
        
        if (activityDTO.getTitle().length() > 100) {
            throw new IllegalArgumentException("活动标题长度不能超过100个字符");
        }
        
        if (activityDTO.getDescription() == null || activityDTO.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("活动描述不能为空");
        }
        
        if (activityDTO.getDescription().length() > 1000) {
            throw new IllegalArgumentException("活动描述长度不能超过1000个字符");
        }
        
        if (activityDTO.getType() == null || activityDTO.getType().trim().isEmpty()) {
            throw new IllegalArgumentException("活动类型不能为空");
        }
        
        if (!isValidType(activityDTO.getType())) {
            throw new IllegalArgumentException("无效的活动类型: " + activityDTO.getType());
        }
        
        if (activityDTO.getDate() == null) {
            throw new IllegalArgumentException("活动日期不能为空");
        }
        
        if (activityDTO.getLocation() == null || activityDTO.getLocation().trim().isEmpty()) {
            throw new IllegalArgumentException("活动地点不能为空");
        }
        
        if (activityDTO.getLocation().length() > 200) {
            throw new IllegalArgumentException("活动地点长度不能超过200个字符");
        }
        
        if (activityDTO.getParticipants() == null || activityDTO.getParticipants() < 0) {
            throw new IllegalArgumentException("参与人数不能为空且必须大于等于0");
        }
        
        if (activityDTO.getTargetParticipants() != null && activityDTO.getTargetParticipants() < 0) {
            throw new IllegalArgumentException("目标参与人数不能为负数");
        }
        
        if (activityDTO.getOrganizer() == null || activityDTO.getOrganizer().trim().isEmpty()) {
            throw new IllegalArgumentException("组织方不能为空");
        }
        
        if (activityDTO.getOrganizer().length() > 100) {
            throw new IllegalArgumentException("组织方长度不能超过100个字符");
        }
        
        if (activityDTO.getStatus() == null || activityDTO.getStatus().trim().isEmpty()) {
            throw new IllegalArgumentException("活动状态不能为空");
        }
        
        if (!isValidStatus(activityDTO.getStatus())) {
            throw new IllegalArgumentException("无效的活动状态: " + activityDTO.getStatus());
        }
        
        if (activityDTO.getBudget() == null || activityDTO.getBudget().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("活动预算不能为空且必须大于等于0");
        }
        
        if (activityDTO.getActualCost() != null && activityDTO.getActualCost().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("实际费用不能为负数");
        }
        
        if (activityDTO.getManager() == null || activityDTO.getManager().trim().isEmpty()) {
            throw new IllegalArgumentException("活动负责人不能为空");
        }
        
        if (activityDTO.getManager().length() > 50) {
            throw new IllegalArgumentException("活动负责人长度不能超过50个字符");
        }
        
        if (activityDTO.getManagerContact() != null && !activityDTO.getManagerContact().trim().isEmpty()) {
            if (!isValidPhone(activityDTO.getManagerContact())) {
                throw new IllegalArgumentException("负责人联系方式格式不正确");
            }
        }
        
        if (activityDTO.getVolunteerNeeded() != null && activityDTO.getVolunteerNeeded() < 0) {
            throw new IllegalArgumentException("志愿者需求数量不能为负数");
        }
        
        if (activityDTO.getActualVolunteers() != null && activityDTO.getActualVolunteers() < 0) {
            throw new IllegalArgumentException("实际志愿者数量不能为负数");
        }
        
        if (activityDTO.getAchievements() != null && activityDTO.getAchievements().length() > 500) {
            throw new IllegalArgumentException("活动成果描述长度不能超过500个字符");
        }
        
        if (activityDTO.getPhotos() != null && activityDTO.getPhotos().length > 10) {
            throw new IllegalArgumentException("活动照片数量不能超过10张");
        }
        
        if (activityDTO.getNotes() != null && activityDTO.getNotes().length() > 500) {
            throw new IllegalArgumentException("备注信息长度不能超过500个字符");
        }
        
        // 验证时间逻辑
        if (activityDTO.getStartTime() != null && activityDTO.getEndTime() != null) {
            if (activityDTO.getStartTime().isAfter(activityDTO.getEndTime())) {
                throw new IllegalArgumentException("活动开始时间不能晚于结束时间");
            }
        }
    }
    
    /**
     * 验证创建人ID
     * 
     * @param createBy 创建人ID
     */
    private void validateCreateBy(Long createBy) {
        if (createBy == null) {
            throw new IllegalArgumentException("创建人ID不能为空");
        }
    }
    
    /**
     * 验证更新人ID
     * 
     * @param updateBy 更新人ID
     */
    private void validateUpdateBy(Long updateBy) {
        if (updateBy == null) {
            throw new IllegalArgumentException("更新人ID不能为空");
        }
    }
    
    /**
     * 验证活动类型是否有效
     * 
     * @param type 活动类型
     * @return true-有效，false-无效
     */
    private boolean isValidType(String type) {
        return "visit".equals(type) || "training".equals(type) || "donation".equals(type) 
               || "event".equals(type) || "volunteer".equals(type) || "fundraising".equals(type) 
               || "education".equals(type) || "medical".equals(type) || "other".equals(type);
    }
    
    /**
     * 验证活动状态是否有效
     * 
     * @param status 活动状态
     * @return true-有效，false-无效
     */
    private boolean isValidStatus(String status) {
        return "planned".equals(status) || "ongoing".equals(status) || "completed".equals(status) 
               || "cancelled".equals(status) || "postponed".equals(status);
    }
    
    /**
     * 验证电话号码格式
     * 
     * @param phone 电话号码
     * @return true-有效，false-无效
     */
    private boolean isValidPhone(String phone) {
        // 简单的电话号码格式验证
        return phone.matches("^1[3-9]\\d{9}$|^0\\d{2,3}-?\\d{7,8}$");
    }
    
    /**
     * 将CharityActivity实体转换为CharityActivityDTO
     * 
     * @param activity 活动实体
     * @return 活动DTO
     */
    private CharityActivityDTO convertToCharityActivityDTO(CharityActivity activity) {
        if (activity == null) {
            return null;
        }
        
        CharityActivityDTO dto = new CharityActivityDTO();
        dto.setId(activity.getId());
        dto.setProjectId(activity.getProjectId());
        dto.setTitle(activity.getTitle());
        dto.setDescription(activity.getDescription());
        dto.setType(activity.getType());
        dto.setDate(activity.getDate());
        dto.setStartTime(activity.getStartTime());
        dto.setEndTime(activity.getEndTime());
        dto.setLocation(activity.getLocation());
        dto.setParticipants(activity.getParticipants());
        dto.setTargetParticipants(activity.getTargetParticipants());
        dto.setOrganizer(activity.getOrganizer());
        dto.setStatus(activity.getStatus());
        dto.setBudget(activity.getBudget());
        dto.setActualCost(activity.getActualCost());
        dto.setManager(activity.getManager());
        dto.setManagerContact(activity.getManagerContact());
        dto.setVolunteerNeeded(activity.getVolunteerNeeded());
        dto.setActualVolunteers(activity.getActualVolunteers());
        dto.setAchievements(activity.getAchievements());
        // Set photos array directly
        dto.setPhotos(activity.getPhotos());
        dto.setNotes(activity.getNotes());
        dto.setCreateTime(activity.getCreateTime());
        dto.setUpdateTime(activity.getUpdateTime());
        // dto.setCreateBy(activity.getCreateBy());
        // dto.setUpdateBy(activity.getUpdateBy());
        
        return dto;
    }
    
    /**
     * 将CharityActivityDTO转换为CharityActivity实体
     * 
     * @param activityDTO 活动DTO
     * @return 活动实体
     */
    private CharityActivity convertToCharityActivity(CharityActivityDTO activityDTO) {
        if (activityDTO == null) {
            return null;
        }
        
        CharityActivity activity = new CharityActivity();
        activity.setId(activityDTO.getId());
        activity.setProjectId(activityDTO.getProjectId());
        activity.setTitle(activityDTO.getTitle());
        activity.setDescription(activityDTO.getDescription());
        activity.setType(activityDTO.getType());
        activity.setDate(activityDTO.getDate());
        activity.setStartTime(activityDTO.getStartTime());
        activity.setEndTime(activityDTO.getEndTime());
        activity.setLocation(activityDTO.getLocation());
        activity.setParticipants(activityDTO.getParticipants());
        activity.setTargetParticipants(activityDTO.getTargetParticipants());
        activity.setOrganizer(activityDTO.getOrganizer());
        activity.setStatus(activityDTO.getStatus());
        activity.setBudget(activityDTO.getBudget());
        activity.setActualCost(activityDTO.getActualCost());
        activity.setManager(activityDTO.getManager());
        activity.setManagerContact(activityDTO.getManagerContact());
        activity.setVolunteerNeeded(activityDTO.getVolunteerNeeded());
        activity.setActualVolunteers(activityDTO.getActualVolunteers());
        activity.setAchievements(activityDTO.getAchievements());
        activity.setPhotos(activityDTO.getPhotos());
        activity.setNotes(activityDTO.getNotes());
        activity.setCreateTime(activityDTO.getCreateTime());
        activity.setUpdateTime(activityDTO.getUpdateTime());
        // activity.setCreateBy(activityDTO.getCreateBy()); // Method not found
        // activity.setUpdateBy(activityDTO.getUpdateBy()); // Method not found
        
        return activity;
    }
}