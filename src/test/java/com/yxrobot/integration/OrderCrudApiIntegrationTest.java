package com.yxrobot.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 订单CRUD操作API集成测试
 * 测试订单的创建、读取、更新、删除功能
 */
@DisplayName("订单CRUD操作API集成测试")
public class OrderCrudApiIntegrationTest extends OrderApiIntegrationTestBase {
    
    @Test
    @DisplayName("测试订单创建功能")
    void testCreateOrder() throws Exception {
        long startTime = System.currentTimeMillis();
        
        // 准备测试数据
        Map<String, Object> orderData = createTestOrderCreateDTO();
        
        // 执行创建请求
        MvcResult result = performPostAndExpectSuccess("/api/admin/orders", orderData);
        
        // 验证响应时间
        validateResponseTime(startTime, 1000); // 创建操作应在1秒内完成
        
        // 解析响应数据
        Map<String, Object> response = parseResponse(result);
        @SuppressWarnings("unchecked")
        Map<String, Object> createdOrder = (Map<String, Object>) response.get("data");
        
        // 验证创建的订单数据
        validateOrderDataFormat(createdOrder);
        
        // 验证订单字段值
        assertEquals(orderData.get("type"), createdOrder.get("type"), "订单类型应匹配");
        assertEquals(orderData.get("customerId"), createdOrder.get("customerId"), "客户ID应匹配");
        assertEquals(orderData.get("deliveryAddress"), createdOrder.get("deliveryAddress"), "配送地址应匹配");
        assertEquals(orderData.get("totalAmount"), createdOrder.get("totalAmount"), "订单总金额应匹配");
        
        // 验证默认状态
        assertEquals("pending", createdOrder.get("status"), "新订单状态应为pending");
        assertEquals("pending", createdOrder.get("paymentStatus"), "新订单支付状态应为pending");
        
        // 验证订单商品
        assertNotNull(createdOrder.get("orderItems"), "订单商品不能为空");
        
        System.out.println("✅ 订单创建功能测试通过，订单ID: " + createdOrder.get("id"));
    }
    
    @Test
    @DisplayName("测试订单详情查询功能")
    void testGetOrderById() throws Exception {
        // 首先创建一个测试订单
        Map<String, Object> orderData = createTestOrderCreateDTO();
        MvcResult createResult = performPostAndExpectSuccess("/api/admin/orders", orderData);
        Map<String, Object> createResponse = parseResponse(createResult);
        @SuppressWarnings("unchecked")
        Map<String, Object> createdOrder = (Map<String, Object>) createResponse.get("data");
        Long orderId = ((Number) createdOrder.get("id")).longValue();
        
        long startTime = System.currentTimeMillis();
        
        // 查询订单详情
        MvcResult result = performGetAndExpectSuccess("/api/admin/orders/" + orderId);
        
        // 验证响应时间
        validateResponseTime(startTime, 500); // 详情查询应在500ms内完成
        
        // 解析响应数据
        Map<String, Object> response = parseResponse(result);
        @SuppressWarnings("unchecked")
        Map<String, Object> orderDetail = (Map<String, Object>) response.get("data");
        
        // 验证订单详情数据
        validateOrderDataFormat(orderDetail);
        
        // 验证订单ID匹配
        assertEquals(orderId, ((Number) orderDetail.get("id")).longValue(), "订单ID应匹配");
        
        // 验证包含完整的关联信息
        assertNotNull(orderDetail.get("orderItems"), "应包含订单商品信息");
        
        // 如果有客户信息，验证客户信息格式
        if (orderDetail.containsKey("customer") && orderDetail.get("customer") != null) {
            @SuppressWarnings("unchecked")
            Map<String, Object> customer = (Map<String, Object>) orderDetail.get("customer");
            assertNotNull(customer.get("id"), "客户ID不能为空");
            assertNotNull(customer.get("name"), "客户姓名不能为空");
        }
        
        System.out.println("✅ 订单详情查询功能测试通过，订单ID: " + orderId);
    }
    
