package com.yxrobot.entity;

import com.yxrobot.enums.DeviceStatus;
import com.yxrobot.enums.MaintenanceStatus;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 租赁设备实体类
 * 对应数据库表 rental_devices
 * 
 * @author Kiro
 * @date 2025-01-28
 */
public class RentalDevice {
    
    /**
     * 设备ID，主键
     */
    private Long id;
    
    /**
     * 设备编号
     */
    @NotBlank(message = "设备编号不能为空")
    @Size(max = 50, message = "设备编号长度不能超过50字符")
    private String deviceId;
    
    /**
     * 设备型号
     */
    @NotBlank(message = "设备型号不能为空")
    @Size(max = 100, message = "设备型号长度不能超过100字符")
    private String deviceModel;
    
    /**
     * 设备名称
     */
    @NotBlank(message = "设备名称不能为空")
    @Size(max = 200, message = "设备名称长度不能超过200字符")
    private String deviceName;
    
    /**
     * 设备类别
     */
    @NotBlank(message = "设备类别不能为空")
    @Size(max = 100, message = "设备类别长度不能超过100字符")
    private String deviceCategory;
    
    /**
     * 序列号
     */
    @Size(max = 100, message = "序列号长度不能超过100字符")
    private String serialNumber;
    
    /**
     * 采购日期
     */
    private LocalDate purchaseDate;
    
    /**
     * 采购价格
     */
    @DecimalMin(value = "0", message = "采购价格不能为负数")
    @DecimalMax(value = "999999.99", message = "采购价格不能超过999999.99")
    @Digits(integer = 8, fraction = 2, message = "采购价格格式不正确")
    private BigDecimal purchasePrice;
    
    /**
     * 日租金价格
     */
    @NotNull(message = "日租金价格不能为空")
    @DecimalMin(value = "0.01", message = "日租金价格必须大于0")
    @DecimalMax(value = "9999.99", message = "日租金价格不能超过9999.99")
    @Digits(integer = 6, fraction = 2, message = "日租金价格格式不正确")
    private BigDecimal dailyRentalPrice;
    
    /**
     * 当前状态
     */
    private DeviceStatus currentStatus;
    
    /**
     * 设备位置
     */
    @Size(max = 200, message = "设备位置长度不能超过200字符")
    private String location;
    
    /**
     * 所在地区
     */
    @Size(max = 100, message = "所在地区长度不能超过100字符")
    private String region;
    
    /**
     * 性能评分（0-100）
     */
    @Min(value = 0, message = "性能评分不能小于0")
    @Max(value = 100, message = "性能评分不能大于100")
    private Integer performanceScore;
    
    /**
     * 信号强度（0-100）
     */
    @Min(value = 0, message = "信号强度不能小于0")
    @Max(value = 100, message = "信号强度不能大于100")
    private Integer signalStrength;
    
    /**
     * 维护状态
     */
    private MaintenanceStatus maintenanceStatus;
    
    /**
     * 最后维护日期
     */
    private LocalDate lastMaintenanceDate;
    
    /**
     * 下次维护日期
     */
    private LocalDate nextMaintenanceDate;
    
    /**
     * 累计租赁天数
     */
    @Min(value = 0, message = "累计租赁天数不能为负数")
    private Integer totalRentalDays;
    
    /**
     * 累计可用天数
     */
    @Min(value = 0, message = "累计可用天数不能为负数")
    private Integer totalAvailableDays;
    
    /**
     * 利用率
     */
    @DecimalMin(value = "0", message = "利用率不能为负数")
    @DecimalMax(value = "100", message = "利用率不能超过100")
    @Digits(integer = 3, fraction = 2, message = "利用率格式不正确")
    private BigDecimal utilizationRate;
    
    /**
     * 最后租赁日期
     */
    private LocalDate lastRentalDate;
    
    /**
     * 是否启用
     */
    private Boolean isActive;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 是否删除
     */
    private Boolean isDeleted;
    
