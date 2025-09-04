package com.yxrobot.service;

import com.yxrobot.dto.OrderCreateDTO;
import com.yxrobot.dto.OrderDTO;
import com.yxrobot.exception.OrderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * 订单验证服务测试类
 * 测试订单数据验证的各种场景
 */
@ExtendWith(MockitoExtension.class)
class OrderValidationServiceTest {
    
    @Mock
    private CustomerService customerService;
    
    @Mock
    private ProductService productService;
    
    @InjectMocks
    private OrderValidationService orderValidationService;
    
    private OrderCreateDTO validOrderCreateDTO;
    private OrderCreateDTO.OrderItemCreateDTO validOrderItem;
    
    @BeforeEach
    void setUp() {
        // 创建有效的订单商品
        validOrderItem = new OrderCreateDTO.OrderItemCreateDTO();
        validOrderItem.setProductId(1L);
        validOrderItem.setProductName("测试产品");
        validOrderItem.setQuantity(2);
        validOrderItem.setUnitPrice(new BigDecimal("500.00"));
        validOrderItem.setTotalPrice(new BigDecimal("1000.00"));
        
        // 创建有效的订单创建DTO
        validOrderCreateDTO = new OrderCreateDTO();
        validOrderCreateDTO.setOrderNumber("ORD1234567890");
        validOrderCreateDTO.setType("sales");
        validOrderCreateDTO.setCustomerId(1L);
        validOrderCreateDTO.setDeliveryAddress("北京市朝阳区测试地址123号");
        validOrderCreateDTO.setSubtotal(new BigDecimal("1000.00"));
        validOrderCreateDTO.setShippingFee(new BigDecimal("50.00"));
        validOrderCreateDTO.setDiscount(new BigDecimal("0.00"));
        validOrderCreateDTO.setTotalAmount(new BigDecimal("1050.00"));
        validOrderCreateDTO.setCurrency("CNY");
        validOrderCreateDTO.setOrderItems(Arrays.asList(validOrderItem));
        
        // 模拟客户和产品存在
        when(customerService.existsById(anyLong())).thenReturn(true);
        when(productService.existsById(anyLong())).thenReturn(true);
    }
    
    /**
     * 测试有效订单创建数据验证 - 成功情况
     */
    @Test
    void testValidateOrderCreate_Success() {
        OrderValidationService.ValidationResult result = 
            orderValidationService.validateOrderCreate(validOrderCreateDTO);
        
        assertTrue(result.isValid());
        assertFalse(result.hasErrors());
        assertEquals(0, result.getErrors().size());
    }
    
    /**
     * 测试订单号为空的验证
     */
    @Test
    void testValidateOrderCreate_EmptyOrderNumber() {
        validOrderCreateDTO.setOrderNumber("");
        
        OrderValidationService.ValidationResult result = 
            orderValidationService.validateOrderCreate(validOrderCreateDTO);
        
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertEquals("ORDER_NUMBER_REQUIRED", result.getErrors().get(0).getCode());
    }
    
    /**
     * 测试订单号格式无效的验证
     */
    @Test
    void testValidateOrderCreate_InvalidOrderNumberFormat() {
        validOrderCreateDTO.setOrderNumber("INVALID123");
        
        OrderValidationService.ValidationResult result = 
            orderValidationService.validateOrderCreate(validOrderCreateDTO);
        
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertEquals("INVALID_ORDER_NUMBER_FORMAT", result.getErrors().get(0).getCode());
    }
    
    /**
     * 测试订单类型为空的验证
     */
    @Test
    void testValidateOrderCreate_EmptyOrderType() {
        validOrderCreateDTO.setType(null);
        
        OrderValidationService.ValidationResult result = 
            orderValidationService.validateOrderCreate(validOrderCreateDTO);
        
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertEquals("ORDER_TYPE_REQUIRED", result.getErrors().get(0).getCode());
    }
    
    /**
     * 测试订单类型无效的验证
     */
    @Test
    void testValidateOrderCreate_InvalidOrderType() {
        validOrderCreateDTO.setType("invalid");
        
        OrderValidationService.ValidationResult result = 
            orderValidationService.validateOrderCreate(validOrderCreateDTO);
        
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertEquals("INVALID_ORDER_TYPE", result.getErrors().get(0).getCode());
    }
    
    /**
     * 测试配送地址为空的验证
     */
    @Test
    void testValidateOrderCreate_EmptyDeliveryAddress() {
        validOrderCreateDTO.setDeliveryAddress("");
        
        OrderValidationService.ValidationResult result = 
            orderValidationService.validateOrderCreate(validOrderCreateDTO);
        
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertEquals("DELIVERY_ADDRESS_REQUIRED", result.getErrors().get(0).getCode());
    }
    
