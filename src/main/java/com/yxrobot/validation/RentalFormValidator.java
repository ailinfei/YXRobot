package com.yxrobot.validation;

import com.yxrobot.entity.RentalCustomer;
import com.yxrobot.entity.RentalDevice;
import com.yxrobot.entity.RentalRecord;
import com.yxrobot.exception.RentalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 租赁表单数据验证器
 * 专门用于验证表单提交的数据完整性和格式正确性
 * 
 * @author Kiro
 * @date 2025-01-28
 */
@Component
public class RentalFormValidator {
    
    @Autowired
    public RentalValidator rentalValidator;
    
    /**
     * 验证租赁记录表单数据
     * 确保必填字段完整性和数据格式正确性
     * 
     * @param rentalRecord 租赁记录对象
     * @return 验证错误列表
     */
    public List<String> validateRentalRecordForm(RentalRecord rentalRecord) {
        List<String> errors = new ArrayList<>();
        
        if (rentalRecord == null) {
            errors.add("租赁记录数据不能为空");
            return errors;
        }
        
        try {
            // 验证必填字段
            validateRequiredFields(rentalRecord, errors);
            
            // 验证数据格式
            validateDataFormats(rentalRecord, errors);
            
            // 验证业务逻辑
            validateBusinessLogic(rentalRecord, errors);
            
        } catch (RentalException e) {
            errors.add(e.getMessage());
        } catch (Exception e) {
            errors.add("数据验证过程中发生错误: " + e.getMessage());
        }
        
        return errors;
    }
    
    /**
     * 验证客户表单数据
     * 
     * @param customer 客户对象
     * @return 验证错误列表
     */
    public List<String> validateCustomerForm(RentalCustomer customer) {
        List<String> errors = new ArrayList<>();
        
        if (customer == null) {
            errors.add("客户数据不能为空");
            return errors;
        }
        
        try {
            // 验证客户名称
            if (!StringUtils.hasText(customer.getCustomerName())) {
                errors.add("客户名称不能为空");
            } else {
                rentalValidator.validateCustomerName(customer.getCustomerName());
            }
            
            // 验证客户类型
            if (customer.getCustomerType() == null) {
                errors.add("客户类型不能为空");
            }
            
            // 验证联系电话
            if (StringUtils.hasText(customer.getPhone())) {
                rentalValidator.validatePhone(customer.getPhone());
            }
            
            // 验证邮箱地址
            if (StringUtils.hasText(customer.getEmail())) {
                rentalValidator.validateEmail(customer.getEmail());
            }
            
            // 验证地址信息
            rentalValidator.validateAddress(customer.getAddress());
            rentalValidator.validateRegion(customer.getRegion());
            rentalValidator.validateIndustry(customer.getIndustry());
            
            // 验证信用等级
            rentalValidator.validateCreditLevel(customer.getCreditLevel());
            
        } catch (RentalException e) {
            errors.add(e.getMessage());
        } catch (Exception e) {
            errors.add("客户数据验证过程中发生错误: " + e.getMessage());
        }
        
        return errors;
    }
    
    /**
     * 验证设备表单数据
     * 
     * @param device 设备对象
     * @return 验证错误列表
     */
    public List<String> validateDeviceForm(RentalDevice device) {
        List<String> errors = new ArrayList<>();
        
        if (device == null) {
            errors.add("设备数据不能为空");
            return errors;
        }
        
        try {
            // 验证设备ID
            if (!StringUtils.hasText(device.getDeviceId())) {
                errors.add("设备ID不能为空");
            } else {
                rentalValidator.validateDeviceId(device.getDeviceId());
            }
            
            // 验证设备型号
            if (!StringUtils.hasText(device.getDeviceModel())) {
                errors.add("设备型号不能为空");
            } else {
                rentalValidator.validateDeviceModel(device.getDeviceModel());
            }
            
            // 验证设备名称
            if (!StringUtils.hasText(device.getDeviceName())) {
                errors.add("设备名称不能为空");
            } else {
                rentalValidator.validateDeviceName(device.getDeviceName());
            }
            
            // 验证日租金
            if (device.getDailyRentalPrice() == null) {
                errors.add("日租金不能为空");
            } else {
                rentalValidator.validateRentalFee(device.getDailyRentalPrice());
            }
            
            // 验证性能评分
            rentalValidator.validatePerformanceScore(device.getPerformanceScore());
            
            // 验证信号强度
            rentalValidator.validateSignalStrength(device.getSignalStrength());
            
            // 验证维护状态
            if (device.getMaintenanceStatus() != null) {
                rentalValidator.validateMaintenanceStatus(device.getMaintenanceStatus().getCode());
            }
            
        } catch (RentalException e) {
            errors.add(e.getMessage());
        } catch (Exception e) {
            errors.add("设备数据验证过程中发生错误: " + e.getMessage());
        }
        
        return errors;
    }
    
