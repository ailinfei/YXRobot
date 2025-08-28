package com.yxrobot.validation;

import com.yxrobot.exception.PlatformLinkException;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * 平台链接数据验证工具类
 * 提供各种数据验证方法
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-22
 */
@Component
public class PlatformLinkValidator {
    
    // URL格式正则表达式
    private static final Pattern URL_PATTERN = Pattern.compile(
        "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$",
        Pattern.CASE_INSENSITIVE
    );
    
    // 语言代码格式正则表达式（ISO 639-1）
    private static final Pattern LANGUAGE_CODE_PATTERN = Pattern.compile(
        "^[a-z]{2}(-[A-Z]{2})?$"
    );
    
    // 地区名称格式正则表达式
    private static final Pattern REGION_NAME_PATTERN = Pattern.compile(
        "^[\\u4e00-\\u9fa5a-zA-Z\\s-]+$"
    );
    
    // 平台名称格式正则表达式
    private static final Pattern PLATFORM_NAME_PATTERN = Pattern.compile(
        "^[\\u4e00-\\u9fa5a-zA-Z0-9\\s.-]+$"
    );
    
    /**
     * 验证URL格式
     * 
     * @param url URL地址
     * @throws PlatformLinkException 如果URL格式无效
     */
    public void validateUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw PlatformLinkException.validationFailed("url", url, "URL不能为空");
        }
        
        String trimmedUrl = url.trim();
        
        // 基本格式验证
        if (!URL_PATTERN.matcher(trimmedUrl).matches()) {
            throw PlatformLinkException.invalidUrlFormat(trimmedUrl);
        }
        
        // 使用Java URL类进行更严格的验证
        try {
            new URL(trimmedUrl);
        } catch (MalformedURLException e) {
            throw PlatformLinkException.invalidUrlFormat(trimmedUrl);
        }
        
        // 检查URL长度
        if (trimmedUrl.length() > 500) {
            throw PlatformLinkException.validationFailed("url", trimmedUrl, "URL长度不能超过500字符");
        }
        
        // 检查是否为HTTPS（推荐）
        if (!trimmedUrl.toLowerCase().startsWith("https://")) {
            // 这里只是警告，不抛出异常
            // logger.warn("建议使用HTTPS协议: {}", trimmedUrl);
        }
    }
    
    /**
     * 验证平台名称
     * 
     * @param platformName 平台名称
     * @throws PlatformLinkException 如果平台名称无效
     */
    public void validatePlatformName(String platformName) {
        if (platformName == null || platformName.trim().isEmpty()) {
            throw PlatformLinkException.validationFailed("platformName", platformName, "平台名称不能为空");
        }
        
        String trimmedName = platformName.trim();
        
        if (trimmedName.length() > 100) {
            throw PlatformLinkException.validationFailed("platformName", trimmedName, "平台名称长度不能超过100字符");
        }
        
        if (!PLATFORM_NAME_PATTERN.matcher(trimmedName).matches()) {
            throw PlatformLinkException.validationFailed("platformName", trimmedName, "平台名称包含无效字符");
        }
    }
    
    /**
     * 验证平台类型
     * 
     * @param platformType 平台类型
     * @throws PlatformLinkException 如果平台类型无效
     */
    public void validatePlatformType(String platformType) {
        if (platformType == null || platformType.trim().isEmpty()) {
            throw PlatformLinkException.validationFailed("platformType", platformType, "平台类型不能为空");
        }
        
        String trimmedType = platformType.trim().toLowerCase();
        
        if (!"ecommerce".equals(trimmedType) && !"rental".equals(trimmedType)) {
            throw PlatformLinkException.validationFailed("platformType", platformType, "平台类型必须是 ecommerce 或 rental");
        }
    }
    
    /**
     * 验证地区名称
     * 
     * @param region 地区名称
     * @throws PlatformLinkException 如果地区名称无效
     */
    public void validateRegion(String region) {
        if (region == null || region.trim().isEmpty()) {
            throw PlatformLinkException.validationFailed("region", region, "地区名称不能为空");
        }
        
        String trimmedRegion = region.trim();
        
        if (trimmedRegion.length() > 50) {
            throw PlatformLinkException.validationFailed("region", trimmedRegion, "地区名称长度不能超过50字符");
        }
        
        if (!REGION_NAME_PATTERN.matcher(trimmedRegion).matches()) {
            throw PlatformLinkException.validationFailed("region", trimmedRegion, "地区名称包含无效字符");
        }
    }
    
    /**
     * 验证国家名称
     * 
     * @param country 国家名称
     * @throws PlatformLinkException 如果国家名称无效
     */
    public void validateCountry(String country) {
        if (country == null || country.trim().isEmpty()) {
            throw PlatformLinkException.validationFailed("country", country, "国家名称不能为空");
        }
        
        String trimmedCountry = country.trim();
        
        if (trimmedCountry.length() > 50) {
            throw PlatformLinkException.validationFailed("country", trimmedCountry, "国家名称长度不能超过50字符");
        }
        
        if (!REGION_NAME_PATTERN.matcher(trimmedCountry).matches()) {
            throw PlatformLinkException.validationFailed("country", trimmedCountry, "国家名称包含无效字符");
        }
    }
    
    /**
     * 验证语言代码
     * 
     * @param languageCode 语言代码
     * @throws PlatformLinkException 如果语言代码无效
     */
    public void validateLanguageCode(String languageCode) {
        if (languageCode == null || languageCode.trim().isEmpty()) {
            throw PlatformLinkException.validationFailed("languageCode", languageCode, "语言代码不能为空");
        }
        
        String trimmedCode = languageCode.trim();
        
        if (trimmedCode.length() > 10) {
            throw PlatformLinkException.validationFailed("languageCode", trimmedCode, "语言代码长度不能超过10字符");
        }
        
        if (!LANGUAGE_CODE_PATTERN.matcher(trimmedCode).matches()) {
            throw PlatformLinkException.validationFailed("languageCode", trimmedCode, "语言代码格式无效，应为 ISO 639-1 格式（如：en, zh-CN）");
        }
    }
    
    /**
     * 验证语言名称
     * 
     * @param languageName 语言名称
     * @throws PlatformLinkException 如果语言名称无效
     */
    public void validateLanguageName(String languageName) {
        if (languageName == null || languageName.trim().isEmpty()) {
            throw PlatformLinkException.validationFailed("languageName", languageName, "语言名称不能为空");
        }
        
        String trimmedName = languageName.trim();
        
        if (trimmedName.length() > 50) {
            throw PlatformLinkException.validationFailed("languageName", trimmedName, "语言名称长度不能超过50字符");
        }
        
        if (!REGION_NAME_PATTERN.matcher(trimmedName).matches()) {
            throw PlatformLinkException.validationFailed("languageName", trimmedName, "语言名称包含无效字符");
        }
    }
    
    /**
     * 验证点击量
     * 
     * @param clickCount 点击量
     * @throws PlatformLinkException 如果点击量无效
     */
    public void validateClickCount(Integer clickCount) {
        if (clickCount != null && clickCount < 0) {
            throw PlatformLinkException.validationFailed("clickCount", clickCount, "点击量不能为负数");
        }
    }
    
    /**
     * 验证转化量
     * 
     * @param conversionCount 转化量
     * @throws PlatformLinkException 如果转化量无效
     */
    public void validateConversionCount(Integer conversionCount) {
        if (conversionCount != null && conversionCount < 0) {
            throw PlatformLinkException.validationFailed("conversionCount", conversionCount, "转化量不能为负数");
        }
    }
    
    /**
     * 验证转化量不能超过点击量
     * 
     * @param clickCount 点击量
     * @param conversionCount 转化量
     * @throws PlatformLinkException 如果转化量超过点击量
     */
    public void validateConversionNotExceedClick(Integer clickCount, Integer conversionCount) {
        if (clickCount != null && conversionCount != null && conversionCount > clickCount) {
            throw PlatformLinkException.validationFailed(
                "conversionCount", 
                conversionCount, 
                "转化量不能超过点击量（点击量: " + clickCount + ", 转化量: " + conversionCount + "）"
            );
        }
    }
    
    /**
     * 验证排序顺序
     * 
     * @param sortOrder 排序顺序
     * @throws PlatformLinkException 如果排序顺序无效
     */
    public void validateSortOrder(Integer sortOrder) {
        if (sortOrder != null && sortOrder < 0) {
            throw PlatformLinkException.validationFailed("sortOrder", sortOrder, "排序顺序不能为负数");
        }
    }
    
    /**
     * 验证ID
     * 
     * @param id ID值
     * @param fieldName 字段名称
     * @throws PlatformLinkException 如果ID无效
     */
    public void validateId(Long id, String fieldName) {
        if (id == null) {
            throw PlatformLinkException.validationFailed(fieldName, id, fieldName + "不能为空");
        }
        
        if (id <= 0) {
            throw PlatformLinkException.validationFailed(fieldName, id, fieldName + "必须大于0");
        }
    }
    
    /**
     * 验证分页参数
     * 
     * @param page 页码
     * @param pageSize 页大小
     * @throws PlatformLinkException 如果分页参数无效
     */
    public void validatePagination(Integer page, Integer pageSize) {
        if (page != null && page < 1) {
            throw PlatformLinkException.validationFailed("page", page, "页码必须大于等于1");
        }
        
        if (pageSize != null) {
            if (pageSize < 1) {
                throw PlatformLinkException.validationFailed("pageSize", pageSize, "页大小必须大于等于1");
            }
            if (pageSize > 100) {
                throw PlatformLinkException.validationFailed("pageSize", pageSize, "页大小不能超过100");
            }
        }
    }
}