package com.yxrobot.validation;

import com.yxrobot.dto.CustomerCreateDTO;
import com.yxrobot.dto.CustomerUpdateDTO;
import com.yxrobot.entity.Customer;
import com.yxrobot.entity.CustomerLevel;
import com.yxrobot.entity.CustomerStatus;
import com.yxrobot.exception.CustomerException;
import com.yxrobot.service.CustomerValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 客户验证集成测试
 * 测试所有验证器的协同工作
 */
@SpringBootTest
@ActiveProfiles("test")
public class CustomerValidationIntegrationTest {
    
    @Autowired
    private CustomerValidationService customerValidationService;
    
    @Autowired
    private CustomerValidator customerValidator;
    
    @Autowired
    private CustomerFormValidator customerFormValidator;
    
    @Autowired
    private PhoneNumberValidator phoneNumberValidator;
    
    @Autowired
    private EmailValidator emailValidator;
    
    @Autowired
    private DataIntegrityValidator dataIntegrityValidator;
    
    private CustomerCreateDTO validCreateDTO;
    private CustomerUpdateDTO validUpdateDTO;
    private Customer validCustomer;
    
    @BeforeEach
    void setUp() {
        // 准备有效的测试数据
        validCreateDTO = new CustomerCreateDTO();
        validCreateDTO.setName("张三");
        validCreateDTO.setPhone("13800138000");
        validCreateDTO.setEmail("zhangsan@example.com");
        validCreateDTO.setCompany("测试公司");
        validCreateDTO.setLevel("regular");
        validCreateDTO.setStatus("active");
        validCreateDTO.setNotes("测试客户");
        
        validUpdateDTO = new CustomerUpdateDTO();
        validUpdateDTO.setName("李四");
        validUpdateDTO.setPhone("13900139000");
        validUpdateDTO.setEmail("lisi@example.com");
        validUpdateDTO.setCompany("更新公司");
        validUpdateDTO.setLevel("vip");
        validUpdateDTO.setStatus("active");
        validUpdateDTO.setNotes("更新测试客户");
        
        validCustomer = new Customer();
        validCustomer.setId(1L);
        validCustomer.setCustomerName("王五");
        validCustomer.setPhone("13700137000");
        validCustomer.setEmail("wangwu@example.com");
        validCustomer.setContactPerson("测试联系人");
        validCustomer.setCustomerLevel(CustomerLevel.REGULAR);
        validCustomer.setCustomerStatus(CustomerStatus.ACTIVE);
        validCustomer.setTotalSpent(new BigDecimal("5000.00"));
        validCustomer.setCustomerValue(new BigDecimal("7.5"));
        validCustomer.setCreatedAt(LocalDateTime.now().minusDays(30));
        validCustomer.setUpdatedAt(LocalDateTime.now());
        validCustomer.setRegisteredAt(LocalDateTime.now().minusDays(30));
        validCustomer.setLastActiveAt(LocalDateTime.now().minusDays(1));
    }
    
    @Test
    void testValidCustomerCreateValidation() {
        // 测试有效的客户创建数据
        CustomerValidationService.ValidationSummary summary = 
            customerValidationService.validateCustomerCreate(validCreateDTO);
        
        assertTrue(summary.isValid(), "有效数据应该通过验证");
        assertFalse(summary.hasErrors(), "不应该有错误");
        assertTrue(summary.isBusinessValidationPassed(), "业务验证应该通过");
    }
    
    @Test
    void testInvalidCustomerCreateValidation() {
        // 测试无效的客户创建数据
        CustomerCreateDTO invalidDTO = new CustomerCreateDTO();
        invalidDTO.setName(""); // 空姓名
        invalidDTO.setPhone("invalid_phone"); // 无效电话
        invalidDTO.setEmail("invalid_email"); // 无效邮箱
        
        CustomerValidationService.ValidationSummary summary = 
            customerValidationService.validateCustomerCreate(invalidDTO);
        
        assertFalse(summary.isValid(), "无效数据应该验证失败");
        assertTrue(summary.hasErrors(), "应该有错误");
        assertFalse(summary.isBusinessValidationPassed(), "业务验证不应该通过");
        
        // 检查具体错误
        List<CustomerValidationService.ValidationMessage> errors = summary.getErrors();
        assertTrue(errors.stream().anyMatch(e -> e.getField().equals("name")), "应该有姓名错误");
        assertTrue(errors.stream().anyMatch(e -> e.getField().equals("phone")), "应该有电话错误");
        assertTrue(errors.stream().anyMatch(e -> e.getField().equals("email")), "应该有邮箱错误");
    }
    
