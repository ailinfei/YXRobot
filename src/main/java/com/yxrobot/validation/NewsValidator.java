package com.yxrobot.validation;

import com.yxrobot.dto.NewsFormDTO;
import com.yxrobot.entity.NewsStatus;
import com.yxrobot.exception.NewsValidationException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 新闻数据验证器
 * 提供新闻相关的业务验证逻辑
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@Component
public class NewsValidator {
    
    // HTML标签正则表达式
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]+>");
    
    // 敏感词列表（示例）
    private static final String[] SENSITIVE_WORDS = {
        "违法", "暴力", "色情", "赌博", "毒品"
    };
    
    /**
     * 验证新闻表单数据
     * 
     * @param newsFormDTO 新闻表单数据
     * @throws NewsValidationException 验证失败时抛出异常
     */
    public void validateNewsForm(NewsFormDTO newsFormDTO) {
        Map<String, String> errors = new HashMap<>();
        
        // 验证标题
        validateTitle(newsFormDTO.getTitle(), errors);
        
        // 验证内容
        validateContent(newsFormDTO.getContent(), errors);
        
        // 验证摘要
        validateExcerpt(newsFormDTO.getExcerpt(), newsFormDTO.getContent(), errors);
        
        // 验证作者
        validateAuthor(newsFormDTO.getAuthor(), errors);
        
        // 验证发布时间
        validatePublishTime(newsFormDTO.getPublishTime(), newsFormDTO.getStatus(), errors);
        
        // 验证封面图片
        validateCoverImage(newsFormDTO.getCoverImage(), errors);
        
        // 验证排序权重
        validateSortOrder(newsFormDTO.getSortOrder(), errors);
        
        if (!errors.isEmpty()) {
            throw new NewsValidationException("新闻数据验证失败", errors);
        }
    }
    
    /**
     * 验证新闻标题
     * 
     * @param title 标题
     * @param errors 错误信息集合
     */
    private void validateTitle(String title, Map<String, String> errors) {
        if (!StringUtils.hasText(title)) {
            errors.put("title", "新闻标题不能为空");
            return;
        }
        
        // 去除HTML标签后的长度验证
        String plainTitle = HTML_TAG_PATTERN.matcher(title).replaceAll("");
        if (plainTitle.length() < 5) {
            errors.put("title", "新闻标题（去除HTML标签后）长度不能少于5个字符");
        } else if (plainTitle.length() > 200) {
            errors.put("title", "新闻标题（去除HTML标签后）长度不能超过200个字符");
        }
        
        // 敏感词检查
        if (containsSensitiveWords(plainTitle)) {
            errors.put("title", "新闻标题包含敏感词汇，请修改后重试");
        }
        
        // 特殊字符检查
        if (title.contains("javascript:") || title.contains("<script")) {
            errors.put("title", "新闻标题包含不安全的内容");
        }
    }
    
    /**
     * 验证新闻内容
     * 
     * @param content 内容
     * @param errors 错误信息集合
     */
    private void validateContent(String content, Map<String, String> errors) {
        if (!StringUtils.hasText(content)) {
            errors.put("content", "新闻内容不能为空");
            return;
        }
        
        // 去除HTML标签后的长度验证
        String plainContent = HTML_TAG_PATTERN.matcher(content).replaceAll("");
        if (plainContent.length() < 50) {
            errors.put("content", "新闻内容（去除HTML标签后）长度不能少于50个字符");
        } else if (plainContent.length() > 50000) {
            errors.put("content", "新闻内容（去除HTML标签后）长度不能超过50000个字符");
        }
        
        // 敏感词检查
        if (containsSensitiveWords(plainContent)) {
            errors.put("content", "新闻内容包含敏感词汇，请修改后重试");
        }
        
        // 危险脚本检查
        if (content.toLowerCase().contains("<script") || 
            content.toLowerCase().contains("javascript:") ||
            content.toLowerCase().contains("onclick=") ||
            content.toLowerCase().contains("onerror=")) {
            errors.put("content", "新闻内容包含不安全的脚本代码");
        }
    }
    
    /**
     * 验证新闻摘要
     * 
     * @param excerpt 摘要
     * @param content 内容
     * @param errors 错误信息集合
     */
    private void validateExcerpt(String excerpt, String content, Map<String, String> errors) {
        if (StringUtils.hasText(excerpt)) {
            // 去除HTML标签后的长度验证
            String plainExcerpt = HTML_TAG_PATTERN.matcher(excerpt).replaceAll("");
            if (plainExcerpt.length() < 10) {
                errors.put("excerpt", "新闻摘要（去除HTML标签后）长度不能少于10个字符");
            } else if (plainExcerpt.length() > 500) {
                errors.put("excerpt", "新闻摘要（去除HTML标签后）长度不能超过500个字符");
            }
            
            // 敏感词检查
            if (containsSensitiveWords(plainExcerpt)) {
                errors.put("excerpt", "新闻摘要包含敏感词汇，请修改后重试");
            }
        } else if (StringUtils.hasText(content)) {
            // 如果没有提供摘要，检查是否可以从内容中自动生成
            String plainContent = HTML_TAG_PATTERN.matcher(content).replaceAll("");
            if (plainContent.length() < 100) {
                errors.put("excerpt", "新闻内容过短，无法自动生成摘要，请手动填写摘要");
            }
        }
    }
    
    /**
     * 验证作者信息
     * 
     * @param author 作者
     * @param errors 错误信息集合
     */
    private void validateAuthor(String author, Map<String, String> errors) {
        if (!StringUtils.hasText(author)) {
            errors.put("author", "作者不能为空");
            return;
        }
        
        if (author.length() > 100) {
            errors.put("author", "作者姓名不能超过100个字符");
        }
        
        // 特殊字符检查
        if (author.matches(".*[<>\"'&].*")) {
            errors.put("author", "作者姓名不能包含特殊字符");
        }
    }
    
    /**
     * 验证发布时间
     * 
     * @param publishTime 发布时间
     * @param status 新闻状态
     * @param errors 错误信息集合
     */
    private void validatePublishTime(LocalDateTime publishTime, NewsStatus status, Map<String, String> errors) {
        if (status == NewsStatus.PUBLISHED && publishTime == null) {
            errors.put("publishTime", "发布状态的新闻必须设置发布时间");
        }
        
        if (publishTime != null) {
            LocalDateTime now = LocalDateTime.now();
            
            // 发布时间不能太早（1年前）
            if (publishTime.isBefore(now.minusYears(1))) {
                errors.put("publishTime", "发布时间不能早于一年前");
            }
            
            // 发布时间不能太晚（1年后）
            if (publishTime.isAfter(now.plusYears(1))) {
                errors.put("publishTime", "发布时间不能晚于一年后");
            }
        }
    }
    
    /**
     * 验证封面图片
     * 
     * @param coverImage 封面图片URL
     * @param errors 错误信息集合
     */
    private void validateCoverImage(String coverImage, Map<String, String> errors) {
        if (StringUtils.hasText(coverImage)) {
            // URL格式验证
            if (!isValidImageUrl(coverImage)) {
                errors.put("coverImage", "封面图片URL格式不正确");
            }
            
            // 长度验证
            if (coverImage.length() > 500) {
                errors.put("coverImage", "封面图片URL长度不能超过500个字符");
            }
        }
    }
    
    /**
     * 验证排序权重
     * 
     * @param sortOrder 排序权重
     * @param errors 错误信息集合
     */
    private void validateSortOrder(Integer sortOrder, Map<String, String> errors) {
        if (sortOrder != null) {
            if (sortOrder < 0) {
                errors.put("sortOrder", "排序权重不能为负数");
            } else if (sortOrder > 9999) {
                errors.put("sortOrder", "排序权重不能超过9999");
            }
        }
    }
    
    /**
     * 验证新闻状态转换是否合法
     * 
     * @param currentStatus 当前状态
     * @param targetStatus 目标状态
     * @return 是否合法
     */
    public boolean isValidStatusTransition(NewsStatus currentStatus, NewsStatus targetStatus) {
        if (currentStatus == null || targetStatus == null) {
            return false;
        }
        
        if (currentStatus == targetStatus) {
            return true;
        }
        
        switch (currentStatus) {
            case DRAFT:
                // 草稿可以转换为已发布或已下线
                return targetStatus == NewsStatus.PUBLISHED || targetStatus == NewsStatus.OFFLINE;
            
            case PUBLISHED:
                // 已发布可以转换为草稿或已下线
                return targetStatus == NewsStatus.DRAFT || targetStatus == NewsStatus.OFFLINE;
            
            case OFFLINE:
                // 已下线可以转换为草稿或已发布
                return targetStatus == NewsStatus.DRAFT || targetStatus == NewsStatus.PUBLISHED;
            
            default:
                return false;
        }
    }
    
    /**
     * 检查文本是否包含敏感词
     * 
     * @param text 文本
     * @return 是否包含敏感词
     */
    private boolean containsSensitiveWords(String text) {
        if (!StringUtils.hasText(text)) {
            return false;
        }
        
        String lowerText = text.toLowerCase();
        for (String word : SENSITIVE_WORDS) {
            if (lowerText.contains(word)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 验证图片URL格式
     * 
     * @param url 图片URL
     * @return 是否为有效的图片URL
     */
    private boolean isValidImageUrl(String url) {
        if (!StringUtils.hasText(url)) {
            return false;
        }
        
        // 简单的URL格式验证
        if (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("/")) {
            return false;
        }
        
        // 图片文件扩展名验证
        String lowerUrl = url.toLowerCase();
        return lowerUrl.endsWith(".jpg") || 
               lowerUrl.endsWith(".jpeg") || 
               lowerUrl.endsWith(".png") || 
               lowerUrl.endsWith(".gif") || 
               lowerUrl.endsWith(".webp") ||
               lowerUrl.contains("/upload/") || // 允许上传路径
               lowerUrl.contains("image"); // 允许包含image的动态URL
    }
}