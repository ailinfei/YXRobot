package com.yxrobot.service;

import com.yxrobot.dto.LinkValidationResultDTO;
import com.yxrobot.entity.LinkValidationLog;
import com.yxrobot.entity.PlatformLink;
import com.yxrobot.mapper.LinkValidationLogMapper;
import com.yxrobot.mapper.PlatformLinkMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 链接验证服务类
 * 负责处理平台链接验证相关的业务逻辑
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-22
 */
@Service
@Transactional
public class LinkValidationService {
    
    private static final Logger logger = LoggerFactory.getLogger(LinkValidationService.class);
    
    private static final int CONNECTION_TIMEOUT = 10000; // 10秒连接超时
    private static final int READ_TIMEOUT = 15000; // 15秒读取超时
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
    
    @Autowired
    private PlatformLinkMapper platformLinkMapper;
    
    @Autowired
    private LinkValidationLogMapper validationLogMapper;
    
    /**
     * 验证单个平台链接的有效性
     * 
     * @param linkId 链接ID
     * @return 验证结果DTO
     * @throws IllegalArgumentException 如果链接不存在
     */
    public LinkValidationResultDTO validatePlatformLink(Long linkId) {
        logger.info("开始验证平台链接 - ID: {}", linkId);
        
        if (linkId == null) {
            throw new IllegalArgumentException("链接ID不能为空");
        }
        
        // 查询链接信息
        PlatformLink link = platformLinkMapper.selectById(linkId);
        if (link == null) {
            throw new IllegalArgumentException("链接不存在，ID: " + linkId);
        }
        
        // 执行验证
        LinkValidationResultDTO result = performValidation(link);
        
        // 记录验证日志
        recordValidationLog(linkId, result);
        
        // 更新链接状态
        updateLinkStatus(linkId, result);
        
        logger.info("平台链接验证完成 - ID: {}, 有效: {}, 状态码: {}, 响应时间: {}ms", 
                   linkId, result.getIsValid(), result.getStatusCode(), result.getResponseTime());
        
        return result;
    }
    
    /**
     * 异步验证平台链接
     * 
     * @param linkId 链接ID
     * @return 异步验证结果
     */
    @Async
    public CompletableFuture<LinkValidationResultDTO> validatePlatformLinkAsync(Long linkId) {
        logger.info("开始异步验证平台链接 - ID: {}", linkId);
        
        try {
            LinkValidationResultDTO result = validatePlatformLink(linkId);
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            logger.error("异步验证平台链接失败 - ID: {}, 错误: {}", linkId, e.getMessage(), e);
            
            // 创建失败结果
            LinkValidationResultDTO errorResult = new LinkValidationResultDTO();
            errorResult.setId(linkId);
            errorResult.setIsValid(false);
            errorResult.setErrorMessage("验证过程中发生错误: " + e.getMessage());
            errorResult.setCheckedAt(LocalDateTime.now());
            
            return CompletableFuture.completedFuture(errorResult);
        }
    }
    
    /**
     * 批量验证平台链接
     * 
     * @param linkIds 链接ID列表
     * @return 验证结果列表
     */
    public List<LinkValidationResultDTO> batchValidatePlatformLinks(List<Long> linkIds) {
        logger.info("开始批量验证平台链接 - 数量: {}", linkIds.size());
        
        if (linkIds == null || linkIds.isEmpty()) {
            throw new IllegalArgumentException("链接ID列表不能为空");
        }
        
        List<LinkValidationResultDTO> results = linkIds.parallelStream()
                .map(linkId -> {
                    try {
                        return validatePlatformLink(linkId);
                    } catch (Exception e) {
                        logger.warn("验证链接失败 - ID: {}, 错误: {}", linkId, e.getMessage());
                        
                        LinkValidationResultDTO errorResult = new LinkValidationResultDTO();
                        errorResult.setId(linkId);
                        errorResult.setIsValid(false);
                        errorResult.setErrorMessage("验证失败: " + e.getMessage());
                        errorResult.setCheckedAt(LocalDateTime.now());
                        
                        return errorResult;
                    }
                })
                .collect(java.util.stream.Collectors.toList());
        
        logger.info("批量验证平台链接完成 - 总数: {}, 成功: {}", 
                   linkIds.size(), results.stream().mapToInt(r -> r.getIsValid() ? 1 : 0).sum());
        
        return results;
    }
    
    /**
     * 验证需要检查的链接（定时任务使用）
     * 
     * @param limit 限制数量
     * @return 验证结果列表
     */
    public List<LinkValidationResultDTO> validateLinksForScheduledTask(Integer limit) {
        logger.info("开始定时验证链接 - 限制数量: {}", limit);
        
        if (limit == null || limit <= 0) {
            limit = 50; // 默认限制50个
        }
        
        // 查询需要检查的链接
        List<PlatformLink> linksToCheck = platformLinkMapper.selectLinksForCheck(limit);
        
        if (linksToCheck.isEmpty()) {
            logger.info("没有需要验证的链接");
            return java.util.Collections.emptyList();
        }
        
        // 批量验证
        List<Long> linkIds = linksToCheck.stream()
                .map(PlatformLink::getId)
                .collect(java.util.stream.Collectors.toList());
        
        List<LinkValidationResultDTO> results = batchValidatePlatformLinks(linkIds);
        
        logger.info("定时验证链接完成 - 检查数量: {}, 成功数量: {}", 
                   linksToCheck.size(), results.stream().mapToInt(r -> r.getIsValid() ? 1 : 0).sum());
        
        return results;
    }
    
