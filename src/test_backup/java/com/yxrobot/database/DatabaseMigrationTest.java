package com.yxrobot.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

/**
 * 数据库迁移测试
 */
@SpringBootTest
public class DatabaseMigrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void addVersionFieldToCharityStats() {
        System.out.println("=== 为charity_stats表添加version字段 ===");
        
        try {
            // 1. 检查version字段是否已存在
            String checkSql = "SELECT COUNT(*) as field_exists " +
                             "FROM INFORMATION_SCHEMA.COLUMNS " +
                             "WHERE TABLE_SCHEMA = DATABASE() " +
                             "AND TABLE_NAME = 'charity_stats' " +
                             "AND COLUMN_NAME = 'version'";
            
            Integer fieldExists = jdbcTemplate.queryForObject(checkSql, Integer.class);
            
            if (fieldExists > 0) {
                System.out.println("✅ version字段已存在，无需添加");
                return;
            }
            
            System.out.println("ℹ️ version字段不存在，开始添加...");
            
            // 2. 添加version字段
            String alterSql = "ALTER TABLE charity_stats " +
                             "ADD COLUMN version INT DEFAULT 1 COMMENT '版本号，用于乐观锁控制' " +
                             "AFTER is_current";
            
            jdbcTemplate.execute(alterSql);
            System.out.println("✅ 成功添加version字段");
            
            // 3. 为现有记录设置默认版本号
            String updateSql = "UPDATE charity_stats SET version = 1 WHERE version IS NULL";
            int updatedRows = jdbcTemplate.update(updateSql);
            System.out.println("✅ 为 " + updatedRows + " 条现有记录设置了默认版本号");
            
            // 4. 验证字段添加成功
            String verifySql = "SELECT " +
                              "COLUMN_NAME, " +
                              "DATA_TYPE, " +
                              "IS_NULLABLE, " +
                              "COLUMN_DEFAULT, " +
                              "COLUMN_COMMENT " +
                              "FROM INFORMATION_SCHEMA.COLUMNS " +
                              "WHERE TABLE_SCHEMA = DATABASE() " +
                              "AND TABLE_NAME = 'charity_stats' " +
                              "AND COLUMN_NAME = 'version'";
            
            List<Map<String, Object>> result = jdbcTemplate.queryForList(verifySql);
            
            if (!result.isEmpty()) {
                Map<String, Object> column = result.get(0);
                System.out.println("✅ version字段验证成功：");
                System.out.printf("  - 字段名: %s%n", column.get("COLUMN_NAME"));
                System.out.printf("  - 数据类型: %s%n", column.get("DATA_TYPE"));
                System.out.printf("  - 可为空: %s%n", column.get("IS_NULLABLE"));
                System.out.printf("  - 默认值: %s%n", column.get("COLUMN_DEFAULT"));
                System.out.printf("  - 注释: %s%n", column.get("COLUMN_COMMENT"));
            }
            
            // 5. 测试version字段功能
            testVersionField();
            
        } catch (Exception e) {
            System.out.println("❌ 添加version字段失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void testVersionField() {
        System.out.println("\n=== 测试version字段功能 ===");
        
        try {
            // 查询一条记录测试version字段
            String testSql = "SELECT id, version FROM charity_stats LIMIT 1";
            List<Map<String, Object>> records = jdbcTemplate.queryForList(testSql);
            
            if (!records.isEmpty()) {
                Map<String, Object> record = records.get(0);
                Long id = ((Number) record.get("id")).longValue();
                Integer version = (Integer) record.get("version");
                
                System.out.printf("✅ 测试记录 ID: %d, 当前版本: %d%n", id, version);
                
                // 测试版本号更新
                String updateVersionSql = "UPDATE charity_stats SET version = version + 1 WHERE id = ?";
                int updated = jdbcTemplate.update(updateVersionSql, id);
                
                if (updated > 0) {
                    // 验证版本号是否更新
                    Integer newVersion = jdbcTemplate.queryForObject(
                        "SELECT version FROM charity_stats WHERE id = ?", 
                        Integer.class, 
                        id
                    );
                    System.out.printf("✅ 版本号更新成功，新版本: %d%n", newVersion);
                } else {
                    System.out.println("❌ 版本号更新失败");
                }
            } else {
                System.out.println("ℹ️ 没有找到测试记录");
            }
            
        } catch (Exception e) {
            System.out.println("❌ 测试version字段功能失败：" + e.getMessage());
        }
    }
}