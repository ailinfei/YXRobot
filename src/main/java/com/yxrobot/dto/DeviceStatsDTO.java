package com.yxrobot.dto;

/**
 * 设备统计DTO类
 * 作为ManagedDeviceStatsDTO的别名，保持向后兼容性
 * 适配前端统计需求
 */
public class DeviceStatsDTO extends ManagedDeviceStatsDTO {
    
    // 构造函数
    public DeviceStatsDTO() {
        super();
    }
    
    public DeviceStatsDTO(Integer total, Integer online, Integer offline, Integer error, Integer maintenance) {
        super(total, online, offline, error, maintenance);
    }
}