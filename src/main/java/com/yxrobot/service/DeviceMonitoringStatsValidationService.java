package com.yxrobot.service;

import com.yxrobot.dto.DeviceMonitoringStatsDTO;
import com.yxrobot.util.DeviceMonitoringStatsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 设备监控统计数据验证服务
 * 确保统计数据的准确性和一致性
 * 
 * @author YXRobot Development Team
 * @since 2024
 */
@Service
public class DeviceMonitoringStatsValidationService {
    
    private static final Logger logger = LoggerFactory.getLogger(DeviceMonitoringStatsValidationService.class);
    
    /**
     * 验证统计数据的完整性和一致性
     * 
     * @param stats 统计数据
     * @return 验证结果
     */
    public ValidationResult validateStats(DeviceMonitoringStatsDTO stats) {
        logger.debug("开始验证统计数据");
        
        ValidationResult result = new ValidationResult();
        
        if (stats == null) {
            result.addError("统计数据不能为空");
            return result;
        }
        
        // 1. 验证基本数据完整性
        validateBasicData(stats, result);
        
        // 2. 验证数据一致性
        validateDataConsistency(stats, result);
        
        // 3. 验证数据合理性
        validateDataReasonableness(stats, result);
        
        // 4. 验证趋势数据
        validateTrendData(stats, result);
        
        logger.debug("统计数据验证完成，发现{}个错误，{}个警告", 
                    result.getErrors().size(), result.getWarnings().size());
        
        return result;
    }
    
    /**
     * 验证基本数据完整性
     */
    private void validateBasicData(DeviceMonitoringStatsDTO stats, ValidationResult result) {
        // 检查必填字段
        if (stats.getOnline() == null) {
            result.addError("在线设备数量不能为空");
        }
        
        if (stats.getOffline() == null) {
            result.addError("离线设备数量不能为空");
        }
        
        if (stats.getError() == null) {
            result.addError("故障设备数量不能为空");
        }
        
        if (stats.getTotal() == null) {
            result.addError("设备总数不能为空");
        }
        
        if (stats.getAvgPerformance() == null) {
            result.addError("平均性能不能为空");
        }
        
        // 检查数值范围
        if (stats.getOnline() != null && stats.getOnline() < 0) {
            result.addError("在线设备数量不能为负数");
        }
        
        if (stats.getOffline() != null && stats.getOffline() < 0) {
            result.addError("离线设备数量不能为负数");
        }
        
        if (stats.getError() != null && stats.getError() < 0) {
            result.addError("故障设备数量不能为负数");
        }
        
        if (stats.getTotal() != null && stats.getTotal() < 0) {
            result.addError("设备总数不能为负数");
        }
        
        if (stats.getAvgPerformance() != null && 
            (stats.getAvgPerformance().compareTo(BigDecimal.ZERO) < 0 || 
             stats.getAvgPerformance().compareTo(BigDecimal.valueOf(100)) > 0)) {
            result.addError("平均性能必须在0-100之间");
        }
    }
    
    /**
     * 验证数据一致性
     */
    private void validateDataConsistency(DeviceMonitoringStatsDTO stats, ValidationResult result) {
        // 验证总数一致性
        if (!DeviceMonitoringStatsUtil.validateStatsConsistency(stats)) {
            result.addError("各状态设备数量之和与总数不一致");
        }
        
        // 验证维护设备数量
        if (stats.getMaintenance() != null && stats.getMaintenance() < 0) {
            result.addError("维护中设备数量不能为负数");
        }
        
        // 验证设备总数不能为0但各状态都有设备
        if (stats.getTotal() != null && stats.getTotal() == 0) {
            if ((stats.getOnline() != null && stats.getOnline() > 0) ||
                (stats.getOffline() != null && stats.getOffline() > 0) ||
                (stats.getError() != null && stats.getError() > 0)) {
                result.addError("设备总数为0但存在各状态设备");
            }
        }
    }
    
