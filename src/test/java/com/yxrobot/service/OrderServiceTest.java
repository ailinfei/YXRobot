package com.yxrobot.service;

import com.yxrobot.dto.*;
import com.yxrobot.entity.*;
import com.yxrobot.mapper.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 订单管理服务测试类
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderItemMapper orderItemMapper;

    @Mock
    private ShippingInfoMapper shippingInfoMapper;

    @Mock
    private OrderLogMapper orderLogMapper;

    @InjectMocks
    private OrderService orderService;

    private Order testOrder;
    private OrderDTO testOrderDTO;
    private OrderCreateDTO testCreateDTO;
    private OrderQueryDTO testQueryDTO;

    @BeforeEach
    void setUp() {
        // 创建测试订单实体
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setOrderNumber("ORD202501010001");
        testOrder.setType(OrderType.SALES);
        testOrder.setStatus(OrderStatus.PENDING);
        testOrder.setCustomerId(1L);
        testOrder.setDeliveryAddress("北京市朝阳区测试地址123号");
        testOrder.setSubtotal(new BigDecimal("900.00"));
        testOrder.setShippingFee(new BigDecimal("50.00"));
        testOrder.setDiscount(new BigDecimal("0.00"));
        testOrder.setTotalAmount(new BigDecimal("950.00"));
        testOrder.setCurrency("CNY");
        testOrder.setPaymentStatus(PaymentStatus.PENDING);
        testOrder.setPaymentMethod("支付宝");
        testOrder.setExpectedDeliveryDate(LocalDate.now().plusDays(7));
        testOrder.setSalesPerson("张三");
        testOrder.setNotes("测试订单");
        testOrder.setCreatedAt(LocalDateTime.now());
        testOrder.setIsDeleted(false);

        // 创建测试创建DTO
        testCreateDTO = new OrderCreateDTO();
        testCreateDTO.setType("sales");
        testCreateDTO.setCustomerId(1L);
        testCreateDTO.setDeliveryAddress("北京市朝阳区测试地址123号");
        testCreateDTO.setSubtotal(new BigDecimal("900.00"));
        testCreateDTO.setShippingFee(new BigDecimal("50.00"));
        testCreateDTO.setDiscount(new BigDecimal("0.00"));
        testCreateDTO.setTotalAmount(new BigDecimal("950.00"));
        testCreateDTO.setCurrency("CNY");
        testCreateDTO.setPaymentMethod("支付宝");
        testCreateDTO.setExpectedDeliveryDate(LocalDate.now().plusDays(7));
        testCreateDTO.setSalesPerson("张三");
        testCreateDTO.setNotes("测试订单");

        // 创建测试商品明细
        OrderCreateDTO.OrderItemCreateDTO itemDTO = new OrderCreateDTO.OrderItemCreateDTO();
        itemDTO.setProductId(1L);
        itemDTO.setProductName("测试产品");
        itemDTO.setQuantity(1);
        itemDTO.setUnitPrice(new BigDecimal("900.00"));
        itemDTO.setTotalPrice(new BigDecimal("900.00"));
        testCreateDTO.setOrderItems(Arrays.asList(itemDTO));

        // 创建测试查询DTO
        testQueryDTO = new OrderQueryDTO();
        testQueryDTO.setPage(1);
        testQueryDTO.setSize(10);
        testQueryDTO.setKeyword("ORD");
        testQueryDTO.setType("sales");
        testQueryDTO.setStatus("pending");
    }

    @Test
    void testGetOrders() {
        // 准备测试数据
        List<Order> orders = Arrays.asList(testOrder);
        when(orderMapper.selectOrdersWithPagination(anyString(), anyString(), anyString(), 
                any(), any(), anyInt(), anyInt())).thenReturn(orders);
        when(orderMapper.countOrders(anyString(), anyString(), anyString(), 
                any(), any())).thenReturn(1);
        when(orderItemMapper.selectByOrderId(anyLong())).thenReturn(Arrays.asList());
        when(shippingInfoMapper.selectByOrderId(anyLong())).thenReturn(null);
        when(orderLogMapper.selectByOrderId(anyLong())).thenReturn(Arrays.asList());

        // 执行测试
        OrderService.OrderQueryResult result = orderService.getOrders(testQueryDTO);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getList().size());
        
        OrderDTO orderDTO = result.getList().get(0);
        assertEquals("1", orderDTO.getId());
        assertEquals("ORD202501010001", orderDTO.getOrderNumber());
        assertEquals("sales", orderDTO.getType());
        assertEquals("pending", orderDTO.getStatus());

        // 验证方法调用
        verify(orderMapper).selectOrdersWithPagination(anyString(), anyString(), anyString(), 
                any(), any(), anyInt(), anyInt());
        verify(orderMapper).countOrders(anyString(), anyString(), anyString(), any(), any());
    }

    @Test
    void testGetOrderById() {
        // 准备测试数据
        when(orderMapper.selectById(1L)).thenReturn(testOrder);
        when(orderItemMapper.selectByOrderId(1L)).thenReturn(Arrays.asList());
        when(shippingInfoMapper.selectByOrderId(1L)).thenReturn(null);
        when(orderLogMapper.selectByOrderId(1L)).thenReturn(Arrays.asList());

        // 执行测试
        OrderDTO result = orderService.getOrderById(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("ORD202501010001", result.getOrderNumber());
        assertEquals("sales", result.getType());
        assertEquals("pending", result.getStatus());

        // 验证方法调用
        verify(orderMapper).selectById(1L);
        verify(orderItemMapper).selectByOrderId(1L);
        verify(shippingInfoMapper).selectByOrderId(1L);
        verify(orderLogMapper).selectByOrderId(1L);
    }

    @Test
    void testGetOrderByIdNotFound() {
        // 准备测试数据
        when(orderMapper.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.getOrderById(999L);
        });

        assertEquals("订单不存在", exception.getMessage());
        verify(orderMapper).selectById(999L);
    }

    @Test
    void testCreateOrder() {
        // 准备测试数据
        when(orderMapper.insert(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return 1;
        });
        when(orderItemMapper.insert(any(OrderItem.class))).thenReturn(1);
        when(orderLogMapper.insert(any(OrderLog.class))).thenReturn(1);
        when(orderItemMapper.selectByOrderId(anyLong())).thenReturn(Arrays.asList());
        when(shippingInfoMapper.selectByOrderId(anyLong())).thenReturn(null);
        when(orderLogMapper.selectByOrderId(anyLong())).thenReturn(Arrays.asList());

        // 执行测试
        OrderDTO result = orderService.createOrder(testCreateDTO);

        // 验证结果
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertNotNull(result.getOrderNumber());
        assertTrue(result.getOrderNumber().startsWith("ORD"));
        assertEquals("sales", result.getType());
        assertEquals("pending", result.getStatus());

        // 验证方法调用
        verify(orderMapper).insert(any(Order.class));
        verify(orderItemMapper).insert(any(OrderItem.class));
        verify(orderLogMapper).insert(any(OrderLog.class));
    }

    @Test
    void testUpdateOrder() {
        // 准备测试数据
        when(orderMapper.selectById(1L)).thenReturn(testOrder);
        when(orderMapper.updateById(any(Order.class))).thenReturn(1);
        when(orderItemMapper.deleteByOrderId(1L)).thenReturn(1);
        when(orderItemMapper.insert(any(OrderItem.class))).thenReturn(1);
        when(orderLogMapper.insert(any(OrderLog.class))).thenReturn(1);
        when(orderItemMapper.selectByOrderId(anyLong())).thenReturn(Arrays.asList());
        when(shippingInfoMapper.selectByOrderId(anyLong())).thenReturn(null);
        when(orderLogMapper.selectByOrderId(anyLong())).thenReturn(Arrays.asList());

        // 执行测试
        OrderDTO result = orderService.updateOrder(1L, testOrderDTO);

        // 验证结果
        assertNotNull(result);
        assertEquals("1", result.getId());

        // 验证方法调用
        verify(orderMapper).selectById(1L);
        verify(orderMapper).updateById(any(Order.class));
        verify(orderItemMapper).deleteByOrderId(1L);
        verify(orderItemMapper).insert(any(OrderItem.class));
        verify(orderLogMapper).insert(any(OrderLog.class));
    }

    @Test
    void testUpdateOrderNotFound() {
        // 准备测试数据
        when(orderMapper.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.updateOrder(999L, testOrderDTO);
        });

        assertEquals("订单不存在", exception.getMessage());
        verify(orderMapper).selectById(999L);
    }

    @Test
    void testDeleteOrder() {
        // 准备测试数据
        when(orderMapper.selectById(1L)).thenReturn(testOrder);
        when(orderMapper.updateById(any(Order.class))).thenReturn(1);
        when(orderLogMapper.insert(any(OrderLog.class))).thenReturn(1);

        // 执行测试
        assertDoesNotThrow(() -> orderService.deleteOrder(1L));

        // 验证方法调用
        verify(orderMapper).selectById(1L);
        verify(orderMapper).updateById(any(Order.class));
        verify(orderLogMapper).insert(any(OrderLog.class));
    }

    @Test
    void testDeleteOrderNotFound() {
        // 准备测试数据
        when(orderMapper.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.deleteOrder(999L);
        });

        assertEquals("订单不存在", exception.getMessage());
        verify(orderMapper).selectById(999L);
    }

    @Test
    void testConvertToOrderDTO() {
        // 准备测试数据
        when(orderItemMapper.selectByOrderId(1L)).thenReturn(Arrays.asList());
        when(shippingInfoMapper.selectByOrderId(1L)).thenReturn(null);
        when(orderLogMapper.selectByOrderId(1L)).thenReturn(Arrays.asList());

        // 通过getOrderById方法间接测试convertToOrderDTO
        when(orderMapper.selectById(1L)).thenReturn(testOrder);
        OrderDTO result = orderService.getOrderById(1L);

        // 验证转换结果
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("ORD202501010001", result.getOrderNumber());
        assertEquals("sales", result.getType());
        assertEquals("pending", result.getStatus());
        assertEquals("1", result.getCustomerId());
        assertEquals("北京市朝阳区测试地址123号", result.getDeliveryAddress());
        assertEquals(new BigDecimal("900.00"), result.getSubtotal());
        assertEquals(new BigDecimal("50.00"), result.getShippingFee());
        assertEquals(new BigDecimal("0.00"), result.getDiscount());
        assertEquals(new BigDecimal("950.00"), result.getTotalAmount());
        assertEquals("CNY", result.getCurrency());
        assertEquals("pending", result.getPaymentStatus());
        assertEquals("支付宝", result.getPaymentMethod());
        assertEquals("张三", result.getSalesPerson());
        assertEquals("测试订单", result.getNotes());
    }

    @Test
    void testGenerateOrderNumber() {
        // 通过创建订单间接测试订单号生成
        when(orderMapper.insert(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return 1;
        });
        when(orderItemMapper.insert(any(OrderItem.class))).thenReturn(1);
        when(orderLogMapper.insert(any(OrderLog.class))).thenReturn(1);
        when(orderItemMapper.selectByOrderId(anyLong())).thenReturn(Arrays.asList());
        when(shippingInfoMapper.selectByOrderId(anyLong())).thenReturn(null);
        when(orderLogMapper.selectByOrderId(anyLong())).thenReturn(Arrays.asList());

        OrderDTO result = orderService.createOrder(testCreateDTO);

        // 验证订单号格式
        assertNotNull(result.getOrderNumber());
        assertTrue(result.getOrderNumber().startsWith("ORD"));
        assertEquals(17, result.getOrderNumber().length()); // ORD + 8位日期 + 6位随机数
    }
}