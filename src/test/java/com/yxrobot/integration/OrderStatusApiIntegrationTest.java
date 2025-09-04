package com.yxrobot.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 订单状态管理API集成测试
 * 测试订单状态变更、批量操作等功能
 */
@DisplayName("订单状态管理API集成测试")
public class OrderStatusApiIntegrationTest extends OrderApiIntegrationTestBase {
    
    @Test
    @DisplayName("测试单个订单状态变更")
    void testUpdateOrderStatus() throws Exception {
        // 首先创建一个测试订单
        Map<String, Object> orderData = createTestOrderCreateDTO();
        MvcResult createResult = performPostAndExpectSuccess("/api/admin/orders", orderData);
        Map<String, Object> createResponse = parseResponse(createResult);
        @SuppressWarnings("unchecked")
        Map<String, Object> createdOrder = (Map<String, Object>) createResponse.get("data");
        Long orderId = ((Number) createdOrder.get("id")).longValue();
        
        // 验证初始状态
        assertEquals("pending", createdOrder.get("status"), "新订单状态应为pending");
        
        long startTime = System.currentTimeMillis();
        
        // 准备状态更新数据
        Map<String, Object> statusUpdateData = createTestStatusUpdateRequest();
        
        // 执行状态更新
        MvcResult result = performPatchAndExpectSuccess("/api/admin/orders/" + orderId + "/status", statusUpdateData);
        
        // 验证响应时间
        validateResponseTime(startTime, 1000); // 状态更新应在1秒内完成
        
        // 解析响应数据
        Map<String, Object> response = parseResponse(result);
        @SuppressWarnings("unchecked")
        Map<String, Object> updatedOrder = (Map<String, Object>) response.get("data");
        
        // 验证状态已更新
        assertEquals(statusUpdateData.get("status"), updatedOrder.get("status"), "订单状态应已更新");
        assertEquals(orderId, ((Number) updatedOrder.get("id")).longValue(), "订单ID应保持不变");
        
        // 验证订单数据格式
        validateOrderDataFormat(updatedOrder);
        
        System.out.println("✅ 单个订单状态变更测试通过，订单ID: " + orderId + ", 新状态: " + updatedOrder.get("status"));
    }
    
    @Test
    @DisplayName("测试订单状态流转验证")
    void testOrderStatusTransitionValidation() throws Exception {
        // 创建测试订单
        Map<String, Object> orderData = createTestOrderCreateDTO();
        MvcResult createResult = performPostAndExpectSuccess("/api/admin/orders", orderData);
        Map<String, Object> createResponse = parseResponse(createResult);
        @SuppressWarnings("unchecked")
        Map<String, Object> createdOrder = (Map<String, Object>) createResponse.get("data");
        Long orderId = ((Number) createdOrder.get("id")).longValue();
        
        // 测试有效的状态流转：pending -> confirmed
        Map<String, Object> validTransition = Map.of(
            "status", "confirmed",
            "operator", "testUser",
            "notes", "状态流转测试"
        );
        
        performPatchAndExpectSuccess("/api/admin/orders/" + orderId + "/status", validTransition);
        
        // 验证状态已更新
        MvcResult getResult = performGetAndExpectSuccess("/api/admin/orders/" + orderId);
        Map<String, Object> getResponse = parseResponse(getResult);
        @SuppressWarnings("unchecked")
        Map<String, Object> order = (Map<String, Object>) getResponse.get("data");
        assertEquals("confirmed", order.get("status"), "状态应已更新为confirmed");
        
        // 测试无效的状态流转：confirmed -> pending（不允许回退）
        Map<String, Object> invalidTransition = Map.of(
            "status", "pending",
            "operator", "testUser",
            "notes", "无效状态流转测试"
        );
        
        mockMvc.perform(patch("/api/admin/orders/" + orderId + "/status")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(invalidTransition)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
        
        System.out.println("✅ 订单状态流转验证测试通过");
    }
    
    @Test
    @DisplayName("测试批量订单状态更新")
    void testBatchUpdateOrderStatus() throws Exception {
        // 创建多个测试订单
        List<Long> orderIds = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Map<String, Object> orderData = createTestOrderCreateDTO();
            orderData.put("orderNumber", generateTestOrderNumber());
            MvcResult createResult = performPostAndExpectSuccess("/api/admin/orders", orderData);
            Map<String, Object> createResponse = parseResponse(createResult);
            @SuppressWarnings("unchecked")
            Map<String, Object> createdOrder = (Map<String, Object>) createResponse.get("data");
            orderIds.add(((Number) createdOrder.get("id")).longValue());
        }
        
        long startTime = System.currentTimeMillis();
        
        // 准备批量状态更新数据
        Map<String, Object> batchUpdateData = createTestBatchStatusUpdateRequest(orderIds);
        
        // 执行批量状态更新
        MvcResult result = performPatchAndExpectSuccess("/api/admin/orders/batch/status", batchUpdateData);
        
        // 验证响应时间
        validateResponseTime(startTime, 2000); // 批量操作应在2秒内完成
        
        // 解析响应数据
        Map<String, Object> response = parseResponse(result);
        @SuppressWarnings("unchecked")
        Map<String, Object> batchResult = (Map<String, Object>) response.get("data");
        
        // 验证批量操作结果
        assertNotNull(batchResult.get("totalCount"), "总数不能为空");
        assertNotNull(batchResult.get("successCount"), "成功数量不能为空");
        assertNotNull(batchResult.get("failedCount"), "失败数量不能为空");
        
        assertEquals(orderIds.size(), ((Number) batchResult.get("totalCount")).intValue(), "总数应匹配");
        
        // 验证每个订单的状态是否已更新
        for (Long orderId : orderIds) {
            MvcResult getResult = performGetAndExpectSuccess("/api/admin/orders/" + orderId);
            Map<String, Object> getResponse = parseResponse(getResult);
            @SuppressWarnings("unchecked")
            Map<String, Object> order = (Map<String, Object>) getResponse.get("data");
            assertEquals(batchUpdateData.get("status"), order.get("status"), 
                "订单 " + orderId + " 的状态应已更新");
        }
        
        System.out.println("✅ 批量订单状态更新测试通过，更新了 " + orderIds.size() + " 个订单");
    }
    
