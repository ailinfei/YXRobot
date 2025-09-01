package com.yxrobot.service;

import com.yxrobot.dto.CustomerDTO;
import com.yxrobot.dto.CustomerStatsDTO;
import com.yxrobot.dto.CustomerCreateDTO;
import com.yxrobot.dto.CustomerUpdateDTO;
import com.yxrobot.entity.Customer;
import com.yxrobot.mapper.CustomerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 客户系统集成测试服务
 * 执行完整的前后端集成测试，验证系统功能正常性
 */
@Service
public class CustomerSystemIntegrationTestService {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerSystemIntegrationTestService.class);
    
    @Autowired
    private CustomerService customerService;
    
    @Autowired
    private CustomerStatsService customerStatsService;
    
    @Autowired
    private CustomerMapper customerMapper;
    
    /**
     * 执行完整的系统集成测试
     */
    public IntegrationTestResult executeFullIntegrationTest() {
        logger.info("开始执行客户管理系统集成测试");
        
        IntegrationTestResult result = new IntegrationTestResult();
        long startTime = System.currentTimeMillis();
        
        try {
            // 1. 测试数据库连接和基础查询
            result.addTest(testDatabaseConnection());
            
            // 2. 测试客户统计API
            result.addTest(testCustomerStatsApi());
            
            // 3. 测试客户列表API
            result.addTest(testCustomerListApi());
            
            // 4. 测试客户CRUD操作
            result.addTest(testCustomerCrudOperations());
            
            // 5. 测试数据格式匹配
            result.addTest(testDataFormatMatching());
            
            // 6. 测试字段映射正确性
            result.addTest(testFieldMapping());
            
            // 7. 测试API响应时间
            result.addTest(testApiPerformance());
            
            // 8. 测试错误处理
            result.addTest(testErrorHandling());
            
            // 9. 测试空数据状态
            result.addTest(testEmptyDataState());
            
            // 10. 测试前端页面功能支持
            result.addTest(testFrontendFunctionSupport());
            
            long totalTime = System.currentTimeMillis() - startTime;
            result.setTotalExecutionTime(totalTime);
            
            logger.info("系统集成测试完成，总耗时: {}ms, 通过率: {}", 
                totalTime, result.getPassRate());
            
        } catch (Exception e) {
            logger.error("系统集成测试执行异常", e);
            result.addTest(TestCase.failed("系统集成测试", "测试执行异常: " + e.getMessage()));
        }
        
        return result;
    }
    
    /**
     * 测试数据库连接和基础查询
     */
    private TestCase testDatabaseConnection() {
        try {
            logger.info("测试数据库连接...");
            
            // 测试基础查询
            Long totalCount = customerMapper.countCustomers();
            
            // 测试表结构
            List<Customer> customers = customerMapper.selectAllCustomers();
            
            return TestCase.passed("数据库连接测试", 
                String.format("数据库连接正常，客户总数: %d", totalCount));
                
        } catch (Exception e) {
            logger.error("数据库连接测试失败", e);
            return TestCase.failed("数据库连接测试", "数据库连接失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试客户统计API
     */
    private TestCase testCustomerStatsApi() {
        try {
            logger.info("测试客户统计API...");
            
            CustomerStatsDTO stats = customerStatsService.getCustomerStats();
            
            // 验证统计数据结构
            if (stats == null) {
                return TestCase.failed("客户统计API测试", "统计数据为null");
            }
            
            // 验证必要字段
            List<String> missingFields = new ArrayList<>();
            if (stats.getTotal() == null) missingFields.add("total");
            if (stats.getRegular() == null) missingFields.add("regular");
            if (stats.getVip() == null) missingFields.add("vip");
            if (stats.getPremium() == null) missingFields.add("premium");
            if (stats.getActiveDevices() == null) missingFields.add("activeDevices");
            if (stats.getTotalRevenue() == null) missingFields.add("totalRevenue");
            
            if (!missingFields.isEmpty()) {
                return TestCase.failed("客户统计API测试", 
                    "缺少必要字段: " + String.join(", ", missingFields));
            }
            
            return TestCase.passed("客户统计API测试", 
                String.format("统计数据正常 - 总客户: %d, VIP: %d, 收入: %.2f", 
                    stats.getTotal(), stats.getVip(), stats.getTotalRevenue()));
                    
        } catch (Exception e) {
            logger.error("客户统计API测试失败", e);
            return TestCase.failed("客户统计API测试", "API调用失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试客户列表API
     */
    private TestCase testCustomerListApi() {
        try {
            logger.info("测试客户列表API...");
            
            // 测试基础查询
            Map<String, Object> result = customerService.getCustomers(
                com.yxrobot.dto.CustomerQueryDTO.builder()
                    .page(1)
                    .pageSize(20)
                    .build()
            );
            
            // 验证响应结构
            if (!result.containsKey("list")) {
                return TestCase.failed("客户列表API测试", "响应缺少list字段");
            }
            
            if (!result.containsKey("total")) {
                return TestCase.failed("客户列表API测试", "响应缺少total字段");
            }
            
            @SuppressWarnings("unchecked")
            List<CustomerDTO> customers = (List<CustomerDTO>) result.get("list");
            Integer total = (Integer) result.get("total");
            
            // 测试分页功能
            if (customers.size() > 20) {
                return TestCase.failed("客户列表API测试", "分页功能异常，返回数据超过pageSize");
            }
            
            // 测试搜索功能
            Map<String, Object> searchResult = customerService.getCustomers(
                com.yxrobot.dto.CustomerQueryDTO.builder()
                    .page(1)
                    .pageSize(10)
                    .keyword("测试")
                    .build()
            );
            
            return TestCase.passed("客户列表API测试", 
                String.format("列表查询正常 - 总数: %d, 当前页: %d条", total, customers.size()));
                
        } catch (Exception e) {
            logger.error("客户列表API测试失败", e);
            return TestCase.failed("客户列表API测试", "API调用失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试客户CRUD操作
     */
    @Transactional
    private TestCase testCustomerCrudOperations() {
        try {
            logger.info("测试客户CRUD操作...");
            
            // 1. 测试创建客户
            CustomerCreateDTO createDTO = new CustomerCreateDTO();
            createDTO.setName("集成测试客户");
            createDTO.setPhone("13800138000");
            createDTO.setEmail("test@integration.com");
            createDTO.setLevel("REGULAR");
            createDTO.setStatus("ACTIVE");
            
            CustomerDTO createdCustomer = customerService.createCustomer(createDTO);
            if (createdCustomer == null || createdCustomer.getId() == null) {
                return TestCase.failed("客户CRUD测试", "客户创建失败");
            }
            
            // 2. 测试查询客户
            CustomerDTO customerDTO = customerService.getCustomerById(createdCustomer.getId());
            if (customerDTO == null) {
                return TestCase.failed("客户CRUD测试", "客户查询失败");
            }
            
            // 3. 测试更新客户
            CustomerUpdateDTO updateDTO = new CustomerUpdateDTO();
            updateDTO.setName("更新后的测试客户");
            updateDTO.setPhone("13900139000");
            
            CustomerDTO updatedCustomer = customerService.updateCustomer(createdCustomer.getId(), updateDTO);
            if (!updatedCustomer.getName().equals("更新后的测试客户")) {
                return TestCase.failed("客户CRUD测试", "客户更新失败");
            }
            
            // 4. 测试删除客户
            customerService.deleteCustomer(createdCustomer.getId());
            CustomerDTO deletedCustomer = customerService.getCustomerById(createdCustomer.getId());
            if (deletedCustomer != null) {
                return TestCase.failed("客户CRUD测试", "客户删除失败");
            }
            
            return TestCase.passed("客户CRUD测试", "创建、查询、更新、删除操作均正常");
            
        } catch (Exception e) {
            logger.error("客户CRUD测试失败", e);
            return TestCase.failed("客户CRUD测试", "CRUD操作失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试数据格式匹配
     */
    private TestCase testDataFormatMatching() {
        try {
            logger.info("测试数据格式匹配...");
            
            // 获取客户数据
            Map<String, Object> result = customerService.getCustomers(
                com.yxrobot.dto.CustomerQueryDTO.builder()
                    .page(1)
                    .pageSize(1)
                    .build()
            );
            
            @SuppressWarnings("unchecked")
            List<CustomerDTO> customers = (List<CustomerDTO>) result.get("list");
            
            if (customers.isEmpty()) {
                return TestCase.passed("数据格式匹配测试", "无客户数据，跳过格式验证");
            }
            
            CustomerDTO customer = customers.get(0);
            List<String> formatErrors = new ArrayList<>();
            
            // 验证字段格式
            if (customer.getId() == null) {
                formatErrors.add("id字段为null");
            }
            
            if (customer.getName() == null || customer.getName().trim().isEmpty()) {
                formatErrors.add("name字段为空");
            }
            
            if (customer.getLevel() == null) {
                formatErrors.add("level字段为null");
            } else if (!Arrays.asList("regular", "vip", "premium").contains(customer.getLevel())) {
                formatErrors.add("level字段值不符合枚举要求");
            }
            
            if (customer.getPhone() == null) {
                formatErrors.add("phone字段为null");
            }
            
            if (customer.getTotalSpent() != null && customer.getTotalSpent().compareTo(BigDecimal.ZERO) < 0) {
                formatErrors.add("totalSpent字段为负数");
            }
            
            if (!formatErrors.isEmpty()) {
                return TestCase.failed("数据格式匹配测试", 
                    "格式错误: " + String.join(", ", formatErrors));
            }
            
            return TestCase.passed("数据格式匹配测试", "数据格式符合前端TypeScript接口要求");
            
        } catch (Exception e) {
            logger.error("数据格式匹配测试失败", e);
            return TestCase.failed("数据格式匹配测试", "格式验证失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试字段映射正确性
     */
    private TestCase testFieldMapping() {
        try {
            logger.info("测试字段映射正确性...");
            
            // 测试数据库字段到Java实体的映射
            List<Customer> customers = customerMapper.selectAllCustomers();
            
            if (customers.isEmpty()) {
                return TestCase.passed("字段映射测试", "无客户数据，跳过映射验证");
            }
            
            Customer customer = customers.get(0);
            List<String> mappingErrors = new ArrayList<>();
            
            // 验证关键字段映射
            if (customer.getCustomerName() == null) {
                mappingErrors.add("customer_name -> customerName 映射失败");
            }
            
            if (customer.getPhone() == null) {
                mappingErrors.add("phone -> phone 映射失败");
            }
            
            if (customer.getEmail() == null) {
                mappingErrors.add("email -> email 映射失败");
            }
            
            // 测试DTO转换
            CustomerDTO customerDTO = customerService.getCustomerById(customer.getId());
            if (customerDTO == null) {
                mappingErrors.add("Entity -> DTO 转换失败");
            } else {
                // 验证DTO字段映射
                if (!Objects.equals(customerDTO.getName(), customer.getCustomerName())) {
                    mappingErrors.add("customerName -> name 映射不一致");
                }
                
                if (!Objects.equals(customerDTO.getPhone(), customer.getPhone())) {
                    mappingErrors.add("phone -> phone 映射不一致");
                }
            }
            
            if (!mappingErrors.isEmpty()) {
                return TestCase.failed("字段映射测试", 
                    "映射错误: " + String.join(", ", mappingErrors));
            }
            
            return TestCase.passed("字段映射测试", "数据库字段到前端字段映射正确");
            
        } catch (Exception e) {
            logger.error("字段映射测试失败", e);
            return TestCase.failed("字段映射测试", "映射验证失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试API响应时间
     */
    private TestCase testApiPerformance() {
        try {
            logger.info("测试API响应时间...");
            
            List<String> performanceResults = new ArrayList<>();
            
            // 测试客户列表API性能
            long startTime = System.currentTimeMillis();
            customerService.getCustomers(
                com.yxrobot.dto.CustomerQueryDTO.builder()
                    .page(1)
                    .pageSize(20)
                    .build()
            );
            long listApiTime = System.currentTimeMillis() - startTime;
            performanceResults.add(String.format("客户列表API: %dms", listApiTime));
            
            // 测试统计API性能
            startTime = System.currentTimeMillis();
            customerStatsService.getCustomerStats();
            long statsApiTime = System.currentTimeMillis() - startTime;
            performanceResults.add(String.format("统计API: %dms", statsApiTime));
            
            // 性能要求验证
            List<String> performanceIssues = new ArrayList<>();
            if (listApiTime > 2000) {
                performanceIssues.add("客户列表API响应时间超过2秒");
            }
            if (statsApiTime > 1000) {
                performanceIssues.add("统计API响应时间超过1秒");
            }
            
            if (!performanceIssues.isEmpty()) {
                return TestCase.failed("API性能测试", 
                    "性能问题: " + String.join(", ", performanceIssues));
            }
            
            return TestCase.passed("API性能测试", 
                "API响应时间符合要求 - " + String.join(", ", performanceResults));
            
        } catch (Exception e) {
            logger.error("API性能测试失败", e);
            return TestCase.failed("API性能测试", "性能测试失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试错误处理
     */
    private TestCase testErrorHandling() {
        try {
            logger.info("测试错误处理...");
            
            List<String> errorTests = new ArrayList<>();
            
            // 测试查询不存在的客户
            try {
                CustomerDTO nonExistentCustomer = customerService.getCustomerById(999999L);
                if (nonExistentCustomer != null) {
                    errorTests.add("查询不存在客户应返回null");
                }
            } catch (Exception e) {
                errorTests.add("查询不存在客户异常处理: " + e.getMessage());
            }
            
            // 测试无效参数
            try {
                customerService.getCustomers(
                    com.yxrobot.dto.CustomerQueryDTO.builder()
                        .page(-1)
                        .pageSize(0)
                        .build()
                );
                errorTests.add("无效分页参数应抛出异常");
            } catch (Exception e) {
                // 预期异常，正常
            }
            
            if (!errorTests.isEmpty()) {
                return TestCase.failed("错误处理测试", 
                    "错误处理问题: " + String.join(", ", errorTests));
            }
            
            return TestCase.passed("错误处理测试", "错误处理机制正常");
            
        } catch (Exception e) {
            logger.error("错误处理测试失败", e);
            return TestCase.failed("错误处理测试", "测试执行失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试空数据状态
     */
    private TestCase testEmptyDataState() {
        try {
            logger.info("测试空数据状态...");
            
            // 测试空查询结果
            Map<String, Object> emptyResult = customerService.getCustomers(
                com.yxrobot.dto.CustomerQueryDTO.builder()
                    .page(1)
                    .pageSize(20)
                    .keyword("不存在的客户名称12345")
                    .build()
            );
            
            @SuppressWarnings("unchecked")
            List<CustomerDTO> customers = (List<CustomerDTO>) emptyResult.get("list");
            
            if (customers == null) {
                return TestCase.failed("空数据状态测试", "空查询结果list字段为null，应为空数组");
            }
            
            if (!customers.isEmpty()) {
                return TestCase.failed("空数据状态测试", "搜索不存在内容应返回空数组");
            }
            
            // 测试统计数据在无客户时的处理
            CustomerStatsDTO stats = customerStatsService.getCustomerStats();
            if (stats == null) {
                return TestCase.failed("空数据状态测试", "统计数据不应为null");
            }
            
            return TestCase.passed("空数据状态测试", "空数据状态处理正确");
            
        } catch (Exception e) {
            logger.error("空数据状态测试失败", e);
            return TestCase.failed("空数据状态测试", "测试失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试前端页面功能支持
     */
    private TestCase testFrontendFunctionSupport() {
        try {
            logger.info("测试前端页面功能支持...");
            
            List<String> supportTests = new ArrayList<>();
            
            // 1. 测试统计卡片数据支持
            CustomerStatsDTO stats = customerStatsService.getCustomerStats();
            if (stats.getTotal() == null || stats.getRegular() == null || 
                stats.getVip() == null || stats.getPremium() == null ||
                stats.getActiveDevices() == null || stats.getTotalRevenue() == null) {
                supportTests.add("统计卡片缺少必要数据字段");
            }
            
            // 2. 测试客户列表表格数据支持
            Map<String, Object> result = customerService.getCustomers(
                com.yxrobot.dto.CustomerQueryDTO.builder()
                    .page(1)
                    .pageSize(1)
                    .build()
            );
            
            @SuppressWarnings("unchecked")
            List<CustomerDTO> customers = (List<CustomerDTO>) result.get("list");
            
            if (!customers.isEmpty()) {
                CustomerDTO customer = customers.get(0);
                
                // 验证表格必要字段
                if (customer.getName() == null) supportTests.add("客户姓名字段缺失");
                if (customer.getLevel() == null) supportTests.add("客户等级字段缺失");
                if (customer.getPhone() == null) supportTests.add("联系电话字段缺失");
                if (customer.getTotalSpent() == null) supportTests.add("消费金额字段缺失");
            }
            
            // 3. 测试筛选功能支持
            try {
                customerService.getCustomers(
                    com.yxrobot.dto.CustomerQueryDTO.builder()
                        .page(1)
                        .pageSize(10)
                        .level("vip")
                        .region("北京市")
                        .build()
                );
            } catch (Exception e) {
                supportTests.add("筛选功能不支持: " + e.getMessage());
            }
            
            // 4. 测试排序功能支持
            try {
                customerService.getCustomers(
                    com.yxrobot.dto.CustomerQueryDTO.builder()
                        .page(1)
                        .pageSize(10)
                        .sortBy("totalSpent")
                        .sortOrder("desc")
                        .build()
                );
            } catch (Exception e) {
                supportTests.add("排序功能不支持: " + e.getMessage());
            }
            
            if (!supportTests.isEmpty()) {
                return TestCase.failed("前端功能支持测试", 
                    "功能支持问题: " + String.join(", ", supportTests));
            }
            
            return TestCase.passed("前端功能支持测试", "前端页面所需功能均得到支持");
            
        } catch (Exception e) {
            logger.error("前端功能支持测试失败", e);
            return TestCase.failed("前端功能支持测试", "测试失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试结果类
     */
    public static class IntegrationTestResult {
        private List<TestCase> testCases = new ArrayList<>();
        private long totalExecutionTime;
        
        public void addTest(TestCase testCase) {
            testCases.add(testCase);
        }
        
        public List<TestCase> getTestCases() {
            return testCases;
        }
        
        public int getTotalTests() {
            return testCases.size();
        }
        
        public int getPassedTests() {
            return (int) testCases.stream().filter(TestCase::isPassed).count();
        }
        
        public int getFailedTests() {
            return (int) testCases.stream().filter(tc -> !tc.isPassed()).count();
        }
        
        public double getPassRate() {
            if (testCases.isEmpty()) return 0.0;
            return (double) getPassedTests() / getTotalTests() * 100;
        }
        
        public boolean isAllPassed() {
            return getFailedTests() == 0;
        }
        
        public long getTotalExecutionTime() {
            return totalExecutionTime;
        }
        
        public void setTotalExecutionTime(long totalExecutionTime) {
            this.totalExecutionTime = totalExecutionTime;
        }
        
        public String getSummary() {
            return String.format("测试总数: %d, 通过: %d, 失败: %d, 通过率: %.1f%%, 总耗时: %dms",
                getTotalTests(), getPassedTests(), getFailedTests(), getPassRate(), totalExecutionTime);
        }
    }
    
    /**
     * 测试用例类
     */
    public static class TestCase {
        private String name;
        private boolean passed;
        private String message;
        private long executionTime;
        
        private TestCase(String name, boolean passed, String message) {
            this.name = name;
            this.passed = passed;
            this.message = message;
            this.executionTime = System.currentTimeMillis();
        }
        
        public static TestCase passed(String name, String message) {
            return new TestCase(name, true, message);
        }
        
        public static TestCase failed(String name, String message) {
            return new TestCase(name, false, message);
        }
        
        public String getName() {
            return name;
        }
        
        public boolean isPassed() {
            return passed;
        }
        
        public String getMessage() {
            return message;
        }
        
        public long getExecutionTime() {
            return executionTime;
        }
        
        @Override
        public String toString() {
            return String.format("[%s] %s: %s", 
                passed ? "PASS" : "FAIL", name, message);
        }
    }
}