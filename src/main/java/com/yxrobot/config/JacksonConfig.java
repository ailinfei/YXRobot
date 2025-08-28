package com.yxrobot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

/**
 * Jackson配置类
 * 配置JSON序列化和反序列化规则
 * 
 * @author YXRobot
 * @version 1.0
 * @since 2024-12-18
 */
@Configuration
public class JacksonConfig implements WebMvcConfigurer {
    
    @Bean
    public SimpleModule customDateModule() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(LocalDate.class, new CustomLocalDateDeserializer());
        return module;
    }
    
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setDefaultCharset(StandardCharsets.UTF_8);
        
        ObjectMapper objectMapper = converter.getObjectMapper();
        objectMapper.registerModule(customDateModule());
        
        converters.add(0, converter);
    }
}