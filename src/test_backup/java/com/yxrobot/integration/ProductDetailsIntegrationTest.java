package com.yxrobot.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yxrobot.common.Result;
import com.yxrobot.dto.ProductDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 产品详情API集成测试
 * 测试完整的HTTP请求-响应流程
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-19
 */
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
public class ProductDetailsIntegrationTest {
    
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    
    /**
     * 测试获取产品详情 - 成功场景
     * 验证完整的HTTP请求响应流程
     */
    @Test
    void testGetProductDetails_Success() throws Exception {
        // 假设数据库中存在ID为1的产品
        Long productId = 1L;
        
        // 执行HTTP GET请求
        MvcResult result = mockMvc.perform(get("/api/v1/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.id").value(productId))
                .andExpect(jsonPath("$.data.name").exists())
                .andExpect(jsonPath("$.data.model").exists())
                .andExpect(jsonPath("$.data.price").exists())
                .andExpect(jsonPath("$.data.status").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andReturn();
        
        // 解析响应内容
        String responseContent = result.getResponse().getContentAsString();
        Result<ProductDTO> response = objectMapper.readValue(responseContent, 
            objectMapper.getTypeFactory().constructParametricType(Result.class, ProductDTO.class));
        
        // 验证响应数据
        assertNotNull(response);
        assertEquals(200, response.getCode());
        assertEquals("success", response.getMessage());
        assertNotNull(response.getData());
        assertNotNull(response.getTimestamp());
        
        // 验证产品数据完整性
        ProductDTO product = response.getData();
        assertEquals(productId, product.getId());
        assertNotNull(product.getName());
        assertNotNull(product.getModel());
        assertNotNull(product.getPrice());
        assertNotNull(product.getStatus());
        assertNotNull(product.getCreatedAt());
        assertNotNull(product.getUpdatedAt());
    }
    
    /**
     * 测试获取产品详情 - 产品不存在
     */
    @Test
    void testGetProductDetails_NotFound() throws Exception {
        // 使用不存在的产品ID
        Long nonExistentId = 99999L;
        
        // 执行HTTP GET请求
        mockMvc.perform(get("/api/v1/products/{id}", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("产品不存在"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists());
    }
    
    /**
     * 测试获取产品详情 - 无效参数
     */
    @Test
    void testGetProductDetails_InvalidParameter() throws Exception {
        // 使用无效的产品ID（字符串）
        mockMvc.perform(get("/api/v1/products/{id}", "invalid")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        
        // 使用负数ID
        mockMvc.perform(get("/api/v1/products/{id}", -1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }
    
    /**
     * 测试响应时间性能
     */
    @Test
    void testGetProductDetails_Performance() throws Exception {
        Long productId = 1L;
        
        // 记录开始时间
        long startTime = System.currentTimeMillis();
        
        // 执行请求
        mockMvc.perform(get("/api/v1/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        
        // 记录结束时间
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;
        
        // 验证响应时间（应该小于1秒）
        assertTrue(responseTime < 1000, 
            String.format("响应时间过长: %d ms", responseTime));
    }
    
    /**
     * 测试并发请求
     */
    @Test
    void testGetProductDetails_Concurrent() throws Exception {
        Long productId = 1L;
        int concurrentRequests = 10;
        
        // 创建并发请求任务
        Runnable requestTask = () -> {
            try {
                mockMvc.perform(get("/api/v1/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.code").value(200));
            } catch (Exception e) {
                fail("并发请求失败: " + e.getMessage());
            }
        };
        
        // 执行并发请求
        Thread[] threads = new Thread[concurrentRequests];
        for (int i = 0; i < concurrentRequests; i++) {
            threads[i] = new Thread(requestTask);
            threads[i].start();
        }
        
        // 等待所有线程完成
        for (Thread thread : threads) {
            thread.join();
        }
    }
    
    /**
     * 测试响应头信息
     */
    @Test
    void testGetProductDetails_ResponseHeaders() throws Exception {
        Long productId = 1L;
        
        mockMvc.perform(get("/api/v1/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(header().exists("Access-Control-Allow-Origin")); // CORS支持
    }
    
    /**
     * 测试数据格式验证
     */
    @Test
    void testGetProductDetails_DataFormat() throws Exception {
        Long productId = 1L;
        
        MvcResult result = mockMvc.perform(get("/api/v1/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        
        String responseContent = result.getResponse().getContentAsString();
        
        // 验证JSON格式有效性
        assertDoesNotThrow(() -> {
            objectMapper.readTree(responseContent);
        }, "响应不是有效的JSON格式");
        
        // 验证必要字段存在
        assertTrue(responseContent.contains("\"code\""), "响应缺少code字段");
        assertTrue(responseContent.contains("\"message\""), "响应缺少message字段");
        assertTrue(responseContent.contains("\"timestamp\""), "响应缺少timestamp字段");
    }
}