    @Test
    @DisplayName("测试订单更新功能")
    void testUpdateOrder() throws Exception {
        // 首先创建一个测试订单
        Map<String, Object> orderData = createTestOrderCreateDTO();
        MvcResult createResult = performPostAndExpectSuccess("/api/admin/orders", orderData);
        Map<String, Object> createResponse = parseResponse(createResult);
        @SuppressWarnings("unchecked")
        Map<String, Object> createdOrder = (Map<String, Object>) createResponse.get("data");
        Long orderId = ((Number) createdOrder.get("id")).longValue();
        
        // 准备更新数据
        Map<String, Object> updateData = Map.of(
            "deliveryAddress", "北京市海淀区更新后的地址456号",
            "totalAmount", new BigDecimal("1200.00"),
            "notes", "更新后的备注信息"
        );
        
        long startTime = System.currentTimeMillis();
        
        // 执行更新请求
        MvcResult result = performPutAndExpectSuccess("/api/admin/orders/" + orderId, updateData);
        
        // 验证响应时间
        validateResponseTime(startTime, 1000); // 更新操作应在1秒内完成
        
        // 解析响应数据
        Map<String, Object> response = parseResponse(result);
        @SuppressWarnings("unchecked")
        Map<String, Object> updatedOrder = (Map<String, Object>) response.get("data");
        
        // 验证更新后的订单数据
        validateOrderDataFormat(updatedOrder);
        
        // 验证更新的字段
        assertEquals(updateData.get("deliveryAddress"), updatedOrder.get("deliveryAddress"), "配送地址应已更新");
        assertEquals(updateData.get("totalAmount"), updatedOrder.get("totalAmount"), "订单总金额应已更新");
        assertEquals(updateData.get("notes"), updatedOrder.get("notes"), "备注信息应已更新");
        
        // 验证订单ID未变
        assertEquals(orderId, ((Number) updatedOrder.get("id")).longValue(), "订单ID不应改变");
        
        System.out.println("✅ 订单更新功能测试通过，订单ID: " + orderId);
    }
    
    @Test
    @DisplayName("测试订单删除功能")
    void testDeleteOrder() throws Exception {
        // 首先创建一个测试订单
        Map<String, Object> orderData = createTestOrderCreateDTO();
        MvcResult createResult = performPostAndExpectSuccess("/api/admin/orders", orderData);
        Map<String, Object> createResponse = parseResponse(createResult);
        @SuppressWarnings("unchecked")
        Map<String, Object> createdOrder = (Map<String, Object>) createResponse.get("data");
        Long orderId = ((Number) createdOrder.get("id")).longValue();
        
        long startTime = System.currentTimeMillis();
        
        // 执行删除请求
        MvcResult result = performDeleteAndExpectSuccess("/api/admin/orders/" + orderId);
        
        // 验证响应时间
        validateResponseTime(startTime, 500); // 删除操作应在500ms内完成
        
        // 验证删除后无法查询到订单（应返回404或空结果）
        mockMvc.perform(get("/api/admin/orders/" + orderId))
                .andExpect(status().isBadRequest()); // 或者根据实际实现返回404
        
        System.out.println("✅ 订单删除功能测试通过，订单ID: " + orderId);
    }
    
    @Test
    @DisplayName("测试订单创建数据验证")
    void testCreateOrder_Validation() throws Exception {
        // 测试缺少必填字段
        Map<String, Object> invalidData1 = Map.of(
            "type", "sales"
            // 缺少其他必填字段
        );
        
        performAndExpectError("POST", "/api/admin/orders", invalidData1);
        
        // 测试无效的订单类型
        Map<String, Object> invalidData2 = createTestOrderCreateDTO();
        invalidData2.put("type", "invalid_type");
        
        performAndExpectError("POST", "/api/admin/orders", invalidData2);
        
        // 测试无效的金额
        Map<String, Object> invalidData3 = createTestOrderCreateDTO();
        invalidData3.put("totalAmount", new BigDecimal("-100.00"));
        
        performAndExpectError("POST", "/api/admin/orders", invalidData3);
        
        // 测试无效的客户ID
        Map<String, Object> invalidData4 = createTestOrderCreateDTO();
        invalidData4.put("customerId", -1L);
        
        performAndExpectError("POST", "/api/admin/orders", invalidData4);
        
        System.out.println("✅ 订单创建数据验证测试通过");
    }
    
    @Test
    @DisplayName("测试订单更新数据验证")
    void testUpdateOrder_Validation() throws Exception {
        // 首先创建一个测试订单
        Map<String, Object> orderData = createTestOrderCreateDTO();
        MvcResult createResult = performPostAndExpectSuccess("/api/admin/orders", orderData);
        Map<String, Object> createResponse = parseResponse(createResult);
        @SuppressWarnings("unchecked")
        Map<String, Object> createdOrder = (Map<String, Object>) createResponse.get("data");
        Long orderId = ((Number) createdOrder.get("id")).longValue();
        
        // 测试无效的订单ID
        Map<String, Object> updateData = Map.of("notes", "测试更新");
        performAndExpectError("PUT", "/api/admin/orders/-1", updateData);
        
        // 测试无效的金额
        Map<String, Object> invalidUpdateData = Map.of("totalAmount", new BigDecimal("-100.00"));
        performAndExpectError("PUT", "/api/admin/orders/" + orderId, invalidUpdateData);
        
        System.out.println("✅ 订单更新数据验证测试通过");
    }
    
