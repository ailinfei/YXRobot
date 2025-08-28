package com.yxrobot.service;

import com.yxrobot.dto.LinkClickLogDTO;
import com.yxrobot.entity.LinkClickLog;
import com.yxrobot.entity.PlatformLink;
import com.yxrobot.mapper.LinkClickLogMapper;
import com.yxrobot.mapper.PlatformLinkMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 链接点击统计服务类
 * 负责处理链接点击统计和转化跟踪相关的业务逻辑
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-22
 */
@Service
@Transactional
public class LinkClickService {
    
    private static final Logger logger = LoggerFactory.getLogger(LinkClickService.class);
    
    @Autowired
    private LinkClickLogMapper clickLogMapper;
    
    @Autowired
    private PlatformLinkMapper platformLinkMapper;
    
    /**
     * 记录链接点击事件
     * 
     * @param linkId 链接ID
     * @param request HTTP请求对象
     * @return 点击日志DTO
     * @throws IllegalArgumentException 如果链接不存在
     */
    @Async
    public LinkClickLogDTO recordClick(Long linkId, HttpServletRequest request) {
        logger.info("记录链接点击事件 - 链接ID: {}", linkId);
        
        if (linkId == null) {
            throw new IllegalArgumentException("链接ID不能为空");
        }
        
        // 验证链接是否存在
        PlatformLink link = platformLinkMapper.selectById(linkId);
        if (link == null) {
            throw new IllegalArgumentException("链接不存在，ID: " + linkId);
        }
        
        // 创建点击日志
        LinkClickLog clickLog = new LinkClickLog();
        clickLog.setLinkId(linkId);
        clickLog.setClickedAt(LocalDateTime.now());
        
        // 从请求中提取信息
        if (request != null) {
            clickLog.setUserIp(getClientIpAddress(request));
            clickLog.setUserAgent(request.getHeader("User-Agent"));
            clickLog.setReferer(request.getHeader("Referer"));
        }
        
        // 保存点击日志
        int result = clickLogMapper.insert(clickLog);
        if (result <= 0) {
            throw new RuntimeException("记录点击事件失败");
        }
        
        // 异步更新链接统计
        updateLinkClickStats(linkId);
        
        logger.info("记录链接点击事件成功 - 链接ID: {}, 日志ID: {}", linkId, clickLog.getId());
        return new LinkClickLogDTO(clickLog);
    }
    
    /**
     * 记录转化事件
     * 
     * @param linkId 链接ID
     * @param conversionType 转化类型
     * @param conversionValue 转化价值
     * @param request HTTP请求对象
     * @return 点击日志DTO
     * @throws IllegalArgumentException 如果链接不存在
     */
    @Async
    public LinkClickLogDTO recordConversion(Long linkId, String conversionType, 
                                          BigDecimal conversionValue, HttpServletRequest request) {
        logger.info("记录转化事件 - 链接ID: {}, 类型: {}, 价值: {}", linkId, conversionType, conversionValue);
        
        if (linkId == null) {
            throw new IllegalArgumentException("链接ID不能为空");
        }
        
        // 验证链接是否存在
        PlatformLink link = platformLinkMapper.selectById(linkId);
        if (link == null) {
            throw new IllegalArgumentException("链接不存在，ID: " + linkId);
        }
        
        // 创建转化日志
        LinkClickLog clickLog = new LinkClickLog();
        clickLog.setLinkId(linkId);
        clickLog.setClickedAt(LocalDateTime.now());
        clickLog.setIsConversion(true);
        clickLog.setConversionType(conversionType);
        clickLog.setConversionValue(conversionValue);
        
        // 从请求中提取信息
        if (request != null) {
            clickLog.setUserIp(getClientIpAddress(request));
            clickLog.setUserAgent(request.getHeader("User-Agent"));
            clickLog.setReferer(request.getHeader("Referer"));
        }
        
        // 保存转化日志
        int result = clickLogMapper.insert(clickLog);
        if (result <= 0) {
            throw new RuntimeException("记录转化事件失败");
        }
        
        // 异步更新链接统计
        updateLinkClickStats(linkId);
        
        logger.info("记录转化事件成功 - 链接ID: {}, 日志ID: {}, 类型: {}", 
                   linkId, clickLog.getId(), conversionType);
        return new LinkClickLogDTO(clickLog);
    }
    
