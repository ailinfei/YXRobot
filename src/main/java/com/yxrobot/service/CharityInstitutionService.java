package com.yxrobot.service;

import com.yxrobot.dto.CharityInstitutionDTO;
import com.yxrobot.dto.PageResult;
import com.yxrobot.entity.CharityInstitution;
import com.yxrobot.exception.CharityException;
import com.yxrobot.mapper.CharityInstitutionMapper;
import com.yxrobot.validation.CharityDataValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 合作机构管理业务服务类
 * 负责处理合作机构相关的业务逻辑，包括CRUD操作、查询和统计分析
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-18
 */
@Service
@Transactional
public class CharityInstitutionService {
    
    private static final Logger logger = LoggerFactory.getLogger(CharityInstitutionService.class);
    
    @Autowired
    private CharityInstitutionMapper charityInstitutionMapper;
    
    @Autowired
    private CharityDataValidator charityDataValidator;
    

    
    @Autowired
    private com.yxrobot.util.QueryOptimizer queryOptimizer;
    
    /**
     * 获取合作机构列表
     * 支持分页查询、搜索和多条件筛选
     * 
     * @param keyword 搜索关键词（机构名称、联系人、地区）
     * @param type 机构类型
     * @param status 机构状态
     * @param page 页码，从1开始
     * @param size 每页数量
     * @return 分页机构列表
     */
    @Transactional(readOnly = true)
    public PageResult<CharityInstitutionDTO> getCharityInstitutions(String keyword, String type, String status, 
                                                                   Integer page, Integer size) {
        logger.info("获取合作机构列表，关键词: {}, 类型: {}, 状态: {}, 页码: {}, 每页数量: {}", 
                   keyword, type, status, page, size);
        
        // 使用查询优化器优化分页参数
        Map<String, Integer> paginationParams = queryOptimizer.optimizePagination(page, size);
        page = paginationParams.get("page");
        size = paginationParams.get("size");
        Integer offset = paginationParams.get("offset");
        
        // 优化搜索关键词
        keyword = queryOptimizer.optimizeKeyword(keyword);
        
        try {
            // 查询机构列表
            List<CharityInstitution> institutions = charityInstitutionMapper.selectByQuery(
                keyword, type, status, offset, size);
            
            // 查询总数
            Long total = charityInstitutionMapper.countByQuery(keyword, type, status);
            
            logger.info("查询到 {} 条机构记录，总数: {}", institutions.size(), total);
            
            // 转换为DTO
            List<CharityInstitutionDTO> institutionDTOs = institutions.stream()
                    .map(this::convertToCharityInstitutionDTO)
                    .collect(Collectors.toList());
            
            return PageResult.of(institutionDTOs, total, page, size);
            
        } catch (Exception e) {
            logger.error("获取合作机构列表失败", e);
            throw new RuntimeException("获取合作机构列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取机构详情
     * 
     * @param id 机构ID
     * @return 机构详情，如果不存在返回null
     */
    @Transactional(readOnly = true)
    public CharityInstitutionDTO getCharityInstitutionById(Long id) {
        logger.info("获取合作机构详情，ID: {}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("机构ID不能为空");
        }
        
        try {

            
            CharityInstitution institution = charityInstitutionMapper.selectById(id);
            if (institution == null) {
                logger.warn("合作机构不存在，ID: {}", id);
                return null;
            }
            
            CharityInstitutionDTO institutionDTO = convertToCharityInstitutionDTO(institution);
            
            logger.info("成功获取合作机构详情: {}", institution.getName());
            
            return institutionDTO;
            
        } catch (Exception e) {
            logger.error("获取合作机构详情失败，ID: {}", id, e);
            throw new RuntimeException("获取合作机构详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建新的合作机构
     * 
     * @param institutionDTO 机构信息DTO
     * @param createBy 创建人ID
     * @return 创建的机构信息DTO
     */
    public CharityInstitutionDTO createCharityInstitution(CharityInstitutionDTO institutionDTO, Long createBy) {
        logger.info("创建新的合作机构: {}", institutionDTO.getName());
        
        // 参数验证
        validateCharityInstitutionDTO(institutionDTO);
        validateCreateBy(createBy);
        
        // 检查机构名称唯一性
        if (charityInstitutionMapper.existsByName(institutionDTO.getName())) {
            throw new IllegalArgumentException("机构名称已存在: " + institutionDTO.getName());
        }
        
        try {
            // 转换为实体对象
            CharityInstitution institution = convertToCharityInstitution(institutionDTO);
            institution.setCreateTime(LocalDateTime.now());
            institution.setUpdateTime(LocalDateTime.now());
            institution.setCreateBy(createBy);
            institution.setUpdateBy(createBy);
            institution.setDeleted(0);
            
            // 插入数据库
            int result = charityInstitutionMapper.insert(institution);
            if (result <= 0) {
                throw new RuntimeException("创建合作机构失败");
            }
            
            logger.info("成功创建合作机构，ID: {}, 名称: {}", institution.getId(), institution.getName());
            return convertToCharityInstitutionDTO(institution);
            
        } catch (Exception e) {
            logger.error("创建合作机构失败", e);
            throw new RuntimeException("创建合作机构失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新机构信息
     * 
     * @param id 机构ID
     * @param institutionDTO 机构信息DTO
     * @param updateBy 更新人ID
     * @return 更新后的机构信息DTO
     */
    public CharityInstitutionDTO updateCharityInstitution(Long id, CharityInstitutionDTO institutionDTO, Long updateBy) {
        logger.info("更新合作机构信息，ID: {}, 名称: {}", id, institutionDTO.getName());
        
        if (id == null) {
            throw new IllegalArgumentException("机构ID不能为空");
        }
        
        // 参数验证
        validateCharityInstitutionDTO(institutionDTO);
        validateUpdateBy(updateBy);
        
        // 检查机构是否存在
        CharityInstitution existingInstitution = charityInstitutionMapper.selectById(id);
        if (existingInstitution == null) {
            throw new IllegalArgumentException("合作机构不存在，ID: " + id);
        }
        
        // 检查机构名称唯一性（排除当前机构）
        if (charityInstitutionMapper.existsByNameExcludeId(institutionDTO.getName(), id)) {
            throw new IllegalArgumentException("机构名称已存在: " + institutionDTO.getName());
        }
        
        try {
            // 转换为实体对象
            CharityInstitution institution = convertToCharityInstitution(institutionDTO);
            institution.setId(id);
            institution.setUpdateTime(LocalDateTime.now());
            institution.setUpdateBy(updateBy);
            // 保留创建信息
            institution.setCreateTime(existingInstitution.getCreateTime());
            institution.setCreateBy(existingInstitution.getCreateBy());
            institution.setDeleted(existingInstitution.getDeleted());
            
            // 更新数据库
            int result = charityInstitutionMapper.updateById(institution);
            if (result <= 0) {
                throw new RuntimeException("更新合作机构失败");
            }
            
            logger.info("成功更新合作机构，ID: {}, 名称: {}", id, institution.getName());
            return convertToCharityInstitutionDTO(institution);
            
        } catch (Exception e) {
            logger.error("更新合作机构失败，ID: {}", id, e);
            throw new RuntimeException("更新合作机构失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除机构（软删除）
     * 
     * @param id 机构ID
     */
    public void deleteCharityInstitution(Long id) {
        logger.info("删除合作机构，ID: {}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("机构ID不能为空");
        }
        
        // 检查机构是否存在
        CharityInstitution institution = charityInstitutionMapper.selectById(id);
        if (institution == null) {
            throw new IllegalArgumentException("合作机构不存在，ID: " + id);
        }
        
        try {
            // 执行软删除
            int result = charityInstitutionMapper.deleteById(id);
            if (result <= 0) {
                throw new RuntimeException("删除合作机构失败");
            }
            
            logger.info("成功删除合作机构，ID: {}, 名称: {}", id, institution.getName());
            
        } catch (Exception e) {
            logger.error("删除合作机构失败，ID: {}", id, e);
            throw new RuntimeException("删除合作机构失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量删除机构（软删除）
     * 
     * @param ids 机构ID列表
     * @return 删除的机构数量
     */
    public int batchDeleteCharityInstitutions(List<Long> ids) {
        logger.info("批量删除合作机构，数量: {}", ids.size());
        
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("机构ID列表不能为空");
        }
        
        try {
            // 执行批量软删除
            int result = charityInstitutionMapper.batchDeleteByIds(ids);
            
            logger.info("成功批量删除合作机构，删除数量: {}", result);
            return result;
            
        } catch (Exception e) {
            logger.error("批量删除合作机构失败", e);
            throw new RuntimeException("批量删除合作机构失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新机构最后访问日期
     * 
     * @param id 机构ID
     * @param lastVisitDate 最后访问日期
     */
    public void updateLastVisitDate(Long id, LocalDate lastVisitDate) {
        logger.info("更新机构最后访问日期，ID: {}, 日期: {}", id, lastVisitDate);
        
        if (id == null) {
            throw new IllegalArgumentException("机构ID不能为空");
        }
        
        if (lastVisitDate == null) {
            throw new IllegalArgumentException("最后访问日期不能为空");
        }
        
        try {
            int result = charityInstitutionMapper.updateLastVisitDate(id, lastVisitDate);
            if (result <= 0) {
                throw new RuntimeException("更新机构最后访问日期失败");
            }
            
            logger.info("成功更新机构最后访问日期，ID: {}", id);
            
        } catch (Exception e) {
            logger.error("更新机构最后访问日期失败，ID: {}", id, e);
            throw new RuntimeException("更新机构最后访问日期失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量更新机构状态
     * 
     * @param ids 机构ID列表
     * @param status 新状态
     * @return 更新的机构数量
     */
    public int batchUpdateStatus(List<Long> ids, String status) {
        logger.info("批量更新机构状态，数量: {}, 状态: {}", ids.size(), status);
        
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("机构ID列表不能为空");
        }
        
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("机构状态不能为空");
        }
        
        // 验证状态值
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("无效的机构状态: " + status);
        }
        
        try {
            int result = charityInstitutionMapper.batchUpdateStatus(ids, status);
            
            logger.info("成功批量更新机构状态，更新数量: {}", result);
            return result;
            
        } catch (Exception e) {
            logger.error("批量更新机构状态失败", e);
            throw new RuntimeException("批量更新机构状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取活跃机构列表
     * 
     * @return 活跃机构列表
     */
    @Transactional(readOnly = true)
    public List<CharityInstitutionDTO> getActiveInstitutions() {
        logger.info("获取活跃机构列表");
        
        try {
            List<CharityInstitution> institutions = charityInstitutionMapper.selectActiveInstitutions();
            
            List<CharityInstitutionDTO> institutionDTOs = institutions.stream()
                    .map(this::convertToCharityInstitutionDTO)
                    .collect(Collectors.toList());
            
            logger.info("获取到 {} 个活跃机构", institutionDTOs.size());
            return institutionDTOs;
            
        } catch (Exception e) {
            logger.error("获取活跃机构列表失败", e);
            throw new RuntimeException("获取活跃机构列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取最近合作的机构
     * 
     * @param limit 限制返回数量
     * @return 最近合作机构列表
     */
    @Transactional(readOnly = true)
    public List<CharityInstitutionDTO> getRecentCooperations(Integer limit) {
        logger.info("获取最近合作的机构，限制数量: {}", limit);
        
        if (limit == null || limit <= 0) {
            limit = 10; // 默认10个
        }
        
        try {
            List<CharityInstitution> institutions = charityInstitutionMapper.selectRecentCooperations(limit);
            
            List<CharityInstitutionDTO> institutionDTOs = institutions.stream()
                    .map(this::convertToCharityInstitutionDTO)
                    .collect(Collectors.toList());
            
            logger.info("获取到 {} 个最近合作机构", institutionDTOs.size());
            return institutionDTOs;
            
        } catch (Exception e) {
            logger.error("获取最近合作机构失败", e);
            throw new RuntimeException("获取最近合作机构失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取机构状态统计
     * 
     * @return 状态统计信息
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getStatusStatistics() {
        logger.info("获取机构状态统计");
        
        try {
            List<Map<String, Object>> statistics = charityInstitutionMapper.getStatusStatistics();
            logger.info("获取到 {} 种状态的统计信息", statistics.size());
            return statistics;
            
        } catch (Exception e) {
            logger.error("获取机构状态统计失败", e);
            throw new RuntimeException("获取机构状态统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取机构类型统计
     * 
     * @return 类型统计信息
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTypeStatistics() {
        logger.info("获取机构类型统计");
        
        try {
            List<Map<String, Object>> statistics = charityInstitutionMapper.getTypeStatistics();
            logger.info("获取到 {} 种类型的统计信息", statistics.size());
            return statistics;
            
        } catch (Exception e) {
            logger.error("获取机构类型统计失败", e);
            throw new RuntimeException("获取机构类型统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取地区分布统计
     * 
     * @return 地区分布统计信息
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getLocationStatistics() {
        logger.info("获取机构地区分布统计");
        
        try {
            List<Map<String, Object>> statistics = charityInstitutionMapper.getLocationStatistics();
            logger.info("获取到 {} 个地区的统计信息", statistics.size());
            return statistics;
            
        } catch (Exception e) {
            logger.error("获取机构地区分布统计失败", e);
            throw new RuntimeException("获取机构地区分布统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取机构合作时长统计
     * 
     * @return 合作时长统计信息
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getCooperationDurationStats() {
        logger.info("获取机构合作时长统计");
        
        try {
            List<Map<String, Object>> statistics = charityInstitutionMapper.getCooperationDurationStats();
            logger.info("获取到 {} 个机构的合作时长统计", statistics.size());
            return statistics;
            
        } catch (Exception e) {
            logger.error("获取机构合作时长统计失败", e);
            throw new RuntimeException("获取机构合作时长统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 验证机构DTO参数
     * 
     * @param institutionDTO 机构DTO
     */
    private void validateCharityInstitutionDTO(CharityInstitutionDTO institutionDTO) {
        if (institutionDTO == null) {
            throw new IllegalArgumentException("机构信息不能为空");
        }
        
        if (institutionDTO.getName() == null || institutionDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("机构名称不能为空");
        }
        
        if (institutionDTO.getName().length() > 100) {
            throw new IllegalArgumentException("机构名称长度不能超过100个字符");
        }
        
        if (institutionDTO.getType() == null || institutionDTO.getType().trim().isEmpty()) {
            throw new IllegalArgumentException("机构类型不能为空");
        }
        
        if (!isValidType(institutionDTO.getType())) {
            throw new IllegalArgumentException("无效的机构类型: " + institutionDTO.getType());
        }
        
        if (institutionDTO.getLocation() == null || institutionDTO.getLocation().trim().isEmpty()) {
            throw new IllegalArgumentException("所在地区不能为空");
        }
        
        if (institutionDTO.getLocation().length() > 50) {
            throw new IllegalArgumentException("所在地区长度不能超过50个字符");
        }
        
        if (institutionDTO.getAddress() == null || institutionDTO.getAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("详细地址不能为空");
        }
        
        if (institutionDTO.getAddress().length() > 200) {
            throw new IllegalArgumentException("详细地址长度不能超过200个字符");
        }
        
        if (institutionDTO.getContactPerson() == null || institutionDTO.getContactPerson().trim().isEmpty()) {
            throw new IllegalArgumentException("联系人姓名不能为空");
        }
        
        if (institutionDTO.getContactPerson().length() > 50) {
            throw new IllegalArgumentException("联系人姓名长度不能超过50个字符");
        }
        
        if (institutionDTO.getContactPhone() == null || institutionDTO.getContactPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("联系电话不能为空");
        }
        
        if (!isValidPhone(institutionDTO.getContactPhone())) {
            throw new IllegalArgumentException("联系电话格式不正确");
        }
        
        if (institutionDTO.getEmail() != null && !institutionDTO.getEmail().trim().isEmpty()) {
            if (!isValidEmail(institutionDTO.getEmail())) {
                throw new IllegalArgumentException("邮箱格式不正确");
            }
            if (institutionDTO.getEmail().length() > 100) {
                throw new IllegalArgumentException("邮箱地址长度不能超过100个字符");
            }
        }
        
        if (institutionDTO.getStudentCount() != null && institutionDTO.getStudentCount() < 0) {
            throw new IllegalArgumentException("学生数量不能为负数");
        }
        
        if (institutionDTO.getCooperationDate() == null) {
            throw new IllegalArgumentException("合作开始日期不能为空");
        }
        
        if (institutionDTO.getStatus() == null || institutionDTO.getStatus().trim().isEmpty()) {
            throw new IllegalArgumentException("机构状态不能为空");
        }
        
        if (!isValidStatus(institutionDTO.getStatus())) {
            throw new IllegalArgumentException("无效的机构状态: " + institutionDTO.getStatus());
        }
        
        if (institutionDTO.getDeviceCount() != null && institutionDTO.getDeviceCount() < 0) {
            throw new IllegalArgumentException("设备数量不能为负数");
        }
        
        if (institutionDTO.getNotes() != null && institutionDTO.getNotes().length() > 500) {
            throw new IllegalArgumentException("备注信息长度不能超过500个字符");
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
     * 验证机构类型是否有效
     * 
     * @param type 机构类型
     * @return true-有效，false-无效
     */
    private boolean isValidType(String type) {
        return "school".equals(type) || "hospital".equals(type) || "community".equals(type) 
               || "ngo".equals(type) || "other".equals(type);
    }
    
    /**
     * 验证机构状态是否有效
     * 
     * @param status 机构状态
     * @return true-有效，false-无效
     */
    private boolean isValidStatus(String status) {
        return "active".equals(status) || "inactive".equals(status) || "suspended".equals(status);
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
     * 验证邮箱格式
     * 
     * @param email 邮箱地址
     * @return true-有效，false-无效
     */
    private boolean isValidEmail(String email) {
        // 简单的邮箱格式验证
        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }
    
    /**
     * 将CharityInstitution实体转换为CharityInstitutionDTO
     * 
     * @param institution 机构实体
     * @return 机构DTO
     */
    private CharityInstitutionDTO convertToCharityInstitutionDTO(CharityInstitution institution) {
        if (institution == null) {
            return null;
        }
        
        CharityInstitutionDTO dto = new CharityInstitutionDTO();
        dto.setId(institution.getId());
        dto.setName(institution.getName());
        dto.setType(institution.getType());
        dto.setLocation(institution.getLocation());
        dto.setAddress(institution.getAddress());
        dto.setContactPerson(institution.getContactPerson());
        dto.setContactPhone(institution.getContactPhone());
        dto.setEmail(institution.getEmail());
        dto.setStudentCount(institution.getStudentCount());
        dto.setCooperationDate(institution.getCooperationDate());
        dto.setStatus(institution.getStatus());
        dto.setDeviceCount(institution.getDeviceCount());
        dto.setLastVisitDate(institution.getLastVisitDate());
        dto.setNotes(institution.getNotes());
        dto.setCreateTime(institution.getCreateTime());
        dto.setUpdateTime(institution.getUpdateTime());
        // dto.setCreateBy(institution.getCreateBy()); // Method not found
        // dto.setUpdateBy(institution.getUpdateBy()); // Method not found
        
        return dto;
    }
    
    /**
     * 将CharityInstitutionDTO转换为CharityInstitution实体
     * 
     * @param institutionDTO 机构DTO
     * @return 机构实体
     */
    private CharityInstitution convertToCharityInstitution(CharityInstitutionDTO institutionDTO) {
        if (institutionDTO == null) {
            return null;
        }
        
        CharityInstitution institution = new CharityInstitution();
        institution.setId(institutionDTO.getId());
        institution.setName(institutionDTO.getName());
        institution.setType(institutionDTO.getType());
        institution.setLocation(institutionDTO.getLocation());
        institution.setAddress(institutionDTO.getAddress());
        institution.setContactPerson(institutionDTO.getContactPerson());
        institution.setContactPhone(institutionDTO.getContactPhone());
        institution.setEmail(institutionDTO.getEmail());
        institution.setStudentCount(institutionDTO.getStudentCount());
        institution.setCooperationDate(institutionDTO.getCooperationDate());
        institution.setStatus(institutionDTO.getStatus());
        institution.setDeviceCount(institutionDTO.getDeviceCount());
        institution.setLastVisitDate(institutionDTO.getLastVisitDate());
        institution.setNotes(institutionDTO.getNotes());
        institution.setCreateTime(institutionDTO.getCreateTime());
        institution.setUpdateTime(institutionDTO.getUpdateTime());
        // institution.setCreateBy(institutionDTO.getCreateBy()); // Method not found
        // institution.setUpdateBy(institutionDTO.getUpdateBy()); // Method not found
        
        return institution;
    }
}