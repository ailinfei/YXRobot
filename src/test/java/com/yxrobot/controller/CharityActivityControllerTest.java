package com.yxrobot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yxrobot.dto.CharityActivityDTO;
import com.yxrobot.dto.PageResult;
import com.yxrobot.service.CharityActivityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * CharityActivityController单元测试类
 * 测试公益活动控制器的查询API端点
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-18
 */
@WebMvcTest(CharityActivityController.class)
class CharityActivityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CharityActivityService charityActivityService;

    @Autowired
    private ObjectMapper objectMapper;

    private CharityActivityDTO testActivityDTO;
    private PageResult<CharityActivityDTO> testPageResult;

    @BeforeEach
    void setUp() {
        // 创建测试数据
        testActivityDTO = new CharityActivityDTO();
        testActivityDTO.setId(1L);
        testActivityDTO.setProjectId(1L);
        testActivityDTO.setProjectName("测试项目");
        testActivityDTO.setTitle("测试公益活动");
        testActivityDTO.setDescription("这是一个测试公益活动的描述");
        testActivityDTO.setType("donation");
        testActivityDTO.setTypeDisplay("捐赠");
        testActivityDTO.setDate(LocalDate.now().plusDays(7));
        testActivityDTO.setStartTime(LocalDateTime.now().plusDays(7).withHour(9).withMinute(0));
        testActivityDTO.setEndTime(LocalDateTime.now().plusDays(7).withHour(17).withMinute(0));
        testActivityDTO.setLocation("测试地点");
        testActivityDTO.setParticipants(100);
        testActivityDTO.setTargetParticipants(150);
        testActivityDTO.setOrganizer("测试组织方");
        testActivityDTO.setStatus("planned");
        testActivityDTO.setStatusDisplay("计划中");
        testActivityDTO.setBudget(new BigDecimal("10000.00"));
        testActivityDTO.setActualCost(new BigDecimal("0.00"));
        testActivityDTO.setManager("测试负责人");
        testActivityDTO.setManagerContact("13800138000");
        testActivityDTO.setVolunteerNeeded(20);
        testActivityDTO.setActualVolunteers(0);
        testActivityDTO.setAchievements("");
        testActivityDTO.setPhotos(new String[]{});
        testActivityDTO.setNotes("测试备注");
        testActivityDTO.setCreateTime(LocalDateTime.now().minusDays(1));
        testActivityDTO.setUpdateTime(LocalDateTime.now().minusHours(2));

        // 创建分页结果
        testPageResult = PageResult.of(Arrays.asList(testActivityDTO), 1L, 1, 20);
    }

    @Test
    void testGetCharityActivities_Success() throws Exception {
        // 准备测试数据
        when(charityActivityService.getCharityActivities(any(), any(), any(), any(), any(), any(), any()))
            .thenReturn(testPageResult);

        // 执行测试
        mockMvc.perform(get("/api/admin/charity/activities")
                .param("page", "1")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.size").value(20))
                .andExpect(jsonPath("$.data.list").isArray())
                .andExpect(jsonPath("$.data.list[0].id").value(1))
                .andExpect(jsonPath("$.data.list[0].title").value("测试公益活动"))
                .andExpect(jsonPath("$.data.list[0].type").value("donation"))
                .andExpect(jsonPath("$.data.list[0].status").value("planned"))
                .andExpect(jsonPath("$.data.list[0].createTime").exists())
                .andExpect(jsonPath("$.data.list[0].updateTime").exists());
    }

    @Test
    void testGetCharityActivities_WithKeywordSearch() throws Exception {
        // 准备测试数据
        when(charityActivityService.getCharityActivities(eq("测试"), any(), any(), any(), any(), any(), any()))
            .thenReturn(testPageResult);

        // 执行测试
        mockMvc.perform(get("/api/admin/charity/activities")
                .param("keyword", "测试")
                .param("page", "1")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].title").value("测试公益活动"));
    }

    @Test
    void testGetCharityActivities_WithTypeAndStatusFilters() throws Exception {
        // 准备测试数据
        when(charityActivityService.getCharityActivities(any(), eq("donation"), eq("planned"), any(), any(), any(), any()))
            .thenReturn(testPageResult);

        // 执行测试
        mockMvc.perform(get("/api/admin/charity/activities")
                .param("type", "donation")
                .param("status", "planned")
                .param("page", "1")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list[0].type").value("donation"))
                .andExpect(jsonPath("$.data.list[0].status").value("planned"));
    }

    @Test
    void testGetCharityActivities_WithDateRange() throws Exception {
        // 准备测试数据
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(30);
        
        when(charityActivityService.getCharityActivities(any(), any(), any(), eq(startDate), eq(endDate), any(), any()))
            .thenReturn(testPageResult);

        // 执行测试
        mockMvc.perform(get("/api/admin/charity/activities")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .param("page", "1")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    void testGetCharityActivities_WithAllFilters() throws Exception {
        // 准备测试数据
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(30);
        
        when(charityActivityService.getCharityActivities(eq("测试"), eq("donation"), eq("planned"), 
                                                        eq(startDate), eq(endDate), eq(2), eq(10)))
            .thenReturn(testPageResult);

        // 执行测试
        mockMvc.perform(get("/api/admin/charity/activities")
                .param("keyword", "测试")
                .param("type", "donation")
                .param("status", "planned")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .param("page", "2")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    void testGetCharityActivities_EmptyResult() throws Exception {
        // 准备测试数据 - 空结果
        PageResult<CharityActivityDTO> emptyResult = PageResult.of(Collections.emptyList(), 0L, 1, 20);
        when(charityActivityService.getCharityActivities(any(), any(), any(), any(), any(), any(), any()))
            .thenReturn(emptyResult);

        // 执行测试
        mockMvc.perform(get("/api/admin/charity/activities")
                .param("page", "1")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(0))
                .andExpect(jsonPath("$.data.list").isArray())
                .andExpect(jsonPath("$.data.list").isEmpty());
    }

    @Test
    void testGetCharityActivities_DefaultPagination() throws Exception {
        // 准备测试数据
        when(charityActivityService.getCharityActivities(any(), any(), any(), any(), any(), eq(1), eq(20)))
            .thenReturn(testPageResult);

        // 执行测试 - 不传递分页参数，应该使用默认值
        mockMvc.perform(get("/api/admin/charity/activities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.size").value(20));
    }

    @Test
    void testGetCharityActivities_ServiceException() throws Exception {
        // 准备测试数据 - 模拟服务异常
        when(charityActivityService.getCharityActivities(any(), any(), any(), any(), any(), any(), any()))
            .thenThrow(new RuntimeException("数据库连接失败"));

        // 执行测试
        mockMvc.perform(get("/api/admin/charity/activities")
                .param("page", "1")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("获取公益活动列表失败: 数据库连接失败"));
    }

    @Test
    void testGetCharityActivities_IllegalArgumentException() throws Exception {
        // 准备测试数据 - 模拟参数异常
        when(charityActivityService.getCharityActivities(any(), any(), any(), any(), any(), any(), any()))
            .thenThrow(new IllegalArgumentException("无效的分页参数"));

        // 执行测试
        mockMvc.perform(get("/api/admin/charity/activities")
                .param("page", "0")
                .param("size", "-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("无效的分页参数"));
    }

    @Test
    void testGetCharityActivityById_Success() throws Exception {
        // 准备测试数据
        when(charityActivityService.getCharityActivityById(1L)).thenReturn(testActivityDTO);

        // 执行测试
        mockMvc.perform(get("/api/admin/charity/activities/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("测试公益活动"))
                .andExpect(jsonPath("$.data.type").value("donation"))
                .andExpect(jsonPath("$.data.status").value("planned"))
                .andExpect(jsonPath("$.data.createTime").exists())
                .andExpect(jsonPath("$.data.updateTime").exists());
    }

    @Test
    void testGetCharityActivityById_NotFound() throws Exception {
        // 准备测试数据
        when(charityActivityService.getCharityActivityById(999L)).thenReturn(null);

        // 执行测试
        mockMvc.perform(get("/api/admin/charity/activities/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("公益活动不存在"));
    }

    @Test
    void testGetCharityActivityById_IllegalArgumentException() throws Exception {
        // 准备测试数据 - 模拟参数异常
        when(charityActivityService.getCharityActivityById(any()))
            .thenThrow(new IllegalArgumentException("活动ID不能为空"));

        // 执行测试 - 使用有效的数字ID但会触发服务层异常
        mockMvc.perform(get("/api/admin/charity/activities/0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("活动ID不能为空"));
    }

    @Test
    void testGetActivitiesByProjectId_Success() throws Exception {
        // 准备测试数据
        List<CharityActivityDTO> projectActivities = Arrays.asList(testActivityDTO);
        when(charityActivityService.getActivitiesByProjectId(1L)).thenReturn(projectActivities);

        // 执行测试
        mockMvc.perform(get("/api/admin/charity/activities/project/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].projectId").value(1))
                .andExpect(jsonPath("$.data[0].title").value("测试公益活动"));
    }

    @Test
    void testGetActivitiesByProjectId_EmptyResult() throws Exception {
        // 准备测试数据 - 空结果
        when(charityActivityService.getActivitiesByProjectId(999L)).thenReturn(Collections.emptyList());

        // 执行测试
        mockMvc.perform(get("/api/admin/charity/activities/project/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testTimestampFieldsInApiResponse() throws Exception {
        // 准备测试数据 - 确保时间戳字段有值
        testActivityDTO.setCreateTime(LocalDateTime.of(2024, 12, 18, 10, 30, 0));
        testActivityDTO.setUpdateTime(LocalDateTime.of(2024, 12, 18, 15, 45, 30));
        
        when(charityActivityService.getCharityActivityById(1L)).thenReturn(testActivityDTO);

        // 执行测试
        mockMvc.perform(get("/api/admin/charity/activities/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.createTime").value("2024-12-18 10:30:00"))
                .andExpect(jsonPath("$.data.updateTime").value("2024-12-18 15:45:30"));
    }

    @Test
    void testQueryWithTimeSortingVerification() throws Exception {
        // 准备测试数据 - 多个活动，验证时间排序
        CharityActivityDTO activity1 = new CharityActivityDTO();
        activity1.setId(1L);
        activity1.setTitle("较早的活动");
        activity1.setDate(LocalDate.now().plusDays(5));
        activity1.setCreateTime(LocalDateTime.now().minusDays(2));
        activity1.setUpdateTime(LocalDateTime.now().minusDays(1));
        
        CharityActivityDTO activity2 = new CharityActivityDTO();
        activity2.setId(2L);
        activity2.setTitle("较晚的活动");
        activity2.setDate(LocalDate.now().plusDays(10));
        activity2.setCreateTime(LocalDateTime.now().minusDays(1));
        activity2.setUpdateTime(LocalDateTime.now().minusHours(1));
        
        PageResult<CharityActivityDTO> sortedResult = PageResult.of(Arrays.asList(activity2, activity1), 2L, 1, 20);
        when(charityActivityService.getCharityActivities(any(), any(), any(), any(), any(), any(), any()))
            .thenReturn(sortedResult);

        // 执行测试
        mockMvc.perform(get("/api/admin/charity/activities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(2))
                .andExpect(jsonPath("$.data.list[0].id").value(2))
                .andExpect(jsonPath("$.data.list[0].title").value("较晚的活动"))
                .andExpect(jsonPath("$.data.list[1].id").value(1))
                .andExpect(jsonPath("$.data.list[1].title").value("较早的活动"));
    }

    @Test
    void testPaginationFunctionality() throws Exception {
        // 准备测试数据 - 测试分页功能
        PageResult<CharityActivityDTO> page1 = PageResult.of(Arrays.asList(testActivityDTO), 25L, 1, 10);
        PageResult<CharityActivityDTO> page2 = PageResult.of(Arrays.asList(testActivityDTO), 25L, 2, 10);
        
        when(charityActivityService.getCharityActivities(any(), any(), any(), any(), any(), eq(1), eq(10)))
            .thenReturn(page1);
        when(charityActivityService.getCharityActivities(any(), any(), any(), any(), any(), eq(2), eq(10)))
            .thenReturn(page2);

        // 测试第一页
        mockMvc.perform(get("/api/admin/charity/activities")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.size").value(10))
                .andExpect(jsonPath("$.data.total").value(25));

        // 测试第二页
        mockMvc.perform(get("/api/admin/charity/activities")
                .param("page", "2")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.page").value(2))
                .andExpect(jsonPath("$.data.size").value(10))
                .andExpect(jsonPath("$.data.total").value(25));
    }

    @Test
    void testCorsHeaders() throws Exception {
        // 准备测试数据
        when(charityActivityService.getCharityActivities(any(), any(), any(), any(), any(), any(), any()))
            .thenReturn(testPageResult);

        // 执行测试 - 验证CORS头部
        mockMvc.perform(get("/api/admin/charity/activities")
                .header("Origin", "http://localhost:3000"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }

    @Test
    void testInvalidDateFormat() throws Exception {
        // 执行测试 - 传递无效的日期格式，Spring会返回500错误
        mockMvc.perform(get("/api/admin/charity/activities")
                .param("startDate", "invalid-date")
                .param("endDate", "2024-12-31"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500));
    }
}