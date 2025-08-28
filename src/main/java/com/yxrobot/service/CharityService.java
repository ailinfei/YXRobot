package com.yxrobot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yxrobot.dto.CharityStatsDTO;
import com.yxrobot.entity.CharityStats;
import com.yxrobot.exception.CharityException;
import com.yxrobot.mapper.CharityStatsMapper;
import com.yxrobot.validation.CharityDataValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * 公益项目管理业务服务类
 * 负责处理公益项目相关的业务逻辑，包括统计数据管理、数据分析等
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-18
 */
@Service
@Transactional
public class CharityService {
    
    private static final Logger logger = LoggerFactory.getLogger(CharityService.class);
    
    @Autowired
    private CharityStatsMapper charityStatsMapper;
    
    @Autowired
    private CharityDataValidator charityDataValidator;
    
    @Autowired
    private CharityStatsLogService charityStatsLogService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 获取基础公益统计数据
     * 
     * @return 基础统计数据DTO
     */
    @Transactional(readOnly = true)
    public CharityStatsDTO getCharityStats() {
        logger.info("获取基础公益统计数据");
        
        try {
            // 直接从数据库获取最新统计数据
            CharityStats charityStats = charityStatsMapper.selectLatest();
            
            if (charityStats == null) {
                logger.warn("未找到公益统计数据，返回默认值");
                return createDefaultCharityStats();
            }
            
            CharityStatsDTO statsDTO = convertToCharityStatsDTO(charityStats);
            logger.info("成功获取基础统计数据");
            
            return statsDTO;
            
        } catch (Exception e) {
            logger.error("获取基础公益统计数据失败", e);
            throw CharityException.statsError("获取基础公益统计数据失败", e.getMessage());
        }
    }
    
    /**
     * 获取增强的公益统计数据
     * 包含趋势分析、增长率等高级统计信息
     * 
     * @return 增强统计数据DTO
     */
    @Transactional(readOnly = true)
    public CharityStatsDTO getEnhancedCharityStats() {
        logger.info("获取增强公益统计数据");
        
        try {
            // 获取当前统计数据
            CharityStats currentStats = charityStatsMapper.selectLatest();
            if (currentStats == null) {
                logger.warn("未找到当前统计数据，返回默认值");
                return createDefaultCharityStats();
            }
            
            // 获取历史数据用于计算增长率（使用历史记录的第二条）
            List<CharityStats> historyList = charityStatsMapper.selectHistory(2);
            CharityStats previousStats = historyList.size() > 1 ? historyList.get(1) : null;
            
            CharityStatsDTO statsDTO = convertToCharityStatsDTO(currentStats);
            
            // 计算增长率和趋势数据
            if (previousStats != null) {
                calculateGrowthRates(statsDTO, currentStats, previousStats);
            } else {
                logger.info("未找到历史数据，增长率设为0");
                setZeroGrowthRates(statsDTO);
            }
            
            // 获取额外的统计信息
            enrichStatsWithAdditionalData(statsDTO);
            
            logger.info("成功获取增强统计数据");
            return statsDTO;
            
        } catch (CharityException e) {
            logger.error("获取增强公益统计数据失败", e);
            throw e;
        } catch (Exception e) {
            logger.error("获取增强公益统计数据失败", e);
            // 降级到基础统计数据
            logger.info("降级到基础统计数据");
            try {
                return getCharityStats();
            } catch (Exception fallbackException) {
                logger.error("降级获取基础统计数据也失败", fallbackException);
                throw CharityException.statsError("获取统计数据失败", e.getMessage());
            }
        }
    }
    