    @Test
    void testValidCustomerUpdateValidation() {
        // 测试有效的客户更新数据
        CustomerValidationService.ValidationSummary summary = 
            customerValidationService.validateCustomerUpdate(1L, validUpdateDTO);
        
        assertTrue(summary.isValid(), "有效数据应该通过验证");
        assertFalse(summary.hasErrors(), "不应该有错误");
        assertTrue(summary.isBusinessValidationPassed(), "业务验证应该通过");
    }
    
    @Test
    void testInvalidCustomerIdValidation() {
        // 测试无效的客户ID
        assertThrows(CustomerException.InvalidCustomerDataException.class, () -> {
            customerValidationService.validateCustomerUpdate(null, validUpdateDTO);
        }, "空客户ID应该抛出异常");
        
        assertThrows(CustomerException.InvalidCustomerDataException.class, () -> {
            customerValidationService.validateCustomerUpdate(-1L, validUpdateDTO);
        }, "负数客户ID应该抛出异常");
    }
    
    @Test
    void testCustomerIntegrityValidation() {
        // 测试客户数据完整性验证
        CustomerValidationService.ValidationSummary summary = 
            customerValidationService.validateCustomerIntegrity(validCustomer);
        
        assertTrue(summary.isValid(), "有效客户数据应该通过完整性验证");
        assertFalse(summary.hasErrors(), "不应该有错误");
    }
    
    @Test
    void testCustomerIntegrityValidationWithInconsistentData() {
        // 测试不一致的客户数据
        Customer inconsistentCustomer = new Customer();
        inconsistentCustomer.setId(1L);
        inconsistentCustomer.setCustomerName("测试客户");
        inconsistentCustomer.setPhone("13800138000");
        inconsistentCustomer.setCustomerLevel(CustomerLevel.PREMIUM);
        inconsistentCustomer.setCustomerStatus(CustomerStatus.ACTIVE);
        inconsistentCustomer.setTotalSpent(new BigDecimal("100.00")); // 消费金额与高级客户等级不匹配
        inconsistentCustomer.setCustomerValue(new BigDecimal("15.0")); // 超出范围的客户价值
        inconsistentCustomer.setCreatedAt(LocalDateTime.now());
        inconsistentCustomer.setUpdatedAt(LocalDateTime.now().minusDays(1)); // 更新时间早于创建时间
        
        CustomerValidationService.ValidationSummary summary = 
            customerValidationService.validateCustomerIntegrity(inconsistentCustomer);
        
        assertFalse(summary.isValid(), "不一致的数据应该验证失败");
        assertTrue(summary.hasErrors(), "应该有错误");
        assertTrue(summary.hasWarnings(), "应该有警告");
    }
    
    @Test
    void testQueryParametersValidation() {
        // 测试有效的查询参数
        assertDoesNotThrow(() -> {
            customerValidationService.validateQueryParameters("张三", "regular", "active", 1, 20);
        }, "有效查询参数不应该抛出异常");
        
        // 测试无效的查询参数
        assertThrows(CustomerException.InvalidCustomerDataException.class, () -> {
            customerValidationService.validateQueryParameters(null, null, null, 0, 20);
        }, "无效页码应该抛出异常");
        
        assertThrows(CustomerException.InvalidCustomerDataException.class, () -> {
            customerValidationService.validateQueryParameters(null, null, null, 1, 0);
        }, "无效页面大小应该抛出异常");
        
        assertThrows(CustomerException.InvalidCustomerDataException.class, () -> {
            customerValidationService.validateQueryParameters(null, "invalid_level", null, 1, 20);
        }, "无效客户等级应该抛出异常");
    }
    
