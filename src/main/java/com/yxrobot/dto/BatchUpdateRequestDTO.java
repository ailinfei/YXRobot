package com.yxrobot.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 批量更新请求数据传输对象
 * 用于批量更新平台链接的操作
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-22
 */
public class BatchUpdateRequestDTO {
    
    /**
     * 要更新的链接ID列表
     */
    @NotEmpty(message = "链接ID列表不能为空")
    private List<Long> linkIds;
    
    /**
     * 更新的数据
     */
    @NotNull(message = "更新数据不能为空")
    @Valid
    private UpdateDataDTO updates;
    
    // 默认构造函数
    public BatchUpdateRequestDTO() {
    }
    
    // 带参构造函数
    public BatchUpdateRequestDTO(List<Long> linkIds, UpdateDataDTO updates) {
        this.linkIds = linkIds;
        this.updates = updates;
    }
    
    // Getters and Setters
    public List<Long> getLinkIds() {
        return linkIds;
    }
    
    public void setLinkIds(List<Long> linkIds) {
        this.linkIds = linkIds;
    }
    
    public UpdateDataDTO getUpdates() {
        return updates;
    }
    
    public void setUpdates(UpdateDataDTO updates) {
        this.updates = updates;
    }
    
    /**
     * 更新数据DTO
     */
    public static class UpdateDataDTO {
        
        /**
         * 是否启用
         */
        private Boolean isEnabled;
        
        /**
         * 地区
         */
        private String region;
        
        /**
         * 国家
         */
        private String country;
        
        /**
         * 语言代码
         */
        private String languageCode;
        
        /**
         * 平台类型
         */
        private String platformType;
        
        // 默认构造函数
        public UpdateDataDTO() {
        }
        
        // Getters and Setters
        public Boolean getIsEnabled() {
            return isEnabled;
        }
        
        public void setIsEnabled(Boolean isEnabled) {
            this.isEnabled = isEnabled;
        }
        
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
        
        public String getLanguageCode() {
            return languageCode;
        }
        
        public void setLanguageCode(String languageCode) {
            this.languageCode = languageCode;
        }
        
        public String getPlatformType() {
            return platformType;
        }
        
        public void setPlatformType(String platformType) {
            this.platformType = platformType;
        }
        
        /**
         * 检查是否有任何更新字段
         * @return true如果有任何字段需要更新
         */
        public boolean hasUpdates() {
            return isEnabled != null || 
                   (region != null && !region.trim().isEmpty()) ||
                   (country != null && !country.trim().isEmpty()) ||
                   (languageCode != null && !languageCode.trim().isEmpty()) ||
                   (platformType != null && !platformType.trim().isEmpty());
        }
        
        @Override
        public String toString() {
            return "UpdateDataDTO{" +
                    "isEnabled=" + isEnabled +
                    ", region='" + region + '\'' +
                    ", country='" + country + '\'' +
                    ", languageCode='" + languageCode + '\'' +
                    ", platformType='" + platformType + '\'' +
                    '}';
        }
    }
    
    /**
     * 获取要更新的链接数量
     * @return 链接数量
     */
    public int getLinkCount() {
        return linkIds != null ? linkIds.size() : 0;
    }
    
    @Override
    public String toString() {
        return "BatchUpdateRequestDTO{" +
                "linkIds=" + linkIds +
                ", updates=" + updates +
                '}';
    }
}