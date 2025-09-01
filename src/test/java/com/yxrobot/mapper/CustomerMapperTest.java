package com.yxrobot.mapper;

import com.yxrobot.dto.CustomerDTO;
import com.yxrobot.dto.CustomerQueryDTO;
import com.yxrobot.dto.CustomerStatsDTO;
import com.yxrobot.entity.Customer;
import com.yxrobot.entity.CustomerLevel;
import com.yxrobot.entity.CustomerStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CustomerMapper单元测试类
 * 测试数据访问层的SQL映射和查询功能
 */
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
public class CustomerMapperTest {
    
    @Autowired
    private CustomerMapper customerMapper;
    
    private Customer testCustomer;
    private CustomerQueryDTO testQueryDTO;
    
    @BeforeEach
    void setUp() {
        setupTestData();
        insertTestCustomer();
    }
    
    private void setupTestData() {
        testCustomer = new Customer();
        testCustomer.setCustomerName("测试客户");
        testCustomer.setPhone("13800138001");
        testCustomer.setEmail("test@example.com");
        testCustomer.setContactPerson("张三");
        testCustomer.setCustomerLevel(CustomerLevel.VIP);
        testCustomer.setCustomerStatus(CustomerStatus.ACTIVE);
        testCustomer.setRegion("北京-朝阳区");
        testCustomer.setIndustry("科技");
        testCustomer.setTotalSpent(new BigDecimal("50000.00"));
        testCustomer.setCustomerValue(new BigDecimal("8.5"));
        testCustomer.setRegisteredAt(LocalDateTime.now().minusMonths(6));
        testCustomer.setLastActiveAt(LocalDateTime.now().minusDays(1));
        testCustomer.setIsActive(true);
        testCustomer.setIsDeleted(false);
        
        testQueryDTO = new CustomerQueryDTO();
        testQueryDTO.setPage(1);
        testQueryDTO.setPageSize(20);
    }
    
    private void insertTestCustomer() {
        customerMapper.insert(testCustomer);
        assertNotNull(testCustomer.getId());
    }
    
    // ==================== 基础CRUD操作测试 ====================
    
    @Test
    void testInsert_Success() {
        // Given
        Customer newCustomer = new Customer();
        newCustomer.setCustomerName("新客户");
        newCustomer.setPhone("13900139001");
        newCustomer.setEmail("newcustomer@example.com");
        newCustomer.setCustomerLevel(CustomerLevel.REGULAR);
        newCustomer.setCustomerStatus(CustomerStatus.ACTIVE);
        newCustomer.setTotalSpent(BigDecimal.ZERO);
        newCustomer.setCustomerValue(BigDecimal.ZERO);
        newCustomer.setRegisteredAt(LocalDateTime.now());
        newCustomer.setIsActive(true);
        newCustomer.setIsDeleted(false);
        
        // When
        int result = customerMapper.insert(newCustomer);
        
        // Then
        assertEquals(1, result);
        assertNotNull(newCustomer.getId());
        assertTrue(newCustomer.getId() > 0);
    }
    
    @Test
    void testSelectById_Success() {
        // When
        Customer result = customerMapper.selectById(testCustomer.getId());
        
        // Then
        assertNotNull(result);
        assertEquals(testCustomer.getCustomerName(), result.getCustomerName());
        assertEquals(testCustomer.getPhone(), result.getPhone());
        assertEquals(testCustomer.getEmail(), result.getEmail());
        assertEquals(testCustomer.getCustomerLevel(), result.getCustomerLevel());
    }
    
    @Test
    void testSelectById_NotFound() {
        // When
        Customer result = customerMapper.selectById(99999L);
        
        // Then
        assertNull(result);
    }
    
    @Test
    void testSelectDTOById_Success() {
        // When
        CustomerDTO result = customerMapper.selectDTOById(testCustomer.getId());
        
        // Then
        assertNotNull(result);
        assertEquals(testCustomer.getCustomerName(), result.getCustomerName());
        assertEquals(testCustomer.getPhone(), result.getPhone());
        assertEquals(testCustomer.getEmail(), result.getEmail());
        assertEquals(testCustomer.getCustomerLevel().getCode(), result.getCustomerLevel());
    }
    
    @Test
    void testUpdateById_Success() {
        // Given
        testCustomer.setCustomerName("更新后的客户");
        testCustomer.setPhone("13900139002");
        testCustomer.setTotalSpent(new BigDecimal("60000.00"));
        
        // When
        int result = customerMapper.updateById(testCustomer);
        
        // Then
        assertEquals(1, result);
        
        Customer updated = customerMapper.selectById(testCustomer.getId());
        assertEquals("更新后的客户", updated.getCustomerName());
        assertEquals("13900139002", updated.getPhone());
        assertEquals(0, new BigDecimal("60000.00").compareTo(updated.getTotalSpent()));
    }
    
