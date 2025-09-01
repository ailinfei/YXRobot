package com.yxrobot.performance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yxrobot.dto.NewsFormDTO;
import com.yxrobot.entity.NewsStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 新闻管理系统性能测试
 * 测试系统在高并发和大数据量情况下的性能表现
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Disabled("性能测试，需要时手动启用")
public class NewsManagementPerformanceTest {
    
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private MockMvc mockMvc;
    private ExecutorService executorService;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        executorService = Executors.newFixedThreadPool(20);
    }
    
    /**
     * 测试新闻创建性能
     * 并发创建大量新闻，测试系统吞吐量
     */
    @Test
    void testNewsCreationPerformance() throws Exception {
        int totalNews = 1000;
        int concurrentThreads = 10;
        int newsPerThread = totalNews / concurrentThreads;
        
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        AtomicLong totalTime = new AtomicLong(0);
        
        List<Future<Void>> futures = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        
        // 启动并发创建任务
        for (int i = 0; i < concurrentThreads; i++) {
            final int threadId = i;
            Future<Void> future = executorService.submit(() -> {
                for (int j = 0; j < newsPerThread; j++) {
                    try {
                        long requestStart = System.currentTimeMillis();
                        
                        NewsFormDTO newsForm = createTestNewsForm(threadId, j);
                        
                        mockMvc.perform(post("/api/news")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newsForm)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200));
                        
                        long requestTime = System.currentTimeMillis() - requestStart;
                        totalTime.addAndGet(requestTime);
                        successCount.incrementAndGet();
                        
                    } catch (Exception e) {
                        failureCount.incrementAndGet();
                        System.err.println("创建新闻失败: " + e.getMessage());
                    }
                }
                return null;
            });
            futures.add(future);
        }
        
        // 等待所有任务完成
        for (Future<Void> future : futures) {
            future.get(60, TimeUnit.SECONDS);
        }
        
        long endTime = System.currentTimeMillis();
        long totalDuration = endTime - startTime;
        
        // 输出性能统计
        System.out.println("=== 新闻创建性能测试结果 ===");
        System.out.println("总新闻数: " + totalNews);
        System.out.println("并发线程数: " + concurrentThreads);
        System.out.println("成功创建: " + successCount.get());
        System.out.println("创建失败: " + failureCount.get());
        System.out.println("总耗时: " + totalDuration + "ms");
        System.out.println("平均响应时间: " + (totalTime.get() / successCount.get()) + "ms");
        System.out.println("吞吐量: " + (successCount.get() * 1000.0 / totalDuration) + " 请求/秒");
        
        // 断言性能要求
        assertTrue(successCount.get() > totalNews * 0.95, "成功率应该超过95%");
        assertTrue(totalTime.get() / successCount.get() < 1000, "平均响应时间应该小于1秒");
    }
    
    /**
     * 测试新闻列表查询性能
     * 在大数据量情况下测试分页查询性能
     */
    @Test
    void testNewsListQueryPerformance() throws Exception {
        int totalQueries = 500;
        int concurrentThreads = 10;
        int queriesPerThread = totalQueries / concurrentThreads;
        
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        AtomicLong totalTime = new AtomicLong(0);
        
        List<Future<Void>> futures = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        
        // 启动并发查询任务
        for (int i = 0; i < concurrentThreads; i++) {
            Future<Void> future = executorService.submit(() -> {
                for (int j = 0; j < queriesPerThread; j++) {
                    try {
                        long requestStart = System.currentTimeMillis();
                        
                        int page = (j % 10) + 1; // 查询前10页
                        int pageSize = 20;
                        
                        mockMvc.perform(get("/api/news")
                                .param("page", String.valueOf(page))
                                .param("pageSize", String.valueOf(pageSize)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200));
                        
                        long requestTime = System.currentTimeMillis() - requestStart;
                        totalTime.addAndGet(requestTime);
                        successCount.incrementAndGet();
                        
                    } catch (Exception e) {
                        failureCount.incrementAndGet();
                        System.err.println("查询新闻列表失败: " + e.getMessage());
                    }
                }
                return null;
            });
            futures.add(future);
        }
        
        // 等待所有任务完成
        for (Future<Void> future : futures) {
            future.get(30, TimeUnit.SECONDS);
        }
        
        long endTime = System.currentTimeMillis();
        long totalDuration = endTime - startTime;
        
        // 输出性能统计
        System.out.println("=== 新闻列表查询性能测试结果 ===");
        System.out.println("总查询数: " + totalQueries);
        System.out.println("并发线程数: " + concurrentThreads);
        System.out.println("成功查询: " + successCount.get());
        System.out.println("查询失败: " + failureCount.get());
        System.out.println("总耗时: " + totalDuration + "ms");
        System.out.println("平均响应时间: " + (totalTime.get() / successCount.get()) + "ms");
        System.out.println("吞吐量: " + (successCount.get() * 1000.0 / totalDuration) + " 请求/秒");
        
        // 断言性能要求
        assertTrue(successCount.get() > totalQueries * 0.98, "成功率应该超过98%");
        assertTrue(totalTime.get() / successCount.get() < 500, "平均响应时间应该小于500ms");
    }
    
    /**
     * 测试新闻搜索性能
     * 测试全文搜索在大数据量情况下的性能
     */
    @Test
    void testNewsSearchPerformance() throws Exception {
        int totalSearches = 200;
        int concurrentThreads = 5;
        int searchesPerThread = totalSearches / concurrentThreads;
        
        String[] keywords = {"测试", "新闻", "系统", "管理", "功能", "用户", "产品", "技术", "创新", "发布"};
        
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        AtomicLong totalTime = new AtomicLong(0);
        
        List<Future<Void>> futures = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        
        // 启动并发搜索任务
        for (int i = 0; i < concurrentThreads; i++) {
            Future<Void> future = executorService.submit(() -> {
                for (int j = 0; j < searchesPerThread; j++) {
                    try {
                        long requestStart = System.currentTimeMillis();
                        
                        String keyword = keywords[j % keywords.length];
                        
                        mockMvc.perform(get("/api/news")
                                .param("keyword", keyword)
                                .param("page", "1")
                                .param("pageSize", "10"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200));
                        
                        long requestTime = System.currentTimeMillis() - requestStart;
                        totalTime.addAndGet(requestTime);
                        successCount.incrementAndGet();
                        
                    } catch (Exception e) {
                        failureCount.incrementAndGet();
                        System.err.println("搜索新闻失败: " + e.getMessage());
                    }
                }
                return null;
            });
            futures.add(future);
        }
        
        // 等待所有任务完成
        for (Future<Void> future : futures) {
            future.get(30, TimeUnit.SECONDS);
        }
        
        long endTime = System.currentTimeMillis();
        long totalDuration = endTime - startTime;
        
        // 输出性能统计
        System.out.println("=== 新闻搜索性能测试结果 ===");
        System.out.println("总搜索数: " + totalSearches);
        System.out.println("并发线程数: " + concurrentThreads);
        System.out.println("成功搜索: " + successCount.get());
        System.out.println("搜索失败: " + failureCount.get());
        System.out.println("总耗时: " + totalDuration + "ms");
        System.out.println("平均响应时间: " + (totalTime.get() / successCount.get()) + "ms");
        System.out.println("吞吐量: " + (successCount.get() * 1000.0 / totalDuration) + " 请求/秒");
        
        // 断言性能要求
        assertTrue(successCount.get() > totalSearches * 0.95, "成功率应该超过95%");
        assertTrue(totalTime.get() / successCount.get() < 800, "平均响应时间应该小于800ms");
    }
    
    /**
     * 测试新闻互动性能
     * 测试高频互动操作的性能
     */
    @Test
    void testNewsInteractionPerformance() throws Exception {
        // 先创建一条测试新闻
        NewsFormDTO newsForm = createTestNewsForm(0, 0);
        String createResponse = mockMvc.perform(post("/api/news")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newsForm)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        var responseMap = objectMapper.readValue(createResponse, java.util.Map.class);
        var dataMap = (java.util.Map<String, Object>) responseMap.get("data");
        Long newsId = Long.valueOf(dataMap.get("id").toString());
        
        // 发布新闻
        mockMvc.perform(post("/api/news/{id}/publish", newsId))
                .andExpect(status().isOk());
        
        int totalInteractions = 1000;
        int concurrentThreads = 20;
        int interactionsPerThread = totalInteractions / concurrentThreads;
        
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        AtomicLong totalTime = new AtomicLong(0);
        
        List<Future<Void>> futures = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        
        // 启动并发互动任务
        for (int i = 0; i < concurrentThreads; i++) {
            Future<Void> future = executorService.submit(() -> {
                for (int j = 0; j < interactionsPerThread; j++) {
                    try {
                        long requestStart = System.currentTimeMillis();
                        
                        // 随机选择互动类型
                        String interactionType = (j % 2 == 0) ? "view" : "like";
                        
                        mockMvc.perform(post("/api/news/{id}/{action}", newsId, interactionType))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200));
                        
                        long requestTime = System.currentTimeMillis() - requestStart;
                        totalTime.addAndGet(requestTime);
                        successCount.incrementAndGet();
                        
                    } catch (Exception e) {
                        failureCount.incrementAndGet();
                        System.err.println("新闻互动失败: " + e.getMessage());
                    }
                }
                return null;
            });
            futures.add(future);
        }
        
        // 等待所有任务完成
        for (Future<Void> future : futures) {
            future.get(30, TimeUnit.SECONDS);
        }
        
        long endTime = System.currentTimeMillis();
        long totalDuration = endTime - startTime;
        
        // 输出性能统计
        System.out.println("=== 新闻互动性能测试结果 ===");
        System.out.println("总互动数: " + totalInteractions);
        System.out.println("并发线程数: " + concurrentThreads);
        System.out.println("成功互动: " + successCount.get());
        System.out.println("互动失败: " + failureCount.get());
        System.out.println("总耗时: " + totalDuration + "ms");
        System.out.println("平均响应时间: " + (totalTime.get() / successCount.get()) + "ms");
        System.out.println("吞吐量: " + (successCount.get() * 1000.0 / totalDuration) + " 请求/秒");
        
        // 断言性能要求
        assertTrue(successCount.get() > totalInteractions * 0.98, "成功率应该超过98%");
        assertTrue(totalTime.get() / successCount.get() < 200, "平均响应时间应该小于200ms");
    }
    
    /**
     * 测试数据库连接池性能
     * 测试在高并发情况下数据库连接的表现
     */
    @Test
    void testDatabaseConnectionPoolPerformance() throws Exception {
        int totalRequests = 500;
        int concurrentThreads = 50; // 高并发
        int requestsPerThread = totalRequests / concurrentThreads;
        
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        AtomicLong totalTime = new AtomicLong(0);
        
        List<Future<Void>> futures = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        
        // 启动高并发数据库访问任务
        for (int i = 0; i < concurrentThreads; i++) {
            Future<Void> future = executorService.submit(() -> {
                for (int j = 0; j < requestsPerThread; j++) {
                    try {
                        long requestStart = System.currentTimeMillis();
                        
                        // 执行数据库密集型操作
                        mockMvc.perform(get("/api/news/stats"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200));
                        
                        long requestTime = System.currentTimeMillis() - requestStart;
                        totalTime.addAndGet(requestTime);
                        successCount.incrementAndGet();
                        
                    } catch (Exception e) {
                        failureCount.incrementAndGet();
                        System.err.println("数据库访问失败: " + e.getMessage());
                    }
                }
                return null;
            });
            futures.add(future);
        }
        
        // 等待所有任务完成
        for (Future<Void> future : futures) {
            future.get(60, TimeUnit.SECONDS);
        }
        
        long endTime = System.currentTimeMillis();
        long totalDuration = endTime - startTime;
        
        // 输出性能统计
        System.out.println("=== 数据库连接池性能测试结果 ===");
        System.out.println("总请求数: " + totalRequests);
        System.out.println("并发线程数: " + concurrentThreads);
        System.out.println("成功请求: " + successCount.get());
        System.out.println("失败请求: " + failureCount.get());
        System.out.println("总耗时: " + totalDuration + "ms");
        System.out.println("平均响应时间: " + (totalTime.get() / successCount.get()) + "ms");
        System.out.println("吞吐量: " + (successCount.get() * 1000.0 / totalDuration) + " 请求/秒");
        
        // 断言性能要求
        assertTrue(successCount.get() > totalRequests * 0.95, "成功率应该超过95%");
        assertTrue(totalTime.get() / successCount.get() < 1000, "平均响应时间应该小于1秒");
    }
    
    /**
     * 创建测试用的新闻表单
     */
    private NewsFormDTO createTestNewsForm(int threadId, int index) {
        NewsFormDTO newsForm = new NewsFormDTO();
        newsForm.setTitle("性能测试新闻-" + threadId + "-" + index);
        newsForm.setExcerpt("这是性能测试新闻的摘要内容-" + threadId + "-" + index);
        newsForm.setContent("这是性能测试新闻的详细内容，用于验证系统在高并发情况下的性能表现。线程ID: " + threadId + ", 索引: " + index);
        newsForm.setCategoryId(1L);
        newsForm.setAuthor("性能测试-" + threadId);
        newsForm.setStatus(NewsStatus.DRAFT);
        newsForm.setIsFeatured(false);
        newsForm.setSortOrder(0);
        return newsForm;
    }
}