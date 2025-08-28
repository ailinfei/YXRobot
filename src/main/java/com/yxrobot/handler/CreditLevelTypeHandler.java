package com.yxrobot.handler;

import com.yxrobot.entity.CreditLevel;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 信用等级枚举类型处理器
 * 用于MyBatis中枚举类型与数据库字符串类型的转换
 * 
 * 字段映射规范：
 * - 数据库存储：大写字母（如：A, B, C, D）
 * - Java枚举：大写命名（如：A, B, C, D）
 */
public class CreditLevelTypeHandler extends BaseTypeHandler<CreditLevel> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, CreditLevel parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getValue());
    }

    @Override
    public CreditLevel getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : CreditLevel.fromValue(value);
    }

    @Override
    public CreditLevel getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : CreditLevel.fromValue(value);
    }

    @Override
    public CreditLevel getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : CreditLevel.fromValue(value);
    }
}