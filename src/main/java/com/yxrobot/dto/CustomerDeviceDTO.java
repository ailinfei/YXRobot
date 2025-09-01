package com.yxrobot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * 客户设备数据传输对象
 * 与前端CustomerDevice接口保持一致
 * 用于客户设备列表和详情显示
 */
public class CustomerDeviceDTO {
    
    private String id;                  // 设备ID
    
    @JsonProperty("serialNumber")
    private String serialNumber;        // 设备序列号
    
    @JsonProperty("model")
    private String model;               // 设备型号
    
    @JsonProperty("type")
    private String type;                // 设备类型：purchased, rental
    
    @JsonProperty("status")
    private String status;              // 设备状态：pending, active, offline, maintenance, retired
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("activatedAt")
    private String activatedAt;         // 激活时间
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("lastOnlineAt")
    private String lastOnlineAt;        // 最后在线时间
    
    @JsonProperty("firmwareVersion")
    private String firmwareVersion;     // 固件版本
    
    @JsonProperty("healthScore")
    private Integer healthScore;        // 健康评分
    
    @JsonProperty("usageStats")
    private DeviceUsageStatsDTO usageStats; // 使用统计
    
    @JsonProperty("notes")
    private String notes;               // 备注
    
    // 租赁相关信息
    @JsonProperty("rentalInfo")
    private RentalInfoDTO rentalInfo;   // 租赁信息
    
    // 购买相关信息
    @JsonProperty("purchaseInfo")
    private PurchaseInfoDTO purchaseInfo; // 购买信息
    
    // 内部类 - 设备使用统计
    public static class DeviceUsageStatsDTO {
        @JsonProperty("totalUsageHours")
        private Integer totalUsageHours;    // 总使用小时数
        
        @JsonProperty("dailyAverageUsage")
        private Double dailyAverageUsage;   // 日均使用时长
        
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonProperty("lastUsedAt")
        private String lastUsedAt;          // 最后使用时间
        
        @JsonProperty("coursesCompleted")
        private Integer coursesCompleted;   // 完成课程数
        
        @JsonProperty("charactersWritten")
        private Integer charactersWritten;  // 书写字符数
        
        // Getter和Setter
        public Integer getTotalUsageHours() { return totalUsageHours; }
        public void setTotalUsageHours(Integer totalUsageHours) { this.totalUsageHours = totalUsageHours; }
        public Double getDailyAverageUsage() { return dailyAverageUsage; }
        public void setDailyAverageUsage(Double dailyAverageUsage) { this.dailyAverageUsage = dailyAverageUsage; }
        public String getLastUsedAt() { return lastUsedAt; }
        public void setLastUsedAt(String lastUsedAt) { this.lastUsedAt = lastUsedAt; }
        public Integer getCoursesCompleted() { return coursesCompleted; }
        public void setCoursesCompleted(Integer coursesCompleted) { this.coursesCompleted = coursesCompleted; }
        public Integer getCharactersWritten() { return charactersWritten; }
        public void setCharactersWritten(Integer charactersWritten) { this.charactersWritten = charactersWritten; }
    }
    
    // 内部类 - 租赁信息
    public static class RentalInfoDTO {
        @JsonFormat(pattern = "yyyy-MM-dd")
        @JsonProperty("startDate")
        private String startDate;           // 租赁开始日期
        
        @JsonFormat(pattern = "yyyy-MM-dd")
        @JsonProperty("endDate")
        private String endDate;             // 租赁结束日期
        
        @JsonProperty("dailyRentalFee")
        private BigDecimal dailyRentalFee;  // 日租金
        
        @JsonProperty("totalRentalFee")
        private BigDecimal totalRentalFee;  // 总租金
        
        // Getter和Setter
        public String getStartDate() { return startDate; }
        public void setStartDate(String startDate) { this.startDate = startDate; }
        public String getEndDate() { return endDate; }
        public void setEndDate(String endDate) { this.endDate = endDate; }
        public BigDecimal getDailyRentalFee() { return dailyRentalFee; }
        public void setDailyRentalFee(BigDecimal dailyRentalFee) { this.dailyRentalFee = dailyRentalFee; }
        public BigDecimal getTotalRentalFee() { return totalRentalFee; }
        public void setTotalRentalFee(BigDecimal totalRentalFee) { this.totalRentalFee = totalRentalFee; }
    }
    
    // 内部类 - 购买信息
    public static class PurchaseInfoDTO {
        @JsonFormat(pattern = "yyyy-MM-dd")
        @JsonProperty("purchaseDate")
        private String purchaseDate;        // 购买日期
        
        @JsonProperty("purchasePrice")
        private BigDecimal purchasePrice;   // 购买价格
        
        @JsonFormat(pattern = "yyyy-MM-dd")
        @JsonProperty("warrantyEndDate")
        private String warrantyEndDate;     // 保修结束日期
        
        // Getter和Setter
        public String getPurchaseDate() { return purchaseDate; }
        public void setPurchaseDate(String purchaseDate) { this.purchaseDate = purchaseDate; }
        public BigDecimal getPurchasePrice() { return purchasePrice; }
        public void setPurchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; }
        public String getWarrantyEndDate() { return warrantyEndDate; }
        public void setWarrantyEndDate(String warrantyEndDate) { this.warrantyEndDate = warrantyEndDate; }
    }
    
    // 构造函数
    public CustomerDeviceDTO() {}
    
    // Getter和Setter方法
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getSerialNumber() {
        return serialNumber;
    }
    
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getActivatedAt() {
        return activatedAt;
    }
    
    public void setActivatedAt(String activatedAt) {
        this.activatedAt = activatedAt;
    }
    
    public String getLastOnlineAt() {
        return lastOnlineAt;
    }
    
    public void setLastOnlineAt(String lastOnlineAt) {
        this.lastOnlineAt = lastOnlineAt;
    }
    
    public String getFirmwareVersion() {
        return firmwareVersion;
    }
    
    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }
    
    public Integer getHealthScore() {
        return healthScore;
    }
    
    public void setHealthScore(Integer healthScore) {
        this.healthScore = healthScore;
    }
    
    public DeviceUsageStatsDTO getUsageStats() {
        return usageStats;
    }
    
    public void setUsageStats(DeviceUsageStatsDTO usageStats) {
        this.usageStats = usageStats;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public RentalInfoDTO getRentalInfo() {
        return rentalInfo;
    }
    
    public void setRentalInfo(RentalInfoDTO rentalInfo) {
        this.rentalInfo = rentalInfo;
    }
    
    public PurchaseInfoDTO getPurchaseInfo() {
        return purchaseInfo;
    }
    
    public void setPurchaseInfo(PurchaseInfoDTO purchaseInfo) {
        this.purchaseInfo = purchaseInfo;
    }
}