package com.yxrobot.validation;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 电话号码验证器
 * 提供详细的电话号码格式验证和运营商识别
 */
@Component
public class PhoneNumberValidator {
    
    // 手机号码正则表达式
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    
    // 固定电话正则表达式
    private static final Pattern LANDLINE_PATTERN = Pattern.compile("^0\\d{2,3}-?\\d{7,8}$");
    
    // 400电话正则表达式
    private static final Pattern SERVICE_400_PATTERN = Pattern.compile("^400-?\\d{3}-?\\d{4}$");
    
    // 运营商号段映射
    private static final Map<String, String> CARRIER_MAP = new HashMap<>();
    
    static {
        // 中国移动
        CARRIER_MAP.put("134", "中国移动");
        CARRIER_MAP.put("135", "中国移动");
        CARRIER_MAP.put("136", "中国移动");
        CARRIER_MAP.put("137", "中国移动");
        CARRIER_MAP.put("138", "中国移动");
        CARRIER_MAP.put("139", "中国移动");
        CARRIER_MAP.put("147", "中国移动");
        CARRIER_MAP.put("150", "中国移动");
        CARRIER_MAP.put("151", "中国移动");
        CARRIER_MAP.put("152", "中国移动");
        CARRIER_MAP.put("157", "中国移动");
        CARRIER_MAP.put("158", "中国移动");
        CARRIER_MAP.put("159", "中国移动");
        CARRIER_MAP.put("178", "中国移动");
        CARRIER_MAP.put("182", "中国移动");
        CARRIER_MAP.put("183", "中国移动");
        CARRIER_MAP.put("184", "中国移动");
        CARRIER_MAP.put("187", "中国移动");
        CARRIER_MAP.put("188", "中国移动");
        CARRIER_MAP.put("198", "中国移动");
        
        // 中国联通
        CARRIER_MAP.put("130", "中国联通");
        CARRIER_MAP.put("131", "中国联通");
        CARRIER_MAP.put("132", "中国联通");
        CARRIER_MAP.put("145", "中国联通");
        CARRIER_MAP.put("155", "中国联通");
        CARRIER_MAP.put("156", "中国联通");
        CARRIER_MAP.put("166", "中国联通");
        CARRIER_MAP.put("175", "中国联通");
        CARRIER_MAP.put("176", "中国联通");
        CARRIER_MAP.put("185", "中国联通");
        CARRIER_MAP.put("186", "中国联通");
        
        // 中国电信
        CARRIER_MAP.put("133", "中国电信");
        CARRIER_MAP.put("149", "中国电信");
        CARRIER_MAP.put("153", "中国电信");
        CARRIER_MAP.put("173", "中国电信");
        CARRIER_MAP.put("177", "中国电信");
        CARRIER_MAP.put("180", "中国电信");
        CARRIER_MAP.put("181", "中国电信");
        CARRIER_MAP.put("189", "中国电信");
        CARRIER_MAP.put("199", "中国电信");
        
        // 虚拟运营商
        CARRIER_MAP.put("170", "虚拟运营商");
        CARRIER_MAP.put("171", "虚拟运营商");
        CARRIER_MAP.put("162", "虚拟运营商");
        CARRIER_MAP.put("165", "虚拟运营商");
        CARRIER_MAP.put("167", "虚拟运营商");
    }
    
    /**
     * 验证电话号码
     */
    public PhoneValidationResult validatePhone(String phone) {
        PhoneValidationResult result = new PhoneValidationResult();
        
        if (phone == null || phone.trim().isEmpty()) {
            result.setValid(false);
            result.setErrorMessage("电话号码不能为空");
            return result;
        }
        
        // 清理电话号码（移除空格、横线等）
        String cleanPhone = cleanPhoneNumber(phone);
        result.setCleanedPhone(cleanPhone);
        
        // 判断电话号码类型并验证
        if (isMobileNumber(cleanPhone)) {
            validateMobileNumber(cleanPhone, result);
        } else if (isLandlineNumber(cleanPhone)) {
            validateLandlineNumber(cleanPhone, result);
        } else if (isService400Number(cleanPhone)) {
            validateService400Number(cleanPhone, result);
        } else {
            result.setValid(false);
            result.setErrorMessage("不支持的电话号码格式");
        }
        
        return result;
    }
    
    /**
     * 验证手机号码
     */
    private void validateMobileNumber(String phone, PhoneValidationResult result) {
        if (!MOBILE_PATTERN.matcher(phone).matches()) {
            result.setValid(false);
            result.setErrorMessage("手机号码格式不正确，请输入11位手机号");
            return;
        }
        
        result.setValid(true);
        result.setPhoneType("手机号码");
        
        // 识别运营商
        String prefix = phone.substring(0, 3);
        String carrier = CARRIER_MAP.get(prefix);
        if (carrier != null) {
            result.setCarrier(carrier);
            
            // 虚拟运营商提示
            if ("虚拟运营商".equals(carrier)) {
                result.addWarning("检测到虚拟运营商号码，请确认号码有效性");
            }
        } else {
            result.setCarrier("未知运营商");
            result.addWarning("未识别的号码段，请确认号码正确性");
        }
        
        // 检查特殊号码段
        checkSpecialNumberSegments(phone, result);
    }
    
