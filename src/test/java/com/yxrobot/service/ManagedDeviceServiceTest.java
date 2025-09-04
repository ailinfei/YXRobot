package com.yxrobot.service;

import com.yxrobot.dto.ManagedDeviceDTO;
import com.yxrobot.dto.ManagedDeviceSearchCriteria;
import com.yxrobot.entity.ManagedDevice;
import com.yxrobot.entity.Customer;
import com.yxrobot.exception.ManagedDeviceException;
import com.yxrobot.mapper.ManagedDeviceMapper;
import com.yxrobot.mapper.CustomerMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ManagedDeviceService 单元测试类
 * 
 * 测试覆盖范围：
 * - 设备管理业务逻辑
 * - 数据验证和转换
 * - 异常处理
 * - 边界条件测试
 * 
 * 测试目标覆盖率：
 * - 行覆盖率 ≥ 80%
 * - 分支覆盖率 ≥ 75%
 * - 方法覆盖率 ≥ 85%
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("设备管理服务单元测试")
public class ManagedDeviceServiceTest {

    @Mock
    private ManagedDeviceMapper managedDeviceMapper;
    
    @Mock
    private CustomerMapper customerMapper;
    
    @InjectMocks
    private ManagedDeviceService managedDeviceService;
    
    private ManagedDevice testDevice;
    private Customer testCustomer;
    private ManagedDeviceDTO testDeviceDTO;
    private ManagedDeviceSearchCriteria testCriteria;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // 初始化测试数据
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("[测试客户]");
        testCustomer.setPhone("[测试电话]");
        testCustomer.setEmail("[测试邮箱]");
        
        testDevice = new ManagedDevice();
        testDevice.setId(1L);
        testDevice.setSerialNumber("YX-TEST-001");
        testDevice.setModel("YX-EDU-2024");
        testDevice.setStatus("online");
        testDevice.setFirmwareVersion("1.0.0");
        testDevice.setCustomerId(1L);
        testDevice.setCustomerName("[测试客户]");
        testDevice.setCustomerPhone("[测试电话]");
        testDevice.setCreatedAt(LocalDateTime.now());
        testDevice.setUpdatedAt(LocalDateTime.now());
        
        testDeviceDTO = new ManagedDeviceDTO();
        testDeviceDTO.setId("1");
        testDeviceDTO.setSerialNumber("YX-TEST-001");
        testDeviceDTO.setModel("YX-EDU-2024");
        testDeviceDTO.setStatus("online");
        testDeviceDTO.setFirmwareVersion("1.0.0");
        testDeviceDTO.setCustomerId("1");
        testDeviceDTO.setCustomerName("[测试客户]");
        testDeviceDTO.setCustomerPhone("[测试电话]");
        