    /**
     * 更新公益统计数据
     * 
     * @param statsDTO 统计数据DTO
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     * @param operatorIp 操作人IP
     * @param updateReason 更新原因
     * @return 更新后的统计数据DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public CharityStatsDTO updateCharityStats(CharityStatsDTO statsDTO, Long operatorId, String operatorName, String operatorIp, String updateReason) {
        logger.info("更新公益统计数据，操作人: {}, 原因: {}", operatorName, updateReason);
        
        // 数据验证
        try {
            validateCharityStatsDTO(statsDTO);
        } catch (IllegalArgumentException e) {
            logger.error("统计数据验证失败: {}", e.getMessage());
            throw CharityException.validationError("统计数据验证失败", e.getMessage());
        }
        
        CharityStats currentStats = null;
        try {
            // 获取当前统计数据用于日志记录
            currentStats = charityStatsMapper.selectLatest();
            
            // 转换为实体对象
            CharityStats charityStats = convertToCharityStats(statsDTO);
            charityStats.setUpdateTime(LocalDateTime.now());
            
            int result;
            if (currentStats != null) {
                charityStats.setVersion(currentStats.getVersion() + 1);
                charityStats.setId(currentStats.getId());
                // 更新现有记录
                result = charityStatsMapper.updateById(charityStats);
            } else {
                charityStats.setVersion(1);
                charityStats.setCreateTime(LocalDateTime.now());
                // 插入新记录
                result = charityStatsMapper.insert(charityStats);
            }
            
            if (result <= 0) {
                throw CharityException.operationFailed("更新统计数据失败", "数据库操作返回结果为0");
            }
            
            // 记录操作日志
            try {
                charityStatsLogService.logStatsUpdate(charityStats, updateReason != null ? updateReason : "手动更新");
            } catch (Exception logException) {
                logger.warn("记录操作日志失败，但不影响主业务流程", logException);
            }
            
            logger.info("成功更新公益统计数据，ID: {}, 版本: {}", charityStats.getId(), charityStats.getVersion());
            
            return convertToCharityStatsDTO(charityStats);
            
        } catch (CharityException e) {
            logger.error("更新公益统计数据失败", e);
            throw e;
        } catch (Exception e) {
            logger.error("更新公益统计数据失败", e);
            throw CharityException.operationFailed("更新公益统计数据失败", e.getMessage());
        }
    }
    
    /**
     * 重新计算公益统计数据
     * 基于实际的机构、活动、项目数据重新计算统计信息
     * 
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     * @param operatorIp 操作人IP
     * @return 重新计算后的统计数据DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public CharityStatsDTO recalculateCharityStats(Long operatorId, String operatorName, String operatorIp) {
        logger.info("重新计算公益统计数据，操作人: {}", operatorName);
        
        CharityStats currentStats = null;
        try {
            // 获取当前统计数据用于备份
            currentStats = charityStatsMapper.selectLatest();
            
            // 使用mapper中的计算方法重新计算统计数据
            CharityStats calculatedStats = charityStatsMapper.calculateStatsFromSource();
            
            if (calculatedStats == null) {
                logger.warn("从源数据计算统计失败，创建默认数据");
                // 如果计算失败，创建默认数据
                calculatedStats = createDefaultCharityStatsEntity();
            }
            
            // 设置计算时间和版本
            calculatedStats.setUpdateTime(LocalDateTime.now());
            calculatedStats.setCreateTime(LocalDateTime.now());
            
            int result;
            if (currentStats != null) {
                calculatedStats.setVersion(currentStats.getVersion() + 1);
                calculatedStats.setId(currentStats.getId());
                // 更新现有记录
                result = charityStatsMapper.updateById(calculatedStats);
            } else {
                calculatedStats.setVersion(1);
                // 插入新记录
                result = charityStatsMapper.insert(calculatedStats);
            }
            
            if (result <= 0) {
                throw CharityException.operationFailed("保存重新计算的统计数据失败", "数据库操作返回结果为0");
            }
            
            // 记录操作日志
            try {
                charityStatsLogService.logStatsUpdate(calculatedStats, "重新计算");
            } catch (Exception logException) {
                logger.warn("记录操作日志失败，但不影响主业务流程", logException);
            }
            
            logger.info("成功重新计算公益统计数据，ID: {}, 版本: {}", calculatedStats.getId(), calculatedStats.getVersion());
            
            return convertToCharityStatsDTO(calculatedStats);
            
        } catch (CharityException e) {
            logger.error("重新计算公益统计数据失败", e);
            throw e;
        } catch (Exception e) {
            logger.error("重新计算公益统计数据失败", e);
            throw CharityException.operationFailed("重新计算公益统计数据失败", e.getMessage());
        }
    }
    
    /**
     * 获取统计数据历史记录
     * 
     * @param limit 限制返回数量
     * @return 历史统计数据列表
     */
    @Transactional(readOnly = true)
    public List<CharityStatsDTO> getStatsHistory(Integer limit) {
        logger.info("获取统计数据历史记录，限制数量: {}", limit);
        
        if (limit == null || limit <= 0) {
            limit = 10; // 默认10条
        }
        
        if (limit > 100) {
            limit = 100; // 限制最大数量，防止内存溢出
        }
        
        try {
            List<CharityStats> historyList = charityStatsMapper.selectHistory(limit);
            
            if (historyList == null) {
                logger.warn("查询历史统计数据返回null，返回空列表");
                return new ArrayList<>();
            }
            
            List<CharityStatsDTO> historyDTOs = historyList.stream()
                    .filter(stats -> stats != null) // 过滤null值
                    .map(this::convertToCharityStatsDTO)
                    .collect(java.util.stream.Collectors.toList());
            
            logger.info("获取到 {} 条历史统计数据", historyDTOs.size());
            return historyDTOs;
            
        } catch (Exception e) {
            logger.error("获取统计数据历史记录失败", e);
            throw CharityException.statsError("获取统计数据历史记录失败", e.getMessage());
        }
    }
    
