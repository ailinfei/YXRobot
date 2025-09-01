package com.yxrobot.service;

import com.yxrobot.entity.RentalRecord;
import com.yxrobot.entity.RentalCustomer;
import com.yxrobot.validation.RealDataValidator;
import com.yxrobot.validation.RealDataValidator.ValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 真实数据验证测试
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("真实数据验证测试")
public class RealDataValidationTest {
    
    private RealDataValidator realDataValidator;
    
    @BeforeEach
    void setUp() {
        realDataValidator = new RealDataValidator();
    }
    
    @Test
    @DisplayName("应该通过真实租赁记录验证")
    void shouldPassValidRentalRecord() {
        // 创建真实的租赁记录
        RentalRecord record = createRealRentalRecord();
        
        ValidationResult result = realDataValidator.validateRentalRecord(record);
        
        assertTrue(result.isValid(), "真实租赁记录应该通过验证");
        assertTrue(result.getErrors().isEmpty(), "不应该有验证错误");
    }
    
    @Test
    @DisplayName("应该拒绝包含模拟数据特征的租赁记录")
    void shouldRejectMockRentalRecord() {
        // 创建包含模拟数据特征的租赁记录
        RentalRecord record = createMockRentalRecord();
        
        ValidationResult result = realDataValidator.validateRentalRecord(record);
        
        assertFalse(result.isValid(), "模拟租赁记录应该被拒绝");
        assertFalse(result.getErrors().isEmpty(), "应该有验证错误");
        assertTrue(result.getErrorMessage().contains("模拟数据特征"), "错误信息应该包含模拟数据特征");
    }
    
    @Test
    @DisplayName("应该拒绝系统生成的租赁记录")
    void shouldRejectSystemGeneratedRentalRecord() {
        // 创建系统生成的租赁记录
        RentalRecord record = createSystemGeneratedRentalRecord();
        
        ValidationResult result = realDataValidator.validateRentalRecord(record);
        
        assertFalse(result.isValid(), "系统生成的租赁记录应该被拒绝");
        assertTrue(result.getErrorMessage().contains("系统生成"), "错误信息应该包含系统生成特征");
    }
    
    @Test
    @DisplayName("应该通过真实客户信息验证")
    void shouldPassValidCustomer() {
        // 创建真实的客户信息
        RentalCustomer customer = createRealCustomer();
        
        ValidationResult result = realDataValidator.validateCustomer(customer);
        
        assertTrue(result.isValid(), "真实客户信息应该通过验证");
        assertTrue(result.getErrors().isEmpty(), "不应该有验证错误");
    }
    
    @Test
    @DisplayName("应该拒绝包含模拟数据特征的客户信息")
    void shouldRejectMockCustomer() {
        // 创建包含模拟数据特征的客户信息
        RentalCustomer customer = createMockCustomer();
        
        ValidationResult result = realDataValidator.validateCustomer(customer);
        
        assertFalse(result.isValid(), "模拟客户信息应该被拒绝");
        assertFalse(result.getErrors().isEmpty(), "应该有验证错误");
    }
    
    @Test
    @DisplayName("应该拒绝可疑邮箱地址")
    void shouldRejectSuspiciousEmail() {
        RentalCustomer customer = createRealCustomer();
        customer.setEmail("test@example.com"); // 可疑邮箱域名
        
        ValidationResult result = realDataValidator.validateCustomer(customer);
        
        assertFalse(result.isValid(), "可疑邮箱应该被拒绝");
        assertTrue(result.getErrorMessage().contains("邮箱地址疑似模拟数据"), "错误信息应该包含邮箱验证失败");
    }
    
    @Test
    @DisplayName("应该拒绝可疑电话号码")
    void shouldRejectSuspiciousPhone() {
        RentalCustomer customer = createRealCustomer();
        customer.setPhone("000-0000-1234"); // 可疑电话号码格式
        
        ValidationResult result = realDataValidator.validateCustomer(customer);
        
        assertFalse(result.isValid(), "可疑电话号码应该被拒绝");
        assertTrue(result.getErrorMessage().contains("电话号码疑似模拟数据"), "错误信息应该包含电话验证失败");
    }
    
    @Test
    @DisplayName("应该验证数据创建来源")
    void shouldValidateCreationSource() {
        RentalRecord realRecord = createRealRentalRecord();
        RentalRecord systemRecord = createSystemGeneratedRentalRecord();
        
        assertTrue(realDataValidator.validateCreationSource(realRecord), "真实数据应该通过来源验证");
        assertFalse(realDataValidator.validateCreationSource(systemRecord), "系统生成数据应该被拒绝");
    }
    
    /**
     * 创建真实的租赁记录
     */
    private RentalRecord createRealRentalRecord() {
        RentalRecord record = new RentalRecord();
        record.setRentalOrderNumber("YX2025012501");
        record.setDeviceId(1L);
        record.setCustomerId(1L);
        record.setRentalStartDate(LocalDate.now());
        record.setRentalPeriod(30);
        record.setDailyRentalFee(new BigDecimal("100.00"));
        record.setTotalRentalFee(new BigDecimal("3000.00"));
        record.setDeliveryAddress("北京市朝阳区真实地址123号");
        record.setNotes("客户要求加急配送");
        return record;
    }
    
    /**
     * 创建包含模拟数据特征的租赁记录
     */
    private RentalRecord createMockRentalRecord() {
        RentalRecord record = new RentalRecord();
        record.setRentalOrderNumber("TEST2025012501");
        record.setDeviceId(1L);
        record.setCustomerId(1L);
        record.setRentalStartDate(LocalDate.now());
        record.setRentalPeriod(30);
        record.setDailyRentalFee(new BigDecimal("100.00"));
        record.setTotalRentalFee(new BigDecimal("3000.00"));
        record.setDeliveryAddress("示例地址123号");
        record.setNotes("这是一个测试订单");
        return record;
    }
    
    /**
     * 创建系统生成的租赁记录
     */
    private RentalRecord createSystemGeneratedRentalRecord() {
        RentalRecord record = new RentalRecord();
        record.setRentalOrderNumber("SYS2025012501");
        record.setDeviceId(1L);
        record.setCustomerId(1L);
        record.setRentalStartDate(LocalDate.now());
        record.setRentalPeriod(30);
        record.setDailyRentalFee(new BigDecimal("100.00"));
        record.setTotalRentalFee(new BigDecimal("3000.00"));
        record.setDeliveryAddress("系统地址");
        record.setNotes("系统生成的订单");
        return record;
    }
    
    /**
     * 创建真实的客户信息
     */
    private RentalCustomer createRealCustomer() {
        RentalCustomer customer = new RentalCustomer();
        customer.setCustomerName("北京教育科技有限公司");
        customer.setCustomerType("enterprise");
        customer.setContactPerson("张经理");
        customer.setPhone("138-0013-8888");
        customer.setEmail("zhang.manager@company.com");
        customer.setAddress("北京市海淀区中关村大街1号");
        customer.setRegion("中国大陆");
        customer.setIndustry("教育科技");
        return customer;
    }
    
    /**
     * 创建包含模拟数据特征的客户信息
     */
    private RentalCustomer createMockCustomer() {
        RentalCustomer customer = new RentalCustomer();
        customer.setCustomerName("测试客户公司");
        customer.setCustomerType("enterprise");
        customer.setContactPerson("测试联系人");
        customer.setPhone("138-0013-8888");
        customer.setEmail("test@example.com");
        customer.setAddress("示例地址123号");
        customer.setRegion("中国大陆");
        customer.setIndustry("教育科技");
        return customer;
    }
}