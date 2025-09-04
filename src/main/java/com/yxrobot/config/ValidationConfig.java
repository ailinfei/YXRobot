package com.yxrobot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 验证配置类
 * 用于配置订单数据验证的各种规则和参数
 */
@Configuration
@ConfigurationProperties(prefix = "order.validation")
public class ValidationConfig {
    
    /**
     * 订单号验证配置
     */
    private OrderNumberConfig orderNumber = new OrderNumberConfig();
    
    /**
     * 金额验证配置
     */
    private AmountConfig amount = new AmountConfig();
    
    /**
     * 客户信息验证配置
     */
    private CustomerConfig customer = new CustomerConfig();
    
    /**
     * 产品信息验证配置
     */
    private ProductConfig product = new ProductConfig();
    
    /**
     * 租赁订单验证配置
     */
    private RentalConfig rental = new RentalConfig();
    
    /**
     * 业务规则验证配置
     */
    private BusinessRuleConfig businessRule = new BusinessRuleConfig();
    
    // Getter和Setter方法
    public OrderNumberConfig getOrderNumber() {
        return orderNumber;
    }
    
    public void setOrderNumber(OrderNumberConfig orderNumber) {
        this.orderNumber = orderNumber;
    }
    
    public AmountConfig getAmount() {
        return amount;
    }
    
    public void setAmount(AmountConfig amount) {
        this.amount = amount;
    }
    
    public CustomerConfig getCustomer() {
        return customer;
    }
    
    public void setCustomer(CustomerConfig customer) {
        this.customer = customer;
    }
    
    public ProductConfig getProduct() {
        return product;
    }
    
    public void setProduct(ProductConfig product) {
        this.product = product;
    }
    
    public RentalConfig getRental() {
        return rental;
    }
    
    public void setRental(RentalConfig rental) {
        this.rental = rental;
    }
    
    public BusinessRuleConfig getBusinessRule() {
        return businessRule;
    }
    
    public void setBusinessRule(BusinessRuleConfig businessRule) {
        this.businessRule = businessRule;
    }
    
    /**
     * 订单号验证配置
     */
    public static class OrderNumberConfig {
        private String pattern = "^[A-Z]{3}\\d{10}$";
        private int minLength = 13;
        private int maxLength = 20;
        private List<String> allowedPrefixes = List.of("ORD", "SAL", "REN");
        
        public String getPattern() {
            return pattern;
        }
        
        public void setPattern(String pattern) {
            this.pattern = pattern;
        }
        
        public int getMinLength() {
            return minLength;
        }
        
        public void setMinLength(int minLength) {
            this.minLength = minLength;
        }
        
        public int getMaxLength() {
            return maxLength;
        }
        
        public void setMaxLength(int maxLength) {
            this.maxLength = maxLength;
        }
        
        public List<String> getAllowedPrefixes() {
            return allowedPrefixes;
        }
        
        public void setAllowedPrefixes(List<String> allowedPrefixes) {
            this.allowedPrefixes = allowedPrefixes;
        }
    }
    
    /**
     * 金额验证配置
     */
    public static class AmountConfig {
        private BigDecimal minAmount = new BigDecimal("0.01");
        private BigDecimal maxAmount = new BigDecimal("999999.99");
        private int decimalPlaces = 2;
        private boolean allowZero = false;
        private boolean allowNegative = false;
        
        public BigDecimal getMinAmount() {
            return minAmount;
        }
        
        public void setMinAmount(BigDecimal minAmount) {
            this.minAmount = minAmount;
        }
        
        public BigDecimal getMaxAmount() {
            return maxAmount;
        }
        
        public void setMaxAmount(BigDecimal maxAmount) {
            this.maxAmount = maxAmount;
        }
        
        public int getDecimalPlaces() {
            return decimalPlaces;
        }
        
        public void setDecimalPlaces(int decimalPlaces) {
            this.decimalPlaces = decimalPlaces;
        }
        
        public boolean isAllowZero() {
            return allowZero;
        }
        
        public void setAllowZero(boolean allowZero) {
            this.allowZero = allowZero;
        }
        
        public boolean isAllowNegative() {
            return allowNegative;
        }
        
        public void setAllowNegative(boolean allowNegative) {
            this.allowNegative = allowNegative;
        }
    }
    
    /**
     * 客户信息验证配置
     */
    public static class CustomerConfig {
        private String phonePattern = "^1[3-9]\\d{9}$";
        private String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        private int nameMinLength = 2;
        private int nameMaxLength = 50;
        private int addressMaxLength = 200;
        
        public String getPhonePattern() {
            return phonePattern;
        }
        
        public void setPhonePattern(String phonePattern) {
            this.phonePattern = phonePattern;
        }
        
        public String getEmailPattern() {
            return emailPattern;
        }
        
        public void setEmailPattern(String emailPattern) {
            this.emailPattern = emailPattern;
        }
        
        public int getNameMinLength() {
            return nameMinLength;
        }
        
        public void setNameMinLength(int nameMinLength) {
            this.nameMinLength = nameMinLength;
        }
        
