package com.yxrobot.mapper;

import com.yxrobot.entity.RegionConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 区域配置数据访问层接口
 * 负责区域配置相关的数据库操作
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-22
 */
@Mapper
public interface RegionConfigMapper {
    
    /**
     * 根据ID查询区域配置
     * @param id 配置ID
     * @return 区域配置实体
     */
    RegionConfig selectById(@Param("id") Long id);
    
    /**
     * 查询所有激活的区域配置
     * @return 区域配置列表
     */
    List<RegionConfig> selectAllActive();
    
    /**
     * 根据地区查询配置
     * @param region 地区名称
     * @return 区域配置列表
     */
    List<RegionConfig> selectByRegion(@Param("region") String region);
    
    /**
     * 根据国家查询配置
     * @param country 国家名称
     * @return 区域配置列表
     */
    List<RegionConfig> selectByCountry(@Param("country") String country);
    
    /**
     * 根据语言代码查询配置
     * @param languageCode 语言代码
     * @return 区域配置列表
     */
    List<RegionConfig> selectByLanguageCode(@Param("languageCode") String languageCode);
    
    /**
     * 查询所有不重复的地区
     * @return 地区列表
     */
    List<String> selectDistinctRegions();
    
    /**
     * 查询所有不重复的国家
     * @return 国家列表
     */
    List<String> selectDistinctCountries();
    
    /**
     * 查询所有不重复的语言
     * @return 语言列表
     */
    List<java.util.Map<String, String>> selectDistinctLanguages();
    
    /**
     * 插入区域配置
     * @param config 区域配置实体
     * @return 影响行数
     */
    int insert(RegionConfig config);
    
    /**
     * 更新区域配置
     * @param config 区域配置实体
     * @return 影响行数
     */
    int update(RegionConfig config);
    
    /**
     * 删除区域配置
     * @param id 配置ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 更新激活状态
     * @param id 配置ID
     * @param isActive 是否激活
     * @return 影响行数
     */
    int updateActiveStatus(@Param("id") Long id, @Param("isActive") Boolean isActive);
    
    /**
     * 检查区域和语言组合是否已存在
     * @param region 地区
     * @param languageCode 语言代码
     * @param excludeId 排除的ID（用于更新时检查）
     * @return 存在的记录数
     */
    int checkExists(@Param("region") String region, 
                   @Param("languageCode") String languageCode,
                   @Param("excludeId") Long excludeId);
    
    /**
     * 批量插入区域配置
     * @param configs 区域配置列表
     * @return 影响行数
     */
    int batchInsert(@Param("configs") List<RegionConfig> configs);
    
    /**
     * 根据地区和语言验证配置是否有效
     * @param region 地区
     * @param languageCode 语言代码
     * @return 是否有效
     */
    boolean validateRegionLanguage(@Param("region") String region, @Param("languageCode") String languageCode);
}