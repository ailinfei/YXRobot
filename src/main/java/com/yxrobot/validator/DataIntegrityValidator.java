package com.yxrobot.validator;

import com.yxrobot.dto.OrderCreateDTO;
import com.yxrobot.dto.OrderDTO;
import com.yxrobot.exception.OrderException;
import com.yxrobot.service.CustomerService;
import com.yxrobot.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据完整性验证器
 * 确保订单数据的完整性和一致性
 * 验证关联数据的存在性和有效性
 */
@Component
public class DataIntegrityValidator {
    
    private static final Logger logger = LoggerFactory.getLogger(DataIntegrityValidator.class);
    
    @Autowired
    private CustomerService customerService;
    
    @Autowired
    private ProductService productService;
    
    /**
     * 验证订单创建数据的完整性
     * 
     * @param createDTO 创建订单DTO
     * @throws OrderException 验证失败时抛出异常
     */
    public void validateCreateDataIntegrity(OrderCreateDTO createDTO) {
        logger.debug("开始验证订单创建数据完整性");
        
        List<String> errors = new ArrayList<>();
        
        try {
            // 验证客户数据完整性
            validateCustomerIntegrity(createDTO.getCustomerId(), errors);
            
            // 验证产品数据完整性
            validateProductsIntegrity(createDTO.getOrderItems(), errors);
            
            // 验证金额数据完整性
            validateAmountIntegrity(createDTO, errors);
            
            // 验证关联数据一致性
            validateDataConsistency(createDTO, errors);
            
            // 如果有错误，抛出异常
            if (!errors.isEmpty()) {
                String errorMessage = "数据完整性验证失败：" + String.join("; ", errors);
                throw OrderException.dataIntegrityViolation(errorMessage);
            }
            
            logger.debug("订单创建数据完整性验证通过");
            
        } catch (OrderException e) {
            throw e;
        } catch (Exception e) {
            logger.error("数据完整性验证过程中发生异常", e);
            throw OrderException.dataIntegrityViolation("数据完整性验证过程中发生异常：" + e.getMessage());
        }
    }
    
    /**
     * 验证订单更新数据的完整性
     * 
     * @param orderId 订单ID
     * @param updateDTO 更新订单DTO
     * @throws OrderException 验证失败时抛出异常
     */
    public void validateUpdateDataIntegrity(Long orderId, OrderDTO updateDTO) {
        logger.debug("开始验证订单更新数据完整性，订单ID: {}", orderId);
        
        List<String> errors = new ArrayList<>();
        
        try {
            // 验证订单存在性
            if (orderId == null || orderId <= 0) {
                errors.add("订单ID无效");
            }
            
            // 验证更新数据的完整性
            if (updateDTO.getTotalAmount() != null && updateDTO.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
                errors.add("订单总金额必须大于0");
            }
            
            // 验证状态数据完整性
            if (updateDTO.getStatus() != null) {
                validateStatusIntegrity(updateDTO.getStatus(), errors);
            }
            
            // 如果有错误，抛出异常
            if (!errors.isEmpty()) {
                String errorMessage = "数据完整性验证失败：" + String.join("; ", errors);
                throw OrderException.dataIntegrityViolation(errorMessage);
            }
            
            logger.debug("订单更新数据完整性验证通过");
            
        } catch (OrderException e) {
            throw e;
        } catch (Exception e) {
            logger.error("数据完整性验证过程中发生异常", e);
            throw OrderException.dataIntegrityViolation("数据完整性验证过程中发生异常：" + e.getMessage());
        }
    }
    
    /**
     * 验证客户数据完整性
     */
    private void validateCustomerIntegrity(Long customerId, List<String> errors) {
        if (customerId == null) {
            errors.add("客户ID不能为空");
            return;
        }
        
        try {
            // 检查客户是否存在
            if (!customerService.existsById(customerId)) {
                errors.add("客户不存在：" + customerId);
                return;
            }
            
            // 检查客户状态是否有效
            if (!customerService.isValidCustomer(customerId)) {
                errors.add("客户状态无效：" + customerId);
            }
            
        } catch (Exception e) {
            logger.warn("验证客户数据完整性时发生异常", e);
            errors.add("无法验证客户数据完整性：" + e.getMessage());
        }
    }
    
