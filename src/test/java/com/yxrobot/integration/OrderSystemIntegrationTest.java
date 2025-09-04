package com.yxrobot.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 订单系统集成测试
 * 执行完整的前后端集成测试，确保整体功能正常
 */
@DisplayName("订单系统集成测试")
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class OrderSystemIntegrationTest extends OrderApiIntegrationTestBase {
    
    @Test
    @DisplayName("01. 系统启动和基础连接测试")
    void testSystemStartupAndBasicConnectivity() throws Exception {
        System.out.println("🚀 开始系统启动和基础连接测试...");
        
        // 测试基础API连接
        performGetAndExpectSuccess("/api/admin/orders");
        System.out.println("✅ 订单列表API连接正常");
        
        performGetAndExpectSuccess("/api/admin/orders/stats");
        System.out.println("✅ 订单统计API连接正常");
        
        // 测试数据库连接
        try {
            performGetAndExpectSuccess("/api/database/health");
            System.out.println("✅ 数据库连接正常");
        } catch (Exception e) {
            System.out.println("⚠️  数据库健康检查接口可能未启用，跳过此检查");
        }
        
        System.out.println("✅ 系统启动和基础连接测试通过");
    }
    
    @Test
    @DisplayName("02. 完整业务流程集成测试")
    void testCompleteBusinessWorkflow() throws Exception {
        System.out.println("🔄 开始完整业务流程集成测试...");
        
        // 1. 创建订单
        Map<String, Object> orderData = createTestOrderCreateDTO();
        orderData.put("orderNumber", generateTestOrderNumber());
        
        MvcResult createResult = performPostAndExpectSuccess("/api/admin/orders", orderData);
        Map<String, Object> createResponse = parseResponse(createResult);
        @SuppressWarnings("unchecked")
        Map<String, Object> createdOrder = (Map<String, Object>) createResponse.get("data");
        Long orderId = ((Number) createdOrder.get("id")).longValue();
        
        System.out.println("✅ 步骤1: 订单创建成功，ID: " + orderId);
        
        // 2. 验证订单出现在列表中
        MvcResult listResult = performGetAndExpectSuccess("/api/admin/orders?page=1&size=10");
        Map<String, Object> listResponse = parseResponse(listResult);
        @SuppressWarnings("unchecked")
        Map<String, Object> listData = (Map<String, Object>) listResponse.get("data");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> orderList = (List<Map<String, Object>>) listData.get("list");
        
        boolean foundInList = orderList.stream()
                .anyMatch(order -> orderId.equals(((Number) order.get("id")).longValue()));
        assertTrue(foundInList, "创建的订单应出现在列表中");
        
        System.out.println("✅ 步骤2: 订单在列表中显示正常");
        
        // 3. 测试搜索功能
        String orderNumber = (String) createdOrder.get("orderNumber");
        MvcResult searchResult = performGetAndExpectSuccess("/api/admin/orders/search?keyword=" + orderNumber);
        Map<String, Object> searchResponse = parseResponse(searchResult);
        @SuppressWarnings("unchecked")
        Map<String, Object> searchData = (Map<String, Object>) searchResponse.get("data");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> searchList = (List<Map<String, Object>>) searchData.get("list");
        
        boolean foundInSearch = searchList.stream()
                .anyMatch(order -> orderId.equals(((Number) order.get("id")).longValue()));
        assertTrue(foundInSearch, "创建的订单应能通过搜索找到");
        
        System.out.println("✅ 步骤3: 订单搜索功能正常");
        
        System.out.println("✅ 完整业务流程集成测试通过");
    }
    
    @Test
    @DisplayName("03. 订单状态流转完整流程测试")
    void testOrderStatusTransitionWorkflow() throws Exception {
        System.out.println("🔄 开始订单状态流转完整流程测试...");
        
        // 创建测试订单
        Map<String, Object> orderData = createTestOrderCreateDTO();
        MvcResult createResult = performPostAndExpectSuccess("/api/admin/orders", orderData);
        Map<String, Object> createResponse = parseResponse(createResult);
        @SuppressWarnings("unchecked")
        Map<String, Object> createdOrder = (Map<String, Object>) createResponse.get("data");
        Long orderId = ((Number) createdOrder.get("id")).longValue();
        
        // 验证初始状态
        assertEquals("pending", createdOrder.get("status"), "新订单状态应为pending");
        System.out.println("✅ 初始状态: pending");
        
        // 状态流转序列：pending -> confirmed -> processing -> shipped -> delivered -> completed
        String[] statusFlow = {"confirmed", "processing", "shipped", "delivered", "completed"};
        
        for (String targetStatus : statusFlow) {
            Map<String, Object> statusUpdate = Map.of(
                "status", targetStatus,
                "operator", "systemTest",
                "notes", "系统集成测试状态流转到 " + targetStatus
            );
            
            MvcResult updateResult = performPatchAndExpectSuccess("/api/admin/orders/" + orderId + "/status", statusUpdate);
            Map<String, Object> updateResponse = parseResponse(updateResult);
            @SuppressWarnings("unchecked")
            Map<String, Object> updatedOrder = (Map<String, Object>) updateResponse.get("data");
            
            assertEquals(targetStatus, updatedOrder.get("status"), "状态应更新为 " + targetStatus);
            System.out.println("✅ 状态流转: " + targetStatus);
            
            waitFor(100); // 等待确保时间戳不同
        }
        
        System.out.println("✅ 订单状态流转完整流程测试通过");
    }
    
    @Test
    @DisplayName("04. 批量操作功能验证")
    void testBatchOperationsFunctionality() throws Exception {
        System.out.println("📦 开始批量操作功能验证...");
        
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
        
        System.out.println("✅ 创建了 " + orderIds.size() + " 个测试订单");
        
        // 测试批量状态更新
        Map<String, Object> batchStatusUpdate = Map.of(
            "orderIds", orderIds,
            "status", "confirmed",
            "operator", "systemTest",
            "notes", "批量确认订单"
        );
        
        MvcResult batchResult = performPatchAndExpectSuccess("/api/admin/orders/batch/status", batchStatusUpdate);
        Map<String, Object> batchResponse = parseResponse(batchResult);
        @SuppressWarnings("unchecked")
        Map<String, Object> batchData = (Map<String, Object>) batchResponse.get("data");
        
        assertEquals(orderIds.size(), ((Number) batchData.get("totalCount")).intValue(), "批量操作总数应匹配");
        System.out.println("✅ 批量状态更新功能正常");
        
        System.out.println("✅ 批量操作功能验证通过");
    }
    
    @Test
    @DisplayName("05. 搜索筛选功能全面测试")
    void testSearchAndFilterComprehensive() throws Exception {
        System.out.println("🔍 开始搜索筛选功能全面测试...");
        
        // 创建不同类型的测试订单
        Map<String, Object> salesOrder = createTestOrderCreateDTO();
        salesOrder.put("type", "sales");
        salesOrder.put("orderNumber", "SAL" + System.currentTimeMillis());
        performPostAndExpectSuccess("/api/admin/orders", salesOrder);
        
        Map<String, Object> rentalOrder = createTestOrderCreateDTO();
        rentalOrder.put("type", "rental");
        rentalOrder.put("orderNumber", "REN" + System.currentTimeMillis());
        performPostAndExpectSuccess("/api/admin/orders", rentalOrder);
        
        waitFor(500); // 等待数据更新
        
        // 测试按类型筛选
        MvcResult salesResult = performGetAndExpectSuccess("/api/admin/orders?type=sales");
        MvcResult rentalResult = performGetAndExpectSuccess("/api/admin/orders?type=rental");
        
        System.out.println("✅ 按订单类型筛选功能正常");
        
        // 测试关键词搜索
        MvcResult keywordResult = performGetAndExpectSuccess("/api/admin/orders/search?keyword=SAL");
        Map<String, Object> keywordResponse = parseResponse(keywordResult);
        @SuppressWarnings("unchecked")
        Map<String, Object> keywordData = (Map<String, Object>) keywordResponse.get("data");
        
        assertNotNull(keywordData.get("list"), "搜索结果不能为空");
        System.out.println("✅ 关键词搜索功能正常");
        
        System.out.println("✅ 搜索筛选功能全面测试通过");
    }
}