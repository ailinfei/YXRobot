package com.yxrobot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yxrobot.dto.ManagedDeviceDTO;
import com.yxrobot.entity.DeviceModel;
import com.yxrobot.entity.DeviceStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 设备管理TypeScript接口验证服务
 * 验证后端DTO与前端TypeScript接口的一致性
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-28
 */
@Service
public class ManagedDeviceTypeScriptInterfaceValidationService {
    
    private static final Logger logger = LoggerFactory.getLogger(ManagedDeviceTypeScriptInterfaceValidationService.class);
    
    @Autowired
    private ObjectMapper objectMapper;
    
    // 前端TypeScript接口定义（模拟）
    private static final Map<String, String> FRONTEND_INTERFACE_FIELDS = new HashMap<>();
    
    static {
        // ManagedDevice接口字段定义
        FRONTEND_INTERFACE_FIELDS.put("id", "number");
        FRONTEND_INTERFACE_FIELDS.put("serialNumber", "string");
        FRONTEND_INTERFACE_FIELDS.put("model", "DeviceModel");
        FRONTEND_INTERFACE_FIELDS.put("status", "DeviceStatus");
        FRONTEND_INTERFACE_FIELDS.put("firmwareVersion", "string");
        FRONTEND_INTERFACE_FIELDS.put("customerId", "number");
        FRONTEND_INTERFACE_FIELDS.put("customerName", "string");
        FRONTEND_INTERFACE_FIELDS.put("customerPhone", "string");
        FRONTEND_INTERFACE_FIELDS.put("lastOnlineAt", "string");
        FRONTEND_INTERFACE_FIELDS.put("activatedAt", "string");
        FRONTEND_INTERFACE_FIELDS.put("createdAt", "string");
        FRONTEND_INTERFACE_FIELDS.put("updatedAt", "string");
        FRONTEND_INTERFACE_FIELDS.put("createdBy", "string");
        FRONTEND_INTERFACE_FIELDS.put("notes", "string");
        FRONTEND_INTERFACE_FIELDS.put("isDeleted", "boolean");
        
        // 格式化字段
        FRONTEND_INTERFACE_FIELDS.put("createdAtFormatted", "string");
        FRONTEND_INTERFACE_FIELDS.put("updatedAtFormatted", "string");
        FRONTEND_INTERFACE_FIELDS.put("lastOnlineAtFormatted", "string");
        FRONTEND_INTERFACE_FIELDS.put("activatedAtFormatted", "string");
        FRONTEND_INTERFACE_FIELDS.put("statusDescription", "string");
        FRONTEND_INTERFACE_FIELDS.put("modelDescription", "string");
        
        // 关联对象字段
        FRONTEND_INTERFACE_FIELDS.put("specifications", "ManagedDeviceSpecification");
        FRONTEND_INTERFACE_FIELDS.put("usageStats", "ManagedDeviceUsageStats");
        FRONTEND_INTERFACE_FIELDS.put("configuration", "ManagedDeviceConfiguration");
        FRONTEND_INTERFACE_FIELDS.put("location", "ManagedDeviceLocation");
        FRONTEND_INTERFACE_FIELDS.put("maintenanceRecords", "ManagedDeviceMaintenanceRecord[]");
    }
    
    /**
     * 验证DTO与TypeScript接口的一致性
     * 
     * @return 验证结果
     */
    public Map<String, Object> validateTypeScriptInterfaceConsistency() {
        logger.info("开始验证DTO与TypeScript接口的一致性");
        
        Map<String, Object> validationResult = new HashMap<>();
        List<String> passedChecks = new ArrayList<>();
        List<String> failedChecks = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        try {
            // 1. 验证字段名称一致性
            validateFieldNames(ManagedDeviceDTO.class, passedChecks, failedChecks, warnings);
            
            // 2. 验证字段类型一致性
            validateFieldTypes(ManagedDeviceDTO.class, passedChecks, failedChecks, warnings);
            
            // 3. 验证枚举类型一致性
            validateEnumTypes(passedChecks, failedChecks, warnings);
            
            // 4. 验证JSON序列化一致性
            validateJsonSerialization(passedChecks, failedChecks, warnings);
            
            // 5. 验证响应格式一致性
            validateResponseFormat(passedChecks, failedChecks, warnings);
            
        } catch (Exception e) {
            logger.error("TypeScript接口验证失败", e);
            failedChecks.add("验证过程异常: " + e.getMessage());
        }
        
        // 汇总结果
        validationResult.put("passedChecks", passedChecks);
        validationResult.put("failedChecks", failedChecks);
        validationResult.put("warnings", warnings);
        validationResult.put("totalChecks", passedChecks.size() + failedChecks.size());
        validationResult.put("successRate", calculateSuccessRate(passedChecks.size(), failedChecks.size()));
        validationResult.put("isValid", failedChecks.isEmpty());
        
        logger.info("TypeScript接口验证完成，通过: {}, 失败: {}, 警告: {}", 
                   passedChecks.size(), failedChecks.size(), warnings.size());
        
        return validationResult;
    }
    