    /**
     * 验证产品数据完整性
     */
    private void validateProductsIntegrity(List<OrderCreateDTO.OrderItemCreateDTO> orderItems, List<String> errors) {
        if (orderItems == null || orderItems.isEmpty()) {
            errors.add("订单商品列表不能为空");
            return;
        }
        
        for (int i = 0; i < orderItems.size(); i++) {
            OrderCreateDTO.OrderItemCreateDTO item = orderItems.get(i);
            String itemPrefix = String.format("商品[%d]", i + 1);
            
            if (item.getProductId() == null) {
                errors.add(itemPrefix + "产品ID不能为空");
                continue;
            }
            
            try {
                // 检查产品是否存在
                if (!productService.existsById(item.getProductId())) {
                    errors.add(itemPrefix + "产品不存在：" + item.getProductId());
                    continue;
                }
                
                // 检查产品状态是否有效
                if (!productService.isValidProduct(item.getProductId())) {
                    errors.add(itemPrefix + "产品状态无效：" + item.getProductId());
                    continue;
                }
                
                // 检查库存是否充足
                if (item.getQuantity() != null && !productService.hasEnoughStock(item.getProductId(), item.getQuantity())) {
                    Integer availableStock = productService.getStock(item.getProductId());
                    errors.add(itemPrefix + String.format("库存不足，需要 %d，可用 %d", 
                        item.getQuantity(), availableStock));
                }
                
            } catch (Exception e) {
                logger.warn("验证产品数据完整性时发生异常", e);
                errors.add(itemPrefix + "无法验证产品数据完整性：" + e.getMessage());
            }
        }
    }
    
    /**
     * 验证金额数据完整性
     */
    private void validateAmountIntegrity(OrderCreateDTO createDTO, List<String> errors) {
        // 验证订单商品金额计算
        if (createDTO.getOrderItems() != null) {
            BigDecimal calculatedSubtotal = BigDecimal.ZERO;
            
            for (OrderCreateDTO.OrderItemCreateDTO item : createDTO.getOrderItems()) {
                if (item.getTotalPrice() != null) {
                    calculatedSubtotal = calculatedSubtotal.add(item.getTotalPrice());
                }
                
                // 验证单个商品金额计算
                if (item.getQuantity() != null && item.getUnitPrice() != null && item.getTotalPrice() != null) {
                    BigDecimal expectedTotal = item.getUnitPrice().multiply(new BigDecimal(item.getQuantity()));
                    if (item.getTotalPrice().compareTo(expectedTotal) != 0) {
                        errors.add(String.format("商品 %s 金额计算错误，应为：%s，实际：%s", 
                            item.getProductName(), expectedTotal, item.getTotalPrice()));
                    }
                }
            }
            
            // 验证小计金额
            if (createDTO.getSubtotal() != null && calculatedSubtotal.compareTo(createDTO.getSubtotal()) != 0) {
                errors.add(String.format("订单小计金额计算错误，应为：%s，实际：%s", 
                    calculatedSubtotal, createDTO.getSubtotal()));
            }
        }
        
        // 验证总金额计算
        if (createDTO.getTotalAmount() != null && createDTO.getSubtotal() != null) {
            BigDecimal expectedTotal = createDTO.getSubtotal();
            
            if (createDTO.getShippingFee() != null) {
                expectedTotal = expectedTotal.add(createDTO.getShippingFee());
            }
            
            if (createDTO.getDiscount() != null) {
                expectedTotal = expectedTotal.subtract(createDTO.getDiscount());
            }
            
            if (createDTO.getTotalAmount().compareTo(expectedTotal) != 0) {
                errors.add(String.format("订单总金额计算错误，应为：%s，实际：%s", 
                    expectedTotal, createDTO.getTotalAmount()));
            }
        }
    }
    
