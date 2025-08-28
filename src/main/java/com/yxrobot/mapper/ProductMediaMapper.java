package com.yxrobot.mapper;

import com.yxrobot.entity.ProductMedia;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 产品媒体数据访问接口
 * 使用MyBatis进行数据库操作
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-19
 */
@Mapper
public interface ProductMediaMapper {
    
    /**
     * 根据产品ID获取媒体列表
     * 按排序值升序排列
     * 
     * @param productId 产品ID
     * @return 媒体列表
     */
    List<ProductMedia> selectByProductId(@Param("productId") Long productId);
    
    /**
     * 根据产品ID和媒体类型获取媒体列表
     * 
     * @param productId 产品ID
     * @param mediaType 媒体类型
     * @return 媒体列表
     */
    List<ProductMedia> selectByProductIdAndType(@Param("productId") Long productId, 
                                               @Param("mediaType") String mediaType);
    
    /**
     * 根据ID查询媒体详情
     * 
     * @param id 媒体ID
     * @return 媒体详情，如果不存在返回null
     */
    ProductMedia selectById(@Param("id") Long id);
    
    /**
     * 插入新媒体记录
     * 
     * @param productMedia 媒体对象
     * @return 影响的行数
     */
    int insert(ProductMedia productMedia);
    
    /**
     * 根据ID更新媒体信息
     * 
     * @param productMedia 媒体对象
     * @return 影响的行数
     */
    int updateById(ProductMedia productMedia);
    
    /**
     * 根据ID软删除媒体
     * 将is_deleted字段设置为1
     * 
     * @param id 媒体ID
     * @return 影响的行数
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据产品ID删除所有媒体
     * 用于删除产品时清理相关媒体
     * 
     * @param productId 产品ID
     * @return 影响的行数
     */
    int deleteByProductId(@Param("productId") Long productId);
    
    /**
     * 批量删除媒体
     * 
     * @param ids 媒体ID列表
     * @return 影响的行数
     */
    int batchDeleteByIds(@Param("ids") List<Long> ids);
    
    /**
     * 统计产品的媒体数量
     * 
     * @param productId 产品ID
     * @return 媒体数量
     */
    Long countByProductId(@Param("productId") Long productId);
    
    /**
     * 统计产品指定类型的媒体数量
     * 
     * @param productId 产品ID
     * @param mediaType 媒体类型
     * @return 媒体数量
     */
    Long countByProductIdAndType(@Param("productId") Long productId, 
                                @Param("mediaType") String mediaType);
    
    /**
     * 获取产品媒体的最大排序值
     * 用于新增媒体时确定排序值
     * 
     * @param productId 产品ID
     * @param mediaType 媒体类型
     * @return 最大排序值，如果没有记录返回0
     */
    Integer getMaxSortOrder(@Param("productId") Long productId, 
                           @Param("mediaType") String mediaType);
    
    /**
     * 更新媒体排序值
     * 
     * @param id 媒体ID
     * @param sortOrder 新的排序值
     * @return 影响的行数
     */
    int updateSortOrder(@Param("id") Long id, @Param("sortOrder") Integer sortOrder);
}