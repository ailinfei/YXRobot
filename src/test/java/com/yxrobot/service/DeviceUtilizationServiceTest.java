package com.yxrobot.service;

import com.yxrobot.dto.DeviceUtilizationDTO;
import com.yxrobot.dto.DeviceStatusStatsDTO;
import com.yxrobot.entity.RentalDevice;
import com.yxrobot.mapper.RentalDeviceMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 设备利用率服务测试类
 * 测试设备利用率管理业务逻辑的正确性
 * 
 * @author Kiro
 * @date 2025-01-28
 */
@ExtendWith(MockitoExtension.class)
class DeviceUtilizationServiceTest {
    
    @Mock
    private RentalDeviceMapper rentalDeviceMapper;
    
    @InjectMocks
    private DeviceUtilizationService deviceUtilizationService;
    
    private List<DeviceUtilizationDTO> mockDeviceList;
    private Map<String, Object> mockStatusStats;
    private RentalDevice mockDevice;
    
    @BeforeEach
    void setUp() {
        // 模拟设备利用率数据列表
        mockDeviceList = new ArrayList<>();
        
        DeviceUtilizationDTO device1 = new DeviceUtilizationDTO();
        device1.setDeviceId("YX-001");
        device1.setDeviceModel("YX-Robot-Pro");
        device1.setUtilizationRate(new BigDecimal("85.5"));
        device1.setCurrentStatus("active");
        device1.setTotalRentalDays(120);
        
        DeviceUtilizationDTO device2 = new DeviceUtilizationDTO();
        device2.setDeviceId("YX-002");
        device2.setDeviceModel("YX-Robot-Standard");
        device2.setUtilizationRate(new BigDecimal("72.3"));
        device2.setCurrentStatus("idle");
        device2.setTotalRentalDays(95);
        
        mockDeviceList.add(device1);
        mockDeviceList.add(device2);
        
        // 模拟设备状态统计
        mockStatusStats = new HashMap<>();
        mockStatusStats.put("active", 65);
        mockStatusStats.put("idle", 15);
        mockStatusStats.put("maintenance", 8);
        
        // 模拟设备详情
        mockDevice = new RentalDevice();
        mockDevice.setId(1L);
        mockDevice.setDeviceId("YX-001");
        mockDevice.setDeviceModel("YX-Robot-Pro");
        mockDevice.setDeviceName("智能机器人Pro版");
        mockDevice.setUtilizationRate(new BigDecimal("85.5"));
    }
    
    @Test
    void testGetDeviceUtilizationData_WithValidParams_ShouldReturnDeviceList() {
        // Given
        Map<String, Object> params = new HashMap<>();
        params.put("page", 1);
        params.put("pageSize", 20);
        params.put("keyword", "YX");
        
        when(rentalDeviceMapper.selectDeviceUtilizationList(params)).thenReturn(mockDeviceList);
        
        // When
        List<DeviceUtilizationDTO> result = deviceUtilizationService.getDeviceUtilizationData(params);
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("YX-001", result.get(0).getDeviceId());
        assertEquals("YX-Robot-Pro", result.get(0).getDeviceModel());
        assertEquals(new BigDecimal("85.5"), result.get(0).getUtilizationRate());
        
        verify(rentalDeviceMapper).selectDeviceUtilizationList(params);
    }
    
