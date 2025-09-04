package com.yxrobot.service;

import com.yxrobot.entity.*;
import com.yxrobot.mapper.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 订单状态管理服务测试类
 */
@ExtendWith(MockitoExtension.class)
class OrderStatusServiceTest {

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderLogMapper orderLogMapper;

    @InjectMocks
    private OrderStatusService orderStatusService;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        // 创建测试订单
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setOrderNumber("ORD202501010001");
        testOrder.setStatus(OrderStatus.PENDING);
        testOrder.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testUpdateOrderStatusSuccess() {
        // 准备测试数据
        when(orderMapper.selectById(1L)).thenReturn(testOrder);
        when(orderMapper.updateStatus(1L, "confirmed")).thenReturn(1);
        when(orderLogMapper.insert(any(OrderLog.class))).thenReturn(1);

        // 执行测试
        boolean result = orderStatusService.updateOrderStatus(1L, "confirmed", "admin", "确认订单");

        // 验证结果
        assertTrue(result);

        // 验证方法调用
        verify(orderMapper).selectById(1L);
        verify(orderMapper).updateStatus(1L, "confirmed");
        verify(orderLogMapper).insert(any(OrderLog.class));
    }

    @Test
    void testUpdateOrderStatusOrderNotFound() {
        // 准备测试数据
        when(orderMapper.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderStatusService.updateOrderStatus(999L, "confirmed", "admin", "确认订单");
        });

