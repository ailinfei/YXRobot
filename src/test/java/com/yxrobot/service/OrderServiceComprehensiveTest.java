package com.yxrobot.service;

import com.yxrobot.dto.*;
import com.yxrobot.entity.*;
import com.yxrobot.exception.OrderException;
import com.yxrobot.mapper.*;
import com.yxrobot.validator.OrderFormValidator;
import com.yxrobot.validator.DataIntegrityValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 订单管理服务综合测试类
 * 任务16: 编写单元测试 - 测试订单服务的业务逻辑
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("订单管理服务综合测试")
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class OrderServiceComprehensiveTest {

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderItemMapper orderItemMapper;

    @Mock
    private ShippingInfoMapper shippingInfoMapper;

    @Mock
    private OrderLogMapper orderLogMapper;

    @Mock
    private OrderFormValidator orderFormValidator;

    @Mock
    private DataIntegrityValidator dataIntegrityValidator;

    @InjectMocks
    private OrderService orderService;

    private Order testOrder;
    private OrderDTO testOrderDTO;
    private OrderCreateDTO testCreateDTO;
    private OrderQueryDTO testQueryDTO;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        testOrder = createTestOrder();
        testOrderDTO = createTestOrderDTO();
        testCreateDTO = createTestOrderCreateDTO();
        testQueryDTO = createTestOrderQueryDTO();
    }

    @Test
    @DisplayName("01. 订单创建成功测试")
    void testCreateOrderSuccess() {
        System.out.println("🔍 开始订单创建成功测试...");
        
        // 准备测试数据
        when(orderMapper.insert(any(Order.class))).thenReturn(1);
        when(orderItemMapper.insert(any(OrderItem.class))).thenReturn(1);
        doNothing().when(orderFormValidator).validateCreateForm(any(OrderCreateDTO.class));
        doNothing().when(dataIntegrityValidator).validateCreateDataIntegrity(any(OrderCreateDTO.class));
        
        // 执行测试
        OrderDTO result = orderService.createOrder(testCreateDTO);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(testCreateDTO.getOrderNumber(), result.getOrderNumber());
        assertEquals(testCreateDTO.getCustomerName(), result.getCustomerName());
        assertEquals(testCreateDTO.getType(), result.getType());
        
        // 验证方法调用
        verify(orderFormValidator).validateCreateForm(testCreateDTO);
        verify(dataIntegrityValidator).validateCreateDataIntegrity(testCreateDTO);
        verify(orderMapper).insert(any(Order.class));
        
        System.out.println("✅ 订单创建成功测试通过");
    }

    @Test
    @DisplayName("02. 订单创建验证失败测试")
    void testCreateOrderValidationFailed() {
        System.out.println("🔍 开始订单创建验证失败测试...");
        
        // 准备测试数据 - 模拟验证失败
        doThrow(OrderException.validationFailed("orderNumber", "订单号不能为空"))
            .when(orderFormValidator).validateCreateForm(any(OrderCreateDTO.class));
        
        // 执行测试并验证异常
        OrderException exception = assertThrows(OrderException.class, () -> {
            orderService.createOrder(testCreateDTO);
        });
        
        // 验证异常信息
        assertEquals("VALIDATION_FAILED", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("订单号"));
        
        // 验证方法调用
        verify(orderFormValidator).validateCreateForm(testCreateDTO);
        verify(orderMapper, never()).insert(any(Order.class));
        
        System.out.println("✅ 订单创建验证失败测试通过");
    }

    @Test
    @DisplayName("03. 订单查询成功测试")
    void testGetOrderByIdSuccess() {
        System.out.println("🔍 开始订单查询成功测试...");
        
        // 准备测试数据
        Long orderId = 1L;
        when(orderMapper.selectById(orderId)).thenReturn(testOrder);
        when(orderItemMapper.selectByOrderId(orderId)).thenReturn(createTestOrderItems());
        
        // 执行测试
        OrderDTO result = orderService.getOrderById(orderId);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(testOrder.getId(), result.getId());
        assertEquals(testOrder.getOrderNumber(), result.getOrderNumber());
        assertEquals(testOrder.getCustomerName(), result.getCustomerName());
        
        // 验证方法调用
        verify(orderMapper).selectById(orderId);
        verify(orderItemMapper).selectByOrderId(orderId);
        
        System.out.println("✅ 订单查询成功测试通过");
    }

    @Test
    @DisplayName("04. 订单查询不存在测试")
    void testGetOrderByIdNotFound() {
        System.out.println("🔍 开始订单查询不存在测试...");
        
        // 准备测试数据
        Long orderId = 999L;
        when(orderMapper.selectById(orderId)).thenReturn(null);
        
        // 执行测试并验证异常
        OrderException exception = assertThrows(OrderException.class, () -> {
            orderService.getOrderById(orderId);
        });
        
        // 验证异常信息
        assertEquals("ORDER_NOT_FOUND", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("订单不存在"));
        
        // 验证方法调用
        verify(orderMapper).selectById(orderId);
        verify(orderItemMapper, never()).selectByOrderId(orderId);
        
        System.out.println("✅ 订单查询不存在测试通过");
    }

    @Test
    @DisplayName("05. 订单更新成功测试")
    void testUpdateOrderSuccess() {
        System.out.println("🔍 开始订单更新成功测试...");
        
        // 准备测试数据
        Long orderId = 1L;
        when(orderMapper.selectById(orderId)).thenReturn(testOrder);
        when(orderMapper.updateById(any(Order.class))).thenReturn(1);
        doNothing().when(orderFormValidator).validateUpdateForm(any(OrderDTO.class));
        doNothing().when(dataIntegrityValidator).validateUpdateDataIntegrity(eq(orderId), any(OrderDTO.class));
        
        // 执行测试
        OrderDTO result = orderService.updateOrder(orderId, testOrderDTO);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(testOrderDTO.getCustomerName(), result.getCustomerName());
        
        // 验证方法调用
        verify(orderFormValidator).validateUpdateForm(testOrderDTO);
        verify(dataIntegrityValidator).validateUpdateDataIntegrity(orderId, testOrderDTO);
        verify(orderMapper).selectById(orderId);
        verify(orderMapper).updateById(any(Order.class));
        
        System.out.println("✅ 订单更新成功测试通过");
    }

    @Test
    @DisplayName("06. 订单更新不存在测试")
    void testUpdateOrderNotFound() {
        System.out.println("🔍 开始订单更新不存在测试...");
        
        // 准备测试数据
        Long orderId = 999L;
        when(orderMapper.selectById(orderId)).thenReturn(null);
        
        // 执行测试并验证异常
        OrderException exception = assertThrows(OrderException.class, () -> {
            orderService.updateOrder(orderId, testOrderDTO);
        });
        
        // 验证异常信息
        assertEquals("ORDER_NOT_FOUND", exception.getErrorCode());
        
        // 验证方法调用
        verify(orderMapper).selectById(orderId);
        verify(orderMapper, never()).updateById(any(Order.class));
        
        System.out.println("✅ 订单更新不存在测试通过");
    }

    @Test
    @DisplayName("07. 订单删除成功测试")
    void testDeleteOrderSuccess() {
        System.out.println("🔍 开始订单删除成功测试...");
        
        // 准备测试数据
        Long orderId = 1L;
        when(orderMapper.selectById(orderId)).thenReturn(testOrder);
        when(orderMapper.updateById(any(Order.class))).thenReturn(1);
        
        // 执行测试
        assertDoesNotThrow(() -> {
            orderService.deleteOrder(orderId);
        });
        
        // 验证方法调用
        verify(orderMapper).selectById(orderId);
        verify(orderMapper).updateById(any(Order.class));
        
        System.out.println("✅ 订单删除成功测试通过");
    }

    @Test
    @DisplayName("08. 订单列表查询测试")
    void testGetOrdersWithPagination() {
        System.out.println("🔍 开始订单列表查询测试...");
        
        // 准备测试数据
        List<Order> orderList = Arrays.asList(testOrder, createTestOrder());
        when(orderMapper.selectOrdersWithPagination(anyString(), anyString(), anyString(), any(), any(), anyInt(), anyInt())).thenReturn(orderList);
        when(orderMapper.countOrders(anyString(), anyString(), anyString(), any(), any())).thenReturn(2);
        
        // 执行测试
        var result = orderService.getOrders(testQueryDTO);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.getList().size());
        assertEquals(2L, result.getTotal());
        
        // 验证方法调用
        verify(orderMapper).selectOrdersWithPagination(anyString(), anyString(), anyString(), any(), any(), anyInt(), anyInt());
        verify(orderMapper).countOrders(anyString(), anyString(), anyString(), any(), any());
        
        System.out.println("✅ 订单列表查询测试通过");
    }

    @Test
    @DisplayName("09. 订单搜索功能测试")
    void testSearchOrders() {
        System.out.println("🔍 开始订单搜索功能测试...");
        
        // 准备测试数据
        String keyword = "ORD123";
        testQueryDTO.setKeyword(keyword);
        List<Order> searchResults = Arrays.asList(testOrder);
        when(orderMapper.selectOrdersWithPagination(anyString(), anyString(), anyString(), any(), any(), anyInt(), anyInt())).thenReturn(searchResults);
        when(orderMapper.countOrders(anyString(), anyString(), anyString(), any(), any())).thenReturn(1);
        
        // 执行测试
        var result = orderService.getOrders(testQueryDTO);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getList().size());
        assertEquals(1L, result.getTotal());
        
        // 验证搜索参数
        verify(orderMapper).selectOrdersWithPagination(anyString(), anyString(), anyString(), any(), any(), anyInt(), anyInt());
        
        System.out.println("✅ 订单搜索功能测试通过");
    }

    @Test
    @DisplayName("10. 订单筛选功能测试")
    void testFilterOrders() {
        System.out.println("🔍 开始订单筛选功能测试...");
        
        // 准备测试数据
        testQueryDTO.setType("sales");
        testQueryDTO.setStatus("pending");
        List<Order> filterResults = Arrays.asList(testOrder);
        when(orderMapper.selectOrdersWithPagination(anyString(), anyString(), anyString(), any(), any(), anyInt(), anyInt())).thenReturn(filterResults);
        when(orderMapper.countOrders(anyString(), anyString(), anyString(), any(), any())).thenReturn(1);
        
        // 执行测试
        var result = orderService.getOrders(testQueryDTO);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getList().size());
        
        // 验证筛选参数
        verify(orderMapper).selectOrdersWithPagination(anyString(), anyString(), anyString(), any(), any(), anyInt(), anyInt());
        
        System.out.println("✅ 订单筛选功能测试通过");
    }

    @Test
    @DisplayName("11. 订单金额计算测试")
    void testOrderAmountCalculation() {
        System.out.println("🔍 开始订单金额计算测试...");
        
        // 准备测试数据
        testCreateDTO.setOrderItems(createTestOrderItemCreateDTOs());
        BigDecimal expectedTotal = new BigDecimal("300.00"); // 2*100 + 1*100
        testCreateDTO.setTotalAmount(expectedTotal);
        
        when(orderMapper.insert(any(Order.class))).thenReturn(1);
        when(orderItemMapper.insert(any(OrderItem.class))).thenReturn(1);
        doNothing().when(orderFormValidator).validateCreateForm(any(OrderCreateDTO.class));
        doNothing().when(dataIntegrityValidator).validateCreateDataIntegrity(any(OrderCreateDTO.class));
        
        // 执行测试
        OrderDTO result = orderService.createOrder(testCreateDTO);
        
        // 验证金额计算
        assertNotNull(result);
        assertEquals(expectedTotal, result.getTotalAmount());
        
        System.out.println("✅ 订单金额计算测试通过");
    }

    @Test
    @DisplayName("12. 订单业务规则验证测试")
    void testOrderBusinessRuleValidation() {
        System.out.println("🔍 开始订单业务规则验证测试...");
        
        // 准备测试数据 - 租赁订单缺少租赁日期
        testCreateDTO.setType("rental");
        testCreateDTO.setRentalStartDate(null);
        testCreateDTO.setRentalEndDate(null);
        
        doThrow(OrderException.businessRuleViolation("租赁规则", "租赁订单必须设置租赁日期"))
            .when(orderFormValidator).validateCreateForm(any(OrderCreateDTO.class));
        
        // 执行测试并验证异常
        OrderException exception = assertThrows(OrderException.class, () -> {
            orderService.createOrder(testCreateDTO);
        });
        
        // 验证异常信息
        assertEquals("BUSINESS_RULE_VIOLATION", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("租赁"));
        
        System.out.println("✅ 订单业务规则验证测试通过");
    }

    // 辅助方法：创建测试订单
    private Order createTestOrder() {
        Order order = new Order();
        order.setId(1L);
        order.setOrderNumber("ORD1234567890");
        order.setCustomerName("测试客户");
        order.setCustomerPhone("13800138000");
        order.setCustomerEmail("test@example.com");
        order.setType(OrderType.SALES);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(new BigDecimal("200.00"));
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setIsDeleted(false);
        return order;
    }

    // 辅助方法：创建测试订单DTO
    private OrderDTO createTestOrderDTO() {
        OrderDTO dto = new OrderDTO();
        dto.setId(1L);
        dto.setOrderNumber("ORD1234567890");
        dto.setCustomerName("更新后的客户");
        dto.setCustomerPhone("13800138000");
        dto.setCustomerEmail("test@example.com");
        dto.setType("sales");
        dto.setStatus("pending");
        dto.setTotalAmount(new BigDecimal("200.00"));
        return dto;
    }

    // 辅助方法：创建测试订单创建DTO
    private OrderCreateDTO createTestOrderCreateDTO() {
        OrderCreateDTO dto = new OrderCreateDTO();
        dto.setOrderNumber("ORD1234567890");
        dto.setCustomerName("测试客户");
        dto.setCustomerPhone("13800138000");
        dto.setCustomerEmail("test@example.com");
        dto.setType("sales");
        dto.setTotalAmount(new BigDecimal("200.00"));
        dto.setNotes("测试订单");
        dto.setDeliveryAddress("测试地址");  // 修复方法调用问题
        return dto;
    }

    // 辅助方法：创建测试查询DTO
    private OrderQueryDTO createTestOrderQueryDTO() {
        OrderQueryDTO dto = new OrderQueryDTO();
        dto.setPage(1);
        dto.setSize(10);
        return dto;
    }

    // 辅助方法：创建测试订单项
    private List<OrderItem> createTestOrderItems() {
        List<OrderItem> items = new ArrayList<>();
        
        OrderItem item1 = new OrderItem();
        item1.setId(1L);
        item1.setOrderId(1L);
        item1.setProductId(1L);
        item1.setProductName("测试产品1");
        item1.setQuantity(2);
        item1.setUnitPrice(new BigDecimal("100.00"));
        item1.setTotalPrice(new BigDecimal("200.00"));
        items.add(item1);
        
        return items;
    }

    // 辅助方法：创建测试订单项创建DTO
    private List<OrderCreateDTO.OrderItemCreateDTO> createTestOrderItemCreateDTOs() {
        List<OrderCreateDTO.OrderItemCreateDTO> items = new ArrayList<>();
        
        OrderCreateDTO.OrderItemCreateDTO item1 = new OrderCreateDTO.OrderItemCreateDTO();
        item1.setProductId(1L);
        item1.setProductName("测试产品1");
        item1.setQuantity(2);
        item1.setUnitPrice(new BigDecimal("100.00"));
        item1.setTotalPrice(new BigDecimal("200.00"));
        items.add(item1);

        OrderCreateDTO.OrderItemCreateDTO item2 = new OrderCreateDTO.OrderItemCreateDTO();
        item2.setProductId(2L);
        item2.setProductName("测试产品2");
        item2.setQuantity(1);
        item2.setUnitPrice(new BigDecimal("100.00"));
        item2.setTotalPrice(new BigDecimal("100.00"));
        items.add(item2);

        return items;
    }
}