package com.yxrobot.exception;

/**
 * 销售记录未找到异常
 * 当根据ID查找销售记录但记录不存在时抛出此异常
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-26
 */
public class SalesRecordNotFoundException extends RuntimeException {
    
    private final Long salesRecordId;
    
    public SalesRecordNotFoundException(Long salesRecordId) {
        super("销售记录不存在，ID: " + salesRecordId);
        this.salesRecordId = salesRecordId;
    }
    
    public SalesRecordNotFoundException(Long salesRecordId, String message) {
        super(message);
        this.salesRecordId = salesRecordId;
    }
    
    public SalesRecordNotFoundException(String message) {
        super(message);
        this.salesRecordId = null;
    }
    
    public SalesRecordNotFoundException(Long salesRecordId, String message, Throwable cause) {
        super(message, cause);
        this.salesRecordId = salesRecordId;
    }
    
    public Long getSalesRecordId() {
        return salesRecordId;
    }
}