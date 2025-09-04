package com.yxrobot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yxrobot.dto.*;
import com.yxrobot.entity.Order;
import com.yxrobot.exception.OrderException;
import com.yxrobot.service.OrderService;
import com.yxrobot.service.OrderStatusService;
import com.yxrobot.validator.OrderFormValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 订单控制器综合测试类
 * 任务16: 编写单元测试 - 测试订单API接口
 */
@WebMvcTest(OrderController.class)
@DisplayName("订单控制器综合测试")
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class OrderControllerComprehensiveTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private OrderStatusService orderStatusService;

    @MockBean
    private OrderFormValidator orderFormValidator;

    @Autowired
    private ObjectMapper objectMapper;

    private OrderDTO testOrderDTO;
    private OrderCreateDTO testCreateDTO;
    private OrderQueryDTO testQueryDTO;

    @BeforeEach
    void setUp() {
        testOrderDTO = createTestOrderDTO();
        testCreateDTO = createTestOrderCreateDTO();
        testQueryDTO = createTestOrderQueryDTO();
    }

    @Test
    @DisplayName("01. 获取订单列表API测试")
    void testGetOrdersApi() throws Exception {
        System.out.println("🔍 开始获取订单列表API测试...");
        
        // 准备测试数据
        List<OrderDTO> orderList = Arrays.asList(testOrderDTO);
        OrderService.OrderQueryResult pageResult = new OrderService.OrderQueryResult(orderList, 1);
        
        when(orderService.getOrders(any(OrderQueryDTO.class))).thenReturn(pageResult);
        
        // 执行测试
        mockMvc.perform(get("/api/admin/orders")
                .param("page", "1")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.list").isArray())
                .andExpect(jsonPath("$.data.list[0].orderNumber").value(testOrderDTO.getOrderNumber()))
                .andExpect(jsonPath("$.data.total").value(1));
        
        // 验证方法调用
        verify(orderService).getOrders(any(OrderQueryDTO.class));
        
        System.out.println("✅ 获取订单列表API测试通过");
    }

    @Test
    @DisplayName("02. 获取订单详情API测试")
    void testGetOrderByIdApi() throws Exception {
        System.out.println("🔍 开始获取订单详情API测试...");
        
        // 准备测试数据
        Long orderId = 1L;
        when(orderService.getOrderById(orderId)).thenReturn(testOrderDTO);
        
        // 执行测试
        mockMvc.perform(get("/api/admin/orders/{id}", orderId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.id").value(orderId))
                .andExpect(jsonPath("$.data.orderNumber").value(testOrderDTO.getOrderNumber()))
                .andExpect(jsonPath("$.data.customerName").value(testOrderDTO.getCustomerName()));
        
        // 验证方法调用
        verify(orderService).getOrderById(orderId);
        
        System.out.println("✅ 获取订单详情API测试通过");
    }

    @Test
    @DisplayName("03. 创建订单API测试")
    void testCreateOrderApi() throws Exception {
        System.out.println("🔍 开始创建订单API测试...");
        
        // 准备测试数据
        when(orderService.createOrder(any(OrderCreateDTO.class))).thenReturn(testOrderDTO);
        
        // 执行测试
        mockMvc.perform(post("/api/admin/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCreateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("创建成功"))
                .andExpect(jsonPath("$.data.orderNumber").value(testOrderDTO.getOrderNumber()))
                .andExpect(jsonPath("$.data.customerName").value(testOrderDTO.getCustomerName()));
        
        // 验证方法调用
        verify(orderService).createOrder(any(OrderCreateDTO.class));
        
        System.out.println("✅ 创建订单API测试通过");
    }

    @Test
    @DisplayName("04. 更新订单API测试")
    void testUpdateOrderApi() throws Exception {
        System.out.println("🔍 开始更新订单API测试...");
        
        // 准备测试数据
        Long orderId = 1L;
        when(orderService.updateOrder(eq(orderId), any(OrderDTO.class))).thenReturn(testOrderDTO);
        
        // 执行测试
        mockMvc.perform(put("/api/admin/orders/{id}", orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOrderDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data.id").value(orderId));
        
        // 验证方法调用
        verify(orderService).updateOrder(eq(orderId), any(OrderDTO.class));
        
        System.out.println("✅ 更新订单API测试通过");
    }

    @Test
    @DisplayName("05. 删除订单API测试")
    void testDeleteOrderApi() throws Exception {
        System.out.println("🔍 开始删除订单API测试...");
        
        // 准备测试数据
        Long orderId = 1L;
        doNothing().when(orderService).deleteOrder(orderId);
        
        // 执行测试
        mockMvc.perform(delete("/api/admin/orders/{id}", orderId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"));
        
        // 验证方法调用
        verify(orderService).deleteOrder(orderId);
        
        System.out.println("✅ 删除订单API测试通过");
    }

    @Test
    @DisplayName("06. 搜索订单API测试")
    void testSearchOrdersApi() throws Exception {
        System.out.println("🔍 开始搜索订单API测试...");
        
        // 准备测试数据
        String keyword = "ORD123";
        List<OrderDTO> searchResults = Arrays.asList(testOrderDTO);
        OrderService.OrderQueryResult pageResult = new OrderService.OrderQueryResult(searchResults, 1);
        
        when(orderService.getOrders(any(OrderQueryDTO.class))).thenReturn(pageResult);
        
        // 执行测试
        mockMvc.perform(get("/api/admin/orders")
                .param("keyword", keyword)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.list").isArray())
                .andExpect(jsonPath("$.data.list[0].orderNumber").value(testOrderDTO.getOrderNumber()))
                .andExpect(jsonPath("$.data.total").value(1));
        
        // 验证方法调用
        verify(orderService).getOrders(any(OrderQueryDTO.class));
        
        System.out.println("✅ 搜索订单API测试通过");
    }

    @Test
    @DisplayName("07. 筛选订单API测试")
    void testFilterOrdersApi() throws Exception {
        System.out.println("🔍 开始筛选订单API测试...");
        
        // 准备测试数据
        List<OrderDTO> filterResults = Arrays.asList(testOrderDTO);
        OrderService.OrderQueryResult pageResult = new OrderService.OrderQueryResult(filterResults, 1);
        
        when(orderService.getOrders(any(OrderQueryDTO.class))).thenReturn(pageResult);
        
        // 执行测试
        mockMvc.perform(get("/api/admin/orders")
                .param("type", "sales")
                .param("status", "pending")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.list").isArray())
                .andExpect(jsonPath("$.data.list[0].type").value("sales"))
                .andExpect(jsonPath("$.data.total").value(1));
        
        // 验证方法调用
        verify(orderService).getOrders(any(OrderQueryDTO.class));
        
        System.out.println("✅ 筛选订单API测试通过");
    }

    @Test
    @DisplayName("08. 分页查询订单API测试")
    void testPaginationOrdersApi() throws Exception {
        System.out.println("🔍 开始分页查询订单API测试...");
        
        // 准备测试数据
        List<OrderDTO> pageResults = Arrays.asList(testOrderDTO, createTestOrderDTO());
        OrderService.OrderQueryResult pageResult = new OrderService.OrderQueryResult(pageResults, 2);
        
        when(orderService.getOrders(any(OrderQueryDTO.class))).thenReturn(pageResult);
        
        // 执行测试
        mockMvc.perform(get("/api/admin/orders")
                .param("page", "1")
                .param("size", "2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.list").isArray())
                .andExpect(jsonPath("$.data.list.length()").value(2))
                .andExpect(jsonPath("$.data.total").value(2));
        
        // 验证方法调用
        verify(orderService).getOrders(any(OrderQueryDTO.class));
        
        System.out.println("✅ 分页查询订单API测试通过");
    }

    @Test
    @DisplayName("09. 订单状态更新API测试")
    void testUpdateOrderStatusApi() throws Exception {
        System.out.println("🔍 开始订单状态更新API测试...");
        
        // 准备测试数据
        Long orderId = 1L;
        String newStatus = "confirmed";
        doNothing().when(orderService).updateOrderStatus(orderId, newStatus);
        
        // 执行测试
        mockMvc.perform(put("/api/admin/orders/{id}/status", orderId)
                .param("status", newStatus)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"));
        
        // 验证方法调用
        verify(orderService).updateOrderStatus(orderId, newStatus);
        
        System.out.println("✅ 订单状态更新API测试通过");
    }

    @Test
    @DisplayName("10. 批量删除订单API测试")
    void testBatchDeleteOrdersApi() throws Exception {
        System.out.println("🔍 开始批量删除订单API测试...");
        
        // 准备测试数据
        List<Long> orderIds = Arrays.asList(1L, 2L, 3L);
        doNothing().when(orderService).batchDeleteOrders(orderIds);
        
        // 执行测试
        mockMvc.perform(delete("/api/admin/orders/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"));
        
        // 验证方法调用
        verify(orderService).batchDeleteOrders(orderIds);
        
        System.out.println("✅ 批量删除订单API测试通过");
    }

    // 辅助方法：创建测试订单DTO
    private OrderDTO createTestOrderDTO() {
        OrderDTO dto = new OrderDTO();
        dto.setId(1L);
        dto.setOrderNumber("ORD1234567890");
        dto.setCustomerName("测试客户");
        dto.setCustomerPhone("13800138000");
        dto.setCustomerEmail("test@example.com");
        dto.setType("sales");
        dto.setStatus("pending");
        dto.setTotalAmount(new BigDecimal("200.00"));
        dto.setCreatedAt(java.time.LocalDateTime.now());
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
        dto.setDeliveryAddress("测试地址");
        return dto;
    }

    // 辅助方法：创建测试查询DTO
    private OrderQueryDTO createTestOrderQueryDTO() {
        OrderQueryDTO dto = new OrderQueryDTO();
        dto.setPage(1);
        dto.setSize(10);
        return dto;
    }
}