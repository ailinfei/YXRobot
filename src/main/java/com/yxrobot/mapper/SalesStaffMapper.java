package com.yxrobot.mapper;

import com.yxrobot.entity.SalesStaff;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 销售人员数据访问层
 */
@Mapper
public interface SalesStaffMapper {
    
    /**
     * 根据ID查询销售人员
     * @param id 销售人员ID
     * @return 销售人员信息
     */
    SalesStaff selectById(@Param("id") Long id);
    
    /**
     * 查询所有销售人员
     * @return 销售人员列表
     */
    List<SalesStaff> selectAll();
    
    /**
     * 查询活跃销售人员
     * @return 活跃销售人员列表
     */
    List<SalesStaff> selectActive();
    
    /**
     * 插入销售人员
     * @param salesStaff 销售人员信息
     * @return 影响行数
     */
    int insert(SalesStaff salesStaff);
    
    /**
     * 更新销售人员
     * @param salesStaff 销售人员信息
     * @return 影响行数
     */
    int update(SalesStaff salesStaff);
    
    /**
     * 删除销售人员
     * @param id 销售人员ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
}