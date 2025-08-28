package com.yxrobot.service;

import com.yxrobot.dto.NewsDTO;
import com.yxrobot.dto.NewsFormDTO;
import com.yxrobot.entity.News;
import com.yxrobot.entity.NewsCategory;
import com.yxrobot.entity.NewsStatus;
import com.yxrobot.exception.NewsNotFoundException;
import com.yxrobot.exception.NewsValidationException;
import com.yxrobot.mapper.NewsCategoryMapper;
import com.yxrobot.mapper.NewsMapper;
import com.yxrobot.mapper.NewsTagMapper;
import com.yxrobot.mapper.NewsTagRelationMapper;
import com.yxrobot.validation.NewsValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * NewsService单元测试
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@ExtendWith(MockitoExtension.class)
class NewsServiceTest {
    
    @Mock
    private NewsMapper newsMapper;
    
    @Mock
    private NewsCategoryMapper newsCategoryMapper;
    
    @Mock
    private NewsTagMapper newsTagMapper;
    
    @Mock
    private NewsTagRelationMapper newsTagRelationMapper;
    
    @Mock
    private NewsValidator newsValidator;
    
    @InjectMocks
    private NewsService newsService;
    
    private News testNews;
    private NewsFormDTO testNewsFormDTO;
    private NewsCategory testCategory;
    
    @BeforeEach
    void setUp() {
        // 准备测试数据
        testNews = new News();
        testNews.setId(1L);
        testNews.setTitle("测试新闻标题");
        testNews.setContent("这是一个测试新闻的内容，内容长度超过50个字符以满足验证要求。");
        testNews.setExcerpt("测试新闻摘要");
        testNews.setCategoryId(1L);
        testNews.setAuthor("测试作者");
        testNews.setStatus(NewsStatus.DRAFT);
        testNews.setViews(0);
        testNews.setLikes(0);
        testNews.setComments(0);
        testNews.setIsFeatured(false);
        testNews.setSortOrder(0);
        testNews.setCreatedAt(LocalDateTime.now());
        testNews.setUpdatedAt(LocalDateTime.now());
        testNews.setIsDeleted(false);
        
        testNewsFormDTO = new NewsFormDTO();
        testNewsFormDTO.setTitle("测试新闻标题");
        testNewsFormDTO.setContent("这是一个测试新闻的内容，内容长度超过50个字符以满足验证要求。");
        testNewsFormDTO.setExcerpt("测试新闻摘要");
        testNewsFormDTO.setCategoryId(1L);
        testNewsFormDTO.setAuthor("测试作者");
        testNewsFormDTO.setStatus(NewsStatus.DRAFT);
        testNewsFormDTO.setIsFeatured(false);
        testNewsFormDTO.setSortOrder(0);
        
        testCategory = new NewsCategory();
        testCategory.setId(1L);
        testCategory.setName("测试分类");
        testCategory.setDescription("测试分类描述");
    }
    
    @Test
    void testCreateNews_Success() {
        // 准备Mock数据
        when(newsCategoryMapper.selectById(1L)).thenReturn(testCategory);
        when(newsMapper.insert(any(News.class))).thenAnswer(invocation -> {
            News news = invocation.getArgument(0);
            news.setId(1L);
            return 1;
        });
        when(newsMapper.selectByIdWithDetails(1L)).thenReturn(testNews);
        
        // 执行测试
        NewsDTO result = newsService.createNews(testNewsFormDTO);
        
        // 验证结果
        assertNotNull(result);
        assertEquals("测试新闻标题", result.getTitle());
        assertEquals("测试作者", result.getAuthor());
        assertEquals(NewsStatus.DRAFT, result.getStatus());
        
        // 验证Mock调用
        verify(newsValidator).validateNewsForm(testNewsFormDTO);
        verify(newsCategoryMapper).selectById(1L);
        verify(newsMapper).insert(any(News.class));
        verify(newsMapper).selectByIdWithDetails(1L);
    }
    
    @Test
    void testCreateNews_ValidationFailure() {
        // 准备Mock数据 - 验证失败
        doThrow(new NewsValidationException("title", "", "新闻标题不能为空"))
                .when(newsValidator).validateNewsForm(any(NewsFormDTO.class));
        
        // 执行测试并验证异常
        assertThrows(NewsValidationException.class, () -> {
            newsService.createNews(testNewsFormDTO);
        });
        
        // 验证Mock调用
        verify(newsValidator).validateNewsForm(testNewsFormDTO);
        verify(newsMapper, never()).insert(any(News.class));
    }
    
