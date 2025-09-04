package com.yxrobot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 合作机构数据传输对象
 * 用于前后端数据传输，包含机构信息的展示和管理
 * 
 * @author YXRobot
 * @version 1.0
 * @since 2024-12-18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharityInstitutionDTO {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 机构名称
     */
    @NotBlank(message = "机构名称不能为空")
    @Size(max = 100, message = "机构名称长度不能超过100个字符")
    private String name;
    
    /**
     * 机构类型
     */
    @NotBlank(message = "机构类型不能为空")
    @Pattern(regexp = "^(school|hospital|community|ngo|other)$", 
             message = "机构类型必须是：school、hospital、community、ngo、other 中的一种")
    private String type;
    
    /**
     * 机构类型显示名称
     */
    private String typeDisplay;
    
    /**
     * 所在地区
     */
    @NotBlank(message = "所在地区不能为空")
    @Size(max = 50, message = "所在地区长度不能超过50个字符")
    private String location;
    
    /**
     * 详细地址
     */
    @NotBlank(message = "详细地址不能为空")
    @Size(max = 200, message = "详细地址长度不能超过200个字符")
    private String address;
    
    /**
     * 联系人姓名
     */
    @NotBlank(message = "联系人姓名不能为空")
    @Size(max = 50, message = "联系人姓名长度不能超过50个字符")
    private String contactPerson;
    
    /**
     * 联系电话
     */
    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$|^0\\d{2,3}-?\\d{7,8}$", 
             message = "联系电话格式不正确")
    private String contactPhone;
    
    /**
     * 邮箱地址
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱地址长度不能超过100个字符")
    private String email;
    
    /**
     * 学生数量
     */
    @Min(value = 0, message = "学生数量不能为负数")
    private Integer studentCount;
    
    /**
     * 合作开始日期
     */
    @NotNull(message = "合作开始日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate cooperationDate;
    
    /**
     * 机构状态
     */
    @NotBlank(message = "机构状态不能为空")
    @Pattern(regexp = "^(active|inactive|suspended)$", 
             message = "机构状态必须是：active、inactive、suspended 中的一种")
    private String status;
    
    /**
     * 机构状态显示名称
     */
    private String statusDisplay;
    
    /**
     * 设备数量
     */
    @Min(value = 0, message = "设备数量不能为负数")
    private Integer deviceCount;
    
    /**
     * 最后访问日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastVisitDate;
    
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
    
    // 手动添加getter和setter方法，确保Lombok正常工作
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
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
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getContactPerson() {
        return contactPerson;
    }
    
    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }
    
    public String getContactPhone() {
        return contactPhone;
    }
    
    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Integer getStudentCount() {
        return studentCount;
    }
    
    public void setStudentCount(Integer studentCount) {
        this.studentCount = studentCount;
    }
    
    public LocalDate getCooperationDate() {
        return cooperationDate;
    }
    
    public void setCooperationDate(LocalDate cooperationDate) {
        this.cooperationDate = cooperationDate;
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
    
    public Integer getDeviceCount() {
        return deviceCount;
    }
    
    public void setDeviceCount(Integer deviceCount) {
        this.deviceCount = deviceCount;
    }
    
    public LocalDate getLastVisitDate() {
        return lastVisitDate;
    }
    
    public void setLastVisitDate(LocalDate lastVisitDate) {
        this.lastVisitDate = lastVisitDate;
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
}

/**
 * 合作机构创建请求DTO
 * 用于接收前端的机构创建请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class CharityInstitutionCreateDTO {
    
    /**
     * 机构名称
     */
    @NotBlank(message = "机构名称不能为空")
    @Size(max = 100, message = "机构名称长度不能超过100个字符")
    private String name;
    
    /**
     * 机构类型
     */
    @NotBlank(message = "机构类型不能为空")
    @Pattern(regexp = "^(school|hospital|community|ngo|other)$", 
             message = "机构类型必须是：school、hospital、community、ngo、other 中的一种")
    private String type;
    
    /**
     * 所在地区
     */
    @NotBlank(message = "所在地区不能为空")
    @Size(max = 50, message = "所在地区长度不能超过50个字符")
    private String location;
    
    /**
     * 详细地址
     */
    @NotBlank(message = "详细地址不能为空")
    @Size(max = 200, message = "详细地址长度不能超过200个字符")
    private String address;
    
    /**
     * 联系人姓名
     */
    @NotBlank(message = "联系人姓名不能为空")
    @Size(max = 50, message = "联系人姓名长度不能超过50个字符")
    private String contactPerson;
    
    /**
     * 联系电话
     */
    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$|^0\\d{2,3}-?\\d{7,8}$", 
             message = "联系电话格式不正确")
    private String contactPhone;
    
    /**
     * 邮箱地址
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱地址长度不能超过100个字符")
    private String email;
    
    /**
     * 学生数量
     */
    @Min(value = 0, message = "学生数量不能为负数")
    private Integer studentCount;
    
    /**
     * 合作开始日期
     */
    @NotNull(message = "合作开始日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate cooperationDate;
    
    /**
     * 机构状态
     */
    @NotBlank(message = "机构状态不能为空")
    @Pattern(regexp = "^(active|inactive|suspended)$", 
             message = "机构状态必须是：active、inactive、suspended 中的一种")
    private String status;
    
    /**
     * 设备数量
     */
    @Min(value = 0, message = "设备数量不能为负数")
    private Integer deviceCount;
    
    /**
     * 备注信息
     */
    @Size(max = 500, message = "备注信息长度不能超过500个字符")
    private String notes;
}

