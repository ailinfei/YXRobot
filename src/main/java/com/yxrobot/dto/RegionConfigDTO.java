package com.yxrobot.dto;

import com.yxrobot.entity.RegionConfig;

import java.util.List;

/**
 * 区域配置数据传输对象
 * 用于返回区域配置信息给前端
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-22
 */
public class RegionConfigDTO {
    
    /**
     * 地区名称
     */
    private String region;
    
    /**
     * 国家名称
     */
    private String country;
    
    /**
     * 支持的语言列表
     */
    private List<LanguageDTO> languages;
    
    // 默认构造函数
    public RegionConfigDTO() {
    }
    
    // 带参构造函数
    public RegionConfigDTO(String region, String country, List<LanguageDTO> languages) {
        this.region = region;
        this.country = country;
        this.languages = languages;
    }
    
    // Getters and Setters
    public String getRegion() {
        return region;
    }
    
    public void setRegion(String region) {
        this.region = region;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public List<LanguageDTO> getLanguages() {
        return languages;
    }
    
    public void setLanguages(List<LanguageDTO> languages) {
        this.languages = languages;
    }
    
    /**
     * 语言DTO
     */
    public static class LanguageDTO {
        private String code;
        private String name;
        
        // 构造函数
        public LanguageDTO() {
        }
        
        public LanguageDTO(String code, String name) {
            this.code = code;
            this.name = name;
        }
        
        // 从实体类转换的构造函数
        public LanguageDTO(RegionConfig config) {
            this.code = config.getLanguageCode();
            this.name = config.getLanguageName();
        }
        
        // Getters and Setters
        public String getCode() {
            return code;
        }
        
        public void setCode(String code) {
            this.code = code;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        @Override
        public String toString() {
            return "LanguageDTO{" +
                    "code='" + code + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
    
    @Override
    public String toString() {
        return "RegionConfigDTO{" +
                "region='" + region + '\'' +
                ", country='" + country + '\'' +
                ", languages=" + languages +
                '}';
    }
}