package com.yxrobot.handler;

import com.yxrobot.entity.NewsStatus;
import com.yxrobot.entity.InteractionType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 基于代码的枚举类型处理器
 * 用于处理NewsStatus和InteractionType等枚举类型
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
public class CodeEnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {
    
    private Class<E> enumType;
    
    /**
     * 无参构造函数，MyBatis 需要
     */
    public CodeEnumTypeHandler() {
        // MyBatis 会通过反射设置类型
    }
    
    public CodeEnumTypeHandler(Class<E> enumType) {
        if (enumType == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.enumType = enumType;
    }
    
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        String code = getEnumCode(parameter);
        ps.setString(i, code);
    }
    
    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String code = rs.getString(columnName);
        return code == null ? null : getEnumByCode(code);
    }
    
    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String code = rs.getString(columnIndex);
        return code == null ? null : getEnumByCode(code);
    }
    
    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String code = cs.getString(columnIndex);
        return code == null ? null : getEnumByCode(code);
    }
    
    /**
     * 获取枚举的代码值
     */
    private String getEnumCode(E enumValue) {
        if (enumValue instanceof NewsStatus) {
            return ((NewsStatus) enumValue).getCode();
        } else if (enumValue instanceof InteractionType) {
            return ((InteractionType) enumValue).getCode();
        }
        // 默认使用枚举名称
        return enumValue.name();
    }
    
    /**
     * 根据代码获取枚举值
     */
    private E getEnumByCode(String code) {
        if (enumType == NewsStatus.class) {
            return (E) NewsStatus.fromCode(code);
        } else if (enumType == InteractionType.class) {
            return (E) InteractionType.fromCode(code);
        }
        
        // 默认使用枚举名称查找
        for (E enumConstant : enumType.getEnumConstants()) {
            if (enumConstant.name().equals(code)) {
                return enumConstant;
            }
        }
        
        throw new IllegalArgumentException("未知的枚举代码: " + code + " for type: " + enumType.getSimpleName());
    }
}