    @Test
    void testSoftDeleteById_Success() {
        // When
        int result = customerMapper.softDeleteById(testCustomer.getId());
        
        // Then
        assertEquals(1, result);
        
        Customer deleted = customerMapper.selectById(testCustomer.getId());
        assertNull(deleted); // 软删除后查询不到
    }
    
    @Test
    void testSoftDeleteByIds_Success() {
        // Given
        Customer customer2 = new Customer();
        customer2.setCustomerName("客户2");
        customer2.setPhone("13900139003");
        customer2.setEmail("customer2@example.com");
        customer2.setCustomerLevel(CustomerLevel.REGULAR);
        customer2.setCustomerStatus(CustomerStatus.ACTIVE);
        customer2.setTotalSpent(BigDecimal.ZERO);
        customer2.setCustomerValue(BigDecimal.ZERO);
        customer2.setRegisteredAt(LocalDateTime.now());
        customer2.setIsActive(true);
        customer2.setIsDeleted(false);
        customerMapper.insert(customer2);
        
        List<Long> ids = Arrays.asList(testCustomer.getId(), customer2.getId());
        
        // When
        int result = customerMapper.softDeleteByIds(ids);
        
        // Then
        assertEquals(2, result);
        assertNull(customerMapper.selectById(testCustomer.getId()));
        assertNull(customerMapper.selectById(customer2.getId()));
    }    /
/ ==================== 查询功能测试 ====================
    
