package com.yxrobot.service;

import com.yxrobot.dto.ManagedDeviceDTO;
import com.yxrobot.dto.ManagedDeviceSearchCriteria;
import com.yxrobot.dto.PageResult;
import com.yxrobot.entity.ManagedDevice;
import com.yxrobot.entity.DeviceModel;
import com.yxrobot.entity.DeviceStatus;
import com.yxrobot.exception.ManagedDeviceException;
import com.yxrobot.mapper.ManagedDeviceMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 设备管理搜索服务测试
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("设备管理搜索服务测试")
class ManagedDeviceSearchServiceTest {
    
    @Mock
    private ManagedDeviceMapper managedDeviceMapper;
    
    @Mock
    private ManagedDeviceValidationService validationService;
    
    @Mock
    private ManagedDeviceSecurityService securityService;
    
    @InjectMocks
    private ManagedDeviceSearchService searchService;
    
    private ManagedDevice testDevice;
    private ManagedDeviceSearchCriteria testCriteria;
    
    @BeforeEach
    void setUp() {
        // 创建测试设备
        testDevice = new ManagedDevice();
        testDevice.setId(1L);
        testDevice.setSerialNumber("YX2025001234");
        testDevice.setModel(DeviceModel.EDUCATION);
        testDevice.setStatus(DeviceStatus.ONLINE);
        testDevice.setFirmwareVersion("1.0.0");
        testDevice.setCustomerId(1L);
        testDevice.setCustomerName("张三");
        testDevice.setCustomerPhone("13812345678");
        testDevice.setCreatedAt(LocalDateTime.now());
        testDevice.setUpdatedAt(LocalDateTime.now());
        
        // 创建测试搜索条件
        testCriteria = new ManagedDeviceSearchCriteria();
        testCriteria.setPage(1);
        testCriteria.setPageSize(20);
    }
    
    @Test
    @DisplayName("测试高级搜索 - 正常情况")
    void testAdvancedSearch_Success() {
        // 准备测试数据
        List<ManagedDevice> devices = Arrays.asList(testDevice);
        when(managedDeviceMapper.advancedSearch(any())).thenReturn(devices);
        when(managedDeviceMapper.countAdvancedSearch(any())).thenReturn(1);
        
        // 执行搜索
        PageResult<ManagedDeviceDTO> result = searchService.advancedSearch(testCriteria);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getList().size());
        assertEquals("YX2025001234", result.getList().get(0).getSerialNumber());
        