        testCriteria = new ManagedDeviceSearchCriteria();
        testCriteria.setPage(1);
        testCriteria.setPageSize(20);
    }

    @Test
    @DisplayName("获取设备列表 - 成功场景")
    void testGetManagedDevices_Success() {
        // Given
        List<ManagedDevice> mockDevices = Arrays.asList(testDevice);
        when(managedDeviceMapper.selectBySearchCriteria(any(ManagedDeviceSearchCriteria.class)))
            .thenReturn(mockDevices);
        when(managedDeviceMapper.countBySearchCriteria(any(ManagedDeviceSearchCriteria.class)))
            .thenReturn(1L);

        // When
        Map<String, Object> result = managedDeviceService.getManagedDevices(testCriteria);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.get("total"));
        assertEquals(1, result.get("page"));
        assertEquals(20, result.get("pageSize"));
        
        @SuppressWarnings("unchecked")
        List<ManagedDeviceDTO> devices = (List<ManagedDeviceDTO>) result.get("list");
        assertNotNull(devices);
        assertEquals(1, devices.size());
        assertEquals("YX-TEST-001", devices.get(0).getSerialNumber());
        
        verify(managedDeviceMapper).selectBySearchCriteria(testCriteria);
        verify(managedDeviceMapper).countBySearchCriteria(testCriteria);
    }

    @Test
    @DisplayName("获取设备列表 - 空结果")
    void testGetManagedDevices_EmptyResult() {
        // Given
        when(managedDeviceMapper.selectBySearchCriteria(any(ManagedDeviceSearchCriteria.class)))
            .thenReturn(Arrays.asList());
        when(managedDeviceMapper.countBySearchCriteria(any(ManagedDeviceSearchCriteria.class)))
            .thenReturn(0L);

        // When
        Map<String, Object> result = managedDeviceService.getManagedDevices(testCriteria);

        // Then
        assertNotNull(result);
        assertEquals(0L, result.get("total"));
        
        @SuppressWarnings("unchecked")
        List<ManagedDeviceDTO> devices = (List<ManagedDeviceDTO>) result.get("list");
        assertNotNull(devices);
        assertTrue(devices.isEmpty());
    }

    @Test
    @DisplayName("根据ID获取设备 - 成功场景")
    void testGetManagedDeviceById_Success() {
        // Given
        when(managedDeviceMapper.selectById(1L)).thenReturn(testDevice);

        // When
        ManagedDeviceDTO result = managedDeviceService.getManagedDeviceById(1L);

        // Then
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("YX-TEST-001", result.getSerialNumber());
        assertEquals("YX-EDU-2024", result.getModel());
        assertEquals("online", result.getStatus());
        
        verify(managedDeviceMapper).selectById(1L);
    }

    @Test
    @DisplayName("根据ID获取设备 - 设备不存在")
    void testGetManagedDeviceById_NotFound() {
        // Given
        when(managedDeviceMapper.selectById(999L)).thenReturn(null);

        // When & Then
        ManagedDeviceException exception = assertThrows(
            ManagedDeviceException.class,
            () -> managedDeviceService.getManagedDeviceById(999L)
        );
        
        assertEquals("设备不存在", exception.getMessage());
        verify(managedDeviceMapper).selectById(999L);
    }

    @Test
    @DisplayName("创建设备 - 成功场景")
    void testCreateManagedDevice_Success() {
        // Given
        when(customerMapper.selectById(1L)).thenReturn(testCustomer);
        when(managedDeviceMapper.selectBySerialNumber("YX-TEST-002")).thenReturn(null);
        when(managedDeviceMapper.insert(any(ManagedDevice.class))).thenReturn(1);
        
        ManagedDeviceDTO newDeviceDTO = new ManagedDeviceDTO();
        newDeviceDTO.setSerialNumber("YX-TEST-002");
        newDeviceDTO.setModel("YX-EDU-2024");
        newDeviceDTO.setCustomerId("1");
        newDeviceDTO.setFirmwareVersion("1.0.0");

        // When
        ManagedDeviceDTO result = managedDeviceService.createManagedDevice(newDeviceDTO);

        // Then
        assertNotNull(result);
        assertEquals("YX-TEST-002", result.getSerialNumber());
        assertEquals("YX-EDU-2024", result.getModel());
        assertEquals("1", result.getCustomerId());
        assertEquals("[测试客户]", result.getCustomerName());
        
        verify(customerMapper).selectById(1L);
        verify(managedDeviceMapper).selectBySerialNumber("YX-TEST-002");
        verify(managedDeviceMapper).insert(any(ManagedDevice.class));
    }

    @Test
    @DisplayName("创建设备 - 序列号重复")
    void testCreateManagedDevice_DuplicateSerialNumber() {
        // Given
        when(managedDeviceMapper.selectBySerialNumber("YX-TEST-001")).thenReturn(testDevice);
        
        ManagedDeviceDTO newDeviceDTO = new ManagedDeviceDTO();
        newDeviceDTO.setSerialNumber("YX-TEST-001");
        newDeviceDTO.setModel("YX-EDU-2024");
        newDeviceDTO.setCustomerId("1");

        // When & Then
        ManagedDeviceException exception = assertThrows(
            ManagedDeviceException.class,
            () -> managedDeviceService.createManagedDevice(newDeviceDTO)
        );
        
        assertEquals("设备序列号已存在", exception.getMessage());
        verify(managedDeviceMapper).selectBySerialNumber("YX-TEST-001");
        verify(managedDeviceMapper, never()).insert(any(ManagedDevice.class));
    }

    @Test
    @DisplayName("创建设备 - 客户不存在")
    void testCreateManagedDevice_CustomerNotFound() {
        // Given
        when(customerMapper.selectById(999L)).thenReturn(null);
        
        ManagedDeviceDTO newDeviceDTO = new ManagedDeviceDTO();
        newDeviceDTO.setSerialNumber("YX-TEST-002");
        newDeviceDTO.setModel("YX-EDU-2024");
        newDeviceDTO.setCustomerId("999");

        // When & Then
        ManagedDeviceException exception = assertThrows(
            ManagedDeviceException.class,
            () -> managedDeviceService.createManagedDevice(newDeviceDTO)
        );
        
        assertEquals("客户不存在", exception.getMessage());
        verify(customerMapper).selectById(999L);
        verify(managedDeviceMapper, never()).insert(any(ManagedDevice.class));
    }

    @Test
    @DisplayName("更新设备 - 成功场景")
    void testUpdateManagedDevice_Success() {
        // Given
        when(managedDeviceMapper.selectById(1L)).thenReturn(testDevice);
        when(customerMapper.selectById(1L)).thenReturn(testCustomer);
        when(managedDeviceMapper.updateById(any(ManagedDevice.class))).thenReturn(1);
        
        ManagedDeviceDTO updateDTO = new ManagedDeviceDTO();
        updateDTO.setId("1");
        updateDTO.setSerialNumber("YX-TEST-001-UPDATED");
        updateDTO.setModel("YX-PRO-2024");
        updateDTO.setCustomerId("1");
        updateDTO.setFirmwareVersion("1.1.0");

        // When
        ManagedDeviceDTO result = managedDeviceService.updateManagedDevice(1L, updateDTO);

        // Then
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("YX-TEST-001-UPDATED", result.getSerialNumber());
        assertEquals("YX-PRO-2024", result.getModel());
        assertEquals("1.1.0", result.getFirmwareVersion());
        
        verify(managedDeviceMapper).selectById(1L);
        verify(customerMapper).selectById(1L);
        verify(managedDeviceMapper).updateById(any(ManagedDevice.class));
    }

    @Test
    @DisplayName("更新设备 - 设备不存在")
    void testUpdateManagedDevice_DeviceNotFound() {
        // Given
        when(managedDeviceMapper.selectById(999L)).thenReturn(null);
        
        ManagedDeviceDTO updateDTO = new ManagedDeviceDTO();
        updateDTO.setId("999");

        // When & Then
        ManagedDeviceException exception = assertThrows(
            ManagedDeviceException.class,
            () -> managedDeviceService.updateManagedDevice(999L, updateDTO)
        );
        
        assertEquals("设备不存在", exception.getMessage());
        verify(managedDeviceMapper).selectById(999L);
        verify(managedDeviceMapper, never()).updateById(any(ManagedDevice.class));
    }

    @Test
    @DisplayName("删除设备 - 成功场景")
    void testDeleteManagedDevice_Success() {
        // Given
        when(managedDeviceMapper.selectById(1L)).thenReturn(testDevice);
        when(managedDeviceMapper.deleteById(1L)).thenReturn(1);

        // When
        boolean result = managedDeviceService.deleteManagedDevice(1L);

        // Then
        assertTrue(result);
        verify(managedDeviceMapper).selectById(1L);
        verify(managedDeviceMapper).deleteById(1L);
    }

    @Test
    @DisplayName("删除设备 - 设备不存在")
    void testDeleteManagedDevice_DeviceNotFound() {
        // Given
        when(managedDeviceMapper.selectById(999L)).thenReturn(null);

        // When & Then
        ManagedDeviceException exception = assertThrows(
            ManagedDeviceException.class,
            () -> managedDeviceService.deleteManagedDevice(999L)
        );
        
        assertEquals("设备不存在", exception.getMessage());
        verify(managedDeviceMapper).selectById(999L);
        verify(managedDeviceMapper, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("搜索条件验证 - 关键词搜索")
    void testSearchCriteria_KeywordSearch() {
        // Given
        testCriteria.setKeyword("YX-TEST");
        List<ManagedDevice> mockDevices = Arrays.asList(testDevice);
        when(managedDeviceMapper.selectBySearchCriteria(testCriteria)).thenReturn(mockDevices);
        when(managedDeviceMapper.countBySearchCriteria(testCriteria)).thenReturn(1L);

        // When
        Map<String, Object> result = managedDeviceService.getManagedDevices(testCriteria);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.get("total"));
        verify(managedDeviceMapper).selectBySearchCriteria(testCriteria);
    }

    @Test
    @DisplayName("搜索条件验证 - 状态筛选")
    void testSearchCriteria_StatusFilter() {
        // Given
        testCriteria.setStatus("online");
        List<ManagedDevice> mockDevices = Arrays.asList(testDevice);
        when(managedDeviceMapper.selectBySearchCriteria(testCriteria)).thenReturn(mockDevices);
        when(managedDeviceMapper.countBySearchCriteria(testCriteria)).thenReturn(1L);

        // When
        Map<String, Object> result = managedDeviceService.getManagedDevices(testCriteria);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.get("total"));
        verify(managedDeviceMapper).selectBySearchCriteria(testCriteria);
    }

    @Test
    @DisplayName("搜索条件验证 - 型号筛选")
    void testSearchCriteria_ModelFilter() {
        // Given
        testCriteria.setModel("YX-EDU-2024");
        List<ManagedDevice> mockDevices = Arrays.asList(testDevice);
        when(managedDeviceMapper.selectBySearchCriteria(testCriteria)).thenReturn(mockDevices);
        when(managedDeviceMapper.countBySearchCriteria(testCriteria)).thenReturn(1L);

        // When
        Map<String, Object> result = managedDeviceService.getManagedDevices(testCriteria);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.get("total"));
        verify(managedDeviceMapper).selectBySearchCriteria(testCriteria);
    }

    @Test
    @DisplayName("分页参数验证 - 默认分页")
    void testPagination_DefaultValues() {
        // Given
        ManagedDeviceSearchCriteria criteria = new ManagedDeviceSearchCriteria();
        // 不设置分页参数，测试默认值
        
        when(managedDeviceMapper.selectBySearchCriteria(criteria)).thenReturn(Arrays.asList());
        when(managedDeviceMapper.countBySearchCriteria(criteria)).thenReturn(0L);

        // When
        Map<String, Object> result = managedDeviceService.getManagedDevices(criteria);

        // Then
        assertNotNull(result);
        assertEquals(1, result.get("page")); // 默认第1页
        assertEquals(20, result.get("pageSize")); // 默认20条/页
    }

    @Test
    @DisplayName("数据转换测试 - Entity到DTO")
    void testEntityToDTO_Conversion() {
        // Given
        ManagedDevice device = new ManagedDevice();
        device.setId(1L);
        device.setSerialNumber("YX-TEST-001");
        device.setModel("YX-EDU-2024");
        device.setStatus("online");
        device.setFirmwareVersion("1.0.0");
        device.setCustomerId(1L);
        device.setCustomerName("[测试客户]");
        device.setCustomerPhone("[测试电话]");
        device.setCreatedAt(LocalDateTime.now());

        // When
        ManagedDeviceDTO dto = managedDeviceService.convertToDTO(device);

        // Then
        assertNotNull(dto);
        assertEquals("1", dto.getId());
        assertEquals("YX-TEST-001", dto.getSerialNumber());
        assertEquals("YX-EDU-2024", dto.getModel());
        assertEquals("online", dto.getStatus());
        assertEquals("1.0.0", dto.getFirmwareVersion());
        assertEquals("1", dto.getCustomerId());
        assertEquals("[测试客户]", dto.getCustomerName());
        assertEquals("[测试电话]", dto.getCustomerPhone());
        assertNotNull(dto.getCreatedAt());
    }

    @Test
    @DisplayName("数据转换测试 - DTO到Entity")
    void testDTOToEntity_Conversion() {
        // Given
        ManagedDeviceDTO dto = new ManagedDeviceDTO();
        dto.setId("1");
        dto.setSerialNumber("YX-TEST-001");
        dto.setModel("YX-EDU-2024");
        dto.setStatus("online");
        dto.setFirmwareVersion("1.0.0");
        dto.setCustomerId("1");
        dto.setCustomerName("[测试客户]");
        dto.setCustomerPhone("[测试电话]");

        // When
        ManagedDevice entity = managedDeviceService.convertToEntity(dto);

        // Then
        assertNotNull(entity);
        assertEquals(1L, entity.getId());
        assertEquals("YX-TEST-001", entity.getSerialNumber());
        assertEquals("YX-EDU-2024", entity.getModel());
        assertEquals("online", entity.getStatus());
        assertEquals("1.0.0", entity.getFirmwareVersion());
        assertEquals(1L, entity.getCustomerId());
        assertEquals("[测试客户]", entity.getCustomerName());
        assertEquals("[测试电话]", entity.getCustomerPhone());
    }

    @Test
    @DisplayName("异常处理测试 - 数据库操作异常")
    void testDatabaseException_Handling() {
        // Given
        when(managedDeviceMapper.selectBySearchCriteria(any(ManagedDeviceSearchCriteria.class)))
            .thenThrow(new RuntimeException("数据库连接异常"));

        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> managedDeviceService.getManagedDevices(testCriteria)
        );
        
        assertEquals("数据库连接异常", exception.getMessage());
    }

    @Test
    @DisplayName("边界条件测试 - 空参数处理")
    void testBoundaryConditions_NullParameters() {
        // When & Then
        assertThrows(IllegalArgumentException.class, 
            () -> managedDeviceService.getManagedDevices(null));
        
        assertThrows(IllegalArgumentException.class, 
            () -> managedDeviceService.getManagedDeviceById(null));
        
        assertThrows(IllegalArgumentException.class, 
            () -> managedDeviceService.createManagedDevice(null));
        
        assertThrows(IllegalArgumentException.class, 
            () -> managedDeviceService.updateManagedDevice(1L, null));
        
        assertThrows(IllegalArgumentException.class, 
            () -> managedDeviceService.deleteManagedDevice(null));
    }

    @Test
    @DisplayName("性能测试 - 大数据量处理")
    void testPerformance_LargeDataSet() {
        // Given
        List<ManagedDevice> largeDeviceList = Arrays.asList(
            testDevice, testDevice, testDevice, testDevice, testDevice
        );
        when(managedDeviceMapper.selectBySearchCriteria(any(ManagedDeviceSearchCriteria.class)))
            .thenReturn(largeDeviceList);
        when(managedDeviceMapper.countBySearchCriteria(any(ManagedDeviceSearchCriteria.class)))
            .thenReturn(5L);

        // When
        long startTime = System.currentTimeMillis();
        Map<String, Object> result = managedDeviceService.getManagedDevices(testCriteria);
        long endTime = System.currentTimeMillis();

        // Then
        assertNotNull(result);
        assertEquals(5L, result.get("total"));
        
        // 性能断言：处理时间应该在合理范围内（小于1秒）
        assertTrue((endTime - startTime) < 1000, "处理时间应该小于1秒");
    }
}