package com.yxrobot.handler;

import com.yxrobot.entity.CustomerType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 客户类型枚举类型处理器
 * 用于MyBatis中枚举类型与数据库字符串类型的转换
 * 
 * 字段映射规范：
 * - 数据库存储：小写英文字符串（如：individual, enterprise）
 * - Java枚举：大写命名（如：INDIVIDUAL, ENTERPRISE）
 */
public class CustomerTypeTypeHandler extends BaseTypeHandler<CustomerType> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, CustomerType parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getValue());
    }

    @Override
    public CustomerType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : CustomerType.fromValue(value);
    }

    @Override
    public CustomerType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : CustomerType.fromValue(value);
    }

    @Override
    public CustomerType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : CustomerType.fromValue(value);
    }
}