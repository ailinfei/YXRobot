package com.yxrobot.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 真实数据验证器
 * 用于检测和防止模拟/示例数据的插入
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2025-01-25
 */
@Component
public class RealDataValidator {
    
    private static final Logger logger = LoggerFactory.getLogger(RealDataValidator.class);
    
    @Autowired
    private DataSource dataSource;
    
    // 可疑关键词模式
    private static final Pattern SUSPICIOUS_NAME_PATTERN = Pattern.compile(
        ".*(测试|示例|test|demo|mock|sample|example|张三|李四|王五|赵六|admin|root).*", 
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern SUSPICIOUS_PHONE_PATTERN = Pattern.compile(
        "^(000|111|123|999|888)-.*|^(000|111|123|999|888)\\d{8}$|^1[0-9]{2}(0{8}|1{8}|2{8})$"
    );
    
    private static final Pattern SUSPICIOUS_EMAIL_PATTERN = Pattern.compile(
        ".*@(example|test|demo|mock|sample|localhost)\\.(com|org|net|cn)$", 
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern SUSPICIOUS_ADDRESS_PATTERN = Pattern.compile(
        ".*(测试|示例|test|demo|example|假地址|虚拟地址|临时地址).*", 
        Pattern.CASE_INSENSITIVE
    );
    
    /**
     * 验证客户数据是否为真实数据
     * 
     * @param customerName 客户姓名
     * @param phone 电话号码
     * @param email 邮箱地址
     * @param address 地址
     * @return 验证结果
     */
    public ValidationResult validateCustomerData(String customerName, String phone, String email, String address) {
        ValidationResult result = new ValidationResult();
        result.setValid(true);
        
        List<String> warnings = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        
        // 验证客户姓名
        if (customerName != null && SUSPICIOUS_NAME_PATTERN.matcher(customerName).matches()) {
            errors.add("客户姓名包含可疑的测试关键词: " + customerName);
            result.setValid(false);
        }
        
        // 验证电话号码
        if (phone != null && SUSPICIOUS_PHONE_PATTERN.matcher(phone).matches()) {
            errors.add("电话号码疑似测试数据: " + phone);
            result.setValid(false);
        }
        
        // 验证邮箱地址
        if (email != null && SUSPICIOUS_EMAIL_PATTERN.matcher(email).matches()) {
            errors.add("邮箱地址疑似测试数据: " + email);
            result.setValid(false);
        }
        
        // 验证地址
        if (address != null && SUSPICIOUS_ADDRESS_PATTERN.matcher(address).matches()) {
            warnings.add("地址可能包含测试关键词: " + address);
        }
        
        // 检查数据完整性
        if (customerName == null || customerName.trim().isEmpty()) {
            errors.add("客户姓名不能为空");
            result.setValid(false);
        }
        
        if (phone == null || phone.trim().isEmpty()) {
            errors.add("电话号码不能为空");
            result.setValid(false);
        }
        
        result.setWarnings(warnings);
        result.setErrors(errors);
        
        return result;
    }
    
    /**
     * 验证订单数据是否为真实数据
     * 
     * @param customerName 客户姓名
     * @param productName 产品名称
     * @param orderAmount 订单金额
     * @return 验证结果
     */
    public ValidationResult validateOrderData(String customerName, String productName, String orderAmount) {
        ValidationResult result = new ValidationResult();
        result.setValid(true);
        
        List<String> warnings = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        
        // 验证客户姓名
        if (customerName != null && SUSPICIOUS_NAME_PATTERN.matcher(customerName).matches()) {
            errors.add("客户姓名包含可疑的测试关键词: " + customerName);
            result.setValid(false);
        }
        
        // 验证产品名称
        if (productName != null && productName.matches(".*(测试|示例|test|demo|mock|sample|example).*")) {
            errors.add("产品名称疑似测试数据: " + productName);
            result.setValid(false);
        }
        
        // 验证订单金额
        if (orderAmount != null) {
            try {
                double amount = Double.parseDouble(orderAmount);
                if (amount <= 0) {
                    errors.add("订单金额必须大于0: " + orderAmount);
                    result.setValid(false);
                } else if (amount == 1.0 || amount == 100.0 || amount == 1000.0) {
                    warnings.add("订单金额可能是测试数据: " + orderAmount);
                }
            } catch (NumberFormatException e) {
                errors.add("订单金额格式不正确: " + orderAmount);
                result.setValid(false);
            }
        }
        
        // 检查数据完整性
        if (customerName == null || customerName.trim().isEmpty()) {
            errors.add("客户姓名不能为空");
            result.setValid(false);
        }
        
        if (productName == null || productName.trim().isEmpty()) {
            errors.add("产品名称不能为空");
            result.setValid(false);
        }
        
        if (orderAmount == null || orderAmount.trim().isEmpty()) {
            errors.add("订单金额不能为空");
            result.setValid(false);
        }
        
        result.setWarnings(warnings);
        result.setErrors(errors);
        
        return result;
    }
    
    /**
     * 扫描数据库中的可疑数据
     * 
     * @return 扫描结果
     */
    public DatabaseScanResult scanSuspiciousData() {
        DatabaseScanResult result = new DatabaseScanResult();
        
        try (Connection connection = dataSource.getConnection()) {
            // 扫描客户表
            List<SuspiciousRecord> customerSuspiciousRecords = scanCustomersTable(connection);
            result.setSuspiciousCustomers(customerSuspiciousRecords);
            
            // 扫描租赁客户表
            List<SuspiciousRecord> rentalCustomerSuspiciousRecords = scanRentalCustomersTable(connection);
            result.setSuspiciousRentalCustomers(rentalCustomerSuspiciousRecords);
            
            // 统计结果
            int totalSuspicious = customerSuspiciousRecords.size() + rentalCustomerSuspiciousRecords.size();
            result.setTotalSuspiciousRecords(totalSuspicious);
            result.setHasSuspiciousData(totalSuspicious > 0);
            
            logger.info("数据库扫描完成，发现 {} 条可疑记录", totalSuspicious);
            
        } catch (SQLException e) {
            logger.error("扫描数据库时发生错误", e);
            result.setError("扫描数据库时发生错误: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 扫描客户表中的可疑数据
     */
    private List<SuspiciousRecord> scanCustomersTable(Connection connection) throws SQLException {
        List<SuspiciousRecord> suspiciousRecords = new ArrayList<>();
        
        String sql = "SELECT id, customer_name, phone, email, address FROM customers " +
                    "WHERE (is_deleted = 0 OR is_deleted IS NULL) " +
                    "AND (customer_name REGEXP '(测试|示例|test|demo|mock|sample|example|张三|李四|王五|赵六)' " +
                    "OR phone REGEXP '^(000|111|123|999)' " +
                    "OR email REGEXP '@(example|test|demo|mock|sample)\\\\.' " +
                    "OR address REGEXP '(测试|示例|test|demo|example)')";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                SuspiciousRecord record = new SuspiciousRecord();
                record.setTableName("customers");
                record.setRecordId(rs.getLong("id"));
                record.setCustomerName(rs.getString("customer_name"));
                record.setPhone(rs.getString("phone"));
                record.setEmail(rs.getString("email"));
                record.setAddress(rs.getString("address"));
                
                // 分析可疑原因
                List<String> reasons = new ArrayList<>();
                if (SUSPICIOUS_NAME_PATTERN.matcher(record.getCustomerName()).matches()) {
                    reasons.add("姓名包含测试关键词");
                }
                if (SUSPICIOUS_PHONE_PATTERN.matcher(record.getPhone()).matches()) {
                    reasons.add("电话号码疑似测试数据");
                }
                if (SUSPICIOUS_EMAIL_PATTERN.matcher(record.getEmail()).matches()) {
                    reasons.add("邮箱地址疑似测试数据");
                }
                if (SUSPICIOUS_ADDRESS_PATTERN.matcher(record.getAddress()).matches()) {
                    reasons.add("地址包含测试关键词");
                }
                
                record.setSuspiciousReasons(reasons);
                suspiciousRecords.add(record);
            }
        }
        
        return suspiciousRecords;
    }
    
    /**
     * 扫描租赁客户表中的可疑数据
     */
    private List<SuspiciousRecord> scanRentalCustomersTable(Connection connection) throws SQLException {
        List<SuspiciousRecord> suspiciousRecords = new ArrayList<>();
        
        String sql = "SELECT customer_id, customer_name, phone, email, address FROM rental_customers " +
                    "WHERE (is_deleted = 0 OR is_deleted IS NULL) " +
                    "AND (customer_name REGEXP '(测试|示例|test|demo|mock|sample|example|类型)' " +
                    "OR phone REGEXP '^(000|111|123|999)' " +
                    "OR email REGEXP '@(example|test|demo|mock|sample)\\\\.' " +
                    "OR address REGEXP '(测试|示例|test|demo|example|类型)')";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                SuspiciousRecord record = new SuspiciousRecord();
                record.setTableName("rental_customers");
                record.setRecordId(rs.getLong("customer_id"));
                record.setCustomerName(rs.getString("customer_name"));
                record.setPhone(rs.getString("phone"));
                record.setEmail(rs.getString("email"));
                record.setAddress(rs.getString("address"));
                
                // 分析可疑原因
                List<String> reasons = new ArrayList<>();
                String customerName = record.getCustomerName();
                String phone = record.getPhone();
                String email = record.getEmail();
                String address = record.getAddress();
                
                if (customerName != null && SUSPICIOUS_NAME_PATTERN.matcher(customerName).matches()) {
                    reasons.add("姓名包含测试关键词");
                }
                if (phone != null && SUSPICIOUS_PHONE_PATTERN.matcher(phone).matches()) {
                    reasons.add("电话号码疑似测试数据");
                }
                if (email != null && SUSPICIOUS_EMAIL_PATTERN.matcher(email).matches()) {
                    reasons.add("邮箱地址疑似测试数据");
                }
                if (address != null && SUSPICIOUS_ADDRESS_PATTERN.matcher(address).matches()) {
                    reasons.add("地址包含测试关键词");
                }
                
                record.setSuspiciousReasons(reasons);
                suspiciousRecords.add(record);
            }
        }
        
        return suspiciousRecords;
    }
    
    /**
     * 验证结果类
     */
    public static class ValidationResult {
        private boolean valid;
        private List<String> warnings;
        private List<String> errors;
        
        public boolean isValid() {
            return valid;
        }
        
        public void setValid(boolean valid) {
            this.valid = valid;
        }
        
        public List<String> getWarnings() {
            return warnings;
        }
        
        public void setWarnings(List<String> warnings) {
            this.warnings = warnings;
        }
        
        public List<String> getErrors() {
            return errors;
        }
        
        public void setErrors(List<String> errors) {
            this.errors = errors;
        }
        
        public boolean hasWarnings() {
            return warnings != null && !warnings.isEmpty();
        }
        
        public boolean hasErrors() {
            return errors != null && !errors.isEmpty();
        }
        
        /**
         * 获取违规消息
         * @return 违规消息字符串
         */
        public String getViolationMessage() {
            StringBuilder message = new StringBuilder();
            
            if (hasErrors()) {
                message.append("错误: ").append(String.join("; ", errors));
            }
            
            if (hasWarnings()) {
                if (message.length() > 0) {
                    message.append(" | ");
                }
                message.append("警告: ").append(String.join("; ", warnings));
            }
            
            return message.toString();
        }
    }
    
    /**
     * 数据库扫描结果类
     */
    public static class DatabaseScanResult {
        private List<SuspiciousRecord> suspiciousCustomers;
        private List<SuspiciousRecord> suspiciousRentalCustomers;
        private int totalSuspiciousRecords;
        private boolean hasSuspiciousData;
        private String error;
        
        public List<SuspiciousRecord> getSuspiciousCustomers() {
            return suspiciousCustomers;
        }
        
        public void setSuspiciousCustomers(List<SuspiciousRecord> suspiciousCustomers) {
            this.suspiciousCustomers = suspiciousCustomers;
        }
        
        public List<SuspiciousRecord> getSuspiciousRentalCustomers() {
            return suspiciousRentalCustomers;
        }
        
        public void setSuspiciousRentalCustomers(List<SuspiciousRecord> suspiciousRentalCustomers) {
            this.suspiciousRentalCustomers = suspiciousRentalCustomers;
        }
        
        public int getTotalSuspiciousRecords() {
            return totalSuspiciousRecords;
        }
        
        public void setTotalSuspiciousRecords(int totalSuspiciousRecords) {
            this.totalSuspiciousRecords = totalSuspiciousRecords;
        }
        
        public boolean isHasSuspiciousData() {
            return hasSuspiciousData;
        }
        
        public void setHasSuspiciousData(boolean hasSuspiciousData) {
            this.hasSuspiciousData = hasSuspiciousData;
        }
        
        public String getError() {
            return error;
        }
        
        public void setError(String error) {
            this.error = error;
        }
    }
    
    /**
     * 可疑记录类
     */
    public static class SuspiciousRecord {
        private String tableName;
        private Long recordId;
        private String customerName;
        private String phone;
        private String email;
        private String address;
        private List<String> suspiciousReasons;
        
        public String getTableName() {
            return tableName;
        }
        
        public void setTableName(String tableName) {
            this.tableName = tableName;
        }
        
        public Long getRecordId() {
            return recordId;
        }
        
        public void setRecordId(Long recordId) {
            this.recordId = recordId;
        }
        
        public String getCustomerName() {
            return customerName;
        }
        
        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }
        
        public String getPhone() {
            return phone;
        }
        
        public void setPhone(String phone) {
            this.phone = phone;
        }
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public String getAddress() {
            return address;
        }
        
        public void setAddress(String address) {
            this.address = address;
        }
        
        public List<String> getSuspiciousReasons() {
            return suspiciousReasons;
        }
        
        public void setSuspiciousReasons(List<String> suspiciousReasons) {
            this.suspiciousReasons = suspiciousReasons;
        }
    }
}