    @Test
    void testGetDeviceUtilizationData_WithNullResult_ShouldReturnEmptyList() {
        // Given
        Map<String, Object> params = new HashMap<>();
        when(rentalDeviceMapper.selectDeviceUtilizationList(params)).thenReturn(null);
        
        // When
        List<DeviceUtilizationDTO> result = deviceUtilizationService.getDeviceUtilizationData(params);
        
        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    
    @Test
    void testGetDeviceUtilizationData_WithException_ShouldReturnEmptyList() {
        // Given
        Map<String, Object> params = new HashMap<>();
        when(rentalDeviceMapper.selectDeviceUtilizationList(params))
            .thenThrow(new RuntimeException("Database error"));
        
        // When
        List<DeviceUtilizationDTO> result = deviceUtilizationService.getDeviceUtilizationData(params);
        
        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    
    @Test
    void testGetDeviceUtilizationCount_WithValidParams_ShouldReturnCount() {
        // Given
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", "YX");
        
        when(rentalDeviceMapper.selectDeviceUtilizationCount(params)).thenReturn(50L);
        
        // When
        Long result = deviceUtilizationService.getDeviceUtilizationCount(params);
        
        // Then
        assertEquals(50L, result);
        verify(rentalDeviceMapper).selectDeviceUtilizationCount(params);
    }
    
    @Test
    void testGetDeviceUtilizationCount_WithNullResult_ShouldReturnZero() {
        // Given
        Map<String, Object> params = new HashMap<>();
        when(rentalDeviceMapper.selectDeviceUtilizationCount(params)).thenReturn(null);
        
        // When
        Long result = deviceUtilizationService.getDeviceUtilizationCount(params);
        
        // Then
        assertEquals(0L, result);
    }
    
    @Test
    void testGetDeviceUtilizationData_WithPaginationParams_ShouldReturnPaginatedResult() {
        // Given
        Integer page = 2;
        Integer pageSize = 10;
        String deviceModel = "YX-Robot-Pro";
        String currentStatus = "active";
        String keyword = "YX";
        
        when(rentalDeviceMapper.selectDeviceUtilizationList(any(Map.class))).thenReturn(mockDeviceList);
        when(rentalDeviceMapper.selectDeviceUtilizationCount(any(Map.class))).thenReturn(25L);
        
        // When
        Map<String, Object> result = deviceUtilizationService.getDeviceUtilizationData(
            page, pageSize, deviceModel, currentStatus, keyword);
        
        // Then
        assertNotNull(result);
        assertEquals(mockDeviceList, result.get("list"));
        assertEquals(25L, result.get("total"));
        assertEquals(2, result.get("page"));
        assertEquals(10, result.get("pageSize"));
        assertEquals(false, result.get("isEmpty"));
        
        // 验证参数构建
        verify(rentalDeviceMapper).selectDeviceUtilizationList(argThat(params -> {
            Map<String, Object> p = (Map<String, Object>) params;
            return p.get("page").equals(2) &&
                   p.get("pageSize").equals(10) &&
                   p.get("offset").equals(10) &&
                   p.get("deviceModel").equals("YX-Robot-Pro") &&
                   p.get("currentStatus").equals("active") &&
                   p.get("keyword").equals("YX");
        }));
    }
    
    @Test
    void testGetDeviceUtilizationData_WithNullParams_ShouldUseDefaults() {
        // Given
        when(rentalDeviceMapper.selectDeviceUtilizationList(any(Map.class))).thenReturn(mockDeviceList);
        when(rentalDeviceMapper.selectDeviceUtilizationCount(any(Map.class))).thenReturn(2L);
        
        // When
        Map<String, Object> result = deviceUtilizationService.getDeviceUtilizationData(
            null, null, null, null, null);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.get("page")); // 默认页码
        assertEquals(20, result.get("pageSize")); // 默认页大小
    }
    
    @Test
    void testGetDeviceUtilizationData_WithEmptyStrings_ShouldIgnoreEmptyParams() {
        // Given
        when(rentalDeviceMapper.selectDeviceUtilizationList(any(Map.class))).thenReturn(mockDeviceList);
        when(rentalDeviceMapper.selectDeviceUtilizationCount(any(Map.class))).thenReturn(2L);
        
        // When
        Map<String, Object> result = deviceUtilizationService.getDeviceUtilizationData(
            1, 20, "", "  ", "   ");
        
        // Then
        assertNotNull(result);
        
        // 验证空字符串参数不会被添加到查询参数中
        verify(rentalDeviceMapper).selectDeviceUtilizationList(argThat(params -> {
            Map<String, Object> p = (Map<String, Object>) params;
            return !p.containsKey("deviceModel") &&
                   !p.containsKey("currentStatus") &&
                   !p.containsKey("keyword");
        }));
    }
    
    @Test
    void testGetDeviceStatusStats_WithValidData_ShouldReturnStatusStats() {
        // Given
        when(rentalDeviceMapper.selectDeviceStatusStats()).thenReturn(mockStatusStats);
        
        // When
        Map<String, Object> result = deviceUtilizationService.getDeviceStatusStats();
        
        // Then
        assertNotNull(result);
        assertEquals(65, result.get("active"));
        assertEquals(15, result.get("idle"));
        assertEquals(8, result.get("maintenance"));
        
        verify(rentalDeviceMapper).selectDeviceStatusStats();
    }
    
    @Test
    void testGetDeviceStatusStats_WithNullData_ShouldReturnDefaultStats() {
        // Given
        when(rentalDeviceMapper.selectDeviceStatusStats()).thenReturn(null);
        
        // When
        Map<String, Object> result = deviceUtilizationService.getDeviceStatusStats();
        
        // Then
        assertNotNull(result);
        assertEquals(0, result.get("active"));
        assertEquals(0, result.get("idle"));
        assertEquals(0, result.get("maintenance"));
    }
    
    @Test
    void testGetDeviceStatusStatsDTO_WithValidData_ShouldReturnDTO() {
        // Given
        when(rentalDeviceMapper.selectDeviceStatusStats()).thenReturn(mockStatusStats);
        
        // When
        DeviceStatusStatsDTO result = deviceUtilizationService.getDeviceStatusStatsDTO();
        
        // Then
        assertNotNull(result);
        assertEquals(Integer.valueOf(65), result.getActive());
        assertEquals(Integer.valueOf(15), result.getIdle());
        assertEquals(Integer.valueOf(8), result.getMaintenance());
    }
    
    @Test
    void testGetTopDevicesByUtilization_WithValidLimit_ShouldReturnTopDevices() {
        // Given
        Integer limit = 5;
        when(rentalDeviceMapper.selectTopDevicesByUtilization(limit)).thenReturn(mockDeviceList);
        
        // When
        List<DeviceUtilizationDTO> result = deviceUtilizationService.getTopDevicesByUtilization(limit);
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(rentalDeviceMapper).selectTopDevicesByUtilization(5);
    }
    
    @Test
    void testGetTopDevicesByUtilization_WithNullLimit_ShouldUseDefaultLimit() {
        // Given
        when(rentalDeviceMapper.selectTopDevicesByUtilization(5)).thenReturn(mockDeviceList);
        
        // When
        List<DeviceUtilizationDTO> result = deviceUtilizationService.getTopDevicesByUtilization(null);
        
        // Then
        assertNotNull(result);
        verify(rentalDeviceMapper).selectTopDevicesByUtilization(5); // 默认TOP5
    }
    
    @Test
    void testGetTopDevicesByUtilization_WithZeroLimit_ShouldUseDefaultLimit() {
        // Given
        when(rentalDeviceMapper.selectTopDevicesByUtilization(5)).thenReturn(mockDeviceList);
        
        // When
        List<DeviceUtilizationDTO> result = deviceUtilizationService.getTopDevicesByUtilization(0);
        
        // Then
        assertNotNull(result);
        verify(rentalDeviceMapper).selectTopDevicesByUtilization(5);
    }
    
    @Test
    void testGetDeviceById_WithValidId_ShouldReturnDevice() {
        // Given
        Long deviceId = 1L;
        when(rentalDeviceMapper.selectById(deviceId)).thenReturn(mockDevice);
        
        // When
        RentalDevice result = deviceUtilizationService.getDeviceById(deviceId);
        
        // Then
        assertNotNull(result);
        assertEquals("YX-001", result.getDeviceId());
        assertEquals("YX-Robot-Pro", result.getDeviceModel());
        verify(rentalDeviceMapper).selectById(deviceId);
    }
    
    @Test
    void testGetDeviceById_WithNullId_ShouldReturnNull() {
        // When
        RentalDevice result = deviceUtilizationService.getDeviceById(null);
        
        // Then
        assertNull(result);
        verify(rentalDeviceMapper, never()).selectById(any());
    }
    
    @Test
    void testGetDeviceByDeviceId_WithValidDeviceId_ShouldReturnDevice() {
        // Given
        String deviceId = "YX-001";
        when(rentalDeviceMapper.selectByDeviceId(deviceId)).thenReturn(mockDevice);
        
        // When
        RentalDevice result = deviceUtilizationService.getDeviceByDeviceId(deviceId);
        
        // Then
        assertNotNull(result);
        assertEquals("YX-001", result.getDeviceId());
        verify(rentalDeviceMapper).selectByDeviceId(deviceId);
    }
    
    @Test
    void testGetDeviceByDeviceId_WithEmptyDeviceId_ShouldReturnNull() {
        // When
        RentalDevice result = deviceUtilizationService.getDeviceByDeviceId("");
        
        // Then
        assertNull(result);
        verify(rentalDeviceMapper, never()).selectByDeviceId(any());
    }
    
    @Test
    void testGetAllDeviceModels_ShouldReturnModelList() {
        // Given
        List<String> mockModels = Arrays.asList("YX-Robot-Pro", "YX-Robot-Standard", "YX-Robot-Lite");
        when(rentalDeviceMapper.selectAllDeviceModels()).thenReturn(mockModels);
        
        // When
        List<String> result = deviceUtilizationService.getAllDeviceModels();
        
        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("YX-Robot-Pro"));
        verify(rentalDeviceMapper).selectAllDeviceModels();
    }
    
