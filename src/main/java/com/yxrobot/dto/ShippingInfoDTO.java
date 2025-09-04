package com.yxrobot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 物流信息数据传输对象
 * 适配前端TypeScript接口，确保字段名称完全匹配
 * 
 * 字段映射规范：
 * - 数据库字段：snake_case（如：tracking_number, shipped_at）
 * - Java DTO：camelCase（如：trackingNumber, shippedAt）
 * - 前端接口：camelCase（如：trackingNumber, shippedAt）
 */
public class ShippingInfoDTO {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("orderId")
    private Long orderId;               // 订单ID
    
    @JsonProperty("company")
    @Size(max = 100, message = "物流公司名称不能超过100个字符")
    private String company;             // 物流公司
    
    @JsonProperty("trackingNumber")
    @Size(max = 100, message = "运单号不能超过100个字符")
    private String trackingNumber;      // 运单号
    
    @JsonProperty("shippedAt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime shippedAt;    // 发货时间
    
    @JsonProperty("deliveredAt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deliveredAt;  // 送达时间
    
    @JsonProperty("notes")
    private String notes;               // 物流备注
    
    // 系统字段
    @JsonProperty("createdAt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonProperty("updatedAt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // 构造函数
    public ShippingInfoDTO() {}
    
    public ShippingInfoDTO(Long orderId, String company, String trackingNumber) {
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
}