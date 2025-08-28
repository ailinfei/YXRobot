package com.yxrobot.mapper;

import com.yxrobot.entity.CharityActivity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CharityActivityMapper测试类
 * 测试公益活动数据访问层的查询功能
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-18
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CharityActivityMapperTest {

    @Autowired
    private CharityActivityMapper charityActivityMapper;

    private CharityActivity testActivity;

    @BeforeEach
    void setUp() {
        // 创建测试数据
        testActivity = new CharityActivity();
        testActivity.setProjectId(1L);
        testActivity.setTitle("测试公益活动");
        testActivity.setDescription("这是一个测试公益活动的描述");
        testActivity.setType("donation");
        testActivity.setDate(LocalDate.now().plusDays(7));
        testActivity.setStartTime(LocalDateTime.now().plusDays(7).withHour(9).withMinute(0));
        testActivity.setEndTime(LocalDateTime.now().plusDays(7).withHour(17).withMinute(0));
        testActivity.setLocation("测试地点");
        testActivity.setParticipants(100);
        testActivity.setTargetParticipants(150);
        testActivity.setOrganizer("测试组织方");
        testActivity.setStatus("planned");
        testActivity.setBudget(new BigDecimal("10000.00"));
        testActivity.setActualCost(new BigDecimal("0.00"));
        testActivity.setManager("测试负责人");
        testActivity.setManagerContact("13800138000");
        testActivity.setVolunteerNeeded(20);
        testActivity.setActualVolunteers(0);
        testActivity.setAchievements("");
        testActivity.setPhotos(new String[]{});
        testActivity.setNotes("测试备注");
        testActivity.setCreateTime(LocalDateTime.now());
        testActivity.setUpdateTime(LocalDateTime.now());
        testActivity.setCreateBy(1L);
        testActivity.setUpdateBy(1L);
        testActivity.setDeleted(0);

        // 插入测试数据
        charityActivityMapper.insert(testActivity);
    }

    @Test
    void testSelectByQuery_WithoutFilters() {
        // 测试不带任何过滤条件的查询
        List<CharityActivity> activities = charityActivityMapper.selectByQuery(
            null, null, null, null, null, 0, 10);
        
        assertNotNull(activities);
        assertTrue(activities.size() > 0);
        
        // 验证返回的活动包含我们插入的测试数据
        boolean foundTestActivity = activities.stream()
            .anyMatch(activity -> "测试公益活动".equals(activity.getTitle()));
        assertTrue(foundTestActivity, "应该能找到测试活动");
    }

    @Test
    void testSelectByQuery_WithKeyword() {
        // 测试关键词搜索
        List<CharityActivity> activities = charityActivityMapper.selectByQuery(
            "测试", null, null, null, null, 0, 10);
        
        assertNotNull(activities);
        assertTrue(activities.size() > 0);
        
        // 验证所有返回的活动都包含关键词
        boolean allContainKeyword = activities.stream()
            .allMatch(activity -> 
                activity.getTitle().contains("测试") ||
                activity.getOrganizer().contains("测试") ||
                activity.getLocation().contains("测试"));
        assertTrue(allContainKeyword, "所有返回的活动都应该包含关键词");
    }

    @Test
    void testSelectByQuery_WithTypeFilter() {
        // 测试类型过滤
        List<CharityActivity> activities = charityActivityMapper.selectByQuery(
            null, "donation", null, null, null, 0, 10);
        
        assertNotNull(activities);
        
        // 验证所有返回的活动都是指定类型
        boolean allMatchType = activities.stream()
            .allMatch(activity -> "donation".equals(activity.getType()));
        assertTrue(allMatchType, "所有返回的活动都应该是donation类型");
    }

    @Test
    void testSelectByQuery_WithStatusFilter() {
        // 测试状态过滤
        List<CharityActivity> activities = charityActivityMapper.selectByQuery(
            null, null, "planned", null, null, 0, 10);
        
        assertNotNull(activities);
        
        // 验证所有返回的活动都是指定状态
        boolean allMatchStatus = activities.stream()
            .allMatch(activity -> "planned".equals(activity.getStatus()));
        assertTrue(allMatchStatus, "所有返回的活动都应该是planned状态");
    }

    @Test
    void testSelectByQuery_WithDateRange() {
        // 测试日期范围过滤
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(30);
        
        List<CharityActivity> activities = charityActivityMapper.selectByQuery(
            null, null, null, startDate, endDate, 0, 10);
        
        assertNotNull(activities);
        
        // 验证所有返回的活动都在指定日期范围内
        boolean allInDateRange = activities.stream()
            .allMatch(activity -> 
                !activity.getDate().isBefore(startDate) && 
                !activity.getDate().isAfter(endDate));
        assertTrue(allInDateRange, "所有返回的活动都应该在指定日期范围内");
    }

    @Test
    void testSelectByQuery_WithPagination() {
        // 测试分页功能
        List<CharityActivity> firstPage = charityActivityMapper.selectByQuery(
            null, null, null, null, null, 0, 5);
        List<CharityActivity> secondPage = charityActivityMapper.selectByQuery(
            null, null, null, null, null, 5, 5);
        
        assertNotNull(firstPage);
        assertNotNull(secondPage);
        
        // 验证分页结果不重复
        boolean noOverlap = firstPage.stream()
            .noneMatch(activity1 -> secondPage.stream()
                .anyMatch(activity2 -> activity1.getId().equals(activity2.getId())));
        assertTrue(noOverlap, "分页结果不应该有重复");
    }

    @Test
    void testSelectByQuery_OrderByDateAndCreateTime() {
        // 测试排序功能（按日期和创建时间倒序）
        List<CharityActivity> activities = charityActivityMapper.selectByQuery(
            null, null, null, null, null, 0, 10);
        
        assertNotNull(activities);
        if (activities.size() > 1) {
            // 验证排序是否正确
            for (int i = 0; i < activities.size() - 1; i++) {
                CharityActivity current = activities.get(i);
                CharityActivity next = activities.get(i + 1);
                
                // 首先按日期倒序，然后按创建时间倒序
                assertTrue(
                    current.getDate().isAfter(next.getDate()) ||
                    (current.getDate().equals(next.getDate()) && 
                     !current.getCreateTime().isBefore(next.getCreateTime())),
                    "活动应该按日期和创建时间倒序排列"
                );
            }
        }
    }

    @Test
    void testCountByQuery_WithoutFilters() {
        // 测试计数功能
        Long count = charityActivityMapper.countByQuery(
            null, null, null, null, null);
        
        assertNotNull(count);
        assertTrue(count > 0, "应该有活动记录");
    }

    @Test
    void testCountByQuery_WithFilters() {
        // 测试带过滤条件的计数
        Long count = charityActivityMapper.countByQuery(
            "测试", "donation", "planned", null, null);
        
        assertNotNull(count);
        assertTrue(count > 0, "应该能找到匹配的活动");
    }

    @Test
    void testSelectById() {
        // 测试根据ID查询
        CharityActivity activity = charityActivityMapper.selectById(testActivity.getId());
        
        assertNotNull(activity);
        assertEquals(testActivity.getId(), activity.getId());
        assertEquals("测试公益活动", activity.getTitle());
        assertEquals("donation", activity.getType());
        assertEquals("planned", activity.getStatus());
        
        // 验证时间戳字段正确返回
        assertNotNull(activity.getCreateTime(), "创建时间应该不为空");
        assertNotNull(activity.getUpdateTime(), "更新时间应该不为空");
    }

    @Test
    void testSelectById_NotFound() {
        // 测试查询不存在的ID
        CharityActivity activity = charityActivityMapper.selectById(99999L);
        
        assertNull(activity, "不存在的ID应该返回null");
    }

    @Test
    void testSelectByProjectId() {
        // 测试根据项目ID查询
        List<CharityActivity> activities = charityActivityMapper.selectByProjectId(1L);
        
        assertNotNull(activities);
        assertTrue(activities.size() > 0);
        
        // 验证所有返回的活动都属于指定项目
        boolean allBelongToProject = activities.stream()
            .allMatch(activity -> Long.valueOf(1L).equals(activity.getProjectId()));
        assertTrue(allBelongToProject, "所有返回的活动都应该属于指定项目");
    }

    @Test
    void testTimestampFieldsInResultMapping() {
        // 专门测试时间戳字段的映射是否正确
        CharityActivity activity = charityActivityMapper.selectById(testActivity.getId());
        
        assertNotNull(activity);
        assertNotNull(activity.getCreateTime(), "create_time字段应该正确映射到createTime属性");
        assertNotNull(activity.getUpdateTime(), "update_time字段应该正确映射到updateTime属性");
        
        // 验证时间戳字段的值是否合理
        assertTrue(activity.getCreateTime().isBefore(LocalDateTime.now().plusMinutes(1)), 
                  "创建时间应该在当前时间之前");
        assertTrue(activity.getUpdateTime().isBefore(LocalDateTime.now().plusMinutes(1)), 
                  "更新时间应该在当前时间之前");
    }

    @Test
    void testQueryWithAllFiltersAndPagination() {
        // 测试所有过滤条件和分页的组合
        List<CharityActivity> activities = charityActivityMapper.selectByQuery(
            "测试", "donation", "planned", 
            LocalDate.now(), LocalDate.now().plusDays(30),
            0, 5);
        
        assertNotNull(activities);
        
        // 验证返回的活动符合所有条件
        for (CharityActivity activity : activities) {
            assertTrue(
                activity.getTitle().contains("测试") ||
                activity.getOrganizer().contains("测试") ||
                activity.getLocation().contains("测试"),
                "活动应该包含关键词"
            );
            assertEquals("donation", activity.getType(), "活动类型应该匹配");
            assertEquals("planned", activity.getStatus(), "活动状态应该匹配");
            assertTrue(
                !activity.getDate().isBefore(LocalDate.now()) &&
                !activity.getDate().isAfter(LocalDate.now().plusDays(30)),
                "活动日期应该在指定范围内"
            );
        }
        
        // 验证分页限制
        assertTrue(activities.size() <= 5, "返回的记录数不应该超过分页限制");
    }
}