package com.yxrobot.exception;

/**
 * 销售人员未找到异常
 * 当根据ID查找销售人员但销售人员不存在时抛出此异常
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-27
 */
public class SalesStaffNotFoundException extends RuntimeException {
    
    private final Long salesStaffId;
    
    public SalesStaffNotFoundException(Long salesStaffId) {
        super("销售人员不存在，ID: " + salesStaffId);
        this.salesStaffId = salesStaffId;
    }
    
    public SalesStaffNotFoundException(Long salesStaffId, String message) {
        super(message);
        this.salesStaffId = salesStaffId;
    }
    
    public SalesStaffNotFoundException(String message) {
        super(message);
        this.salesStaffId = null;
    }
    
    public SalesStaffNotFoundException(Long salesStaffId, String message, Throwable cause) {
        super(message, cause);
        this.salesStaffId = salesStaffId;
    }
    
    public Long getSalesStaffId() {
        return salesStaffId;
    }
}