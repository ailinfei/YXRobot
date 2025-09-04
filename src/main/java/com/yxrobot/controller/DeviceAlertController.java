package com.yxrobot.controller;

import com.yxrobot.dto.DeviceAlertDTO;
import com.yxrobot.entity.AlertLevel;
import com.yxrobot.service.DeviceAlertService;
import com.yxrobot.service.DeviceMonitoringService.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备告警控制器
 * 提供设备告警相关的RESTful API接口，适配前端告警功能
 * 
 * 主要接口：
 * - GET /api/admin/device/monitoring/alerts - 获取告警列表
 * - GET /api/admin/device/monitoring/alerts/unresolved - 获取未解决告警
 * - GET /api/admin/device/monitoring/alerts/recent - 获取最近告警
 * - POST /api/admin/device/monitoring/alerts/{id}/resolve - 解决告警
 * - POST /api/admin/device/monitoring/alerts/batch-resolve - 批量解决告警
 * 
 * @author YXRobot Development Team
 * @since 2024
 */
@RestController
@RequestMapping("/api/admin/device/monitoring/alerts")
@CrossOrigin(origins = "*")
public class DeviceAlertController {
    
    private static final Logger logger = LoggerFactory.getLogger(DeviceAlertController.class);
    
    @Autowired
    private DeviceAlertService deviceAlertService;
}