    /**
     * 验证固定电话
     */
    private void validateLandlineNumber(String phone, PhoneValidationResult result) {
        if (!LANDLINE_PATTERN.matcher(phone).matches()) {
            result.setValid(false);
            result.setErrorMessage("固定电话格式不正确");
            return;
        }
        
        result.setValid(true);
        result.setPhoneType("固定电话");
        
        // 识别区号
        String areaCode = extractAreaCode(phone);
        if (areaCode != null) {
            result.setAreaCode(areaCode);
            String city = getAreaCodeCity(areaCode);
            if (city != null) {
                result.setCity(city);
            }
        }
    }
    
    /**
     * 验证400电话
     */
    private void validateService400Number(String phone, PhoneValidationResult result) {
        if (!SERVICE_400_PATTERN.matcher(phone).matches()) {
            result.setValid(false);
            result.setErrorMessage("400电话格式不正确");
            return;
        }
        
        result.setValid(true);
        result.setPhoneType("400服务电话");
        result.addWarning("400电话为企业服务电话，请确认用途");
    }
    
    /**
     * 清理电话号码
     */
    private String cleanPhoneNumber(String phone) {
        return phone.replaceAll("[\\s-()]", "");
    }
    
    /**
     * 判断是否为手机号码
     */
    private boolean isMobileNumber(String phone) {
        return phone.length() == 11 && phone.startsWith("1");
    }
    
    /**
     * 判断是否为固定电话
     */
    private boolean isLandlineNumber(String phone) {
        return phone.startsWith("0") && phone.length() >= 10 && phone.length() <= 12;
    }
    
    /**
     * 判断是否为400电话
     */
    private boolean isService400Number(String phone) {
        return phone.startsWith("400") && phone.length() >= 10;
    }
    
    /**
     * 检查特殊号码段
     */
    private void checkSpecialNumberSegments(String phone, PhoneValidationResult result) {
        // 检查连号
        if (hasConsecutiveDigits(phone, 4)) {
            result.addWarning("号码包含连续数字，请确认正确性");
        }
        
        // 检查重复数字
        if (hasRepeatingDigits(phone, 5)) {
            result.addWarning("号码包含大量重复数字，请确认正确性");
        }
        
        // 检查测试号码
        if (isTestNumber(phone)) {
            result.addWarning("检测到测试号码，请使用真实号码");
        }
    }
    
    /**
     * 检查是否有连续数字
     */
    private boolean hasConsecutiveDigits(String phone, int count) {
        for (int i = 0; i <= phone.length() - count; i++) {
            boolean consecutive = true;
            for (int j = 1; j < count; j++) {
                if (phone.charAt(i + j) != phone.charAt(i + j - 1) + 1) {
                    consecutive = false;
                    break;
                }
            }
            if (consecutive) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 检查是否有重复数字
     */
    private boolean hasRepeatingDigits(String phone, int count) {
        for (int i = 0; i <= phone.length() - count; i++) {
            boolean repeating = true;
            char digit = phone.charAt(i);
            for (int j = 1; j < count; j++) {
                if (phone.charAt(i + j) != digit) {
                    repeating = false;
                    break;
                }
            }
            if (repeating) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 检查是否为测试号码
     */
    private boolean isTestNumber(String phone) {
        String[] testNumbers = {
            "13800138000", "13800138001", "13800138002",
            "18888888888", "16666666666", "19999999999"
        };
        
        for (String testNumber : testNumbers) {
            if (phone.equals(testNumber)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 提取区号
     */
    private String extractAreaCode(String phone) {
        if (phone.startsWith("0")) {
            if (phone.length() == 11) {
                return phone.substring(0, 3); // 3位区号
            } else if (phone.length() == 12) {
                return phone.substring(0, 4); // 4位区号
            }
        }
        return null;
    }
    
    /**
     * 根据区号获取城市
     */
    private String getAreaCodeCity(String areaCode) {
        Map<String, String> areaCodes = new HashMap<>();
        areaCodes.put("010", "北京");
        areaCodes.put("021", "上海");
        areaCodes.put("022", "天津");
        areaCodes.put("023", "重庆");
        areaCodes.put("025", "南京");
        areaCodes.put("027", "武汉");
        areaCodes.put("028", "成都");
        areaCodes.put("029", "西安");
        // 可以添加更多区号映射
        
        return areaCodes.get(areaCode);
    }
    
    /**
     * 电话验证结果类
     */
    public static class PhoneValidationResult {
        private boolean valid;
        private String errorMessage;
        private String cleanedPhone;
        private String phoneType;
        private String carrier;
        private String areaCode;
        private String city;
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
        
        public String getCleanedPhone() {
            return cleanedPhone;
        }
        
        public void setCleanedPhone(String cleanedPhone) {
            this.cleanedPhone = cleanedPhone;
        }
        
        public String getPhoneType() {
            return phoneType;
        }
        
        public void setPhoneType(String phoneType) {
            this.phoneType = phoneType;
        }
        
        public String getCarrier() {
            return carrier;
        }
        
        public void setCarrier(String carrier) {
            this.carrier = carrier;
        }
        
        public String getAreaCode() {
            return areaCode;
        }
        
        public void setAreaCode(String areaCode) {
            this.areaCode = areaCode;
        }
        
        public String getCity() {
            return city;
        }
        
        public void setCity(String city) {
            this.city = city;
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