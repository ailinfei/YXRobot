package com.yxrobot.handler;

import com.yxrobot.entity.InteractionType;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * 互动类型枚举类型处理器
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@MappedTypes(InteractionType.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class InteractionTypeHandler extends CodeEnumTypeHandler<InteractionType> {
    
    public InteractionTypeHandler() {
        super(InteractionType.class);
    }
}