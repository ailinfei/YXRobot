package com.yxrobot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yxrobot.common.Result;
import com.yxrobot.dto.ProductDTO;
import com.yxrobot.dto.ProductQueryDTO;
import com.yxrobot.dto.PageResult;
import com.yxrobot.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ProductController 单元测试
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-19
 */
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductDTO testProductDTO;
    private PageResult<ProductDTO> testPageResult;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        testProductDTO = new ProductDTO();
        testProductDTO.setId(1L);
        testProductDTO.setName("测试产品");
        testProductDTO.setModel("TEST-001");
        testProductDTO.setDescription("测试产品描述");
        testProductDTO.setPrice(new BigDecimal("999.00"));
        testProductDTO.setStatus("published");
        testProductDTO.setCoverImageUrl("http://example.com/image.jpg");
        testProductDTO.setCreatedAt(LocalDateTime.now());
        testProductDTO.setUpdatedAt(LocalDateTime.now());

        List<ProductDTO> products = Arrays.asList(testProductDTO);
        testPageResult = PageResult.of(products, 1L, 1, 20);
    }

    @Test
    void testGetProducts_Success() throws Exception {
        // 模拟Service调用
        List<ProductDTO> mockList = Arrays.asList(testProductDTO);
        when(productService.getProducts(any(ProductQueryDTO.class))).thenReturn(mockList);

        // 执行测试
        mockMvc.perform(get("/api/admin/products")
                .param("page", "1")
                .param("size", "20")
                .param("keyword", "测试")
                .param("status", "published"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list").isArray())
                .andExpect(jsonPath("$.data.list[0].id").value(1))
                .andExpect(jsonPath("$.data.list[0].name").value("测试产品"))
                .andExpect(jsonPath("$.data.total").value(1));

        // 验证Service调用
        verify(productService, times(1)).getProducts(any());
    }

    @Test
    void testGetProducts_DefaultParams() throws Exception {
        // 模拟Service调用
        List<ProductDTO> mockList = Arrays.asList(testProductDTO);
        when(productService.getProducts(any(ProductQueryDTO.class))).thenReturn(mockList);

        // 执行测试（使用默认参数）
        mockMvc.perform(get("/api/admin/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 验证Service调用
        verify(productService, times(1)).getProducts(any());
    }

    @Test
    void testGetProduct_Success() throws Exception {
        // 模拟Service调用
        when(productService.getProductById(1L)).thenReturn(testProductDTO);

        // 执行测试
        mockMvc.perform(get("/api/admin/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("测试产品"))
                .andExpect(jsonPath("$.data.model").value("TEST-001"));

        // 验证Service调用
        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    void testGetProduct_NotFound() throws Exception {
        // 模拟Service调用返回null
        when(productService.getProductById(999L)).thenReturn(null);

        // 执行测试
        mockMvc.perform(get("/api/admin/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("产品不存在"));

        // 验证Service调用
        verify(productService, times(1)).getProductById(999L);
    }

    @Test
    void testCreateProduct_JSON_Success() throws Exception {
        // 模拟Service调用
        when(productService.createProduct(any(ProductDTO.class))).thenReturn(testProductDTO);

        // 准备请求数据
        ProductDTO createData = new ProductDTO();
        createData.setName("新产品");
        createData.setModel("NEW-001");
        createData.setDescription("新产品描述");
        createData.setPrice(new BigDecimal("1999.00"));
        createData.setStatus("draft");

        // 执行测试
        mockMvc.perform(post("/api/admin/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("产品创建成功"))
                .andExpect(jsonPath("$.data.id").value(1));

        // 验证Service调用
        verify(productService, times(1)).createProduct(any(ProductDTO.class));
    }

    @Test
    void testCreateProduct_Multipart_Success() throws Exception {
        // 模拟Service调用
        when(productService.createProduct(any(ProductDTO.class))).thenReturn(testProductDTO);

        // 准备文件数据
        MockMultipartFile coverFile = new MockMultipartFile(
            "coverFiles", "test.jpg", "image/jpeg", "test image content".getBytes());

        // 执行测试
        mockMvc.perform(multipart("/api/admin/products")
                .file(coverFile)
                .param("name", "新产品")
                .param("model", "NEW-001")
                .param("description", "新产品描述")
                .param("price", "1999.00")
                .param("status", "draft"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("产品创建成功"));

        // 验证Service调用
        verify(productService, times(1)).createProduct(any(ProductDTO.class));
    }

    @Test
    void testCreateProduct_ValidationError() throws Exception {
        // 准备无效数据（缺少必填字段）
        ProductDTO invalidData = new ProductDTO();
        invalidData.setName(""); // 空名称
        invalidData.setModel("NEW-001");
        invalidData.setPrice(new BigDecimal("1999.00"));
        invalidData.setStatus("draft");

        // 执行测试
        mockMvc.perform(post("/api/admin/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidData)))
                .andExpect(status().isBadRequest());

        // 验证Service没有被调用
        verify(productService, never()).createProduct(any());
    }

    @Test
    void testUpdateProduct_Success() throws Exception {
        // 模拟Service调用
        when(productService.getProductById(1L)).thenReturn(testProductDTO);
        when(productService.updateProduct(eq(1L), any(ProductDTO.class))).thenReturn(testProductDTO);

        // 准备更新数据
        ProductDTO updateData = new ProductDTO();
        updateData.setName("更新产品");
        updateData.setModel("UPDATE-001");
        updateData.setDescription("更新产品描述");
        updateData.setPrice(new BigDecimal("2999.00"));
        updateData.setStatus("published");

        // 执行测试
        mockMvc.perform(put("/api/admin/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("产品更新成功"));

        // 验证Service调用
        verify(productService, times(1)).getProductById(1L);
        verify(productService, times(1)).updateProduct(eq(1L), any(ProductDTO.class));
    }

    @Test
    void testUpdateProduct_NotFound() throws Exception {
        // 模拟Service调用
        when(productService.getProductById(999L)).thenReturn(null);

        // 准备更新数据
        ProductDTO updateData = new ProductDTO();
        updateData.setName("更新产品");
        updateData.setModel("UPDATE-001");
        updateData.setPrice(new BigDecimal("2999.00"));
        updateData.setStatus("published");

        // 执行测试
        mockMvc.perform(put("/api/admin/products/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("产品不存在"));

        // 验证Service调用
        verify(productService, times(1)).getProductById(999L);
        verify(productService, never()).updateProduct(any(), any());
    }

    @Test
    void testDeleteProduct_Success() throws Exception {
        // 模拟Service调用
        doNothing().when(productService).deleteProduct(1L);

        // 执行测试
        mockMvc.perform(delete("/api/admin/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 验证Service调用
        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    void testDeleteProduct_NotFound() throws Exception {
        // 模拟Service抛出异常
        doThrow(new IllegalArgumentException("产品不存在，ID: 999"))
                .when(productService).deleteProduct(999L);

        // 执行测试
        mockMvc.perform(delete("/api/admin/products/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("产品不存在，ID: 999"));

        // 验证Service调用
        verify(productService, times(1)).deleteProduct(999L);
    }

    @Test
    void testBatchDeleteProducts_Success() throws Exception {
        // 模拟Service调用
        when(productService.batchDeleteProducts(any())).thenReturn(3);

        // 准备请求数据
        java.util.Map<String, List<Long>> requestBody = java.util.Map.of(
            "productIds", Arrays.asList(1L, 2L, 3L)
        );

        // 执行测试
        mockMvc.perform(post("/api/admin/products/batch-delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("批量删除成功"))
                .andExpect(jsonPath("$.data.deletedCount").value(3))
                .andExpect(jsonPath("$.data.requestCount").value(3));

        // 验证Service调用
        verify(productService, times(1)).batchDeleteProducts(any());
    }

    @Test
    void testBatchDeleteProducts_EmptyList() throws Exception {
        // 准备空的请求数据
        java.util.Map<String, List<Long>> requestBody = java.util.Map.of(
            "productIds", Arrays.asList()
        );

        // 执行测试
        mockMvc.perform(post("/api/admin/products/batch-delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("产品ID列表不能为空"));

        // 验证Service没有被调用
        verify(productService, never()).batchDeleteProducts(any());
    }

    @Test
    void testGetStatusStatistics_Success() throws Exception {
        // 准备测试数据
        java.util.Map<String, Integer> mockStatistics = new java.util.HashMap<>();
        mockStatistics.put("published", 10);
        mockStatistics.put("draft", 5);
        mockStatistics.put("total", 15);

        // 模拟Service调用
        when(productService.getStatusStatistics()).thenReturn(mockStatistics);

        // 执行测试
        mockMvc.perform(get("/api/admin/products/statistics/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.published").value(10))
                .andExpect(jsonPath("$.data.draft").value(5))
                .andExpect(jsonPath("$.data.total").value(15));

        // 验证Service调用
        verify(productService, times(1)).getStatusStatistics();
    }

    @Test
    void testHandleOptions() throws Exception {
        // 执行CORS预检请求测试
        mockMvc.perform(options("/api/admin/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}