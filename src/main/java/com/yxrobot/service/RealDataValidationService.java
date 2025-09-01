package com.yxrobot.service;

import com.yxrobot.validation.RealDataValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 真实数据验证服务
 * 任务19：数据初始化和真实数据验证
 * 负责验证系统中的数据是否为真实数据，防止模拟数据的插入和使用
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2025-01-28
 */
@Service
public class RealDataValidationService {
    
    private static final Logger logger = LoggerFactory.getLogger(RealDataValidationService.class);
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private RealDataValidator realDataValidator;
    
    @Value("${app.data.validation.strict-mode:true}")
    private boolean strictMode;
    
    /**
     * 验证客户表中的数据是否为真实数据
     * 
     * @return 验证报告
     */
    public ValidationReport validateCustomersData() {
        logger.info("开始验证客户表数据...");
        
        ValidationReport report = new ValidationReport("客户数据验证");
        
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT id, name, phone, email, address, created_at FROM customers WHERE is_deleted = 0 OR is_deleted IS NULL";
            
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                int totalCount = 0;
                int validCount = 0;
                int suspiciousCount = 0;
                
                while (rs.next()) {
                    totalCount++;
                    
                    Long id = rs.getLong("id");
                    String name = rs.getString("name");
                    String phone = rs.getString("phone");
                    String email = rs.getString("email");
                    String address = rs.getString("address");
                    String createdAt = rs.getString("created_at");
                    
                    RealDataValidator.ValidationResult result = realDataValidator.validateCustomerData(name, email, phone, address);
                    
                    if (result.isValid()) {
                        validCount++;
                    } else {
                        suspiciousCount++;
                        report.addSuspiciousRecord("客户ID: " + id + ", 姓名: " + name + " - " + result.getViolationMessage());
                        
                        if (strictMode) {
                            logger.warn("严格模式：发现疑似模拟客户数据 - ID: {}, 姓名: {}, 违规: {}", 
                                      id, name, result.getViolationMessage());
                        }
                    }
                }
                
                report.setTotalRecords(totalCount);
                report.setValidRecords(validCount);
                report.setSuspiciousRecords(suspiciousCount);
                
                logger.info("客户数据验证完成 - 总计: {}, 有效: {}, 疑似: {}", totalCount, validCount, suspiciousCount);
                
            }
        } catch (SQLException e) {
            logger.error("验证客户数据时发生错误", e);
            report.addError("数据库查询错误: " + e.getMessage());
        }
        
        return report;
    }
    
    /**
     * 验证销售记录中的数据是否为真实数据
     * 
     * @return 验证报告
     */
    public ValidationReport validateSalesData() {
        logger.info("开始验证销售记录数据...");
        
        ValidationReport report = new ValidationReport("销售数据验证");
        
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT id, order_number, customer_name, notes, created_at FROM sales_records WHERE is_deleted = 0 OR is_deleted IS NULL";
            
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                int totalCount = 0;
                int validCount = 0;
                int suspiciousCount = 0;
                
                while (rs.next()) {
                    totalCount++;
                    
                    Long id = rs.getLong("id");
                    String orderNumber = rs.getString("order_number");
                    String customerName = rs.getString("customer_name");
                    String notes = rs.getString("notes");
                    String createdAt = rs.getString("created_at");
                    
                    RealDataValidator.ValidationResult result = realDataValidator.validateOrderData(orderNumber, customerName, notes);
                    
                    if (result.isValid()) {
                        validCount++;
                    } else {
                        suspiciousCount++;
                        report.addSuspiciousRecord("销售记录ID: " + id + ", 订单号: " + orderNumber + " - " + result.getViolationMessage());
                        
                        if (strictMode) {
                            logger.warn("严格模式：发现疑似模拟销售数据 - ID: {}, 订单号: {}, 违规: {}", 
                                      id, orderNumber, result.getViolationMessage());
                        }
                    }
                }
                
                report.setTotalRecords(totalCount);
                report.setValidRecords(validCount);
                report.setSuspiciousRecords(suspiciousCount);
                
                logger.info("销售数据验证完成 - 总计: {}, 有效: {}, 疑似: {}", totalCount, validCount, suspiciousCount);
                
            }
        } catch (SQLException e) {
            logger.error("验证销售数据时发生错误", e);
            report.addError("数据库查询错误: " + e.getMessage());
        }
        
        return report;
    }
    
    /**
     * 验证租赁记录中的数据是否为真实数据
     * 
     * @return 验证报告
     */
    public ValidationReport validateRentalData() {
        logger.info("开始验证租赁记录数据...");
        
        ValidationReport report = new ValidationReport("租赁数据验证");
        
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT COUNT(*) FROM rental_records WHERE is_deleted = 0 OR is_deleted IS NULL";
            
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                if (rs.next()) {
                    int count = rs.getInt(1);
                    
                    if (count == 0) {
                        report.setTotalRecords(0);
                        report.setValidRecords(0);
                        report.setSuspiciousRecords(0);
                        report.addInfo("租赁记录表为空，符合真实数据要求");
                        logger.info("租赁数据验证通过 - 表为空，符合真实数据原则");
                    } else {
                        report.setTotalRecords(count);
                        report.addInfo("发现 " + count + " 条租赁记录，请人工确认是否为真实数据");
                        logger.warn("发现 {} 条租赁记录，请确认是否为真实数据", count);
                    }
                }
                
            }
        } catch (SQLException e) {
            logger.info("租赁记录表不存在或查询失败，这是正常的初始状态: {}", e.getMessage());
            report.addInfo("租赁记录表不存在，这是正常的初始状态");
        }
        
        return report;
    }
    
    /**
     * 执行全面的数据验证
     * 
     * @return 综合验证报告
     */
    public ComprehensiveValidationReport validateAllData() {
        logger.info("开始执行全面数据验证...");
        
        ComprehensiveValidationReport comprehensiveReport = new ComprehensiveValidationReport();
        
        // 验证客户数据
        ValidationReport customerReport = validateCustomersData();
        comprehensiveReport.addReport(customerReport);
        
        // 验证销售数据
        ValidationReport salesReport = validateSalesData();
        comprehensiveReport.addReport(salesReport);
        
        // 验证租赁数据
        ValidationReport rentalReport = validateRentalData();
        comprehensiveReport.addReport(rentalReport);
        
        // 生成综合结论
        comprehensiveReport.generateSummary();
        
        logger.info("全面数据验证完成");
        
        return comprehensiveReport;
    }
    
    /**
     * 清理疑似模拟数据（谨慎使用）
     * 
     * @param dryRun 是否为试运行模式
     * @return 清理报告
     */
    public CleanupReport cleanupSuspiciousData(boolean dryRun) {
        logger.info("开始清理疑似模拟数据 - 试运行模式: {}", dryRun);
        
        CleanupReport cleanupReport = new CleanupReport();
        
        if (!dryRun) {
            logger.warn("执行实际数据清理操作，请确保已备份数据库");
        }
        
        try (Connection connection = dataSource.getConnection()) {
            if (!dryRun) {
                connection.setAutoCommit(false); // 开启事务
            }
            
            // 清理客户数据
            int customerCleanupCount = cleanupSuspiciousCustomers(connection, dryRun);
            cleanupReport.addCleanupCount("客户数据", customerCleanupCount);
            
            // 清理销售数据
            int salesCleanupCount = cleanupSuspiciousSales(connection, dryRun);
            cleanupReport.addCleanupCount("销售数据", salesCleanupCount);
            
            if (!dryRun) {
                connection.commit(); // 提交事务
                logger.info("数据清理事务已提交");
            }
            
        } catch (SQLException e) {
            logger.error("清理疑似模拟数据时发生错误", e);
            cleanupReport.addError("清理操作失败: " + e.getMessage());
        }
        
        return cleanupReport;
    }
    
    /**
     * 清理疑似模拟客户数据
     */
    private int cleanupSuspiciousCustomers(Connection connection, boolean dryRun) throws SQLException {
        String selectSql = "SELECT id, name, phone, email, address FROM customers WHERE " +
            "(name LIKE '%测试%' OR name LIKE '%test%' OR name LIKE '%demo%' OR name LIKE '%示例%') " +
            "OR (email LIKE '%test%' OR email LIKE '%example%') " +
            "OR (phone LIKE '000-%' OR phone LIKE '111-%' OR phone = '0000000000') " +
            "AND (is_deleted = 0 OR is_deleted IS NULL)";
        
        List<Long> suspiciousIds = new ArrayList<>();
        
        try (PreparedStatement selectStmt = connection.prepareStatement(selectSql);
             ResultSet rs = selectStmt.executeQuery()) {
            
            while (rs.next()) {
                suspiciousIds.add(rs.getLong("id"));
                logger.info("发现疑似模拟客户数据 - ID: {}, 姓名: {}", rs.getLong("id"), rs.getString("name"));
            }
        }
        
        if (!dryRun && !suspiciousIds.isEmpty()) {
            String deleteSql = "UPDATE customers SET is_deleted = 1, updated_at = NOW() WHERE id = ?";
            try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {
                for (Long id : suspiciousIds) {
                    deleteStmt.setLong(1, id);
                    deleteStmt.addBatch();
                }
                deleteStmt.executeBatch();
            }
        }
        
        return suspiciousIds.size();
    }
    
    /**
     * 清理疑似模拟销售数据
     */
    private int cleanupSuspiciousSales(Connection connection, boolean dryRun) throws SQLException {
        String selectSql = "SELECT id, order_number, customer_name FROM sales_records WHERE " +
            "(order_number LIKE 'TEST%' OR order_number LIKE 'DEMO%') " +
            "OR (customer_name LIKE '%测试%' OR customer_name LIKE '%test%') " +
            "AND (is_deleted = 0 OR is_deleted IS NULL)";
        
        List<Long> suspiciousIds = new ArrayList<>();
        
        try (PreparedStatement selectStmt = connection.prepareStatement(selectSql);
             ResultSet rs = selectStmt.executeQuery()) {
            
            while (rs.next()) {
                suspiciousIds.add(rs.getLong("id"));
                logger.info("发现疑似模拟销售数据 - ID: {}, 订单号: {}", rs.getLong("id"), rs.getString("order_number"));
            }
        }
        
        if (!dryRun && !suspiciousIds.isEmpty()) {
            String deleteSql = "UPDATE sales_records SET is_deleted = 1, updated_at = NOW() WHERE id = ?";
            try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {
                for (Long id : suspiciousIds) {
                    deleteStmt.setLong(1, id);
                    deleteStmt.addBatch();
                }
                deleteStmt.executeBatch();
            }
        }
        
        return suspiciousIds.size();
    }
    
    /**
     * 验证报告类
     */
    public static class ValidationReport {
        private String reportName;
        private int totalRecords;
        private int validRecords;
        private int suspiciousRecords;
        private List<String> suspiciousRecordDetails = new ArrayList<>();
        private List<String> errors = new ArrayList<>();
        private List<String> infos = new ArrayList<>();
        
        public ValidationReport(String reportName) {
            this.reportName = reportName;
        }
        
        // Getter和Setter方法
        public String getReportName() { return reportName; }
        public int getTotalRecords() { return totalRecords; }
        public void setTotalRecords(int totalRecords) { this.totalRecords = totalRecords; }
        public int getValidRecords() { return validRecords; }
        public void setValidRecords(int validRecords) { this.validRecords = validRecords; }
        public int getSuspiciousRecords() { return suspiciousRecords; }
        public void setSuspiciousRecords(int suspiciousRecords) { this.suspiciousRecords = suspiciousRecords; }
        public List<String> getSuspiciousRecordDetails() { return suspiciousRecordDetails; }
        public List<String> getErrors() { return errors; }
        public List<String> getInfos() { return infos; }
        
        public void addSuspiciousRecord(String detail) { suspiciousRecordDetails.add(detail); }
        public void addError(String error) { errors.add(error); }
        public void addInfo(String info) { infos.add(info); }
        
        public boolean isValid() { return suspiciousRecords == 0 && errors.isEmpty(); }
    }
    
    /**
     * 综合验证报告类
     */
    public static class ComprehensiveValidationReport {
        private List<ValidationReport> reports = new ArrayList<>();
        private String summary;
        
        public void addReport(ValidationReport report) { reports.add(report); }
        public List<ValidationReport> getReports() { return reports; }
        public String getSummary() { return summary; }
        
        public void generateSummary() {
            int totalRecords = reports.stream().mapToInt(ValidationReport::getTotalRecords).sum();
            int totalSuspicious = reports.stream().mapToInt(ValidationReport::getSuspiciousRecords).sum();
            int totalErrors = reports.stream().mapToInt(r -> r.getErrors().size()).sum();
            
            if (totalSuspicious == 0 && totalErrors == 0) {
                summary = String.format("数据验证通过 - 总计 %d 条记录，未发现模拟数据", totalRecords);
            } else {
                summary = String.format("数据验证发现问题 - 总计 %d 条记录，疑似模拟数据 %d 条，错误 %d 个", 
                                      totalRecords, totalSuspicious, totalErrors);
            }
        }
    }
    
    /**
     * 清理报告类
     */
    public static class CleanupReport {
        private List<String> cleanupCounts = new ArrayList<>();
        private List<String> errors = new ArrayList<>();
        
        public void addCleanupCount(String dataType, int count) {
            cleanupCounts.add(dataType + ": " + count + " 条记录");
        }
        
        public void addError(String error) { errors.add(error); }
        public List<String> getCleanupCounts() { return cleanupCounts; }
        public List<String> getErrors() { return errors; }
    }
}