package com.yxrobot.mapper;

import com.yxrobot.entity.CharityActivity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 公益活动数据访问接口
 * 使用MyBatis进行数据库操作，支持活动信息的CRUD操作和复杂查询
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-18
 */
@Mapper
public interface CharityActivityMapper {
    
    /**
     * 根据查询条件获取公益活动列表
     * 支持分页、搜索、类型筛选和状态筛选
     * 
     * @param keyword 搜索关键词（活动标题、组织方、地点）
     * @param type 活动类型
     * @param status 活动状态
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param offset 分页偏移量
     * @param limit 每页数量
     * @return 活动列表
     */
    List<CharityActivity> selectByQuery(@Param("keyword") String keyword,
                                      @Param("type") String type,
                                      @Param("status") String status,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate,
                                      @Param("offset") Integer offset,
                                      @Param("limit") Integer limit);
    
    /**
     * 根据查询条件统计活动总数
     * 用于分页计算
     * 
     * @param keyword 搜索关键词
     * @param type 活动类型
     * @param status 活动状态
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 活动总数
     */
    Long countByQuery(@Param("keyword") String keyword,
                     @Param("type") String type,
                     @Param("status") String status,
                     @Param("startDate") LocalDate startDate,
                     @Param("endDate") LocalDate endDate);
    
    /**
     * 根据ID查询活动详情
     * 
     * @param id 活动ID
     * @return 活动详情，如果不存在返回null
     */
    CharityActivity selectById(@Param("id") Long id);
    
    /**
     * 根据项目ID查询活动列表
     * 
     * @param projectId 项目ID
     * @return 活动列表
     */
    List<CharityActivity> selectByProjectId(@Param("projectId") Long projectId);
    
    /**
     * 插入新活动
     * 
     * @param activity 活动对象
     * @return 影响的行数
     */
    int insert(CharityActivity activity);
    
    /**
     * 根据ID更新活动信息
     * 
     * @param activity 活动对象
     * @return 影响的行数
     */
    int updateById(CharityActivity activity);
    
    /**
     * 根据ID软删除活动
     * 将deleted字段设置为1
     * 
     * @param id 活动ID
     * @return 影响的行数
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 批量软删除活动
     * 将多个活动的deleted字段设置为1
     * 
     * @param ids 活动ID列表
     * @return 影响的行数
     */
    int batchDeleteByIds(@Param("ids") List<Long> ids);
    
    /**
     * 根据状态统计活动数量
     * 用于统计分析
     * 
     * @param status 活动状态
     * @return 活动数量
     */
    Long countByStatus(@Param("status") String status);
    
    /**
     * 根据类型统计活动数量
     * 用于统计分析
     * 
     * @param type 活动类型
     * @return 活动数量
     */
    Long countByType(@Param("type") String type);
    
    /**
     * 统计指定月份的活动数量
     * 
     * @param year 年份
     * @param month 月份
     * @return 活动数量
     */
    Long countByMonth(@Param("year") Integer year, @Param("month") Integer month);
    
    /**
     * 获取所有活动状态统计
     * 返回各状态的活动数量
     * 
     * @return 状态统计列表
     */
    List<Map<String, Object>> getStatusStatistics();
    
    /**
     * 获取所有活动类型统计
     * 返回各类型的活动数量
     * 
     * @return 类型统计列表
     */
    List<Map<String, Object>> getTypeStatistics();
    
    /**
     * 获取月度活动统计
     * 返回指定月份数量的活动统计数据
     * 
     * @param months 月份数量
     * @return 月度活动统计列表
     */
    List<Map<String, Object>> getMonthlyStatistics(@Param("months") Integer months);
    
    /**
     * 获取志愿者参与统计
     * 返回志愿者活动参与情况统计
     * 
     * @param months 月份数量
     * @return 志愿者参与统计列表
     */
    List<Map<String, Object>> getVolunteerParticipationStats(@Param("months") Integer months);
    
    /**
     * 获取活动预算执行统计
     * 返回预算与实际费用的对比统计
     * 
     * @return 预算执行统计列表
     */
    List<Map<String, Object>> getBudgetExecutionStats();
    
    /**
     * 获取即将开始的活动
     * 返回未来指定天数内的活动
     * 
     * @param days 天数
     * @return 即将开始的活动列表
     */
    List<CharityActivity> selectUpcomingActivities(@Param("days") Integer days);
    
    /**
     * 获取正在进行的活动
     * 返回状态为ongoing的活动
     * 
     * @return 正在进行的活动列表
     */
    List<CharityActivity> selectOngoingActivities();
    
    /**
     * 获取最近完成的活动
     * 按完成时间倒序排列
     * 
     * @param limit 限制返回数量
     * @return 最近完成的活动列表
     */
    List<CharityActivity> selectRecentCompletedActivities(@Param("limit") Integer limit);
    
    /**
     * 更新活动状态
     * 
     * @param id 活动ID
     * @param status 新状态
     * @return 影响的行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    
    /**
     * 批量更新活动状态
     * 
     * @param ids 活动ID列表
     * @param status 新状态
     * @return 影响的行数
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") String status);
    
    /**
     * 获取活动地区分布统计
     * 返回各地区的活动数量
     * 
     * @return 地区分布统计列表
     */
    List<Map<String, Object>> getLocationStatistics();
    
    /**
     * 获取活动参与人数统计
     * 返回参与人数的分布情况
     * 
     * @return 参与人数统计列表
     */
    List<Map<String, Object>> getParticipantStatistics();
}