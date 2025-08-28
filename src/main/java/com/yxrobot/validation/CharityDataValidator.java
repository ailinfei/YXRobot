package com.yxrobot.validation;

import com.yxrobot.dto.CharityStatsDTO;
import com.yxrobot.dto.CharityInstitutionDTO;
import com.yxrobot.dto.CharityActivityDTO;
import com.yxrobot.exception.CharityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 公益项目数据验证器
 * 提供统一的数据验证逻辑，确保数据完整性和业务规则一致性
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-19
 */
@Component
public class CharityDataValidator {
    
    private static final Logger logger = LoggerFactory.getLogger(CharityDataValidator.class);
    
    // 正则表达式模式
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^1[3-9]\\d{9}$|^0\\d{2,3}-?\\d{7,8}$|^400-?\\d{3}-?\\d{4}$"
    );
    
    private static final Pattern NAME_PATTERN = Pattern.compile(
        "^[\\u4e00-\\u9fa5a-zA-Z0-9\\s\\-_()（）]{2,50}$"
    );
    
    /**
     * 验证公益统计数据
     * 
     * @param statsDTO 统计数据DTO
     * @throws CharityException 验证失败时抛出异常
     */
    public void validateCharityStats(CharityStatsDTO statsDTO) {
        logger.debug("开始验证公益统计数据");
        
        if (statsDTO == null) {
            throw CharityException.validationError("统计数据不能为空");
        }
        
        List<String> errors = new ArrayList<>();
        
        // 验证基础统计数据
        validateBasicStats(statsDTO, errors);
        
        // 验证资金统计数据
        validateFundingStats(statsDTO, errors);
        
        // 验证项目统计数据
        validateProjectStats(statsDTO, errors);
        
        // 验证活动统计数据
        validateActivityStats(statsDTO, errors);
        
        // 验证逻辑一致性
        validateStatsConsistency(statsDTO, errors);
        
        if (!errors.isEmpty()) {
            String errorMessage = "统计数据验证失败：" + String.join("; ", errors);
            logger.warn("统计数据验证失败: {}", errorMessage);
            throw CharityException.validationError(errorMessage, String.join("\n", errors));
        }
        
        logger.debug("公益统计数据验证通过");
    }
    
    /**
     * 验证合作机构数据
     * 
     * @param institutionDTO 机构数据DTO
     * @throws CharityException 验证失败时抛出异常
     */
    public void validateCharityInstitution(CharityInstitutionDTO institutionDTO) {
        logger.debug("开始验证合作机构数据");
        
        if (institutionDTO == null) {
            throw CharityException.validationError("机构数据不能为空");
        }
        
        List<String> errors = new ArrayList<>();
        
        // 验证必填字段
        validateInstitutionRequiredFields(institutionDTO, errors);
        
        // 验证字段格式
        validateInstitutionFieldFormats(institutionDTO, errors);
        
        // 验证业务规则
        validateInstitutionBusinessRules(institutionDTO, errors);
        
        if (!errors.isEmpty()) {
            String errorMessage = "机构数据验证失败：" + String.join("; ", errors);
            logger.warn("机构数据验证失败: {}", errorMessage);
            throw CharityException.validationError(errorMessage, String.join("\n", errors));
        }
        
        logger.debug("合作机构数据验证通过");
    }
    
    /**
     * 验证公益活动数据
     * 
     * @param activityDTO 活动数据DTO
     * @throws CharityException 验证失败时抛出异常
     */
    public void validateCharityActivity(CharityActivityDTO activityDTO) {
        logger.debug("开始验证公益活动数据");
        
        if (activityDTO == null) {
            throw CharityException.validationError("活动数据不能为空");
        }
        
        List<String> errors = new ArrayList<>();
        
        // 验证必填字段
        validateActivityRequiredFields(activityDTO, errors);
        
        // 验证字段格式
        validateActivityFieldFormats(activityDTO, errors);
        
        // 验证业务规则
        validateActivityBusinessRules(activityDTO, errors);
        
        if (!errors.isEmpty()) {
            String errorMessage = "活动数据验证失败：" + String.join("; ", errors);
            logger.warn("活动数据验证失败: {}", errorMessage);
            throw CharityException.validationError(errorMessage, String.join("\n", errors));
        }
        
        logger.debug("公益活动数据验证通过");
    }
    
    /**
     * 验证基础统计数据
     */
    private void validateBasicStats(CharityStatsDTO statsDTO, List<String> errors) {
        // 验证受益人数
        if (statsDTO.getTotalBeneficiaries() == null) {
            errors.add("累计受益人数不能为空");
        } else if (statsDTO.getTotalBeneficiaries() < 0) {
            errors.add("累计受益人数不能为负数");
        } else if (statsDTO.getTotalBeneficiaries() > 10000000) {
            errors.add("累计受益人数不能超过1000万");
        }
        
        // 验证机构数量
        if (statsDTO.getTotalInstitutions() == null) {
            errors.add("合作机构总数不能为空");
        } else if (statsDTO.getTotalInstitutions() < 0) {
            errors.add("合作机构总数不能为负数");
        } else if (statsDTO.getTotalInstitutions() > 100000) {
            errors.add("合作机构总数不能超过10万");
        }
        
        // 验证活跃机构数
        if (statsDTO.getCooperatingInstitutions() == null) {
            errors.add("活跃合作机构数不能为空");
        } else if (statsDTO.getCooperatingInstitutions() < 0) {
            errors.add("活跃合作机构数不能为负数");
        }
        
        // 验证志愿者数量
        if (statsDTO.getTotalVolunteers() == null) {
            errors.add("志愿者总数不能为空");
        } else if (statsDTO.getTotalVolunteers() < 0) {
            errors.add("志愿者总数不能为负数");
        } else if (statsDTO.getTotalVolunteers() > 1000000) {
            errors.add("志愿者总数不能超过100万");
        }
    }
    
    /**
     * 验证资金统计数据
     */
    private void validateFundingStats(CharityStatsDTO statsDTO, List<String> errors) {
        // 验证筹集金额
        if (statsDTO.getTotalRaised() == null) {
            errors.add("累计筹集金额不能为空");
        } else if (statsDTO.getTotalRaised().compareTo(BigDecimal.ZERO) < 0) {
            errors.add("累计筹集金额不能为负数");
        } else if (statsDTO.getTotalRaised().compareTo(new BigDecimal("1000000000")) > 0) {
            errors.add("累计筹集金额不能超过10亿元");
        }
        
        // 验证捐赠金额
        if (statsDTO.getTotalDonated() == null) {
            errors.add("累计捐赠金额不能为空");
        } else if (statsDTO.getTotalDonated().compareTo(BigDecimal.ZERO) < 0) {
            errors.add("累计捐赠金额不能为负数");
        } else if (statsDTO.getTotalDonated().compareTo(new BigDecimal("1000000000")) > 0) {
            errors.add("累计捐赠金额不能超过10亿元");
        }
    }
    
    /**
     * 验证项目统计数据
     */
    private void validateProjectStats(CharityStatsDTO statsDTO, List<String> errors) {
        // 验证项目总数
        if (statsDTO.getTotalProjects() == null) {
            errors.add("项目总数不能为空");
        } else if (statsDTO.getTotalProjects() < 0) {
            errors.add("项目总数不能为负数");
        } else if (statsDTO.getTotalProjects() > 100000) {
            errors.add("项目总数不能超过10万");
        }
        
        // 验证进行中项目数
        if (statsDTO.getActiveProjects() == null) {
            errors.add("进行中项目数不能为空");
        } else if (statsDTO.getActiveProjects() < 0) {
            errors.add("进行中项目数不能为负数");
        }
        
        // 验证已完成项目数
        if (statsDTO.getCompletedProjects() == null) {
            errors.add("已完成项目数不能为空");
        } else if (statsDTO.getCompletedProjects() < 0) {
            errors.add("已完成项目数不能为负数");
        }
    }
    
    /**
     * 验证活动统计数据
     */
    private void validateActivityStats(CharityStatsDTO statsDTO, List<String> errors) {
        // 验证活动总数
        if (statsDTO.getTotalActivities() == null) {
            errors.add("活动总数不能为空");
        } else if (statsDTO.getTotalActivities() < 0) {
            errors.add("活动总数不能为负数");
        } else if (statsDTO.getTotalActivities() > 1000000) {
            errors.add("活动总数不能超过100万");
        }
        
        // 验证本月活动数
        if (statsDTO.getThisMonthActivities() == null) {
            errors.add("本月活动数不能为空");
        } else if (statsDTO.getThisMonthActivities() < 0) {
            errors.add("本月活动数不能为负数");
        } else if (statsDTO.getThisMonthActivities() > 10000) {
            errors.add("本月活动数不能超过1万");
        }
    }
    
    /**
     * 验证统计数据逻辑一致性
     */
    private void validateStatsConsistency(CharityStatsDTO statsDTO, List<String> errors) {
        // 活跃机构数不能超过总机构数
        if (statsDTO.getTotalInstitutions() != null && statsDTO.getCooperatingInstitutions() != null) {
            if (statsDTO.getCooperatingInstitutions() > statsDTO.getTotalInstitutions()) {
                errors.add("活跃合作机构数不能超过合作机构总数");
            }
        }
        
        // 捐赠金额不能超过筹集金额
        if (statsDTO.getTotalRaised() != null && statsDTO.getTotalDonated() != null) {
            if (statsDTO.getTotalDonated().compareTo(statsDTO.getTotalRaised()) > 0) {
                errors.add("累计捐赠金额不能超过累计筹集金额");
            }
        }
        
        // 项目数量逻辑验证
        if (statsDTO.getTotalProjects() != null && 
            statsDTO.getActiveProjects() != null && 
            statsDTO.getCompletedProjects() != null) {
            if (statsDTO.getActiveProjects() + statsDTO.getCompletedProjects() > statsDTO.getTotalProjects()) {
                errors.add("进行中项目数和已完成项目数之和不能超过项目总数");
            }
        }
        
        // 本月活动数不能超过活动总数
        if (statsDTO.getTotalActivities() != null && statsDTO.getThisMonthActivities() != null) {
            if (statsDTO.getThisMonthActivities() > statsDTO.getTotalActivities()) {
                errors.add("本月活动数不能超过活动总数");
            }
        }
    }
    
    /**
     * 验证机构必填字段
     */
    private void validateInstitutionRequiredFields(CharityInstitutionDTO institutionDTO, List<String> errors) {
        if (institutionDTO.getName() == null || institutionDTO.getName().trim().isEmpty()) {
            errors.add("机构名称不能为空");
        }
        
        if (institutionDTO.getType() == null || institutionDTO.getType().trim().isEmpty()) {
            errors.add("机构类型不能为空");
        }
        
        if (institutionDTO.getLocation() == null || institutionDTO.getLocation().trim().isEmpty()) {
            errors.add("机构地区不能为空");
        }
        
        if (institutionDTO.getContactPerson() == null || institutionDTO.getContactPerson().trim().isEmpty()) {
            errors.add("联系人不能为空");
        }
        
        if (institutionDTO.getContactPhone() == null || institutionDTO.getContactPhone().trim().isEmpty()) {
            errors.add("联系电话不能为空");
        }
        
        if (institutionDTO.getStatus() == null || institutionDTO.getStatus().trim().isEmpty()) {
            errors.add("机构状态不能为空");
        }
    }
    
    /**
     * 验证机构字段格式
     */
    private void validateInstitutionFieldFormats(CharityInstitutionDTO institutionDTO, List<String> errors) {
        // 验证机构名称格式
        if (institutionDTO.getName() != null && !institutionDTO.getName().trim().isEmpty()) {
            if (!NAME_PATTERN.matcher(institutionDTO.getName().trim()).matches()) {
                errors.add("机构名称格式不正确，只能包含中文、英文、数字、空格和常用符号，长度2-50个字符");
            }
        }
        
        // 验证联系人格式
        if (institutionDTO.getContactPerson() != null && !institutionDTO.getContactPerson().trim().isEmpty()) {
            if (!NAME_PATTERN.matcher(institutionDTO.getContactPerson().trim()).matches()) {
                errors.add("联系人姓名格式不正确，只能包含中文、英文、数字、空格和常用符号，长度2-50个字符");
            }
        }
        
        // 验证电话格式
        if (institutionDTO.getContactPhone() != null && !institutionDTO.getContactPhone().trim().isEmpty()) {
            if (!PHONE_PATTERN.matcher(institutionDTO.getContactPhone().trim()).matches()) {
                errors.add("联系电话格式不正确，请输入有效的手机号码或固定电话");
            }
        }
        
        // 验证邮箱格式
        if (institutionDTO.getEmail() != null && !institutionDTO.getEmail().trim().isEmpty()) {
            if (!EMAIL_PATTERN.matcher(institutionDTO.getEmail().trim()).matches()) {
                errors.add("邮箱地址格式不正确");
            }
        }
        
        // 验证机构类型
        if (institutionDTO.getType() != null && !institutionDTO.getType().trim().isEmpty()) {
            String[] validTypes = {"school", "hospital", "community", "ngo", "government", "enterprise", "other"};
            boolean isValidType = false;
            for (String validType : validTypes) {
                if (validType.equals(institutionDTO.getType().trim())) {
                    isValidType = true;
                    break;
                }
            }
            if (!isValidType) {
                errors.add("机构类型不正确，必须是：school、hospital、community、ngo、government、enterprise、other 中的一种");
            }
        }
        
        // 验证机构状态
        if (institutionDTO.getStatus() != null && !institutionDTO.getStatus().trim().isEmpty()) {
            String[] validStatuses = {"active", "inactive", "suspended", "terminated"};
            boolean isValidStatus = false;
            for (String validStatus : validStatuses) {
                if (validStatus.equals(institutionDTO.getStatus().trim())) {
                    isValidStatus = true;
                    break;
                }
            }
            if (!isValidStatus) {
                errors.add("机构状态不正确，必须是：active、inactive、suspended、terminated 中的一种");
            }
        }
    }
    
    /**
     * 验证机构业务规则
     */
    private void validateInstitutionBusinessRules(CharityInstitutionDTO institutionDTO, List<String> errors) {
        // 验证学生数量
        if (institutionDTO.getStudentCount() != null) {
            if (institutionDTO.getStudentCount() < 0) {
                errors.add("学生数量不能为负数");
            } else if (institutionDTO.getStudentCount() > 100000) {
                errors.add("学生数量不能超过10万");
            }
        }
        
        // 验证设备数量
        if (institutionDTO.getDeviceCount() != null) {
            if (institutionDTO.getDeviceCount() < 0) {
                errors.add("设备数量不能为负数");
            } else if (institutionDTO.getDeviceCount() > 10000) {
                errors.add("设备数量不能超过1万");
            }
        }
        
        // 验证合作日期
        if (institutionDTO.getCooperationDate() != null) {
            LocalDate today = LocalDate.now();
            LocalDate cooperationDate = institutionDTO.getCooperationDate();
            
            if (cooperationDate.isAfter(today)) {
                errors.add("合作日期不能是未来日期");
            }
            
            // 合作日期不能早于2000年
            if (cooperationDate.isBefore(LocalDate.of(2000, 1, 1))) {
                errors.add("合作日期不能早于2000年1月1日");
            }
        }
        
        // 验证最后访问日期
        if (institutionDTO.getLastVisitDate() != null) {
            LocalDate today = LocalDate.now();
            LocalDate lastVisitDate = institutionDTO.getLastVisitDate();
            
            if (lastVisitDate.isAfter(today)) {
                errors.add("最后访问日期不能是未来时间");
            }
        }
    }
    
    /**
     * 验证活动必填字段
     */
    private void validateActivityRequiredFields(CharityActivityDTO activityDTO, List<String> errors) {
        if (activityDTO.getTitle() == null || activityDTO.getTitle().trim().isEmpty()) {
            errors.add("活动标题不能为空");
        }
        
        if (activityDTO.getType() == null || activityDTO.getType().trim().isEmpty()) {
            errors.add("活动类型不能为空");
        }
        
        if (activityDTO.getDate() == null) {
            errors.add("活动日期不能为空");
        }
        
        if (activityDTO.getLocation() == null || activityDTO.getLocation().trim().isEmpty()) {
            errors.add("活动地点不能为空");
        }
        
        if (activityDTO.getOrganizer() == null || activityDTO.getOrganizer().trim().isEmpty()) {
            errors.add("组织方不能为空");
        }
        
        if (activityDTO.getStatus() == null || activityDTO.getStatus().trim().isEmpty()) {
            errors.add("活动状态不能为空");
        }
    }
    
    /**
     * 验证活动字段格式
     */
    private void validateActivityFieldFormats(CharityActivityDTO activityDTO, List<String> errors) {
        // 验证活动标题长度
        if (activityDTO.getTitle() != null && !activityDTO.getTitle().trim().isEmpty()) {
            if (activityDTO.getTitle().trim().length() > 100) {
                errors.add("活动标题长度不能超过100个字符");
            }
        }
        
        // 验证活动描述长度
        if (activityDTO.getDescription() != null && !activityDTO.getDescription().trim().isEmpty()) {
            if (activityDTO.getDescription().trim().length() > 1000) {
                errors.add("活动描述长度不能超过1000个字符");
            }
        }
        
        // 验证活动类型
        if (activityDTO.getType() != null && !activityDTO.getType().trim().isEmpty()) {
            String[] validTypes = {"donation", "volunteer", "education", "medical", "environmental", "cultural", "other"};
            boolean isValidType = false;
            for (String validType : validTypes) {
                if (validType.equals(activityDTO.getType().trim())) {
                    isValidType = true;
                    break;
                }
            }
            if (!isValidType) {
                errors.add("活动类型不正确，必须是：donation、volunteer、education、medical、environmental、cultural、other 中的一种");
            }
        }
        
        // 验证活动状态
        if (activityDTO.getStatus() != null && !activityDTO.getStatus().trim().isEmpty()) {
            String[] validStatuses = {"planned", "ongoing", "completed", "cancelled"};
            boolean isValidStatus = false;
            for (String validStatus : validStatuses) {
                if (validStatus.equals(activityDTO.getStatus().trim())) {
                    isValidStatus = true;
                    break;
                }
            }
            if (!isValidStatus) {
                errors.add("活动状态不正确，必须是：planned、ongoing、completed、cancelled 中的一种");
            }
        }
    }
    
    /**
     * 验证活动业务规则
     */
    private void validateActivityBusinessRules(CharityActivityDTO activityDTO, List<String> errors) {
        // 验证参与人数
        if (activityDTO.getParticipants() != null) {
            if (activityDTO.getParticipants() < 0) {
                errors.add("参与人数不能为负数");
            } else if (activityDTO.getParticipants() > 100000) {
                errors.add("参与人数不能超过10万");
            }
        }
        
        // 验证预算金额
        if (activityDTO.getBudget() != null) {
            if (activityDTO.getBudget().compareTo(BigDecimal.ZERO) < 0) {
                errors.add("预算金额不能为负数");
            } else if (activityDTO.getBudget().compareTo(new BigDecimal("10000000")) > 0) {
                errors.add("预算金额不能超过1000万元");
            }
        }
        
        // 验证实际费用
        if (activityDTO.getActualCost() != null) {
            if (activityDTO.getActualCost().compareTo(BigDecimal.ZERO) < 0) {
                errors.add("实际费用不能为负数");
            } else if (activityDTO.getActualCost().compareTo(new BigDecimal("10000000")) > 0) {
                errors.add("实际费用不能超过1000万元");
            }
        }
        
        // 验证预算和实际费用的关系
        if (activityDTO.getBudget() != null && activityDTO.getActualCost() != null) {
            // 实际费用超出预算200%时给出警告
            BigDecimal maxAllowedCost = activityDTO.getBudget().multiply(new BigDecimal("3"));
            if (activityDTO.getActualCost().compareTo(maxAllowedCost) > 0) {
                errors.add("实际费用超出预算过多，请检查数据是否正确");
            }
        }
        
        // 验证活动日期
        if (activityDTO.getDate() != null) {
            LocalDate activityDate = activityDTO.getDate();
            LocalDate today = LocalDate.now();
            
            // 活动日期不能早于2000年
            if (activityDate.isBefore(LocalDate.of(2000, 1, 1))) {
                errors.add("活动日期不能早于2000年1月1日");
            }
            
            // 活动日期不能超过未来5年
            if (activityDate.isAfter(today.plusYears(5))) {
                errors.add("活动日期不能超过未来5年");
            }
        }
    }
    
    /**
     * 验证字符串是否为空或null
     * 
     * @param str 待验证字符串
     * @return true-为空，false-不为空
     */
    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * 验证数字是否在指定范围内
     * 
     * @param value 待验证数值
     * @param min 最小值
     * @param max 最大值
     * @return true-在范围内，false-不在范围内
     */
    private boolean isInRange(Integer value, int min, int max) {
        return value != null && value >= min && value <= max;
    }
    
    /**
     * 验证BigDecimal是否在指定范围内
     * 
     * @param value 待验证数值
     * @param min 最小值
     * @param max 最大值
     * @return true-在范围内，false-不在范围内
     */
    private boolean isInRange(BigDecimal value, BigDecimal min, BigDecimal max) {
        return value != null && value.compareTo(min) >= 0 && value.compareTo(max) <= 0;
    }
}