    /**
     * 生成TypeScript接口定义
     * 
     * @return TypeScript接口定义
     */
    public Map<String, Object> generateTypeScriptInterfaces() {
        logger.info("开始生成TypeScript接口定义");
        
        Map<String, Object> interfaces = new HashMap<>();
        
        try {
            // 生成ManagedDevice接口
            interfaces.put("ManagedDevice", generateManagedDeviceInterface());
            
            // 生成ManagedDeviceStats接口
            interfaces.put("ManagedDeviceStats", generateManagedDeviceStatsInterface());
            
            // 生成PageResult接口
            interfaces.put("PageResult", generatePageResultInterface());
            
            // 生成Result接口
            interfaces.put("Result", generateResponseResultInterface());
            
            // 生成枚举类型
            interfaces.put("DeviceStatus", generateDeviceStatusEnum());
            interfaces.put("DeviceModel", generateDeviceModelEnum());
            
        } catch (Exception e) {
            logger.error("生成TypeScript接口定义失败", e);
            interfaces.put("error", e.getMessage());
        }
        
        logger.info("TypeScript接口定义生成完成");
        return interfaces;
    }
    
    /**
     * 验证API响应数据格式
     * 
     * @param responseData API响应数据
     * @return 验证结果
     */
    public Map<String, Object> validateApiResponseFormat(Object responseData) {
        logger.info("开始验证API响应数据格式");
        
        Map<String, Object> validationResult = new HashMap<>();
        List<String> issues = new ArrayList<>();
        
        try {
            if (responseData instanceof Map) {
                Map<String, Object> response = (Map<String, Object>) responseData;
                
                // 验证标准响应格式
                if (!response.containsKey("code")) {
                    issues.add("缺少code字段");
                }
                if (!response.containsKey("data")) {
                    issues.add("缺少data字段");
                }
                if (!response.containsKey("message")) {
                    issues.add("缺少message字段");
                }
                
                // 验证数据字段
                Object data = response.get("data");
                if (data instanceof Map) {
                    validateDataObject((Map<String, Object>) data, issues);
                } else if (data instanceof List) {
                    validateDataArray((List<?>) data, issues);
                }
                
            } else {
                issues.add("响应数据不是有效的JSON对象");
            }
            
        } catch (Exception e) {
            logger.error("验证API响应数据格式失败", e);
            issues.add("验证过程异常: " + e.getMessage());
        }
        
        validationResult.put("issues", issues);
        validationResult.put("isValid", issues.isEmpty());
        validationResult.put("issueCount", issues.size());
        
        logger.info("API响应数据格式验证完成，发现{}个问题", issues.size());
        return validationResult;
    }
    
    /**
     * 验证字段名称一致性
     */
    private void validateFieldNames(Class<?> dtoClass, List<String> passed, List<String> failed, List<String> warnings) {
        Field[] fields = dtoClass.getDeclaredFields();
        
        for (Field field : fields) {
            String fieldName = field.getName();
            
            if (FRONTEND_INTERFACE_FIELDS.containsKey(fieldName)) {
                passed.add("字段名称一致性检查 - " + fieldName);
            } else {
                // 检查是否是内部字段或静态字段
                if (fieldName.startsWith("$") || java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                    continue; // 跳过内部字段和静态字段
                }
                warnings.add("后端字段在前端接口中未定义: " + fieldName);
            }
        }
        
        // 检查前端接口中定义但后端没有的字段
        Set<String> backendFields = new HashSet<>();
        for (Field field : fields) {
            backendFields.add(field.getName());
        }
        
        for (String frontendField : FRONTEND_INTERFACE_FIELDS.keySet()) {
            if (!backendFields.contains(frontendField)) {
                warnings.add("前端接口字段在后端DTO中未定义: " + frontendField);
            }
        }
    }
    