    /**
     * 获取统计数据趋势分析
     * 
     * @param days 分析天数
     * @return 趋势分析数据
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getStatsTrend(Integer days) {
        logger.info("获取统计数据趋势分析，天数: {}", days);
        
        if (days == null || days <= 0) {
            days = 30; // 默认30天
        }
        
        if (days > 365) {
            days = 365; // 限制最大天数，防止查询过多数据
        }
        
        try {
            List<Map<String, Object>> trendDataList = charityStatsMapper.selectStatsTrend(days);
            
            if (trendDataList == null) {
                logger.warn("查询趋势数据返回null，返回空数据");
                trendDataList = new ArrayList<>();
            }
            
            Map<String, Object> trendData = new HashMap<>();
            trendData.put("trendData", trendDataList);
            trendData.put("days", days);
            trendData.put("dataCount", trendDataList.size());
            trendData.put("queryTime", LocalDateTime.now());
            
            logger.info("成功获取 {} 天的趋势分析数据，共 {} 条记录", days, trendDataList.size());
            return trendData;
            
        } catch (Exception e) {
            logger.error("获取统计数据趋势分析失败", e);
            throw CharityException.statsError("获取统计数据趋势分析失败", e.getMessage());
        }
    }
    
    /**
     * 验证统计数据逻辑一致性
     * 
     * @param statsDTO 统计数据DTO
     * @return 验证结果
     */
    @Transactional(readOnly = true)
    public Map<String, Object> validateStatsConsistency(CharityStatsDTO statsDTO) {
        logger.info("验证统计数据逻辑一致性");
        
        Map<String, Object> result = new HashMap<>();
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        try {
            // 验证基本数据合理性
            if (statsDTO.getTotalInstitutions() != null && statsDTO.getTotalInstitutions() < 0) {
                errors.add("机构总数不能为负数");
            }
            
            if (statsDTO.getTotalActivities() != null && statsDTO.getTotalActivities() < 0) {
                errors.add("活动总数不能为负数");
            }
            
            if (statsDTO.getTotalProjects() != null && statsDTO.getTotalProjects() < 0) {
                errors.add("项目总数不能为负数");
            }
            
            if (statsDTO.getTotalBeneficiaries() != null && statsDTO.getTotalBeneficiaries() < 0) {
                errors.add("受益人总数不能为负数");
            }
            
            if (statsDTO.getTotalDonated() != null && statsDTO.getTotalDonated().compareTo(BigDecimal.ZERO) < 0) {
                errors.add("总捐款额不能为负数");
            }
            
            // 验证逻辑一致性
            if (statsDTO.getTotalProjects() != null && statsDTO.getTotalBeneficiaries() != null) {
                if (statsDTO.getTotalProjects() > 0 && statsDTO.getTotalBeneficiaries() == 0) {
                    warnings.add("有项目但没有受益人，请检查数据");
                }
            }
            
            if (statsDTO.getTotalActivities() != null && statsDTO.getTotalInstitutions() != null) {
                if (statsDTO.getTotalActivities() > statsDTO.getTotalInstitutions() * 100) {
                    warnings.add("活动数量相对于机构数量过多，请检查数据");
                }
            }
            
            boolean isValid = errors.isEmpty();
            result.put("is_valid", String.valueOf(isValid));
            result.put("errors", errors);
            result.put("warnings", warnings);
            result.put("validation_time", LocalDateTime.now());
            
            logger.info("统计数据逻辑一致性验证完成 - 有效: {}, 错误数: {}, 警告数: {}", 
                       isValid, errors.size(), warnings.size());
            
            return result;
            
        } catch (Exception e) {
            logger.error("验证统计数据逻辑一致性失败", e);
            result.put("is_valid", "false");
            result.put("errors", List.of("验证过程中发生错误: " + e.getMessage()));
            result.put("warnings", new ArrayList<>());
            return result;
        }
    }
    
