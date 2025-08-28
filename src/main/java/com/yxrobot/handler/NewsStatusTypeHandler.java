package com.yxrobot.handler;

import com.yxrobot.entity.NewsStatus;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * 新闻状态枚举类型处理器
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@MappedTypes(NewsStatus.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class NewsStatusTypeHandler extends CodeEnumTypeHandler<NewsStatus> {
    
    public NewsStatusTypeHandler() {
        super(NewsStatus.class);
    }
}