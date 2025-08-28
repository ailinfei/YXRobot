package com.yxrobot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * YXRobot 应用主启动类
 * 
 * 支持两种启动方式：
 * 1. 开发模式：直接运行main方法，内嵌Tomcat启动
 * 2. 生产模式：打包为WAR文件，部署到外部Tomcat
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-08-16
 */
@SpringBootApplication
@EnableTransactionManagement
public class YXRobotApplication extends SpringBootServletInitializer {

    /**
     * 开发模式启动入口
     * 使用内嵌Tomcat启动应用
     */
    public static void main(String[] args) {
        SpringApplication.run(YXRobotApplication.class, args);
    }

    /**
     * 生产模式启动配置
     * 用于WAR包部署到外部Tomcat
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(YXRobotApplication.class);
    }
}