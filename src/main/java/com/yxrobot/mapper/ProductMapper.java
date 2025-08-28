package com.yxrobot.mapper;

import com.yxrobot.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 产品数据访问层接口
 * 对应数据库表：products
 */
@Mapper
public interface ProductMapper {
    
    /**
     * 根据ID查询产品
     * @param id 产品ID
     * @return 产品信息
     */
    Product selectById(@Param("id") Long id);
    
    /**
     * 根据产品型号查询产品
     * @param model 产品型号
     * @return 产品信息
     */
    Product selectByModel(@Param("model") String model);
    
    /**
     * 查询所有产品
     * @return 产品列表
     */
    List<Product> selectAll();
    
    /**
     * 根据状态查询产品
     * @param status 产品状态
     * @return 产品列表
     */
    List<Product> selectByStatus(@Param("status") String status);
    
    /**
     * 分页查询产品
     * @param offset 偏移量
     * @param limit 限制数量
     * @param status 产品状态（可选）
     * @return 产品列表
     */
    List<Product> selectWithPagination(@Param("offset") int offset, 
                                      @Param("limit") int limit,
                                      @Param("status") String status);
    
    /**
     * 根据条件搜索产品
     * @param name 产品名称（模糊匹配）
     * @param model 产品型号（模糊匹配）
     * @param status 产品状态
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @return 产品列表
     */
    List<Product> searchProducts(@Param("name") String name,
                                @Param("model") String model,
                                @Param("status") String status,
                                @Param("minPrice") BigDecimal minPrice,
                                @Param("maxPrice") BigDecimal maxPrice);
    
    /**
     * 统计产品总数
     * @return 产品总数
     */
    int countAll();
    
    /**
     * 根据状态统计产品数量
     * @param status 产品状态
     * @return 产品数量
     */
    int countByStatus(@Param("status") String status);
    
    /**
     * 统计搜索结果数量
     * @param name 产品名称（模糊匹配）
     * @param model 产品型号（模糊匹配）
     * @param status 产品状态
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @return 搜索结果数量
     */
    int countSearchResults(@Param("name") String name,
                          @Param("model") String model,
                          @Param("status") String status,
                          @Param("minPrice") BigDecimal minPrice,
                          @Param("maxPrice") BigDecimal maxPrice);
    
    /**
     * 插入新产品
     * @param product 产品信息
     * @return 影响行数
     */
    int insert(Product product);
    
    /**
     * 更新产品信息
     * @param product 产品信息
     * @return 影响行数
     */
    int update(Product product);
    
    /**
     * 更新产品状态
     * @param id 产品ID
     * @param status 新状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    
    /**
     * 更新产品价格
     * @param id 产品ID
     * @param price 新价格
     * @return 影响行数
     */
    int updatePrice(@Param("id") Long id, @Param("price") BigDecimal price);
    
    /**
     * 软删除产品
     * @param id 产品ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 批量软删除产品
     * @param ids 产品ID列表
     * @return 影响行数
     */
    int deleteBatch(@Param("ids") List<Long> ids);
    
    /**
     * 恢复已删除的产品
     * @param id 产品ID
     * @return 影响行数
     */
    int restoreById(@Param("id") Long id);
    
    /**
     * 检查产品型号是否存在
     * @param model 产品型号
     * @return 是否存在
     */
    boolean existsByModel(@Param("model") String model);
    
    /**
     * 检查产品型号是否存在（排除指定ID）
     * @param model 产品型号
     * @param id 排除的产品ID
     * @return 是否存在
     */
    boolean existsByModelExcludeId(@Param("model") String model, @Param("id") Long id);
    
    // 添加测试代码期望的方法
    
    /**
     * 根据查询条件查询产品（用于测试兼容）
     * @param queryDTO 查询条件
     * @return 产品列表
     */
    List<Product> selectByQuery(com.yxrobot.dto.ProductQueryDTO queryDTO);
    
    /**
     * 根据查询条件统计产品数量（用于测试兼容）
     * @param queryDTO 查询条件
     * @return 产品数量
     */
    int countByQuery(com.yxrobot.dto.ProductQueryDTO queryDTO);
    
    /**
     * 根据ID更新产品（用于测试兼容）
     * @param product 产品信息
     * @return 影响行数
     */
    int updateById(Product product);
    
    /**
     * 批量删除产品（用于测试兼容）
     * @param ids 产品ID列表
     * @return 影响行数
     */
    int batchDeleteByIds(@Param("ids") List<Long> ids);
    
    /**
     * 获取状态统计（用于测试兼容）
     * @return 状态统计Map
     */
    java.util.Map<String, Integer> getStatusStatistics();
}