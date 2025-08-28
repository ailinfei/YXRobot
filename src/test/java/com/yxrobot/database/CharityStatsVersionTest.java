package com.yxrobot.database;

import com.yxrobot.entity.CharityStats;
import com.yxrobot.mapper.CharityStatsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * CharityStats版本字段功能测试
 */
@SpringBootTest
public class CharityStatsVersionTest {

    @Autowired
    private CharityStatsMapper charityStatsMapper;

    @Test
    public void testCharityStatsWithVersion() {
        System.out.println("=== 测试CharityStats版本字段功能 ===");
        
        try {
            // 1. 测试查询最新统计数据
            CharityStats latestStats = charityStatsMapper.selectLatest();
            
            if (latestStats != null) {
                System.out.printf("✅ 查询到最新统计数据，ID: %d, 版本: %d%n", 
                    latestStats.getId(), latestStats.getVersion());
                
                // 2. 测试版本字段更新
                Integer originalVersion = latestStats.getVersion();
                latestStats.setVersion(originalVersion + 1);
                latestStats.setUpdateTime(LocalDateTime.now());
                
                int updateResult = charityStatsMapper.updateById(latestStats);
                
                if (updateResult > 0) {
                    System.out.println("✅ 版本字段更新成功");
                    
                    // 3. 验证版本号是否正确更新
                    CharityStats updatedStats = charityStatsMapper.selectById(latestStats.getId());
                    if (updatedStats != null && updatedStats.getVersion().equals(originalVersion + 1)) {
                        System.out.printf("✅ 版本号验证成功，从 %d 更新到 %d%n", 
                            originalVersion, updatedStats.getVersion());
                    } else {
                        System.out.println("❌ 版本号验证失败");
                    }
                } else {
                    System.out.println("❌ 版本字段更新失败");
                }
                
            } else {
                System.out.println("ℹ️ 没有找到统计数据，创建测试数据...");
                createTestData();
            }
            
            // 4. 测试乐观锁功能
            testOptimisticLocking();
            
        } catch (Exception e) {
            System.out.println("❌ 测试失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void createTestData() {
        System.out.println("\n=== 创建测试数据 ===");
        
        try {
            CharityStats testStats = new CharityStats();
            testStats.setTotalBeneficiaries(1000);
            testStats.setTotalInstitutions(50);
            testStats.setCooperatingInstitutions(30);
            testStats.setTotalVolunteers(200);
            testStats.setTotalRaised(new BigDecimal("100000.00"));
            testStats.setTotalDonated(new BigDecimal("80000.00"));
            testStats.setTotalProjects(25);
            testStats.setActiveProjects(10);
            testStats.setCompletedProjects(15);
            testStats.setTotalActivities(100);
            testStats.setThisMonthActivities(5);
            testStats.setCreateTime(LocalDateTime.now());
            testStats.setUpdateTime(LocalDateTime.now());
            testStats.setDeleted(0);
            testStats.setVersion(1);
            
            int insertResult = charityStatsMapper.insert(testStats);
            
            if (insertResult > 0) {
                System.out.printf("✅ 测试数据创建成功，ID: %d, 版本: %d%n", 
                    testStats.getId(), testStats.getVersion());
            } else {
                System.out.println("❌ 测试数据创建失败");
            }
            
        } catch (Exception e) {
            System.out.println("❌ 创建测试数据失败：" + e.getMessage());
        }
    }
    
    private void testOptimisticLocking() {
        System.out.println("\n=== 测试乐观锁功能 ===");
        
        try {
            CharityStats stats = charityStatsMapper.selectLatest();
            
            if (stats != null) {
                Integer originalVersion = stats.getVersion();
                
                // 模拟乐观锁更新
                stats.setTotalBeneficiaries(stats.getTotalBeneficiaries() + 10);
                stats.setUpdateTime(LocalDateTime.now());
                
                // 使用updateByVersion方法（应该包含乐观锁逻辑）
                int updateResult = charityStatsMapper.updateByVersion(stats);
                
                if (updateResult > 0) {
                    System.out.println("✅ 乐观锁更新成功");
                    
                    // 验证版本号是否自动递增
                    CharityStats updatedStats = charityStatsMapper.selectById(stats.getId());
                    if (updatedStats != null && updatedStats.getVersion() > originalVersion) {
                        System.out.printf("✅ 乐观锁版本号自动递增，从 %d 到 %d%n", 
                            originalVersion, updatedStats.getVersion());
                    } else {
                        System.out.println("❌ 乐观锁版本号未正确递增");
                    }
                } else {
                    System.out.println("❌ 乐观锁更新失败");
                }
                
                // 测试版本冲突情况
                testVersionConflict(stats);
                
            } else {
                System.out.println("ℹ️ 没有数据可用于乐观锁测试");
            }
            
        } catch (Exception e) {
            System.out.println("❌ 乐观锁测试失败：" + e.getMessage());
        }
    }
    
    private void testVersionConflict(CharityStats stats) {
        System.out.println("\n=== 测试版本冲突 ===");
        
        try {
            // 使用过期的版本号尝试更新
            CharityStats conflictStats = new CharityStats();
            conflictStats.setId(stats.getId());
            conflictStats.setVersion(stats.getVersion() - 1); // 使用旧版本号
            conflictStats.setTotalBeneficiaries(stats.getTotalBeneficiaries() + 100);
            conflictStats.setUpdateTime(LocalDateTime.now());
            
            int conflictResult = charityStatsMapper.updateByVersion(conflictStats);
            
            if (conflictResult == 0) {
                System.out.println("✅ 版本冲突检测成功，更新被拒绝");
            } else {
                System.out.println("❌ 版本冲突检测失败，应该拒绝更新");
            }
            
        } catch (Exception e) {
            System.out.println("❌ 版本冲突测试失败：" + e.getMessage());
        }
    }
}