package com.yxrobot.mapper;

import com.yxrobot.entity.SalesProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 销售产品Mapper接口
 * 对应sales_products表
 */
@Mapper
public interface SalesProductMapper {
    
    /**
     * 插入销售产品记录
     */
    int insert(SalesProduct salesProduct);
    
    /**
     * 批量插入销售产品记录
     */
    int insertBatch(@Param("list") List<SalesProduct> salesProducts);
    
    /**
     * 根据ID查询销售产品
     */
    SalesProduct selectById(@Param("id") Long id);
    
    /**
     * 根据产品编码查询
     */
    SalesProduct selectByProductCode(@Param("productCode") String productCode);
    
    /**
     * 根据类别查询产品列表
     */
    List<SalesProduct> selectByCategory(@Param("category") String category);
    
    /**
     * 查询所有启用的销售产品
     */
    List<SalesProduct> selectAllActive();
    
    /**
     * 查询所有销售产品记录
     */
    List<SalesProduct> selectAll();
    
    /**
     * 分页查询销售产品
     */
    List<SalesProduct> selectByPage(@Param("offset") Integer offset, 
                                   @Param("limit") Integer limit,
                                   @Param("category") String category,
                                   @Param("productName") String productName,
                                   @Param("isActive") Boolean isActive);
    
    /**
     * 统计产品数量
     */
    Long countByCondition(@Param("category") String category,
                         @Param("productName") String productName,
                         @Param("isActive") Boolean isActive);
    
    /**
     * 更新销售产品记录
     */
    int updateById(SalesProduct salesProduct);
    
    /**
     * 更新库存数量
     */
    int updateStockQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);
    
    /**
     * 根据ID删除销售产品记录（逻辑删除）
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 启用/禁用产品
     */
    int updateActiveStatus(@Param("id") Long id, @Param("isActive") Boolean isActive);
    
    /**
     * 查询低库存产品
     */
    List<SalesProduct> selectLowStockProducts(@Param("threshold") Integer threshold);
    
    /**
     * 根据品牌查询产品
     */
    List<SalesProduct> selectByBrand(@Param("brand") String brand);
    
    /**
     * 查询产品类别列表
     */
    List<String> selectDistinctCategories();
    
    /**
     * 查询品牌列表
     */
    List<String> selectDistinctBrands();
}