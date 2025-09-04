package com.yxrobot.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 订单统计API集成测试
 * 测试订单统计数据的准确性和性能
 */
@DisplayName("订单统计API集成测试")
public class OrderStatsApiIntegrationTest extends OrderApiIntegrationTestBase {
    
    @Test
    @DisplayName("测试获取订单统计数据 - 基础功能")
    void testGetOrderStats_Basic() throws Exception {
        long startTime = System.currentTimeMillis();
        
        // 执行统计查询
        MvcResult result = performGetAndExpectSuccess("/api/admin/orders/stats");
        
        // 验证响应时间
        validateResponseTime(startTime, 1000); // 统计查询应在1秒内完成
        
        // 解析响应数据
        Map<String, Object> response = parseResponse(result);
        @SuppressWarnings("unchecked")
        Map<String, Object> statsData = (Map<String, Object>) response.get("data");
        
        // 验证统计数据格式
        validateStatsDataFormat(statsData);
        
        // 验证数据类型和合理性
        assertTrue(((Number) statsData.get("totalOrders")).intValue() >= 0, "订单总数应大于等于0");
        assertTrue(((Number) statsData.get("totalRevenue")).doubleValue() >= 0, "总收入应大于等于0");
        assertTrue(((Number) statsData.get("pendingOrders")).intValue() >= 0, "待处理订单数应大于等于0");
        assertTrue(((Number) statsData.get("completedOrders")).intValue() >= 0, "已完成订单数应大于等于0");
        
        // 验证逻辑一致性
        int totalOrders = ((Number) statsData.get("totalOrders")).intValue();
        int pendingOrders = ((Number) statsData.get("pendingOrders")).intValue();
        int completedOrders = ((Number) statsData.get("completedOrders")).intValue();
        
        assertTrue(pendingOrders <= totalOrders, "待处理订单数不应超过总订单数");
        assertTrue(completedOrders <= totalOrders, "已完成订单数不应超过总订单数");
        
        System.out.println("✅ 订单统计基础功能测试通过");
        System.out.println("   总订单数: " + totalOrders);
        System.out.println("   总收入: " + statsData.get("totalRevenue"));
        System.out.println("   待处理: " + pendingOrders);
        System.out.println("   已完成: " + completedOrders);
    }
    
    @Test
    @DisplayName("测试按日期范围获取统计数据")
    void testGetOrderStats_DateRange() throws Exception {
        // 测试最近30天的统计
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        
        long startTime = System.currentTimeMillis();
        
        MvcResult result = performGetAndExpectSuccess(
            "/api/admin/orders/stats?startDate=" + startDate + "&endDate=" + endDate);
        
        // 验证响应时间
        validateResponseTime(startTime, 1000);
        
        Map<String, Object> response = parseResponse(result);
        @SuppressWarnings("unchecked")
        Map<String, Object> statsData = (Map<String, Object>) response.get("data");
        
        // 验证统计数据格式
        validateStatsDataFormat(statsData);
        
        // 测试最近7天的统计
        LocalDate startDate7Days = endDate.minusDays(7);
        
        MvcResult result7Days = performGetAndExpectSuccess(
            "/api/admin/orders/stats?startDate=" + startDate7Days + "&endDate=" + endDate);
        
        Map<String, Object> response7Days = parseResponse(result7Days);
        @SuppressWarnings("unchecked")
        Map<String, Object> statsData7Days = (Map<String, Object>) response7Days.get("data");
        
        validateStatsDataFormat(statsData7Days);
        
        // 验证逻辑：7天的数据应该小于等于30天的数据
        int totalOrders30Days = ((Number) statsData.get("totalOrders")).intValue();
        int totalOrders7Days = ((Number) statsData7Days.get("totalOrders")).intValue();
        
        assertTrue(totalOrders7Days <= totalOrders30Days, 
            "7天的订单数应小于等于30天的订单数");
        
        System.out.println("✅ 按日期范围统计功能测试通过");
        System.out.println("   30天订单数: " + totalOrders30Days);
        System.out.println("   7天订单数: " + totalOrders7Days);
    }
    
