package com.yxrobot.handler;

import com.yxrobot.entity.SalesStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 销售状态枚举类型处理器
 * 用于MyBatis中枚举类型与数据库字符串类型的转换
 * 
 * 字段映射规范：
 * - 数据库存储：小写英文字符串（如：pending, confirmed）
 * - Java枚举：大写命名（如：PENDING, CONFIRMED）
 */
public class SalesStatusTypeHandler extends BaseTypeHandler<SalesStatus> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, SalesStatus parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getValue());
    }

    @Override
    public SalesStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : SalesStatus.fromValue(value);
    }

    @Override
    public SalesStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : SalesStatus.fromValue(value);
    }

    @Override
    public SalesStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : SalesStatus.fromValue(value);
    }
}