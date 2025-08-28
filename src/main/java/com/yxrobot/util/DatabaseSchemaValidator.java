package com.yxrobot.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * 数据库架构验证工具
 * 用于验证数据库表结构与映射器配置的一致性
 * 
 * @author YXRobot
 * @version 1.0
 * @since 2024-12-18
 */
@Component
public class DatabaseSchemaValidator {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseSchemaValidator.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * 应用启动时验证数据库架构
     */
    @PostConstruct
    public void validateSchemaOnStartup() {
        logger.info("开始验证数据库架构...");
        
        try {
            validateCharityActivitySchema();
            validateCharityProjectSchema();
            validateCharityInstitutionSchema();
            validateCharityStatsSchema();
            
            logger.info("数据库架构验证完成");
        } catch (Exception e) {
            logger.error("数据库架构验证失败", e);
        }
    }
    
    /**
     * 验证charity_activities表结构
     */
    public void validateCharityActivitySchema() {
        logger.info("验证charity_activities表结构...");
        
        try {
            // 检查表是否存在
            if (!tableExists("charity_activities")) {
                logger.error("表charity_activities不存在");
                return;
            }
            
            // 获取表字段信息
            List<Map<String, Object>> columns = getTableColumns("charity_activities");
            
            // 验证关键时间戳字段
            boolean hasCreateTime = false;
            boolean hasUpdateTime = false;
            boolean hasCreatedAt = false;
            boolean hasUpdatedAt = false;
            
            for (Map<String, Object> column : columns) {
                String columnName = (String) column.get("COLUMN_NAME");
                String dataType = (String) column.get("DATA_TYPE");
                
                logger.info("字段: {} - 类型: {}", columnName, dataType);
                
                if ("create_time".equals(columnName)) {
                    hasCreateTime = true;
                    logger.info("✓ 找到create_time字段");
                }
                if ("update_time".equals(columnName)) {
                    hasUpdateTime = true;
                    logger.info("✓ 找到update_time字段");
                }
                if ("created_at".equals(columnName)) {
                    hasCreatedAt = true;
                    logger.warn("⚠ 找到created_at字段（映射器期望的字段）");
                }
                if ("updated_at".equals(columnName)) {
                    hasUpdatedAt = true;
                    logger.warn("⚠ 找到updated_at字段（映射器期望的字段）");
                }
            }
            
            // 报告字段映射问题
            if (hasCreateTime && hasUpdateTime) {
                logger.info("✓ charity_activities表使用create_time/update_time字段命名");
            }
            
            if (hasCreatedAt || hasUpdatedAt) {
                logger.warn("⚠ charity_activities表包含created_at/updated_at字段");
            }
            
            if (!hasCreateTime && !hasCreatedAt) {
                logger.error("✗ charity_activities表缺少创建时间字段");
            }
            
            if (!hasUpdateTime && !hasUpdatedAt) {
                logger.error("✗ charity_activities表缺少更新时间字段");
            }
            
            // 检查映射器期望的字段是否存在
            validateFieldMapping("charity_activities", "created_at", hasCreatedAt);
            validateFieldMapping("charity_activities", "updated_at", hasUpdatedAt);
            
        } catch (Exception e) {
            logger.error("验证charity_activities表结构时出错", e);
        }
    }
    
    /**
     * 验证charity_projects表结构
     */
    public void validateCharityProjectSchema() {
        logger.info("验证charity_projects表结构...");
        
        try {
            if (!tableExists("charity_projects")) {
                logger.error("表charity_projects不存在");
                return;
            }
            
            List<Map<String, Object>> columns = getTableColumns("charity_projects");
            
            boolean hasCreatedAt = false;
            boolean hasUpdatedAt = false;
            
            for (Map<String, Object> column : columns) {
                String columnName = (String) column.get("COLUMN_NAME");
                
                if ("created_at".equals(columnName)) {
                    hasCreatedAt = true;
                }
                if ("updated_at".equals(columnName)) {
                    hasUpdatedAt = true;
                }
            }
            
            validateFieldMapping("charity_projects", "created_at", hasCreatedAt);
            validateFieldMapping("charity_projects", "updated_at", hasUpdatedAt);
            
        } catch (Exception e) {
            logger.error("验证charity_projects表结构时出错", e);
        }
    }
    