    /**
     * 验证批量操作表单数据
     * 
     * @param batchData 批量操作数据
     * @return 验证错误列表
     */
    public List<String> validateBatchOperationForm(Map<String, Object> batchData) {
        List<String> errors = new ArrayList<>();
        
        if (batchData == null || batchData.isEmpty()) {
            errors.add("批量操作数据不能为空");
            return errors;
        }
        
        try {
            // 获取操作参数
            @SuppressWarnings("unchecked")
            List<String> ids = (List<String>) batchData.get("ids");
            String operation = (String) batchData.get("operation");
            @SuppressWarnings("unchecked")
            Map<String, Object> params = (Map<String, Object>) batchData.get("params");
            
            // 验证基本参数
            rentalValidator.validateBatchOperationParams(ids, operation);
            
            // 根据操作类型验证特定参数
            switch (operation.toLowerCase()) {
                case "updatestatus":
                    String newStatus = params != null ? (String) params.get("status") : null;
                    if (!StringUtils.hasText(newStatus)) {
                        errors.add("更新状态操作需要提供新状态参数");
                    } else {
                        rentalValidator.validateDeviceStatus(newStatus);
                    }
                    break;
                    
                case "maintenance":
                    String maintenanceStatus = params != null ? (String) params.get("maintenanceStatus") : null;
                    if (StringUtils.hasText(maintenanceStatus)) {
                        rentalValidator.validateMaintenanceStatus(maintenanceStatus);
                    }
                    break;
                    
                case "delete":
                    // 删除操作不需要额外参数验证
                    break;
                    
                default:
                    errors.add("不支持的批量操作类型: " + operation);
                    break;
            }
            
        } catch (RentalException e) {
            errors.add(e.getMessage());
        } catch (Exception e) {
            errors.add("批量操作数据验证过程中发生错误: " + e.getMessage());
        }
        
        return errors;
    }
    
    /**
     * 验证查询参数表单数据
     * 
     * @param queryParams 查询参数
     * @return 验证错误列表
     */
    public List<String> validateQueryParamsForm(Map<String, Object> queryParams) {
        List<String> errors = new ArrayList<>();
        
        if (queryParams == null) {
            return errors; // 查询参数可以为空
        }
        
        try {
            // 验证分页参数
            Integer page = (Integer) queryParams.get("page");
            Integer pageSize = (Integer) queryParams.get("pageSize");
            rentalValidator.validatePaginationParams(page, pageSize);
            
            // 验证搜索关键词
            String keyword = (String) queryParams.get("keyword");
            rentalValidator.validateSearchKeyword(keyword);
            
            // 验证设备状态
            String deviceStatus = (String) queryParams.get("currentStatus");
            rentalValidator.validateDeviceStatus(deviceStatus);
            
            // 验证日期参数
            String startDate = (String) queryParams.get("startDate");
            String endDate = (String) queryParams.get("endDate");
            
            LocalDate start = rentalValidator.validateDateParam(startDate, "startDate");
            LocalDate end = rentalValidator.validateDateParam(endDate, "endDate");
            rentalValidator.validateDateRange(start, end);
            
            // 验证时间周期
            String period = (String) queryParams.get("period");
            rentalValidator.validatePeriodParam(period);
            
            // 验证分布类型
            String type = (String) queryParams.get("type");
            rentalValidator.validateDistributionType(type);
            
            // 验证限制数量
            Integer limit = (Integer) queryParams.get("limit");
            rentalValidator.validateLimitParam(limit);
            
        } catch (RentalException e) {
            errors.add(e.getMessage());
        } catch (Exception e) {
            errors.add("查询参数验证过程中发生错误: " + e.getMessage());
        }
        
        return errors;
    }
    
