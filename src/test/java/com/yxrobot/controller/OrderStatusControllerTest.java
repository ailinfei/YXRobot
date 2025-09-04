package com.yxrobot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yxrobot.dto.OrderDTO;
import com.yxrobot.service.OrderService;
import com.yxrobot.service.OrderStatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 订单状态管理控制器测试类
 */
@WebMvcTest(OrderStatusController.class)
class OrderStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderStatusService orderStatusService;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    private OrderDTO testOrderDTO;

    @BeforeEach
    void setUp() {
        // 创建测试订单DTO
        testOrderDTO = new OrderDTO();
        testOrderDTO.setId(1L);
        testOrderDTO.setOrderNumber("ORD202501010001");
        testOrderDTO.setType("sales");
        testOrderDTO.setStatus("confirmed");
        testOrderDTO.setCustomerId(1L);
        testOrderDTO.setCustomerName("测试客户");
        testOrderDTO.setCustomerPhone("13800138000");
        testOrderDTO.setDeliveryAddress("北京市朝阳区测试地址123号");
        testOrderDTO.setTotalAmount(new BigDecimal("1000.00"));
        testOrderDTO.setCurrency("CNY");
        testOrderDTO.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testUpdateOrderStatusSuccess() throws Exception {
        // 准备测试数据
        OrderStatusController.StatusUpdateRequest request = new OrderStatusController.StatusUpdateRequest();
        request.setStatus("processing");
        request.setOperator("admin");
        request.setNotes("开始处理订单");

        when(orderStatusService.hasStatusChangePermission(1L, "admin", "processing")).thenReturn(true);
        when(orderStatusService.updateOrderStatus(1L, "processing", "admin", "开始处理订单")).thenReturn(true);
        when(orderService.getOrderById(1L)).thenReturn(testOrderDTO);

        // 执行测试
        mockMvc.perform(patch("/api/admin/orders/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("状态更新成功"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.orderNumber").value("ORD202501010001"));

        // 验证方法调用
        verify(orderStatusService).hasStatusChangePermission(1L, "admin", "processing");
        verify(orderStatusService).updateOrderStatus(1L, "processing", "admin", "开始处理订单");
        verify(orderService).getOrderById(1L);
    }

    @Test
    void testUpdateOrderStatusNoPermission() throws Exception {
        // 准备测试数据
        OrderStatusController.StatusUpdateRequest request = new OrderStatusController.StatusUpdateRequest();
        request.setStatus("processing");
        request.setOperator("user");
        request.setNotes("开始处理订单");

        when(orderStatusService.hasStatusChangePermission(1L, "user", "processing")).thenReturn(false);

        // 执行测试
        mockMvc.perform(patch("/api/admin/orders/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("没有权限执行此操作"))
                .andExpect(jsonPath("$.success").value(false));

        // 验证方法调用
        verify(orderStatusService).hasStatusChangePermission(1L, "user", "processing");
        verify(orderStatusService, never()).updateOrderStatus(anyLong(), anyString(), anyString(), anyString());
        verify(orderService, never()).getOrderById(anyLong());
    }

    @Test
    void testUpdateOrderStatusInvalidTransition() throws Exception {
        // 准备测试数据
        OrderStatusController.StatusUpdateRequest request = new OrderStatusController.StatusUpdateRequest();
        request.setStatus("completed");
        request.setOperator("admin");
        request.setNotes("完成订单");

        when(orderStatusService.hasStatusChangePermission(1L, "admin", "completed")).thenReturn(true);
        when(orderStatusService.updateOrderStatus(1L, "completed", "admin", "完成订单"))
                .thenThrow(new RuntimeException("不允许的状态流转"));

        // 执行测试
        mockMvc.perform(patch("/api/admin/orders/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("不允许的状态流转"))
                .andExpect(jsonPath("$.success").value(false));

        // 验证方法调用
        verify(orderStatusService).hasStatusChangePermission(1L, "admin", "completed");
        verify(orderStatusService).updateOrderStatus(1L, "completed", "admin", "完成订单");
    }

    @Test
    void testBatchUpdateOrderStatusSuccess() throws Exception {
        // 准备测试数据
        OrderStatusController.BatchStatusUpdateRequest request = new OrderStatusController.BatchStatusUpdateRequest();
        request.setOrderIds(Arrays.asList(1L, 2L, 3L));
        request.setStatus("confirmed");
        request.setOperator("admin");
        request.setNotes("批量确认订单");

        OrderStatusService.BatchUpdateResult result = new OrderStatusService.BatchUpdateResult();
        result.setTotalCount(3);
        result.setSuccessCount(2);
        result.setFailedCount(1);
        result.setSuccessIds(Arrays.asList(1L, 2L));
        result.setFailedIds(Arrays.asList(3L));
        result.setFailedReasons(Arrays.asList("订单ID 3: 订单不存在"));

        when(orderStatusService.batchUpdateOrderStatus(
                Arrays.asList(1L, 2L, 3L), "confirmed", "admin", "批量确认订单"))
                .thenReturn(result);

        // 执行测试
        mockMvc.perform(patch("/api/admin/orders/batch/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("批量操作完成：成功 2 个，失败 1 个"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalCount").value(3))
                .andExpect(jsonPath("$.data.successCount").value(2))
                .andExpect(jsonPath("$.data.failedCount").value(1))
                .andExpect(jsonPath("$.data.successIds").isArray())
                .andExpect(jsonPath("$.data.failedIds").isArray())
                .andExpect(jsonPath("$.data.failedReasons").isArray());

        // 验证方法调用
        verify(orderStatusService).batchUpdateOrderStatus(
                Arrays.asList(1L, 2L, 3L), "confirmed", "admin", "批量确认订单");
    }

    @Test
    void testBatchDeleteOrdersSuccess() throws Exception {
        // 准备测试数据
        OrderStatusController.BatchDeleteRequest request = new OrderStatusController.BatchDeleteRequest();
        request.setOrderIds(Arrays.asList(1L, 2L));
        request.setOperator("admin");
        request.setReason("批量删除测试订单");

        doNothing().when(orderService).deleteOrder(1L);
        doNothing().when(orderService).deleteOrder(2L);

        // 执行测试
        mockMvc.perform(delete("/api/admin/orders/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("批量删除完成：成功 2 个，失败 0 个"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalCount").value(2))
                .andExpect(jsonPath("$.data.successCount").value(2))
                .andExpect(jsonPath("$.data.failedCount").value(0));

        // 验证方法调用
        verify(orderService).deleteOrder(1L);
        verify(orderService).deleteOrder(2L);
    }

    @Test
    void testBatchDeleteOrdersPartialFailure() throws Exception {
        // 准备测试数据
        OrderStatusController.BatchDeleteRequest request = new OrderStatusController.BatchDeleteRequest();
        request.setOrderIds(Arrays.asList(1L, 999L));
        request.setOperator("admin");
        request.setReason("批量删除测试订单");

        doNothing().when(orderService).deleteOrder(1L);
        doThrow(new RuntimeException("订单不存在")).when(orderService).deleteOrder(999L);

        // 执行测试
        mockMvc.perform(delete("/api/admin/orders/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("批量删除完成：成功 1 个，失败 1 个"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalCount").value(2))
                .andExpect(jsonPath("$.data.successCount").value(1))
                .andExpect(jsonPath("$.data.failedCount").value(1))
                .andExpect(jsonPath("$.data.failedReasons[0]").value("订单ID 999: 订单不存在"));

        // 验证方法调用
        verify(orderService).deleteOrder(1L);
        verify(orderService).deleteOrder(999L);
    }

    @Test
    void testGetOrderStatusHistory() throws Exception {
        // 准备测试数据
        when(orderService.getOrderById(1L)).thenReturn(testOrderDTO);

        // 执行测试
        mockMvc.perform(get("/api/admin/orders/1/status/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderId").value(1))
                .andExpect(jsonPath("$.data.orderNumber").value("ORD202501010001"))
                .andExpect(jsonPath("$.data.currentStatus").value("confirmed"));

        // 验证方法调用
        verify(orderService).getOrderById(1L);
    }

    @Test
    void testCheckStatusChangePermission() throws Exception {
        // 准备测试数据
        when(orderStatusService.hasStatusChangePermission(1L, "admin", "processing")).thenReturn(true);

        // 执行测试
        mockMvc.perform(get("/api/admin/orders/1/status/permission")
                .param("operator", "admin")
                .param("targetStatus", "processing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("验证成功"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderId").value(1))
                .andExpect(jsonPath("$.data.operator").value("admin"))
                .andExpect(jsonPath("$.data.targetStatus").value("processing"))
                .andExpect(jsonPath("$.data.hasPermission").value(true));

        // 验证方法调用
        verify(orderStatusService).hasStatusChangePermission(1L, "admin", "processing");
    }

    @Test
    void testUpdateOrderStatusValidationError() throws Exception {
        // 准备测试数据 - 缺少必填字段
        OrderStatusController.StatusUpdateRequest request = new OrderStatusController.StatusUpdateRequest();
        // 不设置必填字段

        // 执行测试
        mockMvc.perform(patch("/api/admin/orders/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        // 验证方法未被调用
        verify(orderStatusService, never()).hasStatusChangePermission(anyLong(), anyString(), anyString());
        verify(orderStatusService, never()).updateOrderStatus(anyLong(), anyString(), anyString(), anyString());
    }

    @Test
    void testBatchUpdateOrderStatusValidationError() throws Exception {
        // 准备测试数据 - 空的订单ID列表
        OrderStatusController.BatchStatusUpdateRequest request = new OrderStatusController.BatchStatusUpdateRequest();
        request.setOrderIds(Arrays.asList()); // 空列表
        request.setStatus("confirmed");
        request.setOperator("admin");

        // 执行测试
        mockMvc.perform(patch("/api/admin/orders/batch/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        // 验证方法未被调用
        verify(orderStatusService, never()).batchUpdateOrderStatus(anyList(), anyString(), anyString(), anyString());
    }
}