package com.yxrobot.mapper;

import com.yxrobot.dto.ProductQueryDTO;
import com.yxrobot.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
// import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ProductMapper 单元测试
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-19
 */
// @MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class ProductMapperTest {

    @Autowired
    private ProductMapper productMapper;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        testProduct = new Product();
        testProduct.setName("测试产品");
        testProduct.setModel("TEST-MAPPER-001");
        testProduct.setDescription("测试产品描述");
        testProduct.setPrice(new BigDecimal("999.00"));
        testProduct.setStatus("published");
        testProduct.setCoverImageUrl("http://example.com/test.jpg");
        testProduct.setCreatedAt(LocalDateTime.now());
        testProduct.setUpdatedAt(LocalDateTime.now());
        testProduct.setIsDeleted(false);
    }

    @Test
    void testInsert_Success() {
        // 执行插入
        int result = productMapper.insert(testProduct);

        // 验证结果
        assertEquals(1, result);
        assertNotNull(testProduct.getId());
        assertTrue(testProduct.getId() > 0);
    }

    @Test
    void testSelectById_Success() {
        // 先插入数据
        productMapper.insert(testProduct);
        Long productId = testProduct.getId();

        // 执行查询
        Product result = productMapper.selectById(productId);

        // 验证结果
        assertNotNull(result);
        assertEquals(productId, result.getId());
        assertEquals(testProduct.getName(), result.getName());
        assertEquals(testProduct.getModel(), result.getModel());
        assertEquals(testProduct.getPrice(), result.getPrice());
        assertEquals(testProduct.getStatus(), result.getStatus());
    }

    @Test
    void testSelectById_NotFound() {
        // 查询不存在的ID
        Product result = productMapper.selectById(99999L);

        // 验证结果
        assertNull(result);
    }

    @Test
    void testUpdateById_Success() {
        // 先插入数据
        productMapper.insert(testProduct);
        Long productId = testProduct.getId();

        // 修改数据
        testProduct.setName("更新后的产品名称");
        testProduct.setPrice(new BigDecimal("1999.00"));
        testProduct.setStatus("draft");
        testProduct.setUpdatedAt(LocalDateTime.now());

        // 执行更新
        int result = productMapper.updateById(testProduct);

        // 验证结果
        assertEquals(1, result);

        // 查询验证更新结果
        Product updated = productMapper.selectById(productId);
        assertNotNull(updated);
        assertEquals("更新后的产品名称", updated.getName());
        assertEquals(new BigDecimal("1999.00"), updated.getPrice());
        assertEquals("draft", updated.getStatus());
    }

    @Test
    void testDeleteById_Success() {
        // 先插入数据
        productMapper.insert(testProduct);
        Long productId = testProduct.getId();

        // 执行软删除
        int result = productMapper.deleteById(productId);

        // 验证结果
        assertEquals(1, result);

        // 查询验证删除结果（软删除后数据仍存在但is_deleted=1）
        Product deleted = productMapper.selectById(productId);
        assertNull(deleted); // selectById应该过滤已删除的记录
    }

    @Test
    void testSelectByQuery_WithKeyword() {
        // 插入测试数据
        productMapper.insert(testProduct);

        Product product2 = new Product();
        product2.setName("另一个产品");
        product2.setModel("ANOTHER-001");
        product2.setDescription("另一个产品描述");
        product2.setPrice(new BigDecimal("1999.00"));
        product2.setStatus("draft");
        product2.setCreatedAt(LocalDateTime.now());
        product2.setUpdatedAt(LocalDateTime.now());
        product2.setIsDeleted(false);
        productMapper.insert(product2);

        // 构建查询条件
        ProductQueryDTO queryDTO = new ProductQueryDTO();
        queryDTO.setName("测试");
        queryDTO.setPage(1);
        queryDTO.setSize(10);

        // 执行查询
        List<Product> results = productMapper.selectByQuery(queryDTO);

        // 验证结果
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(testProduct.getName(), results.get(0).getName());
    }

    @Test
    void testSelectByQuery_WithStatus() {
        // 插入测试数据
        productMapper.insert(testProduct);

        Product draftProduct = new Product();
        draftProduct.setName("草稿产品");
        draftProduct.setModel("DRAFT-001");
        draftProduct.setDescription("草稿产品描述");
        draftProduct.setPrice(new BigDecimal("1999.00"));
        draftProduct.setStatus("draft");
        draftProduct.setCreatedAt(LocalDateTime.now());
        draftProduct.setUpdatedAt(LocalDateTime.now());
        draftProduct.setIsDeleted(false);
        productMapper.insert(draftProduct);

        // 构建查询条件
        ProductQueryDTO queryDTO = new ProductQueryDTO();
        queryDTO.setStatus("published");
        queryDTO.setPage(1);
        queryDTO.setSize(10);

        // 执行查询
        List<Product> results = productMapper.selectByQuery(queryDTO);

        // 验证结果
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("published", results.get(0).getStatus());
    }

    @Test
    void testCountByQuery() {
        // 插入测试数据
        productMapper.insert(testProduct);

        Product product2 = new Product();
        product2.setName("另一个产品");
        product2.setModel("ANOTHER-001");
        product2.setDescription("另一个产品描述");
        product2.setPrice(new BigDecimal("1999.00"));
        product2.setStatus("published");
        product2.setCreatedAt(LocalDateTime.now());
        product2.setUpdatedAt(LocalDateTime.now());
        product2.setIsDeleted(false);
        productMapper.insert(product2);

        // 构建查询条件
        ProductQueryDTO queryDTO = new ProductQueryDTO();
        queryDTO.setStatus("published");

        // 执行统计查询
        Integer count = productMapper.countByQuery(queryDTO);

        // 验证结果
        assertNotNull(count);
        assertEquals(2, count.intValue());
    }

    @Test
    void testExistsByModel_True() {
        // 插入测试数据
        productMapper.insert(testProduct);

        // 检查型号是否存在
        boolean exists = productMapper.existsByModel(testProduct.getModel());

        // 验证结果
        assertTrue(exists);
    }

    @Test
    void testExistsByModel_False() {
        // 检查不存在的型号
        boolean exists = productMapper.existsByModel("NON-EXISTENT-MODEL");

        // 验证结果
        assertFalse(exists);
    }

    @Test
    void testExistsByModelExcludeId_True() {
        // 插入两个产品
        productMapper.insert(testProduct);
        Long firstId = testProduct.getId();

        Product product2 = new Product();
        product2.setName("另一个产品");
        product2.setModel("ANOTHER-001");
        product2.setDescription("另一个产品描述");
        product2.setPrice(new BigDecimal("1999.00"));
        product2.setStatus("published");
        product2.setCreatedAt(LocalDateTime.now());
        product2.setUpdatedAt(LocalDateTime.now());
        product2.setIsDeleted(false);
        productMapper.insert(product2);

        // 检查型号是否存在（排除指定ID）
        boolean exists = productMapper.existsByModelExcludeId(testProduct.getModel(), product2.getId());

        // 验证结果
        assertTrue(exists);
    }

    @Test
    void testExistsByModelExcludeId_False() {
        // 插入测试数据
        productMapper.insert(testProduct);

        // 检查型号是否存在（排除自己的ID）
        boolean exists = productMapper.existsByModelExcludeId(testProduct.getModel(), testProduct.getId());

        // 验证结果
        assertFalse(exists);
    }

    @Test
    void testBatchDeleteByIds_Success() {
        // 插入多个测试数据
        productMapper.insert(testProduct);
        Long id1 = testProduct.getId();

        Product product2 = new Product();
        product2.setName("产品2");
        product2.setModel("BATCH-002");
        product2.setDescription("产品2描述");
        product2.setPrice(new BigDecimal("1999.00"));
        product2.setStatus("published");
        product2.setCreatedAt(LocalDateTime.now());
        product2.setUpdatedAt(LocalDateTime.now());
        product2.setIsDeleted(false);
        productMapper.insert(product2);
        Long id2 = product2.getId();

        Product product3 = new Product();
        product3.setName("产品3");
        product3.setModel("BATCH-003");
        product3.setDescription("产品3描述");
        product3.setPrice(new BigDecimal("2999.00"));
        product3.setStatus("published");
        product3.setCreatedAt(LocalDateTime.now());
        product3.setUpdatedAt(LocalDateTime.now());
        product3.setIsDeleted(false);
        productMapper.insert(product3);
        Long id3 = product3.getId();

        // 执行批量删除
        List<Long> idsToDelete = Arrays.asList(id1, id2, id3);
        int result = productMapper.batchDeleteByIds(idsToDelete);

        // 验证结果
        assertEquals(3, result);

        // 验证数据已被软删除
        assertNull(productMapper.selectById(id1));
        assertNull(productMapper.selectById(id2));
        assertNull(productMapper.selectById(id3));
    }

    @Test
    void testCountByStatus() {
        // 插入不同状态的产品
        productMapper.insert(testProduct); // published

        Product draftProduct = new Product();
        draftProduct.setName("草稿产品");
        draftProduct.setModel("DRAFT-001");
        draftProduct.setDescription("草稿产品描述");
        draftProduct.setPrice(new BigDecimal("1999.00"));
        draftProduct.setStatus("draft");
        draftProduct.setCreatedAt(LocalDateTime.now());
        draftProduct.setUpdatedAt(LocalDateTime.now());
        draftProduct.setIsDeleted(false);
        productMapper.insert(draftProduct);

        Product archivedProduct = new Product();
        archivedProduct.setName("归档产品");
        archivedProduct.setModel("ARCHIVED-001");
        archivedProduct.setDescription("归档产品描述");
        archivedProduct.setPrice(new BigDecimal("2999.00"));
        archivedProduct.setStatus("archived");
        archivedProduct.setCreatedAt(LocalDateTime.now());
        archivedProduct.setUpdatedAt(LocalDateTime.now());
        archivedProduct.setIsDeleted(false);
        productMapper.insert(archivedProduct);

        // 统计各状态的产品数量
        Integer publishedCount = productMapper.countByStatus("published");
        Integer draftCount = productMapper.countByStatus("draft");
        Integer archivedCount = productMapper.countByStatus("archived");

        // 验证结果
        assertTrue(publishedCount >= 1);
        assertTrue(draftCount >= 1);
        assertTrue(archivedCount >= 1);
    }

    @Test
    void testGetStatusStatistics() {
        // 插入不同状态的产品
        productMapper.insert(testProduct); // published

        Product draftProduct = new Product();
        draftProduct.setName("草稿产品");
        draftProduct.setModel("DRAFT-STAT-001");
        draftProduct.setDescription("草稿产品描述");
        draftProduct.setPrice(new BigDecimal("1999.00"));
        draftProduct.setStatus("draft");
        draftProduct.setCreatedAt(LocalDateTime.now());
        draftProduct.setUpdatedAt(LocalDateTime.now());
        draftProduct.setIsDeleted(false);
        productMapper.insert(draftProduct);

        // 获取状态统计
        java.util.Map<String, Integer> statistics = productMapper.getStatusStatistics();

        // 验证结果
        assertNotNull(statistics);
        assertTrue(statistics.size() > 0);

        // 验证统计数据包含必要字段
        assertTrue(statistics.containsKey("published"));
        assertTrue(statistics.containsKey("draft"));
        assertTrue(statistics.containsKey("total"));
    }
}