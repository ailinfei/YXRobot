package com.yxrobot.mapper;

import com.yxrobot.entity.CharityProject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 公益项目数据访问接口
 * 使用MyBatis进行数据库操作，支持项目信息的CRUD操作和复杂查询
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-18
 */
@Mapper
public interface CharityProjectMapper {
    
    /**
     * 根据查询条件获取公益项目列表
     * 支持分页、搜索、类型筛选和状态筛选
     * 
     * @param keyword 搜索关键词（项目名称、负责人、地区）
     * @param type 项目类型
     * @param status 项目状态
     * @param region 项目地区
     * @param priority 项目优先级
     * @param offset 分页偏移量
     * @param limit 每页数量
     * @return 项目列表
     */
    List<CharityProject> selectByQuery(@Param("keyword") String keyword,
                                     @Param("type") String type,
                                     @Param("status") String status,
                                     @Param("region") String region,
                                     @Param("priority") Integer priority,
                                     @Param("offset") Integer offset,
                                     @Param("limit") Integer limit);
    
    /**
     * 根据查询条件统计项目总数
     * 用于分页计算
     * 
     * @param keyword 搜索关键词
     * @param type 项目类型
     * @param status 项目状态
     * @param region 项目地区
     * @param priority 项目优先级
     * @return 项目总数
     */
    Long countByQuery(@Param("keyword") String keyword,
                     @Param("type") String type,
                     @Param("status") String status,
                     @Param("region") String region,
                     @Param("priority") Integer priority);
    
    /**
     * 根据ID查询项目详情
     * 
     * @param id 项目ID
     * @return 项目详情，如果不存在返回null
     */
    CharityProject selectById(@Param("id") Long id);
    
    /**
     * 根据机构ID查询项目列表
     * 
     * @param institutionId 机构ID
     * @return 项目列表
     */
    List<CharityProject> selectByInstitutionId(@Param("institutionId") Long institutionId);
    
    /**
     * 插入新项目
     * 
     * @param project 项目对象
     * @return 影响的行数
     */
    int insert(CharityProject project);
    
    /**
     * 根据ID更新项目信息
     * 
     * @param project 项目对象
     * @return 影响的行数
     */
    int updateById(CharityProject project);
    
    /**
     * 根据ID软删除项目
     * 将deleted字段设置为1
     * 
     * @param id 项目ID
     * @return 影响的行数
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 批量软删除项目
     * 将多个项目的deleted字段设置为1
     * 
     * @param ids 项目ID列表
     * @return 影响的行数
     */
    int batchDeleteByIds(@Param("ids") List<Long> ids);
    
    /**
     * 检查项目名称是否存在
     * 用于验证项目名称唯一性
     * 
     * @param name 项目名称
     * @return 存在返回true，不存在返回false
     */
    boolean existsByName(@Param("name") String name);
    
    /**
     * 检查项目名称是否存在（排除指定ID）
     * 用于更新时验证项目名称唯一性
     * 
     * @param name 项目名称
     * @param excludeId 排除的项目ID
     * @return 存在返回true，不存在返回false
     */
    boolean existsByNameExcludeId(@Param("name") String name, @Param("excludeId") Long excludeId);
    
    /**
     * 根据状态统计项目数量
     * 用于统计分析
     * 
     * @param status 项目状态
     * @return 项目数量
     */
    Long countByStatus(@Param("status") String status);
    
    /**
     * 根据类型统计项目数量
     * 用于统计分析
     * 
     * @param type 项目类型
     * @return 项目数量
     */
    Long countByType(@Param("type") String type);
    
    /**
     * 根据地区统计项目数量
     * 用于统计分析
     * 
     * @param region 项目地区
     * @return 项目数量
     */
    Long countByRegion(@Param("region") String region);
    
    /**
     * 获取所有项目状态统计
     * 返回各状态的项目数量
     * 
     * @return 状态统计列表
     */
    List<Map<String, Object>> getStatusStatistics();
    
    /**
     * 获取所有项目类型统计
     * 返回各类型的项目数量
     * 
     * @return 类型统计列表
     */
    List<Map<String, Object>> getTypeStatistics();
    
    /**
     * 获取地区分布统计
     * 返回各地区的项目数量
     * 
     * @return 地区分布统计列表
     */
    List<Map<String, Object>> getRegionStatistics();
    
    /**
     * 获取优先级分布统计
     * 返回各优先级的项目数量
     * 
     * @return 优先级分布统计列表
     */
    List<Map<String, Object>> getPriorityStatistics();
    
    /**
     * 获取项目进度统计
     * 返回项目进度分布情况
     * 
     * @return 进度统计列表
     */
    List<Map<String, Object>> getProgressStatistics();
    
    /**
     * 获取活跃项目列表
     * 返回状态为active的项目
     * 
     * @return 活跃项目列表
     */
    List<CharityProject> selectActiveProjects();
    
    /**
     * 获取即将结束的项目
     * 返回结束日期在指定天数内的项目
     * 
     * @param days 天数
     * @return 即将结束的项目列表
     */
    List<CharityProject> selectProjectsEndingSoon(@Param("days") Integer days);
    
    /**
     * 获取最近创建的项目
     * 按创建时间倒序排列
     * 
     * @param limit 限制返回数量
     * @return 最近创建的项目列表
     */
    List<CharityProject> selectRecentProjects(@Param("limit") Integer limit);
    
    /**
     * 更新项目状态
     * 
     * @param id 项目ID
     * @param status 新状态
     * @return 影响的行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    
    /**
     * 更新项目进度
     * 
     * @param id 项目ID
     * @param progress 新进度
     * @return 影响的行数
     */
    int updateProgress(@Param("id") Long id, @Param("progress") Integer progress);
    
    /**
     * 批量更新项目状态
     * 
     * @param ids 项目ID列表
     * @param status 新状态
     * @return 影响的行数
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") String status);
    
    /**
     * 获取项目预算执行统计
     * 返回预算与实际支出的对比统计
     * 
     * @return 预算执行统计列表
     */
    List<Map<String, Object>> getBudgetExecutionStats();
    
    /**
     * 获取项目受益人统计
     * 返回受益人数的分布情况
     * 
     * @return 受益人统计列表
     */
    List<Map<String, Object>> getBeneficiaryStatistics();
    
    /**
     * 获取项目时长统计
     * 计算各项目的执行时长
     * 
     * @return 项目时长统计列表
     */
    List<Map<String, Object>> getProjectDurationStats();
}