package com.yxrobot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Web页面控制器
 * 处理前端路由，支持Vue Router的History模式
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-19
 */
@Controller
public class WebController {

    /**
     * 处理所有前端路由请求
     * 将所有非API请求转发到index.html，让Vue Router处理路由
     * 
     * @return 转发到index.html
     */
    @RequestMapping(value = {
        "/",
        "/admin/**",
        "/website/**",
        "/test/**"
    })
    public String forward() {
        return "forward:/index.html";
    }
}