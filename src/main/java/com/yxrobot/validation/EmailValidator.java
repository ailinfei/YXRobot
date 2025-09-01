package com.yxrobot.validation;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 邮箱地址验证器
 * 提供详细的邮箱格式验证和域名检查
 */
@Component
public class EmailValidator {
    
    // 邮箱正则表达式
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
    );
    
    // 更严格的邮箱正则表达式
    private static final Pattern STRICT_EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$"
    );
    
    // 常见邮箱服务商
    private static final Set<String> COMMON_EMAIL_PROVIDERS = new HashSet<>(Arrays.asList(
        "gmail.com", "yahoo.com", "hotmail.com", "outlook.com", "live.com",
        "qq.com", "163.com", "126.com", "sina.com", "sohu.com",
        "foxmail.com", "aliyun.com", "yeah.net"
    ));
    
    // 企业邮箱常见域名
    private static final Set<String> ENTERPRISE_EMAIL_DOMAINS = new HashSet<>(Arrays.asList(
        "company.com", "corp.com", "enterprise.com", "business.com",
        "work.com", "office.com"
    ));
    
    // 临时邮箱域名
    private static final Set<String> TEMPORARY_EMAIL_DOMAINS = new HashSet<>(Arrays.asList(
        "10minutemail.com", "guerrillamail.com", "tempmail.org", "mailinator.com",
        "yopmail.com", "temp-mail.org", "throwaway.email", "maildrop.cc"
    ));
    
    // 教育机构邮箱域名后缀
    private static final Set<String> EDUCATION_EMAIL_SUFFIXES = new HashSet<>(Arrays.asList(
        ".edu", ".edu.cn", ".ac.cn", ".org.cn"
    ));
    
    /**
     * 验证邮箱地址
     */
    public EmailValidationResult validateEmail(String email) {
        EmailValidationResult result = new EmailValidationResult();
        
        if (email == null || email.trim().isEmpty()) {
            result.setValid(false);
            result.setErrorMessage("邮箱地址不能为空");
            return result;
        }
        
        // 清理邮箱地址
        String cleanEmail = email.trim().toLowerCase();
        result.setCleanedEmail(cleanEmail);
        
        // 基本格式验证
        if (!EMAIL_PATTERN.matcher(cleanEmail).matches()) {
            result.setValid(false);
            result.setErrorMessage("邮箱地址格式不正确");
            return result;
        }
        
        // 长度验证
        if (cleanEmail.length() > 254) {
            result.setValid(false);
            result.setErrorMessage("邮箱地址长度不能超过254个字符");
            return result;
        }
        
        // 分离用户名和域名
        String[] parts = cleanEmail.split("@");
        if (parts.length != 2) {
            result.setValid(false);
            result.setErrorMessage("邮箱地址格式不正确");
            return result;
        }
        
        String username = parts[0];
        String domain = parts[1];
        
        result.setUsername(username);
        result.setDomain(domain);
        
        // 验证用户名部分
        validateUsername(username, result);
        
        // 验证域名部分
        validateDomain(domain, result);
        
        // 如果基本验证通过，进行高级验证
        if (result.isValid()) {
            performAdvancedValidation(cleanEmail, result);
        }
        
        return result;
    }
    
    /**
     * 验证用户名部分
     */
    private void validateUsername(String username, EmailValidationResult result) {
        if (username.isEmpty()) {
            result.setValid(false);
            result.setErrorMessage("邮箱用户名不能为空");
            return;
        }
        
        if (username.length() > 64) {
            result.setValid(false);
            result.setErrorMessage("邮箱用户名长度不能超过64个字符");
            return;
        }
        
        // 检查用户名开头和结尾
        if (username.startsWith(".") || username.endsWith(".")) {
            result.setValid(false);
            result.setErrorMessage("邮箱用户名不能以点号开头或结尾");
            return;
        }
        
        // 检查连续的点号
        if (username.contains("..")) {
            result.setValid(false);
            result.setErrorMessage("邮箱用户名不能包含连续的点号");
            return;
        }
        
        // 检查特殊字符
        if (!username.matches("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+$")) {
            result.addWarning("邮箱用户名包含特殊字符，可能不被所有系统支持");
        }
        
        result.setValid(true);
    }
    
    /**
     * 验证域名部分
     */
    private void validateDomain(String domain, EmailValidationResult result) {
        if (domain.isEmpty()) {
            result.setValid(false);
            result.setErrorMessage("邮箱域名不能为空");
            return;
        }
        
        if (domain.length() > 253) {
            result.setValid(false);
            result.setErrorMessage("邮箱域名长度不能超过253个字符");
            return;
        }
        
        // 检查域名格式
        if (!domain.matches("^[a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(\\.[a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$")) {
            result.setValid(false);
            result.setErrorMessage("邮箱域名格式不正确");
            return;
        }
        
        // 检查顶级域名
        String[] domainParts = domain.split("\\.");
        if (domainParts.length < 2) {
            result.setValid(false);
            result.setErrorMessage("邮箱域名必须包含顶级域名");
            return;
        }
        
        String tld = domainParts[domainParts.length - 1];
        if (tld.length() < 2) {
            result.setValid(false);
            result.setErrorMessage("顶级域名长度至少为2个字符");
            return;
        }
        
        result.setValid(true);
    }
    
    /**
     * 执行高级验证
     */
    private void performAdvancedValidation(String email, EmailValidationResult result) {
        String domain = result.getDomain();
        
        // 识别邮箱类型
        identifyEmailType(domain, result);
        
        // 检查临时邮箱
        if (TEMPORARY_EMAIL_DOMAINS.contains(domain)) {
            result.setTemporary(true);
            result.addWarning("检测到临时邮箱，建议使用常用邮箱地址");
        }
        
        // 检查教育邮箱
        for (String suffix : EDUCATION_EMAIL_SUFFIXES) {
            if (domain.endsWith(suffix)) {
                result.setEducational(true);
                result.setEmailType("教育邮箱");
                break;
            }
        }
        
        // 检查可疑域名
        checkSuspiciousDomain(domain, result);
        
        // 检查国际化域名
        if (domain.contains("xn--")) {
            result.addWarning("检测到国际化域名，请确认域名正确性");
        }
        
        // 严格模式验证
        if (!STRICT_EMAIL_PATTERN.matcher(email).matches()) {
            result.addWarning("邮箱地址在严格模式下可能不被某些系统接受");
        }
    }
    
    /**
     * 识别邮箱类型
     */
    private void identifyEmailType(String domain, EmailValidationResult result) {
        if (COMMON_EMAIL_PROVIDERS.contains(domain)) {
            result.setEmailType("个人邮箱");
            result.setProvider(getProviderName(domain));
        } else if (ENTERPRISE_EMAIL_DOMAINS.contains(domain) || isLikelyEnterpriseDomain(domain)) {
            result.setEmailType("企业邮箱");
        } else {
            result.setEmailType("其他邮箱");
        }
    }
    
    /**
     * 获取邮箱服务商名称
     */
    private String getProviderName(String domain) {
        switch (domain) {
            case "gmail.com":
                return "Gmail";
            case "yahoo.com":
                return "Yahoo";
            case "hotmail.com":
            case "outlook.com":
            case "live.com":
                return "Microsoft";
            case "qq.com":
                return "腾讯QQ邮箱";
            case "163.com":
                return "网易163邮箱";
            case "126.com":
                return "网易126邮箱";
            case "sina.com":
                return "新浪邮箱";
            case "sohu.com":
                return "搜狐邮箱";
            case "foxmail.com":
                return "腾讯Foxmail";
            case "aliyun.com":
                return "阿里云邮箱";
            case "yeah.net":
                return "网易yeah邮箱";
            default:
                return "未知服务商";
        }
    }
    
    /**
     * 判断是否可能是企业域名
     */
    private boolean isLikelyEnterpriseDomain(String domain) {
        // 简单的企业域名判断逻辑
        return !COMMON_EMAIL_PROVIDERS.contains(domain) && 
               !TEMPORARY_EMAIL_DOMAINS.contains(domain) &&
               domain.split("\\.").length >= 2;
    }
    
    /**
     * 检查可疑域名
     */
    private void checkSuspiciousDomain(String domain, EmailValidationResult result) {
        // 检查域名长度
        if (domain.length() > 50) {
            result.addWarning("域名过长，请确认正确性");
        }
        
        // 检查数字域名
        if (domain.matches("^\\d+\\.\\d+\\.\\d+\\.\\d+$")) {
            result.addWarning("检测到IP地址作为域名，建议使用正式域名");
        }
        
        // 检查可疑字符
        if (domain.contains("--") || domain.contains("..")) {
            result.addWarning("域名包含可疑字符，请确认正确性");
        }
        
        // 检查新顶级域名
        String[] parts = domain.split("\\.");
        String tld = parts[parts.length - 1];
        if (tld.length() > 4 && !isCommonTLD(tld)) {
            result.addWarning("检测到不常见的顶级域名，请确认正确性");
        }
    }
    
    /**
     * 检查是否为常见顶级域名
     */
    private boolean isCommonTLD(String tld) {
        Set<String> commonTLDs = new HashSet<>(Arrays.asList(
            "com", "org", "net", "edu", "gov", "mil", "int",
            "cn", "com.cn", "org.cn", "net.cn", "edu.cn", "gov.cn"
        ));
        return commonTLDs.contains(tld);
    }
    
    /**
     * 邮箱验证结果类
     */
    public static class EmailValidationResult {
        private boolean valid;
        private String errorMessage;
        private String cleanedEmail;
        private String username;
        private String domain;
        private String emailType;
        private String provider;
        private boolean temporary;
        private boolean educational;
        private java.util.List<String> warnings = new java.util.ArrayList<>();
        
        // Getter和Setter方法
        public boolean isValid() {
            return valid;
        }
        
        public void setValid(boolean valid) {
            this.valid = valid;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
        
        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
        
        public String getCleanedEmail() {
            return cleanedEmail;
        }
        
        public void setCleanedEmail(String cleanedEmail) {
            this.cleanedEmail = cleanedEmail;
        }
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getDomain() {
            return domain;
        }
        
        public void setDomain(String domain) {
            this.domain = domain;
        }
        
        public String getEmailType() {
            return emailType;
        }
        
        public void setEmailType(String emailType) {
            this.emailType = emailType;
        }
        
        public String getProvider() {
            return provider;
        }
        
        public void setProvider(String provider) {
            this.provider = provider;
        }
        
        public boolean isTemporary() {
            return temporary;
        }
        
        public void setTemporary(boolean temporary) {
            this.temporary = temporary;
        }
        
        public boolean isEducational() {
            return educational;
        }
        
        public void setEducational(boolean educational) {
            this.educational = educational;
        }
        
        public java.util.List<String> getWarnings() {
            return warnings;
        }
        
        public void addWarning(String warning) {
            this.warnings.add(warning);
        }
        
        public boolean hasWarnings() {
            return !warnings.isEmpty();
        }
    }
}