    @Test
    void testGetAllRegions_ShouldReturnRegionList() {
        // Given
        List<String> mockRegions = Arrays.asList("北京", "上海", "广州", "深圳");
        when(rentalDeviceMapper.selectAllRegions()).thenReturn(mockRegions);
        
        // When
        List<String> result = deviceUtilizationService.getAllRegions();
        
        // Then
        assertNotNull(result);
        assertEquals(4, result.size());
        assertTrue(result.contains("北京"));
        verify(rentalDeviceMapper).selectAllRegions();
    }
    
    @Test
    void testUpdateDeviceStatus_WithValidParams_ShouldReturnTrue() {
        // Given
        String deviceId = "YX-001";
        String newStatus = "maintenance";
        when(rentalDeviceMapper.updateDeviceStatus(deviceId, newStatus)).thenReturn(1);
        
        // When
        boolean result = deviceUtilizationService.updateDeviceStatus(deviceId, newStatus);
        
        // Then
        assertTrue(result);
        verify(rentalDeviceMapper).updateDeviceStatus(deviceId, newStatus);
    }
    
    @Test
    void testUpdateDeviceStatus_WithEmptyDeviceId_ShouldReturnFalse() {
        // When
        boolean result = deviceUtilizationService.updateDeviceStatus("", "active");
        
        // Then
        assertFalse(result);
        verify(rentalDeviceMapper, never()).updateDeviceStatus(any(), any());
    }
    
