package com.yxrobot.validation;

import com.yxrobot.exception.RentalException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

/**
 * 租赁模块参数验证器
 * 验证API请求参数和数据格式
 * 
 * @author Kiro
 * @date 2025-01-28
 */
@Component
public class RentalValidator {
    
    // 支持的时间周期
    private static final List<String> VALID_PERIODS = Arrays.asList("daily", "weekly", "monthly", "quarterly");
    
    // 支持的分布类型
    private static final List<String> VALID_DISTRIBUTION_TYPES = Arrays.asList("region", "device-model", "utilization-ranking");
    
    // 支持的设备状态
    private static final List<String> VALID_DEVICE_STATUSES = Arrays.asList("active", "idle", "maintenance", "retired");
    
    // 支持的租赁状态
    private static final List<String> VALID_RENTAL_STATUSES = Arrays.asList("pending", "active", "completed", "cancelled", "overdue");
    
    // 支持的付款状态
    private static final List<String> VALID_PAYMENT_STATUSES = Arrays.asList("unpaid", "partial", "paid", "refunded");
    
    /**
     * 验证分页参数
     */
    public void validatePaginationParams(Integer page, Integer pageSize) {
        if (page != null && page <= 0) {
            throw RentalException.dataValidationError("page", String.valueOf(page));
        }
        
        if (pageSize != null && (pageSize <= 0 || pageSize > 100)) {
            throw RentalException.dataValidationError("pageSize", String.valueOf(pageSize));
        }
    }
    