    /**
     * 验证charity_institutions表结构
     */
    public void validateCharityInstitutionSchema() {
        logger.info("验证charity_institutions表结构...");
        
        try {
            if (!tableExists("charity_institutions")) {
                logger.error("表charity_institutions不存在");
                return;
            }
            
            List<Map<String, Object>> columns = getTableColumns("charity_institutions");
            
            boolean hasCreatedAt = false;
            boolean hasUpdatedAt = false;
            
            for (Map<String, Object> column : columns) {
                String columnName = (String) column.get("COLUMN_NAME");
                
                if ("created_at".equals(columnName)) {
                    hasCreatedAt = true;
                }
                if ("updated_at".equals(columnName)) {
                    hasUpdatedAt = true;
                }
            }
            
            validateFieldMapping("charity_institutions", "created_at", hasCreatedAt);
            validateFieldMapping("charity_institutions", "updated_at", hasUpdatedAt);
            
        } catch (Exception e) {
            logger.error("验证charity_institutions表结构时出错", e);
        }
    }
    
    /**
     * 验证charity_stats表结构
     */
    public void validateCharityStatsSchema() {
        logger.info("验证charity_stats表结构...");
        
        try {
            if (!tableExists("charity_stats")) {
                logger.error("表charity_stats不存在");
                return;
            }
            
            List<Map<String, Object>> columns = getTableColumns("charity_stats");
            
            boolean hasCreateTime = false;
            boolean hasUpdateTime = false;
            
            for (Map<String, Object> column : columns) {
                String columnName = (String) column.get("COLUMN_NAME");
                
                if ("create_time".equals(columnName)) {
                    hasCreateTime = true;
                }
                if ("update_time".equals(columnName)) {
                    hasUpdateTime = true;
                }
            }
            
            if (hasCreateTime && hasUpdateTime) {
                logger.info("✓ charity_stats表使用create_time/update_time字段命名");
            }
            
        } catch (Exception e) {
            logger.error("验证charity_stats表结构时出错", e);
        }
    }
    
    /**
     * 检查表是否存在
     */
    private boolean tableExists(String tableName) {
        try {
            String sql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, tableName);
            return count != null && count > 0;
        } catch (Exception e) {
            logger.error("检查表{}是否存在时出错", tableName, e);
            return false;
        }
    }
    
    /**
     * 获取表的字段信息
     */
    private List<Map<String, Object>> getTableColumns(String tableName) {
        String sql = "SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_DEFAULT " +
                    "FROM information_schema.columns " +
                    "WHERE table_schema = DATABASE() AND table_name = ? " +
                    "ORDER BY ORDINAL_POSITION";
        return jdbcTemplate.queryForList(sql, tableName);
    }
    
    /**
     * 验证字段映射
     */
    private void validateFieldMapping(String tableName, String fieldName, boolean exists) {
        if (exists) {
            logger.info("✓ 表{}包含字段{}", tableName, fieldName);
        } else {
            logger.error("✗ 表{}缺少字段{} - 映射器配置可能有误", tableName, fieldName);
        }
    }
    
    /**
     * 生成字段映射问题报告
     */
    public void generateFieldMappingReport() {
        logger.info("=== 字段映射问题报告 ===");
        
        logger.info("问题分析:");
        logger.info("1. CharityActivityMapper.xml中使用created_at/updated_at字段");
        logger.info("2. 数据库表charity_activities使用create_time/update_time字段");
        logger.info("3. 实体类CharityActivity使用createTime/updateTime属性");
        
        logger.info("解决方案:");
        logger.info("方案A: 修改数据库表字段名为created_at/updated_at");
        logger.info("方案B: 修改映射器使用create_time/update_time字段（推荐）");
        
        logger.info("推荐使用方案B，因为:");
        logger.info("- 数据库表结构已存在，修改风险较小");
        logger.info("- 保持与其他表的命名一致性");
        logger.info("- 避免数据迁移的复杂性");
    }
}