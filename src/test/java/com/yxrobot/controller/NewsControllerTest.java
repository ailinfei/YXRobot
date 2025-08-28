package com.yxrobot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yxrobot.dto.NewsDTO;
import com.yxrobot.dto.NewsFormDTO;
import com.yxrobot.dto.NewsCategoryDTO;
import com.yxrobot.dto.NewsTagDTO;
import com.yxrobot.dto.NewsStatsDTO;
import com.yxrobot.entity.NewsStatus;
import com.yxrobot.exception.ExceptionMonitor;
import com.yxrobot.exception.NewsNotFoundException;
import com.yxrobot.exception.NewsValidationException;
import com.yxrobot.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * NewsController单元测试
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@WebMvcTest(NewsController.class)
class NewsControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private NewsService newsService;
    
    @MockBean
    private ExceptionMonitor exceptionMonitor;
    
    @MockBean
    private NewsCategoryService newsCategoryService;
    
    @MockBean
    private NewsTagService newsTagService;
    
    @MockBean
    private NewsStatsService newsStatsService;
    
    @MockBean
    private NewsFileUploadService newsFileUploadService;
    
    @MockBean
    private NewsInteractionService newsInteractionService;
    
    @MockBean
    private NewsSearchService newsSearchService;
    
    private NewsDTO testNewsDTO;
    private NewsFormDTO testNewsFormDTO;
    
    @BeforeEach
    void setUp() {
        // 准备测试数据
        testNewsDTO = new NewsDTO();
        testNewsDTO.setId(1L);
        testNewsDTO.setTitle("测试新闻标题");
        testNewsDTO.setContent("这是一个测试新闻的内容，内容长度必须超过50个字符以满足验证要求。这里添加更多内容来确保满足最小长度要求，让测试能够正常通过验证。");
        testNewsDTO.setExcerpt("测试新闻摘要");
        testNewsDTO.setCategoryId(1L);
        testNewsDTO.setAuthor("测试作者");
        testNewsDTO.setStatus(NewsStatus.DRAFT);
        testNewsDTO.setViews(0);
        testNewsDTO.setLikes(0);
        testNewsDTO.setComments(0);
        testNewsDTO.setIsFeatured(false);
        testNewsDTO.setSortOrder(0);
        testNewsDTO.setCreatedAt(LocalDateTime.now());
        testNewsDTO.setUpdatedAt(LocalDateTime.now());
        
        testNewsFormDTO = new NewsFormDTO();
        testNewsFormDTO.setTitle("测试新闻标题");
        testNewsFormDTO.setContent("这是一个测试新闻的内容，内容长度必须超过50个字符以满足验证要求。这里添加更多内容来确保满足最小长度要求，让测试能够正常通过验证。");
        testNewsFormDTO.setExcerpt("这是一个测试新闻摘要，长度超过10个字符以满足验证要求。");
        testNewsFormDTO.setCategoryId(1L);
        testNewsFormDTO.setAuthor("测试作者");
        testNewsFormDTO.setStatus(NewsStatus.DRAFT);
        testNewsFormDTO.setIsFeatured(false);
        testNewsFormDTO.setSortOrder(0);
    }
    
    @Test
    void testGetNewsList_Success() throws Exception {
        // 准备Mock数据
        Map<String, Object> result = new HashMap<>();
        result.put("list", Arrays.asList(testNewsDTO));
        result.put("total", 1);
        result.put("page", 1);
        result.put("pageSize", 10);
        result.put("totalPages", 1);
        
        when(newsService.getNewsList(1, 10, null, null, null, null, null)).thenReturn(result);
        
        // 执行测试
        mockMvc.perform(get("/api/news")
                .param("page", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].title").value("测试新闻标题"));
        
        // 验证Mock调用
        verify(newsService).getNewsList(1, 10, null, null, null, null, null);
    }
    
    @Test
    void testGetNewsById_Success() throws Exception {
        // 准备Mock数据
        when(newsService.getNewsById(1L)).thenReturn(testNewsDTO);
        
        // 执行测试
        mockMvc.perform(get("/api/news/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("测试新闻标题"));
        
        // 验证Mock调用
        verify(newsService).getNewsById(1L);
    }
    
    @Test
    void testGetNewsById_NotFound() throws Exception {
        // 准备Mock数据 - 新闻不存在
        when(newsService.getNewsById(1L)).thenThrow(new NewsNotFoundException(1L));
        
        // 执行测试
        mockMvc.perform(get("/api/news/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
        
        // 验证Mock调用
        verify(newsService).getNewsById(1L);
    }
    
    @Test
    void testCreateNews_Success() throws Exception {
        // 准备Mock数据
        when(newsService.createNews(any(NewsFormDTO.class))).thenReturn(testNewsDTO);
        
        // 执行测试
        mockMvc.perform(post("/api/news")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testNewsFormDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("创建成功"))
                .andExpect(jsonPath("$.data.title").value("测试新闻标题"));
        
        // 验证Mock调用
        verify(newsService).createNews(any(NewsFormDTO.class));
    }
    
    @Test
    void testCreateNews_ValidationError() throws Exception {
        // 创建一个无效的NewsFormDTO（标题太短）
        NewsFormDTO invalidNewsFormDTO = new NewsFormDTO();
        invalidNewsFormDTO.setTitle("短"); // 标题太短，不满足@Size(min = 5)
        invalidNewsFormDTO.setContent("短内容"); // 内容太短，不满足@Size(min = 50)
        invalidNewsFormDTO.setExcerpt("短摘要"); // 摘要太短，不满足@Size(min = 10)
        invalidNewsFormDTO.setCategoryId(1L);
        invalidNewsFormDTO.setAuthor("测试作者");
        
        // 执行测试 - 验证会在Controller层失败，不会调用Service
        mockMvc.perform(post("/api/news")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidNewsFormDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
        
        // 验证Service没有被调用（因为验证在Controller层就失败了）
        verify(newsService, never()).createNews(any(NewsFormDTO.class));
    }
    
    @Test
    void testUpdateNews_Success() throws Exception {
        // 准备Mock数据
        when(newsService.updateNews(eq(1L), any(NewsFormDTO.class))).thenReturn(testNewsDTO);
        
        // 执行测试
        mockMvc.perform(put("/api/news/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testNewsFormDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("更新成功"))
                .andExpect(jsonPath("$.data.title").value("测试新闻标题"));
        
        // 验证Mock调用
        verify(newsService).updateNews(eq(1L), any(NewsFormDTO.class));
    }
    
    @Test
    void testDeleteNews_Success() throws Exception {
        // 准备Mock数据
        doNothing().when(newsService).deleteNews(1L);
        
        // 执行测试
        mockMvc.perform(delete("/api/news/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("删除成功"));
        
        // 验证Mock调用
        verify(newsService).deleteNews(1L);
    }
    
    @Test
    void testBatchDeleteNews_Success() throws Exception {
        // 准备Mock数据
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        doNothing().when(newsService).batchDeleteNews(ids);
        
        // 执行测试
        mockMvc.perform(delete("/api/news/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("批量删除成功"));
        
        // 验证Mock调用
        verify(newsService).batchDeleteNews(ids);
    }
    
    @Test
    void testSearchNews_Success() throws Exception {
        // 准备Mock数据
        Map<String, Object> result = new HashMap<>();
        result.put("list", Arrays.asList(testNewsDTO));
        result.put("total", 1);
        result.put("page", 1);
        result.put("pageSize", 10);
        result.put("totalPages", 1);
        
        when(newsService.searchNews("测试", 1, 10)).thenReturn(result);
        
        // 执行测试
        mockMvc.perform(get("/api/news/search")
                .param("keyword", "测试")
                .param("page", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("搜索成功"))
                .andExpect(jsonPath("$.data.total").value(1));
        
        // 验证Mock调用
        verify(newsService).searchNews("测试", 1, 10);
    }
    
    @Test
    void testGetFeaturedNews_Success() throws Exception {
        // 准备Mock数据
        List<NewsDTO> featuredNews = Arrays.asList(testNewsDTO);
        when(newsService.getFeaturedNews(10)).thenReturn(featuredNews);
        
        // 执行测试
        mockMvc.perform(get("/api/news/featured")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data[0].title").value("测试新闻标题"));
        
        // 验证Mock调用
        verify(newsService).getFeaturedNews(10);
    }
    
    @Test
    void testGetHotNews_Success() throws Exception {
        // 准备Mock数据
        List<NewsDTO> hotNews = Arrays.asList(testNewsDTO);
        when(newsService.getHotNews(10)).thenReturn(hotNews);
        
        // 执行测试
        mockMvc.perform(get("/api/news/hot")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"));
        
        // 验证Mock调用
        verify(newsService).getHotNews(10);
    }
    
    @Test
    void testGetLatestNews_Success() throws Exception {
        // 准备Mock数据
        List<NewsDTO> latestNews = Arrays.asList(testNewsDTO);
        when(newsService.getLatestNews(10)).thenReturn(latestNews);
        
        // 执行测试
        mockMvc.perform(get("/api/news/latest")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"));
        
        // 验证Mock调用
        verify(newsService).getLatestNews(10);
    }
    
    @Test
    void testGetRelatedNews_Success() throws Exception {
        // 准备Mock数据
        List<NewsDTO> relatedNews = Arrays.asList(testNewsDTO);
        when(newsService.getRelatedNews(1L, 5)).thenReturn(relatedNews);
        
        // 执行测试
        mockMvc.perform(get("/api/news/1/related")
                .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"));
        
        // 验证Mock调用
        verify(newsService).getRelatedNews(1L, 5);
    }
    
    @Test
    void testRecordView_Success() throws Exception {
        // 准备Mock数据
        doNothing().when(newsInteractionService).recordView(eq(1L), isNull(), eq("127.0.0.1"), isNull());
        
        // 执行测试
        mockMvc.perform(post("/api/news/1/view"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("记录成功"));
        
        // 验证Mock调用
        verify(newsInteractionService).recordView(eq(1L), isNull(), eq("127.0.0.1"), isNull());
    }
    
    @Test
    void testRecordLike_Success() throws Exception {
        // 准备Mock数据
        doNothing().when(newsInteractionService).recordLike(eq(1L), isNull(), eq("127.0.0.1"), isNull());
        
        // 执行测试
        mockMvc.perform(post("/api/news/1/like"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("点赞成功"));
        
        // 验证Mock调用
        verify(newsInteractionService).recordLike(eq(1L), isNull(), eq("127.0.0.1"), isNull());
    }
    
    @Test
    void testGetInteractionStats_Success() throws Exception {
        // 准备Mock数据
        Map<String, Object> stats = new HashMap<>();
        stats.put("views", 100);
        stats.put("likes", 10);
        stats.put("comments", 5);
        stats.put("shares", 2);
        
        when(newsInteractionService.getInteractionStats(1L)).thenReturn(stats);
        
        // 执行测试
        mockMvc.perform(get("/api/news/1/interaction-stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.views").value(100))
                .andExpect(jsonPath("$.data.likes").value(10));
        
        // 验证Mock调用
        verify(newsInteractionService).getInteractionStats(1L);
    }
    
    @Test
    void testGetNewsCategories_Success() throws Exception {
        // 准备Mock数据
        NewsCategoryDTO categoryDTO = new NewsCategoryDTO();
        categoryDTO.setId(1L);
        categoryDTO.setName("测试分类");
        List<NewsCategoryDTO> categories = Arrays.asList(categoryDTO);
        
        when(newsCategoryService.getAllEnabledCategories()).thenReturn(categories);
        
        // 执行测试
        mockMvc.perform(get("/api/news/category-list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data[0].name").value("测试分类"));
        
        // 验证Mock调用
        verify(newsCategoryService).getAllEnabledCategories();
    }
    
    @Test
    void testGetNewsTags_Success() throws Exception {
        // 准备Mock数据
        NewsTagDTO tagDTO = new NewsTagDTO();
        tagDTO.setId(1L);
        tagDTO.setName("测试标签");
        List<NewsTagDTO> tags = Arrays.asList(tagDTO);
        
        when(newsTagService.getAllTags()).thenReturn(tags);
        
        // 执行测试
        mockMvc.perform(get("/api/news/tag-list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data[0].name").value("测试标签"));
        
        // 验证Mock调用
        verify(newsTagService).getAllTags();
    }
    
    @Test
    void testGetNewsStats_Success() throws Exception {
        // 准备Mock数据
        NewsStatsDTO statsDTO = new NewsStatsDTO();
        statsDTO.setTotalNews(100);
        statsDTO.setPublishedNews(80);
        statsDTO.setDraftNews(15);
        statsDTO.setOfflineNews(5);
        
        when(newsStatsService.getNewsStats()).thenReturn(statsDTO);
        
        // 执行测试
        mockMvc.perform(get("/api/news/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.totalNews").value(100));
        
        // 验证Mock调用
        verify(newsStatsService).getNewsStats();
    }
    
    @Test
    void testUploadNewsImage_Success() throws Exception {
        // 准备Mock数据
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "test image content".getBytes());
        
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("originalUrl", "/uploads/news/test.jpg");
        uploadResult.put("fileName", "test.jpg");
        uploadResult.put("fileSize", file.getSize());
        
        when(newsFileUploadService.uploadNewsImage(any())).thenReturn(uploadResult);
        
        // 执行测试
        mockMvc.perform(multipart("/api/news/upload-image")
                .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("上传成功"))
                .andExpect(jsonPath("$.data.fileName").value("test.jpg"));
        
        // 验证Mock调用
        verify(newsFileUploadService).uploadNewsImage(any());
    }
    
    @Test
    void testParameterValidation() throws Exception {
        // 测试无效的页码参数
        mockMvc.perform(get("/api/news")
                .param("page", "0"))
                .andExpect(status().isBadRequest());
        
        // 测试无效的每页大小参数
        mockMvc.perform(get("/api/news")
                .param("pageSize", "0"))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testInternalServerError() throws Exception {
        // 准备Mock数据 - 模拟内部错误
        when(newsService.getNewsList(anyInt(), anyInt(), any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("数据库连接失败"));
        
        // 执行测试
        mockMvc.perform(get("/api/news"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500));
        
        // 验证Mock调用
        verify(newsService).getNewsList(anyInt(), anyInt(), any(), any(), any(), any(), any());
    }
}