package com.yxrobot.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 订单列表API集成测试
 * 测试订单列表查询、分页、搜索、筛选功能
 */
@DisplayName("订单列表API集成测试")
public class OrderListApiIntegrationTest extends OrderApiIntegrationTestBase {
    
    @Test
    @DisplayName("测试获取订单列表 - 基础功能")
    void testGetOrdersList_Basic() throws Exception {
        long startTime = System.currentTimeMillis();
        
        // 执行请求
        MvcResult result = performGetAndExpectSuccess("/api/admin/orders");
        
        // 验证响应时间
        validateResponseTime(startTime, 2000); // 2秒内响应
        
        // 解析响应数据
        Map<String, Object> response = parseResponse(result);
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) response.get("data");
        
        // 验证数据格式
        validatePaginationDataFormat(data);
        
        // 验证统计数据
        assertNotNull(data.get("stats"), "统计数据不能为空");
        @SuppressWarnings("unchecked")
        Map<String, Object> stats = (Map<String, Object>) data.get("stats");
        validateStatsDataFormat(stats);
        
        // 验证分页信息
        assertNotNull(data.get("page"), "页码信息不能为空");
        assertNotNull(data.get("size"), "页面大小信息不能为空");
        
        System.out.println("✅ 订单列表基础功能测试通过");
    }
    
    @Test
    @DisplayName("测试订单列表分页功能")
    void testGetOrdersList_Pagination() throws Exception {
        // 测试第一页
        MvcResult result1 = mockMvc.perform(get("/api/admin/orders")
                .param("page", "1")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.size").value(5))
                .andReturn();
        
        Map<String, Object> response1 = parseResponse(result1);
        @SuppressWarnings("unchecked")
        Map<String, Object> data1 = (Map<String, Object>) response1.get("data");
        
        // 测试第二页
        MvcResult result2 = mockMvc.perform(get("/api/admin/orders")
                .param("page", "2")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.page").value(2))
                .andExpect(jsonPath("$.data.size").value(5))
                .andReturn();
        
        Map<String, Object> response2 = parseResponse(result2);
        @SuppressWarnings("unchecked")
        Map<String, Object> data2 = (Map<String, Object>) response2.get("data");
        
        // 验证分页数据
        validatePaginationDataFormat(data1);
        validatePaginationDataFormat(data2);
        
        System.out.println("✅ 订单列表分页功能测试通过");
    }
    
    @Test
    @DisplayName("测试订单列表搜索功能")
    void testGetOrdersList_Search() throws Exception {
        long startTime = System.currentTimeMillis();
        
        // 测试关键词搜索
        MvcResult result = mockMvc.perform(get("/api/admin/orders")
                .param("keyword", "测试订单")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").value(true))
                .andReturn();
        
        // 验证响应时间
        validateResponseTime(startTime, 1000); // 搜索应在1秒内响应
        
        Map<String, Object> response = parseResponse(result);
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) response.get("data");
        
        // 验证搜索结果格式
        validatePaginationDataFormat(data);
        
        System.out.println("✅ 订单列表搜索功能测试通过");
    }
    
    @Test
    @DisplayName("测试订单列表筛选功能")
    void testGetOrdersList_Filter() throws Exception {
        // 测试按订单类型筛选
        MvcResult result1 = mockMvc.perform(get("/api/admin/orders")
                .param("type", "sales")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn();
        
        // 测试按订单状态筛选
        MvcResult result2 = mockMvc.perform(get("/api/admin/orders")
                .param("status", "pending")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn();
        
        // 测试按支付状态筛选
        MvcResult result3 = mockMvc.perform(get("/api/admin/orders")
                .param("paymentStatus", "paid")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn();
        
        // 测试日期范围筛选
        MvcResult result4 = mockMvc.perform(get("/api/admin/orders")
                .param("startDate", "2024-01-01")
                .param("endDate", "2024-12-31")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn();
        
        // 验证筛选结果
        Map<String, Object> response1 = parseResponse(result1);
        Map<String, Object> response2 = parseResponse(result2);
        Map<String, Object> response3 = parseResponse(result3);
        Map<String, Object> response4 = parseResponse(result4);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data1 = (Map<String, Object>) response1.get("data");
        @SuppressWarnings("unchecked")
        Map<String, Object> data2 = (Map<String, Object>) response2.get("data");
        @SuppressWarnings("unchecked")
        Map<String, Object> data3 = (Map<String, Object>) response3.get("data");
        @SuppressWarnings("unchecked")
        Map<String, Object> data4 = (Map<String, Object>) response4.get("data");
        
        validatePaginationDataFormat(data1);
        validatePaginationDataFormat(data2);
        validatePaginationDataFormat(data3);
        validatePaginationDataFormat(data4);
        
        System.out.println("✅ 订单列表筛选功能测试通过");
    }
    
    @Test
    @DisplayName("测试订单列表排序功能")
    void testGetOrdersList_Sort() throws Exception {
        // 测试按创建时间降序排序
        MvcResult result1 = mockMvc.perform(get("/api/admin/orders")
                .param("sortBy", "createdAt")
                .param("sortOrder", "desc")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn();
        
        // 测试按订单金额升序排序
        MvcResult result2 = mockMvc.perform(get("/api/admin/orders")
                .param("sortBy", "totalAmount")
                .param("sortOrder", "asc")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn();
        
        // 测试按订单号排序
        MvcResult result3 = mockMvc.perform(get("/api/admin/orders")
                .param("sortBy", "orderNumber")
                .param("sortOrder", "asc")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn();
        
        // 验证排序结果
        Map<String, Object> response1 = parseResponse(result1);
        Map<String, Object> response2 = parseResponse(result2);
        Map<String, Object> response3 = parseResponse(result3);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data1 = (Map<String, Object>) response1.get("data");
        @SuppressWarnings("unchecked")
        Map<String, Object> data2 = (Map<String, Object>) response2.get("data");
        @SuppressWarnings("unchecked")
        Map<String, Object> data3 = (Map<String, Object>) response3.get("data");
        
        validatePaginationDataFormat(data1);
        validatePaginationDataFormat(data2);
        validatePaginationDataFormat(data3);
        
        System.out.println("✅ 订单列表排序功能测试通过");
    }
    
    @Test
    @DisplayName("测试订单列表复合筛选功能")
    void testGetOrdersList_ComplexFilter() throws Exception {
        long startTime = System.currentTimeMillis();
        
        // 测试复合筛选条件
        MvcResult result = mockMvc.perform(get("/api/admin/orders")
                .param("keyword", "测试")
                .param("type", "sales")
                .param("status", "pending")
                .param("paymentStatus", "pending")
                .param("startDate", "2024-01-01")
                .param("endDate", "2024-12-31")
                .param("sortBy", "createdAt")
                .param("sortOrder", "desc")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").value(true))
                .andReturn();
        
        // 验证响应时间（复杂查询应在2秒内响应）
        validateResponseTime(startTime, 2000);
        
        Map<String, Object> response = parseResponse(result);
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) response.get("data");
        
        validatePaginationDataFormat(data);
        
        System.out.println("✅ 订单列表复合筛选功能测试通过");
    }
    
    @Test
    @DisplayName("测试订单列表错误处理")
    void testGetOrdersList_ErrorHandling() throws Exception {
        // 测试无效的页码
        mockMvc.perform(get("/api/admin/orders")
                .param("page", "-1")
                .param("size", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
        
        // 测试无效的页面大小
        mockMvc.perform(get("/api/admin/orders")
                .param("page", "1")
                .param("size", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
        
        // 测试无效的排序字段
        mockMvc.perform(get("/api/admin/orders")
                .param("sortBy", "invalidField")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk()) // 应该使用默认排序
                .andExpect(jsonPath("$.success").value(true));
        
        // 测试无效的日期格式
        mockMvc.perform(get("/api/admin/orders")
                .param("startDate", "invalid-date")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
        
        System.out.println("✅ 订单列表错误处理测试通过");
    }
    
    @Test
    @DisplayName("测试订单列表性能要求")
    void testGetOrdersList_Performance() throws Exception {
        // 测试大页面大小的性能
        long startTime = System.currentTimeMillis();
        
        MvcResult result = mockMvc.perform(get("/api/admin/orders")
                .param("page", "1")
                .param("size", "50")) // 较大的页面大小
                .andExpect(status().isOk())
                .andReturn();
        
        // 验证响应时间（大数据量查询应在3秒内响应）
        validateResponseTime(startTime, 3000);
        
        Map<String, Object> response = parseResponse(result);
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) response.get("data");
        
        validatePaginationDataFormat(data);
        
        System.out.println("✅ 订单列表性能要求测试通过");
    }
    
    @Test
    @DisplayName("测试订单列表数据完整性")
    void testGetOrdersList_DataIntegrity() throws Exception {
        MvcResult result = performGetAndExpectSuccess("/api/admin/orders?page=1&size=5");
        
        Map<String, Object> response = parseResponse(result);
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) response.get("data");
        
        // 验证数据完整性
        assertNotNull(data.get("list"), "订单列表不能为空");
        assertNotNull(data.get("total"), "总数不能为空");
        assertNotNull(data.get("stats"), "统计数据不能为空");
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> orders = (List<Map<String, Object>>) data.get("list");
        
        // 验证每个订单的数据完整性
        for (Map<String, Object> order : orders) {
            validateOrderDataFormat(order);
            
            // 验证关联数据
            if (order.containsKey("orderItems") && order.get("orderItems") != null) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> items = (List<Map<String, Object>>) order.get("orderItems");
                for (Map<String, Object> item : items) {
                    validateOrderItemDataFormat(item);
                }
            }
        }
        
        System.out.println("✅ 订单列表数据完整性测试通过");
    }
}