    /**
     * 验证日期参数
     */
    public LocalDate validateDateParam(String dateStr, String paramName) {
        if (!StringUtils.hasText(dateStr)) {
            return null;
        }
        
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw RentalException.dataValidationError(paramName, dateStr);
        }
    }
    
    /**
     * 验证日期范围
     */
    public void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null) {
            if (startDate.isAfter(endDate)) {
                throw new RentalException(RentalException.ErrorCodes.DATA_VALIDATION_ERROR, 
                                        "开始日期不能晚于结束日期", 
                                        "开始日期: " + startDate + ", 结束日期: " + endDate);
            }
            
            // 检查日期范围是否过大（超过2年）
            if (startDate.plusYears(2).isBefore(endDate)) {
                throw new RentalException(RentalException.ErrorCodes.DATA_VALIDATION_ERROR, 
                                        "日期范围不能超过2年", 
                                        "开始日期: " + startDate + ", 结束日期: " + endDate);
            }
        }
    }
    
    /**
     * 验证时间周期参数
     */
    public void validatePeriodParam(String period) {
        if (StringUtils.hasText(period) && !VALID_PERIODS.contains(period.toLowerCase())) {
            throw RentalException.dataValidationError("period", period);
        }
    }
    
    /**
     * 验证分布类型参数
     */
    public void validateDistributionType(String type) {
        if (StringUtils.hasText(type) && !VALID_DISTRIBUTION_TYPES.contains(type.toLowerCase())) {
            throw RentalException.dataValidationError("type", type);
        }
    }
    
    /**
     * 验证设备状态参数
     */
    public void validateDeviceStatus(String status) {
        if (StringUtils.hasText(status) && !VALID_DEVICE_STATUSES.contains(status.toLowerCase())) {
            throw RentalException.dataValidationError("currentStatus", status);
        }
    }
    
    /**
     * 验证租赁状态参数
     */
    public void validateRentalStatus(String status) {
        if (StringUtils.hasText(status) && !VALID_RENTAL_STATUSES.contains(status.toLowerCase())) {
            throw RentalException.dataValidationError("rentalStatus", status);
        }
    }
    
    /**
     * 验证付款状态参数
     */
    public void validatePaymentStatus(String status) {
        if (StringUtils.hasText(status) && !VALID_PAYMENT_STATUSES.contains(status.toLowerCase())) {
            throw RentalException.dataValidationError("paymentStatus", status);
        }
    }
    
    /**
     * 验证限制数量参数
     */
    public void validateLimitParam(Integer limit) {
        if (limit != null && (limit <= 0 || limit > 1000)) {
            throw RentalException.dataValidationError("limit", String.valueOf(limit));
        }
    }
    
    /**
     * 验证设备ID参数
     */
    public void validateDeviceId(String deviceId) {
        if (!StringUtils.hasText(deviceId)) {
            throw RentalException.dataValidationError("deviceId", "设备ID不能为空");
        }
        
        if (deviceId.length() > 50) {
            throw RentalException.dataValidationError("deviceId", "设备ID长度不能超过50个字符");
        }
    }
    
    /**
     * 验证客户ID参数
     */
    public void validateCustomerId(Long customerId) {
        if (customerId == null || customerId <= 0) {
            throw RentalException.dataValidationError("customerId", String.valueOf(customerId));
        }
    }
    
    /**
     * 验证租赁期间参数
     */
    public void validateRentalPeriod(Integer period) {
        if (period == null || period <= 0 || period > 365) {
            throw RentalException.invalidRentalPeriod(period != null ? period : 0);
        }
    }
    
    /**
     * 验证租金金额参数
     */
    public void validateRentalFee(BigDecimal fee) {
        if (fee == null || fee.compareTo(BigDecimal.ZERO) <= 0) {
            throw RentalException.dataValidationError("rentalFee", fee != null ? fee.toString() : "null");
        }
        
        if (fee.compareTo(new BigDecimal("999999.99")) > 0) {
            throw RentalException.dataValidationError("rentalFee", "租金金额不能超过999999.99");
        }
    }
    
    /**
     * 验证搜索关键词参数
     */
    public void validateSearchKeyword(String keyword) {
        if (StringUtils.hasText(keyword)) {
            if (keyword.length() > 100) {
                throw RentalException.dataValidationError("keyword", "搜索关键词长度不能超过100个字符");
            }
            
            // 检查是否包含特殊字符（防止SQL注入）
            if (keyword.matches(".*[<>\"'%;()&+].*")) {
                throw RentalException.dataValidationError("keyword", "搜索关键词包含非法字符");
            }
        }
    }
    
    /**
     * 验证排序参数
     */
    public void validateSortParam(String sortBy, String sortOrder) {
        if (StringUtils.hasText(sortBy)) {
            List<String> validSortFields = Arrays.asList(
                "deviceId", "deviceModel", "utilizationRate", "totalRentalDays", 
                "currentStatus", "lastRentalDate", "createdAt", "updatedAt"
            );
            
            if (!validSortFields.contains(sortBy)) {
                throw RentalException.dataValidationError("sortBy", sortBy);
            }
        }
        
        if (StringUtils.hasText(sortOrder)) {
            if (!Arrays.asList("asc", "desc").contains(sortOrder.toLowerCase())) {
                throw RentalException.dataValidationError("sortOrder", sortOrder);
            }
        }
    }
    
    /**
     * 验证批量操作参数
     */
    public void validateBatchOperationParams(List<String> ids, String operation) {
        if (ids == null || ids.isEmpty()) {
            throw RentalException.dataValidationError("ids", "批量操作ID列表不能为空");
        }
        
        if (ids.size() > 100) {
            throw RentalException.dataValidationError("ids", "批量操作数量不能超过100个");
        }
        
        if (!StringUtils.hasText(operation)) {
            throw RentalException.dataValidationError("operation", "操作类型不能为空");
        }
        
        List<String> validOperations = Arrays.asList("updateStatus", "maintenance", "delete");
        if (!validOperations.contains(operation)) {
            throw RentalException.dataValidationError("operation", operation);
        }
    }
    
    /**
     * 验证统计查询参数
     */
    public void validateStatsQueryParams(LocalDate startDate, LocalDate endDate, String groupBy) {
        validateDateRange(startDate, endDate);
        
        if (StringUtils.hasText(groupBy)) {
            List<String> validGroupBy = Arrays.asList("day", "week", "month", "quarter", "year");
            if (!validGroupBy.contains(groupBy.toLowerCase())) {
                throw RentalException.dataValidationError("groupBy", groupBy);
            }
        }
    }
    
    /**
     * 验证客户名称
     */
    public void validateCustomerName(String customerName) {
        if (!StringUtils.hasText(customerName)) {
            throw RentalException.requiredFieldMissing("customerName");
        }
        
        if (customerName.length() > 200) {
            throw RentalException.fieldLengthExceeded("customerName", 200, customerName.length());
        }
        
        // 检查是否包含特殊字符
        if (customerName.matches(".*[<>\"'%;()&+].*")) {
            throw RentalException.dataValidationError("customerName", "客户名称包含非法字符");
        }
    }
    
    /**
     * 验证客户类型
     */
    public void validateCustomerType(String customerType) {
        if (!StringUtils.hasText(customerType)) {
            throw RentalException.requiredFieldMissing("customerType");
        }
        
        List<String> validTypes = Arrays.asList("individual", "enterprise", "institution");
        if (!validTypes.contains(customerType.toLowerCase())) {
            throw RentalException.invalidCustomerType(customerType);
        }
    }
    
    /**
     * 验证联系电话
     */
    public void validatePhone(String phone) {
        if (StringUtils.hasText(phone)) {
            // 验证手机号格式：11位数字，以1开头
            if (!phone.matches("^1[3-9]\\d{9}$") && !phone.matches("^0\\d{2,3}-?\\d{7,8}$")) {
                throw RentalException.invalidPhoneFormat(phone);
            }
        }
    }
    
    /**
     * 验证邮箱地址
     */
    public void validateEmail(String email) {
        if (StringUtils.hasText(email)) {
            String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
            if (!email.matches(emailRegex)) {
                throw RentalException.invalidEmailFormat(email);
            }
            
            if (email.length() > 100) {
                throw RentalException.fieldLengthExceeded("email", 100, email.length());
            }
        }
    }
    
    /**
     * 验证地址信息
     */
    public void validateAddress(String address) {
        if (StringUtils.hasText(address) && address.length() > 500) {
            throw RentalException.fieldLengthExceeded("address", 500, address.length());
        }
    }
    
    /**
     * 验证地区信息
     */
    public void validateRegion(String region) {
        if (StringUtils.hasText(region) && region.length() > 100) {
            throw RentalException.fieldLengthExceeded("region", 100, region.length());
        }
    }
    
    /**
     * 验证行业信息
     */
    public void validateIndustry(String industry) {
        if (StringUtils.hasText(industry) && industry.length() > 100) {
            throw RentalException.fieldLengthExceeded("industry", 100, industry.length());
        }
    }
    
    /**
     * 验证信用等级
     */
    public void validateCreditLevel(String creditLevel) {
        if (StringUtils.hasText(creditLevel)) {
            List<String> validLevels = Arrays.asList("A", "B", "C", "D");
            if (!validLevels.contains(creditLevel.toUpperCase())) {
                throw RentalException.dataValidationError("creditLevel", creditLevel);
            }
        }
    }
    
    /**
     * 验证租赁订单号
     */
    public void validateRentalOrderNumber(String orderNumber) {
        if (!StringUtils.hasText(orderNumber)) {
            throw RentalException.requiredFieldMissing("rentalOrderNumber");
        }
        
        if (orderNumber.length() > 50) {
            throw RentalException.fieldLengthExceeded("rentalOrderNumber", 50, orderNumber.length());
        }
        
        // 验证订单号格式：字母数字组合
        if (!orderNumber.matches("^[A-Z0-9]{8,20}$")) {
            throw RentalException.dataValidationError("rentalOrderNumber", "订单号格式不正确，应为8-20位字母数字组合");
        }
    }
    
    /**
     * 验证设备名称
     */
    public void validateDeviceName(String deviceName) {
        if (!StringUtils.hasText(deviceName)) {
            throw RentalException.requiredFieldMissing("deviceName");
        }
        
        if (deviceName.length() > 200) {
            throw RentalException.fieldLengthExceeded("deviceName", 200, deviceName.length());
        }
    }
    
    /**
     * 验证设备型号
     */
    public void validateDeviceModel(String deviceModel) {
        if (!StringUtils.hasText(deviceModel)) {
            throw RentalException.requiredFieldMissing("deviceModel");
        }
        
        if (deviceModel.length() > 100) {
            throw RentalException.fieldLengthExceeded("deviceModel", 100, deviceModel.length());
        }
        
        // 验证设备型号格式
        List<String> validModels = Arrays.asList("YX-Robot-Pro", "YX-Robot-Standard", "YX-Robot-Lite", "YX-Robot-Mini");
        if (!validModels.contains(deviceModel)) {
            throw RentalException.invalidDeviceModel(deviceModel);
        }
    }
    
    /**
     * 验证维护状态
     */
    public void validateMaintenanceStatus(String maintenanceStatus) {
        if (StringUtils.hasText(maintenanceStatus)) {
            List<String> validStatuses = Arrays.asList("normal", "warning", "urgent");
            if (!validStatuses.contains(maintenanceStatus.toLowerCase())) {
                throw RentalException.invalidMaintenanceStatus(maintenanceStatus);
            }
        }
    }
    
    /**
     * 验证性能评分
     */
    public void validatePerformanceScore(Integer score) {
        if (score != null && (score < 0 || score > 100)) {
            throw RentalException.dataValidationError("performanceScore", "性能评分必须在0-100之间");
        }
    }
    
    /**
     * 验证信号强度
     */
    public void validateSignalStrength(Integer strength) {
        if (strength != null && (strength < 0 || strength > 100)) {
            throw RentalException.dataValidationError("signalStrength", "信号强度必须在0-100之间");
        }
    }
    
    /**
     * 验证利用率
     */
    public void validateUtilizationRate(BigDecimal rate) {
        if (rate != null && (rate.compareTo(BigDecimal.ZERO) < 0 || rate.compareTo(new BigDecimal("100")) > 0)) {
            throw RentalException.dataValidationError("utilizationRate", "利用率必须在0-100之间");
        }
    }
    
    /**
     * 验证租赁天数
     */
    public void validateRentalDays(Integer days) {
        if (days != null && days < 0) {
            throw RentalException.dataValidationError("rentalDays", "租赁天数不能为负数");
        }
    }
    
    /**
     * 验证完整的租赁记录数据
     */
    public void validateRentalRecordData(String orderNumber, String deviceId, Long customerId, 
                                       LocalDate startDate, LocalDate endDate, Integer period, 
                                       BigDecimal dailyFee, BigDecimal totalFee) {
        validateRentalOrderNumber(orderNumber);
        validateDeviceId(deviceId);
        validateCustomerId(customerId);
        
        if (startDate == null) {
            throw RentalException.requiredFieldMissing("rentalStartDate");
        }
        
        if (endDate != null && startDate.isAfter(endDate)) {
            throw RentalException.invalidDateRange(startDate.toString(), endDate.toString());
        }
        
        validateRentalPeriod(period);
        validateRentalFee(dailyFee);
        validateRentalFee(totalFee);
        
        // 验证总费用计算是否正确
        if (dailyFee != null && period != null && totalFee != null) {
            BigDecimal expectedTotal = dailyFee.multiply(new BigDecimal(period));
            if (totalFee.compareTo(expectedTotal) != 0) {
                throw RentalException.dataValidationError("totalRentalFee", "总费用计算不正确");
            }
        }
    }
    
    /**
     * 验证完整的客户数据
     */
    public void validateCustomerData(String customerName, String customerType, String phone, 
                                   String email, String address, String region, String industry) {
        validateCustomerName(customerName);
        validateCustomerType(customerType);
        validatePhone(phone);
        validateEmail(email);
        validateAddress(address);
        validateRegion(region);
        validateIndustry(industry);
    }
    
    /**
     * 验证完整的设备数据
     */
    public void validateDeviceData(String deviceId, String deviceModel, String deviceName, 
                                 BigDecimal dailyPrice, Integer performanceScore, Integer signalStrength) {
        validateDeviceId(deviceId);
        validateDeviceModel(deviceModel);
        validateDeviceName(deviceName);
        validateRentalFee(dailyPrice);
        validatePerformanceScore(performanceScore);
        validateSignalStrength(signalStrength);
    }
}