    /**
     * 验证必填字段
     */
    private void validateRequiredFields(RentalRecord rentalRecord, List<String> errors) {
        if (!StringUtils.hasText(rentalRecord.getRentalOrderNumber())) {
            errors.add("租赁订单号不能为空");
        }
        
        if (rentalRecord.getDeviceId() == null) {
            errors.add("设备ID不能为空");
        }
        
        if (rentalRecord.getCustomerId() == null) {
            errors.add("客户ID不能为空");
        }
        
        if (rentalRecord.getRentalStartDate() == null) {
            errors.add("租赁开始日期不能为空");
        }
        
        if (rentalRecord.getRentalPeriod() == null) {
            errors.add("租赁期间不能为空");
        }
        
        if (rentalRecord.getDailyRentalFee() == null) {
            errors.add("日租金不能为空");
        }
        
        if (rentalRecord.getTotalRentalFee() == null) {
            errors.add("总租金不能为空");
        }
    }
    
    /**
     * 验证数据格式
     */
    private void validateDataFormats(RentalRecord rentalRecord, List<String> errors) {
        try {
            if (StringUtils.hasText(rentalRecord.getRentalOrderNumber())) {
                rentalValidator.validateRentalOrderNumber(rentalRecord.getRentalOrderNumber());
            }
            
            if (rentalRecord.getCustomerId() != null) {
                rentalValidator.validateCustomerId(rentalRecord.getCustomerId());
            }
            
            if (rentalRecord.getRentalPeriod() != null) {
                rentalValidator.validateRentalPeriod(rentalRecord.getRentalPeriod());
            }
            
            if (rentalRecord.getDailyRentalFee() != null) {
                rentalValidator.validateRentalFee(rentalRecord.getDailyRentalFee());
            }
            
            if (rentalRecord.getTotalRentalFee() != null) {
                rentalValidator.validateRentalFee(rentalRecord.getTotalRentalFee());
            }
            
            if (rentalRecord.getRentalStatus() != null) {
                rentalValidator.validateRentalStatus(rentalRecord.getRentalStatus().getCode());
            }
            
            if (rentalRecord.getPaymentStatus() != null) {
                rentalValidator.validatePaymentStatus(rentalRecord.getPaymentStatus().getCode());
            }
            
        } catch (RentalException e) {
            errors.add(e.getMessage());
        }
    }
    
    /**
     * 验证业务逻辑
     */
    private void validateBusinessLogic(RentalRecord rentalRecord, List<String> errors) {
        // 验证日期逻辑
        if (rentalRecord.getRentalStartDate() != null && rentalRecord.getRentalEndDate() != null) {
            if (rentalRecord.getRentalStartDate().isAfter(rentalRecord.getRentalEndDate())) {
                errors.add("租赁开始日期不能晚于结束日期");
            }
        }
        
        // 验证费用计算逻辑
        if (rentalRecord.getDailyRentalFee() != null && 
            rentalRecord.getRentalPeriod() != null && 
            rentalRecord.getTotalRentalFee() != null) {
            
            BigDecimal expectedTotal = rentalRecord.getDailyRentalFee()
                .multiply(new BigDecimal(rentalRecord.getRentalPeriod()));
            
            if (rentalRecord.getTotalRentalFee().compareTo(expectedTotal) != 0) {
                errors.add("总租金计算不正确，应为: " + expectedTotal);
            }
        }
        
        // 验证押金逻辑
        if (rentalRecord.getDepositAmount() != null && 
            rentalRecord.getDepositAmount().compareTo(BigDecimal.ZERO) < 0) {
            errors.add("押金金额不能为负数");
        }
        
        // 验证实际支付金额
        if (rentalRecord.getActualPayment() != null && 
            rentalRecord.getActualPayment().compareTo(BigDecimal.ZERO) < 0) {
            errors.add("实际支付金额不能为负数");
        }
    }
    
    /**
     * 验证表单数据并抛出异常
     * 如果有验证错误，直接抛出RentalException
     * 
     * @param errors 验证错误列表
     * @param formType 表单类型
     */
    public void validateAndThrow(List<String> errors, String formType) {
        if (!errors.isEmpty()) {
            String errorMessage = formType + "表单验证失败: " + String.join("; ", errors);
            throw new RentalException(RentalException.ErrorCodes.DATA_VALIDATION_ERROR, 
                                    errorMessage, 
                                    "验证错误数量: " + errors.size());
        }
    }
}