    // 构造函数
    public RentalDevice() {
        this.currentStatus = DeviceStatus.IDLE;
        this.maintenanceStatus = MaintenanceStatus.NORMAL;
        this.performanceScore = 100;
        this.signalStrength = 100;
        this.totalRentalDays = 0;
        this.totalAvailableDays = 0;
        this.utilizationRate = BigDecimal.ZERO;
        this.isActive = true;
        this.isDeleted = false;
    }
    
    // Getter 和 Setter 方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    
    public String getDeviceModel() {
        return deviceModel;
    }
    
    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }
    
    public String getDeviceName() {
        return deviceName;
    }
    
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
    
    public String getDeviceCategory() {
        return deviceCategory;
    }
    
    public void setDeviceCategory(String deviceCategory) {
        this.deviceCategory = deviceCategory;
    }
    
    public String getSerialNumber() {
        return serialNumber;
    }
    
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }
    
    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
    
    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }
    
    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }
    
    public BigDecimal getDailyRentalPrice() {
        return dailyRentalPrice;
    }
    
    public void setDailyRentalPrice(BigDecimal dailyRentalPrice) {
        this.dailyRentalPrice = dailyRentalPrice;
    }
    
    public DeviceStatus getCurrentStatus() {
        return currentStatus;
    }
    
    public void setCurrentStatus(DeviceStatus currentStatus) {
        this.currentStatus = currentStatus;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getRegion() {
        return region;
    }
    
    public void setRegion(String region) {
        this.region = region;
    }
    
    public Integer getPerformanceScore() {
        return performanceScore;
    }
    
    public void setPerformanceScore(Integer performanceScore) {
        this.performanceScore = performanceScore;
    }
    
    public Integer getSignalStrength() {
        return signalStrength;
    }
    
    public void setSignalStrength(Integer signalStrength) {
        this.signalStrength = signalStrength;
    }
    
    public MaintenanceStatus getMaintenanceStatus() {
        return maintenanceStatus;
    }
    
    public void setMaintenanceStatus(MaintenanceStatus maintenanceStatus) {
        this.maintenanceStatus = maintenanceStatus;
    }
    
    public LocalDate getLastMaintenanceDate() {
        return lastMaintenanceDate;
    }
    
    public void setLastMaintenanceDate(LocalDate lastMaintenanceDate) {
        this.lastMaintenanceDate = lastMaintenanceDate;
    }
    
    public LocalDate getNextMaintenanceDate() {
        return nextMaintenanceDate;
    }
    
    public void setNextMaintenanceDate(LocalDate nextMaintenanceDate) {
        this.nextMaintenanceDate = nextMaintenanceDate;
    }
    
    public Integer getTotalRentalDays() {
        return totalRentalDays;
    }
    
    public void setTotalRentalDays(Integer totalRentalDays) {
        this.totalRentalDays = totalRentalDays;
    }
    
    public Integer getTotalAvailableDays() {
        return totalAvailableDays;
    }
    
    public void setTotalAvailableDays(Integer totalAvailableDays) {
        this.totalAvailableDays = totalAvailableDays;
    }
    
    public BigDecimal getUtilizationRate() {
        return utilizationRate;
    }
    
    public void setUtilizationRate(BigDecimal utilizationRate) {
        this.utilizationRate = utilizationRate;
    }
    
    public LocalDate getLastRentalDate() {
        return lastRentalDate;
    }
    
    public void setLastRentalDate(LocalDate lastRentalDate) {
        this.lastRentalDate = lastRentalDate;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
    
    public Boolean getIsDeleted() {
        return isDeleted;
    }
    
    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    
    @Override
    public String toString() {
        return "RentalDevice{" +
                "id=" + id +
                ", deviceId='" + deviceId + '\'' +
                ", deviceModel='" + deviceModel + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", currentStatus=" + currentStatus +
                ", utilizationRate=" + utilizationRate +
                ", region='" + region + '\'' +
                '}';
    }
}