package com.yxrobot.performance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yxrobot.dto.ManagedDeviceDTO;
import com.yxrobot.dto.ManagedDeviceSearchCriteria;
import com.yxrobot.entity.DeviceModel;
import com.yxrobot.entity.DeviceStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 设备管理API性能测试
 * 验证API响应时间和并发处理能力
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("设备管理API性能测试")
class ManagedDeviceApiPerformanceTest {
    
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private MockMvc mockMvc;
    
    // 性能要求阈值（毫秒）
    private static final long DEVICE_LIST_THRESHOLD = 2000;    // 设备列表加载时间 < 2秒
    private static final long SEARCH_FILTER_THRESHOLD = 1000;  // 搜索筛选响应时间 < 1秒
    private static final long DEVICE_OPERATION_THRESHOLD = 1000; // 设备操作响应时间 < 1秒
    private static final long STATS_CALCULATION_THRESHOLD = 1000; // 统计数据计算时间 < 1秒
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    
    @Test
    @DisplayName("性能测试 - 设备列表加载时间")
    void testDeviceListLoadingPerformance() throws Exception {
        long startTime = System.currentTimeMillis();
        
        mockMvc.perform(get("/api/admin/devices")
                .param("page", "1")
                .param("pageSize", "50") // 较大的页面大小
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;
        
        System.out.println("设备列表加载时间: " + responseTime + "ms");
        assertTrue(responseTime < DEVICE_LIST_THRESHOLD, 
            "设备列表加载时间应该小于" + DEVICE_LIST_THRESHOLD + "ms，实际: " + responseTime + "ms");
    }
    
    @Test
    @DisplayName("性能测试 - 搜索筛选响应时间")
    void testSearchFilterPerformance() throws Exception {
        long startTime = System.currentTimeMillis();
        
        mockMvc.perform(get("/api/admin/devices")
                .param("keyword", "YX2025")
                .param("status", "online")
                .param("model", "EDUCATION")
                .param("page", "1")
                .param("pageSize", "20")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;
        
        System.out.println("搜索筛选响应时间: " + responseTime + "ms");
        assertTrue(responseTime < SEARCH_FILTER_THRESHOLD, 
            "搜索筛选响应时间应该小于" + SEARCH_FILTER_THRESHOLD + "ms，实际: " + responseTime + "ms");
    }
    
    @Test
    @DisplayName("性能测试 - 高级搜索响应时间")
    void testAdvancedSearchPerformance() throws Exception {
        ManagedDeviceSearchCriteria criteria = new ManagedDeviceSearchCriteria();
        criteria.setKeyword("YX2025");
        criteria.setStatusList(Arrays.asList("online", "offline"));
        criteria.setModelList(Arrays.asList("EDUCATION", "HOME"));
        criteria.setPage(1);
        criteria.setPageSize(20);
        
        String criteriaJson = objectMapper.writeValueAsString(criteria);
        
        long startTime = System.currentTimeMillis();
        
        mockMvc.perform(post("/api/admin/devices/search/advanced")
                .contentType(MediaType.APPLICATION_JSON)
                .content(criteriaJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;
        
        System.out.println("高级搜索响应时间: " + responseTime + "ms");
        assertTrue(responseTime < SEARCH_FILTER_THRESHOLD, 
            "高级搜索响应时间应该小于" + SEARCH_FILTER_THRESHOLD + "ms，实际: " + responseTime + "ms");
    }
    
    @Test
    @DisplayName("性能测试 - 统计数据计算时间")
    void testStatsCalculationPerformance() throws Exception {
        long startTime = System.currentTimeMillis();
        
        mockMvc.perform(get("/api/admin/devices/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;
        
        System.out.println("统计数据计算时间: " + responseTime + "ms");
        assertTrue(responseTime < STATS_CALCULATION_THRESHOLD, 
            "统计数据计算时间应该小于" + STATS_CALCULATION_THRESHOLD + "ms，实际: " + responseTime + "ms");
    }
    
    @Test
    @DisplayName("性能测试 - 设备操作响应时间")
    void testDeviceOperationPerformance() throws Exception {
        // 测试状态更新操作
        Map<String, String> statusUpdate = new HashMap<>();
        statusUpdate.put("status", "online");
        String statusJson = objectMapper.writeValueAsString(statusUpdate);
        
        long startTime = System.currentTimeMillis();
        
        mockMvc.perform(patch("/api/admin/devices/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(statusJson))
                .andExpect(status().isOk());
        
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;
        
        System.out.println("设备操作响应时间: " + responseTime + "ms");
        assertTrue(responseTime < DEVICE_OPERATION_THRESHOLD, 
            "设备操作响应时间应该小于" + DEVICE_OPERATION_THRESHOLD + "ms，实际: " + responseTime + "ms");
    }
    
    @Test
    @DisplayName("性能测试 - 批量操作响应时间")
    void testBatchOperationPerformance() throws Exception {
        Map<String, Object> batchRequest = new HashMap<>();
        batchRequest.put("deviceIds", Arrays.asList(1L, 2L, 3L, 4L, 5L));
        
        String requestJson = objectMapper.writeValueAsString(batchRequest);
        
        long startTime = System.currentTimeMillis();
        
        mockMvc.perform(post("/api/admin/devices/batch/reboot")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;
        
        System.out.println("批量操作响应时间: " + responseTime + "ms");
        assertTrue(responseTime < DEVICE_OPERATION_THRESHOLD, 
            "批量操作响应时间应该小于" + DEVICE_OPERATION_THRESHOLD + "ms，实际: " + responseTime + "ms");
    }
    
    @Test
    @DisplayName("性能测试 - 设备日志查询响应时间")
    void testDeviceLogsPerformance() throws Exception {
        long startTime = System.currentTimeMillis();
        
        mockMvc.perform(get("/api/admin/devices/1/logs")
                .param("page", "1")
                .param("pageSize", "50")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;
        
        System.out.println("设备日志查询响应时间: " + responseTime + "ms");
        assertTrue(responseTime < SEARCH_FILTER_THRESHOLD, 
            "设备日志查询响应时间应该小于" + SEARCH_FILTER_THRESHOLD + "ms，实际: " + responseTime + "ms");
    }
    
    @Test
    @DisplayName("并发测试 - 设备列表查询")
    void testConcurrentDeviceListQuery() throws Exception {
        int concurrentUsers = 10;
        int requestsPerUser = 5;
        ExecutorService executor = Executors.newFixedThreadPool(concurrentUsers);
        
        CompletableFuture<Void>[] futures = new CompletableFuture[concurrentUsers];
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < concurrentUsers; i++) {
            futures[i] = CompletableFuture.runAsync(() -> {
                try {
                    for (int j = 0; j < requestsPerUser; j++) {
                        mockMvc.perform(get("/api/admin/devices")
                                .param("page", "1")
                                .param("pageSize", "20")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
                    }
                } catch (Exception e) {
                    fail("并发请求失败: " + e.getMessage());
                }
            }, executor);
        }
        
        // 等待所有请求完成
        CompletableFuture.allOf(futures).get(30, TimeUnit.SECONDS);
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        
        System.out.println("并发测试完成: " + concurrentUsers + "个用户，每用户" + requestsPerUser + "个请求，总耗时: " + totalTime + "ms");
        
        // 验证平均响应时间
        long avgResponseTime = totalTime / (concurrentUsers * requestsPerUser);
        assertTrue(avgResponseTime < DEVICE_LIST_THRESHOLD, 
            "并发情况下平均响应时间应该小于" + DEVICE_LIST_THRESHOLD + "ms，实际: " + avgResponseTime + "ms");
        
        executor.shutdown();
    }
    
    @Test
    @DisplayName("并发测试 - 搜索功能")
    void testConcurrentSearchQuery() throws Exception {
        int concurrentUsers = 5;
        ExecutorService executor = Executors.newFixedThreadPool(concurrentUsers);
        
        CompletableFuture<Void>[] futures = new CompletableFuture[concurrentUsers];
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < concurrentUsers; i++) {
            final int userId = i;
            futures[i] = CompletableFuture.runAsync(() -> {
                try {
                    mockMvc.perform(get("/api/admin/devices")
                            .param("keyword", "YX2025" + userId)
                            .param("page", "1")
                            .param("pageSize", "10")
                            .contentType(MediaType.APPLICATION_JSON))
                            .andExpect(status().isOk());
                } catch (Exception e) {
                    fail("并发搜索请求失败: " + e.getMessage());
                }
            }, executor);
        }
        
        // 等待所有请求完成
        CompletableFuture.allOf(futures).get(15, TimeUnit.SECONDS);
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        
        System.out.println("并发搜索测试完成: " + concurrentUsers + "个并发搜索，总耗时: " + totalTime + "ms");
        
        // 验证平均响应时间
        long avgResponseTime = totalTime / concurrentUsers;
        assertTrue(avgResponseTime < SEARCH_FILTER_THRESHOLD * 2, // 并发情况下允许稍长的响应时间
            "并发搜索平均响应时间应该小于" + (SEARCH_FILTER_THRESHOLD * 2) + "ms，实际: " + avgResponseTime + "ms");
        
        executor.shutdown();
    }
    
    @Test
    @DisplayName("压力测试 - 大数据量分页查询")
    void testLargeDataPagination() throws Exception {
        // 测试大页面大小的查询性能
        long startTime = System.currentTimeMillis();
        
        mockMvc.perform(get("/api/admin/devices")
                .param("page", "1")
                .param("pageSize", "1000") // 大页面大小
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;
        
        System.out.println("大数据量分页查询响应时间: " + responseTime + "ms");
        assertTrue(responseTime < DEVICE_LIST_THRESHOLD * 2, // 大数据量允许更长时间
            "大数据量分页查询响应时间应该小于" + (DEVICE_LIST_THRESHOLD * 2) + "ms，实际: " + responseTime + "ms");
    }
    
    @Test
    @DisplayName("内存测试 - 检查内存使用情况")
    void testMemoryUsage() throws Exception {
        Runtime runtime = Runtime.getRuntime();
        
        // 执行垃圾回收
        System.gc();
        Thread.sleep(1000);
        
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
        
        // 执行多次API调用
        for (int i = 0; i < 100; i++) {
            mockMvc.perform(get("/api/admin/devices")
                    .param("page", "1")
                    .param("pageSize", "20")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
        
        // 再次执行垃圾回收
        System.gc();
        Thread.sleep(1000);
        
        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = memoryAfter - memoryBefore;
        
        System.out.println("内存使用情况 - 执行前: " + (memoryBefore / 1024 / 1024) + "MB, " +
                          "执行后: " + (memoryAfter / 1024 / 1024) + "MB, " +
                          "增加: " + (memoryUsed / 1024 / 1024) + "MB");
        
        // 验证内存使用不会过度增长（允许50MB的增长）
        assertTrue(memoryUsed < 50 * 1024 * 1024, 
            "内存使用增长应该小于50MB，实际增长: " + (memoryUsed / 1024 / 1024) + "MB");
    }
}