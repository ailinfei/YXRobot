package com.yxrobot.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

/**
 * 数据库表结构验证测试
 */
@SpringBootTest
public class DatabaseSchemaTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void verifyDatabaseSchema() {
        System.out.println("=== 数据库表结构验证 ===");
        
        try {
            // 1. 测试数据库连接
            String dbName = jdbcTemplate.queryForObject("SELECT DATABASE() as db_name", String.class);
            System.out.println("✅ 连接到数据库: " + dbName);
            
            // 2. 检查charity相关表
            String tablesSql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES " +
                              "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME LIKE 'charity_%' " +
                              "ORDER BY TABLE_NAME";
            
            List<String> tables = jdbcTemplate.queryForList(tablesSql, String.class);
            
            if (tables.isEmpty()) {
                System.out.println("❌ 未找到任何charity相关表！");
                return;
            }
            
            System.out.println("✅ 找到 " + tables.size() + " 个charity相关表：");
            for (String table : tables) {
                System.out.println("  - " + table);
                
                // 检查每个表的记录数
                try {
                    String countSql = "SELECT COUNT(*) FROM " + table;
                    Integer count = jdbcTemplate.queryForObject(countSql, Integer.class);
                    System.out.println("    记录数: " + count);
                } catch (Exception e) {
                    System.out.println("    记录数: 查询失败 - " + e.getMessage());
                }
            }
            
            // 3. 验证charity_stats表结构
            if (tables.contains("charity_stats")) {
                System.out.println("\n=== 验证charity_stats表结构 ===");
                verifyCharityStatsTable();
            }
            
            // 4. 验证charity_institutions表结构
            if (tables.contains("charity_institutions")) {
                System.out.println("\n=== 验证charity_institutions表结构 ===");
                verifyCharityInstitutionsTable();
            }
            
        } catch (Exception e) {
            System.out.println("❌ 数据库验证失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void verifyCharityStatsTable() {
        String sql = "SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE " +
                    "FROM INFORMATION_SCHEMA.COLUMNS " +
                    "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'charity_stats' " +
                    "ORDER BY ORDINAL_POSITION";
        
        try {
            List<Map<String, Object>> columns = jdbcTemplate.queryForList(sql);
            
            System.out.println("charity_stats表字段：");
            for (Map<String, Object> column : columns) {
                System.out.printf("  - %s (%s) %s%n", 
                    column.get("COLUMN_NAME"),
                    column.get("DATA_TYPE"),
                    "YES".equals(column.get("IS_NULLABLE")) ? "NULL" : "NOT NULL");
            }
            
            // 验证必要字段
            String[] requiredFields = {
                "id", "total_beneficiaries", "total_institutions", "cooperating_institutions",
                "total_volunteers", "total_raised", "total_donated", "total_projects",
                "active_projects", "completed_projects", "total_activities", "this_month_activities"
            };
            
            System.out.println("\n必要字段检查：");
            for (String field : requiredFields) {
                boolean exists = columns.stream()
                    .anyMatch(col -> field.equals(col.get("COLUMN_NAME")));
                System.out.printf("  %s %s%n", exists ? "✅" : "❌", field);
            }
            
        } catch (Exception e) {
            System.out.println("❌ 验证charity_stats表失败：" + e.getMessage());
        }
    }
    
    private void verifyCharityInstitutionsTable() {
        String sql = "SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE " +
                    "FROM INFORMATION_SCHEMA.COLUMNS " +
                    "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'charity_institutions' " +
                    "ORDER BY ORDINAL_POSITION";
        
        try {
            List<Map<String, Object>> columns = jdbcTemplate.queryForList(sql);
            
            System.out.println("charity_institutions表字段：");
            for (Map<String, Object> column : columns) {
                System.out.printf("  - %s (%s) %s%n", 
                    column.get("COLUMN_NAME"),
                    column.get("DATA_TYPE"),
                    "YES".equals(column.get("IS_NULLABLE")) ? "NULL" : "NOT NULL");
            }
            
            // 验证必要字段
            String[] requiredFields = {
                "id", "name", "type", "location", "address", "contact_person", 
                "contact_phone", "cooperation_date", "status"
            };
            
            System.out.println("\n必要字段检查：");
            for (String field : requiredFields) {
                boolean exists = columns.stream()
                    .anyMatch(col -> field.equals(col.get("COLUMN_NAME")));
                System.out.printf("  %s %s%n", exists ? "✅" : "❌", field);
            }
            
        } catch (Exception e) {
            System.out.println("❌ 验证charity_institutions表失败：" + e.getMessage());
        }
    }
}