    @Test
    void testUpdateDeviceStatus_WithEmptyStatus_ShouldReturnFalse() {
        // When
        boolean result = deviceUtilizationService.updateDeviceStatus("YX-001", "");
        
        // Then
        assertFalse(result);
        verify(rentalDeviceMapper, never()).updateDeviceStatus(any(), any());
    }
    
    @Test
    void testUpdateDeviceStatus_WithZeroRowsAffected_ShouldReturnFalse() {
        // Given
        String deviceId = "YX-001";
        String newStatus = "active";
        when(rentalDeviceMapper.updateDeviceStatus(deviceId, newStatus)).thenReturn(0);
        
        // When
        boolean result = deviceUtilizationService.updateDeviceStatus(deviceId, newStatus);
        
        // Then
        assertFalse(result);
    }
    
    @Test
    void testBatchUpdateDeviceStatus_WithValidIds_ShouldReturnSuccessCount() {
        // Given
        List<String> deviceIds = Arrays.asList("YX-001", "YX-002", "YX-003");
        String newStatus = "maintenance";
        
        when(rentalDeviceMapper.updateDeviceStatus("YX-001", newStatus)).thenReturn(1);
        when(rentalDeviceMapper.updateDeviceStatus("YX-002", newStatus)).thenReturn(1);
        when(rentalDeviceMapper.updateDeviceStatus("YX-003", newStatus)).thenReturn(0); // 失败
        
        // When
        int result = deviceUtilizationService.batchUpdateDeviceStatus(deviceIds, newStatus);
        
        // Then
        assertEquals(2, result); // 2个成功，1个失败
        verify(rentalDeviceMapper, times(3)).updateDeviceStatus(anyString(), eq(newStatus));
    }
    