    /**
     * 验证统计数据DTO
     * 
     * @param statsDTO 统计数据DTO
     */
    private void validateCharityStatsDTO(CharityStatsDTO statsDTO) {
        if (statsDTO == null) {
            throw new IllegalArgumentException("统计数据不能为空");
        }
        
        // 验证数值字段不能为负数
        if (statsDTO.getTotalInstitutions() != null && statsDTO.getTotalInstitutions() < 0) {
            throw new IllegalArgumentException("机构总数不能为负数");
        }
        
        if (statsDTO.getTotalActivities() != null && statsDTO.getTotalActivities() < 0) {
            throw new IllegalArgumentException("活动总数不能为负数");
        }
        
        if (statsDTO.getTotalProjects() != null && statsDTO.getTotalProjects() < 0) {
            throw new IllegalArgumentException("项目总数不能为负数");
        }
        
        if (statsDTO.getTotalBeneficiaries() != null && statsDTO.getTotalBeneficiaries() < 0) {
            throw new IllegalArgumentException("受益人总数不能为负数");
        }
        
        if (statsDTO.getTotalRaised() != null && statsDTO.getTotalRaised().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("总筹集金额不能为负数");
        }
        
        if (statsDTO.getTotalDonated() != null && statsDTO.getTotalDonated().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("总捐款额不能为负数");
        }
        
        if (statsDTO.getActiveProjects() != null && statsDTO.getActiveProjects() < 0) {
            throw new IllegalArgumentException("活跃项目数不能为负数");
        }
        
        if (statsDTO.getCompletedProjects() != null && statsDTO.getCompletedProjects() < 0) {
            throw new IllegalArgumentException("完成项目数不能为负数");
        }
        
        if (statsDTO.getCooperatingInstitutions() != null && statsDTO.getCooperatingInstitutions() < 0) {
            throw new IllegalArgumentException("合作机构数不能为负数");
        }
        
        if (statsDTO.getTotalVolunteers() != null && statsDTO.getTotalVolunteers() < 0) {
            throw new IllegalArgumentException("志愿者总数不能为负数");
        }
        
        if (statsDTO.getThisMonthActivities() != null && statsDTO.getThisMonthActivities() < 0) {
            throw new IllegalArgumentException("本月活动数不能为负数");
        }
        
        // 验证逻辑一致性
        if (statsDTO.getTotalProjects() != null && statsDTO.getActiveProjects() != null && 
            statsDTO.getCompletedProjects() != null) {
            int calculatedTotal = statsDTO.getActiveProjects() + statsDTO.getCompletedProjects();
            if (calculatedTotal > statsDTO.getTotalProjects()) {
                throw new IllegalArgumentException("活跃项目数和完成项目数之和不能大于项目总数");
            }
        }
        
        // 验证金额字段的合理性
        if (statsDTO.getTotalRaised() != null && statsDTO.getTotalDonated() != null) {
            if (statsDTO.getTotalDonated().compareTo(statsDTO.getTotalRaised()) > 0) {
                throw new IllegalArgumentException("总捐款额不能大于总筹集金额");
            }
        }
        
        // 验证机构相关数据的一致性
        if (statsDTO.getTotalInstitutions() != null && statsDTO.getCooperatingInstitutions() != null) {
            if (statsDTO.getCooperatingInstitutions() > statsDTO.getTotalInstitutions()) {
                throw new IllegalArgumentException("合作机构数不能大于机构总数");
            }
        }
    }
    
