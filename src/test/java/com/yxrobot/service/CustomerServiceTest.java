package com.yxrobot.service;

import com.yxrobot.dto.*;
import com.yxrobot.entity.Customer;
import com.yxrobot.entity.CustomerLevel;
import com.yxrobot.entity.CustomerStatus;
import com.yxrobot.mapper.CustomerMapper;
import com.yxrobot.mapper.CustomerAddressMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * CustomerService单元测试类
 * 测试客户服务层的业务逻辑
 */
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class CustomerServiceTest {
    
    @Mock
    private CustomerMapper customerMapper;
    
    @Mock
    private CustomerAddressMapper customerAddressMapper;
    
    @InjectMocks
    private CustomerService customerService;
    
    private Customer testCustomer;
    private CustomerDTO testCustomerDTO;
    private CustomerQueryDTO testQueryDTO;
    private CustomerCreateDTO testCreateDTO;
    private CustomerUpdateDTO testUpdateDTO;
    
    @BeforeEach
    void setUp() {
        // 初始化测试数据
        setupTestData();
    }
    
    private void setupTestData() {
        // 创建测试客户实体
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setCustomerName("测试客户");
        testCustomer.setPhone("13800138001");
        testCustomer.setEmail("test@example.com");
        testCustomer.setCustomerLevel(CustomerLevel.VIP);
        testCustomer.setCustomerStatus(CustomerStatus.ACTIVE);
        testCustomer.setTotalSpent(new BigDecimal("50000.00"));
        testCustomer.setCustomerValue(new BigDecimal("8.5"));
        testCustomer.setRegisteredAt(LocalDateTime.now().minusMonths(6));
        testCustomer.setLastActiveAt(LocalDateTime.now().minusDays(1));
        testCustomer.setIsActive(true);
        testCustomer.setIsDeleted(false);
    }
}    // ==
================== 客户查询功能测试 ====================
    
    @Test
    void testGetCustomers_Success() {
        // Given
        testQueryDTO = new CustomerQueryDTO();
        testQueryDTO.setPage(1);
        testQueryDTO.setPageSize(20);
        
        List<CustomerDTO> mockCustomers = Arrays.asList(testCustomerDTO);
        when(customerMapper.selectDTOList(any(CustomerQueryDTO.class))).thenReturn(mockCustomers);
        when(customerMapper.selectCount(any(CustomerQueryDTO.class))).thenReturn(1L);
        
        // When
        Map<String, Object> result = customerService.getCustomers(testQueryDTO);
        
        // Then
        assertNotNull(result);
        assertEquals(1L, result.get("total"));
        assertEquals(1, result.get("page"));
        assertEquals(20, result.get("pageSize"));
        assertFalse((Boolean) result.get("isEmpty"));
        
        @SuppressWarnings("unchecked")
        List<CustomerDTO> customers = (List<CustomerDTO>) result.get("list");
        assertEquals(1, customers.size());
        
        verify(customerMapper).selectDTOList(any(CustomerQueryDTO.class));
        verify(customerMapper).selectCount(any(CustomerQueryDTO.class));
    }
    
    @Test
    void testGetCustomers_EmptyResult() {
        // Given
        testQueryDTO = new CustomerQueryDTO();
        when(customerMapper.selectDTOList(any(CustomerQueryDTO.class))).thenReturn(Collections.emptyList());
        when(customerMapper.selectCount(any(CustomerQueryDTO.class))).thenReturn(0L);
        
        // When
        Map<String, Object> result = customerService.getCustomers(testQueryDTO);
        
        // Then
        assertNotNull(result);
        assertEquals(0L, result.get("total"));
        assertTrue((Boolean) result.get("isEmpty"));
        
        @SuppressWarnings("unchecked")
        List<CustomerDTO> customers = (List<CustomerDTO>) result.get("list");
        assertTrue(customers.isEmpty());
    }
    
    @Test
    void testGetCustomers_WithDefaultPagination() {
        // Given - null queryDTO should use defaults
        when(customerMapper.selectDTOList(any(CustomerQueryDTO.class))).thenReturn(Collections.emptyList());
        when(customerMapper.selectCount(any(CustomerQueryDTO.class))).thenReturn(0L);
        
        // When
        Map<String, Object> result = customerService.getCustomers(null);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.get("page"));
        assertEquals(20, result.get("pageSize"));
    }
    
    @Test
    void testGetCustomerById_Success() {
        // Given
        Long customerId = 1L;
        when(customerMapper.selectDTOById(customerId)).thenReturn(testCustomerDTO);
        
        // When
        CustomerDTO result = customerService.getCustomerById(customerId);
        
        // Then
        assertNotNull(result);
        assertEquals(testCustomerDTO, result);
        verify(customerMapper).selectDTOById(customerId);
    }
    
    @Test
    void testGetCustomerById_NotFound() {
        // Given
        Long customerId = 999L;
        when(customerMapper.selectDTOById(customerId)).thenReturn(null);
        
        // When
        CustomerDTO result = customerService.getCustomerById(customerId);
        
        // Then
        assertNull(result);
        verify(customerMapper).selectDTOById(customerId);
    }
    
    @Test
    void testGetCustomerById_InvalidId() {
        // When & Then
        assertNull(customerService.getCustomerById(null));
        assertNull(customerService.getCustomerById(0L));
        assertNull(customerService.getCustomerById(-1L));
        
        verify(customerMapper, never()).selectDTOById(any());
    }    //
 ==================== 客户CRUD操作测试 ====================
    
    @Test
    void testCreateCustomer_Success() {
        // Given
        testCreateDTO = new CustomerCreateDTO();
        testCreateDTO.setCustomerName("新客户");
        testCreateDTO.setPhone("13900139001");
        testCreateDTO.setEmail("newcustomer@example.com");
        
        when(customerMapper.existsByCustomerName(anyString())).thenReturn(false);
        when(customerMapper.existsByPhone(anyString())).thenReturn(false);
        when(customerMapper.existsByEmail(anyString())).thenReturn(false);
        when(customerMapper.insert(any(Customer.class))).thenReturn(1);
        when(customerMapper.selectDTOById(any())).thenReturn(testCustomerDTO);
        
        // When
        CustomerDTO result = customerService.createCustomer(testCreateDTO);
        
        // Then
        assertNotNull(result);
        verify(customerMapper).existsByCustomerName(testCreateDTO.getCustomerName());
        verify(customerMapper).existsByPhone(testCreateDTO.getPhone());
        verify(customerMapper).existsByEmail(testCreateDTO.getEmail());
        verify(customerMapper).insert(any(Customer.class));
    }
    
    @Test
    void testCreateCustomer_DuplicateName() {
        // Given
        testCreateDTO = new CustomerCreateDTO();
        testCreateDTO.setCustomerName("重复客户");
        testCreateDTO.setPhone("13900139002");
        testCreateDTO.setEmail("duplicate@example.com");
        
        when(customerMapper.existsByCustomerName(anyString())).thenReturn(true);
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            customerService.createCustomer(testCreateDTO);
        });
        
        assertTrue(exception.getMessage().contains("客户姓名已存在"));
        verify(customerMapper, never()).insert(any(Customer.class));
    }
    
    @Test
    void testCreateCustomer_InvalidPhone() {
        // Given
        testCreateDTO = new CustomerCreateDTO();
        testCreateDTO.setCustomerName("测试客户");
        testCreateDTO.setPhone("invalid_phone");
        testCreateDTO.setEmail("test@example.com");
        
        when(customerMapper.existsByCustomerName(anyString())).thenReturn(false);
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            customerService.createCustomer(testCreateDTO);
        });
        
        assertTrue(exception.getMessage().contains("电话号码格式不正确"));
    }
    
    @Test
    void testUpdateCustomer_Success() {
        // Given
        Long customerId = 1L;
        testUpdateDTO = new CustomerUpdateDTO();
        testUpdateDTO.setCustomerName("更新客户");
        testUpdateDTO.setPhone("13900139003");
        
        when(customerMapper.selectById(customerId)).thenReturn(testCustomer);
        when(customerMapper.existsByCustomerNameExcludeId(anyString(), eq(customerId))).thenReturn(false);
        when(customerMapper.existsByPhoneExcludeId(anyString(), eq(customerId))).thenReturn(false);
        when(customerMapper.updateById(any(Customer.class))).thenReturn(1);
        when(customerMapper.selectDTOById(customerId)).thenReturn(testCustomerDTO);
        
        // When
        CustomerDTO result = customerService.updateCustomer(customerId, testUpdateDTO);
        
        // Then
        assertNotNull(result);
        verify(customerMapper).selectById(customerId);
        verify(customerMapper).updateById(any(Customer.class));
    }
    
    @Test
    void testUpdateCustomer_NotFound() {
        // Given
        Long customerId = 999L;
        testUpdateDTO = new CustomerUpdateDTO();
        
        when(customerMapper.selectById(customerId)).thenReturn(null);
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            customerService.updateCustomer(customerId, testUpdateDTO);
        });
        
        assertTrue(exception.getMessage().contains("客户不存在"));
        verify(customerMapper, never()).updateById(any(Customer.class));
    }
    
    @Test
    void testDeleteCustomer_Success() {
        // Given
        Long customerId = 1L;
        when(customerMapper.selectById(customerId)).thenReturn(testCustomer);
        when(customerMapper.softDeleteById(customerId)).thenReturn(1);
        when(customerAddressMapper.softDeleteByCustomerId(customerId)).thenReturn(1);
        
        // When
        assertDoesNotThrow(() -> customerService.deleteCustomer(customerId));
        
        // Then
        verify(customerMapper).selectById(customerId);
        verify(customerMapper).softDeleteById(customerId);
        verify(customerAddressMapper).softDeleteByCustomerId(customerId);
    }
    
    @Test
    void testDeleteCustomer_NotFound() {
        // Given
        Long customerId = 999L;
        when(customerMapper.selectById(customerId)).thenReturn(null);
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            customerService.deleteCustomer(customerId);
        });
        
        assertTrue(exception.getMessage().contains("客户不存在"));
        verify(customerMapper, never()).softDeleteById(any());
    }    