    /**
     * 验证数据合理性
     */
    private void validateDataReasonableness(DeviceMonitoringStatsDTO stats, ValidationResult result) {
        // 检查异常高的故障率
        if (stats.getTotal() != null && stats.getTotal() > 0 && 
            stats.getError() != null && stats.getError() > 0) {
            
            BigDecimal errorRate = DeviceMonitoringStatsUtil.calculatePercentage(stats.getError(), stats.getTotal());
            if (errorRate.compareTo(BigDecimal.valueOf(50)) > 0) {
                result.addWarning("故障设备比例过高：" + errorRate + "%");
            }
        }
        
        // 检查异常低的在线率
        if (stats.getTotal() != null && stats.getTotal() > 0 && 
            stats.getOnline() != null) {
            
            BigDecimal onlineRate = DeviceMonitoringStatsUtil.calculatePercentage(stats.getOnline(), stats.getTotal());
            if (onlineRate.compareTo(BigDecimal.valueOf(20)) < 0) {
                result.addWarning("在线设备比例过低：" + onlineRate + "%");
            }
        }
        
        // 检查异常低的平均性能
        if (stats.getAvgPerformance() != null && 
            stats.getAvgPerformance().compareTo(BigDecimal.valueOf(30)) < 0) {
            result.addWarning("平均性能过低：" + stats.getAvgPerformance() + "%");
        }
        
        // 检查设备总数异常
        if (stats.getTotal() != null && stats.getTotal() > 10000) {
            result.addWarning("设备总数异常高：" + stats.getTotal());
        }
    }
    
    /**
     * 验证趋势数据
     */
    private void validateTrendData(DeviceMonitoringStatsDTO stats, ValidationResult result) {
        // 检查趋势数据的合理性
        if (stats.getOnlineTrend() != null && Math.abs(stats.getOnlineTrend()) > 1000) {
            result.addWarning("在线设备趋势变化异常大：" + stats.getOnlineTrend());
        }
        
        if (stats.getErrorTrend() != null && stats.getErrorTrend() > 100) {
            result.addWarning("故障设备趋势增长异常：" + stats.getErrorTrend());
        }
        
        if (stats.getPerformanceTrend() != null && Math.abs(stats.getPerformanceTrend()) > 50) {
            result.addWarning("性能趋势变化异常大：" + stats.getPerformanceTrend());
        }
    }
    
    /**
     * 修复统计数据中的常见问题
     * 
     * @param stats 统计数据
     * @return 修复后的统计数据
     */
    public DeviceMonitoringStatsDTO fixCommonIssues(DeviceMonitoringStatsDTO stats) {
        if (stats == null) {
            return new DeviceMonitoringStatsDTO();
        }
        
        logger.debug("开始修复统计数据中的常见问题");
        
        // 确保所有数值字段不为null
        if (stats.getOnline() == null) stats.setOnline(0);
        if (stats.getOffline() == null) stats.setOffline(0);
        if (stats.getError() == null) stats.setError(0);
        if (stats.getMaintenance() == null) stats.setMaintenance(0);
        if (stats.getAvgPerformance() == null) stats.setAvgPerformance(BigDecimal.ZERO);
        
        // 修复总数不一致问题
        int calculatedTotal = stats.getOnline() + stats.getOffline() + 
                             stats.getError() + stats.getMaintenance();
        if (stats.getTotal() == null || !stats.getTotal().equals(calculatedTotal)) {
            stats.setTotal(calculatedTotal);
            logger.debug("修复了设备总数不一致问题");
        }
        
        // 确保趋势数据不为null
        if (stats.getOnlineTrend() == null) stats.setOnlineTrend(0);
        if (stats.getOfflineTrend() == null) stats.setOfflineTrend(0);
        if (stats.getErrorTrend() == null) stats.setErrorTrend(0);
        if (stats.getPerformanceTrend() == null) stats.setPerformanceTrend(0);
        
        // 修复性能值超出范围问题
        if (stats.getAvgPerformance().compareTo(BigDecimal.ZERO) < 0) {
            stats.setAvgPerformance(BigDecimal.ZERO);
            logger.debug("修复了负数平均性能问题");
        }
        if (stats.getAvgPerformance().compareTo(BigDecimal.valueOf(100)) > 0) {
            stats.setAvgPerformance(BigDecimal.valueOf(100));
            logger.debug("修复了超出100%的平均性能问题");
        }
        
        logger.debug("统计数据修复完成");
        return stats;
    }
    
    /**
     * 验证结果类
     */
    public static class ValidationResult {
        private final List<String> errors = new ArrayList<>();
        private final List<String> warnings = new ArrayList<>();
        
        public void addError(String error) {
            errors.add(error);
        }
        
        public void addWarning(String warning) {
            warnings.add(warning);
        }
        
        public List<String> getErrors() {
            return errors;
        }
        
        public List<String> getWarnings() {
            return warnings;
        }
        
        public boolean hasErrors() {
            return !errors.isEmpty();
        }
        
        public boolean hasWarnings() {
            return !warnings.isEmpty();
        }
        
        public boolean isValid() {
            return !hasErrors();
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("ValidationResult{");
            sb.append("errors=").append(errors.size());
            sb.append(", warnings=").append(warnings.size());
            sb.append("}");
            return sb.toString();
        }
    }
}