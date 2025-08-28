package com.yxrobot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 公益统计数据传输对象
 * 用于前后端数据传输，包含统计数据的展示和更新
 * 
 * @author YXRobot
 * @version 1.0
 * @since 2024-12-18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharityStatsDTO {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 累计受益人数
     */
    @NotNull(message = "累计受益人数不能为空")
    @Min(value = 0, message = "累计受益人数不能为负数")
    private Integer totalBeneficiaries;
    
    /**
     * 合作机构总数
     */
    @NotNull(message = "合作机构总数不能为空")
    @Min(value = 0, message = "合作机构总数不能为负数")
    private Integer totalInstitutions;
    
    /**
     * 活跃合作机构数
     */
    @NotNull(message = "活跃合作机构数不能为空")
    @Min(value = 0, message = "活跃合作机构数不能为负数")
    private Integer cooperatingInstitutions;
    
    /**
     * 志愿者总数
     */
    @NotNull(message = "志愿者总数不能为空")
    @Min(value = 0, message = "志愿者总数不能为负数")
    private Integer totalVolunteers;
    
    /**
     * 累计筹集金额（单位：元，前端显示用）
     */
    @NotNull(message = "累计筹集金额不能为空")
    @Min(value = 0, message = "累计筹集金额不能为负数")
    private BigDecimal totalRaised;
    
    /**
     * 累计捐赠金额（单位：元，前端显示用）
     */
    @NotNull(message = "累计捐赠金额不能为空")
    @Min(value = 0, message = "累计捐赠金额不能为负数")
    private BigDecimal totalDonated;
    
    /**
     * 项目总数
     */
    @NotNull(message = "项目总数不能为空")
    @Min(value = 0, message = "项目总数不能为负数")
    private Integer totalProjects;
    
    /**
     * 进行中项目数
     */
    @NotNull(message = "进行中项目数不能为空")
    @Min(value = 0, message = "进行中项目数不能为负数")
    private Integer activeProjects;
    
    /**
     * 已完成项目数
     */
    @NotNull(message = "已完成项目数不能为空")
    @Min(value = 0, message = "已完成项目数不能为负数")
    private Integer completedProjects;
    
    /**
     * 活动总数
     */
    @NotNull(message = "活动总数不能为空")
    @Min(value = 0, message = "活动总数不能为负数")
    private Integer totalActivities;
    
    /**
     * 本月活动数
     */
    @NotNull(message = "本月活动数不能为空")
    @Min(value = 0, message = "本月活动数不能为负数")
    private Integer thisMonthActivities;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    
    /**
     * 数据版本号
     */
    private Integer version;
    
    // 增强数据字段（用于趋势分析）
    
    /**
     * 受益人数环比增长率（百分比）
     */
    private BigDecimal beneficiariesGrowthRate;
    
    /**
     * 筹集金额环比增长率（百分比）
     */
    private BigDecimal raisedGrowthRate;
    
    /**
     * 项目数环比增长率（百分比）
     */
    private BigDecimal projectsGrowthRate;
}

/**
 * 公益统计数据更新请求DTO
 * 用于接收前端的统计数据更新请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class CharityStatsUpdateDTO {
    
    /**
     * 累计受益人数
     */
    @NotNull(message = "累计受益人数不能为空")
    @Min(value = 0, message = "累计受益人数不能为负数")
    private Integer totalBeneficiaries;
    
    /**
     * 合作机构总数
     */
    @NotNull(message = "合作机构总数不能为空")
    @Min(value = 0, message = "合作机构总数不能为负数")
    private Integer totalInstitutions;
    
    /**
     * 活跃合作机构数
     */
    @NotNull(message = "活跃合作机构数不能为空")
    @Min(value = 0, message = "活跃合作机构数不能为负数")
    private Integer cooperatingInstitutions;
    
    /**
     * 志愿者总数
     */
    @NotNull(message = "志愿者总数不能为空")
    @Min(value = 0, message = "志愿者总数不能为负数")
    private Integer totalVolunteers;
    
    /**
     * 累计筹集金额（单位：元）
     */
    @NotNull(message = "累计筹集金额不能为空")
    @Min(value = 0, message = "累计筹集金额不能为负数")
    private BigDecimal totalRaised;
    
    /**
     * 累计捐赠金额（单位：元）
     */
    @NotNull(message = "累计捐赠金额不能为空")
    @Min(value = 0, message = "累计捐赠金额不能为负数")
    private BigDecimal totalDonated;
    
    /**
     * 项目总数
     */
    @NotNull(message = "项目总数不能为空")
    @Min(value = 0, message = "项目总数不能为负数")
    private Integer totalProjects;
    
    /**
     * 进行中项目数
     */
    @NotNull(message = "进行中项目数不能为空")
    @Min(value = 0, message = "进行中项目数不能为负数")
    private Integer activeProjects;
    
    /**
     * 已完成项目数
     */
    @NotNull(message = "已完成项目数不能为空")
    @Min(value = 0, message = "已完成项目数不能为负数")
    private Integer completedProjects;
    
    /**
     * 活动总数
     */
    @NotNull(message = "活动总数不能为空")
    @Min(value = 0, message = "活动总数不能为负数")
    private Integer totalActivities;
    
    /**
     * 本月活动数
     */
    @NotNull(message = "本月活动数不能为空")
    @Min(value = 0, message = "本月活动数不能为负数")
    private Integer thisMonthActivities;
    
    /**
     * 更新原因
     */
    @NotNull(message = "更新原因不能为空")
    private String updateReason;
}