    /**
     * 更新已有点击记录为转化
     * 
     * @param clickLogId 点击日志ID
     * @param conversionType 转化类型
     * @param conversionValue 转化价值
     * @return 是否更新成功
     */
    public boolean updateConversion(Long clickLogId, String conversionType, BigDecimal conversionValue) {
        logger.info("更新点击记录为转化 - 日志ID: {}, 类型: {}, 价值: {}", 
                   clickLogId, conversionType, conversionValue);
        
        if (clickLogId == null) {
            throw new IllegalArgumentException("点击日志ID不能为空");
        }
        
        // 查询点击日志
        LinkClickLog clickLog = clickLogMapper.selectById(clickLogId);
        if (clickLog == null) {
            throw new IllegalArgumentException("点击日志不存在，ID: " + clickLogId);
        }
        
        // 更新转化信息
        int result = clickLogMapper.updateConversion(clickLogId, conversionType, conversionValue);
        if (result <= 0) {
            logger.warn("更新转化信息失败 - 日志ID: {}", clickLogId);
            return false;
        }
        
        // 异步更新链接统计
        updateLinkClickStats(clickLog.getLinkId());
        
        logger.info("更新点击记录为转化成功 - 日志ID: {}", clickLogId);
        return true;
    }
    
    /**
     * 获取链接点击趋势数据
     * 
     * @param linkId 链接ID（可选）
     * @param days 天数
     * @return 趋势数据列表
     */
    public List<Map<String, Object>> getClickTrends(Long linkId, Integer days) {
        logger.info("获取点击趋势数据 - 链接ID: {}, 天数: {}", linkId, days);
        
        if (days == null || days <= 0) {
            days = 30; // 默认30天
        }
        
        List<Map<String, Object>> trends = clickLogMapper.selectClickTrends(linkId, days);
        
        logger.info("获取点击趋势数据完成 - 链接ID: {}, 数据点: {}", linkId, trends.size());
        return trends;
    }
    
    /**
     * 获取转化趋势数据
     * 
     * @param linkId 链接ID（可选）
     * @param days 天数
     * @return 趋势数据列表
     */
    public List<Map<String, Object>> getConversionTrends(Long linkId, Integer days) {
        logger.info("获取转化趋势数据 - 链接ID: {}, 天数: {}", linkId, days);
        
        if (days == null || days <= 0) {
            days = 30; // 默认30天
        }
        
        List<Map<String, Object>> trends = clickLogMapper.selectConversionTrends(linkId, days);
        
        logger.info("获取转化趋势数据完成 - 链接ID: {}, 数据点: {}", linkId, trends.size());
        return trends;
    }
    
    /**
     * 获取转化率统计
     * 
     * @param linkId 链接ID（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 转化率统计数据
     */
    public Map<String, Object> getConversionRates(Long linkId, LocalDateTime startTime, LocalDateTime endTime) {
        logger.info("获取转化率统计 - 链接ID: {}, 开始时间: {}, 结束时间: {}", linkId, startTime, endTime);
        
        // 获取点击量
        Long clickCount = clickLogMapper.selectClickCount(linkId, startTime, endTime);
        
        // 获取转化量
        Long conversionCount = clickLogMapper.selectConversionCount(linkId, startTime, endTime);
        
        // 计算转化率
        Double conversionRate = 0.0;
        if (clickCount != null && clickCount > 0 && conversionCount != null) {
            conversionRate = (conversionCount.doubleValue() / clickCount.doubleValue()) * 100;
        }
        
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("clickCount", clickCount != null ? clickCount : 0L);
        result.put("conversionCount", conversionCount != null ? conversionCount : 0L);
        result.put("conversionRate", Math.round(conversionRate * 100.0) / 100.0);
        
        logger.info("获取转化率统计完成 - 点击: {}, 转化: {}, 转化率: {}%", 
                   clickCount, conversionCount, result.get("conversionRate"));
        
        return result;
    }
    
    /**
     * 获取用户设备统计
     * 
     * @param linkId 链接ID（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 设备统计数据
     */
    public List<Map<String, Object>> getDeviceStats(Long linkId, LocalDateTime startTime, LocalDateTime endTime) {
        logger.info("获取用户设备统计 - 链接ID: {}, 开始时间: {}, 结束时间: {}", linkId, startTime, endTime);
        
        List<Map<String, Object>> deviceStats = clickLogMapper.selectDeviceStats(linkId, startTime, endTime);
        
        logger.info("获取用户设备统计完成 - 链接ID: {}, 设备类型数: {}", linkId, deviceStats.size());
        return deviceStats;
    }
    