    @Test
    @DisplayName("测试批量删除订单")
    void testBatchDeleteOrders() throws Exception {
        // 创建多个测试订单
        List<Long> orderIds = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Map<String, Object> orderData = createTestOrderCreateDTO();
            orderData.put("orderNumber", generateTestOrderNumber());
            MvcResult createResult = performPostAndExpectSuccess("/api/admin/orders", orderData);
            Map<String, Object> createResponse = parseResponse(createResult);
            @SuppressWarnings("unchecked")
            Map<String, Object> createdOrder = (Map<String, Object>) createResponse.get("data");
            orderIds.add(((Number) createdOrder.get("id")).longValue());
        }
        
        long startTime = System.currentTimeMillis();
        
        // 准备批量删除数据
        Map<String, Object> batchDeleteData = Map.of(
            "orderIds", orderIds,
            "operator", "testUser",
            "reason", "批量删除测试"
        );
        
        // 执行批量删除
        MvcResult result = mockMvc.perform(delete("/api/admin/orders/batch")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(batchDeleteData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andReturn();
        
        // 验证响应时间
        validateResponseTime(startTime, 2000); // 批量删除应在2秒内完成
        
        // 解析响应数据
        Map<String, Object> response = parseResponse(result);
        @SuppressWarnings("unchecked")
        Map<String, Object> batchResult = (Map<String, Object>) response.get("data");
        
        // 验证批量删除结果
        assertNotNull(batchResult.get("totalCount"), "总数不能为空");
        assertNotNull(batchResult.get("successCount"), "成功数量不能为空");
        
        // 验证订单已被删除（查询应返回错误）
        for (Long orderId : orderIds) {
            mockMvc.perform(get("/api/admin/orders/" + orderId))
                    .andExpect(status().isBadRequest());
        }
        
        System.out.println("✅ 批量删除订单测试通过，删除了 " + orderIds.size() + " 个订单");
    }
    
    @Test
    @DisplayName("测试订单状态历史记录")
    void testOrderStatusHistory() throws Exception {
        // 创建测试订单
        Map<String, Object> orderData = createTestOrderCreateDTO();
        MvcResult createResult = performPostAndExpectSuccess("/api/admin/orders", orderData);
        Map<String, Object> createResponse = parseResponse(createResult);
        @SuppressWarnings("unchecked")
        Map<String, Object> createdOrder = (Map<String, Object>) createResponse.get("data");
        Long orderId = ((Number) createdOrder.get("id")).longValue();
        
        // 执行几次状态变更
        String[] statuses = {"confirmed", "processing", "shipped"};
        for (String status : statuses) {
            Map<String, Object> statusUpdate = Map.of(
                "status", status,
                "operator", "testUser",
                "notes", "状态变更为 " + status
            );
            performPatchAndExpectSuccess("/api/admin/orders/" + orderId + "/status", statusUpdate);
            waitFor(100); // 等待一小段时间确保时间戳不同
        }
        
        // 查询状态历史
        MvcResult result = performGetAndExpectSuccess("/api/admin/orders/" + orderId + "/status/history");
        Map<String, Object> response = parseResponse(result);
        @SuppressWarnings("unchecked")
        Map<String, Object> historyData = (Map<String, Object>) response.get("data");
        
        // 验证历史记录
        assertNotNull(historyData.get("orderId"), "订单ID不能为空");
        assertNotNull(historyData.get("currentStatus"), "当前状态不能为空");
        assertNotNull(historyData.get("history"), "历史记录不能为空");
        
        assertEquals(orderId, ((Number) historyData.get("orderId")).longValue(), "订单ID应匹配");
        assertEquals("shipped", historyData.get("currentStatus"), "当前状态应为最后设置的状态");
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> history = (List<Map<String, Object>>) historyData.get("history");
        assertTrue(history.size() >= statuses.length, "历史记录数量应包含所有状态变更");
        
        System.out.println("✅ 订单状态历史记录测试通过，历史记录数量: " + history.size());
    }
    
    @Test
    @DisplayName("测试状态变更权限验证")
    void testStatusChangePermission() throws Exception {
        // 创建测试订单
        Map<String, Object> orderData = createTestOrderCreateDTO();
        MvcResult createResult = performPostAndExpectSuccess("/api/admin/orders", orderData);
        Map<String, Object> createResponse = parseResponse(createResult);
        @SuppressWarnings("unchecked")
        Map<String, Object> createdOrder = (Map<String, Object>) createResponse.get("data");
        Long orderId = ((Number) createdOrder.get("id")).longValue();
        
        // 测试权限验证
        MvcResult result = performGetAndExpectSuccess(
            "/api/admin/orders/" + orderId + "/status/permission?operator=testUser&targetStatus=confirmed");
        
        Map<String, Object> response = parseResponse(result);
        @SuppressWarnings("unchecked")
        Map<String, Object> permissionData = (Map<String, Object>) response.get("data");
        
        // 验证权限检查结果
        assertNotNull(permissionData.get("orderId"), "订单ID不能为空");
        assertNotNull(permissionData.get("operator"), "操作人不能为空");
        assertNotNull(permissionData.get("targetStatus"), "目标状态不能为空");
        assertNotNull(permissionData.get("hasPermission"), "权限结果不能为空");
        
        assertEquals(orderId, ((Number) permissionData.get("orderId")).longValue(), "订单ID应匹配");
        assertEquals("testUser", permissionData.get("operator"), "操作人应匹配");
        assertEquals("confirmed", permissionData.get("targetStatus"), "目标状态应匹配");
        
        System.out.println("✅ 状态变更权限验证测试通过");
    }
    
    @Test
    @DisplayName("测试状态变更错误处理")
    void testStatusChangeErrorHandling() throws Exception {
        // 创建测试订单
        Map<String, Object> orderData = createTestOrderCreateDTO();
        MvcResult createResult = performPostAndExpectSuccess("/api/admin/orders", orderData);
        Map<String, Object> createResponse = parseResponse(createResult);
        @SuppressWarnings("unchecked")
        Map<String, Object> createdOrder = (Map<String, Object>) createResponse.get("data");
        Long orderId = ((Number) createdOrder.get("id")).longValue();
        
        // 测试无效的订单ID
        Map<String, Object> statusUpdate = createTestStatusUpdateRequest();
        performAndExpectError("PATCH", "/api/admin/orders/999999/status", statusUpdate);
        
        // 测试无效的状态值
        Map<String, Object> invalidStatus = Map.of(
            "status", "invalid_status",
            "operator", "testUser",
            "notes", "无效状态测试"
        );
        
        mockMvc.perform(patch("/api/admin/orders/" + orderId + "/status")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(invalidStatus)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
        
        // 测试缺少必填字段
        Map<String, Object> incompleteData = Map.of("status", "confirmed");
        
        mockMvc.perform(patch("/api/admin/orders/" + orderId + "/status")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(incompleteData)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
        
        System.out.println("✅ 状态变更错误处理测试通过");
    }
    
    @Test
    @DisplayName("测试批量操作错误处理")
    void testBatchOperationErrorHandling() throws Exception {
        // 测试空的订单ID列表
        Map<String, Object> emptyBatch = Map.of(
            "orderIds", Arrays.asList(),
            "status", "confirmed",
            "operator", "testUser"
        );
        
        performAndExpectError("PATCH", "/api/admin/orders/batch/status", emptyBatch);
        
        // 测试无效的订单ID
        Map<String, Object> invalidBatch = Map.of(
            "orderIds", Arrays.asList(999999L, 999998L),
            "status", "confirmed",
            "operator", "testUser"
        );
        
        // 这个请求可能成功但返回失败的详情
        MvcResult result = mockMvc.perform(patch("/api/admin/orders/batch/status")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(invalidBatch)))
                .andExpect(status().isOk())
                .andReturn();
        
        Map<String, Object> response = parseResponse(result);
        @SuppressWarnings("unchecked")
        Map<String, Object> batchResult = (Map<String, Object>) response.get("data");
        
        // 验证所有操作都失败了
        assertEquals(0, ((Number) batchResult.get("successCount")).intValue(), "成功数量应为0");
        assertEquals(2, ((Number) batchResult.get("failedCount")).intValue(), "失败数量应为2");
        
        System.out.println("✅ 批量操作错误处理测试通过");
    }
}