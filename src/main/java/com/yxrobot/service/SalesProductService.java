package com.yxrobot.service;

import com.yxrobot.entity.SalesProduct;
import com.yxrobot.dto.SalesProductDTO;
import com.yxrobot.mapper.SalesProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 销售产品服务类
 * 对应sales_products表的业务逻辑
 */
@Service
public class SalesProductService {
    
    @Autowired
    private SalesProductMapper salesProductMapper;
    
    /**
     * 创建销售产品
     */
    @Transactional
    public SalesProduct createSalesProduct(SalesProduct salesProduct) {
        salesProduct.setCreatedAt(LocalDateTime.now());
        salesProduct.setUpdatedAt(LocalDateTime.now());
        if (salesProduct.getIsDeleted() == null) {
            salesProduct.setIsDeleted(false);
        }
        if (salesProduct.getIsActive() == null) {
            salesProduct.setIsActive(true);
        }
        
        salesProductMapper.insert(salesProduct);
        return salesProduct;
    }
    
    /**
     * 批量创建销售产品
     */
    @Transactional
    public void createSalesProductsBatch(List<SalesProduct> salesProducts) {
        if (salesProducts == null || salesProducts.isEmpty()) {
            return;
        }
        
        LocalDateTime now = LocalDateTime.now();
        for (SalesProduct salesProduct : salesProducts) {
            salesProduct.setCreatedAt(now);
            salesProduct.setUpdatedAt(now);
            if (salesProduct.getIsDeleted() == null) {
                salesProduct.setIsDeleted(false);
            }
            if (salesProduct.getIsActive() == null) {
                salesProduct.setIsActive(true);
            }
        }
        
        salesProductMapper.insertBatch(salesProducts);
    }
    
    /**
     * 根据ID查询销售产品
     */
    public SalesProduct getSalesProductById(Long id) {
        return salesProductMapper.selectById(id);
    }
    
    /**
     * 根据产品编码查询
     */
    public SalesProduct getSalesProductByCode(String productCode) {
        return salesProductMapper.selectByProductCode(productCode);
    }
    
    /**
     * 根据类别查询产品列表
     */
    public List<SalesProduct> getSalesProductsByCategory(String category) {
        return salesProductMapper.selectByCategory(category);
    }
    
    /**
     * 查询所有启用的销售产品
     */
    public List<SalesProduct> getAllActiveSalesProducts() {
        return salesProductMapper.selectAllActive();
    }
    
    /**
     * 查询所有销售产品
     */
    public List<SalesProduct> getAllSalesProducts() {
        return salesProductMapper.selectAll();
    }
    
    /**
     * 分页查询销售产品
     */
    public List<SalesProduct> getSalesProductsByPage(Integer page, Integer size, 
                                                    String category, String productName, Boolean isActive) {
        Integer offset = (page - 1) * size;
        return salesProductMapper.selectByPage(offset, size, category, productName, isActive);
    }
    
    /**
     * 统计产品数量
     */
    public Long countSalesProducts(String category, String productName, Boolean isActive) {
        return salesProductMapper.countByCondition(category, productName, isActive);
    }
    
    /**
     * 更新销售产品
     */
    @Transactional
    public SalesProduct updateSalesProduct(SalesProduct salesProduct) {
        salesProduct.setUpdatedAt(LocalDateTime.now());
        salesProductMapper.updateById(salesProduct);
        return salesProduct;
    }
    
    /**
     * 更新库存数量
     */
    @Transactional
    public void updateStockQuantity(Long id, Integer quantity) {
        salesProductMapper.updateStockQuantity(id, quantity);
    }
    
    /**
     * 删除销售产品
     */
    @Transactional
    public void deleteSalesProduct(Long id) {
        salesProductMapper.deleteById(id);
    }
    
    /**
     * 启用/禁用产品
     */
    @Transactional
    public void updateActiveStatus(Long id, Boolean isActive) {
        salesProductMapper.updateActiveStatus(id, isActive);
    }
    
    /**
     * 查询低库存产品
     */
    public List<SalesProduct> getLowStockProducts(Integer threshold) {
        return salesProductMapper.selectLowStockProducts(threshold);
    }
    
    /**
     * 根据品牌查询产品
     */
    public List<SalesProduct> getSalesProductsByBrand(String brand) {
        return salesProductMapper.selectByBrand(brand);
    }
    
    /**
     * 查询所有产品类别
     */
    public List<String> getAllCategories() {
        return salesProductMapper.selectDistinctCategories();
    }
    
    /**
     * 查询所有品牌
     */
    public List<String> getAllBrands() {
        return salesProductMapper.selectDistinctBrands();
    }
    
    /**
     * 检查产品编码是否存在
     */
    public boolean isProductCodeExists(String productCode) {
        SalesProduct product = salesProductMapper.selectByProductCode(productCode);
        return product != null;
    }
    
    /**
     * 检查产品编码是否存在（排除指定ID）
     */
    public boolean isProductCodeExists(String productCode, Long excludeId) {
        SalesProduct product = salesProductMapper.selectByProductCode(productCode);
        return product != null && !product.getId().equals(excludeId);
    }
    
    /**
     * 获取所有产品的DTO列表 - 为销售控制器提供
     */
    public List<SalesProductDTO> getAllProducts() {
        List<SalesProduct> products = getAllActiveSalesProducts();
        return products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 将SalesProduct实体转换为DTO
     */
    private SalesProductDTO convertToDTO(SalesProduct product) {
        SalesProductDTO dto = new SalesProductDTO();
        dto.setId(product.getId());
        dto.setProductName(product.getProductName());
        dto.setProductCode(product.getProductCode());
        dto.setCategory(product.getCategory());
        dto.setBrand(product.getBrand());
        dto.setModel(product.getModel());
        dto.setUnitPrice(product.getUnitPrice());
        dto.setCostPrice(product.getCostPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setUnit(product.getUnit());
        dto.setDescription(product.getDescription());
        dto.setSpecifications(product.getSpecifications());
        dto.setIsActive(product.getIsActive());
        dto.setCreatedAt(product.getCreatedAt() != null ? product.getCreatedAt().toString() : null);
        dto.setUpdatedAt(product.getUpdatedAt() != null ? product.getUpdatedAt().toString() : null);
        return dto;
    }
}