        assertEquals("订单不存在", exception.getMessage());
        verify(orderMapper).selectById(999L);
        verify(orderMapper, never()).updateStatus(anyLong(), anyString());
        verify(orderLogMapper, never()).insert(any(OrderLog.class));
    }

    @Test
    void testUpdateOrderStatusInvalidTransition() {
        // 准备测试数据 - 尝试从PENDING直接跳转到COMPLETED（不合法）
        when(orderMapper.selectById(1L)).thenReturn(testOrder);

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderStatusService.updateOrderStatus(1L, "completed", "admin", "完成订单");
        });

        assertTrue(exception.getMessage().contains("不允许的状态流转"));
        verify(orderMapper).selectById(1L);
        verify(orderMapper, never()).updateStatus(anyLong(), anyString());
        verify(orderLogMapper, never()).insert(any(OrderLog.class));
    }

    @Test
    void testUpdateOrderStatusDatabaseUpdateFailed() {
        // 准备测试数据
        when(orderMapper.selectById(1L)).thenReturn(testOrder);
        when(orderMapper.updateStatus(1L, "confirmed")).thenReturn(0); // 模拟数据库更新失败

        // 执行测试
        boolean result = orderStatusService.updateOrderStatus(1L, "confirmed", "admin", "确认订单");

        // 验证结果
        assertFalse(result);

        // 验证方法调用
        verify(orderMapper).selectById(1L);
        verify(orderMapper).updateStatus(1L, "confirmed");
        verify(orderLogMapper, never()).insert(any(OrderLog.class)); // 更新失败时不应记录日志
    }

    @Test
    void testBatchUpdateOrderStatusSuccess() {
        // 准备测试数据
        Order order1 = new Order();
        order1.setId(1L);
        order1.setStatus(OrderStatus.PENDING);

        Order order2 = new Order();
        order2.setId(2L);
        order2.setStatus(OrderStatus.PENDING);

        when(orderMapper.selectById(1L)).thenReturn(order1);
        when(orderMapper.selectById(2L)).thenReturn(order2);
        when(orderMapper.updateStatus(1L, "confirmed")).thenReturn(1);
        when(orderMapper.updateStatus(2L, "confirmed")).thenReturn(1);
        when(orderLogMapper.insert(any(OrderLog.class))).thenReturn(1);

        List<Long> orderIds = Arrays.asList(1L, 2L);

        // 执行测试
        OrderStatusService.BatchUpdateResult result = orderStatusService.batchUpdateOrderStatus(
            orderIds, "confirmed", "admin", "批量确认订单");

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.getTotalCount());
        assertEquals(2, result.getSuccessCount());
        assertEquals(0, result.getFailedCount());
        assertEquals(Arrays.asList(1L, 2L), result.getSuccessIds());
        assertTrue(result.getFailedIds().isEmpty());

        // 验证方法调用
        verify(orderMapper, times(2)).selectById(anyLong());
        verify(orderMapper, times(2)).updateStatus(anyLong(), eq("confirmed"));
        verify(orderLogMapper, times(2)).insert(any(OrderLog.class));
    }

    @Test
    void testBatchUpdateOrderStatusPartialFailure() {
        // 准备测试数据
        Order order1 = new Order();
        order1.setId(1L);
        order1.setStatus(OrderStatus.PENDING);

        // order2不存在，order3状态流转不合法
        Order order3 = new Order();
        order3.setId(3L);
        order3.setStatus(OrderStatus.COMPLETED); // 已完成状态不能再变更

        when(orderMapper.selectById(1L)).thenReturn(order1);
        when(orderMapper.selectById(2L)).thenReturn(null); // 订单不存在
        when(orderMapper.selectById(3L)).thenReturn(order3);
        when(orderMapper.updateStatus(1L, "confirmed")).thenReturn(1);
        when(orderLogMapper.insert(any(OrderLog.class))).thenReturn(1);

        List<Long> orderIds = Arrays.asList(1L, 2L, 3L);

        // 执行测试
        OrderStatusService.BatchUpdateResult result = orderStatusService.batchUpdateOrderStatus(
            orderIds, "confirmed", "admin", "批量确认订单");

        // 验证结果
        assertNotNull(result);
        assertEquals(3, result.getTotalCount());
        assertEquals(1, result.getSuccessCount());
        assertEquals(2, result.getFailedCount());
        assertEquals(Arrays.asList(1L), result.getSuccessIds());
        assertEquals(Arrays.asList(2L, 3L), result.getFailedIds());
        assertEquals(2, result.getFailedReasons().size());

        // 验证方法调用
        verify(orderMapper, times(3)).selectById(anyLong());
        verify(orderMapper, times(1)).updateStatus(1L, "confirmed");
        verify(orderLogMapper, times(1)).insert(any(OrderLog.class));
    }

    @Test
    void testGetAvailableStatusTransitions() {
        // 准备测试数据 - PENDING状态的订单
        when(orderMapper.selectById(1L)).thenReturn(testOrder);

        // 执行测试
        List<OrderStatus> availableTransitions = orderStatusService.getAvailableStatusTransitions(1L);

        // 验证结果
        assertNotNull(availableTransitions);
        assertEquals(2, availableTransitions.size());
        assertTrue(availableTransitions.contains(OrderStatus.CONFIRMED));
        assertTrue(availableTransitions.contains(OrderStatus.CANCELLED));

        verify(orderMapper).selectById(1L);
    }

    @Test
    void testGetAvailableStatusTransitionsOrderNotFound() {
        // 准备测试数据
        when(orderMapper.selectById(999L)).thenReturn(null);

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderStatusService.getAvailableStatusTransitions(999L);
        });

        assertEquals("订单不存在", exception.getMessage());
        verify(orderMapper).selectById(999L);
    }

    @Test
    void testHasStatusChangePermission() {
        // 准备测试数据
        when(orderMapper.selectById(1L)).thenReturn(testOrder);

        // 执行测试 - 合法的状态流转
        boolean hasPermission = orderStatusService.hasStatusChangePermission(1L, "admin", "confirmed");

        // 验证结果
        assertTrue(hasPermission);

        // 执行测试 - 不合法的状态流转
        boolean hasPermissionInvalid = orderStatusService.hasStatusChangePermission(1L, "admin", "completed");

        // 验证结果
        assertFalse(hasPermissionInvalid);

        verify(orderMapper, times(2)).selectById(1L);
    }

    @Test
    void testHasStatusChangePermissionOrderNotFound() {
        // 准备测试数据
        when(orderMapper.selectById(999L)).thenReturn(null);

        // 执行测试
        boolean hasPermission = orderStatusService.hasStatusChangePermission(999L, "admin", "confirmed");

        // 验证结果
        assertFalse(hasPermission);
        verify(orderMapper).selectById(999L);
    }

    @Test
    void testValidStatusTransitions() {
        // 测试各种状态流转的合法性
        assertTrue(OrderStatus.PENDING.canTransitionTo(OrderStatus.CONFIRMED));
        assertTrue(OrderStatus.PENDING.canTransitionTo(OrderStatus.CANCELLED));
        assertFalse(OrderStatus.PENDING.canTransitionTo(OrderStatus.COMPLETED));

        assertTrue(OrderStatus.CONFIRMED.canTransitionTo(OrderStatus.PROCESSING));
        assertTrue(OrderStatus.CONFIRMED.canTransitionTo(OrderStatus.CANCELLED));
        assertFalse(OrderStatus.CONFIRMED.canTransitionTo(OrderStatus.DELIVERED));

        assertTrue(OrderStatus.PROCESSING.canTransitionTo(OrderStatus.SHIPPED));
        assertFalse(OrderStatus.PROCESSING.canTransitionTo(OrderStatus.PENDING));

        assertTrue(OrderStatus.SHIPPED.canTransitionTo(OrderStatus.DELIVERED));
        assertFalse(OrderStatus.SHIPPED.canTransitionTo(OrderStatus.PROCESSING));

        assertTrue(OrderStatus.DELIVERED.canTransitionTo(OrderStatus.COMPLETED));
        assertFalse(OrderStatus.DELIVERED.canTransitionTo(OrderStatus.SHIPPED));

        // 终态不能再流转
        assertFalse(OrderStatus.COMPLETED.canTransitionTo(OrderStatus.PENDING));
        assertFalse(OrderStatus.CANCELLED.canTransitionTo(OrderStatus.CONFIRMED));
    }
}