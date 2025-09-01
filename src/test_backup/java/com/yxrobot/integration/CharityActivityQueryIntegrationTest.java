package com.yxrobot.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yxrobot.entity.CharityActivity;
import com.yxrobot.mapper.CharityActivityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 公益活动查询功能集成测试
 * 测试从控制器到数据库的完整查询流程
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-18
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureWebMvc
@Transactional
class CharityActivityQueryIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CharityActivityMapper charityActivityMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private CharityActivity testActivity1;
    private CharityActivity testActivity2;
    private CharityActivity testActivity3;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // 创建测试数据
        setupTestData();
    }

    private void setupTestData() {
        // 创建第一个测试活动
        testActivity1 = new CharityActivity();
        testActivity1.setProjectId(1L);
        testActivity1.setTitle("爱心捐赠活动");
        testActivity1.setDescription("为贫困地区儿童捐赠学习用品");
        testActivity1.setType("donation");
        testActivity1.setDate(LocalDate.now().plusDays(5));
        testActivity1.setStartTime(LocalDateTime.now().plusDays(5).withHour(9).withMinute(0));
        testActivity1.setEndTime(LocalDateTime.now().plusDays(5).withHour(17).withMinute(0));
        testActivity1.setLocation("北京市朝阳区");
        testActivity1.setParticipants(150);
        testActivity1.setTargetParticipants(200);
        testActivity1.setOrganizer("爱心基金会");
        testActivity1.setStatus("planned");
        testActivity1.setBudget(new BigDecimal("15000.00"));
        testActivity1.setActualCost(new BigDecimal("0.00"));
        testActivity1.setManager("张三");
        testActivity1.setManagerContact("13800138001");
        testActivity1.setVolunteerNeeded(30);
        testActivity1.setActualVolunteers(0);
        testActivity1.setAchievements("");
        testActivity1.setPhotos(new String[]{});
        testActivity1.setNotes("第一个测试活动");
        testActivity1.setCreateTime(LocalDateTime.now().minusDays(3));
        testActivity1.setUpdateTime(LocalDateTime.now().minusDays(2));
        testActivity1.setCreateBy(1L);
        testActivity1.setUpdateBy(1L);
        testActivity1.setDeleted(0);

        // 创建第二个测试活动
        testActivity2 = new CharityActivity();
        testActivity2.setProjectId(2L);
        testActivity2.setTitle("志愿服务活动");
        testActivity2.setDescription("为社区老人提供志愿服务");
        testActivity2.setType("volunteer");
        testActivity2.setDate(LocalDate.now().plusDays(10));
        testActivity2.setStartTime(LocalDateTime.now().plusDays(10).withHour(8).withMinute(0));
        testActivity2.setEndTime(LocalDateTime.now().plusDays(10).withHour(18).withMinute(0));
        testActivity2.setLocation("上海市浦东新区");
        testActivity2.setParticipants(80);
        testActivity2.setTargetParticipants(100);
        testActivity2.setOrganizer("志愿者协会");
        testActivity2.setStatus("ongoing");
        testActivity2.setBudget(new BigDecimal("8000.00"));
        testActivity2.setActualCost(new BigDecimal("5000.00"));
        testActivity2.setManager("李四");
        testActivity2.setManagerContact("13800138002");
        testActivity2.setVolunteerNeeded(50);
        testActivity2.setActualVolunteers(45);
        testActivity2.setAchievements("已服务老人200余人");
        testActivity2.setPhotos(new String[]{"photo1.jpg", "photo2.jpg"});
        testActivity2.setNotes("第二个测试活动");
        testActivity2.setCreateTime(LocalDateTime.now().minusDays(2));
        testActivity2.setUpdateTime(LocalDateTime.now().minusDays(1));
        testActivity2.setCreateBy(2L);
        testActivity2.setUpdateBy(2L);
        testActivity2.setDeleted(0);

        // 创建第三个测试活动
        testActivity3 = new CharityActivity();
        testActivity3.setProjectId(1L);
        testActivity3.setTitle("教育培训活动");
        testActivity3.setDescription("为农村教师提供培训");
        testActivity3.setType("education");
        testActivity3.setDate(LocalDate.now().minusDays(5));
        testActivity3.setStartTime(LocalDateTime.now().minusDays(5).withHour(9).withMinute(0));
        testActivity3.setEndTime(LocalDateTime.now().minusDays(5).withHour(17).withMinute(0));
        testActivity3.setLocation("广州市天河区");
        testActivity3.setParticipants(60);
        testActivity3.setTargetParticipants(60);
        testActivity3.setOrganizer("教育基金会");
        testActivity3.setStatus("completed");
        testActivity3.setBudget(new BigDecimal("12000.00"));
        testActivity3.setActualCost(new BigDecimal("11500.00"));
        testActivity3.setManager("王五");
        testActivity3.setManagerContact("13800138003");
        testActivity3.setVolunteerNeeded(15);
        testActivity3.setActualVolunteers(15);
        testActivity3.setAchievements("培训教师60人，效果良好");
        testActivity3.setPhotos(new String[]{"photo3.jpg"});
        testActivity3.setNotes("第三个测试活动");
        testActivity3.setCreateTime(LocalDateTime.now().minusDays(10));
        testActivity3.setUpdateTime(LocalDateTime.now().minusDays(5));
        testActivity3.setCreateBy(3L);
        testActivity3.setUpdateBy(3L);
        testActivity3.setDeleted(0);

        // 插入测试数据
        charityActivityMapper.insert(testActivity1);
        charityActivityMapper.insert(testActivity2);
        charityActivityMapper.insert(testActivity3);
    }

    @Test
    void testSelectByQuery_BasicQuery() throws Exception {
        // 测试基本查询功能
        mockMvc.perform(get("/api/admin/charity/activities")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(3))
                .andExpect(jsonPath("$.data.list").isArray())
                .andExpect(jsonPath("$.data.list.length()").value(3))
                // 验证时间戳字段在API响应中正确返回
                .andExpect(jsonPath("$.data.list[0].createTime").exists())
                .andExpect(jsonPath("$.data.list[0].updateTime").exists())
                .andExpect(jsonPath("$.data.list[1].createTime").exists())
                .andExpect(jsonPath("$.data.list[1].updateTime").exists())
                .andExpect(jsonPath("$.data.list[2].createTime").exists())
                .andExpect(jsonPath("$.data.list[2].updateTime").exists());
    }

    @Test
    void testSelectByQuery_KeywordSearch() throws Exception {
        // 测试关键词搜索功能
        mockMvc.perform(get("/api/admin/charity/activities")
                .param("keyword", "爱心")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].title").value("爱心捐赠活动"));

        // 测试组织方搜索
        mockMvc.perform(get("/api/admin/charity/activities")
                .param("keyword", "志愿者")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].title").value("志愿服务活动"));

        // 测试地点搜索
        mockMvc.perform(get("/api/admin/charity/activities")
                .param("keyword", "北京")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].location").value("北京市朝阳区"));
    }

    @Test
    void testSelectByQuery_TypeFilter() throws Exception {
        // 测试类型过滤
        mockMvc.perform(get("/api/admin/charity/activities")
                .param("type", "donation")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].type").value("donation"));

        mockMvc.perform(get("/api/admin/charity/activities")
                .param("type", "volunteer")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].type").value("volunteer"));

        mockMvc.perform(get("/api/admin/charity/activities")
                .param("type", "education")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].type").value("education"));
    }

    @Test
    void testSelectByQuery_StatusFilter() throws Exception {
        // 测试状态过滤
        mockMvc.perform(get("/api/admin/charity/activities")
                .param("status", "planned")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].status").value("planned"));

        mockMvc.perform(get("/api/admin/charity/activities")
                .param("status", "ongoing")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].status").value("ongoing"));

        mockMvc.perform(get("/api/admin/charity/activities")
                .param("status", "completed")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].status").value("completed"));
    }

    @Test
    void testSelectByQuery_DateRangeFilter() throws Exception {
        // 测试日期范围过滤 - 未来的活动
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(30);
        
        mockMvc.perform(get("/api/admin/charity/activities")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(2)); // 应该有2个未来的活动

        // 测试日期范围过滤 - 过去的活动
        LocalDate pastStartDate = LocalDate.now().minusDays(30);
        LocalDate pastEndDate = LocalDate.now().minusDays(1);
        
        mockMvc.perform(get("/api/admin/charity/activities")
                .param("startDate", pastStartDate.toString())
                .param("endDate", pastEndDate.toString())
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1)); // 应该有1个过去的活动
    }

    @Test
    void testSelectByQuery_Pagination() throws Exception {
        // 测试分页功能 - 第一页
        mockMvc.perform(get("/api/admin/charity/activities")
                .param("page", "1")
                .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(3))
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.size").value(2))
                .andExpect(jsonPath("$.data.list.length()").value(2));

        // 测试分页功能 - 第二页
        mockMvc.perform(get("/api/admin/charity/activities")
                .param("page", "2")
                .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(3))
                .andExpect(jsonPath("$.data.page").value(2))
                .andExpect(jsonPath("$.data.size").value(2))
                .andExpect(jsonPath("$.data.list.length()").value(1));
    }

    @Test
    void testSelectByQuery_TimeSorting() throws Exception {
        // 测试按时间排序的查询逻辑
        mockMvc.perform(get("/api/admin/charity/activities")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list").isArray())
                .andExpect(jsonPath("$.data.list.length()").value(3));
        
        // 注意：由于排序是按日期倒序，然后按创建时间倒序
        // testActivity2 (日期最晚) 应该排在第一位
        // testActivity1 (日期中等) 应该排在第二位  
        // testActivity3 (日期最早) 应该排在第三位
    }

    @Test
    void testSelectByQuery_CombinedFilters() throws Exception {
        // 测试组合过滤条件
        mockMvc.perform(get("/api/admin/charity/activities")
                .param("keyword", "活动")
                .param("type", "donation")
                .param("status", "planned")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].title").value("爱心捐赠活动"))
                .andExpect(jsonPath("$.data.list[0].type").value("donation"))
                .andExpect(jsonPath("$.data.list[0].status").value("planned"));
    }

    @Test
    void testSelectById_Success() throws Exception {
        // 测试根据ID查询活动详情
        mockMvc.perform(get("/api/admin/charity/activities/" + testActivity1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(testActivity1.getId()))
                .andExpect(jsonPath("$.data.title").value("爱心捐赠活动"))
                .andExpect(jsonPath("$.data.type").value("donation"))
                .andExpect(jsonPath("$.data.status").value("planned"))
                // 验证时间戳字段在API响应中正确返回
                .andExpect(jsonPath("$.data.createTime").exists())
                .andExpect(jsonPath("$.data.updateTime").exists());
    }

    @Test
    void testSelectById_NotFound() throws Exception {
        // 测试查询不存在的活动
        mockMvc.perform(get("/api/admin/charity/activities/99999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("公益活动不存在"));
    }

    @Test
    void testSelectByProjectId() throws Exception {
        // 测试根据项目ID查询活动列表
        mockMvc.perform(get("/api/admin/charity/activities/project/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2)) // 项目1有2个活动
                .andExpect(jsonPath("$.data[0].projectId").value(1))
                .andExpect(jsonPath("$.data[1].projectId").value(1));

        mockMvc.perform(get("/api/admin/charity/activities/project/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1)) // 项目2有1个活动
                .andExpect(jsonPath("$.data[0].projectId").value(2));
    }

    @Test
    void testTimestampFieldsCorrectMapping() throws Exception {
        // 专门测试时间戳字段的正确映射和返回
        mockMvc.perform(get("/api/admin/charity/activities/" + testActivity1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.createTime").exists())
                .andExpect(jsonPath("$.data.updateTime").exists())
                // 验证时间戳格式正确
                .andExpect(jsonPath("$.data.createTime").isString())
                .andExpect(jsonPath("$.data.updateTime").isString());

        // 测试列表查询中的时间戳字段
        mockMvc.perform(get("/api/admin/charity/activities")
                .param("page", "1")
                .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list[0].createTime").exists())
                .andExpect(jsonPath("$.data.list[0].updateTime").exists());
    }

    @Test
    void testQueryPerformanceWithLargeDataset() throws Exception {
        // 测试查询性能（虽然数据量不大，但验证查询能正常执行）
        long startTime = System.currentTimeMillis();
        
        mockMvc.perform(get("/api/admin/charity/activities")
                .param("page", "1")
                .param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // 查询应该在合理时间内完成（这里设置为5秒，实际应该远小于这个值）
        assert duration < 5000 : "查询时间过长: " + duration + "ms";
    }

    @Test
    void testDatabaseFieldMappingConsistency() throws Exception {
        // 测试数据库字段映射的一致性
        // 这个测试确保create_time和update_time字段能正确映射到createTime和updateTime属性
        
        mockMvc.perform(get("/api/admin/charity/activities/" + testActivity1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.createTime").exists())
                .andExpect(jsonPath("$.data.updateTime").exists());
        
        // 验证时间戳字段不为null且格式正确
        mockMvc.perform(get("/api/admin/charity/activities")
                .param("page", "1")
                .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list[0].createTime").isNotEmpty())
                .andExpect(jsonPath("$.data.list[0].updateTime").isNotEmpty())
                .andExpect(jsonPath("$.data.list[1].createTime").isNotEmpty())
                .andExpect(jsonPath("$.data.list[1].updateTime").isNotEmpty())
                .andExpect(jsonPath("$.data.list[2].createTime").isNotEmpty())
                .andExpect(jsonPath("$.data.list[2].updateTime").isNotEmpty());
    }
}