    /**
     * 创建默认的统计数据
     * 
     * @return 默认统计数据DTO
     */
    private CharityStatsDTO createDefaultCharityStats() {
        CharityStatsDTO defaultStats = new CharityStatsDTO();
        defaultStats.setTotalInstitutions(0);
        defaultStats.setTotalActivities(0);
        defaultStats.setTotalProjects(0);
        defaultStats.setTotalBeneficiaries(0);
        defaultStats.setTotalRaised(BigDecimal.ZERO);
        defaultStats.setTotalDonated(BigDecimal.ZERO);
        defaultStats.setActiveProjects(0);
        defaultStats.setCompletedProjects(0);
        defaultStats.setCooperatingInstitutions(0);
        defaultStats.setTotalVolunteers(0);
        defaultStats.setThisMonthActivities(0);
        defaultStats.setUpdateTime(LocalDateTime.now());
        defaultStats.setCreateTime(LocalDateTime.now());
        defaultStats.setVersion(0);
        
        // 设置零增长率
        setZeroGrowthRates(defaultStats);
        
        return defaultStats;
    }
    
    /**
     * 创建默认的统计数据实体
     * 
     * @return 默认统计数据实体
     */
    private CharityStats createDefaultCharityStatsEntity() {
        CharityStats defaultStats = new CharityStats();
        defaultStats.setTotalInstitutions(0);
        defaultStats.setTotalActivities(0);
        defaultStats.setTotalProjects(0);
        defaultStats.setTotalBeneficiaries(0);
        defaultStats.setTotalRaised(BigDecimal.ZERO);
        defaultStats.setTotalDonated(BigDecimal.ZERO);
        defaultStats.setActiveProjects(0);
        defaultStats.setCompletedProjects(0);
        defaultStats.setCooperatingInstitutions(0);
        defaultStats.setTotalVolunteers(0);
        defaultStats.setThisMonthActivities(0);
        defaultStats.setUpdateTime(LocalDateTime.now());
        defaultStats.setCreateTime(LocalDateTime.now());
        defaultStats.setVersion(1);
        
        return defaultStats;
    }
    
