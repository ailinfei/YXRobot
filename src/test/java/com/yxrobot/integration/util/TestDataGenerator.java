package com.yxrobot.integration.util;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 测试数据生成器
 * 为集成测试生成各种类型的测试数据
 */
public class TestDataGenerator {
    
    private static final String[] CUSTOMER_NAMES = {
        "张三", "李四", "王五", "赵六", "钱七", "孙八", "周九", "吴十",
        "郑十一", "王十二", "冯十三", "陈十四", "褚十五", "卫十六"
    };
    
    private static final String[] PRODUCT_NAMES = {
        "智能机器人A型", "智能机器人B型", "智能机器人C型", "智能机器人D型",
        "服务机器人X1", "服务机器人X2", "工业机器人Y1", "工业机器人Y2",
        "教育机器人Z1", "教育机器人Z2", "清洁机器人W1", "清洁机器人W2"
    };
    
    private static final String[] ADDRESSES = {
        "北京市朝阳区建国路1号", "上海市浦东新区陆家嘴环路2号", "广州市天河区珠江新城3号",
        "深圳市南山区科技园4号", "杭州市西湖区文三路5号", "成都市高新区天府大道6号",
        "武汉市洪山区光谷大道7号", "西安市雁塔区高新路8号", "南京市江宁区九龙湖9号",
        "苏州市工业园区星海街10号", "青岛市崂山区海尔路11号", "大连市高新园区软件园12号"
    };
    
    private static final String[] ORDER_TYPES = {"sales", "rental"};
    private static final String[] ORDER_STATUSES = {"pending", "confirmed", "processing", "shipped", "delivered", "completed"};
    private static final String[] PAYMENT_STATUSES = {"unpaid", "paid", "refunded"};
    
