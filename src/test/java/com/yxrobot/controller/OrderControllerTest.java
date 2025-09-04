package com.yxrobot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yxrobot.dto.*;
import com.yxrobot.service.OrderService;
import com.yxrobot.service.OrderStatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 订单管理控制器测试类
 */
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private OrderStatsService orderStatsService;

    @Autowired
    private ObjectMapper objectMapper;

    private OrderDTO testOrderDTO;
    private OrderCreateDTO testCreateDTO;
    private OrderStatsDTO testStatsDTO;

    @BeforeEach
    void setUp() {
        // 创建测试订单DTO
        testOrderDTO = new OrderDTO();
        testOrderDTO.setId(1L);
        testOrderDTO.setOrderNumber("ORD202501010001");
        testOrderDTO.setType("sales");
        testOrderDTO.setStatus("pending");
        testOrderDTO.setCustomerId(1L);
        testOrderDTO.setCustomerName("测试客户");
        testOrderDTO.setCustomerPhone("13800138000");
        testOrderDTO.setDeliveryAddress("北京市朝阳区测试地址123号");
        testOrderDTO.setTotalAmount(new BigDecimal("1000.00"));
        testOrderDTO.setCurrency("CNY");
        testOrderDTO.setCreatedAt(LocalDateTime.now());

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

        // 创建测试统计DTO
        testStatsDTO = new OrderStatsDTO();
        testStatsDTO.setTotal(100);
        testStatsDTO.setPending(10);
        testStatsDTO.setProcessing(20);
        testStatsDTO.setCompleted(60);
        testStatsDTO.setCancelled(10);
        testStatsDTO.setTotalRevenue(new BigDecimal("100000.00"));
        testStatsDTO.setAverageOrderValue(new BigDecimal("1000.00"));
        testStatsDTO.setSalesOrders(80);
        testStatsDTO.setRentalOrders(20);
    }

    @Test
    void testGetOrders() throws Exception {
        // 准备测试数据
        List<OrderDTO> orderList = Arrays.asList(testOrderDTO);
        OrderService.OrderQueryResult queryResult = new OrderService.OrderQueryResult(orderList, 1);
        
        when(orderService.getOrders(any(OrderQueryDTO.class))).thenReturn(queryResult);
        when(orderStatsService.getOrderStats()).thenReturn(testStatsDTO);

        // 执行测试
        mockMvc.perform(get("/api/admin/orders")
                .param("page", "1")
                .param("size", "10")
                .param("keyword", "ORD")
                .param("type", "sales")
                .param("status", "pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.list").isArray())
                .andExpect(jsonPath("$.data.list[0].id").value(1))
                .andExpect(jsonPath("$.data.list[0].orderNumber").value("ORD202501010001"))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.stats.total").value(100));

        // 验证方法调用
        verify(orderService).getOrders(any(OrderQueryDTO.class));
        verify(orderStatsService).getOrderStats();
    }

    @Test
    void testGetOrderById() throws Exception {
        // 准备测试数据
        when(orderService.getOrderById(1L)).thenReturn(testOrderDTO);

        // 执行测试
        mockMvc.perform(get("/api/admin/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.orderNumber").value("ORD202501010001"))
                .andExpect(jsonPath("$.data.type").value("sales"))
                .andExpect(jsonPath("$.data.status").value("pending"));

        // 验证方法调用
        verify(orderService).getOrderById(1L);
    }

    @Test
    void testGetOrderByIdNotFound() throws Exception {
        // 准备测试数据
        when(orderService.getOrderById(999L)).thenThrow(new RuntimeException("订单不存在"));

        // 执行测试
        mockMvc.perform(get("/api/admin/orders/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("订单不存在"))
                .andExpect(jsonPath("$.success").value(false));

        // 验证方法调用
        verify(orderService).getOrderById(999L);
    }

    @Test
    void testCreateOrder() throws Exception {
        // 准备测试数据
        when(orderService.createOrder(any(OrderCreateDTO.class))).thenReturn(testOrderDTO);

        // 执行测试
        mockMvc.perform(post("/api/admin/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCreateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("创建成功"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.orderNumber").value("ORD202501010001"));

        // 验证方法调用
        verify(orderService).createOrder(any(OrderCreateDTO.class));
    }

    @Test
    void testCreateOrderValidationError() throws Exception {
        // 准备测试数据 - 缺少必填字段
        OrderCreateDTO invalidDTO = new OrderCreateDTO();
        // 不设置必填字段

        // 执行测试
        mockMvc.perform(post("/api/admin/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        // 验证方法未被调用
        verify(orderService, never()).createOrder(any(OrderCreateDTO.class));
    }

    @Test
    void testUpdateOrder() throws Exception {
        // 准备测试数据
        when(orderService.updateOrder(eq(1L), any(OrderCreateDTO.class))).thenReturn(testOrderDTO);

        // 执行测试
        mockMvc.perform(put("/api/admin/orders/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCreateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("更新成功"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));

        // 验证方法调用
        verify(orderService).updateOrder(eq(1L), any(OrderCreateDTO.class));
    }

    @Test
    void testUpdateOrderNotFound() throws Exception {
        // 准备测试数据
        when(orderService.updateOrder(eq(999L), any(OrderCreateDTO.class)))
                .thenThrow(new RuntimeException("订单不存在"));

        // 执行测试
        mockMvc.perform(put("/api/admin/orders/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCreateDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("订单不存在"))
                .andExpect(jsonPath("$.success").value(false));

        // 验证方法调用
        verify(orderService).updateOrder(eq(999L), any(OrderCreateDTO.class));
    }

    @Test
    void testDeleteOrder() throws Exception {
        // 准备测试数据
        doNothing().when(orderService).deleteOrder(1L);

        // 执行测试
        mockMvc.perform(delete("/api/admin/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("删除成功"))
                .andExpect(jsonPath("$.success").value(true));

        // 验证方法调用
        verify(orderService).deleteOrder(1L);
    }

    @Test
    void testDeleteOrderNotFound() throws Exception {
        // 准备测试数据
        doThrow(new RuntimeException("订单不存在")).when(orderService).deleteOrder(999L);

        // 执行测试
        mockMvc.perform(delete("/api/admin/orders/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("订单不存在"))
                .andExpect(jsonPath("$.success").value(false));

        // 验证方法调用
        verify(orderService).deleteOrder(999L);
    }

    @Test
    void testGetOrderStats() throws Exception {
        // 准备测试数据
        when(orderStatsService.getOrderStats()).thenReturn(testStatsDTO);

        // 执行测试
        mockMvc.perform(get("/api/admin/orders/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.total").value(100))
                .andExpect(jsonPath("$.data.pending").value(10))
                .andExpect(jsonPath("$.data.processing").value(20))
                .andExpect(jsonPath("$.data.completed").value(60))
                .andExpect(jsonPath("$.data.cancelled").value(10))
                .andExpect(jsonPath("$.data.totalRevenue").value(100000.00))
                .andExpect(jsonPath("$.data.salesOrders").value(80))
                .andExpect(jsonPath("$.data.rentalOrders").value(20));

        // 验证方法调用
        verify(orderStatsService).getOrderStats();
    }

    @Test
    void testGetOrderStatsByDateRange() throws Exception {
        // 准备测试数据
        when(orderStatsService.getOrderStatsByDateRange(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(testStatsDTO);

        // 执行测试
        mockMvc.perform(get("/api/admin/orders/stats")
                .param("startDate", "2025-01-01")
                .param("endDate", "2025-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.total").value(100));

        // 验证方法调用
        verify(orderStatsService).getOrderStatsByDateRange(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void testSearchOrders() throws Exception {
        // 准备测试数据
        List<OrderDTO> orderList = Arrays.asList(testOrderDTO);
        OrderService.OrderQueryResult queryResult = new OrderService.OrderQueryResult(orderList, 1);
        
        when(orderService.getOrders(any(OrderQueryDTO.class))).thenReturn(queryResult);

        // 执行测试
        mockMvc.perform(get("/api/admin/orders/search")
                .param("keyword", "ORD")
                .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("搜索成功"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.list").isArray())
                .andExpect(jsonPath("$.data.list[0].orderNumber").value("ORD202501010001"))
                .andExpect(jsonPath("$.data.total").value(1));

        // 验证方法调用
        verify(orderService).getOrders(any(OrderQueryDTO.class));
    }

    @Test
    void testValidateOrderNumber() throws Exception {
        // 执行测试
        mockMvc.perform(get("/api/admin/orders/validate/orderNumber")
                .param("orderNumber", "ORD202501010001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("验证成功"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.exists").value(false))
                .andExpect(jsonPath("$.data.available").value(true));
    }

    @Test
    void testGetOrderTransitions() throws Exception {
        // 执行测试
        mockMvc.perform(get("/api/admin/orders/1/transitions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.transitions").isArray());
    }

    @Test
    void testGetOrdersWithDateRange() throws Exception {
        // 准备测试数据
        List<OrderDTO> orderList = Arrays.asList(testOrderDTO);
        OrderService.OrderQueryResult queryResult = new OrderService.OrderQueryResult(orderList, 1);
        
        when(orderService.getOrders(any(OrderQueryDTO.class))).thenReturn(queryResult);
        when(orderStatsService.getOrderStats()).thenReturn(testStatsDTO);

        // 执行测试
        mockMvc.perform(get("/api/admin/orders")
                .param("startDate", "2025-01-01")
                .param("endDate", "2025-01-31")
                .param("sortBy", "createdAt")
                .param("sortOrder", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list").isArray())
                .andExpect(jsonPath("$.data.total").value(1));

        // 验证方法调用
        verify(orderService).getOrders(any(OrderQueryDTO.class));
        verify(orderStatsService).getOrderStats();
    }
}