package com.yxrobot.integration;

import com.yxrobot.dto.CharityActivityDTO;
import com.yxrobot.dto.PageResult;
import com.yxrobot.service.CharityActivityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 公益活动查询功能测试
 * 验证查询功能在实际环境中能正常工作
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-18
 */
@SpringBootTest
@ActiveProfiles("test")
class CharityActivityQueryFunctionalTest {

    @Autowired
    private CharityActivityService charityActivityService;

    @Test
    void testSelectByQuery_BasicFunctionality() {
        // 测试基本查询功能能正常执行
        try {
            PageResult<CharityActivityDTO> result = charityActivityService.getCharityActivities(
                null, null, null, null, null, 1, 10);
            
            assertNotNull(result, "查询结果不应该为null");
            assertNotNull(result.getList(), "查询结果列表不应该为null");
            assertTrue(result.getTotal() >= 0, "总数应该大于等于0");
            assertTrue(result.getPage() > 0, "页码应该大于0");
            assertTrue(result.getSize() > 0, "每页大小应该大于0");
            
            System.out.println("✓ selectByQuery方法能正确执行");
            System.out.println("✓ 查询结果总数: " + result.getTotal());
            System.out.println("✓ 当前页数据量: " + result.getList().size());
            
        } catch (Exception e) {
            fail("selectByQuery方法执行失败: " + e.getMessage());
        }
    }

    @Test
    void testSelectByQuery_WithPagination() {
        // 测试分页查询功能
        try {
            PageResult<CharityActivityDTO> result = charityActivityService.getCharityActivities(
                null, null, null, null, null, 1, 5);
            
            assertNotNull(result);
            assertEquals(1, result.getPage(), "页码应该正确");
            assertEquals(5, result.getSize(), "每页大小应该正确");
            assertTrue(result.getList().size() <= 5, "返回的数据量不应该超过每页大小");
            
            System.out.println("✓ 分页查询功能正常工作");
            
        } catch (Exception e) {
            fail("分页查询功能测试失败: " + e.getMessage());
        }
    }

    @Test
    void testSelectByQuery_WithFilters() {
        // 测试带过滤条件的查询
        try {
            // 测试类型过滤
            PageResult<CharityActivityDTO> result = charityActivityService.getCharityActivities(
                null, "donation", null, null, null, 1, 10);
            
            assertNotNull(result);
            
            // 验证返回的活动都是指定类型（如果有数据的话）
            for (CharityActivityDTO activity : result.getList()) {
                assertEquals("donation", activity.getType(), "返回的活动类型应该匹配过滤条件");
            }
            
            System.out.println("✓ 类型过滤查询功能正常工作");
            
        } catch (Exception e) {
            fail("过滤查询功能测试失败: " + e.getMessage());
        }
    }

    @Test
    void testSelectById_Functionality() {
        // 测试根据ID查询功能
        try {
            // 测试查询不存在的ID
            CharityActivityDTO result = charityActivityService.getCharityActivityById(99999L);
            assertNull(result, "查询不存在的ID应该返回null");
            
            System.out.println("✓ selectById方法能正确执行");
            
        } catch (Exception e) {
            fail("selectById方法测试失败: " + e.getMessage());
        }
    }

    @Test
    void testTimestampFieldsMapping() {
        // 测试时间戳字段映射功能
        try {
            PageResult<CharityActivityDTO> result = charityActivityService.getCharityActivities(
                null, null, null, null, null, 1, 1);
            
            assertNotNull(result);
            
            // 如果有数据，验证时间戳字段
            if (!result.getList().isEmpty()) {
                CharityActivityDTO activity = result.getList().get(0);
                // 时间戳字段可能为null（如果数据库中没有设置），但不应该抛出异常
                // 这里主要验证字段映射不会导致SQL错误
                System.out.println("✓ 时间戳字段映射正常，createTime: " + activity.getCreateTime());
                System.out.println("✓ 时间戳字段映射正常，updateTime: " + activity.getUpdateTime());
            }
            
            System.out.println("✓ 时间戳字段在API响应中正确返回");
            
        } catch (Exception e) {
            fail("时间戳字段映射测试失败: " + e.getMessage());
        }
    }

    @Test
    void testQueryPerformance() {
        // 测试查询性能
        long startTime = System.currentTimeMillis();
        
        try {
            PageResult<CharityActivityDTO> result = charityActivityService.getCharityActivities(
                null, null, null, null, null, 1, 20);
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            assertNotNull(result);
            assertTrue(duration < 5000, "查询时间应该在合理范围内（5秒以内）");
            
            System.out.println("✓ 查询性能正常，耗时: " + duration + "ms");
            
        } catch (Exception e) {
            fail("查询性能测试失败: " + e.getMessage());
        }
    }

    @Test
    void testQueryWithSorting() {
        // 测试按时间排序的查询逻辑
        try {
            PageResult<CharityActivityDTO> result = charityActivityService.getCharityActivities(
                null, null, null, null, null, 1, 10);
            
            assertNotNull(result);
            
            // 如果有多条数据，验证排序
            if (result.getList().size() > 1) {
                for (int i = 0; i < result.getList().size() - 1; i++) {
                    CharityActivityDTO current = result.getList().get(i);
                    CharityActivityDTO next = result.getList().get(i + 1);
                    
                    // 验证排序逻辑（按日期倒序，然后按创建时间倒序）
                    if (current.getDate() != null && next.getDate() != null) {
                        assertTrue(
                            current.getDate().isAfter(next.getDate()) ||
                            current.getDate().equals(next.getDate()),
                            "活动应该按日期倒序排列"
                        );
                    }
                }
            }
            
            System.out.println("✓ 按时间排序的查询逻辑正常工作");
            
        } catch (Exception e) {
            fail("排序查询测试失败: " + e.getMessage());
        }
    }
}