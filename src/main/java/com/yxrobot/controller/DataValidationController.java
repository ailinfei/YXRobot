package com.yxrobot.controller;

import com.yxrobot.validation.RealDataValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据验证控制器
 * 提供数据质量检查和验证功能
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2025-01-25
 */
@RestController
@RequestMapping("/api/admin/data-validation")
@CrossOrigin(origins = "*")
public class DataValidationController {
    
    private static final Logger logger = LoggerFactory.getLogger(DataValidationController.class);
    
    @Autowired
    private RealDataValidator realDataValidator;
    
    /**
     * 扫描数据库中的可疑数据
     * 
     * @return 扫描结果
     */
    @GetMapping("/scan-suspicious-data")
    public ResponseEntity<Map<String, Object>> scanSuspiciousData() {
        logger.info("开始扫描数据库中的可疑数据");
        
        try {
            RealDataValidator.DatabaseScanResult scanResult = realDataValidator.scanSuspiciousData();
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "数据扫描完成");
            response.put("data", scanResult);
            
            if (scanResult.isHasSuspiciousData()) {
                logger.warn("发现 {} 条可疑数据记录", scanResult.getTotalSuspiciousRecords());
                response.put("message", "发现可疑数据，请检查并清理");
            } else {
                logger.info("未发现可疑数据，数据质量良好");
                response.put("message", "数据质量检查通过，未发现可疑数据");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("扫描可疑数据时发生错误", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "扫描失败: " + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 验证客户数据是否为真实数据
     * 
     * @param customerData 客户数据
     * @return 验证结果
     */
    @PostMapping("/validate-customer-data")
    public ResponseEntity<Map<String, Object>> validateCustomerData(@RequestBody Map<String, String> customerData) {
        logger.info("验证客户数据: {}", customerData.get("customerName"));
        
        try {
            String customerName = customerData.get("customerName");
            String phone = customerData.get("phone");
            String email = customerData.get("email");
            String address = customerData.get("address");
            
            RealDataValidator.ValidationResult validationResult = 
                realDataValidator.validateCustomerData(customerName, phone, email, address);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("data", validationResult);
            
            if (validationResult.isValid()) {
                response.put("message", "数据验证通过");
                logger.info("客户数据验证通过: {}", customerName);
            } else {
                response.put("message", "数据验证失败，请检查数据质量");
                logger.warn("客户数据验证失败: {}, 错误: {}", customerName, validationResult.getErrors());
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("验证客户数据时发生错误", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "验证失败: " + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取数据质量报告
     * 
     * @return 数据质量报告
     */
    @GetMapping("/data-quality-report")
    public ResponseEntity<Map<String, Object>> getDataQualityReport() {
        logger.info("生成数据质量报告");
        
        try {
            RealDataValidator.DatabaseScanResult scanResult = realDataValidator.scanSuspiciousData();
            
            Map<String, Object> report = new HashMap<>();
            report.put("scanTime", System.currentTimeMillis());
            report.put("totalSuspiciousRecords", scanResult.getTotalSuspiciousRecords());
            report.put("hasSuspiciousData", scanResult.isHasSuspiciousData());
            report.put("suspiciousCustomersCount", 
                      scanResult.getSuspiciousCustomers() != null ? scanResult.getSuspiciousCustomers().size() : 0);
            report.put("suspiciousRentalCustomersCount", 
                      scanResult.getSuspiciousRentalCustomers() != null ? scanResult.getSuspiciousRentalCustomers().size() : 0);
            
            // 数据质量评分 (0-100)
            int qualityScore = scanResult.getTotalSuspiciousRecords() == 0 ? 100 : 
                              Math.max(0, 100 - scanResult.getTotalSuspiciousRecords() * 10);
            report.put("qualityScore", qualityScore);
            
            // 质量等级
            String qualityLevel;
            if (qualityScore >= 90) {
                qualityLevel = "优秀";
            } else if (qualityScore >= 70) {
                qualityLevel = "良好";
            } else if (qualityScore >= 50) {
                qualityLevel = "一般";
            } else {
                qualityLevel = "需要改进";
            }
            report.put("qualityLevel", qualityLevel);
            
            // 建议
            if (scanResult.isHasSuspiciousData()) {
                report.put("recommendations", new String[]{
                    "发现可疑数据，建议运行清理脚本 remove-mock-customer-data.sql",
                    "检查并删除测试/示例数据",
                    "确保所有客户数据都是真实有效的",
                    "建立数据录入规范，防止测试数据混入"
                });
            } else {
                report.put("recommendations", new String[]{
                    "数据质量良好，继续保持",
                    "定期运行数据质量检查",
                    "建立数据录入规范和审核流程"
                });
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "数据质量报告生成成功");
            response.put("data", report);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("生成数据质量报告时发生错误", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "生成报告失败: " + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 检查系统数据初始化状态
     * 
     * @return 初始化状态
     */
    @GetMapping("/initialization-status")
    public ResponseEntity<Map<String, Object>> getInitializationStatus() {
        logger.info("检查系统数据初始化状态");
        
        try {
            Map<String, Object> status = new HashMap<>();
            
            // 检查可疑数据
            RealDataValidator.DatabaseScanResult scanResult = realDataValidator.scanSuspiciousData();
            
            status.put("hasMockData", scanResult.isHasSuspiciousData());
            status.put("mockDataCount", scanResult.getTotalSuspiciousRecords());
            status.put("dataQualityPassed", !scanResult.isHasSuspiciousData());
            
            // 系统状态
            if (scanResult.isHasSuspiciousData()) {
                status.put("systemStatus", "需要清理");
                status.put("statusMessage", "检测到模拟数据，需要清理后才能正常使用");
                status.put("nextAction", "运行数据清理脚本");
            } else {
                status.put("systemStatus", "正常");
                status.put("statusMessage", "系统数据初始化正常，可以开始录入真实数据");
                status.put("nextAction", "通过管理后台录入客户数据");
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "初始化状态检查完成");
            response.put("data", status);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("检查初始化状态时发生错误", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "状态检查失败: " + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.status(500).body(response);
        }
    }
}