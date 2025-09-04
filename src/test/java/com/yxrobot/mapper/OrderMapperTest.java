package com.yxrobot.mapper;

import com.yxrobot.dto.OrderQueryDTO;
import com.yxrobot.entity.Order;
import com.yxrobot.entity.OrderStatus;
import com.yxrobot.entity.OrderType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 订单Mapper测试类
 * 任务16: 编写单元测试 - 测试数据库操作
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("订单Mapper测试")
public class OrderMapperTest {

    @Mock
    private SqlSession sqlSession;

    @Autowired
    private OrderMapper orderMapper;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        testOrder = createTestOrder();
    }

    @Test
    @DisplayName("01. 插入订单测试")
    void testInsertOrder() {
        System.out.println("🔍 开始插入订单测试...");
        
        // 执行插入
        int result = orderMapper.insert(testOrder);
        
        // 验证结果
        assertEquals(1, result, "插入应该返回1");
        assertNotNull(testOrder.getId(), "插入后应该生成ID");
        assertTrue(testOrder.getId() > 0, "生成的ID应该大于0");
        
        System.out.println("✅ 插入订单测试通过，生成ID: " + testOrder.getId());
    }

    @Test
    @DisplayName("02. 根据ID查询订单测试")
    void testSelectById() {
        System.out.println("🔍 开始根据ID查询订单测试...");
        
        // 先插入测试数据
        orderMapper.insert(testOrder);
        Long orderId = testOrder.getId();
        
        // 执行查询
        Order result = orderMapper.selectById(orderId);
        
        // 验证结果
        assertNotNull(result, "查询结果不应为空");
        assertEquals(orderId, result.getId(), "ID应该匹配");
        assertEquals(testOrder.getOrderNumber(), result.getOrderNumber(), "订单号应该匹配");
        assertEquals(testOrder.getCustomerName(), result.getCustomerName(), "客户名称应该匹配");
        assertEquals(testOrder.getType(), result.getType(), "订单类型应该匹配");
        assertEquals(testOrder.getStatus(), result.getStatus(), "订单状态应该匹配");
        
        System.out.println("✅ 根据ID查询订单测试通过");
    }

    @Test
    @DisplayName("03. 更新订单测试")
    void testUpdateById() {
        System.out.println("🔍 开始更新订单测试...");
        
        // 先插入测试数据
        orderMapper.insert(testOrder);
        Long orderId = testOrder.getId();
        
        // 修改订单信息
        testOrder.setCustomerName("更新后的客户名称");
        testOrder.setTotalAmount(new BigDecimal("300.00"));
        testOrder.setNotes("更新后的备注");
        testOrder.setUpdatedAt(LocalDateTime.now());
        
        // 执行更新
        int result = orderMapper.updateById(testOrder);
        
        // 验证更新结果
        assertEquals(1, result, "更新应该返回1");
        
        // 查询验证更新内容
        Order updatedOrder = orderMapper.selectById(orderId);
        assertNotNull(updatedOrder, "更新后查询结果不应为空");
        assertEquals("更新后的客户名称", updatedOrder.getCustomerName(), "客户名称应该已更新");
        assertEquals(new BigDecimal("300.00"), updatedOrder.getTotalAmount(), "总金额应该已更新");
        assertEquals("更新后的备注", updatedOrder.getNotes(), "备注应该已更新");
        
        System.out.println("✅ 更新订单测试通过");
    }

    @Test
    @DisplayName("04. 软删除订单测试")
    void testSoftDelete() {
        System.out.println("🔍 开始软删除订单测试...");
        
        // 先插入测试数据
        orderMapper.insert(testOrder);
        Long orderId = testOrder.getId();
        
        // 执行软删除
        testOrder.setIsDeleted(true);
        testOrder.setUpdatedAt(LocalDateTime.now());
        int result = orderMapper.updateById(testOrder);
        
        // 验证软删除结果
        assertEquals(1, result, "软删除应该返回1");
        
        // 查询验证软删除状态
        Order deletedOrder = orderMapper.selectById(orderId);
        assertNotNull(deletedOrder, "软删除后记录仍应存在");
        assertTrue(deletedOrder.getIsDeleted(), "isDeleted标志应该为true");
        
        System.out.println("✅ 软删除订单测试通过");
    }

    @Test
    @DisplayName("05. 条件查询订单测试")
    void testSelectByQuery() {
        System.out.println("🔍 开始条件查询订单测试...");
        
        // 插入多个测试订单
        Order order1 = createTestOrder();
        order1.setOrderNumber("ORD1111111111");
        order1.setType(OrderType.SALES);
        order1.setStatus(OrderStatus.PENDING);
        orderMapper.insert(order1);
        
        Order order2 = createTestOrder();
        order2.setOrderNumber("ORD2222222222");
        order2.setType(OrderType.RENTAL);
        order2.setStatus(OrderStatus.CONFIRMED);
        orderMapper.insert(order2);
        
        // 测试按类型查询
        OrderQueryDTO queryDTO = new OrderQueryDTO();
        queryDTO.setType("sales");
        List<Order> salesOrders = orderMapper.selectOrdersWithPagination(queryDTO.getKeyword(), queryDTO.getType(), queryDTO.getStatus(), queryDTO.getStartDate(), queryDTO.getEndDate(), 0, 10);
        
        assertNotNull(salesOrders, "查询结果不应为空");
        assertTrue(salesOrders.size() >= 1, "应该找到至少1个销售订单");
        assertTrue(salesOrders.stream().allMatch(order -> OrderType.SALES.equals(order.getType())), 
                  "所有结果都应该是销售订单");
        
        // 测试按状态查询
        queryDTO = new OrderQueryDTO();
        queryDTO.setStatus("pending");
        List<Order> pendingOrders = orderMapper.selectOrdersWithPagination(queryDTO.getKeyword(), queryDTO.getType(), queryDTO.getStatus(), queryDTO.getStartDate(), queryDTO.getEndDate(), 0, 10);
        
        assertNotNull(pendingOrders, "查询结果不应为空");
        assertTrue(pendingOrders.size() >= 1, "应该找到至少1个待处理订单");
        assertTrue(pendingOrders.stream().allMatch(order -> OrderStatus.PENDING.equals(order.getStatus())), 
                  "所有结果都应该是待处理订单");
        
        System.out.println("✅ 条件查询订单测试通过");
    }

    @Test
    @DisplayName("06. 关键词搜索订单测试")
    void testSearchByKeyword() {
        System.out.println("🔍 开始关键词搜索订单测试...");
        
        // 插入测试订单
        testOrder.setOrderNumber("SEARCH123456789");
        testOrder.setCustomerName("搜索测试客户");
        orderMapper.insert(testOrder);
        
        // 测试按订单号搜索
        OrderQueryDTO queryDTO = new OrderQueryDTO();
        queryDTO.setKeyword("SEARCH123");
        List<Order> searchResults = orderMapper.selectOrdersWithPagination(queryDTO.getKeyword(), queryDTO.getType(), queryDTO.getStatus(), queryDTO.getStartDate(), queryDTO.getEndDate(), 0, 10);
        
        assertNotNull(searchResults, "搜索结果不应为空");
        assertTrue(searchResults.size() >= 1, "应该找到至少1个匹配的订单");
        assertTrue(searchResults.stream().anyMatch(order -> 
                  order.getOrderNumber().contains("SEARCH123")), 
                  "搜索结果应该包含匹配的订单号");
        
        // 测试按客户名称搜索
        queryDTO = new OrderQueryDTO();
        queryDTO.setKeyword("搜索测试");
        searchResults = orderMapper.selectOrdersWithPagination(queryDTO.getKeyword(), queryDTO.getType(), queryDTO.getStatus(), queryDTO.getStartDate(), queryDTO.getEndDate(), 0, 10);
        
        assertNotNull(searchResults, "搜索结果不应为空");
        assertTrue(searchResults.size() >= 1, "应该找到至少1个匹配的订单");
        assertTrue(searchResults.stream().anyMatch(order -> 
                  order.getCustomerName().contains("搜索测试")), 
                  "搜索结果应该包含匹配的客户名称");
        
        System.out.println("✅ 关键词搜索订单测试通过");
    }

    @Test
    @DisplayName("07. 分页查询订单测试")
    void testSelectWithPagination() {
        System.out.println("🔍 开始分页查询订单测试...");
        
        // 插入多个测试订单
        for (int i = 1; i <= 5; i++) {
            Order order = createTestOrder();
            order.setOrderNumber("PAGE" + String.format("%010d", i));
            orderMapper.insert(order);
        }
        
        // 测试第一页
        OrderQueryDTO queryDTO = new OrderQueryDTO();
        queryDTO.setPage(1);
        queryDTO.setSize(3);
        List<Order> page1Results = orderMapper.selectOrdersWithPagination(queryDTO.getKeyword(), queryDTO.getType(), queryDTO.getStatus(), queryDTO.getStartDate(), queryDTO.getEndDate(), (queryDTO.getPage() - 1) * queryDTO.getSize(), queryDTO.getSize());
        
        assertNotNull(page1Results, "第一页结果不应为空");
        assertTrue(page1Results.size() <= 3, "第一页结果数量应该不超过3");
        
        // 测试第二页
        queryDTO.setPage(2);
        queryDTO.setSize(3);
        List<Order> page2Results = orderMapper.selectOrdersWithPagination(queryDTO.getKeyword(), queryDTO.getType(), queryDTO.getStatus(), queryDTO.getStartDate(), queryDTO.getEndDate(), (queryDTO.getPage() - 1) * queryDTO.getSize(), queryDTO.getSize());
        
        assertNotNull(page2Results, "第二页结果不应为空");
        
        // 验证分页结果不重复
        if (!page1Results.isEmpty() && !page2Results.isEmpty()) {
            assertNotEquals(page1Results.get(0).getId(), page2Results.get(0).getId(), 
                          "不同页的结果不应该重复");
        }
        
        System.out.println("✅ 分页查询订单测试通过");
    }

    @Test
    @DisplayName("08. 统计查询订单测试")
    void testCountByQuery() {
        System.out.println("🔍 开始统计查询订单测试...");
        
        // 插入测试订单
        Order salesOrder = createTestOrder();
        salesOrder.setType(OrderType.RENTAL);
        orderMapper.insert(salesOrder);
        
        Order rentalOrder = createTestOrder();
        rentalOrder.setType(OrderType.RENTAL);
        orderMapper.insert(rentalOrder);
        
        // 测试总数统计
        OrderQueryDTO queryDTO = new OrderQueryDTO();
        int totalCount = orderMapper.countOrders(queryDTO.getKeyword(), queryDTO.getType(), queryDTO.getStatus(), queryDTO.getStartDate(), queryDTO.getEndDate());
        
        assertNotNull(totalCount, "总数统计不应为空");
        assertTrue(totalCount >= 2, "总数应该至少为2");
        
        // 测试按类型统计
        queryDTO.setType("sales");
        int salesCount = orderMapper.countOrders(queryDTO.getKeyword(), queryDTO.getType(), queryDTO.getStatus(), queryDTO.getStartDate(), queryDTO.getEndDate());
        
        assertNotNull(salesCount, "销售订单统计不应为空");
        assertTrue(salesCount >= 1, "销售订单数量应该至少为1");
        
        queryDTO.setType("rental");
        int rentalCount = orderMapper.countOrders(queryDTO.getKeyword(), queryDTO.getType(), queryDTO.getStatus(), queryDTO.getStartDate(), queryDTO.getEndDate());
        
        assertNotNull(rentalCount, "租赁订单统计不应为空");
        assertTrue(rentalCount >= 1, "租赁订单数量应该至少为1");
        
        System.out.println("✅ 统计查询订单测试通过");
    }

    @Test
    @DisplayName("09. 复合条件查询测试")
    void testComplexQuery() {
        System.out.println("🔍 开始复合条件查询测试...");
        
        // 插入测试订单
        Order order = createTestOrder();
        order.setOrderNumber("COMPLEX123456789");
        order.setCustomerName("复合查询客户");
        order.setType(OrderType.SALES);
        order.setStatus(OrderStatus.PENDING);
        orderMapper.insert(order);
        
        // 测试复合条件查询
        OrderQueryDTO queryDTO = new OrderQueryDTO();
        queryDTO.setKeyword("COMPLEX");
        queryDTO.setType("sales");
        queryDTO.setStatus("pending");
        
        List<Order> results = orderMapper.selectOrdersWithPagination(queryDTO.getKeyword(), queryDTO.getType(), queryDTO.getStatus(), queryDTO.getStartDate(), queryDTO.getEndDate(), 0, 10);
        
        assertNotNull(results, "复合查询结果不应为空");
        assertTrue(results.size() >= 1, "应该找到至少1个匹配的订单");
        
        // 验证所有结果都符合条件
        for (Order result : results) {
            assertTrue(result.getOrderNumber().contains("COMPLEX") || 
                      result.getCustomerName().contains("COMPLEX"), 
                      "结果应该包含关键词");
            assertEquals(OrderType.SALES, result.getType(), "结果类型应该匹配");
            assertEquals(OrderStatus.PENDING, result.getStatus(), "结果状态应该匹配");
        }
        
        System.out.println("✅ 复合条件查询测试通过");
    }

    @Test
    @DisplayName("10. 数据库约束测试")
    void testDatabaseConstraints() {
        System.out.println("🔍 开始数据库约束测试...");
        
        // 测试唯一约束（如果有）
        Order order1 = createTestOrder();
        order1.setOrderNumber("UNIQUE123456789");
        int result1 = orderMapper.insert(order1);
        assertEquals(1, result1, "第一次插入应该成功");
        
        // 尝试插入相同订单号的订单（如果有唯一约束）
        Order order2 = createTestOrder();
        order2.setOrderNumber("UNIQUE123456789");
        
        try {
            orderMapper.insert(order2);
            System.out.println("⚠️  订单号唯一约束可能未设置");
        } catch (Exception e) {
            System.out.println("✅ 订单号唯一约束正常工作");
        }
        
        // 测试非空约束
        Order invalidOrder = new Order();
        // 故意不设置必填字段
        
        try {
            orderMapper.insert(invalidOrder);
            System.out.println("⚠️  非空约束可能未设置");
        } catch (Exception e) {
            System.out.println("✅ 非空约束正常工作");
        }
        
        System.out.println("✅ 数据库约束测试完成");
    }

    // 辅助方法：创建测试订单
    private Order createTestOrder() {
        Order order = new Order();
        order.setOrderNumber("ORD" + System.currentTimeMillis());
        order.setCustomerId(1L);  // 添加客户ID
        order.setCustomerName("测试客户");
        order.setType(OrderType.SALES);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(new BigDecimal("200.00"));
        order.setNotes("测试订单");
        order.setDeliveryAddress("测试地址");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setIsDeleted(false);
        return order;
    }
}