    @Test
    void testCreateNews_CategoryNotFound() {
        // 准备Mock数据 - 分类不存在
        when(newsCategoryMapper.selectById(1L)).thenReturn(null);
        
        // 执行测试并验证异常
        assertThrows(NewsValidationException.class, () -> {
            newsService.createNews(testNewsFormDTO);
        });
        
        // 验证Mock调用
        verify(newsValidator).validateNewsForm(testNewsFormDTO);
        verify(newsCategoryMapper).selectById(1L);
        verify(newsMapper, never()).insert(any(News.class));
    }
    
    @Test
    void testUpdateNews_Success() {
        // 准备Mock数据
        when(newsMapper.selectById(1L)).thenReturn(testNews);
        when(newsCategoryMapper.selectById(1L)).thenReturn(testCategory);
        when(newsMapper.updateById(any(News.class))).thenReturn(1);
        when(newsTagRelationMapper.selectTagIdsByNewsId(1L)).thenReturn(Arrays.asList(1L, 2L));
        when(newsMapper.selectByIdWithDetails(1L)).thenReturn(testNews);
        
        // 执行测试
        NewsDTO result = newsService.updateNews(1L, testNewsFormDTO);
        
        // 验证结果
        assertNotNull(result);
        assertEquals("测试新闻标题", result.getTitle());
        
        // 验证Mock调用
        verify(newsMapper).selectById(1L);
        verify(newsValidator).validateNewsForm(testNewsFormDTO);
        verify(newsMapper).updateById(any(News.class));
    }
    
    @Test
    void testUpdateNews_NewsNotFound() {
        // 准备Mock数据 - 新闻不存在
        when(newsMapper.selectById(1L)).thenReturn(null);
        
        // 执行测试并验证异常
        assertThrows(NewsNotFoundException.class, () -> {
            newsService.updateNews(1L, testNewsFormDTO);
        });
        
        // 验证Mock调用
        verify(newsMapper).selectById(1L);
        verify(newsMapper, never()).updateById(any(News.class));
    }
    
    @Test
    void testGetNewsById_Success() {
        // 准备Mock数据
        when(newsMapper.selectByIdWithDetails(1L)).thenReturn(testNews);
        
        // 执行测试
        NewsDTO result = newsService.getNewsById(1L);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("测试新闻标题", result.getTitle());
        
        // 验证Mock调用
        verify(newsMapper).selectByIdWithDetails(1L);
    }
    
    @Test
    void testGetNewsById_NotFound() {
        // 准备Mock数据 - 新闻不存在
        when(newsMapper.selectByIdWithDetails(1L)).thenReturn(null);
        
        // 执行测试并验证异常
        assertThrows(NewsNotFoundException.class, () -> {
            newsService.getNewsById(1L);
        });
        
        // 验证Mock调用
        verify(newsMapper).selectByIdWithDetails(1L);
    }
    
    @Test
    void testDeleteNews_Success() {
        // 准备Mock数据
        when(newsMapper.selectById(1L)).thenReturn(testNews);
        when(newsMapper.softDeleteById(1L)).thenReturn(1);
        
        // 执行测试
        assertDoesNotThrow(() -> {
            newsService.deleteNews(1L);
        });
        
        // 验证Mock调用
        verify(newsMapper).selectById(1L);
        verify(newsMapper).softDeleteById(1L);
    }
    
    @Test
    void testDeleteNews_NotFound() {
        // 准备Mock数据 - 新闻不存在
        when(newsMapper.selectById(1L)).thenReturn(null);
        
        // 执行测试并验证异常
        assertThrows(NewsNotFoundException.class, () -> {
            newsService.deleteNews(1L);
        });
        
        // 验证Mock调用
        verify(newsMapper).selectById(1L);
        verify(newsMapper, never()).softDeleteById(anyLong());
    }
    
    @Test
    void testGetNewsList_Success() {
        // 准备Mock数据
        List<News> newsList = Arrays.asList(testNews);
        when(newsMapper.selectByConditions(any(), anyInt(), anyInt())).thenReturn(newsList);
        when(newsMapper.countByConditions(any())).thenReturn(1);
        
        // 执行测试
        Map<String, Object> result = newsService.getNewsList(1, 10, null, null, null, null, null);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.get("total"));
        assertEquals(1, result.get("page"));
        assertEquals(10, result.get("pageSize"));
        assertNotNull(result.get("list"));
        
