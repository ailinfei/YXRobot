package com.yxrobot.entity;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 物流信息实体类
 * 对应shipping_info表
 * 记录订单的物流配送信息
 * 
 * 字段映射规范：
 * - 数据库字段：snake_case（如：tracking_number, shipped_at）
 * - Java实体类：camelCase（如：trackingNumber, shippedAt）
 * - 前端接口：camelCase（如：trackingNumber, shippedAt）
 */
public class ShippingInfo {
    
    private Long id;
    private Long orderId;               // 订单ID
    
    @Size(max = 100, message = "物流公司名称不能超过100个字符")
    private String company;             // 物流公司
    
    @Size(max = 100, message = "运单号不能超过100个字符")
    private String trackingNumber;      // 运单号
    
    private LocalDateTime shippedAt;    // 发货时间
    private LocalDateTime deliveredAt;  // 送达时间
    private String notes;               // 物流备注
    
    // 系统字段
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 构造函数
    public ShippingInfo() {}
    
    public ShippingInfo(Long orderId, String company, String trackingNumber) {
        this.orderId = orderId;
        this.company = company;
        this.trackingNumber = trackingNumber;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    
    public String getCompany() {
        return company;
    }
    
    public void setCompany(String company) {
        this.company = company;
    }
    
    public String getTrackingNumber() {
        return trackingNumber;
    }
    
    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }
    
    public LocalDateTime getShippedAt() {
        return shippedAt;
    }
    
    public void setShippedAt(LocalDateTime shippedAt) {
        this.shippedAt = shippedAt;
    }
    
    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }
    
    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "ShippingInfo{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", company='" + company + '\'' +
                ", trackingNumber='" + trackingNumber + '\'' +
                ", shippedAt=" + shippedAt +
                ", deliveredAt=" + deliveredAt +
                '}';
    }
}