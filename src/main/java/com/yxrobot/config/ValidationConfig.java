package com.yxrobot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

/**
 * 验证配置类
 * 管理客户数据验证的各种配置参数
 */
@Configuration
@ConfigurationProperties(prefix = "yxrobot.validation.customer")
public class ValidationConfig {
    
    // 字段长度限制
    private FieldLimits fieldLimits = new FieldLimits();
    
    // 业务规则配置
    private BusinessRules businessRules = new BusinessRules();
    
    // 验证开关
    private ValidationSwitches switches = new ValidationSwitches();
    
    // 敏感词配置
    private SensitiveWords sensitiveWords = new SensitiveWords();
    
    // 电话号码配置
    private PhoneConfig phoneConfig = new PhoneConfig();
    
    // 邮箱配置
    private EmailConfig emailConfig = new EmailConfig();
    
    public FieldLimits getFieldLimits() {
        return fieldLimits;
    }
    
    public void setFieldLimits(FieldLimits fieldLimits) {
        this.fieldLimits = fieldLimits;
    }
    
    public BusinessRules getBusinessRules() {
        return businessRules;
    }
    
    public void setBusinessRules(BusinessRules businessRules) {
        this.businessRules = businessRules;
    }
    
    public ValidationSwitches getSwitches() {
        return switches;
    }
    
    public void setSwitches(ValidationSwitches switches) {
        this.switches = switches;
    }
    
    public SensitiveWords getSensitiveWords() {
        return sensitiveWords;
    }
    
    public void setSensitiveWords(SensitiveWords sensitiveWords) {
        this.sensitiveWords = sensitiveWords;
    }
    
    public PhoneConfig getPhoneConfig() {
        return phoneConfig;
    }
    
    public void setPhoneConfig(PhoneConfig phoneConfig) {
        this.phoneConfig = phoneConfig;
    }
    
    public EmailConfig getEmailConfig() {
        return emailConfig;
    }
    
    public void setEmailConfig(EmailConfig emailConfig) {
        this.emailConfig = emailConfig;
    }
    
    /**
     * 字段长度限制配置
     */
    public static class FieldLimits {
        private int minNameLength = 2;
        private int maxNameLength = 50;
        private int maxCompanyLength = 100;
        private int maxNotesLength = 500;
        private int maxTagsCount = 10;
        private int maxTagLength = 20;
        private int maxEmailLength = 100;
        private int maxAddressLength = 200;
        private int maxKeywordLength = 100;
        
        // Getters and Setters
        public int getMinNameLength() {
            return minNameLength;
        }
        
        public void setMinNameLength(int minNameLength) {
            this.minNameLength = minNameLength;
        }
        
        public int getMaxNameLength() {
            return maxNameLength;
        }
        
        public void setMaxNameLength(int maxNameLength) {
            this.maxNameLength = maxNameLength;
        }
        
        public int getMaxCompanyLength() {
            return maxCompanyLength;
        }
        
        public void setMaxCompanyLength(int maxCompanyLength) {
            this.maxCompanyLength = maxCompanyLength;
        }
        
        public int getMaxNotesLength() {
            return maxNotesLength;
        }
        
        public void setMaxNotesLength(int maxNotesLength) {
            this.maxNotesLength = maxNotesLength;
        }
        
        public int getMaxTagsCount() {
            return maxTagsCount;
        }
        
        public void setMaxTagsCount(int maxTagsCount) {
            this.maxTagsCount = maxTagsCount;
        }
        
        public int getMaxTagLength() {
            return maxTagLength;
        }
        
        public void setMaxTagLength(int maxTagLength) {
            this.maxTagLength = maxTagLength;
        }
        
        public int getMaxEmailLength() {
            return maxEmailLength;
        }
        
        public void setMaxEmailLength(int maxEmailLength) {
            this.maxEmailLength = maxEmailLength;
        }
        
        public int getMaxAddressLength() {
            return maxAddressLength;
        }
        
        public void setMaxAddressLength(int maxAddressLength) {
            this.maxAddressLength = maxAddressLength;
        }
        
        public int getMaxKeywordLength() {
            return maxKeywordLength;
        }
        
