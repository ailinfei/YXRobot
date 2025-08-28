package com.yxrobot.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步配置类
 * 配置异步任务执行器和定时任务
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-22
 */
@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(AsyncConfig.class);
    
    /**
     * 链接验证异步任务执行器
     * 
     * @return 任务执行器
     */
    @Bean("linkValidationExecutor")
    public Executor linkValidationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // 核心线程数
        executor.setCorePoolSize(5);
        
        // 最大线程数
        executor.setMaxPoolSize(20);
        
        // 队列容量
        executor.setQueueCapacity(100);
        
        // 线程名前缀
        executor.setThreadNamePrefix("LinkValidation-");
        
        // 线程空闲时间（秒）
        executor.setKeepAliveSeconds(60);
        
        // 拒绝策略：调用者运行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        
        // 等待任务完成后关闭
        executor.setWaitForTasksToCompleteOnShutdown(true);
        
        // 等待时间
        executor.setAwaitTerminationSeconds(30);
        
        // 初始化
        executor.initialize();
        
        logger.info("链接验证异步任务执行器初始化完成 - 核心线程: {}, 最大线程: {}, 队列容量: {}", 
                   executor.getCorePoolSize(), executor.getMaxPoolSize(), executor.getQueueCapacity());
        
        return executor;
    }
    
    /**
     * 点击统计异步任务执行器
     * 
     * @return 任务执行器
     */
    @Bean("clickStatsExecutor")
    public Executor clickStatsExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // 核心线程数
        executor.setCorePoolSize(3);
        
        // 最大线程数
        executor.setMaxPoolSize(10);
        
        // 队列容量
        executor.setQueueCapacity(200);
        
        // 线程名前缀
        executor.setThreadNamePrefix("ClickStats-");
        
        // 线程空闲时间（秒）
        executor.setKeepAliveSeconds(60);
        
        // 拒绝策略：丢弃最旧的任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
        
        // 等待任务完成后关闭
        executor.setWaitForTasksToCompleteOnShutdown(true);
        
        // 等待时间
        executor.setAwaitTerminationSeconds(30);
        
        // 初始化
        executor.initialize();
        
        logger.info("点击统计异步任务执行器初始化完成 - 核心线程: {}, 最大线程: {}, 队列容量: {}", 
                   executor.getCorePoolSize(), executor.getMaxPoolSize(), executor.getQueueCapacity());
        
        return executor;
    }
    
    /**
     * 通用异步任务执行器
     * 
     * @return 任务执行器
     */
    @Bean("taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // 核心线程数
        executor.setCorePoolSize(2);
        
        // 最大线程数
        executor.setMaxPoolSize(8);
        
        // 队列容量
        executor.setQueueCapacity(50);
        
        // 线程名前缀
        executor.setThreadNamePrefix("AsyncTask-");
        
        // 线程空闲时间（秒）
        executor.setKeepAliveSeconds(60);
        
        // 拒绝策略：调用者运行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        
        // 等待任务完成后关闭
        executor.setWaitForTasksToCompleteOnShutdown(true);
        
        // 等待时间
        executor.setAwaitTerminationSeconds(30);
        
        // 初始化
        executor.initialize();
        
        logger.info("通用异步任务执行器初始化完成 - 核心线程: {}, 最大线程: {}, 队列容量: {}", 
                   executor.getCorePoolSize(), executor.getMaxPoolSize(), executor.getQueueCapacity());
        
        return executor;
    }
}