package com.yxrobot.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 订单统计控制器集成测试
 * 测试真实的HTTP请求和响应
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderStatsControllerIntegrationTest {
    
    @LocalServerPort
    private int port;
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    /**
     * 测试获取订单统计数据接口
     */
    @Test
    void testGetOrderStats() {
        String url = "http://localhost:" + port + "/api/admin/orders/stats";
        
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        // 验证响应状态
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        // 验证响应结构
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.get("code"));
        assertEquals("查询成功", body.get("message"));
        assertNotNull(body.get("data"));
        
        // 验证统计数据结构
        Map<String, Object> data = (Map<String, Object>) body.get("data");
        assertTrue(data.containsKey("total"));
        assertTrue(data.containsKey("pending"));
        assertTrue(data.containsKey("completed"));
        assertTrue(data.containsKey("totalRevenue"));
        assertTrue(data.containsKey("salesOrders"));
        assertTrue(data.containsKey("rentalOrders"));
    }
    
    /**
     * 测试获取简化统计数据接口
     */
    @Test
    void testGetOrderStatsSummary() {
        String url = "http://localhost:" + port + "/api/admin/orders/stats/summary";
        
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        // 验证响应状态
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        // 验证响应结构
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.get("code"));
        
        // 验证简化统计数据结构
        Map<String, Object> data = (Map<String, Object>) body.get("data");
        assertTrue(data.containsKey("totalOrders"));
        assertTrue(data.containsKey("totalRevenue"));
        assertTrue(data.containsKey("processingOrders"));
        assertTrue(data.containsKey("completedOrders"));
    }
    
    /**
     * 测试健康检查接口
     */
    @Test
    void testHealthCheck() {
        String url = "http://localhost:" + port + "/api/admin/orders/stats/health";
        
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        // 验证响应状态
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        // 验证响应结构
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.get("code"));
        assertEquals("服务正常", body.get("message"));
        
        // 验证健康检查数据
        Map<String, Object> data = (Map<String, Object>) body.get("data");
        assertEquals("healthy", data.get("status"));
        assertEquals("OrderStatsService", data.get("service"));
        assertTrue(data.containsKey("timestamp"));
        assertTrue(data.containsKey("dataAvailable"));
    }
}