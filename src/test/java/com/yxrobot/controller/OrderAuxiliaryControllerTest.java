package com.yxrobot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yxrobot.dto.OrderDTO;
import com.yxrobot.dto.OrderQueryDTO;
import com.yxrobot.dto.ShippingInfoDTO;
import com.yxrobot.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 订单辅助功能控制器测试类
 * 测试订单管理的辅助功能API接口
 * 
 * 测试覆盖：
 * 1. 订单数据导出接口测试
 * 2. 物流信息查询接口测试
 * 3. 物流信息更新接口测试
 * 4. 客户列表查询接口测试
 * 5. 产品列表查询接口测试
 * 6. 异常处理测试
 */
@ExtendWith(MockitoExtension.class)
class OrderAuxiliaryControllerTest {
    
    @Mock
    private OrderService orderService;
    
    @InjectMocks
    private OrderController orderController;
    
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private OrderDTO mockOrder;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
        objectMapper = new ObjectMapper();
        
        // 创建模拟订单数据
        mockOrder = new OrderDTO();
        mockOrder.setId(1L);
        mockOrder.setOrderNumber("ORD202501010001");
        mockOrder.setType("sales");
        mockOrder.setStatus("pending");
        mockOrder.setCustomerName("测试客户");
        mockOrder.setCustomerPhone("13800138000");
        mockOrder.setDeliveryAddress("北京市朝阳区测试地址123号");
        mockOrder.setTotalAmount(new BigDecimal("1000.00"));
        mockOrder.setCurrency("CNY");
        mockOrder.setCreatedAt(LocalDateTime.now());
        
