package com.yxrobot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 公益统计数据实体类
 * 用于存储公益项目的各项统计指标数据
 * 
 * @author YXRobot
 * @version 1.0
 * @since 2024-12-18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharityStats {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 累计受益人数
     * 记录通过公益项目受益的总人数
     */
    @NotNull(message = "累计受益人数不能为空")
    @Min(value = 0, message = "累计受益人数不能为负数")
    private Integer totalBeneficiaries;
    
    /**
     * 合作机构总数
     * 记录所有合作机构的总数量
     */
    @NotNull(message = "合作机构总数不能为空")
    @Min(value = 0, message = "合作机构总数不能为负数")
    private Integer totalInstitutions;
    
    /**
     * 活跃合作机构数
     * 记录当前活跃状态的合作机构数量
     */
    @NotNull(message = "活跃合作机构数不能为空")
    @Min(value = 0, message = "活跃合作机构数不能为负数")
    private Integer cooperatingInstitutions;
    
    /**
     * 志愿者总数
     * 记录参与公益项目的志愿者总人数
     */
    @NotNull(message = "志愿者总数不能为空")
    @Min(value = 0, message = "志愿者总数不能为负数")
    private Integer totalVolunteers;
    
    /**
     * 活跃志愿者数
     * 记录当前活跃状态的志愿者人数
     */
    @NotNull(message = "活跃志愿者数不能为空")
    @Min(value = 0, message = "活跃志愿者数不能为负数")
    private Integer activeVolunteers;
    
    /**
     * 累计筹集金额（单位：分）
     * 记录通过各种渠道筹集的资金总额
     */
    @NotNull(message = "累计筹集金额不能为空")
    @Min(value = 0, message = "累计筹集金额不能为负数")
    private BigDecimal totalRaised;
    
    /**
     * 累计捐赠金额（单位：分）
     * 记录实际用于公益项目的捐赠金额总额
     */
    @NotNull(message = "累计捐赠金额不能为空")
    @Min(value = 0, message = "累计捐赠金额不能为负数")
    private BigDecimal totalDonated;
    
    /**
     * 项目总数
     * 记录所有公益项目的总数量
     */
    @NotNull(message = "项目总数不能为空")
    @Min(value = 0, message = "项目总数不能为负数")
    private Integer totalProjects;
    
    /**
     * 进行中项目数
     * 记录当前正在执行的项目数量
     */
    @NotNull(message = "进行中项目数不能为空")
    @Min(value = 0, message = "进行中项目数不能为负数")
    private Integer activeProjects;
    
    /**
     * 已完成项目数
     * 记录已经完成的项目数量
     */
    @NotNull(message = "已完成项目数不能为空")
    @Min(value = 0, message = "已完成项目数不能为负数")
    private Integer completedProjects;
    
    /**
     * 活动总数
     * 记录所有公益活动的总数量
     */
    @NotNull(message = "活动总数不能为空")
    @Min(value = 0, message = "活动总数不能为负数")
    private Integer totalActivities;
    
    /**
     * 本月活动数
     * 记录当前月份的活动数量
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
     * 是否删除（0：未删除，1：已删除）
     */
    private Integer deleted;
    
    /**
     * 版本号，用于乐观锁控制
     */
    private Integer version;
    
    /**
     * 更新原因
     * 记录数据更新的原因或说明
     */
    private String updateReason;
    
    /**
     * 更新人
     * 记录最后更新数据的操作人员
     */
    private String updatedBy;
}