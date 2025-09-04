package com.yxrobot.mapper;

import com.yxrobot.dto.ManagedDeviceSearchCriteria;
import com.yxrobot.entity.ManagedDevice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ManagedDeviceMapper 单元测试类
 * 
 * 测试覆盖范围：
 * - 数据库CRUD操作
 * - 复杂查询和筛选
 * - 分页查询
 * - 字段映射验证
 * - 数据完整性验证
 * 
 * 测试目标覆盖率：
 * - 行覆盖率 ≥ 80%
 * - 分支覆盖率 ≥ 75%
 * - 方法覆盖率 ≥ 85%
 */
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
@DisplayName("设备管理数据访问层单元测试")
public class ManagedDeviceMapperTest {

    @Autowired
    private ManagedDeviceMapper managedDeviceMapper;

    private ManagedDevice testDevice;
    private ManagedDeviceSearchCriteria testCriteria;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        testDevice = new ManagedDevice();
        testDevice.setSerialNumber("YX-TEST-MAPPER-001");
        testDevice.setModel("YX-EDU-2024");
        testDevice.setStatus("online");
        testDevice.setFirmwareVersion("1.0.0");
        testDevice.setCustomerId(1L);
        testDevice.setCustomerName("[测试客户]");
        testDevice.setCustomerPhone("[测试电话]");
        testDevice.setCreatedAt(LocalDateTime.now());
        testDevice.setUpdatedAt(LocalDateTime.now());
        testDevice.setCreatedBy("test-user");
        testDevice.setNotes("测试设备备注");
        testDevice.setIsDeleted(0);