        // 验证方法调用
        verify(validationService).validatePaginationParams(1, 20);
        verify(managedDeviceMapper).advancedSearch(testCriteria);
        verify(managedDeviceMapper).countAdvancedSearch(testCriteria);
        verify(securityService).logDataAccessEvent("SEARCH", "ManagedDevice", "ADVANCED_SEARCH", "SYSTEM", true);
    }
    
    @Test
    @DisplayName("测试高级搜索 - 空条件")
    void testAdvancedSearch_NullCriteria() {
        // 模拟验证失败
        doThrow(new ManagedDeviceException("VALIDATION_FAILED", "搜索条件不能为空"))
            .when(validationService).validatePaginationParams(any(), any());
        
        // 执行搜索并验证异常
        assertThrows(ManagedDeviceException.class, 
            () -> searchService.advancedSearch(null));
    }
    
    @Test
    @DisplayName("测试快速搜索 - 正常情况")
    void testQuickSearch_Success() {
        // 准备测试数据
        List<ManagedDevice> devices = Arrays.asList(testDevice);
        when(managedDeviceMapper.advancedSearch(any())).thenReturn(devices);
        when(managedDeviceMapper.countAdvancedSearch(any())).thenReturn(1);
        
        // 执行搜索
        PageResult<ManagedDeviceDTO> result = searchService.quickSearch("YX2025", 1, 20);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getList().size());
        
        // 验证方法调用
        verify(managedDeviceMapper).advancedSearch(any(ManagedDeviceSearchCriteria.class));
    }
    
    @Test
    @DisplayName("测试按状态搜索 - 正常情况")
    void testSearchByStatus_Success() {
        // 准备测试数据
        List<String> statusList = Arrays.asList("online", "offline");
        List<ManagedDevice> devices = Arrays.asList(testDevice);
        when(managedDeviceMapper.advancedSearch(any())).thenReturn(devices);
        when(managedDeviceMapper.countAdvancedSearch(any())).thenReturn(1);
        
        // 执行搜索
        PageResult<ManagedDeviceDTO> result = searchService.searchByStatus(statusList, 1, 20);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        
        // 验证方法调用
        verify(managedDeviceMapper).advancedSearch(any(ManagedDeviceSearchCriteria.class));
    }
    
    @Test
    @DisplayName("测试按型号搜索 - 正常情况")
    void testSearchByModel_Success() {
        // 准备测试数据
        List<String> modelList = Arrays.asList("EDUCATION", "HOME");
        List<ManagedDevice> devices = Arrays.asList(testDevice);
        when(managedDeviceMapper.advancedSearch(any())).thenReturn(devices);
        when(managedDeviceMapper.countAdvancedSearch(any())).thenReturn(1);
        
        // 执行搜索
        PageResult<ManagedDeviceDTO> result = searchService.searchByModel(modelList, 1, 20);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        
        // 验证方法调用
        verify(managedDeviceMapper).advancedSearch(any(ManagedDeviceSearchCriteria.class));
    }
    
    @Test
    @DisplayName("测试按客户搜索 - 正常情况")
    void testSearchByCustomer_Success() {
        // 准备测试数据
        List<ManagedDevice> devices = Arrays.asList(testDevice);
        when(managedDeviceMapper.advancedSearch(any())).thenReturn(devices);
        when(managedDeviceMapper.countAdvancedSearch(any())).thenReturn(1);
        
        // 执行搜索
        PageResult<ManagedDeviceDTO> result = searchService.searchByCustomer(1L, "张三", 1, 20);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        
        // 验证方法调用
        verify(managedDeviceMapper).advancedSearch(any(ManagedDeviceSearchCriteria.class));
    }
    
    @Test
    @DisplayName("测试按时间范围搜索 - 正常情况")
    void testSearchByDateRange_Success() {
        // 准备测试数据
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        List<ManagedDevice> devices = Arrays.asList(testDevice);
        when(managedDeviceMapper.advancedSearch(any())).thenReturn(devices);
        when(managedDeviceMapper.countAdvancedSearch(any())).thenReturn(1);
        
        // 执行搜索
        PageResult<ManagedDeviceDTO> result = searchService.searchByDateRange(startDate, endDate, "created", 1, 20);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        
        // 验证方法调用
        verify(managedDeviceMapper).advancedSearch(any(ManagedDeviceSearchCriteria.class));
    }
    
    @Test
    @DisplayName("测试按时间范围搜索 - 无效时间类型")
    void testSearchByDateRange_InvalidDateType() {
        // 执行搜索并验证异常
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        
        assertThrows(ManagedDeviceException.class, 
            () -> searchService.searchByDateRange(startDate, endDate, "invalid", 1, 20));
    }
    
    @Test
    @DisplayName("测试搜索在线设备 - 正常情况")
    void testSearchOnlineDevices_Success() {
        // 准备测试数据
        List<ManagedDevice> devices = Arrays.asList(testDevice);
        when(managedDeviceMapper.advancedSearch(any())).thenReturn(devices);
        when(managedDeviceMapper.countAdvancedSearch(any())).thenReturn(1);
        
        // 执行搜索
        PageResult<ManagedDeviceDTO> result = searchService.searchOnlineDevices(1, 20);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        
        // 验证方法调用
        verify(managedDeviceMapper).advancedSearch(any(ManagedDeviceSearchCriteria.class));
    }
    
    @Test
    @DisplayName("测试搜索离线设备 - 正常情况")
    void testSearchOfflineDevices_Success() {
        // 准备测试数据
        List<ManagedDevice> devices = Arrays.asList(testDevice);
        when(managedDeviceMapper.advancedSearch(any())).thenReturn(devices);
        when(managedDeviceMapper.countAdvancedSearch(any())).thenReturn(1);
        
        // 执行搜索
        PageResult<ManagedDeviceDTO> result = searchService.searchOfflineDevices(1, 20);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        
        // 验证方法调用
        verify(managedDeviceMapper).advancedSearch(any(ManagedDeviceSearchCriteria.class));
    }
    
    @Test
    @DisplayName("测试搜索未激活设备 - 正常情况")
    void testSearchUnactivatedDevices_Success() {
        // 准备测试数据
        List<ManagedDevice> devices = Arrays.asList(testDevice);
        when(managedDeviceMapper.advancedSearch(any())).thenReturn(devices);
        when(managedDeviceMapper.countAdvancedSearch(any())).thenReturn(1);
        
        // 执行搜索
        PageResult<ManagedDeviceDTO> result = searchService.searchUnactivatedDevices(1, 20);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        
        // 验证方法调用
        verify(managedDeviceMapper).advancedSearch(any(ManagedDeviceSearchCriteria.class));
    }
    
    @Test
    @DisplayName("测试获取搜索建议 - 正常情况")
    void testGetSearchSuggestions_Success() {
        // 准备测试数据
        List<String> suggestions = Arrays.asList("YX2025001234", "YX2025001235", "YX2025001236");
        when(managedDeviceMapper.getSearchSuggestions(anyString(), anyString(), anyInt())).thenReturn(suggestions);
        
        // 执行搜索
        List<String> result = searchService.getSearchSuggestions("serialNumber", "YX2025", 10);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("YX2025001234"));
        
        // 验证方法调用
        verify(managedDeviceMapper).getSearchSuggestions("serialNumber", "YX2025", 10);
    }
    
    @Test
    @DisplayName("测试获取搜索建议 - XSS攻击检测")
    void testGetSearchSuggestions_XSSAttack() {
        // 模拟XSS攻击检测
        String maliciousQuery = "<script>alert('xss')</script>";
        
        // 执行搜索并验证异常处理
        assertThrows(Exception.class, 
            () -> searchService.getSearchSuggestions("serialNumber", maliciousQuery, 10));
    }
    
    @Test
    @DisplayName("测试搜索条件验证 - 关键词过长")
    void testValidateSearchCriteria_KeywordTooLong() {
        // 创建包含过长关键词的搜索条件
        ManagedDeviceSearchCriteria criteria = new ManagedDeviceSearchCriteria();
        criteria.setKeyword("A".repeat(101)); // 超过100字符
        
        // 模拟验证失败
        doThrow(new ManagedDeviceException("VALIDATION_FAILED", "搜索关键词长度不能超过100字符"))
            .when(validationService).validatePaginationParams(any(), any());
        
        // 执行搜索并验证异常
        assertThrows(ManagedDeviceException.class, 
            () -> searchService.advancedSearch(criteria));
    }
    
    @Test
    @DisplayName("测试搜索条件验证 - 时间范围无效")
    void testValidateSearchCriteria_InvalidDateRange() {
        // 创建包含无效时间范围的搜索条件
        ManagedDeviceSearchCriteria criteria = new ManagedDeviceSearchCriteria();
        criteria.setCreatedStartDate(LocalDateTime.now());
        criteria.setCreatedEndDate(LocalDateTime.now().minusDays(1)); // 结束时间早于开始时间
        
        // 验证时间范围无效
        assertFalse(criteria.isValidAllDateRanges());
    }
    
    @Test
    @DisplayName("测试搜索条件 - 检查是否有搜索条件")
    void testSearchCriteria_HasSearchCriteria() {
        // 空条件
        ManagedDeviceSearchCriteria emptyCriteria = new ManagedDeviceSearchCriteria();
        assertFalse(emptyCriteria.hasSearchCriteria());
        
        // 有关键词
        ManagedDeviceSearchCriteria keywordCriteria = new ManagedDeviceSearchCriteria();
        keywordCriteria.setKeyword("test");
        assertTrue(keywordCriteria.hasSearchCriteria());
        
        // 有状态筛选
        ManagedDeviceSearchCriteria statusCriteria = new ManagedDeviceSearchCriteria();
        statusCriteria.setStatus("online");
        assertTrue(statusCriteria.hasSearchCriteria());
        
        // 有时间范围
        ManagedDeviceSearchCriteria dateCriteria = new ManagedDeviceSearchCriteria();
        dateCriteria.setCreatedStartDate(LocalDateTime.now().minusDays(7));
        assertTrue(dateCriteria.hasSearchCriteria());
    }
    
    @Test
    @DisplayName("测试搜索条件 - 偏移量和限制计算")
    void testSearchCriteria_OffsetAndLimit() {
        ManagedDeviceSearchCriteria criteria = new ManagedDeviceSearchCriteria();
        criteria.setPage(3);
        criteria.setPageSize(25);
        
        assertEquals(50, criteria.getOffset()); // (3-1) * 25 = 50
        assertEquals(25, criteria.getLimit());
    }
    
    @Test
    @DisplayName("测试搜索条件 - 参数默认值")
    void testSearchCriteria_DefaultValues() {
        ManagedDeviceSearchCriteria criteria = new ManagedDeviceSearchCriteria();
        
        assertEquals(1, criteria.getPage());
        assertEquals(20, criteria.getPageSize());
        assertEquals("createdAt", criteria.getSortBy());
        assertEquals("DESC", criteria.getSortOrder());
        assertEquals(false, criteria.getIncludeDeleted());
    }
    
    @Test
    @DisplayName("测试搜索条件 - 参数边界值处理")
    void testSearchCriteria_BoundaryValues() {
        ManagedDeviceSearchCriteria criteria = new ManagedDeviceSearchCriteria();
        
        // 测试页码边界值
        criteria.setPage(0);
        assertEquals(1, criteria.getPage()); // 应该被修正为1
        
        criteria.setPage(-1);
        assertEquals(1, criteria.getPage()); // 应该被修正为1
        
        // 测试页大小边界值
        criteria.setPageSize(0);
        assertEquals(20, criteria.getPageSize()); // 应该被修正为20
        
        criteria.setPageSize(1001);
        assertEquals(20, criteria.getPageSize()); // 应该被修正为20
        
        criteria.setPageSize(500);
        assertEquals(500, criteria.getPageSize()); // 有效值应该保持
    }
}