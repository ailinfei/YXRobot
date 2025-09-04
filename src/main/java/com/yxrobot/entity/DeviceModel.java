package com.yxrobot.entity;

/**
 * 设备型号枚举
 * 对应managed_devices表的model字段
 * 
 * @author YXRobot Development Team
 * @since 2024
 */
public enum DeviceModel {
    YX_ROBOT_V1("YX-ROBOT-V1", "YX机器人V1版本"),
    YX_ROBOT_V2("YX-ROBOT-V2", "YX机器人V2版本"),
    YX_ROBOT_PRO("YX-ROBOT-PRO", "YX机器人专业版"),
    YX_ROBOT_LITE("YX-ROBOT-LITE", "YX机器人轻量版"),
    YX_SENSOR_V1("YX-SENSOR-V1", "YX传感器V1版本"),
    YX_SENSOR_V2("YX-SENSOR-V2", "YX传感器V2版本"),
    YX_CONTROLLER("YX-CONTROLLER", "YX控制器"),
    YX_EDU_2024("YX-EDU-2024", "YX教育版2024"),
    YX_HOME_2024("YX-HOME-2024", "YX家庭版2024"),
    YX_PRO_2024("YX-PRO-2024", "YX专业版2024"),
    UNKNOWN("UNKNOWN", "未知型号");
    
    private final String code;
    private final String description;
    
    DeviceModel(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static DeviceModel fromCode(String code) {
        for (DeviceModel model : DeviceModel.values()) {
            if (model.code.equals(code)) {
                return model;
            }
        }
        return UNKNOWN;
    }
}