    /**
     * 验证数据一致性
     */
    private void validateDataConsistency(OrderCreateDTO createDTO, List<String> errors) {
        // 验证租赁订单的数据一致性
        if ("rental".equals(createDTO.getType())) {
            if (createDTO.getRentalStartDate() == null) {
                errors.add("租赁订单必须指定租赁开始日期");
            }
            
            if (createDTO.getRentalEndDate() == null) {
                errors.add("租赁订单必须指定租赁结束日期");
            }
            
            if (createDTO.getRentalStartDate() != null && createDTO.getRentalEndDate() != null) {
                if (createDTO.getRentalEndDate().isBefore(createDTO.getRentalStartDate())) {
                    errors.add("租赁结束日期不能早于开始日期");
                }
                
                // 验证租赁天数计算
                if (createDTO.getRentalDays() != null) {
                    long actualDays = java.time.temporal.ChronoUnit.DAYS.between(
                        createDTO.getRentalStartDate(), createDTO.getRentalEndDate()) + 1;
                    
                    if (createDTO.getRentalDays() != actualDays) {
                        errors.add(String.format("租赁天数计算错误，应为：%d，实际：%d", 
                            actualDays, createDTO.getRentalDays()));
                    }
                }
            }
        }
        
        // 验证订单号唯一性（这里可以添加数据库查询）
        if (createDTO.getOrderNumber() != null) {
            // TODO: 查询数据库验证订单号是否已存在
            // 由于当前没有实现数据库查询，这里跳过
        }
        
        // 验证配送地址与客户地址的一致性
        // TODO: 可以添加更多的数据一致性验证逻辑
    }
    
    /**
     * 验证状态数据完整性
     */
    private void validateStatusIntegrity(String status, List<String> errors) {
        if (status == null || status.trim().isEmpty()) {
            errors.add("订单状态不能为空");
            return;
        }
        
        // 验证状态值是否有效
        String[] validStatuses = {"pending", "confirmed", "processing", "shipped", "delivered", "completed", "cancelled"};
        boolean isValidStatus = false;
        for (String validStatus : validStatuses) {
            if (validStatus.equals(status)) {
                isValidStatus = true;
                break;
            }
        }
        
        if (!isValidStatus) {
            errors.add("订单状态无效：" + status);
        }
    }
    
    /**
     * 验证批量操作数据完整性
     * 
     * @param orderIds 订单ID列表
     * @param operation 操作类型
     * @throws OrderException 验证失败时抛出异常
     */
    public void validateBatchOperationIntegrity(List<Long> orderIds, String operation) {
        logger.debug("开始验证批量操作数据完整性，操作类型: {}, 订单数量: {}", operation, orderIds.size());
        
        List<String> errors = new ArrayList<>();
        
        try {
            // 验证订单ID列表
            if (orderIds == null || orderIds.isEmpty()) {
                errors.add("订单ID列表不能为空");
            } else {
                // 验证每个订单ID
                for (int i = 0; i < orderIds.size(); i++) {
                    Long orderId = orderIds.get(i);
                    if (orderId == null || orderId <= 0) {
                        errors.add(String.format("订单ID[%d]无效：%s", i, orderId));
                    }
                }
                
                // 验证批量操作数量限制
                if (orderIds.size() > 100) {
                    errors.add("批量操作订单数量不能超过100个");
                }
            }
            
            // 验证操作类型
            if (operation == null || operation.trim().isEmpty()) {
                errors.add("操作类型不能为空");
            }
            
            // 如果有错误，抛出异常
            if (!errors.isEmpty()) {
                String errorMessage = "批量操作数据完整性验证失败：" + String.join("; ", errors);
                throw OrderException.dataIntegrityViolation(errorMessage);
            }
            
            logger.debug("批量操作数据完整性验证通过");
            
        } catch (OrderException e) {
            throw e;
        } catch (Exception e) {
            logger.error("批量操作数据完整性验证过程中发生异常", e);
            throw OrderException.dataIntegrityViolation("批量操作数据完整性验证过程中发生异常：" + e.getMessage());
        }
    }
}