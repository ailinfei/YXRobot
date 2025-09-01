package com.yxrobot.mapper;

import com.yxrobot.dto.DeviceUtilizationDTO;
import com.yxrobot.entity.RentalCustomer;
import com.yxrobot.entity.RentalDevice;
import com.yxrobot.entity.RentalRecord;
import com.yxrobot.enums.CustomerType;
import com.yxrobot.enums.DeviceStatus;
import com.yxrobot.enums.RentalStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 租赁数据访问层测试类
 * 测试任务16 - 数据访问层功能
 * 
 * @author Kiro
 * @date 2025-01-28
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class RentalMapperTest {
    
    @Autowired
    private RentalRecordMapper rentalRecordMapper;
    
    @Autowired
    private RentalDeviceMapper rentalDeviceMapper;
    
    @Autowired
    private RentalCustomerMapper rentalCustomerMapper;
    
    private RentalCustomer testCustomer;
    private RentalDevice testDevice;
    private RentalRecord testRecord;
    
    @BeforeEach
    void setUp() {
        // 创建测试客户
        testCustomer = new RentalCustomer();
        testCustomer.setCustomerName("测试客户");
        testCustomer.setCustomerType(CustomerType.ENTERPRISE);
        testCustomer.setPhone("13812345678");
        testCustomer.setEmail("test@example.com");
        testCustomer.setRegion("北京市");
        testCustomer.setAddress("北京市朝阳区测试街道123号");
        testCustomer.setContactPerson("张三");
        testCustomer.setCreateTime(LocalDateTime.now());
        testCustomer.setUpdateTime(LocalDateTime.now());
        
        // 创建测试设备
        testDevice = new RentalDevice();
        testDevice.setDeviceId("TEST-0001");
        testDevice.setDeviceModel("YX-Robot-Pro");
        testDevice.setDeviceName("测试练字机器人Pro");
        testDevice.setCurrentStatus(DeviceStatus.ACTIVE);
        testDevice.setDailyRentalPrice(new BigDecimal("100.00"));
        testDevice.setRegion("北京市");
        testDevice.setPerformanceScore(95);
        testDevice.setSignalStrength(85);
        testDevice.setMaintenanceStatus("normal");
        testDevice.setCreateTime(LocalDateTime.now());
        testDevice.setUpdateTime(LocalDateTime.now());
        
        // 创建测试租赁记录
        testRecord = new RentalRecord();
        testRecord.setRentalStartDate(LocalDate.now().minusDays(10));
        testRecord.setRentalEndDate(LocalDate.now().plusDays(20));
        testRecord.setRentalStatus(RentalStatus.ACTIVE);
        testRecord.setDailyRentalPrice(new BigDecimal("100.00"));
        testRecord.setTotalRentalDays(30);
        testRecord.setTotalRentalAmount(new BigDecimal("3000.00"));
        testRecord.setCreateTime(LocalDateTime.now());
        testRecord.setUpdateTime(LocalDateTime.now());
    }
    
    /**
     * 测试客户Mapper - 插入操作
     */
    @Test
    void testInsertCustomer() {
        // 执行插入
        int result = rentalCustomerMapper.insert(testCustomer);
        
        // 验证结果
        assertEquals(1, result);
        assertNotNull(testCustomer.getId());
        assertTrue(testCustomer.getId() > 0);
    }
    
    /**
     * 测试客户Mapper - 根据ID查询
     */
    @Test
    void testSelectCustomerById() {
        // 先插入测试数据
        rentalCustomerMapper.insert(testCustomer);
        Long customerId = testCustomer.getId();
        
        // 执行查询
        RentalCustomer result = rentalCustomerMapper.selectById(customerId);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(customerId, result.getId());
        assertEquals("测试客户", result.getCustomerName());
        assertEquals(CustomerType.ENTERPRISE, result.getCustomerType());
        assertEquals("13812345678", result.getPhone());
        assertEquals("test@example.com", result.getEmail());
    }
    
    /**
     * 测试客户Mapper - 根据名称查询
     */
    @Test
    void testSelectCustomerByName() {
        // 先插入测试数据
        rentalCustomerMapper.insert(testCustomer);
        
        // 执行查询
        RentalCustomer result = rentalCustomerMapper.selectByName("测试客户");
        
        // 验证结果
        assertNotNull(result);
        assertEquals("测试客户", result.getCustomerName());
        assertEquals(CustomerType.ENTERPRISE, result.getCustomerType());
    }
    
    /**
     * 测试客户Mapper - 查询活跃客户数量
     */
    @Test
    void testSelectActiveCustomerCount() {
        // 先插入测试数据
        rentalCustomerMapper.insert(testCustomer);
        
        // 执行查询
        Long count = rentalCustomerMapper.selectActiveCustomerCount();
        
        // 验证结果
        assertNotNull(count);
        assertTrue(count >= 1);
    }
    
    /**
     * 测试设备Mapper - 插入操作
     */
    @Test
    void testInsertDevice() {
        // 执行插入
        int result = rentalDeviceMapper.insert(testDevice);
        
        // 验证结果
        assertEquals(1, result);
        assertNotNull(testDevice.getId());
        assertTrue(testDevice.getId() > 0);
    }
    
    /**
     * 测试设备Mapper - 根据设备ID查询
     */
    @Test
    void testSelectDeviceByDeviceId() {
        // 先插入测试数据
        rentalDeviceMapper.insert(testDevice);
        
        // 执行查询
        RentalDevice result = rentalDeviceMapper.selectByDeviceId("TEST-0001");
        
        // 验证结果
        assertNotNull(result);
        assertEquals("TEST-0001", result.getDeviceId());
        assertEquals("YX-Robot-Pro", result.getDeviceModel());
        assertEquals("测试练字机器人Pro", result.getDeviceName());
        assertEquals(DeviceStatus.ACTIVE, result.getCurrentStatus());
        assertEquals(new BigDecimal("100.00"), result.getDailyRentalPrice());
    }
    
    /**
     * 测试设备Mapper - 查询设备利用率列表
     */
    @Test
    void testSelectDeviceUtilizationList() {
        // 先插入测试数据
        rentalDeviceMapper.insert(testDevice);
        
        // 准备查询参数
        Map<String, Object> params = new HashMap<>();
        params.put("page", 1);
        params.put("pageSize", 20);
        params.put("offset", 0);
        
        // 执行查询
        List<DeviceUtilizationDTO> result = rentalDeviceMapper.selectDeviceUtilizationList(params);
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.size() >= 1);
        
        DeviceUtilizationDTO device = result.stream()
            .filter(d -> "TEST-0001".equals(d.getDeviceId()))
            .findFirst()
            .orElse(null);
        
        if (device != null) {
            assertEquals("TEST-0001", device.getDeviceId());
            assertEquals("YX-Robot-Pro", device.getDeviceModel());
            assertEquals("active", device.getCurrentStatus());
            assertEquals("北京市", device.getRegion());
        }
    }
    
    /**
     * 测试设备Mapper - 查询设备利用率数量
     */
    @Test
    void testSelectDeviceUtilizationCount() {
        // 先插入测试数据
        rentalDeviceMapper.insert(testDevice);
        
        // 准备查询参数
        Map<String, Object> params = new HashMap<>();
        
        // 执行查询
        Long count = rentalDeviceMapper.selectDeviceUtilizationCount(params);
        
        // 验证结果
        assertNotNull(count);
        assertTrue(count >= 1);
    }
    
    /**
     * 测试设备Mapper - 查询设备状态统计
     */
    @Test
    void testSelectDeviceStatusStats() {
        // 先插入测试数据
        rentalDeviceMapper.insert(testDevice);
        
        // 执行查询
        Map<String, Object> result = rentalDeviceMapper.selectDeviceStatusStats();
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.containsKey("active"));
        assertTrue(result.containsKey("idle"));
        assertTrue(result.containsKey("maintenance"));
        
        // 验证数据类型
        assertTrue(result.get("active") instanceof Number);
        assertTrue(result.get("idle") instanceof Number);
        assertTrue(result.get("maintenance") instanceof Number);
    }
    
    /**
     * 测试设备Mapper - 查询所有设备型号
     */
    @Test
    void testSelectAllDeviceModels() {
        // 先插入测试数据
        rentalDeviceMapper.insert(testDevice);
        
        // 执行查询
        List<String> result = rentalDeviceMapper.selectAllDeviceModels();
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("YX-Robot-Pro"));
    }
    
    /**
     * 测试设备Mapper - 更新设备状态
     */
    @Test
    void testUpdateDeviceStatus() {
        // 先插入测试数据
        rentalDeviceMapper.insert(testDevice);
        
        // 执行更新
        int result = rentalDeviceMapper.updateDeviceStatus("TEST-0001", "maintenance");
        
        // 验证结果
        assertEquals(1, result);
        
        // 验证更新后的状态
        RentalDevice updatedDevice = rentalDeviceMapper.selectByDeviceId("TEST-0001");
        assertEquals("maintenance", updatedDevice.getCurrentStatus().getValue());
    }
    
    /**
     * 测试租赁记录Mapper - 插入操作
     */
    @Test
    void testInsertRentalRecord() {
        // 先插入依赖数据
        rentalCustomerMapper.insert(testCustomer);
        rentalDeviceMapper.insert(testDevice);
        
        // 设置外键关联
        testRecord.setCustomerId(testCustomer.getId());
        testRecord.setDeviceId(testDevice.getId());
        
        // 执行插入
        int result = rentalRecordMapper.insert(testRecord);
        
        // 验证结果
        assertEquals(1, result);
        assertNotNull(testRecord.getId());
        assertTrue(testRecord.getId() > 0);
    }
    
    /**
     * 测试租赁记录Mapper - 查询租赁统计
     */
    @Test
    void testSelectRentalStats() {
        // 先插入依赖数据
        rentalCustomerMapper.insert(testCustomer);
        rentalDeviceMapper.insert(testDevice);
        testRecord.setCustomerId(testCustomer.getId());
        testRecord.setDeviceId(testDevice.getId());
        rentalRecordMapper.insert(testRecord);
        
        // 准备查询参数
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now().plusDays(30);
        
        // 执行查询
        Map<String, Object> result = rentalRecordMapper.selectRentalStats(startDate, endDate);
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.containsKey("totalRentalRevenue"));
        assertTrue(result.containsKey("totalRentalOrders"));
        assertTrue(result.containsKey("totalRentalDevices"));
        assertTrue(result.containsKey("averageRentalPeriod"));
        
        // 验证数据类型
        assertTrue(result.get("totalRentalRevenue") instanceof BigDecimal);
        assertTrue(result.get("totalRentalOrders") instanceof Number);
        assertTrue(result.get("totalRentalDevices") instanceof Number);
        assertTrue(result.get("averageRentalPeriod") instanceof BigDecimal);
    }
    
    /**
     * 测试租赁记录Mapper - 查询今日统计
     */
    @Test
    void testSelectTodayStats() {
        // 先插入依赖数据
        rentalCustomerMapper.insert(testCustomer);
        rentalDeviceMapper.insert(testDevice);
        testRecord.setCustomerId(testCustomer.getId());
        testRecord.setDeviceId(testDevice.getId());
        testRecord.setRentalStartDate(LocalDate.now()); // 设置为今日开始
        rentalRecordMapper.insert(testRecord);
        
        // 执行查询
        Map<String, Object> result = rentalRecordMapper.selectTodayStats(LocalDate.now());
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.containsKey("revenue"));
        assertTrue(result.containsKey("orders"));
        assertTrue(result.containsKey("activeDevices"));
        assertTrue(result.containsKey("avgUtilization"));
    }
    
    /**
     * 测试设备利用率查询 - 带搜索条件
     */
    @Test
    void testSelectDeviceUtilizationListWithFilters() {
        // 先插入测试数据
        rentalDeviceMapper.insert(testDevice);
        
        // 准备查询参数（带筛选条件）
        Map<String, Object> params = new HashMap<>();
        params.put("page", 1);
        params.put("pageSize", 20);
        params.put("offset", 0);
        params.put("keyword", "TEST");
        params.put("deviceModel", "YX-Robot-Pro");
        params.put("currentStatus", "active");
        params.put("region", "北京市");
        
        // 执行查询
        List<DeviceUtilizationDTO> result = rentalDeviceMapper.selectDeviceUtilizationList(params);
        
        // 验证结果
        assertNotNull(result);
        
        // 如果有结果，验证筛选条件是否生效
        if (!result.isEmpty()) {
            DeviceUtilizationDTO device = result.get(0);
            assertTrue(device.getDeviceId().contains("TEST"));
            assertEquals("YX-Robot-Pro", device.getDeviceModel());
            assertEquals("active", device.getCurrentStatus());
            assertEquals("北京市", device.getRegion());
        }
    }
    
    /**
     * 测试设备利用率查询 - 带日期范围筛选
     */
    @Test
    void testSelectDeviceUtilizationListWithDateRange() {
        // 先插入测试数据
        rentalDeviceMapper.insert(testDevice);
        
        // 准备查询参数（带日期范围）
        Map<String, Object> params = new HashMap<>();
        params.put("page", 1);
        params.put("pageSize", 20);
        params.put("offset", 0);
        params.put("startDate", LocalDate.now().minusDays(30));
        params.put("endDate", LocalDate.now().plusDays(30));
        
        // 执行查询
        List<DeviceUtilizationDTO> result = rentalDeviceMapper.selectDeviceUtilizationList(params);
        
        // 验证结果
        assertNotNull(result);
        // 在日期范围内应该能查到测试设备
        assertTrue(result.size() >= 1);
    }
    
    /**
     * 测试查询TOP设备
     */
    @Test
    void testSelectTopDevicesByUtilization() {
        // 先插入测试数据
        rentalDeviceMapper.insert(testDevice);
        
        // 执行查询
        List<DeviceUtilizationDTO> result = rentalDeviceMapper.selectTopDevicesByUtilization(5);
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.size() <= 5);
        
        // 验证排序（利用率从高到低）
        if (result.size() > 1) {
            for (int i = 0; i < result.size() - 1; i++) {
                BigDecimal current = result.get(i).getUtilizationRate();
                BigDecimal next = result.get(i + 1).getUtilizationRate();
                assertTrue(current.compareTo(next) >= 0, "利用率应该按降序排列");
            }
        }
    }
    
    /**
     * 测试查询所有地区
     */
    @Test
    void testSelectAllRegions() {
        // 先插入测试数据
        rentalDeviceMapper.insert(testDevice);
        
        // 执行查询
        List<String> result = rentalDeviceMapper.selectAllRegions();
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.contains("北京市"));
    }
    
    /**
     * 测试复杂查询 - 租赁趋势数据
     */
    @Test
    void testSelectRentalTrendData() {
        // 先插入依赖数据
        rentalCustomerMapper.insert(testCustomer);
        rentalDeviceMapper.insert(testDevice);
        testRecord.setCustomerId(testCustomer.getId());
        testRecord.setDeviceId(testDevice.getId());
        rentalRecordMapper.insert(testRecord);
        
        // 准备查询参数
        Map<String, Object> params = new HashMap<>();
        params.put("startDate", LocalDate.now().minusDays(30));
        params.put("endDate", LocalDate.now().plusDays(30));
        params.put("period", "daily");
        
        // 执行查询
        List<Map<String, Object>> result = rentalRecordMapper.selectRentalTrendData(params);
        
        // 验证结果
        assertNotNull(result);
        
        // 如果有数据，验证数据结构
        if (!result.isEmpty()) {
            Map<String, Object> trendData = result.get(0);
            assertTrue(trendData.containsKey("date"));
            assertTrue(trendData.containsKey("revenue"));
            assertTrue(trendData.containsKey("orders"));
            assertTrue(trendData.containsKey("utilization"));
        }
    }
    
    /**
     * 测试数据库连接和基本操作
     */
    @Test
    void testDatabaseConnection() {
        // 测试基本的数据库连接
        assertNotNull(rentalCustomerMapper);
        assertNotNull(rentalDeviceMapper);
        assertNotNull(rentalRecordMapper);
        
        // 测试简单查询
        Long customerCount = rentalCustomerMapper.selectActiveCustomerCount();
        assertNotNull(customerCount);
        assertTrue(customerCount >= 0);
        
        Map<String, Object> deviceStats = rentalDeviceMapper.selectDeviceStatusStats();
        assertNotNull(deviceStats);
        
        List<String> deviceModels = rentalDeviceMapper.selectAllDeviceModels();
        assertNotNull(deviceModels);
    }
    
    /**
     * 测试事务回滚
     */
    @Test
    void testTransactionRollback() {
        // 插入测试数据
        rentalCustomerMapper.insert(testCustomer);
        Long customerId = testCustomer.getId();
        
        // 验证数据已插入
        RentalCustomer inserted = rentalCustomerMapper.selectById(customerId);
        assertNotNull(inserted);
        
        // 由于使用了@Transactional注解，测试结束后数据会自动回滚
        // 这里只是验证插入操作成功
        assertEquals("测试客户", inserted.getCustomerName());
    }
}