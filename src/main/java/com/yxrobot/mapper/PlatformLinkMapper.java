package com.yxrobot.mapper;

import com.yxrobot.entity.PlatformLink;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 平台链接数据访问层接口
 * 负责平台链接相关的数据库操作
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-22
 */
@Mapper
public interface PlatformLinkMapper {
    
    /**
     * 根据ID查询平台链接
     * @param id 链接ID
     * @return 平台链接实体
     */
    PlatformLink selectById(@Param("id") Long id);
    
    /**
     * 分页查询平台链接列表
     * @param params 查询参数
     * @return 平台链接列表
     */
    List<PlatformLink> selectList(@Param("params") Map<String, Object> params);
    
    /**
     * 查询平台链接总数
     * @param params 查询参数
     * @return 总数
     */
    int selectCount(@Param("params") Map<String, Object> params);
    
    /**
     * 插入平台链接
     * @param platformLink 平台链接实体
     * @return 影响行数
     */
    int insert(PlatformLink platformLink);
    
    /**
     * 更新平台链接
     * @param platformLink 平台链接实体
     * @return 影响行数
     */
    int update(PlatformLink platformLink);
    
    /**
     * 软删除平台链接
     * @param id 链接ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 更新链接启用状态
     * @param id 链接ID
     * @param isEnabled 是否启用
     * @return 影响行数
     */
    int updateEnabledStatus(@Param("id") Long id, @Param("isEnabled") Boolean isEnabled);
    
    /**
     * 更新链接状态和最后检查时间
     * @param id 链接ID
     * @param linkStatus 链接状态
     * @return 影响行数
     */
    int updateLinkStatus(@Param("id") Long id, @Param("linkStatus") String linkStatus);
    
    /**
     * 更新点击量
     * @param id 链接ID
     * @param clickCount 点击量
     * @return 影响行数
     */
    int updateClickCount(@Param("id") Long id, @Param("clickCount") Integer clickCount);
    
    /**
     * 更新转化量
     * @param id 链接ID
     * @param conversionCount 转化量
     * @return 影响行数
     */
    int updateConversionCount(@Param("id") Long id, @Param("conversionCount") Integer conversionCount);
    
    /**
     * 获取统计数据
     * @return 统计数据Map
     */
    Map<String, Object> selectStats();
    
    /**
     * 获取地区统计数据
     * @return 地区统计数据列表
     */
    List<Map<String, Object>> selectRegionStats();
    
    /**
     * 获取语言统计数据
     * @return 语言统计数据列表
     */
    List<Map<String, Object>> selectLanguageStats();
    
    /**
     * 获取表现最佳的链接
     * @param limit 限制数量
     * @return 表现最佳的链接列表
     */
    List<Map<String, Object>> selectTopPerformingLinks(@Param("limit") Integer limit);
    
    /**
     * 检查平台链接是否已存在
     * @param platformName 平台名称
     * @param region 地区
     * @param languageCode 语言代码
     * @param excludeId 排除的ID（用于更新时检查）
     * @return 存在的记录数
     */
    int checkExists(@Param("platformName") String platformName, 
                   @Param("region") String region, 
                   @Param("languageCode") String languageCode,
                   @Param("excludeId") Long excludeId);
    
    /**
     * 根据地区和语言查询链接
     * @param region 地区
     * @param languageCode 语言代码
     * @return 平台链接列表
     */
    List<PlatformLink> selectByRegionAndLanguage(@Param("region") String region, 
                                               @Param("languageCode") String languageCode);
    
    /**
     * 根据平台类型查询链接
     * @param platformType 平台类型
     * @return 平台链接列表
     */
    List<PlatformLink> selectByPlatformType(@Param("platformType") String platformType);
    
    /**
     * 查询需要检查的链接（用于定时任务）
     * @param limit 限制数量
     * @return 需要检查的链接列表
     */
    List<PlatformLink> selectLinksForCheck(@Param("limit") Integer limit);
}