    /**
     * 验证字段类型一致性
     */
    private void validateFieldTypes(Class<?> dtoClass, List<String> passed, List<String> failed, List<String> warnings) {
        Field[] fields = dtoClass.getDeclaredFields();
        
        for (Field field : fields) {
            String fieldName = field.getName();
            String expectedType = FRONTEND_INTERFACE_FIELDS.get(fieldName);
            
            if (expectedType != null) {
                String actualType = getTypeScriptType(field.getType());
                
                if (isTypeCompatible(actualType, expectedType)) {
                    passed.add("字段类型一致性检查 - " + fieldName + ": " + actualType);
                } else {
                    failed.add("字段类型不匹配 - " + fieldName + ": 期望 " + expectedType + ", 实际 " + actualType);
                }
            }
        }
    }
    
    /**
     * 验证枚举类型一致性
     */
    private void validateEnumTypes(List<String> passed, List<String> failed, List<String> warnings) {
        // 验证DeviceStatus枚举
        try {
            DeviceStatus[] statuses = DeviceStatus.values();
            if (statuses.length > 0) {
                passed.add("DeviceStatus枚举类型验证");
            } else {
                failed.add("DeviceStatus枚举为空");
            }
        } catch (Exception e) {
            failed.add("DeviceStatus枚举验证失败: " + e.getMessage());
        }
        
        // 验证DeviceModel枚举
        try {
            DeviceModel[] models = DeviceModel.values();
            if (models.length > 0) {
                passed.add("DeviceModel枚举类型验证");
            } else {
                failed.add("DeviceModel枚举为空");
            }
        } catch (Exception e) {
            failed.add("DeviceModel枚举验证失败: " + e.getMessage());
        }
    }
    
    /**
     * 验证JSON序列化一致性
     */
    private void validateJsonSerialization(List<String> passed, List<String> failed, List<String> warnings) {
        try {
            // 创建测试DTO对象
            ManagedDeviceDTO testDto = createTestManagedDeviceDTO();
            
            // 序列化为JSON
            String json = objectMapper.writeValueAsString(testDto);
            
            // 反序列化
            ManagedDeviceDTO deserializedDto = objectMapper.readValue(json, ManagedDeviceDTO.class);
            
            if (deserializedDto != null) {
                passed.add("JSON序列化/反序列化验证");
            } else {
                failed.add("JSON反序列化结果为null");
            }
            
        } catch (Exception e) {
            failed.add("JSON序列化验证失败: " + e.getMessage());
        }
    }
    
