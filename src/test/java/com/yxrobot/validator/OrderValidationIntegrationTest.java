package com.yxrobot.validator;

import com.yxrobot.dto.OrderCreateDTO;
import com.yxrobot.dto.OrderDTO;
import com.yxrobot.exception.OrderException;
import com.yxrobot.service.OrderValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 订单验证集成测试
 * 测试各种验证场景和异常处理
 */
@SpringBootTest
@SpringJUnitConfig
public class OrderValidationIntegrationTest {
    
    private OrderFormValidator orderFormValidator;
    private DataIntegrityValidator dataIntegrityValidator;
    private OrderValidationService orderValidationService;
    
    @BeforeEach
    void setUp() {
        orderFormValidator = new OrderFormValidator();
        dataIntegrityValidator = new DataIntegrityValidator();
        orderValidationService = new OrderValidationService();
    }
    
    @Test
    void testValidOrderCreation() {
        // 创建有效的订单数据
        OrderCreateDTO createDTO = createValidOrderCreateDTO();
        
        // 验证应该通过
        assertDoesNotThrow(() -> {
            orderFormValidator.validateCreateForm(createDTO);
        });
    }
    
    @Test
    void testInvalidOrderNumber() {
        OrderCreateDTO createDTO = createValidOrderCreateDTO();
        createDTO.setOrderNumber("INVALID123"); // 无效格式
        
        // 验证应该失败
        OrderException exception = assertThrows(OrderException.class, () -> {
            orderFormValidator.validateCreateForm(createDTO);
        });
        
        assertTrue(exception.getMessage().contains("订单号格式不正确"));
    }
    
    @Test
    void testMissingRequiredFields() {
        OrderCreateDTO createDTO = new OrderCreateDTO();
        // 不设置必填字段
        
        // 验证应该失败
        OrderException exception = assertThrows(OrderException.class, () -> {
            orderFormValidator.validateCreateForm(createDTO);
        });
        
        assertTrue(exception.getMessage().contains("不能为空"));
    }
    
    @Test
    void testInvalidAmount() {
        OrderCreateDTO createDTO = createValidOrderCreateDTO();
        createDTO.setTotalAmount(BigDecimal.ZERO); // 无效金额
        
        // 验证应该失败
        OrderException exception = assertThrows(OrderException.class, () -> {
            orderFormValidator.validateCreateForm(createDTO);
        });
        
        assertTrue(exception.getMessage().contains("必须大于0"));
    }
    
    @Test
    void testInvalidEmail() {
        OrderCreateDTO createDTO = createValidOrderCreateDTO();
        createDTO.setContactEmail("invalid-email"); // 无效邮箱格式
        
        // 验证应该失败
        OrderException exception = assertThrows(OrderException.class, () -> {
            orderFormValidator.validateCreateForm(createDTO);
        });
        
        assertTrue(exception.getMessage().contains("邮箱格式不正确"));
    }
    
    @Test
    void testInvalidPhone() {
        OrderCreateDTO createDTO = createValidOrderCreateDTO();
        createDTO.setContactPhone("123456"); // 无效电话格式
        
        // 验证应该失败
        OrderException exception = assertThrows(OrderException.class, () -> {
            orderFormValidator.validateCreateForm(createDTO);
        });
        
        assertTrue(exception.getMessage().contains("联系电话格式不正确"));
    }
    
    @Test
    void testAmountCalculationError() {
        OrderCreateDTO createDTO = createValidOrderCreateDTO();
        createDTO.setTotalAmount(new BigDecimal("999.99")); // 与计算结果不符
        
        // 验证应该失败
        OrderException exception = assertThrows(OrderException.class, () -> {
            orderFormValidator.validateCreateForm(createDTO);
        });
        
        assertTrue(exception.getMessage().contains("金额计算错误"));
    }
    
    @Test
    void testRentalOrderValidation() {
        OrderCreateDTO createDTO = createValidOrderCreateDTO();
        createDTO.setType("rental");
        createDTO.setRentalStartDate(null); // 租赁订单缺少开始日期
        
        // 验证应该失败
        OrderException exception = assertThrows(OrderException.class, () -> {
            orderFormValidator.validateCreateForm(createDTO);
        });
        
        assertTrue(exception.getMessage().contains("租赁开始日期不能为空"));
    }
    
    @Test
    void testOrderUpdateValidation() {
        OrderDTO updateDTO = new OrderDTO();
        updateDTO.setStatus("invalid_status"); // 无效状态
        
        // 验证应该失败
        OrderException exception = assertThrows(OrderException.class, () -> {
            orderFormValidator.validateUpdateForm(updateDTO);
        });
        
        assertTrue(exception.getMessage().contains("订单状态无效"));
    }
    
    @Test
    void testBatchOperationValidation() {
        List<Long> orderIds = new ArrayList<>();
        // 添加超过100个订单ID
        for (int i = 1; i <= 101; i++) {
            orderIds.add((long) i);
        }
        
        // 验证应该失败
        OrderException exception = assertThrows(OrderException.class, () -> {
            dataIntegrityValidator.validateBatchOperationIntegrity(orderIds, "update_status");
        });
        
        assertTrue(exception.getMessage().contains("不能超过100个"));
    }
    
    @Test
    void testStatusTransitionValidation() {
        // 测试无效的状态流转
        OrderValidationService.ValidationResult result = 
            orderValidationService.validateStatusChange("completed", "pending");
        
        assertFalse(result.isValid());
        assertTrue(result.getErrors().get(0).getMessage().contains("不允许"));
    }
    
    @Test
    void testValidStatusTransition() {
        // 测试有效的状态流转
        OrderValidationService.ValidationResult result = 
            orderValidationService.validateStatusChange("pending", "confirmed");
        
        assertTrue(result.isValid());
    }
    
    /**
     * 创建有效的订单创建DTO
     */
    private OrderCreateDTO createValidOrderCreateDTO() {
        OrderCreateDTO createDTO = new OrderCreateDTO();
        createDTO.setOrderNumber("ORD1234567890");
        createDTO.setType("sales");
        createDTO.setCustomerId(1L);
        createDTO.setDeliveryAddress("北京市朝阳区示例地址123号");
        createDTO.setContactPhone("13800138000");
        createDTO.setContactEmail("test@example.com");
        createDTO.setSubtotal(new BigDecimal("1000.00"));
        createDTO.setShippingFee(new BigDecimal("50.00"));
        createDTO.setDiscount(new BigDecimal("0.00"));
        createDTO.setTotalAmount(new BigDecimal("1050.00"));
        createDTO.setCurrency("CNY");
        createDTO.setPaymentMethod("alipay");
        createDTO.setExpectedDeliveryDate(LocalDate.now().plusDays(7));
        createDTO.setSalesPerson("张三");
        createDTO.setNotes("测试订单");
        
        // 添加订单商品
        List<OrderCreateDTO.OrderItemCreateDTO> orderItems = new ArrayList<>();
        OrderCreateDTO.OrderItemCreateDTO item = new OrderCreateDTO.OrderItemCreateDTO();
        item.setProductId(1L);
        item.setProductName("练字机器人 Pro");
        item.setQuantity(1);
        item.setUnitPrice(new BigDecimal("1000.00"));
        item.setTotalPrice(new BigDecimal("1000.00"));
        orderItems.add(item);
        
        createDTO.setOrderItems(orderItems);
        
        return createDTO;
    }
}