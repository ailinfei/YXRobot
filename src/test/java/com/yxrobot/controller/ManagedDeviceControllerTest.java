package com.yxrobot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yxrobot.dto.ManagedDeviceDTO;
import com.yxrobot.dto.ManagedDeviceSearchCriteria;
import com.yxrobot.service.ManagedDeviceService;
import com.yxrobot.exception.ManagedDeviceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ManagedDeviceController 单元测试类
 * 
 * 测试覆盖范围：
 * - RESTful API接口
 * - 请求参数验证
 * - 响应格式验证
 * - 异常处理
 * - HTTP状态码验证
 * 
 * 测试目标覆盖率：
 * - 行覆盖率 ≥ 80%
 * - 分支覆盖率 ≥ 75%
 * - 方法覆盖率 ≥ 85%
 */
@WebMvcTest(ManagedDeviceController.class)
@ActiveProfiles("test")
@DisplayName("设备管理控制器单元测试")
public class ManagedDeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ManagedDeviceService managedDeviceService;

    @Autowired
    private ObjectMapper objectMapper;

    private ManagedDeviceDTO testDeviceDTO;
    private Map<String, Object> testListResponse;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        testDeviceDTO = new ManagedDeviceDTO();
        testDeviceDTO.setId("1");
        testDeviceDTO.setSerialNumber("YX-TEST-001");
        testDeviceDTO.setModel("YX-EDU-2024");
        testDeviceDTO.setStatus("online");
        testDeviceDTO.setFirmwareVersion("1.0.0");
        testDeviceDTO.setCustomerId("1");
        testDeviceDTO.setCustomerName("[测试客户]");
        testDeviceDTO.setCustomerPhone("[测试电话]");

        testListResponse = new HashMap<>();
        testListResponse.put("list", Arrays.asList(testDeviceDTO));
        testListResponse.put("total", 1L);
        testListResponse.put("page", 1);
        testListResponse.put("pageSize", 20);
    }

    @Test
    @DisplayName("获取设备列表 - 成功场景")
    void testGetDevices_Success() throws Exception {
        // Given
        when(managedDeviceService.getManagedDevices(any(ManagedDeviceSearchCriteria.class)))
            .thenReturn(testListResponse);

        // When & Then
        mockMvc.perform(get("/api/admin/devices")
                .param("page", "1")
                .param("pageSize", "20")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list").isArray())
                .andExpect(jsonPath("$.data.list[0].id").value("1"))
                .andExpect(jsonPath("$.data.list[0].serialNumber").value("YX-TEST-001"))
                .andExpect(jsonPath("$.data.list[0].model").value("YX-EDU-2024"))
                .andExpect(jsonPath("$.data.list[0].status").value("online"));

        verify(managedDeviceService).getManagedDevices(any(ManagedDeviceSearchCriteria.class));
    }

    @Test
    @DisplayName("获取设备列表 - 带搜索条件")
    void testGetDevices_WithSearchCriteria() throws Exception {
        // Given
        when(managedDeviceService.getManagedDevices(any(ManagedDeviceSearchCriteria.class)))
            .thenReturn(testListResponse);

        // When & Then
        mockMvc.perform(get("/api/admin/devices")
                .param("page", "1")
                .param("pageSize", "20")
                .param("keyword", "YX-TEST")
                .param("status", "online")
                .param("model", "YX-EDU-2024")
                .param("customerId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.total").value(1));

        verify(managedDeviceService).getManagedDevices(any(ManagedDeviceSearchCriteria.class));
    }

    @Test
    @DisplayName("获取设备列表 - 空结果")
    void testGetDevices_EmptyResult() throws Exception {
        // Given
        Map<String, Object> emptyResponse = new HashMap<>();
        emptyResponse.put("list", Arrays.asList());
        emptyResponse.put("total", 0L);
        emptyResponse.put("page", 1);
        emptyResponse.put("pageSize", 20);
        
        when(managedDeviceService.getManagedDevices(any(ManagedDeviceSearchCriteria.class)))
            .thenReturn(emptyResponse);

        // When & Then
        mockMvc.perform(get("/api/admin/devices")
                .param("page", "1")
                .param("pageSize", "20")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.total").value(0))
                .andExpect(jsonPath("$.data.list").isArray())
                .andExpect(jsonPath("$.data.list").isEmpty());
    }

    @Test
    @DisplayName("根据ID获取设备 - 成功场景")
    void testGetDeviceById_Success() throws Exception {
        // Given
        when(managedDeviceService.getManagedDeviceById(1L)).thenReturn(testDeviceDTO);

        // When & Then
        mockMvc.perform(get("/api/admin/devices/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.serialNumber").value("YX-TEST-001"))
                .andExpect(jsonPath("$.data.model").value("YX-EDU-2024"))
                .andExpect(jsonPath("$.data.status").value("online"))
                .andExpect(jsonPath("$.data.customerName").value("[测试客户]"));

        verify(managedDeviceService).getManagedDeviceById(1L);
    }

    @Test
    @DisplayName("根据ID获取设备 - 设备不存在")
    void testGetDeviceById_NotFound() throws Exception {
        // Given
        when(managedDeviceService.getManagedDeviceById(999L))
            .thenThrow(new ManagedDeviceException("设备不存在"));

        // When & Then
        mockMvc.perform(get("/api/admin/devices/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("设备不存在"));

        verify(managedDeviceService).getManagedDeviceById(999L);
    }

    @Test
    @DisplayName("创建设备 - 成功场景")
    void testCreateDevice_Success() throws Exception {
        // Given
        ManagedDeviceDTO createDTO = new ManagedDeviceDTO();
        createDTO.setSerialNumber("YX-TEST-002");
        createDTO.setModel("YX-EDU-2024");
        createDTO.setCustomerId("1");
        createDTO.setFirmwareVersion("1.0.0");

        ManagedDeviceDTO createdDTO = new ManagedDeviceDTO();
        createdDTO.setId("2");
        createdDTO.setSerialNumber("YX-TEST-002");
        createdDTO.setModel("YX-EDU-2024");
        createdDTO.setStatus("offline");
        createdDTO.setCustomerId("1");
        createdDTO.setCustomerName("[测试客户]");
        createdDTO.setFirmwareVersion("1.0.0");

        when(managedDeviceService.createManagedDevice(any(ManagedDeviceDTO.class)))
            .thenReturn(createdDTO);

        // When & Then
        mockMvc.perform(post("/api/admin/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("创建成功"))
                .andExpect(jsonPath("$.data.id").value("2"))
                .andExpect(jsonPath("$.data.serialNumber").value("YX-TEST-002"))
                .andExpect(jsonPath("$.data.model").value("YX-EDU-2024"))
                .andExpect(jsonPath("$.data.status").value("offline"));

        verify(managedDeviceService).createManagedDevice(any(ManagedDeviceDTO.class));
    }

    @Test
    @DisplayName("创建设备 - 数据验证失败")
    void testCreateDevice_ValidationFailed() throws Exception {
        // Given
        ManagedDeviceDTO invalidDTO = new ManagedDeviceDTO();
        // 缺少必填字段

        // When & Then
        mockMvc.perform(post("/api/admin/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").exists());

        verify(managedDeviceService, never()).createManagedDevice(any(ManagedDeviceDTO.class));
    }

    @Test
    @DisplayName("创建设备 - 序列号重复")
    void testCreateDevice_DuplicateSerialNumber() throws Exception {
        // Given
        ManagedDeviceDTO createDTO = new ManagedDeviceDTO();
        createDTO.setSerialNumber("YX-TEST-001");
        createDTO.setModel("YX-EDU-2024");
        createDTO.setCustomerId("1");
        createDTO.setFirmwareVersion("1.0.0");

        when(managedDeviceService.createManagedDevice(any(ManagedDeviceDTO.class)))
            .thenThrow(new ManagedDeviceException("设备序列号已存在"));

        // When & Then
        mockMvc.perform(post("/api/admin/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpected(jsonPath("$.message").value("设备序列号已存在"));

        verify(managedDeviceService).createManagedDevice(any(ManagedDeviceDTO.class));
    }

    @Test
    @DisplayName("更新设备 - 成功场景")
    void testUpdateDevice_Success() throws Exception {
        // Given
        ManagedDeviceDTO updateDTO = new ManagedDeviceDTO();
        updateDTO.setId("1");
        updateDTO.setSerialNumber("YX-TEST-001-UPDATED");
        updateDTO.setModel("YX-PRO-2024");
        updateDTO.setCustomerId("1");
        updateDTO.setFirmwareVersion("1.1.0");

        ManagedDeviceDTO updatedDTO = new ManagedDeviceDTO();
        updatedDTO.setId("1");
        updatedDTO.setSerialNumber("YX-TEST-001-UPDATED");
        updatedDTO.setModel("YX-PRO-2024");
        updatedDTO.setStatus("online");
        updatedDTO.setCustomerId("1");
        updatedDTO.setCustomerName("[测试客户]");
        updatedDTO.setFirmwareVersion("1.1.0");

        when(managedDeviceService.updateManagedDevice(eq(1L), any(ManagedDeviceDTO.class)))
            .thenReturn(updatedDTO);

        // When & Then
        mockMvc.perform(put("/api/admin/devices/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("更新成功"))
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.serialNumber").value("YX-TEST-001-UPDATED"))
                .andExpect(jsonPath("$.data.model").value("YX-PRO-2024"))
                .andExpect(jsonPath("$.data.firmwareVersion").value("1.1.0"));

        verify(managedDeviceService).updateManagedDevice(eq(1L), any(ManagedDeviceDTO.class));
    }

    @Test
    @DisplayName("更新设备 - 设备不存在")
    void testUpdateDevice_NotFound() throws Exception {
        // Given
        ManagedDeviceDTO updateDTO = new ManagedDeviceDTO();
        updateDTO.setId("999");
        updateDTO.setSerialNumber("YX-TEST-999");

        when(managedDeviceService.updateManagedDevice(eq(999L), any(ManagedDeviceDTO.class)))
            .thenThrow(new ManagedDeviceException("设备不存在"));

        // When & Then
        mockMvc.perform(put("/api/admin/devices/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("设备不存在"));

        verify(managedDeviceService).updateManagedDevice(eq(999L), any(ManagedDeviceDTO.class));
    }

    @Test
    @DisplayName("删除设备 - 成功场景")
    void testDeleteDevice_Success() throws Exception {
        // Given
        when(managedDeviceService.deleteManagedDevice(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/admin/devices/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("删除成功"));

        verify(managedDeviceService).deleteManagedDevice(1L);
    }

    @Test
    @DisplayName("删除设备 - 设备不存在")
    void testDeleteDevice_NotFound() throws Exception {
        // Given
        when(managedDeviceService.deleteManagedDevice(999L))
            .thenThrow(new ManagedDeviceException("设备不存在"));

        // When & Then
        mockMvc.perform(delete("/api/admin/devices/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("设备不存在"));

        verify(managedDeviceService).deleteManagedDevice(999L);
    }

    @Test
    @DisplayName("参数验证 - 无效的页码")
    void testParameterValidation_InvalidPage() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/admin/devices")
                .param("page", "-1")
                .param("pageSize", "20")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").exists());

        verify(managedDeviceService, never()).getManagedDevices(any(ManagedDeviceSearchCriteria.class));
    }

    @Test
    @DisplayName("参数验证 - 无效的页面大小")
    void testParameterValidation_InvalidPageSize() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/admin/devices")
                .param("page", "1")
                .param("pageSize", "0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").exists());

        verify(managedDeviceService, never()).getManagedDevices(any(ManagedDeviceSearchCriteria.class));
    }

    @Test
    @DisplayName("参数验证 - 无效的设备ID")
    void testParameterValidation_InvalidDeviceId() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/admin/devices/invalid")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").exists());

        verify(managedDeviceService, never()).getManagedDeviceById(anyLong());
    }

    @Test
    @DisplayName("内容类型验证 - 不支持的媒体类型")
    void testContentType_UnsupportedMediaType() throws Exception {
        // Given
        ManagedDeviceDTO createDTO = new ManagedDeviceDTO();
        createDTO.setSerialNumber("YX-TEST-002");

        // When & Then
        mockMvc.perform(post("/api/admin/devices")
                .contentType(MediaType.TEXT_PLAIN)
                .content("invalid content"))
                .andExpect(status().isUnsupportedMediaType());

        verify(managedDeviceService, never()).createManagedDevice(any(ManagedDeviceDTO.class));
    }

    @Test
    @DisplayName("HTTP方法验证 - 不支持的方法")
    void testHttpMethod_MethodNotAllowed() throws Exception {
        // When & Then
        mockMvc.perform(patch("/api/admin/devices")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());

        verify(managedDeviceService, never()).getManagedDevices(any(ManagedDeviceSearchCriteria.class));
    }

    @Test
    @DisplayName("响应格式验证 - 统一响应结构")
    void testResponseFormat_UnifiedStructure() throws Exception {
        // Given
        when(managedDeviceService.getManagedDevices(any(ManagedDeviceSearchCriteria.class)))
            .thenReturn(testListResponse);

        // When & Then
        mockMvc.perform(get("/api/admin/devices")
                .param("page", "1")
                .param("pageSize", "20")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").exists())
                .andExpected(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.list").isArray())
                .andExpect(jsonPath("$.data.total").isNumber())
                .andExpect(jsonPath("$.data.page").isNumber())
                .andExpect(jsonPath("$.data.pageSize").isNumber());
    }

    @Test
    @DisplayName("异常处理 - 服务层异常")
    void testExceptionHandling_ServiceException() throws Exception {
        // Given
        when(managedDeviceService.getManagedDevices(any(ManagedDeviceSearchCriteria.class)))
            .thenThrow(new RuntimeException("服务异常"));

        // When & Then
        mockMvc.perform(get("/api/admin/devices")
                .param("page", "1")
                .param("pageSize", "20")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("性能测试 - 响应时间验证")
    void testPerformance_ResponseTime() throws Exception {
        // Given
        when(managedDeviceService.getManagedDevices(any(ManagedDeviceSearchCriteria.class)))
            .thenReturn(testListResponse);

        // When
        long startTime = System.currentTimeMillis();
        
        mockMvc.perform(get("/api/admin/devices")
                .param("page", "1")
                .param("pageSize", "20")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;

        // Then
        // API响应时间应该在合理范围内（小于2秒）
        assertTrue(responseTime < 2000, "API响应时间应该小于2秒，实际：" + responseTime + "ms");
    }

    @Test
    @DisplayName("并发测试 - 多线程访问")
    void testConcurrency_MultipleRequests() throws Exception {
        // Given
        when(managedDeviceService.getManagedDevices(any(ManagedDeviceSearchCriteria.class)))
            .thenReturn(testListResponse);

        // When & Then
        // 模拟并发请求
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/api/admin/devices")
                    .param("page", "1")
                    .param("pageSize", "20")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }

        // 验证服务被调用了5次
        verify(managedDeviceService, times(5)).getManagedDevices(any(ManagedDeviceSearchCriteria.class));
    }
}