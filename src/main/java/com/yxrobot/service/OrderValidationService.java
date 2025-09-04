package com.yxrobot.service;

import com.yxrobot.dto.OrderCreateDTO;
import com.yxrobot.dto.OrderDTO;
import com.yxrobot.dto.OrderItemDTO;
import com.yxrobot.entity.Order;
import com.yxrobot.exception.OrderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 订单数据验证服务
 * 负责订单相关数据的验证逻辑
 * 
 * 验证范围：
 * 1. 订单基本信息验证
 * 2. 客户信息验证
 * 3. 产品信息验证
 * 4. 金额计算验证
 * 5. 业务规则验证
 * 6. 状态流转验证
 */
@Service
public class OrderValidationService {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderValidationService.class);
    
    // 订单号格式正则表达式
    private static final Pattern ORDER_NUMBER_PATTERN = Pattern.compile("^[A-Z]{3}\\d{10}$");
    
    // 电话号码格式正则表达式
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    
    // 邮箱格式正则表达式
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    @Autowired
    private CustomerService customerService;
    
    @Autowired
    private ProductService productService;
    
    /**
     * 验证订单创建数据
     */
    public ValidationResult validateOrderCreate(OrderCreateDTO createDTO) {
        ValidationResult result = new ValidationResult();
        
        try {
            logger.debug("开始验证订单创建数据");
            
            // 基本信息验证
            validateBasicInfo(createDTO, result);
            
            // 客户信息验证
            validateCustomerInfo(createDTO, result);
            
            // 订单商品验证
            validateOrderItems(createDTO, result);
            
            // 金额计算验证
            validateAmountCalculation(createDTO, result);
            
            // 业务规则验证
            validateBusinessRules(createDTO, result);
            
            logger.debug("订单创建数据验证完成，错误数量: {}", result.getErrors().size());
            
        } catch (Exception e) {
            logger.error("订单创建数据验证异常", e);
            result.addError("VALIDATION_ERROR", "数据验证过程中发生异常：" + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 验证订单更新数据
     */
    public ValidationResult validateOrderUpdate(Long orderId, OrderDTO updateDTO) {
        ValidationResult result = new ValidationResult();
        
        try {
            logger.debug("开始验证订单更新数据，订单ID: {}", orderId);
            
            // 订单存在性验证
            if (orderId == null || orderId <= 0) {
                result.addError("INVALID_ORDER_ID", "订单ID无效");
                return result;
            }
            
            // 基本信息验证（更新场景）
            validateBasicInfoForUpdate(updateDTO, result);
            
            // 状态流转验证
            validateStatusTransition(updateDTO, result);
            
            logger.debug("订单更新数据验证完成，错误数量: {}", result.getErrors().size());
            
        } catch (Exception e) {
            logger.error("订单更新数据验证异常", e);
            result.addError("VALIDATION_ERROR", "数据验证过程中发生异常：" + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 验证订单状态变更
     */
    public ValidationResult validateStatusChange(String currentStatus, String targetStatus) {
        ValidationResult result = new ValidationResult();
        
        try {
            logger.debug("验证订单状态变更：{} -> {}", currentStatus, targetStatus);
            
            if (currentStatus == null || currentStatus.trim().isEmpty()) {
                result.addError("INVALID_CURRENT_STATUS", "当前状态不能为空");
                return result;
            }
            
            if (targetStatus == null || targetStatus.trim().isEmpty()) {
                result.addError("INVALID_TARGET_STATUS", "目标状态不能为空");
                return result;
            }
            
            // 验证状态流转的合法性
            if (!isValidStatusTransition(currentStatus, targetStatus)) {
                result.addError("INVALID_STATUS_TRANSITION", 
                    String.format("不允许从状态 %s 变更为 %s", currentStatus, targetStatus));
            }
            
        } catch (Exception e) {
            logger.error("订单状态变更验证异常", e);
            result.addError("VALIDATION_ERROR", "状态变更验证过程中发生异常：" + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 验证基本信息
     */
    private void validateBasicInfo(OrderCreateDTO createDTO, ValidationResult result) {
        // 订单号验证
        if (createDTO.getOrderNumber() == null || createDTO.getOrderNumber().trim().isEmpty()) {
            result.addError("ORDER_NUMBER_REQUIRED", "订单号不能为空");
        } else if (!ORDER_NUMBER_PATTERN.matcher(createDTO.getOrderNumber()).matches()) {
            result.addError("INVALID_ORDER_NUMBER_FORMAT", "订单号格式不正确，应为3位字母+10位数字");
        }
        
        // 订单类型验证
        if (createDTO.getType() == null || createDTO.getType().trim().isEmpty()) {
            result.addError("ORDER_TYPE_REQUIRED", "订单类型不能为空");
        } else if (!isValidOrderType(createDTO.getType())) {
            result.addError("INVALID_ORDER_TYPE", "订单类型无效，只支持 sales 或 rental");
        }
        
        // 配送地址验证
        if (createDTO.getDeliveryAddress() == null || createDTO.getDeliveryAddress().trim().isEmpty()) {
            result.addError("DELIVERY_ADDRESS_REQUIRED", "配送地址不能为空");
        } else if (createDTO.getDeliveryAddress().length() < 10) {
            result.addError("DELIVERY_ADDRESS_TOO_SHORT", "配送地址至少需要10个字符");
        } else if (createDTO.getDeliveryAddress().length() > 500) {
            result.addError("DELIVERY_ADDRESS_TOO_LONG", "配送地址不能超过500个字符");
        }
        
        // 租赁订单特殊验证
        if ("rental".equals(createDTO.getType())) {
            validateRentalInfo(createDTO, result);
        }
    }
    
    /**
     * 验证客户信息
     */
    private void validateCustomerInfo(OrderCreateDTO createDTO, ValidationResult result) {
        if (createDTO.getCustomerId() == null) {
            result.addError("CUSTOMER_ID_REQUIRED", "客户ID不能为空");
            return;
        }
        
        try {
            // 验证客户是否存在
            boolean customerExists = customerService.existsById(createDTO.getCustomerId());
            if (!customerExists) {
                result.addError("CUSTOMER_NOT_FOUND", "客户不存在：" + createDTO.getCustomerId());
            }
        } catch (Exception e) {
            logger.warn("验证客户信息时发生异常", e);
            result.addWarning("CUSTOMER_VALIDATION_WARNING", "无法验证客户信息：" + e.getMessage());
        }
    }
    
    /**
     * 验证订单商品
     */
    private void validateOrderItems(OrderCreateDTO createDTO, ValidationResult result) {
        if (createDTO.getOrderItems() == null || createDTO.getOrderItems().isEmpty()) {
            result.addError("ORDER_ITEMS_REQUIRED", "订单必须包含至少一个商品");
            return;
        }
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        for (int i = 0; i < createDTO.getOrderItems().size(); i++) {
            OrderCreateDTO.OrderItemCreateDTO item = createDTO.getOrderItems().get(i);
            String itemPrefix = String.format("商品[%d]", i + 1);
            
            // 产品ID验证
            if (item.getProductId() == null) {
                result.addError("PRODUCT_ID_REQUIRED", itemPrefix + "产品ID不能为空");
                continue;
            }
            
            // 产品名称验证
            if (item.getProductName() == null || item.getProductName().trim().isEmpty()) {
                result.addError("PRODUCT_NAME_REQUIRED", itemPrefix + "产品名称不能为空");
            }
            
            // 数量验证
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                result.addError("INVALID_QUANTITY", itemPrefix + "数量必须大于0");
            }
            
            // 单价验证
            if (item.getUnitPrice() == null || item.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
                result.addError("INVALID_UNIT_PRICE", itemPrefix + "单价必须大于0");
            }
            
            // 小计验证
            if (item.getTotalPrice() == null || item.getTotalPrice().compareTo(BigDecimal.ZERO) <= 0) {
                result.addError("INVALID_TOTAL_PRICE", itemPrefix + "小计金额必须大于0");
            } else if (item.getQuantity() != null && item.getUnitPrice() != null) {
                BigDecimal expectedTotal = item.getUnitPrice().multiply(new BigDecimal(item.getQuantity()));
                if (item.getTotalPrice().compareTo(expectedTotal) != 0) {
                    result.addError("INCORRECT_TOTAL_PRICE", 
                        itemPrefix + "小计金额计算错误，应为：" + expectedTotal);
                }
            }
            
            // 累计总金额
            if (item.getTotalPrice() != null) {
                totalAmount = totalAmount.add(item.getTotalPrice());
            }
            
            // 验证产品是否存在
            try {
                boolean productExists = productService.existsById(item.getProductId());
                if (!productExists) {
                    result.addError("PRODUCT_NOT_FOUND", itemPrefix + "产品不存在：" + item.getProductId());
                }
            } catch (Exception e) {
                logger.warn("验证产品信息时发生异常", e);
                result.addWarning("PRODUCT_VALIDATION_WARNING", 
                    itemPrefix + "无法验证产品信息：" + e.getMessage());
            }
        }
    }
    
    /**
     * 验证金额计算
     */
    private void validateAmountCalculation(OrderCreateDTO createDTO, ValidationResult result) {
        if (createDTO.getTotalAmount() == null) {
            result.addError("TOTAL_AMOUNT_REQUIRED", "订单总金额不能为空");
            return;
        }
        
        if (createDTO.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            result.addError("INVALID_TOTAL_AMOUNT", "订单总金额必须大于0");
            return;
        }
        
        // 计算预期总金额
        BigDecimal expectedTotal = BigDecimal.ZERO;
        
        // 小计
        if (createDTO.getSubtotal() != null) {
            expectedTotal = expectedTotal.add(createDTO.getSubtotal());
        }
        
        // 运费
        if (createDTO.getShippingFee() != null) {
            expectedTotal = expectedTotal.add(createDTO.getShippingFee());
        }
        
        // 减去折扣
        if (createDTO.getDiscount() != null) {
            expectedTotal = expectedTotal.subtract(createDTO.getDiscount());
        }
        
        // 验证总金额计算是否正确
        if (createDTO.getTotalAmount().compareTo(expectedTotal) != 0) {
            result.addError("INCORRECT_TOTAL_AMOUNT", 
                String.format("订单总金额计算错误，应为：%s，实际：%s", 
                    expectedTotal, createDTO.getTotalAmount()));
        }
    }
    
    /**
     * 验证业务规则
     */
    private void validateBusinessRules(OrderCreateDTO createDTO, ValidationResult result) {
        // 最大订单金额限制
        BigDecimal maxOrderAmount = new BigDecimal("999999.99");
        if (createDTO.getTotalAmount() != null && 
            createDTO.getTotalAmount().compareTo(maxOrderAmount) > 0) {
            result.addError("ORDER_AMOUNT_EXCEEDED", 
                "订单金额不能超过 " + maxOrderAmount);
        }
        
        // 最大商品数量限制
        if (createDTO.getOrderItems() != null && createDTO.getOrderItems().size() > 50) {
            result.addError("TOO_MANY_ITEMS", "订单商品数量不能超过50个");
        }
        
        // 预期交付日期验证
        if (createDTO.getExpectedDeliveryDate() != null) {
            LocalDate today = LocalDate.now();
            if (createDTO.getExpectedDeliveryDate().isBefore(today)) {
                result.addError("INVALID_DELIVERY_DATE", "预期交付日期不能早于今天");
            }
            
            LocalDate maxDeliveryDate = today.plusDays(365);
            if (createDTO.getExpectedDeliveryDate().isAfter(maxDeliveryDate)) {
                result.addError("DELIVERY_DATE_TOO_FAR", "预期交付日期不能超过一年");
            }
        }
    }
    
    /**
     * 验证租赁信息
     */
    private void validateRentalInfo(OrderCreateDTO createDTO, ValidationResult result) {
        if (createDTO.getRentalStartDate() == null) {
            result.addError("RENTAL_START_DATE_REQUIRED", "租赁开始日期不能为空");
        }
        
        if (createDTO.getRentalEndDate() == null) {
            result.addError("RENTAL_END_DATE_REQUIRED", "租赁结束日期不能为空");
        }
        
        if (createDTO.getRentalStartDate() != null && createDTO.getRentalEndDate() != null) {
            if (createDTO.getRentalEndDate().isBefore(createDTO.getRentalStartDate())) {
                result.addError("INVALID_RENTAL_PERIOD", "租赁结束日期不能早于开始日期");
            }
            
            // 验证租赁天数
            long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(
                createDTO.getRentalStartDate(), createDTO.getRentalEndDate()) + 1;
            
            if (createDTO.getRentalDays() != null && createDTO.getRentalDays() != daysBetween) {
                result.addError("INCORRECT_RENTAL_DAYS", 
                    String.format("租赁天数计算错误，应为：%d，实际：%d", daysBetween, createDTO.getRentalDays()));
            }
        }
    }
    
    /**
     * 验证更新场景的基本信息
     */
    private void validateBasicInfoForUpdate(OrderDTO updateDTO, ValidationResult result) {
        // 更新场景下的验证逻辑
        if (updateDTO.getTotalAmount() != null && 
            updateDTO.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            result.addError("INVALID_TOTAL_AMOUNT", "订单总金额必须大于0");
        }
        
        // 其他更新验证逻辑...
    }
    
    /**
     * 验证状态流转
     */
    private void validateStatusTransition(OrderDTO updateDTO, ValidationResult result) {
        // 状态流转验证逻辑
        if (updateDTO.getStatus() != null && !isValidOrderStatus(updateDTO.getStatus())) {
            result.addError("INVALID_ORDER_STATUS", "订单状态无效：" + updateDTO.getStatus());
        }
    }
    
    /**
     * 检查订单类型是否有效
     */
    private boolean isValidOrderType(String type) {
        return "sales".equals(type) || "rental".equals(type);
    }
    
    /**
     * 检查订单状态是否有效
     */
    private boolean isValidOrderStatus(String status) {
        return "pending".equals(status) || "confirmed".equals(status) || 
               "processing".equals(status) || "shipped".equals(status) ||
               "delivered".equals(status) || "completed".equals(status) || 
               "cancelled".equals(status);
    }
    
    /**
     * 检查状态流转是否合法
     */
    private boolean isValidStatusTransition(String currentStatus, String targetStatus) {
        // 定义允许的状态流转
        switch (currentStatus) {
            case "pending":
                return "confirmed".equals(targetStatus) || "cancelled".equals(targetStatus);
            case "confirmed":
                return "processing".equals(targetStatus) || "cancelled".equals(targetStatus);
            case "processing":
                return "shipped".equals(targetStatus);
            case "shipped":
                return "delivered".equals(targetStatus);
            case "delivered":
                return "completed".equals(targetStatus);
            case "completed":
            case "cancelled":
                return false; // 终态，不允许再变更
            default:
                return false;
        }
    }
    
    /**
     * 验证结果类
     */
    public static class ValidationResult {
        private List<ValidationError> errors = new ArrayList<>();
        private List<ValidationError> warnings = new ArrayList<>();
        
        public void addError(String code, String message) {
            errors.add(new ValidationError(code, message));
        }
        
        public void addWarning(String code, String message) {
            warnings.add(new ValidationError(code, message));
        }
        
        public boolean hasErrors() {
            return !errors.isEmpty();
        }
        
        public boolean hasWarnings() {
            return !warnings.isEmpty();
        }
        
        public boolean isValid() {
            return !hasErrors();
        }
        
        public List<ValidationError> getErrors() {
            return errors;
        }
        
        public List<ValidationError> getWarnings() {
            return warnings;
        }
    }
    
    /**
     * 验证错误类
     */
    public static class ValidationError {
        private String code;
        private String message;
        
        public ValidationError(String code, String message) {
            this.code = code;
            this.message = message;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getMessage() {
            return message;
        }
        
        @Override
        public String toString() {
            return String.format("[%s] %s", code, message);
        }
    }
}