        public void setMaxKeywordLength(int maxKeywordLength) {
            this.maxKeywordLength = maxKeywordLength;
        }
    }
    
    /**
     * 业务规则配置
     */
    public static class BusinessRules {
        private BigDecimal vipThreshold = new BigDecimal("10000");
        private BigDecimal premiumThreshold = new BigDecimal("50000");
        private BigDecimal maxCustomerValue = new BigDecimal("10");
        private BigDecimal minCustomerValue = new BigDecimal("0");
        private int inactiveThresholdDays = 90;
        private int activeThresholdDays = 30;
        private int maxPageSize = 100;
        private int defaultPageSize = 20;
        
        // Getters and Setters
        public BigDecimal getVipThreshold() {
            return vipThreshold;
        }
        
        public void setVipThreshold(BigDecimal vipThreshold) {
            this.vipThreshold = vipThreshold;
        }
        
        public BigDecimal getPremiumThreshold() {
            return premiumThreshold;
        }
        
        public void setPremiumThreshold(BigDecimal premiumThreshold) {
            this.premiumThreshold = premiumThreshold;
        }
        
        public BigDecimal getMaxCustomerValue() {
            return maxCustomerValue;
        }
        
        public void setMaxCustomerValue(BigDecimal maxCustomerValue) {
            this.maxCustomerValue = maxCustomerValue;
        }
        
        public BigDecimal getMinCustomerValue() {
            return minCustomerValue;
        }
        
        public void setMinCustomerValue(BigDecimal minCustomerValue) {
            this.minCustomerValue = minCustomerValue;
        }
        
        public int getInactiveThresholdDays() {
            return inactiveThresholdDays;
        }
        
        public void setInactiveThresholdDays(int inactiveThresholdDays) {
            this.inactiveThresholdDays = inactiveThresholdDays;
        }
        
        public int getActiveThresholdDays() {
            return activeThresholdDays;
        }
        
        public void setActiveThresholdDays(int activeThresholdDays) {
            this.activeThresholdDays = activeThresholdDays;
        }
        
        public int getMaxPageSize() {
            return maxPageSize;
        }
        
        public void setMaxPageSize(int maxPageSize) {
            this.maxPageSize = maxPageSize;
        }
        
        public int getDefaultPageSize() {
            return defaultPageSize;
        }
        
        public void setDefaultPageSize(int defaultPageSize) {
            this.defaultPageSize = defaultPageSize;
        }
    }
    
    /**
     * 验证开关配置
     */
    public static class ValidationSwitches {
        private boolean enableStrictValidation = true;
        private boolean enableSensitiveWordCheck = true;
        private boolean enablePhoneCarrierCheck = true;
        private boolean enableEmailProviderCheck = true;
        private boolean enableTemporaryEmailCheck = true;
        private boolean enableDataIntegrityCheck = true;
        private boolean enableBusinessRuleCheck = true;
        private boolean enableUniquenessCheck = true;
        
        // Getters and Setters
        public boolean isEnableStrictValidation() {
            return enableStrictValidation;
        }
        
        public void setEnableStrictValidation(boolean enableStrictValidation) {
            this.enableStrictValidation = enableStrictValidation;
        }
        
        public boolean isEnableSensitiveWordCheck() {
            return enableSensitiveWordCheck;
        }
        
        public void setEnableSensitiveWordCheck(boolean enableSensitiveWordCheck) {
            this.enableSensitiveWordCheck = enableSensitiveWordCheck;
        }
        
        public boolean isEnablePhoneCarrierCheck() {
            return enablePhoneCarrierCheck;
        }
        
        public void setEnablePhoneCarrierCheck(boolean enablePhoneCarrierCheck) {
            this.enablePhoneCarrierCheck = enablePhoneCarrierCheck;
        }
        
        public boolean isEnableEmailProviderCheck() {
            return enableEmailProviderCheck;
        }
        
        public void setEnableEmailProviderCheck(boolean enableEmailProviderCheck) {
            this.enableEmailProviderCheck = enableEmailProviderCheck;
        }
        
        public boolean isEnableTemporaryEmailCheck() {
            return enableTemporaryEmailCheck;
        }
        
