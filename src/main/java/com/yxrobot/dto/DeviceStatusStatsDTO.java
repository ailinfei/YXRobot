package com.yxrobot.dto;

/**
 * 设备状态统计数据传输对象
 * 适配前端右侧面板的设备状态分布功能
 * 
 * @author Kiro
 * @date 2025-01-28
 */
public class DeviceStatusStatsDTO {
    
    /**
     * 运行中设备数
     * 对应前端字段: active
     */
    private Integer active;
    
    /**
     * 空闲设备数
     * 对应前端字段: idle
     */
    private Integer idle;
    
    /**
     * 维护中设备数
     * 对应前端字段: maintenance
     */
    private Integer maintenance;
    
    // 构造函数
    public DeviceStatusStatsDTO() {
        this.active = 0;
        this.idle = 0;
        this.maintenance = 0;
    }
    
    public DeviceStatusStatsDTO(Integer active, Integer idle, Integer maintenance) {
        this.active = active;
        this.idle = idle;
        this.maintenance = maintenance;
    }
    
    // Getter 和 Setter 方法
    public Integer getActive() {
        return active;
    }
    
    public void setActive(Integer active) {
        this.active = active;
    }
    
    public Integer getIdle() {
        return idle;
    }
    
    public void setIdle(Integer idle) {
        this.idle = idle;
    }
    
    public Integer getMaintenance() {
        return maintenance;
    }
    
    public void setMaintenance(Integer maintenance) {
        this.maintenance = maintenance;
    }
    
    @Override
    public String toString() {
        return "DeviceStatusStatsDTO{" +
                "active=" + active +
                ", idle=" + idle +
                ", maintenance=" + maintenance +
                '}';
    }
}