        public int getNameMaxLength() {
            return nameMaxLength;
        }
        
        public void setNameMaxLength(int nameMaxLength) {
            this.nameMaxLength = nameMaxLength;
        }
        
        public int getAddressMaxLength() {
            return addressMaxLength;
        }
        
        public void setAddressMaxLength(int addressMaxLength) {
            this.addressMaxLength = addressMaxLength;
        }
    }
    
    /**
     * 产品信息验证配置
     */
    public static class ProductConfig {
        private int nameMaxLength = 100;
        private int maxQuantity = 9999;
        private int minQuantity = 1;
        private boolean checkStock = true;
        private boolean checkProductStatus = true;
        
        public int getNameMaxLength() {
            return nameMaxLength;
        }
        
        public void setNameMaxLength(int nameMaxLength) {
            this.nameMaxLength = nameMaxLength;
        }
        
        public int getMaxQuantity() {
            return maxQuantity;
        }
        
        public void setMaxQuantity(int maxQuantity) {
            this.maxQuantity = maxQuantity;
        }
        
        public int getMinQuantity() {
            return minQuantity;
        }
        
        public void setMinQuantity(int minQuantity) {
            this.minQuantity = minQuantity;
        }
        
        public boolean isCheckStock() {
            return checkStock;
        }
        
        public void setCheckStock(boolean checkStock) {
            this.checkStock = checkStock;
        }
        
        public boolean isCheckProductStatus() {
            return checkProductStatus;
        }
        
        public void setCheckProductStatus(boolean checkProductStatus) {
            this.checkProductStatus = checkProductStatus;
        }
    }
    
    /**
     * 租赁订单验证配置
     */
    public static class RentalConfig {
        private int minRentalDays = 1;
        private int maxRentalDays = 365;
        private int advanceBookingDays = 30;
        private boolean requireDeposit = true;
        private BigDecimal minDepositRate = new BigDecimal("0.1");
        private BigDecimal maxDepositRate = new BigDecimal("0.5");
        
        public int getMinRentalDays() {
            return minRentalDays;
        }
        
        public void setMinRentalDays(int minRentalDays) {
            this.minRentalDays = minRentalDays;
        }
        
        public int getMaxRentalDays() {
            return maxRentalDays;
        }
        
        public void setMaxRentalDays(int maxRentalDays) {
            this.maxRentalDays = maxRentalDays;
        }
        
        public int getAdvanceBookingDays() {
            return advanceBookingDays;
        }
        
        public void setAdvanceBookingDays(int advanceBookingDays) {
            this.advanceBookingDays = advanceBookingDays;
        }
        
        public boolean isRequireDeposit() {
            return requireDeposit;
        }
        
        public void setRequireDeposit(boolean requireDeposit) {
            this.requireDeposit = requireDeposit;
        }
        
        public BigDecimal getMinDepositRate() {
            return minDepositRate;
        }
        
        public void setMinDepositRate(BigDecimal minDepositRate) {
            this.minDepositRate = minDepositRate;
        }
        
        public BigDecimal getMaxDepositRate() {
            return maxDepositRate;
        }
        
        public void setMaxDepositRate(BigDecimal maxDepositRate) {
            this.maxDepositRate = maxDepositRate;
        }
    }
    
    /**
     * 业务规则验证配置
     */
    public static class BusinessRuleConfig {
        private List<String> allowedOrderTypes = List.of("sales", "rental");
        private List<String> allowedOrderStatuses = List.of("pending", "confirmed", "processing", "shipped", "delivered", "completed", "cancelled");
        private List<String> allowedPaymentMethods = List.of("cash", "card", "online", "transfer");
        private Map<String, List<String>> statusTransitions = Map.of(
            "pending", List.of("confirmed", "cancelled"),
            "confirmed", List.of("processing", "cancelled"),
            "processing", List.of("shipped", "cancelled"),
            "shipped", List.of("delivered", "cancelled"),
            "delivered", List.of("completed"),
            "completed", List.of(),
            "cancelled", List.of()
        );
        
        public List<String> getAllowedOrderTypes() {
            return allowedOrderTypes;
        }
        
        public void setAllowedOrderTypes(List<String> allowedOrderTypes) {
            this.allowedOrderTypes = allowedOrderTypes;
        }
        
        public List<String> getAllowedOrderStatuses() {
            return allowedOrderStatuses;
        }
        
        public void setAllowedOrderStatuses(List<String> allowedOrderStatuses) {
            this.allowedOrderStatuses = allowedOrderStatuses;
        }
        
        public List<String> getAllowedPaymentMethods() {
            return allowedPaymentMethods;
        }
        
        public void setAllowedPaymentMethods(List<String> allowedPaymentMethods) {
            this.allowedPaymentMethods = allowedPaymentMethods;
        }
        
        public Map<String, List<String>> getStatusTransitions() {
            return statusTransitions;
        }
        
        public void setStatusTransitions(Map<String, List<String>> statusTransitions) {
            this.statusTransitions = statusTransitions;
        }
    }
}