    /**
     * 获取来源统计
     * 
     * @param linkId 链接ID（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 来源统计数据
     */
    public List<Map<String, Object>> getRefererStats(Long linkId, LocalDateTime startTime, LocalDateTime endTime) {
        logger.info("获取来源统计 - 链接ID: {}, 开始时间: {}, 结束时间: {}", linkId, startTime, endTime);
        
        List<Map<String, Object>> refererStats = clickLogMapper.selectRefererStats(linkId, startTime, endTime);
        
        logger.info("获取来源统计完成 - 链接ID: {}, 来源数: {}", linkId, refererStats.size());
        return refererStats;
    }
    
    /**
     * 获取链接的点击历史
     * 
     * @param linkId 链接ID
     * @param limit 限制数量
     * @return 点击历史列表
     */
    public List<LinkClickLogDTO> getClickHistory(Long linkId, Integer limit) {
        logger.info("获取链接点击历史 - 链接ID: {}, 限制: {}", linkId, limit);
        
        if (linkId == null) {
            throw new IllegalArgumentException("链接ID不能为空");
        }
        
        if (limit == null || limit <= 0) {
            limit = 50; // 默认50条
        }
        
        List<LinkClickLog> history = clickLogMapper.selectByLinkId(linkId, limit);
        List<LinkClickLogDTO> result = history.stream()
                .map(LinkClickLogDTO::new)
                .collect(Collectors.toList());
        
        logger.info("获取链接点击历史完成 - 链接ID: {}, 数量: {}", linkId, result.size());
        return result;
    }
    
    /**
     * 批量记录点击事件
     * 
     * @param clickLogs 点击日志列表
     * @return 成功记录的数量
     */
    public int batchRecordClicks(List<LinkClickLog> clickLogs) {
        logger.info("批量记录点击事件 - 数量: {}", clickLogs.size());
        
        if (clickLogs == null || clickLogs.isEmpty()) {
            throw new IllegalArgumentException("点击日志列表不能为空");
        }
        
        // 设置默认值
        LocalDateTime now = LocalDateTime.now();
        clickLogs.forEach(log -> {
            if (log.getClickedAt() == null) {
                log.setClickedAt(now);
            }
            if (log.getIsConversion() == null) {
                log.setIsConversion(false);
            }
        });
        
        int result = clickLogMapper.batchInsert(clickLogs);
        
        // 异步更新相关链接的统计
        clickLogs.stream()
                .map(LinkClickLog::getLinkId)
                .distinct()
                .forEach(this::updateLinkClickStats);
        
        logger.info("批量记录点击事件完成 - 成功: {}/{}", result, clickLogs.size());
        return result;
    }
    
    /**
     * 清理过期的点击日志
     * 
     * @param beforeTime 时间点
     * @return 清理的数量
     */
    public int cleanupOldClickLogs(LocalDateTime beforeTime) {
        logger.info("清理过期点击日志 - 时间点: {}", beforeTime);
        
        if (beforeTime == null) {
            throw new IllegalArgumentException("时间点不能为空");
        }
        
        int result = clickLogMapper.deleteBeforeTime(beforeTime);
        
        logger.info("清理过期点击日志完成 - 清理数量: {}", result);
        return result;
    }
    
    /**
     * 异步更新链接的点击统计
     * 
     * @param linkId 链接ID
     */
    @Async
    private void updateLinkClickStats(Long linkId) {
        try {
            clickLogMapper.updateLinkClickStats(linkId);
            logger.debug("更新链接点击统计成功 - 链接ID: {}", linkId);
        } catch (Exception e) {
            logger.error("更新链接点击统计失败 - 链接ID: {}, 错误: {}", linkId, e.getMessage(), e);
        }
    }
    
    /**
     * 获取客户端真实IP地址
     * 
     * @param request HTTP请求对象
     * @return IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String[] headerNames = {
            "X-Forwarded-For",
            "X-Real-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
        };
        
        for (String headerName : headerNames) {
            String ip = request.getHeader(headerName);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // 多级代理的情况，取第一个IP
                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }
        
        return request.getRemoteAddr();
    }
}