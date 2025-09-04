package com.yxrobot.validator;

import com.yxrobot.service.OrderValidationService;
import com.yxrobot.validator.ValidationResultCollector.ValidationError;
import com.yxrobot.validator.ValidationResultCollector.ValidationWarning;

import java.util.ArrayList;
import java.util.List;

/**
 * 验证结果汇总类
 * 用于汇总多个验证器的验证结果
 */
public class ValidationResultSummary {
    
    private List<OrderValidationService.ValidationError> allErrors;
    private List<OrderValidationService.ValidationError> allWarnings;
    private List<String> validationSteps;
    private int errorCount;
    private int warningCount;
    private boolean valid;
    private List<ValidationError> errors;
    private List<ValidationWarning> warnings;
    
    public ValidationResultSummary() {
        this.allErrors = new ArrayList<>();
        this.allWarnings = new ArrayList<>();
        this.validationSteps = new ArrayList<>();
        this.errors = new ArrayList<>();
        this.warnings = new ArrayList<>();
    }
    
    /**
     * 添加验证结果
     * 
     * @param stepName 验证步骤名称
     * @param result 验证结果
     */
    public void addValidationResult(String stepName, OrderValidationService.ValidationResult result) {
        validationSteps.add(stepName);
        
        if (result.hasErrors()) {
            for (OrderValidationService.ValidationError error : result.getErrors()) {
                allErrors.add(new OrderValidationService.ValidationError(
                    stepName + "_" + error.getCode(), 
                    "[" + stepName + "] " + error.getMessage()
                ));
            }
        }
        
        if (result.hasWarnings()) {
            for (OrderValidationService.ValidationError warning : result.getWarnings()) {
                allWarnings.add(new OrderValidationService.ValidationError(
                    stepName + "_" + warning.getCode(), 
                    "[" + stepName + "] " + warning.getMessage()
                ));
            }
        }
    }
    
    /**
     * 添加单个错误
     * 
     * @param stepName 验证步骤名称
     * @param code 错误代码
     * @param message 错误消息
     */
    public void addError(String stepName, String code, String message) {
        allErrors.add(new OrderValidationService.ValidationError(
            stepName + "_" + code, 
            "[" + stepName + "] " + message
        ));
    }
    
    /**
     * 添加单个警告
     * 
     * @param stepName 验证步骤名称
     * @param code 警告代码
     * @param message 警告消息
     */
    public void addWarning(String stepName, String code, String message) {
        allWarnings.add(new OrderValidationService.ValidationError(
            stepName + "_" + code, 
            "[" + stepName + "] " + message
        ));
    }
    
    /**
     * 是否有错误
     */
    public boolean hasErrors() {
        return !allErrors.isEmpty();
    }
    
    /**
     * 是否有警告
     */
    public boolean hasWarnings() {
        return !allWarnings.isEmpty();
    }
    
    /**
     * 验证是否通过（无错误）
     */
    public boolean isValid() {
        return !hasErrors();
    }
    
    /**
     * 获取所有错误
     */
    public List<OrderValidationService.ValidationError> getAllErrors() {
        return allErrors;
    }
    
    /**
     * 获取所有警告
     */
    public List<OrderValidationService.ValidationError> getAllWarnings() {
        return allWarnings;
    }
    
    /**
     * 获取验证步骤列表
     */
    public List<String> getValidationSteps() {
        return validationSteps;
    }
    
    /**
     * 获取错误数量
     */
    public int getErrorCount() {
        return allErrors.size();
    }
    
    /**
     * 获取警告数量
     */
    public int getWarningCount() {
        return allWarnings.size();
    }
    
    /**
     * 获取验证步骤数量
     */
    public int getStepCount() {
        return validationSteps.size();
    }
    
    /**
     * 获取错误消息摘要
     */
    public String getErrorSummary() {
        if (!hasErrors()) {
            return "无错误";
        }
        
        StringBuilder summary = new StringBuilder();
        summary.append("共发现 ").append(getErrorCount()).append(" 个错误：");
        
        for (int i = 0; i < allErrors.size() && i < 5; i++) {
            summary.append("\n- ").append(allErrors.get(i).getMessage());
        }
        
        if (allErrors.size() > 5) {
            summary.append("\n- 还有 ").append(allErrors.size() - 5).append(" 个错误...");
        }
        
        return summary.toString();
    }
    
    /**
     * 获取警告消息摘要
     */
    public String getWarningSummary() {
        if (!hasWarnings()) {
            return "无警告";
        }
        
        StringBuilder summary = new StringBuilder();
        summary.append("共发现 ").append(getWarningCount()).append(" 个警告：");
        
        for (int i = 0; i < allWarnings.size() && i < 3; i++) {
            summary.append("\n- ").append(allWarnings.get(i).getMessage());
        }
        
        if (allWarnings.size() > 3) {
            summary.append("\n- 还有 ").append(allWarnings.size() - 3).append(" 个警告...");
        }
        
        return summary.toString();
    }
    
    /**
     * 获取完整的验证报告
     */
    public String getFullReport() {
        StringBuilder report = new StringBuilder();
        
        report.append("=== 订单数据验证报告 ===\n");
        report.append("验证步骤数量: ").append(getStepCount()).append("\n");
        report.append("验证步骤: ").append(String.join(", ", validationSteps)).append("\n");
        report.append("错误数量: ").append(getErrorCount()).append("\n");
        report.append("警告数量: ").append(getWarningCount()).append("\n");
        report.append("验证结果: ").append(isValid() ? "通过" : "失败").append("\n");
        
        if (hasErrors()) {
            report.append("\n=== 错误详情 ===\n");
            for (OrderValidationService.ValidationError error : allErrors) {
                report.append("- [").append(error.getCode()).append("] ").append(error.getMessage()).append("\n");
            }
        }
        
        if (hasWarnings()) {
            report.append("\n=== 警告详情 ===\n");
            for (OrderValidationService.ValidationError warning : allWarnings) {
                report.append("- [").append(warning.getCode()).append("] ").append(warning.getMessage()).append("\n");
            }
        }
        
        report.append("\n=== 报告结束 ===");
        
        return report.toString();
    }
    
    /**
     * 清空所有验证结果
     */
    public void clear() {
        allErrors.clear();
        allWarnings.clear();
        validationSteps.clear();
    }
    
    // 添加缺失的setter方法
    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }
    
    public void setWarningCount(int warningCount) {
        this.warningCount = warningCount;
    }
    
    public void setErrors(List<ValidationError> errors) {
        this.errors = errors;
    }
    
    public void setWarnings(List<ValidationWarning> warnings) {
        this.warnings = warnings;
    }
    
    public void setValid(boolean valid) {
        this.valid = valid;
    }
    
    @Override
    public String toString() {
        return String.format("ValidationResultSummary{steps=%d, errors=%d, warnings=%d, valid=%s}", 
            getStepCount(), getErrorCount(), getWarningCount(), isValid());
    }
}