        // 创建模拟物流信息
        ShippingInfoDTO shippingInfo = new ShippingInfoDTO();
        shippingInfo.setCompany("顺丰快递");
        shippingInfo.setTrackingNumber("SF1234567890");
        shippingInfo.setShippedAt(LocalDateTime.now());
        shippingInfo.setNotes("正常发货");
        mockOrder.setShippingInfo(shippingInfo);
    }
    
    /**
     * 测试订单数据导出接口 - 成功情况
     */
    @Test
    void testExportOrders_Success() throws Exception {
        // 准备测试数据
        List<OrderDTO> orderList = Arrays.asList(mockOrder);
        OrderService.OrderQueryResult mockResult = new OrderService.OrderQueryResult(orderList, 1);  // 修复构造函数问题
        
        when(orderService.getOrders(any(OrderQueryDTO.class))).thenReturn(mockResult);
        
        // 执行请求并验证结果
        mockMvc.perform(get("/api/admin/orders/export")
                .param("keyword", "测试")
                .param("type", "sales")
                .param("status", "pending")
                .param("startDate", "2025-01-01")
                .param("endDate", "2025-01-31")
                .param("format", "excel")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("导出数据准备完成"))
                .andExpect(jsonPath("$.data.orders").isArray())
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.format").value("excel"))
                .andExpect(jsonPath("$.data.filters.keyword").value("测试"))
                .andExpect(jsonPath("$.data.filters.type").value("sales"));
        
        // 验证服务方法被调用
        verify(orderService, times(1)).getOrders(any(OrderQueryDTO.class));
    }
    
    /**
     * 测试订单数据导出接口 - 无筛选条件
     */
    @Test
    void testExportOrders_NoFilters() throws Exception {
        // 准备测试数据
        List<OrderDTO> orderList = Arrays.asList(mockOrder);
        OrderService.OrderQueryResult mockResult = new OrderService.OrderQueryResult(orderList, 1);  // 修复构造函数问题
        
        when(orderService.getOrders(any(OrderQueryDTO.class))).thenReturn(mockResult);
        
        // 执行请求并验证结果
        mockMvc.perform(get("/api/admin/orders/export")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("导出数据准备完成"))
                .andExpect(jsonPath("$.data.format").value("excel"))
                .andExpect(jsonPath("$.data.filters.keyword").value(""))
                .andExpect(jsonPath("$.data.filters.type").value(""));
        
        // 验证服务方法被调用
        verify(orderService, times(1)).getOrders(any(OrderQueryDTO.class));
    }
    
    /**
     * 测试获取订单物流信息接口 - 成功情况
     */
    @Test
    void testGetOrderTracking_Success() throws Exception {
        // 准备测试数据
        when(orderService.getOrderById(1L)).thenReturn(mockOrder);
        
        // 执行请求并验证结果
        mockMvc.perform(get("/api/admin/orders/1/tracking")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.orderId").value("1"))
                .andExpect(jsonPath("$.data.orderNumber").value("ORD202501010001"))
                .andExpect(jsonPath("$.data.customerName").value("测试客户"))
                .andExpect(jsonPath("$.data.company").value("顺丰快递"))
                .andExpect(jsonPath("$.data.trackingNumber").value("SF1234567890"))
                .andExpect(jsonPath("$.data.status").value("pending"));
        
        // 验证服务方法被调用
        verify(orderService, times(1)).getOrderById(1L);
    }
    
    /**
     * 测试获取订单物流信息接口 - 无物流信息
     */
    @Test
    void testGetOrderTracking_NoShippingInfo() throws Exception {
        // 准备测试数据 - 无物流信息的订单
        OrderDTO orderWithoutShipping = new OrderDTO();
        orderWithoutShipping.setId(2L);  // 修复类型不匹配问题
        orderWithoutShipping.setOrderNumber("ORD202501010002");
        orderWithoutShipping.setStatus("pending");
        orderWithoutShipping.setCustomerName("测试客户2");
        orderWithoutShipping.setDeliveryAddress("北京市朝阳区测试地址456号");
        orderWithoutShipping.setShippingInfo(null);
        
        when(orderService.getOrderById(2L)).thenReturn(orderWithoutShipping);
        
        // 执行请求并验证结果
        mockMvc.perform(get("/api/admin/orders/2/tracking")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.orderId").value(2))  // 修复类型不匹配问题
                .andExpect(jsonPath("$.data.orderNumber").value("ORD202501010002"))
                .andExpect(jsonPath("$.data.company").isEmpty())
                .andExpect(jsonPath("$.data.trackingNumber").isEmpty())
                .andExpect(jsonPath("$.data.status").value("pending"));
        
        // 验证服务方法被调用
        verify(orderService, times(1)).getOrderById(2L);
    }
    
    /**
     * 测试更新订单物流信息接口 - 成功情况
     */
    @Test
    void testUpdateOrderTracking_Success() throws Exception {
        // 准备测试数据
        when(orderService.getOrderById(1L)).thenReturn(mockOrder);
        
        Map<String, Object> trackingData = new HashMap<>();
        trackingData.put("company", "中通快递");
        trackingData.put("trackingNumber", "ZTO9876543210");
        trackingData.put("notes", "更新物流信息");
        
        // 执行请求并验证结果
        mockMvc.perform(put("/api/admin/orders/1/tracking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(trackingData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("物流信息更新成功"))
                .andExpect(jsonPath("$.data.orderId").value(1))
                .andExpect(jsonPath("$.data.orderNumber").value("ORD202501010001"))
                .andExpect(jsonPath("$.data.company").value("中通快递"))
                .andExpect(jsonPath("$.data.trackingNumber").value("ZTO9876543210"))
                .andExpect(jsonPath("$.data.notes").value("更新物流信息"));
        
        // 验证服务方法被调用
        verify(orderService, times(1)).getOrderById(1L);
    }
    
    /**
     * 测试获取客户列表接口 - 成功情况
     */
    @Test
    void testGetCustomersForOrder_Success() throws Exception {
        // 执行请求并验证结果
        mockMvc.perform(get("/api/admin/orders/customers")
                .param("keyword", "测试")
                .param("type", "enterprise")
                .param("active", "true")
                .param("limit", "20")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.list").isArray())
                .andExpect(jsonPath("$.data.total").isNumber());
    }
    
    /**
     * 测试获取产品列表接口 - 成功情况
     */
    @Test
    void testGetProductsForOrder_Success() throws Exception {
        // 执行请求并验证结果
        mockMvc.perform(get("/api/admin/orders/products")
                .param("keyword", "机器人")
                .param("category", "robot")
                .param("active", "true")
                .param("limit", "30")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.list").isArray())
                .andExpect(jsonPath("$.data.total").isNumber());
    }
    
    /**
     * 测试订单数据导出接口 - 异常情况
     */
    @Test
    void testExportOrders_Exception() throws Exception {
        // 准备测试数据 - 模拟服务异常
        when(orderService.getOrders(any(OrderQueryDTO.class)))
                .thenThrow(new RuntimeException("数据库连接失败"));
        
        // 执行请求并验证结果
        mockMvc.perform(get("/api/admin/orders/export")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("导出失败：数据库连接失败"));
        
        // 验证服务方法被调用
        verify(orderService, times(1)).getOrders(any(OrderQueryDTO.class));
    }
    
    /**
     * 测试获取订单物流信息接口 - 订单不存在
     */
    @Test
    void testGetOrderTracking_OrderNotFound() throws Exception {
        // 准备测试数据 - 模拟订单不存在
        when(orderService.getOrderById(999L))
                .thenThrow(new RuntimeException("订单不存在"));
        
        // 执行请求并验证结果
        mockMvc.perform(get("/api/admin/orders/999/tracking")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("订单不存在"));
        
        // 验证服务方法被调用
        verify(orderService, times(1)).getOrderById(999L);
    }
    
    /**
     * 测试更新订单物流信息接口 - 订单不存在
     */
    @Test
    void testUpdateOrderTracking_OrderNotFound() throws Exception {
        // 准备测试数据 - 模拟订单不存在
        when(orderService.getOrderById(999L))
                .thenThrow(new RuntimeException("订单不存在"));
        
        Map<String, Object> trackingData = new HashMap<>();
        trackingData.put("company", "申通快递");
        trackingData.put("trackingNumber", "STO1234567890");
        
        // 执行请求并验证结果
        mockMvc.perform(put("/api/admin/orders/999/tracking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(trackingData)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("订单不存在"));
        
        // 验证服务方法被调用
        verify(orderService, times(1)).getOrderById(999L);
    }
    
    /**
     * 测试日期参数格式验证
     */
    @Test
    void testExportOrders_InvalidDateFormat() throws Exception {
        // 执行请求并验证结果 - 无效日期格式
        mockMvc.perform(get("/api/admin/orders/export")
                .param("startDate", "invalid-date")
                .param("endDate", "2025-01-31")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));

        // 验证服务方法未被调用
        verify(orderService, never()).getOrders(any(OrderQueryDTO.class));       
    }
}