    @Test
    void testBatchCustomerIntegrityValidation() {
        // 准备批量测试数据
        Customer customer1 = new Customer();
        customer1.setId(1L);
        customer1.setCustomerName("客户1");
        customer1.setPhone("13800138001");
        customer1.setEmail("customer1@example.com");
        customer1.setCustomerLevel(CustomerLevel.REGULAR);
        customer1.setCustomerStatus(CustomerStatus.ACTIVE);
        customer1.setCreatedAt(LocalDateTime.now());
        customer1.setUpdatedAt(LocalDateTime.now());
        
        Customer customer2 = new Customer();
        customer2.setId(2L);
        customer2.setCustomerName("客户2");
        customer2.setPhone("13800138001"); // 重复电话号码
        customer2.setEmail("customer2@example.com");
        customer2.setCustomerLevel(CustomerLevel.VIP);
        customer2.setCustomerStatus(CustomerStatus.ACTIVE);
        customer2.setCreatedAt(LocalDateTime.now());
        customer2.setUpdatedAt(LocalDateTime.now());
        
        List<Customer> customers = Arrays.asList(customer1, customer2);
        
        List<CustomerValidationService.ValidationSummary> summaries = 
            customerValidationService.validateCustomersIntegrity(customers);
        
        assertEquals(2, summaries.size(), "应该返回两个验证摘要");
        
        // 检查重复电话号码错误
        boolean hasPhoneError = summaries.stream()
            .anyMatch(s -> s.getErrors().stream()
                .anyMatch(e -> e.getField().equals("phone") && e.getMessage().contains("重复")));
        assertTrue(hasPhoneError, "应该检测到重复电话号码");
    }
    
    @Test
    void testPhoneNumberValidation() {
        // 测试有效电话号码
        PhoneNumberValidator.PhoneValidationResult result = 
            phoneNumberValidator.validatePhone("13800138000");
        
        assertTrue(result.isValid(), "有效电话号码应该通过验证");
        assertEquals("手机号码", result.getPhoneType(), "应该识别为手机号码");
        assertNotNull(result.getCarrier(), "应该识别运营商");
        
        // 测试无效电话号码
        result = phoneNumberValidator.validatePhone("invalid_phone");
        assertFalse(result.isValid(), "无效电话号码应该验证失败");
        assertNotNull(result.getErrorMessage(), "应该有错误消息");
        
        // 测试虚拟运营商号码
        result = phoneNumberValidator.validatePhone("17000000000");
        assertTrue(result.isValid(), "虚拟运营商号码应该有效");
        assertEquals("虚拟运营商", result.getCarrier(), "应该识别为虚拟运营商");
        assertTrue(result.hasWarnings(), "应该有警告");
    }
    
    @Test
    void testEmailValidation() {
        // 测试有效邮箱
        EmailValidator.EmailValidationResult result = 
            emailValidator.validateEmail("test@gmail.com");
        
        assertTrue(result.isValid(), "有效邮箱应该通过验证");
        assertEquals("个人邮箱", result.getEmailType(), "应该识别为个人邮箱");
        assertEquals("Gmail", result.getProvider(), "应该识别为Gmail");
        
        // 测试无效邮箱
        result = emailValidator.validateEmail("invalid_email");
        assertFalse(result.isValid(), "无效邮箱应该验证失败");
        assertNotNull(result.getErrorMessage(), "应该有错误消息");
        
        // 测试临时邮箱
        result = emailValidator.validateEmail("test@10minutemail.com");
        assertTrue(result.isValid(), "临时邮箱格式应该有效");
        assertTrue(result.isTemporary(), "应该识别为临时邮箱");
        assertTrue(result.hasWarnings(), "应该有警告");
    }
    
