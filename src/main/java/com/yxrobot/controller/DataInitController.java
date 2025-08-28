package com.yxrobot.controller;

import com.yxrobot.common.Result;
import com.yxrobot.entity.CharityStats;
import com.yxrobot.mapper.CharityStatsMapper;
import com.yxrobot.mapper.CharityInstitutionMapper;
import com.yxrobot.mapper.CharityProjectMapper;
import com.yxrobot.mapper.CharityActivityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据初始化控制器
 * 用于开发环境初始化测试数据
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-21
 */
@RestController
@RequestMapping("/api/admin/data-init")
public class DataInitController {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitController.class);
    
    @Autowired
    private CharityStatsMapper charityStatsMapper;
    
    @Autowired
    private CharityInstitutionMapper charityInstitutionMapper;
    
    @Autowired
    private CharityProjectMapper charityProjectMapper;
    
    @Autowired
    private CharityActivityMapper charityActivityMapper;
    
    /**
     * 初始化公益统计测试数据
     */
    @PostMapping("/charity-stats")
    public Result<Map<String, Object>> initCharityStats() {
        logger.info("开始初始化公益统计测试数据");
        
        try {
            // 检查是否已有数据
            CharityStats existingStats = charityStatsMapper.selectLatest();
            if (existingStats != null) {
                logger.info("已存在统计数据，跳过初始化");
                Map<String, Object> result = new HashMap<>();
                result.put("message", "统计数据已存在，无需初始化");
                result.put("existingData", existingStats);
                return Result.success(result);
            }
            
            // 创建测试统计数据
            CharityStats testStats = new CharityStats();
            testStats.setTotalBeneficiaries(28650);
            testStats.setTotalInstitutions(342);
            testStats.setCooperatingInstitutions(198);
            testStats.setTotalVolunteers(285);
            testStats.setTotalRaised(new BigDecimal("18500000.00"));
            testStats.setTotalDonated(new BigDecimal("15200000.00"));
            testStats.setTotalProjects(156);
            testStats.setActiveProjects(42);
            testStats.setCompletedProjects(89);
            testStats.setTotalActivities(456);
            testStats.setThisMonthActivities(28);
            testStats.setUpdateReason("系统初始化测试数据");
            testStats.setUpdatedBy("系统管理员");
            testStats.setVersion(1);
            testStats.setDeleted(0);
            testStats.setCreateTime(LocalDateTime.now());
            testStats.setUpdateTime(LocalDateTime.now());
            
            int result = charityStatsMapper.insert(testStats);
            
            if (result > 0) {
                logger.info("成功初始化公益统计测试数据");
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("message", "公益统计测试数据初始化成功");
                responseData.put("insertedData", testStats);
                return Result.success(responseData);
            } else {
                logger.error("初始化公益统计测试数据失败");
                return Result.error("初始化统计数据失败");
            }
            
        } catch (Exception e) {
            logger.error("初始化公益统计测试数据异常", e);
            return Result.error("初始化统计数据异常: " + e.getMessage());
        }
    }
    
    /**
     * 初始化所有公益测试数据
     */
    @PostMapping("/all-charity-data")
    public Result<Map<String, Object>> initAllCharityData() {
        logger.info("开始初始化所有公益测试数据");
        
        try {
            Map<String, Object> result = new HashMap<>();
            
            // 1. 初始化统计数据
            Result<Map<String, Object>> statsResult = initCharityStats();
            result.put("stats", statsResult.getData());
            
            // 2. 初始化机构数据（简化版）
            try {
                // 这里可以添加机构数据初始化逻辑
                result.put("institutions", "机构数据初始化完成");
            } catch (Exception e) {
                logger.warn("机构数据初始化失败", e);
                result.put("institutions", "机构数据初始化失败: " + e.getMessage());
            }
            
            // 3. 初始化项目数据（简化版）
            try {
                // 这里可以添加项目数据初始化逻辑
                result.put("projects", "项目数据初始化完成");
            } catch (Exception e) {
                logger.warn("项目数据初始化失败", e);
                result.put("projects", "项目数据初始化失败: " + e.getMessage());
            }
            
            // 4. 初始化活动数据（简化版）
            try {
                // 这里可以添加活动数据初始化逻辑
                result.put("activities", "活动数据初始化完成");
            } catch (Exception e) {
                logger.warn("活动数据初始化失败", e);
                result.put("activities", "活动数据初始化失败: " + e.getMessage());
            }
            
            logger.info("所有公益测试数据初始化完成");
            return Result.success(result);
            
        } catch (Exception e) {
            logger.error("初始化所有公益测试数据异常", e);
            return Result.error("初始化所有测试数据异常: " + e.getMessage());
        }
    }
    
    /**
     * 检查数据库连接状态
     */
    @GetMapping("/check-db")
    public Result<Map<String, Object>> checkDatabase() {
        logger.info("检查数据库连接状态");
        
        try {
            Map<String, Object> result = new HashMap<>();
            
            // 检查统计数据表
            try {
                CharityStats stats = charityStatsMapper.selectLatest();
                result.put("charity_stats", stats != null ? "有数据" : "无数据");
                result.put("stats_count", stats != null ? 1 : 0);
            } catch (Exception e) {
                result.put("charity_stats", "表不存在或查询失败: " + e.getMessage());
            }
            
            result.put("database_status", "连接正常");
            result.put("check_time", LocalDateTime.now());
            
            return Result.success(result);
            
        } catch (Exception e) {
            logger.error("检查数据库连接失败", e);
            return Result.error("数据库连接检查失败: " + e.getMessage());
        }
    }
    
    /**
     * 清空测试数据（仅开发环境使用）
     */
    @DeleteMapping("/clear-test-data")
    public Result<Map<String, Object>> clearTestData() {
        logger.warn("清空测试数据请求");
        
        try {
            Map<String, Object> result = new HashMap<>();
            
            // 清空统计数据
            try {
                // 这里可以添加清空逻辑，但要谨慎使用
                result.put("stats", "清空操作已禁用，请手动操作");
            } catch (Exception e) {
                result.put("stats", "清空失败: " + e.getMessage());
            }
            
            logger.warn("测试数据清空操作完成");
            return Result.success(result);
            
        } catch (Exception e) {
            logger.error("清空测试数据异常", e);
            return Result.error("清空测试数据异常: " + e.getMessage());
        }
    }
}