    /**
     * 测试配送地址过短的验证
     */
    @Test
    void testValidateOrderCreate_DeliveryAddressTooShort() {
        validOrderCreateDTO.setDeliveryAddress("短地址");
        
        OrderValidationService.ValidationResult result = 
            orderValidationService.validateOrderCreate(validOrderCreateDTO);
        
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertEquals("DELIVERY_ADDRESS_TOO_SHORT", result.getErrors().get(0).getCode());
    }
    
    /**
     * 测试客户ID为空的验证
     */
    @Test
    void testValidateOrderCreate_EmptyCustomerId() {
        validOrderCreateDTO.setCustomerId(null);
        
        OrderValidationService.ValidationResult result = 
            orderValidationService.validateOrderCreate(validOrderCreateDTO);
        
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertEquals("CUSTOMER_ID_REQUIRED", result.getErrors().get(0).getCode());
    }
    
    /**
     * 测试客户不存在的验证
     */
    @Test
    void testValidateOrderCreate_CustomerNotFound() {
        when(customerService.existsById(anyLong())).thenReturn(false);
        
        OrderValidationService.ValidationResult result = 
            orderValidationService.validateOrderCreate(validOrderCreateDTO);
        
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertEquals("CUSTOMER_NOT_FOUND", result.getErrors().get(0).getCode());
    }
    
    /**
     * 测试订单商品为空的验证
     */
    @Test
    void testValidateOrderCreate_EmptyOrderItems() {
        validOrderCreateDTO.setOrderItems(null);
        
        OrderValidationService.ValidationResult result = 
            orderValidationService.validateOrderCreate(validOrderCreateDTO);
        
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertEquals("ORDER_ITEMS_REQUIRED", result.getErrors().get(0).getCode());
    }
    
    /**
     * 测试订单商品产品ID为空的验证
     */
    @Test
    void testValidateOrderCreate_EmptyProductId() {
        validOrderItem.setProductId(null);
        
        OrderValidationService.ValidationResult result = 
            orderValidationService.validateOrderCreate(validOrderCreateDTO);
        
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertEquals("PRODUCT_ID_REQUIRED", result.getErrors().get(0).getCode());
    }
    
    /**
     * 测试订单商品数量无效的验证
     */
    @Test
    void testValidateOrderCreate_InvalidQuantity() {
        validOrderItem.setQuantity(0);
        
        OrderValidationService.ValidationResult result = 
            orderValidationService.validateOrderCreate(validOrderCreateDTO);
        
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertEquals("INVALID_QUANTITY", result.getErrors().get(0).getCode());
    }
    
    /**
     * 测试订单商品单价无效的验证
     */
    @Test
    void testValidateOrderCreate_InvalidUnitPrice() {
        validOrderItem.setUnitPrice(BigDecimal.ZERO);
        
        OrderValidationService.ValidationResult result = 
            orderValidationService.validateOrderCreate(validOrderCreateDTO);
        
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertEquals("INVALID_UNIT_PRICE", result.getErrors().get(0).getCode());
    }
    
    /**
     * 测试订单商品小计计算错误的验证
     */
    @Test
    void testValidateOrderCreate_IncorrectTotalPrice() {
        validOrderItem.setTotalPrice(new BigDecimal("999.00")); // 应该是 500 * 2 = 1000
        
        OrderValidationService.ValidationResult result = 
            orderValidationService.validateOrderCreate(validOrderCreateDTO);
        
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertEquals("INCORRECT_TOTAL_PRICE", result.getErrors().get(0).getCode());
    }
    
    /**
     * 测试订单总金额为空的验证
     */
    @Test
    void testValidateOrderCreate_EmptyTotalAmount() {
        validOrderCreateDTO.setTotalAmount(null);
        
        OrderValidationService.ValidationResult result = 
            orderValidationService.validateOrderCreate(validOrderCreateDTO);
        
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertEquals("TOTAL_AMOUNT_REQUIRED", result.getErrors().get(0).getCode());
    }
    
    /**
     * 测试订单总金额计算错误的验证
     */
    @Test
    void testValidateOrderCreate_IncorrectTotalAmount() {
        validOrderCreateDTO.setTotalAmount(new BigDecimal("2000.00")); // 应该是 1000 + 50 - 0 = 1050
        
        OrderValidationService.ValidationResult result = 
            orderValidationService.validateOrderCreate(validOrderCreateDTO);
        
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertEquals("INCORRECT_TOTAL_AMOUNT", result.getErrors().get(0).getCode());
    }
    
    /**
     * 测试订单金额超限的验证
     */
    @Test
    void testValidateOrderCreate_OrderAmountExceeded() {
        validOrderCreateDTO.setTotalAmount(new BigDecimal("1000000.00"));
        validOrderCreateDTO.setSubtotal(new BigDecimal("1000000.00"));
        
        OrderValidationService.ValidationResult result = 
            orderValidationService.validateOrderCreate(validOrderCreateDTO);
        
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertEquals("ORDER_AMOUNT_EXCEEDED", result.getErrors().get(0).getCode());
    }
    