    /**
     * 获取链接的验证历史
     * 
     * @param linkId 链接ID
     * @param limit 限制数量
     * @return 验证历史列表
     */
    public List<LinkValidationLog> getValidationHistory(Long linkId, Integer limit) {
        logger.info("查询链接验证历史 - ID: {}, 限制: {}", linkId, limit);
        
        if (linkId == null) {
            throw new IllegalArgumentException("链接ID不能为空");
        }
        
        if (limit == null || limit <= 0) {
            limit = 20; // 默认20条
        }
        
        List<LinkValidationLog> history = validationLogMapper.selectByLinkId(linkId, limit);
        
        logger.info("查询链接验证历史完成 - ID: {}, 数量: {}", linkId, history.size());
        return history;
    }
    
    /**
     * 获取链接的最新验证结果
     * 
     * @param linkId 链接ID
     * @return 最新验证结果
     */
    public LinkValidationLog getLatestValidationResult(Long linkId) {
        logger.info("查询链接最新验证结果 - ID: {}", linkId);
        
        if (linkId == null) {
            throw new IllegalArgumentException("链接ID不能为空");
        }
        
        LinkValidationLog latest = validationLogMapper.selectLatestByLinkId(linkId);
        
        logger.info("查询链接最新验证结果完成 - ID: {}, 有结果: {}", linkId, latest != null);
        return latest;
    }
    
    /**
     * 执行实际的链接验证
     * 
     * @param link 平台链接实体
     * @return 验证结果
     */
    private LinkValidationResultDTO performValidation(PlatformLink link) {
        LinkValidationResultDTO result = new LinkValidationResultDTO();
        result.setId(link.getId());
        result.setCheckedAt(LocalDateTime.now());
        
        HttpURLConnection connection = null;
        long startTime = System.currentTimeMillis();
        
        try {
            // 创建URL连接
            URL url = new URL(link.getLinkUrl());
            connection = (HttpURLConnection) url.openConnection();
            
            // 设置请求属性
            connection.setRequestMethod("HEAD"); // 使用HEAD请求减少数据传输
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setRequestProperty("User-Agent", USER_AGENT);
            connection.setRequestProperty("Accept", "*/*");
            connection.setInstanceFollowRedirects(true);
            
            // 执行请求
            connection.connect();
            
            // 计算响应时间
            long responseTime = System.currentTimeMillis() - startTime;
            result.setResponseTime((int) responseTime);
            
            // 获取响应状态码
            int statusCode = connection.getResponseCode();
            result.setStatusCode(statusCode);
            
            // 判断是否有效
            boolean isValid = statusCode >= 200 && statusCode < 400;
            result.setIsValid(isValid);
            
            if (!isValid) {
                result.setErrorMessage("HTTP状态码: " + statusCode + " - " + connection.getResponseMessage());
            }
            
            logger.debug("链接验证详情 - URL: {}, 状态码: {}, 响应时间: {}ms, 有效: {}", 
                        link.getLinkUrl(), statusCode, responseTime, isValid);
            
        } catch (SocketTimeoutException e) {
            result.setIsValid(false);
            result.setErrorMessage("连接超时: " + e.getMessage());
            result.setResponseTime((int) (System.currentTimeMillis() - startTime));
            logger.warn("链接验证超时 - URL: {}, 错误: {}", link.getLinkUrl(), e.getMessage());
            
        } catch (IOException e) {
            result.setIsValid(false);
            result.setErrorMessage("网络错误: " + e.getMessage());
            result.setResponseTime((int) (System.currentTimeMillis() - startTime));
            logger.warn("链接验证网络错误 - URL: {}, 错误: {}", link.getLinkUrl(), e.getMessage());
            
        } catch (Exception e) {
            result.setIsValid(false);
            result.setErrorMessage("验证异常: " + e.getMessage());
            result.setResponseTime((int) (System.currentTimeMillis() - startTime));
            logger.error("链接验证异常 - URL: {}, 错误: {}", link.getLinkUrl(), e.getMessage(), e);
            
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        
        return result;
    }
    
    /**
     * 记录验证日志
     * 
     * @param linkId 链接ID
     * @param result 验证结果
     */
    private void recordValidationLog(Long linkId, LinkValidationResultDTO result) {
        try {
            LinkValidationLog log = new LinkValidationLog();
            log.setLinkId(linkId);
            log.setIsValid(result.getIsValid());
            log.setStatusCode(result.getStatusCode());
            log.setResponseTime(result.getResponseTime());
            log.setErrorMessage(result.getErrorMessage());
            log.setCheckedAt(result.getCheckedAt());
            
            validationLogMapper.insert(log);
            
            logger.debug("验证日志记录成功 - 链接ID: {}", linkId);
            
        } catch (Exception e) {
            logger.error("记录验证日志失败 - 链接ID: {}, 错误: {}", linkId, e.getMessage(), e);
        }
    }
    
    /**
     * 更新链接状态
     * 
     * @param linkId 链接ID
     * @param result 验证结果
     */
    private void updateLinkStatus(Long linkId, LinkValidationResultDTO result) {
        try {
            String linkStatus;
            if (result.getIsValid()) {
                linkStatus = PlatformLink.LinkStatus.ACTIVE.getCode();
            } else {
                linkStatus = PlatformLink.LinkStatus.INACTIVE.getCode();
            }
            
            platformLinkMapper.updateLinkStatus(linkId, linkStatus);
            
            logger.debug("链接状态更新成功 - 链接ID: {}, 状态: {}", linkId, linkStatus);
            
        } catch (Exception e) {
            logger.error("更新链接状态失败 - 链接ID: {}, 错误: {}", linkId, e.getMessage(), e);
        }
    }
}