// ==================== 搜索和筛选功能测试 ====================
    
    @Test
    void testSearchCustomers_Success() {
        // Given
        String keyword = "测试";
        Integer limit = 10;
        List<CustomerDTO> mockResults = Arrays.asList(testCustomerDTO);
        
        when(customerMapper.searchCustomers(keyword, limit)).thenReturn(mockResults);
        
        // When
        List<CustomerDTO> result = customerService.searchCustomers(keyword, limit);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(customerMapper).searchCustomers(keyword, limit);
    }
    
    @Test
    void testSearchCustomers_EmptyKeyword() {
        // When
        List<CustomerDTO> result = customerService.searchCustomers("", 10);
        
        // Then
        assertTrue(result.isEmpty());
        verify(customerMapper, never()).searchCustomers(anyString(), anyInt());
    }
    
    @Test
    void testSearchCustomers_NullKeyword() {
        // When
        List<CustomerDTO> result = customerService.searchCustomers(null, 10);
        
        // Then
        assertTrue(result.isEmpty());
        verify(customerMapper, never()).searchCustomers(anyString(), anyInt());
    }
    
    @Test
    void testGetCustomersByLevel_Success() {
        // Given
        String level = "vip";
        testQueryDTO = new CustomerQueryDTO();
        List<CustomerDTO> mockResults = Arrays.asList(testCustomerDTO);
        
        when(customerMapper.selectByCustomerLevel(level, testQueryDTO)).thenReturn(mockResults);
        
        // When
        List<CustomerDTO> result = customerService.getCustomersByLevel(level, testQueryDTO);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(customerMapper).selectByCustomerLevel(level, testQueryDTO);
    }
    
    @Test
    void testGetCustomersByRegion_Success() {
        // Given
        String region = "北京";
        testQueryDTO = new CustomerQueryDTO();
        List<CustomerDTO> mockResults = Arrays.asList(testCustomerDTO);
        
        when(customerMapper.selectByRegion(region, testQueryDTO)).thenReturn(mockResults);
        
        // When
        List<CustomerDTO> result = customerService.getCustomersByRegion(region, testQueryDTO);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(customerMapper).selectByRegion(region, testQueryDTO);
    }
    
    @Test
    void testAdvancedSearch_Success() {
        // Given
        testQueryDTO = new CustomerQueryDTO();
        testQueryDTO.setKeyword("测试");
        testQueryDTO.setCustomerLevel("vip");
        testQueryDTO.setPage(1);
        testQueryDTO.setPageSize(20);
        
        List<CustomerDTO> mockResults = Arrays.asList(testCustomerDTO);
        when(customerMapper.selectDTOList(any(CustomerQueryDTO.class))).thenReturn(mockResults);
        when(customerMapper.selectCount(any(CustomerQueryDTO.class))).thenReturn(1L);
        
        // When
        Map<String, Object> result = customerService.advancedSearch(testQueryDTO);
        
        // Then
        assertNotNull(result);
        assertEquals(1L, result.get("total"));
        assertFalse((Boolean) result.get("isEmpty"));
        
        @SuppressWarnings("unchecked")
        List<CustomerDTO> customers = (List<CustomerDTO>) result.get("list");
        assertEquals(1, customers.size());
    }
    
    // ==================== 数据验证功能测试 ====================
    
    @Test
    void testExistsByCustomerName_True() {
        // Given
        String customerName = "存在的客户";
        when(customerMapper.existsByCustomerName(customerName)).thenReturn(true);
        
        // When
        boolean result = customerService.existsByCustomerName(customerName);
        
        // Then
        assertTrue(result);
        verify(customerMapper).existsByCustomerName(customerName);
    }
    
    @Test
    void testExistsByCustomerName_False() {
        // Given
        String customerName = "不存在的客户";
        when(customerMapper.existsByCustomerName(customerName)).thenReturn(false);
        
        // When
        boolean result = customerService.existsByCustomerName(customerName);
        
        // Then
        assertFalse(result);
        verify(customerMapper).existsByCustomerName(customerName);
    }
    
    @Test
    void testExistsByPhone_Success() {
        // Given
        String phone = "13800138001";
        when(customerMapper.existsByPhone(phone)).thenReturn(true);
        
        // When
        boolean result = customerService.existsByPhone(phone);
        
        // Then
        assertTrue(result);
        verify(customerMapper).existsByPhone(phone);
    }
    
    @Test
    void testExistsByEmail_Success() {
        // Given
        String email = "test@example.com";
        when(customerMapper.existsByEmail(email)).thenReturn(true);
        
        // When
        boolean result = customerService.existsByEmail(email);
        
        // Then
        assertTrue(result);
        verify(customerMapper).existsByEmail(email);
    }    
