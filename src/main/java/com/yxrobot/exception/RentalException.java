package com.yxrobot.exception;

/**
 * 租赁业务异常类
 * 处理租赁数据分析模块的业务异常
 * 
 * @author Kiro
 * @date 2025-01-28
 */
public class RentalException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 错误代码
     */
    private String errorCode;
    
    /**
     * 错误详细信息
     */
    private String errorDetail;
    
    public RentalException(String message) {
        super(message);
    }
    
    public RentalException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public RentalException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public RentalException(String errorCode, String message, String errorDetail) {
        super(message);
        this.errorCode = errorCode;
        this.errorDetail = errorDetail;
    }
    
    public RentalException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    public String getErrorDetail() {
        return errorDetail;
    }
    
    public void setErrorDetail(String errorDetail) {
        this.errorDetail = errorDetail;
    }
    
    /**
     * 租赁业务错误代码常量
     */
    public static class ErrorCodes {
        public static final String RENTAL_RECORD_NOT_FOUND = "RENTAL_001";
        public static final String DEVICE_NOT_FOUND = "RENTAL_002";
        public static final String CUSTOMER_NOT_FOUND = "RENTAL_003";
        public static final String INVALID_RENTAL_PERIOD = "RENTAL_004";
        public static final String INVALID_RENTAL_STATUS = "RENTAL_005";
        public static final String DEVICE_NOT_AVAILABLE = "RENTAL_006";
        public static final String INVALID_PAYMENT_STATUS = "RENTAL_007";
        public static final String RENTAL_CONFLICT = "RENTAL_008";
        public static final String DATA_VALIDATION_ERROR = "RENTAL_009";
        public static final String PERMISSION_DENIED = "RENTAL_010";
        
        // 新增错误代码
        public static final String INVALID_DEVICE_MODEL = "RENTAL_011";
        public static final String INVALID_CUSTOMER_TYPE = "RENTAL_012";
        public static final String INVALID_DATE_FORMAT = "RENTAL_013";
        public static final String INVALID_DATE_RANGE = "RENTAL_014";
        public static final String INVALID_AMOUNT = "RENTAL_015";
        public static final String DUPLICATE_RENTAL_ORDER = "RENTAL_016";
        public static final String RENTAL_ALREADY_COMPLETED = "RENTAL_017";
        public static final String RENTAL_ALREADY_CANCELLED = "RENTAL_018";
        public static final String INSUFFICIENT_DEVICE_INVENTORY = "RENTAL_019";
        public static final String INVALID_MAINTENANCE_STATUS = "RENTAL_020";
        public static final String BATCH_OPERATION_FAILED = "RENTAL_021";
        public static final String REQUIRED_FIELD_MISSING = "RENTAL_022";
        public static final String FIELD_LENGTH_EXCEEDED = "RENTAL_023";
        public static final String INVALID_PHONE_FORMAT = "RENTAL_024";
        public static final String INVALID_EMAIL_FORMAT = "RENTAL_025";
        
        public static final String SYSTEM_ERROR = "RENTAL_999";
    }
    
    /**
     * 创建租赁记录未找到异常
     */
    public static RentalException rentalRecordNotFound(String rentalId) {
        return new RentalException(ErrorCodes.RENTAL_RECORD_NOT_FOUND, 
                                 "租赁记录未找到", 
                                 "租赁记录ID: " + rentalId);
    }
    
    /**
     * 创建设备未找到异常
     */
    public static RentalException deviceNotFound(String deviceId) {
        return new RentalException(ErrorCodes.DEVICE_NOT_FOUND, 
                                 "设备未找到", 
                                 "设备ID: " + deviceId);
    }
    
    /**
     * 创建客户未找到异常
     */
    public static RentalException customerNotFound(String customerId) {
        return new RentalException(ErrorCodes.CUSTOMER_NOT_FOUND, 
                                 "客户未找到", 
                                 "客户ID: " + customerId);
    }
    
    /**
     * 创建无效租赁期间异常
     */
    public static RentalException invalidRentalPeriod(int period) {
        return new RentalException(ErrorCodes.INVALID_RENTAL_PERIOD, 
                                 "无效的租赁期间", 
                                 "租赁期间: " + period + " 天");
    }
    
    /**
     * 创建设备不可用异常
     */
    public static RentalException deviceNotAvailable(String deviceId, String status) {
        return new RentalException(ErrorCodes.DEVICE_NOT_AVAILABLE, 
                                 "设备当前不可用", 
                                 "设备ID: " + deviceId + ", 当前状态: " + status);
    }
    
    /**
     * 创建数据验证异常
     */
    public static RentalException dataValidationError(String field, String value) {
        return new RentalException(ErrorCodes.DATA_VALIDATION_ERROR, 
                                 "数据验证失败", 
                                 "字段: " + field + ", 值: " + value);
    }
    
    /**
     * 创建系统错误异常
     */
    public static RentalException systemError(String message, Throwable cause) {
        return new RentalException(ErrorCodes.SYSTEM_ERROR, 
                                 "系统内部错误: " + message, 
                                 cause);
    }
    
    /**
     * 创建无效设备型号异常
     */
    public static RentalException invalidDeviceModel(String deviceModel) {
        return new RentalException(ErrorCodes.INVALID_DEVICE_MODEL, 
                                 "无效的设备型号", 
                                 "设备型号: " + deviceModel);
    }
    
    /**
     * 创建无效客户类型异常
     */
    public static RentalException invalidCustomerType(String customerType) {
        return new RentalException(ErrorCodes.INVALID_CUSTOMER_TYPE, 
                                 "无效的客户类型", 
                                 "客户类型: " + customerType);
    }
    
    /**
     * 创建无效日期格式异常
     */
    public static RentalException invalidDateFormat(String dateStr) {
        return new RentalException(ErrorCodes.INVALID_DATE_FORMAT, 
                                 "日期格式不正确，请使用YYYY-MM-DD格式", 
                                 "输入的日期: " + dateStr);
    }
    
    /**
     * 创建无效日期范围异常
     */
    public static RentalException invalidDateRange(String startDate, String endDate) {
        return new RentalException(ErrorCodes.INVALID_DATE_RANGE, 
                                 "日期范围不正确", 
                                 "开始日期: " + startDate + ", 结束日期: " + endDate);
    }
    
    /**
     * 创建无效金额异常
     */
    public static RentalException invalidAmount(String field, String amount) {
        return new RentalException(ErrorCodes.INVALID_AMOUNT, 
                                 "金额格式不正确", 
                                 "字段: " + field + ", 金额: " + amount);
    }
    
    /**
     * 创建重复租赁订单异常
     */
    public static RentalException duplicateRentalOrder(String orderNumber) {
        return new RentalException(ErrorCodes.DUPLICATE_RENTAL_ORDER, 
                                 "租赁订单号已存在", 
                                 "订单号: " + orderNumber);
    }
    
    /**
     * 创建租赁已完成异常
     */
    public static RentalException rentalAlreadyCompleted(String rentalId) {
        return new RentalException(ErrorCodes.RENTAL_ALREADY_COMPLETED, 
                                 "租赁记录已完成，无法修改", 
                                 "租赁记录ID: " + rentalId);
    }
    
    /**
     * 创建租赁已取消异常
     */
    public static RentalException rentalAlreadyCancelled(String rentalId) {
        return new RentalException(ErrorCodes.RENTAL_ALREADY_CANCELLED, 
                                 "租赁记录已取消，无法修改", 
                                 "租赁记录ID: " + rentalId);
    }
    
    /**
     * 创建设备库存不足异常
     */
    public static RentalException insufficientDeviceInventory(String deviceModel) {
        return new RentalException(ErrorCodes.INSUFFICIENT_DEVICE_INVENTORY, 
                                 "设备库存不足", 
                                 "设备型号: " + deviceModel);
    }
    
    /**
     * 创建无效维护状态异常
     */
    public static RentalException invalidMaintenanceStatus(String status) {
        return new RentalException(ErrorCodes.INVALID_MAINTENANCE_STATUS, 
                                 "无效的维护状态", 
                                 "维护状态: " + status);
    }
    
    /**
     * 创建批量操作失败异常
     */
    public static RentalException batchOperationFailed(String operation, int successCount, int failCount) {
        return new RentalException(ErrorCodes.BATCH_OPERATION_FAILED, 
                                 "批量操作部分失败", 
                                 "操作: " + operation + ", 成功: " + successCount + ", 失败: " + failCount);
    }
    
    /**
     * 创建必填字段缺失异常
     */
    public static RentalException requiredFieldMissing(String fieldName) {
        return new RentalException(ErrorCodes.REQUIRED_FIELD_MISSING, 
                                 "必填字段不能为空", 
                                 "字段名: " + fieldName);
    }
    
    /**
     * 创建字段长度超限异常
     */
    public static RentalException fieldLengthExceeded(String fieldName, int maxLength, int actualLength) {
        return new RentalException(ErrorCodes.FIELD_LENGTH_EXCEEDED, 
                                 "字段长度超出限制", 
                                 "字段: " + fieldName + ", 最大长度: " + maxLength + ", 实际长度: " + actualLength);
    }
    
    /**
     * 创建无效手机号格式异常
     */
    public static RentalException invalidPhoneFormat(String phone) {
        return new RentalException(ErrorCodes.INVALID_PHONE_FORMAT, 
                                 "手机号格式不正确", 
                                 "手机号: " + phone);
    }
    
    /**
     * 创建无效邮箱格式异常
     */
    public static RentalException invalidEmailFormat(String email) {
        return new RentalException(ErrorCodes.INVALID_EMAIL_FORMAT, 
                                 "邮箱格式不正确", 
                                 "邮箱: " + email);
    }
}