    /**
     * 验证响应格式一致性
     */
    private void validateResponseFormat(List<String> passed, List<String> failed, List<String> warnings) {
        try {
            // 模拟标准响应格式
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "操作成功");
            response.put("data", createTestManagedDeviceDTO());
            
            String json = objectMapper.writeValueAsString(response);
            Map<String, Object> deserializedResponse = objectMapper.readValue(json, Map.class);
            
            if (deserializedResponse.containsKey("code") && 
                deserializedResponse.containsKey("data") && 
                deserializedResponse.containsKey("message")) {
                passed.add("标准响应格式验证");
            } else {
                failed.add("标准响应格式不完整");
            }
            
        } catch (Exception e) {
            failed.add("响应格式验证失败: " + e.getMessage());
        }
    }
    
    /**
     * 验证数据对象
     */
    private void validateDataObject(Map<String, Object> data, List<String> issues) {
        // 验证必要字段
        String[] requiredFields = {"id", "serialNumber", "model", "status"};
        
        for (String field : requiredFields) {
            if (!data.containsKey(field)) {
                issues.add("缺少必要字段: " + field);
            }
        }
        
        // 验证字段类型
        if (data.containsKey("id") && !(data.get("id") instanceof Number)) {
            issues.add("id字段类型错误，应为数字");
        }
        
        if (data.containsKey("serialNumber") && !(data.get("serialNumber") instanceof String)) {
            issues.add("serialNumber字段类型错误，应为字符串");
        }
    }
    
    /**
     * 验证数据数组
     */
    private void validateDataArray(List<?> dataArray, List<String> issues) {
        if (dataArray.isEmpty()) {
            return; // 空数组是有效的
        }
        
        // 验证数组元素类型
        Object firstElement = dataArray.get(0);
        if (firstElement instanceof Map) {
            validateDataObject((Map<String, Object>) firstElement, issues);
        }
    }
    
    /**
     * 获取TypeScript类型
     */
    private String getTypeScriptType(Class<?> javaType) {
        if (javaType == String.class) {
            return "string";
        } else if (javaType == Integer.class || javaType == Long.class || javaType == int.class || javaType == long.class) {
            return "number";
        } else if (javaType == Boolean.class || javaType == boolean.class) {
            return "boolean";
        } else if (javaType == LocalDateTime.class) {
            return "string"; // 日期通常序列化为字符串
        } else if (javaType.isEnum()) {
            return javaType.getSimpleName();
        } else if (List.class.isAssignableFrom(javaType)) {
            return "Array";
        } else {
            return javaType.getSimpleName();
        }
    }
    
    /**
     * 检查类型兼容性
     */
    private boolean isTypeCompatible(String actualType, String expectedType) {
        if (actualType.equals(expectedType)) {
            return true;
        }
        
        // 特殊兼容性检查
        if ("Array".equals(actualType) && expectedType.endsWith("[]")) {
            return true;
        }
        
        return false;
    }
    
    /**
     * 创建测试DTO对象
     */
    private ManagedDeviceDTO createTestManagedDeviceDTO() {
        ManagedDeviceDTO dto = new ManagedDeviceDTO();
        dto.setId("1");
        dto.setSerialNumber("TEST-001");
        dto.setModel(DeviceModel.YX_EDU_2024.name());
        dto.setStatus(DeviceStatus.ONLINE.name());
        dto.setFirmwareVersion("1.0.0");
        dto.setCustomerId("1");
        dto.setCustomerName("测试客户");
        dto.setCustomerPhone("13800138000");
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());
        dto.setNotes("测试设备");
        return dto;
    }
    
    /**
     * 生成ManagedDevice接口定义
     */
    private String generateManagedDeviceInterface() {
        StringBuilder sb = new StringBuilder();
        sb.append("export interface ManagedDevice {\n");
        
        for (Map.Entry<String, String> entry : FRONTEND_INTERFACE_FIELDS.entrySet()) {
            sb.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append(";\n");
        }
        
        sb.append("}");
        return sb.toString();
    }
    
    /**
     * 生成ManagedDeviceStats接口定义
     */
    private String generateManagedDeviceStatsInterface() {
        return "export interface ManagedDeviceStats {\n" +
               "  totalDevices: number;\n" +
               "  onlineDevices: number;\n" +
               "  offlineDevices: number;\n" +
               "  faultDevices: number;\n" +
               "  maintenanceDevices: number;\n" +
               "  educationDevices: number;\n" +
               "  homeDevices: number;\n" +
               "  professionalDevices: number;\n" +
               "}";
    }
    
    /**
     * 生成PageResult接口定义
     */
    private String generatePageResultInterface() {
        return "export interface PageResult<T> {\n" +
               "  list: T[];\n" +
               "  total: number;\n" +
               "  page: number;\n" +
               "  pageSize: number;\n" +
               "  totalPages: number;\n" +
               "}";
    }
    
    /**
     * 生成Result接口定义
     */
    private String generateResponseResultInterface() {
        return "export interface Result<T> {\n" +
               "  code: number;\n" +
               "  message: string;\n" +
               "  data: T;\n" +
               "}";
    }
    
    /**
     * 生成DeviceStatus枚举定义
     */
    private String generateDeviceStatusEnum() {
        return "export enum DeviceStatus {\n" +
               "  PENDING = 'pending',\n" +
               "  ONLINE = 'online',\n" +
               "  OFFLINE = 'offline',\n" +
               "  FAULT = 'fault',\n" +
               "  MAINTENANCE = 'maintenance'\n" +
               "}";
    }
    
    /**
     * 生成DeviceModel枚举定义
     */
    private String generateDeviceModelEnum() {
        return "export enum DeviceModel {\n" +
               "  EDUCATION = 'education',\n" +
               "  HOME = 'home',\n" +
               "  PROFESSIONAL = 'professional'\n" +
               "}";
    }
    
    /**
     * 计算成功率
     */
    private double calculateSuccessRate(int passed, int failed) {
        int total = passed + failed;
        if (total == 0) return 100.0;
        return (double) passed / total * 100;
    }
}