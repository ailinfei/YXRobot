package com.yxrobot.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 订单API集成测试基类
 * 提供通用的测试工具和方法
 */
@SpringBootTest
@SpringJUnitConfig
@AutoConfigureWebMvc
public abstract class OrderApiIntegrationTestBase {
    
    @Autowired
    protected WebApplicationContext webApplicationContext;
    
    protected MockMvc mockMvc;
    protected ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }
    
    /**
     * 创建测试用的订单创建DTO
     */
    protected Map<String, Object> createTestOrderCreateDTO() {
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("orderNumber", "ORD" + System.currentTimeMillis());
        orderData.put("type", "sales");
        orderData.put("customerId", 1L);
        orderData.put("deliveryAddress", "北京市朝阳区测试地址123号");
        orderData.put("contactPhone", "13800138000");
        orderData.put("contactEmail", "test@example.com");
        orderData.put("subtotal", new BigDecimal("1000.00"));
        orderData.put("shippingFee", new BigDecimal("50.00"));
        orderData.put("discount", new BigDecimal("0.00"));
        orderData.put("totalAmount", new BigDecimal("1050.00"));
        orderData.put("currency", "CNY");
        orderData.put("paymentMethod", "alipay");
        orderData.put("expectedDeliveryDate", LocalDate.now().plusDays(7).toString());
        orderData.put("salesPerson", "张三");
        orderData.put("notes", "集成测试订单");
        
        // 订单商品
        List<Map<String, Object>> orderItems = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("productId", 1L);
        item.put("productName", "练字机器人 Pro");
        item.put("quantity", 1);
        item.put("unitPrice", new BigDecimal("1000.00"));
        item.put("totalPrice", new BigDecimal("1000.00"));
        orderItems.add(item);
        
        orderData.put("orderItems", orderItems);
        
        return orderData;
    }
    
    /**
     * 创建测试用的订单查询DTO
     */
    protected Map<String, Object> createTestOrderQueryDTO() {
        Map<String, Object> queryData = new HashMap<>();
        queryData.put("page", 1);
        queryData.put("size", 10);
        queryData.put("keyword", "测试");
        queryData.put("type", "sales");
        queryData.put("status", "pending");
        queryData.put("sortBy", "createdAt");
        queryData.put("sortOrder", "desc");
        
        return queryData;
    }
    
    /**
     * 创建测试用的状态更新请求
     */
    protected Map<String, Object> createTestStatusUpdateRequest() {
        Map<String, Object> statusData = new HashMap<>();
        statusData.put("status", "confirmed");
        statusData.put("operator", "testUser");
        statusData.put("notes", "集成测试状态变更");
        
        return statusData;
    }
    
    /**
     * 创建测试用的批量状态更新请求
     */
    protected Map<String, Object> createTestBatchStatusUpdateRequest(List<Long> orderIds) {
        Map<String, Object> batchData = new HashMap<>();
        batchData.put("orderIds", orderIds);
        batchData.put("status", "processing");
        batchData.put("operator", "testUser");
        batchData.put("notes", "批量状态更新测试");
        
        return batchData;
    }
    
    /**
     * 执行GET请求并验证响应
     */
    protected MvcResult performGetAndExpectSuccess(String url) throws Exception {
        return mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists())
                .andReturn();
    }
    
    /**
     * 执行POST请求并验证响应
     */
    protected MvcResult performPostAndExpectSuccess(String url, Object requestBody) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").value(true))
                .andReturn();
    }
    
    /**
     * 执行PUT请求并验证响应
     */
    protected MvcResult performPutAndExpectSuccess(String url, Object requestBody) throws Exception {
        return mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").value(true))
                .andReturn();
    }
    
    /**
     * 执行PATCH请求并验证响应
     */
    protected MvcResult performPatchAndExpectSuccess(String url, Object requestBody) throws Exception {
        return mockMvc.perform(patch(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").value(true))
                .andReturn();
    }
    
    /**
     * 执行DELETE请求并验证响应
     */
    protected MvcResult performDeleteAndExpectSuccess(String url) throws Exception {
        return mockMvc.perform(delete(url)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").value(true))
                .andReturn();
    }
    
    /**
     * 执行请求并期望错误响应
     */
    protected MvcResult performAndExpectError(String method, String url, Object requestBody) throws Exception {
        switch (method.toUpperCase()) {
            case "GET":
                return mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.code").value(400))
                        .andExpect(jsonPath("$.success").value(false))
                        .andReturn();
            case "POST":
                return mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.code").value(400))
                        .andExpect(jsonPath("$.success").value(false))
                        .andReturn();
            default:
                throw new IllegalArgumentException("Unsupported method: " + method);
        }
    }
    
    /**
     * 解析响应数据
     */
    @SuppressWarnings("unchecked")
    protected Map<String, Object> parseResponse(MvcResult result) throws Exception {
        String content = result.getResponse().getContentAsString();
        return objectMapper.readValue(content, Map.class);
    }
    
    /**
     * 验证订单数据格式
     */
    protected void validateOrderDataFormat(Map<String, Object> orderData) {
        // 验证必需字段存在
        assertNotNull(orderData.get("id"), "订单ID不能为空");
        assertNotNull(orderData.get("orderNumber"), "订单号不能为空");
        assertNotNull(orderData.get("type"), "订单类型不能为空");
        assertNotNull(orderData.get("status"), "订单状态不能为空");
        assertNotNull(orderData.get("customerId"), "客户ID不能为空");
        assertNotNull(orderData.get("totalAmount"), "订单总金额不能为空");
        assertNotNull(orderData.get("createdAt"), "创建时间不能为空");
        
        // 验证字段类型
        assertTrue(orderData.get("id") instanceof Number, "订单ID应为数字类型");
        assertTrue(orderData.get("orderNumber") instanceof String, "订单号应为字符串类型");
        assertTrue(orderData.get("type") instanceof String, "订单类型应为字符串类型");
        assertTrue(orderData.get("status") instanceof String, "订单状态应为字符串类型");
        assertTrue(orderData.get("customerId") instanceof Number, "客户ID应为数字类型");
        assertTrue(orderData.get("totalAmount") instanceof Number, "订单总金额应为数字类型");
        
        // 验证字段命名格式（camelCase）
        assertTrue(orderData.containsKey("orderNumber"), "应使用camelCase命名：orderNumber");
        assertTrue(orderData.containsKey("customerId"), "应使用camelCase命名：customerId");
        assertTrue(orderData.containsKey("totalAmount"), "应使用camelCase命名：totalAmount");
        assertTrue(orderData.containsKey("createdAt"), "应使用camelCase命名：createdAt");
        
        // 验证订单商品数据格式
        if (orderData.containsKey("orderItems") && orderData.get("orderItems") != null) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> orderItems = (List<Map<String, Object>>) orderData.get("orderItems");
            for (Map<String, Object> item : orderItems) {
                validateOrderItemDataFormat(item);
            }
        }
    }
    
    /**
     * 验证订单商品数据格式
     */
    protected void validateOrderItemDataFormat(Map<String, Object> itemData) {
        assertNotNull(itemData.get("id"), "商品ID不能为空");
        assertNotNull(itemData.get("productId"), "产品ID不能为空");
        assertNotNull(itemData.get("productName"), "产品名称不能为空");
        assertNotNull(itemData.get("quantity"), "数量不能为空");
        assertNotNull(itemData.get("unitPrice"), "单价不能为空");
        assertNotNull(itemData.get("totalPrice"), "小计不能为空");
        
        // 验证字段命名格式（camelCase）
        assertTrue(itemData.containsKey("productId"), "应使用camelCase命名：productId");
        assertTrue(itemData.containsKey("productName"), "应使用camelCase命名：productName");
        assertTrue(itemData.containsKey("unitPrice"), "应使用camelCase命名：unitPrice");
        assertTrue(itemData.containsKey("totalPrice"), "应使用camelCase命名：totalPrice");
    }
    
    /**
     * 验证分页数据格式
     */
    protected void validatePaginationDataFormat(Map<String, Object> responseData) {
        assertNotNull(responseData.get("list"), "列表数据不能为空");
        assertNotNull(responseData.get("total"), "总数不能为空");
        assertTrue(responseData.get("total") instanceof Number, "总数应为数字类型");
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> list = (List<Map<String, Object>>) responseData.get("list");
        assertNotNull(list, "列表不能为null");
        
        // 验证列表中每个订单的数据格式
        for (Map<String, Object> order : list) {
            validateOrderDataFormat(order);
        }
    }
    
    /**
     * 验证统计数据格式
     */
    protected void validateStatsDataFormat(Map<String, Object> statsData) {
        assertNotNull(statsData.get("totalOrders"), "订单总数不能为空");
        assertNotNull(statsData.get("totalRevenue"), "总收入不能为空");
        assertNotNull(statsData.get("pendingOrders"), "待处理订单数不能为空");
        assertNotNull(statsData.get("completedOrders"), "已完成订单数不能为空");
        
        // 验证字段类型
        assertTrue(statsData.get("totalOrders") instanceof Number, "订单总数应为数字类型");
        assertTrue(statsData.get("totalRevenue") instanceof Number, "总收入应为数字类型");
        assertTrue(statsData.get("pendingOrders") instanceof Number, "待处理订单数应为数字类型");
        assertTrue(statsData.get("completedOrders") instanceof Number, "已完成订单数应为数字类型");
        
        // 验证字段命名格式（camelCase）
        assertTrue(statsData.containsKey("totalOrders"), "应使用camelCase命名：totalOrders");
        assertTrue(statsData.containsKey("totalRevenue"), "应使用camelCase命名：totalRevenue");
        assertTrue(statsData.containsKey("pendingOrders"), "应使用camelCase命名：pendingOrders");
        assertTrue(statsData.containsKey("completedOrders"), "应使用camelCase命名：completedOrders");
    }
    
    /**
     * 验证响应时间
     */
    protected void validateResponseTime(long startTime, long maxResponseTime) {
        long responseTime = System.currentTimeMillis() - startTime;
        assertTrue(responseTime <= maxResponseTime, 
            String.format("响应时间 %dms 超过了最大限制 %dms", responseTime, maxResponseTime));
    }
    
    /**
     * 生成测试用的订单号
     */
    protected String generateTestOrderNumber() {
        return "TST" + System.currentTimeMillis();
    }
    
    /**
     * 等待一段时间（用于测试异步操作）
     */
    protected void waitFor(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}