    /**
     * 生成基础订单数据
     */
    public static Map<String, Object> generateBasicOrder(String prefix) {
        Map<String, Object> order = new HashMap<>();
        
        order.put("orderNumber", generateOrderNumber(prefix));
        order.put("customerName", getRandomCustomerName());
        order.put("customerPhone", generatePhoneNumber());
        order.put("customerEmail", generateEmail());
        order.put("type", getRandomOrderType());
        order.put("status", "pending");
        order.put("paymentStatus", "unpaid");
        order.put("totalAmount", generateRandomAmount());
        order.put("notes", "集成测试订单 - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        order.put("shippingAddress", getRandomAddress());
        
        return order;
    }
    
    /**
     * 生成完整订单数据（包含订单项）
     */
    public static Map<String, Object> generateCompleteOrder(String prefix) {
        Map<String, Object> order = generateBasicOrder(prefix);
        
        // 添加订单项
        List<Map<String, Object>> orderItems = generateOrderItems();
        order.put("orderItems", orderItems);
        
        // 计算总金额
        BigDecimal totalAmount = orderItems.stream()
                .map(item -> new BigDecimal(item.get("subtotal").toString()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.put("totalAmount", totalAmount);
        
        return order;
    }
    
    /**
     * 生成销售订单
     */
    public static Map<String, Object> generateSalesOrder(String prefix) {
        Map<String, Object> order = generateBasicOrder(prefix);
        order.put("type", "sales");
        order.put("orderNumber", "SAL_" + generateOrderNumber(prefix));
        return order;
    }
    
    /**
     * 生成租赁订单
     */
    public static Map<String, Object> generateRentalOrder(String prefix) {
        Map<String, Object> order = generateBasicOrder(prefix);
        order.put("type", "rental");
        order.put("orderNumber", "REN_" + generateOrderNumber(prefix));
        
        // 租赁订单特有字段
        order.put("rentalStartDate", LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        order.put("rentalEndDate", LocalDateTime.now().plusDays(30).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        order.put("rentalDeposit", generateRandomAmount().multiply(new BigDecimal("0.2")));
        
        return order;
    }
    
    /**
     * 生成不同状态的订单
     */
    public static Map<String, Object> generateOrderWithStatus(String prefix, String status) {
        Map<String, Object> order = generateBasicOrder(prefix);
        order.put("status", status);
        order.put("orderNumber", status.toUpperCase() + "_" + generateOrderNumber(prefix));
        
        // 根据状态设置相应的支付状态
        if (Arrays.asList("completed", "delivered", "shipped").contains(status)) {
            order.put("paymentStatus", "paid");
        }
        
        return order;
    }
    
    /**
     * 生成批量测试订单
     */
    public static List<Map<String, Object>> generateBatchOrders(String prefix, int count) {
        List<Map<String, Object>> orders = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            Map<String, Object> order = generateBasicOrder(prefix);
            order.put("orderNumber", prefix + "BATCH_" + i + "_" + System.currentTimeMillis());
            orders.add(order);
        }
        
        return orders;
    }
    
    /**
     * 生成多样化测试订单（不同类型、状态、金额）
     */
    public static List<Map<String, Object>> generateDiverseOrders(String prefix, int count) {
        List<Map<String, Object>> orders = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            Map<String, Object> order;
            
            // 随机选择订单类型
            if (i % 2 == 0) {
                order = generateSalesOrder(prefix);
            } else {
                order = generateRentalOrder(prefix);
            }
            
            // 随机设置状态
            String status = ORDER_STATUSES[i % ORDER_STATUSES.length];
            order.put("status", status);
            
            // 设置不同的金额范围
            BigDecimal baseAmount = new BigDecimal("100");
            BigDecimal multiplier = new BigDecimal(String.valueOf(1 + i * 0.5));
            order.put("totalAmount", baseAmount.multiply(multiplier));
            
            order.put("orderNumber", prefix + "DIVERSE_" + i + "_" + System.currentTimeMillis());
            orders.add(order);
        }
        
        return orders;
    }
    
    /**
     * 生成性能测试订单
     */
    public static Map<String, Object> generatePerformanceTestOrder(String prefix) {
        Map<String, Object> order = generateCompleteOrder(prefix);
        order.put("orderNumber", prefix + "PERF_" + System.currentTimeMillis());
        
        // 添加更多订单项以测试性能
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> orderItems = (List<Map<String, Object>>) order.get("orderItems");
        
        // 扩展到10个订单项
        while (orderItems.size() < 10) {
            orderItems.add(generateOrderItem());
        }
        
        // 重新计算总金额
        BigDecimal totalAmount = orderItems.stream()
                .map(item -> new BigDecimal(item.get("subtotal").toString()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.put("totalAmount", totalAmount);
        
        return order;
    }
    
    /**
     * 生成无效订单数据（用于错误处理测试）
     */
    public static Map<String, Object> generateInvalidOrder(String invalidType) {
        Map<String, Object> order = new HashMap<>();
        
        switch (invalidType) {
            case "empty_order_number":
                order.put("orderNumber", "");
                order.put("customerName", "测试客户");
                order.put("type", "sales");
                break;
                
            case "empty_customer_name":
                order.put("orderNumber", "INVALID_" + System.currentTimeMillis());
                order.put("customerName", "");
                order.put("type", "sales");
                break;
                
            case "invalid_type":
                order.put("orderNumber", "INVALID_" + System.currentTimeMillis());
                order.put("customerName", "测试客户");
                order.put("type", "invalid_type");
                break;
                
            case "negative_amount":
                order.put("orderNumber", "INVALID_" + System.currentTimeMillis());
                order.put("customerName", "测试客户");
                order.put("type", "sales");
                order.put("totalAmount", new BigDecimal("-100"));
                break;
                
            case "missing_required_fields":
                order.put("notes", "缺少必需字段的订单");
                break;
                
            default:
                order.put("orderNumber", "INVALID_" + System.currentTimeMillis());
                order.put("customerName", "无效测试客户");
                order.put("type", "sales");
        }
        
        return order;
    }
    
    /**
     * 生成订单项列表
     */
    private static List<Map<String, Object>> generateOrderItems() {
        List<Map<String, Object>> items = new ArrayList<>();
        int itemCount = ThreadLocalRandom.current().nextInt(1, 4); // 1-3个订单项
        
        for (int i = 0; i < itemCount; i++) {
            items.add(generateOrderItem());
        }
        
        return items;
    }
    
    /**
     * 生成单个订单项
     */
    private static Map<String, Object> generateOrderItem() {
        Map<String, Object> item = new HashMap<>();
        
        int productId = ThreadLocalRandom.current().nextInt(1, 101);
        String productName = PRODUCT_NAMES[ThreadLocalRandom.current().nextInt(PRODUCT_NAMES.length)];
        int quantity = ThreadLocalRandom.current().nextInt(1, 6);
        BigDecimal unitPrice = generateRandomAmount();
        BigDecimal subtotal = unitPrice.multiply(new BigDecimal(quantity));
        
        item.put("productId", productId);
        item.put("productName", productName);
        item.put("quantity", quantity);
        item.put("unitPrice", unitPrice);
        item.put("subtotal", subtotal);
        
        return item;
    }
    
    /**
     * 生成订单号
     */
    private static String generateOrderNumber(String prefix) {
        return prefix + System.currentTimeMillis() + "_" + ThreadLocalRandom.current().nextInt(1000, 9999);
    }
    
    /**
     * 获取随机客户名
     */
    private static String getRandomCustomerName() {
        return CUSTOMER_NAMES[ThreadLocalRandom.current().nextInt(CUSTOMER_NAMES.length)];
    }
    
    /**
     * 生成手机号
     */
    private static String generatePhoneNumber() {
        return "138" + String.format("%08d", ThreadLocalRandom.current().nextInt(10000000, 99999999));
    }
    
    /**
     * 生成邮箱
     */
    private static String generateEmail() {
        String[] domains = {"example.com", "test.com", "demo.com", "sample.com"};
        String username = "user" + ThreadLocalRandom.current().nextInt(1000, 9999);
        String domain = domains[ThreadLocalRandom.current().nextInt(domains.length)];
        return username + "@" + domain;
    }
    
    /**
     * 获取随机订单类型
     */
    private static String getRandomOrderType() {
        return ORDER_TYPES[ThreadLocalRandom.current().nextInt(ORDER_TYPES.length)];
    }
    
    /**
     * 获取随机地址
     */
    private static String getRandomAddress() {
        return ADDRESSES[ThreadLocalRandom.current().nextInt(ADDRESSES.length)];
    }
    
    /**
     * 生成随机金额
     */
    private static BigDecimal generateRandomAmount() {
        double amount = ThreadLocalRandom.current().nextDouble(100.0, 10000.0);
        return new BigDecimal(String.format("%.2f", amount));
    }
    
    /**
     * 生成状态更新数据
     */
    public static Map<String, Object> generateStatusUpdate(String targetStatus, String operator) {
        Map<String, Object> statusUpdate = new HashMap<>();
        statusUpdate.put("status", targetStatus);
        statusUpdate.put("operator", operator != null ? operator : "systemTest");
        statusUpdate.put("notes", "集成测试状态更新: " + targetStatus);
        statusUpdate.put("updateTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return statusUpdate;
    }
    
    /**
     * 生成批量状态更新数据
     */
    public static Map<String, Object> generateBatchStatusUpdate(List<Long> orderIds, String targetStatus, String operator) {
        Map<String, Object> batchUpdate = new HashMap<>();
        batchUpdate.put("orderIds", orderIds);
        batchUpdate.put("status", targetStatus);
        batchUpdate.put("operator", operator != null ? operator : "systemTest");
        batchUpdate.put("notes", "批量状态更新: " + targetStatus);
        return batchUpdate;
    }
    
    /**
     * 生成搜索关键词
     */
    public static List<String> generateSearchKeywords(String prefix) {
        List<String> keywords = new ArrayList<>();
        keywords.add(prefix);
        keywords.add("测试");
        keywords.add("客户");
        keywords.add("订单");
        keywords.add("SAL");
        keywords.add("REN");
        return keywords;
    }
    
    /**
     * 生成筛选条件
     */
    public static Map<String, Object> generateFilterConditions() {
        Map<String, Object> filters = new HashMap<>();
        
        // 随机添加筛选条件
        if (ThreadLocalRandom.current().nextBoolean()) {
            filters.put("type", getRandomOrderType());
        }
        
        if (ThreadLocalRandom.current().nextBoolean()) {
            filters.put("status", ORDER_STATUSES[ThreadLocalRandom.current().nextInt(ORDER_STATUSES.length)]);
        }
        
        if (ThreadLocalRandom.current().nextBoolean()) {
            filters.put("paymentStatus", PAYMENT_STATUSES[ThreadLocalRandom.current().nextInt(PAYMENT_STATUSES.length)]);
        }
        
        // 日期范围
        if (ThreadLocalRandom.current().nextBoolean()) {
            filters.put("startDate", LocalDateTime.now().minusDays(30).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            filters.put("endDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        
        return filters;
    }
    
    /**
     * 生成分页参数
     */
    public static Map<String, Object> generatePaginationParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("page", ThreadLocalRandom.current().nextInt(1, 6)); // 1-5页
        params.put("size", ThreadLocalRandom.current().nextInt(5, 21)); // 5-20条
        return params;
    }
}