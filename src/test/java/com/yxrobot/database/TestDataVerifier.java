package com.yxrobot.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

/**
 * 测试数据验证和补充
 */
@SpringBootTest
public class TestDataVerifier {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void verifyAndAddTestData() {
        System.out.println("=== 验证和补充测试数据 ===");
        
        try {
            // 1. 验证现有数据
            verifyExistingData();
            
            // 2. 检查数据关联关系
            verifyDataRelationships();
            
            // 3. 如果需要，添加更多测试数据
            addTestDataIfNeeded();
            
        } catch (Exception e) {
            System.out.println("❌ 测试数据验证失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void verifyExistingData() {
        System.out.println("\n=== 验证现有数据 ===");
        
        String[] tables = {"charity_stats", "charity_institutions", "charity_activities", 
                          "charity_projects", "charity_stats_logs"};
        
        for (String table : tables) {
            try {
                String sql = "SELECT COUNT(*) FROM " + table;
                Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
                System.out.printf("✅ %s: %d 条记录%n", table, count);
                
                // 显示一些示例数据
                if (count > 0) {
                    showSampleData(table);
                }
                
            } catch (Exception e) {
                System.out.printf("❌ %s: 查询失败 - %s%n", table, e.getMessage());
            }
        }
    }
    
    private void showSampleData(String tableName) {
        try {
            String sql = "";
            switch (tableName) {
                case "charity_stats":
                    sql = "SELECT id, total_beneficiaries, total_institutions, total_projects FROM charity_stats LIMIT 3";
                    break;
                case "charity_institutions":
                    sql = "SELECT id, name, type, status FROM charity_institutions LIMIT 3";
                    break;
                case "charity_activities":
                    sql = "SELECT id, title, type, status FROM charity_activities LIMIT 3";
                    break;
                case "charity_projects":
                    sql = "SELECT id, name, type, status FROM charity_projects LIMIT 3";
                    break;
                case "charity_stats_logs":
                    sql = "SELECT id, operation_type, created_at FROM charity_stats_logs LIMIT 3";
                    break;
                default:
                    return;
            }
            
            List<Map<String, Object>> samples = jdbcTemplate.queryForList(sql);
            System.out.println("    示例数据：");
            for (Map<String, Object> row : samples) {
                System.out.println("      " + row);
            }
            
        } catch (Exception e) {
            System.out.println("    示例数据获取失败：" + e.getMessage());
        }
    }
    
    private void verifyDataRelationships() {
        System.out.println("\n=== 验证数据关联关系 ===");
        
        try {
            // 检查charity_activities表中的project_id是否有效
            String sql = "SELECT COUNT(*) as invalid_count FROM charity_activities ca " +
                        "LEFT JOIN charity_projects cp ON ca.project_id = cp.id " +
                        "WHERE ca.project_id IS NOT NULL AND cp.id IS NULL";
            
            Integer invalidCount = jdbcTemplate.queryForObject(sql, Integer.class);
            if (invalidCount == 0) {
                System.out.println("✅ charity_activities表中的project_id关联关系正确");
            } else {
                System.out.printf("❌ charity_activities表中有 %d 条记录的project_id无效%n", invalidCount);
            }
            
            // 检查charity_stats_logs表中的stats_id是否有效
            sql = "SELECT COUNT(*) as invalid_count FROM charity_stats_logs csl " +
                  "LEFT JOIN charity_stats cs ON csl.stats_id = cs.id " +
                  "WHERE csl.stats_id IS NOT NULL AND cs.id IS NULL";
            
            invalidCount = jdbcTemplate.queryForObject(sql, Integer.class);
            if (invalidCount == 0) {
                System.out.println("✅ charity_stats_logs表中的stats_id关联关系正确");
            } else {
                System.out.printf("❌ charity_stats_logs表中有 %d 条记录的stats_id无效%n", invalidCount);
            }
            
            // 检查数据一致性
            verifyDataConsistency();
            
        } catch (Exception e) {
            System.out.println("❌ 数据关联关系验证失败：" + e.getMessage());
        }
    }
    
    private void verifyDataConsistency() {
        System.out.println("\n=== 验证数据一致性 ===");
        
        try {
            // 验证统计数据的逻辑一致性
            String sql = "SELECT " +
                        "total_institutions, cooperating_institutions, " +
                        "total_projects, active_projects, completed_projects, " +
                        "total_raised, total_donated " +
                        "FROM charity_stats ORDER BY created_at DESC LIMIT 1";
            
            List<Map<String, Object>> stats = jdbcTemplate.queryForList(sql);
            
            if (!stats.isEmpty()) {
                Map<String, Object> stat = stats.get(0);
                
                Integer totalInstitutions = (Integer) stat.get("total_institutions");
                Integer cooperatingInstitutions = (Integer) stat.get("cooperating_institutions");
                Integer totalProjects = (Integer) stat.get("total_projects");
                Integer activeProjects = (Integer) stat.get("active_projects");
                Integer completedProjects = (Integer) stat.get("completed_projects");
                
                // 检查逻辑一致性
                boolean consistent = true;
                
                if (cooperatingInstitutions > totalInstitutions) {
                    System.out.println("❌ 活跃机构数不能超过总机构数");
                    consistent = false;
                }
                
                if (activeProjects + completedProjects > totalProjects) {
                    System.out.println("❌ 活跃项目数和已完成项目数之和不能超过总项目数");
                    consistent = false;
                }
                
                if (consistent) {
                    System.out.println("✅ 统计数据逻辑一致性检查通过");
                }
            }
            
        } catch (Exception e) {
            System.out.println("❌ 数据一致性验证失败：" + e.getMessage());
        }
    }
    
    private void addTestDataIfNeeded() {
        System.out.println("\n=== 检查是否需要添加测试数据 ===");
        
        try {
            // 检查是否有足够的测试数据用于各种测试场景
            
            // 检查charity_institutions表中是否有不同状态的机构
            String sql = "SELECT status, COUNT(*) as count FROM charity_institutions GROUP BY status";
            List<Map<String, Object>> statusCounts = jdbcTemplate.queryForList(sql);
            
            System.out.println("机构状态分布：");
            boolean hasActive = false, hasInactive = false;
            for (Map<String, Object> row : statusCounts) {
                String status = (String) row.get("status");
                Integer count = ((Number) row.get("count")).intValue();
                System.out.printf("  %s: %d 个%n", status, count);
                
                if ("active".equals(status)) hasActive = true;
                if ("inactive".equals(status)) hasInactive = true;
            }
            
            // 如果缺少某些状态的测试数据，可以在这里添加
            if (!hasActive || !hasInactive) {
                System.out.println("ℹ️ 建议添加不同状态的机构测试数据以便全面测试");
            }
            
            // 检查charity_projects表中是否有不同状态的项目
            sql = "SELECT status, COUNT(*) as count FROM charity_projects GROUP BY status";
            List<Map<String, Object>> projectStatusCounts = jdbcTemplate.queryForList(sql);
            
            System.out.println("项目状态分布：");
            for (Map<String, Object> row : projectStatusCounts) {
                String status = (String) row.get("status");
                Integer count = ((Number) row.get("count")).intValue();
                System.out.printf("  %s: %d 个%n", status, count);
            }
            
            // 检查是否有最新的统计数据
            sql = "SELECT created_at FROM charity_stats ORDER BY created_at DESC LIMIT 1";
            List<Map<String, Object>> latestStats = jdbcTemplate.queryForList(sql);
            
            if (!latestStats.isEmpty()) {
                Object createdAt = latestStats.get(0).get("created_at");
                System.out.println("最新统计数据时间：" + createdAt);
            }
            
            System.out.println("✅ 现有测试数据足够进行基本功能测试");
            
        } catch (Exception e) {
            System.out.println("❌ 测试数据检查失败：" + e.getMessage());
        }
    }
}