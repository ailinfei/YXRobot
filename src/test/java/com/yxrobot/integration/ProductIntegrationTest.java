package com.yxrobot.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yxrobot.dto.ProductDTO;
import com.yxrobot.entity.Product;
import com.yxrobot.mapper.ProductMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 产品管理完整流程集成测试
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-19
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductMapper productMapper;

    private ProductDTO testProductDTO;

    @BeforeEach
    void setUp() {
        // 清理测试数据
        // 由于使用@Transactional，每个测试方法后会自动回滚

        // 初始化测试数据
        testProductDTO = new ProductDTO();
        testProductDTO.setName("集成测试产品");
        testProductDTO.setModel("INTEGRATION-TEST-001");
        testProductDTO.setDescription("这是一个集成测试产品");
        testProductDTO.setPrice(new BigDecimal("1999.00"));
        testProductDTO.setStatus("draft");
    }

    @Test
    void testCompleteProductLifecycle() throws Exception {
        // 1. 创建产品
        String createResponse = mockMvc.perform(post("/api/admin/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProductDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("集成测试产品"))
                .andExpect(jsonPath("$.data.model").value("INTEGRATION-TEST-001"))
                .andExpect(jsonPath("$.data.status").value("draft"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // 解析创建响应获取产品ID
        com.fasterxml.jackson.databind.JsonNode createResult = objectMapper.readTree(createResponse);
        Long productId = createResult.get("data").get("id").asLong();

        // 2. 查询产品详情
        mockMvc.perform(get("/api/admin/products/" + productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(productId))
                .andExpect(jsonPath("$.data.name").value("集成测试产品"))
                .andExpect(jsonPath("$.data.model").value("INTEGRATION-TEST-001"));

        // 3. 更新产品
        testProductDTO.setName("更新后的产品名称");
        testProductDTO.setStatus("published");
        testProductDTO.setPrice(new BigDecimal("2999.00"));

        mockMvc.perform(put("/api/admin/products/" + productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProductDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("更新后的产品名称"))
                .andExpect(jsonPath("$.data.status").value("published"))
                .andExpect(jsonPath("$.data.price").value(2999.00));

        // 4. 查询产品列表（验证更新后的数据）
        mockMvc.perform(get("/api/admin/products")
                .param("keyword", "更新后"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list").isArray())
                .andExpect(jsonPath("$.data.list[0].name").value("更新后的产品名称"));

        // 5. 删除产品
        mockMvc.perform(delete("/api/admin/products/" + productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 6. 验证产品已被软删除（查询不到）
        mockMvc.perform(get("/api/admin/products/" + productId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void testProductWithFileUpload() throws Exception {
        // 准备文件数据
        MockMultipartFile coverFile = new MockMultipartFile(
            "coverFiles", 
            "test-cover.jpg", 
            "image/jpeg", 
            "test image content".getBytes()
        );

        // 1. 创建带文件的产品
        String createResponse = mockMvc.perform(multipart("/api/admin/products")
                .file(coverFile)
                .param("name", "带文件的产品")
                .param("model", "FILE-TEST-001")
                .param("description", "这是一个带文件上传的产品")
                .param("price", "3999.00")
                .param("status", "published"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("带文件的产品"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // 解析响应获取产品ID
        com.fasterxml.jackson.databind.JsonNode createResult = objectMapper.readTree(createResponse);
        Long productId = createResult.get("data").get("id").asLong();

        // 2. 验证产品创建成功
        mockMvc.perform(get("/api/admin/products/" + productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("带文件的产品"))
                .andExpect(jsonPath("$.data.model").value("FILE-TEST-001"));

        // 3. 获取产品媒体列表
        mockMvc.perform(get("/api/admin/products/" + productId + "/media"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());

        // 4. 上传产品媒体
        MockMultipartFile mediaFile = new MockMultipartFile(
            "mediaFile", 
            "product-image.jpg", 
            "image/jpeg", 
            "product image content".getBytes()
        );

        String mediaResponse = mockMvc.perform(multipart("/api/admin/products/" + productId + "/media")
                .file(mediaFile)
                .param("mediaType", "image")
                .param("sortOrder", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // 解析媒体响应获取媒体ID
        com.fasterxml.jackson.databind.JsonNode mediaResult = objectMapper.readTree(mediaResponse);
        Long mediaId = mediaResult.get("data").get("id").asLong();

        // 5. 更新媒体排序
        mockMvc.perform(put("/api/admin/products/media/" + mediaId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"sortOrder\": 2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 6. 删除媒体
        mockMvc.perform(delete("/api/admin/products/media/" + mediaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testBatchOperations() throws Exception {
        // 1. 创建多个产品
        Long[] productIds = new Long[3];
        
        for (int i = 0; i < 3; i++) {
            ProductDTO product = new ProductDTO();
            product.setName("批量测试产品" + (i + 1));
            product.setModel("BATCH-TEST-00" + (i + 1));
            product.setDescription("批量测试产品描述" + (i + 1));
            product.setPrice(new BigDecimal("1000.00").add(new BigDecimal(i * 500)));
            product.setStatus("published");

            String response = mockMvc.perform(post("/api/admin/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(product)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            com.fasterxml.jackson.databind.JsonNode result = objectMapper.readTree(response);
            productIds[i] = result.get("data").get("id").asLong();
        }

        // 2. 查询产品列表（验证创建的产品）
        mockMvc.perform(get("/api/admin/products")
                .param("keyword", "批量测试"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list").isArray())
                .andExpect(jsonPath("$.data.list", hasSize(greaterThanOrEqualTo(3))));

        // 3. 批量删除产品
        java.util.Map<String, java.util.List<Long>> batchDeleteRequest = java.util.Map.of(
            "productIds", java.util.Arrays.asList(productIds)
        );

        mockMvc.perform(post("/api/admin/products/batch-delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(batchDeleteRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.deletedCount").value(3))
                .andExpect(jsonPath("$.data.requestCount").value(3));

        // 4. 验证产品已被删除
        for (Long productId : productIds) {
            mockMvc.perform(get("/api/admin/products/" + productId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404));
        }
    }

    @Test
    void testProductStatusStatistics() throws Exception {
        // 1. 创建不同状态的产品
        String[] statuses = {"draft", "published", "archived"};
        
        for (String status : statuses) {
            for (int i = 0; i < 2; i++) {
                ProductDTO product = new ProductDTO();
                product.setName("状态测试产品-" + status + "-" + (i + 1));
                product.setModel("STATUS-TEST-" + status.toUpperCase() + "-00" + (i + 1));
                product.setDescription("状态测试产品描述");
                product.setPrice(new BigDecimal("1500.00"));
                product.setStatus(status);

                mockMvc.perform(post("/api/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.code").value(200));
            }
        }

        // 2. 获取状态统计
        mockMvc.perform(get("/api/admin/products/statistics/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(3))));
    }

    @Test
    void testErrorHandling() throws Exception {
        // 1. 测试创建重复型号的产品
        mockMvc.perform(post("/api/admin/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProductDTO)))
                .andExpect(status().isOk());

        // 尝试创建相同型号的产品
        mockMvc.perform(post("/api/admin/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProductDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message", containsString("产品型号已存在")));

        // 2. 测试查询不存在的产品
        mockMvc.perform(get("/api/admin/products/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("产品不存在"));

        // 3. 测试更新不存在的产品
        mockMvc.perform(put("/api/admin/products/99999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProductDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));

        // 4. 测试删除不存在的产品
        mockMvc.perform(delete("/api/admin/products/99999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));

        // 5. 测试无效的请求参数
        ProductDTO invalidProduct = new ProductDTO();
        invalidProduct.setName(""); // 空名称
        invalidProduct.setModel("INVALID-001");
        invalidProduct.setPrice(new BigDecimal("-100")); // 负价格
        invalidProduct.setStatus("invalid_status"); // 无效状态

        mockMvc.perform(post("/api/admin/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void testDatabaseConsistency() throws Exception {
        // 1. 通过API创建产品
        String response = mockMvc.perform(post("/api/admin/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProductDTO)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        com.fasterxml.jackson.databind.JsonNode result = objectMapper.readTree(response);
        Long productId = result.get("data").get("id").asLong();

        // 2. 直接从数据库查询验证
        Product dbProduct = productMapper.selectById(productId);
        assertNotNull(dbProduct);
        assertEquals("集成测试产品", dbProduct.getName());
        assertEquals("INTEGRATION-TEST-001", dbProduct.getModel());
        assertEquals("draft", dbProduct.getStatus());
        assertEquals(0, dbProduct.getIsDeleted());

        // 3. 通过API删除产品
        mockMvc.perform(delete("/api/admin/products/" + productId))
                .andExpect(status().isOk());

        // 4. 验证数据库中产品被软删除
        Product deletedProduct = productMapper.selectById(productId);
        assertNull(deletedProduct); // selectById应该过滤已删除的记录
    }
}