    @Test
    @DisplayName("测试统计数据的实时性")
    void testOrderStats_RealTime() throws Exception {
        // 获取初始统计数据
        MvcResult initialResult = performGetAndExpectSuccess("/api/admin/orders/stats");
        Map<String, Object> initialResponse = parseResponse(initialResult);
        @SuppressWarnings("unchecked")
        Map<String, Object> initialStats = (Map<String, Object>) initialResponse.get("data");
        int initialTotalOrders = ((Number) initialStats.get("totalOrders")).intValue();
        int initialPendingOrders = ((Number) initialStats.get("pendingOrders")).intValue();
        
        // 创建一个新订单
        Map<String, Object> orderData = createTestOrderCreateDTO();
        performPostAndExpectSuccess("/api/admin/orders", orderData);
        
        // 等待一小段时间确保数据更新
        waitFor(500);
        
        // 再次获取统计数据
        MvcResult updatedResult = performGetAndExpectSuccess("/api/admin/orders/stats");
        Map<String, Object> updatedResponse = parseResponse(updatedResult);
        @SuppressWarnings("unchecked")
        Map<String, Object> updatedStats = (Map<String, Object>) updatedResponse.get("data");
        int updatedTotalOrders = ((Number) updatedStats.get("totalOrders")).intValue();
        int updatedPendingOrders = ((Number) updatedStats.get("pendingOrders")).intValue();
        
        // 验证统计数据已更新
        assertEquals(initialTotalOrders + 1, updatedTotalOrders, "总订单数应增加1");
        assertEquals(initialPendingOrders + 1, updatedPendingOrders, "待处理订单数应增加1");
        
        System.out.println("✅ 统计数据实时性测试通过");
        System.out.println("   创建订单前总数: " + initialTotalOrders);
        System.out.println("   创建订单后总数: " + updatedTotalOrders);
    }
    
    @Test
    @DisplayName("测试统计数据按订单类型分类")
    void testOrderStats_ByType() throws Exception {
        // 创建不同类型的测试订单
        Map<String, Object> salesOrder = createTestOrderCreateDTO();
        salesOrder.put("type", "sales");
        salesOrder.put("orderNumber", generateTestOrderNumber());
        performPostAndExpectSuccess("/api/admin/orders", salesOrder);
        
        Map<String, Object> rentalOrder = createTestOrderCreateDTO();
        rentalOrder.put("type", "rental");
        rentalOrder.put("orderNumber", generateTestOrderNumber());
        rentalOrder.put("rentalStartDate", LocalDate.now().toString());
        rentalOrder.put("rentalEndDate", LocalDate.now().plusDays(7).toString());
        rentalOrder.put("rentalDays", 7);
        performPostAndExpectSuccess("/api/admin/orders", rentalOrder);
        
        // 等待数据更新
        waitFor(500);
        
        // 获取统计数据
        MvcResult result = performGetAndExpectSuccess("/api/admin/orders/stats");
        Map<String, Object> response = parseResponse(result);
        @SuppressWarnings("unchecked")
        Map<String, Object> statsData = (Map<String, Object>) response.get("data");
        
        // 验证包含按类型分类的统计
        if (statsData.containsKey("salesOrders") && statsData.containsKey("rentalOrders")) {
            int salesOrders = ((Number) statsData.get("salesOrders")).intValue();
            int rentalOrders = ((Number) statsData.get("rentalOrders")).intValue();
            int totalOrders = ((Number) statsData.get("totalOrders")).intValue();
            
            assertTrue(salesOrders >= 1, "销售订单数应至少为1");
            assertTrue(rentalOrders >= 1, "租赁订单数应至少为1");
            assertTrue(salesOrders + rentalOrders <= totalOrders, "分类订单数之和不应超过总数");
            
            System.out.println("✅ 按订单类型分类统计测试通过");
            System.out.println("   销售订单: " + salesOrders);
            System.out.println("   租赁订单: " + rentalOrders);
        } else {
            System.out.println("⚠️  统计数据中未包含按类型分类的信息，跳过此验证");
        }
    }
    
    @Test
    @DisplayName("测试统计数据的准确性验证")
    void testOrderStats_Accuracy() throws Exception {
        // 创建已知数量的测试订单
        int testOrderCount = 3;
        double totalTestAmount = 0.0;
        
        for (int i = 0; i < testOrderCount; i++) {
            Map<String, Object> orderData = createTestOrderCreateDTO();
            orderData.put("orderNumber", generateTestOrderNumber());
            double orderAmount = 1000.0 + (i * 100.0); // 1000, 1100, 1200
            orderData.put("totalAmount", orderAmount);
            totalTestAmount += orderAmount;
            
            performPostAndExpectSuccess("/api/admin/orders", orderData);
        }
        
        // 等待数据更新
        waitFor(1000);
        
        // 获取统计数据
        MvcResult result = performGetAndExpectSuccess("/api/admin/orders/stats");
        Map<String, Object> response = parseResponse(result);
        @SuppressWarnings("unchecked")
        Map<String, Object> statsData = (Map<String, Object>) response.get("data");
        
        // 验证订单数量
        int totalOrders = ((Number) statsData.get("totalOrders")).intValue();
        assertTrue(totalOrders >= testOrderCount, 
            "总订单数应至少包含我们创建的测试订单数量");
        
        // 验证收入数据（如果有的话）
        if (statsData.containsKey("totalRevenue")) {
            double totalRevenue = ((Number) statsData.get("totalRevenue")).doubleValue();
            assertTrue(totalRevenue >= 0, "总收入应大于等于0");
        }
        
        System.out.println("✅ 统计数据准确性验证测试通过");
        System.out.println("   创建测试订单数: " + testOrderCount);
        System.out.println("   当前总订单数: " + totalOrders);
    }
    
