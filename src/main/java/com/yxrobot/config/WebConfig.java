package com.yxrobot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * Web配置类
 * 配置静态资源访问和CORS
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Value("${file.upload.path:uploads}")
    private String uploadPath;
    
    /**
     * 配置静态资源处理
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置上传文件的静态资源访问
        String uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize().toString();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir + "/");
        
        // 配置前端静态资源
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
        
        // 配置Swagger UI资源（如果需要）
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/");
    }
    
    /**
     * 配置CORS跨域
     * 修复allowCredentials=true时不能使用allowedOriginPatterns("*")的问题
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(
                    "http://localhost:*", 
                    "http://127.0.0.1:*", 
                    "https://localhost:*",
                    "https://127.0.0.1:*"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}