    @Test
    void testCustomerFormValidation() {
        // 测试有效表单
        CustomerFormValidator.ValidationResult result = 
            customerFormValidator.validateCreateForm(validCreateDTO);
        
        assertFalse(result.hasErrors(), "有效表单不应该有错误");
        
        // 测试无效表单
        CustomerCreateDTO invalidDTO = new CustomerCreateDTO();
        invalidDTO.setName("a"); // 姓名太短
        invalidDTO.setPhone("123"); // 电话格式错误
        invalidDTO.setEmail("invalid"); // 邮箱格式错误
        
        result = customerFormValidator.validateCreateForm(invalidDTO);
        assertTrue(result.hasErrors(), "无效表单应该有错误");
        
        List<CustomerFormValidator.ValidationError> errors = result.getErrors();
        assertTrue(errors.stream().anyMatch(e -> e.getField().equals("name")), "应该有姓名错误");
        assertTrue(errors.stream().anyMatch(e -> e.getField().equals("phone")), "应该有电话错误");
        assertTrue(errors.stream().anyMatch(e -> e.getField().equals("email")), "应该有邮箱错误");
    }
    
    @Test
    void testCustomerValidatorBusinessRules() {
        // 测试业务规则验证
        Customer customer = new Customer();
        customer.setCustomerName("测试客户");
        customer.setPhone("13800138000");
        customer.setCustomerLevel(CustomerLevel.REGULAR);
        customer.setCustomerStatus(CustomerStatus.ACTIVE);
        
        assertDoesNotThrow(() -> {
            customerValidator.validateCustomerForCreate(customer);
        }, "有效客户数据不应该抛出异常");
        
        // 测试无效数据
        customer.setCustomerName(""); // 空姓名
        assertThrows(CustomerException.InvalidCustomerDataException.class, () -> {
            customerValidator.validateCustomerForCreate(customer);
        }, "空姓名应该抛出异常");
    }
    
    @Test
    void testExceptionHandling() {
        // 测试异常处理
        CustomerValidationService.ValidationSummary summary = new CustomerValidationService.ValidationSummary();
        summary.addError("test", "测试错误");
        
        assertThrows(CustomerException.InvalidCustomerDataException.class, () -> {
            summary.throwIfHasErrors();
        }, "有错误时应该抛出异常");
        
        // 测试无错误情况
        CustomerValidationService.ValidationSummary validSummary = new CustomerValidationService.ValidationSummary();
        assertDoesNotThrow(() -> {
            validSummary.throwIfHasErrors();
        }, "无错误时不应该抛出异常");
    }
    
    @Test
    void testValidationSummaryMethods() {
        CustomerValidationService.ValidationSummary summary = new CustomerValidationService.ValidationSummary();
        
        // 初始状态
        assertTrue(summary.isValid(), "初始状态应该有效");
        assertFalse(summary.hasErrors(), "初始状态不应该有错误");
        assertFalse(summary.hasWarnings(), "初始状态不应该有警告");
        assertFalse(summary.hasInfos(), "初始状态不应该有信息");
        
        // 添加错误
        summary.addError("field1", "错误1");
        assertFalse(summary.isValid(), "有错误时应该无效");
        assertTrue(summary.hasErrors(), "应该有错误");
        assertEquals(1, summary.getErrors().size(), "应该有1个错误");
        
        // 添加警告
        summary.addWarning("field2", "警告1");
        assertTrue(summary.hasWarnings(), "应该有警告");
        assertEquals(1, summary.getWarnings().size(), "应该有1个警告");
        
        // 添加信息
        summary.addInfo("field3", "信息1");
        assertTrue(summary.hasInfos(), "应该有信息");
        assertEquals(1, summary.getInfos().size(), "应该有1个信息");
        
        // 测试摘要
        String summaryText = summary.getSummary();
        assertNotNull(summaryText, "摘要不应该为空");
        assertTrue(summaryText.contains("错误: 1"), "摘要应该包含错误数量");
        assertTrue(summaryText.contains("警告: 1"), "摘要应该包含警告数量");
        assertTrue(summaryText.contains("信息: 1"), "摘要应该包含信息数量");
    }
}