// ==================== 异常处理测试 ====================
    
    @Test
    void testGetCustomers_DatabaseException() {
        // Given
        testQueryDTO = new CustomerQueryDTO();
        when(customerMapper.selectDTOList(any(CustomerQueryDTO.class)))
            .thenThrow(new RuntimeException("Database connection failed"));
        
        // When
        Map<String, Object> result = customerService.getCustomers(testQueryDTO);
        
        // Then
        assertNotNull(result);
        assertTrue((Boolean) result.get("isEmpty"));
        assertEquals(0L, result.get("total"));
    }
    
    @Test
    void testCreateCustomer_DatabaseException() {
        // Given
        testCreateDTO = new CustomerCreateDTO();
        testCreateDTO.setCustomerName("测试客户");
        testCreateDTO.setPhone("13900139001");
        testCreateDTO.setEmail("test@example.com");
        
        when(customerMapper.existsByCustomerName(anyString())).thenReturn(false);
        when(customerMapper.existsByPhone(anyString())).thenReturn(false);
        when(customerMapper.existsByEmail(anyString())).thenReturn(false);
        when(customerMapper.insert(any(Customer.class)))
            .thenThrow(new RuntimeException("Database insert failed"));
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            customerService.createCustomer(testCreateDTO);
        });
        
        assertTrue(exception.getMessage().contains("创建客户失败"));
    }
    
    // ==================== 边界条件测试 ====================
    
    @Test
    void testGetCustomers_LargePagination() {
        // Given
        testQueryDTO = new CustomerQueryDTO();
        testQueryDTO.setPage(1000);
        testQueryDTO.setPageSize(100);
        
        when(customerMapper.selectDTOList(any(CustomerQueryDTO.class))).thenReturn(Collections.emptyList());
        when(customerMapper.selectCount(any(CustomerQueryDTO.class))).thenReturn(0L);
        
        // When
        Map<String, Object> result = customerService.getCustomers(testQueryDTO);
        
        // Then
        assertNotNull(result);
        assertEquals(1000, result.get("page"));
        assertEquals(100, result.get("pageSize"));
    }
    
    @Test
    void testSearchCustomers_LargeLimit() {
        // Given
        String keyword = "测试";
        Integer limit = 1000;
        
        when(customerMapper.searchCustomers(keyword, limit)).thenReturn(Collections.emptyList());
        
        // When
        List<CustomerDTO> result = customerService.searchCustomers(keyword, limit);
        
        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(customerMapper).searchCustomers(keyword, limit);
    }
    
    @Test
    void testBatchDeleteCustomers_Success() {
        // Given
        List<Long> customerIds = Arrays.asList(1L, 2L, 3L);
        when(customerMapper.softDeleteByIds(customerIds)).thenReturn(3);
        
        // When
        assertDoesNotThrow(() -> customerService.deleteCustomers(customerIds));
        
        // Then
        verify(customerMapper).softDeleteByIds(customerIds);
        verify(customerAddressMapper, times(3)).softDeleteByCustomerId(any());
    }
    
    @Test
    void testBatchDeleteCustomers_EmptyList() {
        // Given
        List<Long> emptyIds = Collections.emptyList();
        
        // When
        assertDoesNotThrow(() -> customerService.deleteCustomers(emptyIds));
        
        // Then
        verify(customerMapper, never()).softDeleteByIds(any());
    }
    
    @Test
    void testBatchDeleteCustomers_NullList() {
        // When
        assertDoesNotThrow(() -> customerService.deleteCustomers(null));
        
        // Then
        verify(customerMapper, never()).softDeleteByIds(any());
    }
    
    // ==================== 辅助方法测试 ====================
    
    private void setupTestCustomerDTO() {
        testCustomerDTO = new CustomerDTO();
        testCustomerDTO.setId(1L);
        testCustomerDTO.setCustomerName("测试客户");
        testCustomerDTO.setPhone("13800138001");
        testCustomerDTO.setEmail("test@example.com");
        testCustomerDTO.setCustomerLevel("vip");
        testCustomerDTO.setTotalSpent(new BigDecimal("50000.00"));
        testCustomerDTO.setCustomerValue(new BigDecimal("8.5"));
    }
    
    @Test
    void testProcessCustomerList_NullSafety() {
        // Given
        when(customerMapper.selectDTOList(any(CustomerQueryDTO.class))).thenReturn(null);
        when(customerMapper.selectCount(any(CustomerQueryDTO.class))).thenReturn(0L);
        
        // When
        Map<String, Object> result = customerService.getCustomers(new CustomerQueryDTO());
        
        // Then
        assertNotNull(result);
        assertTrue((Boolean) result.get("isEmpty"));
    }
}