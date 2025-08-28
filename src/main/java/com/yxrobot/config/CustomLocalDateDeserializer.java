package com.yxrobot.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 自定义LocalDate反序列化器
 * 支持多种日期格式的解析
 * 
 * @author YXRobot
 * @version 1.0
 * @since 2024-12-18
 */
@Component
public class CustomLocalDateDeserializer extends JsonDeserializer<LocalDate> {
    
    private static final DateTimeFormatter[] FORMATTERS = {
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy/MM/dd"),
        DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
    };
    
    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String dateString = p.getValueAsString();
        
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        
        // 如果包含时间部分，只取日期部分
        if (dateString.contains(" ")) {
            dateString = dateString.split(" ")[0];
        }
        
        // 尝试不同的日期格式
        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                return LocalDate.parse(dateString, formatter);
            } catch (DateTimeParseException e) {
                // 继续尝试下一个格式
            }
        }
        
        // 如果所有格式都失败，尝试默认解析
        try {
            return LocalDate.parse(dateString);
        } catch (DateTimeParseException e) {
            throw new IOException("无法解析日期格式: " + dateString, e);
        }
    }
}