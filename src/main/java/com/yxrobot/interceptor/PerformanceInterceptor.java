package com.yxrobot.interceptor;

import com.yxrobot.service.ManagedDevicePerformanceMonitorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 性能监控拦截器
 * 自动监控API响应时间和调用统计
 */
@Component
public class PerformanceInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceInterceptor.class);
    
    @Autowired
    private ManagedDevicePerformanceMonitorService performanceMonitorService;
    
    private static final String START_TIME_ATTRIBUTE = "startTime";
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 记录请求开始时间
        long startTime = System.currentTimeMillis();
        request.setAttribute(START_TIME_ATTRIBUTE, startTime);
        
        // 记录请求信息
        String requestUri = request.getRequestURI();
        String method = request.getMethod();
        logger.debug("API请求开始 - {} {}", method, requestUri);
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 计算响应时间
        Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        if (startTime != null) {
            long responseTime = System.currentTimeMillis() - startTime;
            
            // 获取API信息
            String requestUri = request.getRequestURI();
            String method = request.getMethod();
            String apiName = method + " " + requestUri;
            
            // 只监控设备管理相关的API
            if (requestUri.contains("/api/admin/devices")) {
                // 记录API性能
                performanceMonitorService.recordApiPerformance(apiName, responseTime);
                
                // 记录功能使用统计
                recordFunctionUsage(requestUri, method);
                
                logger.debug("API请求完成 - {} {}, 响应时间: {}ms, 状态码: {}", 
                    method, requestUri, responseTime, response.getStatus());
            }
        }
    }
    
    /**
     * 记录功能使用统计
     * 
     * @param requestUri 请求URI
     * @param method HTTP方法
     */
    private void recordFunctionUsage(String requestUri, String method) {
        try {
            String functionName = extractFunctionName(requestUri, method);
            if (functionName != null) {
                performanceMonitorService.recordFunctionUsage(functionName);
            }
        } catch (Exception e) {
            logger.warn("记录功能使用统计失败: {}", e.getMessage());
        }
    }
    
    /**
     * 从请求URI提取功能名称
     * 
     * @param requestUri 请求URI
     * @param method HTTP方法
     * @return 功能名称
     */
    private String extractFunctionName(String requestUri, String method) {
        if (requestUri.contains("/api/admin/devices")) {
            // 设备列表查询
            if ("GET".equals(method) && requestUri.equals("/api/admin/devices")) {
                return "device_list_query";
            }
            // 设备详情查询
            else if ("GET".equals(method) && requestUri.matches("/api/admin/devices/\\d+")) {
                return "device_detail_query";
            }
            // 设备创建
            else if ("POST".equals(method) && requestUri.equals("/api/admin/devices")) {
                return "device_create";
            }
            // 设备更新
            else if ("PUT".equals(method) && requestUri.matches("/api/admin/devices/\\d+")) {
                return "device_update";
            }
            // 设备删除
            else if ("DELETE".equals(method) && requestUri.matches("/api/admin/devices/\\d+")) {
                return "device_delete";
            }
            // 设备状态更新
            else if ("PATCH".equals(method) && requestUri.matches("/api/admin/devices/\\d+/status")) {
                return "device_status_update";
            }
            // 设备重启
            else if ("POST".equals(method) && requestUri.matches("/api/admin/devices/\\d+/reboot")) {
                return "device_reboot";
            }
            // 设备激活
            else if ("POST".equals(method) && requestUri.matches("/api/admin/devices/\\d+/activate")) {
                return "device_activate";
            }
            // 固件推送
            else if ("POST".equals(method) && requestUri.matches("/api/admin/devices/\\d+/firmware")) {
                return "firmware_push";
            }
            // 设备日志查询
            else if ("GET".equals(method) && requestUri.matches("/api/admin/devices/\\d+/logs")) {
                return "device_logs_query";
            }
            // 批量固件推送
            else if ("POST".equals(method) && requestUri.equals("/api/admin/devices/batch/firmware")) {
                return "batch_firmware_push";
            }
            // 批量重启
            else if ("POST".equals(method) && requestUri.equals("/api/admin/devices/batch/reboot")) {
                return "batch_reboot";
            }
            // 批量删除
            else if ("DELETE".equals(method) && requestUri.equals("/api/admin/devices/batch")) {
                return "batch_delete";
            }
            // 设备统计
            else if ("GET".equals(method) && requestUri.equals("/api/admin/devices/stats")) {
                return "device_stats_query";
            }
            // 设备导出
            else if ("GET".equals(method) && requestUri.equals("/api/admin/devices/export")) {
                return "device_export";
            }
        }
        
        return null;
    }
}