package com.yxrobot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 公益项目数据传输对象
 * 用于前后端数据传输，包含项目信息的展示和管理
 * 
 * @author YXRobot
 * @version 1.0
 * @since 2024-12-18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharityProjectDTO {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 项目名称
     */
    @NotBlank(message = "项目名称不能为空")
    @Size(max = 100, message = "项目名称长度不能超过100个字符")
    private String name;
    
    /**
     * 项目描述
     */
    @NotBlank(message = "项目描述不能为空")
    @Size(max = 1000, message = "项目描述长度不能超过1000个字符")
    private String description;
    
    /**
     * 项目类型
     */
    @NotBlank(message = "项目类型不能为空")
    @Pattern(regexp = "^(education|medical|environment|poverty|disaster|other)$", 
             message = "项目类型必须是：education、medical、environment、poverty、disaster、other 中的一种")
    private String type;
    
    /**
     * 项目类型显示名称
     */
    private String typeDisplay;
    
    /**
     * 项目状态
     */
    @NotBlank(message = "项目状态不能为空")
    @Pattern(regexp = "^(planning|active|completed|suspended|cancelled)$", 
             message = "项目状态必须是：planning、active、completed、suspended、cancelled 中的一种")
    private String status;
    
    /**
     * 项目状态显示名称
     */
    private String statusDisplay;
    
    /**
     * 项目开始日期
     */
    @NotNull(message = "项目开始日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    /**
     * 项目结束日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    /**
     * 目标受益人数
     */
    @NotNull(message = "目标受益人数不能为空")
    @Min(value = 1, message = "目标受益人数必须大于0")
    private Integer targetBeneficiaries;
    
    /**
     * 实际受益人数
     */
    @Min(value = 0, message = "实际受益人数不能为负数")
    private Integer actualBeneficiaries;
    
    /**
     * 项目预算（单位：元，前端显示用）
     */
    @NotNull(message = "项目预算不能为空")
    @DecimalMin(value = "0", message = "项目预算不能为负数")
    private BigDecimal budget;
    
    /**
     * 实际支出（单位：元，前端显示用）
     */
    @DecimalMin(value = "0", message = "实际支出不能为负数")
    private BigDecimal actualCost;
    
    /**
     * 项目负责人
     */
    @NotBlank(message = "项目负责人不能为空")
    @Size(max = 50, message = "项目负责人长度不能超过50个字符")
    private String manager;
    
    /**
     * 项目负责人联系方式
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$|^0\\d{2,3}-?\\d{7,8}$", 
             message = "联系方式格式不正确")
    private String managerContact;
    
    /**
     * 合作机构ID
     */
    private Long institutionId;
    
    /**
     * 合作机构名称
     */
    private String institutionName;
    
    /**
     * 项目地区
     */
    @NotBlank(message = "项目地区不能为空")
    @Size(max = 100, message = "项目地区长度不能超过100个字符")
    private String region;
    
    /**
     * 项目优先级
     */
    @NotNull(message = "项目优先级不能为空")
    @Min(value = 1, message = "项目优先级最小值为1")
    @Max(value = 3, message = "项目优先级最大值为3")
    private Integer priority;
    
    /**
     * 项目优先级显示名称
     */
    private String priorityDisplay;
    
    /**
     * 项目进度百分比（0-100）
     */
    @Min(value = 0, message = "项目进度不能小于0")
    @Max(value = 100, message = "项目进度不能大于100")
    private Integer progress;
    
    /**
     * 项目标签列表
     */
    private String[] tags;
    
    /**
     * 备注信息
     */
    @Size(max = 500, message = "备注信息长度不能超过500个字符")
    private String notes;
    
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
}

/**
 * 公益项目创建请求DTO
 * 用于接收前端的项目创建请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class CharityProjectCreateDTO {
    
    /**
     * 项目名称
     */
    @NotBlank(message = "项目名称不能为空")
    @Size(max = 100, message = "项目名称长度不能超过100个字符")
    private String name;
    
    /**
     * 项目描述
     */
    @NotBlank(message = "项目描述不能为空")
    @Size(max = 1000, message = "项目描述长度不能超过1000个字符")
    private String description;
    
    /**
     * 项目类型
     */
    @NotBlank(message = "项目类型不能为空")
    @Pattern(regexp = "^(education|medical|environment|poverty|disaster|other)$", 
             message = "项目类型必须是：education、medical、environment、poverty、disaster、other 中的一种")
    private String type;
    
    /**
     * 项目状态
     */
    @NotBlank(message = "项目状态不能为空")
    @Pattern(regexp = "^(planning|active|completed|suspended|cancelled)$", 
             message = "项目状态必须是：planning、active、completed、suspended、cancelled 中的一种")
    private String status;
    
    /**
     * 项目开始日期
     */
    @NotNull(message = "项目开始日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    /**
     * 项目结束日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    /**
     * 目标受益人数
     */
    @NotNull(message = "目标受益人数不能为空")
    @Min(value = 1, message = "目标受益人数必须大于0")
    private Integer targetBeneficiaries;
    
    /**
     * 项目预算（单位：元）
     */
    @NotNull(message = "项目预算不能为空")
    @DecimalMin(value = "0", message = "项目预算不能为负数")
    private BigDecimal budget;
    
    /**
     * 项目负责人
     */
    @NotBlank(message = "项目负责人不能为空")
    @Size(max = 50, message = "项目负责人长度不能超过50个字符")
    private String manager;
    
    /**
     * 项目负责人联系方式
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$|^0\\d{2,3}-?\\d{7,8}$", 
             message = "联系方式格式不正确")
    private String managerContact;
    
    /**
     * 合作机构ID
     */
    private Long institutionId;
    
    /**
     * 项目地区
     */
    @NotBlank(message = "项目地区不能为空")
    @Size(max = 100, message = "项目地区长度不能超过100个字符")
    private String region;
    
    /**
     * 项目优先级
     */
    @NotNull(message = "项目优先级不能为空")
    @Min(value = 1, message = "项目优先级最小值为1")
    @Max(value = 3, message = "项目优先级最大值为3")
    private Integer priority;
    
    /**
     * 项目标签
     */
    @Size(max = 200, message = "项目标签长度不能超过200个字符")
    private String tags;
    
    /**
     * 备注信息
     */
    @Size(max = 500, message = "备注信息长度不能超过500个字符")
    private String notes;
}

/**
 * 公益项目更新请求DTO
 * 用于接收前端的项目更新请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class CharityProjectUpdateDTO {
    
    /**
     * 项目ID
     */
    @NotNull(message = "项目ID不能为空")
    private Long id;
    
    /**
     * 项目名称
     */
    @NotBlank(message = "项目名称不能为空")
    @Size(max = 100, message = "项目名称长度不能超过100个字符")
    private String name;
    
    /**
     * 项目描述
     */
    @NotBlank(message = "项目描述不能为空")
    @Size(max = 1000, message = "项目描述长度不能超过1000个字符")
    private String description;
    
    /**
     * 项目类型
     */
    @NotBlank(message = "项目类型不能为空")
    @Pattern(regexp = "^(education|medical|environment|poverty|disaster|other)$", 
             message = "项目类型必须是：education、medical、environment、poverty、disaster、other 中的一种")
    private String type;
    
    /**
     * 项目状态
     */
    @NotBlank(message = "项目状态不能为空")
    @Pattern(regexp = "^(planning|active|completed|suspended|cancelled)$", 
             message = "项目状态必须是：planning、active、completed、suspended、cancelled 中的一种")
    private String status;
    
    /**
     * 项目开始日期
     */
    @NotNull(message = "项目开始日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    /**
     * 项目结束日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    /**
     * 目标受益人数
     */
    @NotNull(message = "目标受益人数不能为空")
    @Min(value = 1, message = "目标受益人数必须大于0")
    private Integer targetBeneficiaries;
    
    /**
     * 实际受益人数
     */
    @Min(value = 0, message = "实际受益人数不能为负数")
    private Integer actualBeneficiaries;
    
    /**
     * 项目预算（单位：元）
     */
    @NotNull(message = "项目预算不能为空")
    @DecimalMin(value = "0", message = "项目预算不能为负数")
    private BigDecimal budget;
    
    /**
     * 实际支出（单位：元）
     */
    @DecimalMin(value = "0", message = "实际支出不能为负数")
    private BigDecimal actualCost;
    
    /**
     * 项目负责人
     */
    @NotBlank(message = "项目负责人不能为空")
    @Size(max = 50, message = "项目负责人长度不能超过50个字符")
    private String manager;
    
    /**
     * 项目负责人联系方式
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$|^0\\d{2,3}-?\\d{7,8}$", 
             message = "联系方式格式不正确")
    private String managerContact;
    
    /**
     * 合作机构ID
     */
    private Long institutionId;
    
    /**
     * 项目地区
     */
    @NotBlank(message = "项目地区不能为空")
    @Size(max = 100, message = "项目地区长度不能超过100个字符")
    private String region;
    
    /**
     * 项目优先级
     */
    @NotNull(message = "项目优先级不能为空")
    @Min(value = 1, message = "项目优先级最小值为1")
    @Max(value = 3, message = "项目优先级最大值为3")
    private Integer priority;
    
    /**
     * 项目进度百分比（0-100）
     */
    @Min(value = 0, message = "项目进度不能小于0")
    @Max(value = 100, message = "项目进度不能大于100")
    private Integer progress;
    
    /**
     * 项目标签
     */
    @Size(max = 200, message = "项目标签长度不能超过200个字符")
    private String tags;
    
    /**
     * 备注信息
     */
    @Size(max = 500, message = "备注信息长度不能超过500个字符")
    private String notes;
}