    /**
     * 测试租赁订单验证 - 成功情况
     */
    @Test
    void testValidateOrderCreate_RentalOrder_Success() {
        validOrderCreateDTO.setType("rental");
        validOrderCreateDTO.setRentalStartDate(LocalDate.now().plusDays(1));
        validOrderCreateDTO.setRentalEndDate(LocalDate.now().plusDays(8));
        validOrderCreateDTO.setRentalDays(8);
        
        OrderValidationService.ValidationResult result = 
            orderValidationService.validateOrderCreate(validOrderCreateDTO);
        
        assertTrue(result.isValid());
        assertFalse(result.hasErrors());
    }
    
    /**
     * 测试租赁订单开始日期为空的验证
     */
    @Test
    void testValidateOrderCreate_RentalOrder_EmptyStartDate() {
        validOrderCreateDTO.setType("rental");
        validOrderCreateDTO.setRentalStartDate(null);
        validOrderCreateDTO.setRentalEndDate(LocalDate.now().plusDays(7));
        
        OrderValidationService.ValidationResult result = 
            orderValidationService.validateOrderCreate(validOrderCreateDTO);
        
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertEquals("RENTAL_START_DATE_REQUIRED", result.getErrors().get(0).getCode());
    }
    
    /**
     * 测试租赁订单日期范围无效的验证
     */
    @Test
    void testValidateOrderCreate_RentalOrder_InvalidDateRange() {
        validOrderCreateDTO.setType("rental");
        validOrderCreateDTO.setRentalStartDate(LocalDate.now().plusDays(7));
        validOrderCreateDTO.setRentalEndDate(LocalDate.now().plusDays(1)); // 结束日期早于开始日期
        
        OrderValidationService.ValidationResult result = 
            orderValidationService.validateOrderCreate(validOrderCreateDTO);
        
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertEquals("INVALID_RENTAL_PERIOD", result.getErrors().get(0).getCode());
    }
    
    /**
     * 测试状态变更验证 - 成功情况
     */
    @Test
    void testValidateStatusChange_Success() {
        OrderValidationService.ValidationResult result = 
            orderValidationService.validateStatusChange("pending", "confirmed");
        
        assertTrue(result.isValid());
        assertFalse(result.hasErrors());
    }
    
    /**
     * 测试状态变更验证 - 无效流转
     */
    @Test
    void testValidateStatusChange_InvalidTransition() {
        OrderValidationService.ValidationResult result = 
            orderValidationService.validateStatusChange("completed", "pending");
        
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertEquals("INVALID_STATUS_TRANSITION", result.getErrors().get(0).getCode());
    }
    
    /**
     * 测试状态变更验证 - 当前状态为空
     */
    @Test
    void testValidateStatusChange_EmptyCurrentStatus() {
        OrderValidationService.ValidationResult result = 
            orderValidationService.validateStatusChange(null, "confirmed");
        
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertEquals("INVALID_CURRENT_STATUS", result.getErrors().get(0).getCode());
    }
    
    /**
     * 测试订单更新验证 - 成功情况
     */
    @Test
    void testValidateOrderUpdate_Success() {
        OrderDTO updateDTO = new OrderDTO();
        updateDTO.setTotalAmount(new BigDecimal("1200.00"));
        updateDTO.setStatus("confirmed");
        
        OrderValidationService.ValidationResult result = 
            orderValidationService.validateOrderUpdate(1L, updateDTO);
        
        assertTrue(result.isValid());
        assertFalse(result.hasErrors());
    }
    
    /**
     * 测试订单更新验证 - 无效订单ID
     */
    @Test
    void testValidateOrderUpdate_InvalidOrderId() {
        OrderDTO updateDTO = new OrderDTO();
        
        OrderValidationService.ValidationResult result = 
            orderValidationService.validateOrderUpdate(null, updateDTO);
        
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertEquals("INVALID_ORDER_ID", result.getErrors().get(0).getCode());
    }
    
    /**
     * 测试多个验证错误的情况
     */
    @Test
    void testValidateOrderCreate_MultipleErrors() {
        validOrderCreateDTO.setOrderNumber("");
        validOrderCreateDTO.setType(null);
        validOrderCreateDTO.setCustomerId(null);
        validOrderCreateDTO.setOrderItems(null);
        
        OrderValidationService.ValidationResult result = 
            orderValidationService.validateOrderCreate(validOrderCreateDTO);
        
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertTrue(result.getErrors().size() >= 4); // 至少有4个错误
    }
    
    /**
     * 测试验证过程中的异常处理
     */
    @Test
    void testValidateOrderCreate_ExceptionHandling() {
        when(customerService.existsById(anyLong())).thenThrow(new RuntimeException("数据库连接失败"));
        
        OrderValidationService.ValidationResult result = 
            orderValidationService.validateOrderCreate(validOrderCreateDTO);
        
        // 验证应该继续进行，但会有警告
        assertTrue(result.hasWarnings());
    }
}