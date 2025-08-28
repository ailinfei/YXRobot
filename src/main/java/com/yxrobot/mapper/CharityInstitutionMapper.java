package com.yxrobot.mapper;

import com.yxrobot.entity.CharityInstitution;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * 合作机构数据访问接口
 * 使用MyBatis进行数据库操作，支持机构信息的CRUD操作和复杂查询
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-18
 */
@Mapper
public interface CharityInstitutionMapper {
    
    /**
     * 根据查询条件获取合作机构列表
     * 支持分页、搜索、类型筛选和状态筛选
     * 
     * @param keyword 搜索关键词（机构名称、联系人、地区）
     * @param type 机构类型
     * @param status 机构状态
     * @param offset 分页偏移量
     * @param limit 每页数量
     * @return 机构列表
     */
    List<CharityInstitution> selectByQuery(@Param("keyword") String keyword,
                                         @Param("type") String type,
                                         @Param("status") String status,
                                         @Param("offset") Integer offset,
                                         @Param("limit") Integer limit);
    
    /**
     * 根据查询条件统计机构总数
     * 用于分页计算
     * 
     * @param keyword 搜索关键词
     * @param type 机构类型
     * @param status 机构状态
     * @return 机构总数
     */
    Long countByQuery(@Param("keyword") String keyword,
                     @Param("type") String type,
                     @Param("status") String status);
    
    /**
     * 根据ID查询机构详情
     * 
     * @param id 机构ID
     * @return 机构详情，如果不存在返回null
     */
    CharityInstitution selectById(@Param("id") Long id);
    
    /**
     * 插入新机构
     * 
     * @param institution 机构对象
     * @return 影响的行数
     */
    int insert(CharityInstitution institution);
    
    /**
     * 根据ID更新机构信息
     * 
     * @param institution 机构对象
     * @return 影响的行数
     */
    int updateById(CharityInstitution institution);
    
    /**
     * 根据ID软删除机构
     * 将deleted字段设置为1
     * 
     * @param id 机构ID
     * @return 影响的行数
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 批量软删除机构
     * 将多个机构的deleted字段设置为1
     * 
     * @param ids 机构ID列表
     * @return 影响的行数
     */
    int batchDeleteByIds(@Param("ids") List<Long> ids);
    
    /**
     * 检查机构名称是否存在
     * 用于验证机构名称唯一性
     * 
     * @param name 机构名称
     * @return 存在返回true，不存在返回false
     */
    boolean existsByName(@Param("name") String name);
    
    /**
     * 检查机构名称是否存在（排除指定ID）
     * 用于更新时验证机构名称唯一性
     * 
     * @param name 机构名称
     * @param excludeId 排除的机构ID
     * @return 存在返回true，不存在返回false
     */
    boolean existsByNameExcludeId(@Param("name") String name, @Param("excludeId") Long excludeId);
    
    /**
     * 根据状态统计机构数量
     * 用于统计分析
     * 
     * @param status 机构状态
     * @return 机构数量
     */
    Long countByStatus(@Param("status") String status);
    
    /**
     * 根据类型统计机构数量
     * 用于统计分析
     * 
     * @param type 机构类型
     * @return 机构数量
     */
    Long countByType(@Param("type") String type);
    
    /**
     * 获取所有机构状态统计
     * 返回各状态的机构数量
     * 
     * @return 状态统计列表
     */
    List<Map<String, Object>> getStatusStatistics();
    
    /**
     * 获取所有机构类型统计
     * 返回各类型的机构数量
     * 
     * @return 类型统计列表
     */
    List<Map<String, Object>> getTypeStatistics();
    
    /**
     * 获取地区分布统计
     * 返回各地区的机构数量
     * 
     * @return 地区分布统计列表
     */
    List<Map<String, Object>> getLocationStatistics();
    
    /**
     * 获取活跃机构列表
     * 返回状态为active的机构
     * 
     * @return 活跃机构列表
     */
    List<CharityInstitution> selectActiveInstitutions();
    
    /**
     * 获取最近合作的机构
     * 按合作日期倒序排列
     * 
     * @param limit 限制返回数量
     * @return 最近合作机构列表
     */
    List<CharityInstitution> selectRecentCooperations(@Param("limit") Integer limit);
    
    /**
     * 更新机构最后访问日期
     * 
     * @param id 机构ID
     * @param lastVisitDate 最后访问日期
     * @return 影响的行数
     */
    int updateLastVisitDate(@Param("id") Long id, @Param("lastVisitDate") java.time.LocalDate lastVisitDate);
    
    /**
     * 批量更新机构状态
     * 
     * @param ids 机构ID列表
     * @param status 新状态
     * @return 影响的行数
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") String status);
    
    /**
     * 获取机构合作时长统计
     * 计算各机构的合作天数
     * 
     * @return 合作时长统计列表
     */
    List<Map<String, Object>> getCooperationDurationStats();
}