    /**
     * 计算增长率
     * 
     * @param statsDTO 当前统计数据DTO
     * @param current 当前统计数据
     * @param previous 历史统计数据
     */
    private void calculateGrowthRates(CharityStatsDTO statsDTO, CharityStats current, CharityStats previous) {
        // 计算受益人增长率
        if (previous.getTotalBeneficiaries() > 0) {
            double beneficiaryGrowth = ((double) (current.getTotalBeneficiaries() - previous.getTotalBeneficiaries()) 
                    / previous.getTotalBeneficiaries()) * 100;
            statsDTO.setBeneficiariesGrowthRate(BigDecimal.valueOf(beneficiaryGrowth).setScale(2, RoundingMode.HALF_UP));
        } else {
            statsDTO.setBeneficiariesGrowthRate(BigDecimal.ZERO);
        }
        
        // 计算筹集金额增长率
        if (previous.getTotalRaised() != null && previous.getTotalRaised().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal raisedDiff = current.getTotalRaised().subtract(previous.getTotalRaised());
            BigDecimal raisedGrowth = raisedDiff.divide(previous.getTotalRaised(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
            statsDTO.setRaisedGrowthRate(raisedGrowth);
        } else {
            statsDTO.setRaisedGrowthRate(BigDecimal.ZERO);
        }
        
        // 计算项目数增长率
        if (previous.getTotalProjects() > 0) {
            double projectGrowth = ((double) (current.getTotalProjects() - previous.getTotalProjects()) 
                    / previous.getTotalProjects()) * 100;
            statsDTO.setProjectsGrowthRate(BigDecimal.valueOf(projectGrowth).setScale(2, RoundingMode.HALF_UP));
        } else {
            statsDTO.setProjectsGrowthRate(BigDecimal.ZERO);
        }
    }
    
    /**
     * 设置零增长率
     * 
     * @param statsDTO 统计数据DTO
     */
    private void setZeroGrowthRates(CharityStatsDTO statsDTO) {
        statsDTO.setBeneficiariesGrowthRate(BigDecimal.ZERO);
        statsDTO.setRaisedGrowthRate(BigDecimal.ZERO);
        statsDTO.setProjectsGrowthRate(BigDecimal.ZERO);
    }
    
    /**
     * 用额外数据丰富统计信息
     * 
     * @param statsDTO 统计数据DTO
     */
    private void enrichStatsWithAdditionalData(CharityStatsDTO statsDTO) {
        try {
            // 由于DTO中没有额外的字段，这里暂时不做额外处理
            // 如果需要额外的统计信息，可以在DTO中添加相应字段
            logger.debug("统计数据已丰富完成");
            
        } catch (Exception e) {
            logger.warn("获取额外统计数据时出现异常，使用默认值", e);
        }
    }
    
    /**
     * 将CharityStats实体转换为CharityStatsDTO
     * 
     * @param charityStats 统计数据实体
     * @return 统计数据DTO
     */
    private CharityStatsDTO convertToCharityStatsDTO(CharityStats charityStats) {
        if (charityStats == null) {
            return null;
        }
        
        CharityStatsDTO dto = new CharityStatsDTO();
        dto.setId(charityStats.getId());
        dto.setTotalInstitutions(charityStats.getTotalInstitutions());
        dto.setTotalActivities(charityStats.getTotalActivities());
        dto.setTotalProjects(charityStats.getTotalProjects());
        dto.setTotalBeneficiaries(charityStats.getTotalBeneficiaries());
        dto.setTotalRaised(charityStats.getTotalRaised());
        dto.setTotalDonated(charityStats.getTotalDonated());
        dto.setActiveProjects(charityStats.getActiveProjects());
        dto.setCompletedProjects(charityStats.getCompletedProjects());
        dto.setCooperatingInstitutions(charityStats.getCooperatingInstitutions());
        dto.setTotalVolunteers(charityStats.getTotalVolunteers());
        dto.setThisMonthActivities(charityStats.getThisMonthActivities());
        dto.setUpdateTime(charityStats.getUpdateTime());
        dto.setCreateTime(charityStats.getCreateTime());
        dto.setVersion(charityStats.getVersion());
        
        return dto;
    }
    
    /**
     * 将CharityStatsDTO转换为CharityStats实体
     * 
     * @param statsDTO 统计数据DTO
     * @return 统计数据实体
     */
    private CharityStats convertToCharityStats(CharityStatsDTO statsDTO) {
        if (statsDTO == null) {
            return null;
        }
        
        CharityStats entity = new CharityStats();
        entity.setId(statsDTO.getId());
        entity.setTotalInstitutions(statsDTO.getTotalInstitutions());
        entity.setTotalActivities(statsDTO.getTotalActivities());
        entity.setTotalProjects(statsDTO.getTotalProjects());
        entity.setTotalBeneficiaries(statsDTO.getTotalBeneficiaries());
        entity.setTotalRaised(statsDTO.getTotalRaised());
        entity.setTotalDonated(statsDTO.getTotalDonated());
        entity.setActiveProjects(statsDTO.getActiveProjects());
        entity.setCompletedProjects(statsDTO.getCompletedProjects());
        entity.setCooperatingInstitutions(statsDTO.getCooperatingInstitutions());
        entity.setTotalVolunteers(statsDTO.getTotalVolunteers());
        entity.setThisMonthActivities(statsDTO.getThisMonthActivities());
        entity.setUpdateTime(statsDTO.getUpdateTime());
        entity.setCreateTime(statsDTO.getCreateTime());
        entity.setVersion(statsDTO.getVersion());
        
        return entity;
    }
}