        public void setEnableTemporaryEmailCheck(boolean enableTemporaryEmailCheck) {
            this.enableTemporaryEmailCheck = enableTemporaryEmailCheck;
        }
        
        public boolean isEnableDataIntegrityCheck() {
            return enableDataIntegrityCheck;
        }
        
        public void setEnableDataIntegrityCheck(boolean enableDataIntegrityCheck) {
            this.enableDataIntegrityCheck = enableDataIntegrityCheck;
        }
        
        public boolean isEnableBusinessRuleCheck() {
            return enableBusinessRuleCheck;
        }
        
        public void setEnableBusinessRuleCheck(boolean enableBusinessRuleCheck) {
            this.enableBusinessRuleCheck = enableBusinessRuleCheck;
        }
        
        public boolean isEnableUniquenessCheck() {
            return enableUniquenessCheck;
        }
        
        public void setEnableUniquenessCheck(boolean enableUniquenessCheck) {
            this.enableUniquenessCheck = enableUniquenessCheck;
        }
    }
    
    /**
     * 敏感词配置
     */
    public static class SensitiveWords {
        private List<String> words = List.of("测试", "admin", "test", "fuck", "shit");
        private boolean caseSensitive = false;
        
        public List<String> getWords() {
            return words;
        }
        
        public void setWords(List<String> words) {
            this.words = words;
        }
        
        public boolean isCaseSensitive() {
            return caseSensitive;
        }
        
        public void setCaseSensitive(boolean caseSensitive) {
            this.caseSensitive = caseSensitive;
        }
    }
    
    /**
     * 电话号码配置
     */
    public static class PhoneConfig {
        private List<String> virtualPrefixes = List.of("170", "171", "162", "165", "167");
        private List<String> testNumbers = List.of("13800138000", "13800138001", "18888888888");
        private boolean allowVirtualNumbers = true;
        private boolean allowTestNumbers = false;
        
        public List<String> getVirtualPrefixes() {
            return virtualPrefixes;
        }
        
        public void setVirtualPrefixes(List<String> virtualPrefixes) {
            this.virtualPrefixes = virtualPrefixes;
        }
        
        public List<String> getTestNumbers() {
            return testNumbers;
        }
        
        public void setTestNumbers(List<String> testNumbers) {
            this.testNumbers = testNumbers;
        }
        
        public boolean isAllowVirtualNumbers() {
            return allowVirtualNumbers;
        }
        
        public void setAllowVirtualNumbers(boolean allowVirtualNumbers) {
            this.allowVirtualNumbers = allowVirtualNumbers;
        }
        
        public boolean isAllowTestNumbers() {
            return allowTestNumbers;
        }
        
        public void setAllowTestNumbers(boolean allowTestNumbers) {
            this.allowTestNumbers = allowTestNumbers;
        }
    }
    
    /**
     * 邮箱配置
     */
    public static class EmailConfig {
        private List<String> temporaryDomains = List.of(
            "10minutemail.com", "guerrillamail.com", "tempmail.org", "mailinator.com"
        );
        private List<String> trustedDomains = List.of(
            "gmail.com", "yahoo.com", "hotmail.com", "outlook.com", "qq.com", "163.com"
        );
        private boolean allowTemporaryEmails = false;
        private boolean requireTrustedDomains = false;
        
        public List<String> getTemporaryDomains() {
            return temporaryDomains;
        }
        
        public void setTemporaryDomains(List<String> temporaryDomains) {
            this.temporaryDomains = temporaryDomains;
        }
        
        public List<String> getTrustedDomains() {
            return trustedDomains;
        }
        
        public void setTrustedDomains(List<String> trustedDomains) {
            this.trustedDomains = trustedDomains;
        }
        
        public boolean isAllowTemporaryEmails() {
            return allowTemporaryEmails;
        }
        
        public void setAllowTemporaryEmails(boolean allowTemporaryEmails) {
            this.allowTemporaryEmails = allowTemporaryEmails;
        }
        
        public boolean isRequireTrustedDomains() {
            return requireTrustedDomains;
        }
        
        public void setRequireTrustedDomains(boolean requireTrustedDomains) {
            this.requireTrustedDomains = requireTrustedDomains;
        }
    }
}