    @Test
    void testSelectList_Success() {
        // When
        List<Customer> result = customerMapper.selectList(testQueryDTO);
        
        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(c -> c.getCustomerName().equals("测试客户")));
    }
    
    @Test
    void testSelectDTOList_Success() {
        // When
        List<CustomerDTO> result = customerMapper.selectDTOList(testQueryDTO);
        
        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(c -> c.getCustomerName().equals("测试客户")));
    }
    
    @Test
    void testSelectCount_Success() {
        // When
        Long count = customerMapper.selectCount(testQueryDTO);
        
        // Then
        assertNotNull(count);
        assertTrue(count >= 1);
    }
    
    @Test
    void testSelectList_WithKeywordSearch() {
        // Given
        testQueryDTO.setKeyword("测试");
        
        // When
        List<Customer> result = customerMapper.selectList(testQueryDTO);
        
        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(c -> c.getCustomerName().contains("测试")));
    }
    
    @Test
    void testSelectList_WithLevelFilter() {
        // Given
        testQueryDTO.setCustomerLevel("vip");
        
        // When
        List<Customer> result = customerMapper.selectList(testQueryDTO);
        
        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(c -> c.getCustomerLevel() == CustomerLevel.VIP));
    }
    
    @Test
    void testSelectList_WithRegionFilter() {
        // Given
        testQueryDTO.setRegion("北京");
        
        // When
        List<Customer> result = customerMapper.selectList(testQueryDTO);
        
        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(c -> c.getRegion().contains("北京")));
    }
    
    @Test
    void testSelectList_WithPagination() {
        // Given
        testQueryDTO.setPage(1);
        testQueryDTO.setPageSize(5);
        
        // When
        List<Customer> result = customerMapper.selectList(testQueryDTO);
        
        // Then
        assertNotNull(result);
        assertTrue(result.size() <= 5);
    }
    
    @Test
    void testSelectList_WithSorting() {
        // Given
        testQueryDTO.setSortBy("customerName");
        testQueryDTO.setSortOrder("ASC");
        
        // When
        List<Customer> result = customerMapper.selectList(testQueryDTO);
        
        // Then
        assertNotNull(result);
        if (result.size() > 1) {
            for (int i = 0; i < result.size() - 1; i++) {
                assertTrue(result.get(i).getCustomerName()
                    .compareTo(result.get(i + 1).getCustomerName()) <= 0);
            }
        }
    }
    
    // ==================== 统计功能测试 ====================
    
    @Test
    void testSelectCustomerStats_Success() {
        // When
        CustomerStatsDTO stats = customerMapper.selectCustomerStats();
        
        // Then
        assertNotNull(stats);
        assertTrue(stats.getTotal() >= 1);
        assertTrue(stats.getVip() >= 1); // 我们插入了一个VIP客户
        assertNotNull(stats.getTotalRevenue());
    }
    
    @Test
    void testSelectCustomerLevelStats_Success() {
        // When
        List<Map<String, Object>> stats = customerMapper.selectCustomerLevelStats();
        
        // Then
        assertNotNull(stats);
        assertFalse(stats.isEmpty());
        assertTrue(stats.stream().anyMatch(s -> "vip".equals(s.get("level"))));
    }
    
    @Test
    void testSelectNewCustomersThisMonth_Success() {
        // When
        Integer count = customerMapper.selectNewCustomersThisMonth();
        
        // Then
        assertNotNull(count);
        assertTrue(count >= 1); // 我们插入的客户是最近注册的
    }
    
    @Test
    void testSelectTotalRevenue_Success() {
        // When
        BigDecimal revenue = customerMapper.selectTotalRevenue();
        
        // Then
        assertNotNull(revenue);
        assertTrue(revenue.compareTo(BigDecimal.ZERO) >= 0);
    }
    
    // ==================== 搜索功能测试 ====================
    
    @Test
    void testSearchCustomers_Success() {
        // When
        List<CustomerDTO> result = customerMapper.searchCustomers("测试", 10);
        
        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(c -> c.getCustomerName().contains("测试")));
    }
    
    @Test
    void testSearchCustomers_ByPhone() {
        // When
        List<CustomerDTO> result = customerMapper.searchCustomers("13800138001", 10);
        
        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(c -> c.getPhone().contains("13800138001")));
    }
    
    @Test
    void testSearchCustomers_ByEmail() {
        // When
        List<CustomerDTO> result = customerMapper.searchCustomers("test@example.com", 10);
        
        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(c -> c.getEmail().contains("test@example.com")));
    }
    
    @Test
    void testSelectByCustomerLevel_Success() {
        // When
        List<CustomerDTO> result = customerMapper.selectByCustomerLevel("vip", testQueryDTO);
        
        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(c -> "vip".equals(c.getCustomerLevel())));
    }
    
    @Test
    void testSelectByRegion_Success() {
        // When
        List<CustomerDTO> result = customerMapper.selectByRegion("北京", testQueryDTO);
        
        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(c -> c.getRegion().contains("北京")));
    }   
 // ==================== 数据验证功能测试 ====================
    
    @Test
    void testExistsByCustomerName_True() {
        // When
        boolean exists = customerMapper.existsByCustomerName("测试客户");
        
        // Then
        assertTrue(exists);
    }
    
    @Test
    void testExistsByCustomerName_False() {
        // When
        boolean exists = customerMapper.existsByCustomerName("不存在的客户");
        
        // Then
        assertFalse(exists);
    }
    
    @Test
    void testExistsByPhone_True() {
        // When
        boolean exists = customerMapper.existsByPhone("13800138001");
        
        // Then
        assertTrue(exists);
    }
    
    @Test
    void testExistsByPhone_False() {
        // When
        boolean exists = customerMapper.existsByPhone("19999999999");
        
        // Then
        assertFalse(exists);
    }
    
    @Test
    void testExistsByEmail_True() {
        // When
        boolean exists = customerMapper.existsByEmail("test@example.com");
        
        // Then
        assertTrue(exists);
    }
    
    @Test
    void testExistsByEmail_False() {
        // When
        boolean exists = customerMapper.existsByEmail("notexist@example.com");
        
        // Then
        assertFalse(exists);
    }
    
    @Test
    void testExistsByCustomerNameExcludeId_True() {
        // Given - 创建另一个客户
        Customer anotherCustomer = new Customer();
        anotherCustomer.setCustomerName("另一个客户");
        anotherCustomer.setPhone("13900139004");
        anotherCustomer.setEmail("another@example.com");
        anotherCustomer.setCustomerLevel(CustomerLevel.REGULAR);
        anotherCustomer.setCustomerStatus(CustomerStatus.ACTIVE);
        anotherCustomer.setTotalSpent(BigDecimal.ZERO);
        anotherCustomer.setCustomerValue(BigDecimal.ZERO);
        anotherCustomer.setRegisteredAt(LocalDateTime.now());
        anotherCustomer.setIsActive(true);
        anotherCustomer.setIsDeleted(false);
        customerMapper.insert(anotherCustomer);
        
        // When - 检查是否存在同名客户（排除当前客户）
        boolean exists = customerMapper.existsByCustomerNameExcludeId("另一个客户", testCustomer.getId());
        
        // Then
        assertTrue(exists);
    }
    
    @Test
    void testExistsByCustomerNameExcludeId_False() {
        // When - 检查自己的名字（排除自己）
        boolean exists = customerMapper.existsByCustomerNameExcludeId("测试客户", testCustomer.getId());
        
        // Then
        assertFalse(exists);
    }
    
    @Test
    void testSelectByPhone_Success() {
        // When
        Customer result = customerMapper.selectByPhone("13800138001");
        
        // Then
        assertNotNull(result);
        assertEquals("测试客户", result.getCustomerName());
        assertEquals("13800138001", result.getPhone());
    }
    
    @Test
    void testSelectByEmail_Success() {
        // When
        Customer result = customerMapper.selectByEmail("test@example.com");
        
        // Then
        assertNotNull(result);
        assertEquals("测试客户", result.getCustomerName());
        assertEquals("test@example.com", result.getEmail());
    }
    
    // ==================== 高级查询功能测试 ====================
    
    @Test
    void testSelectHighValueCustomers_Success() {
        // When
        List<CustomerDTO> result = customerMapper.selectHighValueCustomers(new BigDecimal("8.0"), 10);
        
        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(c -> c.getCustomerValue().compareTo(new BigDecimal("8.0")) >= 0));
    }
    
    @Test
    void testSelectActiveCustomers_Success() {
        // When
        List<CustomerDTO> result = customerMapper.selectActiveCustomers(30, 10);
        
        // Then
        assertNotNull(result);
        // 我们的测试客户最后活跃时间是1天前，应该在30天内
        assertFalse(result.isEmpty());
    }
    
    @Test
    void testSelectNewCustomers_Success() {
        // When
        List<CustomerDTO> result = customerMapper.selectNewCustomers(365, 10);
        
        // Then
        assertNotNull(result);
        // 我们的测试客户注册时间是6个月前，应该在365天内
        assertFalse(result.isEmpty());
    }
    
    // ==================== 筛选选项数据测试 ====================
    
    @Test
    void testGetCustomerLevelOptions_Success() {
        // When
        List<Map<String, Object>> options = customerMapper.getCustomerLevelOptions();
        
        // Then
        assertNotNull(options);
        assertFalse(options.isEmpty());
        assertTrue(options.stream().anyMatch(o -> "vip".equals(o.get("value"))));
    }
    
    @Test
    void testGetRegionOptions_Success() {
        // When
        List<Map<String, Object>> options = customerMapper.getRegionOptions();
        
        // Then
        assertNotNull(options);
        assertFalse(options.isEmpty());
        assertTrue(options.stream().anyMatch(o -> o.get("value").toString().contains("北京")));
    }
    
    // ==================== 客户价值和等级管理测试 ====================
    
    @Test
    void testUpdateCustomerValue_Success() {
        // Given
        BigDecimal newValue = new BigDecimal("9.0");
        
        // When
        int result = customerMapper.updateCustomerValue(testCustomer.getId(), newValue);
        
        // Then
        assertEquals(1, result);
        
        Customer updated = customerMapper.selectById(testCustomer.getId());
        assertEquals(0, newValue.compareTo(updated.getCustomerValue()));
    }
    
    @Test
    void testUpdateCustomerLevel_Success() {
        // Given
        String newLevel = "premium";
        
        // When
        int result = customerMapper.updateCustomerLevel(testCustomer.getId(), newLevel);
        
        // Then
        assertEquals(1, result);
        
        Customer updated = customerMapper.selectById(testCustomer.getId());
        assertEquals(CustomerLevel.PREMIUM, updated.getCustomerLevel());
    }
    
    @Test
    void testUpdateTotalSpent_Success() {
        // Given
        BigDecimal newSpent = new BigDecimal("75000.00");
        
        // When
        int result = customerMapper.updateTotalSpent(testCustomer.getId(), newSpent);
        
        // Then
        assertEquals(1, result);
        
        Customer updated = customerMapper.selectById(testCustomer.getId());
        assertEquals(0, newSpent.compareTo(updated.getTotalSpent()));
    }
    
    @Test
    void testUpdateLastActiveAt_Success() {
        // Given
        LocalDateTime newActiveTime = LocalDateTime.now();
        
        // When
        int result = customerMapper.updateLastActiveAt(testCustomer.getId(), newActiveTime);
        
        // Then
        assertEquals(1, result);
        
        Customer updated = customerMapper.selectById(testCustomer.getId());
        assertNotNull(updated.getLastActiveAt());
    }
    
    @Test
    void testBatchUpdateCustomerLevel_Success() {
        // Given
        Customer customer2 = new Customer();
        customer2.setCustomerName("客户2");
        customer2.setPhone("13900139005");
        customer2.setEmail("customer2@example.com");
        customer2.setCustomerLevel(CustomerLevel.REGULAR);
        customer2.setCustomerStatus(CustomerStatus.ACTIVE);
        customer2.setTotalSpent(BigDecimal.ZERO);
        customer2.setCustomerValue(BigDecimal.ZERO);
        customer2.setRegisteredAt(LocalDateTime.now());
        customer2.setIsActive(true);
        customer2.setIsDeleted(false);
        customerMapper.insert(customer2);
        
        List<Long> customerIds = Arrays.asList(testCustomer.getId(), customer2.getId());
        
        // When
        int result = customerMapper.batchUpdateCustomerLevel(customerIds, "premium");
        
        // Then
        assertEquals(2, result);
        
        Customer updated1 = customerMapper.selectById(testCustomer.getId());
        Customer updated2 = customerMapper.selectById(customer2.getId());
        assertEquals(CustomerLevel.PREMIUM, updated1.getCustomerLevel());
        assertEquals(CustomerLevel.PREMIUM, updated2.getCustomerLevel());
    }
}