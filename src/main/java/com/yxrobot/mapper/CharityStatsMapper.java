package com.yxrobot.mapper;

import com.yxrobot.entity.CharityStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * 公益统计数据访问接口
 * 使用MyBatis进行数据库操作，支持统计数据的CRUD操作和复杂查询
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-18
 */
@Mapper
public interface CharityStatsMapper {
    
    /**
     * 获取最新的公益统计数据
     * 返回最新版本的统计数据记录
     * 
     * @return 最新的统计数据，如果不存在返回null
     */
    CharityStats selectLatest();
    
    /**
     * 根据ID查询统计数据
     * 
     * @param id 统计数据ID
     * @return 统计数据详情，如果不存在返回null
     */
    CharityStats selectById(@Param("id") Long id);
    
    /**
     * 插入新的统计数据记录
     * 
     * @param charityStats 统计数据对象
     * @return 影响的行数
     */
    int insert(CharityStats charityStats);
    
    /**
     * 根据ID更新统计数据
     * 
     * @param charityStats 统计数据对象
     * @return 影响的行数
     */
    int updateById(CharityStats charityStats);
    
    /**
     * 根据版本号更新统计数据（乐观锁）
     * 用于防止并发更新冲突
     * 
     * @param charityStats 统计数据对象
     * @return 影响的行数，0表示版本冲突
     */
    int updateByVersion(CharityStats charityStats);
    
    /**
     * 获取统计数据历史记录
     * 按创建时间倒序排列
     * 
     * @param limit 限制返回记录数
     * @return 历史统计数据列表
     */
    List<CharityStats> selectHistory(@Param("limit") Integer limit);
    
    /**
     * 获取增强统计数据
     * 包含趋势分析数据，如环比增长率等
     * 
     * @return 增强统计数据Map
     */
    Map<String, Object> selectEnhancedStats();
    
    /**
     * 获取统计数据趋势
     * 返回指定时间范围内的统计数据变化趋势
     * 
     * @param days 天数范围
     * @return 趋势数据列表
     */
    List<Map<String, Object>> selectStatsTrend(@Param("days") Integer days);
    
    /**
     * 验证统计数据逻辑一致性
     * 检查各项统计数据之间的逻辑关系是否正确
     * 
     * @param charityStats 待验证的统计数据
     * @return 验证结果Map，包含是否通过验证和错误信息
     */
    Map<String, Object> validateStatsConsistency(CharityStats charityStats);
    
    /**
     * 获取项目状态分布数据
     * 用于生成项目状态分布饼图
     * 
     * @return 项目状态分布数据列表
     */
    List<Map<String, Object>> selectProjectStatusDistribution();
    
    /**
     * 获取资金筹集趋势数据
     * 用于生成资金筹集趋势折线图
     * 
     * @param months 月份数量
     * @return 资金趋势数据列表
     */
    List<Map<String, Object>> selectFundingTrend(@Param("months") Integer months);
    
    /**
     * 获取地区分布数据
     * 用于生成项目地区分布饼图
     * 
     * @return 地区分布数据列表
     */
    List<Map<String, Object>> selectRegionDistribution();
    
    /**
     * 获取志愿者活动统计数据
     * 用于生成志愿者活动统计柱状图
     * 
     * @param months 月份数量
     * @return 志愿者活动统计数据列表
     */
    List<Map<String, Object>> selectVolunteerActivityStats(@Param("months") Integer months);
    
    /**
     * 计算统计数据汇总
     * 从基础数据表中重新计算统计数据
     * 
     * @return 计算得出的统计数据
     */
    CharityStats calculateStatsFromSource();
    
    /**
     * 检查统计数据是否存在
     * 
     * @return 存在返回true，不存在返回false
     */
    boolean existsStats();
    
    /**
     * 软删除统计数据
     * 将deleted字段设置为1
     * 
     * @param id 统计数据ID
     * @return 影响的行数
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 获取未删除的最新统计数据
     * 
     * @return 最新的未删除统计数据
     */
    CharityStats selectLatestNotDeleted();
    
    /**
     * 获取所有未删除的统计数据
     * 
     * @return 未删除的统计数据列表
     */
    List<CharityStats> selectAllNotDeleted();
    
    /**
     * 软删除统计数据
     * 
     * @param id 统计数据ID
     * @return 影响的行数
     */
    int softDeleteById(@Param("id") Long id);
    
    /**
     * 恢复软删除的统计数据
     * 
     * @param id 统计数据ID
     * @return 影响的行数
     */
    int restoreById(@Param("id") Long id);
    
    /**
     * 根据ID查询未删除的统计数据
     * 
     * @param id 统计数据ID
     * @return 未删除的统计数据
     */
    CharityStats selectByIdNotDeleted(@Param("id") Long id);
    
    /**
     * 统计未删除记录数量
     * 
     * @return 未删除记录数量
     */
    Integer countNotDeleted();
}