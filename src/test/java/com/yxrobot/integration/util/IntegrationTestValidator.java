package com.yxrobot.integration.util;

import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 集成测试验证工具类
 * 提供统一的测试结果验证方法
 */
public class IntegrationTestValidator {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 验证API响应格式
     */
    public static void validateApiResponseFormat(Map<String, Object> response) {
        assertNotNull(response, "响应不能为空");
        assertTrue(response.containsKey("code"), "响应应包含code字段");
        assertTrue(response.containsKey("message"), "响应应包含message字段");
        assertTrue(response.containsKey("data"), "响应应包含data字段");
        
        Object code = response.get("code");
        assertTrue(code instanceof Number, "code字段应为数字类型");
        assertEquals(200, ((Number) code).intValue(), "成功响应的code应为200");
    }
    
    /**
     * 验证分页响应格式
     */
    public static void validatePaginationResponse(Map<String, Object> response) {
        validateApiResponseFormat(response);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) response.get("data");
        assertNotNull(data, "分页响应data不能为空");
        
        assertTrue(data.containsKey("list"), "分页响应应包含list字段");
        assertTrue(data.containsKey("total"), "分页响应应包含total字段");
        
        Object list = data.get("list");
        assertTrue(list instanceof List, "list字段应为数组类型");
        
