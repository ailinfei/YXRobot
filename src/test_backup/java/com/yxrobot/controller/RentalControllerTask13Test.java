package com.yxrobot.controller;

import com.yxrobot.service.RentalCustomerService;
import com.yxrobot.service.DeviceUtilizationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 租赁控制器任务13功能测试类
 * 测试辅助功能控制器接口
 * 
 * @author Kiro
 * @date 2025-01-28
 */
public class RentalControllerTask13Test {
    
    @Mock
    private RentalCustomerService rentalCustomerService;
    
    @Mock
    private DeviceUtilizationService deviceUtilizationService;
    
    @InjectMocks
    private RentalController rentalController;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    /**
     * 测试获取客户列表接口
     */
    @Test
    void testGetCustomers() {
        // 准备测试数据
        List<Object> mockCustomerList = Arrays.asList(
            createMockCustomer("1", "测试客户1", "enterprise", "北京市"),
            createMockCustomer("2", "测试客户2", "individual", "上海市")
        );
        
        when(rentalCustomerService.getCustomerList(any(Map.class)))
            .thenReturn((List) mockCustomerList);
        when(rentalCustomerService.getCustomerCount(any(Map.class)))
            .thenReturn(2L);
        
        // 执行测试
        ResponseEntity<Map<String, Object>> response = rentalController.getCustomers(
            1, 20, "测试", "enterprise", "北京市"
        );
        
        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.get("code"));
        assertEquals("查询成功", responseBody.get("message"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
        assertNotNull(data);
        assertEquals(2L, data.get("total"));
        assertEquals(1, data.get("page"));
        assertEquals(20, data.get("pageSize"));
        
        // 验证服务方法被调用
        verify(rentalCustomerService, times(1)).getCustomerList(any(Map.class));
        verify(rentalCustomerService, times(1)).getCustomerCount(any(Map.class));
    }
    
    /**
     * 测试批量操作接口 - 更新状态
     */
    @Test
    void testBatchOperationUpdateStatus() {
        // 准备测试数据
        Map<String, Object> batchRequest = new HashMap<>();
        batchRequest.put("ids", Arrays.asList("YX-0001", "YX-0002"));
        batchRequest.put("operation", "updateStatus");
        
        Map<String, Object> params = new HashMap<>();
        params.put("status", "maintenance");
        batchRequest.put("params", params);
        
        // 模拟服务方法返回成功
        when(deviceUtilizationService.updateDeviceStatus(anyString(), anyString()))
            .thenReturn(true);
        
        // 执行测试
        ResponseEntity<Map<String, Object>> response = rentalController.batchOperation(batchRequest);
        
        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.get("code"));
        assertTrue(responseBody.get("message").toString().contains("批量操作完成"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
        assertNotNull(data);
        assertEquals(2, data.get("successCount"));
        assertEquals(0, data.get("failCount"));
        assertEquals(2, data.get("totalCount"));
        
        // 验证服务方法被调用
        verify(deviceUtilizationService, times(2)).updateDeviceStatus(anyString(), eq("maintenance"));
    }
    
    /**
     * 测试批量操作接口 - 维护操作
     */
    @Test
    void testBatchOperationMaintenance() {
        // 准备测试数据
        Map<String, Object> batchRequest = new HashMap<>();
        batchRequest.put("ids", Arrays.asList("YX-0001", "YX-0002", "YX-0003"));
        batchRequest.put("operation", "maintenance");
        
        Map<String, Object> params = new HashMap<>();
        params.put("maintenanceStatus", "urgent");
        batchRequest.put("params", params);
        
        // 模拟服务方法返回成功和失败
        when(deviceUtilizationService.updateMaintenanceStatus("YX-0001", "urgent"))
            .thenReturn(true);
        when(deviceUtilizationService.updateMaintenanceStatus("YX-0002", "urgent"))
            .thenReturn(true);
        when(deviceUtilizationService.updateMaintenanceStatus("YX-0003", "urgent"))
            .thenReturn(false);
        
        // 执行测试
        ResponseEntity<Map<String, Object>> response = rentalController.batchOperation(batchRequest);
        
        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.get("code"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
        assertNotNull(data);
        assertEquals(2, data.get("successCount"));
        assertEquals(1, data.get("failCount"));
        assertEquals(3, data.get("totalCount"));
        
        // 验证服务方法被调用
        verify(deviceUtilizationService, times(3)).updateMaintenanceStatus(anyString(), eq("urgent"));
    }
    
    /**
     * 测试批量操作接口 - 参数验证
     */
    @Test
    void testBatchOperationValidation() {
        // 测试空ID列表
        Map<String, Object> batchRequest = new HashMap<>();
        batchRequest.put("ids", new ArrayList<>());
        batchRequest.put("operation", "updateStatus");
        
        ResponseEntity<Map<String, Object>> response = rentalController.batchOperation(batchRequest);
        
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(400, responseBody.get("code"));
        assertEquals("操作对象ID列表不能为空", responseBody.get("message"));
        
        // 测试空操作类型
        batchRequest.put("ids", Arrays.asList("YX-0001"));
        batchRequest.put("operation", "");
        
        response = rentalController.batchOperation(batchRequest);
        
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        
        responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(400, responseBody.get("code"));
        assertEquals("操作类型不能为空", responseBody.get("message"));
    }
    
    /**
     * 测试获取客户类型列表接口
     */
    @Test
    void testGetCustomerTypes() {
        // 准备测试数据
        List<String> mockCustomerTypes = Arrays.asList("individual", "enterprise", "institution");
        
        when(rentalCustomerService.getAllCustomerTypes())
            .thenReturn(mockCustomerTypes);
        
        // 执行测试
        ResponseEntity<Map<String, Object>> response = rentalController.getCustomerTypes();
        
        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.get("code"));
        assertEquals("查询成功", responseBody.get("message"));
        
        @SuppressWarnings("unchecked")
        List<String> data = (List<String>) responseBody.get("data");
        assertNotNull(data);
        assertEquals(3, data.size());
        assertTrue(data.contains("individual"));
        assertTrue(data.contains("enterprise"));
        assertTrue(data.contains("institution"));
        
        // 验证服务方法被调用
        verify(rentalCustomerService, times(1)).getAllCustomerTypes();
    }
    
    /**
     * 测试获取客户地区列表接口
     */
    @Test
    void testGetCustomerRegions() {
        // 准备测试数据
        List<String> mockRegions = Arrays.asList("北京市", "上海市", "广州市", "深圳市");
        
        when(rentalCustomerService.getAllCustomerRegions())
            .thenReturn(mockRegions);
        
        // 执行测试
        ResponseEntity<Map<String, Object>> response = rentalController.getCustomerRegions();
        
        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.get("code"));
        assertEquals("查询成功", responseBody.get("message"));
        
        @SuppressWarnings("unchecked")
        List<String> data = (List<String>) responseBody.get("data");
        assertNotNull(data);
        assertEquals(4, data.size());
        assertTrue(data.contains("北京市"));
        assertTrue(data.contains("上海市"));
        
        // 验证服务方法被调用
        verify(rentalCustomerService, times(1)).getAllCustomerRegions();
    }
    
    /**
     * 创建模拟客户对象
     */
    private Object createMockCustomer(String id, String name, String type, String region) {
        Map<String, Object> customer = new HashMap<>();
        customer.put("id", Long.valueOf(id));
        customer.put("customerName", name);
        customer.put("customerType", type);
        customer.put("region", region);
        customer.put("totalRentalAmount", 10000.00);
        customer.put("totalRentalDays", 30);
        customer.put("isActive", true);
        return customer;
    }
}