/**
 * 合作机构更新请求DTO
 * 用于接收前端的机构更新请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class CharityInstitutionUpdateDTO {
    
    /**
     * 机构ID
     */
    @NotNull(message = "机构ID不能为空")
    private Long id;
    
    /**
     * 机构名称
     */
    @NotBlank(message = "机构名称不能为空")
    @Size(max = 100, message = "机构名称长度不能超过100个字符")
    private String name;
    
    /**
     * 机构类型
     */
    @NotBlank(message = "机构类型不能为空")
    @Pattern(regexp = "^(school|hospital|community|ngo|other)$", 
             message = "机构类型必须是：school、hospital、community、ngo、other 中的一种")
    private String type;
    
    /**
     * 所在地区
     */
    @NotBlank(message = "所在地区不能为空")
    @Size(max = 50, message = "所在地区长度不能超过50个字符")
    private String location;
    
    /**
     * 详细地址
     */
    @NotBlank(message = "详细地址不能为空")
    @Size(max = 200, message = "详细地址长度不能超过200个字符")
    private String address;
    
    /**
     * 联系人姓名
     */
    @NotBlank(message = "联系人姓名不能为空")
    @Size(max = 50, message = "联系人姓名长度不能超过50个字符")
    private String contactPerson;
    
    /**
     * 联系电话
     */
    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$|^0\\d{2,3}-?\\d{7,8}$", 
             message = "联系电话格式不正确")
    private String contactPhone;
    
    /**
     * 邮箱地址
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱地址长度不能超过100个字符")
    private String email;
    
    /**
     * 学生数量
     */
    @Min(value = 0, message = "学生数量不能为负数")
    private Integer studentCount;
    
    /**
     * 合作开始日期
     */
    @NotNull(message = "合作开始日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate cooperationDate;
    
    /**
     * 机构状态
     */
    @NotBlank(message = "机构状态不能为空")
    @Pattern(regexp = "^(active|inactive|suspended)$", 
             message = "机构状态必须是：active、inactive、suspended 中的一种")
    private String status;
    
    /**
     * 设备数量
     */
    @Min(value = 0, message = "设备数量不能为负数")
    private Integer deviceCount;
    
    /**
     * 最后访问日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastVisitDate;
    
    /**
     * 备注信息
     */
    @Size(max = 500, message = "备注信息长度不能超过500个字符")
    private String notes;
}