        Object total = data.get("total");
        assertTrue(total instanceof Number, "total字段应为数字类型");
        assertTrue(((Number) total).intValue() >= 0, "total应为非负数");
    }
    
    /**
     * 验证订单数据完整性
     */
    public static void validateOrderData(Map<String, Object> order) {
        assertNotNull(order, "订单数据不能为空");
        
        // 必需字段验证
        assertTrue(order.containsKey("id"), "订单应包含id字段");
        assertTrue(order.containsKey("orderNumber"), "订单应包含orderNumber字段");
        assertTrue(order.containsKey("customerName"), "订单应包含customerName字段");
        assertTrue(order.containsKey("type"), "订单应包含type字段");
        assertTrue(order.containsKey("status"), "订单应包含status字段");
        assertTrue(order.containsKey("totalAmount"), "订单应包含totalAmount字段");
        
        // 字段类型验证
        Object id = order.get("id");
        assertTrue(id instanceof Number, "id字段应为数字类型");
        assertTrue(((Number) id).longValue() > 0, "id应为正数");
        
        Object orderNumber = order.get("orderNumber");
        assertTrue(orderNumber instanceof String, "orderNumber字段应为字符串类型");
        assertFalse(((String) orderNumber).trim().isEmpty(), "orderNumber不能为空");
        
        Object customerName = order.get("customerName");
        assertTrue(customerName instanceof String, "customerName字段应为字符串类型");
        assertFalse(((String) customerName).trim().isEmpty(), "customerName不能为空");
        
        Object type = order.get("type");
        assertTrue(type instanceof String, "type字段应为字符串类型");
        assertTrue(Arrays.asList("sales", "rental").contains(type), "type应为sales或rental");
        
        Object status = order.get("status");
        assertTrue(status instanceof String, "status字段应为字符串类型");
        assertTrue(Arrays.asList("pending", "confirmed", "processing", "shipped", "delivered", "completed", "cancelled")
                .contains(status), "status应为有效状态值");
        
        Object totalAmount = order.get("totalAmount");
        assertTrue(totalAmount instanceof Number || totalAmount instanceof String, 
                "totalAmount字段应为数字或字符串类型");
        
        if (totalAmount instanceof String) {
            try {
                BigDecimal amount = new BigDecimal((String) totalAmount);
                assertTrue(amount.compareTo(BigDecimal.ZERO) >= 0, "totalAmount应为非负数");
            } catch (NumberFormatException e) {
                fail("totalAmount字符串格式无效: " + totalAmount);
            }
        } else {
            assertTrue(((Number) totalAmount).doubleValue() >= 0, "totalAmount应为非负数");
        }
    }
    
    /**
     * 验证统计数据完整性
     */
    public static void validateStatsData(Map<String, Object> stats) {
        assertNotNull(stats, "统计数据不能为空");
        
        // 检查可能的字段名变体
        String[] totalOrdersFields = {"totalOrders", "total_orders", "orderCount"};
        String[] totalRevenueFields = {"totalRevenue", "total_revenue", "revenue"};
        
        boolean hasTotalOrders = Arrays.stream(totalOrdersFields)
                .anyMatch(field -> stats.containsKey(field));
        assertTrue(hasTotalOrders, "统计数据应包含订单总数字段");
        
        boolean hasTotalRevenue = Arrays.stream(totalRevenueFields)
                .anyMatch(field -> stats.containsKey(field));
        assertTrue(hasTotalRevenue, "统计数据应包含总收入字段");
        
        // 验证数值类型和范围
        for (String field : totalOrdersFields) {
            if (stats.containsKey(field)) {
                Object value = stats.get(field);
                assertTrue(value instanceof Number, field + "字段应为数字类型");
                assertTrue(((Number) value).intValue() >= 0, field + "应为非负数");
                break;
            }
        }
        
        for (String field : totalRevenueFields) {
            if (stats.containsKey(field)) {
                Object value = stats.get(field);
                assertTrue(value instanceof Number || value instanceof String, 
                        field + "字段应为数字或字符串类型");
                
                if (value instanceof Number) {
                    assertTrue(((Number) value).doubleValue() >= 0, field + "应为非负数");
                } else {
                    try {
                        BigDecimal amount = new BigDecimal((String) value);
                        assertTrue(amount.compareTo(BigDecimal.ZERO) >= 0, field + "应为非负数");
                    } catch (NumberFormatException e) {
                        fail(field + "字符串格式无效: " + value);
                    }
                }
                break;
            }
        }
    }
    
    /**
     * 验证字段映射一致性
     */
    public static void validateFieldMapping(Map<String, Object> data) {
        assertNotNull(data, "数据不能为空");
        
        // 验证字段名使用camelCase格式
        for (String key : data.keySet()) {
            if (key.contains("_")) {
                fail("字段名应使用camelCase格式，发现snake_case字段: " + key);
            }
            
            // 验证字段名首字母小写
            if (!key.isEmpty() && Character.isUpperCase(key.charAt(0))) {
                fail("字段名首字母应小写: " + key);
            }
        }
    }
    
    /**
     * 验证响应时间
     */
    public static void validateResponseTime(long responseTime, long maxTime, String operation) {
        assertTrue(responseTime >= 0, "响应时间不能为负数");
        assertTrue(responseTime <= maxTime, 
                String.format("%s响应时间超出限制: %dms > %dms", operation, responseTime, maxTime));
    }
    
    /**
     * 验证批量操作结果
     */
    public static void validateBatchOperationResult(Map<String, Object> result, int expectedTotal) {
        assertNotNull(result, "批量操作结果不能为空");
        
        assertTrue(result.containsKey("totalCount"), "批量操作结果应包含totalCount字段");
        assertTrue(result.containsKey("successCount"), "批量操作结果应包含successCount字段");
        assertTrue(result.containsKey("failureCount"), "批量操作结果应包含failureCount字段");
        
        int totalCount = ((Number) result.get("totalCount")).intValue();
        int successCount = ((Number) result.get("successCount")).intValue();
        int failureCount = ((Number) result.get("failureCount")).intValue();
        
        assertEquals(expectedTotal, totalCount, "总数应匹配预期");
        assertEquals(totalCount, successCount + failureCount, "成功数+失败数应等于总数");
        assertTrue(successCount >= 0, "成功数应为非负数");
        assertTrue(failureCount >= 0, "失败数应为非负数");
    }
    
    /**
     * 验证搜索结果相关性
     */
    public static void validateSearchRelevance(List<Map<String, Object>> results, String keyword) {
        assertNotNull(results, "搜索结果不能为空");
        
        if (!results.isEmpty() && keyword != null && !keyword.trim().isEmpty()) {
            // 验证至少有一个结果包含关键词
            boolean hasRelevantResult = results.stream().anyMatch(result -> {
                String orderNumber = (String) result.get("orderNumber");
                String customerName = (String) result.get("customerName");
                
                return (orderNumber != null && orderNumber.toLowerCase().contains(keyword.toLowerCase())) ||
                       (customerName != null && customerName.toLowerCase().contains(keyword.toLowerCase()));
            });
            
            assertTrue(hasRelevantResult, "搜索结果应包含与关键词相关的内容");
        }
    }
    
    /**
     * 验证状态流转合法性
     */
    public static void validateStatusTransition(String fromStatus, String toStatus) {
        assertNotNull(fromStatus, "原状态不能为空");
        assertNotNull(toStatus, "目标状态不能为空");
        
        // 定义合法的状态流转路径
        Map<String, List<String>> validTransitions = Map.of(
            "pending", Arrays.asList("confirmed", "cancelled"),
            "confirmed", Arrays.asList("processing", "cancelled"),
            "processing", Arrays.asList("shipped", "cancelled"),
            "shipped", Arrays.asList("delivered", "cancelled"),
            "delivered", Arrays.asList("completed"),
            "completed", Collections.emptyList(),
            "cancelled", Collections.emptyList()
        );
        
        List<String> allowedNextStates = validTransitions.get(fromStatus);
        assertNotNull(allowedNextStates, "未知的原状态: " + fromStatus);
        
        assertTrue(allowedNextStates.contains(toStatus), 
                String.format("不允许的状态流转: %s -> %s", fromStatus, toStatus));
    }
    
    /**
     * 验证数据一致性
     */
    public static void validateDataConsistency(Map<String, Object> order) {
        validateOrderData(order);
        
        // 验证订单项与总金额一致性
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> orderItems = (List<Map<String, Object>>) order.get("orderItems");
        
        if (orderItems != null && !orderItems.isEmpty()) {
            BigDecimal calculatedTotal = orderItems.stream()
                    .map(item -> {
                        Object subtotal = item.get("subtotal");
                        if (subtotal instanceof String) {
                            return new BigDecimal((String) subtotal);
                        } else if (subtotal instanceof Number) {
                            return BigDecimal.valueOf(((Number) subtotal).doubleValue());
                        }
                        return BigDecimal.ZERO;
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            Object totalAmount = order.get("totalAmount");
            BigDecimal orderTotal;
            if (totalAmount instanceof String) {
                orderTotal = new BigDecimal((String) totalAmount);
            } else {
                orderTotal = BigDecimal.valueOf(((Number) totalAmount).doubleValue());
            }
            
            assertEquals(0, calculatedTotal.compareTo(orderTotal), 
                    "订单项小计总和应等于订单总金额");
        }
    }
    
    /**
     * 生成测试报告摘要
     */
    public static String generateTestSummary(String testName, long duration, boolean success, 
                                           String details) {
        StringBuilder summary = new StringBuilder();
        summary.append("=".repeat(50)).append("\n");
        summary.append("测试名称: ").append(testName).append("\n");
        summary.append("执行时间: ").append(duration).append("ms\n");
        summary.append("测试结果: ").append(success ? "✅ 通过" : "❌ 失败").append("\n");
        if (details != null && !details.trim().isEmpty()) {
            summary.append("详细信息: ").append(details).append("\n");
        }
        summary.append("=".repeat(50)).append("\n");
        return summary.toString();
    }
    
    /**
     * 解析MvcResult响应
     */
    public static Map<String, Object> parseResponse(MvcResult result) throws Exception {
        String content = result.getResponse().getContentAsString();
        @SuppressWarnings("unchecked")
        Map<String, Object> response = objectMapper.readValue(content, Map.class);
        return response;
    }
}