    @Test
    @DisplayName("测试订单查询错误处理")
    void testGetOrder_ErrorHandling() throws Exception {
        // 测试查询不存在的订单
        mockMvc.perform(get("/api/admin/orders/999999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
        
        // 测试无效的订单ID
        mockMvc.perform(get("/api/admin/orders/invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
        
        System.out.println("✅ 订单查询错误处理测试通过");
    }
    
    @Test
    @DisplayName("测试订单完整的CRUD流程")
    void testOrderCrudWorkflow() throws Exception {
        // 1. 创建订单
        Map<String, Object> orderData = createTestOrderCreateDTO();
        MvcResult createResult = performPostAndExpectSuccess("/api/admin/orders", orderData);
        Map<String, Object> createResponse = parseResponse(createResult);
        @SuppressWarnings("unchecked")
        Map<String, Object> createdOrder = (Map<String, Object>) createResponse.get("data");
        Long orderId = ((Number) createdOrder.get("id")).longValue();
        
        // 2. 查询订单详情
        MvcResult getResult = performGetAndExpectSuccess("/api/admin/orders/" + orderId);
        Map<String, Object> getResponse = parseResponse(getResult);
        @SuppressWarnings("unchecked")
        Map<String, Object> retrievedOrder = (Map<String, Object>) getResponse.get("data");
        
        // 验证查询到的订单与创建的订单一致
        assertEquals(createdOrder.get("id"), retrievedOrder.get("id"), "订单ID应一致");
        assertEquals(createdOrder.get("orderNumber"), retrievedOrder.get("orderNumber"), "订单号应一致");
        
        // 3. 更新订单
        Map<String, Object> updateData = Map.of(
            "notes", "CRUD流程测试更新",
            "deliveryAddress", "CRUD测试更新地址"
        );
        MvcResult updateResult = performPutAndExpectSuccess("/api/admin/orders/" + orderId, updateData);
        Map<String, Object> updateResponse = parseResponse(updateResult);
        @SuppressWarnings("unchecked")
        Map<String, Object> updatedOrder = (Map<String, Object>) updateResponse.get("data");
        
        // 验证更新成功
        assertEquals(updateData.get("notes"), updatedOrder.get("notes"), "备注应已更新");
        assertEquals(updateData.get("deliveryAddress"), updatedOrder.get("deliveryAddress"), "地址应已更新");
        
        // 4. 再次查询验证更新
        MvcResult getUpdatedResult = performGetAndExpectSuccess("/api/admin/orders/" + orderId);
        Map<String, Object> getUpdatedResponse = parseResponse(getUpdatedResult);
        @SuppressWarnings("unchecked")
        Map<String, Object> finalOrder = (Map<String, Object>) getUpdatedResponse.get("data");
        
        assertEquals(updateData.get("notes"), finalOrder.get("notes"), "查询到的备注应为更新后的值");
        
        // 5. 删除订单
        performDeleteAndExpectSuccess("/api/admin/orders/" + orderId);
        
        // 6. 验证删除成功
        mockMvc.perform(get("/api/admin/orders/" + orderId))
                .andExpect(status().isBadRequest());
        
        System.out.println("✅ 订单完整CRUD流程测试通过，订单ID: " + orderId);
    }
    
    @Test
    @DisplayName("测试订单数据字段映射一致性")
    void testOrderDataFieldMapping() throws Exception {
        // 创建订单
        Map<String, Object> orderData = createTestOrderCreateDTO();
        MvcResult result = performPostAndExpectSuccess("/api/admin/orders", orderData);
        Map<String, Object> response = parseResponse(result);
        @SuppressWarnings("unchecked")
        Map<String, Object> createdOrder = (Map<String, Object>) response.get("data");
        
        // 验证字段命名一致性（camelCase）
        String[] expectedFields = {
            "id", "orderNumber", "type", "status", "customerId", "deliveryAddress",
            "totalAmount", "paymentStatus", "paymentMethod", "createdAt", "updatedAt"
        };
        
        for (String field : expectedFields) {
            assertTrue(createdOrder.containsKey(field), 
                "订单数据应包含字段: " + field);
        }
        
        // 验证不应包含数据库风格的字段名（snake_case）
        String[] unexpectedFields = {
            "order_number", "customer_id", "delivery_address", "total_amount",
            "payment_status", "payment_method", "created_at", "updated_at"
        };
        
        for (String field : unexpectedFields) {
            assertFalse(createdOrder.containsKey(field), 
                "订单数据不应包含数据库风格的字段名: " + field);
        }
        
        System.out.println("✅ 订单数据字段映射一致性测试通过");
    }
}