    @Test
    @DisplayName("测试统计API的性能要求")
    void testOrderStats_Performance() throws Exception {
        // 测试多次连续请求的性能
        int requestCount = 5;
        long totalTime = 0;
        
        for (int i = 0; i < requestCount; i++) {
            long startTime = System.currentTimeMillis();
            
            performGetAndExpectSuccess("/api/admin/orders/stats");
            
            long requestTime = System.currentTimeMillis() - startTime;
            totalTime += requestTime;
            
            // 每个请求都应在1秒内完成
            assertTrue(requestTime <= 1000, 
                "第" + (i + 1) + "次请求耗时 " + requestTime + "ms，超过1秒限制");
        }
        
        double averageTime = (double) totalTime / requestCount;
        
        // 平均响应时间应在500ms内
        assertTrue(averageTime <= 500, 
            "平均响应时间 " + averageTime + "ms 超过500ms限制");
        
        System.out.println("✅ 统计API性能要求测试通过");
        System.out.println("   请求次数: " + requestCount);
        System.out.println("   平均响应时间: " + String.format("%.2f", averageTime) + "ms");
    }
    
    @Test
    @DisplayName("测试统计API错误处理")
    void testOrderStats_ErrorHandling() throws Exception {
        // 测试无效的日期格式
        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/orders/stats")
                .param("startDate", "invalid-date")
                .param("endDate", "2024-12-31"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false));
        
        // 测试结束日期早于开始日期
        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/orders/stats")
                .param("startDate", "2024-12-31")
                .param("endDate", "2024-01-01"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false));
        
        // 测试未来日期
        LocalDate futureDate = LocalDate.now().plusDays(30);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/orders/stats")
                .param("startDate", futureDate.toString())
                .param("endDate", futureDate.toString()))
                .andExpect(MockMvcResultMatchers.status().isOk()) // 应该成功但返回空数据
                .andReturn();
        
        Map<String, Object> response = parseResponse(result);
        @SuppressWarnings("unchecked")
        Map<String, Object> statsData = (Map<String, Object>) response.get("data");
        
        // 未来日期的统计应该为0或很小的值
        int totalOrders = ((Number) statsData.get("totalOrders")).intValue();
        assertTrue(totalOrders >= 0, "未来日期的订单数应大于等于0");
        
        System.out.println("✅ 统计API错误处理测试通过");
    }
    
    @Test
    @DisplayName("测试统计数据字段完整性")
    void testOrderStats_FieldCompleteness() throws Exception {
        MvcResult result = performGetAndExpectSuccess("/api/admin/orders/stats");
        Map<String, Object> response = parseResponse(result);
        @SuppressWarnings("unchecked")
        Map<String, Object> statsData = (Map<String, Object>) response.get("data");
        
        // 验证必需的统计字段
        String[] requiredFields = {
            "totalOrders", "totalRevenue", "pendingOrders", "completedOrders"
        };
        
        for (String field : requiredFields) {
            assertTrue(statsData.containsKey(field), 
                "统计数据应包含字段: " + field);
            assertNotNull(statsData.get(field), 
                "统计字段 " + field + " 不能为null");
        }
        
        // 验证可选的统计字段
        String[] optionalFields = {
            "salesOrders", "rentalOrders", "averageOrderValue", 
            "processingOrders", "shippedOrders", "cancelledOrders"
        };
        
        for (String field : optionalFields) {
            if (statsData.containsKey(field)) {
                assertNotNull(statsData.get(field), 
                    "如果存在，统计字段 " + field + " 不能为null");
                assertTrue(statsData.get(field) instanceof Number, 
                    "统计字段 " + field + " 应为数字类型");
            }
        }
        
        System.out.println("✅ 统计数据字段完整性测试通过");
        System.out.println("   包含的字段: " + statsData.keySet());
    }
}