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
 * 公益活动数据传输对象
 * 用于前后端数据传输，包含活动信息的展示和管理
 * 
 * @author YXRobot
 * @version 1.0
 * @since 2024-12-18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharityActivityDTO {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 关联的项目ID
     */
    private Long projectId;
    
    /**
     * 关联的项目名称
     */
    private String projectName;
    
    /**
     * 活动标题
     */
    @NotBlank(message = "活动标题不能为空")
    @Size(max = 100, message = "活动标题长度不能超过100个字符")
    private String title;
    
    /**
     * 活动描述
     */
    @NotBlank(message = "活动描述不能为空")
    @Size(max = 1000, message = "活动描述长度不能超过1000个字符")
    private String description;
    
    /**
     * 活动类型
     */
    @NotBlank(message = "活动类型不能为空")
    @Pattern(regexp = "^(visit|training|donation|event|volunteer|fundraising|education|medical|other)$", 
             message = "活动类型必须是：visit、training、donation、event、volunteer、fundraising、education、medical、other 中的一种")
    private String type;
    
    /**
     * 活动类型显示名称
     */
    private String typeDisplay;
    
    /**
     * 活动日期
     */
    @NotNull(message = "活动日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    
    /**
     * 活动开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    
    /**
     * 活动结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    
    /**
     * 活动地点
     */
    @NotBlank(message = "活动地点不能为空")
    @Size(max = 200, message = "活动地点长度不能超过200个字符")
    private String location;
    
    /**
     * 参与人数
     */
    @NotNull(message = "参与人数不能为空")
    @Min(value = 0, message = "参与人数不能为负数")
    private Integer participants;
    
    /**
     * 目标参与人数
     */
    @Min(value = 0, message = "目标参与人数不能为负数")
    private Integer targetParticipants;
    
    /**
     * 组织方
     */
    @NotBlank(message = "组织方不能为空")
    @Size(max = 100, message = "组织方长度不能超过100个字符")
    private String organizer;
    
    /**
     * 活动状态
     */
    @NotBlank(message = "活动状态不能为空")
    @Pattern(regexp = "^(planned|ongoing|completed|cancelled|postponed)$", 
             message = "活动状态必须是：planned、ongoing、completed、cancelled、postponed 中的一种")
    private String status;
    
    /**
     * 活动状态显示名称
     */
    private String statusDisplay;
    
    /**
     * 活动预算（单位：元，前端显示用）
     */
    @NotNull(message = "活动预算不能为空")
    @DecimalMin(value = "0", message = "活动预算不能为负数")
    private BigDecimal budget;
    
    /**
     * 实际费用（单位：元，前端显示用）
     */
    @DecimalMin(value = "0", message = "实际费用不能为负数")
    private BigDecimal actualCost;
    
    /**
     * 活动负责人
     */
    @NotBlank(message = "活动负责人不能为空")
    @Size(max = 50, message = "活动负责人长度不能超过50个字符")
    private String manager;
    
    /**
     * 负责人联系方式
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$|^0\\d{2,3}-?\\d{7,8}$", 
             message = "联系方式格式不正确")
    private String managerContact;
    
    /**
     * 志愿者需求数量
     */
    @Min(value = 0, message = "志愿者需求数量不能为负数")
    private Integer volunteerNeeded;
    
    /**
     * 实际志愿者数量
     */
    @Min(value = 0, message = "实际志愿者数量不能为负数")
    private Integer actualVolunteers;
    
    /**
     * 活动成果描述
     */
    @Size(max = 500, message = "活动成果描述长度不能超过500个字符")
    private String achievements;
    
    /**
     * 活动照片URL列表
     */
    private String[] photos;
    
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
    
    /**
     * 创建人ID
     */
    private Long createBy;
    
    /**
     * 更新人ID
     */
    private Long updateBy;
    
    // 手动添加getter和setter方法，因为Lombok可能没有正确生成
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getProjectId() {
        return projectId;
    }
    
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
    
    public String getProjectName() {
        return projectName;
    }
    
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getTypeDisplay() {
        return typeDisplay;
    }
    
    public void setTypeDisplay(String typeDisplay) {
        this.typeDisplay = typeDisplay;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public Integer getParticipants() {
        return participants;
    }
    
    public void setParticipants(Integer participants) {
        this.participants = participants;
    }
    
    public Integer getTargetParticipants() {
        return targetParticipants;
    }
    
    public void setTargetParticipants(Integer targetParticipants) {
        this.targetParticipants = targetParticipants;
    }
    
    public String getOrganizer() {
        return organizer;
    }
    
    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getStatusDisplay() {
        return statusDisplay;
    }
    
    public void setStatusDisplay(String statusDisplay) {
        this.statusDisplay = statusDisplay;
    }
    
    public BigDecimal getBudget() {
        return budget;
    }
    
    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }
    
    public BigDecimal getActualCost() {
        return actualCost;
    }
    
    public void setActualCost(BigDecimal actualCost) {
        this.actualCost = actualCost;
    }
    
    public String getManager() {
        return manager;
    }
    
    public void setManager(String manager) {
        this.manager = manager;
    }
    
    public String getManagerContact() {
        return managerContact;
    }
    
    public void setManagerContact(String managerContact) {
        this.managerContact = managerContact;
    }
    
    public Integer getVolunteerNeeded() {
        return volunteerNeeded;
    }
    
    public void setVolunteerNeeded(Integer volunteerNeeded) {
        this.volunteerNeeded = volunteerNeeded;
    }
    
    public Integer getActualVolunteers() {
        return actualVolunteers;
    }
    
    public void setActualVolunteers(Integer actualVolunteers) {
        this.actualVolunteers = actualVolunteers;
    }
    
    public String getAchievements() {
        return achievements;
    }
    
    public void setAchievements(String achievements) {
        this.achievements = achievements;
    }
    
    public String[] getPhotos() {
        return photos;
    }
    
    public void setPhotos(String[] photos) {
        this.photos = photos;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
    
    public Long getCreateBy() {
        return createBy;
    }
    
    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }
    
    public Long getUpdateBy() {
        return updateBy;
    }
    
    public void setUpdateBy(Long updateBy) {
        this.updateBy = updateBy;
    }
}

/**
 * 公益活动创建请求DTO
 * 用于接收前端的活动创建请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class CharityActivityCreateDTO {
    
    /**
     * 关联的项目ID
     */
    @NotNull(message = "关联项目ID不能为空")
    private Long projectId;
    
    /**
     * 活动标题
     */
    @NotBlank(message = "活动标题不能为空")
    @Size(max = 100, message = "活动标题长度不能超过100个字符")
    private String title;
    
    /**
     * 活动描述
     */
    @NotBlank(message = "活动描述不能为空")
    @Size(max = 1000, message = "活动描述长度不能超过1000个字符")
    private String description;
    
    /**
     * 活动类型
     */
    @NotBlank(message = "活动类型不能为空")
    @Pattern(regexp = "^(visit|training|donation|event|volunteer|fundraising|education|medical|other)$", 
             message = "活动类型必须是：visit、training、donation、event、volunteer、fundraising、education、medical、other 中的一种")
    private String type;
    
    /**
     * 活动日期
     */
    @NotNull(message = "活动日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    
    /**
     * 活动开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    
    /**
     * 活动结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    
    /**
     * 活动地点
     */
    @NotBlank(message = "活动地点不能为空")
    @Size(max = 200, message = "活动地点长度不能超过200个字符")
    private String location;
    
    /**
     * 目标参与人数
     */
    @Min(value = 0, message = "目标参与人数不能为负数")
    private Integer targetParticipants;
    
    /**
     * 组织方
     */
    @NotBlank(message = "组织方不能为空")
    @Size(max = 100, message = "组织方长度不能超过100个字符")
    private String organizer;
    
    /**
     * 活动状态
     */
    @NotBlank(message = "活动状态不能为空")
    @Pattern(regexp = "^(planned|ongoing|completed|cancelled|postponed)$", 
             message = "活动状态必须是：planned、ongoing、completed、cancelled、postponed 中的一种")
    private String status;
    
    /**
     * 活动预算（单位：元）
     */
    @NotNull(message = "活动预算不能为空")
    @DecimalMin(value = "0", message = "活动预算不能为负数")
    private BigDecimal budget;
    
    /**
     * 活动负责人
     */
    @NotBlank(message = "活动负责人不能为空")
    @Size(max = 50, message = "活动负责人长度不能超过50个字符")
    private String manager;
    
    /**
     * 负责人联系方式
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$|^0\\d{2,3}-?\\d{7,8}$", 
             message = "联系方式格式不正确")
    private String managerContact;
    
    /**
     * 志愿者需求数量
     */
    @Min(value = 0, message = "志愿者需求数量不能为负数")
    private Integer volunteerNeeded;
    
    /**
     * 备注信息
     */
    @Size(max = 500, message = "备注信息长度不能超过500个字符")
    private String notes;
}

/**
 * 公益活动更新请求DTO
 * 用于接收前端的活动更新请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class CharityActivityUpdateDTO {
    
    /**
     * 活动ID
     */
    @NotNull(message = "活动ID不能为空")
    private Long id;
    
    /**
     * 关联的项目ID
     */
    @NotNull(message = "关联项目ID不能为空")
    private Long projectId;
    
    /**
     * 活动标题
     */
    @NotBlank(message = "活动标题不能为空")
    @Size(max = 100, message = "活动标题长度不能超过100个字符")
    private String title;
    
    /**
     * 活动描述
     */
    @NotBlank(message = "活动描述不能为空")
    @Size(max = 1000, message = "活动描述长度不能超过1000个字符")
    private String description;
    
    /**
     * 活动类型
     */
    @NotBlank(message = "活动类型不能为空")
    @Pattern(regexp = "^(visit|training|donation|event|volunteer|fundraising|education|medical|other)$", 
             message = "活动类型必须是：visit、training、donation、event、volunteer、fundraising、education、medical、other 中的一种")
    private String type;
    
    /**
     * 活动日期
     */
    @NotNull(message = "活动日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    
    /**
     * 活动开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    
    /**
     * 活动结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    
    /**
     * 活动地点
     */
    @NotBlank(message = "活动地点不能为空")
    @Size(max = 200, message = "活动地点长度不能超过200个字符")
    private String location;
    
    /**
     * 参与人数
     */
    @NotNull(message = "参与人数不能为空")
    @Min(value = 0, message = "参与人数不能为负数")
    private Integer participants;
    
    /**
     * 目标参与人数
     */
    @Min(value = 0, message = "目标参与人数不能为负数")
    private Integer targetParticipants;
    
    /**
     * 组织方
     */
    @NotBlank(message = "组织方不能为空")
    @Size(max = 100, message = "组织方长度不能超过100个字符")
    private String organizer;
    
    /**
     * 活动状态
     */
    @NotBlank(message = "活动状态不能为空")
    @Pattern(regexp = "^(planned|ongoing|completed|cancelled|postponed)$", 
             message = "活动状态必须是：planned、ongoing、completed、cancelled、postponed 中的一种")
    private String status;
    
    /**
     * 活动预算（单位：元）
     */
    @NotNull(message = "活动预算不能为空")
    @DecimalMin(value = "0", message = "活动预算不能为负数")
    private BigDecimal budget;
    
    /**
     * 实际费用（单位：元）
     */
    @DecimalMin(value = "0", message = "实际费用不能为负数")
    private BigDecimal actualCost;
    
    /**
     * 活动负责人
     */
    @NotBlank(message = "活动负责人不能为空")
    @Size(max = 50, message = "活动负责人长度不能超过50个字符")
    private String manager;
    
    /**
     * 负责人联系方式
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$|^0\\d{2,3}-?\\d{7,8}$", 
             message = "联系方式格式不正确")
    private String managerContact;
    
    /**
     * 志愿者需求数量
     */
    @Min(value = 0, message = "志愿者需求数量不能为负数")
    private Integer volunteerNeeded;
    
    /**
     * 实际志愿者数量
     */
    @Min(value = 0, message = "实际志愿者数量不能为负数")
    private Integer actualVolunteers;
    
    /**
     * 活动成果描述
     */
    @Size(max = 500, message = "活动成果描述长度不能超过500个字符")
    private String achievements;
    
    /**
     * 活动照片URL列表
     */
    @Size(max = 1000, message = "活动照片URL长度不能超过1000个字符")
    private String[] photos;
    
    /**
     * 备注信息
     */
    @Size(max = 500, message = "备注信息长度不能超过500个字符")
    private String notes;
}