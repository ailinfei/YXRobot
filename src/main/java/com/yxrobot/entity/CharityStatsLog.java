package com.yxrobot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * 公益统计数据更新日志实体类
 * 用于记录统计数据的更新历史，支持审计和问题排查
 * 
 * @author YXRobot
 * @version 1.0
 * @since 2024-12-18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharityStatsLog {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 关联的统计数据ID
     */
    @NotNull(message = "统计数据ID不能为空")
    private Long statsId;
    
    /**
     * 操作类型
     * 可选值：create（创建）、update（更新）、delete（删除）
     */
    @NotBlank(message = "操作类型不能为空")
    @Pattern(regexp = "^(create|update|delete)$", 
             message = "操作类型必须是：create、update、delete 中的一种")
    private String operationType;
    
    /**
     * 更新字段名称
     * 记录具体更新了哪些字段，多个字段用逗号分隔
     */
    @Size(max = 500, message = "更新字段名称长度不能超过500个字符")
    private String updatedFields;
    
    /**
     * 更新前的数据（JSON格式）
     * 记录更新前的完整数据快照
     */
    @Size(max = 2000, message = "更新前数据长度不能超过2000个字符")
    private String beforeData;
    
    /**
     * 更新后的数据（JSON格式）
     * 记录更新后的完整数据快照
     */
    @Size(max = 2000, message = "更新后数据长度不能超过2000个字符")
    private String afterData;
    
    /**
     * 更新原因
     */
    @NotBlank(message = "更新原因不能为空")
    @Size(max = 200, message = "更新原因长度不能超过200个字符")
    private String updateReason;
    
    /**
     * 操作人ID
     */
    @NotNull(message = "操作人ID不能为空")
    private Long operatorId;
    
    /**
     * 操作人姓名
     */
    @NotBlank(message = "操作人姓名不能为空")
    @Size(max = 50, message = "操作人姓名长度不能超过50个字符")
    private String operatorName;
    
    /**
     * 操作IP地址
     */
    @Pattern(regexp = "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$|^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$", 
             message = "IP地址格式不正确")
    private String operatorIp;
    
    /**
     * 操作时间
     */
    @NotNull(message = "操作时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operationTime;
    
    /**
     * 数据版本号
     * 记录操作时的数据版本
     */
    private Integer dataVersion;
    
    /**
     * 操作结果
     * 可选值：success（成功）、failed（失败）
     */
    @NotBlank(message = "操作结果不能为空")
    @Pattern(regexp = "^(success|failed)$", 
             message = "操作结果必须是：success、failed 中的一种")
    private String operationResult;
    
    /**
     * 错误信息
     * 当操作失败时记录具体的错误信息
     */
    @Size(max = 500, message = "错误信息长度不能超过500个字符")
    private String errorMessage;
    
    /**
     * 备注信息
     */
    @Size(max = 300, message = "备注信息长度不能超过300个字符")
    private String notes;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    // 手动添加setter方法，确保Lombok正常工作
    public void setStatsId(Long statsId) {
        this.statsId = statsId;
    }
    
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }
    
    public void setUpdatedFields(String updatedFields) {
        this.updatedFields = updatedFields;
    }
    
    public void setBeforeData(String beforeData) {
        this.beforeData = beforeData;
    }
    
    public void setAfterData(String afterData) {
        this.afterData = afterData;
    }
    
    public void setUpdateReason(String updateReason) {
        this.updateReason = updateReason;
    }
    
    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }
    
    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }
    
    public void setOperatorIp(String operatorIp) {
        this.operatorIp = operatorIp;
    }
    
    public void setOperationTime(LocalDateTime operationTime) {
        this.operationTime = operationTime;
    }
    
    public void setDataVersion(Integer dataVersion) {
        this.dataVersion = dataVersion;
    }
    
    public void setOperationResult(String operationResult) {
        this.operationResult = operationResult;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    // 手动添加getter方法，确保Lombok正常工作
    public Long getId() {
        return id;
    }
    
    public Long getStatsId() {
        return statsId;
    }
    
    public String getOperationType() {
        return operationType;
    }
    
    public String getUpdatedFields() {
        return updatedFields;
    }
    
    public String getBeforeData() {
        return beforeData;
    }
    
    public String getAfterData() {
        return afterData;
    }
    
    public String getUpdateReason() {
        return updateReason;
    }
    
    public Long getOperatorId() {
        return operatorId;
    }
    
    public String getOperatorName() {
        return operatorName;
    }
    
    public String getOperatorIp() {
        return operatorIp;
    }
    
    public LocalDateTime getOperationTime() {
        return operationTime;
    }
    
    public Integer getDataVersion() {
        return dataVersion;
    }
    
    public String getOperationResult() {
        return operationResult;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
}