        testCriteria = new ManagedDeviceSearchCriteria();
        testCriteria.setPage(1);
        testCriteria.setPageSize(20);
    }

    @Test
    @DisplayName("插入设备 - 成功场景")
    void testInsert_Success() {
        // When
        int result = managedDeviceMapper.insert(testDevice);

        // Then
        assertEquals(1, result);
        assertNotNull(testDevice.getId());
        assertTrue(testDevice.getId() > 0);
    }

    @Test
    @DisplayName("根据ID查询设备 - 成功场景")
    void testSelectById_Success() {
        // Given
        managedDeviceMapper.insert(testDevice);
        Long deviceId = testDevice.getId();

        // When
        ManagedDevice result = managedDeviceMapper.selectById(deviceId);

        // Then
        assertNotNull(result);
        assertEquals(deviceId, result.getId());
        assertEquals("YX-TEST-MAPPER-001", result.getSerialNumber());
        assertEquals("YX-EDU-2024", result.getModel());
        assertEquals("online", result.getStatus());
        assertEquals("1.0.0", result.getFirmwareVersion());
        assertEquals(1L, result.getCustomerId());
        assertEquals("[测试客户]", result.getCustomerName());
        assertEquals("[测试电话]", result.getCustomerPhone());
        assertEquals("test-user", result.getCreatedBy());
        assertEquals("测试设备备注", result.getNotes());
        assertEquals(0, result.getIsDeleted());
    }

    @Test
    @DisplayName("根据ID查询设备 - 设备不存在")
    void testSelectById_NotFound() {
        // When
        ManagedDevice result = managedDeviceMapper.selectById(999999L);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("根据序列号查询设备 - 成功场景")
    void testSelectBySerialNumber_Success() {
        // Given
        managedDeviceMapper.insert(testDevice);

        // When
        ManagedDevice result = managedDeviceMapper.selectBySerialNumber("YX-TEST-MAPPER-001");

        // Then
        assertNotNull(result);
        assertEquals("YX-TEST-MAPPER-001", result.getSerialNumber());
        assertEquals("YX-EDU-2024", result.getModel());
    }

    @Test
    @DisplayName("根据序列号查询设备 - 设备不存在")
    void testSelectBySerialNumber_NotFound() {
        // When
        ManagedDevice result = managedDeviceMapper.selectBySerialNumber("NON-EXISTENT-SERIAL");

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("更新设备 - 成功场景")
    void testUpdateById_Success() {
        // Given
        managedDeviceMapper.insert(testDevice);
        Long deviceId = testDevice.getId();

        // 修改设备信息
        testDevice.setSerialNumber("YX-TEST-MAPPER-001-UPDATED");
        testDevice.setModel("YX-PRO-2024");
        testDevice.setStatus("offline");
        testDevice.setFirmwareVersion("1.1.0");
        testDevice.setNotes("更新后的设备备注");
        testDevice.setUpdatedAt(LocalDateTime.now());

        // When
        int result = managedDeviceMapper.updateById(testDevice);

        // Then
        assertEquals(1, result);

        // 验证更新结果
        ManagedDevice updatedDevice = managedDeviceMapper.selectById(deviceId);
        assertNotNull(updatedDevice);
        assertEquals("YX-TEST-MAPPER-001-UPDATED", updatedDevice.getSerialNumber());
        assertEquals("YX-PRO-2024", updatedDevice.getModel());
        assertEquals("offline", updatedDevice.getStatus());
        assertEquals("1.1.0", updatedDevice.getFirmwareVersion());
        assertEquals("更新后的设备备注", updatedDevice.getNotes());
    }

    @Test
    @DisplayName("删除设备 - 成功场景")
    void testDeleteById_Success() {
        // Given
        managedDeviceMapper.insert(testDevice);
        Long deviceId = testDevice.getId();

        // When
        int result = managedDeviceMapper.deleteById(deviceId);

        // Then
        assertEquals(1, result);

        // 验证删除结果（软删除）
        ManagedDevice deletedDevice = managedDeviceMapper.selectById(deviceId);
        if (deletedDevice != null) {
            assertEquals(1, deletedDevice.getIsDeleted());
        }
    }

    @Test
    @DisplayName("搜索条件查询 - 无条件查询")
    void testSelectBySearchCriteria_NoConditions() {
        // Given
        managedDeviceMapper.insert(testDevice);

        // When
        List<ManagedDevice> result = managedDeviceMapper.selectBySearchCriteria(testCriteria);

        // Then
        assertNotNull(result);
        assertTrue(result.size() >= 1);
        
        // 验证分页
        assertTrue(result.size() <= testCriteria.getPageSize());
    }

    @Test
    @DisplayName("搜索条件查询 - 关键词搜索")
    void testSelectBySearchCriteria_KeywordSearch() {
        // Given
        managedDeviceMapper.insert(testDevice);
        testCriteria.setKeyword("YX-TEST-MAPPER");

        // When
        List<ManagedDevice> result = managedDeviceMapper.selectBySearchCriteria(testCriteria);

        // Then
        assertNotNull(result);
        assertTrue(result.size() >= 1);
        
        // 验证搜索结果包含关键词
        boolean found = result.stream()
            .anyMatch(device -> device.getSerialNumber().contains("YX-TEST-MAPPER"));
        assertTrue(found);
    }

    @Test
    @DisplayName("搜索条件查询 - 状态筛选")
    void testSelectBySearchCriteria_StatusFilter() {
        // Given
        managedDeviceMapper.insert(testDevice);
        testCriteria.setStatus("online");

        // When
        List<ManagedDevice> result = managedDeviceMapper.selectBySearchCriteria(testCriteria);

        // Then
        assertNotNull(result);
        
        // 验证所有结果都是在线状态
        result.forEach(device -> assertEquals("online", device.getStatus()));
    }

    @Test
    @DisplayName("搜索条件查询 - 型号筛选")
    void testSelectBySearchCriteria_ModelFilter() {
        // Given
        managedDeviceMapper.insert(testDevice);
        testCriteria.setModel("YX-EDU-2024");

        // When
        List<ManagedDevice> result = managedDeviceMapper.selectBySearchCriteria(testCriteria);

        // Then
        assertNotNull(result);
        
        // 验证所有结果都是指定型号
        result.forEach(device -> assertEquals("YX-EDU-2024", device.getModel()));
    }

    @Test
    @DisplayName("搜索条件查询 - 客户筛选")
    void testSelectBySearchCriteria_CustomerFilter() {
        // Given
        managedDeviceMapper.insert(testDevice);
        testCriteria.setCustomerId(1L);

        // When
        List<ManagedDevice> result = managedDeviceMapper.selectBySearchCriteria(testCriteria);

        // Then
        assertNotNull(result);
        
        // 验证所有结果都属于指定客户
        result.forEach(device -> assertEquals(1L, device.getCustomerId()));
    }

    @Test
    @DisplayName("搜索条件查询 - 复合条件")
    void testSelectBySearchCriteria_MultipleConditions() {
        // Given
        managedDeviceMapper.insert(testDevice);
        testCriteria.setKeyword("YX-TEST");
        testCriteria.setStatus("online");
        testCriteria.setModel("YX-EDU-2024");
        testCriteria.setCustomerId(1L);

        // When
        List<ManagedDevice> result = managedDeviceMapper.selectBySearchCriteria(testCriteria);

        // Then
        assertNotNull(result);
        
        // 验证结果符合所有条件
        result.forEach(device -> {
            assertTrue(device.getSerialNumber().contains("YX-TEST"));
            assertEquals("online", device.getStatus());
            assertEquals("YX-EDU-2024", device.getModel());
            assertEquals(1L, device.getCustomerId());
        });
    }

    @Test
    @DisplayName("统计查询 - 无条件统计")
    void testCountBySearchCriteria_NoConditions() {
        // Given
        managedDeviceMapper.insert(testDevice);

        // When
        Long count = managedDeviceMapper.countBySearchCriteria(testCriteria);

        // Then
        assertNotNull(count);
        assertTrue(count >= 1);
    }

    @Test
    @DisplayName("统计查询 - 带条件统计")
    void testCountBySearchCriteria_WithConditions() {
        // Given
        managedDeviceMapper.insert(testDevice);
        testCriteria.setStatus("online");

        // When
        Long count = managedDeviceMapper.countBySearchCriteria(testCriteria);

        // Then
        assertNotNull(count);
        assertTrue(count >= 1);
    }

    @Test
    @DisplayName("分页查询 - 第一页")
    void testPagination_FirstPage() {
        // Given
        // 插入多个测试设备
        for (int i = 1; i <= 25; i++) {
            ManagedDevice device = new ManagedDevice();
            device.setSerialNumber("YX-TEST-PAGE-" + String.format("%03d", i));
            device.setModel("YX-EDU-2024");
            device.setStatus("online");
            device.setFirmwareVersion("1.0.0");
            device.setCustomerId(1L);
            device.setCustomerName("[测试客户]");
            device.setCustomerPhone("[测试电话]");
            device.setCreatedAt(LocalDateTime.now());
            device.setUpdatedAt(LocalDateTime.now());
            device.setIsDeleted(0);
            managedDeviceMapper.insert(device);
        }

        testCriteria.setPage(1);
        testCriteria.setPageSize(10);

        // When
        List<ManagedDevice> result = managedDeviceMapper.selectBySearchCriteria(testCriteria);

        // Then
        assertNotNull(result);
        assertTrue(result.size() <= 10);
    }

    @Test
    @DisplayName("分页查询 - 第二页")
    void testPagination_SecondPage() {
        // Given
        // 插入多个测试设备
        for (int i = 1; i <= 25; i++) {
            ManagedDevice device = new ManagedDevice();
            device.setSerialNumber("YX-TEST-PAGE2-" + String.format("%03d", i));
            device.setModel("YX-EDU-2024");
            device.setStatus("online");
            device.setFirmwareVersion("1.0.0");
            device.setCustomerId(1L);
            device.setCustomerName("[测试客户]");
            device.setCustomerPhone("[测试电话]");
            device.setCreatedAt(LocalDateTime.now());
            device.setUpdatedAt(LocalDateTime.now());
            device.setIsDeleted(0);
            managedDeviceMapper.insert(device);
        }

        testCriteria.setPage(2);
        testCriteria.setPageSize(10);

        // When
        List<ManagedDevice> result = managedDeviceMapper.selectBySearchCriteria(testCriteria);

        // Then
        assertNotNull(result);
        assertTrue(result.size() <= 10);
    }

    @Test
    @DisplayName("字段映射验证 - 所有字段")
    void testFieldMapping_AllFields() {
        // Given
        testDevice.setLastOnlineAt(LocalDateTime.now().minusHours(1));
        testDevice.setActivatedAt(LocalDateTime.now().minusDays(1));
        managedDeviceMapper.insert(testDevice);
        Long deviceId = testDevice.getId();

        // When
        ManagedDevice result = managedDeviceMapper.selectById(deviceId);

        // Then
        assertNotNull(result);
        
        // 验证所有字段映射正确
        assertEquals(testDevice.getSerialNumber(), result.getSerialNumber());
        assertEquals(testDevice.getModel(), result.getModel());
        assertEquals(testDevice.getStatus(), result.getStatus());
        assertEquals(testDevice.getFirmwareVersion(), result.getFirmwareVersion());
        assertEquals(testDevice.getCustomerId(), result.getCustomerId());
        assertEquals(testDevice.getCustomerName(), result.getCustomerName());
        assertEquals(testDevice.getCustomerPhone(), result.getCustomerPhone());
        assertEquals(testDevice.getCreatedBy(), result.getCreatedBy());
        assertEquals(testDevice.getNotes(), result.getNotes());
        assertEquals(testDevice.getIsDeleted(), result.getIsDeleted());
        
        // 验证时间字段
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        assertNotNull(result.getLastOnlineAt());
        assertNotNull(result.getActivatedAt());
    }

    @Test
    @DisplayName("数据完整性验证 - 必填字段")
    void testDataIntegrity_RequiredFields() {
        // Given
        ManagedDevice deviceWithoutRequired = new ManagedDevice();
        // 只设置必填字段
        deviceWithoutRequired.setSerialNumber("YX-REQUIRED-TEST");
        deviceWithoutRequired.setModel("YX-EDU-2024");
        deviceWithoutRequired.setStatus("offline");
        deviceWithoutRequired.setFirmwareVersion("1.0.0");
        deviceWithoutRequired.setCustomerId(1L);
        deviceWithoutRequired.setCustomerName("[测试客户]");
        deviceWithoutRequired.setCustomerPhone("[测试电话]");
        deviceWithoutRequired.setCreatedAt(LocalDateTime.now());
        deviceWithoutRequired.setUpdatedAt(LocalDateTime.now());
        deviceWithoutRequired.setIsDeleted(0);

        // When
        int result = managedDeviceMapper.insert(deviceWithoutRequired);

        // Then
        assertEquals(1, result);
        assertNotNull(deviceWithoutRequired.getId());
    }

    @Test
    @DisplayName("排序验证 - 按创建时间排序")
    void testSorting_ByCreatedAt() {
        // Given
        // 插入多个设备，时间间隔1秒
        for (int i = 1; i <= 3; i++) {
            ManagedDevice device = new ManagedDevice();
            device.setSerialNumber("YX-SORT-" + i);
            device.setModel("YX-EDU-2024");
            device.setStatus("online");
            device.setFirmwareVersion("1.0.0");
            device.setCustomerId(1L);
            device.setCustomerName("[测试客户]");
            device.setCustomerPhone("[测试电话]");
            device.setCreatedAt(LocalDateTime.now().plusSeconds(i));
            device.setUpdatedAt(LocalDateTime.now().plusSeconds(i));
            device.setIsDeleted(0);
            managedDeviceMapper.insert(device);
        }

        testCriteria.setSortBy("created_at");
        testCriteria.setSortOrder("desc");

        // When
        List<ManagedDevice> result = managedDeviceMapper.selectBySearchCriteria(testCriteria);

        // Then
        assertNotNull(result);
        assertTrue(result.size() >= 3);
        
        // 验证排序（最新的在前）
        for (int i = 0; i < result.size() - 1; i++) {
            assertTrue(result.get(i).getCreatedAt().isAfter(result.get(i + 1).getCreatedAt()) ||
                      result.get(i).getCreatedAt().isEqual(result.get(i + 1).getCreatedAt()));
        }
    }

    @Test
    @DisplayName("性能测试 - 大数据量查询")
    void testPerformance_LargeDataSet() {
        // Given
        // 插入大量测试数据
        for (int i = 1; i <= 100; i++) {
            ManagedDevice device = new ManagedDevice();
            device.setSerialNumber("YX-PERF-" + String.format("%03d", i));
            device.setModel(i % 3 == 0 ? "YX-PRO-2024" : "YX-EDU-2024");
            device.setStatus(i % 2 == 0 ? "online" : "offline");
            device.setFirmwareVersion("1.0.0");
            device.setCustomerId((long) (i % 5 + 1));
            device.setCustomerName("[测试客户" + (i % 5 + 1) + "]");
            device.setCustomerPhone("[测试电话" + (i % 5 + 1) + "]");
            device.setCreatedAt(LocalDateTime.now().minusDays(i % 30));
            device.setUpdatedAt(LocalDateTime.now().minusDays(i % 30));
            device.setIsDeleted(0);
            managedDeviceMapper.insert(device);
        }

        testCriteria.setPageSize(50);

        // When
        long startTime = System.currentTimeMillis();
        List<ManagedDevice> result = managedDeviceMapper.selectBySearchCriteria(testCriteria);
        Long count = managedDeviceMapper.countBySearchCriteria(testCriteria);
        long endTime = System.currentTimeMillis();

        // Then
        assertNotNull(result);
        assertNotNull(count);
        assertTrue(result.size() <= 50);
        assertTrue(count >= 100);
        
        // 性能断言：查询时间应该在合理范围内（小于1秒）
        long queryTime = endTime - startTime;
        assertTrue(queryTime < 1000, "查询时间应该小于1秒，实际：" + queryTime + "ms");
    }

    @Test
    @DisplayName("边界条件测试 - 空字符串处理")
    void testBoundaryConditions_EmptyStrings() {
        // Given
        testCriteria.setKeyword("");
        testCriteria.setStatus("");
        testCriteria.setModel("");

        // When
        List<ManagedDevice> result = managedDeviceMapper.selectBySearchCriteria(testCriteria);
        Long count = managedDeviceMapper.countBySearchCriteria(testCriteria);

        // Then
        assertNotNull(result);
        assertNotNull(count);
        // 空字符串应该被忽略，不影响查询结果
    }

    @Test
    @DisplayName("边界条件测试 - 极大页码")
    void testBoundaryConditions_LargePageNumber() {
        // Given
        testCriteria.setPage(999999);
        testCriteria.setPageSize(20);

        // When
        List<ManagedDevice> result = managedDeviceMapper.selectBySearchCriteria(testCriteria);

        // Then
        assertNotNull(result);
        // 超出范围的页码应该返回空结果
        assertTrue(result.isEmpty());
    }
}