    @Test
    void testSoftDeleteDevice_WithValidDeviceId_ShouldReturnTrue() {
        // Given
        String deviceId = "YX-001";
        when(rentalDeviceMapper.softDeleteByDeviceId(deviceId)).thenReturn(1);
        
        // When
        boolean result = deviceUtilizationService.softDeleteDevice(deviceId);
        
        // Then
        assertTrue(result);
        verify(rentalDeviceMapper).softDeleteByDeviceId(deviceId);
    }
    
    @Test
    void testBatchSoftDeleteDevices_WithValidIds_ShouldReturnSuccessCount() {
        // Given
        List<String> deviceIds = Arrays.asList("YX-001", "YX-002");
        
        when(rentalDeviceMapper.softDeleteByDeviceId("YX-001")).thenReturn(1);
        when(rentalDeviceMapper.softDeleteByDeviceId("YX-002")).thenReturn(1);
        
        // When
        int result = deviceUtilizationService.batchSoftDeleteDevices(deviceIds);
        
        // Then
        assertEquals(2, result);
        verify(rentalDeviceMapper, times(2)).softDeleteByDeviceId(anyString());
    }
    
    @Test
    void testBuildAdvancedSearchParams_WithCompleteRequest_ShouldBuildCorrectParams() {
        // Given
        Map<String, Object> searchRequest = new HashMap<>();
        searchRequest.put("page", 2);
        searchRequest.put("pageSize", 15);
        searchRequest.put("keyword", "YX");
        searchRequest.put("deviceModel", "YX-Robot-Pro");
        searchRequest.put("currentStatus", "active");
        searchRequest.put("minUtilization", 50.0);
        searchRequest.put("maxUtilization", 90.0);
        
        // When
        Map<String, Object> result = deviceUtilizationService.buildAdvancedSearchParams(searchRequest);
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.get("page"));
        assertEquals(15, result.get("pageSize"));
        assertEquals(15, result.get("offset")); // (2-1)*15
        assertEquals("YX", result.get("keyword"));
        assertEquals("YX-Robot-Pro", result.get("deviceModel"));
        assertEquals("active", result.get("currentStatus"));
        assertEquals(50.0, result.get("minUtilization"));
        assertEquals(90.0, result.get("maxUtilization"));
    }
    
    @Test
    void testBuildAdvancedSearchParams_WithEmptyRequest_ShouldReturnEmptyParams() {
        // Given
        Map<String, Object> searchRequest = new HashMap<>();
        
        // When
        Map<String, Object> result = deviceUtilizationService.buildAdvancedSearchParams(searchRequest);
        
        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    
    @Test
    void testGetIntegerValue_WithDifferentTypes_ShouldConvertCorrectly() {
        // 通过测试getDeviceStatusStatsDTO方法间接测试getIntegerValue方法
        
        // Given - 测试不同类型的数值转换
        Map<String, Object> mixedTypeStats = new HashMap<>();
        mixedTypeStats.put("active", 65L); // Long
        mixedTypeStats.put("idle", "15"); // String
        mixedTypeStats.put("maintenance", 8.0); // Double
        
        when(rentalDeviceMapper.selectDeviceStatusStats()).thenReturn(mixedTypeStats);
        
        // When
        DeviceStatusStatsDTO result = deviceUtilizationService.getDeviceStatusStatsDTO();
        
        // Then
        assertNotNull(result);
        assertEquals(Integer.valueOf(65), result.getActive());
        assertEquals(Integer.valueOf(15), result.getIdle());
        assertEquals(Integer.valueOf(8), result.getMaintenance());
    }
    
    @Test
    void testGetIntegerValue_WithInvalidData_ShouldReturnZero() {
        // Given - 测试无效数据的处理
        Map<String, Object> invalidStats = new HashMap<>();
        invalidStats.put("active", null);
        invalidStats.put("idle", "invalid");
        invalidStats.put("maintenance", new HashMap<>()); // 无法转换的对象
        
        when(rentalDeviceMapper.selectDeviceStatusStats()).thenReturn(invalidStats);
        
        // When
        DeviceStatusStatsDTO result = deviceUtilizationService.getDeviceStatusStatsDTO();
        
        // Then
        assertNotNull(result);
        assertEquals(Integer.valueOf(0), result.getActive());
        assertEquals(Integer.valueOf(0), result.getIdle());
        assertEquals(Integer.valueOf(0), result.getMaintenance());
    }
}