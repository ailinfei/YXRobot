package com.yxrobot.validator;

import com.yxrobot.dto.OrderCreateDTO;
import com.yxrobot.dto.OrderDTO;
import com.yxrobot.exception.OrderException;
import com.yxrobot.util.OrderValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单表单数据验证器
 * 专门处理前端表单提交的数据验证
 * 确保必填字段完整性和数据格式正确性
 */
@Component
public class OrderFormValidator {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderFormValidator.class);
    
    /**
     * 验证订单创建表单数据
     * 
     * @param createDTO 创建订单DTO
     * @throws OrderException 验证失败时抛出异常
     */
    public void validateCreateForm(OrderCreateDTO createDTO) {
        logger.debug("开始验证订单创建表单数据");
        
        List<String> errors = new ArrayList<>();
        
        try {
            // 验证必填字段
            validateRequiredFields(createDTO, errors);
            
            // 验证数据格式
            validateDataFormats(createDTO, errors);
            
            // 验证业务逻辑
            validateBusinessLogic(createDTO, errors);
            
            // 如果有错误，抛出异常
            if (!errors.isEmpty()) {
                String errorMessage = "表单验证失败：" + String.join("; ", errors);
                throw OrderException.validationFailed("form", errorMessage);
            }
            
            logger.debug("订单创建表单数据验证通过");
            
        } catch (OrderException e) {
            throw e;
        } catch (Exception e) {
            logger.error("表单验证过程中发生异常", e);
            throw OrderException.validationFailed("form", "表单验证过程中发生异常：" + e.getMessage());
        }
    }
    
    /**
     * 验证订单更新表单数据
     * 
     * @param updateDTO 更新订单DTO
     * @throws OrderException 验证失败时抛出异常
     */
    public void validateUpdateForm(OrderDTO updateDTO) {
        logger.debug("开始验证订单更新表单数据");
        
        List<String> errors = new ArrayList<>();
        
        try {
            // 验证更新字段
            validateUpdateFields(updateDTO, errors);
            
            // 如果有错误，抛出异常
            if (!errors.isEmpty()) {
                String errorMessage = "表单验证失败：" + String.join("; ", errors);
                throw OrderException.validationFailed("form", errorMessage);
            }
            
            logger.debug("订单更新表单数据验证通过");
            
        } catch (OrderException e) {
            throw e;
        } catch (Exception e) {
            logger.error("表单验证过程中发生异常", e);
            throw OrderException.validationFailed("form", "表单验证过程中发生异常：" + e.getMessage());
        }
    }
    
    /**
     * 验证必填字段
     */
    private void validateRequiredFields(OrderCreateDTO createDTO, List<String> errors) {
        // 订单号
        if (createDTO.getOrderNumber() == null || createDTO.getOrderNumber().trim().isEmpty()) {
            errors.add("订单号不能为空");
        }
        
        // 订单类型
        if (createDTO.getType() == null || createDTO.getType().trim().isEmpty()) {
            errors.add("订单类型不能为空");
        }
        
        // 客户ID
        if (createDTO.getCustomerId() == null) {
            errors.add("客户ID不能为空");
        }
        
        // 配送地址
        if (createDTO.getDeliveryAddress() == null || createDTO.getDeliveryAddress().trim().isEmpty()) {
            errors.add("配送地址不能为空");
        }
        
        // 订单总金额
        if (createDTO.getTotalAmount() == null) {
            errors.add("订单总金额不能为空");
        }
        
        // 订单商品
        if (createDTO.getOrderItems() == null || createDTO.getOrderItems().isEmpty()) {
            errors.add("订单必须包含至少一个商品");
        } else {
            // 验证每个商品的必填字段
            for (int i = 0; i < createDTO.getOrderItems().size(); i++) {
                OrderCreateDTO.OrderItemCreateDTO item = createDTO.getOrderItems().get(i);
                String itemPrefix = String.format("商品[%d]", i + 1);
                
                if (item.getProductId() == null) {
                    errors.add(itemPrefix + "产品ID不能为空");
                }
                
                if (item.getProductName() == null || item.getProductName().trim().isEmpty()) {
                    errors.add(itemPrefix + "产品名称不能为空");
                }
                
                if (item.getQuantity() == null) {
                    errors.add(itemPrefix + "数量不能为空");
                }
                
                if (item.getUnitPrice() == null) {
                    errors.add(itemPrefix + "单价不能为空");
                }
                
                if (item.getTotalPrice() == null) {
                    errors.add(itemPrefix + "小计金额不能为空");
                }
            }
        }
    }
    
    /**
     * 验证数据格式
     */
    private void validateDataFormats(OrderCreateDTO createDTO, List<String> errors) {
        // 订单号格式
        if (createDTO.getOrderNumber() != null && !createDTO.getOrderNumber().trim().isEmpty()) {
            try {
                OrderValidationUtil.validateOrderNumber(createDTO.getOrderNumber());
            } catch (OrderException e) {
                errors.add("订单号格式不正确");
            }
        }
        
        // 订单类型
        if (createDTO.getType() != null && !createDTO.getType().trim().isEmpty()) {
            try {
                OrderValidationUtil.validateOrderType(createDTO.getType());
            } catch (OrderException e) {
                errors.add("订单类型无效");
            }
        }
        
        // 客户ID
        if (createDTO.getCustomerId() != null) {
            try {
                OrderValidationUtil.validateId(createDTO.getCustomerId(), "客户ID");
            } catch (OrderException e) {
                errors.add("客户ID无效");
            }
        }
        
        // 配送地址长度
        if (createDTO.getDeliveryAddress() != null && !createDTO.getDeliveryAddress().trim().isEmpty()) {
            try {
                OrderValidationUtil.validateLength(createDTO.getDeliveryAddress(), "配送地址", 10, 500);
            } catch (OrderException e) {
                errors.add("配送地址长度不符合要求（10-500个字符）");
            }
        }
        
        // 联系电话格式
        if (createDTO.getCustomerPhone() != null && !createDTO.getCustomerPhone().trim().isEmpty()) {
            try {
                OrderValidationUtil.validatePhone(createDTO.getCustomerPhone());
            } catch (OrderException e) {
                errors.add("联系电话格式不正确");
            }
        }
        
        // 邮箱格式
        if (createDTO.getCustomerEmail() != null && !createDTO.getCustomerEmail().trim().isEmpty()) {
            try {
                OrderValidationUtil.validateEmail(createDTO.getCustomerEmail());
            } catch (OrderException e) {
                errors.add("邮箱格式不正确");
            }
        }
        
        // 订单总金额
        if (createDTO.getTotalAmount() != null) {
            try {
                OrderValidationUtil.validateAmount(createDTO.getTotalAmount(), "订单总金额");
            } catch (OrderException e) {
                errors.add("订单总金额必须大于0");
            }
        }
        
        // 运费
        if (createDTO.getShippingFee() != null && createDTO.getShippingFee().compareTo(BigDecimal.ZERO) < 0) {
            errors.add("运费不能为负数");
        }
        
        // 折扣
        if (createDTO.getDiscount() != null && createDTO.getDiscount().compareTo(BigDecimal.ZERO) < 0) {
            errors.add("折扣不能为负数");
        }
        
        // 预期交付日期
        if (createDTO.getExpectedDeliveryDate() != null) {
            LocalDate today = LocalDate.now();
            if (createDTO.getExpectedDeliveryDate().isBefore(today)) {
                errors.add("预期交付日期不能早于今天");
            }
        }
        
        // 验证订单商品格式
        if (createDTO.getOrderItems() != null) {
            for (int i = 0; i < createDTO.getOrderItems().size(); i++) {
                OrderCreateDTO.OrderItemCreateDTO item = createDTO.getOrderItems().get(i);
                String itemPrefix = String.format("商品[%d]", i + 1);
                
                // 产品ID
                if (item.getProductId() != null) {
                    try {
                        OrderValidationUtil.validateId(item.getProductId(), "产品ID");
                    } catch (OrderException e) {
                        errors.add(itemPrefix + "产品ID无效");
                    }
                }
                
                // 产品名称长度
                if (item.getProductName() != null && !item.getProductName().trim().isEmpty()) {
                    try {
                        OrderValidationUtil.validateLength(item.getProductName(), "产品名称", 1, 200);
                    } catch (OrderException e) {
                        errors.add(itemPrefix + "产品名称长度不符合要求（1-200个字符）");
                    }
                }
                
                // 数量
                if (item.getQuantity() != null) {
                    try {
                        OrderValidationUtil.validateQuantity(item.getQuantity(), "数量");
                    } catch (OrderException e) {
                        errors.add(itemPrefix + "数量必须大于0");
                    }
                }
                
                // 单价
                if (item.getUnitPrice() != null) {
                    try {
                        OrderValidationUtil.validateAmount(item.getUnitPrice(), "单价");
                    } catch (OrderException e) {
                        errors.add(itemPrefix + "单价必须大于0");
                    }
                }
                
                // 小计金额
                if (item.getTotalPrice() != null) {
                    try {
                        OrderValidationUtil.validateAmount(item.getTotalPrice(), "小计金额");
                    } catch (OrderException e) {
                        errors.add(itemPrefix + "小计金额必须大于0");
                    }
                }
            }
        }
    }
    
    /**
     * 验证业务逻辑
     */
    private void validateBusinessLogic(OrderCreateDTO createDTO, List<String> errors) {
        // 验证订单商品小计计算
        if (createDTO.getOrderItems() != null) {
            for (int i = 0; i < createDTO.getOrderItems().size(); i++) {
                OrderCreateDTO.OrderItemCreateDTO item = createDTO.getOrderItems().get(i);
                String itemPrefix = String.format("商品[%d]", i + 1);
                
                // 验证小计金额计算是否正确
                if (item.getQuantity() != null && item.getUnitPrice() != null && item.getTotalPrice() != null) {
                    BigDecimal expectedTotal = new BigDecimal(item.getQuantity()).multiply(item.getUnitPrice());
                    if (item.getTotalPrice().compareTo(expectedTotal) != 0) {
                        errors.add(itemPrefix + "小计金额计算不正确");
                    }
                }
            }
        }
        
        // 验证租赁相关字段（如果是租赁订单）
        if ("rental".equals(createDTO.getType())) {
            if (createDTO.getRentalStartDate() == null) {
                errors.add("租赁订单必须指定租赁开始日期");
            }
            
            if (createDTO.getRentalEndDate() == null) {
                errors.add("租赁订单必须指定租赁结束日期");
            }
            
            if (createDTO.getRentalStartDate() != null && createDTO.getRentalEndDate() != null) {
                if (createDTO.getRentalStartDate().isAfter(createDTO.getRentalEndDate())) {
                    errors.add("租赁开始日期不能晚于结束日期");
                }
            }
        }
    }
    
    /**
     * 验证更新字段
     */
    private void validateUpdateFields(OrderDTO updateDTO, List<String> errors) {
        // 订单号格式
        if (updateDTO.getOrderNumber() != null && !updateDTO.getOrderNumber().trim().isEmpty()) {
            try {
                OrderValidationUtil.validateOrderNumber(updateDTO.getOrderNumber());
            } catch (OrderException e) {
                errors.add("订单号格式不正确");
            }
        }
        
        // 订单类型
        if (updateDTO.getType() != null && !updateDTO.getType().trim().isEmpty()) {
            try {
                OrderValidationUtil.validateOrderType(updateDTO.getType());
            } catch (OrderException e) {
                errors.add("订单类型无效");
            }
        }
        
        // 客户ID
        if (updateDTO.getCustomerId() != null) {
            try {
                OrderValidationUtil.validateId(updateDTO.getCustomerId(), "客户ID");
            } catch (OrderException e) {
                errors.add("客户ID无效");
            }
        }
        
        // 配送地址长度
        if (updateDTO.getDeliveryAddress() != null && !updateDTO.getDeliveryAddress().trim().isEmpty()) {
            try {
                OrderValidationUtil.validateLength(updateDTO.getDeliveryAddress(), "配送地址", 10, 500);
            } catch (OrderException e) {
                errors.add("配送地址长度不符合要求（10-500个字符）");
            }
        }
        
        // 联系电话格式
        if (updateDTO.getCustomerPhone() != null && !updateDTO.getCustomerPhone().trim().isEmpty()) {
            try {
                OrderValidationUtil.validatePhone(updateDTO.getCustomerPhone());
            } catch (OrderException e) {
                errors.add("联系电话格式不正确");
            }
        }
        
        // 邮箱格式
        if (updateDTO.getCustomerEmail() != null && !updateDTO.getCustomerEmail().trim().isEmpty()) {
            try {
                OrderValidationUtil.validateEmail(updateDTO.getCustomerEmail());
            } catch (OrderException e) {
                errors.add("邮箱格式不正确");
            }
        }
        
        // 订单总金额
        if (updateDTO.getTotalAmount() != null) {
            try {
                OrderValidationUtil.validateAmount(updateDTO.getTotalAmount(), "订单总金额");
            } catch (OrderException e) {
                errors.add("订单总金额必须大于0");
            }
        }
        
        // 运费
        if (updateDTO.getShippingFee() != null && updateDTO.getShippingFee().compareTo(BigDecimal.ZERO) < 0) {
            errors.add("运费不能为负数");
        }
        
        // 折扣
        if (updateDTO.getDiscount() != null && updateDTO.getDiscount().compareTo(BigDecimal.ZERO) < 0) {
            errors.add("折扣不能为负数");
        }
    }
}