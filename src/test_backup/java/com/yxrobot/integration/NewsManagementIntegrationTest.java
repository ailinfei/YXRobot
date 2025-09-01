package com.yxrobot.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yxrobot.dto.NewsDTO;
import com.yxrobot.dto.NewsFormDTO;
import com.yxrobot.entity.NewsStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 新闻管理系统集成测试
 * 测试完整的新闻管理功能流程
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class NewsManagementIntegrationTest {
    
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private MockMvc mockMvc;
    private Long createdNewsId;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    
    /**
     * 测试1：获取新闻分类列表
     */
    @Test
    @Order(1)
    void testGetNewsCategories() throws Exception {
        mockMvc.perform(get("/api/news/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.data[0].name").exists())
                .andExpect(jsonPath("$.data[0].description").exists());
    }
    
    /**
     * 测试2：获取新闻标签列表
     */
    @Test
    @Order(2)
    void testGetNewsTags() throws Exception {
        mockMvc.perform(get("/api/news/tags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.data[0].name").exists())
                .andExpect(jsonPath("$.data[0].color").exists());
    }
    
    /**
     * 测试3：创建新闻
     */
    @Test
    @Order(3)
    void testCreateNews() throws Exception {
        NewsFormDTO newsForm = new NewsFormDTO();
        newsForm.setTitle("集成测试新闻标题");
        newsForm.setExcerpt("这是一条集成测试新闻的摘要");
        newsForm.setContent("这是集成测试新闻的详细内容，用于验证新闻创建功能是否正常工作。");
        newsForm.setCategoryId(1L);
        newsForm.setAuthor("集成测试");
        newsForm.setStatus(NewsStatus.DRAFT);
        newsForm.setIsFeatured(false);
        newsForm.setSortOrder(0);
        
        MvcResult result = mockMvc.perform(post("/api/news")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newsForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.title").value("集成测试新闻标题"))
                .andExpect(jsonPath("$.data.status").value("DRAFT"))
                .andReturn();
        
        // 保存创建的新闻ID用于后续测试
        String responseContent = result.getResponse().getContentAsString();
        var responseMap = objectMapper.readValue(responseContent, java.util.Map.class);
        var dataMap = (java.util.Map<String, Object>) responseMap.get("data");
        createdNewsId = Long.valueOf(dataMap.get("id").toString());
        
        assertNotNull(createdNewsId);
    }
    
    /**
     * 测试4：获取新闻详情
     */
    @Test
    @Order(4)
    void testGetNewsDetail() throws Exception {
        // 先创建一条新闻用于测试
        testCreateNews();
        
        mockMvc.perform(get("/api/news/{id}", createdNewsId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(createdNewsId))
                .andExpect(jsonPath("$.data.title").value("集成测试新闻标题"))
                .andExpect(jsonPath("$.data.author").value("集成测试"));
    }
    
    /**
     * 测试5：更新新闻
     */
    @Test
    @Order(5)
    void testUpdateNews() throws Exception {
        // 先创建一条新闻用于测试
        testCreateNews();
        
        NewsFormDTO updateForm = new NewsFormDTO();
        updateForm.setTitle("更新后的新闻标题");
        updateForm.setExcerpt("更新后的新闻摘要");
        updateForm.setContent("更新后的新闻内容");
        updateForm.setCategoryId(1L);
        updateForm.setAuthor("集成测试");
        updateForm.setStatus(NewsStatus.DRAFT);
        updateForm.setIsFeatured(true);
        updateForm.setSortOrder(10);
        
        mockMvc.perform(put("/api/news/{id}", createdNewsId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.title").value("更新后的新闻标题"))
                .andExpect(jsonPath("$.data.isFeatured").value(true))
                .andExpect(jsonPath("$.data.sortOrder").value(10));
    }
    
    /**
     * 测试6：发布新闻
     */
    @Test
    @Order(6)
    void testPublishNews() throws Exception {
        // 先创建一条新闻用于测试
        testCreateNews();
        
        mockMvc.perform(post("/api/news/{id}/publish", createdNewsId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("PUBLISHED"))
                .andExpect(jsonPath("$.data.publishTime").exists());
    }
    
    /**
     * 测试7：下线新闻
     */
    @Test
    @Order(7)
    void testOfflineNews() throws Exception {
        // 先创建并发布一条新闻
        testCreateNews();
        testPublishNews();
        
        mockMvc.perform(post("/api/news/{id}/offline", createdNewsId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("OFFLINE"));
    }
    
    /**
     * 测试8：获取新闻列表（分页）
     */
    @Test
    @Order(8)
    void testGetNewsList() throws Exception {
        mockMvc.perform(get("/api/news")
                .param("page", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list").isArray())
                .andExpect(jsonPath("$.data.total").exists())
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(10));
    }
    
    /**
     * 测试9：搜索新闻
     */
    @Test
    @Order(9)
    void testSearchNews() throws Exception {
        mockMvc.perform(get("/api/news")
                .param("keyword", "测试")
                .param("page", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list").isArray());
    }
    
    /**
     * 测试10：按分类筛选新闻
     */
    @Test
    @Order(10)
    void testFilterNewsByCategory() throws Exception {
        mockMvc.perform(get("/api/news")
                .param("categoryId", "1")
                .param("page", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list").isArray());
    }
    
    /**
     * 测试11：按状态筛选新闻
     */
    @Test
    @Order(11)
    void testFilterNewsByStatus() throws Exception {
        mockMvc.perform(get("/api/news")
                .param("status", "PUBLISHED")
                .param("page", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list").isArray());
    }
    
    /**
     * 测试12：获取新闻统计数据
     */
    @Test
    @Order(12)
    void testGetNewsStats() throws Exception {
        mockMvc.perform(get("/api/news/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalNews").exists())
                .andExpect(jsonPath("$.data.publishedNews").exists())
                .andExpect(jsonPath("$.data.draftNews").exists())
                .andExpect(jsonPath("$.data.offlineNews").exists());
    }
    
    /**
     * 测试13：记录新闻浏览
     */
    @Test
    @Order(13)
    void testRecordNewsView() throws Exception {
        // 先创建并发布一条新闻
        testCreateNews();
        testPublishNews();
        
        mockMvc.perform(post("/api/news/{id}/view", createdNewsId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
    
    /**
     * 测试14：新闻点赞
     */
    @Test
    @Order(14)
    void testLikeNews() throws Exception {
        // 先创建并发布一条新闻
        testCreateNews();
        testPublishNews();
        
        mockMvc.perform(post("/api/news/{id}/like", createdNewsId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
    
    /**
     * 测试15：批量操作新闻
     */
    @Test
    @Order(15)
    void testBatchOperateNews() throws Exception {
        // 先创建几条新闻用于测试
        testCreateNews();
        Long newsId1 = createdNewsId;
        
        testCreateNews();
        Long newsId2 = createdNewsId;
        
        var batchRequest = java.util.Map.of(
            "ids", java.util.List.of(newsId1, newsId2),
            "operation", "publish"
        );
        
        mockMvc.perform(post("/api/news/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(batchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
    
    /**
     * 测试16：删除新闻
     */
    @Test
    @Order(16)
    void testDeleteNews() throws Exception {
        // 先创建一条新闻用于测试
        testCreateNews();
        
        mockMvc.perform(delete("/api/news/{id}", createdNewsId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        
        // 验证新闻已被删除
        mockMvc.perform(get("/api/news/{id}", createdNewsId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }
    
    /**
     * 测试17：数据验证 - 创建无效新闻
     */
    @Test
    @Order(17)
    void testCreateInvalidNews() throws Exception {
        NewsFormDTO invalidForm = new NewsFormDTO();
        // 故意留空必填字段
        
        mockMvc.perform(post("/api/news")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidForm)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }
    
    /**
     * 测试18：异常处理 - 获取不存在的新闻
     */
    @Test
    @Order(18)
    void testGetNonExistentNews() throws Exception {
        mockMvc.perform(get("/api/news/{id}", 99999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.errorCode").value("NEWS_NOT_FOUND"));
    }
    
    /**
     * 测试19：状态转换验证
     */
    @Test
    @Order(19)
    void testInvalidStatusTransition() throws Exception {
        // 先创建一条新闻
        testCreateNews();
        
        // 尝试下线一条草稿状态的新闻（应该失败）
        mockMvc.perform(post("/api/news/{id}/offline", createdNewsId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }
    
    /**
     * 测试20：完整工作流程
     */
    @Test
    @Order(20)
    void testCompleteWorkflow() throws Exception {
        // 1. 创建新闻
        NewsFormDTO newsForm = new NewsFormDTO();
        newsForm.setTitle("完整流程测试新闻");
        newsForm.setExcerpt("完整流程测试摘要");
        newsForm.setContent("完整流程测试内容");
        newsForm.setCategoryId(1L);
        newsForm.setAuthor("流程测试");
        newsForm.setStatus(NewsStatus.DRAFT);
        newsForm.setIsFeatured(false);
        newsForm.setSortOrder(0);
        
        MvcResult createResult = mockMvc.perform(post("/api/news")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newsForm)))
                .andExpect(status().isOk())
                .andReturn();
        
        // 获取创建的新闻ID
        String createResponse = createResult.getResponse().getContentAsString();
        var createMap = objectMapper.readValue(createResponse, java.util.Map.class);
        var createData = (java.util.Map<String, Object>) createMap.get("data");
        Long workflowNewsId = Long.valueOf(createData.get("id").toString());
        
        // 2. 更新新闻
        newsForm.setTitle("更新后的完整流程测试新闻");
        mockMvc.perform(put("/api/news/{id}", workflowNewsId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newsForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("更新后的完整流程测试新闻"));
        
        // 3. 发布新闻
        mockMvc.perform(post("/api/news/{id}/publish", workflowNewsId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PUBLISHED"));
        
        // 4. 记录互动
        mockMvc.perform(post("/api/news/{id}/view", workflowNewsId))
                .andExpect(status().isOk());
        
        mockMvc.perform(post("/api/news/{id}/like", workflowNewsId))
                .andExpect(status().isOk());
        
        // 5. 验证统计数据更新
        mockMvc.perform(get("/api/news/{id}", workflowNewsId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.views").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.likes").value(greaterThanOrEqualTo(1)));
        
        // 6. 下线新闻
        mockMvc.perform(post("/api/news/{id}/offline", workflowNewsId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("OFFLINE"));
        
        // 7. 删除新闻
        mockMvc.perform(delete("/api/news/{id}", workflowNewsId))
                .andExpect(status().isOk());
        
        // 8. 验证新闻已删除
        mockMvc.perform(get("/api/news/{id}", workflowNewsId))
                .andExpect(status().isNotFound());
    }
}