        // 验证Mock调用
        verify(newsMapper).selectByConditions(any(), eq(0), eq(10));
        verify(newsMapper).countByConditions(any());
    }
    
    @Test
    void testSearchNews_Success() {
        // 准备Mock数据
        List<News> newsList = Arrays.asList(testNews);
        when(newsMapper.searchByKeyword(eq("测试"), anyInt(), anyInt())).thenReturn(newsList);
        when(newsMapper.countByKeyword("测试")).thenReturn(1);
        
        // 执行测试
        Map<String, Object> result = newsService.searchNews("测试", 1, 10);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.get("total"));
        assertEquals(1, result.get("page"));
        assertEquals(10, result.get("pageSize"));
        
        // 验证Mock调用
        verify(newsMapper).searchByKeyword("测试", 0, 10);
        verify(newsMapper).countByKeyword("测试");
    }
    
    @Test
    void testSearchNews_EmptyKeyword() {
        // 执行测试并验证异常
        assertThrows(NewsValidationException.class, () -> {
            newsService.searchNews("", 1, 10);
        });
        
        assertThrows(NewsValidationException.class, () -> {
            newsService.searchNews(null, 1, 10);
        });
    }
    
    @Test
    void testGetFeaturedNews_Success() {
        // 准备Mock数据
        List<News> featuredNews = Arrays.asList(testNews);
        when(newsMapper.selectFeatured(10)).thenReturn(featuredNews);
        
        // 执行测试
        List<NewsDTO> result = newsService.getFeaturedNews(10);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("测试新闻标题", result.get(0).getTitle());
        
        // 验证Mock调用
        verify(newsMapper).selectFeatured(10);
    }
    
    @Test
    void testGetHotNews_Success() {
        // 准备Mock数据
        List<News> hotNews = Arrays.asList(testNews);
        when(newsMapper.selectHotNews(10)).thenReturn(hotNews);
        
        // 执行测试
        List<NewsDTO> result = newsService.getHotNews(10);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        
        // 验证Mock调用
        verify(newsMapper).selectHotNews(10);
    }
    
    @Test
    void testGetLatestNews_Success() {
        // 准备Mock数据
        List<News> latestNews = Arrays.asList(testNews);
        when(newsMapper.selectLatestPublished(10)).thenReturn(latestNews);
        
        // 执行测试
        List<NewsDTO> result = newsService.getLatestNews(10);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        
        // 验证Mock调用
        verify(newsMapper).selectLatestPublished(10);
    }
    
    @Test
    void testGetRelatedNews_Success() {
        // 准备Mock数据
        testNews.setCategoryId(1L);
        List<News> relatedNews = Arrays.asList(testNews);
        when(newsMapper.selectById(1L)).thenReturn(testNews);
        when(newsMapper.selectRelatedNews(1L, 1L, 5)).thenReturn(relatedNews);
        
        // 执行测试
        List<NewsDTO> result = newsService.getRelatedNews(1L, 5);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        
        // 验证Mock调用
        verify(newsMapper).selectById(1L);
        verify(newsMapper).selectRelatedNews(1L, 1L, 5);
    }
    
    @Test
    void testBatchDeleteNews_Success() {
        // 准备Mock数据
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        when(newsMapper.batchSoftDelete(ids)).thenReturn(3);
        
        // 执行测试
        assertDoesNotThrow(() -> {
            newsService.batchDeleteNews(ids);
        });
        
        // 验证Mock调用
        verify(newsMapper).batchSoftDelete(ids);
    }
    
    @Test
    void testBatchDeleteNews_EmptyList() {
        // 执行测试并验证异常
        assertThrows(NewsValidationException.class, () -> {
            newsService.batchDeleteNews(null);
        });
        
        assertThrows(NewsValidationException.class, () -> {
            newsService.batchDeleteNews(Arrays.asList());
        });
    }
    
    @Test
    void testIncrementViews_Success() {
        // 准备Mock数据
        when(newsMapper.selectById(1L)).thenReturn(testNews);
        when(newsMapper.incrementViews(1L)).thenReturn(1);
        
        // 执行测试
        assertDoesNotThrow(() -> {
            newsService.incrementViews(1L);
        });
        
        // 验证Mock调用
        verify(newsMapper).selectById(1L);
        verify(newsMapper).incrementViews(1L);
    }
    
    @Test
    void testParameterValidation() {
        // 测试空ID参数
        assertThrows(NewsValidationException.class, () -> {
            newsService.getNewsById(null);
        });
        
        assertThrows(NewsValidationException.class, () -> {
            newsService.updateNews(null, testNewsFormDTO);
        });
        
        assertThrows(NewsValidationException.class, () -> {
            newsService.deleteNews(null);
        });
    }
}