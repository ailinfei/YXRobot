package com.yxrobot.handler;

import com.yxrobot.entity.PlatformLink.PlatformType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * PlatformType枚举类型处理器
 * 用于在数据库字符串值和枚举值之间进行转换
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-22
 */
public class PlatformTypeHandler extends BaseTypeHandler<PlatformType> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, PlatformType parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getCode());
    }

    @Override
    public PlatformType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String code = rs.getString(columnName);
        return code == null ? null : PlatformType.fromCode(code);
    }

    @Override
    public PlatformType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String code = rs.getString(columnIndex);
        return code == null ? null : PlatformType.fromCode(code);
    }

    @Override
    public PlatformType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String code = cs.getString(columnIndex);
        return code == null ? null : PlatformType.fromCode(code);
    }
}